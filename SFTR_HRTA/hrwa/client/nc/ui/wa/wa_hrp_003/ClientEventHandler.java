package nc.ui.wa.wa_hrp_003;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import nc.bs.framework.common.NCLocator;
import nc.itf.hr.comp.IHrPara;
import nc.itf.hrp.pub.IHRPBtn;
import nc.itf.hrp.pub.IhrpNCModule;
import nc.itf.uap.IUAPQueryBS;
import nc.itf.wa.hrp.pub.IHRPWABtn;
import nc.itf.wa.wa_hrppub.WaHrpBillStatus;
import nc.jdbc.framework.processor.MapProcessor;
import nc.ui.hrp.pub.bill.HRPEventhandle;
import nc.ui.pub.ClientEnvironment;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UIDialog;
import nc.ui.pub.filesystem.FileManageUI;
import nc.ui.trade.business.HYPubBO_Client;
import nc.ui.trade.controller.IControllerBase;
import nc.ui.trade.manage.BillManageUI;
import nc.ui.wa.wa_hrp_002.DeptMnyDLG;
import nc.ui.wa.wa_hrp_007.AuditBillDlg;
import nc.ui.wa.wa_hrp_pub.WaPubCount;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDouble;
import nc.vo.wa.wa_hrp_002.MyHYBillVO;
import nc.vo.wa.wa_hrp_002.PsnClassItemBVO;
import nc.vo.wa.wa_hrp_002.PsnClassItemHVO;

/**
 * @author 宋旨昊
 * 2011-3-21上午10:34:30
 * 说明：
 */
@SuppressWarnings("restriction")
public class ClientEventHandler extends HRPEventhandle {

