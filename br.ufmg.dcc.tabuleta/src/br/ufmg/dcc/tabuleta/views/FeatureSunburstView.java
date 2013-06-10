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
package br.ufmg.dcc.tabuleta.views;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import javax.swing.JComponent;

import org.eclipse.albireo.core.SwingControl;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
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
import org.eclipse.jdt.internal.ui.javaeditor.JavaEditor;
import org.eclipse.jdt.internal.ui.javaeditor.selectionactions.SelectionHistory;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.FilteredItemsSelectionDialog;
import org.eclipse.ui.part.ViewPart;
import org.jacoco.core.analysis.ICoverageNode;

import prefuse.data.Graph;
import prefuse.data.Node;
import br.ufmg.dcc.tabuleta.Tabuleta;
import br.ufmg.dcc.tabuleta.actions.util.CMElementTag;
import br.ufmg.dcc.tabuleta.actions.util.CmFilesOperations;
import br.ufmg.dcc.tabuleta.ui.ProblemManager;
import br.ufmg.dcc.tabuleta.views.components.GraphManager;
import ca.utoronto.cs.prefuseextensions.demo.StarburstDemo;

import com.mountainminds.eclemma.core.CoverageTools;
import com.mountainminds.eclemma.core.ICoverageSession;
import com.mountainminds.eclemma.core.ScopeUtils;

/**
 * This class is responsible to draw a a Sunburst of the test classes associated
 * with the features in concern model.
 * 
 * @author Alcemir R. Santos
 * 
 */
public class FeatureSunburstView extends ViewPart {

	private Graph graph = new Graph();

	private Composite parent;
	private static Composite myContents;

	private GraphsViewer graphsViewer;

