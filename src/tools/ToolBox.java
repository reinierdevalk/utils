package tools;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import org.apache.commons.math3.stat.inference.WilcoxonSignedRankTest;

import de.uos.fmt.musitech.utility.math.Rational;


public class ToolBox {
	
	public static final int TAB_LEN = 8;

	/**
	 * Stores the given object in the given file.
	 * 
	 * @param obj
	 * @param file
	 */
	// TESTED through testing getStoredObject() 
	public static void storeObject(Object obj, File file) {
		try {
			file.getParentFile().mkdirs();
			FileOutputStream fileOut = new FileOutputStream(file);
			XMLEncoder encoder = new XMLEncoder(fileOut);
			encoder.writeObject(obj);
			encoder.close();
			fileOut.close();
		} catch(IOException i) {
			i.printStackTrace();
		}	
	}


	/**
	 * Gets the Object of the given type that is stored in the given file.
	 * 
	 * @param objOfClassT
	 * @param file
	 * 
	 * @return
	 */
	// TESTED
	@SuppressWarnings("unchecked")
	public static <T> T getStoredObject(T objOfClassT, File file) {
		String fileName = file.getAbsolutePath();
		T storedObject; 
		try {
			FileInputStream fileIn = new FileInputStream(fileName);    
			XMLDecoder in = new XMLDecoder(fileIn);
			storedObject = (T) in.readObject(); // (Integer) in.readObject();
			in.close();
			fileIn.close();
		} catch(IOException i) {
			i.printStackTrace();
			return null;
		} catch (ClassCastException e) {
			e.printStackTrace();
			return null;
		}
		return storedObject;
	}


	/**
	 * Returns the observed significance level, or p-value, associated with a Wilcoxon signed ranked statistic comparing
	 * mean for two related samples or repeated measurements on a single sample.
	 * @param x
	 * @param y
	 * @return
	 */
	public static double sig(double[] x, double[] y) {
		WilcoxonSignedRankTest sigTest = new WilcoxonSignedRankTest();
		return sigTest.wilcoxonSignedRankTest(x, y, true);
	}


	/**
	 * Calculates the weighted geometric mean of the given numbers using the given weighting.
	 * See https://en.wikipedia.org/wiki/Weighted_geometric_mean
	 * 
	 * @param numbers
	 * @param weights
	 * @return
	 */
	// TESTED
	public static double weightedGeometricMean(List<Double> numbers, List<Double> weights) {
		double sum = 0;
		for (int i = 0; i < numbers.size(); i++) {
			double x = numbers.get(i); 
			double w = weights.get(i);
			double d = Math.log(x);
			// w = 0 and x = 0 gives NaN because log(0) = -infinity, which cannot
			// be multiplied by 0 
			// Ignore the contribution of models with w = 0 by not adding the output (weighted
			// probability) to the sum
			if (w != 0) {
				sum += (w * d);
			}
		}
		return Math.exp(sum / ToolBox.sumListDouble(weights));
	}


	public static double stDev(List<Double> valuesAsList) {
		StandardDeviation stDev = new StandardDeviation();
		double[] values = new double[valuesAsList.size()];
		for (int i = 0; i < valuesAsList.size(); i++) {
			values[i] = valuesAsList.get(i);
		}
		return stDev.evaluate(values);
	}


	public static void storeObjectBinary(Object obj, File file) {
		try {
			file.getParentFile().mkdirs();
			FileOutputStream fileOut = new FileOutputStream(file);
			ObjectOutputStream encoder = new ObjectOutputStream(fileOut);
			encoder.writeObject(obj);
			encoder.close();
			fileOut.close();
		} catch(IOException i) {
			i.printStackTrace();
		}	
	}


	@SuppressWarnings("unchecked")
	public static <T> T getStoredObjectBinary(T objOfClassT, File file) {
		String fileName = file.getAbsolutePath();
		T storedObject = null; 
		try {
			FileInputStream fileIn = new FileInputStream(fileName);    
			ObjectInputStream in = new ObjectInputStream(fileIn);
			storedObject = (T) in.readObject(); 
			in.close();
			fileIn.close();
		} 
		catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // (Integer) in.readObject();
		catch(IOException i) {
			i.printStackTrace();
			return null;
		} catch (ClassCastException e) {
			e.printStackTrace();
			return null;
		}
		return storedObject;
	}


	/**
	 * Returns the contents of the given File as a String.
	 * 
	 * @param file
	 * @return
	 */
	public static String readTextFile(File file) {
		StringBuffer contents = new StringBuffer(); 
		BufferedReader reader = null; 
		try { 
			reader = new BufferedReader(new FileReader(file)); 
			String text = null; 
			// Repeat until all lines are read 
			while ((text = reader.readLine()) != null) { 
				contents.append(text).append(System.getProperty("line.separator")); // .append("\r\n") works also      	
			}
		} catch (FileNotFoundException e) { 
			e.printStackTrace(); 
		} catch (IOException e) { 
			e.printStackTrace(); 
		} finally {       
			try {           
				if (reader != null) {     
					reader.close();               
				} 
			} catch (IOException e) { 
				e.printStackTrace();           
			}       
		}   
		// Show file contents here
		return contents.toString();
//		return contents.toString().replace("\r\n\r\n", "\r\n");
	}


	/**
	 * Stores the given contents in the given File.
	 * NB: All line breaks must be "\r\n" in order to be stored properly. 
	 * 
	 * @param contents
	 * @param file
	 */
	public static void storeTextFile(String contents, File file) {
		try {
			// Create file 
			FileWriter fstream = new FileWriter(file);
			BufferedWriter out = new BufferedWriter(fstream);
			out.write(contents);
			//Close the output stream
			out.close();
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
		}
	}


	public static String breakList(List<Integer> arg, int numCols, String spaceBefore) {
		StringBuffer sb = new StringBuffer();
		int numItems = arg.size(); 
		if (numItems == 0) {
			return spaceBefore + "-";
		}
		else {
			int numRows = (int) Math.ceil(numItems / (double) numCols);
			int itemInd = 0;
			for (int i = 0; i < numRows; i++) {
				sb.append(spaceBefore);
				for (int j = itemInd; j < itemInd + numCols; j++) {
					if (j < numItems) {
						sb.append(ToolBox.tabify(arg.get(j) + "", 1));
					}
				}
				if (i < numRows - 1) {
					sb.append("\r\n");
				}
				itemInd += numCols;
			}	
		}
		return sb.toString();
	}


	/**
	 * Creates a String out of the given String[][], where <br>
	 * <ul>    
	 * <li>the rows are separated by a line break ("\r\n"); </li>
	 * <li>the columns are separated by a comma (","). </li> 
	 * </ul>
	 * The last row is not followed by a line break, and each last column is not
	 * followed by a comma.
	 *  
	 * @param arg
	 * @return
	 */
	// TESTED
	public static String createCSVTableString(String[][] arg) {	
		StringBuffer csvTable = new StringBuffer();

		int numRows = arg.length;
		int numCols = arg[0].length;

		for (int i = 0; i < numRows; i++) {
			for (int j = 0; j < numCols; j++) {
				csvTable.append(arg[i][j]);
				// For all colums but the last
				if (j != numCols - 1) {
					csvTable.append(",");
				}
				else {
					// For all rows but the last
					if (i != numRows - 1) {
						csvTable.append("\r\n");
					}
				}
			}
		}
		return csvTable.toString(); 
	}


	// TESTED
	public static String[][] retrieveCSVTable(String arg) {
		String[] splitInRows;
		if (arg.contains("\r\n")) {
			splitInRows = arg.split("\r\n");
		}
		// On MacOS, all \r\n are \n
		else {
			splitInRows = arg.split("\n");
		}
		int numRows = splitInRows.length;
		String firstRow = splitInRows[0]; 

		// The -1 is necessary to avoid that trailing empty strings are not included; see
		// http://docs.oracle.com/javase/8/docs/api/java/lang/String.html#split-java.lang.String-int-
		int numCols = firstRow.split(",", -1).length;
		
//		// Add any closing empty String
//		// NB: As per the implementation, each row ends with some String (and not with a
//		// comma). Thus, if the last char of the row is a comma, it is actually followed by
//		// an empty String
//		if (firstRow.endsWith(",")) {
//			numCols++;
//		}

		String[][] table = new String[numRows][numCols];
		for (int i = 0; i < numRows; i++) {
			String currRow = splitInRows[i];
			String[] currRowSplit = currRow.split(",", -1);
			for (int j = 0; j < currRowSplit.length; j++) {
				table[i][j] = currRowSplit[j];
			}
			// Add any closing empty String
			if (currRow.endsWith(",")) {
				table[i][numCols - 1] = "";
			}	
		}
		return table;
	}


	// TESTED
	public static List<double[]> convertCSVTable(String[][] tbl) {
		List<double[]> res = new ArrayList<double[]>();
		for (String[] row : tbl) {
			double[] d = new double[row.length];
			for (int i = 0; i < row.length; i++) {
				d[i] = Double.parseDouble(row[i]);
			}
			res.add(d);
		}
		return res;
	}


	/**
	 * Stores the given matrix as a CSV file.
	 * 
	 * @param matrix
	 * @param file
	 */
	public static void storeMatrixAsCSVFile(Integer[][] matrix, File file) {
		try {
			file.getParentFile().mkdirs();
			String matrixAsString = "";
			for (Integer[] row : matrix) {
				String currentRow = "";
				for (int column : row) {
					currentRow += column;
					currentRow += ",";
				}
				currentRow = currentRow.substring(0, currentRow.length() - 1); 
				currentRow += "\r\n";
				matrixAsString += currentRow;
			} 
			storeTextFile(matrixAsString, file);
		} catch(Exception i) {
			i.printStackTrace();
		}
	}


