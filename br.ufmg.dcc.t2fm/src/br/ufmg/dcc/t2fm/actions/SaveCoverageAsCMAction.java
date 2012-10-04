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
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.SaveAsDialog;
import org.jacoco.core.analysis.ICoverageNode;

import br.ufmg.dcc.t2fm.Test2FeatureMapper;
import br.ufmg.dcc.t2fm.model.ConcernModel;
import br.ufmg.dcc.t2fm.model.io.ModelIOException;
import br.ufmg.dcc.t2fm.model.io.ModelWriter;
import br.ufmg.dcc.t2fm.ui.ConcernMapperPreferencePage;
import br.ufmg.dcc.t2fm.ui.ProblemManager;
import br.ufmg.dcc.t2fm.views.MapView;
import br.ufmg.dcc.t2fm.views.components.ConcernNode;

import com.mountainminds.eclemma.core.CoverageTools;
import com.mountainminds.eclemma.core.ICoverageSession;
import com.mountainminds.eclemma.core.ScopeUtils;

/**
 * @author Alcemir R. Santos
 * 
 */
public class SaveCoverageAsCMAction extends Action {

	private MapView aView;
	private ConcernModel concernModel;
	private String selectedFeature;

	/**
	 * @param mapView
	 */
	public SaveCoverageAsCMAction(MapView mapView) {
		this.aView = mapView;
		setText("Save Coverage into a cm File");
		setToolTipText("Save Coverage into a cm File");
		setImageDescriptor( Test2FeatureMapper.imageDescriptorFromPlugin( Test2FeatureMapper.ID_PLUGIN, "icons/coverage2cm.png"));
		concernModel = new ConcernModel();
	}

	public void run() {
		boolean thereIsActiveCoverageSession = false;
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
				IJavaProject javaProject = (IJavaProject) fragmentRoot.getParent();
				IPath aPath = javaProject.getPath();
				try {
//					if(isSourcePath(aPath))
						selectPackagesToModel(javaProject);
				} catch (JavaModelException e) {
					e.printStackTrace();
				}
			}

