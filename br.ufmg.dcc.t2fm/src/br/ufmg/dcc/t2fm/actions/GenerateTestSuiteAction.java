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
package br.ufmg.dcc.t2fm.actions;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.SaveAsDialog;

import br.ufmg.dcc.t2fm.Test2FeatureMapper;
import br.ufmg.dcc.t2fm.model.io.JavaFileWriter;
import br.ufmg.dcc.t2fm.views.MapView;
import br.ufmg.dcc.t2fm.views.components.ConcernNode;

/**
 * @author Alcemir R. Santos
 * 
 */
public class GenerateTestSuiteAction extends Action {

	MapView aView;

	public GenerateTestSuiteAction(MapView pView) {
		aView = pView;
		setText("Generate Test Suite");
		setToolTipText("Generate Test Suite Action");
		setImageDescriptor(Test2FeatureMapper.imageDescriptorFromPlugin(
				Test2FeatureMapper.ID_PLUGIN, "icons/generateJU.png"));
	}

	public void run()  {
		IFile lFile = null;
		IPath lPath = null;
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
		IPath s = lWorkspace.getRoot().getLocation();
		
//		SaveAsDialog lDialog = new TestSuiteSaveAsDialog( PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell() );
		String testPath = Test2FeatureMapper.getDefault().getPreferenceStore().getString("TESTSPATH");
		if (testPath==null || testPath.isEmpty()) {
//			lDialog.open();
//			if( lDialog.getReturnCode() == Window.CANCEL )	{
//				return;
//			}
//			lPath =  lDialog.getResult();
//			className = lPath.lastSegment();
			showMessage("You must set the path to tests files on the Test2FeatureMapper preference page.");
			return;
		}else{
			Path aPath = new Path(testPath);
			// find file in workspace
			IFile file =  ResourcesPlugin.getWorkspace().getRoot().getFileForLocation( aPath );
			if (file != null ) {
				lPath = trunkPath(file.getLocation(), s);
			}else{
				showMessage("We are not able to find the path to tests files. You can set it on the preference page.");
				return;
			}
		}
		// check if file in workspace
		
		String src = s.toString() + File.separator +lPath.segment(0) + File.separator + lPath.segment(1);
		String pakkage = buildPackage(lPath);
		className = genClassName(selectedFeature);
		lFile = lWorkspace.getRoot().getFile( lPath );
		
		if( !lFile.exists() ){
			JavaFileWriter lWriter = new JavaFileWriter(src, pakkage, className, Test2FeatureMapper.getDefault().getConcernModel() );
			lWriter.write(aView.getCurrentSelection());
		}else{
			// TODO ask if wants to overwrite the already existent file. 
			showMessage("a file already exists.");
		}
		
		showMessage("Generate Test Suite Action executed.");
	}

	/**
	 * @param selectedFeature
	 * @return
	 */
	private String genClassName(String selectedFeature) {
		char first = selectedFeature.toUpperCase().charAt(0);
		String s = first+selectedFeature.substring(1)+"TestSuite";
		return s;
	}

	private IPath trunkPath(IPath path, IPath with){
		int i = path.matchingFirstSegments(with);		
		return path.removeFirstSegments(i);
	}

	private String buildPackage(IPath lPath) {
		String pakkage = "t2fm.testsuites";
		String t2fm = "t2fm";
		String testsuites = "testsuites";
		
		if (lPath.segmentCount()>2) {
			pakkage = lPath.segment(2);
			for (int i = 3; i < lPath.segments().length - 1; i++) {
				pakkage += "." + lPath.segment(i);
			}
		} else{
		
		IFolder iff = (IFolder) ResourcesPlugin.getWorkspace().getRoot().getFolder(lPath.append(t2fm));
		try {
			iff.create(true, true, new NullProgressMonitor());
			iff = (IFolder) ResourcesPlugin.getWorkspace().getRoot().getFolder(lPath.append(t2fm+File.separator+testsuites));
			iff.create(true, true, new NullProgressMonitor());
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}
		return pakkage;
	}

	private void showMessage(String message) {
		MessageDialog.openInformation(PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getShell(),
				"Test2FeatureMapper View", message);
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

			setTitle(Test2FeatureMapper
					.getResourceString("actions.GenerateTestSuiteAction.DialogTitle"));
			setMessage(Test2FeatureMapper
					.getResourceString("actions.GenerateTestSuiteAction.DialogMessage"));

			return lContents;
		}
	}

}
