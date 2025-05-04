package tools.text;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import conversion.exports.MEIExport;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StringToolsTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}


	@Test
	public void testParseStringifiedPythonList() {
		List<String> strs = Arrays.asList(
			// 1D
			"[]",
			"['a', 'b', 'c']",
			"['d','e','f']", 
			"['g' ,'h' , 'i' ]",
			// 2D
			"[[], []]",
			"[[\"a\", \"b\", \"c\"], [\"d\",\"e\",\"f\"], [\"g\" ,\"h\" , \"i\" ], []]" 
		);

		List<String> ints = Arrays.asList(
			// 1D
			"[]",
			"[1, 2, 3]", 
			"[4,5,6]", 
			"[7 ,8 , 9 ]",
			// 2D
			"[[], []]",
			"[[1, 2, 3], [4,5,6], [7 ,8 , 9 ], []]"
		);

		// Arrays
		// 1D
		List<String[]> expected1DArrStr = new ArrayList<>();
		expected1DArrStr.add(new String[0]);
		expected1DArrStr.add(new String[]{"a", "b", "c"});
		expected1DArrStr.add(new String[]{"d", "e", "f"});
		expected1DArrStr.add(new String[]{"g", "h", "i"});
		List<Integer[]> expected1DArrInt = new ArrayList<>();
		expected1DArrInt.add(new Integer[0]);
		expected1DArrInt.add(new Integer[]{1, 2, 3});
		expected1DArrInt.add(new Integer[]{4, 5, 6});
		expected1DArrInt.add(new Integer[]{7, 8, 9});
		// 2D
		List<String[][]> expected2DArrStr = new ArrayList<>();
		expected2DArrStr.add(new String[][]{new String[0], new String[0]});
		expected2DArrStr.add(new String[][]{{"a", "b", "c"}, {"d", "e", "f"}, {"g", "h", "i"}, new String[0]});
		List<Integer[][]> expected2DArrInt = new ArrayList<>();
		expected2DArrInt.add(new Integer[][]{new Integer[0], new Integer[0]});
		expected2DArrInt.add(new Integer[][]{{1, 2, 3}, {4, 5, 6}, {7, 8, 9}, new Integer[0]});
		// Lists
		// 1D
		List<List<String>> expected1DListStr = new ArrayList<>();
		expected1DListStr.add(new ArrayList<>());
		expected1DListStr.add(Arrays.asList("a", "b", "c"));
		expected1DListStr.add(Arrays.asList("d", "e", "f"));
		expected1DListStr.add(Arrays.asList("g", "h", "i"));
		List<List<Integer>> expected1DListInt = new ArrayList<>();
		expected1DListInt.add(new ArrayList<>());
		expected1DListInt.add(Arrays.asList(1, 2, 3));
		expected1DListInt.add(Arrays.asList(4, 5, 6));
		expected1DListInt.add(Arrays.asList(7, 8, 9));
		// 2D
		List<List<List<String>>> expected2DListStr = new ArrayList<>();
		expected2DListStr.add(Arrays.asList(new ArrayList<>(), new ArrayList<>()));
		expected2DListStr.add(Arrays.asList(Arrays.asList("a", "b", "c"), 
			Arrays.asList("d", "e", "f"), Arrays.asList("g", "h", "i"), new ArrayList<>()));
		List<List<List<Integer>>> expected2DListInt = new ArrayList<>();
		expected2DListInt.add(Arrays.asList(new ArrayList<>(), new ArrayList<>()));
		expected2DListInt.add(Arrays.asList(Arrays.asList(1, 2, 3), 
			Arrays.asList(4, 5, 6), Arrays.asList(7, 8, 9), new ArrayList<>()));

		// Arrays
		// 1D
		List<String[]> actual1DArrStr = new ArrayList<>();
		for (String s : strs.subList(0, 4)) {
			actual1DArrStr.add(
				(String[]) StringTools.parseStringifiedPythonList(s, "Array", "String")
			);
		}
		List<Integer[]> actual1DArrInt = new ArrayList<>();
		for (String s : ints.subList(0, 4)) {
			actual1DArrInt.add(
				(Integer[]) StringTools.parseStringifiedPythonList(s, "Array", "Integer")
			);
		}
		// 2D
		List<String[][]> actual2DArrStr = new ArrayList<>();
		for (String s : strs.subList(4, 6)) {
			actual2DArrStr.add(
				(String[][]) StringTools.parseStringifiedPythonList(s, "Array", "String")
			);
		}
		List<Integer[][]> actual2DArrInt = new ArrayList<>();
		for (String s : ints.subList(4, 6)) {
			actual2DArrInt.add(
				(Integer[][]) StringTools.parseStringifiedPythonList(s, "Array", "Integer")
			);
		}
		// Lists
		// 1D
		List<List<String>> actual1DListStr = new ArrayList<>();
		for (String s : strs.subList(0, 4)) {
			actual1DListStr.add(
				(List<String>) StringTools.parseStringifiedPythonList(s, "List", "String")	
			);
		}
		List<List<Integer>> actual1DListInt = new ArrayList<>();
		for (String s : ints.subList(0, 4)) {
			actual1DListInt.add(
				(List<Integer>) StringTools.parseStringifiedPythonList(s, "List", "Integer")	
			);
		}
		// 2D
		List<List<List<String>>> actual2DListStr = new ArrayList<>();
		for (String s : strs.subList(4, 6)) {
			actual2DListStr.add(
				(List<List<String>>) StringTools.parseStringifiedPythonList(s, "List", "String")	
			);
		}
		List<List<List<Integer>>> actual2DListInt = new ArrayList<>();
		for (String s : ints.subList(4, 6)) {
			actual2DListInt.add(
				(List<List<Integer>>) StringTools.parseStringifiedPythonList(s, "List", "Integer")	
			);
		}

		// Arrays
		// 1D
		assertEquals(expected1DArrStr.size(), actual1DArrStr.size());
		for (int i = 0; i < expected1DArrStr.size(); i++) {
	  		assertEquals(expected1DArrStr.get(i).length, actual1DArrStr.get(i).length);
	  		for (int j = 0; j < expected1DArrStr.get(i).length; j++) {
	  			assertEquals(expected1DArrStr.get(i)[j], actual1DArrStr.get(i)[j]);
	  		}
		}
		assertEquals(expected1DArrInt.size(), actual1DArrInt.size());
		for (int i = 0; i < expected1DArrInt.size(); i++) {
	  		assertEquals(expected1DArrInt.get(i).length, actual1DArrInt.get(i).length);
	  		for (int j = 0; j < expected1DArrInt.get(i).length; j++) {
	  			assertEquals(expected1DArrInt.get(i)[j], actual1DArrInt.get(i)[j]);
	  		}
		}
		// 2D
		assertEquals(expected2DArrStr.size(), actual2DArrStr.size());
		for (int i = 0; i < expected2DArrStr.size(); i++) {
			assertEquals(expected2DArrStr.get(i).length, actual2DArrStr.get(i).length);
			for (int j = 0; j < expected2DArrStr.get(i).length; j++) {
				assertEquals(expected2DArrStr.get(i)[j].length, actual2DArrStr.get(i)[j].length);
				for (int k = 0; k < expected2DArrStr.get(i)[j].length; k++) {
					assertEquals(expected2DArrStr.get(i)[j][k], actual2DArrStr.get(i)[j][k]);
				}
			}
		}
		assertEquals(expected2DArrInt.size(), actual2DArrInt.size());
		for (int i = 0; i < expected2DArrInt.size(); i++) {
			assertEquals(expected2DArrInt.get(i).length, actual2DArrInt.get(i).length);
			for (int j = 0; j < expected2DArrInt.get(i).length; j++) {
				assertEquals(expected2DArrInt.get(i)[j].length, actual2DArrInt.get(i)[j].length);
				for (int k = 0; k < expected2DArrInt.get(i)[j].length; k++) {
					assertEquals(expected2DArrInt.get(i)[j][k], actual2DArrInt.get(i)[j][k]);
				}
			}
		}
		// Lists
		// 1D
		assertEquals(expected1DListStr.size(), actual1DListStr.size());
		for (int i = 0; i < expected1DListStr.size(); i++) {
	  		assertEquals(expected1DListStr.get(i).size(), actual1DListStr.get(i).size());
	  		for (int j = 0; j < expected1DListStr.get(i).size(); j++) {
	  			assertEquals(expected1DListStr.get(i).get(j), actual1DListStr.get(i).get(j));
	  		}
		}
		assertEquals(expected1DListInt.size(), actual1DListInt.size());
		for (int i = 0; i < expected1DListInt.size(); i++) {
	  		assertEquals(expected1DListInt.get(i).size(), actual1DListInt.get(i).size());
	  		for (int j = 0; j < expected1DListInt.get(i).size(); j++) {
	  			assertEquals(expected1DListInt.get(i).get(j), actual1DListInt.get(i).get(j));
	  		}
		}
		// 2D
		assertEquals(expected2DListStr.size(), actual2DListStr.size());
		for (int i = 0; i < expected2DListStr.size(); i++) {
			assertEquals(expected2DListStr.get(i).size(), actual2DListStr.get(i).size());
			for (int j = 0; j < expected2DListStr.get(i).size(); j++) {
				assertEquals(expected2DListStr.get(i).get(j).size(), actual2DListStr.get(i).get(j).size());
				for (int k = 0; k < expected2DListStr.get(i).get(j).size(); k++) {
					assertEquals(expected2DListStr.get(i).get(j).get(k), actual2DListStr.get(i).get(j).get(k));
				}
			}
		}
		assertEquals(expected2DListInt.size(), actual2DListInt.size());
		for (int i = 0; i < expected2DListInt.size(); i++) {
			assertEquals(expected2DListInt.get(i).size(), actual2DListInt.get(i).size());
			for (int j = 0; j < expected2DListInt.get(i).size(); j++) {
				assertEquals(expected2DListInt.get(i).get(j).size(), actual2DListInt.get(i).get(j).size());
				for (int k = 0; k < expected2DListInt.get(i).get(j).size(); k++) {
					assertEquals(expected2DListInt.get(i).get(j).get(k), actual2DListInt.get(i).get(j).get(k));
				}
			}
		}
	}


	@Test
	public void testCrlf2lf() {
		String s = "a\r\nb\r\nc";

		String expected = "a\nb\nc";
		String actual = StringTools.crlf2lf(s);

		assertEquals(expected, actual);
	}


	@Test
	public void testRemoveExtensions() {
		List<String> expected = Arrays.asList("a", "b", "c", "d");
		List<String> actual = StringTools.removeExtensions(
			Arrays.asList("a.txt", "b.mei", "c.pdf", "d")
		);

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i), actual.get(i));
		}
		
	}
}
