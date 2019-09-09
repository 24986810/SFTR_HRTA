package nc.ui.wa.wa_hrp_006;

import nc.ui.hrp.pub.bill.HRPCtrl;
import nc.ui.trade.businessaction.IBusinessActionType;
import nc.ui.trade.button.IBillButton;
import nc.vo.wa.wa_hrp_006.DeptChargeBVO;
import nc.vo.wa.wa_hrp_006.DeptChargeHVO;
import nc.vo.wa.wa_hrp_006.MyHYBillVO;

/**
 * @author szh
 *
 */
@SuppressWarnings("restriction")
public class ClientControl extends HRPCtrl  {

	public ClientControl() {
	}

	@Override
	public String getBillType() {
		return "65RP";
	}
	@Override
	public int[] getCardButtonAry() {
		return new int[]{
				IBillButton.Add,
				IBillButton.Line,
				//IBillButton.Edit,
				//IBillButton.Delete,
				IBillButton.Save,
				//IBillButton.Commit,
				//IBillButton.Audit,
				IBillButton.CancelAudit,
				IBillButton.Cancel,
				IBillButton.Refresh,
				IBillButton.Brow,
				IBillButton.Return,
				IBillButton.Query,
				IBillButton.Print,
				//IHRPWABtn.QUERYDEPTMNY
		};
	}
	public String[] getBillVoName() {
		return new String[]{
				MyHYBillVO.class.getName(),
				DeptChargeHVO.class.getName(),
				DeptChargeBVO.class.getName()
		};
	}
	public int getBusinessActionType() {
		return IBusinessActionType.PLATFORM;
	}
	public Boolean isEditInGoing() throws Exception {
		return true;
	}
	public boolean isExistBillStatus() {
		return true;
	}
	public boolean isLoadCardFormula() {
		return true;
	}
	public String[] getListBodyHideCol() {
		return null;
	}
	public int[] getListButtonAry() {
		return new int[]{
				IBillButton.Add,
				//IBillButton.Edit,
				//IBillButton.Delete,
				//IBillButton.Commit,
				//IBillButton.Audit,
				//IBillButton.CancelAudit,
				IBillButton.Refresh,
				IBillButton.Card,
				IBillButton.Query,
		};
	}

	public String[] getListHeadHideCol() {
		return null;
	}
	public boolean isShowListRowNo() {
		return true;
	}

	public boolean isShowListTotal() {
		return false;
	}
}
