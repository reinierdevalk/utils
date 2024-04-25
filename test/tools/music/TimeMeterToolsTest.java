package tools.music;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

///import org.junit.After;
///import org.junit.Before;
///import org.junit.Test;

import de.uos.fmt.musitech.utility.math.Rational;
import exports.MEIExport;
import junit.framework.TestCase;
import path.Path;
import representations.Tablature;
import structure.TimelineTest;
import tbp.Encoding;

public class TimeMeterToolsTest extends TestCase {

	private File encodingTestpiece;
	private File encodingTestGetMeterInfo;
//	private File midiTestpiece;
//	private File midiTestGetMeterKeyInfo;
	public static final double T_99 = 99.99999999999999;
	public static final double T_100 = 100.0;
	public static final double T_289 = 289.99937166802806;
	public static final double T_439 = 439.0008341015848;
	
	private final Rational r128 = new Rational(1, 128);
	private final Rational r64 = new Rational(1, 64);
	private final Rational r32 = new Rational(1, 32);
	private final Rational r16 = new Rational(1, 16);
	private final Rational r8 = new Rational(1, 8);
	private final Rational r4 = new Rational(1, 4);
	private final Rational r2 = new Rational(1, 2);
	private final Rational r1 = new Rational(1, 1);


	public void setUp() throws Exception {
		String root = Path.ROOT_PATH_DEPLOYMENT_DEV; 
		encodingTestpiece = new File(
			root + Path.ENCODINGS_REL_PATH + Path.TEST_DIR + "testpiece.tbp"
		);
		encodingTestGetMeterInfo = new File(
			root + Path.ENCODINGS_REL_PATH + Path.TEST_DIR + "test_get_meter_info.tbp"
		);
//		midiTestpiece = new File(
//			root + Path.getMIDIPath() + Path.getTestDir() + "testpiece.mid"
//		);
//		midiTestGetMeterKeyInfo = new File(
//			root + Path.getMIDIPath() + Path.getTestDir() + "test_get_meter_key_info.mid"
//		);
	}


	public void tearDown() throws Exception {
	}