	/**
	 * Parses a String of ints separated by the given regular expression, and returns them in a
	 * List<Integer>.
	 * 
	 * @param stringOfIntegers
	 * @param regEx
	 * @return
	 */
	// TESTED
	public static List<Integer> parseStringOfIntegers(String stringOfIntegers, String regEx){
		List<Integer> values = new ArrayList<Integer>();
		String[] valuesAsStrings = stringOfIntegers.split(regEx);
		for (String s: valuesAsStrings) {
			int currentValue = Integer.parseInt(s);
			values.add(currentValue);
		}
		return values;
	}


	/**
	 * 
	 * @param l
	 * @return  
	 */
	// TESTED
	public static int encodeListOfIntegers(List<Integer> l) {
		if (l == null || l.size() == 0) {
			return -1;
		}
		else {
			String enc = "";
			for (int i : l) {
				enc += i;
			}
			return Integer.parseInt(enc);
		}
	}


	/**
	 * 
	 * @param encodings
	 * @return
	 */
	// TESTED
	public static List<Integer> decodeListOfIntegers(int encoding, int numDigits) {
		if (encoding == -1) {
			return Arrays.asList(new Integer[]{-1});
		}
		else {
			List<Integer> res = new ArrayList<Integer>();
			String asStr = Integer.toString(encoding);
			for (int i = 0; i < asStr.length(); i+=numDigits) {
				res.add(Integer.parseInt(asStr.substring(i, i + numDigits)));
			}
			return res;
		}
	}


	/**
	 * 
	 * @param hyperParams
	 * @return
	 */
	// TESTED
	public static List<List<Double>> createGrid(List<List<Double>> hyperParams) {
		List<List<Double>> res = new ArrayList<List<Double>>();
		if (hyperParams.size() == 2) {
			for (double d1 : hyperParams.get(0)) {
				List<Double> first = Arrays.asList(new Double[]{d1});
				for (double d2 : hyperParams.get(1)) {
					List<Double> comb = new ArrayList<Double>(first);
					comb.add(d2);
					res.add(comb);
				}
			}
		}
		else if (hyperParams.size() == 3) {
			for (double d1 : hyperParams.get(0)) {
				List<Double> first = Arrays.asList(new Double[]{d1});
				for (double d2 : hyperParams.get(1)) {
					List<Double> comb = new ArrayList<Double>(first);
					comb.add(d2);
					for (double d3 : hyperParams.get(2)) {
						List<Double> comb2 = new ArrayList<Double>(comb);
						comb2.add(d3);
						res.add(comb2);
					}
				}
			}
		}
		return res;
	}


	/**
	 * Reads the contents of the given csv file into a list. 
	 * 
	 * @param contents
	 * @param delimiter The delimiter used to separate inidividual line items
	 * @param isMultiDimensional Whether or not each line contains multiple items
	 * @return
	 */
	// TESTED
	public static List<List<Integer>> readCSVFile(String contents, String delimiter,
		boolean isMultiDimensional) {
		List<List<Integer>> res = new ArrayList<List<Integer>>();

		String lineEnd = "\r\n";
		if (!contents.contains(lineEnd)) {
			lineEnd = "\n";
		}
		String[] lines = contents.split(lineEnd);
		for (String s: lines) {
			List<Integer> curr = new ArrayList<Integer>();
			if (!isMultiDimensional) {
				curr.add(Integer.parseInt(s));
			}
			else {	
				for (String e : s.split(delimiter)) {
					curr.add(Integer.parseInt(e));
				}
			}
			res.add(curr);
		}
		return res;
	}


	/**
	 * Returns the given matrix as a table in String format, with the values in the rows separated by tabs. 
	 * 
	 * @param matrix The given matrix
	 * @param decForm The main format. If null, all matrix values are left unformatted
	 * @param altDecForm Alternative format, to be used if one of the columns should be formatted
	 *        differently
	 * @param roundingMode Determines whether and how the last decimal digit should be rounded (yes if set to 
	 *        RoundingMode.HALF_UP; no if set to RoundingMode.DOWN)
	 * @param columnsToSkip Contains the column indices that should be left unformatted (if any) 
	 * @param columnsWithAltDecFor Contains the column indices that require alternative formatting
	 *        (if any) 
	 * @param doesNotApplyValue The value that should be shown as "n/a"       
	 * @return
	 */
	// TESTED TODO not used
	public static String writeMatrixAsTable(String[] legend, double[][] matrix, DecimalFormat decForm, 
		DecimalFormat altDecForm, RoundingMode roundingMode, List<Integer> columnsToSkip,
		List<Integer> columnsWithAltDecFor, double doesNotApplyValue) {
		String matrixAsTable = "";
		
		if (legend != null) { // TODO?
			for (String s : legend) {
				 matrixAsTable =  matrixAsTable.concat(s + "\t");
			}
			matrixAsTable = matrixAsTable.concat("\r\n");
		}
		
		DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.UK);
		otherSymbols.setDecimalSeparator('.'); 
		decForm.setDecimalFormatSymbols(otherSymbols);
		decForm.setRoundingMode(roundingMode);
		altDecForm.setDecimalFormatSymbols(otherSymbols);
		altDecForm.setRoundingMode(roundingMode);
		
