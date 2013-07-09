package edu.teco.dnd.tests;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.LinkedList;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import edu.teco.dnd.module.messages.killApp.KillAppAck;
import edu.teco.dnd.module.messages.killApp.KillAppMessage;
import edu.teco.dnd.network.codecs.MessageAdapter;
import edu.teco.dnd.network.messages.Message;
import edu.teco.dnd.util.Base64Adapter;
import edu.teco.dnd.util.InetSocketAddressAdapter;
import edu.teco.dnd.util.NetConnection;
import edu.teco.dnd.util.NetConnectionAdapter;

public class GsonDeEnCodingTest {

	private final static UUID TEST_UUID = UUID.randomUUID();
	private static final Logger LOGGER = LogManager.getLogger(GsonDeEnCodingTest.class);
	private static final Gson gson;
	private static final MessageAdapter msgAdapter = new MessageAdapter();
	static {
		GsonBuilder builder = new GsonBuilder();
		builder.setPrettyPrinting();
		builder.registerTypeAdapter(Message.class, msgAdapter);
		// builder.registerTypeAdapter(KillAppMessage.class, msgAdapter);
		builder.registerTypeAdapter(InetSocketAddress.class, new InetSocketAddressAdapter());
		builder.registerTypeAdapter(NetConnection.class, new NetConnectionAdapter());
		builder.registerTypeAdapter(byte[].class, new Base64Adapter());
		gson = builder.create();
	}

	public static void main(String[] args) throws Exception {
		Collection<Message> testMsgs = new LinkedList<Message>();
		testMsgs.add(new KillAppMessage(TEST_UUID));
		msgAdapter.addMessageType(KillAppMessage.class);

		testMsgs.add(new KillAppAck(new KillAppMessage(TEST_UUID)));

		for (Message msg : testMsgs) {
			testEnDeCoding(msg);
		}
	}

	public static void testEnDeCoding(Message msg) {
		String gsonHolder;
		Message decodedMsg;

		try {
			gsonHolder = gson.toJson(msg);
		} catch (Exception ex) {
			LOGGER.fatal(msg);
			LOGGER.fatal("Encoding Error.");
			LOGGER.throwing(ex);
			throw new Error(ex);
		}

		try {
			decodedMsg = gson.fromJson(gsonHolder, Message.class);
		} catch (Exception ex) {
			LOGGER.fatal(msg + "\nDecoding Error.Encoded Gson: \n\n" + gsonHolder + "\n\n");
			LOGGER.throwing(ex);
			throw new Error(ex);
		}

		if (!msg.equals(decodedMsg)) {
			LOGGER.fatal("Decoded " + msg + " wrong.(Or messages equal methode is broken)\nWas decoded to: "
					+ decodedMsg);
		} else {
			LOGGER.info("Succefull de/encoded msg: " + msg);
		}

	}
}
