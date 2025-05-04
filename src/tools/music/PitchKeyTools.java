package tools.music;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.lang3.ArrayUtils;

import conversion.imports.TabImport;
import de.uos.fmt.musitech.data.score.NotationChord;
import de.uos.fmt.musitech.data.score.NotationStaff;
import de.uos.fmt.musitech.data.score.NotationVoice;
import de.uos.fmt.musitech.data.structure.Note;
import external.Tablature;
import interfaces.CLInterface;
import internal.core.Encoding;
import internal.core.ScorePiece;
import internal.core.Encoding.Stage;
import tools.ToolBox;
import tools.text.StringTools;


public class PitchKeyTools {
	// For each key, represented by number of flats (-) or sharps (+), the MIDI 
	// pitch class of the key and its minor parallel
	public static final Map<Integer, Integer[]> KEY_SIG_MPCS;
	static {
		KEY_SIG_MPCS = new LinkedHashMap<Integer, Integer[]>();
		KEY_SIG_MPCS.put(-8, new Integer[]{4, 1}); // Fb/db (simpler as E/c#)
		KEY_SIG_MPCS.put(-7, new Integer[]{11, 8}); // Cb/ab (simpler as B/g#)
		KEY_SIG_MPCS.put(-6, new Integer[]{6, 3}); // Gb/eb (== F#/d#)
		KEY_SIG_MPCS.put(-5, new Integer[]{1, 10}); // Db/bb
		KEY_SIG_MPCS.put(-4, new Integer[]{8, 5}); // Ab/f
		KEY_SIG_MPCS.put(-3, new Integer[]{3, 0}); // Eb/c
		KEY_SIG_MPCS.put(-2, new Integer[]{10, 7}); // Bb/g
		KEY_SIG_MPCS.put(-1, new Integer[]{5, 2}); // F/d
		KEY_SIG_MPCS.put(0, new Integer[]{0, 9}); // C/a
		KEY_SIG_MPCS.put(1, new Integer[]{7, 4}); // G/e
		KEY_SIG_MPCS.put(2, new Integer[]{2, 11}); // D/b
		KEY_SIG_MPCS.put(3, new Integer[]{9, 6}); // A/f#
		KEY_SIG_MPCS.put(4, new Integer[]{4, 1}); // E/c#
		KEY_SIG_MPCS.put(5, new Integer[]{11, 8}); // B/g#
		KEY_SIG_MPCS.put(6, new Integer[]{6, 3}); // F#/d# (== Gb/eb)
		KEY_SIG_MPCS.put(7, new Integer[]{1, 10}); // C#/a# (simpler as Db/bb)
		KEY_SIG_MPCS.put(8, new Integer[]{8, 5}); // G#/e# (simpler as Ab/f) 
	}
	private static final int MAX_NUM_ACCID = 8;
	
	// F#, C#, G#, D#, A#, E#, B#
	private static final List<Integer> KEY_ACCID_MPC_SHARP = Arrays.asList(6, 1, 8, 3, 10, 5, 0);
	public static final List<String> KEY_ACCID_PC_SHARP = Arrays.asList("f", "c", "g", "d", "a", "e", "b");
	// Bb, Eb, Ab, Db, Gb, Cb, Fb
	private static final List<Integer> KEY_ACCID_MPC_FLAT = Arrays.asList(10, 3, 8, 1, 6, 11, 4);
	public static final List<String> KEY_ACCID_PC_FLAT = Arrays.asList("b", "e", "a", "d", "g", "c", "f");
	
	private static boolean verbose = false;


