package nc.ui.wa.wa_hrp_002;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.itf.hr.wa.IClassitem;
import nc.itf.hr.wa.IItem;
import nc.itf.hr.wa.IItemright;
import nc.itf.hrp.pub.IHRPBtn;
import nc.itf.hrwa.IHRWaServices;
import nc.itf.uap.IUAPQueryBS;
import nc.itf.wa.hrp.pub.IHRPWABtn;
import nc.itf.wa.wa_hrppub.WaHrpBillStatus;
import nc.jdbc.framework.processor.BeanProcessor;
import nc.ui.bd.ref.AbstractRefTreeModel;
import nc.ui.hr.global.Global;
import nc.ui.hrp.pub.bill.HRPManagerUI;
import nc.ui.pub.FramePanel;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UIDialog;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.bill.BillCardBeforeEditListener;
import nc.ui.pub.bill.BillEditEvent;
import nc.ui.pub.bill.BillItem;
import nc.ui.pub.bill.BillItemEvent;
import nc.ui.pub.bill.BillModel;
import nc.ui.pub.linkoperate.ILinkApproveData;
import nc.ui.pub.linkoperate.ILinkMaintainData;
import nc.ui.trade.base.IBillOperate;
import nc.ui.trade.bill.AbstractManageController;
import nc.ui.trade.business.HYPubBO_Client;
import nc.ui.trade.button.IBillButton;
import nc.ui.trade.manage.ManageEventHandler;
import nc.ui.wa.pub.WADelegator;
import nc.ui.wa.wa_031.InfoDLG;
import nc.uif.pub.exception.UifException;
import nc.vo.pub.BusinessException;
import nc.vo.pub.btn.DataUpdateBtnVO;
import nc.vo.pub.btn.ExcelImportBtnVO;
import nc.vo.pub.btn.ExcelParentImportButVo;
import nc.vo.pub.lang.UFDouble;
import nc.vo.tam.tongren001.DeptKqVO;
import nc.vo.trade.button.ButtonVO;
import nc.vo.trade.pub.IBillStatus;
import nc.vo.wa.itemright.ItemrightVO;
import nc.vo.wa.wa_001.WaclassHeaderVO;
import nc.vo.wa.wa_002.ClassitemVO;
import nc.vo.wa.wa_024.ItemVO;
import nc.vo.wa.wa_hrp_002.PERIODVO;
import nc.vo.wa.wa_hrp_002.PsnClassItemBVO;
import nc.vo.wa.wa_hrp_002.PsnClassItemHVO;
import nc.vo.wa.wa_hrp_002.PsnOutStaffVO;
import nc.vo.wa.wa_hrp_004.DeptMnyBVO;
import nc.vo.wa.wa_hrp_004.DeptMnyHVO;
import nc.vo.wa.wa_hrp_004.ItemSetBVO;
import nc.vo.wa.wa_hrp_009.UserDeptVO;
import nc.vo.wa.wa_hrppub.CommitReturnBtn;
import nc.vo.wa.wa_hrppub.FileManageBtnVO;
import nc.vo.wa.wa_hrppub.QueryDeptButtonSUMVO;
import nc.vo.wa.wa_hrppub.QueryDeptButtonVO;

/**
 * @author 宋旨昊
 * 2011-3-21上午10:33:19
 * 说明：
 */
@SuppressWarnings("restriction")
public class ClientUI extends HRPManagerUI implements BillCardBeforeEditListener{
	private static final long serialVersionUID = 1L;
	public String pk_deptid = null;
	public String pk_deptdoc = null;
	public String pk_psndoc = null;
	public ItemSetBVO[] itemvos = null;
	public HashMap<String,ItemVO> mapitem = new HashMap<String, ItemVO>();
	public HashMap<String,ClassitemVO> mapclassitem = new HashMap<String, ClassitemVO>();
	public HashMap<String, String> mapkeytoitem = new HashMap<String, String>();
	public int MAX_INDEX = 150;
	public String oldpk_dept=null;
	private String pk_wa_class = IHRPWABtn.PK_JIANG;//新华设置为默认奖金类别

	PERIODVO waPeriodVO = null;
	//查询科室对应的：本次可分配金额合计、月考核类、一次性奖金、调入月考核奖金、调出月考核奖金、调入一次性奖金金额、调出一次性奖金金额、累计结余
	Map<String, Map<String, Map<String, UFDouble>>> inoutMap = null;
	//科室奖金数据字典， key：奖金类别（合计金额：nmnyTotal、月考核奖：nmny1Total、一次性奖金：nmny2Total），value：奖金类别数据字典map
	//value：奖金类别数据字典map， key：奖金科室pk_dept, value：金额
	Map<String, Map<String, UFDouble>> ksjjMap = null;
	
	//获取所有科室上期累计结余数据字典，key：科室pk_dept， value：上期累计结余金额 vdef14
	Map<String, UFDouble> lastJYMap = null;
	
	//【薪酬类别】参照项数据字典：key：pk_wa_classitem（参照项主键）, value：vname（参照项名称）
	Map<String, String> waClassItemVOMap = null;
		
	//存储当前选择科室的本次可分配金额总数
	UFDouble curDeptTotalMoney = new UFDouble(0);
	
	boolean iscan = false;

	protected Integer lastbillstatus = IBillOperate.OP_INIT; 


	IUAPQueryBS service = NCLocator.getInstance().lookup(IUAPQueryBS.class);
	
	ArrayList<String> list_power_item = new ArrayList<String>();

