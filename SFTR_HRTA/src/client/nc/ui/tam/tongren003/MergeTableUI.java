package nc.ui.tam.tongren003;

import javax.swing.table.*;
import javax.swing.plaf.basic.*;
import java.awt.*;
import java.lang.reflect.Method;

import javax.swing.*;

//import org.jvnet.substance.SubstanceTableUI;

public class MergeTableUI extends BasicTableUI {
	
	int begindex = 0;
	int endindex = 0;
	boolean isCutByPreCol = false;
	boolean isCutByPreCol1 = false;
	int begindex1 = 0;
	int endindex1 = 0;
	int needindex =0;
	int needindex1 =0;
	int mergindex = 1;
	
	public MergeTableUI(){
		super();
	}
	
	public MergeTableUI(int begindex, int endindex, boolean isCutByPreCol,int needindex){
		super();
		this.begindex = begindex;
		this.endindex = endindex;
		this.isCutByPreCol = isCutByPreCol;//是否根据前一列的一致性进行分割
		this.needindex = needindex;// 需要合并的依赖的行
		
	}
	
	public MergeTableUI(int begindex, int endindex, boolean isCutByPreCol,int needindex,int needindex1,int mergindex){
		super();
		this.begindex = begindex;
		this.endindex = endindex;
		this.isCutByPreCol = isCutByPreCol;//是否根据前一列的一致性进行分割
		this.needindex = needindex;// 需要合并的依赖的行
		this.needindex1 = needindex1;// 需要合并的依赖的行
		this.mergindex = mergindex;//从根据哪列合并
	}
	
	public void paint(Graphics g, JComponent c) {
		Rectangle r = g.getClipBounds();
//		int firstCol = table.columnAtPoint(new Point(r.x, 0));
		int firstCol = begindex;
		int lastCol = endindex;//这里设置需要合并的终止column序号
		int firstCol1 = begindex1;
		int lastCol1 = endindex1;//这里设置需要合并的终止column序号
		int normal = lastCol + 1;
		int normal1 = lastCol1 + 1;
		
		/**源码**START**/
		
		Rectangle rectangle = g.getClipBounds();
        Rectangle rectangle1 = table.getBounds();
        rectangle1.x = rectangle1.y = 0;
        if(table.getRowCount() <= 0 || table.getColumnCount() <= 0 || !rectangle1.intersects(rectangle))
            return;
        Point point = rectangle.getLocation();
        Point point1 = new Point((rectangle.x + rectangle.width) - 1, (rectangle.y + rectangle.height) - 1);
        int i = table.rowAtPoint(point);
        int j = table.rowAtPoint(point1);
        if(i == -1)
            i = 0;
        if(j == -1)
            j = table.getRowCount() - 1;
        boolean flag = table.getComponentOrientation().isLeftToRight();
        
        int k = table.columnAtPoint(flag ? point : point1);
        int l = table.columnAtPoint(flag ? point1 : point);
        
        if(k == -1)
            k = 0;
        if(l == -1)
            l = table.getColumnCount() - 1;
        
        /**变更_取值后加上+normal，系统的构造从不需重构的col开始**lxiaofan**START**/
        if(k <= normal){//这里判断k <= normal 是为了保证在滚动条拖动时不进行动态重构
        	/**先把起始行之前的正常画完**START**/
        	paintGrid(g, i, j, k, firstCol);
            paintCells(g, i, j, k, firstCol);
            /**先把起始行之前的正常画完**END**/
            k = normal;
        }
        /**变更_取值后加上+normal，系统的构造从不需重构的col开始**lxiaofan**END**/
        
        paintGrid(g, i, j, k, l);
        paintCells(g, i, j, k, l);
        
        /**源码**END**/
        
        //重画的关键方法
        for(int f = firstCol; f <= lastCol; f++){
			paintCol(f, g);
		}
        
        
        
        
        
        
////        Object[] obj = new Object[5];
////        obj[0] = g;
////        obj[1] = i;
////        obj[2] = j;
////        obj[3] = k;
////        obj[4] = l;
//        Class[] type = new Class[5];
//        type[0] = Graphics.class;
//        type[1] = int.class;
//        type[2] = int.class;
//        type[3] = int.class;
//        type[4] = int.class;
//        try{
//        	Class hintWinClass = Class.forName("javax.swing.plaf.basic.BasicTableUI");
//			Method paintGrid = hintWinClass.getDeclaredMethod("paintGrid", type);
//			Method paintCells = hintWinClass.getDeclaredMethod("paintCells", type);
//			paintGrid.setAccessible(true);
//			paintGrid.invoke(hintWinClass, g,i,j,k,l);
//			paintCells.invoke(hintWinClass, g,i,j,k,l);
//        }catch (Exception e) {
//			e.printStackTrace();
//		}
	}
	
