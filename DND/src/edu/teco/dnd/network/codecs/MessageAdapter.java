package edu.teco.dnd.network.codecs;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

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

import edu.teco.dnd.network.messages.Message;

/**
 * A Gson adapter that adds a type field to Message objects when serializing and uses the type field to select the right
 * class when deserializing.
 * 
 * @author Philipp Adolf
 */
public class MessageAdapter implements JsonSerializer<Message>, JsonDeserializer<Message> {
	/**
	 * The logger for this class.
	 */
	private static final Logger LOGGER = LogManager.getLogger(MessageAdapter.class);

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
	private final String typeFieldName;

	/**
	 * Maps from type name to actual class.
	 */
	private final Map<String, Class<? extends Message>> types = new HashMap<String, Class<? extends Message>>();

	/**
	 * Maps from class to type name.
	 */
	private final Map<Class<? extends Message>, String> clss = new HashMap<Class<? extends Message>, String>();

	/**
	 * Lock for reading and writing {@link #types} and {@link #clss}.
	 */
	private final ReadWriteLock typeLock = new ReentrantReadWriteLock();

	/**
	 * Creates a new MessageAdapter.
	 * 
	 * @param typeFieldName
	 *            the type name to use
	 */
	public MessageAdapter(final String typeFieldName) {
		LOGGER.entry(typeFieldName);
		this.typeFieldName = typeFieldName;
		LOGGER.exit();
	}

	/**
	 * Creates a new MessageAdapter using the default type field name.
	 * 
	 * @see #DEFAULT_TYPE_FIELD_NAME
	 */
	public MessageAdapter() {
		this(DEFAULT_TYPE_FIELD_NAME);
	}

	/**
	 * Adds a type of Message. If either the class or the type name are already in use, nothing is done.
	 * 
	 * @param cls
	 *            the class to add
	 * @param type
	 *            the name to use when (de-)serializing this class
	 */
	public void addMessageType(final Class<? extends Message> cls, final String type) {
		LOGGER.entry(cls, type);
		typeLock.readLock().lock();
		try {
			if (clss.containsKey(cls)) {
				LOGGER.warn("class {} already registered", cls);
				LOGGER.exit();
				return;
			}
			if (types.containsKey(type)) {
				LOGGER.warn("type {} already registered", type);
				LOGGER.exit();
				return;
			}
		} finally {
			typeLock.readLock().unlock();
		}

		LOGGER.debug("{} and {} not found, getting write lock", cls, type);

		typeLock.writeLock().lock();
		try {
			if (clss.containsKey(cls)) {
				LOGGER.warn("class {} already registered", cls);
				LOGGER.exit();
				return;
			}
			if (types.containsKey(type)) {
				LOGGER.warn("type {} already registered", type);
				LOGGER.exit();
				return;
			}
			LOGGER.debug("got write lock, {}  and {} still missing", cls, type);
			clss.put(cls, type);
			types.put(type, cls);
		} finally {
			typeLock.writeLock().unlock();
		}
		LOGGER.exit();
	}

	/**
	 * Adds a type of Message. The attribute named {@value #TYPE_ATTRIBUTE_NAME} is used to determine the type name. If
	 * either the class or the type name are already in use, nothing is done. Throws {@link IllegalArgumentException} if
	 * the type field attribute could not be retrieved or is not a String.
	 * 
	 * @param cls
	 *            the class to add
	 * @see #addMessageType(Class, String)
	 */
	public void addMessageType(final Class<? extends Message> cls) {
		LOGGER.entry(cls);
		String type = null;
		try {
			final Object obj = cls.getDeclaredField(TYPE_ATTRIBUTE_NAME).get(null);
			if (obj == null || !String.class.isAssignableFrom(obj.getClass())) {
				throw new IllegalArgumentException("type field is null or not a String");
			}
			type = (String) obj;
		} catch (final SecurityException e) {
			final IllegalArgumentException iae = new IllegalArgumentException("could not get type", e);
			LOGGER.exit(iae);
			throw iae;
		} catch (final IllegalAccessException e) {
			final IllegalArgumentException iae = new IllegalArgumentException("could not get type", e);
			LOGGER.exit(iae);
			throw iae;
		} catch (final NoSuchFieldException e) {
			final IllegalArgumentException iae = new IllegalArgumentException("could not get type", e);
			LOGGER.exit(iae);
			throw iae;
		}
		addMessageType(cls, type);
		LOGGER.exit();
	}

	@Override
	public Message deserialize(final JsonElement json, final Type typeOfT, final JsonDeserializationContext context)
			throws JsonParseException {
		LOGGER.entry(json, typeOfT, context);
		if (!(json instanceof JsonObject)) {
			LOGGER.exit();
			throw new JsonParseException("not an object");
		}
		final JsonElement typeElement = ((JsonObject) json).get(typeFieldName);
		if (typeElement == null) {
			LOGGER.exit();
			throw new JsonParseException("no type field");
		}
		if (!(typeElement instanceof JsonPrimitive) || !((JsonPrimitive) typeElement).isString()) {
			LOGGER.exit();
			throw new JsonParseException("type is not a String");
		}
		final String type = ((JsonPrimitive) typeElement).getAsString();
		typeLock.readLock().lock();
		final Class<? extends Message> cls = types.get(type);
		typeLock.readLock().unlock();
		if (cls == null) {
			LOGGER.warn("received message of type '{}', but the type is not registered", type);
			LOGGER.exit(null);
			return null;
		}
		final Message message = context.deserialize(json, cls);
		LOGGER.exit(message);
		return message;
	}

	@Override
	public JsonElement serialize(final Message src, final Type typeOfSrc, final JsonSerializationContext context) {
		LOGGER.entry(src, typeOfSrc, context);
		typeLock.readLock().lock();
		final String type = clss.get(src.getClass());
		typeLock.readLock().unlock();
		if (type == null) {
			if (LOGGER.isWarnEnabled()) {
				LOGGER.warn("{} has class {} which was not registered", src, src.getClass());
			}
			LOGGER.exit(null);
			return null;
		}
		final JsonElement element = context.serialize(src);
		if (!(element instanceof JsonObject)) {
			LOGGER.warn("got {} for {}, which is not a JsonObject", element, src);
			LOGGER.exit(null);
			return null;
		}
		final JsonObject object = (JsonObject) element;
		object.addProperty(typeFieldName, type);
		LOGGER.exit(object);
		return object;
	}
}
