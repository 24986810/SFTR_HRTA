/**
 * 
 */
package nc.ui.tam.tongren007;

import java.awt.Color;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
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
import nc.vo.bd.b06.PsndocVO;
import nc.vo.bd.def.DefdocVO;
import nc.vo.hr.para2.ParValueVO;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.tam.tongren.power.UserClassTypeVO;
import nc.vo.tam.tongren001.DeptKqBVO;
import nc.vo.tam.tongren003.PaiBanAuditMsg;
import nc.vo.tam.tongren003.PanbanWeekBVO;
import nc.vo.tam.tongren006.ZhibanTempVO;
import nc.vo.tam.tongren007.ZhibanWeekVO;
import nc.vo.tbm.tbm_029.BclbHeaderVO;

/**
 * @author 28729
 *
 */
public class EventHandler extends CardEventHandler {

	private int showtype = 804;
	/**
	 * @param billUI
	 * @param control
	 */
	public EventHandler(BillCardUI billUI, ICardController control) {
		super(billUI, control);
		// TODO Auto-generated constructor stub
	}
	@Override
	public void onBoAdd(ButtonObject bo) throws Exception {
		// TODO Auto-generated method stub
		String[] pks = ((UIRefPane)getBillCardPanelWrapper().getBillCardPanel().getHeadItem("deptnamekq").getComponent()).getRefPKs();
		if(pks==null) return;
		ArrayList<String> list = new ArrayList<String>();
		list.addAll(Arrays.asList(pks));
//		PaiBanAuditMsg[] msgvos =(PaiBanAuditMsg[]) HYPubBO_Client.queryByCondition(PaiBanAuditMsg.class,
//				" isnull(dr,0)=0 "+HRPPubTool.formInSQL("pk_dept", list)+" and vbillstatus in('1','3') ");
		ArrayList<String> list_audit = new ArrayList<String>();
//		if(msgvos!=null&&msgvos.length>0){
//			for(PaiBanAuditMsg msgvo:msgvos){
//				list_audit.add(msgvo.getVperiod());
//			}
//		}

		ParValueVO[] valuevos = (ParValueVO[])HYPubBO_Client.queryByCondition(ParValueVO.class, " isnull(dr,0)=0 and pk_corp='"+_getCorp().getPrimaryKey()+"' and  PAR_CODE='BPB' ");
		int day = 1;
		if(valuevos!=null&&valuevos.length>0){
			day = Integer.parseInt( valuevos[0].getPar_value());
		}
		UFDate curdate = nc.ui.hr.global.Global.getServerTime().getDate();
		UFDate enableStartdate = curdate.getDateBefore(day);//可以编辑的最早的一天
		String vdate = getBillCardPanelWrapper().getBillCardPanel().getHeadItem("vdate").getValueObject().toString();
		if(list_audit.contains(vdate.substring(0,7))){
			MessageDialog.showHintDlg(this.getBillUI(), "提示", "当前日期已不能再排班");
			return;
		}
		UFDate enddate = new UFDate(vdate.substring(11));
		if(enddate.compareTo(enableStartdate)<0){
			MessageDialog.showHintDlg(this.getBillUI(), "提示", "当前日期已不能再排班");
			return;
		}
		// biscancle 替换 bisstop zhanghua 20190903
		ZhibanTempVO[] tempvos = (ZhibanTempVO[])HYPubBO_Client.queryByCondition(ZhibanTempVO.class,
				" isnull(dr,0)=0 and isnull(bisstop,'N')='N' and pk_dept like '%"+pks[0]+"%' and dbegindate<='"+enddate+"' ");
		HashMap<String, ZhibanTempVO> map_temp = new HashMap<String, ZhibanTempVO>();
		if(tempvos==null||tempvos.length<=0) {
			return;
		}else{
			for(ZhibanTempVO tempvo:tempvos){
				map_temp.put(tempvo.getPk_bb()+pks[0], tempvo);
			}
		}
		((ClientUI)getBillUI()).setEditstate(1);
		getBillCardPanelWrapper().getBillCardPanel().getBillModel().clearBodyData();
		getButtonManager().getButton(IBillButton.Add).setEnabled(false);
		getButtonManager().getButton(801).setEnabled(false);
		getButtonManager().getButton(802).setEnabled(false);
		getButtonManager().getButton(803).setEnabled(false);
		getButtonManager().getButton(804).setEnabled(false);
		getButtonManager().getButton(IBillButton.Edit).setEnabled(true);
		getButtonManager().getButton(IBillButton.Copy).setEnabled(true);
		getButtonManager().getButton(IBillButton.Save).setEnabled(true);
		getButtonManager().getButton(IBillButton.Cancel).setEnabled(true);
		getBillUI().updateButtons();
		getBillCardPanelWrapper().getBillCardPanel().setEnabled(true);
		getBillCardPanelWrapper().getBillCardPanel().getHeadItem("deptnamekq").setEnabled(false);
		getBillCardPanelWrapper().getBillCardPanel().getHeadItem("type").setEnabled(false);
		vdate = getBillCardPanelWrapper().getBillCardPanel().getHeadItem("vdate").getValueObject().toString();
		UFDate begin = new UFDate(vdate.substring(0,10));
		UFDate end = new UFDate(vdate.substring(11));
		try {


			ArrayList<ZhibanWeekVO> list_vo = new ArrayList<ZhibanWeekVO>();
			PsndocVO[] psnvos = (PsndocVO[])HYPubBO_Client.queryByCondition(PsndocVO.class, " isnull(dr,0)=0 and pk_corp='"+_getCorp().getPrimaryKey()+"' ");
			HashMap<String,PsndocVO> map_psn = new HashMap<String, PsndocVO>();
			for(PsndocVO psnvo:psnvos){
				map_psn.put(psnvo.getPrimaryKey(), psnvo);
			}
			
			
			
			PanbanWeekBVO[] weekbvos = (PanbanWeekBVO[])HYPubBO_Client.queryByCondition(PanbanWeekBVO.class,
					"isnull(dr,0)=0 and pk_corp='"+_getCorp().getPrimaryKey()+"' and isnull(biszb,'N')='Y' and (ddate>='"+begin+"' and ddate<='"+end+"') and  pk_deptzb like '%"+pks[0]+"%'  order by pk_bb,ddate ");
			if(weekbvos!=null&&weekbvos.length>0){
				HashMap<String,ZhibanWeekVO> map = new HashMap<String, ZhibanWeekVO>();
				for(PanbanWeekBVO bvos:weekbvos){
					
					String vbillstatus1 = bvos.getVbillstatus1();
					String vbillstatus2 = bvos.getVbillstatus2();
					String vbillstatus3 = bvos.getVbillstatus3();
					Integer uploadnum = bvos.getUploadnum();
					String vbillstatus = "";
					
					vbillstatus = getStatus(vbillstatus1,vbillstatus2,vbillstatus3);
//					// 医务状态
//					if(vbillstatus1 != null){
//						if(vbillstatus1.equals("2")){
//							vbillstatus += "医务审核通过";
//						}else if(vbillstatus1.equals("3")){
//							vbillstatus += "医务退回";
//							
//						}else if(vbillstatus1.equals("4")){
//							vbillstatus += "OA提交";
//						}else if(vbillstatus1.equals("10")){
//							vbillstatus += "审批通过";
//						}
//						
//					}
//					
//					//门办状态
//					if(vbillstatus2 != null){
//						if(vbillstatus2.equals("5")){
//							vbillstatus += "门办审核通过";
//						}else if(vbillstatus2.equals("6")){
//							vbillstatus += "门办退回";
//							
//						}
//					}
//					// 排班状太
//					if(vbillstatus3 != null){
//						if(vbillstatus3.equals("1")){
//							vbillstatus += "提交";
//						}else if(vbillstatus3.equals("0")){
//							vbillstatus += "未提交";
//						}
//					}
					
					ZhibanWeekVO weekvo = map.get(bvos.getPk_bb()+pks[0])!=null?map.get(bvos.getPk_bb()+pks[0]):new ZhibanWeekVO();
					weekvo.setPk_bb(bvos.getPk_bb());
					weekvo.setPk_temp_old(bvos.getPk_temp());
					weekvo.setPk_temp(map_temp.get(bvos.getPk_bb()+pks[0])!=null?map_temp.get(bvos.getPk_bb()+pks[0]).getPrimaryKey():bvos.getPk_temp());
					weekvo.setPk_dept(bvos.getPk_deptzb());
					weekvo.setDeptnamekq(bvos.getDeptzbname());
					weekvo.setUploadnum(uploadnum);
					
					
					if(vbillstatus.equals("门办退回")||vbillstatus.equals("医务退回")||vbillstatus.equals("退回")){
						weekvo.setVbillstatus("未提交");
						weekvo.setVbillstatus3("0");
						weekvo.setVbillstatus2("0");
						weekvo.setVbillstatus1("0");
					}else{
						weekvo.setVbillstatus(vbillstatus);
						weekvo.setVbillstatus3(vbillstatus3);
						weekvo.setVbillstatus2(vbillstatus2);
						weekvo.setVbillstatus1(vbillstatus1);
					}
					
					
					
					UFDate ddate = bvos.getDdate();
					int days = new UFDate().getDaysBetween(begin, ddate);
					weekvo.setAttributeValue("pk_bb"+(days+1)+"", weekvo.getAttributeValue("pk_bb"+(days+1)+"")!=null&&weekvo.getAttributeValue("pk_bb"+(days+1)+"").toString().trim().length()>0?
							(weekvo.getAttributeValue("pk_bb"+(days+1)+"").toString().trim()+","+bvos.getPk_psndoc()+""):bvos.getPk_psndoc());
					weekvo.setAttributeValue("vbbname"+(days+1)+"", weekvo.getAttributeValue("vbbname"+(days+1)+"")!=null&&weekvo.getAttributeValue("vbbname"+(days+1)+"").toString().trim().length()>0?
							(weekvo.getAttributeValue("vbbname"+(days+1)+"").toString().trim()+","+(map_psn.get(bvos.getPk_psndoc())).getPsnname()+""):(map_psn.get(bvos.getPk_psndoc())).getPsnname());
					map.put(bvos.getPk_bb()+pks[0], weekvo);
				}
				list_vo.addAll(map.values());
				String[] oldkeys = map.keySet().toArray(new String[0]);
				for(String oldkey:oldkeys){
					map_temp.remove(oldkey);
				}
			}
			if(map_temp!=null&&map_temp.size()>0){
				String[] keys = map_temp.keySet().toArray(new String[0]);
				for(int i=0;i<keys.length;i++){
					ZhibanWeekVO weekvo = new ZhibanWeekVO();
					ZhibanTempVO tempvo = map_temp.get(keys[i]);
					weekvo.setDr(0);
					weekvo.setPk_bb(tempvo.getPk_bb());
					weekvo.setPk_dept(tempvo.getPk_dept());
					weekvo.setPk_temp(tempvo.getPrimaryKey());
					weekvo.setDeptnamekq(tempvo.getDeptnamekq());
					weekvo.setVdate(vdate);
					weekvo.setPk_corp(_getCorp().getPrimaryKey());
					
					weekvo.setVbillstatus3("0");
					weekvo.setVbillstatus2("0");
					weekvo.setVbillstatus1("0");
					weekvo.setVbillstatus("未提交");
					weekvo.setUploadnum(0);
					
					list_vo.add(weekvo);
				}
			}
			if(list_vo!=null&&list_vo.size()>0){
				getBillCardPanelWrapper().getBillCardPanel().getBillModel().setBodyDataVO(list_vo.toArray(new ZhibanWeekVO[0]));
				getBillCardPanelWrapper().getBillCardPanel().getBillModel().execLoadFormula();
			}
		} catch (UifException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		int rowcount = getBillCardPanelWrapper().getBillCardPanel().getBillModel().getRowCount();
		
		
		for(int i=1;i<32;i++){
			String name = getBillCardPanelWrapper().getBillCardPanel().getBillModel().getItemByKey("vbbname"+(i)+"").getName().substring(0,3);
			UFDate showdate = begin.getDateAfter(i-1);
			//			getBillCardPanelWrapper().getBillCardPanel().getBillModel().getItemByKey("vbbname"+(i)+"").setName(name+"("+showdate+")");
			if(showdate.compareTo(enableStartdate)>=0){
			//	getBillCardPanelWrapper().getBillCardPanel().getBillModel().getItemByKey("vbbname"+(i)+"").setEdit(true);
				int index = getBillCardPanelWrapper().getBillCardPanel().getBillModel().getBodyColByKey("vbbname"+(i)+"");
				for(int j=0;j<rowcount;j++){
					getBillCardPanelWrapper().getBillCardPanel().getBillModel().setBackground(Color.cyan, j, index);
					
				}
			}else{
				//getBillCardPanelWrapper().getBillCardPanel().getBillModel().getItemByKey("vbbname"+(i)+"").setEdit(false);
				int index = getBillCardPanelWrapper().getBillCardPanel().getBillModel().getBodyColByKey("vbbname"+(i)+"");
				for(int j=0;j<rowcount;j++){
					getBillCardPanelWrapper().getBillCardPanel().getBillModel().setBackground(Color.gray, j, index);
					
				}
			}
		}
		
		UserClassTypeVO[] kqclasstypevos = (UserClassTypeVO[])HYPubBO_Client.queryByCondition(UserClassTypeVO.class, " isnull(dr,0)=0 and pk_user='"+_getOperator()+"' and powertype=0 and  pk_corp='"+_getCorp().getPrimaryKey()+"' and pk_docid='6' ");
		String autoaudit = "N";
		if(kqclasstypevos != null){
			if(kqclasstypevos.length>0){
				autoaudit="Y";
			}
		}
		
		for(int i=0;i<rowcount;i++){
			for(int j=1;j<32;j++){
				String vbillstatus = (String)getBillCardPanelWrapper().getBillCardPanel().getBillModel().getValueAt(i, "vbillstatus");
				if(!autoaudit.equals("Y")){
					if(vbillstatus!=null){
						if(vbillstatus.equals("医务审核通过门办审核通过") || vbillstatus.equals("医务审核通过") || vbillstatus.equals("门办审核通过") || vbillstatus.equals("提交") || vbillstatus.equals("OA提交")){
							getBillCardPanelWrapper().getBillCardPanel().getBillModel().setCellEditable(i, "vbbname"+(j)+"", false);;
						}
					}
				}else{
					if(vbillstatus!=null){
						if(vbillstatus.equals("OA提交")){
							getBillCardPanelWrapper().getBillCardPanel().getBillModel().setCellEditable(i, "vbbname"+(j)+"", false);;
						}
					}
				}
				
			}
		}

		getBillCardPanelWrapper().getBillCardPanel().setBillData(getBillCardPanelWrapper().getBillCardPanel().getBillData());
	}
	
	public String getStatus(String vbillstatus1,String vbillstatus2,String vbillstatus3){
		
		String status ="";
		if(vbillstatus1.equals("1") && vbillstatus2.equals("1") && vbillstatus3.equals("1")){
			status += "提交";
		}else if(vbillstatus1.equals("0") && vbillstatus2.equals("0") && vbillstatus3.equals("0")){
			status += "未提交";
		}else if(!vbillstatus1.equals("1")&& !vbillstatus1.equals("0")){
			if(vbillstatus1 != null){
				if(vbillstatus1.equals("2")){
					status += "医务审核通过";
				}else if(vbillstatus1.equals("3")){
					status += "医务退回";
				}else if(vbillstatus1.equals("4")){
					status += "OA提交";
				}else if(vbillstatus1.equals("10")){
					status += "审批通过";
				}else if(vbillstatus1.equals("9")){
					status += "退回";
				}
				
			}
		}else if(!vbillstatus2.equals("1")&&!vbillstatus2.equals("0")){
			if(vbillstatus2 != null){
				if(vbillstatus2.equals("5")){
					status += "门办审核通过";
				}else if(vbillstatus2.equals("6")){
					status += "门办退回";
				}else if(vbillstatus2.equals("4")){
					status += "门办OA提交";
				}
			}
		}else if(!vbillstatus3.equals("1") && !vbillstatus3.equals("0")){
			if(vbillstatus3 != null){
				if(vbillstatus3.equals("4")){
					status += "OA提交";
				}else if(vbillstatus3.equals("8")){
					status += "审核通过";
				}else if(vbillstatus3.equals("9")){
					status += "退回";
				}
			}
		}
			
		return status;
			
	}
	@Override
	protected void onBoEdit() throws Exception {
		// TODO Auto-generated method stub
		int x = MessageDialog.showOkCancelDlg(this.getBillUI(), "提示", "确认按模板数据覆盖当前排班数据?");
		if(x!=UIDialog.ID_OK) return;
		ZhibanTempVO[] vos = (ZhibanTempVO[])HYPubBO_Client.queryByCondition(ZhibanTempVO.class, " isnull(bisstop,'N')='N' and isnull(dr,0)=0 and pk_corp='"+_getCorp().getPrimaryKey()+"' ");
		HashMap<String,ZhibanTempVO> map = new HashMap<String, ZhibanTempVO>();
		if(vos!=null&&vos.length>0){
			for(ZhibanTempVO vo:vos){
				map.put(vo.getPrimaryKey(), vo);
			}
		}
		int rowcount = getBillCardPanelWrapper().getBillCardPanel().getBillModel().getRowCount();
		for(int i=0;i<rowcount;i++){
			String pk_bb = getBillCardPanelWrapper().getBillCardPanel().getBillModel().getValueAt(i, "pk_temp").toString();
			ZhibanTempVO vo = map.get(pk_bb);
			if(vo!=null&& vo.getNxhdays()!=null&& vo.getNxhdays().intValue()>0){
				int xhdays = vo.getNxhdays();
				UFDate begindate = vo.getDbegindate();
				String vdate = getBillCardPanelWrapper().getBillCardPanel().getHeadItem("vdate").getValueObject().toString();
				UFDate begin = new UFDate(vdate.substring(0,10));
				UFDate begincopy = new UFDate(vdate.substring(0,10));
				if(begin.compareTo(begindate)<0) begin = begindate;
				int days = new UFDate().getDaysBetween(begindate, begin)+1;
				int beginindex = days%xhdays;
				if(beginindex==0){
					beginindex = xhdays;
				}
				for(int j=1;j<32;j++){
					if(begincopy.getDateAfter(j-1).compareTo(begindate)<0){
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
	protected void onBoSave() throws Exception {
		// TODO Auto-generated method stub

		ArrayList<SuperVO> listnew = new ArrayList<SuperVO>();
		ArrayList<SuperVO> listupdate = new ArrayList<SuperVO>();
		ArrayList<SuperVO> listdel = new ArrayList<SuperVO>();
		ZhibanWeekVO[] weekvos = (ZhibanWeekVO[])getBillCardPanelWrapper().getBillCardPanel().getBillModel().getBodyValueVOs(ZhibanWeekVO.class.getName());

		try{
			//			Ihrppub impl = (Ihrppub)NCLocator.getInstance().lookup(Ihrppub.class.getName());
			//			impl.saveVOs(listdel,listupdate,listnew);
			String[] pks = ((UIRefPane)getBillCardPanelWrapper().getBillCardPanel().getHeadItem("deptnamekq").getComponent()).getRefPKs();
			ArrayList<String> list = new ArrayList<String>();
			String vdate = getBillCardPanelWrapper().getBillCardPanel().getHeadItem("vdate").getValueObject().toString();
			//			ZhibanWeekVO[] weekvos = (ZhibanWeekVO[])HYPubBO_Client.queryByCondition(ZhibanWeekVO.class,
			//					"isnull(dr,0)=0 and pk_corp='"+_getCorp().getPrimaryKey()+"' and vdate='"+vdate+"' "+HRPPubTool.formInSQL("pk_dept", list)+" ");
			UFDate begin = new UFDate(vdate.substring(0,10));
			UFDate end = new UFDate(vdate.substring(11));
			// 计算下月时间
			UFDate nextenddate = end.getDateAfter(1);
			UFDate nextbegindate = new UFDate();
			int day = 0;
			if(getDay(nextenddate.toString()) != 2){// 不为周一
				if(getDay(nextenddate.toString())==1){ // 但为周天
					day = 8;
				}else{
					day = getDay(nextenddate.toString());
				}
				nextbegindate = new UFDate(nextenddate.toString()).getDateBefore(day - 2);
			}else{
				nextbegindate = end.getDateAfter(1);
			}
			ArrayList<PanbanWeekBVO> list_b = new ArrayList<PanbanWeekBVO>();
			//			HashMap<String,ZhibanTempVO> map = new HashMap<String, ZhibanTempVO>();
			//			ZhibanTempVO[] tvos = (ZhibanTempVO[])HYPubBO_Client.queryByCondition(ZhibanTempVO.class, " isnull(dr,0)=0 and pk_corp='"+_getCorp().getPrimaryKey()+"' ");
			//			if(tvos!=null&&tvos.length>0){
			//				for(ZhibanTempVO vo:tvos){
			//					map.put(vo.getPk_dept(), vo);
			//				}
			//			}
//			 是否自动审批
			UserClassTypeVO[] kqclasstypevos = (UserClassTypeVO[])HYPubBO_Client.queryByCondition(UserClassTypeVO.class, " isnull(dr,0)=0 and pk_user='"+_getOperator()+"' and powertype=0 and  pk_corp='"+_getCorp().getPrimaryKey()+"' and pk_docid='6' ");
			String autoaudit = "N";
			if(kqclasstypevos != null){
				if(kqclasstypevos.length>0){
					autoaudit="Y";
				}
			}
			
			for(ZhibanWeekVO weekvo:weekvos){
				if(weekvo.getPk_temp_old()!=null&&weekvo.getPk_temp_old().trim().length()>0){
					list.add(weekvo.getPk_temp_old());
				}
				
				for(int i=1;i<=31;i++){
					String pk_bbs = weekvo.getAttributeValue("pk_bb"+i+"")!=null?weekvo.getAttributeValue("pk_bb"+i+"").toString().trim():null;
					if(pk_bbs!=null&&pk_bbs.length()>0){
						String[] pk_bbss = pk_bbs.split(",");
						for(String pk_bb:pk_bbss){
							
							UFDate currdate = begin.getDateAfter(i-1);
							UFDate currperiod = begin;
							if(currdate.compareTo(nextbegindate) >= 0 && currdate.compareTo(nextenddate) < 0){
								currperiod = nextenddate;
							}
							PanbanWeekBVO bvo = new PanbanWeekBVO();
							bvo.setPrimaryKey(null);
							bvo.setDr(0);
							bvo.setPk_temp(weekvo.getPk_temp());
							bvo.setPk_psndoc(pk_bb);
							bvo.setPk_deptzb(weekvo.getPk_dept());
							bvo.setDeptzbname(weekvo.getDeptnamekq());
							bvo.setPk_corp(_getCorp().getPrimaryKey());
							bvo.setPk_bb(weekvo.getPk_bb());
							bvo.setBiszb(new UFBoolean(true));
							bvo.setDdate(begin.getDateAfter(i-1));
							if(autoaudit.equals("Y")){
																
								bvo.setVbillstatus3("");
								bvo.setVbillstatus2("");
								bvo.setVbillstatus1("10");
								bvo.setUploadnum(weekvo.getUploadnum());
								
							}else{
								bvo.setVbillstatus3(weekvo.getVbillstatus3());
								bvo.setVbillstatus2(weekvo.getVbillstatus2());
								bvo.setVbillstatus1(weekvo.getVbillstatus1());
								bvo.setUploadnum(weekvo.getUploadnum());
							}
							
							bvo.setDclassperiod(currperiod.toString());
							
							DeptKqBVO[] kqvos = (DeptKqBVO[])HYPubBO_Client.queryByCondition(DeptKqBVO.class,
									" isnull(dr,0)=0 and pk_psndoc='"+pk_bb+"' and dstartdate<='"+bvo.getDdate()+"' and (denddate>='"+bvo.getDdate()+"' or denddate is null ) ");
							if(kqvos!=null&&kqvos.length>0){
								bvo.setPk_dept(kqvos[0].getPk_dept());
							}
							list_b.add(bvo);
						}
					}
				}
			}
			if(list!=null&&list.size()>0){
				HYPubBO_Client.deleteByWhereClause(PanbanWeekBVO.class, " isnull(biszb,'N')='Y' and isnull(dr,0)=0 "+HRPPubTool.formInSQL("pk_temp", list)+" and (ddate>= '"+begin+"' and ddate<='"+end+"') ");
			}
			HYPubBO_Client.insertAry(list_b.toArray(new PanbanWeekBVO[0]));
		}catch(Exception e){
			e.printStackTrace(); 
		}

		((ClientUI)getBillUI()).setEditstate(0);
		getBillCardPanelWrapper().getBillCardPanel().setEnabled(false);
		getBillCardPanelWrapper().getBillCardPanel().getHeadItem("deptnamekq").setEnabled(true);
		getBillCardPanelWrapper().getBillCardPanel().getHeadItem("type").setEnabled(true);
		getButtonManager().getButton(801).setEnabled(true);
		getButtonManager().getButton(802).setEnabled(true);
		getButtonManager().getButton(803).setEnabled(true);
		getButtonManager().getButton(804).setEnabled(true);
		getButtonManager().getButton(IBillButton.Add).setEnabled(true);
		getButtonManager().getButton(IBillButton.Edit).setEnabled(false);
		getButtonManager().getButton(IBillButton.Copy).setEnabled(false);
		getButtonManager().getButton(IBillButton.Save).setEnabled(false);
		getButtonManager().getButton(IBillButton.Cancel).setEnabled(false);
		getBillUI().updateButtons();
		((ClientUI)getBillUI()).setdate();

	}
	
	
	@Override
	protected void onBoCommit() throws Exception {
		// TODO Auto-generated method stub
		//super.onBoCommit();
		String vdate = getBillCardPanelWrapper().getBillCardPanel().getHeadItem("vdate").getValueObject().toString();
		//			ZhibanWeekVO[] weekvos = (ZhibanWeekVO[])HYPubBO_Client.queryByCondition(ZhibanWeekVO.class,
		//					"isnull(dr,0)=0 and pk_corp='"+_getCorp().getPrimaryKey()+"' and vdate='"+vdate+"' "+HRPPubTool.formInSQL("pk_dept", list)+" ");
		UFDate begin = new UFDate(vdate.substring(0,10));
		UFDate end = new UFDate(vdate.substring(11));

		ZhibanWeekVO[] weekvos = (ZhibanWeekVO[])getBillCardPanelWrapper().getBillCardPanel().getBillModel().getBodyValueVOs(ZhibanWeekVO.class.getName());
		ArrayList<String> listupdate = new ArrayList<String>();
		
		for(ZhibanWeekVO weekvo:weekvos){
			String pk_temp = weekvo.getPk_temp();
			String vbillstatus = weekvo.getVbillstatus();
			Integer curnum = 0;
			if(weekvo.getUploadnum()!=null){
				curnum = weekvo.getUploadnum();
			}
			Integer uploadnum = curnum+1;
			if(vbillstatus!= null){
				if(vbillstatus.equals("未提交")){
					String auditmsg = pk_temp+"@"+begin+"@"+end+"@"+uploadnum;
					
					if(!listupdate.contains(auditmsg)){
						listupdate.add(auditmsg);
					}
				}
			}
			
		}
		IBclbDefining defin = NCLocator.getInstance().lookup(IBclbDefining.class);
		defin.onBoZbCommit(listupdate);
		
		getButtonManager().getButton(804).setEnabled(false);
		MessageDialog.showHintDlg(this.getBillUI(), "提示", begin + "至"+ end +"提交完成");
		getBillUI().showHintMessage(begin + "至"+ end +"提交完成!");
	}
	@Override
	protected void onBoCancel() throws Exception {
		// TODO Auto-generated method stub
		((ClientUI)getBillUI()).setEditstate(0);
		getBillCardPanelWrapper().getBillCardPanel().setEnabled(false);
		getBillCardPanelWrapper().getBillCardPanel().getHeadItem("deptnamekq").setEnabled(true);
		getBillCardPanelWrapper().getBillCardPanel().getHeadItem("type").setEnabled(true);
		getButtonManager().getButton(801).setEnabled(true);
		getButtonManager().getButton(802).setEnabled(true);
		getButtonManager().getButton(803).setEnabled(true);
		getButtonManager().getButton(IBillButton.Add).setEnabled(true);
		getButtonManager().getButton(IBillButton.Edit).setEnabled(false);
		getButtonManager().getButton(IBillButton.Copy).setEnabled(false);
		getButtonManager().getButton(IBillButton.Save).setEnabled(false);
		getButtonManager().getButton(IBillButton.Cancel).setEnabled(false);
		getBillUI().updateButtons();
		((ClientUI)getBillUI()).setdate();

	}
	@Override
	protected void onBoCopy() throws Exception {
		// TODO Auto-generated method stub
		int x = MessageDialog.showOkCancelDlg(this.getBillUI(), "提示", "确认按上周数据当前排班数据?");
		if(x!=UIDialog.ID_OK) return;
		String vdate =  getBillCardPanelWrapper().getBillCardPanel().getHeadItem("vdate").getValueObject().toString();
		vdate =  vdate.substring(0,10);
		UFDate begin = new UFDate(vdate).getDateBefore(7);
		UFDate end = new UFDate(vdate).getDateBefore(1);
		String[] pks = ((UIRefPane)getBillCardPanelWrapper().getBillCardPanel().getHeadItem("deptnamekq").getComponent()).getRefPKs();
		ArrayList<String> list = new ArrayList<String>();
		list.addAll(Arrays.asList(pks));
		vdate = begin+"至"+end;
		ZhibanWeekVO[] weekvos = (ZhibanWeekVO[])HYPubBO_Client.queryByCondition(ZhibanWeekVO.class,
				"isnull(dr,0)=0 and pk_corp='"+_getCorp().getPrimaryKey()+"' and vdate='"+vdate+"' "+HRPPubTool.formInSQL("pk_dept", list)+" ");
		if(weekvos!=null&&weekvos.length>0){
			HashMap<String, ZhibanWeekVO> map = new HashMap<String, ZhibanWeekVO>();
			for(ZhibanWeekVO vo:weekvos){
				map.put(vo.getPk_dept(), vo);
			}
			int rowcount = getBillCardPanelWrapper().getBillCardPanel().getBillModel().getRowCount();
			for(int i=0;i<rowcount;i++){
				String pk_dept = getBillCardPanelWrapper().getBillCardPanel().getBillModel().getValueAt(i, "pk_dept").toString();
				ZhibanWeekVO vo = map.get(pk_dept);
				if(vo!=null){
					for(int j=1;j<8;j++){
						if(getBillCardPanelWrapper().getBillCardPanel().getBillModel().getItemByKey("vbbname"+j+"").isEnabled()){
							getBillCardPanelWrapper().getBillCardPanel().getBillModel().setValueAt(vo.getAttributeValue("vbbname"+j+""), i, "vbbname"+j+"");
							getBillCardPanelWrapper().getBillCardPanel().getBillModel().setValueAt(vo.getAttributeValue("pk_bb"+j+""), i, "pk_bb"+j+"");
						}
					}
				}
			}
		}

	}

	@Override
	protected void onBoElse(int intBtn) throws Exception {
		// TODO Auto-generated method stub
		String type = getBillCardPanelWrapper().getBillCardPanel().getHeadItem("type").getValueObject().toString();

		switch (intBtn) {
		case 801:
			if(type.equals("按周")){
				String vdate =  getBillCardPanelWrapper().getBillCardPanel().getHeadItem("vdate").getValueObject().toString();
				vdate =  vdate.substring(11);
				UFDate begin = new UFDate(vdate).getDateAfter(1);
				UFDate end = new UFDate(vdate).getDateAfter(7);
				getBillCardPanelWrapper().getBillCardPanel().getHeadItem("vdate").setValue(begin+"至"+end);
			}else{
				String vdate =  getBillCardPanelWrapper().getBillCardPanel().getHeadItem("vdate").getValueObject().toString();
				vdate =  vdate.substring(11);
				UFDate begin = new UFDate(vdate).getDateAfter(1);
				UFDate end = new UFDate(vdate).getDateAfter(begin.getDaysMonth());
				getBillCardPanelWrapper().getBillCardPanel().getHeadItem("vdate").setValue(begin+"至"+end);
			}
			((ClientUI)getBillUI()).setdate();
			((ClientUI)getBillUI()).updateButtons();
			break;
		case 802:
			if(type.equals("按周")){
				String vdate =  getBillCardPanelWrapper().getBillCardPanel().getHeadItem("vdate").getValueObject().toString();
				vdate =  vdate.substring(0,10);
				UFDate begin = new UFDate(vdate).getDateBefore(7);
				UFDate end = new UFDate(vdate).getDateBefore(1);
				getBillCardPanelWrapper().getBillCardPanel().getHeadItem("vdate").setValue(begin+"至"+end);
			}else{
				String vdate =  getBillCardPanelWrapper().getBillCardPanel().getHeadItem("vdate").getValueObject().toString();
				vdate =  vdate.substring(0,10);
				UFDate end = new UFDate(vdate).getDateBefore(1);
				UFDate begin = new UFDate(vdate).getDateBefore(end.getDaysMonth());
				getBillCardPanelWrapper().getBillCardPanel().getHeadItem("vdate").setValue(begin+"至"+end);
			}

			((ClientUI)getBillUI()).setdate();
			((ClientUI)getBillUI()).updateButtons();
			break;
		case 803:
			if(type.equals("按周")){
				int x = _getDate().getWeek();
				UFDate begin = _getDate().getDateBefore(x-1);
				UFDate end = _getDate().getDateAfter(7-x);
				getBillCardPanelWrapper().getBillCardPanel().getHeadItem("vdate").setValue(begin+"至"+end);
			}else{
				UFDate begin = new UFDate(_getDate().toString().substring(0,8)+"01");
				UFDate end = new UFDate(_getDate().toString().substring(0,8)+""+begin.getDaysMonth()+"");
				getBillCardPanelWrapper().getBillCardPanel().getHeadItem("vdate").setValue(begin+"至"+end);
			}
			((ClientUI)getBillUI()).setdate();
			((ClientUI)getBillUI()).updateButtons();
			break;
		case 804:
			onBoCommit();
			((ClientUI)getBillUI()).setdate();
			((ClientUI)getBillUI()).updateButtons();
			break;
		default:
			break;
		}
	}
	
	public int getDay(String str) throws ParseException{
		SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd");
		Date date =sdf.parse(str);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		// 第几周
		int week = calendar.get(Calendar.WEEK_OF_MONTH);
		// 第几天，从周日开始
		int day = calendar.get(Calendar.DAY_OF_WEEK);
		return day;
	}
}
