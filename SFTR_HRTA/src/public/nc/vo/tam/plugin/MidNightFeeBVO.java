/**
 * 
 */
package nc.vo.tam.plugin;

import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFDouble;

/**
 * @author 28729
 *
 */
public class MidNightFeeBVO extends SuperVO {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private UFDateTime ts;
	private Integer dr;
	private String pk_paiban;
	private String pk_paiban_b;
	private UFDate ddate;
	private String pk_bb;
	private String pk_corp;
	private String pk_deptzb;
	private String pptype; // 排班类型，1为补班
	private String pk_temp;
	private Integer uploadnum;// 上传次数
	private String dclassperiod;// 排班期间
	private String vbillstatus1;
	private String vbillstatus2;
	private String vbillstatus3;
	private String deptzbname;
	private String memo;
	private UFBoolean biszb;
	private String pk_psndoc;
	private String pk_dept;
	public UFBoolean getBiszb() {
		return biszb;
	}
	public void setBiszb(UFBoolean biszb) {
		this.biszb = biszb;
	}
	public String getDclassperiod() {
		return dclassperiod;
	}
	public void setDclassperiod(String dclassperiod) {
		this.dclassperiod = dclassperiod;
	}
	public UFDate getDdate() {
		return ddate;
	}
	public void setDdate(UFDate ddate) {
		this.ddate = ddate;
	}
	public String getDeptzbname() {
		return deptzbname;
	}
	public void setDeptzbname(String deptzbname) {
		this.deptzbname = deptzbname;
	}
	public Integer getDr() {
		return dr;
	}
	public void setDr(Integer dr) {
		this.dr = dr;
	}
	public String getMemo() {
		return memo;
	}
	public void setMemo(String memo) {
		this.memo = memo;
	}
	public String getPk_bb() {
		return pk_bb;
	}
	public void setPk_bb(String pk_bb) {
		this.pk_bb = pk_bb;
	}
	public String getPk_corp() {
		return pk_corp;
	}
	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}
	public String getPk_dept() {
		return pk_dept;
	}
	public void setPk_dept(String pk_dept) {
		this.pk_dept = pk_dept;
	}
	public String getPk_deptzb() {
		return pk_deptzb;
	}
	public void setPk_deptzb(String pk_deptzb) {
		this.pk_deptzb = pk_deptzb;
	}
	public String getPk_paiban() {
		return pk_paiban;
	}
	public void setPk_paiban(String pk_paiban) {
		this.pk_paiban = pk_paiban;
	}
	public String getPk_paiban_b() {
		return pk_paiban_b;
	}
	public void setPk_paiban_b(String pk_paiban_b) {
		this.pk_paiban_b = pk_paiban_b;
	}
	public String getPk_psndoc() {
		return pk_psndoc;
	}
	public void setPk_psndoc(String pk_psndoc) {
		this.pk_psndoc = pk_psndoc;
	}
	public String getPk_temp() {
		return pk_temp;
	}
	public void setPk_temp(String pk_temp) {
		this.pk_temp = pk_temp;
	}
	public String getPptype() {
		return pptype;
	}
	public void setPptype(String pptype) {
		this.pptype = pptype;
	}
	public UFDateTime getTs() {
		return ts;
	}
	public void setTs(UFDateTime ts) {
		this.ts = ts;
	}
	public Integer getUploadnum() {
		return uploadnum;
	}
	public void setUploadnum(Integer uploadnum) {
		this.uploadnum = uploadnum;
	}
	public String getVbillstatus1() {
		return vbillstatus1;
	}
	public void setVbillstatus1(String vbillstatus1) {
		this.vbillstatus1 = vbillstatus1;
	}
	public String getVbillstatus2() {
		return vbillstatus2;
	}
	public void setVbillstatus2(String vbillstatus2) {
		this.vbillstatus2 = vbillstatus2;
	}
	public String getVbillstatus3() {
		return vbillstatus3;
	}
	public void setVbillstatus3(String vbillstatus3) {
		this.vbillstatus3 = vbillstatus3;
	}
	@Override
	public String getPKFieldName() {
		// TODO Auto-generated method stub
		return "pk_paiban_b";
	}
	@Override
	public String getParentPKFieldName() {
		// TODO Auto-generated method stub
		return "pk_paiban";
	}
	@Override
	public String getTableName() {
		// TODO Auto-generated method stub
		return "trtam_midnightfee_b";
	}
	
	
}