	public static void main(String[] args) {
		if (args.length == 0)  {
			List<Integer> pitches = Arrays.asList(61);
			int numAlt = 2;
			List<Object> grids = createGrids(numAlt, 0);
			Integer[] mpcGrid = (Integer[]) grids.get(0);
			String[] altGrid = (String[]) grids.get(1);
			String[] pcGrid = (String[]) grids.get(2);
			System.out.println(Arrays.asList(mpcGrid));
			System.out.println(Arrays.asList(altGrid));
			System.out.println(Arrays.asList(pcGrid));
			System.out.println("-----");
			List<List<Integer>> aie = new ArrayList<>();
			aie.add(new ArrayList<>());
			aie.add(new ArrayList<>());
			List<Integer> blbl = new ArrayList<>();
			blbl.add(60);
			aie.add(blbl);
			aie.add(new ArrayList<>());
			aie.add(new ArrayList<>());
			System.out.println(aie);
			
			for (int i : pitches) {
				List<Object> ps = spellPitch(
					i, numAlt, Arrays.asList(new Object[]{mpcGrid, altGrid, pcGrid}), aie
				);
				String[] pa = (String[]) ps.get(0);
				System.out.println("MIDI :     " + i);
				System.out.println("pname:     " + pa[0]);
				System.out.println("accid:     " + pa[1]);
				System.out.println("accid.ges: " + pa[2]);
				aie = (List<List<Integer>>) ps.get(1);
				System.out.println(aie);
			}
		}
		// Called from diplomat.py
		else {
			boolean dev = args[CLInterface.DEV_IND].equals(String.valueOf(true));
			String type = args[1];

			// NB If this class is called from Python, the _call_java() function reads the stdout
			// returned by this class, and passes it to json.loads(). Therefore, in this class
			// - System.out.println() (the stdout) must be used to return a json-formatted string
			// - System.err.println() (the stderr) must be used for any debugging -- if the stdout
			//   is used for this, it makes the output returned non-valid json
			// 1. To make grids
			if (type.equals("grids")) {
				verbose = false;
				int numAlt = Integer.parseInt(args[2]);
				int mode = Integer.parseInt(args[3]);

				List<Object> grids = PitchKeyTools.createGrids(numAlt, mode);
				Integer[] mpcGrid = (Integer[]) grids.get(0);
				String[] altGrid = (String[]) grids.get(1);
				String[] pcGrid = (String[]) grids.get(2);

				Map<String, Object> m = new LinkedHashMap<>();
				m.put("mpcGrid", mpcGrid);
				m.put("altGrid", altGrid);
				m.put("pcGrid", pcGrid);
				String pythonDict = StringTools.createJSONString(m);
				System.out.println(pythonDict);
			}
			// 2. To detect key
			else if (type.equals("key")) {
				verbose = false;
				String tuning = args[2];
				String file = args[3];
				Map<String, String> paths = CLInterface.getPaths(dev);
				String filePath = StringTools.getPathString(
					Arrays.asList(paths.get("DIPLOMAT_PATH"), "in"
				));

//				boolean mimic = false;		
//				Encoding e;
//				if (mimic) {
//					// Mimic abtab args (java-style)
//					String opts = "-u -f -v";
//					String defaultVals = "i n/a n";
//					String uov = "-u " + tuning +"," + "-f " + file;
//					String[] argsAbtabMimicked = new String[4];
//					argsAbtabMimicked[CLInterface.DEV_IND] = Boolean.toString(dev);
//					argsAbtabMimicked[CLInterface.OPTS_IND] = opts;
//					argsAbtabMimicked[CLInterface.DEFAULT_VALS_IND] = defaultVals;
//					argsAbtabMimicked[CLInterface.USER_OPTS_VALS_IND] = uov;
//
//					List<Object> parsed = CLInterface.parseCLIArgs(
//						argsAbtabMimicked, StringTools.getPathString(
//							Arrays.asList(paths.get("DIPLOMAT_PATH"), "in")
//						)
//					);
//					Map<String, String> cliOptsVals = (Map<String, String>) parsed.get(0);
//					List<String> pieces = (List<String>) parsed.get(1);
//	
//					verbose = cliOptsVals.get(CLInterface.VERBOSE).equals("y") ? true : false;
//
////					ToolBox.printMap(cliOptsVals);
////					System.err.println(pieces);
//	
//					String rawEncoding = TabImport.convertToTbp(filePath, cliOptsVals.get(CLInterface.FILE));
//					e = new Encoding(rawEncoding, ToolBox.splitExt(file)[0], Stage.RULES_CHECKED);
////					e = CLInterface.getEncodingFromAnyFormat(filePath, cliOptsVals.get(CLInterface.FILE));
////					e = new Encoding(new File(filePath + pieces.get(0) + Encoding.TBP_EXT));
//					e.overwriteTuning(cliOptsVals.get(CLInterface.TUNING));
//				}
//				else {
//					String rawEncoding = TabImport.convertToTbp(filePath, file);
//					Encoding e = new Encoding(rawEncoding, ToolBox.splitExt(file)[0], Stage.RULES_CHECKED);
////					// If file is not tbp: convert
////					e = CLInterface.getEncodingFromAnyFormat(filePath, file);
////					e = new Encoding(new File(filePath + ToolBox.splitExt(file)[0] + Encoding.TBP_EXT));
//					e.overwriteTuning(tuning);
//				}

				String rawEncoding = TabImport.convertToTbp(filePath, file, paths);
				Encoding e = new Encoding(rawEncoding, ToolBox.splitExt(file)[0], Stage.RULES_CHECKED);
				e.overwriteTuning(tuning);

				System.err.print(e.getMetadata());
				int key = detectKey(null, e);
				System.out.println(key);
			}
			// 3. To spell pitch
			else if (type.equals("pitch")) {
				verbose = false;
				int pitch = Integer.parseInt(args[2]);
				int numAlt = Integer.parseInt(args[3]);

				// Convert grids back from String
				String mpcGridStr = args[4];
				Integer[] mpcGrid = (Integer[]) StringTools.parseStringifiedPythonList(
					mpcGridStr, "Array", "Integer"
				);
				String altGridStr = args[5];
				String[] altGrid = (String[]) StringTools.parseStringifiedPythonList(
					altGridStr, "Array", "String"
				);
				String pcGridStr = args[6];
				String[] pcGrid = (String[]) StringTools.parseStringifiedPythonList(
					pcGridStr, "Array", "String"
				);
				// Convert accidsInEffect back from String
				String accidsInEffectStr = args[7];				
				List<List<Integer>> accidsInEffect = 
					accidsInEffectStr.equals("null") ? null : 
					(List<List<Integer>>) StringTools.parseStringifiedPythonList(
						accidsInEffectStr, "List", "Integer"
					);

				List<Object> pitchSpell = spellPitch(
					pitch, numAlt, Arrays.asList(new Object[]{mpcGrid, altGrid, pcGrid}), accidsInEffect
				);
				String[] pa = (String[]) pitchSpell.get(0);
				Map<String, String> m = new LinkedHashMap<>();
				m.put("pname", pa[0]);
				m.put("accid", pa[1]);
				m.put("accid.ges", pa[2]);
				m.put("accidsInEffect", accidsInEffect.toString());	
				String pythonDict = StringTools.createJSONString(m);
				System.out.println(pythonDict);
			}
		}
	}


	/**
	 * Returns the scientific pitch notation for the given MIDI pitch. Covers C0 (MIDI pitch 12) up
	 * to and including C8 (MIDI pitch = 108).
	 * See https://en.wikipedia.org/wiki/Scientific_pitch_notation
	 * 
	 * @param midiPitch
	 * @return
	 */
	// TESTED
	public static String getScientificNotation(int midiPitch) {
		List<Integer> pitchesInt = IntStream.rangeClosed(12, 108).boxed().collect(Collectors.toList());
		List<String> pitchesStr = Arrays.asList(new String[]{
			"C", "C#", "D", "Eb", "E", "F", "F#", "G", "Ab", "A", "Bb", "B"});
		Map<Integer, String> scientific = new LinkedHashMap<Integer, String>();
		for (int i : pitchesInt) {
			scientific.put(i, pitchesStr.get(i%12) + "" + getOctave(i));
		}
		return scientific.get(midiPitch);
	}


	/**
	 * Returns the octave of the given MIDI pitch. Middle C (midiPitch = 60) marks octave 4.
	 * 
	 * @param midiPitch
	 * @return
	 */
	// TESTED
	public static int getOctave(int midiPitch) {
		// Example: 61 is in octave 4: ( (61-1)/12 ) - 1 = 4
		int c = midiPitch - (midiPitch % 12);
		return (c/12) - 1;
	}


	// NOT TESTED (wrapper method)
	public static List<Object> createGrids(int numAlt, int mode) {
		Integer[][] mpcg = makeMIDIPitchClassGrids(mode);
		String[][] ag = makeAlterationGrids(mpcg);
		String[][] pcg = makePitchClassGrids(mode);
		int keyInd = numAlt + MAX_NUM_ACCID;
		Integer[] mpcGrid = mpcg[keyInd];
		String[] altGrid = ag[keyInd];
		String[] pcGrid = pcg[keyInd];

		return Arrays.asList(new Object[]{mpcGrid, altGrid, pcGrid});
	}


