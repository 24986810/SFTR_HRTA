package nc.ui.wa.wa_hrpreport_tr_001;

import java.util.ArrayList;
import java.util.Vector;

import nc.bs.framework.common.NCLocator;
import nc.itf.uap.IUAPQueryBS;
import nc.itf.uap.bd.def.IDefdoc;
import nc.jdbc.framework.processor.VectorProcessor;
import nc.ui.pub.ClientEnvironment;
import nc.ui.pub.bill.BillItem;
import nc.ui.trade.bill.ICardController;
import nc.ui.trade.business.HYPubBO_Client;
import nc.ui.trade.button.IBillButton;
import nc.ui.trade.card.BillCardUI;
import nc.ui.trade.card.CardEventHandler;
import nc.uif.pub.exception.UifException;
import nc.vo.bd.def.DefdocVO;
import nc.vo.bd.period.AccperiodVO;
import nc.vo.bd.period2.AccperiodmonthVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.btn.ExcelOutBtnVO;
import nc.vo.trade.button.ButtonVO;

/**
 * @author szh
 *
 */
public class ReportUI extends BillCardUI {

	/**
	 * 
	 */
	public ReportUI() {
		// TODO Auto-generated constructor stub
		getBillCardPanel().getHeadItem("pk_wa_period").setEnabled(true);
		getButtonManager().getButton(IBillButton.Print).setEnabled(true);
		
		updateButtons();
		try {
			String pk_wa_period = getPeriod();
			getBillCardPanel().getHeadItem("pk_wa_period").setValue(pk_wa_period);	
			ReportHandler myevent = (ReportHandler) createEventHandler();
			myevent.onBoQuery();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @param pk_corp
	 * @param pk_billType
	 * @param pk_busitype
	 * @param operater
	 * @param billId
	 */
	public ReportUI(String pk_corp, String pk_billType, String pk_busitype,
			String operater, String billId) {
		super(pk_corp, pk_billType, pk_busitype, operater, billId);
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see nc.ui.trade.card.BillCardUI#createController()
	 */
	@Override
	protected ICardController createController() {
		// TODO Auto-generated method stub
		return new ReportCtrl();
	}

	/* (non-Javadoc)
	 * @see nc.ui.trade.base.AbstractBillUI#getRefBillType()
	 */
	@Override
	public String getRefBillType() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see nc.ui.trade.base.AbstractBillUI#initSelfData()
	 */
	@Override
	protected void initSelfData() {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see nc.ui.trade.base.AbstractBillUI#setDefaultData()
	 */
	@Override
	public void setDefaultData() throws Exception {
		// TODO Auto-generated method stub

	}
	@Override
	protected CardEventHandler createEventHandler() {
		// TODO Auto-generated method stub
		return new ReportHandler(this,this.createController());
	}
	
	
	public String getPeriod() throws BusinessException{
		IUAPQueryBS service = (IUAPQueryBS)NCLocator.getInstance().lookup(IUAPQueryBS.class.getName());
		String curedate = ClientEnvironment.getInstance().getDate().toString().substring(0, 7);
		String strSQL = "select pk_wa_period from wa_period where cyear||'-'||cperiod='"+curedate+"' and dr=0";
		String pk_wa_period = "";
		
		Vector o1 = (Vector) service.executeQuery(strSQL,new VectorProcessor());
		if (o1.size() > 0 && o1 != null) {
			for (int i = 0; i < o1.size(); i++) {
				pk_wa_period = new String(((Vector) o1.elementAt(i)).elementAt(0) != null ? ((Vector) o1.elementAt(i)).elementAt(0).toString() : "");
			}
		}
		
		return pk_wa_period;
	}
}
