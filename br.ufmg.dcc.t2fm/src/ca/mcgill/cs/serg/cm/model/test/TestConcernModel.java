/* ConcernMapper - A concern modeling plug-in for Eclipse
 * Copyright (C) 2006  McGill University (http://www.cs.mcgill.ca/~martin/cm)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * $Revision: 1.32 $
 */

package ca.mcgill.cs.serg.cm.model.test;

import java.util.Set;

import ca.mcgill.cs.serg.cm.model.ConcernModel;
import ca.mcgill.cs.serg.cm.model.ConcernModelChangeListener;
import ca.mcgill.cs.serg.cm.model.ConcernModelException;
import junit.framework.TestCase;

/**
 * Performs unit tests for the ConcernModel class.
 * CSOFF: EmptyBlockCheck
 * CSOFF: MagicNumberCheck
 * CSOFF: VisibilityModifierCheck
 */
public class TestConcernModel extends TestCase
{
	private ConcernModel aModel;
	private String aElement1;
	private String aElement2;
	private String aElement3;
	private String aElement4;
	
	/**
	 * @see junit.framework.TestCase#setUp()
	 * @throws Exception an exception
	 */
	protected void setUp() throws Exception 
	{
		aModel = new ConcernModel();
		aElement1 = "Element 1";
		aElement2 = "Element 2";
		aElement3 = "Element 3";
		aElement4 = "Element 4";
	}
	
	/**
	 * @see junit.framework.TestCase#tearDown()
	 * @throws Exception an exception
	 */
	protected void tearDown() throws Exception
	{}
	
	/**
	 * Tests resetting the model to an empty model.
	 */
	public void testReset()
	{
		aModel.newConcern( "Concern1" );
		aModel.newConcern( "Concern2" );
		aModel.reset();
		String[] lConcerns = aModel.getConcernNames();
		assertEquals( 0, lConcerns.length );
	}
	
	/**
	 * Tests the creation of a new concern.
	 */
	public void testNewConcern()
	{
		try
		{
			aModel.newConcern( null );
			fail( "Exception expected" );
		}
		catch( ConcernModelException lException )
		{}
		try
		{
			aModel.newConcern( "" );
			fail( "Exception expected" );
		}
		catch( ConcernModelException lException )
		{}
		
		aModel.newConcern( "Concern1" );
		assertTrue( aModel.exists( "Concern1" ));
		aModel.newConcern( "Concern2" );
		assertTrue( aModel.exists( "Concern1" ));
		assertTrue( aModel.exists( "Concern2" ));
		
		try
		{
			aModel.newConcern( "Concern1" );
			fail( "Exception expected" );
		}
		catch( ConcernModelException lException )
		{}
	}
	
	/**
	 * Tests renaming a concern.
	 */
	public void testRenameConcern()
	{
		aModel.newConcern( "Concern1" );
		aModel.newConcern( "Concern2" );
		aModel.newConcern( "Concern3" );
		aModel.addElement( "Concern1", aElement1, 100 );
		aModel.addElement( "Concern1", aElement2, 100 );
		aModel.addElement( "Concern2", aElement1, 100 );
		aModel.addElement( "Concern2", aElement2, 100 );
		aModel.addElement( "Concern3", aElement1, 100 );
		aModel.addElement( "Concern3", aElement2, 100 );
		try
		{
			aModel.renameConcern( "Foo", "Bar" );
			fail( "Exception expected" );
		}
		catch( ConcernModelException lException )
		{}
		aModel.renameConcern( "Concern1", "NewConcern" );
		String[] lConcerns = aModel.getConcernNames();
		assertEquals( 3, lConcerns.length );
		assertTrue( aModel.exists( "NewConcern"));
		assertTrue( aModel.exists( "Concern2"));
		assertTrue( aModel.exists( "Concern3"));
		assertFalse( aModel.exists( "Concern1"));
		Set<Object> lElements = aModel.getElements( "NewConcern" );
		assertEquals( 2, lElements.size() );
		assertTrue( lElements.contains( aElement1 ));
		assertTrue( lElements.contains( aElement2 ));
	}
	
