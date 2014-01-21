package edu.teco.dnd.server;

import java.net.InetSocketAddress;

import edu.teco.dnd.module.messages.infoReq.ApplicationBlockID;
import edu.teco.dnd.module.messages.infoReq.ApplicationListResponse;
import edu.teco.dnd.module.messages.infoReq.BlockIDAdapter;
import edu.teco.dnd.module.messages.infoReq.ModuleInfoMessage;
import edu.teco.dnd.module.messages.infoReq.ModuleInfoMessageAdapter;
import edu.teco.dnd.module.messages.infoReq.RequestApplicationListMessage;
import edu.teco.dnd.module.messages.infoReq.RequestModuleInfoMessage;
import edu.teco.dnd.module.messages.joinStartApp.JoinApplicationAck;
import edu.teco.dnd.module.messages.joinStartApp.JoinApplicationMessage;
import edu.teco.dnd.module.messages.joinStartApp.JoinApplicationNak;
import edu.teco.dnd.module.messages.joinStartApp.StartApplicationAck;
import edu.teco.dnd.module.messages.joinStartApp.StartApplicationMessage;
import edu.teco.dnd.module.messages.joinStartApp.StartApplicationNak;
import edu.teco.dnd.module.messages.killApp.KillAppAck;
import edu.teco.dnd.module.messages.killApp.KillAppMessage;
import edu.teco.dnd.module.messages.killApp.KillAppNak;
import edu.teco.dnd.module.messages.loadStartBlock.BlockAck;
import edu.teco.dnd.module.messages.loadStartBlock.BlockMessage;
import edu.teco.dnd.module.messages.loadStartBlock.BlockNak;
import edu.teco.dnd.module.messages.loadStartBlock.LoadClassAck;
import edu.teco.dnd.module.messages.loadStartBlock.LoadClassMessage;
import edu.teco.dnd.module.messages.loadStartBlock.LoadClassNak;
import edu.teco.dnd.module.messages.values.BlockFoundResponse;
import edu.teco.dnd.module.messages.values.ValueAck;
import edu.teco.dnd.module.messages.values.ValueMessage;
import edu.teco.dnd.module.messages.values.ValueNak;
import edu.teco.dnd.module.messages.values.WhoHasBlockMessage;
import edu.teco.dnd.network.tcp.TCPConnectionManager;
import edu.teco.dnd.util.Base64Adapter;
import edu.teco.dnd.util.InetSocketAddressAdapter;
import edu.teco.dnd.util.NetConnection;
import edu.teco.dnd.util.NetConnectionAdapter;

/**
 * This class is used to initialize {@link TCPConnectionManager}s. It does so by registering needed type adapters and
 * message types.
 * 
 * @author Philipp Adolf
 */
public class TCPProtocol {
	public void initialize(final TCPConnectionManager tcpConnectionManager) {
		tcpConnectionManager.registerTypeAdapter(InetSocketAddress.class, new InetSocketAddressAdapter());
		tcpConnectionManager.registerTypeAdapter(NetConnection.class, new NetConnectionAdapter());
		tcpConnectionManager.registerTypeAdapter(byte[].class, new Base64Adapter());
		tcpConnectionManager.registerTypeAdapter(ModuleInfoMessage.class, new ModuleInfoMessageAdapter());
		tcpConnectionManager.registerTypeAdapter(ApplicationBlockID.class, new BlockIDAdapter());

		tcpConnectionManager.addMessageType(JoinApplicationMessage.class);
		tcpConnectionManager.addMessageType(JoinApplicationAck.class);
		tcpConnectionManager.addMessageType(JoinApplicationNak.class);
		tcpConnectionManager.addMessageType(ValueMessage.class);
		tcpConnectionManager.addMessageType(WhoHasBlockMessage.class);
		tcpConnectionManager.addMessageType(ValueNak.class);
		tcpConnectionManager.addMessageType(ValueAck.class);
		tcpConnectionManager.addMessageType(BlockFoundResponse.class);
		tcpConnectionManager.addMessageType(LoadClassNak.class);
		tcpConnectionManager.addMessageType(LoadClassMessage.class);
		tcpConnectionManager.addMessageType(LoadClassAck.class);
		tcpConnectionManager.addMessageType(BlockNak.class);
		tcpConnectionManager.addMessageType(BlockMessage.class);
		tcpConnectionManager.addMessageType(BlockAck.class);
		tcpConnectionManager.addMessageType(KillAppNak.class);
		tcpConnectionManager.addMessageType(KillAppAck.class);
		tcpConnectionManager.addMessageType(KillAppMessage.class);
		tcpConnectionManager.addMessageType(StartApplicationMessage.class);
		tcpConnectionManager.addMessageType(StartApplicationAck.class);
		tcpConnectionManager.addMessageType(StartApplicationNak.class);
		tcpConnectionManager.addMessageType(RequestModuleInfoMessage.class);
		tcpConnectionManager.addMessageType(RequestApplicationListMessage.class);
		tcpConnectionManager.addMessageType(ApplicationListResponse.class);
		tcpConnectionManager.addMessageType(ModuleInfoMessage.class);
	}
}
