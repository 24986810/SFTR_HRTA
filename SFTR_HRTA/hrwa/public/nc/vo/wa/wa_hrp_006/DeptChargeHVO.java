/**
 * 
 */
package nc.vo.wa.wa_hrp_006;

import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFDouble;

/**
 * @author szh
 *
 */
public class DeptChargeHVO extends SuperVO {
	private String pk_dept_charge;
	private UFDateTime ts;
	private Integer dr;
	private String pk_corp;
	private String vbillno;
	/*单据号*/
	private String vbillname;
	/*单据名称*/

	private String vmemo;
	/*说明*/
	private String pk_billtype ; 
	/*单据类型*/
	private Integer vbillstatus ;
	/*单据状态*/
	private String vapproveid;
	/*审核人*/
	private UFDate dapprovedate;
	/*审核日期*/
	private String vapprovenote ;
	/*审核批语*/
	private String voperatorid ;
	/*制单人*/
	private UFDate dmakedate;
	
	private String reddased;// 调拨奖金红冲标识
	private String pk_dept_out;
	private String pk_dept_in;
	private UFDouble nmny;
	private String vdef1;
	private String vdef2;
	private String vdef3;
	private String vdef4;
	private String vdef5;
	private String vdef6;
	private String vdef7;
	private String vdef8;
	private UFDate vdef9;
	private UFBoolean vdef10;
	private UFDouble vdef11;
	private UFDouble vdef12;
	private UFDouble vdef13;
	private UFDouble vdef14;
	private UFDouble vdef15;
	private String vdef16;
	private String vdef17;
	private String vdef18;
	private UFDate vdef19;
	private UFDateTime vdef20;
	
	private String vyear;
	private String vperiod;
	
	
	
	public String getReddased() {
		return reddased;
	}

	public void setReddased(String reddased) {
		this.reddased = reddased;
	}

	public String getVperiod() {
		return vperiod;
	}

	public void setVperiod(String vperiod) {
		this.vperiod = vperiod;
	}

	public String getVyear() {
		return vyear;
	}

	public void setVyear(String vyear) {
		this.vyear = vyear;
	}
	
	public String getVdef1() {
		return vdef1;
	}

	public void setVdef1(String vdef1) {
		this.vdef1 = vdef1;
	}

	public UFBoolean getVdef10() {
		return vdef10;
	}

	public void setVdef10(UFBoolean vdef10) {
		this.vdef10 = vdef10;
	}

	public UFDouble getVdef11() {
		return vdef11;
	}

	public void setVdef11(UFDouble vdef11) {
		this.vdef11 = vdef11;
	}

	public UFDouble getVdef12() {
		return vdef12;
	}

	public void setVdef12(UFDouble vdef12) {
		this.vdef12 = vdef12;
	}

	public UFDouble getVdef13() {
		return vdef13;
	}

	public void setVdef13(UFDouble vdef13) {
		this.vdef13 = vdef13;
	}

	public UFDouble getVdef14() {
		return vdef14;
	}

	public void setVdef14(UFDouble vdef14) {
		this.vdef14 = vdef14;
	}

	public UFDouble getVdef15() {
		return vdef15;
	}

	public void setVdef15(UFDouble vdef15) {
		this.vdef15 = vdef15;
	}

	public String getVdef16() {
		return vdef16;
	}

	public void setVdef16(String vdef16) {
		this.vdef16 = vdef16;
	}

	public String getVdef17() {
		return vdef17;
	}

	public void setVdef17(String vdef17) {
		this.vdef17 = vdef17;
	}

	public String getVdef18() {
		return vdef18;
	}

	public void setVdef18(String vdef18) {
		this.vdef18 = vdef18;
	}

	public UFDate getVdef19() {
		return vdef19;
	}

	public void setVdef19(UFDate vdef19) {
		this.vdef19 = vdef19;
	}

	public String getVdef2() {
		return vdef2;
	}

	public void setVdef2(String vdef2) {
		this.vdef2 = vdef2;
	}

	public UFDateTime getVdef20() {
		return vdef20;
	}

