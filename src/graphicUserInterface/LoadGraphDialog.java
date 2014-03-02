/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * LoadGraphDialog.java
 *
 * Created on May 20, 2009, 10:58:04 PM
 */
package graphicUserInterface;

import fuzzyRDFSEngine.*;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;

/**
 * 
 * @author feffo
 */
public class LoadGraphDialog extends javax.swing.JFrame implements Runnable {

	private FuzzySystem fuzzySystem;

	LoadGraphDialog(FuzzySystem fuzzySystem) {
		this.fuzzySystem = fuzzySystem;
	}

	public void run() {
		initComponents();
		this.setVisible(true);
		this.setAlwaysOnTop(false);
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
	// <editor-fold defaultstate="collapsed" desc="Generated
	// Code">//GEN-BEGIN:initComponents
	private void initComponents() {

		jPanel1 = new javax.swing.JPanel();
		jLabel1 = new javax.swing.JLabel();
		jSeparator1 = new javax.swing.JSeparator();
		jPanel2 = new javax.swing.JPanel();
		browseFileButton = new javax.swing.JButton();
		fileNameLabel = new javax.swing.JLabel();
		choosedFileLabel = new javax.swing.JLabel();
		jPanel3 = new javax.swing.JPanel();
		jScrollPane1 = new javax.swing.JScrollPane();
		msgArea = new javax.swing.JTextArea();
		jPanel4 = new javax.swing.JPanel();
		addGraphButton = new javax.swing.JButton();

		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application
				.getInstance(graphicUserInterface.GUIMain.class)
				.getContext().getResourceMap(LoadGraphDialog.class);
		setTitle(resourceMap.getString("Form.title")); // NOI18N
		setName("Form"); // NOI18N

		jPanel1.setName("jPanel1"); // NOI18N

		jLabel1.setFont(resourceMap.getFont("jLabel1.font")); // NOI18N
		jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
		jLabel1.setName("jLabel1"); // NOI18N

		javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(
				jPanel1);
		jPanel1.setLayout(jPanel1Layout);
		jPanel1Layout.setHorizontalGroup(jPanel1Layout.createParallelGroup(
				javax.swing.GroupLayout.Alignment.LEADING).addGroup(
				jPanel1Layout.createSequentialGroup().addGap(172, 172, 172)
						.addComponent(jLabel1).addContainerGap(221,
								Short.MAX_VALUE)));
		jPanel1Layout.setVerticalGroup(jPanel1Layout.createParallelGroup(
				javax.swing.GroupLayout.Alignment.LEADING).addGroup(
				jPanel1Layout.createSequentialGroup().addComponent(jLabel1)
						.addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE,
								Short.MAX_VALUE)));

		jSeparator1.setName("jSeparator1"); // NOI18N

		jPanel2.setBorder(javax.swing.BorderFactory
				.createTitledBorder(resourceMap
						.getString("jPanel2.border.title"))); // NOI18N
		jPanel2.setName("jPanel2"); // NOI18N

		browseFileButton
				.setText(resourceMap.getString("browseFileButton.text")); // NOI18N
		browseFileButton.setName("browseFileButton"); // NOI18N
		browseFileButton.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				browseFileButtonMouseClicked(evt);
			}
		});

		fileNameLabel.setText(resourceMap.getString("fileNameLabel.text")); // NOI18N
		fileNameLabel.setName("fileNameLabel"); // NOI18N

		choosedFileLabel
				.setText(resourceMap.getString("choosedFileLabel.text")); // NOI18N
		choosedFileLabel.setName("choosedFileLabel"); // NOI18N

		javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(
				jPanel2);
		jPanel2.setLayout(jPanel2Layout);
		jPanel2Layout
				.setHorizontalGroup(jPanel2Layout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								jPanel2Layout
										.createSequentialGroup()
										.addContainerGap()
										.addComponent(browseFileButton)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(fileNameLabel)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(
												choosedFileLabel,
												javax.swing.GroupLayout.DEFAULT_SIZE,
												346, Short.MAX_VALUE)
										.addContainerGap()));
		jPanel2Layout
				.setVerticalGroup(jPanel2Layout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								jPanel2Layout
										.createSequentialGroup()
										.addGroup(
												jPanel2Layout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.BASELINE)
														.addComponent(
																browseFileButton)
														.addComponent(
																fileNameLabel)
														.addComponent(
																choosedFileLabel,
																javax.swing.GroupLayout.PREFERRED_SIZE,
																28,
																javax.swing.GroupLayout.PREFERRED_SIZE))
										.addContainerGap(51, Short.MAX_VALUE)));

		jPanel3.setName("jPanel3"); // NOI18N

		jScrollPane1.setName("jScrollPane1"); // NOI18N

		msgArea.setColumns(20);
		msgArea.setRows(5);
		msgArea.setName("msgArea"); // NOI18N
		jScrollPane1.setViewportView(msgArea);

		javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(
				jPanel3);
		jPanel3.setLayout(jPanel3Layout);
		jPanel3Layout.setHorizontalGroup(jPanel3Layout.createParallelGroup(
				javax.swing.GroupLayout.Alignment.LEADING).addComponent(
				jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 618,
				Short.MAX_VALUE));
		jPanel3Layout.setVerticalGroup(jPanel3Layout.createParallelGroup(
				javax.swing.GroupLayout.Alignment.LEADING).addGroup(
				jPanel3Layout.createSequentialGroup().addComponent(
						jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE,
						167, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE,
								Short.MAX_VALUE)));

		jPanel4.setName("jPanel4"); // NOI18N

		addGraphButton.setText(resourceMap.getString("addGraphButton.text")); // NOI18N
		addGraphButton.setName("addGraphButton"); // NOI18N
		addGraphButton.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				addGraphButtonMouseClicked(evt);
			}
		});

		javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(
				jPanel4);
		jPanel4.setLayout(jPanel4Layout);
		jPanel4Layout.setHorizontalGroup(jPanel4Layout.createParallelGroup(
				javax.swing.GroupLayout.Alignment.LEADING).addGroup(
				jPanel4Layout.createSequentialGroup().addGap(270, 270, 270)
						.addComponent(addGraphButton).addContainerGap(270,
								Short.MAX_VALUE)));
		jPanel4Layout.setVerticalGroup(jPanel4Layout.createParallelGroup(
				javax.swing.GroupLayout.Alignment.LEADING).addGroup(
				jPanel4Layout.createSequentialGroup().addContainerGap()
						.addComponent(addGraphButton).addContainerGap(44,
								Short.MAX_VALUE)));

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(
				getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(
				javax.swing.GroupLayout.Alignment.LEADING).addComponent(
				jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE,
				javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
				.addComponent(jSeparator1,
						javax.swing.GroupLayout.DEFAULT_SIZE, 618,
						Short.MAX_VALUE).addComponent(jPanel2,
						javax.swing.GroupLayout.DEFAULT_SIZE,
						javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
				.addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE,
						javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
				.addComponent(jPanel4,
						javax.swing.GroupLayout.Alignment.TRAILING,
						javax.swing.GroupLayout.DEFAULT_SIZE,
						javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));
		layout
				.setVerticalGroup(layout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								layout
										.createSequentialGroup()
										.addComponent(
												jPanel1,
												javax.swing.GroupLayout.PREFERRED_SIZE,
												javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(
												jSeparator1,
												javax.swing.GroupLayout.PREFERRED_SIZE,
												10,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(
												jPanel2,
												javax.swing.GroupLayout.PREFERRED_SIZE,
												javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(
												jPanel3,
												javax.swing.GroupLayout.PREFERRED_SIZE,
												javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addGap(18, 18, 18)
										.addComponent(
												jPanel4,
												javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.DEFAULT_SIZE,
												Short.MAX_VALUE)));

		pack();
	}// </editor-fold>//GEN-END:initComponents

	private void browseFileButtonMouseClicked(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_browseFileButtonMouseClicked
		JFileChooser fileChooser = new JFileChooser();
		int returnVal = fileChooser.showOpenDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			try {
				choosedFileLabel.setText(fileChooser.getSelectedFile()
						.getCanonicalPath());
			} catch (IOException ex) {
				Logger.getLogger(LoadGraphDialog.class.getName()).log(
						Level.SEVERE, null, ex);
			}
		}
	}// GEN-LAST:event_browseFileButtonMouseClicked

	private void addGraphButtonMouseClicked(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_addGraphButtonMouseClicked

		try {
			long startTime = System.currentTimeMillis();
			msgArea.setText(null);
			msgArea.append("loading file " + choosedFileLabel.getText()
					+ ", this operation might take minutes, please wait\n");
			fuzzySystem.read(choosedFileLabel.getText(), "");
			msgArea.setForeground(java.awt.Color.BLACK);
			long elapsedTime = System.currentTimeMillis() - startTime;
			msgArea
					.append("\nfile " + choosedFileLabel.getText()
							+ " loaded correctly in " + elapsedTime
							+ " milliseconds\n");
		} catch (Exception ex) {
			GUIView.showException(ex, msgArea);
			ex.printStackTrace();
		}
	}// GEN-LAST:event_addGraphButtonMouseClicked

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private javax.swing.JButton addGraphButton;

	private javax.swing.JButton browseFileButton;

	private javax.swing.JLabel choosedFileLabel;

	private javax.swing.JLabel fileNameLabel;

	private javax.swing.JLabel jLabel1;

	private javax.swing.JPanel jPanel1;

	private javax.swing.JPanel jPanel2;

	private javax.swing.JPanel jPanel3;

	private javax.swing.JPanel jPanel4;

	private javax.swing.JScrollPane jScrollPane1;

	private javax.swing.JSeparator jSeparator1;

	private javax.swing.JTextArea msgArea;
	// End of variables declaration//GEN-END:variables
}
