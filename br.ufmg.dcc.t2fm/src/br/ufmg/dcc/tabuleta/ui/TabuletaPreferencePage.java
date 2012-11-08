/*************************************************************************
 * Copyright (c) 2012 Federal University of Minas Gerais - UFMG 
 * All rights avaiable. This program and the accompanying materials
 * are made avaiable under the terms of the Eclipse Public Lincense v1.0
 * which accompanies this distribution, and is avaiable at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *   Alcemir R. Santos - improvements on the ConcernMapper
 * 			architeture. ConcernMapper is available at
 * 			http://www.cs.mcgill.ca/~martin/cm/
 *************************************************************************/
package br.ufmg.dcc.tabuleta.ui;

import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import br.ufmg.dcc.tabuleta.Tabuleta;

/**
 * Esta classe é responsável pela criação da <code>Página de Preferências</code> no 
 *   útilitário de Preferencências do Eclipse. Acessível através de:
 *    <code>Window &gt; Preferences;</code>
 * @author Alcemir R. Santos
 * 
 */
public class TabuletaPreferencePage extends FieldEditorPreferencePage
		implements IWorkbenchPreferencePage {

	public TabuletaPreferencePage() {
		super(GRID);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	@Override
	public void init(IWorkbench workbench) {
		setPreferenceStore(Tabuleta.getDefault().getPreferenceStore());
		setDescription("TaBuLeTa Preference Page");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.preference.FieldEditorPreferencePage#createFieldEditors
	 * ()
	 */
	@Override
	protected void createFieldEditors() {
		addField(new DirectoryFieldEditor("TESTSPATH", "Set &tests source directory preference:",
				getFieldEditorParent()));
		
		addField(new DirectoryFieldEditor("CMPATH", "Set &cm file directory preference:",
				getFieldEditorParent()));
		
	}

}
