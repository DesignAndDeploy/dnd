package edu.teco.dnd.tests;

import java.net.InetSocketAddress;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.corba.se.impl.protocol.giopmsgheaders.Message;

import edu.teco.dnd.module.messages.killApp.KillAppMessage;
import edu.teco.dnd.network.messages.ApplicationSpecificMessage;
import edu.teco.dnd.util.Base64Adapter;
import edu.teco.dnd.util.InetSocketAddressAdapter;
import edu.teco.dnd.util.NetConnection;
import edu.teco.dnd.util.NetConnectionAdapter;

public class GsonDeEnCodingTest {

	private final static  UUID TEST_UUID = UUID.randomUUID(); 
	private static transient final Logger LOGGER = LogManager.getLogger(GsonDeEnCodingTest.class);
	private static transient final Gson gson;
	static {
		GsonBuilder builder = new GsonBuilder();
		builder.setPrettyPrinting();
		builder.registerTypeAdapter(InetSocketAddress.class, new InetSocketAddressAdapter());
		builder.registerTypeAdapter(NetConnection.class, new NetConnectionAdapter());
		builder.registerTypeAdapter(byte[].class, new Base64Adapter());
		gson = builder.create();
	}
	
	public static void main(String[] args) throws Exception {
		ApplicationSpecificMessage testMsg = new KillAppMessage(TEST_UUID);
		
		try {
		testEnDeCoding(testMsg);
		} catch (Exception ex) {
			LOGGER.fatal("Error while de/encoding msg: " + testMsg);
			throw ex;
		}
	}
	
	public static void testEnDeCoding(ApplicationSpecificMessage msg) {
		LOGGER.entry(msg);
		String gsonHolder = gson.toJson(msg);
		if(!msg.equals(gson.fromJson(gsonHolder, Message.class))) {
			LOGGER.fatal("Decoded " + msg + " wrong.(Or messages equal methode is broken)");
		} else {
			LOGGER.info("Succefull de/encoded msg: " + msg);
		}
		
	}

}
