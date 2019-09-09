package nc.ui.tam.tongren019;

import nc.ui.trade.button.IBillButton;

/**
 * @author 28729
 *
 */
public class ClientQueryCtrl extends ClientCtrl {

	/**
	 * 
	 */
	public ClientQueryCtrl() {
		// TODO Auto-generated constructor stub
	}
	public int[] getCardButtonAry() {
		// TODO Auto-generated method stub
		return new int[]{
				IBillButton.Query,
				IBillButton.Print
		};
	}
	public String getBillType() {
		// TODO Auto-generated method stub
		return "6017010195";
	}
}
