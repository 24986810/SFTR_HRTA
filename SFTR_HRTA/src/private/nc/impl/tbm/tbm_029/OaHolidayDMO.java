/**
 * 
 */
package nc.impl.tbm.tbm_029;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;

import javax.naming.NamingException;

import nc.bs.dao.BaseDAO;
import nc.bs.framework.common.NCLocator;
import nc.bs.pub.DataManageObject;
import nc.bs.pub.SuperDMO;
import nc.itf.hr.ta.IBclbDefining;
import nc.vo.bd.b06.PsndocVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.tam.tongren001.DeptKqBVO;
import nc.vo.tam.tongren003.PanbanWeekBVO;
import nc.vo.tam.tongren008.ApplyBVO;
import nc.vo.tam.tongren011.AdjustTamVO;
import nc.vo.tam.tongrenoa.OaHoildayVO;
import nc.vo.tbm.tbm_029.BclbVO;

/**
 * @author 28729
 *
 */
public class OaHolidayDMO extends DataManageObject {

	/**
	 * @throws NamingException
	 */
	public OaHolidayDMO() throws NamingException {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param dbName
	 * @throws NamingException
	 */
	public OaHolidayDMO(String dbName) throws NamingException {
		super(dbName);
		// TODO Auto-generated constructor stub
	}
	public void autoPsnIn(UFDate date) throws BusinessException{
		String sql = " isnull(dr,0)=0 and istate=1 and denddate<='"+date.getDateAfter(2)+"' and pk_dept_new not in ('','','') ";
		SuperDMO dmo = new SuperDMO();
		ApplyBVO[] bvos = (ApplyBVO[])dmo.queryByWhereClause(ApplyBVO.class, sql);
		if(bvos!=null&&bvos.length>0){
			ArrayList<ApplyBVO> list = new ArrayList<ApplyBVO>();
			ArrayList<DeptKqBVO> listb = new ArrayList<DeptKqBVO>();
			ArrayList<PanbanWeekBVO> listbb = new ArrayList<PanbanWeekBVO>();
			for(ApplyBVO bvo:bvos){
				bvo.setDnewstartdate(bvo.getDenddate().getDateAfter(1));
				bvo.setDjsdate(date);
				bvo.setDjspsnid("");
				bvo.setIstate(2);
				list.add(bvo);
				DeptKqBVO kbvo = new DeptKqBVO();
				kbvo.setDr(0);
				kbvo.setPrimaryKey(null);
				kbvo.setBisnew(new UFBoolean(true));
				kbvo.setPk_psndoc(bvo.getPk_psndoc());
				kbvo.setPk_dept(bvo.getPk_dept_new());
				kbvo.setDstartdate(bvo.getDnewstartdate());
				listb.add(kbvo);

				PanbanWeekBVO[] panbaibvos = (PanbanWeekBVO[])dmo.queryByWhereClause(PanbanWeekBVO.class, 
						" isnull(dr,0)=0 and pk_psndoc='"+bvo.getPk_psndoc()+"' and ddate>='"+bvo.getDnewstartdate()+"' and pk_bb in (select pk_bclbid from tbm_bclb where (lbbm like '9903%' or lbbm like '9905%' or lbbm like '9906%') ) ");
				if(panbaibvos!=null&&panbaibvos.length>0){
					for(PanbanWeekBVO panbaibvo:panbaibvos){
						panbaibvo.setPk_dept(bvo.getPk_dept_new());
					}
					listbb.addAll(Arrays.asList(panbaibvos));
				}

			}
			dmo.updateArray(list.toArray(new ApplyBVO[0]));
			dmo.insertArray(listb.toArray(new DeptKqBVO[0]));
			if(listbb!=null&&listbb.size()>0){
				dmo.updateArray(listbb.toArray(new PanbanWeekBVO[0]));
			}
		}
	}
	public void getOAHoilday(UFDate date) throws BusinessException {
		// TODO Auto-generated method stub
		BaseDAO dao = new BaseDAO();//and ksrq='"+date+"' 
//		BaseDAO dao = new BaseDAO("HISMID");
		SuperDMO dmo = new SuperDMO();
		IBclbDefining def = NCLocator.getInstance().lookup(IBclbDefining.class);
		BclbVO[] bclbs = def.queryBclbByCorp("1002");
		PsndocVO[] psnvos =  (PsndocVO[])dmo.queryByWhereClause(PsndocVO.class, " isnull(dr,0)=0 and pk_corp='1002' ");
		HashMap<String,PsndocVO> map = new HashMap<String, PsndocVO>();
		HashMap<String,BclbVO> map_bc = new HashMap<String, BclbVO>();
		for(PsndocVO psnvo:psnvos){
//			map.put(psnvo.getPsncode().substring(1), psnvo);去掉1位限制
			map.put(psnvo.getPsncode(), psnvo);
		}
		for(BclbVO lbvo:bclbs){
			map_bc.put(lbvo.getLbbm(), lbvo);
			map_bc.put(lbvo.getPk_bclbid(), lbvo);
		}
		Collection coll = dao.retrieveByClause(OaHoildayVO.class, // and bjsj like '"+date.getDateBefore(1).toString()+"%' 
				" zt=100 and empnumber is not null and ksrq>='2018'  and convert(char,bjsj,23) = '"+date.getDateBefore(1).toString()+"'    ");
		if(coll!=null&&coll.size()>0){
			OaHoildayVO[] oavos = (OaHoildayVO[])coll.toArray(new OaHoildayVO[0]);
			ArrayList<AdjustTamVO> list = new ArrayList<AdjustTamVO>();
			for(OaHoildayVO oavo:oavos){
				if(map_bc.get(oavo.getLx())!=null&&map.get(oavo.getEmpnumber())!=null){
					UFDate ksrq = oavo.getKsrq();
					UFDate jssq = oavo.getJsrq();
					int days = new UFDate().getDaysBetween(ksrq, jssq);
					for(int i=0;i<=days;i++){
						AdjustTamVO atmvo = new AdjustTamVO();
						atmvo.setPk_corp("1002");
						atmvo.setVdef1("OA系统");
						atmvo.setDr(0);
						atmvo.setPk_psn(map.get(oavo.getEmpnumber()).getPrimaryKey());
						atmvo.setDdate(ksrq.getDateAfter(i));
						atmvo.setDapplydate(new UFDate(oavo.getBjsj().substring(0,10)));
						atmvo.setIstate(1);
						atmvo.setVtype("1");
						atmvo.setPk_bb_new(map_bc.get(oavo.getLx()).getPk_bclbid());
						atmvo.setBbname_new(map_bc.get(oavo.getLx()).getLbmc());
						if(oavo.getKssxw().equals(oavo.getJssxw())){
							atmvo.setVmemo(ksrq.getDateAfter(i)+oavo.getKssxw()+oavo.getBjsj());
						}else{
							atmvo.setVmemo(ksrq.getDateAfter(i)+oavo.getKssxw()+"至"+oavo.getJssxw()+oavo.getBjsj());
						}
						DeptKqBVO[] deptvos = (DeptKqBVO[])dmo.queryByWhereClause(DeptKqBVO.class,
								" isnull(dr,0)=0 and pk_psndoc='"+atmvo.getPk_psn()+"'  and (denddate is null or denddate>='"+ksrq.getDateAfter(i)+"') and dstartdate<='"+ksrq.getDateAfter(i).toString()+"' ");
						PanbanWeekBVO[] vos = (PanbanWeekBVO[])dmo.queryByWhereClause(PanbanWeekBVO.class, 
								" ddate='"+ksrq.getDateAfter(i).toString()+"' and pk_psndoc='"+atmvo.getPk_psn()+"' ");
						if(vos!=null&&vos.length>0){
							String names = "";
							String pks = "";
							for(PanbanWeekBVO vo:vos){
								pks+=""+vo.getPk_bb()+",";
								names+= ""+map_bc.get(vo.getPk_bb()).getLbmc()+",";
							}
							pks = pks.substring(0, pks.length()-1);
							names = names.substring(0, names.length()-1);
							atmvo.setPk_bb_old(pks);
							atmvo.setBbname_old(names);
						}else{
							atmvo.setPk_bb_old(null);
							atmvo.setBbname_old(null);
						}
						if(deptvos!=null&&deptvos.length>0){
							atmvo.setPk_dept(deptvos[0].getPk_dept());
						}
						list.add(atmvo);
					}
				}
			}
			if(list!=null&&list.size()>0){
				dmo.insertArray(list.toArray(new AdjustTamVO[0]));
			}
		}
	}
}
