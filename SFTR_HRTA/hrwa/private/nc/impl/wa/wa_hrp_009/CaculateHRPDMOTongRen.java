package nc.impl.wa.wa_hrp_009;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.naming.NamingException;
import nc.bs.dao.BaseDAO;
import nc.bs.framework.common.NCLocator;
import nc.bs.pub.DataManageObject;
import nc.bs.pub.SuperDMO;
import nc.bs.pub.SystemException;
import nc.impl.wa.wa_009.DataDMO;
import nc.itf.hr.comp.IHrPara;
import nc.itf.uap.IUAPQueryBS;
import nc.jdbc.framework.processor.BeanListProcessor;
import nc.vo.pub.BusinessException;
import nc.vo.pub.VOStatus;
import nc.vo.pub.lang.UFDouble;
import nc.vo.tm.hrp.pub.TMBVO;
import nc.vo.wa.application.temptable.TempTableVO;
import nc.vo.wa.wa_002.ClassitemVO;
import nc.vo.wa.wa_009.DataVO;
import nc.vo.wa.wa_009.ReCacuVO;
import nc.vo.wa.wa_hrp_002.PsnClassItemBBVO;
import nc.vo.wa.wa_hrp_002.PsnClassItemBVO;
import nc.vo.wa.wa_hrp_002.PsnClassItemHVO;

/**
 * @author tianxfc
 * 2018-12-03 15:00:00
 * 奖金发放月考核、一次性奖金取数逻辑
 */
public class CaculateHRPDMOTongRen extends DataManageObject {

	private IHrPara hrpara;
	public CaculateHRPDMOTongRen(String arg0) throws NamingException {
		super(arg0);
	}

	public CaculateHRPDMOTongRen() throws NamingException {
		super();
	}
	//sqt
	public IHrPara getHrpara() {
		if(hrpara==null){
			hrpara=NCLocator.getInstance().lookup(IHrPara.class);
		}
		return hrpara ;
	}
	
