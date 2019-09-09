package nc.ui.wa.wa_hrp_report_mutdept;

import nc.itf.hrp.pub.IHRPBtn;
import nc.ui.trade.bill.ICardController;
import nc.ui.trade.bill.ISingleController;
import nc.ui.trade.button.IBillButton;
import nc.vo.trade.pub.HYBillVO;
import nc.vo.wa.wa_reporthrp_008.DeptMnySumAndMxVO;

/**
 * @author szh
 *
 */
public class ReportCtrl implements ICardController, ISingleController {

	/**
	 * 
	 */
	public ReportCtrl() {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see nc.ui.trade.bill.ICardController#getCardBodyHideCol()
	 */
	public String[] getCardBodyHideCol() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see nc.ui.trade.bill.ICardController#getCardButtonAry()
	 */
	public int[] getCardButtonAry() {
		// TODO Auto-generated method stub
		return new int[]{
				IBillButton.Query,
				IBillButton.Print,
				IHRPBtn.ExcelOut,
		};
	}

	/* (non-Javadoc)
	 * @see nc.ui.trade.bill.ICardController#isShowCardRowNo()
	 */
	public boolean isShowCardRowNo() {
		// TODO Auto-generated method stub
		return true;
	}

	/* (non-Javadoc)
	 * @see nc.ui.trade.bill.ICardController#isShowCardTotal()
	 */
	public boolean isShowCardTotal() {
		// TODO Auto-generated method stub
		return true;
	}

	/* (non-Javadoc)
	 * @see nc.ui.trade.controller.IControllerBase#getBillType()
	 */
	public String getBillType() {
		// TODO Auto-generated method stub
		return "HY02031630";
	}

	/* (non-Javadoc)
	 * @see nc.ui.trade.controller.IControllerBase#getBillVoName()
	 */
	public String[] getBillVoName() {
		// TODO Auto-generated method stub
		return new String[]{
				HYBillVO.class.getName(),
				DeptMnySumAndMxVO.class.getName(),
				DeptMnySumAndMxVO.class.getName(),
		};
	}

	/* (non-Javadoc)
	 * @see nc.ui.trade.controller.IControllerBase#getBodyCondition()
	 */
	public String getBodyCondition() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see nc.ui.trade.controller.IControllerBase#getBodyZYXKey()
	 */
	public String getBodyZYXKey() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see nc.ui.trade.controller.IControllerBase#getBusinessActionType()
	 */
	public int getBusinessActionType() {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see nc.ui.trade.controller.IControllerBase#getChildPkField()
	 */
	public String getChildPkField() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see nc.ui.trade.controller.IControllerBase#getHeadZYXKey()
	 */
	public String getHeadZYXKey() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see nc.ui.trade.controller.IControllerBase#getPkField()
	 */
	public String getPkField() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see nc.ui.trade.controller.IControllerBase#isEditInGoing()
	 */
	public Boolean isEditInGoing() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see nc.ui.trade.controller.IControllerBase#isExistBillStatus()
	 */
	public boolean isExistBillStatus() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see nc.ui.trade.controller.IControllerBase#isLoadCardFormula()
	 */
	public boolean isLoadCardFormula() {
		// TODO Auto-generated method stub
		return true;
	}

	/* (non-Javadoc)
	 * @see nc.ui.trade.bill.ISingleController#isSingleDetail()
	 */
	public boolean isSingleDetail() {
		// TODO Auto-generated method stub
		return true;
	}

}
