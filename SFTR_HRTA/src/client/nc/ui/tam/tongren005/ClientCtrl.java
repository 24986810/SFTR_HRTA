package nc.ui.tam.tongren005;

import nc.ui.trade.bill.ICardController;
import nc.ui.trade.bill.ISingleController;
import nc.ui.trade.button.IBillButton;
import nc.vo.tam.tongren005.ZybMnyVO;
import nc.vo.trade.pub.HYBillVO;

 public class ClientCtrl
  implements ICardController, ISingleController
 {
   public String[] getCardBodyHideCol()
   {
    return null;
  }
 
  public int[] getCardButtonAry()
  {
    return new int[] { 
      5, 
     402,
	IBillButton.Add};
  }

   public boolean isShowCardRowNo()
   {
    return true;
  }

   public boolean isShowCardTotal()
  {
     return true;
  }

   public String getBillType()
  {
    return "6017010155";
   }

   public String[] getBillVoName()
   {
     return new String[] { 
       HYBillVO.class.getName(), 
      ZybMnyVO.class.getName(), 
      ZybMnyVO.class.getName() };
   }

   public String getBodyCondition()
   {
     return null;
   }
 
   public String getBodyZYXKey()
  {
     return null;
   }
 
   public int getBusinessActionType()
   {
     return 0;
   }

   public String getChildPkField()
   {
     return null;
   }

   public String getHeadZYXKey()
   {
     return null;
   }
 
   public String getPkField()
   {
     return null;
   }
 
   public Boolean isEditInGoing()
     throws Exception
   {
     return null;
   }
 
   public boolean isExistBillStatus()
   {
     return false;
   }

   public boolean isLoadCardFormula()
   {
     return true;
   }
 
   public boolean isSingleDetail()
   {
     return true;
   }
 }

