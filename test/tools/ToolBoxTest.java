package tools;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import de.uos.fmt.musitech.utility.math.Rational;
import interfaces.CLInterface;
import internal.core.Encoding;
import tools.text.StringTools;


public class ToolBoxTest {
	
	private File encodingTestpiece;
	private double delta;
		
	@Before
	public void setUp() throws Exception {
		Map<String, String> paths = CLInterface.getPaths(true);
		String ep = paths.get("ENCODINGS_PATH");
		String td = "test/5vv/";

		encodingTestpiece = new File(StringTools.getPathString(
			Arrays.asList(ep, td)) + "testpiece.tbp"
		);
		delta = 1e-9;
	}

	@After
	public void tearDown() throws Exception {
	}


	@Test
	public void testGetFilename() {
//		File f = new File("F:/research/data/annotated/encodings/test/testpiece.tbp");

		List<String> expected = Arrays.asList(new String[]{"testpiece", "testpiece.tbp"});
		List<String> actual = new ArrayList<>();
		actual.add(ToolBox.getFilename(encodingTestpiece, Encoding.TBP_EXT));
		actual.add(ToolBox.getFilename(encodingTestpiece, null));

		assertEquals(expected, actual);
	}


	@Test
	public void testReadTextFile() {
//		File f = new File("F:/research/computation/data/annotated/voice_separation/encodings/test/testpiece.tbp");

		String expected = 
			"{AUTHOR: Author }" + "\r\n" + 
			"{TITLE:Title}" + "\r\n" + 
			"{SOURCE:Source (year)}" + "\r\n" +	"\r\n" + 
			"{TABSYMBOLSET:French}" + "\r\n" + 
			"{TUNING:A}" + "\r\n" + 
			"{METER_INFO:2/2 (1-3)}" + "\r\n" + 
			"{DIMINUTION:1}" + "\r\n" + "\r\n" +
			"{bar 1}" + "\r\n" +
			"MC\\.>.sb.>.mi.>.mi.a5.c4.b2.a1.>.|{@Footnote 1}." + "\r\n" +
			"{bar 2}" + "\r\n" +
			"sm*.a6.c4.i2.a1.>.fu.d6.>.sm.c6.a5.e4.b2.>.a6.>.mi.a6.h5.c4.b3.f2{@'mi.a6.' in source}.>.sm.a6.b3.a2.a1.>.a3.e2.>.|./" + "\r\n" + 
			"{bar 3}" + "\r\n" +
			"||.fu.a6.c4.a2.a1.>.e2.>.sf.a1.>.e2.>.|.c2.>.e2.>.mi.a1.>.mi.>.mi.a6.c4.a2.a1.>.||.//" + "\r\n";

		String actual = ToolBox.readTextFile(encodingTestpiece);
		
		assertEquals(expected, actual);
	}


