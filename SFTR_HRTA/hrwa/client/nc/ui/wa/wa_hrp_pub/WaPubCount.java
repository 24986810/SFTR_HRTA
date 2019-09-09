package nc.ui.wa.wa_hrp_pub;

import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.itf.hrwa.IHRwaPub;
import nc.itf.wa.wa_hrppub.WaHrpBillStatus;
import nc.ui.pub.ClientEnvironment;
import nc.ui.trade.business.HYPubBO_Client;
import nc.uif.pub.exception.UifException;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDate;
import nc.vo.wa.wa_hrp_002.PERIODVO;
import nc.vo.wa.wa_hrp_002.PsnClassItemBVO;
import nc.vo.wa.wa_hrp_002.PsnClassItemHVO;
import nc.vo.wa.wa_hrp_005.FenpeiItemHVO;

/**
 * @author szh
 *
 */
public class WaPubCount{
	
	public WaPubCount(){
		
	}
	public static String onAudit(FenpeiItemHVO hvo,int status_audit,String msg,PERIODVO pvo,boolean flag){
		//UFDate dmakedate = hvo.getDcommitdate();
		String operatorid = ClientEnvironment.getInstance().getUser().getPrimaryKey();
		UFDate date = ClientEnvironment.getInstance().getDate();
		String tmp = flag?"���" : "����";
		if(hvo.getVbillstatus_audit()==WaHrpBillStatus.DEL){
			return "���� " + hvo.getVbillno() + "" + tmp + "����:�����Ѿ����ϡ�\n";
		}
		
		// update by dychf
		/*
		if(flag&&hvo.getVbillstatus_audit()!=WaHrpBillStatus.COMMIT){
			return "���� " + hvo.getVbillno() + " " + tmp + "����:���ݷ��ύ̬��\n";
		}
		if(!flag&&hvo.getVbillstatus_audit()!=WaHrpBillStatus.PASS){
			return "���� " + hvo.getVbillno() + " " + tmp + "����:���ݷ����̬��\n";
		}
		if(dmakedate.after(date)){
			return "���� " + hvo.getVbillno() + " " + tmp + "����:�������ڲ��������ύ���ڡ�\n";
		}
		if(hvo.getPk_wa_period()==null||!hvo.getPk_wa_period().equals(pvo.getPrimaryKey())){
			return "���� " + hvo.getVbillno() + " " + tmp + "����:�ǵ��µ��ݲ��ܲ�����\n";
		}
		*/
		
		hvo.setVapproveid(operatorid);
		hvo.setDapprovedate(date);
		hvo.setVbillstatus_audit(status_audit);
		hvo.setVapprovenote(msg);
		if(hvo.getPk_wa_period()==null||hvo.getPk_wa_period().trim().length()<=0){
			hvo.setPk_wa_period(pvo.getPrimaryKey());
			hvo.setVyear(pvo.getCyear());
			hvo.setVperiod(pvo.getVcalmonth());
		}
		IHRwaPub pub = NCLocator.getInstance().lookup(IHRwaPub.class);
		try {
			pub.onAudit(hvo,flag);
			return "���� " + hvo.getVbillno() + " " + tmp + "�ɹ���\n";
		} catch (BusinessException e) {
			e.printStackTrace();
			return "���� " + hvo.getVbillno() + " " + tmp + "����:" + e.getMessage() + "��\n";
		}
	}
	
