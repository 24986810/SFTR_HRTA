package nc.impl.hrwa;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.bs.dao.BaseDAO;
import nc.itf.hrp.pub.IHRPSysParam;
import nc.itf.hrwa.IHRWaServices;
import nc.itf.uap.busibean.SysinitAccessor;
import nc.jdbc.framework.processor.BeanListProcessor;
import nc.jdbc.framework.processor.ColumnListProcessor;
import nc.vo.hrp.pub.pub03.DeptDocVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.para.SysInitVO;
import nc.vo.pub.psnvo.PsnDocVO;
import nc.vo.tam.tongren001.DeptKqBVO;
import nc.vo.wa.wa_hrp_002.PsnClassItemHVO;
import nc.vo.wa.wa_hrp_004.DeptMnyHVO;
import nc.vo.wa.wa_hrp_005.FenpeiItemBVO;
import nc.vo.wa.wa_hrp_005.FenpeiItemHVO;
import nc.vo.wa.wa_hrp_006.DeptChargeBVO;
import nc.vo.wa.wa_hrp_006.DeptChargeHVO;
import nc.vo.wa.wa_hrp_006.WaClassItemVO;

public class HRWaServicesImpl implements IHRWaServices{
	
	/**
	 * 根据考勤科室（部门）主键和考勤期间查询当前期间下该部门下的所有人
	 */
	@SuppressWarnings("unchecked")
	public List<String> queryAllPsndocVOByCond(String pk_corp, String pk_perioddept, String vyear, String vperiod) throws BusinessException {
		//获取系统预置参数：同仁奖金发放期间参数
		SysInitVO sysInitVO = SysinitAccessor.getInstance().getParaByAccuratePk_org(pk_corp, "TONGRENQJ");
		int tongrenqj = 2;
		if(sysInitVO != null && sysInitVO.getValue() != null){
			tongrenqj = Integer.parseInt(sysInitVO.getValue());
		}
		BaseDAO dao = new BaseDAO();
		List<String> pkpsnList = null;
		//根据当前期间获取其前两个期间
		String lastyear = "";
		String lastperiod = "";
		int period = Integer.parseInt(vperiod);
//		if(period == 1){
//			lastyear = (Integer.parseInt(vyear)-1) + "";
//			lastperiod = "11";
//		}else if(period == 2){
//			lastyear = (Integer.parseInt(vyear) - 1) + "";
//			lastperiod = "12";
//		}else if(period <= 11){
//			lastyear = vyear;
//			lastperiod = "0" + (Integer.parseInt(vperiod) - 2);
//		}else{
//			lastyear = vyear;
//			lastperiod = "" + (Integer.parseInt(vperiod) - 2);
//		}
		if(period - tongrenqj <= 0){
			lastyear = (Integer.parseInt(vyear)-1) + "";
			if(12- (tongrenqj - period) < 10){
				lastperiod = "0" + (12- (tongrenqj - period));
			}else{
				lastperiod = 12- (tongrenqj - period) + "";
			}
		}else{
			lastyear = vyear;
			if(period - tongrenqj < 10){
				lastperiod = "0" + (period - tongrenqj);
			}else{
				lastperiod = "" + (period - tongrenqj);
			}
		}
		
		String dstartdate = lastyear + "-" + lastperiod + "-01";
		String denddate = "";
		//根据调整后的期间组装对应的开始、结束日期
		if(lastperiod != null 
				&& ("01".equals(lastperiod) || "03".equals(lastperiod) || "05".equals(lastperiod) || "07".equals(lastperiod) || "08".equals(lastperiod) || "10".equals(lastperiod) || "12".equals(lastperiod))){
			denddate = lastyear + "-" + lastperiod + "-31";
		}else if(lastperiod != null 
				&& ("04".equals(lastperiod) || "06".equals(lastperiod) || "09".equals(lastperiod) || "11".equals(lastperiod))){
			denddate = lastyear + "-" + lastperiod + "-30";
		}else if(lastperiod != null && "02".equals(lastperiod)){
			denddate = lastyear + "-" + lastperiod + "-29";
		}
		
		String sql = "select pk_deptdoc from bd_deptdoc where pk_deptdoc = (select pk_deptdoc from pf_perioddept where pk_corp = '" + pk_corp + "' and pk_perioddept = '" + pk_perioddept + "')";
		List<String> pkdeptdocsList = (List<String>) dao.executeQuery(sql, new ColumnListProcessor());
		if(pkdeptdocsList != null && !pkdeptdocsList.isEmpty()){
			List<String> listDeptPK = getChildByPK(dao, pk_corp, pkdeptdocsList.get(0), null, DeptDocVO.class);
			StringBuffer bufStr = new StringBuffer();
			if(listDeptPK != null && !listDeptPK.isEmpty()){
				int count = 0;
				for(String pk_deptdoc : listDeptPK){
					if(count == 0){
						bufStr.append("'" + pk_deptdoc + "'");
						count++;
					}else{
						bufStr.append(",'" + pk_deptdoc + "'");
					}
				}
				String wherePKdeptdoc = bufStr.toString();
				if(wherePKdeptdoc != null && wherePKdeptdoc.trim().length() > 0){
					String qrySql = "SELECT DISTINCT pk_psndoc FROM trtam_deptdoc_kq_b WHERE nvl(dr,0)=0 AND "
							+ "pk_dept IN (SELECT pk_dept FROM trtam_deptdoc_kq WHERE nvl(dr,0)=0 AND vcode IN("
							+ "SELECT deptcode FROM bd_deptdoc WHERE pk_deptdoc IN(" + wherePKdeptdoc + ")"
							+ ")"
							+ ")"
							+ "AND dstartdate <= '" + denddate + "' AND (denddate >= '" + dstartdate + "' OR denddate IS NULL)";
					//根据选择的奖金科室pk及其对应的期间查询该科室下的当前期间的考勤人员pk_psndoc
					pkpsnList = (List<String>) dao.executeQuery(qrySql, new ColumnListProcessor());
				}
			}
		}
		return pkpsnList;
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, String> queryPsnMapByCond(String pk_corp, String pk_perioddept, String vyear, String vperiod) throws BusinessException {
		//获取系统预置参数：同仁奖金发放期间参数
		SysInitVO sysInitVO = SysinitAccessor.getInstance().getParaByAccuratePk_org(pk_corp, "TONGRENQJ");
		int tongrenqj = 2;
		if(sysInitVO != null && sysInitVO.getValue() != null){
			tongrenqj = Integer.parseInt(sysInitVO.getValue());
		}
		BaseDAO dao = new BaseDAO();
		List<DeptKqBVO> voList = null;
		//根据当前期间获取其前两个期间
		String lastyear = "";
		String lastperiod = "";
		int period = Integer.parseInt(vperiod);
//		if(period == 1){
//			lastyear = (Integer.parseInt(vyear)-1) + "";
//			lastperiod = "11";
//		}else if(period == 2){
//			lastyear = (Integer.parseInt(vyear) - 1) + "";
//			lastperiod = "12";
//		}else if(period <= 11){
//			lastyear = vyear;
//			lastperiod = "0" + (Integer.parseInt(vperiod) - 2);
//		}else{
//			lastyear = vyear;
//			lastperiod = "" + (Integer.parseInt(vperiod) - 2);
//		}
		if(period - tongrenqj <= 0){
			lastyear = (Integer.parseInt(vyear)-1) + "";
			if(12- (tongrenqj - period) < 10){
				lastperiod = "0" + (12- (tongrenqj - period));
			}else{
				lastperiod = 12- (tongrenqj - period) + "";
			}
		}else{
			lastyear = vyear;
			if(period - tongrenqj < 10){
				lastperiod = "0" + (period - tongrenqj);
			}else{
				lastperiod = "" + (period - tongrenqj);
			}
		}
		
		String dstartdate = lastyear + "-" + lastperiod + "-01";
		String denddate = "";
		//根据调整后的期间组装对应的开始、结束日期
		if(lastperiod != null 
				&& ("01".equals(lastperiod) || "03".equals(lastperiod) || "05".equals(lastperiod) || "07".equals(lastperiod) || "08".equals(lastperiod) || "10".equals(lastperiod) || "12".equals(lastperiod))){
			denddate = lastyear + "-" + lastperiod + "-31";
		}else if(lastperiod != null 
				&& ("04".equals(lastperiod) || "06".equals(lastperiod) || "09".equals(lastperiod) || "11".equals(lastperiod))){
			denddate = lastyear + "-" + lastperiod + "-30";
		}else if(lastperiod != null && "02".equals(lastperiod)){
			denddate = lastyear + "-" + lastperiod + "-29";
		}
		
		Map<String, String> map = new HashMap<String, String>();
		String sql = "select pk_deptdoc from bd_deptdoc where pk_deptdoc = (select pk_deptdoc from pf_perioddept where pk_corp = '" + pk_corp + "' and pk_perioddept = '" + pk_perioddept + "')";
		List<String> pkdeptdocsList = (List<String>) dao.executeQuery(sql, new ColumnListProcessor());
		if(pkdeptdocsList != null && !pkdeptdocsList.isEmpty()){
			List<String> listDeptPK = getChildByPK(dao, pk_corp, pkdeptdocsList.get(0), null, DeptDocVO.class);
			StringBuffer bufStr = new StringBuffer();
			if(listDeptPK != null && !listDeptPK.isEmpty()){
				int count = 0;
				for(String pk_deptdoc : listDeptPK){
					if(count == 0){
						bufStr.append("'" + pk_deptdoc + "'");
						count++;
					}else{
						bufStr.append(",'" + pk_deptdoc + "'");
					}
				}
				String wherePKdeptdoc = bufStr.toString();
				if(wherePKdeptdoc != null && wherePKdeptdoc.trim().length() > 0){
					String qrySql = "SELECT * FROM trtam_deptdoc_kq_b WHERE nvl(dr,0)=0 AND "
							+ "pk_dept IN (SELECT pk_dept FROM trtam_deptdoc_kq WHERE nvl(dr,0)=0 AND vcode IN("
							+ "SELECT deptcode FROM bd_deptdoc WHERE pk_deptdoc IN(" + wherePKdeptdoc + ")"
							+ ")"
							+ ")"
							+ "AND dstartdate <= '" + denddate + "' AND (denddate >= '" + dstartdate + "' OR denddate IS NULL)";
					//根据选择的奖金科室pk及其对应的期间查询该科室下的当前期间的考勤人员pk_psndoc
					voList = (List<DeptKqBVO>) dao.executeQuery(qrySql, new BeanListProcessor(DeptKqBVO.class));
					if(voList != null && !voList.isEmpty()){
						for(DeptKqBVO vo : voList){
							if(!map.containsKey(vo.getPk_psndoc())){
								map.put(vo.getPk_psndoc(), vo.getPk_dept());
							}
						}
					}
				}
			}
		}
		return map;
	}
	
	/**
	 * 根据考勤科室（部门）主键和考勤期间查询当前期间下该部门下的所有人
	 */
	@SuppressWarnings("unchecked")
	public List<String> queryAllPsndocVOByCond_bak(String pk_corp, String pk_perioddept, String dstartdate, String denddate) throws BusinessException {
		List<String> pkpsnList = null;
		BaseDAO dao = new BaseDAO();
		
		String sql = "select pk_deptdoc from bd_deptdoc where pk_deptdoc = (select pk_deptdoc from pf_perioddept where pk_corp = '" + pk_corp + "' and pk_perioddept = '" + pk_perioddept + "')";
		List<String> pkdeptdocsList = (List<String>) dao.executeQuery(sql, new ColumnListProcessor());
		if(pkdeptdocsList != null && !pkdeptdocsList.isEmpty()){
			List<String> listDeptPK = getChildByPK(dao, pk_corp, pkdeptdocsList.get(0), null, DeptDocVO.class);
			StringBuffer bufStr = new StringBuffer();
			if(listDeptPK != null && !listDeptPK.isEmpty()){
				int count = 0;
				for(String pk_deptdoc : listDeptPK){
					if(count == 0){
						bufStr.append("'" + pk_deptdoc + "'");
						count++;
					}else{
						bufStr.append(",'" + pk_deptdoc + "'");
					}
				}
				String wherePKdeptdoc = bufStr.toString();
				if(wherePKdeptdoc != null && wherePKdeptdoc.trim().length() > 0){
					String qrySql = "SELECT DISTINCT pk_psndoc FROM trtam_deptdoc_kq_b WHERE nvl(dr,0)=0 AND "
							+ "pk_dept IN (SELECT pk_dept FROM trtam_deptdoc_kq WHERE nvl(dr,0)=0 AND vcode IN("
							+ "SELECT deptcode FROM bd_deptdoc WHERE pk_deptdoc IN(" + wherePKdeptdoc + ")"
							+ ")"
							+ ")"
							+ "AND dstartdate <= '" + denddate + "' AND (denddate >= '" + dstartdate + "' OR denddate IS NULL)";
					//根据选择的奖金科室pk及其对应的期间查询该科室下的当前期间的考勤人员pk_psndoc
					pkpsnList = (List<String>) dao.executeQuery(qrySql, new ColumnListProcessor());
				}
			}
		}
		return pkpsnList;
	}
	
	/**
	 * 查询当前主键的全部下级PK(包括当前pk)
	 * @param pk 要查询的主键
	 * @param voClass 要查询的VO
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<String> getChildByPK(BaseDAO dao, String pk_corp, String pk_perioddept, ArrayList<String> listPK, Class<DeptDocVO> voClass) {
		if(listPK==null){
			listPK = new ArrayList<String>();
		}
		try {
			String sql = "SELECT * from BD_DEPTDOC where nvl(dr,0)=0 and (pk_corp='" + IHRPSysParam.corp + "' or  pk_corp= '"+ pk_corp + "') and pk_fathedept ='" + pk_perioddept + "'";
			List<DeptDocVO> vosList = (List<DeptDocVO>) dao.executeQuery(sql, new BeanListProcessor(DeptDocVO.class));
			//只加不存在的
			if ( !listPK.contains(pk_perioddept)){
				listPK.add(pk_perioddept);
			}
			// 递归
			for (DeptDocVO vo : vosList) {
				getChildByPK(dao, pk_corp, vo.getPrimaryKey(),listPK,voClass) ;
			}
		} catch (BusinessException e) {
			e.printStackTrace();
			System.out.println("查询下级主键错误");
		}
		return listPK;
	}

	@SuppressWarnings("unchecked")
	public Map<String, FenpeiItemBVO> queryFenpeiItemBVOMapByCond(String vyear, String vperiod, String pk_corp) throws BusinessException {
		Map<String, FenpeiItemBVO> map = null;
		BaseDAO dao = new BaseDAO();
		//奖金科室分配主表vo：FenpeiItemHVO，表：wa_fenpei_h;   奖金科室分配子表vo：FenpeiItemBVO，表：wa_fenpei_b
		//select nmny, nmny1, pk_corp, pk_fenpei_b, pk_fenpei_h, pk_wa_dept, ts, dr from wa_fenpei_b where pk_fenpei_h = (select pk_fenpei_h from wa_fenpei_h where vyear = '2018' and vperiod = '08' and pk_dept is null and pk_corp = '1002'  and nvl(dr,0)=0 and VBILLSTATUS_AUDIT = 2)
		//VBILLSTATUS_AUDIT:"编写中:0","已提交:1","已批准:2","不通过:3","驳回:4","作废:5"
		String sql = "SELECT nmny, nmny1, pk_corp, pk_fenpei_b, pk_fenpei_h, pk_wa_dept, ts, dr "
					+ "FROM wa_fenpei_b "
						+ "WHERE nvl(dr,0)=0 AND pk_fenpei_h = ("
							+ "SELECT pk_fenpei_h FROM wa_fenpei_h WHERE vyear = '" + vyear + "' AND vperiod = '" + vperiod + "' AND pk_dept is null AND pk_corp = '" + pk_corp + "' AND vbillstatus_audit = 2 AND nvl(dr,0)=0)";
		List<FenpeiItemBVO> fenpeiItemBVOList = (List<FenpeiItemBVO>) dao.executeQuery(sql, new BeanListProcessor(FenpeiItemBVO.class));
		if(fenpeiItemBVOList != null && !fenpeiItemBVOList.isEmpty()){
			map = new HashMap<String, FenpeiItemBVO>();
			for(FenpeiItemBVO vo: fenpeiItemBVOList){
				if(!map.containsKey(vo.getPk_wa_dept())){
					map.put(vo.getPk_wa_dept(), vo);
				}
			}
		}
		return map;
	}

	@SuppressWarnings("unchecked")
	public Map<String, Map<String, Map<String, UFDouble>>> queryMnyMapByCond_New(String pk_corp, String pk_perioddept, String vyear, String vperiod) throws BusinessException {
		BaseDAO dao = new BaseDAO();
		//查询当前期间对应的奖金科室分配方案.
		//"select PK_FENPEI_H from wa_fenpei_h where nvl(dr,0)=0 and PK_CORP = '1002' and PK_BILLTYPE = '64RP' and VYEAR = '2018' and VPERIOD = '08' and ISZYBFFLAG = 'N' and VBILLSTATUS_AUDIT = 2";
		
		//查询当前期间全部科室的结余
		String qryJYSql = "SELECT * FROM wa_deptmny_h WHERE nvl(dr,0)=0 AND pk_corp = '" + pk_corp + "'";
		List<DeptMnyHVO> list = (List<DeptMnyHVO>) dao.executeQuery(qryJYSql, new BeanListProcessor(DeptMnyHVO.class));
		//组装各科室结余数据字典， key：科室pk_dept， value：科室结余nmny
		Map<String, UFDouble> map = new HashMap<String, UFDouble>();
		if(list != null && !list.isEmpty()){
			for(DeptMnyHVO vo : list){
				if(!map.containsKey(vo.getPk_dept())){
					map.put(vo.getPk_dept(), vo.getNmny());
				}
			}
		}
		Map<String, Map<String, UFDouble>> ksjyMap = new HashMap<String, Map<String, UFDouble>>();
		ksjyMap.put("ksjyMap", map);
		
		//查询当前期间科室转账主表数据
		String qryKszzSql = "SELECT * FROM wa_deptcharge WHERE nvl(dr,0)=0 AND pk_dept_charge IN(SELECT DISTINCT csourcebillhid FROM wa_deptmny_b WHERE nvl(dr,0)=0 AND PK_CORP = '" + pk_corp + "' AND VYEAR = '" + vyear + "' AND VPERIOD = '" + vperiod + "' AND CSOURCEBILLTYPECODE = '65RP')";
		List<DeptChargeHVO> kszzList = (List<DeptChargeHVO>) dao.executeQuery(qryKszzSql, new BeanListProcessor(DeptChargeHVO.class));
		//组装各科室转账转入科室数据字典， key：转入科室pk_dept_in， value：科室转账主表主键pk_dept_charge
		Map<String, String> kszzInMap = new HashMap<String, String>();
		//组装各科室转账转出科室数据字典， key：转出科室pk_dept_out， value：科室转账主表主键pk_dept_charge
		Map<String, String> kszzOutMap = new HashMap<String, String>();
		//部门转账主表主键集合pk_dept_charge
		List<String> deptChargePkList = new ArrayList<String>();
		if(kszzList != null && !kszzList.isEmpty()){
			for(DeptChargeHVO vo : kszzList){
				if(!kszzInMap.containsKey(vo.getPk_dept_in())){
					//kszzInMap.put(vo.getPk_dept_in(), vo.getPk_dept_charge());
					kszzInMap.put(vo.getPk_dept_charge(), vo.getPk_dept_in());
				}
				if(!kszzOutMap.containsKey(vo.getPk_dept_out())){
					//kszzOutMap.put(vo.getPk_dept_out(), vo.getPk_dept_charge());
					kszzOutMap.put(vo.getPk_dept_charge(), vo.getPk_dept_out());
				}
				if(!deptChargePkList.contains(vo.getPk_dept_charge())){
					deptChargePkList.add(vo.getPk_dept_charge());
				}
			}
		}
		
		//组装各科室转入数据字典， key：转入科室pk_dept， value：科室转账详情map：key：项目pk（月考核奖、一次性奖金）, value：调转金额
		Map<String, Map<String, UFDouble>> zrMap = new HashMap<String, Map<String, UFDouble>>();
		//组装各科室转出数据字典， key：转出科室pk_dept， value：科室转账详情map：key：项目pk（月考核奖、一次性奖金）, value：调转金额
		Map<String, Map<String, UFDouble>> zcMap = new HashMap<String, Map<String, UFDouble>>();
		
		if(deptChargePkList != null && !deptChargePkList.isEmpty()){
			StringBuffer bufStr = new StringBuffer();
			if(deptChargePkList != null && !deptChargePkList.isEmpty()){
				int count = 0;
				for(String pk_dept_charge : deptChargePkList){
					if(count == 0){
						bufStr.append("'" + pk_dept_charge + "'");
						count++;
					}else{
						bufStr.append(",'" + pk_dept_charge + "'");
					}
				}
				
				String pKdeptCharges = bufStr.toString();
				if(pKdeptCharges != null && pKdeptCharges.trim().length() > 0){
					String qrySql = "SELECT * FROM wa_deptcharge_b WHERE nvl(dr,0)=0 AND pk_dept_charge IN(" + pKdeptCharges + ")";
					//根据科室转账主表id集合查询对应的子表信息
					List<DeptChargeBVO> deptChargeBList = (List<DeptChargeBVO>) dao.executeQuery(qrySql, new BeanListProcessor(DeptChargeBVO.class));
					if(deptChargeBList != null && !deptChargeBList.isEmpty()){
						//kszzInMap: 组装各科室转账转入科室数据字典， key：转入科室pk_dept_in， value：科室转账主表主键pk_dept_charge
						if(kszzInMap != null && !kszzInMap.isEmpty()){
							for(String key : kszzInMap.keySet()){
								//科室转账(转入)详情map, key：项目pk（月考核奖、一次性奖金）, value：调转金额
								Map<String, UFDouble> inMap = new HashMap<String, UFDouble>();
								for(DeptChargeBVO vo : deptChargeBList){
									//转入科室
//									if(kszzInMap.get(key) != null && vo.getPk_dept_charge() != null && kszzInMap.get(key).equals(vo.getPk_dept_charge())){
//										if(inMap.containsKey(vo.getPk_class_item())){
//											inMap.put(vo.getPk_class_item(), inMap.get(vo.getPk_class_item()).add(vo.getNmny()));
//										}else{
//											inMap.put(vo.getPk_class_item(), vo.getNmny());
//										}
//									}
									if(key != null && vo.getPk_dept_charge() != null && key.equals(vo.getPk_dept_charge())){
										if(inMap.containsKey(vo.getPk_class_item())){
											inMap.put(vo.getPk_class_item(), inMap.get(vo.getPk_class_item()).add(vo.getNmny()));
										}else{
											inMap.put(vo.getPk_class_item(), vo.getNmny());
										}
									}
								}
								if(zrMap.containsKey(kszzInMap.get(key))){
									Map<String, UFDouble> temp = zrMap.get(kszzInMap.get(key));
									if(temp != null && !temp.isEmpty()){
										for(String tempkey : temp.keySet()){
											if(inMap.containsKey(tempkey)){
												UFDouble d = temp.get(tempkey).add(inMap.get(tempkey));
												temp.put(tempkey, d);
											}
										}
									}
								}else{
									zrMap.put(kszzInMap.get(key), inMap);
								}
							}
						}
						
						//组装各科室转账转出科室数据字典， key：转出科室pk_dept_out， value：科室转账主表主键pk_dept_charge
						if(kszzOutMap != null && !kszzOutMap.isEmpty()){
							for(String key : kszzOutMap.keySet()){
								//科室转账(转出)详情map, key：项目pk（月考核奖、一次性奖金）, value：调转金额
								Map<String, UFDouble> outMap = new HashMap<String, UFDouble>();
								for(DeptChargeBVO vo : deptChargeBList){
									//转出科室
//									if(kszzOutMap.get(key) != null && vo.getPk_dept_charge() != null && kszzOutMap.get(key).equals(vo.getPk_dept_charge())){
//										if(outMap.containsKey(vo.getPk_class_item())){
//											outMap.put(vo.getPk_class_item(), outMap.get(vo.getPk_class_item()).add(vo.getNmny()));
//										}else{
//											outMap.put(vo.getPk_class_item(), vo.getNmny());
//										}
//									}
									if(key != null && vo.getPk_dept_charge() != null && key.equals(vo.getPk_dept_charge())){
										if(outMap.containsKey(vo.getPk_class_item())){
											outMap.put(vo.getPk_class_item(), outMap.get(vo.getPk_class_item()).add(vo.getNmny()));
										}else{
											outMap.put(vo.getPk_class_item(), vo.getNmny());
										}
									}
								}
								if(zcMap.containsKey(kszzOutMap.get(key))){
									Map<String, UFDouble> temp = zcMap.get(kszzOutMap.get(key));
									if(temp != null && !temp.isEmpty()){
										for(String tempkey : temp.keySet()){
											if(outMap.containsKey(tempkey)){
												UFDouble d = temp.get(tempkey).add(outMap.get(tempkey));
												temp.put(tempkey, d);
											}
										}
									}
								}else{
									zcMap.put(kszzOutMap.get(key), outMap);
								}
							}
						}
					}
				}
			}
		}
		
		Map<String, Map<String, Map<String, UFDouble>>> retMap = new HashMap<String, Map<String, Map<String, UFDouble>>>();
		retMap.put("zrMap", zrMap);
		retMap.put("zcMap", zcMap);
		//各科室结余map
		retMap.put("ksjyMap", ksjyMap);
		
		//组装各科室当前期间-调入月考核奖金
		
		//组装各科室当前期间-调出月考核奖金
		
		//组装各科室当前期间-调入一次性奖金金额
		
		//组装各科室当前期间-调出一次性奖金金额
		
		//查询当前期间对应的所有的
		//String sql = "select * from wa_deptmny_b where Csourcebillhid = (select PK_FENPEI_H from wa_fenpei_h where nvl(dr,0)=0 and PK_CORP = '1002' and PK_BILLTYPE = '64RP' and VYEAR = '2018' and VPERIOD = '08' and ISZYBFFLAG = 'N' and VBILLSTATUS_AUDIT = 2)";
		
		return retMap;
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, Map<String, Map<String, UFDouble>>> queryMnyMapByCond(String pk_corp, String pk_perioddept, String vyear, String vperiod) throws BusinessException {
		Map<String, Map<String, Map<String, UFDouble>>> retMap = new HashMap<String, Map<String, Map<String, UFDouble>>>();
		BaseDAO dao = new BaseDAO();
		//查询当前期间对应的奖金科室分配方案.
		//"select PK_FENPEI_H from wa_fenpei_h where nvl(dr,0)=0 and PK_CORP = '1002' and PK_BILLTYPE = '64RP' and VYEAR = '2018' and VPERIOD = '08' and ISZYBFFLAG = 'N' and VBILLSTATUS_AUDIT = 2";
		
		//根据当前期间查询是否存在【奖金上传复核】数据
		String qryFPSql = "SELECT * FROM wa_fenpei_h WHERE nvl(dr,0)=0 AND pk_corp = '" + pk_corp + "' AND vyear = '" + vyear + "' AND vperiod = '" + vperiod + "' AND vbillstatus_audit = 2 AND pk_dept is null ";
		List<FenpeiItemHVO> fenpeiItemHList = (List<FenpeiItemHVO>) dao.executeQuery(qryFPSql, new BeanListProcessor(FenpeiItemHVO.class));
		if(fenpeiItemHList == null || fenpeiItemHList.isEmpty()){
			retMap = null;
			return retMap;
		}
		FenpeiItemHVO hvo = fenpeiItemHList.get(0);
		String pk_fenpei_h = hvo.getPk_fenpei_h();
		List<FenpeiItemBVO> fenpeiItemBList = null;
		if(pk_fenpei_h != null && pk_fenpei_h.trim().length() > 0){
			//查询【奖金上传复核】子表数据：nmny：奖金科室合计金额； nmny1：月考核奖； nmny2：一次性奖金
			String qryFenpeiB = "select * from wa_fenpei_b where pk_fenpei_h = '" + pk_fenpei_h + "'";
			fenpeiItemBList = (List<FenpeiItemBVO>) dao.executeQuery(qryFenpeiB, new BeanListProcessor(FenpeiItemBVO.class));
		}
		//奖金科室-合计金额
		Map<String, UFDouble> nmnyMap = new HashMap<String, UFDouble>();
		//奖金科室-月考核奖金额
		Map<String, UFDouble> nmny1Map = new HashMap<String, UFDouble>();
		//奖金科室-一次性奖金金额
		Map<String, UFDouble> nmny2Map = new HashMap<String, UFDouble>();
		if(fenpeiItemBList != null && !fenpeiItemBList.isEmpty()){
			for(FenpeiItemBVO vo : fenpeiItemBList){
				//组装合计金额map
				if(nmnyMap.containsKey(vo.getPk_wa_dept())){
					//月考核奖
					UFDouble nmny1 = vo.getNmny1() != null ? vo.getNmny1() : new UFDouble(0);
					//一次性奖金
					UFDouble nmny2 = vo.getNmny2() != null ? vo.getNmny2() : new UFDouble(0);
					//合计金额
					UFDouble nmnyTotal = nmnyMap.get(vo.getPk_wa_dept()).add(nmny1.add(nmny2));
					nmnyMap.put(vo.getPk_wa_dept(), nmnyTotal);
				}else{
					//月考核奖
					UFDouble nmny1 = vo.getNmny1() != null ? vo.getNmny1() : new UFDouble(0);
					//一次性奖金
					UFDouble nmny2 = vo.getNmny2() != null ? vo.getNmny2() : new UFDouble(0);
					//合计金额
					UFDouble nmnyTotal = nmny1.add(nmny2);
					nmnyMap.put(vo.getPk_wa_dept(), nmnyTotal);
				}
				
				//组装月考核奖map
				if(nmny1Map.containsKey(vo.getPk_wa_dept())){
					UFDouble nmny1 = vo.getNmny1() != null ? vo.getNmny1() : new UFDouble(0);
					UFDouble nmny1Total = nmny1Map.get(vo.getPk_wa_dept()).add(nmny1);
					nmny1Map.put(vo.getPk_wa_dept(), nmny1Total);
				}else{
					UFDouble nmny1 = vo.getNmny1() != null ? vo.getNmny1() : new UFDouble(0);
					nmny1Map.put(vo.getPk_wa_dept(), nmny1);
				}
				
				//组装一次性奖金map
				if(nmny2Map.containsKey(vo.getPk_wa_dept())){
					UFDouble nmny2 = vo.getNmny2() != null ? vo.getNmny2() : new UFDouble(0);
					UFDouble nmny2Total = nmny2Map.get(vo.getPk_wa_dept()).add(nmny2);
					nmny2Map.put(vo.getPk_wa_dept(), nmny2Total);
				}else{
					UFDouble nmny2 = vo.getNmny2() != null ? vo.getNmny2() : new UFDouble(0);
					nmny2Map.put(vo.getPk_wa_dept(), nmny2);
				}
				
			}
		}
		//记录全院科室奖金数据信息
		Map<String, Map<String, UFDouble>> ksjjMap = new HashMap<String, Map<String, UFDouble>>();
		ksjjMap.put("nmnyTotal", nmnyMap);
		ksjjMap.put("nmny1Total", nmny1Map);
		ksjjMap.put("nmny2Total", nmny2Map);
		
		
//		List<DeptMnyHVO> list = (List<DeptMnyHVO>) dao.executeQuery(qryJYSql, new BeanListProcessor(DeptMnyHVO.class));
//		//组装各科室结余数据字典， key：科室pk_dept， value：科室结余nmny
//		Map<String, UFDouble> map = new HashMap<String, UFDouble>();
//		if(list != null && !list.isEmpty()){
//			for(DeptMnyHVO vo : list){
//				if(!map.containsKey(vo.getPk_dept())){
//					map.put(vo.getPk_dept(), vo.getNmny());
//				}
//			}
//		}
//		Map<String, Map<String, UFDouble>> ksjyMap = new HashMap<String, Map<String, UFDouble>>();
//		ksjyMap.put("ksjyMap", map);
		
		//查询当前期间科室转账主表数据
		String qryKszzSql = "SELECT * FROM wa_deptcharge WHERE nvl(dr,0)=0 and vbillstatus = 1 and reddased=0 AND pk_dept_charge IN(SELECT DISTINCT csourcebillhid FROM wa_deptmny_b WHERE nvl(dr,0)=0 AND PK_CORP = '" + pk_corp + "' AND VYEAR = '" + vyear + "' AND VPERIOD = '" + vperiod + "' AND CSOURCEBILLTYPECODE = '65RP')";
		List<DeptChargeHVO> kszzList = (List<DeptChargeHVO>) dao.executeQuery(qryKszzSql, new BeanListProcessor(DeptChargeHVO.class));
		//组装各科室转账转入科室数据字典， key：转入科室pk_dept_in， value：科室转账主表主键pk_dept_charge
		Map<String, String> kszzInMap = new HashMap<String, String>();
		//组装各科室转账转出科室数据字典， key：转出科室pk_dept_out， value：科室转账主表主键pk_dept_charge
		Map<String, String> kszzOutMap = new HashMap<String, String>();
		//部门转账主表主键集合pk_dept_charge
		List<String> deptChargePkList = new ArrayList<String>();
		if(kszzList != null && !kszzList.isEmpty()){
			for(DeptChargeHVO vo : kszzList){
				if(!kszzInMap.containsKey(vo.getPk_dept_in())){
					//kszzInMap.put(vo.getPk_dept_in(), vo.getPk_dept_charge());
					kszzInMap.put(vo.getPk_dept_charge(), vo.getPk_dept_in());
				}
				if(!kszzOutMap.containsKey(vo.getPk_dept_out())){
					//kszzOutMap.put(vo.getPk_dept_out(), vo.getPk_dept_charge());
					kszzOutMap.put(vo.getPk_dept_charge(), vo.getPk_dept_out());
				}
				if(!deptChargePkList.contains(vo.getPk_dept_charge())){
					deptChargePkList.add(vo.getPk_dept_charge());
				}
			}
		}
		
		//组装各科室转入数据字典， key：转入科室pk_dept， value：科室转账详情map：key：项目pk（月考核奖、一次性奖金）, value：调转金额
		Map<String, Map<String, UFDouble>> zrMap = new HashMap<String, Map<String, UFDouble>>();
		//组装各科室转出数据字典， key：转出科室pk_dept， value：科室转账详情map：key：项目pk（月考核奖、一次性奖金）, value：调转金额
		Map<String, Map<String, UFDouble>> zcMap = new HashMap<String, Map<String, UFDouble>>();
		
		if(deptChargePkList != null && !deptChargePkList.isEmpty()){
			StringBuffer bufStr = new StringBuffer();
			if(deptChargePkList != null && !deptChargePkList.isEmpty()){
				int count = 0;
				for(String pk_dept_charge : deptChargePkList){
					if(count == 0){
						bufStr.append("'" + pk_dept_charge + "'");
						count++;
					}else{
						bufStr.append(",'" + pk_dept_charge + "'");
					}
				}
				
				String pKdeptCharges = bufStr.toString();
				if(pKdeptCharges != null && pKdeptCharges.trim().length() > 0){
					String qrySql = "SELECT * FROM wa_deptcharge_b WHERE nvl(dr,0)=0 AND pk_dept_charge IN(" + pKdeptCharges + ")";
					//根据科室转账主表id集合查询对应的子表信息
					List<DeptChargeBVO> deptChargeBList = (List<DeptChargeBVO>) dao.executeQuery(qrySql, new BeanListProcessor(DeptChargeBVO.class));
					if(deptChargeBList != null && !deptChargeBList.isEmpty()){
						//kszzInMap: 组装各科室转账转入科室数据字典， key：转入科室pk_dept_in， value：科室转账主表主键pk_dept_charge
						if(kszzInMap != null && !kszzInMap.isEmpty()){
							for(String key : kszzInMap.keySet()){
								//科室转账(转入)详情map, key：项目pk（月考核奖、一次性奖金）, value：调转金额
								Map<String, UFDouble> inMap = new HashMap<String, UFDouble>();
								for(DeptChargeBVO vo : deptChargeBList){
									//转入科室
									if(key != null && vo.getPk_dept_charge() != null && key.equals(vo.getPk_dept_charge())){
										if(inMap.containsKey(vo.getPk_class_item())){
											inMap.put(vo.getPk_class_item(), inMap.get(vo.getPk_class_item()).add(vo.getNmny()));
										}else{
											inMap.put(vo.getPk_class_item(), vo.getNmny());
										}
									}
								}
								if(zrMap.containsKey(kszzInMap.get(key))){
									Map<String, UFDouble> temp = zrMap.get(kszzInMap.get(key));
									if(temp != null && !temp.isEmpty()){
										for(String tempkey : temp.keySet()){
											if(inMap.containsKey(tempkey)){
												UFDouble d = temp.get(tempkey).add(inMap.get(tempkey));
												temp.put(tempkey, d);
											}
										}
									}else{
										zrMap.put(kszzInMap.get(key), inMap);
									}
								}else{
									zrMap.put(kszzInMap.get(key), inMap);
								}
							}
						}
						
						//组装各科室转账转出科室数据字典， key：转出科室pk_dept_out， value：科室转账主表主键pk_dept_charge
						if(kszzOutMap != null && !kszzOutMap.isEmpty()){
							for(String key : kszzOutMap.keySet()){
								//科室转账(转出)详情map, key：项目pk（月考核奖、一次性奖金）, value：调转金额
								Map<String, UFDouble> outMap = new HashMap<String, UFDouble>();
								for(DeptChargeBVO vo : deptChargeBList){
									//转出科室
									if(key != null && vo.getPk_dept_charge() != null && key.equals(vo.getPk_dept_charge())){
										if(outMap.containsKey(vo.getPk_class_item())){
											outMap.put(vo.getPk_class_item(), outMap.get(vo.getPk_class_item()).add(vo.getNmny()));
										}else{
											outMap.put(vo.getPk_class_item(), vo.getNmny());
										}
									}
								}
								if(zcMap.containsKey(kszzOutMap.get(key))){
									Map<String, UFDouble> temp = zcMap.get(kszzOutMap.get(key));
									if(temp != null && !temp.isEmpty()){
										for(String tempkey : temp.keySet()){
											if(outMap.containsKey(tempkey)){
												UFDouble d = temp.get(tempkey).add(outMap.get(tempkey));
												temp.put(tempkey, d);
											}
										}
									}else{
										zcMap.put(kszzOutMap.get(key), outMap);
									}
								}else{
									zcMap.put(kszzOutMap.get(key), outMap);
								}
							}
						}
					}
				}
			}
		}
		
		retMap.put("zrMap", zrMap);
		retMap.put("zcMap", zcMap);
		//各科室结余map
		//retMap.put("ksjyMap", ksjyMap);
		//科室奖金信息
		retMap.put("ksjjMap", ksjjMap);
		
		
		//组装各科室当前期间-调入月考核奖金
		
		//组装各科室当前期间-调出月考核奖金
		
		//组装各科室当前期间-调入一次性奖金金额
		
		//组装各科室当前期间-调出一次性奖金金额
		
		//查询当前期间对应的所有的
		//String sql = "select * from wa_deptmny_b where Csourcebillhid = (select PK_FENPEI_H from wa_fenpei_h where nvl(dr,0)=0 and PK_CORP = '1002' and PK_BILLTYPE = '64RP' and VYEAR = '2018' and VPERIOD = '08' and ISZYBFFLAG = 'N' and VBILLSTATUS_AUDIT = 2)";
		
		return retMap;
	}

	@SuppressWarnings("unchecked")
	public Map<String, UFDouble> queryLastTotalJYByCond(String pk_corp, String vyear, String vperiod) throws BusinessException {
		BaseDAO dao = new BaseDAO();
		Map<String, UFDouble> map = new HashMap<String, UFDouble>();
		//根据当前期间转换获取其上一期间
		String curDate = vyear + "-" + vperiod;
		DateFormat format2 =  new SimpleDateFormat("yyyy-MM");
		Date date = new Date();
		try{
		    date = format2.parse(curDate);
		}catch(Exception e){
		    e.printStackTrace();
		}
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		SimpleDateFormat format =  new SimpleDateFormat("yyyy-MM");
		c.add(Calendar.MONTH, -1);
		String lastPeriod = format.format(c.getTime());
		String year = lastPeriod.substring(0, 4);
		String period = lastPeriod.substring(5, 7);
		String qrySql = "SELECT pk_dept, ndeptmny_after from wa_psn_item_h where nvl(dr,0)=0 and pk_corp = '" + pk_corp + "' and VYEAR = '" + year + "' and VPERIOD = '" + period + "'";
		List<PsnClassItemHVO> list = (List<PsnClassItemHVO>) dao.executeQuery(qrySql, new BeanListProcessor(PsnClassItemHVO.class));
		if(list != null && !list.isEmpty()){
			for(PsnClassItemHVO vo : list){
				map.put(vo.getPk_dept(), vo.getNdeptmny_after()); //ndeptmny_after: 分配后本月结余（分配后结存）
			}
		}
		return map;
	}

	@SuppressWarnings("unchecked")
	public boolean valiisDeptbillhas(String pk_corp, String pk_perioddept, String vyear, String vperiod) throws BusinessException {
		BaseDAO dao = new BaseDAO();
		String qrySql = "SELECT pk_dept, ndeptmny_after from wa_psn_item_h where nvl(dr,0)=0 and pk_corp = '" + pk_corp + "' and pk_dept = '" + pk_perioddept + "' and VYEAR = '" + vyear + "' and VPERIOD = '" + vperiod + "'";
		List<PsnClassItemHVO> list = (List<PsnClassItemHVO>) dao.executeQuery(qrySql, new BeanListProcessor(PsnClassItemHVO.class));
		if(list != null && !list.isEmpty()){
			return true;
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	public String valiOtherPeriodDept(String pk_corp, String pk_perioddept, String vyear, String vperiod, List<String> psnpkList) throws BusinessException {
		BaseDAO dao = new BaseDAO();
		StringBuffer retStr = null;
		String qrySql = "SELECT DISTINCT pk_psndoc FROM wa_psn_item_b WHERE nvl(dr,0)=0 AND pk_psn_item_h IN(SELECT pk_psn_item_h FROM wa_psn_item_h WHERE nvl(dr,0)=0 AND pk_corp = '" + pk_corp + "' AND pk_dept <> '" + pk_perioddept + "' AND vyear='" + vyear + "' AND vperiod='" + vperiod + "')  AND (nmny1 > 0 OR nmny2 > 0) ";
		//记录已在其他科室中分配奖金的人员pk
		List<String> otherPsnpksList = (List<String>) dao.executeQuery(qrySql, new ColumnListProcessor());
		//重复的人员pk
		List<String> samePsnpkList = new ArrayList<String>();
		if(otherPsnpksList != null && !otherPsnpksList.isEmpty()){
			for(String pk : psnpkList){
				if(otherPsnpksList.contains(pk)){
					if(!samePsnpkList.contains(pk)){
						samePsnpkList.add(pk);
					}
				}
			}
		}
		StringBuffer bufStr = new StringBuffer();
		if(samePsnpkList != null && !samePsnpkList.isEmpty()){
			int count = 0;
			for(String pk_psndoc : samePsnpkList){
				if(count == 0){
					bufStr.append("'" + pk_psndoc + "'");
					count++;
				}else{
					bufStr.append(",'" + pk_psndoc + "'");
				}
			}
			String wherePKpsndoc = bufStr.toString();
			if(wherePKpsndoc != null && wherePKpsndoc.trim().length() > 0){
				String sql = "SELECT psnname,psncode, pk_psndoc FROM bd_psndoc WHERE nvl(dr,0)=0 AND pk_psndoc in (" + wherePKpsndoc + ")";
				List<PsnDocVO> psnDocVOList = (List<PsnDocVO>) dao.executeQuery(sql, new BeanListProcessor(PsnDocVO.class));
				if(psnDocVOList != null && !psnDocVOList.isEmpty()){
					retStr = new StringBuffer();
					int count1 = 0;
					for(PsnDocVO vo : psnDocVOList){
						if(count1 == 0){
							retStr.append(vo.getPsnname() + "[" + vo.getPsncode()+ "]");
							count1++;
						}else{
							retStr.append("，'" + vo.getPsnname() + "[" + vo.getPsncode()+ "]");
						}
					}
				}
				
			}
		}
		if(retStr != null && retStr.toString().trim().length() > 0){
			String ret = retStr.toString();
			return ret;
		}else{
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public Map<String, String> qryWAClassitemMap(String pk_wa_class, String vyear, String vperiod, String cuserid) throws BusinessException {
		BaseDAO dao = new BaseDAO();
		// private String pk_wa_class = IHRPWABtn.PK_JIANG;//新华设置为默认奖金类别
		//查询【薪酬类别】参照项
		String sql = "SELECT wa_classitem.vname, wa_item.ifldwidth, wa_item.iflddecimal, wa_classitem.pk_wa_class, wa_classitem.pk_wa_classitem, wa_item.pk_wa_item "
					+ " FROM wa_classitem "
						+ "INNER JOIN wa_item ON wa_classitem.pk_wa_item=wa_item.pk_wa_item  "
					+ " WHERE "
						+ "(wa_classitem.cyear='" + vyear + "' AND wa_classitem.cperiod='" + vperiod + "' AND wa_classitem.ifromflag=20 "
								+ "AND wa_classitem.pk_wa_item IN ("
									+ "SELECT pk_wa_item FROM wa_itemright WHERE cuserid = '" + cuserid + "' UNION SELECT pk_wa_item FROM wa_itemright_group WHERE groupid IN (SELECT pk_role FROM sm_user_role WHERE sm_user_role.cuserid = '" + cuserid + "' )"
								+ ") "
						+ ") AND (wa_classitem.pk_wa_class = '" + pk_wa_class + "' ) ORDER BY wa_classitem.vname";
		
		List<WaClassItemVO> waClassItemVOList = (List<WaClassItemVO>) dao.executeQuery(sql, new BeanListProcessor(WaClassItemVO.class));
		Map<String, String> map = new HashMap<String, String>();
		if(waClassItemVOList != null && !waClassItemVOList.isEmpty()){
			for(WaClassItemVO vo : waClassItemVOList){
				if(!map.containsKey(vo.getPk_wa_classitem())){
					map.put(vo.getPk_wa_classitem(), vo.getVname());
				}
			}
		}
		return map;
	}

//	public String valiPsncl(String pk_corp, String pk_perioddept, String vyear, String vperiod, List<String> psnpkList) throws BusinessException {
//		BaseDAO dao = new BaseDAO();
//		StringBuffer retStr = null;
//		String qrySql = "SELECT pk_psndoc FROM wa_psn_item_b WHERE pk_psn_item_h IN(SELECT pk_psn_item_h FROM wa_psn_item_h WHERE nvl(dr,0)=0 AND pk_corp = '" + pk_corp + "' AND pk_dept <> '" + pk_perioddept + "' AND vyear='" + vyear + "' AND vperiod='" + vperiod + "')";
//		//记录已在其他科室中分配奖金的人员pk
//		List<String> otherPsnpksList = (List<String>) dao.executeQuery(qrySql, new ColumnListProcessor());
//		
//		StringBuffer bufStr = new StringBuffer();
//		if(psnpkList != null && !psnpkList.isEmpty()){
//			int count = 0;
//			for(String pk_psndoc : psnpkList){
//				if(count == 0){
//					bufStr.append("'" + pk_psndoc + "'");
//					count++;
//				}else{
//					bufStr.append(",'" + pk_psndoc + "'");
//				}
//			}
//			String wherePKpsndoc = bufStr.toString();
//			if(wherePKpsndoc != null && wherePKpsndoc.trim().length() > 0){
//				String sql = "SELECT psnname,psncode, pk_psndoc FROM bd_psndoc WHERE nvl(dr,0)=0 AND pk_psndoc in (" + wherePKpsndoc + ")";
//				List<PsnDocVO> psnDocVOList = (List<PsnDocVO>) dao.executeQuery(sql, new BeanListProcessor(PsnDocVO.class));
//				if(psnDocVOList != null && !psnDocVOList.isEmpty()){
//					retStr = new StringBuffer();
//					int count1 = 0;
//					for(PsnDocVO vo : psnDocVOList){
//						if(count1 == 0){
//							retStr.append(vo.getPsnname() + "[" + vo.getPsncode()+ "]");
//							count1++;
//						}else{
//							retStr.append("，'" + vo.getPsnname() + "[" + vo.getPsncode()+ "]");
//						}
//					}
//				}
//				
//			}
//		}
//		if(retStr != null && retStr.toString().trim().length() > 0){
//			String ret = retStr.toString();
//			return ret;
//		}else{
//			return null;
//		}
//	}
	
}
