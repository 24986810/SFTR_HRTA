package nc.impl.hrwa;

import java.sql.SQLException;
import java.text.CollationKey;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import javax.naming.NamingException;

import nc.bs.dao.BaseDAO;
import nc.bs.hrwa.HrWaToGlDMO;
import nc.bs.logging.Logger;
import nc.bs.pub.SuperDMO;
import nc.bs.wa.wa_hrp01.Wa_hrp01DMO;
import nc.bs.wa.wa_hrp01.Wa_hrp02DMO;
import nc.bs.wa.wa_hrp_002.PsnItemDMO;
import nc.bs.wa.wa_hrp_005.FenPeiDMO;
import nc.itf.hrwa.IHRwaPub;
import nc.itf.wa.wa_hrppub.WaHrpBillStatus;
import nc.jdbc.framework.processor.ResultSetProcessor;
import nc.vo.hrwa.troo.DeptBonusSummariesVO;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFDate;
import nc.vo.trade.pub.HYBillVO;
import nc.vo.trade.pub.IBillStatus;
import nc.vo.wa.wa_016.PsnVO;
import nc.vo.wa.wa_hrp01.SendClassVO;
import nc.vo.wa.wa_hrp_002.PsnClassItemBVO;
import nc.vo.wa.wa_hrp_002.PsnClassItemHVO;
import nc.vo.wa.wa_hrp_005.FenpeiItemBVO;
import nc.vo.wa.wa_hrp_005.FenpeiItemHVO;
import nc.vo.wa.wa_hrp_006.DeptChargeHVO;
import nc.vo.wa.wa_hrp_007.DeptChargeMnyBVO;
import nc.vo.wa.wa_hrp_007.DeptChargeMnyHVO;
import nc.vo.wa.wa_hrp_009.UserDeptVO;
import nc.vo.wa.wa_reporthrp_003.ReportBodyVO3;
import nc.vo.wa.wa_reporthrp_008.JjMnySumVO1;
import nc.vo.wa.wa_xhreport001.XhReport01VO;

public class HRwaImpl implements IHRwaPub {
	private Wa_hrp01DMO  hrp01dmo;
	
	private Wa_hrp02DMO  hrp02dmo;
	
