package edu.teco.dnd.module.messages.values;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.UUID;

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

import edu.teco.dnd.module.Application;
import edu.teco.dnd.module.ApplicationID;
import edu.teco.dnd.module.Module;
import edu.teco.dnd.util.Base64;

/**
 * Adapter for {@link ValueMessage}. Serializes the value using the Serializable interface, then encode that as Base64.
 * Other fields are simply encoded using default GSON encoders.
 */
public class ValueMessageAdapter implements JsonDeserializer<ValueMessage>, JsonSerializer<ValueMessage> {
	private static final Logger LOGGER = LogManager.getLogger(ValueMessageAdapter.class);

	private final Module module;

	/**
	 * Initializes a new ValueMessageAdapter.
	 * 
	 * @param module
	 *            this module is used to get the Application's ClassLoaders (based on the Application ID in the received
	 *            message). If null or if the Application is missing only the default ClassLoader will be used.
	 */
	public ValueMessageAdapter(final Module module) {
		this.module = module;
	}

	@Override
	public JsonElement serialize(final ValueMessage src, final Type typeOfSrc, final JsonSerializationContext context) {
		LOGGER.entry(src, typeOfSrc, context);
		String encodedValue = null;
		try {
			encodedValue = Base64.encodeObject(src.value);
		} catch (final IOException e) {
			throw new JsonParseException("Failed to encode value as Base64", e);
		}
		
		final JsonObject jsonObject = new JsonObject();
		jsonObject.add("appId", context.serialize(src.getApplicationID()));
		jsonObject.add("blockUuid", context.serialize(src.blockId));
		jsonObject.add("input", context.serialize(src.input));
		jsonObject.add("value", new JsonPrimitive(encodedValue));

		return LOGGER.exit(jsonObject);
	}

	@Override
	public ValueMessage deserialize(final JsonElement json, final Type typeOfT, final JsonDeserializationContext context)
			throws JsonParseException {
		LOGGER.entry(json, typeOfT, context);
		if (!json.isJsonObject()) {
			throw LOGGER.throwing(new JsonParseException("not a JSON object"));
		}
		final JsonObject jsonObject = json.getAsJsonObject();

		final ApplicationID applicationID = context.deserialize(jsonObject.get("applicationID"), ApplicationID.class);
		final UUID blockUuid = context.deserialize(jsonObject.get("blockUuid"), UUID.class);
		final String input = context.deserialize(jsonObject.get("input"), String.class);

		ClassLoader loader = getClassLoaderForApplication(applicationID);
		Serializable value = null;
		try {
			value = (Serializable) Base64.decodeToObject(jsonObject.get("value").getAsString(), Base64.NO_OPTIONS, loader);
		} catch (final IOException e) {
			throw LOGGER.throwing(new JsonParseException("error parsing Base64 value", e));
		} catch (final ClassNotFoundException e) {
			throw LOGGER.throwing(new JsonParseException("could not find class of value", e));
		}

		return new ValueMessage(applicationID, blockUuid, input, value);
	}

	private ClassLoader getClassLoaderForApplication(final ApplicationID appId) {
		LOGGER.entry(appId);
		if (module == null) {
			return LOGGER.exit(null);
		}

		final Application application = module.getApplication(appId);
		if (application == null) {
			return LOGGER.exit(null);
		}

		return LOGGER.exit(application.getClassLoader());
	}
}
