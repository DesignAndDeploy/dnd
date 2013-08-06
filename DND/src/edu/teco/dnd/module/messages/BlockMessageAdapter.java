package edu.teco.dnd.module.messages;

import java.io.IOException;
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

import edu.teco.dnd.blocks.FunctionBlock;
import edu.teco.dnd.module.ModuleApplicationManager;
import edu.teco.dnd.module.messages.loadStartBlock.BlockMessage;
import edu.teco.dnd.util.Base64;

public class BlockMessageAdapter implements JsonDeserializer<BlockMessage>, JsonSerializer<BlockMessage> {
	/**
	 * The logger for this class.
	 */
	private static final Logger LOGGER = LogManager.getLogger(BlockMessageAdapter.class);
	private final ModuleApplicationManager appMan;

	public BlockMessageAdapter(ModuleApplicationManager appMan) {
		this.appMan = appMan;
	}

	@Override
	public JsonElement serialize(BlockMessage src, Type typeOfSrc, JsonSerializationContext context) {
		LOGGER.entry(src, typeOfSrc, context);
		final JsonObject jsonObject = new JsonObject();
		jsonObject.add("appId", context.serialize(src.getApplicationID()));
		jsonObject.add("className", context.serialize(src.className));
		jsonObject.add("uuid", context.serialize(src.getUUID()));
		try {
			jsonObject.add("block", new JsonPrimitive(Base64.encodeObject(src.block)));
		} catch (IOException e) {
			throw new JsonParseException("Can not base64 value of valueMessage.");
		}

		LOGGER.exit(jsonObject);
		return jsonObject;
	}

	@Override
	public BlockMessage deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
		UUID appId;
		UUID msgUuid;
		String className;
		FunctionBlock block;

		LOGGER.entry(json, typeOfT, context);
		if (!json.isJsonObject()) {
			LOGGER.exit();
			throw new JsonParseException("not a JSON primitive");
		}
		JsonObject jObject = json.getAsJsonObject();

		appId = context.deserialize(jObject.get("appId"), UUID.class);
		msgUuid = context.deserialize(jObject.get("uuid"), UUID.class);
		ClassLoader loader = appMan.getAppClassLoader(appId);
		className = context.deserialize(jObject.get("className"), String.class);
		try {
			block = (FunctionBlock) Base64
					.decodeToObject(jObject.get("block").getAsString(), Base64.NO_OPTIONS, loader);
		} catch (IOException e) {
			throw new JsonParseException("can not parse base64 serializable");
		} catch (ClassNotFoundException e) {
			throw new JsonParseException("no appropriate class to decode serializable in value message.");
		}

		return new BlockMessage(msgUuid, appId, className, block);
	}

}
