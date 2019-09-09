package nc.ui.tam.tongren.basepower005;

import java.util.ArrayList;

import nc.bs.framework.common.NCLocator;
import nc.itf.uap.IUAPQueryBS;
import nc.jdbc.framework.processor.BeanListProcessor;
import nc.ui.pub.ClientEnvironment;
import nc.ui.trade.pub.IVOTreeDataByID;
import nc.vo.pub.SuperVO;
import nc.vo.tam.tongren.power.RoleVO;
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
		return "role_code+role_name+deptname";
	}

	public SuperVO[] getTreeVO() {
		String corp=ClientEnvironment.getInstance().getCorporation().getPrimaryKey();
		
		
		String sql = 
//			"select pk_role,role_code,role_name,'"+IConstant.RoleFlag+"' role_memo from sm_role where  " + " PK_CORP = '"+corp+"' " +
//		" OR exists (select 1 from SM_ROLE_CORP_ALLOC b where b.pk_corp ='"+corp+"' and sm_role.pk_role = b.pk_role ) " +
//		" union all " +
		" select distinct sm.cuserid pk_role,user_code role_code,user_name role_name,nvl(deptname,user_name) deptname ,'"+IConstant.UserFlag+"' role_memo from sm_user sm " +
		" inner join sm_user_role role on sm.cuserid=role.cuserid "+		
		" left join sm_userandclerk cl on cl.userid=sm.cuserid left join bd_psndoc psn on cl.pk_psndoc=psn.pk_psnbasdoc " +
				" left join bd_deptdoc dept on dept.pk_deptdoc=psn.pk_deptdoc  " +
				"where role.pk_role in('0001A11000000010PHOT','00018L1000000010MPRI') and sm.pk_corp in( '"+corp+"','0001','@@@@')  and nvl(locked_tag,'N')='N' order by nvl(deptname,user_name),user_code" ;
//0001A11000000010PHOT 00018L1000000010MPRI
		
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
			vo2.setDeptname("");
//			list.add(vo1);
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
