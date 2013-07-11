package edu.teco.dnd.module.messages;

import java.lang.reflect.Field;
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

import edu.teco.dnd.network.messages.Message;

/**
 * A Gson adapter that uses the $TYPE_ATTRIBUTE_NAME field to determine which subclass of Message to deserialize the
 * given Json object to.
 * 
 * 
 * A Message this adapter is to be used for MUST have a "static String {@value #TYPE_ATTRIBUTE_NAME}
 * " variable (e.g. 'private static String MESSAGE_TYPE = "kill" ' ) <br>
 * The used Gson decoder MUST be initialized with "gsonBuilder.excludeFieldsWithModifiers(Modifier.TRANSIENT);" or
 * similar, as long as "static" is not excludedField <br>
 * Every message type MUST be registered before decoding it. <br>
 * The type adapter SHOULD be registered as handling Message.class.
 * 
 * @author Marvin Marx, Philipp Adolf
 */
public class ModuleMessageAdapter implements JsonDeserializer<Message> {
	/**
	 * The logger for this class.
	 */
	private static final Logger LOGGER = LogManager.getLogger(ModuleMessageAdapter.class);

	/**
	 * The name of the attribute that will be read if {@link #addMessageType(Class)} is used.
	 */
	public static final String TYPE_ATTRIBUTE_NAME = "MESSAGE_TYPE";

	/**
	 * Maps from type name to actual class.
	 */
	private final Map<String, Class<? extends Message>> types = new HashMap<String, Class<? extends Message>>();

	/**
	 * Lock for reading and writing {@link #types}.
	 */
	private final ReadWriteLock typeLock = new ReentrantReadWriteLock();

	/**
	 * Creates a new MessageAdapter.
	 * 
	 */
	public ModuleMessageAdapter() {
	}

	/**
	 * Adds a type of Message. If either the class or the type name are already in use, nothing is done.
	 * 
	 * @param cls
	 *            the class to add
	 * @param type
	 *            the value of {@value #TYPE_ATTRIBUTE_NAME} the class is recognized by during deserializing.
	 */
	public void addMessageType(final Class<? extends Message> cls, final String type) {
		LOGGER.entry(cls, type);
		typeLock.readLock().lock();
		if (!types.containsKey(type)) {
			LOGGER.debug("{} not found, getting write lock", type);
			typeLock.readLock().unlock();
			typeLock.writeLock().lock();
			if (!types.containsKey(type)) {
				LOGGER.debug("got write lock, {} still missing", type);
				types.put(type, cls);
			} else {
				LOGGER.debug("got write lock, but {} is already registered", type);
			}
			typeLock.readLock().lock();
			typeLock.writeLock().unlock();
		}
		typeLock.readLock().unlock();
		LOGGER.exit();
	}

	/**
	 * Adds a type of Message. The attribute named {@value #TYPE_ATTRIBUTE_NAME} is used to determine the type name. If
	 * the type name of the class is already in use, nothing is done. Throws {@link IllegalArgumentException} if the
	 * type field attribute could not be retrieved or is not a String.
	 * 
	 * @param cls
	 *            the class to add
	 * @see #addMessageType(Class, String)
	 */
	public void addMessageType(final Class<? extends Message> cls) {
		LOGGER.entry(cls);
		String type = null;
		try {
			Field f = cls.getDeclaredField(TYPE_ATTRIBUTE_NAME);
			f.setAccessible(true);
			final Object obj = f.get(null);
			if (obj == null || !String.class.isAssignableFrom(obj.getClass())) {
				throw new IllegalArgumentException("type field is null or not a String");
			}
			type = (String) obj;
		} catch (final SecurityException e) {
			final IllegalArgumentException iae = new IllegalArgumentException("could not get " + TYPE_ATTRIBUTE_NAME, e);
			LOGGER.exit(iae);
			throw iae;
		} catch (final IllegalAccessException e) {
			final IllegalArgumentException iae = new IllegalArgumentException(
					"could not access " + TYPE_ATTRIBUTE_NAME, e);
			LOGGER.exit(iae);
			throw iae;
		} catch (final NoSuchFieldException e) {
			final IllegalArgumentException iae = new IllegalArgumentException("field " + TYPE_ATTRIBUTE_NAME
					+ "does not exist.", e);
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
		final JsonElement typeElement = ((JsonObject) json).get(TYPE_ATTRIBUTE_NAME);
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

}
