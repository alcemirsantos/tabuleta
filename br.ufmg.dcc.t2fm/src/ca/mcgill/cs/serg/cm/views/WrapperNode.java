/* ConcernMapper - A concern modeling plug-in for Eclipse
 * Copyright (C) 2006  McGill University (http://www.cs.mcgill.ca/~martin/cm)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * $Revision: 1.4 $
 */

package ca.mcgill.cs.serg.cm.views;

/**
 * A node that wraps an individual element in the ConcernMapperView.
 */
abstract class WrapperNode extends AbstractElementNode 
{
	private Object aWrapped;
	
	/**
	 * Create a new WrapperNode.
	 * @param pWrapped The element to be wrapped by this node.
	 */
	protected WrapperNode( Object pWrapped )
	{
		aWrapped = pWrapped;
	}
	
	/**
	 * @return The element wrapped by this node.
	 */
	public Object getElement()
	{
		return aWrapped;
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
		return ( aWrapped.equals( ((WrapperNode)pObject).aWrapped )) && ( getConcern().equals( ((WrapperNode)pObject).getConcern()) );
	}

	/**
	 * @see java.lang.Object#hashCode()
	 * @return See above.
	 */
	public int hashCode() 
	{
		return getConcern().hashCode() + aWrapped.hashCode();
	}
	
	/**
	 * @see java.lang.Object#toString()
	 * @return The string representation of the wrapped element.
	 */
	public String toString()
	{
		return aWrapped.toString();
	}
}