	//mergindex根据哪列合并
	private void paintCol(int col, Graphics g) {
		int verticalMargin = table.getRowMargin();
		int horizontalMargin = table.getColumnModel().getColumnMargin();
		
		int rowcount = table.getRowCount();
		for (int i = 0; i < rowcount; i++) {
			int samerow = 0;
			int colindex = 1;
			if(isCutByPreCol && col > 0 && (col==3 ||col==5 || col==4)){//是否根据前一列的一致性进行分割,3或5列，根据前一列合并
				// 按月显示
				if(needindex1 !=0 &&  needindex !=0){
					
//					
						if(col == 4){// 按第一列为准合并
						colindex = 3;
					}
						
						if(col == 5){// 按第一列为准合并
							colindex = 4;
						}
//					
					
				}
				//进行非空判断，防止死循环,同时判断是否需要根据前一列进行分割
				while((table.getValueAt(i+samerow, col-colindex) != null ? table.getValueAt(i+samerow, col-colindex) : "")
						.equals(table.getValueAt(i, col-colindex) != null ? table.getValueAt(i, col-colindex) : "") 
						&& (table.getValueAt(i+samerow, col) != null ? table.getValueAt(i+samerow, col) : "")
						.equals(table.getValueAt(i, col) != null ? table.getValueAt(i, col) : "") 
						&& i+samerow <= rowcount){
					samerow++;
				}
			}else{
				while((table.getValueAt(i+samerow, col) != null ? table.getValueAt(i+samerow, col) : "")
						.equals(table.getValueAt(i, col) != null ? table.getValueAt(i, col) : "") 
						&& i+samerow <= rowcount){
					samerow++;
				}
			}
			
			Rectangle r = table.getCellRect(i, col, true);
			g.setColor(table.getGridColor());
			g.drawRect(r.x, r.y, r.width - horizontalMargin, r.height*samerow - verticalMargin);
			
			r.setBounds(r.x + horizontalMargin / 2,
					r.y + verticalMargin / 2, r.width - horizontalMargin,
					r.height - verticalMargin);
			
			TableCellRenderer renderer = table.getCellRenderer(i, col);
			Component component = table.prepareRenderer(renderer, i, col);
			if (component.getParent() == null){
				rendererPane.add(component);
			}
			rendererPane.paintComponent(g, component, table, r.x, r.y,
					r.width, r.height*samerow + verticalMargin*(samerow-1), true);
			
			i += samerow - 1;
		}
	}
	
	
	
	/**
	 * 下面开始都是java原有方法，不变更
	 * @param g
	 * @param i
	 * @param j
	 * @param k
	 * @param l
	 */
	private void paintGrid(Graphics g, int i, int j, int k, int l)
    {
        g.setColor(table.getGridColor());
        Rectangle rectangle = table.getCellRect(i, k, true);
        Rectangle rectangle1 = table.getCellRect(j, l, true);
        Rectangle rectangle2 = rectangle.union(rectangle1);
        if(table.getShowHorizontalLines())
        {
            int i1 = rectangle2.x + rectangle2.width;
            int j1 = rectangle2.y;
            for(int l1 = i; l1 <= j; l1++)
            {
                j1 += table.getRowHeight(l1);
                g.drawLine(rectangle2.x, j1 - 1, i1 - 1, j1 - 1);
            }

        }
        if(table.getShowVerticalLines())
        {
            TableColumnModel tablecolumnmodel = table.getColumnModel();
            int k1 = rectangle2.y + rectangle2.height;
            if(table.getComponentOrientation().isLeftToRight())
            {
                int i2 = rectangle2.x;
                for(int k2 = k; k2 <= l; k2++)
                {
                    int i3 = tablecolumnmodel.getColumn(k2).getWidth();
                    i2 += i3;
                    g.drawLine(i2 - 1, 0, i2 - 1, k1 - 1);
                }

            } else
            {
                int j2 = rectangle2.x + rectangle2.width;
                for(int l2 = k; l2 < l; l2++)
                {
                    int j3 = tablecolumnmodel.getColumn(l2).getWidth();
                    j2 -= j3;
                    g.drawLine(j2 - 1, 0, j2 - 1, k1 - 1);
                }

                j2 -= tablecolumnmodel.getColumn(l).getWidth();
                g.drawLine(j2, 0, j2, k1 - 1);
            }
        }
    }

