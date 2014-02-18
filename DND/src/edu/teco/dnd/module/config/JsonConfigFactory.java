package edu.teco.dnd.module.config;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.InetSocketAddress;
import java.net.URI;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import edu.teco.dnd.util.InetSocketAddressAdapter;
import edu.teco.dnd.util.NetConnection;
import edu.teco.dnd.util.NetConnectionAdapter;

public class JsonConfigFactory implements ModuleConfigFactory {
	private static final Logger LOGGER = LogManager.getLogger(JsonConfigFactory.class);
	
	private static final Gson GSON;
	static {
		final GsonBuilder builder = new GsonBuilder();
		builder.setPrettyPrinting();
		
		builder.registerTypeAdapter(InetSocketAddress.class, new InetSocketAddressAdapter());
		builder.registerTypeAdapter(NetConnection.class, new NetConnectionAdapter());
		
		final ExclusionStrategy amountLeftExclusionStrategy = new AmountLeftExclusionStrategy();
		builder.addDeserializationExclusionStrategy(amountLeftExclusionStrategy);
		builder.addSerializationExclusionStrategy(amountLeftExclusionStrategy);
		
		GSON = builder.create();
	}
	
	@Override
	public ModuleConfig loadConfiguration(final URI uri) throws IOException {
		LOGGER.entry(uri);
		final InputStream inputStream = uri.toURL().openStream();
		final Reader reader = new InputStreamReader(inputStream);
		final JsonConfig jsonConfig = GSON.fromJson(reader, JsonConfig.class);
		jsonConfig.initialize();
		return LOGGER.exit(jsonConfig);
	}

	private static class AmountLeftExclusionStrategy implements ExclusionStrategy {
		@Override
		public boolean shouldSkipField(final FieldAttributes f) {
			return BlockTypeHolder.class.equals(f.getDeclaringClass()) && "amountLeft".equals(f.getName());
		}
	
		@Override
		public boolean shouldSkipClass(final Class<?> clazz) {
			return false;
		}
	}
}
