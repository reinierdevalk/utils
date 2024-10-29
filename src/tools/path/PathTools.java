package tools.path;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class PathTools {

	private static final String PATHS_FILE = "paths.json";
	private static final String PATHS_FILE_DEV = "paths-dev.json";
	private static final String CONFIG_FILE = "config.cfg";
	private static final String CONFIG_FILE_DEV = "config-dev.cfg";

	
	public static void main(String[] args) {
//		getUserDefinedPaths(true);
		getPaths(true);
	}


	/**
	 * Reads <code>paths.json</code> (located on the <code>CODE_PATH</code>). It is preferred
	 * if this method is called from a <code>main()</code>, and its value then passed on.
	 * 
	 * @param dev <code>true</code> if called in development mode.
	 * 
	 * @return A {@code Map<String, String>}, containing for each key in
	 *         <code>paths.json</code> the value (relative path) extended 
	 *         to its full path.
	 */
	public static final Map<String, String> getPaths(boolean dev) {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);

		Map<String, String> m = null;
		try {
			// 1. Read user-defined paths from .cfg file 
			// ROOT_PATH leads to all things not code (data, datasets, models, templates, ...) 
			// CODE_PATH leads to the code 
			// EXE_PATH leads to the executable (and is only needed in install.sh)
			Map<String, String> userDefinedPaths = getUserDefinedPaths(dev);
			String rp = userDefinedPaths.get("ROOT_PATH");
			String cp = userDefinedPaths.get("LIB_PATH");

			// 2. Read the Map from the JSON file
			// Define the type reference for the Map
			TypeReference<Map<String, Map<String, String>>> typeRef = new TypeReference<Map<String, Map<String, String>>>(){};
			Map<String, Map<String, String>> config = objectMapper.readValue(
				new File(getPathString(Arrays.asList(cp)) + (dev ? PATHS_FILE_DEV : PATHS_FILE)), typeRef
			);
			Map<String, String> paths = config.get("paths");
			Map<String, String> scripts = config.get("scripts");

			// 3. Set paths in m
			m = new LinkedHashMap<String, String>();
			// a. Set ROOT_PATH and CODE_PATH
			m.put("ROOT_PATH", getPathString(Arrays.asList(rp)));
			m.put("CODE_PATH", getPathString(Arrays.asList(cp)));
			// b. Complete paths on ROOT_PATH by prepending ROOT_PATH
			m.put("ENCODINGS_PATH", getPathString(
				Arrays.asList(rp, paths.get("ENCODINGS_PATH"))
			));
			m.put("MIDI_PATH", getPathString(
				Arrays.asList(rp, paths.get("MIDI_PATH"))
			));
			m.put("ENCODINGS_PATH_JOSQUINTAB", getPathString(
				Arrays.asList(rp, paths.get("ENCODINGS_PATH_JOSQUINTAB"))
			));
			m.put("MIDI_PATH_JOSQUINTAB", getPathString(
				Arrays.asList(rp, paths.get("MIDI_PATH_JOSQUINTAB"))
			));
			m.put("DATASETS_PATH", getPathString(
				Arrays.asList(rp, paths.get("DATASETS_PATH"))
			));
			m.put("EXPERIMENTS_PATH", getPathString(
				Arrays.asList(rp, paths.get("EXPERIMENTS_PATH"))
			));
			m.put("MODELS_PATH", getPathString(
				Arrays.asList(rp, paths.get("MODELS_PATH"))
			));
			m.put("TEMPLATES_PATH", getPathString(
				Arrays.asList(rp, paths.get("TEMPLATES_PATH"))
			));
			m.put("ANALYSER_PATH", getPathString(
				Arrays.asList(rp, paths.get("ANALYSER_PATH"))
			));
			m.put("CONVERTER_PATH", getPathString(
				Arrays.asList(rp, paths.get("CONVERTER_PATH"))
			));
			m.put("TABMAPPER_PATH", getPathString(
				Arrays.asList(rp, paths.get("TABMAPPER_PATH"))
			));
			m.put("DIPLOMAT_PATH", getPathString(
				Arrays.asList(rp, paths.get("DIPLOMAT_PATH"))
			));
			m.put("POLYPHONIST_PATH", getPathString(
				Arrays.asList(rp, paths.get("POLYPHONIST_PATH"))
			));
			// c. Complete paths on CODE_PATH by prepending CODE_PATH
			m.put("UTILS_PYTHON_PATH", getPathString(
				Arrays.asList(cp, paths.get("UTILS_PYTHON_PATH"))
			));
			m.put("VOICE_SEP_PYTHON_PATH", getPathString(
				Arrays.asList(cp, paths.get("VOICE_SEP_PYTHON_PATH"))
			));
			m.put("VOICE_SEP_MATLAB_PATH", getPathString(
				Arrays.asList(cp, paths.get("VOICE_SEP_MATLAB_PATH"))
			));
			m.put("ANALYSIS_PYTHON_PATH", getPathString(
				Arrays.asList(cp, paths.get("ANALYSIS_PYTHON_PATH"))
			));

			// 4. Set scripts in m
			m.put("BEAM", scripts.get("BEAM"));
			m.put("SCIKIT", scripts.get("SCIKIT"));
			m.put("TENSORFLOW", scripts.get("TENSORFLOW"));
		} catch (IOException e) {
			e.printStackTrace();
		}
//		System.out.println("completed paths:");
//		for (Map.Entry<String, String> entry : m.entrySet()) {
//			System.out.println(entry.getKey() + " -- " + entry.getValue());
//		}
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
	 * as defined by the user in <code>config.cfg</code> (located on the <code>CODE_PATH</code>.
	 * 
	 * @param dev <code>true</code> if called in development mode.
	 * 
	 * @return
	 */
	public static Map<String, String> getUserDefinedPaths(boolean dev) {
		try {
			Map<String, String> userPaths = new HashMap<>();

			// Get the path to tools.path.PathTools.class (i.e., the current class)
			// NB Java counts the packages 'tools' and 'path' not as dirs, but as part of the class
			// (= F:/research/computation/software/code/eclipse/utils/bin/)
			String classPath = PathTools.class
				.getProtectionDomain()
				.getCodeSource()
				.getLocation()
				.toURI()
				.getPath();

			// Get the path to utils/ 
			// (= F:/research/computation/software/code/eclipse/)
			String codePath = new File(classPath)
				.toPath()
				.getParent()
				.getParent()
				.toString();

			// Read config file
			try (BufferedReader br = new BufferedReader(
				new FileReader(getPathString(Arrays.asList(codePath)) + (dev ? CONFIG_FILE_DEV : CONFIG_FILE)))) {
				String line;
				while ((line = br.readLine()) != null) {
					String[] parts = line.split("=");
					userPaths.put(
						// Key
						parts[0].trim(), 
						// Value: replace any backward slashes (Windows); remove quotes  
						parts[1].trim().replace("\\", "/").replace("\"", "")
					);
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
	 * Constructs a path <code>String</code> from the given list of dir names.
	 * The list elements are added in the order they appear in the list.
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

}
