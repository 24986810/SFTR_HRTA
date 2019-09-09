/**
 * 
 */
package nc.ui.tam.tongren003b;

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
import nc.itf.uap.IUAPQueryBS;
import nc.itf.uap.bd.def.IDefdoc;
import nc.jdbc.framework.processor.BeanListProcessor;
import nc.jdbc.framework.processor.VectorProcessor;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.bill.BillEditEvent;
import nc.ui.pub.bill.BillItem;
import nc.ui.tam.tongren003.PaibanWeek;
import nc.ui.trade.bill.ICardController;
import nc.ui.trade.business.HYPubBO_Client;
import nc.ui.trade.button.IBillButton;
import nc.ui.trade.card.BillCardUI;
import nc.ui.trade.card.CardEventHandler;
import nc.uif.pub.exception.UifException;
import nc.vo.bd.b38.BdDefVO;
import nc.vo.bd.def.DefdocVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.tam.tongren.power.UserClassDeptVO;
import nc.vo.tam.tongren.power.UserClassTypeVO;
import nc.vo.tam.tongren.power.UserDeptVO;
import nc.vo.tam.tongren001.DeptKqBVO;
import nc.vo.tam.tongren001.DeptKqVO;
import nc.vo.tam.tongren003.PaiBanAuditMsg;
import nc.vo.tam.tongren003.PaiBanAuditReason;
import nc.vo.tam.tongren003.PaiPanReportVO;
import nc.vo.tam.tongren003.PaibanWeekVO;
import nc.vo.tam.tongren007.ZhibanWeekVO;
import nc.vo.tbm.tbm_029.BclbHeaderVO;

/**
 * @author 28729
 *
 */
public class ClientQueryUI2 extends BillCardUI {
	private ArrayList<String> list_dept = new ArrayList<String>();
	private UFBoolean bisaudit = new UFBoolean(false);
	private String allselect = "";
	private String operatetype = "";// 操作人员类型，是门办还是医务
	private ArrayList<String> defdoclist = null;
	private HashMap<String,PaiPanReportVO> mapall = null;
	
	
	public HashMap<String, PaiPanReportVO> getMapall() {
		return mapall;
	}

	public void setMapall(HashMap<String, PaiPanReportVO> mapall) {
		this.mapall = mapall;
	}

	public String getAllselect() {
		return allselect;
	}

	public void setAllselect(String allselect) {
		this.allselect = allselect;
	}

	public UFBoolean getBisaudit() {
		return bisaudit;
	}

	public void setBisaudit(UFBoolean bisaudit) {
		this.bisaudit = bisaudit;
	}
	private String vperiod = null;
	public String getVperiod() {
		return vperiod;
	}

	public void setVperiod(String vperiod) {
		this.vperiod = vperiod;
	}

	public ArrayList<String> getList_dept() {
		return list_dept;
	}

	public void setList_dept(ArrayList<String> list_dept) {
		this.list_dept = list_dept;
	}

