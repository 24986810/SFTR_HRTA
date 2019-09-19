package nc.bs.tam.plugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;

import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.bs.pub.taskcenter.BgWorkingContext;
import nc.bs.pub.taskcenter.IBackgroundWorkPlugin;
import nc.jdbc.framework.processor.BeanListProcessor;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.tam.tongren001.DeptKqBVO;
import nc.vo.tam.tongren001.DeptKqVO;
import nc.vo.tam.tongren003.PaibanWeekVO;
import nc.vo.tam.tongren003.PanbanWeekBVO;
import nc.vo.tam.tongren008.ApplyBVO;

/**
 * 长病假人员排班
 * @author zhuchaoli
 *
 */
public class SynLongSickPaiBan implements IBackgroundWorkPlugin {

	public String executeTask(BgWorkingContext arg0) throws BusinessException {
		// TODO Auto-generated method stub
		exportConfirm(arg0);
		
		return null;
	}

	
	/**
	 * 查询长病假下的人员
	 * @return
	 * @throws DAOException
	 */
	public DeptKqBVO[] getDeptKqBVOS() throws DAOException{
		String vcode="160";//长病假  vcode
		String pk_parent="10028L10000000000GN4"; //长病假 pk_dept 主键
		BaseDAO dao = new BaseDAO();
		String sql = "SELECT *"
			+"  FROM trtam_deptdoc_kq_b b"
			+" WHERE b.pk_dept in " 
			+"(select a.pk_dept from trtam_deptdoc_kq a where a.vcode ='"+vcode+"' or a.pk_parent ='"+pk_parent+"')";
		ArrayList<DeptKqBVO> list = (ArrayList<DeptKqBVO>)dao.executeQuery(sql, new BeanListProcessor(DeptKqBVO.class));
		return list.toArray(new DeptKqBVO[0]);
	}
	
	
	/**
	 * 主方法
	 * @param arg0
	 * @throws DAOException
	 */
	public void exportConfirm(BgWorkingContext arg0) throws DAOException{
		UFDate curdate = new UFDate();
		//查询长病假
		DeptKqBVO[] deptKqBVOS = getDeptKqBVOS();
		
		BaseDAO basedao = new BaseDAO();
		
		ArrayList<PanbanWeekBVO> list_b = new ArrayList<PanbanWeekBVO>(); 
		ArrayList<PanbanWeekBVO> list_c = new ArrayList<PanbanWeekBVO>(); 
		ArrayList<PaibanWeekVO> list = new ArrayList<PaibanWeekVO>();
		ArrayList<PaibanWeekVO> list_a = new ArrayList<PaibanWeekVO>();
		ArrayList<DeptKqBVO> list_dept = new ArrayList<DeptKqBVO>(); //用来装没有排班记录人员的部门
		
		for(DeptKqBVO deptkqbvo :deptKqBVOS){
			String pk_psndoc = deptkqbvo.getPk_psndoc();
			if(deptkqbvo.getDenddate()==null || deptkqbvo.getDenddate().compareTo(curdate)>=0 ){
				//没有结束日期 或结束日期比服务器日期大
				String pk_dept = deptkqbvo.getPk_dept();
				//PaibanWeekVO[] weekvos = (PaibanWeekVO[])HYPubBO_Client.queryByCondition(PaibanWeekVO.class,
				//		"isnull(dr,0)=0 and pptype is null and pk_dept = '"+pk_dept+"' and pk_psndoc='"+pk_psndoc+"'  ");
				String sql = "select * from trtam_paiban where nvl(dr,0)=0 and pptype is null and pk_dept = '"+pk_dept+"' and pk_psndoc = '"+pk_psndoc+"' ";
				ArrayList<PaibanWeekVO> weekvoslist = (ArrayList<PaibanWeekVO>)basedao.executeQuery(sql, new BeanListProcessor(PaibanWeekVO.class));
				
				int day = curdate.getDay(); //日期
				
				
				PaibanWeekVO[] weekvos = weekvoslist.toArray( new PaibanWeekVO[weekvoslist.size()]);  //new PanbanWeekBVO[list_b.size()]
				for (PaibanWeekVO weekvo :weekvos){
					String vbbname = (String) weekvo.getAttributeValue("vbbname"+day);
					if(vbbname == null ){ //未排班
						String vdate = weekvo.getVdate();
						String stringBegin = vdate.substring(0, 10);
						UFDate date = new UFDate(stringBegin);
						
						if(date.getMonth()==curdate.getMonth()){
							weekvo.setAttributeValue("vbbname"+day, "全天病假"); 
							weekvo.setAttributeValue("pk_bb"+day, "10028L100000000002BQ");  //全天病假对应的pk_bb 
							list.add(weekvo);
							
							//子表
							PanbanWeekBVO bvo = new PanbanWeekBVO();
							bvo.setPrimaryKey(null);
							bvo.setDr(0);
							bvo.setPk_corp(weekvo.getPk_corp());
							bvo.setPk_psndoc(weekvo.getPk_psndoc());
							bvo.setPk_dept(weekvo.getPk_dept());
							bvo.setPk_paiban(weekvo.getPrimaryKey());
							bvo.setPk_bb("10028L100000000002BQ");
							bvo.setBiszb(new UFBoolean(false));
							bvo.setDdate(curdate);
							bvo.setMemo(weekvo.getMemo());
							list_b.add(bvo);
							
						}
						
					}
				}
				
				
				//当前月份无排班记录
				if(weekvos ==null || weekvos.length ==0){
					PaibanWeekVO weekVO = new PaibanWeekVO();
					//String sqlwhere =	" pk_dept='" +deptkqbvo.getPk_dept()+ "' and isnull(bisseal,'N')='N' and isnull(dr,0)=0 ";
					//DeptKqVO[] deptkqvos = (DeptKqVO[])HYPubBO_Client.queryByCondition(DeptKqVO.class, sqlwhere);
					
					sql = "select * from trtam_deptdoc_kq where pk_dept = '"+deptkqbvo.getPk_dept()+"' and nvl(bisseal,'N')='N' and nvl(dr,0)=0 ";
					ArrayList<DeptKqVO> deptkqvoslist = (ArrayList<DeptKqVO>)basedao.executeQuery(sql, new BeanListProcessor(DeptKqVO.class));
					 DeptKqVO[] deptkqvos = deptkqvoslist.toArray( new DeptKqVO[deptkqvoslist.size()]);  //new PanbanWeekBVO[list_b.size()]
					
					weekVO.setPk_corp(deptkqvos[0].getPk_corp());
					weekVO.setPrimaryKey(null);
					weekVO.setPk_dept(deptkqvos[0].getPk_dept());
					//得到期间
					int day2 = curdate.getDay();
					int year = curdate.getYear();
					//开始时间
					String stringBegin = curdate.getDateBefore(day2-1).toString();
					int daysMonth = curdate.getDaysMonth();
					//结束时间
					String stringEnd = curdate.getDateBefore(day2).getDateAfter(daysMonth).toString();
					
					weekVO.setVdate(stringBegin+"至"+stringEnd);
					weekVO.setPk_psndoc(deptkqbvo.getPk_psndoc());
					
					weekVO.setAttributeValue("vbbname"+day2, "全天病假");
					weekVO.setAttributeValue("pk_bb"+day2, "10028L100000000002BQ");
					
					list_dept.add(deptkqbvo);
					
					list_a.add(weekVO);
					
					
					/*//子表
					PanbanWeekBVO bvo = new PanbanWeekBVO();
					bvo.setPrimaryKey(null);
					bvo.setDr(0);
					bvo.setPk_corp(weekVO.getPk_corp());
					bvo.setPk_psndoc(weekVO.getPk_psndoc());
					bvo.setPk_dept(weekVO.getPk_dept());
					bvo.setPk_paiban(weekvo.getPrimaryKey());
					bvo.setPk_bb("10028L100000000002BQ");
					bvo.setBiszb(new UFBoolean(false));
					bvo.setDdate(curdate);
					bvo.setMemo(weekvo.getMemo());
					list_b.add(bvo);*/
				}
			}
		}
		
		//保存  有排班记录时的保存
		BaseDAO dao = new BaseDAO();
		if(list!=null&&list.size()>0){
			dao.updateVOArray(list.toArray(new PaibanWeekVO[0]));
		}
		
		//子表 有排班记录时的保存
		//HYPubBO_Client.insertAry(list_b.toArray(new PanbanWeekBVO[list_b.size()]));
		dao.insertVOArray(list_b.toArray(new PanbanWeekBVO[list_b.size()]));
		//无排班记录时的排班主表插入保存
		//HYPubBO_Client.insertAry(list_a.toArray(new PaibanWeekVO[list_a.size()]));
		dao.insertVOArray(list_a.toArray(new PaibanWeekVO[list_a.size()]));
		
		//最后 无排班记录时的排班 子表插入保存
		for(DeptKqBVO deptkqbvo :list_dept){
			String pk_psndoc = deptkqbvo.getPk_psndoc();
			//得到期间
			int day2 = curdate.getDay();
			int year = curdate.getYear();
			//开始时间
			String stringBegin = curdate.getDateBefore(day2-1).toString();
			int daysMonth = curdate.getDaysMonth();
			//结束时间
			String stringEnd = curdate.getDateBefore(day2).getDateAfter(daysMonth).toString();
			String vdate_new = stringBegin+"至"+stringEnd;
			
			String pk_dept = deptkqbvo.getPk_dept();
			//PaibanWeekVO[] weekvos = (PaibanWeekVO[])HYPubBO_Client.queryByCondition(PaibanWeekVO.class,
			//		"isnull(dr,0)=0 and pptype is null and pk_dept = '"+pk_dept+"' and vdate = '"+vdate_new+"'and pk_psndoc='"+pk_psndoc+"'  ");
			//and pptype!='1'
			String sql1= "select * from trtam_paiban where nvl(dr,0)=0 and pptype is null and pk_dept= '"+pk_dept+"' and vdate = '"+vdate_new+"' and pk_psndoc = '"+pk_psndoc+"' ";
			ArrayList<PaibanWeekVO> weekvoslist = (ArrayList<PaibanWeekVO>)basedao.executeQuery(sql1, new BeanListProcessor(PaibanWeekVO.class));
			PaibanWeekVO[] weekvos = weekvoslist.toArray( new PaibanWeekVO[weekvoslist.size()]);  // new PaibanWeekVO[list_a.size()]
			
			//子表
			PanbanWeekBVO bvo = new PanbanWeekBVO();
			bvo.setPrimaryKey(null);
			bvo.setDr(0);
			bvo.setPk_corp(weekvos[0].getPk_corp());
			bvo.setPk_psndoc(weekvos[0].getPk_psndoc());
			bvo.setPk_dept(weekvos[0].getPk_dept());
			bvo.setPk_paiban(weekvos[0].getPrimaryKey());
			bvo.setPk_bb("10028L100000000002BQ");
			bvo.setBiszb(new UFBoolean(false));
			bvo.setDdate(curdate);
			bvo.setMemo(weekvos[0].getMemo());
			list_c.add(bvo);
				
		}
		
		//子表 有排班记录时的保存
		//HYPubBO_Client.insertAry(list_c.toArray(new PanbanWeekBVO[list_c.size()]));
		dao.insertVOArray(list_c.toArray(new PanbanWeekBVO[list_c.size()]));
	}
	
}