	private Action selectCMAction;
	private Action updateViewWithCoverageSession;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets
	 * .Composite)
	 */
	@Override
	public void createPartControl(final Composite parent) {
		this.parent = parent;
		myContents = new Composite(parent, SWT.NONE);

		graphsViewer = new GraphsViewer();

		GridLayout gLayout = new GridLayout(1, false);
		myContents.setLayout(gLayout);

		Label lbl = new Label(myContents, SWT.NULL);
		lbl.setText("Shit happens!");

		Label lbl2 = new Label(myContents, SWT.NULL);
		lbl2.setText("Here goes the visualization.");

		makeActions();
		contributeToActionBars();
	}

	/**
	 * 
	 */
	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillLocalPullDown(IMenuManager manager) {
		manager.add(selectCMAction);
		manager.add(updateViewWithCoverageSession);
		manager.add(new Separator());
	}

	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(selectCMAction);
		manager.add(updateViewWithCoverageSession);
	}

	/**
	 * 
	 */
	private void makeActions() {
		selectCMAction = new CmFileSelectAction();
		updateViewWithCoverageSession = new UpdateViewWithCoverageSessionAction();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

	public static void refresh() {
		for (Control con : myContents.getChildren()) {
			con.dispose();
		}

		getSwingControl();
	}

	protected static SwingControl getSwingControl() {
		SwingControl swingControl = new SwingControl(myContents, SWT.NONE) {
			protected JComponent createSwingComponent() {
				Graph graph = GraphManager.getInstance().getLastGraph();
				return StarburstDemo.demo(graph, "name");
				// return StarburstDemo.demo("/socialnet.xml", "name");
			}

			public Composite getLayoutAncestor() {
				return myContents;
			}
		};
		return swingControl;
	}

	private void showMessage(String message) {
		MessageDialog.openInformation(myContents.getShell(),
				"Feature Sunburst View", message);
	}

	/**
	 * @return
	 */
	public static FeatureSunburstView getGraphsViewer() {
		// TODO Auto-generated method stub
		return null;
	}

	protected class CmFileSelectAction extends Action {

		public CmFileSelectAction() {
			setText("Select CM");
			setToolTipText("Select CM action tooltip");
			setImageDescriptor(PlatformUI.getWorkbench().getSharedImages()
					.getImageDescriptor(ISharedImages.IMG_OBJ_FOLDER));
		}

		public void run() {
			// File standard dialog
			FileDialog fileDialog = new FileDialog(myContents.getShell());
			// Set the text
			fileDialog.setText("Select File");
			// Set filter on .txt files
			fileDialog.setFilterExtensions(new String[] { "*.cm" });
			// Put in a readable name for the filter
			fileDialog.setFilterNames(new String[] { "CM Files(*.cm)" });
			// Open Dialog and save result of selection
			String selected = fileDialog.open();

			System.out.println(selected);

			Graph g;
			try {
				g = CmFilesOperations.getCMGraphML(selected);
				GraphManager.getInstance().addGraph(g);
				CmFilesOperations.writeCMGraphMLFile(g,
						selected.substring(0, selected.length() - 3));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		private String[] getDirectoryFiles(String cM_PATH) {
			return null;
		}
	}

	/**
	 * 
	 */
	protected class UpdateViewWithCoverageSessionAction extends Action {
		public UpdateViewWithCoverageSessionAction() {
			setText("update View With Coverage Session");
			setToolTipText("update View With Coverage Session  tooltip");
			setImageDescriptor(Tabuleta.imageDescriptorFromPlugin(
					Tabuleta.ID_PLUGIN, "icons/refresh.gif"));
		}

		public void run() {
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

				Graph g = new Graph();

				g.addColumn("id", String.class);
				g.addColumn("name", String.class);
				g.addColumn("degree", String.class);
				g.addColumn("type", String.class);

				Object[] array = escopo.toArray();
				IJavaProject project = null;
				if (array[0] instanceof IPackageFragmentRoot)
					project = ((IPackageFragmentRoot) array[0])
							.getJavaProject();

				Node root = g.addNode();
				root.set("name", project.getElementName());
				root.set("type", "Project");
				root.set("degree", "100");
				root.set("id", "project-" + project.getElementName());

				IPackageFragment[] fragments = null;
				try {
					fragments = project.getPackageFragments();
				} catch (JavaModelException e) {
					e.printStackTrace();
				}

				for (IPackageFragment fragment : fragments) {
					try {
						if (fragment.getKind() == IPackageFragmentRoot.K_SOURCE) {
							System.out.println("Package "+ fragment.getElementName());
							ICoverageNode node;
							node = (ICoverageNode) fragment.getAdapter(ICoverageNode.class);
							double ratio = 0.0;
							if (node == null) {
								continue;
							}
							ratio = node.getLineCounter().getCoveredRatio();
							Node pfNode = addCoverageNodeToGraph(g, node, root, "PackageFragment", String.valueOf(ratio));
							printICompilationUnitInfo(fragment, g, pfNode);
						}
					} catch (JavaModelException e) {
						e.printStackTrace();
					}
				}
//				for (IPackageFragmentRoot fragmentRoot : escopo) {
//					buildGraph(fragmentRoot, g, root);
//				}

				GraphManager.getInstance().addGraph(g);
			} else {
				showMessage("You must run test suite with EclEmma coverage mode first.");
			}
		}
	}

	private void printICompilationUnitInfo(IPackageFragment mypackage, Graph g, Node root)
			throws JavaModelException {
		for (ICompilationUnit unit : mypackage.getCompilationUnits()) {
			printCompilationUnitDetails(unit, g, root);
		}
	}
	
	private void printCompilationUnitDetails(ICompilationUnit unit, Graph g, Node root)
			throws JavaModelException {
		System.out.println("Source file " + unit.getElementName());
		
		ICoverageNode node = (ICoverageNode) unit.getAdapter(ICoverageNode.class);
		
		double ratio = 0.0;
		if (node == null) {
			return;
		}
		ratio = node.getLineCounter().getCoveredRatio();
		
		Node cuNode = addCoverageNodeToGraph(g, node, root, "CompilationUnit", String.valueOf(ratio));
		
		printIMethods(unit,g,cuNode);
	}

	private void printIMethods(ICompilationUnit unit, Graph g, Node root) throws JavaModelException {
		IType[] allTypes = unit.getAllTypes();
		for (IType type : allTypes) {
			printIMethodDetails(type, g, root);
		}
	}

	private void printIMethodDetails(IType type, Graph g, Node root) throws JavaModelException {
		IMethod[] methods = type.getMethods();
		for (IMethod method : methods) {

			System.out.println("Method name " + method.getElementName());
			System.out.println("Signature " + method.getSignature());
			System.out.println("Return Type " + method.getReturnType());
			
			ICoverageNode node;
			double ratio = 0.0;
			node = (ICoverageNode) method.getAdapter(ICoverageNode.class);
			if (node == null) {
				continue;
			}
			ratio = node.getLineCounter().getCoveredRatio();
			
			addCoverageNodeToGraph(g, node, root, "Method", String.valueOf(ratio));
		}
	}


	private void buildGraph(IPackageFragmentRoot fragmentRoot, Graph g,
			Node root) {
		IJavaElement[] packageElements = null;
		try {
			packageElements = fragmentRoot.getChildren();
		} catch (JavaModelException e) {
			e.printStackTrace();
			ProblemManager.reportException(e);
		}

		try {
			for (IJavaElement lNext : packageElements) {
				ICoverageNode node;
				if (supportedElement(lNext)) {
					node = (ICoverageNode) lNext
							.getAdapter(ICoverageNode.class);
					if (node != null) {
						double ratio = node.getLineCounter().getCoveredRatio();

						addCoverageNodeToGraph(g, node, root,
								"Anonymous Element", String.valueOf(ratio));
					}
				}
				// if it is a class or interface, get its members
				// and add them to the concern
				else if (supportedType(lNext)) {
					ICoverageNode classCovNode = (ICoverageNode) lNext
							.getAdapter(ICoverageNode.class);
					double ratio = classCovNode.getLineCounter()
							.getCoveredRatio();
					System.out.println("Class: " + classCovNode.getName());

					Node classNode = g.addNode();
					classNode.set("name", classCovNode.getName());
					classNode.set("type", "Class or Interface"
							+ lNext.getClass().getName());
					classNode.set("degree", String.valueOf(ratio));
					classNode.set("id", "element-" + classCovNode.getName());

					g.addEdge(root, classNode);

					final IField[] lFields = ((IType) lNext).getFields();
					final IMethod[] lMethods = ((IType) lNext).getMethods();
					ratio = 0.0;

					for (IField lField : lFields) {
						node = (ICoverageNode) lField
								.getAdapter(ICoverageNode.class);
						if (node != null)
							addCoverageNodeToGraph(g, node, classNode, "Field",
									"Not defined");
						else
							addCoverageNodeToGraph(g, null, classNode, "Field"
									+ lField.getElementName(), "Not defined");
					}

					for (IMethod lMethod : lMethods) {
						node = (ICoverageNode) lMethod
								.getAdapter(ICoverageNode.class);
						if (node != null) {
							ratio = node.getLineCounter().getCoveredRatio();
							addCoverageNodeToGraph(g, node, classNode,
									"Method", String.valueOf(ratio));
						} else
							addCoverageNodeToGraph(g, null, classNode, "Field"
									+ lMethod.getElementName(), "Not defined");
					}
				} else {
					// The element is not supported by ConcernMapper
				}
			}

		} catch (JavaModelException e1) {
			e1.printStackTrace();
			ProblemManager.reportException(e1);
		}
	}

	private Node addCoverageNodeToGraph(Graph g, ICoverageNode node,
			Node parent, String type, String degree) {
		System.out.println(type + ": " + node.getName());

		Node child = g.addNode();
		child.set("type", type);
		child.set("degree", degree);
		if (node != null) {
			child.set("name", node.getName());
			child.set("id", type + node.getName());
		} else {
			child.set("name", type);
			child.set("id", type);
		}

		g.addEdge(parent, child);
		
		return child;
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

	private class ResourceSelectionHistory extends SelectionHistory {

		/**
		 * @param editor
		 */
		public ResourceSelectionHistory(JavaEditor editor) {
			super(editor);
			// TODO Auto-generated constructor stub
		}

		protected Object restoreItemFromMemento(IMemento element) {
			return null;
		}

		protected void storeItemToMemento(Object item, IMemento element) {
		}
	}

	protected class CMItensSelectionDialog extends FilteredItemsSelectionDialog {

		private static final String DIALOG_SETTINGS = "FilteredResourcesSelectionDialogExampleSettings";

		private final ArrayList resources = new ArrayList();

		{
			generateRescourcesTestCases('A', 'C', 8, ""); //$NON-NLS-1$
			generateRescourcesTestCases('a', 'c', 4, ""); //$NON-NLS-1$
		}

		private void generateRescourcesTestCases(char startChar, char endChar,
				int length, String resource) {
			for (char ch = startChar; ch <= endChar; ch++) {
				String res = resource + String.valueOf(ch);
				if (length == res.length())
					resources.add(res);
				else if ((res.trim().length() % 2) == 0)
					generateRescourcesTestCases(
							Character.toUpperCase((char) (startChar + 1)),
							Character.toUpperCase((char) (endChar + 1)),
							length, res);
				else
					generateRescourcesTestCases(
							Character.toLowerCase((char) (startChar + 1)),
							Character.toLowerCase((char) (endChar + 1)),
							length, res);
			}
		}

		/**
		 * @param shell
		 * @param multi
		 */
		public CMItensSelectionDialog(Shell shell, boolean multi) {
			super(shell, multi);
			setTitle("CM File Selection Dialog");
			// setSelectionHistory(new ResourceSelectionHistory(null));
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.ui.dialogs.FilteredItemsSelectionDialog#
		 * createExtendedContentArea(org.eclipse.swt.widgets.Composite)
		 */
		@Override
		protected Control createExtendedContentArea(Composite parent) {
			return null;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.ui.dialogs.FilteredItemsSelectionDialog#getDialogSettings
		 * ()
		 */
		@Override
		protected IDialogSettings getDialogSettings() {
			IDialogSettings settings = Tabuleta.getDefault()
					.getDialogSettings().getSection(DIALOG_SETTINGS);
			if (settings == null) {
				settings = Tabuleta.getDefault().getDialogSettings()
						.addNewSection(DIALOG_SETTINGS);
			}
			return settings;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.ui.dialogs.FilteredItemsSelectionDialog#validateItem(
		 * java.lang.Object)
		 */
		@Override
		protected IStatus validateItem(Object item) {
			return Status.OK_STATUS;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.ui.dialogs.FilteredItemsSelectionDialog#createFilter()
		 */
		@Override
		protected ItemsFilter createFilter() {
			return new ItemsFilter() {
				public boolean matchItem(Object item) {
					return matches(item.toString());
				}

				public boolean isConsistentItem(Object item) {
					return true;
				}
			};
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.ui.dialogs.FilteredItemsSelectionDialog#getItemsComparator
		 * ()
		 */
		@Override
		protected Comparator getItemsComparator() {
			return new Comparator() {
				public int compare(Object arg0, Object arg1) {
					return arg0.toString().compareTo(arg1.toString());
				}
			};
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.ui.dialogs.FilteredItemsSelectionDialog#fillContentProvider
		 * (org.eclipse.ui.dialogs.FilteredItemsSelectionDialog.
		 * AbstractContentProvider,
		 * org.eclipse.ui.dialogs.FilteredItemsSelectionDialog.ItemsFilter,
		 * org.eclipse.core.runtime.IProgressMonitor)
		 */
		@Override
		protected void fillContentProvider(
				AbstractContentProvider contentProvider,
				ItemsFilter itemsFilter, IProgressMonitor progressMonitor)
				throws CoreException {

			progressMonitor.beginTask("Searching", resources.size()); //$NON-NLS-1$
			for (Iterator iter = resources.iterator(); iter.hasNext();) {
				contentProvider.add(iter.next(), itemsFilter);
				progressMonitor.worked(1);
			}
			progressMonitor.done();

		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.ui.dialogs.FilteredItemsSelectionDialog#getElementName
		 * (java.lang.Object)
		 */
		@Override
		public String getElementName(Object item) {
			return item.toString();
		}

	}

	protected class GraphsViewer {

		public void refresh() {
			refresh();
		}
	}
}
