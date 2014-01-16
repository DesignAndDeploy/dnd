package edu.teco.dnd.module;

import io.netty.channel.EventLoopGroup;
import io.netty.util.concurrent.Future;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

/**
 * Runnable passed to ModuleApplicationManager which will be executed when the ModuleInfo is supposed to be shut down.
 * 
 */
public class ModuleShutdownHook implements Runnable {

	private final Set<EventLoopGroup> eventLoopGroups;

	/**
	 * @param eventLoopGroups
	 *            the eventLoopGroups that will be "shutdownGracefully()", when the runnable is executed.
	 */
	public ModuleShutdownHook(final Set<EventLoopGroup> eventLoopGroups) {
		this.eventLoopGroups = eventLoopGroups;
	}

	@Override
	public void run() {
		synchronized (this) { // synchronized with code in ModuleMain.

			final Collection<Future<?>> futures = new ArrayList<Future<?>>();
			for (final EventLoopGroup group : eventLoopGroups) {
				futures.add(group.shutdownGracefully());
			}
			for (final Future<?> future : futures) {
				future.awaitUninterruptibly();
			}
			eventLoopGroups.clear();
		}
	}

}
