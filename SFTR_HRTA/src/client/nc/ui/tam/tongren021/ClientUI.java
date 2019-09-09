package nc.ui.tam.tongren021;


import nc.itf.hrp.pub.IHRPBtn;
import nc.itf.wa.wa_hrppub.WaHrpBillStatus;
import nc.ui.pub.ClientEnvironment;
import nc.ui.pub.FramePanel;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.bill.BillEditEvent;
import nc.ui.tam.tongren001.KqPsndocRefModel;
import nc.ui.trade.base.IBillOperate;
import nc.ui.trade.bill.AbstractManageController;
import nc.ui.trade.button.IBillButton;
import nc.ui.trade.manage.BillManageUI;
import nc.ui.trade.manage.ManageEventHandler;
import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.pub.btn.ExcelImportBtnVO;
import nc.vo.pub.lang.UFDate;
import nc.vo.tam.tongren008.ApplyHVO;
import nc.vo.tam.tongren020.WorkDayHVO;
import nc.vo.trade.button.ButtonVO;

/**
 * 加班天数复核
 * @author 28729
 *
 */
public class ClientUI extends BillManageUI {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 
	 */
	public ClientUI() {
		// TODO Auto-generated constructor stub
		if (getButtonManager().getButton(IHRPBtn.ExcelImport) != null) {
			getButtonManager().getButton(IHRPBtn.ExcelImport).setName("加班计算");
		}
		updateButtons();
	}

	/**
	 * @param fp
	 */
	public ClientUI(FramePanel fp) {
		super(fp);
		// TODO Auto-generated constructor stub
		
		getButtonManager().getButton(IBillButton.CancelAudit).setName("取消审核");
		updateButtons();
	}

	/**
	 * @param useBillSource
	 */
	public ClientUI(Boolean useBillSource) {
		super(useBillSource);
		// TODO Auto-generated constructor stub

	}

