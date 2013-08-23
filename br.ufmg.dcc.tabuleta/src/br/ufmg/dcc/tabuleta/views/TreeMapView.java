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

import java.util.HashSet;

import org.eclipse.jdt.core.ICompilationUnit;
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
import org.eclipse.jface.text.Document;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.part.ViewPart;
import org.jacoco.core.analysis.ICoverageNode;

import br.ufmg.dcc.tabuleta.Tabuleta;
import br.ufmg.dcc.tabuleta.ui.ProblemManager;
import br.ufmg.dcc.tabuleta.views.components.TreeMapNode;

import com.mountainminds.eclemma.core.CoverageTools;
import com.mountainminds.eclemma.core.ICoverageSession;
import com.mountainminds.eclemma.core.ScopeUtils;

import de.engehausen.treemap.IColorProvider;
import de.engehausen.treemap.ILabelProvider;
import de.engehausen.treemap.IRectangle;
import de.engehausen.treemap.ISelectionChangeListener;
import de.engehausen.treemap.ITreeModel;
import de.engehausen.treemap.impl.GenericTreeModel;
import de.engehausen.treemap.impl.SquarifiedLayout;
import de.engehausen.treemap.swt.TreeMap;
import de.engehausen.treemap.swt.impl.CushionRectangleRendererEx;
import de.engehausen.treemap.swt.impl.DefaultColorProvider;

/**
 * @author Alcemir R. Santos
 * 
 */
