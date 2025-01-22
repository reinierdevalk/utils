package tools.music;

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

import de.uos.fmt.musitech.data.score.NotationChord;
import de.uos.fmt.musitech.data.score.NotationStaff;
import de.uos.fmt.musitech.data.score.NotationVoice;
import de.uos.fmt.musitech.data.structure.Note;
import internal.core.ScorePiece;
import tools.text.StringTools;

//import de.uos.fmt.musitech.data.score.NotationVoice;
//import de.uos.fmt.musitech.utility.math.Rational;

public class PitchKeyTools {
	// For each key, represented by number of flats (-) or sharps (+), the MIDI 
	// pitch class of the key and its minor parallel
	public static final Map<Integer, Integer[]> KEY_SIG_MPCS;
	static {
		KEY_SIG_MPCS = new LinkedHashMap<Integer, Integer[]>();
		KEY_SIG_MPCS.put(-7, new Integer[]{11, 8}); // Cb/ab
		KEY_SIG_MPCS.put(-6, new Integer[]{6, 3}); // Gb/eb
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
		KEY_SIG_MPCS.put(6, new Integer[]{6, 3}); // F#/d#
		KEY_SIG_MPCS.put(7, new Integer[]{1, 10}); // C#/a#
	}
	
	// F#, C#, G#, D#, A#, E#, B#
	private static final List<Integer> KEY_ACCID_MPC_SHARP = Arrays.asList(6, 1, 8, 3, 10, 5, 0); 
	public static final List<String> KEY_ACCID_PC_SHARP = Arrays.asList("f", "c", "g", "d", "a", "e", "b");
	// Bb, Eb, Ab, Db, Gb, Cb, Fb
	private static final List<Integer> KEY_ACCID_MPC_FLAT = Arrays.asList(10, 3, 8, 1, 6, 11, 4);
	public static final List<String> KEY_ACCID_PC_FLAT = Arrays.asList("b", "e", "a", "d", "g", "c", "f");
//	// ks at ind i has accids up to and including ind i in KEY_ACCID_PC_FLAT; e.g., Eb (ind=2) has Bb, Eb, Ab 
//	public static final List<String> KEYS_PC_FLAT = Arrays.asList("f", "b", "e", "a", "d");
	
	private static boolean verbose = false;

	public static void main(String[] args) {
		
	}


	public static void main2(String[] args) {
		if (args.length != 0) {
			// 1. To make grids
			if (args.length == 2) {
				verbose = false;
				int numAlt = Integer.parseInt(args[0]);
				int mode = Integer.parseInt(args[1]) == 0 ? 0 : 1;
				List<Object> grids = PitchKeyTools.createGrids(numAlt, mode);
				Integer[] mpcGrid = (Integer[]) grids.get(0);
				String[] altGrid = (String[]) grids.get(1);
				String[] pcGrid = (String[]) grids.get(2);
				String dict = 
					"{\"mpcGrid\": " + Arrays.toString(mpcGrid) + ", " + 
//					"\"altGrid\": " + Arrays.toString(altGrid) + ", " +
//					"\"pcGrid\": " + Arrays.toString(pcGrid) + "}"; 
					"\"altGrid\": " + ("[" + 
					Arrays.asList(altGrid).stream()
					.map(s -> "\"" + s + "\"") // s needs to be placed in double quotes for json.loads() 
					.collect(Collectors.joining(", ")) + "]") + ", " +
					"\"pcGrid\": " + ("[" + 
					Arrays.asList(pcGrid).stream()
					.map(s -> "\"" + s + "\"") // s needs to be placed in double quotes for json.loads()
					.collect(Collectors.joining(", ")) + "]") + "}";
				System.out.println(dict);
			}
			// 2. To spell pitch
			else {
				verbose = true;
				int pitch = Integer.parseInt(args[0]);
				int numAlt = Integer.parseInt(args[1]);
				// Convert grids back from String. NB: The String representations of altGrid and
				// pcGrid contain single quotes around the 'list' items; these need to be removed
				// when constructing the Arrays
				// mpcGrid
				String mpcGridStr = args[2];
				Integer[] mpcGrid = Arrays.stream(
					mpcGridStr.substring(1, mpcGridStr.length()-1).split("\\s*,\\s*"))
					.map(Integer::valueOf)
					.toArray(Integer[]::new);
				// altGrid
				String altGridStr = args[3];
				String[] altGrid = Arrays.stream(
					altGridStr.substring(1, altGridStr.length()-1).split("\\s*,\\s*"))
					.map(s -> s.replace("'", ""))
					.toArray(String[]::new);
				// pcGrid 
				String pcGridStr = args[4];
				String[] pcGrid = Arrays.stream(
					pcGridStr.substring(1, pcGridStr.length()-1).split("\\s*,\\s*"))
					.map(s -> s.replace("'", ""))
					.toArray(String[]::new);
				// Convert accidsInEffect back from String.
				String accidsInEffectStr = args[5];				
				List<List<Integer>> accidsInEffect = 
					accidsInEffectStr.equals("null") ? null : 
					StringTools.parseStringifiedListOfIntegers(accidsInEffectStr);

//				System.out.println("FROM JAVA");
//				System.out.println(pitch);
//				System.out.println(accidsInEffectStr);
				List<Object> pitchSpell = spellPitch(
					pitch, numAlt, Arrays.asList(new Object[]{mpcGrid, altGrid, pcGrid}), accidsInEffect
				);
				String[] pa = (String[]) pitchSpell.get(0);
				String dict = 
					"{\"pname\": " + "\"" + pa[0] + "\"" + ", " + // pa[<n>] needs to be placed in double quotes for json.loads()
					"\"accid\": " + "\"" + pa[1] + "\"" + ", " +
					"\"accid.ges\": " + "\"" + pa[2] + "\"" + ", " +
					"\"accidsInEffect\": " + accidsInEffect.toString() + "}";
				System.out.println(dict);
			}
		}
		else {
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
	}


	public static Map<Integer, Integer[]> getKeySigMPCs() {
		return KEY_SIG_MPCS;
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
			// Example: 61 is in octave 4: ( (61-1)/12 ) - 1 = 4
			int octave = ((i-(i%12))/12) - 1;
			scientific.put(i, pitchesStr.get(i%12) + "" + octave);
		}
		return scientific.get(midiPitch);
	}


