package tools.music;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import de.uos.fmt.musitech.utility.math.Rational;
import representations.Tablature;
import representations.Transcription;
import tools.ToolBox;

public class TimeMeterTools {
	
	////////////////////////////////
	//
	//  C L A S S  M E T H O D S
	//
	/**
	 * Given an undiminuted meter and a diminution, calculates the diminuted meter.
	 * 
	 * <ul>
	 * <li>diminution > 0: meter count stays the same; meter unit halves</li>
	 * <ul>
	 * <li>diminution = 2:	2/1 --> 2/(1*2) = 2/2</li>
	 * <li>diminution = 2:	4/2 --> 4/(2*2) = 4/4</li>
	 * <li>diminution = 4:	4/1 --> 4/(1*4) = 4/4</li>
	 * </ul>
	 * <li>diminution < 0: meter count stays the same; meter unit doubles</li>
	 * <ul>
	 * <li>diminution = -2: 2/4  --> 2/(4/|-2|) = 2/2</li>
	 * <li>diminution = -2: 4/8  --> 4/(8/|-2|) = 4/4</li>                    
	 * <li>diminution = -4: 4/16 --> 4/(16/|-4|) = 4/4</li>
	 * </ul>
	 * </ul>
	 * @param meter
	 * @param diminution
	 * @return
	 */
	// TESTED
	public static Rational diminuteMeter(Rational meter, int diminution) {
		Rational newMeter;
		if (diminution == 1) {
			newMeter = new Rational(meter.getNumer(), meter.getDenom());
		}
		else if (diminution > 0) {
			newMeter = new Rational(meter.getNumer(), (meter.getDenom() * diminution)); 
		}
		else {
			newMeter = new Rational(meter.getNumer(), meter.getDenom() / Math.abs(diminution));
		}
		return newMeter;
	}


	/**
	 * Given a diminuted meter and a diminution, calculates the undiminuted meter. 
	 * This method does the opposite of <code>diminuteMeter()</code>.
	 * 
	 * <ul>
	 * <li>diminution > 0: meter count stays the same; meter unit doubles</li>
	 * <ul>
	 * <li>diminution = 2:	2/2 --> 2/(2/2) = 2/1</li>
	 * <li>diminution = 2:	4/4 --> 4/(4/2) = 4/2</li>
	 * <li>diminution = 4:	4/4 --> 4/(4/4) = 4/1</li>
	 * </ul>
	 * <li>diminution < 0: meter count stays the same; meter unit halves</li>
	 * <ul>
	 * <li>diminution = -2: 2/2 --> 2/(2*|-2|) = 2/4</li>
	 * <li>diminution = -2: 4/4 --> 4/(4*|-2|) = 4/8</li>                    
	 * <li>diminution = -4: 4/4 --> 4/(4*|-4|) = 4/16</li>
	 * </ul>
	 * </ul>
	 * @param meter
	 * @param diminution
	 * @return
	 */
	// TESTED
	public static Rational undiminuteMeter(Rational meter, int diminution) {
		if (diminution == 1) {
			return new Rational(meter.getNumer(), meter.getDenom());
		}
		else if (diminution > 0) {
			return new Rational(meter.getNumer(), meter.getDenom() / diminution); 
		}
		else {
			return new Rational(meter.getNumer(), (meter.getDenom() * Math.abs(diminution)));
		}
	}


	/**
	 * Given a Rational (metric time or metric duration) and a diminution, calculates the 
	 * diminuted Rational.
	 * 
	 * @param r
	 * @param diminution
	 * @return
	 */
	// TESTED
	public static Rational diminute(Rational r, int diminution) {
		if (diminution == 1) {
			return r;
		}
		else if (diminution > 0) {
			return r.div(diminution); 
		}
		else {
			return r.mul(Math.abs(diminution));
		}
	}


	/**
	 * Given a double (time or tempo) and a diminution, calculates the diminuted double.
	 * 
	 * @param d
	 * @param diminution
	 * @return
	 */
	// TESTED
	public static double diminute(double d, int diminution) {
		if (diminution == 1) {
			return d;
		}
		else if (diminution > 0) {
			return d / diminution; 
		}
		else {
			return d * Math.abs(diminution);
		}
	}