	private List<List<Rational>> getTestFractions() {
		List<List<Rational>> testFractions = new ArrayList<List<Rational>>();
		testFractions.add(Arrays.asList(new Rational[]{r128}));
		testFractions.add(Arrays.asList(new Rational[]{r64}));
		testFractions.add(Arrays.asList(new Rational[]{r64, r128})); // 1 dot
		testFractions.add(Arrays.asList(new Rational[]{r32}));
		testFractions.add(Arrays.asList(new Rational[]{r32, r128}));
		testFractions.add(Arrays.asList(new Rational[]{r32, r64})); // 1 dot
		testFractions.add(Arrays.asList(new Rational[]{r32, r64, r128})); // 2 dots
		testFractions.add(Arrays.asList(new Rational[]{r16}));
		testFractions.add(Arrays.asList(new Rational[]{r16, r128}));
		testFractions.add(Arrays.asList(new Rational[]{r16, r64}));
		testFractions.add(Arrays.asList(new Rational[]{r16, r64, r128}));
		testFractions.add(Arrays.asList(new Rational[]{r16, r32})); // 1 dot
		testFractions.add(Arrays.asList(new Rational[]{r16, r32, r128}));
		testFractions.add(Arrays.asList(new Rational[]{r16, r32, r64})); // 2 dots
		testFractions.add(Arrays.asList(new Rational[]{r16, r32, r64, r128})); // 3 dots
		testFractions.add(Arrays.asList(new Rational[]{r8}));
		testFractions.add(Arrays.asList(new Rational[]{r8, r128}));
		testFractions.add(Arrays.asList(new Rational[]{r8, r64}));
		testFractions.add(Arrays.asList(new Rational[]{r8, r64, r128}));
		testFractions.add(Arrays.asList(new Rational[]{r8, r32}));
		testFractions.add(Arrays.asList(new Rational[]{r8, r32, r128}));
		testFractions.add(Arrays.asList(new Rational[]{r8, r32, r64}));
		testFractions.add(Arrays.asList(new Rational[]{r8, r32, r64, r128}));
		testFractions.add(Arrays.asList(new Rational[]{r8, r16})); // 1 dot
		testFractions.add(Arrays.asList(new Rational[]{r8, r16, r128})); 
		testFractions.add(Arrays.asList(new Rational[]{r8, r16, r64}));
		testFractions.add(Arrays.asList(new Rational[]{r8, r16, r64, r128}));
		testFractions.add(Arrays.asList(new Rational[]{r8, r16, r32})); // 2 dots
		testFractions.add(Arrays.asList(new Rational[]{r8, r16, r32, r128}));
		testFractions.add(Arrays.asList(new Rational[]{r8, r16, r32, r64})); // 3 dots
		testFractions.add(Arrays.asList(new Rational[]{r8, r16, r32, r64, r128})); // 4 dots
		testFractions.add(Arrays.asList(new Rational[]{r4}));
		//
		testFractions.add(Arrays.asList(new Rational[]{r2, r4, r8})); // 2 dots
		testFractions.add(Arrays.asList(new Rational[]{r2, r4, r8, r128}));
		testFractions.add(Arrays.asList(new Rational[]{r2, r4, r8, r64}));
		testFractions.add(Arrays.asList(new Rational[]{r2, r4, r8, r64, r128}));
		testFractions.add(Arrays.asList(new Rational[]{r2, r4, r8, r32}));
		testFractions.add(Arrays.asList(new Rational[]{r2, r4, r8, r32, r128}));
		testFractions.add(Arrays.asList(new Rational[]{r2, r4, r8, r32, r64}));
		testFractions.add(Arrays.asList(new Rational[]{r2, r4, r8, r32, r64, r128}));
		testFractions.add(Arrays.asList(new Rational[]{r2, r4, r8, r16})); // 3 dots
		testFractions.add(Arrays.asList(new Rational[]{r2, r4, r8, r16, r128}));
		testFractions.add(Arrays.asList(new Rational[]{r2, r4, r8, r16, r64}));
		testFractions.add(Arrays.asList(new Rational[]{r2, r4, r8, r16, r64, r128}));
		testFractions.add(Arrays.asList(new Rational[]{r2, r4, r8, r16, r32})); // 4 dots
		testFractions.add(Arrays.asList(new Rational[]{r2, r4, r8, r16, r32, r128}));
		testFractions.add(Arrays.asList(new Rational[]{r2, r4, r8, r16, r32, r64})); // 5 dots
		testFractions.add(Arrays.asList(new Rational[]{r2, r4, r8, r16, r32, r64, r128})); // 6 dots
		testFractions.add(Arrays.asList(new Rational[]{r1}));
		
		return testFractions;
	}