	/**
	 * Returns the octave of the given midiPitch. Middle C (midiPitch = 60) marks octave 4.
	 * 
	 * @param midiPitch
	 * @return
	 */
	// TESTED
	public static int getOctave(int midiPitch) {
		int c = midiPitch - (midiPitch % 12);
		return (c/12) - 1;
	}


	public static List<Object> createGrids(int numAlt, int mode) {
		Integer[][] mpcg = makeMIDIPitchClassGrid(mode);
		String[][] ag = makeAlterationGrid(mpcg);
		String[][] pcg = makePitchClassGrid(mode);
		int keyInd = numAlt + 7;
		Integer[] mpcGrid = mpcg[keyInd];
		String[] altGrid = ag[keyInd];
		String[] pcGrid = pcg[keyInd];

		return Arrays.asList(new Object[]{mpcGrid, altGrid, pcGrid});
	}


	/**
	 * Returns, for each key (starting at 7 flats and ending at 7 sharps), the MIDI pitch classes
	 * for that key. 
	 * A MIDI pitch class is a note's MIDI pitch % 12, and has one of the values [0-11]. 
	 * 
	 * Example C major: [0, 2, 4, 5, 7, 9, 11]
	 * Example A major: [9, 11, 1, 2, 4, 6, 8]
	 * 
	 * @param mode
	 * @return
	 */
	// TESTED
	static Integer[][] makeMIDIPitchClassGrid(int mode) {
		List<Integer> semitones = Arrays.asList(new Integer[]{2, 2, 1, 2, 2, 2, 1});
		
		Integer[][] mpcGrid = new Integer[KEY_SIG_MPCS.size()][7];
		int i = 0;
		for (Entry<Integer, Integer[]> entry : KEY_SIG_MPCS.entrySet()) {
			int currBeginPitch = entry.getValue()[0];
			List<Integer> asList = new ArrayList<Integer>();
			asList.add(currBeginPitch);
			for (int j = 0; j < semitones.size()-1; j++) {
				asList.add((asList.get(j) + semitones.get(j)) % 12);
			}
			mpcGrid[i] = asList.toArray(new Integer[asList.size()]);
			i++;
		}
		if (mode == 1) {
			for (int j = 0; j < mpcGrid.length; j++) {
				mpcGrid[j] = ArrayUtils.addAll(
					Arrays.copyOfRange(mpcGrid[j], 5, 7), Arrays.copyOfRange(mpcGrid[j], 0, 5)
				);
			}
		}
		return mpcGrid;
	}


