package br.ufmg.dcc.t2fm;

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
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IWindowListener;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import ca.mcgill.cs.serg.cm.actions.LoadConcernModelAction;
import ca.mcgill.cs.serg.cm.actions.SaveAction;
import ca.mcgill.cs.serg.cm.model.ConcernModel;
import ca.mcgill.cs.serg.cm.model.ConcernModelChangeListener;
import ca.mcgill.cs.serg.cm.ui.ConcernMapperPreferencePage;
import ca.mcgill.cs.serg.cm.ui.ProblemManager;

/**
 * The activator class controls the plug-in life cycle
 */
public class Test2FeatureMapper extends AbstractUIPlugin implements ConcernModelChangeListener{

	/** An ID for the plugin (same as in the plugin.xml file). */
	public static final String PLUGIN_ID = "br.ufmg.dcc.t2fm"; //$NON-NLS-1$

	/**	 An ID for the view same as in the plugin.xml file). */
	public static final String ID_VIEW = "ca.mcgill.cs.serg.cm.views.ConcernMapper";

	// The shared instance
	private static Test2FeatureMapper thisPlugin;
	
	//Resource bundle.
	private ResourceBundle aResourceBundle;
	
	// The singleton concern model.
	private ConcernModel aModel;
	
	// A default location for saving the concern model.
	private IFile aFile;
	
	// A flag indicating that the data in the model is unsaved.
	private boolean aDirty = false;

	/**
	 * The constructor
	 */
	public Test2FeatureMapper() {
		thisPlugin = this;
		aModel = new ConcernModel();
		try {
			aResourceBundle = ResourceBundle.getBundle("br.ufmg.dcc.t2fm.Test2FeatureMapperResources");
		} catch (MissingResourceException lException) {
			aResourceBundle = null;
		}
		aModel.addListener(thisPlugin);
		PlatformUI.getWorkbench().addWindowListener(new ConcernMapperWindowListener());
		JavaCore.addElementChangedListener(new ConcernMapperElementChangedListener());
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		thisPlugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		thisPlugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Test2FeatureMapper getDefault() {
		return thisPlugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}

	/**
	 * @return The concern model associated with the plugin
	 */
	public ConcernModel getConcernModel(){
		return aModel;
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
	 * When the model has changed we must reset the dirty bit.
	 * @see ca.mcgill.cs.serg.cm.model.ConcernModelChangeListener#modelChanged()
	 * {@inheritDoc}
	 */
	@Override
	public void modelChanged(int pChange) {
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
						
			for( String lConcern : Test2FeatureMapper.getDefault().getConcernModel().getConcernNames() )
			{
				Set<Object> lElements = Test2FeatureMapper.getDefault().getConcernModel().getAllElements( lConcern );
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
				Test2FeatureMapper.getDefault().getConcernModel().notifyChange( ConcernModel.DEFAULT );
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
			if( Test2FeatureMapper.getDefault().getPreferenceStore().getBoolean( ConcernMapperPreferencePage.P_AUTO_LOAD) &&
					!( Test2FeatureMapper.getDefault().getPreferenceStore().getString( ConcernMapperPreferencePage.P_AUTO_LOAD_RESOURCE)
							.equals( ConcernMapperPreferencePage.NO_LOADED_RESOURCE)))
			{
				IFile lFile = ResourcesPlugin.getWorkspace().getRoot().getFile( 
						Path.fromOSString( Test2FeatureMapper.getDefault().getPreferenceStore().
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
			if( (Test2FeatureMapper.getDefault().getPreferenceStore().getBoolean( ConcernMapperPreferencePage.P_AUTO_SAVE )) &&
					(Test2FeatureMapper.getDefault().isDirty()) && !((Test2FeatureMapper.getDefault().getConcernModel().getConcernNames().length == 0) && 
							Test2FeatureMapper.getDefault().getDefaultResource() == null ))
			{
				new SaveAction( null ).run();			
			}
			
			if( Test2FeatureMapper.getDefault().getPreferenceStore().getBoolean( ConcernMapperPreferencePage.P_AUTO_LOAD))
			{
				if( Test2FeatureMapper.getDefault().getDefaultResource() != null )
				{
					Test2FeatureMapper.getDefault().getPreferenceStore().setValue( ConcernMapperPreferencePage.P_AUTO_LOAD_RESOURCE,
							Test2FeatureMapper.getDefault().getDefaultResource().getFullPath().toPortableString());
				}
				else
				{
					Test2FeatureMapper.getDefault().getPreferenceStore().setValue( ConcernMapperPreferencePage.P_AUTO_LOAD_RESOURCE,
							ConcernMapperPreferencePage.NO_LOADED_RESOURCE);
				}
			}
		}
		
	}

}
