package nc.ui.tam.tongren011;

import nc.ui.hrp.pub.bill.HRPCtrl;
import nc.ui.trade.bill.ISingleController;
import nc.ui.trade.businessaction.IBusinessActionType;
import nc.ui.trade.button.IBillButton;
import nc.vo.tam.tongren011.AdjustTamVO;
import nc.vo.trade.pub.HYBillVO;

public class ClientCtrl extends HRPCtrl implements ISingleController {

	public ClientCtrl() {
		super();
	}
	public String[] getBillVoName() {
		// TODO 自动生成方法存根
		return new String[]{
				HYBillVO.class.getName(),
				AdjustTamVO.class.getName(),
				AdjustTamVO.class.getName()
		};
	}
	public String getBillType() {
		// TODO 自动生成方法存根
		return "6017010485";
	}
	public boolean isSingleDetail() {
		// TODO Auto-generated method stub
		return false;
}

	@Override
	public int[] getCardButtonAry() {
		// TODO Auto-generated method stub
		return new int[]{		
				IBillButton.Query,
				IBillButton.Add,
				IBillButton.Edit,
				IBillButton.Save,
				IBillButton.Delete,
				IBillButton.Cancel,
				IBillButton.Return,
				IBillButton.Line,
				IBillButton.Refresh,
//				IBillButton.Print
//				//新增导入按钮
//				IHRPBtn.ExcelImport
		};
	}
	@Override
	public int[] getListButtonAry() {
		// TODO Auto-generated method stub
		return new int[] { 
				
				IBillButton.Add,
				IBillButton.Edit,
				IBillButton.Commit,
//				IBillButton.CancelAudit,
				IBillButton.Delete,
				IBillButton.Save,
				IBillButton.Cancel,
				IBillButton.Query, 
				
				IBillButton.Refresh,
//				//新增导入按钮
//				IHRPBtn.ExcelImport
		};
	}
	
	@Override
	public int getBusinessActionType() {
		// TODO Auto-generated method stub
		return IBusinessActionType.BD;
	}

	@Override
	public String getChildPkField() {
		// TODO Auto-generated method stub
		return super.getChildPkField();
	}


	@Override
	public boolean isLoadCardFormula() {
		// TODO Auto-generated method stub
		return true;
	}
	@Override
	public boolean isShowListRowNo() {
		// TODO Auto-generated method stub
		return true;
				
	}
	

}