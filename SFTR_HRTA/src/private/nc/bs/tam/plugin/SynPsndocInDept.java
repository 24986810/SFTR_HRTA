package nc.bs.tam.plugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.bs.pub.taskcenter.BgWorkingContext;
import nc.bs.pub.taskcenter.IBackgroundWorkPlugin;
import nc.jdbc.framework.processor.BeanListProcessor;
import nc.ui.trade.business.HYPubBO_Client;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.tam.tongren001.DeptKqBVO;
import nc.vo.tam.tongren003.PanbanWeekBVO;
import nc.vo.tam.tongren008.ApplyBVO;

/**
 * 自动把调出人员调入
 * @author huazhang
 *
 */
public class SynPsndocInDept implements IBackgroundWorkPlugin {

	public String executeTask(BgWorkingContext arg0) throws BusinessException {
		// TODO Auto-generated method stub
		// 自动调入已审核的人员
		synPsndocInDept(arg0);
		
		return null;
	}

	public ApplyBVO[] getApplyBVOS(String dapplydate) throws DAOException{
		BaseDAO dao = new BaseDAO();
		String sql = "SELECT *"
			+"  FROM trtam_apply_psn_b"
			+" WHERE (nvl(dr, 0) = 0)"
			+"   and istate = 1"
			+"   and dnewstartdate is null"
			+"   and pk_apply in (SELECT pk_apply"
			+"                      FROM trtam_apply_psn"
			+"                     where dapplydate <= '"+dapplydate+"'  and dapplydate>='2019-04-01'"
			+"                       and (nvl(dr, 0) = 0) and bisaudit='Y') ";
		ArrayList<ApplyBVO> list = (ArrayList<ApplyBVO>)dao.executeQuery(sql, new BeanListProcessor(ApplyBVO.class));
		return list.toArray(new ApplyBVO[0]);
		
	}
	
	public void synPsndocInDept(BgWorkingContext arg0) throws DAOException{
		ArrayList<ApplyBVO> list = new ArrayList<ApplyBVO>();
		ArrayList<DeptKqBVO> listb = new ArrayList<DeptKqBVO>();
		ArrayList<PanbanWeekBVO> listbb = new ArrayList<PanbanWeekBVO>();
		
//		UFDate curdate = new UFDate(new Date()).getDateBefore(3);
		UFDate curdate = new UFDate(new Date());
		ApplyBVO[] appbvos = getApplyBVOS(curdate.toString());
		
		for(int i=0;i<appbvos.length;i++){
			ApplyBVO bvo = appbvos[i];
			
			if(bvo.getDnewstartdate()==null){
				bvo.setDnewstartdate(bvo.getDenddate().getDateAfter(1));
			}
			bvo.setDjsdate(new UFDate(new Date()));
			bvo.setDjspsnid(arg0.getPk_user());
			bvo.setIstate(2);
			list.add(bvo);
			DeptKqBVO kbvo = new DeptKqBVO();
			kbvo.setDr(0);
			kbvo.setPrimaryKey(null);
			kbvo.setBisnew(new UFBoolean(true));
			kbvo.setPk_psndoc(bvo.getPk_psndoc());
			kbvo.setPk_dept(bvo.getPk_dept_new());
			kbvo.setDstartdate(bvo.getDnewstartdate());
			PanbanWeekBVO[] panbaibvos = getPanbanWeekBVOS(bvo.getPk_psndoc(),bvo.getDnewstartdate().toString());
			if(panbaibvos!=null&&panbaibvos.length>0){
				for(PanbanWeekBVO panbaibvo:panbaibvos){
					panbaibvo.setPk_dept(bvo.getPk_dept_new());
				}
				listbb.addAll(Arrays.asList(panbaibvos));
			}
			listb.add(kbvo);
		}
		
		BaseDAO dao = new BaseDAO();
		if(list!=null&&list.size()>0){
			dao.updateVOArray(list.toArray(new ApplyBVO[0]));
			dao.insertVOArray(listb.toArray(new DeptKqBVO[0]));
			if(listbb!=null&&listbb.size()>0){
				dao.updateVOArray(listbb.toArray(new PanbanWeekBVO[0]));
			}
		}
	}
	
	public PanbanWeekBVO[] getPanbanWeekBVOS(String pk_psndoc,String dnewstartdate) throws DAOException{
		BaseDAO dao = new BaseDAO();
		String sql = "select * from trtam_paiban_b where isnull(dr,0)=0 and pk_psndoc='"+pk_psndoc+"' and ddate>='"+dnewstartdate+"' and pk_bb in (select pk_bclbid from tbm_bclb where (lbbm like '9903%' or lbbm like '9905%' or lbbm like '9906%') ) ";
		ArrayList<PanbanWeekBVO> list = (ArrayList<PanbanWeekBVO>)dao.executeQuery(sql, new BeanListProcessor(PanbanWeekBVO.class));
		return list.toArray(new PanbanWeekBVO[0]);
	}
}
