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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.util.HashSet;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.eclipse.albireo.core.SwingControl;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
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
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;
import org.eclipse.ui.part.ViewPart;
import org.jacoco.core.analysis.ICoverageNode;

import prefuse.Visualization;
import prefuse.controls.ControlAdapter;
import prefuse.data.Graph;
import prefuse.data.Node;
import prefuse.data.Table;
import prefuse.data.query.SearchQueryBinding;
import prefuse.data.search.SearchTupleSet;
import prefuse.util.FontLib;
import prefuse.util.ui.JFastLabel;
import prefuse.util.ui.JSearchPanel;
import prefuse.util.ui.JValueSlider;
import prefuse.util.ui.UILib;
import prefuse.visual.VisualItem;
import br.ufmg.dcc.tabuleta.Tabuleta;
import br.ufmg.dcc.tabuleta.actions.util.CmFilesOperations;
import br.ufmg.dcc.tabuleta.ui.ProblemManager;
import br.ufmg.dcc.tabuleta.views.components.GraphManager;
import br.ufmg.dcc.tabuleta.views.components.Starburst;
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
public class SunburstView extends ViewPart {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "br.ufmg.dcc.tabuleta.views.Sunburst";

	private Composite parent;
	private static Composite myContents;

	private static GraphsViewer graphsViewer;

	private Action selectCMAction;
	private Action updateViewWithCoverageSession;
	private Action updateViewWithAPreviousGraph;

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
		lbl.setText("No graphs to display at this time.");

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
		manager.add(updateViewWithCoverageSession);
		manager.add(updateViewWithAPreviousGraph);
	}

	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(selectCMAction);
		manager.add(updateViewWithCoverageSession);
		manager.add(updateViewWithAPreviousGraph);
	}

	/**
	 * 
	 */
	private void makeActions() {
		selectCMAction = new CmFileSelectAction();
		updateViewWithCoverageSession = new UpdateViewWithCoverageSessionAction();
		updateViewWithAPreviousGraph = new UptadeViewWithPreviousGraphs();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	@Override
	public void setFocus() {}

	public static void refresh() {
		for (Control con : myContents.getChildren()) {
			con.dispose();
		}

		getSunburstVisualization();
	}

	protected static SwingControl getSunburstVisualization(){
		SwingControl sc = new SwingControl(myContents, SWT.NONE) {
			
			@Override
			public Composite getLayoutAncestor() {
				return myContents;
			}
			
			@Override
			protected JComponent createSwingComponent() {
				final String label = "name";
				String treeNodes = "tree.nodes";
				Graph g = GraphManager.getInstance().getActiveGraph();
				
				final Starburst gview = new Starburst(g, label);
				final Visualization vis = gview.getVisualization();
				
				// create a search panel for the tree map
				SearchQueryBinding sq = new SearchQueryBinding((Table) vis
						.getGroup(treeNodes), label, (SearchTupleSet) vis
						.getGroup(Visualization.SEARCH_ITEMS));
				JSearchPanel search = sq.createSearchPanel();
				search.setShowResultCount(true);
				search.setBorder(BorderFactory.createEmptyBorder(5, 5, 4, 0));
				search.setFont(FontLib.getFont("Tahoma", Font.PLAIN, 11));

				final JFastLabel title = new JFastLabel("                 ");
				title.setPreferredSize(new Dimension(350, 20));
				title.setVerticalAlignment(SwingConstants.BOTTOM);
				title.setBorder(BorderFactory.createEmptyBorder(3, 0, 0, 0));
				title.setFont(FontLib.getFont("Tahoma", Font.PLAIN, 16));

				gview.addControlListener(new ControlAdapter() {
					public void itemEntered(VisualItem item, MouseEvent e) {
						if (item.canGetString(label))
							title.setText(item.getString(label));
					}

					public void itemExited(VisualItem item, MouseEvent e) {
						title.setText(null);
					}
				});

				JCheckBox resizeCheckBox = new JCheckBox("auto zoom");
				resizeCheckBox.setBorder(BorderFactory.createEmptyBorder(5,5,4,0));
				resizeCheckBox.setFont(FontLib.getFont("Tahoma", Font.PLAIN, 12));
				resizeCheckBox.setSelected(true);
				resizeCheckBox.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						gview.setAutoResize(((JCheckBox)e.getSource()).isSelected());
					}
				});
				
				JValueSlider filterLevelSlider = new JValueSlider("Filter level", 1, 20, 1);
				filterLevelSlider.addChangeListener(new ChangeListener() {
					public void stateChanged(ChangeEvent e) {
						gview.setFilterLevel(((JValueSlider)e.getSource()).getValue().intValue());
					}
				});
				
				Box box = new Box(BoxLayout.X_AXIS);
				box.add(Box.createHorizontalStrut(10));
				box.add(title);
				box.add(Box.createHorizontalGlue());
				box.add(resizeCheckBox);
				box.add(Box.createHorizontalStrut(30));
				box.add(filterLevelSlider);
				box.add(Box.createHorizontalStrut(30));
				box.add(search);
				box.add(Box.createHorizontalStrut(3));

				JPanel panel = new JPanel(new BorderLayout());
				panel.add(gview, BorderLayout.CENTER);
				panel.add(box, BorderLayout.SOUTH);

				Color BACKGROUND = Color.WHITE;
				Color FOREGROUND = Color.DARK_GRAY;
				UILib.setColor(panel, BACKGROUND, FOREGROUND);
				
//				addComboBox(panel);
				return panel;
			}
			