		for (double[] row : matrix) {
			String currentRow = "";
			for (int column = 0; column < row.length; column++) {
				double value = row[column];
				// If DecimalFormat is unspecified or current value should not be formatted
				if (decForm == null || columnsToSkip.contains(column)) {
					currentRow += value;
				}
				// If DecimalFormat is specified
				else {
					// If an alternative DecimalFormat is specified for the current value
					if (columnsWithAltDecFor.contains(column)) {
						currentRow += altDecForm.format(value);
					}
					else {
						// If the error metric does not apply
						if (value == doesNotApplyValue) {
							currentRow += "n/a";
						}
						else {
							currentRow += decForm.format(value);
						}
					}
				} 
				currentRow += "\t";
			}
			currentRow = currentRow.substring(0, currentRow.length() - 1); 
			currentRow += "\r\n";
			matrixAsTable += currentRow;
		}
		return matrixAsTable;
	}


	/**
	 * Stores the given List<List>> as a CSV-file.
	 * 
	 * @param matrix
	 * @param file
	 */
	public static void storeListOfListsAsCSVFileOLD(List<List<Integer>> listOfLists, File file) {
		try {
			file.getParentFile().mkdirs();
			String listOfListsAsString = "";
			for (List<Integer> list : listOfLists) {
				String currentRow = "";
				for (int element : list) {
					currentRow += element;
					currentRow += ",";
				}
				currentRow = currentRow.substring(0, currentRow.length() - 1); 
				currentRow += "\r\n";
				listOfListsAsString += currentRow;
			} 
			storeTextFile(listOfListsAsString, file);
		} catch(Exception i) {
			i.printStackTrace();
		}
	}


	/**
	 * Stores the given List<List>> as a CSV-file.
	 * @param <T>
	 * 
	 * @param matrix
	 * @param file
	 */
	public static <T> void storeListOfListsAsCSVFile(List<List<T>> listOfLists, File file) {
		try {
			file.getParentFile().mkdirs();
			String listOfListsAsString = "";
			for (List<T> list : listOfLists) {
				String currentRow = "";
				for (T element : list) {
					currentRow += element;
					currentRow += ",";
				}
				currentRow = currentRow.substring(0, currentRow.length() - 1); 
				currentRow += "\r\n";
				listOfListsAsString += currentRow;
			} 
			storeTextFile(listOfListsAsString, file);
		} catch(Exception i) {
			i.printStackTrace();
		}
	}


	/**
	 * Add as many leading zeros as needed to the String representation of num so that 
	 * its length is maxLen.
	 * @param num
	 * @param maxLen
	 * @return
	 */
	// TESTED
	public static String zerofy(int num, int maxLen) {
		String s = Integer.toString(num);
		int len = s.length();
		int dif = maxLen - len;
		for (int i = 0; i < dif; i++) {
			s = "0".concat(s);
		}
		return s;
	}


	public static String getShortName(String name) {
		String shortName = "";
		for (String s : name.split("_")) {
			shortName += s.substring(0, 1).toUpperCase();
		}	
		return (shortName.length() < 4) ? shortName : shortName.substring(0, 3);
	}


	public static String[] getAveragesForMixedList(Integer[] intsToAvg, Double[] doublesToAvg, 
		int divisor, int maxLenDouble, int totalNumChars) {
		String[] res = new String[intsToAvg.length];
		res[0] = "avg";
		for (int i = 0; i < intsToAvg.length; i++) {
			if (intsToAvg[i] != null) {
				res[i] = String.valueOf(Math.round(intsToAvg[i] / (double) divisor));
			}
			if (doublesToAvg[i] != null) {
				res[i] = ToolBox.formatDouble((doublesToAvg[i] / (double) divisor), 
					maxLenDouble, totalNumChars);
			}
		}
		return res;
	}


	public static String createLaTeXTable(String[][] dataStr, Integer[] intsToAvg, 
		Double[] doublesToAvg, int maxLenDouble, int totalNumChars, boolean includeAvgs) {
		String table = "";
		String lineBr = " \\" + "\\" + "\r\n";
		
		int numRows = dataStr.length;
		
		// Set any averages
		if (includeAvgs) {
			dataStr[numRows-1] = 
				getAveragesForMixedList(intsToAvg, doublesToAvg, numRows-1, maxLenDouble, 
				totalNumChars);
//			dataStr[numRows-1][0] = "avg";
//			for (int i = 0; i < intsToAvg.length; i++) {
//				if (intsToAvg[i] != null) {
//					dataStr[numRows-1][i] = 
//						String.valueOf(Math.round(intsToAvg[i] / (double) (numRows-1)));
//				}
//				if (doublesToAvg[i] != null) {
//					dataStr[numRows-1][i] = 
//						ToolBox.formatDouble((doublesToAvg[i] / (double) (numRows-1)), 
//							maxLenDouble, totalNumChars);
//				}
//			}
		}

		// Create table
		for (int i = 0; i < numRows; i++) {
			String[] row = dataStr[i];
			for (int j = 0; j < row.length; j++) {
				table += row[j] + ( (j != row.length-1) ? " & " : lineBr );
			}
			// In case of any averages: add \hline below penultimate line
			if (i == numRows-2 && includeAvgs) {
				table += "\\hline" + "\r\n";
			}
		}		
		return table;
	}


	/**
	 * Formats the given double by adding leading zeroes so that the part before the 
	 * decimal point equals maxLen, and by cutting so that its number of characters does
	 * not exceed totalNumChars.  
	 * 
	 * @param d
	 * @param maxLen
	 * @param totalNumChars
	 * @return
	 */
	// TESTED
	public static String formatDouble(double d, int maxLen, int totalNumChars) {
		// Convert the double to a String, making sure that no scientific notation is used
		String s =  String.format("%.15f", d);
		if (maxLen != 0) { 
			s = zerofy(s, maxLen);
		}
		else {
			s = s.substring(s.indexOf("."));
		}
		
		int len = s.length();
		if (len >= totalNumChars) {
			s = s.substring(0, totalNumChars);
		}
		else {
			for (int i = 0; i < totalNumChars - len; i++) {
				s = s.concat("0");
			}
		}
		return s;
	}


	// TESTED
	public static String zerofy(String s, int maxLen) {
//		String s = Double.toString(num);
		int len = s.substring(0, s.indexOf(".")).length();
		int dif = maxLen - len;
		for (int i = 0; i < dif; i++) {
			s = "0".concat(s);
		}
		return s;
	}
	

	// TESTED
	public static int maxLen(int arg) {
		int maxLen = 2;
		if (arg > 99) {
			maxLen = Integer.toString(arg).length();
		}
		return maxLen;
	}


	/**
	 * Completes the given String with whitespace/tabs so that its total length equals that of
	 * the given number of tabs. 
	 * 
	 * @param s
	 * @param numTabs
	 * @return
	 */
	// TESTED
	public static String tabify(String s, int numTabs) {
		StringBuffer sb = new StringBuffer();
		int tabLen = TAB_LEN;
		int totalLen = numTabs*tabLen;

		sb.append(s);
		
		if (s == null) {
			s = "";
		}

		int tabsToAdd = 0;
		// a. If s has a length that is a multiple of tabLen: determine the number of tabs to add
		double d = s.length() / (double)tabLen;
		// When d is a multiple of tabLen, i.e., a positive integer
		if (d == (int) d) {
			tabsToAdd = numTabs - (int) d; 
		}
		// b. If not: find the closest tabLen multiple and determine the number of tabs to add
		else {
			for (int i = s.length(); i <= totalLen; i++) {	
				d = i / (double) tabLen;
				// When d is a multiple of tabLen, i.e., a positive integer
				// NB The first tab added completes the length of s to tabLength - but if
				// a string is prepended to s (e.g., in a print statement) this shortens
				// \t with the length of the prepended String
				if (d == (int) d) {
					tabsToAdd = numTabs - ((int) d - 1);
					break;
				}
			}
		}

		// Add tabs to s 
		for (int i = 1; i <= tabsToAdd; i++) {
			sb.append("\t");
		}

		return sb.toString();
	}


	/**
	 * Returns a list containing the names of all files with the given extension in 
	 * the given folder.
	 * 
	 * @param f The folder
	 * @param e The extension
	 * @return
	 */
	public static List<String> getFileNamesWithExtension(File f, String e) {
		if (!e.startsWith(".")) {
			throw new RuntimeException("ERROR: The extension must start with a dot.");
		}

		List<String> fileNames = new ArrayList<String>();
		for (String s : f.list()) {
			if (s.endsWith(e)) {
				fileNames.add(s.substring(0, s.indexOf(e)));
			}
		}
		return fileNames;
	}


	public static String pathify(String[] dirs) {
		String path = "";
		for (int i = 0; i < dirs.length; i++) {
			String s = dirs[i];
			path = path.concat(s);
			if (!s.endsWith("/")) {
				path = path.concat("/");
			}
		}
		if (path.contains("//")) {
			path = path.replace("//", "/");
		}
		if (path.contains("\\")) {
			path = path.replace("\\", "/");
		}
		return path;
	}


	/**
	 * Inserts the given substring into the given string before the given index.
	 * 
	 * @param s
	 * @param substr
	 * @param ind
	 * @return
	 */
	// TESTED
	public static String insertIntoString(String s, String substr, int ind) {
		return s.substring(0, ind) + substr + s.substring(ind);
	}


	/**
	 * Replaces the first occurrence of the given substring in the given string with
	 * the given replacement.
	 * 
	 * @param s
	 * @param substr
	 * @param replacement
	 * @return If <code>s</code> contains <code>substr</code>, s with the replacement;
	 *         else the original <code>s</code>.
	 */
	// TESTED
	public static String replaceFirstInString(String s, String substr, String replacement) {
		if (s.contains(substr)) {
			int ind = s.indexOf(substr);
			s = s.substring(0, ind) + replacement + s.substring(ind + substr.length()); 
			return s;
		}
		return s;
	}


	/**
	 * Get the index of the first char in the string that is not any of the given
	 * characters. If none such char is found, -1 is returned. 
	 * 
	 * @param s
	 * @param not
	 * @return The index of the first char in the string that is not any of the 
	 *         given characters, or, if none such char is found, -1. 
	 */
	// TESTED
	public static int getFirstIndexOfNot(String s, List<String> not) {
		int first = -1;
		for (int i = 0; i < s.length(); i++) {
			if (!not.contains(s.substring(i, i+1))) {
				return i;
			}
		}
		return first;
	}


	/**
	 * Breaks the given string up into separate lines shorter than or equal to the
	 * given maximum line length.
	 * 
	 * @param s
	 * @param maxLineLen
	 * @return A <code>List</code> of <code>String<code>s, each of which is a
	 */
	// TESTED
	public static List<String> breakIntoLines(String s, int maxLineLen) {		
		// Remove any double spaces
		while (s.contains("  ")) {
			s = s.replace("  ", " ");
		}
		String[] split = s.split(" ");
		List<String> lines = new ArrayList<>();
		String currLine = "";
		for (int j = 0; j < split.length; j++) {
			String word = split[j];
			// If word fits on currentLine, add
			if (currLine.length() + word.length() <= maxLineLen) {
				currLine += word + " ";
			}
			// If not, add currentLine to lines and add word to reset currentLine
			else {
				lines.add(currLine.trim());
				currLine = word + " ";
			}
			// In case of last word: add currentLine to lines
			if (j == split.length - 1) {
				lines.add(currLine.trim());
			}
		}
		return lines;
	}


	/**
	 * Converts a boolean to an int, where false == 0 and true == 1.
	 * 
	 * @param arg
	 * @return
	 */
	// TESTED
	public static int toInt(Boolean arg) {
		if (arg == false) {
			return 0;
		}
		else {
			return 1;
		}
	}


	/**
	 * Converts an int to a boolean, where 0 == false and 1 == true.
	 * 
	 * @param arg
	 * @return
	 */
	// TESTED
	public static boolean toBoolean(int arg) {
		if (arg != 0 && arg != 1) {
			throw new IllegalArgumentException("Argument must be 0 or 1");
		}
		if (arg == 0) {
			return false;
		}
		else {
			return true;
		}
	}


	/**
	 * Combines the Lists given by pairing up the values of each element of the respective Lists, and adding the 
	 * List<Integer> thus obtained to the List<List>> that is returned. Both Lists must have the same size. 
	 * Example:
	 * if listOne = [10, 20, 30] and listTwo = [1, 2, 3], a new List<List<> is returned: [[10, 1], [20, 2], [30, 3]]  
	 *  
	 * @param listOne
	 * @param listTwo
	 * @return
	 */
	// TESTED
	public static List<List<Integer>> combineLists(List<Integer> listOne, List<Integer> listTwo) {
		List<List<Integer>> combined = new ArrayList<List<Integer>>();

		// Verify that both Lists have the same size
		if (listOne.size() != listTwo.size()) {
			System.out.println("ERROR: the Lists do not have the same size" + "\n");
			throw new RuntimeException("ERROR (see console for details)");
		}

		for (int i = 0; i < listOne.size(); i++) {
			List<Integer> currentPair = new ArrayList<Integer>();
			currentPair.add(listOne.get(i));
			currentPair.add(listTwo.get(i));
			combined.add(currentPair);
		}
		return combined;
	}


	/**
	 * Returns the given metric position as a String.
	 * 
	 * @param metricPosition
	 * @return
	 */
	public static String getMetricPositionAsString(Rational[] metricPosition) {
		int currentBar = metricPosition[0].getNumer();
		Rational currentPositionInBar = metricPosition[1];
		currentPositionInBar.reduce();
		if (currentPositionInBar.getNumer() != 0) {
			return Integer.toString(currentBar).concat(" ").concat(currentPositionInBar.toString());
		}
		else {
			return Integer.toString(currentBar);
		}
	}


	/**
	 * Searches the part of the given complete String after the given marker text, and returns a String that consists
	 * of all the chars between that point (including the first char immediately following the marker text) and
	 * the first char from that point that is not an allowed char. 
	 * The chars allowed are .0123456789-+Ee/ 
	 * 
	 * @param completeString The String to search through
	 * @param marker The marker text
	 * @return
	 */
	// TESTED 
	public static String getAllowedCharactersAfterMarker(String completeString, String marker) {
		int startIndex = completeString.indexOf(marker) + marker.length();
		String allowedTextAfterMarker = completeString.substring(startIndex);

		for (int i = 0; i < allowedTextAfterMarker.length(); i++) {
			String allowedChars = ".0123456789-+Ee/"; 
			char c = allowedTextAfterMarker.charAt(i);
			if(allowedChars.indexOf(c) == -1) {
				allowedTextAfterMarker = allowedTextAfterMarker.substring(0, i);
				break;
			}	
		}
		// Remove any scientific notation 
		allowedTextAfterMarker = convertToDecimalNotation(allowedTextAfterMarker);
		return allowedTextAfterMarker;
	}


	/**
	 * Adds a line break in the given string after every x-th instance (where x is defined by instanceNum) of the given 
	 * char after which to break. If the argument insert is not an empty String, insert is added directly after each 
	 * line break.
	 * 
	 * @param aString
	 * @param instanceNum
	 * @param breakChar
	 * @param insert
	 * @return
	 */
	// TESTED
	public static String breakString(String aString, int instanceNum, char breakChar, String insert) {
		String newString = "";
		int numOfBreakChars = 0;
		int startInd = 0;
		int endInd = -1;
		for (int i = 0; i < aString.length(); i++) {
			if (aString.charAt(i) == breakChar || i == aString.length() - 1) {
				numOfBreakChars++;
				if (numOfBreakChars % instanceNum == 0) {
					endInd = i + 1;
					newString = newString.concat(aString.substring(startInd, endInd));
					if (aString.charAt(i) == breakChar) {
						newString = newString.concat("\r\n" + insert);
					}
					startInd = endInd;
				}
			}
		}
		return newString;
	}


