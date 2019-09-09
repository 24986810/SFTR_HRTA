package nc.ui.wa.wa_hrp_002;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import nc.bs.framework.common.NCLocator;
import nc.itf.hrp.pub.IHRPBtn;
import nc.itf.hrp.pub.IHRPSysParam;
import nc.itf.hrp.pub.IhrpNCModule;
import nc.itf.hrwa.IHRWaServices;
import nc.itf.hrwa.IHRwaPub;
import nc.itf.uap.IUAPQueryBS;
import nc.itf.wa.hrp.pub.IHRPWABtn;
import nc.itf.wa.wa_hrppub.WaHrpBillStatus;
import nc.jdbc.framework.processor.BeanListProcessor;
import nc.jdbc.framework.processor.BeanProcessor;
import nc.jdbc.framework.processor.VectorProcessor;
import nc.ui.hr.global.Global;
import nc.ui.hrp.pub.bill.HRPEventhandle;
import nc.ui.hrp.pub.excel.ExcelOut;
import nc.ui.hrp.pub.excel.ImportExcelData;
import nc.ui.pub.ButtonObject;
import nc.ui.pub.ClientEnvironment;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UIDialog;
import nc.ui.pub.bill.BillEditEvent;
import nc.ui.pub.bill.BillItem;
import nc.ui.pub.filesystem.FileManageUI;
import nc.ui.pub.query.QueryConditionClient;
import nc.ui.trade.base.IBillOperate;
import nc.ui.trade.business.HYPubBO_Client;
import nc.ui.trade.button.IBillButton;
import nc.ui.trade.controller.IControllerBase;
import nc.ui.trade.manage.BillManageUI;
import nc.ui.wa.pub.WADelegator;
import nc.ui.wa.wa_hrp_003.MyCardPanelPRTS;
import nc.ui.wa.wa_hrp_005.MyQueryConditionClient;
import nc.uif.pub.exception.UifException;
import nc.vo.bd.b04.DeptdocVO;
import nc.vo.bd.b06.PsndocVO;
import nc.vo.hrp.pf02.PerioddeptVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.query.ConditionVO;
import nc.vo.sm.user.UserAndClerkVO;
import nc.vo.trade.pub.HYBillVO;
import nc.vo.trade.pub.IBillStatus;
import nc.vo.wa.wa_001.WaclassHeaderVO;
import nc.vo.wa.wa_002.ClassitemVO;
import nc.vo.wa.wa_020.WaDataVO;
import nc.vo.wa.wa_024.ItemVO;
import nc.vo.wa.wa_hrp_002.PERIODVO;
import nc.vo.wa.wa_hrp_002.PsnClassItemBBVO;
import nc.vo.wa.wa_hrp_002.PsnClassItemBVO;
import nc.vo.wa.wa_hrp_002.PsnClassItemHVO;
import nc.vo.wa.wa_hrp_004.ItemSetBVO;
import nc.vo.wa.wa_hrp_005.FenpeiItemBVO;
import nc.vo.wa.wa_hrp_006.DeptChargeHVO;

/**
 * @author ��ּ�
 * 2011-3-21����10:34:30
 * ˵����
 */
public class ClientEventHandler extends HRPEventhandle {
	private MyQueryConditionClient m_queryDialog = null;
	private String pk_wa_class = IHRPWABtn.PK_JIANG;//�»�����ΪĬ�Ͻ������
	
	boolean hasFlag = true;
	
	Map<String, FenpeiItemBVO> fenpeiItemBVOMap = null;
	/**
	 * @param billUI
	 * @param control
	 */
	public ClientEventHandler(BillManageUI billUI, IControllerBase control) {
		super(billUI, control);
	}

	//	public void onBoExcelImportb() throws Exception{
	//		//�������ݵȴ���
	//		String code = getBillCardPanelWrapper().getBillCardPanel().getCurrentBodyTableCode();
	//		if(!code.equals("wa_psn_item_b")){
	//			return;
	//		}
	//		 HashMap<String,ItemVO> mapitem = new HashMap<String, ItemVO>();
	//		IItem item = (IItem)NCLocator.getInstance().lookup(IItem.class);
	//		ItemVO[] itemvos = item.queryAllItem(_getCorp().getPrimaryKey());
	//		if(itemvos!=null&&itemvos.length>0){
	//			for(ItemVO itemvo:itemvos){
	//				mapitem.put(itemvo.getVname(), itemvo);
	//			}
	//		}
	//		PsndocVO[] psnvo = (PsndocVO[])HYPubBO_Client.queryByCondition(PsndocVO.class, " isnull(dr,0)=0 and pk_corp='"+_getCorp().getPrimaryKey()+"'  ");
	//		HashMap<String,PsndocVO> map_psn = new HashMap<String, PsndocVO>();
	//		HashMap<String,PerioddeptVO> map_dept = new HashMap<String, PerioddeptVO>();
	//		PerioddeptVO[] deptvos = (PerioddeptVO[])HYPubBO_Client.queryByCondition(PerioddeptVO.class, " isnull(dr,0)=0 and pk_corp='"+_getCorp().getPrimaryKey()+"'");
	//		if(deptvos!=null&&deptvos.length>0){
	//			for(int uu=0;uu<deptvos.length;uu++){//�������ƶ���
	//				map_dept.put(deptvos[uu].getVcode().trim(), deptvos[uu]);
	//				map_dept.put(deptvos[uu].getVname().trim(), deptvos[uu]);
	//			}
	//		}
	//		if(psnvo!=null&&psnvo.length>0){
	//			for(int uu=0;uu<psnvo.length;uu++){
	//				PsndocVO vo=psnvo[uu];
	//				if(vo.getPsncode()!=null&&vo.getPsncode().trim().length()>0){
	//					map_psn.put(vo.getPsncode().trim(),vo);
	//				}
	//			}
	//		}
	//		
	//		
	//		ImportExcelData excel = new ImportExcelData();
	//		try {
	//			Object[][] values = excel.executeImport();
	//
	//			if(values==null||values.length<=1) return;
	//			try {
	//				PsnClassItemHVO hvo = new PsnClassItemHVO();
	//				hvo.setVyear("2015");
	//				hvo.setVperiod("03");
	//				hvo.setPk_corp(_getCorp().getPrimaryKey());
	//				hvo.setDmakedate(new UFDate("2015-03-31"));
	//				hvo.setVbillstatus(IBillStatus.CHECKPASS);
	//				hvo.setVbillstatus_audit(WaHrpBillStatus.PASS);
	//				hvo.setPk_billtype("68RP");
	//				hvo.setDcommitdate(new UFDate("2015-03-31"));
	//				hvo.setBispay(new UFBoolean(true));
	//				hvo.setDapprovedate(new UFDate("2015-03-31"));
	//				hvo.setDpaydate(new UFDate("2015-03-31"));
	//				hvo.setBisjiangjin(new UFBoolean(false));
	//				hvo.setPk_wa_period("0001691000000001FYIZ");
	//				hvo.setVoperatorid("0001231000000000ARCZ");
	//				hvo.setVapproveid("0001231000000000ARCZ");
	//				hvo.setVcommitid("0001231000000000ARCZ");
	//				hvo.setVpaypsnid("0001231000000000ARCZ");
	//				hvo.setPk_wa_class(IHRPWABtn.PK_JINTIE);
	//				
	//				
	//			    Object[] value = values[0];
	//			    for(int i=9;i<value.length;i++){
	//			    	hvo.setNindex(i-8);
	//			    	if(mapitem.get(value[i])==null){
	//			    		MessageDialog.showHintDlg(getBillUI(), "��ʾ", value[i]+"");
	//			    		return;
	//			    	}
	//			    	hvo.setAttributeValue("pk_item"+(i-8)+"", mapitem.get(value[i]).getPrimaryKey());
	//			    }
	//			    HashMap<String,ArrayList<PsnClassItemBVO>> map = new HashMap<String, ArrayList<PsnClassItemBVO>>();
	//			    HashMap<String,UFDouble[]> map_mny = new HashMap<String, UFDouble[]>();
	//				for(int i=1;i<values.length;i++){
	//					if(map_dept.get(values[i][1].toString().trim())==null){
	//						map_dept.get(values[i][1].toString().trim());
	//					}
	//					String key = map_dept.get(values[i][1].toString().trim()).getPrimaryKey()+"@"+values[i][2]+"@"+values[i][3];
	//					ArrayList<PsnClassItemBVO> list = map.get(key)==null?new ArrayList<PsnClassItemBVO>():map.get(key);
	//					UFDouble[] mny = map_mny.get(key)==null?new UFDouble[]{new UFDouble(0),new UFDouble(0),new UFDouble(0)}:map_mny.get(key);
	//					PsnClassItemBVO bvo = new PsnClassItemBVO();
	//					bvo.setDr(0);
	//					if(map_psn.get(values[i][4].toString().trim())==null){
	//						map_psn.get(values[i][4].toString().trim());
	//					}
	//					bvo.setPk_psndoc(map_psn.get(values[i][4].toString().trim()).getPrimaryKey());
	//					bvo.setPk_corp(_getCorp().getPrimaryKey());
	//					bvo.setPk_wa_dept(map_dept.get(values[i][1]).getPrimaryKey());
	//					bvo.setNmny(new UFDouble(values[i][6].toString()));
	//					bvo.setNsmny(new UFDouble(values[i][7].toString()));
	//					bvo.setNaftersmny(new UFDouble(values[i][8].toString()));
	//					bvo.setNsfmny(new UFDouble(values[i][8].toString()));
	//					bvo.setNmny(new UFDouble(values[i][6].toString()));
	//					bvo.setNbcnsmny(new UFDouble(values[i][6].toString()));
	//					bvo.setCtaxtableid("0001691000000001D7IW");
	//					mny[0] = mny[0].add(new UFDouble(values[i][6].toString()));
	//					mny[1] = mny[1].add(new UFDouble(values[i][7].toString()));
	//					mny[2] = mny[2].add(new UFDouble(values[i][8].toString()));
	//					for(int j=9;j<values[i].length;j++){
	//						bvo.setAttributeValue("nmny"+(j-8)+"", new UFDouble(values[i][j].toString()));
	//					}
	//					list.add(bvo);
	//					map.put(key, list);
	//					map_mny.put(key, mny);
	//				}
	//			    String[] keys = map.keySet().toArray(new String[0]);
	//			    for(String key:keys){
	//			    	UFDouble[] mny = map_mny.get(key);
	//			    	HYBillVO billvo = new HYBillVO();
	//			    	PsnClassItemHVO newhvo = (PsnClassItemHVO)hvo.clone();
	//			    	newhvo.setTotalmoney(mny[0]);
	//			    	newhvo.setTotalaftersmny(mny[2]);
	//			    	newhvo.setTotalsmny(mny[1]);
	//			    	newhvo.setPk_dept(key.split("@")[0]);
	//			    	newhvo.setVbatchcode_approve(Integer.parseInt(key.split("@")[1]));
	//			    	newhvo.setVbatchcode_dept(Integer.parseInt(key.split("@")[2]));
	//			    	newhvo.setVbillno(HYPubBO_Client.getBillNo("68RP", _getCorp().getPrimaryKey(), null, null));
	//			    	billvo.setParentVO(newhvo);
	//			    	billvo.setChildrenVO(map.get(key).toArray(new PsnClassItemBVO[0]));
	//			    	HYPubBO_Client.saveBD(billvo, null);
	//			    }
	//				
	//			} catch (Exception e) {
	//				e.printStackTrace();
	//				MessageDialog.showHintDlg(getBillUI(), "��ʾ", e.getMessage());
	//			}
	//		} catch (BusinessException e1) {
	//			e1.printStackTrace();
	//			MessageDialog.showHintDlg(getBillUI(), "��ʾ", e1.getMessage());
	//			return;
	//		}
	//
	//	}