	/**
	 * Returns, for each key (starting at 8 flats and ending at 8 sharps), the MIDI pitch classes
	 * for that key.
	 * 
	 * A MIDI pitch class is a note's MIDI pitch % 12, and has one of the values [0-11]. 
	 * 
	 * Example C major: [0, 2, 4, 5, 7, 9, 11]
	 * Example A major: [9, 11, 1, 2, 4, 6, 8]
	 * 
	 * @param mode
	 * @return
	 */
	// TESTED
	static Integer[][] makeMIDIPitchClassGrids(int mode) {
		List<Integer> semitones = Arrays.asList(new Integer[]{2, 2, 1, 2, 2, 2, 1});
		
		Integer[][] mpcGrids = new Integer[KEY_SIG_MPCS.size()][7];
		int i = 0;
		for (Entry<Integer, Integer[]> entry : KEY_SIG_MPCS.entrySet()) {
			int currBeginPitch = entry.getValue()[0];
			List<Integer> asList = new ArrayList<Integer>();
			asList.add(currBeginPitch);
			for (int j = 0; j < semitones.size()-1; j++) {
				asList.add((asList.get(j) + semitones.get(j)) % 12);
			}
			mpcGrids[i] = asList.toArray(new Integer[asList.size()]);
			i++;
		}
		if (mode == 1) {
			for (int j = 0; j < mpcGrids.length; j++) {
				mpcGrids[j] = ArrayUtils.addAll(
					Arrays.copyOfRange(mpcGrids[j], 5, 7), Arrays.copyOfRange(mpcGrids[j], 0, 5)
				);
			}
		}
		return mpcGrids;
	}


	/**
	 * Returns, for each key (starting at 8 flats and ending at 8 sharps), the alterations going
	 * with the pitch classes for that key.
	 * 
	 * An alteration is a flat ("f"), sharp ("s"), double flat ("ff") or double sharp ("x"); no 
	 * alteration is indicated by "n". 
	 * 
	 * @param MIDIPitchClassGrids
	 * @return
	 */
	// TESTED
	static String[][] makeAlterationGrids(Integer[][] MIDIPitchClassGrids) {
		List<Integer> diatonicPitchCl = Arrays.asList(new Integer[]{0, 2, 4, 5, 7, 9, 11});
		String[][] altGrids = new String[KEY_SIG_MPCS.size()][7];
		int i = 0;
		for (Entry<Integer, Integer[]> entry : KEY_SIG_MPCS.entrySet()) {
			int currKey = entry.getKey();
			String alt = currKey < 0 ? "f" : "s";

			List<String> asList = new ArrayList<String>();
			for (int p : MIDIPitchClassGrids[i]) {
				// Add sharp or flat of altered note
				// If there are no enharmonic equivalents
				if (Math.abs(currKey) < 6) {
					if (!diatonicPitchCl.contains(p)) {
						asList.add(alt);
					}
					else {
						asList.add("n");
					}
				}
				// If there are enharmonic equivalents 
				else { //if	(Math.abs(currKey) >= 6) {
					if (!diatonicPitchCl.contains(p)) {
						asList.add(alt);
					}
					else {
						// Enharmonic equivalents are cb (11), fb (4), and bb (9)
						if (alt.equals("f")) {
							if (p == 11 || p == 4) {
								asList.add(alt);
							}
							else if (p == 9) {
								asList.add("ff");
							}
							else {
								asList.add("n");
							}
						}
						// 0, 2, 4, 5, 7, 9, 11
						// Enharmonic equivalents are e# (5), b# (0), and fx (7)
						else if (alt.equals("s")) {
							if (p == 5 || p == 0) {
								asList.add(alt);
							}
							else if (p == 7) {
								asList.add("x");
							}
							else {
								asList.add("n");
							}
						}
					}
				}
			}
			altGrids[i] = asList.toArray(new String[asList.size()]);
			i++;
		}
		return altGrids;
	}


	/**
	 * Returns, for each key (starting at 8 flats and ending at 8 sharps), the pitch classes 
	 * for that key.
	 * 
	 * A pitch class is a note's nominal pitch, and has one of the values 
	 * ["c", "d", "e", "f", "g", "a", "b"].  
	 * 
	 * @mode 
	 * @return
	 */
	// TESTED
	static String[][] makePitchClassGrids(int mode) {
		String[] pitchCl = new String[]{"c", "d", "e", "f", "g", "a", "b"};
		pitchCl = new String[]{"f", "g", "a", "b", "c", "d", "e"};
		List<String> pitchClasses = new ArrayList<String>();
		for (String s : pitchCl) {
			pitchClasses.add(s);
		}

		int fromInd = 0;
		String[][] pcGrids = new String[KEY_SIG_MPCS.size()][7];
		for (int i = 0; i < KEY_SIG_MPCS.size(); i++) {
			// Reorder pitchClasses: split at fromIndex and paste the first part after the second
			List<String> asList = 
				new ArrayList<String>(pitchClasses.subList(fromInd, pitchClasses.size())); 
			List<String> secondHalf = pitchClasses.subList(0, fromInd);
			asList.addAll(secondHalf);
			pcGrids[i] = asList.toArray(new String[asList.size()]);
			// Increment fromInd to be the index of the note a fifth higher
			fromInd = (fromInd + 4) % 7;
		}
		if (mode == 1) {
			for (int j = 0; j < pcGrids.length; j++) {
				pcGrids[j] = ArrayUtils.addAll(
					Arrays.copyOfRange(pcGrids[j], 5, 7), Arrays.copyOfRange(pcGrids[j], 0, 5)
				);
			}
		}
		return pcGrids;
	}


