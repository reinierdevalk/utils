package tools.text;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import tbp.symbols.Symbol;
import tools.ToolBox;

public class StringTools {

	public static void main(String[] args) {
	}


	/**
	 * Reads a CSV file into a {@code List<String[]>}. Empty lines in the file are ignored.
	 * 
	 * @return
	 */
	public static List<String[]> readCSVFile(String f) {
		List<String[]> l = new ArrayList<>();
		try {
			l = Files.lines(Paths.get(f))
					.map(String::trim) // trim the line itself
					.filter(line -> !line.isEmpty()) // skip empty lines
					.map(line -> line.split(","))
					.map(arr -> Arrays.stream(arr).map(String::trim).toArray(String[]::new))
					.collect(Collectors.toList());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return l;
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


	public static String randomID(int length) {
		String allowedChars = "abcdefghijklmnopqrstuvwxyz123456789";
		Random random = new Random();
		StringBuilder res = new StringBuilder(length);

		for (int i = 0; i < length; i++) {
			// Get a random index in allowedChars and at the character to res 
			int index = random.nextInt(allowedChars.length());
			res.append(allowedChars.charAt(index));
		}

		return res.toString();
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


	/**
	 * Reads a JSON file into a <code>Map</code>.
	 * 
	 * @param fileName The path to the JSON file.
	 * @return
	 */
	public static Map<String, Map<String, String>> readJSONFile(String fileName) {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);

		Map<String, Map<String, String>> m = null;
		try {
			// Define the type reference for the Map
			TypeReference<Map<String, Map<String, String>>> typeRef = 
				new TypeReference<Map<String, Map<String, String>>>(){};
			m = objectMapper.readValue(new File(fileName), typeRef);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return m;
	}


	/**
	 * Creates a well-formed JSON String from a {@code Map<String, ?>}.
	 *  
	 * @param m
	 * @return
	 */
	public static String createJSONString(Map<String, ?> map) {
		ObjectMapper objectMapper = new ObjectMapper();
		String jsonString = null;
		try {
			jsonString = objectMapper.writeValueAsString(map);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}

		return jsonString;
	}


	// TESTED
	public static String crlf2lf(String s) {
		return s.replace("\r\n", "\n");
	}


	/**
	 * Parses a String representation of a 1D or 2D Python list as returned by 
	 * Python's <code>json.dumps()</code>.
	 * 
	 * @param s
	 * @param ds Target data structure; one of <code>Array</code> or <code>List</code>.
	 * @param dt Target data type; one of <code>String</code>, <code>Integer</code>, or <code>Double</code>.
	 * @return The target data structure, which is 1D if the given String is a representation 
	 *         of a 1D data structure and 2D if it is a representation of a 2D data structure.
	 */
	public static Object parseJSONString(String json, int dim, String ds, String dt) {
		ObjectMapper mapper = new ObjectMapper();

		try {
			if (ds.equalsIgnoreCase("List")) {
				if (dim == 1) {
					switch (dt.toLowerCase()) {
						case "string": return mapper.readValue(json, new TypeReference<List<String>>() {});
						case "integer": return mapper.readValue(json, new TypeReference<List<Integer>>() {});
						case "double": return mapper.readValue(json, new TypeReference<List<Double>>() {});
						default: throw new IllegalArgumentException("Unsupported type for List: " + dt);
					}
				} else if (dim == 2) {
					switch (dt.toLowerCase()) {
						case "string": return mapper.readValue(json, new TypeReference<List<List<String>>>() {});
						case "integer": return mapper.readValue(json, new TypeReference<List<List<Integer>>>() {});
						case "double": return mapper.readValue(json, new TypeReference<List<List<Double>>>() {});
						default: throw new IllegalArgumentException("Unsupported type for List: " + dt);
					}
				} else {
					throw new IllegalArgumentException("Only dimensions 1 or 2 are supported.");
				}
			} else if (ds.equalsIgnoreCase("Array")) {
				if (dim == 1) {
					switch (dt.toLowerCase()) {
						case "string": return mapper.readValue(json, String[].class);
						case "integer": return mapper.readValue(json, Integer[].class);
						case "double": return mapper.readValue(json, Double[].class);
						default: throw new IllegalArgumentException("Unsupported type for Array: " + dt);
					}
				} else if (dim == 2) {
					switch (dt.toLowerCase()) {
						case "string": return mapper.readValue(json, String[][].class);
						case "integer": return mapper.readValue(json, Integer[][].class);
						case "double": return mapper.readValue(json, Double[][].class);
						default: throw new IllegalArgumentException("Unsupported type for Array: " + dt);
					}
				} else {
					throw new IllegalArgumentException("Only dimensions 1 or 2 are supported.");
				}
			} else {
				throw new IllegalArgumentException("ds must be 'List' or 'Array'");
			}
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}


	/**
	 * Parses a JSON String returned by Python's <code>json.dumps()</code>. Accepts
	 * 1D and 2D lists.
	 * 
	 * @param s
	 * @param dim
	 * @return 
	 */
	public static Object parseJSONString(String json, int dim, String ds) {
		ObjectMapper mapper = new ObjectMapper();
		
		if (ds.equals("List")) {
			try {
				if (dim == 1) {
					return mapper.readValue(json, new TypeReference<List<String>>() {});
				} else if (dim == 2) {
					return mapper.readValue(json, new TypeReference<List<List<String>>>() {});
				} else {
					throw new IllegalArgumentException("Only dimensions 1 or 2 are supported.");
				}
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		}
		else {
			try {
				if (dim == 1) {
					return mapper.readValue(json, String[].class);  // 1D array
				} else if (dim == 2) {
					return mapper.readValue(json, String[][].class);  // 2D array
				} else {
					throw new IllegalArgumentException("Only dimensions 1 or 2 are supported.");
				}
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
	}


	/**
	 * Parses a JSON String returned by Python's <code>json.dumps()</code>.
	 * 
	 * @param s
	 * @return
	 */
	public static List<String> parseJSONString(String s) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.readValue(s, new TypeReference<List<String>>(){});
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}


	/**
	 * Removes the extensions from the Strings in the given list.
	 * 
	 * @param l
	 * @return
	 */
	// TESTED
	public static List<String> removeExtensions(List<String> l) {
		List<String> res = new ArrayList<>();
		for (String s : l) {
			if (s.contains(".")) {
				res.add(s.substring(0, s.lastIndexOf(".")));
			}
			else {
				res.add(s);
			}
		}

		return res;

		// This works if it is guaranteed that all List elements have an extension
//		return l.stream()
//			.map(s -> s.substring(0, s.lastIndexOf(".")))
//			.collect(Collectors.toList());
	}


	/**
	 * Constructs a path <code>String</code> from the given list of dir names.
	 * The list elements are added in the order they appear in the list; a file
	 * separator (/) is added to the end of the path.
	 * 
	 * @param l
	 * @return
	 */
	public static String getPathString(List<String> l) {
		Path path = Path.of(l.get(0));
		for (int i = 1; i < l.size(); i++) {
			path = path.resolve(l.get(i));
		}
		String pathStr = path.toString();
		// Replace any backward slashes (Windows)
		pathStr = pathStr.replace("\\", "/");
		// Add final file separator
		if (!pathStr.equals("")) {
			pathStr += "/";
		}

		return pathStr;
	}


	/**
	 * Parses a String representation of a 1D or 2D Python list.
	 * NB Obsolete: replaced by parseJSONString().
	 * 
	 * @param s
	 * @param ds Target data structure; one of <code>Array</code> or <code>List</code>.
	 * @param dt Target data type; one of <code>String</code>, <code>Integer</code>, or <code>Double</code>.
	 * @return The target data structure, which is 1D if the given String is a representation 
	 *         of a 1D data structure and 2D if it is a representation of a 2D data structure.
	 */
	// TESTED
	public static Object parseStringifiedPythonList(String s, String ds, String dt) {
		// Remove all spaces
		s = s.replaceAll(" ", "");
		// Remove any single or double quotes around the 'list' items
		if (dt.equals("String")) {
			s = s.replaceAll("'", "");
			s = s.replaceAll("\"", "");
		}

		// 1D data structures
		if (!s.startsWith("[[")) {
			if (s.equals("[]")) {
				if (ds.equals("Array")) {
					return 
						dt.equals("String") ? new String[0] :
						dt.equals("Integer") ? new Integer[0] : 
						new Double[0];
				}
				else {
					return new ArrayList<>();
				}
			}
			else {
				Stream<String> strm = Arrays.stream(s.substring(1, s.length()-1).split(","));
				if (ds.equals("Array")) {
					return
						dt.equals("String")	? strm.toArray(String[]::new) :
						dt.equals("Integer") ? strm.map(Integer::valueOf).toArray(Integer[]::new) :	
						null; // TODO
				}
				else {
					return 
						dt.equals("String") ? strm.collect(Collectors.toList()) :
						dt.equals("Integer") ? strm.map(Integer::valueOf).collect(Collectors.toList()) :
						null; // TODO
				}
			}
		}
		// 2D data structures
		else {
			// Split s into sublists; remove the outer [] of the first dimension
			s = s.replaceAll(",\\[", "x\\[");
			String[] split = s.substring(1, s.length()-1).split("x"); // [...],[...],[...]

			String[][] arrStr = new String[split.length][];
			Integer[][] arrInt = new Integer[split.length][];
			Double[][] arrDbl = new Double[split.length][];
			List<List<String>> listStr = new ArrayList<>();
			List<List<Integer>> listInt = new ArrayList<>();
			List<List<Double>> listDbl = new ArrayList<>();
			for (int i = 0; i < split.length; i++) {
				String ss = split[i];
				if (ss.equals("[]")) {
					if (ds.equals("Array")) {
						if (dt.equals("String")) {
							arrStr[i] = new String[0];
						}
						else if (dt.equals("Integer")) {
							arrInt[i] = new Integer[0];
						}
						else {
							arrDbl[i] = new Double[0];
						}
					}
					else {
						if (dt.equals("String")) {
							listStr.add(new ArrayList<>());
						}
						else if (dt.equals("Integer")) {
							listInt.add(new ArrayList<>());
						}
						else {
							listDbl.add(new ArrayList<>());
						}
					}
				}
				else {
					Stream<String> strm = Arrays.stream(ss.substring(1, ss.length()-1).split(","));
					if (ds.equals("Array")) {
						if (dt.equals("String")) {
							arrStr[i] = strm.toArray(String[]::new);
						}
						else if (dt.equals("Integer")) {
							arrInt[i] = strm.map(Integer::valueOf).toArray(Integer[]::new);
						}
						else {
							arrDbl[i] = null; // TODO
						}
					}
					else {
						if (dt.equals("String")) {
							listStr.add(strm.collect(Collectors.toList()));
						}
						else if (dt.equals("Integer")) {
							listInt.add(strm.map(Integer::valueOf).collect(Collectors.toList()));
						}
						else {
							listDbl.add(null); // TODO
						}
					}
				}
			}
			if (ds.equals("Array")) {
				return
					dt.equals("String") ? arrStr :
					dt.equals("Integer") ? arrInt :
					arrDbl;
			}
			else {
				return 
					dt.equals("String") ? listStr :
					dt.equals("Integer") ? listInt :
					listDbl;		
			}
		}
	}

}