	/**
	 * 
	 */
	public ClientQueryUI2() {
		// TODO Auto-generated constructor stub
		getBillCardPanel().getHeadItem("bisshowtype").setEnabled(true);
		getBillCardPanel().getHeadItem("bisshowtype").setValue("班别");
		getButtonManager().getButton(IBillButton.CancelAudit).setName("取消审核");
		getButtonManager().getButton(IBillButton.Print).setName("导出");
		getButtonManager().getButton(IBillButton.Print).setEnabled(true);
		getButtonManager().getButton(IBillButton.Edit).setName("退回");
		
		getButtonManager().getButton(IBillButton.Audit).setEnabled(false);
		getButtonManager().getButton(IBillButton.CancelAudit).setEnabled(false);
		updateButtons();
		
		try {
			defdoclist = getDefdoc();
			initUnAuditData();
			
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
	public ClientQueryUI2(String pk_corp, String pk_billType,
			String pk_busitype, String operater, String billId) {
		super(pk_corp, pk_billType, pk_busitype, operater, billId);
		// TODO Auto-generated constructor stub
	}
	@Override
	protected ICardController createController() {
		// TODO Auto-generated method stub
		return new ClientQueryCtrl2();
	}
	@Override
	protected CardEventHandler createEventHandler() {
		// TODO Auto-generated method stub
		return new EventQueryHandler2(this,this.createController());
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
		
		if(e.getPos() == BODY && e.getKey().equals("flag")){
			Object temp = getBillCardPanel().getBodyValueAt(e.getRow(), "flag"); 
			String pk_seldeptdoc = (String) getBillCardPanel().getBillModel().getValueAt(e.getRow(),"pk_dept");
			String pk_selparent = "";
			ArrayList<String> list = new ArrayList<String>();
			ArrayList<String> list_billstatus = new ArrayList<String>();
			
			
			
			// 查找上级科室
			try {
				DeptKqVO[] kqdeptvos = (DeptKqVO[])HYPubBO_Client.queryByCondition(DeptKqVO.class, " isnull(dr,0)=0 and pk_corp='"+_getCorp().getPrimaryKey()+"' and pk_dept='"+pk_seldeptdoc+"'");
			
				if(kqdeptvos[0].getPk_parent() == null){
					pk_selparent = pk_seldeptdoc;
				}else{
					pk_selparent = kqdeptvos[0].getPk_parent();
				}
					
				
				if(temp != null && temp.toString().equals("false")){
					int rowcount = getBillCardPanel().getBillTable().getRowCount();
					for(int i = 0;i < rowcount;i++){
						String pk_currdept = (String) getBillCardPanel().getBillModel().getValueAt(i,"pk_dept");
						String pk_currparent = "";
						
						DeptKqVO[] currkqdeptvos = (DeptKqVO[])HYPubBO_Client.queryByCondition(DeptKqVO.class, " isnull(dr,0)=0 and pk_corp='"+_getCorp().getPrimaryKey()+"' and pk_dept='"+pk_currdept+"'");
						if(currkqdeptvos.length > 0){
							if( currkqdeptvos[0].getPk_parent() == null){
								pk_currparent = pk_currdept;
							}else{
								pk_currparent = currkqdeptvos[0].getPk_parent();
							}
						}
						if(pk_selparent.equals(pk_currparent)){
							getBillCardPanel().getBillModel().setValueAt("N", i, "flag");
						}
					}
				}else{
					int rowcount = getBillCardPanel().getBillTable().getRowCount();
					for(int i = 0;i < rowcount;i++){
						String pk_currdept = (String) getBillCardPanel().getBillModel().getValueAt(i,"pk_dept");
						String pk_currparent = "";
						
						DeptKqVO[] currkqdeptvos = (DeptKqVO[])HYPubBO_Client.queryByCondition(DeptKqVO.class, " isnull(dr,0)=0 and pk_corp='"+_getCorp().getPrimaryKey()+"' and pk_dept='"+pk_currdept+"'");
						if(currkqdeptvos.length > 0){
							if( currkqdeptvos[0].getPk_parent() == null){
								pk_currparent = pk_currdept;
							}else{
								pk_currparent = currkqdeptvos[0].getPk_parent();
							}
						}
						
						if(pk_selparent.equals(pk_currparent)){
							getBillCardPanel().getBillModel().setValueAt("Y", i, "flag");
							if(!list.contains(pk_currdept)){
								list.add(pk_currdept);
							}	
							
							String begindate;
							String enddate;
							if(vperiod==null||vperiod.trim().length()<=0){
								int year = _getDate().getYear();
								int month = _getDate().getMonth()+1;
								String monthv = "";
								if(month==13){
									year = year+1;
									monthv = year+"-01";
								}else if(month<10){
									monthv = year+"-0"+month;
								}else{
									monthv = year+"-"+month;
								}
								vperiod = monthv;
							}
							
							
							
							begindate = vperiod+"-01";
							int day = 0;
							if(getDay(begindate)==1){
								day = 8;
							}else{
								day = getDay(begindate);
							}
							
							String enddate1 = getEndDay(begindate);
							int numweek = getWeek(enddate1);
							int numday = getDay(enddate1);// 最后一天是周天，取当月所有天数
							int oneday = getDay(begindate);
							
							int numdays = 0;
							if(numday == 0){
								numdays = new UFDate(begindate).getDaysMonth();
							}else if(numday != 0){
								if(oneday !=1){
									numweek = numweek-1;
								}
								numdays = numweek * 7;
							}
							int days = numdays ;
							
							String pk_deptdoc = (String) getBillCardPanel().getBillModel().getValueAt(i,"pk_dept");
							String pk_psndoc = (String) getBillCardPanel().getBillModel().getValueAt(i,"pk_psndoc");
							String vdate = getBillCardPanel().getHeadItem("ddate").getValueObject().toString();
							for(int j=1;j<=days+1;j++){
								BillItem items = getBillCardPanel().getBillModel().getItemByKey("vbbnames"+j);
								String vddate = items.getName().substring(0, 10);
								
								String pk_bb = getBillCardPanel().getBillModel().getValueAt(i, "pk_bb"+j+"")!=null?
										getBillCardPanel().getBillModel().getValueAt(i, "pk_bb"+j+"").toString().trim():null;
									if(pk_bb!=null&&pk_bb.trim().length()>0){
											
										String[] values = pk_bb.split(",");
										for(String value:values){
											String sqlwhere = " vperiod='"+vdate+"'and pk_dept='"+pk_deptdoc+"' and pk_psndoc='"+pk_psndoc+"' and pk_bb='"+value+"' and ddate='"+vddate+"'";
											PaiBanAuditMsg[] msgvos = (PaiBanAuditMsg[])HYPubBO_Client.queryByCondition(PaiBanAuditMsg.class, sqlwhere);
											if(msgvos != null){
													if(msgvos.length > 0){
														if(!list_billstatus.contains(""+msgvos[0].getVbillstatus())){
															list_billstatus.add(""+msgvos[0].getVbillstatus());
														}
														
													}else{
														//未查到审核记录
														if(!list_billstatus.contains("9")){
															list_billstatus.add("9");
														}
														
													}
												}
											}

									}
							}
						}
						
						
					}
					if(list_billstatus.size() >1){// 有可能有已审或未审
						getButtonManager().getButton(IBillButton.Audit).setEnabled(true);
						getButtonManager().getButton(IBillButton.CancelAudit).setEnabled(true);
						getButtonManager().getButton(IBillButton.Edit).setEnabled(true);
						updateButtons();
					}else if(list_billstatus.size() == 1){
						if(list_billstatus.get(0).equals("1")){// 审核通过
							getButtonManager().getButton(IBillButton.Audit).setEnabled(false);
							getButtonManager().getButton(IBillButton.CancelAudit).setEnabled(true);
							getButtonManager().getButton(IBillButton.Edit).setEnabled(false);
							updateButtons();
						}else if(list_billstatus.get(0).equals("0")){//退回
							getButtonManager().getButton(IBillButton.Audit).setEnabled(false);
							getButtonManager().getButton(IBillButton.CancelAudit).setEnabled(false);
							getButtonManager().getButton(IBillButton.Edit).setEnabled(false);
							updateButtons();
						}else if(list_billstatus.get(0).equals("3")){// 取消审核
							getButtonManager().getButton(IBillButton.Audit).setEnabled(false);
							getButtonManager().getButton(IBillButton.CancelAudit).setEnabled(false);
							getButtonManager().getButton(IBillButton.Edit).setEnabled(true);
							updateButtons();
						}else if(list_billstatus.get(0).equals("9")){// 未审核
							getButtonManager().getButton(IBillButton.Audit).setEnabled(true);
							getButtonManager().getButton(IBillButton.CancelAudit).setEnabled(false);
							getButtonManager().getButton(IBillButton.Edit).setEnabled(true);
							updateButtons();
						}
					}
					
				}
				
				//
				
				
//				int days = new UFDate(begindate).getDaysMonth()+(day - 2);
				
//				enddate = vperiod+"-"+days+"";
//				String vdate = begindate +"至"+enddate;
//				String upstatus = getUploadStatus(vdate,list);
//				
//				if(upstatus.equals("1")){
//					getButtonManager().getButton(IBillButton.Audit).setEnabled(false);
//					getButtonManager().getButton(IBillButton.CancelAudit).setEnabled(true);
//					getButtonManager().getButton(IBillButton.Edit).setEnabled(false);
//					updateButtons();
//					//MessageDialog.showErrorDlg(null, "提示","上传失败：已经审核不能上传!");
//					
//				}else if(upstatus.equals("0")){
//					//MessageDialog.showErrorDlg(null, "提示","上传失败：已经上传不能再次上传，需审核员退回后才能重新上传!");
//					getButtonManager().getButton(IBillButton.Audit).setEnabled(false);
//					getButtonManager().getButton(IBillButton.CancelAudit).setEnabled(false);
//					getButtonManager().getButton(IBillButton.Edit).setEnabled(false);
//					updateButtons();
//					
//				}else if(upstatus.equals("4")){
//					//MessageDialog.showErrorDlg(null, "提示","上传失败：已经有审批通过或退回，只能选退回科室后再上传!");
//					getButtonManager().getButton(IBillButton.Audit).setEnabled(true);
//					getButtonManager().getButton(IBillButton.CancelAudit).setEnabled(true);
//					getButtonManager().getButton(IBillButton.Edit).setEnabled(true);
//					updateButtons();
//				}else if(upstatus.equals("2")){
//					getButtonManager().getButton(IBillButton.Audit).setEnabled(false);
//					getButtonManager().getButton(IBillButton.CancelAudit).setEnabled(false);
//					getButtonManager().getButton(IBillButton.Edit).setEnabled(false);
//					updateButtons();
//				}else if(upstatus.equals("3")){
//					getButtonManager().getButton(IBillButton.Audit).setEnabled(true);
//					getButtonManager().getButton(IBillButton.CancelAudit).setEnabled(false);
//					getButtonManager().getButton(IBillButton.Edit).setEnabled(true);
//					updateButtons();
//				}else if(upstatus.equals("5")){
//					getButtonManager().getButton(IBillButton.Audit).setEnabled(false);
//					getButtonManager().getButton(IBillButton.CancelAudit).setEnabled(false);
//					getButtonManager().getButton(IBillButton.Edit).setEnabled(true);
//					updateButtons();
//				}else if(upstatus.equals("6")){
//					getButtonManager().getButton(IBillButton.Audit).setEnabled(true);
//					getButtonManager().getButton(IBillButton.CancelAudit).setEnabled(true);
//					getButtonManager().getButton(IBillButton.Edit).setEnabled(true);
//					updateButtons();
//				}
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
		nc.vo.bd.defref.DefdocVO[] defvos2= (nc.vo.bd.defref.DefdocVO[]) HYPubBO_Client.queryByCondition(nc.vo.bd.defref.DefdocVO.class,"(bd_defdoc.pk_defdoclist = '000154100000001119NG' and pk_corp in ('1002', '0001')) and (sealflag is null or sealflag <> 'Y') and docname like '%门诊%'");
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
		UFDate begin = new UFDate(vdate+"-01");
		UFDate end = new UFDate(vdate+"-"+begin.getDaysMonth());
		
		String enddate1 = getEndDay(begin.toString());
		int numweek = getWeek(enddate1);
		int numday = getDay(enddate1);// 最后一天是周天，取当月所有天数
		int oneday = getDay(begin.toString());
		
		int numdays = 0;
		if(numday == 0){
			numdays = new UFDate(begin.toString()).getDaysMonth();
		}else if(numday != 0){
			if(oneday !=1){
				numweek = numweek-1;
			}
			numdays = numweek * 7;
		}
		
		int days = numdays;//new UFDate().getDaysBetween(begin, end);
		for(int i=0;i<rowcount;i++){
			String pk_deptdoc = (String) getBillCardPanel().getBillModel().getValueAt(i,"pk_dept");
			String pk_psndoc = (String) getBillCardPanel().getBillModel().getValueAt(i,"pk_psndoc");

			for(int j=1;j<=days+1;j++){
				BillItem items = getBillCardPanel().getBillModel().getItemByKey("vbbnames"+j);
				String vddate = items.getName().substring(0, 10);
				String pk_bb = getBillCardPanel().getBillModel().getValueAt(i, "pk_bb"+j+"")!=null?
						getBillCardPanel().getBillModel().getValueAt(i, "pk_bb"+j+"").toString().trim():null;
						if(pk_bb!=null&&pk_bb.trim().length()>0){
							String[] values = pk_bb.split(",");
							String vbbname = "";
							String qz ="<html>";
							String hz = "</html>";
							for(String value:values){
								BclbHeaderVO bclbvo = map_bclb.get(value);
								// 查询班别是否已经审核
								
								
								String color = "<font color=black>";
								if(bclbvo!=null){
									//
									String keyall = pk_deptdoc+pk_psndoc+value+vddate;			
									PaiPanReportVO reptvo = mapall.get(keyall);
									if(reptvo!= null){
										if("1".equals(reptvo.getVbillstatus())){
											color = "<font color=green>";
										}
									}
//									String sqlwhere = " vperiod='"+vdate+"'and pk_dept='"+pk_deptdoc+"' and pk_psndoc='"+pk_psndoc+"' and pk_bb='"+value+"' and vbillstatus='1' and ddate='"+vddate+"'";
//									PaiBanAuditMsg[] msgvos = (PaiBanAuditMsg[])HYPubBO_Client.queryByCondition(PaiBanAuditMsg.class, sqlwhere);
//									if(msgvos != null){
//										if(msgvos.length > 0){
//											color = "<font color=green>";
//										}
//									}
									// 医务
									if(operatetype.equals("YW")){
										for(int k=0;k<defvos2.length;k++){
											if(defvos2[k].pk_defdoc.equals(value)){
												color = "<font color=purple>";
											}
										}
										
										if(bclbvo.getAttributeValue(key).toString().trim().contains("门诊") || defdoclist.contains(bclbvo.getPk_dd())){
											if(!bclbvo.getAttributeValue(key).toString().trim().contains("肠道门诊")
													|| !bclbvo.getAttributeValue(key).toString().trim().contains("发热门诊")){
												color = "<font color=purple>";
											}
											
										}
										vbbname+=""+(bclbvo.getAttributeValue(key)!=null?color + bclbvo.getAttributeValue(key).toString().trim():"")+"/";
									}else{
										vbbname+=""+(bclbvo.getAttributeValue(key)!=null?color + bclbvo.getAttributeValue(key).toString().trim():"")+"/";
									}
								}
							}
							vbbname = vbbname.substring(0,vbbname.length()-1);
							getBillCardPanel().getBillModel().setValueAt(qz + vbbname +hz, i, "vbbnames"+j+"");
						}
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
	
	public HashMap<String,PaiBanAuditReason> getPaiBanAuditReasonMap(String wheresql) throws UifException{
		HashMap<String,PaiBanAuditReason> map = new HashMap<String, PaiBanAuditReason>();
		PaiBanAuditReason[] reasons = (PaiBanAuditReason[])HYPubBO_Client.queryByCondition(PaiBanAuditReason.class, " 1=1 "+wheresql);
		if(reasons!=null&&reasons.length>0){
			for(PaiBanAuditReason reasonvo:reasons){
				map.put(reasonvo.getPk_dept()+reasonvo.getPk_psndoc(), reasonvo);
			}
		}
		
		return map;
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
	
	public static String getEndDay(String str) throws ParseException{
		SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd");
		Date date =sdf.parse(str);

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.DAY_OF_MONTH,calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
		String lastDay= sdf.format(calendar.getTime());
		
		return lastDay;
	}
	
	public static int getWeek(String str) throws Exception{
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
	
	public void initUnAuditData() throws Exception{
		String begindate;
		String enddate;
		DeptKqVO[] kqdeptvos = (DeptKqVO[])HYPubBO_Client.queryByCondition(DeptKqVO.class, " isnull(dr,0)=0 and pk_corp='"+_getCorp().getPrimaryKey()+"' ");
		HashMap<String,DeptKqVO> map_dept = new HashMap<String, DeptKqVO>();
		for(DeptKqVO deptvo:kqdeptvos){
			map_dept.put(deptvo.getPrimaryKey(), deptvo);
		}
		if(vperiod==null||vperiod.trim().length()<=0){
			int year = _getDate().getYear();
			int month = _getDate().getMonth()+1;
			String monthv = "";
			if(month==13){
				year = year+1;
				monthv = year+"-01";
			}else if(month<10){
				monthv = year+"-0"+month;
			}else{
				monthv = year+"-"+month;
			}
			vperiod = monthv;
		}
		
		
		
		begindate = vperiod+"-01";
		int day = 0;
		if(getDay(begindate)==1){
			day = 8;
		}else{
			day = getDay(begindate);
		}
		
		String enddate1 = getEndDay(begindate);
		int numweek = getWeek(enddate1);
		int numday = getDay(enddate1);// 最后一天是周天，取当月所有天数
		int oneday = getDay(begindate);
		
		int numdays = 0;
		if(numday == 0){
			numdays = new UFDate(begindate).getDaysMonth();
		}else if(numday != 0){
			if(oneday !=1){
				numweek = numweek-1;
			}
			numdays = numweek * 7;
		}
		
//		int days = new UFDate(begindate).getDaysMonth()+(day - 2);
		int days = numdays ;
		enddate = vperiod+"-"+days+"";

		String wheredept = 	" and trtam_deptdoc_kq_b.pk_dept in (select pk_dept from trtam_deptdoc_kq where isnull(dr,0)=0 and  pk_corp='" + _getCorp().getPrimaryKey()+ "' and isnull(bisseal,'N')='N' and pk_dept in (select pk_docid from bd_tr_userclasspower where isnull(dr,0)=0 and pk_user='"+_getOperator()+"' and powertype=0 and  pk_corp='"+_getCorp().getPrimaryKey()+"')) ";

		String wheresql = " isnull(dr,0)=0 "+wheredept+" and ((dstartdate<'"+begindate+"' and denddate>='"+enddate+"') or (dstartdate>='"+begindate+"' and dstartdate<='"+enddate+"' ))  ";

		if(list_dept==null||list_dept.size()<=0){
			UserClassDeptVO[] deptvos = (UserClassDeptVO[])HYPubBO_Client.queryByCondition(UserClassDeptVO.class, " isnull(dr,0)=0 and pk_user='"+_getOperator()+"' and powertype=0 and  pk_corp='"+_getCorp().getPrimaryKey()+"' ");
			if(deptvos!=null&&deptvos.length>0){
				for(UserClassDeptVO deptvo:deptvos){
					list_dept.add(deptvo.getPk_docid());
				}
			}
		}
//		if(bisaudit==null||!bisaudit.booleanValue()){
//			PaiBanAuditMsg[] oldvos = (PaiBanAuditMsg[])HYPubBO_Client.queryByCondition(PaiBanAuditMsg.class, " vperiod='"+vperiod+"' and isnull(dr,0)=0 and pk_corp='"+_getCorp().getPrimaryKey()+"' ");
//			if(oldvos!=null&&oldvos.length>0){
//				for(PaiBanAuditMsg oldvo:oldvos){
//					list_dept.remove(oldvo.getPk_dept());
//				}
//			}
//		}
		if(bisaudit.booleanValue()){
			wheresql+=" and trtam_deptdoc_kq_b.pk_dept in (select pk_dept from trtam_paiban_msg where vperiod='"+vperiod+"' and isnull(dr,0)=0 and pk_corp='"+_getCorp().getPrimaryKey()+"' )";
		}
		
		
		// 增加考勤人员班别审核权限
		String whereclasstype1= "",whereclasstype2= "",whereclasstype3= "",whereclasstype4= "",whereclasstype5= "";
		UserClassTypeVO[] kqclasstypevos = (UserClassTypeVO[])HYPubBO_Client.queryByCondition(UserClassTypeVO.class, " isnull(dr,0)=0 and pk_user='"+_getOperator()+"' and powertype=0 and  pk_corp='"+_getCorp().getPrimaryKey()+"'  ");
		
		for(UserClassTypeVO classtypevo:kqclasstypevos){
			if(classtypevo.getPk_docid().equals("1")){// 医务
				operatetype = "YW";
				whereclasstype2 = "  TRTAM_PAIBAN_DETAIL.pk_bb in (select pk_bclbid from tbm_bclb where (lbbm  like '10%' or lbbm  like '99%') and dr =0)  ";
			}else if(classtypevo.getPk_docid().equals("2")){//门办
				operatetype="";
				whereclasstype2 = "  TRTAM_PAIBAN_DETAIL.pk_bb in (select pk_bclbid from trtam_filter_bclb where (lbmc not like '%肠道门诊%' or lbmc not like '%发热门诊%') )";
			}else if(classtypevo.getPk_docid().equals("3")){// 护理
				operatetype="";
				whereclasstype2 = "  TRTAM_PAIBAN_DETAIL.pk_bb in (select pk_bclbid from tbm_bclb where (lbbm  like '20%' or lbbm  like '99%')  and dr =0)  ";
			}else if(classtypevo.getPk_docid().equals("4")){// 其他
				operatetype="";
				whereclasstype2 = "  TRTAM_PAIBAN_DETAIL.pk_bb in (select pk_bclbid from tbm_bclb where (lbbm  like '50%' or lbbm  like '99%' or lbbm  like '30%')  and dr =0)  ";
			}
			
		}
		
		String whereclasstype = " and (" + whereclasstype1 + whereclasstype2 + whereclasstype3 +whereclasstype4 +whereclasstype5 +") ";
		
		if(whereclasstype.trim().length() == 6){
//			whereclasstype = " and TRTAM_PAIBAN_DETAIL.pk_bb in (select pk_bclbid from tbm_bclb where lbbm not like '20%' and dr =0)  ";
			whereclasstype = " and 1=1  ";
		}
		
		if(list_dept!=null&&list_dept.size()>0){
			wheresql += ""+HRPPubTool.formInSQL("pk_dept", list_dept)+" ";
		}else{
			wheresql += " and 1=2 ";
		}
		// 考勤人员
		DeptKqBVO[] kqbvos = (DeptKqBVO[])HYPubBO_Client.queryByCondition(DeptKqBVO.class, wheresql);
		HashMap<String,PaiPanReportVO> map = new HashMap<String, PaiPanReportVO>();
		mapall = new HashMap<String, PaiPanReportVO>();
		if(kqbvos!=null&&kqbvos.length>0){
			for(DeptKqBVO kqbvo:kqbvos){
				PaiPanReportVO vo = new PaiPanReportVO();
				vo.setBispaiban(new UFBoolean(false));
				vo.setPk_dept(kqbvo.getPk_dept());
				vo.setPk_psndoc(kqbvo.getPk_psndoc());
				map.put(kqbvo.getPk_dept()+kqbvo.getPk_psndoc(), vo);
			}
		}

		// 班次类别
		IBclbDefining defin = NCLocator.getInstance().lookup(IBclbDefining.class);
		BclbHeaderVO[] bclbvos =  defin.queryBclb029AllBclbHeader(_getCorp().getPrimaryKey(), null);
		ArrayList<String> list_kqbb = new ArrayList<String>();
		HashMap<String,BclbHeaderVO> map_bclb = new HashMap<String, BclbHeaderVO>();
		for(BclbHeaderVO bclbvo:bclbvos){
			if(bclbvo.getLbbm().startsWith("99")){
				list_kqbb.add(bclbvo.getPrimaryKey());
			}
			map_bclb.put(bclbvo.getPrimaryKey(), bclbvo);
		}
		getBillCardPanel().getBillModel().clearBodyData();

		String sql = " SELECT distinct trtam_deptdoc_kq_b.pk_psndoc, TRTAM_PAIBAN_DETAIL.pk_bb, trtam_deptdoc_kq_b.pk_dept, TRTAM_PAIBAN_DETAIL.ddate, pk_bbz, pk_dd, biszb, TRTAM_PAIBAN_DETAIL.memo, vbillstatus,vbillstatus2,TRTAM_PAIBAN_DETAIL.uploadnum ";
		sql+=" FROM trtam_deptdoc_kq_b left join TRTAM_PAIBAN_DETAIL on trtam_deptdoc_kq_b.pk_psndoc = TRTAM_PAIBAN_DETAIL.pk_psndoc and trtam_deptdoc_kq_b.pk_dept = TRTAM_PAIBAN_DETAIL.pk_dept ";
//		sql+=" left join trtam_paiban_reason on TRTAM_PAIBAN_DETAIL.pk_dept = trtam_paiban_reason.pk_dept and TRTAM_PAIBAN_DETAIL.pk_psndoc = trtam_paiban_reason.pk_psndoc and trtam_paiban_reason.vperiod='"+vperiod+"' ";
		sql+=" and dclassperiod like '"+vperiod+"%' ";
		sql+= whereclasstype + 
		"  "
		+"  where 1=1 and bisnew='Y' and trtam_deptdoc_kq_b.dr=0 ";
		
//		if(!allselect.equals("ALL")){
//			if(bisaudit.booleanValue()){
//				sql+=" and trtam_deptdoc_kq_b.pk_psndoc in (select distinct pk_psndoc from trtam_paiban_msg where vperiod='"+vperiod+"' and isnull(dr,0)=0 and pk_corp='"+_getCorp().getPrimaryKey()+"' )";
//			}else if(!bisaudit.booleanValue()){
//				sql+=" and trtam_deptdoc_kq_b.pk_psndoc not in (select distinct pk_psndoc from trtam_paiban_msg where vperiod='"+vperiod+"' and isnull(dr,0)=0 and pk_corp='"+_getCorp().getPrimaryKey()+"' )";
//
//			}
//		}
		
		if(list_dept!=null&&list_dept.size()>0){
			sql += ""+HRPPubTool.formInSQL("trtam_deptdoc_kq_b.pk_dept", list_dept)+" ";
		}else{
			sql += " and 1=2 ";
		}
		sql+="  "+wheredept+" order by ddate";
		String deptname = "";
		if(list_dept!=null&&list_dept.size()>0){
			for(int i=0;i<list_dept.size();i++){
				deptname += (map_dept.get(list_dept.get(i)).getVname()+",");
			}
		}
		
		HashMap<String,PaiBanAuditReason> seasonmap = getPaiBanAuditReasonMap(HRPPubTool.formInSQL("pk_dept", list_dept)+" and vperiod='"+vperiod+"'");
		IUAPQueryBS bs= NCLocator.getInstance().lookup(IUAPQueryBS.class);
		ArrayList<PaiPanReportVO> list = (ArrayList<PaiPanReportVO>)bs.executeQuery(sql, new BeanListProcessor(PaiPanReportVO.class));
		if(list!=null&&list.size()>0){
			UFDate begin = list.get(0).getDdate();
			if(begin == null){
				begin = new UFDate(vperiod + "-01");
			}
			UFDate end = list.get(list.size()-1).getDdate();
			getBillCardPanel().setHeadItem("ddate", vperiod);
			getBillCardPanel().setHeadItem("deptname", deptname.substring(0,deptname.trim().length()-1));
			ArrayList<String> list_zb = new ArrayList<String>();
			ArrayList<String> list_kq = new ArrayList<String>();
			for(int i=0;i<list.size();i++){
				String key = list.get(i).getPk_dept()+list.get(i).getPk_psndoc();
				
				String keyall = list.get(i).getPk_dept()+list.get(i).getPk_psndoc()+list.get(i).getPk_bb()+list.get(i).getDdate();			
				mapall.put(keyall, list.get(i));
				
				PaiPanReportVO vo = map.get(key)!=null?map.get(key):new PaiPanReportVO();
				vo.setPk_dept(list.get(i).getPk_dept());
				vo.setPk_psndoc(list.get(i).getPk_psndoc());
				vo.setBispaiban(new UFBoolean(true));
				vo.setVbillstatus(list.get(i).getVbillstatus());
				vo.setUploadnum(list.get(i).getUploadnum());
				String reason = "";
				if(seasonmap.get(list.get(i).getPk_dept()+list.get(i).getPk_psndoc()) != null)
					reason=seasonmap.get(list.get(i).getPk_dept()+list.get(i).getPk_psndoc()).getReason();
				vo.setReason(reason);
				if(list.get(i).getMemo()!=null&&list.get(i).getMemo().trim().length()>0){
					if(vo.getMemo()==null||vo.getMemo().trim().length()<=0){
						vo.setMemo(list.get(i).getMemo().trim());
					}else{
						String[] memos = vo.getMemo().split(";&");
						ArrayList<String> list_memo = new ArrayList<String>();
						list_memo.addAll(Arrays.asList(memos));
						if(!list_memo.contains(list.get(i).getMemo().trim())){
							vo.setMemo(vo.getMemo()+";&"+list.get(i).getMemo().trim());
						}

					}
				}

				String pk_bb = list.get(i).getPk_bb();// 班别
				UFDate ddate = list.get(i).getDdate();
				int index = new UFDate().getDaysBetween(begin, ddate)+1;
				Object value = vo.getAttributeValue("pk_bb"+index+"");
				if(value==null||value.toString().trim().length()<=0){
					vo.setAttributeValue("pk_bb"+index+"", pk_bb);
				}else{
					vo.setAttributeValue("pk_bb"+index+"", value.toString().trim()+","+pk_bb);
				}
				Object value1 = vo.getAttributeValue("vbbnames"+index+"");
				if(value1==null||value1.toString().trim().length()<=0){
					if(pk_bb != null){
						vo.setAttributeValue("vbbnames"+index+"", map_bclb.get(pk_bb).getLbmc().trim());	
					}else{
						vo.setAttributeValue("vbbnames"+index+"", "");	
					}
					
				}else{
					vo.setAttributeValue("vbbnames"+index+"", value1.toString().trim()+"/"+ map_bclb.get(pk_bb).getLbmc().trim());
				}


				if(list.get(i).getBiszb()!=null&&list.get(i).getBiszb().booleanValue()){
					list_zb.add(key+list.get(i).getDdate());
				}
				if(list_kqbb.contains(list.get(i).getPk_bb())){
					list_kq.add(key+list.get(i).getDdate());
				}
				map.put(key, vo);
			}


			BillItem[] items = getBillCardPanel().getBodyItems();
			ArrayList<BillItem> list_item = new ArrayList<BillItem>();
			for(BillItem item :items){
				if(!item.getKey().startsWith("vbbname")&&(!item.getKey().startsWith("pk_bb"))){
					list_item.add(item);
					item.setForeground(3);
				}
			}
			for(int i=1;i<=days+1;i++){
				BillItem item = new BillItem();

				item.setKey("vbbnames"+i+"");
				item.setLength(200);
				item.setWidth(120);
				int week = begin.getDateAfter(i-1).getWeek();
				String name = "";
				if(week==0){
					name = "(星期日)";
				}else if(week==1){
					name = "(星期一)";
				}else if(week==2){
					name = "(星期二)";
				}else if(week==3){
					name = "(星期三)";
				}else if(week==4){
					name = "(星期四)";
				}else if(week==5){
					name = "(星期五)";
				}else if(week==6){
					name = "(星期六)";
				}
				item.setName(begin.getDateAfter(i-1).toString()+name);
				item.setDataType(BillItem.STRING);
				item.setNull(false);
				if(week==0||week==6){
					//						item.setForeground(nc.ui.bill.tools.ColorConstants.COLOR_PINK);
				}
				list_item.add(item);

				BillItem item1 = new BillItem();
				item1.setKey("pk_bb"+i+"");
				item1.setLength(200);
				item1.setWidth(120);
				item1.setShow(false);
				item1.setName(begin.getDateAfter(i-1).toString());
				item1.setDataType(BillItem.STRING);
				
				list_item.add(item1);
			}
			String[] keys = map.keySet().toArray(new String[0]);
			Arrays.sort(keys);
			ArrayList<PaiPanReportVO> list_data = new ArrayList<PaiPanReportVO>();
			for(String key:keys){
				list_data.add(map.get(key));
			}
			getBillCardPanel().getBillModel().setBodyItems(list_item.toArray(new BillItem[0]));
			getBillCardPanel().setBillData(getBillCardPanel().getBillData());
			getBillCardPanel().getBillTable().setHeaderHeight(30);
			getBillCardPanel().getBillModel().setBodyDataVO(list_data.toArray(new PaiPanReportVO[0]));
			getBillCardPanel().getBillModel().execLoadFormula();
			int rowcount = getBillCardPanel().getBillModel().getRowCount();
			for(int i=0;i<rowcount;i++){
				getBillCardPanel().getBillModel().setEnabled(true);
//				getBillCardPanel().getBillModel().setCellEditable(i, "flag", true);
//				getBillCardPanel().getBillModel().getItemByKey("flag").setEdit(true);
//				getBillCardPanel().getBillModel().getItemByKey("flag").setEnabled(true);
				String pk_psndoc = getBillCardPanel().getBillModel().getValueAt(i, "pk_psndoc").toString();
				String pk_dept = getBillCardPanel().getBillModel().getValueAt(i, "pk_dept").toString();
				UFBoolean bispaipan = getBillCardPanel().getBillModel().getValueAt(i, "bispaiban")!=null
				?new UFBoolean(getBillCardPanel().getBillModel().getValueAt(i, "bispaiban").toString()):new UFBoolean(false);
				if(bispaipan==null||!bispaipan.booleanValue()){
					int psncodeindex = getBillCardPanel().getBillModel().getBodyColByKey("psncode");
					int psnnameindex = getBillCardPanel().getBillModel().getBodyColByKey("psnname");
					getBillCardPanel().getBillModel().setBackground(Color.RED, i, psncodeindex);
					getBillCardPanel().getBillModel().setBackground(Color.RED, i, psnnameindex);
				}
				for(int j=1;j<=days+1;j++){
					int index = getBillCardPanel().getBillModel().getBodyColByKey("vbbnames"+(j)+"");
					UFDate showdate = begin.getDateAfter(j-1);
					if(list_zb.contains(pk_dept+pk_psndoc+showdate)){
						getBillCardPanel().getBillModel().setBackground(Color.pink, i, index);
					}
					if(list_kq.contains(pk_dept+pk_psndoc+showdate)){
						getBillCardPanel().getBillModel().setBackground(Color.yellow, i, index);
					}
				}
			}
			getButtonManager().getButton(IBillButton.Refresh).setEnabled(true);
			getButtonManager().getButton(IBillButton.SelAll).setEnabled(true);
			getButtonManager().getButton(IBillButton.SelNone).setEnabled(true);
			
//			this.setBillOperate(nc.ui.trade.base.IBillOperate.OP_EDIT);
			
			onshow();
		}
	}
	
	public ArrayList<String> getDefdoc() throws BusinessException{
		IUAPQueryBS service = (IUAPQueryBS)NCLocator.getInstance().lookup(IUAPQueryBS.class.getName());
		String strSQL = "select docname"
			+"  from bd_defdoc"
			+" where (bd_defdoc.pk_defdoclist = '000154100000001119NG' and"
			+"       pk_corp in ('1002', '0001'))"
			+"   and (sealflag is null or sealflag <> 'Y')"
			+"   and docname like '%门诊%'";
		
		ArrayList<String> list_vo = new ArrayList<String>();
		Vector o1 = (Vector) service.executeQuery(strSQL,new VectorProcessor());
		if (o1.size() > 0 && o1 != null) {
			for (int i = 0; i < o1.size(); i++) {
				String pk_defdoc = new String(((Vector) o1.elementAt(i)).elementAt(0) != null ? ((Vector) o1.elementAt(i)).elementAt(0).toString() : "");
				list_vo.add(pk_defdoc);
			}
		}
		
		return list_vo;
	}
	
	public ArrayList<String> getDefdoc1() throws BusinessException{
		IUAPQueryBS service = (IUAPQueryBS)NCLocator.getInstance().lookup(IUAPQueryBS.class.getName());
		String strSQL = "select docname"
			+"  from bd_defdoc"
			+" where (bd_defdoc.pk_defdoclist = '000154100000001119NG' and"
			+"       pk_corp in ('1002', '0001'))"
			+"   and (sealflag is null or sealflag <> 'Y')"
			+"   and docname like '%门诊%'";
		
		ArrayList<String> list_vo = new ArrayList<String>();
		Vector o1 = (Vector) service.executeQuery(strSQL,new VectorProcessor());
		if (o1.size() > 0 && o1 != null) {
			for (int i = 0; i < o1.size(); i++) {
				String pk_defdoc = new String(((Vector) o1.elementAt(i)).elementAt(0) != null ? ((Vector) o1.elementAt(i)).elementAt(0).toString() : "");
				list_vo.add(pk_defdoc);
			}
		}
		
		return list_vo;
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
		
		if (list_dept.size() > 0 && list_dept != null) {
			upstatus = "3";//已经上传未审核
		}
		
		String whereclasstype1= "",whereclasstype2= "",whereclasstype3= "",whereclasstype4= "",whereclasstype5= "";
		UserClassTypeVO[] kqclasstypevos = (UserClassTypeVO[])HYPubBO_Client.queryByCondition(UserClassTypeVO.class, " isnull(dr,0)=0 and pk_user='"+_getOperator()+"' and powertype=0 and  pk_corp='"+_getCorp().getPrimaryKey()+"'  ");
		
		for(UserClassTypeVO classtypevo:kqclasstypevos){
			if(classtypevo.getPk_docid().equals("1")){// 医务
				whereclasstype2 = " and pk_bb in (select pk_bclbid from tbm_bclb where (lbbm  like '10%' or lbbm  like '99%') and dr =0)  ";
			}else if(classtypevo.getPk_docid().equals("2")){//门办
				whereclasstype2 = " and pk_bb in (select pk_bclbid from trtam_filter_bclb where (lbmc not like '%肠道门诊%' or lbmc not like '%发热门诊%') )";
			}else if(classtypevo.getPk_docid().equals("3")){// 护理
				whereclasstype2 = " and pk_bb in (select pk_bclbid from tbm_bclb where (lbbm  like '20% or lbbm  like '99%'')  and dr =0)  ";
			}else if(classtypevo.getPk_docid().equals("4")){// 其他
				whereclasstype2 = "  and pk_bb in (select pk_bclbid from tbm_bclb where (lbbm  like '50%' or lbbm  like '99%' or lbbm  like '30%')  and dr =0)  ";
			}
			
		}
		
		String pbsql = "select distinct pk_dept,vbillstatus from trtam_paiban_msg where  dr=0 "+HRPPubTool.formInSQL("pk_dept", list) +" and ddate<='"+end+"' and ddate>='"+begin+"' " +whereclasstype2;
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
							upstatus = "5";// 取消审核
							if(!list_deptstatus.contains(upstatus)){
								list_deptstatus.add(upstatus);
							}
						}
					
					
				}
			
			
		}
		
		if(list_dept.size() != o2.size()){
			upstatus = "6";// 审批数量与上传数量不一样,则有部门未审核
			if(!list_deptstatus.contains(upstatus)){
				list_deptstatus.add(upstatus);
			}
		}
		
		if (list_deptstatus != null) {
			if(list_deptstatus.size() >= 2){
				for(int i=0;i<list_deptstatus.size();i++){
					if(list_deptstatus.get(i).equals("6")){
						upstatus="6";// 有部分未审核
					}
				}
				if(list_deptstatus.get(0).equals("1") || list_deptstatus.get(1).equals("0")){
					upstatus="4";// 有退回或审批通过
				}
				
			}else if(list_deptstatus.size() == 1){
				upstatus = list_deptstatus.get(0);
			}
			
		}
		return upstatus;
	}
}