	/**
	 * @param billUI
	 * @param control
	 */
	public ClientEventHandler(BillManageUI billUI, IControllerBase control) {
		super(billUI, control);
	}
	@Override
	protected void onBoElse(int btn) throws Exception {
		switch (btn) {
		case IHRPBtn.ExcelOut:
			onBoExcelOut();
			break;
		case IHRPBtn.ExcelImport:
			onBoExcelImport();
			break;
		case IHRPBtn.COMMITREURN:
			onCommitReturn();
			break;
		case IHRPBtn.FileManage:
			onBoFileManage();
			break;
//		case IHRPWABtn.PAYMNY:
//			onBoPayMny();
//			break;
//		case IHRPWABtn.CANCELPAYMNY:
//			onBoCancelPayMny();
//			break;
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
				pk_dept = (String)((ClientUI)getBillUI()).getBillListPanel().getHeadBillModel().getValueAt(row, "pk_dept");
				vyear = (String)((ClientUI)getBillUI()).getBillListPanel().getHeadBillModel().getValueAt(row, "vyear");
				vperiod = (String)((ClientUI)getBillUI()).getBillListPanel().getHeadBillModel().getValueAt(row, "vperiod");
				totalmoney = ((ClientUI)getBillUI()).getBillListPanel().getHeadBillModel().getValueAt(row, "totalmoney")!=null?new UFDouble(((ClientUI)getBillUI()).getBillListPanel().getHeadBillModel().getValueAt(row, "totalmoney").toString()):new UFDouble(0);
				flag = ((ClientUI)getBillUI()).getBillListPanel().getHeadBillModel().getValueAt(row, "vbillstatus_audit")!=null&&Integer.parseInt(((ClientUI)getBillUI()).getBillListPanel().getHeadBillModel().getValueAt(row, "vbillstatus_audit").toString())==WaHrpBillStatus.PASS;
			}
		}else{
			pk_dept = (String)((ClientUI)getBillUI()).getBillCardPanel().getHeadItem("pk_dept").getValueObject();
			vyear = (String)((ClientUI)getBillUI()).getBillCardPanel().getHeadItem("vyear").getValueObject();
			vperiod = (String)((ClientUI)getBillUI()).getBillCardPanel().getHeadItem("vperiod").getValueObject();
			totalmoney = ((ClientUI)getBillUI()).getBillCardPanel().getHeadItem("totalmoney").getValueObject()!=null?new UFDouble(((ClientUI)getBillUI()).getBillCardPanel().getHeadItem("totalmoney").getValueObject().toString()):new UFDouble(0);
			flag = ((ClientUI)getBillUI()).getBillCardPanel().getHeadItem("vbillstatus_audit").getValueObject()!=null&&Integer.parseInt(((ClientUI)getBillUI()).getBillCardPanel().getHeadItem("vbillstatus_audit").getValueObject().toString())==WaHrpBillStatus.PASS;
		}
		DeptMnyDLG dlg = new DeptMnyDLG(this.getBillUI(),pk_dept,vyear,vperiod,totalmoney,flag,true);
		dlg.showModal();
	}
	private void onBoPayMny() throws Exception{
		if(getBufferData()==null||getBufferData().getCurrentVO()==null) return;

		boolean islist = ((ClientUI)getBillUI()).isListPanelSelected();
		PsnClassItemHVO[] oldvos = (PsnClassItemHVO[])HYPubBO_Client.queryByCondition(PsnClassItemHVO.class, " isnull(dr,0)=0 and pk_billtype='63RP' and pk_corp='"+_getCorp().getPrimaryKey()+"' and vbatchcode_approve is not null order by vbatchcode_approve desc ");
		int batchcode = 1;
		if(oldvos!=null&&oldvos.length>0){
			batchcode = oldvos[0].getVbatchcode_approve()!=null?oldvos[0].getVbatchcode_approve()+1:1;
		}
		if(islist){
			int rowcount = ((ClientUI)getBillUI()).getBillListPanel().getHeadTable().getRowCount();
			String msg = "";
			String okmsg = "";
			for(int i=0;i<rowcount;i++){
				try{
					MyHYBillVO value = (MyHYBillVO)getBufferData().getVOByRowNo(i);
					PsnClassItemHVO hvo = (PsnClassItemHVO)value.getParentVO();
					boolean select = ((ClientUI)getBillUI()).getBillListPanel().getHeadBillModel().getValueAt(i, "bisselect")!=null?
							new UFBoolean(((ClientUI)getBillUI()).getBillListPanel().getHeadBillModel().getValueAt(i, "bisselect").toString()).booleanValue():false;
							if(select&&hvo.getVbillstatus_audit()==WaHrpBillStatus.PASS&&(hvo.getBispay()==null||!hvo.getBispay().booleanValue())){
								hvo.setBispay(new UFBoolean(true));
								hvo.setDpaydate(_getDate());
								if(_getDate().compareTo(hvo.getDapprovedate())<0){
									msg+="单据"+hvo.getVbillno()+"支付失败:支付日期不能早于复核日期\n";
									continue;
								}
								SuperVO[] bvos = HYPubBO_Client.queryByCondition(PsnClassItemBVO.class, " isnull(dr,0)=0 and isnull(biscount,'N')='N' and pk_psn_item_h='"+hvo.getPrimaryKey()+"' ");
								if(bvos!=null&&bvos.length>0){
									msg+="单据"+hvo.getVbillno()+"支付失败:有未复核计税的员工，请检查\n";
									continue;
								}
								hvo.setVpaypsnid(_getOperator());
								hvo.setVbatchcode_approve(batchcode);
								HYPubBO_Client.update(hvo);
								PsnClassItemHVO newhvo = (PsnClassItemHVO)HYPubBO_Client.queryByPrimaryKey(PsnClassItemHVO.class, hvo.getPrimaryKey());
								value.setParentVO(newhvo);
								getBufferData().setVOAt(i, value);
//								list.add(hvo);
								okmsg+="单据"+hvo.getVbillno()+"支付成功\n";
							}else{
								if(select){
									msg+="单据"+hvo.getVbillno()+"已支付或未复核\n";
								}
							}
				}catch(Exception e){
					MyHYBillVO value = (MyHYBillVO)getBufferData().getVOByRowNo(i);
					PsnClassItemHVO hvo = (PsnClassItemHVO)value.getParentVO();
					msg+="单据"+hvo.getVbillno()+"支付失败:"+e.getMessage()+"\n";
				}
			}
			if((okmsg+msg).trim().length()<=0){
				MessageDialog.showHintDlg(this.getBillUI(), "提示","没有需要支付的单据");
			}else{
				MessageDialog.showHintDlg(this.getBillUI(), "提示",okmsg+msg);
			}
			updateBuffer();
		}else{
			PsnClassItemHVO hvo = (PsnClassItemHVO)getBufferData().getCurrentVO().getParentVO();
			SuperVO[] bvos = HYPubBO_Client.queryByCondition(PsnClassItemBVO.class, " isnull(dr,0)=0 and isnull(biscount,'N')='N' and pk_psn_item_h='"+hvo.getPrimaryKey()+"' ");
			if(bvos!=null&&bvos.length>0){
				MessageDialog.showHintDlg(this.getBillUI(), "提示","单据"+hvo.getVbillno()+"支付失败:有未复核计税的员工，请检查");
				return;
			}
			if(hvo.getVbillstatus_audit()==WaHrpBillStatus.PASS){
				hvo.setBispay(new UFBoolean(true));
				hvo.setDpaydate(_getDate());
				hvo.setVpaypsnid(_getOperator());
				hvo.setVbatchcode_approve(batchcode);
				HYPubBO_Client.update(hvo);
			}
			onBoRefresh();
			getBillUI().showHintMessage("支付完成");
		}

	}
	private void onBoCancelPayMny() throws Exception{
		if(getBufferData()==null||getBufferData().getCurrentVO()==null) return;
		PsnClassItemHVO hvo = (PsnClassItemHVO)getBufferData().getCurrentVO().getParentVO();
		hvo.setBispay(new UFBoolean(false));
		hvo.setDpaydate(null);
		hvo.setVpaypsnid(null);
		hvo.setVbatchcode_approve(null);
		HYPubBO_Client.update(hvo);
		onBoRefresh();
	}
	/**
	 * 文件管理
	 * @author 宋旨昊
	 * 2011-3-29上午09:20:30
	 */
	protected void onBoFileManage(){
		FileManageUI.showInDlg(getBillUI(), "模板下载","部门维护薪酬项目");
	}


	/**
	 * 表头部门对应的薪资类别若已经审核，则不允许取消复核操作
	 * @return
	 * @throws BusinessException
	 */
	private boolean checkIflag() throws BusinessException {
		String pk_wa_class = getBillCardPanelWrapper().getBillCardPanel().getHeadItem("pk_wa_class").getValueObject()!=null?getBillCardPanelWrapper().getBillCardPanel().getHeadItem("pk_wa_class").getValueObject().toString():"";
		String cyear = ClientEnvironment.getInstance().getAccountYear();
		String cperiod = ClientEnvironment.getInstance().getAccountMonth();

		String sql="select count(wa_data.icheckflag) count from wa_data " +
		" inner join(wa_psn left outer join wa_bank on wa_psn.bankid = wa_bank.pk_wa_bank) on wa_data.psnid = wa_psn.psnid " +
		" inner join bd_psndoc on wa_data.psnid = bd_psndoc.pk_psndoc" +
		" inner join bd_psncl on wa_data.psnclid = bd_psncl.pk_psncl" +
		" inner join bd_deptdoc on wa_data.deptid = bd_deptdoc.pk_deptdoc" +
		" inner join bd_psnbasdoc on bd_psnbasdoc.pk_psnbasdoc = bd_psndoc.pk_psnbasdoc" +
		" left outer join om_job on wa_data.pk_om_job = om_job.pk_om_job" +
		" where wa_data.classid = '"+pk_wa_class+"'" +
		" and wa_data.cyear = '"+cyear+"'" +
		" and wa_data.cperiod = '"+cperiod+"' " +
		" and wa_psn.classid = wa_data.classid and wa_psn.cyear = wa_data.cyear and wa_psn.cperiod = wa_data.cperiod " +
		" and wa_psn.dr = 0 and wa_data.dr = 0 and wa_data.istopflag = 0 and wa_psn.dr = 0 and bd_psndoc.dr = 0 and bd_psncl.dr = 0 and bd_deptdoc.dr = 0 and bd_psnbasdoc.dr = 0 and wa_data.icheckflag=1";

		IUAPQueryBS service = (IUAPQueryBS)NCLocator.getInstance().lookup(IUAPQueryBS.class.getName());
		Map map=(Map)service.executeQuery(sql, new MapProcessor());

		if(map==null && map.size()==0){
			return true;
		}else{
			int checkIflah = map.get("count")!=null?new Integer(map.get("count").toString()):0;
			if(checkIflah>0){
				return false;
			}
			return true;
		}

	}
	@Override
	protected void onBoPrint() throws Exception {
		nc.ui.pub.print.IDataSource dataSource = new MyCardPanelPRTS(getBillUI()
				._getModuleCode(), getBillCardPanelWrapper().getBillCardPanel());
		nc.ui.pub.print.PrintEntry print = new nc.ui.pub.print.PrintEntry(null,
				dataSource);
		print.setTemplateID(getBillUI()._getCorp().getPrimaryKey(), getBillUI()
				._getModuleCode(), getBillUI()._getOperator(), getBillUI()
				.getBusinessType(), getBillUI().getNodeKey());
		if (print.selectTemplate() == 1)
			print.preview();
	}
	@Override
	protected void onBoSave() throws Exception {
		getBillCardPanelWrapper().getBillCardPanel().dataNotNullValidate();
		int rowcount = getBillCardPanelWrapper().getBillCardPanel().getBillTable().getRowCount();
		ArrayList<String> list = new ArrayList<String>();
		for(int i=0;i<rowcount;i++){
			Object psndoc = getBillCardPanelWrapper().getBillCardPanel().getBodyValueAt(i,"pk_psndoc");
			Object pk_wa_dept = getBillCardPanelWrapper().getBillCardPanel().getBodyValueAt(i,"pk_wa_dept");
			if(psndoc!=null&&psndoc.toString().trim().length()>0&&pk_wa_dept!=null&&pk_wa_dept.toString().trim().length()>0){
				if(list.contains(psndoc.toString().trim()+pk_wa_dept.toString().trim())){
					MessageDialog.showErrorDlg(this.getBillUI(), "保存提示", "表体支付部门人员重复，请检查");
					return;
				}else{
					list.add(psndoc.toString().trim()+pk_wa_dept.toString().trim());
				}
			}
		}
		super.onBoSave();
		onBoRefresh();
	}
	/**
	 * 驳回操作
	 */
	private void onCommitReturn() throws Exception{
		String operatorid = ClientEnvironment.getInstance().getUser().getPrimaryKey();
		UFDate date = ClientEnvironment.getInstance().getDate();
		if(getBufferData()==null||getBufferData().getCurrentVO()==null) return;
		AuditBillDlg dlg = new AuditBillDlg(this.getBillUI());
		int x=dlg.showModal();
		if(x!=UIDialog.ID_OK){
			return;
		}
		int ok =  MessageDialog.showOkCancelDlg(this.getBillUI(), "驳回", "确认驳回选择的单据?");
		if(ok!=UIDialog.ID_OK) return;

		int pk_item12 = WaHrpBillStatus.NOPASS_RETURN;
		PsnClassItemHVO hvo = (PsnClassItemHVO)getBufferData().getCurrentVO().getParentVO();
		
		if(hvo.getVbillstatus_audit() != null && 
				((hvo.getPk_item14() == 2 || hvo.getPk_item14() == 3 || hvo.getPk_item14() == 4) || (hvo.getVbillstatus_audit() == 2 || hvo.getVbillstatus_audit() == 3 || hvo.getVbillstatus_audit() == 4))){
			MessageDialog.showErrorDlg(this.getBillUI(), "驳回失败","一级半或二级已审核，您不能进行驳回操作！");
			return;
		}
		
		hvo.setVapproveid(operatorid);
		hvo.setDapprovedate(date);
		String text = dlg.getField_auditnode().getFieldText();
		if(text == null || text.trim().length() <= 0){
			text = "驳回";
		}
		hvo.setVapprovenote("奖金一级审核：" + text);
		//hvo.setVapprovenote(dlg.getField_auditnode().getFieldText());
		hvo.setPk_item12(pk_item12); //一级驳回态
		HYPubBO_Client.update(hvo);
		MessageDialog.showHintDlg(this.getBillUI(), "提示","驳回完成");
		onBoRefresh();
	}
	@Override
	protected void onBoCancelAudit() throws Exception {
		IHrPara hrpara = NCLocator.getInstance().lookup(IHrPara.class);
		int pvalue = hrpara.getParaIntValue(_getCorp().getPrimaryKey(),"BMXCFH",null, null).intValue();
		int ok =  MessageDialog.showOkCancelDlg(this.getBillUI(), "提示", "确认取消审核选择的单据?");
		if(ok!=UIDialog.ID_OK) return;

		//int status_audit = WaHrpBillStatus.NOPASS;
		//一级审核状态
		int pk_item12 = WaHrpBillStatus.NOPASS;
		boolean islist = ((ClientUI)getBillUI()).isListPanelSelected();
		String msg = "";
		if(islist){
			int rowcount = ((ClientUI)getBillUI()).getBillListPanel().getHeadTable().getRowCount();
			HashMap<Integer,Integer> maporder = new HashMap<Integer, Integer>();
			for(int i=0;i<rowcount;i++){
				MyHYBillVO value = (MyHYBillVO)getBufferData().getVOByRowNo(i);
				PsnClassItemHVO hvo = (PsnClassItemHVO)value.getParentVO();
				boolean select = ((ClientUI)getBillUI()).getBillListPanel().getHeadBillModel().getValueAt(i, "bisselect")!=null?
						new UFBoolean(((ClientUI)getBillUI()).getBillListPanel().getHeadBillModel().getValueAt(i, "bisselect").toString()).booleanValue():false;
//						if(select){
//							if(hvo.getIauditorder()!=null&&hvo.getVyear().equals(((ClientUI)getBillUI()).getWaPeriodVO().getCyear())&&hvo.getVperiod().equals(((ClientUI)getBillUI()).getWaPeriodVO().getVcalmonth())){
//								maporder.put(hvo.getIauditorder(), i);
//							}
							
//							String rmsg = WaPubCount.onAuditAndSj( hvo,null,pvalue,status_audit,"不通过",((ClientUI)getBillUI()).getWaPeriodVO(),false);
//							msg+=rmsg;
//							PsnClassItemHVO newhvo = (PsnClassItemHVO)HYPubBO_Client.queryByPrimaryKey(PsnClassItemHVO.class, hvo.getPrimaryKey());
//							value.setParentVO(newhvo);
//							PsnClassItemBVO[] newbvos = (PsnClassItemBVO[])HYPubBO_Client.queryByCondition(PsnClassItemBVO.class, " isnull(dr,0)=0 and pk_psn_item_h='"+hvo.getPrimaryKey()+"' ");
//							value.setChildrenVO(newbvos);
//							getBufferData().setVOAt(i, value);
//						}
						
				if(select){
					String rmsg = WaPubCount.onAuditYJSH(hvo, null, pvalue, pk_item12, "不通过", ((ClientUI)getBillUI()).getWaPeriodVO(), false);
					msg += rmsg;
					PsnClassItemHVO newhvo = (PsnClassItemHVO)HYPubBO_Client.queryByPrimaryKey(PsnClassItemHVO.class, hvo.getPrimaryKey());
					value.setParentVO(newhvo);
					PsnClassItemBVO[] newbvos = (PsnClassItemBVO[])HYPubBO_Client.queryByCondition(PsnClassItemBVO.class, " isnull(dr,0)=0 and pk_psn_item_h='"+hvo.getPrimaryKey()+"' ");
					value.setChildrenVO(newbvos);
					getBufferData().setVOAt(i, value);
				}
			}
			
//			if(maporder!=null&&maporder.size()>0){
//				Integer[] keys = maporder.keySet().toArray(new Integer[0]);
//				Arrays.sort(keys);
//				for(int i=keys.length-1;i>=0;i--){
//					int row = maporder.get(keys[i]);
//					MyHYBillVO value = (MyHYBillVO)getBufferData().getVOByRowNo(row);
//					PsnClassItemHVO hvo = (PsnClassItemHVO)value.getParentVO();
//					//String rmsg = WaPubCount.onAuditAndSj( hvo,null,pvalue,status_audit,"不通过",((ClientUI)getBillUI()).getWaPeriodVO(),false);
//					String rmsg = WaPubCount.onAuditYJSH(hvo, null, pvalue, pk_item12, "不通过", ((ClientUI)getBillUI()).getWaPeriodVO(), false);
//					msg+=rmsg;
//					PsnClassItemHVO newhvo = (PsnClassItemHVO)HYPubBO_Client.queryByPrimaryKey(PsnClassItemHVO.class, hvo.getPrimaryKey());
//					value.setParentVO(newhvo);
//					PsnClassItemBVO[] newbvos = (PsnClassItemBVO[])HYPubBO_Client.queryByCondition(PsnClassItemBVO.class, " isnull(dr,0)=0 and pk_psn_item_h='"+hvo.getPrimaryKey()+"' ");
//					value.setChildrenVO(newbvos);
//					getBufferData().setVOAt(row, value);
//				}
//			}

			updateBuffer();
			if((msg).trim().length()<=0){
				MessageDialog.showErrorDlg(this.getBillUI(), "提示","没有需要取消审核的单据");
			}else{
				MessageDialog.showHintDlg(this.getBillUI(), "提示",msg);
			}
		}else{
			if(getBufferData()==null||getBufferData().getCurrentVO()==null) return;
			PsnClassItemHVO hvo = (PsnClassItemHVO)getBufferData().getCurrentVO().getParentVO();
			int rowcount = getBillCardPanelWrapper().getBillCardPanel().getBillTable("wa_psn_item_b").getRowCount();
			ArrayList<PsnClassItemBVO> list = new ArrayList<PsnClassItemBVO>();
			for(int i=0;i<rowcount;i++){
				PsnClassItemBVO bvo = (PsnClassItemBVO)getBillCardPanelWrapper().getBillCardPanel().getBillModel("wa_psn_item_b").getBodyValueRowVO(i,PsnClassItemBVO.class.getName());
				list.add(bvo);
			}
			//String rmsg = WaPubCount.onAuditAndSj( hvo,list!=null&&list.size()>0?list.toArray(new PsnClassItemBVO[0]):null,pvalue,status_audit,"不通过",((ClientUI)getBillUI()).getWaPeriodVO(),false);
			String rmsg = WaPubCount.onAuditYJSH(hvo,list!=null&&list.size()>0?list.toArray(new PsnClassItemBVO[0]):null, pvalue, pk_item12, "不通过", ((ClientUI)getBillUI()).getWaPeriodVO(), false);
			MessageDialog.showHintDlg(this.getBillUI(), "提示",rmsg);
			onBoRefresh();
		}
	}
	@Override
	public void onBoAudit() throws Exception {
		IHrPara hrpara = NCLocator.getInstance().lookup(IHrPara.class);
		int pvalue=hrpara.getParaIntValue(_getCorp().getPrimaryKey(),"BMXCFH",null, null).intValue();
		int ok =  MessageDialog.showOkCancelDlg(this.getBillUI(), "提示", "确认审核选择的单据?");
		if(ok!=UIDialog.ID_OK) return;
		//int status_audit = WaHrpBillStatus.PASS;
		//一级审核状态
		int pk_item12 = WaHrpBillStatus.PASS;
		boolean islist = ((ClientUI)getBillUI()).isListPanelSelected();
		String msg = "";
		if(islist){
			int rowcount = ((ClientUI)getBillUI()).getBillListPanel().getHeadTable().getRowCount();
			for(int i=0;i<rowcount;i++){
				MyHYBillVO value = (MyHYBillVO)getBufferData().getVOByRowNo(i);
				PsnClassItemHVO hvo = (PsnClassItemHVO)value.getParentVO();
				boolean select = ((ClientUI)getBillUI()).getBillListPanel().getHeadBillModel().getValueAt(i, "bisselect")!=null?
						new UFBoolean(((ClientUI)getBillUI()).getBillListPanel().getHeadBillModel().getValueAt(i, "bisselect").toString()).booleanValue():false;
						if(select){
							//String rmsg = WaPubCount.onAuditAndSj( hvo,null,pvalue,status_audit,"通过",((ClientUI)getBillUI()).getWaPeriodVO(),true);
							String rmsg = WaPubCount.onAuditYJSH(hvo, null, pvalue, pk_item12, "通过", ((ClientUI)getBillUI()).getWaPeriodVO(), true);
							msg+=rmsg;
							PsnClassItemHVO newhvo = (PsnClassItemHVO)HYPubBO_Client.queryByPrimaryKey(PsnClassItemHVO.class, hvo.getPrimaryKey());
							value.setParentVO(newhvo);
							PsnClassItemBVO[] newbvos = (PsnClassItemBVO[])HYPubBO_Client.queryByCondition(PsnClassItemBVO.class, " isnull(dr,0)=0 and pk_psn_item_h='"+hvo.getPrimaryKey()+"' ");
							value.setChildrenVO(newbvos);
							getBufferData().setVOAt(i, value);
						}

			}
			updateBuffer();
			if((msg).trim().length()<=0){
				MessageDialog.showErrorDlg(this.getBillUI(), "提示","没有需要审核的单据");
			}else{
				MessageDialog.showHintDlg(this.getBillUI(), "提示",msg);
			}
		}else{
			if(getBufferData()==null||getBufferData().getCurrentVO()==null) return;
			PsnClassItemHVO hvo = (PsnClassItemHVO)getBufferData().getCurrentVO().getParentVO();
			int rowcount = getBillCardPanelWrapper().getBillCardPanel().getBillTable("wa_psn_item_b").getRowCount();
			ArrayList<PsnClassItemBVO> list = new ArrayList<PsnClassItemBVO>();
			for(int i=0;i<rowcount;i++){
				PsnClassItemBVO bvo = (PsnClassItemBVO)getBillCardPanelWrapper().getBillCardPanel().getBillModel("wa_psn_item_b").getBodyValueRowVO(i,PsnClassItemBVO.class.getName());
				list.add(bvo);
			}
			//String rmsg = WaPubCount.onAuditAndSj( hvo,list!=null&&list.size()>0?list.toArray(new PsnClassItemBVO[0]):null,pvalue,status_audit,"通过",((ClientUI)getBillUI()).getWaPeriodVO(),true);
			String rmsg = WaPubCount.onAuditYJSH( hvo,list!=null&&list.size()>0?list.toArray(new PsnClassItemBVO[0]):null, pvalue, pk_item12, "通过", ((ClientUI)getBillUI()).getWaPeriodVO(), true);
			MessageDialog.showHintDlg(this.getBillUI(), "提示",rmsg);
			onBoRefresh();
		}
	}
	
	protected void onBoQueryInit() throws Exception{

		StringBuffer strWhere = new StringBuffer();

		
		
		String pk_deptid = ((ClientUI)getBillUI()).getPk_deptid();
		String userid = _getOperator();
		String pk_corp=_getCorp().getPrimaryKey();
		String pk_module=IhrpNCModule.HRWA; 
		//(pk_wa_period = '10028L10000000033IKJ') 
		String curedate = ClientEnvironment.getInstance().getDate().toString().substring(0, 7);
		String wheresql = " (1=1) and (isnull(dr,0)=0) and pk_corp='"+pk_corp+"' and pk_wa_period = ( select pk_wa_period from wa_period where cyear||'-'||cperiod='"+curedate+"' and dr=0) ";

		//wheresql+="and vbillstatus_audit in("+WaHrpBillStatus.COMMIT+","+WaHrpBillStatus.PASS+","+WaHrpBillStatus.NOPASS+") ";
		wheresql+="and vbillstatus_audit in(" + WaHrpBillStatus.COMMIT + "," + WaHrpBillStatus.PASS + "," + WaHrpBillStatus.NOPASS + "," + WaHrpBillStatus.NEEDPASS + ") ";
		wheresql = wheresql+" and  "+" pk_dept in(" +
		" select pk_deptdoc from bd_wa_userdept where " +
		"   isnull(dr,0)=0 and pk_user='"+_getOperator()+"') ";

		//配置where语句，增加权限过滤
		String waclssSql = " and  pk_billtype='63RP' and pk_wa_class='"+IHRPWABtn.PK_JIANG+"' and  pk_wa_class in" +
		"((select classid from wa_uclsright " +
		" where pk_corp = '"+_getCorp().getPrimaryKey()+"' and cuserid = '"+_getOperator()+"' and moduleflag = 0) " +
		" union " +
		" (select classid from wa_gclsright where cgroupid in " +
		" (select pk_role from sm_user_role " +
		" where sm_user_role.cuserid = '"+_getOperator()+"') and pk_corp = '"+_getCorp().getPrimaryKey()+"' and moduleflag = 0))";

		SuperVO[] queryVos = queryHeadVOs(wheresql+" "+waclssSql);

		getBufferData().clear();
		// 增加数据到Buffer
		addDataToBuffer(queryVos);

		updateBuffer();
		int currRow = getBufferData().getCurrentRow();
		getBufferData().setCurrentRow(currRow);
		getBufferData().setCurrentRow(currRow);
		//onBoRefresh();
		//onBoRefresh();
	
	}
	protected void onBoQuery() throws Exception {
		StringBuffer strWhere = new StringBuffer();

		if (askForQueryCondition(strWhere) == false)
			return;// 用户放弃了查询
		String pk_deptid = ((ClientUI)getBillUI()).getPk_deptid();
		String userid = _getOperator();
		String pk_corp=_getCorp().getPrimaryKey();
		String pk_module=IhrpNCModule.HRWA; 
		String wheresql = strWhere.toString();

		//wheresql+="and vbillstatus_audit in("+WaHrpBillStatus.COMMIT+","+WaHrpBillStatus.PASS+","+WaHrpBillStatus.NOPASS+") ";
		wheresql+="and vbillstatus_audit in(" + WaHrpBillStatus.COMMIT + "," + WaHrpBillStatus.PASS + "," + WaHrpBillStatus.NOPASS + "," + WaHrpBillStatus.NEEDPASS + ") ";
		wheresql = wheresql+" and  "+" pk_dept in(" +
		" select pk_deptdoc from bd_wa_userdept where " +
		"   isnull(dr,0)=0 and pk_user='"+_getOperator()+"') ";

		//配置where语句，增加权限过滤
		String waclssSql = " and  pk_billtype='63RP' and pk_wa_class='"+IHRPWABtn.PK_JIANG+"' and  pk_wa_class in" +
		"((select classid from wa_uclsright " +
		" where pk_corp = '"+_getCorp().getPrimaryKey()+"' and cuserid = '"+_getOperator()+"' and moduleflag = 0) " +
		" union " +
		" (select classid from wa_gclsright where cgroupid in " +
		" (select pk_role from sm_user_role " +
		" where sm_user_role.cuserid = '"+_getOperator()+"') and pk_corp = '"+_getCorp().getPrimaryKey()+"' and moduleflag = 0))";

		SuperVO[] queryVos = queryHeadVOs(wheresql+" "+waclssSql);

		getBufferData().clear();
		// 增加数据到Buffer
		addDataToBuffer(queryVos);

		updateBuffer();
		int currRow = getBufferData().getCurrentRow();
		getBufferData().setCurrentRow(currRow);
		getBufferData().setCurrentRow(currRow);
		//onBoRefresh();
		//onBoRefresh();
	}
	@Override
	protected void onBoCard() throws Exception {
		super.onBoCard();
		int currRow = getBufferData().getCurrentRow();
		getBufferData().setCurrentRow(currRow);
		getBufferData().setCurrentRow(currRow);
	}
	@Override
	protected void onBoDel() throws Exception {
		if(getBufferData().getCurrentVO()!=null&&getBufferData().getCurrentVO().getParentVO()!=null){
			PsnClassItemHVO hvo = (PsnClassItemHVO)getBufferData().getCurrentVO().getParentVO();
			PsnClassItemHVO oldvo = (PsnClassItemHVO)HYPubBO_Client.queryByPrimaryKey(PsnClassItemHVO.class, hvo.getPrimaryKey());
			if(oldvo==null||!oldvo.getTs().equals(hvo.getTs())){
				MessageDialog.showErrorDlg(this.getBillUI(), "提示", "数据已被修改，请刷新后重试!");
				return;
			}
			int x = MessageDialog.showOkCancelDlg(this.getBillUI(), "提示", "确认作废当前单据，作废后不能恢复");
			if(x!= UIDialog.ID_OK){
				return;
			}
			hvo.setVbillstatus_audit(WaHrpBillStatus.DEL);
			hvo.setDapprovedate(_getDate());
			hvo.setVapproveid(_getOperator());
			HYPubBO_Client.update(hvo);
			onBoRefresh();
		}
	}
	@Override
	protected void onBoRefresh() throws Exception {
		// TODO Auto-generated method stub
		//super.onBoRefresh();
		
		onBoQueryInit();
	}
	
	
}
