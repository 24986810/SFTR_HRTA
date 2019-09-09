/**
 * 
 */
package nc.ui.tam.tongren010;

import java.util.ArrayList;
import java.util.HashMap;

import nc.ui.hrp.pub.excel.ImportExcelData;
import nc.ui.pub.ButtonObject;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.bill.BillItem;
import nc.ui.trade.business.HYPubBO_Client;
import nc.ui.trade.button.IBillButton;
import nc.ui.trade.controller.IControllerBase;
import nc.ui.trade.manage.BillManageUI;
import nc.ui.trade.manage.ManageEventHandler;
import nc.vo.bd.b06.PsndocVO;
import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.pub.SuperVO;
import nc.vo.tam.tongren010.GxBVO;
import nc.vo.tam.tongren010.GxHVO;
import nc.vo.trade.pub.HYBillVO;

/**
 * @author 28729
 *
 */
public class EventHandler extends ManageEventHandler {

	/**
	 * @param billUI
	 * @param control
	 */
	public EventHandler(BillManageUI billUI, IControllerBase control) {
		super(billUI, control);
		// TODO Auto-generated constructor stub
	}
	@Override
	protected void onBoCancel() throws Exception {
		// TODO Auto-generated method stub
		super.onBoCancel();
		getButtonManager().getButton(IBillButton.ImportBill).setEnabled(false);
		getBillUI().updateButtons();
	}
	
