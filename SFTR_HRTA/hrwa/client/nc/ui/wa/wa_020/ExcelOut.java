package nc.ui.wa.wa_020;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;


import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;




import nc.ui.hrp.pub.excel.UIFileChooserHRP;
import nc.ui.pub.ClientEnvironment;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UIDialog;
import nc.ui.pub.beans.UIRefPane;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;


public class ExcelOut {
	protected ClientEnvironment m_ceSingleton = null;
	protected Container c = null;
	private String year;
	private String period;
	/**
	 * 获得当前的环境信息。
	 * 
	 * @version (00-6-13 10:51:14)
	 * 
	 * @return ClientEnvironment
	 */
	protected ClientEnvironment getClientEnvironment() {
		if (m_ceSingleton == null) {
			m_ceSingleton = ClientEnvironment.getInstance();
		}
		return m_ceSingleton;
	}
	public ExcelOut() {
		// TODO Auto-generated constructor stub
	}
	public ExcelOut(Container c,String year,String period) {
		this.c = c;
		this.year = year;
		this.period = period;
		// TODO Auto-generated constructor stub
	}
	/**
	 * 2009-8-20 上午11:39:04
	 * 宋旨昊
	 * 说明：当成功输出Xls文件时 返回true 否则返回false 
	 */
	public boolean createExcelFile(HashMap<String,Object[][]> map) {
		FileChooserRefPane fc = new FileChooserRefPane(c!=null?c:getClientEnvironment().getDesktopApplet(),year,period);
		if(map==null||map.size()<=0) {
			MessageDialog.showHintDlg(c!=null?c:fc,"提示","导出数据为空！");
			return false;
		}
		try {
			int button = fc.onButtonClicked_b();
			if(button!=0) {
				MessageDialog.showHintDlg(c!=null?c:fc,"提示","取消导出！");
				return false;
			}
			String filePath = fc.getSelectedFilePath()+"";
			if(filePath == null||filePath.trim().length()<=0){
				MessageDialog.showHintDlg(c!=null?c:fc,"提示","取消导出！");
				return false;
			}
			File file = new File(filePath);

			if (file.exists()) {
				int x = MessageDialog.showOkCancelDlg(c!=null?c:fc,"提示","存在相同文件名文件，是否覆盖？");
				if(x!=UIDialog.ID_OK){
					MessageDialog.showHintDlg(c!=null?c:fc,"提示","取消导出！");
					return false;
				}
				file.delete();
			}
			WritableWorkbook wwb = Workbook.createWorkbook(new File(filePath));
			String[] keys = map.keySet().toArray(new String[0]);
			for(int i=0;i<keys.length;i++){
				WritableSheet wsa = wwb.createSheet(keys[i], i);
				Object[][] values = map.get(keys[i]);
				for(int j = 0;j<values.length;j++){
					for(int k = 0;k<values[j].length;k++){
						String value =  values[j][k]!=null?values[j][k].toString():"";
						Label label00 = new Label(k, j, value);
						wsa.addCell(label00);
					}
				}
			}

			wwb.write();
			wwb.close();

			return true;
		} catch (IOException e) {
			e.printStackTrace();
			MessageDialog.showHintDlg(c!=null?c:fc,"提示",e.getMessage());
			return false;
		} catch (WriteException e) {
			e.printStackTrace();
			MessageDialog.showHintDlg(c!=null?c:fc,"提示",e.getMessage());
			return false;
		}
	}
	public boolean createExcelFile1(HashMap<String,ArrayList> map){
		FileChooserRefPane fc = new FileChooserRefPane(c!=null?c:getClientEnvironment().getDesktopApplet(),year,period);
		if(map==null||map.size()<=0) return false;
		String[] keys = map.keySet().toArray(new String[0]);
		Object[][] value;
		Integer[] type;
		for(int i=0;i<keys.length;i++){
			ArrayList listobj = map.get(keys[i]);
			if(map.get(keys[i])==null||map.get(keys[i]).size()!=2){
				MessageDialog.showHintDlg(c!=null?c:fc,"提示",""+keys[i]+"页签导出参数错误");
				return false;
			}
			if((listobj.get(0) instanceof Object[][])&&(listobj.get(1) instanceof Integer[])){
				value =(Object[][]) listobj.get(0);
				type = (Integer[])listobj.get(1);
				if(value==null||type==null){
					MessageDialog.showHintDlg(c!=null?c:fc,"提示",""+keys[i]+"页签导出参数错误");
					return false; 
				}
				if(value[0].length!=type.length){
					MessageDialog.showHintDlg(c!=null?c:fc,"提示",""+keys[i]+"页签导出参数错误");
					return false; 
				}
			}else{
				MessageDialog.showHintDlg(c!=null?c:fc,"提示",""+keys[i]+"页签导出参数错误");
				return false;
			}
		}


		try {
			int button = fc.onButtonClicked_b();
			if(button!=0) {
				MessageDialog.showHintDlg(c!=null?c:fc,"提示","取消导出！");
				return false;
			}
			String filePath = fc.getSelectedFilePath()+"";
			if(filePath == null){
				MessageDialog.showHintDlg(c!=null?c:fc,"提示","取消导出！");
				return false;
			}

			File file = new File(filePath);

			if (file.exists()) {
				int x = MessageDialog.showOkCancelDlg(c!=null?c:fc,"提示","存在相同文件名文件，是否覆盖？");
				if(x!=UIDialog.ID_OK){
					MessageDialog.showHintDlg(c!=null?c:fc,"提示","取消导出！");
					return false;
				}
				file.delete();
				file.createNewFile();
			}else{
				file.createNewFile();
			}
			FileOutputStream fileOut = new FileOutputStream(filePath, false);
			HSSFWorkbook wb = new HSSFWorkbook();

			HSSFSheet[] sheet = new HSSFSheet[keys.length];
			for(int i=0;i<keys.length;i++){
				value = (Object[][])map.get(keys[i]).get(0);
				type = (Integer[])map.get(keys[i]).get(1);
				sheet[i] = wb.createSheet(keys[i]);
				sheet[i].setDefaultColumnWidth((short)(12));

				createExcelHead(sheet[i],value[0],wb);
				createExcelBody(sheet[i],value,wb,type);
			}

			// Write the output to a file
			wb.write(fileOut);
			fileOut.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			MessageDialog.showHintDlg(c!=null?c:fc,"提示",e.getMessage());
			return false;
		}
	}
	/**
	 * 
	 * @param listobj 包含两个值，第一个为二维数组，第二个为每一列的数据类型数组
	 * 数组型HSSFCell.CELL_TYPE_NUMERIC ；字符型：HSSFCell.CELL_TYPE_STRING
	 * @return
	 */
	public boolean createExcelFile(ArrayList listobj){
		FileChooserRefPane fc = new FileChooserRefPane(c!=null?c:getClientEnvironment().getDesktopApplet(),year,period);
		//对导出参数的检验
		if(listobj==null||listobj.size()!=2){
			MessageDialog.showHintDlg(c!=null?c:fc,"提示","导出参数错误");
			return false;
		}
		Object[][] value;
		Integer[] type;

		if((listobj.get(0) instanceof Object[][])&&(listobj.get(1) instanceof Integer[])){
			value =(Object[][]) listobj.get(0);
			type = (Integer[])listobj.get(1);
			if(value==null||type==null){
				MessageDialog.showHintDlg(c!=null?c:fc,"提示","导出参数错误");
				return false; 
			}
			if(value[0].length!=type.length){
				MessageDialog.showHintDlg(c!=null?c:fc,"提示","导出参数错误");
				return false; 
			}
		}else{
			MessageDialog.showHintDlg(c!=null?c:fc,"提示","导出参数错误");
			return false;
		}
		try {
			int button = fc.onButtonClicked_b();
			if(button!=0) {
				MessageDialog.showHintDlg(c!=null?c:fc,"提示","取消导出！");
				return false;
			}
			String filePath = fc.getSelectedFilePath()+"";
			if(filePath == null){
				MessageDialog.showHintDlg(c!=null?c:fc,"提示","取消导出！");
				return false;
			}

			File file = new File(filePath);

			if (file.exists()) {
				int x = MessageDialog.showOkCancelDlg(c!=null?c:fc,"提示","存在相同文件名文件，是否覆盖？");
				if(x!=UIDialog.ID_OK){
					MessageDialog.showHintDlg(c!=null?c:fc,"提示","取消导出！");
					return false;
				}
				file.delete();
				file.createNewFile();
			}else{
				file.createNewFile();
			}
			FileOutputStream fileOut = new FileOutputStream(filePath, false);
			HSSFWorkbook wb = new HSSFWorkbook();
			HSSFSheet[] sheet = new HSSFSheet[1];
			sheet[0] = wb.createSheet("Sheet1");
			sheet[0].setDefaultColumnWidth((short)(12));//设置列宽
            //设置导出头信息，一般情况下第一行展示的为显示字段名称。
			createExcelHead(sheet[0],value[0],wb);
			//设置具体行数据
			createExcelBody(sheet[0],value,wb,type);


			// Write the output to a file
			wb.write(fileOut);
			fileOut.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			MessageDialog.showHintDlg(c!=null?c:fc,"提示",e.getMessage());
			return false;
		}
	}
	private void createExcelBody(HSSFSheet sheet,Object[][] value,HSSFWorkbook wb,Integer[] type) {
		HSSFRow row = null;
		HSSFCell cell = null;
		for(int i=1;i<value.length;i++){
			row = sheet.getRow(i);
			if (row == null){
				row = sheet.createRow(i);
			}
			for(int j=0;j<value[i].length;j++){
				cell = row.getCell((short) j);
				if (cell == null)
					cell = row.createCell((short) j);

				cell.setCellType(type[j]);
				if(type[j]==HSSFCell.CELL_TYPE_NUMERIC){
					if(value[i][j]!=null){
						cell.setCellValue(Double.parseDouble(value[i][j].toString()));
					}
				}else if(type[j]==HSSFCell.CELL_TYPE_STRING){
					cell.setCellValue(value[i][j]!=null?value[i][j].toString():"");
				}else if(type[j]==HSSFCell.CELL_TYPE_BOOLEAN){
					if(value[i][j]!=null){
						cell.setCellValue(Boolean.parseBoolean(value[i][j].toString()));
					}
				}
			}
		}
	}
	private void createExcelHead(HSSFSheet sheet,Object[] value,HSSFWorkbook wb) {
//		创建表头信息
		HSSFRow row = null;
		HSSFCell cell = null;
		row = sheet.getRow(0);
		if (row == null){
			row = sheet.createRow(0);
		}
		HSSFCellStyle style1 = wb.createCellStyle();
		style1.setAlignment(HSSFCellStyle.ALIGN_CENTER_SELECTION);
		HSSFFont font1 = wb.createFont();
		font1.setFontHeightInPoints((short)10);//设置高度
		font1.setFontName("Courier New");
		font1.setColor(HSSFColor.BLUE.index);//颜色
		style1.setFont(font1);
		style1.setBorderBottom(HSSFCellStyle.BORDER_DASHED);//设置边框
		style1.setBottomBorderColor(HSSFColor.BLACK.index);
		style1.setBorderTop(HSSFCellStyle.BORDER_MEDIUM_DASHED);
		style1.setTopBorderColor(HSSFColor.BLACK.index);
		style1.setBorderLeft(HSSFCellStyle.BORDER_MEDIUM_DASHED);
		style1.setLeftBorderColor(HSSFColor.BLUE.index);
		style1.setBorderRight(HSSFCellStyle.BORDER_MEDIUM_DASHED);
		style1.setRightBorderColor(HSSFColor.BLUE.index);

		for(int i=0;i<value.length;i++){
			cell = row.getCell((short) i);
			if (cell == null)
				cell = row.createCell((short) i);
			cell.setCellType(HSSFCell.CELL_TYPE_STRING);//设置数据类型
			cell.setCellStyle(style1);//设置数据的样式
			cell.setCellValue((String)value[i]);
		}
	}
	public boolean createExcelFile(Object[][] values) {
		FileChooserRefPane fc = new FileChooserRefPane(c!=null?c:getClientEnvironment().getDesktopApplet(),year,period);
		try {
			int button = fc.onButtonClicked_b();
			if(button!=0) {
				MessageDialog.showHintDlg(c!=null?c:fc,"提示","取消导出！");
				return false;
			}
			String filePath = fc.getSelectedFilePath()+"";
			if(filePath == null){
				MessageDialog.showHintDlg(c!=null?c:fc,"提示","取消导出！");
				return false;
			}

			File file = new File(filePath);

			if (file.exists()) {
				int x = MessageDialog.showOkCancelDlg(c!=null?c:fc,"提示","存在相同文件名文件，是否覆盖？");
				if(x!=UIDialog.ID_OK){
					MessageDialog.showHintDlg(c!=null?c:fc,"提示","取消导出！");
					return false;
				}
				file.delete();
			}
			WritableWorkbook wwb = Workbook.createWorkbook(new File(filePath));
			WritableSheet ws = wwb.createSheet("sheet1", 0);

			for(int i = 0;i<values.length;i++){
				for(int j = 0;j<values[i].length;j++){
					String value =  values[i][j]!=null?values[i][j].toString():"";
					Label label00 = new Label(j, i,value);
					ws.addCell(label00);
				}
			}

			wwb.write();
			wwb.close();

			return true;
		} catch (IOException e) {
			e.printStackTrace();
			MessageDialog.showHintDlg(c!=null?c:fc,"提示",e.getMessage());
			return false;
		} catch (WriteException e) {
			e.printStackTrace();
			MessageDialog.showHintDlg(c!=null?c:fc,"提示",e.getMessage());
			return false;
		}

	}
	public void on(){
		Object[][] pp = {{2,3,4},{4,3,""},{4,3,6}};
		createExcelFile(pp);
	}
}

