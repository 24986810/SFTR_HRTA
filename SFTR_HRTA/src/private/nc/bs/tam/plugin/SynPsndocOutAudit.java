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
 * 人员调出自动审核 状态：调出申请变到调出确认
 * @author zhuchaoli
 *
 */
public class SynPsndocOutAudit implements IBackgroundWorkPlugin {

	public String executeTask(BgWorkingContext arg0) throws BusinessException {
		// TODO Auto-generated method stub
		// 调出确认 
		exportConfirm(arg0);
		
		return null;
	}

	
	/**
	 * 调出确认
	 * @param dapplydate
	 * @return
	 * @throws DAOException
	 */
	public ApplyBVO[] getApplyBVOS(String dapplydate) throws DAOException{
		BaseDAO dao = new BaseDAO();
		String sql = "SELECT *"
			+"  FROM trtam_apply_psn_b"
			+" WHERE (nvl(dr, 0) = 0)"
			+"   and istate = 0"
			+"   and dnewstartdate is null"
			+"   and pk_apply in (SELECT pk_apply"
			+"                      FROM trtam_apply_psn"
			+"                     where dapplydate = '"+dapplydate+"'  "
			+"                       and (nvl(dr, 0) = 0) ) ";
		ArrayList<ApplyBVO> list = (ArrayList<ApplyBVO>)dao.executeQuery(sql, new BeanListProcessor(ApplyBVO.class));
		return list.toArray(new ApplyBVO[0]);
	}
	
	/**
	 * 调出确认主方法
	 * @param arg0
	 * @throws DAOException
	 */
	public void exportConfirm(BgWorkingContext arg0) throws DAOException{
		ArrayList<ApplyBVO> list = new ArrayList<ApplyBVO>();
		
		UFDate curdate = new UFDate(new Date());
		
		//调出确认
		ApplyBVO[] applyBVOS = getApplyBVOS(curdate.toString());
		for(int i=0;i<applyBVOS.length;i++){
			ApplyBVO bvo = applyBVOS[i];
			
			bvo.setDjsdate(new UFDate(new Date()));
			bvo.setDjspsnid(arg0.getPk_user());
			bvo.setIstate(1);
			list.add(bvo);
		}
		
		BaseDAO dao = new BaseDAO();
		if(list!=null&&list.size()>0){
			dao.updateVOArray(list.toArray(new ApplyBVO[0]));
		}
	}
	
}
