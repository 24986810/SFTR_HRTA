/**
 * 
 */
package nc.ui.tam.tongren003bb;

import nc.ui.tam.tongren003.ClientCtrl;
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
//				IBillButton.SelNone,
//				IBillButton.SelAll,
				IBillButton.Query,
				IBillButton.Audit,
				IBillButton.CancelAudit,
				IBillButton.Refresh,
				IBillButton.Print
		};
	}
	public String getBillType() {
		// TODO Auto-generated method stub
		return "6017010492";
	}
}