	/**
	 * Calculates the time the given metric duration takes in the given tempo. 
	 * 
	 * Formula:
	 * tmp BPM = tmp/60 beats/s --> one whole note (four beats) every 240/tmp s (tmp/60 * x = 4 --> x = 240/tmp)
	 * 							--> ten whole notes every (10*240)/tmp s
	 * 							--> n whole notes every (n*240)/tmp s 
	 */
	// TESTED
	public static long calculateTime(Rational dur, double tempo) {
		double time = (dur.toDouble() * 240) / tempo;
		// Multiply by 1000000 to get time in microseconds; round
		time = Math.round(time * 1000000);
		return (long) time;
	}


	/**
	 * Gets the metric position of the note at the onset time. Returns a Rational[] with 
	 *   <ul>
	 *   <li>As element 0: the bar number, where the denominator is 1.</li>
	 *   <li>As element 1: the position within the bar, reduced and starting at 0/x (where x is the 
	 *                     common denominator, i.e., the product of the denominator of metricTime 
	 *                     and the largest meter denominator).</li>
	 *   </ul>
	 * If there is an anacrusis: if mt falls within the anacrusis, the bar number returned will be 0,
	 * and the position within the bar will be the position as if the anacrusis were a full bar.
	 * <br><br>
	 * Example: a metric time of 9/8 in meter 6/8 returns 2/1 and 3/8 (i.e., the fourth 8th note in bar 2).
	 * 
	 * Convenience method, to be used when when the <code>Transcription</code> or <code>Tablature</code>
	 * from which the meter info is extracted is not available.
	 * 
	 * @param mt
	 * @param meterInfo
	 * @return
	 */
	// TESTED
	public static Rational[] getMetricPosition(Rational mt, List<Integer[]> meterInfo) {
		Rational[] metricPos = new Rational[2];

		// 0. Determine the presence of an anacrusis
		boolean containsAnacrusis = false;
		if (meterInfo.get(0)[Tablature.MI_FIRST_BAR] == 0) {
			containsAnacrusis = true;
		}

		// 1. Determine the largest meter denominator and then the common denominator
		int largestMeterDenom = -1;
		for (Integer[] in : meterInfo) {
			if (in[Tablature.MI_DEN] > largestMeterDenom) {
				largestMeterDenom = in[Tablature.MI_DEN];
			}
		}
		int commonDenom = mt.getDenom() * largestMeterDenom;

		// 2. Express metricTime and all meters in commonDenom  	
		// a. metricTime
		Rational metricTimeInLargestDenom = 
			new Rational(mt.getNumer() * largestMeterDenom, mt.getDenom() * largestMeterDenom);
		// b. All meters
		List<Rational> metersInLargestDenom = new ArrayList<Rational>();
		for (int i = 0; i < meterInfo.size(); i++) {
			Integer[] currMeter = 
				new Integer[]{meterInfo.get(i)[Tablature.MI_NUM], meterInfo.get(i)[Tablature.MI_DEN]};
			// factor will always be an int because largestMeterDenom will always be a multiple of currMeter[1]    	
			int factor = (largestMeterDenom / currMeter[1]) * mt.getDenom();  
			metersInLargestDenom.add(new Rational(currMeter[0] * factor, commonDenom));
		}

		// 3. List for the initial meter and any following meter change points the metric time (in commonDenom).
		// The first element of the list will be the metric time of the first full bar
		// The last element of the list will be the metric time of the fictional bar after the last bar
		List<Rational> meterChangePointsMetricTimes = new ArrayList<Rational>();
		// Determine the initial meter change point and set startIndex so that if an anacrusis is present, the
		// first element of argMeterInfo (containing the anacrusis information) is skipped
		int startIndex;
		if (containsAnacrusis) {
			meterChangePointsMetricTimes.add(
				new Rational(metersInLargestDenom.get(0).getNumer(), commonDenom));
			startIndex = 1;
		}
		else {
			meterChangePointsMetricTimes.add(new Rational(0, commonDenom));
			startIndex = 0;
		}
		// Determine the remaining meter change points
		for (int i = startIndex; i < meterInfo.size(); i++) {
			// Determine the number of bars in the current meter
			int numBarsInCurrMeter = 
				(meterInfo.get(i)[Tablature.MI_LAST_BAR] - meterInfo.get(i)[Tablature.MI_FIRST_BAR]) + 1;
			// Determine the metric time of the next meter change point and add it to meterChangePointsMetricTimes
			// NB: When creating the new Rational do not use add() to avoid automatic reduction
			Rational currMeter = metersInLargestDenom.get(i);
			int toAdd = numBarsInCurrMeter * currMeter.getNumer();
			meterChangePointsMetricTimes.add(
				new Rational(meterChangePointsMetricTimes.get(i - startIndex).getNumer() + toAdd, commonDenom));	 	
		}

		// 4. Determine the bar number and the position in the bar, and set metricPos
		// a. If metricTime falls within the anacrusis (the if can only be satisfied if there
		// is an anacrusis)
		if (metricTimeInLargestDenom.getNumer() < meterChangePointsMetricTimes.get(0).getNumer()) {
			// Determine the position in the bar as if it were a full bar 
			Rational lengthAnacrusis = metersInLargestDenom.get(0);
			Rational meterFirstBar = metersInLargestDenom.get(1);
			int toAdd = meterFirstBar.getNumer() - lengthAnacrusis.getNumer();
			Rational posInBar = new Rational(metricTimeInLargestDenom.getNumer() + toAdd, commonDenom);
			posInBar.reduce();
			// Set metricPos; the bar number is 0
			metricPos[0] = new Rational(0, 1);
			metricPos[1] = posInBar;
		}
		// b. If metricTime falls after the anacrusis
		else {
			for (int i = 0; i < meterChangePointsMetricTimes.size() - 1; i++) {
				// Determine the meter change points and bar size (in commonDenom) for the current meter
				Rational currPrev = meterChangePointsMetricTimes.get(i);
				Rational currNext = meterChangePointsMetricTimes.get(i + 1); 
				int currBarSize = metersInLargestDenom.get(i + startIndex).getNumer();

				// If metricTime falls within the current meter change points: determine bar number and position in bar
				if (metricTimeInLargestDenom.isGreaterOrEqual(currPrev) && metricTimeInLargestDenom.isLess(currNext)) {
					// Determine the bar number
					int currDistance = metricTimeInLargestDenom.getNumer() - currPrev.getNumer();
					int numBarsToAdd =	(currDistance - (currDistance % currBarSize)) / currBarSize;   			
					int currBarNum = meterInfo.get(i + startIndex)[Tablature.MI_FIRST_BAR] + numBarsToAdd;
					// Determine the position in the bar
					Rational currPosInBar = new Rational(currDistance % currBarSize, commonDenom);
					currPosInBar.reduce();
					// Set metricPos and break
					metricPos[0] = new Rational(currBarNum, 1);
					metricPos[1] = currPosInBar;
					break;
				}
			}
		}
		return metricPos;
	}