	@Test
	public void testWeightedGeometricMean() {
		double one = Math.E; // ln = 1.0
		double two = Math.pow(Math.E, 2); // ln = 2.0
		double three = Math.pow(Math.E, 3); // ln = 3.0
		double four = Math.pow(Math.E, 4); // ln = 4.0
		
		List<List<Double>> numbers = new ArrayList<List<Double>>();
		numbers.add(Arrays.asList(new Double[]{one, two, two}));
		numbers.add(Arrays.asList(new Double[]{two, three, two}));
		numbers.add(Arrays.asList(new Double[]{three, four, two}));
		List<Double> weights = Arrays.asList(new Double[]{0.25, 0.5, 0.4});
		
		List<Double> expected = Arrays.asList(new Double[]{
			( Math.exp( (0.25*1 + 0.5*2 + 0.4*2) / (1.15) ) ),
			( Math.exp( (0.25*2 + 0.5*3 + 0.4*2) / (1.15) ) ),
			( Math.exp( (0.25*3 + 0.5*4 + 0.4*2) / (1.15) ) )
		});
		
		List<Double> actual = new ArrayList<Double>();
		for (List<Double> l : numbers) {
			actual.add(ToolBox.weightedGeometricMean(l, weights));
		}
		
		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i), actual.get(i));
		}
	}


	@Test
	public void testGetStoredObject() {
		// Determine expected and store it
		double[][] expected0 = new double[2][5];
	  expected0[0] = new double[]{0.0, 1.0, 2.0, 3.0, 4.0};
	  expected0[1] = new double[]{5.0, 6.0, 7.0, 8.0, 9.0};
	  
	  List<List<Double>> expected1 = new ArrayList<List<Double>>();
	  expected1.add(new ArrayList<Double>(Arrays.asList(new Double[]{0.0, 1.0, 2.0, 3.0, 4.0})));
	  expected1.add(new ArrayList<Double>(Arrays.asList(new Double[]{5.0, 6.0, 7.0, 8.0, 9.0})));
	
	  File file0 = new File("F:/research/data/results/testGetStoredObject_Array.xml");
	  ToolBox.storeObject(expected0, file0);
	  File file1 = new File("F:/research/data/results/testGetStoredObject_List.xml");
	  ToolBox.storeObject(expected1, file1);

	  double[][] actual0 = ToolBox.getStoredObject(new double[][]{}, file0);
	  List<List<Double>> actual1 = ToolBox.getStoredObject(new ArrayList<List<Double>>(), file1);

	  assertEquals(expected0.length, actual0.length);
	  for (int i = 0; i < expected0.length; i++) {
		  assertEquals(expected0[i].length, actual0[i].length);
		 for (int j = 0; j < expected0[i].length; j++) {
		  	assertEquals(expected0[i][j], actual0[i][j], delta);
		  }
	  }
	  assertEquals(expected1.size(), actual1.size());
	  for (int i = 0; i < expected1.size(); i++) {
		  assertEquals(expected1.get(i).size(), actual1.get(i).size());
		 for (int j = 0; j < expected1.get(i).size(); j++) {
		  	assertEquals(expected1.get(i).get(j), actual1.get(i).get(j));
		  }
	  }
	}


	@Test
	public void testWriteMatrixAsTable() {
		double[][] matrix = new double[3][5];
		matrix[0] = new double[]{0.1, 0.0012304, 1.01200, 2.01201, 3.01202, -1.0};
		matrix[1] = new double[]{0.01, 0.0012305, 4.01203, 5.01204, 6.01205, -1.0};
		matrix[2] = new double[]{0.001, 0.0012306, 7.01206, 8.01207, 9.01208, -1.0};
		
		String expected0 = 
			"0.1" + "\t" + "0.001230" + "\t" + "1.0120" + "\t" + "2.0120" + "\t" + "3.0120" + "\t" + "n/a" + "\r\n" + 
			"0.01" + "\t" + "0.001230" + "\t" +"4.0120" + "\t" + "5.0120" + "\t" + "6.0120" + "\t" + "n/a" + "\r\n" + 
			"0.001" + "\t" + "0.001230" + "\t" +"7.0120" + "\t" + "8.0120" + "\t" + "9.0120" + "\t" + "n/a" + "\r\n"; 
		
		String expected1 = 	
			"0.1" + "\t" + "0.001230" + "\t" + "1.0120" + "\t" + "2.0120" + "\t" + "3.0120" + "\t" + "n/a" + "\r\n" + 
			"0.01" + "\t" + "0.001231" + "\t" +"4.0120" + "\t" + "5.0120" + "\t" + "6.0121" + "\t" + "n/a" + "\r\n" + 
			"0.001" + "\t" + "0.001231" + "\t" +"7.0121" + "\t" + "8.0121" + "\t" + "9.0121" + "\t" + "n/a" + "\r\n"; 
				
		String[] legend = null; //new String[]{"<col>\t" , "<col>\t", "<col>\t", "<col>\t", "<col>\t", "<col>\t\r\n" };
		DecimalFormat decimalFormat = new DecimalFormat("0.0000");
		DecimalFormat alternativeDecimalFormat = new DecimalFormat("0.000000");
		List<Integer> columnsToSkip = Arrays.asList(new Integer[]{0});
		List<Integer> columnsWithAlternativeDecimalFormat = Arrays.asList(new Integer[]{1});
		double doesNotApplyValue = -1.0;
		String actual0 = ToolBox.writeMatrixAsTable(legend, matrix, decimalFormat, alternativeDecimalFormat, 
			RoundingMode.DOWN, columnsToSkip, columnsWithAlternativeDecimalFormat, doesNotApplyValue);
		String actual1 = ToolBox.writeMatrixAsTable(legend, matrix, decimalFormat, alternativeDecimalFormat, 
			RoundingMode.HALF_UP, columnsToSkip, columnsWithAlternativeDecimalFormat, doesNotApplyValue);
		
		assertEquals(expected0, actual0);
		assertEquals(expected1, actual1);
	}


	@Test
	public void testZerofyInt() {
		List<Integer> args = 
			Arrays.asList(new Integer[]{0, 1, 9, 10, 50, 75});
		List<String> expected = 
			Arrays.asList(new String[]{"00", "01", "09", "10", "50", "75"});
		
		List<String> actual = new ArrayList<String>();
		for (int i: args) {
			actual.add(ToolBox.zerofy(i, 2));
		}
		
		assertEquals(expected, actual);
	}


	@Test
	public void testZerofy() {
		List<String> args = 
			Arrays.asList(new String[]{"0.1", "1.1", "9.1", "10.1", "50.1", "75.1"});
		List<String> expected = 
			Arrays.asList(new String[]{"00.1", "01.1", "09.1", "10.1", "50.1", "75.1"});
		
		List<String> actual = new ArrayList<String>();
		for (String s: args) {
			actual.add(ToolBox.zerofy(s, 2));
		}

		assertEquals(expected, actual);
	}


	@Test
	public void testDetermineMaxLen() {
		List<Integer> args = Arrays.asList(new Integer[]{2, 9, 99, 101, 1001});
		List<Integer> expected = Arrays.asList(new Integer[]{2, 2, 2, 3, 4});
		
		List<Integer> actual = new ArrayList<Integer>();
		for (Integer i: args) {
			actual.add(ToolBox.maxLen(i));
		}
		
		assertEquals(expected, actual);
	}


	@Test
	public void testFormatDouble() {
		double d1 = 0.12345;
		double d2 = 3.1E-4; // 0.00031 
		List<String> expected = 
			Arrays.asList(new String[]{
			"0.12345", "00.1234", "0.123450" , ".12345", ".1234", ".123450", 
			"0.00031", "00.0003", "0.000310", ".00031", ".0003", ".000310"});

		List<String> actual = new ArrayList<String>();
		actual.add(ToolBox.formatDouble(d1, 1, 7)); // 0.12345  = allowed len
		actual.add(ToolBox.formatDouble(d1, 2, 7)); // 00.12345 > allowed len: cut 
		actual.add(ToolBox.formatDouble(d1, 1, 8)); // 0.12345  < allowed len: add zeroes
		actual.add(ToolBox.formatDouble(d1, 0, 6)); // .12345   = allowed len
		actual.add(ToolBox.formatDouble(d1, 0, 5)); // .12345   > allowed len: cut
		actual.add(ToolBox.formatDouble(d1, 0, 7)); // .12345   < allowed len: add zeroes
		
		actual.add(ToolBox.formatDouble(d2, 1, 7)); // 0.00031   = allowed len
		actual.add(ToolBox.formatDouble(d2, 2, 7)); // 00.00031  > allowed len: cut
		actual.add(ToolBox.formatDouble(d2, 1, 8)); // 0.00031 < allowed len: add zeroes
		actual.add(ToolBox.formatDouble(d2, 0, 6)); // .00031   = allowed len
		actual.add(ToolBox.formatDouble(d2, 0, 5)); // .00031   > allowed len: cut
		actual.add(ToolBox.formatDouble(d2, 0, 7)); // .00031   < allowed len: add zeroes

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i), actual.get(i));
		}
		assertEquals(expected, actual);
	}


	@Test
	public void testTabify() {
		String one = "test";
		String two = "longer test";
		String three = "(1)  Footnote 1";
		String spaces = "        ";
		
		List<String> expected = Arrays.asList(new String[]{
			"test\t", // the \t fills the String up to a full \t (8 spaces) 
			"test\t" + "\t",
			"longer test\t",
			"longer test\t" + "\t",
			"(1)  Footnote 1\t" + "\t",
			//
			"test    ", // the \t fills the String up to a full \t (8 spaces) 
			"test    " + spaces,
			"longer test     ",
			"longer test     " + spaces,
			"(1)  Footnote 1 " + spaces
		});

		List<String> actual = new ArrayList<>();
		actual.add(ToolBox.tabify(one, 1, true));
		actual.add(ToolBox.tabify(one, 2, true));
		actual.add(ToolBox.tabify(two, 2, true));
		actual.add(ToolBox.tabify(two, 3, true));
		actual.add(ToolBox.tabify(three, 3, true));
		//
		actual.add(ToolBox.tabify(one, 1, false));
		actual.add(ToolBox.tabify(one, 2, false));
		actual.add(ToolBox.tabify(two, 2, false));
		actual.add(ToolBox.tabify(two, 3, false));
		actual.add(ToolBox.tabify(three, 3, false));

		assertEquals(expected, actual);
	}


	@Test
	public void testReplaceFirstInString() {
		List<String> strings = Arrays.asList(new String[]{
			"some text bla some more bla text", 
			"some text bla some more bla text", 
			"some text some more text"	
		});
		List<String> replacements = Arrays.asList(new String[]{
			"and", "or", "but"		
		});
		
		List<String> expected = Arrays.asList(new String[]{
			"some text and some more bla text", 
			"some text or some more bla text", 
			"some text some more text" 			
		});
		
		List<String> actual = new ArrayList<>();
		for (int i = 0; i < strings.size(); i++) {
			actual.add(ToolBox.replaceFirstInString(strings.get(i), 
				"bla", replacements.get(i)));
		}
 		
		assertEquals(expected, actual);
	}


	@Test
	public void testSplitExt() {
		List<String> filenames = Arrays.asList(
			"file1.tc", 
			"file.no.2.txt" 
		);

		List<String[]> expected = Arrays.asList(
			new String[]{"file1", ".tc"},
			new String[]{"file.no.2", ".txt"}
		);

		List<String[]> actual = new ArrayList<>();
		for (String s : filenames) {
			actual.add(ToolBox.splitExt(s));
		}

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i).length, actual.get(i).length);
			for (int j = 0; j < expected.get(i).length; j++) {
				assertEquals(expected.get(i)[j], actual.get(i)[j]);
			}
		}
	}


	@Test
	public void testInsertIntoString() {
		List<Integer> indices = Arrays.asList(new Integer[]{0, 10, 6});

		List<String> strings = Arrays.asList(
			"string one", "string two", "string three"		
		);

		List<String> expected = Arrays.asList(
			"HEREstring one", "string twoHERE", "stringHERE three"		
		);

		List<String> actual = new ArrayList<>();
		for (int i = 0; i < strings.size(); i++) {
			actual.add(ToolBox.insertIntoString(strings.get(i), "HERE", indices.get(i)));
		}

		assertEquals(expected, actual);	
	}


	@Test
	public void testGetFirstIndexOfNot() {
		List<String> strings = Arrays.asList(new String[]{
			"---x--", "- - - x - -", "bananas", "doesn't matter", "abababab"		
		});
		
		List<List<String>> notLists = new ArrayList<>();
		notLists.add(Arrays.asList(new String[]{"-"}));
		notLists.add(Arrays.asList(new String[]{" ", "-"}));
		notLists.add(Arrays.asList(new String[]{"a", "b", "n"}));
		notLists.add(Arrays.asList(new String[]{}));
		notLists.add(Arrays.asList(new String[]{"a", "b"}));

		List<Integer> expected = Arrays.asList(new Integer[]{3, 6, 6, 0, -1});
		
		List<Integer> actual = new ArrayList<>();
		for (int i = 0; i < strings.size(); i++) {
			actual.add(ToolBox.getFirstIndexOfNot(strings.get(i), notLists.get(i)));
		}
		
		assertEquals(expected, actual);
	}


	@Test
	public void testBreakIntoLines() {
		List<String> strings = Arrays.asList(new String[]{
			"yes fits on line", 
			"fits on two  lines",
			"does not fit on two lines but fits on three"
		});

		List<List<String>> expected = new ArrayList<>();
		expected.add(Arrays.asList(new String[]{"yes fits on line"}));
		expected.add(Arrays.asList(new String[]{"fits on two", "lines"}));
		expected.add(Arrays.asList(new String[]{
			"does not fit on", "two lines but", "fits on three"}));
		List<List<String>> actual = new ArrayList<>();
		for (String s : strings) {
			actual.add(ToolBox.breakIntoLines(s, 16));
		}
		
		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i).size(), actual.get(i).size());
			for (int j = 0; j < expected.get(i).size(); j++) {
				assertEquals(expected.get(i).get(j), actual.get(i).get(j));
			}
		}
	}


	@Test
	public void testToInt() {
		assertEquals(0, ToolBox.toInt(false));
		assertEquals(1, ToolBox.toInt(true));
	}


	@Test
	public void testToBoolean() {
		assertEquals(false, ToolBox.toBoolean(0));
		assertEquals(true, ToolBox.toBoolean(1));
	}


	@Test
	public void testCombineLists() {
	  List<Integer> listOne = Arrays.asList(new Integer[]{10, 20, 30, 40});	
	  List<Integer> listTwo = Arrays.asList(new Integer[]{1, 2, 3, 4});
	  
	  List<List<Integer>> expected = new ArrayList<List<Integer>>();
	  expected.add(Arrays.asList(new Integer[]{10, 1}));
	  expected.add(Arrays.asList(new Integer[]{20, 2}));
	  expected.add(Arrays.asList(new Integer[]{30, 3}));
	  expected.add(Arrays.asList(new Integer[]{40, 4}));
	  
	  List<List<Integer>> actual = ToolBox.combineLists(listOne, listTwo);
	  
	  assertEquals(expected.size(), actual.size());
	  for (int i = 0; i < expected.size(); i++) {
	  	assertEquals(expected.get(i).size(), actual.get(i).size());
	  	for (int j = 0; j < expected.get(i).size(); j++) {
	  		assertEquals(expected.get(i).get(j), actual.get(i).get(j));
	  	}
	  }
	}


	@Test
	public void testGetAllowedCharactersAfterMarker() {	
		// Create the complete strings to test
		// Example for finding the training error (in the "Training results" file) 
		String completeString0 = "After this: 1.234E-3" + ")" + "\n" + "text after";
		// Example for finding the classification error on the training set (in the "Training results" file)
		String completeString1 = "After this: 1.234E-3" + "\n" + "text after";
		// Example for finding the application error on the training set (in the "application_rec" file)
		String completeString2 = "After this: 12/34" + " " + "text after";
		// Example for finding the classification error on the test set (in the "Test results" file) 
		String completeString3 = "After this: 0.1234";
		// Example for finding the application error on the test set (in the "application_rec" file)
		String completeString4 = "After this: 0.1234" + "\n" + "text after";
		// Example for finding a number in decimal notation (in any of the above files)
//		String completeString5 = "After this: 1.2345" + "E-5" + "56";
		List<String> completeStrings = Arrays.asList(new String[]{completeString0, completeString1, completeString2,
			completeString3, completeString4}); 
		String marker = "After this: ";

		List<String> expected = new ArrayList<String>();
		expected.add("0.001234"); expected.add("0.001234"); expected.add("12/34"); expected.add("0.1234");
		expected.add("0.1234");

		List<String> actual = new ArrayList<String>();
		for (int i = 0; i < completeStrings.size(); i++) {
			String currentActual = ToolBox.getAllowedCharactersAfterMarker(completeStrings.get(i), marker);
			actual.add(currentActual);
		}

		assertEquals(expected, actual);
	}


	@Test
	public void testBreakString() {
	  List<Double> aList = Arrays.asList(new Double[]{0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0});	
	  String aString = aList.toString();
	  
	  String expected1 = 
		  "[0.0, 0.0, 0.0," + "\r\n" + " 1.0, 0.0, 0.0," + "\r\n" + " 0.0, 0.0, 0.0," + "\r\n" + " 0.0, 0.0, 0.0]";
	  String expected2 = 
	  	"[0.0, 0.0, 0.0," + "\r\n__" + " 1.0, 0.0, 0.0," + "\r\n__" + " 0.0, 0.0, 0.0," + "\r\n__" + " 0.0, 0.0, 0.0]";
	  
	  String actual1 = ToolBox.breakString(aString, 3, ',', "");
	  String actual2 = ToolBox.breakString(aString, 3, ',', "__");
	  
	  assertEquals(expected1.length(), actual1.length());
	  assertEquals(expected1, actual1);
	  assertEquals(expected2.length(), actual2.length());
	  assertEquals(expected2, actual2);
	}


	@Test
	public void testCreateCSVTableString() {
		String[][] s = new String[4][4];
		s[0] = new String[]{"00", "01", "02", "03"};
		s[1] = new String[]{"10", "11", "12", "13"};
		s[2] = new String[]{"20", "21", "22", "23"};
		s[3] = new String[]{"", "", "", "33"};

		String expected = new String(
			"00,01,02,03" + "\r\n" +
			"10,11,12,13" + "\r\n" +
			"20,21,22,23" + "\r\n" +
			",,,33"
		);

		String actual = ToolBox.createCSVTableString(s);

		assertEquals(expected, actual);
	}


	@Test
	public void testRetrieveCSVTable() {
		String s = new String(
			"00,01,,03,04" + "\r\n" +
			"10,11,,13," + "\r\n" +
			"20,21,,23,"
		);
		
		String[][] expected = new String[3][4];
		expected[0] = new String[]{"00", "01", "", "03", "04"};
		expected[1] = new String[]{"10", "11", "", "13", ""};
		expected[2] = new String[]{"20", "21", "", "23", ""};
		String[][] actual = ToolBox.retrieveCSVTable(s);
		
		assertEquals(expected.length, actual.length);
		for (int i = 0; i < expected.length; i++) {
			assertEquals(expected[i].length, actual[i].length);
			for (int j = 0; j < expected[i].length; j++) {
				assertEquals(expected[i][j], actual[i][j]);
			}
		}
	}


	@Test
	public void testConvertCSVTable() {
		String[][] s = new String[3][4];
		s[0] = new String[]{"0.1234", "0.99", "0.01", "3.3"};
		s[1] = new String[]{"1.234", "1000.0", "1.234", "0.00000001"};
		s[2] = new String[]{"0.35", "345.4", "0.01", "100.0001"};
		
		List<double[]> expected = new ArrayList<double[]>();
		expected.add(new double[]{0.1234, 0.99, 0.01, 3.3});
		expected.add(new double[]{1.234, 1000.0, 1.234, 0.00000001});
		expected.add(new double[]{0.35, 345.4, 0.01, 100.0001});
		
		List<double[]> actual = ToolBox.convertCSVTable(s);

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i).length, actual.get(i).length);
			for (int j = 0; j < expected.get(i).length; j++) {
				assertEquals(expected.get(i)[j], actual.get(i)[j], delta);
			}
		}
	}


	@Test
	public void testParseStringOfIntegers() {
		String testString0 = "1\n2\n3\n4\n5";
		String testString1 = "2-3-4-5-6";
		String testString2 = "3 4 5 6 7";

		List<List<Integer>> expected = new ArrayList<List<Integer>>();
		expected.add(new ArrayList<Integer>(Arrays.asList(new Integer[]{1, 2, 3, 4, 5})));
		expected.add(new ArrayList<Integer>(Arrays.asList(new Integer[]{2, 3, 4, 5, 6})));
		expected.add(new ArrayList<Integer>(Arrays.asList(new Integer[]{3, 4, 5, 6, 7})));

		String regEx0 = "\n";
		String regEx1 = "-";
		String regEx2 = " "; 
		List<List<Integer>> actual = new ArrayList<List<Integer>>();
		actual.add(ToolBox.parseStringOfIntegers(testString0, regEx0));
		actual.add(ToolBox.parseStringOfIntegers(testString1, regEx1));
		actual.add(ToolBox.parseStringOfIntegers(testString2, regEx2));

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i).size(), actual.get(i).size());
			for (int j = 0; j < expected.get(i).size(); j++) {
				assertEquals(expected.get(i).get(j), actual.get(i).get(j));
			}
		}
		assertEquals(expected, actual);
	}


	@Test
	public void testEncodeListOfIntegers() {
		List<List<Integer>> all = new ArrayList<List<Integer>>();
		all.add(null);
		all.add(new ArrayList<Integer>());
		all.add(Arrays.asList(new Integer[]{1, 3, 5, 7, 9}));

		List<Integer> expected = new ArrayList<Integer>();
		expected.add(-1);
		expected.add(-1);
		expected.add(13579);
		List<Integer> actual = new ArrayList<Integer>();
		for (List<Integer> l : all) {
			actual.add(ToolBox.encodeListOfIntegers(l));
		}

		assertEquals(expected.size(), actual.size());
		assertEquals(expected, actual);
	}


	@Test
	public void decodeListOfIntegers() {
		List<List<Integer>> expected = new ArrayList<List<Integer>>(); 
		expected.add(Arrays.asList(new Integer[]{-1}));
		expected.add(Arrays.asList(new Integer[]{1, 5, 7}));
		expected.add(Arrays.asList(new Integer[]{10, 24, 89}));
		List<List<Integer>> actual = new ArrayList<List<Integer>>();
		actual.add(ToolBox.decodeListOfIntegers(-1, 1));
		actual.add(ToolBox.decodeListOfIntegers(157, 1));
		actual.add(ToolBox.decodeListOfIntegers(102489, 2));
		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i).size(), actual.get(i).size());
			for (int j = 0; j < expected.get(i).size(); j++) {
				assertEquals(expected.get(i).get(j), actual.get(i).get(j));
			}
		}
		assertEquals(expected, actual);
	}


	@Test
	public void testCreateGrid() {
		List<List<List<Double>>> inputs = new ArrayList<List<List<Double>>>();
		List<List<Double>> two = new ArrayList<List<Double>>();
		two.add(Arrays.asList(new Double[]{1.0, 2.0, 3.0}));
		two.add(Arrays.asList(new Double[]{4.0, 5.0, 6.0, 7.0}));
		List<List<Double>> three = new ArrayList<List<Double>>(two);
		three.add(Arrays.asList(new Double[]{7.0, 8.0, 9.0}));
		inputs.add(two);
		inputs.add(three);
		
		List<List<List<Double>>> expected = new ArrayList<List<List<Double>>>();
		List<List<Double>> expTwo = new ArrayList<List<Double>>();
		expTwo.add(Arrays.asList(new Double[]{1.0, 4.0}));
		expTwo.add(Arrays.asList(new Double[]{1.0, 5.0}));
		expTwo.add(Arrays.asList(new Double[]{1.0, 6.0}));
		expTwo.add(Arrays.asList(new Double[]{1.0, 7.0}));
		expTwo.add(Arrays.asList(new Double[]{2.0, 4.0}));
		expTwo.add(Arrays.asList(new Double[]{2.0, 5.0}));
		expTwo.add(Arrays.asList(new Double[]{2.0, 6.0}));
		expTwo.add(Arrays.asList(new Double[]{2.0, 7.0}));
		expTwo.add(Arrays.asList(new Double[]{3.0, 4.0}));
		expTwo.add(Arrays.asList(new Double[]{3.0, 5.0}));
		expTwo.add(Arrays.asList(new Double[]{3.0, 6.0}));
		expTwo.add(Arrays.asList(new Double[]{3.0, 7.0}));
		List<List<Double>> expThree = new ArrayList<List<Double>>();
		expThree.add(Arrays.asList(new Double[]{1.0, 4.0, 7.0}));
		expThree.add(Arrays.asList(new Double[]{1.0, 4.0, 8.0}));
		expThree.add(Arrays.asList(new Double[]{1.0, 4.0, 9.0}));
		expThree.add(Arrays.asList(new Double[]{1.0, 5.0, 7.0}));
		expThree.add(Arrays.asList(new Double[]{1.0, 5.0, 8.0}));
		expThree.add(Arrays.asList(new Double[]{1.0, 5.0, 9.0}));
		expThree.add(Arrays.asList(new Double[]{1.0, 6.0, 7.0}));
		expThree.add(Arrays.asList(new Double[]{1.0, 6.0, 8.0}));
		expThree.add(Arrays.asList(new Double[]{1.0, 6.0, 9.0}));
		expThree.add(Arrays.asList(new Double[]{1.0, 7.0, 7.0}));
		expThree.add(Arrays.asList(new Double[]{1.0, 7.0, 8.0}));
		expThree.add(Arrays.asList(new Double[]{1.0, 7.0, 9.0}));
		expThree.add(Arrays.asList(new Double[]{2.0, 4.0, 7.0}));
		expThree.add(Arrays.asList(new Double[]{2.0, 4.0, 8.0}));
		expThree.add(Arrays.asList(new Double[]{2.0, 4.0, 9.0}));
		expThree.add(Arrays.asList(new Double[]{2.0, 5.0, 7.0}));
		expThree.add(Arrays.asList(new Double[]{2.0, 5.0, 8.0}));
		expThree.add(Arrays.asList(new Double[]{2.0, 5.0, 9.0}));
		expThree.add(Arrays.asList(new Double[]{2.0, 6.0, 7.0}));
		expThree.add(Arrays.asList(new Double[]{2.0, 6.0, 8.0}));
		expThree.add(Arrays.asList(new Double[]{2.0, 6.0, 9.0}));
		expThree.add(Arrays.asList(new Double[]{2.0, 7.0, 7.0}));
		expThree.add(Arrays.asList(new Double[]{2.0, 7.0, 8.0}));
		expThree.add(Arrays.asList(new Double[]{2.0, 7.0, 9.0}));
		expThree.add(Arrays.asList(new Double[]{3.0, 4.0, 7.0}));
		expThree.add(Arrays.asList(new Double[]{3.0, 4.0, 8.0}));
		expThree.add(Arrays.asList(new Double[]{3.0, 4.0, 9.0}));
		expThree.add(Arrays.asList(new Double[]{3.0, 5.0, 7.0}));
		expThree.add(Arrays.asList(new Double[]{3.0, 5.0, 8.0}));
		expThree.add(Arrays.asList(new Double[]{3.0, 5.0, 9.0}));
		expThree.add(Arrays.asList(new Double[]{3.0, 6.0, 7.0}));
		expThree.add(Arrays.asList(new Double[]{3.0, 6.0, 8.0}));
		expThree.add(Arrays.asList(new Double[]{3.0, 6.0, 9.0}));
		expThree.add(Arrays.asList(new Double[]{3.0, 7.0, 7.0}));
		expThree.add(Arrays.asList(new Double[]{3.0, 7.0, 8.0}));
		expThree.add(Arrays.asList(new Double[]{3.0, 7.0, 9.0}));
		expected.add(expTwo);
		expected.add(expThree);
		
		List<List<List<Double>>> actual = new ArrayList<List<List<Double>>>();
		for (List<List<Double>> l : inputs) {
			actual.add(ToolBox.createGrid(l));
		}
		
		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i).size(), actual.get(i).size()); 
			for (int j = 0; j < expected.get(i).size(); j++) {
				assertEquals(expected.get(i).get(j).size(), actual.get(i).get(j).size());
				for (int k = 0; k < expected.get(i).get(j).size(); k++) {
					assertEquals(expected.get(i).get(j).get(k), actual.get(i).get(j).get(k));
				}
			}
		}
		assertEquals(expected, actual);
	}


	@Test
	public void testReadCSVFile() {
		String contentsNonMulti = "1\r\n2\r\n3\r\n4\r\n5";
		String contentsMulti = "1;1\n2;2\n3;3\n4;4\n5;5";
		
		List<List<List<Integer>>> expected = new ArrayList<List<List<Integer>>>();
		List<List<Integer>> nonMulti = new ArrayList<List<Integer>>();
		nonMulti.add(Arrays.asList(new Integer[]{1}));
		nonMulti.add(Arrays.asList(new Integer[]{2}));
		nonMulti.add(Arrays.asList(new Integer[]{3}));
		nonMulti.add(Arrays.asList(new Integer[]{4}));
		nonMulti.add(Arrays.asList(new Integer[]{5}));
		expected.add(nonMulti);
		List<List<Integer>> multi = new ArrayList<List<Integer>>();
		multi.add(Arrays.asList(new Integer[]{1, 1}));
		multi.add(Arrays.asList(new Integer[]{2, 2}));
		multi.add(Arrays.asList(new Integer[]{3, 3}));
		multi.add(Arrays.asList(new Integer[]{4, 4}));
		multi.add(Arrays.asList(new Integer[]{5, 5}));
		expected.add(multi);
		
		List<List<List<Integer>>> actual = new ArrayList<List<List<Integer>>>();
		actual.add(ToolBox.readCSVFile(contentsNonMulti, ",", false));
		actual.add(ToolBox.readCSVFile(contentsMulti, ";", true));

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i).size(), actual.get(i).size());
			for (int j = 0; j < expected.get(i).size(); j++) {
				assertEquals(expected.get(i).get(j).size(), actual.get(i).get(j).size());
				for (int k = 0; k < expected.get(i).get(j).size(); k++) {
					assertEquals(expected.get(i).get(j).get(k), actual.get(i).get(j).get(k));
				}
			}
		}
		assertEquals(expected, actual);
	}


	@Test
	public void testArrayToList() {
		List<double[]> arrays = new ArrayList<double[]>();
		arrays.add(new double[]{1.0, 2.0, 3.0, 4.0});
		arrays.add(new double[]{-1.0, -2.0, -3.0, -4.0});

		List<List<Double>> expected = new ArrayList<List<Double>>();
		expected.add(Arrays.asList(new Double[]{1.0, 2.0, 3.0, 4.0}));
		expected.add(Arrays.asList(new Double[]{-1.0, -2.0, -3.0, -4.0}));

		List<List<Double>> actual = new ArrayList<List<Double>>();
		for (int i = 0; i < expected.size(); i++) {
			double[] currentArray = arrays.get(i);
			List<Double> currentActual = ToolBox.arrayToList(currentArray);
			actual.add(currentActual);
		}

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i).size(), actual.get(i).size());
			for (int j = 0; j < expected.get(i).size(); j++) {
				assertEquals(expected.get(i).get(j), actual.get(i).get(j));
			}
		}
	}


	@Test
	public void testListToArray() {
		List<List<Double>> lists = new ArrayList<List<Double>>();
		lists.add(Arrays.asList(new Double[]{1.0, 2.0, 3.0, 4.0}));
		lists.add(Arrays.asList(new Double[]{-1.0, -2.0, -3.0, -4.0}));
		
		List<double[]> expected = new ArrayList<double[]>();
		expected.add(new double[]{1.0, 2.0, 3.0, 4.0});
		expected.add(new double[]{-1.0, -2.0, -3.0, -4.0});
		
		List<double[]> actual = new ArrayList<double[]>();
		for (List<Double> l : lists) {
			actual.add(ToolBox.listToArray(l));
		}

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i).length, actual.get(i).length);
			for (int j = 0; j < expected.get(i).length; j++) {
				assertEquals(expected.get(i)[j], actual.get(i)[j], delta);
			}
		}
	}


	@Test
	public void testConvertToListDouble(){
		List<Integer> integers = new ArrayList<Integer>(Arrays.asList(new Integer[]{-3, -1, -2, 0, -1, 1}));
		
		// Make expected values
		List<Double> expected = new ArrayList<Double>(Arrays.asList(new Double[]{-3.0, -1.0, -2.0, 0.0, -1.0, 1.0}));
		
		// Get actual values
		List<Double> actual = ToolBox.convertToListDouble(integers);
		
		// Assert equality
		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i), actual.get(i));
		}
		assertEquals(expected, actual);
	}


	@Test
	public void testConvertToListString() {
		List<List<Double>> listOfDoubles = new ArrayList<List<Double>>();
		listOfDoubles.add(Arrays.asList(new Double[]{1.0, 3/8.0, -1/2.0, 0.12345}));
		listOfDoubles.add(Arrays.asList(new Double[]{0.12345, -1/2.0, 3/8.0, 1.0}));
		
		List<List<String>> expected = new ArrayList<List<String>>();
		expected.add(Arrays.asList(new String[]{"1.0", "0.375", "-0.5", "0.12345"}));
		expected.add(Arrays.asList(new String[]{"0.12345", "-0.5", "0.375", "1.0"}));
		
		List<List<String>> actual = ToolBox.convertToListString(listOfDoubles);
		
		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i).size(), actual.get(i).size());
			for (int j = 0; j < expected.get(i).size(); j++) {
				assertEquals(expected.get(i).get(j), actual.get(i).get(j));
			}
		}
	}


	@Test
	public void testConvertToRational() {
		List<String> strings = new ArrayList<String>(Arrays.asList(new String[]{"1/2", "2/3", "3/4", "-15/17"}));

		Rational r0 = new Rational(1, 2);
		Rational r1 = new Rational(2, 3);
		Rational r2 = new Rational(3, 4);
		Rational r3 = new Rational(-15, 17);
		List<Rational> expected = new ArrayList<Rational>(Arrays.asList(new Rational[]{r0, r1, r2, r3}));

		List<Rational> actual = new ArrayList<Rational>();
		for (String s : strings) {
			Rational currentActual = ToolBox.convertToRational(s);
			actual.add(currentActual);
		}

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i), actual.get(i));
		}
		assertEquals(expected, actual);
	}


	@Test
	public void testSumListInteger() {
		List<Integer> testValues = Arrays.asList(new Integer[]{-4, -2, 0, 2, 4, 6, 8});
		int expected = 14;
		assertEquals(expected, ToolBox.sumListInteger(testValues));
	}


	@Test
	public void testSumListDouble() {
		List<Double> testValues = Arrays.asList(new Double[]{-4.1, -2.003, 0.0, 2.124, 4.0, 6.112, 8.4});
		double expected = -4.1 + -2.003 + 0.0 + 2.124 + 4.0 + 6.112 + 8.4;
		assertEquals(expected, ToolBox.sumListDouble(testValues), delta);
	}


	@Test
	public void testSumDoubleArray() {
		double[] testValues = new double[]{-4.1, -2.003, 0.0, 2.124, 4.0, 6.112, 8.4};
		double expected = -4.1 + -2.003 + 0.0 + 2.124 + 4.0 + 6.112 + 8.4;
		assertEquals(expected, ToolBox.sumDoubleArray(testValues), delta);
	}


	@Test
	public void testSumListRational() {
		List<Rational> list1 = 
			Arrays.asList(new Rational[]{new Rational(3, 4), new Rational(1, 2), new Rational(1, 7)});
		List<Rational> list2 = 
			Arrays.asList(new Rational[]{new Rational(1, 3), new Rational(2, 4), new Rational(5, 3)});
			
		// Determine expected
		List<Rational> expected = Arrays.asList(new Rational[]{new Rational(39, 28), new Rational(5, 2)});
		
		// Calculate actual
		List<Rational> actual = new ArrayList<Rational>();
		actual.add(ToolBox.sumListRational(list1));
		actual.add(ToolBox.sumListRational(list2));
		
		// Assert equality
		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i).getNumer(), actual.get(i).getNumer());
			assertEquals(expected.get(i).getDenom(), actual.get(i).getDenom());
		}	
	}


	@Test
	public void testIsMultiple() {
		List<Rational> rs = Arrays.asList(new Rational[]{
			new Rational(17, 12), // true
			new Rational(192, 1), // true
			new Rational(18496, 96), // true
			new Rational(13, 7), // false
			new Rational(21, 7), // true
			new Rational(197291, 1024), // false
		});
		
		List<Boolean> expected = 
			Arrays.asList(new Boolean[]{true, true, true, false, true, false});
		
		List<Boolean> actual = new ArrayList<>();
		for (Rational r : rs) {
			actual.add(ToolBox.isMultiple(r, new Rational(1, 12)));
		}
		
		assertEquals(expected.size(), actual.size());
		assertEquals(expected, actual);
	}


	@Test
	public void testConvertToStringNoTrailingZeros(){
		List<String> expected = new ArrayList<String>(Arrays.asList(new String[]{"1.0", "1.0", "1.0", "0.1",
			"0.01", "0.001", "1.0E-4", "1.0E-5", "0.0"}));
		List<String> actual = new ArrayList<String>();
		actual.add(ToolBox.convertToStringNoTrailingZeros(1.000));
		actual.add(ToolBox.convertToStringNoTrailingZeros(1.00));
		actual.add(ToolBox.convertToStringNoTrailingZeros(1.0));
		actual.add(ToolBox.convertToStringNoTrailingZeros(0.10));
		actual.add(ToolBox.convertToStringNoTrailingZeros(0.010));
		actual.add(ToolBox.convertToStringNoTrailingZeros(0.0010));
		actual.add(ToolBox.convertToStringNoTrailingZeros(0.00010));
		actual.add(ToolBox.convertToStringNoTrailingZeros(0.000010));
		actual.add(ToolBox.convertToStringNoTrailingZeros(0.0));
		assertEquals(expected, actual);
	}


	@Test
	public void testStringifyList() {
		List<List<Integer>> lists = new ArrayList<List<Integer>>();
		lists.add(Arrays.asList(new Integer[]{1, 2}));
		lists.add(Arrays.asList(new Integer[]{1, 2, 3}));
		lists.add(Arrays.asList(new Integer[]{1, 2, 3, 4, 5}));
		
		List<String> expected = Arrays.asList(new String[]{
			"1 and 2", 
			"1, 2, and 3", 
			"1, 2, 3, 4, and 5"
		});
		
		List<String> actual = new ArrayList<String>();
		for (List<Integer> l : lists) {
			actual.add(ToolBox.StringifyList(l));
		}
		
		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i), actual.get(i));
		}	
	}


	@Test
	public void testCalculatePrecisonRecallF1ScoreAsRationals() {
		// Make some random values
		Integer[] truePositives = new Integer[]{45, 92};
		Integer[] falsePositives = new Integer[]{15, 253};
		Integer[] falseNegatives = new Integer[]{35, 94};
		
		// For each set of tP, fP, and fN: determine expected
		List<Rational[]> expected = new ArrayList<Rational[]>();
		Rational[] expected0 = new Rational[3];
		Rational precision0 = new Rational(45, 60);
		Rational recall0 = new Rational(45, 80); 
		Rational f1Score0 = new Rational(9, 14);
		expected0[0] = precision0; 
		expected0[1] = recall0;
		expected0[2] = f1Score0;
		Rational[] expected1 = new Rational[3];
		Rational precision1 = new Rational(92, 345);
		Rational recall1 = new Rational(92, 186);
		Rational f1Score1 = new Rational(184, 531);
		expected1[0] = precision1; 
		expected1[1] = recall1;
		expected1[2] = f1Score1;
		expected.add(expected0); expected.add(expected1); 
		
		// For each set of tP, fP, and fN: calculate actual
		List<Rational[]> actual = new ArrayList<Rational[]>();
		Rational[] actual0 = 
			ToolBox.calculatePrecisonRecallF1ScoreAsRationals(truePositives[0], falsePositives[0], falseNegatives[0]);
		Rational[] actual1 = 
				ToolBox.calculatePrecisonRecallF1ScoreAsRationals(truePositives[1], falsePositives[1], falseNegatives[1]);
		actual.add(actual0); actual.add(actual1);
		
		// Assert equality
		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i).length, actual.get(i).length);
			for (int j = 0; j < expected.get(i).length; j++) {
				assertEquals(expected.get(i)[j], actual.get(i)[j]);
			}
		}
	}


	@Test
	public void testConcatDoubleArrays() {	
		List<double[]> listOfArrays = new ArrayList<double[]>();
		listOfArrays.add(new double[]{0.0, 1.0, 2.0, 3.0, 4.0});
		listOfArrays.add(new double[]{5.0, 6.0, 7.0, 8.0});
		listOfArrays.add(new double[]{-1.0, -2.0});
		
		// Determine expected
		double[] expected = new double[]{0.0, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, -1.0, -2.0};
		
		// Calculate actual
		double[] actual = ToolBox.concatDoubleArrays(listOfArrays);

		assertEquals(expected.length, actual.length);
		for (int i = 0; i < expected.length; i++) {
			assertEquals(expected[i], actual[i], delta);
		}
	}


	@Test
	public void testConcatArrays() {	
		List<Object[]> listOfArrays = new ArrayList<Object[]>();
		listOfArrays.add(new String[]{"0.0", "1.0", "2.0", "3.0", "4.0"});
		listOfArrays.add(new String[]{"5.0", "6.0", "7.0", "8.0"});
		listOfArrays.add(new String[]{"-1.0", "-2.0"});
		
		// Determine expected
		String[] expected = 
			new String[]{"0.0", "1.0", "2.0", "3.0", "4.0", "5.0", "6.0", "7.0", "8.0",
			"-1.0", "-2.0"};
		
		// Calculate actual
		Object[] actual = ToolBox.concatArrays(listOfArrays);
		
		// Assert equality
		assertEquals(expected.length, actual.length);
		for (int i = 0; i < expected.length; i++) {
			assertEquals(expected[i], actual[i]);
		}
	}


	@Test
	public void testConvertToDecimalNotation() {
	  // Make a List of test values
		List<String> testValues = 
			Arrays.asList(new String[]{"1.2345E-0", "1.2345E-1", "1.2345E-2", "1.2345E-3", "1.2345E-4", "0.12345"}); 
		 
		// Make the List of expected values
		List<String> expected = Arrays.asList(new String[]{"1.2345", "0.12345", "0.012345", "0.0012345", "0.00012345",
			"0.12345"}); 
		
		// Calculate the actual values and add them to a List
		List<String> actual = new ArrayList<String>();
		for (int i = 0; i < expected.size(); i++) {
			String currentActual = ToolBox.convertToDecimalNotation(testValues.get(i));
			actual.add(currentActual);
		}
		
		// Assert equality
		assertEquals(expected, actual);
	}


	@Test
	public void testConvertDoubleArrayToString() {
		double[] aDoubleArray = new double[]{1.0, 2.0, 3.0, 4.0, 5.0};
		
		String expected = "[1.0, 2.0, 3.0, 4.0, 5.0]";
		String actual = ToolBox.convertDoubleArrayToString(aDoubleArray);
		
		assertEquals(expected, actual);
	}


	@Test
	public void testNormaliseListDouble() {
		List<List<Double>> all = new ArrayList<List<Double>>();
		all.add(Arrays.asList(new Double[]{0.7, 0.6, 0.3, 0.4}));
		all.add(Arrays.asList(new Double[]{0.3, 0.3, 0.9, 0.1}));
		all.add(Arrays.asList(new Double[]{0.6, 0.8, 0.0, 0.2}));
		all.add(Arrays.asList(new Double[]{0.0, 0.0, 0.0, 0.0}));
		
		List<List<Double>> expected = new ArrayList<List<Double>>();
		expected.add(Arrays.asList(new Double[]{0.35, 0.3, 0.15, 0.2}));
		expected.add(Arrays.asList(new Double[]{0.1875, 0.1875, 0.5625, 0.0625}));
		expected.add(Arrays.asList(new Double[]{0.375, 0.5, 0.0, 0.125}));
		expected.add(Arrays.asList(new Double[]{0.25, 0.25, 0.25, 0.25}));
		
		List<List<Double>> actual = new ArrayList<List<Double>>();
		for (List<Double> l : all) {
			actual.add(ToolBox.normaliseListDouble(l));
		}
	
		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i).size(), actual.get(i).size(), 0.00001);
			for (int j = 0; j < expected.get(i).size(); j++) {
				assertEquals(expected.get(i).get(j), actual.get(i).get(j), 0.00001);
			}
		}		
	}


	@Test
	public void testNormaliseDoubleArray() {
		List<double[]> all = new ArrayList<double[]>();
		all.add(new double[]{0.7, 0.6, 0.3, 0.4});
		all.add(new double[]{0.3, 0.3, 0.9, 0.1});
		all.add(new double[]{0.6, 0.8, 0.0, 0.2});
		all.add(new double[]{0.0, 0.0, 0.0, 0.0});
		
		List<double[]> expected = new ArrayList<double[]>();
		expected.add(new double[]{0.35, 0.3, 0.15, 0.2});
		expected.add(new double[]{0.1875, 0.1875, 0.5625, 0.0625});
		expected.add(new double[]{0.375, 0.5, 0.0, 0.125});
		expected.add(new double[]{0.25, 0.25, 0.25, 0.25});
		
		List<double[]> actual = new ArrayList<double[]>();
		for (double[] d : all) {
			actual.add(ToolBox.normaliseDoubleArray(d));
		}
	
		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i).length, actual.get(i).length, 0.00001);
			for (int j = 0; j < expected.get(i).length; j++) {
				assertEquals(expected.get(i)[j], actual.get(i)[j], 0.00001);
			}
		}		
	}


	@Test
	public void testGetIndicesOf() {
		List<Integer> list = Arrays.asList(new Integer[]{0, 1, 0, 2, 0, 3, 0, 1, 0, 2, 0, 3, 4});
		List<List<Integer>> expected = new ArrayList<List<Integer>>();
		expected.add(Arrays.asList(new Integer[]{0, 2, 4, 6, 8, 10}));
		expected.add(Arrays.asList(new Integer[]{1, 7}));
		expected.add(Arrays.asList(new Integer[]{3, 9}));
		expected.add(Arrays.asList(new Integer[]{5, 11}));
		expected.add(Arrays.asList(new Integer[]{12}));
		
		List<List<Integer>> actual = new ArrayList<List<Integer>>();
		actual.add(ToolBox.getIndicesOf(list, 0));
		actual.add(ToolBox.getIndicesOf(list, 1));
		actual.add(ToolBox.getIndicesOf(list, 2));
		actual.add(ToolBox.getIndicesOf(list, 3));
		actual.add(ToolBox.getIndicesOf(list, 4));
		
		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i).size(), actual.get(i).size());
			for (int j = 0; j < expected.get(i).size(); j++) {
				assertEquals(expected.get(i).get(j), actual.get(i).get(j));
			}
		}
		assertEquals(expected, actual);
	}


	@Test
	public void testGroupListOfIntegers() {
		List<List<Integer>> lists = new ArrayList<>();
		// Single item
		lists.add(Arrays.asList(new Integer[]{1}));
		// All separate
		lists.add(Arrays.asList(new Integer[]{1, 3, 5, 7}));
		// Starts with separate, ends with combined
		lists.add(Arrays.asList(new Integer[]{1, 3, 4, 5, 7, 9, 10, 11}));
		// Starts with combined, ends with separate
		lists.add(Arrays.asList(new Integer[]{1, 2, 3, 5, 6, 7, 9}));
		// All combined
		lists.add(Arrays.asList(new Integer[]{1, 2, 3, 5, 6, 7, 9, 10, 11}));
		
		List<List<List<Integer>>> expected = new ArrayList<>();
		//
		List<List<Integer>> expected0 = new ArrayList<>();
		expected0.add(Arrays.asList(new Integer[]{1}));
		expected.add(expected0);
		//
		List<List<Integer>> expected1 = new ArrayList<>();
		expected1.add(Arrays.asList(new Integer[]{1}));
		expected1.add(Arrays.asList(new Integer[]{3}));
		expected1.add(Arrays.asList(new Integer[]{5}));
		expected1.add(Arrays.asList(new Integer[]{7}));
		expected.add(expected1);
		//
		List<List<Integer>> expected2 = new ArrayList<>();
		expected2.add(Arrays.asList(new Integer[]{1}));
		expected2.add(Arrays.asList(new Integer[]{3, 4, 5}));
		expected2.add(Arrays.asList(new Integer[]{7}));
		expected2.add(Arrays.asList(new Integer[]{9, 10, 11}));
		expected.add(expected2);
		//
		List<List<Integer>> expected3 = new ArrayList<>();
		expected3.add(Arrays.asList(new Integer[]{1, 2, 3}));
		expected3.add(Arrays.asList(new Integer[]{5, 6, 7}));
		expected3.add(Arrays.asList(new Integer[]{9}));
		expected.add(expected3);
		//
		List<List<Integer>> expected4 = new ArrayList<>();
		expected4.add(Arrays.asList(new Integer[]{1, 2, 3}));
		expected4.add(Arrays.asList(new Integer[]{5, 6, 7}));
		expected4.add(Arrays.asList(new Integer[]{9, 10, 11}));
		expected.add(expected4);
		
		List<List<List<Integer>>> actual = new ArrayList<>();
		for (List<Integer> l : lists) {
			actual.add(ToolBox.groupListOfIntegers(l));
		}
		
		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i).size(), actual.get(i).size());
			for (int j = 0; j < expected.get(i).size(); j++) {
				assertEquals(expected.get(i).get(j).size(), actual.get(i).get(j).size());
				for (int k = 0; k < expected.get(i).get(j).size(); k++) {
					assertEquals(expected.get(i).get(j).get(k), actual.get(i).get(j).get(k));
				}
			}
		}
		assertEquals(expected, actual);
	}


	@Test
	public void testMax() {
		List<double[]> doubles = new ArrayList<double[]>();
		doubles.add(new double[]{1.1, 1.2, 1.3, 1.4, 1.5});
		doubles.add(new double[]{1.5, 1.2, 1.3, 1.4, 1.5});
		doubles.add(new double[]{1.1, 1.1, 1.1, 1.1, 1.1});
		
		List<Double> expected = Arrays.asList(new Double[]{1.5, 1.5, 1.1});
		
		List<Double> actual = new ArrayList<Double>();
		for (double[] d : doubles) {
			actual.add(ToolBox.max(d));
		}
		
		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i), actual.get(i));
		}
	}


	@Test
	public void testMaxIndices() {
		List<double[]> doubles = new ArrayList<double[]>();
		doubles.add(new double[]{1.1, 1.2, 1.3, 1.4, 1.5});
		doubles.add(new double[]{1.5, 1.2, 1.3, 1.4, 1.5});
		doubles.add(new double[]{1.1, 1.1, 1.1, 1.1, 1.1});
		
		List<List<Integer>> expected = new ArrayList<List<Integer>>();
		expected.add(Arrays.asList(new Integer[]{4}));
		expected.add(Arrays.asList(new Integer[]{0, 4}));
		expected.add(Arrays.asList(new Integer[]{0, 1, 2, 3, 4}));
		
		List<List<Integer>> actual = new ArrayList<List<Integer>>();
		for (double[] d : doubles) {
			actual.add(ToolBox.maxIndices(d));
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


	@Test
	public void testTransposeListOfLists() {
		List<List<Integer>> l = new ArrayList<List<Integer>>();
		l.add(Arrays.asList(new Integer[]{100, 10, 1}));
		l.add(Arrays.asList(new Integer[]{200, 20, 2}));
		l.add(Arrays.asList(new Integer[]{300, 30, 3}));
		l.add(Arrays.asList(new Integer[]{400, 40, 4}));
		
		List<List<Integer>> expected = new ArrayList<List<Integer>>();
		expected.add(Arrays.asList(new Integer[]{100, 200, 300, 400}));
		expected.add(Arrays.asList(new Integer[]{10, 20, 30, 40}));
		expected.add(Arrays.asList(new Integer[]{1, 2, 3, 4}));

		List<List<Integer>> actual = ToolBox.transposeListOfLists(l);

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i).size(), actual.get(i).size());
			for (int j = 0; j < expected.get(i).size(); j++) {
				assertEquals(expected.get(i).get(j), actual.get(i).get(j));
			}
		}
		assertEquals(expected, actual);
	}


	@Test
	public void testGetCombinations() {
		List<List<Integer[]>> expected = new ArrayList<>();
		expected.add(Arrays.asList(new Integer[][]{
			new Integer[]{0, 0}, new Integer[]{1, 1}, new Integer[]{2, 2}}));
		expected.add(Arrays.asList(new Integer[][]{
			new Integer[]{0, 0}, new Integer[]{1, 2}, new Integer[]{2, 1}}));
		//
		expected.add(Arrays.asList(new Integer[][]{
			new Integer[]{0, 1}, new Integer[]{1, 0}, new Integer[]{2, 2}}));
		expected.add(Arrays.asList(new Integer[][]{
			new Integer[]{0, 1}, new Integer[]{1, 2}, new Integer[]{2, 0}}));
		//
		expected.add(Arrays.asList(new Integer[][]{
			new Integer[]{0, 2}, new Integer[]{1, 1}, new Integer[]{2, 0}}));
		expected.add(Arrays.asList(new Integer[][]{
			new Integer[]{0, 2}, new Integer[]{1, 0}, new Integer[]{2, 1}}));
		
		List<List<Integer[]>> actual = ToolBox.getCombinations(3);
		
		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {			
			assertEquals(expected.get(i).size(), actual.get(i).size());
			for (int j = 0; j < expected.get(i).size(); j++) {
				assertEquals(expected.get(i).get(j).length, actual.get(i).get(j).length);
				for (int k = 0; k < expected.get(i).get(j).length; k++) {
					assertEquals(expected.get(i).get(j)[k], actual.get(i).get(j)[k]);
				}
			}
		}
	}


	@Test
	public void testGetPermutations() {
		List<List<List<Integer>>> expected = new ArrayList<>();
		List<List<Integer>> expectedThree = new ArrayList<>();
		expectedThree.add(Arrays.asList(new Integer[]{0, 1, 2}));
		expectedThree.add(Arrays.asList(new Integer[]{0, 2, 1}));
		//
		expectedThree.add(Arrays.asList(new Integer[]{1, 0, 2}));
		expectedThree.add(Arrays.asList(new Integer[]{1, 2, 0}));
		//
		expectedThree.add(Arrays.asList(new Integer[]{2, 1, 0}));
		expectedThree.add(Arrays.asList(new Integer[]{2, 0, 1}));
		expected.add(expectedThree);
		
		List<List<Integer>> expectedFour = new ArrayList<>();
		expectedFour.add(Arrays.asList(new Integer[]{0, 1, 2, 3}));
		expectedFour.add(Arrays.asList(new Integer[]{0, 1, 3, 2}));
		expectedFour.add(Arrays.asList(new Integer[]{0, 2, 1, 3}));
		expectedFour.add(Arrays.asList(new Integer[]{0, 2, 3, 1}));
		expectedFour.add(Arrays.asList(new Integer[]{0, 3, 2, 1}));
		expectedFour.add(Arrays.asList(new Integer[]{0, 3, 1, 2}));
		//
		expectedFour.add(Arrays.asList(new Integer[]{1, 0, 2, 3}));
		expectedFour.add(Arrays.asList(new Integer[]{1, 0, 3, 2}));
		expectedFour.add(Arrays.asList(new Integer[]{1, 2, 0, 3}));
		expectedFour.add(Arrays.asList(new Integer[]{1, 2, 3, 0}));
		expectedFour.add(Arrays.asList(new Integer[]{1, 3, 2, 0}));
		expectedFour.add(Arrays.asList(new Integer[]{1, 3, 0, 2}));
		//
		expectedFour.add(Arrays.asList(new Integer[]{2, 1, 0, 3}));
		expectedFour.add(Arrays.asList(new Integer[]{2, 1, 3, 0}));
		expectedFour.add(Arrays.asList(new Integer[]{2, 0, 1, 3}));
		expectedFour.add(Arrays.asList(new Integer[]{2, 0, 3, 1}));
		expectedFour.add(Arrays.asList(new Integer[]{2, 3, 0, 1}));
		expectedFour.add(Arrays.asList(new Integer[]{2, 3, 1, 0}));
		//
		expectedFour.add(Arrays.asList(new Integer[]{3, 1, 2, 0}));
		expectedFour.add(Arrays.asList(new Integer[]{3, 1, 0, 2}));
		expectedFour.add(Arrays.asList(new Integer[]{3, 2, 1, 0}));
		expectedFour.add(Arrays.asList(new Integer[]{3, 2, 0, 1}));
		expectedFour.add(Arrays.asList(new Integer[]{3, 0, 2, 1}));
		expectedFour.add(Arrays.asList(new Integer[]{3, 0, 1, 2}));
		expected.add(expectedFour);
		
		List<List<List<Integer>>> actual = new ArrayList<>();
		actual.add(ToolBox.getPermutations(Arrays.asList(new Integer[]{0, 1, 2})));
		actual.add(ToolBox.getPermutations(Arrays.asList(new Integer[]{0, 1, 2, 3})));
		
		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i).size(), actual.get(i).size());
			for (int j = 0; j < expected.get(i).size(); j++) {
				assertEquals(expected.get(i).get(j).size(), actual.get(i).get(j).size());
				for (int k = 0; k < expected.get(i).get(j).size(); k++) {
					assertEquals(expected.get(i).get(j).get(k), actual.get(i).get(j).get(k));
				}
			}
		}	
	}


	@Test
	public void testFindAllUniqueCombinations() {
		List<Integer> x = Arrays.asList(new Integer[]{0, 1, 2, 3});
		List<Integer> y = Arrays.asList(new Integer[]{0, 1, 2, 3});
		
		ToolBox.findAllUniqueCombinations(x, y);
	}


	@Test
	public void testGetSubsets() {	
		List<Integer> set = Arrays.asList(new Integer[]{0, 1, 2});
		
		List<List<Integer>> expected = new ArrayList<>();
		expected.add(Arrays.asList(new Integer[]{0, 1, 2}));
		
		System.out.println(ToolBox.getSubsets(set, 2));
	}


	@Test
	public void testRemoveItemsAtIndices() {
		List<List<Integer>> ints = new ArrayList<>();
		ints.add(Arrays.asList(new Integer[]{0, 0}));
		ints.add(Arrays.asList(new Integer[]{1, 1}));
		ints.add(Arrays.asList(new Integer[]{2, 2}));
		ints.add(null);
		ints.add(Arrays.asList(new Integer[]{4, 4}));
		ints.add(Arrays.asList(new Integer[]{5, 5}));
		ints.add(Arrays.asList(new Integer[]{6, 6}));
		ints.add(null);
		ints.add(Arrays.asList(new Integer[]{8, 8}));
		ints.add(Arrays.asList(new Integer[]{9, 9}));

		List<List<Double>> doubles = new ArrayList<>();
		doubles.add(Arrays.asList(new Double[]{0.0, 0.0})); 
		doubles.add(Arrays.asList(new Double[]{1.0, 1.0}));
		doubles.add(Arrays.asList(new Double[]{2.0, 2.0})); //
		doubles.add(null); //
		doubles.add(Arrays.asList(new Double[]{4.0, 4.0}));
		doubles.add(Arrays.asList(new Double[]{5.0, 5.0})); //
		doubles.add(Arrays.asList(new Double[]{6.0, 6.0}));
		doubles.add(null);
		doubles.add(Arrays.asList(new Double[]{8.0, 8.0}));
		doubles.add(Arrays.asList(new Double[]{9.0, 9.0})); //
		
		List<Integer[]> intArrs = new ArrayList<Integer[]>();
		intArrs.add(new Integer[]{0, 0});
		intArrs.add(new Integer[]{1, 1});
		intArrs.add(new Integer[]{2, 2}); //
		intArrs.add(null); //
		intArrs.add(new Integer[]{4, 4});
		intArrs.add(new Integer[]{5, 5}); //
		intArrs.add(new Integer[]{6, 6});
		intArrs.add(null);
		intArrs.add(new Integer[]{8, 8});
		intArrs.add(new Integer[]{9, 9}); //

		List<List<Integer>> expectedInts = new ArrayList<>();
		expectedInts.add(Arrays.asList(new Integer[]{0, 0}));
		expectedInts.add(Arrays.asList(new Integer[]{1, 1}));
		expectedInts.add(Arrays.asList(new Integer[]{4, 4}));
		expectedInts.add(Arrays.asList(new Integer[]{6, 6}));
		expectedInts.add(null);
		expectedInts.add(Arrays.asList(new Integer[]{8, 8}));

		List<List<Double>> expectedDoubles = new ArrayList<>();
		expectedDoubles.add(Arrays.asList(new Double[]{0.0, 0.0}));
		expectedDoubles.add(Arrays.asList(new Double[]{1.0, 1.0}));
		expectedDoubles.add(Arrays.asList(new Double[]{4.0, 4.0}));
		expectedDoubles.add(Arrays.asList(new Double[]{6.0, 6.0}));
		expectedDoubles.add(null);
		expectedDoubles.add(Arrays.asList(new Double[]{8.0, 8.0}));
		
		List<Integer[]> expectedIntArrs = new ArrayList<Integer[]>();
		expectedIntArrs.add(new Integer[]{0, 0});
		expectedIntArrs.add(new Integer[]{1, 1});
		expectedIntArrs.add(new Integer[]{4, 4});
		expectedIntArrs.add(new Integer[]{6, 6});
		expectedIntArrs.add(null);
		expectedIntArrs.add(new Integer[]{8, 8});

		List<Integer> indices = Arrays.asList(new Integer[]{2, 3, 5, 9});
		List<List<Integer>> actualInts = ToolBox.removeItemsAtIndices(ints, indices);
		List<List<Double>> actualDoubles = ToolBox.removeItemsAtIndices(doubles, indices);
		List<Integer[]> actualIntArrs = ToolBox.removeItemsAtIndices(intArrs, indices);

		assertEquals(expectedInts.size(), actualInts.size());
		for (int i = 0; i < expectedInts.size(); i++) {
			if (expectedInts.get(i) != null) {
				assertEquals(expectedInts.get(i).size(), actualInts.get(i).size());
				for (int j = 0; j < expectedInts.get(i).size(); j++) {
					assertEquals(expectedInts.get(i).get(j), actualInts.get(i).get(j));
				}
			}
			else {
				assertEquals(expectedInts.get(i), actualInts.get(i));
			}
		}
		assertEquals(expectedInts, actualInts);
		//
		assertEquals(expectedDoubles.size(), actualDoubles.size());
		for (int i = 0; i < expectedDoubles.size(); i++) {
			if (expectedDoubles.get(i) != null) {
				assertEquals(expectedDoubles.get(i).size(), actualDoubles.get(i).size());
				for (int j = 0; j < expectedDoubles.get(i).size(); j++) {
					assertEquals(expectedDoubles.get(i).get(j), actualDoubles.get(i).get(j));
				}
			}
			else {
				assertEquals(expectedDoubles.get(i), actualDoubles.get(i)); 
			}
		}
		assertEquals(expectedDoubles, actualDoubles);
		//
		assertEquals(expectedIntArrs.size(), actualIntArrs.size());
		for (int i = 0; i < expectedIntArrs.size(); i++) {
			if (expectedIntArrs.get(i) != null) {
				assertEquals(expectedIntArrs.get(i).length, actualIntArrs.get(i).length);
				for (int j = 0; j < expectedIntArrs.get(i).length; j++) {
					assertEquals(expectedIntArrs.get(i)[j], actualIntArrs.get(i)[j]);
				}
			}
			else {
				assertNull(expectedIntArrs.get(i));
				assertNull(actualIntArrs.get(i));
//				assertEquals(expectedIntArrs.get(i), actualIntArrs.get(i));
			}
		}
	}


	@Test
	public void testRemoveDuplicateItems() {
		List<List<Integer>> ints = new ArrayList<>();
		ints.add(Arrays.asList(new Integer[]{0, 0}));
		ints.add(Arrays.asList(new Integer[]{1, 1}));
		ints.add(Arrays.asList(new Integer[]{2, 2}));
		ints.add(null);
		ints.add(Arrays.asList(new Integer[]{0, 0}));
		ints.add(Arrays.asList(new Integer[]{5, 5}));
		ints.add(Arrays.asList(new Integer[]{5, 5}));
		ints.add(null);
		ints.add(Arrays.asList(new Integer[]{8, 8}));
		ints.add(Arrays.asList(new Integer[]{2, 2}));
		//
		List<List<Double>> doubles = new ArrayList<>();
		doubles.add(Arrays.asList(new Double[]{0.0, 0.0})); 
		doubles.add(Arrays.asList(new Double[]{1.0, 1.0}));
		doubles.add(Arrays.asList(new Double[]{2.0, 2.0}));
		doubles.add(null);
		doubles.add(Arrays.asList(new Double[]{0.0, 0.0}));
		doubles.add(Arrays.asList(new Double[]{5.0, 5.0}));
		doubles.add(Arrays.asList(new Double[]{5.0, 5.0}));
		doubles.add(null);
		doubles.add(Arrays.asList(new Double[]{8.0, 8.0}));
		doubles.add(Arrays.asList(new Double[]{2.0, 2.0}));
		
		List<List<Integer>> expected1 = new ArrayList<>();
		expected1.add(Arrays.asList(new Integer[]{0, 0}));
		expected1.add(Arrays.asList(new Integer[]{1, 1}));
		expected1.add(Arrays.asList(new Integer[]{2, 2}));
		expected1.add(null);
		expected1.add(Arrays.asList(new Integer[]{5, 5}));
		expected1.add(Arrays.asList(new Integer[]{8, 8}));
		//
		List<List<Double>> expected2 = new ArrayList<>();
		expected2.add(Arrays.asList(new Double[]{0.0, 0.0})); 
		expected2.add(Arrays.asList(new Double[]{1.0, 1.0}));
		expected2.add(Arrays.asList(new Double[]{2.0, 2.0}));
		expected2.add(null);
		expected2.add(Arrays.asList(new Double[]{5.0, 5.0}));
		expected2.add(Arrays.asList(new Double[]{8.0, 8.0}));
		
		List<List<Integer>> actual1 = ToolBox.removeDuplicateItems(ints);
		List<List<Double>> actual2 = ToolBox.removeDuplicateItems(doubles);
		
		assertEquals(expected1.size(), actual1.size());
		for (int i = 0; i < expected1.size(); i++) {
			if (expected1.get(i) != null) {
				assertEquals(expected1.get(i).size(), actual1.get(i).size());
				for (int j = 0; j < expected1.get(i).size(); j++) {
					assertEquals(expected1.get(i).get(j), actual1.get(i).get(j));
				}
			}
			else {
				assertEquals(expected1.get(i), actual1.get(i));
			}
		}
		assertEquals(expected1, actual1);
		//
		assertEquals(expected2.size(), actual2.size());
		for (int i = 0; i < expected2.size(); i++) {
			if (expected2.get(i) != null) {
				assertEquals(expected2.get(i).size(), actual2.get(i).size());
				for (int j = 0; j < expected2.get(i).size(); j++) {
					assertEquals(expected2.get(i).get(j), actual2.get(i).get(j));
				}
			}
			else {
				assertEquals(expected2.get(i), actual2.get(i));
			}
		}
		assertEquals(expected2, actual2);
	}


	@Test
	public void testGetRange() {
		List<List<Integer>> expected = Arrays.asList(
			Arrays.asList(new Integer[]{-3, -2, -1, 0, 1, 2}),
			Arrays.asList(new Integer[]{3, 4, 5, 6, 7, 8})
		);

		List<List<Integer>> actual = Arrays.asList(
			ToolBox.getRange(-3, 3),
			ToolBox.getRange(3, 9)
		);

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i).size(), actual.get(i).size());
			for (int j = 0; j < expected.get(i).size(); j++) {
				assertEquals(expected.get(i).get(j), actual.get(i).get(j));
			}
		}
		assertEquals(expected, actual);	
	}


	@Test
	public void testGetDuplicates() {		
		List<List<String>> strs = Arrays.asList(
			Arrays.asList("a", "b", "c", "d"),
			Arrays.asList("a", "b", "c", "c", "d"),
			Arrays.asList("a", "b", "b", "c", "c")
		);
		//
		List<List<Integer>> ints = Arrays.asList(
			Arrays.asList(1, 2, 3, 4),
			Arrays.asList(1, 2, 3, 3, 4),
			Arrays.asList(1, 2, 2, 3, 3)
		);

		List<List<String>> expectedStr = Arrays.asList(
			new ArrayList<String>(),
			Arrays.asList("c"),
			Arrays.asList("b", "c")
		);
		//
		List<List<Integer>> expectedInt = Arrays.asList(
			new ArrayList<Integer>(),
			Arrays.asList(3),
			Arrays.asList(2, 3)
		);

		List<List<String>> actualStr = new ArrayList<>();
		for (List<String> l : strs) {
			actualStr.add(ToolBox.getDuplicates(l));
		}
		//
		List<List<Integer>> actualInt = new ArrayList<>();
		for (List<Integer> l : ints) {
			actualInt.add(ToolBox.getDuplicates(l));
		}
		
		assertEquals(expectedStr.size(), actualStr.size());
		for (int i = 0; i < expectedStr.size(); i++) {
			assertEquals(expectedStr.get(i).size(), actualStr.get(i).size());
			for (int j = 0; j < expectedStr.get(i).size(); j++) {
				assertEquals(expectedStr.get(i).get(j), actualStr.get(i).get(j));
			}
		}
		assertEquals(expectedStr, actualStr);
		//
		assertEquals(expectedInt.size(), actualInt.size());
		for (int i = 0; i < expectedInt.size(); i++) {
			assertEquals(expectedInt.get(i).size(), actualInt.get(i).size());
			for (int j = 0; j < expectedInt.get(i).size(); j++) {
				assertEquals(expectedInt.get(i).get(j), actualInt.get(i).get(j));
			}
		}
		assertEquals(expectedInt, actualInt);
	}


	@Test
	public void testGetItemsAtIndex() {
		List<Integer[]> list1 = new ArrayList<Integer[]>();
		list1.add(new Integer[]{3, 20});
		list1.add(new Integer[]{1, 50});
		list1.add(new Integer[]{5, 10});
		list1.add(new Integer[]{2, 30});
		
		List<Double[]> list2 = new ArrayList<Double[]>();
		list2.add(new Double[]{3.0, 20.0});
		list2.add(new Double[]{1.0, 50.0});
		list2.add(new Double[]{5.0, 10.0});
		list2.add(new Double[]{2.0, 30.0});
		
		List<Integer> expected1 = Arrays.asList(new Integer[]{3, 1, 5, 2, 20, 50, 10, 30});		
		List<Double> expected2 = 
			Arrays.asList(new Double[]{3.0, 1.0, 5.0, 2.0, 20.0, 50.0, 10.0, 30.0});
		
		List<Integer> actual1 = ToolBox.getItemsAtIndex(list1, 0);
		actual1.addAll(ToolBox.getItemsAtIndex(list1, 1));
		List<Double> actual2 = ToolBox.getItemsAtIndex(list2, 0);
		actual2.addAll(ToolBox.getItemsAtIndex(list2, 1));
		
		assertEquals(expected1.size(), actual1.size());
		assertEquals(expected1, actual1);
		assertEquals(expected2.size(), actual2.size());
		assertEquals(expected2, actual2);
	}


	@Test
	public void testGetItemsAtIndexRational() {
		List<Rational[]> aList = new ArrayList<>();
		aList.add(new Rational[]{new Rational(3, 1), new Rational(20, 1)});
		aList.add(new Rational[]{new Rational(1, 1), new Rational(50, 1)});
		aList.add(new Rational[]{new Rational(5, 1), new Rational(10, 1)});
		aList.add(new Rational[]{new Rational(2, 1), new Rational(30, 1)});
		
		List<Rational> expected = Arrays.asList(new Rational[]{
			new Rational(3, 1), new Rational(1, 1), new Rational(5, 1), new Rational(2, 1),
			new Rational(20, 1), new Rational(50, 1), new Rational(10, 1), new Rational(30, 1),
		});
		
		List<Rational> actual = ToolBox.getItemsAtIndexRational(aList, 0);
		actual.addAll(ToolBox.getItemsAtIndexRational(aList, 1));
		
		assertEquals(expected.size(), actual.size());
		assertEquals(expected, actual);	
	}


	@Test
	public void testGetIndexOfNthItem() {
		List<Integer> l = Arrays.asList(new Integer[]{10, 11, 10, 12, 10, 12, 11, 11});
		
		// 1st of 10; 1st of 12; 2nd of 12; 3rd of 11 
		List<Integer> expected = Arrays.asList(new Integer[]{0, 3, 5, 7, -1});
		
		List<Integer> actual = new ArrayList<Integer>();
		actual.add(ToolBox.getIndexOfNthItem(l, 10, 1));
		actual.add(ToolBox.getIndexOfNthItem(l, 12, 1));
		actual.add(ToolBox.getIndexOfNthItem(l, 12, 2));
		actual.add(ToolBox.getIndexOfNthItem(l, 11, 3));
		actual.add(ToolBox.getIndexOfNthItem(l, 13, 1));
		
		assertEquals(expected.size(), actual.size());
		assertEquals(expected, actual);
	}


	@Test
	public void testReorderByIndex() {
		List<Integer> inds = Arrays.asList(new Integer[]{2, 4, 1, 3, 0});
		
		List<double[]> l1 = new ArrayList<>();
		l1.add(new double[]{0.0, 0.0, 0.0});
		l1.add(new double[]{1.0, 1.0, 1.0});
		l1.add(new double[]{2.0, 2.0, 2.0});
		l1.add(new double[]{3.0, 3.0, 3.0});
		l1.add(new double[]{4.0, 4.0, 4.0});

		List<double[]> expected1 = new ArrayList<>();
		expected1.add(new double[]{4.0, 4.0, 4.0});
		expected1.add(new double[]{2.0, 2.0, 2.0});
		expected1.add(new double[]{0.0, 0.0, 0.0});
		expected1.add(new double[]{3.0, 3.0, 3.0});
		expected1.add(new double[]{1.0, 1.0, 1.0});
		
		List<Integer> l2 = new ArrayList<>();
		l2.add(0); l2.add(1); l2.add(2); l2.add(3); l2.add(4);

		List<Integer> expected2 = new ArrayList<>();
		expected2.add(4); expected2.add(2); expected2.add(0);
		expected2.add(3); expected2.add(1);
		
		List<double[]> actual1 = ToolBox.reorderByIndex(l1, inds);
		List<Integer> actual2 = ToolBox.reorderByIndex(l2, inds);

		assertEquals(expected1.size(), actual1.size());
		for (int i = 0; i < expected1.size(); i++) {
			assertEquals(expected1.get(i).length, actual1.get(i).length);
			for (int j = 0; j < expected1.get(i).length; j++) {
				assertEquals(expected1.get(i)[j], actual1.get(i)[j], delta);
			}
		}
		assertEquals(expected2.size(), actual2.size());
		for (int i = 0; i < expected2.size(); i++) {
			assertEquals(expected2.get(i), actual2.get(i));
		}
	}


	@Test
	public void testSortBy() {
		List<Integer[]> toSort = new ArrayList<Integer[]>();
		toSort.add(new Integer[]{3, 20});
		toSort.add(new Integer[]{1, 50});
		toSort.add(new Integer[]{5, 10});
		toSort.add(new Integer[]{2, 30});
		
		List<Integer[]> expected = new ArrayList<Integer[]>();
		expected.add(new Integer[]{1, 50});
		expected.add(new Integer[]{2, 30});
		expected.add(new Integer[]{3, 20});
		expected.add(new Integer[]{5, 10});
		//
		expected.add(new Integer[]{5, 10});
		expected.add(new Integer[]{3, 20});
		expected.add(new Integer[]{2, 30});
		expected.add(new Integer[]{1, 50});
		
		List<Integer[]> actual = ToolBox.sortBy(toSort, 0);
		toSort = new ArrayList<Integer[]>();
		toSort.add(new Integer[]{3, 20});
		toSort.add(new Integer[]{1, 50});
		toSort.add(new Integer[]{5, 10});
		toSort.add(new Integer[]{2, 30});
		actual.addAll(ToolBox.sortBy(toSort, 1));

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i).length, actual.get(i).length);
			for (int j = 0; j < expected.get(i).length; j++) {
				assertEquals(expected.get(i)[j], actual.get(i)[j]);
			}
		}
	}


	@Test
	public void testSortByAlt() {
		List<Integer[]> toSort = Arrays.asList(
			new Integer[]{3, 20},
			new Integer[]{1, 50},
			new Integer[]{5, 10},
			new Integer[]{2, 30}
		);

		List<Integer[]> expected = Arrays.asList(
			// Addition
			new Integer[]{5, 10},
			new Integer[]{3, 20},
			new Integer[]{2, 30},
			new Integer[]{1, 50},
			// Subtraction
			new Integer[]{1, 50},
			new Integer[]{2, 30},
			new Integer[]{3, 20},
			new Integer[]{5, 10},
			// Multiplication
			new Integer[]{1, 50},
			new Integer[]{5, 10},
			new Integer[]{2, 30},
			new Integer[]{3, 20},
			// Division
			new Integer[]{1, 50},
			new Integer[]{2, 30},
			new Integer[]{3, 20},
			new Integer[]{5, 10}
		);

		List<Integer[]> actual = new ArrayList<>();
		actual.addAll(ToolBox.sortBy(toSort, 0, 1, "addition"));
		actual.addAll(ToolBox.sortBy(toSort, 0, 1, "subtraction"));
		actual.addAll(ToolBox.sortBy(toSort, 0, 1, "multiplication"));
		actual.addAll(ToolBox.sortBy(toSort, 0, 1, "division"));

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i).length, actual.get(i).length);
			for (int j = 0; j < expected.get(i).length; j++) {
				assertEquals(expected.get(i)[j], actual.get(i)[j]);
			}
		}
	}


	@Test
	public void testSortByRational() {
		List<Rational[]> toSort = new ArrayList<Rational[]>();
		toSort.add(new Rational[]{new Rational(3, 2), new Rational(20, 2)});
		toSort.add(new Rational[]{new Rational(1, 2), new Rational(50, 2)});
		toSort.add(new Rational[]{new Rational(5, 2), new Rational(10, 2)});
		toSort.add(new Rational[]{new Rational(2, 2), new Rational(30, 2)});
		
		List<Rational[]> expected = new ArrayList<Rational[]>();
		expected.add(new Rational[]{new Rational(1, 2), new Rational(50, 2)});
		expected.add(new Rational[]{new Rational(2, 2), new Rational(30, 2)});
		expected.add(new Rational[]{new Rational(3, 2), new Rational(20, 2)});
		expected.add(new Rational[]{new Rational(5, 2), new Rational(10, 2)});
		//
		expected.add(new Rational[]{new Rational(5, 2), new Rational(10, 2)});
		expected.add(new Rational[]{new Rational(3, 2), new Rational(20, 2)});
		expected.add(new Rational[]{new Rational(2, 2), new Rational(30, 2)});
		expected.add(new Rational[]{new Rational(1, 2), new Rational(50, 2)});
		
		List<Rational[]> actual = ToolBox.sortByRational(toSort, 0);
		toSort = new ArrayList<Rational[]>();
		toSort.add(new Rational[]{new Rational(3, 2), new Rational(20, 2)});
		toSort.add(new Rational[]{new Rational(1, 2), new Rational(50, 2)});
		toSort.add(new Rational[]{new Rational(5, 2), new Rational(10, 2)});
		toSort.add(new Rational[]{new Rational(2, 2), new Rational(30, 2)});
		actual.addAll(ToolBox.sortByRational(toSort, 1));

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i).length, actual.get(i).length);
			for (int j = 0; j < expected.get(i).length; j++) {
				assertEquals(expected.get(i)[j], actual.get(i)[j]);
			}
		}
	}


	@Test
	public void testSortBySubstring() {
		List<String> num = Arrays.asList(
			"bla-num_3-bla",
			"bla-num_1-bla",
			"bla-num_2-bla"
		);
		List<String> str = Arrays.asList(
			"bla-num_C-bla",
			"bla-num_A-bla",
			"bla-num_B-bla"
		);

		List<String> expected = Arrays.asList(
			"bla-num_1-bla",
			"bla-num_2-bla",
			"bla-num_3-bla",
			"bla-num_A-bla",
			"bla-num_B-bla",
			"bla-num_C-bla"
		);

		List<String> actual = new ArrayList<>();
		actual.addAll(ToolBox.sortBySubstring(num, "num_", null, "number"));
		actual.addAll(ToolBox.sortBySubstring(str, "num_", "-bla", "string"));

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i), actual.get(i));
		}
	}


	@Test
	public void testExtractFromString() {
		List<String> num = Arrays.asList(
			"num_1-bla-bla",
			"bla-num_2-bla",
			"bla-bla-num_3",
			"bla-bla-numm_3"
		);
		List<String> str = Arrays.asList(
			"num_A-bla-bla",
			"bla-num_B-bla",
			"bla-bla-num_C",
			"bla-bla-numm_C"
		);

		List<String> expected = Arrays.asList("1", "2", "3", null, "A", "B", "C", null);

		List<String> actual = new ArrayList<>();
		num.forEach(s -> actual.add(ToolBox.extractFromString(s, "num_", null, "number")));
		str.forEach(s -> actual.add(ToolBox.extractFromString(s, "num_", "-bla", "string")));

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i), actual.get(i));
		}
	}


	@Test
	public void testSortByString() {
		// int
		String[] a = new String[]{"1", "50", "1.50"};
		String[] b = new String[]{"2", "30", "2.30"};
		String[] c = new String[]{"3", "20", "3.20"};
		String[] d = new String[]{"5", "10", "5.10"};
		List<String[]> toSort = Arrays.asList(c, a, d, b);
		List<String[]> expected = Arrays.asList(
			a, b, c, d, 
			//
			d, c, b, a,
			// 
			a, b, c, d,
			//
			d, c, b, a
		);
		List<String[]> actual = ToolBox.sortByString(new ArrayList<String[]>(toSort), 0, "int", null, null);
		actual.addAll(ToolBox.sortByString(new ArrayList<String[]>(toSort), 1, "int", null, null));
		actual.addAll(ToolBox.sortByString(new ArrayList<String[]>(toSort), 2, "int", ".", "first"));
		actual.addAll(ToolBox.sortByString(new ArrayList<String[]>(toSort), 2, "int", ".", "last"));

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i).length, actual.get(i).length);
			for (int j = 0; j < expected.get(i).length; j++) {
				assertEquals(expected.get(i)[j], actual.get(i)[j]);
			}
		}

		// Rational
		String[] ra = new String[]{"0/1", "20"};
		String[] rb = new String[]{"1/2", "30"}; 
		String[] rc = new String[]{"1/1", "50"}; 
		String[] rd = new String[]{"3/2", "10"}; 	
		toSort = Arrays.asList(rc, rd, rb, ra);		
		expected = Arrays.asList(ra, rb, rc, rd);
		actual = ToolBox.sortByString(toSort, 0, "Rational", null, null);

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i).length, actual.get(i).length);
			for (int j = 0; j < expected.get(i).length; j++) {
				assertEquals(expected.get(i)[j], actual.get(i)[j]);
			}
		}
	}


	@Test
	public void testBubbleSort() {
		List<List<Integer>> listToSort0 = new ArrayList<List<Integer>>();
		listToSort0.add(Arrays.asList(new Integer[]{30, 3}));
		listToSort0.add(Arrays.asList(new Integer[]{10, 1}));
		listToSort0.add(Arrays.asList(new Integer[]{20, 2}));
		listToSort0.add(Arrays.asList(new Integer[]{40, 4}));

		List<List<Integer>> listToSort1 = new ArrayList<List<Integer>>();
		listToSort1.add(Arrays.asList(new Integer[]{30, 3}));
		listToSort1.add(Arrays.asList(new Integer[]{10, 2}));
		listToSort1.add(Arrays.asList(new Integer[]{10, 1}));
		listToSort1.add(Arrays.asList(new Integer[]{40, 4}));

		List<List<Integer>> listToSort2 = new ArrayList<List<Integer>>();
		listToSort2.add(Arrays.asList(new Integer[]{30, 3}));
		listToSort2.add(Arrays.asList(new Integer[]{10, 1}));
		listToSort2.add(Arrays.asList(new Integer[]{10, 2}));
		listToSort2.add(Arrays.asList(new Integer[]{30, 0}));
		
		List<List<Integer>> listToSort3 = new ArrayList<List<Integer>>();
		listToSort3.add(Arrays.asList(new Integer[]{30, 3}));
		listToSort3.add(Arrays.asList(new Integer[]{10, 1}));
		listToSort3.add(Arrays.asList(new Integer[]{20, 2}));
		listToSort3.add(Arrays.asList(new Integer[]{40, 4}));

		List<List<Integer>> listToSort4 = new ArrayList<List<Integer>>();
		listToSort4.add(Arrays.asList(new Integer[]{30, 3}));
		listToSort4.add(Arrays.asList(new Integer[]{20, 1}));
		listToSort4.add(Arrays.asList(new Integer[]{10, 1}));
		listToSort4.add(Arrays.asList(new Integer[]{40, 4}));

		List<List<Integer>> listToSort5 = new ArrayList<List<Integer>>();
		listToSort5.add(Arrays.asList(new Integer[]{40, 3}));
		listToSort5.add(Arrays.asList(new Integer[]{10, 1}));
		listToSort5.add(Arrays.asList(new Integer[]{20, 1}));
		listToSort5.add(Arrays.asList(new Integer[]{30, 3}));

		List<List<List<Integer>>> expected = new ArrayList<List<List<Integer>>>();
		List<List<Integer>> expected0 = new ArrayList<List<Integer>>();
		expected0.add(Arrays.asList(new Integer[]{10, 1}));
		expected0.add(Arrays.asList(new Integer[]{20, 2}));
		expected0.add(Arrays.asList(new Integer[]{30, 3}));
		expected0.add(Arrays.asList(new Integer[]{40, 4}));

		List<List<Integer>> expected1 = new ArrayList<List<Integer>>();
		expected1.add(Arrays.asList(new Integer[]{10, 2}));
		expected1.add(Arrays.asList(new Integer[]{10, 1}));
		expected1.add(Arrays.asList(new Integer[]{30, 3}));
		expected1.add(Arrays.asList(new Integer[]{40, 4}));

		List<List<Integer>> expected2 = new ArrayList<List<Integer>>();
		expected2.add(Arrays.asList(new Integer[]{10, 1}));
		expected2.add(Arrays.asList(new Integer[]{10, 2}));
		expected2.add(Arrays.asList(new Integer[]{30, 3}));
		expected2.add(Arrays.asList(new Integer[]{30, 0}));

		List<List<Integer>> expected3 = new ArrayList<List<Integer>>();
		expected3.add(Arrays.asList(new Integer[]{10, 1}));
		expected3.add(Arrays.asList(new Integer[]{20, 2}));
		expected3.add(Arrays.asList(new Integer[]{30, 3}));
		expected3.add(Arrays.asList(new Integer[]{40, 4}));
		
		List<List<Integer>> expected4 = new ArrayList<List<Integer>>();
		expected4.add(Arrays.asList(new Integer[]{20, 1}));
		expected4.add(Arrays.asList(new Integer[]{10, 1}));
		expected4.add(Arrays.asList(new Integer[]{30, 3}));
		expected4.add(Arrays.asList(new Integer[]{40, 4}));
		
		List<List<Integer>> expected5 = new ArrayList<List<Integer>>();
		expected5.add(Arrays.asList(new Integer[]{10, 1}));
		expected5.add(Arrays.asList(new Integer[]{20, 1}));
		expected5.add(Arrays.asList(new Integer[]{40, 3}));
		expected5.add(Arrays.asList(new Integer[]{30, 3}));

		expected.add(expected0); expected.add(expected1); expected.add(expected2);
		expected.add(expected3); expected.add(expected4); expected.add(expected5);

		List<List<List<Integer>>> actual = new ArrayList<List<List<Integer>>>();
		actual.add(ToolBox.bubbleSort(listToSort0, 0));
		actual.add(ToolBox.bubbleSort(listToSort1, 0));
		actual.add(ToolBox.bubbleSort(listToSort2, 0));
		actual.add(ToolBox.bubbleSort(listToSort3, 1));
		actual.add(ToolBox.bubbleSort(listToSort4, 1));
		actual.add(ToolBox.bubbleSort(listToSort5, 1));

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i).size(), actual.get(i).size());
			for (int j = 0; j < expected.get(i).size(); j++) {
				assertEquals(expected.get(i).get(j).size(), actual.get(i).get(j).size());
				for (int k = 0; k < expected.get(i).get(j).size(); k++) {
					assertEquals(expected.get(i).get(j).get(k), actual.get(i).get(j).get(k));
				}
			}
		}
		assertEquals(expected, actual);
	}


	@Test
	public void testBubbleSortStringList() {
		List<List<String>> unordered = new ArrayList<List<String>>();
		unordered.add(Arrays.asList(new String[]{"5,Five", "4,Four", "x,Three", "2,Two", "1,One"}));
		unordered.add(Arrays.asList(new String[]{"5,Five", "4,Four", "3,Three", "2,Two", "1,One"}));
		unordered.add(Arrays.asList(new String[]{"0-Zero", "1-One", "2-Two", "3-Three", "4-Four", "5-Five"}));
		unordered.add(Arrays.asList(new String[]{"3:Three", "1:One", "5:Five", "4:Four", "2:Two"}));

		List<List<String>> expected = new ArrayList<List<String>>();
		expected.add(null);
		expected.add(Arrays.asList(new String[]{"1,One", "2,Two", "3,Three", "4,Four", "5,Five"}));
		expected.add(Arrays.asList(new String[]{"0-Zero", "1-One", "2-Two", "3-Three", "4-Four", "5-Five"}));
		expected.add(Arrays.asList(new String[]{"1:One", "2:Two", "3:Three", "4:Four", "5:Five"}));

		List<String> seprators = Arrays.asList(new String[]{",", ",", "-", ":"});
		
		List<List<String>> actual = new ArrayList<List<String>>();
		for (int i = 0; i < unordered.size(); i++) {
			actual.add(ToolBox.bubbleSortStringList(unordered.get(i), seprators.get(i)));
		}

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			if (expected.get(i) == null) {
				assertEquals(expected.get(i), actual.get(i));
			}
			else {
				assertEquals(expected.get(i).size(), actual.get(i).size());
				for (int j = 0; j < expected.get(i).size(); j++) {
					assertEquals(expected.get(i).get(j), actual.get(i).get(j));
				}
			}
		}  
	}


	@Test
	public void testBubbleSortString() {
		List<List<String>> unordered = new ArrayList<List<String>>();
		unordered.add(Arrays.asList(new String[]{"05-Five", "04-Four", "0x-Three", "02-Two", "01-One"}));
		unordered.add(Arrays.asList(new String[]{"05-Five", "04-Four", "0x-Three", "2-Two", "01-One"}));
		unordered.add(Arrays.asList(new String[]{"05-Five", "04-Four", "03-Three", "02-Two", "01-One"}));
		unordered.add(Arrays.asList(new String[]{"01-One", "02-Two", "03-Three", "04-Four", "05-Five"}));
		unordered.add(Arrays.asList(new String[]{"03-Three", "01-One", "05-Five", "04-Four", "02-Two"}));
		unordered.add(Arrays.asList(new String[]{"05-Five","04-Four", "01-One", "03-Three", "02-Two"}));
		unordered.add(Arrays.asList(new String[]{"09-Nine","02-Two", "05-Five", "03-Three", "07-Seven"}));

		List<List<String>> expected = new ArrayList<List<String>>();
		expected.add(null);
		expected.add(null);
		expected.add(Arrays.asList(new String[]{"01-One", "02-Two", "03-Three", "04-Four", "05-Five"}));
		expected.add(Arrays.asList(new String[]{"01-One", "02-Two", "03-Three", "04-Four", "05-Five"}));
		expected.add(Arrays.asList(new String[]{"01-One", "02-Two", "03-Three", "04-Four", "05-Five"}));
		expected.add(Arrays.asList(new String[]{"01-One", "02-Two", "03-Three", "04-Four", "05-Five"}));
		expected.add(Arrays.asList(new String[]{"02-Two", "03-Three", "05-Five", "07-Seven", "09-Nine"}));

		List<List<String>> actual = new ArrayList<List<String>>();
		for (List<String> l : unordered) {
			actual.add(ToolBox.bubbleSortString(l));
		}

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			if (expected.get(i) == null) {
				assertEquals(expected.get(i), actual.get(i));
			}
			else {
				assertEquals(expected.get(i).size(), actual.get(i).size());
				for (int j = 0; j < expected.get(i).size(); j++) {
					assertEquals(expected.get(i).get(j), actual.get(i).get(j));
				}
			}
		}  
	}


	@Test
	public void testGetTwoSmallestValues() {
		List<Integer> listToBeSearched = Arrays.asList(new Integer[]{5, 7, 1, 3, 3, 0, 9, 7, 2, 2, 0, 1, 8, 1, 0, 9});
		
		List<Integer> expected = Arrays.asList(new Integer[]{0, 1});
		List<Integer> actual = ToolBox.getTwoSmallestValues(listToBeSearched);
		
		assertEquals(expected.size(), actual.size());
		assertEquals(expected, actual);
	}


	@Test
	public void testGetTwoLargestValues() {
		List<Integer> listToBeSearched = Arrays.asList(new Integer[]{5, 7, 1, 3, 3, 0, 9, 8, 2, 2, 0, 1, 8, 1, 0, 9});
		
		List<Integer> expected = Arrays.asList(new Integer[]{9, 8});
		List<Integer> actual = ToolBox.getTwoLargestValues(listToBeSearched);
		
		assertEquals(expected.size(), actual.size());
		assertEquals(expected, actual);
	}


	@Test
	public void testGetTimeDiff() {
		// Same day
		String beginTime = "27.02.2016, 01:39:30";
		String endTime = "27.02.2016, 03:15:15";
		long expected = 5745;
		long actual = ToolBox.getTimeDiff(beginTime, endTime);
		assertEquals(expected, actual);
		
		// Consecutive days within month
		beginTime = "27.02.2016, 22:39:30";
		endTime = "28.02.2016, 03:15:15";
		expected = 16545;
		actual = ToolBox.getTimeDiff(beginTime, endTime);
		
		// Consecutive days across months in non-leap year
		beginTime = "28.02.2015, 22:39:30";
		endTime = "01.03.2015, 03:15:15";
		expected = 16545;
		actual = ToolBox.getTimeDiff(beginTime, endTime);
		assertEquals(expected, actual);
		
		// Consecutive days across months in leap year (February has 29 days)
		beginTime = "28.02.2016, 22:39:30";
		endTime = "01.03.2016, 03:15:15";
		expected = 16545 + 86400;
		actual = ToolBox.getTimeDiff(beginTime, endTime);
		assertEquals(expected, actual);
	}


	@Test
	public void testGetTimeDiffPrecise() {
		// Same day
		String beginTime = "27.02.2016, 01:39:30:000";
		String endTime = "27.02.2016, 03:15:15:000";
		long expected = 5745000;
		long actual = ToolBox.getTimeDiffPrecise(beginTime, endTime);
		assertEquals(expected, actual);
		
		// Consecutive days within month
		beginTime = "27.02.2016, 22:39:30:125";
		endTime = "28.02.2016, 03:15:15:125";
		expected = 16545000;
		actual = ToolBox.getTimeDiffPrecise(beginTime, endTime);
		
		// Consecutive days across months in non-leap year
		beginTime = "28.02.2015, 22:39:30:250";
		endTime = "01.03.2015, 03:15:15:000";
		expected = 16545000 - 250;
		actual = ToolBox.getTimeDiffPrecise(beginTime, endTime);
		assertEquals(expected, actual);
		
		// Consecutive days across months in leap year (February has 29 days)
		beginTime = "28.02.2016, 22:39:30:750";
		endTime = "01.03.2016, 03:15:15:250";
		expected = 16545000 + 86400000 - 500;
		actual = ToolBox.getTimeDiffPrecise(beginTime, endTime);
		assertEquals(expected, actual);
	}


	@Test
	public void testFactorial() {
		List<Integer> input = Arrays.asList(new Integer[]{10, 5, 4, 3, 2});
		List<Integer> expected = Arrays.asList(new Integer[]{3628800, 120, 24, 6, 2});
		List<Integer> actual = new ArrayList<Integer>();
		for (int i : input) {
			actual.add(ToolBox.factorial(i));
		}
		assertEquals(expected.size(), actual.size());
		assertEquals(expected, actual);
	}


	@Test
	public void testGetAverage() {
		List<Double> toBeAveraged = Arrays.asList(new Double[]{1.5, 0.3, 1.0/7.0, 1.0/3.0, 0.3});
		double expected = (1.5 + 0.3 + 1.0/7.0 + 1.0/3.0 + 0.3) / 5;
		double actual = ToolBox.getAverage(toBeAveraged);
		assertEquals(expected, actual, delta);	
	}


