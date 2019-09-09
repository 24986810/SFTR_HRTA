package nc.ui.pub.bill;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

import nc.ui.pub.beans.table.CellFont;
import nc.ui.pub.beans.table.ColoredCell;
import nc.ui.pub.bill.BillModel.TotalTableModel;
import nc.vo.pub.bill.BillRendererVO;
import nc.vo.pub.lang.UFDouble;

/**
 * 
 * 创建日期:(01-2-28 14:49:01)
 */
@SuppressWarnings("serial")
public class BillTableCellRenderer extends JLabel implements TableCellRenderer,
		Serializable ,IBillTableColorCellRenderer{
	protected static Border noFocusBorder;

	// We need a place to store the color the JLabel should be returned
	// to after its foreground and background colors have been set
	// to the selection background color.
	// These ivars will be made protected when their names are finalized.
	private Color unselectedForeground;

	private Color unselectedBackground;

	private int m_iDataType = 0; // 数据类型

	// private int m_iDecimalDigits = 0; // 小数位数

	private BillRendererVO paraVO = new BillRendererVO();
	
	private BillItemNumberFormat numberFormat = null;

	// {(row,col),Color}
	private HashMap<String, Color> hashBackGround = new HashMap<String, Color>();

	private HashMap<String, Color> hashForeGround = new HashMap<String, Color>();
	
	private static BillTableCellRenderer cellrender = new BillTableCellRenderer();

	/**
	 * Creates a default table cell renderer.
	 */
	public BillTableCellRenderer() {
		super();
		noFocusBorder = new EmptyBorder(1, 2, 1, 2);
		setOpaque(true);
		setBorder(noFocusBorder);
	}

	/**
	 * BillTableCellRenderer 构造子注解.
	 */
	public BillTableCellRenderer(BillItem item) {
		this();
		m_iDataType = item.getDataType();
		numberFormat = item.getNumberFormat();
//		if (item.getNumberFormat() != null) {
//			paraVO.setNegativeSign(item.getNumberFormat().isNegativeSign());
//			paraVO.setShowRed(item.getNumberFormat().isShowRed());
//			paraVO.setShowThMark(item.getNumberFormat().isShowThMark());
//			paraVO.setShowZeroLikeNull(item.getNumberFormat().isShowZeroLikeNull());
//		}
	}
	
	

	/**
	 * BillTableCellRenderer 构造子注解.
	 */
	public BillTableCellRenderer(BillItem item, BillRendererVO newParameterVO) {
		this(item.getDataType(), newParameterVO);
	}

	/**
	 * BillTableCellRenderer 构造子注解.
	 */
	public BillTableCellRenderer(int dataType, BillRendererVO newParameterVO) {
		this();
		m_iDataType = dataType;
		paraVO = newParameterVO;
	}

	/**
	 * 
	 * 创建日期:(2003-6-19 16:29:41)
	 * 
	 * @return java.awt.Color
	 */
	public Color getBackGround(int row, int col) {
		return hashBackGround.get(row + "," + col);
	}

	// 获得数据类型
	public int getDataType() {
		return m_iDataType;
	}

	// // 获得小数位数
	// public int getDecimalDigits() {
	// return m_iDecimalDigits;
	// }

	/**
	 * 
	 * 创建日期:(2003-6-19 16:29:41)
	 * 
	 * @return java.awt.Color
	 */
	public Color getForeGround(int row, int col) {
		return hashForeGround.get(row + "," + col);
	}

	// 负数是否显示符号
	private boolean isNegativeSign() {
		
		if(getNumberFormat() != null)
			return getNumberFormat().isNegativeSign();
		
		return paraVO.isNegativeSign();
	}

	// 负数是否显示红字
	private boolean isShowRed() {
		if(getNumberFormat() != null)
			return getNumberFormat().isShowRed();

		return paraVO.isShowRed();
	}

	// 是否显示千分位
	private boolean isShowThMark() {
		if(getNumberFormat() != null)
			return getNumberFormat().isShowThMark();
		return paraVO.isShowThMark();
	}

	// 是否将零显示为空串
	private boolean isShowZeroLikeNull() {
		if(getNumberFormat() != null)
			return getNumberFormat().isShowZeroLikeNull();
		return paraVO.isShowZeroLikeNull();
	}

	// 是否显示百分比
	private boolean isShowPercent() {
		return paraVO.isShowPercent();
	}

	// implements javax.swing.table.TableCellRenderer
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		Color color = null;
		TableModel model = table.getModel();
		
		TableModel mainmodel = model;
		
		 if(model instanceof TotalTableModel) {
			 mainmodel = ((TotalTableModel)model).getMainModel();
		 }
		
		if(mainmodel != null && mainmodel instanceof BillModel) {
			int modelcolumn = table.convertColumnIndexToModel(column);
			BillItem item = ((BillModel)mainmodel).getBodyItems()[modelcolumn];
			m_iDataType = item.getDataType();
			unselectedForeground = null;
			unselectedBackground = null;
			setNumberFormat(item.getNumberFormat());
		}

		if (isSelected) {
			super.setForeground(table.getSelectionForeground());
			super.setBackground(table.getSelectionBackground());
		} else {
			

			boolean ismaintable = false; 

			if(model instanceof BillModel) {
				ismaintable = true;
			}
			
			if(model instanceof ColoredCell) {
				
				int modelcolumn = table.convertColumnIndexToModel(column);
				
				color = ((ColoredCell)model).getBackground(row, modelcolumn);
				if (color != null)
					super.setBackground(color);
				else
					super.setBackground((unselectedBackground != null) ? unselectedBackground : table.getBackground());
				
				color = ((ColoredCell)model).getForeground(row, modelcolumn);
				if (color != null)
					super.setForeground(color);
				else
//					super.setForeground( table.getForeground());
					super.setForeground((unselectedForeground != null) ? unselectedForeground : table.getForeground());
				
//				super.setForeground(unselectedForeground);
			
			}else {
				if ((color = getBackGround(row, column)) != null && ismaintable)
					super.setBackground(color);
				else
					super.setBackground((unselectedBackground != null) ? unselectedBackground : table.getBackground());
				if ((color = getForeGround(row, column)) != null && ismaintable)
					super.setForeground(color);
				else
					super.setForeground((unselectedForeground != null) ? unselectedForeground : table.getForeground());
			}
			
		}
		
		Font font = null;
		
		if(model instanceof CellFont) {
			font = ((CellFont)model).getFont(row, column);
			if(font == null)
				font = table.getFont();
		}
		
		setFont(font);

		if (hasFocus) {
			setBorder(UIManager.getBorder("Table.focusCellHighlightBorder"));
			if (table.isCellEditable(row, column)) {
				super.setForeground(UIManager.getColor("Table.focusCellForeground"));
				super.setBackground(UIManager.getColor("Table.focusCellBackground"));
			}
		} else {
			setBorder(noFocusBorder);
		}

		// when datatype is integer or decimal,the foreground can be modified;
		setValue(value);
		

		// reset foreground
		if (color != null)
			super.setForeground(color);
		return this;
	}

	public void resumeDefaultBackGround() {
		hashBackGround.clear();
	}

	public void resumeDefaultForeGround() {
		hashForeGround.clear();
	}

	/**
	 * 
	 * 创建日期:(2003-6-19 16:29:41)
	 * 
	 * @param newBackGround
	 *            java.awt.Color
	 */
	public void setBackGround(int row, int col, java.awt.Color color) {
		if (color == null)
			hashBackGround.remove(row + "," + col);
		else
			hashBackGround.put(row + "," + col, color);
	}

	/**
	 * Overrides <code>JComponent.setForeground</code> to specify the
	 * unselected-background color using the specified color.
	 */
	public void setBackground(Color c) {
		super.setBackground(c);
		unselectedBackground = c;
	}

	// // 设置数据类型
	// public void setDataType(int iDataType) {
	// m_iDataType = iDataType;
	// }

	// // 获得小数位数
	// public void setDecimalDigits(int iDecimalDigits) {
	// m_iDecimalDigits = iDecimalDigits;
	// }

	/**
	 * 
	 * 创建日期:(2003-6-19 16:29:41)
	 * 
	 * @param newForeGround
	 *            java.awt.Color
	 */
	public void setForeGround(int row, int col, java.awt.Color color) {
		if (color == null)
			hashForeGround.remove(row + "," + col);
		else
			hashForeGround.put(row + "," + col, color);
	}

	/**
	 * Overrides <code>JComponent.setForeground</code> to specify the
	 * unselected-foreground color using the specified color.
	 */
	public void setForeground(Color c) {
		super.setForeground(c);
		unselectedForeground = c;
	}

	/**
	 * 加入千分位标志. 创建日期:(2002-01-29 14:51:16)
	 * 
	 * @param str
	 *            java.lang.String
	 * @return java.lang.String
	 */
	public static String setMark(String str) {
		if ((str == null) || (str.trim().length() < 3))
			return str;
		str = str.trim();
		String str0 = "";
		String str1 = str;
		if (str.startsWith("-")) {
			str0 = "-";
			str1 = str.substring(1);
		}
		int pointIndex = str1.indexOf(".");
		String str2 = "";
		String newStr = "";

		// 小数
		if (pointIndex != -1) {
			str2 = str1.substring(pointIndex);
			str1 = str1.substring(0, pointIndex);
		}
		int ii = (str1.length() - 1) % 3;
		for (int i = 0; i < str1.length(); i++) {
			newStr += str1.charAt(i);
			if (ii <= 0) {
				newStr += ",";
				ii += 2;
			} else
				ii--;
			ii = ii % 3;
		}
		if (newStr.length() > 0)
			newStr = newStr.substring(0, newStr.length() - 1) + str2;
		else
			newStr = str2;
		newStr = str0 + newStr;
		return newStr;
	}

	// // 设置负数是否显示符号
	// public void setNegativeSign(boolean newValue) {
	// paraVO.setNegativeSign(newValue);
	// }
	//
	// // 设置负数是否显示红字
	// public void setShowRed(boolean newValue) {
	// paraVO.setShowRed(newValue);
	// }
	//
	// // 设置是否显示千分位
	// public void setShowThMark(boolean newValue) {
	// paraVO.setShowThMark(newValue);
	// }
	//
	// 设置是否将零显示为空串
	public void setShowZeroLikeNull(boolean newValue) {
		paraVO.setShowZeroLikeNull(newValue);
	}

	@SuppressWarnings("unchecked")
	protected void setValue(Object value) {
		setHorizontalAlignment(SwingConstants.LEFT);

		if (value == null || value.equals("")) {
			setText("");
		} else {
			// 数据类型
			switch (m_iDataType) {
			case BillItem.INTEGER:
			case BillItem.DECIMAL:
				if (m_iDataType == BillItem.INTEGER || m_iDataType == BillItem.DECIMAL) {
					setHorizontalAlignment(SwingConstants.RIGHT);
					setForeground(java.awt.Color.black);

					// 辅助量小计合计特殊处理,其值为ArrayList
					if (value instanceof ArrayList) {
						StringBuffer valueBuf = new StringBuffer();
						ArrayList valueList = (ArrayList) value;
						int unitNum = valueList.size() / 2;
						for (int i = 0; i < unitNum; i++) {
							if (i > 0) {
								valueBuf.append("/ ");
							}
							valueBuf.append(valueList.get(i * 2));
							valueBuf.append(valueList.get(i * 2 + 1));
						}
						value = valueBuf.toString();
					} else {
						String v = value.toString();

						double dou = 0;

						if (value instanceof UFDouble)
							dou = ((UFDouble) value).doubleValue();
						else
							dou = Double.valueOf(v);

						if (dou == 0) {
							if (isShowZeroLikeNull())
								value = "0.00";
						} else {
							// 负数
							if (dou < 0) {
								if (isShowRed())
									setForeground(java.awt.Color.red);
								if (!isNegativeSign()) {
									value = v.replaceFirst("-", "");
									v = value.toString();
								}
							} else if (isShowPercent()) {
								dou = dou * 100;
								value = dou + "%";
								v = value.toString();
							}
							// 显示千分位
							if (isShowThMark()) {
								value = setMark(v);
							}
						}
					}
				}
				break;
			case BillItem.PASSWORDFIELD:
				value = "********";
				break;
			default:
				break;
			}
			setText(value.toString());
		}
	}

	/**
	 * Notification from the UIManager that the L&F has changed. Replaces the
	 * current UI object with the latest version from the UIManager.
	 * 
	 * @see JComponent#updateUI
	 */
	public void updateUI() {
		super.updateUI();
		setForeground(null);
		setBackground(null);
	}

	public BillItemNumberFormat getNumberFormat() {
		return numberFormat;
	}

	public void setNumberFormat(BillItemNumberFormat numberFormat) {
		this.numberFormat = numberFormat;
	}

	public static BillTableCellRenderer getInstance() {
		return cellrender;
	}
}
