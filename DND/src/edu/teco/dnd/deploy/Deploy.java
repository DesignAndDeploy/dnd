package edu.teco.dnd.deploy;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.teco.dnd.blocks.FunctionBlock;
import edu.teco.dnd.deploy.Distribution.BlockTarget;
import edu.teco.dnd.module.Module;
import edu.teco.dnd.module.messages.joinStartApp.JoinApplicationMessage;
import edu.teco.dnd.module.messages.joinStartApp.StartApplicationAck;
import edu.teco.dnd.module.messages.joinStartApp.StartApplicationMessage;
import edu.teco.dnd.module.messages.loadStartBlock.BlockAck;
import edu.teco.dnd.module.messages.loadStartBlock.BlockMessage;
import edu.teco.dnd.module.messages.loadStartBlock.LoadClassMessage;
import edu.teco.dnd.network.ConnectionManager;
import edu.teco.dnd.network.messages.DefaultResponse;
import edu.teco.dnd.network.messages.Response;
import edu.teco.dnd.util.ClassFile;
import edu.teco.dnd.util.DefaultFutureNotifier;
import edu.teco.dnd.util.Dependencies;
import edu.teco.dnd.util.FinishedFutureNotifier;
import edu.teco.dnd.util.FutureListener;
import edu.teco.dnd.util.FutureNotifier;
import edu.teco.dnd.util.JoinedFutureNotifier;

public class Deploy {
	/**
	 * The logger for this class.
	 */
	private static final Logger LOGGER = LogManager.getLogger(Deploy.class);
	
	private final ConnectionManager connectionManager;
	
	private final Dependencies dependencies;
	
	private final ConcurrentMap<File, SoftReference<byte[]>> classFiles = new ConcurrentHashMap<File, SoftReference<byte[]>>();
	
	public Deploy(final ConnectionManager connectionManager, final Dependencies dependencies) {
		this.connectionManager = connectionManager;
		this.dependencies = dependencies;
	}
	
