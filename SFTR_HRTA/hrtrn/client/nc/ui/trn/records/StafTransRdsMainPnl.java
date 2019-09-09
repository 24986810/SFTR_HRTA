package nc.ui.trn.records;

import java.util.Observable;

import javax.swing.JTable;
import javax.swing.tree.DefaultMutableTreeNode;

import nc.bs.logging.Logger;
import nc.hr.utils.PubEnv;
import nc.hr.utils.ResHelper;
import nc.itf.hr.pub.PubDelegator;
import nc.ui.hr.frame.FrameUI;
import nc.ui.hr.frame.LeftSubject;
import nc.ui.hr.frame.impl.MainBillListPanel;
import nc.ui.hr.frame.state.StateChangeEvent;
import nc.ui.hr.frame.state.StateRegister;
import nc.ui.hr.frame.util.BillPanelUtils;
import nc.ui.hr.global.Global;
import nc.ui.hr.utils.Util;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.beans.constenum.DefaultConstEnum;
import nc.ui.pub.bill.BillCellEditor;
import nc.ui.pub.bill.BillEditEvent;
import nc.ui.pub.bill.BillItem;
import nc.ui.pub.bill.BillModel;
import nc.ui.pub.bill.BillScrollPane;
import nc.ui.pub.bill.IBillItem;
import nc.ui.smtm.pub.CommonValue;
import nc.ui.trade.business.HYPubBO_Client;
import nc.ui.trn.records.action.QueryAction;
import nc.uif.pub.exception.UifException;
import nc.vo.hr.tools.pub.HRAggVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.NullFieldException;
import nc.vo.pub.SuperVO;
import nc.vo.pub.ValidationException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.trn.records.PsndocVO;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

@SuppressWarnings("serial")
public class StafTransRdsMainPnl extends MainBillListPanel {

	//�������ӻ����޸ĵ�ʱ�򻺴����ݣ������ڵ��ȡ����ʱ���ָ���ǰ������
	private HRAggVO hrAggCacheVOs[] = null;
	
	private Boolean RECORDUSEDEPTPOWER=null;

	public StafTransRdsMainPnl(FrameUI frameUI, String strBillType2,
			String strBusiType2) {
		super(frameUI, strBillType2, strBusiType2);
		init();
		initCardPart();
	}

	private void init() {
		setMainPanelEnabled(false);
		initListener();
		changeBodySelectMode();
		initComboBox(IBillItem.BODY, CommonValue.HI_PSNDOC_PART, "jobtype", CommonValue.JOBTYPE_FOR_PART, false);
	}
	