	/**
	 * Tests the implementation of the observer pattern.
	 */
	public void testObserver()
	{
		/**
		 * Convenience class for testing the observer pattern.
		 */
		class ObserverTester implements ConcernModelChangeListener
		{
			public int aModelChanged = 0;
			
			/**
			 * @see ca.mcgill.cs.serg.cm.model.ConcernModelChangeListener#modelChanged(int)
			 * @param pChange The type of change to the model. See the 
			 * constants in ConcernModel
			 */
			public void modelChanged( int pChange )
			{
				aModelChanged++;
			}
		}
		
		ObserverTester lTester1 = new ObserverTester();
		ObserverTester lTester2 = new ObserverTester();
		aModel.addListener( lTester1 );
		aModel.newConcern( "test1");
		assertTrue( lTester1.aModelChanged == 1 );
		aModel.addListener( lTester2 );
		aModel.newConcern( "test2");
		assertTrue( lTester1.aModelChanged == 2 );
		assertTrue( lTester2.aModelChanged == 1 );
		aModel.removeListener( lTester1 );
		aModel.newConcern( "test3");
		assertTrue( lTester1.aModelChanged == 2 );
		assertTrue( lTester2.aModelChanged == 2 );
		aModel.removeListener( lTester2 );
		aModel.removeListener( lTester2 );
		aModel.newConcern( "test4");
		assertTrue( lTester1.aModelChanged == 2 );
		assertTrue( lTester2.aModelChanged == 2 );
		
		aModel.addListener( lTester1 );
		aModel.addListener( lTester2 );
		String lFoo = "Foo";
		aModel.addElement( "test1", lFoo, 100 );
		assertEquals( 3, lTester1.aModelChanged );
		assertEquals( 3, lTester2.aModelChanged );
		
		// Test changes using the parameterized model changed
		aModel.setDegree( "test1", lFoo, 50);
		assertEquals( 4, lTester1.aModelChanged );
		assertEquals( 4, lTester2.aModelChanged );
		
		// And one go with streaming on.
		aModel.startStreaming();
		aModel.setDegree( "test1", lFoo, 75 );
		assertEquals( 4, lTester1.aModelChanged );
		assertEquals( 4, lTester2.aModelChanged );
		aModel.stopStreaming( true );
		assertEquals( 5, lTester1.aModelChanged );
		assertEquals( 5, lTester2.aModelChanged );
	}

	/**
	 * Tests the streaming optimization.
	 */
	public void testStreaming()
	{
		/**
		 * Convenience class for testing the observer pattern.
		 */
		class ObserverTester implements ConcernModelChangeListener
		{
			public int aModelChanged = 0;
			
			/**
			 * @see ca.mcgill.cs.serg.cm.model.ConcernModelChangeListener#modelChanged(int)
			 * @param pChange The type of change to the model. See the 
			 * constants in ConcernModel
			 */
			public void modelChanged( int pChange )
			{
				aModelChanged++;
			}
		}
		
		ObserverTester lTester1 = new ObserverTester();
		aModel.addListener( lTester1 );
		aModel.newConcern( "test1");
		assertTrue( lTester1.aModelChanged == 1 );
		aModel.startStreaming();
		aModel.newConcern( "test2");
		aModel.newConcern( "test3");
		assertTrue( lTester1.aModelChanged == 1 );
		aModel.stopStreaming();
		assertEquals( 2, lTester1.aModelChanged );
		
		// Test without the change notification
		lTester1.aModelChanged = 0;
		aModel.startStreaming();
		aModel.newConcern( "test4" );
		aModel.newConcern( "test5" );
		assertEquals( 0, lTester1.aModelChanged );
		aModel.stopStreaming( false );
		assertEquals( 0, lTester1.aModelChanged );
		aModel.startStreaming();
		aModel.newConcern( "test6" );
		assertEquals( 0, lTester1.aModelChanged );
		aModel.stopStreaming( true );
		assertEquals( 1, lTester1.aModelChanged );
		
		// Test with no observers
		aModel.removeListener( lTester1 );
		lTester1.aModelChanged = 0;
		aModel.reset();
		aModel.startStreaming();
		aModel.newConcern( "test1" );
		aModel.newConcern( "test2" );
		assertEquals( 0, lTester1.aModelChanged );
		aModel.stopStreaming( true );
		assertEquals( 0, lTester1.aModelChanged );
	}
	