	/**
	 * @param pk_corp
	 * @param pk_billType
	 * @param pk_busitype
	 * @param operater
	 * @param billId
	 */
	public ClientUI(String pk_corp, String pk_billType, String pk_busitype,
			String operater, String billId) {
		super(pk_corp, pk_billType, pk_busitype, operater, billId);
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see nc.ui.trade.manage.BillManageUI#createController()
	 */
	@Override
	protected AbstractManageController createController() {
		// TODO Auto-generated method stub
		return new ClientCtrl();
	}
	@Override
	protected ManageEventHandler createEventHandler() {
		// TODO Auto-generated method stub
		return new ClientEventHandler(this,this.createController());
	}
	/* (non-Javadoc)
	 * @see nc.ui.trade.manage.BillManageUI#setBodySpecialData(nc.vo.pub.CircularlyAccessibleValueObject[])
	 */
	@Override
	public void setBodySpecialData(CircularlyAccessibleValueObject[] vos)
	throws Exception {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see nc.ui.trade.manage.BillManageUI#setHeadSpecialData(nc.vo.pub.CircularlyAccessibleValueObject, int)
	 */
	@Override
	protected void setHeadSpecialData(CircularlyAccessibleValueObject vo,
			int intRow) throws Exception {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see nc.ui.trade.manage.BillManageUI#setTotalHeadSpecialData(nc.vo.pub.CircularlyAccessibleValueObject[])
	 */
	@Override
	protected void setTotalHeadSpecialData(CircularlyAccessibleValueObject[] vos)
	throws Exception {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see nc.ui.trade.base.AbstractBillUI#initSelfData()
	 */
	@Override
	protected void initSelfData() {
		// TODO Auto-generated method stub
		getBillCardPanel().setAutoExecHeadEditFormula(true);
		String wheredept = 	" pk_corp='" + ClientEnvironment.getInstance().getCorporation().getPrimaryKey()+ "' and isnull(bisseal,'N')='N' and pk_dept in (select pk_docid from bd_tr_userpower where isnull(dr,0)=0 and pk_user='"+ClientEnvironment.getInstance().getUser().getPrimaryKey()+"' and powertype=0 and  pk_corp='"+ClientEnvironment.getInstance().getCorporation().getPrimaryKey()+"') ";
		
	}
	@Override
	public void afterEdit(BillEditEvent e) {
		// TODO Auto-generated method stub
		if(e.getKey().equals("pk_dept")&&e.getPos()==HEAD){
			getBillCardPanel().getBillModel().clearBodyData();
			String pk_dept = ((UIRefPane)getBillCardPanel().getHeadItem("pk_dept").getComponent()).getRefPK();
		    UIRefPane pane = ((UIRefPane)getBillCardPanel().getBillModel().getItemByKey("psncode").getComponent());
		   ((KqPsndocRefModel)pane.getRefModel()).setClassWherePart(" pk_corp='" + _getCorp().getPrimaryKey() + "' and isnull(bisseal,'N')='N' and pk_dept='"+pk_dept+"' ");
		}else if(e.getKey().equals("psncode")){
			UIRefPane pane = (UIRefPane)getBillCardPanel().getBillModel().getItemByKey(e.getKey()).getComponent();
			Object[] dstartdate = (Object[])pane.getRefValues("trtam_deptdoc_kq_b.dstartdate");
			String[] pks = pane.getRefPKs();
			String[] psncodes = pane.getRefCodes();
			String[] psnnames = pane.getRefNames();
			Object[] pk_depts = (Object[])pane.getRefValues("trtam_deptdoc_kq_b.pk_dept");
			int row = getBillCardPanel().getBillTable().getSelectedRow();
			if(pks!=null&&pks.length>=1){
				int rowcount = getBillCardPanel().getBillModel().getRowCount();
				for(int i=0;i<pks.length;i++){
					if(i>0){
						if(row==rowcount-1){
							getBillCardPanel().getBillModel().addLine();
						}else{
							getBillCardPanel().getBillModel().insertRow(1);
						}
					}
					getBillCardPanel().getBillModel().setValueAt(pks[i], row+i, "pk_psndoc");
					getBillCardPanel().getBillModel().setValueAt(psncodes[i], row+i, "psncode");
					getBillCardPanel().getBillModel().setValueAt(psnnames[i], row+i, "psnname");
					getBillCardPanel().getBillModel().setValueAt(pk_depts[i], row+i, "pk_dept_old");
					getBillCardPanel().getBillModel().setValueAt(dstartdate[i], row+i, "dstartdate");
					getBillCardPanel().getBillModel().setValueAt(_getCorp().getPrimaryKey(), row+i, "pk_corp");
					getBillCardPanel().getBillModel().setValueAt(0, row+i, "istate");
					if(getBillCardPanel().getBillModel().getValueAt(e.getRow(), "denddate")!=null&&
							getBillCardPanel().getBillModel().getValueAt(e.getRow(), "denddate").toString().trim().length()>0){
						UFDate enddate = new UFDate(getBillCardPanel().getBillModel().getValueAt(e.getRow(), "denddate").toString());
						if(enddate.before(new UFDate(dstartdate[i].toString()))){
//							MessageDialog.showHintDlg(this, "提示", "截至日期不能早于开始日期");
							getBillCardPanel().getBillModel().setValueAt(null, row+i, "denddate");
						}
					}
				}	
			}else{
				getBillCardPanel().getBillModel().setValueAt(null, row, "pk_psndoc");
				getBillCardPanel().getBillModel().setValueAt(null, row, "psncode");
				getBillCardPanel().getBillModel().setValueAt(null, row, "psnname");
				getBillCardPanel().getBillModel().setValueAt(null, row, "pk_dept_old");
				getBillCardPanel().getBillModel().setValueAt(null, row, "dstartdate");
			}
			getBillCardPanel().getBillModel().execLoadFormula();
		}else if(e.getKey().equals("denddate")){
			if(e.getValue()!=null){
				if(getBillCardPanel().getBillModel().getValueAt(e.getRow(), "dstartdate")!=null&&
						getBillCardPanel().getBillModel().getValueAt(e.getRow(), "dstartdate").toString().trim().length()>0){
					UFDate enddate = new UFDate(getBillCardPanel().getBillModel().getValueAt(e.getRow(), "denddate").toString());
					UFDate begindate = new UFDate(getBillCardPanel().getBillModel().getValueAt(e.getRow(), "dstartdate").toString());
					if(enddate.before(begindate)){
						MessageDialog.showHintDlg(this, "提示", "截至日期不能早于开始日期");
						getBillCardPanel().getBillModel().setValueAt(null, e.getRow(), "denddate");
					}
				}
			}
		}
	}
	/* (non-Javadoc)
	 * @see nc.ui.trade.base.AbstractBillUI#setDefaultData()
	 */
	@Override
	public void setDefaultData() throws Exception {
		// TODO Auto-generated method stub
		getBillCardPanel().setHeadItem("pk_corp", _getCorp().getPrimaryKey());
		getBillCardPanel().setHeadItem("vapplypsnid", _getOperator());
		getBillCardPanel().setHeadItem("dapplydate", _getDate());
	}
	@Override
	public void afterUpdate() {
		// TODO Auto-generated method stub
		if(getBufferData().getCurrentVO()!=null&&getBufferData().getCurrentVO().getParentVO()!=null){
			WorkDayHVO hvo = (WorkDayHVO)getBufferData().getCurrentVO().getParentVO();
			if(hvo.getVbillstatus()!=null&&hvo.getVbillstatus() ==  WaHrpBillStatus.COMMIT){
				getButtonManager().getButton(IBillButton.CancelAudit).setEnabled(false);
				getButtonManager().getButton(IBillButton.Audit).setEnabled(true);
				int rowcount = getBillCardPanel().getBillTable().getRowCount();
//				for(int i=0;i<rowcount;i++){
//					String value = getBillCardPanel().getBillModel().getValueAt(i, "istate").toString();
//					if(value.equals("调入确认")){
//						getButtonManager().getButton(IBillButton.CancelAudit).setEnabled(false);
//						break;
//					}
//				}
			}else if(hvo.getVbillstatus()!=null&&hvo.getVbillstatus() ==  WaHrpBillStatus.PASS){
				
				getButtonManager().getButton(IBillButton.CancelAudit).setEnabled(true);
				getButtonManager().getButton(IBillButton.Audit).setEnabled(false);
			}else if(hvo.getVbillstatus()!=null&&hvo.getVbillstatus() ==  WaHrpBillStatus.WRITE){
				
				getButtonManager().getButton(IBillButton.CancelAudit).setEnabled(false);
				getButtonManager().getButton(IBillButton.Audit).setEnabled(false);
			}
			updateButtons();
		}
	}

	@Override
	protected void initPrivateButton() {
		// TODO Auto-generated method stub
		super.initPrivateButton();
		
		ButtonVO importbtn = new ExcelImportBtnVO().getButtonVO();
		addPrivateButton(importbtn);
		importbtn.setOperateStatus(new int[] { IBillOperate.OP_ADD, IBillOperate.OP_EDIT });
		
	}
	
	
}