	protected void initListener() {
		getBillListPanel().getBodyTabbedPane().addChangeListener(
				new javax.swing.event.ChangeListener() {
					public void stateChanged(javax.swing.event.ChangeEvent e) {
						//�л�ҳǩʱ���ж��ӱ��Ƿ�ѡ���У��л���ť״̬	
						int currstate = getDataMdl().getCurrentState();
						if(!ArrayUtils.contains(StafTransRdsDataModel.unfixedStates,currstate))
							return;
						int headSelectedRow = getHeadSelectedRow();
						//����ߵĲ���û����Ա����ʱ��Ҫ����ұ��ӱ������ fengwei 2010-11-12
						if(headSelectedRow < 0){
							clearBodyData();
							((StafTransRdsUI)getParentUI()).setCurrentState(
									getDataMdl().isQueryPsn() ? StafTransRdsStateReg.PSN_SUBLIST_SELECTED
											: StafTransRdsStateReg.RDS_SUBLIST_SELECTED);
						}
						int row = getBodySelectedRow();
						if(row < 0){
							((StafTransRdsUI)getParentUI()).setCurrentState(
									getDataMdl().isQueryPsn() ? StafTransRdsStateReg.PSN_PSNLIST_SELECTED
											: StafTransRdsStateReg.RDS_PSNLIST_SELECTED);
						}else{
							((StafTransRdsUI)getParentUI()).setCurrentState(
									getDataMdl().isQueryPsn() ? StafTransRdsStateReg.PSN_SUBLIST_SELECTED
											: StafTransRdsStateReg.RDS_SUBLIST_SELECTED);
						}								
					}
				});
	}

	
	private Boolean getneedDeptPower(){
		if (RECORDUSEDEPTPOWER!=null) return RECORDUSEDEPTPOWER;
		try {
			int intvalue = PubDelegator.getIParValue().getParaInt(Global.getCorpPK(), "RECORDUSEDEPTPOWER").intValue();
			RECORDUSEDEPTPOWER = (intvalue==1);
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return RECORDUSEDEPTPOWER;
	}
	private void initHeadRef() {
		UIRefPane deptref = ListRefUtils.getRefPaneByKey("pk_deptdoc", getBillListPanel().getHeadBillModel());
		if(deptref!=null){
			deptref.getRefModel().setUseDataPower(false);
		}
	}

	private void initCardPsnChg() {
		BillModel billModel = getBodyBillModel(CommonValue.HI_PSNDOC_PSNCHG);
//		���ò���ĩ������
		ListRefUtils.setNotLeafSelectedEnabled(billModel);
		
	}

	private void initCardRetire() {
		BillModel billModel = getBodyBillModel(CommonValue.HI_PSNDOC_RETIRE);
//		���ò���ĩ������
		ListRefUtils.setNotLeafSelectedEnabled(billModel);
	}

	private void initCardDimission() {
		BillModel billModel = getBodyBillModel(CommonValue.HI_PSNDOC_DIMISSION);
		if (billModel == null) {
			return;
		}
		UIRefPane refpane = null;
		// ��ְ����Ա���
		refpane = ListRefUtils.getRefPaneByKey("psnclafter", billModel);
		if (refpane != null)
			ListRefUtils.resetPsnclRef(refpane, CommonValue.HI_PSNDOC_DIMISSION, Global.getCorpPK());
		// ��ְ����
		if (refpane != null)
			refpane = ListRefUtils.getRefPaneByKey("pkdeptafter", billModel);
		ListRefUtils.resetDeptRef(refpane, CommonValue.HI_PSNDOC_DIMISSION, Global.getCorpPK());
		// ���ò���ĩ������
		ListRefUtils.setNotLeafSelectedEnabled(billModel);
	}

	private void initCardPart() {
		BillModel billModel = getBodyBillModel(CommonValue.HI_PSNDOC_PART);

		UIRefPane refpane = null;
		// ����
		refpane = ListRefUtils.getRefPaneByKey("pk_deptdoc", billModel);
		if (refpane != null)
			ListRefUtils.resetDeptRef(refpane, CommonValue.HI_PSNDOC_PART, Global.getCorpPK());
		// ��Ա���
		refpane = ListRefUtils.getRefPaneByKey("pk_psncl", billModel);
		if (refpane != null)
			ListRefUtils.resetPsnclRef(refpane, CommonValue.HI_PSNDOC_PART, Global.getCorpPK());
		// ְλ
		refpane = ListRefUtils.getRefPaneByKey("pk_om_duty", billModel);
		if (refpane != null)
			ListRefUtils.resetDutyRef(refpane, CommonValue.HI_PSNDOC_PART, Global.getCorpPK(), null);
		// ���ò���ĩ������
		ListRefUtils.setNotLeafSelectedEnabled(billModel);
		
		initCardDimission();
	}

	private void initCardDeptChg() {
		BillModel deptchgModel = getBodyBillModel(CommonValue.HI_PSNDOC_DEPTCHG);
		UIRefPane refpane = null;
		//����
		refpane = ListRefUtils.getRefPaneByKey("pk_deptdoc", deptchgModel);
		ListRefUtils.resetDeptRef(refpane, CommonValue.HI_PSNDOC_DEPTCHG, Global.getCorpPK());
		//��Ա���
		refpane = ListRefUtils.getRefPaneByKey("pk_psncl", deptchgModel);
		ListRefUtils.resetPsnclRef(refpane, CommonValue.HI_PSNDOC_DEPTCHG, Global.getCorpPK());
		//��λ
		refpane = ListRefUtils.getRefPaneByKey("pk_postdoc", deptchgModel);
		ListRefUtils.resetPostRef(refpane, CommonValue.HI_PSNDOC_DEPTCHG, Global.getCorpPK(),null);
		//ְλ
		refpane = ListRefUtils.getRefPaneByKey("pk_om_duty", deptchgModel);
		ListRefUtils.resetDutyRef(refpane, CommonValue.HI_PSNDOC_DEPTCHG, Global.getCorpPK(),null);
		//���ò���ĩ������
		ListRefUtils.setNotLeafSelectedEnabled(deptchgModel);
	}
	
	private void changeBodySelectMode() {
		JTable table = null;
		for (String s : getDataModel().getBodyTableCodeArray()) {
			if(getBillListPanel().getBodyScrollPane(s)==null){
				continue;
			}
			getBillListPanel().getBodyScrollPane(s).removeTableSortListener();
			table = getBillListPanel().getBodyScrollPane(s).getTable();
			if (table!=null) {
				table.setCellSelectionEnabled(true);
			}			
		}
	}

	@Override
	public void update(Observable o, Object arg) {
		if (o instanceof RdsTopSubject) {
			// ��Ա���仯������ֵ
			clearHeadData();
			clearBodyData();
			try {
				getDataMdl().setPsnclscope(((RdsTopSubject)o).getPsnclscorp());
				new QueryAction(getParentUI(),true).execute();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (o instanceof LeftSubject) {
			// �����仯������ֵ
			onLeftTreeSelected((LeftSubject) o, arg);
		}
	}

	@Override
	protected void onLeftTreeSelected(LeftSubject leftSubject, Object obj) {
		setData(getDataMdl().getData());
		
		if(getDataMdl().getData()!=null&&getDataMdl().getData().length>0){
			setHeadSelectedRow(0, 0);
		}else{
			DefaultMutableTreeNode node = getDataMdl().getSelectedNode();			
			if (node.isRoot())
	        {
	            ((StafTransRdsUI)getParentUI()).setCurrentState(!getDataMdl().isQueryPsn()?StafTransRdsStateReg.RDS_ROOT_SELECTED
	            		:StateRegister.STATE_ROOT_SELECTED);
	        }else if (node.isLeaf())
	        {
	        	((StafTransRdsUI)getParentUI()).setCurrentState(!getDataMdl().isQueryPsn()?StafTransRdsStateReg.RDS_SUBNODE_SELECTED:
	            	StafTransRdsStateReg.OMG_SUBNODE_SELECTED);
	        }			
		}
	}

	private StafTransRdsDataModel getDataMdl() {
		return (StafTransRdsDataModel) getParentUI().getDataModel();
	}

	public boolean beforeEdit(BillEditEvent e) {
		if (e.getRow() < 0 || e.getKey() == null)
			return false;
		String strTabCode = getCurrentBodyPageCode();
		BillModel billmodel = getBodyBillModel(strTabCode);
		if (e.getRow() != billmodel.getEditRow()) {
			return false;
		}
		//������༭ְ���
		if (e.getKey().startsWith("pk_detytype"))
			return false;
		//��������ְ��¼������༭�Ƿ��ڸ�
		if(e.getKey().equals("poststat")&&CommonValue.HI_PSNDOC_DEPTCHG.equals(getCurrentBodyPageCode())){
			UFBoolean lastflag = new UFBoolean(billmodel.getValueAt(e.getRow(), "lastflag").toString());
		 	int rowstate = billmodel.getRowState(e.getRow());
		 	if(rowstate != BillModel.ADD && !lastflag.booleanValue()){
		 		return false;
		 	}
		}

		if (e.getKey().startsWith("pk_deptdoc") && CommonValue.HI_PSNDOC_DEPTCHG.equals(getCurrentBodyPageCode())) {
			if (!getneedDeptPower())
				return true;
			BillModel billModel = getBodyBillModel(CommonValue.HI_PSNDOC_DEPTCHG);

			UIRefPane refpane = ListRefUtils.getRefPaneByKey("pk_deptdoc", billModel);

			if (refpane != null) {
				refpane.getRefModel().setUseDataPower(true);
				refpane.getRefModel().reloadData();
			}
		}
		
		if (e.getKey().startsWith("pkdept") && CommonValue.HI_PSNDOC_DIMISSION.equals(getCurrentBodyPageCode())) {
			if (!getneedDeptPower())
				return true;
			BillModel billModel = getBodyBillModel(CommonValue.HI_PSNDOC_DIMISSION);

			UIRefPane refpane = ListRefUtils.getRefPaneByKey("pkdeptafter", billModel);

			if (refpane != null) {
				refpane.getRefModel().setUseDataPower(true);
				refpane.getRefModel().reloadData();
			}
		}
		//add by lq begin -----
		beforeEditPsnType(e);
		//add by lq end  --------
		return true;
	}

	public void afterEdit(BillEditEvent event) {
		String tblName = getCurrentBodyPageCode();
		if (CommonValue.HI_PSNDOC_DEPTCHG.equalsIgnoreCase(tblName)) { //��ְ��Ϣ
			afterEditDeptchgRef(event);
		} else if (CommonValue.HI_PSNDOC_PART.equalsIgnoreCase(tblName)) { //��ְ��Ϣ
			afterEditPartRef(event);
		} else if (CommonValue.HI_PSNDOC_DIMISSION.equalsIgnoreCase(tblName)) {//��Ա��ְ��Ϣ
			afterEditDimissionRef(event);
			//add by lq begin ��ְҵ�����ͱ༭֮�������Ա���-------------
			//afterEditPsnType(event);zhanghuaȥ��
			//add by lq end  ---------------------
		}
		ListRefUtils.setNotLeafSelectedEnabled(getBodyBillModel(tblName));
	}
	
	private void beforeEditPsnType(BillEditEvent e){
		String key = e.getKey();
		BillModel billModel = getBodyBillModel(CommonValue.HI_PSNDOC_DEPTCHG);
		
		 if(key.equals("type_showname")){
			 
			 //��Ƹ
			 String defcode ="003";
			 //��ְҵ������
			 String deflistcode="HR303";
			 if(getDataMdl().getPsnclscope()==nc.vo.hi.pub.CommonValue.PSNCLSCOPE_RETIRE
						&& getDataMdl().getSelectedPerson().getIsreturn().booleanValue()){
				 String pk_defdoc = qryDefPk(defcode, deflistcode);
					UIRefPane refPane = (UIRefPane)getBillListPanel().getBodyItem("type_showname").getComponent();
					if(pk_defdoc!=null){
						refPane.getRefModel().addWherePart(" and pk_defdoc='"+pk_defdoc+"'");
					}
			 }else{
				 String pk_defdoc = qryDefPk(defcode, deflistcode);
				 UIRefPane refPane = (UIRefPane)getBillListPanel().getBodyItem("type_showname").getComponent();
				 refPane.getRefModel().addWherePart(" and pk_defdoc!='"+pk_defdoc+"' and pk_defdoclist =(select pk_defdoclist from bd_defdoclist  where doclistcode='"+deflistcode+"') ");
			 }
		}	
	}

	private String qryDefPk(String defcode, String deflistcode) {
		String pk_defdoc =null;
			   try {
				  pk_defdoc = (String)HYPubBO_Client.findColValue("bd_defdoc", "pk_defdoc", " doccode='"+defcode+"' and pk_defdoclist=(select pk_defdoclist from bd_defdoclist  where doclistcode='"+deflistcode+"')");
			   
			   } catch (UifException ex) {
				   Logger.error("��ѯ��ְҵ�����ͳ���");
				   ex.printStackTrace();
			   }
		return pk_defdoc;
	}
	/**
	 * add by lq  ������Ա��� 
	 * 
	 * @param e
	 */
	private void afterEditPsnType(BillEditEvent e){
		
		String key = e.getKey();
		BillModel billModel = getBodyBillModel(CommonValue.HI_PSNDOC_DEPTCHG);
		 if(key.equals("type_showname")){
			 DefaultConstEnum value = (DefaultConstEnum) e.getValue(); 
			String doccode =null;
			if(value!=null){
			   try {
				 doccode = (String)HYPubBO_Client.findColValue("bd_defdoc", "doccode", " pk_defdoc='"+value.getValue()+"'");
			   } catch (UifException ex) {
				   Logger.error("��ѯ��ְҵ�����ͳ���");
				   ex.printStackTrace();
			   }
			}
			UIRefPane refPane = (UIRefPane)getBillListPanel().getBodyItem("psnclafter_showname").getComponent();
			if(doccode==null){
				refPane.getRefModel().addWherePart(" and psnclscope<>2 and psnclscope<>3 ");
			}
			else if("001".equals(doccode) 
					|| "003".equals(doccode)){
			    refPane.getRefModel().addWherePart(" and psnclscope<>2 ");
			}else if("2".equals(doccode)){
				
			}
			else{
				refPane.getRefModel().addWherePart(" and psnclscope<>3 ");
			}
			getBillListPanel().getBodyBillModel().setValueAt(null, e.getRow(), "psnclafter_showname");
			
			
		}
	}
	private void afterEditDimissionRef(BillEditEvent e){
		if(!(e.getSource() instanceof UIRefPane))
			return;
		BillModel billModel = getBodyBillModel(CommonValue.HI_PSNDOC_DEPTCHG);
		String key = e.getKey();
		String showname = BillPanelUtils.REF_SHOW_NAME;
		String contiainKeys[] = new String[]{"pkdeptbefore"+showname,
				"pkpostbefore"+showname};
		if(!ArrayUtils.contains(contiainKeys, key)){
			return;
		}
		int row = e.getRow();
		String value = (String) e.getValue();
		UIRefPane refpane = (UIRefPane)e.getSource();
		if (key.startsWith("pkdeptbefore")) {
			//�����ز���
			String[] keys = { "pkpostbefore" };
			for(String s:keys){
				ListRefUtils.clearRefValueByKey(s, row, billModel);
			}
			UIRefPane postref = ListRefUtils.getRefPaneByKey("pkpostbefore", billModel);
			ListRefUtils.resetPostRef(postref, getCurrentBodyPageCode(), Global.getCorpPK(), value);
			
		}else if (key.startsWith("pkpostbefore")) {//��λ			
			//�޸ĸ�λ������ְ�����
			UIRefPane dutyRefPane = ListRefUtils.getRefPaneByKey("pkomdutybefore", billModel);
			String jobseries =(String) refpane.getRefValue("om_job.jobseries");
			ListRefUtils.resetDutyRef(dutyRefPane, getCurrentBodyPageCode(), Global.getCorpPK(), jobseries);
			//��λ�ж�Ӧְ������ְ��ֵ��ְ����ղ�����
			//û�ж�Ӧְ��ְ����տ���
			String pk_om_duty = (String)refpane.getRefValue("om_job.pk_om_duty");
			if(!StringUtils.isBlank(pk_om_duty)){
				ListRefUtils.setRefItemValueByPK("pkomdutybefore", row, pk_om_duty, billModel);
				dutyRefPane.setEnabled(true);
			}else{
				ListRefUtils.clearRefValueByKey("pkomdutybefore", row, billModel);
				dutyRefPane.setEnabled(true);
			}
		}
		deafBodyRefNameByPK(CommonValue.HI_PSNDOC_DIMISSION);
	}
	private void afterEditDeptchgRef(BillEditEvent e){
		Object obj = e.getSource();		
		if(!(obj instanceof BillCellEditor) 
				|| !(((BillCellEditor)obj).getComponent() instanceof UIRefPane)
				|| (e.getValue()!=null && !(e.getValue() instanceof DefaultConstEnum)))
			return;
		BillModel billModel = getBodyBillModel(CommonValue.HI_PSNDOC_DEPTCHG);
		String key = e.getKey();
		String showname = BillPanelUtils.REF_SHOW_NAME;
		String contiainKeys[] = new String[]{"pk_deptdoc"+showname,
				"pk_jobserial"+showname,
				"pk_postdoc"+showname,
				"pk_om_duty"+showname};
		if(!ArrayUtils.contains(contiainKeys, key)){
			return;
		}
		int row = e.getRow();
		String value;
		if (e.getValue()!=null) {
			value = (String) ((DefaultConstEnum) e.getValue()).getValue();
		}else{
			value = null;
		}		
		UIRefPane refpane = ListRefUtils.getRefPaneByKey(key, billModel);
		//���ű仯
		if(key.startsWith("pk_deptdoc")){
//			 �����ز��գ����ű仯��Ӱ��ְ����Ϣ
			String[] keys = { "pk_postdoc", "pk_jobserial", "pk_jobrank"};
			for(String k:keys){
				ListRefUtils.clearRefValueByKey(k, row, billModel);
			}
			ListRefUtils.resetPostRef(ListRefUtils.getRefPaneByKey("pk_postdoc", billModel), getCurrentBodyPageCode(), Global.getCorpPK(), value);

		}else if(key.startsWith("pk_jobserial")){
//			���ְ��������λ����,��ôְ�����������λʱ���ְ��
			if(Util.isDutyDependJobSeries){
				String pk_jobserial =(String) refpane.getRefValue("pk_defdoc");				
				UIRefPane dutyrefPane = ListRefUtils.getRefPaneByKey("pk_om_duty", billModel);
				String om_duty =(String) dutyrefPane.getRefValue("series");
				if(om_duty==null || pk_jobserial == null || om_duty.equalsIgnoreCase(pk_jobserial)){
					;
				}else{
					ListRefUtils.clearRefValueByKey("pk_om_duty", row, billModel);
				}
			}
		}else if(key.startsWith("pk_postdoc")){
			afterEditPost(refpane, billModel, row, Global.getCorpPK());
		}else if(key.startsWith("pk_om_duty") ||key.startsWith("pk_om_duty_showname")){
			afterEditDuty(refpane, billModel, row, Global.getCorpPK());
		}
		
		deafBodyRefNameByPK(CommonValue.HI_PSNDOC_DEPTCHG);
	}
	private void afterEditDuty(UIRefPane refpane, BillModel billModel, int row,
			String pk_corp) {
		// ְ���
		UIRefPane detytyperefPane = ListRefUtils.getRefPaneByKey("pk_detytype",
				billModel);
		//ְ��������λ����ʱ��series ��Ӧ��λ����
		String series = (String) refpane.getRefValue("series");
		if (!Util.isDutyDependJobSeries && !StringUtils.isBlank(series)) {
			ListRefUtils.resetDeptType(detytyperefPane, pk_corp);
			setBodyValueAt(getCurrentBodyPageCode(), "pk_detytype", row, series);
			detytyperefPane.setEnabled(false);
		} else {
			ListRefUtils.clearRefValueByKey("pk_detytype", row, billModel);
			detytyperefPane.setEnabled(false);
		}
		// �ж�ְ���Ը�λ����Ϊ���෽ʽ
		// 1.���ѡ���ְ�����ڸ�λ����,���λ���в���
		// 2.���ѡ���ְ�����ڸ�λ����,���λ�������
		String pk_jobserial = ListRefUtils.getRefPaneByKey("pk_jobserial",
				billModel).getRefPK();
		if(StringUtils.isBlank(pk_jobserial) ){
			if(Util.isDutyDependJobSeries) 
			setBodyValueAt(getCurrentBodyPageCode(), "pk_jobserial", row, series);
		} else if (Util.isDutyDependJobSeries && !pk_jobserial.equals(series)) {
			ListRefUtils.clearRefValueByKey("pk_jobserial", row, billModel);
		}
		if (getBodyBillModel(getCurrentBodyPageCode()).getItemByKey("pk_dutyrank") != null) {
			String pk_dutyrank = (String) refpane.getRefValue("dutyrank");
			if (!StringUtils.isBlank(pk_dutyrank)) {
				setBodyValueAt(getCurrentBodyPageCode(), "pk_dutyrank", row,pk_dutyrank);
			} else {
				ListRefUtils.clearRefValueByKey("pk_dutyrank", row, billModel);
			}
		}
	}
	private void afterEditPost(UIRefPane refpane,BillModel billModel,int row,String pk_corp){
		//��λ
		String jobseries = (String)refpane.getRefValue("om_job.jobseries");
		UIRefPane dutyrefPane = ListRefUtils.getRefPaneByKey("pk_om_duty", billModel);
		//�޸ĸ�λ������ְ�����
//		ListRefUtils.resetDutyRef(dutyrefPane, getCurrentBodyPageCode(), pk_corp, jobseries);
		//��λ�ж�Ӧְ������ְ��ֵ��ְ����ղ�����
		//û�ж�Ӧְ��ְ����տ���
		String pk_om_duty = (String)refpane.getRefValue("om_job.pk_om_duty");
		if(!StringUtils.isBlank(pk_om_duty)){
			setBodyValueAt(getCurrentBodyPageCode(), "pk_om_duty", row, pk_om_duty);
			dutyrefPane.setEnabled(true);
		}else{
			//fengwei 2010-11-20 ��λ�仯�������λû�ж�Ӧְ��ְ�񲻱䣬����ʾԭ���ģ������
//			ListRefUtils.clearRefValueByKey("pk_om_duty", row, billModel);
			dutyrefPane.setEnabled(true);
		}
		//ְ���
		UIRefPane detytyperefPane = ListRefUtils.getRefPaneByKey("pk_detytype", billModel);
		String series = (String)refpane.getRefValue("om_duty.series");
		if(!Util.isDutyDependJobSeries
				&& !StringUtils.isBlank(series)){
			setBodyValueAt(getCurrentBodyPageCode(), "pk_detytype", row, series);
			detytyperefPane.setEnabled(true);
		}else{
			//ListRefUtils.clearRefValueByKey("pk_detytype", row, billModel);
			detytyperefPane.setEnabled(true);
		}
		//ְ�񼶱�
		UIRefPane dutyrankrefPane = ListRefUtils.getRefPaneByKey("pk_dutyrank", billModel);
		String pk_dutyrank = (String)refpane.getRefValue("om_duty.dutyrank");
		if (!StringUtils.isBlank(pk_dutyrank)) {
			setBodyValueAt(getCurrentBodyPageCode(), "pk_dutyrank", row,pk_dutyrank);
		}else{
			ListRefUtils.clearRefValueByKey("pk_dutyrank", row, billModel);
		}		
		if (dutyrankrefPane!=null) {
			dutyrankrefPane.setEnabled(true);
		}		
		//��λ����
		UIRefPane jobseriesrefPane = ListRefUtils.getRefPaneByKey("pk_jobserial", billModel);
		String pk_jobserial = (String)refpane.getRefValue("om_job.jobseries");
		if (!StringUtils.isBlank(pk_jobserial)) {
			setBodyValueAt(getCurrentBodyPageCode(), "pk_jobserial", row, pk_jobserial);
			jobseriesrefPane.setEnabled(false);
		}else{
			ListRefUtils.clearRefValueByKey("pk_jobserial", row, billModel);
			jobseriesrefPane.setEnabled(true);
		}		
		//��λ�ȼ�
		UIRefPane jobrankrefPane = ListRefUtils.getRefPaneByKey("pk_jobrank", billModel);
		String pk_jobrank = (String)refpane.getRefValue("om_job.jobrank");
		if (!StringUtils.isBlank(pk_jobrank)) {
			setBodyValueAt(getCurrentBodyPageCode(), "pk_jobrank", row,pk_jobrank);
			jobrankrefPane.setEnabled(false);
		}else{
			ListRefUtils.clearRefValueByKey("pk_jobrank", row, billModel);
			jobrankrefPane.setEnabled(true);
		}		
	}
	private void afterEditPartRef(BillEditEvent e){
		Object obj = e.getSource();
		if(!(obj instanceof BillCellEditor && ((BillCellEditor)obj).getComponent() instanceof UIRefPane))
			return;
		BillModel billModel = getBodyBillModel(CommonValue.HI_PSNDOC_PART);
		String key = e.getKey();
		String showname = BillPanelUtils.REF_SHOW_NAME;
		String containKeys[] = new String[]{"pk_corp"+showname,
				"pk_jobserial"+showname,
				"pk_deptdoc"+showname,
				"pk_postdoc"+showname,
				"pk_om_duty"+showname};
		if(!ArrayUtils.contains(containKeys, key)){
			return;
		}
		int row = e.getRow();
		String value;
		if (e.getValue()!=null && !StringUtils.isBlank(e.getValue().toString()) ) {			
			Object vobj = ((DefaultConstEnum) e.getValue()).getValue();
			value = (vobj != null)?vobj.toString():null;
		}else{
			value = null;
		}		
		UIRefPane refpane = ListRefUtils.getRefPaneByKey(key, billModel);		
		if (key.startsWith("pk_corp")) {
			ListRefUtils.resetCorpRef(getCurrentBodyPageCode(), value, billModel);
			//�����ز���
			String[] keys = { "pk_deptdoc", "pk_postdoc", "pk_om_duty",
					"pk_detytype", "pk_jobserial", "pk_jobrank", "pk_psncl" };
			for(String s:keys){
				ListRefUtils.clearRefValueByKey(s, row, billModel);
			}
		}//��λ����
		else if (key.startsWith("pk_jobserial")) {
			String partCorpPK = ListRefUtils.getRefPaneByKey("pk_corp", billModel).getRefPK();
			//ְ��
			ListRefUtils.resetDutyRef(ListRefUtils.getRefPaneByKey("pk_om_duty", billModel), getCurrentBodyPageCode(), partCorpPK, value);					
		}//�������ʱ���λ����
		else if (key.startsWith("pk_deptdoc")) {	
			String partCorpPK = ListRefUtils.getRefPaneByKey("pk_corp", billModel).getRefPK();
			//�����ز���
			String[] keys = { "pk_postdoc","pk_jobserial", "pk_jobrank" };
			for(String s:keys){
				ListRefUtils.clearRefValueByKey(s, row, billModel);
			}
			//��λ
			ListRefUtils.resetPostRef(ListRefUtils.getRefPaneByKey("pk_postdoc", billModel), getCurrentBodyPageCode(), partCorpPK, value);
			
		}//��λ����
		else if (key.startsWith("pk_postdoc")) {
			String partCorpPK = ListRefUtils.getRefPaneByKey("pk_corp", billModel).getRefPK();
			afterEditPost(refpane, billModel, row, partCorpPK);
			
		}//���ְ�����
		else if (key.startsWith("pk_om_duty")) {
			String partCorpPK = ListRefUtils.getRefPaneByKey("pk_corp", billModel).getRefPK();
			afterEditDuty(refpane, billModel, row, partCorpPK);
		}
		deafBodyRefNameByPK(CommonValue.HI_PSNDOC_PART);
	}
	
	public void bodyRowChange(BillEditEvent e) {
		if (e == null) {
			return;
		}
		int selectedRow = e.getRow();
		if (selectedRow < 0) {
			return;
		}
		if (e.getSource() == getBillListPanel().getHeadTable()) {
			PsndocVO psndocvo = (PsndocVO) getBillListPanel()
					.getHeadBillModel().getBodyValueRowVO(selectedRow,
							getDataMdl().getHeadVOClass().getName());
			getDataMdl().setSelectedPerson(psndocvo);
			
			getDataModel().setCurrentSelectIndex(getHeadSelectedRow());
			
			HRAggVO hrAggVO = loadBodyData();            
			
            setBodyData(hrAggVO);    
            
            if (getDataModel().isAutoDealWithRefField())
            {
                BillPanelUtils.dealWithRefShowNameByPk(getBillListPanel(), IBillItem.BODY);
            }
            
            getDataModel().getBillBufferUtils().setCurrentBufferData(hrAggVO);
            getDataModel().getBillBufferUtils().setBufferData(new HRAggVO[]{hrAggVO}, false);
            
            hrAggCacheVOs = getDataModel().getBillBufferUtils().getBufferDatas();
            
            ((StafTransRdsUI)getParentUI()).setCurrentState(
							getDataMdl().isQueryPsn() ? StafTransRdsStateReg.PSN_PSNLIST_SELECTED
									: StafTransRdsStateReg.RDS_PSNLIST_SELECTED);
		} else if (getDataMdl().getCurrentState() != StafTransRdsStateReg.SUBLIST_EDITING) {
			if(getParentUI().getModuleCode().equals(CommonValue.TURNOVER__RECORDS)){
				//��ְ��¼Ҫ�жϵ�ǰ��¼����ְ�����˾�Ƿ�ǰ��˾��
				SuperVO[] vos = getBodySelectedDatas();
				if(vos.length<0) return;
				if(!PubEnv.getPk_corp().equals(vos[0].getAttributeValue("pk_corpafter"))){
					return;
				}
				
			}
			((StafTransRdsUI)getParentUI()).setCurrentState(
							getDataMdl().isQueryPsn() ? StafTransRdsStateReg.PSN_SUBLIST_SELECTED
									: StafTransRdsStateReg.RDS_SUBLIST_SELECTED);
		}
	}

	@Override
	public void stateChanged(StateChangeEvent event) {
		switch (event.getCurrentState()) {
		case StafTransRdsStateReg.SUBLIST_EDITING:
			getBillListPanel().getBodyTabbedPane().setEnabled(false);
			break;
		default:
			getBillListPanel().getBodyTabbedPane().setEnabled(true);
		}
	}

	public int getBodySelectedPaneRowCount(String tablecode) {
		return getBillListPanel().getBodyScrollPane(tablecode).getTableModel()
				.getRowCount();
	}

	public Object getBodyValueAt(String strTableCode, String strItemKey,
			int iRowIndex) {
		return getBillListPanel().getBodyScrollPane(strTableCode)
				.getTableModel().getValueAt(iRowIndex, strItemKey);
	}

	public BillModel getBodyBillModel(String strTabCode) {
		return getBillListPanel().getBodyBillModel(strTabCode);
	}

	public void setCellEditable(String strTabCode, String key, int row,
			boolean isEditable) {
		getBodyBillModel(strTabCode).setCellEditable(row, key, isEditable);
	}

	/**
	 * �����ͷ������ʾֵ
	 *
	 */
	public void dealHeadRefNameByPK() {
		BillPanelUtils.dealWithRefShowNameByPk(getBillListPanel()
				.getHeadBillModel().getBodyItems(), getBillListPanel()
				.getHeadBillModel());
	}

	/**
	 * ������������ʾֵ
	 * @param tablecode
	 */
	public void deafBodyRefNameByPK(String tablecode) {
		BillPanelUtils.dealWithRefShowNameByPk(getBillListPanel()
				.getBodyBillModel(tablecode).getBodyItems(), getBillListPanel()
				.getBodyBillModel(tablecode));
	}

	@Override
	public void setMainPanelEnabled(boolean blEnabled) {
		super.setMainPanelEnabled(blEnabled);
		getBillListPanel().getHeadTable().setEnabled(!blEnabled);
	}

	@Override
	public boolean isFullTableEdit() {
		return false;
	}

	@Override
	public boolean insertLine(String strTableCode) {

		getBillListPanel().setEnabled(true);
		BillScrollPane headPanel = getBillListPanel().getBodyScrollPane(
				strTableCode);
		headPanel.insertLine(headPanel.getTable().getSelectedRow(), 1);

		BillModel billModel = getBillListPanel().getBodyBillModel(strTableCode);

		setFullTableEditable(billModel, getBodySelectedRow(strTableCode));

		return true;
	}

	@Override
	public int getCurrentBodyPageIndex() {
		return getBillListPanel().getBodyTabbedPane().getSelectedIndex();
	}
	public BillItem getRefItemByKey(int iPosition,String strTabCode,String strItem){
		strItem+=BillPanelUtils.REF_SHOW_NAME;
		return BillPanelUtils.getFromListPanel(getBillListPanel(), iPosition, strTabCode, strItem);
	}
	/**
	 * У��ָ���Ӽ��ǿ���
	 * @param strTabCode
	 * @throws ValidationException
	 */
	public void dataNotNullValidate(String strTabCode) throws ValidationException{
		
		stopEditing();		
		
		BillModel bodyBillModel = getBillListPanel().getBodyBillModel(strTabCode);
		
		int selrow = bodyBillModel.getEditRow();
		
		if (bodyBillModel == null)
        {
            return;
        }
        
		BillItem bodyBillItems[] = bodyBillModel.getBodyItems();
        
        for (BillItem element2 : bodyBillItems)
        {
            if (element2.isNull())
            {
                Object aValue = bodyBillModel.getValueAt(selrow, element2.getKey());
                
                if (aValue == null || aValue.equals(""))
                {
                    // "�� {0} �� ��{1}��";
                    String strMessage =
                        ResHelper.getString("nc_hr_ui_frame", "UPPnc_hr_ui_frame-000139", new String[]{String.valueOf(selrow + 1),
                            element2.getName()});
                    
                    String strBodyTableName = getBillListPanel().getBillListData().getBodyTableName(strTabCode);
                    
                    // "��{0}��ҳǩ {1};
                    strMessage = ResHelper.getString("nc_hr_ui_frame", "UPPnc_hr_ui_frame-000140", new String[]{strBodyTableName})+strMessage;
                    
                    startEditing();
                    
                    // @res ����ǿռ�飺
                    throw new NullFieldException(strMessage);
                }
            }
        }
	}
	

}