class FileChooserRefPane extends UIRefPane {
	private UIFileChooserHRP ivjUIFileChooser1 = null;
	private Container c = null;
	private String year;
	private String period;
	public FileChooserRefPane() {
		initialize();
	}

	public FileChooserRefPane(Container parent,String year,String period) {
		super(parent);
		this.c = parent;
		this.c = parent;
		this.year = year;
		this.period = period;
		initialize();
	}

	public FileChooserRefPane(LayoutManager p0) {
		super(p0);
		initialize();
	}

	public FileChooserRefPane(LayoutManager p0, boolean p1) {
		super(p0, p1);
		initialize();
	}

	public FileChooserRefPane(boolean p0) {
		super(p0);
		initialize();
	}

	public String getSelectedFilePath() {
		return getUITextField().getText();
	}

	private UIFileChooserHRP getUIFileChooser1() {
		if (ivjUIFileChooser1 == null) {
			try {
				ivjUIFileChooser1 = new UIFileChooserHRP();
				ivjUIFileChooser1.setName("UIFileChooser1");
				ivjUIFileChooser1.setBounds(218, 85, 500, 300);
			} catch (Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return ivjUIFileChooser1;
	}

	private void handleException(Throwable exception) {
		exception.printStackTrace(System.out);
	}

	private void initialize() {
		try {
			setName("FileChooserRefPane");
			setSize(200, 22);
			setPreferredSize(new Dimension(200, 22));
			getUITextField().setMaxLength(400);
		} catch (Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	public void onButtonClicked() {
		int result = getUIFileChooser1().showSaveDialog(this.c!=null?this.c:ClientEnvironment.getInstance().getDesktopApplet());
		if (result == getUIFileChooser1().APPROVE_OPTION && getUIFileChooser1().getSelectedFile() != null){
			getUITextField().setText(
					getUIFileChooser1().getSelectedFile().getPath());
		}else{
			getUITextField().setText(null);
		}
	}
	public int onButtonClicked_b() {
		getUIFileChooser1().setSelectedFile(new File(""+year+"-"+period+".xls"));//设置初始文件后缀为.xls
		int result = getUIFileChooser1().showSaveDialog(this.c!=null?this.c:ClientEnvironment.getInstance().getDesktopApplet());
		if (result == getUIFileChooser1().APPROVE_OPTION && getUIFileChooser1().getSelectedFile() != null){
			getUITextField().setText(
					getUIFileChooser1().getSelectedFile().getPath());
		}else{
			getUITextField().setText(null);
		}
		return result;
	}

}
