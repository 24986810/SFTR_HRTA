/**
 * 
 */
package nc.vo.tam.tongren011;

import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDateTime;

/**
 * @author 28729
 *
 */
public class AdjustTamVO extends SuperVO {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String pk_corp;
	public String getPk_corp() {
		return pk_corp;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}
	private Integer istate;
	public Integer getIstate() {
		return istate;
	}

	public void setIstate(Integer istate) {
		this.istate = istate;
	}
	private String pk_apply;//	pk_apply
	private String  pk_psn;//	pk_psn
	private UFDate  ddate;//	日期
	private String  pk_dept;//	pk_dept
	private String  pk_bb_old;//	pk_bb_old
	private String  pk_bb_new;//	pk_bb_new
	private String  bbname_old;//	原班别
	private String  bbname_new;//	调换班别
	private UFDateTime  ts;//	ts
	private Integer  dr	;//dr
	private String  vdef1;//	vdef1
	private String  vdef2;//	vdef2
	private UFDate  dapplydate;//	申请日期
	private String  pk_psn2;//	pk_psn2
	private String  bbnameold2;//	换班人原班别
	private String  pk_dept2;//	pk_dept2
	private String  vtype;//	vtype
	private String  pk_bb_old2;//	pk_bb_old2
	private String  pk_bb_audit	;//pk_bb_audit
	private String  bbauditname	;//bbauditname
	private String vmemo;
	private String deptname;
	private String pk_temp;// 值班模板
	
	
	
	
	public String getPk_temp() {
		return pk_temp;
	}

	public void setPk_temp(String pk_temp) {
		this.pk_temp = pk_temp;
	}

	public String getDeptname() {
		return deptname;
	}

	public void setDeptname(String deptname) {
		this.deptname = deptname;
	}

	public String getVmemo() {
		return vmemo;
	}

	public void setVmemo(String vmemo) {
		this.vmemo = vmemo;
	}

	public String getPk_apply() {
		return pk_apply;
	}

	public void setPk_apply(String pk_apply) {
		this.pk_apply = pk_apply;
	}

	public String getPk_psn() {
		return pk_psn;
	}

	public void setPk_psn(String pk_psn) {
		this.pk_psn = pk_psn;
	}

	public UFDate getDdate() {
		return ddate;
	}

	public void setDdate(UFDate ddate) {
		this.ddate = ddate;
	}

	public String getPk_dept() {
		return pk_dept;
	}

	public void setPk_dept(String pk_dept) {
		this.pk_dept = pk_dept;
	}

	public String getPk_bb_old() {
		return pk_bb_old;
	}

	public void setPk_bb_old(String pk_bb_old) {
		this.pk_bb_old = pk_bb_old;
	}

	public String getPk_bb_new() {
		return pk_bb_new;
	}

	public void setPk_bb_new(String pk_bb_new) {
		this.pk_bb_new = pk_bb_new;
	}

	public String getBbname_old() {
		return bbname_old;
	}

	public void setBbname_old(String bbname_old) {
		this.bbname_old = bbname_old;
	}

	public String getBbname_new() {
		return bbname_new;
	}

	public void setBbname_new(String bbname_new) {
		this.bbname_new = bbname_new;
	}

	public UFDateTime getTs() {
		return ts;
	}

	public void setTs(UFDateTime ts) {
		this.ts = ts;
	}

	public Integer getDr() {
		return dr;
	}

	public void setDr(Integer dr) {
		this.dr = dr;
	}

	public String getVdef1() {
		return vdef1;
	}

	public void setVdef1(String vdef1) {
		this.vdef1 = vdef1;
	}

	public String getVdef2() {
		return vdef2;
	}

	public void setVdef2(String vdef2) {
		this.vdef2 = vdef2;
	}

	public UFDate getDapplydate() {
		return dapplydate;
	}

	public void setDapplydate(UFDate dapplydate) {
		this.dapplydate = dapplydate;
	}

	public String getPk_psn2() {
		return pk_psn2;
	}

	public void setPk_psn2(String pk_psn2) {
		this.pk_psn2 = pk_psn2;
	}

	public String getBbnameold2() {
		return bbnameold2;
	}

	public void setBbnameold2(String bbnameold2) {
		this.bbnameold2 = bbnameold2;
	}

	public String getPk_dept2() {
		return pk_dept2;
	}

	public void setPk_dept2(String pk_dept2) {
		this.pk_dept2 = pk_dept2;
	}

	public String getVtype() {
		return vtype;
	}

	public void setVtype(String vtype) {
		this.vtype = vtype;
	}

	public String getPk_bb_old2() {
		return pk_bb_old2;
	}

	public void setPk_bb_old2(String pk_bb_old2) {
		this.pk_bb_old2 = pk_bb_old2;
	}

	public String getPk_bb_audit() {
		return pk_bb_audit;
	}

	public void setPk_bb_audit(String pk_bb_audit) {
		this.pk_bb_audit = pk_bb_audit;
	}

	public String getBbauditname() {
		return bbauditname;
	}

	public void setBbauditname(String bbauditname) {
		this.bbauditname = bbauditname;
	}

	/**
	 * 
	 */
	public AdjustTamVO() {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see nc.vo.pub.SuperVO#getPKFieldName()
	 */
	@Override
	public String getPKFieldName() {
		// TODO Auto-generated method stub
		return "pk_apply";
	}

	/* (non-Javadoc)
	 * @see nc.vo.pub.SuperVO#getParentPKFieldName()
	 */
	@Override
	public String getParentPKFieldName() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see nc.vo.pub.SuperVO#getTableName()
	 */
	@Override
	public String getTableName() {
		// TODO Auto-generated method stub
		return "trtam_paiban_adjust";
	}

}
