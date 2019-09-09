package nc.ui.tam.tongren021;

import nc.itf.hrp.pub.IHRPBtn;
import nc.ui.trade.bill.AbstractManageController;
import nc.ui.trade.businessaction.IBusinessActionType;
import nc.ui.trade.button.IBillButton;
import nc.vo.tam.tongren020.WorkDayBVO;
import nc.vo.tam.tongren020.WorkDayHVO;
import nc.vo.trade.pub.HYBillVO;

/**
 * @author 28729
 *
 */
public class ClientCtrl extends AbstractManageController {

	/**
	 * 
	 */
	public ClientCtrl() {
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
				IBillButton.Audit,         //奖金复核
				IBillButton.CancelAudit,   //取消奖金复核
				IBillButton.Query,         //查询
				IBillButton.Refresh,
				IBillButton.Print,
				IBillButton.Return
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
		return false;
	}

	/* (non-Javadoc)
	 * @see nc.ui.trade.controller.IControllerBase#getBillType()
	 */
	public String getBillType() {
		// TODO Auto-generated method stub
		return "6017010494";//6017010493,6017010175
	}

	/* (non-Javadoc)
	 * @see nc.ui.trade.controller.IControllerBase#getBillVoName()
	 */
	public String[] getBillVoName() {
		// TODO Auto-generated method stub
		return new String[]{HYBillVO.class.getName(),
				WorkDayHVO.class.getName(),
				WorkDayBVO.class.getName()};
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
		return IBusinessActionType.BD;
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
		return false;
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
		return false;
	}

	/* (non-Javadoc)
	 * @see nc.ui.trade.bill.IListController#getListBodyHideCol()
	 */
	public String[] getListBodyHideCol() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see nc.ui.trade.bill.IListController#getListButtonAry()
	 */
	public int[] getListButtonAry() {
		// TODO Auto-generated method stub
		return new int[]{
				IBillButton.Audit,         //奖金复核
				IBillButton.CancelAudit,   //取消奖金复核
				IBillButton.Query,         //查询
				IBillButton.Refresh,
				IBillButton.Print,
				IBillButton.Card
		};
	}

	/* (non-Javadoc)
	 * @see nc.ui.trade.bill.IListController#getListHeadHideCol()
	 */
	public String[] getListHeadHideCol() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see nc.ui.trade.bill.IListController#isShowListRowNo()
	 */
	public boolean isShowListRowNo() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see nc.ui.trade.bill.IListController#isShowListTotal()
	 */
	public boolean isShowListTotal() {
		// TODO Auto-generated method stub
		return false;
	}

}
