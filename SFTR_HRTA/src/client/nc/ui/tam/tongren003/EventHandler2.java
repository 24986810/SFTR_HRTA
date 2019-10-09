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
public class EventHandler2 extends CardEventHandler {

	protected int showtype = 804;
	private ArrayList<String> list_zb = new ArrayList<String>();
	/**
	 * @param billUI
	 * @param control
	 */
	public EventHandler2(BillCardUI billUI, ICardController control) {
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
		String vdate = getBillCardPanelWrapper().getBillCardPanel().getHeadItem("vdate").getValueObject().toString();
		UFDate enddate = new UFDate(vdate.substring(11));
		if(enddate.compareTo(enableStartdate)<0){
			MessageDialog.showHintDlg(this.getBillUI(), "提示", "当前周日期已不能再排班");
			return;
		}
		UFDate lastdate14 = curdate.getDateBefore(30);
		int addx = lastdate14.getWeek();
		UFDate addbegin = lastdate14.getDateBefore(addx-1);
		
		if(new UFDate(begindate1).compareTo(addbegin) <0){
			MessageDialog.showHintDlg(this.getBillUI(), "提示", "只能补当前时间前一个月的班");
			return;	
		}
		((ClientUI2)getBillUI()).setEditstate(1);
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
			//已经补过的班
			PaibanWeekVO[] weekvos = (PaibanWeekVO[])HYPubBO_Client.queryByCondition(PaibanWeekVO.class,
					"isnull(dr,0)=0 and pptype='1' and pk_corp='"+_getCorp().getPrimaryKey()+"' and vdate='"+vdate+"' "+HRPPubTool.formInSQL("pk_dept", list)+" ");
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
					" isnull(dr,0)=0 "+HRPPubTool.formInSQL("pk_dept", list)+"  and dstartdate>='"+addbegin+"' ");
			
			String beginDate = vdate.substring(0, 10);
			ArrayList<UFDate> dateList = new ArrayList<UFDate>(); //用来装时间
			UFDate date = new UFDate(beginDate);
			dateList.add(date);
			for(int i=1;i<7;i++){
				UFDate dateAfter = date.getDateAfter(i);
				dateList.add(dateAfter);
			}
			
			//部门map
			HashMap<String,DeptKqVO> deptKqVO = getDeptKqVO();
			