	/**
	 * Tests the getting of concern names.
	 */
	public void testGetConcernNames()
	{
	    String[] lNames = aModel.getConcernNames();
	    assertEquals( 0, lNames.length );
	    aModel.newConcern( "Concern 1");
	    lNames = aModel.getConcernNames();
	    assertEquals( 1, lNames.length );
	    assertEquals( "Concern 1", lNames[0]);
	    aModel.newConcern( "Concern 2");
	    lNames = aModel.getConcernNames();
	    assertEquals( 2, lNames.length );
	}
	
	/**
	 * Tests the test of concern existence.
	 */
	public void testExists()
	{
		assertFalse( aModel.exists( "Concern1", aElement1 ));
		aModel.newConcern( "Concern1");
		aModel.addElement( "Concern1", aElement1, 100);
		assertTrue( aModel.exists( "Concern1", aElement1 ));
		aModel.newConcern( "Concern2");
		aModel.addElement( "Concern2", aElement1, 100);
		assertTrue( aModel.exists( "Concern2", aElement1 ));
		aModel.addElement( "Concern2", aElement2, 100);
		assertTrue( aModel.exists( "Concern2", aElement2 ));
	}
	
	/**
	 * Test adding and getting elements.
	 */
	public void testAddGetElements()
	{
	    // Normal operations
	    aModel.newConcern( "Concern1");
	    Set<Object> lElements = aModel.getElements( "Concern1");
	    assertEquals( 0, lElements.size() );
	    aModel.addElement( "Concern1", aElement1, 50 );
	    lElements = aModel.getElements( "Concern1" );
	    assertEquals( 1, lElements.size() );
	    assertTrue( lElements.contains( aElement1 ));
	    assertEquals( 50, aModel.getDegree( "Concern1", aElement1 ));
	    aModel.addElement( "Concern1", aElement2, 75 );
	    aModel.addElement( "Concern1", aElement3, 100 );
	    lElements = aModel.getElements( "Concern1" );
	    assertEquals( 3, lElements.size() );
	    assertTrue( lElements.contains( aElement1 ));
	    assertTrue( lElements.contains( aElement2 ));
	    assertTrue( lElements.contains( aElement3 ));
	    assertEquals( 50, aModel.getDegree( "Concern1", aElement1 ));
	    assertEquals( 75, aModel.getDegree( "Concern1", aElement2 ));
	    assertEquals( 100, aModel.getDegree( "Concern1", aElement3 ));
	    
	    // Attempting to do add an existing element
	    try
		{
	        aModel.addElement( "Concern1", aElement1, 100 );
			fail( "Exception expected");
		}
		catch( ConcernModelException lException )
		{}
		// Attempting to add/retrieve from an non-existing concern
		try
		{
		    aModel.addElement( "Concern2", aElement1, 100 );
			fail( "Exception expected");
		}
		catch( ConcernModelException lException )
		{}
		try
		{
		    aModel.getElements( "Concern2" );
			fail( "Exception expected");
		}
		catch( ConcernModelException lException )
		{}
		
		// Adding with invalid degree 
		try
		{
		    aModel.addElement( "Concern1", aElement4, -1 );
			fail( "Exception expected");
		}
		catch( ConcernModelException lException )
		{}
		try
		{
		    aModel.addElement( "Concern1", aElement4, 101 );
			fail( "Exception expected");
		}
		catch( ConcernModelException lException )
		{}
	}
	
