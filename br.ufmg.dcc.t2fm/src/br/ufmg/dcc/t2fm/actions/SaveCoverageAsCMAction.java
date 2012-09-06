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

import java.util.HashSet;
import java.util.Iterator;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaModel;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.SaveAsDialog;

import br.ufmg.dcc.t2fm.Test2FeatureMapper;
import br.ufmg.dcc.t2fm.model.ConcernModel;
import br.ufmg.dcc.t2fm.model.io.ModelIOException;
import br.ufmg.dcc.t2fm.model.io.ModelWriter;
import br.ufmg.dcc.t2fm.ui.ConcernMapperPreferencePage;
import br.ufmg.dcc.t2fm.views.MapView;

import com.mountainminds.eclemma.core.CoverageTools;
import com.mountainminds.eclemma.core.ICoverageSession;
import com.mountainminds.eclemma.core.ScopeUtils;

/**
 * @author Alcemir R. Santos
 * 
 */
public class SaveCoverageAsCMAction extends Action {

	MapView aView;

	/**
	 * @param mapView
	 */
	public SaveCoverageAsCMAction(MapView mapView) {
		this.aView = mapView;
		setText("Save Coverage into a cm File");
		setToolTipText("Save Coverage into a cm File");
		setImageDescriptor(PlatformUI.getWorkbench().getSharedImages()
				.getImageDescriptor(ISharedImages.IMG_OBJ_FILE));
	}

	public void run() {
		ConcernModel cm = null;
		boolean thereIsActiveCoverageSession = false;
		ICoverageSession activeSession = CoverageTools.getSessionManager()
				.getActiveSession();
		thereIsActiveCoverageSession = activeSession == null ? false : true;

		HashSet<IPackageFragmentRoot> escopo = null;

		// se existe uma sessão ativa
		if (thereIsActiveCoverageSession) {
			try {
				escopo = (HashSet<IPackageFragmentRoot>) ScopeUtils
						.filterJREEntries(activeSession.getScope());
			} catch (JavaModelException e) {
				e.printStackTrace();
			}

			// && e se o elemento está no escopo
			Iterator<IPackageFragmentRoot> iterator = escopo.iterator();
			while (iterator.hasNext()) {
				IPackageFragmentRoot fragmentRoot = iterator.next();
				String s = fragmentRoot.getPath().lastSegment();
				IJavaModel ijm = fragmentRoot.getJavaModel();
				IWorkspace workspace = ijm.getWorkspace();
				IWorkspaceRoot root = workspace.getRoot();
				// Get all projects in the workspace
				IProject[] projects = root.getProjects();
				// Loop over all projects
				for (IProject project : projects) {
					try {
						printProjectInfo(project);
					} catch (CoreException e) {
						e.printStackTrace();
					}
				}
				System.out.println(s);

				/** criar um concernmodel com o que foi coberto [igual ao listener interno do AddToConcernAction];*/
				// CoverageTools.getCoverageInfo(object);
				// ICoverageNode coverage = (ICoverageNode)
				// someJavaElement.getAdapter(ICoverageNode.class);
			}

		}


		// TODO escrever no cm file;
		create File(cm);
		
		// TODO matar sessão de cobertura
	}