	/**
	 * Gets the root (one of [C, D, E, F, G, A, B]) and the alteration (1 or nothing) for the 
	 * given key and mode. The alteration is 1 if the key has a lowered or raised root, e.g.,
	 * the key of Bb has root B and alteration 1; the key of F# has root F and alteration 1 --
	 * but the key of A has root A and no alteration.
	 * 
	 * @param numAlt
	 * @param mode
	 * @return
	 */
	// TESTED
	public static String[] getRootAndRootAlteration(int numAlt, int mode) {
		Integer[] mpcs = KEY_SIG_MPCS.get(numAlt);
		int mpc = mpcs[mode];
		String[] pitches = new String[]{"C", "", "D", "", "E", "F", "", "G", "", "A", "", "B"};
		String root, rootAlt;
		if (!pitches[mpc].equals("")) {
			root = pitches[mpc];
			rootAlt = "";
		}
		else {
			root = numAlt < 0 ? pitches[mpc+1] : pitches[mpc-1]; 
			rootAlt = "1";
		}
		return new String[]{root, rootAlt};
	}


	/**
	 * Given a <code>ScorePiece</code> or <code>Encoding</code>, detects its key (as the number 
	 * of alterations).
	 *
	 * This method does not always give good results for key signatures with more than five key 
	 * accidentals (KA) because of enharmonicity issues.
	 *
	 * @param sp
	 * @param e
	 * @return
	 */
	// TESTED
	public static int detectKey(ScorePiece sp, Encoding e) {
		List<Integer> pitchClassCounts = getPitchClassCount(sp, e);
		
//		String outp = "";
//		for (String s : Arrays.asList("C", ".", "D", ".", "E", "F", ".", "G", ".", "A", ".", "B")) {
//			outp += s + "   ";
//		}
//		outp += "\r\n";
//		for (int i : pitchClassCounts) {
//			String s = String.valueOf(i);
//			outp += s + " ".repeat(4 - s.length());
//		}
//		System.out.println(outp);

		// Add first double flat/sharp (Bbb/Fx)
		List<Integer> kaMpcFlat = new ArrayList<>(KEY_ACCID_MPC_FLAT);
		kaMpcFlat.add(9);
		List<Integer> kaMpcSharp = new ArrayList<>(KEY_ACCID_MPC_SHARP);
		kaMpcSharp.add(7);

		// Iterate through all flat/sharp accidentals. For each accidental, if there are more
		// notes with the accidental than without it, the key contains the accidental. Break
		// when the condition is no longer met
		Integer[] numAlts = new Integer[2];
		for (int i = 0; i < kaMpcFlat.size(); i++) {
			// Get pitch class of the current accid and that of its natural counterpart
			int pcAccid = kaMpcFlat.get(i);
			// pcNatural is pcAccid+1 (or 0 in the case of 11 (for Cb))
			int pcNatural = (pcAccid + 1) % 12;
			// Update numAlts or break
			if (pitchClassCounts.get(pcAccid) > pitchClassCounts.get(pcNatural)) {
				numAlts[0] = -(i+1);
			}
			else {
				break;
			}
		}

		for (int i = 0; i < kaMpcSharp.size(); i++) {
			// Get pitch class of the current accid and that of its natural counterpart
			int pcAccid = kaMpcSharp.get(i);
			// pcNatural pcAccid-1 (or 11 in the case of the 0 (for B#)
			int pcNatural = (pcAccid + 11) % 12;
			// Update numAlts or break
			if (pitchClassCounts.get(pcAccid) > pitchClassCounts.get(pcNatural)) {
				numAlts[1] = i+1;
			}
			else {
				break;
			}
		}

		// No key with flats or sharps found
		if (numAlts[0] == null && numAlts[1] == null) {
			return 0;
		}
		// Key with flats found
		else if (numAlts[0] != null && numAlts[1] == null) {
			return numAlts[0];
		}
		// Key with sharps found
		else if (numAlts[0] == null && numAlts[1] != null) {
			return numAlts[1];
		}
		// Key with flats and key with sharps found: return the one with
		// fewer accidentals (e.g., Cb (7 flats) == B (5 sharps))
		// When there is a tie, return sharps
		else {
			return Math.abs(numAlts[0]) < numAlts[1] ? numAlts[0] : numAlts[1];
		}
	}


	/**
	 * Counts the pitch class frequencies for the given <code>ScorePiece</code> or <code>Encoding</code>, 
	 * where C is pitch class 0, C#/Db pitch class 1, etc.
	 *
	 * @param sp
	 * @param e
	 * @return A list containing 12 elements, each representing a pitch class. The value of the element
	 *         is the frequency of the represented pitch class.
	 */
	// TESTED
	static List<Integer> getPitchClassCount(ScorePiece sp, Encoding e) {
		List<Integer> pitchClassCounts = new ArrayList<>(Collections.nCopies(12, 0));
		if (sp != null) {
			for (NotationStaff nst : sp.getScore()) {
				for (NotationVoice nv : nst) {
					for (NotationChord nc : nv) {
						for (Note n : nc) {
							int pc = n.getMidiPitch() % 12;
							pitchClassCounts.set(pc, pitchClassCounts.get(pc) +1);
						}
					}
				}
			}
		}
		else {
			Tablature t = new Tablature(e, false);
			Integer[][] btp = t.getBasicTabSymbolProperties();
			for (Integer[] in : btp) {
				int pc = in[Tablature.PITCH] % 12;
				pitchClassCounts.set(pc, pitchClassCounts.get(pc) +1);
			}
		}

		return pitchClassCounts;
	}


	/**
	 * Transposes the given key signature by the given transposition interval (in semitones).
	 * 
	 * @return
	 */
	// TESTED
	public static int transposeKeySig(int ks, int transInterval) {
		// Accidentals for C, Db, D, Eb, E, F, F#, G, Ab, A, Bb, B
		List<Integer> kss = Arrays.asList(0, -5, 2, -3, 4, -1, 6, 1, -4, 3, -2, 5);
		int indTransKs = (kss.indexOf(ks) + transInterval);

		return kss.get((indTransKs < 0) ? indTransKs + 12 : indTransKs % 12);
	}


