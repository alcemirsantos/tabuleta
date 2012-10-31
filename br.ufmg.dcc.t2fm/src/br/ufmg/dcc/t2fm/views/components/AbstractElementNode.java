/* ConcernMapper - A concern modeling plug-in for Eclipse
 * Copyright (C) 2006  McGill University (http://www.cs.mcgill.ca/~martin/cm)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * $Revision: 1.2 $
 */

package br.ufmg.dcc.t2fm.views.components;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A bundle to facilitate the handling of elements in the 
 * ConcernModelContentProvider.
 */
public abstract class AbstractElementNode implements IConcernMapperViewNode
{
	private List<IConcernMapperViewNode> aChildren;			
	private Object aParent = null;			
	
	/**
	 * Creates a new ElementNode.
	 */
	protected AbstractElementNode()
	{
		aChildren = new ArrayList<IConcernMapperViewNode>();
		aParent = new Object();
	}
	
	/**
	 * @return All the element nodes that are children of this node.
	 */
	public Object[] getChildren()
	{
		return aChildren.toArray();
	}
	
	/**
	 * @see ca.mcgill.cs.serg.cm.views.IConcernMapperViewNode#getConcern()
	 * @return See above.
	 */
	public String getConcern()
	{
		IConcernMapperViewNode lNode = this;
		while( !(lNode instanceof ConcernNode ))
		{
			lNode = (IConcernMapperViewNode)lNode.getParent();
		}
		return ((ConcernNode)lNode).getConcernName();
			
	}
	
	/**
	 * @return The parent of this node.
	 */
	public Object getParent()
	{
		return aParent;
	}
	
	/**
	 * Sets the parent for this node.
	 * @param pParent The parent of this node.
	 */
	public void setParent( Object pParent )
	{
		aParent = pParent;
	}
	
	/**
	 * Add a child to this node.
	 * @param pChild The child node to add.
	 */
	public void addChild( IConcernMapperViewNode pChild )
	{
		aChildren.add( pChild );
	}
	
	/**
	 * Build and return a list of all the element nodes that are direct
	 * or indirect children of thtis node, including this node.
	 * @return This object plus all its transitive children. 
	 */
	public Set<IConcernMapperViewNode> getAllDescendants()
	{
		Set<IConcernMapperViewNode> lReturn = new HashSet<IConcernMapperViewNode>();
		lReturn.add( this );
		if( hasChildren() )
		{
			for( int lI = 0 ; lI < aChildren.size(); lI++ )
			{
				lReturn.addAll( aChildren.get( lI ).getAllDescendants() );
			}
		}
		return lReturn;
	}
	
	/**
	 * @return Whether this nodes has children.
	 */
	public boolean hasChildren()
	{
		return aChildren.size() > 0;
	}
}
