package edu.teco.dnd.module.messages.loadStartBlock;

import java.io.IOException;
import java.lang.reflect.Type;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import edu.teco.dnd.util.Base64;

public class BlockMessageSerializerAdapter implements JsonSerializer<BlockMessage> {
	/**
	 * The logger for this class.
	 */
	private static final Logger LOGGER = LogManager.getLogger(BlockMessageSerializerAdapter.class);

	@Override
	public JsonElement serialize(BlockMessage src, Type typeOfSrc, JsonSerializationContext context) {
		LOGGER.entry(src, typeOfSrc, context);
		final JsonObject jsonObject = new JsonObject();
		jsonObject.add("appId", context.serialize(src.getApplicationID()));
		jsonObject.add("uuid", context.serialize(src.getUUID()));
		jsonObject.add("scheduleToId", context.serialize(src.scheduleToId));
		try {
			jsonObject.add("block", new JsonPrimitive(Base64.encodeObject(src.block)));
		} catch (IOException e) {
			throw new JsonParseException("Can not base64 block of blockMessage.");
		}

		LOGGER.exit(jsonObject);
		return jsonObject;
	}
}