	/**
	 * Spells the given pitch, considering the given key signature as number of alterations. 
	 * numAlt <= 0 indicates flats; numAlt > 0 indicates sharps (NB: Am/C is considered a key 
	 * signature with zero flats.)
	 *
	 * This method does not always give good results for key signatures with more than five 
	 * key accidentals (KA) because of enharmonicity issues.<br><br>
	 *  
	 * @param pitch
	 * @param numAlt
	 * @param grids: mpcGrid, altGrid, pcGrid
	 * @param accidsInEffect
	 * @return A list containing
	 *         <ul>
	 *         <li>As element 0: a String[] containing pname, accid, and accid.ges (in MEI terminology).</li>
	 *         <li>As element 1: the updated <code>accidsInEffect</code> (if it is not <code>null</code>).</li>
	 *         </ul>
	 */
	// TESTED
	public static List<Object> spellPitch(int pitch, int numAlt, List<Object> grids, List<List<Integer>> accidsInEffect) {
		String pname = "";
		String accid = "";
		String accidGes = "";

		Integer[] mpcGrid = (Integer[]) grids.get(0); 
		String[] altGrid = (String[]) grids.get(1);  
		String[] pcGrid = (String[]) grids.get(2);

		List<Integer> doubleFlatsInEffect = null;
		List<Integer> flatsInEffect = null; 
		List<Integer> naturalsInEffect = null;
		List<Integer> sharpsInEffect = null;
		List<Integer> doubleSharpsInEffect = null;
		if (accidsInEffect != null) {
			doubleFlatsInEffect = accidsInEffect.get(0);
			flatsInEffect = accidsInEffect.get(1);
			naturalsInEffect = accidsInEffect.get(2);
			sharpsInEffect = accidsInEffect.get(3);
			doubleSharpsInEffect = accidsInEffect.get(4);
		}

		int mpc = pitch % 12; // value is between and including [0, 11]
		boolean considerContext = accidsInEffect != null && !accidsInEffect.contains(null);

		// a. pitch is in key
		if (Arrays.asList(mpcGrid).contains(mpc)) {
			if (verbose) System.err.println("pitch is in key");
			int pcInd = Arrays.asList(mpcGrid).indexOf(mpc);
			pname = pcGrid[pcInd];
			if (!considerContext) {
				accid = altGrid[pcInd];
			}
			else {
				// Previously double flat (but must be flat)
				if (doubleFlatsInEffect.contains(pitch-1)) {
					accid = "f";
					doubleFlatsInEffect.remove(doubleFlatsInEffect.indexOf(pitch-1));
				}
				// Previously flat (but must be natural)
				else if (flatsInEffect.contains(pitch-1)) {
					accid = "n";
					flatsInEffect.remove(flatsInEffect.indexOf(pitch-1));
				}
				// Previously natural (but must be flat)
				else if (naturalsInEffect.contains(pitch+1) && altGrid[pcInd].equals("f")) {
					accid = "f";
					naturalsInEffect.remove(naturalsInEffect.indexOf(pitch+1));
				}
				// Previously natural (but must be sharp)
				else if (naturalsInEffect.contains(pitch-1) && altGrid[pcInd].equals("s")) {
					accid = "s";
					naturalsInEffect.remove(naturalsInEffect.indexOf(pitch-1));
				}
				// Previously sharp (but must be natural)
				else if (sharpsInEffect.contains(pitch+1)) {
					accid = "n";
					sharpsInEffect.remove(sharpsInEffect.indexOf(pitch+1));
				}
				// Previously double sharp (but must be sharp)
				else if (doubleSharpsInEffect.contains(pitch+1)) {
					accid = "f";
					doubleSharpsInEffect.remove(doubleSharpsInEffect.indexOf(pitch+1));
				}
				// No accidental
				else {
					accid = "";
					if (getMIDIPitchClassKeySigs(numAlt).contains(mpc)) {
						accidGes = numAlt <= 0 ? "f" : "s";
					}
				}
			}
		}
		// b. pitch is not in key
		else {
			// Iterate over all key sigs and find the closest key sig that has pitch
			List<Integer> kss = new ArrayList<>(KEY_SIG_MPCS.keySet());
			int indNumAlt = kss.indexOf(numAlt);
			outerLoop: for (int i = 1; i <= KEY_SIG_MPCS.size() - 1; i++) {
				// Check if key sig to the left or right contains mpc
				int currNumAltLeft = (indNumAlt - i) < 0 ? Integer.MAX_VALUE : kss.get(indNumAlt - i);
				int currNumAltRight = (indNumAlt + i) >= kss.size() ? Integer.MAX_VALUE : kss.get(indNumAlt + i);
				// NB The key sig to the right must be checked first, so that the case of the LLT for the minor 
				// parallel is spelled correctly. In this case, the distance to the key that contains mpc is the 
				// same left and right. Example:
				// - 68 in C should be spelled G# (it is the LLT for minor parallel)
				//   - distance to first ks that has G# (3) is three steps to the right from 0 
				//   - distance to first ks that has Ab (-3) is three steps to the left from 0
				for (int currNumAlt : Arrays.asList(currNumAltRight, currNumAltLeft)) {
					if (currNumAlt != Integer.MAX_VALUE) {						
						List<Object> currGrids = createGrids(currNumAlt, 0);
						Integer[] currMpcGrid = (Integer[]) currGrids.get(0);
						String[] currAltGrid = (String[]) currGrids.get(1);
						String[] currPcGrid = (String[]) currGrids.get(2);

						// If mpc is in the current key
						if (Arrays.asList(currMpcGrid).contains(mpc)) {
							int currIndMpc = Arrays.asList(currMpcGrid).indexOf(mpc);
							pname = currPcGrid[currIndMpc];
							accid = currAltGrid[currIndMpc];
							if (considerContext) {
								boolean isInEffect = false; 
								// If mpc is a KA in the current key: flat or sharp
								if (getMIDIPitchClassKeySigs(currNumAlt).contains(mpc)) {
									// Flats
									if (currNumAlt <= 0) {
										// Not a double flat
										if (!accid.equals("ff")) {
											isInEffect = addToListIfNotInList(flatsInEffect, pitch);
										}
										// Double flat
										else {
											isInEffect = addToListIfNotInList(doubleFlatsInEffect, pitch);
										}
									}
									// Sharps
									else {
										// Not a double sharp
										if (!accid.equals("x")) {
											isInEffect = addToListIfNotInList(sharpsInEffect, pitch);
										}
										// Double sharp
										else {
											isInEffect = addToListIfNotInList(doubleSharpsInEffect, pitch);
										}
									}
								}
								// If mpc is not a KA in the current key: natural
								else {
									isInEffect = addToListIfNotInList(naturalsInEffect, pitch);
								}
								// If pitch was already an accidental in effect: set accidGes
								if (isInEffect) {
									accidGes = accid;
									accid = "";
								}
							}
							break outerLoop;
						}
					}
				}
			}
		}
		String[] pa = new String[]{pname, accid, accidGes};
		return Arrays.asList(new Object[]{pa, accidsInEffect});
	}


