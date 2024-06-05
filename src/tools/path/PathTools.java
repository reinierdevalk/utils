package tools.path;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class PathTools {

	private static final String JSON_FILE = "paths.json";
	private static final String CONFIG_FILE = "config.cfg";


	/**
	 * Reads <code>paths.json</code> (located on the <code>CODE_PATH</code>).
	 * 
	 * @return A {@code Map<String, String>}, containing for each key in
	 *         <code>paths.json</code> the value (relative path) extended 
	 *         to its full path.
	 */
	public static final Map<String, String> getPaths() {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);

		Map<String, String> m = null;
		try {
			// Define the type reference for the Map
			TypeReference<Map<String, String>> typeRef = new TypeReference<Map<String, String>>(){};

			// Read JSON file and convert to a Map with specific type
			Map<String, String> userDefinedPaths = getUserDefinedPaths();
			String rootPath = userDefinedPaths.get("ROOT_PATH");
			String codePath = userDefinedPaths.get("CODE_PATH");
			m = objectMapper.readValue(
				new File(getPathString(Arrays.asList(rootPath, codePath)) + JSON_FILE), typeRef
			);

			// 1. Set ROOT_PATH
			m.put("ROOT_PATH", rootPath);

			// 2. Complete paths on ROOT_PATH by prepending ROOT_PATH
			m.put("CODE_PATH", getPathString(
				Arrays.asList(rootPath, codePath)
			));
			m.put("DEPLOYMENT_DEV_PATH", getPathString(
				Arrays.asList(rootPath, m.get("DEPLOYMENT_DEV_PATH"))
			));
			m.put("DATA_PATH", getPathString(
				Arrays.asList(rootPath, m.get("DATA_PATH"))
			));
			m.put("EXPERIMENTS_PATH", getPathString(
				Arrays.asList(rootPath, m.get("EXPERIMENTS_PATH"))
			));
			m.put("TEMPLATES_PATH", getPathString(
				Arrays.asList(rootPath, m.get("TEMPLATES_PATH"))
			));
			m.put("MODELS_PATH", getPathString(
				Arrays.asList(rootPath, m.get("MODELS_PATH"))
			));

			// 3. Complete ENCODINGS and MIDI paths by prepending completed DATA_PATH
			String dataPath = m.get("DATA_PATH");
			m.put("ENCODINGS_PATH", getPathString(
				Arrays.asList(dataPath, m.get("ENCODINGS_PATH"))
			));
			m.put("MIDI_PATH", getPathString(
				Arrays.asList(dataPath, m.get("MIDI_PATH"))
			));
			m.put("ENCODINGS_PATH_JOSQUINTAB", getPathString(
				Arrays.asList(dataPath, m.get("ENCODINGS_PATH_JOSQUINTAB"))
			));
			m.put("MIDI_PATH_JOSQUINTAB", getPathString(
				Arrays.asList(dataPath, m.get("MIDI_PATH_JOSQUINTAB"))
			));
			m.put("DATASETS_PATH", getPathString(
				Arrays.asList(dataPath, m.get("DATASETS_PATH"))
			));
//			for (Map.Entry<String, String> entry : m.entrySet()) {
//				System.out.println(entry.getKey() + " -- " + entry.getValue());
//			}
//			System.exit(0);
		} catch (IOException e) {
			e.printStackTrace();
		}

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
	 * Gets the <code>ROOT_PATH</code>, <code>CODE_PATH</code>, and <code>PATH_PATH</code>, 
	 * as defined by the user in <code>config.cfg</code> (located on the <code>CODE_PATH</code>.
	 * 
	 * @return
	 */
	public static Map<String, String> getUserDefinedPaths() {
		try {
			Map<String, String> userPaths = new HashMap<>();

			// Get the path to the current class
			// (F:/research/computation/software/code/eclipse/utils/bin/ contains
			//  tools.path.PathTools.class -- note that the packages tools and path
			//  are not considered dirs here)
			String classPath = PathTools.class
				.getProtectionDomain()
				.getCodeSource()
				.getLocation()
				.toURI()
				.getPath();

			// Get the path to utils/ (i.e., CODE_PATH)
			// (F:/research/computation/software/code/eclipse/ contains utils/)
			String codePath = new File(classPath)
				.toPath()
				.getParent()
				.getParent()
				.toString();

			// Read config.cfg
			try (BufferedReader br = new BufferedReader(
				new FileReader(getPathString(Arrays.asList(codePath)) + CONFIG_FILE))) {
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
		pathStr += "/";

		return pathStr;
	}

}