	public void reCaculateHRP_jj(ReCacuVO aRecaVO,String[][] hrpItems ) throws BusinessException{
		try{
			String vyear = aRecaVO.getWaYear();
			String vperiod = aRecaVO.getWaPeriod();
			String pk_period = aRecaVO.getWaPeriodvo().getPk_wa_period();
			String pk_corp = aRecaVO.getPk_corp();
			String operatorid = aRecaVO.getUserid();
			String logindate = aRecaVO.getLogDate();
			aRecaVO.getAllItemCodes();
			aRecaVO.getAllItemDecimal();
			aRecaVO.getAllItemNames();
			aRecaVO.getAllItemWidth();
			ClassitemVO[] itemvos = aRecaVO.getClassitemVOs();
			HashMap<String,ClassitemVO> map = new HashMap<String, ClassitemVO>();
			if(itemvos!=null){
				for(ClassitemVO vo:itemvos){
					map.put(vo.getIItemID(), vo);
				}
			}
			String classid = aRecaVO.getWaClassVO().getPrimaryKey();

			SuperDMO dmo = new SuperDMO();
			if (hrpItems == null || hrpItems.length < 1) {
				return;
			}




			String[] itemcodes = new String[hrpItems.length];
			String[] itemtypes = new String[itemcodes.length];
			for(int i=0;i<itemtypes.length;i++){
				itemtypes[i] = "1";
			}
			//and isnull(nmny,0)>0
			String wheresql = " isnull(dr,0)=0  and pk_wa_class='"+classid+"' and isnull(nmny,0)<>0 and vyear='"+vyear+"' and vperiod='"+vperiod+"' and pk_corp='"+pk_corp+"' ";
			if(hrpItems!=null){
				wheresql+=" and pk_classitem in ( ";
				for(int i=0;i<hrpItems.length-1;i++){
					String key = hrpItems[i][0];
					itemcodes[i] = key;
					ClassitemVO vo = map.get(key);
					if(vo!=null&&vo.getPrimaryKey()!=null){
						wheresql+="'"+vo.getPk_wa_item()+"',";
					}
				}
				String key = hrpItems[hrpItems.length-1][0];
				itemcodes[hrpItems.length-1] = key;
				ClassitemVO vo = map.get(key);
				if(vo!=null&&vo.getPrimaryKey()!=null){
					wheresql+="'"+vo.getPk_wa_item()+"') ";
				}
			}else{
				wheresql+=" and 1=2 ";
			}
			String wheresql_a  = aRecaVO.getSelCondition();
			DataDMO datadmo = new DataDMO();
			DataVO[] datavos = datadmo.queryAll(classid, vyear, vperiod, itemcodes, itemtypes,wheresql_a);
			if(datavos==null||datavos.length<=0) return;
			ArrayList<String> list_psn = new ArrayList<String>();
			for(DataVO vo:datavos){
				if(vo.getPsnid()!=null&&!list_psn.contains(vo.getPsnid().trim())){
					list_psn.add(vo.getPsnid().trim());
				}
			}

			TempTableVO tmptb = null;
			try {
				TempTableDMO tempTableDMO = new TempTableDMO();
				tmptb = tempTableDMO.getTempByData(list_psn.toArray(new String[0]), "tmp_wa_psn_item_bb_temp",true);
			} catch (Exception e) {
				e.printStackTrace();
				throw new BusinessException("创建数据库临时表出错");
			}

			String inFk_usedepts = null;
			if (tmptb.getInSql() != null) {
				inFk_usedepts = tmptb.getInSql();
			} else {
				inFk_usedepts = "( select " + tmptb.getTempPK() + " from " + tmptb.getTempName() + " )";
			}
			if(inFk_usedepts!=null&&inFk_usedepts.trim().length()>0){
				wheresql += " and  pk_psndoc in "+inFk_usedepts+" ";
			}
			//sqt
			//			hrpara=getHrpara();
			//			Integer nowstatu=hrpara.getParaIntValue(pk_corp,IHRwaPubBus.BMRYXCQZ,null, null);

			PsnClassItemBBVO[] bbvos = (PsnClassItemBBVO[])dmo.queryByWhereClause(PsnClassItemBBVO.class, wheresql+" order by dapprovedate ");
			HashMap<String,ArrayList<PsnClassItemBBVO>> map_bbvo = new HashMap<String,ArrayList<PsnClassItemBBVO>>();
			if(bbvos!=null){
				for(PsnClassItemBBVO vo:bbvos){
					if(map_bbvo.containsKey(vo.getPk_psndoc()+"-"+vo.getPk_classitem())){
						ArrayList<PsnClassItemBBVO> oldlist = map_bbvo.get(vo.getPk_psndoc()+"-"+vo.getPk_classitem());
						//						if(nowstatu==1){//求和
						PsnClassItemBBVO oldvo=oldlist.get(0);
						UFDouble nmny = oldvo!=null&&oldvo.getNmny()!=null?oldvo.getNmny():new UFDouble(0.0);
						nmny = vo.getNmny()!=null?nmny.add(vo.getNmny()):nmny;
						vo.setNmny(nmny);
						oldlist.clear();
						//						}else{//求最新
						//						oldlist.clear();
						//						}
						//						ArrayList<PsnClassItemBBVO> oldlist = map_bbvo.get(vo.getPk_psndoc()+"-"+vo.getPk_classitem());
						//						PsnClassItemBBVO oldvo = map_bbvo.get(vo.getPk_psndoc()+"-"+vo.getPk_classitem());
						//						UFDouble nmny = oldvo!=null&&oldvo.getNmny()!=null?oldvo.getNmny():new UFDouble(0.0);
						//						nmny = vo.getNmny()!=null?nmny.add(vo.getNmny()):nmny;
						//						vo.setNmny(nmny);
						oldlist.add(vo);
						map_bbvo.put(vo.getPk_psndoc()+"-"+vo.getPk_classitem(), oldlist);
					}else{
						ArrayList<PsnClassItemBBVO> oldlist = new ArrayList<PsnClassItemBBVO>();
						oldlist.add(vo);
						map_bbvo.put(vo.getPk_psndoc()+"-"+vo.getPk_classitem(), oldlist);
					}
				}
			}


			ArrayList<String> listhid = new ArrayList<String>();
			BaseDAO dao = new BaseDAO();
			//			Connection con = null;
			//			PreparedStatement stmt = null;
			//			try {
			//			con = getConnection();
			//			String updatesql = "  ";
			//			stmt = prepareStatement(con, updatesql);
			for(int i=0;i<datavos.length;i++){
				String updatesql = "update wa_data set dr=0 ";
				for(String item:itemcodes){
					UFDouble value = new UFDouble(0.0);
					if(map.get(item)!=null&&datavos[i].getPsnid()!=null&&
							map.get(item).getPk_wa_item()!=null&&
							map_bbvo.get(datavos[i].getPsnid()+"-"+map.get(item).getPk_wa_item())!=null){
						ArrayList<PsnClassItemBBVO> list = map_bbvo.get(datavos[i].getPsnid()+"-"+map.get(item).getPk_wa_item());
						if(list!=null&&list.size()>0){
							for(int j=0;j<list.size();j++){//修改取数  每次都取最后一个vo  
								value = list.get(j).getNmny()!=null?list.get(j).getNmny()/*value.add(list.get(j).getNmny())*/:value;
								String headkey = list.get(j).getPk_psn_item_h();
								if(!listhid.contains(headkey)){
									listhid.add(headkey);
								}
							}
						}
					}
					if(value==null) value = new UFDouble(0.0);
					updatesql+=","+item+"="+value+" ";
				}
				updatesql+=" where pk_wa_data='"+datavos[i].getPrimaryKey()+"' ";

				dao.executeUpdate(updatesql);
			}
			if(listhid!=null&&listhid.size()>0){
				String wherehid = " isnull(dr,0)=0 and pk_psn_item_h in ( ";
				for(int i=0;i<listhid.size()-1;i++){
					wherehid+=" '"+listhid.get(i)+"', ";
				}
				wherehid+=" '"+listhid.get(listhid.size()-1)+"') ";
				PsnClassItemHVO[] hvos = (PsnClassItemHVO[])dmo.queryByWhereClause(PsnClassItemHVO.class,wherehid);
				if(hvos!=null&&hvos.length>0){
					for(PsnClassItemHVO hvo:hvos){
						//hvo.setBisused(new UFBoolean(true));
						hvo.setStatus(VOStatus.UPDATED);
					}
					dmo.updateArray(hvos);
				}
			}
			//dmo.queryByWhereClause(PsnClassItemHVO.class,)
		}catch(SQLException e){
			e.printStackTrace();
		}catch(NamingException e){
			e.printStackTrace();
		}catch(SystemException e){
			e.printStackTrace();
		}
	}
	/**
	 * @author 宋旨昊
	 * 2011-3-23下午04:26:51
	 * @param aRecaVO
	 * @throws BusinessException
	 * 说明：新加薪酬发放时数据来源于薪酬部门管理项目的值
	 * @throws NamingException 
	 * @throws SystemException 
	 * @throws SQLException 
	 */
	public void reCaculateHRP(ReCacuVO aRecaVO) throws BusinessException{
		String[][] hrpItems = aRecaVO.getHRPItem();
		ArrayList<String[]> kk_list = new ArrayList<String[]>();
		ArrayList<String[]> kkdays_list = new ArrayList<String[]>();
		ArrayList<String[]> jj_list = new ArrayList<String[]>();
		if(hrpItems==null||hrpItems.length<=0) return;

		for(int i=0;i<hrpItems.length;i++){
			if(hrpItems[i][1].indexOf("valueofNkkmny")>=0){
				kk_list.add(hrpItems[i]);
			}if(hrpItems[i][1].indexOf("valueofMoneys")>=0){
				kkdays_list.add(hrpItems[i]);
			}else{
				jj_list.add(hrpItems[i]);
			}
		}
		if(jj_list!=null&&jj_list.size()>0){//新华这边推式生成，不从这边走，方法注释掉
			//reCaculateHRP_jj(aRecaVO, jj_list.toArray(new String[jj_list.size()][2]));
		}
		if(kk_list!=null&&kk_list.size()>0){//
			reCaculateHRP_kk(aRecaVO, kk_list.toArray(new String[kk_list.size()][2]));
		}
		if(kkdays_list!=null&&kkdays_list.size()>0){//
			reCaculateHRP_kkdays(aRecaVO, kkdays_list.toArray(new String[kkdays_list.size()][2]));
		}
	}