	/**
	 * ԭһ�����/ȡ�����
	 * @param hvo
	 * @param bvos
	 * @param pvalue
	 * @param status_audit
	 * @param vapprovenote
	 * @param pevo
	 * @param flag
	 * @return
	 */
	public static String onAuditAndSj(PsnClassItemHVO hvo,PsnClassItemBVO[] bvos,int pvalue,int status_audit,String vapprovenote,PERIODVO pevo,boolean flag){
		UFDate dmakedate = hvo.getDcommitdate();
		String operatorid = ClientEnvironment.getInstance().getUser().getPrimaryKey();
		UFDate date = ClientEnvironment.getInstance().getDate();
		//String tmp = flag?"���˼�˰":"ȡ�����˼�˰";
		String tmp = flag ?" ���𸴺�" : "ȡ�����𸴺�";
		if(hvo.getBispay()!=null&&hvo.getBispay().booleanValue()){
			return "���� " + hvo.getVbillno() + "" + tmp + "���󣺵����Ѿ�֧����\n";
		}
		if(hvo.getVbillstatus_audit()==WaHrpBillStatus.DEL){
			return "���� " + hvo.getVbillno() + " " + tmp + "���󣺵����Ѿ����ϡ�\n";
		}
		if(flag&&hvo.getVbillstatus_audit()!=WaHrpBillStatus.COMMIT){
//			return "����"+hvo.getVbillno()+"���˼�˰����:���ύ״̬�����ܸ���.\n";
		}
		
		if(hvo.getVdef1()!=null&&hvo.getVdef1().trim().length()>0){
			return "���� " + hvo.getVbillno() + " " + tmp + "����ȡ��������ɵĵ��ݲ��ܲ�����\n";
		}
		
		if(dmakedate.after(date)){
			return "���� " + hvo.getVbillno() + " " + tmp + "����������ڲ��������ύ���ڡ�\n";
		}
		int oldorder = 0;
		try {
			String wheresql = " isnull(dr,0)=0 and vyear='"+pevo.getCyear()+"' and vperiod='"+pevo.getVcalmonth()+"' and vbillstatus_audit="+WaHrpBillStatus.PASS+" order by iauditorder desc ";
			PsnClassItemHVO[] oldvos = (PsnClassItemHVO[])HYPubBO_Client.queryByCondition(PsnClassItemHVO.class,wheresql);
		   if(oldvos!=null&&oldvos.length>0){
			   oldorder = oldvos[0].getIauditorder()!=null?oldvos[0].getIauditorder():0;
		   }
		} catch (UifException e1) {
			e1.printStackTrace();
		}
		
		if(flag){
			if(pvalue==0){  //ԭ���߼�
				if(hvo.getVcommitid()!=null&&!operatorid.equals(hvo.getVcommitid())){

				}else{
					//return "���� " + hvo.getVbillno() + " " + tmp + "���󣺸��������ύ�˲���Ϊͬһ�ˡ�\n";
				}
			}
		}else{
			if(hvo.getIauditorder()!=null&&hvo.getIauditorder()<oldorder){
				return "���� " + hvo.getVbillno() + " " + tmp + "���󣺲�����������š�����Ϊ��" + oldorder + "\n";
			}
		}
		if(hvo.getPk_wa_period()==null||!hvo.getPk_wa_period().equals(pevo.getPrimaryKey())){
			return "���� " + hvo.getVbillno() + " " + tmp + "���󣺷ǵ��µ��ݲ��ܲ�����\n";
		}
		hvo.setVapproveid(operatorid);
		hvo.setDapprovedate(date);
		hvo.setVbillstatus_audit(status_audit);
		hvo.setVapprovenote(vapprovenote);
		if(flag){
			hvo.setIauditorder(oldorder+1);
		}else{
			hvo.setIauditorder(null);
		}
		if(hvo.getPk_wa_period()==null||hvo.getPk_wa_period().trim().length()<=0){
			hvo.setPk_wa_period(pevo.getPrimaryKey());
			hvo.setVyear(pevo.getCyear());
			hvo.setVperiod(pevo.getVcalmonth());
		}
		IHRwaPub pub = NCLocator.getInstance().lookup(IHRwaPub.class);
		try {
			pub.countAndAudit(hvo,bvos,flag);
			return "���� " + hvo.getVbillno() + " " + tmp + "�ɹ���\n";
		} catch (BusinessException e) {
			e.printStackTrace();
			return "���� " + hvo.getVbillno() + " " + tmp + "����:" + e.getMessage() + "��\n";
		}

	}
	
