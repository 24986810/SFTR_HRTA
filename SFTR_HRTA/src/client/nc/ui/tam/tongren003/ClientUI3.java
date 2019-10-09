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
import nc.itf.uap.IUAPQueryBS;
import nc.ui.bd.ref.AbstractRefModel;
import nc.ui.dbcache.UiDBCacheFacade;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.bill.BillEditEvent;
import nc.ui.tam.tongren002.TambblbRefTreeModel;
import nc.ui.trade.base.IBillOperate;
import nc.ui.trade.bill.ICardController;
import nc.ui.trade.business.HYPubBO_Client;
import nc.ui.trade.button.IBillButton;
import nc.ui.trade.card.BillCardUI;
import nc.ui.trade.card.CardEventHandler;
import nc.uif.pub.exception.UifException;
import nc.vo.dbcache.MatchPkVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.btn.ExcelImportBtnVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDouble;
import nc.vo.tam.button.BenWeekBtnVO;
import nc.vo.tam.button.NextWeekBtnVO;
import nc.vo.tam.button.PreWeekBtnVO;
import nc.vo.tam.button.ShowBCbtnVO;
import nc.vo.tam.button.ShowBbjcbtnVO;
import nc.vo.tam.button.ShowBzbtnVO;
import nc.vo.tam.button.ShowDdbtnVO;
import nc.vo.tam.button.ShowTimebtnVO;
import nc.vo.tam.button.UploadWeekBtnVO;
import nc.vo.tam.tongren.power.UserDeptVO;
import nc.vo.tam.tongren003.PaibanWeekVO;
import nc.vo.tam.tongren003.PanbanWeekBVO;
import nc.vo.tam.tongren010.GxBVO;
import nc.vo.tbm.tbm_029.BclbHeaderVO;
import nc.vo.trade.button.ButtonVO;

/**
 * @author 28729
 *
 */
public class ClientUI3 extends BillCardUI {

	/**
	 * 
	 */
	
	private static final long serialVersionUID = 1L;
	private int editstate = 0;//0为非排班状态，1为排班状态
	
	public int getEditstate() {
		return editstate;
	}

	public void setEditstate(int editstate) {
		this.editstate = editstate;
	}

