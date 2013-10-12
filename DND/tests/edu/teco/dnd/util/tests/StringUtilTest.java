package edu.teco.dnd.util.tests;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import edu.teco.dnd.util.StringUtil;

@RunWith(Parameterized.class)
public class StringUtilTest {
	private final Class<? extends Object[]> arrayClass;

	private final Object[] array;

	private final String separator;

	private final String expected;

	public StringUtilTest(final Class<? extends Object[]> arrayClass, final Object[] array, final String separator,
			final String expected) {
		this.arrayClass = arrayClass;
		this.array = array;
		this.separator = separator;
		this.expected = expected;
	}

	@Parameters
	public static Collection<Object[]> data() {
		final Collection<Object[]> parameters = new ArrayList<Object[]>();
		parameters.add(new Object[] { String[].class, new String[] { "foo", "bar" }, ":", "foo:bar" });
		parameters.add(new Object[] { String[].class, new String[] { "foo" }, ":", "foo" });
		parameters.add(new Object[] { Integer[].class, new Integer[] { 1, 2, 3, 4 }, ",", "1,2,3,4" });
		parameters.add(new Object[] { Integer[].class, null, ":", null });
		parameters.add(new Object[] { Boolean[].class, new Boolean[] { true, true, false }, "", "truetruefalse" });
		parameters.add(new Object[] { Boolean[].class, new Boolean[] { true, true, false }, null, "truetruefalse" });
		parameters.add(new Object[] { Boolean[].class, new Boolean[] { true }, ":", "true" });
		parameters.add(new Object[] { Boolean[].class, new Boolean[] { true }, "", "true" });
		parameters.add(new Object[] { Boolean[].class, new Boolean[] { true }, null, "true" });
		parameters.add(new Object[] { Boolean[].class, new Boolean[] {}, ":", "" });
		parameters.add(new Object[] { Boolean[].class, new Boolean[] {}, "", "" });
		parameters.add(new Object[] { Boolean[].class, new Boolean[] {}, null, "" });
		return parameters;
	}

	@Test
	public void joinArrayTest() {
		assertEquals(expected, StringUtil.joinArray(arrayClass.cast(array), separator));
	}
}
