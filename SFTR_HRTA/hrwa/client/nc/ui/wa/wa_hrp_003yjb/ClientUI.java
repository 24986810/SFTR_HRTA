package nc.ui.wa.wa_hrp_003yjb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.bs.framework.common.NCLocator;
import nc.itf.hr.wa.IItem;
import nc.itf.hrp.pub.IHRPBtn;
import nc.itf.uap.IUAPQueryBS;
import nc.itf.wa.hrp.pub.IHRPWABtn;
import nc.itf.wa.wa_hrp_001.ISalaryProjServ;
import nc.itf.wa.wa_hrppub.WaHrpBillStatus;
import nc.jdbc.framework.processor.BeanProcessor;
import nc.ui.hr.global.Global;
import nc.ui.hrp.pub.bill.HRPManagerUI;
import nc.ui.pub.FramePanel;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.bill.BillEditEvent;
import nc.ui.pub.bill.BillItem;
import nc.ui.pub.bill.BillModel;
import nc.ui.pub.linkoperate.ILinkApproveData;
import nc.ui.pub.linkoperate.ILinkMaintainData;
import nc.ui.trade.base.IBillOperate;
import nc.ui.trade.bill.AbstractManageController;
import nc.ui.trade.business.HYPubBO_Client;
import nc.ui.trade.button.IBillButton;
import nc.ui.trade.manage.ManageEventHandler;
import nc.ui.wa.pub.WADelegator;
import nc.vo.pub.BusinessException;
import nc.vo.pub.btn.ExcelOutBtnVO;
import nc.vo.trade.button.ButtonVO;
import nc.vo.wa.wa_001.WaclassHeaderVO;
import nc.vo.wa.wa_024.ItemVO;
import nc.vo.wa.wa_hrp_001.DeptClassItemVO;
import nc.vo.wa.wa_hrp_002.PERIODVO;
import nc.vo.wa.wa_hrp_002.PsnClassItemBVO;
import nc.vo.wa.wa_hrp_002.PsnClassItemHVO;
import nc.vo.wa.wa_hrppub.CancelPayButtonVO;
import nc.vo.wa.wa_hrppub.CommitReturnBtn;
import nc.vo.wa.wa_hrppub.FileManageBtnVO;
import nc.vo.wa.wa_hrppub.PayButtonVO;
import nc.vo.wa.wa_hrppub.QueryDeptButtonVO;

/**
 * @author tianxfc
 * 2018-11-22
 * 规培生奖金审核（一级半审核）节点代码
 */
@SuppressWarnings("restriction")
public class ClientUI extends HRPManagerUI{
	private static final long serialVersionUID = 1L;
	public String pk_deptid = null;
	public String pk_psndoc = null;
	public DeptClassItemVO[] itemvos = null;
	private String pk_wa_class = IHRPWABtn.PK_JIANG;//新华设置为默认奖金类别

	HashMap<String,ArrayList<DeptClassItemVO>> deptitemvos=null;
	public HashMap<String,ItemVO> mapitem = new HashMap<String, ItemVO>();
	public HashMap<String, String> mapkeytoitem = new HashMap<String, String>();
	public int MAX_INDEX = 150;
	PERIODVO waPeriodVO = null;
	boolean iscan = false;


