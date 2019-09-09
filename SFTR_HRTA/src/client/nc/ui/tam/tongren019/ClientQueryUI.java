package nc.ui.tam.tongren019;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import nc.bs.framework.common.NCLocator;
import nc.itf.hr.ta.IBclbDefining;
import nc.itf.hrp.pub.HRPPubTool;
import nc.itf.uap.bd.def.IDefdoc;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.bill.BillEditEvent;
import nc.ui.trade.bill.ICardController;
import nc.ui.trade.business.HYPubBO_Client;
import nc.ui.trade.button.IBillButton;
import nc.ui.trade.card.BillCardUI;
import nc.ui.trade.card.CardEventHandler;
import nc.uif.pub.exception.UifException;
import nc.vo.bd.def.DefdocVO;
import nc.vo.pub.lang.UFDate;
import nc.vo.tam.tongren003.PaibanWeekVO;
import nc.vo.tam.tongren007.ZhibanWeekVO;
import nc.vo.tbm.tbm_029.BclbHeaderVO;

/**
 * @author 28729
 *
 */
public class ClientQueryUI extends BillCardUI {

	/**
	 * 
	 */
	public ClientQueryUI() {
		// TODO Auto-generated constructor stub
		
		getButtonManager().getButton(IBillButton.Print).setName("导出");
		getButtonManager().getButton(IBillButton.Print).setEnabled(true);
		updateButtons();
	}

	/**
	 * @param pk_corp
	 * @param pk_billType
	 * @param pk_busitype
	 * @param operater
	 * @param billId
	 */
	public ClientQueryUI(String pk_corp, String pk_billType,
			String pk_busitype, String operater, String billId) {
		super(pk_corp, pk_billType, pk_busitype, operater, billId);
		// TODO Auto-generated constructor stub
	}
	@Override
	protected ICardController createController() {
		// TODO Auto-generated method stub
		return new ClientQueryCtrl();
	}
	@Override
	protected CardEventHandler createEventHandler() {
		// TODO Auto-generated method stub
		return new EventQueryHandler(this,this.createController());
	}
	@Override
	public void afterEdit(BillEditEvent e) {
		// TODO Auto-generated method stub
		if(e.getPos()==HEAD&&e.getKey().equals("bisshowtype")){
			try {
				onshow();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}

	public void onshow() throws Exception{
		IBclbDefining defin = NCLocator.getInstance().lookup(IBclbDefining.class);
		BclbHeaderVO[] bclbvos = defin.queryBclb029AllBclbHeader(_getCorp().getPrimaryKey(), null);
		IDefdoc def = NCLocator.getInstance().lookup(IDefdoc.class);
		DefdocVO[] defvos = def.queryDocs(_getCorp().getPrimaryKey(), "000154100000001119NG");
		DefdocVO[] defvos1= def.queryDocs(_getCorp().getPrimaryKey(), "000154100000001119NR");
		HashMap<String,DefdocVO> map_def = new HashMap<String, DefdocVO>();
		if(defvos!=null){
			for(DefdocVO defvo:defvos){
				map_def.put(defvo.getPrimaryKey(), defvo);
			}
		}
		if(defvos1!=null){
			for(DefdocVO defvo:defvos1){
				map_def.put(defvo.getPrimaryKey(), defvo);
			}
		}
		HashMap<String,BclbHeaderVO> map_bclb = new HashMap<String, BclbHeaderVO>();
		if(bclbvos!=null&&bclbvos.length>0){
			for(BclbHeaderVO bclbvo:bclbvos){
				bclbvo.setTimebegintime(bclbvo.getTimebegintime()+"~"+bclbvo.getTimeendtime());
				bclbvo.setPk_dd(bclbvo.getPk_dd()!=null&&map_def.get(bclbvo.getPk_dd())!=null?map_def.get(bclbvo.getPk_dd()).getDocname():null);
				bclbvo.setPk_bbz(bclbvo.getPk_bbz()!=null&&map_def.get(bclbvo.getPk_bbz())!=null?map_def.get(bclbvo.getPk_bbz()).getDocname():null);
				map_bclb.put(bclbvo.getPrimaryKey(), bclbvo);
			}
		}
		String bisshowtype = getBillCardPanel().getHeadItem("bisshowtype").getValueObject().toString();
		String key = "lbmc";
		if(bisshowtype.equals("地点")){
			key = "pk_dd";
		}else if(bisshowtype.equals("时间")){
			key = "timebegintime";
		}else if(bisshowtype.equals("班组")){
			key = "pk_bbz";
		}else if(bisshowtype.equals("简称")){
			key = "lbjc";
		}
		int rowcount = getBillCardPanel().getBillModel().getRowCount();
		String vdate = getBillCardPanel().getHeadItem("ddate").getValueObject().toString();
		UFDate begin = new UFDate(vdate.substring(0,10));
		UFDate end = new UFDate(vdate.substring(11));
		int days = new UFDate().getDaysBetween(begin, end);
		for(int i=0;i<rowcount;i++){
			for(int j=1;j<=days+1;j++){
				String pk_bb = getBillCardPanel().getBillModel().getValueAt(i, "pk_bb"+j+"")!=null?
						getBillCardPanel().getBillModel().getValueAt(i, "pk_bb"+j+"").toString().trim():null;
						if(pk_bb!=null&&pk_bb.trim().length()>0){
							String[] values = pk_bb.split(",");
							String vbbname = "";
							for(String value:values){
								BclbHeaderVO bclbvo = map_bclb.get(value);
								if(bclbvo!=null){
									vbbname+=""+(bclbvo.getAttributeValue(key)!=null?bclbvo.getAttributeValue(key).toString().trim():"")+"/";
								}
							}
							vbbname = vbbname.substring(0,vbbname.length()-1);
							getBillCardPanel().getBillModel().setValueAt(vbbname, i, "vbbnames"+j+"");
						}
						UFDate showdate = begin.getDateAfter(j-1);
						int index = getBillCardPanel().getBillModel().getBodyColByKey("vbbnames"+(j)+"");
						String pk_psndoc = getBillCardPanel().getBillModel().getValueAt(i, "pk_psndoc").toString().trim();
			}
		}
	}
	@Override
	public String getRefBillType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void initSelfData() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setDefaultData() throws Exception {
		// TODO Auto-generated method stub
		
	}
}
