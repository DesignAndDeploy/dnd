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

import edu.teco.dnd.blocks.FunctionBlockID;
import edu.teco.dnd.module.ApplicationID;

/**
 * A Gson Adapter that handles BlockIDs.
 * 
 * @author jung
 * 
 */
public class ApplicationBlockIDAdapter implements JsonSerializer<ApplicationBlockID>,
		JsonDeserializer<ApplicationBlockID> {
	/**
	 * The logger for this class.
	 */
	private static final Logger LOGGER = LogManager.getLogger(ApplicationBlockIDAdapter.class);

	@Override
	public ApplicationBlockID deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException {
		LOGGER.entry(json, typeOfT, context);

		if (!json.isJsonObject()) {
			LOGGER.exit();
			throw new JsonParseException("not a JSON object");
		}

		final JsonObject obj = (JsonObject) json;
		final JsonElement blockID = obj.get("blockID");
		final JsonElement applicationID = obj.get("applicationID");
		if (blockID == null || applicationID == null) {
			LOGGER.exit();
			throw new JsonParseException("blockID/applicationID missing");
		}

		if (!blockID.isJsonPrimitive() || !((JsonPrimitive) blockID).isString()) {
			LOGGER.exit();
			throw new JsonParseException("blockID is not a string");
		}
		if (!applicationID.isJsonPrimitive() || !((JsonPrimitive) applicationID).isString()) {
			LOGGER.exit();
			throw new JsonParseException("applicationID is not a string");
		}

		final ApplicationBlockID applicationBlockID =
				new ApplicationBlockID(new FunctionBlockID(UUID.fromString(blockID.getAsString())), new ApplicationID(
						UUID.fromString(applicationID.getAsString())));
		LOGGER.exit(applicationBlockID);
		return applicationBlockID;
	}

	@Override
	public JsonElement serialize(ApplicationBlockID src, Type typeOfSrc, JsonSerializationContext context) {
		LOGGER.entry(src, typeOfSrc, context);
		final JsonObject obj = new JsonObject();
		obj.addProperty("blockID", src.getBlockID().toString());
		obj.add("applicationID",
				context.serialize(src.getApplicationID() == null ? null : src.getApplicationID().getUUID()));
		LOGGER.exit(obj);
		return obj;
	}

}
