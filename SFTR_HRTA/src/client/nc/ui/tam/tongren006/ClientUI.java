package nc.ui.tam.tongren006;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import nc.bs.framework.common.NCLocator;
import nc.itf.hrp.pub.HRPPubTool;
import nc.itf.uap.IUAPQueryBS;
import nc.jdbc.framework.processor.VectorProcessor;
import nc.ui.bd.ref.RefUIConfig;
import nc.ui.bd.ref.UFRefGridTreeUI;
import nc.ui.hrp.pub.bill.HRPManagerSingleHeadUI;
import nc.ui.hrp.pub.bill.HRPManagerUI;
import nc.ui.pub.ClientEnvironment;
import nc.ui.pub.FramePanel;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.bill.BillEditEvent;
import nc.ui.pub.bill.BillEditListener;
import nc.ui.pub.bill.BillModel;
import nc.ui.tam.tongren001.KqPsndocRefModel;
import nc.ui.tam.tongren002.TambblbRefTreeModel;
import nc.ui.trade.base.IBillOperate;
import nc.ui.trade.bill.AbstractManageController;
import nc.ui.trade.business.HYPubBO_Client;
import nc.ui.trade.button.IBillButton;
import nc.ui.trade.manage.ManageEventHandler;
import nc.uif.pub.exception.UifException;
import nc.vo.bd.b06.PsndocVO;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.tam.tongren.power.UserDeptVO;
import nc.vo.tam.tongren003.PaibanWeekVO;
import nc.vo.tam.tongren006.ZhibanTempVO;
import nc.vo.trade.pub.HYBillVO;

