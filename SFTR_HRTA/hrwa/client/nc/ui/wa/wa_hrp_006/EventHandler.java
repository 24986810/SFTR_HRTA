package nc.ui.wa.wa_hrp_006;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import nc.bs.framework.common.NCLocator;
import nc.itf.uap.IUAPQueryBS;
import nc.itf.wa.hrp.pub.IHRPWABtn;
import nc.jdbc.framework.processor.BeanListProcessor;
import nc.jdbc.framework.processor.VectorProcessor;
import nc.ui.hrp.pub.bill.HRPEventhandle;
import nc.ui.pub.ButtonObject;
import nc.ui.pub.ClientEnvironment;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UIDialog;
import nc.ui.trade.base.IBillOperate;
import nc.ui.trade.business.HYPubBO_Client;
import nc.ui.trade.controller.IControllerBase;
import nc.ui.trade.manage.BillManageUI;
import nc.ui.wa.wa_hrp_002.DeptMnyDLG;
import nc.vo.hrp.pf02.PerioddeptVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFDouble;
import nc.vo.wa.wa_hrp_002.PERIODVO;
import nc.vo.wa.wa_hrp_004.DeptMnyBVO;
import nc.vo.wa.wa_hrp_004.DeptMnyHVO;
import nc.vo.wa.wa_hrp_005.FenpeiItemHVO;
import nc.vo.wa.wa_hrp_006.DeptChargeHVO;
import nc.vo.wa.wa_hrpreport_tr_001.ReportBodyVO;

/**
 * @author szh
 *
 */
@SuppressWarnings("restriction")
public class EventHandler extends HRPEventhandle {
	
	public EventHandler(BillManageUI arg0, IControllerBase arg1) {
		super(arg0, arg1);
	}

