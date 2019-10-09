package nc.ui.tam.tongren015;


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
import nc.itf.uap.IUAPQueryBS;
import nc.jdbc.framework.processor.BeanListProcessor;
import nc.ui.hrp.pub.bill.HRPEventhandle;
import nc.ui.hrp.pub.bill.HRPEventhandleSingleHead;
import nc.ui.pub.ButtonObject;
import nc.ui.pub.ClientEnvironment;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UIDialog;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.bill.BillItem;
import nc.ui.pub.bill.BillModel;
import nc.ui.querytemplate.QueryConditionDLG;
import nc.ui.querytemplate.meta.FilterMeta;
import nc.ui.querytemplate.valueeditor.IFieldValueElementEditor;
import nc.ui.querytemplate.valueeditor.IFieldValueElementEditorFactory;
import nc.ui.querytemplate.valueeditor.RefElementEditor;
import nc.ui.querytemplate.valueeditor.UIRefpaneCreator;
import nc.ui.trade.business.HYPubBO_Client;
import nc.ui.trade.button.IBillButton;
import nc.ui.trade.controller.IControllerBase;
import nc.ui.trade.manage.BillManageUI;
import nc.ui.trade.query.HYQueryConditionDLG;
import nc.ui.trade.query.INormalQuery;
import nc.vo.bd.ConditionVO;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.SuperVO;
import nc.vo.pub.VOStatus;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDouble;
import nc.vo.querytemplate.TemplateInfo;
import nc.vo.tam.tongren001.DeptKqVO;
import nc.vo.tam.tongren003.PaiBanAuditMsg;
import nc.vo.tam.tongren003.PaiBanAuditMsg2;
import nc.vo.tam.tongren003.PaibanWeekVO;
import nc.vo.tam.tongren003.PanbanWeekBVO;
import nc.vo.tam.tongren006.ZhibanTempVO;
import nc.vo.tam.tongren010.GxBVO;
import nc.vo.tam.tongren011.AdjustTamVO;
import nc.vo.tbm.tbm_029.BclbHeaderVO;
import nc.vo.trade.pub.HYBillVO;

public class ClientEH extends HRPEventhandleSingleHead {
	BillManageUI ui = null;
	public ClientEH(BillManageUI billUI, IControllerBase control) {
		super(billUI, control);
		ui = billUI;
		


	}
	
