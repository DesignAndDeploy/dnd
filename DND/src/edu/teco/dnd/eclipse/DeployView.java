package edu.teco.dnd.eclipse;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.part.ViewPart;

public class DeployView extends ViewPart {

	@Override
	public void createPartControl(Composite parent) {
		Label label = new Label(parent, SWT.None);
		label.setText("Under Construction. Please be patient, our incredible DeployView will be here soon.");
		label.pack();
		
	}

	@Override
	public void setFocus() {
		
	}
	/**
	 * Planung: Gebraucht: - Verfügbare Anwendungen anzeigen - Anwendung
	 * anwählen - Verteilungsalgorithmus auswählen - Fest im Code einbinden? -
	 * Verteilung erstellen lassen und anzeigen - Verteilung bestätigen
	 * 
	 */

}
