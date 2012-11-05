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
package br.ufmg.dcc.t2fm.views;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.DrillDownAdapter;
import org.eclipse.ui.part.ViewPart;

/**
 * @author Alcemir R. Santos
 * 
 */
public class CMFilesView extends ViewPart {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "br.ufmg.dcc.t2fm.views.CMFilesView";

	private TreeViewer treeViewer;
	private DrillDownAdapter drillDownAdapter;

	/**
	 * 
	 */
	public CMFilesView() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createPartControl(Composite parent) {
		GridLayout lLayout = new GridLayout();
		lLayout.numColumns = 1;
		parent.setLayout(lLayout);
		parent.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		createCMFilesViewer(parent);
		// TODO createContributions();
		// TODO createContextMenu();
		// TODO createToolbarButtons();
		// TODO createViewPulldownMenu();
		hookMouse();
	}

	/**
	 * Create the treeViewer to show the .cm files.
	 * 
	 * @param parent
	 */
	private void createCMFilesViewer(Composite parent) {
		treeViewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL 	| SWT.V_SCROLL);
		treeViewer.setContentProvider(new TreeViewContentProvider());
		treeViewer.setLabelProvider(new TreeViewLabelProvider());
		drillDownAdapter = new DrillDownAdapter(treeViewer);
		treeViewer.setInput(getViewSite());
		

		GridData lGridData = new GridData();
		lGridData.verticalAlignment = GridData.FILL;
		lGridData.horizontalAlignment = GridData.FILL;
		lGridData.grabExcessHorizontalSpace = true;
		lGridData.grabExcessVerticalSpace = true;

		treeViewer.getControl().setLayoutData(lGridData);

		treeViewer.setSorter(new NameSorter());

		getSite().setSelectionProvider(treeViewer);
	}

	
	/**
	 * Adds a listener to open an editor with a Double Click in a .cm file.
	 */
	private void hookMouse() {
		treeViewer.getTree().addMouseListener(new MouseAdapter() {
			public void mouseDoubleClick(MouseEvent e) {
				openEditor(getSite().getPage(), treeViewer.getSelection());
			}
		});
	}

	/**
	 * Open an editor on the first selected element
	 * 
	 * @param page
	 *            the page in which the editor should be opened
	 * @param selection
	 *            the selection containing the object to be edited
	 */
	public void openEditor(IWorkbenchPage page, ISelection selection) {

		// Get the first element.

		if (!(selection instanceof IStructuredSelection))
			return;
		Iterator<?> iter = ((IStructuredSelection) selection).iterator();
		if (!iter.hasNext())
			return;
		Object elem = iter.next();
		// Adapt the first element to a file.

		if (!(elem instanceof IAdaptable))
			return;

		IFile file = (IFile) ((IAdaptable) elem).getAdapter(IFile.class);
		if (file == null)
			return;

		// Open an editor on that file.

		try {
			IDE.openEditor(page, file);
		} catch (PartInitException e) {
			System.err.println("Open editor failed: " + file.toString());
			System.err.println(e.getMessage());
		}
	}

	/**
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

	/*
	 * The content provider class is responsible for
	 * providing objects to the view. It can wrap
	 * existing objects in adapters or simply return
	 * objects as-is. These objects may be sensitive
	 * to the current input of the view, or ignore
	 * it and always show the same content 
	 * (like Task List, for example).
	 */
	 
	class TreeObject implements IAdaptable {
		private String name;
		private TreeParent parent;
		
		public TreeObject(String name) {
			this.name = name;
		}
		public String getName() {
			return name;
		}
		public void setParent(TreeParent parent) {
			this.parent = parent;
		}
		public TreeParent getParent() {
			return parent;
		}
		public String toString() {
			return getName();
		}
		public Object getAdapter(Class key) {
			return null;
		}
	}
	
	class TreeParent extends TreeObject {
		private ArrayList children;
		public TreeParent(String name) {
			super(name);
			children = new ArrayList();
		}
		public void addChild(TreeObject child) {
			children.add(child);
			child.setParent(this);
		}
		public void removeChild(TreeObject child) {
			children.remove(child);
			child.setParent(null);
		}
		public TreeObject [] getChildren() {
			return (TreeObject [])children.toArray(new TreeObject[children.size()]);
		}
		public boolean hasChildren() {
			return children.size()>0;
		}
	}
	
	class TreeViewContentProvider implements IStructuredContentProvider,
			ITreeContentProvider {
		private TreeParent invisibleRoot;

		public void inputChanged(Viewer v, Object oldInput, Object newInput) {
		}

		public void dispose() {
		}

		public Object[] getElements(Object parent) {
			if (parent.equals(getViewSite())) {
				if (invisibleRoot == null)
					initialize();
				return getChildren(invisibleRoot);
			}
			return getChildren(parent);
		}

		public Object getParent(Object child) {
			if (child instanceof TreeObject) {
				return ((TreeObject) child).getParent();
			}
			return null;
		}

		public Object[] getChildren(Object parent) {
			if (parent instanceof TreeParent) {
				return ((TreeParent) parent).getChildren();
			}
			return new Object[0];
		}

		public boolean hasChildren(Object parent) {
			if (parent instanceof TreeParent)
				return ((TreeParent) parent).hasChildren();
			return false;
		}

		/*
		 * We will set up a dummy model to initialize tree heararchy. In a real
		 * code, you will connect to a real model and expose its hierarchy.
		 */
		private void initialize() {
			TreeObject to1 = new TreeObject("Leaf 1");
			TreeObject to2 = new TreeObject("Leaf 2");
			TreeObject to3 = new TreeObject("Leaf 3");
			TreeParent p1 = new TreeParent("Parent 1");
			p1.addChild(to1);
			p1.addChild(to2);
			p1.addChild(to3);

			TreeObject to4 = new TreeObject("Leaf 4");
			TreeParent p2 = new TreeParent("Parent 2");
			p2.addChild(to4);

			TreeParent root = new TreeParent("Root");
			root.addChild(p1);
			root.addChild(p2);

			invisibleRoot = new TreeParent("");
			invisibleRoot.addChild(root);
		}
	}

	class TreeViewLabelProvider extends LabelProvider {

		public String getText(Object obj) {
			return obj.toString();
		}
		public Image getImage(Object obj) {
			String imageKey = ISharedImages.IMG_OBJ_ELEMENT;
			if (obj instanceof TreeParent)
			   imageKey = ISharedImages.IMG_OBJ_FOLDER;
			return PlatformUI.getWorkbench().getSharedImages().getImage(imageKey);
		}
	}

	class NameSorter extends ViewerSorter {
	}
	
}
