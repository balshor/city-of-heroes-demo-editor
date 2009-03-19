package cohdemoeditor;

import java.util.Comparator;

import javax.swing.DefaultListModel;

/**
 * This class defines the required ordering for a DemoCommandList to be exported
 * in a useable way to a .cohdemo file. Note that this is not a total
 * ordering, and so it will be inconsistent with equals. It is intended for use
 * by a stable sort algorithm to resort a DemoCommandList.
 * 
 * @author Darren Lee
 * 
 */
@SuppressWarnings("serial")
public class DemoCommandComparator extends DefaultListModel implements
		Comparator<DemoCommand> {

	private final static String[] DEFAULT_COMMAND_ORDER = { "Version", "Map",
			"Time", "SKY", "Player", "NEW", "NPC", "COSTUME", "PARTSNAME",
			"FX", "FXSCALE", "ORIGIN", "TARGET", "HPMAX", "HP", "Chat",
			"floatdmg", "float", "POS", "PYR", "MOV", "EntRagdoll",
			"FXDESTROY", "DEL" };

	public DemoCommandComparator() {
		for (String str : DEFAULT_COMMAND_ORDER) {
			addElement(str);
		}
	}

	/**
	 * The ordering is as follows:
	 * <ol>
	 * <li>commands are ordered according to time</li>
	 * <li>commands are ordered according to reference number</li>
	 * <li>commands are ordered according to command (by the order in this
	 * DemoCommandComparator, then all other commands)</li>
	 * </ol>
	 * Note that this is not a total ordering, and so it will be inconsistent
	 * with equals. It is intended for use by a stable sort algorithm to resort
	 * a DemoCommandList.
	 */
	@Override
	public int compare(DemoCommand o1, DemoCommand o2) {
		if (o1 == null || o2 == null)
			throw new IllegalArgumentException(
					"Cannot compare a DemoCommand to null.");
		if (o1 == o2)
			return 0;
		if (o1.getTime() != o2.getTime()) {
			return o1.getTime() - o2.getTime();
		}
		final int ref1 = o1.getReference();
		final int ref2 = o2.getReference();
		if (ref1 != ref2) {
			if (ref1 == 0) return -1;
			if (ref2 == 0) return 1;
			if (ref1*ref2 < 0) {
				if (ref1 < 0) return -1;
				return 1;
			}
			return ref1-ref2;
		}
		if (!o1.getCommand().equals(o2.getCommand())) {
			final int index1 = indexOf(o1.getCommand());
			final int index2 = indexOf(o2.getCommand());
			if (index1 != -1 && index2 != -1) {
				return index1 - index2;
			} else {
				if (index1 == -1 && index2 != -1) {
					return 1;
				} else if (index1 != -1 && index2 == -1) {
					return -1;
				}
			}
		}
		return 0;
	}

}
