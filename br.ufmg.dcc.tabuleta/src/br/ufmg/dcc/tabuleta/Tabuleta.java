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
package br.ufmg.dcc.tabuleta;

import java.util.HashSet;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.ElementChangedEvent;
import org.eclipse.jdt.core.IElementChangedListener;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaElementDelta;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.action.IAction;
import org.eclipse.ui.IWindowListener;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import br.ufmg.dcc.tabuleta.actions.LoadConcernModelAction;
import br.ufmg.dcc.tabuleta.actions.SaveAction;
import br.ufmg.dcc.tabuleta.model.ConcernModel;
import br.ufmg.dcc.tabuleta.model.ConcernModelChangeListener;
import br.ufmg.dcc.tabuleta.ui.ConcernMapperPreferencePage;
import br.ufmg.dcc.tabuleta.ui.ProblemManager;

/**
 * The activator class controls the plug-in life cycle
 */
public class Tabuleta extends AbstractUIPlugin implements ConcernModelChangeListener{

	/** An ID for the plugin (same as in the plugin.xml file). */
	public static final String ID_PLUGIN = "br.ufmg.dcc.tabuleta"; //$NON-NLS-1$
	/**	 An ID for the view same as in the plugin.xml file). */
	public static final String ID_VIEW = "br.ufmg.dcc.tabuleta.views.MapView";

	// The shared instance.
	private static Tabuleta aPlugin;
	
	//Resource bundle.
	private ResourceBundle aResourceBundle;
	
	// The singleton concern model.
	private ConcernModel aModel;
	
	// A default location for saving the concern model.
	private IFile aFile;
	
	// A flag indicating that the data in the model is unsaved.
	private boolean aDirty = false;
	
	/**
	 * The constructor.  Loads the resource bundle.
	 */
	public Tabuleta() 
	{
		aPlugin = this;
		aModel = new ConcernModel();
		try 
		{
			aResourceBundle = ResourceBundle.getBundle( "br.ufmg.dcc.tabuleta.TabuletaResources" );
		}
		catch( MissingResourceException lException )
		{
			aResourceBundle = null;
		}
		aModel.addListener( this );
		PlatformUI.getWorkbench().addWindowListener( new ConcernMapperWindowListener() );

		JavaCore.addElementChangedListener( new ConcernMapperElementChangedListener() );
	}
	
	/**
	 * Returns the resource where the model is stored by default.
	 * @return A file handle.  Can be null.
	 */
	public IFile getDefaultResource()
	{
		return aFile;
	}
	
	/**
	 * Indicates whether the model contains unsaved information.
	 * @return True if the model contains unsaved data.
	 */
	public boolean isDirty()
	{
		return aDirty;
	}
	
	/**
	 * Resets the dirty flag, thus signifying that the model
	 * does not contain unsaved data.
	 */
	public void resetDirty()
	{
		aDirty = false;
	}
	
	/**
	 * Sets the resource where the concern model will be saved by default.
	 * @param pFile The default resource.
	 */
	public void setDefaultResource( IFile pFile )
	{
		aFile = pFile;
	}
	
	/**
	 * @return The concern model associated with the plugin
	 */
	public ConcernModel getConcernModel()
	{
		return aModel;
	}
	
	/** 
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 * @param pContext The context
	 * @throws Exception If something happens
	 */
	public void start( BundleContext pContext ) throws Exception
	{
		super.start( pContext );
	}
	
	/**
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 * @param pContext the parameter
	 * @throws Exception the exception
	 */
	public void stop( BundleContext pContext ) throws Exception 
	{
		super.stop( pContext );
	}
	
	/**
	 * Returns the shared instance.
	 * @return The shared instance.
	 */
	public static Tabuleta getDefault()
	{
		return aPlugin;
	}
	
	/**
	 * Returns the string from the plugin's resource bundle,
	 * or 'key' if not found.
	 * @param pKey The key to use for the property lookup
	 * @return A string representing the resource.
	 */
	public static String getResourceString( String pKey )
	{
		final ResourceBundle lBundle = Tabuleta.getDefault().getResourceBundle();
		try 
		{
			if( lBundle != null )
			{
				return lBundle.getString( pKey );
			}
			else
			{
				return pKey;
			}
		}
		catch( MissingResourceException lException )
		{
			return pKey;
		}
	}
	
	/**
	 * @return The plugin's resource bundle
	 */
	public ResourceBundle getResourceBundle()
	{
		return aResourceBundle;
	}
	
	/**
	 * When the model has changed we must reset the dirty bit.
	 * @see ca.mcgill.cs.serg.cm.model.ConcernModelChangeListener#modelChanged()
	 * {@inheritDoc}
	 */
	public void modelChanged( int pChange ) 
	{
		if( pChange != ConcernModel.CLEAR_VIEW )
		{
			aDirty = true;
		}
	}
	

