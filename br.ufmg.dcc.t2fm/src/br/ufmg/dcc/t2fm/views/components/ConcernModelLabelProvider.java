/* ConcernMapper - A concern modeling plug-in for Eclipse
 * Copyright (C) 2006  McGill University (http://www.cs.mcgill.ca/~martin/cm)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * $Revision: 1.12 $
 */

package br.ufmg.dcc.t2fm.views.components;

import org.eclipse.jdt.ui.JavaElementLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

/**
 * Provides the image and text labels for elemens in a concern
 * model.
 */
public class ConcernModelLabelProvider extends LabelProvider
{
    private int aFlags = JavaElementLabelProvider.SHOW_SMALL_ICONS | JavaElementLabelProvider.SHOW_PARAMETERS;
	private JavaElementLabelProvider aProvider = new JavaElementLabelProvider( aFlags );
	
	/**
	 * Provides the text for an object in a concern model.
	 * @param pObject The object to provide the text for.
	 * @return The text label
	 */
	public String getText( Object pObject )
	{
		String lReturn = null;
		if( pObject instanceof JavaElementNode )
		{
		   lReturn = aProvider.getText( ((JavaElementNode)pObject).getElement());
		}
		else if( pObject instanceof ConcernNode )
		{
		    lReturn = ((ConcernNode)pObject).getConcernName();
		}
		return lReturn;
	}
	
	/**
	 * Provides the image for an object in a concern model.
	 * @param pObject The object to provide the image for.
	 * @return The image
	 */
	public Image getImage( Object pObject )
	{
		Image lReturn = null;
		if( pObject instanceof JavaElementNode )
		{
			if( ((JavaElementNode)pObject).getElement().exists())
			{
				lReturn = aProvider.getImage( ((WrapperNode)pObject).getElement());
			}
			else
			{
				lReturn = PlatformUI.getWorkbench().getSharedImages().getImage( ISharedImages.IMG_OBJS_WARN_TSK );
			}
		}
		else if( pObject instanceof ConcernNode )
		{
		    lReturn = PlatformUI.getWorkbench().getSharedImages().getImage( ISharedImages.IMG_OBJ_ELEMENT );
		}
		return lReturn;
	}
}
