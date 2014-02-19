package edu.teco.dnd.module.config;

import java.io.IOException;
import java.net.URI;

/**
 * A factory that can load {@link ModuleConfig}s from {@link URI}s.
 */
public interface ModuleConfigFactory {
	/**
	 * Loads a {@link ModuleConfig} from a given {@link URI}.
	 * 
	 * @param uri
	 *            the URI to load. Implementations do not have to accept all possible kinds of URIs.
	 * @return the ModuleConfig if successfully loaded
	 * @throws IOException
	 *             if an error occurs during loading
	 */
	ModuleConfig loadConfiguration(URI uri) throws IOException;
}
