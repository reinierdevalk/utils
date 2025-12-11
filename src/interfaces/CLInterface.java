package interfaces;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import conversion.exports.MEIExport;
import conversion.imports.MIDIImport;
import conversion.imports.TabImport;
import external.Tablature;
import external.Tablature.Tuning;
import internal.core.Encoding;
import tbp.symbols.TabSymbol.TabSymbolSet;
import tools.ToolBox;
import tools.text.StringTools;

public class CLInterface {
	private static final String PATHS_FILE = "paths.json";
	private static final String PATHS_FILE_DEV = "paths-dev.json";
	private static final String CONFIG_FILE = "config.cfg";

	public static final int DEV_IND = 0;
	public static final int OPTS_IND = 1;
	public static final int DEFAULT_VALS_IND = 2;
	public static final int USER_OPTS_VALS_IND = 3;

	
	// CLI args (as in abtab script)
	// Transcriber
	public final static String TUNING = "-u"; // only needed in MEIExport
	public final static String KEY = "-k"; // only needed in TestManager
	public final static String MODE = "-m"; // only needed in TestManager
	public final static String MODEL = "-o"; // only needed in UI
	public final static String VERBOSE = "-v"; // only needed in UI

	// TabMapper
	public final static String ORNAMENTATION = "-o";
	public final static String DURATION = "-d";
	
	// Layout
	public final static String STAFF = "-s";
	public final static String TABLATURE = "-t"; // only needed in MEIExport
	public final static String TYPE = "-y"; // only needed in MEIExport
	public final static String PLACEMENT = "-p";

	// Input
	public static final String FILE = "-f";
	public static final String FORMAT = "-a";

	public final static String INPUT = "i";
	public final static String SINGLE_STAFF = "s";
	
	public static final List<String> ALLOWED_FILE_FORMATS = Arrays.asList(
		MEIExport.MEI_EXT, // .mei
		TabImport.TAB_EXT, // .tab
		Encoding.TBP_EXT, // .tbp
		TabImport.TC_EXT, // .tc
		MEIExport.XML_EXT // .xml
	);


	public static void main(String[] args) {
	}