	public void testDiminuteMeter() {
		Rational twoTwo = new Rational(2, 2);
		Rational fourFour = new Rational(4, 4);

		List<Rational> expected = new ArrayList<>();
		expected.add(twoTwo); // 2/1, dim = 2
		expected.add(fourFour); // 4/2, dim = 2
		expected.add(fourFour); // 4/1, dim = 4
		expected.add(twoTwo); // 2/4, dim = -2
		expected.add(fourFour); // 4/8, dim = -2
		expected.add(fourFour); // 4/16, dim = -4

		List<Rational> meters = Arrays.asList(new Rational[]{
			new Rational(2, 1), new Rational(4, 2), new Rational(4, 1), 
			new Rational(2, 4), new Rational(4, 8), new Rational(4, 16)
		});
		List<Integer> dims = Arrays.asList(new Integer[]{2, 2, 4, -2, -2, -4});
		List<Rational> actual = new ArrayList<>();
		for (int i = 0; i < meters.size(); i++) {
			actual.add(TimeMeterTools.diminuteMeter(meters.get(i), dims.get(i)));
		}

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i), actual.get(i));
		} 
		assertEquals(expected, actual);
	}


	public void testUndiminuteMeter() {
		List<Rational> expected = new ArrayList<>();
		expected.add(new Rational(2, 1)); // 2/2, dim = 2
		expected.add(new Rational(4, 2)); // 4/4, dim = 2
		expected.add(new Rational(4, 1)); // 4/4, dim = 4
		expected.add(new Rational(2, 4)); // 2/2, dim = -2
		expected.add(new Rational(4, 8)); // 4/4, dim = -2
		expected.add(new Rational(4, 16)); // 4/4, dim = -4

		Rational twoTwo = new Rational(2, 2);
		Rational fourFour = new Rational(4, 4);
		List<Rational> meters = Arrays.asList(new Rational[]{
			twoTwo, fourFour, fourFour, twoTwo, fourFour, fourFour
		});
		List<Integer> dims = Arrays.asList(new Integer[]{2, 2, 4, -2, -2, -4});
		List<Rational> actual = new ArrayList<>();
		for (int i = 0; i < meters.size(); i++) {
			actual.add(TimeMeterTools.undiminuteMeter(meters.get(i), dims.get(i)));
		}

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i), actual.get(i));
		} 
		assertEquals(expected, actual);
	}


	public void testDiminute() {
		List<Rational> expected = Arrays.asList(new Rational[]{
			new Rational(3, 2),
			new Rational(2, 2), 
			new Rational(2, 2),
			new Rational(3, 2),
			new Rational(3, 8),
		});

		List<Rational> undim = Arrays.asList(new Rational[]{
			new Rational(3, 8),
			new Rational(2, 4),
			new Rational(2, 2),
			new Rational(3, 1),
			new Rational(3, 2),
		});
		List<Integer> diminutions = Arrays.asList(new Integer[]{-4, -2, 1, 2, 4});
		List<Rational> actual = new ArrayList<>();
		for (int i = 0; i < diminutions.size(); i++) {
			actual.add(TimeMeterTools.diminute(undim.get(i), diminutions.get(i)));
		}

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i), actual.get(i));
		}
		assertEquals(expected, actual);
	}


	public void testDiminuteAlt() {
		List<Double> expected = Arrays.asList(new Double[]{1.5, 1.0, 1.0, 1.5, 0.375});

		List<Double> undim = Arrays.asList(new Double[]{0.375, 0.5, 1.0, 3.0, 1.5});
		List<Integer> diminutions = Arrays.asList(new Integer[]{-4, -2, 1, 2, 4});
		List<Double> actual = new ArrayList<>();
		for (int i = 0; i < diminutions.size(); i++) {
			actual.add(TimeMeterTools.diminute(undim.get(i), diminutions.get(i)));
		}

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i), actual.get(i));
		}
		assertEquals(expected, actual);
	}


	public void testCalculateTime() {
		// Tablature/non-tablature case
		List<Rational> durs = Arrays.asList(new Rational[]{
			new Rational(10, 1),
			new Rational(10, 1),
			new Rational(10, 1),
			new Rational(10, 1),
		});
		List<Double> tempi = Arrays.asList(new Double[]{
			T_99,
			T_100,
			T_289,
			T_439,
		});

		List<Long> expected = Arrays.asList(new Long[]{
			(long) 24000000, (long) 12000000,
			(long) 24000000, (long) 12000000,
			(long) 8275880, (long) 4137940,
			(long) 5466960, (long) 2733480
		});

		List<Long> actual = new ArrayList<>();
		for (int i = 0; i < tempi.size(); i++) {
			actual.add(TimeMeterTools.calculateTime(durs.get(i), tempi.get(i)));
			actual.add(TimeMeterTools.calculateTime(durs.get(i).div(2), tempi.get(i)));
		}

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i), actual.get(i));
		}
		assertEquals(expected, actual);
	}


	public void testGetMetricPosition() {
		// For a piece with meter changes
		Tablature t1 = new Tablature(encodingTestGetMeterInfo);
		List<Rational[]> expected = TimelineTest.getMetricPositions("testGetMeterInfo", true);

		// For a piece with no meter changes
		Tablature t2 = new Tablature(encodingTestpiece);
		expected.addAll(TimelineTest.getMetricPositions("testpiece", true));

		List<Rational[]> actual = new ArrayList<Rational[]>();
		Integer[][] btp1 = t1.getBasicTabSymbolProperties();
		List<Integer[]> meterInfo1 = t1.getMeterInfo();
//		List<Integer[]> meterInfo1 = t1.getTimeline().getMeterInfoOBS();
		for (int i = 0; i < btp1.length; i++) {
			Rational currMetricTime = 
				new Rational(btp1[i][Tablature.ONSET_TIME], Tablature.SRV_DEN);
			currMetricTime.reduce();
			actual.add(TimeMeterTools.getMetricPosition(currMetricTime, meterInfo1));
		}
		Integer[][] btp2 = t2.getBasicTabSymbolProperties();
		List<Integer[]> meterInfo2 = t2.getMeterInfo();
//		List<Integer[]> meterInfo2 = t2.getTimeline().getMeterInfoOBS();
		for (int i = 0; i < btp2.length; i++) {
			Rational currMetricTime = 
				new Rational(btp2[i][Tablature.ONSET_TIME], Tablature.SRV_DEN);
			currMetricTime.reduce();
			actual.add(TimeMeterTools.getMetricPosition(currMetricTime, meterInfo2));
		}

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i).length, actual.get(i).length);
			for (int j = 0; j < expected.get(i).length; j++) {
				assertEquals(expected.get(i)[j], actual.get(i)[j]);
			}
		}
	}


	public void testGetDiminution() {
		List<List<Rational>> allMetricTimes = new ArrayList<>();
		// For testGetMeterInfo
		List<Rational> metricTimes1 = new ArrayList<>();
		metricTimes1.add(new Rational(0, 1)); // start bar 1 
		metricTimes1.add(new Rational(5, 8)); // bar 2 1/4
		metricTimes1.add(new Rational(11, 8)); // start bar 3
		metricTimes1.add(new Rational(21, 8)); // bar 4 1/4
		metricTimes1.add(new Rational(25, 8)); // start bar 5
		metricTimes1.add(new Rational(33, 8)); // bar 6 1/4
		metricTimes1.add(new Rational(39, 8)); // start bar 7
		metricTimes1.add(new Rational(48, 8)); // in bar 8 2/16
		metricTimes1.add(new Rational(99, 16)); // start bar 9
		allMetricTimes.add(metricTimes1);
		// For testpiece
		List<Rational> metricTimes2 = new ArrayList<>();
		metricTimes2.add(new Rational(0, 1)); // start bar 1
		metricTimes2.add(new Rational(7, 4)); // in bar 2
		metricTimes2.add(new Rational(8, 4)); // start bar 3
		metricTimes2.add(new Rational(10, 4)); // in bar 3
		allMetricTimes.add(metricTimes2);

		List<Integer> expected = new ArrayList<>();
		// For testGetMeterInfo
		expected.addAll(Arrays.asList(new Integer[]{2, 2, 2, 4, 4, 1, 1, 1, -2}));
		// For testPiece
		expected.addAll(Arrays.asList(new Integer[]{1, 1, 1, 1}));

		List<Integer> actual = new ArrayList<>();
		List<Tablature> tabs = Arrays.asList(new Tablature[]{
			new Tablature(encodingTestGetMeterInfo),
			new Tablature(encodingTestpiece)});
		for (int i = 0; i < tabs.size(); i++) {
			for (Rational mt : allMetricTimes.get(i)) {
				actual.add(TimeMeterTools.getDiminution(mt, tabs.get(i).getMeterInfo()));
			}
		}

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i), actual.get(i));
		}
		assertEquals(expected, actual);
	}


	public void testGetUnitFractions() {
		List<List<Rational>> expected = new ArrayList<List<Rational>>(getTestFractions());
		List<List<Rational>> actual = new ArrayList<List<Rational>>();
		for (int i = 1; i <= 32; i++) {
			actual.add(TimeMeterTools.getUnitFractions(new Rational(i, 128), new Rational(1, 128)));
		}
		for (int i = 112; i <= 128; i++) {
			actual.add(TimeMeterTools.getUnitFractions(new Rational(i, 128), new Rational(1, 128)));
		}
		
		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i).size(), actual.get(i).size());
			for (int j = 0; j < expected.get(i).size(); j++) {
				assertEquals(expected.get(i).get(j), actual.get(i).get(j));
			}
		}
		assertEquals(expected, actual);
	}


	public void testGetUnitFractionSequences() {
		List<List<Rational>> all = new ArrayList<List<Rational>>();
		
		all.add(Arrays.asList(new Rational[]{r2})); // 1 list
		all.add(Arrays.asList(new Rational[]{r2, r4, r8, r16})); // 1 list
		all.add(Arrays.asList(new Rational[]{r2, r4, r16, r32})); // 2 lists
		all.add(Arrays.asList(new Rational[]{r2, r4, r8, r32})); // 2 lists
		all.add(Arrays.asList(new Rational[]{r2, r8, r32, r128})); // 4 lists
		
		List<List<List<Rational>>> expected = new ArrayList<List<List<Rational>>>();
		List<List<Rational>> exp0 = new ArrayList<List<Rational>>();
		exp0.add(Arrays.asList(new Rational[]{r2}));
		expected.add(exp0);
		List<List<Rational>> exp1 = new ArrayList<List<Rational>>();
		exp1.add(Arrays.asList(new Rational[]{r2, r4, r8, r16}));
		expected.add(exp1);
		List<List<Rational>> exp2 = new ArrayList<List<Rational>>();
		exp2.add(Arrays.asList(new Rational[]{r2, r4}));
		exp2.add(Arrays.asList(new Rational[]{r16, r32}));
		expected.add(exp2);
		List<List<Rational>> exp3 = new ArrayList<List<Rational>>();
		exp3.add(Arrays.asList(new Rational[]{r2, r4, r8}));
		exp3.add(Arrays.asList(new Rational[]{r32}));
		expected.add(exp3);
		List<List<Rational>> exp4 = new ArrayList<List<Rational>>();
		exp4.add(Arrays.asList(new Rational[]{r2}));
		exp4.add(Arrays.asList(new Rational[]{r8}));
		exp4.add(Arrays.asList(new Rational[]{r32}));
		exp4.add(Arrays.asList(new Rational[]{r128}));
		expected.add(exp4);
		
		List<List<List<Rational>>> actual = new ArrayList<List<List<Rational>>>();
		for (List<Rational> l : all) {
			actual.add(TimeMeterTools.getUnitFractionSequences(l));
		}

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i).size(), actual.get(i).size());
			for (int j = 0; j < expected.get(i).size(); j++) {
				assertEquals(expected.get(i).get(j).size(), actual.get(i).get(j).size());
				for (int k = 0; k < expected.get(i).get(j).size(); k++) {
					assertEquals(expected.get(i).get(j).get(k), actual.get(i).get(j).get(k));
				}
			}
		}
		assertEquals(expected, actual);
	}


	public void testGetDotLengtheningFactor() {
		List<Rational> expected = Arrays.asList(
			Rational.ZERO,
			new Rational(1, 2), 
			new Rational(3, 4), 
			new Rational(7, 8)
		);

		List<Rational> actual = new ArrayList<>();
		actual.add(TimeMeterTools.getDotLengtheningFactor(0));
		actual.add(TimeMeterTools.getDotLengtheningFactor(1));
		actual.add(TimeMeterTools.getDotLengtheningFactor(2));
		actual.add(TimeMeterTools.getDotLengtheningFactor(3));

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i), actual.get(i));
		}
		assertEquals(expected, actual);
	}


	public void testGetNumDots() {
		List<List<Rational>> all = getTestFractions();
		
		List<Integer> expected = new ArrayList<Integer>();
		for (int i = 0; i < all.size(); i++) {
			expected.add(0);
		}
		expected.set(2, 1); expected.set(5, 1); expected.set(11, 1); expected.set(23, 1);
		expected.set(6, 2); expected.set(13, 2); expected.set(27, 2); expected.set(32, 2);
		expected.set(14, 3); expected.set(29, 3); expected.set(40, 3);
		expected.set(30, 4); expected.set(44, 4); expected.set(46, 5); expected.set(47, 6); 
		
		List<Integer> actual = new ArrayList<Integer>();
		for (List<Rational> l : all) {
			actual.add(TimeMeterTools.getNumDots(l));		
		}
		
		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
		 	assertEquals(expected.get(i), actual.get(i));
		}
		assertEquals(expected, actual);
	}


	public void testRound() {
		List<Rational> all = Arrays.asList(new Rational[]{
			new Rational(1, 32),
			new Rational(7, 128),
			new Rational(57, 128),
			new Rational(131, 128)
		});

		List<Rational> expected = Arrays.asList(new Rational[]{
			new Rational(1, 32), // 1/32
			new Rational(1, 16), // 7/128
			new Rational(29, 64), // 57/128
			new Rational(33, 32) // 131/128
		});

		List<Rational> actual = new ArrayList<Rational>();
		for (Rational r : all) {
			actual.add(TimeMeterTools.round(r, new Rational(1, 64)));
		}

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i), actual.get(i));
		}
		assertEquals(expected, actual);
	}


	public void testRoundAlt() {
		Rational two = new Rational(2, 1);
		List<Rational> all = Arrays.asList(new Rational[]{
			// On grid
			new Rational(0, 96),
			new Rational(48, 96),
			new Rational(96, 96),
			// Between 0/96 and 1/96
			new Rational(1, 4*96), // closest to 0/96
			new Rational(2, 4*96), // equally close to both
			new Rational(3, 4*96), // closest to 1/96
			// Between 47/96 and 48/96
			new Rational(47, 96).add(new Rational(1, 4*96)), // closest to 47/96
			new Rational(47, 96).add(new Rational(2, 4*96)), // equally close to both
			new Rational(47, 96).add(new Rational(3, 4*96)), // closest to 48/96
			// Between 95/96 and 96/96
			new Rational(95, 96).add(new Rational(1, 4*96)), // closest to 95/96
			new Rational(95, 96).add(new Rational(2, 4*96)), // equally close to both
			new Rational(95, 96).add(new Rational(3, 4*96)), // closest to 96/96
			
			// Between 2 and 2 1/96
			two.add(new Rational(1, 4*96)), // closest to 2
			two.add(new Rational(2, 4*96)), // equally close to both
			two.add(new Rational(3, 4*96)), // closest to 2 1/96
			// Between 2 47/96 and 2 48/96
			two.add(new Rational(47, 96).add(new Rational(1, 4*96))), // closest to 2 47/96
			two.add(new Rational(47, 96).add(new Rational(2, 4*96))), // equally close to both
			two.add(new Rational(47, 96).add(new Rational(3, 4*96))), // closest to 2 48/96
			// Between 2 95/96 and 3
			two.add(new Rational(95, 96).add(new Rational(1, 4*96))), // closest to 2 95/96
			two.add(new Rational(95, 96).add(new Rational(2, 4*96))), // equally close to both
			two.add(new Rational(95, 96).add(new Rational(3, 4*96))), // closest to 3
		});
		List<Rational> expected = Arrays.asList(new Rational[]{
			new Rational(0, 96),
			new Rational(1, 2),
			new Rational(1, 1),
			//
			new Rational(0, 96), 
			new Rational(1, 96), 
			new Rational(1, 96),
			//
			new Rational(47, 96), 
			new Rational(48, 96), 
			new Rational(48, 96),
			//
			new Rational(95, 96), 
			new Rational(96, 96), 
			new Rational(96, 96),
			//
			two.add(new Rational(0, 96)), 
			two.add(new Rational(1, 96)), 
			two.add(new Rational(1, 96)),
			//
			two.add(new Rational(47, 96)), 
			two.add(new Rational(48, 96)), 
			two.add(new Rational(48, 96)),
			//
			two.add(new Rational(95, 96)), 
			two.add(new Rational(96, 96)), 
			two.add(new Rational(96, 96)),
		});
		List<Integer> gridNums = IntStream.rangeClosed(0, 96).boxed().collect(Collectors.toList());
		List<Rational> actual = new ArrayList<Rational>();
		for (Rational r : all) {
			actual.add(TimeMeterTools.round(r, gridNums));
		}
		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i), actual.get(i));
		}
		assertEquals(expected, actual);
	}


	public void testGetDottedNoteLength() {
		List<Rational> expected = Arrays.asList(new Rational[]{
			// 1/1
			new Rational(1, 1), // 0 dots
			new Rational(3, 2), // 1 dot
			new Rational(7, 4), // 2 dots
			new Rational(15, 8), // 3 dots
			// 1/2
			new Rational(1, 2), // 0 dots
			new Rational(3, 4), // 1 dot
			new Rational(7, 8), // 2 dots
			new Rational(15, 16), // 3 dots
			// 1/4
			new Rational(1, 4), // 0 dots
			new Rational(3, 8), // 1 dot
			new Rational(7, 16), // 2 dots
			new Rational(15, 32), // 3 dots

		});

		List<Rational> undotted = Arrays.asList(new Rational[]{
			new Rational(1, 1), new Rational(1, 1), new Rational(1, 1), new Rational(1, 1),
			new Rational(1, 2), new Rational(1, 2), new Rational(1, 2), new Rational(1, 2),
			new Rational(1, 4), new Rational(1, 4), new Rational(1, 4), new Rational(1, 4)
		});
		List<Integer> dots = Arrays.asList(new Integer[]{0, 1, 2, 3, 0, 1, 2, 3, 0, 1, 2, 3});
		List<Rational> actual = new ArrayList<>();
		for (int i = 0; i < undotted.size(); i++) {
			actual.add(TimeMeterTools.getDottedNoteLength(undotted.get(i), dots.get(i)));
		}

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i), actual.get(i));
		}
		assertEquals(expected, actual);
	}


	public void testGetDottedNoteLengthAlt() {
		List<Integer> expected = Arrays.asList(new Integer[]{
			// 1/1
			96, 144, 168, 180, // 0, 1, 2, 3 dots
			// 1/2
			48, 72, 84, 90, // 0, 1, 2, 3 dots
			// 1/4
			24, 36, 42, 45 // 0, 1, 2, 3 dots
		});

		List<Integer> undotted = Arrays.asList(new Integer[]{
			96, 96, 96, 96, 48, 48, 48, 48, 24, 24, 24, 24
		});
		List<Integer> dots = Arrays.asList(new Integer[]{0, 1, 2, 3, 0, 1, 2, 3, 0, 1, 2, 3});
		List<Integer> actual = new ArrayList<>();
		for (int i = 0; i < undotted.size(); i++) {
			actual.add(TimeMeterTools.getDottedNoteLength(undotted.get(i), dots.get(i)));
		}

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i), actual.get(i));
		}
		assertEquals(expected, actual);
	}


	public void testGetUndottedNoteLength() {
		List<Integer> expected = Arrays.asList(new Integer[]{
			96, 96, 96, 96, // 1/1 
			48, 48, 48, 48, // 1/2 
			24, 24, 24, 24 // 1/4 
		});

		List<Integer> dotted = Arrays.asList(new Integer[]{
			96, 144, 168, 180, // 0, 1, 2, 3 dots 
			48, 72, 84, 90, // 0, 1, 2, 3 dots 
			24, 36, 42, 45 // 0, 1, 2, 3 dots
		});
		List<Integer> dots = Arrays.asList(new Integer[]{0, 1, 2, 3, 0, 1, 2, 3, 0, 1, 2, 3});
		List<Integer> actual = new ArrayList<>();
		for (int i = 0; i < dotted.size(); i++) {
			actual.add(TimeMeterTools.getUndottedNoteLength(dotted.get(i), dots.get(i)));
		}

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i), actual.get(i));
		}
		assertEquals(expected, actual);
	}


	public void testIsTripletOnset() {
		// Example from 4465_33-34_memor_esto-2.tbp, bars 64-65
		// Rhythm = H Q | Q Q Q |
		// Onsets = 63/1, 63 1/3 (= 190/3) | 63 1/2 (= 127/2), 63 2/3 (= 191/3), 63 5/6 (= 383/6) |
		Rational o1 = new Rational(63, 1);
		Rational o2 = new Rational(190, 3); // 63 1/3
		//
		Rational o3 = new Rational(127, 2); // 63 1/2
		Rational o4 = new Rational(191, 3); // 63 2/3
		Rational o5 = new Rational(383, 6); // 63 5/6
		//
		Rational o6 = new Rational(64, 1);
		Rational trUnit = new Rational(24, 1);

		List<Rational> onsets = Arrays.asList(
			o1, o2, o3, o4, o5, o6
		);

		List<Rational[]> tripletOnsetPairs = Arrays.asList(
			new Rational[]{o1, o2, trUnit},
			new Rational[]{o3, o5, trUnit}
		);

		List<List<Boolean>> expected = Arrays.asList(
			Arrays.asList(true, false, false),
			Arrays.asList(false, false, true),
			//
			Arrays.asList(true, false, false),
			Arrays.asList(false, true, false),
			Arrays.asList(false, false, true),
			//
			Arrays.asList(false, false, false)
		);

		List<List<Boolean>> actual = new ArrayList<>();
		for (Rational o : onsets) {
			actual.add(TimeMeterTools.isTripletOnset(tripletOnsetPairs, o));
		}

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i), actual.get(i));
			for (int j = 0; j < expected.get(i).size(); j++) {
				assertEquals(expected.get(i).get(j), actual.get(i).get(j) );
			}
		}		
	}


	public void testGetExtendedTripletOnsetPair() {
		// Example from 4465_33-34_memor_esto-2.tbp, bars 64-65
		// Rhythm = H Q | Q Q Q |
		// Onsets = 63/1, 63 1/3 (= 190/3) | 63 1/2 (= 127/2), 63 2/3 (= 191/3), 63 5/6 (= 383/6) |
		Rational o1 = new Rational(63, 1);
		Rational o2 = new Rational(190, 3); // 63 1/3
		//
		Rational o3 = new Rational(127, 2); // 63 1/2
		Rational o4 = new Rational(191, 3); // 63 2/3
		Rational o5 = new Rational(383, 6); // 63 5/6
		//
		Rational o6 = new Rational(64, 1);
		Rational trUnit = new Rational(24, 1);

		List<Rational> onsets = Arrays.asList(
			o1, o2, o3, o4, o5, o6
		);

		List<Rational[]> tripletOnsetPairs = Arrays.asList(
			new Rational[]{o1, o2, trUnit},
			new Rational[]{o3, o5, trUnit}
		);

		List<Rational[]> expected = Arrays.asList(
			new Rational[]{o1, o2, trUnit, new Rational(1, 2)}, // onset = 63; diminution = 1
//			new Rational[]{o1, o2, trUnit, new Rational(1, 1)}, // onset = 63; diminution = 2
//			new Rational[]{o1, o2, trUnit, new Rational(1, 4)}, // onset = 63; diminution = -2
			new Rational[]{o1, o2, trUnit, new Rational(1, 2)}, // onset = 63 1/3; diminution = 1
//			new Rational[]{o1, o2, trUnit, new Rational(1, 1)}, // onset = 63 1/3; diminution = 2
//			new Rational[]{o1, o2, trUnit, new Rational(1, 4)}, // onset = 63 1/3; diminution = -2
			//
			new Rational[]{o3, o5, trUnit, new Rational(1, 2)}, // onset = 63 1/2; diminution = 1
//			new Rational[]{o3, o5, trUnit, new Rational(1, 1)}, // onset = 63 1/2; diminution = 2
//			new Rational[]{o3, o5, trUnit, new Rational(1, 4)}, // onset = 63 1/2; diminution = -2
			new Rational[]{o3, o5, trUnit, new Rational(1, 2)}, // onset = 63 2/3; diminution = 1
//			new Rational[]{o3, o5, trUnit, new Rational(1, 1)}, // onset = 63 2/3; diminution = 2
//			new Rational[]{o3, o5, trUnit, new Rational(1, 4)}, // onset = 63 2/3; diminution = -2
			new Rational[]{o3, o5, trUnit, new Rational(1, 2)}, // onset = 63 5/6; diminution = 1
//			new Rational[]{o3, o5, trUnit, new Rational(1, 2)}, // onset = 63 5/6; diminution = -2
//			new Rational[]{o3, o5, trUnit, new Rational(1, 1)}, // onset = 63 5/6; diminution = 2
			//
			null // onset = 64; diminution = 1
//			null, // onset = 64; diminution = 2
//			null  // onset = 64; diminution = -2
		);

		List<Rational[]> actual = new ArrayList<>();
		for (Rational o : onsets) {
			actual.add(TimeMeterTools.getExtendedTripletOnsetPair(o, tripletOnsetPairs/*, 1*/));
//			actual.add(TimeMeterTools.getExtendedTripletOnsetPair(o, tripletOnsetPairs, 2));
//			actual.add(TimeMeterTools.getExtendedTripletOnsetPair(o, tripletOnsetPairs, -2));
		}

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
//			System.out.println("i = " + i);
//			if (expected.get(i) != null) {
//				System.out.println(Arrays.asList(expected.get(i)));
//			}
//			else {
//				System.out.println(expected.get(i));
//			}
//			if (actual.get(i) != null) {
//				System.out.println(Arrays.asList(actual.get(i)));
//			}
//			else {
//				System.out.println(actual.get(i));
//			}
			if (expected.get(i) == null) {
				assertEquals(expected.get(i), actual.get(i));
			}
			else {
				assertEquals(expected.get(i).length, actual.get(i).length);
				for (int j = 0; j < expected.get(i).length; j++) {
					assertEquals(expected.get(i)[j], actual.get(i)[j]);
				}
			}			
		}
	}


	public void testGetFinalOffset() {
		List<Rational> expected = Arrays.asList(
			new Rational(6, 2),
			new Rational(115, 16)
		);

		List<Rational> actual = Arrays.asList(
			TimeMeterTools.getFinalOffset(
				new Tablature(new Encoding(encodingTestpiece), false).getMeterInfo()
			),
			TimeMeterTools.getFinalOffset(
				new Tablature(new Encoding(encodingTestGetMeterInfo), false).getMeterInfo()
			)
		);

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i), actual.get(i));
		} 
		assertEquals(expected, actual);
	}

}