	/**
	 * Returns, for the given key, the MIDI pitch classes of the key signature for that key. 
	 * A MIDI pitch class is a note's MIDI pitch % 12, and has one of the values [0-11]. 
	 * 
	 * Example Ab major: [10, 3, 8, 1] (= Bb, Eb, Ab, Dd)
	 * Example A major: [6, 1, 8] (= F#, C#, G#)
	 * 
	 * @param key
	 * @return
	 */
	// TESTED
	static List<Integer> getMIDIPitchClassKeySigs(int numAlt) {
		List<Integer> mpcKeySigs = new ArrayList<Integer>();

		// Flats
		if (numAlt < 0) {
			int end = numAlt >= -7 ? -numAlt : 7;
			mpcKeySigs.addAll(KEY_ACCID_MPC_FLAT.subList(0, end));
			// Double flat (first flat becomes Bbb)
			if (numAlt == -8) {
				mpcKeySigs.set(0, mpcKeySigs.get(0) - 1);
			}
		}
		// Sharps
		else if (numAlt > 0) {
			int end = numAlt <= 7 ? numAlt : 7;
			mpcKeySigs.addAll(KEY_ACCID_MPC_SHARP.subList(0, end));
			// Double sharp (first sharp becomes Fx)
			if (numAlt == 8) {
				mpcKeySigs.set(0, mpcKeySigs.get(0) + 1);
			}
		}

		return mpcKeySigs;
	}


	/**
	 * Adds the given element to the given list if it is not already in it.
	 * 
	 * @param list
	 * @param element
	 * @return <code>false</code> if the element is not already in the list; <code>true</code> if it is.
	 */
	// TESTED
	static boolean addToListIfNotInList(List<Integer> list, int element) {
		if (!list.contains(element)) {
			list.add(element);
			return false;
		}
		return true;
	}


