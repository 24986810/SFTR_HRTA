package nc.ui.wa.wa_hrpreport_tr_001;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import nc.bs.framework.common.NCLocator;
import nc.itf.uap.IUAPQueryBS;
import nc.itf.uap.bd.def.IDefdoc;
import nc.jdbc.framework.processor.BeanListProcessor;
import nc.ui.bd.def.DefdefBO_Client;
import nc.ui.bd.def.DefdocBO_Client;
import nc.ui.bd.def.DefdoclistBO_Client;
import nc.ui.hrp.pub.excel.ImportExcelData;
import nc.ui.pub.ButtonObject;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UIDialog;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.bill.BillItem;
import nc.ui.trade.bill.ICardController;
import nc.ui.trade.business.HYPubBO_Client;
import nc.ui.trade.card.BillCardUI;
import nc.ui.trade.card.CardEventHandler;
import nc.ui.wa.wa_hrp_pub.UpdateBB;
import nc.vo.bd.b05.PsnclVO;
import nc.vo.bd.def.DefdocVO;
import nc.vo.pub.SuperVO;
import nc.vo.wa.wa_hrppub.PsnDocBackVO;
import nc.vo.wa.wa_hrpreport_tr_001.ReportBodyVO;
import nc.vo.wa.wa_reporthrp_003.ReportBodyVO1;
import nc.vo.wa.wa_reporthrp_003.ReportBodyVO2;
import nc.vo.wa.wa_reporthrp_003.ReportHeadVO;

/**
 * @author szh
 *
 */
public class ReportHandler extends CardEventHandler {

	/**
	 * @param billUI
	 * @param control
	 */
	public ReportHandler(BillCardUI billUI, ICardController control) {
		super(billUI, control);
		// TODO Auto-generated constructor stub
	}
	@Override
	public void onBoAdd(ButtonObject bo) throws Exception {
		// TODO Auto-generated method stub
		UpdateBB bb = new UpdateBB();
		try {
			SuperVO[] vos = HYPubBO_Client.queryByCondition(PsnDocBackVO.class, " isnull(dr,0)=0 and vyear='"+_getDate().toString().substring(0,4)+"' and vperiod='"+_getDate().toString().substring(5,7)+"' ");
			if(vos!=null&&vos.length>0){
				int x = MessageDialog.showOkCancelDlg(this.getBillUI(), "提示", "登陆期间人员信息已归档，是否重新归档");
				if(x!=UIDialog.ID_OK) return;
			}
			bb.insertPsnBack(_getDate().getYear()+"",_getDate().toString().substring(5,7));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Override
	protected void onBoQuery() throws Exception {
		// TODO Auto-generated method stub
		UIRefPane pane = (UIRefPane)getBillCardPanelWrapper().getBillCardPanel().getHeadItem("pk_wa_period").getComponent();
		String pk_wa_period =  pane.getRefPK();
		
		if(pk_wa_period == null){
			MessageDialog.showErrorDlg(this.getBillUI(), "查询提示", "请选择需要查询的期间!");
			return;
		}
		IUAPQueryBS bs= NCLocator.getInstance().lookup(IUAPQueryBS.class);
		String sql = " SELECT pk_wa_dept pk_deptdoc,nmny1,nmny2,vapprovenote,case when ih.vdef8 = '01' then '不含规培生'  when ih.vdef8 = '03' then '含规培生' else ih.vdef8 end remark "
			+"   FROM WA_FENPAI_DETAIL fb"
			+"   left join wa_psn_item_h ih"
			+"     on fb.pk_wa_dept = ih.pk_dept"
			+"    and fb.pk_wa_period = ih.pk_wa_period"
			+"    and ih.dr = 0"
			+"  where"
			+"  fb.pk_wa_period = '"+pk_wa_period+"'";
		ArrayList<ReportBodyVO> list = (ArrayList<ReportBodyVO>)bs.executeQuery(sql, new BeanListProcessor(ReportBodyVO.class));
		getBillCardPanelWrapper().getBillCardPanel().getBillModel().setBodyDataVO(list.toArray(new ReportBodyVO[0] ));

		getBillCardPanelWrapper().getBillCardPanel().getBillModel().execLoadFormula();
	}
	@Override
	protected void onBoRefresh() throws Exception {
		// TODO Auto-generated method stub
		super.onBoRefresh();
	}

}
