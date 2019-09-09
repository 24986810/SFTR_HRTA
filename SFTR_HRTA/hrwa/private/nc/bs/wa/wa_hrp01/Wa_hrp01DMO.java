package nc.bs.wa.wa_hrp01;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import javax.naming.NamingException;


import nc.bs.dao.BaseDAO;
import nc.bs.framework.common.NCLocator;
import nc.bs.ml.NCLangResOnserver;
import nc.bs.pub.DataManageObject;
import nc.bs.pub.SuperDMO;
import nc.bs.pub.SystemException;
import nc.bs.wa.wa_hrp_005.FenPeiDMO;
import nc.impl.wa.wa_004.TaxbaseImpl;
import nc.impl.wa.wa_009.DataDMO;
import nc.impl.wa.wa_009.TaxgroupDAO;
import nc.impl.wa.wa_019.PeriodDMO;
import nc.itf.hr.pub.PubDelegator;
import nc.itf.hr.wa.IClassitem;
import nc.itf.hr.wa.IItem;
import nc.itf.hrp.pub.HRPPubTool;
import nc.itf.uif.pub.IUifService;
import nc.itf.wa.hrp.pub.IHRPWABtn;
import nc.itf.wa.wa_hrppub.WaHrpBillStatus;
import nc.jdbc.framework.SQLParameter;
import nc.jdbc.framework.processor.BeanListProcessor;
import nc.jdbc.framework.processor.ColumnListProcessor;
import nc.vo.bd.b06.PsndocVO;
import nc.vo.hr.comp.sort.ItemSortUtil;
import nc.vo.jcom.util.Convertor;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.VOStatus;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDouble;
import nc.vo.wa.wa_002.ClassitemVO;
import nc.vo.wa.wa_004.TaxbaseVO;
import nc.vo.wa.wa_008.WaclassVO;
import nc.vo.wa.wa_009.DataVO;
import nc.vo.wa.wa_009.ReCacuVO;
import nc.vo.wa.wa_019.PeriodVO;
import nc.vo.wa.wa_024.ItemVO;
import nc.vo.wa.wa_hrp01.SendClassOtherVO;
import nc.vo.wa.wa_hrp01.SendClassVO;
import nc.vo.wa.wa_hrp_002.PsnClassItemBBVO;
import nc.vo.wa.wa_hrp_002.PsnClassItemBVO;
import nc.vo.wa.wa_hrp_002.PsnClassItemHVO;
import nc.vo.wa.wa_hrp_002.PsnDeptSumVO;
import nc.vo.wa.wa_hrp_002.PsnQkBVO;
import nc.vo.wa.wa_hrp_002.PsnQkVO;
import nc.vo.wa.wa_hrp_002.PsnSjBVO;
import nc.vo.wa.wa_hrp_002.PsnSjVO;
import nc.vo.wa.wa_hrp_004.DeptMnyBVO;
import nc.vo.wa.wa_hrp_004.DeptMnyHVO;

public class Wa_hrp01DMO extends DataManageObject {
	public Wa_hrp01DMO() throws NamingException {
		super();
		// TODO Auto-generated constructor stub
	}