	/**
	 * ��һ�����/ȡ�����
	 * @param hvo
	 * @param bvos
	 * @param pvalue
	 * @param pk_item12��һ�����״̬
	 * @param vapprovenote
	 * @param pevo
	 * @param flag
	 * @return
	 */
	public static String onAuditYJSH(PsnClassItemHVO hvo, PsnClassItemBVO[] bvos, int pvalue, int pk_item12, String vapprovenote, PERIODVO pevo, boolean flag){
		UFDate dmakedate = hvo.getDcommitdate();
		String operatorid = ClientEnvironment.getInstance().getUser().getPrimaryKey();
		UFDate date = ClientEnvironment.getInstance().getDate();
		//String tmp = flag?"���˼�˰":"ȡ�����˼�˰";
		String tmp = flag ?" ���" : "ȡ�����";
		if(hvo.getBispay()!=null&&hvo.getBispay().booleanValue()){
			return "���� " + hvo.getVbillno() + "" + tmp + "���󣺵����Ѿ�֧����\n";
		}
		if(hvo.getVbillstatus_audit()==WaHrpBillStatus.DEL){
			return "���� " + hvo.getVbillno() + " " + tmp + "���󣺵����Ѿ����ϡ�\n";
		}
		if(flag&&hvo.getVbillstatus_audit()!=WaHrpBillStatus.COMMIT){
//			return "����"+hvo.getVbillno()+"���˼�˰����:���ύ״̬�����ܸ���.\n";
		}
		
		if(hvo.getVdef1()!=null&&hvo.getVdef1().trim().length()>0){
			return "���� " + hvo.getVbillno() + " " + tmp + "����ȡ��������ɵĵ��ݲ��ܲ�����\n";
		}
		
		if(dmakedate.after(date)){
			return "���� " + hvo.getVbillno() + " " + tmp + "����������ڲ��������ύ���ڡ�\n";
		}
		if(pk_item12 == 2 && (
				(hvo.getPk_item14() != null && (hvo.getPk_item14() == 2 || hvo.getPk_item14() == 3 || hvo.getPk_item14() == 4)) 
				|| (hvo.getVbillstatus_audit() != null && (hvo.getVbillstatus_audit() == 2 || hvo.getVbillstatus_audit() == 3 || hvo.getVbillstatus_audit() == 4))
			)){
			return "���� " + hvo.getVbillno() + " " + tmp + "����һ������߶�������ˣ������ܽ�����˲�����\n";
		}
		
		//���/ȡ������߼�У��:��һ������߶����Ѿ�����ͨ�����ܽ���ȡ����������
		if(pk_item12 == 3 && (
				(hvo.getPk_item14() != null && (hvo.getPk_item14() == 2 || hvo.getPk_item14() == 3 || hvo.getPk_item14() == 4)) 
				|| (hvo.getVbillstatus_audit() != null && (hvo.getVbillstatus_audit() == 2 || hvo.getVbillstatus_audit() == 3 || hvo.getVbillstatus_audit() == 4))
			)){
			return "���� " + hvo.getVbillno() + " " + tmp + "����һ������߶�������ˣ������ܽ���ȡ����˲�����\n";
		}
		int oldorder = 0;
		try {
			String wheresql = " isnull(dr,0)=0 and vyear='"+pevo.getCyear()+"' and vperiod='"+pevo.getVcalmonth()+"' and pk_item12=" + WaHrpBillStatus.PASS + " order by iauditorder desc ";
			PsnClassItemHVO[] oldvos = (PsnClassItemHVO[])HYPubBO_Client.queryByCondition(PsnClassItemHVO.class,wheresql);
		   if(oldvos!=null&&oldvos.length>0){
			   oldorder = oldvos[0].getIauditorder()!=null?oldvos[0].getIauditorder():0;
		   }
		} catch (UifException e1) {
			e1.printStackTrace();
		}
		
		if(flag){
			if(pvalue==0){  //ԭ���߼�
				if(hvo.getVcommitid()!=null&&!operatorid.equals(hvo.getVcommitid())){

				}else{
					//return "���� " + hvo.getVbillno() + " " + tmp + "���󣺸��������ύ�˲���Ϊͬһ�ˡ�\n";
				}
			}
		}else{
			if(hvo.getIauditorder()!=null&&hvo.getIauditorder()<oldorder){
				return "���� " + hvo.getVbillno() + " " + tmp + "���󣺲�����������š�����Ϊ��" + oldorder + "\n";
			}
		}
		if(hvo.getPk_wa_period()==null||!hvo.getPk_wa_period().equals(pevo.getPrimaryKey())){
			return "���� " + hvo.getVbillno() + " " + tmp + "���󣺷ǵ��µ��ݲ��ܲ�����\n";
		}
		//һ�������
		hvo.setPk_item11(ClientEnvironment.getInstance().getUser().getPrimaryKey());
		//һ�����״̬
		hvo.setPk_item12(pk_item12);
		hvo.setVapprovenote("һ����ˣ�" + vapprovenote);
		hvo.setDapprovedate(date);
		try {
			HYPubBO_Client.update(hvo);
			return "���� " + hvo.getVbillno() + " " + tmp + "�ɹ���\n";
		} catch (UifException e) {
			Logger.error("���� " + hvo.getVbillno() + " " + tmp + "����:" + e.getMessage() + "��");
			return "���� " + hvo.getVbillno() + " " + tmp + "����:" + e.getMessage() + "��\n";
		}
	}
	
