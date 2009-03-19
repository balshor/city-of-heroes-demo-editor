package cohdemoeditor.swing;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Calendar;
import java.util.List;

import javax.swing.*;

/**
 * <p>
 * This class extends SwingWorker to display a modal progress dialog of the
 * given task, a la ProgressMonitor.
 * </p>
 * 
 * @author Darren Lee
 * @param <T>
 *            the type returned by the doInBackground and get methods
 * 
 */
public abstract class ProgressMonitoringSwingWorker<T> extends
		SwingWorker<T, String> {

	protected final JCenteringDialog progressDialog;
	protected final JProgressBar progressBar;
	protected final JLabel messageLabel;
	protected final JButton cancelButton;

	private final static String DEFAULT_MESSAGE = "Working...";
	private final static String DEFAULT_TITLE = "Progress";
	private final static String CANCEL_BUTTON_NAME = "Cancel";
	private final static String CANCEL_BUTTON_DESCRIPTION = "Cancels the task.";
	private long startTime;
	
	/**
	 * Creates a new ProgressMonitoringSwingWorker with the given JDialog as a
	 * parent for the progress dialog.
	 * 
	 * @param parent
	 */
	public ProgressMonitoringSwingWorker(JFrame parent) {
		progressDialog = new JCenteringDialog(parent, true);
		progressBar = new JProgressBar();
		messageLabel = new JLabel(DEFAULT_MESSAGE);
		cancelButton = new JButton();
		initialize();
	}

	/**
	 * Creates a new ProgressMonitoringSwingWorker with the given JDialog as a
	 * parent for the progress dialog.
	 * 
	 * @param parent
	 */
	public ProgressMonitoringSwingWorker(JDialog parent) {
		progressDialog = new JCenteringDialog(parent, true);
		progressBar = new JProgressBar();
		messageLabel = new JLabel(DEFAULT_MESSAGE);
		cancelButton = new JButton();
		initialize();
	}

	/**
	 * Initializes the dialog components. Adds a property change listener to
	 * show or hide the progress dialog as the worker starts and finishes.
	 */
	@SuppressWarnings("serial")
	protected void initialize() {
		progressDialog.setTitle(DEFAULT_TITLE);
		progressDialog.setLayout(new BorderLayout());
		progressDialog.add(progressBar, BorderLayout.CENTER);
		progressDialog.add(messageLabel, BorderLayout.NORTH);
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout());
		panel.add(cancelButton);
		progressDialog.add(panel, BorderLayout.SOUTH);

		final Action cancelAction = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ProgressMonitoringSwingWorker.this.cancel(true);
			}
		};
		cancelAction.putValue(Action.NAME, CANCEL_BUTTON_NAME);
		cancelAction.putValue(Action.SHORT_DESCRIPTION,
				CANCEL_BUTTON_DESCRIPTION);
		cancelButton.setAction(cancelAction);
		progressDialog.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
		progressDialog.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				ProgressMonitoringSwingWorker.this.cancel(true);
			}
		});

		progressDialog.pack();

		addPropertyChangeListener(new PropertyChangeListener() {

			/*
			 * Shows the dialog based on the worker's status. Updates the
			 * progress bar on progress updates.
			 */
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				final String propertyName = evt.getPropertyName();
				if (progressDialog.isVisible()) {
					final long now = Calendar.getInstance().getTimeInMillis();
					if (now - startTime > 250) {
						progressDialog.setVisible(true);
					}
				}
				if ("progress".equals(propertyName)) {
					try {
						final int newProgress = (Integer) evt.getNewValue();
						if (progressDialog.isVisible() && newProgress != progressBar.getValue()) {
							progressBar.setValue(newProgress);
						}
					} catch (Exception e) {
						// ignore
					}
				}
			}

		});
		startTime = Calendar.getInstance().getTimeInMillis();
	}

	/**
	 * Sets the progress dialog's title.
	 */
	public void setTitle(String title) {
		progressDialog.setTitle(title);
	}

	/**
	 * This method directly sets the message label's text.
	 * 
	 * @param message
	 *            the text to set
	 */
	public void setCurrentStatusMessage(String message) {
		messageLabel.setText(message);
	}

	/**
	 * Processes a List of Strings. The last non-null String will be used to
	 * update the message label. Does nothing if all Strings are null.
	 */
	@Override
	protected void process(List<String> chunks) {
		for (int i = chunks.size() - 1; i >= 0; i--) {
			final String str = chunks.get(i);
			if (str != null) {
				messageLabel.setText(str);
				return;
			}
		}
	}

	/**
	 * Cleans up the ProgressMonitoringSwingWorker on completion.
	 */
	@Override
	protected void done() {
		progressDialog.setVisible(false);
		progressDialog.dispose();
	}
}
