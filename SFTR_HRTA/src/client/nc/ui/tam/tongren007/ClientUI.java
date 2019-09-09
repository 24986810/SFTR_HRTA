/**
 * 
 */
package nc.ui.tam.tongren007;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;

import com.sun.media.sound.HsbParser;

import nc.itf.hrp.pub.HRPPubTool;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.bill.BillEditEvent;
import nc.ui.tam.tongren001.KqPsndocRefModel;
import nc.ui.trade.bill.ICardController;
import nc.ui.trade.business.HYPubBO_Client;
import nc.ui.trade.button.IBillButton;
import nc.ui.trade.card.BillCardUI;
import nc.ui.trade.card.CardEventHandler;
import nc.uif.pub.exception.UifException;
import nc.vo.bd.b06.PsndocVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDate;
import nc.vo.tam.button.BenWeekBtnVO;
import nc.vo.tam.button.NextWeekBtnVO;
import nc.vo.tam.button.PreWeekBtnVO;
import nc.vo.tam.button.ShowBCbtnVO;
import nc.vo.tam.button.ShowBbjcbtnVO;
import nc.vo.tam.button.ShowBzbtnVO;
import nc.vo.tam.button.ShowDdbtnVO;
import nc.vo.tam.button.ShowTimebtnVO;
import nc.vo.tam.tongren.power.UserDeptVO;
import nc.vo.tam.tongren003.PanbanWeekBVO;
import nc.vo.tam.tongren006.ZhibanTempVO;
import nc.vo.tam.tongren007.ZhibanWeekVO;
import nc.vo.tbm.tbm_029.BclbHeaderVO;
import nc.vo.trade.button.ButtonVO;

/**
 * @author 28729
 *
 */
