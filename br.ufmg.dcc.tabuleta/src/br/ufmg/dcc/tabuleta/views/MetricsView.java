package br.ufmg.dcc.tabuleta.views;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.part.ViewPart;

import br.ufmg.dcc.tabuleta.actions.CalculateMetricsAction;
import br.ufmg.dcc.tabuleta.views.components.MetricsManager;
import br.ufmg.dcc.tabuleta.views.components.MetricsReport;

/**
 * Esta classe representa a visão <code>Metrics</code> utilizada para a exibição das 
 *   métricas que uma vez calculadas pela classe {@link CalculateMetricsAction}, são
 *   representadas pela classe {@link MetricsReport} e gerenciadas pela 
 *   {@link MetricsManager};
 *   
 * @author Alcemir R. Santos
 *
 */
public class MetricsView extends ViewPart {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "br.ufmg.dcc.tabuleta.views.MetricsView";
	
	private static TableViewer tblViewer;
	
	public MetricsView() {
	}
	
	public static TableViewer getMetricsView() {
		return tblViewer;
	}

	@Override
	public void createPartControl(Composite parent) {
		// [HINT] link para o tutorial do voguela: http://www.vogella.com/articles/EclipseJFaceTable/article.html#jfacetable_selectionlistener
		tblViewer = new TableViewer(parent, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI | SWT.FULL_SELECTION);
		
		// Create the columns 
		createColumns(parent);

		// Make lines and make header visible
		final Table table = tblViewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true); 

		// Set the ContentProvider
		tblViewer.setContentProvider(ArrayContentProvider.getInstance());

		// Get the content for the Viewer, setInput will call getElements in the ContentProvider
		tblViewer.setInput(MetricsManager.getInstance().getItens()); 
	}

	/**
	 * @param parent
	 */
	private void createColumns(Composite parent) {
		 String[] titles = { "CM File", "TP", "FP", "FN", "Recall", "Precision", "F1-Score" };
		    int[] bounds = { 250, 100, 100, 100, 100, 100, 100 };

		    // This column is for the CM file 
		    int columnID = 0;
		    TableViewerColumn col = createTableViewerColumn(titles[columnID], bounds[columnID], columnID);
		    col.setLabelProvider(new ColumnLabelProvider() {
		      @Override
		      public String getText(Object element) {
		        MetricsReport p = (MetricsReport) element;
		        return p.getFileName();
		      }
		    });
		    
		    // This column is for the TP
		    columnID = 1;
		    col = createTableViewerColumn(titles[columnID], bounds[columnID], columnID);
		    col.setLabelProvider(new ColumnLabelProvider() {
		      @Override
		      public String getText(Object element) {
		        MetricsReport p = (MetricsReport) element;
		        return p.getTruePositives();
		      }
		    });

		    // This column is for the FP
		    columnID = 2;
		    col = createTableViewerColumn(titles[columnID], bounds[columnID], columnID);
		    col.setLabelProvider(new ColumnLabelProvider() {
		      @Override
		      public String getText(Object element) {
		        MetricsReport p = (MetricsReport) element;
		        return p.getFalsePositives();
		      }
		    });

		    // This column is for the FN
		    columnID = 3;
		    col = createTableViewerColumn(titles[columnID], bounds[columnID], columnID);
		    col.setLabelProvider(new ColumnLabelProvider() {
		      @Override
		      public String getText(Object element) {
		        MetricsReport p = (MetricsReport) element;
		        return p.getFalseNegatives();
		      }
		    });

		    // This column is for the Recall
		    columnID = 4;
		    col = createTableViewerColumn(titles[columnID], bounds[columnID], columnID);
		    col.setLabelProvider(new ColumnLabelProvider() {
		      @Override
		      public String getText(Object element) {
		        MetricsReport p = (MetricsReport) element;
		        return p.getRecall();
		      }
		    });

		    // This column is for the Precision
		    columnID = 5;
		    col = createTableViewerColumn(titles[columnID], bounds[columnID], columnID);
		    col.setLabelProvider(new ColumnLabelProvider() {
		      @Override
		      public String getText(Object element) {
		        MetricsReport p = (MetricsReport) element;
		        return p.getPrecision();
		      }
		    });
		    
		    // This column is for the F1-score
		    columnID = 6;
		    col = createTableViewerColumn(titles[columnID], bounds[columnID], columnID);
		    col.setLabelProvider(new ColumnLabelProvider() {
		      @Override
		      public String getText(Object element) {
		        MetricsReport p = (MetricsReport) element;
		        return p.getF1Score();
		      }
		    });
	}

	/**
	 * Retorna o layout de uma coluna da tabela.
	 * @param title
	 * @param bound
	 * @param colNumber
	 * @return
	 */
	private TableViewerColumn createTableViewerColumn(String title, int bound, final int colNumber) {
	    final TableViewerColumn viewerColumn = new TableViewerColumn(tblViewer,  SWT.NONE);
	    final TableColumn column = viewerColumn.getColumn();
	    column.setText(title);
	    column.setWidth(bound);
	    column.setResizable(true);
	    column.setMoveable(true);
	    return viewerColumn;
	  }
	
	/**
	 * Passing the focus request to the viewer's control.
	 */
	@Override
	  public void setFocus() {
	    tblViewer.getControl().setFocus();
	  }
	
	class NameSorter extends ViewerSorter {	}
}