			if(bvos!=null&&bvos.length>0){
				for(DeptKqBVO bvo:bvos){
					//已经补过的班的人员pk list 不包含 部门中的人员pk
					if(!pk_list_psn.contains(bvo.getPk_psndoc())){
						PaibanWeekVO weekvo = new PaibanWeekVO();
						weekvo.setDr(0);
						weekvo.setPk_psndoc(bvo.getPk_psndoc());
						weekvo.setPk_dept(bvo.getPk_dept());
						weekvo.setVdate(vdate);
						weekvo.setPk_corp(_getCorp().getPrimaryKey());
						weekvo.setNgxs(map.get(bvo.getPk_psndoc()));
						weekvo.setNsygxs(map.get(bvo.getPk_psndoc()));
						/*PaibanWeekVO[] weekvosquery = (PaibanWeekVO[])HYPubBO_Client.queryByCondition(PaibanWeekVO.class,
								" vdate='"+vdate+"' and pk_psndoc='"+bvo.getPk_psndoc()+"' and pk_dept='"+bvo.getPk_dept()+"' "+HRPPubTool.formInSQL("pk_dept", list)+" ");*/
						
						String flag ="显示"; //标识
						
						for(int i=0;i<7;i++){
							//查询时间、人员条件下的排班  有一个有排班那就有排班，   
							//----已经补过的班   未排过班的人
							
							//1  按天 人员 查排班
							
						
							//1.1    科室
							String pk_dept = weekvo.getPk_dept();
							String string = deptKqVO.get(pk_dept).getVcode();
							String biaoshi="N";
							if( string.length()>=4 && ("1900").equals(string.substring(0, 4))==false){
								//不是护理科室
								biaoshi="Y";
							}else if(string.length()<4){
								//不是护理科室
								biaoshi="Y";
							}
							
							
							
							if(biaoshi=="N"){
								//护理科室
								PanbanWeekBVO[] weekbvosquery = (PanbanWeekBVO[])HYPubBO_Client.queryByCondition(PanbanWeekBVO.class,
										"biszb !='Y' and ddate='"+dateList.get(i).toString()+"' and pk_psndoc='"+bvo.getPk_psndoc()+"' and pk_dept='"+bvo.getPk_dept()+"' "+" ");
								
								if(weekbvosquery!=null && weekbvosquery.length>0){
									//有一天有班 就代表已经排过班了  就不显示
									flag ="不显示";
									break;
								}
								
							}else{
								//非护理科室
								UFDate date2 = dateList.get(i);
								UFDate dateBefore = date2.getDateBefore(date2.getDay()-1); //月初
								int daysMonth = date2.getDaysMonth();
								UFDate dateAfter = dateBefore.getDateAfter(daysMonth-1); //月末
								String vdateMonth = dateBefore.toString()+"至"+dateAfter.toString();
								PaibanWeekVO[] weekvosquery =(PaibanWeekVO[])HYPubBO_Client.queryByCondition(PaibanWeekVO.class,
										"pptype is null and  vdate='"+vdateMonth+"' and pk_psndoc='"+bvo.getPk_psndoc()+"' and pk_dept='"+bvo.getPk_dept()+"' "+" ");
							
								if(weekvosquery!=null && weekvosquery.length>0){
									//有排班记录 就代表已经排过班了  就不显示
									flag ="不显示";
									break;
								}
							}
							
						}
						
						if("显示".equals(flag)){
							list_vo.add(weekvo);
							pk_list_psn.add(bvo.getPk_psndoc());
						}
						
					}
				}
			}
			
			
			if(list_vo!=null&&list_vo.size()>0){
				//
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
//					if(list_audit.contains(pk_dept+showdate.toString().substring(0,7))){
//						int index = getBillCardPanelWrapper().getBillCardPanel().getBillModel().getBodyColByKey("vbbname"+(j)+"");
//						getBillCardPanelWrapper().getBillCardPanel().getBillModel().setCellEditable(i, "vbbname"+(j)+"", false);
//						getBillCardPanelWrapper().getBillCardPanel().getBillModel().setBackground(Color.LIGHT_GRAY, i, index);
//						if(list_zb.contains(pk_psndoc+showdate)){
//							getBillCardPanelWrapper().getBillCardPanel().getBillModel().setBackground(Color.pink, i, index);
//						}
//					}else{
						if(bvos[0].getDstartdate().compareTo(showdate)>0||(bvos[0].getDenddate()!=null&&bvos[0].getDenddate().compareTo(showdate)<0)){
							int index = getBillCardPanelWrapper().getBillCardPanel().getBillModel().getBodyColByKey("vbbname"+(j)+"");
							getBillCardPanelWrapper().getBillCardPanel().getBillModel().setCellEditable(i, "vbbname"+(j)+"", false);
							getBillCardPanelWrapper().getBillCardPanel().getBillModel().setBackground(Color.LIGHT_GRAY, i, index);
							if(list_zb.contains(pk_psndoc+showdate)){
								getBillCardPanelWrapper().getBillCardPanel().getBillModel().setBackground(Color.pink, i, index);
							}
						}
//					}
				}
			}
		}
		getBillCardPanelWrapper().getBillCardPanel().setBillData(getBillCardPanelWrapper().getBillCardPanel().getBillData());
		getBillCardPanelWrapper().getBillCardPanel().getBillTable().setHeaderHeight(30);
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
		ArrayList<SuperVO> listnew = new ArrayList<SuperVO>();
		ArrayList<SuperVO> listupdate = new ArrayList<SuperVO>();
		ArrayList<SuperVO> listdel = new ArrayList<SuperVO>();
		PaibanWeekVO[] vos = (PaibanWeekVO[])getBillCardPanelWrapper().getBillCardPanel().getBillModel().getBodyValueVOs(PaibanWeekVO.class.getName());
		
		String[] pks = ((UIRefPane)getBillCardPanelWrapper().getBillCardPanel().getHeadItem("deptnamekq").getComponent()).getRefPKs();
		ArrayList<String> list = new ArrayList<String>();
		list.addAll(Arrays.asList(pks));
		
		//设置审核状态
		String vbillStatus1=null;
		String vbillStatus2=null;
		String vbillStatus3=null;
		
		// 保存时判断公休是否为<0,则不能保存
		StringBuffer errorMsg = new StringBuffer();   //错误提示信息
		for(int i=0;i<vos.length;i++){
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
			
		}
		
		if(errorMsg.length() != 0 || !"".equals(errorMsg.toString())){
			 MessageDialog.showErrorDlg(null, "提示","保存失败："+errorMsg);
			 return;
		}
		
		
		// end
		// 保存时本月最后一周时，校验有没有漏排班
		
		String begin = getBillCardPanelWrapper().getBillCardPanel().getHeadItem("begindate").getValueObject().toString();
		String end = getBillCardPanelWrapper().getBillCardPanel().getHeadItem("enddate").getValueObject().toString();
		
		HashMap<String,DeptKqVO> deptKqVO = getDeptKqVO();
		
		
		for(PaibanWeekVO vo:vos){
			String vdate = vo.getVdate();
			String begin1 = vdate.substring(0,10); //周排班开始时间
			String end1 = vdate.substring(11); //周排班结束时间
			UFDate date = new UFDate(begin1);
			int thisMonthDay = calculateThisMonthDay(begin1);
			
			int day = date.getDay();
			String startDate = date.getDateBefore(day-1).toString();
			String endDate = new UFDate(startDate).getDateAfter(thisMonthDay-1).toString();
			
			String vdate_new = startDate+"至"+endDate;
			
			if(vo.getPrimaryKey()!=null&&vo.getPrimaryKey().trim().length()>0){
				String vdate2 = getBillCardPanelWrapper().getBillCardPanel().getHeadItem("vdate").getValueObject().toString();
				
				PaibanWeekVO[] weekvos1 = (PaibanWeekVO[])HYPubBO_Client.queryByCondition(PaibanWeekVO.class,
						"isnull(dr,0)=0 and pk_corp='"+_getCorp().getPrimaryKey()+"' and vdate='"+vdate2+"' "+HRPPubTool.formInSQL("pk_dept", list)+" ");
				
				//只要有一个不是护理科室就不清状态
				String flag="N"; //是护理科室
				for(String pk_dept :list){
					String string = deptKqVO.get(pk_dept).getVcode();
					if( string.length()>=4 && ("1900").equals(string.substring(0, 4))==false){
						//不是护理科室
						flag="Y";
						break;
					}else if(string.length()<4){
						//不是护理科室
						flag="Y";
						break;
					}
				}
				
				if(("N").equals(flag)){ //是护理科室
					if(weekvos1!=null && weekvos1.length >0 ){
						vbillStatus1 = weekvos1[0].getVbillstatus1();
						vbillStatus2 = weekvos1[0].getVbillstatus2();
						vbillStatus3 = weekvos1[0].getVbillstatus3();
					}
					
					vo.setVbillstatus1(vbillStatus1);
					vo.setVbillstatus2(vbillStatus2);
					vo.setVbillstatus3(vbillStatus3);
				}
				
				vo.setPptype("1");
				
				listupdate.add(vo);
				
				//-------之前补过班 再次补班 也更新到月补班
				
				HashMap<String, String> vbbnameMap1 = new HashMap<String, String>();
				HashMap<String, String> pkbbMap1 = new HashMap<String, String>();
				//
				if(vo.getVbbname1()!=null){
					vbbnameMap1.put("vbbname1", vo.getVbbname1());
					pkbbMap1.put("pk_bb1", vo.getPk_bb1());
				}
				if(vo.getVbbname2()!=null){
					vbbnameMap1.put("vbbname2", vo.getVbbname2());
					pkbbMap1.put("pk_bb2", vo.getPk_bb2());
				}
				if(vo.getVbbname3()!=null){
					vbbnameMap1.put("vbbname3", vo.getVbbname3());
					pkbbMap1.put("pk_bb3", vo.getPk_bb3());
				}	
				
				if(vo.getVbbname4()!=null){
					vbbnameMap1.put("vbbname4", vo.getVbbname4());
					pkbbMap1.put("pk_bb4", vo.getPk_bb4());
				}
				
				if(vo.getVbbname5()!=null){
					vbbnameMap1.put("vbbname5", vo.getVbbname5());
					pkbbMap1.put("pk_bb5", vo.getPk_bb5());
				}
				
				if(vo.getVbbname6()!=null){
					vbbnameMap1.put("vbbname6", vo.getVbbname6());
					pkbbMap1.put("pk_bb6", vo.getPk_bb6());
				}
				
				if(vo.getVbbname7()!=null){
					vbbnameMap1.put("vbbname7", vo.getVbbname7());
					pkbbMap1.put("pk_bb7", vo.getPk_bb7());
				}
				
				//本月 月排班 更新
				PaibanWeekVO[] weekvosss = (PaibanWeekVO[])HYPubBO_Client.queryByCondition(PaibanWeekVO.class,
						"isnull(dr,0)=0 and pptype ='1' and pk_corp='"+_getCorp().getPrimaryKey()+"' and vdate='"+vdate_new+"' "+HRPPubTool.formInSQL("pk_dept", list)+" ");
				
				for(PaibanWeekVO weekvo :weekvosss){
					if((weekvo.getPk_psndoc()).equals(vo.getPk_psndoc())){
						for(int i=0;i<7;i++){
							weekvo.setAttributeValue("vbbname"+(day+i), vbbnameMap1.get("vbbname"+(i+1)));
							weekvo.setAttributeValue("pk_bb"+(day+i), pkbbMap1.get("pk_bb"+(i+1)));
						}
						
						listupdate.add(weekvo);
					}
				}
				
				//跨月  月排班 更新
				if(thisMonthDay-date.getDay()<6){
					
					//跨月
					UFDate date2 = new UFDate(end1); //周排班结束时间
					int day2 = date2.getDay();//周排班结束日期
					//int month2 = date2.getMonth();//周排班结束月份
					
					String kystartDate = date2.getDateBefore(day2-1).toString();
					int kyMonthDay = calculateThisMonthDay(kystartDate);
					String kyendDate = new UFDate(kystartDate).getDateAfter(kyMonthDay-1).toString();
					
					String kyvdate_new = kystartDate+"至"+kyendDate;
					
					PaibanWeekVO[] kyweekvos = (PaibanWeekVO[])HYPubBO_Client.queryByCondition(PaibanWeekVO.class,
							"isnull(dr,0)=0 and pptype ='1' and pk_corp='"+_getCorp().getPrimaryKey()+"' and vdate='"+kyvdate_new+"' "+HRPPubTool.formInSQL("pk_dept", list)+" ");
					
					for(PaibanWeekVO weekvo :kyweekvos){
						if((weekvo.getPk_psndoc()).equals(vo.getPk_psndoc())){  
							for(int i=day2,j=1;i>=1;i--,j++){
								weekvo.setAttributeValue("vbbname"+(j), vbbnameMap1.get("vbbname"+(7-i+1)));
								weekvo.setAttributeValue("pk_bb"+(j), pkbbMap1.get("pk_bb"+(7-i+1)));
							}
							listupdate.add(weekvo);
						}
					}
				}
			}else{
				
				PaibanWeekVO[] weekvos11 = (PaibanWeekVO[])HYPubBO_Client.queryByCondition(PaibanWeekVO.class,
						"isnull(dr,0)=0 and pk_corp='"+_getCorp().getPrimaryKey()+"' and vdate='"+vdate_new+"' "+HRPPubTool.formInSQL("pk_dept", list)+" ");
				
				if(weekvos11!=null && weekvos11.length >0){
					vbillStatus1 = weekvos11[0].getVbillstatus1();
					vbillStatus2 = weekvos11[0].getVbillstatus2();
					vbillStatus3 = weekvos11[0].getVbillstatus3();
				}
				
				vo.setVbillstatus1(vbillStatus1);
				vo.setVbillstatus2(vbillStatus2);
				vo.setVbillstatus3(vbillStatus3);
				
				vo.setUploadnum(1);
				
				//科室
				String pk_dept = vo.getPk_dept();
				String string = deptKqVO.get(pk_dept).getVcode();
				String flag="N";
				if( string.length()>=4 && ("1900").equals(string.substring(0, 4))==false){
					//不是护理科室
					flag="Y";
				}else if(string.length()<4){
					//不是护理科室
					flag="Y";
				}
				if(flag=="N"){
					//护理科室
					
					//修改状态
					PaibanWeekVO[] hlweekvos1 = (PaibanWeekVO[])HYPubBO_Client.queryByCondition(PaibanWeekVO.class,
							"isnull(dr,0)=0 and pk_corp='"+_getCorp().getPrimaryKey()+"' and vdate='"+vo.getVdate()+"' "+HRPPubTool.formInSQL("pk_dept", list)+" ");
					
					if(hlweekvos1!=null && hlweekvos1.length >0){
						vbillStatus1 = hlweekvos1[0].getVbillstatus1();
						vbillStatus2 = hlweekvos1[0].getVbillstatus2();
						vbillStatus3 = hlweekvos1[0].getVbillstatus3();
					}
					
					vo.setVbillstatus1(vbillStatus1);
					vo.setVbillstatus2(vbillStatus2);
					vo.setVbillstatus3(vbillStatus3);
					
					vo.setPptype("1");
					
					if(vo.getPk_bb1()==null && vo.getPk_bb2()==null && vo.getPk_bb3()==null && vo.getPk_bb4()==null && vo.getPk_bb5()==null && vo.getPk_bb6()==null && vo.getPk_bb7()==null){
					
					}else{
						listnew.add(vo);
					}
				}else{
					//非护理科室
					vo.setPptype("1");
					
					if(vo.getPk_bb1()==null && vo.getPk_bb2()==null && vo.getPk_bb3()==null && vo.getPk_bb4()==null && vo.getPk_bb5()==null && vo.getPk_bb6()==null && vo.getPk_bb7()==null){
						
					}else{
						listnew.add(vo);
						HashMap<String, String> vbbnameMap = new HashMap<String, String>();
						HashMap<String, String> pkbbMap = new HashMap<String, String>();
						//
						if(vo.getVbbname1()!=null){
							vbbnameMap.put("vbbname1", vo.getVbbname1());
							pkbbMap.put("pk_bb1", vo.getPk_bb1());
						}
						if(vo.getVbbname2()!=null){
							vbbnameMap.put("vbbname2", vo.getVbbname2());
							pkbbMap.put("pk_bb2", vo.getPk_bb2());
						}
						if(vo.getVbbname3()!=null){
							vbbnameMap.put("vbbname3", vo.getVbbname3());
							pkbbMap.put("pk_bb3", vo.getPk_bb3());
						}	
						
						if(vo.getVbbname4()!=null){
							vbbnameMap.put("vbbname4", vo.getVbbname4());
							pkbbMap.put("pk_bb4", vo.getPk_bb4());
						}
						
						if(vo.getVbbname5()!=null){
							vbbnameMap.put("vbbname5", vo.getVbbname5());
							pkbbMap.put("pk_bb5", vo.getPk_bb5());
						}
						
						if(vo.getVbbname6()!=null){
							vbbnameMap.put("vbbname6", vo.getVbbname6());
							pkbbMap.put("pk_bb6", vo.getPk_bb6());
						}
						
						if(vo.getVbbname7()!=null){
							vbbnameMap.put("vbbname7", vo.getVbbname7());
							pkbbMap.put("pk_bb7", vo.getPk_bb7());
						}
						
						PaibanWeekVO[] weekvoss = (PaibanWeekVO[])HYPubBO_Client.queryByCondition(PaibanWeekVO.class,
								"isnull(dr,0)=0 and pptype ='1' and pk_corp='"+_getCorp().getPrimaryKey()+"' and vdate='"+vdate_new+"' "+HRPPubTool.formInSQL("pk_dept", list)+" ");
						
						
						if(weekvoss==null ||weekvoss.length==0  ){
							//-------之前没有补班记录----月排班
							PaibanWeekVO weekVO = getNewPaibanWeekVO(vo);
							weekVO.setVdate(vdate_new);//月排班时间
							
							//清空pk_bb1~7  vbbname1~7
							for(int i=1;i<8;i++){
								weekVO.setAttributeValue("pk_bb"+i, null);
								weekVO.setAttributeValue("vbbname"+i, null);
								
							}
							
							for(int i=0;i<7;i++){
								weekVO.setAttributeValue("vbbname"+(day+i), vbbnameMap.get("vbbname"+(i+1)));
								weekVO.setAttributeValue("pk_bb"+(day+i), pkbbMap.get("pk_bb"+(i+1)));
							}
							
							listnew.add(weekVO);
						}
						else{
							for(PaibanWeekVO weekvo :weekvoss){
								if((weekvo.getPk_psndoc()).equals(vo.getPk_psndoc())){
									for(int i=0;i<7;i++){
										weekvo.setAttributeValue("vbbname"+(day+i), vbbnameMap.get("vbbname"+(i+1)));
										weekvo.setAttributeValue("pk_bb"+(day+i), pkbbMap.get("pk_bb"+(i+1)));
									}
									
									listupdate.add(weekvo);
								}
							}
							
							if(weekvoss !=null || weekvoss.length>0 ){
								//补班 之后  再从别的科室掉过来人
								ArrayList<String> paibanWeekVOList = new ArrayList<String>();
								for(PaibanWeekVO weekvo :weekvoss){
									paibanWeekVOList.add(weekvo.getPk_psndoc());
								}
								if(paibanWeekVOList.contains(vo.getPk_psndoc())==false){
									PaibanWeekVO weekVO = getNewPaibanWeekVO(vo);
									weekVO.setVdate(vdate_new);//月排班时间
									
									//	清空pk_bb1~7  vbbname1~7
									for(int i=1 ;i<8;i++){
										weekVO.setAttributeValue("pk_bb"+i, null);
										weekVO.setAttributeValue("vbbname"+i, null);
									}
									
									for(int i=0;i<7;i++){
										weekVO.setAttributeValue("vbbname"+(day+i), vbbnameMap.get("vbbname"+(i+1)));
										weekVO.setAttributeValue("pk_bb"+(day+i), pkbbMap.get("pk_bb"+(i+1)));
									}
									
									listnew.add(weekVO);
								}
							}
							
						}
						
						//---------跨月补班-----------
						
						if(thisMonthDay-date.getDay()<6){
							
							//跨月
							UFDate date2 = new UFDate(end1); //周排班结束时间
							int day2 = date2.getDay();//周排班结束日期
							
							String kystartDate = date2.getDateBefore(day2-1).toString();
							int kyMonthDay = calculateThisMonthDay(kystartDate);
							String kyendDate = new UFDate(kystartDate).getDateAfter(kyMonthDay-1).toString();
							
							String kyvdate_new = kystartDate+"至"+kyendDate;
							
							//跨月状态设置
							PaibanWeekVO[] kyweekvos1 = (PaibanWeekVO[])HYPubBO_Client.queryByCondition(PaibanWeekVO.class,
									"isnull(dr,0)=0 and pk_corp='"+_getCorp().getPrimaryKey()+"' and vdate='"+kyvdate_new+"' "+HRPPubTool.formInSQL("pk_dept", list)+" ");
							
							if(kyweekvos1!=null && kyweekvos1.length >0){
								vbillStatus1 = kyweekvos1[0].getVbillstatus1();
								vbillStatus2 = kyweekvos1[0].getVbillstatus2();
								vbillStatus3 = kyweekvos1[0].getVbillstatus3();
							}
							
							vo.setVbillstatus1(vbillStatus1);
							vo.setVbillstatus2(vbillStatus2);
							vo.setVbillstatus3(vbillStatus3);
							
							
							PaibanWeekVO[] kyweekvos = (PaibanWeekVO[])HYPubBO_Client.queryByCondition(PaibanWeekVO.class,
									"isnull(dr,0)=0 and pptype ='1' and pk_corp='"+_getCorp().getPrimaryKey()+"' and vdate='"+kyvdate_new+"' "+HRPPubTool.formInSQL("pk_dept", list)+" ");
							
							if(kyweekvos==null ||kyweekvos.length==0 ){
								//-------之前没有补班记录----跨月月排班
								PaibanWeekVO weekVO = getNewPaibanWeekVO(vo);
								weekVO.setVdate(kyvdate_new);
								
								//清空pk_bb1~7  vbbname1~7
								for(int i=1 ;i<8;i++){
									weekVO.setAttributeValue("pk_bb"+i, null);
									weekVO.setAttributeValue("vbbname"+i, null);
								}
								
								for(int i=day2,j=1;i>=1;i--,j++){
									weekVO.setAttributeValue("vbbname"+(j), vbbnameMap.get("vbbname"+(7-i+1)));
									weekVO.setAttributeValue("pk_bb"+(j), pkbbMap.get("pk_bb"+(7-i+1)));
								}
								listnew.add(weekVO);
							}else{
								
								
								for(PaibanWeekVO weekvo :kyweekvos){
									if((weekvo.getPk_psndoc()).equals(vo.getPk_psndoc())){  
										for(int i=day2,j=1;i>=1;i--,j++){
											weekvo.setAttributeValue("vbbname"+(j), vbbnameMap.get("vbbname"+(7-i+1)));
											weekvo.setAttributeValue("pk_bb"+(j), pkbbMap.get("pk_bb"+(7-i+1)));
										}
										listupdate.add(weekvo);
									}
								}
								
								if(kyweekvos !=null || kyweekvos.length>0 ){
									//补班 之后  再从别的科室掉过来人
									ArrayList<String> paibanWeekVOList = new ArrayList<String>();
									for(PaibanWeekVO weekvo :kyweekvos){
										paibanWeekVOList.add(weekvo.getPk_psndoc());
									}
									if(paibanWeekVOList.contains(vo.getPk_psndoc())==false){
										PaibanWeekVO weekVO = getNewPaibanWeekVO(vo);
										weekVO.setVdate(kyvdate_new);
										
										//	清空pk_bb1~7  vbbname1~7
										for(int i=1 ;i<8;i++){
											weekVO.setAttributeValue("pk_bb"+i, null);
											weekVO.setAttributeValue("vbbname"+i, null);
										}
										
										for(int i=day2,j=1;i>=1;i--,j++){
											weekVO.setAttributeValue("vbbname"+(j), vbbnameMap.get("vbbname"+(7-i+1)));
											weekVO.setAttributeValue("pk_bb"+(j), pkbbMap.get("pk_bb"+(7-i+1)));
										}
										listnew.add(weekVO);
									}
								}
							}
						}
					}
				}
			}
		}
		try{
			Ihrppub impl = (Ihrppub)NCLocator.getInstance().lookup(Ihrppub.class.getName());
			impl.saveVOs(listdel,listupdate,listnew);
			
			
			String vdate0814 = getBillCardPanelWrapper().getBillCardPanel().getHeadItem("vdate").getValueObject().toString();
			PaibanWeekVO[] weekvos = (PaibanWeekVO[])HYPubBO_Client.queryByCondition(PaibanWeekVO.class,
					"isnull(dr,0)=0 and pptype ='1' and pk_corp='"+_getCorp().getPrimaryKey()+"' and vdate='"+vdate0814+"' "+HRPPubTool.formInSQL("pk_dept", list)+" ");
			UFDate begin0814 = new UFDate(vdate0814.substring(0,10));
			// 根据当前时间,取下一个月开始时间,结束时间,当前时间在下一个月区间内,则排在下月,否则在本月
			
			UFDate curdate = nc.ui.hr.global.Global.getServerTime().getDate();
			ArrayList<PanbanWeekBVO> list_b = new ArrayList<PanbanWeekBVO>();
			HashMap<String,PaibanTempVO> map = new HashMap<String, PaibanTempVO>();
			PaibanTempVO[] tvos = (PaibanTempVO[])HYPubBO_Client.queryByCondition(PaibanTempVO.class, " isnull(dr,0)=0 and pk_corp='"+_getCorp().getPrimaryKey()+"' ");
			if(vos!=null&&vos.length>0){
				for(PaibanTempVO vo0814:tvos){
					map.put(vo0814.getPk_psndoc(), vo0814);
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
							bvo.setDdate(begin0814.getDateAfter(i-1));
							bvo.setMemo(weekvo.getMemo());
							bvo.setUploadnum(1);
							bvo.setDclassperiod(getDclassDate(begin0814.toString()));
							bvo.setPptype("1");
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

			//HYPubBO_Client.deleteByWhereClause(PanbanWeekBVO.class, "    isnull(biszb,'N')='N' and isnull(dr,0)=0 "+HRPPubTool.formInSQL("pk_dept", list)+" and (ddate>= '"+begin+"' and ddate<='"+end+"') ");
			HYPubBO_Client.insertAry(list_b.toArray(new PanbanWeekBVO[list_b.size()]));
			
			
			PanbanWeekBVO[] bvos =(PanbanWeekBVO[]) HYPubBO_Client.queryByCondition(PanbanWeekBVO.class, " isnull(dr,0)=0 "+HRPPubTool.formInSQL("pk_psndoc", list_psn)+" and pk_bb in ('10028L100000000002D8','10028L100000000002XL') and ddate like '"+begin0814.getYear()+"%' ");
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
						" isnull(dr,0)=0 and vyear='"+begin0814.getYear()+"' and pk_corp='"+_getCorp().getPrimaryKey()+"' "+HRPPubTool.formInSQL("pk_psndoc", list_psn)+" ");
				if(gxvos!=null&&gxvos.length>0){
					for(GxBVO gxvo:gxvos){
						
						gxvo.setNyygx(map_gxs.get(gxvo.getPk_psndoc()));
						gxvo.setNsxgx((gxvo.getNsfgx()!=null?gxvo.getNsfgx():new UFDouble(0)).sub(gxvo.getNyygx()!=null?gxvo.getNyygx():new UFDouble(0)));
					}
				}
				HYPubBO_Client.updateAry(gxvos);
			}else{
				GxBVO[] gxvos = (GxBVO[])HYPubBO_Client.queryByCondition(GxBVO.class,
						" isnull(dr,0)=0 and vyear='"+begin0814.getYear()+"' and pk_corp='"+_getCorp().getPrimaryKey()+"' "+HRPPubTool.formInSQL("pk_psndoc", list_psn)+" ");
				if(gxvos!=null&&gxvos.length>0){
					for(GxBVO gxvo:gxvos){
						gxvo.setNyygx(map_gxs.get(gxvo.getPk_psndoc()));
						gxvo.setNsxgx(gxvo.getNsfgx().sub(gxvo.getNyygx()!=null?gxvo.getNyygx():new UFDouble(0)));
					}
				}
				HYPubBO_Client.updateAry(gxvos);
			}


		}catch(Exception e){
			e.printStackTrace();
		}

		((ClientUI2)getBillUI()).setEditstate(0);
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
	
	public int calculateThisMonthDay(String vdate){
		int month = new UFDate(vdate).getMonth();
		
		if(month ==1 || month ==3 || month==5 || month==7 || month==8 || month==10 || month==12){
			return 31;
		}else if(month ==4 || month ==6 || month==9 || month==11 ){
			return 30;
		}else {
			int year = new UFDate(vdate).getYear();
			if((year % 4 ==0 && year %100 !=0) ||(year % 400 ==0)){
				return 29;
			}else{
				return 28;
			}
		}
	}
	
	public String getDclassDate(String currdate) throws ParseException{
		
		UFDate ufcurrdate = new UFDate(currdate);
		UFDate ufnextdate = new UFDate(ufcurrdate.getYear() + "-" + (ufcurrdate.getMonth()+1) +"-01");
		UFDate nextbegindate = null,ufdclassdate = null;
		int day = 0;
		if(getDay(ufnextdate.toString()) != 2){// 不为周一
			if(getDay(ufnextdate.toString())==1){ // 但为周天
				day = 8;
			}else{
				day = getDay(ufnextdate.toString());
			}
			nextbegindate = new UFDate(ufnextdate.toString()).getDateBefore(day - 2);
		}
		
		if(ufcurrdate.compareTo(nextbegindate) >= 0){
			ufdclassdate = ufnextdate;
		}else{
			ufdclassdate = new UFDate(ufcurrdate.getYear() + "-" +ufcurrdate.getMonth() +"-01");
		}
		
		return ufdclassdate.toString();
	}
	@Override
	protected void onBoCancel() throws Exception {
		// TODO Auto-generated method stub
		((ClientUI2)getBillUI()).setEditstate(0);
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
					"isnull(dr,0)=0 and pptype='1' and pk_corp='"+_getCorp().getPrimaryKey()+"' and vdate='"+vdate+"' "+HRPPubTool.formInSQL("pk_dept", list)+" ");
			
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
					
					//String status = getStatus(weekvo.getVbillstatus1(),weekvo.getVbillstatus2(),weekvo.getVbillstatus3());
					//weekvo.setVbillstatus(status);
				}
				
				getBillCardPanelWrapper().getBillCardPanel().getBillModel().setBodyDataVO(weekvos);
				getBillCardPanelWrapper().getBillCardPanel().getBillModel().execLoadFormula();
			}
			
				getButtonManager().getButton(IBillButton.Add).setEnabled(true);
				getBillUI().updateButtons();
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
		case 801: //下周
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
		
		/***************************************************/
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
		/***************************************************/
		
		if(deptlist.size() == 2){
			 MessageDialog.showErrorDlg(null, "提示","上传失败："+"上传科室中包含有护理科室!");
			 return;
		}
		
		for(int i=0;i<deptlist.size();i++){
			if(deptlist.get(i).equals("Y")){
				onUploadHl(list01);// 护理科室上传
			}else if(deptlist.get(i).equals("N")){
				onUploadOther(list01);
			}
			
		}
		
		
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
			
	
			String[] vdates = getBeginAndEnd(begindate,new UFDate(nextdate).toString());
