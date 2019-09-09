package nc.ui.tam.tongren011;

import java.util.ArrayList;
import java.util.HashMap;

import nc.bs.framework.common.NCLocator;
import nc.itf.hr.ta.IBclbDefining;
import nc.itf.hrp.pub.HRPPubTool;
import nc.ui.bd.ref.RefUIConfig;
import nc.ui.bd.ref.UFRefGridTreeUI;
import nc.ui.hrp.pub.bill.HRPManagerSingleHeadUI;
import nc.ui.hrp.pub.bill.HRPManagerUI;
import nc.ui.pub.ButtonObject;
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
import nc.vo.pub.lang.UFDouble;
import nc.vo.sm.user.UserAndClerkVO;
import nc.vo.tam.tongren.power.UserDeptVO;
import nc.vo.tam.tongren001.DeptKqBVO;
import nc.vo.tam.tongren003.PanbanWeekBVO;
import nc.vo.tam.tongren006.ZhibanTempVO;
import nc.vo.tam.tongren011.AdjustTamVO;
import nc.vo.tbm.tbm_029.BclbHeaderVO;
import nc.vo.trade.pub.HYBillVO;

public class ClientUI extends HRPManagerSingleHeadUI implements BillEditListener{
	public ClientUI() {
		super();
		if(getBillListPanel().getHeadBillModel().getItemByKey("bbname_new")!=null){
			UIRefPane pane = (UIRefPane)getBillListPanel().getHeadBillModel().getItemByKey("bbname_new").getComponent();
			TambblbRefTreeModel model = new TambblbRefTreeModel("班别档案");
			pane.setRefModel(model);
			pane.setMultiSelectedEnabled(true);
			pane.setAutoCheck(false);
			pane.setReturnCode(true);
			pane.setTreeGridNodeMultiSelected(true);
			getBillListPanel().getHeadBillModel().getItemByKey("bbname_new").setIDColName("pk_bb_new");
		}
		
		
		
//		getButtonManager().getButton(IBillButton.CancelAudit).setName("取消提交");
//		initData();
		
	}
	protected void initEventListener() {
		super.initEventListener();
		getBillListPanel().getParentListPanel().addEditListener2(this);
	}
	@Override
	public boolean beforeEdit(BillEditEvent e) {
		// TODO Auto-generated method stub
		if((getBillListPanel().getHeadBillModel().getValueAt(e.getRow(), "istate")!=null
				&&getBillListPanel().getHeadBillModel().getValueAt(e.getRow(), "istate").toString().equals("新增"))
				){
			getBillListPanel().getHeadBillModel().setCellEditable(e.getRow(), "bbname_new",true);
			getBillListPanel().getHeadBillModel().setCellEditable(e.getRow(), "ddate",true);
			getBillListPanel().getHeadBillModel().setCellEditable(e.getRow(), "vmemo",true);
			getBillListPanel().getHeadBillModel().setCellEditable(e.getRow(), "psncode",true);
		}else{
			getBillListPanel().getHeadBillModel().setCellEditable(e.getRow(), "bbname_new",false);
			getBillListPanel().getHeadBillModel().setCellEditable(e.getRow(), "ddate",false);
			getBillListPanel().getHeadBillModel().setCellEditable(e.getRow(), "vmemo",false);
			getBillListPanel().getHeadBillModel().setCellEditable(e.getRow(), "psncode",false);
		}
		if(e.getKey().equals("bbname_new")){
			UIRefPane pane = (UIRefPane)getBillListPanel().getHeadBillModel().getItemByKey(e.getKey()).getComponent();
			String pk_dept = (String)getBillListPanel().getHeadBillModel().getValueAt(e.getRow(), "pk_dept");
			
			String value = (String)getBillListPanel().getHeadBillModel().getValueAt(e.getRow(), "pk_bb_new");
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
			" isnull(tbm_bclb.iscancel,'N')='N' and tbm_bclb.lbbm<>'DEFAULT'  ";
			if(list_pk!=null&&list_pk.size()>0){
				sql += ""+HRPPubTool.formInSQL("tbm_bclb.pk_bclbid", list_pk)+"" ;
			}else{
				sql+= " and 1=2 ";
			}
			pane.setWhereString(sql);
		}
		
		if(e.getKey().equals("psncode")){
			UIRefPane pane = (UIRefPane)getBillListPanel().getHeadBillModel().getItemByKey(e.getKey()).getComponent();
			pane.setWhereString(" denddate is null  and trtam_deptdoc_kq_b.dr=0");// zhanghua增加dr标识
		}
		
		return super.beforeEdit(e);
	}
	public String getModuleCode() {
		// TODO Auto-generated method stub
		return "6017010485";
	}
	
	
	@Override
	public void bodyRowChange(BillEditEvent arg0) {
		// TODO Auto-generated method stub
		super.bodyRowChange(arg0);
		AdjustTamVO headvo = (AdjustTamVO) getBillListPanel().getHeadBillModel().getBodyValueRowVO(arg0.getRow(), "nc.vo.tam.tongren011.AdjustTamVO");
		
		if(headvo.getIstate() == 0){
			getButtonManager().getButton(IBillButton.Edit).setEnabled(true);
			getButtonManager().getButton(IBillButton.Commit).setEnabled(true);
			getButtonManager().getButton(IBillButton.Delete).setEnabled(true);
		
		}else if(headvo.getIstate() == 1){
			
			getButtonManager().getButton(IBillButton.Edit).setEnabled(false);
			getButtonManager().getButton(IBillButton.Commit).setEnabled(false);
			getButtonManager().getButton(IBillButton.Delete).setEnabled(false);
		}else{
			getButtonManager().getButton(IBillButton.Edit).setEnabled(false);
			getButtonManager().getButton(IBillButton.Commit).setEnabled(false);
			getButtonManager().getButton(IBillButton.Delete).setEnabled(false);
			
		}
		
	}
	@Override
	public void afterEdit(BillEditEvent e) {
		if(e.getKey().equals("ddate")||e.getKey().equals("psncode2")||e.getKey().equals("psncode")){
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
			if(e.getValue()!=null){
				try {
					String pk_psn = (String)getBillListPanel().getHeadBillModel().getValueAt(e.getRow(), "pk_psn");
					String pk_psn2 = (String)getBillListPanel().getHeadBillModel().getValueAt(e.getRow(), "pk_psn2");
					Object ddate = getBillListPanel().getHeadBillModel().getValueAt(e.getRow(), "ddate");
					if(ddate!=null&&ddate.toString().trim().length()>0){
						if(pk_psn!=null&&pk_psn.trim().length()>0){
							DeptKqBVO[] deptvos = (DeptKqBVO[])HYPubBO_Client.queryByCondition(DeptKqBVO.class,
									" isnull(dr,0)=0 and pk_psndoc='"+pk_psn+"'  and (denddate is null or denddate>='"+ddate+"') and dstartdate<='"+ddate.toString()+"' ");
							PanbanWeekBVO[] vos = (PanbanWeekBVO[])HYPubBO_Client.queryByCondition(PanbanWeekBVO.class, 
									" ddate='"+ddate.toString()+"' and pk_psndoc='"+pk_psn+"' ");
							if(vos!=null&&vos.length>0){
								String names = "";
								String pks = "";
								
								
								for(PanbanWeekBVO vo:vos){
									pks+=""+vo.getPk_bb()+",";
									names+= ""+map_bb.get(vo.getPk_bb()).getLbmc()+",";
									
									
								}
								pks = pks.substring(0, pks.length()-1);
								names = names.substring(0, names.length()-1);
								
								
								getBillListPanel().getHeadBillModel().setValueAt(names, e.getRow(), "bbname_old");
								getBillListPanel().getHeadBillModel().setValueAt(pks, e.getRow(), "pk_bb_old");
								if(deptvos!=null&&deptvos.length>0){
									getBillListPanel().getHeadBillModel().setValueAt(deptvos[0].getPk_dept(), e.getRow(),"pk_dept");
								}
							}else{
								getBillListPanel().getHeadBillModel().setValueAt(null, e.getRow(), "bbname_old");
								getBillListPanel().getHeadBillModel().setValueAt(null, e.getRow(), "pk_bb_old");
								if(deptvos!=null&&deptvos.length>0){
									getBillListPanel().getHeadBillModel().setValueAt(deptvos[0].getPk_dept(), e.getRow(),"pk_dept");
								}
							}
						}

						if(pk_psn2!=null&&pk_psn2.trim().length()>0){
							DeptKqBVO[] deptvos = (DeptKqBVO[])HYPubBO_Client.queryByCondition(DeptKqBVO.class,
									" isnull(dr,0)=0 and pk_psndoc='"+pk_psn2+"'  and (denddate is null or denddate>='"+ddate+"') and dstartdate<='"+ddate+"' ");
							PanbanWeekBVO[] vos = (PanbanWeekBVO[])HYPubBO_Client.queryByCondition(PanbanWeekBVO.class, 
									" ddate='"+ddate.toString()+"' and pk_psndoc='"+pk_psn2+"' ");
							if(vos!=null&&vos.length>0){
								String names = "";
								String pks = "";
								for(PanbanWeekBVO vo:vos){
									pks+=""+vo.getPk_bb()+",";
									names+= ""+map_bb.get(vo.getPk_bb()).getLbmc()+",";
								}
								pks = pks.substring(0, pks.length()-1);
								names = names.substring(0, names.length()-1);
								getBillListPanel().getHeadBillModel().setValueAt(names, e.getRow(), "bbnameold2");
								getBillListPanel().getHeadBillModel().setValueAt(pks, e.getRow(), "pk_bb_old2");
								if(deptvos!=null&&deptvos.length>0){
									getBillListPanel().getHeadBillModel().setValueAt(deptvos[0].getPk_dept(), e.getRow(),"pk_dept2");
								}
							}else{
								getBillListPanel().getHeadBillModel().setValueAt(null, e.getRow(), "bbnameold2");
								getBillListPanel().getHeadBillModel().setValueAt(null, e.getRow(), "pk_bb_old2");
								if(deptvos!=null&&deptvos.length>0){
									getBillListPanel().getHeadBillModel().setValueAt(deptvos[0].getPk_dept(), e.getRow(),"pk_dept2");
								}
							}
						}
					}


					getBillListPanel().getHeadBillModel().execLoadFormula();
				} catch (UifException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}else if(e.getKey().startsWith("bbname_new")){
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
				if(e.getValue()!=null){
					UIRefPane pane = (UIRefPane)getBillListPanel().getHeadBillModel().getItemByKey(e.getKey()).getComponent();
					String[] pks = pane.getRefPKs();
					String[] names = pane.getRefNames();
					if(pks==null||pks.length<=0){
						if(e.getValue().toString().trim().length()<=0){
							getBillListPanel().getHeadBillModel().setValueAt(null, e.getRow(), e.getKey());
							getBillListPanel().getHeadBillModel().setValueAt(null, e.getRow(), "pk_bb_new");
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
						getBillListPanel().getHeadBillModel().setValueAt(null, e.getRow(), e.getKey());
						getBillListPanel().getHeadBillModel().setValueAt(null, e.getRow(), "pk_bb_new");
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
					
					getBillListPanel().getHeadBillModel().setValueAt("", e.getRow(), "pk_temp");
//					// 带值班模板
					String iszb = "N",pk_bclbid="";
					for(String pk:pks){
						BclbHeaderVO bcvo = map_bb.get(pk);
						if(bcvo!=null){
							if(bcvo.getLbmc().indexOf("值班") != -1){
								iszb = "Y";
								pk_bclbid = bcvo.getPk_bclbid();
							}
						}
					}
					
					if(iszb.equals("Y")){
						
						getBillListPanel().getHeadBillModel().setCellEditable(e.getRow(), "deftemp",true);
						
						UIRefPane refpane = (UIRefPane)getBillListPanel().getHeadBillModel().getItemByKey("deftemp").getComponent();
						refpane.setWhereString(" pk_bclbid='"+pk_bclbid+"' and isnull(dr,0)=0 and isnull(bisstop,'N')='N'");
					}else{
						getBillListPanel().getHeadBillModel().setCellEditable(e.getRow(), "deftemp",false);
					}
					
					vbbname = vbbname.substring(0, vbbname.length()-1);
					getBillListPanel().getHeadBillModel().setValueAt(vbbname, e.getRow(), e.getKey());
					getBillListPanel().getHeadBillModel().setValueAt(pk_bb, e.getRow(), "pk_bb_new");
				}else{
					getBillListPanel().getHeadBillModel().setValueAt(null, e.getRow(), e.getKey());
					getBillListPanel().getHeadBillModel().setValueAt(null, e.getRow(), "pk_bb_new");
				}
			}
				if(getBillListPanel().getHeadBillModel().getValueAt(e.getRow(), "pk_apply")!=null){
					getBillListPanel().getHeadBillModel().getRowAttribute(e.getRow()).setRowState(BillModel.MODIFICATION);
				}
	}
	public void initData(){
		getBufferData().clear();
		initUIData();
	}
	@Override
	protected void initUIData() {
		// TODO Auto-generated method stub
		try {
			AdjustTamVO[] vos = (AdjustTamVO[])HYPubBO_Client.queryByCondition(AdjustTamVO.class, 
					" isnull(dr,0)=0 and pk_corp='"+_getCorp().getPrimaryKey()+"'  and vtype=1 and pk_dept in (select pk_docid from bd_tr_userpower where isnull(dr,0)=0 and pk_user='"+_getOperator()+"' and powertype=0 and  pk_corp='"+_getCorp().getPrimaryKey()+"')  and istate=0 ");
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
						getButtonManager().getButton(IBillButton.Edit).setEnabled(true);
						updateButtons();
					} else {
						setListHeadData(null);
						setBillOperate(IBillOperate.OP_INIT);
						getBufferData().setCurrentRow(-1);
						showHintMessage(nc.ui.ml.NCLangRes.getInstance().getStrByID("uifactory",
						"UPPuifactory-000066")/* @res "没有查到任何满足条件的数据!" */);
						getButtonManager().getButton(IBillButton.Edit).setEnabled(false);
						updateButtons();
					}
				}
			}else{
				getButtonManager().getButton(IBillButton.Edit).setEnabled(false);
				updateButtons();
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
	@Override
	public void updateButton(ButtonObject arg0) {
		// TODO Auto-generated method stub
		super.updateButton(arg0);
	}
	@Override
	public void updateButtons() {
		// TODO Auto-generated method stub
		super.updateButtons();
		
		
	}

}