	/**
	 * һ�������/ȡ�����
	 * @param hvo
	 * @param bvos
	 * @param pvalue
	 * @param status_audit
	 * @param vapprovenote
	 * @param pevo
	 * @param flag
	 * @return
	 */
	public static String onAuditYJBSH(PsnClassItemHVO hvo, PsnClassItemBVO[] bvos, int pvalue, int pk_item14, String vapprovenote, PERIODVO pevo, boolean flag){
		UFDate dmakedate = hvo.getDcommitdate();
		String operatorid = ClientEnvironment.getInstance().getUser().getPrimaryKey();
		UFDate date = ClientEnvironment.getInstance().getDate();
		//String tmp = flag?"���˼�˰":"ȡ�����˼�˰";
		String tmp = flag ?" ���" : "ȡ�����";
		if(hvo.getBispay()!=null&&hvo.getBispay().booleanValue()){
			return "���� " + hvo.getVbillno() + "" + tmp + "���󣺵����Ѿ�֧����\n";
		}
		if(hvo.getVbillstatus_audit()==WaHrpBillStatus.DEL){
			return "���� " + hvo.getVbillno() + " " + tmp + "���󣺵����Ѿ����ϡ�\n";
		}
		if(flag&&hvo.getVbillstatus_audit()!=WaHrpBillStatus.COMMIT){
//			return "����"+hvo.getVbillno()+"���˼�˰����:���ύ״̬�����ܸ���.\n";
		}
		
		if(hvo.getVdef1()!=null&&hvo.getVdef1().trim().length()>0){
			return "���� " + hvo.getVbillno() + " " + tmp + "����ȡ��������ɵĵ��ݲ��ܲ�����\n";
		}
		
		if(dmakedate.after(date)){
			return "���� " + hvo.getVbillno() + " " + tmp + "����������ڲ��������ύ���ڡ�\n";
		}
		//У��һ��������Ƿ������
		if((hvo.getPk_item12() == null || hvo.getPk_item12() != 2) && pk_item14 == 2){
			return "���� " + hvo.getVbillno() + " " + tmp + "����һ����δ���ͨ���������ܽ�����˲�����\n";
		}
		
		if(pk_item14 == 2 && (hvo.getVbillstatus_audit() != null && (hvo.getVbillstatus_audit() == 2 || hvo.getVbillstatus_audit() == 3 || hvo.getVbillstatus_audit() == 4))){
			return "���� " + hvo.getVbillno() + " " + tmp + "���󣺶�������ˣ������ܽ�����˲�����\n";
		}
		
		if(pk_item14 == 3 && (hvo.getVbillstatus_audit() != null && (hvo.getVbillstatus_audit() == 2 || hvo.getVbillstatus_audit() == 3 || hvo.getVbillstatus_audit() == 4))){
			return "���� " + hvo.getVbillno() + " " + tmp + "���󣺶������������ˣ������ܽ���ȡ����˲�����\n";
		}
		int oldorder = 0;
		try {
			String wheresql = " isnull(dr,0)=0 and vyear = '" + pevo.getCyear() + "' and vperiod = '" + pevo.getVcalmonth() + "' and pk_item14 = " + WaHrpBillStatus.PASS + " order by iauditorder desc ";
			PsnClassItemHVO[] oldvos = (PsnClassItemHVO[])HYPubBO_Client.queryByCondition(PsnClassItemHVO.class,wheresql);
		   if(oldvos!=null&&oldvos.length>0){
			   oldorder = oldvos[0].getIauditorder()!=null?oldvos[0].getIauditorder():0;
		   }
		} catch (UifException e1) {
			e1.printStackTrace();
		}
		
		if(flag){
			if(pvalue==0){  //ԭ���߼�
				if(hvo.getVcommitid()!=null&&!operatorid.equals(hvo.getVcommitid())){

				}else{
					//return "���� " + hvo.getVbillno() + " " + tmp + "���󣺸��������ύ�˲���Ϊͬһ�ˡ�\n";
				}
			}
		}else{
			if(hvo.getIauditorder()!=null&&hvo.getIauditorder()<oldorder){
				//return "���� " + hvo.getVbillno() + " " + tmp + "���󣺲�����������š�����Ϊ��" + oldorder + "\n";
			}
		}
		if(hvo.getPk_wa_period()==null||!hvo.getPk_wa_period().equals(pevo.getPrimaryKey())){
			return "���� " + hvo.getVbillno() + " " + tmp + "���󣺷ǵ��µ��ݲ��ܲ�����\n";
		}
		//һ���������
		hvo.setPk_item13(ClientEnvironment.getInstance().getUser().getPrimaryKey());
		//һ�������״̬
		hvo.setPk_item14(pk_item14);
		hvo.setVapprovenote("������������ˣ�" + vapprovenote);
		hvo.setDapprovedate(ClientEnvironment.getInstance().getDate());
		try {
			HYPubBO_Client.update(hvo);
			return "���� " + hvo.getVbillno() + " " + tmp + "�ɹ���\n";
		} catch (UifException e) {
			Logger.error("���� " + hvo.getVbillno() + " " + tmp + "����:" + e.getMessage() + "��");
			return "���� " + hvo.getVbillno() + " " + tmp + "����:" + e.getMessage() + "��\n";
		}

	}
	
