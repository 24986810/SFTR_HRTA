package nc.ui.tam.tongren011;


import java.util.ArrayList;

import nc.bs.framework.common.NCLocator;
import nc.itf.hrp.pub.HRPPubTool;
import nc.itf.hrp.pub.Ihrppub;
import nc.itf.uap.busibean.ISysInitQry;
import nc.ui.hrp.pub.bill.HRPEventhandle;
import nc.ui.hrp.pub.bill.HRPEventhandleSingleHead;
import nc.ui.pub.ButtonObject;
import nc.ui.pub.ClientEnvironment;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UIDialog;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.bill.BillItem;
import nc.ui.pub.bill.BillModel;
import nc.ui.querytemplate.QueryConditionDLG;
import nc.ui.querytemplate.meta.FilterMeta;
import nc.ui.querytemplate.valueeditor.IFieldValueElementEditor;
import nc.ui.querytemplate.valueeditor.IFieldValueElementEditorFactory;
import nc.ui.querytemplate.valueeditor.RefElementEditor;
import nc.ui.querytemplate.valueeditor.UIRefpaneCreator;
import nc.ui.trade.business.HYPubBO_Client;
import nc.ui.trade.button.IBillButton;
import nc.ui.trade.controller.IControllerBase;
import nc.ui.trade.manage.BillManageUI;
import nc.ui.trade.query.INormalQuery;
import nc.vo.hr.para2.ParValueVO;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;
import nc.vo.pub.VOStatus;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.querytemplate.TemplateInfo;
import nc.vo.tam.tongren003.PaiBanAuditMsg;
import nc.vo.tam.tongren003.PaiBanAuditMsg2;
import nc.vo.tam.tongren011.AdjustTamVO;
import nc.vo.trade.pub.HYBillVO;

public class ClientEH extends HRPEventhandleSingleHead {
	BillManageUI ui = null;
	Integer ibillstatus = 0;
	public ClientEH(BillManageUI billUI, IControllerBase control) {
		super(billUI, control);
		ui = billUI;
	}
	@Override
	public void onBoAdd(ButtonObject bo) throws Exception {
		// TODO Auto-generated method stub
		onBoEdit();
		onBoLineAdd();
		getButtonManager().getButton(IBillButton.Commit).setEnabled(true);
		ui.updateButtons();
		ibillstatus = 1;
	}
	@Override
	protected void onBoQuery() throws Exception {
		// TODO Auto-generated method stub
		StringBuffer strWhere = new StringBuffer();

		if (askForQueryCondition(strWhere) == false)
			return;// 用户放弃了查询
		String sql = strWhere.toString();
		if(sql!=null&&sql.trim().length()>0){
			sql += " and vtype=1 and pk_dept in (select pk_docid from bd_tr_userpower where isnull(dr,0)=0 and pk_user='"+_getOperator()+"' and powertype=0 and  pk_corp='"+_getCorp().getPrimaryKey()+"')  and vtype='1' " ;
		}else{
			sql += " isnull(dr,0)=0 and vtype=1 and pk_corp='"+_getCorp().getPrimaryKey()+"' and pk_dept in (select pk_docid from bd_tr_userpower where isnull(dr,0)=0 and pk_user='"+_getOperator()+"' and powertype=0 and  pk_corp='"+_getCorp().getPrimaryKey()+"')  and vtype='1' " ;

		}
		SuperVO[] queryVos = queryHeadVOs(sql);

		getBufferData().clear();
		// 增加数据到Buffer
		addDataToBuffer(queryVos);

		updateBuffer();
		if(getBufferData().getVOBufferSize()<=0){
			getButtonManager().getButton(IBillButton.Edit).setEnabled(false);
			getBillUI().updateButtons();
		}else{
			HYBillVO aggvo = (HYBillVO) getBufferData().getCurrentVO();
			AdjustTamVO headvo = (AdjustTamVO) aggvo.getParentVO();
			
			if(headvo.getIstate() == 0 ){
				getButtonManager().getButton(IBillButton.Edit).setEnabled(true);
				getButtonManager().getButton(IBillButton.Commit).setEnabled(true);
				getButtonManager().getButton(IBillButton.Delete).setEnabled(true);
			}else if(headvo.getIstate() == 1){
//				getButtonManager().getButton(IBillButton.CancelAudit).setEnabled(true);
				getButtonManager().getButton(IBillButton.Edit).setEnabled(false);
				getButtonManager().getButton(IBillButton.Commit).setEnabled(false);
				getButtonManager().getButton(IBillButton.Delete).setEnabled(false);
			}else{
				getButtonManager().getButton(IBillButton.Edit).setEnabled(false);
				getButtonManager().getButton(IBillButton.Commit).setEnabled(false);
				getButtonManager().getButton(IBillButton.Delete).setEnabled(false);
			}
			
			
			getBillUI().updateButtons();
		}
	}
	