	@Override
	protected void onBoElse(int btn) throws Exception {
		switch (btn) {
		case IHRPBtn.ExcelOut:
			onBoExcelOut();
			break;
//		case IHRPBtn.ExcelImport:
//			onBoExcelImport();
//			break;
		case IHRPBtn.COMMITREURN:
			onCommitReturn();
			break;
		case IHRPBtn.FileManage:
			onBoFileManage();
			break;
		case IHRPWABtn.QUERYDEPTMNY:
			onBoQueryDept();
			break;
		default:
			break;
		}
	}
	@Override
	protected void onBoLineIns() throws Exception {
		String code = getBillCardPanelWrapper().getBillCardPanel().getCurrentBodyTableCode();
		if(code.equals("wa_deptsum")){
			return;
		}
		int nindex = getBillCardPanelWrapper().getBillCardPanel().getHeadItem("nindex").getValueObject()!=null?
				Integer.parseInt(getBillCardPanelWrapper().getBillCardPanel().getHeadItem("nindex").getValueObject().toString()):0;
				if(getQueryDialog().showModal() == QueryConditionClient.ID_OK) {
					ConditionVO[] vos = getQueryDialog().getConditionVO();
					ArrayList<ConditionVO> list = new ArrayList<ConditionVO>();
					if(vos!=null&&vos.length>0){
						for(ConditionVO vo:vos){
							if(vo.getFieldCode().equals("bd_psnbasdoc.sex")&&vo.getValue()!=null&&vo.getValue().trim().equals("ȫ��")){
								continue;
							}else{
								list.add(vo);
							}
						}
					}
					vos = list!=null&&list.size()>0?list.toArray(new ConditionVO[0]):null;

					HashMap<String,String> map = new HashMap<String, String>();
					for(int i=0;i<nindex;i++){
						BillItem item = getBillCardPanelWrapper().getBillCardPanel().getBillModel("wa_psn_item_b").getItemByKey("nmny"+(i+1)+"");
						map.put(item.getKey(), item.getName());
					}
					if(map==null||map.size()<=0) return;
					String sql = getQueryDialog().getWhereSQL(vos);
					if(sql!=null&&sql.trim().length()>0){
						sql = sql.replace("right like", "like");
						sql = sql.replace("left like", "like");
					}
					PfPsnDLG dlg = new PfPsnDLG(this.getBillUI(),sql,map);
					PsnClassItemBVO[] oldvos = (PsnClassItemBVO[])getBillCardPanelWrapper().getBillCardPanel().getBillModel("wa_psn_item_b").getBodyValueVOs(PsnClassItemBVO.class.getName());

					dlg.setOldbvos(oldvos);
					int x = dlg.showModal();
					if(x==UIDialog.ID_OK){
						PsnClassItemBVO[] rvos = dlg.getReturnvos();
						ArrayList<String> list_psn = dlg.getList_psn();
						HashMap<String, UFDouble> map_mny = dlg.getMap_mny();
						if(list_psn!=null&&list_psn.size()>0&&map_mny!=null&&map_mny.size()>0){
							int rowcount = getBillCardPanelWrapper().getBillCardPanel().getBillTable("wa_psn_item_b").getRowCount();
							for(int i=0;i<rowcount;i++){
								Object value = getBillCardPanelWrapper().getBillCardPanel().getBillModel("wa_psn_item_b").getValueAt(i, "pk_psndoc");
								if(value!=null&&value.toString().trim().length()>0&&list_psn.contains(value.toString().trim())){
									String[] keys = map_mny.keySet().toArray(new String[0]);
									for(String key:keys){
										getBillCardPanelWrapper().getBillCardPanel().getBillModel("wa_psn_item_b").setValueAt(map_mny.get(key), i, key);
									}
								}
								UFDouble nmny = new UFDouble(0);
								for(int j=0;j<nindex;j++){
									Object valueb = getBillCardPanelWrapper().getBillCardPanel().getBillModel("wa_psn_item_b").getValueAt(i,"nmny"+(j+1)+"");
									nmny = nmny.add(valueb!=null?new UFDouble(valueb.toString()):new UFDouble(0));
								}
								getBillCardPanelWrapper().getBillCardPanel().getBillModel("wa_psn_item_b").setValueAt(nmny, i, "nmny");
							}
						}
						HashMap<String, ItemVO> mapitem= ((ClientUI)getBillUI()).getMapitem();
						if(rvos!=null&&rvos.length>0){
							for(int i=0;i<rvos.length;i++){
								onBoLineAdd();
								int rows = getBillCardPanelWrapper().getBillCardPanel().getBillTable("wa_psn_item_b").getSelectedRow();
								getBillCardPanelWrapper().getBillCardPanel().getBillModel("wa_psn_item_b").setBodyRowVO(rvos[i], rows);
								getBillCardPanelWrapper().getBillCardPanel().getBillModel("wa_psn_item_b").execEditFormulas(rows);
								UFDouble nmny = new UFDouble(0);
								for(int j=0;j<nindex;j++){
									String pk_item = (String)getBillCardPanelWrapper().getBillCardPanel().getHeadItem("pk_item"+(j+1)+"").getValueObject();
									ItemVO iitemvo = mapitem.get(pk_item);
									Object value = getBillCardPanelWrapper().getBillCardPanel().getBillModel("wa_psn_item_b").getValueAt(rows,"nmny"+(j+1)+"");
									if(iitemvo!=null&&iitemvo.getIproperty()!=null&&iitemvo.getIproperty()==1){
										nmny = nmny.sub(value!=null?new UFDouble(value.toString()):new UFDouble(0));
									}else{
										nmny = nmny.add(value!=null?new UFDouble(value.toString()):new UFDouble(0));
									}
								}
								getBillCardPanelWrapper().getBillCardPanel().getBillModel("wa_psn_item_b").setValueAt(nmny, rows, "nmny");
							}
						}
					}
					((ClientUI)getBillUI()).countHeadMny2();
				}
	}
	private void onBoQueryDept() throws Exception{
		String pk_dept = null;
		String vyear = null;
		String vperiod = null;
		UFDouble totalmoney = null;
		boolean flag = false;
		if(((ClientUI)getBillUI()).isListPanelSelected()){
			int row = ((ClientUI)getBillUI()).getBillListPanel().getHeadTable().getSelectedRow();
			if(row>=0){
				PsnClassItemHVO hvo =(PsnClassItemHVO) ((ClientUI)getBillUI()).getBillListPanel().getHeadBillModel().getBodyValueRowVO(row, PsnClassItemHVO.class.getName());
				pk_dept = hvo.getPk_dept();//(String)((ClientUI)getBillUI()).getBillListPanel().getHeadBillModel().getValueAt(row, "pk_dept");
				vyear = hvo.getVyear();//(String)((ClientUI)getBillUI()).getBillListPanel().getHeadBillModel().getValueAt(row, "vyear");
				vperiod = hvo.getVperiod();//(String)((ClientUI)getBillUI()).getBillListPanel().getHeadBillModel().getValueAt(row, "vperiod");
				totalmoney = hvo.getTotalmoney()!=null?hvo.getTotalmoney():new UFDouble(0);//((ClientUI)getBillUI()).getBillListPanel().getHeadBillModel().getValueAt(row, "totalmoney")!=null?new UFDouble(((ClientUI)getBillUI()).getBillListPanel().getHeadBillModel().getValueAt(row, "totalmoney").toString()):new UFDouble(0);
				flag = hvo.getVbillstatus_audit()!=null&&hvo.getVbillstatus_audit()==WaHrpBillStatus.PASS;//((ClientUI)getBillUI()).getBillListPanel().getHeadBillModel().getValueAt(row, "vbillstatus_audit")!=null&&Integer.parseInt(((ClientUI)getBillUI()).getBillListPanel().getHeadBillModel().getValueAt(row, "vbillstatus_audit").toString())==WaHrpBillStatus.PASS;
			}
		}else{
			pk_dept = (String)((ClientUI)getBillUI()).getBillCardPanel().getHeadItem("pk_dept").getValueObject();
			vyear = (String)((ClientUI)getBillUI()).getBillCardPanel().getHeadItem("vyear").getValueObject();
			vperiod = (String)((ClientUI)getBillUI()).getBillCardPanel().getHeadItem("vperiod").getValueObject();
			totalmoney = ((ClientUI)getBillUI()).getBillCardPanel().getHeadItem("totalmoney").getValueObject()!=null?new UFDouble(((ClientUI)getBillUI()).getBillCardPanel().getHeadItem("totalmoney").getValueObject().toString()):new UFDouble(0);
			flag = ((ClientUI)getBillUI()).getBillCardPanel().getHeadItem("vbillstatus_audit").getValueObject()!=null&&Integer.parseInt(((ClientUI)getBillUI()).getBillCardPanel().getHeadItem("vbillstatus_audit").getValueObject().toString())==WaHrpBillStatus.PASS;
		}
		DeptMnyDLG dlg = new DeptMnyDLG(this.getBillUI(),pk_dept,vyear,vperiod,totalmoney,flag,true);
		dlg.showModal();
	}


