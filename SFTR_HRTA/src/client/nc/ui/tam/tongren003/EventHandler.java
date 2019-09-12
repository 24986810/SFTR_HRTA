/**
 * 
 */
package nc.ui.tam.tongren003;

import java.awt.Color;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Vector;

import nc.bs.framework.common.NCLocator;
import nc.itf.hr.ta.IBclbDefining;
import nc.itf.hrp.pub.HRPPubTool;
import nc.itf.hrp.pub.Ihrppub;
import nc.itf.uap.IUAPQueryBS;
import nc.itf.uap.bd.def.IDefdoc;
import nc.itf.uap.busibean.ISysInitQry;
import nc.jdbc.framework.processor.VectorProcessor;
import nc.ui.pub.ButtonObject;
import nc.ui.pub.ClientEnvironment;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UIDialog;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.bill.BillItem;
import nc.ui.trade.bill.ICardController;
import nc.ui.trade.business.HYPubBO_Client;
import nc.ui.trade.button.IBillButton;
import nc.ui.trade.card.BillCardUI;
import nc.ui.trade.card.CardEventHandler;
import nc.uif.pub.exception.UifException;
import nc.vo.bd.def.DefdocVO;
import nc.vo.hr.para2.ParValueVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDouble;
import nc.vo.tam.tongren.power.UserClassTypeVO;
import nc.vo.tam.tongren001.DeptKqBVO;
import nc.vo.tam.tongren001.DeptKqVO;
import nc.vo.tam.tongren002.PaibanTempVO;
import nc.vo.tam.tongren003.PaiBanAuditMsg;
import nc.vo.tam.tongren003.PaiBanAuditMsg2;
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
	private ArrayList<String> list_zb = new ArrayList<String>();
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
		
		String vdate = getBillCardPanelWrapper().getBillCardPanel().getHeadItem("vdate").getValueObject().toString();
		
		HashMap<String,String> vbillstatusMap = new HashMap<String, String>();  
		vbillstatusMap.put("0", "未提交");
		vbillstatusMap.put("1", "提交");
		vbillstatusMap.put("2", "医务审核通过");
		vbillstatusMap.put("3", "医务退回");
		vbillstatusMap.put("4", "OA提交");
		vbillstatusMap.put("5", "门办审核通过");
		vbillstatusMap.put("6", "门办退回");
		vbillstatusMap.put("7", "医务门办审核通过");
		vbillstatusMap.put("8", "审核通过");
		vbillstatusMap.put("9", "退回");
		vbillstatusMap.put("10", "审核通过");
		
		PaibanWeekVO[] weekvos = (PaibanWeekVO[])HYPubBO_Client.queryByCondition(PaibanWeekVO.class,
				"isnull(dr,0)=0 and pk_corp='"+_getCorp().getPrimaryKey()+"' and vdate='"+vdate+"' "+HRPPubTool.formInSQL("pk_dept", list)+" ");
		
		if(weekvos!=null&&weekvos.length>0){
			if("6".equals(weekvos[0].getVbillstatus2())||"3".equals(weekvos[0].getVbillstatus1())||("0".equals(weekvos[0].getVbillstatus3()) && "0".equals(weekvos[0].getVbillstatus2()) && "0".equals(weekvos[0].getVbillstatus1()))||"9".equals(weekvos[0].getVbillstatus3())||weekvos[0].getVbillstatus1()==null ||weekvos[0].getVbillstatus2()==null||weekvos[0].getVbillstatus3()==null){
				
			}else{
				/*String vbillstatusNum =null;
				if(weekvos[0].getVbillstatus1()!=null){
					 vbillstatusNum = weekvos[0].getVbillstatus1();
				}else if(weekvos[0].getVbillstatus2()!=null){
					 vbillstatusNum = weekvos[0].getVbillstatus2();
				}else if(weekvos[0].getVbillstatus3()!=null){
					 vbillstatusNum = weekvos[0].getVbillstatus3();
				}*/
				
				String status = getStatus(weekvos[0].getVbillstatus1(),weekvos[0].getVbillstatus2(),weekvos[0].getVbillstatus3());
				 MessageDialog.showErrorDlg(null, "提示",status+",不能继续修改，请前往个人调班申请。");
				 return;
			}
		}
		
		
//		 zhanghua
		String begindate1 = getBillCardPanelWrapper().getBillCardPanel().getHeadItem("begindate").getValueObject().toString();
		String enddate1 = getBillCardPanelWrapper().getBillCardPanel().getHeadItem("enddate").getValueObject().toString();
		
		PaiBanAuditMsg[] msgvos = 
			(PaiBanAuditMsg[]) HYPubBO_Client.queryByCondition(PaiBanAuditMsg.class,
					" isnull(dr,0)=0 and vbillstatus=1"+HRPPubTool.formInSQL("pk_dept", list)+" and ddate >='"+begindate1+"' and ddate <='"+enddate1+"'");
		ArrayList<String> list_audit = new ArrayList<String>();
		if(msgvos!=null&&msgvos.length>0){
			for(PaiBanAuditMsg msgvo:msgvos){
				list_audit.add(msgvo.getPk_dept()+msgvo.getVperiod());
			}
		}
		ParValueVO[] valuevos = (ParValueVO[])HYPubBO_Client.queryByCondition(ParValueVO.class, " isnull(dr,0)=0 and pk_corp='"+_getCorp().getPrimaryKey()+"' and  PAR_CODE='BPB' ");
		int day = 1;
		if(valuevos!=null&&valuevos.length>0){
			day = Integer.parseInt( valuevos[0].getPar_value());
		}
		UFDate curdate = nc.ui.hr.global.Global.getServerTime().getDate();
		UFDate enableStartdate = curdate.getDateBefore(day);//可以编辑的最早的一天
		
		UFDate enddate = new UFDate(vdate.substring(11));
		if(enddate.compareTo(enableStartdate)<0){
			MessageDialog.showHintDlg(this.getBillUI(), "提示", "当前周日期已不能再排班");
			return;
		}
		((ClientUI)getBillUI()).setEditstate(1);
		getBillCardPanelWrapper().getBillCardPanel().getBillModel().clearBodyData();
		//		if(enddate.before(curdate)){//不能给之前日期排班，只能之后的
		//			int x = curdate.getWeek();
		//			UFDate begin = curdate.getDateBefore(x-1);
		//			UFDate end = curdate.getDateAfter(7-x);
		//			getBillCardPanelWrapper().getBillCardPanel().getHeadItem("vdate").setValue(begin+"至"+end);
		//		}
		getButtonManager().getButton(IBillButton.Add).setEnabled(false);
		getButtonManager().getButton(801).setEnabled(false);
		getButtonManager().getButton(802).setEnabled(false);
		getButtonManager().getButton(803).setEnabled(false);
		getButtonManager().getButton(804).setEnabled(false);
		getButtonManager().getButton(805).setEnabled(false);
		getButtonManager().getButton(806).setEnabled(false);
		getButtonManager().getButton(807).setEnabled(false);
		getButtonManager().getButton(808).setEnabled(false);
		getButtonManager().getButton(IBillButton.Edit).setEnabled(true);
		getButtonManager().getButton(IBillButton.Copy).setEnabled(true);
		getButtonManager().getButton(IBillButton.Save).setEnabled(true);
		getButtonManager().getButton(IBillButton.Cancel).setEnabled(true);
		getBillUI().updateButtons();
		getBillCardPanelWrapper().getBillCardPanel().setEnabled(true);
		getBillCardPanelWrapper().getBillCardPanel().getHeadItem("deptnamekq").setEnabled(false);
		
		getBillCardPanelWrapper().getBillCardPanel().getHeadItem("begindate").setEnabled(false);
		getBillCardPanelWrapper().getBillCardPanel().getHeadItem("enddate").setEnabled(false);


		vdate = getBillCardPanelWrapper().getBillCardPanel().getHeadItem("vdate").getValueObject().toString();
		
		UFDate begin = new UFDate(begindate1);
		UFDate end = new UFDate(enddate1);
		
		if(getDay(begindate1) != 2){
			MessageDialog.showErrorDlg(null, "提示","排班只能从星期一开始.");
			 return;
		}
