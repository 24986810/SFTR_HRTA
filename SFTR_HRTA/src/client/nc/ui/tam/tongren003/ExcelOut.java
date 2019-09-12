package nc.ui.tam.tongren003;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


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
import jxl.format.UnderlineStyle;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;


public class ExcelOut {
	protected ClientEnvironment m_ceSingleton = null;
	protected Container c = null;
	private String year;
	private String period;
	private static int colcount =0;
	/**
	 * ��õ�ǰ�Ļ�����Ϣ��
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
	 * 2009-8-20 ����11:39:04
	 * ��ּ�
	 * ˵�������ɹ����Xls�ļ�ʱ ����true ���򷵻�false 
	 */
	public boolean createExcelFile(HashMap<String,Object[][]> map) {
		FileChooserRefPane fc = new FileChooserRefPane(c!=null?c:getClientEnvironment().getDesktopApplet(),year,period);
		if(map==null||map.size()<=0) {
			MessageDialog.showHintDlg(c!=null?c:fc,"��ʾ","��������Ϊ�գ�");
			return false;
		}
		try {
			int button = fc.onButtonClicked_b();
			if(button!=0) {
				MessageDialog.showHintDlg(c!=null?c:fc,"��ʾ","ȡ��������");
				return false;
			}
			String filePath = fc.getSelectedFilePath()+"";
			if(filePath == null||filePath.trim().length()<=0){
				MessageDialog.showHintDlg(c!=null?c:fc,"��ʾ","ȡ��������");
				return false;
			}
			File file = new File(filePath);

			if (file.exists()) {
				int x = MessageDialog.showOkCancelDlg(c!=null?c:fc,"��ʾ","������ͬ�ļ����ļ����Ƿ񸲸ǣ�");
				if(x!=UIDialog.ID_OK){
					MessageDialog.showHintDlg(c!=null?c:fc,"��ʾ","ȡ��������");
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
			MessageDialog.showHintDlg(c!=null?c:fc,"��ʾ",e.getMessage());
			return false;
		} catch (WriteException e) {
			e.printStackTrace();
			MessageDialog.showHintDlg(c!=null?c:fc,"��ʾ",e.getMessage());
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
				MessageDialog.showHintDlg(c!=null?c:fc,"��ʾ",""+keys[i]+"ҳǩ������������");
				return false;
			}
			if((listobj.get(0) instanceof Object[][])&&(listobj.get(1) instanceof Integer[])){
				value =(Object[][]) listobj.get(0);
				type = (Integer[])listobj.get(1);
				if(value==null||type==null){
					MessageDialog.showHintDlg(c!=null?c:fc,"��ʾ",""+keys[i]+"ҳǩ������������");
					return false; 
				}
				if(value[0].length!=type.length){
					MessageDialog.showHintDlg(c!=null?c:fc,"��ʾ",""+keys[i]+"ҳǩ������������");
					return false; 
				}
			}else{
				MessageDialog.showHintDlg(c!=null?c:fc,"��ʾ",""+keys[i]+"ҳǩ������������");
				return false;
			}
		}


		try {
			int button = fc.onButtonClicked_b();
			if(button!=0) {
				MessageDialog.showHintDlg(c!=null?c:fc,"��ʾ","ȡ��������");
				return false;
			}
			String filePath = fc.getSelectedFilePath()+"";
			if(filePath == null){
				MessageDialog.showHintDlg(c!=null?c:fc,"��ʾ","ȡ��������");
				return false;
			}

			File file = new File(filePath);

			if (file.exists()) {
				int x = MessageDialog.showOkCancelDlg(c!=null?c:fc,"��ʾ","������ͬ�ļ����ļ����Ƿ񸲸ǣ�");
				if(x!=UIDialog.ID_OK){
					MessageDialog.showHintDlg(c!=null?c:fc,"��ʾ","ȡ��������");
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
			MessageDialog.showHintDlg(c!=null?c:fc,"��ʾ",e.getMessage());
			return false;
		}
	}
	/**
	 * 
	 * @param listobj ��������ֵ����һ��Ϊ��ά���飬�ڶ���Ϊÿһ�е�������������
	 * ������HSSFCell.CELL_TYPE_NUMERIC ���ַ��ͣ�HSSFCell.CELL_TYPE_STRING
	 * @return
	 */
	public boolean createExcelFile(ArrayList listobj){
		FileChooserRefPane fc = new FileChooserRefPane(c!=null?c:getClientEnvironment().getDesktopApplet(),year,period);
		//�Ե��������ļ���
		if(listobj==null||listobj.size()!=2){
			MessageDialog.showHintDlg(c!=null?c:fc,"��ʾ","������������");
			return false;
		}
		Object[][] value;
		Integer[] type;

		if((listobj.get(0) instanceof Object[][])&&(listobj.get(1) instanceof Integer[])){
			value =(Object[][]) listobj.get(0);
			type = (Integer[])listobj.get(1);
			if(value==null||type==null){
				MessageDialog.showHintDlg(c!=null?c:fc,"��ʾ","������������");
				return false; 
			}
			if(value[0].length!=type.length){
				MessageDialog.showHintDlg(c!=null?c:fc,"��ʾ","������������");
				return false; 
			}
		}else{
			MessageDialog.showHintDlg(c!=null?c:fc,"��ʾ","������������");
			return false;
		}
		try {
			int button = fc.onButtonClicked_b();
			if(button!=0) {
				MessageDialog.showHintDlg(c!=null?c:fc,"��ʾ","ȡ��������");
				return false;
			}
			String filePath = fc.getSelectedFilePath()+"";
			if(filePath == null){
				MessageDialog.showHintDlg(c!=null?c:fc,"��ʾ","ȡ��������");
				return false;
			}

			File file = new File(filePath);

			if (file.exists()) {
				int x = MessageDialog.showOkCancelDlg(c!=null?c:fc,"��ʾ","������ͬ�ļ����ļ����Ƿ񸲸ǣ�");
				if(x!=UIDialog.ID_OK){
					MessageDialog.showHintDlg(c!=null?c:fc,"��ʾ","ȡ��������");
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
			sheet[0].setDefaultColumnWidth((short)(12));//�����п�
            //���õ���ͷ��Ϣ��һ������µ�һ��չʾ��Ϊ��ʾ�ֶ����ơ�
			createExcelHead(sheet[0],value[0],wb);
			//���þ���������
			createExcelBody(sheet[0],value,wb,type);


			// Write the output to a file
			wb.write(fileOut);
			fileOut.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			MessageDialog.showHintDlg(c!=null?c:fc,"��ʾ",e.getMessage());
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
//		������ͷ��Ϣ
		HSSFRow row = null;
		HSSFCell cell = null;
		row = sheet.getRow(0);
		if (row == null){
			row = sheet.createRow(0);
		}
		HSSFCellStyle style1 = wb.createCellStyle();
		style1.setAlignment(HSSFCellStyle.ALIGN_CENTER_SELECTION);
		HSSFFont font1 = wb.createFont();
		font1.setFontHeightInPoints((short)10);//���ø߶�
		font1.setFontName("Courier New");
		font1.setColor(HSSFColor.BLUE.index);//��ɫ
		style1.setFont(font1);
		style1.setBorderBottom(HSSFCellStyle.BORDER_DASHED);//���ñ߿�
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
			cell.setCellType(HSSFCell.CELL_TYPE_STRING);//������������
			cell.setCellStyle(style1);//�������ݵ���ʽ
			cell.setCellValue((String)value[i]);
		}
	}
	public boolean createExcelFile(Object[][] values) {
		FileChooserRefPane fc = new FileChooserRefPane(c!=null?c:getClientEnvironment().getDesktopApplet(),year,period);
		try {
			int button = fc.onButtonClicked_b();
			if(button!=0) {
				MessageDialog.showHintDlg(c!=null?c:fc,"��ʾ","ȡ��������");
				return false;
			}
			String filePath = fc.getSelectedFilePath()+"";
			if(filePath == null){
				MessageDialog.showHintDlg(c!=null?c:fc,"��ʾ","ȡ��������");
				return false;
			}

			File file = new File(filePath);

			if (file.exists()) {
				int x = MessageDialog.showOkCancelDlg(c!=null?c:fc,"��ʾ","������ͬ�ļ����ļ����Ƿ񸲸ǣ�");
				if(x!=UIDialog.ID_OK){
					MessageDialog.showHintDlg(c!=null?c:fc,"��ʾ","ȡ��������");
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
			MessageDialog.showHintDlg(c!=null?c:fc,"��ʾ",e.getMessage());
			return false;
		} catch (WriteException e) {
			e.printStackTrace();
			MessageDialog.showHintDlg(c!=null?c:fc,"��ʾ",e.getMessage());
			return false;
		}

	}
	
	public boolean createMergExcelFile(Object[][] values) throws Exception {
		FileChooserRefPane fc = new FileChooserRefPane(c!=null?c:getClientEnvironment().getDesktopApplet(),year,period);
		try {
			int button = fc.onButtonClicked_b();
			if(button!=0) {
				MessageDialog.showHintDlg(c!=null?c:fc,"��ʾ","ȡ��������");
				return false;
			}
			String filePath = fc.getSelectedFilePath()+"";
			if(filePath == null){
				MessageDialog.showHintDlg(c!=null?c:fc,"��ʾ","ȡ��������");
				return false;
			}

			File file = new File(filePath);

			if (file.exists()) {
				int x = MessageDialog.showOkCancelDlg(c!=null?c:fc,"��ʾ","������ͬ�ļ����ļ����Ƿ񸲸ǣ�");
				if(x!=UIDialog.ID_OK){
					MessageDialog.showHintDlg(c!=null?c:fc,"��ʾ","ȡ��������");
					return false;
				}
				file.delete();
			}
//			WritableWorkbook wwb = Workbook.createWorkbook(new File(filePath));
//			WritableSheet ws = wwb.createSheet("sheet1", 0);

//			for(int i = 0;i<values.length;i++){
//				for(int j = 0;j<values[i].length;j++){
//					String value =  values[i][j]!=null?values[i][j].toString():"";
//					Label label00 = new Label(j, i,value);
//					ws.addCell(label00);
//				}
//			}
			
			
			writeExcel(new File(filePath),values);
			

//			wwb.write();
//			wwb.close();

			return true;
		} catch (IOException e) {
			e.printStackTrace();
			MessageDialog.showHintDlg(c!=null?c:fc,"��ʾ",e.getMessage());
			return false;
		} catch (WriteException e) {
			e.printStackTrace();
			MessageDialog.showHintDlg(c!=null?c:fc,"��ʾ",e.getMessage());
			return false;
		}

	}
	
	public static void writeExcel(File os,Object[][] values)throws Exception  
	{  
	    try  
	    {  
	  
	    	jxl.write.WritableWorkbook wwb = Workbook.createWorkbook(os);  
	    	jxl.write.WritableSheet ws = wwb.createSheet("sheet1", 0);  

	        // �ϲ���Ԫ�����弰��ʾ��ʽ
	        WritableFont wf_merge = new WritableFont(WritableFont.ARIAL, 10, WritableFont.NO_BOLD,  
	        false, UnderlineStyle.NO_UNDERLINE, jxl.format.Colour.BLACK);  
	        WritableCellFormat wff_merge = new WritableCellFormat(wf_merge);  
	       
	        // �ϲ���ֱ����
	        WritableCellFormat cellFormat = new WritableCellFormat();
	        cellFormat.setVerticalAlignment(jxl.format.VerticalAlignment.CENTRE);
   
	       
	        
	        
	        ExcelPsnVO[] excelvos = getExcelPsnVOS(values);
	        
	        //����
	        for(int i=1;i<excelvos.length ;i++){
	        	for(int j=1;j<excelvos.length -i;j++){
	        		String psncode = excelvos[j].getPsncode();
	        		String nextPsncode = excelvos[j+1].getPsncode();
	        		if(psncode.compareTo(nextPsncode)>0 ){
	        			ExcelPsnVO temp = excelvos[j];
	        			excelvos[j] = excelvos[j+1];
	        			excelvos[j+1] = temp;
	        			
	        		}
	        	}
	        }
	        
	        
	        ArrayList<ExcelPsnVO> list = new ArrayList<ExcelPsnVO>();
	        ArrayList<ExcelPsnVO> psnlist = new ArrayList<ExcelPsnVO>();
	        ArrayList<ExcelPsnVO> monthlist = new ArrayList<ExcelPsnVO>();
	        ArrayList<ExcelPsnVO> daylist = new ArrayList<ExcelPsnVO>();
	        ArrayList<ExcelPsnVO> remarklist = new ArrayList<ExcelPsnVO>();
	        
	        String lastdept = "",lastpsn ="",lastmonth="",lastcontent ="",lastday="",lastremark="";
	        int isamecount = 0,ipsnsamecount = 0,imonthcount=0,icontentcount =0,idaysamecount=0,iremarksamecount=0;
	        int ibeginindex = 0,ipsnbeginindex = 0,imonthbeginindex=0,icontentbeginindex=0,idaybeginindex=0,iremarkbeginindex=0;
	        int iendindex = 0,ipsnendindex = 0,imonthendindex=0,icontentendindex=0,idayendindex=0,iremarkendindex=0;
	        
	        //begin
	        for(int i=0;i<excelvos.length;i++){
	        	ExcelPsnVO excelvo = excelvos[i];

	        	if(excelvo.getDept().equals(lastdept)){
	        		isamecount = isamecount +1;
	        		
    	        	if(isamecount !=0){
    	        		ibeginindex = i-isamecount;
    	        		iendindex = i;
    	        	}
    	        	
    	        	// ��һ�ϲ���	
    	        	ExcelPsnVO oldmergvo = new ExcelPsnVO();
    	        	oldmergvo.setBeginindex(ibeginindex);
    	        	oldmergvo.setEndindex(iendindex-1);
    	        	
    	        	ExcelPsnVO mergvo = new ExcelPsnVO();
    	        	mergvo.setBeginindex(ibeginindex);
    	        	mergvo.setEndindex(iendindex);
    	        		
    	        	
    	        	for(int j=0;j<list.size();j++){
    	        		ExcelPsnVO mergvo1 = list.get(j);
    	        		// ȥ����һ�ϲ���
    	        		if(mergvo1.getBeginindex() == oldmergvo.getBeginindex() && mergvo1.getEndindex()==oldmergvo.getEndindex()){
    	        			list.remove(j);
    	        		}
    	        	}
    	        	
    	        	list.add(mergvo);
    	        	
	        	}else{
	        		isamecount=0;
	        	}

	        	
	        	//end
	        	
	        	
	        	
	        	// ��Ա�ϲ�
	        	if(excelvo.getPsncode().equals(lastpsn)){
	        		ipsnsamecount = ipsnsamecount +1;
	        		
    	        	if(ipsnsamecount !=0){
    	        		ipsnbeginindex = i-ipsnsamecount;
    	        		ipsnendindex = i;
    	        	}
    	        	
    	        	// ��һ�ϲ���	
    	        	ExcelPsnVO oldmergvo = new ExcelPsnVO();
    	        	oldmergvo.setBeginindex(ipsnbeginindex);
    	        	oldmergvo.setEndindex(ipsnendindex-1);
    	        	
    	        	ExcelPsnVO mergvo = new ExcelPsnVO();
    	        	mergvo.setBeginindex(ipsnbeginindex);
    	        	mergvo.setEndindex(ipsnendindex);
    	        		
    	        	
    	        	for(int j=0;j<psnlist.size();j++){
    	        		ExcelPsnVO mergvo1 = psnlist.get(j);
    	        		// ȥ����һ�ϲ���
    	        		if(mergvo1.getBeginindex() == oldmergvo.getBeginindex() && mergvo1.getEndindex()==oldmergvo.getEndindex()){
    	        			psnlist.remove(j);
    	        		}
    	        	}
    	        	
    	        	psnlist.add(mergvo);
    	        	
	        	}else{
	        		ipsnsamecount=0;
	        	}
	        	
	        	// ��Աend
	        
	        	// �����ϲ�
	        	String mergmonth = excelvo.getMonth()+excelvo.getPsncode();
	        	if(mergmonth.equals(lastmonth)){
	        		imonthcount = imonthcount +1;
	        		
    	        	if(imonthcount !=0){
    	        		imonthbeginindex = i-imonthcount;
    	        		imonthendindex = i;
    	        	}
    	        	
    	        	// ��һ�ϲ���	
    	        	ExcelPsnVO oldmergvo = new ExcelPsnVO();
    	        	oldmergvo.setBeginindex(imonthbeginindex);
    	        	oldmergvo.setEndindex(imonthendindex-1);
    	        	
    	        	ExcelPsnVO mergvo = new ExcelPsnVO();
    	        	mergvo.setBeginindex(imonthbeginindex);
    	        	mergvo.setEndindex(imonthendindex);
    	        		
    	        	
    	        	for(int j=0;j<monthlist.size();j++){
    	        		ExcelPsnVO mergvo1 = monthlist.get(j);
    	        		// ȥ����һ�ϲ���
    	        		if(mergvo1.getBeginindex() == oldmergvo.getBeginindex() && mergvo1.getEndindex()==oldmergvo.getEndindex()){
    	        			monthlist.remove(j);
    	        		}
    	        	}
    	        	
    	        	monthlist.add(mergvo);
    	        	
	        	}else{
	        		imonthcount=0;
	        	}
	        	
	        	// ����end
	        	
	        	if(colcount ==6){
		        	// �����ϲ�
	        		String mergdays = excelvo.getDays()+excelvo.getRemark();
		        	if(mergdays.equals(lastday)){
		        		idaysamecount = idaysamecount +1;
		        		
	    	        	if(idaysamecount !=0){
	    	        		idaybeginindex = i-idaysamecount;
	    	        		idayendindex = i;
	    	        	}
	    	        	
	    	        	// ��һ�ϲ���	
	    	        	ExcelPsnVO oldmergvo = new ExcelPsnVO();
	    	        	oldmergvo.setBeginindex(idaybeginindex);
	    	        	oldmergvo.setEndindex(idayendindex-1);
	    	        	
	    	        	ExcelPsnVO mergvo = new ExcelPsnVO();
	    	        	mergvo.setBeginindex(idaybeginindex);
	    	        	mergvo.setEndindex(idayendindex);
	    	        		
	    	        	
	    	        	for(int j=0;j<daylist.size();j++){
	    	        		ExcelPsnVO mergvo1 = daylist.get(j);
	    	        		// ȥ����һ�ϲ���
	    	        		if(mergvo1.getBeginindex() == oldmergvo.getBeginindex() && mergvo1.getEndindex()==oldmergvo.getEndindex()){
	    	        			daylist.remove(j);
	    	        		}
	    	        	}
	    	        	
	    	        	daylist.add(mergvo);
	    	        	
		        	}else{
		        		idaysamecount=0;
		        	}
		        	lastday = excelvo.getDays()+excelvo.getRemark();
		        	
		        	
		        	// ���ϲ�
		        	String mergremark = excelvo.getRemark()+excelvo.getPsncode();
		        	if(mergremark.equals(lastremark)){
		        		iremarksamecount = iremarksamecount +1;
		        		
	    	        	if(iremarksamecount !=0){
	    	        		iremarkbeginindex = i-iremarksamecount;
	    	        		iremarkendindex = i;
	    	        	}
	    	        	
	    	        	// ��һ�ϲ���	
	    	        	ExcelPsnVO oldmergvo = new ExcelPsnVO();
	    	        	oldmergvo.setBeginindex(iremarkbeginindex);
	    	        	oldmergvo.setEndindex(iremarkendindex-1);
	    	        	
	    	        	ExcelPsnVO mergvo = new ExcelPsnVO();
	    	        	mergvo.setBeginindex(iremarkbeginindex);
	    	        	mergvo.setEndindex(iremarkendindex);
	    	        		
	    	        	
	    	        	for(int j=0;j<remarklist.size();j++){
	    	        		ExcelPsnVO mergvo1 = remarklist.get(j);
	    	        		// ȥ����һ�ϲ���
	    	        		if(mergvo1.getBeginindex() == oldmergvo.getBeginindex() && mergvo1.getEndindex()==oldmergvo.getEndindex()){
	    	        			remarklist.remove(j);
	    	        		}
	    	        	}
	    	        	
	    	        	remarklist.add(mergvo);
	    	        	
		        	}else{
		        		iremarksamecount=0;
		        	}
		        	lastremark = excelvo.getRemark()+excelvo.getPsncode();
	        	}
	        	
	        	
	        	lastmonth = excelvo.getMonth()+excelvo.getPsncode();
	        	lastpsn = excelvo.getPsncode();
	        	lastdept = excelvo.getDept();
	        	
	        	//end
	        	
	        	jxl.write.NumberFormat nf = new jxl.write.NumberFormat("#0000.00"); // �������ָ�ʽ
				jxl.write.WritableCellFormat wcfN = new jxl.write.WritableCellFormat(nf); // ���ñ���ʽ

	        	jxl.write.Label label3011 = new Label(0, i, excelvo.getDept(), wff_merge);  
	        	jxl.write.Label label3012 = new Label(1, i, excelvo.getPsncode(), wff_merge);  
	        	jxl.write.Label label3013 = new Label(2, i, excelvo.getPanname(), wff_merge); 
	        	jxl.write.Label label3014 = new Label(3, i, excelvo.getMonth(), wff_merge);  
	        	jxl.write.Label label3015 = new Label(4, i, excelvo.getRemark(), wff_merge); 
	        	jxl.write.Label label3016 = new Label(5, i, excelvo.getDays(), wff_merge); 
	        	jxl.write.Label label3017 = new Label(6, i, excelvo.getNperiodnum(), wff_merge); 
	        	jxl.write.Label label3018 = new Label(7, i, excelvo.getWorkingage(), wff_merge); 
	        	jxl.write.Label label3019 = new Label(8, i, excelvo.getDept2(), wff_merge); 
	        	jxl.write.Label label3020 = new Label(9, i, excelvo.getPsncode2(), wff_merge); 
	        	jxl.write.Label label3021 = new Label(10, i, excelvo.getPanname2(), wff_merge); 
	        	jxl.write.Label label3022 = new Label(11, i, excelvo.getAppointmentheld(), wff_merge); 
	        	jxl.write.Label label3023 = new Label(12, i, excelvo.getMonthStr(), wff_merge); 
	        	jxl.write.Label label3024 = new Label(13, i, excelvo.getMonthNum(), wff_merge); 
//	        	jxl.write.Number label3017 = new jxl.write.Number(6,i,899.01,wcfN);

	        	
	        	label3011.setCellFormat(cellFormat);
	        	label3012.setCellFormat(cellFormat);// ����
	        	label3013.setCellFormat(cellFormat);
	        	label3014.setCellFormat(cellFormat);
	        	label3015.setCellFormat(cellFormat);
	        	label3016.setCellFormat(cellFormat);
	        	label3017.setCellFormat(cellFormat);
	        	label3018.setCellFormat(cellFormat);
	        	label3019.setCellFormat(cellFormat);
	        	label3020.setCellFormat(cellFormat);
	        	label3021.setCellFormat(cellFormat);
	        	label3022.setCellFormat(cellFormat);
	        	label3023.setCellFormat(cellFormat);
	        	label3024.setCellFormat(cellFormat);
	        	
	        	ws.addCell(label3011);
	        	ws.addCell(label3012);
	        	ws.addCell(label3013);
	        	ws.addCell(label3014);
	        	ws.addCell(label3015);
	        	ws.addCell(label3016);
	        	ws.addCell(label3017);
	        	ws.addCell(label3018);
	        	ws.addCell(label3019);
	        	ws.addCell(label3020);
	        	ws.addCell(label3021);
	        	ws.addCell(label3022);
	        	ws.addCell(label3023);
	        	ws.addCell(label3024);
	        }
	        
	        ExcelPsnVO[] merg = list.toArray(new ExcelPsnVO[0]);
	        for(int i=0;i<merg.length;i++){
	        	ExcelPsnVO mergvo = merg[i];
	        	ws.mergeCells(0, mergvo.beginindex, 0, mergvo.endindex); 
	        }
	        
	        ExcelPsnVO[] psnmerg = psnlist.toArray(new ExcelPsnVO[0]);
	        for(int i=0;i<psnmerg.length;i++){
	        	ExcelPsnVO mergvo = psnmerg[i];
	        	ws.mergeCells(1, mergvo.beginindex, 1, mergvo.endindex); 
	        	ws.mergeCells(2, mergvo.beginindex, 2, mergvo.endindex); 
	        }
	        
	        ExcelPsnVO[] monthmerg = monthlist.toArray(new ExcelPsnVO[0]);
	        for(int i=0;i<monthmerg.length;i++){
	        	ExcelPsnVO mergvo = monthmerg[i];
	        	ws.mergeCells(3, mergvo.beginindex, 3, mergvo.endindex); 
	        	//ws.mergeCells(4, mergvo.beginindex, 4, mergvo.endindex); 
	        }
	        
	        if(colcount ==6){
	        	ExcelPsnVO[] daymerg = daylist.toArray(new ExcelPsnVO[0]);
	 	        for(int i=0;i<daymerg.length;i++){
	 	        	ExcelPsnVO mergvo = daymerg[i];
	 	        	ws.mergeCells(5, mergvo.beginindex, 5, mergvo.endindex); 
	 	        	ws.mergeCells(4, mergvo.beginindex, 4, mergvo.endindex); 
	 	        }
	 	        
	 	        ExcelPsnVO[] remarkmerg = remarklist.toArray(new ExcelPsnVO[0]);
	 	        for(int i=0;i<remarkmerg.length;i++){
	 	        	ExcelPsnVO mergvo = remarkmerg[i];
	 	        	ws.mergeCells(4, mergvo.beginindex, 4, mergvo.endindex); 
	 	        }
	        }
	       
	        

	        wwb.write();  
	        wwb.close();  
	  
	    }  
	    catch (Exception e )  
	    {  
	        throw e;  
	    }  
	}  
	
	
   
	
	public static Map<String,ExcelPsnVO> getSameDept(ExcelPsnVO[] excelvos){
		int isamecount = 1;
		String lastdept = "";
		ArrayList<ExcelPsnVO> list = new ArrayList<ExcelPsnVO>();
		Map<String,ExcelPsnVO> map = new HashMap<String,ExcelPsnVO>();
		
		
		for(int i=0;i<excelvos.length;i++){
			 ExcelPsnVO excelvo = excelvos[i];
			 
			 if(lastdept.equals(excelvo.getDept())){
				 isamecount = isamecount +1;
				 ExcelPsnVO newexcelvo = new ExcelPsnVO();
				 newexcelvo.setDept(excelvo.getDept());
				 newexcelvo.setSamecount(isamecount);
				 
     			
     			map.put(excelvo.getDept(), newexcelvo);
			 }else{
				 ExcelPsnVO newexcelvo = new ExcelPsnVO();
				 newexcelvo.setDept(excelvo.getDept());
				 newexcelvo.setSamecount(isamecount);
				 
				
				 map.put(excelvo.getDept(), newexcelvo);
				 
				 isamecount = 1;
				 
			 }
			 
			lastdept = excelvo.getDept();
		 }
		 
		return map;
	}

	public static ExcelPsnVO[] getExcelPsnVOS(Object[][] values){
		
		ExcelPsnVO[] excelvos = new  ExcelPsnVO[values.length];
		
		for(int i = 0;i<values.length;i++){
			 ExcelPsnVO excelpsnvo = new ExcelPsnVO();
			 colcount = values[i].length;
				for(int j = 0;j<values[i].length;j++){
			        if(j == 0){
						String currdept = values[i][j]!=null?values[i][j].toString():"";
						excelpsnvo.setDept(currdept);
					}else if(j == 1){
						String currpsncode = values[i][j]!=null?values[i][j].toString():"";
						excelpsnvo.setPsncode(currpsncode);
					}else if(j == 2){
						String currpanname = values[i][j]!=null?values[i][j].toString():"";
						excelpsnvo.setPanname(currpanname);		
					}else if(j == 3){
						String month =  values[i][j]!=null?values[i][j].toString():"";
						excelpsnvo.setMonth(month);
					}else if(j == 4){
						String remark =  values[i][j]!=null?values[i][j].toString():"";
						excelpsnvo.setRemark(remark);
					}else if(j == 5){
						String days =  values[i][j]!=null?values[i][j].toString():"";
						excelpsnvo.setDays(days);
					}else if(j == 6){
						String nperiodnum =  values[i][j]!=null?values[i][j].toString():"";
						excelpsnvo.setNperiodnum(nperiodnum);
					}else if(j == 7){
						String workingage =  values[i][j]!=null?values[i][j].toString():"";
						excelpsnvo.setWorkingage(workingage);
					}else if(j == 8){
						String dept2 =  values[i][j]!=null?values[i][j].toString():"";
						excelpsnvo.setDept2(dept2);
					}else if(j == 9){
						String psncode2 =  values[i][j]!=null?values[i][j].toString():"";
						excelpsnvo.setPsncode2(psncode2);
					}else if(j == 10){
						String panname2 =  values[i][j]!=null?values[i][j].toString():"";
						excelpsnvo.setPanname2(panname2);
					}else if(j == 11){
						String appointmentheld =  values[i][j]!=null?values[i][j].toString():"";
						excelpsnvo.setAppointmentheld(appointmentheld);
					}else if(j==14){
						//�����·�
						String monthStr =  values[i][j]!=null?values[i][j].toString():"";
						excelpsnvo.setMonthStr(monthStr);
					}else if(j== 15){
						//����
						String monthNum =  values[i][j]!=null?values[i][j].toString():"";
						excelpsnvo.setMonthNum(monthNum);
					}
				}
				
				excelvos[i] = excelpsnvo;
			}
		 
		return excelvos;
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
		getUIFileChooser1().setSelectedFile(new File(""+year+"-"+period+".xls"));//���ó�ʼ�ļ���׺Ϊ.xls
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
