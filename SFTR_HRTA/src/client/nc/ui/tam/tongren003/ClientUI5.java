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

import nc.bs.framework.common.NCLocator;
import nc.itf.hr.ta.IBclbDefining;
import nc.itf.hrp.pub.HRPPubTool;
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
import nc.vo.pub.BusinessException;
import nc.vo.pub.btn.ExcelImportBtnVO;
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
import nc.vo.tam.tongren.power.UserClassDeptVO;
import nc.vo.tam.tongren.power.UserDeptVO;
import nc.vo.tam.tongren001.DeptKqVO;
import nc.vo.tam.tongren003.PaibanWeekVO;
import nc.vo.tam.tongren003.PanbanWeekBVO;
import nc.vo.tam.tongren010.GxBVO;
import nc.vo.tbm.tbm_029.BclbHeaderVO;
import nc.vo.trade.button.ButtonVO;

/**
 * @author 28729
 *
 */
public class ClientUI5 extends BillCardUI {

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
	public ClientUI5() {
		// TODO Auto-generated constructor stub
		if(getUIControl().getBillType().equals("6017010442")){
			getButtonManager().getButton(IBillButton.Add).setName("查询");
			getButtonManager().getButton(801).setName("全消");
			getButtonManager().getButton(802).setName("全选");
			getButtonManager().getButton(803).setName("审核");
			getButtonManager().getButton(804).setName("退回");
			getButtonManager().getButton(805).setName("上传OA");
		
		}
		
		
		getButtonManager().getButton(IBillButton.Add).setEnabled(true);
		updateButtons();
		getBillCardPanel().getHeadItem("deptnamekq").setEnabled(true);
		getBillCardPanel().getHeadItem("vbillstatus").setEnabled(true);
		getBillCardPanel().getHeadItem("classtype").setEnabled(false);
		((UIRefPane)getBillCardPanel().getHeadItem("deptnamekq").getComponent()).setMultiSelectedEnabled(true);
		((UIRefPane)getBillCardPanel().getHeadItem("deptnamekq").getComponent()).setAutoCheck(false);
		((UIRefPane)getBillCardPanel().getHeadItem("deptnamekq").getComponent()).setReturnCode(false);
		String wheredept = 	" pk_corp='" + getCorpPrimaryKey()+ "' and isnull(bisseal,'N')='N' and pk_dept in (select pk_docid from bd_tr_userclasspower where isnull(dr,0)=0 and pk_user='"+_getOperator()+"' and powertype=0 and  pk_corp='"+_getCorp().getPrimaryKey()+"') ";
		((UIRefPane)getBillCardPanel().getHeadItem("deptnamekq").getComponent()).setWhereString(wheredept);
		
		try {
			ArrayList<String> list_dept = new ArrayList<String>();
			/*UserDeptVO[] deptvos = (UserDeptVO[])HYPubBO_Client.queryByCondition(UserDeptVO.class, " isnull(dr,0)=0 and pk_user='"+_getOperator()+"' and powertype=0 and  pk_corp='"+_getCorp().getPrimaryKey()+"' ");
			if(deptvos!=null&&deptvos.length>0){
				for(UserDeptVO deptvo:deptvos){
					list_dept.add(deptvo.getPk_docid());
				}
			}*/
			UserClassDeptVO[] userclassdeptvos = (UserClassDeptVO[])HYPubBO_Client.queryByCondition(UserClassDeptVO.class, " isnull(dr,0)=0 and pk_user='"+_getOperator()+"' and powertype=0 and  pk_corp='"+_getCorp().getPrimaryKey()+"' ");
			if(userclassdeptvos!=null&&userclassdeptvos.length>0){
				for(UserClassDeptVO userclassdeptvo:userclassdeptvos){
					list_dept.add(userclassdeptvo.getPk_docid());
				}
			}
			((UIRefPane)getBillCardPanel().getHeadItem("deptnamekq").getComponent()).setPKs(list_dept.toArray(new String[0]));
		} catch (UifException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		UFDate curdate = nc.ui.hr.global.Global.getServerTime().getDate();
		int x = curdate.getWeek();
		
		UFDate begin = curdate.getDateBefore(x-1);
		UFDate end = curdate.getDateAfter(7-x);
		int days = 7;


		getBillCardPanel().getHeadItem("begindate").setValue(begin);
		getBillCardPanel().getHeadItem("enddate").setValue(end);
		getBillCardPanel().getHeadItem("begindate").setEnabled(true);
		getBillCardPanel().getHeadItem("enddate").setEnabled(true);
		
		getBillCardPanel().getHeadItem("vbillstatus").setValue("8");
		getBillCardPanel().getHeadItem("classtype").setValue("2");
		
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
		
		EventHandler5 ev5 = (EventHandler5) createEventHandler(); 
		try {
			ev5.onBoAdd(getButtonManager().getButton(IBillButton.Add));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		try {
//			this.setBillOperate(IBillOperate.OP_ADD);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
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
	public ClientUI5(String pk_corp, String pk_billType, String pk_busitype,
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
		return new ClientCtrl5();
	}
	@Override
	protected CardEventHandler createEventHandler() {
		// TODO Auto-generated method stub
		return new EventHandler5(this,this.createController());
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
				String[] pks = pane.getRefPKs();
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
								list_pks.add(bcvo.getPrimaryKey());
								list_names.add(bcvo.getLbmc());
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
			
		}else if(e.getPos()==HEAD&&e.getKey().equals("begindate")){
			String begindate1 = getBillCardPanel().getHeadItem("begindate").getValueObject().toString();
			//String enddate1 = getBillCardPanel().getHeadItem("enddate").getValueObject().toString();
			
			UFDate curdate = new UFDate(begindate1);
			int x = curdate.getWeek();
			UFDate begin = curdate.getDateBefore(x-1);
			UFDate end = curdate.getDateAfter(7-x);

			getBillCardPanel().getHeadItem("vdate").setValue(begin+"至"+end);
			getBillCardPanel().getHeadItem("enddate").setValue(end);
			
		}else if(e.getPos()==HEAD&&e.getKey().equals("enddate")){
			String begindate1 = getBillCardPanel().getHeadItem("begindate").getValueObject().toString();
			String enddate1 = getBillCardPanel().getHeadItem("enddate").getValueObject().toString();
			
			UFDate curdate = new UFDate(enddate1);
			int x = curdate.getWeek();
			UFDate end = curdate.getDateBefore(x-1);
			UFDate  begin = curdate.getDateAfter(x-6);

			getBillCardPanel().getHeadItem("vdate").setValue(begin+"至"+end);
			getBillCardPanel().getHeadItem("begindate").setValue(begin);
			
		}if(e.getPos() == BODY && e.getKey().equals("flag")){
			Object temp = getBillCardPanel().getBodyValueAt(e.getRow(), "flag"); 
			String classtype = (String)getBillCardPanel().getHeadItem("classtype").getValueObject();
			setRowSelect(temp,classtype,e);
			
			String vbillstatus = (String) getBillCardPanel().getBillModel().getValueAt(e.getRow(),"vbillstatus");
			if(vbillstatus != null){
				if(vbillstatus.equals("提交")){
					getButtonManager().getButton(803).setEnabled(true);
					getButtonManager().getButton(804).setEnabled(true);
					getButtonManager().getButton(805).setEnabled(false);
					
					updateButtons();
				}else if(vbillstatus.equals("审核通过")||vbillstatus.equals("医务审核通过")||vbillstatus.equals("门办审核通过")){
					getButtonManager().getButton(805).setEnabled(true);
					
					updateButtons();
				}else if(vbillstatus.equals("医务审核通过")||vbillstatus.equals("门办审核通过")){
					getButtonManager().getButton(803).setEnabled(true);
					getButtonManager().getButton(804).setEnabled(false);
					getButtonManager().getButton(805).setEnabled(false);
					
					updateButtons();
				}
			}
		}
	}
	
	public void setRowSelect(Object temp,String classtype, BillEditEvent e){
		
		if(temp != null && temp.toString().equals("false")){
			int rowcount = getBillCardPanel().getBillTable().getRowCount();
			String sel_parent = (String) getBillCardPanel().getBillModel().getValueAt(e.getRow(),"pk_parent");
			if(sel_parent == null)sel_parent = (String) getBillCardPanel().getBillModel().getValueAt(e.getRow(),"pk_dept");
			String sel_deptzb = (String) getBillCardPanel().getBillModel().getValueAt(e.getRow(),"pk_deptzb");

			for(int i = 0;i < rowcount;i++){
				String pk_currdept = (String) getBillCardPanel().getBillModel().getValueAt(i,"pk_dept");
				String pk_parent = (String) getBillCardPanel().getBillModel().getValueAt(i,"pk_parent");
				String pk_deptzb = (String) getBillCardPanel().getBillModel().getValueAt(i,"pk_deptzb");
				
				if(classtype.equals("1")){
//					if(sel_deptzb.equals(pk_deptzb)){
//						getBillCardPanel().getBillModel().setValueAt("N", i, "flag");
//					}
					String[] pk_zbs = pk_deptzb.split(",");
					String[] pk_sel_zbs = sel_deptzb.split(",");
					
					for(int j = 0;j < pk_zbs.length;j++){
						String pk_zb = pk_zbs[j];
						for(int k = 0;k < pk_sel_zbs.length;k++){
							String pk_sel_zb = pk_sel_zbs[k];
							if(pk_zb.equals(pk_sel_zb)){
								getBillCardPanel().getBillModel().setValueAt("N", i, "flag");
							}
						}
					}
				}else{
					if(pk_parent == null){
						pk_parent = pk_currdept;
					}
					
					if(sel_parent.equals(pk_parent)){
						getBillCardPanel().getBillModel().setValueAt("N", i, "flag");
					}
				}
				
			}
		}else{
			int rowcount = getBillCardPanel().getBillTable().getRowCount();
			String sel_parent = (String) getBillCardPanel().getBillModel().getValueAt(e.getRow(),"pk_parent");
			if(sel_parent == null)sel_parent = (String) getBillCardPanel().getBillModel().getValueAt(e.getRow(),"pk_dept");
			String sel_deptzb = (String) getBillCardPanel().getBillModel().getValueAt(e.getRow(),"pk_deptzb");

			for(int i = 0;i < rowcount;i++){
				String pk_currdept = (String) getBillCardPanel().getBillModel().getValueAt(i,"pk_dept");
				String pk_parent = (String) getBillCardPanel().getBillModel().getValueAt(i,"pk_parent");
				String pk_deptzb = (String) getBillCardPanel().getBillModel().getValueAt(i,"pk_deptzb");
				
				if(classtype.equals("1")){
					String[] pk_zbs = pk_deptzb.split(",");
					String[] pk_sel_zbs = sel_deptzb.split(",");
					
					for(int j = 0;j < pk_zbs.length;j++){
						String pk_zb = pk_zbs[j];
						for(int k = 0;k < pk_sel_zbs.length;k++){
							String pk_sel_zb = pk_sel_zbs[k];
							if(pk_zb.equals(pk_sel_zb)){
								getBillCardPanel().getBillModel().setValueAt("Y", i, "flag");
							}
						}
					}
					
//					if(sel_deptzb.equals(pk_deptzb)){
//						getBillCardPanel().getBillModel().setValueAt("Y", i, "flag");
//					}
				}else{
					if(pk_parent == null){
						pk_parent = pk_currdept;
					}
					if(sel_parent.equals(pk_parent)){
						getBillCardPanel().getBillModel().setValueAt("Y", i, "flag");
					}
				}
				
			}
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
