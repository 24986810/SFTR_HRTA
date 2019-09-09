package nc.ui.tam.tongren021;

import java.util.ArrayList;

import nc.bs.framework.common.NCLocator;
import nc.itf.hr.ta.IBclbDefining;
import nc.itf.hrp.pub.IHRPBtn;
import nc.itf.uap.IUAPQueryBS;
import nc.itf.wa.wa_hrppub.WaHrpBillStatus;
import nc.jdbc.framework.processor.BeanListProcessor;
import nc.ui.pub.ButtonObject;
import nc.ui.pub.ClientEnvironment;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UIDialog;
import nc.ui.pub.bill.BillItem;
import nc.ui.trade.business.HYPubBO_Client;
import nc.ui.trade.button.IBillButton;
import nc.ui.trade.controller.IControllerBase;
import nc.ui.trade.manage.BillManageUI;
import nc.ui.trade.manage.ManageEventHandler;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.querytemplate.TemplateInfo;
import nc.vo.tam.tongren003.PaiPanReportVO;
import nc.vo.tam.tongren003.PanbanWeekBVO;
import nc.vo.tam.tongren008.ApplyBVO;
import nc.vo.tam.tongren008.ApplyHVO;
import nc.vo.tam.tongren020.WorkDayBVO;
import nc.vo.tam.tongren020.WorkDayHVO;
import nc.vo.trade.pub.HYBillVO;
import nc.vo.wa.wa_hrp_005.FenpeiItemHVO;

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
	@Override
	protected void onBoElse(int intBtn) throws Exception {
		// TODO Auto-generated method stub
		switch (intBtn) {
			case IHRPBtn.ExcelImport:
				onBoReadImport();
				break;
			default:
				break;
		}
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
		super.onBoSave();
	}
	@Override
	protected void onBoQuery() throws Exception {
		// TODO Auto-generated method stub
		if (getQryDlg2().showModal() == UIDialog.ID_OK) {
			String wheresql = getQryDlg2().getWhereSql();
			
			if(wheresql == null){
				wheresql = " 1 = 1  ";
			}
			SuperVO[] queryVos = queryHeadVOs(wheresql);

			getBufferData().clear();
			// 增加数据到Buffer
			addDataToBuffer(queryVos);

			updateBuffer();
		}
	}
	
	@Override
	public void onBoAdd(ButtonObject bo) throws Exception {
		// TODO Auto-generated method stub
		super.onBoAdd(bo);
	
	}
	@Override
	protected void onBoEdit() throws Exception {
		// TODO Auto-generated method stub
		super.onBoEdit();
		
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
			MessageDialog.showHintDlg(this.getBillUI(), "提示", "单据状态已发生变化，请刷新！");
			return;
		}
		if(getBufferData().getCurrentVO()!=null&&getBufferData().getCurrentVO().getParentVO()!=null){
			int x = MessageDialog.showOkCancelDlg(this.getBillUI(), "提示", "确认取消审核?");
			if(x!=UIDialog.ID_OK) return;
			HYBillVO billvo = (HYBillVO)getBufferData().getCurrentVO();
			
			WorkDayHVO hvo = (WorkDayHVO)getBufferData().getCurrentVO().getParentVO();
			hvo.setDapprovedate(null);
			hvo.setVapproveid("");
			hvo.setVbillstatus(WaHrpBillStatus.WRITE);
			
			HYPubBO_Client.update(hvo);
			onBoRefresh();
		}
	}
	@Override
	public void onBoAudit() throws Exception {
		// TODO Auto-generated method stub1
		if(getBufferData().getCurrentVO()!=null&&getBufferData().getCurrentVO().getParentVO()!=null){
			int x = MessageDialog.showOkCancelDlg(this.getBillUI(), "提示", "确认审核通过吗?");
			if(x!=UIDialog.ID_OK) return;
			
			WorkDayHVO hvo = (WorkDayHVO)getBufferData().getCurrentVO().getParentVO();
			hvo.setDapprovedate(_getDate());
			hvo.setVapproveid(_getOperator());
			hvo.setVbillstatus(WaHrpBillStatus.PASS);
			
			HYPubBO_Client.update(hvo);
			onBoRefresh();
		}
	}
	
	
	
	@Override
	protected void onBoCommit() throws Exception {
		// TODO Auto-generated method stub
//		super.onBoCommit();
		WorkDayHVO hvo = (WorkDayHVO) getBillCardPanelWrapper().getBillVOFromUI().getParentVO();
		WorkDayHVO newhvo = (WorkDayHVO) HYPubBO_Client.queryByPrimaryKey(WorkDayHVO.class, hvo.getPk_workday_h());
		
		int status_audit = WaHrpBillStatus.COMMIT;
		newhvo.setVbillstatus(status_audit);
		hvo.setDmakedate(_getDate());
		hvo.setVoperatorid(_getOperator());
		HYPubBO_Client.update(newhvo);
		getBufferData().getCurrentVO().setParentVO(newhvo);
		updateBuffer();
		MessageDialog.showHintDlg(getBillUI(), "提示", "提交成功!");
		onBoRefresh();
	}

	public void onBoReadImport() throws BusinessException {
		String begindate = (String) getBillCardPanelWrapper().getBillCardPanel().getHeadItem("begindate").getValueObject();
		String enddate = (String) getBillCardPanelWrapper().getBillCardPanel().getHeadItem("enddate").getValueObject();
		WorkDayBVO[] workdaybvos = getDataFromPaiban(begindate,enddate);
		getBillCardPanelWrapper().getBillCardPanel().getBillModel().setBodyDataVO(workdaybvos);
		getBillCardPanelWrapper().getBillCardPanel().getBillModel().execLoadFormula();
	}
	
	public WorkDayBVO[] getDataFromPaiban(String begindate,String enddate) throws BusinessException{
		IUAPQueryBS bs= NCLocator.getInstance().lookup(IUAPQueryBS.class);
		String sql = "select sum(b.zsts) workdays, pk_dept, pk_psndoc"
			+"  from trtam_paiban_b a"
			+"  left join tbm_wt b"
			+"    on a.pk_bb = b.pk_bclbid"
			+" where ddate >= '"+begindate+"'"
			+"   and ddate <= '"+enddate+"'"
			+"   and a.dr = 0"
			+"   and b.dr = 0"
			+" group by pk_dept, pk_psndoc"
			+" order by pk_dept, pk_psndoc";
		ArrayList<WorkDayBVO> list = (ArrayList<WorkDayBVO>)bs.executeQuery(sql, new BeanListProcessor(WorkDayBVO.class));
		
		return list.toArray(new WorkDayBVO[0]);

		
	}
}
