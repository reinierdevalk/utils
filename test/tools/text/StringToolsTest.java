package tools.text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;

public class StringToolsTest extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public void testParseStringifiedListOfIntegers() {
		List<String> str = Arrays.asList(
			"[1, 2, 3]", 
			"[4,5,6]", 
			"[7 ,8 , 9]",
			"[]",
			"[[1, 2, 3], [4,5,6], [7 ,8 , 9], []]"
		);

		List<List<List<Integer>>> expected = new ArrayList<>();
		expected.add(Arrays.asList(
			Arrays.asList(1, 2, 3)
		));
		expected.add(Arrays.asList(
			Arrays.asList(4, 5, 6)
		));
		expected.add(Arrays.asList(
			Arrays.asList(7, 8, 9)
		));
		expected.add(Arrays.asList(
			Arrays.asList()
		));
		expected.add(Arrays.asList(
			Arrays.asList(1, 2, 3),
			Arrays.asList(4, 5, 6),
			Arrays.asList(7, 8, 9),
			Arrays.asList()
		));

		List<List<List<Integer>>> actual = new ArrayList<>();
		for (String s : str) {
			actual.add(StringTools.parseStringifiedListOfIntegers(s));
		}

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i).size(), actual.get(i).size());
			for (int j = 0; j < expected.get(i).size(); j++) {
				assertEquals(expected.get(i).get(j), actual.get(i).get(j));
			}
		}
		assertEquals(expected, actual);
	}

}
