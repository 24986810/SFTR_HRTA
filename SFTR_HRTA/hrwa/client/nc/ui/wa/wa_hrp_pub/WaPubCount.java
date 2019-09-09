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
		String tmp = flag?"审核" : "弃审";
		if(hvo.getVbillstatus_audit()==WaHrpBillStatus.DEL){
			return "单据 " + hvo.getVbillno() + "" + tmp + "错误:单据已经作废。\n";
		}
		
		// update by dychf
		/*
		if(flag&&hvo.getVbillstatus_audit()!=WaHrpBillStatus.COMMIT){
			return "单据 " + hvo.getVbillno() + " " + tmp + "错误:单据非提交态。\n";
		}
		if(!flag&&hvo.getVbillstatus_audit()!=WaHrpBillStatus.PASS){
			return "单据 " + hvo.getVbillno() + " " + tmp + "错误:单据非审核态。\n";
		}
		if(dmakedate.after(date)){
			return "单据 " + hvo.getVbillno() + " " + tmp + "错误:复核日期不能早于提交日期。\n";
		}
		if(hvo.getPk_wa_period()==null||!hvo.getPk_wa_period().equals(pvo.getPrimaryKey())){
			return "单据 " + hvo.getVbillno() + " " + tmp + "错误:非当月单据不能操作。\n";
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
			return "单据 " + hvo.getVbillno() + " " + tmp + "成功。\n";
		} catch (BusinessException e) {
			e.printStackTrace();
			return "单据 " + hvo.getVbillno() + " " + tmp + "错误:" + e.getMessage() + "。\n";
		}
	}
	
	/**
	 * 原一级审核/取消审核
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
		//String tmp = flag?"复核计税":"取消复核计税";
		String tmp = flag ?" 奖金复核" : "取消奖金复核";
		if(hvo.getBispay()!=null&&hvo.getBispay().booleanValue()){
			return "单据 " + hvo.getVbillno() + "" + tmp + "错误：单据已经支付。\n";
		}
		if(hvo.getVbillstatus_audit()==WaHrpBillStatus.DEL){
			return "单据 " + hvo.getVbillno() + " " + tmp + "错误：单据已经作废。\n";
		}
		if(flag&&hvo.getVbillstatus_audit()!=WaHrpBillStatus.COMMIT){
//			return "单据"+hvo.getVbillno()+"复核计税错误:非提交状态，不能复核.\n";
		}
		
		if(hvo.getVdef1()!=null&&hvo.getVdef1().trim().length()>0){
			return "单据 " + hvo.getVbillno() + " " + tmp + "错误：取消审核生成的单据不能操作。\n";
		}
		
		if(dmakedate.after(date)){
			return "单据 " + hvo.getVbillno() + " " + tmp + "错误：审核日期不能早于提交日期。\n";
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
			if(pvalue==0){  //原有逻辑
				if(hvo.getVcommitid()!=null&&!operatorid.equals(hvo.getVcommitid())){

				}else{
					//return "单据 " + hvo.getVbillno() + " " + tmp + "错误：复核人与提交人不能为同一人。\n";
				}
			}
		}else{
			if(hvo.getIauditorder()!=null&&hvo.getIauditorder()<oldorder){
				return "单据 " + hvo.getVbillno() + " " + tmp + "错误：不是最大审批号。最大号为：" + oldorder + "\n";
			}
		}
		if(hvo.getPk_wa_period()==null||!hvo.getPk_wa_period().equals(pevo.getPrimaryKey())){
			return "单据 " + hvo.getVbillno() + " " + tmp + "错误：非当月单据不能操作。\n";
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
			return "单据 " + hvo.getVbillno() + " " + tmp + "成功。\n";
		} catch (BusinessException e) {
			e.printStackTrace();
			return "单据 " + hvo.getVbillno() + " " + tmp + "错误:" + e.getMessage() + "。\n";
		}

	}
	
	/**
	 * 新一级审核/取消审核
	 * @param hvo
	 * @param bvos
	 * @param pvalue
	 * @param pk_item12：一级审核状态
	 * @param vapprovenote
	 * @param pevo
	 * @param flag
	 * @return
	 */
	public static String onAuditYJSH(PsnClassItemHVO hvo, PsnClassItemBVO[] bvos, int pvalue, int pk_item12, String vapprovenote, PERIODVO pevo, boolean flag){
		UFDate dmakedate = hvo.getDcommitdate();
		String operatorid = ClientEnvironment.getInstance().getUser().getPrimaryKey();
		UFDate date = ClientEnvironment.getInstance().getDate();
		//String tmp = flag?"复核计税":"取消复核计税";
		String tmp = flag ?" 审核" : "取消审核";
		if(hvo.getBispay()!=null&&hvo.getBispay().booleanValue()){
			return "单据 " + hvo.getVbillno() + "" + tmp + "错误：单据已经支付。\n";
		}
		if(hvo.getVbillstatus_audit()==WaHrpBillStatus.DEL){
			return "单据 " + hvo.getVbillno() + " " + tmp + "错误：单据已经作废。\n";
		}
		if(flag&&hvo.getVbillstatus_audit()!=WaHrpBillStatus.COMMIT){
//			return "单据"+hvo.getVbillno()+"复核计税错误:非提交状态，不能复核.\n";
		}
		
		if(hvo.getVdef1()!=null&&hvo.getVdef1().trim().length()>0){
			return "单据 " + hvo.getVbillno() + " " + tmp + "错误：取消审核生成的单据不能操作。\n";
		}
		
		if(dmakedate.after(date)){
			return "单据 " + hvo.getVbillno() + " " + tmp + "错误：审核日期不能早于提交日期。\n";
		}
		if(pk_item12 == 2 && (
				(hvo.getPk_item14() != null && (hvo.getPk_item14() == 2 || hvo.getPk_item14() == 3 || hvo.getPk_item14() == 4)) 
				|| (hvo.getVbillstatus_audit() != null && (hvo.getVbillstatus_audit() == 2 || hvo.getVbillstatus_audit() == 3 || hvo.getVbillstatus_audit() == 4))
			)){
			return "单据 " + hvo.getVbillno() + " " + tmp + "错误：一级半或者二级已审核，您不能进行审核操作！\n";
		}
		
		//审核/取消审核逻辑校验:当一级半或者二级已经审批通过则不能进行取消审批操作
		if(pk_item12 == 3 && (
				(hvo.getPk_item14() != null && (hvo.getPk_item14() == 2 || hvo.getPk_item14() == 3 || hvo.getPk_item14() == 4)) 
				|| (hvo.getVbillstatus_audit() != null && (hvo.getVbillstatus_audit() == 2 || hvo.getVbillstatus_audit() == 3 || hvo.getVbillstatus_audit() == 4))
			)){
			return "单据 " + hvo.getVbillno() + " " + tmp + "错误：一级半或者二级已审核，您不能进行取消审核操作！\n";
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
			if(pvalue==0){  //原有逻辑
				if(hvo.getVcommitid()!=null&&!operatorid.equals(hvo.getVcommitid())){

				}else{
					//return "单据 " + hvo.getVbillno() + " " + tmp + "错误：复核人与提交人不能为同一人。\n";
				}
			}
		}else{
			if(hvo.getIauditorder()!=null&&hvo.getIauditorder()<oldorder){
				return "单据 " + hvo.getVbillno() + " " + tmp + "错误：不是最大审批号。最大号为：" + oldorder + "\n";
			}
		}
		if(hvo.getPk_wa_period()==null||!hvo.getPk_wa_period().equals(pevo.getPrimaryKey())){
			return "单据 " + hvo.getVbillno() + " " + tmp + "错误：非当月单据不能操作。\n";
		}
		//一级审核人
		hvo.setPk_item11(ClientEnvironment.getInstance().getUser().getPrimaryKey());
		//一级审核状态
		hvo.setPk_item12(pk_item12);
		hvo.setVapprovenote("一级审核：" + vapprovenote);
		hvo.setDapprovedate(date);
		try {
			HYPubBO_Client.update(hvo);
			return "单据 " + hvo.getVbillno() + " " + tmp + "成功。\n";
		} catch (UifException e) {
			Logger.error("单据 " + hvo.getVbillno() + " " + tmp + "错误:" + e.getMessage() + "。");
			return "单据 " + hvo.getVbillno() + " " + tmp + "错误:" + e.getMessage() + "。\n";
		}
	}
	
	/**
	 * 一级半审核/取消审核
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
		//String tmp = flag?"复核计税":"取消复核计税";
		String tmp = flag ?" 审核" : "取消审核";
		if(hvo.getBispay()!=null&&hvo.getBispay().booleanValue()){
			return "单据 " + hvo.getVbillno() + "" + tmp + "错误：单据已经支付。\n";
		}
		if(hvo.getVbillstatus_audit()==WaHrpBillStatus.DEL){
			return "单据 " + hvo.getVbillno() + " " + tmp + "错误：单据已经作废。\n";
		}
		if(flag&&hvo.getVbillstatus_audit()!=WaHrpBillStatus.COMMIT){
//			return "单据"+hvo.getVbillno()+"复核计税错误:非提交状态，不能复核.\n";
		}
		
		if(hvo.getVdef1()!=null&&hvo.getVdef1().trim().length()>0){
			return "单据 " + hvo.getVbillno() + " " + tmp + "错误：取消审核生成的单据不能操作。\n";
		}
		
		if(dmakedate.after(date)){
			return "单据 " + hvo.getVbillno() + " " + tmp + "错误：审核日期不能早于提交日期。\n";
		}
		//校验一级审核人是否已审核
		if((hvo.getPk_item12() == null || hvo.getPk_item12() != 2) && pk_item14 == 2){
			return "单据 " + hvo.getVbillno() + " " + tmp + "错误：一级还未审核通过，您不能进行审核操作！\n";
		}
		
		if(pk_item14 == 2 && (hvo.getVbillstatus_audit() != null && (hvo.getVbillstatus_audit() == 2 || hvo.getVbillstatus_audit() == 3 || hvo.getVbillstatus_audit() == 4))){
			return "单据 " + hvo.getVbillno() + " " + tmp + "错误：二级已审核，您不能进行审核操作！\n";
		}
		
		if(pk_item14 == 3 && (hvo.getVbillstatus_audit() != null && (hvo.getVbillstatus_audit() == 2 || hvo.getVbillstatus_audit() == 3 || hvo.getVbillstatus_audit() == 4))){
			return "单据 " + hvo.getVbillno() + " " + tmp + "错误：二级审核人已审核，您不能进行取消审核操作！\n";
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
			if(pvalue==0){  //原有逻辑
				if(hvo.getVcommitid()!=null&&!operatorid.equals(hvo.getVcommitid())){

				}else{
					//return "单据 " + hvo.getVbillno() + " " + tmp + "错误：复核人与提交人不能为同一人。\n";
				}
			}
		}else{
			if(hvo.getIauditorder()!=null&&hvo.getIauditorder()<oldorder){
				//return "单据 " + hvo.getVbillno() + " " + tmp + "错误：不是最大审批号。最大号为：" + oldorder + "\n";
			}
		}
		if(hvo.getPk_wa_period()==null||!hvo.getPk_wa_period().equals(pevo.getPrimaryKey())){
			return "单据 " + hvo.getVbillno() + " " + tmp + "错误：非当月单据不能操作。\n";
		}
		//一级半审核人
		hvo.setPk_item13(ClientEnvironment.getInstance().getUser().getPrimaryKey());
		//一级半审核状态
		hvo.setPk_item14(pk_item14);
		hvo.setVapprovenote("规培生奖金审核：" + vapprovenote);
		hvo.setDapprovedate(ClientEnvironment.getInstance().getDate());
		try {
			HYPubBO_Client.update(hvo);
			return "单据 " + hvo.getVbillno() + " " + tmp + "成功。\n";
		} catch (UifException e) {
			Logger.error("单据 " + hvo.getVbillno() + " " + tmp + "错误:" + e.getMessage() + "。");
			return "单据 " + hvo.getVbillno() + " " + tmp + "错误:" + e.getMessage() + "。\n";
		}

	}
	
	/**
	 * 二级审核/取消审核
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
		//String tmp = flag?"复核计税":"取消复核计税";
		String tmp = flag ?" 审核" : "取消审核";
		if(hvo.getBispay()!=null&&hvo.getBispay().booleanValue()){
			return "单据 " + hvo.getVbillno() + "" + tmp + "错误：单据已经支付。\n";
		}
		if(hvo.getVbillstatus_audit()==WaHrpBillStatus.DEL){
			return "单据 " + hvo.getVbillno() + " " + tmp + "错误：单据已经作废。\n";
		}
		if(flag&&hvo.getVbillstatus_audit()!=WaHrpBillStatus.COMMIT){
//			return "单据"+hvo.getVbillno()+"复核计税错误:非提交状态，不能复核.\n";
		}
		
		if(hvo.getVdef1()!=null&&hvo.getVdef1().trim().length()>0){
			return "单据 " + hvo.getVbillno() + " " + tmp + "错误：取消审核生成的单据不能操作。\n";
		}
		
		if(dmakedate.after(date)){
			return "单据 " + hvo.getVbillno() + " " + tmp + "错误：审核日期不能早于提交日期。\n";
		}
		//校验一级、一级半审核是否通过, 一级审核状态：pk_item12； 规培生审核状态：pk_item14
//		if(!(hvo.getPk_item12() != null && hvo.getPk_item12() == 2 && (hvo.getPk_item14() != null && hvo.getPk_item14() == 2 && hvo.getVdef8() != null && "03".equals(hvo.getVdef8()))) && status_audit == 2){
//			return "单据 " + hvo.getVbillno() + " " + tmp + "错误：一级或一级半还未审核通过，您不能进行审核通过操作！\n";
//		}
		
		if(!(hvo.getPk_item12() != null && hvo.getPk_item12() == 2) && status_audit == 2){
			return "单据 " + hvo.getVbillno() + " " + tmp + "错误：一级还未审核通过，您不能进行审核操作！\n";
		}else{
			//是否包含规培生：03表示包含
			if(hvo.getVdef8() != null && "03".equals(hvo.getVdef8())){
				if(!(hvo.getPk_item14() != null && hvo.getPk_item14() == 2) && status_audit == 2){
					return "单据 " + hvo.getVbillno() + " " + tmp + "错误：一级半还未审核通过，您不能进行审核操作！\n";					
				}
			}else{
				if(status_audit == 2){
					hvo.setPk_item14(status_audit); //审核时直接将规培生的审核状态也置为审核通过
				}
			}
		}
		
//		if(!(hvo.getPk_item12() != null && hvo.getPk_item12() == 2 && hvo.getPk_item14() != null && hvo.getPk_item14() == 2) && status_audit == 3){
//			return "单据 " + hvo.getVbillno() + " " + tmp + "错误：一级或一级半还未审核，您不能进行取消审核操作！\n";
//		}
		if(!(hvo.getPk_item12() != null && hvo.getPk_item12() == 2) && status_audit == 3){
			return "单据 " + hvo.getVbillno() + " " + tmp + "错误：一级还未审核，您不能进行取消审核操作！\n";
		}else{
			//是否包含规培生：03表示包含
			if(hvo.getVdef8() != null && "03".equals(hvo.getVdef8())){
				if(!(hvo.getPk_item14() != null && hvo.getPk_item14() == 2) && status_audit == 3){
					return "单据 " + hvo.getVbillno() + " " + tmp + "错误：一级半还未审核，您不能进行取消审核操作！\n";					
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
			if(pvalue==0){  //原有逻辑
				if(hvo.getVcommitid()!=null&&!operatorid.equals(hvo.getVcommitid())){

				}else{
					//return "单据 " + hvo.getVbillno() + " " + tmp + "错误：复核人与提交人不能为同一人。\n";
				}
			}
		}else{
			if(hvo.getIauditorder()!=null&&hvo.getIauditorder()<oldorder){
				//return "单据 " + hvo.getVbillno() + " " + tmp + "错误：不是最大审批号。最大号为：" + oldorder + "\n";
			}
		}
		if(hvo.getPk_wa_period()==null||!hvo.getPk_wa_period().equals(pevo.getPrimaryKey())){
			return "单据 " + hvo.getVbillno() + " " + tmp + "错误：非当月单据不能操作。\n";
		}
		hvo.setVapproveid(operatorid);
		hvo.setDapprovedate(date);
		hvo.setVbillstatus_audit(status_audit);
		hvo.setVapprovenote("二级审核：" + vapprovenote);
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
			return "单据 " + hvo.getVbillno() + " " + tmp + "成功。\n";
		} catch (BusinessException e) {
			e.printStackTrace();
			return "单据 " + hvo.getVbillno() + " " + tmp + "错误:" + e.getMessage() + "。\n";
		}

	}
}