	@Override
	public void onBoAdd(ButtonObject bo) throws Exception {
		super.onBoAdd(bo);
		((ClientUI)getBillUI()).getBillCardPanel().getHeadItem("pk_dept_out").setValue(null);
		
		onBoLineAdd();
	}
	
	
	@Override
	protected void onBoQuery() throws Exception {
		StringBuffer strWhere = new StringBuffer();

		if (askForQueryCondition(strWhere) == false)
			return;// 用户放弃了查询

		String wheresql = strWhere.toString();
		
		//新处理逻辑begin tianxfc
		//String sql = "SELECT h.pk_perioddept, h.pk_parent, vcode, vname FROM pf_perioddept h WHERE pk_corp in ('" + _getCorp().getPrimaryKey() + "','0001') AND nvl(dr,0)=0 AND pk_perioddept in(SELECT pk_deptdoc FROM bd_wa_userdept WHERE nvl(dr,0)=0 AND pk_user='" + _getOperator() + "') AND nvl(vdef5,'N')='Y' ORDER BY vcode";
		String where = " pk_corp in ('" + _getCorp().getPrimaryKey() + "','0001') AND nvl(dr,0)=0 AND pk_perioddept in(SELECT pk_deptdoc FROM bd_wa_userdept WHERE nvl(dr,0)=0 AND pk_user='" + _getOperator() + "') AND nvl(vdef5,'N')='Y' ORDER BY vcode";
		PerioddeptVO[] deptvos = (PerioddeptVO[]) HYPubBO_Client.queryByCondition(PerioddeptVO.class, where);
		
		StringBuffer bufStr = new StringBuffer();
		if(deptvos != null && deptvos.length > 0){
			int count = 0;
			for(PerioddeptVO  vo : deptvos){
				if(count == 0){
					bufStr.append("'" + vo.getPk_perioddept() + "'");
					count++;
				}else{
					bufStr.append(",'" + vo.getPk_perioddept() + "'");
				}
			}
			String wherePKPerioddept = bufStr.toString();
			if(wherePKPerioddept != null && wherePKPerioddept.trim().length() > 0){
				if(wheresql==null||wheresql.length()<=0){
					wheresql = " pk_dept_out in(" + wherePKPerioddept + ") ";
				}else{
					wheresql += " and pk_dept_out in(" + wherePKPerioddept + ") ";
				}
			}else{
				wheresql = " 1=2 ";
			}
		}else{
			wheresql = " 1=2 ";
		}
		//新处理逻辑end tianxfc
		
		//原处理逻辑begin
//		UserAndClerkVO[] vos = (UserAndClerkVO[])HYPubBO_Client.queryByCondition(UserAndClerkVO.class, " isnull(dr,0)=0 and userid='"+_getOperator()+"' ");
//		if(vos!=null&&vos.length>0){
//			PsndocVO[] pvos = (PsndocVO[])HYPubBO_Client.queryByCondition(PsndocVO.class, " isnull(dr,0)=0 and pk_corp='"+_getCorp().getPrimaryKey()+"' and pk_psnbasdoc ='"+vos[0].getPk_psndoc()+"' ");
//			if(pvos!=null&&pvos.length>0){
//				PerioddeptVO[] deptvos = (PerioddeptVO[])HYPubBO_Client.queryByCondition(PerioddeptVO.class, " pk_deptdoc='"+pvos[0].getPk_deptdoc()+"' and isnull(dr,0)=0 and pk_corp='"+_getCorp().getPrimaryKey()+"' ");
//				if(deptvos!=null&&deptvos.length>0){
//					if(wheresql==null||wheresql.length()<=0){
//						wheresql = " (pk_dept_out='"+deptvos[0].getPrimaryKey()+"' or pk_dept_in='"+deptvos[0].getPrimaryKey()+"' ) ";
//					}else{
//						wheresql += " and (pk_dept_out='"+deptvos[0].getPrimaryKey()+"' or pk_dept_in='"+deptvos[0].getPrimaryKey()+"') ";
//					}
//				}else{
//					wheresql = " 1=2 ";
//				}
//			}else{
//				wheresql = " 1=2 ";
//			}
//		}else{
//			wheresql = " 1=2 ";
//		}
		//原处理逻辑end
		
		SuperVO[] queryVos = queryHeadVOs(wheresql);
		getBufferData().clear();
		// 增加数据到Buffer
		addDataToBuffer(queryVos);
		updateBuffer();
		//((ClientUI)getBillUI()).getBillListWrapper().getBillListPanel().getHeadBillModel().execLoadFormula();
		//getBillListPanel().getBillListData().getHeadBillModel().execLoadFormula();
	}
	@Override
	protected void onBoSave() throws Exception {
		getBillCardPanelWrapper().getBillCardPanel().dataNotNullValidate();
		int xx = MessageDialog.showOkCancelDlg(this.getBillUI(), "提示", "转账后不可撤销，确认转账?");
		if(xx!=UIDialog.ID_OK) return;
		//String pk_dept_out = (String)getBillCardPanelWrapper().getBillCardPanel().getHeadItem("pk_dept_out").getValueObject();
		//DeptMnyHVO[] dhvo = (DeptMnyHVO[])HYPubBO_Client.queryByCondition(DeptMnyHVO.class, " isnull(dr,0)=0 and pk_dept='"+pk_dept_out+"'");
		//nmny = dhvo != null && dhvo.length > 0 ? dhvo[0].getNmny().sub(nmny):new UFDouble(0).sub(nmny);
		
		//转出科室月考核奖结余
		UFDouble vdef11 = getBillCardPanelWrapper().getBillCardPanel().getHeadItem("vdef11").getValueObject()!=null?new UFDouble(getBillCardPanelWrapper().getBillCardPanel().getHeadItem("vdef11").getValueObject().toString(), 2):new UFDouble(0, 2);
		//转出科室一次性奖结余
		UFDouble vdef12 = getBillCardPanelWrapper().getBillCardPanel().getHeadItem("vdef12").getValueObject()!=null?new UFDouble(getBillCardPanelWrapper().getBillCardPanel().getHeadItem("vdef12").getValueObject().toString(), 2):new UFDouble(0, 2);
		//转出科室月考核奖结余 + 转出科室一次性奖结余
		UFDouble totalMoney = new UFDouble(vdef11.add(vdef12).toString(), 2);
		//转账金额
		UFDouble nmny = getBillCardPanelWrapper().getBillCardPanel().getHeadItem("nmny").getValueObject()!=null?new UFDouble(getBillCardPanelWrapper().getBillCardPanel().getHeadItem("nmny").getValueObject().toString()):new UFDouble(0);
		if(nmny != null && nmny.doubleValue() <= 0){
			MessageDialog.showErrorDlg(this.getBillUI(), "提示","转账金额不能为负数！");
			return;
		}
		if(nmny.doubleValue() > totalMoney.doubleValue()){
			//int x = MessageDialog.showOkCancelDlg(this.getBillUI(), "提示", "转账后转出部门的余额小于0,是否继续");
			//if(x!=UIDialog.ID_OK) return;
			MessageDialog.showErrorDlg(this.getBillUI(), "转账失败","转账金额不能大于【转出科室月考核奖结余】+【转账科室一次性奖结余】 = " + totalMoney);
			return;
		}
		
		int rowcount = getBillCardPanelWrapper().getBillCardPanel().getBillTable().getRowCount();
		if(rowcount<=0){
			MessageDialog.showErrorDlg(this.getBillUI(), "提示","请添加转账项目！");
			return;
		}
		Map<String, UFDouble> map = new HashMap<String, UFDouble>();
		for(int i=0; i<rowcount; i++){
			String pk_class_item = getBillCardPanelWrapper().getBillCardPanel().getBodyValueAt(i,"pk_class_item").toString();
			
			Object obj = getBillCardPanelWrapper().getBillCardPanel().getBodyValueAt(i,"nmny");
			//金额
			UFDouble money = obj != null ? new UFDouble(obj.toString(),2):new UFDouble(0);
			if(money != null && money.doubleValue() <= 0){
				MessageDialog.showErrorDlg(this.getBillUI(), "提示","转账项目中金额不能为负数！");
				return;
			}
			if(map.containsKey(pk_class_item)){
				UFDouble temp = map.get(pk_class_item).add(money);
				map.put(pk_class_item, temp);
			}else{
				map.put(pk_class_item, money);
			}
		}
		Map<String, String> waClassItemVOMap = ((ClientUI)getBillUI()).getWaClassItemVOMap();
		if(map != null && !map.isEmpty()){
			for(String key : map.keySet()){
				if(waClassItemVOMap != null && !waClassItemVOMap.isEmpty()){
					//薪酬类别项名称
					String vname = waClassItemVOMap.get(key);
					//月考核奖
					if(vname != null && "月考核奖".equals(vname)){
						UFDouble totalYkh = map.get(key) != null ? map.get(key) : new UFDouble(0);
						if(totalYkh.doubleValue() > vdef11.doubleValue()){
							MessageDialog.showErrorDlg(this.getBillUI(), "提示","项目：月考核奖合计金额不能大于【转出科室月考核奖结余】 = " + vdef11);
							return;
						}
					}
					//一次性奖金
					if(vname != null && "一次性奖金".equals(vname)){
						UFDouble totalYcx = map.get(key) != null ? map.get(key) : new UFDouble(0);
						if(totalYcx.doubleValue() > vdef12.doubleValue()){
							MessageDialog.showErrorDlg(this.getBillUI(), "提示","项目：一次性奖金合计金额不能大于【转出科室一次性奖结余】 = " + vdef12);
							return;
						}
					}
				}
			}
		}
		
		//月考核奖
//		if(map.containsKey("10024Z1000000002OOJ7")){
//			UFDouble totalYkh = map.get("10024Z1000000002OOJ7") != null ? map.get("10024Z1000000002OOJ7") : new UFDouble(0);
//			if(totalYkh.doubleValue() > vdef11.doubleValue()){
//				MessageDialog.showErrorDlg(this.getBillUI(), "提示","项目：月考核奖合计金额不能大于【转出科室月考核奖结余】 = " + vdef11);
//				return;
//			}
//		}
//		//一次性奖金
//		if(map.containsKey("10024Z1000000002M6TL")){
//			UFDouble totalYcx = map.get("10024Z1000000002M6TL") != null ? map.get("10024Z1000000002M6TL") : new UFDouble(0);
//			if(totalYcx.doubleValue() > vdef12.doubleValue()){
//				MessageDialog.showErrorDlg(this.getBillUI(), "提示","项目：一次性奖金合计金额不能大于【转出科室一次性奖结余】 = " + vdef12);
//				return;
//			}
//		}
		
		//奖金期间
		PERIODVO waPeriodVO  =((ClientUI)getBillUI()).getWaPeriodVO();
		if(waPeriodVO != null){
			//为期间字段赋值
			getBillCardPanelWrapper().getBillCardPanel().getHeadItem("vyear").setValue(waPeriodVO.getCyear());
			getBillCardPanelWrapper().getBillCardPanel().getHeadItem("vperiod").setValue(waPeriodVO.getCperiod());
		}
		
		//getBillCardPanelWrapper().getBillCardPanel().getHeadItem("vbillstatus").setValue("1");
		//MessageDialog.showErrorDlg(this.getBillUI(), "提示","保存后请提交！");
		// 红冲标识
		getBillCardPanelWrapper().getBillCardPanel().getHeadItem("reddased").setValue("0");
		
	
		
		super.onBoSave();
		super.onBoAudit();
	}
	@Override
	public void onBoAudit() throws Exception {
		if(getBufferData()==null||getBufferData().getCurrentVO()==null) return;
		DeptChargeHVO hvo = (DeptChargeHVO)getBufferData().getCurrentVO().getParentVO();
		DeptMnyHVO[] dhvo = (DeptMnyHVO[])HYPubBO_Client.queryByCondition(DeptMnyHVO.class, " isnull(dr,0)=0 and pk_dept='"+hvo.getPk_dept_out()+"'");
		UFDouble nmny = dhvo!=null&&dhvo.length>0?dhvo[0].getNmny().sub(hvo.getNmny()):new UFDouble(0).sub(hvo.getNmny());
		if(nmny.doubleValue()<=0){
			//int x = MessageDialog.showOkCancelDlg(this.getBillUI(), "提示", "转账后转出部门的余额小于0,是否继续");
			//if(x!=UIDialog.ID_OK) return;
			MessageDialog.showHintDlg(this.getBillUI(), "转账失败","转账金额不能大于转出科室结余！");
			return;
		}
		
		
		
		super.onBoAudit();
	}
	@Override
	protected void onBoCancelAudit() throws Exception {
		//查询当前期科室有没有进行分配
		DeptChargeHVO hvo = (DeptChargeHVO)getBufferData().getCurrentVO().getParentVO();
		DeptMnyBVO[] deptmnybvos = (DeptMnyBVO[]) HYPubBO_Client.queryByCondition(DeptMnyBVO.class, 
				" isnull(dr,0)=0 and csourcebillbid='"+hvo.getPk_dept_charge()+"'");
		
		if(deptmnybvos != null){
			if(deptmnybvos.length >0){
				for(int i=0;i<deptmnybvos.length;i++){
					DeptMnyBVO deptmnybvo = deptmnybvos[i];
					
					String period = deptmnybvo.getVyear() + "-"+deptmnybvo.getVperiod();
					String pk_wa_period = getPeriod(period);
					String pk_deptdoc = deptmnybvo.getPk_dept();
					
					ArrayList<ReportBodyVO> list = getReportBodyVO(pk_wa_period,pk_deptdoc);
					if(list !=null){
						if(list.size() >0){
							String vapprovenote= list.get(0).getVapprovenote();
							if(vapprovenote != null){
								if(!"二级审核：不通过".equals(vapprovenote)){
									MessageDialog.showHintDlg(this.getBillUI(), "退回失败","转入或转出科室已经分配不能退回，请反审后！");
									return;
								}
							}
							
						}
					}
				}
			}
		}
		
		super.onBoCancelAudit();
	}
	