	/**
	 * Reads <code>paths.json</code> (located on the <code>CODE_PATH</code>). It is preferred
	 * that this method is called from a <code>main()</code>, and its value then passed on.
	 * 
	 * @param dev <code>true</code> if called in development mode.
	 * 
	 * @return A {@code Map<String, String>}, containing for each key in
	 *         <code>paths.json</code> the value (relative path) extended 
	 *         to its full path.
	 */
	public static Map<String, String> getPaths(boolean dev) {
		Map<String, String> m = null;

		// 1. Read user-defined paths from .cfg file 
		// ROOT_PATH leads to all things not code (data, datasets, models, templates, ...) 
		// CODE_PATH leads to the code 
		// EXE_PATH leads to the executable (and is only needed in install.sh)
		Map<String, String> userDefinedPaths = getUserDefinedPaths(dev);
		String rp = userDefinedPaths.get("ROOT_PATH");
		String cp = userDefinedPaths.get("LIB_PATH");

		// 2. Read the Map from the JSON file
		Map<String, Map<String, String>> pathsConfig = StringTools.readJSONFile(
			StringTools.getPathString(Arrays.asList(cp)) + (dev ? PATHS_FILE_DEV : PATHS_FILE)		
		);
		Map<String, String> paths = pathsConfig.get("paths");
		Map<String, String> files = pathsConfig.get("files");

		// 3. Set paths in m
		m = new LinkedHashMap<String, String>();
		// a. Set ROOT_PATH and CODE_PATH
		m.put("ROOT_PATH", StringTools.getPathString(Arrays.asList(rp)));
		m.put("CODE_PATH", StringTools.getPathString(Arrays.asList(cp)));
		// b. Complete paths on ROOT_PATH by prepending ROOT_PATH
		m.put("ENCODINGS_PATH", StringTools.getPathString(
			Arrays.asList(rp, paths.get("ENCODINGS_PATH"))
		));
		m.put("MIDI_PATH", StringTools.getPathString(
			Arrays.asList(rp, paths.get("MIDI_PATH"))
		));
		m.put("ENCODINGS_PATH_JOSQUINTAB", StringTools.getPathString(
			Arrays.asList(rp, paths.get("ENCODINGS_PATH_JOSQUINTAB"))
		));
		m.put("MIDI_PATH_JOSQUINTAB", StringTools.getPathString(
			Arrays.asList(rp, paths.get("MIDI_PATH_JOSQUINTAB"))
		));
		m.put("DATASETS_PATH", StringTools.getPathString(
			Arrays.asList(rp, paths.get("DATASETS_PATH"))
		));
		m.put("EXPERIMENTS_PATH", StringTools.getPathString(
			Arrays.asList(rp, paths.get("EXPERIMENTS_PATH"))
		));
		m.put("MODELS_PATH", StringTools.getPathString(
			Arrays.asList(rp, paths.get("MODELS_PATH"))
		));
		m.put("TEMPLATES_PATH", StringTools.getPathString(
			Arrays.asList(rp, paths.get("TEMPLATES_PATH"))
		));
		m.put("ANALYSER_PATH", StringTools.getPathString(
			Arrays.asList(rp, paths.get("ANALYSER_PATH"))
		));
		m.put("CONVERTER_PATH", StringTools.getPathString(
			Arrays.asList(rp, paths.get("CONVERTER_PATH"))
		));
		m.put("TABMAPPER_PATH", StringTools.getPathString(
			Arrays.asList(rp, paths.get("TABMAPPER_PATH"))
		));
		m.put("DIPLOMAT_PATH", StringTools.getPathString(
			Arrays.asList(rp, paths.get("DIPLOMAT_PATH"))
		));
		m.put("POLYPHONIST_PATH", StringTools.getPathString(
			Arrays.asList(rp, paths.get("POLYPHONIST_PATH"))
		));
		// c. Complete paths on CODE_PATH by prepending CODE_PATH
		m.put("UTILS_PYTHON_PATH", StringTools.getPathString(
			Arrays.asList(cp, paths.get("UTILS_PYTHON_PATH"))
		));
		m.put("VOICE_SEP_PYTHON_PATH", StringTools.getPathString(
			Arrays.asList(cp, paths.get("VOICE_SEP_PYTHON_PATH"))
		));
		m.put("VOICE_SEP_MATLAB_PATH", StringTools.getPathString(
			Arrays.asList(cp, paths.get("VOICE_SEP_MATLAB_PATH"))
		));
		m.put("ANALYSIS_PYTHON_PATH", StringTools.getPathString(
			Arrays.asList(cp, paths.get("ANALYSIS_PYTHON_PATH"))
		));
		m.put("FORMATS_PYTHON_PATH", StringTools.getPathString(
			Arrays.asList(cp, paths.get("FORMATS_PYTHON_PATH"))
		));
		m.put("VENV_PATH", StringTools.getPathString(
			Arrays.asList(cp, paths.get("VENV_PATH"))
		));

		// 4. Set scripts in m
		m.put("MEI2TBP_SCRIPT", files.get("MEI2TBP_SCRIPT"));
		m.put("BEAM_SCRIPT", files.get("BEAM_SCRIPT"));
		m.put("SCIKIT_SCRIPT", files.get("SCIKIT_SCRIPT"));
		m.put("TENSORFLOW_SCRIPT", files.get("TENSORFLOW_SCRIPT"));
		m.put("MEI_TEMPLATE", files.get("MEI_TEMPLATE"));
		m.put("MODEL_PARAMETERS", files.get("MODEL_PARAMETERS"));
		m.put("VERSION", files.get("VERSION"));
		
//		System.out.println("completed paths:");
//		ToolBox.printMap(m);
//		System.exit(0);

		return m;
	}


	/**
	 * Gets the full path to the parent folder of the folder java is run from. 
	 * - when running java from Eclipse, this parent is fixed, and is always the parent
	 *   of the repository folder (i.e., the folder that holds the src/ and bin/ folders
	 *   holding the class that is run)
	 *   Examples:
	 *   - ui.UI 
	 *     - java is run from F:/research/computation/software/code/eclipse/voice_separation/
	 *     - returns F:/research/computation/software/code/eclipse/
	 *   - tabmapper.TabMapper
	 *     - java is run from F:/research/computation/software/code/eclipse/tabmapper/
	 *     - returns F:/research/computation/software/code/eclipse/
	 * - when running java from the CLI, this parent is not fixed, and is always the
	 *   parent of the CLI's cwd
	 *   
	 * @return
	 */
	private static Path getPathToParent() {
		// Get the current working directory
		Path currentPath = Path.of("");
		return currentPath.toAbsolutePath().getParent();
	}