	public FutureNotifier<Void> deploy(final String name, final UUID appId, final Map<FunctionBlock, BlockTarget> distribution) {
		LOGGER.entry(name, appId, distribution);
		final Map<FunctionBlock, Module> moduleMapping = getModuleMapping(distribution);
		
		final Map<Module, Set<FunctionBlock>> reverseModuleMapping = new HashMap<Module, Set<FunctionBlock>>();
		for (final Entry<FunctionBlock, Module> entry : moduleMapping.entrySet()) {
			final Module module = entry.getValue();
			Set<FunctionBlock> blocks = reverseModuleMapping.get(module);
			if (blocks == null) {
				blocks = new HashSet<FunctionBlock>();
				reverseModuleMapping.put(module, blocks);
			}
			blocks.add(entry.getKey());
		}
		
		final Map<FunctionBlock, Set<ClassFile>> neededFiles = getNeededFiles(distribution.keySet());
		
		final Map<Module, Set<ClassFile>> filesPerModule = new HashMap<Module, Set<ClassFile>>();
		for (final Entry<FunctionBlock, Set<ClassFile>> entry : neededFiles.entrySet()) {
			final Module module = moduleMapping.get(entry.getKey());
			Set<ClassFile> files = filesPerModule.get(module);
			if (files == null) {
				files = new HashSet<ClassFile>();
				filesPerModule.put(module, files);
			}
			files.addAll(entry.getValue());
		}
		
		final MultipleFutureNotifier sendBlockFutureNotifier = new MultipleFutureNotifier(filesPerModule.size());
		final MultipleFutureNotifier classDeliveryFutureNotifer = new MultipleFutureNotifier(filesPerModule.size());
		
		final FutureListener<FutureNotifier<Response>> startAppResponseListener = new FutureListener<FutureNotifier<Response>>() {
			@Override
			public void operationComplete(final FutureNotifier<Response> future) {
				if (future.isSuccess()) {
					if (LOGGER.isDebugEnabled()) {
						final Response response = future.getNow();
						LOGGER.debug("module {} started the application successfully", response == null ? "unknown UUID" : response.getSourceUUID());
					}
					sendBlockFutureNotifier.finishedSuccessfully();
				} else {
					LOGGER.warn("one module failed to start the application");
					sendBlockFutureNotifier.failed(future.cause());
				}
			}
		};
		
		classDeliveryFutureNotifer.addListener(new FutureListener<FutureNotifier<Void>>() {
			@Override
			public void operationComplete(final FutureNotifier<Void> future) {
				if (future.isSuccess()) {
					LOGGER.debug("sending StartApplicationMessages app {}", appId);
					final StartApplicationMessage startAppMessage = new StartApplicationMessage(appId);
					for (final Module module : filesPerModule.keySet()) {
						connectionManager.sendMessage(module.getUUID(), startAppMessage).addListener(startAppResponseListener);
					}
				}
			}
		});
		
		final JoinApplicationMessage joinAppMsg = new JoinApplicationMessage(name, appId);
		for (final Module module : new HashSet<Module>(moduleMapping.values())) {
			LOGGER.trace("sending JoinApplicationMessage for {} to {}", appId, module);
			final FutureNotifier<Response> futureNotifier = connectionManager.sendMessage(module.getUUID(), joinAppMsg);
			futureNotifier.addListener(new FutureListener<FutureNotifier<Response>>() {
				@Override
				public void operationComplete(final FutureNotifier<Response> future) {
					if (future.isSuccess()) {
						LOGGER.debug("{} successfully joined the application {}", module, appId);
						sendClasses(module.getUUID(), filesPerModule.get(module), appId).addListener(new FutureListener<FutureNotifier<? super Collection<Response>>>() {
							@Override
							public void operationComplete(final FutureNotifier<? super Collection<Response>> future) throws Exception {
								if (future.isSuccess()) {
									sendBlocks(module.getUUID(), reverseModuleMapping.get(module), appId).addListener(new FutureListener<FutureNotifier<Collection<Response>>>() {
										@Override
										public void operationComplete(FutureNotifier<Collection<Response>> future) throws Exception {
											if (future.isSuccess()) {
												for (final Response response : future.getNow()) {
													if (!(response instanceof BlockAck)) {
														sendBlockFutureNotifier.failed(null);
														return;
													}
												}
												sendBlockFutureNotifier.finishedSuccessfully();
											} else {
												sendBlockFutureNotifier.failed(future.cause());
											}
										}
									});
								} else {
									sendBlockFutureNotifier.failed(future.cause());
								}
							}
						});
					} else {
						sendBlockFutureNotifier.failed(future.cause());
					}
				}
			});
		}
		
		final DeployFutureNotifier deployFutureNotifier = new DeployFutureNotifier();
		sendBlockFutureNotifier.addListener(new FutureListener<FutureNotifier<? super Void>>() {
			@Override
			public void operationComplete(FutureNotifier<? super Void> future) {
				final Set<UUID> moduleUUIDs = new HashSet<UUID>();
				for (final Module module : filesPerModule.keySet()) {
					moduleUUIDs.add(module.getUUID());
				}
				sendStartApplication(moduleUUIDs, appId, deployFutureNotifier);
			}
		});
		LOGGER.exit(deployFutureNotifier);
		return deployFutureNotifier;
	}
	
	private Map<FunctionBlock, Module> getModuleMapping(final Map<FunctionBlock, BlockTarget> distribution) {
		final Map<FunctionBlock, Module> moduleMapping = new HashMap<FunctionBlock, Module>();
		
		for (final Entry<FunctionBlock, BlockTarget> entry : distribution.entrySet()) {
			moduleMapping.put(entry.getKey(), entry.getValue().getModule());
		}
		
		return moduleMapping;
	}

	private Map<FunctionBlock, Set<ClassFile>> getNeededFiles(final Collection<FunctionBlock> blocks) {
		final Map<FunctionBlock, Set<ClassFile>> neededFiles = new HashMap<FunctionBlock, Set<ClassFile>>();
		final Map<String, Set<ClassFile>> neededFilesCache = new HashMap<String, Set<ClassFile>>();
		
		for (final FunctionBlock block : blocks) {
			final String clsName = block.getClass().getName();
			Set<ClassFile> needed = neededFilesCache.get(clsName);
			if (needed == null) {
				final Set<ClassFile> files = new HashSet<ClassFile>();
				files.addAll(dependencies.getDependencies(clsName));
				needed = Collections.unmodifiableSet(files);
				neededFilesCache.put(clsName, needed);
			}
			neededFiles.put(block, needed);
		}
		
		return Collections.unmodifiableMap(neededFiles);
	}
	