	public String getPeriod(String period) throws BusinessException{
		IUAPQueryBS service = (IUAPQueryBS)NCLocator.getInstance().lookup(IUAPQueryBS.class.getName());
		String curedate = ClientEnvironment.getInstance().getDate().toString().substring(0, 7);
		String strSQL = "select pk_wa_period from wa_period where cyear||'-'||cperiod='"+period+"' and dr=0";
		String pk_wa_period = "";
		
		Vector o1 = (Vector) service.executeQuery(strSQL,new VectorProcessor());
		if (o1.size() > 0 && o1 != null) {
			for (int i = 0; i < o1.size(); i++) {
				pk_wa_period = new String(((Vector) o1.elementAt(i)).elementAt(0) != null ? ((Vector) o1.elementAt(i)).elementAt(0).toString() : "");
			}
		}
		
		return pk_wa_period;
	}
	
	public ArrayList<ReportBodyVO> getReportBodyVO(String pk_wa_period,String pk_deptdoc) throws BusinessException{
		IUAPQueryBS bs= NCLocator.getInstance().lookup(IUAPQueryBS.class);
		String sql = " SELECT pk_wa_dept pk_deptdoc,nmny1,nmny2,vapprovenote "
			+"   FROM WA_FENPAI_DETAIL fb"
			+"   left join wa_psn_item_h ih"
			+"     on fb.pk_wa_dept = ih.pk_dept"
			+"    and fb.pk_wa_period = ih.pk_wa_period"
			+"    and ih.dr = 0"
			+"  where"
			+"  fb.pk_wa_period = '"+pk_wa_period+"' and fb.pk_wa_dept='"+pk_deptdoc+"'";
		ArrayList<ReportBodyVO> list = (ArrayList<ReportBodyVO>)bs.executeQuery(sql, new BeanListProcessor(ReportBodyVO.class));
		return list;
	}
	