//	public void testGetWeightedAverageAsDouble() {
//		ErrorFraction e1 = new ErrorFraction(7, 8); 
//		ErrorFraction e2 = new ErrorFraction(4, 5);
//		ErrorFraction e3 = new ErrorFraction(-5, 6);
//		ErrorFraction e4 = new ErrorFraction(2, 11);
//    List<ErrorFraction> toBeAveraged = Arrays.asList(new ErrorFraction[]{e1, e2, e3, e4});
//		
//		// Calculate the expected value		
//		double expected = (double)(7 + 4 + -5 + 2)/(8 + 5 + 6 + 11);
//		
//		// Calculate the actual value
//		double actual = AuxiliaryTool.getWeightedAverageAsDouble(toBeAveraged);
//		
//		// Assert equality
//		assertEquals(expected, actual);	
//	}
	
	
//	public void testGetWeightedAverageAsDouble() {
//		Rational r1 = new Rational(7, 8); 
//		Rational r2 = new Rational(4, 5);
//		Rational r3 = new Rational(-5, 6);
//		Rational r4 = new Rational(2, 11);
//    List<Rational> toBeAveraged = Arrays.asList(new Rational[]{r1, r2, r3, r4});
//		
//		// Calculate the expected value		
//		double expected = (double)(7 + 4 + -5 + 2)/(8 + 5 + 6 + 11);
//		
//		// Calculate the actual value
//		double actual = AuxiliaryTool.getWeightedAverageAsDouble(toBeAveraged);
//		
//		// Assert equality
//		assertEquals(expected, actual);	
//	}


	@Test
	public void testGetWeightedAverageAsRational() {
		Rational r1 = new Rational(7, 8); 
		Rational r2 = new Rational(4, 5);
		Rational r3 = new Rational(-5, 6);
		Rational r4 = new Rational(2, 11);
		List<Rational> toBeAveraged = Arrays.asList(new Rational[]{r1, r2, r3, r4});

		Rational expected = new Rational((7 + 4 + -5 + 2), (8 + 5 + 6 + 11));

		Rational actual = ToolBox.getWeightedAverageAsRational(toBeAveraged);

		assertEquals(expected, actual);	
	}


	@Test
	public void testCalculateCorrelationCoefficient() {	
		// Make xList and yList (values taken from example at http://www.wisfaq.nl/show3archive.asp?id=19189&j=2004)
		List<Double> xList = Arrays.asList(new Double[]{3.2, 6.7, 7.5, 3.2, 4.6, 4.3, 9.8, 8.7});
		List<Double> yList = Arrays.asList(new Double[]{10.0, 19.0, 16.0, 11.0, 15.0, 14.0, 26.0, 21.0});

		int n = 8;
		double sumX = 48;
		double sumY = 132;
		double sumXY = 881.2;
		double sumXSqrd = 333;
		double sumYSqrd = 2376;

		double nu = n*sumXY - sumX*sumY;
		double d = Math.sqrt(n*sumXSqrd - Math.pow(sumX, 2)) * Math.sqrt(n*sumYSqrd - Math.pow(sumY, 2));

		double expected = nu / d; 
  	
//	  // Calculate the necessary averages
//	  List<Double> xyList = new ArrayList<Double>();
//		List<Double> xSquaredList = new ArrayList<Double>();
//		List<Double> ySquaredList = new ArrayList<Double>();
//		for (int i = 0; i < xList.size(); i++) {
//			xyList.add(xList.get(i) * yList.get(i));
//			xSquaredList.add(Math.pow(xList.get(i), 2.0));
//			ySquaredList.add(Math.pow(yList.get(i), 2.0));
//		}
//		double avgX = AuxiliaryTool.getAverage(xList);
//		double avgY = AuxiliaryTool.getAverage(yList);
//		double avgXY = AuxiliaryTool.getAverage(xyList);
//		double avgXSquared = AuxiliaryTool.getAverage(xSquaredList);
//		double avgYSquared = AuxiliaryTool.getAverage(ySquaredList);
//		
//		// Calculate the expected value using E(xy) - E(x)*E(y) / sqrt( E(x^2) - (E(x))^2 ) * sqrt( E(y^2) - (E(y))^2 )
//		// (see http://en.wikipedia.org/wiki/Pearson_product-moment_correlation_coefficient)
//		double numerator = avgXY - avgX * avgY;
//		double denominator = Math.sqrt(avgXSquared - Math.pow(avgX, 2.0)) * Math.sqrt(avgYSquared - Math.pow(avgY, 2.0));
//		double expected = numerator / denominator;  

		double actual = ToolBox.calculateCorrelationCoefficient(xList, yList);

		expected = (double)Math.round(expected * 1000000000) / 1000000000; // TODO
		actual = (double)Math.round(actual * 1000000000) / 1000000000;

		assertEquals(expected, actual, delta);
	}

}
