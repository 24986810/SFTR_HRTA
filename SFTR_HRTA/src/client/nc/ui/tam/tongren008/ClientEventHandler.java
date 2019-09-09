/**
 * 
 */
package nc.ui.tam.tongren008;

import java.util.ArrayList;

import nc.bs.framework.common.NCLocator;
import nc.itf.hr.ta.IBclbDefining;
import nc.ui.pub.ButtonObject;
import nc.ui.pub.ClientEnvironment;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UIDialog;
import nc.ui.pub.pf.PfUtilClient;
import nc.ui.trade.base.IBillOperate;
import nc.ui.trade.bill.ISingleController;
import nc.ui.trade.business.HYPubBO_Client;
import nc.ui.trade.button.IBillButton;
import nc.ui.trade.controller.IControllerBase;
import nc.ui.trade.manage.BillManageUI;
import nc.ui.trade.manage.ManageEventHandler;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.querytemplate.TemplateInfo;
import nc.vo.tam.tongren003.PanbanWeekBVO;
import nc.vo.tam.tongren008.ApplyBVO;
import nc.vo.tam.tongren008.ApplyHVO;
import nc.vo.trade.pub.HYBillVO;

/**
 * @author 28729
 *
 */
public class ClientEventHandler extends ManageEventHandler {
	private QryDlg2 m_qryDlg = null;
	protected QryDlg2 createQryDLG() {
		TemplateInfo tempinfo = getTempInfo();
		QryDlg2 dlg = new QryDlg(this.getBillUI(),null,tempinfo);

		return dlg;
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
	public QryDlg2 getQryDlg2() {
		if (m_qryDlg == null) {
			m_qryDlg = createQryDLG();
		}
		return m_qryDlg;
	}
	/**
	 * @param billUI
	 * @param control
	 */
	public ClientEventHandler(BillManageUI billUI, IControllerBase control) {
		super(billUI, control);
		// TODO Auto-generated constructor stub
	}
	@Override
	protected void onBoLineAdd() throws Exception {
		// TODO Auto-generated method stub
		super.onBoLineAdd();
		int row = getBillCardPanelWrapper().getBillCardPanel().getBillTable().getSelectedRow();
		getBillCardPanelWrapper().getBillCardPanel().getBillModel().setValueAt(_getCorp().getPrimaryKey(), row, "pk_corp");
		getBillCardPanelWrapper().getBillCardPanel().getBillModel().setValueAt(0, row, "istate");
	}
	@Override
	protected void onBoLineIns() throws Exception {
		// TODO Auto-generated method stub
		super.onBoLineIns();
		int row = getBillCardPanelWrapper().getBillCardPanel().getBillTable().getSelectedRow();
		getBillCardPanelWrapper().getBillCardPanel().getBillModel().setValueAt(_getCorp().getPrimaryKey(), row, "pk_corp");
		getBillCardPanelWrapper().getBillCardPanel().getBillModel().setValueAt(0, row, "istate");
	}
	@Override
	protected void onBoLinePaste() throws Exception {
		// TODO Auto-generated method stub
		super.onBoLinePaste();
		int row = getBillCardPanelWrapper().getBillCardPanel().getBillTable().getSelectedRow();
		getBillCardPanelWrapper().getBillCardPanel().getBillModel().setValueAt(_getCorp().getPrimaryKey(), row, "pk_corp");
		getBillCardPanelWrapper().getBillCardPanel().getBillModel().setValueAt(0, row, "istate");
	}
	@Override
	protected void onBoLinePasteToTail() throws Exception {
		// TODO Auto-generated method stub
		super.onBoLinePasteToTail();
		int row = getBillCardPanelWrapper().getBillCardPanel().getBillTable().getSelectedRow();
		getBillCardPanelWrapper().getBillCardPanel().getBillModel().setValueAt(_getCorp().getPrimaryKey(), row, "pk_corp");
		getBillCardPanelWrapper().getBillCardPanel().getBillModel().setValueAt(0, row, "istate");
	}
	@Override
	protected void onBoSave() throws Exception {
		// TODO Auto-generated method stub
		getBillCardPanelWrapper().getBillCardPanel().dataNotNullValidate();
		int rowcount = getBillCardPanelWrapper().getBillCardPanel().getBillModel().getRowCount();
		ArrayList<String> list = new ArrayList<String>();
		for(int i=0;i<rowcount;i++){
			String pk_psndoc = getBillCardPanelWrapper().getBillCardPanel().getBillModel().getValueAt(i, "pk_psndoc").toString();
			if(list.contains(pk_psndoc)){
				MessageDialog.showHintDlg(this.getBillUI(), "��ʾ", "��"+(i+1)+"����Ա�ظ�");
				return;
			}else{
				list.add(pk_psndoc);
			}
		}
		if(!checkPsn()){
			return;
		}
		super.onBoSave();
		
		HYBillVO billvo = (HYBillVO)getBufferData().getCurrentVO();
		IBclbDefining bs = NCLocator.getInstance().lookup(IBclbDefining.class);
		bs.onSaveApply(billvo);
	}
	@Override
	protected void onBoQuery() throws Exception {
		// TODO Auto-generated method stub
		if (getQryDlg2().showModal() == UIDialog.ID_OK) {
			String wheresql = getQryDlg2().getWhereSql();
			if(wheresql == null){
				wheresql = "  pk_dept in (select pk_docid from bd_tr_userpower where isnull(dr,0)=0 and pk_user='"+ClientEnvironment.getInstance().getUser().getPrimaryKey()+"' and powertype=0 and  pk_corp='"+ClientEnvironment.getInstance().getCorporation().getPrimaryKey()+"')" ;
			}else{
				wheresql += " and pk_dept in (select pk_docid from bd_tr_userpower where isnull(dr,0)=0 and pk_user='"+ClientEnvironment.getInstance().getUser().getPrimaryKey()+"' and powertype=0 and  pk_corp='"+ClientEnvironment.getInstance().getCorporation().getPrimaryKey()+"')" ;
			}
			SuperVO[] queryVos = queryHeadVOs(wheresql);

			getBufferData().clear();
			// �������ݵ�Buffer
			addDataToBuffer(queryVos);

			updateBuffer();
		}
	}
	private boolean checkPsnAudit()throws Exception{
		int rowcount = getBillCardPanelWrapper().getBillCardPanel().getBillTable().getRowCount();
		for(int i=0;i<rowcount;i++){
			String psnid = getBillCardPanelWrapper().getBillCardPanel().getBillModel().getValueAt(i, "pk_psndoc").toString();
			String deptid = getBillCardPanelWrapper().getBillCardPanel().getBillModel().getValueAt(i, "pk_dept_old").toString();
			//��ʼ����
			UFDate dstartdate = new UFDate(getBillCardPanelWrapper().getBillCardPanel().getBillModel().getValueAt(i, "dstartdate").toString());
			if(dstartdate.compareTo(new UFDate("2018-01-01")) < 0){
				dstartdate= new UFDate("2018-01-01");
			}
			//�����ҽ�������
			UFDate denddate = new UFDate(getBillCardPanelWrapper().getBillCardPanel().getBillModel().getValueAt(i, "denddate").toString());
//			PanbanWeekBVO[] bvos =(PanbanWeekBVO[])HYPubBO_Client.queryByCondition(PanbanWeekBVO.class,
//					" isnull(dr,0)=0 and pk_psndoc='"+psnid+"' and pk_dept='"+deptid+"' and ddate>'"+denddate+"'  and pk_bb not in (select pk_bclbid from tbm_bclb where (lbbm like '9903%' or lbbm like '9905%' or lbbm like '9906%') ) ");
//			if(bvos!=null&&bvos.length>0){
//				MessageDialog.showHintDlg(this.getBillUI(), "��ʾ", "��"+(i+1)+"����Ա��ԭ����"+denddate+"֮�������Ű���Ϣ�����ȴ���");
//				return false;
//			}
			//UFDate begindate = denddate.getDateBefore(6);
			PanbanWeekBVO[] bbvos =(PanbanWeekBVO[])HYPubBO_Client.queryByCondition(PanbanWeekBVO.class,
					" isnull(dr,0)=0 and pk_psndoc='"+psnid+"' and pk_dept='"+deptid+"' and ddate>='" + dstartdate + "' and ddate<='"+denddate+"'  ");
		
		      ArrayList<UFDate> list = new ArrayList<UFDate>();
		      if(bbvos!=null&&bbvos.length>0){
		    	  for(PanbanWeekBVO bvo:bbvos){
		    		  if(!list.contains(bvo.getDdate()))
		    		  list.add(bvo.getDdate());
		    	  }
		      }
		      int size = denddate.getDaysAfter(dstartdate) + 1;
		      if(list.size() < size){
		    	  MessageDialog.showHintDlg(this.getBillUI(), "��ʾ", "��"+(i+1)+"����Ա��ԭ�����Ű���Ϣ�����������ȴ���");
					return false;
		      }
		}
		
		return true;
	}
	private boolean checkPsn() throws Exception{
		int rowcount = getBillCardPanelWrapper().getBillCardPanel().getBillTable().getRowCount();
		for(int i=0;i<rowcount;i++){
			String psnid = getBillCardPanelWrapper().getBillCardPanel().getBillModel().getValueAt(i, "pk_psndoc").toString();
			String deptid = getBillCardPanelWrapper().getBillCardPanel().getBillModel().getValueAt(i, "pk_dept_old").toString();
			//��ʼ����
			UFDate dstartdate = new UFDate(getBillCardPanelWrapper().getBillCardPanel().getBillModel().getValueAt(i, "dstartdate").toString());
			//�����ҽ�������
			UFDate denddate = new UFDate(getBillCardPanelWrapper().getBillCardPanel().getBillModel().getValueAt(i, "denddate").toString());
			PanbanWeekBVO[] bvos =(PanbanWeekBVO[])HYPubBO_Client.queryByCondition(PanbanWeekBVO.class,
					" isnull(dr,0)=0 and pk_psndoc='"+psnid+"' and pk_dept='"+deptid+"' and ddate>'"+denddate+"'  and pk_bb not in (select pk_bclbid from tbm_bclb where (lbbm like '9903%' or lbbm like '9905%' or lbbm like '9906%') ) ");
			if(bvos != null){
				if(bvos.length == 0){
					MessageDialog.showHintDlg(this.getBillUI(), "��ʾ", "��"+(i+1)+"����Ա��ԭ����"+denddate+"֮��δ�ϴ��Ű���Ϣ�����ȴ���");
					return false;
				}
			}
			
			//UFDate begindate = denddate.getDateBefore(6);
//			PanbanWeekBVO[] bbvos =(PanbanWeekBVO[])HYPubBO_Client.queryByCondition(PanbanWeekBVO.class,
//					" isnull(dr,0)=0 and pk_psndoc='"+psnid+"' and pk_dept='"+deptid+"' and ddate>='" + dstartdate + "' and ddate<='"+denddate+"'  ");
//		
//		      ArrayList<UFDate> list = new ArrayList<UFDate>();
//		      if(bbvos!=null&&bbvos.length>0){
//		    	  for(PanbanWeekBVO bvo:bbvos){
//		    		  if(!list.contains(bvo.getDdate()))
//		    		  list.add(bvo.getDdate());
//		    	  }
//		      }
//		      int size = denddate.getDaysAfter(dstartdate) + 1;
//		      if(list.size() < size){
//		    	  MessageDialog.showHintDlg(this.getBillUI(), "��ʾ", "��"+(i+1)+"����Ա��ԭ�����Ű���Ϣ�����������ȴ���");
//					return false;
//		      }
		}
		
		return true;
	}
	@Override
	public void onBoAdd(ButtonObject bo) throws Exception {
		// TODO Auto-generated method stub
		super.onBoAdd(bo);
		getBillCardPanelWrapper().getBillCardPanel().getHeadItem("pk_dept").setEnabled(true);
	}
	@Override
	protected void onBoEdit() throws Exception {
		// TODO Auto-generated method stub
		super.onBoEdit();
		getBillCardPanelWrapper().getBillCardPanel().getHeadItem("pk_dept").setEnabled(false);
	}
	@Override
	protected void onBoCancel() throws Exception {
		// TODO Auto-generated method stub
		super.onBoCancel();
	}
	@Override
	protected void onBoCancelAudit() throws Exception {
		// TODO Auto-generated method stub
		onBoRefresh();
		boolean flag = getButtonManager().getButton(IBillButton.CancelAudit).isEnabled();
		if(!flag){
			MessageDialog.showHintDlg(this.getBillUI(), "��ʾ", "����״̬�ѷ����仯����ˢ�£�");
			return;
		}
		if(getBufferData().getCurrentVO()!=null&&getBufferData().getCurrentVO().getParentVO()!=null){
			int x = MessageDialog.showOkCancelDlg(this.getBillUI(), "��ʾ", "ȷ��ȡ������?");
			if(x!=UIDialog.ID_OK) return;
			HYBillVO billvo = (HYBillVO)getBufferData().getCurrentVO();
			ApplyHVO hvo = (ApplyHVO)getBufferData().getCurrentVO().getParentVO();
			hvo.setDauditdate(null);
			hvo.setCauditpsnid(null);
			hvo.setBisaudit(new UFBoolean(false));
			ApplyBVO[] bvos =  (ApplyBVO[])getBufferData().getCurrentVO().getChildrenVO();

			for(ApplyBVO bvo:bvos){
				bvo.setIstate(0);
			}
			billvo.setParentVO(hvo);
			billvo.setChildrenVO(bvos);
			IBclbDefining bs = NCLocator.getInstance().lookup(IBclbDefining.class);
			bs.onCancleAuditApply(billvo);
			onBoRefresh();
		}
	}
	@Override
	public void onBoAudit() throws Exception {
		// TODO Auto-generated method stub1
		if(getBufferData().getCurrentVO()!=null&&getBufferData().getCurrentVO().getParentVO()!=null){
			int x = MessageDialog.showOkCancelDlg(this.getBillUI(), "��ʾ", "ȷ�Ͻ�ѡ����Աת��?");
			if(x!=UIDialog.ID_OK) return;
			if(!checkPsnAudit()){
				return;
			}
			HYBillVO billvo = (HYBillVO)getBufferData().getCurrentVO();
			ApplyHVO hvo = (ApplyHVO)getBufferData().getCurrentVO().getParentVO();
			hvo.setDauditdate(_getDate());
			hvo.setCauditpsnid(_getOperator());
			hvo.setBisaudit(new UFBoolean(true));
			ApplyBVO[] bvos =  (ApplyBVO[])getBufferData().getCurrentVO().getChildrenVO();

			for(ApplyBVO bvo:bvos){
				bvo.setIstate(1);
			}
			billvo.setParentVO(hvo);
			billvo.setChildrenVO(bvos);
			IBclbDefining bs = NCLocator.getInstance().lookup(IBclbDefining.class);
			bs.onAuditApply(billvo);
			onBoRefresh();
		}
	}

	

	@Override
	protected void onBoDelete() throws Exception {
		// TODO Auto-generated method stub
		
		//super.onBoDelete();
		

		// ����û�����ݻ��������ݵ���û��ѡ���κ���
		if (getBufferData().getCurrentVO() == null)
			return;

		if (MessageDialog.showOkCancelDlg(getBillUI(),
				nc.ui.ml.NCLangRes.getInstance().getStrByID("uifactory",
						"UPPuifactory-000064")/* @res "����ɾ��" */,
				nc.ui.ml.NCLangRes.getInstance().getStrByID("uifactory",
						"UPPuifactory-000065")/* @res "�Ƿ�ȷ��ɾ���û�������?" */
				, MessageDialog.ID_CANCEL) != UIDialog.ID_OK)
			return;
		
		// ����ɾ��
		HYBillVO billvo = (HYBillVO)getBufferData().getCurrentVO();
		IBclbDefining bs = NCLocator.getInstance().lookup(IBclbDefining.class);
		bs.onDelApply(billvo);

		AggregatedValueObject modelVo = getBufferData().getCurrentVO();
		getBusinessAction().delete(modelVo, getUIController().getBillType(),
				getBillUI()._getDate().toString(), getBillUI().getUserObject());
		if (PfUtilClient.isSuccess()) {
			// �����������
			getBillUI().removeListHeadData(getBufferData().getCurrentRow());
			if (getUIController() instanceof ISingleController) {
				ISingleController sctl = (ISingleController) getUIController();
				if (!sctl.isSingleDetail())
					getBufferData().removeCurrentRow();
			} else {
				getBufferData().removeCurrentRow();
			}

		}
		if (getBufferData().getVOBufferSize() == 0)
			getBillUI().setBillOperate(IBillOperate.OP_INIT);
		else
			getBillUI().setBillOperate(IBillOperate.OP_NOTEDIT);
		getBufferData().setCurrentRow(getBufferData().getCurrentRow());
	
		
		
	}
	
	
	
}
