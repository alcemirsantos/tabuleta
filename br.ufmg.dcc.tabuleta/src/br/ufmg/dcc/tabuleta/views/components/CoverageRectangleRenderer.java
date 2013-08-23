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
package br.ufmg.dcc.tabuleta.views.components;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.Color;

import de.engehausen.treemap.IColorProvider;
import de.engehausen.treemap.ILabelProvider;
import de.engehausen.treemap.IRectangle;
import de.engehausen.treemap.IRectangleRenderer;
import de.engehausen.treemap.ITreeModel;

/**
 * @author Alcemir R. Santos
 *
 */
public class CoverageRectangleRenderer<N> implements IRectangleRenderer<N, PaintEvent, Color> {

	/**
	 * @param colorRangeSize
	 */
	public CoverageRectangleRenderer(int colorRangeSize){
		// TODO super(colorRangeSize);
	}

	/* (non-Javadoc)
	 * @see de.engehausen.treemap.IRectangleRenderer#render(java.lang.Object, de.engehausen.treemap.ITreeModel, de.engehausen.treemap.IRectangle, de.engehausen.treemap.IColorProvider, de.engehausen.treemap.ILabelProvider)
	 */
	@Override
	public void render(PaintEvent graphics, ITreeModel<IRectangle<N>> model,
			IRectangle<N> rectangle, IColorProvider<N, Color> colorProvider,
			ILabelProvider<N> labelProvider) {
		if (!model.hasChildren(rectangle)) {
			paintCushion(graphics, colorProvider.getColor(model, rectangle), rectangle);
		} else if (rectangle.equals(model.getRoot())) {
			// paint the whole background black			
			graphics.gc.setBackground(graphics.display.getSystemColor(SWT.COLOR_BLUE));
			graphics.gc.fillRectangle(rectangle.getX(), rectangle.getY(), rectangle.getWidth(), rectangle.getHeight());
		}
		
	}

	/**
	 * @param graphics
	 * @param color
	 * @param rectangle
	 */
	private void paintCushion(PaintEvent graphics, Color color,
			IRectangle<N> rectangle) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see de.engehausen.treemap.IRectangleRenderer#highlight(java.lang.Object, de.engehausen.treemap.ITreeModel, de.engehausen.treemap.IRectangle, de.engehausen.treemap.IColorProvider, de.engehausen.treemap.ILabelProvider)
	 */
	@Override
	public void highlight(PaintEvent graphics, ITreeModel<IRectangle<N>> model,
			IRectangle<N> rectangle, IColorProvider<N, Color> colorProvider,
			ILabelProvider<N> labelProvider) {
		graphics.gc.setForeground(graphics.display.getSystemColor(SWT.COLOR_RED));
		graphics.gc.drawRectangle(rectangle.getX(), rectangle.getY(), rectangle.getWidth()-1, rectangle.getHeight()-1);
		final IRectangle<N> root = model.getRoot();
		IRectangle<N> runner = rectangle, last;
		do {
			last = runner;
			runner = model.getParent(runner);
		} while (runner != root && runner != null);
		if (last != root) {
			graphics.gc.setForeground(graphics.display.getSystemColor(SWT.COLOR_YELLOW));
			graphics.gc.drawRectangle(last.getX(), last.getY(), last.getWidth()-1, last.getHeight()-1);			
		}

		
	}

}
