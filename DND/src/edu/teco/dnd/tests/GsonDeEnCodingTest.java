package edu.teco.dnd.tests;

import java.io.Serializable;
import java.lang.reflect.Modifier;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import edu.teco.dnd.blocks.FunctionBlock;
import edu.teco.dnd.meeting.BeamerOperatorBlock;
import edu.teco.dnd.module.messages.ModuleMessageAdapter;
import edu.teco.dnd.module.messages.infoReq.ApplicationListResponse;
import edu.teco.dnd.module.messages.infoReq.ModuleInfoMessage;
import edu.teco.dnd.module.messages.infoReq.RequestApplicationListMessage;
import edu.teco.dnd.module.messages.infoReq.RequestModuleInfoMessage;
import edu.teco.dnd.module.messages.joinStartApp.JoinApplicationAck;
import edu.teco.dnd.module.messages.joinStartApp.JoinApplicationMessage;
import edu.teco.dnd.module.messages.joinStartApp.JoinApplicationNak;
import edu.teco.dnd.module.messages.joinStartApp.StartApplicationMessage;
import edu.teco.dnd.module.messages.killApp.KillAppAck;
import edu.teco.dnd.module.messages.killApp.KillAppMessage;
import edu.teco.dnd.module.messages.killApp.KillAppNak;
import edu.teco.dnd.module.messages.loadStartBlock.BlockAck;
import edu.teco.dnd.module.messages.loadStartBlock.BlockMessage;
import edu.teco.dnd.module.messages.loadStartBlock.BlockNak;
import edu.teco.dnd.module.messages.loadStartBlock.LoadClassAck;
import edu.teco.dnd.module.messages.loadStartBlock.LoadClassMessage;
import edu.teco.dnd.module.messages.loadStartBlock.LoadClassNak;
import edu.teco.dnd.module.messages.values.AppBlockIdFoundMessage;
import edu.teco.dnd.module.messages.values.BlockFoundMessage;
import edu.teco.dnd.module.messages.values.ValueAck;
import edu.teco.dnd.module.messages.values.ValueMessage;
import edu.teco.dnd.module.messages.values.ValueNak;
import edu.teco.dnd.module.messages.values.WhoHasBlockMessage;
import edu.teco.dnd.network.messages.Message;
import edu.teco.dnd.util.Base64Adapter;
import edu.teco.dnd.util.InetSocketAddressAdapter;
import edu.teco.dnd.util.NetConnection;
import edu.teco.dnd.util.NetConnectionAdapter;

public class GsonDeEnCodingTest {
	private static final Logger LOGGER = LogManager.getLogger(GsonDeEnCodingTest.class);

	private final static UUID TEST_MODULE_UUID = UUID.fromString("00000000-9abc-def0-1234-56789abcdef0");
	private final static UUID TEST_APP_UUID = UUID.fromString("11111111-9abc-def0-1234-56789abcdef0");
	private final static UUID TEST_FUNBLOCK_UUID = UUID.fromString("99999999-9abc-def0-1234-56789abcdef0");

	private static final Gson gson;
	private static final ModuleMessageAdapter msgAdapter = new ModuleMessageAdapter();
	static {
		GsonBuilder builder = new GsonBuilder();
		builder.excludeFieldsWithModifiers(Modifier.TRANSIENT);
		builder.setPrettyPrinting();
		builder.registerTypeAdapter(Message.class, msgAdapter);
		builder.registerTypeAdapter(InetSocketAddress.class, new InetSocketAddressAdapter());
		builder.registerTypeAdapter(NetConnection.class, new NetConnectionAdapter());
		builder.registerTypeAdapter(byte[].class, new Base64Adapter());
		gson = builder.create();
	}

	public static void main(String[] args) throws Exception {
		Collection<Message> testMsgs = new LinkedList<Message>();
		addTestMessages(testMsgs);

		for (Message msg : testMsgs) {
			testEnDeCoding(msg);
		}
	}

