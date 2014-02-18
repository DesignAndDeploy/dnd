package edu.teco.dnd.module.config;

import java.io.IOException;
import java.net.URI;

public interface ModuleConfigFactory {
	ModuleConfig loadConfiguration(URI uri) throws IOException;
}