	public ClientUI() {
		getBillListPanel().getHeadTable().setSelectionMode(javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		initDeptItem();
		try {
			ClientEventHandler myenventhandler= (ClientEventHandler) createEventHandler();
			myenventhandler.onBoQueryInit();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @param fp
	 */
	public ClientUI(FramePanel fp) {
		super(fp);
		getBillListPanel().getHeadTable().setSelectionMode(javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		
		initDeptItem();
		
		try {
			ClientEventHandler myenventhandler= (ClientEventHandler) createEventHandler();
			myenventhandler.onBoQueryInit();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @param useBillSource
	 */
	public ClientUI(Boolean useBillSource) {
		super(useBillSource);
		getBillListPanel().getHeadTable().setSelectionMode(javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		initDeptItem();
		
		try {
			ClientEventHandler myenventhandler= (ClientEventHandler) createEventHandler();
			myenventhandler.onBoQueryInit();
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
	public ClientUI(String pk_corp, String pk_billType, String pk_busitype,
			String operater, String billId) {
		super(pk_corp, pk_billType, pk_busitype, operater, billId);
		initDeptItem();
		try {
			ClientEventHandler myenventhandler= (ClientEventHandler) createEventHandler();
			myenventhandler.onBoQueryInit();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Override
	protected AbstractManageController createController() {
		return new ClientControl();
	}
	@Override
	protected ManageEventHandler createEventHandler() {
		return new ClientEventHandler(this,this.getUIControl());
	}
	@Override
	public void setDefaultData() throws Exception {
	}
	@Override
	protected void initSelfData() {
		super.initSelfData();
		nc.vo.pub.bill.BillRendererVO voCell = new nc.vo.pub.bill.BillRendererVO();
		voCell.setShowZeroLikeNull(false);//---liuxiaoxi 2009-07-21 显示表体0
		getBillCardPanel().setBodyShowFlags(voCell);
		getBillCardPanel().setAutoExecHeadEditFormula(true);
		UIRefPane pane1 = (UIRefPane)getBillCardPanel().getBillModel().getItemByKey("psnname").getComponent();
		pane1.setMultiSelectedEnabled(true);
		String[] values = WaHrpBillStatus.STATUSSHOW; 
		//一级审核状态
		getBillCardWrapper().initHeadComboBox("pk_item12", values, true);
		getBillListWrapper().initHeadComboBox("pk_item12", values, true);
		//一级半审核状态
		getBillCardWrapper().initHeadComboBox("pk_item14", values, true);
		getBillListWrapper().initHeadComboBox("pk_item14", values, true);
		//二级审核状态
		getBillCardWrapper().initHeadComboBox("vbillstatus_audit", values, true);
		getBillListWrapper().initHeadComboBox("vbillstatus_audit", values, true);
	}
	@Override
	protected void initPrivateButton() {
		super.initPrivateButton();
		ButtonVO commitReturnBtn = new CommitReturnBtn().getButtonVO();
		addPrivateButton(commitReturnBtn);
		commitReturnBtn.setOperateStatus(new int[]{IBillOperate.OP_NOTEDIT});
		ButtonVO importbtn = new PayButtonVO().getButtonVO();
		addPrivateButton(importbtn);
//		importbtn.setOperateStatus(new int[]{IBillOperate.OP_NO_ADDANDEDIT});
		ButtonVO cancelimportbtn = new CancelPayButtonVO().getButtonVO();
		addPrivateButton(cancelimportbtn);
		cancelimportbtn.setOperateStatus(new int[]{IBillOperate.OP_NO_ADDANDEDIT});

		ButtonVO querydept = new QueryDeptButtonVO().getButtonVO();
		addPrivateButton(querydept);
		ButtonVO filebtn = new FileManageBtnVO().getButtonVO();

		ButtonVO outbtn = new ExcelOutBtnVO().getButtonVO();
		addPrivateButton(outbtn);
//		addPrivateButton(filebtn);
	}
	/**
	 * @author 宋旨昊
	 * 2011-3-21下午01:30:53
	 * 说明：初始化部门薪酬项目信息
	 */
	private void initDeptItem(){
		try {

			BillItem[] items = getBillCardPanel().getBillModel("wa_psn_item_b").getBodyItems();
			for(BillItem item:items){
				if(item.getKey().equals("bisselect")){
					item.setWidth(40);
				}else{
					item.setWidth(70);
				}
			}
			getBillCardPanel().setBillData(getBillCardPanel().getBillData());

			items = getBillListPanel().getBodyBillModel("wa_psn_item_b").getBodyItems();
			for(BillItem item:items){
				item.setWidth(70);
			}
			getBillListPanel().setListData(getBillListPanel().getBillListData());
//			String userid = _getOperator();
//			Ihrppub ihrpub=NCLocator.getInstance().lookup(Ihrppub.class);
//			ArrayList<UserDeptVO> listuserdptvo=ihrpub.getUserDeptvo(_getCorp().getPrimaryKey(), userid, IhrpNCModule.HRWA);
//			StringBuffer sb=new StringBuffer("  ");
//			for(int i=0;i<listuserdptvo.size();i++){
//			sb.append(" '").append(listuserdptvo.get(i).getPk_deptdoc()).append("',");
//			}
//			pk_deptid=sb.toString().substring(0,sb.toString().length()-1);
//			if(pk_deptid==null){
//			//MessageDialog.showHintDlg(this, "提示", "当前用户未设置业务员信息，不能进行增/删/改等操作");
//			}else{
//			itemvos = (DeptClassItemVO[])HYPubBO_Client.queryByCondition(DeptClassItemVO.class,
//			" isnull(dr,0)=0 and pk_dept in ("+pk_deptid+") and isnull(bisclose,'N')='N' ");
//			}
//			HashMap<String,ArrayList<DeptClassItemVO>> deptitemvosnow=new HashMap<String, ArrayList<DeptClassItemVO>>();
//			for(int m=0;m<itemvos.length;m++){
//			ArrayList<DeptClassItemVO> templist=new ArrayList<DeptClassItemVO>();
//			if(deptitemvosnow.containsKey(itemvos[m].getPk_dept())){
//			templist=deptitemvosnow.get(itemvos[m].getPk_dept());//2012.04.20 更改map
//			}
//			templist.add(itemvos[m]);
//			deptitemvosnow.put(itemvos[m].getPk_dept(),templist);
//			}
//			deptitemvos=deptitemvosnow;
			IItem item = (IItem)NCLocator.getInstance().lookup(IItem.class);
			ItemVO[] itemvos = item.queryAllItem(_getCorp().getPrimaryKey());
			if(itemvos!=null&&itemvos.length>0){
				for(ItemVO itemvo:itemvos){
					mapitem.put(itemvo.getPrimaryKey(), itemvo);
				}
			}

			try{
				WaclassHeaderVO[] m_waclasss = WADelegator.getWaClass().queryAllHeader(Global.getCorpPK(), Global.getUserID());

				String period=null;
				String year=null;

				for(WaclassHeaderVO wa_class:m_waclasss) {
					if(wa_class.getPk_wa_class().equals(pk_wa_class)) {
						String strperiod = WADelegator.getWaPeriod().getYWPeriod(wa_class.getPk_wa_class(), false);
						//period=wa_class.getCperiod();
						period=strperiod.substring(4);
						year=strperiod.substring(0,4);
					}
				}

				IUAPQueryBS service = NCLocator.getInstance().lookup(IUAPQueryBS.class);

				String sql="select * from wa_period where isnull(dr,0)=0 and cperiod='"+period+"' and cyear='"+year+"'";
				waPeriodVO = (PERIODVO) service.executeQuery(sql, new BeanProcessor(PERIODVO.class)); 


				String curdate = _getDate().toString().substring(0,7);
				if(waPeriodVO!=null){
					if((waPeriodVO.getCyear()+"-"+waPeriodVO.getVcalmonth()).equals(curdate)){
						iscan = true;
					}
				}
				if(!iscan){
					MessageDialog.showHintDlg(this, "提示", "登陆期间不是最小未结账月，不能进行业务操作，只有查询权限");
				}
				if(waPeriodVO==null){
					MessageDialog.showHintDlg(this, "提示", "没有奖金类别权限，请联系管理员");
				}
			}catch(Exception e){
				waPeriodVO = null;
				iscan = false;
				if(!iscan){
					MessageDialog.showHintDlg(this, "提示", "登陆期间不是最小未结账月，不能进行业务操作，只有查询权限");
				}
			}
		} catch (BusinessException e) {
			e.printStackTrace();
		}
		//行操作按钮只留增行删行，其他都隐藏掉。
		getButtonManager().getButton(IBillButton.Audit).setName("审核");
		getButtonManager().getButton(IHRPBtn.COMMITREURN).setName("驳回");
		getButtonManager().getButton(IBillButton.CancelAudit).setName("取消审核");
		updateButtons();
		getBillListPanel().getHeadTable().setSelectionMode(javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
	}
	@Override
	public void doApproveAction(ILinkApproveData approvedata) {
		super.doApproveAction(approvedata);
		updateBtn();
		updateItems();
	}
	@Override
	public void doMaintainAction(ILinkMaintainData maintaindata) {
		super.doMaintainAction(maintaindata);
		updateBtn();
		updateItems();
	}

	public DeptClassItemVO[] getItemvos() {
		return itemvos;
	}

	public void setItemvos(DeptClassItemVO[] itemvos) {
		this.itemvos = itemvos;
	}

	public String getPk_deptid() {
		return pk_deptid;
	}

	public void setPk_deptid(String pk_deptid) {
		this.pk_deptid = pk_deptid;
	}
	@Override
	public void afterEdit(BillEditEvent e) {
		if(e.getKey().equals("psnname")){
		}else{
			super.afterEdit(e);
		}
	}

	public String getPk_psndoc() {
		return pk_psndoc;
	}

	public void setPk_psndoc(String pk_psndoc) {
		this.pk_psndoc = pk_psndoc;
	}

	public HashMap<String, ItemVO> getMapitem() {
		return mapitem;
	}

	public void setMapitem(HashMap<String, ItemVO> mapitem) {
		this.mapitem = mapitem;
	}

	public HashMap<String, String> getMapkeytoitem() {
		return mapkeytoitem;
	}

	public void setMapkeytoitem(HashMap<String, String> mapkeytoitem) {
		this.mapkeytoitem = mapkeytoitem;
	}
	
	/**
	 * @author 宋旨昊
	 * 2011-3-22上午11:12:35
	 * 说明：根据当前单据审批状态设置按钮状态
	 */
	public void updateBtn(){
		if(getBufferData().getCurrentVO()!=null&&getBufferData().getCurrentVO().getParentVO()!=null){
			PsnClassItemHVO hvo = (PsnClassItemHVO)getBufferData().getCurrentVO().getParentVO();
//			if(getBufferData() != null && getBufferData().getCurrentVO() != null && getBufferData().getCurrentVO().getChildrenVO() != null){
//				PsnClassItemBVO[] vos = (PsnClassItemBVO[]) getBufferData().getCurrentVO().getChildrenVO();
//				List<PsnClassItemBVO> list = new ArrayList<PsnClassItemBVO>();
//				if(vos != null){
//					for(PsnClassItemBVO vo : vos){
//						if(vo.getVdef17() != null && "03".equals(vo.getVdef17())){
//							list.add(vo);
//						}
//					}
//				}
//				if(list != null && !list.isEmpty()){
//					getBufferData().getCurrentVO().setChildrenVO(list.toArray(new PsnClassItemBVO[0]));
//				}
//			}
			
			
			//一级半审核状态
			int pk_item14 = hvo.getPk_item14() != null ? hvo.getPk_item14() : -1;
			int status_audit = hvo.getVbillstatus_audit()!=null?hvo.getVbillstatus_audit():-1;
			if(!iscan){
				getButtonManager().getButton(IBillButton.Audit).setEnabled(false);
				getButtonManager().getButton(IBillButton.CancelAudit).setEnabled(false);
				//getButtonManager().getButton(IHRPWABtn.PAYMNY).setEnabled(false);
				//getButtonManager().getButton(IHRPWABtn.CANCELPAYMNY).setEnabled(false);
				getButtonManager().getButton(IHRPBtn.COMMITREURN).setEnabled(false);
				//if(status_audit==WaHrpBillStatus.DEL){
				//	getButtonManager().getButton(IBillButton.Del).setEnabled(false);
				//}
			}else{
				if(isListPanelSelected()){
					//getButtonManager().getButton(IHRPWABtn.PAYMNY).setEnabled(true);
					getButtonManager().getButton(IBillButton.Audit).setEnabled(true);
					getButtonManager().getButton(IBillButton.CancelAudit).setEnabled(true);
					getButtonManager().getButton(IHRPBtn.COMMITREURN).setEnabled(true);
					//当二级已审核（通过、不通过、驳回，则一级半不能进去审核/取消审核/驳回等操作）
					if(status_audit == 2 || status_audit == 3 || status_audit == 4){
						getButtonManager().getButton(IBillButton.Audit).setEnabled(false);        //审核
						getButtonManager().getButton(IBillButton.CancelAudit).setEnabled(false);  //取消审核
					}
					if(pk_item14 == 2){
						getButtonManager().getButton(IBillButton.Audit).setEnabled(false);       //审核
						getButtonManager().getButton(IBillButton.CancelAudit).setEnabled(true);  //取消审核
					}else if(pk_item14 == 3){
						getButtonManager().getButton(IBillButton.Audit).setEnabled(true);        //审核
						getButtonManager().getButton(IBillButton.CancelAudit).setEnabled(false); //取消审核
					}else if(pk_item14 == 4){
						getButtonManager().getButton(IBillButton.Audit).setEnabled(true);        //审核
						getButtonManager().getButton(IBillButton.CancelAudit).setEnabled(false); //取消审核
					}else{
						getButtonManager().getButton(IBillButton.Audit).setEnabled(true);        //审核
						getButtonManager().getButton(IBillButton.CancelAudit).setEnabled(false); //取消审核
					}
				}else{
					switch (pk_item14) {
//					case WaHrpBillStatus.DEL:
//						getButtonManager().getButton(IHRPBtn.COMMITREURN).setEnabled(false);
//						//getButtonManager().getButton(IHRPWABtn.PAYMNY).setEnabled(false);
//						getButtonManager().getButton(IBillButton.Audit).setEnabled(false);
//						getButtonManager().getButton(IBillButton.CancelAudit).setEnabled(false);
//						//getButtonManager().getButton(IHRPWABtn.CANCELPAYMNY).setEnabled(false);
//						getButtonManager().getButton(IBillButton.Del).setEnabled(false);
//						break;
					case WaHrpBillStatus.WRITE:
					case WaHrpBillStatus.NOPASS_RETURN:
						getButtonManager().getButton(IHRPBtn.COMMITREURN).setEnabled(false);
						//getButtonManager().getButton(IHRPWABtn.PAYMNY).setEnabled(false);
						getButtonManager().getButton(IBillButton.Audit).setEnabled(false);
						getButtonManager().getButton(IBillButton.CancelAudit).setEnabled(false);
						//getButtonManager().getButton(IHRPWABtn.CANCELPAYMNY).setEnabled(false);
						//getButtonManager().getButton(IBillButton.Del).setEnabled(true);
						break;
					case WaHrpBillStatus.NOPASS:
					case WaHrpBillStatus.COMMIT:
						getButtonManager().getButton(IBillButton.Audit).setEnabled(true);
						getButtonManager().getButton(IHRPBtn.COMMITREURN).setEnabled(true);
						getButtonManager().getButton(IBillButton.CancelAudit).setEnabled(false);
						//getButtonManager().getButton(IHRPWABtn.PAYMNY).setEnabled(false);
						//getButtonManager().getButton(IHRPWABtn.CANCELPAYMNY).setEnabled(false);
						//getButtonManager().getButton(IBillButton.Del).setEnabled(true);
						break;
					case WaHrpBillStatus.PASS:
						getButtonManager().getButton(IHRPBtn.COMMITREURN).setEnabled(false);
						getButtonManager().getButton(IBillButton.Audit).setEnabled(false);
						//getButtonManager().getButton(IBillButton.Del).setEnabled(false);
						if(hvo.getBispay()!=null&&hvo.getBispay().booleanValue()){
							//getButtonManager().getButton(IHRPWABtn.PAYMNY).setEnabled(false);
							getButtonManager().getButton(IBillButton.CancelAudit).setEnabled(false);
							//getButtonManager().getButton(IHRPWABtn.CANCELPAYMNY).setEnabled(true);
						}else{
							getButtonManager().getButton(IBillButton.CancelAudit).setEnabled(true);
							//getButtonManager().getButton(IHRPWABtn.PAYMNY).setEnabled(true);
							//getButtonManager().getButton(IHRPWABtn.CANCELPAYMNY).setEnabled(false);
						}
						break;
					default:
						break;
					}
				}
			}
			updateButtons();
		}
	}
	/**
	 * @author liqiange
	 * 
	 * 对薪资项目进行重新排序处理。
	 * 
	 * @param hvo
	 * @param index
	 * @return
	 * @throws BusinessException
	 */
	private BillItem[] reSetItemOrder(PsnClassItemHVO hvo,int index)throws BusinessException{
		BillItem []bis = null;
		String pk_wa_class = null;
		if(isListPanelSelected()){
			int selectRow = getBillListPanel().getHeadTable().getSelectedRow();
			pk_wa_class = (String)getBillListPanel().getHeadBillModel().getValueAt(selectRow, "pk_wa_class");
			bis = getBillListPanel().getBillListData().getBodyItems();
		}else{
			pk_wa_class = (String)getBillCardPanel().getHeadItem("pk_wa_class").getValueObject();
			bis = getBillCardPanel().getBodyItems();
		}

		ISalaryProjServ service = NCLocator.getInstance().lookup(ISalaryProjServ.class);
		Map<String,Map<String,Integer>>map = service.qrySalaryProj(pk_wa_class,_getCorp().getPrimaryKey());



		Map<String,Integer>mnyBiMap = new HashMap<String,Integer>();
		for(int i=0;i<index;i++){
			String pk_item = hvo.getAttributeValue("pk_item"+""+(i+1)+"")!=null?hvo.getAttributeValue("pk_item"+""+(i+1)+"").toString().trim():"";
			//Integer order = map.get(pk_item);
			Map<String,Integer> keyOrder = map.get(pk_item);
			for(int j=0;j<bis.length;j++){
				if(keyOrder !=null && keyOrder.containsKey(bis[j].getName())){
					mnyBiMap.put(bis[j].getKey(),keyOrder.get(bis[j].getName()));
					break;
				}
			}
		}

		for(int i=0;i<bis.length;i++){

			for(int j=i+1;j<bis.length;j++){
				String ikey = bis[i].getKey();
				String jkey = bis[j].getKey();

				if(ikey.startsWith("nmny") && ikey.length()>4 
						&& jkey.startsWith("nmny") && jkey.length()>4){
					if(Integer.parseInt(ikey.substring(4, ikey.length()))<=index
							&& Integer.parseInt(jkey.substring(4, jkey.length()))<=index){
						if(mnyBiMap.get(jkey)!=null && mnyBiMap.get(ikey)!=null){
							if(mnyBiMap.get(jkey)<mnyBiMap.get(ikey)){
								BillItem bi = bis[i];
								bis[i] = bis[j];
								bis[j] = bi;
							}
						}else if(mnyBiMap.get(jkey)!=null && mnyBiMap.get(ikey)==null){
							BillItem bi = bis[i];
							bis[i] = bis[j];
							bis[j] = bi;
						}
					}
				}
			}
		}
		return bis;
	}
	/**
	 * @author 宋旨昊
	 * 2011-3-22上午11:11:52
	 * 说明：根据当前单据属性设置显示薪酬项目
	 */
	public void updateItems(){

		if(getBufferData().getCurrentVO()!=null&&getBufferData().getCurrentVO().getParentVO()!=null){
			PsnClassItemHVO hvo = (PsnClassItemHVO)getBufferData().getCurrentVO().getParentVO();
			int index = hvo.getNindex();
			if(isListPanelSelected()){
				for(int i=0;i<index;i++){
					String pk_item = hvo.getAttributeValue("pk_item"+""+(i+1)+"")!=null?hvo.getAttributeValue("pk_item"+""+(i+1)+"").toString().trim():"";
					BillItem itemlist = getBillListPanel().getBodyBillModel("wa_psn_item_b").getItemByKey("nmny"+""+(i+1)+"");
					if(itemlist!=null&&itemlist.getKey()!=null){
						itemlist.setShow(true);
						itemlist.setTatol(true);
						itemlist.setName(mapitem.get(pk_item)!=null?mapitem.get(pk_item).getVname():itemlist.getName());
					}
				}
				if(MAX_INDEX>index){
					for(int i=index;i<MAX_INDEX;i++){
						BillItem itemlist = getBillListPanel().getBodyBillModel("wa_psn_item_b").getItemByKey("nmny"+""+(i+1)+"");
						if(itemlist!=null&&itemlist.getKey()!=null){
							itemlist.setShow(false);
						}
					}
				}

				getBillListPanel().setBodyListData(getBillListPanel().getBillListData());

			}else{
				for(int i=0;i<index;i++){
					String pk_item = hvo.getAttributeValue("pk_item"+""+(i+1)+"")!=null?hvo.getAttributeValue("pk_item"+""+(i+1)+"").toString().trim():"";
					BillItem item = getBillCardPanel().getBillModel("wa_psn_item_b").getItemByKey("nmny"+""+(i+1)+"");
					if(pk_item!=null&&pk_item.trim().length()>0&&item!=null&&item.getKey()!=null){
						item.setShow(true);
						item.setName(mapitem.get(pk_item)!=null?mapitem.get(pk_item).getVname():item.getName());
					}
				}
				if(MAX_INDEX>index){
					for(int i=index;i<MAX_INDEX;i++){
						BillItem item = getBillCardPanel().getBillModel("wa_psn_item_b").getItemByKey("nmny"+""+(i+1)+"");
						if(item!=null&&item.getKey()!=null){
							item.setShow(false);
						}
					}
				}
				getBillCardPanel().setBillData(getBillCardPanel().getBillData());
			}
		}
	}
	@Override
	public void afterUpdate() {
		super.afterUpdate();
		updateItems();
		//reSetFldValue();
		getBillListPanel().getHeadItem("bisselect").setEnabled(true);
		getBillCardPanel().setEnabled(true);
		BillItem[] headitems = getBillCardPanel().getHeadItems();
		for(BillItem item:headitems){
			item.setEnabled(false);
		}
		BillItem[] items = getBillCardPanel().getBodyShowItems();
		for(BillItem item:items){
			item.setEnabled(item.getKey().equals("bisselect"));
			item.setEdit(item.getKey().equals("bisselect"));
		}
		
		if(getBufferData().getCurrentVO() != null && getBufferData().getCurrentVO().getChildrenVO() != null){
			PsnClassItemBVO[] vos = (PsnClassItemBVO[]) getBufferData().getCurrentVO().getChildrenVO();
			List<PsnClassItemBVO> list = new ArrayList<PsnClassItemBVO>();
			if(vos != null){
				for(PsnClassItemBVO vo : vos){
					if(vo.getVdef17() != null && "03".equals(vo.getVdef17())){
						list.add(vo);
					}
				}
			}
			if(list != null && !list.isEmpty()){
				getBufferData().getCurrentVO().setChildrenVO(null);
				getBufferData().getCurrentVO().setChildrenVO(list.toArray(new PsnClassItemBVO[0]));
			}
		}
		getBillCardPanel().setBillData(getBillCardPanel().getBillData());
		updateBtn();
		
	}
	private void reSetFldValue() {
		PsnClassItemBVO []vos = null;
		BillModel bm = null;
		if(isListPanelSelected()){
			bm = getBillListPanel().getBodyBillModel();
		}else{
			bm = getBillCardPanel().getBillModel();
		}

		try {
			if(getBufferData().getCurrentVO()==null){
				return;
			}
			String billid = (String)getBufferData().getCurrentVO().getParentVO().getPrimaryKey();
			vos = (PsnClassItemBVO[])HYPubBO_Client.queryByCondition(PsnClassItemBVO.class, " pk_psn_item_h='"+billid+"' and dr=0");
		} catch (BusinessException e) {
			e.printStackTrace();
		}
		
		Map<String,PsnClassItemBVO>map = new HashMap<String,PsnClassItemBVO>();
		for(PsnClassItemBVO bvo : vos){
			String pk = bvo.getPk_psn_item_b();
			map.put(pk, bvo);
		}
		for(int rowIndex=0;rowIndex<bm.getRowCount();rowIndex++){

			String pk = (String)bm.getValueAt(rowIndex, "pk_psn_item_b");
			PsnClassItemBVO bvo = map.get(pk);
			for(int j=1;j<31;j++){
				bm.setValueAt(bvo.getAttributeValue("nmny"+j), rowIndex, "nmny"+j);
			}
		}
	}
	
	
	@Override
	public void bodyRowChange(BillEditEvent e) {
		super.bodyRowChange(e);
		int selrow = e.getRow();
		getBufferData().setCurrentRow(selrow);
//		if(e.getPos()==HEAD){
//			updateItems();
//		}
	}

	public PERIODVO getWaPeriodVO() {
		return waPeriodVO;
	}

	public void setWaPeriodVO(PERIODVO waPeriodVO) {
		this.waPeriodVO = waPeriodVO;
	}
}
