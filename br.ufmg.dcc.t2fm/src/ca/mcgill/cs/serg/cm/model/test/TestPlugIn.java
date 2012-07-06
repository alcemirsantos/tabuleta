/* ConcernMapper - A concern modeling plug-in for Eclipse
 * Copyright (C) 2006  McGill University (http://www.cs.mcgill.ca/~martin/cm)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * $Revision: 1.6 $
 */

package ca.mcgill.cs.serg.cm.model.test;

//import junit.framework.TestCase;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.action.IAction;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ca.mcgill.cs.serg.cm.ConcernMapper;
import ca.mcgill.cs.serg.cm.actions.LoadConcernModelAction;
import ca.mcgill.cs.serg.cm.actions.SaveAction;
import ca.mcgill.cs.serg.cm.model.io.ModelIOException;
import ca.mcgill.cs.serg.cm.model.io.ModelReader;
import ca.mcgill.cs.serg.cm.model.io.XMLTags;

public class TestPlugIn //extends TestCase
{
	private ca.mcgill.cs.serg.cm.model.io.IProgressMonitor aMonitor = new ca.mcgill.cs.serg.cm.model.io.IProgressMonitor()
	{
    	public void setTotal( int pTotal ) {}
		
		public void worked( int pAmount ) {}
    };
	
	public TestPlugIn()
	{}
	
	@Before
	public void setUp() throws Exception 
    {
		ConcernMapper.getDefault().getConcernModel().reset();
    }

	@After
	public void tearDown() throws Exception 
    {
		ConcernMapper.getDefault().resetDirty();
    }
    
    // This test for a concern containing a standard field
    @Test
    public void testLoadStandardField()
    {
    	IFile lTarget = ResourcesPlugin.getWorkspace().getRoot().getProject("CMTest").getFile("concerns/defaultpackage/standardfield.cm");
    	LoadConcernModelAction lAction = new LoadConcernModelAction();
    	lAction.setFile( lTarget );
    	lAction.run( (IAction)null );
    	assertEquals( 1, ConcernMapper.getDefault().getConcernModel().getAllElements("StandardField").size());
    	assertEquals("Testing comment loading",
    			ConcernMapper.getDefault().getConcernModel().getConcernComment("StandardField"));
    	assertEquals("Testing comment loading for an element",
    			ConcernMapper.getDefault().getConcernModel().getElementComment(
    					"StandardField",
    					ConcernMapper.getDefault().getConcernModel().getElements("StandardField").iterator().next()) );
    }
    