	/**
	 * Gets the <code>ROOT_PATH</code>, <code>CODE_PATH</code>, and <code>EXE_PATH</code>, 
	 * as defined by the user in <code>config.cfg</code> (located on the <code>CODE_PATH</code>).
	 * 
	 * @param dev <code>true</code> if called in development mode.
	 * 
	 * @return
	 */
	static Map<String, String> getUserDefinedPaths(boolean dev) {
		try {
			Map<String, String> userPaths = new HashMap<>();

			// Get the path to interfaces.CLInterface.class (i.e., the current class)
			// NB Java counts the package 'interfaces' not as dir, but as part of the class
			// (= F:/research/computation/software/code/eclipse/utils/bin/)
//			// Get the path to tools.path.PathTools.class (i.e., the current class)
//			// NB Java counts the packages 'tools' and 'path' not as dirs, but as part of the class
//			// (= F:/research/computation/software/code/eclipse/utils/bin/)
			String classPath = CLInterface.class
//			String classPath = PathTools.class
				.getProtectionDomain()
				.getCodeSource()
				.getLocation()
				.toURI()
				.getPath();

			// Get the path to utils/ 
			// (= F:/research/computation/software/code/eclipse/)
			String codePath;
			if (dev) {
				codePath = new File(classPath)
					.toPath()
					.getParent()
					.getParent()
					.toString();
			}
			else {
				codePath = new File(classPath)
					.toPath()
					.getParent()
					.getParent()
					.getParent()
					.toString();
			}

			// Read config file
			try (BufferedReader br = new BufferedReader(
				new FileReader(StringTools.getPathString(Arrays.asList(codePath)) + CONFIG_FILE))) {
//				new FileReader(getPathString(Arrays.asList(codePath)) + (dev ? CONFIG_FILE_DEV : CONFIG_FILE)))) {
				String line;
				while ((line = br.readLine()) != null) {
					String[] parts = line.split("=");
					if (!line.equals("")) { // not if the line is empty
						userPaths.put(
							// Key
							parts[0].trim(),
							// Value: replace any backward slashes (Windows); remove quotes
							parts[1].trim().replace("\\", "/").replace("\"", "")
						);
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			return userPaths;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}


	/**
	 * Parses the given CLI arguments.
	 *
	 * @param args Has a fixed sequence -- see <code>abtab</code> script.
	 * @param path
	 * @return
	 */
	public static List<Object> parseCLIArgs(String[] args, String path) {
		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("__EMPTY__")) {
				args[i] = "";
			}
		}

		String[] opts = args[OPTS_IND].split(" ");
		String[] defaultVals = args[DEFAULT_VALS_IND].split(" ");
		String uov = args[USER_OPTS_VALS_IND];
		String[] userOptsVals = !uov.equals("") ? uov.split(",") : new String[]{};
//		System.err.println("userOptsVals : " + Arrays.asList(userOptsVals));
//		System.err.println("from CLInterface.parseCLIArgs():");
//		System.err.println("opts : " + Arrays.asList(opts));
//		System.err.println("defs : " + Arrays.asList(defaultVals));
//		System.err.println("user : " + Arrays.asList(userOptsVals));
//		System.exit(0);

		// Populate cliOptsVals with default values
		Map<String, String> cliOptsVals = new LinkedHashMap<String, String>();
		for (int i = 0; i < opts.length; i++) {
			cliOptsVals.put(opts[i], defaultVals[i]);
		}

		// Parse userOptsVals and overwrite any default values in cliOptsVals
		for (String s : userOptsVals) {
			String[] optVal = s.trim().split(" ");
			cliOptsVals.put(optVal[0], optVal[1]);
		}

		// Set piecenames
		List<String> piecenames = new ArrayList<>();
		if (path != null) {
			// All pieces in path
			if (cliOptsVals.get(FILE).equals("n/a")) {
				piecenames.addAll(ToolBox.getFilesInFolder(
					path, cliOptsVals.get(FORMAT).equals("t") ? ALLOWED_FILE_FORMATS : 
					Arrays.asList(MIDIImport.MID_EXT), true
				));
			}
			// Selection of pieces or single piece
			else {
				// Selection of pieces
				if (cliOptsVals.get(FILE).equals("files.txt")) {
					List<String[]> l = StringTools.readCSVFile(Paths.get(path).getParent().resolve("files.txt").toString());
					// tabmapper case where tab and MIDI file do not have the same name
					if (l.get(0).length == 2) {
						l.forEach(s -> piecenames.add(s[0] + "," + s[1]));
					}
					// Other cases
					else {
						l.forEach(s -> piecenames.add(s[0]));
					}
				}
				// Single piece
				else {
					piecenames.add(cliOptsVals.get(FILE));
//					piecenames.add(ToolBox.splitExt(cliOptsVals.get(FILE))[0]);
				}
			}
		}
		
		return Arrays.asList(new Object[]{cliOptsVals, piecenames});
	}


	/**
	 * Sets piece-specific transParams that (can) have CLInterface.INPUT as value.
	 * 
	 * @param cliOptsVals
	 * @param tab
	 * @param tool
	 * @return
	 */
	public static Map<String, String> setPieceSpecificTransParams(Map<String, String> cliOptsVals, Tablature tab, String tool) {
		Tuning[] tunings = tab.getTunings();
		TabSymbolSet tss = tab.getEncoding().getTabSymbolSet();

		if (tool.equals("converter")) {
			// Tuning and type are always INPUT
			cliOptsVals.put(CLInterface.TUNING, tunings[Tablature.ENCODED_TUNING_IND].getName());
			cliOptsVals.put(CLInterface.TYPE, tss.getShortType());
		}
		else if (tool.equals("tabmapper")) {
			// Tuning is always INPUT; type can be INPUT
			cliOptsVals.put(CLInterface.TUNING, tunings[Tablature.ENCODED_TUNING_IND].getName());
			if (cliOptsVals.get(CLInterface.TYPE).equals(CLInterface.INPUT)) {
				cliOptsVals.put(CLInterface.TYPE, tss.getShortType());
			}	
		}
		else if (tool.equals("transcriber-dev")) {
			// Tuning and type are always INPUT
			cliOptsVals.put(CLInterface.TUNING, tunings[Tablature.NORMALISED_TUNING_IND].getName());
			cliOptsVals.put(CLInterface.TYPE, tss.getShortType());
		}
		else if (tool.equals("transcriber")) {
			// Tuning and type can be INPUT
			if (cliOptsVals.get(CLInterface.TUNING).equals(CLInterface.INPUT)) {
				cliOptsVals.put(CLInterface.TUNING, tunings[Tablature.ENCODED_TUNING_IND].getName());
			}
			if (cliOptsVals.get(CLInterface.TYPE).equals(CLInterface.INPUT)) {
				cliOptsVals.put(CLInterface.TYPE, tss.getShortType());
			}
		}

		return cliOptsVals;
	}


	public static Map<String, String> getTranscriptionParams(Map<String, String> cliOptsVals) {
		Map<String, String> transParams = new LinkedHashMap<String, String>();

		List<String> keys = Arrays.asList(TUNING, STAFF, TABLATURE, TYPE, PLACEMENT);
		for (String key : keys) {
			if (cliOptsVals.containsKey(key)) {
				transParams.put(key, cliOptsVals.get(key));
			}
		}

		return transParams;
	}


	public static boolean isWin() {
		String os = System.getProperty("os.name").toLowerCase();
		return os.contains("win");
	}


//	/**
//	 * Converts, for each piece in piecenames, <inPath/piece> into the .tbp format (if it does 
//	 * not exist yet).
//	 * 
//	 * @param inPath
//	 * @param piecenames
//	 */
//	private static void convertToTbp(String inPath, List<String> piecenames) {
//		for (String p : piecenames) {
//			String ip = inPath + p;
//			if (!Files.exists(Paths.get(ip + Encoding.TBP_EXT))) {
//				// .tc file
//				if (Files.exists(Paths.get(ip + TabImport.TC_EXT))) {
//					String s = TabImport.tc2tbp(
//						ToolBox.readTextFile(new File(ip + TabImport.TC_EXT))
//					);
//					ToolBox.storeTextFile(s, new File(ip + Encoding.TBP_EXT));
//				}
//				// .mei file
//				else if (Files.exists(Paths.get(ip + MEIExport.MEI_EXT))) {
//					// TODO luteconv .mei -> .tc; TabImport.tc2tbp()
//				}
//				// .xml file 
//				else if (Files.exists(Paths.get(ip + MEIExport.XML_EXT))) {
//					// TODO luteconv .xml -> .tc; TabImport.tc2tbp()
//				}
//			}
//		}
//	}


//	/**
//	 * Extracts an <code>Encoding</code> from the file in the given format.
//	 * 
//	 * @param path
//	 * @param filename
//	 * @return
//	 */
//	private static Encoding getEncodingFromAnyFormat(String path, String filename) {
//		Encoding e = null;
//		for (String s : CLInterface.ALLOWED_FILE_FORMATS) {
//			if (Files.exists(Paths.get(path + filename + s))) {
//				String rawEncoding;
//				if (s.equals(Encoding.TBP_EXT)) {
//					rawEncoding = ToolBox.readTextFile(new File(path + filename + s));
//				}
//				else {
//					rawEncoding = TabImport.convertToTbp(path, filename + s);
//				}
//				e = new Encoding(rawEncoding, filename, Stage.RULES_CHECKED);
//				break;
//			}
//		}
//		return e;
//	}

}
