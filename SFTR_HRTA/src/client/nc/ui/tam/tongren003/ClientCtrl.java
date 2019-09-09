/**
 * 
 */
package nc.ui.tam.tongren003;

import nc.ui.trade.bill.ICardController;
import nc.ui.trade.businessaction.IBusinessActionType;
import nc.ui.trade.button.IBillButton;
import nc.vo.tam.tongren002.PaibanTempVO;
import nc.vo.tam.tongren003.PaibanWeekVO;
import nc.vo.trade.pub.HYBillVO;

/**
 * @author 28729
 *
 */
public class ClientCtrl implements ICardController {

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
				IBillButton.Add,
				802,
				801,
				803,
				804,808,
				805,806,807,
				IBillButton.Save,
				IBillButton.Cancel,
				IBillButton.Copy,
				IBillButton.Edit,
				IBillButton.Print,
				810
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
		return "6017010440";
	}

	/* (non-Javadoc)
	 * @see nc.ui.trade.controller.IControllerBase#getBillVoName()
	 */
	public String[] getBillVoName() {
		// TODO Auto-generated method stub
		return new String[]{
				HYBillVO.class.getName(),
				PaibanWeekVO.class.getName(),
				PaibanWeekVO.class.getName()
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
		return false;
	}

}