			createFile();
			// TODO matar sessão de cobertura

		}else{
			showMessage("You must run the generated suite test class with EclEmma " +
					"coverage tool before save the .cm file.");
		}

	}

	private void showMessage(String message) {
		MessageDialog.openInformation(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Test2FeatureMapper View", message);
	}
	
	/**
	 * testa se é um pacote de source
	 * @param aPath
	 */
	private boolean isSourcePath(IPath aPath) {
		for (int i = 0; i < aPath.segments().length; i++) {
			if (aPath.segment(i).contains("src")) {
				return true;
			}
		}
		return false;
	}

	private void createConcernModel(String feature, IPackageFragment mypackage) {

		IJavaElement[] packageElements;
		ICompilationUnit[] units;
		double threshold = 0.5;
		try {
			units = mypackage.getCompilationUnits();

			for (ICompilationUnit unit : units) {
				if (mypackage.hasChildren()) {
					packageElements = unit.getChildren();
					for (int i = 0; i < packageElements.length; i++) {
						IJavaElement lNext = packageElements[i];
						ICoverageNode node;
						if (supportedElement(lNext)) {
							node = (ICoverageNode) lNext.getAdapter(ICoverageNode.class);
							if (node!=null) {
								double ratio = node.getLineCounter().getCoveredRatio();
								System.out.println(node.getName()+": "+ratio);
								if( ratio >= threshold)
									addToConcern(lNext, feature, (int)Math.ceil(ratio*100));
							}
						}
						// if it is a class or interface, get its members
						// and add them to the concern
						else if (supportedType(lNext)) {
							final IField[] lFields = returnFields((IType) lNext);
							final IMethod[] lMethods = returnMethods((IType) lNext);
							
							for (IField lField : lFields) {
								node = (ICoverageNode) lField.getAdapter(ICoverageNode.class);
								if (node!=null) {
									double ratio = node.getLineCounter().getCoveredRatio();
									System.out.println(node.getName()+": "+ratio);
//									if( ratio >= threshold)
										addToConcern(lField, feature, (int)Math.ceil(ratio*100));
								}
							}
							for (IMethod lMethod : lMethods) {
								node = (ICoverageNode) lMethod.getAdapter(ICoverageNode.class);
								if (node!=null) {
									double ratio = node.getLineCounter().getCoveredRatio();
									System.out.println(node.getName()+": "+ratio);
									if( ratio >= threshold)
										addToConcern(lMethod, feature, (int)Math.ceil(ratio*100));
								}
							}
						} else {
							// The element is not supported by ConcernMapper
						}
					}
				}
			}

		} catch (JavaModelException e1) {
			e1.printStackTrace();
		}
	}

	/**
	 * @param cm
	 */
	private void createFile() {
		IFile lFile = null;
		IPath lPath = null;
		
		// pede o nome e lugar para salvar o arquivo .cm
//		SelectPathDialog lDialog = new SelectPathDialog(PlatformUI
//				.getWorkbench().getActiveWorkbenchWindow().getShell());
//
//		lDialog.open();
//
//		if (lDialog.getReturnCode() == Window.CANCEL) {
//			return;
//		}
//		
//		IPath lPath = lDialog.getResult();
		
		if (selectedFeature==null || selectedFeature.isEmpty()) {
			showMessage("You must select a feature.");
			return;
		}
		
		IWorkspace lWorkspace = ResourcesPlugin.getWorkspace();
		IPath s = lWorkspace.getRoot().getLocation();
		
		String cmPath = Test2FeatureMapper.getDefault().getPreferenceStore().getString("CMPATH");
		if (cmPath==null || cmPath.isEmpty()) {
			showMessage("You must set the path to .cm files on the Test2FeatureMapper preference page.");
			return;
		}else{
			Path aPath = new Path(cmPath);
			// find file in workspace
			IFile file =  ResourcesPlugin.getWorkspace().getRoot().getFileForLocation( aPath );
			if (file != null ) {
				lPath = trunkPath(file.getLocation(), s ).append("CM"+selectedFeature);
			}else{
				showMessage("We are not able to find the path to .cm files. You can set it on the preference page.");
				return;
			}
		}
		
		//
		lPath = addCMFileExtension(lPath);

		lFile = lWorkspace.getRoot().getFile(lPath);

		// cria o arquivo
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

		// escreve no arquivo
		try {
			ModelWriter lWriter = new ModelWriter(concernModel);
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

		// TODO criar uma dirty para só ativar o criar cm depois de rodar os
		// testes
		// Test2FeatureMapper.getDefault().resetDirty();
		// if (aView != null) {
		// aView.updateActionState();
		// }
	}

	private IPath trunkPath(IPath path, IPath with){
		int i = path.matchingFirstSegments(with);		
		return path.removeFirstSegments(i);
	}
	
	/**
	 * Determines if pElement can be included in a concern model.
	 * 
	 * @param pElement
	 *            The element to test
	 * @return true if pElement is of a type that is supported by the concern
	 *         model.
	 */

	private boolean supportedElement(IJavaElement pElement) {
		boolean lReturn = false;
		if ((pElement instanceof IField) || (pElement instanceof IMethod)) {
			try {
				if (((IMember) pElement).getDeclaringType().isAnonymous()
						|| ((IMember) pElement).getDeclaringType().isLocal()) {
					lReturn = false;
				} else {
					lReturn = true;
				}
			} catch (JavaModelException lException) {
				ProblemManager.reportException(lException);
				lReturn = false;
			}
		}
		return lReturn;
	}

	/**
	 * Determines if pElement is a class or an interface which elements are to
	 * be put into the concern model.
	 * 
	 * @param pElement
	 *            The element to test
	 * @return true if pElement is a class or an interface
	 * 
	 */

	private boolean supportedType(IJavaElement pElement) {
		boolean lReturn = false;
		if (pElement instanceof IType) {
			lReturn = true;
			try {
				if (((IType) pElement).isAnonymous()
						|| ((IType) pElement).isLocal()) {
					lReturn = false;
				} else {
					lReturn = true;
				}
			} catch (JavaModelException lException) {
				ProblemManager.reportException(lException);
				lReturn = false;
			}
		}
		return lReturn;
	}

	/**
	 * Parses and returns the fields of IType element.
	 * 
	 * @param pElement
	 *            The element whose fields are to be obtained
	 */
	private IField[] returnFields(IType pElement) {
		IField[] lReturn = null;
		try {
			lReturn = pElement.getFields();
		} catch (JavaModelException lException) {
			ProblemManager.reportException(lException);
		}

		return lReturn;
	}

	/**
	 * Parses and returns the methods of IType element.
	 * 
	 * @param pElement
	 *            The element whose methods are to be obtained
	 */
	private IMethod[] returnMethods(IType pElement) {
		IMethod[] lReturn = null;
		try {
			lReturn = pElement.getMethods();
		} catch (JavaModelException lException) {
			ProblemManager.reportException(lException);
		}

		return lReturn;
	}

	/**
	 * Adds the element to an identified concern.
	 * 
	 * @param pElement
	 *            The element we want to add into the concern model pConcern The
	 *            string-identified concern we want to augment
	 */
	private void addToConcern(IJavaElement pElement, String pConcern, int degree) {
		if (!concernModel.exists(pConcern)) {
			concernModel.newConcern(pConcern);
		}
		if (!concernModel.exists(pConcern, pElement)) {
			concernModel.addElement(pConcern, pElement, degree);
		}
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

	private void selectPackagesToModel(IJavaProject javaProject)
			throws JavaModelException {
		IPackageFragment[] packages = javaProject.getPackageFragments();
		for (IPackageFragment mypackage : packages) {

			if (!isSourcePath( mypackage.getAncestor(IJavaElement.PACKAGE_FRAGMENT).getPath())) {
				break;
			}
			// Package fragments include all packages in the
			// classpath
			// We will only look at the package from the source
			// folder
			// K_BINARY would include also included JARS, e.g.
			// rt.jar
			if (mypackage.getKind() == IPackageFragmentRoot.K_SOURCE) {
				System.out.println("Package " + mypackage.getElementName());
				addPackageToConcernModel(mypackage);
			}
		}
	}

	/**
	 * Adiciona o pacote passado como parâmetro para o ConcernModel.
	 * 
	 * @param mypackage
	 */
	private void addPackageToConcernModel(IPackageFragment mypackage) {
		createConcernModel(selectedFeature, mypackage);
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
					.getResourceString("actions.SaveCoverageAsCMAction.DialogTitle"));
			setMessage(Test2FeatureMapper
					.getResourceString("actions.SaveCoverageAsCMAction.DialogMessage"));

			return lContents;
		}
	}
}
