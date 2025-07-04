package tools.music;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import conversion.imports.MIDIImport;
import external.Tablature;
import external.Transcription;
import interfaces.CLInterface;
import internal.core.ScorePiece;
import tools.ToolBox;
import tools.text.StringTools;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


public class PitchKeyToolsTest {

	private Map<String, String> paths;
	private File encodingTestpiece;
	private File midiTestpiece;
	private String bachPath;

	@Before
	public void setUp() throws Exception {
		paths = CLInterface.getPaths(true);
		String ep = paths.get("ENCODINGS_PATH");
		String mp = paths.get("MIDI_PATH");
		String td = "test/5vv/";
		String bd = "bach-WTC/thesis/";
		bachPath = StringTools.getPathString(Arrays.asList(mp, bd));

		encodingTestpiece = new File(StringTools.getPathString(
			Arrays.asList(ep, td)) + "testpiece.tbp"
		);		
		midiTestpiece = new File(StringTools.getPathString(
			Arrays.asList(mp, td)) + "testpiece.mid"
		);		
	}

	@After
	public void tearDown() throws Exception {
	}


	@Test
	public void testGetScientificNotation() {
		List<Integer> testVals = Arrays.asList(new Integer[]{
			12, // C0
			12+1, 12+2, // C#0, D0 
			24+3, 24+4, // Eb1, E1
			36+5, 36+6, // F2, F#2
			48+7, // G3
			60+8, // Ab4
			72+9, // A5
			84+10, // Bb6
			96+11, // B7
			108, // C8
		});
		
		List<String> expected = Arrays.asList(new String[]{
			"C0", "C#0", "D0", "Eb1", "E1", "F2", "F#2", "G3", "Ab4", "A5", "Bb6", "B7", "C8"
		});
		
		List<String> actual = new ArrayList<>();
		for (int i : testVals) {
			actual.add(PitchKeyTools.getScientificNotation(i));
		}
		
		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i), actual.get(i));
		}
	}


	@Test
	public void testGetOctave() {	
		List<Integer> pitches = Arrays.asList(new Integer[]{47, 48, 61, 75, 85});
		List<Integer> expected = Arrays.asList(new Integer[]{2, 3, 4, 5, 6});
		
		List<Integer> actual = new ArrayList<Integer>();
		for (int p : pitches) {
			actual.add(PitchKeyTools.getOctave(p));
		}
		
		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i), actual.get(i));		
		}
		assertEquals(expected, actual);
	}


	@Test
	public void testMakeMIDIPitchClassGrid() {
		Integer[][] expected = new Integer[34][7];
		expected[0] = new Integer[]{4, 6, 8, 9, 11, 1, 3}; // Fb
		expected[1] = new Integer[]{11, 1, 3, 4, 6, 8, 10}; // Cb
		expected[2] = new Integer[]{6, 8, 10, 11, 1, 3, 5}; // Gb
		expected[3] = new Integer[]{1, 3, 5, 6, 8, 10, 0}; // Db
		expected[4] = new Integer[]{8, 10, 0, 1, 3, 5, 7}; // Ab
		expected[5] = new Integer[]{3, 5, 7, 8, 10, 0, 2}; // Eb
		expected[6] = new Integer[]{10, 0, 2, 3, 5, 7, 9}; // Bb
		expected[7] = new Integer[]{5, 7, 9, 10, 0, 2, 4}; // F
		expected[8] = new Integer[]{0, 2, 4, 5, 7, 9, 11}; // C 
		expected[9] = new Integer[]{7, 9, 11, 0, 2, 4, 6}; // G
		expected[10] = new Integer[]{2, 4, 6, 7, 9, 11, 1}; // D
		expected[11] = new Integer[]{9, 11, 1, 2, 4, 6, 8}; // A
		expected[12] = new Integer[]{4, 6, 8, 9, 11, 1, 3}; // E
		expected[13] = new Integer[]{11, 1, 3, 4, 6, 8, 10}; // B
		expected[14] = new Integer[]{6, 8, 10, 11, 1, 3, 5}; // F#
		expected[15] = new Integer[]{1, 3, 5, 6, 8, 10, 0}; // C#
		expected[16] = new Integer[]{8, 10, 0, 1, 3, 5, 7}; // G#
		//
		expected[17] = new Integer[]{1, 3, 4, 6, 8, 9, 11}; // Dbm 
		expected[18] = new Integer[]{8, 10, 11, 1, 3, 4, 6}; // Abm
		expected[19] = new Integer[]{3, 5, 6, 8, 10, 11, 1}; // Ebm
		expected[20] = new Integer[]{10, 0, 1, 3, 5, 6, 8}; // Bbm
		expected[21] = new Integer[]{5, 7, 8, 10, 0, 1, 3}; // Fm
		expected[22] = new Integer[]{0, 2, 3, 5, 7, 8, 10}; // Cm
		expected[23] = new Integer[]{7, 9, 10, 0, 2, 3, 5}; // Gm
		expected[24] = new Integer[]{2, 4, 5, 7, 9, 10, 0}; // Dm
		expected[25] = new Integer[]{9, 11, 0, 2, 4, 5, 7}; // Am 
		expected[26] = new Integer[]{4, 6, 7, 9, 11, 0, 2}; // Em
		expected[27] = new Integer[]{11, 1, 2, 4, 6, 7, 9}; // Bm
		expected[28] = new Integer[]{6, 8, 9, 11, 1, 2, 4}; // F#m
		expected[29] = new Integer[]{1, 3, 4, 6, 8, 9, 11}; // C#m
		expected[30] = new Integer[]{8, 10, 11, 1, 3, 4, 6}; // G#m
		expected[31] = new Integer[]{3, 5, 6, 8, 10, 11, 1}; // D#m
		expected[32] = new Integer[]{10, 0, 1, 3, 5, 6, 8}; // A#m
		expected[33] = new Integer[]{5, 7, 8, 10, 0, 1, 3}; // E#m 

		Integer[][] maj = PitchKeyTools.makeMIDIPitchClassGrids(0);
		Integer[][] min = PitchKeyTools.makeMIDIPitchClassGrids(1);
		Integer[][] actual = new Integer[34][7];
		for (int i = 0; i < maj.length; i++) {
			actual[i] = maj[i];
		}
		for (int i = 0; i < min.length; i++) {
			actual[i+17] = min[i];
		}

		assertEquals(expected.length, actual.length);
		for (int i = 0; i < expected.length; i++) {
			assertEquals(expected[i].length, actual[i].length);
			for (int j = 0; j < expected[i].length; j++) {
				assertEquals(expected[i][j], actual[i][j]);
			}
	    }
	}


	@Test
	public void testMakeAlterationGrid() {
		String[][] expected = new String[34][7];
		expected[0] = new String[]{"f", "f", "f", "ff", "f", "f", "f"}; // Fb
		expected[1] = new String[]{"f", "f", "f", "f", "f", "f", "f"}; // Cb
		expected[2] = new String[]{"f", "f", "f", "f", "f", "f", "n"}; // Gb
		expected[3] = new String[]{"f", "f", "n", "f", "f", "f", "n"}; // Db
		expected[4] = new String[]{"f", "f", "n", "f", "f", "n", "n"}; // Ab
		expected[5] = new String[]{"f", "n", "n", "f", "f", "n", "n"}; // Eb
		expected[6] = new String[]{"f", "n", "n", "f", "n", "n", "n"}; // Bb
		expected[7] = new String[]{"n", "n", "n", "f", "n", "n", "n"}; // F
		expected[8] = new String[]{"n", "n", "n", "n", "n", "n", "n"}; // C
		expected[9] = new String[]{"n", "n", "n", "n", "n", "n", "s"}; // G
		expected[10] = new String[]{"n", "n", "s", "n", "n", "n", "s"}; // D
		expected[11] = new String[]{"n", "n", "s", "n", "n", "s", "s"}; // A
		expected[12] = new String[]{"n", "s", "s", "n", "n", "s", "s"}; // E
		expected[13] = new String[]{"n", "s", "s", "n", "s", "s", "s"}; // B
		expected[14] = new String[]{"s", "s", "s", "n", "s", "s", "s"}; // F#
		expected[15] = new String[]{"s", "s", "s", "s", "s", "s", "s"}; // C#
		expected[16] = new String[]{"s", "s", "s", "s", "s", "s", "x"}; // G#
		//
		expected[17] = new String[]{"f", "f", "f", "f", "f", "ff", "f"}; // Dbm
		expected[18] = new String[]{"f", "f", "f", "f", "f", "f", "f"}; // Abm
		expected[19] = new String[]{"f", "n", "f", "f", "f", "f", "f"}; // Ebm
		expected[20] = new String[]{"f", "n", "f", "f", "n", "f", "f"}; // Bbm
		expected[21] = new String[]{"n", "n", "f", "f", "n", "f", "f"}; // Fm
		expected[22] = new String[]{"n", "n", "f", "n", "n", "f", "f"}; // Cm
		expected[23] = new String[]{"n", "n", "f", "n", "n", "f", "n"}; // Gm
		expected[24] = new String[]{"n", "n", "n", "n", "n", "f", "n"}; // Dm
		expected[25] = new String[]{"n", "n", "n", "n", "n", "n", "n"}; // Am
		expected[26] = new String[]{"n", "s", "n", "n", "n", "n", "n"}; // Em
		expected[27] = new String[]{"n", "s", "n", "n", "s", "n", "n"}; // Bm
		expected[28] = new String[]{"s", "s", "n", "n", "s", "n", "n"}; // F#m
		expected[29] = new String[]{"s", "s", "n", "s", "s", "n", "n"}; // C#m
		expected[30] = new String[]{"s", "s", "n", "s", "s", "n", "s"}; // G#m
		expected[31] = new String[]{"s", "s", "s", "s", "s", "n", "s"}; // D#m
		expected[32] = new String[]{"s", "s", "s", "s", "s", "s", "s"}; // A#m
		expected[33] = new String[]{"s", "x", "s", "s", "s", "s", "s"}; // E#m
		
		String[][] maj = PitchKeyTools.makeAlterationGrids(PitchKeyTools.makeMIDIPitchClassGrids(0));
		String[][] min = PitchKeyTools.makeAlterationGrids(PitchKeyTools.makeMIDIPitchClassGrids(1));
		String[][] actual = new String[34][7];
		for (int i = 0; i < maj.length; i++) {
			actual[i] = maj[i];
		}
		for (int i = 0; i < min.length; i++) {
			actual[i+17] = min[i];
		}
		
		assertEquals(expected.length, actual.length);
		for (int i = 0; i < expected.length; i++) {
			assertEquals(expected[i].length, actual[i].length);
			for (int j = 0; j < expected[i].length; j++) {
				assertEquals(expected[i][j], actual[i][j]);
			}
	    }
	}


	@Test
	public void testMakePitchClassGrid() {
		String[][] expected = new String[34][7];
		expected[0] = new String[]{"f", "g", "a", "b", "c", "d", "e"}; // Fb
		expected[1] = new String[]{"c", "d", "e", "f", "g", "a", "b"}; // Cb
		expected[2] = new String[]{"g", "a", "b", "c", "d", "e", "f"}; // Gb
		expected[3] = new String[]{"d", "e", "f", "g", "a", "b", "c"}; // Db
		expected[4] = new String[]{"a", "b", "c", "d", "e", "f", "g"}; // Ab
		expected[5] = new String[]{"e", "f", "g", "a", "b", "c", "d"}; // Eb
		expected[6] = new String[]{"b", "c", "d", "e", "f", "g", "a"}; // Bb
		expected[7] = new String[]{"f", "g", "a", "b", "c", "d", "e"}; // F
		expected[8] = new String[]{"c", "d", "e", "f", "g", "a", "b"}; // C
		expected[9] = new String[]{"g", "a", "b", "c", "d", "e", "f"}; // G
		expected[10] = new String[]{"d", "e", "f", "g", "a", "b", "c"}; // D
		expected[11] = new String[]{"a", "b", "c", "d", "e", "f", "g"}; // A
		expected[12] = new String[]{"e", "f", "g", "a", "b", "c", "d"}; // E
		expected[13] = new String[]{"b", "c", "d", "e", "f", "g", "a"}; // B
		expected[14] = new String[]{"f", "g", "a", "b", "c", "d", "e"}; // F#
		expected[15] = new String[]{"c", "d", "e", "f", "g", "a", "b"}; // C#
		expected[16] = new String[]{"g", "a", "b", "c", "d", "e", "f"}; // G#
		//
		expected[17] = new String[]{"d", "e", "f", "g", "a", "b", "c"}; // Dbm
		expected[18] = new String[]{"a", "b", "c", "d", "e", "f", "g"}; // Abm
		expected[19] = new String[]{"e", "f", "g", "a", "b", "c", "d"}; // Ebm
		expected[20] = new String[]{"b", "c", "d", "e", "f", "g", "a"}; // Bbm
		expected[21] = new String[]{"f", "g", "a", "b", "c", "d", "e"}; // Fm
		expected[22] = new String[]{"c", "d", "e", "f", "g", "a", "b"}; // Cm
		expected[23] = new String[]{"g", "a", "b", "c", "d", "e", "f"}; // Gm
		expected[24] = new String[]{"d", "e", "f", "g", "a", "b", "c"}; // Dm
		expected[25] = new String[]{"a", "b", "c", "d", "e", "f", "g"}; // Am
		expected[26] = new String[]{"e", "f", "g", "a", "b", "c", "d"}; // Em
		expected[27] = new String[]{"b", "c", "d", "e", "f", "g", "a"}; // Bm
		expected[28] = new String[]{"f", "g", "a", "b", "c", "d", "e"}; // F#m
		expected[29] = new String[]{"c", "d", "e", "f", "g", "a", "b", }; // C#m
		expected[30] = new String[]{"g", "a", "b", "c", "d", "e", "f"}; // G#m
		expected[31] = new String[]{"d", "e", "f", "g", "a", "b", "c"}; // D#m
		expected[32] = new String[]{"a", "b", "c", "d", "e", "f", "g"}; // A#m
		expected[33] = new String[]{"e", "f", "g", "a", "b", "c", "d"}; // E#m

		String[][] maj = PitchKeyTools.makePitchClassGrids(0);
		String[][] min = PitchKeyTools.makePitchClassGrids(1);
		String[][] actual = new String[34][7];
		for (int i = 0; i < maj.length; i++) {
			actual[i] = maj[i];
		}
		for (int i = 0; i < min.length; i++) {
			actual[i+17] = min[i];
		}
		
		assertEquals(expected.length, actual.length);
		for (int i = 0; i < expected.length; i++) {
			assertEquals(expected[i].length, actual[i].length);
			for (int j = 0; j < expected[i].length; j++) {
				assertEquals(expected[i][j], actual[i][j]);
			}
	    }
	}


	@Test
	public void testGetRootAndAlteration() {
		List<String[]> expected = Arrays.asList(
			new String[]{"C", ""}, new String[]{"A", ""}, 
			new String[]{"F", ""}, new String[]{"D", ""}, 
			new String[]{"B", "1"}, new String[]{"G", ""}, 
			new String[]{"E", "1"}, new String[]{"C", ""}, 
			new String[]{"G", ""}, new String[]{"E", ""}, 
			new String[]{"D", ""}, new String[]{"B", ""}, 
			new String[]{"F", "1"}, new String[]{"D", "1"}, 
			new String[]{"C", "1"}, new String[]{"A", "1"} 
		);

		List<String[]> actual = new ArrayList<>();
		List<Integer[]> keyAndMode = Arrays.asList(
			new Integer[]{0, 0}, new Integer[]{0, 1},   // C/am
			new Integer[]{-1, 0}, new Integer[]{-1, 1}, // F/dm
			new Integer[]{-2, 0}, new Integer[]{-2, 1}, // Bb/gm
			new Integer[]{-3, 0}, new Integer[]{-3, 1}, // Eb/cm
			new Integer[]{1, 0}, new Integer[]{1, 1},   // G/em
			new Integer[]{2, 0}, new Integer[]{2, 1},   // D/bm
			new Integer[]{6, 0}, new Integer[]{6, 1},   // F#/d#m
			new Integer[]{7, 0}, new Integer[]{7, 1}    // C#/a#m
		);
		keyAndMode.forEach(in -> actual.add(PitchKeyTools.getRootAndRootAlteration(in[0], in[1])));

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i).length, actual.get(i).length);
			for (int j = 0; j < expected.get(i).length; j++) {
				assertEquals(expected.get(i)[j], actual.get(i)[j]);
			}
		}
	}


	@Test
	public void testDetectKey() {
		int tab = 0;

		// 3vv
		int BWV_847 = -3;
		int BWV_848 = -5; // ks in score is 7 (C# major) -- spelt as Db major (enharmonically equivalent); check fails at 'more F# than F': key has also E# 
		int BWV_851 = 0;  // ks in score is -1 (d minor) -- fail because there are more B than Bb (47:48) (raised 6th) --> PREREQ FOR ALG NOT MET
		int BWV_852 = -3;
		int BWV_853 = 6;  // ks in score is 6 (d# minor) -- correct result for both -6 and 6 (enharmonically equivalent); sharps chosen
		int BWV_854 = 4;
		int BWV_856 = -1;
		int BWV_858 = 6;  // ks in score is 6 (F# major) -- correct result for both -6 and 6 (enharmonically equivalent); sharps chosen
		int BWV_860 = 1;
		int BWV_864 = 3;
		int BWV_866 = -2;
		int BWV_870 = 0;
		int BWV_872 = -5; // ks in score is 7 (C# major) -- spelt as Db major (enharmonically equivalent); check fails at 'more F# than F': key has also E#
		int BWV_873 = 4;
		int BWV_875 = -1;
		int BWV_879 = 1;
		int BWV_880 = -1;
		int BWV_881 = -4;
		int BWV_882 = 6;  // ks in score is 6 (F# major) -- correct result for both -6 and 6 (enharmonically equivalent); sharps chosen
		int BWV_883 = 3;
		int BWV_884 = 1;
		int BWV_887 = 5;
		int BWV_888 = 3;
		int BWV_889 = 0;
		int BWV_890 = -2;
		int BWV_893 = 2;
		List<Integer> expected = new ArrayList<>();
		expected.add(tab);
		expected.addAll(Arrays.asList(
			BWV_847, BWV_848, BWV_851, BWV_852, BWV_853, BWV_854, BWV_856, BWV_858, BWV_860, 
			BWV_864, BWV_866, BWV_870, BWV_872, BWV_873, BWV_875, BWV_879, BWV_880, BWV_881, 
			BWV_882, BWV_883, BWV_884, BWV_887, BWV_888, BWV_889, BWV_890, BWV_893
		));

		// 4vv
		int BWV_846 = 0;
		int BWV_850 = 2;
		int BWV_857 = -3; // ks in score is -4 (f minor) -- fail because there are more D than Db (76:119) (raised 6th) --> PREREQ FOR ALG NOT MET
		int BWV_859 = 4;  // ks in score is 3 (f# minor) -- fail because there are more D# than D (46:54) (raised 6th) --> PREREQ FOR ALG NOT MET
		int BWV_861 = -2;
		int BWV_862 = -4;
		int BWV_863 = 5;
		int BWV_865 = 0;
		int BWV_868 = 5;
		int BWV_869 = 2;
		int BWV_871 = -3;
		int BWV_874 = 2;
		int BWV_876 = -3;
		int BWV_877 = -6; // ks in score is 6 (d# minor) -- correct result for only -6 (enharmonically equivalent); check fails at 'more F# than F': key has also E#
		int BWV_878 = 4;
		int BWV_885 = -2;
		int BWV_886 = -4;
		int BWV_891 = -5;
		int BWV_892 = 5;
		expected.addAll(Arrays.asList(
			BWV_846, BWV_850, 
			BWV_857, BWV_859, BWV_861, BWV_862, BWV_863, BWV_865, BWV_868, BWV_869, 
			BWV_871, BWV_874, BWV_876, 
			BWV_877, BWV_878, BWV_885, BWV_886, BWV_891, BWV_892
		));

		List<Transcription> trans = new ArrayList<>();
		List<String> three = ToolBox.getFilesInFolder(bachPath + "3vv/", Arrays.asList(MIDIImport.MID_EXT), true);
		three = ToolBox.sortBySubstring(three, "BWV_", null, "number");
		for (String p : three) {
			File f = new File(StringTools.getPathString(Arrays.asList(bachPath, "3vv/")) + p);
			trans.add(new Transcription(f));
		}
		List<String> four = ToolBox.getFilesInFolder(bachPath + "4vv/", Arrays.asList(MIDIImport.MID_EXT), true);
		four = ToolBox.sortBySubstring(four, "BWV_", null, "number");
		for (String p : four) {
			File f = new File(StringTools.getPathString(Arrays.asList(bachPath, "4vv/")) + p);
			trans.add(new Transcription(f));
		}

		List<Integer> actual = new ArrayList<>();
		actual.add(PitchKeyTools.detectKey(null, new Tablature(encodingTestpiece, false).getEncoding()));
		for (int i = 0; i < trans.size(); i++) {
			actual.add(PitchKeyTools.detectKey(trans.get(i).getScorePiece(), null));
		}

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i), actual.get(i));
		}
		assertEquals(expected, actual);
	}


	@Test
	public void testGetPitchClassCounts() {
		Tablature tab = new Tablature(encodingTestpiece, false);
		Transcription trans = new Transcription(midiTestpiece);

		List<Integer> expected = new ArrayList<Integer>(Arrays.asList(4, 0, 2, 0, 3, 2, 1, 0, 4, 20, 0, 3)); // tab
		expected.addAll(Arrays.asList(4, 0, 2, 0, 3, 3, 1, 0, 4, 20, 0, 3)); // trans

		List<Integer> actual = PitchKeyTools.getPitchClassCount(null, tab.getEncoding()); // tab
		actual.addAll(PitchKeyTools.getPitchClassCount(trans.getScorePiece(), null)); // trans

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i), actual.get(i));
		}
		assertEquals(expected, actual);
	}


	@Test
	public void testTransposeKeySig() {		
		List<Integer> transIntervals = Arrays.asList(-1, -2, -3, -4, -5, 0, 1, 2, 3, 4, 5);

		// Transpose Eb (-3) to D, Db, C, B, Bb and Eb, E, F, F#, G, Ab
		List<Integer> expected1 = Arrays.asList(2, -5, 0, 5, -2, -3, 4, -1, 6, 1, -4);
		// Transpose A (3) to Ab, G, F#, F, E and A, Bb, B, C, Db, D
		List<Integer> expected2 = Arrays.asList(-4, 1, 6, -1, 4, 3, -2, 5, 0, -5, 2);
		List<Integer> expected = new ArrayList<>(expected1);
        expected.addAll(expected2);

		List<Integer> actual = new ArrayList<>();
		transIntervals.forEach(i -> actual.add(PitchKeyTools.transposeKeySig(-3, i)));
		transIntervals.forEach(i -> actual.add(PitchKeyTools.transposeKeySig(3, i)));

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i), actual.get(i));
		}
		assertEquals(expected, actual);
	}


	@Test
	public void testSpellPitch() {
		Integer[][] mpcGridMaj = PitchKeyTools.makeMIDIPitchClassGrids(0);
		String[][] altGridMaj = PitchKeyTools.makeAlterationGrids(mpcGridMaj);
		String[][] pcGridMaj = PitchKeyTools.makePitchClassGrids(0);
		Integer[][] mpcGridMin = PitchKeyTools.makeMIDIPitchClassGrids(1);
		String[][] altGridMin = PitchKeyTools.makeAlterationGrids(mpcGridMin);
		String[][] pcGridMin = PitchKeyTools.makePitchClassGrids(1);

		int Bbm = 3;
		int Fm = 4;
		int Cm = 5;
		int Gm = 6;		
		int Dm = 7;
		int Am = 8; // int C = 8;
		int Em = 9; // int G = 9;
		int Bm = 10; // int D = 10;
		int Fsm = 11;
		int Csm = 12;
		int Gsm = 13;

		List<Object> gridsMinAm = Arrays.asList(new Object[]{mpcGridMin[Am], altGridMin[Am], pcGridMin[Am]});
		List<Object> gridsMinDm = Arrays.asList(new Object[]{mpcGridMin[Dm], altGridMin[Dm], pcGridMin[Dm]});
		List<Object> gridsMajGm = Arrays.asList(new Object[]{mpcGridMin[Gm], altGridMin[Gm], pcGridMin[Gm]});
		List<Object> gridsMinCm = Arrays.asList(new Object[]{mpcGridMin[Cm], altGridMin[Cm], pcGridMin[Cm]});
		List<Object> gridsMinFm = Arrays.asList(new Object[]{mpcGridMin[Fm], altGridMin[Fm], pcGridMin[Fm]});
		List<Object> gridsMajBbm = Arrays.asList(new Object[]{mpcGridMin[Bbm], altGridMin[Bbm], pcGridMin[Bbm]});
		List<Object> gridsMinEm = Arrays.asList(new Object[]{mpcGridMin[Em], altGridMin[Em], pcGridMin[Em]});
		List<Object> gridsMinBm = Arrays.asList(new Object[]{mpcGridMin[Bm], altGridMin[Bm], pcGridMin[Bm]});
		List<Object> gridsMinFsm = Arrays.asList(new Object[]{mpcGridMin[Fsm], altGridMin[Fsm], pcGridMin[Fsm]});
		List<Object> gridsMinCsm = Arrays.asList(new Object[]{mpcGridMin[Csm], altGridMin[Csm], pcGridMin[Csm]});
		List<Object> gridsMinGsm = Arrays.asList(new Object[]{mpcGridMin[Gsm], altGridMin[Gsm], pcGridMin[Gsm]});
		List<Object> gridsMinGm = Arrays.asList(new Object[]{mpcGridMin[Gm], altGridMin[Gm], pcGridMin[Gm]});
		List<Object> gridsMinBbm = Arrays.asList(new Object[]{mpcGridMin[Bbm], altGridMin[Bbm], pcGridMin[Bbm]});

		List<List<Integer>> aie = null;

		List<String[]> expected = new ArrayList<>();
		// pitch is in key
		// Flats
		expected.add(new String[]{"b", "n", ""}); // 71 in Am
		expected.add(new String[]{"b", "f", ""}); // 70 in Dm
		expected.add(new String[]{"e", "f", ""}); // 63 in Gm
		expected.add(new String[]{"a", "f", ""}); // 68 in Cm
		expected.add(new String[]{"d", "f", ""}); // 61 in Fm
		expected.add(new String[]{"g", "f", ""}); // 66 in Bbm
		// Sharps
		expected.add(new String[]{"f", "s", ""}); // 66 in Em
		expected.add(new String[]{"c", "s", ""}); // 61 in Bm
		expected.add(new String[]{"g", "s", ""}); // 68 in F#m
		expected.add(new String[]{"d", "s", ""}); // 63 in C#m
		expected.add(new String[]{"a", "s", ""}); // 70 in G#m
		// pitch is not in key
		// 1. next or second-next KA
		// Flats
		expected.add(new String[]{"b", "f", ""}); // 70 in Am
		expected.add(new String[]{"a", "f", ""}); // 68 in Dm
		expected.add(new String[]{"a", "f", ""}); // 68 in Gm
		expected.add(new String[]{"g", "f", ""}); // 66 in Cm
		expected.add(new String[]{"g", "f", ""}); // 66 in Fm
		expected.add(new String[]{"f", "f", ""}); // 64 in Bbm
		// Sharps
		expected.add(new String[]{"g", "s", ""}); // 68 in Em
		expected.add(new String[]{"g", "s", ""}); // 68 in Bm
		expected.add(new String[]{"a", "s", ""}); // 70 in F#m
		expected.add(new String[]{"a", "s", ""}); // 70 in C#m
		expected.add(new String[]{"b", "s", ""}); // 60 in G#m
		// 2. naturalised KA
		// Flats
		expected.add(new String[]{"b", "n", ""}); // 71 in Dm
		expected.add(new String[]{"e", "n", ""}); // 64 in Gm
		expected.add(new String[]{"a", "n", ""}); // 69 in Cm
		expected.add(new String[]{"d", "n", ""}); // 62 in Fm
		expected.add(new String[]{"g", "n", ""}); // 67 in Bbm
		// Sharps
		expected.add(new String[]{"f", "n", ""}); // 65 in Em
		expected.add(new String[]{"c", "n", ""}); // 60 in Bm
		expected.add(new String[]{"g", "n", ""}); // 67 in F#m
		expected.add(new String[]{"d", "n", ""}); // 62 in C#m
		expected.add(new String[]{"a", "n", ""}); // 69 in G#m		
		// 3. ULT/LLT for minor (or minor parallel)
		// ULT, flats
		expected.add(new String[]{"b", "f", ""}); // 70 in Am
		expected.add(new String[]{"e", "f", ""}); // 63 in Dm
		expected.add(new String[]{"a", "f", ""}); // 68 in Gm
		expected.add(new String[]{"d", "f", ""}); // 61 in Cm
		expected.add(new String[]{"g", "f", ""}); // 66 in Fm
		expected.add(new String[]{"c", "f", ""}); // 71 in Bbm
		// ULT, sharps
		expected.add(new String[]{"f", "n", ""}); // 65 in Em
		expected.add(new String[]{"c", "n", ""}); // 60 in Bm
		expected.add(new String[]{"g", "n", ""}); // 67 in F#m
		expected.add(new String[]{"d", "n", ""}); // 62 in C#m
		expected.add(new String[]{"a", "n", ""}); // 69 in G#m

		// LLT, flats
		expected.add(new String[]{"g", "s", ""}); // 68 in Am
		expected.add(new String[]{"c", "s", ""}); // 61 in Dm
		expected.add(new String[]{"f", "s", ""}); // 66 in Gm
		expected.add(new String[]{"b", "n", ""}); // 71 in Cm
		expected.add(new String[]{"e", "n", ""}); // 64 in Fm
		expected.add(new String[]{"a", "n", ""}); // 69 in Bbm
		// LLT, sharps
		expected.add(new String[]{"d", "s", ""}); // 63 in Em
		expected.add(new String[]{"a", "s", ""}); // 70 in Bm
		expected.add(new String[]{"e", "s", ""}); // 65 in F#m
		expected.add(new String[]{"b", "s", ""}); // 60 in C#m
		expected.add(new String[]{"f", "x", ""}); // 67 in G#m

		// 4. R3 for minor (or minor parallel)
		// Flats
		expected.add(new String[]{"c", "s", ""}); // 61 in Am
		expected.add(new String[]{"f", "s", ""}); // 66 in Dm
		expected.add(new String[]{"b", "n", ""}); // 71 in Gm
		expected.add(new String[]{"e", "n", ""}); // 64 in Cm
		expected.add(new String[]{"a", "n", ""}); // 69 in Fm
		expected.add(new String[]{"d", "n", ""}); // 62 in Bbm
		// Sharps
		expected.add(new String[]{"g", "s", ""}); // 68 in Em
		expected.add(new String[]{"d", "s", ""}); // 63 in Bm
		expected.add(new String[]{"a", "s", ""}); // 70 in F#m
		expected.add(new String[]{"e", "s", ""}); // 65 in C#m
		expected.add(new String[]{"b", "s", ""}); // 60 in G#m
		// 5. R6 for minor (or minor parallel)
		// Flats
		expected.add(new String[]{"f", "s", ""}); // 66 in Am
		expected.add(new String[]{"b", "n", ""}); // 71 in Dm
		expected.add(new String[]{"e", "n", ""}); // 64 in Gm
		expected.add(new String[]{"a", "n", ""}); // 69 in Cm
		expected.add(new String[]{"d", "n", ""}); // 62 in Fm
		expected.add(new String[]{"g", "n", ""}); // 67 in Bbm
		// Sharps
		expected.add(new String[]{"c", "s", ""}); // 61 in Em
		expected.add(new String[]{"g", "s", ""}); // 68 in Bm
		expected.add(new String[]{"d", "s", ""}); // 63 in F#m
		expected.add(new String[]{"a", "s", ""}); // 70 in C#m
		expected.add(new String[]{"e", "s", ""}); // 65 in G#m

		List<String[]> actual = new ArrayList<>();
		// pitch is in key
		// Flats
		actual.add((String[]) PitchKeyTools.spellPitch(71, 0, gridsMinAm, aie).get(0));
		actual.add((String[]) PitchKeyTools.spellPitch(70, -1, gridsMinDm, aie).get(0));
		actual.add((String[]) PitchKeyTools.spellPitch(63, -2, gridsMajGm, aie).get(0));
		actual.add((String[]) PitchKeyTools.spellPitch(68, -3, gridsMinCm, aie).get(0));
		actual.add((String[]) PitchKeyTools.spellPitch(61, -4, gridsMinFm, aie).get(0));
		actual.add((String[]) PitchKeyTools.spellPitch(66, -5, gridsMajBbm, aie).get(0));
		// Sharps
		actual.add((String[]) PitchKeyTools.spellPitch(66, 1, gridsMinEm, aie).get(0));
		actual.add((String[]) PitchKeyTools.spellPitch(61, 2, gridsMinBm, aie).get(0));
		actual.add((String[]) PitchKeyTools.spellPitch(68, 3, gridsMinFsm, aie).get(0));
		actual.add((String[]) PitchKeyTools.spellPitch(63, 4, gridsMinCsm, aie).get(0));
		actual.add((String[]) PitchKeyTools.spellPitch(70, 5, gridsMinGsm, aie).get(0));
		// pitch is not in key
		// 1. next or second-next KA
		// Flats
		actual.add((String[]) PitchKeyTools.spellPitch(70, 0, gridsMinAm, aie).get(0));
		actual.add((String[]) PitchKeyTools.spellPitch(68, -1, gridsMinDm, aie).get(0));
		actual.add((String[]) PitchKeyTools.spellPitch(68, -2, gridsMajGm, aie).get(0));
		actual.add((String[]) PitchKeyTools.spellPitch(66, -3, gridsMinCm, aie).get(0));
		actual.add((String[]) PitchKeyTools.spellPitch(66, -4, gridsMinFm, aie).get(0));
		actual.add((String[]) PitchKeyTools.spellPitch(64, -5, gridsMajBbm, aie).get(0));
		// Sharps
		actual.add((String[]) PitchKeyTools.spellPitch(68, 1, gridsMinEm, aie).get(0));
		actual.add((String[]) PitchKeyTools.spellPitch(68, 2, gridsMinBm, aie).get(0));
		actual.add((String[]) PitchKeyTools.spellPitch(70, 3, gridsMinFsm, aie).get(0));
		actual.add((String[]) PitchKeyTools.spellPitch(70, 4, gridsMinCsm, aie).get(0));
		actual.add((String[]) PitchKeyTools.spellPitch(60, 5, gridsMinGsm, aie).get(0));
		// 2. naturalised KA
		// Flats
		actual.add((String[]) PitchKeyTools.spellPitch(71, -1, gridsMinDm, aie).get(0));
		actual.add((String[]) PitchKeyTools.spellPitch(64, -2, gridsMajGm, aie).get(0));
		actual.add((String[]) PitchKeyTools.spellPitch(69, -3, gridsMinCm, aie).get(0));
		actual.add((String[]) PitchKeyTools.spellPitch(62, -4, gridsMinFm, aie).get(0));
		actual.add((String[]) PitchKeyTools.spellPitch(67, -5, gridsMajBbm, aie).get(0));
		// Sharps
		actual.add((String[]) PitchKeyTools.spellPitch(65, 1, gridsMinEm, aie).get(0));
		actual.add((String[]) PitchKeyTools.spellPitch(60, 2, gridsMinBm, aie).get(0));
		actual.add((String[]) PitchKeyTools.spellPitch(67, 3, gridsMinFsm, aie).get(0));
		actual.add((String[]) PitchKeyTools.spellPitch(62, 4, gridsMinCsm, aie).get(0));
		actual.add((String[]) PitchKeyTools.spellPitch(69, 5, gridsMinGsm, aie).get(0));
		// 3. ULT/LLT for minor (or minor parallel)
		// ULT, flats
		actual.add((String[]) PitchKeyTools.spellPitch(70, 0, gridsMinAm, aie).get(0));
		actual.add((String[]) PitchKeyTools.spellPitch(63, -1, gridsMinDm, aie).get(0));
		actual.add((String[]) PitchKeyTools.spellPitch(68, -2, gridsMinGm, aie).get(0));
		actual.add((String[]) PitchKeyTools.spellPitch(61, -3, gridsMinCm, aie).get(0));
		actual.add((String[]) PitchKeyTools.spellPitch(66, -4, gridsMinFm, aie).get(0));
		actual.add((String[]) PitchKeyTools.spellPitch(71, -5, gridsMinBbm, aie).get(0));
		// ULT, sharps
		actual.add((String[]) PitchKeyTools.spellPitch(65, 1, gridsMinEm, aie).get(0));
		actual.add((String[]) PitchKeyTools.spellPitch(60, 2, gridsMinBm, aie).get(0));
		actual.add((String[]) PitchKeyTools.spellPitch(67, 3, gridsMinFsm, aie).get(0));
		actual.add((String[]) PitchKeyTools.spellPitch(62, 4, gridsMinCsm, aie).get(0));
		actual.add((String[]) PitchKeyTools.spellPitch(69, 5, gridsMinGsm, aie).get(0));

		// LLT, flats
		actual.add((String[]) PitchKeyTools.spellPitch(68, 0, gridsMinAm, aie).get(0));
		actual.add((String[]) PitchKeyTools.spellPitch(61, -1, gridsMinDm, aie).get(0));
		actual.add((String[]) PitchKeyTools.spellPitch(66, -2, gridsMinGm, aie).get(0));
		actual.add((String[]) PitchKeyTools.spellPitch(71, -3, gridsMinCm, aie).get(0));
		actual.add((String[]) PitchKeyTools.spellPitch(64, -4, gridsMinFm, aie).get(0));
		actual.add((String[]) PitchKeyTools.spellPitch(69, -5, gridsMinBbm, aie).get(0));
		// LLT, sharps
		actual.add((String[]) PitchKeyTools.spellPitch(63, 1, gridsMinEm, aie).get(0));
		actual.add((String[]) PitchKeyTools.spellPitch(70, 2, gridsMinBm, aie).get(0));
		actual.add((String[]) PitchKeyTools.spellPitch(65, 3, gridsMinFsm, aie).get(0));
		actual.add((String[]) PitchKeyTools.spellPitch(60, 4, gridsMinCsm, aie).get(0));
		actual.add((String[]) PitchKeyTools.spellPitch(67, 5, gridsMinGsm, aie).get(0));

		// 4. R3 for minor (or minor parallel)
		// Flats
		actual.add((String[]) PitchKeyTools.spellPitch(61, 0, gridsMinAm, aie).get(0));
		actual.add((String[]) PitchKeyTools.spellPitch(66, -1, gridsMinDm, aie).get(0));
		actual.add((String[]) PitchKeyTools.spellPitch(71, -2, gridsMinGm, aie).get(0));
		actual.add((String[]) PitchKeyTools.spellPitch(64, -3, gridsMinCm, aie).get(0));
		actual.add((String[]) PitchKeyTools.spellPitch(69, -4, gridsMinFm, aie).get(0));
		actual.add((String[]) PitchKeyTools.spellPitch(62, -5, gridsMinBbm, aie).get(0));
		// Sharps
		actual.add((String[]) PitchKeyTools.spellPitch(68, 1, gridsMinEm, aie).get(0));
		actual.add((String[]) PitchKeyTools.spellPitch(63, 2, gridsMinBm, aie).get(0));
		actual.add((String[]) PitchKeyTools.spellPitch(70, 3, gridsMinFsm, aie).get(0));
		actual.add((String[]) PitchKeyTools.spellPitch(65, 4, gridsMinCsm, aie).get(0));
		actual.add((String[]) PitchKeyTools.spellPitch(60, 5, gridsMinGsm, aie).get(0));
		// 5. R6 for minor (or minor parallel)
		// Flats
		actual.add((String[]) PitchKeyTools.spellPitch(66, 0, gridsMinAm, aie).get(0));
		actual.add((String[]) PitchKeyTools.spellPitch(71, -1, gridsMinDm, aie).get(0));
		actual.add((String[]) PitchKeyTools.spellPitch(64, -2, gridsMinGm, aie).get(0));
		actual.add((String[]) PitchKeyTools.spellPitch(69, -3, gridsMinCm, aie).get(0));
		actual.add((String[]) PitchKeyTools.spellPitch(62, -4, gridsMinFm, aie).get(0));
		actual.add((String[]) PitchKeyTools.spellPitch(67, -5, gridsMinBbm, aie).get(0));
		// Sharps
		actual.add((String[]) PitchKeyTools.spellPitch(61, 1, gridsMinEm, aie).get(0));
		actual.add((String[]) PitchKeyTools.spellPitch(68, 2, gridsMinBm, aie).get(0));
		actual.add((String[]) PitchKeyTools.spellPitch(63, 3, gridsMinFsm, aie).get(0));
		actual.add((String[]) PitchKeyTools.spellPitch(70, 4, gridsMinCsm, aie).get(0));
		actual.add((String[]) PitchKeyTools.spellPitch(65, 5, gridsMinGsm, aie).get(0));

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i).length, actual.get(i).length);
			for (int j = 0; j < expected.get(i).length; j++) {
				assertEquals(expected.get(i)[j], actual.get(i)[j]);
			}
		}
	}


	@Test
	public void testGetMIDIPitchClassKeySigs() {		
		List<List<Integer>> expected = new ArrayList<List<Integer>>();
		expected.add(Arrays.asList(new Integer[]{9, 3, 8, 1, 6, 11, 4}));
		expected.add(Arrays.asList(new Integer[]{10, 3, 8, 1, 6, 11, 4}));
		expected.add(Arrays.asList(new Integer[]{10, 3, 8, 1, 6, 11}));
		expected.add(Arrays.asList(new Integer[]{10, 3, 8, 1, 6}));
		expected.add(Arrays.asList(new Integer[]{10, 3, 8, 1}));
		expected.add(Arrays.asList(new Integer[]{10, 3, 8}));
		expected.add(Arrays.asList(new Integer[]{10, 3}));
		expected.add(Arrays.asList(new Integer[]{10}));
		expected.add(Arrays.asList(new Integer[]{}));
		expected.add(Arrays.asList(new Integer[]{6}));
		expected.add(Arrays.asList(new Integer[]{6, 1}));
		expected.add(Arrays.asList(new Integer[]{6, 1, 8}));
		expected.add(Arrays.asList(new Integer[]{6, 1, 8, 3}));
		expected.add(Arrays.asList(new Integer[]{6, 1, 8, 3, 10}));
		expected.add(Arrays.asList(new Integer[]{6, 1, 8, 3, 10, 5}));
		expected.add(Arrays.asList(new Integer[]{6, 1, 8, 3, 10, 5, 0}));
		expected.add(Arrays.asList(new Integer[]{7, 1, 8, 3, 10, 5, 0}));
		
		List<List<Integer>> actual = new ArrayList<List<Integer>>();
		for (Integer key : PitchKeyTools.KEY_SIG_MPCS.keySet()) {
			actual.add(PitchKeyTools.getMIDIPitchClassKeySigs(key));
		}
		
		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i).size(), actual.get(i).size());
			for (int j = 0; j < expected.get(i).size(); j++) {
				assertEquals(expected.get(i).get(j), actual.get(i).get(j));
			}
		}	
	};


	@Test
	public void testAddToListIfNotInList() {
		List<Boolean> expected = Arrays.asList(false, true, false, true);

		List<Boolean> actual = new ArrayList<>();
		List<Integer> l = new ArrayList<>(Arrays.asList(0, 1, 2, 3));
		List<Integer> elems = Arrays.asList(4, 1, -1, 3);
		elems.forEach(e -> actual.add(PitchKeyTools.addToListIfNotInList(l, e)));

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i), actual.get(i));
		}
		assertEquals(expected, actual);
	}


	@Test
	public void testSpellPitchDeprecated() {
		Integer[][] mpcGridMaj = PitchKeyTools.makeMIDIPitchClassGrids(0);
		String[][] altGridMaj = PitchKeyTools.makeAlterationGrids(mpcGridMaj);
		String[][] pcGridMaj = PitchKeyTools.makePitchClassGrids(0);
		Integer[][] mpcGridMin = PitchKeyTools.makeMIDIPitchClassGrids(1);
		String[][] altGridMin = PitchKeyTools.makeAlterationGrids(mpcGridMin);
		String[][] pcGridMin = PitchKeyTools.makePitchClassGrids(1);

		int Bbm = 3;
		int Fm = 4;
		int Cm = 5;
		int Gm = 6;		
		int Dm = 7;
		int Am = 8; // int C = 8;
		int Em = 9; // int G = 9;
		int Bm = 10; // int D = 10;
		int Fsm = 11;
		int Csm = 12;
		int Gsm = 13;

		List<Object> gridsMinAm = Arrays.asList(new Object[]{mpcGridMin[Am], altGridMin[Am], pcGridMin[Am]});
		List<Object> gridsMinDm = Arrays.asList(new Object[]{mpcGridMin[Dm], altGridMin[Dm], pcGridMin[Dm]});
		List<Object> gridsMajGm = Arrays.asList(new Object[]{mpcGridMin[Gm], altGridMin[Gm], pcGridMin[Gm]});
		List<Object> gridsMinCm = Arrays.asList(new Object[]{mpcGridMin[Cm], altGridMin[Cm], pcGridMin[Cm]});
		List<Object> gridsMinFm = Arrays.asList(new Object[]{mpcGridMin[Fm], altGridMin[Fm], pcGridMin[Fm]});
		List<Object> gridsMajBbm = Arrays.asList(new Object[]{mpcGridMin[Bbm], altGridMin[Bbm], pcGridMin[Bbm]});
		List<Object> gridsMinEm = Arrays.asList(new Object[]{mpcGridMin[Em], altGridMin[Em], pcGridMin[Em]});
		List<Object> gridsMinBm = Arrays.asList(new Object[]{mpcGridMin[Bm], altGridMin[Bm], pcGridMin[Bm]});
		List<Object> gridsMinFsm = Arrays.asList(new Object[]{mpcGridMin[Fsm], altGridMin[Fsm], pcGridMin[Fsm]});
		List<Object> gridsMinCsm = Arrays.asList(new Object[]{mpcGridMin[Csm], altGridMin[Csm], pcGridMin[Csm]});
		List<Object> gridsMinGsm = Arrays.asList(new Object[]{mpcGridMin[Gsm], altGridMin[Gsm], pcGridMin[Gsm]});
		List<Object> gridsMinGm = Arrays.asList(new Object[]{mpcGridMin[Gm], altGridMin[Gm], pcGridMin[Gm]});
		List<Object> gridsMinBbm = Arrays.asList(new Object[]{mpcGridMin[Bbm], altGridMin[Bbm], pcGridMin[Bbm]});

		List<List<Integer>> aie = null;

		List<String[]> expected = new ArrayList<>();
		// pitch is in key
		// Flats
		expected.add(new String[]{"b", "n", ""}); // 71 in Am
		expected.add(new String[]{"b", "f", ""}); // 70 in Dm
		expected.add(new String[]{"e", "f", ""}); // 63 in Gm
		expected.add(new String[]{"a", "f", ""}); // 68 in Cm
		expected.add(new String[]{"d", "f", ""}); // 61 in Fm
		expected.add(new String[]{"g", "f", ""}); // 66 in Bbm
		// Sharps
		expected.add(new String[]{"f", "s", ""}); // 66 in Em
		expected.add(new String[]{"c", "s", ""}); // 61 in Bm
		expected.add(new String[]{"g", "s", ""}); // 68 in F#m
		expected.add(new String[]{"d", "s", ""}); // 63 in C#m
		expected.add(new String[]{"a", "s", ""}); // 70 in G#m
		// pitch is not in key
		// 1. next or second-next KA
		// Flats
		expected.add(new String[]{"b", "f", ""}); // 70 in Am
		expected.add(new String[]{"a", "f", ""}); // 68 in Dm
		expected.add(new String[]{"a", "f", ""}); // 68 in Gm
		expected.add(new String[]{"g", "f", ""}); // 66 in Cm
		expected.add(new String[]{"g", "f", ""}); // 66 in Fm
		expected.add(new String[]{"f", "f", ""}); // 64 in Bbm
		// Sharps
		expected.add(new String[]{"g", "s", ""}); // 68 in Em
		expected.add(new String[]{"g", "s", ""}); // 68 in Bm
		expected.add(new String[]{"a", "s", ""}); // 70 in F#m
		expected.add(new String[]{"a", "s", ""}); // 70 in C#m
		expected.add(new String[]{"b", "s", ""}); // 60 in G#m
		// 2. naturalised KA
		// Flats
		expected.add(new String[]{"b", "n", ""}); // 71 in Dm
		expected.add(new String[]{"e", "n", ""}); // 64 in Gm
		expected.add(new String[]{"a", "n", ""}); // 69 in Cm
		expected.add(new String[]{"d", "n", ""}); // 62 in Fm
		expected.add(new String[]{"g", "n", ""}); // 67 in Bbm
		// Sharps
		expected.add(new String[]{"f", "n", ""}); // 65 in Em
		expected.add(new String[]{"c", "n", ""}); // 60 in Bm
		expected.add(new String[]{"g", "n", ""}); // 67 in F#m
		expected.add(new String[]{"d", "n", ""}); // 62 in C#m
		expected.add(new String[]{"a", "n", ""}); // 69 in G#m
		// 3. ULT/LLT for minor (or minor parallel)
		// ULT, flats
		expected.add(new String[]{"b", "f", ""}); // 70 in Am
		expected.add(new String[]{"e", "f", ""}); // 63 in Dm
		expected.add(new String[]{"a", "f", ""}); // 68 in Gm
		expected.add(new String[]{"d", "f", ""}); // 61 in Cm
		expected.add(new String[]{"g", "f", ""}); // 66 in Fm
		expected.add(new String[]{"c", "f", ""}); // 71 in Bbm
		// ULT, sharps
		expected.add(new String[]{"f", "n", ""}); // 65 in Em
		expected.add(new String[]{"c", "n", ""}); // 60 in Bm
		expected.add(new String[]{"g", "n", ""}); // 67 in F#m
		expected.add(new String[]{"d", "n", ""}); // 62 in C#m
		expected.add(new String[]{"a", "n", ""}); // 69 in G#m
		// LLT, flats
		expected.add(new String[]{"g", "s", ""}); // 68 in Am
		expected.add(new String[]{"c", "s", ""}); // 61 in Dm
		expected.add(new String[]{"f", "s", ""}); // 66 in Gm
		expected.add(new String[]{"b", "n", ""}); // 71 in Cm
		expected.add(new String[]{"e", "n", ""}); // 64 in Fm
		expected.add(new String[]{"a", "n", ""}); // 69 in Bbm
		// LLT, sharps
		expected.add(new String[]{"d", "s", ""}); // 63 in Em
		expected.add(new String[]{"a", "s", ""}); // 70 in Bm
		expected.add(new String[]{"e", "s", ""}); // 65 in F#m
		expected.add(new String[]{"b", "s", ""}); // 60 in C#m
		expected.add(new String[]{"f", "x", ""}); // 67 in G#m
		// 4. R3 for minor (or minor parallel)
		// Flats
		expected.add(new String[]{"c", "s", ""}); // 61 in Am
		expected.add(new String[]{"f", "s", ""}); // 66 in Dm
		expected.add(new String[]{"b", "n", ""}); // 71 in Gm
		expected.add(new String[]{"e", "n", ""}); // 64 in Cm
		expected.add(new String[]{"a", "n", ""}); // 69 in Fm
		expected.add(new String[]{"d", "n", ""}); // 62 in Bbm
		// Sharps
		expected.add(new String[]{"g", "s", ""}); // 68 in Em
		expected.add(new String[]{"d", "s", ""}); // 63 in Bm
		expected.add(new String[]{"a", "s", ""}); // 70 in F#m
		expected.add(new String[]{"e", "s", ""}); // 65 in C#m
		expected.add(new String[]{"b", "s", ""}); // 60 in G#m
		// 5. R6 for minor (or minor parallel)
		// Flats
		expected.add(new String[]{"f", "s", ""}); // 66 in Am
		expected.add(new String[]{"b", "n", ""}); // 71 in Dm
		expected.add(new String[]{"e", "n", ""}); // 64 in Gm
		expected.add(new String[]{"a", "n", ""}); // 69 in Cm
		expected.add(new String[]{"d", "n", ""}); // 62 in Fm
		expected.add(new String[]{"g", "n", ""}); // 67 in Bbm
		// Sharps
		expected.add(new String[]{"c", "s", ""}); // 61 in Em
		expected.add(new String[]{"g", "s", ""}); // 68 in Bm
		expected.add(new String[]{"d", "s", ""}); // 63 in F#m
		expected.add(new String[]{"a", "s", ""}); // 70 in C#m
		expected.add(new String[]{"e", "s", ""}); // 65 in G#m

		List<String[]> actual = new ArrayList<>();
		// pitch is in key
		// Flats
		actual.add((String[]) PitchKeyTools.spellPitchDeprecated(71, 0, gridsMinAm, aie).get(0));
		actual.add((String[]) PitchKeyTools.spellPitchDeprecated(70, -1, gridsMinDm, aie).get(0));
		actual.add((String[]) PitchKeyTools.spellPitchDeprecated(63, -2, gridsMajGm, aie).get(0));
		actual.add((String[]) PitchKeyTools.spellPitchDeprecated(68, -3, gridsMinCm, aie).get(0));
		actual.add((String[]) PitchKeyTools.spellPitchDeprecated(61, -4, gridsMinFm, aie).get(0));
		actual.add((String[]) PitchKeyTools.spellPitchDeprecated(66, -5, gridsMajBbm, aie).get(0));
		// Sharps
		actual.add((String[]) PitchKeyTools.spellPitchDeprecated(66, 1, gridsMinEm, aie).get(0));
		actual.add((String[]) PitchKeyTools.spellPitchDeprecated(61, 2, gridsMinBm, aie).get(0));
		actual.add((String[]) PitchKeyTools.spellPitchDeprecated(68, 3, gridsMinFsm, aie).get(0));
		actual.add((String[]) PitchKeyTools.spellPitchDeprecated(63, 4, gridsMinCsm, aie).get(0));
		actual.add((String[]) PitchKeyTools.spellPitchDeprecated(70, 5, gridsMinGsm, aie).get(0));
		// pitch is not in key
		// 1. next or second-next KA
		// Flats
		actual.add((String[]) PitchKeyTools.spellPitchDeprecated(70, 0, gridsMinAm, aie).get(0));
		actual.add((String[]) PitchKeyTools.spellPitchDeprecated(68, -1, gridsMinDm, aie).get(0));
		actual.add((String[]) PitchKeyTools.spellPitchDeprecated(68, -2, gridsMajGm, aie).get(0));
		actual.add((String[]) PitchKeyTools.spellPitchDeprecated(66, -3, gridsMinCm, aie).get(0));
		actual.add((String[]) PitchKeyTools.spellPitchDeprecated(66, -4, gridsMinFm, aie).get(0));
		actual.add((String[]) PitchKeyTools.spellPitchDeprecated(64, -5, gridsMajBbm, aie).get(0));
		// Sharps
		actual.add((String[]) PitchKeyTools.spellPitchDeprecated(68, 1, gridsMinEm, aie).get(0));
		actual.add((String[]) PitchKeyTools.spellPitchDeprecated(68, 2, gridsMinBm, aie).get(0));
		actual.add((String[]) PitchKeyTools.spellPitchDeprecated(70, 3, gridsMinFsm, aie).get(0));
		actual.add((String[]) PitchKeyTools.spellPitchDeprecated(70, 4, gridsMinCsm, aie).get(0));
		actual.add((String[]) PitchKeyTools.spellPitchDeprecated(60, 5, gridsMinGsm, aie).get(0));
		// 2. naturalised KA
		// Flats
		actual.add((String[]) PitchKeyTools.spellPitchDeprecated(71, -1, gridsMinDm, aie).get(0));
		actual.add((String[]) PitchKeyTools.spellPitchDeprecated(64, -2, gridsMajGm, aie).get(0));
		actual.add((String[]) PitchKeyTools.spellPitchDeprecated(69, -3, gridsMinCm, aie).get(0));
		actual.add((String[]) PitchKeyTools.spellPitchDeprecated(62, -4, gridsMinFm, aie).get(0));
		actual.add((String[]) PitchKeyTools.spellPitchDeprecated(67, -5, gridsMajBbm, aie).get(0));
		// Sharps
		actual.add((String[]) PitchKeyTools.spellPitchDeprecated(65, 1, gridsMinEm, aie).get(0));
		actual.add((String[]) PitchKeyTools.spellPitchDeprecated(60, 2, gridsMinBm, aie).get(0));
		actual.add((String[]) PitchKeyTools.spellPitchDeprecated(67, 3, gridsMinFsm, aie).get(0));
		actual.add((String[]) PitchKeyTools.spellPitchDeprecated(62, 4, gridsMinCsm, aie).get(0));
		actual.add((String[]) PitchKeyTools.spellPitchDeprecated(69, 5, gridsMinGsm, aie).get(0));
		// 3. ULT/LLT for minor (or minor parallel)
		// ULT, flats
		actual.add((String[]) PitchKeyTools.spellPitchDeprecated(70, 0, gridsMinAm, aie).get(0));
		actual.add((String[]) PitchKeyTools.spellPitchDeprecated(63, -1, gridsMinDm, aie).get(0));
		actual.add((String[]) PitchKeyTools.spellPitchDeprecated(68, -2, gridsMinGm, aie).get(0));
		actual.add((String[]) PitchKeyTools.spellPitchDeprecated(61, -3, gridsMinCm, aie).get(0));
		actual.add((String[]) PitchKeyTools.spellPitchDeprecated(66, -4, gridsMinFm, aie).get(0));
		actual.add((String[]) PitchKeyTools.spellPitchDeprecated(71, -5, gridsMinBbm, aie).get(0));
		// ULT, sharps
		actual.add((String[]) PitchKeyTools.spellPitchDeprecated(65, 1, gridsMinEm, aie).get(0));
		actual.add((String[]) PitchKeyTools.spellPitchDeprecated(60, 2, gridsMinBm, aie).get(0));
		actual.add((String[]) PitchKeyTools.spellPitchDeprecated(67, 3, gridsMinFsm, aie).get(0));
		actual.add((String[]) PitchKeyTools.spellPitchDeprecated(62, 4, gridsMinCsm, aie).get(0));
		actual.add((String[]) PitchKeyTools.spellPitchDeprecated(69, 5, gridsMinGsm, aie).get(0));
		// LLT, flats
		actual.add((String[]) PitchKeyTools.spellPitchDeprecated(68, 0, gridsMinAm, aie).get(0));
		actual.add((String[]) PitchKeyTools.spellPitchDeprecated(61, -1, gridsMinDm, aie).get(0));
		actual.add((String[]) PitchKeyTools.spellPitchDeprecated(66, -2, gridsMinGm, aie).get(0));
		actual.add((String[]) PitchKeyTools.spellPitchDeprecated(71, -3, gridsMinCm, aie).get(0));
		actual.add((String[]) PitchKeyTools.spellPitchDeprecated(64, -4, gridsMinFm, aie).get(0));
		actual.add((String[]) PitchKeyTools.spellPitchDeprecated(69, -5, gridsMinBbm, aie).get(0));
		// LLT, sharps
		actual.add((String[]) PitchKeyTools.spellPitchDeprecated(63, 1, gridsMinEm, aie).get(0));
		actual.add((String[]) PitchKeyTools.spellPitchDeprecated(70, 2, gridsMinBm, aie).get(0));
		actual.add((String[]) PitchKeyTools.spellPitchDeprecated(65, 3, gridsMinFsm, aie).get(0));
		actual.add((String[]) PitchKeyTools.spellPitchDeprecated(60, 4, gridsMinCsm, aie).get(0));
		actual.add((String[]) PitchKeyTools.spellPitchDeprecated(67, 5, gridsMinGsm, aie).get(0));
		// 4. R3 for minor (or minor parallel)
		// Flats
		actual.add((String[]) PitchKeyTools.spellPitchDeprecated(61, 0, gridsMinAm, aie).get(0));
		actual.add((String[]) PitchKeyTools.spellPitchDeprecated(66, -1, gridsMinDm, aie).get(0));
		actual.add((String[]) PitchKeyTools.spellPitchDeprecated(71, -2, gridsMinGm, aie).get(0));
		actual.add((String[]) PitchKeyTools.spellPitchDeprecated(64, -3, gridsMinCm, aie).get(0));
		actual.add((String[]) PitchKeyTools.spellPitchDeprecated(69, -4, gridsMinFm, aie).get(0));
		actual.add((String[]) PitchKeyTools.spellPitchDeprecated(62, -5, gridsMinBbm, aie).get(0));
		// Sharps
		actual.add((String[]) PitchKeyTools.spellPitchDeprecated(68, 1, gridsMinEm, aie).get(0));
		actual.add((String[]) PitchKeyTools.spellPitchDeprecated(63, 2, gridsMinBm, aie).get(0));
		actual.add((String[]) PitchKeyTools.spellPitchDeprecated(70, 3, gridsMinFsm, aie).get(0));
		actual.add((String[]) PitchKeyTools.spellPitchDeprecated(65, 4, gridsMinCsm, aie).get(0));
		actual.add((String[]) PitchKeyTools.spellPitchDeprecated(60, 5, gridsMinGsm, aie).get(0));
		// 5. R6 for minor (or minor parallel)
		// Flats
		actual.add((String[]) PitchKeyTools.spellPitchDeprecated(66, 0, gridsMinAm, aie).get(0));
		actual.add((String[]) PitchKeyTools.spellPitchDeprecated(71, -1, gridsMinDm, aie).get(0));
		actual.add((String[]) PitchKeyTools.spellPitchDeprecated(64, -2, gridsMinGm, aie).get(0));
		actual.add((String[]) PitchKeyTools.spellPitchDeprecated(69, -3, gridsMinCm, aie).get(0));
		actual.add((String[]) PitchKeyTools.spellPitchDeprecated(62, -4, gridsMinFm, aie).get(0));
		actual.add((String[]) PitchKeyTools.spellPitchDeprecated(67, -5, gridsMinBbm, aie).get(0));
		// Sharps
		actual.add((String[]) PitchKeyTools.spellPitchDeprecated(61, 1, gridsMinEm, aie).get(0));
		actual.add((String[]) PitchKeyTools.spellPitchDeprecated(68, 2, gridsMinBm, aie).get(0));
		actual.add((String[]) PitchKeyTools.spellPitchDeprecated(63, 3, gridsMinFsm, aie).get(0));
		actual.add((String[]) PitchKeyTools.spellPitchDeprecated(70, 4, gridsMinCsm, aie).get(0));
		actual.add((String[]) PitchKeyTools.spellPitchDeprecated(65, 5, gridsMinGsm, aie).get(0));

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i).length, actual.get(i).length);
			for (int j = 0; j < expected.get(i).length; j++) {
				assertEquals(expected.get(i)[j], actual.get(i)[j]);
			}
		}
	}

}
