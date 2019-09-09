package nc.ui.wa.wa_hrp_013;

import java.util.ArrayList;

import nc.bs.framework.common.NCLocator;
import nc.itf.uap.IUAPQueryBS;
import nc.jdbc.framework.processor.BeanListProcessor;
import nc.ui.pub.ClientEnvironment;
import nc.ui.trade.pub.IVOTreeDataByID;
import nc.vo.pub.SuperVO;
import nc.vo.uap.rbac.RoleVO;
import nc.itf.hrp.pub.IConstant;

/**
 * @author admin
 *
 */
public class TreeData implements IVOTreeDataByID  {

	SuperVO[] treevos = null;
	/**
	 * 
	 */
	public TreeData() {
		// TODO Auto-generated constructor stub
	}

	
	public String getIDFieldName() {
		// TODO Auto-generated method stub
		return "pk_role";
	}

	
	public String getParentIDFieldName() {
		// TODO Auto-generated method stub
		return "role_memo";
	}

	
	public String getShowFieldName() {
		// TODO Auto-generated method stub
		return "role_code+role_name";
	}

	public SuperVO[] getTreeVO() {
		String corp=ClientEnvironment.getInstance().getCorporation().getPrimaryKey();
		
		
		String sql = 
			"select pk_role,role_code,role_name,'"+IConstant.RoleFlag+"' role_memo from sm_role where  " + " PK_CORP = '"+corp+"' " +
		" OR exists (select 1 from SM_ROLE_CORP_ALLOC b where b.pk_corp ='"+corp+"' and sm_role.pk_role = b.pk_role ) " +
		" union all " +
		" select cuserid,user_code,user_name,'"+IConstant.UserFlag+"' from sm_user where pk_corp in( '"+corp+"','0001','@@@@')" ;
//		" union all select '"+IConstant.RoleFlag+"' pk_role,' ' role_code,'角色列表' role_name,'' role_memo from dual " +
//		" union all  select '"+IConstant.UserFlag+"',' ','用户列表','' from dual  ";
		
		
		IUAPQueryBS service = (IUAPQueryBS)NCLocator.getInstance().lookup(IUAPQueryBS.class.getName());
		try{
			if(treevos == null ){
			@SuppressWarnings("unchecked")
			ArrayList<RoleVO> list =(ArrayList<RoleVO>) service.executeQuery( sql,new BeanListProcessor(RoleVO.class));
			RoleVO vo1=new RoleVO();
			vo1.setPk_role("00000000000000000000");
			vo1.setRole_name("角色列表");
			vo1.setRole_code("");
			RoleVO vo2=new RoleVO();
			vo2.setPk_role("11111111111111111111");
			vo2.setRole_name("用户列表");
			vo2.setRole_code("");
			list.add(vo1);
			list.add(vo2);
			
			SuperVO[] queryVos = (RoleVO[])list.toArray(new RoleVO[0]);
			return queryVos;
			}else
				return treevos;
		}
		catch(Exception e){
			return null;
		}
	}
	
	public void setTreeVO(SuperVO[] superVOs ) {
		
		this.treevos = superVOs;
		
	}
}
