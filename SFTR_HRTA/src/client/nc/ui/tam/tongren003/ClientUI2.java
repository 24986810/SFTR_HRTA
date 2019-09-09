/**
 * 
 */
package nc.ui.tam.tongren003;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
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
 * ����
 * @author 28729
 *
 */
public class ClientUI2 extends BillCardUI {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int editstate = 0;//0Ϊ���Ű�״̬��1Ϊ�Ű�״̬
	public int getEditstate() {
		return editstate;
	}

	public void setEditstate(int editstate) {
		this.editstate = editstate;
	}

	/**
	 * 
	 */
	public ClientUI2() {
		// TODO Auto-generated constructor stub
		if(getUIControl().getBillType().equals("6017010440")){
			getButtonManager().getButton(IBillButton.Add).setName("�Ű�");
			getButtonManager().getButton(IBillButton.Copy).setName("��������");
			getButtonManager().getButton(IBillButton.Edit).setName("��ģ���Ű�");
		}
		getButtonManager().getButton(IBillButton.Print).setEnabled(true);
	
		getButtonManager().getButton(IBillButton.Add).setEnabled(true);
		updateButtons();
		getBillCardPanel().getHeadItem("deptnamekq").setEnabled(true);
		((UIRefPane)getBillCardPanel().getHeadItem("deptnamekq").getComponent()).setMultiSelectedEnabled(true);
		((UIRefPane)getBillCardPanel().getHeadItem("deptnamekq").getComponent()).setAutoCheck(false);
		((UIRefPane)getBillCardPanel().getHeadItem("deptnamekq").getComponent()).setReturnCode(false);
		String wheredept = 	" pk_corp='" + getCorpPrimaryKey()+ "' and isnull(bisseal,'N')='N' and pk_dept in (select pk_docid from bd_tr_userpower where isnull(dr,0)=0 and pk_user='"+_getOperator()+"' and powertype=0 and  pk_corp='"+_getCorp().getPrimaryKey()+"') ";
		((UIRefPane)getBillCardPanel().getHeadItem("deptnamekq").getComponent()).setWhereString(wheredept);

		for(int i=1;i<8;i++){
			//			getBillListPanel().getHeadBillModel().getItemByKey("vbbname"+i+"").setComponent(ui);
			UIRefPane pane = (UIRefPane)getBillCardPanel().getBillModel().getItemByKey("vbbname"+i+"").getComponent();
			TambblbRefTreeModel model = new TambblbRefTreeModel("��𵵰�");
			pane.setRefModel(model);
			pane.setMultiSelectedEnabled(true);
			//			pane.setWhereString(" lbbm<>'DEFAULT'  and isnull(iscancel,'N')='N' and pk_corp='"+_getCorp().getPrimaryKey()+"' and isnull(dr,0)=0 ");
			pane.setAutoCheck(false);
			pane.setReturnCode(true);
			pane.setTreeGridNodeMultiSelected(true);
			pane.setWhereString(" tbm_bclb.pk_corp='" + _getCorp().getPrimaryKey()+"'  and isnull(tbm_bclb.dr,0)=0 and " +
					" isnull(tbm_bclb.iscancel,'N')='N' and tbm_bclb.lbbm<>'DEFAULT'  " +
					" and tbm_bclb.pk_bclbid in (select pk_docid from bd_tr_userpower where isnull(dr,0)=0 and pk_user='"+_getOperator()+"' and powertype=1 and  pk_corp='"+_getCorp().getPrimaryKey()+"')");

		}
		UFDate curdate = nc.ui.hr.global.Global.getServerTime().getDate();
//		int x = curdate.getWeek();
//		UFDate begin = curdate.getDateBefore(x-1);
//		UFDate end = curdate.getDateAfter(7-x);
		
		UFDate adddate = curdate.getDateBefore(14);
		int addx = adddate.getWeek();
		UFDate begin = adddate.getDateBefore(addx-1);
		UFDate end = adddate.getDateAfter(7-addx);

		getBillCardPanel().getHeadItem("begindate").setValue(begin);
		getBillCardPanel().getHeadItem("enddate").setValue(end);
		getBillCardPanel().getHeadItem("begindate").setEnabled(true);
		getBillCardPanel().getHeadItem("enddate").setEnabled(true);
		
		getBillCardPanel().getHeadItem("voperatorid").setValue(getClientEnvironment().getUser().getPrimaryKey());
		
		getBillCardPanel().getHeadItem("vdate").setValue(begin+"��"+end);
		for(int i=1;i<8;i++){
			String name = getBillCardPanel().getBillModel().getItemByKey("vbbname"+(i)+"").getName().substring(0,3);
			getBillCardPanel().getBillModel().getItemByKey("vbbname"+(i)+"").setName(name+"("+begin.getDateAfter(i-1)+")");
		}
		getBillCardPanel().setBillData(getBillCardPanel().getBillData());
		getBillCardPanel().getBillTable().setHeaderHeight(40);
	}

