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

import java.util.Random;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.eclipse.albireo.core.SwingControl;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import prefuse.Display;
import prefuse.Visualization;
import prefuse.action.ActionList;
import prefuse.action.RepaintAction;
import prefuse.action.assignment.ColorAction;
import prefuse.action.layout.RandomLayout;
import prefuse.controls.DragControl;
import prefuse.controls.PanControl;
import prefuse.controls.ZoomControl;
import prefuse.data.Graph;
import prefuse.render.DefaultRendererFactory;
import prefuse.render.ShapeRenderer;
import prefuse.util.ColorLib;
import prefuse.visual.VisualItem;

/**
 * This class is responsible to draw a a Sunburst of the test classes associated with the features in concern model.
 * @author Alcemir R. Santos
 * 
 */
public class FeatureSunburstView extends ViewPart {

	// the data
	private Graph graph = new Graph();
	// The Visualization
	private static Visualization vis;
	// The Display
	private static Display d;

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createPartControl( final Composite parent) {
		setUpData();
		setUpVisualization();
		setUpRenderers();
		setUpActions();
		setUpDisplay();
		
		SwingControl swingControl = new SwingControl(parent, SWT.NONE) {
            protected JComponent createSwingComponent() {
                return d;
            }

            public Composite getLayoutAncestor() {
                return parent;
            }
            
        };
        vis.run("color");
		vis.run("layout");
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

	public void method(){
		setUpData();
		setUpVisualization();
		setUpRenderers();
		setUpActions();
		setUpDisplay();

		// launch the visualization -------------------------------------

		// The following is standard java.awt.
		// A JFrame is the basic window element in awt.
		// It has a menu (minimize, maximize, close) and can hold
		// other gui elements.

		// Create a new window to hold the visualization.
		// We pass the text value to be displayed in the
		// menubar to the constructor.
		JFrame frame = new JFrame("prefuse example");

		// Ensure application exits when window is closed
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// The Display object (d) is a subclass of JComponent, which
		// can be added to JFrames with the add method.
		frame.add(d);

		// Prepares the window.
		frame.pack();

		// Shows the window.
		frame.setVisible(true);

		// We have to start the ActionLists that we
		// added to the visualization
		vis.run("color");
		vis.run("layout");
	}

	// -- 1. load the data --------------------------------------------
	public void setUpData() {
		// Here we are manually creating the data structures. 100 nodes are
		// added to the Graph structure. 100 edges are made randomly
		// between the nodes.
		int numNodes = 20;

		for (int i = 0; i < numNodes; i++)
			graph.addNode();

		Random rand = new Random();

		for (int i = 0; i < numNodes; i++) {
			int first = rand.nextInt(numNodes);
			int second = rand.nextInt(numNodes);
			graph.addEdge(first, second);
		}

	}

	// -- 2. the visualization -----------------------------------------
	public void setUpVisualization() {
		// Create the Visualization object.
		vis = new Visualization();

		// Now we add our previously created Graph object
		// to the visualization.
		// The graph gets a textual label so that we can refer
		// to it later on.
		vis.add("graph", graph);

	}

	// -- 3. the renderers and renderer factory -------------------------
	public static void setUpRenderers() {
		// Create a default ShapeRenderer
		ShapeRenderer r = new ShapeRenderer();

		// create a new DefaultRendererFactory
		// This Factory will use the ShapeRenderer for all nodes.
		vis.setRendererFactory(new DefaultRendererFactory(r));
	}

	// -- 4. the actions --------------------------------------
	public static void setUpActions() {
		// We must color the nodes of the graph.
		// Notice that we refer to the nodes using the text label for the graph,
		// and then appending ".nodes". The same will work for ".edges" when we
		// only want to access those items.
		// The ColorAction must know what to color, what aspect of those
		// items to color, and the color that should be used.
		ColorAction fill = new ColorAction("graph.nodes", VisualItem.FILLCOLOR,
				ColorLib.rgb(0, 200, 0));

		// Similarly to the node coloring, we use a ColorAction for the
		// edges
		ColorAction edges = new ColorAction("graph.edges",
				VisualItem.STROKECOLOR, ColorLib.gray(200));

		// Create an action list containing all color assignments
		// ActionLists are used for actions that will be executed
		// at the same time.
		ActionList color = new ActionList();
		color.add(fill);
		color.add(edges);

		// The layout ActionList recalculates
		// the positions of the nodes.
		ActionList layout = new ActionList();

		// We add the layout to the layout ActionList, and tell it
		// to operate on the "graph".
		layout.add(new RandomLayout("graph"));

		// We add a RepaintAction so that every time the layout is
		// changed, the Visualization updates it's screen.
		layout.add(new RepaintAction());

		// add the actions to the visualization
		vis.putAction("color", color);
		vis.putAction("layout", layout);

	}

	// -- 5. the display ----------------------------------------
	public static void setUpDisplay() {
		// Create the Display object, and pass it the visualization that it
		// will hold.
		d = new Display(vis);

		// Set the size of the display.
		d.setSize(720, 500);

		// We use the addControlListener method to set up interaction.

		// The DragControl is a built in class for manually moving
		// nodes with the mouse.
		d.addControlListener(new DragControl());
		// Pan with left-click drag on background
		d.addControlListener(new PanControl());
		// Zoom with right-click drag
		d.addControlListener(new ZoomControl());
	}

}