	protected TemplateInfo getTempInfo() {
		ClientEnvironment ce = ClientEnvironment.getInstance();
		TemplateInfo tempinfo = new TemplateInfo();
		tempinfo.setPk_Org(ce.getCorporation().getPrimaryKey());
		tempinfo.setCurrentCorpPk(ce.getCorporation().getPrimaryKey());
		tempinfo.setFunNode(getUIController().getBillType());
		tempinfo.setUserid(ce.getUser().getPrimaryKey());
		tempinfo.setNodekey(getUIController().getBillType());
		return tempinfo;
	}
	
	
	@Override
	public void onBoAudit() throws Exception {
		
		// TODO Auto-generated method stub
		int rowcount = getBillListPanel().getHeadBillModel().getRowCount();
		// 判断调整后时间，是在第几周，星期几，更新排班主表
		
		ArrayList<AdjustTamVO> list = new ArrayList<AdjustTamVO>();
		for(int i=0;i<rowcount;i++){
			Object value = getBillListPanel().getHeadBillModel().getValueAt(i, "isselect");
			if(value!=null&&new UFBoolean(value.toString()).booleanValue()){
				if((getBillListPanel().getHeadBillModel().getValueAt(i, "istate")!=null
						&&getBillListPanel().getHeadBillModel().getValueAt(i, "istate").toString().equals("提交"))
						|| 
						getBillListPanel().getHeadBillModel().getValueAt(i, "istate")!=null
						&&getBillListPanel().getHeadBillModel().getValueAt(i, "istate").toString().equals("不确认")){
					getBillListPanel().getHeadBillModel().setValueAt(3, i, "istate");
					getBillListPanel().getHeadBillModel().getRowAttribute(i).setRowState(BillModel.MODIFICATION);
					AdjustTamVO vo = (AdjustTamVO)getBillListPanel().getHeadBillModel().getBodyValueRowVO(i, AdjustTamVO.class.getName());
					list.add(vo);
				}
			}
		}
		
		IBclbDefining def = NCLocator.getInstance().lookup(IBclbDefining.class);
		BclbHeaderVO[] bclbvos = def.queryBclb029AllBclbHeader(_getCorp().getPrimaryKey(), null, " and pk_bbz in ('00018L1000000010I5FM','00018L1000000010I5FS','00018L1000000010J12W','00018L1000000010J12V','00018L1000000010J12X')  ");
		ArrayList<String> list_zb = new ArrayList<String>();
		for(BclbHeaderVO bclbvo:bclbvos){
			list_zb.add(bclbvo.getPk_bclbid());
		}
		
		if(list!=null&&list.size()>0){
			ArrayList<PanbanWeekBVO> list_dele = new ArrayList<PanbanWeekBVO>();
			ArrayList<PanbanWeekBVO> list_new = new ArrayList<PanbanWeekBVO>();
			
			
			ArrayList<String> list_psn = new ArrayList<String>();
			String year = list.get(0).getDdate().getYear()+"";
			for(int i=0;i<list.size();i++){
				AdjustTamVO tamvo = list.get(i);
				list_psn.add(tamvo.getPk_psn());
				//****** 更新排班主表 / 
				
				PaibanWeekVO[] oldhvos = (PaibanWeekVO[])HYPubBO_Client.queryByCondition(PaibanWeekVO.class,
						" isnull(dr,0)=0 and pk_corp = '"+tamvo.getPk_corp()+"' and pk_dept='"+tamvo.getPk_dept()+"' and pk_psndoc='"+tamvo.getPk_psn()+"' and substr(vdate,0,10)<='"+tamvo.getDdate()+"' and substr(vdate,12,11)>='"+tamvo.getDdate()+"' ");
				
				Integer index = getDay(tamvo.getDdate().toString())-1 ;
				if(index==0){
					index=7;
				}
				String pk_bbs_new = tamvo.getPk_bb_new();
				String pk_bb_new ="",pk_bb_newname="";
				if(pk_bbs_new!=null&&pk_bbs_new.length()>0){
					String[] pk_bbss = pk_bbs_new.split(",");
					String[] pk_bbnames = tamvo.getBbname_new().split(",");
					
					int count = 0;
					for(String pk_bb:pk_bbss){
						if(!list_zb.contains(pk_bb)){
							pk_bb_new = pk_bb_new + ","+pk_bb;
							pk_bb_newname = pk_bb_newname +","+pk_bbnames[count];
						}
						count++;
					}
				}
				
				for(PaibanWeekVO vo:oldhvos){
					String vdate = vo.getVdate();
					String begin = vdate.substring(0,10);
					String end = vdate.substring(11);
					int j = new UFDate().getDaysBetween(new UFDate(begin),new UFDate(end))+1; //日期间隔天数
					if(!pk_bb_newname.equals("")){
						if(j==7){
							//周排班
							vo.setAttributeValue("vbbname"+index, pk_bb_newname.substring(1, pk_bb_newname.length()));
							vo.setAttributeValue("pk_bb"+index, pk_bb_new.substring(1, pk_bb_new.length()));
						}
						if(j>7){
							//月排班
							UFDate ddate = tamvo.getDdate();
							int day = ddate.getDay();
							vo.setAttributeValue("vbbname"+day, pk_bb_newname.substring(1, pk_bb_newname.length()));
							vo.setAttributeValue("pk_bb"+day, pk_bb_new.substring(1, pk_bb_new.length()));
						}
						
					}else{
						if(j==7){
						//周排班
						vo.setAttributeValue("vbbname"+index, "");
						vo.setAttributeValue("pk_bb"+index, "");
						}
						if(j>7){
							//月排班
							UFDate ddate = tamvo.getDdate();
							int day = ddate.getDay();
							vo.setAttributeValue("vbbname"+day, "");
							vo.setAttributeValue("pk_bb"+day, "");
						}
						
					}
					
					vo.setStatus(VOStatus.UPDATED);
					//lzch 20190823 
					HYPubBO_Client.update(vo);
				}
				//list_h_update.addAll(Arrays.asList(oldhvos));
				///******/
				
				/////begin
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd"); 
				Calendar ca = Calendar.getInstance();    
				ca.setTime(tamvo.getDdate().toDate()); 
		        ca.set(Calendar.DAY_OF_MONTH, ca.getActualMaximum(Calendar.DAY_OF_MONTH));  
		        String last = format.format(ca.getTime());

				
				// end
				
				String pk_paiban = "",dclassperiod="",pk_deptzb="",deptzbname="",pk_temp="";
				
				PanbanWeekBVO[] oldvos = (PanbanWeekBVO[])HYPubBO_Client.queryByCondition(PanbanWeekBVO.class,
						" isnull(dr,0)=0 and pk_corp = '"+tamvo.getPk_corp()+"'and pk_dept ='"+tamvo.getPk_dept()+"' and pk_psndoc='"+tamvo.getPk_psn()+"' and ddate='"+tamvo.getDdate()+"' ");
				
				if(oldvos!=null&&oldvos.length>0){
					for(PanbanWeekBVO vo:oldvos){
						vo.setDr(1);
						vo.setStatus(VOStatus.UPDATED);
						
						pk_paiban = vo.getPk_paiban();
						dclassperiod = vo.getDclassperiod();
						pk_deptzb= vo.getPk_deptzb();
						deptzbname= vo.getDeptzbname();
						pk_temp = vo.getPk_temp();
					}
					list_dele.addAll(Arrays.asList(oldvos));
				}
				String pk_bbs = tamvo.getPk_bb_new();
				if(pk_bbs!=null&&pk_bbs.length()>0){
					String[] pk_bbss = pk_bbs.split(",");
					for(String pk_bb:pk_bbss){
						PanbanWeekBVO bvo = new PanbanWeekBVO();
						bvo.setPrimaryKey(null);
						bvo.setDr(0);
						bvo.setPk_corp(_getCorp().getPrimaryKey());
						bvo.setPk_psndoc(tamvo.getPk_psn());
						bvo.setPk_dept(tamvo.getPk_dept());
						bvo.setPk_bb(pk_bb);
						
						if(list_zb.contains(pk_bb)){ //判断是不是值班
							
							ZhibanTempVO[] tempvos = (ZhibanTempVO[])HYPubBO_Client.queryByCondition(ZhibanTempVO.class,
									" isnull(dr,0)=0 and isnull(biscancle,'N')='N'  and pk_bb = '"+pk_bb+"' and dbegindate<='"+last+"' and pk_paiban = '"+tamvo.getPk_temp()+"'");
							
							//
							if(tempvos !=null && tempvos.length > 0) {
								bvo.setBiszb(new UFBoolean(true));
								bvo.setPk_deptzb(tempvos[0].getPk_dept());
								bvo.setDeptzbname(tempvos[0].getDeptnamekq());
								bvo.setPk_temp(tempvos[0].getPk_paiban());
								
								UFDate ddate = tamvo.getDdate();
								int day = ddate.getDay();
								UFDate dateBefore = ddate.getDateBefore(day-1); //月初
								
								PanbanWeekBVO[] findvos = (PanbanWeekBVO[])HYPubBO_Client.queryByCondition(PanbanWeekBVO.class,
										" isnull(dr,0)=0 and biszb = 'Y' and pk_deptzb like'"+tempvos[0].getPk_dept()+"' and ddate <='"+tamvo.getDdate().toString()+"' and ddate >='"+dateBefore.toString()+"' and pk_temp='"+tempvos[0].getPk_paiban()+"' order by ddate desc");
								
								if(findvos != null && findvos.length >0){
									bvo.setVbillstatus1(findvos[0].getVbillstatus1());
									bvo.setVbillstatus2(findvos[0].getVbillstatus2());
									bvo.setVbillstatus3(findvos[0].getVbillstatus3());
								}else{
									bvo.setVbillstatus1("0");
									bvo.setVbillstatus2("0");
									bvo.setVbillstatus3("0");
								}
							}
							
							
						}else{
							bvo.setPk_paiban(pk_paiban);
							bvo.setBiszb(new UFBoolean(false));
						}
						
						bvo.setDdate(tamvo.getDdate());
						bvo.setDclassperiod(dclassperiod);
						bvo.setMemo(tamvo.getVmemo());
						bvo.setUploadnum(1);
						
						list_new.add(bvo);
						
					}					
				}
				
				
			}
			
			HYPubBO_Client.updateAry(list.toArray(new AdjustTamVO[0]));
			/*if(list_h_update != null && list_h_update.size() > 0){
				HYPubBO_Client.updateAry(list_h_update.toArray(new PaibanWeekVO[0]));
			}*/
			if(list_dele!=null&&list_dele.size()>0){
				HYPubBO_Client.updateAry(list_dele.toArray(new PanbanWeekBVO[0]));
			}
			if(list_new!=null&&list_new.size()>0){
				HYPubBO_Client.insertAry(list_new.toArray(new PanbanWeekBVO[0]));
			}

				
			PanbanWeekBVO[] bvos =(PanbanWeekBVO[]) HYPubBO_Client.queryByCondition(PanbanWeekBVO.class, 
						" isnull(dr,0)=0 "+HRPPubTool.formInSQL("pk_psndoc", list_psn)+" and pk_bb in ('10028L100000000002D8','10028L100000000002XL') and ddate like '"+year+"%' ");
				
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
							" isnull(dr,0)=0 and vyear='"+year+"' and pk_corp='"+_getCorp().getPrimaryKey()+"' "+HRPPubTool.formInSQL("pk_psndoc", list_psn)+" ");
					
					
					if(gxvos!=null&&gxvos.length>0){
						for(GxBVO gxvo:gxvos){
							gxvo.setNyygx(map_gxs.get(gxvo.getPk_psndoc()));
							gxvo.setNsxgx((gxvo.getNsfgx()!=null?gxvo.getNsfgx():new UFDouble(0)).sub(gxvo.getNyygx()!=null?gxvo.getNyygx():new UFDouble(0)));
						}
					}
					HYPubBO_Client.updateAry(gxvos);
				}
				
