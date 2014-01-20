package edu.teco.dnd.module.messages.infoReq;

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

/**
 * A Gson Adapter that handles BlockIDs.
 * 
 * @author jung
 * 
 */
public class BlockIDAdapter implements JsonSerializer<ApplicationBlockID>, JsonDeserializer<ApplicationBlockID> {
	/**
	 * The logger for this class.
	 */
	private static final Logger LOGGER = LogManager.getLogger(BlockIDAdapter.class);

	@Override
	public ApplicationBlockID deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException {
		LOGGER.entry(json, typeOfT, context);

		if (!json.isJsonObject()) {
			LOGGER.exit();
			throw new JsonParseException("not a JSON object");
		}

		final JsonObject obj = (JsonObject) json;
		final JsonElement blockUUID = obj.get("blockUUID");
		final JsonElement appUUID = obj.get("appID");
		if (blockUUID == null || appUUID == null) {
			LOGGER.exit();
			throw new JsonParseException("blockUUID/appID missing");
		}

		if (!blockUUID.isJsonPrimitive() || !((JsonPrimitive) blockUUID).isString()) {
			LOGGER.exit();
			throw new JsonParseException("blockUUID is not a string");
		}
		if (!appUUID.isJsonPrimitive() || !((JsonPrimitive) appUUID).isString()) {
			LOGGER.exit();
			throw new JsonParseException("appID is not a string");
		}

		final ApplicationBlockID applicationBlockID =
				new ApplicationBlockID(UUID.fromString(blockUUID.getAsString()), UUID.fromString(appUUID.getAsString()));
		LOGGER.exit(applicationBlockID);
		return applicationBlockID;
	}

	@Override
	public JsonElement serialize(ApplicationBlockID src, Type typeOfSrc, JsonSerializationContext context) {
		LOGGER.entry(src, typeOfSrc, context);
		final JsonObject obj = new JsonObject();
		obj.addProperty("blockUUID", src.getBlockUUID().toString());
		obj.addProperty("appID", src.getAppID().toString());
		LOGGER.exit(obj);
		return obj;
	}

}
