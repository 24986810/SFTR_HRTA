package nc.impl.hi.hi_301;

/**
 * 人员信息维护。 创建日期：(2004-5-9 19:21:02)
 * 
 * @author：Administrator
 */

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.naming.NamingException;

import nc.bs.bd.b04.DeptdocDMO;
import nc.bs.dao.BaseDAO;
import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.bs.framework.server.util.NewObjectService;
import nc.bs.hr.utils.temptable.TempTableUtils;
import nc.bs.logging.Logger;
import nc.bs.ml.NCLangResOnserver;
import nc.bs.pub.SuperDMO;
import nc.bs.pub.SystemException;
import nc.bs.pub.billcodemanage.BillcodeGenerater;
import nc.bs.uap.lock.PKLock;
import nc.bs.uap.sf.facility.SFServiceFacility;
import nc.hr.frame.persistence.PersistenceDAO;
import nc.hr.utils.PubEnv;
import nc.impl.hi.pub.PsnInfoDMO;
import nc.itf.hi.HIDelegator;
import nc.itf.hi.IPsnInf;
import nc.itf.hr.bd.ISetdict;
import nc.itf.hr.bd.ITBMPsndocForTRN;
import nc.itf.hr.cm.IHrcmPsnChanged;
import nc.itf.hr.comp.IParValue;
import nc.itf.hr.comp.hrfi.IHrFiUtil;
import nc.itf.hr.pub.PubDelegator;
import nc.itf.hr.u9.psn.IPersonADDSV;
import nc.itf.hr.wa.IPsnChanged;
import nc.itf.uap.bd.def.IDefdoc;
import nc.itf.uap.bd.psn.IPsncl;
import nc.itf.uap.bd.refcheck.IReferenceCheck;
import nc.itf.uap.busibean.IDataPowerService;
import nc.itf.uap.pf.IPFConfig;
import nc.itf.uap.sf.ICreateCorpQueryService;
import nc.jdbc.framework.SQLParameter;
import nc.jdbc.framework.processor.MapListProcessor;
import nc.jdbc.framework.processor.VectorProcessor;
import nc.vo.bd.CorpVO;
import nc.vo.bd.b04.DeptdocVO;
import nc.vo.bd.b05.PsnclVO;
import nc.vo.bd.b06.PsndocVO;
import nc.vo.bd.psndoc.PsnbasdocVO;
import nc.vo.hi.hi_301.CtrlDeptVO;
import nc.vo.hi.hi_301.GeneralVO;
import nc.vo.hi.hi_306.DocApplyHVO;
import nc.vo.hi.pub.CommonValue;
import nc.vo.hr.bd.setdict.FlddictVO;
import nc.vo.hr.formulaset.BusinessFuncParser_sql;
import nc.vo.hr.global.GlobalTool;
import nc.vo.hr.tools.pub.CommonVO;
import nc.vo.hr.tools.pub.CommonVOProcessor;
import nc.vo.hrp.hrhi02.PSNDOCEXPORTVO;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.om.om_013.PsnTypeWorkoutRegVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.pub.VOStatus;
import nc.vo.pub.bill.BillTempletVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.msg.SysMessageParam;
import nc.vo.pub.msg.UserNameObject;
import nc.vo.pub.query.ConditionVO;
import nc.vo.rm.psndocdef.PsndocGrpDef15;
import nc.vo.sm.UserVO;
import nc.vo.sm.user.UserAndClerkVO;
import nc.vo.tam.tongren001.DeptKqBVO;
import nc.vo.tam.tongren001.DeptKqVO;
import nc.vo.tam.tongren003.PaibanWeekVO;
import nc.vo.tam.tongren003.PanbanWeekBVO;
import nc.vo.trn.records.PsndocDimissionVO;
import nc.vo.uap.rbac.RoleVO;

import org.apache.commons.lang.ArrayUtils;

public class PsnInfImpl implements IPsnInf {
	/**
	 * 异常处理
	 * 
	 * @param e
	 * @throws BusinessException
	 */
	private void throwBusinessException(Exception e) throws BusinessException {
		e.printStackTrace();//
		// Logger.error( e.getMessage());
		throw new BusinessException(e.getMessage());
	}

	// 人员跟踪信息集合，包括表名和模块标识
	private static final String[][] traceTables = {
		// { "hi_psndoc_deptchg", "HI" },/* 任职情况 */
		{ "hi_psndoc_ctrt", "HRCM" },/* 劳动合同 */
		// { "hi_psndoc_part", "HI" },/* 兼职情况 */
		{ "hi_psndoc_training", "TRM" },/* 培训记录 */
		{ "hi_psndoc_ass", "PE" },/* 考核记录 */
		// { "hi_psndoc_retire", "HI" },/* 离退待遇 */
		// { "hi_psndoc_orgpsn", "HI" },/* 虚拟组织 */
		// { "hi_psndoc_psnchg", "HI" }, /* 员工流动 */
		// { "hi_psndoc_dimission", "HI" } /* 离职情况 */
	};

	// 跟踪信息集映射，主要是为了高速存取
	private static final Hashtable traceTableMap = new Hashtable();
	static {

		// 初始化人员信息跟踪映射表
		initTraceTableMap();
	}

	/**
	 * PsnInfBO 构造子注解。
	 */
	public PsnInfImpl() {
		super();
	}

	/**
	 * 查询部门pk_deptdoc的所有子部门和其自身。 创建日期：(2004-5-10 11:38:35)
	 * 
	 * @return java.lang.String[]
	 * @param pk_dept
	 *            java.lang.String
	 */
	public DeptdocVO[] deptChildren(String pk_deptdoc, boolean includehrcanceled) throws BusinessException {

		try {
			// 部门为空返回空
			if (pk_deptdoc == null)
				return new DeptdocVO[] {};

			// 获取子部门
			// DeptdocVO[] depts = HIDelegator.getDeptdocQry().queryAllchildernDeptdoc(pk_deptdoc);
			DeptdocVO dept = HIDelegator.getDeptdocQry().findDeptdocVOByPK(pk_deptdoc);
			PsnInfDMO dmo = new PsnInfDMO();
			DeptdocVO[] depts = null;
			if (!includehrcanceled) {
				depts = dmo.queryDeptVOs(" hrcanceled <> 'Y' and innercode like '" + dept.getInnercode()
						+ "%' and pk_corp = '" + dept.getPk_corp() + "'");
			} else {
				depts = dmo.queryDeptVOs(" innercode like '" + dept.getInnercode() + "%' and pk_corp = '"
						+ dept.getPk_corp() + "'");
			}
			if (depts == null) // 没有子部门
				return new DeptdocVO[] { dept };
			Vector vTemp = new Vector();
			for (int i = 0; i < depts.length; i++) {
				vTemp.addElement(depts[i]);
			}
			depts = new DeptdocVO[vTemp.size()];
			vTemp.copyInto(depts);

			DeptdocVO[] alldept = new DeptdocVO[depts.length + 1];
			alldept[0] = dept;
			for (int i = 1; i <= depts.length; i++)
				alldept[i] = depts[i - 1];

			// 返回
			return alldept;
		} catch (Exception e) {
			Logger.debug(e.getMessage());
			throw new BusinessException(e.getMessage(), e);
		}

	}

	/**
	 * 取得用户的email地址
	 * 
	 * @param recievers
	 * @return
	 */
	public GeneralVO[] getRecieverEmails(String recievers) throws BusinessException {
		GeneralVO[] emailsaddr = null;
		try {
			PsnInfDMO dmo = new PsnInfDMO();
			String[] cuserids = recievers.split(",");
			String userids = parsePkcorps(cuserids);
			emailsaddr = dmo.getRecieverEmails(userids);
		} catch (Exception e) {

			throwBusinessException(e);
		}
		return emailsaddr;
	}

	/**
	 * 获取拥有权限的部门。 创建日期：(2004-5-10 9:16:14)
	 * 
	 * @return java.lang.String[]
	 * @param userID
	 *            java.lang.String
	 * @param pk_corp
	 *            java.lang.String
	 * @exception java.lang.Exception
	 *                异常说明。
	 */
	private DeptdocVO[] hasPower(String userID, String pk_corp, boolean useDeptPower, String modulecode,
			boolean includeHrCancled) throws java.lang.Exception {

		// 查出所有有权限的部门主键
		IDataPowerService datapower = ((IDataPowerService) NCLocator.getInstance().lookup(
				IDataPowerService.class.getName()));
		String deptpower = null;
		if (useDeptPower) {
			deptpower = datapower.getSubSql("bd_deptdoc", "部门档案", userID, pk_corp);
		}

		// 如果没有启用权限，则为该公司下的所有部门。
		PsnInfDMO dmo = new PsnInfDMO();
		String cond = "";
		// 不查看已撤销部门
		if (deptpower != null && deptpower.toLowerCase().trim().startsWith("select")) {
			cond = "pk_deptdoc in (" + deptpower + ") and";
		}

		if (modulecode != null && "600704".equals(modulecode)) {// 不查看已封存的部门
			if (includeHrCancled) {// 包含撤销
				cond += " dr=0 and ((canceled = 'N' ) or ( canceled is null)) and pk_corp = '"
					+ pk_corp
					+ "' order by  (case when bd_deptdoc.showorder is null then 999999 else bd_deptdoc.showorder end),bd_deptdoc.deptcode asc";
			} else {
				cond += " dr=0 and ((canceled = 'N' and hrcanceled = 'N') or ( canceled is null and hrcanceled is null)) and pk_corp = '"
					+ pk_corp
					+ "' order by (case when bd_deptdoc.showorder is null then 999999 else bd_deptdoc.showorder end),bd_deptdoc.deptcode asc";
			}
		} else {
			if (includeHrCancled) {// 包含撤销
				cond += " dr=0  and pk_corp = '"// and ((canceled = 'N' ) or ( canceled is null))
					+ pk_corp
					+ "' order by (case when bd_deptdoc.showorder is null then 999999 else bd_deptdoc.showorder end),bd_deptdoc.deptcode asc";
			} else {
				cond += " dr=0 and (hrcanceled = 'N' or hrcanceled is null) and pk_corp = '"
					+ pk_corp
					+ "' order by (case when bd_deptdoc.showorder is null then 999999 else bd_deptdoc.showorder end),bd_deptdoc.deptcode asc";
			}
		}
		return dmo.queryDeptVOs(cond);

	}

	/**
	 * 初始化人员信息跟踪映射表
	 */
	private static void initTraceTableMap() {
		for (int i = 0; i < traceTables.length; i++) {
			traceTableMap.put(traceTables[i][0], traceTables[i][1]);
		}
	}

	/**
	 * 锁记录
	 * 
	 * @param psnpk
	 * @return
	 * @throws java.lang.Exception
	 */
	public boolean lockPsn(String psnpk, String userid) {
		return PKLock.getInstance().acquireLock(psnpk, userid, null);

	}

	public void freeLockPsn(String psnpk, String userid) {
		PKLock.getInstance().releaseLock(psnpk, userid, null);
	}

	private void checkBeforeIntoDoc(GeneralVO[] intoDocData, String[] pk_psndocs, String userid)
	throws BusinessException {
		java.util.Vector v = new java.util.Vector();
		Vector vPkPsn = new Vector();
		IDefdoc defdoc = NCLocator.getInstance().lookup(IDefdoc.class);
		SuperDMO dmo = new SuperDMO();
		for (int i = 0; i < intoDocData.length; i++) {
			String pk = (String) intoDocData[i].getFieldValue("pk_psndoc");
			PsndocVO docvo = (PsndocVO)dmo.queryByPrimaryKey(PsndocVO.class, pk);
			PsnbasdocVO basvo = (PsnbasdocVO)dmo.queryByPrimaryKey(PsnbasdocVO.class, docvo.getPk_psnbasdoc());

			String pk1 = basvo.getPenelauth();  //pane1.getRefPK();//0001A610000000001LLM 干部  0001A610000000001LLJ 工人
			String code2 = docvo.getGroupdef8(); 
			if(code2!=null&&code2.trim().length()>0){
				code2 = defdoc.findDefDocVOByPK(code2).getCode();
			}
			String pk4 = docvo.getGroupdef16(); 
			String pk26 = docvo.getGroupdef26();
			if(pk26!=null&&(pk26.trim().equals("00016910000000002OHE")||pk26.trim().equals("00016910000000006CYZ"))){//就新华医院 和新华派遣的 01 和0401


				if(pk1!=null&&pk1.trim().equals("0001A610000000001LLM")){
					if(code2!=null&&code2.trim().length()==3){
						if(code2.trim().endsWith("1")){
							docvo.setGroupdef22("0001691000000000TX3R");
						}else if(code2.trim().endsWith("2")){
							docvo.setGroupdef22("0001691000000000TX3S");
						}else if(code2.trim().endsWith("3")){
							docvo.setGroupdef22("0001691000000000TX3T");
						}else if(code2.trim().endsWith("4")||code2.trim().endsWith("5")){
							docvo.setGroupdef22("0001691000000000TX3U");
						}else{
							docvo.setGroupdef22("0001691000000000TX3V");
						}
					}
				}else if(pk1!=null&&pk1.trim().equals("0001A610000000001LLJ")){
					if(pk4!=null&&pk4.trim().length()>0){
						if(pk4.equals("0001691000000000TX3X")){
							docvo.setGroupdef22("0001691000000000TX3X");
						}else if(pk4.equals("000169100000000033R3")){
							docvo.setGroupdef22("0001691000000000TX3Y");
						}else if(pk4.equals("000169100000000033R2")){
							docvo.setGroupdef22("0001691000000000TX3Z");
						}else if(pk4.equals("000169100000000033R5")||pk4.equals("000169100000000033R6")){
							docvo.setGroupdef22("0001691000000000TX41");
						}else {
							docvo.setGroupdef22("0001691000000000TX40");
						}
					}else{
						docvo.setGroupdef22("0001691000000000TX40");
					}
				}

			}
			String cardcode = basvo.getId().toUpperCase();
			basvo.setId(cardcode);
			dmo.update(docvo, new String[]{"groupdef22"});
			dmo.update(basvo, new String[]{"id"});
			if (!lockPsn(pk, userid)) {

				for (int k = 0; k < v.size(); k++) {
					freeLockPsn((String) v.elementAt(k), userid);
				}
				throw new BusinessException(nc.bs.ml.NCLangResOnserver.getInstance().getStrByID("600704",
				"UPP600704-000082")/*
				 * @res "当前列表中的人员:"
				 */
				+ intoDocData[i].getAttributeValue("psnname").toString()
				+ nc.bs.ml.NCLangResOnserver.getInstance().getStrByID("600704", "UPP600704-000083")/*
				 * @res
				 * "正在被其他用户操作，操作失败"
				 */);
			}
			String sql = " select 1 from bd_psndoc p inner join bd_psnbasdoc b on p.pk_psnbasdoc=b.pk_psnbasdoc where (p.pk_psndoc ='" + pk + "' or b.id='"+cardcode+"') and p.indocflag ='Y' ";
			boolean isinto = isRecordExist(sql);

			v.addElement(pk);
			if (isinto) {
				// ****************NCdp201031295 为一个已入职的人员做转入档案处理，会产生加锁现象 2009.10.15**********************
				for (int k = 0; k < v.size(); k++) {
					freeLockPsn((String) v.elementAt(k), userid);
				}
				// **************************************
				// throw new
				// BusinessException(intoDocData[i].getAttributeValue("psnname").toString()+"已经转入人员档案，不能再次入职!");
				throw new BusinessException(intoDocData[i].getAttributeValue("psnname").toString()
						+ nc.bs.ml.NCLangResOnserver.getInstance().getStrByID("600700", "UPP600700-000119")/*
						 * @res
						 * "已经转入人员档案，不能再次入职!"
						 */);
			}
			if (!vPkPsn.contains(pk)) {// wangkf add
				vPkPsn.addElement(pk);
			}
		}
	}

	/**
	 * 转入人员档案,同时把 任职子集 的数据同步到 工作履历表。
	 * 
	 * @author wangkf 王开福
	 * @param psnList
	 * @param pk_psndocs
	 * @throws BusinessException
	 */
	public void intoDoc(GeneralVO[] psnList, String[] pk_psndocs, String userid) throws BusinessException {
		// 校验（原ui端的校验移到此处）
		checkBeforeIntoDoc(psnList, pk_psndocs, userid);

		try {
			if (psnList == null || psnList.length == 0) {
				return;
			}
			PsnInfDMO dmo = new PsnInfDMO();
			HashMap accToSubDeptchgMap = new HashMap();
			Vector vDeptchg = new Vector();
			HashMap accToSubDimisMap = new HashMap();
			Vector vDimis = new Vector();
			// 为合同模块所做连续签订次数 变更
			Vector vCtrt = new Vector();
			String[] pk_psnbasdocs = null;

			dmo.intoDoc(psnList);
			dmo.intoDocBas(psnList);
			// add by sunxj 2010-04-27 H型接口插件 start
			// 增加人员增量数据
			UFBoolean waHrFiFlagTemp = PubDelegator.getIParValue().getParaBoolean(null, "WA-HRFI");
			boolean waHrFiFlag = waHrFiFlagTemp == null ? false : waHrFiFlagTemp.booleanValue();
			if (waHrFiFlag) {
				insertPsndocAdd(psnList);
			}
			// add by sunxj 2010-04-27 H型接口插件 end
			String personpk_corp = (String) psnList[0].getAttributeValue("pk_corp");
			GeneralVO[] relatedFields = queryAllRelatedTableField(personpk_corp);
			// 放入映射表中
			for (int i = 0; i < relatedFields.length; i++) {
				String table = (String) relatedFields[i].getAttributeValue("setcode");
				String field = (String) relatedFields[i].getAttributeValue("fldcode");
				String accfield = (String) relatedFields[i].getAttributeValue("accfldcode");
				if (table.equalsIgnoreCase("hi_psndoc_deptchg")) {
					// 取出预置信息
					Hashtable<String, String> h = new Hashtable<String, String>();
					// 预置部分信息
					String[] fieldNames = { "pk_deptdoc", "pk_psncl", "pk_jobrank", "pk_jobserial", "pk_om_duty",
							"pk_detytype", "pk_postdoc", "begindate", "pk_dutyrank" };
					for (int j = 0; j < fieldNames.length; j++) {
						h.put(fieldNames[j], fieldNames[j]);

					}
					// 预置、同步管理档案信息项都有时以预置为主
					if (!h.contains(field)) {
						accToSubDeptchgMap.put(accfield, field);
						vDeptchg.addElement(accfield);
					}
				}
				if (table.equalsIgnoreCase("hi_psndoc_dimission")) {
					Hashtable<String, String> h = new Hashtable<String, String>();
					// 预置部分信息
					String[] fieldNames = { "pkdeptafter", "pkdeptbefore", "pkomdutybefore", "pkpostbefore",
							"psnclafter", "leavedate", "psnclbefore" };
					for (int j = 0; j < fieldNames.length; j++) {
						h.put(fieldNames[j], fieldNames[j]);

					}
					// 预置、同步管理档案信息项都有时以预置为主
					if (!h.contains(field)) {
						accToSubDimisMap.put(accfield, field);
						vDimis.addElement(accfield);
					}
				}
			}
			String[] relationdeptchg = null;
			if (vDeptchg.size() > 0) {
				relationdeptchg = new String[vDeptchg.size()];
				vDeptchg.copyInto(relationdeptchg);
			}
			String[] relationdimis = null;
			if (vDimis.size() > 0) {
				relationdimis = new String[vDimis.size()];
				vDimis.copyInto(relationdimis);
			}
			HashMap hash = new HashMap(); // 存放返聘再聘人员的新老pk对应
			// add by zhyan 2006-06-03 转入人员档案时增加人员的任职记录或者离职记录
			SuperDMO ssdmo = new SuperDMO();
			for (int i = 0; i < psnList.length; i++) {
				String isreturn = (String) psnList[i].getAttributeValue("isreturn");
				String pk_corp = (String) psnList[i].getAttributeValue("pk_corp");
				String pk_psnbasdoc = (String) psnList[i].getAttributeValue("pk_psnbasdoc");
				boolean isnotzaizhi = dmo.isNOtZaizhiPsnclscope((String) psnList[i].getAttributeValue("pk_psncl"));
				String pk_psndoc = (String) psnList[i].getAttributeValue("pk_psndoc");
				

				//sqt 二次开发 为考勤档案赋值 考勤部门
				String pk_deptdoc = (String) psnList[i].getAttributeValue("pk_deptdoc");
				
				DeptKqBVO[] kqbvos = (DeptKqBVO[])ssdmo.queryByWhereClause(DeptKqBVO.class, " isnull(dr,0)=0 and pk_psndoc='"+pk_psndoc+"' ");
				if(kqbvos==null||kqbvos.length<=0){
					DeptKqVO[] kqvos = (DeptKqVO[])ssdmo.queryByWhereClause(DeptKqVO.class, " isnull(dr,0)=0 and pk_hrp_dept='"+pk_deptdoc+"' ");
					if(kqvos!=null&&kqvos.length>0){
						PsnbasdocVO basvo = (PsnbasdocVO)ssdmo.queryByPrimaryKey(PsnbasdocVO.class, pk_psnbasdoc);
						PsndocVO psnvo = (PsndocVO)ssdmo.queryByPrimaryKey(PsndocVO.class, pk_psndoc);
						DeptKqBVO bvo = new DeptKqBVO();
						bvo.setBisnew(new UFBoolean(true));
						bvo.setDr(0);
						bvo.setDstartdate(psnvo.getIndutydate());
						bvo.setPk_dept(kqvos[0].getPrimaryKey());
						bvo.setPk_psndoc(pk_psndoc);
						ssdmo.insert(bvo);
					}
				}

				// psnList[i] = dmo.queryDetail(psnList[i],relation);
				//add by lq 如果是离职返聘的，并且人员类别为离职的也当在职处理。---------------
				if ((!isnotzaizhi) || "Y".equals(isreturn)) {// 在职和其他人员
					psnList[i] = dmo.queryDetail(psnList[i], relationdeptchg);
					String sql1 = " select 1 from bd_psndoc where psnclscope = 0 and indocflag = 'Y' and pk_psnbasdoc ='"
						+ pk_psnbasdoc + "' and pk_corp <>'" + pk_corp + "'";
					boolean existrehire = isRecordExist(sql1);
					if (existrehire) {// 如果此人员在其他公司返聘并转入档案，则不能在本公司入职
						// throwBusinessException(new Exception("该人员已经在其他公司入职，不能在本公司入职！"));
						throwBusinessException(new Exception(NCLangResOnserver.getInstance().getStrByID("600700",
						"UPP600700-000238")/* @res "该人员已经在其他公司入职，不能在本公司入职！" */));
					}
					// 再聘人员的查询，看此人员在本公司有没有非在职记录，如果有，同返聘人员处理
					String sql = " select 1 from bd_psndoc where psnclscope in (2,3,4,5) and indocflag ='Y' and pk_psnbasdoc ='"
						+ pk_psnbasdoc + "' and pk_corp ='" + pk_corp + "' and pk_psndoc <> '" + pk_psndoc + "'";
					boolean rehire = isRecordExist(sql);
					// 如果是返聘人员或者再聘人员，则将需要查询原主键，更新到本工作信息，删除以前的非在职记录
					if (isreturn.equalsIgnoreCase("Y") || rehire) {
						// 查询返聘人员在本公司的以前的工作信息
						String updatepk_psndoc = dmo.getPkpsndoc(pk_psnbasdoc, pk_corp,pk_psndoc);
						if (updatepk_psndoc != null) {// 如果是跨公司返聘，则直接转人员档案
							dmo.updatePsnpkAnddel(pk_psndoc, updatepk_psndoc);
							hash.put(pk_psndoc, updatepk_psndoc);
							pk_psndoc = updatepk_psndoc;// 将原pk赋值给当前pk，作增加任职记录用
						}
					}
					// 任职开始日期规则：如果是第一次任职，首先取最新到岗日期，如果没有，则取到职日期。如果不是第一次任职，则只取最新到岗日期。
					// 到职日期
					UFDate indutydate = psnList[i].getAttributeValue("indutydate") == null ? null : new UFDate(
							psnList[i].getAttributeValue("indutydate").toString().trim());
					// 最新到岗日期
					UFDate onpostdate = psnList[i].getAttributeValue("onpostdate") == null ? null : new UFDate(
							psnList[i].getAttributeValue("onpostdate").toString().trim());
					// 由最新到岗日期、任职记录取得新任职记录的开始日期
					UFDate begindate = dmo.updateDeptchgRecornum(1, pk_psndoc, onpostdate, indutydate);

					// ---------------------二次开发的代码zg add start
					// 判断是否为任职记录的修改
					boolean flag = HIDelegator.getIHRhiQBS().checkPsnWorkDate(pk_corp, pk_psndoc, begindate, null);
					if (flag) {
						// throw new Exception("任职日期和其他公司的任职日期有冲突");
						throw new Exception(NCLangResOnserver.getInstance().getStrByID("600700", "UPP600700-000239")/*
						 * @res
						 * "任职日期和其他公司的任职日期有冲突"
						 */);
					}
					// ----------------------二次开发的代码zg add end
					// 如果存在任职记录，且任职记录开始日期为空或者到职日期在最新任职记录结束日期之前时，不写入任职开始日期
					if (begindate == null) {
						psnList[i].setFieldValue("indutydate", null);
					} else {
						psnList[i].setFieldValue("indutydate", begindate);
					}
					psnList[i].setAttributeValue("pk_psndoc", pk_psndoc);// 对人员pk重新赋值，如果是返聘再聘，就修改，否则不变
					dmo.insertDeptChg(psnList[i], relationdeptchg, accToSubDeptchgMap);
					//如果是本公司返聘人员或者再聘人员，更新原有的薪酬记录
					if(isreturn.equalsIgnoreCase("Y") || rehire){
						//返聘再聘人员同步薪资福利档案信息，同步的类型为在职人员，不分本公司或者是跨公司
						updateWa(pk_psndoc,dmo.getPsnchgpk(pk_psndoc),IPsnChanged.NORMARL,pk_corp,begindate);

					}
				} else {// 非在职人员
					psnList[i] = dmo.queryDetailForDimis(psnList[i], relationdimis);
					String leavedate = dmo.queryDimissionDate(pk_psndoc);
					String outdutydate = dmo.queryOutdutyDate(pk_psndoc);
					dmo.updateRecornum("hi_psndoc_dimission", new Integer(1), pk_psndoc);
					if (leavedate != null && outdutydate != null
							&& UFDate.getDate(leavedate).after(UFDate.getDate(outdutydate))) {
						outdutydate = null;
					}
					// 如果离职人员存在任职记录，则离职记录的离职前部门为任职记录的部门
					GeneralVO deptchginfo = dmo.getChginfo(pk_psndoc, pk_corp);
					dmo.insertDismissionChd(psnList[i], outdutydate, deptchginfo, relationdimis, accToSubDimisMap);
				}
				//处理考勤信息
				if(SFServiceFacility.getCreateCorpQueryService().isEnabled(pk_corp, "TA")){
					try{
						ITBMPsndocForTRN ita =  (ITBMPsndocForTRN)NewObjectService.newInstance("hrta","nc.impl.tbm.pub001.TBMPsndocForTRNImpl");
						UFDate indutyDate = (UFDate) psnList[i].getAttributeValue("indutydate") == null ? PubEnv.getServerDate() 
								: (UFDate) psnList[i].getAttributeValue("indutydate");
						ita.addTBMPsndocForInduty_yl(pk_corp, new String[]{pk_psndoc}, indutyDate,pk_deptdoc);
					}
					catch(Exception eee){
						throw new BusinessException("调用TA接口出错：ITBMPsndocForTRN.addTBMPsndocForInduty",eee);
					}
				}
			}
			// 同步工作履历
			if (psnList != null) {
				for (int i = 0; i < psnList.length; i++) {
					String pk_psndoc = (String) psnList[i].getAttributeValue("pk_psndoc");
					String isreturn = (String) psnList[i].getAttributeValue("isreturn");
					String pk_psnbasdoc = (String) psnList[i].getAttributeValue("pk_psnbasdoc");
					vCtrt.addElement(pk_psnbasdoc);

					if (hash.get(pk_psndoc) != null) {
						pk_psndoc = (String) hash.get(pk_psndoc);
					}
					// 查询任职记录信息
					GeneralVO[] subInfos = dmo.queryDeptChgInfos(pk_psndoc, isreturn);
					if (subInfos != null && subInfos.length > 0) {
						// 返聘时将最新任职记录同步到工作履历
						if (isreturn != null && "Y".equalsIgnoreCase(isreturn)) {
							dmo.updateRecornumforWork("hi_psndoc_work", 1, pk_psnbasdoc);
						} else {
							dmo.updateRecornum("hi_psndoc_work", new Integer(subInfos.length), pk_psndoc);
						}
						dmo.insertHiPsnWork(subInfos);
					}
				}
			}
			// 处理合同签订次数
			if (vCtrt.size() > 0 && BSUtil.isModuleInstalled(personpk_corp, "HRCM")) {
				pk_psnbasdocs = new String[vCtrt.size()];
				vCtrt.copyInto(pk_psnbasdocs);
				IHrcmPsnChanged hrcm = (IHrcmPsnChanged) NCLocator.getInstance()
				.lookup(IHrcmPsnChanged.class.getName());
				hrcm.chgContinueTimeForPsn(pk_psnbasdocs);
			}


			if (psnList != null && psnList.length > 0) {
				String pk_corp = (String) psnList[0].getAttributeValue("pk_corp");
				HIDelegator.getIPersonADDSV().operatePersonInfo(pk_psndocs, IPersonADDSV.ADD, pk_corp);
			}
		} catch (Exception e) {

			throwBusinessException(e);
		} finally {
			for (int i = 0; i < pk_psndocs.length; i++) {
				freeLockPsn(pk_psndocs[i], userid);
			}
		}
	}

	/**
	 * 原转入人员档案方法 转入人员档案,同时把 任职子集 的数据同步到 工作履历表。
	 * 
	 * @author wangkf 王开福
	 * @param psnList
	 * @param pk_psndocs
	 * @throws BusinessException
	 */
	public void intoDoc(GeneralVO[] psnList, String[] pk_psndocs) throws BusinessException {
		try {
			if (psnList == null || psnList.length == 0) {
				return;
			}
			PsnInfDMO dmo = new PsnInfDMO();
			HashMap accToSubDeptchgMap = new HashMap();
			Vector vDeptchg = new Vector();
			HashMap accToSubDimisMap = new HashMap();
			Vector vDimis = new Vector();
			// 为合同模块所做连续签订次数 变更
			Vector vCtrt = new Vector();
			String[] pk_psnbasdocs = null;

			dmo.intoDoc(psnList);
			dmo.intoDocBas(psnList);
			// add by sunxj 2010-04-27 H型接口插件 start
			// 增加人员增量数据
			UFBoolean waHrFiFlagTemp = PubDelegator.getIParValue().getParaBoolean(null, "WA-HRFI");
			boolean waHrFiFlag = waHrFiFlagTemp == null ? false : waHrFiFlagTemp.booleanValue();
			if (waHrFiFlag) {
				insertPsndocAdd(psnList);
			}
			// add by sunxj 2010-04-27 H型接口插件 end
			String personpk_corp = (String) psnList[0].getAttributeValue("pk_corp");
			GeneralVO[] relatedFields = queryAllRelatedTableField(personpk_corp);
			// 放入映射表中
			for (int i = 0; i < relatedFields.length; i++) {
				String table = (String) relatedFields[i].getAttributeValue("setcode");
				String field = (String) relatedFields[i].getAttributeValue("fldcode");
				String accfield = (String) relatedFields[i].getAttributeValue("accfldcode");
				if (table.equalsIgnoreCase("hi_psndoc_deptchg")) {
					// 取出预置信息
					Hashtable<String, String> h = new Hashtable<String, String>();
					// 预置部分信息
					String[] fieldNames = { "pk_deptdoc", "pk_psncl", "pk_jobrank", "pk_jobserial", "pk_om_duty",
							"pk_detytype", "pk_postdoc", "begindate", "pk_dutyrank" };
					for (int j = 0; j < fieldNames.length; j++) {
						h.put(fieldNames[j], fieldNames[j]);

					}
					// 预置、同步管理档案信息项都有时以预置为主
					if (!h.contains(field)) {
						accToSubDeptchgMap.put(accfield, field);
						vDeptchg.addElement(accfield);
					}
				}
				if (table.equalsIgnoreCase("hi_psndoc_dimission")) {
					Hashtable<String, String> h = new Hashtable<String, String>();
					// 预置部分信息
					String[] fieldNames = { "pkdeptafter", "pkdeptbefore", "pkomdutybefore", "pkpostbefore",
							"psnclafter", "leavedate", "psnclbefore" };
					for (int j = 0; j < fieldNames.length; j++) {
						h.put(fieldNames[j], fieldNames[j]);

					}
					// 预置、同步管理档案信息项都有时以预置为主
					if (!h.contains(field)) {
						accToSubDimisMap.put(accfield, field);
						vDimis.addElement(accfield);
					}
				}
			}
			String[] relationdeptchg = null;
			if (vDeptchg.size() > 0) {
				relationdeptchg = new String[vDeptchg.size()];
				vDeptchg.copyInto(relationdeptchg);
			}
			String[] relationdimis = null;
			if (vDimis.size() > 0) {
				relationdimis = new String[vDimis.size()];
				vDimis.copyInto(relationdimis);
			}
			HashMap hash = new HashMap(); // 存放返聘再聘人员的新老pk对应
			// add by zhyan 2006-06-03 转入人员档案时增加人员的任职记录或者离职记录
			for (int i = 0; i < psnList.length; i++) {
				String isreturn = (String) psnList[i].getAttributeValue("isreturn");
				String pk_corp = (String) psnList[i].getAttributeValue("pk_corp");
				String pk_psnbasdoc = (String) psnList[i].getAttributeValue("pk_psnbasdoc");
				boolean isnotzaizhi = dmo.isNOtZaizhiPsnclscope((String) psnList[i].getAttributeValue("pk_psncl"));
				String pk_psndoc = (String) psnList[i].getAttributeValue("pk_psndoc");
				//sqt 二次开发 为考勤档案赋值 考勤部门
				String pk_deptdoc = (String) psnList[i].getAttributeValue("pk_deptdoc");
				// psnList[i] = dmo.queryDetail(psnList[i],relation);

				if (!isnotzaizhi) {// 在职和其他人员
					psnList[i] = dmo.queryDetail(psnList[i], relationdeptchg);
					String sql1 = " select 1 from bd_psndoc where psnclscope = 0 and indocflag = 'Y' and pk_psnbasdoc ='"
						+ pk_psnbasdoc + "' and pk_corp <>'" + pk_corp + "'";
					boolean existrehire = isRecordExist(sql1);
					if (existrehire) {// 如果此人员在其他公司返聘并转入档案，则不能在本公司入职
						// throwBusinessException(new Exception("该人员已经在其他公司入职，不能在本公司入职！"));
						throwBusinessException(new Exception(NCLangResOnserver.getInstance().getStrByID("600700",
						"UPP600700-000238")/* @res "该人员已经在其他公司入职，不能在本公司入职！" */));
					}
					// 再聘人员的查询，看此人员在本公司有没有非在职记录，如果有，同返聘人员处理
					String sql = " select 1 from bd_psndoc where psnclscope in (2,3,4,5) and indocflag ='Y' and pk_psnbasdoc ='"
						+ pk_psnbasdoc + "' and pk_corp ='" + pk_corp + "' and pk_psndoc <> '" + pk_psndoc + "'";
					boolean rehire = isRecordExist(sql);
					// 如果是返聘人员或者再聘人员，则将需要查询原主键，更新到本工作信息，删除以前的非在职记录
					if (isreturn.equalsIgnoreCase("Y") || rehire) {
						// 查询返聘人员在本公司的以前的工作信息
						String updatepk_psndoc = dmo.getPkpsndoc(pk_psnbasdoc, pk_corp,pk_psndoc);
						if (updatepk_psndoc != null) {// 如果是跨公司返聘，则直接转人员档案
							dmo.updatePsnpkAnddel(pk_psndoc, updatepk_psndoc);
							hash.put(pk_psndoc, updatepk_psndoc);
							pk_psndoc = updatepk_psndoc;// 将原pk赋值给当前pk，作增加任职记录用
						}
					}
					// 任职开始日期规则：如果是第一次任职，首先取最新到岗日期，如果没有，则取到职日期。如果不是第一次任职，则只取最新到岗日期。
					// 到职日期
					UFDate indutydate = psnList[i].getAttributeValue("indutydate") == null ? null : new UFDate(
							psnList[i].getAttributeValue("indutydate").toString().trim());
					// 最新到岗日期
					UFDate onpostdate = psnList[i].getAttributeValue("onpostdate") == null ? null : new UFDate(
							psnList[i].getAttributeValue("onpostdate").toString().trim());
					// 由最新到岗日期、任职记录取得新任职记录的开始日期
					UFDate begindate = dmo.updateDeptchgRecornum(1, pk_psndoc, onpostdate, indutydate);

					// ---------------------二次开发的代码zg add start
					// 判断是否为任职记录的修改
					boolean flag = HIDelegator.getIHRhiQBS().checkPsnWorkDate(pk_corp, pk_psndoc, begindate, null);
					if (flag) {
						// throw new Exception("任职日期和其他公司的任职日期有冲突");
						throw new Exception(NCLangResOnserver.getInstance().getStrByID("600700", "UPP600700-000239")/*
						 * @res
						 * "任职日期和其他公司的任职日期有冲突"
						 */);
					}
					// ----------------------二次开发的代码zg add end
					// 如果存在任职记录，且任职记录开始日期为空或者到职日期在最新任职记录结束日期之前时，不写入任职开始日期
					if (begindate == null) {
						psnList[i].setFieldValue("indutydate", null);
					} else {
						psnList[i].setFieldValue("indutydate", begindate);
					}
					psnList[i].setAttributeValue("pk_psndoc", pk_psndoc);// 对人员pk重新赋值，如果是返聘再聘，就修改，否则不变
					dmo.insertDeptChg(psnList[i], relationdeptchg, accToSubDeptchgMap);
					//如果是本公司返聘人员或者再聘人员，更新原有的薪酬记录
					if(isreturn.equalsIgnoreCase("Y") || rehire){
						//返聘再聘人员同步薪资福利档案信息，同步的类型为在职人员，不分本公司或者是跨公司
						updateWa(pk_psndoc,dmo.getPsnchgpk(pk_psndoc),IPsnChanged.NORMARL,pk_corp,begindate);
					}

				} else {// 非在职人员
					psnList[i] = dmo.queryDetailForDimis(psnList[i], relationdimis);
					String leavedate = dmo.queryDimissionDate(pk_psndoc);
					String outdutydate = dmo.queryOutdutyDate(pk_psndoc);
					dmo.updateRecornum("hi_psndoc_dimission", new Integer(1), pk_psndoc);
					if (leavedate != null && outdutydate != null
							&& UFDate.getDate(leavedate).after(UFDate.getDate(outdutydate))) {
						outdutydate = null;
					}
					// 如果离职人员存在任职记录，则离职记录的离职前部门为任职记录的部门
					GeneralVO deptchginfo = dmo.getChginfo(pk_psndoc, pk_corp);
					dmo.insertDismissionChd(psnList[i], outdutydate, deptchginfo, relationdimis, accToSubDimisMap);
				}

				//处理考勤信息
				if(SFServiceFacility.getCreateCorpQueryService().isEnabled(pk_corp, "TA")){
					ITBMPsndocForTRN ita =  (ITBMPsndocForTRN)NewObjectService.newInstance("hrta","nc.impl.tbm.pub001.TBMPsndocForTRNImpl");
					UFDate indutyDate =  psnList[i].getAttributeValue("indutydate") == null ? PubEnv.getServerDate() 
							: (new UFDate(psnList[i].getAttributeValue("indutydate").toString()));
					ita.addTBMPsndocForInduty_yl(pk_corp, new String[]{pk_psndoc}, indutyDate,pk_deptdoc);
				}
			}
			// 同步工作履历
			if (psnList != null) {
				for (int i = 0; i < psnList.length; i++) {
					String pk_psndoc = (String) psnList[i].getAttributeValue("pk_psndoc");
					String isreturn = (String) psnList[i].getAttributeValue("isreturn");
					String pk_psnbasdoc = (String) psnList[i].getAttributeValue("pk_psnbasdoc");
					vCtrt.addElement(pk_psnbasdoc);

					if (hash.get(pk_psndoc) != null) {
						pk_psndoc = (String) hash.get(pk_psndoc);
					}
					// 查询任职记录信息
					GeneralVO[] subInfos = dmo.queryDeptChgInfos(pk_psndoc, isreturn);
					if (subInfos != null && subInfos.length > 0) {
						// 返聘时将最新任职记录同步到工作履历
						if (isreturn != null && "Y".equalsIgnoreCase(isreturn)) {
							dmo.updateRecornumforWork("hi_psndoc_work", 1, pk_psnbasdoc);
						} else {
							dmo.updateRecornum("hi_psndoc_work", new Integer(subInfos.length), pk_psndoc);
						}
						dmo.insertHiPsnWork(subInfos);
					}
				}
			}
			// 处理合同签订次数
			if (vCtrt.size() > 0 && BSUtil.isModuleInstalled(personpk_corp, "HRCM")) {
				pk_psnbasdocs = new String[vCtrt.size()];
				vCtrt.copyInto(pk_psnbasdocs);
				IHrcmPsnChanged hrcm = (IHrcmPsnChanged) NCLocator.getInstance()
				.lookup(IHrcmPsnChanged.class.getName());
				hrcm.chgContinueTimeForPsn(pk_psnbasdocs);
			}

			//处理考勤信息
//			if(pk_psndocs != null && pk_psndocs.length > 0){
//			String pk_corp = (String) psnList[0].getAttributeValue("pk_corp");
//			if(SFServiceFacility.getCreateCorpQueryService().isEnabled(pk_corp, "TA")){
//			ITBMPsndocForTRN ita =  (ITBMPsndocForTRN)NewObjectService.newInstance("hrta","nc.impl.tbm.pub001.TBMPsndocForTRNImpl");
//			ita.addTBMPsndocForInduty(pk_corp, pk_psndocs, (UFDate) psnList[0].getAttributeValue("indutydate"));
//			}
//			}

			if (psnList != null && psnList.length > 0) {
				String pk_corp = (String) psnList[0].getAttributeValue("pk_corp");
				HIDelegator.getIPersonADDSV().operatePersonInfo(pk_psndocs, IPersonADDSV.ADD, pk_corp);
			}
		} catch (Exception e) {
			throwBusinessException(e);
		}
	}
	/**
	 * 更新工资 创建日期：(2002-4-2 20:41:17)
	 */
	private void updateWa(String pk_psndoc, String pk_psndoc_sub, int type,
			String pk_corp,UFDate userDate) throws BusinessException {
		try {
			if (SFServiceFacility.getCreateCorpQueryService().isEnabled(pk_corp, "WA")) {
				IPsnChanged ls = (IPsnChanged) NCLocator.getInstance().lookup(IPsnChanged.class.getName());
				PsndocVO vo = new PsndocVO();
				vo.setPrimaryKey(pk_psndoc);
				vo.setPk_deptdoc(null);
				ls.psndocUpdated(vo, pk_psndoc_sub, type, pk_corp, userDate);
			}

		} catch (BusinessException e) {
			Logger.error(e.getMessage(), e);
			throw e;
		}
	}

	/**
	 * 判断人员类别是否为非在职
	 * 
	 * @param pk_psncl
	 * @return
	 * @throws BusinessException
	 */
	public boolean isOutdutyPsncl(String pk_psncl) throws BusinessException {
		boolean outdutypsncl = true;
		try {
			PsnInfDMO dmo = new PsnInfDMO();
			outdutypsncl = dmo.isNOtZaizhiPsnclscope(pk_psncl);
		} catch (Exception e) {
			throwBusinessException(e);
		}
		return outdutypsncl;
	}

	/**
	 * 是否存在人员编码重复的人员
	 * 
	 * @return boolean
	 * @throws BusinessException
	 */
	public boolean hasPerson() throws BusinessException {
		boolean hasperson = false;
		try {
			PsnInfDMO persondmo = new PsnInfDMO();
			hasperson = persondmo.hasPerson();
		} catch (Exception e) {
			throwBusinessException(e);
		}
		return hasperson;
	}

	/**
	 * 查询经理所在部门及子部门的树。 创建日期：(2004-5-10 11:37:03)
	 * 
	 * @return nc.vo.hi.hi_301.CtrlDeptVO
	 * @param userID
	 *            java.lang.String
	 * @param pk_corp
	 *            java.lang.String
	 * @exception BusinessException
	 * 
	 */
	public CtrlDeptVO queryMgrDepts(String pk_deptdoc, String pk_corp, boolean includehrcanceled)
	throws BusinessException {
		// 根结点
		CtrlDeptVO root = new CtrlDeptVO();
		root.setNodeType(-1);
		root.setCode("");
		root.setName(nc.bs.ml.NCLangResOnserver.getInstance().getStrByID("common", "UC000-0000404")/* @res "公司" */);
		root.setPk_corp("0000");
		root.setControlled(false);
		nc.bs.bd.CorpBO cbo = new nc.bs.bd.CorpBO();
		nc.vo.bd.CorpVO corp = cbo.findByPrimaryKey(pk_corp);
		CtrlDeptVO corpvo = new CtrlDeptVO();
		corpvo.setControlled(true);
		corpvo.setPk_corp(pk_corp);
		corpvo.setCode(corp.getUnitcode());
		corpvo.setName(corp.getUnitname());
		corpvo.setLoadDept(true);
		corpvo.setNodeType(CtrlDeptVO.CORP);
		root.addChild(corpvo);

		try {

			DeptdocDMO deptdmo = new DeptdocDMO();
			// 查找当前部门
			DeptdocVO dept = deptdmo.findByPrimaryKey(pk_deptdoc);
			CtrlDeptVO deptvo = new CtrlDeptVO();
			deptvo.setControlled(true);
			deptvo.setCode(dept.getDeptcode());
			deptvo.setName(dept.getDeptname());
			deptvo.setPk_corp(pk_corp);
			deptvo.setPk_dept(pk_deptdoc);
			deptvo.setNodeType(CtrlDeptVO.DEPT);
			deptvo.setHrcanceled(dept.getHrcanceled().booleanValue());
			corpvo.addDeptChildren(deptvo);
			// 查出所有有权限的部门主键
			DeptdocVO[] depts = deptChildren(pk_deptdoc, includehrcanceled);

			// 将有权限的部门放在哈希表中，加快判断地速度
			Hashtable powered = new Hashtable();
			for (int i = 0; i < depts.length; i++) {
				powered.put(depts[i].getPk_deptdoc(), depts[i]);
			}

			// 添加所有的部门，并创建树
			for (int i = 0; i < depts.length; i++)
				addtoDeptTree(deptvo, depts[i], powered);

		} catch (Exception e) {
			throwBusinessException(e);
		}
		return root;
	}

	/**
	 * 查询人员信息
	 * 
	 * @param pk_psndoc
	 * @param pk_psnbasdoc
	 * @param table
	 * @param isTraceTable
	 * @param islookhistory
	 *            v50 add 查看历史记录
	 * @param loginPkcorp
	 *            V55 add 登陆公司
	 * @return GeneralVO[] 人员vo数组
	 * @throws BusinessException
	 */
	public GeneralVO[] queryPersonInfo(String pk_corp, String pk_psnbasdoc, String table, boolean isTraceTable,
			boolean islookhistory, String pk_psndoc, String loginPkcorp) throws BusinessException {

		GeneralVO[] psnList = null;
		try {
			// 缺省查询所有信息
			String select = "*";
			// 组织sql
			String sql = null;
			if (isTraceTable) {// 业务子集
				if (islookhistory) {
					sql = " select " + select + " from " + table + " where pk_psnbasdoc='" + pk_psnbasdoc + "'";
				} else {
					if (table.equalsIgnoreCase("hi_psndoc_dimission")) {
						//若是离职记录表，离职后管理公司也可以查询。
						sql = " select " + select + " from " + table + " where pk_psnbasdoc='" + pk_psnbasdoc
						+ "' and (pk_corp ='" + pk_corp + "' or pk_corp ='" + loginPkcorp 
						+ "' or pk_corpafter='"+pk_corp+"' or pk_corpafter='"+loginPkcorp+"' ) ";
					}else{
						// V55 add 在登陆上级公司查看下级公司人员业务子集时，要包含业务发生单位为当前登陆公司的子集信息
						sql = " select " + select + " from " + table + " where pk_psnbasdoc='" + pk_psnbasdoc
						+ "' and (pk_corp ='" + pk_corp + "' or pk_corp ='" + loginPkcorp + "') ";
					}
				}
			} else {// 非业务子集
				sql = " select " + select + " from " + table + " where pk_psnbasdoc='" + pk_psnbasdoc + "'";
			}
			// 特殊条件处理
			if (table.equalsIgnoreCase("hi_psndoc_part")) {
				if (islookhistory) {
					sql = " select * from hi_psndoc_deptchg " + " where pk_psnbasdoc='" + pk_psnbasdoc
					+ "'  and jobtype<>0";
				} else {
					sql = "select * from hi_psndoc_deptchg where " + " pk_psndoc='" + pk_psndoc + "'  and jobtype<>0";
				}
			} else if (table.equalsIgnoreCase("hi_psndoc_deptchg")) {
				sql += " and jobtype=0";
			} else if (table.equalsIgnoreCase("hi_psndoc_training")) {
				sql += " and approveflag = 2";
			} else if (table.equalsIgnoreCase("hi_psndoc_ctrt")) {
				sql += " and isrefer = 'Y'";
			} else if ("hi_psndoc_capa".equalsIgnoreCase(table)) {
				sql = "select hi_psndoc_capa.pk_pe_scogrditem, hi_psndoc_capa.eva_date, pe_indicator.indi_code, pe_indicator.indi_name, "
					+ "pe_indicator.pk_indi_parent, pe_indicator.indi_desc, hi_psndoc_capa.lastflag, hi_psndoc_capa.pk_pe_indi, "
					+ "hi_psndoc_capa.begindate, hi_psndoc_capa.enddate, hi_psndoc_capa.score "
					+ "from hi_psndoc_capa inner join pe_indicator on hi_psndoc_capa.pk_pe_indi = pe_indicator.pk_pe_indi "
					+ "where pk_psnbasdoc = (select pk_psnbasdoc from bd_psndoc where pk_psndoc = '"
					+ pk_psndoc
					+ "') and hi_psndoc_capa.lastflag = 'Y'";
			}
			// 是子集添加排序条件
			if (!table.equals("bd_psnbasdoc") && !table.equals("bd_psndoc")
					&& !table.equalsIgnoreCase("hi_psndoc_mainadd") && !table.equals("hi_psndoc_ref")) {
				if (isTraceTable) {// 业务子集
					if (table.equalsIgnoreCase("hi_psndoc_part") || table.equalsIgnoreCase("hi_psndoc_deptchg")
							|| table.equalsIgnoreCase("hi_psndoc_retire") || table.equalsIgnoreCase("hi_psndoc_psnchg")
							|| table.equalsIgnoreCase("hi_psndoc_training") || table.equalsIgnoreCase("hi_psndoc_ass")) {
						sql += " order by begindate ";
					} else if (table.equalsIgnoreCase("hi_psndoc_dimission")) {
						sql += " order by leavedate ";
					} else if (table.equalsIgnoreCase("hi_psndoc_ctrt")) {
						// V55 add recordnum
						sql += " order by dsigndate,recordnum desc ";
					} else if (table.equalsIgnoreCase("hi_psndoc_orgpsn")) {
						sql += " order by djoindate ";
					} else if (table.equalsIgnoreCase("hi_psndoc_capa")) {
						sql += " order by pe_indicator.indi_code desc ";
					} else {
						sql += " order by recordnum desc ";
					}
				} else {
					sql += " order by recordnum desc ";
				}
			}
			PsnInfDMO dmo = new PsnInfDMO();
			psnList = dmo.queryBySql(sql);
			// 培训子集需要更新名称
			if (table.equalsIgnoreCase("hi_psndoc_training") && psnList != null && psnList.length > 0) {
				for (int i = 0; i < psnList.length; i++) {
					psnList[i] = updateTraingInfo(psnList[i]);
				}
			}
		} catch (Exception e) {
			throwBusinessException(e);
		}
		return psnList;
	}

	/**
	 * 
	 * @param trainginfo
	 * @return
	 * @throws BusinessException
	 */
	private GeneralVO updateTraingInfo(GeneralVO trainginfo) throws BusinessException {

		try {
			PsnInfDMO dmo = new PsnInfDMO();
			String tratypepks = (String) trainginfo.getAttributeValue("tra_type");
			String tramodepks = (String) trainginfo.getAttributeValue("tra_mode");

			String[] tratypepk = null;
			if (tratypepks != null && !tratypepks.equalsIgnoreCase("")) {
				tratypepk = tratypepks.split(",");
				String tratypepkssql = parsePkcorps(tratypepk);
				String[] tratypenames = dmo.getNames(tratypepkssql, 0);
				if (tratypenames != null && tratypenames.length > 0) {
					String types = connectNames(tratypenames);
					trainginfo.setAttributeValue("trm_class_names", types);
					String pk_sub = (String) trainginfo.getAttributeValue("pk_psndoc_sub");
					dmo.updateTraining(pk_sub, types, 0);
				}
			}
			String[] tramodepk = null;
			if (tramodepks != null && !tramodepks.equalsIgnoreCase("")) {
				tramodepk = tramodepks.split(",");
				String tramodepkssql = parsePkcorps(tramodepk);
				String[] tramodenames = dmo.getNames(tramodepkssql, 1);
				if (tramodenames != null && tramodenames.length > 0) {
					String modes = connectNames(tramodenames);
					trainginfo.setAttributeValue("tra_mode_name", modes);
					String pk_sub = (String) trainginfo.getAttributeValue("pk_psndoc_sub");
					dmo.updateTraining(pk_sub, modes, 1);
				}
			}
		} catch (Exception e) {
			throwBusinessException(e);
		}
		return trainginfo;
	}

	private String connectNames(String[] traTypeNames) {

		String traNamelist = null;
		if (traTypeNames != null) {
			traNamelist = "";
			for (int i = 0; i < traTypeNames.length; i++) {
				traNamelist += traTypeNames[i];
				if (i < traTypeNames.length - 1) {
					traNamelist += ",";
				}
			}
		}
		return traNamelist;
	}

	/**
	 * 查询兼职人员业务子集信息
	 * 
	 * @param pk_psndoc
	 * @param pk_corp
	 * @param table
	 * @return
	 * @throws BusinessException
	 */
	public GeneralVO[] queryPartPersonChildInfo(String pk_psndoc, String pk_corp, String table)
	throws BusinessException {

		GeneralVO[] psnList = null;
		try {
			// 缺省查询所有信息
			String select = "*";
			// 组织sql
			String sql = null;
			sql = " select " + select + " from " + table + " where pk_psndoc='" + pk_psndoc + "'";
			// 兼职人员发生的业务记录,需要用当前公司过滤
			if (table.equalsIgnoreCase("hi_psndoc_part")) {
				sql = "select * from hi_psndoc_deptchg where pk_psndoc='" + pk_psndoc
				+ "' and jobtype<>0 and pk_corp ='" + pk_corp + "'";
			} else if (table.equalsIgnoreCase("hi_psndoc_ass")) {
				sql += " and pk_corpassess ='" + pk_corp + "'";
			} else if (table.equalsIgnoreCase("hi_psndoc_training")) {
				sql += " and approveflag = 2 and pk_corp ='" + pk_corp + "'";
			} else if (table.equalsIgnoreCase("hi_psndoc_ctrt")) {
				sql += " and isrefer = 'Y' and pk_corp ='" + pk_corp + "'";
			} else if (table.equalsIgnoreCase("hi_psndoc_orgpsn")) {
				sql += " and pk_corp ='" + pk_corp + "'";
			}  else if ("hi_psndoc_capa".equalsIgnoreCase(table)) {
				sql = "select hi_psndoc_capa.pk_pe_scogrditem, hi_psndoc_capa.eva_date, pe_indicator.indi_code, pe_indicator.indi_name, "
					+ "pe_indicator.pk_indi_parent, pe_indicator.indi_desc, hi_psndoc_capa.lastflag, hi_psndoc_capa.pk_pe_indi, "
					+ "hi_psndoc_capa.begindate, hi_psndoc_capa.enddate, hi_psndoc_capa.score "
					+ "from hi_psndoc_capa inner join pe_indicator on hi_psndoc_capa.pk_pe_indi = pe_indicator.pk_pe_indi "
					+ "where pk_psnbasdoc = (select pk_psnbasdoc from bd_psndoc where pk_psndoc = '"
					+ pk_psndoc
					+ "') and hi_psndoc_capa.lastflag = 'Y'";
			}
			// 是子集添加排序条件
			if (table.equalsIgnoreCase("hi_psndoc_part") || table.equalsIgnoreCase("hi_psndoc_training")
					|| table.equalsIgnoreCase("hi_psndoc_ass")) {
				sql += " order by begindate ";
			} else if (table.equalsIgnoreCase("hi_psndoc_ctrt")) {
				sql += " order by dsigndate ";
			} else if (table.equalsIgnoreCase("hi_psndoc_orgpsn")) {
				sql += " order by djoindate ";
			} else if (table.equalsIgnoreCase("hi_psndoc_capa")) {
				sql += " order by pe_indicator.indi_code desc ";
			} else {
				sql += " order by recordnum desc ";
			}

			PsnInfDMO dmo = new PsnInfDMO();

			psnList = dmo.queryBySql(sql);
			// if (table.equalsIgnoreCase("hi_psndoc_mainadd")
			// && (psnList == null || psnList.length == 0)) {
			// psnList = new GeneralVO[1];
			// GeneralVO vo = new GeneralVO();
			// vo.setAttributeValue("pk_psndoc", pk_psndoc);
			// psnList[0] = vo;
			// }

		} catch (Exception e) {
			throwBusinessException(e);
		}
		return psnList;
	}

	/**
	 * 查询人员pk_psndoc信息表table的所有记录。 创建日期：(2004-5-13 17:00:55)
	 * 
	 * @return nc.vo.hi.hi_301.GeneralVO[]
	 * @param pk_psndoc
	 *            java.lang.String
	 * @param table
	 *            java.lang.String
	 * @exception BusinessException
	 *                异常说明。
	 */
	public GeneralVO[] queryPersonInfo(String pk, String table) throws BusinessException {
		return queryMainPersonInfo(pk, null, table, null);
	}

	/**
	 * 根据人员基本档案主键和公司主键查询人员bd_psndoc信息表的记录
	 * 
	 * @param pk
	 * @param table
	 * @param pk_corp
	 * @return
	 * @throws BusinessException
	 */
	public GeneralVO[] queryPersonInfo(String pk, String table, String pk_corp) throws BusinessException {
		GeneralVO[] psnList = null;
		try {
			// 缺省查询所有信息
			String select = "*";
			// 组织sql
			String sql = null;
			if ("bd_psndoc".equalsIgnoreCase(table)) {
				sql = " select " + select + " from " + table + " where pk_psnbasdoc='" + pk + "' and pk_corp ='"
				+ pk_corp + "'and indocflag ='Y' ";// V35 pk_psndoc
			}
			PsnInfDMO dmo = new PsnInfDMO();
			psnList = dmo.queryBySql(sql);

		} catch (Exception e) {
			throwBusinessException(e);
		}
		return psnList;
	}

	/**
	 * 更新辅助人员信息。 创建日期：(2004-5-19 14:53:19)
	 * 
	 * @param accpsndocVO
	 *            nc.vo.hi.hi_301.GeneralVO
	 */
	public void updateAccpsndoc(GeneralVO accpsndocVO, String pk_psndoc) throws BusinessException {
		try {
			PsnInfDMO dmo = new PsnInfDMO();
			accpsndocVO.removeAttributeName("belong_pk_corp");
			String pk_psnbasdoc = (String) accpsndocVO.getFieldValue("pk_psnbasdoc");
			dmo.updateTable("bd_psnbasdoc", accpsndocVO, "pk_psnbasdoc='" + pk_psnbasdoc + "'");
			nc.bs.bd.cache.CacheProxy.fireDataUpdated("bd_psnbasdoc", pk_psnbasdoc);

			GeneralVO tempvo = new GeneralVO();
			tempvo.setAttributeValue("psnname", (String) accpsndocVO.getFieldValue("psnname"));
			dmo.updateTable("bd_psndoc", tempvo, "pk_psnbasdoc='" + pk_psnbasdoc + "'");
			nc.bs.bd.cache.CacheProxy.fireDataUpdated("bd_psndoc", pk_psndoc);

//			if (accpsndocVO != null) {
//			String pk_corp = (String) accpsndocVO.getAttributeValue("pk_corp");
//			HIDelegator.getIPersonADDSV().operatePersonInfo(new String[] { pk_psndoc }, IPersonADDSV.MODIFY,
//			pk_corp);
//			}
		} catch (Exception e) {
			throwBusinessException(e);
		}
	}

	/**
	 * 批量更新人员信息。
	 * 
	 */
	public void batchUpdateAccpsndoc(GeneralVO[] accpsndocVOs, String[] pk_psndocs) throws BusinessException {
		if(pk_psndocs==null||pk_psndocs.length<1) return;
		try {
			PsnInfDMO dmo = new PsnInfDMO();
			for(int i=0;i<pk_psndocs.length;i++){
				GeneralVO accpsndocVO = accpsndocVOs[i];
				String pk_psndoc = pk_psndocs[i];
				accpsndocVO.removeAttributeName("belong_pk_corp");
				String pk_psnbasdoc = (String) accpsndocVO.getFieldValue("pk_psnbasdoc");
				dmo.updateTable("bd_psnbasdoc", accpsndocVO, "pk_psnbasdoc='" + pk_psnbasdoc + "'");
				nc.bs.bd.cache.CacheProxy.fireDataUpdated("bd_psnbasdoc", pk_psnbasdoc);

				GeneralVO tempvo = new GeneralVO();
				tempvo.setAttributeValue("psnname", (String) accpsndocVO.getFieldValue("psnname"));
				dmo.updateTable("bd_psndoc", tempvo, "pk_psnbasdoc='" + pk_psnbasdoc + "'");
				nc.bs.bd.cache.CacheProxy.fireDataUpdated("bd_psndoc", pk_psndoc);
			}
//			String pk_corp = (String) accpsndocVOs[0].getAttributeValue("pk_corp");
//			HIDelegator.getIPersonADDSV().operatePersonInfo(pk_psndocs, IPersonADDSV.MODIFY,
//			pk_corp);
		} catch (Exception e) {
			throwBusinessException(e);
		}
	}
	/**
	 * V35 add 扩展了 updateAccpsndoc(GeneralVO accpsndocVO)方法,专门用来处理信息子集与关联表（bd_psndoc bd_psnbasdoc）的信息
	 * 
	 * @param hmRelVo
	 *            -key:存放的是表名,value:存放的是GeneralVO对象：表对应的数据字段值
	 * @throws BusinessException
	 */
	public void updateRelTable(HashMap hmRelVo) throws BusinessException {
		if (hmRelVo == null) {
			return;
		}
		try {
			PsnInfDMO dmo = new PsnInfDMO();
			Set keys = hmRelVo.keySet();
			for (Iterator iter = keys.iterator(); iter.hasNext();) {
				String keyTable = (String) iter.next();
				GeneralVO vo = (GeneralVO) hmRelVo.get(keyTable);
				String pk = null;
				if ("bd_psnbasdoc".equalsIgnoreCase(keyTable)) {
					pk = "pk_psnbasdoc";
				} else {
					pk = "pk_psndoc";
				}
				String pkValue = (String) vo.getFieldValue(pk);
				// 
				dmo.updateTable(keyTable, vo, pk + "='" + pkValue + "'");
				// 更新考勤信息
				if (SFServiceFacility.getCreateCorpQueryService().isEnabled((String) vo.getAttributeValue("pk_corp"),
				"TA")) {
					if (vo.getAttributeValue("sealdate") != null) {
						// 判断如果是封存人员(取消引用)，调用时间管理接口结束考勤档案和考勤日历
						ITBMPsndocForTRN ls = (ITBMPsndocForTRN) NewObjectService.newInstance("hrta",
						"nc.impl.tbm.pub001.TBMPsndocForTRNImpl");
						ls.updateTBMPsndocForCrossCorp((String) vo.getAttributeValue("pk_corp"), null,
								new String[] { pkValue }, (UFDate) vo.getAttributeValue("sealdate"), false);
					}
				}
			}
		} catch (Exception e) {
			throwBusinessException(e);
		}
	}

	/**
	 * 批量取消人员引用
	 * 
	 * @param vos
	 * @throws BusinessException
	 */
	public void batchCancelRefPsn(GeneralVO[] vos) throws BusinessException {
		if (vos == null) {
			return;
		}
		try {
			for (GeneralVO vo : vos) {
				PsnInfDMO dmo = new PsnInfDMO();
				String pkValue = (String) vo.getFieldValue("pk_psndoc");
				dmo.updateTable("bd_psndoc", vo, "pk_psndoc='" + pkValue + "'");
				// 更新考勤信息
				if (SFServiceFacility.getCreateCorpQueryService().isEnabled((String) vo.getAttributeValue("pk_corp"),
				"TA")) {
					if (vo.getAttributeValue("sealdate") != null) {
						// 判断如果是封存人员(取消引用)，调用时间管理接口结束考勤档案和考勤日历
						ITBMPsndocForTRN ls = (ITBMPsndocForTRN) NewObjectService.newInstance("hrta",
						"nc.impl.tbm.pub001.TBMPsndocForTRNImpl");
						ls.updateTBMPsndocForCrossCorp((String) vo.getAttributeValue("pk_corp"), null,
								new String[] { pkValue }, (UFDate) vo.getAttributeValue("sealdate"), false);
					}
				}
			}
			if (vos != null && vos.length > 0) {
				String pk_corp = (String) vos[0].getAttributeValue("pk_corp");
				HIDelegator.getIPersonADDSV().operatePersonInfo(getPKsFromVOS(vos), IPersonADDSV.MODIFY, pk_corp);
			}
			// add by sunxj 2010-04-27 H型接口插件 start
			// 增加人员增量数据 取消引用人员
			UFBoolean waHrFiFlagTemp = PubDelegator.getIParValue().getParaBoolean(null, "WA-HRFI");
			boolean waHrFiFlag = waHrFiFlagTemp == null ? false : waHrFiFlagTemp.booleanValue();
			if (waHrFiFlag) {
				insertPsndocAdd(vos);
			}
			// add by sunxj 2010-04-27 H型接口插件 end
		} catch (Exception e) {
			throwBusinessException(e);
		}

	}

	/**
	 * 更新基本信息集合信息。 创建日期：(2004-5-19 14:52:39)
	 * 
	 * @param psndocVO
	 *            nc.vo.hi.hi_301.GeneralVO
	 * @exception BusinessException
	 *                异常说明。
	 */
	private void updatePsndoc(GeneralVO psndocVO) throws BusinessException {
		try {
			PsnInfDMO dmo = new PsnInfDMO();
			String pk_psndoc = (String) psndocVO.getFieldValue("pk_psndoc");
			dmo.updateTable("bd_psndoc", psndocVO, "pk_psndoc='" + pk_psndoc + "'");
			nc.bs.bd.cache.CacheProxy.fireDataUpdated("bd_psndoc", pk_psndoc);
		} catch (Exception e) {
			throwBusinessException(e);
		}
	}

	/**
	 * 更新主表记录的删除标志
	 * 
	 * @param pk_psndoc
	 * @param ifdelete
	 * @throws nc.vo.pub.BusinessException
	 */
	public void updateDeleteFlag(String pk_psndoc, boolean ifdelete) throws BusinessException {
		try {
			PsnInfDMO dmo = new PsnInfDMO();
			dmo.updateDeleteFlag(pk_psndoc, ifdelete);

		} catch (Exception e) {
			throwBusinessException(e);
		}
	}

	/**
	 * 往部门树种添加部门,并返回自身节点 创建日期：(2004-5-9 19:58:27)
	 * 
	 * @param root
	 *            nc.vo.hi.hi_301.CtrlDeptVO
	 * @param pk_dept
	 *            java.lang.String
	 */
	private CtrlDeptVO addtoDeptTree(CtrlDeptVO root, DeptdocVO dept, Hashtable powered) throws Exception {
		PsnInfDMO dmo = new PsnInfDMO();
		// 查找当前部门

		if (dept == null || dept.getPk_deptdoc() == null) // 没有责返回
			return null;

		// 有可能改部门已经被先前添加其他节点作为父接点已经插入，所以先查找
		CtrlDeptVO thisCtrlDept = root.findDeptChild(dept.getPk_deptdoc());

		if (thisCtrlDept != null)
			// 找到了说明已经插入了,返回自身
			return thisCtrlDept;

		// 否则生成当前部门的CtrlDeptVO ，准备插入
		thisCtrlDept = new CtrlDeptVO();
		thisCtrlDept.setNodeType(CtrlDeptVO.DEPT);
		thisCtrlDept.setCode(dept.getDeptcode());
		thisCtrlDept.setName(dept.getDeptname());
		thisCtrlDept.setPk_dept(dept.getPk_deptdoc());
		thisCtrlDept.setPk_corp(root.getPk_corp());
		thisCtrlDept.setControlled(powered.get(dept.getPk_deptdoc()) != null);
		thisCtrlDept.setHrcanceled(dept.getHrcanceled().booleanValue());

		// 在树中查找它的父节点
		CtrlDeptVO target = root.findDeptChild(dept.getPk_fathedept());
		if (target != null) {
			// 找到了
			target.addChild(thisCtrlDept);
		} else {
			DeptdocVO fathervo = null;
			if (dept.getPk_fathedept() != null && dept.getPk_fathedept().length() == 20) {
				if (powered.get(dept.getPk_fathedept()) != null) {
					String where = "pk_deptdoc = '" + dept.getPk_fathedept() + "'";
					DeptdocVO[] vos = dmo.queryDeptVOs(where);
					if (vos.length > 0) {
						fathervo = vos[0];
					}
				} else {
					fathervo = (DeptdocVO) powered.get(dept.getPk_fathedept());
				}
			}
			// 说明父节点还没有被插入,添加父亲节点
			CtrlDeptVO fatherCtrlDept = addtoDeptTree(root, fathervo, powered);
			// 将当前节点添加到自己的父亲节点下
			fatherCtrlDept.addChild(thisCtrlDept);
		}

		// 返回自身
		return thisCtrlDept;
	}

	/**
	 * wangkf add
	 * 
	 * @param root
	 * @param dept
	 * @param powered
	 * @param allRelated
	 *            --存储所有构造部门树所必须的树节点
	 * @return
	 * @throws Exception
	 */
	private CtrlDeptVO addtoDeptTree(CtrlDeptVO root, DeptdocVO dept, Hashtable powered, HashMap allRelated)
	throws Exception {
		if (dept == null || dept.getPk_deptdoc() == null) // 没有则返回
			return null;

		// 有可能改部门已经被先前添加其他节点作为父接点已经插入，所以先查找
		CtrlDeptVO thisCtrlDept = root.findDeptChild(dept.getPk_deptdoc());

		if (thisCtrlDept != null) {
			// 找到了说明已经插入了,返回自身
			return thisCtrlDept;
		}

		// 否则生成当前部门的CtrlDeptVO ，准备插入
		thisCtrlDept = new CtrlDeptVO();
		thisCtrlDept.setNodeType(CtrlDeptVO.DEPT);
		thisCtrlDept.setCode(dept.getDeptcode());
		thisCtrlDept.setName(dept.getDeptname());
		thisCtrlDept.setPk_dept(dept.getPk_deptdoc());
		thisCtrlDept.setPk_corp(root.getPk_corp());
		thisCtrlDept.setCanceled(dept.getCanceled().booleanValue());
		thisCtrlDept.setHrcanceled(dept.getHrcanceled().booleanValue());
		// 内部编码
		thisCtrlDept.setInnercode(dept.getInnercode());
		thisCtrlDept.setControlled(powered.get(dept.getPk_deptdoc()) != null);

		// 在树中查找它的父节点
		CtrlDeptVO target = root.findDeptChild(dept.getPk_fathedept());
		if (target != null) {
			// 找到了
			target.addChild(thisCtrlDept);
		} else {
			DeptdocVO fathervo = null;
			if (dept.getPk_fathedept() != null && dept.getPk_fathedept().length() == 20) {
				fathervo = (DeptdocVO) allRelated.get(dept.getPk_fathedept());
			}
			// 说明父节点还没有被插入,添加父亲节点
			CtrlDeptVO fatherCtrlDept = addtoDeptTree(root, fathervo, powered, allRelated);
			// 将当前节点添加到自己的父亲节点下
			fatherCtrlDept.addChild(thisCtrlDept);
		}

		// 返回自身
		return thisCtrlDept;
	}

	/**
	 * 此处插入方法描述。 创建日期：(2004-10-19 10:08:32)
	 * 
	 * @param parent
	 *            nc.vo.hi.hi_301.CtrlDeptVO
	 * @param pk_corp
	 *            java.lang.String
	 * @exception java.lang.Exception
	 *                异常说明。
	 */
	private void addChildCorpToDeptTree(CtrlDeptVO root, CtrlDeptVO parent, String userid, boolean isRelate)
	throws java.lang.Exception {
		PsnInfDMO dmo = new PsnInfDMO();
		CtrlDeptVO[] childvos = dmo.queryCreateCorpChildVOs(parent.getPk_corp(), "HI", userid, isRelate);
		if (childvos != null && childvos.length > 0) {
			for (int i = 0; i < childvos.length; i++) {
				root.addChild(childvos[i]);
				addChildCorpToDeptTree(root, childvos[i], userid, isRelate);

			}
		}
	}

	/**
	 * 批量修改
	 */
	public void batchUpdate(String[] pk_psndocs, String tableCode, String fieldCode, Object value, String modulcode,
			String pk_corp) throws BusinessException {
		try {
			// add by sunxj 2010-05-18 H型人力财务接口 start
			UFBoolean waHrFiFlagTemp = PubDelegator.getIParValue().getParaBoolean(null, "WA-HRFI");
			boolean waHrFiFlag = waHrFiFlagTemp == null ? false : waHrFiFlagTemp.booleanValue();
			// add by sunxj 2010-05-18 H型人力财务接口 end
			PsnInfDMO dmo = new PsnInfDMO();
			ArrayList<GeneralVO> psnList = new ArrayList<GeneralVO>();
			if (modulcode.equalsIgnoreCase("600707")) {
				boolean caneditkeypsn = PubDelegator.getIParValue().getParaBoolean(pk_corp, "HI_KEYPERSON")
				.booleanValue();
				if (!caneditkeypsn) {// 如果关键人员不能在维护节点编辑
					Vector v = new Vector();
					String[] newpks = null;
					// loop,loop,loop,why not use one sql?
					for (int i = 0; i < pk_psndocs.length; i++) {
						// 过滤关键人员
						if (tableCode.equalsIgnoreCase("hi_psndoc_deptchg")
								|| tableCode.equalsIgnoreCase("hi_psndoc_ctrt")
								|| tableCode.equalsIgnoreCase("hi_psndoc_part")
								|| tableCode.equalsIgnoreCase("hi_psndoc_training")
								|| tableCode.equalsIgnoreCase("hi_psndoc_ass")
								|| tableCode.equalsIgnoreCase("hi_psndoc_retire")
								|| tableCode.equalsIgnoreCase("hi_psndoc_orgpsn")
								|| tableCode.equalsIgnoreCase("hi_psndoc_psnchg")
								|| tableCode.equalsIgnoreCase("hi_psndoc_dimission")
								|| tableCode.equalsIgnoreCase("bd_psndoc")) {
							String sql = " select 1 from hi_psndoc_keypsn where pk_psndoc ='" + pk_psndocs[i]
							                                                                               + "'and enddate is null";
							boolean iskeypsn = isRecordExist(sql);
							if (!iskeypsn) {
								v.addElement(pk_psndocs[i]);
								// add by sunxj 2010-04-28 H型财务接口 start
								// 批量修改的是工作信息部分 将人员加入增量数据
								if (waHrFiFlag && tableCode.equalsIgnoreCase("bd_psndoc")) {
									GeneralVO generalVO = new GeneralVO();
									// 增加人员基本信息主键
									generalVO.setAttributeValue("pk_psnbasdoc", dmo.queryPkPsnBasDoc(pk_psndocs[i]));
									generalVO.setAttributeValue("pk_psndoc", pk_psndocs[i]);
									psnList.add(generalVO);
								}
								// add by sunxj 2010-04-28 H型财务接口 start
							}
						} else {
							String sql = " select 1 from hi_psndoc_keypsn where pk_psnbasdoc ='" + pk_psndocs[i]
							                                                                                  + "'and enddate is null";
							boolean iskeypsn = isRecordExist(sql);
							if (!iskeypsn) {
								v.addElement(pk_psndocs[i]);
								// add by sunxj 2010-04-28 H型财务接口 start
								// 批量修改的是基本信息部分 将人员加入增量数据
								if (waHrFiFlag && tableCode.equalsIgnoreCase("bd_psnbasdoc")) {
									GeneralVO generalVO = new GeneralVO();
									generalVO.setAttributeValue("pk_psnbasdoc", pk_psndocs[i]);
									// 增加人员工作信息主键
									generalVO.setAttributeValue("pk_psndoc", dmo.queryPkPsnDoc(pk_psndocs[i], pk_corp));
									psnList.add(generalVO);
								}
								// add by sunxj 2010-04-28 H型财务接口 start
							}
						}
					}
					if (v.size() > 0) {
						newpks = new String[v.size()];
						v.copyInto(newpks);
						dmo.batchUpdate(newpks, tableCode, fieldCode, value);
					}

				} else {
					dmo.batchUpdate(pk_psndocs, tableCode, fieldCode, value);
					// add by sunxj 2010-04-28 H型财务接口 start
					// 维护节点可以修改关键人员信息
					if (waHrFiFlag
							&& (tableCode.equalsIgnoreCase("bd_psndoc") || tableCode.equalsIgnoreCase("bd_psnbasdoc"))) {
						for (int i = 0; i < pk_psndocs.length; i++) {
							// 批量修改的是工作信息部分 将人员加入增量数据
							if (tableCode.equalsIgnoreCase("bd_psndoc")) {
								GeneralVO generalVO = new GeneralVO();
								// 增加人员基本信息主键
								generalVO.setAttributeValue("pk_psnbasdoc", dmo.queryPkPsnBasDoc(pk_psndocs[i]));
								generalVO.setAttributeValue("pk_psndoc", pk_psndocs[i]);
								psnList.add(generalVO);
								// 批量修改的是基本信息部分 将人员加入增量数据
							} else if (tableCode.equalsIgnoreCase("bd_psnbasdoc")) {
								GeneralVO generalVO = new GeneralVO();
								generalVO.setAttributeValue("pk_psnbasdoc", pk_psndocs[i]);
								// 增加人员工作信息主键
								generalVO.setAttributeValue("pk_psndoc", dmo.queryPkPsnDoc(pk_psndocs[i], pk_corp));
								psnList.add(generalVO);
							}
						}
					}
					// add by sunxj 2010-04-28 H型财务接口 end
				}
			} else {
				dmo.batchUpdate(pk_psndocs, tableCode, fieldCode, value);
				// add by sunxj 2010-04-28 H型财务接口 start
				// 关键人员
				if (waHrFiFlag && modulcode.equalsIgnoreCase("600710")
						&& (tableCode.equalsIgnoreCase("bd_psndoc") || tableCode.equalsIgnoreCase("bd_psnbasdoc"))) {
					for (int i = 0; i < pk_psndocs.length; i++) {
						// 批量修改的是工作信息部分 将人员加入增量数据
						if (tableCode.equalsIgnoreCase("bd_psndoc")) {
							GeneralVO generalVO = new GeneralVO();
							// 增加人员基本信息主键
							generalVO.setAttributeValue("pk_psnbasdoc", dmo.queryPkPsnBasDoc(pk_psndocs[i]));
							generalVO.setAttributeValue("pk_psndoc", pk_psndocs[i]);
							psnList.add(generalVO);
							// 批量修改的是基本信息部分 将人员加入增量数据
						} else if (tableCode.equalsIgnoreCase("bd_psnbasdoc")) {
							GeneralVO generalVO = new GeneralVO();
							generalVO.setAttributeValue("pk_psnbasdoc", pk_psndocs[i]);
							// 增加人员工作信息主键
							generalVO.setAttributeValue("pk_psndoc", dmo.queryPkPsnDoc(pk_psndocs[i], pk_corp));
							psnList.add(generalVO);
						}
					}
				}
				// add by sunxj 2010-04-28 H型财务接口 end
			}
			// add by sunxj 2010-04-28 H型财务接口 start
			if (waHrFiFlag && (modulcode.equalsIgnoreCase("600710") || modulcode.equalsIgnoreCase("600707"))
					&& (tableCode.equalsIgnoreCase("bd_psndoc") || tableCode.equalsIgnoreCase("bd_psnbasdoc"))) {
				// 保存增量信息
				insertPsndocAdd(psnList.toArray(new GeneralVO[0]));
			}
			// add by sunxj 2010-04-28 H型财务接口 end
			if ("bd_psndoc".equalsIgnoreCase(tableCode)) {
				nc.bs.bd.cache.CacheProxy.fireDataUpdated("bd_psndoc", null);
			}
			HIDelegator.getIPersonADDSV().operatePersonInfo(pk_psndocs, IPersonADDSV.MODIFY, pk_corp);
		} catch (Exception e) {
			throwBusinessException(e);
		}

	}

	/**
	 * 批量修改人员编码
	 */
	public void batchUpdatePsnCode(GeneralVO[] psnvos) throws BusinessException {

		try {
			PsnInfDMO dmo = new PsnInfDMO();
			dmo.batchUpatePsnCode(psnvos);
			nc.bs.bd.cache.CacheProxy.fireDataUpdated("bd_psndoc", null);
			if (psnvos != null && psnvos.length > 0) {
				String pk_corp = (String) psnvos[0].getAttributeValue("pk_corp");
				HIDelegator.getIPersonADDSV().operatePersonInfo(getPKsFromVOS(psnvos), IPersonADDSV.MODIFY, pk_corp);
			}
		} catch (Exception e) {
			throwBusinessException(e);
		}

	}

	/**
	 * 处理主键
	 * 
	 * @param pk_psndocs
	 * @return
	 */
	private String parsePkcorps(String[] pk_corps) {
		String newpk = "";
		if (pk_corps != null && pk_corps.length > 0) {
			for (int i = 0; i < pk_corps.length; i++) {
				newpk += pk_corps[i] + "','";
			}
			newpk = "('" + newpk.substring(0, newpk.length() - 2) + ")";
		}
		return newpk;
	}

	/**
	 * 处理主键，分组，每组３００
	 * 
	 * @param pk_psndocs
	 * @return
	 */
	private String[] parsePks(String[] pk_psndocs) {
		String[] newpks = null;
		if (pk_psndocs != null && pk_psndocs.length > 0) {
			if (pk_psndocs.length <= 300) {
				newpks = new String[1];
				newpks[0] = "";
				for (int i = 0; i < pk_psndocs.length; i++) {
					newpks[0] += pk_psndocs[i] + "','";
				}
				newpks[0] = "('" + newpks[0].substring(0, newpks[0].length() - 2) + ")";
			} else {
				int oldlength = pk_psndocs.length;
				int group = oldlength / 300 + 1;
				newpks = new String[group];
				for (int i = 0; i < group; i++) {
					if (i == group - 1) {
						int left = oldlength - (group - 1) * 300;
						newpks[i] = "";
						for (int j = 0; j < left; j++) {
							newpks[i] += pk_psndocs[i * 300 + j] + "','";
						}
						newpks[i] = "('" + newpks[i].substring(0, newpks[i].length() - 2) + ")";
					} else {
						newpks[i] = "";
						for (int j = 0; j < 300; j++) {
							newpks[i] += pk_psndocs[i * 300 + j] + "','";
						}
						newpks[i] = "('" + newpks[i].substring(0, newpks[i].length() - 2) + ")";
					}
				}
			}
		}

		return newpks;
	}

	/**
	 * 删除一条子表记录。 创建日期：(2004-8-6 9:28:43)
	 * 
	 * @return java.lang.String
	 * @param tableCode
	 *            java.lang.String
	 * @param pk_psndoc
	 *            java.lang.String
	 * @param vo
	 *            nc.vo.hi.hi_301.GeneralVO
	 * @exception BusinessException
	 *                异常说明。
	 */
	public void deleteChild(String tableCode, String pk_psndoc, GeneralVO vo) throws BusinessException {
		try {
			PsnInfDMO dmo = new PsnInfDMO();
			String pk_psndoc_sub = (String) vo.getAttributeValue("pk_psndoc_sub");
			// wangkf add---- 如果是兼职记录 则实际从hi_psndoc_deptchg中删除一条记录
			String tempTableCode = tableCode;
			if (tableCode.equalsIgnoreCase("hi_psndoc_part")) {
				tempTableCode = "hi_psndoc_deptchg";
			}
			// -------
			if (!dmo.checkPsnSub(pk_psndoc_sub, tempTableCode)) {
				throw new BusinessException(nc.bs.ml.NCLangResOnserver.getInstance().getStrByID("600704",
				"UPP600704-000003")/*
				 * @res "该记录已经被删除，请刷新后再试！"
				 */);
			}
			String where = "pk_psndoc_sub = '" + pk_psndoc_sub + "'";
			dmo.deleteData(tableCode, where);// tempTableCode
			dmo.updateRecornum(tableCode, pk_psndoc, (Integer) vo.getAttributeValue("recordnum"), new Boolean(false));
			if (tableCode.equalsIgnoreCase("hi_psndoc_ctrt")) {
				Integer rec = (Integer) vo.getAttributeValue("recordnum");
				if (rec.intValue() == 0) {
					dmo.updateCtrt(pk_psndoc, new Boolean(false));
				}
			}
			if (vo != null) {
				String pk_corp = (String) vo.getAttributeValue("pk_corp");
				HIDelegator.getIPersonADDSV().operatePersonInfo(new String[] { pk_psndoc }, IPersonADDSV.MODIFY,
						pk_corp);
			}
		} catch (Exception e) {
			throwBusinessException(e);
		}

	}

	/**
	 * 删除维护节点人员。 创建日期：(2004-12-3 16:23:41)
	 * 
	 * @param psnvo
	 *            GeneralVO
	 * @param pk_corp
	 *            java.lang.String
	 * @exception BusinessException
	 *                异常说明。
	 */
	public void deletePersonDoc(GeneralVO psnvo, String pk_corp) throws BusinessException {
		try {
			String pk_psnbasdoc = (String) psnvo.getFieldValue("pk_psnbasdoc");
			PsnInfDMO dmo = new PsnInfDMO();
			String[] pk_psndocs = dmo.queryRefPsndoc(pk_psnbasdoc);
			for (int i = 0; i < pk_psndocs.length; i++) {
				if (pk_psndocs[i] == null || pk_psndocs[i].length() != 20) {
					throwBusinessException(new Exception(nc.bs.ml.NCLangResOnserver.getInstance().getStrByID("600704",
					"UPP600704-000004")/*
					 * @res "数据错误，请刷新后重试！"
					 */));
				}
				IReferenceCheck refCheck = (IReferenceCheck) NCLocator.getInstance().lookup(
						IReferenceCheck.class.getName());
				if (refCheck.isReferenced("bd_psndoc", pk_psndocs[i])) {
					throwBusinessException(new Exception(nc.bs.ml.NCLangResOnserver.getInstance().getStrByID("600704",
					"UPP600704-000000")/*
					 * @res "已引用，不能删除该记录，其他数据引用了该记录！"
					 */));
				}
				String[] tableCodes = dmo.queryAllSubTable(pk_corp);// 查询出所有子表
				if (tableCodes != null) {
					for (int j = 0; j < tableCodes.length; j++) {
						dmo.deletePsndoc(tableCodes[j], psnvo);
					}
				}
				// dmo.deletePsndoc("hi_psndoc_flag", psnvo);// 删除人员标志表纪录
				dmo.deletePsndoc("bd_psndoc", psnvo);// 删除主表记录
			}

			dmo.deleteMainPsnDoc(psnvo);
			// wangkf add
			nc.bs.bd.cache.BDDelLog delLog = new nc.bs.bd.cache.BDDelLog();
			delLog.delPKs("bd_psndoc", pk_psndocs);
			delLog.delPKs("bd_psnbasdoc", new String[] { pk_psnbasdoc });

			// 通知缓存删除该人员
			nc.bs.bd.cache.CacheProxy.fireDataDeleted("bd_psndoc", (String) psnvo.getFieldValue("pk_psndoc"));// v50 add
			// zhyan
			nc.bs.bd.cache.CacheProxy.fireDataDeleted("bd_psnbasdoc", (String) psnvo.getFieldValue("pk_psnbasdoc"));// v50
			// add
			// zhyan
			HIDelegator.getIPersonADDSV().operatePersonInfo(pk_psndocs, IPersonADDSV.DELETE, pk_corp);
		} catch (java.sql.SQLException e) {
			if (e.getMessage().startsWith(
					nc.bs.ml.NCLangResOnserver.getInstance().getStrByID("600704", "UPP600704-000001")/* @res "已引用" */)) {
				throwBusinessException(new Exception(nc.bs.ml.NCLangResOnserver.getInstance().getStrByID("600704",
				"UPP600704-000002")/*
				 * @res "该记录已经被引用，不能被删除！"
				 */));
			} else {
				throwBusinessException(e);
			}
		} catch (Exception e) {
			throwBusinessException(e);
		}

	}

	/**
	 * 删除采集节点人员。 创建日期：(2004-12-3 16:23:41)
	 * 
	 * @param psnvo
	 *            GeneralVO
	 * @param pk_corp
	 *            java.lang.String
	 * @exception BusinessException
	 *                异常说明。
	 */
	public void deletePersonnotinDoc(GeneralVO psnvo, String pk_corp) throws BusinessException {
		try {
			String pk_psnbasdoc = (String) psnvo.getFieldValue("pk_psnbasdoc");
			PsnInfDMO dmo = new PsnInfDMO();
			String[] pk_psndocs = dmo.queryRefPsndoc(pk_psnbasdoc);
			for (int i = 0; i < pk_psndocs.length; i++) {
				if (pk_psndocs[i] == null || pk_psndocs[i].length() != 20) {
					throwBusinessException(new Exception(nc.bs.ml.NCLangResOnserver.getInstance().getStrByID("600704",
					"UPP600704-000004")/*
					 * @res "数据错误，请刷新后重试！"
					 */));
				}
				String[] tableCodes = dmo.queryAllSubTable(pk_corp);// 查询出所有子表
				if (tableCodes != null) {
					for (int j = 0; j < tableCodes.length; j++) {
						dmo.deletePsndoc(tableCodes[j], psnvo);// 删除子表记录
					}
				}
				dmo.deletePsndoc("bd_psndoc", psnvo);// 删除主表记录
			}
			dmo.deleteMainPsnDoc(psnvo);
			// wangkf add
			nc.bs.bd.cache.BDDelLog delLog = new nc.bs.bd.cache.BDDelLog();
			delLog.delPKs("bd_psndoc", pk_psndocs);
			delLog.delPKs("bd_psnbasdoc", new String[] { pk_psnbasdoc });

			// 通知缓存删除该人员
			nc.bs.bd.cache.CacheProxy.fireDataDeleted("bd_psndoc", (String) psnvo.getFieldValue("pk_psndoc"));// v50 add
			// zhyan
			nc.bs.bd.cache.CacheProxy.fireDataDeleted("bd_psnbasdoc", (String) psnvo.getFieldValue("pk_psnbasdoc"));// v50
			// add
			// zhyan

		} catch (java.sql.SQLException e) {
			throwBusinessException(e);
		} catch (Exception e) {
			throwBusinessException(e);
		}
	}

	/**
	 * 删除返聘人员。 创建日期：(2004-12-3 16:23:41)
	 * 
	 * @param psnvo
	 *            GeneralVO
	 * @param pk_corp
	 *            java.lang.String
	 * @param hiretype
	 *            int 0 返聘，1 再聘
	 * @exception BusinessException
	 *                异常说明。
	 */
	public void deletePersonRehire(GeneralVO psnvo, String pk_corp, int hiretype) throws BusinessException {
		try {
			String pk_psndoc = (String) psnvo.getFieldValue("pk_psndoc");
			String pk_psnbasdoc = (String) psnvo.getFieldValue("pk_psnbasdoc");

			PsnInfDMO dmo = new PsnInfDMO();
			String[] tableCodes = dmo.queryAllSubTable(pk_corp);// 查询出所有子表
			if (tableCodes != null) {
				for (int j = 0; j < tableCodes.length; j++) {
					dmo.deletePsndoc(tableCodes[j], psnvo);
				}
			}
			dmo.deletePsndoc("bd_psndoc", psnvo);// 删除主表记录
			String belongpk_corp = null;
			if (hiretype == 0) {// 返聘查询离退3
				belongpk_corp = dmo.queryOriPkcorp(pk_psnbasdoc, 3);
			}
			// 再聘删除时不需要改回归属公司
			else {// 再聘查解聘2
				belongpk_corp = dmo.queryOriPkcorp(pk_psnbasdoc, 2);
			}
			if (belongpk_corp != null && !belongpk_corp.equalsIgnoreCase(pk_corp)) {
				dmo.updateBasCorpPk(pk_psnbasdoc, belongpk_corp);
			}

			nc.bs.bd.cache.BDDelLog delLog = new nc.bs.bd.cache.BDDelLog();
			delLog.delPKs("bd_psndoc", new String[] { pk_psndoc });
			// 通知缓存删除该人员
			nc.bs.bd.cache.CacheProxy.fireDataDeleted("bd_psndoc", pk_psndoc);// v50 add zhyan

			HIDelegator.getIPersonADDSV().operatePersonInfo(new String[] { pk_psndoc }, IPersonADDSV.DELETE, pk_corp);
		} catch (java.sql.SQLException e) {
			throwBusinessException(e);
		} catch (Exception e) {
			throwBusinessException(e);
		}
	}

	/**
	 * 过滤单据模板，除去不可见页签，设置追踪信息集。 创建日期：(2004-5-26 9:40:04)
	 * 
	 * @return nc.vo.pub.bill.BillTempletVO
	 * @param newTempletVO
	 *            nc.vo.pub.bill.BillTempletVO
	 */
	public Object[] filter(String corp, BillTempletVO newTempletVO1) throws BusinessException {
		Object[] result = null;
		try {
			// 设置追踪信息集
			Hashtable traces = new Hashtable();
			traces.put("hi_psndoc_deptchg", "hi_psndoc_deptchg");
			traces.put("hi_psndoc_part", "hi_psndoc_part");
			traces.put("hi_psndoc_retire", "hi_psndoc_retire");
			traces.put("hi_psndoc_orgpsn", "hi_psndoc_orgpsn");
			traces.put("hi_psndoc_psnchg", "hi_psndoc_psnchg");
			traces.put("hi_psndoc_dimission", "hi_psndoc_dimission");
			traces.put("hi_psndoc_capa", "hi_psndoc_capa");
			// traces.put("hi_psndoc_keypsn", "hi_psndoc_keypsn");
			// BillTabVO[] vos = head.getStructvo().getBillTabVOs();
			// if (vos != null && vos.length > 0) {
			// for (int i = 0; i < vos.length; i++) {
			Enumeration<String> enumer = traceTableMap.keys();
			while (enumer.hasMoreElements()) {
				String tableCode = enumer.nextElement();
				String module = (String) traceTableMap.get(tableCode);
				if (module != null && BSUtil.isModuleInstalled(corp, module))
					traces.put(tableCode, tableCode);
			}
			// }
			// }
			result = new Object[] { traces, null };
		} catch (Exception e) {
			throwBusinessException(e);
		}
		return result;
	}

	/**
	 * 此处插入方法描述。 创建日期：(2004-5-30 15:31:14)
	 * 
	 * @return nc.vo.bd.def.DefdoclistVO
	 * @param pk_defdoclist
	 *            java.lang.String
	 * @exception BusinessException
	 *                异常说明。
	 */
	public nc.vo.bd.def.DefdoclistVO findDefdoclistByPk(String pk_defdoclist) throws BusinessException {
		nc.vo.bd.def.DefdoclistVO result = null;
		try {
			nc.bs.bd.def.DefdoclistDMO dmo = new nc.bs.bd.def.DefdoclistDMO();
			result = dmo.findByPrimaryKey(pk_defdoclist);
		} catch (Exception e) {
			throwBusinessException(e);//
		}
		return result;
	}

	/**
	 * 
	 * @param pk_psnbasdoc
	 * @return
	 * @throws BusinessException
	 */
	public int deletePsnValidate(String pk_psnbasdoc) throws BusinessException {
		int count = 0;
		try {
			PsnInfDMO dmo = new PsnInfDMO();
			count = dmo.deletePsnValidate(pk_psnbasdoc);
		} catch (Exception e) {
			throwBusinessException(e);
		}
		return count;
	}

	/**
	 * 此处插入方法描述。 创建日期：(2004-7-10 17:06:07)
	 * 
	 * @return java.lang.String[]
	 * @param corpPK
	 *            java.lang.String
	 * @exception BusinessException
	 *                异常说明。
	 */
	public String[] getAllUserCode(String corpPK) throws BusinessException {
		String[] result = null;
		try {
			PsnInfDMO dmo = new PsnInfDMO();
			result = dmo.getAllUserCode(corpPK);
		} catch (Exception e) {
			throwBusinessException(e);
		}
		return result;
	}

	/**
	 * 此处插入方法描述。 创建日期：(2004-6-8 17:17:26)
	 * 
	 * @return java.lang.Object
	 * @param tableName
	 *            java.lang.String
	 * @param pkField
	 *            java.lang.String
	 * @param codeField
	 *            java.lang.String
	 * @param pk
	 *            java.lang.Object
	 * @exception BusinessException
	 *                异常说明。
	 */
	public Object getValue(String tableName, String pkField, String vField, Object pk) throws BusinessException {
		Object result = null;
		try {
			PsnInfDMO dmo = new PsnInfDMO();
			result = dmo.getValue(tableName, pkField, vField, pk);
		} catch (Exception e) {
			throwBusinessException(e);//
		}
		return result;
	}

	/**
	 * 添加一条子表记录。 创建日期：(2004-8-6 9:26:47)
	 * 
	 * @return java.lang.String
	 * @param tableCode
	 *            java.lang.String
	 * @param pk_psndoc
	 *            java.lang.String
	 * @param vo
	 *            nc.vo.hi.hi_301.GeneralVO
	 * @exception BusinessException
	 *                异常说明。
	 */
	public String insertChild(String tableCode, String pk_psndoc, GeneralVO vo) throws BusinessException {
		String pk_psndoc_sub = null;
		try {
			PsnInfDMO dmo = new PsnInfDMO();
			dmo.updateRecornum(tableCode, pk_psndoc, (Integer) vo.getAttributeValue("recordnum"), new Boolean(true));
			if (tableCode.equalsIgnoreCase("hi_psndoc_part")) {
				boolean isnotzaizhi = dmo.isNOtZaizhiPerson(pk_psndoc);
				if (isnotzaizhi) {
					vo.setAttributeValue("bendflag", "Y");
				}
			}
			pk_psndoc_sub = dmo.insertTable(tableCode, vo, "pk_psndoc_sub");

			if (tableCode.equalsIgnoreCase("hi_psndoc_deptchg")) {
				Object begindate = vo.getAttributeValue("begindate");
				// 如果上一条记录的结束日期为空则更新为当前记录的起始日期的前一天
				if (begindate != null) {
					dmo.synchroDeptChgEnddate(pk_psndoc, (new UFDate (begindate.toString()) ).getDateBefore(1));
				}
				// 新增一条记录，如果当前编辑的行的“是否在岗”为true，设置其余的为false fengwei 2009-09-21
				UFBoolean poststat = new UFBoolean(vo.getAttributeValue("poststat").toString()) ;
				if (poststat != null && poststat == UFBoolean.TRUE) {
					dmo.updatePoststat(pk_psndoc, pk_psndoc_sub);
				}

			} else if (tableCode.equalsIgnoreCase("hi_psndoc_ctrt")) {
				dmo.updateCtrt(pk_psndoc, new Boolean(true));
			} else if (tableCode.equalsIgnoreCase("hi_psndoc_edu")) {
				Object lasteducation = new UFBoolean(vo.getAttributeValue("lasteducation").toString());
				if (lasteducation != null && ((UFBoolean) lasteducation).booleanValue()) {
					dmo.updateEdu(pk_psndoc, pk_psndoc_sub);
				}
			}

		} catch (Exception e) {
			throwBusinessException(e);
		}
		return pk_psndoc_sub;
	}

	/**
	 * 保存引用人员接收消息用户
	 * 
	 * @param pk_corp
	 * @param userids
	 * @return
	 * @throws BusinessException
	 */
	public String insertRecievers(String pk_corp, String userids, int sendtype) throws BusinessException {
		String pk_psndoc_sub = null;
		try {
			PsnInfDMO dmo = new PsnInfDMO();
			boolean exist = dmo.isExistRecievers(pk_corp);
			if (exist) {
				dmo.updateRecievers(pk_corp, userids, sendtype);
			} else {
				pk_psndoc_sub = dmo.insertRecievers(pk_corp, userids, sendtype);
			}
		} catch (Exception e) {
			throwBusinessException(e);
		}
		return pk_psndoc_sub;
	}

	/**
	 * 插入主信息集。 v53增加返聘处理
	 */
	public String insertMain(GeneralVO psndocVO, GeneralVO accpsndocVO, GeneralVO mainaddpsndocVO,
			nc.vo.pub.billcodemanage.BillCodeObjValueVO billcodevo) throws BusinessException {
		String result = null;
		try {
			PsnInfDMO dmo = new PsnInfDMO();
			if (!"".equals(psndocVO.getAttributeValue("psncode"))) {
				if (dmo.checkPsn(psndocVO, new Integer(1)) == 1) { // 检查编码是否已存在
					IParValue paravaluebo = PubDelegator.getIParValue();
					String strBillCodeParam = paravaluebo.getParaString("0001", "HI_CODECRTTYPE");
					String isSoleInGroup = paravaluebo.getParaString("0001", "HI_CODEUNIQUE");
					String pk_corp = "";
					if (strBillCodeParam != null && strBillCodeParam.equalsIgnoreCase("1")) {// 如果自动产生人员编码，重复则重新产生一个
						//原接口会导致编号重复
						//IBillcodeRuleService billcodebo = SFAppServiceUtil.getBillcodeRuleService();
						BillcodeGenerater billcodebo =  new BillcodeGenerater();
						if (isSoleInGroup != null && "Y".equalsIgnoreCase(isSoleInGroup)) {// 如果是集团内唯一，则按集团产生人员编码
							pk_corp = "0001";
						} else {
							pk_corp = (String) psndocVO.getAttributeValue("pk_corp");
						}
//						String autopsnbillcode = billcodebo.getBillCode_RequiresNew("BS", pk_corp, null, billcodevo);
						String autopsnbillcode = billcodebo.getBillCode("BS", pk_corp, null, billcodevo);
						psndocVO.setAttributeValue("psncode", autopsnbillcode);
						if (dmo.checkPsn(psndocVO, new Integer(1)) == 1) { // 检查编码是否已存在
							// 如果再次取号还存在重复,则将重取的号放回pub_billcode_returned表中
							// HIDelegator.getBillcodeRule().returnBillCodeOnDelete(pk_corp, "BS", autopsnbillcode,
							// billcodevo);
							throw new BusinessException("psncode"
									+ nc.bs.ml.NCLangResOnserver.getInstance().getStrByID("600704", "UPP600704-000005")/*
									 * @res
									 * "该人员编码已经存在，请刷新再试！"
									 */);
						}

					} else {// 如果手工产生人员编码，则抛例外
						throw new BusinessException(nc.bs.ml.NCLangResOnserver.getInstance().getStrByID("600704",
						"UPP600704-000005")/*
						 * @res "该人员编码已经存在，请刷新再试！"
						 */);
					}
				}

			}
			accpsndocVO.setAttributeValue("psnname", psndocVO.getAttributeValue("psnname"));
			if (accpsndocVO.getAttributeValue("id") != null
					&& accpsndocVO.getAttributeValue("id").toString().trim().length() > 0) { // 身份证不为空检查是否已经加入黑名单（按身份证和姓名）
				if (dmo.checkPsn(accpsndocVO, new Integer(2)) == 2) {
					throw new BusinessException(nc.bs.ml.NCLangResOnserver.getInstance().getStrByID("600704",
					"UPP600704-000006")/*
					 * @res "该人员身份证＋姓名已经在黑名单中存在！"
					 */);
				}
			}
			String pk_psncl = (String) psndocVO.getAttributeValue("pk_psncl");
			boolean isoutduty = isOutdutyPsncl(pk_psncl);
			if (psndocVO.getAttributeValue("pk_deptdoc") != null
					&& ((String) psndocVO.getAttributeValue("pk_deptdoc")).length() == 20) {
				String pk_deptdoc = ((String) psndocVO.getAttributeValue("pk_deptdoc"));
				if (dmo.isDeptCancled(pk_deptdoc)) {
					throw new BusinessException(nc.bs.ml.NCLangResOnserver.getInstance().getStrByID("600704",
					"UPT600704-000275")/*
					 * @res "该部门已经封存,请刷新后再试！"
					 */);
				}

				if (dmo.isDeptDrop(pk_deptdoc) && !isoutduty) {
					throw new BusinessException(nc.bs.ml.NCLangResOnserver.getInstance().getStrByID("600704",
					"UPT600704-000333")/*
					 * @res "该部门已经撤消,请刷新后再试！"
					 */);
				}
			}

			if (psndocVO.getAttributeValue("pk_om_job") != null
					&& ((String) psndocVO.getAttributeValue("pk_om_job")).length() == 20) {
				String pk_om_job = ((String) psndocVO.getAttributeValue("pk_om_job"));
				if (dmo.isJobAbort(pk_om_job) && !isoutduty) {// 非在职人员才提示
					throw new BusinessException(nc.bs.ml.NCLangResOnserver.getInstance().getStrByID("600704",
					"UPT600704-000276")/*
					 * @res "该岗位已经撤销,请刷新后再试！"
					 */);
				}
			}
			accpsndocVO.removeAttributeName("belong_pk_corp");
			// 修改如下行
			psndocVO.setAttributeValue("tbm_prop", new Integer(2));

			String[] keys = dmo.insertMain(psndocVO, accpsndocVO, mainaddpsndocVO);

			// 更新拼音码
			updatePsnnamePinyin(keys[1], (String) psndocVO.getAttributeValue("psnname"));

			nc.bs.bd.cache.CacheProxy.fireDataInserted("bd_psnbasdoc", keys[0]);
			nc.bs.bd.cache.CacheProxy.fireDataInserted("bd_psndoc", keys[1]);

			psndocVO.setAttributeValue("pk_psnbasdoc", keys[0]);
			psndocVO.setAttributeValue("pk_psndoc", keys[1]);
			result = keys[1];
		} catch (Exception e) {
			throwBusinessException(e);
		}
		return result;
	}

	/**
	 * 
	 * @param accpsndocVO
	 * @return
	 * @throws BusinessException
	 */
	public boolean checkBadlist(GeneralVO accpsndocVO) throws BusinessException {
		boolean isbad = false;
		try {
			PsnInfDMO dmo = new PsnInfDMO();
			if (accpsndocVO.getAttributeValue("id") != null
					&& accpsndocVO.getAttributeValue("id").toString().trim().length() > 0) { // 身份证不为空检查是否已经加入黑名单（按身份证和姓名）
				if (dmo.checkPsn(accpsndocVO, new Integer(2)) == 2) {
					isbad = true;
				}
			}
		} catch (Exception e) {
			throwBusinessException(e);
		}

		return isbad;
	}

	/**
	 * 保存引用人员的工作信息 (hi_psndoc_ref)
	 * 
	 * @param psndocVO
	 * @return
	 * @throws BusinessException
	 */
	public String insertHiRef(GeneralVO psndocVO, boolean isNeedAFirm) throws BusinessException {
		String result = null;
		try {
			PsnInfDMO dmo = new PsnInfDMO();
			if (dmo.checkPsn(psndocVO, new Integer(1)) == 1) { // 检查编码是否已存在
				throw new BusinessException(nc.bs.ml.NCLangResOnserver.getInstance().getStrByID("600704",
				"UPP600704-000005")/*
				 * @res "该人员编码已经存在，请刷新再试！"
				 */);
			}

			// 检查原来是否被引用过，被引用过，则使用原来的pk_psndoc
			String oldpk_psndoc = dmo.getOldPsnDocPKOfRef(psndocVO);
			psndocVO.setAttributeValue("pk_psndoc", oldpk_psndoc);
			if (isNeedAFirm) {
				if (oldpk_psndoc != null) {
					deletePsnData("hi_psndoc_ref", oldpk_psndoc);
				}
				result = dmo.insertHiRef(psndocVO);
			} else {
				PsnInfDAO dao = new PsnInfDAO();
				if (oldpk_psndoc != null) {// 如果原来引用过，再直接插入原pk必定报主键冲突，因此现将原已取消引用的记录删除。
					deletePsnData("bd_psndoc", oldpk_psndoc);
				}
				result = dao.insertTable_NOTAlwaysNewPK("bd_psndoc", psndocVO, "pk_psndoc");
			}

			psndocVO.setAttributeValue("pk_psndoc", result);

			if (psndocVO != null) {
				String pk_corp = (String) psndocVO.getAttributeValue("pk_corp");
				HIDelegator.getIPersonADDSV().operatePersonInfo(new String[] { result }, IPersonADDSV.MODIFY, pk_corp);
			}
		} catch (Exception e) {
			throwBusinessException(e);
		}
		return result;
	}

	/**
	 * 处理多条引用人员，保存到hi_psndoc_ref，或保存到bd_psndoc
	 * 
	 * @param psndocVO
	 * @return
	 * @throws BusinessException
	 */
	public String insertHiRefs(GeneralVO[] psndocVOs, boolean isNeedAFirm,
			nc.vo.pub.billcodemanage.BillCodeObjValueVO billcodevo) throws BusinessException {
		String result = null;
		if (psndocVOs.length < 1)
			return null;
		try {

			PsnInfDMO dmo = new PsnInfDMO();
			checkPsnCode(psndocVOs, dmo, billcodevo);

			if (isNeedAFirm) {// 若需要确认，则将记录插入hi_psndoc_ref
				dmo.insertHiRefs(psndocVOs);
			} else {// 若不需确认，则直接插入bd_psndoc
				dmo.insertPsndocs(psndocVOs);

				if (psndocVOs != null && psndocVOs.length > 0) {
					String pk_corp = (String) psndocVOs[0].getAttributeValue("pk_corp");
					HIDelegator.getIPersonADDSV().operatePersonInfo(getPKsFromVOS(psndocVOs), IPersonADDSV.MODIFY,
							pk_corp);
				}
			}

		} catch (Exception e) {
			throwBusinessException(e);
		}
		return result;
	}

	private void checkPsnCode(GeneralVO[] psndocVOs, PsnInfDMO dmo,
			nc.vo.pub.billcodemanage.BillCodeObjValueVO billcodevo) throws SQLException, BusinessException {

		IParValue paravaluebo = PubDelegator.getIParValue();
		String strBillCodeParam = paravaluebo.getParaString("0001", "HI_CODECRTTYPE");
		String isSoleInGroup = paravaluebo.getParaString("0001", "HI_CODEUNIQUE");
		String pk_corp = "";

		// 检查编码是否存在
		boolean existerror = false;
		String psnnames = "";
		BillcodeGenerater billcodebo =  new BillcodeGenerater();
		for (GeneralVO psndocVO : psndocVOs) {
			if (dmo.checkPsn(psndocVO, new Integer(1)) == 1) {// 编码存在

				if (strBillCodeParam != null && strBillCodeParam.equalsIgnoreCase("1")) {// 如果自动产生人员编码，重复则重新产生一个
					//原接口可能会导致编号重复
					//IBillcodeRuleService billcodebo = SFAppServiceUtil.getBillcodeRuleService();

					if (isSoleInGroup != null && "Y".equalsIgnoreCase(isSoleInGroup)) {// 如果是集团内唯一，则按集团产生人员编码
						pk_corp = "0001";
					} else {
						pk_corp = (String) psndocVO.getAttributeValue("pk_corp");
					}
					//String autopsnbillcode = billcodebo.getBillCode_RequiresNew("BS", pk_corp, null, billcodevo);
					String autopsnbillcode = billcodebo.getBillCode("BS", pk_corp, null, billcodevo);

					psndocVO.setAttributeValue("psncode", autopsnbillcode);
					// 新产生的代码若还存在，则报异常
					if (dmo.checkPsn(psndocVO, new Integer(1)) == 1) {
						existerror = true;
						psnnames = "," + psndocVO.getAttributeValue("psnname");
					}
				} else {// 手工填写的编码，返回异常。
					existerror = true;
					psnnames = "," + psndocVO.getAttributeValue("psnname");
				}
			}
		}
		if (existerror) {
			if (strBillCodeParam != null && strBillCodeParam.equalsIgnoreCase("1")) {
				throw new BusinessException(nc.bs.ml.NCLangResOnserver.getInstance().getStrByID("600704",
				"UPT600704-000316")/*
				 * @res "下列人员的人员编码已经存在，请刷新再试！"
				 */
				+ "\n" + psnnames.substring(1));
			} else {
				throw new BusinessException(nc.bs.ml.NCLangResOnserver.getInstance().getStrByID("600704",
				"UPT600704-000323")/*
				 * @res "下列人员的人员编码已经存在，请修改！"
				 */
				+ "\n" + psnnames.substring(1));
			}
		}
	}

	/**
	 * 从hi_flddict何hi_setdic表中查询bd_accpsndoc中字段acc_fldcode的关联字段。 创建日期：(2004-5-30 14:50:48)
	 * 
	 * @return boolean
	 * @param acc_fldcode
	 *            java.lang.String
	 * @exception BusinessException
	 *                异常说明。
	 */
	public GeneralVO[] queryAllRelatedTableField(String corppk) throws BusinessException {
		GeneralVO[] vos = null;
		try {
			vos = new PsnInfDMO().queryAllRelatedTableField(corppk);
		} catch (Exception e) {
			throwBusinessException(e);
		}
		return vos;
	}

	/**
	 * 采集节点保存完人员后查询人员信息。 创建日期：(2004-5-20 14:17:39)
	 * 
	 * @return nc.vo.hi.hi_301.GeneralVO[]
	 * @param fields
	 *            java.lang.String[]
	 * @param pk_corp
	 *            java.lang.String
	 * @param conditions
	 *            nc.vo.pub.query.ConditionVO[]
	 * @exception BusinessException
	 *                异常说明。
	 */
	public GeneralVO[] queryByCondition(String pk_corp, ConditionVO[] conditions, String powerSql, String listfield,
			String normalwheresql) throws BusinessException {
		GeneralVO[] vos = null;
		try {
			String fixedField = getFixedFiled(1);// sql语句中的固定字段
			String selfield = null;
			if (listfield == null || "".equalsIgnoreCase(listfield.trim())) {
				selfield = fixedField;
			} else {
				selfield = fixedField + "," + listfield;
			}
			String where = null;

			StringBuffer tableStrb = new StringBuffer();
			tableStrb.append(" from bd_psndoc ");
			tableStrb.append(" inner join bd_psnbasdoc on bd_psndoc.pk_psnbasdoc=bd_psnbasdoc.pk_psnbasdoc");
			tableStrb.append(" inner join bd_corp on bd_psndoc.pk_corp=bd_corp.pk_corp");
			tableStrb.append(" left outer join bd_deptdoc on bd_psndoc.pk_deptdoc=bd_deptdoc.pk_deptdoc");

			tableStrb.append(" inner join bd_psncl on bd_psndoc.pk_psncl=bd_psncl.pk_psncl");
			tableStrb.append(" left outer join om_job on bd_psndoc.pk_om_job=om_job.pk_om_job");
			tableStrb.append(" left outer join om_duty on bd_psndoc.dutyname=om_duty.pk_om_duty");
			tableStrb.append(" left outer join hi_psndoc_keypsn on bd_psndoc.pk_psndoc=hi_psndoc_keypsn.pk_psndoc");

			//内控可能传空
			if(pk_corp==null){
				where = " where 1=1";
			}else{
				where = " where bd_psndoc.pk_corp = '" + pk_corp + "'";
			}
			if (normalwheresql != null && !normalwheresql.trim().equalsIgnoreCase("")) {
				where += normalwheresql;
			}
			String cwhere = getWhere2(conditions, 1);
			if (cwhere != null && cwhere.length() > 0) {
				where += " and (" + cwhere + ")";
			}
			if (powerSql != null && powerSql.trim().startsWith("select"))
				where += " and (bd_psndoc.pk_deptdoc in (" + powerSql + "))";

			String select = " select distinct ";// "+" top "+recordcount

			// String appTable = getTableStr(conditions, 1);
			String appTable = getTableStr(conditions, 1);
			if (appTable != null && appTable.trim().length() > 0) {
				tableStrb.append(appTable);
			}
			String tableStr = tableStrb.toString();
			String order = " order by bd_psndoc.showorder asc,bd_corp.unitcode asc,bd_deptdoc.deptcode asc,bd_psndoc.psncode asc,om_job.jobcode asc,bd_psncl.psnclasscode asc,om_duty.dutycode asc ";// hi_psndoc_deptchg.jobtype
			// asc,
			String sql = select + selfield + tableStr + where + order;

			// PsnInfDMO dmo = new PsnInfDMO();
			vos = new PsnInfDMO().queryBySql(sql);
			vos = fomularShoworder(vos);// v50 add
		} catch (Exception e) {
			throwBusinessException(e);
		}
		return vos;
	}

	private String getFixedFiled(int scope) {
		String fixedField = null;
		if (scope == 0) {
			fixedField = " hi_psndoc_deptchg.pk_psndoc_sub,hi_psndoc_deptchg.jobtype as jobtypeflag,bd_psndoc.clerkflag,bd_psndoc.isreturn, "
				+ " hi_psndoc_deptchg.pk_corp,bd_psnbasdoc.pk_psnbasdoc,bd_psndoc.psncode,bd_psnbasdoc.psnname,bd_psnbasdoc.id "
				+ " ,bd_corp.unitcode,om_job.jobcode,bd_psncl.psnclasscode,om_duty.dutycode ,bd_psndoc.pk_corp man_pk_corp,bd_psnbasdoc.pk_corp belong_pk_corp "
				+ ", om_job.jobname,bd_deptdoc.deptname,hi_psndoc_deptchg.pk_deptdoc,hi_psndoc_deptchg.pk_om_duty,bd_psndoc.pk_psndoc "
				+ ",bd_psndoc.showorder ,bd_deptdoc.deptcode,bd_deptdoc.showorder bd_deptdoc_showorder,hi_psndoc_deptchg.pk_psncl,bd_corp.unitname,bd_corp.showorder bd_corp_showorder "
				+ ",bd_psncl.psnclassname,hi_psndoc_deptchg.pk_postdoc as pk_om_job "
				+ ",hi_psndoc_deptchg.pk_jobserial as jobseries,hi_psndoc_deptchg.pk_detytype as series,hi_psndoc_deptchg.pk_jobrank as jobrank ,bd_psncl.psnclscope";
		} else if (scope == 5) {
			fixedField = " bd_psndoc.clerkflag,bd_psnbasdoc.indocflag,bd_psndoc.isreturn, "
				+ " bd_psndoc.pk_corp man_pk_corp,bd_psnbasdoc.pk_psnbasdoc,bd_psndoc.psncode,bd_psnbasdoc.psnname,bd_psnbasdoc.id "
				+ " ,bd_corp.unitcode,bd_corp.showorder bd_corp_showorder,bd_psndoc.pk_om_job as pk_om_job ,om_job.jobcode,bd_psncl.psnclasscode,om_duty.dutycode ,bd_psnbasdoc.pk_corp belong_pk_corp"
				+ ", om_job.jobname,bd_deptdoc.deptname,bd_deptdoc.showorder bd_deptdoc_showorder,bd_psndoc.pk_deptdoc,om_duty.pk_om_duty,bd_psndoc.pk_psndoc"
				+ ",bd_psndoc.showorder ,bd_deptdoc.deptcode,bd_psndoc.pk_psncl,bd_corp.unitname,bd_psncl.psnclassname ,bd_psncl.psnclscope,bd_psndoc.sealdate,bd_psndoc.isreferenced";
		} else if (scope > 0) {
			fixedField = " bd_psndoc.clerkflag,bd_psnbasdoc.indocflag,bd_psndoc.isreturn, "
				+ " bd_psndoc.pk_corp man_pk_corp,bd_psnbasdoc.pk_psnbasdoc,bd_psndoc.psncode,bd_psnbasdoc.psnname,bd_psnbasdoc.id "
				+ " ,bd_corp.unitcode,bd_corp.showorder bd_corp_showorder,bd_psndoc.pk_om_job as pk_om_job ,om_job.jobcode,bd_psncl.psnclasscode,om_duty.dutycode ,bd_psnbasdoc.pk_corp belong_pk_corp"
				+ ", om_job.jobname,bd_deptdoc.deptname,bd_deptdoc.showorder bd_deptdoc_showorder,bd_psndoc.pk_deptdoc,om_duty.pk_om_duty,bd_psndoc.pk_psndoc"
				+ ",bd_psndoc.showorder ,bd_deptdoc.deptcode,bd_psndoc.pk_psncl,bd_corp.unitname,bd_psncl.psnclassname ,bd_psncl.psnclscope";
		} else {
			fixedField = " bd_psndoc.clerkflag,bd_psndoc.isreturn, "
				+ " bd_psndoc.pk_corp man_pk_corp,bd_psnbasdoc.pk_psnbasdoc,bd_psndoc.psncode,bd_psnbasdoc.psnname,bd_psnbasdoc.id "
				+ " ,bd_corp.unitcode,bd_corp.showorder bd_corp_showorder,om_job.jobcode,bd_psncl.psnclasscode,om_duty.dutycode ,bd_psnbasdoc.pk_corp belong_pk_corp"
				+ ", om_job.jobname,bd_deptdoc.deptname,bd_deptdoc.showorder bd_deptdoc_showorder,bd_psndoc.pk_deptdoc,om_duty.pk_om_duty,bd_psndoc.pk_psndoc"
				+ ",bd_psndoc.showorder ,bd_deptdoc.deptcode,bd_psndoc.pk_psncl,bd_corp.unitname,bd_psncl.psnclassname ,bd_psncl.psnclscope";

		}

		return fixedField;

	}

	/**
	 * 维护节点查询人员信息记录总数。(过期)
	 * 
	 * @param indocflag
	 * @param pk_corps
	 * @param conditions
	 * @param scope
	 * @param userid
	 * @param jobtype
	 * @param listfield
	 * @return
	 * @throws BusinessException
	 */
	public int queryRecordCountByCondition(boolean indocflag, String[] pk_corps, ConditionVO[] conditions, int scope,
			String loginCorp, String userid, int jobtype, String normalwheresql) throws BusinessException {
		int result = -1;
		try {
			String where = null;
			StringBuffer tableStrb = new StringBuffer();
			String powerSql = null;
			String appTable = "";
			if (scope == 0) {
				appTable = getTableStr(conditions, 0);
			} else {
				appTable = getTableStr(conditions, 1);
			}
			// String corps = null;
			// corps = parsePkcorps(pk_corps);
			if (scope > 0) {// 归属范围
				tableStrb.append(" from bd_psndoc ");
				tableStrb.append(" inner join bd_psnbasdoc on bd_psndoc.pk_psnbasdoc=bd_psnbasdoc.pk_psnbasdoc");
				tableStrb.append(" inner join bd_corp on bd_psndoc.pk_corp=bd_corp.pk_corp");
				tableStrb.append(" left outer join bd_deptdoc on bd_psndoc.pk_deptdoc=bd_deptdoc.pk_deptdoc");
				tableStrb.append(" inner join bd_psncl on bd_psndoc.pk_psncl=bd_psncl.pk_psncl");
				tableStrb.append(" left outer join om_job on bd_psndoc.pk_om_job=om_job.pk_om_job");
				tableStrb.append(" left outer join om_duty on bd_psndoc.dutyname=om_duty.pk_om_duty");
				// 组织条件
				where = " where 0=0 ";
				// if(corps!=null&& corps.length()>0){
				// where += " and bd_psndoc.pk_corp in " + corps + " " ;
				// }
				if (normalwheresql != null && !normalwheresql.trim().equalsIgnoreCase("")) {
					where += normalwheresql;
				}
				String cwhere = getWhere2(conditions, scope);
				if (cwhere != null && cwhere.length() > 0) {
					where += " and (" + cwhere + ")";
				}
				where += " and bd_psndoc.indocflag = '" + (indocflag ? "Y" : "N") + "'";
				where += " and bd_psndoc.psnclscope = " + scope;

				// 附加部门权限
				powerSql = getDeptPowerSqls("bd_psndoc", userid, pk_corps[0], loginCorp, false);
				if (powerSql != null && powerSql.length() > 0) {
					where += " and (" + powerSql + ")";
				}
				// 附加人员类别权限 V55 add
				String powerPsnclSql = getPsnclPowerSql("bd_psndoc", userid, loginCorp);
				if (powerPsnclSql != null && powerPsnclSql.length() > 0) {
					where += " and (" + powerPsnclSql + ")";
				}
			} else {
				tableStrb
				.append(" from hi_psndoc_deptchg inner join bd_psndoc on hi_psndoc_deptchg.pk_psndoc=bd_psndoc.pk_psndoc");// bd_psndoc
				tableStrb
				.append(" inner join bd_psnbasdoc on hi_psndoc_deptchg.pk_psnbasdoc=bd_psnbasdoc.pk_psnbasdoc");
				tableStrb.append(" inner join bd_corp on hi_psndoc_deptchg.pk_corp=bd_corp.pk_corp");
				tableStrb.append(" left outer join bd_deptdoc on hi_psndoc_deptchg.pk_deptdoc=bd_deptdoc.pk_deptdoc");
				tableStrb.append(" inner join bd_psncl on hi_psndoc_deptchg.pk_psncl=bd_psncl.pk_psncl");
				tableStrb.append(" left outer join om_job on hi_psndoc_deptchg.pk_postdoc=om_job.pk_om_job");
				tableStrb.append(" left outer join om_duty on hi_psndoc_deptchg.pk_om_duty=om_duty.pk_om_duty");
				where = " where  hi_psndoc_deptchg.lastflag = 'Y' and hi_psndoc_deptchg.bendflag='N'";//
				if (normalwheresql != null && !normalwheresql.trim().equalsIgnoreCase("")) {
					if (normalwheresql.indexOf("bd_psndoc.pk_deptdoc") > 0) {
						normalwheresql = StringUtil.replaceAllString(normalwheresql, "bd_psndoc.pk_deptdoc",
						"hi_psndoc_deptchg.pk_deptdoc");
					}
					where += normalwheresql;
				}
				if (jobtype >= 0) {
					where += " and hi_psndoc_deptchg.jobtype=" + jobtype + " ";
				}
				String cwhere = getWhere2(conditions, scope);
				if (cwhere != null && cwhere.length() > 0) {
					where += " and (" + cwhere + ")";
				}
				where += " and bd_psndoc.indocflag = '" + (indocflag ? "Y" : "N") + "'";
				where += " and (bd_psndoc.psnclscope = 0 )  and bd_psncl.psnclscope = " + scope;
				// 附加部门权限
				powerSql = getDeptPowerSqls("hi_psndoc_deptchg", userid, pk_corps[0], loginCorp, false);
				if (powerSql != null && powerSql.length() > 0) {
					where += " and (" + powerSql + ")";
				}

				// 附加人员类别权限 V55 add
				String powerPsnclSql = getPsnclPowerSql("bd_psndoc", userid, loginCorp);
				if (powerPsnclSql != null && powerPsnclSql.length() > 0) {
					where += " and (" + powerPsnclSql + ")";
				}
			}

			if (appTable != null && appTable.trim().length() > 0) {
				tableStrb.append(appTable);
			}
			String tableStr = tableStrb.toString();
			String select = "select count(distinct bd_psndoc.pk_psndoc) ";
			if (scope == 0) {
				select = "select count(distinct hi_psndoc_deptchg.pk_psndoc_sub) ";
			}
			if (tableStr.indexOf("inner join hi_psndoc") >= 0 || tableStr.indexOf("inner join v_hr_psndoc_edu") >= 0) {
			}
			// String
			String sql = select + tableStr + where;
			PsnInfDMO dmo = new PsnInfDMO();
			result = dmo.queryRecordCountBySql(sql);
		} catch (Exception e) {
			throwBusinessException(e);
		}
		return result;
	}

	/**
	 * 修改非业务子集的recordnum v50
	 * 
	 * @param pk_psnbasdoc
	 * @param Subsetdatas
	 */
	public void updateSubsetRecordnum(String pk_psnbasdoc, String tablecode,
			CircularlyAccessibleValueObject[] Subsetdatas) throws BusinessException {
		try {
			PsnInfDMO dmo = new PsnInfDMO();
			dmo.updateSubsetRecordnum(pk_psnbasdoc, tablecode, Subsetdatas);
		} catch (Exception e) {
			throwBusinessException(e);
		}

	}

	/**
	 * v50 add 处理序号
	 * 
	 * @param vos
	 * @return
	 */
	private GeneralVO[] fomularShoworder(GeneralVO[] vos) {
		if (vos != null && vos.length > 0) {
			for (int i = 0; i < vos.length; i++) {
				Object o = vos[i].getAttributeValue("showorder");
				if (o != null) {
					if ((Integer) o == 999999) {
						vos[i].setAttributeValue("showorder", null);
					}
				}
			}
		}
		return vos;
	}

	public CommonVO[] executeQuerySQL(String sql, SQLParameter parm) {
		CommonVO[] vos = null;
		try {
			List<CommonVO> list = (List<CommonVO>) PubDelegator.getIPersistenceHome().executeQuery(sql, parm,
					new CommonVOProcessor());

			if (!list.isEmpty()) {
				vos = list.toArray(new CommonVO[list.size()]);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return vos;
	}

	/**
	 * 得到对应公司的权限sql, 2008.11.24新需求：若是查看下级单位，则不受部门权限控制，本单位（登录单位）受部门权限限制 dusx upt.
	 * 
	 * @param depttable
	 *            部门权限所对应的表
	 * @param userid
	 *            用户id
	 * @param pk_corp
	 *            所选公司pk
	 * @param loginCorp
	 *            登录公司pk
	 * @param includesubcorp
	 *            是否包含子公司
	 */
	public String getDeptPowerSqls(String depttable, String userid, String pk_corp, String loginCorp,
			boolean includesubcorp) throws BusinessException {
		if (pk_corp == null || pk_corp.length() < 1)
			return "";
		IDataPowerService datapower = ((IDataPowerService) NCLocator.getInstance().lookup(
				IDataPowerService.class.getName()));
		String powersql = "";
		try {

			// 部门权限
			String deptsubsql = "";
			if (loginCorp.equals(pk_corp)) {
				boolean useDeptPower = datapower.isUsedDataPower("bd_deptdoc", "部门档案", pk_corp);
				String deptright = datapower.getSubSql("bd_deptdoc", "部门档案", userid, pk_corp);
				if (useDeptPower && deptright != null) {
					deptsubsql = depttable.trim() + ".pk_corp ='" + pk_corp + "' and " + depttable.trim()
					+ ".pk_deptdoc in (" + deptright + ")";
				}
			}
			// 选择公司的innercode
			String innercode = "";
			if (includesubcorp) {
				CommonVO[] commvos = executeQuerySQL("select innercode from bd_corp where pk_corp='" + pk_corp + "' ",
						null);
				if (commvos.length < 1)
					return "";
				innercode = (String) commvos[0].getAttributeValue("innercode");
			}

			// 需要加部门权限
			if (loginCorp.equals(pk_corp) && deptsubsql.length() > 0) {
				// 需要加下级公司(下级公司不需加部门权限)
				if (includesubcorp) {
					powersql = "(" + deptsubsql + ") or bd_corp.innercode like '" + innercode + "_%'";
				} else {
					powersql = deptsubsql;
				}
			} else {// 不需要加部门权限 or 需要加部门权限，但没有部门权限
				// 需要加下级公司
				if (includesubcorp) {
					powersql = "bd_corp.innercode like '" + innercode + "%'";
				} else {
					powersql = depttable.trim() + ".pk_corp = '" + pk_corp + "'";
				}
			}

			if (includesubcorp) {
				// 公司权限sql
				String corppowersql = (new PsnInfoDMO()).queryCorpPower(userid, pk_corp, innercode);
				powersql = "(" + powersql + ")" + "and bd_corp.pk_corp in(" + corppowersql + ")";
			}

		} catch (Exception e) {
			throwBusinessException(e);
		}

		return powersql;

	}

	/**
	 * 得到对应公司的权限sql, 2008.11.24新需求：若是查看下级单位，则不受部门权限控制，本单位（登录单位）受部门权限限制 dusx upt.
	 * 
	 * 2012.04.01 新需求：增加多公司选择功能
	 * @param depttable
	 *            部门权限所对应的表
	 * @param userid
	 *            用户id
	 * @param pk_corp
	 *            所选公司pk
	 * @param loginCorp
	 *            登录公司pk
	 * @param includesubcorp
	 *            是否包含子公司
	 */
	public String getDeptPowerSqlsForPkCorps(String depttable, String userid, String pk_corp, String loginCorp,
			boolean includesubcorp,Vector vecCorp) throws BusinessException {
		if (pk_corp == null || pk_corp.length() < 1)
			return "";
		IDataPowerService datapower = ((IDataPowerService) NCLocator.getInstance().lookup(
				IDataPowerService.class.getName()));
//		String powersql = "";
		StringBuffer sb=new StringBuffer();
		try {
			//外加一个循环外套
			for(int i=0;i<vecCorp.size();i++){
				String powersql = "";
				pk_corp=vecCorp.get(i)+"";
				// 部门权限
				String deptsubsql = "";
				if (loginCorp.equals(pk_corp)) {
					boolean useDeptPower = datapower.isUsedDataPower("bd_deptdoc", "部门档案", pk_corp);
					String deptright = datapower.getSubSql("bd_deptdoc", "部门档案", userid, pk_corp);
					if (useDeptPower && deptright != null) {
						deptsubsql = depttable.trim() + ".pk_corp ='" + pk_corp + "' and " + depttable.trim()
						+ ".pk_deptdoc in (" + deptright + ")";
					}
				}
				// 选择公司的innercode
				String innercode = "";
				if (includesubcorp) {
					CommonVO[] commvos = executeQuerySQL("select innercode from bd_corp where pk_corp='" + pk_corp + "' ",
							null);
					if (commvos.length < 1)
						return "";
					innercode = (String) commvos[0].getAttributeValue("innercode");
				}

				// 需要加部门权限
				if (loginCorp.equals(pk_corp) && deptsubsql.length() > 0) {
					// 需要加下级公司(下级公司不需加部门权限)
					if (includesubcorp) {
						powersql = "(" + deptsubsql + ") or bd_corp.innercode like '" + innercode + "_%'";
					} else {
						powersql = deptsubsql;
					}
				} else {// 不需要加部门权限 or 需要加部门权限，但没有部门权限
					// 需要加下级公司
					if (includesubcorp) {
						powersql = "bd_corp.innercode like '" + innercode + "%'";
					} else {
						powersql = depttable.trim() + ".pk_corp = '" + pk_corp + "'";
					}
				}

				if (includesubcorp) {
					// 公司权限sql
					String corppowersql = (new PsnInfoDMO()).queryCorpPower(userid, pk_corp, innercode);
					powersql = "(" + powersql + ")" + "and bd_corp.pk_corp in(" + corppowersql + ")";
				}

				sb.append("(")
				.append(powersql.toString())
				.append(")or");
			}
		} catch (Exception e) {
			throwBusinessException(e);
		}

		return sb.toString().substring(0,sb.toString().length()-2);

	}
	/**
	 * 卡片节点查询人员信息。(过期)
	 * 
	 * @param indocflag
	 * @param pk_corps
	 * @param conditions
	 * @param scope
	 * @param userid
	 * @param jobtype
	 * @param listfield
	 * @param normalwheresql
	 * @param recordcount
	 * @return
	 * @throws BusinessException
	 */
	public GeneralVO[] queryByCondition(boolean indocflag, String[] pk_corps, ConditionVO[] conditions, int scope,
			String loginCorp, String userid, int jobtype, String listfield, String normalwheresql, int recordcount,
			boolean isCheckDeptPower) throws BusinessException {
		GeneralVO[] vos = null;
		try {
			// 组织查询的列
			String selfield = null;
			// 根据人员归属范围处理显示列
			String fixedField = getFixedFiled(scope);// sql语句中的固定字段
			// 前台传回的显示列
			if (listfield == null || "".equalsIgnoreCase(listfield.trim())) {
				selfield = fixedField;
			} else {
				selfield = fixedField + "," + listfield;
			}
			// 组织查询条件
			String where = null;
			// 组织查询关联表
			StringBuffer tableStrb = new StringBuffer();
			String powerSql = null;
			String appTable = "";
			if (scope == 0) {
				appTable = getTableStr(conditions, 0);
			} else {
				appTable = getTableStr(conditions, 1);
			}
			// String corps = null;
			// corps = parsePkcorps(pk_corps);
			// V35 add --begin
			if (scope > 0) {// 归属范围 非在职人员
				tableStrb.append(" from bd_psndoc ");
				tableStrb.append(" inner join bd_psnbasdoc on bd_psndoc.pk_psnbasdoc=bd_psnbasdoc.pk_psnbasdoc");
				tableStrb.append(" inner join bd_corp on bd_psndoc.pk_corp=bd_corp.pk_corp");
				tableStrb.append(" left outer join bd_deptdoc on bd_psndoc.pk_deptdoc=bd_deptdoc.pk_deptdoc");
				tableStrb.append(" inner join bd_psncl on bd_psndoc.pk_psncl=bd_psncl.pk_psncl");
				tableStrb.append(" left outer join om_job on bd_psndoc.pk_om_job=om_job.pk_om_job");
				tableStrb.append(" left outer join om_duty on bd_psndoc.dutyname=om_duty.pk_om_duty");
				// 组织条件
				where = " where 0=0 ";
				// if(corps!=null&& corps.length()>0){
				// where += " and bd_psndoc.pk_corp in " + corps + " " ;
				// }
				if (normalwheresql != null && !normalwheresql.trim().equalsIgnoreCase("")) {
					where += normalwheresql;
				}
				String cwhere = getWhere2(conditions, scope);
				if (cwhere != null && cwhere.length() > 0) {
					where += " and (" + cwhere + ")";
				}
				// 人员档案标志
				where += " and bd_psndoc.indocflag = '" + (indocflag ? "Y" : "N") + "'";
				// 归属范围标志
				where += " and bd_psndoc.psnclscope = " + scope;
				if (isCheckDeptPower) {
					// 附加部门权限
					powerSql = getDeptPowerSqls("bd_psndoc", userid, pk_corps[0], loginCorp, false);
					if (powerSql != null && powerSql.length() > 0) {
						where += " and (" + powerSql + ")";
					}// end if
					// 附加人员类别权限 V55 add
					String powerPsnclSql = getPsnclPowerSql("bd_psndoc", userid, loginCorp);
					if (powerPsnclSql != null && powerPsnclSql.length() > 0) {
						where += " and (" + powerPsnclSql + ")";
					}
				}// end if
			} else {// 在职人员
				tableStrb
				.append(" from hi_psndoc_deptchg inner join bd_psndoc on hi_psndoc_deptchg.pk_psndoc=bd_psndoc.pk_psndoc");// bd_psndoc
				tableStrb
				.append(" inner join bd_psnbasdoc on hi_psndoc_deptchg.pk_psnbasdoc=bd_psnbasdoc.pk_psnbasdoc");
				tableStrb.append(" inner join bd_corp on hi_psndoc_deptchg.pk_corp=bd_corp.pk_corp");
				tableStrb.append(" left outer join bd_deptdoc on hi_psndoc_deptchg.pk_deptdoc=bd_deptdoc.pk_deptdoc");
				tableStrb.append(" inner join bd_psncl on hi_psndoc_deptchg.pk_psncl=bd_psncl.pk_psncl");
				tableStrb.append(" left outer join om_job on hi_psndoc_deptchg.pk_postdoc=om_job.pk_om_job");
				tableStrb.append(" left outer join om_duty on hi_psndoc_deptchg.pk_om_duty=om_duty.pk_om_duty");

				where = " where hi_psndoc_deptchg.lastflag = 'Y' and hi_psndoc_deptchg.bendflag='N'";// hi_psndoc_deptchg.lastflag
				// = 'Y' and

				if (normalwheresql != null && !normalwheresql.trim().equalsIgnoreCase("")) {
					if (normalwheresql.indexOf("bd_psndoc.pk_deptdoc") > 0) {
						normalwheresql = StringUtil.replaceAllString(normalwheresql, "bd_psndoc.pk_deptdoc",
						"hi_psndoc_deptchg.pk_deptdoc");
					}
					where += normalwheresql;
				}
				if (jobtype >= 0) {
					where += " and hi_psndoc_deptchg.jobtype=" + jobtype + " ";
				}
				String cwhere = getWhere2(conditions, scope);
				if (cwhere != null && cwhere.length() > 0) {
					where += " and (" + cwhere + ")";
				}
				where += " and bd_psndoc.indocflag = '" + (indocflag ? "Y" : "N") + "'";
				where += " and (bd_psndoc.psnclscope = 0 or bd_psndoc.psnclscope = 5) and bd_psncl.psnclscope = "
					+ scope;
				if (isCheckDeptPower) {
					// 附加部门权限
					powerSql = getDeptPowerSqls("hi_psndoc_deptchg", userid, pk_corps[0], loginCorp, false);
					if (powerSql != null && powerSql.length() > 0) {
						where += " and (" + powerSql + ")";
					}// end if
					// 附加人员类别权限 V55 add
					String powerPsnclSql = getPsnclPowerSql("hi_psndoc_deptchg", userid, loginCorp);
					if (powerPsnclSql != null && powerPsnclSql.length() > 0) {
						where += " and (" + powerPsnclSql + ")";
					}
				}// end if
			}

			if (appTable != null && appTable.trim().length() > 0) {
				tableStrb.append(appTable);
			}
			String tableStr = tableStrb.toString();

			String order = null;
			if (scope > 0) {
				order = " order by bd_psndoc.showorder asc,bd_corp.unitcode asc,bd_deptdoc.deptcode asc,bd_psndoc.psncode asc,om_job.jobcode asc,bd_psncl.psnclasscode asc,om_duty.dutycode asc ";
			} else {
				order = " order by bd_psndoc.showorder asc,bd_corp.unitcode asc,bd_deptdoc.deptcode asc,bd_psndoc.psncode asc,om_job.jobcode asc,hi_psndoc_deptchg.jobtype asc,bd_psncl.psnclasscode asc,om_duty.dutycode asc ";
			}

			String select = "select ";
			if (tableStr.indexOf("inner join hi_psndoc") >= 0 || tableStr.indexOf("inner join v_hr_psndoc_edu") >= 0) {
				select += "distinct ";
			}
			select += " top " + recordcount;
			String sql = select + selfield + tableStr + where + order;
			PsnInfDMO dmo = new PsnInfDMO();
			vos = dmo.queryBySql(sql);
			vos = fomularShoworder(vos);// v50 add

		} catch (Exception e) {
			throwBusinessException(e);
		}
		return vos;
	}

	/**
	 * 维护节点查询人员信息。(过期)
	 * 
	 * @param indocflag
	 * @param pk_corps
	 * @param conditions
	 * @param scope
	 * @param userid
	 * @param jobtype
	 * @param listfield
	 * @param normalwheresql
	 * @param recordcount
	 * @return
	 * @throws BusinessException
	 */
	public GeneralVO[] queryByCondition(boolean indocflag, String[] pk_corps, ConditionVO[] conditions, int scope,
			String loginCorp, String userid, int jobtype, String listfield, String normalwheresql, int recordcount)
	throws BusinessException {
		GeneralVO[] vos = null;
		try {
			// 组织查询的列
			String selfield = null;
			// 根据人员归属范围处理显示列
			String fixedField = getFixedFiled(scope);// sql语句中的固定字段
			// 前台传回的显示列
			if (listfield == null || "".equalsIgnoreCase(listfield.trim())) {
				selfield = fixedField;
			} else {
				String[] field = listfield.split(",");
				if (field.length < 2) {
					selfield = fixedField + "," + listfield;
				} else {
					String tempselect = "";
					for (int i = 0; i < field.length; i++) {
						if (fixedField.indexOf(field[i]) > 0) {
							continue;
						} else {
							tempselect += "," + field[i];
						}
					}
					selfield = fixedField + tempselect;
				}
			}
			// 组织查询条件
			String where = null;
			// 组织查询关联表
			StringBuffer tableStrb = new StringBuffer();
			String powerSql = null;
			String appTable = "";
			if (scope == 0) {
				appTable = getTableStr(conditions, 0);
			} else {
				appTable = getTableStr(conditions, 1);
			}
			// String corps = null;
			// corps = parsePkcorps(pk_corps);
			// V35 add --begin
			if (scope > 0) {// 归属范围 非在职人员
				tableStrb.append(" from bd_psndoc ");
				tableStrb.append(" inner join bd_psnbasdoc on bd_psndoc.pk_psnbasdoc=bd_psnbasdoc.pk_psnbasdoc");
				tableStrb.append(" inner join bd_corp on bd_psndoc.pk_corp=bd_corp.pk_corp");
				tableStrb.append(" left outer join bd_deptdoc on bd_psndoc.pk_deptdoc=bd_deptdoc.pk_deptdoc");
				tableStrb.append(" inner join bd_psncl on bd_psndoc.pk_psncl=bd_psncl.pk_psncl");
				tableStrb.append(" left outer join om_job on bd_psndoc.pk_om_job=om_job.pk_om_job");
				tableStrb.append(" left outer join om_duty on bd_psndoc.dutyname=om_duty.pk_om_duty");
				// 组织条件
				where = " where 0=0 ";
				// if(corps!=null&& corps.length()>0){
				// where += " and bd_psndoc.pk_corp in " + corps + " " ;
				// }
				if (normalwheresql != null && !normalwheresql.trim().equalsIgnoreCase("")) {
					where += normalwheresql;
				}
				String cwhere = getWhere2(conditions, scope);
				if (cwhere != null && cwhere.length() > 0) {
					where += " and (" + cwhere + ")";
				}
				// 人员档案标志
				where += " and bd_psndoc.indocflag = '" + (indocflag ? "Y" : "N") + "'";
				// 归属范围标志
				where += " and bd_psndoc.psnclscope = " + scope;
				// 附加部门权限
				powerSql = getDeptPowerSqls("bd_psndoc", userid, pk_corps[0], loginCorp, false);
				if (powerSql != null && powerSql.length() > 0) {
					where += " and (" + powerSql + ")";
				}
				// 附加人员类别权限 V55 add
				String powerPsnclSql = getPsnclPowerSql("bd_psndoc", userid, loginCorp);
				if (powerPsnclSql != null && powerPsnclSql.length() > 0) {
					where += " and (" + powerPsnclSql + ")";
				}

			} else {// 在职人员
				tableStrb
				.append(" from hi_psndoc_deptchg inner join bd_psndoc on hi_psndoc_deptchg.pk_psndoc=bd_psndoc.pk_psndoc");// bd_psndoc
				tableStrb
				.append(" inner join bd_psnbasdoc on hi_psndoc_deptchg.pk_psnbasdoc=bd_psnbasdoc.pk_psnbasdoc");
				tableStrb.append(" inner join bd_corp on hi_psndoc_deptchg.pk_corp=bd_corp.pk_corp");
				tableStrb.append(" left outer join bd_deptdoc on hi_psndoc_deptchg.pk_deptdoc=bd_deptdoc.pk_deptdoc");
				tableStrb.append(" inner join bd_psncl on hi_psndoc_deptchg.pk_psncl=bd_psncl.pk_psncl");
				tableStrb.append(" left outer join om_job on hi_psndoc_deptchg.pk_postdoc=om_job.pk_om_job");
				tableStrb.append(" left outer join om_duty on hi_psndoc_deptchg.pk_om_duty=om_duty.pk_om_duty");

				where = " where hi_psndoc_deptchg.lastflag = 'Y' and hi_psndoc_deptchg.bendflag='N'";// hi_psndoc_deptchg.lastflag
				// = 'Y' and

				if (normalwheresql != null && !normalwheresql.trim().equalsIgnoreCase("")) {
					if (normalwheresql.indexOf("bd_psndoc.pk_deptdoc") > 0) {
						normalwheresql = StringUtil.replaceAllString(normalwheresql, "bd_psndoc.pk_deptdoc",
						"hi_psndoc_deptchg.pk_deptdoc");
					}
					where += normalwheresql;
				}
				if (jobtype >= 0) {
					where += " and hi_psndoc_deptchg.jobtype=" + jobtype + " ";
				}
				String cwhere = getWhere2(conditions, scope);
				if (cwhere != null && cwhere.length() > 0) {
					where += " and (" + cwhere + ")";
				}
				where += " and bd_psndoc.indocflag = '" + (indocflag ? "Y" : "N") + "'";
				where += " and (bd_psndoc.psnclscope = 0 or bd_psndoc.psnclscope = 5) and bd_psncl.psnclscope = "
					+ scope;
				// 附加部门权限
				powerSql = getDeptPowerSqls("hi_psndoc_deptchg", userid, pk_corps[0], loginCorp, false);
				if (powerSql != null && powerSql.length() > 0) {
					where += " and (" + powerSql + ")";
				}
				// 附加人员类别权限 V55 add
				String powerPsnclSql = getPsnclPowerSql("hi_psndoc_deptchg", userid, loginCorp);
				if (powerPsnclSql != null && powerPsnclSql.length() > 0) {
					where += " and (" + powerPsnclSql + ")";
				}
			}

			if (appTable != null && appTable.trim().length() > 0) {
				tableStrb.append(appTable);
			}
			String tableStr = tableStrb.toString();

			String order = null;
			if (scope > 0) {
				order = " order by bd_psndoc.showorder asc,bd_corp.unitcode asc,bd_deptdoc.deptcode asc,bd_psndoc.psncode asc,om_job.jobcode asc,bd_psncl.psnclasscode asc,om_duty.dutycode asc ";
			} else {
				order = " order by bd_psndoc.showorder asc,bd_corp.unitcode asc,bd_deptdoc.deptcode asc,bd_psndoc.psncode asc,om_job.jobcode asc,hi_psndoc_deptchg.jobtype asc,bd_psncl.psnclasscode asc,om_duty.dutycode asc ";
			}

			String select = "select ";
			if (tableStr.indexOf("inner join hi_psndoc") >= 0 || tableStr.indexOf("inner join v_hr_psndoc_edu") >= 0) {
				select += " distinct ";
			}

			// select += " top " + recordcount;
			String sql = select + selfield + tableStr + where + order;
			String transsql = " select top " + recordcount + " * from (" + sql + ") newtable";
			PsnInfDMO dmo = new PsnInfDMO();
			// vos = dmo.queryBySql(sql);
			vos = dmo.queryBySqlForQuery(transsql);
			vos = fomularShoworder(vos);// v50 add

		} catch (Exception e) {
			throwBusinessException(e);
		}
		return vos;
	}

	/**
	 * 维护节点的查询。
	 */
	public GeneralVO[] queryByCondition_Maintain(boolean indocflag, String pk_corp, String DLGwheresql,
			String[] DLGtables, int scope, String loginCorp, String userid, int jobtype, String listfield,
			String normalwheresql, int recordcount, BusinessFuncParser_sql funcParser, String orderbyclause,
			boolean includesubcorp) throws BusinessException {
		GeneralVO[] vos = null;
		try {
			// 组织查询的列
			String selfield = null;
			// 根据人员归属范围处理显示列
			String fixedField = getFixedFiled(scope);// sql语句中的固定字段
			// 前台传回的显示列
			if (listfield == null || "".equalsIgnoreCase(listfield.trim())) {
				selfield = fixedField;
			} else {
				String[] field = listfield.split(",");
				if (field.length < 2) {
					selfield = fixedField + "," + listfield;
				} else {
					String tempselect = "";
					for (int i = 0; i < field.length; i++) {
						if (fixedField.indexOf(field[i]) > 0) {
							continue;
						} else {
							// 考虑有空字段的情况
							if (field[i].length() > 1)
								tempselect += "," + field[i];
						}
					}
					selfield = fixedField + tempselect;
				}
			}
			// 组织查询条件
			String where = null;
			// 组织查询关联表
			StringBuffer tableStrb = new StringBuffer();
			String powerSql = null;
			String appTable = "";
			if (scope == 0) {
				replaceTableStr(DLGwheresql);
				appTable = getTableStr_newDLG(DLGtables, 0);
			} else {
				appTable = getTableStr_newDLG(DLGtables, 1);
			}
			DLGwheresql = GlobalTool.replaceDateFormulaSql(DLGwheresql, funcParser);
			if (scope > 0) {// 归属范围 非在职人员
				tableStrb.append(" from bd_psndoc ");
				tableStrb.append(" inner join bd_psnbasdoc on bd_psndoc.pk_psnbasdoc=bd_psnbasdoc.pk_psnbasdoc");
				tableStrb.append(" inner join bd_corp on bd_psndoc.pk_corp=bd_corp.pk_corp");
				//add by lq  取最大日期的那条人员------------
				tableStrb.append(" inner join (select b.id,max(p.ts) as ts from bd_psndoc p inner join bd_psnbasdoc b on  p.pk_psnbasdoc=b.pk_psnbasdoc group by b.id) c ");
				tableStrb.append(" on bd_psnbasdoc.id=c.id and bd_psndoc.ts=c.ts ");
				//add by lq  -----------------
				tableStrb.append(" left outer join bd_deptdoc on bd_psndoc.pk_deptdoc=bd_deptdoc.pk_deptdoc");
				tableStrb.append(" inner join bd_psncl on bd_psndoc.pk_psncl=bd_psncl.pk_psncl");
				tableStrb.append(" left outer join om_job on bd_psndoc.pk_om_job=om_job.pk_om_job");
				tableStrb.append(" left outer join om_duty on bd_psndoc.dutyname=om_duty.pk_om_duty");
				// 组织条件
				where = " where 0=0 ";
				// if(corps!=null&& corps.length()>0){
				// where += " and bd_psndoc.pk_corp in " + corps + " " ;
				// }
				if (normalwheresql != null && !normalwheresql.trim().equalsIgnoreCase("")) {
					where += normalwheresql;
				}
				String cwhere = DLGwheresql;
				if (cwhere != null && cwhere.length() > 0) {
					where += " and (" + cwhere + ")";
				}
				// 人员档案标志
				where += " and bd_psndoc.indocflag = '" + (indocflag ? "Y" : "N") + "'";

				//add by lq   离退返聘人员应显示在离退范围--------------
				if(scope==3){
					where+= " and (bd_psndoc.psnclscope = "+scope+"  or bd_psndoc.pk_psndoc in(select pk_psndoc from hi_psndoc_deptchg  where lastflag='Y' and isreturn='Y')) ";
				}else{
					// 归属范围标志
					where += " and bd_psndoc.psnclscope = " + scope;
				}
				//add by lq  ---------------------------------------

				// 附加部门权限
				powerSql = getDeptPowerSqls("bd_psndoc", userid, pk_corp, loginCorp, includesubcorp);
				if (powerSql != null && powerSql.length() > 0) {
					where += " and (" + powerSql + ")";
				}
				// 附加人员类别权限 V55 add
				String powerPsnclSql = getPsnclPowerSql("bd_psndoc", userid, loginCorp);
				if (powerPsnclSql != null && powerPsnclSql.length() > 0) {
					where += " and (" + powerPsnclSql + ")";
				}

			} else {// 在职人员
				tableStrb
				.append(" from hi_psndoc_deptchg inner join bd_psndoc on hi_psndoc_deptchg.pk_psndoc=bd_psndoc.pk_psndoc");// bd_psndoc
				tableStrb
				.append(" inner join bd_psnbasdoc on hi_psndoc_deptchg.pk_psnbasdoc=bd_psnbasdoc.pk_psnbasdoc");
				tableStrb.append(" inner join bd_corp on hi_psndoc_deptchg.pk_corp=bd_corp.pk_corp");
				tableStrb.append(" left outer join bd_deptdoc on hi_psndoc_deptchg.pk_deptdoc=bd_deptdoc.pk_deptdoc");
				tableStrb.append(" inner join bd_psncl on hi_psndoc_deptchg.pk_psncl=bd_psncl.pk_psncl");
				tableStrb.append(" left outer join om_job on hi_psndoc_deptchg.pk_postdoc=om_job.pk_om_job");
				tableStrb.append(" left outer join om_duty on hi_psndoc_deptchg.pk_om_duty=om_duty.pk_om_duty");

				where = " where hi_psndoc_deptchg.lastflag = 'Y' and hi_psndoc_deptchg.bendflag='N'";// hi_psndoc_deptchg.lastflag
				// = 'Y' and

				if (normalwheresql != null && !normalwheresql.trim().equalsIgnoreCase("")) {
					if (normalwheresql.indexOf("bd_psndoc.pk_deptdoc") > 0) {
						normalwheresql = StringUtil.replaceAllString(normalwheresql, "bd_psndoc.pk_deptdoc",
						"hi_psndoc_deptchg.pk_deptdoc");
					}
					where += normalwheresql;
				}
				if (jobtype >= 0) {
					where += " and hi_psndoc_deptchg.jobtype=" + jobtype + " ";
				}

				String cwhere = DLGwheresql;

				if (cwhere != null && cwhere.trim().length() > 0) {
					cwhere = cwhere.trim();
					if (cwhere.startsWith("and"))
						cwhere = cwhere.substring(4);
					where += " and (" + cwhere + ")";
				}
				where += " and bd_psndoc.indocflag = '" + (indocflag ? "Y" : "N") + "'";
				where += " and (bd_psndoc.psnclscope = 0 or bd_psndoc.psnclscope = 5) and bd_psncl.psnclscope = "
					+ scope;
				// 附加部门权限
				powerSql = getDeptPowerSqls("hi_psndoc_deptchg", userid, pk_corp, loginCorp, includesubcorp);
				if (powerSql != null && powerSql.length() > 0) {
					where += " and (" + powerSql + ")";
				}
				// 附加人员类别权限 V55 add
				String powerPsnclSql = getPsnclPowerSql("hi_psndoc_deptchg", userid, loginCorp);
				if (powerPsnclSql != null && powerPsnclSql.length() > 0) {
					where += " and (" + powerPsnclSql + ")";
				}
			}
			where += " and (bd_corp.ishasaccount = 'Y' ) and ( bd_corp.isseal is null or bd_corp.isseal <> 'Y' ) and (bd_corp.pk_corp in (select pk_corp from sm_createcorp where sm_createcorp.pk_corp = bd_corp.pk_corp and funccode = '6007'))";

			if (appTable != null && appTable.trim().length() > 0) {
				tableStrb.append(appTable);
			}
			String tableStr = tableStrb.toString();

			String select = "select distinct ";
			String temporder = adjustOrderByClause(orderbyclause);
			if (temporder.indexOf("bd_psndoc.psncode") < 0) {
				if (temporder.indexOf("order") > -1) {
					temporder = temporder + ",bd_psndoc.psncode";
				} else {
					temporder = " order by bd_corp_showorder,bd_corp.unitcode,bd_deptdoc_showorder,bd_deptdoc.deptcode,bd_psndoc.showorder,bd_psndoc.psncode";
				}
			}
			String sql = select + selfield + tableStr + where + temporder;
			String transsql = " select top " + recordcount + " * from (" + sql + ") newtable";
			PsnInfDMO dmo = new PsnInfDMO();

			// Logger.debug("人员信息维护节点查询的原始语句：  "+transsql);
			Logger.debug(NCLangResOnserver.getInstance().getStrByID("600700", "UPP600700-000373")/*
			 * @res
			 * "人员信息维护节点查询的原始语句：  "
			 */+ transsql);
			vos = dmo.queryBySqlForQuery(transsql);
			vos = fomularShoworder(vos);// v50 add
			// 如果是非全职人员，去掉不该看到的工作档案信息
			if (scope == 0 && (jobtype > 0 || jobtype == -1)) {
				Hashtable<String, String> fields_willbeclear = getFieldsofPsnDoc(selfield);
				vos = clearPsnDocInfoofVO(vos, fields_willbeclear);
			}
		} catch (Exception e) {
			throwBusinessException(e);
		}
		return vos;
	}

	/**
	 * 关键人员条件选择增加时的查询方法。
	 */
	public GeneralVO[] queryByCondition_KeyPerson(boolean indocflag, String pk_corp, String DLGwheresql,
			String[] DLGtables, int scope, String loginCorp, String userid, int jobtype, String listfield,
			String normalwheresql, int recordcount, BusinessFuncParser_sql funcParser, String orderbyclause,
			boolean includesubcorp) throws BusinessException {
		GeneralVO[] vos = null;
		try {
			// 组织查询的列
			String selfield = null;
			// 根据人员归属范围处理显示列
			String fixedField = getFixedFiled(scope);// sql语句中的固定字段
			// 前台传回的显示列
			if (listfield == null || "".equalsIgnoreCase(listfield.trim())) {
				selfield = fixedField;
			} else {
				String[] field = listfield.split(",");
				if (field.length < 2) {
					selfield = fixedField + "," + listfield;
				} else {
					String tempselect = "";
					for (int i = 0; i < field.length; i++) {
						if (fixedField.indexOf(field[i]) > 0) {
							continue;
						} else {
							// 考虑有空字段的情况
							if (field[i].length() > 1)
								tempselect += "," + field[i];
						}
					}
					selfield = fixedField + tempselect;
				}
			}
			// 组织查询条件
			String where = null;
			// 组织查询关联表
			StringBuffer tableStrb = new StringBuffer();
			String powerSql = null;
			String appTable = "";
			if (scope == 0) {
				replaceTableStr(DLGwheresql);
				appTable = getTableStr_newDLG(DLGtables, 0);
			} else {
				appTable = getTableStr_newDLG(DLGtables, 1);
			}
			DLGwheresql = GlobalTool.replaceDateFormulaSql(DLGwheresql, funcParser);
			if (scope > 0) {// 归属范围 非在职人员
				tableStrb.append(" from bd_psndoc ");
				tableStrb.append(" inner join bd_psnbasdoc on bd_psndoc.pk_psnbasdoc=bd_psnbasdoc.pk_psnbasdoc");
				tableStrb.append(" inner join bd_corp on bd_psndoc.pk_corp=bd_corp.pk_corp");
				tableStrb.append(" left outer join bd_deptdoc on bd_psndoc.pk_deptdoc=bd_deptdoc.pk_deptdoc");
				tableStrb.append(" inner join bd_psncl on bd_psndoc.pk_psncl=bd_psncl.pk_psncl");
				tableStrb.append(" left outer join om_job on bd_psndoc.pk_om_job=om_job.pk_om_job");
				tableStrb.append(" left outer join om_duty on bd_psndoc.dutyname=om_duty.pk_om_duty");
				// 组织条件
				where = " where 0=0 ";
				// if(corps!=null&& corps.length()>0){
				// where += " and bd_psndoc.pk_corp in " + corps + " " ;
				// }
				if (normalwheresql != null && !normalwheresql.trim().equalsIgnoreCase("")) {
					where += normalwheresql;
				}
				String cwhere = DLGwheresql;
				if (cwhere != null && cwhere.length() > 0) {
					where += " and (" + cwhere + ")";
				}
				// 人员档案标志
				where += " and bd_psndoc.indocflag = '" + (indocflag ? "Y" : "N") + "'";
				// 归属范围标志
				where += " and bd_psndoc.psnclscope = " + scope;
				// 附加部门权限
				powerSql = getDeptPowerSqls("bd_psndoc", userid, pk_corp, loginCorp, includesubcorp);
				if (powerSql != null && powerSql.length() > 0) {
					where += " and (" + powerSql + ")";
				}
				// 附加人员类别权限 V55 add
				String powerPsnclSql = getPsnclPowerSql("bd_psndoc", userid, loginCorp);
				if (powerPsnclSql != null && powerPsnclSql.length() > 0) {
					where += " and (" + powerPsnclSql + ")";
				}

			} else {// 在职人员
				tableStrb
				.append(" from hi_psndoc_deptchg inner join bd_psndoc on hi_psndoc_deptchg.pk_psndoc=bd_psndoc.pk_psndoc");// bd_psndoc
				tableStrb
				.append(" inner join bd_psnbasdoc on hi_psndoc_deptchg.pk_psnbasdoc=bd_psnbasdoc.pk_psnbasdoc");
				tableStrb.append(" inner join bd_corp on hi_psndoc_deptchg.pk_corp=bd_corp.pk_corp");
				tableStrb.append(" left outer join bd_deptdoc on hi_psndoc_deptchg.pk_deptdoc=bd_deptdoc.pk_deptdoc");
				tableStrb.append(" inner join bd_psncl on hi_psndoc_deptchg.pk_psncl=bd_psncl.pk_psncl");
				tableStrb.append(" left outer join om_job on hi_psndoc_deptchg.pk_postdoc=om_job.pk_om_job");
				tableStrb.append(" left outer join om_duty on hi_psndoc_deptchg.pk_om_duty=om_duty.pk_om_duty");

				where = " where hi_psndoc_deptchg.lastflag = 'Y' and hi_psndoc_deptchg.bendflag='N'";// hi_psndoc_deptchg.lastflag
				// = 'Y' and

				if (normalwheresql != null && !normalwheresql.trim().equalsIgnoreCase("")) {
					if (normalwheresql.indexOf("bd_psndoc.pk_deptdoc") > 0) {
						normalwheresql = StringUtil.replaceAllString(normalwheresql, "bd_psndoc.pk_deptdoc",
						"hi_psndoc_deptchg.pk_deptdoc");
					}
					where += normalwheresql;
				}
				if (jobtype >= 0) {
					where += " and hi_psndoc_deptchg.jobtype=" + jobtype + " ";
				}
				String cwhere = DLGwheresql;
				if (cwhere != null && cwhere.length() > 0) {
					where += " and (" + cwhere + ")";
				}
				where += " and bd_psndoc.indocflag = '" + (indocflag ? "Y" : "N") + "'";
				where += " and (bd_psndoc.psnclscope = 0 or bd_psndoc.psnclscope = 5) and bd_psncl.psnclscope = "
					+ scope;
				// 附加部门权限
				powerSql = getDeptPowerSqls("hi_psndoc_deptchg", userid, pk_corp, loginCorp, includesubcorp);
				if (powerSql != null && powerSql.length() > 0) {
					where += " and (" + powerSql + ")";
				}
				// 附加人员类别权限 V55 add
				String powerPsnclSql = getPsnclPowerSql("hi_psndoc_deptchg", userid, loginCorp);
				if (powerPsnclSql != null && powerPsnclSql.length() > 0) {
					where += " and (" + powerPsnclSql + ")";
				}
			}
			where += " and (bd_corp.ishasaccount = 'Y' ) and ( bd_corp.isseal is null or bd_corp.isseal <> 'Y' ) and (bd_corp.pk_corp in (select pk_corp from sm_createcorp where sm_createcorp.pk_corp = bd_corp.pk_corp and funccode = '6007'))";

			if (appTable != null && appTable.trim().length() > 0) {
				tableStrb.append(appTable);
			}
			String tableStr = tableStrb.toString();

			String select = "select distinct ";
			String temporder = adjustOrderByClause(orderbyclause);
			if (temporder.indexOf("bd_psndoc.psncode") < 0) {
				if (temporder.indexOf("order") > -1) {
					temporder = temporder + ",bd_psndoc.psncode";
				} else {
					temporder = " order by bd_corp_showorder,bd_corp.unitcode,bd_deptdoc_showorder,bd_deptdoc.deptcode,bd_psndoc.showorder,bd_psndoc.psncode";
				}
			}
			String sql = select + selfield + tableStr + where + temporder;
			String transsql = " select top " + recordcount + " * from (" + sql + ") newtable";
			PsnInfDMO dmo = new PsnInfDMO();
			vos = dmo.queryBySqlForQuery(transsql);
			vos = fomularShoworder(vos);// v50 add
			// 如果是非全职人员，去掉不该看到的工作档案信息
			if (scope == 0 && (jobtype > 0 || jobtype == -1)) {
				Hashtable<String, String> fields_willbeclear = getFieldsofPsnDoc(selfield);
				vos = clearPsnDocInfoofVO(vos, fields_willbeclear);
			}
		} catch (Exception e) {
			throwBusinessException(e);
		}
		return vos;
	}

	/**
	 * 返回所有bd_psndoc的字段。
	 * 
	 * @param allfields
	 * @return
	 */
	private Hashtable<String, String> getFieldsofPsnDoc(String allfields) {
		Hashtable<String, String> ht_fields = new Hashtable<String, String>();
		String fields[] = allfields.trim().split(",");
		for (String field : fields) {
			if (field != null && field.trim().startsWith("bd_psndoc.")) {
				ht_fields.put(field.trim().substring(10), field);
			}
		}
		return ht_fields;
	}

	/**
	 * 隐藏非全职人员的工作信息
	 * 
	 * @param vos
	 * @return
	 */
	private GeneralVO[] clearPsnDocInfoofVO(GeneralVO[] vos, Hashtable<String, String> ht_psndocfields) {
		if (vos == null || vos.length < 1)
			return vos;
		String fieldnames[] = vos[0].getAttributeNames();
		if (fieldnames == null || fieldnames.length < 1)
			return vos;

		for (int i = 0, j = vos.length - 1; i <= j; i++) {
			// 如果此人是非“全职”，并且兼职公司非主职公司，则隐藏工作信息
			if (((Integer) vos[i].getAttributeValue("jobtypeflag")).intValue() > 0
					&& vos[i].getAttributeValue("pk_corp") != null
					&& !vos[i].getAttributeValue("pk_corp").equals(vos[i].getAttributeValue("belong_pk_corp"))) {
				for (String fieldname : fieldnames) {
					if (!ht_psndocfields.containsKey(fieldname))
						continue;
					if (!(fieldname.equals("psncode") || fieldname.equals("psnname") || fieldname.equals("pk_deptdoc")
							|| fieldname.equals("pk_psncl") || fieldname.equals("pk_om_job")
							|| fieldname.equals("pk_om_duty") || fieldname.equals("series")
							|| fieldname.equals("jobrank") || fieldname.equals("jobseries")
							|| fieldname.equals("pk_corp") || fieldname.equals("belong_pk_corp")
							|| fieldname.equals("pk_psnbasdoc") || fieldname.equals("pk_psndoc"))) {
						vos[i].setFieldValue(fieldname, null);
					}
				}

			}
		}
		return vos;
	}

	/**
	 * 调整排序字段
	 * 
	 * @param orderbyclause
	 * @return
	 */
	public static String adjustOrderByClause(String orderbyclause) {
		if (orderbyclause == null || orderbyclause.length() < 1) {
			orderbyclause = " order by bd_corp.unitcode,bd_deptdoc.deptcode,bd_psndoc.psncode";
		} else {
			if (orderbyclause.indexOf("bd_corp.showorder") != -1) {
				if (orderbyclause.indexOf("bd_corp.unitcode") == -1) {
					orderbyclause = orderbyclause.replaceFirst("bd_corp.showorder",
					"bd_corp.showorder,bd_corp.unitcode");
				}
			}
			if (orderbyclause.indexOf("bd_deptdoc.showorder") != -1) {
				if (orderbyclause.indexOf("bd_deptdoc.unitcode") == -1) {
					orderbyclause = orderbyclause.replaceFirst("bd_deptdoc.showorder",
					"bd_deptdoc.showorder,bd_deptdoc.deptcode");
				}
			}

			// 不再加此字段了，因为select 中不一定有此字段，select distinct时会报错。
			// if(orderbyclause.indexOf("bd_psndoc.psncode") <0) orderbyclause += ",bd_psndoc.psncode";
		}
		return orderbyclause == null ? "" : orderbyclause;
	}

	/**
	 * 培训模块所使用的查询。
	 */
	public GeneralVO[] queryByCondition_Train(boolean indocflag, String[] pk_corps, String DLGwheresql,
			String[] DLGtables, String loginCorp, String userid, String listfield, String normalwheresql,
			int recordcount, BusinessFuncParser_sql funcParser, String ordrebyclause, boolean isDeptPower)
	throws BusinessException {

		GeneralVO[] vos = null;
		try {
			String wheresql_dept = "";
			if (isDeptPower) {// 增加部门权限
				TempTableUtils util = new TempTableUtils(false);
				wheresql_dept = " and bd_deptdoc.pk_deptdoc in ("
					+ util.getDeptPowerForGroup(userid, InvocationInfoProxy.getInstance().getUserDataSource())
					+ ") ";
			}
			normalwheresql = normalwheresql + wheresql_dept;

			// sql语句中的固定字段
			String fixedField = getFixedFiled(1);
			String selfield = null;
			if (listfield == null || "".equalsIgnoreCase(listfield.trim())) {
				selfield = fixedField;
			} else {
				String[] field = listfield.split(",");
				if (field.length < 2) {
					selfield = fixedField + "," + listfield;
				} else {
					String tempselect = "";
					for (int i = 0; i < field.length; i++) {
						if (fixedField.indexOf(field[i]) > 0) {
							continue;
						} else {
							// 考虑有空字段的情况
							if (field[i].length() > 1)
								tempselect += "," + field[i];

						}
					}
					selfield = fixedField + tempselect;
				}

			}
			// 
			DLGwheresql = GlobalTool.replaceDateFormulaSql(DLGwheresql, funcParser);
			String appTable = getTableStr_newDLG(DLGtables, 1);
			StringBuffer buf = new StringBuffer();
			buf.append(" from bd_psndoc ");
			buf.append(" inner join bd_psnbasdoc on bd_psndoc.pk_psnbasdoc=bd_psnbasdoc.pk_psnbasdoc");
			buf.append(" inner join bd_corp on bd_psndoc.pk_corp=bd_corp.pk_corp");
			buf.append(" left outer join bd_deptdoc on bd_psndoc.pk_deptdoc=bd_deptdoc.pk_deptdoc");
			buf.append(" inner join bd_psncl on bd_psndoc.pk_psncl=bd_psncl.pk_psncl");
			buf.append(" left outer join om_job on bd_psndoc.pk_om_job=om_job.pk_om_job");
			buf.append(" left outer join om_duty on bd_psndoc.dutyname=om_duty.pk_om_duty");
			String where = " where 0=0 ";
			// v50 add
			String corps = parsePkcorps(pk_corps);
			if (corps != null && corps.length() > 0) {
				where += " and bd_psndoc.pk_corp in " + corps + " ";
			}
			if (normalwheresql != null && !normalwheresql.trim().equalsIgnoreCase("")) {
				where += normalwheresql;
			}
			String cwhere = DLGwheresql;
			if (cwhere != null && cwhere.length() > 0) {
				where += " and (" + cwhere + ")";
			}
			where += " and bd_psndoc.indocflag = '" + (indocflag ? "Y" : "N") + "'";
			// 附加部门权限 v50 add
			String powerSql = getDeptPowerSqls("bd_psndoc", userid, pk_corps.length > 0 ? pk_corps[0] : null,
					loginCorp, true);
			if (powerSql != null && powerSql.length() > 0) {
				where += " and (" + powerSql + ")";
			}
			// 附加人员类别权限 V55 add
			String powerPsnclSql = getPsnclPowerSql("bd_psndoc", userid, loginCorp);
			if (powerPsnclSql != null && powerPsnclSql.length() > 0) {
				where += " and (" + powerPsnclSql + ")";
			}
			if (where.indexOf("bd_psnbasdoc.approveflag") < 0) {
				where += " and (bd_psnbasdoc.approveflag in (0,1) or bd_psnbasdoc.approveflag is null) ";
			}

			String select = " select distinct ";

			if (appTable != null && appTable.trim().length() > 0) {
				buf.append(appTable);
			}
			String tableStr = buf.toString();
			// if (ordrebyclause == null||ordrebyclause.length()<1) {
			// ordrebyclause = " order by bd_psndoc.showorder asc,bd_corp.unitcode asc,bd_deptdoc.deptcode asc,"
			// + "bd_psndoc.psncode asc,om_job.jobcode asc,bd_psncl.psnclasscode asc,om_duty.dutycode asc ";
			// }
			if (ordrebyclause == null || ordrebyclause.length() < 1) {
				ordrebyclause = " order by bd_corp.unitcode,bd_deptdoc.deptcode,bd_psndoc.psncode";
			} else {
				ordrebyclause += " , bd_corp.unitcode,bd_deptdoc.deptcode,bd_psndoc.psncode";
			}
			String sql = select + selfield + tableStr + where + ordrebyclause;
			String transsql = " select top " + recordcount + " * from (" + sql + ") newtable";
			PsnInfDMO dmo = new PsnInfDMO();
			// vos = dmo.queryBySql(sql);
			vos = dmo.queryBySqlForQuery(transsql);
			vos = fomularShoworder(vos);// v50 add
		} catch (Exception e) {
			throwBusinessException(e);
		}
		return vos;

	}

	/**
	 * 采集节点查询人员信息。（过期）
	 * 
	 * @param indocflag
	 * @param pk_corp
	 * @param conditions
	 * @param userid
	 * @param listfield
	 * @param normalwheresql
	 * @param recordcount
	 * @return
	 * @throws BusinessException
	 */
	public GeneralVO[] queryByCondition(boolean indocflag, String[] pk_corps, ConditionVO[] conditions,
			String loginCorp, String userid, String listfield, String normalwheresql, int recordcount)
	throws BusinessException {
		GeneralVO[] vos = null;
		try {
			// sql语句中的固定字段
			String fixedField = getFixedFiled(1);
			String selfield = null;
			if (listfield == null || "".equalsIgnoreCase(listfield.trim())) {
				selfield = fixedField;
			} else {
				String[] field = listfield.split(",");
				if (field.length < 2) {
					selfield = fixedField + "," + listfield;
				} else {
					String tempselect = "";
					for (int i = 0; i < field.length; i++) {
						if (fixedField.indexOf(field[i]) > 0) {
							continue;
						} else {
							tempselect += "," + field[i];
						}
					}
					selfield = fixedField + tempselect;
				}

			}
			String appTable = getTableStr(conditions, 1);
			StringBuffer buf = new StringBuffer();
			buf.append(" from bd_psndoc ");
			buf.append(" inner join bd_psnbasdoc on bd_psndoc.pk_psnbasdoc=bd_psnbasdoc.pk_psnbasdoc");
			buf.append(" inner join bd_corp on bd_psndoc.pk_corp=bd_corp.pk_corp");
			buf.append(" left outer join bd_deptdoc on bd_psndoc.pk_deptdoc=bd_deptdoc.pk_deptdoc");
			buf.append(" inner join bd_psncl on bd_psndoc.pk_psncl=bd_psncl.pk_psncl");
			buf.append(" left outer join om_job on bd_psndoc.pk_om_job=om_job.pk_om_job");
			buf.append(" left outer join om_duty on bd_psndoc.dutyname=om_duty.pk_om_duty");
			String where = " where 0=0 ";
			// v50 add
			String corps = parsePkcorps(pk_corps);
			if (corps != null && corps.length() > 0) {
				where += " and bd_psndoc.pk_corp in " + corps + " ";
			}
			if (normalwheresql != null && !normalwheresql.trim().equalsIgnoreCase("")) {
				where += normalwheresql;
			}
			String cwhere = getWhere(conditions);
			if (cwhere != null && cwhere.length() > 0) {
				where += " and (" + cwhere + ")";
			}
			where += " and bd_psndoc.indocflag = '" + (indocflag ? "Y" : "N") + "'";
			// 附加部门权限 v50 add
			String powerSql = getDeptPowerSqls("bd_psndoc", userid, pk_corps.length > 0 ? pk_corps[0] : null,
					loginCorp, false);
			if (powerSql != null && powerSql.length() > 0) {
				where += " and (" + powerSql + ")";
			}
			// 附加人员类别权限 V55 add
			String powerPsnclSql = getPsnclPowerSql("bd_psndoc", userid, loginCorp);
			if (powerPsnclSql != null && powerPsnclSql.length() > 0) {
				where += " and (" + powerPsnclSql + ")";
			}
			if (where.indexOf("bd_psnbasdoc.approveflag") < 0) {
				where += " and (bd_psnbasdoc.approveflag in (0,1) or bd_psnbasdoc.approveflag is null) ";
			}

			String select = " select distinct ";

			if (appTable != null && appTable.trim().length() > 0) {
				buf.append(appTable);
			}
			String tableStr = buf.toString();
			String order = " order by bd_psndoc.showorder asc,bd_corp.unitcode asc,bd_deptdoc.deptcode asc,"
				+ "bd_psndoc.psncode asc,om_job.jobcode asc,bd_psncl.psnclasscode asc,om_duty.dutycode asc ";

			String sql = select + selfield + tableStr + where + order;
			String transsql = " select top " + recordcount + " * from (" + sql + ") newtable";
			PsnInfDMO dmo = new PsnInfDMO();
			// vos = dmo.queryBySql(sql);
			vos = dmo.queryBySqlForQuery(transsql);
			vos = fomularShoworder(vos);// v50 add
		} catch (Exception e) {
			throwBusinessException(e);
		}
		return vos;
	}

	/**
	 * 采集节点查询人员信息。
	 * 
	 * @param indocflag
	 * @param pk_corp
	 * @param conditions
	 * @param userid
	 * @param listfield
	 * @param normalwheresql
	 * @param recordcount
	 * @return
	 * @throws BusinessException
	 */
	public GeneralVO[] queryByCondition_Collect(boolean indocflag, String pk_corp, String DLGwheresql,
			String[] DLGtables, String loginCorp, String userid, String listfield, String normalwheresql,
			int recordcount, BusinessFuncParser_sql funcParser, String ordrebyclause, boolean includesubcorp)
	throws BusinessException {
		GeneralVO[] vos = null;
		try {
			// sql语句中的固定字段
			String fixedField = getFixedFiled(1);
			String selfield = null;
			if (listfield == null || "".equalsIgnoreCase(listfield.trim())) {
				selfield = fixedField;
			} else {
				String[] field = listfield.split(",");
				if (field.length < 2) {
					selfield = fixedField + "," + listfield;
				} else {
					String tempselect = "";
					for (int i = 0; i < field.length; i++) {
						if (fixedField.indexOf(field[i]) > 0) {
							continue;
						} else {
							// 考虑有空字段的情况
							if (field[i].length() > 1)
								tempselect += "," + field[i];

						}
					}
					selfield = fixedField + tempselect;
				}

			}
			// 
			DLGwheresql = GlobalTool.replaceDateFormulaSql(DLGwheresql, funcParser);
			String appTable = getTableStr_newDLG(DLGtables, 1);
			StringBuffer buf = new StringBuffer();
			buf.append(" from bd_psndoc ");
			buf.append(" inner join bd_psnbasdoc on bd_psndoc.pk_psnbasdoc=bd_psnbasdoc.pk_psnbasdoc");
			buf.append(" inner join bd_corp on bd_psndoc.pk_corp=bd_corp.pk_corp");
			buf.append(" left outer join bd_deptdoc on bd_psndoc.pk_deptdoc=bd_deptdoc.pk_deptdoc");
			buf.append(" inner join bd_psncl on bd_psndoc.pk_psncl=bd_psncl.pk_psncl");
			buf.append(" left outer join om_job on bd_psndoc.pk_om_job=om_job.pk_om_job");
			buf.append(" left outer join om_duty on bd_psndoc.dutyname=om_duty.pk_om_duty");
			String where = " where 0=0 ";

			if (normalwheresql != null && !normalwheresql.trim().equalsIgnoreCase("")) {
				where += normalwheresql;
			}
			String cwhere = DLGwheresql;
			if (cwhere != null && cwhere.length() > 0) {
				where += " and (" + cwhere + ")";
			}
			where += " and bd_psndoc.indocflag = '" + (indocflag ? "Y" : "N") + "'";
			// 附加部门权限 v50 add
			String powerSql = getDeptPowerSqls("bd_psndoc", userid, pk_corp, loginCorp, includesubcorp);
			if (powerSql != null && powerSql.length() > 0) {
				where += " and (" + powerSql + ")";
			}
			// 附加人员类别权限 V55 add
			String powerPsnclSql = getPsnclPowerSql("bd_psndoc", userid, loginCorp);
			if (powerPsnclSql != null && powerPsnclSql.length() > 0) {
				where += " and (" + powerPsnclSql + ")";
			}
			if (where.indexOf("bd_psnbasdoc.approveflag") < 0) {
				where += " and (bd_psnbasdoc.approveflag in (0,1) or bd_psnbasdoc.approveflag is null) ";
			}

			String select = " select distinct ";

			if (appTable != null && appTable.trim().length() > 0) {
				buf.append(appTable);
			}
			String tableStr = buf.toString();
			String temporder = adjustOrderByClause(ordrebyclause);
			if (temporder.indexOf("bd_psndoc.psncode") < 0) {
				if (temporder.indexOf("order") > -1) {
					temporder = temporder + ",bd_psndoc.psncode";
				} else {
					temporder = " order by bd_psndoc.psncode";
				}
			}
			String sql = select + selfield + tableStr + where + temporder;
			String transsql = " select top " + recordcount + " * from (" + sql + ") newtable";
			PsnInfDMO dmo = new PsnInfDMO();
			// vos = dmo.queryBySql(sql);
			vos = dmo.queryBySqlForQuery(transsql);
			vos = fomularShoworder(vos);// v50 add
		} catch (Exception e) {
			throwBusinessException(e);
		}
		return vos;
	}

	/**
	 * 采集节点查询人员记录数（过期）
	 * 
	 * @param indocflag
	 * @param pk_corps
	 * @param conditions
	 * @param userid
	 * @param normalwheresql
	 * @return
	 * @throws BusinessException
	 */
	public int queryRecordCountByCondition(boolean indocflag, String[] pk_corps, ConditionVO[] conditions,
			String loginCorp, String userid, String normalwheresql) throws BusinessException {
		int result = -1;
		try {

			StringBuffer buf = new StringBuffer();
			String appTable = getTableStr(conditions, 1);
			buf.append(" from bd_psndoc ");
			buf.append(" inner join bd_psnbasdoc on bd_psndoc.pk_psnbasdoc=bd_psnbasdoc.pk_psnbasdoc");
			buf.append(" inner join bd_corp on bd_psndoc.pk_corp=bd_corp.pk_corp");
			buf.append(" left outer join bd_deptdoc on bd_psndoc.pk_deptdoc=bd_deptdoc.pk_deptdoc");
			buf.append(" inner join bd_psncl on bd_psndoc.pk_psncl=bd_psncl.pk_psncl");
			buf.append(" left outer join om_job on bd_psndoc.pk_om_job=om_job.pk_om_job");
			buf.append(" left outer join om_duty on bd_psndoc.dutyname=om_duty.pk_om_duty");
			String where = " where 0=0 ";
			// v50 add
			String corps = parsePkcorps(pk_corps);
			if (corps != null && corps.length() > 0) {
				where += " and bd_psndoc.pk_corp in " + corps + " ";
			}
			if (normalwheresql != null && !normalwheresql.trim().equalsIgnoreCase("")) {
				where += normalwheresql;
			}
			String cwhere = getWhere(conditions);
			if (cwhere != null && cwhere.length() > 0) {
				where += " and (" + cwhere + ")";
			}
			where += " and bd_psndoc.indocflag = '" + (indocflag ? "Y" : "N") + "'";
			// 附加部门权限 v50 add
			String powerSql = getDeptPowerSqls("bd_psndoc", userid, pk_corps.length > 0 ? pk_corps[0] : null,
					loginCorp, false);
			if (powerSql != null && powerSql.length() > 0) {
				where += " and (" + powerSql + ")";
			}
			// 附加人员类别权限 V55 add
			String powerPsnclSql = getPsnclPowerSql("bd_psndoc", userid, loginCorp);
			if (powerPsnclSql != null && powerPsnclSql.length() > 0) {
				where += " and (" + powerPsnclSql + ")";
			}
			if (where.indexOf("bd_psnbasdoc.approveflag") < 0) {
				where += " and (bd_psnbasdoc.approveflag in (0,1) or bd_psnbasdoc.approveflag is null) ";
			}
			String select = " select count(distinct bd_psndoc.pk_psndoc) ";

			if (appTable != null && appTable.trim().length() > 0) {
				buf.append(appTable);
			}
			String sql = select + buf.toString() + where;
			PsnInfDMO dmo = new PsnInfDMO();
			result = dmo.queryRecordCountBySql(sql);
		} catch (Exception e) {
			throwBusinessException(e);
		}
		return result;
	}

	/**
	 * 采集节点查询人员记录数
	 * 
	 * @param indocflag
	 * @param pk_corps
	 * @param conditions
	 * @param userid
	 * @param normalwheresql
	 * @return
	 * @throws BusinessException
	 */
	public int queryRecordCountByCondition_Collect(boolean indocflag, String pk_corp, String DLGwheresql,
			String[] DLGtables, String loginCorp, String userid, String normalwheresql,
			BusinessFuncParser_sql funcParser, boolean includesubcorp) throws BusinessException {
		int result = -1;
		try {

			StringBuffer buf = new StringBuffer();
			String appTable = getTableStr_newDLG(DLGtables, 1);
			DLGwheresql = GlobalTool.replaceDateFormulaSql(DLGwheresql, funcParser);
			buf.append(" from bd_psndoc ");
			buf.append(" inner join bd_psnbasdoc on bd_psndoc.pk_psnbasdoc=bd_psnbasdoc.pk_psnbasdoc");
			buf.append(" inner join bd_corp on bd_psndoc.pk_corp=bd_corp.pk_corp");
			buf.append(" left outer join bd_deptdoc on bd_psndoc.pk_deptdoc=bd_deptdoc.pk_deptdoc");
			buf.append(" inner join bd_psncl on bd_psndoc.pk_psncl=bd_psncl.pk_psncl");
			buf.append(" left outer join om_job on bd_psndoc.pk_om_job=om_job.pk_om_job");
			buf.append(" left outer join om_duty on bd_psndoc.dutyname=om_duty.pk_om_duty");
			String where = " where 0=0 ";
			if (normalwheresql != null && !normalwheresql.trim().equalsIgnoreCase("")) {
				where += normalwheresql;
			}
			String cwhere = DLGwheresql;
			if (cwhere != null && cwhere.length() > 0) {
				where += " and (" + cwhere + ")";
			}
			where += " and bd_psndoc.indocflag = '" + (indocflag ? "Y" : "N") + "'";
			// 附加部门权限 v50 add
			String powerSql = getDeptPowerSqls("bd_psndoc", userid, pk_corp, loginCorp, includesubcorp);
			if (powerSql != null && powerSql.length() > 0) {
				where += " and (" + powerSql + ")";
			}
			// 附加人员类别权限 V55 add
			String powerPsnclSql = getPsnclPowerSql("bd_psndoc", userid, loginCorp);
			if (powerPsnclSql != null && powerPsnclSql.length() > 0) {
				where += " and (" + powerPsnclSql + ")";
			}
			if (where.indexOf("bd_psnbasdoc.approveflag") < 0) {
				where += " and (bd_psnbasdoc.approveflag in (0,1) or bd_psnbasdoc.approveflag is null) ";
			}
			String select = " select count(distinct bd_psndoc.pk_psndoc) ";

			if (appTable != null && appTable.trim().length() > 0) {
				buf.append(appTable);
			}
			String sql = select + buf.toString() + where;
			PsnInfDMO dmo = new PsnInfDMO();
			result = dmo.queryRecordCountBySql(sql);
		} catch (Exception e) {
			throwBusinessException(e);
		}
		return result;
	}

	/**
	 * 维护节点查询人员记录数
	 * 
	 * @param indocflag
	 * @param pk_corps
	 * @param DLGwheresql
	 * @param DLGtables
	 * @param loginCorp
	 * @param userid
	 * @param normalwheresql
	 * @return
	 * @throws BusinessException
	 */
	public int queryRecordCountByCondition_Maintain(boolean indocflag, String pk_corp, String DLGwheresql,
			String[] DLGtables, int scope, String loginCorp, String userid, int jobtype, String normalwheresql,
			BusinessFuncParser_sql funcParser, boolean includesubcorp) throws BusinessException {
		int result = -1;
		try {
			String where = null;
			StringBuffer tableStrb = new StringBuffer();
			String powerSql = null;
			String appTable = "";
			if (scope == 0) {
				replaceTableStr(DLGwheresql);
				appTable = getTableStr_newDLG(DLGtables, 0);
			} else {
				appTable = getTableStr_newDLG(DLGtables, 1);
			}
			// String corps = null;
			// corps = parsePkcorps(pk_corps);
			// 根据传递的条件，来拼写可直接使用的条件语句
			DLGwheresql = GlobalTool.replaceDateFormulaSql(DLGwheresql, funcParser);
			if (scope > 0) {// 归属范围
				tableStrb.append(" from bd_psndoc ");
				tableStrb.append(" inner join bd_psnbasdoc on bd_psndoc.pk_psnbasdoc=bd_psnbasdoc.pk_psnbasdoc");
				tableStrb.append(" inner join bd_corp on bd_psndoc.pk_corp=bd_corp.pk_corp");
				tableStrb.append(" left outer join bd_deptdoc on bd_psndoc.pk_deptdoc=bd_deptdoc.pk_deptdoc");
				tableStrb.append(" inner join bd_psncl on bd_psndoc.pk_psncl=bd_psncl.pk_psncl");
				tableStrb.append(" left outer join om_job on bd_psndoc.pk_om_job=om_job.pk_om_job");
				tableStrb.append(" left outer join om_duty on bd_psndoc.dutyname=om_duty.pk_om_duty");
				// 组织条件
				where = " where 0=0 ";
				if (normalwheresql != null && !normalwheresql.trim().equalsIgnoreCase("")) {
					where += normalwheresql;
				}
				String cwhere = DLGwheresql;
				if (cwhere != null && cwhere.length() > 0) {
					where += " and (" + cwhere + ")";
				}
				where += " and bd_psndoc.indocflag = '" + (indocflag ? "Y" : "N") + "'";
				where += " and bd_psndoc.psnclscope = " + scope;

				// 附加部门权限
				powerSql = getDeptPowerSqls("bd_psndoc", userid, pk_corp, loginCorp, includesubcorp);
				if (powerSql != null && powerSql.length() > 0) {
					where += " and (" + powerSql + ")";
				}
				// 附加人员类别权限 V55 add
				String powerPsnclSql = getPsnclPowerSql("bd_psndoc", userid, loginCorp);
				if (powerPsnclSql != null && powerPsnclSql.length() > 0) {
					where += " and (" + powerPsnclSql + ")";
				}
			} else {
				tableStrb
				.append(" from hi_psndoc_deptchg inner join bd_psndoc on hi_psndoc_deptchg.pk_psndoc=bd_psndoc.pk_psndoc");// bd_psndoc
				tableStrb
				.append(" inner join bd_psnbasdoc on hi_psndoc_deptchg.pk_psnbasdoc=bd_psnbasdoc.pk_psnbasdoc");
				tableStrb.append(" inner join bd_corp on hi_psndoc_deptchg.pk_corp=bd_corp.pk_corp");
				tableStrb.append(" left outer join bd_deptdoc on hi_psndoc_deptchg.pk_deptdoc=bd_deptdoc.pk_deptdoc");
				tableStrb.append(" inner join bd_psncl on hi_psndoc_deptchg.pk_psncl=bd_psncl.pk_psncl");
				tableStrb.append(" left outer join om_job on hi_psndoc_deptchg.pk_postdoc=om_job.pk_om_job");
				tableStrb.append(" left outer join om_duty on hi_psndoc_deptchg.pk_om_duty=om_duty.pk_om_duty");
				where = " where  hi_psndoc_deptchg.lastflag = 'Y' and hi_psndoc_deptchg.bendflag='N'";//
				if (normalwheresql != null && !normalwheresql.trim().equalsIgnoreCase("")) {
					if (normalwheresql.indexOf("bd_psndoc.pk_deptdoc") > 0) {
						normalwheresql = StringUtil.replaceAllString(normalwheresql, "bd_psndoc.pk_deptdoc",
						"hi_psndoc_deptchg.pk_deptdoc");
					}
					where += normalwheresql;
				}
				if (jobtype >= 0) {
					where += " and hi_psndoc_deptchg.jobtype=" + jobtype + " ";
				}
				String cwhere = DLGwheresql;
				if (cwhere != null && cwhere.length() > 0) {
					where += " and (" + cwhere + ")";
				}
				where += " and bd_psndoc.indocflag = '" + (indocflag ? "Y" : "N") + "'";
				where += " and (bd_psndoc.psnclscope = 0 )  and bd_psncl.psnclscope = " + scope;
				// 附加部门权限
				powerSql = getDeptPowerSqls("hi_psndoc_deptchg", userid, pk_corp, loginCorp, includesubcorp);
				if (powerSql != null && powerSql.length() > 0) {
					where += " and (" + powerSql + ")";
				}

				// 附加人员类别权限 V55 add
				String powerPsnclSql = getPsnclPowerSql("hi_psndoc_deptchg", userid, loginCorp);
				if (powerPsnclSql != null && powerPsnclSql.length() > 0) {
					where += " and (" + powerPsnclSql + ")";
				}
			}
			where += " and (bd_corp.ishasaccount = 'Y' ) and ( bd_corp.isseal is null or bd_corp.isseal <> 'Y' ) and (bd_corp.pk_corp in (select pk_corp from sm_createcorp where sm_createcorp.pk_corp = bd_corp.pk_corp and funccode = '6007'))";

			if (appTable != null && appTable.trim().length() > 0) {
				tableStrb.append(appTable);
			}
			String tableStr = tableStrb.toString();
			String select = "select count(distinct bd_psndoc.pk_psndoc) ";
			if (scope == 0) {
				select = "select count(distinct hi_psndoc_deptchg.pk_psndoc_sub) ";
			}
			if (tableStr.indexOf("inner join hi_psndoc") >= 0 || tableStr.indexOf("inner join v_hr_psndoc_edu") >= 0) {
			}
			// String
			String sql = select + tableStr + where;
			PsnInfDMO dmo = new PsnInfDMO();
			result = dmo.queryRecordCountBySql(sql);
		} catch (Exception e) {
			throwBusinessException(e);
		}
		return result;
	}

	/**
	 * 经理自助查询人员信息。
	 * 
	 * @param indocflag
	 * @param pk_corp
	 * @param conditions
	 * @param scope
	 * @param powerSql
	 * @param jobtype
	 * @param listfield
	 * @param normalwheresql
	 * @return
	 * @throws BusinessException
	 */
	public GeneralVO[] queryByConditionDepts(boolean indocflag, String pk_corp, ConditionVO[] conditions, int scope,
			String powerSql, int jobtype, String listfield, String normalwheresql) throws BusinessException {
		GeneralVO[] vos = null;
		try {
			IParValue sysinitbo = (IParValue) PubDelegator.getIParValue();
			Integer recordcount = sysinitbo.getParaInt("0001", "HI_MAXLINE");
			recordcount = (recordcount == null ? new Integer(1000) : recordcount);
			String fixedField = getFixedFiled(scope);// sql语句中的固定字段
			String selfield = null;
			if (listfield == null || "".equalsIgnoreCase(listfield.trim())) {
				selfield = fixedField;
			} else {
				String[] field = listfield.split(",");
				if (field.length < 2) {
					selfield = fixedField + "," + listfield;
				} else {
					String tempselect = "";
					for (int i = 0; i < field.length; i++) {
						if (fixedField.indexOf(field[i]) > 0) {
							continue;
						} else {
							tempselect += "," + field[i];
						}
					}
					selfield = fixedField + tempselect;
				}
			}
			String where = null;
			String appTable = "";
			if (scope == 0) {
				appTable = getTableStr(conditions, 0);
			} else {
				appTable = getTableStr(conditions, 1);
			}
			StringBuffer buf = new StringBuffer();
			// V35 add --begin
			if (scope > 0) {// 归属范围 && scope!=5
				buf.append(" from bd_psndoc ");// bd_psndoc
				buf.append(" inner join bd_psnbasdoc on bd_psndoc.pk_psnbasdoc=bd_psnbasdoc.pk_psnbasdoc");
				buf.append(" inner join bd_corp on bd_psndoc.pk_corp=bd_corp.pk_corp");
				buf.append(" left outer join bd_deptdoc on bd_psndoc.pk_deptdoc=bd_deptdoc.pk_deptdoc");
				buf.append(" inner join bd_psncl on bd_psndoc.pk_psncl=bd_psncl.pk_psncl");
				buf.append(" left outer join om_job on bd_psndoc.pk_om_job=om_job.pk_om_job");
				buf.append(" left outer join om_duty on bd_psndoc.dutyname=om_duty.pk_om_duty");
				where = " where bd_psndoc.pk_corp = '" + pk_corp + "'";
				if (normalwheresql != null && !normalwheresql.trim().equalsIgnoreCase("")) {
					where += normalwheresql;
				}
				String cwhere = getWhere2(conditions, scope);
				if (cwhere != null && cwhere.length() > 0) {
					where += " and (" + cwhere + ")";
				}
				where += " and bd_psndoc.indocflag = '" + (indocflag ? "Y" : "N") + "'";
				where += " and bd_psndoc.psnclscope = " + scope;
				if (powerSql != null && powerSql.trim().startsWith("select"))// 附加部门权限
					where += " and (bd_psndoc.pk_deptdoc in (" + powerSql + "))";
			} else {
				buf
				.append(" from hi_psndoc_deptchg inner join bd_psndoc on hi_psndoc_deptchg.pk_psndoc=bd_psndoc.pk_psndoc");// bd_psndoc
				buf.append(" inner join bd_psnbasdoc on hi_psndoc_deptchg.pk_psnbasdoc=bd_psnbasdoc.pk_psnbasdoc");
				buf.append(" inner join bd_corp on hi_psndoc_deptchg.pk_corp=bd_corp.pk_corp");
				buf.append(" left outer join bd_deptdoc on hi_psndoc_deptchg.pk_deptdoc=bd_deptdoc.pk_deptdoc");
				buf.append(" inner join bd_psncl on hi_psndoc_deptchg.pk_psncl=bd_psncl.pk_psncl");
				buf.append(" left outer join om_job on hi_psndoc_deptchg.pk_postdoc=om_job.pk_om_job");
				buf.append(" left outer join om_duty on hi_psndoc_deptchg.pk_om_duty=om_duty.pk_om_duty");

				where = " where hi_psndoc_deptchg.lastflag = 'Y' and hi_psndoc_deptchg.bendflag='N'";
				if (normalwheresql != null && !normalwheresql.trim().equalsIgnoreCase("")) {
					if (normalwheresql.indexOf("bd_psndoc.pk_deptdoc") > 0) {
						normalwheresql = StringUtil.replaceAllString(normalwheresql, "bd_psndoc.pk_deptdoc",
						"hi_psndoc_deptchg.pk_deptdoc");
					}
					where += normalwheresql;
				}
				if (jobtype >= 0) {// jobtype == -1 表示查询所有人员
					where += " and hi_psndoc_deptchg.jobtype=" + jobtype + " ";
				}
				where += " and hi_psndoc_deptchg.pk_corp = '" + pk_corp + "'";
				String cwhere = getWhere2(conditions, scope);
				if (cwhere != null && cwhere.length() > 0) {
					where += " and (" + cwhere + ")";
				}
				where += " and bd_psndoc.indocflag = '" + (indocflag ? "Y" : "N") + "'";
				where += " and bd_psndoc.psnclscope = " + scope + " and bd_psncl.psnclscope = " + scope;
				if (powerSql != null && powerSql.trim().startsWith("select"))// 附加部门权限
					where += " and (hi_psndoc_deptchg.pk_deptdoc in (" + powerSql + ") )";
			}

			if (appTable != null && appTable.trim().length() > 0) {
				buf.append(appTable);
			}
			String tableStr = buf.toString();
			String order = null;
			if (scope > 0) {
				order = " order by bd_psndoc.showorder asc,bd_corp.unitcode asc,bd_deptdoc.deptcode asc,"
					+ "bd_psndoc.psncode asc,om_job.jobcode asc,bd_psncl.psnclasscode asc,om_duty.dutycode asc ";
			} else {
				order = " order by bd_psndoc.showorder asc,bd_corp.unitcode asc,bd_deptdoc.deptcode asc,"
					+ "bd_psndoc.psncode asc,om_job.jobcode asc,hi_psndoc_deptchg.jobtype asc,"
					+ "bd_psncl.psnclasscode asc,om_duty.dutycode asc ";
			}

			String select = "select ";
			if (tableStr.indexOf("inner join hi_psndoc") >= 0 || tableStr.indexOf("inner join v_hr_psndoc_edu") >= 0) {
				select += "distinct ";
			}
			// select += " top " + recordcount.toString();
			String sql = select + selfield + tableStr + where + order;

			String transsql = " select top " + recordcount + " * from (" + sql + ") newtable";
			PsnInfDMO dmo = new PsnInfDMO();
			// vos = dmo.queryBySql(sql);
			vos = dmo.queryBySqlForQuery(transsql);
			vos = fomularShoworder(vos);
		} catch (Exception e) {
			throwBusinessException(e);
		}
		return vos;
	}

	/**
	 * 在受部门权限控制的情况下 得到按部门编码(按层级排序)排序的 部门 比如： 01 - 01001 - 01002 02 - 02001 注意：select * from bd_deptdoc order by deptcode
	 * 查询出来的数据并不能保证 上述排序
	 * 
	 * @param depts
	 * @param Alldepts
	 * @return
	 */
	private DeptdocVO[] getPkDeptByCode(DeptdocVO[] powerDepts, DeptdocVO[] allDepts) {
		if (powerDepts == null || allDepts == null) {
			return null;
		}
		HashMap hmTemp = new HashMap();// key:最早父亲节点在allDepts中的位置；Value:部门权限的部门
		HashMap hmAll = new HashMap();
		for (int j = 0; j < allDepts.length; j++) {
			hmAll.put(allDepts[j].getPk_deptdoc(), allDepts[j]);
		}
		for (int i = 0; i < powerDepts.length; i++) {
			hmTemp = findFather(powerDepts[i].getPk_deptdoc(), hmAll, allDepts, hmTemp);
		}

		Vector v = new Vector();
		for (int i = 0; i < allDepts.length; i++) {
			DeptdocVO deptdocvo = (DeptdocVO) hmTemp.get(new Integer(i));
			if (deptdocvo != null) {
				v.addElement(deptdocvo);
			}
		}
		DeptdocVO[] deptvos = new DeptdocVO[v.size()];
		v.copyInto(deptvos);
		return deptvos;

	}

	private HashMap findFather(String pk_deptdoc, HashMap hm, DeptdocVO[] allDepts, HashMap outHm) {
		DeptdocVO vo = (DeptdocVO) hm.get(pk_deptdoc);
		for (int i = 0; i < allDepts.length; i++) {
			if (pk_deptdoc.equals(allDepts[i].getPk_deptdoc())) {
				outHm.put(new Integer(i), allDepts[i]);
				break;
			}
		}
		String pk_fatherdept = (vo == null ? null : vo.getPk_fathedept());
		if (pk_fatherdept == null) {
			return outHm;
		} else {
			return findFather(vo.getPk_fathedept(), hm, allDepts, outHm);
		}
	}

	/**
	 * 查询能够控制的部门。 创建日期：(2004-5-9 19:42:40)
	 * 
	 * @return nc.vo.hi.hi_301.CtrlDeptVO
	 * @param userID
	 *            java.lang.String
	 * @param pk_corp
	 *            java.lang.String
	 * @exception BusinessException
	 *                异常说明。
	 */
	public nc.vo.hi.hi_301.CtrlDeptVO queryCorpCtrlDepts(String userID, CtrlDeptVO corpVO, boolean useDeptPower,
			boolean includeHrCanceld) throws BusinessException {
		try {
			// 公司结点
			String pk_corp = corpVO.getPk_corp();
			String moduleCode = corpVO.getModuleCode();

			if (corpVO.getCode() == null) {
				nc.bs.bd.CorpBO cbo = new nc.bs.bd.CorpBO();
				nc.vo.bd.CorpVO corpvo = cbo.findByPrimaryKey(pk_corp);
				corpVO.setName(corpvo.getUnitname());
				corpVO.setCode(corpvo.getUnitcode());
				corpVO.setControlled(false);
				corpVO.setNodeType(CtrlDeptVO.CORP);
			}
			// 查出所有有权限的部门主键
			DeptdocVO[] depts = hasPower(userID, pk_corp, useDeptPower, moduleCode, includeHrCanceld);

			// 将有权限的部门放在哈希表中，加快判断地速度
			Hashtable powered = new Hashtable();
			for (int i = 0; i < depts.length; i++) {
				powered.put(depts[i].getPk_deptdoc(), depts[i]);
			}
			DeptdocVO[] Alldepts = null;
			if (useDeptPower) {
				Alldepts = hasPower(userID, pk_corp, false, moduleCode, includeHrCanceld);
			} else {
				Alldepts = depts;
			}
			DeptdocVO[] orderDepts = getPkDeptByCode(depts, Alldepts);
			HashMap allRelated = new HashMap();
			for (int j = 0; j < orderDepts.length; j++) {
				allRelated.put(orderDepts[j].getPk_deptdoc(), orderDepts[j]);
			}
			Vector vNotFather = new Vector();
			// 按部门编码升序排序，该父亲节点没有找到 的节点。
			HashMap hmAdded = new HashMap();// 记录扫描过的部门
			// 添加所有的部门，并创建树
			for (int i = 0; i < orderDepts.length; i++) {// pk_depts
				DeptdocVO deptTemp = orderDepts[i];
				if (deptTemp != null) {
					hmAdded.put(orderDepts[i].getPk_deptdoc(), orderDepts[i].getPk_deptdoc());
					String pk_fatherdept = deptTemp.getPk_fathedept();
					if (pk_fatherdept == null || hmAdded.get(pk_fatherdept) != null) {
						addtoDeptTree(corpVO, orderDepts[i], powered, allRelated);
					} else {
						vNotFather.addElement(deptTemp);
					}
				}
				int j = 0;
				while (vNotFather.size() > 0 && j < vNotFather.size()) {
					DeptdocVO somedept = (DeptdocVO) vNotFather.elementAt(j);
					String pk_father = somedept.getPk_fathedept();
					if (hmAdded.get(pk_father) != null) {
						addtoDeptTree(corpVO, somedept, powered, allRelated);
						vNotFather.remove(somedept);
						j--;
					}
					j++;
				}
			}
		} catch (Exception e) {
			throwBusinessException(e);
		}
		return corpVO;
	}

	/**
	 * 查询所有关联公司
	 * 
	 * @param userID
	 * @param pk_corp
	 * @param isRelate
	 * @return
	 * @throws BusinessException
	 */
	public CtrlDeptVO[] queryAllRelatedCorps(String userID, String pk_corp, boolean isRelate) throws BusinessException {
		CtrlDeptVO[] vos = null;
		try {
			PsnInfoDMO dmo = new PsnInfoDMO();
			vos = dmo.queryRelatChildCorps(userID, pk_corp, isRelate);
		} catch (Exception e) {
			throwBusinessException(e);
		}
		return vos;
	}

	/**
	 * 此处插入方法描述。 创建日期：(2004-10-19 9:08:23)
	 * 
	 * @return nc.vo.hi.hi_301.CtrlDeptVO
	 * @param userID
	 *            java.lang.String
	 * @param pk_corp
	 *            java.lang.String
	 * @param isRelate
	 *            boolean
	 * @exception BusinessException
	 *                异常说明。
	 */
	public CtrlDeptVO queryRelatedDepts(String userID, String pk_corp, boolean isRelate) throws BusinessException {
		// 根结点
		CtrlDeptVO root = new CtrlDeptVO();
		root.setNodeType(-1);
		root.setCode("");
		root.setName(nc.bs.ml.NCLangResOnserver.getInstance().getStrByID("common", "UC000-0000404")/* @res "公司" */);//
		root.setPk_corp("0001");
		root.setControlled(false);

		try {
			// wangkf fixed
			if (!"0001".equalsIgnoreCase(pk_corp)) {
				nc.bs.bd.CorpBO cbo = new nc.bs.bd.CorpBO();
				nc.vo.bd.CorpVO corpvo = cbo.findByPrimaryKey(pk_corp);
				CtrlDeptVO curCorpvo = new CtrlDeptVO();
				curCorpvo.setNodeType(CtrlDeptVO.CORP);
				curCorpvo.setPk_corp(pk_corp);
				curCorpvo.setName(corpvo.getUnitname());
				curCorpvo.setCode(corpvo.getUnitcode());
				curCorpvo.setControlled(true);
				//
				root.addChild(curCorpvo);
				// 添加所有子公司到树
				addChildCorpToDeptTree(root, curCorpvo, userID, isRelate);// wangkf
			} else {// 如果是集团登录
				CorpVO curcorp = new CorpVO();
				curcorp.setPk_corp("0001");
				UserVO curuser = new UserVO();
				curuser.setPrimaryKey(userID);
				CorpVO[] vcorpVOs = null;
				for (int i = 0; i < vcorpVOs.length; i++) {
					CtrlDeptVO curCorpvo = new CtrlDeptVO();
					curCorpvo.setNodeType(CtrlDeptVO.CORP);
					curCorpvo.setPk_corp(vcorpVOs[i].getPk_corp());
					curCorpvo.setName(vcorpVOs[i].getUnitname());
					curCorpvo.setCode(vcorpVOs[i].getUnitcode());
					curCorpvo.setControlled(true);
					if (root.findCorpVO(vcorpVOs[i].getPk_corp()) == null) {
						root.addChild(curCorpvo);
					}

					addChildCorpToDeptTree(root, curCorpvo, userID, isRelate);
				}
			}
		} catch (Exception e) {
			throwBusinessException(e);
		}
		return root;//
	}

	/**
	 * 修改一条子表记录。 创建日期：(2004-8-6 9:28:24)
	 * 
	 * @return java.lang.String
	 * @param tableCode
	 *            java.lang.String
	 * @param pk_psndoc
	 *            java.lang.String
	 * @param vo
	 *            nc.vo.hi.hi_301.GeneralVO
	 * @exception BusinessException
	 *                异常说明。
	 */
	public void updateChild(String tableCode, String pk_psndoc, GeneralVO vo) throws BusinessException {
		try {
			PsnInfDMO dmo = new PsnInfDMO();
			String pk_psndoc_sub = (String) vo.getAttributeValue("pk_psndoc_sub");
			String tableCode1 = tableCode;
			if (tableCode.equalsIgnoreCase("hi_psndoc_part")) {
				tableCode1 = "hi_psndoc_deptchg";
				boolean isnotzaizhi = dmo.isNOtZaizhiPerson(pk_psndoc);
				if (isnotzaizhi) {
					vo.setAttributeValue("bendflag", "Y");
				}
			}
			if (!dmo.checkPsnSub(pk_psndoc_sub, tableCode1)) {
				throw new BusinessException(nc.bs.ml.NCLangResOnserver.getInstance().getStrByID("600704",
				"UPP600704-000003")/*
				 * @res "该记录已经被删除，请刷新后再试！"
				 */);
			}
			String where = "pk_psndoc_sub = '" + pk_psndoc_sub + "'";
			dmo.updateTable(tableCode1, vo, where);
			if (tableCode.equalsIgnoreCase("hi_psndoc_edu")) {
				Object lasteducation = vo.getAttributeValue("lasteducation");
				if (lasteducation != null && ((UFBoolean) lasteducation).booleanValue()) {
					dmo.updateEdu(pk_psndoc, pk_psndoc_sub);
				}
			}

			if (vo != null) {
				String pk_corp = (String) vo.getAttributeValue("pk_corp");
				HIDelegator.getIPersonADDSV().operatePersonInfo(new String[] { pk_psndoc }, IPersonADDSV.MODIFY,
						pk_corp);
			}
		} catch (Exception e) {

			throwBusinessException(e);
		}
	}

	/**
	 * v53增加返聘处理
	 */
	public void updateMain(GeneralVO psndoc, GeneralVO accpsndocVO, GeneralVO mainaddpsndocVO) throws BusinessException {
		try {
			PsnInfDMO dmo = new PsnInfDMO();
			if (dmo.checkPsn(psndoc, new Integer(0)) == 0) { // 检查是否删除
				throw new BusinessException(nc.bs.ml.NCLangResOnserver.getInstance().getStrByID("600704",
				"UPP600704-000007")/*
				 * @res "更新失败，请刷新界面！"
				 */);
			}
			if (!"".equals(psndoc.getAttributeValue("psncode"))) {
				if (dmo.checkPsn(psndoc, new Integer(5)) == 5) { // 检查编码是否已存在
					throw new BusinessException(nc.bs.ml.NCLangResOnserver.getInstance().getStrByID("600704",
					"UPP600704-000005")/*
					 * @res "该人员编码已经存在，请刷新再试！"
					 */);
				}
			}

			if (accpsndocVO.getAttributeValue("id") != null) { // 身份证不为空检查是否已经加入黑名单（按身份证和姓名）
				accpsndocVO.setAttributeValue("psnname", psndoc.getAttributeValue("psnname"));
				if (dmo.checkPsn(accpsndocVO, new Integer(2)) == 2) {
					throw new BusinessException(nc.bs.ml.NCLangResOnserver.getInstance().getStrByID("600704",
					"UPP600704-000006")/*
					 * @res "该人员身份证＋姓名已经在黑名单中存在！"
					 */);
				}
				accpsndocVO.removeAttributeName("psnname");
			}

			if (psndoc.getAttributeValue("pk_deptdoc") != null
					&& ((String) psndoc.getAttributeValue("pk_deptdoc")).length() == 20) {
				String pk_deptdoc = ((String) psndoc.getAttributeValue("pk_deptdoc"));
				if (dmo.isDeptCancled(pk_deptdoc)) {
					throw new BusinessException(nc.bs.ml.NCLangResOnserver.getInstance().getStrByID("600704",
					"UPT600704-000275")/*
					 * @res "该部门已经封存,请刷新后再试！"
					 */);
				}
			}
			String pk_psncl = (String) psndoc.getAttributeValue("pk_psncl");
			boolean isoutduty = isOutdutyPsncl(pk_psncl);
			if (psndoc.getAttributeValue("pk_om_job") != null
					&& ((String) psndoc.getAttributeValue("pk_om_job")).length() == 20) {
				String pk_om_job = ((String) psndoc.getAttributeValue("pk_om_job"));
				if (dmo.isJobAbort(pk_om_job) && !isoutduty) {// 非在职才提示
					throw new BusinessException(nc.bs.ml.NCLangResOnserver.getInstance().getStrByID("600704",
					"UPT600704-000276")/*
					 * @res "该岗位已经撤销,请刷新后再试！"
					 */);
				}
			}

			psndoc.removeAttributeName("maintain");
			psndoc.removeAttributeName("isSynIndutydate");
			accpsndocVO.setAttributeValue("psnname", psndoc.getAttributeValue("psnname"));// V35 add
			updatePsndoc(psndoc);
			// add by sunxj 2010-04-28 H型财务接口 start
			UFBoolean waHrFiFlagTemp = PubDelegator.getIParValue().getParaBoolean(null, "WA-HRFI");
			boolean waHrFiFlag = waHrFiFlagTemp == null ? false : waHrFiFlagTemp.booleanValue();
			String indocflag = (String) psndoc.getAttributeValue("indocflag");
			if (waHrFiFlag && "Y".equals(indocflag)) {
				// 增加人员增量数据
				insertPsndocAdd(new GeneralVO[] { psndoc });
			}
			// add by sunxj 2010-04-28 H型财务接口 end
			updateAccpsndoc(accpsndocVO, (String) psndoc.getFieldValue("pk_psndoc"));

			// 更新拼音码
			updatePsnnamePinyin((String) psndoc.getFieldValue("pk_psndoc"), (String) accpsndocVO
					.getAttributeValue("psnname"));

			if (psndoc != null) {
				String pk_corp = (String) psndoc.getAttributeValue("pk_corp");
				HIDelegator.getIPersonADDSV().operatePersonInfo(
						new String[] { (String) psndoc.getAttributeValue("pk_psndoc") }, IPersonADDSV.MODIFY, pk_corp);
			}
			
			String groupdef15 = (String) psndoc.getAttributeValue("groupdef46");//zhanghua
			String pk_psndoc = (String) psndoc.getAttributeValue("pk_psndoc");
			String pk_dept = (String) psndoc.getAttributeValue("pk_deptdoc");
			String pk_psnbasdoc = (String) psndoc.getAttributeValue("pk_psnbasdoc");
			UFDate logindate = (UFDate) psndoc.getAttributeValue("groupdef47");
			//UFDate logindate = new();
			if(groupdef15 != null){
				if(groupdef15.equals("离职")){
					BaseDAO dao = new BaseDAO();
					Date now = new Date(); 
					
					SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd"); 
					Calendar ca = Calendar.getInstance();    
					ca.setTime(now); 
//			        String last = format.format(ca.getTime());
					String last =logindate.toString();
			        
					
			        // 回写离职信息表
			        PsndocDimissionVO psndvo = new PsndocDimissionVO();
			        psndvo.setPk_psnbasdoc(pk_psnbasdoc);
			        psndvo.setPk_psndoc(pk_psndoc);
			        psndvo.setLeavedate(new UFDate(last));
			        psndvo.setRecordnum(1);
			        psndvo.setPk_corp("1002");
			        psndvo.setPk_corpafter("1002");
			        dao.insertVO(psndvo);
			       
			        // 离职回写考勤人员
					String sql="update trtam_deptdoc_kq_b set denddate='"+last+"',bisnew ='N' where  pk_psndoc='"+pk_psndoc+"' and dr=0";
					dao.executeUpdate(sql);
					
					// 删除排班
					deletePaiban(last,pk_dept,pk_psndoc);
				}else if(groupdef15.equals("待退休")){
					BaseDAO dao = new BaseDAO();
					Date now = new Date(); 
					
					SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd"); 
					Calendar ca = Calendar.getInstance();    
					ca.setTime(now); 
					String last =logindate.toString();

					String sql="update trtam_deptdoc_kq_b set denddate='"+last+"',bisnew ='N' where  pk_psndoc='"+pk_psndoc+"' and  dr=0";
					dao.executeUpdate(sql);
					
					// 离职回写考勤人员到待退休
					DeptKqBVO deptkqbvo = new DeptKqBVO();
					deptkqbvo.setPk_dept("10028L10000000000GN2");
					deptkqbvo.setPk_psndoc(pk_psndoc);
					deptkqbvo.setDstartdate(new UFDate(last));
					deptkqbvo.setBisnew(new UFBoolean("Y"));
					deptkqbvo.setDr(0);
					dao.insertVO(deptkqbvo);
				}else if(groupdef15.equals("已退休")){
					BaseDAO dao = new BaseDAO();
					Date now = new Date(); 
					
					SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd"); 
					Calendar ca = Calendar.getInstance();    
					ca.setTime(now); 
					String last =logindate.toString();
					
			        // 回写离职信息表
			        PsndocDimissionVO psndvo = new PsndocDimissionVO();
			        psndvo.setPk_psnbasdoc(pk_psnbasdoc);
			        psndvo.setPk_psndoc(pk_psndoc);
			        psndvo.setLeavedate(new UFDate(last));
			        psndvo.setRecordnum(1);
			        psndvo.setPk_corp("1002");
			        psndvo.setPk_corpafter("1002");
			        dao.insertVO(psndvo);
			       
			        // 离职回写考勤人员
					String sql="update trtam_deptdoc_kq_b set denddate='"+last+"',bisnew ='N' where  pk_psndoc='"+pk_psndoc+"' and dr=0";
					dao.executeUpdate(sql);
					
					// 删除排班
					deletePaiban(last,pk_dept,pk_psndoc);
				}
			}
			
			
			
		} catch (Exception e) {
			throwBusinessException(e);
		}
	}
	
	public PaibanWeekVO[] getPaibanWeekBVO(String pk_psndoc,String date)throws BusinessException{
		String pk_psndoc_sub = null;
		String sql = "select pk_paiban,vdate from trtam_paiban where pk_psndoc='"+pk_psndoc+"'"+
		" and substr(vdate,0,10)<='"+date+"' and substr(vdate,12,11)>='"+date+"' and nvl(dr,0)=0";
		Vector o1 = (Vector) PubDelegator.getIPersistenceHome().executeQuery(sql,new VectorProcessor());
		PaibanWeekVO[] vos= null;
		if (o1.size() > 0 && o1 != null) {
			vos = new PaibanWeekVO[o1.size()];
			for (int i = 0; i < o1.size(); i++) {
				String pk_paiban = new String(((Vector) o1.elementAt(i)).elementAt(0) != null ? ((Vector) o1.elementAt(i)).elementAt(0).toString() : null);
				String vdate = new String(((Vector) o1.elementAt(i)).elementAt(1) != null ? ((Vector) o1.elementAt(i)).elementAt(1).toString() : null);
							
				PaibanWeekVO vo = new PaibanWeekVO();
				
				vo.setPk_paiban(pk_paiban);
				vo.setPk_psndoc(pk_psndoc);
				vo.setVdate(vdate);
				vos[i] = vo;
			}
		}
		return vos;	
	}
	public PanbanWeekBVO[] getPaibanWeekBVO(String date) throws BusinessException{
		String pk_psndoc_sub = null;
		String sql = "select pk_paiban,pk_psndoc,pk_bb,ddate from trtam_paiban_b where ddate >='"+date+"'";
		Vector o1 = (Vector) PubDelegator.getIPersistenceHome().executeQuery(sql,new VectorProcessor());
		PanbanWeekBVO[] bvos= null;
		if (o1.size() > 0 && o1 != null) {
			bvos = new PanbanWeekBVO[o1.size()];
			for (int i = 0; i < o1.size(); i++) {
				String pk_paiban = new String(((Vector) o1.elementAt(i)).elementAt(0) != null ? ((Vector) o1.elementAt(i)).elementAt(0).toString() : null);
				String pk_psndoc = new String(((Vector) o1.elementAt(i)).elementAt(0) != null ? ((Vector) o1.elementAt(i)).elementAt(0).toString() : null);
				String pk_bb = new String(((Vector) o1.elementAt(i)).elementAt(0) != null ? ((Vector) o1.elementAt(i)).elementAt(0).toString() : null);
				String ddate = new String(((Vector) o1.elementAt(i)).elementAt(0) != null ? ((Vector) o1.elementAt(i)).elementAt(0).toString() : null);

				PanbanWeekBVO bvo = new PanbanWeekBVO();
				bvo.setPk_bb(pk_bb);
				bvo.setPk_paiban(pk_paiban);
				bvo.setPk_psndoc(pk_psndoc);
				bvo.setDdate(new UFDate(ddate));
				bvos[i] = bvo;
			}
		}
		return bvos;	
	}
	
	public void deletePaiban(String date,String pk_dept,String pk_psndoc) throws BusinessException, ParseException{
		String pk_psndoc_sub = null;
		PaibanWeekVO[] bvos= null;
		String type = "",wbegindate ="",wenddate="",mbegindate="",menddate="";
		PaibanWeekVO[] vos = getPaibanWeekBVO(pk_psndoc,date);
		if(vos != null){
			if(vos.length > 0){
				for(int i=0;i<vos.length;i++ ){
					PaibanWeekVO hvo =  vos[i];
					String vdate = hvo.getVdate();
					
					UFDate begindate = new UFDate(vdate.substring(0, 10));
					UFDate enddate = new UFDate(vdate.substring(11, 21));
					if(enddate.getDaysBetween(begindate, enddate) > 7 ){
						type += "MONTH";
						mbegindate = vdate.substring(0, 10);
						menddate = vdate.substring(11, 21);
					}else{
						type += "WEEK";
						wbegindate = vdate.substring(0, 10);
						wenddate = vdate.substring(11, 21);
					}
				}
				
			}
		}
		
		//
		UFDate ufleavedate = new UFDate(date);
		String leavedate = ufleavedate.toString();
		Integer leaveday = Integer.valueOf(leavedate.substring(8,10));
	
		String vbbname ="";
		String pk_bb ="";
		
		BaseDAO dao = new BaseDAO();
		if(type.indexOf("MONTH") != -1){
			for(int i= leaveday;i<=31;i++){
				vbbname += "vbbname"+i+"='',";
				pk_bb += "pk_bb"+i+"='',";
			}
			// 更新当前离职日期所在区间
			String sql = "update trtam_paiban set "+vbbname+pk_bb+" dr=0 where pk_psndoc='"+pk_psndoc+"'"+
			" and substr(vdate,0,10) = '"+mbegindate+"' and substr(vdate,12,11) = '"+menddate+"'";
			dao.executeUpdate(sql);
			
			String vbbname1="",pk_bb1="";
			for(int i= 1;i<=31;i++){
				vbbname1 += "vbbname"+i+"='',";
				pk_bb1 += "pk_bb"+i+"='',";
			}
			// 删除结束日期后所有班
			String sql1 = "update trtam_paiban set "+vbbname1+pk_bb1+" dr = 1 where pk_psndoc='"+pk_psndoc+"'"+" and substr(vdate,0,10)>'"+menddate+"'";
			dao.executeUpdate(sql1);
			
		}else if(type.indexOf("WEEK") != -1){
			int numday = getDay(date)-1;
			if(numday == 0){
				numday =7;
			}
			
			for(int i= numday;i<=7;i++){
				vbbname += "vbbname"+i+"='',";
				pk_bb += "pk_bb"+i+"='',";
			}
			// 删除
			String sql = "update trtam_paiban set "+vbbname+pk_bb+" dr=0 where pk_psndoc='"+pk_psndoc+"'"+
			" and substr(vdate,0,10) = '"+wbegindate+"' and substr(vdate,12,11) ='"+wenddate+"'";
			
			String sql1 = "update trtam_paiban set vbbname1='',vbbname2='',vbbname3='',vbbname4='',vbbname5='',vbbname6='',vbbname7='',pk_bb1='',pk_bb2='',pk_bb3='',pk_bb4='',pk_bb5='',pk_bb6='',pk_bb7='', dr = 1 where pk_psndoc='"+pk_psndoc+"'"+
			" and substr(vdate,0,10)>'"+wenddate+"'";
			dao.executeUpdate(sql1);
			dao.executeUpdate(sql);
		}
		
		
		String sql2 ="delete trtam_paiban_b where ddate>='"+date+"' and pk_psndoc='"+pk_psndoc+"'";
//		String sql2 ="delete trtam_paiban_b where ddate>='"+date+"' and pk_psndoc='"+pk_psndoc+"'";
		dao.executeUpdate(sql2);
		
		
	}
	
	public int getDay(String str) throws ParseException{
		SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd");
		Date date =sdf.parse(str);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		// 第几周
		int week = calendar.get(Calendar.WEEK_OF_MONTH);
		// 第几天，从周日开始
		int day = calendar.get(Calendar.DAY_OF_WEEK);
		return day;
	}

	/**
	 * v50 add 引用人员修改主集时处理薪资情况
	 */
	public void updateRefPersonMain(GeneralVO psndoc, GeneralVO accpsndocVO, GeneralVO mainaddpsndocVO, UFDate logdate)
	throws BusinessException {
		try {
			PsnInfDMO dmo = new PsnInfDMO();
			String pk_corp = (String) accpsndocVO.getAttributeValue("curret_pk_corp");
			accpsndocVO.removeAttributeName("curret_pk_corp");
			updateMain(psndoc, accpsndocVO, mainaddpsndocVO);
			String pk_psndoc = (String) psndoc.getAttributeValue("pk_psndoc");
			String pk_psndoc_sub = dmo.getPsnchgpk(pk_psndoc);
			boolean wa = isEnableWA(pk_corp);
			if (wa) {
				updateWa(pk_psndoc, pk_psndoc_sub, pk_corp, logdate);// 调用薪资接口更新工资
			}
			if (psndoc != null) {
				String pk_corp1 = (String) psndoc.getAttributeValue("pk_corp");
				HIDelegator.getIPersonADDSV().operatePersonInfo(
						new String[] { (String) psndoc.getAttributeValue("pk_psndoc") }, IPersonADDSV.MODIFY, pk_corp1);
			}
		} catch (Exception e) {
			throwBusinessException(e);
		}
	}

	/**
	 * 调用薪资接口更新工资
	 * 
	 * @param pk_psndoc
	 * @param pk_psndoc_sub
	 */
	private void updateWa(String pk_psndoc, String pk_psndoc_sub, String pk_corp, UFDate logdate)
	throws BusinessException {
		try {
			nc.itf.hr.wa.IPsnChanged psnchanged = (nc.itf.hr.wa.IPsnChanged) NCLocator.getInstance().lookup(
					nc.itf.hr.wa.IPsnChanged.class.getName());
			nc.vo.bd.b06.PsndocVO vo = new nc.vo.bd.b06.PsndocVO();
			vo.setPrimaryKey(pk_psndoc);
			vo.setPk_deptdoc(null);
			psnchanged.psndocUpdated(vo, pk_psndoc_sub, 1, pk_corp, logdate);
		} catch (Exception e) {
			throwBusinessException(e);
		}
	}

	/**
	 * 判断是否启用了薪资模块 WA (funcode：6039)
	 * 
	 * @return boolean true - NC薪资模块，false - 则相反
	 */

	private boolean isEnableWA(String pk_corp) {
		boolean isEnabled = false;
		try {
			ICreateCorpQueryService createCorpQuery = (ICreateCorpQueryService) NCLocator.getInstance().lookup(
					ICreateCorpQueryService.class.getName());
			isEnabled = createCorpQuery.isEnabled(pk_corp, "WA");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return isEnabled;
	}

	/**
	 * 此处插入方法描述。 创建日期：(2005-03-28 21:03:27)
	 * 
	 * @param power
	 *            java.lang.String
	 */
	public String getBillTempletID(String pk_corp, String userid, String nodecode) throws BusinessException {
		String id = null;
		try {
			PsnInfDMO dmo = new PsnInfDMO();
			id = dmo.getBillTempletID(pk_corp, userid, nodecode);
		} catch (Exception e) {
			throwBusinessException(e);
		}
		return id;
	}

	/**
	 * 替换自定义项
	 * 
	 * @param selectfield
	 * @return
	 */
	private String updateDefdocfiled(String selectfield) {
		if (selectfield != null && !"".equalsIgnoreCase(selectfield)) {
			if (selectfield.indexOf("healthcode") > 0) {
				selectfield = StringUtil.replaceAllString(selectfield, "healthcode", "h.doccode as healthcode");
			}
			if (selectfield.indexOf("healthname") > 0) {
				selectfield = StringUtil.replaceAllString(selectfield, "healthname", "h.docname as healthname");
			}
			if (selectfield.indexOf("nationalitycode") > 0) {
				selectfield = StringUtil.replaceAllString(selectfield, "nationalitycode",
				"n.doccode as nationalitycode");
			}
			if (selectfield.indexOf("nationalityname") > 0) {
				selectfield = StringUtil.replaceAllString(selectfield, "nationalityname",
				"n.docname as nationalityname");
			}
			if (selectfield.indexOf("maritalcode") > 0) {
				selectfield = StringUtil.replaceAllString(selectfield, "maritalcode", "m.doccode as maritalcode");
			}
			if (selectfield.indexOf("maritalname") > 0) {
				selectfield = StringUtil.replaceAllString(selectfield, "maritalname", "m.docname as maritalname");
			}
			if (selectfield.indexOf("dutyrankcode") > 0) {// 职务蔟
				selectfield = StringUtil.replaceAllString(selectfield, "dutyrankcode", "d.doccode as dutyrankcode");
			}
			if (selectfield.indexOf("dutyrankname") > 0) {// 职务蔟
				selectfield = StringUtil.replaceAllString(selectfield, "dutyrankname", "d.docname as dutyrankname");
			}
			if (selectfield.indexOf("postsrcode") > 0) {// 岗位序列
				selectfield = StringUtil.replaceAllString(selectfield, "postsrcode", "p.doccode as postsrcode");
			}
			if (selectfield.indexOf("postsrname") > 0) {// 岗位序列
				selectfield = StringUtil.replaceAllString(selectfield, "postsrname", "p.docname as postsrname");
			}
			if (selectfield.indexOf("postlvcode") > 0) {// 岗位等级
				selectfield = StringUtil.replaceAllString(selectfield, "postlvcode", "pp.doccode as postlvcode");
			}
			if (selectfield.indexOf("postlvname") > 0) {// 岗位等级
				selectfield = StringUtil.replaceAllString(selectfield, "postlvname", "pp.docname as postlvname");
			}

		}
		return selectfield;
	}

	/**
	 * 
	 * @param selectfield
	 * @return
	 */
	private StringBuffer appandFromStb(String selectField, StringBuffer tableStrb) {
		if (selectField != null && !"".equalsIgnoreCase(selectField)) {
			if (selectField.indexOf("healthcode") > 0 || selectField.indexOf("healthname") > 0) {
				tableStrb.append(" left outer join bd_defdoc h on bd_psnbasdoc.health = h.pk_defdoc");
			}
			if (selectField.indexOf("nationalitycode") > 0 || selectField.indexOf("nationalityname") > 0) {
				tableStrb.append(" left outer join bd_defdoc n on bd_psnbasdoc.nationality = n.pk_defdoc");
			}
			if (selectField.indexOf("maritalcode") > 0 || selectField.indexOf("maritalname") > 0) {
				tableStrb.append(" left outer join bd_defdoc m on bd_psnbasdoc.marital = m.pk_defdoc");
			}
			if (selectField.indexOf("dutyrankcode") > 0 || selectField.indexOf("dutyrankname") > 0) {// 职务蔟
				tableStrb.append(" left outer join bd_defdoc d on bd_psndoc.series = d.pk_defdoc");
			}
			if (selectField.indexOf("postsrcode") > 0 || selectField.indexOf("postsrname") > 0) {// 岗位序列
				tableStrb.append(" left outer join bd_defdoc p on bd_psndoc.jobseries = p.pk_defdoc");
			}
			if (selectField.indexOf("postlvcode") > 0 || selectField.indexOf("postlvname") > 0) {// 岗位等级
				tableStrb.append(" left outer join bd_defdoc pp on bd_psndoc.jobrank = pp.pk_defdoc");
			}

		}
		return tableStrb;
	}

	/**
	 * 根据条件,查询视图中的所有数据 wangkf new add
	 * 
	 * @param conditions
	 * @param hmPower
	 * @param corps
	 * @param indocflag
	 * @param scope
	 * @return
	 * @throws BusinessException
	 */
	public GeneralVO[] getExtendedData(ConditionVO[] conditions, HashMap hmPower, String[] corps, boolean indocflag,
			int scope, int jobtype, String listfield, String normalwheresql) throws BusinessException {
		if (hmPower == null) {
			return null;
		}
		String selectField = "";
		String fixedField = getFixedFiled(scope);

		String where = null;
		StringBuffer tableStrb = new StringBuffer();
		String appTable = "";
		GeneralVO[] result = null;
		try {
			PsnInfDMO dmo = new PsnInfDMO();
			Vector vAllData = new Vector();
			// 处理
			String psndocdict = "40000000000000000001";
			String basdocdict = "40000000000000000002";
			String[] psndocitems = dmo.queryAllDefFlddict(corps[0], psndocdict);
			String[] basdocitems = dmo.queryAllDefFlddict(corps[0], basdocdict);

			// 得到公司自定义和集团自定义字段的个数 hr_defdoc
			String defTable = "bd_psndoc";
			String defitemSql = "";
			if (psndocitems != null && psndocitems.length > 0) {
				for (int i = 0; i < psndocitems.length; i++) {
					if (psndocitems[i].indexOf("UFAGE[") >= 0
							|| psndocitems[i].indexOf(CommonValue.UFFORMULA_DATA) >= 0) {
						continue;
					} else {
						defitemSql += (defTable + "." + psndocitems[i] + ",");
					}
				}
			}
			// add by zhyan 2006-04-12 处理个人信息的自定义
			String basdefTable = "bd_psnbasdoc";
			String basdefitemSql = "";
			if (basdocitems != null && basdocitems.length > 0) {
				for (int i = 0; i < basdocitems.length; i++) {
					if (basdocitems[i].indexOf("UFAGE[") >= 0
							|| psndocitems[i].indexOf(CommonValue.UFFORMULA_DATA) >= 0) {
						continue;
					} else {
						basdefitemSql += (basdefTable + "." + basdocitems[i] + ",");
					}
				}
			}
			// 前台传回的显示列
			if (listfield == null || "".equalsIgnoreCase(listfield.trim())) {
				selectField = defitemSql + basdefitemSql + fixedField;
			} else {
				selectField = defitemSql + basdefitemSql + fixedField + "," + listfield;
			}
			selectField = updateDefdocfiled(selectField);

			for (int j = 0; j < corps.length; j++) {
				String pk_corp = corps[j];
				String powerSql = (String) hmPower.get(pk_corp);
				tableStrb = new StringBuffer();

				if (scope > 0) {// 归属范围
					tableStrb.append(" from bd_psndoc ");
					tableStrb.append(" inner join bd_psnbasdoc on bd_psndoc.pk_psnbasdoc=bd_psnbasdoc.pk_psnbasdoc");
					tableStrb.append(" inner join bd_corp on bd_psndoc.pk_corp=bd_corp.pk_corp");
					tableStrb.append(" left outer join bd_deptdoc on bd_psndoc.pk_deptdoc=bd_deptdoc.pk_deptdoc");
					tableStrb.append(" inner join bd_psncl on bd_psndoc.pk_psncl=bd_psncl.pk_psncl");
					tableStrb.append(" left outer join om_job on bd_psndoc.pk_om_job=om_job.pk_om_job");
					tableStrb.append(" left outer join om_duty on bd_psndoc.dutyname=om_duty.pk_om_duty");
					tableStrb
					.append(" left outer join hi_psndoc_deptchg on hi_psndoc_deptchg.pk_psndoc=bd_psndoc.pk_psndoc and hi_psndoc_deptchg.recordnum = 0 ");
					where = " where bd_psndoc.pk_corp = '" + pk_corp + "'";
					if (normalwheresql != null && !normalwheresql.trim().equalsIgnoreCase("")) {
						where += normalwheresql;
					}
					String cwhere = getWhere2(conditions, scope);
					if (cwhere != null && cwhere.length() > 0) {
						where += " and (" + cwhere + ")";
					}
					where += " and bd_psndoc.indocflag = '" + (indocflag ? "Y" : "N") + "'";
					where += " and bd_psndoc.psnclscope = " + scope;

				} else if (scope == 0) {
					tableStrb
					.append(" from hi_psndoc_deptchg inner join bd_psndoc on hi_psndoc_deptchg.pk_psndoc=bd_psndoc.pk_psndoc");// bd_psndoc
					tableStrb
					.append(" inner join bd_psnbasdoc on hi_psndoc_deptchg.pk_psnbasdoc=bd_psnbasdoc.pk_psnbasdoc");
					tableStrb.append(" inner join bd_corp on hi_psndoc_deptchg.pk_corp=bd_corp.pk_corp");
					tableStrb
					.append(" left outer join bd_deptdoc on hi_psndoc_deptchg.pk_deptdoc=bd_deptdoc.pk_deptdoc");
					tableStrb.append(" inner join bd_psncl on hi_psndoc_deptchg.pk_psncl=bd_psncl.pk_psncl");
					tableStrb.append(" left outer join om_job on hi_psndoc_deptchg.pk_postdoc=om_job.pk_om_job");
					tableStrb.append(" left outer join om_duty on hi_psndoc_deptchg.pk_om_duty=om_duty.pk_om_duty");

					where = " where hi_psndoc_deptchg.lastflag = 'Y' and hi_psndoc_deptchg.bendflag='N'";
					if (normalwheresql != null && !normalwheresql.trim().equalsIgnoreCase("")) {
						where += normalwheresql;
					}
					if (jobtype >= 0) {// jobtype == -1 表示查询所有人员
						where += " and hi_psndoc_deptchg.jobtype=" + jobtype;
					}
					where += " and hi_psndoc_deptchg.pk_corp = '" + pk_corp + "'";
					String cwhere = getWhere2(conditions, scope);
					if (cwhere != null && cwhere.length() > 0) {
						where += " and (" + cwhere + ")";
					}
					where += " and bd_psndoc.indocflag = '" + (indocflag ? "Y" : "N") + "'";
					where += " and bd_psndoc.psnclscope = " + scope + " and bd_psncl.psnclscope = " + scope;
					if (powerSql != null && powerSql.trim().startsWith("select"))// 附加部门权限
						where += " and (hi_psndoc_deptchg.pk_deptdoc in (" + powerSql + ") )";

				} else {
					tableStrb.append(" from bd_psndoc ");
					tableStrb.append(" inner join bd_psnbasdoc on bd_psndoc.pk_psnbasdoc=bd_psnbasdoc.pk_psnbasdoc");
					tableStrb.append(" inner join bd_corp on bd_psndoc.pk_corp=bd_corp.pk_corp");
					tableStrb.append(" left outer join bd_deptdoc on bd_psndoc.pk_deptdoc=bd_deptdoc.pk_deptdoc");
					tableStrb.append(" inner join bd_psncl on bd_psndoc.pk_psncl=bd_psncl.pk_psncl");
					tableStrb.append(" left outer join om_job on bd_psndoc.pk_om_job=om_job.pk_om_job");
					tableStrb.append(" left outer join om_duty on bd_psndoc.dutyname=om_duty.pk_om_duty");

					where = " where bd_psndoc.pk_corp = '" + pk_corp + "'";
					if (normalwheresql != null && !normalwheresql.trim().equalsIgnoreCase("")) {
						where += normalwheresql;
					}
					String cwhere = getWhere2(conditions, scope);
					if (cwhere != null && cwhere.length() > 0) {
						where += " and (" + cwhere + ")";
					}
					where += "  and bd_psndoc.indocflag = '" + (indocflag ? "Y" : "N") + "'";

				}
				// 关联自定义档案
				tableStrb = appandFromStb(selectField, tableStrb);

				if (scope == 0) {
					appTable = getTableStr(conditions, 0);
				} else {
					appTable = getTableStr(conditions, 1);
				}
				if (appTable != null && appTable.trim().length() > 0) {
					tableStrb.append(appTable);
				}
				String order = null;
				if (scope > 0) {
					order = " order by bd_psndoc.showorder asc,bd_corp.unitcode asc,bd_deptdoc.deptcode asc,bd_psndoc.psncode asc,om_job.jobcode asc,bd_psncl.psnclasscode asc,om_duty.dutycode asc ";
				} else if (scope == 0) {
					order = " order by bd_psndoc.showorder asc,bd_corp.unitcode asc,bd_deptdoc.deptcode asc,bd_psndoc.psncode asc,om_job.jobcode asc,hi_psndoc_deptchg.jobtype asc,bd_psncl.psnclasscode asc,om_duty.dutycode asc ";
				} else {
					order = " order by bd_psndoc.showorder asc,bd_corp.unitcode asc,bd_deptdoc.deptcode asc,bd_psndoc.psncode asc,om_job.jobcode asc,bd_psncl.psnclasscode asc,om_duty.dutycode asc ";
				}
				String sqlTable = " select " + selectField + tableStrb.toString() + where + order;
				GeneralVO[] tempvo = dmo.queryBySql(sqlTable);

				if (tempvo != null) {
					for (int i = 0; i < tempvo.length; i++) {
						vAllData.addElement(tempvo[i]);
					}
				}

			}
			result = new GeneralVO[vAllData.size()];
			if (vAllData.size() > 0) {
				vAllData.copyInto(result);
			}
		} catch (Exception e) {
			throwBusinessException(e);
		}
		return result;
	}

	/**
	 * 从查询条件中取得条件
	 * 
	 * @param conditions
	 * @return
	 */
	private String getWhere(ConditionVO[] conditions) {
		if (conditions == null || conditions.length == 0) {
			return "";
		}
		ConditionVO[] cvos = null;
		int count = conditions.length;
		int fixRowCount = 0;
		if ("##@@##".equals(conditions[0].getFieldCode())) {// 记录固定查询条件行数的查询条件，需要过滤掉
			fixRowCount = conditions[0].getDataType();
			Vector<ConditionVO> v = new Vector<ConditionVO>();
			for (int i = 1; i < conditions.length; i++) {
				v.addElement(conditions[i]);
			}
			cvos = new ConditionVO[count - 1];
			v.copyInto(cvos);
		}
		if (cvos == null) {
			cvos = conditions;
		}
		try {
			String where = "";
			for (int i = 0; i < cvos.length; i++) {
				if (fixRowCount > 0 && fixRowCount == i) { // /处理固定条件后边的条件加括号，并且所有的条件和固定条件是‘and’关系
					cvos[i].setLogic(true);
					cvos[i].setNoLeft(false);
					cvos[cvos.length - 1].setNoRight(false);
				}
				String dateformula = cvos[i].getFieldCode();
				if (dateformula.indexOf("UFAGE[") >= 0) {
					if (cvos[i].getSQLStrForNull().indexOf("is null") >= 0) {
						if (dateformula.indexOf("bd_psnbasdoc") >= 0 && dateformula.indexOf("birthdate") >= 0) {
							where += " and (bd_psnbasdoc.birthdate is null ) ";
						} else if (dateformula.indexOf("bd_psnbasdoc") >= 0 && dateformula.indexOf("joinworkdate") >= 0) {
							where += " and (bd_psnbasdoc.joinworkdate is null)";
						} else if (dateformula.indexOf("bd_psndoc") >= 0 && dateformula.indexOf("indutydate") >= 0) {
							where += " and (bd_psndoc.indutydate is null )";
						}
					} else {
						String tablename = cvos[i].getTableCodeForMultiTable();
						String truesql = nc.vo.hr.global.DateFormulaParse.proDateFormula(dateformula
								.substring(dateformula.indexOf(".") + 1), tablename);
						where += nc.vo.hr.global.GlobalTool.replaceString(cvos[i].getSQLStr(), dateformula, truesql);
					}
				}
				// modified by wl 2004-11-02
				else if (dateformula.indexOf("groupdef") >= 0 || dateformula.indexOf("corpdef") >= 0) {
					if (cvos[i].getDataType() == 1 || cvos[i].getDataType() == 2)// 整形或小数型自定义项都为字符型
						cvos[i].setDataType(0);
					where += cvos[i].getSQLStr();// getSQLStrForNull
				} else {
					where += cvos[i].getSQLStr();
				}
			}
			// 去掉第一个and/or
			if (where.length() > 3) {
				try {
					where = where.substring(where.indexOf(" ", 1));
				} catch (Throwable e) {
					e.printStackTrace();
				}
			} else {
				where = "";
			}
			return where;
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	/**
	 * 替换where条件中的表名。 在查询“在职”人员时，其主表为hi_psndoc_deptchg，岗位、职务等查询条件也都按照此表字段查询。因此需要替换。
	 * 
	 * @param wheresql
	 */
	private void replaceTableStr(String wheresql) {
		if (wheresql == null)
			return;
		wheresql.replaceAll("bd_psndoc.pk_deptdoc", "hi_psndoc_deptchg.pk_deptdoc");
		wheresql.replaceAll("bd_psndoc.pk_om_job", "hi_psndoc_deptchg.pk_postdoc");
		wheresql.replaceAll("bd_psndoc.pk_psncl", "hi_psndoc_deptchg.pk_psncl");
		wheresql.replaceAll("bd_psndoc.dutyname", "hi_psndoc_deptchg.pk_om_duty");
		wheresql.replaceAll("bd_psndoc.jobseries", "hi_psndoc_deptchg.pk_jobserial");
		wheresql.replaceAll("bd_psndoc.jobrank", "hi_psndoc_deptchg.pk_jobrank");
		wheresql.replaceAll("bd_psndoc.series", "hi_psndoc_deptchg.pk_detytype");
		wheresql.replaceAll("bd_psndoc.jobtypeflag", "hi_psndoc_deptchg.jobtype");

	}

	/**
	 * 从查询条件中取得条件
	 * 
	 * @param conditions
	 * @return
	 */
	private String getWhere2(ConditionVO[] conditions, int scope) {
		if (conditions == null || conditions.length == 0) {
			return "";
		}
		int count = conditions.length;
		ConditionVO[] cvos = null;
		int fixRowCount = 0;
		if ("##@@##".equals(conditions[0].getFieldCode())) {// 记录固定查询条件行数的查询条件，需要过滤掉
			fixRowCount = conditions[0].getDataType();
			Vector<ConditionVO> v = new Vector<ConditionVO>();
			for (int i = 1; i < conditions.length; i++) {
				v.addElement(conditions[i]);
			}
			cvos = new ConditionVO[count - 1];
			v.copyInto(cvos);
		}
		if (cvos == null) {
			cvos = conditions;
		}
		Hashtable htfield = new Hashtable();
		if (scope == 0) {// 在职时才替换
			htfield.put("bd_psndoc.pk_deptdoc", "hi_psndoc_deptchg.pk_deptdoc");
			htfield.put("bd_psndoc.pk_om_job", "hi_psndoc_deptchg.pk_postdoc");
			htfield.put("bd_psndoc.pk_psncl", "hi_psndoc_deptchg.pk_psncl");
			htfield.put("bd_psndoc.dutyname", "hi_psndoc_deptchg.pk_om_duty");
			htfield.put("bd_psndoc.jobseries", "hi_psndoc_deptchg.pk_jobserial");
			htfield.put("bd_psndoc.jobrank", "hi_psndoc_deptchg.pk_jobrank");
			htfield.put("bd_psndoc.series", "hi_psndoc_deptchg.pk_detytype");
			htfield.put("bd_psndoc.jobtypeflag", "hi_psndoc_deptchg.jobtype");

			// htfield.put("hi_psndoc_deptchg.pk_deptdoc", "deptchg.pk_deptdoc");
			// htfield.put("hi_psndoc_deptchg.pk_postdoc", "deptchg.pk_postdoc");
			// htfield.put("hi_psndoc_deptchg.pk_psncl", "deptchg.pk_psncl");
			// htfield.put("hi_psndoc_deptchg.pk_om_duty", "deptchg.pk_om_duty");
			// htfield.put("hi_psndoc_deptchg.pk_jobserial","deptchg.pk_jobserial");
			// htfield.put("hi_psndoc_deptchg.pk_jobrank", "deptchg.pk_jobrank");
			// htfield.put("hi_psndoc_deptchg.pk_detytype", "deptchg.pk_detytype");
			// htfield.put("hi_psndoc_deptchg.jobtype", "deptchg.jobtype");

		}
		Vector v = new Vector();
		for (int i = 0; i < cvos.length; i++) {
			String fieldcode = cvos[i].getFieldCode();
			if (htfield.get(fieldcode) != null) {
				cvos[i].setFieldCode((String) htfield.get(fieldcode));
			}
			v.addElement(cvos[i]);
		}
		cvos = new ConditionVO[v.size()];
		v.copyInto(cvos);
		try {
			String where = "";
			for (int i = 0; i < cvos.length; i++) {
				if (fixRowCount > 0 && fixRowCount == i) { // /处理固定条件后边的条件加括号，并且所有的条件和固定条件是‘and’关系
					cvos[i].setLogic(true);
					cvos[i].setNoLeft(false);
					cvos[cvos.length - 1].setNoRight(false);
				}
				String dateformula = cvos[i].getFieldCode();
				if (dateformula.indexOf("UFAGE[") >= 0) {
					if (cvos[i].getSQLStrForNull().indexOf("is null") >= 0) {
						if (dateformula.indexOf("bd_psnbasdoc") >= 0 && dateformula.indexOf("birthdate") >= 0) {
							// String substring =
							// conditions[i].getSQLStrForNull().substring(" and
							// (");
							where += " and (bd_psnbasdoc.birthdate is null ) ";
						} else if (dateformula.indexOf("bd_psnbasdoc") >= 0 && dateformula.indexOf("joinworkdate") >= 0) {
							where += " and (bd_psnbasdoc.joinworkdate is null)";
						} else if (dateformula.indexOf("bd_psndoc") >= 0 && dateformula.indexOf("indutydate") >= 0) {
							where += " and (bd_psndoc.indutydate is null )";
						}
					} else {
						String tablename = cvos[i].getTableCodeForMultiTable();
						String truesql = nc.vo.hr.global.DateFormulaParse.proDateFormula(dateformula
								.substring(dateformula.indexOf(".") + 1), tablename);

						where += nc.vo.hr.global.GlobalTool.replaceString(cvos[i].getSQLStrForNull(), dateformula,
								truesql);
					}
				}
				// modified by wl 2004-11-02
				else if (dateformula.indexOf("groupdef") >= 0 || dateformula.indexOf("corpdef") >= 0) {
					if (cvos[i].getDataType() == 1 || cvos[i].getDataType() == 2)// 整形或小数型自定义项都为字符型
						cvos[i].setDataType(0);
					// where += conditions[i].getSQLStr();// getSQLStrForNull
					where += cvos[i].getSQLStrForNull();
				} else {
					where += cvos[i].getSQLStrForNull();
				}
			}
			// 去掉第一个and/or
			if (where.length() > 3) {
				try {
					where = where.substring(where.indexOf(" ", 1));
				} catch (Throwable e) {
					e.printStackTrace();
				}
			} else {
				where = "";
			}
			return where;
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	private String getTableStr(ConditionVO[] conditions, int flag) {
		Hashtable ht = new Hashtable();
		if (flag == 0) {
			ht.put("hi_psndoc_deptchg", "deptchg");
		}
		ht.put("bd_psndoc", "bd_psndoc");
		ht.put("bd_psnbasdoc", "bd_psnbasdoc");
		ht.put("bd_deptdoc", "bd_deptdoc");
		ht.put("bd_corp", "bd_corp");
		ht.put("bd_psncl", "bd_psncl");
		ht.put("om_job", "om_job");
		ht.put("om_duty", "om_duty");

		StringBuffer tableStr = new StringBuffer();
		if (conditions == null || conditions.length == 0) {
			return "";
		}
		for (int i = 0; i < conditions.length; i++) {
			String fieldCode = conditions[i].getFieldCode();
			int index = fieldCode.indexOf(".");
			if (index < 0) {
				continue;
			}
			if (fieldCode.indexOf("v_hr_psndoc.nationality") >= 0) {
				if (ht.get("v_hr_psndoc.nationality") == null) {
					tableStr
					.append(" left outer join bd_defdoc a on bd_psnbasdoc.nationality = a.pk_defdoc and a.pk_defdoclist = 'HI000000000000000003'");
					ht.put("v_hr_psndoc.nationality", "v_hr_psndoc.nationality");
				}
				conditions[i].setFieldCode("a.doc" + fieldCode.substring("v_hr_psndoc.nationality".length()));
			} else if (fieldCode.indexOf("v_hr_psndoc.marital") >= 0) {
				if (ht.get("v_hr_psndoc.marital") == null) {
					tableStr
					.append(" left outer join bd_defdoc b on bd_psnbasdoc.marital = b.pk_defdoc and b.pk_defdoclist = 'HI000000000000000011'");
					ht.put("v_hr_psndoc.marital", "v_hr_psndoc.marital");
				}
				conditions[i].setFieldCode("b.doc" + fieldCode.substring("v_hr_psndoc.marital".length()));
			} else if (fieldCode.indexOf("v_hr_psndoc.health") >= 0) {
				if (ht.get("v_hr_psndoc.health") == null) {
					tableStr
					.append(" left outer join bd_defdoc c on bd_psnbasdoc.health = c.pk_defdoc and c.pk_defdoclist = 'HI000000000000000010'");
					ht.put("v_hr_psndoc.health", "v_hr_psndoc.health");
				}
				conditions[i].setFieldCode("c.doc" + fieldCode.substring("v_hr_psndoc.health".length()));
			} else if (fieldCode.indexOf("v_hr_psndoc.nativeplace") >= 0) {
				if (ht.get("v_hr_psndoc.nativeplace") == null) {
					tableStr
					.append(" left outer join bd_defdoc d on bd_psnbasdoc.nativeplace = d.pk_defdoc and d.pk_defdoclist = '0001PLY0000000000007'");
					ht.put("v_hr_psndoc.nativeplace", "v_hr_psndoc.nativeplace");
				}
				conditions[i].setFieldCode("d.doc" + fieldCode.substring("v_hr_psndoc.nativeplace".length()));
			} else if (fieldCode.indexOf("v_hr_psndoc.polity") >= 0) {
				if (ht.get("v_hr_psndoc.polity") == null) {
					tableStr
					.append(" left outer join bd_defdoc e on bd_psnbasdoc.polity = e.pk_defdoc and e.pk_defdoclist = 'HI000000000000000028'");
					ht.put("v_hr_psndoc.polity", "v_hr_psndoc.polity");
				}
				conditions[i].setFieldCode("e.doc" + fieldCode.substring("v_hr_psndoc.polity".length()));
			} else if (fieldCode.indexOf("v_hr_psndoc.country") >= 0) {
				if (ht.get("v_hr_psndoc.country") == null) {
					tableStr
					.append(" left outer join bd_defdoc f on bd_psnbasdoc.country = f.pk_defdoc and f.pk_defdoclist = 'HI000000000000000002'");
					ht.put("v_hr_psndoc.country", "v_hr_psndoc.country");
				}
				conditions[i].setFieldCode("f.doc" + fieldCode.substring("v_hr_psndoc.country".length()));
			} else if (fieldCode.indexOf("v_hr_psndoc.titletechpost") >= 0) {
				if (ht.get("v_hr_psndoc.titletechpost") == null) {
					tableStr
					.append(" left outer join bd_defdoc g on bd_psnbasdoc.titletechpost = g.pk_defdoc and g.pk_defdoclist = 'HI000000000000000025'");
					ht.put("v_hr_psndoc.titletechpost", "v_hr_psndoc.titletechpost");
				}
				conditions[i].setFieldCode("g.doc" + fieldCode.substring("v_hr_psndoc.titletechpost".length()));
			} else if (fieldCode.indexOf("v_hr_psndoc.characterrpr") >= 0) {
				if (ht.get("v_hr_psndoc.characterrpr") == null) {
					tableStr
					.append(" left outer join bd_defdoc h on bd_psnbasdoc.characterrpr = h.pk_defdoc and h.pk_defdoclist = 'HI000000000000000027'");
					ht.put("v_hr_psndoc.characterrpr", "v_hr_psndoc.characterrpr");
				}
				conditions[i].setFieldCode("h.doc" + fieldCode.substring("v_hr_psndoc.characterrpr".length()));
			} else if (fieldCode.indexOf("v_hr_psndoc.recruitresource") >= 0) {
				if (ht.get("v_hr_psndoc.recruitresource") == null) {
					tableStr
					.append(" left outer join bd_defdoc k on bd_psndoc.recruitresource = k.pk_defdoc and k.pk_defdoclist = 'HI000000000000000026'");
					ht.put("v_hr_psndoc.recruitresource", "v_hr_psndoc.recruitresource");
				}
				conditions[i].setFieldCode("k.doc" + fieldCode.substring("v_hr_psndoc.recruitresource".length()));
			} else {
				String table = fieldCode.substring(0, index);
				if (table.equalsIgnoreCase("hi_psndoc_deptchg") && flag == 0) {
					if (ht.get("deptchg") == null) {
						tableStr.append(" inner join ");
						tableStr.append(table);
						tableStr.append(" deptchg on hi_psndoc_deptchg.pk_psndoc = ");
						tableStr.append("deptchg");
						tableStr.append(".pk_psndoc and deptchg.jobtype=0 ");
						ht.put("deptchg", "deptchg");
					}
					conditions[i].setFieldCode("deptchg" + fieldCode.substring(index));
				} else if (table.equalsIgnoreCase("hi_psndoc_part")) {
					if (ht.get(table) == null) {
						tableStr.append(" inner join ");
						tableStr.append("hi_psndoc_deptchg ");
						tableStr.append("hi_psndoc_part on hi_psndoc_deptchg.pk_psndoc = ");
						tableStr.append("hi_psndoc_part");
						tableStr.append(".pk_psndoc and hi_psndoc_part.jobtype>0 and hi_psndoc_part.bendflag='N'");
						ht.put("hi_psndoc_part", "hi_psndoc_part");
					}
				} else {
					if (ht.get(table) == null) {
						tableStr.append(" inner join ");
						tableStr.append(table);
						String psnpk = "pk_psndoc"; // ///如果是非跟踪集需要使用人员基本表主键
						if (!checkTraceTable(table)) {
							psnpk = "pk_psnbasdoc";
						}
						if (flag == 0) {
							tableStr.append(" on hi_psndoc_deptchg." + psnpk + " = ");
						} else {
							tableStr.append(" on bd_psndoc." + psnpk + " = ");
						}
						tableStr.append(table);
						tableStr.append("." + psnpk);
						ht.put(table, table);
					}
				}
			}
		}
		return tableStr.toString();
	}

	/**
	 * 传入新查询窗口的tables数组，根据个table表名，关联表。 替换原getTableStr方法。要求查询模板条件中使用defdoc的字段对应的表别名要有规则。
	 * 
	 * @param tables
	 * @param flag
	 *            =0表示不需再拼hi_psndoc_deptchg。（在职）
	 * @return
	 */
	public String getTableStr_newDLG(String[] tables, int flag) {
		Hashtable ht = new Hashtable();// 不需再关联的表
		if (flag == 0) {
			ht.put("hi_psndoc_deptchg", "deptchg");
		}
		ht.put("bd_psndoc", "bd_psndoc");
		ht.put("bd_psnbasdoc", "bd_psnbasdoc");
		ht.put("bd_deptdoc", "bd_deptdoc");
		ht.put("bd_corp", "bd_corp");
		ht.put("bd_psncl", "bd_psncl");
		ht.put("om_job", "om_job");
		ht.put("om_duty", "om_duty");
		ht.put("hi_psndoc_keypsn", "hi_psndoc_keypsn");// 关键人员管理节点也会用到此方法。

		Hashtable<String, String> ht_defdocnametocode = new Hashtable<String, String>();// defdoc信息集的表别名对应code
		ht_defdocnametocode.put("table_nationality", "HI000000000000000003");
		ht_defdocnametocode.put("table_marital", "HI000000000000000011");
		ht_defdocnametocode.put("table_health", "HI000000000000000010");
		ht_defdocnametocode.put("table_nativeplace", "0001PLY0000000000007");
		ht_defdocnametocode.put("table_polity", "HI000000000000000028");
		ht_defdocnametocode.put("table_country", "HI000000000000000002");
		ht_defdocnametocode.put("table_titletechpost", "HI000000000000000025");
		ht_defdocnametocode.put("table_characterrpr", "HI000000000000000027");
		ht_defdocnametocode.put("table_recruitresource", "HI000000000000000026");

		StringBuffer tableStr = new StringBuffer();
		if (tables == null || tables.length == 0) {
			return "";
		}
		for (String table : tables) {
			if (ht.get(table) == null && ht_defdocnametocode.containsKey(table)) {
				String joinfield = table.substring(6);// 去掉前面的”table_“既是字段名。
				tableStr.append(" left outer join bd_defdoc " + table + " on bd_psnbasdoc." + joinfield + " = " + table
						+ ".pk_defdoc and " + table + ".pk_defdoclist = '" + ht_defdocnametocode.get(table) + "' ");
				ht.put(table, table);
			} else {

				if (table.equalsIgnoreCase("hi_psndoc_part")) {
					if (ht.get(table) == null) {
						tableStr.append(" inner join ");
						// 关于为何不用左关联：uap转换有同名表left outer join时出来的sql是错误的。此处改为内关联没有影响。
						// tableStr.append(" left outer join ");
						tableStr.append("hi_psndoc_deptchg ");
						if (flag == 0) {
							tableStr.append("hi_psndoc_part on (hi_psndoc_deptchg.pk_psndoc = ");
						} else {
							tableStr.append("hi_psndoc_part on (bd_psndoc.pk_psndoc = ");
						}
						tableStr.append("hi_psndoc_part");
						tableStr.append(".pk_psndoc and hi_psndoc_part.jobtype>0 and hi_psndoc_part.bendflag='N')");
						ht.put("hi_psndoc_part", "hi_psndoc_part");
					}
				} else {
					if (ht.get(table) == null) {
						// tableStr.append(" inner join ");
						tableStr.append(" left outer join ");
						tableStr.append(table);
						String psnpk = "pk_psndoc"; // ///如果是非跟踪集需要使用人员基本表主键
						if (!checkTraceTable(table)) {
							psnpk = "pk_psnbasdoc";
						}
						if (flag == 0) {
							tableStr.append(" on hi_psndoc_deptchg." + psnpk + " = ");
						} else {
							tableStr.append(" on bd_psndoc." + psnpk + " = ");
						}
						tableStr.append(table);
						tableStr.append("." + psnpk);
						//2011.08.29 还原 最新硬化条件，孙其涛
//						//sqt      包含几个表（'薪资','岗位聘任','行政职务聘任','学历','合同','考核','专业技术职业') 中  只要用到这个表就加一个最新的字段
//						if("hi_psndoc_grpdef2,hi_psndoc_grpdef1,hi_psndoc_grpdef10,hi_psndoc_edu,hi_psndoc_ctrt,hi_psndoc_ass,hi_psndoc_spetech".contains(table)){
//						tableStr.append(" and "+table+".lastflag='Y' ");
//						}else if("@ v_hr_psndoc_edu**".contains(table)){
//						tableStr.append(" and "+table+".lasteducation='Y' ");
//						}
						// 若是hi_psndoc_edu，还需关联最高学历条件
						//fengwei 2010-11-8 如果查询模板没有选择最高学历条件，则不加
//						if ("hi_psndoc_edu".equalsIgnoreCase(table)) {
//						tableStr.append(" and hi_psndoc_edu.lasteducation = 'Y' ");
//						}

						ht.put(table, table);
					}
				}
			}
		}
		return tableStr.toString();
	}

	/**
	 * 更新关联的辅助人员信息。 --解决V30中问题编码为200507111204423395 和 200507210913444548中的同步问题
	 * 
	 * @exception BusinessException
	 *                异常说明。
	 */
	public GeneralVO updateAccRel(String[] fields, Hashtable map, String tableCode, GeneralVO accpsndocVO)
	throws BusinessException {
		try {
			PsnInfDMO psnInfDMO = new PsnInfDMO();
			ISetdict setdictDMO = PubDelegator.getISetdict();
			for (int i = 0; i < fields.length; i++) {
				// 映射表存取主键为"table.field"
				String key = tableCode + "." + fields[i];
				String acc_fldcode = (String) map.get(key);
				String chkformula = null;
				String strItemInf = null;
				if (acc_fldcode != null) {
					chkformula = setdictDMO.findChkFormula(tableCode, fields[i]);
					try {
						strItemInf = psnInfDMO.findItemInf(tableCode, fields[i], (String) accpsndocVO
								.getAttributeValue("pk_psnbasdoc"), chkformula);
					} catch (Exception e) {
						String eMsg = NCLangResOnserver.getInstance().getStrByID("600704", "UPT600704-000247");/*
						 * @res
						 * "辅助项条件设置错误!"
						 */
						throw new BusinessException(eMsg);// "辅助项条件设置错误!"
					}
					if (strItemInf == null) { // 特殊处理空值
						strItemInf = "#null#";
					}
					accpsndocVO.setAttributeValue(acc_fldcode, strItemInf);
				}
			}
		} catch (Exception e) {
			throwBusinessException(e);
		}
		return accpsndocVO;
	}

	/**
	 * 应聘信息中的档案所在地和档案记录子集转入人员信息的档案记录子集
	 * 
	 * @author lvgd1
	 * @exception BusinessException
	 *                异常说明。
	 */
	public void turnBDIntoPsn(String pk_psnbasdoc, String pk_psndoc) throws BusinessException {
		GeneralVO voTemp = new GeneralVO();
		voTemp.setAttributeValue("pk_psnbasdoc", pk_psnbasdoc);
		voTemp.setAttributeValue("pk_psndoc", pk_psndoc);
		try {
			PsnInfDMO psnInfDMO = new PsnInfDMO();
			Hashtable map = psnInfDMO.getTempRelationMap();
			Set keys = map.keySet();
			ISetdict setdictDMO = PubDelegator.getISetdict();
			for (Iterator key = keys.iterator(); key.hasNext();) {
				String keyTable = (String) key.next();
				String tableCode = null;
				String field = null;
				String chkformula = null;
				String strItemInf = null;
				if (keyTable != null && !"".equals(keyTable)) {
					int index = keyTable.indexOf(".");
					tableCode = keyTable.substring(0, index);
					field = keyTable.substring(index + 1, keyTable.length());
				}
				String acc_fldcode = (String) map.get(keyTable);
				if (acc_fldcode != null) {
					chkformula = setdictDMO.findChkFormula(tableCode, field);
					try {
						strItemInf = psnInfDMO.findItemInf(tableCode, field, (String) voTemp
								.getAttributeValue("pk_psnbasdoc"), chkformula);
					} catch (Exception e) {
						String eMsg = NCLangResOnserver.getInstance().getStrByID("600704", "UPT600704-000247");/*
						 * @res
						 * "辅助项条件设置错误!"
						 */
						throw new BusinessException(eMsg);// "辅助项条件设置错误!"
					}
					if (strItemInf == null) { // 特殊处理空值
						strItemInf = "#null#";
					}
					voTemp.setAttributeValue(acc_fldcode, strItemInf);
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throwBusinessException(e);
		}
		HashMap hmRelVO = new HashMap();
		String[] fields = voTemp.getAttributeNames();
		GeneralVO psnvo = new GeneralVO();
		GeneralVO baspsnvo = new GeneralVO();
		psnvo.setAttributeValue("pk_psnbasdoc", pk_psnbasdoc);
		psnvo.setAttributeValue("pk_psndoc", pk_psndoc);
		baspsnvo.setAttributeValue("pk_psnbasdoc", pk_psnbasdoc);
		baspsnvo.setAttributeValue("pk_psndoc", pk_psndoc);
		boolean isRelPsndoc = false;
		boolean isRelBasPsndoc = false;
		for (int i = 0; i < fields.length; i++) {
			if (fields[i].indexOf(".") < 0) {
				continue;
			}
			String table = fields[i].substring(0, fields[i].indexOf("."));
			if ("bd_psndoc".equalsIgnoreCase(table)) {
				String field = fields[i].substring(fields[i].indexOf(".") + 1);
				psnvo.setAttributeValue(field, voTemp.getAttributeValue(fields[i]));
				isRelPsndoc = true;
			} else if ("bd_psnbasdoc".equalsIgnoreCase(table)) {
				String field = fields[i].substring(fields[i].indexOf(".") + 1);
				baspsnvo.setAttributeValue(field, voTemp.getAttributeValue(fields[i]));
				isRelBasPsndoc = true;
			}
		}
		if (isRelPsndoc)
			hmRelVO.put("bd_psndoc", psnvo);
		if (isRelBasPsndoc)
			hmRelVO.put("bd_psnbasdoc", baspsnvo);
		updateRelTable(hmRelVO);
	}

	/**
	 * V31SP1 照片导出 功能。 创建日期：(2005-4-28 15:23:53)
	 * 
	 * @return nc.vo.hi.hi_301.GeneralVO[]
	 * @param psnPK
	 *            java.lang.String
	 * @exception BusinessException
	 *                异常说明。
	 */
	public GeneralVO[] queryByPsnPK(String pk_corp, String psnPK) throws BusinessException {
		GeneralVO[] vos = null;
		try {
			String sql = "select pk_psnbasdoc,photo from bd_psnbasdoc where pk_psnbasdoc='" + psnPK
			+ "' and photo is not null ";
			vos = new PsnInfDMO().queryBySql(sql);
		} catch (Exception e) {
			throwBusinessException(e);
		}
		return vos;
	}

	/**
	 * V35 add 查询项目设置列表
	 * 
	 * @param pk_corp
	 *            登陆公司
	 * @param funcode
	 *            登陆节点号
	 * @param queryScope
	 *            查询范围 all为所有 seted为设置过得
	 * @return
	 * @throws BusinessException
	 */
	public GeneralVO[] queryListItem(String pk_corp, String funcode, String queryScope) throws BusinessException {
		GeneralVO[] result = null;
		try {
			PsnInfDMO dmo = new PsnInfDMO();
			result = dmo.queryListItem(pk_corp, funcode, queryScope);
		} catch (Exception e) {
			throwBusinessException(e);
		}
		return result;
	}

	/**
	 * V35 add 保存批量数据
	 * 
	 * @param data
	 * @param tableCode
	 * @return
	 * @throws BusinessException
	 */
	public String[] insertTable(nc.vo.pub.CircularlyAccessibleValueObject[] data, String tableCode)
	throws BusinessException {
		String[] returnpks = null;
		try {
			PsnInfDMO dmo = new PsnInfDMO();
			if ("hi_itemset".equalsIgnoreCase(tableCode)) {
				dmo.deleteTable(data, tableCode);
				if (data.length > 1) {// 表头数据
					returnpks = new String[data.length - 1];
					returnpks = dmo.insertHiItemSet(data, tableCode);
				}
			} else {
				returnpks = dmo.insertSubTable(tableCode, data, "pk_psndoc_sub");
			}

		} catch (Exception e) {
			throwBusinessException(e);
		}
		return returnpks;
	}

	/**
	 * 把引用的人员转入到管理档案中
	 * 
	 * @param pk_psnbasdoc
	 * @return
	 */
	public int addRefEmployees(nc.vo.hi.hi_301.GeneralVO[] psnvos) throws BusinessException {
		int rowcount = 0;
		try {

			PsnInfDMO dmo = new PsnInfDMO();
			// 判断人员是否已经离职
			dmo.checkPsnHaveChanged(psnvos);
			dmo.addRefEmployees(psnvos, null);
			// add by sunxj 2010-04-27 H型接口插件 start
			// 增加人员增量数据 引用人员
			UFBoolean waHrFiFlagTemp = PubDelegator.getIParValue().getParaBoolean(null, "WA-HRFI");
			boolean waHrFiFlag = waHrFiFlagTemp == null ? false : waHrFiFlagTemp.booleanValue();
			if (waHrFiFlag) {
				insertPsndocAdd(dmo.queryRefPsn(psnvos));
			}
			// add by sunxj 2010-04-27 H型接口插件 end
			rowcount = dmo.deleteEmployeeRef(psnvos, null);
			if (psnvos != null && psnvos.length > 0) {
				String pk_corp = (String) psnvos[0].getAttributeValue("pk_corp");
				HIDelegator.getIPersonADDSV().operatePersonInfo(getPKsFromVOS(psnvos), IPersonADDSV.MODIFY, pk_corp);
			}
		} catch (Exception e) {
			throwBusinessException(e);
		}

		return rowcount;
	}

	public String[] getPKsFromVOS(nc.vo.hi.hi_301.GeneralVO[] psnvos) {
		if (psnvos == null || psnvos.length < 1)
			return null;
		String[] pks = new String[psnvos.length];
		for (int i = 1; i < psnvos.length; i++) {
			pks[i] = (String) psnvos[i].getAttributeValue("pk_psndoc");
		}
		return pks;
	}

	/**
	 * 
	 */
	public nc.vo.hi.hi_301.GeneralVO[] queryRefEmployees(String strParam, Integer type, String power)
	throws BusinessException {// throws

		nc.vo.hi.hi_301.GeneralVO[] gvos = null;
		try {
			PsnInfDMO dmo = new PsnInfDMO();
			gvos = dmo.queryRefEmployees(strParam, type, power);

		} catch (Exception e) {
			throwBusinessException(e);
		}
		return gvos;
	}

	/**
	 * 
	 */
	public int queryPsnCountByPk(String table, String pk, String pk_corp) throws BusinessException {
		try {
			StringBuffer regBuf = null;

			if (table.equalsIgnoreCase("bd_deptdoc")) {
				// v52 add by zhyan
				PsnTypeWorkoutRegVO[] regVOs = PubDelegator.getICorpWorkout().queryRegVOArrayByCondition(pk_corp, null);
				if (regVOs == null || regVOs.length <= 0) {
					regBuf = new StringBuffer(" hi_psndoc_deptchg.pk_psncl is null ");
				} else {
					regBuf = new StringBuffer(" hi_psndoc_deptchg.pk_psncl in ( ");
					for (int i = 0; i < regVOs.length; i++) {
						regBuf.append("'");
						regBuf.append(regVOs[i].getPk_psntype());
						regBuf.append("'");
						if (i < (regVOs.length - 1)) {
							regBuf.append(", ");
						}
					}
					regBuf.append(" ) ");
				}

			} else if (table.equalsIgnoreCase("om_job")) {
				// v52 add by zhyan
				PsnTypeWorkoutRegVO[] regVOs = PubDelegator.getICorpWorkout().queryRegVOArrayByCondition(pk_corp, null);
				if (regVOs == null || regVOs.length <= 0) {
					regBuf = new StringBuffer(" bd_psndoc.pk_psncl is null ");
				} else {
					regBuf = new StringBuffer(" bd_psndoc.pk_psncl in ( ");
					for (int i = 0; i < regVOs.length; i++) {
						regBuf.append("'");
						regBuf.append(regVOs[i].getPk_psntype());
						regBuf.append("'");
						if (i < (regVOs.length - 1)) {
							regBuf.append(", ");
						}
					}
					regBuf.append(" ) ");
				}
			}

			String condition = regBuf.toString();
			return new PsnInfDMO().queryPsnCountByPk(table, pk, condition);

		} catch (Exception e) {
			throwBusinessException(e);
		}
		return -1;
	}

	public nc.vo.pub.msg.UserNameObject[] getPowerUserid(String pk_corp, String funcode) throws BusinessException {
		nc.vo.pub.msg.UserNameObject[] userObjs = null;
		try {
			PsnInfDMO dmo = new PsnInfDMO();
			userObjs = dmo.getPowerUserid(pk_corp, funcode);
		} catch (Exception e) {
			throwBusinessException(e);
		}
		return userObjs;
	}

	/**
	 * 
	 * @param pk_corp
	 * @return
	 * @throws BusinessException
	 */
	public UserNameObject[] getRecievers(String pk_corp, int usertype) throws BusinessException {
		UserNameObject[] uservos = null;

		try {
			PsnInfDMO dmo = new PsnInfDMO();
			String userids = dmo.getRecievers(pk_corp, usertype);
			String[] userid = null;
			Vector v = new Vector();
			if (userids != null) {
				if (userids.trim().length() > 20 && userids.indexOf(",") > 0) {
					userid = userids.split(",");
				} else {
					userid = new String[1];
					userid[0] = userids.trim();
				}
			}
			if (userid != null && userid.length > 0) {
				for (int i = 0; i < userid.length; i++) {
					UserVO tempuservo = SFServiceFacility.getIUserManageQuery().getUser(userid[i]);
					if (tempuservo == null)
						continue;
					UserNameObject usernamevo = new UserNameObject(tempuservo.getUserName());
					usernamevo.setUserCode(tempuservo.getUserCode());
					usernamevo.setUserPK(tempuservo.getPrimaryKey());
					v.addElement(usernamevo);
				}
			}
			uservos = new UserNameObject[v.size()];
			if (v.size() > 0) {
				v.copyInto(uservos);
			}
		} catch (Exception e) {
			throwBusinessException(e);
		}
		return uservos;
	}

	public nc.vo.pub.msg.UserNameObject[] getUserObj(String[] userids) throws BusinessException {
		nc.vo.pub.msg.UserNameObject[] userObjs = null;
		try {
			PsnInfDMO dmo = new PsnInfDMO();
			userObjs = dmo.getUserObj(userids);
		} catch (Exception e) {
			throwBusinessException(e);
		}
		return userObjs;
	}

	public String dataUniqueValidate(String pk_corp, nc.vo.bd.psndoc.PsndocConsItmVO[] conitmUniqueFields,
			CircularlyAccessibleValueObject psnbasDocVO) throws BusinessException {
		String result = null;
		try {

			PsnInfDMO dmo = new PsnInfDMO();
			result = dmo.dataUniqueValidate(pk_corp, conitmUniqueFields, psnbasDocVO);

		} catch (Exception e) {
			throwBusinessException(e);
		}
		return result;
	}

	/**
	 * 检验返聘再聘人员
	 * 
	 * @param pk_corp
	 * @param conitmUniqueFields
	 * @param psnbasDocVO
	 * @return
	 * @throws BusinessException
	 */
	public GeneralVO checkRehirePerson(String pk_corp, nc.vo.bd.psndoc.PsndocConsItmVO[] conitmUniqueFields,
			CircularlyAccessibleValueObject psnbasDocVO, int returntype) throws BusinessException {
		GeneralVO result = null;
		try {

			PsnInfDMO dmo = new PsnInfDMO();
			result = dmo.checkRehirePerson(pk_corp, conitmUniqueFields, psnbasDocVO, returntype);

		} catch (Exception e) {
			throwBusinessException(e);
		}
		return result;
	}

	/**
	 * 查询选中人员的 当前子集数据 返回的 key = pk_psnbasdoc value = GeneralVO[] 的HashMap
	 * 
	 * @param vPkPsnbasdocs
	 * @param tablecode
	 * @return
	 * @throws BusinessException
	 */
	public HashMap querySubInfo(Vector vPkPsnbasdocs, String tablecode) throws BusinessException {
		HashMap hmData = new HashMap();
		try {

			PsnInfDMO dmo = new PsnInfDMO();

			for (int i = 0; i < vPkPsnbasdocs.size(); i++) {
				String pk_psnbasdoc = (String) vPkPsnbasdocs.elementAt(i);
				String sql = " select * from  " + tablecode + " where pk_psnbasdoc = '" + pk_psnbasdoc + "'";
				hmData.put(pk_psnbasdoc, dmo.queryBySql(sql));
			}

		} catch (Exception e) {
			throwBusinessException(e);
		}
		return hmData;
	}

	/*
	 * 检查是否跟踪信息集 2006-05-13 lyq add
	 */
	private boolean checkTraceTable(String tablename) {
		boolean result = false;
		if ("hi_psndoc_deptchg".indexOf(tablename) >= 0) {
			result = true;
		} else if ("hi_psndoc_ctrt".indexOf(tablename) >= 0) {
			result = true;
		} else if ("hi_psndoc_part".indexOf(tablename) >= 0) {
			result = true;
		} else if ("hi_psndoc_training".indexOf(tablename) >= 0) {
			result = true;
		} else if ("hi_psndoc_ass".indexOf(tablename) >= 0) {
			result = true;
		} else if ("hi_psndoc_retire".indexOf(tablename) >= 0) {
			result = true;
		} else if ("hi_psndoc_orgpsn".indexOf(tablename) >= 0) {
			result = true;
			// } else if ("hi_psndoc_tryout".indexOf(tablename) >= 0) {
			// result = true;
		} else if ("hi_psndoc_psnchg".indexOf(tablename) >= 0) {
			result = true;
		} else if ("hi_psndoc_dimission".indexOf(tablename) >= 0) {
			result = true;
		}
		return result;
	}

	public boolean checkUserClerk(String pk_psndoc, String pkCorp) throws BusinessException {
		boolean bool = false;
		try {
			PsnInfDMO dmo = new PsnInfDMO();
			bool = dmo.checkUserClerk(pk_psndoc, pkCorp);
		} catch (Exception e) {
			throwBusinessException(e);
		}
		return bool;
	}

	/**
	 * 更新主表的显示顺序字段 bd_psndoc.showorder
	 * 
	 * @param pk_psndocs
	 *            [] 要更新的人员主键数组
	 * @throws BusinessException
	 */
	public void updateShoworder(String[] pk_psndocs, HashMap psnshoworder) throws BusinessException {
		try {
			PsnInfDMO dmo = new PsnInfDMO();
			dmo.updateShoworder(pk_psndocs, psnshoworder);
		} catch (Exception e) {
			throwBusinessException(e);
		}
	}

	/**
	 * 得到在指定公司兼职的人员所在的公司
	 * 
	 * @param sql
	 * @return
	 * @throws BusinessException
	 */
	public String[] queryDeptPowerBySql(String sql) throws BusinessException {
		String[] pk_deptdocs = null;
		try {
			PsnInfDMO dmo = new PsnInfDMO();
			pk_deptdocs = dmo.queryDeptPowerBySql(sql);
		} catch (Exception e) {
			// throw new BusinessException(e.getMessage());
			throwBusinessException(e);
		}
		return pk_deptdocs;
	}

	/**
	 * 检查是否存在记录
	 */
	public boolean isRecordExist(String sql) throws BusinessException {
		try {
			PsnInfDMO dmo = new PsnInfDMO();
			return dmo.isRecordExist(sql);
		} catch (Exception e) {
			throwBusinessException(e);
		}

		return false;
	}

	/**
	 * 检查业务子集是否允许查看历史
	 */
	public boolean isTraceTableLookHistory(String tablename) throws BusinessException {
		try {
			PsnInfDMO dmo = new PsnInfDMO();
			return dmo.isTraceTableLookHistory(tablename);
		} catch (Exception e) {
			throwBusinessException(e);
		}

		return false;
	}

	public GeneralVO queryPsnInfo(String pk_psndoc, String id, String psnname) throws BusinessException {

		try {
			PsnInfDMO dmo = new PsnInfDMO();
			return dmo.queryPsnInfo(pk_psndoc, id, psnname);
		} catch (Exception e) {
			throwBusinessException(e);
		}
		return null;
	}

	// public Object getParam(Class cls, String unitcode, String initcode)
	// throws BusinessException {
	//
	// try {
	// PsnInfDAO dao = new PsnInfDAO();
	// return dao.getParam(cls, unitcode, initcode);
	// } catch (Exception e) {
	// throwBusinessException(e);
	// }
	// return null;
	// }

	/**
	 * 
	 */
	public void deletePsnData(String tableCode, String pk_psndoc) throws BusinessException {

		try {
			PsnInfDMO dmo = new PsnInfDMO();
			dmo.deletePsnData(tableCode, pk_psndoc);
		} catch (Exception e) {
			throwBusinessException(e);
		}
	}

	/**
	 * 发送mail
	 * 
	 * @param subject
	 * @param content
	 * @param emailAddress
	 */
	public void sendMail(String subject, String content, String[] emailAddress) throws BusinessException {

		nc.vo.pub.msg.DefaultSMTP smtp = null;
		IPFConfig iPfConfig = (IPFConfig) NCLocator.getInstance().lookup(IPFConfig.class.getName());
		SysMessageParam smp = iPfConfig.getSysMsgParam();

		if (smp == null) {
			// throw new BusinessException("查询不到邮件服务器配置信息！");
			throw new BusinessException(NCLangResOnserver.getInstance().getStrByID("600700", "UPP600700-000240")/*
			 * @res
			 * "查询不到邮件服务器配置信息！"
			 */);
		} else {
			smtp = smp.getSmtp();
		}

		if (smtp == null) {
			// throw new BusinessException( "查询不到邮件服务器配置信息！");
			throw new BusinessException(NCLangResOnserver.getInstance().getStrByID("600700", "UPP600700-000240")/*
			 * @res
			 * "查询不到邮件服务器配置信息！"
			 */);
		}
		StringBuffer msgBuf = new StringBuffer();
		// 是否存在无效邮件
		boolean isUnEff = false;

		try {
			// 取得邮件配置信息
			String strMailPwd = smtp.getPassword();
			nc.vo.pub.mail.MailInfo mi = new nc.vo.pub.mail.MailInfo();
			mi.setSmtpHost(smtp.getSmtp());
			mi.setSender(smtp.getSender());
			mi.setUserName(smtp.getUser());
			mi.setPassword(strMailPwd.length() == 0 ? "" : new nc.vo.framework.rsa.Encode().decode(strMailPwd));
			mi.setAttachments(null);
			// 发送邮件
			for (int i = 0; i < emailAddress.length; i++) {
				mi.setSubject(subject);
				mi.setCc(null);
				String email = emailAddress[i].trim();
				mi.setReceiver(email);
				mi.setMessage(content);
				SFServiceFacility.getMailService().sendMail(mi);
			}

		} catch (Exception e) {
			throwBusinessException(e);
		}
	}

	/**
	 * 按照条件多表关联查询入职申请单
	 * 
	 * @return
	 */
	public DocApplyHVO[] queryDocApplyBillByCon(String tableandwhere) throws BusinessException {
		DocApplyHVO[] docapplyhvos = null;
		try {
			PsnInfDMO dmo = new PsnInfDMO();
			docapplyhvos = dmo.queryDocApplyBillByCon(tableandwhere);
		} catch (Exception e) {
			throwBusinessException(e);
		}

		return docapplyhvos;

	}

	/**
	 * 修改人员状态
	 * 
	 * @param pk_psndocs
	 * @throws BusinessException
	 */
	public void updatePsnState(String[] pk_psndocs, int state) throws BusinessException {

		try {
			PsnInfDMO dmo = new PsnInfDMO();
			dmo.updatePsnState(pk_psndocs, state);
		} catch (Exception e) {
			throwBusinessException(e);
		}

	}

	/**
	 * 校验申请单保存时的人员信息
	 * 
	 * @param pk_psndocs
	 * @param pk_docapply_b
	 * @return
	 * @throws BusinessException
	 */
	public String checkApplyPsn(String[] pk_psndocs, HashMap bodypkMap) throws BusinessException {
		String resultmessage = null;
		try {
			PsnInfDMO dmo = new PsnInfDMO();
			Vector v = new Vector();
			String indocmessage = "";
			String applymessage = "";
			if (pk_psndocs != null && pk_psndocs.length > 0) {
				for (int i = 0; i < pk_psndocs.length; i++) {// 首先判断是否已经转入人员档案
					String indocresult = dmo.checkIndoc(pk_psndocs[i]);
					if (indocresult != null) {
						indocmessage += indocresult + ",";
					} else {
						v.addElement(pk_psndocs[i]);
					}
				}
				if (!"".equalsIgnoreCase(indocmessage)) {
					resultmessage = indocmessage.substring(0, indocmessage.length() - 1)
					+ NCLangResOnserver.getInstance().getStrByID("600700", "UPP600700-000241")// " 已转入人员档案! \n"
					;
				}
				if (v.size() > 0) {// 其次判断是否在其他单据中存在
					String[] applypsn = new String[v.size()];
					v.copyInto(applypsn);
					for (int j = 0; j < applypsn.length; j++) {
						String pk_docapply_b = null;
						Object b = bodypkMap.get(pk_psndocs[j]);
						if (b != null) {
							pk_docapply_b = (String) b;
						}
						String existapply = dmo.checkApplyPsn(pk_psndocs[j], pk_docapply_b);
						if (existapply != null) {
							applymessage += existapply + ",";
						}
					}
					if (!"".equalsIgnoreCase(applymessage)) {
						if (resultmessage != null) {
							resultmessage += applymessage.substring(0, applymessage.length() - 1)
							+ NCLangResOnserver.getInstance().getStrByID("600700", "UPP600700-000242")// " 已存在其他单据中!"
							;
						} else {
							resultmessage = applymessage.substring(0, applymessage.length() - 1)
							+ NCLangResOnserver.getInstance().getStrByID("600700", "UPP600700-000242")// " 已存在其他单据中!"
							;
						}
					}
				}

			} else {
				return null;
			}

		} catch (Exception e) {
			throwBusinessException(e);
		}
		return resultmessage;
	}

	public void afterInsertChild(String tablecode, String[] pk_psndoc_subs) throws BusinessException {
		try {
			PsnInfDMO dmo = new PsnInfDMO();
			dmo.afterInsertChild(tablecode, pk_psndoc_subs);
		} catch (Exception e) {
			throwBusinessException(e);
		}

	}

	public void deleteChildSet(String tablecode, String[] pk_psndoc_sub) throws BusinessException {
		try {
			PsnInfDMO dmo = new PsnInfDMO();
			dmo.deleteChildSet(tablecode, pk_psndoc_sub);
		} catch (Exception e) {
			throwBusinessException(e);
		}

	}

	/**
	 * v55 add 处理自助用户
	 */
	public Hashtable isNeedSelfUser(GeneralVO[] vos) throws BusinessException {
		Hashtable isNeedSelfUser = new Hashtable();
		try {
			PsnInfDMO dmo = new PsnInfDMO();
			isNeedSelfUser = dmo.isNeedSelfUser(vos);
		} catch (Exception e) {
			throwBusinessException(e);
		}
		return isNeedSelfUser;
	}

	/**
	 * 得到对应公司的人员类别权限sql V55 add
	 * 
	 * @param userid
	 * @param pk_corps
	 */
	private String getPsnclPowerSqls(String depttable, String userid, String[] pk_corps) throws BusinessException {
		IDataPowerService datapower = ((IDataPowerService) NCLocator.getInstance().lookup(
				IDataPowerService.class.getName()));
		String powersql = "";
		try {
			if (pk_corps != null && pk_corps.length > 0) {
				for (int i = 0; i < pk_corps.length; i++) {
					boolean useDeptPower = datapower.isUsedDataPower("bd_psncl", "人员类别", pk_corps[i]);
					String subsql = datapower.getSubSql("bd_psncl", "人员类别", userid, pk_corps[i]);

					if (useDeptPower && subsql != null) {
						powersql += "(" + depttable.trim() + ".pk_corp ='" + pk_corps[i] + "' and " + depttable.trim()
						+ ".pk_psncl in (" + subsql + ")) or ";
					} else {
						powersql += "(" + depttable.trim() + ".pk_corp ='" + pk_corps[i] + "') or ";
					}
				}
			}
		} catch (Exception e) {
			throwBusinessException(e);
		}
		if (powersql.trim().length() > 15) {
			powersql = powersql.trim().substring(0, powersql.trim().length() - 2);
		}
		return powersql;
	}

	/**
	 * 得到对应公司的人员类别权限sql V55 add
	 * 
	 * @param userid
	 * @param pk_corps
	 */
	private String getPsnclPowerSql(String depttable, String userid, String pk_corp) throws BusinessException {
		IDataPowerService datapower = ((IDataPowerService) NCLocator.getInstance().lookup(
				IDataPowerService.class.getName()));
		String powersql = "";
		try {
			boolean useDeptPower = datapower.isUsedDataPower("bd_psncl", "人员类别", pk_corp);
			String subsql = datapower.getSubSql("bd_psncl", "人员类别", userid, pk_corp);
			if (useDeptPower && subsql != null) {
				powersql += "(" + depttable.trim() + ".pk_psncl in (" + subsql + "))  ";
			}
		} catch (Exception e) {
			throwBusinessException(e);
		}

		return powersql;
	}

	/**
	 * 保存多条子集记录信息
	 * 
	 * @param psndocVO
	 * @return
	 * @throws BusinessException
	 */
	public void saveSubSetInfos(String tableCode, String pk_psndoc, GeneralVO[] vos, String[] delPkPsndocSubs)
	throws BusinessException {
		try {
			PsnInfDMO dmo = new PsnInfDMO();
			GeneralVO vo = null;
			// 删除子集记录信息
			if (delPkPsndocSubs != null && delPkPsndocSubs.length > 0) {
				for (int i = 0; i < delPkPsndocSubs.length; i++) {
					dmo.deleteData(tableCode, "pk_psndoc_sub = '" + delPkPsndocSubs[i] + "'");
				}
			}
			if (vos == null || vos.length < 1)
				return;
			for (int i = 0; i < vos.length; i++) {
				vo = vos[i];
				switch (vo.getStatus()) {
				case VOStatus.NEW:
					// 新增子集记录信息
					dmo.insertTable(tableCode, vo, "pk_psndoc_sub");
					break;
				case VOStatus.UPDATED: {
					// 修改子集记录信息
					dmo.updateTable(tableCode, vo, "pk_psndoc_sub = '" + (String) vo.getAttributeValue("pk_psndoc_sub")
							+ "'");
					break;
				}
				default:
				}
				if (tableCode.equalsIgnoreCase("hi_psndoc_grpdef11")) {//增减变动也签编辑类别同步主表类别及归属
					if(vos[i].getAttributeValue("lastflag")!=null&&new UFBoolean(vos[i].getAttributeValue("lastflag").toString()).booleanValue()){
						String groupdef7 = (String)vos[i].getAttributeValue("$groupdef7");
						if(groupdef7!=null&&groupdef7.trim().length()>0){
							BaseDAO dao = new BaseDAO();
							PsnclVO pvo = (PsnclVO)dao.retrieveByPK(PsnclVO.class, groupdef7);
							String sql = " update bd_psndoc set psnclscope="+pvo.getPsnclscope()+",pk_psncl='"+groupdef7+"' where pk_psndoc='"+vos[i].getAttributeValue("pk_psndoc")+"' ";
							dao.executeUpdate(sql);
						}
					}
				}

			}

			if (tableCode.equalsIgnoreCase("hi_psndoc_ctrt")) {
				dmo.updateCtrt(pk_psndoc, new Boolean(true));
			}

			if (vos != null && vos.length > 0) {
				String pk_corp = (String) vos[0].getAttributeValue("pk_corp");
				HIDelegator.getIPersonADDSV().operatePersonInfo(new String[] { pk_psndoc }, IPersonADDSV.MODIFY,
						pk_corp);
			}
		} catch (Exception e) {
			throwBusinessException(e);
		}
	}

	/**
	 * 返回相应VO所有状态的值,一次循环提高效率。
	 * 
	 * @return java.util.Hashtable
	 * @param vos
	 *            GeneralVO[]
	 */
	private java.util.Hashtable getTableVOsHTWithStatus(GeneralVO[] vos) {
		Hashtable htVOStatus = new Hashtable();
		java.util.Vector vUpdate = new java.util.Vector();
		java.util.Vector vDelete = new java.util.Vector();
		java.util.Vector vNew = new java.util.Vector();

		for (int i = 0; i < vos.length; i++) {
			if (vos[i].getStatus() == nc.vo.pub.VOStatus.UPDATED)
				vUpdate.addElement(vos[i]);
			else if (vos[i].getStatus() == nc.vo.pub.VOStatus.DELETED)
				vDelete.addElement(vos[i]);
			else if (vos[i].getStatus() == nc.vo.pub.VOStatus.NEW)
				vNew.addElement(vos[i]);
		}
		htVOStatus.put("UPDATED", vUpdate);
		htVOStatus.put("DELETED", vDelete);
		htVOStatus.put("NEW", vNew);
		return htVOStatus;
	}

	/**
	 * 查询人员pk_psndoc信息表table的所有记录。 创建日期：(2004-5-13 17:00:55)
	 * 
	 * @return nc.vo.hi.hi_301.GeneralVO[]
	 * @param pk_psndoc
	 *            java.lang.String
	 * @param table
	 *            java.lang.String
	 * @exception BusinessException
	 *                异常说明。
	 */
	public GeneralVO[] queryMainPersonInfo(String pk, String pk_corp, String table, BusinessFuncParser_sql funcParser)
	throws BusinessException {
		GeneralVO[] psnList = null;
		try {
			// 缺省查询所有信息
			String select = "*";
			// 组织sql
			String sql = null;
			if ("bd_psndoc".equalsIgnoreCase(table) || "hi_psndoc_deptchg".equalsIgnoreCase(table)
					|| "hi_psndoc_ctrt".equalsIgnoreCase(table) || "hi_psndoc_part".equalsIgnoreCase(table)
					|| "hi_psndoc_training".equalsIgnoreCase(table) || "hi_psndoc_retire".equalsIgnoreCase(table)
					|| "hi_psndoc_orgpsn".equalsIgnoreCase(table) || "hi_psndoc_psnchg".equalsIgnoreCase(table)
					|| "hi_psndoc_ref".equalsIgnoreCase(table)) {
				sql = " select " + select + " from " + table + " where pk_psndoc='" + pk + "'";// V35 pk_psndoc
			} else {
				sql = " select " + select + " from " + table + " where pk_psnbasdoc='" + pk + "' ";
			}
			if (table.equalsIgnoreCase("hi_psndoc_part")) {
				sql = "select * from hi_psndoc_deptchg where pk_psndoc='" + pk + "' and jobtype<>0";
			} else if (table.equalsIgnoreCase("hi_psndoc_deptchg")) {
				sql += " and jobtype=0";
			} else if (table.equalsIgnoreCase("hi_psndoc_training")) {
				sql += " and approveflag = 2";
			} else if (table.equalsIgnoreCase("hi_psndoc_ctrt")) {
				sql += " and isrefer = 'Y'";
			}
			// 是子集添加排序条件
			if (!table.equals("bd_psnbasdoc") && !table.equals("bd_psndoc") && !table.equals("hi_psndoc_ref")) {
				sql += " order by recordnum desc";
			}
			PsnInfDMO dmo = new PsnInfDMO();
			psnList = dmo.queryBySql(sql);
			if (psnList != null) {
				// 查询主集信息时，增加对日期型公式信息项的处理
				if ("bd_psndoc".equalsIgnoreCase(table)) {
					FlddictVO[] flddictVOs = PubDelegator.getISetdict().queryAllFlddict(pk_corp,
							"40000000000000000001", false, false, false, false);
					FlddictVO[] basflddictVOs = PubDelegator.getISetdict().queryAllFlddict(pk_corp,
							"40000000000000000002", false, false, false, false);
					Vector<String> vFieldName = new Vector<String>();
					StringBuffer sqlDate = new StringBuffer("select pk_psndoc ");
					for (int i = 0; i < flddictVOs.length; i++) {
						if (flddictVOs[i].getFldcode().indexOf(CommonValue.UFFORMULA_DATA) >= 0) {
							vFieldName.addElement(flddictVOs[i].getFldcode());
							// 日期公式解析
							String truesql = GlobalTool.proDateFormulaSql(flddictVOs[i].getFldcode(), null, funcParser);
							if (truesql != null && truesql.trim().length() > 0)
								sqlDate.append(" ,").append(truesql);
						}
					}
					for (int i = 0; i < basflddictVOs.length; i++) {
						if (basflddictVOs[i].getFldcode().indexOf(CommonValue.UFFORMULA_DATA) >= 0) {
							vFieldName.addElement(basflddictVOs[i].getFldcode());
							// 日期公式解析
							String truesql = GlobalTool.proDateFormulaSql(basflddictVOs[i].getFldcode(), null,
									funcParser);
							if (truesql != null && truesql.trim().length() > 0)
								sqlDate.append(" ,").append(truesql);
						}
					}
					if (vFieldName.size() > 0) {
						String[] fieldNames = new String[vFieldName.size()];
						vFieldName.copyInto(fieldNames);
						psnList = dmo.queryPsnInfo(pk, sqlDate.toString(), table, fieldNames, psnList, funcParser);
					}

				}
			}

		} catch (Exception e) {
			throwBusinessException(e);
		}
		return psnList;
	}

	/**
	 * 
	 * @param pk_om_dumorg
	 * @return
	 * @throws java.sql.SQLException
	 * @throws java.io.IOException
	 */
	public GeneralVO queryDetailForDumorg(String pk_om_dumorg) throws BusinessException {
		GeneralVO vo = null;
		try {
			PsnInfDMO dmo = new PsnInfDMO();
			vo = dmo.queryDetailForDumorg(pk_om_dumorg);
		} catch (Exception e) {
			throwBusinessException(e);
		}
		return vo;

	}

	/**
	 * validatePsndocInf 方法注解。
	 */
	public GeneralVO validatePsndocInf(GeneralVO psninfVO, String pk_corp) throws BusinessException {
		int jobtype = (((Integer) psninfVO.getAttributeValue("jobtypeflag")).intValue());
		if (jobtype != 0) {
			return psninfVO;
		}
		// 检查编码是否存在
		String pk_psndoc = (String) psninfVO.getAttributeValue("pk_psndoc");
		// 检查职员编码
		UFBoolean clerkflag =  psninfVO.getAttributeValue("clerkflag")!=null?new UFBoolean(psninfVO.getAttributeValue("clerkflag").toString()):null;
		if (clerkflag != null && clerkflag.booleanValue()) {// 起用职员编码
			String clerkcode = (String) psninfVO.getAttributeValue("clerkcode");
			// 职员编码非空
			boolean exists = HIDelegator.getIHRhiQBS().recordExists("bd_psndoc", "pk_psndoc", pk_psndoc, "clerkcode",
					clerkcode, "pk_corp='" + pk_corp + "'");
			// 并且公司内唯一
			if (exists)
				throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("600704",
				"UPP600704-000165")/* @res "职员编号：" */
				+ clerkcode
				+ nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("600704", "UPP600704-000123")/*
				 * @res
				 * "已经存在！"
				 */);
		}
		// 部门和岗位对应关系是否正确
		String deptpk = (String) ((GeneralVO) psninfVO).getFieldValue("pk_deptdoc");
		String jobpk = (String) ((GeneralVO) psninfVO).getFieldValue("pk_om_job");
		if (deptpk != null && jobpk != null) {
			boolean exists = HIDelegator.getIHRhiQBS().recordExists("om_job", "", null, "pk_om_job", jobpk,
					"pk_deptdoc='" + deptpk + "'");
			if (!exists)
				throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("600704",
				"UPP600704-000147")/*
				 * @res "选择的岗位和部门不匹配，请重新选择！"
				 */);
		}

		// 根据人员类别设置人员归属范围
		String pk_psncl = (String) psninfVO.getAttributeValue("pk_psncl");

		IPsncl ipsncl = (IPsncl) NCLocator.getInstance().lookup(IPsncl.class.getName());
		nc.vo.bd.b05.PsnclVO psncl = ipsncl.findPsnclVOByPk(pk_psncl);
		if (psncl == null) {
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("600704",
			"upt600704-000042")/*
			 * @res "人员类别"
			 */
			+ nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("600704", "UPP600704-000003")/*
			 * @res
			 * "该记录已经被删除,请刷新后再试！"
			 */);
		}
		// 
		int psnclscope = 0;
		if (psncl != null) {
			psnclscope = psncl.getPsnclscope();
			psninfVO.setAttributeValue("psnclscope", psncl.getPsnclscope());
		}
		boolean isonduty = true;
		if (psnclscope == 0 || psnclscope == 5) {
			isonduty = true;
		} else {
			isonduty = false;
		}
		String pk_deptdoc = (String) psninfVO.getAttributeValue("pk_deptdoc");

		// 部门，只有在维护时检查
		if (pk_deptdoc != null) {
			boolean bExist = HIDelegator.getIHRhiQBS().recordExists("bd_deptdoc", null, null, "pk_deptdoc", pk_deptdoc,
					null);
			if (!bExist) {
				throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("600704",
				"upt600704-000139")/* @res "部门" */
				+ nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("600704", "UPP600704-000003")/*
				 * @res
				 * "该记录已经被删除,请刷新后再试！"
				 */);
			}
		}

		String pk_om_job = (String) psninfVO.getAttributeValue("pk_om_job");
		try {
			if (pk_deptdoc != null && pk_deptdoc.length() == 20) {
				boolean bExist = HIDelegator.getIHRhiQBS().recordExists("bd_deptdoc", null, null, "pk_deptdoc",
						pk_deptdoc, "hrcanceled='Y'");
				if (bExist && isonduty) {
					throw new Exception(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("600704",
					"UPP600704-000166")/*
					 * @res "选择的部门已经撤销，不能保存！"
					 */);
				}
			}
			if (pk_om_job != null && pk_om_job.length() == 20) {
				boolean bExist = HIDelegator.getIHRhiQBS().recordExists("om_job", null, null, "pk_om_job", pk_om_job,
				"isabort='Y'");
				if (bExist && isonduty) {
					throw new Exception(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("600704",
					"UPP600704-000178")/*
					 * @res "选择的岗位已经撤销，不能保存！"
					 */);
				}
				// 判断是否删除
				bExist = HIDelegator.getIHRhiQBS().recordExists("om_job", null, null, "pk_om_job", pk_om_job, "0=0");
				if (!bExist) {
					throw new Exception(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("600704",
					"UPP600704-100003")/* @res "岗位" */
					+ nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("600704", "UPP600704-000003")/*
					 * @res
					 * "该记录已经被删除,请刷新后再试！"
					 */);
				}
			}
		} catch (Exception e) {
			throwBusinessException(e);
		}
		return psninfVO;
	}

	/**
	 * 根据人员档案主键，查询人员工作档案的信息
	 * 
	 * @param pk_psndoc
	 * @return
	 * @throws BusinessException
	 */
	public GeneralVO[] queryPersonMainInfo(String[] pk_psndocs, String pk_corp) throws BusinessException {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet result = null;
		GeneralVO[] gvo = null;
		try {
			PsnInfDMO dmo = new PsnInfDMO();
			// 先找信息项
			String fields_setdict = dmo.queryFieldDict(pk_corp);
			String fields_select = fields_setdict
			+ "bd_psnbasdoc.sex,bd_corp.unitcode,bd_corp.unitname,bd_deptdoc.deptcode,bd_deptdoc.deptname,om_job.jobname,bd_psncl.psnclscope,bd_psncl.psnclassname,om_duty.dutycode,om_duty.dutyname";
			if (fields_select.indexOf("psnname") < 0)
				fields_select += ",bd_psnbasdoc.psnname";

			gvo = dmo.queryPerson(pk_psndocs, fields_select);
		} catch (Exception e) {
			throwBusinessException(e);
		}
		return gvo;

	}

	public void checkIfExistsUnAuditBillofPSN(String[] pk_psndocs, String setcode) throws BusinessException {
		PsnInfDMO dmo = null;
		try {
			dmo = new PsnInfDMO();
		} catch (SystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (pk_psndocs == null || pk_psndocs.length < 1)
			return;
		dmo.checkIfExistsUnAuditBillofPSN(pk_psndocs, setcode);

	}

	/**
	 * 获得审批通过的人员档案的GeneralVO
	 * 
	 * @return
	 * @throws BusinessException
	 * @throws BusinessException
	 */
	public GeneralVO[] getIntoDocData(GeneralVO[] psnlistVOS) throws BusinessException {
		Vector<GeneralVO> vector = new Vector<GeneralVO>();
		if (psnlistVOS != null) {
			for (int i = 0; i < psnlistVOS.length; i++) {

				String pk_psndoc = (String) psnlistVOS[i].getAttributeValue("pk_psndoc");
				String unitname = (String) psnlistVOS[i].getAttributeValue("unitname");
				String deptname = (String) psnlistVOS[i].getAttributeValue("deptname");

				String sql = " select pk_psnbasdoc, bd_psndoc.pk_corp, bd_psndoc.pk_deptdoc, pk_psncl, bd_psndoc.jobrank, bd_psndoc.jobseries, dutyname, "
					+ " series, bd_psndoc.pk_om_job, indutydate, bd_psndoc.pk_dutyrank, psnname, psncode, psnclscope, jobname ,isreturn "
					+ " from bd_psndoc left outer join om_job on bd_psndoc.pk_om_job = om_job.pk_om_job where pk_psndoc='"
					+ pk_psndoc + "'";

				GeneralVO vo = null;
				List psnlist = null;

				psnlist = (List) PubDelegator.getIPersistenceHome().executeQuery(sql, null, new MapListProcessor());

				if (psnlist != null && psnlist.size() > 0 && psnlist.get(0) instanceof Map) {
					vo = new GeneralVO();
					Map map = (Map) psnlist.get(0);
					vo.setAttributeValue("unitname", unitname);
					vo.setAttributeValue("deptname", deptname);
					vo.setAttributeValue("jobname", map.get("jobname"));
					vo.setAttributeValue("pk_psndoc", pk_psndoc);
					vo.setAttributeValue("pk_psncl", map.get("pk_psncl"));
					vo.setAttributeValue("psnname", map.get("psnname"));
					vo.setAttributeValue("pk_psnbasdoc", map.get("pk_psnbasdoc"));
					vo.setAttributeValue("pk_corp", map.get("pk_corp"));
					vo.setAttributeValue("belong_pk_corp", map.get("pk_corp"));
					vo.setAttributeValue("pk_deptdoc", map.get("pk_deptdoc"));
					vo.setAttributeValue("jobrank", map.get("jobrank"));
					vo.setAttributeValue("jobseries", map.get("jobseries"));
					vo.setAttributeValue("dutyname", map.get("dutyname"));
					vo.setAttributeValue("series", map.get("series"));
					vo.setAttributeValue("pk_om_job", map.get("pk_om_job"));
					vo.setAttributeValue("indutydate", map.get("indutydate"));
					vo.setAttributeValue("pk_dutyrank", map.get("pk_dutyrank"));
					vo.setAttributeValue("psnclscope", map.get("psnclscope"));
					vo.setAttributeValue("psncode", map.get("psncode"));
					vo.setAttributeValue("isreturn", map.get("isreturn"));
				}
				if (vo != null) {
					vector.add(vo);
				}
			}
		}

		GeneralVO[] intoDocData = new GeneralVO[vector.size()];
		vector.copyInto(intoDocData);
		return intoDocData;
	}

	public boolean[] isRecordExists(String[] sqls) throws BusinessException {
		boolean[] result = new boolean[sqls.length];
		try {
			PsnInfDMO dmo = new PsnInfDMO();
			for (int i = 0; i < sqls.length; i++) {

				result[i] = dmo.isRecordExist(sqls[i]);
			}
		} catch (Exception e) {
			throwBusinessException(e);
		}
		return result;
	}

	public void batchInsertUserRoles(HashMap<String, RoleVO[]> userRoleMap, UserAndClerkVO[] userAndClerks)
	throws BusinessException {
		Iterator keys = userRoleMap.keySet().iterator();
		String userid = "";
		while (keys.hasNext()) {
			userid = (String) (keys.next());
			HIDelegator.getUserManage().updateUserRoles(userid, userRoleMap.get(userid), null);
		}
		for (int i = 0; i < userAndClerks.length; i++) {
			HIDelegator.getUserAndClerkService().insertUserAndClerk(userAndClerks[i]);
		}
	}

	public String[] batchInsertUsers(UserVO[] userVOs) throws BusinessException {
		if (userVOs == null || userVOs.length < 1)
			return null;
		String[] userids = new String[userVOs.length];
		for (int i = 0; i < userVOs.length; i++) {
			UserVO uservo = userVOs[i];
			userids[i] = HIDelegator.getUserManage().addUser(uservo);
		}
		return userids;
	}

	/*********************************************************************************************************
	 * 转入人员档案、入职审批时增加人员增量数据处理
	 * 
	 * @param psnList
	 * @throws BusinessException
	 ********************************************************************************************************/
	public void insertPsndocAdd(GeneralVO[] psnList) throws BusinessException {
		// 增加人员增量数据
		((IHrFiUtil) NCLocator.getInstance().lookup(IHrFiUtil.class)).savePsndocAdd(psnList);
	}

	public void updatePsnnamePinyin() throws BusinessException {

		String bSql = "select pk_psndoc,psnname from bd_psndoc where psnnamepinyin is null ";
		ArrayList<String[]> list2 = (ArrayList<String[]>) PubDelegator.getIPersistenceHome().executeQuery(bSql,
				new nc.jdbc.framework.processor.ArrayListProcessor() {
			private static final long serialVersionUID = 1L;

			public Object handleResultSet(ResultSet rs) throws SQLException {

				java.util.ArrayList<String[]> list = new java.util.ArrayList<String[]>();
				while (rs.next()) {
					String[] psnStr = new String[2];
					psnStr[0] = rs.getString(1);
					psnStr[1] = rs.getString(2);
					list.add(psnStr);
				}
				return list;
			}
		});

		if (list2 == null || list2.size() <= 0) {
			return;
		}

		String aSql = "select chinese,pinyin from hi_pinyin ";
		HashMap<String, String[]> map = (HashMap<String, String[]>) PubDelegator.getIPersistenceHome().executeQuery(
				aSql, new nc.jdbc.framework.processor.MapProcessor() {
					private static final long serialVersionUID = 1L;

					public Object handleResultSet(ResultSet rs) throws SQLException {

						java.util.HashMap<String, String[]> map = new java.util.HashMap<String, String[]>();
						while (rs.next()) {
							if (map.get(rs.getString(1)) != null) {
								String[] strs = map.get(rs.getString(1));

								String[] strs2 = new String[strs.length + 1];
								System.arraycopy(strs, 0, strs2, 0, strs.length);
								strs2[strs.length] = rs.getString(2);
								map.put(rs.getString(1), strs2);
							} else {
								map.put(rs.getString(1), new String[] { rs.getString(2) });
							}
						}
						return map;
					}
				});

		setPsnnamePinyin(list2, map);

		PersistenceDAO persistenceDAO = new PersistenceDAO();
		String strSQL = " update bd_psndoc set psnnamepinyin = {0} where pk_psndoc={1} ";
		for (int j = 0; j < list2.size(); j++) {
			String[] psnStr = list2.get(j);
			if(psnStr[1].length()>=1000) psnStr[1] = psnStr[1].substring(0, 999);
			String aStrSQL = MessageFormat.format(strSQL, "'" + psnStr[1] + "'", "'" + psnStr[0] + "'");
			persistenceDAO.executeSQL(aStrSQL, null);
		}
	}

	public void updatePsnnamePinyin(String pk_psndoc, String psnname) {

		ArrayList<String[]> list2 = new ArrayList<String[]>();
		String[] psn = new String[2];
		psn[0] = pk_psndoc;
		psn[1] = psnname;
		list2.add(psn);

		String aSql = "select chinese,pinyin from hi_pinyin where '"+psnname+"' like '%'||chinese||'%'";
		try {
			HashMap<String, String[]> map = (HashMap<String, String[]>) PubDelegator.getIPersistenceHome()
			.executeQuery(aSql, new nc.jdbc.framework.processor.MapProcessor() {
				private static final long serialVersionUID = 1L;

				public Object handleResultSet(ResultSet rs) throws SQLException {

					java.util.HashMap<String, String[]> map = new java.util.HashMap<String, String[]>();
					while (rs.next()) {
						if (map.get(rs.getString(1)) != null) {
							String[] strs = map.get(rs.getString(1));

							String[] strs2 = new String[strs.length + 1];
							System.arraycopy(strs, 0, strs2, 0, strs.length);
							strs2[strs.length] = rs.getString(2);
							map.put(rs.getString(1), strs2);
						} else {
							map.put(rs.getString(1), new String[] { rs.getString(2) });
						}
					}
					return map;
				}
			});

			setPsnnamePinyin(list2, map);

			PersistenceDAO persistenceDAO = new PersistenceDAO();
			String strSQL = " update bd_psndoc set psnnamepinyin = {0} where pk_psndoc={1} ";
			for (int j = 0; j < list2.size(); j++) {
				String[] psnStr = list2.get(j);
				String aStrSQL = MessageFormat.format(strSQL, "'" + psnStr[1] + "'", "'" + psnStr[0] + "'");
				persistenceDAO.executeSQL(aStrSQL, null);
			}
		} catch (BusinessException e) {
			Logger.error(e.getMessage(), e);
		}
	}

	private void setPsnnamePinyin(ArrayList<String[]> list2, HashMap<String, String[]> map) throws BusinessException {
		for (int i = 0; i < list2.size(); i++) {
			String[] psnStr = list2.get(i);
			String psnname = psnStr[1].trim();
			if (psnname == null || psnname.equals("")) {
				continue;
			}
			String psnnamepinyin = "";
			java.util.ArrayList<String> list3 = new java.util.ArrayList<String>();// 如果有多音字
			for (int j = 0; j < psnname.length(); j++) {
				String aString = psnname.substring(j, j + 1);
				String[] pinyins = map.get(String.valueOf(aString));
				if (pinyins == null || pinyins.length <= 0) {
					// psnnamepinyin = psnnamepinyin + aString;
					list3.add(aString);
					continue;
				}
				String psnnamepinyinTmp = "";

				if (pinyins.length == 1) {
					psnnamepinyinTmp += pinyins[0].substring(0, 1);
				} else {
					ArrayList<String> sameList = new ArrayList<String>();
					for (int k = 0; k < pinyins.length; k++) {
						// psnnamepinyin = psnnamepinyin + pinyins[k].substring(0, 1);
						String firstStr = pinyins[k].substring(0, 1);
						if (!sameList.contains(firstStr)) {
							sameList.add(firstStr);
							psnnamepinyinTmp += firstStr;
						}
					}
				}
				list3.add(psnnamepinyinTmp);
			}
			//
			ArrayList<ArrayList> return_list = new ArrayList<ArrayList>();
			getPsnnameList(list3, return_list);

			if (return_list.size() == 1) {
				ArrayList list = return_list.get(0);
				String aStr = "";
				if (list != null && list.size() > 0) {
					for (int k = 0; k < list.size(); k++) {
						aStr += (String) list.get(k);
					}
				}
				psnnamepinyin += aStr;
			} else {
				ArrayList<String> sameList = new ArrayList<String>();
				for (int j = 0; j < return_list.size(); j++) {
					ArrayList list = return_list.get(j);
					String aStr = "";
					if (list != null && list.size() > 0) {
						for (int k = 0; k < list.size(); k++) {
							aStr += (String) list.get(k);
						}
					}
					if (!sameList.contains(aStr)) {
						sameList.add(aStr);
						psnnamepinyin += aStr;
					}
				}
			}
			psnStr[1] = psnnamepinyin;
		}
	}

	private void getPsnnameList(ArrayList input_list, ArrayList<ArrayList> return_list) {

		if (input_list == null || input_list.size() <= 0) {
			return;
		}

		int i = 0;
		for (; i < input_list.size();) {
			String str = (String) input_list.get(i);
			if (str == null || str.length() == 0) {
				continue;
			} else if (str.length() > 1) {
				break;
			}
			i++;
		}

		if (i == input_list.size()) {
			return_list.add(input_list);
			return;
		}

		int j = 0;
		for (; j < input_list.size(); j++) {
			String str = (String) input_list.get(j);
			if (str.length() > 1) {
				ArrayList aArrayList = (ArrayList) input_list.clone();
				ArrayList bArrayList = (ArrayList) input_list.clone();
				aArrayList.set(j, str.substring(0, 1));
				getPsnnameList(aArrayList, return_list);
				bArrayList.set(j, str.substring(1));
				getPsnnameList(bArrayList, return_list);
			}
		}

	}

	public String getPkpsnbasdoc(String pk_psndoc) throws BusinessException {

		String pk_psnbasdoc = "";
		try {
			PsnInfDMO dmo = new PsnInfDMO();

			pk_psnbasdoc = dmo.getPkpsnbasdoc(pk_psndoc);
		} catch (Exception e) {
			throwBusinessException(e);
		}
		return pk_psnbasdoc;
	}

	public void synUpdatePsnInfo(String pk_psnbasdoc, String pk_psndoc) throws BusinessException {
		GeneralVO voTemp = new GeneralVO();
		voTemp.setAttributeValue("pk_psnbasdoc", pk_psnbasdoc);
		voTemp.setAttributeValue("pk_psndoc", pk_psndoc);
		try {
			PsnInfDMO psnInfDMO = new PsnInfDMO();
			Hashtable map = psnInfDMO.getTempRelationMap();
			Set keys = map.keySet();
			ISetdict setdictDMO = PubDelegator.getISetdict();
			for (Iterator key = keys.iterator(); key.hasNext();) {
				String keyTable = (String) key.next();
				String tableCode = null;
				String field = null;
				String chkformula = null;
				String strItemInf = null;
				if (keyTable != null && !"".equals(keyTable)) {
					int index = keyTable.indexOf(".");
					tableCode = keyTable.substring(0, index);
					field = keyTable.substring(index + 1, keyTable.length());
				}
				String acc_fldcode = (String) map.get(keyTable);
				if (acc_fldcode != null) {
					chkformula = setdictDMO.findChkFormula(tableCode, field);
					try {
						strItemInf = psnInfDMO.findItemInf(tableCode, field, (String) voTemp
								.getAttributeValue("pk_psnbasdoc"), chkformula);
					} catch (Exception e) {
						String eMsg = NCLangResOnserver.getInstance().getStrByID("600704", "UPT600704-000247");
						throw new BusinessException(eMsg);
					}
					if (strItemInf == null)
						strItemInf = "#null#";
					voTemp.setAttributeValue(acc_fldcode, strItemInf);
				}
			}

		} catch (Exception e) {
			throw new BusinessException(e);
		}
		HashMap hmRelVO = new HashMap();
		String fields[] = voTemp.getAttributeNames();
		GeneralVO psnvo = new GeneralVO();
		GeneralVO baspsnvo = new GeneralVO();
		psnvo.setAttributeValue("pk_psnbasdoc", pk_psnbasdoc);
		psnvo.setAttributeValue("pk_psndoc", pk_psndoc);
		baspsnvo.setAttributeValue("pk_psnbasdoc", pk_psnbasdoc);
		baspsnvo.setAttributeValue("pk_psndoc", pk_psndoc);
		boolean isRelPsndoc = false;
		boolean isRelBasPsndoc = false;
		for (int i = 0; i < fields.length; i++)
			if (fields[i].indexOf(".") >= 0) {
				String table = fields[i].substring(0, fields[i].indexOf("."));
				if ("bd_psndoc".equalsIgnoreCase(table)) {
					String field = fields[i].substring(fields[i].indexOf(".") + 1);
					psnvo.setAttributeValue(field, voTemp.getAttributeValue(fields[i]));
					isRelPsndoc = true;
				} else if ("bd_psnbasdoc".equalsIgnoreCase(table)) {
					String field = fields[i].substring(fields[i].indexOf(".") + 1);
					baspsnvo.setAttributeValue(field, voTemp.getAttributeValue(fields[i]));
					isRelBasPsndoc = true;
				}
			}

		if (isRelPsndoc)
			hmRelVO.put("bd_psndoc", psnvo);
		if (isRelBasPsndoc)
			hmRelVO.put("bd_psnbasdoc", baspsnvo);
		updateRelTable(hmRelVO);
	}

	public GeneralVO queryByPsndocPK(String pk_psndoc) throws BusinessException {
		String sql = "select * from bd_psndoc where pk_psndoc = '" + pk_psndoc + "'";
		PsnInfDMO dmo;
		try {
			dmo = new PsnInfDMO();
			GeneralVO[] result = dmo.queryBySql(sql);
			if(ArrayUtils.isEmpty(result)){
				return new GeneralVO();
			}
			return result[0];
		} catch (Exception e) {
			throw new BusinessException(e);
		}

	}

	public void generateDeptchgByPsndoc(String pk_psndoc, String userid,
			String pk_corp) throws BusinessException {
		//根据工作记录主键查询出工作记录信息
		GeneralVO psndocVO = queryByPsndocPK(pk_psndoc);
		//得到工作信息和任职记录对应的信息项
		GeneralVO[] relatedFields = queryAllRelatedTableField(pk_corp);

		HashMap accToSubDeptchgMap = new HashMap();
		Vector vDeptchg = new Vector();

		//放入映射表中
		for (int i = 0; i < relatedFields.length; i++) {
			String table = (String) relatedFields[i].getAttributeValue("setcode");
			String field = (String) relatedFields[i].getAttributeValue("fldcode");
			String accfield = (String) relatedFields[i].getAttributeValue("accfldcode");
			if(table.equalsIgnoreCase("hi_psndoc_deptchg")){
				// 取出预置信息
				Hashtable<String, String> h = new Hashtable<String, String>();
				// 预置部分信息
				String[] fieldNames = { "pk_deptdoc", "pk_psncl",
						"pk_jobrank", "pk_jobserial", "pk_om_duty",
						"pk_detytype", "pk_postdoc", "begindate",
				"pk_dutyrank" };
				for (int j = 0; j < fieldNames.length; j++) {
					h.put(fieldNames[j], fieldNames[j]);

				}
				// 预置、同步管理档案信息项都有时以预置为主
				if (!h.contains(field)) {
					accToSubDeptchgMap.put(accfield, field);
					vDeptchg.addElement(accfield);
				}
			}
		}

		String[] relationdeptchg = null; 
		if(vDeptchg.size()>0){
			relationdeptchg = new String[vDeptchg.size()];
			vDeptchg.copyInto(relationdeptchg);
		}

		//任职开始日期规则：如果是第一次任职，首先取最新到岗日期，如果没有，则取到职日期。如果不是第一次任职，则只取最新到岗日期。
		// 到职日期
		UFDate indutydate = psndocVO
		.getAttributeValue("indutydate") == null ? null
				: new UFDate(psndocVO.getAttributeValue(
				"indutydate").toString().trim());
		// 最新到岗日期
		UFDate onpostdate = psndocVO
		.getAttributeValue("onpostdate") == null ? null
				: new UFDate(psndocVO.getAttributeValue(
				"onpostdate").toString().trim());
		try {
			PsnInfDMO dmo = new PsnInfDMO();

			// 由最新到岗日期、任职记录取得新任职记录的开始日期
			UFDate begindate = dmo.updateDeptchgRecornum(1, pk_psndoc,
					onpostdate,indutydate);

			// 如果存在任职记录，且任职记录开始日期为空或者到职日期在最新任职记录结束日期之前时，不写入任职开始日期
			if (begindate == null) {
				psndocVO.setFieldValue("indutydate", null);
			} else {
				psndocVO.setFieldValue("indutydate", begindate); 
			}
			psndocVO.setAttributeValue("pk_psndoc",pk_psndoc);//对人员pk重新赋值，如果是返聘再聘，就修改，否则不变
			psndocVO.setAttributeValue("belong_pk_corp", pk_corp);
			dmo.insertDeptChg(psndocVO,relationdeptchg,accToSubDeptchgMap);
		} catch (Exception e) {
			throwBusinessException(e);
		} 
	}
	public void saveexptvo(List<PSNDOCEXPORTVO> tvo)throws BusinessException{
		BaseDAO dao = new BaseDAO();
		dao.insertVOList(tvo);
	}

	public void savePsndocGrpDef15(CircularlyAccessibleValueObject[] data) throws BusinessException {
		// TODO Auto-generated method stub
		try {
			PsnInfDMO dmo = new PsnInfDMO();
			GeneralVO[] vos = new GeneralVO[data.length];
			
			for(int i=0;i<data.length;i++){
				String pk_psndoc = data[i].getAttributeValue("pk_psndoc").toString();
				String pk_psnbasdoc = data[i].getAttributeValue("pk_psnbasdoc").toString();
				String year = data[i].getAttributeValue("groupdef1").toString();
				String pk_defdoc = data[i].getAttributeValue("groupdef2").toString();
				String strSQL = "select pk_psndoc_sub from hi_psndoc_grpdef15 where pk_psnbasdoc ='"+pk_psnbasdoc+"' and pk_psndoc ='"+pk_psndoc+"' and groupdef1='"+year+"'";
				//int count = dmo.queryRecordCountBySql(strSQL);
				String pk_psndoc_sub = getPkPsndocSub(strSQL,null);

				GeneralVO vo = new GeneralVO();
				vo.setAttributeValue("groupdef1", year);
				vo.setAttributeValue("groupdef2", pk_defdoc);
				vo.setAttributeValue("pk_psnbasdoc",pk_psnbasdoc);
				vo.setAttributeValue("pk_psndoc", pk_psndoc);
				vo.setAttributeValue("lastflag", new UFBoolean("Y"));
				vo.setAttributeValue("dr", 0);
				 
				if(pk_psndoc_sub == null){		
					vo.setStatus(2);
				}else{
					vo.setStatus(1);
					vo.setAttributeValue("pk_psndoc_sub", pk_psndoc_sub);		
				}
				
				vos[i] = vo;
			}
			//
			
			for(int j=0;j<vos.length;j++){
				GeneralVO vo =  vos[j];
				GeneralVO[] vos1 = new GeneralVO[1];
				vos1[0] = vo;
				saveSubSetInfos("hi_psndoc_grpdef15", (String) vo.getAttributeValue("pk_psndoc"), vos1, null);
			}

		} catch (Exception e) {
			throwBusinessException(e);
		}
		 
	}
	
	
	public String getPkPsndocSub(String sql, SQLParameter parm) throws BusinessException {
		String pk_psndoc_sub = null;
		Vector o1 = (Vector) PubDelegator.getIPersistenceHome().executeQuery(sql,new VectorProcessor());
		if (o1.size() > 0 && o1 != null) {
			for (int i = 0; i < o1.size(); i++) {
				pk_psndoc_sub = new String(((Vector) o1.elementAt(i)).elementAt(0) != null ? ((Vector) o1.elementAt(i)).elementAt(0).toString() : null);
			}
		}	
		return pk_psndoc_sub;
	}
	
}
