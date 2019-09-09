/**
 * 
 */
package nc.ui.hrp.pub.excel;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import nc.vo.pub.lang.UFDouble;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

/**
 * @author ssd
 * 
 */
public class ExcelFileReader {



	private String m_sFilePath = null;

	private int titleRow = 0;

	// field names
	private Hashtable m_hFieldNames = null;

	/** �������� */
	private FileInputStream fileIn = null;

	private POIFSFileSystem fs = null;

	private HSSFWorkbook workBook = null;
	private int row;
	private int count;
	private int sheetnum = 0;
	private String sheetname = null;
	public String getSheetname() {
		return sheetname;
	}

	public void setSheetname(String sheetname) {
		this.sheetname = sheetname;
	}

	public int getSheetnum() {
		return sheetnum;
	}

	public void setSheetnum(int sheetnum) {
		this.sheetnum = sheetnum;
	}

	/*
	 * ���� Javadoc��
	 * 
	 * @see nc.ui.pm.pub.file.readwrite.IFileReader#closeFile()
	 */
	public void closeFile() throws Exception {
		// TODO �Զ����ɷ������
		fileIn.close();

	}
	
	/**
	 * 
	 * @return
	 * ˧ӳ��
	 * 2011-9-2����04:07:49
	 * ˵������ȡ���е�ҳǩ����
	 */
	public int getNumberofSheets(){
		return workBook.getNumberOfSheets();
	}

