package edu.teco.dnd.module.config;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import edu.teco.dnd.util.InetSocketAddressAdapter;
import edu.teco.dnd.util.NetConnection;
import edu.teco.dnd.util.NetConnectionAdapter;

public class JsonConfig extends ConfigReader {
	private String name;
	private UUID uuid = UUID.randomUUID();
	private InetSocketAddress[] listen;
	private InetSocketAddress[] announce;
	private NetConnection[] multicast;
	private BlockTypeHolder allowedBlocks; // the rootBlock

	private static transient final Logger LOGGER = LogManager.getLogger(JsonConfig.class);
	private static transient final Gson gson;
	static {
		GsonBuilder builder = new GsonBuilder();
		builder.setPrettyPrinting();
		builder.registerTypeAdapter(InetSocketAddress.class, new InetSocketAddressAdapter());
		builder.registerTypeAdapter(NetConnection.class, new NetConnectionAdapter());
		gson = builder.create();
	}
	
	private transient Map<String, BlockTypeHolder> blockQuickaccess = new HashMap<String, BlockTypeHolder>();

	public JsonConfig() {
	}

	public JsonConfig(String path) throws IOException {
		this.load(path);
	}

	public void setTo(JsonConfig oldConf) {
		if (oldConf == null) {
			LOGGER.warn("Invalid Config to set(config was null)");
			throw new NullPointerException();
		}

		this.name = oldConf.name;
		if (oldConf.uuid != null) {
			this.uuid = oldConf.uuid;
		}
		this.listen = oldConf.listen;
		this.announce = oldConf.announce;
		this.multicast = oldConf.multicast;
		this.allowedBlocks = oldConf.allowedBlocks;
	}

	@Override
	public void load(String path) throws IOException {
		FileReader reader = null;
		try {
			reader = new FileReader(path);
			setTo((JsonConfig) gson.fromJson(reader, this.getClass()));
		} catch (FileNotFoundException e) {
			LOGGER.catching(e);
			throw e;
		} finally {
			try {
				reader.close();
			} catch (Exception e) {
			}
		}

		
		  //TODO set up base config for testing
//		  allowedBlocks = new BlockType(0); 
//		  BlockType b = new BlockType(1); 
//		  b.addChild(new BlockType("child1Type", 2));
//		  b.addChild(new BlockType("child2Type", 2));
//		  allowedBlocks.addChild(b); 
//		  allowedBlocks.addChild(new BlockType("child2TYPE", 1));
		 

		if (allowedBlocks != null) {
			fillTransientVariables(blockQuickaccess, allowedBlocks);
		}
	}

	private void fillTransientVariables(Map<String, BlockTypeHolder> blockQuickaccess, final BlockTypeHolder currentBlock) {
		Set<BlockTypeHolder> children = currentBlock.getChildren();
		if (children == null) {
			blockQuickaccess.put(currentBlock.type, currentBlock);
		} else {
			for (BlockTypeHolder child : currentBlock.getChildren()) {
				child.setParent(currentBlock);
				fillTransientVariables(blockQuickaccess, child);
			}
		}
	}

	@Override
	public boolean store(String path) {
		FileWriter writer = null;
		try {
			writer = new FileWriter(path);
			gson.toJson(this, writer);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				writer.close();
			} catch (Exception e) {
			}
		}
		return true;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public UUID getUuid() {
		return uuid;
	}

	@Override
	public InetSocketAddress[] getListen() {
		return listen;
	}

	@Override
	public InetSocketAddress[] getAnnounce() {
		return announce;
	}

	@Override
	public NetConnection[] getMulticast() {
		return multicast;
	}

	public BlockTypeHolder getBlockRoot() {
		return allowedBlocks;
	}

	public Map<String, BlockTypeHolder> getAllowedBlocks() {
		return blockQuickaccess;
	}

}