	@Override
	protected void onBoCard() throws Exception {
		// TODO Auto-generated method stub
		super.onBoCard();
		
	}
	@Override
	protected void onBoEdit() throws Exception {
		// TODO Auto-generated method stub
		super.onBoEdit();
		getButtonManager().getButton(IBillButton.ImportBill).setEnabled(true);
		getBillUI().updateButtons();
	}
	@Override
	public void onBoAdd(ButtonObject bo) throws Exception {
		// TODO Auto-generated method stub
		GxHVO[] hvos = (GxHVO[])HYPubBO_Client.queryByCondition(GxHVO.class, 
				" isnull(dr,0)=0 and pk_corp='"+_getCorp().getPrimaryKey()+"' and vyear='"+_getDate().toString().substring(0,4)+"'");
		if(hvos!=null&&hvos.length>0){
			MessageDialog.showHintDlg(this.getBillUI(), "提示", "已维护本年度公休数");
			return;
		}
		super.onBoAdd(bo);
		getButtonManager().getButton(IBillButton.ImportBill).setEnabled(true);
		getBillUI().updateButtons();
	}
	@Override
	protected void onBoSave() throws Exception {
		// TODO Auto-generated method stub
		getBillCardPanelWrapper().getBillCardPanel().dataNotNullValidate();
		int rowcount = getBillCardPanelWrapper().getBillCardPanel().getBillModel().getRowCount();
		ArrayList<String> list = new ArrayList<String>();
		for(int i=0;i<rowcount;i++){
			String pk_psndoc = (String)getBillCardPanelWrapper().getBillCardPanel().getBillModel().getValueAt(i, "pk_psndoc");
			if(list.contains(pk_psndoc)){
				MessageDialog.showHintDlg(this.getBillUI(),  "提示", "第"+(i+1)+"行人员重复");
				return;
			}else{
				list.add(pk_psndoc);
			}
		}
		super.onBoSave();
		getButtonManager().getButton(IBillButton.ImportBill).setEnabled(false);
		getBillUI().updateButtons();
	}
	@Override
	protected void onBoImport() throws Exception {
		// TODO Auto-generated method stub
		PsndocVO[] psnvo = (PsndocVO[])HYPubBO_Client.queryByCondition(PsndocVO.class, " isnull(dr,0)=0 and pk_corp='"+_getCorp().getPrimaryKey()+"' and sealdate is null ");
		HashMap<String,PsndocVO> map_psn = new HashMap<String, PsndocVO>();
		if(psnvo!=null&&psnvo.length>0){
			for(int uu=0;uu<psnvo.length;uu++){
				PsndocVO vo=psnvo[uu];
				if(vo.getPsncode()!=null&&vo.getPsncode().trim().length()>0){
					map_psn.put(vo.getPsncode().trim().toLowerCase(),vo);
				}
			}
		}
		HashMap<String,BillItem> map = new HashMap<String, BillItem>();
		BillItem[] items = getBillCardPanelWrapper().getBillCardPanel().getBodyShowItems();
		for(int i=0;i<items.length;i++){
			map.put(items[i].getName().trim(), items[i]);
		}
		ImportExcelData excel = new ImportExcelData();
		try {
			Object[][] values = excel.executeImport();

			if(values==null||values.length<=1) return;
			int indexpsncode = 1;
			HashMap<String,Integer> mapindex = new HashMap<String, Integer>();
			for(int i=0;i<values[0].length;i++){
				String value = values[0][i]!=null&&values[0][i].toString().trim().length()>0?values[0][i].toString().trim():"";
				if(map.containsKey(value)){
					mapindex.put(value, i);
				}
			}
			
			// 查询现有人员
			int x = getBillCardPanelWrapper().getBillCardPanel().getBillTable().getRowCount()-1;
			HashMap<String, Integer> rowMap = new HashMap<String, Integer>();
			for (int i = 0; i <= x; i++) {
				 rowMap.put(getBillCardPanelWrapper().getBillCardPanel().getBillModel().getValueAt(i, "pk_psndoc").toString(), i);
			}
			
			int row = 0;
			for(int i=1;i<values.length;i++){
				Object tmppsn = values[i][indexpsncode];//人员编码
				if(tmppsn==null||tmppsn.toString().trim().equals("")){
					break;
				}
				String pk_psndoc = map_psn.get(tmppsn.toString().trim().toLowerCase())!=null?map_psn.get(tmppsn.toString().trim().toLowerCase()).getPk_psndoc():null;
				if (rowMap.get(pk_psndoc) == null) {
					// 新增人员
					x++;
					row = x;
					onBoLineAdd();
					getBillCardPanelWrapper().getBillCardPanel().getBillModel().setValueAt(pk_psndoc,row,"pk_psndoc");
				} else {
					// 原有人员
					row = rowMap.get(pk_psndoc);
				}
				
				if(mapindex!=null&&mapindex.size()>0){
					String[] keys = mapindex.keySet().toArray(new String[0]);
					for(int j=0;j<keys.length;j++){
						BillItem item = map.get(keys[j]);
						if(item==null){
							continue;
						}
						int index = mapindex.get(keys[j]);
						Object tmpnmny = values[i][index];
						try{
							getBillCardPanelWrapper().getBillCardPanel().getBillModel().setValueAt(tmpnmny,row,item.getKey());
						}catch(Exception e){
							//
						}
					}
				}
				getBillCardPanelWrapper().getBillCardPanel().getBillModel().execEditFormulas(x);
			}
		
		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			MessageDialog.showHintDlg(getBillUI(), "提示", e.getMessage());
		}
	}
	@Override
	protected void onBoLineAdd() throws Exception {
		// TODO Auto-generated method stub
		super.onBoLineAdd();
		int row = getBillCardPanelWrapper().getBillCardPanel().getBillTable().getSelectedRow();
		getBillCardPanelWrapper().getBillCardPanel().getBillModel().setValueAt(_getDate().getYear()+"",row, "vyear");
		getBillCardPanelWrapper().getBillCardPanel().getBillModel().setValueAt(_getCorp().getPrimaryKey(),row, "pk_corp");
	}
	@Override
	protected void onBoLineIns() throws Exception {
		// TODO Auto-generated method stub
		super.onBoLineIns();
		int row = getBillCardPanelWrapper().getBillCardPanel().getBillTable().getSelectedRow();
		getBillCardPanelWrapper().getBillCardPanel().getBillModel().setValueAt(_getDate().getYear()+"",row, "vyear");
		getBillCardPanelWrapper().getBillCardPanel().getBillModel().setValueAt(_getCorp().getPrimaryKey(),row, "pk_corp");
	}
	@Override
	protected void onBoElse(int intBtn) throws Exception {
		switch (intBtn) {
		case 1111:			// 筛选应维护数据
			onQueryManual();
			break;
		default:
			super.onBoElse(intBtn);
		}
	}
	
	private void onQueryManual() throws Exception {
		HYBillVO billVO = (HYBillVO)getBillCardPanelWrapper().getBillVOFromUI();
		String vyear = (String)billVO.getParentVO().getAttributeValue("vyear");
		SuperVO[] superVOs = HYPubBO_Client.queryByCondition(GxBVO.class, " nvl(dr,0)=0 and nyfgx is null and vyear='"+vyear+"'");
		getBillCardPanelWrapper().getBillCardPanel().getBillModel().setBodyDataVO(superVOs);
		getBillCardPanelWrapper().getBillCardPanel().getBillModel().execLoadFormula();
		super.onBoEdit();
		getBillUI().updateButtons();
	}
	@Override
	protected void doBodyQuery(String arg0) throws Exception, ClassNotFoundException, InstantiationException, IllegalAccessException {
		// TODO Auto-generated method stub
		super.doBodyQuery(arg0);
	}
	@Override
	protected void onBoQuery() throws Exception {
		// TODO Auto-generated method stub
		super.onBoQuery();
	}
	@Override
	protected void onBoBodyQuery() throws Exception {
		// TODO Auto-generated method stub
		super.onBoBodyQuery();
	}

	
}
