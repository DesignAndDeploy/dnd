package edu.teco.dnd.network.messages;

import java.lang.reflect.Type;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

/**
 * A deserializer for {@link BeaconMessage}s that uses the public constructor so that <code>addresses</code> is kept
 * unmodifiable.
 * 
 * @author Philipp Adolf
 */
public class BeaconMessageDeserializer implements JsonDeserializer<BeaconMessage> {
	@Override
	public BeaconMessage deserialize(final JsonElement json, final Type typeOfT,
			final JsonDeserializationContext context) {
		if (!json.isJsonObject()) {
			throw new JsonParseException("not an object");
		}
		final JsonObject jsonObject = json.getAsJsonObject();

		final UUID uuid;
		if (jsonObject.has("uuid")) {
			uuid = context.deserialize(jsonObject.get("uuid"), UUID.class);
		} else {
			uuid = null;
		}

		final UUID moduleUUID;
		if (jsonObject.has("moduleUUID")) {
			moduleUUID = context.deserialize(jsonObject.get("moduleUUID"), UUID.class);
		} else {
			moduleUUID = null;
		}

		final ArrayList<InetSocketAddress> addresses = new ArrayList<InetSocketAddress>();
		if (jsonObject.has("addresses")) {
			final JsonElement addressesElement = jsonObject.get("addresses");
			if (!addressesElement.isJsonArray()) {
				throw new JsonParseException("addresses is not an array");
			}
			final JsonArray addressesArray = addressesElement.getAsJsonArray();
			addresses.ensureCapacity(addressesArray.size());
			for (final JsonElement element : addressesElement.getAsJsonArray()) {
				final InetSocketAddress address = context.deserialize(element, InetSocketAddress.class);
				addresses.add(address);
			}
		}

		return new BeaconMessage(uuid, moduleUUID, addresses);
	}
}