	/**
	 * Returns, for each key (starting at 7 flats and ending at 7 sharps), the alterations going
	 * with the pitch classes for that key. 
	 * An alteration is either a flat ("f"), a sharp ("s"); no alteration is indicated by "n". 
	 * 
	 * @param MIDIPitchClassGrid
	 * @return
	 */
	// TESTED
	static String[][] makeAlterationGrid(Integer[][] MIDIPitchClassGrid) {
		List<Integer> diatonicPitchCl = Arrays.asList(new Integer[]{0, 2, 4, 5, 7, 9, 11});
		String[][] altGrid = new String[KEY_SIG_MPCS.size()][7];
		int i = 0;
		for (Entry<Integer, Integer[]> entry : KEY_SIG_MPCS.entrySet()) {
			int currKey = entry.getKey();
			String alt = "s";
			if (currKey < 0) {
				alt = "f";
			}
			List<String> asList = new ArrayList<String>();
			for (int p : MIDIPitchClassGrid[i]) {
				// Add sharp or flat of altered note
				// If there are no harmmonic equivalents
				if (Math.abs(currKey) < 6) {
					if (!diatonicPitchCl.contains(p)) {
						asList.add(alt);
					}
					else {
						asList.add("n");
					}
				}
				// If there are harmonic equivalents 
				else if	(Math.abs(currKey) > 5) {
					if (!diatonicPitchCl.contains(p)) {
						asList.add(alt);
					}
					else if (diatonicPitchCl.contains(p)) {
						// cb, fb
						if (alt.equals("f") && (p == 11 || p == 4)) {
							asList.add(alt);
						}
						// e#, b#
						else if (alt.equals("s") && (p == 5 || p == 0)) {
							asList.add(alt);
						}
						else {
							asList.add("n");
						}
					}
				}
			}
			altGrid[i] = asList.toArray(new String[asList.size()]);
			i++;
		}
		return altGrid;
	}


	/**
	 * Returns, for each key (starting at 7 flats and ending at 7 sharps), the pitch classes 
	 * for that key. 
	 * A pitch class is a note's nominal pitch, and has one of the values 
	 * ["c", "d", "e", "f", "g", "a", "b"].  
	 * 
	 * @mode 
	 * @return
	 */
	// TESTED
	static String[][] makePitchClassGrid(int mode) {
		String[] pitchCl = new String[]{"c", "d", "e", "f", "g", "a", "b"};		
		List<String> pitchClasses = new ArrayList<String>();
		for (String s : pitchCl) {
			pitchClasses.add(s);
		}

		int fromInd = 0;
		String[][] pcGrid = new String[KEY_SIG_MPCS.size()][7];
		for (int i = 0; i < KEY_SIG_MPCS.size(); i++) {
			// Reorder pitchClasses: split at fromIdex and paste the first part after the second
			List<String> asList = 
				new ArrayList<String>(pitchClasses.subList(fromInd, pitchClasses.size())); 
			List<String> secondHalf = pitchClasses.subList(0, fromInd);
			asList.addAll(secondHalf);
			pcGrid[i] = asList.toArray(new String[asList.size()]);
			// Increment fromInd to be the index of the note a fifth higher
			fromInd = (fromInd + 4) % 7;
		}
		if (mode == 1) {
			for (int j = 0; j < pcGrid.length; j++) {
				pcGrid[j] = ArrayUtils.addAll(
					Arrays.copyOfRange(pcGrid[j], 5, 7), Arrays.copyOfRange(pcGrid[j], 0, 5)
				);
			}
		}
		return pcGrid;
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
			mpcKeySigs.addAll(KEY_ACCID_MPC_FLAT.subList(0, -numAlt));
		}
		// Sharps
		else if (numAlt > 0) {
			mpcKeySigs.addAll(KEY_ACCID_MPC_SHARP.subList(0, numAlt));
		}
		
