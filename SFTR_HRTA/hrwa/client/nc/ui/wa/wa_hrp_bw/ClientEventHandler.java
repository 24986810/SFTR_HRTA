package nc.ui.wa.wa_hrp_bw;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import nc.bs.framework.common.NCLocator;
import nc.itf.hr.ta.IBclbDefining;
import nc.itf.hrp.pub.IHRPBtn;
import nc.itf.uap.IUAPQueryBS;
import nc.itf.wa.wa_hrppub.WaHrpBillStatus;
import nc.jdbc.framework.processor.BeanListProcessor;
import nc.jdbc.framework.processor.BeanProcessor;
import nc.jdbc.framework.processor.VectorProcessor;
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
import nc.vo.pub.lang.UFDouble;
import nc.vo.querytemplate.TemplateInfo;
import nc.vo.tam.tongren003.PaiPanReportVO;
import nc.vo.tam.tongren003.PanbanWeekBVO;
import nc.vo.tam.tongren008.ApplyBVO;
import nc.vo.tam.tongren008.ApplyHVO;
import nc.vo.tam.tongren020.WorkDayBVO;
import nc.vo.tam.tongren020.WorkDayHVO;
import nc.vo.trade.pub.HYBillVO;
import nc.vo.wa.wa_hrp_002.PERIODVO;
import nc.vo.wa.wa_hrp_002.PsnOutStaffVO;
import nc.vo.wa.wa_hrp_005.FenpeiItemHVO;
import nc.vo.wa.wa_hrp_bw.OutStaffPsnBVO;
import nc.vo.wa.wa_hrp_bw.OutStaffPsnHVO;

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
	private boolean checkPsn() throws Exception{
		int rowcount = getBillCardPanelWrapper().getBillCardPanel().getBillTable().getRowCount();
		for(int i=0;i<rowcount;i++){
			String psnid = getBillCardPanelWrapper().getBillCardPanel().getBillModel().getValueAt(i, "pk_psndoc").toString();
			String deptid = getBillCardPanelWrapper().getBillCardPanel().getBillModel().getValueAt(i, "pk_dept_old").toString();
			//开始日期
			UFDate dstartdate = new UFDate(getBillCardPanelWrapper().getBillCardPanel().getBillModel().getValueAt(i, "dstartdate").toString());
			//本科室截至日期
			UFDate denddate = new UFDate(getBillCardPanelWrapper().getBillCardPanel().getBillModel().getValueAt(i, "denddate").toString());
			PanbanWeekBVO[] bvos =(PanbanWeekBVO[])HYPubBO_Client.queryByCondition(PanbanWeekBVO.class,
					" isnull(dr,0)=0 and pk_psndoc='"+psnid+"' and pk_dept='"+deptid+"' and ddate>'"+denddate+"'  and pk_bb not in (select pk_bclbid from tbm_bclb where (lbbm like '9903%' or lbbm like '9905%' or lbbm like '9906%') ) ");
			if(bvos!=null&&bvos.length>0){
				MessageDialog.showHintDlg(this.getBillUI(), "提示", "第"+(i+1)+"行人员在原科室"+denddate+"之后已有排班信息，请先处理！");
				return false;
			}
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
		    	  MessageDialog.showHintDlg(this.getBillUI(), "提示", "第"+(i+1)+"行人员在原科室排班信息不完整，请先处理！");
					return false;
		      }
		}
		
		return true;
	}
	@Override
	public void onBoAdd(ButtonObject bo) throws Exception {
		// TODO Auto-generated method stub
		super.onBoAdd(bo);
		getBillCardPanelWrapper().getBillCardPanel().getHeadItem("voperatorid").setValue(this.getBillUI()._getOperator());
		getBillCardPanelWrapper().getBillCardPanel().getHeadItem("dmakedate").setValue(this.getBillUI()._getDate());

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
			int x = MessageDialog.showOkCancelDlg(this.getBillUI(), "提示", "确认取消调出?");
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
			int x = MessageDialog.showOkCancelDlg(this.getBillUI(), "提示", "确认将选择人员转出?");
			if(x!=UIDialog.ID_OK) return;
			if(!checkPsn()){
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
	protected void onBoCommit() throws Exception {
		// TODO Auto-generated method stub
//		super.onBoCommit();
		OutStaffPsnHVO hvo = (OutStaffPsnHVO) getBillCardPanelWrapper().getBillVOFromUI().getParentVO();
		OutStaffPsnHVO newhvo = (OutStaffPsnHVO) HYPubBO_Client.queryByPrimaryKey(OutStaffPsnHVO.class, hvo.getPk_outstaffpsn_h());
		
		int x = MessageDialog.showOkCancelDlg(this.getBillUI(), "提示", "请确认发放，发放之后不能修改?");
		if(x!=UIDialog.ID_OK) return;
		
		int status_audit = WaHrpBillStatus.COMMIT;
		newhvo.setVbillstatus(status_audit);
		newhvo.setDmakedate(_getDate());
		newhvo.setVoperatorid(_getOperator());
		HYPubBO_Client.update(newhvo);
		getBufferData().getCurrentVO().setParentVO(newhvo);
		updateBuffer();
		MessageDialog.showHintDlg(getBillUI(), "提示", "发放成功!");
		onBoRefresh();
	}

	public void onBoReadImport() throws BusinessException {
		String pk_wa_period = (String) getBillCardPanelWrapper().getBillCardPanel().getHeadItem("pk_wa_period").getValueObject();

		if(pk_wa_period == null){
			MessageDialog.showHintDlg(this.getBillUI(), "提示", "请选择发放期间!");
			return;
		}
		
		IUAPQueryBS service = NCLocator.getInstance().lookup(IUAPQueryBS.class);
		String sql="select * from WA_OUTSTAFFPSN_H where isnull(dr,0)=0 and pk_wa_period='"+pk_wa_period+"'";
		OutStaffPsnHVO hvo = (OutStaffPsnHVO) service.executeQuery(sql, new BeanProcessor(OutStaffPsnHVO.class)); 
		
		if(hvo != null){
			MessageDialog.showErrorDlg(this.getBillUI(), "提示", "操作失败：同一期间只能新增一条单据，请查询并修改!");
			return ;
		}
		
		
		String vyear = (String) getBillCardPanelWrapper().getBillCardPanel().getHeadItem("vyear").getValueObject();
		String vperiod = (String) getBillCardPanelWrapper().getBillCardPanel().getHeadItem("vperiod").getValueObject();

		
		HashMap<String,OutStaffPsnBVO> lastmap = getLastPeriodData(vyear,vperiod);
		HashMap<String,PsnOutStaffVO> psnmap = getPsnOutStaffVO();
		OutStaffPsnBVO[] psnclassitem = getPsnClassItem(pk_wa_period);
		
		if(psnclassitem != null){
			for(int i=0;i<psnclassitem.length;i++){
				OutStaffPsnBVO bvo = lastmap.get(psnclassitem[i].getPk_outstaff());
				String idno = "";
				String bankno = "";
				String account = "";
				if(bvo == null){
					PsnOutStaffVO psnvo = psnmap.get(psnclassitem[i].getPk_outstaff());
					
					if(psnvo != null){
						idno = psnvo.getIdno();
						bankno= psnvo.getBankno();
						account= psnvo.getAccount();
					}			
				}else{
					idno = bvo.getIdno();
					bankno= bvo.getBankno();
					account= bvo.getAccount();
				}
				psnclassitem[i].setIdno(idno);
				psnclassitem[i].setBankno(bankno);
				psnclassitem[i].setAccount(account);
				
			}
		}
		
		// 
		getBillCardPanelWrapper().getBillCardPanel().getBillModel().setBodyDataVO(psnclassitem);
		getBillCardPanelWrapper().getBillCardPanel().getBillModel().execLoadFormula();
	}
	
	public WorkDayBVO[] getDataFromPaiban(String pk_wa_period) throws BusinessException{
		
		IUAPQueryBS bs= NCLocator.getInstance().lookup(IUAPQueryBS.class);
		String sql = "select h.vbillno,h.pk_dept,b.pk_psndoc,b.nmny"
			+"  from wa_psn_item_h h"
			+"  left join wa_psn_item_b b"
			+"    on h.pk_psn_item_h = b.pk_psn_item_h"
			+" where h.pk_wa_period = '"+pk_wa_period+"'"
			+"   and h.dr = 0"
			+"   and b.dr = 0";
		ArrayList<WorkDayBVO> list = (ArrayList<WorkDayBVO>)bs.executeQuery(sql, new BeanListProcessor(WorkDayBVO.class));
		
		return list.toArray(new WorkDayBVO[0]);	
	}
	
	public HashMap<String,PsnOutStaffVO> getPsnOutStaffVO() throws BusinessException{
		HashMap<String,PsnOutStaffVO> mapall = new HashMap<String, PsnOutStaffVO>();
		
		IUAPQueryBS service = NCLocator.getInstance().lookup(IUAPQueryBS.class);
		String sql="select * from WA_PSN_OUTSTAFF where isnull(dr,0)=0 ";
		ArrayList<PsnOutStaffVO> list = (ArrayList<PsnOutStaffVO>)service.executeQuery(sql, new BeanListProcessor(PsnOutStaffVO.class));
		
		if(list != null){
			for(int i=0;i<list.size();i++){
				mapall.put(list.get(i).getPk_outstaff(), list.get(i));
			}
		}
		
		return mapall;
	}
	
	
	
	public HashMap<String,OutStaffPsnBVO> getLastPeriodData(String vyear,String vperiod) throws BusinessException{
		HashMap<String,OutStaffPsnBVO> mapall = new HashMap<String, OutStaffPsnBVO>();
		Integer lastperiod = new Integer(vperiod) - 1;
		IUAPQueryBS service = NCLocator.getInstance().lookup(IUAPQueryBS.class);
		String sql="select * from wa_period where isnull(dr,0)=0 and cperiod='"+lastperiod+"' and cyear='"+vyear+"'";
		PERIODVO waPeriodVO = (PERIODVO) service.executeQuery(sql, new BeanProcessor(PERIODVO.class)); 
		String pk_wa_period = "";
		if(waPeriodVO != null){
			pk_wa_period = waPeriodVO.getPk_wa_period();
		}
		
		IUAPQueryBS bs= NCLocator.getInstance().lookup(IUAPQueryBS.class);
		String strsql = "select *"
			+"  from wa_outstaffpsn_b h"
			+" where h.pk_wa_period = '"+pk_wa_period+"'"
			+"   and h.dr = 0";
		
		ArrayList<OutStaffPsnBVO> list = (ArrayList<OutStaffPsnBVO>)bs.executeQuery(strsql, new BeanListProcessor(OutStaffPsnBVO.class));
		
		if(list != null){
			for(int i=0;i<list.size();i++){
				mapall.put(list.get(i).getPk_outstaff(), list.get(i));
			}
		}
		
		return mapall;
	}
	
	
	public OutStaffPsnBVO[] getPsnClassItem(String sqlwhere) throws BusinessException{
		IUAPQueryBS service = (IUAPQueryBS)NCLocator.getInstance().lookup(IUAPQueryBS.class.getName());
		String sql = "select h.vbillno,h.pk_dept,b.pk_psndoc,b.nmny,'' idno, '' bankno, '' account,h.pk_wa_period,h.dmakedate "
			+"  from wa_psn_item_h h"
			+"  left join wa_psn_item_b b"
			+"    on h.pk_psn_item_h = b.pk_psn_item_h"
			+" where h.pk_wa_period = '"+sqlwhere+"' and stafftype ='2' and h.vbillstatus_audit='2'"
			+"   and h.dr = 0"
			+"   and b.dr = 0";
		OutStaffPsnBVO[] stffvos = null;
		
		ArrayList<OutStaffPsnBVO> list_vo = new ArrayList<OutStaffPsnBVO>();
		Vector o1 = (Vector) service.executeQuery(sql,new VectorProcessor());
		if (o1.size() > 0 && o1 != null) {
			stffvos = new OutStaffPsnBVO[o1.size()];
			for (int i = 0; i < o1.size(); i++) {
				OutStaffPsnBVO staffvo = new OutStaffPsnBVO();
				String vbillno = new String(((Vector) o1.elementAt(i)).elementAt(0) != null ? ((Vector) o1.elementAt(i)).elementAt(0).toString() : "");
				String pk_dept = new String(((Vector) o1.elementAt(i)).elementAt(1) != null ? ((Vector) o1.elementAt(i)).elementAt(1).toString() : "");
				String pk_psndoc = new String(((Vector) o1.elementAt(i)).elementAt(2) != null ? ((Vector) o1.elementAt(i)).elementAt(2).toString() : "");
				String nmny = new String(((Vector) o1.elementAt(i)).elementAt(3) != null ? ((Vector) o1.elementAt(i)).elementAt(3).toString() : "");
				String idno = new String(((Vector) o1.elementAt(i)).elementAt(4) != null ? ((Vector) o1.elementAt(i)).elementAt(4).toString() : "");
				String bankno = new String(((Vector) o1.elementAt(i)).elementAt(5) != null ? ((Vector) o1.elementAt(i)).elementAt(5).toString() : "");
				String account = new String(((Vector) o1.elementAt(i)).elementAt(6) != null ? ((Vector) o1.elementAt(i)).elementAt(6).toString() : "");
				String pk_wa_period = new String(((Vector) o1.elementAt(i)).elementAt(7) != null ? ((Vector) o1.elementAt(i)).elementAt(7).toString() : "");
				String dmakedate = new String(((Vector) o1.elementAt(i)).elementAt(8) != null ? ((Vector) o1.elementAt(i)).elementAt(8).toString() : "");

				
				
				staffvo.setVbillno(vbillno);
				staffvo.setPk_deptdoc(pk_dept);
				staffvo.setPk_outstaff(pk_psndoc);
				staffvo.setNmoney(new UFDouble(nmny));
				staffvo.setIdno(idno);
				staffvo.setBankno(bankno);
				staffvo.setAccount(account);
				staffvo.setPk_wa_period(pk_wa_period);
				staffvo.setDmakedate(new UFDate(dmakedate));
				
				stffvos[i] = staffvo;
				
		 
			}
		}

		return stffvos;
	}
}
