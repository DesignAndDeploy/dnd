package edu.teco.dnd.discover;

import java.util.UUID;

import edu.teco.dnd.module.ModuleInfo;
import edu.teco.dnd.module.messages.infoReq.ModuleInfoMessage;
import edu.teco.dnd.module.messages.infoReq.RequestModuleInfoMessage;
import edu.teco.dnd.network.ConnectionManager;
import edu.teco.dnd.network.messages.Response;
import edu.teco.dnd.util.DefaultFutureNotifier;
import edu.teco.dnd.util.FutureListener;
import edu.teco.dnd.util.FutureNotifier;

public class ModuleQuery {
	private final ConnectionManager connectionManager;

	public ModuleQuery(final ConnectionManager connectionManager) {
		this.connectionManager = connectionManager;
	}

	public ModuleInfoFutureNotifier getModuleInfo(final UUID moduleUUID) {
		final ModuleInfoFutureNotifier notifier = new ModuleInfoFutureNotifier(moduleUUID);
		connectionManager.sendMessage(moduleUUID, new RequestModuleInfoMessage()).addListener(notifier);
		return notifier;
	}

	public static class ModuleInfoFutureNotifier extends DefaultFutureNotifier<ModuleInfo> implements
			FutureListener<FutureNotifier<Response>> {
		private final UUID moduleUUID;

		public ModuleInfoFutureNotifier(final UUID moduleUUID) {
			this.moduleUUID = moduleUUID;
		}

		public UUID getModuleUUID() {
			return this.moduleUUID;
		}

		@Override
		public void operationComplete(final FutureNotifier<Response> future) {
			if (future.isSuccess()) {
				final Response response = future.getNow();
				if (response == null || !(response instanceof ModuleInfoMessage)) {
					setFailure(new IllegalArgumentException("did not get a ModuleInfoMessage"));
				} else {
					setSuccess(((ModuleInfoMessage) response).getModule());
				}
			} else {
				setFailure(future.cause());
			}
		}
	}
}