	public ClientUI() {


		initDeptItem();
		try {
			ClientEventHandler myenventhandler = (ClientEventHandler) createEventHandler();
			myenventhandler.onBoQueryInit();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		getBillCardPanel().setBillBeforeEditListenerHeadTail(this);
	}

	/**
	 * @param fp
	 */
	public ClientUI(FramePanel fp) {
		super(fp);
		initDeptItem();
		try {
			ClientEventHandler myenventhandler = (ClientEventHandler) createEventHandler();
			myenventhandler.onBoQueryInit();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		getBillCardPanel().setBillBeforeEditListenerHeadTail(this);
	}

	/**
	 * @param useBillSource
	 */
	public ClientUI(Boolean useBillSource) {
		super(useBillSource);
		initDeptItem();
		try {
			ClientEventHandler myenventhandler = (ClientEventHandler) createEventHandler();
			myenventhandler.onBoQueryInit();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		getBillCardPanel().setBillBeforeEditListenerHeadTail(this);
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
			ClientEventHandler myenventhandler = (ClientEventHandler) createEventHandler();
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
		getBillCardPanel().setHeadItem("pk_corp", _getCorp().getPrimaryKey());
		getBillCardPanel().setHeadItem("dmakedate", _getDate());
		getBillCardPanel().setHeadItem("voperatorid", _getOperator());
		getBillCardPanel().setHeadItem("pk_billtype", getUIControl().getBillType());
		getBillCardPanel().getHeadItem("pk_dept").setValue(getPk_deptid());
		getBillCardPanel().getHeadItem("pk_psndoc").setValue(getPk_psndoc());
		getBillCardPanel().getHeadItem("vbillstatus").setValue(IBillStatus.FREE);
		getBillCardPanel().getHeadItem("vbillstatus_audit").setValue(WaHrpBillStatus.WRITE);
		getBillCardPanel().getHeadItem("pk_item12").setValue(WaHrpBillStatus.WRITE);
		getBillCardPanel().getHeadItem("pk_item14").setValue(WaHrpBillStatus.WRITE);
		
		try {
			getBillCardPanel().getHeadItem("pk_wa_class").setValue(pk_wa_class);

			//add by ylwb_zhenglei1


			list_power_item.clear();
			if(waPeriodVO!=null) {
				getBillCardPanel().getHeadItem("pk_wa_period").setValue(waPeriodVO.getPk_wa_period());
				getBillCardPanel().getHeadItem("vperiod").setValue(waPeriodVO.getVcalmonth());
				getBillCardPanel().getHeadItem("vyear").setValue(waPeriodVO.getCyear());

				//end
				IItemright right = NCLocator.getInstance().lookup(IItemright.class);
				ItemrightVO[] rightvos = right.queryItemrightByUserid(pk_wa_class, _getOperator(), waPeriodVO.getCyear(), waPeriodVO.getVcalmonth());
				
				if(rightvos!=null&&rightvos.length>0){
					for(ItemrightVO vo:rightvos){
						list_power_item.add(vo.getPk_wa_item());
					}
				}
			}
			
			//改造begin tianxfc
			//上期累计结余
			UFDouble lastTotalJY = new UFDouble(0);
			if(lastJYMap != null){
				lastTotalJY = lastJYMap.get(getPk_deptid()) != null? lastJYMap.get(getPk_deptid()) : new UFDouble(0);
			}
			//月考核奖pk
			String ykhpk_wa_classitem = null;
			//一次性奖金pk
			String ycxpk_wa_classitem = null;
			if(waClassItemVOMap != null && !waClassItemVOMap.isEmpty()){
				for(String key : waClassItemVOMap.keySet()){
					String vname = waClassItemVOMap.get(key);
					//月考核奖
					if(vname != null && "月考核奖".equals(vname)){
						ykhpk_wa_classitem = key;
					}
					//一次性奖金
					if(vname != null && "一次性奖".equals(vname)){
						ycxpk_wa_classitem = key;
					}
				}
			}
			
			//设置调入、调出月考核及一次性考核金额
			UFDouble vdef15 = new UFDouble(0);
			UFDouble vdef16 = new UFDouble(0);
			UFDouble vdef17 = new UFDouble(0);
			UFDouble vdef18 = new UFDouble(0);
			if(inoutMap != null){
				Map<String, Map<String, UFDouble>> zrMap = inoutMap.get("zrMap");
				Map<String, Map<String, UFDouble>> zcMap = inoutMap.get("zcMap");
				if(zrMap != null && zrMap.containsKey(getPk_deptid())){
					Map<String, UFDouble> map = zrMap.get(getPk_deptid());  //zrMap.get("10024Z1000000002M6TL");
					if(map != null && !map.isEmpty() && ykhpk_wa_classitem != null && ycxpk_wa_classitem != null){
						vdef15 = map.get(ykhpk_wa_classitem); //调入月考核奖金额："10024Z1000000002M6TL"
						if(vdef15 == null){
							vdef15 = new UFDouble(0);
						}
						vdef17 = map.get(ycxpk_wa_classitem); //调入一次性奖金："10024Z1000000002M6TL"
						if(vdef17 == null){
							vdef17 = new UFDouble(0);
						}
					}
				}
				if(zcMap != null && zcMap.containsKey(getPk_deptid())){
					Map<String, UFDouble> map = zcMap.get(getPk_deptid());  //zrMap.get("10024Z1000000002M6TL");
					if(map != null && !map.isEmpty() && ykhpk_wa_classitem != null && ycxpk_wa_classitem != null){
						vdef16 = map.get(ykhpk_wa_classitem); //调出月考核奖金额："10024Z1000000002M6TL"
						if(vdef16 == null){
							vdef16 = new UFDouble(0);
						}
						vdef18 = map.get(ycxpk_wa_classitem); //调出一次性奖金："10024Z1000000002M6TL"
						if(vdef18 == null){
							vdef18 = new UFDouble(0);
						}
					}
				}
			}
			
			
			//调入月考核奖金额
			getBillCardPanel().getHeadItem("vdef15").setValue(vdef15);
			//调出月考核奖金额
			getBillCardPanel().getHeadItem("vdef16").setValue(vdef16);
			//调入一次性奖金金额
			getBillCardPanel().getHeadItem("vdef17").setValue(vdef17);
			//调出一次性奖金金额
			getBillCardPanel().getHeadItem("vdef18").setValue(vdef18);
			
			//奖金科室分配：合计金额
			UFDouble totalmoney = new UFDouble(0);
			UFDouble nmnyTotal = new UFDouble(0);
			UFDouble nmny1Total = new UFDouble(0);
			UFDouble nmny2Total = new UFDouble(0);
			if(ksjjMap != null && !ksjjMap.isEmpty()){
				//奖金科室-合计金额
				Map<String, UFDouble> nmnyMap = ksjjMap.get("nmnyTotal");
				//奖金科室-月考核奖金额
				Map<String, UFDouble> nmny1Map = ksjjMap.get("nmny1Total");
				//奖金科室-一次性奖金金额
				Map<String, UFDouble> nmny2Map = ksjjMap.get("nmny2Total");
				
				if(nmnyMap != null && !nmnyMap.isEmpty()){
					//从绩效系统更新过来的科室的合计金额
					nmnyTotal = nmnyMap.get(getPk_deptid()) != null ? nmnyMap.get(getPk_deptid()) : new UFDouble(0);
					//从绩效系统更新过来的科室的月考核奖金额
					nmny1Total = nmny1Map.get(getPk_deptid()) != null ? nmny1Map.get(getPk_deptid()) : new UFDouble(0);
					//从绩效系统更新过来的科室的一次性奖金额
					nmny2Total = nmny2Map.get(getPk_deptid()) != null ? nmny2Map.get(getPk_deptid()) : new UFDouble(0);
					
					//本次可分配金额合计 = 科室合计金额（月考核奖+一次性奖金） + 调入月考核奖金额 + 调入一次性奖金金额 - 调出月考核奖金额 - 调出一次性奖金金额 + 上期结余
					totalmoney = nmnyTotal.add(vdef15).add(vdef17).sub(vdef16).sub(vdef18).add(lastTotalJY);
				}
			}
			//奖金科室分配：月考核奖
			//UFDouble nmny1 = nmny1Total.add(vdef15).sub(vdef16);
			UFDouble nmny1 = nmny1Total;
			//奖金科室分配：一次性奖金
			//UFDouble nmny2 = nmny2Total.add(vdef17).sub(vdef18);
			UFDouble nmny2 = nmny2Total;
			//月考核类
			getBillCardPanel().getHeadItem("vdef12").setValue(nmny1);
			//一次性奖金
			getBillCardPanel().getHeadItem("vdef13").setValue(nmny2);
			//累计结余
			getBillCardPanel().getHeadItem("vdef14").setValue(lastTotalJY);
			
			//本月期初(保存起来，用于后续的提交时校验结余)
			getBillCardPanel().getHeadItem("nbyqc").setValue(totalmoney);
			//本次可分配金额合计
			getBillCardPanel().getHeadItem("totalmoney").setValue(totalmoney);
			curDeptTotalMoney = totalmoney;
			//改造end tianxfc
			
		} catch (UifException e) {
			e.printStackTrace();
		}
	}
	public void countHeadMny(String deptid) throws Exception{
		DeptMnyHVO[] mnyvo = (DeptMnyHVO[])HYPubBO_Client.queryByCondition(DeptMnyHVO.class, " isnull(dr,0)=0 and pk_dept='"+deptid+"' and isnull(pk_billtype,'1')='1' ");
		UFDouble summny = new UFDouble(0);
		if(mnyvo!=null&&mnyvo.length>0){
			getBillCardPanel().getHeadItem("ndeptmny").setValue(mnyvo[0].getNmny());
			summny = mnyvo[0].getNmny();
		}else{
			getBillCardPanel().getHeadItem("ndeptmny").setValue(new UFDouble(0));
		}
		if(mnyvo==null||mnyvo.length<=0) return;
		Object vyear = getBillCardPanel().getHeadItem("vyear").getValueObject();
		Object vperiod = getBillCardPanel().getHeadItem("vperiod").getValueObject();
		DeptMnyBVO[] mnyvos = (DeptMnyBVO[])HYPubBO_Client.queryByCondition(DeptMnyBVO.class, 
				" pk_deptmny_h='"+mnyvo[0].getPrimaryKey()+"' and isnull(dr,0)=0 and pk_dept='"+pk_deptid+"' and vyear='"+vyear+"' and vperiod='"+vperiod+"' ");
		UFDouble ndeptchargemny = new UFDouble(0)	;//本月科室分配金额
		UFDouble ninmny = new UFDouble(0)	;//本月转入
		UFDouble noutmny = new UFDouble(0);//	本月转出
		UFDouble npaymny = new UFDouble(0) ;//	本月现金支出
		UFDouble nfpmny = new UFDouble(0) ;//	本月已分配
		if(mnyvos!=null&&mnyvos.length>0){
			for(DeptMnyBVO vo:mnyvos){
				if(vo.getCsourcebilltypecode()!=null&&vo.getCsourcebilltypecode().trim().equals("63RP")){
					nfpmny = nfpmny.add(vo.getNmny());
				}else if(vo.getCsourcebilltypecode()!=null&&vo.getCsourcebilltypecode().trim().equals("64RP")){
					ndeptchargemny = ndeptchargemny.add(vo.getNmny());
				}else if(vo.getCsourcebilltypecode()!=null&&vo.getCsourcebilltypecode().trim().equals("66RP")){
					npaymny = npaymny.add(vo.getNmny());
				}else if(vo.getCsourcebilltypecode()!=null&&vo.getCsourcebilltypecode().trim().equals("65RP")){
					if(vo.getIflag()==0){
						noutmny = noutmny.add(vo.getNmny());
					}else{
						ninmny = ninmny.add(vo.getNmny());
					}
				}
			}
		}
		getBillCardPanel().getHeadItem("ndeptchargemny").setValue(ndeptchargemny);
		getBillCardPanel().getHeadItem("ninmny").setValue(ninmny);
		getBillCardPanel().getHeadItem("nbyinmny").setValue(ninmny.add(ndeptchargemny));
		getBillCardPanel().getHeadItem("noutmny").setValue(noutmny);
		getBillCardPanel().getHeadItem("npaymny").setValue(npaymny);
		getBillCardPanel().getHeadItem("nbyqc").setValue(summny.add(noutmny).add(nfpmny).add(npaymny).sub(ninmny).sub(ndeptchargemny));

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
		UIRefPane pane2 = (UIRefPane)getBillCardPanel().getBillModel().getItemByKey("psncode").getComponent();
		pane2.setMultiSelectedEnabled(true);
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
		UIRefPane pane = ((UIRefPane)getBillCardPanel().getHeadItem("pk_dept").getComponent());
		pane.getRefModel().addWherePart(" and pk_perioddept in(" +
				" select pk_deptdoc from bd_wa_userdept where "+
				"   isnull(dr,0)=0 and pk_user='"+_getOperator()+"') "
		);
	}
	public String getNewBillNo() throws Exception {
		return getBillNo();
	}
	@Override
	protected String getBillNo() throws Exception {
		return super.getBillNo();
	}
	@Override
	protected void initPrivateButton() {
		super.initPrivateButton();
		ButtonVO importbtn = new ExcelImportBtnVO().getButtonVO();
		addPrivateButton(importbtn);
		importbtn.setOperateStatus(new int[]{IBillOperate.OP_ADD,IBillOperate.OP_EDIT});

		ButtonVO commitReturnBtn = new CommitReturnBtn().getButtonVO();
		addPrivateButton(commitReturnBtn);
		commitReturnBtn.setOperateStatus(new int[]{IBillOperate.OP_NOTEDIT});

		ButtonVO filebtn = new FileManageBtnVO().getButtonVO();
		addPrivateButton(filebtn);

//		ButtonVO importparentbtn = new ExcelParentImportButVo().getButtonVO();
//		importparentbtn.setChildAry(new int[]{IHRPBtn.FileManage,IHRPBtn.ExcelImport});
//		addPrivateButton(importparentbtn);

		//qiutian   2012-05-29 增加按钮  
		ButtonVO dataUpdateVO = new DataUpdateBtnVO().getButtonVO();
		addPrivateButton(dataUpdateVO);


		ButtonVO querydept = new QueryDeptButtonVO().getButtonVO();
		addPrivateButton(querydept);

		ButtonVO querydept_sum = new QueryDeptButtonSUMVO().getButtonVO();
		addPrivateButton(querydept_sum);
	}
	/**
	 * @author 宋旨昊
	 * 2011-3-21下午01:30:53
	 * 说明：初始化部门薪酬项目信息
	 */
	private void initDeptItem(){

//		UpdateBB bb = new UpdateBB();
//		try {
//			bb.updateBB();
//		} catch (Exception e1) {
//			e1.printStackTrace();
//		}
//		ImportExcelData excel = new ImportExcelData();
//		try {
//		Object[][] values = excel.executeImport();
//		PsndocVO[] docvos = (PsndocVO[])HYPubBO_Client.queryByCondition(PsndocVO.class, " isnull(dr,0)=0 and pk_corp='"+_getCorp().getPrimaryKey()+"' ");
//		HashMap<String,PsndocVO> map = new HashMap<String, PsndocVO>();
//		for(PsndocVO vo:docvos){
//		map.put(vo.getPsncode(),vo);
//		}
//		ArrayList<PsnQkVO> list = new ArrayList<PsnQkVO>();
//		for(int i=1;i<values.length;i++){
//		if(values[i][3]!=null&&values[i][2]!=null){
//		PsndocVO vo = map.get(values[i][3].toString().trim());
//		if(vo!=null){
//		PsnQkVO pvo = new PsnQkVO();
//		pvo.setDr(0);
//		pvo.setPk_corp(_getCorp().getPrimaryKey());
//		pvo.setPk_psn(vo.getPrimaryKey());
//		pvo.setNmny(new UFDouble(0).sub(new UFDouble(values[i][2].toString())));
//		list.add(pvo);
//		}
//		}
//		}
//		HYPubBO_Client.insertAry(list.toArray(new PsnQkVO[0]));
//		} catch (BusinessException e) {
//		e.printStackTrace();
//		}

		try {
			String userid = _getOperator();
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
			//2012.02.23 根据sp1 需求更改此处代码逻辑
//			UserAndClerkVO[] uservo = (UserAndClerkVO[]) HYPubBO_Client.queryByCondition(UserAndClerkVO.class, " isnull(dr,0)=0 and userid='"+userid+"' ");
//			if(uservo!=null&&uservo.length>0){
//			String pk_psn = uservo[0].getPk_psndoc();
//			PsndocVO[] docvos = (PsndocVO[])HYPubBO_Client.queryByCondition(PsndocVO.class, " pk_corp='"+_getCorp().getPrimaryKey()+"' and isnull(dr,0)=0 and pk_psnbasdoc='"+pk_psn+"' ");
//			if(docvos!=null&&docvos.length>0){
//			PerioddeptVO[] vos = (PerioddeptVO[]) HYPubBO_Client.queryByCondition(PerioddeptVO.class, 
//			" pk_corp='"+_getCorp().getPrimaryKey()+"' and isnull(dr,0)=0 and pk_psndoc='"+docvos[0].getPrimaryKey()+"' ");
//			if(vos!=null&&vos.length>0){
//			pk_deptid = vos[0].getPrimaryKey();
//			}
//			}
//			}
			UserDeptVO[] deptvos = (UserDeptVO[])HYPubBO_Client.queryByCondition(UserDeptVO.class, " isnull(dr,0)=0 and pk_user='"+_getOperator()+"' and pk_deptdoc in (select pk_perioddept from pf_perioddept where isnull(dr,0)=0 and isnull(vdef5,'N')='Y') ");
			if(deptvos!=null&&deptvos.length>0){
				pk_deptid = deptvos[0].getPk_deptdoc();
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
				String sql="select * from wa_period where isnull(dr,0)=0 and cperiod='"+period+"' and cyear='"+year+"'";
				waPeriodVO = (PERIODVO) service.executeQuery(sql, new BeanProcessor(PERIODVO.class)); 

				String curdate = _getDate().toString().substring(0,7);
				if(waPeriodVO!=null){
					if((waPeriodVO.getCyear()+"-"+waPeriodVO.getVcalmonth()).equals(curdate)){
						iscan = true;
					}
				}
			}catch(Exception e){
				waPeriodVO = null;
				iscan = false;
			}
			
			if(!iscan){
				if(!iscan){
					MessageDialog.showHintDlg(this, "提示", "登陆期间不是最小未结账月，不能进行业务操作，只有查询权限");
				}
				getButtonManager().getButton(IBillButton.Add).setEnabled(false);
				//getButtonManager().getButton(IBillButton.Copy).setEnabled(false);
				getButtonManager().getButton(IBillButton.Edit).setEnabled(false);
				getButtonManager().getButton(IBillButton.Delete).setEnabled(false);
			}
			if(waPeriodVO==null){
				MessageDialog.showHintDlg(this, "提示", "没有奖金类别权限，请联系管理员");
			}
			
			//改造begin tianxfc
			String pk_corp = _getCorp().getPrimaryKey();
			//根据期间年月、组织pk、单据状态查询奖金科室奖金分配数据字典 key：奖金科室编码， value：奖金分配数据vo
			IHRWaServices service =  NCLocator.getInstance().lookup(IHRWaServices.class);
			if(inoutMap != null){
				inoutMap.clear();
			}
			//查询科室对应的：本次可分配金额合计、月考核类、一次性奖金、调入月考核奖金、调出月考核奖金、调入一次性奖金金额、调出一次性奖金金额、累计结余
			inoutMap = service.queryMnyMapByCond(pk_corp, null, waPeriodVO.getCyear(), waPeriodVO.getVcalmonth());
			if(inoutMap != null && !inoutMap.isEmpty()){
				ksjjMap = inoutMap.get("ksjjMap");
			}
			if(lastJYMap != null){
				lastJYMap.clear();
			}
			lastJYMap = service.queryLastTotalJYByCond(pk_corp, waPeriodVO.getCyear(), waPeriodVO.getVcalmonth());
			
			//根据默认薪酬类别、期间、用户id，获取【薪酬类别】参照项数据字典：key：pk_wa_classitem（参照项主键）, value：vname（参照项名称）
			if(waClassItemVOMap == null && waPeriodVO != null){
				waClassItemVOMap = service.qryWAClassitemMap(pk_wa_class, waPeriodVO.getCyear(), waPeriodVO.getCperiod(), _getOperator());
			}
			
			//改造end tianxfc
			
			if(pk_deptid==null){
				//MessageDialog.showHintDlg(this, "提示", "当前用户未设置业务员信息，不能进行增/删/改等操作");
				getButtonManager().getButton(IBillButton.Add).setEnabled(false);
				getButtonManager().getButton(IBillButton.Edit).setEnabled(false);
				//getButtonManager().getButton(IBillButton.Copy).setEnabled(false);
				getButtonManager().getButton(IBillButton.Delete).setEnabled(false);
			}else{
				giveDataToitemvos(pk_deptid,pk_wa_class);
			}
			giveDataToMapitem();
//			giveDataToitemvos(pk_deptid,pk_wa_class);
		} catch (BusinessException e) {
			e.printStackTrace();
			getButtonManager().getButton(IBillButton.Add).setEnabled(false);
			getButtonManager().getButton(IBillButton.Edit).setEnabled(false);
			//getButtonManager().getButton(IBillButton.Copy).setEnabled(false);
			getButtonManager().getButton(IBillButton.Delete).setEnabled(false);
			if(!iscan){
				MessageDialog.showHintDlg(this, "提示", "登陆期间不是最小未结账月，不能进行业务操作，只有查询权限");
			}
		}
		//行操作按钮只留增行删行，其他都隐藏掉。
		getButtonManager().getButton(IBillButton.InsLine).setName("批量维护");
		getButtonManager().getButton(IBillButton.CopyLine).setVisible(false);
		getButtonManager().getButton(IBillButton.PasteLine).setVisible(false);
		getButtonManager().getButton(IBillButton.PasteLinetoTail).setVisible(false);
		updateButtons();
		getBillListPanel().getHeadItem("bisselect").setEnabled(true);
		getBillListPanel().getBodyBillModel("wa_psn_item_b").getItemByKey("bisselect").setEnabled(true);
		getBillCardPanel().getBillModel("wa_psn_item_b").getItemByKey("bisselect").setEnabled(true);

	}
	private void giveDataToMapitem() throws BusinessException {
		IItem item = (IItem)NCLocator.getInstance().lookup(IItem.class);
		ItemVO[] itemvos = item.queryAllItem(_getCorp().getPrimaryKey());
		IClassitem classitem = (IClassitem)NCLocator.getInstance().lookup(IClassitem.class);
		ClassitemVO[] vos = classitem.queryAllClassItem(pk_wa_class,waPeriodVO.getVcalyear(),waPeriodVO.getVcalmonth());
		if(vos!=null&&vos.length>0){
			for(ClassitemVO itemvo:vos){
				mapclassitem.put(itemvo.getPk_wa_item(), itemvo);
			}
		}
		if(itemvos!=null&&itemvos.length>0){
			for(ItemVO itemvo:itemvos){
				mapitem.put(itemvo.getPrimaryKey(), itemvo);
			}
		}
	}
	private void giveDataToitemvos(String pk_waclass) throws UifException {
		itemvos = (ItemSetBVO[])HYPubBO_Client.queryByCondition(ItemSetBVO.class,
				" isnull(dr,0)=0 and pk_waclass='"+pk_waclass+"' and isnull(bisclose,'N')='N' ");
	}

	//增加薪资类别的查询  qiutian  2012-05-31
	private void giveDataToitemvos(String pk_deptid,String pk_wa_class) throws BusinessException {

		String sql = " isnull(dr,0)=0  and pk_waclass='"+pk_wa_class+"' and isnull(bisclose,'N')='N' " +
		"and pk_item in(select pk_classitem from wa_dept_classitem where pk_dept='"+pk_deptid+"' and isnull(dr,0)=0  and pk_wa_class='"+pk_wa_class+"' and isnull(bisclose,'N')='N') order by iorder ";

		itemvos = (ItemSetBVO[])HYPubBO_Client.queryByCondition(ItemSetBVO.class,sql);

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

	public ItemSetBVO[] getItemvos() {
		return itemvos;
	}

	public void setItemvos(ItemSetBVO[] itemvos) {
		this.itemvos = itemvos;
	}

	public String getPk_deptid() {
		return pk_deptid;
	}

	public void setPk_deptid(String pk_deptid) {
		this.pk_deptid = pk_deptid;
	}
	public void countHeadMny2(){
		int rowcount = getBillCardPanel().getBillTable().getRowCount();
		UFDouble totalMoney = new UFDouble(0);  //申请金额
		//现有结存:ndeptmny
		//UFDouble ndeptmny = getBillCardPanel().getHeadItem("ndeptmny").getValueObject()!=null?new UFDouble(getBillCardPanel().getHeadItem("ndeptmny").getValueObject().toString()):new UFDouble(0);
		for(int i=0;i<rowcount;i++){
			Object obj = getBillCardPanel().getBodyValueAt(i,"nmny");
			totalMoney = obj!=null?totalMoney.add(new UFDouble(obj.toString(),2)):totalMoney;
			//getBillCardPanel().getHeadItem("totalmoney").setValue(totalMoney);
			//税后合计: totalaftersmny
			//getBillCardPanel().getHeadItem("totalaftersmny").setValue(totalMoney);
			//ndeptmny_after分配后结余:ndeptmny_after
			//getBillCardPanel().getHeadItem("ndeptmny_after").setValue(ndeptmny.sub(totalMoney));
			//现有结存:ndeptmny
			//Object value = getBillCardPanel().getHeadItem("ndeptmny").getValueObject();
			//if(value!=null&&new UFDouble(value.toString()).doubleValue()!=0){
				//发放比例（%）: nbl
				//getBillCardPanel().getHeadItem("nbl").setValue(totalMoney.div(new UFDouble(value.toString())).multiply(100));
			//}
		}
		UFDouble nbyqc = null;
		//本月期初(保存起来，用于后续的提交时校验结余)
		if(getBillCardPanel().getHeadItem("nbyqc").getValueObject() != null){
			nbyqc = new UFDouble(getBillCardPanel().getHeadItem("nbyqc").getValueObject().toString());
		}else{
			nbyqc = new UFDouble(0);
		}
		totalMoney = nbyqc.sub(totalMoney);
		//本次可分配金额合计
		//getBillCardPanel().getHeadItem("totalmoney").setValue(nbyqc);
		//分配后结余
		getBillCardPanel().getHeadItem("ndeptmny_after").setValue(totalMoney);
		
	}
	
	private OutStaffDLG m_psnOutDlg = null;
	private OutStaffDLG getOutStaffDlg(String pk_dept,String psnname){
		m_psnOutDlg = new OutStaffDLG(this,pk_dept,psnname);
		return m_psnOutDlg;
	}
	@Override
	public void afterEdit(BillEditEvent e) {
		if(e.getKey().equals("psnname")||e.getKey().equals("psncode")){
			// 根据名称查询编外人员，没有查到则弹框填信息保存
			
			if(e.getKey().equals("psnname")){
				Object psnname = getBillCardPanel().getBodyValueAt(e.getRow(), "psnname");
				try {
					PsnOutStaffVO pnsoutstaffvo = getPsnOutStaffVO(""+psnname);
					if(pnsoutstaffvo == null){
						OutStaffDLG dlg =  getOutStaffDlg(getPk_deptid(),""+psnname);
						if(dlg.showModal() == UIDialog.ID_OK){
							PsnOutStaffVO outstaffvo = dlg.getPsnoutstaffvo();
							getBillCardPanel().setBodyValueAt(outstaffvo.getPk_outstaff(), e.getRow(), "pk_psndoc");
							getBillCardPanel().setBodyValueAt(outstaffvo.getPsncode(), e.getRow(), "psncode");
						}
					}else{
						getBillCardPanel().setBodyValueAt(pnsoutstaffvo.getPk_outstaff(), e.getRow(), "pk_psndoc");
						getBillCardPanel().setBodyValueAt(pnsoutstaffvo.getPsncode(), e.getRow(), "psncode");

					}
					
				} catch (BusinessException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			
			
			
			UIRefPane pane = (UIRefPane)getBillCardPanel().getBillModel().getItemByKey(e.getKey()).getComponent();
			String[] pks = pane.getRefPKs();
			int row = e.getRow();
			if(pks!=null&&pks.length>1){
				for(int i=0;i<pks.length-1;i++){
					getBillCardPanel().insertLine();
					getBillCardPanel().getBillModel().setRowState(row+i, BillModel.ADD);
				}
				Object value = getBillCardPanel().getBodyValueAt(row+pks.length-1, "pk_psn_item_b");
				Object pk_wa_dept = getBillCardPanel().getBodyValueAt(row+pks.length-1, "pk_wa_dept");
				if(value!=null&&value.toString().trim().length()>0){
					getBillCardPanel().getBillModel().setRowState(row+pks.length-1, BillModel.MODIFICATION);
				}else{
					getBillCardPanel().getBillModel().setRowState(row+pks.length-1, BillModel.ADD);
				}
				for(int i=0;i<pks.length;i++){
					getBillCardPanel().setBodyValueAt(pks[i], row+i, "pk_psndoc");
					getBillCardPanel().setBodyValueAt(pk_deptid, row+i, "pk_dept");
					//getBillCardPanel().setBodyValueAt(pk_wa_dept, row+i, "pk_wa_dept");
					getBillCardPanel().setBodyValueAt(_getCorp().getPrimaryKey(), row+i, "pk_corp");
					getBillCardPanel().getBillModel().execEditFormulaByKey(row+i, "psncode");
					if(this.pk_deptdoc!=null){
						getBillCardPanel().setBodyValueAt(pk_deptid, row+i, "pk_wa_dept");
						getBillCardPanel().getBillModel().execEditFormulaByKey(row+i, "pk_deptdoc");
					}
				}
			}
			this.pk_deptdoc= null;
			
			if(e.getKey().equals("psncode")){
				String pk_psndoc = (String) getBillCardPanel().getBillModel().getValueAt(e.getRow(),"pk_psndoc");
				String sql="select * from trtam_deptdoc_kq where pk_dept in (select pk_dept from trtam_deptdoc_kq_b where pk_psndoc='"+pk_psndoc+"' and bisnew='Y' and dr=0)";
				try {
					DeptKqVO deptkqvo = (DeptKqVO) service.executeQuery(sql, new BeanProcessor(DeptKqVO.class));
					if(deptkqvo != null){
						getBillCardPanel().setBodyValueAt(deptkqvo.getPk_hrp_dept(), e.getRow(), "vdef18");
						getBillCardPanel().setBodyValueAt(deptkqvo.getVcode(), e.getRow(), "vdef1");
						getBillCardPanel().setBodyValueAt(deptkqvo.getVname(), e.getRow(), "vdef16");
					}
					
				} catch (BusinessException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} 
			}
		}else if(e.getKey().equals("pk_dept")){
			String pk_dept=((UIRefPane)e.getSource()).getRefPK()+"";
			pk_deptid=pk_dept;
			pk_wa_class = (String)getBillCardPanel().getHeadItem("pk_wa_class").getValueObject();
			try {
				list_power_item.clear();
				if(waPeriodVO!=null) {
					getBillCardPanel().getHeadItem("pk_wa_period").setValue(waPeriodVO.getPk_wa_period());
					getBillCardPanel().getHeadItem("vperiod").setValue(waPeriodVO.getVcalmonth());
					getBillCardPanel().getHeadItem("vyear").setValue(waPeriodVO.getCyear());

					//end
					IItemright right = NCLocator.getInstance().lookup(IItemright.class);
					ItemrightVO[] rightvos = right.queryItemrightByUserid(pk_wa_class, _getOperator(), waPeriodVO.getCyear(), waPeriodVO.getVcalmonth());
					
					if(rightvos!=null&&rightvos.length>0){
						for(ItemrightVO vo:rightvos){
							list_power_item.add(vo.getPk_wa_item());
						}
					}
				}
				giveDataToitemvos(pk_deptid,pk_wa_class);
				giveDataToMapitem();
				((ClientEventHandler)getManageEventHandler()).plottable();
				rePayValueToHeadItem();
				((ClientEventHandler)getManageEventHandler()).onboLineDelAry();
				this.pk_deptdoc= null;

//				countHeadMny(pk_deptid);
//				setDeptSum(pk_deptid);
				
				
				//改造begin tianxfc
				//上期累计结余
				UFDouble lastTotalJY = new UFDouble(0);
				if(lastJYMap != null){
					lastTotalJY = lastJYMap.get(getPk_deptid()) != null? lastJYMap.get(getPk_deptid()) : new UFDouble(0);
				}
				
				//月考核奖pk
				String ykhpk_wa_classitem = null;
				//一次性奖金pk
				String ycxpk_wa_classitem = null;
				if(waClassItemVOMap != null && !waClassItemVOMap.isEmpty()){
					for(String key : waClassItemVOMap.keySet()){
						String vname = waClassItemVOMap.get(key);
						//月考核奖
						if(vname != null && "月考核奖".equals(vname)){
							ykhpk_wa_classitem = key;
						}
						//一次性奖金
						if(vname != null && "一次性奖".equals(vname)){
							ycxpk_wa_classitem = key;
						}
					}
				}
				
				
				//设置调入、调出月考核及一次性考核金额
				UFDouble vdef15 = new UFDouble(0);
				UFDouble vdef16 = new UFDouble(0);
				UFDouble vdef17 = new UFDouble(0);
				UFDouble vdef18 = new UFDouble(0);
				if(inoutMap != null){
					Map<String, Map<String, UFDouble>> zrMap = inoutMap.get("zrMap");
					Map<String, Map<String, UFDouble>> zcMap = inoutMap.get("zcMap");
					if(zrMap != null && zrMap.containsKey(getPk_deptid())){
						Map<String, UFDouble> map = zrMap.get(getPk_deptid());  //zrMap.get("10024Z1000000002M6TL");
						if(map != null && !map.isEmpty() && ykhpk_wa_classitem != null && ycxpk_wa_classitem != null){
							vdef15 = map.get(ykhpk_wa_classitem); //调入月考核奖金额："10024Z1000000002M6TL"
							if(vdef15 == null){
								vdef15 = new UFDouble(0);
							}
							vdef17 = map.get(ycxpk_wa_classitem); //调入一次性奖金："10024Z1000000002M6TL"
							if(vdef17 == null){
								vdef17 = new UFDouble(0);
							}
						}
					}
					if(zcMap != null && zcMap.containsKey(getPk_deptid())){
						Map<String, UFDouble> map = zcMap.get(getPk_deptid());  //zrMap.get("10024Z1000000002M6TL");
						if(map != null && !map.isEmpty() && ykhpk_wa_classitem != null && ycxpk_wa_classitem != null){
							vdef16 = map.get(ykhpk_wa_classitem); //调出月考核奖金额："10024Z1000000002M6TL"
							if(vdef16 == null){
								vdef16 = new UFDouble(0);
							}
							vdef18 = map.get(ycxpk_wa_classitem); //调出一次性奖金："10024Z1000000002M6TL"
							if(vdef18 == null){
								vdef18 = new UFDouble(0);
							}
						}
					}
				}
				
				
				//调入月考核奖金额
				getBillCardPanel().getHeadItem("vdef15").setValue(vdef15);
				//调出月考核奖金额
				getBillCardPanel().getHeadItem("vdef16").setValue(vdef16);
				//调入一次性奖金金额
				getBillCardPanel().getHeadItem("vdef17").setValue(vdef17);
				//调出一次性奖金金额
				getBillCardPanel().getHeadItem("vdef18").setValue(vdef18);
				
				//奖金科室分配：合计金额
				UFDouble totalmoney = new UFDouble(0);
				UFDouble nmnyTotal = new UFDouble(0);
				UFDouble nmny1Total = new UFDouble(0);
				UFDouble nmny2Total = new UFDouble(0);
				if(ksjjMap != null && !ksjjMap.isEmpty()){
					//奖金科室-合计金额
					Map<String, UFDouble> nmnyMap = ksjjMap.get("nmnyTotal");
					//奖金科室-月考核奖金额
					Map<String, UFDouble> nmny1Map = ksjjMap.get("nmny1Total");
					//奖金科室-一次性奖金金额
					Map<String, UFDouble> nmny2Map = ksjjMap.get("nmny2Total");
					
					if(nmnyMap != null && !nmnyMap.isEmpty()){
						//从绩效系统更新过来的科室的合计金额
						nmnyTotal = nmnyMap.get(getPk_deptid()) != null ? nmnyMap.get(getPk_deptid()) : new UFDouble(0);
						//从绩效系统更新过来的科室的月考核奖金额
						nmny1Total = nmny1Map.get(getPk_deptid()) != null ? nmny1Map.get(getPk_deptid()) : new UFDouble(0);
						//从绩效系统更新过来的科室的一次性奖金额
						nmny2Total = nmny2Map.get(getPk_deptid()) != null ? nmny2Map.get(getPk_deptid()) : new UFDouble(0);
						
						//本次可分配金额合计 = 科室合计金额（月考核奖+一次性奖金） + 调入月考核奖金额 + 调入一次性奖金金额 - 调出月考核奖金额 - 调出一次性奖金金额 + 上期结余
						totalmoney = nmnyTotal.add(vdef15).add(vdef17).sub(vdef16).sub(vdef18).add(lastTotalJY);
					}
				}
				//奖金科室分配：月考核奖
				//UFDouble nmny1 = nmny1Total.add(vdef15).sub(vdef16);
				UFDouble nmny1 = nmny1Total;
				//奖金科室分配：一次性奖金
				//UFDouble nmny2 = nmny2Total.add(vdef17).sub(vdef18);
				UFDouble nmny2 = nmny2Total;
				//月考核类
				getBillCardPanel().getHeadItem("vdef12").setValue(nmny1);
				//一次性奖金
				getBillCardPanel().getHeadItem("vdef13").setValue(nmny2);
				//累计结余
				getBillCardPanel().getHeadItem("vdef14").setValue(lastTotalJY);
				
				//本月期初(保存起来，用于后续的提交时校验结余)
				getBillCardPanel().getHeadItem("nbyqc").setValue(totalmoney);
				//本次可分配金额合计
				getBillCardPanel().getHeadItem("totalmoney").setValue(totalmoney);
				//改造end tianxfc
				
				((ClientEventHandler)getManageEventHandler()).addPsn();
			} catch (Exception e1) {
				e1.printStackTrace();
			}  
		}else if(e.getKey().equals("pk_wa_class")){
			String pk_wa_class=((UIRefPane)e.getSource()).getRefPK()+"";
			this.pk_wa_class=pk_wa_class;

			try {
				giveDataToitemvos(pk_deptid,pk_wa_class);
				giveDataToMapitem();
				((ClientEventHandler)getManageEventHandler()).plottable();
				rePayValueToHeadItem();

				((ClientEventHandler)getManageEventHandler()).onboLineDelAry();
				this.pk_deptdoc= null;

				//add by ylwb_zhenglei1
				WaclassHeaderVO[] m_waclasss = WADelegator.getWaClass().queryAllHeader(Global.getCorpPK(), Global.getUserID());

				String period=null;
				String year=null;

				for(WaclassHeaderVO wa_class:m_waclasss) {
					if(wa_class.getPk_wa_class().equals(pk_wa_class)) {
						String strperiod = WADelegator.getWaPeriod().getYWPeriod(wa_class.getPk_wa_class(), false);
						//period=wa_class.getCperiod();
						period=strperiod.substring(4);
						year=wa_class.getCyear();
					}
				}
				String sql="select * from wa_period where isnull(dr,0)=0 and cperiod='"+period+"' and cyear='"+year+"'";
				PERIODVO waPeriodVO = (PERIODVO) service.executeQuery(sql, new BeanProcessor(PERIODVO.class)); 
				if(waPeriodVO!=null) {
					getBillCardPanel().getHeadItem("pk_wa_period").setValue(waPeriodVO.getPk_wa_period());
				} else {
					getBillCardPanel().getHeadItem("pk_wa_period").setValue(null);
				}
				//end
			} catch (Exception e1) {
				e1.printStackTrace();
			}

		}else if(e.getKey().startsWith("nmny")){
			UFDouble nmny = new UFDouble(0);
			UFDouble nmny2 = new UFDouble(0);
			for(int i=0;i<MAX_INDEX;i++){
				if(9 < i && i < 15){ // pk_item10 ~ pk_item15 被一级、一级半、二级审核人、审核状态占用
					continue;
				}
				Object value = getBillCardPanel().getBillModel().getValueAt(e.getRow(),"nmny"+(i+1)+"");
				String pk_item = (String)getBillCardPanel().getHeadItem("pk_item"+(i+1)+"").getValueObject();
				ClassitemVO itemvo = mapclassitem.get(pk_item);
				ItemVO iitemvo = mapitem.get(pk_item);
				if(itemvo!=null&&itemvo.getItaxflag()!=null&&itemvo.getItaxflag()==1){
					if(iitemvo!=null&&iitemvo.getIproperty()!=null&&iitemvo.getIproperty()==1){
						nmny2 = nmny2.sub(value!=null?new UFDouble(value.toString()):new UFDouble(0));
					}else{
						nmny2 = nmny2.add(value!=null?new UFDouble(value.toString()):new UFDouble(0));
					}
				}
				if(iitemvo!=null&&iitemvo.getIproperty()!=null&&iitemvo.getIproperty()==1){
					nmny = nmny.sub(value!=null?new UFDouble(value.toString()):new UFDouble(0));
				}else{
					nmny = nmny.add(value!=null?new UFDouble(value.toString()):new UFDouble(0));
				}
			}
			//小计金额: nmny
			getBillCardPanel().getBillModel("wa_psn_item_b").setValueAt(nmny, e.getRow(), "nmny");
			//税后金额: naftersmny
			//getBillCardPanel().getBillModel("wa_psn_item_b").setValueAt(nmny, e.getRow(), "naftersmny");
			//本次扣税基数: nbcnsmny
			//getBillCardPanel().getBillModel("wa_psn_item_b").setValueAt(nmny2, e.getRow(), "nbcnsmny");
			countHeadMny2();
			
			
//			int rowcount = getBillCardPanel().getBillTable("wa_psn_item_b").getRowCount();
//			for(int i=0;i<rowcount;i++){
//			String[] keys = new String[]{"nsmny","nbcnsmny","ctaxtableid","noldsmny","noldnsmny"};
//			for(String key:keys){
//			getBillCardPanel().getBillModel("wa_psn_item_b").setValueAt(null, i,key);
//			}
//			}

		}else if(e.getKey().equals("pk_wa_period")){
			
		}else if(e.getKey().equals("stafftype")){
//			getBillCardPanel().getBodyItem("wa_psn_item_b", "psnname").setEnabled(true);
			String value = (String) getBillCardPanel().getBillModel().getValueAt(e.getRow(),"stafftype");
			if("系统内".equals(value)){
				getBillCardPanel().getBillModel().setCellEditable(e.getRow(), "psncode", true);
				getBillCardPanel().getBillModel().setCellEditable(e.getRow(), "psnname", false);
			}else{
				getBillCardPanel().getBillModel().setCellEditable(e.getRow(), "psnname", true);
				getBillCardPanel().getBillModel().setCellEditable(e.getRow(), "psncode", false);
			}
			
			
			
			
//			getBillCardPanel().getBodyItem("wa_psn_item_b", "psncode").setEnabled(false);
		}else{
			super.afterEdit(e);
			this.pk_deptdoc= null;
		}
	}
	
	public PsnOutStaffVO getPsnOutStaffVO(String psnname) throws BusinessException{
	    IUAPQueryBS service = NCLocator.getInstance().lookup(IUAPQueryBS.class);
		String sql="select * from wa_psn_outstaff where isnull(dr,0)=0 and psnname='"+psnname+"'";
		PsnOutStaffVO outstaffVO = (PsnOutStaffVO) service.executeQuery(sql, new BeanProcessor(PsnOutStaffVO.class)); 
	    return outstaffVO;
	}
	
	public void setDeptEdit(){

		String pk_dept=(String)getBillCardPanel().getHeadItem("pk_dept").getValueObject();;
		pk_deptid=pk_dept;
		pk_wa_class = (String)getBillCardPanel().getHeadItem("pk_wa_class").getValueObject();
		try {
			list_power_item.clear();
			if(waPeriodVO!=null) {
				getBillCardPanel().getHeadItem("pk_wa_period").setValue(waPeriodVO.getPk_wa_period());
				getBillCardPanel().getHeadItem("vperiod").setValue(waPeriodVO.getVcalmonth());
				getBillCardPanel().getHeadItem("vyear").setValue(waPeriodVO.getCyear());

				//end
				IItemright right = NCLocator.getInstance().lookup(IItemright.class);
				ItemrightVO[] rightvos = right.queryItemrightByUserid(pk_wa_class, _getOperator(), waPeriodVO.getCyear(), waPeriodVO.getVcalmonth());
				
				if(rightvos!=null&&rightvos.length>0){
					for(ItemrightVO vo:rightvos){
						list_power_item.add(vo.getPk_wa_item());
					}
				}
			}
			giveDataToitemvos(pk_deptid,pk_wa_class);
			giveDataToMapitem();
			((ClientEventHandler)getManageEventHandler()).plottable();
			rePayValueToHeadItem();
		//	((ClientEventHandler)getManageEventHandler()).onboLineDelAry();
			this.pk_deptdoc= null;

//			countHeadMny(pk_deptid);
//			setDeptSum(pk_deptid);
			
			
			//改造begin tianxfc
			//上期累计结余
			UFDouble lastTotalJY = new UFDouble(0);
			if(lastJYMap != null){
				lastTotalJY = lastJYMap.get(getPk_deptid()) != null? lastJYMap.get(getPk_deptid()) : new UFDouble(0);
			}
			
			//月考核奖pk
			String ykhpk_wa_classitem = null;
			//一次性奖金pk
			String ycxpk_wa_classitem = null;
			if(waClassItemVOMap != null && !waClassItemVOMap.isEmpty()){
				for(String key : waClassItemVOMap.keySet()){
					String vname = waClassItemVOMap.get(key);
					//月考核奖
					if(vname != null && "月考核奖".equals(vname)){
						ykhpk_wa_classitem = key;
					}
					//一次性奖金
					if(vname != null && "一次性奖".equals(vname)){
						ycxpk_wa_classitem = key;
					}
				}
			}
			
			
			//设置调入、调出月考核及一次性考核金额
			UFDouble vdef15 = new UFDouble(0);
			UFDouble vdef16 = new UFDouble(0);
			UFDouble vdef17 = new UFDouble(0);
			UFDouble vdef18 = new UFDouble(0);
			if(inoutMap != null){
				Map<String, Map<String, UFDouble>> zrMap = inoutMap.get("zrMap");
				Map<String, Map<String, UFDouble>> zcMap = inoutMap.get("zcMap");
				if(zrMap != null && zrMap.containsKey(getPk_deptid())){
					Map<String, UFDouble> map = zrMap.get(getPk_deptid());  //zrMap.get("10024Z1000000002M6TL");
					if(map != null && !map.isEmpty() && ykhpk_wa_classitem != null && ycxpk_wa_classitem != null){
						vdef15 = map.get(ykhpk_wa_classitem); //调入月考核奖金额："10024Z1000000002M6TL"
						if(vdef15 == null){
							vdef15 = new UFDouble(0);
						}
						vdef17 = map.get(ycxpk_wa_classitem); //调入一次性奖金："10024Z1000000002M6TL"
						if(vdef17 == null){
							vdef17 = new UFDouble(0);
						}
					}
				}
				if(zcMap != null && zcMap.containsKey(getPk_deptid())){
					Map<String, UFDouble> map = zcMap.get(getPk_deptid());  //zrMap.get("10024Z1000000002M6TL");
					if(map != null && !map.isEmpty() && ykhpk_wa_classitem != null && ycxpk_wa_classitem != null){
						vdef16 = map.get(ykhpk_wa_classitem); //调出月考核奖金额："10024Z1000000002M6TL"
						if(vdef16 == null){
							vdef16 = new UFDouble(0);
						}
						vdef18 = map.get(ycxpk_wa_classitem); //调出一次性奖金："10024Z1000000002M6TL"
						if(vdef18 == null){
							vdef18 = new UFDouble(0);
						}
					}
				}
			}
			
			
			//调入月考核奖金额
			getBillCardPanel().getHeadItem("vdef15").setValue(vdef15);
			//调出月考核奖金额
			getBillCardPanel().getHeadItem("vdef16").setValue(vdef16);
			//调入一次性奖金金额
			getBillCardPanel().getHeadItem("vdef17").setValue(vdef17);
			//调出一次性奖金金额
			getBillCardPanel().getHeadItem("vdef18").setValue(vdef18);
			
			//奖金科室分配：合计金额
			UFDouble totalmoney = new UFDouble(0);
			UFDouble nmnyTotal = new UFDouble(0);
			UFDouble nmny1Total = new UFDouble(0);
			UFDouble nmny2Total = new UFDouble(0);
			if(ksjjMap != null && !ksjjMap.isEmpty()){
				//奖金科室-合计金额
				Map<String, UFDouble> nmnyMap = ksjjMap.get("nmnyTotal");
				//奖金科室-月考核奖金额
				Map<String, UFDouble> nmny1Map = ksjjMap.get("nmny1Total");
				//奖金科室-一次性奖金金额
				Map<String, UFDouble> nmny2Map = ksjjMap.get("nmny2Total");
				
				if(nmnyMap != null && !nmnyMap.isEmpty()){
					//从绩效系统更新过来的科室的合计金额
					nmnyTotal = nmnyMap.get(getPk_deptid()) != null ? nmnyMap.get(getPk_deptid()) : new UFDouble(0);
					//从绩效系统更新过来的科室的月考核奖金额
					nmny1Total = nmny1Map.get(getPk_deptid()) != null ? nmny1Map.get(getPk_deptid()) : new UFDouble(0);
					//从绩效系统更新过来的科室的一次性奖金额
					nmny2Total = nmny2Map.get(getPk_deptid()) != null ? nmny2Map.get(getPk_deptid()) : new UFDouble(0);
					
					//本次可分配金额合计 = 科室合计金额（月考核奖+一次性奖金） + 调入月考核奖金额 + 调入一次性奖金金额 - 调出月考核奖金额 - 调出一次性奖金金额 + 上期结余
					totalmoney = nmnyTotal.add(vdef15).add(vdef17).sub(vdef16).sub(vdef18).add(lastTotalJY);
				}
			}
			//奖金科室分配：月考核奖
			//UFDouble nmny1 = nmny1Total.add(vdef15).sub(vdef16);
			UFDouble nmny1 = nmny1Total;
			//奖金科室分配：一次性奖金
			//UFDouble nmny2 = nmny2Total.add(vdef17).sub(vdef18);
			UFDouble nmny2 = nmny2Total;
			//月考核类
			getBillCardPanel().getHeadItem("vdef12").setValue(nmny1);
			//一次性奖金
			getBillCardPanel().getHeadItem("vdef13").setValue(nmny2);
			//累计结余
			getBillCardPanel().getHeadItem("vdef14").setValue(lastTotalJY);
			
			//本月期初(保存起来，用于后续的提交时校验结余)
			getBillCardPanel().getHeadItem("nbyqc").setValue(totalmoney);
			//本次可分配金额合计
			getBillCardPanel().getHeadItem("totalmoney").setValue(totalmoney);
			//改造end tianxfc
			
			//((ClientEventHandler)getManageEventHandler()).addPsn();
		} catch (Exception e1) {
			e1.printStackTrace();
		}  
	
	}
//	public void setDeptSum(String deptid) throws Exception{
//	// 根据主表主键，取得子表二的数据
//	String pk_wa_class = (String)getBillCardPanel().getHeadItem("pk_wa_class").getValueObject();
//	String pk_wa_period = (String)getBillCardPanel().getHeadItem("pk_wa_period").getValueObject();

//	FenpeiItemHVO[] hvos = (FenpeiItemHVO[])HYPubBO_Client.queryByCondition(FenpeiItemHVO.class, " vbillstatus_audit="+WaHrpBillStatus.PASS+" and  isnull(dr,0)=0 and pk_wa_class='"+pk_wa_class+"' and pk_wa_period='"+pk_wa_period+"' order by dapprovedate ");


//	HashMap<String,HashMap<String,Object[]>> map = new HashMap<String,HashMap<String,Object[]>>();
//	ArrayList<String> list_no = new ArrayList<String>();
//	if(hvos!=null&&hvos.length>0){
//	for(FenpeiItemHVO vo:hvos){
//	int x = vo.getNindex();
//	FenpeiItemBVO[] bvos = (FenpeiItemBVO[])HYPubBO_Client.queryByCondition(FenpeiItemBVO.class, " pk_wa_dept='"+deptid+"' and  isnull(dr,0)=0 and pk_fenpei_h='"+vo.getPrimaryKey()+"' ");
//	if(bvos!=null&&bvos.length>0){
//	for(int i=0;i<x;i++){
//	String item = (String)vo.getAttributeValue("pk_item"+(i+1)+"");
//	if(item!=null&&map.containsKey(item)){
//	HashMap<String,Object[]> map_tmp = map.get(item);
//	map_tmp.put(vo.getVbatchcode()+"", new Object[]{vo.getDapprovedate(),bvos[0].getAttributeValue("nmny"+(i+1)+"")});
//	if(!list_no.contains(vo.getVbatchcode()+"")){
//	list_no.add(vo.getVbatchcode()+"");
//	}
//	map.put(item, map_tmp);
//	}else if(item!=null){
//	HashMap<String,Object[]> map_tmp = new HashMap<String, Object[]>();
//	map_tmp.put(vo.getVbatchcode()+"", new Object[]{vo.getDapprovedate(),bvos[0].getAttributeValue("nmny"+(i+1)+"")});
//	if(!list_no.contains(vo.getVbatchcode()+"")){
//	list_no.add(vo.getVbatchcode()+"");
//	}
//	map.put(item, map_tmp);
//	}
//	}
//	}
//	}
//	}


//	int index2 = list_no!=null?list_no.size():0;
//	getBillCardPanel().getHeadItem("nindex2").setValue(index2);
//	for(int i=1;i<=10;i++){
//	if(i<=index2){
//	getBillCardPanel().getBillModel("wa_deptsum").getItemByKey("ddate"+i+"").setShow(true);
//	getBillCardPanel().getBillModel("wa_deptsum").getItemByKey("ddate"+i+"").setName("分配日期");
//	getBillCardPanel().getBillModel("wa_deptsum").getItemByKey("nmny"+i+"").setShow(true);
//	getBillCardPanel().getBillModel("wa_deptsum").getItemByKey("nmny"+i+"").setName("分配金额");
//	}else{
//	getBillCardPanel().getBillModel("wa_deptsum").getItemByKey("ddate"+i+"").setShow(false);
//	getBillCardPanel().getBillModel("wa_deptsum").getItemByKey("nmny"+i+"").setShow(false);
//	}
//	}


//	getBillCardPanel().setBillData(getBillCardPanel().getBillData());

//	GroupableTableHeader cardHeader=(GroupableTableHeader)getBillCardPanel().getBillTable("wa_deptsum").getTableHeader();
//	TableColumnModel cardTcm=getBillCardPanel().getBillTable("wa_deptsum").getColumnModel();
//	for(int i=0;i<index2;i++){
//	ColumnGroup group_origin = new ColumnGroup("分配序号:"+list_no.get(i)+"");
//	group_origin.add(cardTcm.getColumn(i*2+1));
//	group_origin.add(cardTcm.getColumn(i*2+2));
////	group_origin.add(cardTcm.getColumn(i*3+3));
//	cardHeader.addColumnGroup(group_origin);
//	}


//	DeptSumVO[] deptvos = null;

//	if(map!=null&&map.size()>0){
//	ArrayList<DeptSumVO> list = new ArrayList<DeptSumVO>();
//	String[] keys = map.keySet().toArray(new String[0]);
//	for(String tmpkey:keys){
//	HashMap<String,Object[]> tmp_map = map.get(tmpkey);
//	DeptSumVO vo = new DeptSumVO();
//	vo.setPk_item(tmpkey);
//	vo.setPrimaryKey(null);
//	vo.setStatus(VOStatus.NEW);
//	for(int i=0;i<list_no.size();i++){
//	Object[] value = tmp_map.get(list_no.get(i));
//	vo.setAttributeValue("ddate"+(i+1)+"", value[0]);
//	vo.setAttributeValue("nmny"+(i+1)+"", value[1]);
//	vo.setAttributeValue("vbatchcode"+(i+1)+"",Integer.parseInt(list_no.get(i)));
//	}
//	list.add(vo);
//	}
//	if(list!=null&&list.size()>0){
//	deptvos = list.toArray(new DeptSumVO[0]);
//	}
//	}
//	getBillCardPanel().getBillModel("wa_deptsum").setBodyDataVO(deptvos);
//	getBillCardPanel().getBillModel("wa_deptsum").execLoadFormula();
//	}
	//sqt
	private void rePayValueToHeadItem() {
		//设置薪酬项目
		if(mapkeytoitem!=null&&mapkeytoitem.size()>0){
			String[] keys = mapkeytoitem.keySet().toArray(new String[0]);
			for(String key:keys){
				getBillCardPanel().setHeadItem(key,mapkeytoitem.get(key));
			}
			getBillCardPanel().setHeadItem("nindex",mapkeytoitem.size());
		}else{
			getBillCardPanel().setHeadItem("nindex",0);
		}
	}

	@Override
	public boolean beforeEdit(BillEditEvent e) {
		if(e.getPos()==HEAD&&e.getKey().equals("pk_dept")){
			oldpk_dept=((UIRefPane)e.getSource()).getRefPK()+"";
			((UIRefPane)e.getSource()).updateUI();
			UIRefPane pane = ((UIRefPane)getBillCardPanel().getHeadItem("pk_dept").getComponent());
			pane.setWhereString(" pk_perioddept in(" +
					" select pk_deptdoc from bd_wa_userdept where "+
					"   isnull(dr,0)=0 and pk_user='"+_getOperator()+"') and isnull(vdef5,'N')='Y' "
			);
		}else if(e.getKey().equals("psncode")){ //过滤薪资档案关联的人员
//			UIRefPane pane = (UIRefPane)getBillCardPanel().getBillModel().getItemByKey(e.getKey()).getComponent();

//			AbstractRefTreeModel refModel = (AbstractRefTreeModel) pane.getRefModel();

//			String pk_wa_class = getBillCardPanel().getHeadItem("pk_wa_class").getValueObject()!=null?getBillCardPanel().getHeadItem("pk_wa_class").getValueObject().toString():"";
//			String whereSql = " bd_psnbasdoc.pk_psnbasdoc in (select wd.pk_psnbasdoc from wa_data wd where wd.classid='"+pk_wa_class+"' and istopflag=0 ) ";
//			refModel.setWherePart(whereSql);  

		}else if(e.getKey().equals("psnname")){
//			getBillCardPanel().getBodyItem("wa_psn_item_b", "psnname").setEnabled(true);
			getBillCardPanel().getBillModel().setCellEditable(e.getRow(), "psnname", true);
			getBillCardPanel().getBillModel().setCellEditable(e.getRow(), "psncode", false);

			
		}
		return super.beforeEdit(e);
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
			int status = hvo.getVbillstatus();
			//int status_audit = hvo.getVbillstatus_audit()!=null?hvo.getVbillstatus_audit():-1;
			int pk_item12 = hvo.getPk_item12() != null?hvo.getPk_item12() : -1;
			int pk_item14 = hvo.getPk_item14() != null?hvo.getPk_item14() : -1;
			int status_audit = hvo.getVbillstatus_audit() != null?hvo.getVbillstatus_audit() : -1;
			//当一级、一级半、二级审批中按钮的状态为3、4（"未批准","驳回"）时，奖金二次分配节点修改按钮启用
			if(pk_item12 == 3 || pk_item14 == 3 || pk_item14 == 4 || status_audit == 3 || status_audit == 4){
				pk_item12 = 3;
			}
			switch (pk_item12) {
			case WaHrpBillStatus.DEL:
				getButtonManager().getButton(IBillButton.Edit).setEnabled(false);
				getButtonManager().getButton(IBillButton.Delete).setEnabled(false);
				getButtonManager().getButton(IBillButton.Commit).setEnabled(false);
				break;
			case WaHrpBillStatus.WRITE:
			case WaHrpBillStatus.NOPASS:
			case WaHrpBillStatus.NOPASS_RETURN:
				getButtonManager().getButton(IBillButton.Edit).setEnabled(true);
				getButtonManager().getButton(IBillButton.Delete).setEnabled(true);
//				getButtonManager().getButton(IHRPBtn.COMMITREURN).setEnabled(false);
				break;
			case WaHrpBillStatus.COMMIT:
				getButtonManager().getButton(IBillButton.Edit).setEnabled(false);
				getButtonManager().getButton(IBillButton.Delete).setEnabled(false);
//				getButtonManager().getButton(IHRPBtn.COMMITREURN).setEnabled(true);
				break;
			case WaHrpBillStatus.PASS:
				getButtonManager().getButton(IBillButton.Edit).setEnabled(false);
				getButtonManager().getButton(IBillButton.Delete).setEnabled(false);
//				getButtonManager().getButton(IHRPBtn.COMMITREURN).setEnabled(false);
				break;
//				case WaHrpBillStatus.NOPASS:
//				getButtonManager().getButton(IBillButton.Edit).setEnabled(false);
//				getButtonManager().getButton(IBillButton.Delete).setEnabled(false);
//				getButtonManager().getButton(IHRPBtn.COMMITREURN).setEnabled(false);
//				break;

			default:
				break;
			}
			if(!iscan){
				getButtonManager().getButton(IBillButton.Add).setEnabled(false);
				//getButtonManager().getButton(IBillButton.Copy).setEnabled(false);
				getButtonManager().getButton(IBillButton.Edit).setEnabled(false);
				getButtonManager().getButton(IBillButton.Delete).setEnabled(false);
				getButtonManager().getButton(IBillButton.Commit).setEnabled(false);
//				getButtonManager().getButton(IHRPBtn.COMMITREURN).setEnabled(false);
			}
			updateButtons();
		}
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
						itemlist.setWidth(70);
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

				for(int i=0;i<10;i++){
					getBillListPanel().getBodyBillModel("wa_deptsum").getItemByKey("nmny"+""+(i+1)+"").setShow(false);
					getBillListPanel().getBodyBillModel("wa_deptsum").getItemByKey("ddate"+""+(i+1)+"").setShow(false);
				}

				getBillListPanel().setBodyListData(getBillListPanel().getBillListData());
			}else{
				for(int i=0;i<index;i++){
					String pk_item = hvo.getAttributeValue("pk_item"+""+(i+1)+"")!=null?hvo.getAttributeValue("pk_item"+""+(i+1)+"").toString().trim():"";
					BillItem item = getBillCardPanel().getBillModel("wa_psn_item_b").getItemByKey("nmny"+""+(i+1)+"");
					if(pk_item!=null&&pk_item.trim().length()>0&&item!=null&&item.getKey()!=null){
						item.setShow(true);
						item.setWidth(70);
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
				for(int i=0;i<10;i++){
					getBillCardPanel().getBillModel("wa_deptsum").getItemByKey("nmny"+""+(i+1)+"").setShow(false);
					getBillCardPanel().getBillModel("wa_deptsum").getItemByKey("ddate"+""+(i+1)+"").setShow(false);
				}

				getBillCardPanel().setBillData(getBillCardPanel().getBillData());

			}
		}
	}
	@Override
	public void afterUpdate() {
		super.afterUpdate();
		updateBtn();

		updateItems();

//		updateBodyOrder();
//		reSetFldValue();
		getBillListPanel().getHeadItem("bisselect").setEnabled(true);
		getBillListPanel().getBodyBillModel("wa_psn_item_b").getItemByKey("bisselect").setEnabled(true);
		getBillCardPanel().getBillModel("wa_psn_item_b").getItemByKey("bisselect").setEnabled(true);

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
			vos = (PsnClassItemBVO[])HYPubBO_Client.queryByCondition(PsnClassItemBVO.class
					, " pk_psn_item_h='"+billid+"' and dr=0");
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

	protected void updateBodyOrder(){
		if(getBufferData().getCurrentVO()!=null&&getBufferData().getCurrentVO().getParentVO()!=null){
			PsnClassItemHVO hvo = (PsnClassItemHVO)getBufferData().getCurrentVO().getParentVO();
			int index = hvo.getNindex();
			BillItem[] bis = null;
			UpdataBodyItemOrder itemOrder = new UpdataBodyItemOrder(this);
			try {
				bis = itemOrder.reSetItemOrder(hvo,index);
			} catch (BusinessException e) {
				Logger.error("BILLITEM排序出错。",e);
				e.printStackTrace();
			}
			if(isListPanelSelected()){
				getBillListPanel().getBillListData().setBodyItems(bis);
				getBillListPanel().setBodyListData(getBillListPanel().getBillListData());
			}else{
				getBillCardPanel().getBillData().setBodyItems(bis);
				getBillCardPanel().setBillData(getBillCardPanel().getBillData());
			}
		}
	}
	@Override
	public void bodyRowChange(BillEditEvent e) {
		super.bodyRowChange(e);
		if(e.getPos()==HEAD){
			//updateItems();
		}
	}

	public boolean beforeEdit(BillItemEvent e) {
		if(e.getItem().getKey().equals("pk_dept")){
			((UIRefPane)e.getItem().getComponent()).getRefModel().reloadData();
		}else if(e.getItem().getKey().equals("pk_deptdoc")){  //这里参照的是部门档案，这个主键用pk_deptdoc比较好，这里用了pk_wa_dept不要理解错了
//			取表头薪资类别关联的薪资部门档案
			String pk_wa_class = getBillCardPanel().getHeadItem("pk_wa_class").getValueObject()!=null?getBillCardPanel().getHeadItem("pk_wa_class").getValueObject().toString():"";
			String whereSql = " pk_deptdoc in ( select pk_deptdoc from wa_dept where pk_wa_class='"+pk_wa_class+"'  ) ";
			UIRefPane pane = (UIRefPane)getBillCardPanel().getBillModel().getItemByKey(e.getItem().getKey()).getComponent();
			pane.getRefModel().setWherePart(whereSql);

		}else if(e.getItem().getKey().equals("psncode")){ //过滤薪资档案关联的人员
			UIRefPane pane = (UIRefPane)getBillCardPanel().getBillModel().getItemByKey(e.getItem().getKey()).getComponent();

			AbstractRefTreeModel refModel = (AbstractRefTreeModel) pane.getRefModel();

			String pk_wa_class = getBillCardPanel().getHeadItem("pk_wa_class").getValueObject()!=null?getBillCardPanel().getHeadItem("pk_wa_class").getValueObject().toString():"";
			String whereSql = " bd_psnbasdoc.pk_psnbasdoc in (select wd.pk_psnbasdoc from wa_data wd where wd.classid='"+pk_wa_class+"' and istopflag=0 ) ";
			refModel.setWherePart(whereSql);  

		}else if(e.getItem().getKey().equals("pk_wa_period")){
			UIRefPane pane = (UIRefPane)getBillCardPanel().getHeadItem(e.getItem().getKey()).getComponent();


			String pk_wa_class = getBillCardPanel().getHeadItem("pk_wa_class").getValueObject()!=null?getBillCardPanel().getHeadItem("pk_wa_class").getValueObject().toString():"";
			String whereSql = " wa_waclass.pk_wa_class='"+pk_wa_class+"'  ";
			pane.getRefModel().setWherePart(whereSql);
		}
		else if(e.getItem().getKey().equals("pk_wa_class")){

//			UIRefPane pane = (UIRefPane) e.getItem().getComponent();

//			//配置where语句，增加权限过滤
//			String waclssSql = " pk_wa_class in" +
//			"((select classid from wa_uclsright " +
//			" where pk_corp = '"+_getCorp().getPrimaryKey()+"' and cuserid = '"+_getOperator()+"' and moduleflag = 0) " +
//			" union " +
//			" (select classid from wa_gclsright where cgroupid in " +
//			" (select pk_role from sm_user_role " +
//			" where sm_user_role.cuserid = '"+_getOperator()+"') and pk_corp = '"+_getCorp().getPrimaryKey()+"' and moduleflag = 0))";
//			pane.getRefModel().setWherePart(waclssSql);

		}
		return true;
	}

	public PERIODVO getWaPeriodVO() {
		return waPeriodVO;
	}

	public HashMap<String, ClassitemVO> getMapclassitem() {
		return mapclassitem;
	}

	public void setMapclassitem(HashMap<String, ClassitemVO> mapclassitem) {
		this.mapclassitem = mapclassitem;
	}

	public boolean isIscan() {
		return iscan;
	}

	public void setIscan(boolean iscan) {
		this.iscan = iscan;
	}

	public ArrayList<String> getList_power_item() {
		return list_power_item;
	}

	public void setList_power_item(ArrayList<String> list_power_item) {
		this.list_power_item = list_power_item;
	}
}
