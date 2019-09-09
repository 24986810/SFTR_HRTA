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

	/** 变量定义 */
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
	 * （非 Javadoc）
	 * 
	 * @see nc.ui.pm.pub.file.readwrite.IFileReader#closeFile()
	 */
	public void closeFile() throws Exception {
		// TODO 自动生成方法存根
		fileIn.close();

	}
	
	/**
	 * 
	 * @return
	 * 帅映杰
	 * 2011-9-2下午04:07:49
	 * 说明：获取所有的页签数量
	 */
	public int getNumberofSheets(){
		return workBook.getNumberOfSheets();
	}

	/*
	 * （非 Javadoc）
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
	 * （非 Javadoc）
	 * 
	 * @see nc.ui.pm.pub.file.readwrite.IFileReader#getFieldNames(int)
	 */
	public String[] getFieldNames(int nSheet) {
		// TODO 自动生成方法存根
		return null;
	}

	/*
	 * （非 Javadoc）
	 * 
	 * @see nc.ui.pm.pub.file.readwrite.IFileReader#hasNextRecord(int)
	 */
	public boolean hasNextRecord(int nSheet) {
		// TODO 自动生成方法存根
		return false;
	}

	/*
	 * （非 Javadoc）
	 * 
	 * @see nc.ui.pm.pub.file.readwrite.IFileReader#nextRecord(int)
	 */
	public Object[] nextRecord(int nSheet) throws Exception {
		// TODO 自动生成方法存根
		return null;
	}

	/*
	 * （非 Javadoc）
	 * 
	 * @see nc.ui.pm.pub.file.readwrite.IFileReader#openFile(java.lang.String)
	 */
	public void openFile(String sPath) throws Exception {

		m_sFilePath = sPath;
		File file = new File(m_sFilePath);
		if (!file.exists()) {
			throw new Exception("目标文件访问失败：" + m_sFilePath);
		}

		try {
			/** 打开XLS文件 */
			fileIn = new FileInputStream(m_sFilePath);
			fs = new POIFSFileSystem(fileIn);
			workBook = new HSSFWorkbook(fs);
//			if(getSheetname()!=null){
//			this.sheetnum = workBook.getSheet(getSheetname());
//			}
		} catch (Exception ne) {
			ne.printStackTrace();
			throw new Exception("不支持的Excel文件格式，读取失败");
		}
	}

	/*
	 * （非 Javadoc）
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

	// 这个方法是手工写的一个方法， 不是来源于接口
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
			if (row != null) { // 有数据
				lastColunm = Math.max(row.getLastCellNum(), lastColunm);
			}
		}
		obj = new Object[lastRow - firstRow+1][lastColunm];
		setRow(lastRow - firstRow+1);
		setCount(lastColunm - firstColumn);
		// lastRow-firstRow:不读标题
		for (int j = 0; j < lastRow -firstRow+1; j++) {
			HSSFRow row = sheet.getRow(firstRow +j);
			if (row != null) { // 有数据
				/** 获取列参数 */
				int iFirstCellNum = row.getFirstCellNum(); // 开始列序号
				int iLastCellNum = row.getLastCellNum(); // 结束列序号  
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
	 * 增加方法，返回值为List<Map>
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
			if (row != null) { // 有数据
				lastColunm = Math.max(row.getLastCellNum(), lastColunm);
			}
		}

		setRow(lastRow - firstRow+1);
		setCount(lastColunm - firstColumn);
		// lastRow-firstRow:不读标题
		for (int j = 0; j < lastRow -firstRow+1; j++) {
			HSSFRow row = sheet.getRow(firstRow +j);
			
			Map<String,Object> map = new HashMap<String, Object>();
			if (row != null) { // 有数据
				/** 获取列参数 */
				int iFirstCellNum = row.getFirstCellNum(); // 开始列序号
				int iLastCellNum = row.getLastCellNum(); // 结束列序号  
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
	 * 增加方法，返回值为<页签名称,行<列，数据> >
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
				if (row != null) { // 有数据
					lastColunm = Math.max(row.getLastCellNum(), lastColunm);
				}
			}

			setRow(lastRow - firstRow+1);
			setCount(lastColunm - firstColumn);
			// lastRow-firstRow:不读标题
			for (int j = 0; j < lastRow -firstRow+1; j++) {
				HSSFRow row = sheet.getRow(firstRow +j);
				
				Map<String,Object> map = new HashMap<String, Object>();
				if (row != null) { // 有数据
					/** 获取列参数 */
					int iFirstCellNum = row.getFirstCellNum(); // 开始列序号
					int iLastCellNum = row.getLastCellNum(); // 结束列序号  
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
				throw new Exception("页签:"+sheetname+" 不在所需要的页签范围之内,选择模版错误！");
			return workBook.getSheet(sheetname);
		}
		if (sheetnum < 0 || sheetnum > getSheetNum())
			throw new Exception("页签不在所需要的页签范围之内");
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
		// throw new Exception("不支持的格式，数据页没有数据");
	}

	private HSSFRow getFieldNameRow(HSSFSheet sheet) throws Exception {

		if (getRecorderRows(sheet) >= 0)
			return sheet.getRow(getTitleRow());
		else
			throw new Exception("不支持的格式，数据页没有数据");
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
				System.out.println("布尔型数据未处理，没有读入");
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
	 * @return 返回 titleRow。
	 */
	public int getTitleRow() {
		return titleRow;
	}

	/**
	 * @param titleRow
	 *            要设置的 titleRow。
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
	 * @param row 要设置的 row
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
	 * @param count 要设置的 count
	 */
	public void setCount(int count) {
		this.count = count;
	}
}