//  /**
//   * Stores the given object under the given filename.
//   * 
//   * @param obj
//   * @param fileName
//   */
//  public static void storeObject(Object obj, String fileName) {
//  	try {
//      FileOutputStream fileOut = new FileOutputStream(fileName);
//      XMLEncoder encoder = new XMLEncoder(fileOut);
//      encoder.writeObject(obj);
//      encoder.close();
//      fileOut.close();
//    } catch(IOException i) {
//      i.printStackTrace();
//    }
//  }


//  /**
//   * Gets the Object of the given type that is stored in the given File.
//   * @param objOfClassT
//   * @param file
//   * @return
//   */
//  @SuppressWarnings("unchecked")
//  public static <T> T getStoredObject(T objOfClassT, String fileName) {
//    //	String fileName = file.getAbsolutePath();  
//    T storedObject; 
//    try {
//      FileInputStream fileIn = new FileInputStream(fileName);    
//      XMLDecoder in = new XMLDecoder(fileIn);
//      storedObject = (T) in.readObject(); // (Integer) in.readObject();
//      in.close();
//      fileIn.close();
//    } catch(IOException i) {
//      i.printStackTrace();
//      return null;
//    } catch (ClassCastException e) {
//      e.printStackTrace();
//      return null;
//    }
//    return storedObject;
//  }


	/**
	 * Converts the given Array into a List.
	 * 
	 * @param anArray
	 * @return A List<Double>.
	 */
  	// TESTED
	public static List<Double> arrayToList(double[] anArray) {
		List<Double> arrayAsList = new ArrayList<Double>();
		for (double d : anArray) {
			arrayAsList.add(d);
		}
		return arrayAsList;
	}


	/**
	 * Converts the given List to an Array.
	 * 
	 * @param list
	 * @return A double[]
	 */
	// TESTED
	public static double[] listToArray(List<Double> list) {
		double[] arr = new double[list.size()];
		for (int j = 0; j < list.size(); j++) {
			arr[j] = list.get(j);
		}
		return arr;
	}


	/**
	 * Converts the given List<Integer> to a List<Double>.
	 */
	// TESTED
	public static List<Double> convertToListDouble(List<Integer> aList) {
		List<Double> asDoubles = new ArrayList<Double>();
		for (int i : aList) {
			asDoubles.add((double) i);
		}
		return asDoubles;
	}


	// TESTED
	public static List<List<String>> convertToListString(List<List<Double>> arg) {
		List<List<String>> asString = new ArrayList<List<String>>();
		for (List<Double> ld : arg) {
			List<String> ldString = new ArrayList<String>();
			for (Double d : ld) {
				ldString.add(d.toString());
			}
			asString.add(ldString);
		}	
		return asString;
	}


	/**
	 * Converts the given String to a Rational.
	 * 
	 * @param aString
	 * @return
	 */
	public static Rational convertToRational(String aString) {
  	String[] currentFraction = aString.split("/");
    int numerator = Integer.parseInt(currentFraction[0].trim());
    int denominator = Integer.parseInt(currentFraction[1].trim());
  	
  	return new Rational(numerator, denominator);
  }


	/**
	 * 
	 * 
	 * @param aList
	 * @return The sum of all list elements.
	 */
	// TESTED
	public static int sumListInteger(List<Integer> aList) {
		int sum = 0; 
		for (int i: aList) {
			sum += i;
		}
		return sum;
	}


	/**
	 * 
	 * 
	 * @param aList
	 * @return The sum of all list elements.
	 */
	// TESTED
	public static double sumListDouble(List<Double> aList) {
		double sum = 0.0; 
		for (double d: aList) {
			sum += d;
		}
		return sum;
	}


	/**
	 * 
	 * @param arg
	 * @return The sum of all list elements.
	 */
	// TESTED
	public static double sumDoubleArray(double[] arg) {
		double sum = 0.0; 
		for (double d: arg) {
			sum += d;
		}
		return sum;
	}


	/**
	 * Sums all the elements in the given List of Rationals and returns the (reduced) result.
	 * 
	 * @param aList
	 * @return
	 */
	// TESTED
	public static Rational sumListRational(List<Rational> aList) {
		Rational sum = new Rational(0, 1); 
		for (Rational r: aList) {
			sum = sum.add(r);
		}
		sum.reduce();
		return sum;
	}


	/**
	 * Checks whether the first given Rational is a multiple of second given Rational.
	 * 
	 * @param r1
	 * @param r2
	 * @return
	 */
	// TESTED 
	public static boolean isMultiple(Rational r1, Rational r2) {
		if (r1.div(r2).getDenom() == 1) {
			return true;
		}
		else {
			return false;			
		}
	}


	/**
	 * Returns a String representation of the given double, with no unnecessary trailing zeros. 
	 * 
	 * @param aDouble
	 * @return 
	 */
	// TESTED
	public static String convertToStringNoTrailingZeros(double aDouble) {
		String doubleAsString = Double.toString(aDouble);
		boolean endsWithZero = doubleAsString.endsWith("0");
		boolean endsWithPointZero = doubleAsString.endsWith(".0");
		while (endsWithZero == true && endsWithPointZero == false) {
			doubleAsString = doubleAsString.substring(0, doubleAsString.length() - 1);
			endsWithZero = doubleAsString.endsWith("0");
		}
		return doubleAsString;
	}
	
	
	/**
	 * Represents the given list as a formatted string: <br>
	 * [a, b] --> "a and b" <br>
	 * [a, b, ..., n] --> "a, b, ..., and n" <br>
	 * 
	 * @param l
	 * @return
	 */
	// TESTED
	public static String StringifyList(List<Integer> l) {
		if (l.size() == 1) {
			throw new RuntimeException("The list should contain at least two elements.");
		}
		String s = "";
		int listSize = l.size();
		for (int k = 0; k < listSize; k++) {	
			s += String.valueOf(l.get(k));
			if (k != listSize - 1) {
				s += ", ";
			}
		}
		int splitInd = s.lastIndexOf(",");
		String repl = "";
		if (listSize == 2) {
			repl = " and";
		}
		if (listSize > 2) {
			repl = ", and";
		} 
		
		s = s.substring(0, splitInd) + repl + s.substring(splitInd + 1);
		return s;
		
	}
	
		
	/**
	 * Concatenates the arrays contained by the List given as argument, in the order they are set in the List.
	 * 
	 * @param listOfArrays
	 * @return
	 */
	// TESTED
	public static double[] concatDoubleArrays(List<double[]> listOfArrays) {
		int length = 0;
		for (double[] d : listOfArrays) {
			length +=d.length;
		}
		double[] arraysConcatenated = new double[length];
		
		int startIndex = 0;
		for (int i = 0; i < listOfArrays.size(); i++) {
		  double[] currentArray = listOfArrays.get(i);
		  for (int j = 0; j < currentArray.length; j++) {
			  arraysConcatenated[startIndex + j] = currentArray[j];
	 	  }
		  startIndex += currentArray.length;
		}  
		return arraysConcatenated;
	}
	
	
	/**
	 * Concatenates the arrays contained by the List given as argument, in the order they are set in the List.
	 * 
	 * @param listOfArrays
	 * @return
	 */
	// TESTED
	public static Object[] concatArrays(List<Object[]> listOfArrays) {
		int length = 0;
		for (Object[] d : listOfArrays) {
			length +=d.length;
		}
		Object[] arraysConcatenated = new Object[length];
		
		int startIndex = 0;
		for (Object[] currentArray : listOfArrays) {
//		for (int i = 0; i < listOfArrays.size(); i++) {
//		  Object[] currentArray = listOfArrays.get(i);
		  for (int j = 0; j < currentArray.length; j++) {
			  arraysConcatenated[startIndex + j] = currentArray[j];
	 	  }
		  startIndex += currentArray.length;
		}  
		return arraysConcatenated;
	}
	
	
	
	
	/**
	 * Calculates the precision, recall and F1-Score (in %) from the given numbers. Returns a Rational[] containing
	 *   as element 0: the precision (tP / (tP + fP))
	 *   as element 1: the recall (tp / (tP + fN))
	 *   as element 2: the F1-score (2PR / (P + R))
	 * 
	 * @param truePositives
	 * @param falsePositives
	 * @param falseNegatives
	 * @return
	 */
	// TESTED
	public static Rational[] calculatePrecisonRecallF1ScoreAsRationals(int truePositives, int falsePositives,
		int falseNegatives) {
	  Rational[] precisionRecallAndF1Score = new Rational[3];
	  
	  Rational precision = new Rational(truePositives, truePositives + falsePositives);
	  Rational recall = new Rational(truePositives, truePositives + falseNegatives);
	  Rational numF1Score = precision.mul(recall).mul(2);
	  Rational denomF1Score = precision.add(recall);
	  Rational f1Score = numF1Score.div(denomF1Score);
	  
	  precisionRecallAndF1Score[0] = precision;
    precisionRecallAndF1Score[1] = recall;
    precisionRecallAndF1Score[2] = f1Score;
    	  	  		
	  return precisionRecallAndF1Score;
	}
	
	
	/**
	 * Converts a String representation of a double in scientific notation to a String representation of that double
	 * in decimal notation. If the String representation given as argument is already in decimal notation, the argument
	 * is returned unchanged.
	 * 
	 * @param aString
	 * @return
	 */
  // TESTED
	public static String convertToDecimalNotation(String aString) {
		// If aString is written in scientific notation
		if (aString.contains("E-")) {
			// Get the exponent
		  String exponentAsString = aString.substring(aString.length() - 1, aString.length());
			double exponent = Double.parseDouble(exponentAsString);
			
		  // Remove the complete exponent indication from aString;
			aString = aString.substring(0, aString.indexOf("E-"));
			
			// If exponent = 0: return aString
			if (exponent == 0.0) {
				return aString;
			}
			// If exponent > 0: add the necessary number of zeroes (i.e, the exponent - 1) to prefix, remove the dot
			// from aString, and then concatenate and return prefix and aString
			else {
				String prefix = "0.";
			  for (int i = 0; i < exponent - 1; i++) {  
				  prefix += "0";
			  }
				aString = aString.replace(".", "");
				aString = prefix + aString;
				return aString;
			}
		}
		// If aString is written in decimal notation
		else {
			return aString;
		}		
	}
	
	
	/**
	 * Converts the contents of a given double[] to a String formatted as [x.x, y.y, ...,z.z]
	 * 
	 * @param aDoubleArray
	 * @return
	 */
  // TESTED
	public static String convertDoubleArrayToString(double[] aDoubleArray) {
		String arrayAsString = "[";
		for (int i = 0; i < aDoubleArray.length; i++) {
			arrayAsString += aDoubleArray[i];
			if (i < aDoubleArray.length - 1) {
				arrayAsString += ", ";
			}
		}	
		arrayAsString += "]";
		return arrayAsString;
	}
	
	
	// TODO test
	public static double calculateStandardDeviation(List<Rational> values, Rational mu) {
		double stdDev;
		
		Rational sum = Rational.ZERO;
		for (Rational v : values) {
			Rational r = v.sub(mu);
			Rational rSquared = r.mul(r);
			sum = sum.add(rSquared);
		}
		Rational sumDivided = sum.div(values.size());
		stdDev = Math.sqrt(sumDivided.toDouble());
		
		return stdDev;
	}

	/**
	 * 
	 * @param arg
	 * @return The list normalised so that all elements sum to 1.
	 */
	// TESTED
	public static List<Double> normaliseListDouble(List<Double> arg) {
		List<Double> normalised = new ArrayList<Double>();	
		double sum = sumListDouble(arg);
		for (double d : arg) {
			if (sum == 0.0) {
				normalised.add(1.0/arg.size());
			}
			else {
				normalised.add(d / sum);
			}
		}
		return normalised;
	}


	/**
	 * 
	 * @param arg
	 * @return The list normalised so that all elements sum to 1.
	 */
	// TESTED
	public static double[] normaliseDoubleArray(double[] arg) {
		double[] normalised = new double[arg.length];	
		double sum = sumDoubleArray(arg);
		for (int i = 0; i < arg.length; i++) {
			if (sum == 0.0) {
				normalised[i] = 1.0/arg.length;
			}
			else {
				normalised[i] = arg[i] / sum;
			}
		}
		return normalised;
	}


	/**
	 * Returns all indices of the given element in the given list.
	 *  
	 * @param list 
	 * @param element
	 * @return
	 */
	// TESTED
	public static List<Integer> getIndicesOf(List<Integer> list, int element) {
		List<Integer> indices = new ArrayList<Integer>();
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i) == element) {
				indices.add(i);
			}
		}
		return indices;
	}


	/**
	 * Groups any successive elements in the given list and adds them as separate lists
	 * to the list returned.
	 * 
	 * @param l
	 * @return
	 */
	// TESTED
	public static List<List<Integer>> groupListOfIntegers(List<Integer> l) {
		List<List<Integer>> grouped = new ArrayList<>();
		List<Integer> group = new ArrayList<>();
		group.add(l.get(0));
		for (int i = 1; i < l.size(); i++) {
			int curr = l.get(i);
			int prev = l.get(i-1);
			if (curr != prev+1) {
				grouped.add(group);
				group = new ArrayList<>();
			}
			group.add(curr);
		}
		// Add last group
		grouped.add(group);
		return grouped;
	}


	/**
	 * Gets the highest value contained in the given Array.
	 * 
	 * @param arg
	 * @return The highest value.
	 */
	// TESTED
	public static double max(double[] arg) {
		List<Double> asList = new ArrayList<Double>();
		for (double d : arg) {
			asList.add(d);
		}
		return Collections.max(asList);
	}


	/**
	 * Determines the indices highest value contained in the given Array.
	 * 
	 * @return As element 0: the highest value in the double </br>
	 *         As element 1: the indices of that highest value 
	 */
	// TESTED
	public static List<Integer> maxIndices(double[] arg) {
		List<Integer> indices = new ArrayList<Integer>();
		double max = max(arg);
		for (int i = 0; i < arg.length; i++) {
			if (arg[i] == max) {
				indices.add(i);
			}
		}
		return indices;
	}


	/**
	 * Transposes the given list of lists.
	 * 
	 * @param l
	 * @return
	 */
	// TESTED
	public static List<List<Integer>> transposeListOfLists(List<List<Integer>> l) {
		List<List<Integer>> t = new ArrayList<List<Integer>>();
		int numRows = l.size();
		int numCols = l.get(0).size();
		for (int i = 0; i < numCols; i++) {
			List<Integer> column = new ArrayList<Integer>();
			for (int j = 0; j < numRows; j++) {
				column.add(l.get(j).get(i));
			}
			t.add(column);
		}
		return t;
	}
	
	
	/**
	 * Gets all permutations of the given list.
	 * 
	 * Example: all permutations of [0, 1, 2] are calculated as follows
	 * 
	 * initial list
	 * [0, 1, 2] 
	 * start at index 0 and swap with all following indices (0, 1, 2)
	 * [0, 1, 2]           [1, 0, 2]           [2, 1, 0]  
	 * fix index 0 in result; start at index 1 and swap with all following indices (1, 2)
	 * [0, 1, 2] [0, 2, 1] [1, 0, 2] [1, 2, 0] [2, 1, 0] [2, 0, 1]
	 * fix index 1; start index is now the last index in the list: done
	 * 
	 * See https://www.techiedelight.com/generate-permutations-string-java-recursive-iterative/
	 * 
	 * @param l
	 * @return
	 */
	// TESTED
	public static List<List<Integer>> getPermutations(List<Integer> l) {
		
		List<List<Integer>> permutations = new ArrayList<>();
		
		class Helper {
			private void permutations(List<Integer> l, int currentIndex) {
				if (currentIndex == l.size() - 1) {
					permutations.add(l);
				}

				for (int i = currentIndex; i < l.size(); i++) {
					List<Integer> copy = new ArrayList<Integer>(l);
					Collections.swap(copy, currentIndex, i);
					permutations(copy, currentIndex + 1);
					copy = new ArrayList<Integer>(copy);
					Collections.swap(copy, currentIndex, i);
				}
			}
		}
		
		new Helper().permutations(l, 0);
		return permutations;
	}


	/**
	 * Lists all possible combinations of all elements in two lists of the same length. Each list
	 * element can only occur once in a combination.
	 * 
	 * Example for lists [0, 1, 2] and [a, b, c] with the following Cartesian product
	 * 
	 *   0  1  2
	 * a a0 a1 a2
	 * b b0 b1 b2
	 * c c0 c1 c2
	 * 
	 * Combinations are obtained by iterating the elements of the first row of the Cartesian product 
	 * start at a0 (a and 0 unavailable for indices 1 and 2) 
	 *   [a0, b1, c2]  
	 *   [a0, b2, c1]
	 * start at a1 (a and 1 unavailable for indices 1 and 2)
	 *   [a1, b0, c2] 
	 *   [a1, b2, c0]
	 * start at a2 (a and 2 unavailable for indices 1 and 2)
	 * [a2, b0, c1]
	 * [a2, b1, c0]   
	 * 
	 * @return The indices of the combinations. 
	 */
	// TESTED
	public static List<List<Integer[]>> getCombinations(int listSize) {
		List<List<Integer[]>> combinations = new ArrayList<>();
				
		List<Integer> indices = new ArrayList<>();
		for (int i = 0; i < listSize; i++) {
			indices.add(i);
		}

		List<List<Integer>> perm = getPermutations(indices);
		
		// Combinations can be created by pairing, for each permutation, each indices element 
		// with each perm element, e.g., [0, 1, 2] (indices) and [0, 2, 1] (second permutation)
		// becomes [[0, 0], [1, 2], [2, 1]]
		for (int i = 0; i < perm.size(); i++) {
			List<Integer[]> currComb = new ArrayList<>();
			for (int j = 0; j < indices.size(); j++) {
				currComb.add(new Integer[]{indices.get(j), perm.get(i).get(j)});
			}
			combinations.add(currComb);
		}
		
		boolean print = false;
		if (print) {
			for (List<Integer[]> l : combinations) {
				System.out.println("- - - - - - - - - -");
				for (int k = 0; k < l.size(); k++) {
					System.out.println(Arrays.toString(l.get(k)));
				}
			}
		}

		return combinations;
	}


	public static List<Integer[]> findAllUniqueCombinations(List<Integer> pitches, List<Integer> voices) {
		List<Integer[]> unique = new ArrayList<Integer[]>();
		
		int depth = pitches.size();
		for (int i = 0; i < 2*(depth*depth); i++) {
			int first = i % depth;
//			System.out.println(first);
			for (int j = 0; j < depth; j++) {
				int second = j;
//				unique
			}

		}
		
		
//		for (int i = 0; i < voices.size(); i++) {
//			List<Integer[]> pitchesComb = new ArrayList<>();
//			for (int j = 0; j < pitches.size(); j++) {
//				pitchesComb.add(new Integer[]{i, pitches.get(j)});
//			}
//			//List<Integer> otherV = 
//					voices.remove(currV); 
//		}
//		
//		
//		int pitchStartInd = 0;
//		
//		int voiceStartInd = 0;
//		List<Integer> voicesIndDone = new ArrayList<>();
//		voicesIndDone.add(voiceStartInd);
//		Integer[] currComb = new Integer[]{pitches.get(pitchStartInd), voices.get(voiceStartInd)};
//		List<Integer[]> comb = new ArrayList<>();
//		comb.add(currComb);
//		for (int i = 0; i < pitches.size(); i++) {
//			if (i != pitchStartInd) {
//				int currP = pitches.get(i);
//				for (int j = 0; j < voices.size(); j++) {
//					if (!voicesIndDone.contains(j)) {
//						comb.add(new Integer[]{pitches.get(i), voices.get(j)});
//						voicesIndDone.add(j);
//					} 
//				}
//			}
//		}
//		
//		
//		List<Integer> indicesCols = new ArrayList<>();
//		for (int i = 0; i < pitches.size(); i++) {
//			indicesCols.add(i);
//		}
//		List<Integer> indicesRows = new ArrayList<>();
//		for (int i = 0; i < voices.size(); i++) {
//			indicesRows.add(i);
//		}
//		
//		// Make possibilities matrix
//		List<List<Integer[]>> indOfPoss = new ArrayList<>();
//		for (int indRow : indicesRows) {
//			List<Integer[]> row = new ArrayList<>();
//			for (int indCol : indicesCols) {
//				row.add(new Integer[] {indRow, indCol});
//			}
//			indOfPoss.add(row);
//		}
//		for (List<Integer[]> l : indOfPoss) {
//			String row = "";
//			for (Integer[] i : l) {
//				row += Arrays.toString(i) + " ";
//			}
//			System.out.println(row);
//		}
//		
//		List<Integer[]> firstRow = indOfPoss.get(0); 
//		for (int i = 0; i < firstRow.size(); i++) {
//			Integer[] elem = firstRow.get(i);
//			Integer[] lastElemAdded = elem;
//			List<Integer[]> comb = new ArrayList<>();
//			comb.add(elem);
//			// Check rows 1-n as long as all combinations starting with elem are not exhausted
//			boolean sth = true;
//			while (sth) {
//				for (int j = 1; j < indOfPoss.size(); j++) {
//					List<Integer[]> nextRow = indOfPoss.get(j);
//					for (int k = 0; k < nextRow.size(); k++) {
//						if (k != i) {
//							Integer[] nextElem = nextRow.get(k);
//							if ((nextElem[0] != lastElemAdded[0] && nextElem[1] != lastElemAdded[1])) {
//								comb.add(nextElem);
//								lastElemAdded = nextElem;
//							}
//						}
//					}
//					if 
//				}
//			}
//		}
//				
//		// Get all unique combinations
//		int numRows = indOfPoss.size();
////		for (int i = 0; i < numRows; i++) {
//			List<Integer[]> firstRow = indOfPoss.get(0); 
//			for (int j = 0; j < firstRow.size(); j++) {
//				List<Integer[]> comb = new ArrayList<>();
//				Integer[] elem = firstRow.get(j);
//				List<Integer[]> doneForThisElem = new ArrayList<>();
//				comb.add(elem);
//				for (int k = i+1; k < numRows; k++) {
//					List<Integer[]> nextRow = indOfPoss.get(k);
//					for (int l = 0; l < nextRow.size(); l++) {
//						Integer[] nextElem = nextRow.get(l);
//						if ((nextElem[0] != elem[0] && nextElem[1] != elem[1]) && 
//							!doneForThisElem.contains(nextElem)) {
//							comb.add(nextElem);
//							doneForThisElem.add(nextElem);
//							break;
//						}
//					}
//				}
//			}
////		}
		
		return unique;
	}
	
	
	/**
	 * Gets all subsets of the given size of the given set.
	 * See https://stackoverflow.com/questions/14224953/get-all-subsets-of-a-set
	 * 
	 * @param set
	 * @return
	 */
	// TODO test
	public static List<List<Integer>> getSubsets(List<Integer> set, int size) {
		List<List<Integer>> allsubsets = new ArrayList<>();
		
		int max = 1 << set.size();

		for (int i = 0; i < max; i++) {
			List<Integer> subset = new ArrayList<Integer>();
			for (int j = 0; j < set.size(); j++) {
				if (((i >> j) & 1) == 1) {
					subset.add(set.get(j));
				}
			}
			if (size == -1 || (size != -1 && subset.size() == size)) {
				allsubsets.add(subset);
			}
		}
		return allsubsets;
    }


	/**
	 * Removes the items at the given indices from the list and returns the pruned list.
	 * 
	 * @param data
	 * @param inds
	 * @return
	 */
	// TESTED
	public static <T> List<T> removeItemsAtIndices(List<T> data, List<Integer> inds) {
//		for (int i : inds) {
//			data.set(i, null);
//		}
//		data.removeIf(d -> d == null);

		List<T> dataPruned = new ArrayList<>();
		for (int i = 0; i < data.size(); i++) {
			if (!inds.contains(i)) {
				dataPruned.add(data.get(i));
			}
		}
		return dataPruned;
	}


	/**
	 * Returns a list containing, for each list element, the item at the given index in the element.
	 *  
	 * @param l
	 * @param colInd
	 * @return
	 */
	// TESTED
	public static List<Integer> getItemsAtIndex(List<Integer[]> l, int colInd) {
		List<Integer> col = new ArrayList<>();
		for (Integer[] in : l) {
			col.add(in[colInd]);
		}
		return col;
	}


	/**
	 * Returns a list containing, for each list element, the item at the given index in the element.
	 *  
	 * @param l
	 * @param colInd
	 * @return
	 */
	// TESTED
	public static List<Rational> getItemsAtIndexRational(List<Rational[]> l, int colInd) {
		List<Rational> col = new ArrayList<>();
		for (Rational[] in : l) {
			col.add(in[colInd]);
		}
		return col;
	}


	/**
	 * Returns the index of the nth specified item in the list.
	 * 
	 * @param l
	 * @return
	 */
	// TESTED
	public static int getIndexOfNthItem(List<Integer> l, int item, int n) {
		int counter = 0;
		for (int i = 0; i < l.size(); i++) {
			if (l.get(i) != null && l.get(i) == item) {
				counter++;
				if (counter == n) {
					return i;
				}
			}
		}
		return -1;
	}


	/**
	 * Reorders the given list according to the given list of indices.
	 *  
	 * Example: 
	 * list of indices 	[2, 4, 1, 3, 0]
	 * original list	[[0.0], [1.0], [2.0], [3.0], [4.0]]
	 * reordered list	[[4.0], [2.0], [0.0], [3.0], [1.0]]
	 * 
	 * @param l
	 * @param inds
	 * @return The reordered list, or <code>null</code> if the given lists are of 
	 *         different size.
	 */
	// TESTED
	public static <T> List<T> reorderByIndex(List<T> l, List<Integer> inds) {
		if (l.size() != inds.size()) {
			return null;
		}
		else {
			List<T> reordered = new ArrayList<>();
			inds.forEach(i -> reordered.add(null));
			for (int i = 0; i < l.size(); i++) {
				reordered.set(inds.get(i), l.get(i));
			}
			return reordered;
		}
	}


	/**
	 * Sorts the given list by the given index of each list element.
	 * 
	 * @param toSort
	 * @param index
	 * @return
	 */
	// TESTED
	public static List<Integer[]> sortBy(List<Integer[]> toSort, int index) {
		// See https://stackoverflow.com/questions/20480723/how-to-sort-2d-arrayliststring-by-only-the-first-element
		Collections.sort(toSort, new Comparator<Integer[]>() {    
			@Override
			public int compare(Integer[] o1, Integer[] o2) {
				return o1[index].compareTo(o2[index]);
			}               
		});
		return toSort;
	}


	/**
	 * Sorts the given list by the given index of each list element.
	 * 
	 * @param toSort
	 * @param index
	 * @return
	 */
	// TESTED
	public static List<Rational[]> sortByRational(List<Rational[]> toSort, int index) {
		// See https://stackoverflow.com/questions/20480723/how-to-sort-2d-arrayliststring-by-only-the-first-element
		Collections.sort(toSort, new Comparator<Rational[]>() {    
			@Override
			public int compare(Rational[] o1, Rational[] o2) {
				return o1[index].compareTo(o2[index]);
			}               
		});
		return toSort;
	}


	/**
	 * Sorts the given list by the given index of each list element.
	 * 
	 * @param toSort
	 * @param index
	 * @param type
	 * @return
	 */
	// TESTED
	public static List<String[]> sortByString(List<String[]> toSort, int index, String type) {
		// See https://stackoverflow.com/questions/20480723/how-to-sort-2d-arrayliststring-by-only-the-first-element
		
		List<String> allowed = Arrays.asList(new String[]{"int", "Rational"});
		if (!allowed.contains(type)) {
			throw new RuntimeException("Type " + type + " not allowed: must be one of " +
				allowed	+ ".");
		}
		
		if (type.equals("int")) {
			Collections.sort(toSort, new Comparator<String[]>() {    
				@Override
				public int compare(String[] o1, String[] o2) {
					return String.valueOf(o1[index]).compareTo(String.valueOf(o2[index]));
				}
			});
		}
		else if (type.equals("Rational")) {
			Collections.sort(toSort, new Comparator<String[]>() {    
				@Override
				public int compare(String[] o1, String[] o2) {
					String[] o1spl = o1[index].split("/");
					Rational r1 = 
						new Rational(Integer.parseInt(o1spl[0]), Integer.parseInt(o1spl[1]));
					String[] o2spl = o2[index].split("/");
					Rational r2 = 
						new Rational(Integer.parseInt(o2spl[0]), Integer.parseInt(o2spl[1]));
					return r1.compareTo(r2);
				}
			});
		}
		return toSort;
	}


	/**
	 * Sorts the given List<List>> so that the values at position pos of each List are in ascending numerical order.
	 * NB: this is the implementation of bubblesort as given on http://nl.wikipedia.org/wiki/Bubblesort
	 * 
	 * @param listToSort
	 * @param pos
	 * @return
	 */
	// TESTED
	public static List<List<Integer>> bubbleSort(List<List<Integer>> listToSort, int pos) {
		List<List<Integer>> copyOfListToSort = new ArrayList<List<Integer>>(listToSort);
		for (int j = 0; j < copyOfListToSort.size(); j++) {
			for (int i = 1; i < copyOfListToSort.size() - j; i++) {
				int previousValue = copyOfListToSort.get(i-1).get(pos);
				int currentValue = copyOfListToSort.get(i).get(pos);
				if (previousValue > currentValue) {
					Collections.swap(copyOfListToSort, i, i-1);
				}
			}
		}
		return copyOfListToSort;
	}


	/**
	 * Given a List of Strings, each starting with number separated by the given separator,
	 * returns a List with the Strings in ascending numerical order.
	 * NB: this is the implementation of bubblesort as given on http://nl.wikipedia.org/wiki/Bubblesort
	 * 
	 * @param listToSort
	 * @param separator
	 * @return
	 */
	// TESTED
	public static List<String> bubbleSortStringList(List<String> listToSort, String separator) {
		// Check that the first character of each String is a number between 0-9
		List<String> allNumbers = 
			Arrays.asList(new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"});
		for (String s : listToSort) {
			if (!allNumbers.contains(s.substring(0, 1))) {
				return null;
			}
		}

		List<String> copyOfListToSort = new ArrayList<String>(listToSort);
		for (int j = 0; j < copyOfListToSort.size(); j++) {
			for (int i = 1; i < copyOfListToSort.size() - j; i++) {
				int previousValue = 
					Integer.parseInt(copyOfListToSort.get(i-1).substring(0, 
					copyOfListToSort.get(i-1).indexOf(separator)));
				int currentValue = 
					Integer.parseInt(copyOfListToSort.get(i).substring(0, 
					copyOfListToSort.get(i).indexOf(separator)));
				if (previousValue > currentValue) {
					Collections.swap(copyOfListToSort, i, i-1);
				}
			}
		}
		return copyOfListToSort;
	}


	/**
	 * Given a List of Strings starting with a number between and including 00 and 09, returns a List with 
	 * the Strings in ascending numerical order (00, 01, 02, ...) 
	 * NB: this is the implementation of bubblesort as given on http://nl.wikipedia.org/wiki/Bubblesort
	 * 
	 * @param listToSort
	 * @return
	 */
	// TESTED
	@Deprecated
	public static List<String> bubbleSortString(List<String> listToSort) {
		// Check that the first character of each String is a 0 and the second a number 
		// between 0-9. If not, return null
		List<String> allNumbers = 
			Arrays.asList(new String[]{"01-", "02-", "03-", "04-", "05-", "06-", "07-", "08-", "09-"});
		for (String s : listToSort) {
			// Check whether s starts correctly
//			if (!allNumbers.contains(s.substring(0, 1))) {
////			throw new RuntimeException("ERROR: Filename does not start with a number.");
//				return null;
//			}
//			String first = s.substring(0, 1);
//			String second = s.substring(1, 2);
//			if (!first.equals("0") || !allNumbers.contains(second)) {
//				return null;
//			}
			if (!allNumbers.contains(s.substring(0, 3))) {
				return null;
			}
		}

		// Sort
		List<String> copyOfListToSort = new ArrayList<String>(listToSort);
		for (int j = 0; j < copyOfListToSort.size(); j++) {
			for (int i = 1; i < copyOfListToSort.size() - j; i++) {
//				int previousValue = Integer.parseInt(copyOfListToSort.get(i-1).substring(0, 1));
				int previousValue = Integer.parseInt(copyOfListToSort.get(i-1).substring(1, 2));
//				int currentValue = Integer.parseInt(copyOfListToSort.get(i).substring(0, 1));
				int currentValue = Integer.parseInt(copyOfListToSort.get(i).substring(1, 2));
				if (previousValue > currentValue) {
					Collections.swap(copyOfListToSort, i, i-1);
				}
			}
		}
		return copyOfListToSort;
	}


	/** 
	 * Returns the two smallest values from the given list, with the smallest first. // TODO make applicable List of any type?
	 * 
	 * @param aList
	 * @return
	 */
	// TESTED
	public static List<Integer> getTwoSmallestValues(List<Integer> aList) {
//		int smallestValue = Integer.MAX_VALUE;
//		int secondSmallestValue = Integer.MAX_VALUE;
//		for (int i : aList) {
//			if (i < smallestValue) {
//				smallestValue = i;
//			}
//			if (i > smallestValue && i < secondSmallestValue) {
//				secondSmallestValue = i;
//			}
//		}

		int smallestValue = Collections.min(aList);
		List<Integer> aListWithoutSmallest = new ArrayList<Integer>();
		for (int i : aList) {
			if (i != smallestValue) {
				aListWithoutSmallest.add(i);
			}
		}
		int secondSmallestValue = Collections.min(aListWithoutSmallest);		
		return Arrays.asList(new Integer[]{smallestValue, secondSmallestValue});
		
	}
	
	
	/** 
	 * Returns the two biggest values from the given list, with the biggest first. // TODO make applicable List of any type?
	 * 
	 * @param aList
	 * @return
	 */
	// TESTED
	public static List<Integer> getTwoBiggestValues(List<Integer> aList) {
//		int biggestValue = Integer.MIN_VALUE;
//		int secondBiggestValue = Integer.MIN_VALUE;
//		for (int i : aList) {
//			if (i > biggestValue) {
//				biggestValue = i;
//			}
//			if (i < biggestValue && i > secondBiggestValue) {
//				secondBiggestValue = i;
//			}
//		}
			
		int biggestValue = Collections.max(aList);
		List<Integer> aListWithoutBiggest = new ArrayList<Integer>();
		for (int i : aList) {
			if (i != biggestValue) {
				aListWithoutBiggest.add(i);
			}
		}
		int secondBiggestValue = Collections.max(aListWithoutBiggest);		
		return Arrays.asList(new Integer[]{biggestValue, secondBiggestValue});
	}


	private static final String DATE_TIME_SEP = ", ";
	private static final String DATE_SEP = ".";
	private static final String TIME_SEP = ":";
	/**
	 * Gets the date and time of the moment the method is called.
	 * 
	 * @return The date and time when this method was called, formatted as a String.
	 */
	public static String getTimeStamp() {
		// From http://www.roseindia.net/java/example/java/util/currenttime.shtml
		Calendar calendar = new GregorianCalendar();

		// Day, month, year
		String day = zerofy(calendar.get(Calendar.DATE), 2);
		String month = zerofy(calendar.get(Calendar.MONTH) + 1, 2);
		String year = zerofy(calendar.get(Calendar.YEAR), 2);
		// Hours, minutes, seconds
		String hour = zerofy(calendar.get(Calendar.HOUR_OF_DAY), 2);	
		String min = zerofy(calendar.get(Calendar.MINUTE), 2);	
		String sec = zerofy(calendar.get(Calendar.SECOND), 2);

		return day + DATE_SEP + month + DATE_SEP + year + DATE_TIME_SEP + 
			hour + TIME_SEP + min + TIME_SEP + sec ;
	}
	
	
	public static String getTimeStampPrecise() {
		// From http://www.roseindia.net/java/example/java/util/currenttime.shtml
		Calendar calendar = new GregorianCalendar();

		// Day, month, year
		String day = zerofy(calendar.get(Calendar.DATE), 2);
		String month = zerofy(calendar.get(Calendar.MONTH) + 1, 2);
		String year = zerofy(calendar.get(Calendar.YEAR), 2);
		// Hours, minutes, seconds
		String hour = zerofy(calendar.get(Calendar.HOUR_OF_DAY), 2);	
		String min = zerofy(calendar.get(Calendar.MINUTE), 2);	
		String sec = zerofy(calendar.get(Calendar.SECOND), 2);
		String msec = zerofy(calendar.get(Calendar.MILLISECOND), 2);

		return day + DATE_SEP + month + DATE_SEP + year + DATE_TIME_SEP + 
			hour + TIME_SEP + min + TIME_SEP + sec + TIME_SEP + msec;
	}


	/**
	 * Calculates the time difference (in seconds) between the given start and end time.
	 * 
	 * @param start
	 * @param end
	 * @return
	 */
	// TESTED
	public static long getTimeDiff(String start, String end) {
		String[] s = start.split(DATE_TIME_SEP);
		String sDate = s[0];
		String[] sDayMonYear = sDate.split("[" + DATE_SEP + "]");
		int sDay = Integer.parseInt(sDayMonYear[0]);
		int sMon = Integer.parseInt(sDayMonYear[1]);
		int sYear = Integer.parseInt(sDayMonYear[2]);
		String sTime = s[1];
		String[] sHourMinSec = sTime.split(TIME_SEP);
		int sHour = Integer.parseInt(sHourMinSec[0]);
		int sMin = Integer.parseInt(sHourMinSec[1]);
		int sSec = Integer.parseInt(sHourMinSec[2]);

		String[] e = end.split(DATE_TIME_SEP);
		String eDate = e[0];
		String[] eDayMonYear = eDate.split("[" + DATE_SEP + "]"); 
		int eDay = Integer.parseInt(eDayMonYear[0]);
		int eMon = Integer.parseInt(eDayMonYear[1]);
		int eYear = Integer.parseInt(eDayMonYear[2]);
		String eTime = e[1];	
		String[] eHourMinSec = eTime.split(TIME_SEP);
		int eHour = Integer.parseInt(eHourMinSec[0]);
		int eMin = Integer.parseInt(eHourMinSec[1]);
		int eSec = Integer.parseInt(eHourMinSec[2]);

		// From
		// http://www.threeten.org/articles/local-date-time.html
		// http://www.leveluplunch.com/java/examples/number-of-seconds-between-two-dates/
		LocalDateTime startDate = LocalDateTime.of(sYear, sMon, sDay, sHour, sMin, sSec);
		LocalDateTime endDate = LocalDateTime.of(eYear, eMon, eDay, eHour, eMin, eSec);
		return ChronoUnit.SECONDS.between(startDate, endDate);
	}


	/**
	 * Calculates the time difference (in ms) between the given start and end time.
	 * 
	 * @param start
	 * @param end
	 * @return
	 */
	// TESTED
	public static long getTimeDiffPrecise(String start, String end) {
		String[] s = start.split(DATE_TIME_SEP);
		String sDate = s[0];
		String[] sDayMonYear = sDate.split("[" + DATE_SEP + "]");
		int sDay = Integer.parseInt(sDayMonYear[0]);
		int sMon = Integer.parseInt(sDayMonYear[1]);
		int sYear = Integer.parseInt(sDayMonYear[2]);
		String sTime = s[1];
		String[] sHourMinSec = sTime.split(TIME_SEP);
		int sHour = Integer.parseInt(sHourMinSec[0]);
		int sMin = Integer.parseInt(sHourMinSec[1]);
		int sSec = Integer.parseInt(sHourMinSec[2]);
		int sMilliSec = Integer.parseInt(sHourMinSec[3]);

		String[] e = end.split(DATE_TIME_SEP);
		String eDate = e[0];
		String[] eDayMonYear = eDate.split("[" + DATE_SEP + "]"); 
		int eDay = Integer.parseInt(eDayMonYear[0]);
		int eMon = Integer.parseInt(eDayMonYear[1]);
		int eYear = Integer.parseInt(eDayMonYear[2]);
		String eTime = e[1];	
		String[] eHourMinSec = eTime.split(TIME_SEP);
		int eHour = Integer.parseInt(eHourMinSec[0]);
		int eMin = Integer.parseInt(eHourMinSec[1]);
		int eSec = Integer.parseInt(eHourMinSec[2]);
		int eMilliSec = Integer.parseInt(eHourMinSec[3]);

		// From
		// http://www.threeten.org/articles/local-date-time.html
		// http://www.leveluplunch.com/java/examples/number-of-seconds-between-two-dates/
		LocalDateTime startDate = LocalDateTime.of(sYear, sMon, sDay, sHour, sMin, sSec, 1000000*sMilliSec);
		LocalDateTime endDate = LocalDateTime.of(eYear, eMon, eDay, eHour, eMin, eSec, 1000000*eMilliSec);
		return ChronoUnit.MILLIS.between(startDate, endDate);
	}


	/**
	 * Calculates the factorial of the given integer.
	 *  
	 * @param n
	 * @return
	 */
	//
	public static int factorial(int n) {
		int fact = n;
		for (int i = n - 1; i > 0; i--){    
			fact = fact * i;    
		}
		return fact;
	}


	/**
	 * Calculates the average of all values contained in the List given as argument.
	 *  
	 * @param aList
	 * @return The average of all values contained in the List.
	 */
	// TESTED
	public static double getAverage(List<Double> aList) {
		double sum = 0.0;
		for (int i = 0; i < aList.size(); i++) {
		sum += aList.get(i);
	}
		return sum / aList.size();
	}

	
//	/**
//	 * Calculates the weighted average of all values contained in the List given as argument.
//	 *  
//	 * @param aList
//	 * @return The average of all values contained in the List.
//	 */
//  // TESTED 
//	public static double getWeightedAverageAsDouble(List<ErrorFraction> aList) {
//		int sumOfNumerators = 0;
//		int sumOfDenominators = 0;
//		
//		for (ErrorFraction e: aList) {
//			sumOfNumerators += e.getNumerator();
//			sumOfDenominators += e.getDenominator();
//		}
//		return (double) sumOfNumerators / sumOfDenominators;
//	}
	
	
//	/**
//	 * Calculates the weighted average of all values contained in the List given as argument.
//	 *  
//	 * @param aList
//	 * @return The average of all values contained in the List.
//	 */
//  // TESTED 
//	public static double getWeightedAverageAsDouble(List<Rational> aList) {
//		int sumOfNumerators = 0;
//		int sumOfDenominators = 0;
//		
//		for (Rational r: aList) {
//			sumOfNumerators += r.getNumer();
//			sumOfDenominators += r.getDenom();
//		}
//		return (double) sumOfNumerators / sumOfDenominators;
//	}
	
	
	/**
	 * Calculates the weighted average of all values contained in the List given as argument.
	 *  
	 * @param aList
	 * @return The average of all values contained in the List.
	 */
	// TESTED
   	public static Rational getWeightedAverageAsRational(List<Rational> aList) {
		int sumOfNumerators = 0;
		int sumOfDenominators = 0;
		
		for (Rational r: aList) {
			sumOfNumerators += r.getNumer();
			sumOfDenominators += r.getDenom();
		}
		return new Rational(sumOfNumerators, sumOfDenominators);
	}


	/**
	 * Calculates the correlation coefficient r of the values of two lists, using the following formula:
	 * 
	 * r =  n*(SUMxy) - (SUMx)*(SUMy)
	 *      / 
	 *      SQRT(n*(SUMx^2) - (SUMx)^2) * SQRT(n*(SUMy^2) - (SUMy)^2)
	 *      
	 * (see M. F. Triola, /Elementary Statistics/ (Boston, 2012), p. 520)   
	 * 
	 * NB: Use only for Lists with more than one element; otherwise the method will return 0.0.
	 * 
	 * @param xList
	 * @param yList
	 * @return
	 */
  // TESTED
	public static double calculateCorrelationCoefficient(List<Double> xList, List<Double> yList) {
		
		int n = xList.size();
		double sumX = 0;
		double sumY = 0;
		double sumXY = 0;
		double sumXSqrd = 0;
		double sumYSqrd = 0;
		for (int i = 0; i < xList.size(); i++) {
			sumX += xList.get(i);
			sumY += yList.get(i);
			sumXY += xList.get(i) * yList.get(i);
			sumXSqrd += Math.pow(xList.get(i), 2);
			sumYSqrd += Math.pow(yList.get(i), 2);
		}
		
		double num = n*sumXY - sumX*sumY;
		double denom = Math.sqrt(n * sumXSqrd - Math.pow(sumX, 2)) * Math.sqrt(n * sumYSqrd - Math.pow(sumY, 2));
		
		if (denom == 0) {
			return 0.0;
		}
		else {
		  return num/denom;
		}
		
//		// Calculate variables
//		// E(x)
//		double avgX = getAverage(xList);
//
//		// E(y)
//		double avgY = getAverage(yList);
//		
//		// E(xy), E(x^2), E(y^2) 		
//		List<Double> xYList = new ArrayList<Double>();
//		List<Double> xSquaredList = new ArrayList<Double>();
//		List<Double> ySquaredList = new ArrayList<Double>();
//		for (int i = 0; i < xList.size(); i++) {
//			xYList.add(xList.get(i) * yList.get(i));
//			xSquaredList.add(Math.pow(xList.get(i), 2.0));
//			ySquaredList.add(Math.pow(yList.get(i), 2.0));
//		}
//		double avgXY = getAverage(xYList);
//		double avgXSquared = getAverage(xSquaredList);
//		double avgYSquared = getAverage(ySquaredList);
//		
//	  // Apply the formula E(xy) - E(x)*E(y) / sqrt( E(x^2) - (E(x))^2 ) * sqrt( E(y^2) - (E(y))^2 )
//		// (see http://en.wikipedia.org/wiki/Pearson_product-moment_correlation_coefficient)
//		double numerator = avgXY - avgX * avgY;
//		double denominator = 
//			Math.sqrt(avgXSquared - Math.pow(avgX, 2.0)) * Math.sqrt(avgYSquared - Math.pow(avgY, 2.0));
//		if (denominator == 0) {
//			return 0;
//		}
//		double correlationCoefficient = numerator / denominator;
//		return correlationCoefficient;
	}
		
}
