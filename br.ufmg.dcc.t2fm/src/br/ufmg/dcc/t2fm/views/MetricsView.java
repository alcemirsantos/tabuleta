package br.ufmg.dcc.t2fm.views;

import java.util.ArrayList;

import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import br.ufmg.dcc.t2fm.views.ItensManager.Item;

public class MetricsView extends ViewPart {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "br.ufmg.dcc.t2fm.views.MetricsView";
	
	private TableViewer tblViewer;
	private TableColumn cmColumn;
	private TableColumn rColumn;
	private TableColumn pColumn;
	private TableColumn fnColumn;
	private TableColumn fpColumn;
	private TableColumn tpColumn;
	
	
	public MetricsView() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void createPartControl(Composite parent) {
		createTableViewer(parent);


	}

	/**
	 * Create the tableViewer to show the metrics.
	 * 
	 * @param parent
	 */
	private void createTableViewer(Composite parent) {
		tblViewer = new TableViewer(parent, SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI | SWT.FULL_SELECTION);
		 
		 final Table table = tblViewer.getTable();
		
		 TableColumnLayout layout = new TableColumnLayout();
		 parent.setLayout(layout);
		
		 cmColumn = new TableColumn(table, SWT.LEFT);
		 cmColumn.setText(".cm file");
		 layout.setColumnData(cmColumn, new ColumnPixelData(5));
		 
		 tpColumn = new TableColumn(table, SWT.LEFT);
		 tpColumn.setText("TP");
		 layout.setColumnData(tpColumn, new ColumnPixelData(5));
		
		 fpColumn = new TableColumn(table, SWT.LEFT);
		 fpColumn.setText("FP");
		 layout.setColumnData(fpColumn, new ColumnPixelData(5));
		
		 fnColumn = new TableColumn(table, SWT.LEFT);
		 fnColumn.setText("FN");
		 layout.setColumnData(fnColumn, new ColumnPixelData(5));
		
		 rColumn = new TableColumn(table, SWT.LEFT);
		 rColumn.setText("Recall");
		 layout.setColumnData(tpColumn, new ColumnPixelData(10));
		
		 pColumn = new TableColumn(table, SWT.LEFT);
		 pColumn.setText("Precision");
		 layout.setColumnData(tpColumn, new ColumnPixelData(10));
		
		 table.setHeaderVisible(true);
		 table.setLinesVisible(false);
		
		 tblViewer.setContentProvider(new ViewContentProvider());
		 tblViewer.setLabelProvider(new ViewLabelProvider());
		 tblViewer.setSorter(new NameSorter());
		 tblViewer.setInput(ItensManager.getInstance());
		 
		 getSite().setSelectionProvider(tblViewer);
		
//		Table table = new Table(parent, SWT.SINGLE);
//		table.setHeaderVisible(true);
//		table.setLinesVisible(true);
//
//		TableColumn column1 = new TableColumn(table, SWT.LEFT);
//		TableColumn column2 = new TableColumn(table, SWT.LEFT);
//		TableItem row1 = new TableItem(table, SWT.NONE);
//		TableItem row2 = new TableItem(table, SWT.NONE);
//
//		column1.setWidth(100);
//		column1.setText("Home");
//		column2.setWidth(100);
//		column2.setText("Visitor");
//
//		row1.setText(new String[] { "7", "3" });
//		row2.setText(new String[] { "5", "11" });
	}

	
	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}
	
	
	class ViewContentProvider implements IStructuredContentProvider {
		ItensManager manager = ItensManager.getInstance();
		
		public void inputChanged(Viewer v, Object oldInput, Object newInput) {
		}
		public void dispose() {
		}
		public Object[] getElements(Object parent) {
			return manager.getItens();
		}
	}
	
	
	class ViewLabelProvider extends LabelProvider implements ITableLabelProvider {
		public String getColumnText(Object obj, int index) {
			switch (index) {
			case 0:
				return ((Item) obj).getA();
			case 1:
				return ((Item) obj).getB();
			case 2:
				return ((Item) obj).getC();
			case 3:
				return ((Item) obj).getD();
			case 4:
				return ((Item) obj).getE();
			case 5:
				return ((Item) obj).getF();
			default:
				return "";
			}
		}
		public Image getColumnImage(Object obj, int index) {
			return getImage(obj);
		}
		public Image getImage(Object obj) {
			return PlatformUI.getWorkbench().
					getSharedImages().getImage(ISharedImages.IMG_OBJ_ELEMENT);
		}
	}

	class NameSorter extends ViewerSorter {	}
}