    @Test
    public void testSaveStandardField()
    {
    	IFile lOriginal = ResourcesPlugin.getWorkspace().getRoot().getProject("CMTest").getFile("concerns/defaultpackage/standardfield.cm");
    	IPath lPath = lOriginal.getFullPath();
    	lPath = lPath.removeFileExtension().removeLastSegments(1).append("tmp.cm");
    	try {
			lOriginal.copy( lPath, false, null);
		} catch (CoreException e)
		{
			e.printStackTrace();
		}
    	IFile lTarget = ResourcesPlugin.getWorkspace().getRoot().getProject("CMTest").getFile("concerns/defaultpackage/tmp.cm" );
    	LoadConcernModelAction lLoadAction = new LoadConcernModelAction();
    	lLoadAction.setFile( lTarget );
    	lLoadAction.run( (IAction)null );
    	SaveAction lSaveAction = new SaveAction(null);
    	lSaveAction.run();
    	lLoadAction.run( (IAction)null );
    	try {
			lTarget.delete(false, null);
		} catch (CoreException e)
		{
			e.printStackTrace();
		}
		assertEquals( 1, ConcernMapper.getDefault().getConcernModel().getAllElements("StandardField").size());
    	assertEquals("Testing comment loading",
    			ConcernMapper.getDefault().getConcernModel().getConcernComment("StandardField"));
    	assertEquals("Testing comment loading for an element",
    			ConcernMapper.getDefault().getConcernModel().getElementComment(
    					"StandardField",
    					ConcernMapper.getDefault().getConcernModel().getElements("StandardField").iterator().next()) );
    }
    	

//    @Test
//    public void testLoadStandardMethod()
//    {
//    	IFile lTarget = ResourcesPlugin.getWorkspace().getRoot().getProject("CMTest").getFile("concerns/defaultpackage/standardmethod.cm");
//    	LoadConcernModelAction lAction = new LoadConcernModelAction();
//    	lAction.setFile( lTarget );
//    	lAction.run( (IAction)null );
//    	assertEquals( 1, ConcernMapper.getDefault().getConcernModel().getAllElements("StandardMethod").size());
//    	assertEquals("Testing element comment for a method",
//    			ConcernMapper.getDefault().getConcernModel().getElementComment(
//    					"StandardMethod",
//    					ConcernMapper.getDefault().getConcernModel().getElements("StandardMethod").iterator().next()) );
//    }
//    
//    @Test
//    public void testSaveStandardMethod()
//    {
//    	IFile lOriginal = ResourcesPlugin.getWorkspace().getRoot().getProject("CMTest").getFile("concerns/defaultpackage/standardmethod.cm");
//    	IPath lPath = lOriginal.getFullPath();
//    	lPath = lPath.removeFileExtension().removeLastSegments(1).append("tmp.cm");
//    	try {
//			lOriginal.copy( lPath, false, null);
//		} catch (CoreException e)
//		{
//			e.printStackTrace();
//		}
//    	IFile lTarget = ResourcesPlugin.getWorkspace().getRoot().getProject("CMTest").getFile("concerns/defaultpackage/tmp.cm" );
//    	LoadConcernModelAction lLoadAction = new LoadConcernModelAction();
//    	lLoadAction.setFile( lTarget );
//    	lLoadAction.run( (IAction)null );
//    	SaveAction lSaveAction = new SaveAction(null);
//    	lSaveAction.run();
//    	lLoadAction.run( (IAction)null );
//    	try {
//			lTarget.delete(false, null);
//		} catch (CoreException e)
//		{
//			e.printStackTrace();
//		}
//		assertEquals( 1, ConcernMapper.getDefault().getConcernModel().getAllElements("StandardMethod").size());
//		assertEquals("Testing element comment for a method",
//    			ConcernMapper.getDefault().getConcernModel().getElementComment(
//    					"StandardMethod",
//    					ConcernMapper.getDefault().getConcernModel().getElements("StandardMethod").iterator().next()) );
//    }
//    
//    // This test for a concern containing an anonymous class, note that elements of the anonymous class cannot be mapped into CM
//    @Test
//    public void testLoadAnonymousClass()
//    {
//    	IFile lTarget = ResourcesPlugin.getWorkspace().getRoot().getProject("CMTest").getFile("concerns/defaultpackage/anonymousclass.cm");
//    	LoadConcernModelAction lAction = new LoadConcernModelAction();
//    	lAction.setFile( lTarget );
//    	lAction.run( (IAction)null );
//    	assertEquals( 3, ConcernMapper.getDefault().getConcernModel().getAllElements("AnonymousClass").size());
//    }
//    
//    // This test for a concern containing a local class
//    @Test
//    public void testLoadLocalClass()
//    {
//    	IFile lTarget = ResourcesPlugin.getWorkspace().getRoot().getProject("CMTest").getFile("concerns/defaultpackage/localclass.cm");
//    	LoadConcernModelAction lAction = new LoadConcernModelAction();
//    	lAction.setFile( lTarget );
//    	lAction.run( (IAction)null );
//    	assertEquals( 2, ConcernMapper.getDefault().getConcernModel().getAllElements("LocalClass").size());
//    }
//    
//    // This test for a concern containing a static member interface/class
//    @Test
//    public void testLoadStaticMemberInterface()
//    {
//    	IFile lTarget = ResourcesPlugin.getWorkspace().getRoot().getProject("CMTest").getFile("concerns/defaultpackage/staticmemberinterface.cm");
//    	LoadConcernModelAction lAction = new LoadConcernModelAction();
//    	lAction.setFile( lTarget );
//    	lAction.run( (IAction)null );
//    	assertEquals( 5, ConcernMapper.getDefault().getConcernModel().getAllElements("StaticMemberInterface").size());
//    }
//    
//    // This test for a concern containing member classes
//    @Test
//    public void testLoadMemberClass()
//    {
//    	IFile lTarget = ResourcesPlugin.getWorkspace().getRoot().getProject("CMTest").getFile("concerns/defaultpackage/memberclass.cm");
//    	LoadConcernModelAction lAction = new LoadConcernModelAction();
//    	lAction.setFile( lTarget );
//    	lAction.run( (IAction)null );
//    	assertEquals( 5, ConcernMapper.getDefault().getConcernModel().getAllElements("MemberClass").size());
//    }
//    
//    // This test for a concern containing fields, methods, anonymous classes, local classes, static member classes/interface, member classes
//    @Test
//    public void testCompoundDefaultPackage()
//    {
//    	IFile lTarget = ResourcesPlugin.getWorkspace().getRoot().getProject("CMTest").getFile("concerns/defaultpackage/compound.cm");
//    	LoadConcernModelAction lAction = new LoadConcernModelAction();
//    	lAction.setFile( lTarget );
//    	lAction.run( (IAction)null );
//    	assertEquals( 17, ConcernMapper.getDefault().getConcernModel().getAllElements("Compound").size());
//    }
//    
//    /**
//     * Tests the loading of a concern with elements with generics.
//     * The concern elements we are testing are generic typed fields, generic methods, more than one type
//     */ 
//    
//    // This tests for a concern containing a generic method and two typebounds
//    @Test
//    public void testUtilGenerics()
//    {
//    	IFile lTarget = ResourcesPlugin.getWorkspace().getRoot().getProject("CMTest").getFile("concerns/generics/utils.cm");
//    	LoadConcernModelAction lAction = new LoadConcernModelAction();
//    	lAction.setFile( lTarget );
//    	lAction.run( (IAction)null );
//    	assertEquals( 2, ConcernMapper.getDefault().getConcernModel().getAllElements("utils").size());
//    }
//    
//    // This test for a concern containing a generic method and a wildcard
//    @Test
//    public void testListGenerics()
//    {
//    	IFile lTarget = ResourcesPlugin.getWorkspace().getRoot().getProject("CMTest").getFile("concerns/generics/list.cm");
//    	LoadConcernModelAction lAction = new LoadConcernModelAction();
//    	lAction.setFile( lTarget );
//    	lAction.run( (IAction)null );
//    	assertEquals( 2, ConcernMapper.getDefault().getConcernModel().getAllElements("list").size());
//    }
//    
//    // This test for a concern containing a fields with generic type
//    @Test
//    public void testPairGenerics()
//    {
//    	IFile lTarget = ResourcesPlugin.getWorkspace().getRoot().getProject("CMTest").getFile("concerns/generics/pair.cm");
//    	LoadConcernModelAction lAction = new LoadConcernModelAction();
//    	lAction.setFile( lTarget );
//    	lAction.run( (IAction)null );
//    	assertEquals( 8, ConcernMapper.getDefault().getConcernModel().getAllElements("pair").size());
//    }
//    
//    // This test for a concern containing a generic method, field, a wildcard and two typebounds
//    @Test
//    public void testCompoundGenerics()
//    {
//    	IFile lTarget = ResourcesPlugin.getWorkspace().getRoot().getProject("CMTest").getFile("concerns/generics/compound.cm");
//    	LoadConcernModelAction lAction = new LoadConcernModelAction();
//    	lAction.setFile( lTarget );
//    	lAction.run( (IAction)null );
//    	assertEquals( 9, ConcernMapper.getDefault().getConcernModel().getAllElements("compound").size());
//    }
//    
//    @Test(expected=ModelIOException.class)
//    public void testParseException() throws ModelIOException
//    {
//    	IFile lTarget = ResourcesPlugin.getWorkspace().getRoot().getProject("CMTest").getFile("concerns/IOExceptions/parsingError.cm");
//    	ModelReader lReader = new ModelReader( ConcernMapper.getDefault().getConcernModel() );
//    	try
//    	{
//			lReader.read( lTarget, aMonitor );
//		}
//    	catch( ModelIOException e ) 
//    	{
//			assertEquals("Could not parse input", e.getMessage().substring(0, e.getMessage().indexOf(".")) );
//			throw e;
//		}
//    }
//    
//    @Test(expected=ModelIOException.class)
//    public void testNoFileException() throws ModelIOException
//    {
//    	IFile lTarget = ResourcesPlugin.getWorkspace().getRoot().getProject("CMTest").getFile("idontexist.cm");
//    	ModelReader lReader = new ModelReader( ConcernMapper.getDefault().getConcernModel() );
//    	try {
//			lReader.read( lTarget, aMonitor );
//		} catch (ModelIOException e)
//		{
//			assertEquals("Could not obtain file content", e.getMessage().substring(0, e.getMessage().indexOf(".")) );
//			throw e;
//		}
//    }
//    
//    @Test(expected=ModelIOException.class)
//    public void testFormattingException() throws ModelIOException
//    {
//    	IFile lTarget = ResourcesPlugin.getWorkspace().getRoot().getProject("CMTest").getFile("concerns/IOExceptions/wrongFormatting.cm");
//    	ModelReader lReader = new ModelReader( ConcernMapper.getDefault().getConcernModel() );
//    	try {
//			lReader.read( lTarget, aMonitor );
//		} catch (ModelIOException e)
//		{
//			assertEquals("Document node is not a <" + XMLTags.Elements.MODEL.toString() + "> node", e.getMessage() );
//			throw e;
//		}
//    }
//    
//    @Test(expected=ModelIOException.class)
//    public void testFormattingException2() throws ModelIOException
//    {
//    	IFile lTarget = ResourcesPlugin.getWorkspace().getRoot().getProject("CMTest").getFile("concerns/IOExceptions/wrongFormatting2.cm");
//    	ModelReader lReader = new ModelReader( ConcernMapper.getDefault().getConcernModel() );
//    	try {
//			lReader.read( lTarget, aMonitor );
//		} catch (ModelIOException e)
//		{
//			assertEquals("Invalid node. Expecting <concern> node but got: notconcern",e.getMessage() );
//			throw e;
//		}
//    }
//    
//    @Test(expected=ModelIOException.class)
//    public void testMissingType() throws ModelIOException
//    {
//    	IFile lTarget = ResourcesPlugin.getWorkspace().getRoot().getProject("CMTest").getFile("concerns/IOExceptions/missingType.cm");
//    	ModelReader lReader = new ModelReader( ConcernMapper.getDefault().getConcernModel() );
//    	try
//    	{
//    		lReader.read( lTarget, aMonitor );
//    	} catch (ModelIOException e)
//    	{
//    		assertEquals("Could not build concern model. Missing attribute " + 
//							XMLTags.Attributes.TYPE.toString() + " in XML element type <element>", e.getMessage() );
//    		throw e;
//    	}
//    }
//    	
//    	@Test(expected=ModelIOException.class)
//        public void testMissingID() throws ModelIOException
//        {
//        	IFile lTarget = ResourcesPlugin.getWorkspace().getRoot().getProject("CMTest").getFile("concerns/IOExceptions/missingID.cm");
//        	ModelReader lReader = new ModelReader( ConcernMapper.getDefault().getConcernModel() );
//        	try
//        	{
//        		lReader.read( lTarget, aMonitor );
//        	} catch (ModelIOException e)
//        	{
//        		assertEquals("Could not build concern model. Missing attribute " + 
//    							XMLTags.Attributes.ID.toString() + " in XML element type <element>", e.getMessage() );
//        		throw e;
//        	}
//        }
//    	
//    	@Test(expected=ModelIOException.class)
//        public void testMissingDegree() throws ModelIOException
//        {
//        	IFile lTarget = ResourcesPlugin.getWorkspace().getRoot().getProject("CMTest").getFile("concerns/IOExceptions/missingDegree.cm");
//        	ModelReader lReader = new ModelReader( ConcernMapper.getDefault().getConcernModel() );
//        	try
//        	{
//        		lReader.read( lTarget, aMonitor );
//        	} catch (ModelIOException e)
//        	{
//        		assertEquals("Could not build concern model. Missing attribute " + 
//    							XMLTags.Attributes.DEGREE.toString() + " in XML element type <element>", e.getMessage() );
//        		throw e;
//        	}
//        }
//    	
//    	@Test(expected=ModelIOException.class)
//        public void testWrongType() throws ModelIOException
//        {
//        	IFile lTarget = ResourcesPlugin.getWorkspace().getRoot().getProject("CMTest").getFile("concerns/IOExceptions/wrongType.cm");
//        	ModelReader lReader = new ModelReader( ConcernMapper.getDefault().getConcernModel() );
//        	try
//        	{
//        		lReader.read( lTarget, aMonitor );
//        	} catch (ModelIOException e)
//        	{
//        		assertEquals("Invalid element type: nothing", e.getMessage() );
//        		throw e;
//        	}
//        }
//    	
//    	@Test
//        public void testCorruptedElement() throws ModelIOException
//        {
//        	IFile lTarget = ResourcesPlugin.getWorkspace().getRoot().getProject("CMTest").getFile("concerns/IOExceptions/corruptedElement.cm");
//        	ModelReader lReader = new ModelReader( ConcernMapper.getDefault().getConcernModel() );
//        	assertEquals(1,lReader.read( lTarget, aMonitor ) );
//        	assertEquals( 16, ConcernMapper.getDefault().getConcernModel().getAllElements("Compound").size());
//        }
//    	
//    	@Test(expected=ModelIOException.class)
//        public void testFormattingException3() throws ModelIOException
//        {
//        	IFile lTarget = ResourcesPlugin.getWorkspace().getRoot().getProject("CMTest").getFile("concerns/IOExceptions/wrongFormatting3.cm");
//        	ModelReader lReader = new ModelReader( ConcernMapper.getDefault().getConcernModel() );
//        	try
//        	{
//        		lReader.read( lTarget, aMonitor );
//        	} catch (ModelIOException e)
//        	{
//        		assertEquals("Could not build concern model. Invalid XML element type: <notelement>", e.getMessage() );
//        		throw e;
//        	}
//        }
}
