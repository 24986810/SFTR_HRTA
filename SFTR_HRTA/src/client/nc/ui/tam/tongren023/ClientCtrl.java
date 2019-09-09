package nc.ui.tam.tongren023;

import nc.ui.hrp.pub.bill.HRPCtrl;
import nc.ui.trade.button.IBillButton;
import nc.vo.tam.tongren010.GxBVO;
import nc.vo.tam.tongren010.GxHVO;
import nc.vo.trade.pub.HYBillVO;

/**
 * @author 28729
 *
 */
public class ClientCtrl extends HRPCtrl {

	/**
	 * 
	 */
	public ClientCtrl() {
		// TODO Auto-generated constructor stub
	}
    @Override
    public String getBillType() {
    	// TODO Auto-generated method stub
    	return "60170101H1";// 6017010190
    }
    @Override
    public String[] getBillVoName() {
    	// TODO Auto-generated method stub
    	return new String[]{HYBillVO.class.getName(),GxHVO.class.getName(),GxBVO.class.getName()};
    }
    public int[] getCardButtonAry() {
		// TODO 自动生成方法存根
		return new int[]{IBillButton.Query,IBillButton.Add,IBillButton.Edit,
				IBillButton.Line,IBillButton.ImportBill,IBillButton.Save,
				IBillButton.Cancel,
				IBillButton.Return,IBillButton.Print,
				IBillButton.Refresh,IBillButton.Delete,1111};
	}
    public int[] getListButtonAry() {
		// TODO 自动生成方法存根
		return new int[]{IBillButton.Add,IBillButton.Edit,
				IBillButton.Query,IBillButton.Card,IBillButton.Print,
				IBillButton.Refresh,IBillButton.Delete};
	}
}
