/**
 * 
 */
package nc.vo.tam.tongren003;

import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFDateTime;

/**
 * @author 28729
 *
 */
public class PaiBanAuditMsg2 extends SuperVO {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String pk_msg;
	private String pk_corp;
	public String getPk_corp() {
		return pk_corp;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}

	private UFDateTime audittime;
	private UFDateTime audittime2;
	private String ddate;
	
	
	public String getDdate() {
		return ddate;
	}

	public void setDdate(String ddate) {
		this.ddate = ddate;
	}

	public UFDateTime getAudittime2() {
		return audittime2;
	}

	public void setAudittime2(UFDateTime audittime2) {
		this.audittime2 = audittime2;
	}

	public String getAuditpsn2() {
		return auditpsn2;
	}

	public void setAuditpsn2(String auditpsn2) {
		this.auditpsn2 = auditpsn2;
	}

	public String getPk_msg() {
		return pk_msg;
	}

	public void setPk_msg(String pk_msg) {
		this.pk_msg = pk_msg;
	}

	public UFDateTime getAudittime() {
		return audittime;
	}

	public void setAudittime(UFDateTime audittime) {
		this.audittime = audittime;
	}

	public String getAuditpsn() {
		return auditpsn;
	}

	public void setAuditpsn(String auditpsn) {
		this.auditpsn = auditpsn;
	}

	public Integer getAudittype() {
		return audittype;
	}

	public void setAudittype(Integer audittype) {
		this.audittype = audittype;
	}

	public String getPk_dept() {
		return pk_dept;
	}

	public void setPk_dept(String pk_dept) {
		this.pk_dept = pk_dept;
	}

	public String getVperiod() {
		return vperiod;
	}

	public void setVperiod(String vperiod) {
		this.vperiod = vperiod;
	}

	public Integer getVbillstatus() {
		return vbillstatus;
	}

	public void setVbillstatus(Integer vbillstatus) {
		this.vbillstatus = vbillstatus;
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

	private String auditpsn;
	private String auditpsn2;
	private Integer audittype;
	private String pk_dept;
	private String vperiod;
	private Integer vbillstatus;
	private UFDateTime ts;
	private Integer dr;
	private String pk_psndoc;
	private String pk_bb;
	
	
	
	public String getPk_bb() {
		return pk_bb;
	}

	public void setPk_bb(String pk_bb) {
		this.pk_bb = pk_bb;
	}

	public String getPk_psndoc() {
		return pk_psndoc;
	}

	public void setPk_psndoc(String pk_psndoc) {
		this.pk_psndoc = pk_psndoc;
	}

	/**
	 * 
	 */
	public PaiBanAuditMsg2() {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see nc.vo.pub.SuperVO#getPKFieldName()
	 */
	@Override
	public String getPKFieldName() {
		// TODO Auto-generated method stub
		return "pk_msg";
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
		return "trtam_paiban_msg2";
	}

}
