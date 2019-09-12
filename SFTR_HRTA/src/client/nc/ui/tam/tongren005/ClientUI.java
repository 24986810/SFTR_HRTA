 package nc.ui.tam.tongren005;
import nc.ui.pub.ButtonObject;
import nc.ui.tm.framework.button.lib.AddButton;
import nc.ui.trade.bill.ICardController;
import nc.ui.trade.button.IBillButton;
import nc.ui.trade.card.BillCardUI;
import nc.ui.trade.card.CardEventHandler;
import nc.vo.pub.btn.ExcelOutBtnVO;
import nc.vo.tam.button.BenWeekBtnVO;
import nc.vo.tam.button.NextWeekBtnVO;
import nc.vo.tam.button.PreWeekBtnVO;
import nc.vo.tam.button.ShowBCbtnVO;
import nc.vo.tam.button.ShowBbjcbtnVO;
import nc.vo.tam.button.ShowBzbtnVO;
import nc.vo.tam.button.ShowDdbtnVO;
import nc.vo.tam.button.ShowTimebtnVO;
import nc.vo.tam.button.UploadWeekBtnVO;
import nc.vo.trade.button.ButtonVO;

 public class ClientUI extends BillCardUI
 {
	
   public ClientUI()
  {
		getButtonManager().getButton(IBillButton.Add).setName("查询备份表");
   }

  public ClientUI(String pk_corp, String pk_billType, String pk_busitype, String operater, String billId)
   {
     super(pk_corp, pk_billType, pk_busitype, operater, billId);
  }
 
  protected void initPrivateButton()
   {
    ButtonVO outbtn = new ExcelOutBtnVO().getButtonVO();
    addPrivateButton(outbtn);

   }

  protected ICardController createController()
  {
   return new ClientCtrl();
  }

 public String getRefBillType()
  {
    return null;
  }
 
   protected void initSelfData()
   {
  }

   public void setDefaultData()
    throws Exception
  {
   }

  protected CardEventHandler createEventHandler()
  {
     return new ClientEventHandler(this, createController());
  }
 }

