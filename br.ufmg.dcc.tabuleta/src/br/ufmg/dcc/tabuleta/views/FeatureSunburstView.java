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

import javax.swing.JComponent;

import org.eclipse.albireo.core.SwingControl;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.ui.javaeditor.JavaEditor;
import org.eclipse.jdt.internal.ui.javaeditor.selectionactions.SelectionHistory;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;
import org.eclipse.ui.dialogs.FilteredItemsSelectionDialog;
import org.eclipse.ui.part.ViewPart;

import com.mountainminds.eclemma.core.CoverageTools;
import com.mountainminds.eclemma.core.ICoverageSession;
import com.mountainminds.eclemma.core.ScopeUtils;

import prefuse.data.Graph;
import br.ufmg.dcc.tabuleta.Tabuleta;
import br.ufmg.dcc.tabuleta.actions.util.CmFilesOperations;
import br.ufmg.dcc.tabuleta.views.components.GraphManager;
import ca.utoronto.cs.prefuseextensions.demo.StarburstDemo;

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
	private Action updateCMViewWithCoverageSession;

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
		manager.add(new Separator());
	}

	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(selectCMAction);
	}

	/**
	 * 
	 */
	private void makeActions() {
		selectCMAction = new CmFileSelectAction();
		selectCMAction.setText("Select CM");
		selectCMAction.setToolTipText("Select CM action tooltip");
		selectCMAction.setImageDescriptor(PlatformUI.getWorkbench()
				.getSharedImages()
				.getImageDescriptor(ISharedImages.IMG_OBJ_FOLDER));
		
		updateCMViewWithCoverageSession = new UpdateViewWithCoverageSessionAction();

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
				CmFilesOperations.writeCMGraphMLFile(g, selected.substring(0, selected.length()-3));
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
		public void run(){
			boolean thereIsActiveCoverageSession = false;
			
			ICoverageSession activeSession = CoverageTools.getSessionManager().getActiveSession();
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

				// TODO create the graph from the coverage.
				
				
				
			}else{
				showMessage("You must run the generated suite test class with EclEmma " +
						"coverage tool before save the .cm file.");
			}

		}

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

		   private void generateRescourcesTestCases(char startChar, char endChar, int length, String resource){
		      for (char ch = startChar; ch <= endChar; ch++) {
		         String res = resource + String.valueOf(ch);
		         if (length == res.length()) 
		            resources.add(res);
		         else if ((res.trim().length() % 2) == 0)
		            generateRescourcesTestCases(Character.toUpperCase((char)(startChar + 1)), Character.toUpperCase((char)(endChar + 1)), length, res);
		         else 
		            generateRescourcesTestCases(Character.toLowerCase((char)(startChar + 1)), Character.toLowerCase((char)(endChar + 1)), length, res);
		      }
		   }
		   
		/**
		 * @param shell
		 * @param multi
		 */
		public CMItensSelectionDialog(Shell shell, boolean multi) {
			super(shell, multi);
			setTitle("CM File Selection Dialog");
//			setSelectionHistory(new ResourceSelectionHistory(null));
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