			else{
				GxBVO[] gxvos = (GxBVO[])HYPubBO_Client.queryByCondition(GxBVO.class,
						" isnull(dr,0)=0 and vyear='"+year+"' and pk_corp='"+_getCorp().getPrimaryKey()+"' "+HRPPubTool.formInSQL("pk_psndoc", list_psn)+" ");
				if(gxvos!=null&&gxvos.length>0){
					for(GxBVO gxvo:gxvos){
						gxvo.setNyygx(map_gxs.get(gxvo.getPk_psndoc()));
						gxvo.setNsxgx((gxvo.getNsfgx()!=null?gxvo.getNsfgx():new UFDouble(0)).sub(gxvo.getNyygx()!=null?gxvo.getNyygx():new UFDouble(0)));
					}
				}
			HYPubBO_Client.updateAry(gxvos);
			}
		}
		MessageDialog.showHintDlg(this.getBillUI(), "提示", "确认完成");
		getBillListPanel().setEnabled(true);		
		
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
	
	@Override
	protected void onBoCancelAudit() throws Exception {
		// TODO Auto-generated method stub
		int rowcount = getBillListPanel().getHeadBillModel().getRowCount();
		// 判断调整后时间，是在第几周，星期几，更新排班主表
		
		ArrayList<AdjustTamVO> list = new ArrayList<AdjustTamVO>();
		for(int i=0;i<rowcount;i++){
			Object value = getBillListPanel().getHeadBillModel().getValueAt(i, "isselect");
			if(value!=null&&new UFBoolean(value.toString()).booleanValue()){
				if(getBillListPanel().getHeadBillModel().getValueAt(i, "istate")!=null
						&&getBillListPanel().getHeadBillModel().getValueAt(i, "istate").toString().equals("确认")){
					getBillListPanel().getHeadBillModel().setValueAt(4, i, "istate");
					getBillListPanel().getHeadBillModel().getRowAttribute(i).setRowState(BillModel.MODIFICATION);
					AdjustTamVO vo = (AdjustTamVO)getBillListPanel().getHeadBillModel().getBodyValueRowVO(i, AdjustTamVO.class.getName());
					list.add(vo);
				}
			}
		}
		IBclbDefining def = NCLocator.getInstance().lookup(IBclbDefining.class);
		BclbHeaderVO[] bclbvos = def.queryBclb029AllBclbHeader(_getCorp().getPrimaryKey(), null, " and pk_bbz in ('00018L1000000010I5FM','00018L1000000010I5FS','00018L1000000010J12W','00018L1000000010J12V','00018L1000000010J12X')  ");
		ArrayList<String> list_zb = new ArrayList<String>();
		for(BclbHeaderVO bclbvo:bclbvos){
			list_zb.add(bclbvo.getPk_bclbid());
		}
		if(list!=null&&list.size()>0){
			ArrayList<PanbanWeekBVO> list_dele = new ArrayList<PanbanWeekBVO>();
			ArrayList<PanbanWeekBVO> list_new = new ArrayList<PanbanWeekBVO>();
			ArrayList<PaibanWeekVO> list_h_update = new ArrayList<PaibanWeekVO>();
			//ArrayList<PaiBanAuditMsg> list_msg_new = new ArrayList<PaiBanAuditMsg>();
			//ArrayList<PaiBanAuditMsg2> list_msg2_new = new ArrayList<PaiBanAuditMsg2>();
			
			
			ArrayList<String> list_psn = new ArrayList<String>();
			String year = list.get(0).getDdate().getYear()+"";
			for(int i=0;i<list.size();i++){
				AdjustTamVO tamvo = list.get(i);
				list_psn.add(tamvo.getPk_psn());
				//****** 更新排班主表 / 
				PaibanWeekVO[] oldhvos = (PaibanWeekVO[])HYPubBO_Client.queryByCondition(PaibanWeekVO.class,
						" isnull(dr,0)=0 and pk_dept='"+tamvo.getPk_dept()+"' and pk_psndoc='"+tamvo.getPk_psn()+"' and substr(vdate,0,10)<='"+tamvo.getDdate()+"' and substr(vdate,12,11)>='"+tamvo.getDdate()+"'");
				Integer index = getDay(tamvo.getDdate().toString())-1;
				if(index==0){
					index=7;
				}
				String pk_bbs_old1 = tamvo.getPk_bb_old();
				String pk_bb_old1 ="",pk_bb_oldname="";
				if(pk_bbs_old1!=null&&pk_bbs_old1.length()>0){
					String[] pk_bbss_old = pk_bbs_old1.split(",");
					String[] pk_bbnames = tamvo.getBbname_old().split(",");
					
					int count = 0;
					for(String pk_bb:pk_bbss_old){
						if(!list_zb.contains(pk_bb)){
							pk_bb_old1 = pk_bb_old1 + ","+pk_bb;
							pk_bb_oldname = pk_bb_oldname +","+pk_bbnames[count];
						}
						count++;
					}
				}
				
				for(PaibanWeekVO vo:oldhvos){
					
					String vdate = vo.getVdate();
					String begin = vdate.substring(0, 10);
					String end = vdate.substring(11);
					UFDate beginDate = new UFDate(begin);
					UFDate endDate = new UFDate(end);
					int daysBetween = new UFDate().getDaysBetween(beginDate, endDate);
					
					if(pk_bb_oldname != "" && pk_bb_oldname.length()>0 &&  pk_bb_old1 != "" && pk_bb_old1.length() >0){
						if(daysBetween >7){
							vo.setAttributeValue("vbbname"+tamvo.getDdate().getDay(), pk_bb_oldname.substring(1, pk_bb_oldname.length()));
							vo.setAttributeValue("pk_bb"+tamvo.getDdate().getDay(), pk_bb_old1.substring(1, pk_bb_old1.length()));
						}else{
							vo.setAttributeValue("vbbname"+index, pk_bb_oldname.substring(1, pk_bb_oldname.length()));
							vo.setAttributeValue("pk_bb"+index, pk_bb_old1.substring(1, pk_bb_old1.length()));
						}
						
						vo.setStatus(VOStatus.UPDATED);
					}else{
						if(daysBetween >7){
							vo.setAttributeValue("vbbname"+tamvo.getDdate().getDay(), "");
							vo.setAttributeValue("pk_bb"+tamvo.getDdate().getDay(), "");
						}else{
							vo.setAttributeValue("vbbname"+index, "");
							vo.setAttributeValue("pk_bb"+index, "");
						}
						
						vo.setStatus(VOStatus.UPDATED);
					}
					
				}
				list_h_update.addAll(Arrays.asList(oldhvos));
				
				
				if(list_h_update != null && list_h_update.size() > 0){
					HYPubBO_Client.updateAry(list_h_update.toArray(new PaibanWeekVO[0]));
				}
				
				///******/
				
				String pk_paiban = "",dclassperiod="",pk_temp="";
				PanbanWeekBVO[] oldvos = (PanbanWeekBVO[])HYPubBO_Client.queryByCondition(PanbanWeekBVO.class,
						" isnull(dr,0)=0 and pk_psndoc='"+tamvo.getPk_psn()+"' and ddate='"+tamvo.getDdate()+"' ");
				if(oldvos!=null&&oldvos.length>0){
					for(PanbanWeekBVO vo:oldvos){
						vo.setDr(1);
						vo.setStatus(VOStatus.UPDATED);
						pk_paiban = vo.getPk_paiban();
						//dclassperiod = vo.getDclassperiod();
						pk_temp= vo.getPk_temp();
					}
					list_dele.addAll(Arrays.asList(oldvos));
				}
				
				if(list_dele!=null&&list_dele.size()>0){
					HYPubBO_Client.updateAry(list_dele.toArray(new PanbanWeekBVO[0]));
					
				}
				
				String pk_bbs = tamvo.getPk_bb_new();
				
				
				// 
				////			/begin
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd"); 
				Calendar ca = Calendar.getInstance();    
				ca.setTime(tamvo.getDdate().toDate()); 
		        ca.set(Calendar.DAY_OF_MONTH, ca.getActualMaximum(Calendar.DAY_OF_MONTH));  
		        String last = format.format(ca.getTime());

				
				// end
				String pk_bbs_old = tamvo.getPk_bb_old();
				if(pk_bbs_old!=null&&pk_bbs_old.length()>0){
					String[] pk_bbss_old = pk_bbs_old.split(",");
					for(String pk_bb_old:pk_bbss_old){
						PanbanWeekBVO bvo = new PanbanWeekBVO();
						bvo.setPrimaryKey(null);
						bvo.setDr(0);
						bvo.setPk_corp(_getCorp().getPrimaryKey());
						bvo.setPk_psndoc(tamvo.getPk_psn());
						bvo.setPk_dept(tamvo.getPk_dept());
						bvo.setPk_bb(pk_bb_old);
						
						if(list_zb.contains(pk_bb_old)){
							
							ZhibanTempVO[] tempvos = (ZhibanTempVO[])HYPubBO_Client.queryByCondition(ZhibanTempVO.class,
									" isnull(dr,0)=0 and isnull(biscancle,'N')='N'  and pk_bb = '"+pk_bb_old+"' and dbegindate<='"+last+"' and pk_paiban = '"+tamvo.getPktemp_old()+"'");

							if(tempvos !=null && tempvos.length > 0) {
								bvo.setBiszb(new UFBoolean(true));
								bvo.setPk_deptzb(tempvos[0].getPk_dept());
								bvo.setDeptzbname(tempvos[0].getDeptnamekq());
								bvo.setPk_temp(tempvos[0].getPk_paiban());

							}
							
								UFDate ddate = tamvo.getDdate();
								int day = ddate.getDay();
								UFDate dateBefore = ddate.getDateBefore(day-1); //月初
								
								IUAPQueryBS bs= NCLocator.getInstance().lookup(IUAPQueryBS.class);
								String findbvosql = "select * from ("
									+" select * from trtam_paiban_b a where " 
									+"nvl(a.dr,0)=0 and a.biszb ='Y' and a.pk_deptzb like '%"+tempvos[0].getPk_dept()+"%' and a.ddate <= '"+tamvo.getDdate().toString()+"' and a.ddate >='"+dateBefore.toString()+"'order by a.ddate desc ) where rownum =1";
								ArrayList<PanbanWeekBVO> findvos = (ArrayList<PanbanWeekBVO>)bs.executeQuery(findbvosql, new BeanListProcessor(PanbanWeekBVO.class));
								
								if(findvos != null && findvos.size() >0){
									bvo.setVbillstatus1(findvos.get(0).getVbillstatus1());
									bvo.setVbillstatus2(findvos.get(0).getVbillstatus2());
									bvo.setVbillstatus3(findvos.get(0).getVbillstatus3());
								}else{
									bvo.setVbillstatus1("0");
									bvo.setVbillstatus2("0");
									bvo.setVbillstatus3("0");
								}
								

								/*bvo.setBiszb(new UFBoolean(true));
								bvo.setPk_deptzb(tempvos[0].getPk_dept());
								bvo.setDeptzbname(tempvos[0].getDeptnamekq());
								bvo.setPk_temp(tempvos[0].getPk_paiban());
								
								UFDate ddate = tamvo.getDdate();
								int day = ddate.getDay();
								UFDate dateBefore = ddate.getDateBefore(day-1); //月初
								
								PanbanWeekBVO[] findvos = (PanbanWeekBVO[])HYPubBO_Client.queryByCondition(PanbanWeekBVO.class,
										" isnull(dr,0)=0 and biszb = 'Y' and pk_deptzb like'"+tempvos[0].getPk_dept()+"' and ddate <='"+tamvo.getDdate().toString()+"' and ddate >='"+dateBefore.toString()+"' and pk_temp='"+tempvos[0].getPk_paiban()+"' order by ddate desc");
								
								if(findvos != null && findvos.length >0){
									bvo.setVbillstatus1(findvos[0].getVbillstatus1());
									bvo.setVbillstatus2(findvos[0].getVbillstatus2());
									bvo.setVbillstatus3(findvos[0].getVbillstatus3());
								}else{
									bvo.setVbillstatus1("0");
									bvo.setVbillstatus2("0");
									bvo.setVbillstatus3("0");
								}
							*/
								
							
						}else{
							bvo.setBiszb(new UFBoolean(false));
							bvo.setPk_paiban(pk_paiban);
						}
						bvo.setDdate(tamvo.getDdate());
					//	bvo.setDclassperiod(dclassperiod);
						bvo.setMemo(tamvo.getVmemo());
						bvo.setUploadnum(1);
						list_new.add(bvo);
						
					
				
						
					}
				}
				
			}
			
			HYPubBO_Client.updateAry(list.toArray(new AdjustTamVO[0]));
			
			if(list_new!=null&&list_new.size()>0){
				HYPubBO_Client.insertAry(list_new.toArray(new PanbanWeekBVO[0]));
			}
		

			PanbanWeekBVO[] bvos =(PanbanWeekBVO[]) HYPubBO_Client.queryByCondition(PanbanWeekBVO.class, 
					" isnull(dr,0)=0 "+HRPPubTool.formInSQL("pk_psndoc", list_psn)+" and pk_bb in ('10028L100000000002D8','10028L100000000002XL') and ddate like '"+year+"%' ");
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
						" isnull(dr,0)=0 and vyear='"+year+"' and pk_corp='"+_getCorp().getPrimaryKey()+"' "+HRPPubTool.formInSQL("pk_psndoc", list_psn)+" ");
				if(gxvos!=null&&gxvos.length>0){
					for(GxBVO gxvo:gxvos){
						gxvo.setNyygx(map_gxs.get(gxvo.getPk_psndoc()));
						gxvo.setNsxgx((gxvo.getNsfgx()!=null?gxvo.getNsfgx():new UFDouble(0)).sub(gxvo.getNyygx()!=null?gxvo.getNyygx():new UFDouble(0)));
					}
				}
				HYPubBO_Client.updateAry(gxvos);
			}else{
				GxBVO[] gxvos = (GxBVO[])HYPubBO_Client.queryByCondition(GxBVO.class,
						" isnull(dr,0)=0 and vyear='"+year+"' and pk_corp='"+_getCorp().getPrimaryKey()+"' "+HRPPubTool.formInSQL("pk_psndoc", list_psn)+" ");
				if(gxvos!=null&&gxvos.length>0){
					for(GxBVO gxvo:gxvos){
						gxvo.setNyygx(map_gxs.get(gxvo.getPk_psndoc()));
						if(gxvo.getNsfgx()==null){
							gxvo.setNsfgx(new UFDouble(0.0));
						}
						gxvo.setNsxgx(gxvo.getNsfgx().sub(gxvo.getNyygx()!=null?gxvo.getNyygx():new UFDouble(0)));
					}
				}
				HYPubBO_Client.updateAry(gxvos);
			}
		}
		MessageDialog.showHintDlg(this.getBillUI(), "提示", "取消确认");
		
		getBillListPanel().setEnabled(true);		
	}
	@Override
	public void onBoAdd(ButtonObject bo) throws Exception {
		// TODO Auto-generated method stub
		onBoEdit();
		onBoLineAdd();
	}
	@Override
	protected void onBoQuery() throws Exception {
		// TODO Auto-generated method stub
		
//		 需要给查询模板设定过滤条件
		
		
		StringBuffer strWhere = new StringBuffer();

		if (askForQueryCondition(strWhere) == false)
			return;// 用户放弃了查询
		String sql = strWhere.toString();
		if(sql!=null&&sql.trim().length()>0){
			sql += " and vtype=1 and pk_dept in (select pk_docid from bd_tr_userpower where isnull(dr,0)=0 and pk_user='"+_getOperator()+"' and powertype=0 and  pk_corp='"+_getCorp().getPrimaryKey()+"')  and vtype='1' " ;
		}else{
			sql += " isnull(dr,0)=0 and vtype=1 and pk_corp='"+_getCorp().getPrimaryKey()+"' and pk_dept in (select pk_docid from bd_tr_userpower where isnull(dr,0)=0 and pk_user='"+_getOperator()+"' and powertype=0 and  pk_corp='"+_getCorp().getPrimaryKey()+"')  and vtype='1' " ;

		}
		SuperVO[] queryVos = queryHeadVOs(sql);

		getBufferData().clear();
		// 增加数据到Buffer
		addDataToBuffer(queryVos);

		updateBuffer();
		if(getBufferData().getVOBufferSize()<=0){
			getButtonManager().getButton(IBillButton.Audit).setEnabled(false);
			getButtonManager().getButton(IBillButton.CancelAudit).setEnabled(false);
			getBillUI().updateButtons();
		}else{
			getButtonManager().getButton(IBillButton.Audit).setEnabled(true);
			getButtonManager().getButton(IBillButton.CancelAudit).setEnabled(true);
			getBillListPanel().setEnabled(true);	
			getBillUI().updateButtons();
		}
	}
	
	protected boolean askForQueryCondition(StringBuffer sqlWhereBuf)
	throws Exception {
		if (sqlWhereBuf == null)
			throw new IllegalArgumentException(
					"askForQueryCondition().sqlWhereBuf cann't be null");
		final UIDialog querydialog = getQueryUI();
		
		final QueryConditionDLG qcd = new QueryConditionDLG(ui,getTempInfo());
		qcd.registerFieldValueEelementEditorFactory(new IFieldValueElementEditorFactory() {
			public IFieldValueElementEditor createFieldValueElementEditor(FilterMeta meta) {
				// TODO Auto-generated method stub
				if ("pk_dept".equals(meta.getFieldCode())) {
					try {
						String pk_corp = ClientEnvironment.getInstance().getCorporation().getPrimaryKey();
						String operatorid = ClientEnvironment.getInstance().getUser().getPrimaryKey();
						UIRefPane refPane = new UIRefpaneCreator((qcd).getQryCondEditor().getQueryContext()).createUIRefPane(meta);
						refPane.setWhereString(" pk_corp='" + pk_corp+ "' and isnull(bisseal,'N')='N' and pk_dept in (select pk_docid from bd_tr_userpower where isnull(dr,0)=0 and pk_user='"+operatorid+"' and powertype=0 and  pk_corp='"+pk_corp+"') ");
						return new RefElementEditor(refPane, meta.getReturnType());
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
				return null;
			}});
		
		if (qcd.showModal() != UIDialog.ID_OK)
			return false;
		INormalQuery query = (INormalQuery) querydialog;
		
		String strWhere = qcd.getWhereSQL();
		if (strWhere == null || strWhere.trim().length()==0)
			strWhere = "1=1";
		
		if (getButtonManager().getButton(IBillButton.Busitype) != null) {
			if (getBillIsUseBusiCode().booleanValue())
				// 业务类型编码
				strWhere = "(" + strWhere + ") and "
						+ getBillField().getField_BusiCode() + "='"
						+ getBillUI().getBusicode() + "'";
		
			else
				// 业务类型
				strWhere = "(" + strWhere + ") and "
						+ getBillField().getField_Busitype() + "='"
						+ getBillUI().getBusinessType() + "'";
		
		}
		
		strWhere = "(" + strWhere + ") and (isnull(dr,0)=0)";
		
		if (getHeadCondition() != null)
			strWhere = strWhere + " and " + getHeadCondition();
		// 现在我先直接把这个拼好的串放到StringBuffer中而不去优化拼串的过程
		sqlWhereBuf.append(strWhere);
		return true;
		}
	
	protected void onBoLineAdd() throws Exception {
		// TODO Auto-generated method stub
		getBillListPanel().getHeadBillModel().addLine();	
		int lastRow=getBillListPanel().getHeadTable().getRowCount()-1;
		getBillListPanel().getHeadBillModel().setValueAt( _getCorp().getPrimaryKey(), lastRow, "pk_corp");
		getBillListPanel().getHeadBillModel().setValueAt( "2", lastRow, "vtype");
		getBillListPanel().getHeadBillModel().setValueAt(_getDate(), lastRow, "dapplydate");
		//		getBillListPanel().getHeadBillModel().setValueAt(pk_psn, lastRow, "pk_psn");
		getBillListPanel().getHeadBillModel().setValueAt(ClientEnvironment.getInstance().getUser().getUserName(), lastRow, "vdef1");
		getBillListPanel().getHeadBillModel().setValueAt(0, lastRow, "istate");
		getBufferData().setCurrentRow(lastRow);
		getBillListPanel().getHeadBillModel().execLoadFormula();
	}
	@Override
	protected void onBoCommit() throws Exception {
		// TODO Auto-generated method stub
		int[] rows = getBillListPanel().getHeadTable().getSelectedRows();
		for(int i=0;i<rows.length;i++){
			if(getBillListPanel().getHeadBillModel().getValueAt(rows[i], "istate")!=null
					&&getBillListPanel().getHeadBillModel().getValueAt(rows[i], "istate").toString().equals("新增")){
				getBillListPanel().getHeadBillModel().setValueAt(1, rows[i], "istate");
				getBillListPanel().getHeadBillModel().getRowAttribute(rows[i]).setRowState(BillModel.MODIFICATION);
			}
		}
		onBoSave();
		MessageDialog.showHintDlg(this.getBillUI(), "提示", "提交完成");
	};
	@Override
	protected void onBoEdit() throws Exception {
		super.onBoEdit();
	}
	private boolean dataNoNullCheck(){
		BillItem[] items = getBillListPanel().getHeadBillModel().getBodyItems();
		int rowcount = getBillListPanel().getHeadBillModel().getRowCount();
		String msg = "";
		for(int i=0;i<rowcount;i++){
			String msgr = "";
			for(BillItem item:items){
				if(item.isNull()&&(getBillListPanel().getHeadBillModel().getValueAt(i, item.getKey())==null||
						getBillListPanel().getHeadBillModel().getValueAt(i, item.getKey()).toString().trim().length()<=0)){
					msgr+=""+item.getName()+",";
				}
			}
			if(msgr.trim().length()>0){
				msgr = "第"+(i+1)+"行"+msgr+"不可为空;\n";
				msg+=msgr;
			}
		}
		if(msg.trim().length()>0){
			MessageDialog.showHintDlg(this.getBillListPanel(), "提示", msg);
			return false;
		}
		return true;
	}

	@Override
	protected void onBoRefresh() throws Exception {
		// TODO Auto-generated method stub

		((ClientUI)getBillUI()).initData();
	};
	@Override
	protected void onBoSave() throws Exception {
		if(!dataNoNullCheck()) return;
		SuperVO[] changeVOs=(SuperVO[]) getBillListPanel().getHeadBillModel().getBodyValueChangeVOs(getUIController().getBillVoName()[1]);
		//界面全部的VO
		SuperVO[] checkVOs=(SuperVO[]) getBillListPanel().getHeadBillModel().getBodyValueVOs(getUIController().getBillVoName()[1]);
		if(changeVOs.length!=0){
			ArrayList<SuperVO> listDeleteVos = new ArrayList<SuperVO>();
			ArrayList<SuperVO> listUpdateVos = new ArrayList<SuperVO>();
			ArrayList<SuperVO> listInsertVos = new ArrayList<SuperVO>();
			for (int i = 0; i < changeVOs.length; i++) {
				//				dataNotNullValidate(changeVOs[i]);    //验证空值
				switch (changeVOs[i].getStatus()) {   //根据VO状态修改VO
				case VOStatus.DELETED:
					if(changeVOs[i].getPrimaryKey()!=null){
						changeVOs[i].setAttributeValue("dr", new Integer(1));
						listDeleteVos.add(changeVOs[i]);
					}
					break;
				case VOStatus.UPDATED:
					if(changeVOs[i].getPrimaryKey()!=null){
						for(int j=1;j<51;j++){
							Object value = changeVOs[i].getAttributeValue("vbbname"+j+"");
							if(value!=null&&value.toString().trim().length()>0){
								changeVOs[i].setAttributeValue("nxhdays", j);
							}
						}
						listUpdateVos.add(changeVOs[i]);
					}
					break;
				default:
					if(changeVOs[i].getPrimaryKey()==null){
						for(int j=1;j<51;j++){
							Object value = changeVOs[i].getAttributeValue("vbbname"+j+"");
							if(value!=null&&value.toString().trim().length()>0){
								changeVOs[i].setAttributeValue("nxhdays", j);
							}
						}
						listInsertVos.add(changeVOs[i]);
					}
				break;
				}
			}
			//修改数据的数据校验，如果为false则保存失败
			if(listDeleteVos.size()>0){
				if(!checkDeleteVOList(listDeleteVos)){
					return;
				}
			}
			//修改数据的数据校验，如果为false则保存失败
			if(listUpdateVos.size()>0){
				if(!checkUpdateVOList(listUpdateVos)){
					return;
				}
			}
			//插入数据的数据校验，如果为false则保存失败
			if(listInsertVos.size()>0){
				if(!checkInsertVOList(listInsertVos)){
					return;
				}
			}
			//插入数据
			Ihrppub impl = (Ihrppub)NCLocator.getInstance().lookup(Ihrppub.class.getName());
			listInsertVos=impl.saveVOs(listDeleteVos,listUpdateVos,listInsertVos);
			//将插入的数据更新到checkVOs中
			if(listInsertVos.size()>0){
				fillUITotalVO(checkVOs, listInsertVos.toArray(new SuperVO[0]));
			}
			//生成聚合VO更新至缓存
			ArrayList<AggregatedValueObject> listVOs = creatAggregatedValueObjects(checkVOs);
			getBufferData().clear();
			getBufferData().addVOsToBuffer(listVOs.toArray(new HYBillVO[0]));
			getBufferData().refresh();
			updateBuffer();
		}
		// 设置保存后状态
		setSaveOperateState();
		//		onBoRefresh();
	}
}