	/**
	 * Test adding and getting elements.
	 */
	public void testAddGetElementsWithComments()
	{
	    // Normal operations
	    aModel.newConcern( "Concern1");
	    Set<Object> lElements = aModel.getElements( "Concern1");
	    assertEquals( 0, lElements.size() );
	    aModel.addElement( "Concern1", aElement1, 50 );
	    aModel.setElementComment( "Concern1", aElement1, "Comment1");
	    lElements = aModel.getElements( "Concern1" );
	    assertEquals( 1, lElements.size() );
	    assertTrue( lElements.contains( aElement1 ));
	    assertEquals( "Comment1", aModel.getElementComment( "Concern1", aElement1 ));
	    aModel.addElement( "Concern1", aElement2, 75 );
	    aModel.addElement( "Concern1", aElement3, 100 );
	    aModel.setElementComment( "Concern1", aElement3, "Comment3");
	    lElements = aModel.getElements( "Concern1" );
	    assertEquals( 3, lElements.size() );
	    assertTrue( lElements.contains( aElement1 ));
	    assertTrue( lElements.contains( aElement2 ));
	    assertTrue( lElements.contains( aElement3 ));
	    assertEquals( "Comment1", aModel.getElementComment( "Concern1", aElement1 ));
	    assertEquals( "", aModel.getElementComment( "Concern1", aElement2 ));
	    assertEquals( "Comment3", aModel.getElementComment( "Concern1", aElement3 ));
	    
	    // Attempting to do add an existing element
	    try
		{
	        aModel.addElement( "Concern1", aElement1, 100 );
	        aModel.setElementComment( "Concern1", aElement1, "Foo");
			fail( "Exception expected");
		}
		catch( ConcernModelException lException )
		{}
		// Attempting to add/retrieve from an non-existing concern
		try
		{
		    aModel.addElement( "Concern2", aElement1, 100 );
		    aModel.setElementComment( "Concern2", aElement1, "foo");
			fail( "Exception expected");
		}
		catch( ConcernModelException lException )
		{}
		try
		{
		    aModel.getElements( "Concern2" );
			fail( "Exception expected");
		}
		catch( ConcernModelException lException )
		{}
		
	}

	/**
	 * Test adding and getting elements.
	 */
	public void testConcernsWithComments()
	{
	    // Normal operations
	    aModel.newConcern( "Concern1");
	    assertEquals( "", aModel.getConcernComment( "Concern1" ));
	    aModel.newConcern( "Concern2");
	    assertEquals( "", aModel.getConcernComment( "Concern1" ));
	    assertEquals( "", aModel.getConcernComment( "Concern2" ));
	    
	    aModel.renameConcern( "Concern1", "Concern1a");
	    assertEquals( "", aModel.getConcernComment( "Concern1a" ));
	    
	    aModel.newConcern( "Concern3");
	    aModel.setConcernComment( "Concern3", "This is concern 3" );
	    assertEquals( "This is concern 3", aModel.getConcernComment( "Concern3" ));
	    
	    aModel.setConcernComment( "Concern1a", "Zig zig");
	    assertEquals( "Zig zig", aModel.getConcernComment( "Concern1a" ));
	}
	
	/**
	 * Test adding using the default add method, and getting elements.
	 */
	public void testDefaultAddGetElements()
	{
	    // Normal operations
	    aModel.newConcern( "Concern1");
	    Set<Object> lElements = aModel.getElements( "Concern1");
	    assertEquals( 0, lElements.size() );
	    aModel.addElement( "Concern1", aElement1);
	    lElements = aModel.getElements( "Concern1" );
	    assertEquals( 1, lElements.size() );
	    assertTrue( lElements.contains( aElement1 ));
	    assertEquals( 100, aModel.getDegree( "Concern1", aElement1 ));
	    aModel.addElement( "Concern1", aElement2 );
	    aModel.addElement( "Concern1", aElement3 );
	    lElements = aModel.getElements( "Concern1" );
	    assertEquals( 3, lElements.size() );
	    assertTrue( lElements.contains( aElement1 ));
	    assertTrue( lElements.contains( aElement2 ));
	    assertTrue( lElements.contains( aElement3 ));
	    assertEquals( 100, aModel.getDegree( "Concern1", aElement1 ));
	    assertEquals( 100, aModel.getDegree( "Concern1", aElement2 ));
	    assertEquals( 100, aModel.getDegree( "Concern1", aElement3 ));
	}
	
