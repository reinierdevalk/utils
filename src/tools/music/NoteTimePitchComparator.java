package tools.music;

import java.io.Serializable;
import java.util.Comparator;

//import de.uos.fmt.musitech.data.performance.PerformanceNote;
//import de.uos.fmt.musitech.data.structure.Note;
//import de.uos.fmt.musitech.utility.math.Rational;

/**
 * Class for comparison of Note objects by pitch.
 * 
 * @author Reinier de Valk, based on Aline Honingh's and Tillman Weyde's code. 
 */
//public class NoteTimePitchComparator implements Comparator<Note>, Serializable {
//
//	private static final long serialVersionUID = 1L;
//
//
//	/**
//	 * Compares two notes.
//	 * 
//	 * @param note1 the first note
//	 * @param note2 the second note
//	 * @return A negative number, zero, or a positive number depending on whether the 
//	 *         first note is less, equal or greater than the second note. 
//	 *         Less/greater than is defined by:<br>
//	 *        <ul>
//	 *        <li>1. Onset time (metrical time)</li>
//	 *        <li>2. If onset time is equal, pitch</li>
//	 *        </ul>
//	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
//	 */
//	@Override
//	public int compare(Note note1, Note note2) {
//		Rational t1 = note1.getMetricTime();
//		Rational t2 = note2.getMetricTime();
//
//		if (t1 == null && t2 != null) {
//			return -1;
//		} 
//		else if (t1 != null && t2 == null) {
//			return 1;
//		}
//		else if (t1 != null && t2 != null) {
//			int c = t1.compare(t2);
//			if (c != 0 ) {
//				return c;
//			}
//		}
//
//		PerformanceNote performancePartOfNote1 = note1.getPerformanceNote();
//		int pitch1 = performancePartOfNote1.getPitch();
//		PerformanceNote performancePartOfNote2 = note2.getPerformanceNote();
//		int pitch2 = performancePartOfNote2.getPitch();
//
//		int output = pitch1 - pitch2;
//		if (output != 0) {
//			return output;
//		}
//		if (note1 == note2) {
//			return 0;
//		}
//
//		int hashDiff = note1.hashCode() - note2.hashCode();
//		if (hashDiff != 0) {
//			return hashDiff;
//		}
//		return -1;
//	}
//
//}