	/**
	 * @param pk_corp
	 * @param pk_billType
	 * @param pk_busitype
	 * @param operater
	 * @param billId
	 */
	public ClientUI2(String pk_corp, String pk_billType, String pk_busitype,
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
		return new ClientCtrl2();
	}
	@Override
	protected CardEventHandler createEventHandler() {
		// TODO Auto-generated method stub
		return new EventHandler2(this,this.createController());
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
								// �жϰ���Ƿ�Ϊֵ�� zhanghua
								if(bcvo.getLbmc().indexOf("ֵ��") == -1){
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
					// �жϰ���Ƿ�Ϊֵ�� zhanghua
					if(name.indexOf("ֵ��") != -1){
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
					PaibanWeekVO[] weekvos = (PaibanWeekVO[])HYPubBO_Client.queryByCondition(PaibanWeekVO.class,
							"isnull(dr,0)=0 and pk_corp='"+_getCorp().getPrimaryKey()+"' and vdate='"+vdate+"' "+HRPPubTool.formInSQL("pk_dept", list)+" ");
					if(weekvos!=null&&weekvos.length>0){
						for(PaibanWeekVO weekvo:weekvos){
							weekvo.setNgxs(map.get(weekvo.getPk_psndoc()));
							weekvo.setNsygxs(map.get(weekvo.getPk_psndoc()));
						}
						
						getBillCardPanel().getBillModel().setBodyDataVO(weekvos);
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
					
//					EventHandler myeventhandler = (EventHandler) createEventHandler();
//					String upstatus = myeventhandler.getUploadStatus(vdate, list);
//					
//					if(upstatus.equals("0") || upstatus.equals("2")){
//						getButtonManager().getButton(IBillButton.Add).setEnabled(true);
//						updateButtons();
//					}else{
//						getButtonManager().getButton(IBillButton.Add).setEnabled(false);
//						updateButtons();
//					}
					
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
			UFDate begin = curdate.getDateBefore(x-1);
			UFDate end = curdate.getDateAfter(7-x);

			getBillCardPanel().getHeadItem("vdate").setValue(begin+"��"+end);
			getBillCardPanel().getHeadItem("enddate").setValue(end);
			
		}else if(e.getPos()==HEAD&&e.getKey().equals("enddate")){
			String begindate1 = getBillCardPanel().getHeadItem("begindate").getValueObject().toString();
			String enddate1 = getBillCardPanel().getHeadItem("enddate").getValueObject().toString();
			
			UFDate curdate = new UFDate(enddate1);
			int x = curdate.getWeek();
			UFDate end = curdate.getDateBefore(x-1);
			UFDate  begin = curdate.getDateAfter(x-6);

			getBillCardPanel().getHeadItem("vdate").setValue(begin+"��"+end);
			getBillCardPanel().getHeadItem("begindate").setValue(begin);
			
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