	/**
	 * Tests getting the comments from a concern.
	 */
	public void testGetCommentConcern()
	{
		aModel.newConcern( "Concern1" );
		aModel.addElement( "Concern1", aElement1, 100);
		assertEquals( 100, aModel.getDegree( "Concern1", aElement1 ));
		assertEquals( 0, aModel.getDegree( "Concern1", aElement2 ));
		
		try
		{
		    aModel.getDegree( "Concern2", aElement1 );
			fail( "Exception expected");
		}
		catch( ConcernModelException lException )
		{}
	}
	
	/**
	 * Tests getting the comments from an element.
	 */
	public void testGetCommentElement()
	{
		aModel.newConcern( "Concern1" );
		aModel.addElement( "Concern1", aElement1, 100);
		assertEquals( 100, aModel.getDegree( "Concern1", aElement1 ));
		assertEquals( 0, aModel.getDegree( "Concern1", aElement2 ));
		
		try
		{
		    aModel.getDegree( "Concern2", aElement1 );
			fail( "Exception expected");
		}
		catch( ConcernModelException lException )
		{}
	}

	/**
	 * Tests the exception handling in getDegree().
	 */
	public void testGetDegree()
	{
		aModel.newConcern( "Concern1" );
		aModel.addElement( "Concern1", aElement1, 100);
		assertEquals( 100, aModel.getDegree( "Concern1", aElement1 ));
		assertEquals( 0, aModel.getDegree( "Concern1", aElement2 ));
		
		try
		{
		    aModel.getDegree( "Concern2", aElement1 );
			fail( "Exception expected");
		}
		catch( ConcernModelException lException )
		{}
	}
	
	/**
	 * Tests concern deletion.
	 */
	public void testDeleteConcern()
	{
		try
		{
		    aModel.deleteConcern( "Concern1" );
			fail( "Exception expected");
		}
		catch( ConcernModelException lException )
		{}
		aModel.newConcern( "Concern1" );
		aModel.deleteConcern( "Concern1");
		String[] lConcerns = aModel.getConcernNames();
		assertEquals( 0, lConcerns.length );
		aModel.newConcern( "Concern1");
		aModel.addElement( "Concern1", aElement1, 100 );
		aModel.addElement( "Concern1", aElement2, 100 );
		aModel.newConcern( "Concern2");
		aModel.addElement( "Concern2", aElement1, 100 );
		aModel.addElement( "Concern2", aElement2, 100 );
		aModel.newConcern( "Concern3");
		aModel.deleteConcern( "Concern2" );
		lConcerns = aModel.getConcernNames();
		assertEquals( 2, lConcerns.length );
		aModel.deleteConcern( "Concern3" );
		lConcerns = aModel.getConcernNames();
		assertEquals( 1, lConcerns.length );
		aModel.deleteConcern( "Concern1" );
		lConcerns = aModel.getConcernNames();
		assertEquals( 0, lConcerns.length );
	}
	
	/**
	 * Tests the deletion of individual elements.
	 */
	public void testRemoveElement()
	{
		try
		{
		    aModel.deleteConcern( "Concern1" );
			fail( "Exception expected");
		}
		catch( ConcernModelException lException )
		{}
		aModel.newConcern( "Concern1");
		try
		{
		    aModel.deleteElement( "Concern1", aElement1 );
			fail( "Exception expected");
		}
		catch( ConcernModelException lException )
		{}
		aModel.addElement( "Concern1", aElement1, 100);
		aModel.addElement( "Concern1", aElement2, 100);
		aModel.newConcern( "Concern2");
		aModel.addElement( "Concern2", aElement1, 100);
		aModel.addElement( "Concern2", aElement2, 100);
		aModel.deleteElement( "Concern1", aElement1 );
		Set<Object> lElements1 = aModel.getElements( "Concern1" );
		assertEquals( 1, lElements1.size() );
		assertTrue( lElements1.contains( aElement2 ));
		Set<Object> lElements2 = aModel.getElements( "Concern2" );
		assertEquals( 2, lElements2.size() );
		assertTrue( lElements2.contains( aElement2 ));
		assertTrue( lElements2.contains( aElement2 ));
		aModel.deleteElement( "Concern1", aElement2 );
		lElements1 = aModel.getElements( "Concern1" );
		assertEquals( 0, lElements1.size() );
		lElements2 = aModel.getElements( "Concern2" );
		assertEquals( 2, lElements2.size() );
		assertTrue( lElements2.contains( aElement2 ));
		assertTrue( lElements2.contains( aElement2 ));
	}
	