//		UFDate begin = new UFDate(vdate.substring(0,10));
//		UFDate end = new UFDate(vdate.substring(11));
		ArrayList<String> pk_list_psn = new ArrayList<String>();
		GxBVO[] gxvos = (GxBVO[])HYPubBO_Client.queryByCondition(GxBVO.class, " isnull(dr,0)=0 and vyear='"+begin.getYear()+"' and pk_corp='"+_getCorp().getPrimaryKey()+"' ");
		HashMap<String,UFDouble> map = new HashMap<String, UFDouble>();
		if(gxvos!=null&&gxvos.length>0){
			for(GxBVO gxvo:gxvos){
				map.put(gxvo.getPk_psndoc(), gxvo.getNsxgx());
			}
		}
		try {
			
			ArrayList<PaibanWeekVO> list_vo = new ArrayList<PaibanWeekVO>();
			if(weekvos!=null&&weekvos.length>0){
				for(PaibanWeekVO weekvo:weekvos){
					pk_list_psn.add(weekvo.getPk_psndoc());
					weekvo.setNgxs(map.get(weekvo.getPk_psndoc()));
					weekvo.setNsygxs(map.get(weekvo.getPk_psndoc()));
					
					
					list_vo.add(weekvo);
				}
			}
			DeptKqBVO[] bvos = (DeptKqBVO[])HYPubBO_Client.queryByCondition(DeptKqBVO.class,
					" isnull(dr,0)=0 "+HRPPubTool.formInSQL("pk_dept", list)+"  and (denddate is null or denddate>='"+begin+"') and dstartdate<='"+end+"' ");
			if(bvos!=null&&bvos.length>0){
				for(DeptKqBVO bvo:bvos){
					if(!pk_list_psn.contains(bvo.getPk_psndoc())){
						PaibanWeekVO weekvo = new PaibanWeekVO();
						weekvo.setDr(0);
						weekvo.setPk_psndoc(bvo.getPk_psndoc());
						weekvo.setPk_dept(bvo.getPk_dept());
						weekvo.setVdate(vdate);
						weekvo.setPk_corp(_getCorp().getPrimaryKey());
						weekvo.setNgxs(map.get(bvo.getPk_psndoc()));
						weekvo.setNsygxs(map.get(bvo.getPk_psndoc()));
						
						
						list_vo.add(weekvo);
						pk_list_psn.add(bvo.getPk_psndoc());
					}
				}
			}
			if(list_vo!=null&&list_vo.size()>0){
				getBillCardPanelWrapper().getBillCardPanel().getBillModel().setBodyDataVO(list_vo.toArray(new PaibanWeekVO[0]));
				getBillCardPanelWrapper().getBillCardPanel().getBillModel().execLoadFormula();
			}
		} catch (UifException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		int rowcount = getBillCardPanelWrapper().getBillCardPanel().getBillModel().getRowCount();
		String sql = "";
		if(pk_list_psn!=null&&pk_list_psn.size()>0){
			sql = " isnull(dr,0)=0 and pk_corp='"+_getCorp().getPrimaryKey()+"' and isnull(biszb,'N')='Y' "+HRPPubTool.formInSQL("pk_psndoc", pk_list_psn)+" and ddate<='"+end+"' and ddate>='"+begin+"' ";
		}else{
			sql = "1=2 ";
		}

		PanbanWeekBVO[] panbvos = (PanbanWeekBVO[])HYPubBO_Client.queryByCondition(PanbanWeekBVO.class,sql);
//		ArrayList<String> list_zb = new ArrayList<String>();
		if(panbvos!=null&&panbvos.length>0){
			for(PanbanWeekBVO panbvo:panbvos){
				list_zb.add(panbvo.getPk_psndoc().trim()+panbvo.getDdate().toString().trim());
			}
		}


		for(int i=1;i<8;i++){
			String name = getBillCardPanelWrapper().getBillCardPanel().getBillModel().getItemByKey("vbbname"+(i)+"").getName().substring(0,3);
			UFDate showdate = begin.getDateAfter(i-1);
			getBillCardPanelWrapper().getBillCardPanel().getBillModel().getItemByKey("vbbname"+(i)+"").setName(name+"("+showdate+")");
			if(showdate.compareTo(enableStartdate)>=0){
				getBillCardPanelWrapper().getBillCardPanel().getBillModel().getItemByKey("vbbname"+(i)+"").setEdit(true);
				int index = getBillCardPanelWrapper().getBillCardPanel().getBillModel().getBodyColByKey("vbbname"+(i)+"");
				for(int j=0;j<rowcount;j++){
					getBillCardPanelWrapper().getBillCardPanel().getBillModel().setBackground(Color.cyan, j, index);
					String pk_psndoc = getBillCardPanelWrapper().getBillCardPanel().getBillModel().getValueAt(j, "pk_psndoc").toString().trim();
					if(list_zb.contains(pk_psndoc+showdate)){
						getBillCardPanelWrapper().getBillCardPanel().getBillModel().setBackground(Color.pink, j, index);
					}
				}
			}else{
				getBillCardPanelWrapper().getBillCardPanel().getBillModel().getItemByKey("vbbname"+(i)+"").setEdit(false);
				int index = getBillCardPanelWrapper().getBillCardPanel().getBillModel().getBodyColByKey("vbbname"+(i)+"");
				for(int j=0;j<rowcount;j++){
					getBillCardPanelWrapper().getBillCardPanel().getBillModel().setBackground(Color.LIGHT_GRAY, j, index);
					String pk_psndoc = getBillCardPanelWrapper().getBillCardPanel().getBillModel().getValueAt(j, "pk_psndoc").toString().trim();
					if(list_zb.contains(pk_psndoc+showdate)){
						getBillCardPanelWrapper().getBillCardPanel().getBillModel().setBackground(Color.pink, j, index);
					}
				}
			}
		}
		for(int i=0;i<rowcount;i++){
			String pk_psndoc = getBillCardPanelWrapper().getBillCardPanel().getBillModel().getValueAt(i, "pk_psndoc").toString().trim();
			String pk_dept = getBillCardPanelWrapper().getBillCardPanel().getBillModel().getValueAt(i, "pk_dept").toString().trim();
			DeptKqBVO[] bvos = (DeptKqBVO[])HYPubBO_Client.queryByCondition(DeptKqBVO.class,
					" isnull(dr,0)=0 and pk_psndoc='"+pk_psndoc+"' and pk_dept='"+pk_dept+"'  and (denddate is null or denddate>='"+begin+"') and dstartdate<='"+end+"' order by dstartdate desc ");
			if(bvos==null||bvos.length<=0){
				for(int j=1;j<8;j++){
					UFDate showdate = begin.getDateAfter(j-1);
					int index = getBillCardPanelWrapper().getBillCardPanel().getBillModel().getBodyColByKey("vbbname"+(j)+"");
					getBillCardPanelWrapper().getBillCardPanel().getBillModel().setCellEditable(i, "vbbname"+(j)+"", false);
					getBillCardPanelWrapper().getBillCardPanel().getBillModel().setValueAt(null, i, "vbbname"+(j)+"");
					getBillCardPanelWrapper().getBillCardPanel().getBillModel().setValueAt(null, i, "pk_bb"+(j)+"");
					getBillCardPanelWrapper().getBillCardPanel().getBillModel().setBackground(Color.LIGHT_GRAY, i, index);
					if(list_zb.contains(pk_psndoc+showdate)){
						getBillCardPanelWrapper().getBillCardPanel().getBillModel().setBackground(Color.pink, i, index);
					}
				}
			}else{
				for(int j=1;j<8;j++){
					UFDate showdate = begin.getDateAfter(j-1);
					if(list_audit.contains(pk_dept+showdate.toString().substring(0,7))){
						int index = getBillCardPanelWrapper().getBillCardPanel().getBillModel().getBodyColByKey("vbbname"+(j)+"");
						getBillCardPanelWrapper().getBillCardPanel().getBillModel().setCellEditable(i, "vbbname"+(j)+"", false);
						getBillCardPanelWrapper().getBillCardPanel().getBillModel().setBackground(Color.LIGHT_GRAY, i, index);
						if(list_zb.contains(pk_psndoc+showdate)){
							getBillCardPanelWrapper().getBillCardPanel().getBillModel().setBackground(Color.pink, i, index);
						}
					}else{
						if(bvos[0].getDstartdate().compareTo(showdate)>0||(bvos[0].getDenddate()!=null&&bvos[0].getDenddate().compareTo(showdate)<0)){
							int index = getBillCardPanelWrapper().getBillCardPanel().getBillModel().getBodyColByKey("vbbname"+(j)+"");
							getBillCardPanelWrapper().getBillCardPanel().getBillModel().setCellEditable(i, "vbbname"+(j)+"", false);
							getBillCardPanelWrapper().getBillCardPanel().getBillModel().setBackground(Color.LIGHT_GRAY, i, index);
							if(list_zb.contains(pk_psndoc+showdate)){
								getBillCardPanelWrapper().getBillCardPanel().getBillModel().setBackground(Color.pink, i, index);
							}
						}
					}
				}
			}
		}
		getBillCardPanelWrapper().getBillCardPanel().setBillData(getBillCardPanelWrapper().getBillCardPanel().getBillData());
		getBillCardPanelWrapper().getBillCardPanel().getBillTable().setHeaderHeight(30);
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
	protected void onBoSave() throws Exception {
		// TODO Auto-generated method stub
//		UFDate curdate = new UFDate("2018-12-7");
//		int x = curdate.getWeek();
//		UFDate begin1 = curdate.getDateBefore(x-1);
//		UFDate end1 = curdate.getDateAfter(7-x);
//		int week = getWeek("2018-12-7");
		ArrayList<SuperVO> listnew = new ArrayList<SuperVO>();
		ArrayList<SuperVO> listupdate = new ArrayList<SuperVO>();
		ArrayList<SuperVO> listdel = new ArrayList<SuperVO>();
		PaibanWeekVO[] vos = (PaibanWeekVO[])getBillCardPanelWrapper().getBillCardPanel().getBillModel().getBodyValueVOs(PaibanWeekVO.class.getName());
		// 保存时判断公休是否为<0,则不能保存
		
		
		StringBuffer errorMsg = new StringBuffer();   //错误提示信息
		/*for(int i=0;i<vos.length;i++){
			PaibanWeekVO paibanweekvo = vos[i];
			UFDouble nsygxs = new UFDouble("0.00");
			String pk_vbb = "10028L100000000002D8";// 公休PK
			for(int j=1;j<8;j++){
				String pk_bbs = paibanweekvo.getAttributeValue("pk_bb"+j) != null ? paibanweekvo.getAttributeValue("pk_bb"+j).toString().trim():null;
				if(pk_vbb.equals(pk_bbs)){
					nsygxs=nsygxs.add(new UFDouble("1.00"));
				}
				
			}
			
			if(paibanweekvo.getNsygxs() != null){
				if(paibanweekvo.getNsygxs().compareTo(nsygxs) == -1){
					String psnname = (String) getBillCardPanelWrapper().getBillCardPanel().getBodyValueAt(i, "psnname");
					errorMsg.append("第 "+ (i+1) +"行,"+psnname+"公休数小于0！\n");
					continue;
				}
			}
			
		}*/
		
		/*if(errorMsg.length() != 0 || !"".equals(errorMsg.toString())){
			 MessageDialog.showErrorDlg(null, "提示","保存失败："+errorMsg);
			 return;
		}*/
		// end
		// 保存时本月最后一周时，校验有没有漏排班
		String vdate1 = getBillCardPanelWrapper().getBillCardPanel().getHeadItem("vdate").getValueObject().toString();
		String begindate = vdate1.substring(0, 10);
		
//		if(getWeek(begindate) == 5){
//			UFDate begin4 = new UFDate(begindate);
//			UFDate begin3 = begin4.getDateBefore(7);
//			UFDate end3 = begin3.getDateAfter(6);
//			UFDate begin2 = begin3.getDateBefore(7);
//			UFDate end2 = begin2.getDateAfter(6);
//			UFDate begin1 = begin2.getDateBefore(7);
//			UFDate end1 = begin1.getDateAfter(6);
//			
//			String vdate11 = begin1.toString()+"至"+end1.toString();
//			String vdate12 = begin2.toString()+"至"+end2.toString();
//			String vdate13 = begin3.toString()+"至"+end3.toString();
//			
//			String[] pks = ((UIRefPane)getBillCardPanelWrapper().getBillCardPanel().getHeadItem("deptnamekq").getComponent()).getRefPKs();
//			ArrayList<String> list = new ArrayList<String>();
//			list.addAll(Arrays.asList(pks));
//			
//			String sqlwhere = "vdate in ('" +vdate11+"','"+vdate12+"','"+vdate13+"') "+
//				"and (pk_bb1 is null or pk_bb2 is null or pk_bb3 is null or pk_bb4 is null or pk_bb5 is null or pk_bb6 is null or pk_bb7 is null)"+
//				HRPPubTool.formInSQL("pk_dept", list);
//			
//			PaibanWeek[] lastpaibanweeks = getPaibanWeek(sqlwhere);
//			if(lastpaibanweeks != null){
//				for(int i=0;i<lastpaibanweeks.length;i++){
//					errorMsg.append(lastpaibanweeks[i].getVdate()+","+lastpaibanweeks[i].getPsnname()+"漏排班\n");
//				}
//			}
			
			// 
		String begin = getBillCardPanelWrapper().getBillCardPanel().getHeadItem("begindate").getValueObject().toString();
		String end = getBillCardPanelWrapper().getBillCardPanel().getHeadItem("enddate").getValueObject().toString();
		String voperatorid = getBillCardPanelWrapper().getBillCardPanel().getHeadItem("voperatorid").getValueObject().toString();
		
	/*	UserClassTypeVO[] kqclasstypevos = (UserClassTypeVO[])HYPubBO_Client.queryByCondition(UserClassTypeVO.class, " isnull(dr,0)=0 and pk_user='"+_getOperator()+"' and powertype=0 and  pk_corp='"+_getCorp().getPrimaryKey()+"' and pk_docid='6' ");
		   String autoaudit = "N";
		   if(kqclasstypevos != null){
		    if(kqclasstypevos.length>0){
		     autoaudit="Y";
		    }
		   }*/
		
		for(int i=0;i<vos.length;i++){
				PaibanWeekVO paibanweekvo = vos[i];
				vos[i].setVoperatorid(voperatorid);
				/*if("Y".equals(autoaudit)){
					//vos[i].setVbillstatus2(new String("5"));
					vos[i].setVbillstatus1(new String("10"));
					vos[i].setVbillstatus3(null);
				}else{*/
					vos[i].setVbillstatus3(new String("0")); 
					vos[i].setVbillstatus1(new String("0")); 
					vos[i].setVbillstatus2(new String("0")); 
				//}
				/*int nullnum = 0;
				for(int j=1;j<8;j++){
					String pk_bbs = paibanweekvo.getAttributeValue("pk_bb"+j) != null ? paibanweekvo.getAttributeValue("pk_bb"+j).toString().trim():null;
					
					BillItem items = getBillCardPanelWrapper().getBillCardPanel().getBillModel().getItemByKey("vbbname"+j);
					String vddate = items.getName().substring(0, 3);	
					if("".equals(pk_bbs) || pk_bbs == null){
						
						String pk_psndoc = paibanweekvo.getAttributeValue("pk_psndoc").toString();
						String pk_dept = paibanweekvo.getAttributeValue("pk_dept").toString().trim();
											
						UFDate showdate = new UFDate(begin).getDateAfter(j-1);
						// 是否有排班
						DeptKqBVO[] bvos = (DeptKqBVO[])HYPubBO_Client.queryByCondition(DeptKqBVO.class,
								" isnull(dr,0)=0 and pk_psndoc='"+pk_psndoc+"' and pk_dept='"+pk_dept+"'  and (denddate is null or denddate>='"+begin+"') and dstartdate<='"+end+"' order by dstartdate desc ");
						if(bvos !=null){
							if(bvos.length >0){
							
									if(!list_zb.contains(pk_psndoc+showdate)){// 不是值班
										
										// 排班时间在开始结束区间内
										if(bvos[0].getDenddate() != null){
											if(bvos[0].getDstartdate().compareTo(showdate)<=0
													&& bvos[0].getDenddate().compareTo(showdate)>=0){
												nullnum = nullnum + 1;
											}
										}else{
											// 未结束不判断结束日期
											if(bvos[0].getDstartdate().compareTo(showdate)<=0){
												nullnum = nullnum + 1;
											}
										}
										
									}
							}
						}
						
						
					}else{
						nullnum = 0;
					}
					
					if(nullnum != 0){
						String psnname = (String) getBillCardPanelWrapper().getBillCardPanel().getBodyValueAt(i, "psnname");
						errorMsg.append( "第 "+ (i+1) +"行,"+psnname+" "+vddate+" 漏排班！\n");
						continue;
					}
				}*/
			}
			
			/*if(errorMsg.length() != 0 || !"".equals(errorMsg.toString())){
				 MessageDialog.showErrorDlg(null, "提示","保存失败："+errorMsg);
				 return;
			}*/
			
//		}
		
		for(PaibanWeekVO vo:vos){
			if(vo.getPrimaryKey()!=null&&vo.getPrimaryKey().trim().length()>0){
				listupdate.add(vo);
			}else{
				listnew.add(vo);
			}
		}
		try{
			Ihrppub impl = (Ihrppub)NCLocator.getInstance().lookup(Ihrppub.class.getName());
			impl.saveVOs(listdel,listupdate,listnew);
			
			String[] pks = ((UIRefPane)getBillCardPanelWrapper().getBillCardPanel().getHeadItem("deptnamekq").getComponent()).getRefPKs();
			ArrayList<String> list = new ArrayList<String>();
			list.addAll(Arrays.asList(pks));
			String vdate = getBillCardPanelWrapper().getBillCardPanel().getHeadItem("vdate").getValueObject().toString();
			PaibanWeekVO[] weekvos = (PaibanWeekVO[])HYPubBO_Client.queryByCondition(PaibanWeekVO.class,
					"isnull(dr,0)=0 and pk_corp='"+_getCorp().getPrimaryKey()+"' and vdate='"+vdate+"' "+HRPPubTool.formInSQL("pk_dept", list)+" ");
			UFDate begin1 = new UFDate(vdate.substring(0,10));
			UFDate end1 = new UFDate(vdate.substring(11));
			ArrayList<PanbanWeekBVO> list_b = new ArrayList<PanbanWeekBVO>();
			HashMap<String,PaibanTempVO> map = new HashMap<String, PaibanTempVO>();
			PaibanTempVO[] tvos = (PaibanTempVO[])HYPubBO_Client.queryByCondition(PaibanTempVO.class, " isnull(dr,0)=0 and pk_corp='"+_getCorp().getPrimaryKey()+"' ");
			if(vos!=null&&vos.length>0){
				for(PaibanTempVO vo:tvos){
					map.put(vo.getPk_psndoc(), vo);
				}
			}
			ArrayList<String> list_psn = new ArrayList<String>();
			for(PaibanWeekVO weekvo:weekvos){
				for(int i=1;i<8;i++){
					String pk_bbs = weekvo.getAttributeValue("pk_bb"+i+"")!=null?weekvo.getAttributeValue("pk_bb"+i+"").toString().trim():null;
					if(pk_bbs!=null&&pk_bbs.length()>0){
						String[] pk_bbss = pk_bbs.split(",");
						for(String pk_bb:pk_bbss){
							PanbanWeekBVO bvo = new PanbanWeekBVO();
							bvo.setPrimaryKey(null);
							bvo.setDr(0);
							bvo.setPk_corp(_getCorp().getPrimaryKey());
							bvo.setPk_psndoc(weekvo.getPk_psndoc());
							bvo.setPk_dept(weekvo.getPk_dept());
							bvo.setPk_paiban(weekvo.getPrimaryKey());
							bvo.setPk_bb(pk_bb);
							bvo.setBiszb(new UFBoolean(false));
							bvo.setDdate(begin1.getDateAfter(i-1));
							bvo.setMemo(weekvo.getMemo());
							list_b.add(bvo);
						}
					}
				}
				PaibanTempVO tempvo = map.get(weekvo.getPk_psndoc());
				if(tempvo!=null){
					tempvo.setNnowdays(weekvo.getNnowdays());
				}
				list_psn.add(weekvo.getPk_psndoc());
			}

			//			HYPubBO_Client.updateAry(map.values().toArray(new PaibanTempVO[0]));
			HYPubBO_Client.deleteByWhereClause(PanbanWeekBVO.class, " isnull(biszb,'N')='N' and isnull(dr,0)=0 "+HRPPubTool.formInSQL("pk_dept", list)+" and (ddate>= '"+begin+"' and ddate<='"+end+"') ");
			HYPubBO_Client.insertAry(list_b.toArray(new PanbanWeekBVO[list_b.size()]));
			PanbanWeekBVO[] bvos =(PanbanWeekBVO[]) HYPubBO_Client.queryByCondition(PanbanWeekBVO.class, " isnull(dr,0)=0 "+HRPPubTool.formInSQL("pk_psndoc", list_psn)+" and pk_bb in ('10028L100000000002D8','10028L100000000002XL') and ddate like '"+begin1.getYear()+"%' ");
			HashMap<String,UFDouble> map_gxs = new HashMap<String, UFDouble>();
			if(bvos!=null&&bvos.length>0){
				for(PanbanWeekBVO bvo:bvos){
					String pk_bb = bvo.getPk_bb();
					UFDouble gxs = map_gxs.get(bvo.getPk_psndoc())!=null?map_gxs.get(bvo.getPk_psndoc()):new UFDouble(0);
					if(pk_bb.equals("10028L100000000002D8")){
						gxs = gxs.add(1);
					}else if(pk_bb.equals("10028L100000000002XL")){
						gxs = gxs.add(0.5);
					}
					map_gxs.put(bvo.getPk_psndoc(), gxs);
				}
				GxBVO[] gxvos = (GxBVO[])HYPubBO_Client.queryByCondition(GxBVO.class,
						" isnull(dr,0)=0 and vyear='"+new UFDate(begin).getYear()+"' and pk_corp='"+_getCorp().getPrimaryKey()+"' "+HRPPubTool.formInSQL("pk_psndoc", list_psn)+" ");
				if(gxvos!=null&&gxvos.length>0){
					for(GxBVO gxvo:gxvos){
						gxvo.setNlastsygx(gxvo.getNsxgx());
						gxvo.setNlastyygx(gxvo.getNyygx());
						gxvo.setNyygx(map_gxs.get(gxvo.getPk_psndoc()));
						gxvo.setNsxgx((gxvo.getNsfgx()!=null?gxvo.getNsfgx():new UFDouble(0)).sub(gxvo.getNyygx()!=null?gxvo.getNyygx():new UFDouble(0)));
					}
				}
				HYPubBO_Client.updateAry(gxvos);
			}else{
				GxBVO[] gxvos = (GxBVO[])HYPubBO_Client.queryByCondition(GxBVO.class,
						" isnull(dr,0)=0 and vyear='"+new UFDate(begin).getYear()+"' and pk_corp='"+_getCorp().getPrimaryKey()+"' "+HRPPubTool.formInSQL("pk_psndoc", list_psn)+" ");
				if(gxvos!=null&&gxvos.length>0){
					for(GxBVO gxvo:gxvos){
						gxvo.setNyygx(map_gxs.get(gxvo.getPk_psndoc()));
						if(gxvo.getNsfgx() != null){
						gxvo.setNlastsygx(gxvo.getNsxgx());
						gxvo.setNsxgx(gxvo.getNsfgx().sub(gxvo.getNyygx()!=null?gxvo.getNyygx():new UFDouble(0)));
						}
						}
				}
				HYPubBO_Client.updateAry(gxvos);
			}


		}catch(Exception e){
			e.printStackTrace();
		}

		((ClientUI)getBillUI()).setEditstate(0);
		getBillCardPanelWrapper().getBillCardPanel().setEnabled(false);
		getBillCardPanelWrapper().getBillCardPanel().getHeadItem("deptnamekq").setEnabled(true);
		getBillCardPanelWrapper().getBillCardPanel().getHeadItem("begindate").setEnabled(true);
		getBillCardPanelWrapper().getBillCardPanel().getHeadItem("enddate").setEnabled(true);
		getButtonManager().getButton(801).setEnabled(true);
		getButtonManager().getButton(802).setEnabled(true);
		getButtonManager().getButton(803).setEnabled(true);
		getButtonManager().getButton(804).setEnabled(true);
		getButtonManager().getButton(805).setEnabled(true);
		getButtonManager().getButton(806).setEnabled(true);
		getButtonManager().getButton(807).setEnabled(true);
		getButtonManager().getButton(808).setEnabled(true);
		getButtonManager().getButton(IBillButton.Add).setEnabled(true);
		getButtonManager().getButton(IBillButton.Edit).setEnabled(false);
		getButtonManager().getButton(IBillButton.Copy).setEnabled(false);
		getButtonManager().getButton(IBillButton.Save).setEnabled(false);
		getButtonManager().getButton(IBillButton.Cancel).setEnabled(false);
		getButtonManager().getButton(810).setEnabled(true);
		getBillUI().updateButtons();
		setdate();
	}
	@Override
	protected void onBoCancel() throws Exception {
		// TODO Auto-generated method stub
		((ClientUI)getBillUI()).setEditstate(0);
		getBillCardPanelWrapper().getBillCardPanel().setEnabled(false);
		getBillCardPanelWrapper().getBillCardPanel().getHeadItem("deptnamekq").setEnabled(true);
		getBillCardPanelWrapper().getBillCardPanel().getHeadItem("begindate").setEnabled(true);
		getBillCardPanelWrapper().getBillCardPanel().getHeadItem("enddate").setEnabled(true);
		
		getButtonManager().getButton(801).setEnabled(true);
		getButtonManager().getButton(802).setEnabled(true);
		getButtonManager().getButton(803).setEnabled(true);
		getButtonManager().getButton(804).setEnabled(true);
		getButtonManager().getButton(805).setEnabled(true);
		getButtonManager().getButton(806).setEnabled(true);
		getButtonManager().getButton(807).setEnabled(true);
		getButtonManager().getButton(808).setEnabled(true);
		getButtonManager().getButton(IBillButton.Add).setEnabled(true);
		getButtonManager().getButton(IBillButton.Edit).setEnabled(false);
		getButtonManager().getButton(IBillButton.Copy).setEnabled(false);
		getButtonManager().getButton(IBillButton.Save).setEnabled(false);
		getButtonManager().getButton(IBillButton.Cancel).setEnabled(false);
		getBillUI().updateButtons();
		setdate();

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
		PaibanWeekVO[] weekvos = (PaibanWeekVO[])HYPubBO_Client.queryByCondition(PaibanWeekVO.class,
				"isnull(dr,0)=0 and pk_corp='"+_getCorp().getPrimaryKey()+"' and vdate='"+vdate+"' "+HRPPubTool.formInSQL("pk_dept", list)+" ");
		if(weekvos!=null&&weekvos.length>0){
			HashMap<String, PaibanWeekVO> map = new HashMap<String, PaibanWeekVO>();
			for(PaibanWeekVO vo:weekvos){
				map.put(vo.getPk_psndoc(), vo);
			}
			int rowcount = getBillCardPanelWrapper().getBillCardPanel().getBillModel().getRowCount();
			for(int i=0;i<rowcount;i++){
				String pk_psndoc = getBillCardPanelWrapper().getBillCardPanel().getBillModel().getValueAt(i, "pk_psndoc").toString();
				PaibanWeekVO vo = map.get(pk_psndoc);
				if(vo!=null){
					for(int j=1;j<8;j++){
						if(getBillCardPanelWrapper().getBillCardPanel().getBillModel().getItemByKey("vbbname"+j+"").isEnabled()){
							int col =getBillCardPanelWrapper().getBillCardPanel().getBillModel().getItemIndex("vbbname"+j+"");
							boolean flag = getBillCardPanelWrapper().getBillCardPanel().getBillModel().isCellEditable(i, col);
							if(flag){
								getBillCardPanelWrapper().getBillCardPanel().getBillModel().setValueAt(vo.getAttributeValue("vbbname"+j+""), i, "vbbname"+j+"");
								getBillCardPanelWrapper().getBillCardPanel().getBillModel().setValueAt(vo.getAttributeValue("pk_bb"+j+""), i, "pk_bb"+j+"");
							}
						}
					}
				}
			}
		}

	}
	public void setdate(){
		getBillCardPanelWrapper().getBillCardPanel().getBillModel().clearBodyData();
		String[] pks = ((UIRefPane)getBillCardPanelWrapper().getBillCardPanel().getHeadItem("deptnamekq").getComponent()).getRefPKs();
		String vdate = getBillCardPanelWrapper().getBillCardPanel().getHeadItem("vdate").getValueObject().toString();
		UFDate begin = new UFDate(vdate.substring(0,10));
		for(int i=1;i<8;i++){
			String name = getBillCardPanelWrapper().getBillCardPanel().getBillModel().getItemByKey("vbbname"+(i)+"").getName().substring(0,3);
			getBillCardPanelWrapper().getBillCardPanel().getBillModel().getItemByKey("vbbname"+(i)+"").setName(name+"("+begin.getDateAfter(i-1)+")");
		}
		getBillCardPanelWrapper().getBillCardPanel().setBillData(getBillCardPanelWrapper().getBillCardPanel().getBillData());
		getBillCardPanelWrapper().getBillCardPanel().getBillTable().setHeaderHeight(30);
		if(pks==null) return;
		ArrayList<String> list = new ArrayList<String>();
		list.addAll(Arrays.asList(pks));

		try {
			GxBVO[] gxvos = (GxBVO[])HYPubBO_Client.queryByCondition(GxBVO.class, " isnull(dr,0)=0 and vyear='"+begin.getYear()+"' and pk_corp='"+_getCorp().getPrimaryKey()+"' ");
			HashMap<String,UFDouble> map = new HashMap<String, UFDouble>();
			if(gxvos!=null&&gxvos.length>0){
				for(GxBVO gxvo:gxvos){
					map.put(gxvo.getPk_psndoc(), gxvo.getNsxgx());
				}
			}
			PaibanWeekVO[] weekvos = (PaibanWeekVO[])HYPubBO_Client.queryByCondition(PaibanWeekVO.class,
					"isnull(dr,0)=0 and pk_corp='"+_getCorp().getPrimaryKey()+"' and vdate='"+vdate+"' "+HRPPubTool.formInSQL("pk_dept", list)+" ");
			
			HashMap<String,String> vbillstatusMap = new HashMap<String, String>();  
			vbillstatusMap.put("0", "未提交");
			vbillstatusMap.put("1", "提交");
			vbillstatusMap.put("2", "医务审核通过");
			vbillstatusMap.put("3", "医务退回");
			vbillstatusMap.put("4", "OA提交");
			vbillstatusMap.put("5", "门办审核通过");
			vbillstatusMap.put("6", "门办退回");
			vbillstatusMap.put("7", "医务门办审核通过");
			vbillstatusMap.put("8", "审核通过");
			vbillstatusMap.put("9", "退回");
			vbillstatusMap.put("10", "审核通过");
			
			if(weekvos!=null&&weekvos.length>0){
				for(PaibanWeekVO weekvo:weekvos){
					weekvo.setNgxs(map.get(weekvo.getPk_psndoc()));
					weekvo.setNsygxs(map.get(weekvo.getPk_psndoc()));
					if(weekvo.getVbillstatus3()!=null){
						if(weekvo.getVbillstatus()!=null){
							weekvo.setVbillstatus(weekvo.getVbillstatus()+vbillstatusMap.get(weekvo.getVbillstatus3())); //lzch
						}else{
							weekvo.setVbillstatus(vbillstatusMap.get(weekvo.getVbillstatus3())); //lzch
						}
					}
					if(weekvo.getVbillstatus2()!=null && ("0").equals(weekvo.getVbillstatus2())==false && ("1").equals(weekvo.getVbillstatus2())==false){
						if(weekvo.getVbillstatus()!=null && !("提交").equals(weekvo.getVbillstatus()) ){
							if(!("提交").equals(weekvo.getVbillstatus())){
								weekvo.setVbillstatus(weekvo.getVbillstatus()+vbillstatusMap.get(weekvo.getVbillstatus2())); //lzch
							}else{
								weekvo.setVbillstatus(vbillstatusMap.get(weekvo.getVbillstatus2())); //lzch
							}
						}else{
							weekvo.setVbillstatus(vbillstatusMap.get(weekvo.getVbillstatus2())); //lzch
							if( weekvo.getVbillstatus1()!=null && "4".equals(weekvo.getVbillstatus2()) && "4".equals(weekvo.getVbillstatus1())==false){
								weekvo.setVbillstatus("门办OA提交"); //lzch
							}else if("4".equals(weekvo.getVbillstatus2())==true && weekvo.getVbillstatus2()!=null){
								weekvo.setVbillstatus(weekvo.getVbillstatus()+"门办OA提交"); //lzch
							}else if("4".equals(weekvo.getVbillstatus1())==true){
								weekvo.setVbillstatus(weekvo.getVbillstatus()+"OA提交"); //lzch
							} 
						}
					}
					if(weekvo.getVbillstatus1()!=null && ("0").equals(weekvo.getVbillstatus1())==false && ("1").equals(weekvo.getVbillstatus1())==false){
						if(weekvo.getVbillstatus()!=null && ("4").equals(weekvo.getVbillstatus1())==false && !("提交").equals(weekvo.getVbillstatus())){
							weekvo.setVbillstatus(weekvo.getVbillstatus()+vbillstatusMap.get(weekvo.getVbillstatus1())); //lzch
						}else if(("4").equals(weekvo.getVbillstatus1())==false){
							weekvo.setVbillstatus(vbillstatusMap.get(weekvo.getVbillstatus1())); //lzch
						}else if(("4").equals(weekvo.getVbillstatus1())==true){
							if(weekvo.getVbillstatus()==null || ("提交").equals(weekvo.getVbillstatus())==true){
								weekvo.setVbillstatus(vbillstatusMap.get(weekvo.getVbillstatus1())); //lzch
							}
						}
					}
				}
				getBillCardPanelWrapper().getBillCardPanel().getBillModel().setBodyDataVO(weekvos);
				getBillCardPanelWrapper().getBillCardPanel().getBillModel().execLoadFormula();
			}
			
			/*
			String upstatus = getUploadStatus(vdate, list);
			
			if(upstatus.equals("0") || upstatus.equals("2")){
				getButtonManager().getButton(IBillButton.Add).setEnabled(true);
				getBillUI().updateButtons();
			}else{
				getButtonManager().getButton(IBillButton.Add).setEnabled(false);
				getBillUI().updateButtons();
			}*/
		} catch (BusinessException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		try {
			onshow(showtype);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
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
			getBillCardPanelWrapper().getBillCardPanel().getHeadItem("begindate").setValue(begin);
			getBillCardPanelWrapper().getBillCardPanel().getHeadItem("enddate").setValue(end);
			setdate();
			break;
		case 802:
			vdate =  getBillCardPanelWrapper().getBillCardPanel().getHeadItem("vdate").getValueObject().toString();
			vdate =  vdate.substring(0,10);
			begin = new UFDate(vdate).getDateBefore(7);
			end = new UFDate(vdate).getDateBefore(1);
			getBillCardPanelWrapper().getBillCardPanel().getHeadItem("vdate").setValue(begin+"至"+end);
			getBillCardPanelWrapper().getBillCardPanel().getHeadItem("begindate").setValue(begin);
			getBillCardPanelWrapper().getBillCardPanel().getHeadItem("enddate").setValue(end);
			setdate();
			break;
		case 803:
			int x = _getDate().getWeek();
			begin = _getDate().getDateBefore(x-1);
			end = _getDate().getDateAfter(7-x);
			getBillCardPanelWrapper().getBillCardPanel().getHeadItem("vdate").setValue(begin+"至"+end);
			getBillCardPanelWrapper().getBillCardPanel().getHeadItem("begindate").setValue(begin);
			getBillCardPanelWrapper().getBillCardPanel().getHeadItem("enddate").setValue(end);
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
		case 810:
			onUpload();
		default:
			break;
		}
	}
	
	public int getWeek(String str) throws Exception{
		SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd");
		Date date =sdf.parse(str);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		// 第几周
		int week = calendar.get(Calendar.WEEK_OF_MONTH);
		// 第几天，从周日开始
		int day = calendar.get(Calendar.DAY_OF_WEEK);
		return week;
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
	
	/**
	 * 排班记录上传
	 * @throws Exception 
	 * @throws BusinessException 
	 *
	 */
	public void onUpload() throws BusinessException, Exception{
		String[] pks = ((UIRefPane)getBillCardPanelWrapper().getBillCardPanel().getHeadItem("deptnamekq").getComponent()).getRefPKs();
		if(pks==null) {
			 MessageDialog.showErrorDlg(null, "提示","上传失败："+"请选择上传的部门");
			 return;
		}
		
		String vdate1 = getBillCardPanelWrapper().getBillCardPanel().getHeadItem("vdate").getValueObject().toString();
		String begindate = getBillCardPanelWrapper().getBillCardPanel().getHeadItem("begindate").getValueObject().toString();
//		String begindate = vdate1.substring(0, 10);
		
		// 判断排班一次审核有没有审核，已经审核则不能再次排班或上传排班记录
		// 判断当前时间,是否在参数范围之内
		/*************************************************/
		HashMap<String,DeptKqVO>  map_dept = getDeptKqVO();
		ArrayList<String> listdept1 = new ArrayList<String>();
		ArrayList<String> listdept2 = new ArrayList<String>();
		ArrayList<String> listdept3 = new ArrayList<String>();
		
		for(int i=0;i<pks.length;i++){
			DeptKqVO deptvo = map_dept.get(pks[i]);
			// 根据下级部门查找的上级部门
			if(deptvo.getPk_parent() == null){
				if(listdept1.size() == 0){
					listdept1.add(deptvo.getPk_dept());
				}else{
					if(!listdept1.contains(deptvo.getPk_dept())){
						listdept1.add(deptvo.getPk_dept());
					}
				}
			}else{
				// 已经选中的上级部门
				listdept1.add(deptvo.getPk_parent());	
			}
		}
		
		
		/************************************/
		
		for(int i=0;i<listdept1.size();i++){
			for(String pk_deptdoc: map_dept.keySet()){
				DeptKqVO deptvo = map_dept.get(pk_deptdoc);
				if(deptvo.getPk_parent() == listdept1.get(i)){
					listdept3.add(deptvo.getPk_dept());
				}
			}
			
		}
		
		
		ArrayList<String> list01 = new ArrayList<String>();
//		list01.addAll(Arrays.asList(pks));
		list01.addAll(listdept3);
		list01.addAll(listdept1);
		
		
		onUploadOther(list01);
		/***************************************************//*
		ArrayList<String>  deptlist = new ArrayList<String>();
		for(int i=0;i<list01.size();i++){
			DeptKqVO deptvo = map_dept.get(list01.get(i));
			if(deptvo.getVcode().length() >=4){
				String prefixcode = deptvo.getVcode().substring(0,4);
				if(prefixcode.equals("1900")){
					if(!deptlist.contains("Y")){
						deptlist.add("Y");
					}
				}else{
					if(!deptlist.contains("N")){
						deptlist.add("N");
					}
				}
			}else{
				if(!deptlist.contains("N")){
					deptlist.add("N");
				}
			}
			
		}
		*//***************************************************//*
		
		if(deptlist.size() == 2){
			 MessageDialog.showErrorDlg(null, "提示","上传失败："+"上传科室中包含有护理科室!");
			 return;
		}*/
		
		/*for(int i=0;i<deptlist.size();i++){
			if(deptlist.get(i).equals("Y")){
				onUploadHl(list01);// 护理科室上传
			}else if(deptlist.get(i).equals("N")){
				onUploadOther(list01);
			}
			
		}*/
		
		
	}
	
	public void onUploadOther(ArrayList<String> list01) throws Exception{
		String begindate = getBillCardPanelWrapper().getBillCardPanel().getHeadItem("begindate").getValueObject().toString();

		UFDate serverdate = nc.ui.hr.global.Global.getServerTime().getDate();
		String servernextdate = new UFDate(serverdate.getYear() + "-" + (serverdate.getMonth()+1)+"-01").toString();
		
		Integer uploadnum = 1;
		String sqlwhere0 = " vbillstatus='0'and vperiod='"+servernextdate.substring(0,7)+"'" +HRPPubTool.formInSQL("pk_dept", list01);
		PaiBanAuditMsg[] backpaibanvos = getPaiBanAuditMsg(sqlwhere0);
	
		if(backpaibanvos.length == 0){
			// 查询有没有退回，有则不做时间控制，直接上传
			UFDate curdate = nc.ui.hr.global.Global.getServerTime().getDate();
//			UFDate curdate = ClientEnvironment.getInstance().getDate();
			int curday = curdate.getDay();
			ParValueVO[] valuevos = (ParValueVO[])HYPubBO_Client.queryByCondition(ParValueVO.class, " isnull(dr,0)=0 and pk_corp='"+_getCorp().getPrimaryKey()+"' and  PAR_CODE='TBMSHJY' ");
			int day = 1;
			if(valuevos!=null&&valuevos.length>0){
				day = Integer.parseInt( valuevos[0].getPar_value());
			}
			int paradate = day;
			/*if(curday > paradate){
				 MessageDialog.showErrorDlg(null, "提示","上传失败："+"请将日期切到"+day+"号以前,大于"+day+"号不能上传!");
				 return;
			}*/
		}else{
			uploadnum = 2;// 审核不通过，二次上传
		}
		
			
		
		// 查询有没有审核
//		String sqlwhere1 = " vbillstatus='1'and vperiod='"+servernextdate.substring(0,7)+"'" +HRPPubTool.formInSQL("pk_dept", list01);
//		PaiBanAuditMsg[] paibanvos = getPaiBanAuditMsg(sqlwhere1);
//		if(paibanvos.length>0){
//			MessageDialog.showErrorDlg(null, "提示","上传失败：已经审核不能再次上传!");
//			return;
//		}
		
		UFDate curdate = nc.ui.hr.global.Global.getServerTime().getDate();
		String curmonth = curdate.getYear() + "-" + (curdate.getMonth()+1);
		String nextdate = curmonth + "-"+"01";
		
		UFDate nextbegindate = new UFDate(nextdate);
		int day = 0;
		if(getDay(nextdate) != 2){// 不为周一
			if(getDay(nextdate)==1){ // 但为周天
				day = 8;
			}else{
				day = getDay(nextdate);
			}
			nextbegindate = new UFDate(nextdate).getDateBefore(day - 2);
		}
		
		
		// 判断是否一个月最后一周，最后一周检查前几个周有没有漏排班
//		if(nextbegindate.compareTo(new UFDate(begindate)) == 0){
//			if(getDay(begindate) != 2){
//				MessageDialog.showErrorDlg(null, "提示","上传失败：上传日期只能本月第一周星期一");
//				 return;
//			}
			UFDate begin1 = new UFDate(begindate);
			UFDate end1 = begin1.getDateAfter(6);
			UFDate begin2 = begin1.getDateAfter(7);
			UFDate end2 = begin2.getDateAfter(6);
			UFDate begin3 = begin2.getDateAfter(7);
			UFDate end3 = begin3.getDateAfter(6);
			UFDate begin4 = begin3.getDateAfter(7);
			UFDate end4 = begin4.getDateAfter(6);
			UFDate begin5 = begin4.getDateAfter(7);
			UFDate end5 = begin5.getDateAfter(6);
			
			
			/*************************************************/
			// 判断当前时间与排班开始时间比较，
//			String uploadmonth = end1.getYear()+"-"+(end1.getMonth());
//			if(!curmonth.equals(uploadmonth)){
//				MessageDialog.showErrorDlg(null, "提示","上传失败：上传时间与排班时间不符!");
//				return;
//			}
			
			/*************************************************/
			if(begindate.equals("2019-05-27")){
				nextdate = "2019-06-01";
			}
	
			//String[] vdates = getBeginAndEnd(begindate,new UFDate(nextdate).toString());
//			String[] vdates = {vdate11,vdate12,vdate13,vdate14,vdate15}; 
			
			String vdate = getBillCardPanelWrapper().getBillCardPanel().getHeadItem("vdate").getValueObject().toString();
			
			StringBuffer errorMsg = new StringBuffer();   //错误提示信息
			
			PaibanWeekVO[] vos = (PaibanWeekVO[])getBillCardPanelWrapper().getBillCardPanel().getBillModel().getBodyValueVOs(PaibanWeekVO.class.getName());
			// 保存时判断公休是否为<0,则不能保存
			for(int i=0;i<vos.length;i++){
			PaibanWeekVO paibanweekvo = vos[i];
			/*UFDouble nsygxs = new UFDouble("0.00");
			String pk_vbb = "10028L100000000002D8";// 公休PK
			for(int j=1;j<8;j++){
				String pk_bbs = paibanweekvo.getAttributeValue("pk_bb"+j) != null ? paibanweekvo.getAttributeValue("pk_bb"+j).toString().trim():null;
				if(pk_vbb.equals(pk_bbs)){
					nsygxs=nsygxs.add(new UFDouble("1.00"));
				}
				
			}*/
			
			if(paibanweekvo.getNsygxs() != null){
				if(paibanweekvo.getNsygxs().compareTo(new UFDouble(0.0)) == -1){
					String psnname = (String) getBillCardPanelWrapper().getBillCardPanel().getBodyValueAt(i, "psnname");
					errorMsg.append("第 "+ (i+1) +"行,"+psnname+"公休数小于0！\n");
					continue;
				}
			}
			
		}
		
		if(errorMsg.length() != 0 || !"".equals(errorMsg.toString())){
			 MessageDialog.showErrorDlg(null, "提示","上传失败："+errorMsg);
			 return;
		}
			/*for(int j=0;j<vdates.length;j++){
				String vdate = vdates[j];
				nextdate = vdates[j].substring(11);
				
				String upstatus = getUploadStatus(vdate,list01);
				if(upstatus.equals("1")){
					MessageDialog.showErrorDlg(null, "提示","上传失败：已经审核不能上传!");
					return;
				}else if(upstatus.equals("3")){
					MessageDialog.showErrorDlg(null, "提示","上传失败：已经上传不能再次上传，需审核员退回后才能重新上传!");
					return;
				}else if(upstatus.equals("4")){
					MessageDialog.showErrorDlg(null, "提示","上传失败：已经有审批通过或退回，只能选退回科室后再上传!");
					return;
				}else if(upstatus.equals("5")){
					MessageDialog.showErrorDlg(null, "提示","上传失败：已经有审批取消审核，需审核员退回后才能重新上传!");
					return;
				}*/
				
				errorMsg = getPaibanMsg(vdate,list01);
				if(errorMsg.length() != 0 || !"".equals(errorMsg.toString())){
					 MessageDialog.showErrorDlg(null, "提示","上传失败："+errorMsg + "!");
					 return;
				}
			//}
		
			
			/***************************************************/
			String dclassperiod =  curdate.getYear() + "-" + (curdate.getMonth()+1) +"-01";
			UFDate ufdclassperiod = new UFDate(dclassperiod);
			
			/*String msg = upLoadPaibanWeek(vdates,uploadnum,ufdclassperiod.toString(),list01);
			if(!"".equals(msg)){
				MessageDialog.showErrorDlg(null, "提示","上传失败："+msg);
				return;
			}else{
				MessageDialog.showHintDlg(null, "提示", begindate +"至"+nextdate+",上传成功！");
			}*/
			
//		}else{
//			 MessageDialog.showErrorDlg(null, "提示","上传失败：开始时间与本月第一周开始时间不相等!");
//			 return;
//		}
			HashMap<String,DeptKqVO> deptKqVO = getDeptKqVO();
			
			String vcode = deptKqVO.get(list01.get(0)).getVcode();
			String flag="N";
			if( vcode.length()>=4 && ("1900").equals(vcode.substring(0, 4))==false){
				flag="Y";
			}else if(vcode.length()<4){
				flag="Y";
			}
			
			if("2019-07-29至2019-08-04".equals(vdate)==true && flag=="Y"){
				//不是护理组  
				MessageDialog.showHintDlg(null, "提示", "2019-07-29至2019-07-31,上传成功！");
			}else{
				MessageDialog.showHintDlg(null, "提示", vdate+",上传成功！");
			}
		
		getButtonManager().getButton(810).setEnabled(false);
		getBillUI().updateButtons();
		
		GxBVO[] gxvos = (GxBVO[])HYPubBO_Client.queryByCondition(GxBVO.class, " isnull(dr,0)=0 and vyear='"+vdate.substring(0,4)+"' and pk_corp='"+_getCorp().getPrimaryKey()+"' ");
		HashMap<String,UFDouble> map = new HashMap<String, UFDouble>();
		if(gxvos!=null&&gxvos.length>0){
			for(GxBVO gxvo:gxvos){
				map.put(gxvo.getPk_psndoc(), gxvo.getNsxgx());
			}
		}
		
		HashMap<String,String> vbillstatusMap = new HashMap<String, String>();  
		vbillstatusMap.put("0", "未提交");
		vbillstatusMap.put("1", "提交");
		vbillstatusMap.put("2", "医务审核通过");
		vbillstatusMap.put("3", "医务退回");
		vbillstatusMap.put("4", "OA提交");
		vbillstatusMap.put("5", "门办审核通过");
		vbillstatusMap.put("6", "门办退回");
		vbillstatusMap.put("7", "医务门办审核通过");
		vbillstatusMap.put("8", "审核通过");
		vbillstatusMap.put("9", "退回");
		vbillstatusMap.put("10", "审核通过");
		
		PaibanWeekVO[] weekvos = (PaibanWeekVO[])HYPubBO_Client.queryByCondition(PaibanWeekVO.class,
				"isnull(dr,0)=0 and pk_corp='"+_getCorp().getPrimaryKey()+"' and vdate='"+vdate+"' "+HRPPubTool.formInSQL("pk_dept", list01)+" ");
		ArrayList<PaibanWeekVO> list_vo = new ArrayList<PaibanWeekVO>();
		ArrayList<String> pk_list_psn = new ArrayList<String>();
		if(weekvos!=null&&weekvos.length>0){
			for(PaibanWeekVO weekvo:weekvos){
				pk_list_psn.add(weekvo.getPk_psndoc());
				weekvo.setNgxs(map.get(weekvo.getPk_psndoc()));
				weekvo.setNsygxs(map.get(weekvo.getPk_psndoc()));
				if(weekvo.getVbillstatus3()!=null){
					weekvo.setVbillstatus(vbillstatusMap.get(weekvo.getVbillstatus3())); //lzch
				}else if(weekvo.getVbillstatus2()!=null && ("0").equals(weekvo.getVbillstatus2())==false && ("1").equals(weekvo.getVbillstatus2())==false){
					weekvo.setVbillstatus(vbillstatusMap.get(weekvo.getVbillstatus2())); //lzch
					if( weekvo.getVbillstatus1()!=null && "4".equals(weekvo.getVbillstatus2()) && "4".equals(weekvo.getVbillstatus1())==false){
						weekvo.setVbillstatus("门办OA提交"); //lzch
					}
				}else if(weekvo.getVbillstatus1()!=null && ("0").equals(weekvo.getVbillstatus1())==false && ("1").equals(weekvo.getVbillstatus1())==false){
					weekvo.setVbillstatus(vbillstatusMap.get(weekvo.getVbillstatus1())); //lzch
				}
				list_vo.add(weekvo);
			}
		}
		String begin = vdate.substring(0, 10);
		String end =vdate.substring(11, 21);
		DeptKqBVO[] bvos = (DeptKqBVO[])HYPubBO_Client.queryByCondition(DeptKqBVO.class,
				" isnull(dr,0)=0 "+HRPPubTool.formInSQL("pk_dept", list01)+"  and (denddate is null or denddate>='"+begin+"') and dstartdate<='"+end+"' ");
		if(bvos!=null&&bvos.length>0){
			for(DeptKqBVO bvo:bvos){
				if(!pk_list_psn.contains(bvo.getPk_psndoc())){
					PaibanWeekVO weekvo = new PaibanWeekVO();
					weekvo.setDr(0);
					weekvo.setPk_psndoc(bvo.getPk_psndoc());
					weekvo.setPk_dept(bvo.getPk_dept());
					weekvo.setVdate(vdate);
					weekvo.setPk_corp(_getCorp().getPrimaryKey());
					weekvo.setVbillstatus3("1"); //lzch insert
					weekvo.setVbillstatus("提交");
					weekvo.setNgxs(map.get(weekvo.getPk_psndoc()));
					weekvo.setNsygxs(map.get(weekvo.getPk_psndoc()));
					list_vo.add(weekvo);
					pk_list_psn.add(bvo.getPk_psndoc());
				}
			}
		}
		if(list_vo!=null&&list_vo.size()>0){
			getBillCardPanelWrapper().getBillCardPanel().getBillModel().setBodyDataVO(list_vo.toArray(new PaibanWeekVO[0]));
			getBillCardPanelWrapper().getBillCardPanel().getBillModel().execLoadFormula();
		}
	}
	
	public void onUploadHl(ArrayList<String> list01) throws Exception{
		String begindate = getBillCardPanelWrapper().getBillCardPanel().getHeadItem("begindate").getValueObject().toString();

		UFDate serverdate = nc.ui.hr.global.Global.getServerTime().getDate();
		String servernextdate = new UFDate(serverdate.getYear() + "-" + (serverdate.getMonth()+1)+"-01").toString();
		
		Integer uploadnum = 1;
		String sqlwhere0 = " vbillstatus='0'and vperiod='"+servernextdate.substring(0,7)+"'" +HRPPubTool.formInSQL("pk_dept", list01);
		PaiBanAuditMsg[] backpaibanvos = getPaiBanAuditMsg(sqlwhere0);
	
		if(backpaibanvos.length == 0){
			// 查询有没有退回，有则不做时间控制，直接上传
			UFDate curdate = nc.ui.hr.global.Global.getServerTime().getDate();
//			UFDate curdate = ClientEnvironment.getInstance().getDate();
			int curday = curdate.getDay();
			ParValueVO[] valuevos = (ParValueVO[])HYPubBO_Client.queryByCondition(ParValueVO.class, " isnull(dr,0)=0 and pk_corp='"+_getCorp().getPrimaryKey()+"' and  PAR_CODE='TBMSHJY' ");
			int day = 1;
			if(valuevos!=null&&valuevos.length>0){
				day = Integer.parseInt( valuevos[0].getPar_value());
			}
			int paradate = day;
			if(curday > paradate){
				 MessageDialog.showErrorDlg(null, "提示","上传失败："+"请将日期切到"+day+"号以前,大于"+day+"号不能上传!");
				 return;
			}
		}else{
			uploadnum = 2;// 审核不通过，二次上传
		}
		
			
		
		// 查询有没有审核
//		String sqlwhere1 = " vbillstatus='1'and vperiod='"+servernextdate.substring(0,7)+"'" +HRPPubTool.formInSQL("pk_dept", list01);
//		PaiBanAuditMsg[] paibanvos = getPaiBanAuditMsg(sqlwhere1);
//		if(paibanvos.length>0){
//			MessageDialog.showErrorDlg(null, "提示","上传失败：已经审核不能再次上传!");
//			return;
//		}
		
		UFDate curdate = nc.ui.hr.global.Global.getServerTime().getDate();
		String curmonth = curdate.getYear() + "-" + (curdate.getMonth()+1);
		String nextdate = curmonth + "-"+"01";
		
		UFDate nextbegindate = new UFDate(nextdate);
		int day = 0;
		if(getDay(nextdate) != 2){// 不为周一
			if(getDay(nextdate)==1){ // 但为周天
				day = 8;
			}else{
				day = getDay(nextdate);
			}
			nextbegindate = new UFDate(nextdate).getDateBefore(day - 2);// 计算本月第一周周一
		}
		// 本月第三周周一
		UFDate nextbegindate1 = nextbegindate.getDateAfter(14);
		
		// 护理判断当前选择日期，是否为下月的第一周或者第三周
//		if(nextbegindate.compareTo(new UFDate(begindate)) == 0
//				|| nextbegindate1.compareTo(new UFDate(begindate)) == 0){
//			if(getDay(begindate) != 2){
//				MessageDialog.showErrorDlg(null, "提示","上传失败：上传日期只能本月第一周周一或第三周周一！");
//				 return;
//			}
			UFDate begin1 = new UFDate(begindate);
			UFDate end1 = begin1.getDateAfter(6);
			UFDate begin2 = begin1.getDateAfter(7);
			UFDate end2 = begin2.getDateAfter(6);
			UFDate begin3 = begin2.getDateAfter(7);
			UFDate end3 = begin3.getDateAfter(6);
			UFDate begin4 = begin3.getDateAfter(7);
			UFDate end4 = begin4.getDateAfter(6);
			UFDate begin5 = begin4.getDateAfter(7);
			UFDate end5 = begin5.getDateAfter(6);
			
			String vdate11 = begin1.toString()+"至"+end1.toString();
			String vdate12 = begin2.toString()+"至"+end2.toString();
			String vdate13 = begin3.toString()+"至"+end3.toString();
			String vdate14 = begin4.toString()+"至"+end4.toString();
			String vdate15 = begin5.toString()+"至"+end5.toString();
			
			/*************************************************/
			// 判断当前时间与排班开始时间比较，
//			String uploadmonth = end1.getYear()+"-"+(end1.getMonth());
//			if(!curmonth.equals(uploadmonth)){
//				MessageDialog.showErrorDlg(null, "提示","上传失败：上传时间与排班时间不符!");
//				return;
//			}
			
			/*************************************************/
			
	
			/*************************************************/
//			String[] vdates = getBeginAndEnd(begindate,new UFDate(nextdate).toString());
			String[] vdates = {vdate11,vdate12}; 
			
			StringBuffer errorMsg = new StringBuffer();   //错误提示信息
			for(int j=0;j<vdates.length;j++){
				String vdate = vdates[j];
				
				String upstatus = getUploadStatus(vdate,list01);
				if(upstatus.equals("1")){
					MessageDialog.showErrorDlg(null, "提示","上传失败：已经审核不能上传!");
					return;
				}else if(upstatus.equals("3")){
					MessageDialog.showErrorDlg(null, "提示","上传失败：已经上传不能再次上传，需审核员退回后才能重新上传!");
					return;
				}else if(upstatus.equals("4")){
					MessageDialog.showErrorDlg(null, "提示","上传失败：已经有审批通过或退回，需审核员退回后才能重新上传!");
					return;
				}else if(upstatus.equals("5")){
					MessageDialog.showErrorDlg(null, "提示","上传失败：已经有审批取消审核，需审核员退回后才能重新上传!");
					return;
				}
				
				errorMsg = getPaibanMsg(vdate,list01);
				if(errorMsg.length() != 0 || !"".equals(errorMsg.toString())){
					 MessageDialog.showErrorDlg(null, "提示","上传失败："+errorMsg + "!");
					 return;
				}
			}
			/***************************************************/
			
			String dclassperiod =  curdate.getYear() + "-" + (curdate.getMonth()+1) +"-01";
			UFDate ufdclassperiod = new UFDate(dclassperiod);
			String msg = upLoadPaibanWeek(vdates,uploadnum,ufdclassperiod.toString(),list01);
			if(!"".equals(msg)){
				MessageDialog.showErrorDlg(null, "提示","上传失败："+msg);
				return;
			}
			
//		}else{
//			 MessageDialog.showErrorDlg(null, "提示","上传失败：上传日期只能本月第一周周一或第三周周一!");
//			 return;
//		}
		
	
		MessageDialog.showHintDlg(null, "提示", "上传成功！");
		getButtonManager().getButton(810).setEnabled(false);
		getBillUI().updateButtons();
	}
	
	public String[] getBeginAndEnd(String begindate,String serverdate) throws Exception{
		
		String enddate = getEndDay(serverdate);
		
		//System.out.println("::::::本月最后一天" + enddate);
		int numweek = getWeek(enddate);
		
		//System.out.println("::::::第几周" + numweek);
		
		int numday = getDay(enddate);
		int oneday = getDay(serverdate);
		
//		System.out.println("::::::第几天" + numday);
		
		UFDate begin1 = new UFDate(begindate);
		
		//System.out.println("::::::" + begin1);
		
		ArrayList<String> list = new ArrayList<String>();
		
		if(numday == 0){
			for(int i=0;i<numweek;i++){
				UFDate end1 = new UFDate();
				if(i == 0){
					end1 = begin1.getDateAfter(6);
					list.add(begin1+"至"+end1);
					//System.out.println("::::::" + begin1+"-"+end1);
				}else{
					begin1 = begin1.getDateAfter(1);
					end1 = begin1.getDateAfter(6);
					list.add(begin1+"至"+end1);
					//System.out.println("::::::" + begin1+"-"+end1);
				}
				begin1 = end1;
			
			}
		}else if(numday != 0){
			if(oneday !=1){// 判断第一天是否为星期天
				numweek = numweek-1;
			}
			for(int i=0;i<numweek;i++){
				
				UFDate end1 = new UFDate();
				if(i == 0){
					end1 = begin1.getDateAfter(6);
					list.add(begin1+"至"+end1);
					//System.out.println("::::::" + begin1+"-"+end1);
				}else{
					begin1 = begin1.getDateAfter(1);
					end1 = begin1.getDateAfter(6);
					list.add(begin1+"至"+end1);
					//System.out.println("::::::" + begin1+"-"+end1);
				}
				begin1 = end1;
			}
		}
		String[] arrays  = list.toArray(new String[0]);
		return arrays;
		
	}
	
	public static String getEndDay(String str) throws ParseException{
		SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd");
		Date date =sdf.parse(str);

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.DAY_OF_MONTH,calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
		String lastDay= sdf.format(calendar.getTime());
		
		return lastDay;
	}

	public void chkPaibanWeek(String[] vdates) throws UifException{
		String[] pks = ((UIRefPane)getBillCardPanelWrapper().getBillCardPanel().getHeadItem("deptnamekq").getComponent()).getRefPKs();

		ArrayList<String> list = new ArrayList<String>();
		list.addAll(Arrays.asList(pks));
		
		StringBuffer errorMsg = new StringBuffer();   //错误提示信息
		for(int j=0;j<vdates.length;j++){
			String vdate = vdates[j];
			PaibanWeekVO[] weekvos = (PaibanWeekVO[])HYPubBO_Client.queryByCondition(PaibanWeekVO.class,
					"isnull(dr,0)=0 and pk_corp='"+_getCorp().getPrimaryKey()+"' and vdate='"+vdate+"' "+HRPPubTool.formInSQL("pk_dept", list)+" ");
			
			if(weekvos.length == 0){
				
				errorMsg.append(vdate+"\n");
			}
		}
		if(errorMsg.length() != 0 || !"".equals(errorMsg.toString())){
			 MessageDialog.showErrorDlg(null, "提示","上传失败："+errorMsg + "未排班!");
			 return;
		}
	}
	
	public  int getFirstDayInWeek(String str) throws ParseException
	{
		SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd");
		Date date =sdf.parse(str);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
	   
		calendar.set(Calendar.DATE, 1);//将今天设为1号
	    int firstDay = calendar.get(Calendar.DAY_OF_WEEK);
	    return firstDay;
	}
	
	
	public String upLoadPaibanWeek(String[] vdates,Integer uploadnum,String dclassperiod,ArrayList<String> list01) throws UifException{
		String msg="";
		//String[] pks = ((UIRefPane)getBillCardPanelWrapper().getBillCardPanel().getHeadItem("deptnamekq").getComponent()).getRefPKs();

		//ArrayList<String> list = new ArrayList<String>();
		//list.addAll(Arrays.asList(pks));
		//String vdate = getBillCardPanelWrapper().getBillCardPanel().getHeadItem("vdate").getValueObject().toString();
		// 根据当前时间,计算这一个月
		for(int j=0;j<vdates.length;j++){
			String vdate = vdates[j];
			PaibanWeekVO[] weekvos = (PaibanWeekVO[])HYPubBO_Client.queryByCondition(PaibanWeekVO.class,
					"isnull(dr,0)=0 and pk_corp='"+_getCorp().getPrimaryKey()+"' and vdate='"+vdate+"' "+HRPPubTool.formInSQL("pk_dept", list01)+" ");
			UFDate begin = new UFDate(vdate.substring(0,10));
			UFDate end = new UFDate(vdate.substring(11));
			// 
			
			ArrayList<PanbanWeekBVO> list_b = new ArrayList<PanbanWeekBVO>();
			HashMap<String,PaibanTempVO> map = new HashMap<String, PaibanTempVO>();
			PaibanTempVO[] tvos = (PaibanTempVO[])HYPubBO_Client.queryByCondition(PaibanTempVO.class, " isnull(dr,0)=0 and pk_corp='"+_getCorp().getPrimaryKey()+"' ");
			//if(vos!=null&&vos.length>0){
				for(PaibanTempVO vo:tvos){
					map.put(vo.getPk_psndoc(), vo);
				}
		//	}
			ArrayList<String> list_psn = new ArrayList<String>();
			for(PaibanWeekVO weekvo:weekvos){
				for(int i=1;i<8;i++){
					String pk_bbs = weekvo.getAttributeValue("pk_bb"+i+"")!=null?weekvo.getAttributeValue("pk_bb"+i+"").toString().trim():null;
					if(pk_bbs!=null&&pk_bbs.length()>0){
						String[] pk_bbss = pk_bbs.split(",");
						for(String pk_bb:pk_bbss){
							PanbanWeekBVO bvo = new PanbanWeekBVO();
							bvo.setPrimaryKey(null);
							bvo.setDr(0);
							bvo.setPk_corp(_getCorp().getPrimaryKey());
							bvo.setPk_psndoc(weekvo.getPk_psndoc());
							bvo.setPk_dept(weekvo.getPk_dept());
							bvo.setPk_paiban(weekvo.getPrimaryKey());
							bvo.setPk_bb(pk_bb);
							bvo.setBiszb(new UFBoolean(false));
							bvo.setDdate(begin.getDateAfter(i-1));
							bvo.setMemo(weekvo.getMemo());
							bvo.setUploadnum(uploadnum);
							bvo.setDclassperiod(dclassperiod);
							
							list_b.add(bvo);
						}
					}
				}
				PaibanTempVO tempvo = map.get(weekvo.getPk_psndoc());
				if(tempvo!=null){
					tempvo.setNnowdays(weekvo.getNnowdays());
				}
				list_psn.add(weekvo.getPk_psndoc());
			}

			//			HYPubBO_Client.updateAry(map.values().toArray(new PaibanTempVO[0]));
			// 查询本月数据是否已经审核
			for(int i=0;i<list_b.size();i++){
				PanbanWeekBVO pbvo = list_b.get(i);
				String sqlwhere1 = " vbillstatus='1'and vperiod='"+pbvo.getDclassperiod().substring(0, 7)+"' and pk_bb='"+pbvo.getPk_bb()+"' and pk_dept='"+pbvo.getPk_dept()+"'"
				+" and pk_psndoc='"+pbvo.getPk_psndoc()+"' and ddate='"+pbvo.getDdate()+"'";
				PaiBanAuditMsg[] paibanvos = getPaiBanAuditMsg(sqlwhere1);
				
				if(paibanvos.length>0){
					 msg = "已经审核不能再次上传!";
					 break;
				}
			}
			
			if("".equals(msg)){
				// 上传后退回后才能上传
				
				HYPubBO_Client.deleteByWhereClause(PanbanWeekBVO.class, " isnull(biszb,'N')='N' and isnull(dr,0)=0 "+HRPPubTool.formInSQL("pk_dept", list01)+" and (ddate>= '"+begin+"' and ddate<='"+end+"') ");
				HYPubBO_Client.insertAry(list_b.toArray(new PanbanWeekBVO[list_b.size()]));
				HYPubBO_Client.deleteByWhereClause(PaiBanAuditMsg.class, " vbillstatus=0 and isnull(dr,0)=0 "+HRPPubTool.formInSQL("pk_dept", list01)+" and (ddate>= '"+begin+"' and ddate<='"+end+"') ");
				HYPubBO_Client.deleteByWhereClause(PaiBanAuditMsg2.class, " vbillstatus=0 and isnull(dr,0)=0 "+HRPPubTool.formInSQL("pk_dept", list01)+" and (ddate>= '"+begin+"' and ddate<='"+end+"') ");

				PanbanWeekBVO[] bvos =(PanbanWeekBVO[]) HYPubBO_Client.queryByCondition(PanbanWeekBVO.class, " isnull(dr,0)=0 "+HRPPubTool.formInSQL("pk_psndoc", list_psn)+" and pk_bb in ('10028L100000000002D8','10028L100000000002XL') and ddate like '"+begin.getYear()+"%' ");
				HashMap<String,UFDouble> map_gxs = new HashMap<String, UFDouble>();
				if(bvos!=null&&bvos.length>0){
					for(PanbanWeekBVO bvo:bvos){
						String pk_bb = bvo.getPk_bb();
						UFDouble gxs = map_gxs.get(bvo.getPk_psndoc())!=null?map_gxs.get(bvo.getPk_psndoc()):new UFDouble(0);
						if(pk_bb.equals("10028L100000000002D8")){
							gxs = gxs.add(1);
						}else if(pk_bb.equals("10028L100000000002XL")){
							gxs = gxs.add(0.5);
						}
						map_gxs.put(bvo.getPk_psndoc(), gxs);
					}
					GxBVO[] gxvos = (GxBVO[])HYPubBO_Client.queryByCondition(GxBVO.class,
							" isnull(dr,0)=0 and vyear='"+begin.getYear()+"' and pk_corp='"+_getCorp().getPrimaryKey()+"' "+HRPPubTool.formInSQL("pk_psndoc", list_psn)+" ");
					if(gxvos!=null&&gxvos.length>0){
						for(GxBVO gxvo:gxvos){
							gxvo.setNlastsygx(gxvo.getNsxgx());
							gxvo.setNlastyygx(gxvo.getNyygx());
							gxvo.setNyygx(map_gxs.get(gxvo.getPk_psndoc()));
							gxvo.setNsxgx((gxvo.getNsfgx()!=null?gxvo.getNsfgx():new UFDouble(0)).sub(gxvo.getNyygx()!=null?gxvo.getNyygx():new UFDouble(0)));
						}
					}
					HYPubBO_Client.updateAry(gxvos);
				}else{
					GxBVO[] gxvos = (GxBVO[])HYPubBO_Client.queryByCondition(GxBVO.class,
							" isnull(dr,0)=0 and vyear='"+begin.getYear()+"' and pk_corp='"+_getCorp().getPrimaryKey()+"' "+HRPPubTool.formInSQL("pk_psndoc", list_psn)+" ");
					if(gxvos!=null&&gxvos.length>0){
						for(GxBVO gxvo:gxvos){
							gxvo.setNyygx(map_gxs.get(gxvo.getPk_psndoc()));
							if(gxvo.getNsfgx() != null){
								gxvo.setNlastsygx(gxvo.getNsxgx());
								gxvo.setNsxgx(gxvo.getNsfgx().sub(gxvo.getNyygx()!=null?gxvo.getNyygx():new UFDouble(0)));
							}
						}
					}
					HYPubBO_Client.updateAry(gxvos);
				}
			}
			
		}
		return msg;
	}
	
	public PaibanWeek[] getPaibanWeek(String sqlwhere) throws BusinessException{
		IUAPQueryBS service = (IUAPQueryBS)NCLocator.getInstance().lookup(IUAPQueryBS.class.getName());
		String strSQL = "select b.psncode, b.psnname, a.vdate,a.pk_dept,a.pk_psndoc,a.pk_bb1,a.pk_bb2,a.pk_bb3,a.pk_bb4,a.pk_bb5,a.pk_bb6,a.pk_bb7"
							+"  from trtam_paiban a"
							+" left join bd_psndoc b"
							+"    on a.pk_psndoc = b.pk_psndoc where " + sqlwhere;
		PaibanWeek[] paibanweeks = null;
		
		ArrayList<PaibanWeekVO> list_vo = new ArrayList<PaibanWeekVO>();
		Vector o1 = (Vector) service.executeQuery(strSQL,new VectorProcessor());
		if (o1.size() > 0 && o1 != null) {
			paibanweeks = new PaibanWeek[o1.size()];
			for (int i = 0; i < o1.size(); i++) {
				PaibanWeek paibanweek = new PaibanWeek();
				String psncode = new String(((Vector) o1.elementAt(i)).elementAt(0) != null ? ((Vector) o1.elementAt(i)).elementAt(0).toString() : "");
				String psnname = new String(((Vector) o1.elementAt(i)).elementAt(1) != null ? ((Vector) o1.elementAt(i)).elementAt(1).toString() : "");
				String vdate = new String(((Vector) o1.elementAt(i)).elementAt(2) != null ? ((Vector) o1.elementAt(i)).elementAt(2).toString() : "");
				String pk_dept = new String(((Vector) o1.elementAt(i)).elementAt(3) != null ? ((Vector) o1.elementAt(i)).elementAt(3).toString() : "");
				String pk_psndoc = new String(((Vector) o1.elementAt(i)).elementAt(4) != null ? ((Vector) o1.elementAt(i)).elementAt(4).toString() : "");
				String pk_bb1 = new String(((Vector) o1.elementAt(i)).elementAt(5) != null ? ((Vector) o1.elementAt(i)).elementAt(5).toString() : "");
				String pk_bb2 = new String(((Vector) o1.elementAt(i)).elementAt(6) != null ? ((Vector) o1.elementAt(i)).elementAt(6).toString() : "");
				String pk_bb3 = new String(((Vector) o1.elementAt(i)).elementAt(7) != null ? ((Vector) o1.elementAt(i)).elementAt(7).toString() : "");
				String pk_bb4 = new String(((Vector) o1.elementAt(i)).elementAt(8) != null ? ((Vector) o1.elementAt(i)).elementAt(8).toString() : "");
				String pk_bb5 = new String(((Vector) o1.elementAt(i)).elementAt(9) != null ? ((Vector) o1.elementAt(i)).elementAt(9).toString() : "");
				String pk_bb6 = new String(((Vector) o1.elementAt(i)).elementAt(10) != null ? ((Vector) o1.elementAt(i)).elementAt(10).toString() : "");
				String pk_bb7 = new String(((Vector) o1.elementAt(i)).elementAt(11) != null ? ((Vector) o1.elementAt(i)).elementAt(11).toString() : "");

				
				paibanweek.setPsncode(psncode);
				paibanweek.setPsnname(psnname);
				paibanweek.setVdate(vdate);
				paibanweek.setPk_dept(pk_dept);
				paibanweek.setPk_psndoc(pk_psndoc);
				paibanweek.setPk_bb1(pk_bb1);
				paibanweek.setPk_bb2(pk_bb2);
				paibanweek.setPk_bb3(pk_bb3);
				paibanweek.setPk_bb4(pk_bb4);
				paibanweek.setPk_bb5(pk_bb5);
				paibanweek.setPk_bb6(pk_bb6);
				paibanweek.setPk_bb7(pk_bb7);
				
				paibanweeks[i] = paibanweek;
				
//				list_vo.add(paibanweek);
				 
			}
		}
		
		
		
		return paibanweeks;
	}
	
	public PaiBanAuditMsg[] getPaiBanAuditMsg(String sqlwhere) throws UifException{
		
		PaiBanAuditMsg[] paibanauditmsgs = (PaiBanAuditMsg[])HYPubBO_Client.queryByCondition(PaiBanAuditMsg.class, sqlwhere);
		return paibanauditmsgs;
	}
	
	public HashMap<String,DeptKqVO>  getDeptKqVO() throws UifException{
//		String sqlwhere =	" pk_corp='" + _getCorp().getPrimaryKey()+ "' and isnull(bisseal,'N')='N' and pk_dept in (select pk_docid from bd_tr_userpower where isnull(dr,0)=0 and pk_user='"+_getOperator()+"' and powertype=0 and  pk_corp='"+_getCorp().getPrimaryKey()+"') ";
		String sqlwhere =	" pk_corp='" + _getCorp().getPrimaryKey()+ "' and isnull(bisseal,'N')='N' and isnull(dr,0)=0 ";

		DeptKqVO[] deptkqvos = (DeptKqVO[])HYPubBO_Client.queryByCondition(DeptKqVO.class, sqlwhere);
		HashMap<String,DeptKqVO> map_dept = new HashMap<String, DeptKqVO>();
		
		for(DeptKqVO deptvo:deptkqvos){
			map_dept.put(deptvo.getPk_dept(), deptvo);
		}
		return map_dept;
	}
	
	public ArrayList<String> getListZb(String vdate,String begin,String end,ArrayList<String> list) throws UifException{
		ArrayList<String> pk_list_psn = new ArrayList<String>();
		PaibanWeekVO[] weekvos = (PaibanWeekVO[])HYPubBO_Client.queryByCondition(PaibanWeekVO.class,
				"isnull(dr,0)=0 and pk_corp='"+_getCorp().getPrimaryKey()+"' and vdate='"+vdate+"' "+HRPPubTool.formInSQL("pk_dept", list)+" ");
		ArrayList<PaibanWeekVO> list_vo = new ArrayList<PaibanWeekVO>();
		if(weekvos!=null&&weekvos.length>0){
			for(PaibanWeekVO weekvo:weekvos){
				pk_list_psn.add(weekvo.getPk_psndoc());
//				weekvo.setNgxs(map.get(weekvo.getPk_psndoc()));
//				weekvo.setNsygxs(map.get(weekvo.getPk_psndoc()));
				list_vo.add(weekvo);
			}
		}
		DeptKqBVO[] bvos = (DeptKqBVO[])HYPubBO_Client.queryByCondition(DeptKqBVO.class,
				" isnull(dr,0)=0 "+HRPPubTool.formInSQL("pk_dept", list)+"  and (denddate is null or denddate>='"+begin+"') and dstartdate<='"+end+"' ");
		if(bvos!=null&&bvos.length>0){
			for(DeptKqBVO bvo:bvos){
				if(!pk_list_psn.contains(bvo.getPk_psndoc())){
					PaibanWeekVO weekvo = new PaibanWeekVO();
					weekvo.setDr(0);
					weekvo.setPk_psndoc(bvo.getPk_psndoc());
					weekvo.setPk_dept(bvo.getPk_dept());
					weekvo.setVdate(vdate);
					weekvo.setPk_corp(_getCorp().getPrimaryKey());
//					weekvo.setNgxs(map.get(bvo.getPk_psndoc()));
//					weekvo.setNsygxs(map.get(bvo.getPk_psndoc()));
					list_vo.add(weekvo);
					pk_list_psn.add(bvo.getPk_psndoc());
				}
			}
		}
		
		String sql = "";
		if(pk_list_psn!=null&&pk_list_psn.size()>0){
			sql = " isnull(dr,0)=0 and pk_corp='"+_getCorp().getPrimaryKey()+"' and isnull(biszb,'N')='Y' "+HRPPubTool.formInSQL("pk_psndoc", pk_list_psn)+" and ddate<='"+end+"' and ddate>='"+begin+"' ";
		}else{
			sql = "1=2 ";
		}

		PanbanWeekBVO[] panbvos = (PanbanWeekBVO[])HYPubBO_Client.queryByCondition(PanbanWeekBVO.class,sql);
		ArrayList<String> list_zb1 = new ArrayList<String>();
		if(panbvos!=null&&panbvos.length>0){
			for(PanbanWeekBVO panbvo:panbvos){
				list_zb1.add(panbvo.getPk_psndoc().trim()+panbvo.getDdate().toString().trim());
			}
		}
		return list_zb1;
	}
	
	public StringBuffer getPaibanMsg(String vdate,ArrayList<String> list) throws BusinessException{
		ArrayList<String> pk_list_psn = new ArrayList<String>();
		String begin = vdate.substring(0, 10);
		String end =vdate.substring(11, 21);
		PaibanWeekVO[] weekvos = (PaibanWeekVO[])HYPubBO_Client.queryByCondition(PaibanWeekVO.class,
				"isnull(dr,0)=0 and pk_corp='"+_getCorp().getPrimaryKey()+"' and vdate='"+vdate+"' "+HRPPubTool.formInSQL("pk_dept", list)+" ");
		ArrayList<PaibanWeekVO> list_vo = new ArrayList<PaibanWeekVO>();
		
		UserClassTypeVO[] kqclasstypevos = (UserClassTypeVO[])HYPubBO_Client.queryByCondition(UserClassTypeVO.class, " isnull(dr,0)=0 and pk_user='"+_getOperator()+"' and powertype=0 and  pk_corp='"+_getCorp().getPrimaryKey()+"' and pk_docid='6' ");
		   String autoaudit = "N";
		   if(kqclasstypevos != null){
		    if(kqclasstypevos.length>0){
		     autoaudit="Y";
		    }
		   }
		
		if(weekvos!=null&&weekvos.length>0){
			for(PaibanWeekVO weekvo:weekvos){
				pk_list_psn.add(weekvo.getPk_psndoc());
//				weekvo.setNgxs(map.get(weekvo.getPk_psndoc()));
//				weekvo.setNsygxs(map.get(weekvo.getPk_psndoc()));
				
				if("Y".equals(autoaudit)){
					weekvo.setVbillstatus1(new String("10"));
					weekvo.setVbillstatus3(null); 
				}else{
					weekvo.setVbillstatus1("1"); //lzch insert
					weekvo.setVbillstatus2("1"); //lzch insert
					weekvo.setVbillstatus3("1"); //lzch insert
				}
				
				if(weekvo.getUploadnum()==null){
					weekvo.setUploadnum(1);
				}else{
					weekvo.setUploadnum(weekvo.getUploadnum()+1);
				}
				list_vo.add(weekvo);
			}
		}
		DeptKqBVO[] bvos = (DeptKqBVO[])HYPubBO_Client.queryByCondition(DeptKqBVO.class,
				" isnull(dr,0)=0 "+HRPPubTool.formInSQL("pk_dept", list)+"  and (denddate is null or denddate>='"+begin+"') and dstartdate<='"+end+"' ");
		if(bvos!=null&&bvos.length>0){
			for(DeptKqBVO bvo:bvos){
				if(!pk_list_psn.contains(bvo.getPk_psndoc())){
					PaibanWeekVO weekvo = new PaibanWeekVO();
					weekvo.setDr(0);
					weekvo.setPk_psndoc(bvo.getPk_psndoc());
					weekvo.setPk_dept(bvo.getPk_dept());
					weekvo.setVdate(vdate);
					weekvo.setPk_corp(_getCorp().getPrimaryKey());
//					weekvo.setNgxs(map.get(bvo.getPk_psndoc()));
//					weekvo.setNsygxs(map.get(bvo.getPk_psndoc()));
					
					if("Y".equals(autoaudit)){
						weekvo.setVbillstatus1(new String("10"));
						weekvo.setVbillstatus3(null); 
					}else{
						weekvo.setVbillstatus1("1"); //lzch insert
						weekvo.setVbillstatus2("1"); //lzch insert
						weekvo.setVbillstatus3("1"); //lzch insert
					}
					
					if(weekvo.getUploadnum()==null){
						weekvo.setUploadnum(1);
					}else{
						weekvo.setUploadnum(weekvo.getUploadnum()+1);
					}
					list_vo.add(weekvo);
					pk_list_psn.add(bvo.getPk_psndoc());
				}
			}
		}
		
		String sql = "";
		if(pk_list_psn!=null&&pk_list_psn.size()>0){
			sql = " isnull(dr,0)=0 and pk_corp='"+_getCorp().getPrimaryKey()+"' and isnull(biszb,'N')='Y' "+HRPPubTool.formInSQL("pk_psndoc", pk_list_psn)+" and ddate<='"+end+"' and ddate>='"+begin+"' ";
		}else{
			sql = "1=2 ";
		}

		PanbanWeekBVO[] panbvos = (PanbanWeekBVO[])HYPubBO_Client.queryByCondition(PanbanWeekBVO.class,sql);
		ArrayList<String> list_zb1 = new ArrayList<String>();
		if(panbvos!=null&&panbvos.length>0){
			for(PanbanWeekBVO panbvo:panbvos){
				list_zb1.add(panbvo.getPk_psndoc().trim()+panbvo.getDdate().toString().trim());
			}
		}
		
		HashMap<String,DeptKqVO> deptKqVO = getDeptKqVO();
		StringBuffer errorMsg = new StringBuffer();   //错误提示信息
		for(int i=0;i<list_vo.size();i++){
			PaibanWeekVO paibanweekvo = list_vo.get(i);
			
			String pk_psndoc=paibanweekvo.getPk_psndoc();
			String pk_deptdoc=paibanweekvo.getPk_dept();
			
			String vdate2 = paibanweekvo.getVdate();//周排班时间断
			//String string = vdate2.substring(11);//周排班结束时间
			//int k = new UFDate(string).compareTo(new UFDate("2019-07-31"));
			
			String vcode = deptKqVO.get(paibanweekvo.getPk_dept()).getVcode();
			String flag="N";
			if( vcode.length()>=4 && ("1900").equals(vcode.substring(0, 4))==false){
				//不是1900开头的  ==》不是护理
				flag="Y";  //
			}else if(vcode.length()<4){
				//
				flag="Y";
			}
			if("2019-07-29至2019-08-04".equals(vdate2) && flag=="Y"){
				//不是护理 且 时间段在 2019-07-29至2019-08-04
				for(int j=1;j<8;j++){
					if(paibanweekvo.getAttributeValue("pk_bb"+j) == null){
						
						UFDate showdate = new UFDate(paibanweekvo.getVdate().substring(0, 10)).getDateAfter(j-1);
						String showdateStr = showdate.toString();
						if("2019-08-01".equals(showdateStr)==true ||"2019-08-02".equals(showdateStr)==true ||"2019-08-03".equals(showdateStr)==true ||"2019-08-04".equals(showdateStr)==true ){
							
						}else{
							// 			
							
							DeptKqBVO[] bvos1 = (DeptKqBVO[])HYPubBO_Client.queryByCondition(DeptKqBVO.class,
									" isnull(dr,0)=0 and pk_psndoc='"+pk_psndoc+"' and pk_dept='"+pk_deptdoc+"'  and (denddate is null or denddate>='"+begin+"') and dstartdate<='"+end+"' order by dstartdate desc ");
							
							if(bvos1 !=null){
								if(bvos1.length >0){
									
									
									if(!list_zb1.contains(pk_psndoc+showdate)){// 不是值班
										//errorMsg.append(paibanweekvo.getVdate()+","+getPsnName(pk_psndoc)+" 漏排班\n");
										
//										 调入调出时间判断
										
										if(bvos1[0].getDenddate() != null){
											if(bvos1[0].getDstartdate().compareTo(showdate)<=0
													&& bvos1[0].getDenddate().compareTo(showdate)>=0){
												
												errorMsg.append(paibanweekvo.getVdate()+","+getPsnName(pk_psndoc)+" 漏排班\n");
												
											}
										}else{
											if(bvos1[0].getDstartdate().compareTo(showdate)<=0){
												
												errorMsg.append(paibanweekvo.getVdate()+","+getPsnName(pk_psndoc)+" 漏排班\n");
												
											}
										}
										
										
										
									}
									
									
									
								}
							}
							
						}
						
					}
				}
			}else{
				for(int j=1;j<8;j++){
					if(paibanweekvo.getAttributeValue("pk_bb"+j) == null){
						
						UFDate showdate = new UFDate(paibanweekvo.getVdate().substring(0, 10)).getDateAfter(j-1);
						// 			
						
						DeptKqBVO[] bvos1 = (DeptKqBVO[])HYPubBO_Client.queryByCondition(DeptKqBVO.class,
								" isnull(dr,0)=0 and pk_psndoc='"+pk_psndoc+"' and pk_dept='"+pk_deptdoc+"'  and (denddate is null or denddate>='"+begin+"') and dstartdate<='"+end+"' order by dstartdate desc ");
		
						if(bvos1 !=null){
							if(bvos1.length >0){
								
								
									if(!list_zb1.contains(pk_psndoc+showdate)){// 不是值班
										//errorMsg.append(paibanweekvo.getVdate()+","+getPsnName(pk_psndoc)+" 漏排班\n");
										
//										 调入调出时间判断
										
										if(bvos1[0].getDenddate() != null){
											if(bvos1[0].getDstartdate().compareTo(showdate)<=0
													 && bvos1[0].getDenddate().compareTo(showdate)>=0){
												
												errorMsg.append(paibanweekvo.getVdate()+","+getPsnName(pk_psndoc)+" 漏排班\n");
													
											}
										}else{
											if(bvos1[0].getDstartdate().compareTo(showdate)<=0){
												
												errorMsg.append(paibanweekvo.getVdate()+","+getPsnName(pk_psndoc)+" 漏排班\n");
													
											}
										}
										
										
										
									}
								
								
								
							}
						}
						
					}
				}
			}
			
			
		}
		
		ArrayList<SuperVO> listnew = new ArrayList<SuperVO>();
		ArrayList<SuperVO> listupdate = new ArrayList<SuperVO>();
		ArrayList<SuperVO> listdel = new ArrayList<SuperVO>();
		for(PaibanWeekVO vo:list_vo){
			if(vo.getPrimaryKey()!=null&&vo.getPrimaryKey().trim().length()>0){
				listupdate.add(vo);
			}else{
				listnew.add(vo);
			}
		}
		if(errorMsg.length() == 0 || "".equals(errorMsg.toString())){
			Ihrppub impl = (Ihrppub)NCLocator.getInstance().lookup(Ihrppub.class.getName());
			impl.saveVOs(listdel,listupdate,listnew);
		}
		
		return errorMsg;
	}
	
	
	public String getPsnName(String pk_psndoc) throws BusinessException{
		IUAPQueryBS service = (IUAPQueryBS)NCLocator.getInstance().lookup(IUAPQueryBS.class.getName());
		String strSQL = "select  b.psnname"
							+" from bd_psndoc b"
							+"    where b.pk_psndoc  ='" + pk_psndoc+"' and b.dr=0";
		String psnname = "";
		
		ArrayList<PaibanWeekVO> list_vo = new ArrayList<PaibanWeekVO>();
		Vector o1 = (Vector) service.executeQuery(strSQL,new VectorProcessor());
		if (o1.size() > 0 && o1 != null) {
			for (int i = 0; i < o1.size(); i++) {
				psnname = new String(((Vector) o1.elementAt(i)).elementAt(0) != null ? ((Vector) o1.elementAt(i)).elementAt(0).toString() : ""); 
			}
		}
		return psnname;
	}
	
	public String getUploadStatus(String vdate,ArrayList<String> list) throws BusinessException{
		// 查询部门是否已经上传，如果已经上传,再查询是否退回，退回则可以上传，否则不能上传;没有上传直接上传;
		String upstatus = "2";//未上传
		String begin = vdate.substring(0, 10);
		String end =vdate.substring(11, 21);
		String sql ="select distinct pk_dept from trtam_paiban_b where isnull(dr,0)=0 and pk_corp='"+_getCorp().getPrimaryKey()+"' and isnull(biszb,'N')='N' "+HRPPubTool.formInSQL("pk_dept", list)+" and ddate<='"+end+"' and ddate>='"+begin+"' ";
		IUAPQueryBS service = (IUAPQueryBS)NCLocator.getInstance().lookup(IUAPQueryBS.class.getName());
		
		Vector o1 = (Vector) service.executeQuery(sql,new VectorProcessor());
		ArrayList<String> list_dept = new ArrayList<String>();
		if (o1.size() > 0 && o1 != null) {
			for (int i = 0; i < o1.size(); i++) {
				String pk_dept = new String(((Vector) o1.elementAt(i)).elementAt(0) != null ? ((Vector) o1.elementAt(i)).elementAt(0).toString() : ""); 
				list_dept.add(pk_dept);
			}
		}
		// 已经上传是否退回
		if (list_dept.size() > 0 && list_dept != null) {
			upstatus = "3";//已经上传未审核
		}
		
		String pbsql = "select distinct pk_dept,vbillstatus from trtam_paiban_msg where  dr=0 "+HRPPubTool.formInSQL("pk_dept", list) +" and ddate<='"+end+"' and ddate>='"+begin+"'";
		Vector o2 = (Vector) service.executeQuery(pbsql,new VectorProcessor());
		
		ArrayList<String> list_deptstatus = new ArrayList<String>();
		if (o2.size() > 0 && o2 != null) {
			for (int j = 0; j < o2.size(); j++) {
				String pk_dept = new String(((Vector) o2.elementAt(j)).elementAt(0) != null ? ((Vector) o2.elementAt(j)).elementAt(0).toString() : ""); 
				String vbillstatus = new String(((Vector) o2.elementAt(j)).elementAt(1) != null ? ((Vector) o2.elementAt(j)).elementAt(1).toString() : ""); 
				
				if(vbillstatus.equals("0")){
					upstatus = "0";// 审批退回
					if(!list_deptstatus.contains(upstatus)){
						list_deptstatus.add(upstatus);
					}
				}else if(vbillstatus.equals("1")){
					upstatus = "1";// 审批通过
					if(!list_deptstatus.contains(upstatus)){
						list_deptstatus.add(upstatus);
					}
				}else if(vbillstatus.equals("3")){
					upstatus = "5";// 取消审批
					if(!list_deptstatus.contains(upstatus)){
						list_deptstatus.add(upstatus);
					}
				}
			}
		}
		
		if (list_deptstatus != null) {
			if(list_deptstatus.size() >= 2){
				upstatus="4";// 有退回或审批通过
			}else if(list_deptstatus.size() == 1){
				upstatus = list_deptstatus.get(0);
			}
			
		}
		return upstatus;
	}
}
