/* ConcernMapper - A concern modeling plug-in for Eclipse
 * Copyright (C) 2006  McGill University (http://www.cs.mcgill.ca/~martin/cm)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * $Revision: 1.16 $
 */

package ca.mcgill.cs.serg.cm.views;

import java.util.Set;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import ca.mcgill.cs.serg.cm.ConcernMapper;
import ca.mcgill.cs.serg.cm.ui.ConcernMapperPreferencePage;

/**
 * A filter for the TreeViewer in the ConcernMapperView.
 * The filter should only be applied if ConcernMapperPreferencePage.P_FILTER_ENABLED is set to true.
 */
class ConcernMapperFilter extends ViewerFilter
{
	/**
	 * Applies a filtering algorithm to the elements in the ConcernMapperView.
	 * @see org.eclipse.jface.viewers.ViewerFilter#select(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
	 * @param pViewer the viewer
     * @param pParentElement the parent element
     * @param pElement the element
     * @return <code>true</code> if element is included in the
     *   filtered set, and <code>false</code> if excluded
	 */
	public boolean select( Viewer pViewer, Object pParentElement, Object pElement )
	{
		boolean lReturn = false;
		int lThreshold = ConcernMapper.getDefault().getPreferenceStore().getInt( ConcernMapperPreferencePage.P_FILTER_THRESHOLD );
		if( pElement instanceof WrapperNode )
		{
			WrapperNode lNode = (WrapperNode) pElement;
			if( ConcernMapper.getDefault().getConcernModel().exists( lNode.getConcern(), lNode.getElement() ))
			{
				if( ConcernMapper.getDefault().getConcernModel().getDegree( lNode.getConcern(), lNode.getElement() ) > lThreshold )
				{
					lReturn = true;
				}
			}
			
			Set<IConcernMapperViewNode> lDescendants = lNode.getAllDescendants();
			lDescendants.remove( lNode );
			for( IConcernMapperViewNode lNext : lDescendants )
			{
				assert lNext instanceof WrapperNode;
				if( ConcernMapper.getDefault().getConcernModel().exists( lNext.getConcern(), ((WrapperNode)lNext).getElement() ))
				{
					if(ConcernMapper.getDefault().getConcernModel().getDegree( lNext.getConcern(), ((WrapperNode)lNext).getElement()) > lThreshold )
					{
						lReturn = true;	
					}
				}
			}
			return lReturn;
		}
		return  true;
	}
}