public class TreeMapView extends ViewPart implements
		ISelectionChangeListener<TreeMapNode>, ILabelProvider<TreeMapNode>,
		SelectionListener {
	public TreeMapView() {
	}

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "br.dcc.ufba.tabuleta.views.TreeMapView";

	protected TreeMap<TreeMapNode> treeMap;
	protected GenericTreeModel<TreeMapNode> aModel;
	
	protected Label selectionTitle;
	private Action lineCoverageTreeMapAction;
	private Action branchCoverageTreeMapAction;

	private int coverageType;


	/**
	 * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createPartControl(Composite parent) {
		GridLayout gLayout = new GridLayout();
		gLayout.numColumns = 1;
		gLayout.marginLeft = -2;
		gLayout.marginRight = -2;
		gLayout.marginHeight = 0;
		gLayout.marginBottom = 0;
		gLayout.marginTop = 0;
		gLayout.horizontalSpacing = 0;
		gLayout.verticalSpacing = 0;
		parent.setLayout(gLayout);

		treeMap = new TreeMap<TreeMapNode>(parent);
		RGB[] myColors =  	new RGB[]{
				new RGB(255,255,255),
				new RGB(255,255,102),
				new RGB(215,255,89),
				new RGB(154,255,84),
				new RGB(106,255,0),
				new RGB(0,204,0),
				new RGB(0,153,0)
			};
		treeMap.setColorProvider(new MyColorProvider(Display.getCurrent(), myColors));
		treeMap.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		treeMap.setTreeMapLayout(new SquarifiedLayout<TreeMapNode>(16));
		treeMap.setRectangleRenderer(new CushionRectangleRendererEx<TreeMapNode>(1)); // TODO colorir com cobertura
		treeMap.addSelectionChangeListener(this);
		treeMap.setLabelProvider(this);

		selectionTitle = new Label(parent, SWT.LEFT);
		selectionTitle.setText("Nenhum treeMap a ser exibido.");

		
		makeActions();
		contributeToActionBars();
		hookDoubleClickAction();
	}
	
	private class MyColorProvider extends DefaultColorProvider<TreeMapNode> {

	/**
	 * @param device
	 * @param colorArray
	 */
	public MyColorProvider(Device device, RGB[] colorArray) {
		super(device, colorArray);
	}

	/* (non-Javadoc)
	 * @see de.engehausen.treemap.IColorProvider#getColor(de.engehausen.treemap.ITreeModel, de.engehausen.treemap.IRectangle)
	 */
	@Override
	public Color getColor(ITreeModel<IRectangle<TreeMapNode>> model,
			IRectangle<TreeMapNode> rectangle) {
		Color result = null;
		long key = aModel.getWeight(rectangle.getNode());

		if (key>=0 && key<10) {
			result = colors[0];
		}else if (key>=10 && key<20) {
			result = colors[1];
		}else if (key>=20 && key<40) {
			result = colors[2];
		}else if (key>=40 && key<60) {
			result = colors[3];
		}else if (key>=60 && key<80) {
			result = colors[4];
		}else if (key>=80 && key<95) {
			result = colors[5];
		}else if (key>=95 && key<=100) {
			result = colors[6];
		}	
		return result;
	}
	
}
	/**
	 * 
	 */
	private void buildTreeMap() {
		boolean thereIsActiveCoverageSession = false;
		ICoverageSession activeSession = CoverageTools.getSessionManager()
				.getActiveSession();
		thereIsActiveCoverageSession = activeSession == null ? false : true;

		HashSet<IPackageFragmentRoot> escopo = null;

		aModel = new GenericTreeModel<TreeMapNode>();
		
		// se existe uma sessão ativa
		if (thereIsActiveCoverageSession) {
			// getting the scope
			try {
				escopo = (HashSet<IPackageFragmentRoot>) ScopeUtils
						.filterJREEntries(activeSession.getScope());
			} catch (JavaModelException e) {
				ProblemManager.reportException(e);
			}

			// getting the project in scope
			Object[] array = escopo.toArray();
			IJavaProject project = null;
			if (array[0] instanceof IPackageFragmentRoot) {
				project = ((IPackageFragmentRoot) array[0]).getJavaProject();
			}

			// init the tree model
			TreeMapNode rootNode = new TreeMapNode(project.getElementName(),
					project.getPath().toString(),
					project.getPath().toString());
			aModel.add(rootNode, 0, null);

			// adding elements to the model
			try {
				printPackageInfos(project, rootNode);
				treeMap.setTreeModel(aModel);
				treeMap.redraw();
			} catch (JavaModelException e1) {
				ProblemManager.reportException(e1);
			}
		} else {
			showMessage("You must run a test suite with EclEmma coverage mode first.");
		}
	}

	private void printPackageInfos(IJavaProject javaProject, TreeMapNode root)
			throws JavaModelException {
		IPackageFragment[] packages = javaProject.getPackageFragments();
		for (IPackageFragment mypackage : packages) {
			/*
			 * Package fragments include all packages in the classpath We will
			 * only look at the package from the source folder K_BINARY would
			 * include also included JARS, e.g rt.jar
			 */
			if (mypackage.getKind() == IPackageFragmentRoot.K_SOURCE) {
//				System.out.println("Package " + mypackage.getElementName());
				printICompilationUnitInfo(mypackage, root);
			}
		}
	}

	private void printICompilationUnitInfo(IPackageFragment mypackage, TreeMapNode root)
			throws JavaModelException {
		for (ICompilationUnit unit : mypackage.getCompilationUnits()) {
			printCompilationUnitDetails(unit, root);
		}
	}

	private void printCompilationUnitDetails(ICompilationUnit unit, TreeMapNode root)
			throws JavaModelException {
//		System.out.println("Source file " + unit.getElementName());
		Document doc = new Document(unit.getSource());
		int loc = doc.getNumberOfLines();
//		System.out.println("Has number of lines: " + loc);
		TreeMapNode tNode = new TreeMapNode(unit.getElementName(), 
				"Unidade: "+unit.getElementName()+"\n"+
				"LOC: "+loc, 
				unit.getPath().toString());
		ICoverageNode cNode = (ICoverageNode) unit.getAdapter(ICoverageNode.class);
		if(cNode==null)return;
		long ratio = getCoveredRatio(cNode);
		aModel.add(tNode, ratio, root);
		printIMethods(unit, tNode);
	}
	
	
	
	private void printIMethods(ICompilationUnit unit, TreeMapNode root) throws JavaModelException {
		IType[] allTypes = unit.getAllTypes();
		for (IType type : allTypes) {
			printIMethodDetails(type, root);
		}
	}

	private void printIMethodDetails(IType type, TreeMapNode root) throws JavaModelException {
		IMethod[] methods = type.getMethods();
		TreeMapNode aNode;
		for (IMethod method : methods) {

//			System.out.println("Method name " + method.getElementName());
//			System.out.println("Signature " + method.getSignature());
//			System.out.println("Return Type " + method.getReturnType());
			
			ICoverageNode cNode = (ICoverageNode) method.getAdapter(ICoverageNode.class);
			if(cNode == null) continue;
			long ratio = getCoveredRatio(cNode);
			
			aNode = new TreeMapNode(method.getElementName(),
					"Class: "+ root.getid()+"\n"+
					"Method: "+ method.getElementName()+"\n"+
					"Return Type: " + method.getReturnType()+"\n"+
					"Number of Parameters: " + method.getNumberOfParameters()+"\n"+
					"Coverage Ratio (%): "+ratio,
					method.getPath().toString());
			aModel.add(aNode, ratio, root);

		}
	}

	/**
	 * returns the coverage ratio depending of the type requested.
	 * <p>
	 * Types: <br> Line coverage: 1 <br> Branch coverage: 2
	 * @param type
	 * @return
	 */
	private long getCoveredRatio(ICoverageNode node){
		switch (coverageType) {
		case 1: // user choose coverage of lines 
			System.out.println(node.getLineCounter().getCoveredRatio()*100);
			return (long) (node.getLineCounter().getCoveredRatio()*100);
		case 2: // user choose coverage of branches
			return (long) (node.getBranchCounter().getCoveredRatio()*100);
		default:
			break;
		}
		return 0;
	}
	
	/**
	 * 
	 */
	private void hookDoubleClickAction() {
	}

	/**
	 * 
	 */
	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	/**
	 * 
	 * @param manager
	 */
	private void fillLocalPullDown(IMenuManager manager) {
		manager.add(lineCoverageTreeMapAction);
		manager.add(branchCoverageTreeMapAction);
		manager.add(new Separator());
	}

	/**
	 * 
	 * @param manager
	 */
	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(lineCoverageTreeMapAction);
		manager.add(branchCoverageTreeMapAction);
	}

	/**
	 * 
	 */
	private void makeActions() {
		lineCoverageTreeMapAction = new Action() {
			public void run() {
				coverageType = 1;
				buildTreeMap();
			}
		};
		lineCoverageTreeMapAction.setText("Show line coverage TreeMap");
		lineCoverageTreeMapAction.setImageDescriptor(Tabuleta.imageDescriptorFromPlugin(
				Tabuleta.ID_PLUGIN, "icons/lines-coverage.png"));
		lineCoverageTreeMapAction.setToolTipText("Update the view with Lines Coverage TreeMap");
		
		branchCoverageTreeMapAction = new Action() {
			public void run() {
				coverageType = 2;
				buildTreeMap();
			}
		};
		branchCoverageTreeMapAction.setText("Show branch coverage TreeMap");
		branchCoverageTreeMapAction.setImageDescriptor(Tabuleta.imageDescriptorFromPlugin(
				Tabuleta.ID_PLUGIN, "icons/branch-coverage.png"));
		branchCoverageTreeMapAction.setToolTipText("Update the view with Branches Coverage TreeMap");
		
	}

	/**
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	@Override
	public void setFocus() {
	}

	private void showMessage(String message) {
		MessageDialog.openInformation(getViewSite().getShell(), "TreeMap",
				message);
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
	 */
	@Override
	public void widgetSelected(SelectionEvent e) {
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent)
	 */
	@Override
	public void widgetDefaultSelected(SelectionEvent e) {
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see de.engehausen.treemap.ILabelProvider#getLabel(de.engehausen.treemap.ITreeModel,
	 *      de.engehausen.treemap.IRectangle)
	 */
	@Override
	public String getLabel(ITreeModel<IRectangle<TreeMapNode>> model,
			IRectangle<TreeMapNode> rectangle) {
		return rectangle.getNode().getid();
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see de.engehausen.treemap.ISelectionChangeListener#selectionChanged(de.engehausen.treemap.ITreeModel,
	 *      de.engehausen.treemap.IRectangle, java.lang.String)
	 */
	@Override
	public void selectionChanged(ITreeModel<IRectangle<TreeMapNode>> model,
			IRectangle<TreeMapNode> rectangle, String text) {
		if (text != null) {
			selectionTitle.setText(rectangle.getNode().getPath());
			selectionTitle.pack();
			selectionTitle.update();
			final TreeMapNode info = rectangle.getNode();
			treeMap.setToolTipText(info.getdesc());
		}

	}
}
