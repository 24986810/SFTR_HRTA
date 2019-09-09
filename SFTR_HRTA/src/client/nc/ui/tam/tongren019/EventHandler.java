package nc.ui.tam.tongren019;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import nc.bs.framework.common.NCLocator;
import nc.itf.hr.ta.IBclbDefining;
import nc.itf.hrp.pub.HRPPubTool;
import nc.itf.hrp.pub.Ihrppub;
import nc.itf.uap.bd.def.IDefdoc;
import nc.ui.pub.ButtonObject;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UIDialog;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.trade.bill.ICardController;
import nc.ui.trade.business.HYPubBO_Client;
import nc.ui.trade.button.IBillButton;
import nc.ui.trade.card.BillCardUI;
import nc.ui.trade.card.CardEventHandler;
import nc.uif.pub.exception.UifException;
import nc.vo.bd.def.DefdocVO;
import nc.vo.hr.para2.ParValueVO;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDouble;
import nc.vo.tam.tongren001.DeptKqBVO;
import nc.vo.tam.tongren002.PaibanTempVO;
import nc.vo.tam.tongren003.PaiBanAuditMsg;
import nc.vo.tam.tongren003.PaibanWeekVO;
import nc.vo.tam.tongren003.PanbanWeekBVO;
import nc.vo.tam.tongren010.GxBVO;
import nc.vo.tam.tongren010.GxHVO;
import nc.vo.tbm.tbm_029.BclbHeaderVO;

/**
 * @author 28729
 *
 */
public class EventHandler extends CardEventHandler {

