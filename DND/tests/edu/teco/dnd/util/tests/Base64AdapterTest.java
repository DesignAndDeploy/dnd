package edu.teco.dnd.util.tests;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;

import edu.teco.dnd.util.Base64Adapter;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class Base64AdapterTest {
	private Base64Adapter adapter;
	
	private final byte[] data;
	
	private final JsonPrimitive base64;
	
	public Base64AdapterTest(final byte[] data, final JsonPrimitive base64) {
		this.data = data;
		this.base64 = base64;
	}
	
	@Parameters
	public static Collection<Object[]> parameters() {
		final Collection<Object[]> parameters = new ArrayList<Object[]>();
		parameters.add(new Object[] {
				new byte[0],
				new JsonPrimitive("")
			});
		parameters.add(new Object[] {
				// "Test"
				new byte[] { 0x54, 0x65, 0x73, 0x74 },
				new JsonPrimitive("VGVzdA==")
			});
		parameters.add(new Object[] {
				// "Foobar"
				new byte[] { 0x46, 0x6f, 0x6f, 0x62, 0x61, 0x72 },
				new JsonPrimitive("Rm9vYmFy")
			});
		return parameters;
	}
	
	@Before
	public void createAdapter() {
		this.adapter = new Base64Adapter();
	}
	
	@Test
	public void testSerialize() {
		assertEquals(base64, adapter.serialize(data, byte[].class, mock(JsonSerializationContext.class)));
	}
	
	@Test
	public void testDeserialize() {
		assertArrayEquals(data, adapter.deserialize(base64, byte[].class, mock(JsonDeserializationContext.class)));
	}
}