	/**
	 * 
	 */
	public ClientUI3() {
		// TODO Auto-generated constructor stub
		if(getUIControl().getBillType().equals("6017010441")){
			
			getButtonManager().getButton(IBillButton.Add).setName("排班");
			getButtonManager().getButton(IBillButton.Copy).setName("复制上月");
			getButtonManager().getButton(IBillButton.Edit).setName("按模板排班");
			
			getButtonManager().getButton(802).setName("上月");
			getButtonManager().getButton(801).setName("下月");
			getButtonManager().getButton(803).setName("本月");
		}
		getButtonManager().getButton(IBillButton.Print).setEnabled(true);
		getButtonManager().getButton(810).setEnabled(true);
		
		
		getButtonManager().getButton(IBillButton.Add).setEnabled(true);
		updateButtons();
		getBillCardPanel().getHeadItem("deptnamekq").setEnabled(true);
		((UIRefPane)getBillCardPanel().getHeadItem("deptnamekq").getComponent()).setMultiSelectedEnabled(true);
		((UIRefPane)getBillCardPanel().getHeadItem("deptnamekq").getComponent()).setAutoCheck(false);
		((UIRefPane)getBillCardPanel().getHeadItem("deptnamekq").getComponent()).setReturnCode(false);
		String wheredept = 	" pk_corp='" + getCorpPrimaryKey()+ "' and isnull(bisseal,'N')='N' and pk_dept in (select pk_docid from bd_tr_userpower where isnull(dr,0)=0 and pk_user='"+_getOperator()+"' and powertype=0 and  pk_corp='"+_getCorp().getPrimaryKey()+"') ";
		((UIRefPane)getBillCardPanel().getHeadItem("deptnamekq").getComponent()).setWhereString(wheredept);
		
		UFDate curdate = nc.ui.hr.global.Global.getServerTime().getDate();
		int x = curdate.getWeek();
		UFDate begin = new UFDate(getmindate(curdate.toString()));
		UFDate end =  new UFDate(getmaxdate(curdate.toString()));
		int days = end.getDay()-begin.getDay()+1;
		
		for(int i=1;i<=31;i++){
			//			getBillListPanel().getHeadBillModel().getItemByKey("vbbname"+i+"").setComponent(ui);
			UIRefPane pane = (UIRefPane)getBillCardPanel().getBillModel().getItemByKey("vbbname"+i+"").getComponent();
			TambblbRefTreeModel model = new TambblbRefTreeModel("班别档案");
			pane.setRefModel(model);
			//20190925
			//pane.getRefModel().setPKMatch(true);
			pane.setMultiSelectedEnabled(true);
			//			pane.setWhereString(" lbbm<>'DEFAULT'  and isnull(iscancel,'N')='N' and pk_corp='"+_getCorp().getPrimaryKey()+"' and isnull(dr,0)=0 ");
			pane.setAutoCheck(false);
			pane.setReturnCode(true);
			pane.setTreeGridNodeMultiSelected(true);
			pane.setWhereString(" tbm_bclb.pk_corp='" + _getCorp().getPrimaryKey()+"'  and isnull(tbm_bclb.dr,0)=0 and " +
					" isnull(tbm_bclb.iscancel,'N')='N' and tbm_bclb.lbbm<>'DEFAULT'  " +
					" and tbm_bclb.pk_bclbid in (select pk_docid from bd_tr_userpower where isnull(dr,0)=0 and pk_user='"+_getOperator()+"' and powertype=1 and  pk_corp='"+_getCorp().getPrimaryKey()+"')");

		}
		
		
		
//		UFDate begin = curdate.getDateBefore(x-1);
//		UFDate end = curdate.getDateAfter(7-x);
		
		

		getBillCardPanel().getHeadItem("begindate").setValue(begin);
		getBillCardPanel().getHeadItem("enddate").setValue(end);
		getBillCardPanel().getHeadItem("begindate").setEnabled(true);
		getBillCardPanel().getHeadItem("enddate").setEnabled(true);
		
		getBillCardPanel().getHeadItem("voperatorid").setValue(getClientEnvironment().getUser().getPrimaryKey());
		
		getBillCardPanel().getHeadItem("vdate").setValue(begin+"至"+end);
		for(int i=1;i<=31;i++){
			int week = begin.getDateAfter(i-1).getWeek();
			String name = "";
			if(week==0){
				name = "星期日";
			}else if(week==1){
				name = "星期一";
			}else if(week==2){
				name = "星期二";
			}else if(week==3){
				name = "星期三";
			}else if(week==4){
				name = "星期四";
			}else if(week==5){
				name = "星期五";
			}else if(week==6){
				name = "星期六";
			}
			
			//String name = getBillCardPanel().getBillModel().getItemByKey("vbbname"+(i)+"").getName().substring(0,3);
			getBillCardPanel().getBillModel().getItemByKey("vbbname"+(i)+"").setName(name+"("+begin.getDateAfter(i-1)+")");
		
			if(i<=days){
				getBillCardPanel().getBillModel().getItemByKey("vbbname"+(i)+"").setShow(true);
			}else{
				getBillCardPanel().getBillModel().getItemByKey("vbbname"+(i)+"").setShow(false);
			}
		}
		getBillCardPanel().setBillData(getBillCardPanel().getBillData());
		getBillCardPanel().getBillTable().setHeaderHeight(40);
		
	}
	
	public  String getmindate(String begindate) {
		 
         String mindate="";
		try {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");  
			Calendar calendar = Calendar.getInstance();
			Date date = format.parse(begindate);
			calendar.setTime(date);
			calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
			mindate = format.format(calendar.getTime());
		             
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}   
		
       
        
        return mindate;
    }
	
