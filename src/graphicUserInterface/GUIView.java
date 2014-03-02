/*
 * GUIView.java
 */
package graphicUserInterface;

import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JTextArea;

import org.jdesktop.application.FrameView;
import org.jdesktop.application.SingleFrameApplication;

import fuzzyRDFSEngine.FuzzySystem;
import tnorms.GenericTnormFunctor;

/**
 * The application's main frame.
 */
public class GUIView extends FrameView {

	FuzzySystem fuzzySystem;

	public GUIView(SingleFrameApplication app) {
		super(app);
		initComponents();
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
	// <editor-fold defaultstate="collapsed" desc="Generated
	// <editor-fold defaultstate="collapsed"
	// desc="Generated Code">//GEN-BEGIN:initComponents
	private void initComponents() {

		mainPanel = new javax.swing.JPanel();
		jPanel1 = new javax.swing.JPanel();
		setStorageOption = new javax.swing.JButton();
		clearButton = new javax.swing.JButton();
		addGraphButton = new javax.swing.JButton();
		msgLabel = new javax.swing.JLabel();
		showModelContent = new javax.swing.JButton();
		queryDialog = new javax.swing.JButton();
		tnormChooserComboBox = new javax.swing.JComboBox();
		exitButton = new javax.swing.JButton();
		jPanel2 = new javax.swing.JPanel();
		jScrollPane1 = new javax.swing.JScrollPane();
		textArea = new javax.swing.JTextArea();
		jPanel3 = new javax.swing.JPanel();

		mainPanel.setName("mainPanel"); // NOI18N

		jPanel1.setName("jPanel1"); // NOI18N

		org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application
				.getInstance(graphicUserInterface.GUIMain.class).getContext()
				.getResourceMap(GUIView.class);
		setStorageOption
				.setText(resourceMap.getString("setStorageOption.text")); // NOI18N
		setStorageOption.setName("setStorageOption"); // NOI18N
		setStorageOption.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				setStorageOptionMouseClicked(evt);
			}
		});

		clearButton.setText(resourceMap.getString("clearButton.text")); // NOI18N
		clearButton.setActionCommand(resourceMap
				.getString("clearButton.actionCommand")); // NOI18N
		clearButton.setName("clearButton"); // NOI18N
		clearButton.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				clearButtonMouseClicked(evt);
			}
		});

		addGraphButton.setText(resourceMap.getString("addGraphButton.text")); // NOI18N
		addGraphButton.setName("addGraphButton"); // NOI18N
		addGraphButton.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				addGraphButtonMouseClicked(evt);
			}
		});

		msgLabel.setText(resourceMap.getString("msgLabel.text")); // NOI18N
		msgLabel.setName("msgLabel"); // NOI18N

		showModelContent
				.setText(resourceMap.getString("showModelContent.text")); // NOI18N
		showModelContent.setName("showModelContent"); // NOI18N
		showModelContent.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				showModelContentMouseClicked(evt);
			}
		});

		queryDialog.setText(resourceMap.getString("queryDialog.text")); // NOI18N
		queryDialog.setName("queryDialog"); // NOI18N
		queryDialog.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				queryDialogMouseClicked(evt);
			}
		});

		tnormChooserComboBox.setModel(new javax.swing.DefaultComboBoxModel(
				new String[] { "goedelTnorm", "productTnorm",
						"lucasiewiczTnorm", "drasticTnorm",
						"nilpotentMinimumTnorm", "hamacherProductTnorm" }));
		tnormChooserComboBox.setName("tnormChooserComboBox"); // NOI18N
		tnormChooserComboBox.addItemListener(new java.awt.event.ItemListener() {
			public void itemStateChanged(java.awt.event.ItemEvent evt) {
				tnormChooserComboBoxItemStateChanged(evt);
			}
		});

		exitButton.setText(resourceMap.getString("exitButton.text")); // NOI18N
		exitButton.setName("exitButton"); // NOI18N
		exitButton.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				exitButtonMouseClicked(evt);
			}
		});

		javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(
				jPanel1);
		jPanel1.setLayout(jPanel1Layout);
		jPanel1Layout
				.setHorizontalGroup(jPanel1Layout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								jPanel1Layout
										.createSequentialGroup()
										.addGap(381, 381, 381)
										.addComponent(exitButton)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(
												msgLabel,
												javax.swing.GroupLayout.DEFAULT_SIZE,
												425, Short.MAX_VALUE))
						.addGroup(
								jPanel1Layout
										.createSequentialGroup()
										.addContainerGap()
										.addComponent(clearButton)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
										.addComponent(addGraphButton)
										.addGap(18, 18, 18)
										.addComponent(setStorageOption)
										.addGap(18, 18, 18)
										.addComponent(showModelContent)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
										.addComponent(queryDialog)
										.addContainerGap(116, Short.MAX_VALUE))
						.addGroup(
								jPanel1Layout
										.createSequentialGroup()
										.addContainerGap()
										.addComponent(
												tnormChooserComboBox,
												javax.swing.GroupLayout.PREFERRED_SIZE,
												javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addContainerGap(648, Short.MAX_VALUE)));
		jPanel1Layout
				.setVerticalGroup(jPanel1Layout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								jPanel1Layout
										.createSequentialGroup()
										.addComponent(
												tnormChooserComboBox,
												javax.swing.GroupLayout.PREFERRED_SIZE,
												javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addGap(153, 153, 153)
										.addGroup(
												jPanel1Layout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.BASELINE)
														.addComponent(
																clearButton)
														.addComponent(
																addGraphButton)
														.addComponent(
																setStorageOption)
														.addComponent(
																showModelContent)
														.addComponent(
																queryDialog))
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addGroup(
												jPanel1Layout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.TRAILING)
														.addComponent(
																msgLabel,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																42,
																Short.MAX_VALUE)
														.addComponent(
																exitButton))
										.addContainerGap()));

		jPanel2.setName("jPanel2"); // NOI18N

		javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(
				jPanel2);
		jPanel2.setLayout(jPanel2Layout);
		jPanel2Layout.setHorizontalGroup(jPanel2Layout.createParallelGroup(
				javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 853,
				Short.MAX_VALUE));
		jPanel2Layout.setVerticalGroup(jPanel2Layout.createParallelGroup(
				javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 30,
				Short.MAX_VALUE));

		jScrollPane1.setName("jScrollPane1"); // NOI18N

		textArea.setColumns(20);
		textArea.setRows(5);
		textArea.setName("textArea"); // NOI18N
		jScrollPane1.setViewportView(textArea);

		jPanel3.setName("jPanel3"); // NOI18N

		javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(
				jPanel3);
		jPanel3.setLayout(jPanel3Layout);
		jPanel3Layout.setHorizontalGroup(jPanel3Layout.createParallelGroup(
				javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 841,
				Short.MAX_VALUE));
		jPanel3Layout.setVerticalGroup(jPanel3Layout.createParallelGroup(
				javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 46,
				Short.MAX_VALUE));

		javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(
				mainPanel);
		mainPanel.setLayout(mainPanelLayout);
		mainPanelLayout.setHorizontalGroup(mainPanelLayout.createParallelGroup(
				javax.swing.GroupLayout.Alignment.LEADING).addComponent(
				jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE,
				javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
				.addComponent(jScrollPane1,
						javax.swing.GroupLayout.DEFAULT_SIZE, 853,
						Short.MAX_VALUE).addGroup(
						mainPanelLayout.createSequentialGroup().addComponent(
								jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE,
								javax.swing.GroupLayout.DEFAULT_SIZE,
								Short.MAX_VALUE).addContainerGap())
				.addComponent(jPanel1,
						javax.swing.GroupLayout.Alignment.TRAILING,
						javax.swing.GroupLayout.DEFAULT_SIZE,
						javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));
		mainPanelLayout
				.setVerticalGroup(mainPanelLayout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								mainPanelLayout
										.createSequentialGroup()
										.addComponent(
												jPanel3,
												javax.swing.GroupLayout.PREFERRED_SIZE,
												javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(
												jPanel1,
												javax.swing.GroupLayout.PREFERRED_SIZE,
												javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(
												jScrollPane1,
												javax.swing.GroupLayout.PREFERRED_SIZE,
												298,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED,
												12, Short.MAX_VALUE)
										.addComponent(
												jPanel2,
												javax.swing.GroupLayout.PREFERRED_SIZE,
												javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.PREFERRED_SIZE)));

		setComponent(mainPanel);
	}// </editor-fold>//GEN-END:initComponents

	private void tnormChooserComboBoxItemStateChanged(
			java.awt.event.ItemEvent evt) {// GEN-FIRST:event_tnormChooserComboBoxItemStateChanged
		if (fuzzySystem == null) {
			msgLabel.setForeground(java.awt.Color.RED);
			msgLabel.setText("set a storage option first");
			return;
		}
		int selected = tnormChooserComboBox.getSelectedIndex();
		fuzzySystem
				.setTnorm(GenericTnormFunctor.predefinedTnormArray[selected]);
		textArea.setText(tnormChooserComboBox.getSelectedItem()
				+ " set correctly");
	}// GEN-LAST:event_tnormChooserComboBoxItemStateChanged

	private void exitButtonMouseClicked(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_exitButtonMouseClicked
		try {
			if (fuzzySystem != null) {
				fuzzySystem.close();
			}
		} catch (SQLException ex) {
			Logger.getLogger(GUIView.class.getName()).log(Level.SEVERE, null,
					ex);
		}
		System.exit(0);
	}// GEN-LAST:event_exitButtonMouseClicked

	public static void showException(Exception e, JTextArea textArea) {
		textArea.setText(null);
		textArea.setForeground(java.awt.Color.GREEN);
		textArea.append("\n" + e.getLocalizedMessage() + "\n");
		for (StackTraceElement el : e.getStackTrace()) {
			textArea.append("\t at " + el.getMethodName() + "("
					+ el.getFileName() + ":" + el.getLineNumber() + ")\n");
		}
	}

	private void clearButtonMouseClicked(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_clearButtonMouseClicked
		try {
			synchronized (this) {
				// msgLabel.setForeground(java.awt.Color.BLACK);
				msgLabel.setText(null);
				// msgLabel.setText("clear button muose clicked");
				if (fuzzySystem == null) {
					msgLabel.setForeground(java.awt.Color.RED);
					msgLabel.setText("set a storage option first");
					return;
				}
				fuzzySystem.removeAll();
				textArea.setText("removed all statements");
			}
		} catch (Exception e) {
			try {
				GUIView.showException(e, textArea);
			} catch (Exception ex) {
				Logger.getLogger(GUIView.class.getName()).log(Level.SEVERE,
						null, ex);
			}
		}
	}// GEN-LAST:event_clearButtonMouseClicked

	private void addGraphButtonMouseClicked(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_addGraphButtonMouseClicked
		synchronized (this) {
			// msgLabel.setForeground(java.awt.Color.BLACK);
			msgLabel.setText(null);
			// msgLabel.setText("add graph button muose clicked");
			if (fuzzySystem == null) {
				msgLabel.setForeground(java.awt.Color.RED);
				msgLabel.setText("set a storage option first");
				return;
			}
			java.awt.EventQueue.invokeLater(new LoadGraphDialog(fuzzySystem));
		}
	}// GEN-LAST:event_addGraphButtonMouseClicked

	private void setStorageOptionMouseClicked(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_setStorageOptionMouseClicked
		synchronized (this) {
			// msgLabel.setForeground(java.awt.Color.BLACK);
			msgLabel.setText(null);
			textArea.setText(null);
			// msgLabel.setText("set storage button muose clicked");
			java.awt.EventQueue.invokeLater(new SetStorageOptionDialog(this));
		}
	}// GEN-LAST:event_setStorageOptionMouseClicked

	private void showModelContentMouseClicked(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_showModelContentMouseClicked
		synchronized (this) {
			textArea.setText(null);
			if (fuzzySystem == null) {
				textArea.setForeground(java.awt.Color.RED);
				textArea.setText("set a storage option first");
				return;
			}
			PrintWriter out = new PrintWriter(new JTextAreaWriter(textArea));
			try {
				fuzzySystem.write(out, "N3");
			} catch (SQLException e) {
				GUIView.showException(e, textArea);
			}
		}
	}// GEN-LAST:event_showModelContentMouseClicked

	private void queryDialogMouseClicked(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_queryDialogMouseClicked
		synchronized (this) {
			msgLabel.setText(null);
			msgLabel.setForeground(java.awt.Color.RED);
			msgLabel.setText("WARNING: query dialog not implemented yet");
		}
	}// GEN-LAST:event_queryDialogMouseClicked

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private javax.swing.JButton addGraphButton;
	private javax.swing.JButton clearButton;
	private javax.swing.JButton exitButton;
	private javax.swing.JPanel jPanel1;
	private javax.swing.JPanel jPanel2;
	private javax.swing.JPanel jPanel3;
	private javax.swing.JScrollPane jScrollPane1;
	private javax.swing.JPanel mainPanel;
	private javax.swing.JLabel msgLabel;
	private javax.swing.JButton queryDialog;
	private javax.swing.JButton setStorageOption;
	private javax.swing.JButton showModelContent;
	private javax.swing.JTextArea textArea;
	private javax.swing.JComboBox tnormChooserComboBox;
	// End of variables declaration//GEN-END:variables
}