	public void reCaculateHRP_kkdays(ReCacuVO aRecaVO,String[][] hrpItems ) throws BusinessException{
		try{
			String vyear = aRecaVO.getWaYear();
			String vperiod = aRecaVO.getWaPeriod();
			String pk_corp = aRecaVO.getPk_corp();
			ClassitemVO[] itemvos = aRecaVO.getClassitemVOs();
			HashMap<String,ClassitemVO> map = new HashMap<String, ClassitemVO>();
			if(itemvos!=null){
				for(ClassitemVO vo:itemvos){
					map.put(vo.getIItemID(), vo);
				}
			}
			String classid = aRecaVO.getWaClassVO().getPrimaryKey();

			//SuperDMO dmo = new SuperDMO();
			if (hrpItems == null || hrpItems.length < 1) {
				return;
			}

			String[] itemcodes = new String[hrpItems.length];
			String[] itemtypes = new String[hrpItems.length];
			String[] itemformult = new String[hrpItems.length];
			for(int i=0;i<hrpItems.length;i++){

			}
			for(int i=0;i<itemtypes.length;i++){
				itemcodes[i] = hrpItems[i][0];
				itemformult[i] = hrpItems[i][1];
				itemtypes[i] = "1";
			}

			String wheresql_a  = aRecaVO.getSelCondition();
			DataDMO datadmo = new DataDMO();
			DataVO[] datavos = datadmo.queryAll(classid, vyear, vperiod, itemcodes, itemtypes, wheresql_a);
			//绩效奖金得取前两个期间的数据
//			String lastyear = "";
//			String lastperiod = "";
//			String lastyear_last = "";
//			String lastperiod_last = "";
//			int period = Integer.parseInt(vperiod);
//			if(period == 1){
//				lastyear = (Integer.parseInt(vyear)-1)+"";
//				lastperiod = "11";
//			}else if(period == 2){
//				lastyear = (Integer.parseInt(vyear)-1)+"";
//				lastperiod = "12";
//			}else if(period<=11){
//				lastyear = vyear;
//				lastperiod = "0"+(Integer.parseInt(vperiod)-2);
//			}else{
//				lastyear = vyear;
//				lastperiod = ""+(Integer.parseInt(vperiod)-2);
//			}
//
//			period = Integer.parseInt(lastperiod);
//			if(period==1){
//				lastyear_last = (Integer.parseInt(lastyear)-1)+"";
//				lastperiod_last = "12";
//			}else if(period<=10){
//				lastyear_last = lastyear;
//				lastperiod_last = "0"+(Integer.parseInt(lastperiod)-1);
//			}else{
//				lastyear_last = lastyear;
//				lastperiod_last = ""+(Integer.parseInt(lastperiod)-1);
//			}

			if(datavos==null||datavos.length<=0){
				return;
			}
			//人员的月考核奖数据字典：key：人员pk_psndoc， value：月考核奖金额
			HashMap<String,UFDouble> ykhMap = new HashMap<String, UFDouble>();
			//人员的一次性奖金数据字典：key：人员pk_psndoc， value：一次性奖金金额
			HashMap<String,UFDouble> ycxMap = new HashMap<String, UFDouble>();
			//查询当前期间下所有的已分配金额的人员
			String whereStr = "SELECT * FROM wa_psn_item_b WHERE pk_psn_item_h IN(SELECT pk_psn_item_h FROM wa_psn_item_h WHERE nvl(dr,0)=0 AND pk_corp = '" + pk_corp + "' AND vyear='" + vyear + "' AND vperiod='" + vperiod + "' AND pk_item12 = 2 AND pk_item14 = 2 AND vbillstatus_audit = 2)";
			BaseDAO baseDao = new BaseDAO();
			List<PsnClassItemBVO> list = (List<PsnClassItemBVO>) baseDao.executeQuery(whereStr, new BeanListProcessor(PsnClassItemBVO.class));
			if(list != null && !list.isEmpty()){
				for(PsnClassItemBVO vo : list){
					//月考核奖
					UFDouble ykh = vo.getNmny1() != null ? vo.getNmny1() : new UFDouble(0);
					if(ykhMap.containsKey(vo.getPk_psndoc())){
						UFDouble ykhTotal = ykhMap.get(vo.getPk_psndoc()).add(ykh);
						ykhMap.put(vo.getPk_psndoc(), ykhTotal);
					}else{
						ykhMap.put(vo.getPk_psndoc(), ykh);
					}
					//一次性奖金
					UFDouble ycx = vo.getNmny2() != null ? vo.getNmny2() : new UFDouble(0);
					if(ycxMap.containsKey(vo.getPk_psndoc())){
						UFDouble ycxTotal = ycxMap.get(vo.getPk_psndoc()).add(ycx);
						ycxMap.put(vo.getPk_psndoc(), ycxTotal);
					}else{
						ycxMap.put(vo.getPk_psndoc(), ycx);
					}
				}
			}
			
			BaseDAO dao = new BaseDAO();
			for(int i=0;i<datavos.length;i++){
				//月考核奖
				UFDouble nmny1 = ykhMap.get(datavos[i].getPsnid());
				//一次性奖金
				UFDouble nmny2 = ycxMap.get(datavos[i].getPsnid());
				String updatesql = "update wa_data set dr=0 ";
				for(int j=0;j<hrpItems.length;j++){
					UFDouble value = new UFDouble(0.0);
					String name = hrpItems[j][1].substring(hrpItems[j][1].indexOf("(")+1, hrpItems[j][1].length()-1);
					if(name.equals("ykhmoney")){
						value = nmny1;
					}else if(name.equals("ycxmoney")){
						value = nmny2;
					}
					if(value == null) value = new UFDouble(0.0);
					updatesql += "," + hrpItems[j][0] + "=" + value + " ";
				}
				updatesql+=" where pk_wa_data='"+datavos[i].getPrimaryKey()+"' ";
				dao.executeUpdate(updatesql);
			}
		}catch(SQLException e){
			e.printStackTrace();
		}catch(NamingException e){
			e.printStackTrace();
		}catch(SystemException e){
			e.printStackTrace();
		}
	}