	/**
	 * �������/ȡ�����
	 * @param hvo
	 * @param bvos
	 * @param pvalue
	 * @param status_audit
	 * @param vapprovenote
	 * @param pevo
	 * @param flag
	 * @return
	 */
	public static String onAuditEJSH(PsnClassItemHVO hvo, PsnClassItemBVO[] bvos, int pvalue, int status_audit, String vapprovenote, PERIODVO pevo, boolean flag){
		UFDate dmakedate = hvo.getDcommitdate();
		String operatorid = ClientEnvironment.getInstance().getUser().getPrimaryKey();
		UFDate date = ClientEnvironment.getInstance().getDate();
		//String tmp = flag?"���˼�˰":"ȡ�����˼�˰";
		String tmp = flag ?" ���" : "ȡ�����";
		if(hvo.getBispay()!=null&&hvo.getBispay().booleanValue()){
			return "���� " + hvo.getVbillno() + "" + tmp + "���󣺵����Ѿ�֧����\n";
		}
		if(hvo.getVbillstatus_audit()==WaHrpBillStatus.DEL){
			return "���� " + hvo.getVbillno() + " " + tmp + "���󣺵����Ѿ����ϡ�\n";
		}
		if(flag&&hvo.getVbillstatus_audit()!=WaHrpBillStatus.COMMIT){
//			return "����"+hvo.getVbillno()+"���˼�˰����:���ύ״̬�����ܸ���.\n";
		}
		
		if(hvo.getVdef1()!=null&&hvo.getVdef1().trim().length()>0){
			return "���� " + hvo.getVbillno() + " " + tmp + "����ȡ��������ɵĵ��ݲ��ܲ�����\n";
		}
		
		if(dmakedate.after(date)){
			return "���� " + hvo.getVbillno() + " " + tmp + "����������ڲ��������ύ���ڡ�\n";
		}
		//У��һ����һ��������Ƿ�ͨ��, һ�����״̬��pk_item12�� ���������״̬��pk_item14
//		if(!(hvo.getPk_item12() != null && hvo.getPk_item12() == 2 && (hvo.getPk_item14() != null && hvo.getPk_item14() == 2 && hvo.getVdef8() != null && "03".equals(hvo.getVdef8()))) && status_audit == 2){
//			return "���� " + hvo.getVbillno() + " " + tmp + "����һ����һ���뻹δ���ͨ���������ܽ������ͨ��������\n";
//		}
		
		if(!(hvo.getPk_item12() != null && hvo.getPk_item12() == 2) && status_audit == 2){
			return "���� " + hvo.getVbillno() + " " + tmp + "����һ����δ���ͨ���������ܽ�����˲�����\n";
		}else{
			//�Ƿ������������03��ʾ����
			if(hvo.getVdef8() != null && "03".equals(hvo.getVdef8())){
				if(!(hvo.getPk_item14() != null && hvo.getPk_item14() == 2) && status_audit == 2){
					return "���� " + hvo.getVbillno() + " " + tmp + "����һ���뻹δ���ͨ���������ܽ�����˲�����\n";					
				}
			}else{
				if(status_audit == 2){
					hvo.setPk_item14(status_audit); //���ʱֱ�ӽ������������״̬Ҳ��Ϊ���ͨ��
				}
			}
		}
		
//		if(!(hvo.getPk_item12() != null && hvo.getPk_item12() == 2 && hvo.getPk_item14() != null && hvo.getPk_item14() == 2) && status_audit == 3){
//			return "���� " + hvo.getVbillno() + " " + tmp + "����һ����һ���뻹δ��ˣ������ܽ���ȡ����˲�����\n";
//		}
		if(!(hvo.getPk_item12() != null && hvo.getPk_item12() == 2) && status_audit == 3){
			return "���� " + hvo.getVbillno() + " " + tmp + "����һ����δ��ˣ������ܽ���ȡ����˲�����\n";
		}else{
			//�Ƿ������������03��ʾ����
			if(hvo.getVdef8() != null && "03".equals(hvo.getVdef8())){
				if(!(hvo.getPk_item14() != null && hvo.getPk_item14() == 2) && status_audit == 3){
					return "���� " + hvo.getVbillno() + " " + tmp + "����һ���뻹δ��ˣ������ܽ���ȡ����˲�����\n";					
				}
			}
		}
		
		int oldorder = 0;
		try {
			String wheresql = " isnull(dr,0)=0 and vyear='"+pevo.getCyear()+"' and vperiod='"+pevo.getVcalmonth()+"' and vbillstatus_audit="+WaHrpBillStatus.PASS+" order by iauditorder desc ";
			PsnClassItemHVO[] oldvos = (PsnClassItemHVO[])HYPubBO_Client.queryByCondition(PsnClassItemHVO.class,wheresql);
		   if(oldvos!=null&&oldvos.length>0){
			   oldorder = oldvos[0].getIauditorder()!=null?oldvos[0].getIauditorder():0;
		   }
		} catch (UifException e1) {
			e1.printStackTrace();
		}
		
		if(flag){
			if(pvalue==0){  //ԭ���߼�
				if(hvo.getVcommitid()!=null&&!operatorid.equals(hvo.getVcommitid())){

				}else{
					//return "���� " + hvo.getVbillno() + " " + tmp + "���󣺸��������ύ�˲���Ϊͬһ�ˡ�\n";
				}
			}
		}else{
			if(hvo.getIauditorder()!=null&&hvo.getIauditorder()<oldorder){
				//return "���� " + hvo.getVbillno() + " " + tmp + "���󣺲�����������š�����Ϊ��" + oldorder + "\n";
			}
		}
		if(hvo.getPk_wa_period()==null||!hvo.getPk_wa_period().equals(pevo.getPrimaryKey())){
			return "���� " + hvo.getVbillno() + " " + tmp + "���󣺷ǵ��µ��ݲ��ܲ�����\n";
		}
		hvo.setVapproveid(operatorid);
		hvo.setDapprovedate(date);
		hvo.setVbillstatus_audit(status_audit);
		hvo.setVapprovenote("������ˣ�" + vapprovenote);
		if(flag){
			hvo.setIauditorder(oldorder+1);
		}else{
			hvo.setIauditorder(null);
		}
		if(hvo.getPk_wa_period()==null||hvo.getPk_wa_period().trim().length()<=0){
			hvo.setPk_wa_period(pevo.getPrimaryKey());
			hvo.setVyear(pevo.getCyear());
			hvo.setVperiod(pevo.getVcalmonth());
		}
		IHRwaPub pub = NCLocator.getInstance().lookup(IHRwaPub.class);
		try {
			pub.countAndAudit(hvo,bvos,flag);
			return "���� " + hvo.getVbillno() + " " + tmp + "�ɹ���\n";
		} catch (BusinessException e) {
			e.printStackTrace();
			return "���� " + hvo.getVbillno() + " " + tmp + "����:" + e.getMessage() + "��\n";
		}

	}
}