	/**
	 * Gets the diminution for the given metric time.
	 * 
	 * @param mt
	 * @param meterInfo
	 * @return
	 */
	// TESTED
	public static int getDiminution(Rational mt, List<Integer[]> meterInfo) {
		int diminution = 1; 
		// For each meter
		for (int i = 0; i < meterInfo.size(); i++) {
			Integer[] in = meterInfo.get(i);
			// Not last meter: check if mt falls in current meter
			if (i < meterInfo.size() - 1) {
				Rational lower = 
					new Rational(in[Tablature.MI_NUM_MT_FIRST_BAR], in[Tablature.MI_DEN_MT_FIRST_BAR]);
				Rational upper = 
					new Rational(meterInfo.get(i+1)[Tablature.MI_NUM_MT_FIRST_BAR], 
					meterInfo.get(i+1)[Tablature.MI_DEN_MT_FIRST_BAR]);
				if (mt.isGreaterOrEqual(lower) && mt.isLess(upper)) {
					diminution = in[Tablature.MI_DIM];
					break;
				}
			}
			// Last (or only) meter: mt must fall in this meter
			else {
				diminution = in[Tablature.MI_DIM];
			}
		}
		return diminution;
	}


	/**
	 * Returns the given metric position as a String.
	 * 
	 * @param metricPosition
	 * @return
	 */
	public static String getMetricPositionAsString(Rational[] metricPosition) {
		int currentBar = metricPosition[0].getNumer();
		Rational currentPositionInBar = metricPosition[1];
		currentPositionInBar.reduce();
		if (currentPositionInBar.getNumer() != 0) {
			return Integer.toString(currentBar).concat(" ").concat(currentPositionInBar.toString());
		}
		else {
			return Integer.toString(currentBar);
		}
	}