	BaseDAO dao;
	DataDMO ddmo;
	ArrayList<String> list_taxtableid;
	/**
	 * 薪资发放类别保存
	 * @param 
	 * @param SendClassVO vo  要更新的数据 insert or  update
	 * @return SendClassVO
	 * @throws BusinessException 
	 */
	public SendClassVO saveorupdatevo(SendClassVO vo) throws BusinessException {
		dao=getDao();
		if(vo.getPk_sendclass()!=null){//已经有了。
			StringBuffer sb=new StringBuffer();
			sb.append(" update wa_sendclass_other set ");
			if(vo.getMemo2()==null){//备注
				sb.append("vmemo=null,");//"where pk_sendclass  ='"+vo.getPk_sendclass()+"'"
			}else{
				sb.append("vmemo='"+vo.getMemo2()+"',");
			}
			if(vo.getClassattr()==null){//类别属性
				sb.append("classattr=null ");
			}else{//" update wa_sendclass_other set  classattr='"+vo.getClassattr()+"' ,  vmemo='"+vo.getMemo2()+"' where pk_sendclass  ='"+vo.getPk_sendclass()+"'"
				sb.append("classattr='"+vo.getClassattr()+"' ");
			}

			sb.append(" where pk_sendclass  ='"+vo.getPk_sendclass()+"'");
			dao.executeUpdate(sb.toString());
		}else{
			SendClassOtherVO insertvo=new SendClassOtherVO();
			insertvo.setPk_defdoc(vo.getPk_defdoc());
			insertvo.setClassattr(vo.getClassattr());
			dao.insertVO(insertvo);
		}
		return vo;
	}
	public BaseDAO getDao() {
		if(dao==null)dao=new BaseDAO();
		return dao;
	}
	/**
	 * 计算扣税基数及已扣税计算，及已扣税数
	 * @param hvo
	 * @throws BusinessException
	 */
	protected PsnClassItemBVO[] countBaseTaxAndOld(PsnClassItemHVO hvo,WaclassVO classvo,PsnClassItemBVO[] bvos,boolean flag) throws BusinessException{
//		检查类别是否合法
		String sqlpk_taxgroup = "select distinct pk_taxgroup from wa_taxgrpmember where pk_waclass = '" + hvo.getPk_wa_class() + "' ";
		List<String> pk_taxgroupList = (List<String>)getDao().executeQuery(sqlpk_taxgroup, new ColumnListProcessor());
		String pk_taxgroup = null;
		if(hvo.getPk_billtype()!=null&&!hvo.getPk_billtype().equals("73RP")){
			if(pk_taxgroupList == null || pk_taxgroupList.size() == 0){
				throw new BusinessException("Selected class are not in a taxgroup");
			}else if(pk_taxgroupList.size() > 1){
				throw new BusinessException("Selected class are in more than one taxgroup");
			}
			pk_taxgroup = pk_taxgroupList.get(0);
		}

		HashMap<String,DataVO> map_data = new HashMap<String, DataVO>();
		try {
			DataVO[] datavos = getDDmo().queryAll(hvo.getPk_wa_class(), hvo.getVyear(), hvo.getVperiod(), null, null);
			if(datavos!=null&&datavos.length>0){
				for(DataVO vo:datavos){
					map_data.put(vo.getPsnid(),vo);
					vo.getTaxtableid();
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new BusinessException(e.getMessage());
		}


		String sql = " stafftype='1' and isnull(dr,0)=0 and pk_psn_item_h='"+hvo.getPrimaryKey()+"' ";
		if(flag){
			sql+=" and isnull(biscount,'N')='N' ";
		}else{
			sql+=" and isnull(biscount,'N')='Y' ";
		}
		if(bvos!=null&&bvos.length>0){
			ArrayList<String> list_bkey = new ArrayList<String>();
			for(PsnClassItemBVO vo:bvos){
				list_bkey.add(vo.getPrimaryKey());
			}
			sql+=HRPPubTool.formInSQL(" pk_psn_item_b ", list_bkey);
		}

		Collection<PsnClassItemBVO> coll = getDao().retrieveByClause(PsnClassItemBVO.class,sql);
		if(coll==null||coll.size()<=0) return null;
		IItem item = (IItem)NCLocator.getInstance().lookup(IItem.class);
		ItemVO[] itemvos = item.queryAllItem(hvo.getPk_corp());
		HashMap<String,ItemVO> mapitem = new HashMap<String, ItemVO>();

		if(itemvos!=null&&itemvos.length>0){
			for(ItemVO itemvo:itemvos){
				mapitem.put(itemvo.getPrimaryKey(), itemvo);
			}
		}


		IClassitem classitem = (IClassitem)NCLocator.getInstance().lookup(IClassitem.class);
		ClassitemVO[] vos = classitem.queryAllClassItem(hvo.getPk_wa_class(),hvo.getVyear(),hvo.getVperiod());
		HashMap<String,ClassitemVO> mapclassitem = new HashMap<String, ClassitemVO>();
		if(vos!=null&&vos.length>0){
			for(ClassitemVO itemvo:vos){
				mapclassitem.put(itemvo.getPk_wa_item(), itemvo);
			}
		}
		String type = hvo.getPk_billtype().equals("73RP")?"2":"1";
		Collection<PsnSjVO> coll2 = getDao().retrieveByClause(PsnSjVO.class, 
				" isnull(pk_billtype,'1')='"+type+"' and isnull(dr,0)=0 and pk_corp='"+hvo.getPk_corp()+"' and vyear='"+hvo.getVyear()+"' and vperiod='"+hvo.getVperiod()+"' ");
		PsnSjVO[] sjvos =  coll2!=null?coll2.toArray(new PsnSjVO[0]):null;
		HashMap<String,PsnSjVO> map_sj = new HashMap<String, PsnSjVO>();
		if(sjvos!=null&&sjvos.length>0){
			for(PsnSjVO vo:sjvos){
				map_sj.put(vo.getPk_psn(), vo);
			}
		}
		int index = hvo.getNindex();
		if(index<=0) return null;
		bvos = coll.toArray(new PsnClassItemBVO[0]);
		boolean isInTaxgroup = new TaxgroupDAO().isInTaxGroup(hvo.getPk_wa_class());
		/*先重新计算本次纳税基数及已纳税基数*/
		ArrayList<String> list_psn_bad = new ArrayList<String>();
		list_taxtableid = new ArrayList<String>();
		String newhid = null;
		if(!flag){
			PsnClassItemHVO newhvo = (PsnClassItemHVO)hvo.clone();
			IUifService uif = NCLocator.getInstance().lookup(IUifService.class);
			newhvo.setVbillno(uif.getBillNo(hvo.getPk_billtype(),hvo.getPk_corp(), null, null));
			newhvo.setVmemo("由"+hvo.getVbillno()+"取消复核生成，不能操作");
			newhvo.setVdef1(hvo.getPrimaryKey());
			newhvo.setBisreturn(new UFBoolean(true));
			newhvo.setPrimaryKey(null);
			newhvo.setTotalmoney(new UFDouble(0));
			newhid = getDao().insertVO(newhvo);
		}
		ArrayList<PsnClassItemBVO> newlist = new ArrayList<PsnClassItemBVO>();
		ArrayList<PsnClassItemBVO> oldlist = new ArrayList<PsnClassItemBVO>();
		for(PsnClassItemBVO vo:bvos){
			if(!flag){
				PsnClassItemBVO oldbvo = (PsnClassItemBVO)vo.clone();
//				oldbvo.setPrimaryKey(null);
				oldbvo.setPk_psn_item_h(newhid);
				oldlist.add(oldbvo);
				vo.setPrimaryKey(null);
			}

			DataVO datavo = map_data.get(vo.getPk_psndoc());
			if(datavo==null){
				list_psn_bad.add(vo.getPk_psndoc());
				continue;
			}
			if(datavo.getItaxflag()!=null&&datavo.getItaxflag()==1){
				vo.setCtaxtableid(datavo.getTaxtableid()!=null&&datavo.getTaxtableid().trim().length()>0?datavo.getTaxtableid():(classvo.getTaxtableid()!=null?classvo.getTaxtableid():"0001691000000001D7IW"));
				if(vo.getCtaxtableid()!=null&&vo.getCtaxtableid().trim().length()>0&&!list_taxtableid.contains(vo.getCtaxtableid())){
					list_taxtableid.add(vo.getCtaxtableid());
				}
				PsnSjVO sjvo = map_sj.get(vo.getPk_psndoc());

				vo.setNoldnsmny(sjvo!=null&&sjvo.getNyksjs()!=null?sjvo.getNyksjs():new UFDouble(0));
				vo.setNoldsmny(sjvo!=null&&sjvo.getNsmny()!=null?sjvo.getNsmny():new UFDouble(0));
				vo.setNoldnsmny_nz(sjvo!=null&&sjvo.getNyksjs_nz()!=null?sjvo.getNyksjs_nz():new UFDouble(0));
				vo.setNoldsmny_nz(sjvo!=null&&sjvo.getNsmny_nz()!=null?sjvo.getNsmny_nz():new UFDouble(0));

				UFDouble nmny = new UFDouble(0);
				for(int i=0;i<index;i++){
					ClassitemVO itemvo = mapclassitem.get((String)hvo.getAttributeValue("pk_item"+(i+1)+""));
					ItemVO iitemvo = mapitem.get((String)hvo.getAttributeValue("pk_item"+(i+1)+""));
					Object value = vo.getAttributeValue("nmny"+(i+1)+"");
					if(itemvo!=null){
						if(itemvo!=null&&itemvo.getItaxflag()!=null&&itemvo.getItaxflag()==1){
							if(iitemvo.getIproperty()!=null&&iitemvo.getIproperty()==1){//减项
								nmny = nmny.sub(value!=null?new UFDouble(value.toString()):new UFDouble(0));
							}else{
								nmny = nmny.add(value!=null?new UFDouble(value.toString()):new UFDouble(0));
							}
						}
					}
				}
				vo.setNbcnsmny(nmny);
			}else{
				vo.setCtaxtableid(null);
				vo.setNbcnsmny(null);
			}
			vo.setBiscount(new UFBoolean(true));
			vo.setDcountdate(hvo.getDapprovedate());
			vo.setVcountpsnid(hvo.getVapproveid());

			if(!flag){
				PsnClassItemBVO newbvo = (PsnClassItemBVO)vo.clone();
				newbvo.setPk_psn_item_h(newhid);
				newbvo.setPrimaryKey(null);
				newbvo.setNbcnsmny(new UFDouble(0).sub(vo.getNbcnsmny()!=null?vo.getNbcnsmny():new UFDouble(0)));//设置为负数 然后算税
				for(int i=0;i<index;i++){
					newbvo.setAttributeValue("nmny"+(i+1)+"",new UFDouble(0).sub(vo.getAttributeValue("nmny"+(i+1)+"")!=null?new UFDouble(vo.getAttributeValue("nmny"+(i+1)+"").toString()):new UFDouble(0)));
				}
				newbvo.setAttributeValue("nmny",new UFDouble(0).sub(vo.getAttributeValue("nmny")!=null?new UFDouble(vo.getAttributeValue("nmny").toString()):new UFDouble(0)));
				newbvo.setNaftersmny(newbvo.getNmny());
				newlist.add(newbvo);
				String[] nullkeys = new String[]{"nsmny","nbcnsmny","ctaxtableid","noldsmny","noldnsmny","ndkmny","nsqqkmny","nbqqkmny","nsfmny"};
				for(String key:nullkeys){
					vo.setAttributeValue(key,null);
				}
				vo.setBiscount(new UFBoolean(false));
				vo.setDcountdate(null);
				vo.setVcountpsnid(null);
				vo.setNaftersmny(vo.getNmny());
				vo.setNsfmny(vo.getNmny());
				/*取消复核时，生成一张负单据冲销已计税金额，原因是当前单据可能不是最后一张计税的，后面的已计税不能取消*/
			}



		}


		if(!flag&&newlist!=null&&newlist.size()>0){
			getDao().insertVOArray(newlist.toArray(new PsnClassItemBVO[0]));
			getDao().insertVOArray(bvos);
			Collection<PsnClassItemBVO> coll4 = getDao().retrieveByClause(PsnClassItemBVO.class, " isnull(dr,0)=0 and pk_psn_item_h='"+newhid+"'");
			bvos = coll4.toArray(new PsnClassItemBVO[0]);
			if(oldlist!=null&&oldlist.size()>0){
				getDao().updateVOArray(oldlist.toArray(new PsnClassItemBVO[0]));
			}
		}
		if(flag){
			getDao().updateVOArray(bvos);
		}
		if(list_psn_bad!=null&&list_psn_bad.size()>0){
			Collection<PsndocVO> coll3= getDao().retrieveByClause(PsndocVO.class, " isnull(dr,0)=0 and pk_corp='"+hvo.getPk_corp()+"'");
			HashMap<String,String> map_psn = new HashMap<String, String>();
			if(coll3!=null&&coll3.size()>0){
				PsndocVO[] psnvos = coll3.toArray(new PsndocVO[0]);
				for(int i=0;i<psnvos.length;i++){
					map_psn.put(psnvos[i].getPrimaryKey(),psnvos[i].getPsncode());
				}
			}
			String msg = "以下员工薪资档案不存在，请维护：";
			for(int i=0;i<list_psn_bad.size();i++){
				msg+=""+map_psn.get(list_psn_bad.get(i))+", ";
			}
			//throw new BusinessException(msg);
		}
		if(isInTaxgroup){
			List<PeriodVO> list = getUnitClassPeriodVOList(hvo);
			String unitWaPk = null;
			if (list != null && list.size() > 0) {
				for (int n = 0; n < list.size(); n++) {
					nc.vo.wa.wa_019.PeriodVO unitPeriodVO = list.get(n);
					if (unitPeriodVO != null) {
						unitWaPk = unitPeriodVO.getClassid();
					}
				}
			}
//			第三步：更新期间对类别的依赖关系， 只记录最后一个就可以了。
			String sqlUpdate = null;
			if (unitWaPk != null&&hvo.getPk_billtype()!=null&&!hvo.getPk_billtype().equals("73RP")) {
				sqlUpdate = "update wa_periodstate set cpreclassid = '"+pk_taxgroup + "_" + unitWaPk + ";"+"' where pk_periodset = '"+hvo.getPk_wa_period()+"' and classid='"+hvo.getPk_wa_class()+"'";
			}else{
				sqlUpdate = "update wa_periodstate set cpreclassid = null where pk_periodset = '"+hvo.getPk_wa_period()+"' and classid='"+hvo.getPk_wa_class()+"'";
			}
			getDao().executeUpdate(sqlUpdate);
		}
		return bvos;
	}


	@SuppressWarnings("unchecked")
	private LinkedList<PeriodVO> getUnitClassPeriodVOList(PsnClassItemHVO hvo) throws BusinessException{
		String pk_wa_class = hvo.getPk_wa_class();

		String caccyear = hvo.getVyear();
		String caccperiod= hvo.getVperiod();

		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append("  select distinct wa_periodstate.classid, ");  //   1
		sqlBuffer.append("                  wa_period.cyear, ");   //   2
		sqlBuffer.append("                  wa_period.cperiod, ");  //   3
		sqlBuffer.append("                  wa_periodstate.cpreclassid ");  //   4
		sqlBuffer.append("    from wa_period ");
		sqlBuffer.append("   inner join wa_periodstate on wa_periodstate.pk_periodset =wa_period.pk_wa_period ");
		sqlBuffer.append("   where wa_periodstate.classid in ");
		sqlBuffer.append("         (select wa_taxgrpmember.pk_waclass from wa_taxgrpmember");
		sqlBuffer.append("           where wa_taxgrpmember.pk_taxgroup in ");
		sqlBuffer.append("                 (select wa_taxgrpmember.pk_taxgroup ");
		sqlBuffer.append("                    from wa_taxgrpmember ");
		sqlBuffer.append("                   where wa_taxgrpmember.pk_waclass = ?)) ");
		sqlBuffer.append("     and wa_periodstate.classid <> ? ");
		sqlBuffer.append("     and wa_periodstate.ipayoffflag = 1 ");
		sqlBuffer.append("     and wa_period.caccyear = ? ");
		sqlBuffer.append("     and wa_period.caccperiod = ? ");

		SQLParameter parameter = new SQLParameter();
		parameter.addParam(pk_wa_class);
		parameter.addParam(pk_wa_class);
		parameter.addParam(caccyear);
		parameter.addParam(caccperiod);

		List<PeriodVO> preCaculateClass = (List<PeriodVO> )getDao().executeQuery(sqlBuffer.toString(), parameter, new BeanListProcessor(PeriodVO.class));
		HashMap<PeriodVO, List<PeriodVO>> preClassHashMap = orgnizeRelation(preCaculateClass);
		//排好顺序
		return new ItemSortUtil<PeriodVO>().toplogicalSort(preClassHashMap);

	}
	/**
	 * 
	 * Created on 2008-12-29
	 * @author zhangg
	 * @param preCaculateClass
	 * @return
	 */
	private HashMap<PeriodVO, List<PeriodVO>> orgnizeRelation(List<PeriodVO> preCaculateClass){
		HashMap<PeriodVO, List<PeriodVO>> itemHashMap = new LinkedHashMap<PeriodVO, List<PeriodVO>>();
		for (PeriodVO periodVO : preCaculateClass) {
			List<PeriodVO> depentdList = null;
			String cpreclassid = periodVO.getCpreclassid();
			if(cpreclassid != null){
				depentdList = new LinkedList<PeriodVO>();
				//look for the depended classes
				String prePk_wa_class = cpreclassid.substring(cpreclassid.indexOf("_") + 1, cpreclassid.indexOf(";"));
				for (PeriodVO dependedPeriod : preCaculateClass) {
					if(dependedPeriod.getClassid().equals(prePk_wa_class)){
						depentdList.add(dependedPeriod);
					}
				}
			}
			itemHashMap.put(periodVO, depentdList);
		}
		return itemHashMap;
	}
	private DataDMO getDDmo() {
		if(ddmo==null){
			try {
				ddmo = new DataDMO();
			} catch (SystemException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NamingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return ddmo;
	}
	public void savePsnItemBB(PsnClassItemHVO hvo) throws BusinessException{
		BaseDAO dao = new BaseDAO();
		dao.deleteByClause(PsnClassItemBBVO.class, " isnull(dr,0)=0 and pk_psn_item_h='"+hvo.getPrimaryKey()+"'");
		SuperDMO dmo = new SuperDMO();
		int index = hvo.getNindex()!=null?hvo.getNindex():0;
		PsnClassItemBVO[] bvos = (PsnClassItemBVO[])dmo.queryByWhereClause(PsnClassItemBVO.class, " isnull(dr,0)=0 and pk_psn_item_h='"+hvo.getPrimaryKey()+"'");
		if(bvos==null) return;
		UFDouble mny1 = new UFDouble(0);
		UFDouble mny2 = new UFDouble(0);
		UFDouble mny3 = new UFDouble(0);
		for(int i=0;i<bvos.length;i++){
			mny1 = mny1.add(bvos[i].getNmny()!=null?bvos[i].getNmny():new UFDouble(0));
			mny2 = mny2.add((bvos[i].getNsmny()!=null?bvos[i].getNsmny():new UFDouble(0)).sub(bvos[i].getNdkmny()!=null?bvos[i].getNdkmny():new UFDouble(0)));
			mny3 = mny3.add((bvos[i].getNaftersmny()!=null?bvos[i].getNaftersmny():new UFDouble(0)).add(bvos[i].getNdkmny()!=null?bvos[i].getNdkmny():new UFDouble(0)));
		}
		//注掉:本次可分配金额合计不能修改  tianxfc 2018-12-06
		//hvo.setTotalmoney(mny1);
		hvo.setTotalsmny(mny2);
		hvo.setTotalaftersmny(mny3);

		dmo.update(hvo);

		if(hvo.getVbillstatus_audit()!=null&&hvo.getVbillstatus_audit()==WaHrpBillStatus.PASS){
			ArrayList<PsnClassItemBBVO> list = new ArrayList<PsnClassItemBBVO>();
			for(int i=0;i<bvos.length;i++){
				for(int j=0;j<index;j++){
					PsnClassItemBBVO bbvo = new PsnClassItemBBVO();
					bbvo.setPrimaryKey(null);
					bbvo.setStatus(VOStatus.NEW);
					bbvo.setDr(0);
					bbvo.setPk_psndoc(bvos[i].getPk_psndoc());
					bbvo.setPk_psn_item_b(bvos[i].getPrimaryKey());
					bbvo.setPk_psn_item_h(hvo.getPrimaryKey());
					bbvo.setPk_corp(hvo.getPk_corp());
					bbvo.setPk_dept(bvos[i].getPk_wa_dept());
					bbvo.setVperiod(hvo.getVperiod());
					bbvo.setVyear(hvo.getVyear());
					bbvo.setPk_wa_class(hvo.getPk_wa_class());
					bbvo.setDapprovedate(hvo.getDapprovedate());
					bbvo.setPk_classitem((String)hvo.getAttributeValue("pk_item"+""+(j+1)+""));
					bbvo.setNmny(bvos[i].getAttributeValue("nmny"+(j+1)+"")!=null?new UFDouble(bvos[i].getAttributeValue("nmny"+(j+1)+"").toString()):new UFDouble(0.0));
					if(bbvo.getNmny()!=null&&bbvo.getNmny().doubleValue()!=0){
						list.add(bbvo);
					}
				}
			}
			if(list!=null&&list.size()>0){
				dmo.insertArray(list.toArray(new PsnClassItemBBVO[0]));
			}
		}
	}
	public void countAndunAudit(PsnClassItemHVO hvo,PsnClassItemBVO[] bvos) throws BusinessException{
		Collection<PsnClassItemBVO> coll = getDao().retrieveByClause(PsnClassItemBVO.class, " isnull(dr,0)=0 and pk_psn_item_h='"+hvo.getPrimaryKey()+"' " );
		bvos = coll.toArray(new PsnClassItemBVO[0]);
		for(PsnClassItemBVO vo:bvos){
			vo.setNsmny(null);
			vo.setNaftersmny(vo.getNmny());
			vo.setNsfmny(vo.getNmny());
			vo.setNbcnsmny(null);
			vo.setNbqqkmny(null);
			vo.setNdkmny(null);
			vo.setNoldnsmny(null);
			vo.setNoldsmny(null);
			vo.setNsqqkmny(null);
			vo.setCtaxtableid(null);
			vo.setNsmny_nz(null);
			vo.setNoldnsmny_nz(null);
			vo.setNoldsmny_nz(null);
			vo.setBiscount(new UFBoolean(false));
		}
		getDao().updateVOArray(bvos);
		if(bvos==null) return;
		SuperDMO dmo = new SuperDMO();
		BaseDAO dao = new BaseDAO();
		String type = "1";
		if(hvo.getPk_billtype()!=null&&hvo.getPk_billtype().equals("73RP")){
			type = "2";
		}
		PsnSjVO[] sjhvos = (PsnSjVO[])dmo.queryByWhereClause(PsnSjVO.class, " isnull(pk_billtype,'1')='"+type+"' and isnull(dr,0)=0 and  pk_corp='"+hvo.getPk_corp()+"' and vyear='"+hvo.getVyear()+"' and vperiod='"+hvo.getVperiod()+"' ");
		HashMap<String,PsnSjVO> map = new HashMap<String, PsnSjVO>();
		if(sjhvos!=null&&sjhvos.length>0){
			for(PsnSjVO vo:sjhvos){
				map.put(vo.getPk_psn(),vo);
			}
		}
		PsnSjBVO[] sjbvos = (PsnSjBVO[])dmo.queryByWhereClause(PsnSjBVO.class," isnull(dr,0)=0 and csourcehid='"+hvo.getPrimaryKey()+"' ");
		if(sjbvos!=null&&sjbvos.length>0){
			ArrayList<PsnSjVO> list = new ArrayList<PsnSjVO>();
			for(PsnSjBVO sbvo:sjbvos){
				PsnSjVO svo = map.get(sbvo.getPk_psn());
				svo.setNdkmny((svo.getNdkmny()!=null?svo.getNdkmny():new UFDouble(0)).sub(sbvo.getNdkmny()!=null?sbvo.getNdkmny():new UFDouble(0)));
				svo.setNsmny((svo.getNsmny()!=null?svo.getNsmny():new UFDouble(0)).sub(sbvo.getNsmny()!=null?sbvo.getNsmny():new UFDouble(0)));
				svo.setNyksjs((svo.getNyksjs()!=null?svo.getNyksjs():new UFDouble(0)).sub(sbvo.getNyksjs()!=null?sbvo.getNyksjs():new UFDouble(0)));
				svo.setNsmny_nz((svo.getNsmny_nz()!=null?svo.getNsmny_nz():new UFDouble(0)).sub(sbvo.getNsmny_nz()!=null?sbvo.getNsmny_nz():new UFDouble(0)));
				svo.setNyksjs_nz((svo.getNyksjs_nz()!=null?svo.getNyksjs_nz():new UFDouble(0)).sub(sbvo.getNyksjs_nz()!=null?sbvo.getNyksjs_nz():new UFDouble(0)));
				list.add(svo);
			}
			getDao().updateVOArray(list.toArray(new PsnSjVO[0]));
			getDao().deleteVOArray(sjbvos);
		}
		savePsnItemBB(hvo);
		genDeptmny_d( hvo,bvos);
	}
	/**
	 * 复核通过并计算表体明细税，写入税额明细表
	 * @param hvo
	 * @throws BusinessException
	 */
	public void countAndAudit(PsnClassItemHVO hvo,PsnClassItemBVO[] bvos,boolean flag) throws BusinessException {
		// TODO Auto-generated method stub
		if(hvo==null) return;
		if(!flag){
			countAndunAudit(hvo,bvos);
			return;
		}
		getDao().updateVO(hvo);
		if(hvo.getVbillstatus_audit()==WaHrpBillStatus.NOPASS_RETURN){
			return;//驳回不再往下走，跟新表头状态即可
		}


		WaclassVO classvo = (WaclassVO)getDao().retrieveByPK(WaclassVO.class, hvo.getPk_wa_class());
		if(classvo==null){
			throw new BusinessException("薪资类别错误");
		}
		bvos = countBaseTaxAndOld(hvo,classvo,bvos,flag);



		if(bvos==null||bvos.length<=0) return;

		//缺省税率表  caculateTax_yh

		String sql = " isnull(wa_psn_item_b.dr,0)=0  ";
		sql+= " and pk_psn_item_h='"+bvos[0].getPk_psn_item_h()+"' ";
		if(bvos!=null&&bvos.length>0){
			ArrayList<String> list_bkey = new ArrayList<String>();
			for(PsnClassItemBVO vo:bvos){
				list_bkey.add(vo.getPrimaryKey());
			}
			sql+=HRPPubTool.formInSQL(" pk_psn_item_b ", list_bkey);
		}
		if(list_taxtableid!=null&&list_taxtableid.size()>0){//基本税批量算完  给每个人设置税率表主键，循环计算各税率表数据 方法都类似于基本表
			for(int i=0;i<list_taxtableid.size();i++){
				TaxbaseVO defaultTaxTable = new TaxbaseImpl().findTaxbaseByPk(list_taxtableid.get(i));
				caculateTaxByTaxBase( bvos, hvo,classvo,defaultTaxTable,true,sql);
			}
		}




		/*算完税跟新hrp个人税率表*/
		Collection<PsnClassItemBVO> coll = getDao().retrieveByClause(PsnClassItemBVO.class, sql );
		bvos = coll.toArray(new PsnClassItemBVO[0]);
		onWriteSj(hvo, bvos);

//		if(flag){//复核时计算欠款
		coll = getDao().retrieveByClause(PsnClassItemBVO.class, sql );
		bvos = coll.toArray(new PsnClassItemBVO[0]);//算完抵扣税后再算扣款
		Collection<PsnQkVO> coll2= getDao().retrieveByClause(PsnQkVO.class, " isnull(dr,0)=0 and pk_corp='"+hvo.getPk_corp()+"' " );

		/**  这个月还不能还欠款  4月份
		HashMap<String, PsnQkVO> qk_map = new HashMap<String, PsnQkVO>();
		if(coll2!=null&&coll2.size()>0){
			PsnQkVO[] qkvos = coll2.toArray(new PsnQkVO[0]);//算完抵扣税后再算扣款
			for(int i=0;i<coll2.size();i++){
				qk_map.put(qkvos[i].getPk_psn(), qkvos[i]);//负数
			}
		} 


		ArrayList<PsnQkBVO> list_b = new ArrayList<PsnQkBVO>();
		ArrayList<PsnQkVO> list_h = new ArrayList<PsnQkVO>();

		for(PsnClassItemBVO bvo:bvos){
			PsnQkVO qkvo = null;
			  这个月还不能还欠款  4月份
			if(qk_map.containsKey(bvo.getPk_psndoc())){
				qkvo = qk_map.get(bvo.getPk_psndoc());
			}

			bvo.setNsqqkmny(qkvo!=null&&qkvo.getNmny()!=null?qkvo.getNmny():new UFDouble(0));
			if(bvo.getNsfmny().add(bvo.getNsqqkmny()).doubleValue()>0){
				bvo.setNsfmny(bvo.getNsfmny().add(bvo.getNsqqkmny()));
				bvo.setNbqqkmny(new UFDouble(0));
				if(qkvo!=null){//有欠款还清
					PsnQkBVO qkbvo = new PsnQkBVO();
					qkbvo.setPk_psn_qk(qkvo.getPrimaryKey());
					qkbvo.setNmny(bvo.getNbqqkmny().sub(bvo.getNsqqkmny()));
					qkbvo.setCsourcehid(bvo.getPk_psn_item_h());
					qkbvo.setCsourcebid(bvo.getPk_psn_item_b());
					qkbvo.setCsourcetype(hvo.getPk_billtype());
					qkbvo.setPk_corp(hvo.getPk_corp());
					qkbvo.setPk_psn(bvo.getPk_psndoc());
					qkbvo.setDr(0);
					qkvo.setNmny(new UFDouble(0));
					qkvo.setStatus(VOStatus.UPDATED);
					list_h.add(qkvo);
					list_b.add(qkbvo);
				}else{//本身无欠款，现在还是无欠款,不管

				}
			}else{
				bvo.setNbqqkmny(bvo.getNsfmny().add(bvo.getNsqqkmny()));
				bvo.setNsfmny(new UFDouble(0));
				if(qkvo!=null){//有欠款未还清
					PsnQkBVO qkbvo = new PsnQkBVO();
					qkbvo.setPk_psn_qk(qkvo.getPrimaryKey());
					qkbvo.setNmny(bvo.getNbqqkmny().sub(bvo.getNsqqkmny()));
					qkbvo.setCsourcehid(bvo.getPk_psn_item_h());
					qkbvo.setCsourcebid(bvo.getPk_psn_item_b());
					qkbvo.setCsourcetype(hvo.getPk_billtype());
					qkbvo.setPk_corp(hvo.getPk_corp());
					qkbvo.setPk_psn(bvo.getPk_psndoc());
					qkbvo.setDr(0);
					qkvo.setNmny(bvo.getNbqqkmny());
					qkvo.setStatus(VOStatus.UPDATED);
					list_h.add(qkvo);
					list_b.add(qkbvo);
				}else{//本身无欠款，现在欠款
					qkvo = new PsnQkVO();
					qkvo.setNmny(bvo.getNbqqkmny());
					qkvo.setStatus(VOStatus.NEW);
					qkvo.setDr(0);
					qkvo.setPk_corp(hvo.getPk_corp());
					qkvo.setPk_psn(bvo.getPk_psndoc());
					String id = getDao().insertVO(qkvo);
					PsnQkBVO qkbvo = new PsnQkBVO();
					qkbvo.setPk_psn_qk(id);
					qkbvo.setNmny(bvo.getNbqqkmny().sub(bvo.getNsqqkmny()));
					qkbvo.setCsourcehid(bvo.getPk_psn_item_h());
					qkbvo.setCsourcebid(bvo.getPk_psn_item_b());
					qkbvo.setCsourcetype(hvo.getPk_billtype());
					qkbvo.setPk_corp(hvo.getPk_corp());
					qkbvo.setPk_psn(bvo.getPk_psndoc());
					qkbvo.setDr(0);
					list_b.add(qkbvo);
				}
			}
		}
		getDao().updateVOArray(bvos);
		if(list_h!=null&&list_h.size()>0){
			getDao().updateVOArray(list_h.toArray(new PsnQkVO[0]));
		}
		if(list_b!=null&&list_b.size()>0){
			getDao().insertVOArray(list_b.toArray(new PsnQkBVO[0]));
		}
		 **/
//		}else{
//		coll = getDao().retrieveByClause(PsnClassItemBVO.class, sql );
//		bvos = coll.toArray(new PsnClassItemBVO[0]);//算完抵扣税后再算扣款

//		}

		coll = getDao().retrieveByClause(PsnClassItemBVO.class, sql );
		bvos = coll.toArray(new PsnClassItemBVO[0]);//算完抵扣税后再算扣款



		try {
			//genWaData(hvo,bvos);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new BusinessException(e.getMessage());
		}
		savePsnItemBB(hvo);
		genDeptmny_d( hvo,bvos);
	}
	/**
	 * 传递薪资信息
	 * @param hvo
	 * @param bvos
	 */
	private void genWaData(PsnClassItemHVO hvo,PsnClassItemBVO[] bvos) throws Exception{
		DataDMO datadmo = new DataDMO();
		if(bvos==null||bvos.length<=0) return;
		ArrayList<String> list_psn = new ArrayList<String>();
		HashMap<String,PsnClassItemBVO> map_psn = new HashMap<String, PsnClassItemBVO>();
		for(PsnClassItemBVO bvo:bvos){
			list_psn.add(bvo.getPk_psndoc());
			map_psn.put(bvo.getPk_psndoc(), bvo);
		}
		IItem item = NCLocator.getInstance().lookup(IItem.class);
		ItemVO[] itemvos = item.queryAllItem(hvo.getPk_corp());
		HashMap<String,ItemVO> map_item = new HashMap<String, ItemVO>();
		for(ItemVO vo:itemvos){
			map_item.put(vo.getPrimaryKey(),vo);
		}
		String[] codes = new String[6+hvo.getNindex()];
		String[] types = new String[6+hvo.getNindex()];
		for(int i=0;i<types.length;i++){
			types[i] = "1";
		}
		HashMap<String,String> map_key = new HashMap<String, String>();
		for(int i=0;i<hvo.getNindex();i++){
			codes[i] = "f_"+map_item.get((String)hvo.getAttributeValue("pk_item"+(i+1)+"")).getIitemid();
			map_key.put(codes[i],"nmny"+(i+1)+"");
		}
		codes[hvo.getNindex()] = "f_1";//应发
		map_key.put(codes[hvo.getNindex()],"nmny");
		codes[hvo.getNindex()+1] = "f_3";//实发
		map_key.put(codes[hvo.getNindex()+1],"nsfmny");
		codes[hvo.getNindex()+2] = "f_4";//税额
		map_key.put(codes[hvo.getNindex()+2],"nsmny");
		codes[hvo.getNindex()+3] = "f_5";//纳税基数
		map_key.put(codes[hvo.getNindex()+3],"nbcnsmny");
		codes[hvo.getNindex()+4] = "f_481";//减免税额
		map_key.put(codes[hvo.getNindex()+4],"ndkmny");
		codes[hvo.getNindex()+5] = "f_482";//还款额
		map_key.put(codes[hvo.getNindex()+5],"nbqqkmny-nsqqkmny");

		String wheresql = HRPPubTool.formInSQL("bd_psndoc.pk_psndoc", list_psn);
		wheresql = wheresql.replace("and", " ");
		DataVO[] datavos = datadmo.queryAll(hvo.getPk_wa_class(), hvo.getVyear(), hvo.getVperiod(), codes,types,wheresql);

		if(datavos==null&&datavos.length<=0) return;

		ArrayList<String> list_data = new ArrayList<String>();
		for(int i=0;i<datavos.length;i++){
			String updatesql = "update wa_data set dr=0,icheckflag=1,irecaculateflag=1 ";
			if(map_psn.containsKey(datavos[i].getPsnid())){
				PsnClassItemBVO vo = map_psn.get(datavos[i].getPsnid());
				for(String code:codes){
					UFDouble value = datavos[i].getAttributeValue(code)!=null?new UFDouble( datavos[i].getAttributeValue(code).toString()):new UFDouble(0);
					if(!code.equals("f_482")){
						value = value.add(vo.getAttributeValue(map_key.get(code))!=null?new UFDouble(vo.getAttributeValue(map_key.get(code)).toString()):new UFDouble(0));
					}else{
						UFDouble nhkmny = (vo.getNbqqkmny()!=null?vo.getNbqqkmny():new UFDouble(0)).sub(vo.getNsqqkmny()!=null?vo.getNsqqkmny():new UFDouble(0));
						value = value.add(nhkmny);
					}
					updatesql += " ,"+code+"="+value+" ";
				}
			}
			updatesql+=" where pk_wa_data='"+datavos[i].getPrimaryKey()+"' ";
			list_data.add(datavos[i].getPrimaryKey());
			getDao().executeUpdate(updatesql);
		}
		wheresql = HRPPubTool.formInSQL("wa_data.pk_wa_data", list_data);
		wheresql = wheresql.replace("and", " (");
		copyToDataZandDataF(hvo, codes, wheresql+")");
//		PeriodVO pvo = new PeriodVO();
//		pvo.setClassid(hvo.getPk_wa_class());
//		pvo.setCyear(hvo.getVyear());
//		pvo.setCperiod(hvo.getVperiod());
//		pvo.setPk_corp(hvo.getPk_corp());
		PeriodDMO pdmo = new PeriodDMO();
		PeriodVO pvo = pdmo.queryAllByClassIdWaPeriod(hvo.getPk_wa_class(), hvo.getVyear(), hvo.getVperiod());
		pvo.setCpaydate(hvo.getDapprovedate()!=null?hvo.getDapprovedate().toString():hvo.getVyear()+"-"+hvo.getVperiod()+"-28");
		pvo.setIpayoffflag(1);

		pdmo.updatePayOffFlagAtPeriod(pvo);
	}



	private void copyToDataZandDataF(PsnClassItemHVO hvo,String[] codes,String wheresql) throws Exception {
		try {
			WaclassVO waClassVO = (WaclassVO)getDao().retrieveByPK(WaclassVO.class, hvo.getPk_wa_class());
			ReCacuVO aRecaVO = new ReCacuVO();
			aRecaVO.setWaClassVO(waClassVO);
			aRecaVO.setWaPeriod(hvo.getVperiod());
			aRecaVO.setWaYear(hvo.getVyear());
			aRecaVO.setReCacuCondition(wheresql);

			// String gzlbId = waClassVO.getPrimaryKey();

			// String waYear = aRecaVO.getWaYear();
			// String waPeriod = aRecaVO.getWaPeriod();

			DataDMO dmo = new DataDMO();

			// 复制
			String bzPk = waClassVO.getCurrid();
			String rateY = "1";
			String rateF = "1";
			String opratorY = "*";
			String opratorF = "*";

			boolean isSingleMain = true;
			boolean isZhuBi = true;
			boolean isFuBi = false;


			String[] digitItemRefWithCurr = codes; // 与币种有关
			String[] digitItemRefWithoutCurr = null;
			// 与币种无关

			// 去掉已扣税
			digitItemRefWithCurr = (String[]) Convertor

			.removeObjectFromArray(digitItemRefWithCurr, null, "f_8");
			// 去掉已扣税基数
			digitItemRefWithCurr = (String[]) Convertor.removeObjectFromArray(digitItemRefWithCurr, null, "f_9");
			// 去掉本次扣税
			digitItemRefWithCurr = (String[]) Convertor.removeObjectFromArray(digitItemRefWithCurr, null, "f_4");
			// 去掉本次扣税基数
			digitItemRefWithCurr = (String[]) Convertor.removeObjectFromArray(digitItemRefWithCurr, null, "f_5");

			dmo.updateFromAnotherTable(// 与币种无关
					"wa_data", "wa_dataz", digitItemRefWithoutCurr, "1", aRecaVO, "*");

			if (isSingleMain || isZhuBi) {
				// 单主币 或 主币
				// 更新主币
				if (isZhuBi) {
					rateY = "1";
					opratorY = "*";
				}
				dmo.updateFromAnotherTable("wa_data", "wa_dataz", digitItemRefWithCurr, rateY, aRecaVO, opratorY);
				dmo.updateFromAnotherTableWithout("wa_data", "wa_dataz", digitItemRefWithoutCurr, "1", aRecaVO, "*");
			}

		} catch (Exception e) {
			reportException(e);
			throw e;
		}
	}

	private void deleteOld(String pk_corp,String hid,String pk_billtype) throws BusinessException{
		try {
			FenPeiDMO dmo = new FenPeiDMO();
			dmo.deleteOld(pk_corp, hid, pk_billtype);
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new BusinessException(e.getMessage());
		}

	}
	public void genDeptmny_d(PsnClassItemHVO hvo,PsnClassItemBVO[] bvos) throws BusinessException{
		if(bvos==null||bvos.length<=0) return;
		hvo = (PsnClassItemHVO)getDao().retrieveByPK(PsnClassItemHVO.class, hvo.getPrimaryKey());
		if(hvo==null||hvo.getPk_wa_class()==null||(!hvo.getPk_wa_class().trim().equals(IHRPWABtn.PK_JIANG)&&!hvo.getPk_wa_class().trim().equals(IHRPWABtn.PK_LAOWU))) return;
		if(hvo.getBisjiangjin()!=null&&hvo.getBisjiangjin().booleanValue()) return;
		if(hvo.getBisnianzhong()!=null&&hvo.getBisnianzhong().booleanValue()) return;
		SuperDMO dmo = new SuperDMO();
		String type = "1";
		if(hvo.getPk_billtype().equals("73RP")){
			deleteOld(hvo.getPk_corp(), hvo.getPrimaryKey(),"2");
			type="2";
		}else{
			deleteOld(hvo.getPk_corp(), hvo.getPrimaryKey(),"1");
		}

		if(hvo.getVbillstatus_audit()!=null&&hvo.getVbillstatus_audit()==WaHrpBillStatus.PASS){
			DeptMnyHVO[] dhvos_out = (DeptMnyHVO[])dmo.queryByWhereClause(DeptMnyHVO.class, " isnull(pk_billtype,'1')='"+type+"' and isnull(dr,0)=0 and pk_corp='"+hvo.getPk_corp()+"' and pk_dept ='"+hvo.getPk_dept()+"' ");
			if(dhvos_out!=null&&dhvos_out.length>0){//余额问题在前台校验
				DeptMnyHVO dhvo = dhvos_out[0];
				dhvo.setDr(0);
				dhvo.setPk_corp(hvo.getPk_corp());
				dhvo.setPk_dept(hvo.getPk_dept());
				dhvo.setPk_billtype(type);
				dhvo.setNmny(dhvo.getNmny().sub(hvo.getTotalmoney()));
				dmo.update(dhvo);
				DeptMnyBVO dbvo = new DeptMnyBVO();
				dbvo.setDr(0);
				dbvo.setStatus(VOStatus.NEW);
				dbvo.setNmny(hvo.getTotalmoney());
				dbvo.setPk_corp(hvo.getPk_corp());
				dbvo.setPk_dept(hvo.getPk_dept());
				dbvo.setCsourcebillbid(hvo.getPrimaryKey());
				dbvo.setCsourcebillhid(hvo.getPrimaryKey());
				dbvo.setCsourcebillcode(hvo.getVbillno());
				dbvo.setCsourcebilltypecode(hvo.getPk_billtype());
				dbvo.setIflag(0);
				dbvo.setPk_deptmny_h(dhvo.getPrimaryKey());
				dbvo.setVyear(hvo.getDapprovedate().getYear()+"");
				dbvo.setVperiod(hvo.getDapprovedate().toString().substring(5,7));
				dbvo.setDdate(hvo.getDapprovedate());
				dbvo.setVmemo(hvo.getVmemo());
				if(hvo.getPk_billtype().equals("73RP")){
					dbvo.setBilltypename("劳务费科室发放");
				}else{
					dbvo.setBilltypename("奖金科室发放");
				}
				dmo.insert(dbvo);
			}else{
				DeptMnyHVO dhvo = new DeptMnyHVO();
				dhvo.setDr(0);
				dhvo.setPk_corp(hvo.getPk_corp());
				dhvo.setPk_dept(hvo.getPk_dept());
				dhvo.setPk_billtype(type);
				dhvo.setNmny(new UFDouble(0).sub(hvo.getTotalmoney()));
				String id = dmo.insert(dhvo);
				DeptMnyBVO dbvo = new DeptMnyBVO();
				dbvo.setDr(0);
				dbvo.setStatus(VOStatus.NEW);
				dbvo.setNmny(hvo.getTotalmoney());
				dbvo.setPk_corp(hvo.getPk_corp());
				dbvo.setPk_dept(hvo.getPk_dept());
				dbvo.setCsourcebillbid(hvo.getPrimaryKey());
				dbvo.setCsourcebillhid(hvo.getPrimaryKey());
				dbvo.setCsourcebillcode(hvo.getVbillno());
				dbvo.setCsourcebilltypecode(hvo.getPk_billtype());
				dbvo.setIflag(0);
				if(hvo.getPk_billtype().equals("73RP")){
					dbvo.setBilltypename("劳务费科室发放");
				}else{
					dbvo.setBilltypename("奖金科室发放");
				}
				dbvo.setPk_deptmny_h(id);
				dbvo.setVyear(hvo.getDapprovedate().getYear()+"");
				dbvo.setVperiod(hvo.getDapprovedate().toString().substring(5,7));
				dbvo.setDdate(hvo.getDapprovedate());
				dbvo.setVmemo(hvo.getVmemo());
				dmo.insert(dbvo);
			}
		}
	}
	private ArrayList<String> getCjrID(String pk_corp) throws BusinessException{
		ArrayList<String> list = new ArrayList<String>();
		StringBuffer buffer = new StringBuffer();
		buffer.append(" select pk_psndoc from bd_psndoc h inner join bd_psnbasdoc b on h.pk_psnbasdoc=b.pk_psnbasdoc ");
		buffer.append(" where isnull(h.dr,0)=0 and h.pk_corp='"+pk_corp+"' and b.basgroupdef38='Y' ");
		BaseDAO dao = new BaseDAO();
		ArrayList<PsnClassItemBVO> list_p = (ArrayList<PsnClassItemBVO>)dao.executeQuery(buffer.toString(), new BeanListProcessor(PsnClassItemBVO.class));
		if(list_p!=null&&list_p.size()>0){
			for(int i=0;i<list_p.size();i++){
				list.add(list_p.get(i).getPk_psndoc());
			}
		}
		return list;
	}

	public void onWriteSj(PsnClassItemHVO phvo,PsnClassItemBVO[] bvos) throws BusinessException{
//		IUAPQueryBS bs = NCLocator.getInstance().lookup(IUAPQueryBS.class);
		StringBuffer buffer = new StringBuffer();
		ArrayList<String> list_psn = getCjrID(phvo.getPk_corp());
		if(bvos==null) return;
		SuperDMO dmo = new SuperDMO();
		BaseDAO dao = new BaseDAO();
		String type = "1";
		if(phvo.getPk_billtype()!=null&&phvo.getPk_billtype().equals("73RP")){
			type = "2";
		}
		PsnSjVO[] hvos = (PsnSjVO[])dmo.queryByWhereClause(PsnSjVO.class, " isnull(pk_billtype,'1')='"+type+"' and isnull(dr,0)=0 and  pk_corp='"+phvo.getPk_corp()+"' and vyear='"+phvo.getVyear()+"' and vperiod='"+phvo.getVperiod()+"' ");
		HashMap<String,PsnSjVO> map = new HashMap<String, PsnSjVO>();
		if(hvos!=null&&hvos.length>0){
			for(PsnSjVO vo:hvos){
				map.put(vo.getPk_psn(),vo);
			}
		}
		ArrayList<PsnSjBVO> bnew_list = new ArrayList<PsnSjBVO>();
		ArrayList<PsnSjVO> hnew_list = new ArrayList<PsnSjVO>();
		ArrayList<PsnSjVO> hold_list = new ArrayList<PsnSjVO>();

		for(PsnClassItemBVO vo:bvos){
			PsnSjVO hvo = new PsnSjVO();
			if(list_psn.contains(vo.getPk_psndoc())){
				UFDouble smny = (vo.getNsmny()!=null?vo.getNsmny():new UFDouble(0)).add(vo.getNoldsmny()!=null?vo.getNoldsmny():new UFDouble(0))
				.add(vo.getNoldsmny_nz()!=null?vo.getNoldsmny_nz():new UFDouble(0));
//				if(vo.getNsmny()!=null&&vo.getNsmny().doubleValue()>=0){
//					if((vo.getNoldsmny()!=null?vo.getNoldsmny():new UFDouble(0).add(vo.getNoldsmny_nz()!=null?vo.getNoldsmny_nz():new UFDouble(0))).compareTo(IHRPWABtn.N_CJR_TYPE)<0&&smny.compareTo(IHRPWABtn.N_CJR_TYPE)>=0){
//						vo.setNdkmny((IHRPWABtn.N_CJR_TYPE).sub(vo.getNoldsmny()!=null?vo.getNoldsmny():new UFDouble(0).add(vo.getNoldsmny_nz()!=null?vo.getNoldsmny_nz():new UFDouble(0))));
//					}else if(smny.compareTo(IHRPWABtn.N_CJR_TYPE)<=0){
//						vo.setNdkmny(vo.getNsmny()!=null?vo.getNsmny():new UFDouble(0));
//					}else{
//						vo.setNdkmny(new UFDouble(0));
//					}
//				}else{
//					if((vo.getNoldsmny()!=null?vo.getNoldsmny():new UFDouble(0).add(vo.getNoldsmny_nz()!=null?vo.getNoldsmny_nz():new UFDouble(0))).compareTo(IHRPWABtn.N_CJR_TYPE)>=0&&smny.compareTo(IHRPWABtn.N_CJR_TYPE)<=0){
//						vo.setNdkmny((smny).sub(IHRPWABtn.N_CJR_TYPE));
//					}else if(smny.compareTo(IHRPWABtn.N_CJR_TYPE)<=0){
//						vo.setNdkmny(vo.getNsmny()!=null?vo.getNsmny():new UFDouble(0));
//					}else{
//						vo.setNdkmny(new UFDouble(0));
//					}
//				}
				vo.setNdkmny(new UFDouble(0));
				vo.setNsfmny((vo.getNaftersmny()!=null?vo.getNaftersmny():new UFDouble(0)).add(vo.getNdkmny()));
			}else{
				vo.setNsfmny(vo.getNaftersmny());
			}
			vo.setStatus(VOStatus.UPDATED);
			hvo.setPk_billtype(type);
			PsnSjBVO bvo = new PsnSjBVO();
			bvo.setDr(0);
			bvo.setPk_corp(phvo.getPk_corp());
			bvo.setCsourcetype(phvo.getPk_billtype());
			bvo.setStatus(VOStatus.NEW);
			bvo.setCsourcebid(vo.getPk_psn_item_b());
			bvo.setCsourcehid(vo.getPk_psn_item_h());
			if(phvo.getBisnianzhong()!=null&&phvo.getBisnianzhong().booleanValue()){
				bvo.setNsmny_nz(vo.getNsmny()!=null?vo.getNsmny():new UFDouble(0));
				bvo.setNyksjs_nz(vo.getNbcnsmny()!=null?vo.getNbcnsmny():new UFDouble(0));
			}else{
				bvo.setNsmny_nz(vo.getNsmny_nz()!=null?vo.getNsmny_nz():new UFDouble(0));
				bvo.setNsmny((vo.getNsmny()!=null?vo.getNsmny():new UFDouble(0)).sub(vo.getNsmny_nz()!=null?vo.getNsmny_nz():new UFDouble(0)));
				bvo.setNyksjs(vo.getNbcnsmny()!=null?vo.getNbcnsmny():new UFDouble(0));
			}
			bvo.setNdkmny(vo.getNdkmny()!=null?vo.getNdkmny():new UFDouble(0));
			bvo.setVyear(phvo.getVyear());
			bvo.setVperiod(phvo.getVperiod());
			bvo.setPk_psn(vo.getPk_psndoc());
			bvo.setTaxbaseid(vo.getCtaxtableid()!=null?vo.getCtaxtableid():"0001691000000001D7IW");
			if(map.containsKey(bvo.getPk_psn())){
				hvo = map.get(bvo.getPk_psn());
				hvo.setPk_billtype(type);
				bvo.setPk_psn_sj(hvo.getPrimaryKey());
				if(phvo.getBisnianzhong()!=null&&phvo.getBisnianzhong().booleanValue()){
					hvo.setNsmny_nz((hvo.getNsmny_nz()!=null?hvo.getNsmny_nz():new UFDouble(0)).add(bvo.getNsmny_nz()));
					hvo.setNyksjs_nz((hvo.getNyksjs_nz()!=null?hvo.getNyksjs_nz():new UFDouble(0)).add(bvo.getNyksjs_nz()));
				}else{
					hvo.setNsmny_nz((hvo.getNsmny_nz()!=null?hvo.getNsmny_nz():new UFDouble(0)).add(bvo.getNsmny_nz()));
					hvo.setNsmny((hvo.getNsmny()!=null?hvo.getNsmny():new UFDouble(0)).add(bvo.getNsmny()));
					hvo.setNyksjs((hvo.getNyksjs()!=null?hvo.getNyksjs():new UFDouble(0)).add(bvo.getNyksjs()));
				}
				hvo.setNdkmny((hvo.getNdkmny()!=null?hvo.getNdkmny():new UFDouble(0)).add(bvo.getNdkmny()));
				hold_list.add(hvo);
			}else{
				String hid = getOID();
				bvo.setPk_psn_sj(hid);
				String[] keys = hvo.getAttributeNames();
				for(String key:keys){
					hvo.setAttributeValue(key, bvo.getAttributeValue(key));
				}
				hvo.setPk_billtype(type);
				hnew_list.add(hvo);
			}


			bnew_list.add(bvo);
		}

		dao.updateVOArray(bvos);
		if(hold_list!=null&&hold_list.size()>0){
			dao.updateVOArray(hold_list.toArray(new PsnSjVO[0]));
		}

		if(hnew_list!=null&&hnew_list.size()>0){
			dao.insertVOArrayWithPK(hnew_list.toArray(new PsnSjVO[0]));
		}
		if(bnew_list!=null&&bnew_list.size()>0){
			dao.insertVOArray(bnew_list.toArray(new PsnSjBVO[0]));
		}
	}

	/**
	 * 1,按照税率表进行计税运算 <BR>
	 * 2,同步扣税表wa_tax <BR>
	 * Created on 2007-7-20
	 * 
	 * 
	 * 批量执行提高效率
	 * @author zhangg
	 * @param taxBaseVO
	 * @param aRecaVO
	 * @param isDefault
	 * @throws Exception
	 */
	public void caculateTax_nz(TaxbaseVO taxBaseVO, boolean isDefault,WaclassVO waClass,String waYear,String waPeriod,PsnClassItemHVO hvo,String wheresql) throws Exception {

		int taxFlag = waClass.getItaxsetting().intValue();// 1，3代扣；2，4代缴 都是代扣 奖金工资 为1 
		int nsysflag = waClass.getSysflag() == null ? 0 : waClass.getSysflag().intValue();//这个系统标准也为0 

		// 减费用额
		String subFee = taxBaseVO.getNdebuctamount().toString();

		String inCome_tax = null;
		if (taxFlag == 1 || taxFlag == 3) {//工资奖金肯定是代扣，不用考虑其他情况目前

			inCome_tax = " (isnull(wa_psn_item_b.nbcnsmny,0)+isnull(wa_psn_item_b.noldnsmny_nz,0) -greatest((" + subFee + "-isnull(wa_psn_item_b.noldnsmny,0)),0) )/12 ";
		} 
		String valueStr =  "(isnull(wa_psn_item_b.nbcnsmny,0)+isnull(wa_psn_item_b.noldnsmny_nz,0) -greatest((" + subFee + "-isnull(wa_psn_item_b.noldnsmny,0)),0) )/12";

		StringBuffer sqlB = new StringBuffer();
		sqlB.append("update wa_psn_item_b "); // 1
		sqlB.append("   set wa_psn_item_b.nsmny_nz = round(coalesce((select (" + inCome_tax + " * ntaxrate * 0.01*12 - "); // 2
		sqlB.append("                                   nquickdebuct ) "); // 3
		sqlB.append("                              from wa_taxtable ");
		sqlB.append("                             where pk_wa_taxbase = '" + taxBaseVO.getPrimaryKey() + "' ");
		sqlB.append("                               and  " + valueStr + " > nminamount ");
		sqlB.append("                               and (" + valueStr + " <= nmaxamount or ");
		//liangxr 解决税率表只有一行的情况不能正确计算问题。
		sqlB.append("                                   (nmaxamount <= nminamount))),0), ");// 最大值如果小于最小值成立就可以
		sqlB.append("                            " + 2 + ") - isnull(wa_psn_item_b.noldsmny_nz,0) ");

		sqlB.append("   where "+wheresql+" ");
		sqlB.append("    and ctaxtableid='"+taxBaseVO.getPrimaryKey()+"' ");

		StringBuffer sqlC = new StringBuffer();
		sqlC.append("update wa_psn_item_b set nsmny=nsmny_nz,naftersmny=isnull(nmny,0)-isnull(nsmny_nz,0) "); // 1
		sqlC.append("   where "+wheresql+" ");
		sqlC.append("   and ctaxtableid='"+taxBaseVO.getPrimaryKey()+"' ");

		PubDelegator.getIPersistenceUpdate().executeSQLs(new String[] {sqlB.toString(), sqlC.toString() });

	}

	/**
	 *
	 *
	 * 计算应缴税款，同时将数据保存到wa_tax中 创建日期：(2001-6-5)
	 *
	 * @return nc.vo.wa.wa_016.PsnVO[]
	 * @param gzlbId
	 *                int
	 * @exception java.sql.SQLException
	 *                    异常说明。
	 */
	protected void caculateTaxByTaxBase(PsnClassItemBVO[] bvos,PsnClassItemHVO hvo,WaclassVO classvo, TaxbaseVO aTaxbaseVO, boolean isDefault,String whersql) throws BusinessException {

		try {

			switch (aTaxbaseVO.getItbltype().intValue()) // 固定税率 奖金 津贴目前不会是固定税率 所以不处理
			{
			case 0:

				break;
			case 1: // 变动税率
				nc.impl.wa.wa_004.TaxtableDMO taxTableDMO = new nc.impl.wa.wa_004.TaxtableDMO();
				nc.vo.wa.wa_004.TaxtableVO[] taxTableVO = taxTableDMO.queryByPKTaxbase(aTaxbaseVO.getPrimaryKey());

				// Modified by: zhangg on 2007-7-23 <p>Reason: 修改了扣税的运算规则。 提高效率
				if (taxTableVO != null && taxTableVO.length > 0) {
					// 2,按照税率表进行计税运算
					if(hvo.getBisnianzhong()!=null&&hvo.getBisnianzhong().booleanValue()){
						caculateTax_nz(aTaxbaseVO, isDefault,classvo,hvo.getVyear(),hvo.getVperiod(), hvo,whersql);
					}else{
						caculateTax(aTaxbaseVO, isDefault,classvo,hvo.getVyear(),hvo.getVperiod(), hvo,whersql);
					}
					// 3,同步扣税表wa_tax
					updateWa_tax(aTaxbaseVO, isDefault,classvo,hvo.getVyear(),hvo.getVperiod(), hvo,whersql);
				}
				break;
			case 2: // 个人劳务报酬所得税率

			}
		} catch (Exception e) {
			if (e.getMessage() == null || e.getMessage().trim().length() < 1) {
				e = new Exception(NCLangResOnserver.getInstance().getStrByID("60131004", "UPP60131004-000005")/*
				 * @res
				 * "计税时出错，请重新检查税率表！"
				 */);
			}
			throw new BusinessException(e);
		}
	}


	/**
	 * 1,按照税率表进行计税运算 <BR>
	 * 2,同步扣税表wa_tax <BR>
	 * Created on 2007-7-20
	 * 
	 * 
	 * 批量执行提高效率
	 * @author zhangg
	 * @param taxBaseVO
	 * @param aRecaVO
	 * @param isDefault
	 * @throws Exception
	 */
	public void caculateTax(TaxbaseVO taxBaseVO, boolean isDefault,WaclassVO waClass,String waYear,String waPeriod,PsnClassItemHVO hvo,String wheresql) throws Exception {

		int taxFlag = waClass.getItaxsetting().intValue();// 1，3代扣；2，4代缴 都是代扣 奖金工资 为1 
		int nsysflag = waClass.getSysflag() == null ? 0 : waClass.getSysflag().intValue();//这个系统标准也为0 

		// 减费用额
		String subFee = taxBaseVO.getNdebuctamount().toString();

		String inCome_tax = null;
		
		String inCome_tax_nz = null;
		 
	
		
		if (taxFlag == 1 || taxFlag == 3) {//工资奖金肯定是代扣，不用考虑其他情况目前

			inCome_tax = " (isnull(wa_psn_item_b.nbcnsmny,0)+isnull(wa_psn_item_b.noldnsmny,0) -" + subFee + ") ";
			inCome_tax_nz = " (isnull(wa_psn_item_b.noldnsmny_nz,0) -greatest((" + subFee + "-isnull(wa_psn_item_b.noldnsmny,0)-isnull(wa_psn_item_b.nbcnsmny,0)),0) )/12 ";
			
		} 
		String valueStr =  "(isnull(wa_psn_item_b.nbcnsmny,0)+isnull(wa_psn_item_b.noldnsmny,0) - " + subFee + ")";
		String valueStr_nz =  "(isnull(wa_psn_item_b.noldnsmny_nz,0) -greatest((" + subFee + "-isnull(wa_psn_item_b.noldnsmny,0)-isnull(wa_psn_item_b.nbcnsmny,0)),0) )/12";


		
		StringBuffer sqlA = new StringBuffer();
		sqlA.append("update wa_psn_item_b "); // 1
		sqlA.append("   set wa_psn_item_b.nsmny_nz = round(coalesce((select (" + inCome_tax_nz + " * ntaxrate * 0.01*12 - "); // 2
		sqlA.append("                                   nquickdebuct ) "); // 3
		sqlA.append("                              from wa_taxtable ");
		sqlA.append("                             where pk_wa_taxbase = '" + taxBaseVO.getPrimaryKey() + "' ");
		sqlA.append("                               and  " + valueStr_nz + " > nminamount ");
		sqlA.append("                               and (" + valueStr_nz + " <= nmaxamount or ");
		//liangxr 解决税率表只有一行的情况不能正确计算问题。
		sqlA.append("                                   (nmaxamount <= nminamount))),0), ");// 最大值如果小于最小值成立就可以
		sqlA.append("                            " + 2 + ") - isnull(wa_psn_item_b.noldsmny_nz,0) ");

		sqlA.append("   where "+wheresql+" ");
		sqlA.append("    and ctaxtableid='"+taxBaseVO.getPrimaryKey()+"' ");
		
		StringBuffer sqlB = new StringBuffer();
		sqlB.append("update wa_psn_item_b "); // 1
		sqlB.append("   set wa_psn_item_b.nsmny = round(coalesce((select (" + inCome_tax + " * ntaxrate * 0.01 - "); // 2
		sqlB.append("                                   nquickdebuct ) "); // 3
		sqlB.append("                              from wa_taxtable ");
		sqlB.append("                             where pk_wa_taxbase = '" + taxBaseVO.getPrimaryKey() + "' ");
		sqlB.append("                               and  " + valueStr + " > nminamount ");
		sqlB.append("                               and (" + valueStr + " <= nmaxamount or ");
		//liangxr 解决税率表只有一行的情况不能正确计算问题。
		sqlB.append("                                   (nmaxamount <= nminamount))),0), ");// 最大值如果小于最小值成立就可以
		sqlB.append("                            " + 2 + ") - isnull(wa_psn_item_b.noldsmny,0)+isnull(wa_psn_item_b.nsmny_nz,0) ");

		sqlB.append("   where "+wheresql+" ");
		sqlB.append("    and ctaxtableid='"+taxBaseVO.getPrimaryKey()+"' ");

		StringBuffer sqlC = new StringBuffer();
		sqlC.append("update wa_psn_item_b set naftersmny=isnull(nmny,0)-isnull(nsmny,0) "); // 1
		sqlC.append("   where "+wheresql+" ");
		sqlC.append("   and ctaxtableid='"+taxBaseVO.getPrimaryKey()+"' ");

		PubDelegator.getIPersistenceUpdate().executeSQLs(new String[] {sqlA.toString(),sqlB.toString(), sqlC.toString() });

	}

	/**
	 * 同步扣税表wa_tax Created on 2007-7-20
	 * 
	 * @author zhangg
	 * @throws BusinessException
	 */
	public void updateWa_tax(TaxbaseVO taxBaseVO,  boolean isDefault,WaclassVO waClass,String waYear,String waPeriod,PsnClassItemHVO hvo,String wheresql) throws BusinessException {
		String gzlbId = waClass.getPrimaryKey();
		// 减费用额
		String subFee = taxBaseVO.getNdebuctamount().toString();
		// ------------------------>

		int taxFlag = waClass.getItaxsetting().intValue();// 1，3代扣；2，4代缴 都是代扣 奖金工资 为1 
		int nsysflag = waClass.getSysflag() == null ? 0 : waClass.getSysflag().intValue();//这个系统标准也为0 

		String inCome_tax = null;
		if (taxFlag == 1 || taxFlag == 3) {//工资奖金肯定是代扣，不用考虑其他情况目前
			inCome_tax = " (fmnyz -" + subFee + ") ";
		} 
		String valueStr = nsysflag == 1 ? " (fmnyz/12) " : " (fmnyz - " + subFee + ") ";

		StringBuffer taxPsnCondition = new StringBuffer();
		taxPsnCondition.append("(select pk_psndoc ");
		taxPsnCondition.append("  from wa_psn_item_b ");
		taxPsnCondition.append("   where "+wheresql+" )");

		StringBuffer sqlB = new StringBuffer();// 同步主数据
		StringBuffer sqlB_zero = new StringBuffer(); // 将小于减免费用的， 和不扣税的制0
		StringBuffer sqlB_rate = new StringBuffer(); // 同步税率

		sqlB_zero.append("update wa_tax "); // 1
		sqlB_zero.append("   set ntaxrate = 0, nquickdebuct = 0, ftaxmny = 0 "); // 2
		sqlB_zero.append(" where wa_tax.pk_wa_class = '" + gzlbId + "' ");
		sqlB_zero.append("   and wa_tax.vcalyear = '" + waYear + "' ");
		sqlB_zero.append("   and wa_tax.vcalmonth = '" + waPeriod + "' ");
		sqlB_zero.append("   and (" + valueStr + ") <= 0 ");
		sqlB_zero.append("   and wa_tax.psnid in " + taxPsnCondition);


		sqlB.append("update wa_tax "); // 1
		sqlB.append("   set (fmnyz, ndebuctamount,  ftaxz, f_8) = "); // 2
		sqlB.append("(select isnull(wa_psn_item_b.nbcnsmny,0)+isnull(wa_psn_item_b.noldnsmny,0), " + subFee + ",  "); // 1
		sqlB.append("        isnull(nsmny,0)+isnull(noldsmny,0), isnull(noldsmny,0) "); // 3
		sqlB.append("        "); // 4
		sqlB.append("  from wa_psn_item_b ");
		sqlB.append(" where wa_tax.psnid = wa_psn_item_b.pk_psndoc ");
		sqlB.append("  and "+wheresql+" )");
		sqlB.append(" where wa_tax.pk_wa_class = '" + gzlbId + "' ");
		sqlB.append("   and wa_tax.vcalyear = '" + waYear + "' ");
		sqlB.append("   and wa_tax.vcalmonth = '" + waPeriod + "' ");
		sqlB.append("   and wa_tax.psnid in " + taxPsnCondition);

		sqlB_rate.append("update wa_tax "); // 1
		sqlB_rate.append("   set (ntaxrate, nquickdebuct, ftaxmny) = (select wa_taxtable.ntaxrate, "); // 2
		sqlB_rate.append("                                          wa_taxtable.nquickdebuct, " + inCome_tax); // 3
		sqlB_rate.append("                                     from wa_taxtable ");
		sqlB_rate.append("   									where wa_taxtable.pk_wa_taxbase = '" + taxBaseVO.getPrimaryKey() + "' ");
		sqlB_rate.append("                                      and ( " + valueStr + ") > ");
		sqlB_rate.append("                                          wa_taxtable.nminamount ");
		sqlB_rate.append("                                      and (( " + valueStr + ") <= ");
		sqlB_rate.append("                                          wa_taxtable.nmaxamount or ");
		sqlB_rate.append("                                          wa_taxtable.nmaxamount < ");
		sqlB_rate.append("                                          wa_taxtable.nminamount)) ");
		sqlB_rate.append(" where wa_tax.pk_wa_class = '" + gzlbId + "' ");
		sqlB_rate.append("   and wa_tax.vcalyear = '" + waYear + "' ");
		sqlB_rate.append("   and wa_tax.vcalmonth = '" + waPeriod + "' ");
		sqlB_rate.append("   and ( " + valueStr + ") > 0 ");
		sqlB_rate.append("   and wa_tax.psnid in " + taxPsnCondition);


		PubDelegator.getIPersistenceUpdate().executeSQLs(new String[] { sqlB.toString(), sqlB_zero.toString(), sqlB_rate.toString() });

	}



}
