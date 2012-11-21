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
package br.ufmg.dcc.tabuleta.actions;

import java.io.File;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.SaveAsDialog;

import br.ufmg.dcc.tabuleta.Tabuleta;
import br.ufmg.dcc.tabuleta.model.io.JavaFileWriter;
import br.ufmg.dcc.tabuleta.views.T2FMappingView;
import br.ufmg.dcc.tabuleta.views.components.ConcernNode;

/**
 * Esta classe é responsável por gerar uma suíte de testes <code>JUnit</code> para 
 *  uma característica seleicionada na view {@link T2FMappingView}.
 *  
 * @author Alcemir R. Santos
 * 
 */
public class GenerateTestSuiteAction extends Action {

	T2FMappingView aView;

	public GenerateTestSuiteAction(T2FMappingView pView) {
		aView = pView;
		setText("Generate Test Suite");
		setToolTipText("Generate Test Suite Action");
		setImageDescriptor(Tabuleta.imageDescriptorFromPlugin(
				Tabuleta.ID_PLUGIN, "icons/generateJU.png"));
	}

	public void run()  {
		IFile lFile = null;
		IPath fullPathTrunkedByWorkspacePath = null;
		String className = "";
		String selectedFeature = "";
		if (aView.getCurrentSelection().isEmpty()) {
			showMessage("You must select a feature.");
			return;
		}else{
			ISelection iss = aView.getCurrentSelection();
			ConcernNode cn;
			if (iss instanceof TreeSelection) {
				TreeSelection ts = (TreeSelection) iss;
				cn = (ConcernNode)ts.getFirstElement();
				selectedFeature = cn.getConcernName();
			}
		}
		
		IWorkspace lWorkspace = ResourcesPlugin.getWorkspace();
		IPath workspacePath = lWorkspace.getRoot().getLocation();
		
		String testPath = Tabuleta.getDefault().getPreferenceStore().getString("TESTSPATH");
		if (testPath==null || testPath.isEmpty()) {
			showMessage("You must set the path to tests files on the TaBuLeTa preference page.");
			return;
		}else{
			Path aPath = new Path(testPath);
			// find file in workspace
			IFile file =  ResourcesPlugin.getWorkspace().getRoot().getFileForLocation( aPath );
			if (file != null ) {
				fullPathTrunkedByWorkspacePath = trunkPath(file.getLocation(), workspacePath);
			}else{
				showMessage("We are not able to find the path to tests files. You can set it on the preference page.");
				return;
			}
		}
		// check if file in workspace
		
//		String src = workspacePath.toString() + File.separator +fullPathTrunkedByWorkspacePath.segment(0) + File.separator + fullPathTrunkedByWorkspacePath.segment(1);
		String pakkage = buildPackage(fullPathTrunkedByWorkspacePath);
		className = genClassName(selectedFeature);
		lFile = lWorkspace.getRoot().getFile( fullPathTrunkedByWorkspacePath );
		
		if( !lFile.exists() ){
			JavaFileWriter lWriter = new JavaFileWriter(testPath, pakkage, className, Tabuleta.getDefault().getConcernModel() );
			lWriter.write(aView.getCurrentSelection());
		}else{
			// TODO ask if wants to overwrite the already existent file. 
			showMessage("a file already exists.");
		}
		
		showMessage("Generate Test Suite Action executed.");
	}

	/**
	 * Constrói o nome da suíte de teste de acordo com o nome passado.
	 * @param selectedFeature
	 * @return
	 */
	private String genClassName(String selectedFeature) {
		char first = selectedFeature.toUpperCase().charAt(0);
		String s = first+selectedFeature.substring(1)+"TestSuite";
		return s;
	}

	/**
	 * Dado o parâmetro <b>path</b>, que representa um caminho completo, este método retorna uma instância
	 *   de {@link IPath} em que o parâmetro remove o pedaço do caminho representado por <b>with</b>.
	 *    
	 * @param path
	 * @param with
	 * @return
	 */
	private IPath trunkPath(IPath path, IPath with){
		int i = path.matchingFirstSegments(with);		
		return path.removeFirstSegments(i);
	}

	/**
	 * Constói o pacote para inserção da classe gerada.
	 * 
	 * @param lPath
	 * @return
	 */
	private String buildPackage(IPath lPath) {
		String pakkage = "tabuleta.testsuites";
		String tabuleta = "tabuleta";
		String testsuites = "testsuites";
		
//		if (lPath.segmentCount()>2) {
//			pakkage = lPath.segment(2);
//			for (int i = 3; i < lPath.segments().length - 1; i++) {
//				pakkage += "." + lPath.segment(i);
//			}
//		} else{
			IFolder iff = (IFolder) ResourcesPlugin.getWorkspace().getRoot().getFolder(lPath.append(tabuleta));
			try {
				iff.create(true, true, new NullProgressMonitor());
				iff = (IFolder) ResourcesPlugin.getWorkspace().getRoot().getFolder(lPath.append(tabuleta+File.separator+testsuites));
				iff.create(true, true, new NullProgressMonitor());
			} catch (CoreException e) {
				e.printStackTrace();
			}
//		}
		return pakkage;
	}

	private void showMessage(String message) {
		MessageDialog.openInformation(PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getShell(),
				"Test2Feature Mapping", message);
	}

	/**
	 * We have to define this class simply to be able to change the title of the
	 * dialog. This is necessary because when the save as functionality kicks in
	 * as the result of an autosave the default title is too generic and users
	 * may not directly see that they are saving their test suite.
	 * 
	 * @author Alcemir Santos
	 */
	class TestSuiteSaveAsDialog extends SaveAsDialog {
		/**
		 * Creates a new SaveAsDialog for saving concern models.
		 * 
		 * @param pShell
		 *            The parent shell.
		 */
		public TestSuiteSaveAsDialog(Shell pShell) {
			super(pShell);
		}

		/**
		 * @see org.eclipse.jface.window.Window#createContents(org.eclipse.swt.widgets.Composite)
		 * @param pParent
		 *            the parent composite for the controls in this window. The
		 *            type of layout used is determined by getLayout()
		 * @return the control that will be returned by subsequent calls to
		 *         getControl()
		 */
		protected Control createContents(Composite pParent) {
			Control lContents = super.createContents(pParent);

			setTitle(Tabuleta
					.getResourceString("actions.GenerateTestSuiteAction.DialogTitle"));
			setMessage(Tabuleta
					.getResourceString("actions.GenerateTestSuiteAction.DialogMessage"));

			return lContents;
		}
	}

}