	@Override
	protected void onBoElse(int btn) throws Exception {
		switch (btn) {
		case IHRPWABtn.QUERYDEPTMNY:
			onBoQueryDept();
			break;	
		default:
			break;
		}
	}
	private void onBoQueryDept() throws Exception{
		String pk_dept = null;
		String vyear = null;
		String vperiod = null;
		UFDouble totalmoney = null;
		boolean flag = false;
		if(((ClientUI)getBillUI()).isListPanelSelected()){
			int row = ((ClientUI)getBillUI()).getBillListPanel().getHeadTable().getSelectedRow();
			if(row>=0){
				DeptChargeHVO hvo = (DeptChargeHVO) ((ClientUI)getBillUI()).getBillListPanel().getHeadBillModel().getBodyValueRowVO(row, DeptChargeHVO.class.getName());
				pk_dept = hvo.getPk_dept_out();//(String)((ClientUI)getBillUI()).getBillListPanel().getHeadBillModel().getValueAt(row, "pk_dept_out");
				String date = hvo.getDapprovedate()!=null?hvo.getDapprovedate().toString():hvo.getDmakedate().toString();//((ClientUI)getBillUI()).getBillListPanel().getHeadBillModel().getValueAt(row, "dapprovedate")!=null?(String)((ClientUI)getBillUI()).getBillListPanel().getHeadBillModel().getValueAt(row, "dapprovedate"):(String)((ClientUI)getBillUI()).getBillListPanel().getHeadBillModel().getValueAt(row, "dmakedate");
				if(date==null||date.trim().length()<=0) date = _getDate().toString();
				vyear = date.substring(0,4);
				vperiod = date.substring(5,7);
				totalmoney = hvo.getNmny()!=null?hvo.getNmny():new UFDouble(0);//((ClientUI)getBillUI()).getBillListPanel().getHeadBillModel().getValueAt(row, "nmny")!=null?new UFDouble(((ClientUI)getBillUI()).getBillListPanel().getHeadBillModel().getValueAt(row, "nmny").toString()):new UFDouble(0);
				flag = true;
			}
		}else{
			pk_dept = (String)((ClientUI)getBillUI()).getBillCardPanel().getHeadItem("pk_dept_out").getValueObject();
			String date = ((ClientUI)getBillUI()).getBillCardPanel().getHeadItem("dapprovedate").getValueObject()!=null?(String)((ClientUI)getBillUI()).getBillCardPanel().getHeadItem("dapprovedate").getValueObject():(String)((ClientUI)getBillUI()).getBillCardPanel().getHeadItem("dmakedate").getValueObject();
			if(date==null||date.trim().length()<=0) date = _getDate().toString();
			vyear = date.substring(0,4);
			vperiod = date.substring(5,7);
			totalmoney = ((ClientUI)getBillUI()).getBillCardPanel().getHeadItem("nmny").getValueObject()!=null?new UFDouble(((ClientUI)getBillUI()).getBillCardPanel().getHeadItem("nmny").getValueObject().toString()):new UFDouble(0);
			if(getBillUI().getBillOperate()==IBillOperate.OP_ADD){
				flag = false;
			}else{
				flag = true;
			}
		}
		DeptMnyDLG dlg = new DeptMnyDLG(this.getBillUI(),pk_dept,vyear,vperiod,totalmoney,flag,true);
		dlg.showModal();
//		PfLinkData pflink = new PfLinkData();
//		Object pk_dept_out = getBillCardPanelWrapper().getBillCardPanel().getHeadItem("pk_dept_out").getValueObject();
//		Object pk_dept_in = getBillCardPanelWrapper().getBillCardPanel().getHeadItem("pk_dept_in").getValueObject();
//		if((pk_dept_out!=null&&pk_dept_out.toString().trim().length()>0)||(pk_dept_in!=null&&pk_dept_in.toString().trim().length()>0)){
//		String where = "('"+pk_dept_out+"','"+pk_dept_in+"')";
//		pflink.setBillID(where);
//		openNodeUI("HY02030120", pflink,  ILinkType.LINK_TYPE_MAINTAIN, IFuncWindow.WINDOW_TYPE_DLG,true);
//		}
	}
	
