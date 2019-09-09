/**
 * 
 */
package nc.ui.tam.tongren003;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import nc.bs.framework.common.NCLocator;
import nc.itf.hrp.pub.HRPPubTool;
import nc.itf.uap.IUAPQueryBS;
import nc.jdbc.framework.processor.BeanListProcessor;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.bill.BillItem;
import nc.ui.trade.bill.ICardController;
import nc.ui.trade.card.BillCardUI;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDouble;
import nc.vo.tam.tongren003.PaiPanReportVO;

/**
 * @author 28729
 *
 */
public class EventQueryHandler2 extends EventHandler {

	/**
	 * @param billUI
	 * @param control
	 */
	public EventQueryHandler2(BillCardUI billUI, ICardController control) {
		super(billUI, control);
		// TODO Auto-generated constructor stub
	}
	@Override
	protected void onBoQuery() throws Exception {
		// TODO Auto-generated method stub
		((ClientQueryUI2)getBillUI()).onshowData();
	}
	@Override
	protected void onBoPrint() throws Exception {
		// TODO Auto-generated method stub
		
		
		BillItem[] items = getBillCardPanelWrapper().getBillCardPanel().getBodyShowItems();
		int rowcount = getBillCardPanelWrapper().getBillCardPanel().getBillTable().getRowCount();
		Object value[][] = new Object[rowcount+1][items.length]; 
		for(int i=0;i<items.length;i++){
			value[0][i] = items[i].getName();
			for(int j=0;j<rowcount;j++){
				if(items[i].getDataType()==4){
					Object tmpvalue = getBillCardPanelWrapper().getBillCardPanel().getBodyValueAt(j,items[i].getKey());
					value[j+1][i] = tmpvalue!=null&&new UFBoolean(tmpvalue.toString())!=null&&new UFBoolean(tmpvalue.toString()).booleanValue()?"是":"否";
				}else{
					value[j+1][i] = getBillCardPanelWrapper().getBillCardPanel().getBodyValueAt(j,items[i].getKey());
				}
			}
		}
		ExcelOut excel = new ExcelOut(this.getBillUI(),_getDate().toString().substring(0,4),_getDate().toString().substring(5,7));
		boolean flag = excel.createMergExcelFile(value);//zhanghua
		if(flag){
			MessageDialog.showHintDlg(this.getBillUI(), "提示", "导出完成");
		}
	}
}
