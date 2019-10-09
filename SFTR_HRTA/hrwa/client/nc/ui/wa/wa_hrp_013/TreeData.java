package nc.ui.wa.wa_hrp_013;

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
		String pk_user = ClientEnvironment.getInstance().getUser().getPrimaryKey();
		
		
		String sql = 
//			"select pk_role,role_code,role_name,'"+IConstant.RoleFlag+"' role_memo from sm_role where  " + " PK_CORP = '"+corp+"' " +
//		" OR exists (select 1 from SM_ROLE_CORP_ALLOC b where b.pk_corp ='"+corp+"' and sm_role.pk_role = b.pk_role ) " +
//		" union all " +
		" select f.cuserid pk_role, f.user_code role_code, f.user_name role_name,dept.deptname, '11111111111111111111' role_memo"
		+"  from sm_userandclerk e"
		+"  left join sm_user f"
		+"    on e.userid = f.cuserid"
		+"  left join bd_psndoc psn"
		+"    on e.pk_psndoc = psn.pk_psnbasdoc"
		+"  left join bd_deptdoc dept"
		+"    on dept.pk_deptdoc = psn.pk_deptdoc"
		+" where userid <> '"+pk_user+"'"
		+"   and f.pk_corp in ('"+corp+"', '0001', '@@@@')"
		+"   and e.pk_psndoc in"
		+"       (select d.pk_psnbasdoc"
		+"          from trtam_deptdoc_kq_b c"
		+"          left join bd_psndoc d"
		+"            on c.pk_psndoc = d.pk_psndoc"
		+"         where (nvl(c.dr, 0) = 0)"
		+"           and (nvl(d.dr, 0) = 0)"
		+"           and pk_dept in"
		+"               (select pk_dept"
		+"                  from trtam_deptdoc_kq"
		+"                 where pk_hrp_dept in"
		+"                       (select b.pk_deptdoc"
		+"                          from sm_userandclerk a"
		+"                          left join bd_psndoc b"
		+"                            on a.pk_psndoc = b.pk_psnbasdoc"
		+"                         where a.dr = 0"
		+"                           and b.dr = 0"
		+"                           and a.userid = '"+pk_user+"')))";

//		" select cuserid,user_code,user_name,'"+IConstant.UserFlag+"' from sm_user where pk_corp in( '"+corp+"','0001','@@@@')" ;
//		" union all select '"+IConstant.RoleFlag+"' pk_role,' ' role_code,'角色列表' role_name,'' role_memo from dual " +
//		" union all  select '"+IConstant.UserFlag+"',' ','用户列表','' from dual  ";
		
		
		IUAPQueryBS service = (IUAPQueryBS)NCLocator.getInstance().lookup(IUAPQueryBS.class.getName());
		try{
			if(treevos == null ){
			@SuppressWarnings("unchecked")
			ArrayList<RoleVO> list =(ArrayList<RoleVO>) service.executeQuery( sql,new BeanListProcessor(RoleVO.class));
//			RoleVO vo1=new RoleVO();
//			vo1.setPk_role("00000000000000000000");
//			vo1.setRole_name("角色列表");
//			vo1.setRole_code("");
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
