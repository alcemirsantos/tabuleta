/* ConcernMapper - A concern modeling plug-in for Eclipse
 * Copyright (C) 2006  McGill University (http://www.cs.mcgill.ca/~martin/cm)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * $Revision: 1.4 $
 */

package br.ufmg.dcc.t2fm.views.components;

import java.util.Set;

/**
 * Describes the behavior of a node in the ConcernMapperView.
 */
public interface IConcernMapperViewNode 
{
	/**
	 * @return The children of this node.
	 */
	Object[] getChildren();
	
	/**
	 * @return The parent of this node.
	 */
	Object getParent();
	
	/**
	 * @return True if this node has children, false otherwise.
	 */
	boolean hasChildren();
	
	/**
	 * @return All the nodes that are this node or one of its descendants.
	 */
	Set<IConcernMapperViewNode> getAllDescendants();
	
	/**
	 * Sets the parent for the node.
	 * @param pParent The new parent.
	 */
	void setParent( Object pParent );
	
	/**
	 * Adds a child node.
	 * @param pChild The child node to add.
	 */
	void addChild( IConcernMapperViewNode pChild );
	
	/**
	 * @return The name of the concern associated with this node.
	 */
	String getConcern();
}
