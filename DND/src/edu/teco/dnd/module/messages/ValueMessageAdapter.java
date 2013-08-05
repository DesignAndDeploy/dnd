package edu.teco.dnd.module.messages;

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

import edu.teco.dnd.module.messages.values.ValueMessage;
import edu.teco.dnd.util.Base64;
import edu.teco.dnd.util.SerializableAdapter;

public class ValueMessageAdapter implements JsonDeserializer<ValueMessage>, JsonSerializer<ValueMessage> {

	/**
	 * The logger for this class.
	 */
	private static final Logger LOGGER = LogManager.getLogger(SerializableAdapter.class);
	private final ClassLoader loader;

	public ValueMessageAdapter(ClassLoader loader) {
		this.loader = loader;
	}

	@Override
	public JsonElement serialize(ValueMessage src, Type typeOfSrc, JsonSerializationContext context) {
		LOGGER.entry(src, typeOfSrc, context);
		final JsonObject jsonObject = new JsonObject();
		jsonObject.add("appId", context.serialize(src.getApplicationID()));
		jsonObject.add("blockUuid", context.serialize(src.blockId));
		jsonObject.add("input", context.serialize(src.input));
		try {
			jsonObject.add("value", new JsonPrimitive(Base64.encodeObject(src.value)));
		} catch (IOException e) {
			throw new JsonParseException("Can not base64 value of valueMessage.");
		}

		LOGGER.exit(jsonObject);
		return jsonObject;
	}

	@Override
	public ValueMessage deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException {

		UUID blockUuid;
		UUID appId;
		String input;
		Serializable value;

		LOGGER.entry(json, typeOfT, context);
		if (!json.isJsonObject()) {
			LOGGER.exit();
			throw new JsonParseException("not a JSON primitive");
		}
		JsonObject jObject = json.getAsJsonObject();

		blockUuid = context.deserialize(jObject.get("blockUuid"), UUID.class);
		appId = context.deserialize(jObject.get("appId"), UUID.class);
		input = context.deserialize(jObject.get("input"), String.class);
		try {
			value = (Serializable) Base64.decodeToObject(jObject.get("value").getAsString(), Base64.NO_OPTIONS, loader);
		} catch (IOException e) {
			throw new JsonParseException("can not parse base64 serializable");
		} catch (ClassNotFoundException e) {
			throw new JsonParseException("no appropriate class to decode serializable in value message.");
		}

		return new ValueMessage(appId, blockUuid, input, value);
	}

}