//			String[] vdates = {vdate11,vdate12,vdate13,vdate14,vdate15}; 
			
			StringBuffer errorMsg = new StringBuffer();   //错误提示信息
			for(int j=0;j<vdates.length;j++){
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
			
			String msg = upLoadPaibanWeek(vdates,uploadnum,ufdclassperiod.toString());
			if(!"".equals(msg)){
				MessageDialog.showErrorDlg(null, "提示","上传失败："+msg);
				return;
			}else{
				MessageDialog.showHintDlg(null, "提示", begindate +"至"+nextdate+",上传成功！");
			}
			
//		}else{
//			 MessageDialog.showErrorDlg(null, "提示","上传失败：开始时间与本月第一周开始时间不相等!");
//			 return;
//		}
		
		
		
		getButtonManager().getButton(810).setEnabled(false);
		getBillUI().updateButtons();
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
			String msg = upLoadPaibanWeek(vdates,uploadnum,ufdclassperiod.toString());
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
	
	
	public String upLoadPaibanWeek(String[] vdates,Integer uploadnum,String dclassperiod) throws UifException{
		String msg="";
		String[] pks = ((UIRefPane)getBillCardPanelWrapper().getBillCardPanel().getHeadItem("deptnamekq").getComponent()).getRefPKs();

		ArrayList<String> list = new ArrayList<String>();
		list.addAll(Arrays.asList(pks));
		//String vdate = getBillCardPanelWrapper().getBillCardPanel().getHeadItem("vdate").getValueObject().toString();
		// 根据当前时间,计算这一个月
		for(int j=0;j<vdates.length;j++){
			String vdate = vdates[j];
			PaibanWeekVO[] weekvos = (PaibanWeekVO[])HYPubBO_Client.queryByCondition(PaibanWeekVO.class,
					"isnull(dr,0)=0 and pk_corp='"+_getCorp().getPrimaryKey()+"' and vdate='"+vdate+"' "+HRPPubTool.formInSQL("pk_dept", list)+" ");
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
				
				HYPubBO_Client.deleteByWhereClause(PanbanWeekBVO.class, " isnull(biszb,'N')='N' and isnull(dr,0)=0 "+HRPPubTool.formInSQL("pk_dept", list)+" and (ddate>= '"+begin+"' and ddate<='"+end+"') ");
				HYPubBO_Client.insertAry(list_b.toArray(new PanbanWeekBVO[list_b.size()]));
				HYPubBO_Client.deleteByWhereClause(PaiBanAuditMsg.class, " vbillstatus=0 and isnull(dr,0)=0 "+HRPPubTool.formInSQL("pk_dept", list)+" and (ddate>= '"+begin+"' and ddate<='"+end+"') ");
				HYPubBO_Client.deleteByWhereClause(PaiBanAuditMsg2.class, " vbillstatus=0 and isnull(dr,0)=0 "+HRPPubTool.formInSQL("pk_dept", list)+" and (ddate>= '"+begin+"' and ddate<='"+end+"') ");

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
		StringBuffer errorMsg = new StringBuffer();   //错误提示信息
		for(int i=0;i<list_vo.size();i++){
			PaibanWeekVO paibanweekvo = list_vo.get(i);
			
			String pk_psndoc=paibanweekvo.getPk_psndoc();
			String pk_deptdoc=paibanweekvo.getPk_dept();
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
									
//									 调入调出时间判断
									
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
	
	/**
	 * 得到一个新的补班排班vo
	 * @param vo
	 * @return
	 */
	private PaibanWeekVO getNewPaibanWeekVO(PaibanWeekVO vo){
		PaibanWeekVO weekVO = new PaibanWeekVO();
		weekVO.setDr(vo.getDr());
		//weekVO.setDirty(arg0)
		weekVO.setMemo(vo.getMemo());
		weekVO.setNbcgxs(vo.getNbcgxs());
		weekVO.setNgxs(vo.getNgxs());
		weekVO.setNnowdays(vo.getNnowdays());
		weekVO.setNsygxs(vo.getNsygxs());
		weekVO.setPk_bb1(vo.getPk_bb1());
		weekVO.setPk_bb2(vo.getPk_bb2());
		weekVO.setPk_bb3(vo.getPk_bb3());
		weekVO.setPk_bb4(vo.getPk_bb4());
		weekVO.setPk_bb5(vo.getPk_bb5());
		weekVO.setPk_bb6(vo.getPk_bb6());
		weekVO.setPk_bb7(vo.getPk_bb7());
		weekVO.setPk_bb8(vo.getPk_bb8());
		weekVO.setPk_bb9(vo.getPk_bb9());
		weekVO.setPk_bb10(vo.getPk_bb10());
		weekVO.setPk_bb11(vo.getPk_bb11());
		weekVO.setPk_bb12(vo.getPk_bb12());
		weekVO.setPk_bb13(vo.getPk_bb13());
		weekVO.setPk_bb14(vo.getPk_bb14());
		weekVO.setPk_bb15(vo.getPk_bb15());
		weekVO.setPk_bb16(vo.getPk_bb16());
		weekVO.setPk_bb17(vo.getPk_bb17());
		weekVO.setPk_bb18(vo.getPk_bb18());
		weekVO.setPk_bb19(vo.getPk_bb19());
		weekVO.setPk_bb20(vo.getPk_bb20());
		weekVO.setPk_bb21(vo.getPk_bb21());
		weekVO.setPk_bb22(vo.getPk_bb22());
		weekVO.setPk_bb23(vo.getPk_bb23());
		weekVO.setPk_bb24(vo.getPk_bb24());
		weekVO.setPk_bb25(vo.getPk_bb25());
		weekVO.setPk_bb26(vo.getPk_bb26());
		weekVO.setPk_bb27(vo.getPk_bb27());
		weekVO.setPk_bb28(vo.getPk_bb28());
		weekVO.setPk_bb29(vo.getPk_bb29());
		weekVO.setPk_bb30(vo.getPk_bb30());
		weekVO.setPk_bb31(vo.getPk_bb31());
		weekVO.setPk_corp(vo.getPk_corp());
		weekVO.setPk_dept(vo.getPk_dept());
		weekVO.setPk_deptzb(vo.getPk_deptzb());
		weekVO.setPk_psndoc(vo.getPk_psndoc());
		weekVO.setPk_temp(vo.getPk_temp());
		weekVO.setPptype("1");
		weekVO.setStatus(vo.getStatus());
		weekVO.setTs(vo.getTs());
		weekVO.setUploadnum(vo.getUploadnum());
		weekVO.setVbbname1(vo.getVbbname1());
		weekVO.setVbbname2(vo.getVbbname2());
		weekVO.setVbbname3(vo.getVbbname3());
		weekVO.setVbbname4(vo.getVbbname4());
		weekVO.setVbbname5(vo.getVbbname5());
		weekVO.setVbbname6(vo.getVbbname6());
		weekVO.setVbbname7(vo.getVbbname7());
		weekVO.setVbbname8(vo.getVbbname8());
		weekVO.setVbbname9(vo.getVbbname9());
		weekVO.setVbbname10(vo.getVbbname10());
		weekVO.setVbbname11(vo.getVbbname11());
		weekVO.setVbbname12(vo.getVbbname12());
		weekVO.setVbbname13(vo.getVbbname13());
		weekVO.setVbbname14(vo.getVbbname14());
		weekVO.setVbbname15(vo.getVbbname15());
		weekVO.setVbbname16(vo.getVbbname16());
		weekVO.setVbbname17(vo.getVbbname17());
		weekVO.setVbbname18(vo.getVbbname18());
		weekVO.setVbbname19(vo.getVbbname19());
		weekVO.setVbbname20(vo.getVbbname20());
		weekVO.setVbbname21(vo.getVbbname21());
		weekVO.setVbbname22(vo.getVbbname22());
		weekVO.setVbbname23(vo.getVbbname23());
		weekVO.setVbbname24(vo.getVbbname24());
		weekVO.setVbbname25(vo.getVbbname25());
		weekVO.setVbbname26(vo.getVbbname26());
		weekVO.setVbbname27(vo.getVbbname27());
		weekVO.setVbbname28(vo.getVbbname28());
		weekVO.setVbbname29(vo.getVbbname29());
		weekVO.setVbbname30(vo.getVbbname30());
		weekVO.setVbbname31(vo.getVbbname31());
		weekVO.setVbillstatus(vo.getVbillstatus());
		weekVO.setVbillstatus1(vo.getVbillstatus1());
		weekVO.setVbillstatus2(vo.getVbillstatus2());
		weekVO.setVbillstatus3(vo.getVbillstatus3());
		weekVO.setVdate(vo.getVdate());
		weekVO.setVdef1(vo.getVdef1());
		weekVO.setVdef2(vo.getVdef2());
		weekVO.setVdef3(vo.getVdef3());
		weekVO.setVdef4(vo.getVdef4());
		weekVO.setVdef5(vo.getVdef5());
		weekVO.setVoperatorid(vo.getVoperatorid());
		return weekVO;
	} 
}