	private FutureNotifier<Collection<Response>> sendClasses(final UUID moduleUUID, final Set<ClassFile> classFiles, final UUID appId) {
		LOGGER.entry(moduleUUID, classFiles, appId);
		final Collection<FutureNotifier<? extends Response>> futureNotifiers = new ArrayList<FutureNotifier<? extends Response>>();
		for (final ClassFile classFile : classFiles) {
			futureNotifiers.add(sendClass(moduleUUID, classFile, appId));
		}
		final FutureNotifier<Collection<Response>> futureNotifier = new JoinedFutureNotifier<Response>(futureNotifiers);
		LOGGER.exit(futureNotifier);
		return futureNotifier;
	}
	
	private FutureNotifier<? extends Response> sendClass(final UUID moduleUUID, final ClassFile classFile, final UUID appId) {
		LOGGER.entry(moduleUUID, classFile, appId);
		byte[] classData = null;
		final File file = classFile.getFile();
		SoftReference<byte[]> cachedReference = classFiles.get(file);
		if (cachedReference != null) {
			classData = cachedReference.get();
		}
		if (classData == null) {
			try {
				classData = loadFile(file);
			} catch (final IOException e) {
				return new FinishedFutureNotifier<Response>(e);
			}
			classFiles.put(file, new SoftReference<byte[]>(classData));
		}
		final FutureNotifier<? extends Response> futureNotifer = connectionManager.sendMessage(moduleUUID, new LoadClassMessage(classFile.getClassName(), classData, appId));
		LOGGER.exit(futureNotifer);
		return futureNotifer;
	}

	private byte[] loadFile(final File classFile) throws IOException {
		final byte[] data = new byte[(int) classFile.length()];
		final DataInputStream dis = new DataInputStream(new FileInputStream(classFile));
		try {
			dis.readFully(data);
			return data;
		} finally {
			dis.close();
		}
	}

	private FutureNotifier<Collection<Response>> sendBlocks(final UUID uuid, final Set<FunctionBlock> blocks, UUID appId) {
		LOGGER.entry(uuid, blocks, appId);
		final Collection<FutureNotifier<? extends Response>> futureNotifiers = new ArrayList<FutureNotifier<? extends Response>>();
		for (final FunctionBlock block : blocks) {
			futureNotifiers.add(sendBlock(uuid, block, appId));
		}
		final FutureNotifier<Collection<Response>> futureNotifier = new JoinedFutureNotifier<Response>(futureNotifiers);
		LOGGER.exit(futureNotifier);
		return futureNotifier;
	}
	
	private FutureNotifier<Response> sendBlock(final UUID uuid, final FunctionBlock block, final UUID appId) {
		return connectionManager.sendMessage(uuid, new BlockMessage(appId, block));
	}
	
	private void sendStartApplication(final Collection<UUID> moduleUUIDs, final UUID appId, final DeployFutureNotifier deployFutureNotifier) {
		final StartApplicationMessage startAppMsg = new StartApplicationMessage(appId);
		final Collection<FutureNotifier<? extends Response>> futureNotifiers = new ArrayList<FutureNotifier<? extends Response>>();
		for (final UUID moduleUUID : moduleUUIDs) {
			futureNotifiers.add(connectionManager.sendMessage(moduleUUID, startAppMsg));
		}
		new JoinedFutureNotifier<Response>(futureNotifiers).addListener(new FutureListener<FutureNotifier<Collection<Response>>>() {
			@Override
			public void operationComplete(FutureNotifier<Collection<Response>> future) {
				if (future.isSuccess()) {
					for (final Response response : future.getNow()) {
						if (!(response instanceof StartApplicationAck)) {
							deployFutureNotifier.setFailure0(null);
							return;
						}
					}
					deployFutureNotifier.setSuccess0();
				} else {
					deployFutureNotifier.setFailure0(future.cause());
				}
			}
		});
	}
	
	private class DeployFutureNotifier extends DefaultFutureNotifier<Void> {
		protected void setSuccess0() {
			setSuccess(null);
		}
		
		protected void setFailure0(final Throwable cause) {
			setFailure(cause);
		}
	}

	private class MultipleFutureNotifier extends DefaultFutureNotifier<Void> {
		private int count;
		
		public MultipleFutureNotifier(final int count) {
			this.count = count;
		}
		
		protected synchronized void finishedSuccessfully() {
			if (!isDone()) {
				if (--count <= 0) {
					setSuccess(null);
				}
			}
		}
		
		protected synchronized void failed(final Throwable cause) {
			if (!isDone()) {
				setFailure(cause);
			}
		}
	}
}