public class ClientUI extends HRPManagerSingleHeadUI implements BillEditListener{
	ArrayList<String> list_dept = new ArrayList<String>();
	public ClientUI() {
		super();
		getButtonManager().getButton(IBillButton.Edit).setName("维护");
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
		for(int i=1;i<51;i++){
			if(getBillListPanel().getHeadBillModel().getItemByKey("vbbname"+i+"")!=null){
				UIRefPane pane = (UIRefPane)getBillListPanel().getHeadBillModel().getItemByKey("vbbname"+i+"").getComponent();
				pane.setRefModel(new KqPsndocRefModel());
				pane.setMultiSelectedEnabled(true);
				pane.setAutoCheck(false);
				pane.setReturnCode(true);
				pane.setTreeGridNodeMultiSelected(true);
				getBillListPanel().getHeadBillModel().getItemByKey("vbbname"+i+"").setIDColName("pk_bb"+(i)+"");
			}
		}	
		UIRefPane pane = (UIRefPane)getBillListPanel().getHeadBillModel().getItemByKey("deptnamekq").getComponent();
		pane.setMultiSelectedEnabled(true);
		pane.setAutoCheck(false);
		pane.setReturnCode(false);

		UIRefPane pane1 = (UIRefPane)getBillListPanel().getHeadBillModel().getItemByKey("bbname").getComponent();
		String sql = " tbm_bclb.pk_corp='" + _getCorp().getPrimaryKey()+"'  and isnull(tbm_bclb.dr,0)=0 and " +
		" isnull(tbm_bclb.iscancel,'N')='N' and tbm_bclb.lbbm<>'DEFAULT' and tbm_bclb.pk_bbz  in ('00018L1000000010I5FM','00018L1000000010I5FS','00018L1000000010J12W','00018L1000000010J12V','00018L1000000010J12X')  ";
		if(list_pk!=null&&list_pk.size()>0){
			sql += ""+HRPPubTool.formInSQL("tbm_bclb.pk_bclbid", list_pk)+"" ;
		}else{
			sql+= " and 1=2 ";
		}
		pane1.setWhereString(sql);
	}
	protected void initEventListener() {
		super.initEventListener();
		getBillListPanel().getParentListPanel().addEditListener2(this);
	}
	public String getModuleCode() {
		// TODO Auto-generated method stub
		return "6017010160";
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
				UIRefPane pane = (UIRefPane)getBillListPanel().getHeadBillModel().getItemByKey(e.getKey()).getComponent();
				String[] pks = pane.getRefPKs();
				String[] names = pane.getRefNames();
				if(pks==null||pks.length<=0){
					if(e.getValue().toString().trim().length()<=0){
						getBillListPanel().getHeadBillModel().setValueAt(null, e.getRow(), e.getKey());
						getBillListPanel().getHeadBillModel().setValueAt(null, e.getRow(), "pk_bb"+x+"");
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
					getBillListPanel().getHeadBillModel().setValueAt(null, e.getRow(), e.getKey());
					getBillListPanel().getHeadBillModel().setValueAt(null, e.getRow(), "pk_bb"+x+"");
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
				getBillListPanel().getHeadBillModel().setValueAt(vbbname, e.getRow(), e.getKey());
				getBillListPanel().getHeadBillModel().setValueAt(pk_bb, e.getRow(), "pk_bb"+x+"");
			}else{
				getBillListPanel().getHeadBillModel().setValueAt(null, e.getRow(), e.getKey());
				getBillListPanel().getHeadBillModel().setValueAt(null, e.getRow(), "pk_bb"+x+"");
			}
		}else if(e.getKey().startsWith("deptnamekq")){
			if(e.getValue()!=null){
				UIRefPane pane = (UIRefPane)getBillListPanel().getHeadBillModel().getItemByKey(e.getKey()).getComponent();
				String[] pks = pane.getRefPKs();
				String[] names = pane.getRefNames();
				if(pks==null){
					getBillListPanel().getHeadBillModel().setValueAt(null, e.getRow(), e.getKey());
					getBillListPanel().getHeadBillModel().setValueAt(null, e.getRow(), "pk_dept");
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
				getBillListPanel().getHeadBillModel().setValueAt(vbbname, e.getRow(), e.getKey());
				getBillListPanel().getHeadBillModel().setValueAt(pk_bb, e.getRow(), "pk_dept");
			}else{
				getBillListPanel().getHeadBillModel().setValueAt(null, e.getRow(), e.getKey());
				getBillListPanel().getHeadBillModel().setValueAt(null, e.getRow(), "pk_dept");
			}
		}
				if(getBillListPanel().getHeadBillModel().getValueAt(e.getRow(), "pk_paiban")!=null){
					getBillListPanel().getHeadBillModel().getRowAttribute(e.getRow()).setRowState(BillModel.MODIFICATION);
				}
	}
	
	
	@Override
	public boolean beforeEdit(BillEditEvent e) {
		// TODO Auto-generated method stub
		// 查询当前模板在值班排班中是否已经排班,已排班不能修改模板时间
		if(e.getKey().equals("dbegindate")){
			try {
				String pk_temp = (String) getBillListPanel().getHeadBillModel().getValueAt(e.getRow(), "pk_paiban");
				if(pk_temp != null){
					Integer count = getPaibanCount(pk_temp);
					
					if(count > 0){
						getBillListPanel().getHeadBillModel().setCellEditable(e.getRow(), "dbegindate", false);
					}
				}
				
			} catch (BusinessException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		
		return super.beforeEdit(e);
	}
	public void initData(){
		getBufferData().clear();
		initUIData();
	}
	@Override
	protected void initUIData() {
		// TODO Auto-generated method stub
		try {
			list_dept = new ArrayList<String>();
			ZhibanTempVO[] vos = (ZhibanTempVO[])HYPubBO_Client.queryByCondition(ZhibanTempVO.class, 
					" isnull(dr,0)=0 and pk_corp='"+_getCorp().getPrimaryKey()+"' ");
			UserDeptVO[] deptvos = (UserDeptVO[])HYPubBO_Client.queryByCondition(UserDeptVO.class, " isnull(dr,0)=0 and powertype=0 and pk_user='"+ClientEnvironment.getInstance().getUser().getPrimaryKey()+"' ");
			
			if(deptvos!=null&&deptvos.length>0){
				for(UserDeptVO deptvo:deptvos){
					list_dept.add(deptvo.getPk_docid());
				}
				ArrayList<ZhibanTempVO> values = new ArrayList<ZhibanTempVO>();
				if(vos!=null&&vos.length>0){
					for(ZhibanTempVO vo:vos){
						String[] pks = vo.getPk_dept().split(",");
						for(String pk:pks){
							if(list_dept.contains(pk)){
								values.add(vo);
								break;
							}
						}
					}
					vos = values!=null&&values.size()>0?values.toArray(new ZhibanTempVO[0]):null;
				}
			}else{
				vos = null;
			}
			if(vos!=null&&vos.length>0){
				ArrayList<AggregatedValueObject> listVOs = new ArrayList<AggregatedValueObject>();
				if(vos!=null){
					if(vos.length>0){
						for(CircularlyAccessibleValueObject vo : vos ){
							AggregatedValueObject billVO = null;
							if(vo != null) {
								billVO = (AggregatedValueObject) Class.forName(getUIControl().getBillVoName()[0]).newInstance();
								billVO.setParentVO((CircularlyAccessibleValueObject) vo);
							}
							listVOs.add(billVO);
						}
					}
					getBufferData().addVOsToBuffer(listVOs.toArray(new HYBillVO[0]));
					getBufferData().refresh();
					if (getBufferData().getVOBufferSize() != 0) {
						setListHeadData(getBufferData().getAllHeadVOsFromBuffer());
						setBillOperate(IBillOperate.OP_NOTEDIT);
						getBufferData().setCurrentRow(0);
					} else {
						setListHeadData(null);
						setBillOperate(IBillOperate.OP_INIT);
						getBufferData().setCurrentRow(-1);
						showHintMessage(nc.ui.ml.NCLangRes.getInstance().getStrByID("uifactory",
						"UPPuifactory-000066")/* @res "没有查到任何满足条件的数据!" */);
					}
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Override
	protected AbstractManageController createController() {
		// TODO 自动生成方法存根
		return new ClientCtrl();
	}
	protected ManageEventHandler createEventHandler() {
		return new ClientEH(this, getUIControl());
	}

	@Override
	protected void initSelfData() {
		// TODO Auto-generated method stub
		super.initSelfData();
		getBillCardPanel().setAutoExecHeadEditFormula(true);
	}

	public Integer getPaibanCount(String pk_temp) throws BusinessException{
		IUAPQueryBS service = (IUAPQueryBS)NCLocator.getInstance().lookup(IUAPQueryBS.class.getName());
		String strSQL = "select count(*) counts from trtam_paiban_b where nvl(dr, 0) = 0  and biszb = 'Y' and pk_temp='"+pk_temp+"'";
		Integer counts = 0;
		
		Vector o1 = (Vector) service.executeQuery(strSQL,new VectorProcessor());
		if (o1.size() > 0 && o1 != null) {
			for (int i = 0; i < o1.size(); i++) {
				counts = new Integer(((Vector) o1.elementAt(i)).elementAt(0) != null ? ((Vector) o1.elementAt(i)).elementAt(0).toString() : ""); 
			}
		}
		return counts;
	}
}
