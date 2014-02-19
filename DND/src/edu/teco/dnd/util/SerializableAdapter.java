package edu.teco.dnd.util;

import java.io.IOException;
import java.io.Serializable;
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
import com.google.gson.JsonSyntaxException;

/**
 * A GSON adapter for {@link Serializable} which are stored as Base64 encoded byte arrays.
 */
public class SerializableAdapter implements JsonSerializer<Serializable>, JsonDeserializer<Serializable> {
	private static final Logger LOGGER = LogManager.getLogger(SerializableAdapter.class);

	private final ClassLoader loader;

	/**
	 * Initializes a new SerializableAdapter.
	 * 
	 * @param loader
	 *            a ClassLoader that will be used to deserialize incoming objects
	 */
	public SerializableAdapter(ClassLoader loader) {
		this.loader = loader;
	}

	@Override
	public Serializable deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
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
			return (Serializable) Base64.decodeToObject(obj.getAsString(), Base64.NO_OPTIONS, loader);
		} catch (IOException e) {
			throw new JsonParseException("can not parse base64 serializable");
		} catch (ClassNotFoundException e) {
			throw new JsonParseException("no appropriate class to decode serializable in value message.");
		}

	}

	@Override
	public JsonElement serialize(final Serializable src, final Type typeOfSrc, final JsonSerializationContext context) {
		LOGGER.entry(src, typeOfSrc, context);
		JsonPrimitive a;
		try {
			a = new JsonPrimitive(Base64.encodeObject(src));
		} catch (IOException e) {
			throw new JsonSyntaxException(e);
		}
		LOGGER.exit(a);
		return a;
	}

}
