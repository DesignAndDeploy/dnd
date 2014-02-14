package edu.teco.dnd.network.tcp.tests;

import static edu.teco.dnd.tests.AfterMatcher.after;
import static edu.teco.dnd.tests.ContainsInOrder.containsInOrder;
import static edu.teco.dnd.tests.HasItemThat.hasItemThat;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.isA;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.junit.Assume.assumeThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.concurrent.EventExecutorGroup;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import edu.teco.dnd.module.ModuleID;
import edu.teco.dnd.network.messages.HelloMessage;
import edu.teco.dnd.network.tcp.ClientChannelInitializer;
import edu.teco.dnd.network.tcp.ClientChannelManager;
import edu.teco.dnd.network.tcp.GsonCodec;

@RunWith(MockitoJUnitRunner.class)
public class ClientChannelInitializerTest {
	@Mock
	private Channel channel;
	@Mock
	private ClientChannelManager manager;
	@Mock
	private ChannelHandler messageHandler;
	@Mock
	private EventExecutorGroup messageHandlerGroup;

	private ModuleID localID;
	private ClientChannelInitializer initializer;

	private MockPipeline pipeline = new MockPipeline();

	@Before
	public void setup() {
		localID = new ModuleID();
		initializer = new ClientChannelInitializer(manager, localID);

		when(channel.pipeline()).thenReturn(pipeline);
	}

	@Test
	public void testChannelAddedWithoutMessageHandler() {
		initializer.initChannel(channel);

		verify(manager).addChannel(channel);
	}

	@Test
	public void testPipelineContainsLengthFieldPrepender() {
		initializer.initChannel(channel);

		assertThat(pipeline.handler(), hasItemThat(isA(LengthFieldPrepender.class)));
	}

	@Test
	public void testPipelineContainsLengthFieldBasedFrameDecoder() {
		initializer.initChannel(channel);

		assertThat(pipeline.handler(), hasItemThat(isA(LengthFieldBasedFrameDecoder.class)));
	}

	@Test
	public void testPipelineContainsStringEncoder() {
		initializer.initChannel(channel);

		assertThat(pipeline.handler(), hasItemThat(isA(StringEncoder.class)));
	}

	@Test
	public void testPipelineContainsStringDecoder() {
		initializer.initChannel(channel);

		assertThat(pipeline.handler(), hasItemThat(isA(StringDecoder.class)));
	}

	@Test
	public void testPipelineContainsGsonCodec() {
		initializer.initChannel(channel);

		assertThat(pipeline.handler(), hasItemThat(isA(GsonCodec.class)));
	}

	@Test
	public void testIncomingPipelineOder() {
		initializer.initChannel(channel);

		assumeThat(
				pipeline.handler(),
				allOf(hasItemThat(isA(LengthFieldPrepender.class)), hasItemThat(isA(StringEncoder.class)),
						hasItemThat(isA(GsonCodec.class))));
		assertThat(
				pipeline.handler(),
				containsInOrder(pipeline.get(LengthFieldPrepender.class), pipeline.get(StringEncoder.class),
						pipeline.get(GsonCodec.class)));
	}

	@Test
	public void testOutgoingPipelineOder() {
		initializer.initChannel(channel);

		assumeThat(
				pipeline.handler(),
				allOf(hasItemThat(isA(LengthFieldBasedFrameDecoder.class)), hasItemThat(isA(StringDecoder.class)),
						hasItemThat(isA(GsonCodec.class))));
		assertThat(
				pipeline.handler(),
				containsInOrder(pipeline.get(LengthFieldBasedFrameDecoder.class), pipeline.get(StringDecoder.class),
						pipeline.get(GsonCodec.class)));
	}

	@Test
	public void testMessageHandlerWithoutGroup() {
		initializer.setMessageHandler(messageHandler);

		initializer.initChannel(channel);

		assertThat(pipeline.handler(), hasItemThat(is(equalTo(messageHandler))));
		assertNull(pipeline.getGroup(messageHandler));
	}

	@Test
	public void testMessageHandlerWithGroup() {
		initializer.setMessageHandler(messageHandler);
		initializer.setHandlerGroup(messageHandlerGroup);

		initializer.initChannel(channel);

		assertThat(pipeline.handler(), hasItemThat(is(equalTo(messageHandler))));
		assertEquals(messageHandlerGroup, pipeline.getGroup(messageHandler));
	}

	@Test
	public void testMessageHandlerPositionWithoutGroup() {
		initializer.setMessageHandler(messageHandler);

		initializer.initChannel(channel);

		assumeThat(pipeline.handler(), hasItemThat(is(equalTo(messageHandler))));
		assumeThat(pipeline.handler(), hasItemThat(isA(GsonCodec.class)));
		assertThat(messageHandler, is(after(pipeline.get(GsonCodec.class)).in(pipeline.handler())));
	}

	@Test
	public void testMessageHandlerPositionWithGroup() {
		initializer.setMessageHandler(messageHandler);
		initializer.setHandlerGroup(messageHandlerGroup);

		initializer.initChannel(channel);

		assumeThat(pipeline.handler(), hasItemThat(is(equalTo(messageHandler))));
		assumeThat(pipeline.handler(), hasItemThat(isA(GsonCodec.class)));
		assertThat(messageHandler, is(after(pipeline.get(GsonCodec.class)).in(pipeline.handler())));
	}

	@Test
	public void testFirstMessage() {
		initializer.initChannel(channel);

		final ArgumentCaptor<Object> captor = ArgumentCaptor.forClass(Object.class);
		verify(channel).writeAndFlush(captor.capture());
		final Object sentObject = captor.getValue();
		if (sentObject == null) {
			fail("sent null");
		}
		if (!(sentObject instanceof HelloMessage)) {
			fail("sent " + sentObject + " of class " + sentObject.getClass() + " instead of HelloMessage");
		}
		final HelloMessage msg = (HelloMessage) sentObject;
		assertEquals(localID, msg.getModuleID());
	}
}
