package nc.vo.tam.tongren019;

import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFDouble;

public class GxReportVO extends SuperVO {
	private String pk_gx_snap;
	private String trtam_deptdoc_kp;
	private String vyear;
	private String psncode;
	private String psnname;
	private String vname;
	private String joinyear;
	private UFBoolean groupdef15;
	private String joinworkdate;
	private String joinsysdate;
	private UFDouble ngl;
	private UFDouble nyfgx;
	private UFDouble nkcgx;
	private UFDouble gxyy;
	private UFDouble nsfgx;
	private UFDouble nyygx;
	private UFDouble nsxgx;
	private UFDouble qnbjts;
	private UFDouble qnsjts;
	private UFDouble qnzjdg;
	private String bz;
	private String pk_psndoc;
	private String pk_deptdoc;
	private String pk_corp;
	private UFDateTime ts;
	private Integer dr;
	
	
	
	public String getTrtam_deptdoc_kp() {
		return trtam_deptdoc_kp;
	}

	public void setTrtam_deptdoc_kp(String trtam_deptdoc_kp) {
		this.trtam_deptdoc_kp = trtam_deptdoc_kp;
	}

	public String getPk_gx_snap() {
		return pk_gx_snap;
	}

	public void setPk_gx_snap(String pk_gx_snap) {
		this.pk_gx_snap = pk_gx_snap;
	}

	public Integer getDr() {
		return dr;
	}

	public void setDr(Integer dr) {
		this.dr = dr;
	}

	public String getPk_corp() {
		return pk_corp;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}

	public UFDateTime getTs() {
		return ts;
	}

	public void setTs(UFDateTime ts) {
		this.ts = ts;
	}

	public String getPk_deptdoc() {
		return pk_deptdoc;
	}

	public void setPk_deptdoc(String pk_deptdoc) {
		this.pk_deptdoc = pk_deptdoc;
	}

	public String getPk_psndoc() {
		return pk_psndoc;
	}

	public void setPk_psndoc(String pk_psndoc) {
		this.pk_psndoc = pk_psndoc;
	}

	public String getJoinsysdate() {
		return joinsysdate;
	}

	

	public String getJoinworkdate() {
		return joinworkdate;
	}

	public void setJoinworkdate(String joinworkdate) {
		this.joinworkdate = joinworkdate;
	}

	public void setJoinsysdate(String joinsysdate) {
		this.joinsysdate = joinsysdate;
	}

	public UFBoolean getGroupdef15() {
		return groupdef15;
	}

	public void setGroupdef15(UFBoolean groupdef15) {
		this.groupdef15 = groupdef15;
	}

	

	public String getBz() {
		return bz;
	}

	public void setBz(String bz) {
		this.bz = bz;
	}

	public UFDouble getQnsjts() {
		return qnsjts;
	}

	public void setQnsjts(UFDouble qnsjts) {
		this.qnsjts = qnsjts;
	}

	public UFDouble getQnzjdg() {
		return qnzjdg;
	}

	public void setQnzjdg(UFDouble qnzjdg) {
		this.qnzjdg = qnzjdg;
	}

	public UFDouble getGxyy() {
		return gxyy;
	}

	public void setGxyy(UFDouble gxyy) {
		this.gxyy = gxyy;
	}


	public String getJoinyear() {
		return joinyear;
	}

	public void setJoinyear(String joinyear) {
		this.joinyear = joinyear;
	}

	public UFDouble getNgl() {
		return ngl;
	}

	public void setNgl(UFDouble ngl) {
		this.ngl = ngl;
	}

	public UFDouble getNkcgx() {
		return nkcgx;
	}

	public void setNkcgx(UFDouble nkcgx) {
		this.nkcgx = nkcgx;
	}

	public UFDouble getNsfgx() {
		return nsfgx;
	}

	public void setNsfgx(UFDouble nsfgx) {
		this.nsfgx = nsfgx;
	}

	public UFDouble getNsxgx() {
		return nsxgx;
	}

	public void setNsxgx(UFDouble nsxgx) {
		this.nsxgx = nsxgx;
	}

	public UFDouble getNyfgx() {
		return nyfgx;
	}

	public void setNyfgx(UFDouble nyfgx) {
		this.nyfgx = nyfgx;
	}

	public UFDouble getNyygx() {
		return nyygx;
	}

	public void setNyygx(UFDouble nyygx) {
		this.nyygx = nyygx;
	}

	public String getPsncode() {
		return psncode;
	}

	public void setPsncode(String psncode) {
		this.psncode = psncode;
	}

	public String getPsnname() {
		return psnname;
	}

	public void setPsnname(String psnname) {
		this.psnname = psnname;
	}

	public UFDouble getQnbjts() {
		return qnbjts;
	}

	public void setQnbjts(UFDouble qnbjts) {
		this.qnbjts = qnbjts;
	}

	public String getVname() {
		return vname;
	}

	public void setVname(String vname) {
		this.vname = vname;
	}

	public String getVyear() {
		return vyear;
	}

	public void setVyear(String vyear) {
		this.vyear = vyear;
	}

	@Override
	public String getPKFieldName() {
		// TODO Auto-generated method stub
		return "pk_gx_snap";
	}

	@Override
	public String getParentPKFieldName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getTableName() {
		// TODO Auto-generated method stub
		return "tam_gx_snap";
	}

}