	/*
	 * ���� Javadoc��
	 * 
	 * @see nc.ui.pm.pub.file.readwrite.IFileReader#getFieldCount(int)
	 */
	public int getFieldCount(int nSheet) {

		try {
			HSSFRow row = getFieldNameRow(getSheet(nSheet));
			return row.getPhysicalNumberOfCells();
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	/*
	 * ���� Javadoc��
	 * 
	 * @see nc.ui.pm.pub.file.readwrite.IFileReader#getFieldNames(int)
	 */
	public String[] getFieldNames(int nSheet) {
		// TODO �Զ����ɷ������
		return null;
	}

	/*
	 * ���� Javadoc��
	 * 
	 * @see nc.ui.pm.pub.file.readwrite.IFileReader#hasNextRecord(int)
	 */
	public boolean hasNextRecord(int nSheet) {
		// TODO �Զ����ɷ������
		return false;
	}

	/*
	 * ���� Javadoc��
	 * 
	 * @see nc.ui.pm.pub.file.readwrite.IFileReader#nextRecord(int)
	 */
	public Object[] nextRecord(int nSheet) throws Exception {
		// TODO �Զ����ɷ������
		return null;
	}

	/*
	 * ���� Javadoc��
	 * 
	 * @see nc.ui.pm.pub.file.readwrite.IFileReader#openFile(java.lang.String)
	 */
	public void openFile(String sPath) throws Exception {

		m_sFilePath = sPath;
		File file = new File(m_sFilePath);
		if (!file.exists()) {
			throw new Exception("Ŀ���ļ�����ʧ�ܣ�" + m_sFilePath);
		}

		try {
			/** ��XLS�ļ� */
			fileIn = new FileInputStream(m_sFilePath);
			fs = new POIFSFileSystem(fileIn);
			workBook = new HSSFWorkbook(fs);
//			if(getSheetname()!=null){
//			this.sheetnum = workBook.getSheet(getSheetname());
//			}
		} catch (Exception ne) {
			ne.printStackTrace();
			throw new Exception("��֧�ֵ�Excel�ļ���ʽ����ȡʧ��");
		}
	}

	/*
	 * ���� Javadoc��
	 * 
	 * @see nc.ui.pm.pub.file.readwrite.IFileReader#readAllRecords(int)
	 */
	public Object[][] readAllRecords(int nSheet) throws Exception {
		return null;
	}

	public Object[] readAllTable() throws Exception {

		int num = workBook.getNumberOfSheets();
		Vector v = new Vector();
		Object[] obj = null;
		for (int i = 0; i < num; i++) {
			if (workBook.getSheetName(i) != null) {
				v.addElement(workBook.getSheetName(i));
			}
		}
		if (v != null && v.size() > 0) {
			obj = new Object[v.size()];
			v.copyInto(obj);
		}
		return obj;
	}

	// ����������ֹ�д��һ�������� ������Դ�ڽӿ�
	public Object[][] readExcel() throws Exception {

		Object[][] obj = null;
		String tmpObjNumNo=null;
		HSSFSheet sheet = getSheet(0);
		
		int firstRow = sheet.getFirstRowNum();
		int lastRow = sheet.getLastRowNum();
		int firstColumn = sheet.getRow(firstRow).getFirstCellNum();
		int lastColunm = sheet.getRow(firstRow).getLastCellNum();	
		for (int j = 0; j < lastRow -firstRow+1; j++) {
			HSSFRow row = sheet.getRow(firstRow +j);
			if (row != null) { // ������
				lastColunm = Math.max(row.getLastCellNum(), lastColunm);
			}
		}
		obj = new Object[lastRow - firstRow+1][lastColunm];
		setRow(lastRow - firstRow+1);
		setCount(lastColunm - firstColumn);
		// lastRow-firstRow:��������
		for (int j = 0; j < lastRow -firstRow+1; j++) {
			HSSFRow row = sheet.getRow(firstRow +j);
			if (row != null) { // ������
				/** ��ȡ�в��� */
				int iFirstCellNum = row.getFirstCellNum(); // ��ʼ�����
				int iLastCellNum = row.getLastCellNum(); // ���������  
				for (int m = iFirstCellNum; m <iLastCellNum; m++) {
					HSSFCell cell = row.getCell((short) m);
					if (cell != null)
					{obj[j][m] = new Object();
					obj[j][m] = getCellValue(cell);
					}
				}
			}
		}

		return obj;
	}

	/**
	 * ���ӷ���������ֵΪList<Map>
	 * @return
	 * @throws Exception
	 */
	public List<Map<String,Object>> readExcelToMap() throws Exception {

		List<Map<String,Object>> listMap = new ArrayList<Map<String,Object>>();
		List<String> listHead = new ArrayList<String>();
		
		String tmpObjNumNo=null;
		HSSFSheet sheet = getSheet(0);
		
		int firstRow = sheet.getFirstRowNum();
		int lastRow = sheet.getLastRowNum();
		int firstColumn = sheet.getRow(firstRow).getFirstCellNum();
		int lastColunm = sheet.getRow(firstRow).getLastCellNum();	
		for (int j = 0; j < lastRow -firstRow+1; j++) {
			HSSFRow row = sheet.getRow(firstRow +j);
			if (row != null) { // ������
				lastColunm = Math.max(row.getLastCellNum(), lastColunm);
			}
		}

		setRow(lastRow - firstRow+1);
		setCount(lastColunm - firstColumn);
		// lastRow-firstRow:��������
		for (int j = 0; j < lastRow -firstRow+1; j++) {
			HSSFRow row = sheet.getRow(firstRow +j);
			
			Map<String,Object> map = new HashMap<String, Object>();
			if (row != null) { // ������
				/** ��ȡ�в��� */
				int iFirstCellNum = row.getFirstCellNum(); // ��ʼ�����
				int iLastCellNum = row.getLastCellNum(); // ���������  
				for (int m = iFirstCellNum; m <iLastCellNum; m++) {
					HSSFCell cell = row.getCell((short) m);
					if (cell != null){
						Object obj = getCellValue(cell);
						if(j==0){
							listHead.add(obj!=null?obj.toString():"");
						}else{
							map.put(listHead.get(m), obj);
						}
						
					}
				}
			}
			if(j!=0)  listMap.add(map);
		}

		return listMap;
	}
	
	
	/**
	 * ���ӷ���������ֵΪ<ҳǩ����,��<�У�����> >
	 * @return
	 * @throws Exception
	 */
	public Map<String,List<Map<String,Object>>> readExcelSheetNameToMap() throws Exception {

		Map<String,List<Map<String,Object>>> mapSheet = new HashMap<String,List<Map<String,Object>>>();
		
		Object[] sheetNames = readAllTable();
		
		
		for (Object sheetName : sheetNames) {
			
			HSSFSheet sheet = workBook.getSheet(sheetName.toString());
			
			List<Map<String,Object>> listMap = new ArrayList<Map<String,Object>>();
			List<String> listHead = new ArrayList<String>();
			
			String tmpObjNumNo=null;
			
			int firstRow = sheet.getFirstRowNum();
			int lastRow = sheet.getLastRowNum();
			if(sheet.getRow(firstRow)==null){
				continue;
			}
			int firstColumn = sheet.getRow(firstRow).getFirstCellNum();
			int lastColunm = sheet.getRow(firstRow).getLastCellNum();	
			for (int j = 0; j < lastRow -firstRow+1; j++) {
				HSSFRow row = sheet.getRow(firstRow +j);
				if (row != null) { // ������
					lastColunm = Math.max(row.getLastCellNum(), lastColunm);
				}
			}

			setRow(lastRow - firstRow+1);
			setCount(lastColunm - firstColumn);
			// lastRow-firstRow:��������
			for (int j = 0; j < lastRow -firstRow+1; j++) {
				HSSFRow row = sheet.getRow(firstRow +j);
				
				Map<String,Object> map = new HashMap<String, Object>();
				if (row != null) { // ������
					/** ��ȡ�в��� */
					int iFirstCellNum = row.getFirstCellNum(); // ��ʼ�����
					int iLastCellNum = row.getLastCellNum(); // ���������  
					for (int m = iFirstCellNum; m <iLastCellNum; m++) {
						HSSFCell cell = row.getCell((short) m);
						if (cell != null){
							Object obj = getCellValue(cell);
							if(j==0){
								listHead.add(obj!=null?obj.toString():"");
							}else{
								map.put(listHead.get(m), obj);
							}
							
						}
					}
				}
				if(j!=0)  listMap.add(map);
			}

			mapSheet.put(sheetName.toString(), listMap);
		}
		
		return mapSheet;
	}
	
	
	
	private HSSFSheet getSheet(int sheetIndex) throws Exception {
		if(sheetname!=null){
			if(workBook.getSheet(sheetname)==null)
				throw new Exception("ҳǩ:"+sheetname+" ��������Ҫ��ҳǩ��Χ֮��,ѡ��ģ�����");
			return workBook.getSheet(sheetname);
		}
		if (sheetnum < 0 || sheetnum > getSheetNum())
			throw new Exception("ҳǩ��������Ҫ��ҳǩ��Χ֮��");
		return workBook.getSheetAt(sheetnum);
	}

	private int getSheetNum() {
		return workBook.getNumberOfSheets();
	}

	private int getRecorderRows(HSSFSheet sheet) throws Exception {

		int iNumOfRows = sheet.getPhysicalNumberOfRows();
		return iNumOfRows;
		// if (iNumOfRows > getTitleRow())
		// return iNumOfRows - getTitleRow();
		// else
		// throw new Exception("��֧�ֵĸ�ʽ������ҳû������");
	}

	private HSSFRow getFieldNameRow(HSSFSheet sheet) throws Exception {

		if (getRecorderRows(sheet) >= 0)
			return sheet.getRow(getTitleRow());
		else
			throw new Exception("��֧�ֵĸ�ʽ������ҳû������");
	}

	private Object getCellValue(org.apache.poi.hssf.usermodel.HSSFCell cell)
	throws Exception {
		int type = cell.getCellType();

		try {
			switch (type) {
			case HSSFCell.CELL_TYPE_NUMERIC: {
				Object value = null;
//				String aa = cell.toString();
//				double bbvalue = cell.getNumericCellValue();
//				double aavalue = 0.0;
//				try{
//					aavalue = Double.parseDouble(aa);
//				}catch(Exception e){
//					Date d = cell.getDateCellValue();
//					return new UFDate(d).toString();
//				}
//				if(aavalue==bbvalue){
//					value = new UFDouble(cell.getNumericCellValue());
//					if (value.toString().indexOf(".") > 0) {
//						String s = value.toString().substring(
//								value.toString().indexOf("."),
//								value.toString().length());
//						char[] c = s.toCharArray();
//						int iPower = 0;
//						for (int i = c.length - 1; i >= 1; i--) {
//							if (c[i] != '0') {
//								iPower = i + 1;
//								break;
//							}
//						}
//						value = (new UFDouble(value.toString())).setScale(-iPower, UFDouble.ROUND_DOWN);
//					}
//				}else{
//					try{
//						if(cell.getDateCellValue()!=null){
//							Date d = cell.getDateCellValue();
//							value = new UFDate(d).toString();
//						}
//					}catch(Exception ee){
						value = new UFDouble(cell.getNumericCellValue());
						if (value.toString().indexOf(".") > 0) {
							String s = value.toString().substring(
									value.toString().indexOf("."),
									value.toString().length());
							char[] c = s.toCharArray();
							int iPower = 0;
							for (int i = c.length - 1; i >= 1; i--) {
								if (c[i] != '0') {
									iPower = i + 1;
									break;
								}
							}
							value = (new UFDouble(value.toString())).setScale(-iPower, UFDouble.ROUND_DOWN);
						}
//					}
//				}
				return value;
			}
			case HSSFCell.CELL_TYPE_STRING: {
				return cell.getStringCellValue() == null ? null : cell
						.getStringCellValue().toString();
			}
			case HSSFCell.CELL_TYPE_FORMULA: {
				Double obj = cell.getNumericCellValue();
				if (!obj.equals(Double.NaN))
					return new UFDouble(cell.getNumericCellValue()).toString();
				else
					return cell.getStringCellValue() == null ? null : cell
							.getStringCellValue().toString();
			}
			case HSSFCell.CELL_TYPE_BLANK: {
				return null;
			}
			case HSSFCell.CELL_TYPE_BOOLEAN: {
				System.out.println("����������δ����û�ж���");
				return null;
			}
			case HSSFCell.CELL_TYPE_ERROR: {
			}
			default:
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}


	/**
	 * @return ���� titleRow��
	 */
	public int getTitleRow() {
		return titleRow;
	}

	/**
	 * @param titleRow
	 *            Ҫ���õ� titleRow��
	 */
	public void setTitleRow(int titleRow) {
		this.titleRow = titleRow;
	}


	/**
	 * @return row
	 */
	public int getRow() {
		return row;
	}

	/**
	 * @param row Ҫ���õ� row
	 */
	public void setRow(int row) {
		this.row = row;
	}

	/**
	 * @return count
	 */
	public int getCount() {
		return count;
	}

	/**
	 * @param count Ҫ���õ� count
	 */
	public void setCount(int count) {
		this.count = count;
	}
}