package edu.teco.dnd.module.messages.infoReq;

import java.lang.reflect.Type;
import java.util.UUID;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import edu.teco.dnd.module.ModuleInfo;
import edu.teco.dnd.module.config.BlockTypeHolder;

/**
 * Adapter to deserialize a ModuleInfoMessage. Needed because it contains a BlockTypeHolder tree, which naturally has
 * references to parent nodes, which however are not serialized to avoid infinite recursions. Restoring the values here.
 * 
 * @author Marvin Marx
 * 
 */
public class ModuleInfoMessageAdapter implements JsonDeserializer<ModuleInfoMessage> {
	@Override
	public ModuleInfoMessage deserialize(final JsonElement json, final Type typeOfT,
			final JsonDeserializationContext context) throws JsonParseException {
		if (!json.isJsonObject()) {
			throw new JsonParseException("is not an object");
		}
		final JsonObject jsonObj = (JsonObject) json;
		final JsonElement jsonUUID = jsonObj.get("uuid");
		UUID uuid = context.deserialize(jsonUUID, UUID.class);
		final JsonElement jsonSourceUUID = jsonObj.get("sourceuuid");
		UUID sourceUUID = context.deserialize(jsonSourceUUID, UUID.class);
		final JsonElement jsonModule = jsonObj.get("module");
		ModuleInfo module = context.deserialize(jsonModule, ModuleInfo.class);
		final BlockTypeHolder rootHolder = module.getHolder();
		if (rootHolder != null) {
			setParents(rootHolder);
		}
		return new ModuleInfoMessage(sourceUUID, uuid, module);
	}

	/**
	 * recursively set the parents of nodes in the tree.
	 * 
	 * @param blockTypeHolder
	 *            the root of the tree (if called externally).
	 */
	private static void setParents(final BlockTypeHolder blockTypeHolder) {
		for (final BlockTypeHolder current : blockTypeHolder) {
			for (final BlockTypeHolder child : current.getChildren()) {
				child.setParent(current);
			}
		}
	}
}
