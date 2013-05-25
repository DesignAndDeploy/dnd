package edu.teco.dnd.module.config;

import java.lang.reflect.Type;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import edu.teco.dnd.module.config.ConfigReader.NetConnection;

/**
 * A Gson adapter for {@link NetConnection}.
 * 
 * @author Philipp Adolf
 */
public class NetConnectionAdapter implements JsonSerializer<NetConnection>, JsonDeserializer<NetConnection> {
	/**
	 * The logger for this class.
	 */
	private static final Logger LOGGER = LogManager.getLogger(NetConnectionAdapter.class);
	
	@Override
	public NetConnection deserialize(final JsonElement json, final Type typeOfT,
			final JsonDeserializationContext context) throws JsonParseException {
		LOGGER.entry(json, typeOfT, context);
		
		if (!json.isJsonObject()) {
			LOGGER.exit();
			throw new JsonParseException("not a JSON object");
		}
		
		final InetSocketAddress address = context.deserialize(json, InetSocketAddress.class);
		
		NetworkInterface interf = null;
		final JsonElement interfaceElement = json.getAsJsonObject().get("interface");
		if (interfaceElement != null) {
			if (!interfaceElement.isJsonPrimitive()) {
				throw new JsonParseException("address is not a JSON primitive");
			}
			final JsonPrimitive interfacePrimitive = interfaceElement.getAsJsonPrimitive();
			if (!interfacePrimitive.isString()) {
				throw new JsonParseException("interface is not a string");
			}
			try {
				interf = NetworkInterface.getByName(interfacePrimitive.getAsString());
			} catch (final SocketException e) {
				throw new JsonParseException("could not get interface", e);
			}
		}
		
		final NetConnection netConnection = new NetConnection(address, interf);
		LOGGER.exit(netConnection);
		return netConnection;
	}

	@Override
	public JsonElement serialize(final NetConnection src, final Type typeOfSrc,
			final JsonSerializationContext context) {
		LOGGER.entry(src, typeOfSrc, context);
		final JsonObject jsonObject = (JsonObject) context.serialize(src.getAddress(), InetSocketAddress.class);
		final NetworkInterface interf = src.getInterface();
		if (interf != null) {
			jsonObject.addProperty("interface", src.getInterface().getName());
		}
		LOGGER.exit(jsonObject);
		return jsonObject;
	}

}