	/**
	 * Tests the setting of the degree of an element.
	 */
	public void testSetDegree()
	{
		try
		{
		    aModel.setDegree( "Concern1", aElement1, 100 );
			fail( "Exception expected");
		}
		catch( ConcernModelException lException )
		{}
		aModel.newConcern( "Concern1");
		try
		{
		    aModel.setDegree( "Concern1", aElement1, 100 );
			fail( "Exception expected");
		}
		catch( ConcernModelException lException )
		{}
		aModel.addElement( "Concern1", aElement1, 25 );
		aModel.addElement( "Concern1", aElement2, 50 );
		aModel.addElement( "Concern1", aElement3, 75 );
		aModel.addElement( "Concern1", aElement4, 100 );
		try
		{
		    aModel.setDegree( "Concern1", aElement1, 101 );
			fail( "Exception expected");
		}
		catch( ConcernModelException lException )
		{}
		aModel.setDegree( "Concern1", aElement1, 0);
		assertEquals( 0, aModel.getDegree( "Concern1", aElement1 ));
		aModel.setDegree( "Concern1", aElement1, 25);
		assertEquals( 25, aModel.getDegree( "Concern1", aElement1 ));
		aModel.setDegree( "Concern1", aElement1, 75);
		assertEquals( 75, aModel.getDegree( "Concern1", aElement1 ));
	}
	
	/**
	 * Tests the setting of the degree of an element.
	 */
	public void testSetComment()
	{
		try
		{
		    aModel.setElementComment( "Concern1", aElement1, "Oops" );
			fail( "Exception expected");
		}
		catch( ConcernModelException lException )
		{}
		aModel.newConcern( "Concern1");
		try
		{
		    aModel.setElementComment( "Concern1", aElement1, "Oops" );
			fail( "Exception expected");
		}
		catch( ConcernModelException lException )
		{}
		aModel.addElement( "Concern1", aElement1, 25 );
		aModel.addElement( "Concern1", aElement2, 50 );
		aModel.addElement( "Concern1", aElement3, 75 );
		aModel.addElement( "Concern1", aElement4, 100 );
		
		aModel.setDegree( "Concern1", aElement1, 100 );
		assertEquals( "", aModel.getElementComment( "Concern1", aElement1 ));
		
		aModel.setElementComment( "Concern1", aElement1, "Foo");
		assertEquals( "Foo", aModel.getElementComment( "Concern1", aElement1 ));
		aModel.setElementComment( "Concern1", aElement1, "Baz");
		assertEquals( "Baz", aModel.getElementComment( "Concern1", aElement1 ));
		aModel.setElementComment( "Concern1", aElement1, "Bar");
		assertEquals( "Bar", aModel.getElementComment( "Concern1", aElement1 ));
	}
	
	/**
	 * Test the exception handling in the concern access methods.
	 */
	public void testElementAccessExceptions()
	{
		try
		{
			aModel.getAllElements( "Foowee" );
			fail();
		}
		catch( ConcernModelException lException )
		{}
		
		try
		{
			aModel.getElements( "Foowee" );
			fail();
		}
		catch( ConcernModelException lException )
		{}
	}
	
	// CSON: EmptyBlockCheck
	// CSON: MagicNumberCheck
	// CSON: VisibilityModifierCheck
}