	/**
	 * Splits the given fraction into its unit fractions (from large to small). The fraction must
	 * be a multiple of 1/96 (tablature case) or 1/128 (non-tablature case).
	 *  
	 * @param r
	 * @param mul
	 * @return
	 */
	// TESTED
	static public List<Rational> getUnitFractions(Rational r, Rational mul) {
		List<Rational> uf = new ArrayList<Rational>();
		r.reduce();

		// r must be a multiple of 1/96 or 1/128
		if (r.mul(mul.getDenom()).getDenom() != 1) {
			throw new RuntimeException("ERROR: r must be a multiple of 1/96 (ONLY_TAB case) " + 
				"or 1/128 (ONLY_TRANS/TAB_AND_TRANS cases) but is " + r.toString());
		}
		// If the numerator = 1: add to uf
		int num = r.getNumer();
		int den = r.getDenom();
		if (num == 1 || (num % 2 == 0 && num == num / (double) den)) {
			uf.add(r);
		}
		// If not: split into a fraction of num-1/den and 1/den
		else {
			// Add 1/den to uf
			uf.add(new Rational(1, den));
			// Call method on num-1/den
			uf.addAll(getUnitFractions(new Rational(num-1, den), mul));
		}
		Collections.sort(uf);
		Collections.reverse(uf);

		return uf;
	}


	/**
	 * Returns from the given list of unit fractions the sublists of unit fractions that 
	 * represent dotted notes.
	 * 
	 * Example: when given the list [1/2, 1/4, 1/16, 1/32, 1/64], the method returns the 
	 * sublists [1/2, 1/4] and [1/16, 1/32, 1/64]. 
	 * 
	 * @param uf
	 * @return
	 */
	// TESTED
	public static List<List<Rational>> getUnitFractionSequences(List<Rational> uf) {
		List<List<Rational>> res = new ArrayList<List<Rational>>();
		for (int i = 0; i < uf.size(); i++) {
			List<Rational> temp = new ArrayList<Rational>();
			temp.add(uf.get(i));
			for (int j = i+1; j < uf.size(); j++) {
				Rational curr = uf.get(j);
				if (curr.equals(uf.get(j-1).div(2))) {
					temp.add(curr);
					i = j;
				}
				else { 
					break;
				}
			}
			res.add(temp);
		}
		return res;
	}


	/**
	 * Gets the dot lengthening factor (DLF) for the given number of dots. The DLF is the 
	 * factor that an undotted note's duration is multiplied with to get the duration that 
	 * is added to the original duration by the dots.
	 * 
	 * @param n
	 * @return
	 */
	// TESTED
	public static Rational getDotLengtheningFactor(int n) {
		// The number of dots n lengthens a note by dlf * its undotted duration, 
		// where dlf = ((2^n)-1)/2^n (see https://en.wikipedia.org/wiki/Note_value)
		// Example for double dotted half note: the note is lengthened by ((2^2)-1)/2^2 = 
		// 3/4 * its undotted duration, i.e., it is lengthened by 1/2 * 3/4 = 3/8. 
		return new Rational((int) Math.pow(2, n) - 1, (int) Math.pow(2, n));
	}


	/**
	 * Determines whether the given list of unit fractions represents a dotted note.
	 *  
	 * @param r
	 * @return The number of dots, or, if not applicable, -1.
	 */
	// TESTED
	public static int getNumDots(List<Rational> r) {
		// If r represents a dotted note, n is equal to the number of fractions added to the 
		// base fraction, the first element of r. Thus, if the sum of the remaining elements of 
		// r (the lengthening) equals base * l, the note is dotted, and n is returned 
		int n = r.size()-1; 
		// Do only when there are additional fractions
		if (n > 0) {
			Rational base = r.get(0);
			Rational sumRemaining = ToolBox.sumListRational(r).sub(base);
			Rational l = getDotLengtheningFactor(n);
			if (sumRemaining.equals(base.mul(l))) {
				return n;
			}
		}
		return 0;
	}


	/**
	 * Rounds the given Rational up to the closest value on the grid of the given grid value.
	 * 
	 * @param r
	 * @param grid
	 * @return
	 */
	// TESTED
	public static Rational round(Rational r, Rational grid) {
		Rational rounded;
		for (int i = 0; i < grid.getDenom(); i++) {
			rounded = new Rational(r.getNumer()+i, r.getDenom());
			// r must be a multiple of grid
			if (rounded.mul(grid.getDenom()).getDenom() == 1) {
				rounded.reduce();
				return rounded;
			}
		}
		return null;
	}


