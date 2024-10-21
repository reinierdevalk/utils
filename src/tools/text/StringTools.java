package tools.text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import tbp.symbols.Symbol;
import tools.ToolBox;

public class StringTools {

	public static void main(String[] args) {
	}


	/**
	 * Parses a String representation of a List<Integer> or List<List<Integer>>.
	 * 
	 * @param s The String
	 * @return A 2D List, which contains only one element if the given String is a representation of
	 *         a 1D List.
	 */
	// TESTED
	public static List<List<Integer>> parseStringifiedListOfIntegers(String s) {
		List<List<Integer>> res = new ArrayList<>();

		String[] split = null;
		int dim = s.startsWith("[[") ? 2 : 1;		
		if (dim == 1) {
			split = new String[]{s};
		}
		if (dim == 2) {
			s = s.replaceAll(", \\[", "x\\[");
			split = s.substring(1, s.length()-1).split("x");
		}
		for (String ss : split) {
			if (ss.equals("[]")) {
				res.add(new ArrayList<>());
			}
			else {
				res.add(Arrays.stream(
					ss.substring(1, ss.length()-1).split("\\s*,\\s*")) // take into account spaces around the comma
					.map(Integer::valueOf)
					.collect(Collectors.toList()));
			}
		}

		return res;
	}


	public static String removeTrailingSymbolSeparator(String s) {
		if (s.endsWith(Symbol.SYMBOL_SEPARATOR)) {
			s = s.substring(0, s.lastIndexOf(Symbol.SYMBOL_SEPARATOR)); 
		}
		return s;
	}


	public static int frequencyOfChar(String s, char c) {
		return (int) s.chars().filter(ch -> ch == c).count();
	}


	public static String createLaTeXTable(String[][] dataStr, Integer[] intsToAvg, 
		Double[] doublesToAvg, List<Integer> intInds, int maxLenDouble, int totalNumChars, 
		boolean includeAvgs) {
		String table = "";
		String lineBr = " \\" + "\\" + "\r\n";
		int numRows = dataStr.length;

		// Set any averages
		if (includeAvgs) {
			dataStr[numRows-1] = ToolBox.getAveragesForMixedList(
				intsToAvg, doublesToAvg, intInds, numRows-1, maxLenDouble, totalNumChars
			);
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
}