		return mpcKeySigs;
	}


	private List<String[][]> combine(int mode) {
		List<String[][]> combined = new ArrayList<String[][]>();
		Integer[][] mpcg = makeMIDIPitchClassGrid(mode);
		String[][] ag = makeAlterationGrid(mpcg);
		String[][] pcg = makePitchClassGrid(mode);
		for (int i = 0; i < KEY_SIG_MPCS.size(); i++) {
			String[][] curr = new String[3][7];
			// MIDI pitch classes
			 
		}		
		return combined;
	}


	// TODO test
	public static String[] getRootAndRootAlteration(int numAlt, int mode) {
		Integer[] mpcs = getKeySigMPCs().get(numAlt);
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
	 * @param bar
	 * @param onset
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

		String keySigAccidType = numAlt <= 0 ? "f" : "s";
		// Key sig accidentals as MIDI pitch classes (e.g. 10, 3 for Bb, Eb)
		List<Integer> keySigAccidMpc = new ArrayList<>();
		for (int i = 0; i < altGrid.length; i++) {
			if (altGrid[i].equals(keySigAccidType)) {
				keySigAccidMpc.add(mpcGrid[i]);
			}
		}

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


	/**
	 * Counts the pitch class frequencies for the given <code>ScorePiece</code>, where C is pitch
	 * class 0, C#/Db pitchclass 1, etc.
	 *
	 * @param sp
	 * @return A list containing 12 elements, each representing a pitch class. The value of the element
	 *         is the frequency of the represented pitch class.
	 */
	// TESTED
	public static List<Integer> getPitchClassCount(ScorePiece sp) {
		List<Integer> pitchClassCounts = new ArrayList<>(Collections.nCopies(12, 0));
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

		return pitchClassCounts;
	}


	/**
	 * Given the list of pitch class counts representing a piece, detects the key (as the number of
	 * alterations) of the piece.
	 *
	 * The method works only for key signatures with no more than five key accidentals (KA)
	 * because of enharmonicity problems (the sixth flat/sharp is Cb/E#).
	 *
	 * @param pitchClassCounts
	 * @return
	 */
	// TESTED
	public static int detectKey(List<Integer> pitchClassCounts) {
		List<String> flats = Arrays.asList("", "d", "", "e", "f", "", "g", "", "a", "", "b", "c");
		List<String> sharps = Arrays.asList("b", "c", "", "d", "", "e", "f", "", "g", "", "a", "");

//		pitchClassCounts.set(flats.indexOf("e"), pitchClassCounts.get(flats.indexOf("e")) + 100);
//		pitchClassCounts.set(flats.indexOf("a"), pitchClassCounts.get(flats.indexOf("a")) + 100);
//		pitchClassCounts.set(sharps.indexOf("f"), pitchClassCounts.get(sharps.indexOf("f")) + 100);
//		pitchClassCounts.set(sharps.indexOf("c"), pitchClassCounts.get(sharps.indexOf("c")) + 100);

		// Iterate through all flat/sharp accidentals. For each accidental, if there are more
		// notes with the accidental than without it, the key contains the accidental. Break
		// when the condition is no longer met
		Integer[] numAlts = new Integer[2];
		for (int i = 0; i < KEY_ACCID_PC_FLAT.size(); i++) {
			// Get pitch class of the current accid and that of its natural counterpart
			int pcAccid = flats.indexOf(KEY_ACCID_PC_FLAT.get(i));
			// pcNatural is ind+1, or, in the case of the last ind (for Cb), ind 0
			int pcNatural = (pcAccid + 1) % 12;
			// Update numAlt or break
			if (pitchClassCounts.get(pcAccid) > pitchClassCounts.get(pcNatural)) {
				numAlts[0] = -(i+1);
			}
			else {
				break;
			}
		}

		for (int i = 0; i < KEY_ACCID_PC_SHARP.size(); i++) {
			// Get pitch class of the current accid  and that of its natural counterpart
			int pcAccid = sharps.indexOf(KEY_ACCID_PC_SHARP.get(i));
			// pcNatural ind-1, or, in the case of the first ind (for B#), ind 11
			int pcNatural = (pcAccid + 11) % 12;
			// Update numAlt or break
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
		else {
			return Math.abs(numAlts[0]) < numAlts[1] ? numAlts[0] : numAlts[1];
		}
	}


//	public static List<Object> spellPitchOLD(int pitch, int numAlt, Integer[] mpcGrid, String[] altGrid, 
//			String[] pcGrid, List<List<Integer>> accidsInEffect, NotationVoice nv, Rational onset) {
//			String pname = "";
//			String accid = "";
//
//			List<Integer> doubleFlatsInEffect = accidsInEffect.get(0);
//			List<Integer>  = accidsInEffect.get(1);
//			List<Integer> naturalsInEffect = accidsInEffect.get(2);
//			List<Integer> sharpsInEffect = accidsInEffect.get(3);
//			List<Integer> doubleSharpsInEffect = accidsInEffect.get(4);
//			
//			boolean considerContext = 
//				doubleFlatsInEffect != null && flatsInEffect != null && naturalsInEffect != null && 
//				sharpsInEffect != null && doubleSharpsInEffect != null;
//			boolean isMinor = mpcGrid[2] - mpcGrid[0] == 3 || Math.abs(mpcGrid[2] - mpcGrid[0]) == 9;
//			System.out.println(pitch % 12);
//			System.out.println(Arrays.asList(mpcGrid));
//			System.out.println(Arrays.asList(altGrid));
//			System.out.println(Arrays.asList(pcGrid));
//			
//			List<String> accids = Arrays.asList(new String[]{"ff", "f", "n", "s", "x"});
//			
//			Integer[] mpcGridNomMaj = null;
//			if (isMinor) {
//				// Get the nominal major 
//				mpcGridNomMaj = Arrays.copyOf(mpcGrid, mpcGrid.length);
//				mpcGridNomMaj[2] = (mpcGridNomMaj[2] + 1) % 12;
//				mpcGridNomMaj[5] = (mpcGridNomMaj[5] + 1) % 12;
//				mpcGridNomMaj[6] = (mpcGridNomMaj[6] + 1) % 12;
//			}
//			
//			List<Integer> mpcGridList = Arrays.asList(mpcGrid);
//			int mpc = pitch % 12;
//
//			// pitch is in key
//			if (mpcGridList.contains(mpc)) {
//				System.out.println("in key");
//				int pcInd = mpcGridList.indexOf(mpc);
//				pname = pcGrid[pcInd];
//				if (!considerContext) {
//					accid = altGrid[pcInd];
//				}
//				if (considerContext) {
//					// Previously double flat (but must be flat)
//					if (doubleFlatsInEffect.contains(pitch-1)) {
//						accid = "f";
//						doubleFlatsInEffect.remove(doubleFlatsInEffect.indexOf(pitch-1));
//					}
//					// Previously flat (but must be natural)
//					else if (flatsInEffect.contains(pitch-1)) {
//						accid = "n";
//						flatsInEffect.remove(flatsInEffect.indexOf(pitch-1));
//					}
//					// Previously natural (but must be flat)
//					else if (altGrid[pcInd] == "f" && naturalsInEffect.contains(pitch+1)) {
//						accid = "f";
//						naturalsInEffect.remove(naturalsInEffect.indexOf(pitch+1));
//					}
//					// Previously natural (but must be sharp)
//					else if (altGrid[pcInd] == "s" && naturalsInEffect.contains(pitch-1)) {
//						accid = "s";
//						naturalsInEffect.remove(naturalsInEffect.indexOf(pitch-1));
//					}
//					// Previously sharp (but must be natural)
//					else if (sharpsInEffect.contains(pitch+1)) {
//						accid = "n";
//						sharpsInEffect.remove(sharpsInEffect.indexOf(pitch+1));
//					}
//					// Previously double sharp (but must be sharp)
//					else if (doubleSharpsInEffect.contains(pitch+1)) {
//						accid = "f";
//						doubleSharpsInEffect.remove(doubleSharpsInEffect.indexOf(pitch+1));
//					}
//					// No accidental
//					else {
//						accid = "";
//					}
//				}
//			}
//			// pitch is not in key
//			else {
//				System.out.println("NOT in key");
//				List<Integer> mpcKeySigs = getMIDIPitchClassKeySigs(numAlt);
//				boolean isNaturalisedKA = false;
//				boolean isNextOrSecondNextKA = false;
//				boolean isLLTForMinor = false;
//								
//				// 1. If pitch is a naturalised key accidental (KA)
//				// NB: Since keys sigs with double flats/sharps are not considered, accid is 
//				// always "n" (and never "f" (from "ff") or "s" (from "ss"))
//				if (numAlt < 0 && mpcKeySigs.contains((pitch-1) % 12) ||
//					numAlt > 0 && mpcKeySigs.contains((pitch+1) % 12)) {
//					if (verbose) System.out.println("is naturalised KA");
//					int pcInd = -1;
//					// if-else needed to consistently spell double flats and sharps as naturalised KAs
//					// (see test examples for Bbm and G#m)
//					if (numAlt <= 0) {
//						// Natural for flat
//						if (mpcKeySigs.contains((pitch-1) % 12)) {
//							pcInd = mpcGridList.indexOf((pitch-1) % 12);
//						}
//						// Natural for sharp
//						else if (mpcKeySigs.contains((pitch+1) % 12)) {
//							pcInd = mpcGridList.indexOf((pitch+1) % 12);
//						}
//					}
//					else {
//						// Natural for sharp
//						if (mpcKeySigs.contains((pitch+1) % 12)) {
//							pcInd = mpcGridList.indexOf((pitch+1) % 12);
//						}
//						// Natural for flat
//						else if (mpcKeySigs.contains((pitch-1) % 12)) {
//							pcInd = mpcGridList.indexOf((pitch-1) % 12);
//						}
//					}
//					pname = pcGrid[pcInd];
//					if (!considerContext) {
//						accid = "n";
//					}
//					if (considerContext) {
//						// Only if the natural has not already been indicated for this specific pitch in the bar
//						if (!naturalsInEffect.contains(pitch)) {
//							accid = "n";
//							naturalsInEffect.add(pitch);
//						}
//					}
//					isNaturalisedKA = true;
//				}
//				// If not 1.: continue
//				if (!isNaturalisedKA) {
////				else {
//					if (verbose) System.out.println("is accidentalised");
//
////					// Find pitch of next note of different pitch. If there is none, the voice
////					// ends with a sequence of the same pitches (i.e., with a repetition of 
////					// the last pitch), and nextPitch remains -1
////					int nextPitch = -1;	
////					if (nv != null) {
////						// If not last note
////						if (!(nv.get(nv.size()-1).getMetricTime().equals(onset))) {
////							for (int j = 0; j < nv.size() && nextPitch == -1; j++) {
////								if (nv.get(j).getMetricTime().equals(onset)) {
////									for (int k = j+1; k < nv.size(); k++) {
////										int currNextPitch = nv.get(k).get(0).getMidiPitch();
////										if (currNextPitch != pitch) {
////											nextPitch = currNextPitch;
////											break;
////										}
////									}
////								}
////							}
////						}
////					}
////					boolean nextIsInKey = mpcGridList.contains(nextPitch % 12);
////					// a. Direct leading tone (next different pitch is in-key and a semitone lower/higher)
////					//    or last note in voice (do only if nv is not null)    
////					if (nv != null && nextIsInKey && 
////						(nextPitch == (pitch + 1) || nextPitch == (pitch - 1) || nextPitch == -1)) {
////						// Sharps (if last note, assume sharp )
////						if ((nextPitch != -1 && nextPitch == (pitch + 1)) || nextPitch == -1) {
////							if (verbose) System.out.println("DLT, sharp");
////							// pname is pc of un-sharpened pitch
////							int pcInd = mpcGridList.indexOf((pitch-1) % 12);
////							pname = pcGrid[pcInd];
////							if (!considerContext) {
////								accid = "s";
////								// If pitch is already a sharp: double sharp
////								if (mpcGridList.contains(mpc) && KEY_ACCID_MPC_SHARP.contains(mpc)) {
////									accid = "x";
////								}
////							}
////							if (considerContext) {
////								// Only if the accidental has not already been indicated for this specific pitch in the bar
////								if (!sharpsInEffect.contains(pitch)) {
////									accid = "s";
////									// If pitch is already a sharp: double sharp
////									if (mpcGridList.contains(mpc) && KEY_ACCID_MPC_SHARP.contains(mpc)) {
////										accid = "x";
////									}
////									sharpsInEffect.add(pitch);
////								}
////							}
////						}
////						// Flats
////						else if (nextPitch != -1 && nextPitch == (pitch - 1)) {
////							if (verbose) System.out.println("DLT, flat");
////							// pname is pc of un-flattened pitch
////							int pcInd = mpcGridList.indexOf((pitch+1) % 12);
////							pname = String.valueOf(pcGrid[pcInd]);
////							if (!considerContext) {
////								accid = "f";
////								// If pitch is already a flat: double flat
////								if (mpcGridList.contains(mpc) && KEY_ACCID_MPC_FLAT.contains(mpc)) {
////									accid = "ff";
////								}
////							}
////							if (considerContext) {
////								// Only if the accidental has not already been indicated for this specific pitch in the bar
////								if (!flatsInEffect.contains(pitch)) {
////									accid = "f";
////									// If pitch is already a flat: double flat
////									if (mpcGridList.contains(mpc) && KEY_ACCID_MPC_FLAT.contains(mpc)) {
////										accid = "ff";
////									}
////									flatsInEffect.add(pitch);
////								}
////							}
////						}
////					}
////					// b. Not a direct leading tone
////					else {
//						 
//					// 2. If pitch is the next or second-next KA
//					// NB: It is assumed that double flats or double sharps are not needed, i.e., that the 
//					// key sigs used have no more than five KAs
//					// - Bb Eb Ab Db Gb Cb Fb
//					//   - 6 flats would require Fb and Bbb as next and second-next KA; 7 would require Bbb and Ebb; etc. 
//					// - F# C# G# D# A# E# B#
//					//   - 6 sharps would require B# and Fx as next and second-next KA; 7 would require Fx and Cx; etc.
//					// Flats (or no KA and minor)
//					if (numAlt < 0 || (numAlt == 0 && isMinor)) {
//						String alt = altGrid[mpcGridList.indexOf(mpc+1)];
//						int indLastKeyAccid = 
//							(numAlt == 0 && isMinor) ? -1 :
//							KEY_ACCID_MPC_FLAT.indexOf(mpcKeySigs.get(mpcKeySigs.size()-1));
//						for (int incr : new Integer[]{1, 2}) {
//							if (KEY_ACCID_MPC_FLAT.get(indLastKeyAccid + incr) == mpc) {
//								System.out.println("is KA, flat");
//								pname = KEY_ACCID_PC_FLAT.get(indLastKeyAccid + incr);
//								if (!considerContext) {
//									accid = accids.get(accids.indexOf(alt) - 1);
////									accid = "f";
////									// If pitch is already a flat: double flat
////									if (mpcGridList.contains(mpc) && KEY_ACCID_MPC_FLAT.contains(mpc)) {
////										accid = "ff";
////									}
//								}
//								if (considerContext) {
//									// Only if the accidental has not already been indicated for this specific pitch in the bar
//									if (!flatsInEffect.contains(pitch)) {
//										accid = accids.get(accids.indexOf(alt) - 1);
////										accid = "f";
////										// If pitch is already a flat: double flat
////										if (mpcGridList.contains(mpc) && KEY_ACCID_MPC_FLAT.contains(mpc)) {
////											accid = "ff";
////										}
//										// TODO add to correct list
//										flatsInEffect.add(pitch);
//									}
//								}
//								isNextOrSecondNextKA = true;
//								break;
//							}
//						}
//					}
//					// Sharps (or no KA and major)
//					else if (numAlt > 0 || (numAlt == 0 && !isMinor)) {
//						String alt = altGrid[mpcGridList.indexOf(mpc-1)];
//						int indLastKeyAccid = 
//							(numAlt == 0 && !isMinor) ? -1 :	
//							KEY_ACCID_MPC_SHARP.indexOf(mpcKeySigs.get(mpcKeySigs.size()-1));
//						for (int incr : new Integer[]{1, 2}) {
//							if (KEY_ACCID_MPC_SHARP.get(indLastKeyAccid + incr) == mpc) {
//								pname = KEY_ACCID_PC_SHARP.get(indLastKeyAccid + incr);
//								System.out.println("is KA, sharp");
//								if (!considerContext) {
//									accid = accids.get(accids.indexOf(alt) + 1);
////									accid = "s";
////									// If pitch is already a sharp: double sharp
////									if (mpcGridList.contains(mpc) && KEY_ACCID_MPC_SHARP.contains(mpc)) {
////										accid = "x";
////									}
//								}
//								if (considerContext) {
//									// Only if the accidental has not already been indicated for this specific pitch in the bar
//									if (!sharpsInEffect.contains(pitch)) {
//										accid = accids.get(accids.indexOf(alt) + 1);
////										accid = "s";
////										// If pitch is already a sharp: double sharp
////										if (mpcGridList.contains(mpc) && KEY_ACCID_MPC_SHARP.contains(mpc)) {
////											accid = "x";
////										}
//										// TODO add to correct list
//										sharpsInEffect.add(pitch);
//									}
//								}
//								isNextOrSecondNextKA = true;
//								break;
//							}
//						}	
//					}
//					// If not 2.: continue
//					if (!isNextOrSecondNextKA) {					
////						int mpcMinor = KEY_SIG_MPCS.get(numAlt)[1];
////						if (mpc == mpcMinor + 1) {
////							System.out.println("is ULT");
////							// If mpcMinor is not the last element of mpcGridList: take next
////							if (mpcGridList.indexOf(mpcMinor) != (mpcGridList.size() - 1)) {
////								pname = pcGrid[mpcGridList.indexOf(mpcMinor) + 1];
////							}
////							// Else: next is first element
////							else {
////								pname = pcGrid[0];
////							}
////							if (!considerContext) {
////								accid = "f";
////								// If pitch is already a flat: double flat
////								if (mpcGridList.contains(mpc) && KEY_ACCID_MPC_FLAT.contains(mpc)) {
////									accid = "ff";
////								}
////							}
////							if (considerContext) {
////								// Only if the accidental has not already been indicated for this specific pitch in the bar
////								if (!flatsInEffect.contains(pitch)) {
////									accid = "f";
////									// If pitch is already a flat: double flat
////									if (mpcGridList.contains(mpc) && KEY_ACCID_MPC_FLAT.contains(mpc)) {
////										accid = "ff";
////									}
////									flatsInEffect.add(pitch);
////								}
////							}
////							isLeadingToneForMinor = true;
////						}
//						
//						// 3. If pitch is the lower leading tone (LLT) for the minor parallel
//						// NB: the upper leading tone (ULT) for minor is always covered above
//						//     - in case of flats, ULT is next KA: Bb for Am, Eb for Dm; Ab for Gm; Db for Cm; ...
//						//     - in case of sharps, ULT is last KA naturalised: F for Em; C for Bm; G for F#m; ...
//						// NB2: double flats (for ULT) and double sharps (for LLT) will be caught (wrongly!) above 
//						//      as naturalised KA, e.g.,
//						//      - Cb in Bbm will be caught as B
//						//      - Fx in G#m will be caught as G
//						if (isMinor && mpc == mpcGridNomMaj[mpcGridNomMaj.length-1]) {
////						if (mpc == mpcMinor - 1) {
//							System.out.println("is LLT");
//							pname = pcGrid[pcGrid.length-1];
//							String alt = altGrid[pcGrid.length-1];
//							
////							// If mpcMinor is not the first element of mpcGridList: take previous
////							if (mpcGridList.indexOf(mpcMinor) != 0) {
////								pname = pcGrid[mpcGridList.indexOf(mpcMinor) - 1];
////							}
////							// Else: previous is last element
////							else {
////								pname = pcGrid[mpcGridList.size() - 1];
////							}
//							if (!considerContext) {
//								accid = accids.get(accids.indexOf(alt) + 1);
////								accid = "s";
////								// If pitch is already a sharp: double sharp
////								if (mpcGridList.contains(mpc) && KEY_ACCID_MPC_SHARP.contains(mpc)) {
////									accid = "x";
////								}
//							}
//							if (considerContext) {
//								// Only if the accidental has not already been indicated for this specific pitch in the bar
//								if (!sharpsInEffect.contains(pitch)) {
//									accid = accids.get(accids.indexOf(alt) + 1);
////									accid = "s";
////									// If pitch is already a sharp: double sharp
////									if (mpcGridList.contains(mpc) && KEY_ACCID_MPC_SHARP.contains(mpc)) {
////										accid = "x";
////									}
//									// TODO add to correct list
//									sharpsInEffect.add(pitch);
//								}
//							}
//							isLLTForMinor = true;
//						}
//						// If not 3.: continue
//						if (!isLLTForMinor) {
//							// 4. If pitch is 
//						}
//					}
//						// 3. Else: spell as whichever flat or sharp has the earliest key accidental index, e.g.,
//						//    - C# (2nd sharp) preferred over Db (4th flat)
//						//    - Bb (1st flat) preferred over A# (5th sharp)) 
//						if (!isNextOrSecondNextKA && !isLLTForMinor) {
//							int indInSharps = KEY_ACCID_MPC_SHARP.indexOf(mpc);
//							int indInFlats = KEY_ACCID_MPC_FLAT.indexOf(mpc);
////							System.out.println(bar);
////							System.out.println(voice);
////							System.out.println(mpc);
////							System.out.println(indInFlats);
////							System.out.println(indInSharps);
//							
//							// NB: The KA index in flats and sharps being the same occurs only in the case of 
//							// G# == Ab, and this accidental is always covered above
//							// - in case of no KA: G# is LLT for Am
//							// - in case of flats: Ab is second-next KA for Dm; next KA for Gm; KA for Cm and up 
//							// - in case of sharps: G# is second-next KA for G; next KA for D; KA for A and up
//							if (indInSharps != indInFlats) {
////								pname = pcGrid[mpcGridList.indexOf(
////									(indInSharps < indInFlats ? midiPitchClass - 1 : midiPitchClass + 1))];
////								accid = indInSharps < indInFlats ? "s" : "f";
//								if (indInSharps < indInFlats) {
//									System.out.println("index in sharps earlier");
//									pname = pcGrid[mpcGridList.indexOf(mpc - 1)];
//									if (!considerContext) {
//										accid = "s";
//										// If pitch is already a sharp: double sharp
//										if (mpcGridList.contains(mpc) && KEY_ACCID_MPC_SHARP.contains(mpc)) {
//											accid = "x";
//										}
//									}
//									if (considerContext) {
//										// Only if the accidental has not already been indicated for this specific pitch in the bar
//										if (!sharpsInEffect.contains(pitch)) {
//											accid = "s";
//											// If pitch is already a sharp: double sharp
//											if (mpcGridList.contains(mpc) && KEY_ACCID_MPC_SHARP.contains(mpc)) {
//												accid = "x";
//											}
//											sharpsInEffect.add(pitch);
//										}
//									}
//								}
//								else {
//									System.out.println("index in flats earlier");
//									pname = pcGrid[mpcGridList.indexOf(mpc + 1)];
//									if (!considerContext) {
//										accid = "f";
//										// If pitch is already a flat: double flat
//										if (mpcGridList.contains(mpc) && KEY_ACCID_MPC_FLAT.contains(mpc)) {
//											accid = "ff";
//										}
//									}
//									if (considerContext) {
//										// Only if the accidental has not already been indicated for this specific pitch in the bar
//										if (!flatsInEffect.contains(pitch)) {
//											accid = "f";
//											// If pitch is already a flat: double flat
//											if (mpcGridList.contains(mpc) &&
//												KEY_ACCID_MPC_FLAT.contains(mpc)) {
//												accid = "ff";
//											}
//											flatsInEffect.add(pitch);
//										}
//									}
//								}
//							}
////							else {
////								if (numAlt > 0) {
////									System.out.println("index in flats/sharps same, KS has sharps");
////									pname = pcGrid[mpcGridList.indexOf(mpc - 1)];
////									if (!considerContext) {
////										accid = "s";
////										// If pitch is already a sharp: double sharp
////										if (mpcGridList.contains(mpc) && KEY_ACCID_MPC_SHARP.contains(mpc)) {
////											accid = "x";
////										}
////									}
////									if (considerContext) {
////										// Only if the accidental has not already been indicated for this specific pitch in the bar
////										if (!sharpsInEffect.contains(pitch)) {
////											accid = "s";
////											// If pitch is already a sharp: double sharp
////											if (mpcGridList.contains(mpc) && KEY_ACCID_MPC_SHARP.contains(mpc)) {
////												accid = "x";
////											}
////											sharpsInEffect.add(pitch);
////										}
////									}
////								}
////								else {
////									System.out.println("index in flats/sharps same, KS has flats");
////									pname = pcGrid[mpcGridList.indexOf(mpc + 1)];
////									if (!considerContext) {
////										accid = "f";
////										// If pitch is already a flat: double flat
////										if (mpcGridList.contains(mpc) && KEY_ACCID_MPC_FLAT.contains(mpc)) {
////											accid = "ff";
////										}
////									}
////									if (considerContext) {
////										// Only if the accidental has not already been indicated for this specific pitch in the bar
////										if (!flatsInEffect.contains(pitch)) {
////											accid = "f";
////											// If pitch is already a flat: double flat
////											if (mpcGridList.contains(mpc) && KEY_ACCID_MPC_FLAT.contains(mpc)) {
////												accid = "ff";
////											}
////											flatsInEffect.add(pitch);
////										}
////									}
////								}
////							}
//						}
//					}
//				}
//			
//			String[] pa = new String[]{pname, accid};
//			return Arrays.asList(new Object[]{pa, accidsInEffect});
//		}
}