//		    private void addComboBox(JComponent panel) {
//		        // Many choices, to test what happens when the combobox
//		    	// does not fit into the AWT part.
//		    	final String[] comboboxChoices =
//		    			new String[] {       
//		    			"Color Option 1",
//		    			"Color Optiom 2",
//		    			"Color Option 3",
//		    			"Color Option 4",
//		    			"Color Option 5",
//		    			"Color Option 6",
//		    	};
//
//				final JComboBox combobox1 =
//		            new JComboBox(comboboxChoices);
//		        combobox1.addActionListener(
//		            new ActionListener() {
//		                public void actionPerformed(ActionEvent event) {
//		                    System.out.println("combobox1 now selected: "+combobox1.getSelectedItem());
//		                }
//		            });
//		        combobox1.addItemListener(
//		            new ItemListener() {
//		                public void itemStateChanged(ItemEvent event) {
//		                    System.out.println("combobox1 sent "+event);
//		                }
//		            });
//		        panel.add(combobox1,BorderLayout.NORTH);
//
//		    }
		};
		return sc;
	}
	private void showMessage(String message) {
		MessageDialog.openInformation(myContents.getShell(),
				"Sunburst View", message);
	}

	/**
	 * @return
	 */
	public static GraphsViewer getGraphsViewer() {
		return graphsViewer;
	}

	/**
	 * This viewer helps to update the SunBurst. 
	 */
	public class GraphsViewer {
	
		public void update() {
			refresh();
		}
	}

	/**
	 * This action builds the SunBurst from a .cm file selected in a FileDialog. 
	 */
	protected class CmFileSelectAction extends Action {

		/**
		 * Constructor
		 */
		public CmFileSelectAction() {
			setText("Select CM");
			setToolTipText("Select CM to update the SunBurst view.");
			setImageDescriptor(PlatformUI.getWorkbench().getSharedImages()
					.getImageDescriptor(ISharedImages.IMG_OBJ_FOLDER));
		}

		/**
		 * Exexutes the action
		 */
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
				e.printStackTrace();
				ProblemManager.reportException(e);
			}
		}

		private String[] getDirectoryFiles(String cM_PATH) {
			return null;
		}
	}

	/**
	 * This action retrieves the coverage information from Eclemma to build the SunBurst. 
	 */
	protected class UpdateViewWithCoverageSessionAction extends Action {
		/**
		 * Constructor
		 */
		public UpdateViewWithCoverageSessionAction() {
			setText("Update View With Coverage Session");
			setToolTipText("Update the view with EclEmma Coverage Session information");
			setImageDescriptor(Tabuleta.imageDescriptorFromPlugin(
					Tabuleta.ID_PLUGIN, "icons/refresh.gif"));
		}

		/**
		 * Executes the action
		 */
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

				System.gc();
				
				Graph g = new Graph();

				g.addColumn("id", String.class);
				g.addColumn("name", String.class);
				g.addColumn("degree", Double.class);
				g.addColumn("type", String.class);

				Object[] array = escopo.toArray();
				IJavaProject project = null;
				if (array[0] instanceof IPackageFragmentRoot)
					project = ((IPackageFragmentRoot) array[0])
							.getJavaProject();

				Node root = g.addNode();
				root.set("name", project.getElementName());
				root.set("type", "Project");
				root.set("degree", 100.0);
				root.set("id", "project-" + project.getElementName());

				IPackageFragment[] fragments = null;
				try {
					fragments = project.getPackageFragments();
				} catch (JavaModelException e) {
					e.printStackTrace();
				}

				for (IPackageFragment fragment : fragments) {
					if (!isSourcePath( fragment.getAncestor(IJavaElement.PACKAGE_FRAGMENT).getPath())) {
						// avoid to add the test source folder in the graph.
						break;
					}
					try {
						String lastPackage = "";
						if (fragment.getKind() == IPackageFragmentRoot.K_SOURCE) {
							String fragmentName = fragment.getPath().lastSegment();
							System.out.println(fragmentName);
							String[] pakkages = fragment.getElementName().split(".");
							Node pfNode;
							ICoverageNode node = (ICoverageNode) fragment.getAdapter(ICoverageNode.class);
							if (node == null) {
								if (fragmentName.isEmpty()) {
									pfNode = addCoverageNodeToGraph(g, "<default>", root, "PackageFragment", 0.0);	
								}else{
									pfNode = addCoverageNodeToGraph(g, fragmentName, root, "PackageFragment", 0.0);
								}
								root = pfNode;										
								continue;
							}else{
								Double ratio = node.getLineCounter().getCoveredRatio();
								pfNode = addCoverageNodeToGraph(g, fragmentName, root, "PackageFragment", ratio);
							}
							printICompilationUnitInfo(fragment, g, pfNode);
						}
					} catch (JavaModelException e) {
						e.printStackTrace();
					}
				}

				GraphManager.getInstance().addGraph(g);
			} else {
				showMessage("You must run test suite with EclEmma coverage mode first.");
			}
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
		private void printICompilationUnitInfo(IPackageFragment mypackage, Graph g, Node root)
				throws JavaModelException {
			for (ICompilationUnit unit : mypackage.getCompilationUnits()) {
				printCompilationUnitDetails(unit, g, root);
			}
		}
		
		private void printCompilationUnitDetails(ICompilationUnit unit, Graph g, Node root)
				throws JavaModelException {
			
			ICoverageNode node = (ICoverageNode) unit.getAdapter(ICoverageNode.class);
			
			if (node == null) {
				return;
			}
			Double ratio = node.getLineCounter().getCoveredRatio();
			Node cuNode = addCoverageNodeToGraph(g, node, root, "CompilationUnit", ratio);
			
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
				
				ICoverageNode node;
				node = (ICoverageNode) method.getAdapter(ICoverageNode.class);
				if (node == null) {
					continue;
				}
				Double ratio = node.getLineCounter().getCoveredRatio();			
				addCoverageNodeToGraph(g, node, root, "Method", ratio);
			}
		}
		
		private Node addCoverageNodeToGraph(Graph g, ICoverageNode node,
				Node parent, String type, Double degree) {
			
			Node child = g.addNode();
			child.set("type", type);
			child.set("degree", degree);
			child.set("name", node.getName());
			child.set("id", type + node.getName());
			
			g.addEdge(parent, child);
			
			return child;
		}
		private Node addCoverageNodeToGraph(Graph g, String nodeName, Node parent, String type, Double degree) {
			
			Node child = g.addNode();
			child.set("type", type);
			child.set("degree", degree);
			child.set("name", nodeName);
			child.set("id", type + nodeName);
			
			g.addEdge(parent, child);
			
			return child;
		}
	}
	
	protected class UptadeViewWithPreviousGraphs extends Action{
		/**
		 * Constructor 
		 */
		public UptadeViewWithPreviousGraphs() {
			setText("Update View With Previous Graphs");
			setToolTipText("Select a previous graph to see.");
			setImageDescriptor(PlatformUI.getWorkbench().getSharedImages()
					.getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
		}
		
		/**
		 * Executes the action
		 */
		public void run(){
			String graphChoosed = showGrahpsList();
			GraphManager.getInstance().setActiveGraph(graphChoosed);
		}
		
		private String showGrahpsList(){
			ElementListSelectionDialog dialog = 
					new ElementListSelectionDialog(
							Display.getCurrent().getActiveShell(),
							new LabelProvider());
			String[] graphs = GraphManager.getInstance().getGraphsIDs().toArray(new String[0]);
			dialog.setElements(graphs);
			dialog.setTitle("What graph do you want exhibit?");
			// enquanto o usuário não disser qual é o concern
			while (dialog.open() != Window.OK){
				// TODO revover loop para o caso de clicar em cancel.
				CmFilesOperations.showMessage("Choose a Graph",
						"You must select a graph you want to exhibit.");
			}
			Object[] result = dialog.getResult();
			return (String)result[0];
		}
		
	}
	
}
