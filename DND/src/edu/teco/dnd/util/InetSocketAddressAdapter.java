package edu.teco.dnd.util;

import java.lang.reflect.Type;
import java.net.InetSocketAddress;

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

/**
 * A Gson adapter that handles InetSocketAddresses.
 * 
 * @author Philipp Adolf
 */
public class InetSocketAddressAdapter implements JsonSerializer<InetSocketAddress>, JsonDeserializer<InetSocketAddress> {
	/**
	 * The logger for this class.
	 */
	private static final Logger LOGGER = LogManager.getLogger(InetSocketAddressAdapter.class);

	@Override
	public InetSocketAddress deserialize(final JsonElement json, final Type typeOfT,
			final JsonDeserializationContext context) throws JsonParseException {
		LOGGER.entry(json, typeOfT, context);
		if (!json.isJsonObject()) {
			LOGGER.exit();
			throw new JsonParseException("not a JSON object");
		}
		final JsonObject obj = (JsonObject) json;
		final JsonElement address = obj.get("address");
		final JsonElement port = obj.get("port");
		if (address == null || port == null) {
			LOGGER.exit();
			throw new JsonParseException("address/port missing");
		}
		if (!address.isJsonPrimitive() || !((JsonPrimitive) address).isString()) {
			LOGGER.exit();
			throw new JsonParseException("address is not a string");
		}
		if (!port.isJsonPrimitive() || !((JsonPrimitive) port).isNumber()) {
			LOGGER.exit();
			throw new JsonParseException("port is not a number");
		}
		final InetSocketAddress isa = new InetSocketAddress(address.getAsString(), port.getAsInt());
		LOGGER.exit(isa);
		return isa;
	}

	@Override
	public JsonElement serialize(final InetSocketAddress src, final Type typeOfSrc,
			final JsonSerializationContext context) {
		LOGGER.entry(src, typeOfSrc, context);
		final JsonObject obj = new JsonObject();
		obj.addProperty("address", src.getHostName());
		obj.addProperty("port", src.getPort());
		LOGGER.exit(obj);
		return obj;
	}

}
