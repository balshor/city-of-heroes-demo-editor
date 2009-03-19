/*
 * DemoCommandListFilter.java
 *
 * Created on June 11, 2005, 11:07 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package cohdemoeditor;

import java.util.*;

/**
 * A DemoCommandListFilter is used to choose which DemoCommands are visible in a
 * DemoCommandList. Multiple filters are grouped into a FilterList. Filters can
 * set time ranges, lists of references, commands, or arguments; depending on
 * the filter's mode (SHOW_THESE vs HIDE_THESE), either this set of commands or
 * its complement will be set as invisible.
 * 
 * In each of the four categories (time, ref, cmd, arg), multiple criteria are
 * considered to be ORed together. Thus, a DCLF is a disjunction in each
 * category, and a FilterList is a conjunction of disjunctions.
 * 
 * Time ranges are considered to be inclusive on both ends.
 * 
 * @author Darren Lee
 */
public class DemoCommandListFilter implements Cloneable {

	public static final int TIME_COL = 0;
	public static final int REF_COL = 1;
	public static final int CMD_COL = 2;
	public static final int ARG_COL = 3;
	public static final String[] COL_NAMES = { "Time", "Reference", "Command",
			"Arguments" };
	public static final int NUM_COLS = COL_NAMES.length;

	private SortedSet<TimeRange> times = null;
	private SortedSet<Integer> objects = null;
	private SortedSet<String> commands = null;
	private SortedSet<String> arguments = null;
	private boolean type;

	public static final boolean SHOW_THESE = false;
	public static final boolean HIDE_THESE = true;

	/**
	 * Creates a new instance of DemoCommandListFilter with the specified mode.
	 * 
	 * @param doesFilterOut
	 *            either SHOW_THESE or HIDE_THESE
	 */
	public DemoCommandListFilter(boolean doesFilterOut) {
		this.type = doesFilterOut;
	}

	/**
	 * Creates a SHOW_THESE DemoCommandListFilter
	 */
	public DemoCommandListFilter() {
		this(SHOW_THESE);
	}

	/**
	 * Produces a shallow copy of this DemoCommandListFilter.
	 */
	public DemoCommandListFilter clone() {
		DemoCommandListFilter copy = new DemoCommandListFilter(type);
		if (times != null)
			copy.times = new TreeSet<TimeRange>(times);
		if (objects != null)
			copy.objects = new TreeSet<Integer>(objects);
		if (commands != null)
			copy.commands = new TreeSet<String>(commands);
		if (arguments != null)
			copy.arguments = new TreeSet<String>(arguments);
		return copy;
	}

	/**
	 * Creates a DemoCommandListFilter for the specified command to either show
	 * only this command or to hide this command.
	 * 
	 * @param dcmd
	 * @param doesFilterOut
	 * @return
	 */
	public static DemoCommandListFilter getFilterForCmd(DemoCommand dcmd,
			boolean doesFilterOut) {
		DemoCommandListFilter filter = new DemoCommandListFilter(doesFilterOut);
		filter.addTimeRange(dcmd.getTime(), dcmd.getTime());
		filter.addReference(dcmd.getReference());
		filter.addCommand(dcmd.getCommand());
		int numArgs = dcmd.getArgumentCount();
		for (int i = 0; i < numArgs; i++) {
			filter.addArgument(dcmd.getArgument(i));
		}
		return filter;
	}

	/**
	 * Determines if the filter has any criteria set.
	 * 
	 * @return
	 */
	public boolean isEmpty() {
		return ((times == null || times.size() == 0)
				&& (objects == null || objects.size() == 0)
				&& (commands == null || commands.size() == 0) && (arguments == null || arguments
				.size() == 0));
	}

	/**
	 * Returns the type of the filter.
	 * 
	 * @return
	 */
	public boolean doesFilterOut() {
		return type;
	}

	/**
	 * Sets the type of the filter.
	 * 
	 * @param b
	 */
	public void setFiltersOut(boolean b) {
		type = b;
	}

	/**
	 * Adds a new time range.
	 * 
	 * @param start
	 * @param end
	 */
	public void addTimeRange(int start, int end) {
		if (times == null)
			times = new TreeSet<TimeRange>();
		times.add(new TimeRange(start, end));
	}

	/**
	 * Returns a string representation of the time ranges. Used for display in a
	 * table.
	 * 
	 * @return
	 */
	public String getTimeRanges() {
		if (times == null)
			return "";
		String s = "";
		for (TimeRange tr : times)
			s += " " + tr.toString();
		return s.trim();
	}