	private void paintCells(Graphics g, int i, int j, int k, int l)
    {
        JTableHeader jtableheader = table.getTableHeader();
        TableColumn tablecolumn = jtableheader != null ? jtableheader.getDraggedColumn() : null;
        TableColumnModel tablecolumnmodel = table.getColumnModel();
        int i1 = tablecolumnmodel.getColumnMargin();
        if(table.getComponentOrientation().isLeftToRight())
        {
            for(int i2 = i; i2 <= j; i2++)
            {
                Rectangle rectangle = table.getCellRect(i2, k, false);
                for(int k2 = k; k2 <= l; k2++)
                {
                    TableColumn tablecolumn1 = tablecolumnmodel.getColumn(k2);
                    int j1 = tablecolumn1.getWidth();
                    rectangle.width = j1 - i1;
                    if(tablecolumn1 != tablecolumn)
                        paintCell(g, rectangle, i2, k2);
                    rectangle.x += j1;
                }

            }

        } else
        {
            for(int j2 = i; j2 <= j; j2++)
            {
                Rectangle rectangle1 = table.getCellRect(j2, k, false);
                TableColumn tablecolumn2 = tablecolumnmodel.getColumn(k);
                if(tablecolumn2 != tablecolumn)
                {
                    int k1 = tablecolumn2.getWidth();
                    rectangle1.width = k1 - i1;
                    paintCell(g, rectangle1, j2, k);
                }
                for(int l2 = k + 1; l2 <= l; l2++)
                {
                    TableColumn tablecolumn3 = tablecolumnmodel.getColumn(l2);
                    int l1 = tablecolumn3.getWidth();
                    rectangle1.width = l1 - i1;
                    rectangle1.x -= l1;
                    if(tablecolumn3 != tablecolumn)
                        paintCell(g, rectangle1, j2, l2);
                }

            }

        }
        if(tablecolumn != null)
            paintDraggedArea(g, i, j, tablecolumn, jtableheader.getDraggedDistance());
        rendererPane.removeAll();
    }

	private void paintCell(Graphics g, Rectangle rectangle, int i, int j)
    {
        if(table.isEditing() && table.getEditingRow() == i && table.getEditingColumn() == j)
        {
            Component component = table.getEditorComponent();
            component.setBounds(rectangle);
            component.validate();
        } else
        {
            javax.swing.table.TableCellRenderer tablecellrenderer = table.getCellRenderer(i, j);
            Component component1 = table.prepareRenderer(tablecellrenderer, i, j);
            rendererPane.paintComponent(g, component1, table, rectangle.x, rectangle.y, rectangle.width, rectangle.height, true);
        }
    }
	
	private void paintDraggedArea(Graphics g, int i, int j, TableColumn tablecolumn, int k)
    {
        int l = viewIndexForColumn(tablecolumn);
        Rectangle rectangle = table.getCellRect(i, l, true);
        Rectangle rectangle1 = table.getCellRect(j, l, true);
        Rectangle rectangle2 = rectangle.union(rectangle1);
        g.setColor(table.getParent().getBackground());
        g.fillRect(rectangle2.x, rectangle2.y, rectangle2.width, rectangle2.height);
        rectangle2.x += k;
        g.setColor(table.getBackground());
        g.fillRect(rectangle2.x, rectangle2.y, rectangle2.width, rectangle2.height);
        if(table.getShowVerticalLines())
        {
            g.setColor(table.getGridColor());
            int i1 = rectangle2.x;
            int k1 = rectangle2.y;
            int l1 = (i1 + rectangle2.width) - 1;
            int i2 = (k1 + rectangle2.height) - 1;
            g.drawLine(i1 - 1, k1, i1 - 1, i2);
            g.drawLine(l1, k1, l1, i2);
        }
        for(int j1 = i; j1 <= j; j1++)
        {
            Rectangle rectangle3 = table.getCellRect(j1, l, false);
            rectangle3.x += k;
            paintCell(g, rectangle3, j1, l);
            if(table.getShowHorizontalLines())
            {
                g.setColor(table.getGridColor());
                Rectangle rectangle4 = table.getCellRect(j1, l, true);
                rectangle4.x += k;
                int j2 = rectangle4.x;
                int k2 = rectangle4.y;
                int l2 = (j2 + rectangle4.width) - 1;
                int i3 = (k2 + rectangle4.height) - 1;
                g.drawLine(j2, i3, l2, i3);
            }
        }

    }

	private int viewIndexForColumn(TableColumn tablecolumn)
    {
        TableColumnModel tablecolumnmodel = table.getColumnModel();
        for(int i = 0; i < tablecolumnmodel.getColumnCount(); i++)
            if(tablecolumnmodel.getColumn(i) == tablecolumn)
                return i;

        return -1;
    }
}