public class ClientUI extends BillCardUI {

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
	public ClientUI() {
		// TODO Auto-generated constructor stub
		getButtonManager().getButton(IBillButton.Add).setName("排班");
		getButtonManager().getButton(IBillButton.Copy).setVisible(false);
		getButtonManager().getButton(IBillButton.Copy).setName("复制上月");
		getButtonManager().getButton(804).setName("提交");
		getButtonManager().getButton(803).setName("本月");
		getButtonManager().getButton(802).setName("上月");
		getButtonManager().getButton(801).setName("下月");
		getButtonManager().getButton(IBillButton.Edit).setName("按模板排班");
		getButtonManager().getButton(IBillButton.Print).setEnabled(true);
		
		updateButtons();
		ArrayList<String> list_pk = new ArrayList<String>();
		try {
			UserDeptVO[] deptvos2 = (UserDeptVO[])HYPubBO_Client.queryByCondition(UserDeptVO.class, " isnull(dr,0)=0 and powertype=2 and pk_docid in(select pk_docid from bd_tr_userpower where  isnull(dr,0)=0 and powertype=0 and pk_user='"+_getOperator()+"')");
			if(deptvos2!=null&&deptvos2.length>0){
				for(UserDeptVO vo:deptvos2){
					if(!list_pk.contains(vo.getPk_user())){
						list_pk.add(vo.getPk_user());
					}
				}
			}
		} catch (UifException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		getBillCardPanel().getHeadItem("type").setValue("按月");
		getBillCardPanel().getHeadItem("type").setShow(false);
		getBillCardPanel().getHeadItem("type").setEnabled(false);
		getBillCardPanel().getHeadItem("deptnamekq").setEnabled(true);
//		((UIRefPane)getBillCardPanel().getHeadItem("deptnamekq").getComponent()).setMultiSelectedEnabled(true);
		((UIRefPane)getBillCardPanel().getHeadItem("deptnamekq").getComponent()).setAutoCheck(false);
		((UIRefPane)getBillCardPanel().getHeadItem("deptnamekq").getComponent()).setReturnCode(false);
		String wheredept = 	" pk_corp='" + getCorpPrimaryKey()+ "' and isnull(bisseal,'N')='N' and pk_dept in (select pk_docid from bd_tr_userpower where isnull(dr,0)=0 and pk_user='"+_getOperator()+"' and powertype=0 and  pk_corp='"+_getCorp().getPrimaryKey()+"') ";
		((UIRefPane)getBillCardPanel().getHeadItem("deptnamekq").getComponent()).setWhereString(wheredept);


		UFDate curdate = nc.ui.hr.global.Global.getServerTime().getDate();
		int days = curdate.getDaysMonth();
		int x = curdate.getWeek();
		UFDate begin = new UFDate(curdate.toString().substring(0,8)+"01");
		UFDate end = new UFDate(curdate.toString().substring(0,8)+days);
		getBillCardPanel().getHeadItem("vdate").setValue(begin+"至"+end);
		for(int i=1;i<=31;i++){
			getBillCardPanel().getBillModel().getItemByKey("vbbname"+(i)+"").setName((begin.getDateAfter(i-1)).toString());
			UIRefPane pane = (UIRefPane)getBillCardPanel().getBillModel().getItemByKey("vbbname"+(i)+"").getComponent();
			pane.setRefModel(new KqPsndocRefModel());
			pane.setMultiSelectedEnabled(true);
			pane.setAutoCheck(false);
			pane.setReturnCode(true);
			pane.setTreeGridNodeMultiSelected(true);
			if(i<=days){
				getBillCardPanel().getBillModel().getItemByKey("vbbname"+(i)+"").setShow(true);
			}else{
				getBillCardPanel().getBillModel().getItemByKey("vbbname"+(i)+"").setShow(false);
			}
			getBillCardPanel().getBillModel().getItemByKey("vbbname"+(i)+"").setIDColName("pk_bb"+(i)+"");
		}
		((UIRefPane)getBillCardPanel().getBillModel().getItemByKey("deptnamekq").getComponent()).setMultiSelectedEnabled(true);
		((UIRefPane)getBillCardPanel().getBillModel().getItemByKey("deptnamekq").getComponent()).setAutoCheck(false);
		((UIRefPane)getBillCardPanel().getBillModel().getItemByKey("deptnamekq").getComponent()).setReturnCode(false);
		getBillCardPanel().setBillData(getBillCardPanel().getBillData());
		
		
		
	}

	/**
	 * @param pk_corp
	 * @param pk_billType
	 * @param pk_busitype
	 * @param operater
	 * @param billId
	 */
	public ClientUI(String pk_corp, String pk_billType, String pk_busitype,
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
		return new ClientCtrl();
	}
	@Override
	protected CardEventHandler createEventHandler() {
		// TODO Auto-generated method stub
		return new EventHandler(this,this.createController());
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
	@Override
	public boolean beforeEdit(BillEditEvent e) {
		// TODO Auto-generated method stub
		if(e.getKey().startsWith("vbbname")){
			String vdate = getBillCardPanel().getHeadItem("vdate").getValueObject().toString();
			UFDate begin = new UFDate(vdate.substring(0,10));
			int x = Integer.parseInt(e.getKey().substring(7));
			UFDate editdate = begin.getDateAfter(x-1);
			UIRefPane pane = (UIRefPane)getBillCardPanel().getBillModel().getItemByKey(e.getKey()).getComponent();
			String wheresql = " bd_psndoc.pk_corp='" + _getCorp().getPrimaryKey() + "' " +
			" and isnull(trtam_deptdoc_kq_b.dr,'0')=0 and bd_psndoc.indocflag='Y'" +
			" and trtam_deptdoc_kq_b.dstartdate<='"+editdate+"' and  (trtam_deptdoc_kq_b.denddate is null or trtam_deptdoc_kq_b.denddate>='"+editdate+"' ) ";
			pane.setWhereString(wheresql);
		}
		return super.beforeEdit(e);
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
		HashMap<String,PsndocVO> map_bb = new HashMap<String, PsndocVO>();
		try {
			PsndocVO[] psnvos = (PsndocVO[])HYPubBO_Client.queryByCondition(PsndocVO.class, " isnull(dr,0)=0 and pk_corp='"+_getCorp().getPrimaryKey()+"' ");
			
			for(PsndocVO psnvo:psnvos){
				map_bb.put(psnvo.getPsncode(), psnvo);
				map_bb.put(psnvo.getPsnname(), psnvo);
				map_bb.put(psnvo.getPrimaryKey(), psnvo);
			}

		} catch (BusinessException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}

		// TODO Auto-generated method stub
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
							PsndocVO bcvo = map_bb.get(names[i]);
							if(bcvo!=null){
								list_pks.add(bcvo.getPrimaryKey());
								list_names.add(bcvo.getPsnname());
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
		}else if(e.getPos()==BODY&&e.getKey().equals("deptnamekq")){
			if(e.getValue()!=null){
				UIRefPane pane = (UIRefPane)getBillCardPanel().getBillModel().getItemByKey(e.getKey()).getComponent();
				String[] pks = pane.getRefPKs();
				String[] names = pane.getRefNames();
				if(pks==null){
					getBillCardPanel().getBillModel().setValueAt(null, e.getRow(), e.getKey());
					getBillCardPanel().getBillModel().setValueAt(null, e.getRow(), "pk_dept");
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
				getBillCardPanel().getBillModel().setValueAt(pk_bb, e.getRow(), "pk_dept");
			}else{
				getBillCardPanel().getBillModel().setValueAt(null, e.getRow(), e.getKey());
				getBillCardPanel().getBillModel().setValueAt(null, e.getRow(), "pk_dept");
			}
		}
		else if(e.getPos()==HEAD&&e.getKey().equals("deptnamekq")){
			if(e.getValue()==null){
				getBillCardPanel().getBillModel().clearBodyData();
			}else{
				setdate();
			}
		}else if(e.getPos()==HEAD&&e.getKey().equals("type")){
			UFDate curdate = nc.ui.hr.global.Global.getServerTime().getDate();
			int days = curdate.getDaysMonth();
			int x = curdate.getWeek();
			String type = getBillCardPanel().getHeadItem("type").getValueObject().toString();
			if(type.equals("按月")){
				UFDate begin = new UFDate(curdate.toString().substring(0,8)+"01");
				UFDate end = new UFDate(curdate.toString().substring(0,8)+days);
				getBillCardPanel().getHeadItem("vdate").setValue(begin+"至"+end);
				getButtonManager().getButton(IBillButton.Copy).setName("复制上月");
				getButtonManager().getButton(803).setName("本月");
				getButtonManager().getButton(802).setName("上月");
				getButtonManager().getButton(801).setName("下月");
			}else{
				UFDate begin = curdate.getDateBefore(x-1);
				UFDate end = curdate.getDateAfter(7-x);
				getBillCardPanel().getHeadItem("vdate").setValue(begin+"至"+end);
				getButtonManager().getButton(IBillButton.Copy).setName("复制上周");
				getButtonManager().getButton(803).setName("本周");
				getButtonManager().getButton(802).setName("上周");
				getButtonManager().getButton(801).setName("下周");
			}
			setdate();
			updateButtons();
		}else{
			super.afterEdit(e);
		}
	}
	public void setdate(){
		getBillCardPanel().getBillModel().clearBodyData();
		
		String vdate = getBillCardPanel().getHeadItem("vdate").getValueObject().toString();
		String type = getBillCardPanel().getHeadItem("type").getValueObject().toString();
		UFDate begin = new UFDate(vdate.substring(0,10));
		int days = begin.getDaysMonth();
		UFDate end = new UFDate(vdate.substring(11));
		if(type.equals("按周")){
			String[] names = new String[]{"星期一","星期二","星期三","星期四","星期五","星期六","星期日"};
			for(int i=1;i<32;i++){
				if(i<8){
					String name = names[i-1];
					getBillCardPanel().getBillModel().getItemByKey("vbbname"+(i)+"").setName(name+"("+begin.getDateAfter(i-1)+")");
					getBillCardPanel().getBillModel().getItemByKey("vbbname"+(i)+"").setShow(true);
				}else{
					getBillCardPanel().getBillModel().getItemByKey("vbbname"+(i)+"").setShow(false);
				}
			}
		}else{//按月
			begin = new UFDate(begin.toString().substring(0,8)+"01");
			getBillCardPanel().getHeadItem("vdate").setValue(begin+"至"+end);
			for(int i=1;i<=31;i++){
				if(i<=days){
					getBillCardPanel().getBillModel().getItemByKey("vbbname"+(i)+"").setShow(true);
					getBillCardPanel().getBillModel().getItemByKey("vbbname"+(i)+"").setName((begin.getDateAfter(i-1)).toString());
				}else{
					getBillCardPanel().getBillModel().getItemByKey("vbbname"+(i)+"").setShow(false);
				}
			}
		}
		getBillCardPanel().setBillData(getBillCardPanel().getBillData());
		try {
			String[] pks = ((UIRefPane)getBillCardPanel().getHeadItem("deptnamekq").getComponent()).getRefPKs();
			if(pks==null) return;
			ArrayList<String> list = new ArrayList<String>();
			list.addAll(Arrays.asList(pks));
			PsndocVO[] psnvos = (PsndocVO[])HYPubBO_Client.queryByCondition(PsndocVO.class, " isnull(dr,0)=0 and pk_corp='"+_getCorp().getPrimaryKey()+"' ");
			HashMap<String,PsndocVO> map_psn = new HashMap<String, PsndocVO>();
			for(PsndocVO psnvo:psnvos){
				map_psn.put(psnvo.getPrimaryKey(), psnvo);
			}
			PanbanWeekBVO[] weekbvos = (PanbanWeekBVO[])HYPubBO_Client.queryByCondition(PanbanWeekBVO.class,
					"isnull(dr,0)=0 and pk_corp='"+_getCorp().getPrimaryKey()+"' and isnull(biszb,'N')='Y' and (ddate>='"+begin+"' and ddate<='"+end+"') and  pk_deptzb like '%"+pks[0]+"%' order by pk_bb,ddate ");
			if(weekbvos!=null&&weekbvos.length>0){
				HashMap<String,ZhibanWeekVO> map = new HashMap<String, ZhibanWeekVO>();
				for(PanbanWeekBVO bvos:weekbvos){
					ZhibanWeekVO weekvo = map.get(bvos.getPk_temp())!=null?map.get(bvos.getPk_temp()):new ZhibanWeekVO();
//					 审核状态
					String vbillstatus1 = bvos.getVbillstatus1();
					String vbillstatus2 = bvos.getVbillstatus2();
					String vbillstatus3 = bvos.getVbillstatus3();
					Integer uploadnum = bvos.getUploadnum();
					String vbillstatus = "";
					
					vbillstatus = getStatus(vbillstatus1,vbillstatus2,vbillstatus3);
					// 医务状态
//					if(vbillstatus1 != null){
//						if(vbillstatus1.equals("2")){
//							vbillstatus += "医务审核通过";
//							
//						}else if(vbillstatus1.equals("3")){
//							vbillstatus += "医务退回";
//							
//						}else if(vbillstatus1.equals("4")){
//							vbillstatus += "OA提交";
//							
//						}else if(vbillstatus1.equals("10")){
//							vbillstatus += "审批通过";
//							
//						}
//						
//					}
//					
//					//门办状态
//					if(vbillstatus2 != null){
//						if(vbillstatus2.equals("5")){
//							vbillstatus += "门办审核通过";
//							
//						}else if(vbillstatus2.equals("6")){
//							vbillstatus += "门办退回";
//							
//						}
//					}
//					// 排班状太
//					if(vbillstatus3 != null){
//						if(vbillstatus3.equals("1")){
//							vbillstatus += "提交";
//							
//						}else if(vbillstatus3.equals("0")){
//							vbillstatus += "未提交";
//							
//						}
//					}
					
					weekvo.setVbillstatus(vbillstatus);
					weekvo.setVbillstatus3(vbillstatus3);
					weekvo.setVbillstatus2(vbillstatus2);
					weekvo.setVbillstatus1(vbillstatus1);
					weekvo.setUploadnum(uploadnum);
					weekvo.setPk_bb(bvos.getPk_bb());
					weekvo.setPk_dept(bvos.getPk_deptzb());
					weekvo.setPk_temp(bvos.getPk_temp());
					weekvo.setDeptnamekq(bvos.getDeptzbname());
					UFDate ddate = bvos.getDdate();
					int day = new UFDate().getDaysBetween(begin, ddate);
					weekvo.setAttributeValue("pk_bb"+(day+1)+"", weekvo.getAttributeValue("pk_bb"+(day+1)+"")!=null&&weekvo.getAttributeValue("pk_bb"+(day+1)+"").toString().trim().length()>0?
							(weekvo.getAttributeValue("pk_bb"+(day+1)+"").toString().trim()+","+bvos.getPk_psndoc()+""):bvos.getPk_psndoc());
					weekvo.setAttributeValue("vbbname"+(day+1)+"", weekvo.getAttributeValue("vbbname"+(day+1)+"")!=null&&weekvo.getAttributeValue("vbbname"+(day+1)+"").toString().trim().length()>0?
							(weekvo.getAttributeValue("vbbname"+(day+1)+"").toString().trim()+","+(map_psn.get(bvos.getPk_psndoc())).getPsnname()+""):(map_psn.get(bvos.getPk_psndoc())).getPsnname());
					map.put(bvos.getPk_temp(), weekvo);
				}
				getBillCardPanel().getBillModel().setBodyDataVO(map.values().toArray(new ZhibanWeekVO[0]));
				getBillCardPanel().getBillModel().execLoadFormula();
			}
		} catch (UifException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
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
	}
}
