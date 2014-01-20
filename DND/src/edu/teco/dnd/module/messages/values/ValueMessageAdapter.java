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
import edu.teco.dnd.module.Module;
import edu.teco.dnd.util.Base64;

/**
 * GSon adapter used to (de)serialize a ValueMessage. Reads the Object and writes it as primitives onto the sjson
 * stream. or the other way round.
 * 
 * @author Marvin Marx
 * 
 */
public class ValueMessageAdapter implements JsonDeserializer<ValueMessage>, JsonSerializer<ValueMessage> {

	/**
	 * The logger for this class.
	 */
	private static final Logger LOGGER = LogManager.getLogger(ValueMessageAdapter.class);
	private final Module module;

	/**
	 * 
	 * @param module
	 *            the Module used to retrieve the appropriate class loader. ( In case the value is of a type
	 *            that is missing on the module).
	 */
	public ValueMessageAdapter(Module module) {
		this.module = module;
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
		UUID appId;
		UUID blockUuid;
		String input;
		Serializable value;

		LOGGER.entry(json, typeOfT, context);
		if (!json.isJsonObject()) {
			LOGGER.exit();
			throw new JsonParseException("not a JSON primitive");
		}
		JsonObject jObject = json.getAsJsonObject();

		appId = context.deserialize(jObject.get("appId"), UUID.class);
		
		ClassLoader loader = null;
		final Application application = module.getApplication(appId);
		if (application != null) {
			loader = application.getClassLoader();
		}
		
		blockUuid = context.deserialize(jObject.get("blockUuid"), UUID.class);
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