	private nc.bs.wa.wa_hrp_002.PsnItemDMO hrp002dmo;
	/**
	 * 薪资发放类别保存
	 * @param 
	 * @param SendClassVO vo  要更新的数据 insert or  update
	 * @return SendClassVO
	 * @throws BusinessException 
	 */
	public SendClassVO saveorupdatevo(SendClassVO vo) throws BusinessException {
		// TODO Auto-generated method stub
		return getWa_hrp01DMO().saveorupdatevo(vo);
	}
	private Wa_hrp02DMO getWa_hrp02DMO() {
		if(hrp02dmo==null){
			try {
				hrp02dmo=new Wa_hrp02DMO();
			} catch (NamingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return hrp02dmo;
	}
	
	private Wa_hrp01DMO getWa_hrp01DMO() {
		if(hrp01dmo==null){
			try {
				hrp01dmo=new Wa_hrp01DMO();
			} catch (NamingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return hrp01dmo;
	}
	private nc.bs.wa.wa_hrp_002.PsnItemDMO getwa_hrp02DMODMO() throws NamingException {
		if(hrp002dmo==null){
			hrp002dmo=new PsnItemDMO();
		}
		return hrp002dmo;
	}
	
	/**
	 * 
	 * @param where 
	 * @param bflag 是否停发
	 * @return  薪资导入时要获得所有的当前未停发的 所有的薪资档案
	 * @throws BusinessException
	 */
	public PsnVO[] queryAllByWherePsnVO(String pk_corp, String waYear, String waPeriod, String stWhere, String tableName,Boolean bflag)throws BusinessException{
		try {
			hrp002dmo=getwa_hrp02DMODMO();
			return hrp002dmo.queryAllByWherePsnVO(pk_corp,waYear,waPeriod, stWhere, tableName, bflag);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new nc.vo.pub.BusinessException(e.getMessage());
		}
	}
	public boolean setRoleOrUserAtdTypeAuth(String tempid,List list,int vflag,String pk_module,String pk_corp) throws BusinessException {

		BaseDAO baseDao = new BaseDAO();
		
		String delsql = "delete from bd_wa_userdept ";
		UserDeptVO[] tempVOs = (UserDeptVO[]) list.toArray(new UserDeptVO[0]);
		
		try{
			
			if(vflag == 0){//删除角色
				delsql += " where pk_role = '"+tempid+"'";
			}
			else if(vflag == 1 ){
				delsql += " where pk_user = '"+tempid+"'";
			}
			delsql+=" and pk_module='"+pk_module+"' and pk_corp='"+pk_corp+"'";
			baseDao.executeUpdate(delsql);
			baseDao.insertVOArray(tempVOs);
			
			return true;
		}
		catch(Exception e){
			Logger.error(e.getMessage());
			return false;
		}
		
	}
	/**
	 * 计税
	 */
	public void countAndAudit(PsnClassItemHVO hvo) throws BusinessException {
		// TODO Auto-generated method stub
		getWa_hrp01DMO().countAndAudit(hvo, null, true);
	}
	/**
	 * 合并计税取消计税到一起
	 */
	public void countAndAudit(PsnClassItemHVO hvo, PsnClassItemBVO[] bvos, boolean flag) throws BusinessException {
		// TODO Auto-generated method stub
		if(hvo!=null&&(hvo.getPk_billtype().equals("63RP")||hvo.getPk_billtype().equals("68RP"))){
//			ArrayList<PsnClassItemBVO> list = new ArrayList<PsnClassItemBVO>();
//			for(int i=0;i<bvos.length;i++){
//				if(!"2".equals(bvos[i].getStafftype())){
//					list.add(bvos[i]);
//				}
//			}
//			
//			PsnClassItemBVO[] bvos1= list.toArray(new PsnClassItemBVO[0]);
			getWa_hrp01DMO().countAndAudit(hvo, bvos, flag);
		}else{
			getWa_hrp02DMO().countAndAudit(hvo, bvos, flag);
		}
	}
	public void genDept_xh() throws BusinessException {
		// TODO Auto-generated method stub
		SuperDMO dmo = new SuperDMO();
		DeptChargeMnyHVO[] hhvos = (DeptChargeMnyHVO[])dmo.queryByWhereClause(DeptChargeMnyHVO.class, " isnull(dr,0)=0 and vbillstatus='"+IBillStatus.CHECKPASS+"' ");
	    if(hhvos!=null&&hhvos.length>0){
	    	for(DeptChargeMnyHVO hvo:hhvos){
	    		DeptChargeMnyBVO[] bvos = (DeptChargeMnyBVO[])dmo.queryByWhereClause(DeptChargeMnyBVO.class, " isnull(dr,0)=0 and pk_charge_mny='"+hvo.getPrimaryKey()+"'");
	    		HYBillVO value = new HYBillVO();
	    		value.setParentVO(hvo);
	    		value.setChildrenVO(bvos);
	    		try {
					FenPeiDMO fdmo = new FenPeiDMO();
					fdmo.genDeptmny_c(value);
				} catch (NamingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	    		
	    	}
	    }
	    FenpeiItemHVO[] hvos = (FenpeiItemHVO[])dmo.queryByWhereClause(FenpeiItemHVO.class, " isnull(dr,0)=0 and vbillstatus_audit='"+WaHrpBillStatus.PASS+"' ");
	    if(hvos!=null&&hvos.length>0){
	    	for(FenpeiItemHVO hvo:hvos){
	    		FenpeiItemBVO[] bvos = (FenpeiItemBVO[])dmo.queryByWhereClause(FenpeiItemBVO.class, " isnull(dr,0)=0 and pk_fenpei_h='"+hvo.getPrimaryKey()+"'");
	    		HYBillVO value = new HYBillVO();
	    		value.setParentVO(hvo);
	    		value.setChildrenVO(bvos);
	    		try {
					FenPeiDMO fdmo = new FenPeiDMO();
					fdmo.genDeptmny(value);
				} catch (NamingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	    		
	    	}
	    }
	    
	    DeptChargeHVO[] dhvos = (DeptChargeHVO[])dmo.queryByWhereClause(DeptChargeHVO.class, " isnull(dr,0)=0  ");
	    if(dhvos!=null&&dhvos.length>0){
	    	for(DeptChargeHVO hvo:dhvos){
	    		HYBillVO value = new HYBillVO();
	    		value.setParentVO(hvo);
	    		try {
					FenPeiDMO fdmo = new FenPeiDMO();
					fdmo.genDeptmny_b(value);
				} catch (NamingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	    		
	    	}
	    }
	    PsnClassItemHVO[] phvos = (PsnClassItemHVO[])dmo.queryByWhereClause(PsnClassItemHVO.class, " isnull(dr,0)=0 and pk_billtype='63RP' and vbillstatus_audit='"+WaHrpBillStatus.PASS+"' ");
	    if(phvos!=null&&phvos.length>0){
	    	for(PsnClassItemHVO hvo:phvos){
	    		PsnClassItemBVO[] bvos = (PsnClassItemBVO[])dmo.queryByWhereClause(PsnClassItemBVO.class, " isnull(dr,0)=0 and pk_psn_item_h='"+hvo.getPrimaryKey()+"'");
	    		try {
					Wa_hrp01DMO fdmo = new Wa_hrp01DMO();
					fdmo.genDeptmny_d(hvo, bvos);
					fdmo.savePsnItemBB(hvo);
				} catch (NamingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	    		
	    	}
	    }
	    PsnClassItemHVO[] pphvos = (PsnClassItemHVO[])dmo.queryByWhereClause(PsnClassItemHVO.class, " isnull(dr,0)=0 and pk_billtype='68RP' and vbillstatus_audit='"+WaHrpBillStatus.PASS+"' ");
	    if(pphvos!=null&&pphvos.length>0){
	    	for(PsnClassItemHVO hvo:pphvos){
	    		try {
					Wa_hrp01DMO fdmo = new Wa_hrp01DMO();
					fdmo.savePsnItemBB(hvo);
				} catch (NamingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	    		
	    	}
	    }
	    
	}
	public void onAudit(FenpeiItemHVO hvo, boolean flag) throws BusinessException {
		// TODO Auto-generated method stub
		try {
			FenPeiDMO dmo = new FenPeiDMO();
			dmo.onAudit(hvo, flag);
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void onConfirmToGl(String vyear,String vperiod,XhReport01VO[] datavos, HashMap<String, String> keyMap, HashMap<String, String> valueMap, HashMap<String, String> idtokeyMap, String operatorid, String pk_corp, UFDate curdate) throws BusinessException {
		// TODO Auto-generated method stub
		try {
			HrWaToGlDMO dmo = new HrWaToGlDMO();
			dmo.onConfirmToGl(vyear, vperiod, datavos, keyMap, valueMap, idtokeyMap, operatorid, pk_corp, curdate);
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	public void onConfirmToGl2(String vyear, String vperiod, ArrayList<JjMnySumVO1> list, ArrayList<JjMnySumVO1> list2, String operatorid, String pk_corp, UFDate curdate) throws BusinessException {
		// TODO Auto-generated method stub
		try {
			HrWaToGlDMO dmo = new HrWaToGlDMO();
			dmo.onConfirmToGl2(vyear, vperiod, list, list2, operatorid, pk_corp, curdate);
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public ArrayList<SuperVO> executeQuery(String sql, ResultSetProcessor processor) throws BusinessException {
		// TODO Auto-generated method stub
		BaseDAO dao = new BaseDAO();
		dao.setMaxRows(1000000);
		return (ArrayList<SuperVO>)dao.executeQuery(sql, processor);
	}
	public DeptBonusSummariesVO[] getDeptVos(String month, String year,
			String type) throws BusinessException {
		// TODO Auto-generated method stub
		BaseDAO dao = new BaseDAO("CDR_API");
//		BaseDAO dao = new BaseDAO("");
		Collection cc =  dao.retrieveByClause(DeptBonusSummariesVO.class, " month='"+month+"' and year='"+year+"' and type='"+type+"' ");
	    if(cc!=null&&cc.size()>0){
	    	return (DeptBonusSummariesVO[])cc.toArray( new DeptBonusSummariesVO[0]);
	    }else{
	    	return null;
	    }
	}
}
