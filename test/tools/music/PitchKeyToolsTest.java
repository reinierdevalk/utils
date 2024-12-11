package tools.music;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class PitchKeyToolsTest {

	@Before
	public void setUp() throws Exception {
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
	public void testGetMIDIPitchClassKeySigs() {		
		List<List<Integer>> expected = new ArrayList<List<Integer>>();
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
		
		List<List<Integer>> actual = new ArrayList<List<Integer>>();
		for (Integer key : PitchKeyTools.getKeySigMPCs().keySet()) {
//		for (Entry<Integer, Integer[]> entry : MEIExport.getKeys().entrySet()) {
//		for (Integer[] key : MEIExport.getKeys()) {
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
	public void testMakeMIDIPitchClassGrid() {
		Integer[][] expected = new Integer[30][7];
		expected[0] = new Integer[]{11, 1, 3, 4, 6, 8, 10}; // Cb
		expected[1] = new Integer[]{6, 8, 10, 11, 1, 3, 5}; // Gb
		expected[2] = new Integer[]{1, 3, 5, 6, 8, 10, 0}; // Db
		expected[3] = new Integer[]{8, 10, 0, 1, 3, 5, 7}; // Ab
		expected[4] = new Integer[]{3, 5, 7, 8, 10, 0, 2}; // Eb
		expected[5] = new Integer[]{10, 0, 2, 3, 5, 7, 9}; // Bb
		expected[6] = new Integer[]{5, 7, 9, 10, 0, 2, 4}; // F
		expected[7] = new Integer[]{0, 2, 4, 5, 7, 9, 11}; // C 
		expected[8] = new Integer[]{7, 9, 11, 0, 2, 4, 6}; // G
		expected[9] = new Integer[]{2, 4, 6, 7, 9, 11, 1}; // D
		expected[10] = new Integer[]{9, 11, 1, 2, 4, 6, 8}; // A
		expected[11] = new Integer[]{4, 6, 8, 9, 11, 1, 3}; // E
		expected[12] = new Integer[]{11, 1, 3, 4, 6, 8, 10}; // B
		expected[13] = new Integer[]{6, 8, 10, 11, 1, 3, 5}; // F#
		expected[14] = new Integer[]{1, 3, 5, 6, 8, 10, 0}; // C#
		//
		expected[15] = new Integer[]{8, 10, 11, 1, 3, 4, 6}; // Abm
		expected[16] = new Integer[]{3, 5, 6, 8, 10, 11, 1}; // Ebm
		expected[17] = new Integer[]{10, 0, 1, 3, 5, 6, 8}; // Bbm
		expected[18] = new Integer[]{5, 7, 8, 10, 0, 1, 3}; // Fm
		expected[19] = new Integer[]{0, 2, 3, 5, 7, 8, 10}; // Cm
		expected[20] = new Integer[]{7, 9, 10, 0, 2, 3, 5}; // Gm
		expected[21] = new Integer[]{2, 4, 5, 7, 9, 10, 0}; // Dm
		expected[22] = new Integer[]{9, 11, 0, 2, 4, 5, 7}; // Am 
		expected[23] = new Integer[]{4, 6, 7, 9, 11, 0, 2}; // Em
		expected[24] = new Integer[]{11, 1, 2, 4, 6, 7, 9}; // Bm
		expected[25] = new Integer[]{6, 8, 9, 11, 1, 2, 4}; // F#m
		expected[26] = new Integer[]{1, 3, 4, 6, 8, 9, 11}; // C#m
		expected[27] = new Integer[]{8, 10, 11, 1, 3, 4, 6}; // G#m
		expected[28] = new Integer[]{3, 5, 6, 8, 10, 11, 1}; // D#m
		expected[29] = new Integer[]{10, 0, 1, 3, 5, 6, 8}; // A#m

		Integer[][] maj = PitchKeyTools.makeMIDIPitchClassGrid(0);
		Integer[][] min = PitchKeyTools.makeMIDIPitchClassGrid(1);
		Integer[][] actual = new Integer[30][7];
		for (int i = 0; i < maj.length; i++) {
			actual[i] = maj[i];
		}
		for (int i = 0; i < min.length; i++) {
			actual[i+15] = min[i];
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
		String[][] expected = new String[30][7];
		expected[0] = new String[]{"f", "f", "f", "f", "f", "f", "f"}; // Cb
		expected[1] = new String[]{"f", "f", "f", "f", "f", "f", "n"}; // Gb
		expected[2] = new String[]{"f", "f", "n", "f", "f", "f", "n"}; // Db
		expected[3] = new String[]{"f", "f", "n", "f", "f", "n", "n"}; // Ab
		expected[4] = new String[]{"f", "n", "n", "f", "f", "n", "n"}; // Eb
		expected[5] = new String[]{"f", "n", "n", "f", "n", "n", "n"}; // Bb
		expected[6] = new String[]{"n", "n", "n", "f", "n", "n", "n"}; // F
		expected[7] = new String[]{"n", "n", "n", "n", "n", "n", "n"}; // C
		expected[8] = new String[]{"n", "n", "n", "n", "n", "n", "s"}; // G
		expected[9] = new String[]{"n", "n", "s", "n", "n", "n", "s"}; // D
		expected[10] = new String[]{"n", "n", "s", "n", "n", "s", "s"}; // A
		expected[11] = new String[]{"n", "s", "s", "n", "n", "s", "s"}; // E
		expected[12] = new String[]{"n", "s", "s", "n", "s", "s", "s"}; // B
		expected[13] = new String[]{"s", "s", "s", "n", "s", "s", "s"}; // F#
		expected[14] = new String[]{"s", "s", "s", "s", "s", "s", "s"}; // C#
		//
		expected[15] = new String[]{"f", "f", "f", "f", "f", "f", "f"}; // Abm
		expected[16] = new String[]{"f", "n", "f", "f", "f", "f", "f"}; // Ebm
		expected[17] = new String[]{"f", "n", "f", "f", "n", "f", "f"}; // Bbm
		expected[18] = new String[]{"n", "n", "f", "f", "n", "f", "f"}; // Fm
		expected[19] = new String[]{"n", "n", "f", "n", "n", "f", "f"}; // Cm
		expected[20] = new String[]{"n", "n", "f", "n", "n", "f", "n"}; // Gm
		expected[21] = new String[]{"n", "n", "n", "n", "n", "f", "n"}; // Dm
		expected[22] = new String[]{"n", "n", "n", "n", "n", "n", "n"}; // Am
		expected[23] = new String[]{"n", "s", "n", "n", "n", "n", "n"}; // Em
		expected[24] = new String[]{"n", "s", "n", "n", "s", "n", "n"}; // Bm
		expected[25] = new String[]{"s", "s", "n", "n", "s", "n", "n"}; // F#m
		expected[26] = new String[]{"s", "s", "n", "s", "s", "n", "n"}; // C#m
		expected[27] = new String[]{"s", "s", "n", "s", "s", "n", "s"}; // G#m
		expected[28] = new String[]{"s", "s", "s", "s", "s", "n", "s"}; // D#m
		expected[29] = new String[]{"s", "s", "s", "s", "s", "s", "s"}; // A#m
		
		String[][] maj = PitchKeyTools.makeAlterationGrid(PitchKeyTools.makeMIDIPitchClassGrid(0));
		String[][] min = PitchKeyTools.makeAlterationGrid(PitchKeyTools.makeMIDIPitchClassGrid(1));
		String[][] actual = new String[30][7];
		for (int i = 0; i < maj.length; i++) {
			actual[i] = maj[i];
		}
		for (int i = 0; i < min.length; i++) {
			actual[i+15] = min[i];
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
		String[][] expected = new String[30][7];
		expected[0] = new String[]{"c", "d", "e", "f", "g", "a", "b"}; // Cb
		expected[1] = new String[]{"g", "a", "b", "c", "d", "e", "f"}; // Gb
		expected[2] = new String[]{"d", "e", "f", "g", "a", "b", "c"}; // Db
		expected[3] = new String[]{"a", "b", "c", "d", "e", "f", "g"}; // Ab
		expected[4] = new String[]{"e", "f", "g", "a", "b", "c", "d"}; // Eb
		expected[5] = new String[]{"b", "c", "d", "e", "f", "g", "a"}; // Bb
		expected[6] = new String[]{"f", "g", "a", "b", "c", "d", "e"}; // F
		expected[7] = new String[]{"c", "d", "e", "f", "g", "a", "b"}; // C
		expected[8] = new String[]{"g", "a", "b", "c", "d", "e", "f"}; // G
		expected[9] = new String[]{"d", "e", "f", "g", "a", "b", "c"}; // D
		expected[10] = new String[]{"a", "b", "c", "d", "e", "f", "g"}; // A
		expected[11] = new String[]{"e", "f", "g", "a", "b", "c", "d"}; // E
		expected[12] = new String[]{"b", "c", "d", "e", "f", "g", "a"}; // B
		expected[13] = new String[]{"f", "g", "a", "b", "c", "d", "e"}; // F#
		expected[14] = new String[]{"c", "d", "e", "f", "g", "a", "b"}; // C#
		//
		expected[15] = new String[]{"a", "b", "c", "d", "e", "f", "g"}; // Abm
		expected[16] = new String[]{"e", "f", "g", "a", "b", "c", "d"}; // Ebm
		expected[17] = new String[]{"b", "c", "d", "e", "f", "g", "a"}; // Bbm
		expected[18] = new String[]{"f", "g", "a", "b", "c", "d", "e"}; // Fm
		expected[19] = new String[]{"c", "d", "e", "f", "g", "a", "b"}; // Cm
		expected[20] = new String[]{"g", "a", "b", "c", "d", "e", "f"}; // Gm
		expected[21] = new String[]{"d", "e", "f", "g", "a", "b", "c"}; // Dm
		expected[22] = new String[]{"a", "b", "c", "d", "e", "f", "g"}; // Am
		expected[23] = new String[]{"e", "f", "g", "a", "b", "c", "d"}; // Em
		expected[24] = new String[]{"b", "c", "d", "e", "f", "g", "a"}; // Bm
		expected[25] = new String[]{"f", "g", "a", "b", "c", "d", "e"}; // F#m
		expected[26] = new String[]{"c", "d", "e", "f", "g", "a", "b", }; // C#m
		expected[27] = new String[]{"g", "a", "b", "c", "d", "e", "f"}; // G#m
		expected[28] = new String[]{"d", "e", "f", "g", "a", "b", "c"}; // D#m
		expected[29] = new String[]{"a", "b", "c", "d", "e", "f", "g"}; // A#m

		String[][] maj = PitchKeyTools.makePitchClassGrid(0);
		String[][] min = PitchKeyTools.makePitchClassGrid(1);
		String[][] actual = new String[30][7];
		for (int i = 0; i < maj.length; i++) {
			actual[i] = maj[i];
		}
		for (int i = 0; i < min.length; i++) {
			actual[i+15] = min[i];
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
	public void testSpellPitch() {
		Integer[][] mpcGridMaj = PitchKeyTools.makeMIDIPitchClassGrid(0);
		String[][] altGridMaj = PitchKeyTools.makeAlterationGrid(mpcGridMaj);
		String[][] pcGridMaj = PitchKeyTools.makePitchClassGrid(0);
		Integer[][] mpcGridMin = PitchKeyTools.makeMIDIPitchClassGrid(1);
		String[][] altGridMin = PitchKeyTools.makeAlterationGrid(mpcGridMin);
		String[][] pcGridMin = PitchKeyTools.makePitchClassGrid(1);

		int Bbm = 2;
		int Fm = 3;
		int Cm = 4;
		int Gm = 5;		
		int Dm = 6;
		int Am = 7; // int C = 7;
		int Em = 8; // int G = 8;
		int Bm = 9; // int D = 9;
		int Fsm = 10;
		int Csm = 11;
		int Gsm = 12;

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
//		Rational o = new Rational(1, 2);
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
}