	/**
	 * This class responds to ElementChanged events by determining if the event applies to an 
	 * element in the model, or a parent (which would become consistent or not), and fires a model
	 * changed event if so.
	 */
	static class ConcernMapperElementChangedListener implements IElementChangedListener
	{
		/**
		 * @see org.eclipse.jdt.core.IElementChangedListener#elementChanged(org.eclipse.jdt.core.ElementChangedEvent);
		 * @param pEvent the ElementChangedEvent
		 */
		public void elementChanged( ElementChangedEvent pEvent )
		{
			boolean lRefresh = false;
			Set<IJavaElement> lAffected = getAllAffectedElements( pEvent.getDelta() );
						
			for( String lConcern : Tabuleta.getDefault().getConcernModel().getConcernNames() )
			{
				Set<Object> lElements = Tabuleta.getDefault().getConcernModel().getAllElements( lConcern );
				for( Object lObject : lElements )
				{
					if( lObject instanceof IJavaElement )
					{
						for (IJavaElement lJavaElement : lAffected)
						{
							if( isParentOf( lJavaElement, (IJavaElement) lObject ))
							{
								lRefresh = true;
								break;
							}
						}
					}
				}
			}
			if( lRefresh )
			{
				Tabuleta.getDefault().getConcernModel().notifyChange( ConcernModel.DEFAULT );
			}
			
		}

		private boolean isParentOf(IJavaElement pAffectedElement, IJavaElement pConcernElement)
		{
			if (pConcernElement == null)
			{
				return false;
			}
			if (pAffectedElement.equals( pConcernElement ))
			{
				return true;
			}
			return isParentOf( pAffectedElement, pConcernElement.getParent() );
		}
		
		private static Set<IJavaElement> getAllAffectedElements( IJavaElementDelta pDelta )
		{
			Set<IJavaElement> lReturn = new HashSet<IJavaElement>();
			lReturn.add( pDelta.getElement() );
			for( IJavaElementDelta lChild : pDelta.getAffectedChildren() )
			{
				lReturn.addAll( getAllAffectedElements( lChild ));
			}
			return lReturn;
		}

	}

	
	/**
	 * 
	 */
	static class ConcernMapperWindowListener implements IWindowListener
	{
		/**
		 * Called when the given window has been activated.
		 * @param pWindow the window that was activated
		 */
		public void windowActivated(IWorkbenchWindow pWindow) 
		{ }
		
		/**
		 * Called when the given window has been deactivated.
		 * @param pWindow the window that was deactivated
		 */
		public void windowDeactivated(IWorkbenchWindow pWindow) 
		{ }
		
		/**
		 * Called when the given window has been opened.
		 * @param pWindow the window that was opened
		 */
		public void windowOpened(IWorkbenchWindow pWindow)
		{
			if( Tabuleta.getDefault().getPreferenceStore().getBoolean( ConcernMapperPreferencePage.P_AUTO_LOAD) &&
					!( Tabuleta.getDefault().getPreferenceStore().getString( ConcernMapperPreferencePage.P_AUTO_LOAD_RESOURCE)
							.equals( ConcernMapperPreferencePage.NO_LOADED_RESOURCE)))
			{
				IFile lFile = ResourcesPlugin.getWorkspace().getRoot().getFile( 
						Path.fromOSString( Tabuleta.getDefault().getPreferenceStore().
								getString( ConcernMapperPreferencePage.P_AUTO_LOAD_RESOURCE)));
				try
				{
					lFile.getParent().refreshLocal( IResource.DEPTH_ONE, null);
				}
				catch(CoreException lException)
				{
					ProblemManager.reportException( lException );
				}
				catch(OperationCanceledException lException)
				{
					ProblemManager.reportException( lException );
				}
				if( lFile.exists() )
				{
					LoadConcernModelAction lLoadConcernModelAction = new LoadConcernModelAction();
					lLoadConcernModelAction.setFile( lFile );
					lLoadConcernModelAction.run( (IAction)null );
				}
			}
		}
		/**
		 * Called when the given window has been closed.
		 * @param pWindow the window that was closed.
		 */
		public void windowClosed(IWorkbenchWindow pWindow)
		{
			if( (Tabuleta.getDefault().getPreferenceStore().getBoolean( ConcernMapperPreferencePage.P_AUTO_SAVE )) &&
					(Tabuleta.getDefault().isDirty()) && !((Tabuleta.getDefault().getConcernModel().getConcernNames().length == 0) && 
							Tabuleta.getDefault().getDefaultResource() == null ))
			{
				new SaveAction( null ).run();			
			}
			
			if( Tabuleta.getDefault().getPreferenceStore().getBoolean( ConcernMapperPreferencePage.P_AUTO_LOAD))
			{
				if( Tabuleta.getDefault().getDefaultResource() != null )
				{
					Tabuleta.getDefault().getPreferenceStore().setValue( ConcernMapperPreferencePage.P_AUTO_LOAD_RESOURCE,
							Tabuleta.getDefault().getDefaultResource().getFullPath().toPortableString());
				}
				else
				{
					Tabuleta.getDefault().getPreferenceStore().setValue( ConcernMapperPreferencePage.P_AUTO_LOAD_RESOURCE,
							ConcernMapperPreferencePage.NO_LOADED_RESOURCE);
				}
			}
		}
		
	}
}
