package nc.ui.wa.wa_hrp_006;


import java.util.Map;

import nc.bs.framework.common.NCLocator;
import nc.itf.hrwa.IHRWaServices;
import nc.itf.uap.IUAPQueryBS;
import nc.itf.wa.hrp.pub.IHRPWABtn;
import nc.itf.wa.wa_hrppub.WaHrpBillStatus;
import nc.jdbc.framework.processor.BeanProcessor;
import nc.ui.bd.ref.AbstractRefTreeModel;
import nc.ui.hr.global.Global;
import nc.ui.hrp.pub.bill.HRPManagerUI;
import nc.ui.pub.ClientEnvironment;
import nc.ui.pub.FramePanel;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.bill.BillEditEvent;
import nc.ui.trade.bill.AbstractManageController;
import nc.ui.trade.business.HYPubBO_Client;
import nc.ui.trade.button.IBillButton;
import nc.ui.trade.manage.ManageEventHandler;
import nc.ui.wa.pub.WADelegator;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDouble;
import nc.vo.trade.button.ButtonVO;
import nc.vo.wa.wa_001.WaclassHeaderVO;
import nc.vo.wa.wa_hrp_002.PERIODVO;
import nc.vo.wa.wa_hrp_009.UserDeptVO;
import nc.vo.wa.wa_hrppub.QueryDeptButtonVO;

/**
 * @author szh
 *
 */
@SuppressWarnings("restriction")
public class ClientUI extends HRPManagerUI {
	private static final long serialVersionUID = 1L;

	private String pk_wa_class = IHRPWABtn.PK_JIANG;//�»�����ΪĬ�Ͻ������

	PERIODVO waPeriodVO = null;
	boolean iscan = false;
	
	//begin tianxfc
	IHRWaServices service =  NCLocator.getInstance().lookup(IHRWaServices.class);
	
	//��ѯ���Ҷ�Ӧ�ģ����οɷ�����ϼơ��¿����ࡢһ���Խ��𡢵����¿��˽��𡢵����¿��˽��𡢵���һ���Խ��������һ���Խ�����ۼƽ���
	Map<String, Map<String, Map<String, UFDouble>>> inoutMap = null;
	//���ҽ��������ֵ䣬 key��������𣨺ϼƽ�nmnyTotal���¿��˽���nmny1Total��һ���Խ���nmny2Total����value��������������ֵ�map
	//value��������������ֵ�map�� key���������pk_dept, value�����
	Map<String, Map<String, UFDouble>> ksjjMap = null;
			
	//��ȡ���п��������ۼƽ��������ֵ䣬key������pk_dept�� value�������ۼƽ����� vdef14
	Map<String, UFDouble> lastJYMap = null;
	//��н����𡿲����������ֵ䣺key��pk_wa_classitem��������������, value��vname�����������ƣ�
	Map<String, String> waClassItemVOMap = null;
	//end tianxfc
		
	public ClientUI() {
		setSata();
	}
	
	public ClientUI(FramePanel arg0) {
		super(arg0);
		setSata();
	}