	/**
	 * Rounds (up or down) the given Rational to the closest value on the given grid.
	 * 
	 * @param r
	 * @param grid The grid values, including 0. The elements of the list are the grid values' numerators; 
	 *             the length-1 of the list gives the grid values' denominator. 
	 *             E.g., [0, 1, 2, 3, 4] = 0/4, 1/4, ..., 4/4
	 * @return
	 */
	// TESTED
	public static Rational round(Rational r, List<Integer> gridNums) {
		int den = gridNums.size() -1;
		// If r falls on the grid
		if (r.mul(den).getDenom() == 1) {
			return r;
		}
		// If r does not fall on the grid
		else {
			// Separate r into base and fraction part (e.g., 144/96 = 1 48/96)
			int base = r.floor(); 
			Rational frac = new Rational(r.getNumer() - base * r.getDenom(), r.getDenom());
			for (int i = 0; i < gridNums.size()-1; i++) {
				Rational lowerGridVal = new Rational(gridNums.get(i), den);
				Rational upperGridVal = new Rational(gridNums.get(i+1), den);
				// If r falls between two grid values: check to which it is closest. In case
				// of a draw, return the larger grid value
				if (frac.isGreater(lowerGridVal) && frac.isLess(upperGridVal)) {
					double diffLower = Math.abs(frac.sub(lowerGridVal).toDouble());
					double diffUpper = Math.abs(frac.sub(upperGridVal).toDouble());
					if (diffLower < diffUpper) {
						return new Rational(base, 1).add(lowerGridVal);
					}
					else if (diffUpper < diffLower || diffUpper == diffLower) {
						return new Rational(base, 1).add(upperGridVal);
					}
				}
			}
		}
		return null;
	}


	/**
	 * Gets the duration, as a Rational, of the given undotted note when it is dotted with 
	 * the given number of dots.
	 * 
	 * @param undotted
	 * @param dots
	 * @return
	 */
	// TESTED
	public static Rational getDottedNoteLength(Rational undotted, int dots) {
		if (dots == 0) {
			return undotted;
		}
		else {
			// Each dot d adds 1/(2^d) * the undotted length to the undotted note
			// One dot adds 1/(2^1), e.g., H.  = 1/2 + (1/2 * 1/2)
			// Two dots add 1/(2^2), e.g., H.. = 1/2 + (1/2 * 1/2) + (1/2 * 1/4)
			Rational dotted = undotted;
			for (int i = 1; i <= dots; i++) {
				Rational factor = new Rational(1, (int) Math.pow(2, i));
				Rational increment = undotted.mul(factor);
				dotted = dotted.add(increment);
			}
			return dotted;
		}
	}


	/**
	 * Gets the duration, as an int, of the given undotted note when it is dotted with 
	 * the given number of dots.
	 * 
	 * @param undotted
	 * @param dots
	 * @return
	 */
	// TESTED
	public static int getDottedNoteLength(int undotted, int dots) {
		if (dots == 0) {
			return undotted;
		}
		else {
			// Each dot d adds 1/(2^d) * the undotted length to the undotted note
			// One dot adds 1/(2^1), e.g., H.  = 1 * H + (1/2 * H)             = 3/2 * H
			// Two dots add 1/(2^2), e.g., H.. = 1 * H + (1/2 * H) + (1/4 * H) = 7/4 * H 
			// The dotted length is the undotted length multiplied by this factor
			double factor = 1;
			for (int i = 1; i <= dots; i++) {
				factor += 1 / Math.pow(2, i);
			}
			return (int) (undotted * factor);
		}
	}


	/**
	 * Gets the duration, as an int, of the given dotted note when it is undotted with 
	 * the given number of dots.
	 * 
	 * @param dotted
	 * @param dots
	 * @return
	 */
	// TESTED
	public static int getUndottedNoteLength(int dotted, int dots) {
		if (dots == 0) {
			return dotted;
		}
		else {
			// Each dot d adds 1/(2^d) * the undotted length to the undotted note
			// One dot adds 1/(2^1), e.g., H.  = 1 * H + (1/2 * H)             = 3/2 * H
			// Two dots add 1/(2^2), e.g., H.. = 1 * H + (1/2 * H) + (1/4 * H) = 7/4 * H
			// The undotted length is the dotted length divided by this factor  
			double factor = 1;
			for (int i = 1; i <= dots; i++) {
				factor += 1 / Math.pow(2, i);
			}
			return (int) (dotted / factor);
		}
	}


