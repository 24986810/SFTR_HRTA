package nc.bs.tam.plugin;

import java.util.ArrayList;

import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.bs.pub.taskcenter.BgWorkingContext;
import nc.bs.pub.taskcenter.IBackgroundWorkPlugin;
import nc.jdbc.framework.processor.BeanListProcessor;
import nc.vo.pub.BusinessException;
import nc.vo.tam.plugin.MdmPsndocVO;
import nc.vo.tam.tongren019.GxReportVO;

public class SysTelData implements IBackgroundWorkPlugin{

	public String executeTask(BgWorkingContext arg0) throws BusinessException {
		// TODO Auto-generated method stub
		BaseDAO dao = new BaseDAO();
		MdmPsndocVO[] mdmvos = getMdmPsndocVOS();
		for(int i=0;i<mdmvos.length;i++){
			String sql= "update bd_psnbasdoc set officephone='"+mdmvos[i].getOfficeMobile()+"' where pk_psnbasdoc=(select pk_psnbasdoc from bd_psndoc where psncode='"+mdmvos[i].getUserCode()+"' and dr=0) and dr=0";
			dao.executeUpdate(sql);
		}
		
		return null;
	}
	
	public MdmPsndocVO[] getMdmPsndocVOS() throws DAOException{
		BaseDAO dao = new BaseDAO("SQLMDM");
		String sql = "select * from V_API_UserOfficePhone where OfficeMobile != ''";
		ArrayList<MdmPsndocVO> list = (ArrayList<MdmPsndocVO>)dao.executeQuery(sql, new BeanListProcessor(MdmPsndocVO.class));
		return list.toArray(new MdmPsndocVO[0]);
		
	}

}