	/**
	 * �ļ�����
	 * @author ��ּ�
	 * 2011-3-29����09:20:30
	 */
	protected void onBoFileManage(){
		FileManageUI.showInDlg(getBillUI(), "ģ������","����ά��н����Ŀ");
	}
	/**
	 * @author ��ּ�
	 * 2011-3-21����01:22:34
	 * @throws Exception
	 * ˵�����ύ�س�����
	 */
	private void onCommitReturn() throws Exception{
		HYBillVO billvo = (HYBillVO)getBufferData().getCurrentVO();
		if(billvo==null||billvo.getParentVO()==null) return;
		PsnClassItemHVO hvo = (PsnClassItemHVO)billvo.getParentVO();
		PERIODVO pvo = ((ClientUI)getBillUI()).getWaPeriodVO();
		if(hvo.getPk_wa_period()==null||!hvo.getPk_wa_period().equals(pvo.getPrimaryKey())){
			MessageDialog.showErrorDlg(this.getBillUI(), "�ύ��ʾ", "�ǵ��µ��ݲ��ܲ���");
			return;
		}
		if(hvo.getVcommitid()!=null&&_getOperator().equals(hvo.getVcommitid())){
			int x = MessageDialog.showOkCancelDlg(this.getBillUI(),"�ջ���ʾ","ȷ��Ҫ�ջص�ǰ���ύ���ݣ�");
			if(x!=UIDialog.ID_OK) return;
			onBoEdit();
			getBillCardPanelWrapper().getBillCardPanel().getHeadItem("vcommitid").setValue(null);
			getBillCardPanelWrapper().getBillCardPanel().getHeadItem("dcommitdate").setValue(null);
			getBillCardPanelWrapper().getBillCardPanel().getHeadItem("vbillstatus").setValue(IBillStatus.FREE);
			
			getBillCardPanelWrapper().getBillCardPanel().getHeadItem("pk_item12").setValue(WaHrpBillStatus.WRITE);
			getBillCardPanelWrapper().getBillCardPanel().getHeadItem("pk_item14").setValue(WaHrpBillStatus.WRITE);
			getBillCardPanelWrapper().getBillCardPanel().getHeadItem("vbillstatus_audit").setValue(WaHrpBillStatus.WRITE);
			if(!onBoSaveMySelf(1,"")) {
				onBoCancel();
				return;
			}
		}else{
			MessageDialog.showErrorDlg(this.getBillUI(), "�ջ���ʾ", "��ǰ����Ա���ύ�˲�һ�£������ջ�");
			return;
		}
	}
	private String getOtherDeptMny() throws BusinessException{
		int rowcount = getBillCardPanelWrapper().getBillCardPanel().getBillTable().getRowCount();
		Object value = getBillCardPanelWrapper().getBillCardPanel().getHeadItem("pk_dept").getValueObject();
		String msg = "";
		for(int i=0;i<rowcount;i++){
			Object psndoc = getBillCardPanelWrapper().getBillCardPanel().getBodyValueAt(i,"psncode");
			Object psnname = getBillCardPanelWrapper().getBillCardPanel().getBodyValueAt(i,"psnname");
			Object pk_psndoc = getBillCardPanelWrapper().getBillCardPanel().getBodyValueAt(i,"pk_psndoc");
			Object pk_wa_period = getBillCardPanelWrapper().getBillCardPanel().getBodyValueAt(i,"pk_psndoc");
			String vyear = _getDate().getYear()+"";
			String vperiod = _getDate().toString().substring(5,7);
			String wheresql=" and vyear='"+vyear+"' and vperiod='"+vperiod+"' and fb.pk_psndoc='"+pk_psndoc+"' and fh.pk_dept != '"+value+"'";
			PsnClassItemHVO[] psnvos = gePsnClassItem(wheresql);
			if(psnvos != null){
				if(psnvos.length>0){
					msg +=psnname+"\n";
					//getBillCardPanelWrapper().getBillCardPanel().setBodyValueAt(i,"vdef8");
					//getBillCardPanelWrapper().getBillCardPanel().setBodyValueAt("Y", i, "vdef8", "wa_psn_item_b");
				}
			}
		}
		return msg;
	}
	