	public void reCaculateHRP_kk(ReCacuVO aRecaVO,String[][] hrpItems ) throws BusinessException{
		try{
			String vyear = aRecaVO.getWaYear();
			String vperiod = aRecaVO.getWaPeriod();
			String pk_corp = aRecaVO.getPk_corp();
			ClassitemVO[] itemvos = aRecaVO.getClassitemVOs();
			HashMap<String,ClassitemVO> map = new HashMap<String, ClassitemVO>();
			if(itemvos!=null){
				for(ClassitemVO vo:itemvos){
					map.put(vo.getIItemID(), vo);
				}
			}
			String classid = aRecaVO.getWaClassVO().getPrimaryKey();

			SuperDMO dmo = new SuperDMO();
			if (hrpItems == null || hrpItems.length < 1) {
				return;
			}
			
			String[] itemcodes = new String[hrpItems.length];
			String[] itemtypes = new String[hrpItems.length];
			String[] itemformult = new String[hrpItems.length];
			for(int i=0;i<hrpItems.length;i++){

			}
			for(int i=0;i<itemtypes.length;i++){
				itemcodes[i] = hrpItems[i][0];
				itemformult[i] = hrpItems[i][1];
				itemtypes[i] = "1";
			}

			String wheresql_a  = aRecaVO.getSelCondition();
			DataDMO datadmo = new DataDMO();
			DataVO[] datavos = datadmo.queryAll(classid, vyear, vperiod, itemcodes, itemtypes,wheresql_a);
			//津贴得取上一期间的数据
			String lastyear = "";
			String lastperiod = "";
			String lastyear_last = "";
			String lastperiod_last = "";
			int period = Integer.parseInt(vperiod);
			if(period==1){
				lastyear = (Integer.parseInt(vyear)-1)+"";
				lastperiod = "12";
			}else if(period<=10){
				lastyear = vyear;
				lastperiod = "0"+(Integer.parseInt(vperiod)-1);
			}else{
				lastyear = vyear;
				lastperiod = ""+(Integer.parseInt(vperiod)-1);
			}

			period = Integer.parseInt(lastperiod);
			if(period==1){
				lastyear_last = (Integer.parseInt(lastyear)-1)+"";
				lastperiod_last = "12";
			}else if(period<=10){
				lastyear_last = lastyear;
				lastperiod_last = "0"+(Integer.parseInt(lastperiod)-1);
			}else{
				lastyear_last = lastyear;
				lastperiod_last = ""+(Integer.parseInt(lastperiod)-1);
			}

			if(datavos==null||datavos.length<=0) return;
			ArrayList<String> list_psn = new ArrayList<String>();
			for(DataVO vo:datavos){
				if(vo.getPsnid()!=null&&!list_psn.contains(vo.getPsnid().trim())){
					list_psn.add(vo.getPsnid().trim());
				}
			}
			StringBuffer buffer = new StringBuffer();
			String[] selkeys_gz = new String[]{"deductmny","ageallowancemny","travelallowancemny","teachallowancemny","teachallowancemny2","otherallowance1mny"};
			buffer.append(" select ");
			for(String selkey:selkeys_gz){
				buffer.append("  sum("+selkey+") "+selkey+" , ");	
			}
			buffer.append(" h.pk_psndoc  from tm_leave_b b inner join tm_leave_h h on b.pk_tm_h=h.pk_tm_h ");
			buffer.append(" where isnull(h.dr,0)=0 and b.confirmdays>0 and isnull(b.isback,'N')='N' and isnull(b.dr,0)=0 and b.isconfirm='Y' and b.isdeduct='Y' and month in ('"+vyear+""+vperiod+"','"+lastyear+""+lastperiod+"')  and h.pk_corp='"+pk_corp+"'  ");

			TempTableVO tmptb = null;
			try {
				TempTableDMO tempTableDMO = new TempTableDMO();
				tmptb = tempTableDMO.getTempByData(list_psn.toArray(new String[0]), "tmp_tm_leaveaa_b_temp",true);
			} catch (Exception e) {
				e.printStackTrace();
				throw new BusinessException("创建数据库临时表出错");
			}

			String inFk_usedepts = null;
			if (tmptb.getInSql() != null) {
				inFk_usedepts = tmptb.getInSql();
			} else {
				inFk_usedepts = "( select " + tmptb.getTempPK() + " from " + tmptb.getTempName() + " )";
			}
			if(inFk_usedepts!=null&&inFk_usedepts.trim().length()>0){
				buffer.append(" and  h.pk_psndoc in "+inFk_usedepts+" ");
			}
			buffer.append(" group by h.pk_psndoc ");
			IUAPQueryBS bs = NCLocator.getInstance().lookup(IUAPQueryBS.class);
			ArrayList<TMBVO> list_tmb = (ArrayList<TMBVO>)bs.executeQuery(buffer.toString(), new BeanListProcessor(TMBVO.class));

			HashMap<String,TMBVO> map_tmb = new HashMap<String, TMBVO>();
			if(list_tmb!=null&&list_tmb.size()>0){
				for(int i=0;i<list_tmb.size();i++){
					map_tmb.put(list_tmb.get(i).getPk_psndoc(), list_tmb.get(i));
				}
			}


			StringBuffer buffer_jt = new StringBuffer();
			String[] selkeys_jt = new String[]{"hightemallowance","healthallowance","riceallowance","personreplaceallo","personfillallo","postallo"};

			buffer_jt.append(" select ");
			for(String selkey:selkeys_jt){
				buffer_jt.append("  sum(isnull("+selkey+",0)) "+selkey+" , ");	
			}
			buffer_jt.append(" b.pk_psndoc  from tm_leave_b b inner join tm_leave_period h on b.pk_leave_k_h=h.pk_leave_k_h ");
			buffer_jt.append(" where isnull(h.dr,0)=0 and isnull(b.dr,0)=0   and h.vperiod in('"+lastyear+"-"+lastperiod+"')  and h.vbillstatus='1' and h.pk_corp='"+pk_corp+"'  ");

			if(inFk_usedepts!=null&&inFk_usedepts.trim().length()>0){
				buffer_jt.append(" and  b.pk_psndoc in "+inFk_usedepts+" ");
			}
			buffer_jt.append(" group by b.pk_psndoc ");
			String sql = buffer_jt.toString();
			ArrayList<TMBVO> list_tmb_jt = (ArrayList<TMBVO>)bs.executeQuery(sql, new BeanListProcessor(TMBVO.class));
			ArrayList<TMBVO> list_tmb_jt1 = (ArrayList<TMBVO>)bs.executeQuery(sql.replace("tm_leave_b", "tm_leave_new"), new BeanListProcessor(TMBVO.class));
			ArrayList<TMBVO> list_tmb_jt2 = (ArrayList<TMBVO>)bs.executeQuery(sql.replace("tm_leave_b", "tm_leave_old"), new BeanListProcessor(TMBVO.class));
			ArrayList<TMBVO> list_tmb_jt3 = (ArrayList<TMBVO>)bs.executeQuery(sql.replace("tm_leave_b", "tm_leave_other"), new BeanListProcessor(TMBVO.class));
			ArrayList<TMBVO> list_tmb_jt4 = (ArrayList<TMBVO>)bs.executeQuery(sql.replace("tm_leave_b", "tm_leave_retire"), new BeanListProcessor(TMBVO.class));
			ArrayList<TMBVO> list_jt = new ArrayList<TMBVO>();
			if(list_tmb_jt!=null&&list_tmb_jt.size()>0){
				list_jt.addAll(list_tmb_jt);
			}
			if(list_tmb_jt1!=null&&list_tmb_jt1.size()>0){
				list_jt.addAll(list_tmb_jt1);
			}
			if(list_tmb_jt2!=null&&list_tmb_jt2.size()>0){
				list_jt.addAll(list_tmb_jt2);
			}
			if(list_tmb_jt3!=null&&list_tmb_jt3.size()>0){
				list_jt.addAll(list_tmb_jt3);
			}
			if(list_tmb_jt4!=null&&list_tmb_jt4.size()>0){
				list_jt.addAll(list_tmb_jt4);
			}
			if(list_jt!=null&&list_jt.size()>0){
				for(int i=0;i<list_jt.size();i++){
					if(map_tmb.containsKey(list_jt.get(i).getPk_psndoc())){
						TMBVO oldvo = map_tmb.get(list_jt.get(i).getPk_psndoc());
						TMBVO newvo = list_jt.get(i);
						for(String selkey:selkeys_jt){
							oldvo.setAttributeValue(selkey,(oldvo.getAttributeValue(selkey)!=null?new UFDouble(oldvo.getAttributeValue(selkey).toString()):new UFDouble(0))
									.add(newvo.getAttributeValue(selkey)!=null?new UFDouble(newvo.getAttributeValue(selkey).toString()):new UFDouble(0)));
						}
						map_tmb.put(list_jt.get(i).getPk_psndoc(), oldvo);
					}else{
						map_tmb.put(list_jt.get(i).getPk_psndoc(), list_jt.get(i));
					}
				}
			}


			BaseDAO dao = new BaseDAO();
			for(int i=0;i<datavos.length;i++){
				TMBVO bvo = map_tmb.get(datavos[i].getPsnid());
				String updatesql = "update wa_data set dr=0 ";
				for(int j=0;j<hrpItems.length;j++){
					UFDouble value = new UFDouble(0.0);
					String name = hrpItems[j][1].substring(hrpItems[j][1].indexOf("(")+1,hrpItems[j][1].length()-1);
					if(bvo!=null){
						value = bvo.getAttributeValue(name)!=null?new UFDouble(bvo.getAttributeValue(name).toString()):new UFDouble(0);
					}
					if(value==null) value = new UFDouble(0.0);
					updatesql+=","+hrpItems[j][0]+"="+value+" ";
				}
				updatesql+=" where pk_wa_data='"+datavos[i].getPrimaryKey()+"' ";

				dao.executeUpdate(updatesql);
			}
		}catch(SQLException e){
			e.printStackTrace();
		}catch(NamingException e){
			e.printStackTrace();
		}catch(SystemException e){
			e.printStackTrace();
		}
	}
}