	/**
	 * @param cm
	 */
	private void createFile(ConcernModel cm) {
		ModelWriter mw = new ModelWriter(cm);
		IFile lFile = null;
		
		SelectPathDialog lDialog = new SelectPathDialog(PlatformUI
				.getWorkbench().getActiveWorkbenchWindow().getShell());

		lDialog.open();

		if (lDialog.getReturnCode() == Window.CANCEL) {
			return;
		}
		IPath lPath = lDialog.getResult();
		lPath = addCMFileExtension(lPath);

		IWorkspace lWorkspace = ResourcesPlugin.getWorkspace();
		lFile = lWorkspace.getRoot().getFile(lPath);

		if (!lFile.exists()) {
			try {
				lFile.create(null, true, null);
			} catch (CoreException lException) {
				MessageDialog
						.openError(
								PlatformUI.getWorkbench()
										.getActiveWorkbenchWindow().getShell(),
								Test2FeatureMapper
										.getResourceString("actions.SaveAsAction.ErrorLabel"),
								Test2FeatureMapper
										.getResourceString("actions.SaveAsAction.ErrorMessage")
										+ " " + lException.getMessage());
				return;
			}
		}

		try {
//			Test2FeatureMapper.getDefault().setDefaultResource(lFile);
			ModelWriter lWriter = new ModelWriter(cm);
			lWriter.write(lFile);
		} catch (ModelIOException lException) {
			MessageDialog
					.openError(
							PlatformUI.getWorkbench()
									.getActiveWorkbenchWindow().getShell(),
							Test2FeatureMapper
									.getResourceString("actions.SaveAsAction.ErrorLabel"),
							Test2FeatureMapper
									.getResourceString("actions.SaveAsAction.ErrorMessage")
									+ " " + lException.getMessage());
			return;
		}

//		TODO criar uma dirty para só ativar o criar cm depois de rodar os testes
//		Test2FeatureMapper.getDefault().resetDirty();
//		if (aView != null) {
//			aView.updateActionState();
//		}
	}

	private static IPath addCMFileExtension(IPath pPath) {
		IPath lReturn = pPath;
		if (Test2FeatureMapper.getDefault().getPreferenceStore()
				.getBoolean(ConcernMapperPreferencePage.P_CM_FILE_EXT)) {
			if (lReturn.getFileExtension() == null) {
				lReturn = lReturn.addFileExtension("cm");
			} else if (!lReturn.getFileExtension().equals("cm")) {
				lReturn = lReturn.removeFileExtension();
				lReturn = lReturn.addFileExtension("cm");
			}
		}
		return lReturn;
	}

	private void printProjectInfo(IProject project) throws CoreException,
			JavaModelException {
		System.out.println("Working in project " + project.getName());
		// Check if we have a Java project
		if (project.isNatureEnabled("org.eclipse.jdt.core.javanature")) {
			IJavaProject javaProject = JavaCore.create(project);
			printPackageInfos(javaProject);
		}
	}

	private void printPackageInfos(IJavaProject javaProject)
			throws JavaModelException {
		IPackageFragment[] packages = javaProject.getPackageFragments();
		for (IPackageFragment mypackage : packages) {
			// Package fragments include all packages in the
			// classpath
			// We will only look at the package from the source
			// folder
			// K_BINARY would include also included JARS, e.g.
			// rt.jar
			if (mypackage.getKind() == IPackageFragmentRoot.K_SOURCE) {
				System.out.println("Package " + mypackage.getElementName());
				printICompilationUnitInfo(mypackage);

			}

		}
	}

	private void printICompilationUnitInfo(IPackageFragment mypackage)
			throws JavaModelException {
		for (ICompilationUnit unit : mypackage.getCompilationUnits()) {
			printCompilationUnitDetails(unit);

		}
	}

	private void printIMethods(ICompilationUnit unit) throws JavaModelException {
		IType[] allTypes = unit.getAllTypes();
		for (IType type : allTypes) {
			printIMethodDetails(type);
		}
	}

	private void printCompilationUnitDetails(ICompilationUnit unit)
			throws JavaModelException {
		System.out.println("Source file " + unit.getElementName());
		Document doc = new Document(unit.getSource());
		System.out.println("Has number of lines: " + doc.getNumberOfLines());
		printIMethods(unit);
	}

	private void printIMethodDetails(IType type) throws JavaModelException {
		IMethod[] methods = type.getMethods();
		for (IMethod method : methods) {

			System.out.println("Method name " + method.getElementName());
			System.out.println("Signature " + method.getSignature());
			System.out.println("Return Type " + method.getReturnType());

		}
	}

	class SelectPathDialog extends SaveAsDialog {

		/**
		 * Creates a new SaveAsDialog for saving concern models.
		 * 
		 * @param pShell
		 *            The parent shell.
		 */
		public SelectPathDialog(Shell parentShell) {
			super(parentShell);
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