	protected int showtype = 804;
	/**
	 * @param billUI
	 * @param control
	 */
	public EventHandler(BillCardUI billUI, ICardController control) {
		super(billUI, control);
		// TODO Auto-generated constructor stub
	}
	@Override
	public void onBoAdd(ButtonObject bo) throws Exception {}
	@Override
	protected void onBoEdit() throws Exception {
		// TODO Auto-generated method stub
		int x = MessageDialog.showOkCancelDlg(this.getBillUI(), "提示", "确认按模板数据覆盖当前排班数据?");
		if(x!=UIDialog.ID_OK) return;
		String sql = " isnull(dr,0)=0 and isnull(bisstop,'N')='N' and pk_corp='"+_getCorp().getPrimaryKey()+"' order by  dbegindate ";
		PaibanTempVO[] vos = (PaibanTempVO[])HYPubBO_Client.queryByCondition(PaibanTempVO.class,sql);
		HashMap<String,PaibanTempVO> map = new HashMap<String, PaibanTempVO>();
		if(vos!=null&&vos.length>0){
			for(PaibanTempVO vo:vos){
				map.put(vo.getPk_psndoc()+vo.getPk_dept(), vo);
			}
		}
		int rowcount = getBillCardPanelWrapper().getBillCardPanel().getBillModel().getRowCount();
		for(int i=0;i<rowcount;i++){
			String pk_psndoc = getBillCardPanelWrapper().getBillCardPanel().getBillModel().getValueAt(i, "pk_psndoc").toString();
			String pk_dept = getBillCardPanelWrapper().getBillCardPanel().getBillModel().getValueAt(i, "pk_dept").toString();
			PaibanTempVO vo = map.get(pk_psndoc+pk_dept);
			if(vo!=null&& vo.getNxhdays()!=null&& vo.getNxhdays().intValue()>0){
				int xhdays = vo.getNxhdays();
				UFDate begindate = vo.getDbegindate();
				UFDate enddate = vo.getDenddate()!=null&&vo.getDenddate().toString().trim().length()>0?vo.getDenddate():new UFDate("2099-12-31");
				String vdate = getBillCardPanelWrapper().getBillCardPanel().getHeadItem("vdate").getValueObject().toString();
				UFDate begin = new UFDate(vdate.substring(0,10));
				UFDate begincopy = new UFDate(vdate.substring(0,10));
				if(begin.compareTo(begindate)<0) begin = begindate;
				int days = new UFDate().getDaysBetween(begindate, begin)+1;
				int beginindex = days%xhdays;
				if(beginindex==0){
					beginindex = xhdays;
				}
				for(int j=1;j<8;j++){
					if(begincopy.getDateAfter(j-1).compareTo(begindate)<0){
						continue;
					}
					if(begincopy.getDateAfter(j-1).compareTo(enddate)>0){
						continue;
					}
					if(getBillCardPanelWrapper().getBillCardPanel().getBillModel().getItemByKey("vbbname"+j+"").isShow()&&
							getBillCardPanelWrapper().getBillCardPanel().getBillModel().getItemByKey("vbbname"+j+"").isEnabled()){
						getBillCardPanelWrapper().getBillCardPanel().getBillModel().setValueAt(vo.getAttributeValue("vbbname"+beginindex+""), i, "vbbname"+j+"");
						getBillCardPanelWrapper().getBillCardPanel().getBillModel().setValueAt(vo.getAttributeValue("pk_bb"+beginindex+""), i, "pk_bb"+j+"");
					}
					beginindex++;
					if(beginindex>xhdays){
						beginindex = 1;
					}
				}
			}
		}
	}
	@Override
	protected void onBoSave() throws Exception {}
	@Override
	protected void onBoCancel() throws Exception {
		// TODO Auto-generated method stub
		

	}
	@Override
	protected void onBoCopy() throws Exception {}
	public void setdate(){}
	protected void onshow(int intBtn) throws Exception{
		showtype = intBtn;
		String key = "lbmc";
		if(intBtn==805){
			key = "pk_dd";
		}else if(intBtn==806){
			key = "timebegintime";
		}else if(intBtn==807){
			key = "pk_bbz";
		}else if(intBtn==808){
			key = "lbjc";
		}
		String vdate = getBillCardPanelWrapper().getBillCardPanel().getHeadItem("vdate").getValueObject().toString();
		UFDate begin = new UFDate(vdate.substring(0,10));
		UFDate end = new UFDate(vdate.substring(11));
		int rowcount = getBillCardPanelWrapper().getBillCardPanel().getBillModel().getRowCount();
		ArrayList<String> pk_list_psn = new ArrayList<String>();
		for(int i=0;i<rowcount;i++){
			String pk_psndoc = getBillCardPanelWrapper().getBillCardPanel().getBillModel().getValueAt(i, "pk_psndoc").toString().trim();
			pk_list_psn.add(pk_psndoc);
		}
		PanbanWeekBVO[] panbvos = (PanbanWeekBVO[])HYPubBO_Client.queryByCondition(PanbanWeekBVO.class,
				" isnull(dr,0)=0 and pk_corp='"+_getCorp().getPrimaryKey()+"' and isnull(biszb,'N')='Y' "+HRPPubTool.formInSQL("pk_psndoc", pk_list_psn)+" and ddate<='"+end+"' and ddate>='"+begin+"' ");
		ArrayList<String> list_zb = new ArrayList<String>();
		if(panbvos!=null&&panbvos.length>0){
			for(PanbanWeekBVO panbvo:panbvos){
				list_zb.add(panbvo.getPk_psndoc().trim()+panbvo.getDdate().toString().trim());
			}
		}
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

		for(int i=0;i<rowcount;i++){
			for(int j=1;j<8;j++){
				String pk_bb = getBillCardPanelWrapper().getBillCardPanel().getBillModel().getValueAt(i, "pk_bb"+j+"")!=null?
						getBillCardPanelWrapper().getBillCardPanel().getBillModel().getValueAt(i, "pk_bb"+j+"").toString().trim():null;
						if(pk_bb!=null&&pk_bb.trim().length()>0){
							String[] values = pk_bb.split(",");
							String vbbname = "";
							for(String value:values){
								BclbHeaderVO bclbvo = map_bclb.get(value);
								if(bclbvo!=null){
									vbbname+=""+(bclbvo.getAttributeValue(key)!=null?bclbvo.getAttributeValue(key).toString().trim():"")+",";
								}
							}
							map_bclb.get("10028L100000000000Q5");

							if(vbbname.trim().length()<=0){
								System.out.print("aaa");	
							}
							vbbname = vbbname.substring(0,vbbname.length()-1);
							getBillCardPanelWrapper().getBillCardPanel().getBillModel().setValueAt(vbbname, i, "vbbname"+j+"");
						}
						UFDate showdate = begin.getDateAfter(j-1);
						int index = getBillCardPanelWrapper().getBillCardPanel().getBillModel().getBodyColByKey("vbbname"+(j)+"");
						String pk_psndoc = getBillCardPanelWrapper().getBillCardPanel().getBillModel().getValueAt(i, "pk_psndoc").toString().trim();
						if(list_zb.contains(pk_psndoc+showdate)){
							getBillCardPanelWrapper().getBillCardPanel().getBillModel().setBackground(Color.pink, i, index);
						}
			}
		}
	}
	@Override
	protected void onBoElse(int intBtn) throws Exception {
		// TODO Auto-generated method stub


		switch (intBtn) {
		case 801:
			String vdate =  getBillCardPanelWrapper().getBillCardPanel().getHeadItem("vdate").getValueObject().toString();
			vdate =  vdate.substring(11);
			UFDate begin = new UFDate(vdate).getDateAfter(1);
			UFDate end = new UFDate(vdate).getDateAfter(7);
			getBillCardPanelWrapper().getBillCardPanel().getHeadItem("vdate").setValue(begin+"至"+end);
			setdate();
			break;
		case 802:
			vdate =  getBillCardPanelWrapper().getBillCardPanel().getHeadItem("vdate").getValueObject().toString();
			vdate =  vdate.substring(0,10);
			begin = new UFDate(vdate).getDateBefore(7);
			end = new UFDate(vdate).getDateBefore(1);
			getBillCardPanelWrapper().getBillCardPanel().getHeadItem("vdate").setValue(begin+"至"+end);
			setdate();
			break;
		case 803:
			int x = _getDate().getWeek();
			begin = _getDate().getDateBefore(x-1);
			end = _getDate().getDateAfter(7-x);
			getBillCardPanelWrapper().getBillCardPanel().getHeadItem("vdate").setValue(begin+"至"+end);
			setdate();
			break;
		case 804:
			onshow(intBtn);
			break;
		case 805:
			onshow(intBtn);
			break;
		case 806:
			onshow(intBtn);
			break;
		case 807:
			onshow(intBtn);
			break;
		case 808:
			onshow(intBtn);
			break;
		default:
			break;
		}
	}
}