	protected boolean askForQueryCondition(StringBuffer sqlWhereBuf)
	throws Exception {
		if (sqlWhereBuf == null)
			throw new IllegalArgumentException(
					"askForQueryCondition().sqlWhereBuf cann't be null");
		final UIDialog querydialog = getQueryUI();
		
		final QueryConditionDLG qcd = new QueryConditionDLG(ui,getTempInfo());
		qcd.registerFieldValueEelementEditorFactory(new IFieldValueElementEditorFactory() {
			public IFieldValueElementEditor createFieldValueElementEditor(FilterMeta meta) {
				// TODO Auto-generated method stub
				if ("pk_dept".equals(meta.getFieldCode())) {
					try {
						String pk_corp = ClientEnvironment.getInstance().getCorporation().getPrimaryKey();
						String operatorid = ClientEnvironment.getInstance().getUser().getPrimaryKey();
						UIRefPane refPane = new UIRefpaneCreator((qcd).getQryCondEditor().getQueryContext()).createUIRefPane(meta);
						refPane.setWhereString(" pk_corp='" + pk_corp+ "' and isnull(bisseal,'N')='N' and pk_dept in (select pk_docid from bd_tr_userpower where isnull(dr,0)=0 and pk_user='"+operatorid+"' and powertype=0 and  pk_corp='"+pk_corp+"') ");
						return new RefElementEditor(refPane, meta.getReturnType());
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
				return null;
			}});
		
		if (qcd.showModal() != UIDialog.ID_OK)
			return false;
		INormalQuery query = (INormalQuery) querydialog;
		
		String strWhere = qcd.getWhereSQL();
		if (strWhere == null || strWhere.trim().length()==0)
			strWhere = "1=1";
		
		if (getButtonManager().getButton(IBillButton.Busitype) != null) {
			if (getBillIsUseBusiCode().booleanValue())
				// 业务类型编码
				strWhere = "(" + strWhere + ") and "
						+ getBillField().getField_BusiCode() + "='"
						+ getBillUI().getBusicode() + "'";
		
			else
				// 业务类型
				strWhere = "(" + strWhere + ") and "
						+ getBillField().getField_Busitype() + "='"
						+ getBillUI().getBusinessType() + "'";
		
		}
		
		strWhere = "(" + strWhere + ") and (isnull(dr,0)=0)";
		
		if (getHeadCondition() != null)
			strWhere = strWhere + " and " + getHeadCondition();
		// 现在我先直接把这个拼好的串放到StringBuffer中而不去优化拼串的过程
		sqlWhereBuf.append(strWhere);
		return true;
		}
	
	protected TemplateInfo getTempInfo() {
		ClientEnvironment ce = ClientEnvironment.getInstance();
		TemplateInfo tempinfo = new TemplateInfo();
		tempinfo.setPk_Org(ce.getCorporation().getPrimaryKey());
		tempinfo.setCurrentCorpPk(ce.getCorporation().getPrimaryKey());
		tempinfo.setFunNode(getUIController().getBillType());
		tempinfo.setUserid(ce.getUser().getPrimaryKey());
		tempinfo.setNodekey(getUIController().getBillType());
		return tempinfo;
	}
	
	protected void onBoLineAdd() throws Exception {
		// TODO Auto-generated method stub
		getBillListPanel().getHeadBillModel().addLine();	
		int lastRow=getBillListPanel().getHeadTable().getRowCount()-1;
		getBillListPanel().getHeadBillModel().setValueAt( _getCorp().getPrimaryKey(), lastRow, "pk_corp");
		getBillListPanel().getHeadBillModel().setValueAt( "1", lastRow, "vtype");
		getBillListPanel().getHeadBillModel().setValueAt(_getDate(), lastRow, "dapplydate");
		getBillListPanel().getHeadBillModel().setValueAt(ClientEnvironment.getInstance().getUser().getUserName(), lastRow, "vdef1");
//		String pk_psn = ((ClientUI)getBillUI()).getPk_psn();
		//		getBillListPanel().getHeadBillModel().setValueAt(pk_psn, lastRow, "pk_psn");
		getBillListPanel().getHeadBillModel().setValueAt(0, lastRow, "istate");
		getBufferData().setCurrentRow(lastRow);
		getBillListPanel().getHeadBillModel().execLoadFormula();
	}
	@Override
	protected void onBoCommit() throws Exception {
		// TODO Auto-generated method stub
		
		int x = MessageDialog.showOkCancelDlg(this.getBillUI(), "提示", "提交后不能修改，请确认?");
		if(x!=UIDialog.ID_OK) return;
		
		int[] rows = getBillListPanel().getHeadTable().getSelectedRows();
		ArrayList<String> list = new ArrayList<String>();
		for(int i=0;i<rows.length;i++){
			if(getBillListPanel().getHeadBillModel().getValueAt(rows[i], "istate")!=null
					&&getBillListPanel().getHeadBillModel().getValueAt(rows[i], "istate").toString().equals("新增")){
				getBillListPanel().getHeadBillModel().setValueAt(1, rows[i], "istate");
				
				if(ibillstatus != 1){
					getBillListPanel().getHeadBillModel().getRowAttribute(rows[i]).setRowState(BillModel.MODIFICATION);
				}
				list.add(""+rows[i]);
			}
		}
		if(!dataNoNullCheck()) return;
		SuperVO[] changeVOs=(SuperVO[]) getBillListPanel().getHeadBillModel().getBodyValueChangeVOs(getUIController().getBillVoName()[1]);
		//界面全部的VO
		SuperVO[] checkVOs=(SuperVO[]) getBillListPanel().getHeadBillModel().getBodyValueVOs(getUIController().getBillVoName()[1]);
		if(changeVOs.length!=0){
			
			ArrayList<SuperVO> listDeleteVos = new ArrayList<SuperVO>();
			ArrayList<SuperVO> listUpdateVos = new ArrayList<SuperVO>();
			ArrayList<SuperVO> listInsertVos = new ArrayList<SuperVO>();
			for (int i = 0; i < checkVOs.length; i++) {
				//				dataNotNullValidate(changeVOs[i]);    //验证空值
				// begin
				if(list.indexOf(""+i) !=-1){
					
				
				ParValueVO[] valuevos = (ParValueVO[])HYPubBO_Client.queryByCondition(ParValueVO.class, " isnull(dr,0)=0 and pk_corp='"+_getCorp().getPrimaryKey()+"' and  PAR_CODE='CHGPB' ");
				int day = 1;
				if(valuevos!=null&&valuevos.length>0){
					day = Integer.parseInt( valuevos[0].getPar_value());
				}
				// 取服务器时间减调整天数,如果在些区间就可以保存
				UFDate curserverdate = nc.ui.hr.global.Global.getServerTime().getDate();
				UFDate beforedate = curserverdate.getDateBefore(day);
				UFDate currdate = (UFDate) checkVOs[i].getAttributeValue("ddate");
				if(currdate.compareTo(beforedate) < 0){
					ibillstatus = 0;
					// 设置保存后状态
					setSaveOperateState();
					onBoRefresh();
					MessageDialog.showHintDlg(this.getBillUI(), "提示", "当前日期已不能再调班");
					return;
				}
				// end
				
				// begin 判断是否已经审核,未审核不能进行调班
				String pk_deptdoc = checkVOs[i].getAttributeValue("pk_dept").toString();
//				String pk_psndoc = changeVOs[i].getAttributeValue("").toString();
//				String pk_bbs = changeVOs[i].getAttributeValue("").toString();
				String vperiod = currdate.toString().substring(0, 7);
				
//				PaiBanAuditMsg2[] msgvos =(PaiBanAuditMsg2[]) HYPubBO_Client.queryByCondition(PaiBanAuditMsg2.class,
//						" isnull(dr,0)=0 and pk_dept='"+pk_deptdoc+"' and  ddate like '"+vperiod+"%' and vbillstatus=2");
//				
//				if(msgvos.length ==0){
//					MessageDialog.showHintDlg(this.getBillUI(), "提示", "该科室未二次审核不能调班!");
//					return;
//				}
//				 end
				
				switch (checkVOs[i].getStatus()) {   //根据VO状态修改VO
				case VOStatus.DELETED:
					if(changeVOs[i].getPrimaryKey()!=null){
						checkVOs[i].setAttributeValue("dr", new Integer(1));
						listDeleteVos.add(changeVOs[i]);
					}
					break;
				case VOStatus.UPDATED:
					if(checkVOs[i].getPrimaryKey()!=null){
						for(int j=1;j<51;j++){
							Object value = checkVOs[i].getAttributeValue("vbbname"+j+"");
							if(value!=null&&value.toString().trim().length()>0){
								checkVOs[i].setAttributeValue("nxhdays", j);
							}
						}
						listUpdateVos.add(checkVOs[i]);
					}
					break;
				default:
					if(checkVOs[i].getPrimaryKey()==null){
						for(int j=1;j<51;j++){
							Object value = checkVOs[i].getAttributeValue("vbbname"+j+"");
							if(value!=null&&value.toString().trim().length()>0){
								checkVOs[i].setAttributeValue("nxhdays", j);
							}
						}
						listInsertVos.add(checkVOs[i]);
					}
				break;
				}
			}
		}
			//修改数据的数据校验，如果为false则保存失败
			if(listDeleteVos.size()>0){
				if(!checkDeleteVOList(listDeleteVos)){
					return;
				}
			}
			//修改数据的数据校验，如果为false则保存失败
			if(listUpdateVos.size()>0){
				if(!checkUpdateVOList(listUpdateVos)){
					return;
				}
			}
			//插入数据的数据校验，如果为false则保存失败
			if(listInsertVos.size()>0){
				if(!checkInsertVOList(listInsertVos)){
					return;
				}
			}
			//插入数据
			Ihrppub impl = (Ihrppub)NCLocator.getInstance().lookup(Ihrppub.class.getName());
			listInsertVos=impl.saveVOs(listDeleteVos,listUpdateVos,listInsertVos);
			//将插入的数据更新到checkVOs中
			if(listInsertVos.size()>0){
				fillUITotalVO(checkVOs, listInsertVos.toArray(new SuperVO[0]));
			}
			//生成聚合VO更新至缓存
			ArrayList<AggregatedValueObject> listVOs = creatAggregatedValueObjects(checkVOs);
			getBufferData().clear();
			getBufferData().addVOsToBuffer(listVOs.toArray(new HYBillVO[0]));
			getBufferData().refresh();
			updateBuffer();
			
			
		}
		ibillstatus = 0;
		// 设置保存后状态
		setSaveOperateState();
		onBoRefresh();
		MessageDialog.showHintDlg(this.getBillUI(), "提示", "提交完成");
	};
	@Override
	protected void onBoEdit() throws Exception {
		super.onBoEdit();
	}
	private boolean dataNoNullCheck(){
		BillItem[] items = getBillListPanel().getHeadBillModel().getBodyItems();
		int rowcount = getBillListPanel().getHeadBillModel().getRowCount();
		String msg = "";
		for(int i=0;i<rowcount;i++){
			String msgr = "";
			for(BillItem item:items){
				if(item.isNull()&&(getBillListPanel().getHeadBillModel().getValueAt(i, item.getKey())==null||
						getBillListPanel().getHeadBillModel().getValueAt(i, item.getKey()).toString().trim().length()<=0)){
					msgr+=""+item.getName()+",";
				}
			}
			if(msgr.trim().length()>0){
				msgr = "第"+(i+1)+"行"+msgr+"不可为空;\n";
				msg+=msgr;
			}
		}
		if(msg.trim().length()>0){
			MessageDialog.showHintDlg(this.getBillListPanel(), "提示", msg);
			return false;
		}
		return true;
	}

	@Override
	protected void onBoSave() throws Exception {
		if(!dataNoNullCheck()) return;
		SuperVO[] changeVOs=(SuperVO[]) getBillListPanel().getHeadBillModel().getBodyValueChangeVOs(getUIController().getBillVoName()[1]);
		//界面全部的VO
		SuperVO[] checkVOs=(SuperVO[]) getBillListPanel().getHeadBillModel().getBodyValueVOs(getUIController().getBillVoName()[1]);
		if(changeVOs.length!=0){
			
			ArrayList<SuperVO> listDeleteVos = new ArrayList<SuperVO>();
			ArrayList<SuperVO> listUpdateVos = new ArrayList<SuperVO>();
			ArrayList<SuperVO> listInsertVos = new ArrayList<SuperVO>();
			for (int i = 0; i < changeVOs.length; i++) {
				//				dataNotNullValidate(changeVOs[i]);    //验证空值
				// begin
				ParValueVO[] valuevos = (ParValueVO[])HYPubBO_Client.queryByCondition(ParValueVO.class, " isnull(dr,0)=0 and pk_corp='"+_getCorp().getPrimaryKey()+"' and  PAR_CODE='CHGPB' ");
				int day = 1;
				if(valuevos!=null&&valuevos.length>0){
					day = Integer.parseInt( valuevos[0].getPar_value());
				}
				// 取服务器时间减调整天数,如果在些区间就可以保存
				UFDate curserverdate = nc.ui.hr.global.Global.getServerTime().getDate();
				UFDate beforedate = curserverdate.getDateBefore(day);
				UFDate currdate = (UFDate) changeVOs[i].getAttributeValue("ddate");
			
				if(currdate.compareTo(beforedate) < 0){
					MessageDialog.showHintDlg(this.getBillUI(), "提示", "当前日期已不能再调班");
					return;
				}
				// end
				
				// begin 判断是否已经审核,未审核不能进行调班
				String pk_deptdoc = changeVOs[i].getAttributeValue("pk_dept").toString();
//				String pk_psndoc = changeVOs[i].getAttributeValue("").toString();
//				String pk_bbs = changeVOs[i].getAttributeValue("").toString();
				String vperiod = currdate.toString().substring(0, 7);
				
				String bbname_new = changeVOs[i].getAttributeValue("bbname_new").toString();
				String[] names = bbname_new.split(",");
				
				if(names != null){
					for(int j=0;j<names.length;j++){
						String newname = names[j];
						if(newname.indexOf("值班") != -1){
							String pk_tmep = (String) changeVOs[i].getAttributeValue("pk_temp");
							if(pk_tmep == null){
								MessageDialog.showHintDlg(this.getBillUI(), "提示", "新班别中有值班，未选值班模板不能保存!");
								return;
							}
						}
					}
				}
				
//				PaiBanAuditMsg2[] msgvos =(PaiBanAuditMsg2[]) HYPubBO_Client.queryByCondition(PaiBanAuditMsg2.class,
//						" isnull(dr,0)=0 and pk_dept='"+pk_deptdoc+"' and  ddate like '"+vperiod+"%' and vbillstatus=2");
//				
//				if(msgvos.length ==0){
//					MessageDialog.showHintDlg(this.getBillUI(), "提示", "该科室未二次审核不能调班!");
//					return;
//				}
//				 end
				
				
				switch (changeVOs[i].getStatus()) {   //根据VO状态修改VO
				case VOStatus.DELETED:
					if(changeVOs[i].getPrimaryKey()!=null){
						changeVOs[i].setAttributeValue("dr", new Integer(1));
						listDeleteVos.add(changeVOs[i]);
					}
					break;
				case VOStatus.UPDATED:
					if(changeVOs[i].getPrimaryKey()!=null){
						for(int j=1;j<51;j++){
							Object value = changeVOs[i].getAttributeValue("vbbname"+j+"");
							if(value!=null&&value.toString().trim().length()>0){
								changeVOs[i].setAttributeValue("nxhdays", j);
							}
						}
						listUpdateVos.add(changeVOs[i]);
					}
					break;
				default:
					if(changeVOs[i].getPrimaryKey()==null){
						for(int j=1;j<51;j++){
							Object value = changeVOs[i].getAttributeValue("vbbname"+j+"");
							if(value!=null&&value.toString().trim().length()>0){
								changeVOs[i].setAttributeValue("nxhdays", j);
							}
						}
						listInsertVos.add(changeVOs[i]);
					}
				break;
				}
			}
			//修改数据的数据校验，如果为false则保存失败
			if(listDeleteVos.size()>0){
				if(!checkDeleteVOList(listDeleteVos)){
					return;
				}
			}
			//修改数据的数据校验，如果为false则保存失败
			if(listUpdateVos.size()>0){
				if(!checkUpdateVOList(listUpdateVos)){
					return;
				}
			}
			//插入数据的数据校验，如果为false则保存失败
			if(listInsertVos.size()>0){
				if(!checkInsertVOList(listInsertVos)){
					return;
				}
			}
			//插入数据
			Ihrppub impl = (Ihrppub)NCLocator.getInstance().lookup(Ihrppub.class.getName());
			listInsertVos=impl.saveVOs(listDeleteVos,listUpdateVos,listInsertVos);
			//将插入的数据更新到checkVOs中
			if(listInsertVos.size()>0){
				fillUITotalVO(checkVOs, listInsertVos.toArray(new SuperVO[0]));
			}
			//生成聚合VO更新至缓存
			ArrayList<AggregatedValueObject> listVOs = creatAggregatedValueObjects(checkVOs);
			getBufferData().clear();
			getBufferData().addVOsToBuffer(listVOs.toArray(new HYBillVO[0]));
			getBufferData().refresh();
			updateBuffer();
			
			
		}
		ibillstatus = 0;
		// 设置保存后状态
		setSaveOperateState();
		onBoRefresh();
	}
	
	
	@Override
	protected void onBoCancel() throws Exception {
		// TODO Auto-generated method stub
		super.onBoCancel();
		ibillstatus =0;
		
	}
	@Override
	protected void onBoCancelAudit() throws Exception {
		// TODO Auto-generated method stub
		int[] rows = getBillListPanel().getHeadTable().getSelectedRows();
		for(int i=0;i<rows.length;i++){
			if(getBillListPanel().getHeadBillModel().getValueAt(rows[i], "istate")!=null
					&&getBillListPanel().getHeadBillModel().getValueAt(rows[i], "istate").toString().equals("提交")){
				getBillListPanel().getHeadBillModel().setValueAt(0, rows[i], "istate");
				getBillListPanel().getHeadBillModel().getRowAttribute(rows[i]).setRowState(BillModel.MODIFICATION);				
			}
		}
		onBoSave();
		
		if(getBufferData().getVOBufferSize()<=0){
			getButtonManager().getButton(IBillButton.Edit).setEnabled(false);
			
		}else{
			HYBillVO aggvo = (HYBillVO) getBufferData().getCurrentVO();
			if(aggvo != null){
				AdjustTamVO headvo = (AdjustTamVO) aggvo.getParentVO();
				
				if(headvo.getIstate() == 0 ){
					getButtonManager().getButton(IBillButton.Edit).setEnabled(true);
					getButtonManager().getButton(IBillButton.Commit).setEnabled(true);
					getButtonManager().getButton(IBillButton.Delete).setEnabled(true);
					getButtonManager().getButton(IBillButton.CancelAudit).setEnabled(false);
				}else if(headvo.getIstate() == 1){
					getButtonManager().getButton(IBillButton.CancelAudit).setEnabled(true);
					getButtonManager().getButton(IBillButton.Edit).setEnabled(false);
					getButtonManager().getButton(IBillButton.Commit).setEnabled(false);
					getButtonManager().getButton(IBillButton.Delete).setEnabled(false);
				}else{
					getButtonManager().getButton(IBillButton.Edit).setEnabled(false);
					getButtonManager().getButton(IBillButton.Commit).setEnabled(false);
					getButtonManager().getButton(IBillButton.Delete).setEnabled(false);
				}
			}

		}
		getBillUI().updateButtons();
		MessageDialog.showHintDlg(this.getBillUI(), "提示", "取消提交完成");
	}
	
	
}
