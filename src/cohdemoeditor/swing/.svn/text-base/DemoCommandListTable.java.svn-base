/*
 * CommandListPanel.java
 *
 * Created on June 13, 2005, 9:33 AM
 */

package cohdemoeditor.swing;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import cohdemoeditor.DemoCommandList;
import cohdemoeditor.DemoCommandListFilter;
import cohdemoeditor.ShowsAbsoluteTimesListener;

/**
 * A modified JTable used to display a <CODE>DemoCommandList</CODE>. Implements
 * a couple of methods to make working with <code>DemoCommandList</code>s
 * easier. This table is intended to be placed inside of a DemoCommandListEditor
 * 
 * @author Darren Lee
 */
@SuppressWarnings("serial")
public class DemoCommandListTable extends javax.swing.JTable implements
		ShowsAbsoluteTimesListener {

	private DemoCommandList cmdList = null;
	private JPopupMenu timeMenu;
	private JRadioButtonMenuItem absTimeBtn, relTimeBtn;

	/**
	 * Creates a new <CODE>CommandListPanel</CODE>
	 */
	public DemoCommandListTable(DemoCommandList list) {
		super();
		if (list == null)
			throw new NullPointerException(
					"Cannot open a DemoCommandListTable with a null DemoCommandList.");
		cmdList = list;
		cmdList.addShowsAbsoluteTimesListener(this);
		setModel(cmdList);
		getSelectionModel().setSelectionMode(
				ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		timeMenu = new JPopupMenu();
		ButtonGroup bg = new ButtonGroup();
		absTimeBtn = new JRadioButtonMenuItem("Show Absolute Times");
		relTimeBtn = new JRadioButtonMenuItem("Show Relative Times");
		absTimeBtn.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				int index = getSelectedRow();
				cmdList.setShowsAbsoluteTimes(absTimeBtn.isSelected());
				getSelectionModel().setSelectionInterval(index, index);
			}
		});
		bg.add(absTimeBtn);
		bg.add(relTimeBtn);
		if (list.getShowsAbsoluteTimes()) {
			absTimeBtn.setSelected(true);
		} else {
			relTimeBtn.setSelected(true);
		}
		timeMenu.add(absTimeBtn);
		timeMenu.add(relTimeBtn);
		timeMenu.pack();
		getTableHeader().addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent e) {
				if (e.getButton() != MouseEvent.BUTTON3)
					return;
				int col = getTableHeader().columnAtPoint(
						new Point(e.getPoint()));
				if (col == DemoCommandListFilter.TIME_COL) {
					timeMenu.show(e.getComponent(), e.getX(), e.getY());
				}
			}
		});
	}

	/**
	 * The table will either show absolute times or relative times. This
	 * listener method will tell it which mode it is in.
	 */
	public void showsAbsoluteTimesChanged(DemoCommandList source,
			boolean newValue) {
		if (cmdList != source)
			return;
		absTimeBtn.setSelected(newValue);
		tableChanged(new TableModelEvent(source, TableModelEvent.HEADER_ROW));
	}

	/**
	 * basically, just a wrapper for <code>(DemoCommandList) getModel()</code>
	 * 
	 * @return the <code>DemoCommandList</code> that this
	 *         <code>CommandListPanel</code> is currently displaying
	 */
	public DemoCommandList getDemoCommandList() {
		return cmdList;
	}

}