	public ClientUI(Boolean arg0) {
		super(arg0);
		setSata();
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 * @param arg3
	 * @param arg4
	 */
	public ClientUI(String arg0, String arg1, String arg2, String arg3,
			String arg4) {
		super(arg0, arg1, arg2, arg3, arg4);
		setSata();
	}
	
	private void setSata(){
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
		}catch(Exception e){
			waPeriodVO = null;
			iscan = false;
			if(!iscan){
				MessageDialog.showHintDlg(this, "��ʾ", "��½�ڼ䲻����Сδ�����£����ܽ���ҵ�������ֻ�в�ѯȨ��");
			}
		}
		
		if(!iscan){
			if(!iscan){
				MessageDialog.showHintDlg(this, "��ʾ", "��½�ڼ䲻����Сδ�����£����ܽ���ҵ�������ֻ�в�ѯȨ��");
			}
			getButtonManager().getButton(IBillButton.Add).setEnabled(false);
		}
		getButtonManager().getButton(IBillButton.InsLine).setVisible(false);
		getButtonManager().getButton(IBillButton.CopyLine).setVisible(false);
		getButtonManager().getButton(IBillButton.PasteLine).setVisible(false);
		getButtonManager().getButton(IBillButton.PasteLinetoTail).setVisible(false);
		getButtonManager().getButton(IBillButton.CancelAudit).setName("�����˻�");
		updateButtons();
		UIRefPane pane = (UIRefPane)getBillCardPanel().getHeadItem("pk_dept_in").getComponent();
		pane.getRefModel().addWherePart(" and isnull(vdef5,'N') = 'Y' ");
		UIRefPane pane2 = (UIRefPane)getBillCardPanel().getHeadItem("pk_dept_out").getComponent();
		
		pane2.getRefModel().addWherePart(" and pk_perioddept in(" +
				" select pk_deptdoc from bd_wa_userdept where "+
				"   isnull(dr,0)=0 and pk_user='"+_getOperator()+"') and isnull(vdef5,'N')='Y'  "
		);
//		pane2.getRefModel().addWherePart(" and isnull(vdef5,'N') = 'Y' ");
		
//		pane2.setWhereString(" pk_perioddept in(" +
//				" select pk_deptdoc from bd_wa_userdept where "+
//				"   isnull(dr,0)=0 and pk_user='"+_getOperator()+"') and isnull(vdef5,'N')='Y' "
//		);
		
		//��ȡ���п��������ۼƽ��������ֵ䣬key������pk_dept�� value�������ۼƽ����� vdef14
		String pk_corp = ClientEnvironment.getInstance().getCorporation().getPk_corp();
		if(waPeriodVO != null){
			try {
				lastJYMap = service.queryLastTotalJYByCond(pk_corp, waPeriodVO.getCyear(), waPeriodVO.getVcalmonth());
				//����Ĭ��н������ڼ䡢�û�id����ȡ��н����𡿲����������ֵ䣺key��pk_wa_classitem��������������, value��vname�����������ƣ�
				if(waClassItemVOMap == null && waPeriodVO != null){
					waClassItemVOMap = service.qryWAClassitemMap(pk_wa_class, waPeriodVO.getCyear(), waPeriodVO.getCperiod(), _getOperator());
				}
			} catch (BusinessException e) {
				e.printStackTrace();
			}			
		}

		
	}
	@Override
	protected AbstractManageController createController() {
		return new ClientControl();
	}
	@Override
	public void setDefaultData() throws Exception {
		getBillCardPanel().getHeadItem("dmakedate").setValue(_getDate());
		getBillCardPanel().getHeadItem("pk_corp").setValue(_getCorp().getPrimaryKey());
		getBillCardPanel().getHeadItem("voperatorid").setValue(_getOperator());

		getBillCardPanel().setHeadItem("pk_billtype", getUIControl().getBillType());
		UserDeptVO[] deptvos = (UserDeptVO[])HYPubBO_Client.queryByCondition(UserDeptVO.class, " isnull(dr,0)=0 and pk_user='"+_getOperator()+"' ");
		if(deptvos!=null&&deptvos.length>0){
//			UIRefPane pane = (UIRefPane)getBillCardPanel().getHeadItem("pk_dept_out").getComponent();
//			pane.setPK(deptvos[0].getPrimaryKey());
			getBillCardPanel().getHeadItem("pk_dept_out").setValue(deptvos[0].getPk_deptdoc());
			getBillCardPanel().execHeadLoadFormulas();
		}
		getBillListPanel().getHeadBillModel().execLoadFormula();
	}
	@Override
	protected ManageEventHandler createEventHandler() {
		return new EventHandler(this,this.createController());
	}
	@Override
	public boolean beforeEdit(BillEditEvent e) {
		if(e.getKey().equals("itemname")){ //�µ�н����Ŀ    2012-5-23 qiutian
			UIRefPane pane = (UIRefPane)getBillCardPanel().getBillModel().getItemByKey(e.getKey()).getComponent();
			AbstractRefTreeModel refModel = (AbstractRefTreeModel) pane.getRefModel();
			//����where��䣬����Ȩ�޹���
			String waclssSql = " pk_wa_class ='"+pk_wa_class+"' ";
			refModel.setClassWherePart(waclssSql);

			refModel.setWherePart("  wa_classitem.cyear='"+waPeriodVO.getCyear()+"' and wa_classitem.cperiod='"+waPeriodVO.getVcalmonth()+"' and " +
					" wa_classitem.ifromflag=20 and wa_classitem.pk_wa_item in ( select pk_wa_item from wa_itemright where cuserid = '"+_getOperator()+"'" +
					" union select pk_wa_item from wa_itemright_group where groupid in" +
					" (select pk_role from sm_user_role where sm_user_role.cuserid = '"+_getOperator()+"' ))");
		}
		return super.beforeEdit(e);
	}
	@Override
	public void afterEdit(BillEditEvent e) {
		if(e.getPos()==BODY&&e.getKey().equals("nmny")){
			UFDouble nmny = new UFDouble(0);
			int row = getBillCardPanel().getBillModel().getRowCount();
			for(int i=0;i<row;i++){
				Object value = getBillCardPanel().getBillModel().getValueAt(i, "nmny");
			    nmny = nmny.add(value!=null?new UFDouble(value.toString()):new UFDouble(0));
			}
			getBillCardPanel().getHeadItem("nmny").setValue(nmny);
		}else if(e.getPos()==HEAD&&(e.getKey().equals("pk_dept_in")||e.getKey().equals("pk_dept_out"))){
			Object value1 = getBillCardPanel().getHeadItem("pk_dept_in").getValueObject();
			Object value2 = getBillCardPanel().getHeadItem("pk_dept_out").getValueObject();
			if(value1!=null&&value2!=null&&value1.toString().trim().length()>0&&value1.toString().trim().equals(value2.toString().trim())){
				MessageDialog.showHintDlg(this, "��ʾ", "ת��ת�����Ҳ���һ��");
				getBillCardPanel().getHeadItem(e.getKey()).setValue(null);
				return;
			}
			
			
			//����begin tianxfc
			if(e.getKey().equals("pk_dept_out")){
				if(waPeriodVO!=null) {
					//ת������pk
					String pk_dept_out = (String)getBillCardPanel().getHeadItem("pk_dept_out").getValueObject();
					
					String pk_corp = ClientEnvironment.getInstance().getCorporation().getPk_corp();
					//��ѯ���Ҷ�Ӧ�ģ����οɷ�����ϼơ��¿����ࡢһ���Խ��𡢵����¿��˽��𡢵����¿��˽��𡢵���һ���Խ��������һ���Խ�����ۼƽ���
					try {
						inoutMap = service.queryMnyMapByCond(pk_corp, null, waPeriodVO.getCyear(), waPeriodVO.getVcalmonth());
						if(inoutMap != null && !inoutMap.isEmpty()){
							ksjjMap = inoutMap.get("ksjjMap");
						}
						//�����ۼƽ���
						UFDouble lastTotalJY = new UFDouble(0);
						if(lastJYMap != null){
							lastTotalJY = lastJYMap.get(pk_dept_out) != null? lastJYMap.get(pk_dept_out) : new UFDouble(0);
						}
						
						//���õ��롢�����¿��˼�һ���Կ��˽��
						UFDouble vdef15 = new UFDouble(0); //�����¿��˽����
						UFDouble vdef16 = new UFDouble(0); //�����¿��˽����
						UFDouble vdef17 = new UFDouble(0); //����һ���Խ�����
						UFDouble vdef18 = new UFDouble(0); //����һ���Խ�����
						if(inoutMap != null){
							Map<String, Map<String, UFDouble>> zrMap = inoutMap.get("zrMap");
							Map<String, Map<String, UFDouble>> zcMap = inoutMap.get("zcMap");
							if(zrMap != null && zrMap.containsKey(pk_dept_out)){
								Map<String, UFDouble> map = zrMap.get(pk_dept_out);  //zrMap.get("10024Z1000000002M6TL");
								if(map != null && !map.isEmpty()){
									vdef15 = map.get("10028L1000000004KHJS"); //�����¿��˽���"10024Z1000000002M6TL"
									if(vdef15 == null){
										vdef15 = new UFDouble(0);
									}
									vdef17 = map.get("10028L1000000004KHJT"); //����һ���Խ���"10024Z1000000002M6TL"
									if(vdef17 == null){
										vdef17 = new UFDouble(0);
									}
								}
							}
							if(zcMap != null && zcMap.containsKey(pk_dept_out)){
								Map<String, UFDouble> map = zcMap.get(pk_dept_out);  //zrMap.get("10024Z1000000002M6TL");
								if(map != null && !map.isEmpty()){
									vdef16 = map.get("10028L1000000004KHJS"); //�����¿��˽���"10024Z1000000002M6TL"
									if(vdef16 == null){
										vdef16 = new UFDouble(0);
									}
									vdef18 = map.get("10028L1000000004KHJT"); //����һ���Խ���"10024Z1000000002M6TL"
									if(vdef18 == null){
										vdef18 = new UFDouble(0);
									}
								}
							}
						}
						
						//������ҷ��䣺�ϼƽ��
						//UFDouble totalmoney = null;
						//UFDouble nmnyTotal = new UFDouble(0);
						UFDouble nmny1Total = new UFDouble(0);
						UFDouble nmny2Total = new UFDouble(0);
						if(ksjjMap != null && !ksjjMap.isEmpty()){
							//�������-�ϼƽ��
							Map<String, UFDouble> nmnyMap = ksjjMap.get("nmnyTotal");
							//�������-�¿��˽����
							Map<String, UFDouble> nmny1Map = ksjjMap.get("nmny1Total");
							//�������-һ���Խ�����
							Map<String, UFDouble> nmny2Map = ksjjMap.get("nmny2Total");
							
							if(nmnyMap != null && !nmnyMap.isEmpty()){
								//�Ӽ�Чϵͳ���¹����Ŀ��ҵĺϼƽ��
								//nmnyTotal = nmnyMap.get(pk_dept_out) != null ? nmnyMap.get(pk_dept_out) : new UFDouble(0);
								//�Ӽ�Чϵͳ���¹����Ŀ��ҵ��¿��˽����
								nmny1Total = nmny1Map.get(pk_dept_out) != null ? nmny1Map.get(pk_dept_out) : new UFDouble(0);
								//�Ӽ�Чϵͳ���¹����Ŀ��ҵ�һ���Խ����
								nmny2Total = nmny2Map.get(pk_dept_out) != null ? nmny2Map.get(pk_dept_out) : new UFDouble(0);
								
								//���οɷ�����ϼ� = ���Һϼƽ��¿��˽�+һ���Խ��� + �����¿��˽���� + ����һ���Խ����� - �����¿��˽���� - ����һ���Խ����� + ���ڽ���
								//totalmoney = nmnyTotal.add(vdef15).add(vdef17).sub(vdef16).sub(vdef18).add(lastTotalJY);
							}
						}
						//������ҷ��䣺�¿��˽� = �����¿��˽� + �����¿��˽����  - �����¿��˽���� + ���ڽ���
						UFDouble nmny1 = nmny1Total.add(vdef15).sub(vdef16).add(lastTotalJY);
						//UFDouble nmny1 = nmny1Total;
						//������ҷ��䣺һ���Խ��� = ����һ���Խ��� + ����һ���Խ����� - ����һ���Խ����� 
						UFDouble nmny2 = nmny2Total.add(vdef17).sub(vdef18);
						//UFDouble nmny2 = nmny2Total;
						//�¿�����
						getBillCardPanel().getHeadItem("vdef11").setValue(nmny1);
						//һ���Խ���
						getBillCardPanel().getHeadItem("vdef12").setValue(nmny2);
						
					} catch (BusinessException e1) {
						//getButtonManager().getButton(IBillButton.Add).setEnabled(false);
						e1.printStackTrace();
					}
				}
			}
			//����end tianxfc
		}
		super.afterEdit(e);
	}
	@Override
	protected void initSelfData() {
		super.initSelfData();
		UIRefPane pane = ((UIRefPane)getBillCardPanel().getHeadItem("pk_dept_out").getComponent());
		pane.getRefModel().addWherePart(" and pk_perioddept in(" +
						" select pk_deptdoc from bd_wa_userdept where "+
						"   isnull(dr,0)=0 and pk_user='"+_getOperator()+"') and isnull(vdef5,'N')='Y'   "
				);
		getBillCardPanel().setAutoExecHeadEditFormula(true);
		
		
		
	}
	@Override
	protected void initPrivateButton() {
		super.initPrivateButton();
		ButtonVO querydept = new QueryDeptButtonVO().getButtonVO();
		addPrivateButton(querydept);
	}

	
	
	
	public PERIODVO getWaPeriodVO() {
		return waPeriodVO;
	}

	public void setWaPeriodVO(PERIODVO waPeriodVO) {
		this.waPeriodVO = waPeriodVO;
	}

	public Map<String, Map<String, Map<String, UFDouble>>> getInoutMap() {
		return inoutMap;
	}

	public void setInoutMap(Map<String, Map<String, Map<String, UFDouble>>> inoutMap) {
		this.inoutMap = inoutMap;
	}

	public Map<String, Map<String, UFDouble>> getKsjjMap() {
		return ksjjMap;
	}

	public void setKsjjMap(Map<String, Map<String, UFDouble>> ksjjMap) {
		this.ksjjMap = ksjjMap;
	}

	public Map<String, UFDouble> getLastJYMap() {
		return lastJYMap;
	}

	public void setLastJYMap(Map<String, UFDouble> lastJYMap) {
		this.lastJYMap = lastJYMap;
	}

	public Map<String, String> getWaClassItemVOMap() {
		return waClassItemVOMap;
	}

	public void setWaClassItemVOMap(Map<String, String> waClassItemVOMap) {
		this.waClassItemVOMap = waClassItemVOMap;
	}
	
	
}
