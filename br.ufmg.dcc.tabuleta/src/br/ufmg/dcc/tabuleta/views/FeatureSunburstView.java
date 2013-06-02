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

import javax.swing.JComponent;
import javax.swing.JFrame;

import org.eclipse.albireo.core.SwingControl;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import ca.utoronto.cs.prefuseextensions.demo.StarburstDemo;

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
import prefuse.util.ui.UILib;
import prefuse.visual.VisualItem;

/**
 * This class is responsible to draw a a Sunburst of the test classes associated with the features in concern model.
 * @author Alcemir R. Santos
 * 
 */
public class FeatureSunburstView extends ViewPart {


	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createPartControl( final Composite parent) {
		
		SwingControl swingControl = new SwingControl(parent, SWT.NONE) {
            protected JComponent createSwingComponent() {
        		
        		return StarburstDemo.demo();
            }

            public Composite getLayoutAncestor() {
                return parent;
            }
            
        };
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

}
