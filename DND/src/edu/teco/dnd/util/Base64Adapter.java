package edu.teco.dnd.util;

import java.io.IOException;
import java.lang.reflect.Type;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * A GSON adapter that translates byte-arrays into base64 encoded text.
 */
public class Base64Adapter implements JsonSerializer<byte[]>, JsonDeserializer<byte[]> {
	private static final Logger LOGGER = LogManager.getLogger(Base64Adapter.class);

	@Override
	public byte[] deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException {
		LOGGER.entry(json, typeOfT, context);
		if (!json.isJsonPrimitive()) {
			LOGGER.exit();
			throw new JsonParseException("not a JSON primitive");
		}
		final JsonPrimitive obj = (JsonPrimitive) json;
		if (!obj.isString()) {
			LOGGER.exit();
			throw new JsonParseException("not a String primitive");
		}

		try {
			return Base64.decode(obj.getAsString());
		} catch (IOException e) {
			throw new JsonParseException("can not parse base64 byte[]");
		}

	}

	@Override
	public JsonElement serialize(final byte[] src, final Type typeOfSrc, final JsonSerializationContext context) {
		LOGGER.entry(src, typeOfSrc, context);
		JsonPrimitive a = new JsonPrimitive(Base64.encodeBytes(src));
		LOGGER.exit(a);
		return a;
	}

}
