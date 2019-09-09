package nc.ui.tam.tongren.basepower004;


import nc.ui.trade.businessaction.IBusinessActionType;
import nc.ui.trade.button.IBillButton;
import nc.ui.trade.treecard.ITreeCardController;
import nc.vo.tam.tongren.power.UserDeptVO;
import nc.vo.trade.pub.HYBillVO;

   
/**
 * @author admin
 *
 */
public class Ctrl  implements ITreeCardController  {

	/**
	 * 
	 */
	public Ctrl() {
		// TODO Auto-generated constructor stub
	}

	

	public String getBillType() {
		// TODO Auto-generated method stub
		return "6017010165";
		
	}


	public String[] getBillVoName() {
		// TODO Auto-generated method stub
		return new String[]{HYBillVO.class.getName(),
				UserDeptVO.class.getName(),UserDeptVO.class.getName()};
	}


	public int getBusinessActionType() {
		// TODO Auto-generated method stub
		//return IBusinessActionType.BD;
		return IBusinessActionType.BD;
	}

/*
	public String getBodyCondition() {
		// TODO Auto-generated method stub
		return  " pk_billtemplet = '0001AA10000000005SGD' and pos=1  order by tabindex ";
	}

*/

	public String getChildPkField() {
		// TODO Auto-generated method stub
		return null;
	}


	public String getPkField() {
		// TODO Auto-generated method stub
		return "pk_userdept";
	}

	
/*
	@Override
	public int[] getListButtonAry() {l
		// TODO Auto-generated method stub
		return new int[]{
				IBillButton.Save
			};
	}
*/


	public boolean isExistBillStatus() {
		// TODO Auto-generated method stub
		return false;
	}
	public boolean isSingleDetail(){
		return true;
		
	}


	public boolean isAutoManageTree() {
		// TODO Auto-generated method stub
		return false;
	}



	public boolean isChildTree() {
		// TODO Auto-generated method stub
		return false;
	}



	public boolean isTableTree() {
		// TODO Auto-generated method stub
		return false;
	}



	public String[] getCardBodyHideCol() {
		// TODO Auto-generated method stub
		return null;
	}



	public boolean isShowCardRowNo() {
		// TODO Auto-generated method stub
		return false;
	}



	public boolean isShowCardTotal() {
		// TODO Auto-generated method stub
		return false;
	}



	public String getBodyZYXKey() {
		// TODO Auto-generated method stub
		return null;
	}



	public String getHeadZYXKey() {
		// TODO Auto-generated method stub
		return null;
	}



	public Boolean isEditInGoing() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}



	public boolean isLoadCardFormula() {
		// TODO Auto-generated method stub
		return true;
	}



	public int[] getCardButtonAry() {
		// TODO 自动生成方法存根
		return new int[]{
				
				IBillButton.Edit,
				IBillButton.Save,
				IBillButton.Cancel,
				
		};
	}



	public String getBodyCondition() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