	private static void addTestMessages(Collection<Message> testMsgs) throws SecurityException {
		// FIXME: We really need a test framework!
		// TODO overwrite equals/toString of Messages properly.
		{
			// ApplicationListResponse
			Map<UUID, String> modIds = new TreeMap<UUID, String>();
			modIds.put(TEST_APP_UUID, "APP_1");
			testMsgs.add(new ApplicationListResponse(TEST_MODULE_UUID, modIds));
			msgAdapter.addMessageType(ApplicationListResponse.class);

			// RequestApplicationListMessage
			testMsgs.add(new RequestApplicationListMessage());
			msgAdapter.addMessageType(RequestApplicationListMessage.class);

			// ModuleInfoMessage
			try {
				testMsgs.add(new ModuleInfoMessage(TestConfigReader.getPredefinedReader()));
			} catch (SocketException e) {
				e.printStackTrace();
				throw new Error(e);
			}
			msgAdapter.addMessageType(ModuleInfoMessage.class);

			// RequestModuleInfoMessage
			testMsgs.add(new RequestModuleInfoMessage());
			msgAdapter.addMessageType(RequestModuleInfoMessage.class);
		}
		{
			// JoinApplicationMessage
			JoinApplicationMessage jam = new JoinApplicationMessage("appName", TEST_APP_UUID);
			testMsgs.add(jam);
			msgAdapter.addMessageType(JoinApplicationMessage.class);

			// JoinApplicationAck
			testMsgs.add(new JoinApplicationAck(jam));
			msgAdapter.addMessageType(JoinApplicationAck.class);

			// JoinApplicationNak
			testMsgs.add(new JoinApplicationNak(jam));
			msgAdapter.addMessageType(JoinApplicationNak.class);

			// StartApplicationMessage
			testMsgs.add(new StartApplicationMessage(TEST_APP_UUID));
			msgAdapter.addMessageType(StartApplicationMessage.class);

		}
		{
			KillAppMessage kam = new KillAppMessage(TEST_APP_UUID);
			// KillAppMessage
			testMsgs.add(kam);
			msgAdapter.addMessageType(KillAppMessage.class);

			// KillAppAck
			testMsgs.add(new KillAppAck(kam));
			msgAdapter.addMessageType(KillAppAck.class);

			// KillAppNak
			testMsgs.add(new KillAppNak(kam));
			msgAdapter.addMessageType(KillAppNak.class);
		}
		{
			// BlockAck
			testMsgs.add(new BlockAck("ClassName", TEST_APP_UUID));
			msgAdapter.addMessageType(BlockAck.class);

			// BlockMessage
			testMsgs.add(new BlockMessage("ClassName", TEST_APP_UUID, new BeamerOperatorBlock(TEST_FUNBLOCK_UUID)));
			msgAdapter.addMessageType(BlockMessage.class);

			// BlockNak
			testMsgs.add(new BlockNak("ClassName", TEST_APP_UUID));
			msgAdapter.addMessageType(BlockNak.class);

			// LoadClassAck
			testMsgs.add(new LoadClassAck("ClassName", TEST_APP_UUID));
			msgAdapter.addMessageType(LoadClassAck.class);

			// LoadClassMessage
			byte[] b = "Hello testing world".getBytes();
			testMsgs.add(new LoadClassMessage("ClassName", b, TEST_APP_UUID));
			msgAdapter.addMessageType(LoadClassMessage.class);

			// LoadClassNak
			testMsgs.add(new LoadClassNak("ClassName", TEST_APP_UUID));
			msgAdapter.addMessageType(LoadClassNak.class);

		}
		{
			// AppBlockIdFoundMessage
			testMsgs.add(new AppBlockIdFoundMessage(TEST_APP_UUID, TEST_MODULE_UUID, TEST_FUNBLOCK_UUID));
			msgAdapter.addMessageType(AppBlockIdFoundMessage.class);

			// BlockFoundMessage
			testMsgs.add(new BlockFoundMessage(TEST_APP_UUID, TEST_MODULE_UUID, TEST_FUNBLOCK_UUID));
			msgAdapter.addMessageType(BlockFoundMessage.class);

			// ValueAck
			testMsgs.add(new ValueAck(TEST_APP_UUID));
			msgAdapter.addMessageType(ValueAck.class);

			// ValueMessage
			@SuppressWarnings({ "unused", "serial" })
			class Seri implements Serializable {
				int a = 42;
				Long l = 12L;
				FunctionBlock con = new BeamerOperatorBlock(TEST_FUNBLOCK_UUID);
			}
			testMsgs.add(new ValueMessage(TEST_APP_UUID, TEST_FUNBLOCK_UUID, "InputName", new Seri()));
			msgAdapter.addMessageType(ValueMessage.class);

			// ValueNak
			testMsgs.add(new ValueNak(TEST_APP_UUID, ValueNak.ErrorType.WRONG_MODULE, TEST_FUNBLOCK_UUID, "InputName"));
			msgAdapter.addMessageType(ValueNak.class);

			// WhoHasBlockMessage
			testMsgs.add(new WhoHasBlockMessage(TEST_APP_UUID, TEST_FUNBLOCK_UUID));
			msgAdapter.addMessageType(WhoHasBlockMessage.class);

		}

	}

	public static void testEnDeCoding(Message msg) {
		String gsonHolder;
		Message decodedMsg;

		LOGGER.info(msg.getClass().toString());
		try {
			gsonHolder = gson.toJson(msg);
		} catch (Exception ex) {
			LOGGER.fatal("Encoding Error in MSG: {} .\n!!!FAIL: {}", msg, msg.getClass());
			LOGGER.trace(ex);
			return; // throw new Error(ex);
		}
		LOGGER.info("Gson is:\n--\n{}\n--", gsonHolder);
		try {
			decodedMsg = gson.fromJson(gsonHolder, Message.class);
		} catch (Exception ex) {
			LOGGER.fatal("{}\nDecoding Error.Encoded Gson: \n\n{}\n\n!!!FAIL: {}", msg, gsonHolder, msg.getClass());
			LOGGER.trace(ex);
			return;// throw new Error(ex);
		}

		if (!msg.equals(decodedMsg)) {
			LOGGER.fatal("Decoded {} wrong.(Or messages equal methode is broken)\nWas decoded to: {}\n!!!FAIL: {}",
					msg, decodedMsg, msg.getClass());
		} else {
			LOGGER.info("Succefull de/encoded msg: {}\n####################", msg);

		}

	}
}
