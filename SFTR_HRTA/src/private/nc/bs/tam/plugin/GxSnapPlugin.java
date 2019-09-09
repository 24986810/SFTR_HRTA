package nc.bs.tam.plugin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;

import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.bs.framework.common.NCLocator;
import nc.bs.pub.SuperDMO;
import nc.bs.pub.taskcenter.BgWorkingContext;
import nc.bs.pub.taskcenter.IBackgroundWorkPlugin;
import nc.itf.hr.ta.IBclbDefining;
import nc.jdbc.framework.processor.BeanListProcessor;
import nc.vo.bd.b06.PsndocVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDate;
import nc.vo.tam.tongren001.DeptKqBVO;
import nc.vo.tam.tongren003.PanbanWeekBVO;
import nc.vo.tam.tongren011.AdjustTamVO;
import nc.vo.tam.tongren019.GxReportVO;
import nc.vo.tam.tongrenoa.OaHoildayVO;
import nc.vo.tbm.tbm_029.BclbVO;

/**
 * 公休快照保存
 * @author zhanghua
 *
 */
public class GxSnapPlugin implements IBackgroundWorkPlugin {

	public String executeTask(BgWorkingContext arg0) throws BusinessException {
		// TODO Auto-generated method stub
		UFDate logindate =  arg0.getLoginDate();
//		GxReportVO[] gxreportvos = getGxReportVO(""+logindate.getYear());
	    
//	    BaseDAO dao = new BaseDAO();
//	    dao.executeUpdate("delete tam_gx_snap where vyear='"+logindate.getYear()+"'");
//	    dao.insertVOArray(gxreportvos);
		UFDate testdate =  new UFDate(new Date());
	    getOAHoilday(testdate);
		return null;
	}

	
	public GxReportVO[] getGxReportVO(String vyear) throws DAOException{
		BaseDAO dao = new BaseDAO();
		String sql ="select h.vyear,"
			+"       psn.pk_psndoc,"
			+"       psn.psncode,"
			+"       psn.psnname,"
			+"       substr(bas.joinworkdate, 0, 4) joinworkdate,"
			+"       psn.groupdef15,"
			+"       bas.joinsysdate,"
			+"       b.ngl,"
			+"       b.nyfgx,"
			+"       b.nkcgx,"
			+"       '' gxyy,"
			+"       b.nsfgx,"
			+"       b.nyygx,"
			+"       b.nsxgx,"
			+"       qnbjts.qnbjts,"
			+"       qnsjts.qnsjts,"
			+"       qnzjdg.qnzjdg,"
			+"       decode(minbj.minddate,"
			+"              null,"
			+"              '',"
			+"              minbj.minddate || '至' || maxbj.maxddate) bz"
			+"  from tam_gx_h h"
			+"  left join tam_gx_b b"
			+"    on h.pk_gx_h = b.pk_gx_h"
			+"  left join bd_psndoc psn"
			+"    on b.pk_psndoc = psn.pk_psndoc"
			+"  left join bd_psnbasdoc bas"
			+"    on psn.pk_psnbasdoc = bas.pk_psnbasdoc"
			+"  left join v_trtam_qnbjts qnbjts"
			+"    on b.pk_psndoc = qnbjts.pk_psndoc"
			+"   and qnbjts.year = '"+vyear+"'"
			+"  left join v_trtam_qnsjts qnsjts"
			+"    on b.pk_psndoc = qnsjts.pk_psndoc"
			+"   and qnsjts.year = '"+vyear+"'"
			+"  left join v_trtam_qnzjdg qnzjdg"
			+"    on b.pk_psndoc = qnzjdg.pk_psndoc"
			+"   and qnzjdg.year = '"+vyear+"'"
			+"  left join v_trtam_minqnbjsj minbj"
			+"    on b.pk_psndoc = minbj.pk_psndoc"
			+"   and minbj.year = '"+vyear+"'"
			+"  left join v_trtam_maxqnbjsj maxbj"
			+"    on b.pk_psndoc = maxbj.pk_psndoc"
			+"   and maxbj.year = '"+vyear+"'"
			+" where h.dr = 0"
			+"   and b.dr = 0"
			+"   and bas.dr = 0"
			+"   and psn.dr = 0" ;
		
		ArrayList<GxReportVO> list = (ArrayList<GxReportVO>)dao.executeQuery(sql, new BeanListProcessor(GxReportVO.class));

		
		return list.toArray(new GxReportVO[0]);
		
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
//			map.put(psnvo.getPsncode().substring(1), psnvo);// 去掉1位限制
			map.put(psnvo.getPsncode(), psnvo);
		}
		for(BclbVO lbvo:bclbs){
			map_bc.put(lbvo.getLbbm(), lbvo);
			map_bc.put(lbvo.getPk_bclbid(), lbvo);
		}
		Collection coll = dao.retrieveByClause(OaHoildayVO.class, // and bjsj like '"+date.getDateBefore(1).toString()+"%' 
				" zt=100 and empnumber is not null and ksrq>='2018'  and  substr(bjsj,0,10) = '"+date.getDateBefore(1).toString()+"'    ");
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