	/**
	 * Spells the given pitch, considering the given key signature as number of alterations. 
	 * numAlt <= 0 indicates flats; numAlt > 0 indicates sharps (NB: Am/C is considered a key 
	 * signature with zero flats.)
	 *
	 * The method works only for key signatures with no more than five key accidentals (KA)
	 * because of enharmonicity problems (the sixth flat/sharp is Cb/E#) and the occurrence
	 * of double flats or double sharps. However, alterations may occasionally lead to double
	 * sharps or flats.<br><br>
	 * 
	 * Fb Cb Gb Db Ab Eb Bb X F# C# G# D# A# E# B#
	 * -7 -6 -5 -4 -3 -2 -1 0 1  2  3  4  5  6  7
	 * key = G
	 * midi nr = 10: Bb or A#?
	 *
	 * Sequence of determination
	 * <ul>
	 * <li>1. pitch is the next or second-next KA.</li>
	 * <li>2. pitch is a naturalised KA.</li>
	 * <li>3. pitch is the upper or lower leading tone (ULT/LLT) for minor (or for the minor parallel).</li>
	 * <li>4. pitch is the raised third (R3) for minor (or for the minor parallel).</li>
	 * <li>5. pitch is raised sixth (R6) for minor (or for the minor parallel).</li>
	 * </ul>
	 * 
	 * This covers all non-in-key pitches within the octave. Examples
	 * <ul>
	 * <li>Am  (0b): A,  Bb (1), B,  C,  C# (4), D, Eb (1), E,  F,  F# (5), G, G# (3), A</li>
	 * <li>Dm  (1b): D,  Eb (1), E,  F,  F# (4), G, Ab (1), A,  Bb, B  (2), C, C# (3), D</li>
	 * <li>Gm  (2b): G,  Ab (1), A,  Bb, B  (2), C, Db (1), D,  Eb, E  (2), F, F# (3), G</li>
	 * <li>F#m (3#): F#, G  (2), G#, A,  A# (4), B, C  (2), C#, D,  D# (1), E, E# (3), F#</li>
	 * <li>Em  (1#): E,  E# (2), F#, G,  G# (4), A, A# (x), B,  C,  C# (5), D, D# (3), E </li>
	 * </ul>
	 *  
	 * @param pitch
	 * @param numAlt
	 * @param grids: mpcGrid, altGrid, pcGrid
	 * @param accidsInEffect
	 * @return A list containing
	 *         <ul>
	 *         <li>As element 0: a String[] containing pname, accid, and accid.ges (in MEI terminology).</li>
	 *         <li>As element 1: the updated <code>accidsInEffect</code> (if it is not <code>null</code>).</li>
	 *         </ul>
	 */
	// TESTED
	public static List<Object> spellPitchDeprecated(int pitch, int numAlt, List<Object> grids, List<List<Integer>> accidsInEffect) {
		String pname = "";
		String accid = "";
		String accidGes = "";

		Integer[] mpcGrid = (Integer[]) grids.get(0); 
		String[] altGrid = (String[]) grids.get(1);  
		String[] pcGrid = (String[]) grids.get(2);

		String keySigAccidType = numAlt <= 0 ? "f" : "s";
		// Key sig accidentals as MIDI pitch classes (e.g. 10, 3 for Bb, Eb)
		List<Integer> keySigAccidMpc = getMIDIPitchClassKeySigs(numAlt);


		List<Integer> doubleFlatsInEffect = null;
		List<Integer> flatsInEffect = null; 
		List<Integer> naturalsInEffect = null;
		List<Integer> sharpsInEffect = null;
		List<Integer> doubleSharpsInEffect = null;
		if (accidsInEffect != null) {
			doubleFlatsInEffect = accidsInEffect.get(0);
			flatsInEffect = accidsInEffect.get(1);
			naturalsInEffect = accidsInEffect.get(2);
			sharpsInEffect = accidsInEffect.get(3);
			doubleSharpsInEffect = accidsInEffect.get(4);
		}

		List<Integer> mpcGridList = Arrays.asList(mpcGrid);
		int mpc = pitch % 12; // value is between and including [0, 11]
		List<String> accids = Arrays.asList(new String[]{"ff", "f", "n", "s", "x"});
		boolean considerContext = accidsInEffect != null && !accidsInEffect.contains(null);
		boolean isMinor = mpcGrid[2] - mpcGrid[0] == 3 || Math.abs(mpcGrid[2] - mpcGrid[0]) == 9;

		// Get the grids for the nominal major of minor (or the minor parallel), e.g., 
		// E for Em/G; B for Bm/D; etc.		
		Integer[] mpcGridNomMajOfMin = 
			isMinor ? Arrays.copyOf(mpcGrid, mpcGrid.length) :
			ArrayUtils.addAll(Arrays.copyOfRange(mpcGrid, 5, 7), Arrays.copyOfRange(mpcGrid, 0, 5));
		mpcGridNomMajOfMin[2] = (mpcGridNomMajOfMin[2] + 1) % 12;
		mpcGridNomMajOfMin[5] = (mpcGridNomMajOfMin[5] + 1) % 12;
		mpcGridNomMajOfMin[6] = (mpcGridNomMajOfMin[6] + 1) % 12;
		String[] altGridNomMajOfMin = 	
			isMinor ? Arrays.copyOf(altGrid, altGrid.length) :
			ArrayUtils.addAll(Arrays.copyOfRange(altGrid, 5, 7), Arrays.copyOfRange(altGrid, 0, 5));
		altGridNomMajOfMin[2] = accids.get(accids.indexOf(altGridNomMajOfMin[2]) + 1);
		altGridNomMajOfMin[5] = accids.get(accids.indexOf(altGridNomMajOfMin[5]) + 1);
		altGridNomMajOfMin[6] = accids.get(accids.indexOf(altGridNomMajOfMin[6]) + 1);
		String[] pcGridNomMajOfMin =
			isMinor ? Arrays.copyOf(pcGrid, pcGrid.length) :
			ArrayUtils.addAll(Arrays.copyOfRange(pcGrid, 5, 7), Arrays.copyOfRange(pcGrid, 0, 5));

		// a. pitch is in key
		if (mpcGridList.contains(mpc)) {
			if (verbose) System.err.println("pitch is in key");
			int pcInd = mpcGridList.indexOf(mpc);
			pname = pcGrid[pcInd];
			if (!considerContext) {
				accid = altGrid[pcInd];
			}
			else {
				// Previously double flat (but must be flat)
				if (doubleFlatsInEffect.contains(pitch-1)) {
					accid = "f";
					doubleFlatsInEffect.remove(doubleFlatsInEffect.indexOf(pitch-1));
				}
				// Previously flat (but must be natural)
				else if (flatsInEffect.contains(pitch-1)) {
					accid = "n";
					flatsInEffect.remove(flatsInEffect.indexOf(pitch-1));
				}
				// Previously natural (but must be flat)
				else if (naturalsInEffect.contains(pitch+1) && altGrid[pcInd].equals("f")) {
					accid = "f";
					naturalsInEffect.remove(naturalsInEffect.indexOf(pitch+1));
				}
				// Previously natural (but must be sharp)
				else if (naturalsInEffect.contains(pitch-1) && altGrid[pcInd].equals("s")) {
					accid = "s";
					naturalsInEffect.remove(naturalsInEffect.indexOf(pitch-1));
				}
				// Previously sharp (but must be natural)
				else if (sharpsInEffect.contains(pitch+1)) {
					accid = "n";
					sharpsInEffect.remove(sharpsInEffect.indexOf(pitch+1));
				}
				// Previously double sharp (but must be sharp)
				else if (doubleSharpsInEffect.contains(pitch+1)) {
					accid = "f";
					doubleSharpsInEffect.remove(doubleSharpsInEffect.indexOf(pitch+1));
				}
				// No accidental
				else {
					accid = "";
					if (keySigAccidMpc.contains(mpc)) {
						accidGes = keySigAccidType;
					}
				}
			}
		}
		// b. pitch is not in key
		else {
			List<Integer> mpcKeySigs = getMIDIPitchClassKeySigs(numAlt);
			boolean isNextOrSecondNextKA = false;
			boolean isNaturalisedKA = false;
			boolean isLLTForMinor = false;
			boolean isR3ForMinor = false;

			// 1. pitch is the next or second-next KA
			// Flats
			if (numAlt <= 0) {
				String alt = altGrid[mpcGridList.indexOf((mpc+1) % 12)];
				int indLastKeyAccid = 
					(numAlt == 0) ? -1 : KEY_ACCID_MPC_FLAT.indexOf(mpcKeySigs.get(mpcKeySigs.size()-1));
				for (int incr : new Integer[]{1, 2}) {
					if (KEY_ACCID_MPC_FLAT.get(indLastKeyAccid + incr) == mpc) {
						if (verbose) System.err.println("pitch is next or second-next KA (flats)");
						pname = KEY_ACCID_PC_FLAT.get(indLastKeyAccid + incr);
						accid = accids.get(accids.indexOf(alt) - 1);
						if (considerContext) {
							// Since keys sigs with double KAs are not considered, accid can only be "f"
							if (!flatsInEffect.contains(pitch)) {
								flatsInEffect.add(pitch);	
							}
							else {
								accid = "";
								accidGes = "f";
							}
						}
						isNextOrSecondNextKA = true;
						break;
					}
				}
			}
			// Sharps
			else if (numAlt > 0) {
				String alt = altGrid[mpcGridList.indexOf(((mpc-1) + 12) % 12)];
				int indLastKeyAccid = 
					/*(numAlt == 0) ? -1 : */ KEY_ACCID_MPC_SHARP.indexOf(mpcKeySigs.get(mpcKeySigs.size()-1));
				for (int incr : new Integer[]{1, 2}) {
					if (KEY_ACCID_MPC_SHARP.get(indLastKeyAccid + incr) == mpc) {
						if (verbose) System.err.println("pitch is next or second-next KA (sharps)");
						pname = KEY_ACCID_PC_SHARP.get(indLastKeyAccid + incr);
						accid = accids.get(accids.indexOf(alt) + 1);
						if (considerContext) {
							// Since keys sigs with double KAs are not considered, accid can only be "s"
							if (!sharpsInEffect.contains(pitch)) {
								sharpsInEffect.add(pitch);
							}
							else {
								accid = "";
								accidGes = "s";
							}
						}
						isNextOrSecondNextKA = true;
						break;
					}
				}
			}
			if (!isNextOrSecondNextKA) {
				// 2. pitch is a naturalised KA
				if (numAlt < 0 && mpcKeySigs.contains((pitch-1) % 12) ||
					numAlt > 0 && mpcKeySigs.contains((pitch+1) % 12)) {
					// Exception for LLT; continue to 3. below  
					if (numAlt == 3 && mpc == 5 || numAlt == 4 && mpc == 0 || numAlt == 5 && mpc == 7) {
						if (verbose) System.err.println("pitch is LLT (sharps) (hardcoded)");
					}
					else {
						if (verbose) System.err.println("pitch is naturalised KA");
						int pcInd = -1;
						// Flats
						if (numAlt < 0) {
							if (mpcKeySigs.contains((pitch-1) % 12)) {
								pcInd = mpcGridList.indexOf((pitch-1) % 12);
							}
						}
						// Sharps
						else {
							if (mpcKeySigs.contains((pitch+1) % 12)) {
								pcInd = mpcGridList.indexOf((pitch+1) % 12);
							}
						}
						pname = pcGrid[pcInd];
						accid = "n";
						if (considerContext) {
							// Since keys sigs with double KAs are not considered, accid can only be "n"
							if (!naturalsInEffect.contains(pitch)) {
								naturalsInEffect.add(pitch);
							}
							else {
								accid = "";
								accidGes = "n";
							}
						}
						isNaturalisedKA = true;
					}
				}
				if (!isNaturalisedKA) {
					// 3. pitch is the upper or lower leading tone (ULT/LLT) for minor (or for the minor parallel)
					// a. The ULT case is fully covered above
					//    - flats: the ULT is the next KA
					//    - sharps: the ULT is the last KA, naturalised
					// b. The LLT case is partly covered above
					//	  - zero, one, or two flats: the LLT still has to be calculated
					//    - three flats or more: the LLT is the third-last KA, naturalised   
					//	  - one or two sharps: the LLT still has to be calculated
					//    - three sharps or more: the LLT is the *enharmonic equivalent* of the
					//      third-last KA, naturalised (E# = F; B# = C; Fx = G), and therefore 
					//      caught at 2. above and hardcoded at 3.
					if (mpc == mpcGridNomMajOfMin[mpcGridNomMajOfMin.length-1]) {
						if (verbose) System.err.println("pitch is LLT for minor");
						if (numAlt == 3 && mpc == 5 || numAlt == 4 && mpc == 0 || numAlt == 5 && mpc == 7) {
							pname = numAlt == 3 ? "e" : (numAlt == 4 ? "b" : "f");
							accid = numAlt == 3 ? "s" : (numAlt == 4 ? "s" : "x");
						}
						else {
							pname = pcGridNomMajOfMin[pcGrid.length-1];
							accid = altGridNomMajOfMin[pcGrid.length-1];
							if (considerContext) {
								// Since keys sigs with double KAs are not considered, accid can only be
								// "n", "s", or "x"
								if (accid.equals("n")) {
									if (!naturalsInEffect.contains(pitch)) {
										naturalsInEffect.add(pitch);
									}
									else { 
										accid = "";
										accidGes = "n";
									}
								}
								else if (accid.equals("s")) {
									if (!sharpsInEffect.contains(pitch)) {
										sharpsInEffect.add(pitch);
									}
									else {
										accid = "";
										accidGes = "s";
									}
								}
								else if (accid.equals("x")) {
									if (!doubleSharpsInEffect.contains(pitch)) {
										doubleSharpsInEffect.add(pitch);
									}
									else {
										accid = "";
										accidGes = "x";
									}
								}
							}
							isLLTForMinor = true;
						}
					}
					if (!isLLTForMinor) {
						// 4. pitch is the raised third (R3) for minor (or for the minor parallel)
						//    This is partly covered above
						//    - zero or one flats: the R3 still has to be calculated
						//    - two flats or more: the R3 is the second-last KA, naturalised
						//    - sharps: the R3 is the second-next KA
						if (mpc == mpcGridNomMajOfMin[2]) {
							if (verbose) System.err.println("pitch is R3 for minor");
							pname = pcGridNomMajOfMin[2];
							accid = altGridNomMajOfMin[2];
							if (considerContext) {
								// Since keys sigs with double KAs are not considered, accid can only be 
								// "n" or "s"
								if (accid.equals("n")) {
									if (!naturalsInEffect.contains(pitch)) {
										naturalsInEffect.add(pitch);
									}
									else {
										accid = "";
										accidGes = "n";
									}
								}
								else if (accid.equals("s")) {
									if (!sharpsInEffect.contains(pitch)) {
										sharpsInEffect.add(pitch);
									}
									else {
										accid = "";
										accidGes = "s";
									}
								}
							}
							isR3ForMinor = true;							
						}
						if (!isR3ForMinor) {
							// 5. pitch is the raised sixth (R6) for minor (or for the minor parallel)
							//    This is partly covered above
							//    - zero flats: the R6 still has to be calculated
							//    - one flat or more: the R6 is the last KA, naturalised
							//    - sharps: the R6 is the next KA
							if (mpc == mpcGridNomMajOfMin[5]) {
								if (verbose) System.err.println("pitch is R6 for minor");
								pname = pcGridNomMajOfMin[5];
								accid = altGridNomMajOfMin[5];
								if (considerContext) {
									// Since keys sigs with double KAs are not considered, accid can only be
									// "n" or "s"
									if (accid.equals("n")) {	
										if (!naturalsInEffect.contains(pitch)) {
											naturalsInEffect.add(pitch);
										}
										else { 
											accid = "";
											accidGes = "n";
										}
									}
									else if (accid.equals("s")) {
										if (!sharpsInEffect.contains(pitch)) {
											sharpsInEffect.add(pitch);
										}
										else { 
											accid = "";
											accidGes = "s";
										}
									}
								}
							}
							else {

							}
						}
					}
				}
			}
		}
		String[] pa = new String[]{pname, accid, accidGes};
		return Arrays.asList(new Object[]{pa, accidsInEffect});
	}


//	private List<String[][]> combine(int mode) {
//		List<String[][]> combined = new ArrayList<String[][]>();
//		Integer[][] mpcg = makeMIDIPitchClassGrids(mode);
//		String[][] ag = makeAlterationGrids(mpcg);
//		String[][] pcg = makePitchClassGrids(mode);
//		for (int i = 0; i < KEY_SIG_MPCS.size(); i++) {
//			String[][] curr = new String[3][7];
//			// MIDI pitch classes
//			 
//		}		
//		return combined;
//	}

}