	public  String getmaxdate(String endate){
		String maxdate="";
		try {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd"); 
	        Calendar calendar = Calendar.getInstance();
	        
			Date date = format.parse(endate);
			calendar.setTime(date);
	        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
	  
	        maxdate = format.format(calendar.getTime());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
              
        
        
        return maxdate;
    }

	/**
	 * @param pk_corp
	 * @param pk_billType
	 * @param pk_busitype
	 * @param operater
	 * @param billId
	 */
	public ClientUI3(String pk_corp, String pk_billType, String pk_busitype,
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
		return new ClientCtrl3();
	}
	@Override
	protected CardEventHandler createEventHandler() {
		// TODO Auto-generated method stub
		return new EventHandler3(this,this.createController());
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
	public void afterEdit(BillEditEvent e) {
		// TODO Auto-generated method stub
		IBclbDefining defin = NCLocator.getInstance().lookup(IBclbDefining.class);
		HashMap<String,BclbHeaderVO> map_bb = new HashMap<String, BclbHeaderVO>();
		try {
			BclbHeaderVO[] bclbvos = defin.queryBclb029AllBclbHeader(_getCorp().getPrimaryKey(), null);
			for(BclbHeaderVO bclbvo:bclbvos){
				map_bb.put(bclbvo.getLbbm(), bclbvo);
				map_bb.put(bclbvo.getLbmc(), bclbvo);
				map_bb.put(bclbvo.getPrimaryKey(), bclbvo);
			}

		} catch (BusinessException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}

		if(e.getKey().startsWith("vbbname")){
			int x = Integer.parseInt(e.getKey().substring(7));
			if(e.getValue()!=null){
				
				UIRefPane pane = (UIRefPane)getBillCardPanel().getBillModel().getItemByKey(e.getKey()).getComponent();
			/*	String value = (String)getBillCardPanel().getBillModel().getValueAt(e.getRow(), "pk_bb"+x+"");
				if(value != null){
					UiDBCacheFacade bs = new UiDBCacheFacade();
					String matchSql = pane.getRefModel().getMatchSql(value.split(","));
					MatchPkVO pkVO = new MatchPkVO(matchSql,value.split(","),"tbm_bclb.pk_bclbid");
					Vector vector = bs.matchPK(pkVO, false);
					pane.getRefModel().setSelectedData(vector);
				}*/
				
				String[] pks = pane.getRefPKs();   //pane.getRefModel().m_strPkFieldCode
				String[] names = pane.getRefNames();
				
				if(pks==null||pks.length<=0){ 
					if(e.getValue().toString().trim().length()<=0){
						getBillCardPanel().getBillModel().setValueAt(null, e.getRow(), e.getKey());
						getBillCardPanel().getBillModel().setValueAt(null, e.getRow(), "pk_bb"+x+"");
						return;
					}else{
						names = e.getValue().toString().trim().split(",");
						ArrayList<String> list_pks = new ArrayList<String>();
						ArrayList<String> list_names = new ArrayList<String>();
						for(int i=0;i<names.length;i++){
							BclbHeaderVO bcvo = map_bb.get(names[i]);
							if(bcvo!=null){
								// 判断班别是否为值班 zhanghua
								if(bcvo.getLbmc().indexOf("值班") == -1){
									list_pks.add(bcvo.getPrimaryKey());
									list_names.add(bcvo.getLbmc());
								}
							}
						}
						 pks= list_pks.toArray(new String[0]);
						 names = list_names.toArray(new String[0]);
					}
				} 
				if(pks==null||pks.length<=0){
					getBillCardPanel().getBillModel().setValueAt(null, e.getRow(), e.getKey());
					getBillCardPanel().getBillModel().setValueAt(null, e.getRow(), "pk_bb"+x+"");
					return;
				}
					
				UFBoolean bzb = new UFBoolean("N");
				for(String name:names){
					// 判断班别是否为值班 zhanghua
					if(name.indexOf("值班") != -1){
						bzb = new UFBoolean("Y");
						break;
					}
				}
				
				if(!bzb.booleanValue()){
					String pk_bb = "";
					String vbbname = "";
					for(String pk:pks){
						pk_bb += ""+pk+",";
					}
					pk_bb = pk_bb.substring(0, pk_bb.length()-1);
					for(String name:names){
						vbbname += ""+name+",";
					}
					vbbname = vbbname.substring(0, vbbname.length()-1);
					getBillCardPanel().getBillModel().setValueAt(vbbname, e.getRow(), e.getKey());
					getBillCardPanel().getBillModel().setValueAt(pk_bb, e.getRow(), "pk_bb"+x+"");
				}else{
					getBillCardPanel().getBillModel().setValueAt(null, e.getRow(), e.getKey());
					getBillCardPanel().getBillModel().setValueAt(null, e.getRow(), "pk_bb"+x+"");
				}
			}else{
				getBillCardPanel().getBillModel().setValueAt(null, e.getRow(), e.getKey());
				getBillCardPanel().getBillModel().setValueAt(null, e.getRow(), "pk_bb"+x+"");
			}
			ArrayList<String> gx_list = new ArrayList<String>();
			gx_list.add("10028L100000000002D8");
			gx_list.add("10028L100000000002XL");
			UFDouble gxs = new UFDouble(0);
			UFDouble ngxs = new UFDouble(getBillCardPanel().getBillModel().getValueAt(e.getRow(),"ngxs")!=null?getBillCardPanel().getBillModel().getValueAt(e.getRow(),"ngxs").toString():"0");
			
//			for(int i=1;i<8;i++){
//				String pk_bbs = (String)getBillCardPanel().getBillModel().getValueAt(e.getRow(), "pk_bb"+i+"");
//				if(pk_bbs!=null&&pk_bbs.trim().length()>0){
//					String[] pk_bb = pk_bbs.split(",");
//					for(String pk_bba:pk_bb){
//						if(pk_bba.equals("10028L100000000002D8")){
//							gxs = gxs.add(1);
//						}else if(pk_bba.equals("10028L100000000002XL")){
//							gxs = gxs.add(0.5);
//						}
//					}
//				}
//			}
//			getBillCardPanel().getBillModel().setValueAt(gxs, e.getRow(), "nbcgxs");
//			
//			getBillCardPanel().getBillModel().setValueAt(ngxs.sub(gxs), e.getRow(), "nsygxs");
		}else if(e.getPos()==HEAD&&e.getKey().equals("deptnamekq")){
			if(e.getValue()==null){
				getBillCardPanel().getBillModel().clearBodyData();
			}else{ 
				getBillCardPanel().getBillModel().clearBodyData();
				String[] pks = ((UIRefPane)getBillCardPanel().getHeadItem("deptnamekq").getComponent()).getRefPKs();
				ArrayList<String> list = new ArrayList<String>();
				list.addAll(Arrays.asList(pks)); 
				String vdate = getBillCardPanel().getHeadItem("vdate").getValueObject().toString();
				try {
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
							"isnull(dr,0)=0 and pk_corp='"+_getCorp().getPrimaryKey()+"' and vdate='"+vdate+"' "+HRPPubTool.formInSQL("pk_dept", list)+" ");
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
						getBillCardPanel().getBillModel().setBodyDataVO(weekvos);
						if(weekvos!=null&&weekvos.length>0){
							if("6".equals(weekvos[0].getVbillstatus2())||"3".equals(weekvos[0].getVbillstatus1())||"0".equals(weekvos[0].getVbillstatus3())){
								getButtonManager().getButton(IBillButton.Save).setEnabled(true);
							}else{
								getButtonManager().getButton(IBillButton.Save).setEnabled(false);
							}
						}
						
						getBillCardPanel().getBillModel().execLoadFormula();
						getBillCardPanel().setEnabled(getEditstate()==1);
						getBillCardPanel().getHeadItem("deptnamekq").setEnabled(getEditstate()==0);
						
					}

					UFDate begin = new UFDate(vdate.substring(0,10));
					UFDate end = new UFDate(vdate.substring(11));
					int rowcount = getBillCardPanel().getBillModel().getRowCount();
					ArrayList<String> pk_list_psn = new ArrayList<String>();
					for(int i=0;i<rowcount;i++){
						String pk_psndoc = getBillCardPanel().getBillModel().getValueAt(i, "pk_psndoc").toString().trim();
						pk_list_psn.add(pk_psndoc);
					}
					
					if(pk_list_psn.size() >0){
						PanbanWeekBVO[] panbvos = (PanbanWeekBVO[])HYPubBO_Client.queryByCondition(PanbanWeekBVO.class,
								" isnull(dr,0)=0 and pk_corp='"+_getCorp().getPrimaryKey()+"' and isnull(biszb,'N')='Y' "+HRPPubTool.formInSQL("pk_psndoc", pk_list_psn)+" and ddate<='"+end+"' and ddate>='"+begin+"' ");
						ArrayList<String> list_zb = new ArrayList<String>();
						if(panbvos!=null&&panbvos.length>0){
							for(PanbanWeekBVO panbvo:panbvos){
								list_zb.add(panbvo.getPk_psndoc().trim()+panbvo.getDdate().toString().trim());
							}
						}
						for(int i=0;i<rowcount;i++){
							for(int j=1;j<8;j++){
								UFDate showdate = begin.getDateAfter(j-1);
								int index = getBillCardPanel().getBillModel().getBodyColByKey("vbbname"+(j)+"");
								String pk_psndoc = getBillCardPanel().getBillModel().getValueAt(i, "pk_psndoc").toString().trim();
								if(list_zb.contains(pk_psndoc+showdate)){
									getBillCardPanel().getBillModel().setBackground(Color.pink, i, index);
								}
							}
						}
					}
					
					
					getBillCardPanel().getHeadItem("begindate").setEnabled(true);
					getBillCardPanel().getHeadItem("enddate").setEnabled(true);
					// 
					
					
					
				} catch (BusinessException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}else if(e.getPos()==HEAD&&e.getKey().equals("begindate")){
			String begindate1 = getBillCardPanel().getHeadItem("begindate").getValueObject().toString();
			//String enddate1 = getBillCardPanel().getHeadItem("enddate").getValueObject().toString();
			
			UFDate curdate = new UFDate(begindate1);
			int x = curdate.getWeek();
			UFDate begin = new UFDate(getmindate(curdate.toString()));
			UFDate end =  new UFDate(getmaxdate(curdate.toString()));
			
			getBillCardPanel().getHeadItem("vdate").setValue(begin+"至"+end);
			getBillCardPanel().getHeadItem("enddate").setValue(end);
			
		}else if(e.getPos()==HEAD&&e.getKey().equals("enddate")){
			String begindate1 = getBillCardPanel().getHeadItem("begindate").getValueObject().toString();
			String enddate1 = getBillCardPanel().getHeadItem("enddate").getValueObject().toString();
			
			UFDate curdate = new UFDate(enddate1);
			int x = curdate.getWeek();
			UFDate begin = new UFDate(getmindate(curdate.toString()));
			UFDate end =  new UFDate(getmaxdate(curdate.toString()));

			getBillCardPanel().getHeadItem("vdate").setValue(begin+"至"+end);
			getBillCardPanel().getHeadItem("begindate").setValue(begin);
			getBillCardPanel().getHeadItem("enddate").setValue(end);
			
		}
	}
	@Override
	public boolean beforeEdit(BillEditEvent e) {
		
		// TODO Auto-generated method stub
		if(e.getKey().startsWith("vbbname")){
			int x = Integer.parseInt(e.getKey().substring(7));
			
			UIRefPane pane = (UIRefPane)getBillCardPanel().getBillModel().getItemByKey(e.getKey()).getComponent();
			String pk_dept = getBillCardPanel().getBillModel().getValueAt(e.getRow(), "pk_dept").toString();
			
			String value = (String)getBillCardPanel().getBillModel().getValueAt(e.getRow(), "pk_bb"+x+"");
			if(value!=null&&value.trim().length()>=0){
				pane.setPKs(value.split(","));
			}
			ArrayList<String> list_pk = new ArrayList<String>();
			try {
				UserDeptVO[] deptvos2 = (UserDeptVO[])HYPubBO_Client.queryByCondition(UserDeptVO.class, " isnull(dr,0)=0 and powertype=2 and pk_docid='"+pk_dept+"' ");
				if(deptvos2!=null&&deptvos2.length>0){
					for(UserDeptVO vo:deptvos2){
						if(!list_pk.contains(vo.getPk_user())){
							list_pk.add(vo.getPk_user());
						}
					}
				}
			} catch (UifException ee) {
				// TODO Auto-generated catch block
				ee.printStackTrace();
			}
			String sql = " tbm_bclb.pk_corp='" + _getCorp().getPrimaryKey()+"'  and isnull(tbm_bclb.dr,0)=0 and " +
			" isnull(tbm_bclb.iscancel,'N')='N' and tbm_bclb.lbbm<>'DEFAULT' and tbm_bclb.pk_bbz not in ('00018L1000000010I5FM','00018L1000000010I5FS','00018L1000000010J12W','00018L1000000010J12V','00018L1000000010J12X')  ";
			if(list_pk!=null&&list_pk.size()>0){
				sql += ""+HRPPubTool.formInSQL("tbm_bclb.pk_bclbid", list_pk)+"" ;
			}else{
				sql+= " and 1=2 ";
			}
			pane.setWhereString(sql);
		}
		
		/*int x = Integer.parseInt(e.getKey().substring(7));
		//if(e.getValue()!=null){
		//
		UIRefPane pane = (UIRefPane)getBillCardPanel().getBillModel().getItemByKey(e.getKey()).getComponent();
		String value = (String)getBillCardPanel().getBillModel().getValueAt(e.getRow(), "pk_bb"+x+"");
		if(value != null){
			UiDBCacheFacade bs = new UiDBCacheFacade();
			String matchSql = pane.getRefModel().getMatchSql(value.split(","));
			MatchPkVO pkVO = new MatchPkVO(matchSql,value.split(","),"tbm_bclb.pk_bclbid");
			Vector vector = bs.matchPK(pkVO, false);
			pane.getRefModel().setSelectedData(vector);
		}
		
		String[] pks = pane.getRefPKs();   //pane.getRefModel().m_strPkFieldCode
		String[] names = pane.getRefNames();
		
		if(pks==null||pks.length<=0){ 
			if(e.getValue().toString().trim().length()<=0){
				getBillCardPanel().getBillModel().setValueAt(null, e.getRow(), e.getKey());
				getBillCardPanel().getBillModel().setValueAt(null, e.getRow(), "pk_bb"+x+"");
				return super.beforeEdit(e);
			}else{
				names = e.getValue().toString().trim().split(",");
				ArrayList<String> list_pks = new ArrayList<String>();
				ArrayList<String> list_names = new ArrayList<String>();
				for(int i=0;i<names.length;i++){
					IBclbDefining defin = NCLocator.getInstance().lookup(IBclbDefining.class);
					HashMap<String,BclbHeaderVO> map_bb = new HashMap<String, BclbHeaderVO>();
					try {
						BclbHeaderVO[] bclbvos = defin.queryBclb029AllBclbHeader(_getCorp().getPrimaryKey(), null);
						for(BclbHeaderVO bclbvo:bclbvos){
							map_bb.put(bclbvo.getLbbm(), bclbvo);
							map_bb.put(bclbvo.getLbmc(), bclbvo);
							map_bb.put(bclbvo.getPrimaryKey(), bclbvo);
						}

					} catch (BusinessException e2) {
						// TODO Auto-generated catch block
						e2.printStackTrace();
					}
					
					BclbHeaderVO bcvo = map_bb.get(names[i]);
					if(bcvo!=null){
						// 判断班别是否为值班 zhanghua
						if(bcvo.getLbmc().indexOf("值班") == -1){
							list_pks.add(bcvo.getPrimaryKey());
							list_names.add(bcvo.getLbmc());
						}
					}
				}
				 pks= list_pks.toArray(new String[0]);
				 names = list_names.toArray(new String[0]);
			}
		} 
		if(pks==null||pks.length<=0){
			getBillCardPanel().getBillModel().setValueAt(null, e.getRow(), e.getKey());
			getBillCardPanel().getBillModel().setValueAt(null, e.getRow(), "pk_bb"+x+"");
			return super.beforeEdit(e);
		}
			
		UFBoolean bzb = new UFBoolean("N");
		for(String name:names){
			// 判断班别是否为值班 zhanghua
			if(name.indexOf("值班") != -1){
				bzb = new UFBoolean("Y");
				break;
			}
		}
		
		if(!bzb.booleanValue()){
			String pk_bb = "";
			String vbbname = "";
			for(String pk:pks){
				pk_bb += ""+pk+",";
			}
			pk_bb = pk_bb.substring(0, pk_bb.length()-1);
			for(String name:names){
				vbbname += ""+name+",";
			}
			vbbname = vbbname.substring(0, vbbname.length()-1);
			getBillCardPanel().getBillModel().setValueAt(vbbname, e.getRow(), e.getKey());
			getBillCardPanel().getBillModel().setValueAt(pk_bb, e.getRow(), "pk_bb"+x+"");
		}else{
			getBillCardPanel().getBillModel().setValueAt(null, e.getRow(), e.getKey());
			getBillCardPanel().getBillModel().setValueAt(null, e.getRow(), "pk_bb"+x+"");
		}
	}else{
		getBillCardPanel().getBillModel().setValueAt(null, e.getRow(), e.getKey());
		getBillCardPanel().getBillModel().setValueAt(null, e.getRow(), "pk_bb"+x+"");
	}*/
	
		return super.beforeEdit(e);
	}

	@Override
	protected void initPrivateButton() {
		// TODO Auto-generated method stub
		ButtonVO importbtn = new BenWeekBtnVO().getButtonVO();
		addPrivateButton(importbtn);
		ButtonVO PreWeebtn = new PreWeekBtnVO().getButtonVO();
		addPrivateButton(PreWeebtn);
		ButtonVO nextWeebtn = new NextWeekBtnVO().getButtonVO();
		addPrivateButton(nextWeebtn);
		ButtonVO showbcbtn = new ShowBCbtnVO().getButtonVO();
		addPrivateButton(showbcbtn);
		ButtonVO showddbtn = new ShowDdbtnVO().getButtonVO();
		addPrivateButton(showddbtn);
		ButtonVO ShowTimebtn = new ShowTimebtnVO().getButtonVO();
		addPrivateButton(ShowTimebtn);
		ButtonVO ShowBbjcbtn = new ShowBbjcbtnVO().getButtonVO();
		addPrivateButton(ShowBbjcbtn);
		ButtonVO ShowBzbtn = new ShowBzbtnVO().getButtonVO();
		addPrivateButton(ShowBzbtn);
		ButtonVO Uploadbtn = new UploadWeekBtnVO().getButtonVO();
		addPrivateButton(Uploadbtn);
	}
}