	@Override
	protected void onBoLineAdd() throws Exception {
		
		//int row = getBillCardPanelWrapper().getBillCardPanel().getBillTable().getSelectedRow();
		PERIODVO waPeriodVO  =((ClientUI)getBillUI()).getWaPeriodVO();
		// zhanghua 自动带出薪资类型
		String vyear = waPeriodVO.getCyear();
		String vperiod = waPeriodVO.getCperiod();
		String sqlwhere = " pk_corp = '1002' AND vyear = '"+vyear+"' AND vperiod = '"+vperiod+"' AND vbillstatus_audit = 2 AND pk_dept is null ";
		FenpeiItemHVO[] hvos = (FenpeiItemHVO[]) HYPubBO_Client.queryByCondition(FenpeiItemHVO.class, sqlwhere);
		if(hvos.length == 2){
			super.onBoLineAdd();
			super.onBoLineAdd();
			String[] pk_class_items ={"10028L1000000004KHJS","10028L1000000004KHJT"};
			String[] pk_item ={"10028L1000000002L8H8","10028L1000000002L8H9"};
			String[] pk_waclass ={"10028L1000000001V18U","10028L1000000001V18U"};
			for(int i=0;i<2;i++){
				getBillCardPanelWrapper().getBillCardPanel().getBillModel().setValueAt(_getCorp().getPrimaryKey(), i, "pk_corp");
				getBillCardPanelWrapper().getBillCardPanel().getBillModel().setValueAt(pk_class_items[i], i, "pk_class_item");
				getBillCardPanelWrapper().getBillCardPanel().getBillModel().setValueAt(pk_item[i], 0, "pk_item");
				getBillCardPanelWrapper().getBillCardPanel().getBillModel().setValueAt(pk_waclass[i], 0, "pk_waclass");
			}
			
		}else{
			super.onBoLineAdd();
			String itemname = hvos[0].getVdef18();
			if("月考核奖".equals(itemname)){
				String pk_class_item = "10028L1000000004KHJS";
				String pk_item = "10028L1000000002L8H8";
				String pk_waclass="10028L1000000001V18U";
				getBillCardPanelWrapper().getBillCardPanel().getBillModel().setValueAt(pk_class_item, 0, "pk_class_item");
				getBillCardPanelWrapper().getBillCardPanel().getBillModel().setValueAt(pk_item, 0, "pk_item");
				getBillCardPanelWrapper().getBillCardPanel().getBillModel().setValueAt(pk_waclass, 0, "pk_waclass");
			}else{
				String pk_class_item = "10028L1000000004KHJT";
				String pk_item = "10028L1000000002L8H9";
				String pk_waclass="10028L1000000001V18U";
				getBillCardPanelWrapper().getBillCardPanel().getBillModel().setValueAt(pk_class_item, 0, "pk_class_item");
				getBillCardPanelWrapper().getBillCardPanel().getBillModel().setValueAt(pk_item, 0, "pk_item");
				getBillCardPanelWrapper().getBillCardPanel().getBillModel().setValueAt(pk_waclass, 0, "pk_waclass");
			}
			getBillCardPanelWrapper().getBillCardPanel().getBillModel().setValueAt(_getCorp().getPrimaryKey(), 0, "pk_corp");
			
		}

		getBillCardPanelWrapper().getBillCardPanel().getBillModel().execLoadFormula();
	}
	@Override
	protected void onBoLineDel() throws Exception {
		super.onBoLineDel();
		UFDouble nmny = new UFDouble(0);
		int row = getBillCardPanelWrapper().getBillCardPanel().getBillModel().getRowCount();
		for(int i=0;i<row;i++){
			Object value = getBillCardPanelWrapper().getBillCardPanel().getBillModel().getValueAt(i, "nmny");
		    nmny = nmny.add(value!=null?new UFDouble(value.toString()):new UFDouble(0));
		}
		getBillCardPanelWrapper().getBillCardPanel().getHeadItem("nmny").setValue(nmny);
	}

	@Override
	protected void onBoCard() throws Exception {
		// TODO Auto-generated method stub
		super.onBoCard();
		getBillCardPanelWrapper().getBillCardPanel().execHeadLoadFormulas();
	}
	
	
}