	public PsnClassItemHVO[] gePsnClassItem(String sqlwhere) throws BusinessException{
		IUAPQueryBS service = (IUAPQueryBS)NCLocator.getInstance().lookup(IUAPQueryBS.class.getName());
		String sql = "select  fh.pk_dept, fh.pk_wa_period,  fb.pk_psndoc"
			+"  from wa_psn_item_h fh"
			+"  left join wa_psn_item_b fb"
			+"    on fh.pk_psn_item_h = fb.pk_psn_item_h"
			+" where fh.dr = 0"
			+"   and fb.dr = 0"+sqlwhere;
		PsnClassItemHVO[] psnvos = null;
		
		ArrayList<PsnClassItemHVO> list_vo = new ArrayList<PsnClassItemHVO>();
		Vector o1 = (Vector) service.executeQuery(sql,new VectorProcessor());
		if (o1.size() > 0 && o1 != null) {
			psnvos = new PsnClassItemHVO[o1.size()];
			for (int i = 0; i < o1.size(); i++) {
				PsnClassItemHVO psnvo = new PsnClassItemHVO();
				String pk_dept = new String(((Vector) o1.elementAt(i)).elementAt(0) != null ? ((Vector) o1.elementAt(i)).elementAt(0).toString() : "");
				String pk_wa_period = new String(((Vector) o1.elementAt(i)).elementAt(1) != null ? ((Vector) o1.elementAt(i)).elementAt(1).toString() : "");
				String pk_psndoc = new String(((Vector) o1.elementAt(i)).elementAt(2) != null ? ((Vector) o1.elementAt(i)).elementAt(2).toString() : "");
			
				
				
				
				psnvo.setPk_dept(pk_dept);
				psnvo.setPk_wa_period(pk_wa_class);
				psnvo.setPk_psndoc(pk_psndoc);
				
				psnvos[i] = psnvo;
				
		 
			}
		}

		return psnvos;
	}
	//���水ť����ʱ��0������ʱ����1
	private boolean onBoSaveMySelf(int y,String vapprovenote) throws Exception{
		getBillCardPanelWrapper().getBillCardPanel().dataNotNullValidate();
		Object vbatchcode_dept = getBillCardPanelWrapper().getBillCardPanel().getHeadItem("vbatchcode_dept").getValueObject();

		int rowcount = getBillCardPanelWrapper().getBillCardPanel().getBillTable().getRowCount();
		if(rowcount<=0){
			MessageDialog.showErrorDlg(this.getBillUI(), "��ʾ","������ӱ���Ա��Ϣ��");
			return false;
		}
		String ndeptmny_after = getBillCardPanelWrapper().getBillCardPanel().getHeadItem("ndeptmny_after").getValue();
		UFDouble ddeptmny_after = new UFDouble(ndeptmny_after);
		if(ddeptmny_after.compareTo(new UFDouble("0.00")) > 0){
			MessageDialog.showErrorDlg(this.getBillUI(), "��ʾ","����Ϊ"+ddeptmny_after+",�����·��䣡");
			return false;
		}
		
		getBillCardPanelWrapper().getBillCardPanel().getHeadItem("nrowcount").setValue(rowcount);
		Object value = getBillCardPanelWrapper().getBillCardPanel().getHeadItem("pk_dept").getValueObject();
		if(vbatchcode_dept==null||vbatchcode_dept.toString().trim().length()<=0){
			PsnClassItemHVO[] oldvos = (PsnClassItemHVO[])HYPubBO_Client.queryByCondition(PsnClassItemHVO.class, " isnull(dr,0)=0 and pk_billtype='63RP' and pk_corp='"+_getCorp().getPrimaryKey()+"' and pk_dept='"+value+"' and vbatchcode_dept is not null order by vbatchcode_dept desc ");
			int batchcode = 1;
			if(oldvos!=null&&oldvos.length>0){
				batchcode = oldvos[0].getVbatchcode_dept()!=null?oldvos[0].getVbatchcode_dept()+1:1;
			}
			getBillCardPanelWrapper().getBillCardPanel().getHeadItem("vbatchcode_dept").setValue(batchcode);
		}
		ArrayList<String> list = new ArrayList<String>();
		//����������  
		UFDouble totalMoney = new UFDouble(0);  //������
		UFDouble nmny1TotalMoney = new UFDouble(0);  //�¿��˽��ϼ�
		UFDouble nmny2TotalMoney = new UFDouble(0);  //һ���Խ���ϼ�
		String msg = "";
		//String msg2 = "";
		String vyear = _getDate().getYear()+"";
		String vperiod = _getDate().toString().substring(5,7);
		String pk_dept = getBillCardPanelWrapper().getBillCardPanel().getHeadItem("pk_dept").getValueObject().toString().trim();
		String hid = (String)getBillCardPanelWrapper().getBillCardPanel().getHeadItem("pk_psn_item_h").getValueObject();
		
		//��������ʱУ��ͬһ����ͬһ�ڼ�ֻ�ܴ���һ������
		IHRWaServices service =  NCLocator.getInstance().lookup(IHRWaServices.class);
		String pk_corp = ClientEnvironment.getInstance().getCorporation().getPk_corp();
		if(hid == null || hid.trim().length() <= 0){
			boolean valiflag = service.valiisDeptbillhas(pk_corp, pk_dept, vyear, vperiod);
			if(valiflag){
				MessageDialog.showErrorDlg(this.getBillUI(), "��ʾ", "����ʧ�ܣ�ͬһ����ͬһ�ڼ�ֻ������һ�����ݣ����ѯ���޸�!");
				return false;
			}
		}
		//�Ƿ����������:vdef19, 01��ʾ��������03��ʾ����
		getBillCardPanelWrapper().getBillCardPanel().getHeadItem("vdef8").setValue("01");
		//��¼�������б���Ա������Ϣ
		List<String> pkpsndocList = new ArrayList<String>();
		String awardmsg = "";
		for(int i=0;i<rowcount;i++){
			getBillCardPanelWrapper().getBillCardPanel().setBodyValueAt(value, i, "pk_wa_dept");
			Object obj = getBillCardPanelWrapper().getBillCardPanel().getBodyValueAt(i,"nmny");
			totalMoney = totalMoney.add(obj!=null?new UFDouble(obj.toString(),2):new UFDouble(0));
			
			Object obj1 = getBillCardPanelWrapper().getBillCardPanel().getBodyValueAt(i,"nmny1");
			if(obj1 != null){
				UFDouble nmny1 = new UFDouble(obj1.toString(), 2);
				if(nmny1.doubleValue() >= 0){
					nmny1TotalMoney = nmny1TotalMoney.add(nmny1);
				}else{
					MessageDialog.showErrorDlg(this.getBillUI(), "��ʾ", "�������¿��˽�����Ϊ����!");
					return false;
				}
			}
			//nmny1TotalMoney = nmny1TotalMoney.add(obj1!=null?new UFDouble(obj1.toString(),2):new UFDouble(0));
			
			Object obj2 = getBillCardPanelWrapper().getBillCardPanel().getBodyValueAt(i,"nmny2");
			if(obj2 != null){
				UFDouble nmny2 = new UFDouble(obj2.toString(), 2);
				if(nmny2.doubleValue() >= 0){
					nmny2TotalMoney = nmny2TotalMoney.add(nmny2);
				}else{
					MessageDialog.showErrorDlg(this.getBillUI(), "��ʾ", "������һ���Խ�����Ϊ����!");
					return false;
				}
			}
			//nmny2TotalMoney = nmny2TotalMoney.add(obj2!=null?new UFDouble(obj2.toString(),2):new UFDouble(0));
			
			Object psndoc = getBillCardPanelWrapper().getBillCardPanel().getBodyValueAt(i,"psncode");
			Object pk_psndoc = getBillCardPanelWrapper().getBillCardPanel().getBodyValueAt(i,"pk_psndoc");
			String sql = " isnull(dr,0)=0 and pk_psndoc='"+pk_psndoc+"' and vyear='"+vyear+"' and vperiod='"+vperiod+"' and pk_dept!='"+pk_dept+"' ";
			
			//PsnClassItemBBVO[] bbvos = (PsnClassItemBBVO[])HYPubBO_Client.queryByCondition(PsnClassItemBBVO.class,sql );
			//if(bbvos!=null&&bbvos.length>0){
			//	msg2+=""+psndoc+",";
			//}
			if(psndoc!=null&&psndoc.toString().trim().length()>0 ){
				if(list.contains(psndoc.toString().trim())){
					msg+=""+psndoc+",";
				}else{
					list.add(psndoc.toString().trim());
				}
			}
			
			if(pk_psndoc != null && !pkpsndocList.contains(pk_psndoc)){
				pkpsndocList.add(pk_psndoc.toString());
			}
			
			//У��������������Ƿ����������,begin
			Object vdef17 = getBillCardPanelWrapper().getBillCardPanel().getBodyValueAt(i,"vdef17");
			if(vdef17 != null && "03".equals(vdef17.toString())){
				//�Ƿ������������01��ʾ��������03��ʾ����
				getBillCardPanelWrapper().getBillCardPanel().getHeadItem("vdef8").setValue("03");
			}
			
		}
		
		if(pkpsndocList != null && !pkpsndocList.isEmpty()){
			//У������������е���Ա�Ƿ������������ѷ����� begin
			String retMsg = service.valiOtherPeriodDept(pk_corp, pk_dept, vyear, vperiod, pkpsndocList);
			if(retMsg != null && retMsg.trim().length() > 0){
				int ok =  MessageDialog.showOkCancelDlg(this.getBillUI(), "��ʾ", retMsg + " ���������������з�����Ƿ������");
				if(ok!=UIDialog.ID_OK) return false;
			}
			//У������������е���Ա�Ƿ������������ѷ����� begin
		}
		
		
		if(msg!=null&&msg.trim().length()>0){
			MessageDialog.showErrorDlg(this.getBillUI(), "������ʾ", "����Ա�����ظ���"+msg.substring(0,msg.length()-1)+",����");
			return false;
		}
		
		//if(msg2!=null&&msg2.trim().length()>0){
		//	MessageDialog.showErrorDlg(this.getBillUI(), "������ʾ", "����Ա����"+msg2.substring(0,msg2.length()-1)+"���������з��š�");
		//}

		//У���ͷ�����Ƿ��������֧������   qiutian  2012-05-28
		
		String pk_wa_class = getBillCardPanelWrapper().getBillCardPanel().getHeadItem("pk_wa_class").getValueObject().toString().trim();
		//��ͷ�����Լ��¼�����


		PerioddeptVO pfvo = (PerioddeptVO)HYPubBO_Client.queryByPrimaryKey(PerioddeptVO.class, pk_dept);
		//DeptMnyHVO[] dptvos =(DeptMnyHVO[]) HYPubBO_Client.queryByCondition(DeptMnyHVO.class, " isnull(dr,0)=0 and pk_dept='"+pk_dept+"' and pk_billtype=1 ");
		//UFDouble nmny = dptvos != null && dptvos.length > 0 && dptvos[0].getNmny()!=null?dptvos[0].getNmny():new UFDouble(0);
		//ԭ�����߼�
//		UFDouble totalmoney = getBillCardPanelWrapper().getBillCardPanel().getHeadItem("totalmoney").getValueObject()!=null?
//				new UFDouble(getBillCardPanelWrapper().getBillCardPanel().getHeadItem("totalmoney").getValueObject().toString()):new UFDouble(0);
//		if(totalMoney!=null&&totalMoney.doubleValue()>0){
//			if(totalMoney.doubleValue()>nmny.doubleValue()&&(pfvo.getBischaoe()==null||!pfvo.getBischaoe().booleanValue())){
//				MessageDialog.showHintDlg(this.getBillUI(), "��ʾ", "��ǰ���Ų��ܳ����");
//				return false;
//			}
//		}
		UFDouble nmny = null;
		//�����ڳ�(�������������ں������ύʱУ�����)
		if(getBillCardPanelWrapper().getBillCardPanel().getHeadItem("nbyqc").getValueObject() != null){
			nmny = new UFDouble(getBillCardPanelWrapper().getBillCardPanel().getHeadItem("nbyqc").getValueObject().toString());
		}else{
			nmny = new UFDouble(0);
		}
		if(y!=-1){
			//-1:����ʱ��У������, 0:�ύʱУ������
			if(!(pfvo.getBisliucun() != null && pfvo.getBisliucun().booleanValue())){
				if(nmny.doubleValue()>totalMoney.doubleValue()){
					MessageDialog.showErrorDlg(this.getBillUI(), "��ʾ", "��ǰ���Ҳ������棡");
					return false;
				}
			}
		}
		
		//�´����߼�begin tianxfc
		
		//-1:����ʱ��У�鳬���, 0:�ύʱУ�鳬���
		if(pfvo.getBischaoe() != null && !pfvo.getBischaoe().booleanValue()){
			if(totalMoney != null && totalMoney.doubleValue() > nmny.doubleValue()){
				MessageDialog.showErrorDlg(this.getBillUI(), "��ʾ", "��ǰ���Ҳ��ܳ���ţ�");
				return false;
			}
		}
		
		//һ���Խ���У�飺һ���Խ����ܶ�=��Чϵͳˢ������һ���Խ��� + ����һ���Խ����� - ����һ���Խ�����
		UFDouble vdef13TotalMoney = getBillCardPanelWrapper().getBillCardPanel().getHeadItem("vdef13").getValueObject() != null?
				new UFDouble(getBillCardPanelWrapper().getBillCardPanel().getHeadItem("vdef13").getValueObject().toString()) : new UFDouble(0);
		UFDouble vdef17 = getBillCardPanelWrapper().getBillCardPanel().getHeadItem("vdef17").getValueObject() != null?
				new UFDouble(getBillCardPanelWrapper().getBillCardPanel().getHeadItem("vdef17").getValueObject().toString()) : new UFDouble(0);
		UFDouble vdef18 = getBillCardPanelWrapper().getBillCardPanel().getHeadItem("vdef18").getValueObject() != null?
								new UFDouble(getBillCardPanelWrapper().getBillCardPanel().getHeadItem("vdef18").getValueObject().toString()) : new UFDouble(0);
		vdef13TotalMoney = vdef13TotalMoney.add(vdef17).sub(vdef18);
		if(nmny2TotalMoney != null && nmny2TotalMoney.doubleValue() > 0){
			if(nmny2TotalMoney.doubleValue() > vdef13TotalMoney.doubleValue()){
				MessageDialog.showErrorDlg(this.getBillUI(), "��ʾ", "��ǰ����һ���Խ����ܳ���ţ�");
				return false;
			}
		}
		
		//�¿��˽�У�飺һ���Խ����ܶ�=��Чϵͳˢ������һ���Խ��� + ����һ���Խ����� - ����һ���Խ�����
		UFDouble ykhjTotal = nmny.sub(vdef13TotalMoney);
		if(nmny2TotalMoney != null && nmny2TotalMoney.doubleValue() > 0){
			if(nmny1TotalMoney.doubleValue() > ykhjTotal.doubleValue()){
				MessageDialog.showErrorDlg(this.getBillUI(), "��ʾ", "��ǰ�����¿��˽����ܳ���ţ�");
				return false;
			}
		}
		
		//У��ͨ����Ϊ���������ࡿ�ֶθ�ֵ
//		getBillCardPanelWrapper().getBillCardPanel().getBodyItem(tableCode, strKey)
//		UFDouble totalmoney = getBillCardPanelWrapper().getBillCardPanel().getHeadItem("totalmoney").getValueObject()!=null?
//				new UFDouble(getBillCardPanelWrapper().getBillCardPanel().getHeadItem("totalmoney").getValueObject().toString()):new UFDouble(0);
//		UFDouble curPeriodJY = totalmoney;
//		getBillCardPanelWrapper().getBillCardPanel().getHeadItem("ndeptmny_after").setValue(curPeriodJY);//������½��ࣨ������棩
		
		//�´����߼�end tianxfc
		
		//end  qiutian  2012-05-29
		Object period = getBillCardPanelWrapper().getBillCardPanel().getHeadItem("vperiod").getValueObject();
		Object year = getBillCardPanelWrapper().getBillCardPanel().getHeadItem("vyear").getValueObject();
		Object pk = getBillCardPanelWrapper().getBillCardPanel().getHeadItem("pk_psn_item_h").getValueObject();

		if(year!=null&&year.toString().trim().length()>0 &&period!=null&&period.toString().trim().length()>0){
			String wheresql = "pk_dept='"+pk_dept+"' and isnull(dr,0)=0 and pk_wa_class='"+pk_wa_class+"' and vyear='"+year.toString().trim()+"' and vperiod='"+period.toString().trim()+"' ";
			if(pk!=null&&pk.toString().trim().length()>0){
				wheresql+=" and pk_psn_item_h<>'"+pk.toString().trim()+"' ";
			}
			PsnClassItemHVO[] hvos = (PsnClassItemHVO[]) HYPubBO_Client.queryByCondition(PsnClassItemHVO.class, wheresql);
			if(hvos!=null&&hvos.length>0){
				//MessageDialog.showHintDlg(this.getBillUI(), "��ʾ", "��ǰ����н������ڼ��Ѵ��ڵ��ݣ�����");
				//return false;
			}
		}
		//Object vbillstatus_audit = getBillCardPanelWrapper().getBillCardPanel().getHeadItem("vbillstatus_audit").getValueObject();
		//if(vbillstatus_audit!=null&&(Integer.parseInt(vbillstatus_audit.toString())==WaHrpBillStatus.NOPASS_RETURN||Integer.parseInt(vbillstatus_audit.toString())==WaHrpBillStatus.NOPASS)){
		//getBillCardPanelWrapper().getBillCardPanel().getHeadItem("vcommitid").setValue(_getOperator());
		//getBillCardPanelWrapper().getBillCardPanel().getHeadItem("dcommitdate").setValue(_getDate());
		//getBillCardPanelWrapper().getBillCardPanel().getHeadItem("vbillstatus").setValue(IBillStatus.COMMIT);
		//getBillCardPanelWrapper().getBillCardPanel().getHeadItem("vbillstatus_audit").setValue(WaHrpBillStatus.COMMIT);
		//}
		getBillCardPanelWrapper().getBillCardPanel().getHeadItem("vapprovenote").setValue(vapprovenote);
		
		super.onBoSave();
		if(y == -1){
			MessageDialog.showErrorDlg(this.getBillUI(), "��ʾ", "������뼰ʱ�ύ!");
		}
		onBoRefresh();
		return true;
	}
	@Override
	public void onBoExcelOut() {
		BillItem[] items = getBillCardPanelWrapper().getBillCardPanel().getBodyShowItems();
		int rowcount = getBillCardPanelWrapper().getBillCardPanel().getBillTable().getRowCount();
		Object value[][] = new Object[rowcount+1][items.length]; 
		for(int i=0;i<items.length;i++){
			value[0][i] = items[i].getName();
			for(int j=0;j<rowcount;j++){
				if(items[i].getDataType()==4){
					Object tmpvalue = getBillCardPanelWrapper().getBillCardPanel().getBodyValueAt(j,items[i].getKey());
					value[j+1][i] = tmpvalue!=null&&new UFBoolean(tmpvalue.toString())!=null&&new UFBoolean(tmpvalue.toString()).booleanValue()?"��":"��";
				}else{
					value[j+1][i] = getBillCardPanelWrapper().getBillCardPanel().getBodyValueAt(j,items[i].getKey());
				}
			}
		}
		String billno = (String)getBillCardPanelWrapper().getBillCardPanel().getHeadItem("vbillno").getValueObject();
		String billname = (String)getBillCardPanelWrapper().getBillCardPanel().getHeadItem("vbillname").getValueObject();
		HashMap<String,Object[][]> map = new HashMap<String, Object[][]>();
		String vbillname = billno!=null?billno:""+billname!=null?billname:"";
		map.put(vbillname,value);
		ExcelOut excel = new ExcelOut(this.getBillUI());
		excel.createExcelFile(map);
	}
	@Override
	public void onBoExcelImport(){
		//�������ݵȴ���
		String code = getBillCardPanelWrapper().getBillCardPanel().getCurrentBodyTableCode();
		if(!code.equals("wa_psn_item_b")){
			return;
		}
		HashMap<String, ClassitemVO> mapclassitem= ((ClientUI)getBillUI()).getMapclassitem();
		HashMap<String, ItemVO> mapitem= ((ClientUI)getBillUI()).getMapitem();

		ImportExcelData excel = new ImportExcelData();
		try {
			Object[][] values = excel.executeImport();

			if(values==null||values.length<=1) return;
			try {
				IHRwaPub ihrwapub=NCLocator.getInstance().lookup(IHRwaPub.class);
				//				String pk_corp, String waYear, String waPeriod, String stWhere, String tableName,Boolean bflag							
				PsndocVO[] psnvo = (PsndocVO[])HYPubBO_Client.queryByCondition(PsndocVO.class, " isnull(dr,0)=0 and pk_corp='"+_getCorp().getPrimaryKey()+"' and sealdate is null ");
				//				PsndocVO[] psnvos = (PsndocVO[])HYPubBO_Client.queryByCondition(PsndocVO.class," isnull(dr,0)=0 and pk_corp='"+_getCorp().getPrimaryKey()+"' ");
				HashMap<String,PsndocVO> map_psn = new HashMap<String, PsndocVO>();
				HashMap<String,PerioddeptVO> map_dept = new HashMap<String, PerioddeptVO>();
				PerioddeptVO[] deptvos = (PerioddeptVO[])HYPubBO_Client.queryByCondition(PerioddeptVO.class, " isnull(dr,0)=0 and pk_corp='"+_getCorp().getPrimaryKey()+"'");
				if(deptvos!=null&&deptvos.length>0){
					for(int uu=0;uu<deptvos.length;uu++){//�������ƶ���
						map_dept.put(deptvos[uu].getVcode().trim().toLowerCase(), deptvos[uu]);
						map_dept.put(deptvos[uu].getVname().trim().toLowerCase(), deptvos[uu]);
					}
				}
				if(psnvo!=null&&psnvo.length>0){
					for(int uu=0;uu<psnvo.length;uu++){
						PsndocVO vo=psnvo[uu];
						if(vo.getPsncode()!=null&&vo.getPsncode().trim().length()>0){
							map_psn.put(vo.getPsncode().trim().toLowerCase(),vo);
						}
					}
				}

				Object tmpindex = getBillCardPanelWrapper().getBillCardPanel().getHeadItem("nindex").getValueObject();
				int nindex = tmpindex!=null&&tmpindex.toString().trim().length()>0?Integer.parseInt(tmpindex.toString()):0;
				HashMap<String,BillItem> map = new HashMap<String, BillItem>();
				for(int i=0;i<nindex;i++){
					BillItem item= getBillCardPanelWrapper().getBillCardPanel().getBodyItem("nmny"+""+(i+1)+"");
					if(item!=null&&item.getKey()!=null&&item.getName()!=null){
						map.put(item.getName().trim(), item);
					}
				}
				int indexpsncode = -1;
				int indexpsnname = -1;
				int indexitemname = -1;
				int indexerrmess= -1;
				int indexvmemo = -1;
				boolean iserrorlie=false;
				HashMap<String,Integer> mapindex = new HashMap<String, Integer>();
				for(int i=0;i<values[0].length;i++){
					String value = values[0][i]!=null&&values[0][i].toString().trim().length()>0?values[0][i].toString().trim():"";
					if(value.equals("Ա����")){
						indexpsncode = i;
					}else if(value.equals("����")){
						indexpsnname = i;
					}else if(value.equals("��ע˵��")){
						indexvmemo = i;
					}else if(value.equals("������Ϣ��")){
						indexerrmess=i;
						iserrorlie=true;
					}
					if(map.containsKey(value)){
						mapindex.put(value, i);
					}
				}
				if(indexpsncode==-1){
					MessageDialog.showHintDlg(getBillUI(), "��ʾ", "����ѡ���ļ��Ƿ���ȷ��ȱ�ٱ����Ա������");
					return;
				}
				int x = getBillCardPanelWrapper().getBillCardPanel().getBillTable().getRowCount()-1;
				//��¼ÿ�еı�־ �Ƿ�Ϊ �����Ϲ��������  1���������2���������Ա�����Բ���
				ArrayList<String> errorlist=new ArrayList<String>();
				//				values[0][values[0].length+1]="������Ϣ��";
				Object[][] valueexp;
				if(iserrorlie){
					valueexp= values;
				}else{
					valueexp=new Object[values.length][values[0].length+1];
					valueexp[0][values[0].length]="������Ϣ��";
				}
				for(int i=0;i<values.length;i++){
					for(int j=0;j<values[i].length;j++){
						valueexp[i][j]=values[i][j];
					}
				}
				if(indexerrmess==-1){
					indexerrmess=values[0].length;
				}
				for(int i=1;i<values.length;i++){
					Object tmppsn = values[i][indexpsncode];
					//					Object tmpmemo = values[i][indexvmemo];
					Object tmppsnname=values[i][indexpsnname];
					if(tmppsn==null||tmppsn.toString().trim().equals("")){
						errorlist.add("Ա����Ϊ��");
						valueexp[i][indexerrmess]="Ա����Ϊ��";
						continue;
					}
					if(map_psn.get(tmppsn.toString().toLowerCase())==null){
						errorlist.add("���Ա������ϵͳ���Ҳ�����Ӧ����Ա");
						valueexp[i][indexerrmess]="���Ա������ϵͳ���Ҳ�����Ӧ����Ա";
						continue;
					}
					PsndocVO temppsnvo=map_psn.get(tmppsn.toString().toLowerCase());
					if(temppsnvo.getPsnname().equals(tmppsnname)){
						continue;
					}else{
						errorlist.add("�����Ա���ŶԲ��ϣ���˲鵱ǰ��Ա�Ƿ���ȷ");
						valueexp[i][indexerrmess]="�����Ա���ŶԲ��ϣ���˲鵱ǰ��Ա�Ƿ���ȷ";
						continue;
					}
				}

				if(errorlist.size()>0){//����д��� ��дexcel  ���û�ȥ���ġ�
					File file = new File(excel.getPath());
					if (file.exists()) {
						file.delete();
					}
					WritableWorkbook wwb = Workbook.createWorkbook(new File(excel.getPath()));
					WritableSheet wsa = wwb.createSheet("н����Ŀ��Ϣ", 0);
					for(int j = 0;j<valueexp.length;j++){
						for(int k = 0;k<valueexp[j].length;k++){
							String value =  valueexp[j][k]!=null?valueexp[j][k].toString():"";
							Label label00 = new Label(k, j, value);
							wsa.addCell(label00);
						}
					}
					wwb.write();
					wwb.close();
					MessageDialog.showHintDlg(this.getBillUI(),"��ʾ","���´�excel�˲����ݣ�������ʾ�����һ��");
					return;
				}

				for(int i=1;i<values.length;i++){
					Object tmppsn = values[i][indexpsncode];//��Ա����
					Object tmpmemo =indexvmemo==-1?"": values[i][indexvmemo];
					if(tmppsn==null||tmppsn.toString().trim().equals("")){
						break;
					}
					String pk_psndoc = map_psn.get(tmppsn.toString().trim().toLowerCase())!=null?map_psn.get(tmppsn.toString().trim().toLowerCase()).getPk_psndoc():null;
					x++;
					onBoLineAdd();
					getBillCardPanelWrapper().getBillCardPanel().getBillModel("wa_psn_item_b").setValueAt(tmpmemo+"",x,"vmemo");
					getBillCardPanelWrapper().getBillCardPanel().getBillModel("wa_psn_item_b").setValueAt(pk_psndoc,x,"pk_psndoc");
					if(mapindex!=null&&mapindex.size()>0){
						String[] keys = mapindex.keySet().toArray(new String[0]);
						for(int j=0;j<keys.length;j++){
							BillItem item = map.get(keys[j]);
							int index = mapindex.get(keys[j]);
							Object tmpnmny = values[i][index];
							try{
								UFDouble nmny = tmpnmny!=null?new UFDouble(tmpnmny.toString()):null;
								getBillCardPanelWrapper().getBillCardPanel().getBillModel("wa_psn_item_b").setValueAt(nmny,x,item.getKey());
							}catch(Exception e){
								//
							}
						}
					}
					getBillCardPanelWrapper().getBillCardPanel().getBillModel("wa_psn_item_b").execEditFormulas(x);
				}

				int rowcount = getBillCardPanelWrapper().getBillCardPanel().getBillTable("wa_psn_item_b").getRowCount();
				int index = getBillCardPanelWrapper().getBillCardPanel().getHeadItem("nindex").getValueObject()!=null?
						Integer.parseInt(getBillCardPanelWrapper().getBillCardPanel().getHeadItem("nindex").getValueObject().toString()):0;
						UFDouble totalMoney = new UFDouble(0);  //������
						for(int i=0;i<rowcount;i++){

							UFDouble nmny = new UFDouble(0);
							UFDouble nmny2 = new UFDouble(0);
							for(int j=0;j<index;j++){
								Object value = getBillCardPanelWrapper().getBillCardPanel().getBillModel("wa_psn_item_b").getValueAt(i,"nmny"+(j+1)+"");
								String pk_item = (String)getBillCardPanelWrapper().getBillCardPanel().getHeadItem("pk_item"+(j+1)+"").getValueObject();
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
							getBillCardPanelWrapper().getBillCardPanel().getBillModel("wa_psn_item_b").setValueAt(nmny, i, "nmny");
							getBillCardPanelWrapper().getBillCardPanel().getBillModel("wa_psn_item_b").setValueAt(nmny, i, "naftersmny");
							getBillCardPanelWrapper().getBillCardPanel().getBillModel("wa_psn_item_b").setValueAt(nmny2, i, "nbcnsmny");
							totalMoney = totalMoney.add(nmny);
						}
						getBillCardPanelWrapper().getBillCardPanel().getHeadItem("totalmoney").setValue(totalMoney);
			} catch (UifException e) {
				e.printStackTrace();
				MessageDialog.showHintDlg(getBillUI(), "��ʾ", e.getMessage());
			} catch (BusinessException e) {
				e.printStackTrace();
				MessageDialog.showHintDlg(getBillUI(), "��ʾ", e.getMessage());
			} catch (Exception e) {
				e.printStackTrace();
				MessageDialog.showHintDlg(getBillUI(), "��ʾ", e.getMessage());
			}

			//			} catch(Exception e) {
			//			e.printStackTrace();
			//			getBillUI().showErrorMessage(e.getMessage());
			//			} finally {
			//			//����ϵͳ������ʾ��
			//			dialog.end();
			//			}
			//			}
			//			};
			////			�����߳�
			//			new Thread(checkRun).start();
		} catch (BusinessException e1) {
			e1.printStackTrace();
			MessageDialog.showHintDlg(getBillUI(), "��ʾ", e1.getMessage());
			return;
		}

	}

	public MyQueryConditionClient getQueryDialog() {
		if (m_queryDialog == null) {
			m_queryDialog = new MyQueryConditionClient(this.getBillUI());
			getQueryDialog().setTempletID("0001AA10000000018FLC");
			//			getQueryDialog().setTempletID(_getCorp().getPrimaryKey(), "HY020301", _getOperator(), "PSNQUERY");
			getQueryDialog().hideNormal();
		}
		return m_queryDialog;
	}

	@Override
	protected void onBoEdit() throws Exception {
		super.onBoEdit();
		getBillCardPanelWrapper().getBillCardPanel().getHeadItem("vbillstatus").setValue(IBillStatus.FREE);
		getBillCardPanelWrapper().getBillCardPanel().getHeadItem("pk_item12").setValue(WaHrpBillStatus.WRITE);
		getBillCardPanelWrapper().getBillCardPanel().getHeadItem("pk_item14").setValue(WaHrpBillStatus.WRITE);
		getBillCardPanelWrapper().getBillCardPanel().getHeadItem("vbillstatus_audit").setValue(WaHrpBillStatus.WRITE);
		getBillCardPanelWrapper().getBillCardPanel().getHeadItem("pk_dept").setEnabled(false);
		
		getBillCardPanelWrapper().getBillCardPanel().getHeadItem("pk_dept").setEnabled(true);
		String pk_dept_in = getBillCardPanelWrapper().getBillCardPanel().getHeadItem("pk_dept").getValue();

		
		ClientUI ui =(ClientUI) getBillUI();
		
		ui.setDeptEdit();
		
		
	}
	@Override
	public void onBoAdd(ButtonObject bo) throws Exception {
		super.onBoAdd(bo);
		getBillCardPanelWrapper().getBillCardPanel().getHeadItem("pk_dept").setEnabled(true);
		String pk_dept_in = getBillCardPanelWrapper().getBillCardPanel().getHeadItem("pk_dept").getValue();
		
		/***************************************************************/
		// ���������ҷ���Ľ��
		String curdate = _getDate().toString().substring(0,7);
		String period=curdate.substring(5);
		String year=curdate.substring(0,4);
		DeptChargeHVO[] chargevo = (DeptChargeHVO[]) HYPubBO_Client.queryByCondition(DeptChargeHVO.class, 
				" isnull(dr,0)=0 and pk_dept_in='"+pk_dept_in+"' and vyear='"+year+"' and vperiod='"+period+"'");
		if(chargevo!=null&&chargevo.length>0){
			UFDouble sumnmny = new UFDouble("0.00");
			String deptname="";
			for(int i=0;i<chargevo.length;i++){
				sumnmny = sumnmny.add(chargevo[i].getNmny());
				PerioddeptVO[] deptvo = (PerioddeptVO[]) HYPubBO_Client.queryByCondition(PerioddeptVO.class, 
						" isnull(dr,0)=0 and pk_perioddept='"+chargevo[i].getPk_dept_out()+"'");
				if(deptvo != null){
					if(deptvo.length >0){
						deptname += deptvo[0].getVname() + "\n";
						
					}
					
				}
			}
			
			MessageDialog.showErrorDlg(this.getBillUI(), "��ʾ", "��ǰ������:"+deptname+"ת����Ϊ"+sumnmny.intValue()+"Ԫ��");
		}
		/***************************************************************/
		plottable();
		//		����н����Ŀ
		HashMap<String,String> mapkeytoitem = ((ClientUI)getBillUI()).getMapkeytoitem();
		if(mapkeytoitem!=null&&mapkeytoitem.size()>0){
			String[] keys = mapkeytoitem.keySet().toArray(new String[0]);
			for(String key:keys){
				getBillCardPanelWrapper().getBillCardPanel().setHeadItem(key,mapkeytoitem.get(key));
			}
			getBillCardPanelWrapper().getBillCardPanel().setHeadItem("nindex",mapkeytoitem.size());
		}else{
			getBillCardPanelWrapper().getBillCardPanel().setHeadItem("nindex",0);
		}
		//		((ClientUI)getBillUI()).setDeptSum(((ClientUI)getBillUI()).getPk_deptid());

		addPsn();
	}
	/**
	 *  ����Ĭ����Ա
	 * @throws Exception
	 */
	public void addPsn() throws Exception{
		//PsnClassItemHVO[] hvos = (PsnClassItemHVO[])HYPubBO_Client.queryByCondition(PsnClassItemHVO.class, 
		//		" voperatorid='"+_getOperator()+"' and  pk_billtype='63RP' and pk_wa_class='"+IHRPWABtn.PK_JIANG+"' and isnull(bisjiangjin,'N')='N' and isnull(dr,0)=0 and pk_corp='"+_getCorp().getPrimaryKey()+"' and pk_dept='"+((ClientUI)getBillUI()).getPk_deptid()+"' and vbillstatus_audit="+WaHrpBillStatus.PASS+" order by vbatchcode_dept desc  ");
		
		
//		PsnClassItemHVO[] hvos = (PsnClassItemHVO[])HYPubBO_Client.queryByCondition(PsnClassItemHVO.class, 
//				" voperatorid='" + _getOperator() + "' and  pk_billtype='63RP' and pk_wa_class='" + IHRPWABtn.PK_JIANG + "' and isnull(bisjiangjin,'N')='N' and isnull(dr,0)=0 and pk_corp='" + _getCorp().getPrimaryKey() + "' and pk_dept='" + ((ClientUI)getBillUI()).getPk_deptid() + "' and pk_item12 = " + WaHrpBillStatus.PASS + " order by vbatchcode_dept desc  ");
//		ArrayList<String> list = new ArrayList<String>();
//		if(hvos!=null&&hvos.length>0){
//			PsnClassItemBVO[] bvos = (PsnClassItemBVO[])HYPubBO_Client.queryByCondition(PsnClassItemBVO.class, " isnull(dr,0)=0 and pk_psn_item_h='"+hvos[0].getPrimaryKey()+"' ");
//			if(bvos!=null&&bvos.length>0){
//				for(PsnClassItemBVO bvo:bvos){
//					if(!list.contains(bvo.getPk_psndoc())){
//						list.add(bvo.getPk_psndoc());
//					}
//				}
//			}
//		}
//		if(list==null||list.size()<=0){
//			PsndocVO[] docvos = (PsndocVO[])HYPubBO_Client.queryByCondition(PsndocVO.class, " isnull(dr,0)=0 and pk_corp='"+_getCorp().getPrimaryKey()+"' and groupdef36='"+((ClientUI)getBillUI()).getPk_deptid()+"' ");
//			if(docvos!=null&&docvos.length>0){
//				for(PsndocVO bvo:docvos){
//					if(!list.contains(bvo.getPk_psndoc())){
//						list.add(bvo.getPk_psndoc());
//					}
//				}
//			}
//		}
//		for(int i=0;i<list.size();i++){
//			onBoLineAdd();
//			getBillCardPanelWrapper().getBillCardPanel().getBillModel("wa_psn_item_b").setValueAt(list.get(i),i, "pk_psndoc");
//		}
		
		
		
		//����Ĭ����Ա�б�begin tianxfc
		String pk_corp = _getCorp().getPrimaryKey();
		//������Ҳ���������
		String pk_perioddept = ((ClientUI)getBillUI()).getPk_deptid();
		//�����ڼ�
		PERIODVO waPeriodVO  =((ClientUI)getBillUI()).getWaPeriodVO();
		String vyear = waPeriodVO.getCyear();
		String vperiod = waPeriodVO.getCperiod();
		//String dstartdate = waPeriodVO.getCstartdate().toString();
		//String denddate = waPeriodVO.getCenddate().toString();
		
		IHRWaServices service =  NCLocator.getInstance().lookup(IHRWaServices.class);
		//�����ڼ����¡���֯pk������״̬��ѯ������ҽ�����������ֵ� key��������ұ��룬 value�������������vo
		if(fenpeiItemBVOMap == null){
			fenpeiItemBVOMap = service.queryFenpeiItemBVOMapByCond(waPeriodVO.getCyear(), waPeriodVO.getVcalmonth(), _getCorp().getPrimaryKey());
		}
		if(fenpeiItemBVOMap == null || fenpeiItemBVOMap.isEmpty()){
			MessageDialog.showErrorDlg(this.getBillUI(), "��ʾ", "��ǰ�ڼ�û�п��ҽ������ݣ�");
			hasFlag = false;
			return;
		}else{
			hasFlag = true;
		}
		
		if(pk_perioddept != null){
			
			Map<String, String> map = service.queryPsnMapByCond(pk_corp, pk_perioddept, vyear, vperiod);
			if(map != null && !map.isEmpty()){
				String[] arr = map.keySet().toArray(new String[0]);
				for(int i=0; i<arr.length;i++){
					//ѡ�񽱽���Һ󣬶�̬�Ĵ����ÿ��ҺͶ��ڵĿ����ڼ�ǩ����Ա����Ϣ��ֱ��Ĭ����������
					onBoLineAdd();
					getBillCardPanelWrapper().getBillCardPanel().getBillModel("wa_psn_item_b").setValueAt(arr[i], i, "pk_psndoc");
					getBillCardPanelWrapper().getBillCardPanel().getBillModel("wa_psn_item_b").setValueAt(1, i, "stafftype");
					//���ÿ��ڿ���pk
					getBillCardPanelWrapper().getBillCardPanel().getBillModel("wa_psn_item_b").setValueAt(map.get(arr[i]), i, "vdef18");
				}
			}
//			List<String> pkpsnList = service.queryAllPsndocVOByCond(pk_corp, pk_perioddept, vyear, vperiod);
//			if(pkpsnList != null && !pkpsnList.isEmpty()){
//				for(int i=0;i<pkpsnList.size();i++){
//					//ѡ�񽱽���Һ󣬶�̬�Ĵ����ÿ��ҺͶ��ڵĿ����ڼ�ǩ����Ա����Ϣ��ֱ��Ĭ����������
//					onBoLineAdd();
//					getBillCardPanelWrapper().getBillCardPanel().getBillModel("wa_psn_item_b").setValueAt(pkpsnList.get(i), i, "pk_psndoc");
//				}
//			}
		}
		//����Ĭ����Ա�б�end tianxfc
		
		
		getBillCardPanelWrapper().getBillCardPanel().getBillModel("wa_psn_item_b").execLoadFormula();
	}
	public void plottable() {
		//��н����Ŀ��������Ϣ�Ĳ�������
		ItemSetBVO[] itemvos = ((ClientUI)getBillUI()).getItemvos();
		HashMap<String,ItemVO> mapitem = ((ClientUI)getBillUI()).getMapitem();
		HashMap<String,String> mapkeytoitem = ((ClientUI)getBillUI()).getMapkeytoitem();
		ArrayList<String> list_power_item = ((ClientUI)getBillUI()).getList_power_item();
		if(list_power_item!=null&&list_power_item.size()>0){
			ArrayList<ItemSetBVO> listset = new ArrayList<ItemSetBVO>();
			for(ItemSetBVO bvo:itemvos){
				if(list_power_item.contains(bvo.getPk_item())){
					listset.add(bvo);
				}
			}
			itemvos = listset!=null?listset.toArray(new ItemSetBVO[0]):null;
		}
		mapkeytoitem.clear();

		//������  �ٸ���������ʾ
		BillItem[] showItems = getBillCardPanelWrapper().getBillCardPanel().getBillModel("wa_psn_item_b").getBodyItems();
		for(int i=0;i<showItems.length;i++){
			BillItem item = showItems[i];
			if(item!=null&&item.getKey()!=null&&item.getKey().startsWith("nmny")&&!item.getKey().equals("nmny")){
				item.setShow(false);
			}
		}

		for(int i=0;i<itemvos.length;i++){
			BillItem item = getBillCardPanelWrapper().getBillCardPanel().getBillModel("wa_psn_item_b").getItemByKey("nmny"+""+(i+1)+"");
			if(item!=null&&item.getKey()!=null){
				mapkeytoitem.put("pk_item"+""+(i+1)+"",itemvos[i].getPk_item());
				item.setShow(true);
				item.setWidth(70);
				item.setName(mapitem.get(itemvos[i].getPk_item())!=null?mapitem.get(itemvos[i].getPk_item()).getVname():item.getName());
			}
		}
		((ClientUI)getBillUI()).setMapkeytoitem(mapkeytoitem);
		if(((ClientUI)getBillUI()).lastbillstatus==IBillOperate.OP_INIT){
			getBillCardPanelWrapper().getBillCardPanel().setBillData(getBillCardPanelWrapper().getBillCardPanel().getBillData());
			((ClientUI)getBillUI()).updateUI();
		}

		getBillCardPanelWrapper().getBillCardPanel().setBillData(getBillCardPanelWrapper().getBillCardPanel().getBillData());

	}
	@Override
	public void onButton(ButtonObject bo) {
		((ClientUI)getBillUI()).lastbillstatus =((ClientUI)getBillUI()).getBillOperate();
		super.onButton(bo);
	}
	@Override
	protected void onBoDelete() throws Exception {
		super.onBoDelete();
	}
	@Override
	protected void onBoPrint() throws Exception {
		nc.ui.pub.print.IDataSource dataSource = new MyCardPanelPRTS(getBillUI()
				._getModuleCode(), getBillCardPanelWrapper().getBillCardPanel());
		nc.ui.pub.print.PrintEntry print = new nc.ui.pub.print.PrintEntry(null,
				dataSource);
		print.setTemplateID(getBillUI()._getCorp().getPrimaryKey(), getBillUI()
				._getModuleCode(), getBillUI()._getOperator(), getBillUI()
				.getBusinessType(), getBillUI().getNodeKey());
		if (print.selectTemplate() == 1)
			print.preview();
	}
	@Override
	protected void onBoSave() throws Exception {
		if(hasFlag){
			String msg = getOtherDeptMny();
			if(!msg.equals("")){
				MessageDialog.showErrorDlg(this.getBillUI(), "��ʾ", "��ǰ�ڼ�������Ա:" +msg+"�Ѿ����������ҷ��ţ�");
				
			}
			if(!onBoSaveMySelf(-1,"���η����д��")) return;
		}else{
			MessageDialog.showErrorDlg(this.getBillUI(), "��ʾ", "��ǰ�ڼ�û�п��ҽ������ݣ�");
			return;
		}
	}
	@Override
	protected void onBoLineAdd() throws Exception {
		String code = getBillCardPanelWrapper().getBillCardPanel().getCurrentBodyTableCode();
		if(code.equals("wa_deptsum")){
			return;
		}
		super.onBoLineAdd();
		int row = getBillCardPanelWrapper().getBillCardPanel().getBillTable().getSelectedRow();
		getBillCardPanelWrapper().getBillCardPanel().setBodyValueAt(_getCorp().getPrimaryKey(),row, "pk_corp");
		getBillCardPanelWrapper().getBillCardPanel().setBodyValueAt(((ClientUI)getBillUI()).getPk_deptid(),row, "pk_dept");
		getBillCardPanelWrapper().getBillCardPanel().setBodyValueAt(((ClientUI)getBillUI()).getPk_deptid(),row, "pk_wa_dept");
		//���ڿ��ң���Ԥ���ֶ�vdef16
		//getBillCardPanelWrapper().getBillCardPanel().getBillModel("wa_psn_item_b").setValueAt(((ClientUI)getBillUI()).getPk_deptid(), row, "vdef16");
	}
	
	protected void onBoQueryInit() throws Exception {
		//String strWhere = new StringBuffer();
	
		String userid = _getOperator();
		String pk_corp=_getCorp().getPrimaryKey();
		String pk_module=IhrpNCModule.HRWA; 
		String curedate = ClientEnvironment.getInstance().getDate().toString().substring(0, 7);
		String strWhere = " (1=1) and (isnull(dr,0)=0) and pk_corp='"+pk_corp+"' and pk_wa_period = ( select pk_wa_period from wa_period where cyear||'-'||cperiod='"+curedate+"' and dr=0) ";


		String wheresql = strWhere.toString()+" and  "+" pk_dept in(" +
		" select pk_deptdoc from bd_wa_userdept where " +
		"   isnull(dr,0)=0 and pk_user='"+_getOperator()+"') ";


		UserAndClerkVO[] uservo = (UserAndClerkVO[]) HYPubBO_Client.queryByCondition(UserAndClerkVO.class, " isnull(dr,0)=0 and userid='"+userid+"' ");
		if(uservo!=null&&uservo.length>0){
			String pk_psn = uservo[0].getPk_psndoc();
			PsndocVO[] docvos = (PsndocVO[])HYPubBO_Client.queryByCondition(PsndocVO.class,
					" pk_corp='"+_getCorp().getPrimaryKey()+"' and isnull(dr,0)=0 and pk_psnbasdoc='"+pk_psn+"' and groupdef51='000169100000001051BE' ");
			if(docvos!=null&&docvos.length>0){//����

			}else{
				wheresql+=" and voperatorid='"+_getOperator()+"' ";
			}
		}else{
			wheresql+=" and voperatorid='"+_getOperator()+"' ";
		}

		//����where��䣬����Ȩ�޹���
		String waclssSql = " and  pk_billtype='63RP' and isnull(bisnianzhong,'N')='N' and pk_wa_class='"+IHRPWABtn.PK_JIANG+"' and isnull(bisjiangjin,'N')='N' and pk_wa_class in" +
		"((select classid from wa_uclsright " +
		" where pk_corp = '"+_getCorp().getPrimaryKey()+"' and cuserid = '"+_getOperator()+"' and moduleflag = 0) " +
		" union " +
		" (select classid from wa_gclsright where cgroupid in " +
		" (select pk_role from sm_user_role " +
		" where sm_user_role.cuserid = '"+_getOperator()+"') and pk_corp = '"+_getCorp().getPrimaryKey()+"' and moduleflag = 0))";


		SuperVO[] queryVos = queryHeadVOs(wheresql+" "+waclssSql);

		getBufferData().clear();
		// �������ݵ�Buffer
		addDataToBuffer(queryVos);
		updateBuffer();
		int currRow = getBufferData().getCurrentRow();
		getBufferData().setCurrentRow(currRow);
		//=================2012.11.14 �޸� sqt  ���ڵ����ѯ֮��ť״̬�����½���ˢ��=beg==================
		if(((ClientUI)this.getBillUI()).pk_deptid==null||!((ClientUI)this.getBillUI()).iscan){
			//MessageDialog.showHintDlg(this, "��ʾ", "��ǰ�û�δ����ҵ��Ա��Ϣ�����ܽ�����/ɾ/�ĵȲ���");
			getButtonManager().getButton(IBillButton.Add).setEnabled(false);
			getButtonManager().getButton(IBillButton.Edit).setEnabled(false);
			getButtonManager().getButton(IBillButton.Delete).setEnabled(false);
			this.getBillUI().updateButtons();
		}
		//=================2012.11.14 �޸� sqt  ���ڵ����ѯ֮��ť״̬�����½���ˢ��=end==================
	}
	
	@Override
	protected void onBoQuery() throws Exception {
		StringBuffer strWhere = new StringBuffer();
		if (askForQueryCondition(strWhere) == false)
			return;// �û������˲�ѯ
		String userid = _getOperator();
		String pk_corp=_getCorp().getPrimaryKey();
		String pk_module=IhrpNCModule.HRWA; 
		String wheresql = strWhere.toString();

		wheresql = strWhere.toString()+" and  "+" pk_dept in(" +
		" select pk_deptdoc from bd_wa_userdept where " +
		"   isnull(dr,0)=0 and pk_user='"+_getOperator()+"') ";


		UserAndClerkVO[] uservo = (UserAndClerkVO[]) HYPubBO_Client.queryByCondition(UserAndClerkVO.class, " isnull(dr,0)=0 and userid='"+userid+"' ");
		if(uservo!=null&&uservo.length>0){
			String pk_psn = uservo[0].getPk_psndoc();
			PsndocVO[] docvos = (PsndocVO[])HYPubBO_Client.queryByCondition(PsndocVO.class,
					" pk_corp='"+_getCorp().getPrimaryKey()+"' and isnull(dr,0)=0 and pk_psnbasdoc='"+pk_psn+"' and groupdef51='000169100000001051BE' ");
			if(docvos!=null&&docvos.length>0){//����

			}else{
				wheresql+=" and voperatorid='"+_getOperator()+"' ";
			}
		}else{
			wheresql+=" and voperatorid='"+_getOperator()+"' ";
		}

		//����where��䣬����Ȩ�޹���
		String waclssSql = " and  pk_billtype='63RP' and isnull(bisnianzhong,'N')='N' and pk_wa_class='"+IHRPWABtn.PK_JIANG+"' and isnull(bisjiangjin,'N')='N' and pk_wa_class in" +
		"((select classid from wa_uclsright " +
		" where pk_corp = '"+_getCorp().getPrimaryKey()+"' and cuserid = '"+_getOperator()+"' and moduleflag = 0) " +
		" union " +
		" (select classid from wa_gclsright where cgroupid in " +
		" (select pk_role from sm_user_role " +
		" where sm_user_role.cuserid = '"+_getOperator()+"') and pk_corp = '"+_getCorp().getPrimaryKey()+"' and moduleflag = 0))";


		SuperVO[] queryVos = queryHeadVOs(wheresql+" "+waclssSql);

		getBufferData().clear();
		// �������ݵ�Buffer
		addDataToBuffer(queryVos);
		updateBuffer();
		int currRow = getBufferData().getCurrentRow();
		getBufferData().setCurrentRow(currRow);
		//=================2012.11.14 �޸� sqt  ���ڵ����ѯ֮��ť״̬�����½���ˢ��=beg==================
		if(((ClientUI)this.getBillUI()).pk_deptid==null||!((ClientUI)this.getBillUI()).iscan){
			//MessageDialog.showHintDlg(this, "��ʾ", "��ǰ�û�δ����ҵ��Ա��Ϣ�����ܽ�����/ɾ/�ĵȲ���");
			getButtonManager().getButton(IBillButton.Add).setEnabled(false);
			getButtonManager().getButton(IBillButton.Edit).setEnabled(false);
			getButtonManager().getButton(IBillButton.Delete).setEnabled(false);
			this.getBillUI().updateButtons();
		}
		//=================2012.11.14 �޸� sqt  ���ڵ����ѯ֮��ť״̬�����½���ˢ��=end==================
	}
	@Override
	protected void onBoRefresh() throws Exception {
		super.onBoRefresh();
	}
	@Override
	protected void onBoCard() throws Exception {
		super.onBoCard();
		int currRow = getBufferData().getCurrentRow();
		getBufferData().setCurrentRow(currRow);
	}
	@Override
	protected void onBoCommit() throws Exception {
		HYBillVO billvo = (HYBillVO)getBufferData().getCurrentVO();
		if(billvo==null||billvo.getParentVO()==null) return;
		PsnClassItemHVO hvo = (PsnClassItemHVO)billvo.getParentVO();
		PERIODVO pvo = ((ClientUI)getBillUI()).getWaPeriodVO();
		if(hvo.getPk_wa_period()==null||!hvo.getPk_wa_period().equals(pvo.getPrimaryKey())){
			MessageDialog.showErrorDlg(this.getBillUI(), "�ύ��ʾ", "�ǵ��µ��ݲ��ܲ���");
			return;
		}
		UFDate dmakedate = hvo.getDmakedate();
		if(dmakedate.after(_getDate())){
			MessageDialog.showErrorDlg(this.getBillUI(), "�ύ��ʾ", "�ύ���ڲ��������Ƶ�����");
			return;
		}
		
		int x = MessageDialog.showOkCancelDlg(this.getBillUI(),"�ύ��ʾ","ȷ��Ҫ�ύ��ǰ���ݣ�");
		if(x!=UIDialog.ID_OK) return;
		
		onBoEdit();
		getBillCardPanelWrapper().getBillCardPanel().getHeadItem("vcommitid").setValue(_getOperator());
		getBillCardPanelWrapper().getBillCardPanel().getHeadItem("dcommitdate").setValue(_getDate());
		getBillCardPanelWrapper().getBillCardPanel().getHeadItem("vbillstatus").setValue(IBillStatus.COMMIT);
		//����һ�����״̬
		getBillCardPanelWrapper().getBillCardPanel().getHeadItem("pk_item12").setValue(WaHrpBillStatus.NEEDPASS);
		//����һ�������״̬
		getBillCardPanelWrapper().getBillCardPanel().getHeadItem("pk_item14").setValue(WaHrpBillStatus.NEEDPASS);
		//���ö������״̬
		getBillCardPanelWrapper().getBillCardPanel().getHeadItem("vbillstatus_audit").setValue(WaHrpBillStatus.NEEDPASS);
		//�������
		getBillCardPanelWrapper().getBillCardPanel().getHeadItem("vapprovenote").setValue("һ�������");
		if(!onBoSaveMySelf(0,"һ�������")) {
			onBoCancel();
			return;
		}
	}


	/**ɾ��ѡ����(���в���)*/
	protected void onboLineDelAry() throws Exception {
		int rowCount=getBillCardPanelWrapper().getBillCardPanel().getBillTable().getRowCount();
		if(rowCount>0){
			getBillCardPanelWrapper().getBillCardPanel().getBillTable().selectAll();
			int[] selectRows=getBillCardPanelWrapper().getBillCardPanel().getBillTable().getSelectedRows();
			getBillCardPanelWrapper().getBillCardPanel().getBillModel().delLine(selectRows);
		}
	}
	@Override
	protected void onBoLineDel() throws Exception {
		String code = getBillCardPanelWrapper().getBillCardPanel().getCurrentBodyTableCode();
		if(code.equals("wa_deptsum")){
			return;
		}
		super.onBoLineDel();
		((ClientUI)getBillUI()).countHeadMny2();
	}

	/**
	 * ��ѯ��ǰ������ȫ���¼�PK(������ǰpk)
	 * @author qiutian 
	 * @date 2012-02-28
	 * @param pk Ҫ��ѯ������
	 * @param voClass Ҫ��ѯ��VO
	 */
	public ArrayList<String> getChildByPK(String pk,ArrayList<String> listPK,Class voClass) {
		if(listPK==null){
			listPK = new ArrayList<String>();
		}
		try {

			String strWhere = " isnull(dr,0)=0 and (pk_corp='"+IHRPSysParam.corp+"' or " 
			+ " pk_corp= '"+ ClientEnvironment.getInstance().getCorporation().getPrimaryKey()+ "')"+" " 
			+ " and pk_fathedept ='" + pk + "'";

			SuperVO[] vos = HYPubBO_Client.queryByCondition(voClass, strWhere);

			//ֻ�Ӳ����ڵ�
			if ( !listPK.contains(pk)){
				listPK.add(pk);
			}
			// �ݹ�
			for (SuperVO vo : vos) {
				getChildByPK(vo.getPrimaryKey(),listPK,voClass) ;
			}
		} catch (BusinessException e) {
			e.printStackTrace();
			System.out.println("��ѯ�¼���������");
		}
		return listPK;
	}
	@Override
	protected void onBoCopy() throws Exception {
		if(getBufferData()==null||getBufferData().getCurrentVO()==null) return;
		if(((ClientUI)getBillUI()).isListPanelSelected()){
			onBoCard();
		}
		super.onBoCopy();
		String pk_dept = (String)getBillCardPanelWrapper().getBillCardPanel().getHeadItem("pk_dept").getValueObject();
		getBillCardPanelWrapper().getBillCardPanel().setHeadItem("pk_corp", _getCorp().getPrimaryKey());
		getBillCardPanelWrapper().getBillCardPanel().setHeadItem("dmakedate", _getDate());
		getBillCardPanelWrapper().getBillCardPanel().setHeadItem("voperatorid", _getOperator());
		getBillCardPanelWrapper().getBillCardPanel().setHeadItem("vbillno", ((ClientUI)getBillUI()).getNewBillNo());
		getBillCardPanelWrapper().getBillCardPanel().setHeadItem("pk_billtype", getUIController().getBillType());
		getBillCardPanelWrapper().getBillCardPanel().getHeadItem("pk_psndoc").setValue(((ClientUI)getBillUI()).getPk_psndoc());
		getBillCardPanelWrapper().getBillCardPanel().getHeadItem("vbillstatus").setValue(IBillStatus.FREE);
		getBillCardPanelWrapper().getBillCardPanel().getHeadItem("pk_item12").setValue(WaHrpBillStatus.WRITE);
		getBillCardPanelWrapper().getBillCardPanel().getHeadItem("pk_item14").setValue(WaHrpBillStatus.WRITE);
		getBillCardPanelWrapper().getBillCardPanel().getHeadItem("vbillstatus_audit").setValue(WaHrpBillStatus.WRITE);
		String[] clearkeys = new String[]{"vcommitid","dcommitdate","vapproveid","vbatchcode_dept","vbatchcode_approve",
				"dapprovedate","vapprovenote","pk_wa_period","bisused","bispay","vpaypsnid","dpaydate","totalaftersmny","totalmoney","totalsmny"};
		for(String key:clearkeys){
			getBillCardPanelWrapper().getBillCardPanel().getHeadItem(key).setValue(null);
		}
		PERIODVO waPeriodVO  =((ClientUI)getBillUI()).getWaPeriodVO();
		if(waPeriodVO!=null) {
			getBillCardPanelWrapper().getBillCardPanel().getHeadItem("pk_wa_period").setValue(waPeriodVO.getPk_wa_period());
			getBillCardPanelWrapper().getBillCardPanel().getHeadItem("vperiod").setValue(waPeriodVO.getVcalmonth());
			getBillCardPanelWrapper().getBillCardPanel().getHeadItem("vyear").setValue(waPeriodVO.getCyear());
		}
		((ClientUI)getBillUI()).setPk_deptid(pk_dept);

		//		((ClientUI)getBillUI()).setDeptSum(pk_dept);
		int rowcount = getBillCardPanelWrapper().getBillCardPanel().getBillTable("wa_psn_item_b").getRowCount();
		for(int i=0;i<rowcount;i++){
			String[] keys = new String[]{"nsmny","nbcnsmny","ctaxtableid","noldsmny","noldnsmny","biscount",
					"vcountpsnid","dcountdate","ndkmny","nsqqkmny","nbqqkmny","nsfmny"};
			for(String key:keys){
				getBillCardPanelWrapper().getBillCardPanel().getBillModel("wa_psn_item_b").setValueAt(null, i,key);
			}
			getBillCardPanelWrapper().getBillCardPanel().getBillModel("wa_psn_item_b").setValueAt(getBillCardPanelWrapper().getBillCardPanel().getBillModel("wa_psn_item_b").getValueAt(i, "nmny"), i, "naftersmny");
			getBillCardPanelWrapper().getBillCardPanel().getBillModel("wa_psn_item_b").execLoadFormulaByRow(i);
		}
		((ClientUI)getBillUI()).countHeadMny(pk_dept);
		((ClientUI)getBillUI()).countHeadMny2();
	}
	/**
	 * ��VO������װ�� <����key + VO >����ʽ
	 * @param key  ���Ϊ����Ĭ������Ϊkey
	 * @author qiutian
	 * @date 2012-3-12
	 */
	public static HashMap<String,WaDataVO> getKeyToVO(WaDataVO[] vos){

		HashMap<String,WaDataVO> map = new HashMap<String, WaDataVO>();
		if( null!=vos ){

			for (WaDataVO superVO : vos) {
				map.put(superVO.getPsnid(), superVO);
			}
		}
		return map;
	}
	
}
