package edu.teco.dnd.network.codecs;

import java.lang.reflect.Type;
import java.util.Map;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import edu.teco.dnd.network.messages.Message;

/**
 * A Gson adapter that adds a type field to Message objects when serializing and uses the type field to select the
 * right class when deserializing.
 *
 * @author Philipp Adolf
 */
public class MessageAdapter implements JsonSerializer<Message>, JsonDeserializer<Message> {
	/**
	 * The name of the attribute that will be read if {@link #addMessageType(Class)} is used.
	 */
	public static final String TYPE_ATTRIBUTE_NAME = "MESSAGE_TYPE";
	
	/**
	 * The default name of the type field.
	 */
	public static final String DEFAULT_TYPE_FIELD_NAME = "type";
	
	/**
	 * The name of the type field.
	 */
	private final String typeFieldName = null;
	
	/**
	 * Maps from type name to actual class.
	 */
	private final Map<String, Class<? extends Message>> types = null;
	
	/**
	 * Maps from class to type name.
	 */
	private final Map<Class<? extends Message>, String> clss = null;
	
	/**
	 * Creates a new MessageAdapter.
	 * 
	 * @param type the type name to use
	 */
	public MessageAdapter(final String type) {
	}
	
	/**
	 * Creates a new MessageAdapter using the default type field name.
	 * 
	 * @see #DEFAULT_TYPE_FIELD_NAME
	 */
	public MessageAdapter() {
	}
	
	/**
	 * Adds a type of Message. If either the class or the type name are already in use, nothing is done.
	 * 
	 * @param cls the class to add
	 * @param type the name to use when (de-)serializing this class
	 */
	public void addMessageType(final Class<? extends Message> cls, final String type) {
	}
	
	/**
	 * Adds a type of Message. The attribute named {@value #TYPE_ATTRIBUTE_NAME} is used to determine the type name.
	 * If either the class or the type name are already in use, nothing is done.
	 * 
	 * @param cls the class to add
	 * @see #addMessageType(Class, String)
	 */
	public void addMessageType(final Class<? extends Message> cls) {
	}
	
	@Override
	public Message deserialize(final JsonElement json, final Type typeOfT, final JsonDeserializationContext context)
			throws JsonParseException {
		return null;
	}

	@Override
	public JsonElement serialize(final Message src, final Type typeOfSrc, final JsonSerializationContext context) {
		return null;
	}
}
