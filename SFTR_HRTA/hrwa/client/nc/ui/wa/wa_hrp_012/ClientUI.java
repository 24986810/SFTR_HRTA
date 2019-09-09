package nc.ui.wa.wa_hrp_012;


import nc.itf.hrp.pub.IHRPBtn;
import nc.itf.wa.wa_hrppub.WaHrpBillStatus;
import nc.ui.hrp.pub.bill.HRPManagerUI;
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
import nc.vo.wa.wa_hrp_012.RedDashedHVO;
import nc.vo.wa.wa_hrp_bw.OutStaffPsnHVO;

/**
 * 奖金红冲
 * @author 28729
 *
 */
public class ClientUI extends HRPManagerUI {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 
	 */
	public ClientUI() {
		// TODO Auto-generated constructor stub
		
	}

	/**
	 * @param fp
	 */
	public ClientUI(FramePanel fp) {
		super(fp);
		// TODO Auto-generated constructor stub
		if (getButtonManager().getButton(IHRPBtn.ExcelImport) != null) {
			getButtonManager().getButton(IHRPBtn.ExcelImport).setName("导入科室奖金");
			getButtonManager().getButton(IBillButton.Commit).setName("确认红冲");
		}
		
//		getBillListPanel().getBodyTabbedPane().get
				
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
	}
	/* (non-Javadoc)
	 * @see nc.ui.trade.base.AbstractBillUI#setDefaultData()
	 */
	@Override
	public void setDefaultData() throws Exception {
		// TODO Auto-generated method stub
		getBillCardPanel().setHeadItem("pk_corp", _getCorp().getPrimaryKey());
		
	}
	@Override
	public void afterUpdate() {
		// TODO Auto-generated method stub
		if(getBufferData().getCurrentVO()!=null&&getBufferData().getCurrentVO().getParentVO()!=null){
			RedDashedHVO hvo = (RedDashedHVO)getBufferData().getCurrentVO().getParentVO();
			if(hvo.getVbillstatus()!=null&&hvo.getVbillstatus() ==  WaHrpBillStatus.COMMIT
					|| hvo.getVbillstatus()!=null&&hvo.getVbillstatus() ==  WaHrpBillStatus.PASS){
				getButtonManager().getButton(IBillButton.Edit).setEnabled(false);
				getButtonManager().getButton(IBillButton.Delete).setEnabled(false);
				getButtonManager().getButton(IBillButton.Commit).setEnabled(false);
			
			}else{
				getButtonManager().getButton(IBillButton.Edit).setEnabled(true);
				getButtonManager().getButton(IBillButton.Delete).setEnabled(true);
			
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