	public void setVdef20(UFDateTime vdef20) {
		this.vdef20 = vdef20;
	}

	public String getVdef3() {
		return vdef3;
	}

	public void setVdef3(String vdef3) {
		this.vdef3 = vdef3;
	}

	public String getVdef4() {
		return vdef4;
	}

	public void setVdef4(String vdef4) {
		this.vdef4 = vdef4;
	}

	public String getVdef5() {
		return vdef5;
	}

	public void setVdef5(String vdef5) {
		this.vdef5 = vdef5;
	}

	public String getVdef6() {
		return vdef6;
	}

	public void setVdef6(String vdef6) {
		this.vdef6 = vdef6;
	}

	public String getVdef7() {
		return vdef7;
	}

	public void setVdef7(String vdef7) {
		this.vdef7 = vdef7;
	}

	public String getVdef8() {
		return vdef8;
	}

	public void setVdef8(String vdef8) {
		this.vdef8 = vdef8;
	}

	public UFDate getVdef9() {
		return vdef9;
	}

	public void setVdef9(UFDate vdef9) {
		this.vdef9 = vdef9;
	}

	public UFDouble getNmny() {
		return nmny;
	}

	public void setNmny(UFDouble nmny) {
		this.nmny = nmny;
	}

	public String getPk_dept_in() {
		return pk_dept_in;
	}

	public void setPk_dept_in(String pk_dept_in) {
		this.pk_dept_in = pk_dept_in;
	}

	public String getPk_dept_out() {
		return pk_dept_out;
	}

	public void setPk_dept_out(String pk_dept_out) {
		this.pk_dept_out = pk_dept_out;
	}

	public String getPk_corp() {
		return pk_corp;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}

	/**
	 * 
	 */
	public DeptChargeHVO() {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see nc.vo.pub.SuperVO#getPKFieldName()
	 */
	@Override
	public String getPKFieldName() {
		// TODO Auto-generated method stub
		return "pk_dept_charge";
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
		return "wa_deptcharge";
	}

	public UFDate getDmakedate() {
		return dmakedate;
	}

	public void setDmakedate(UFDate dmakedate) {
		this.dmakedate = dmakedate;
	}

	public Integer getDr() {
		return dr;
	}

	public void setDr(Integer dr) {
		this.dr = dr;
	}


	public UFDateTime getTs() {
		return ts;
	}

	public void setTs(UFDateTime ts) {
		this.ts = ts;
	}


	public String getVoperatorid() {
		return voperatorid;
	}

	public void setVoperatorid(String voperatorid) {
		this.voperatorid = voperatorid;
	}

	public UFDate getDapprovedate() {
		return dapprovedate;
	}

	public void setDapprovedate(UFDate dapprovedate) {
		this.dapprovedate = dapprovedate;
	}

	public String getPk_billtype() {
		return pk_billtype;
	}

	public void setPk_billtype(String pk_billtype) {
		this.pk_billtype = pk_billtype;
	}

	public String getPk_dept_charge() {
		return pk_dept_charge;
	}

	public void setPk_dept_charge(String pk_dept_charge) {
		this.pk_dept_charge = pk_dept_charge;
	}

	public String getVapproveid() {
		return vapproveid;
	}

	public void setVapproveid(String vapproveid) {
		this.vapproveid = vapproveid;
	}

	public String getVapprovenote() {
		return vapprovenote;
	}

	public void setVapprovenote(String vapprovenote) {
		this.vapprovenote = vapprovenote;
	}

	public String getVbillname() {
		return vbillname;
	}

	public void setVbillname(String vbillname) {
		this.vbillname = vbillname;
	}

	public String getVbillno() {
		return vbillno;
	}

	public void setVbillno(String vbillno) {
		this.vbillno = vbillno;
	}

	public Integer getVbillstatus() {
		return vbillstatus;
	}

	public void setVbillstatus(Integer vbillstatus) {
		this.vbillstatus = vbillstatus;
	}

	public String getVmemo() {
		return vmemo;
	}

	public void setVmemo(String vmemo) {
		this.vmemo = vmemo;
	}

}