	/**
	 * Determines whether the given onset is a tripletOnset, i.e., falls within a triplet.
	 * This is the case when, for a tripletOnsetPair in the given list, the given onset is
	 * >= element 0 (the onset of the opening triplet note) and <= element 1 (the onset of 
	 * the closing triplet note) in the tripletOnsetPair.
	 * 
	 * @param tripletOnsetPairs
	 * @param onset
	 * @return A List specifiying whether the onset is a tripletOpen, tripletMid, or tripletClose
	 *         onset.
	 */
	// TESTED
	public static List<Boolean> isTripletOnset(List<Rational[]> tripletOnsetPairs, Rational onset) {		
		boolean tripletOpen = false, tripletMid = false, tripletClose = false;
		for (Rational[] r : tripletOnsetPairs) {
			if (onset.equals(r[0])) {
				tripletOpen = true;
				break;
			}
			if (onset.isGreater(r[0]) && onset.isLess(r[1])) {
				tripletMid = true;
				break;
			}
			if (onset.equals(r[1])) {
				tripletClose = true;
				break;
			}
		}
		return Arrays.asList(new Boolean[]{tripletOpen, tripletMid, tripletClose}); 
	}


	/**
	 * Returns the tripletOnsetPair at the given onset, with the triplet length added.
	 * 
	 * @param onset
	 * @param tripletOnsetPairs
	 * @param mi
	 * @return If <code>onset</code> falls within the triplet, a Rational[] containing
	 * 		   <ul>
	 *         <li>as element 0: the tripletOpen onset</li>
	 *         <li>as element 1: the tripletClose onset</li>
	 *         <li>as element 2: the tripletUnit</li>
	 *         <li>as element 3: the tripletLen</li>
	 *         </ul>
	 *         else, <code>null</code>.
	 */
	// TESTED
	public static Rational[] getExtendedTripletOnsetPair(Rational onset, 
		List<Rational[]> tripletOnsetPairs/*, int diminution*/) {

		Rational[] pairAndLen = null;
		for (Rational[] r : tripletOnsetPairs) {
			Rational tripletOpen = r[0];
			// Calculate tripletLen
			// 1. Undiminished, i.e., length (in multiples of Tablature.SRV) * Tablature.SRV 
			// * 2 (there are three notes of this unit in the time of two)
			// Example for a mi (length = 24): (24 * 1/96) * 2 = 1/2
			Rational currTripletLenUndim = r[2].mul(Tablature.SMALLEST_RHYTHMIC_VALUE).mul(2);
			// Diminution removed on 27.02.2024 because the given tripletOnsetPairs and onset 
			// are already diminuted
//			// 2. Diminished
//			Rational currTripletLen = 
//				diminution > 0 ? currTripletLenUndim.mul(diminution) :
//				currTripletLenUndim.div(Math.abs(diminution));
			Rational currTripletLen = currTripletLenUndim;

			// If onset falls within the triplet time
			if (onset.isGreaterOrEqual(tripletOpen) && onset.isLess(tripletOpen.add(currTripletLen))) {
				pairAndLen = new Rational[4];
				for (int i = 0; i < r.length; i++) {
					pairAndLen[i] = r[i];
				}
				pairAndLen[3] = currTripletLen;
				break;
			}
		}
		return pairAndLen;
	}


	/**
	 * Gets the final offset, i.e., the offset of the final bar, based on the given meterInfo. 
	 * 
	 * @param mi
	 */
	// TESTED
	public static Rational getFinalOffset(List<Integer[]> mi) {
		Rational finalOffset = Rational.ZERO;
		for (Integer[] m : mi) {
			Rational currMeter = new Rational(m[Transcription.MI_NUM], m[Transcription.MI_DEN]);
			int barsInCurrMeter = (m[Transcription.MI_LAST_BAR] - m[Transcription.MI_FIRST_BAR]) + 1;
			finalOffset = finalOffset.add(currMeter.mul(barsInCurrMeter));
		}
		return finalOffset;
	}

}