	/**
	 * Returns a list of the time ranges.
	 * 
	 * @return
	 */
	public List<TimeRange> getTimeRangeList() {
		if (times == null)
			return new ArrayList<TimeRange>(1);
		return new ArrayList<TimeRange>(times);
	}

	/**
	 * Adds a new reference critera to the filter.
	 * 
	 * @param objNum
	 */
	public void addReference(int objNum) {
		if (objects == null)
			objects = new TreeSet<Integer>();
		objects.add(objNum);
	}

	/**
	 * Returns a String representation of the reference criteria. Used for
	 * display purposes.
	 * 
	 * @return
	 */
	public String getReferences() {
		if (objects == null)
			return "";
		String s = "";
		for (Integer i : objects)
			s += " " + DemoCommand.getIDFor(i);
		return s.trim();
	}

	/**
	 * Returns a List of the reference criteria.
	 * 
	 * @return
	 */
	public List<Integer> getReferenceList() {
		if (objects == null)
			return new ArrayList<Integer>(1);
		return new ArrayList<Integer>(objects);
	}

	/**
	 * Adds a new command criteria to this filter.
	 * 
	 * @param cmd
	 */
	public void addCommand(String cmd) {
		if (commands == null)
			commands = new TreeSet<String>();
		commands.add(cmd);
	}

	/**
	 * Returns a string representation of the command criteria. Used for display
	 * purposes.
	 * 
	 * @return
	 */
	public String getCommands() {
		if (commands == null)
			return "";
		String s = "";
		for (String cmd : commands)
			s += " " + cmd;
		return s.trim();
	}

	/**
	 * Returns a List of the command criteria.
	 * 
	 * @return
	 */
	public List<String> getCommandList() {
		if (commands == null)
			return new ArrayList<String>(1);
		return new ArrayList<String>(commands);
	}

	/**
	 * Adds a new argument criteria
	 * 
	 * @param arg
	 */
	public void addArgument(String arg) {
		if (arguments == null)
			arguments = new TreeSet<String>();
		if (arg.startsWith("\"") && arg.endsWith("\""))
			arg = arg.substring(1, arg.length() - 1);
		arguments.add(arg);
	}

	/**
	 * Gets a String representation of the argument critera.
	 * 
	 * @return
	 */
	public String getArguments() {
		if (arguments == null)
			return "";
		String s = "";
		for (String arg : arguments) {
			if (arg.contains(" "))
				s += " \"" + arg + "\"";
			else
				s += " " + arg;
		}
		return s.trim();
	}

	/**
	 * Gets a List of the argument criteria.
	 * 
	 * @return
	 */
	public List<String> getArgumentList() {
		if (arguments == null)
			return new ArrayList<String>(1);
		return new ArrayList<String>(arguments);
	}

	/**
	 * Determines if the given command is visible under this filter
	 * 
	 * @param cmd
	 * @return
	 */
	public boolean isVisible(DemoCommand cmd) {
		boolean t = (times == null);
		if (times != null) {
			for (TimeRange tr : times) {
				t = (tr.start <= cmd.getTime() && cmd.getTime() <= tr.end);
				if (t)
					break;
			}
		}
		boolean o = (objects == null);
		if (objects != null) {
			for (int i : objects) {
				o = (cmd.getReference() == i);
				if (o)
					break;
			}
		}
		boolean c = (commands == null);
		if (commands != null) {
			for (String s : commands) {
				c = (s.equals(cmd.getCommand()));
				if (c)
					break;
			}
		}
		boolean a = (arguments == null);
		if (arguments != null) {
			for (String s : arguments) {
				a = cmd.hasArg(s);
				if (a)
					break;
			}
		}
		return ((t && o && c && a) ^ type);
	}

	/**
	 * A TimeRange is simply a pair of integers. This class does not do any
	 * checking to ensure start < end. TimeRanges are sorted by their start
	 * times.
	 * 
	 * @author Darren Lee
	 * 
	 */
	public static class TimeRange implements Comparable<TimeRange> {
		public final int start, end;

		/**
		 * Create a new TimeRange
		 * 
		 * @param start
		 * @param end
		 */
		private TimeRange(int start, int end) {
			this.start = start;
			this.end = end;
		}

		/**
		 * Get a String representation of this TimeRange. By default,
		 * "start-end".
		 */
		public String toString() {
			return "" + start + "-" + end;
		}

		/**
		 * Compares two TimeRanges by their start times.
		 */
		public int compareTo(TimeRange tr) {
			return (new Integer(start)).compareTo(tr.start);
		}
	}

}
