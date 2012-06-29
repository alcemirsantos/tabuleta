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

/**
 * A node representing a concern in the ConcernMapperView.
 */
public class ConcernNode extends AbstractElementNode 
{
	private String aConcernName;
	
	/**
	 * Build a new node.
	 * @param pConcernName The name of the concern represented by this node.
	 */
	public ConcernNode( String pConcernName )
	{
		aConcernName = pConcernName;
	}
	
	/**
	 * @return The name of the concern represented by this node.
	 */
	public String getConcernName()
	{
		return aConcernName;
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 * @param pObject See above.
	 * @return See above.
	 */
	public boolean equals( Object pObject ) 
	{
		if( this == pObject ) 
		{
			return true;
		}
		if( pObject == null )
		{
			return false;
		}
		if( getClass() != pObject.getClass() )
		{
			return false;
		}
		return aConcernName.equals( ((ConcernNode)pObject).aConcernName );
	}

	/**
	 * @see java.lang.Object#hashCode()
	 * @return See above.
	 */
	public int hashCode() 
	{
		return aConcernName.hashCode();
	}
}
