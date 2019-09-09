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
	 * ���ݿ��ڿ��ң����ţ������Ϳ����ڼ��ѯ��ǰ�ڼ��¸ò����µ�������
	 */
	@SuppressWarnings("unchecked")
	public List<String> queryAllPsndocVOByCond(String pk_corp, String pk_perioddept, String vyear, String vperiod) throws BusinessException {
		//��ȡϵͳԤ�ò�����ͬ�ʽ��𷢷��ڼ����
		SysInitVO sysInitVO = SysinitAccessor.getInstance().getParaByAccuratePk_org(pk_corp, "TONGRENQJ");
		int tongrenqj = 2;
		if(sysInitVO != null && sysInitVO.getValue() != null){
			tongrenqj = Integer.parseInt(sysInitVO.getValue());
		}
		BaseDAO dao = new BaseDAO();
		List<String> pkpsnList = null;
		//���ݵ�ǰ�ڼ��ȡ��ǰ�����ڼ�
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
		//���ݵ�������ڼ���װ��Ӧ�Ŀ�ʼ����������
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
					//����ѡ��Ľ������pk�����Ӧ���ڼ��ѯ�ÿ����µĵ�ǰ�ڼ�Ŀ�����Աpk_psndoc
					pkpsnList = (List<String>) dao.executeQuery(qrySql, new ColumnListProcessor());
				}
			}
		}
		return pkpsnList;
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, String> queryPsnMapByCond(String pk_corp, String pk_perioddept, String vyear, String vperiod) throws BusinessException {
		//��ȡϵͳԤ�ò�����ͬ�ʽ��𷢷��ڼ����
		SysInitVO sysInitVO = SysinitAccessor.getInstance().getParaByAccuratePk_org(pk_corp, "TONGRENQJ");
		int tongrenqj = 2;
		if(sysInitVO != null && sysInitVO.getValue() != null){
			tongrenqj = Integer.parseInt(sysInitVO.getValue());
		}
		BaseDAO dao = new BaseDAO();
		List<DeptKqBVO> voList = null;
		//���ݵ�ǰ�ڼ��ȡ��ǰ�����ڼ�
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
		//���ݵ�������ڼ���װ��Ӧ�Ŀ�ʼ����������
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
					//����ѡ��Ľ������pk�����Ӧ���ڼ��ѯ�ÿ����µĵ�ǰ�ڼ�Ŀ�����Աpk_psndoc
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
	 * ���ݿ��ڿ��ң����ţ������Ϳ����ڼ��ѯ��ǰ�ڼ��¸ò����µ�������
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
					//����ѡ��Ľ������pk�����Ӧ���ڼ��ѯ�ÿ����µĵ�ǰ�ڼ�Ŀ�����Աpk_psndoc
					pkpsnList = (List<String>) dao.executeQuery(qrySql, new ColumnListProcessor());
				}
			}
		}
		return pkpsnList;
	}
	
	/**
	 * ��ѯ��ǰ������ȫ���¼�PK(������ǰpk)
	 * @param pk Ҫ��ѯ������
	 * @param voClass Ҫ��ѯ��VO
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<String> getChildByPK(BaseDAO dao, String pk_corp, String pk_perioddept, ArrayList<String> listPK, Class<DeptDocVO> voClass) {
		if(listPK==null){
			listPK = new ArrayList<String>();
		}
		try {
			String sql = "SELECT * from BD_DEPTDOC where nvl(dr,0)=0 and (pk_corp='" + IHRPSysParam.corp + "' or  pk_corp= '"+ pk_corp + "') and pk_fathedept ='" + pk_perioddept + "'";
			List<DeptDocVO> vosList = (List<DeptDocVO>) dao.executeQuery(sql, new BeanListProcessor(DeptDocVO.class));
			//ֻ�Ӳ����ڵ�
			if ( !listPK.contains(pk_perioddept)){
				listPK.add(pk_perioddept);
			}
			// �ݹ�
			for (DeptDocVO vo : vosList) {
				getChildByPK(dao, pk_corp, vo.getPrimaryKey(),listPK,voClass) ;
			}
		} catch (BusinessException e) {
			e.printStackTrace();
			System.out.println("��ѯ�¼���������");
		}
		return listPK;
	}

	@SuppressWarnings("unchecked")
	public Map<String, FenpeiItemBVO> queryFenpeiItemBVOMapByCond(String vyear, String vperiod, String pk_corp) throws BusinessException {
		Map<String, FenpeiItemBVO> map = null;
		BaseDAO dao = new BaseDAO();
		//������ҷ�������vo��FenpeiItemHVO����wa_fenpei_h;   ������ҷ����ӱ�vo��FenpeiItemBVO����wa_fenpei_b
		//select nmny, nmny1, pk_corp, pk_fenpei_b, pk_fenpei_h, pk_wa_dept, ts, dr from wa_fenpei_b where pk_fenpei_h = (select pk_fenpei_h from wa_fenpei_h where vyear = '2018' and vperiod = '08' and pk_dept is null and pk_corp = '1002'  and nvl(dr,0)=0 and VBILLSTATUS_AUDIT = 2)
		//VBILLSTATUS_AUDIT:"��д��:0","���ύ:1","����׼:2","��ͨ��:3","����:4","����:5"
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
		//��ѯ��ǰ�ڼ��Ӧ�Ľ�����ҷ��䷽��.
		//"select PK_FENPEI_H from wa_fenpei_h where nvl(dr,0)=0 and PK_CORP = '1002' and PK_BILLTYPE = '64RP' and VYEAR = '2018' and VPERIOD = '08' and ISZYBFFLAG = 'N' and VBILLSTATUS_AUDIT = 2";
		
		//��ѯ��ǰ�ڼ�ȫ�����ҵĽ���
		String qryJYSql = "SELECT * FROM wa_deptmny_h WHERE nvl(dr,0)=0 AND pk_corp = '" + pk_corp + "'";
		List<DeptMnyHVO> list = (List<DeptMnyHVO>) dao.executeQuery(qryJYSql, new BeanListProcessor(DeptMnyHVO.class));
		//��װ�����ҽ��������ֵ䣬 key������pk_dept�� value�����ҽ���nmny
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
		
		//��ѯ��ǰ�ڼ����ת����������
		String qryKszzSql = "SELECT * FROM wa_deptcharge WHERE nvl(dr,0)=0 AND pk_dept_charge IN(SELECT DISTINCT csourcebillhid FROM wa_deptmny_b WHERE nvl(dr,0)=0 AND PK_CORP = '" + pk_corp + "' AND VYEAR = '" + vyear + "' AND VPERIOD = '" + vperiod + "' AND CSOURCEBILLTYPECODE = '65RP')";
		List<DeptChargeHVO> kszzList = (List<DeptChargeHVO>) dao.executeQuery(qryKszzSql, new BeanListProcessor(DeptChargeHVO.class));
		//��װ������ת��ת����������ֵ䣬 key��ת�����pk_dept_in�� value������ת����������pk_dept_charge
		Map<String, String> kszzInMap = new HashMap<String, String>();
		//��װ������ת��ת�����������ֵ䣬 key��ת������pk_dept_out�� value������ת����������pk_dept_charge
		Map<String, String> kszzOutMap = new HashMap<String, String>();
		//����ת��������������pk_dept_charge
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
		
		//��װ������ת�������ֵ䣬 key��ת�����pk_dept�� value������ת������map��key����Ŀpk���¿��˽���һ���Խ���, value����ת���
		Map<String, Map<String, UFDouble>> zrMap = new HashMap<String, Map<String, UFDouble>>();
		//��װ������ת�������ֵ䣬 key��ת������pk_dept�� value������ת������map��key����Ŀpk���¿��˽���һ���Խ���, value����ת���
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
					//���ݿ���ת������id���ϲ�ѯ��Ӧ���ӱ���Ϣ
					List<DeptChargeBVO> deptChargeBList = (List<DeptChargeBVO>) dao.executeQuery(qrySql, new BeanListProcessor(DeptChargeBVO.class));
					if(deptChargeBList != null && !deptChargeBList.isEmpty()){
						//kszzInMap: ��װ������ת��ת����������ֵ䣬 key��ת�����pk_dept_in�� value������ת����������pk_dept_charge
						if(kszzInMap != null && !kszzInMap.isEmpty()){
							for(String key : kszzInMap.keySet()){
								//����ת��(ת��)����map, key����Ŀpk���¿��˽���һ���Խ���, value����ת���
								Map<String, UFDouble> inMap = new HashMap<String, UFDouble>();
								for(DeptChargeBVO vo : deptChargeBList){
									//ת�����
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
						
						//��װ������ת��ת�����������ֵ䣬 key��ת������pk_dept_out�� value������ת����������pk_dept_charge
						if(kszzOutMap != null && !kszzOutMap.isEmpty()){
							for(String key : kszzOutMap.keySet()){
								//����ת��(ת��)����map, key����Ŀpk���¿��˽���һ���Խ���, value����ת���
								Map<String, UFDouble> outMap = new HashMap<String, UFDouble>();
								for(DeptChargeBVO vo : deptChargeBList){
									//ת������
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
		//�����ҽ���map
		retMap.put("ksjyMap", ksjyMap);
		
		//��װ�����ҵ�ǰ�ڼ�-�����¿��˽���
		
		//��װ�����ҵ�ǰ�ڼ�-�����¿��˽���
		
		//��װ�����ҵ�ǰ�ڼ�-����һ���Խ�����
		
		//��װ�����ҵ�ǰ�ڼ�-����һ���Խ�����
		
		//��ѯ��ǰ�ڼ��Ӧ�����е�
		//String sql = "select * from wa_deptmny_b where Csourcebillhid = (select PK_FENPEI_H from wa_fenpei_h where nvl(dr,0)=0 and PK_CORP = '1002' and PK_BILLTYPE = '64RP' and VYEAR = '2018' and VPERIOD = '08' and ISZYBFFLAG = 'N' and VBILLSTATUS_AUDIT = 2)";
		
		return retMap;
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, Map<String, Map<String, UFDouble>>> queryMnyMapByCond(String pk_corp, String pk_perioddept, String vyear, String vperiod) throws BusinessException {
		Map<String, Map<String, Map<String, UFDouble>>> retMap = new HashMap<String, Map<String, Map<String, UFDouble>>>();
		BaseDAO dao = new BaseDAO();
		//��ѯ��ǰ�ڼ��Ӧ�Ľ�����ҷ��䷽��.
		//"select PK_FENPEI_H from wa_fenpei_h where nvl(dr,0)=0 and PK_CORP = '1002' and PK_BILLTYPE = '64RP' and VYEAR = '2018' and VPERIOD = '08' and ISZYBFFLAG = 'N' and VBILLSTATUS_AUDIT = 2";
		
		//���ݵ�ǰ�ڼ��ѯ�Ƿ���ڡ������ϴ����ˡ�����
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
			//��ѯ�������ϴ����ˡ��ӱ����ݣ�nmny��������Һϼƽ� nmny1���¿��˽��� nmny2��һ���Խ���
			String qryFenpeiB = "select * from wa_fenpei_b where pk_fenpei_h = '" + pk_fenpei_h + "'";
			fenpeiItemBList = (List<FenpeiItemBVO>) dao.executeQuery(qryFenpeiB, new BeanListProcessor(FenpeiItemBVO.class));
		}
		//�������-�ϼƽ��
		Map<String, UFDouble> nmnyMap = new HashMap<String, UFDouble>();
		//�������-�¿��˽����
		Map<String, UFDouble> nmny1Map = new HashMap<String, UFDouble>();
		//�������-һ���Խ�����
		Map<String, UFDouble> nmny2Map = new HashMap<String, UFDouble>();
		if(fenpeiItemBList != null && !fenpeiItemBList.isEmpty()){
			for(FenpeiItemBVO vo : fenpeiItemBList){
				//��װ�ϼƽ��map
				if(nmnyMap.containsKey(vo.getPk_wa_dept())){
					//�¿��˽�
					UFDouble nmny1 = vo.getNmny1() != null ? vo.getNmny1() : new UFDouble(0);
					//һ���Խ���
					UFDouble nmny2 = vo.getNmny2() != null ? vo.getNmny2() : new UFDouble(0);
					//�ϼƽ��
					UFDouble nmnyTotal = nmnyMap.get(vo.getPk_wa_dept()).add(nmny1.add(nmny2));
					nmnyMap.put(vo.getPk_wa_dept(), nmnyTotal);
				}else{
					//�¿��˽�
					UFDouble nmny1 = vo.getNmny1() != null ? vo.getNmny1() : new UFDouble(0);
					//һ���Խ���
					UFDouble nmny2 = vo.getNmny2() != null ? vo.getNmny2() : new UFDouble(0);
					//�ϼƽ��
					UFDouble nmnyTotal = nmny1.add(nmny2);
					nmnyMap.put(vo.getPk_wa_dept(), nmnyTotal);
				}
				
				//��װ�¿��˽�map
				if(nmny1Map.containsKey(vo.getPk_wa_dept())){
					UFDouble nmny1 = vo.getNmny1() != null ? vo.getNmny1() : new UFDouble(0);
					UFDouble nmny1Total = nmny1Map.get(vo.getPk_wa_dept()).add(nmny1);
					nmny1Map.put(vo.getPk_wa_dept(), nmny1Total);
				}else{
					UFDouble nmny1 = vo.getNmny1() != null ? vo.getNmny1() : new UFDouble(0);
					nmny1Map.put(vo.getPk_wa_dept(), nmny1);
				}
				
				//��װһ���Խ���map
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
		//��¼ȫԺ���ҽ���������Ϣ
		Map<String, Map<String, UFDouble>> ksjjMap = new HashMap<String, Map<String, UFDouble>>();
		ksjjMap.put("nmnyTotal", nmnyMap);
		ksjjMap.put("nmny1Total", nmny1Map);
		ksjjMap.put("nmny2Total", nmny2Map);
		
		
//		List<DeptMnyHVO> list = (List<DeptMnyHVO>) dao.executeQuery(qryJYSql, new BeanListProcessor(DeptMnyHVO.class));
//		//��װ�����ҽ��������ֵ䣬 key������pk_dept�� value�����ҽ���nmny
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
		
		//��ѯ��ǰ�ڼ����ת����������
		String qryKszzSql = "SELECT * FROM wa_deptcharge WHERE nvl(dr,0)=0 and vbillstatus = 1 and reddased=0 AND pk_dept_charge IN(SELECT DISTINCT csourcebillhid FROM wa_deptmny_b WHERE nvl(dr,0)=0 AND PK_CORP = '" + pk_corp + "' AND VYEAR = '" + vyear + "' AND VPERIOD = '" + vperiod + "' AND CSOURCEBILLTYPECODE = '65RP')";
		List<DeptChargeHVO> kszzList = (List<DeptChargeHVO>) dao.executeQuery(qryKszzSql, new BeanListProcessor(DeptChargeHVO.class));
		//��װ������ת��ת����������ֵ䣬 key��ת�����pk_dept_in�� value������ת����������pk_dept_charge
		Map<String, String> kszzInMap = new HashMap<String, String>();
		//��װ������ת��ת�����������ֵ䣬 key��ת������pk_dept_out�� value������ת����������pk_dept_charge
		Map<String, String> kszzOutMap = new HashMap<String, String>();
		//����ת��������������pk_dept_charge
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
		
		//��װ������ת�������ֵ䣬 key��ת�����pk_dept�� value������ת������map��key����Ŀpk���¿��˽���һ���Խ���, value����ת���
		Map<String, Map<String, UFDouble>> zrMap = new HashMap<String, Map<String, UFDouble>>();
		//��װ������ת�������ֵ䣬 key��ת������pk_dept�� value������ת������map��key����Ŀpk���¿��˽���һ���Խ���, value����ת���
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
					//���ݿ���ת������id���ϲ�ѯ��Ӧ���ӱ���Ϣ
					List<DeptChargeBVO> deptChargeBList = (List<DeptChargeBVO>) dao.executeQuery(qrySql, new BeanListProcessor(DeptChargeBVO.class));
					if(deptChargeBList != null && !deptChargeBList.isEmpty()){
						//kszzInMap: ��װ������ת��ת����������ֵ䣬 key��ת�����pk_dept_in�� value������ת����������pk_dept_charge
						if(kszzInMap != null && !kszzInMap.isEmpty()){
							for(String key : kszzInMap.keySet()){
								//����ת��(ת��)����map, key����Ŀpk���¿��˽���һ���Խ���, value����ת���
								Map<String, UFDouble> inMap = new HashMap<String, UFDouble>();
								for(DeptChargeBVO vo : deptChargeBList){
									//ת�����
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
						
						//��װ������ת��ת�����������ֵ䣬 key��ת������pk_dept_out�� value������ת����������pk_dept_charge
						if(kszzOutMap != null && !kszzOutMap.isEmpty()){
							for(String key : kszzOutMap.keySet()){
								//����ת��(ת��)����map, key����Ŀpk���¿��˽���һ���Խ���, value����ת���
								Map<String, UFDouble> outMap = new HashMap<String, UFDouble>();
								for(DeptChargeBVO vo : deptChargeBList){
									//ת������
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
		//�����ҽ���map
		//retMap.put("ksjyMap", ksjyMap);
		//���ҽ�����Ϣ
		retMap.put("ksjjMap", ksjjMap);
		
		
		//��װ�����ҵ�ǰ�ڼ�-�����¿��˽���
		
		//��װ�����ҵ�ǰ�ڼ�-�����¿��˽���
		
		//��װ�����ҵ�ǰ�ڼ�-����һ���Խ�����
		
		//��װ�����ҵ�ǰ�ڼ�-����һ���Խ�����
		
		//��ѯ��ǰ�ڼ��Ӧ�����е�
		//String sql = "select * from wa_deptmny_b where Csourcebillhid = (select PK_FENPEI_H from wa_fenpei_h where nvl(dr,0)=0 and PK_CORP = '1002' and PK_BILLTYPE = '64RP' and VYEAR = '2018' and VPERIOD = '08' and ISZYBFFLAG = 'N' and VBILLSTATUS_AUDIT = 2)";
		
		return retMap;
	}

	@SuppressWarnings("unchecked")
	public Map<String, UFDouble> queryLastTotalJYByCond(String pk_corp, String vyear, String vperiod) throws BusinessException {
		BaseDAO dao = new BaseDAO();
		Map<String, UFDouble> map = new HashMap<String, UFDouble>();
		//���ݵ�ǰ�ڼ�ת����ȡ����һ�ڼ�
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
				map.put(vo.getPk_dept(), vo.getNdeptmny_after()); //ndeptmny_after: ������½��ࣨ������棩
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
		//��¼�������������з��佱�����Աpk
		List<String> otherPsnpksList = (List<String>) dao.executeQuery(qrySql, new ColumnListProcessor());
		//�ظ�����Աpk
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
							retStr.append("��'" + vo.getPsnname() + "[" + vo.getPsncode()+ "]");
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
		// private String pk_wa_class = IHRPWABtn.PK_JIANG;//�»�����ΪĬ�Ͻ������
		//��ѯ��н����𡿲�����
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
//		//��¼�������������з��佱�����Աpk
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
//							retStr.append("��'" + vo.getPsnname() + "[" + vo.getPsncode()+ "]");
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
