/**
 * 
 */
package nc.vo.tam.tongren003;

import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFDouble;

/**
 * @author 28729
 *
 */
public class PaibanWeekVO extends SuperVO {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String pk_paiban;
	private String vdef1;
	private String vdef2;
	private String vdef3;
	private String vdef4;
	private String vdef5;
	private String vdate;
	private String pptype; // 排班类型，1为补班
	private Integer nnowdays;
	private UFDouble ngxs;//	ngxs
	private UFDouble	nbcgxs;
	private UFDouble nsygxs;//	公休数
	private String voperatorid;
	private String vbillstatus;
	private String vbillstatus1;
	private String vbillstatus2;
	private String vbillstatus3;
	private String pk_deptzb;
	private String pk_temp;
	private Integer uploadnum;// 上传次数
	private String classtype;// 班种,1为值班,2月周排班
	
	
	
	public String getClasstype() {
		return classtype;
	}

	public void setClasstype(String classtype) {
		this.classtype = classtype;
	}

	public Integer getUploadnum() {
		return uploadnum;
	}

	public void setUploadnum(Integer uploadnum) {
		this.uploadnum = uploadnum;
	}

	public String getPk_temp() {
		return pk_temp;
	}

	public void setPk_temp(String pk_temp) {
		this.pk_temp = pk_temp;
	}

	public String getPk_deptzb() {
		return pk_deptzb;
	}

	public void setPk_deptzb(String pk_deptzb) {
		this.pk_deptzb = pk_deptzb;
	}

	public String getVbillstatus3() {
		return vbillstatus3;
	}

	public void setVbillstatus3(String vbillstatus3) {
		this.vbillstatus3 = vbillstatus3;
	}

	public String getVbillstatus() {
		return vbillstatus;
	}

	public void setVbillstatus(String vbillstatus) {
		this.vbillstatus = vbillstatus;
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

	public String getPptype() {
		return pptype;
	}

	public void setPptype(String pptype) {
		this.pptype = pptype;
	}

	public String getVoperatorid() {
		return voperatorid;
	}

	public void setVoperatorid(String voperatorid) {
		this.voperatorid = voperatorid;
	}

	public UFDouble getNgxs() {
		return ngxs;
	}

	public void setNgxs(UFDouble ngxs) {
		this.ngxs = ngxs;
	}

	public UFDouble getNbcgxs() {
		return nbcgxs;
	}

	public void setNbcgxs(UFDouble nbcgxs) {
		this.nbcgxs = nbcgxs;
	}

	public UFDouble getNsygxs() {
		return nsygxs;
	}

	public void setNsygxs(UFDouble nsygxs) {
		this.nsygxs = nsygxs;
	}

	public Integer getNnowdays() {
		return nnowdays;
	}

	public void setNnowdays(Integer nnowdays) {
		this.nnowdays = nnowdays;
	}

	public String getVdate() {
		return vdate;
	}

	public void setVdate(String vdate) {
		this.vdate = vdate;
	}

	private String pk_corp;
	private UFDateTime ts;
	private Integer dr;
	public String getPk_paiban() {
		return pk_paiban;
	}

	public void setPk_paiban(String pk_paiban) {
		this.pk_paiban = pk_paiban;
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

	public Integer getDr() {
		return dr;
	}

	public void setDr(Integer dr) {
		this.dr = dr;
	}

	public String getPk_psndoc() {
		return pk_psndoc;
	}

	public void setPk_psndoc(String pk_psndoc) {
		this.pk_psndoc = pk_psndoc;
	}

	public String getPk_dept() {
		return pk_dept;
	}

	public void setPk_dept(String pk_dept) {
		this.pk_dept = pk_dept;
	}

	public String getVbbname1() {
		return vbbname1;
	}

	public void setVbbname1(String vbbname1) {
		this.vbbname1 = vbbname1;
	}

	public String getVbbname2() {
		return vbbname2;
	}

	public void setVbbname2(String vbbname2) {
		this.vbbname2 = vbbname2;
	}

	public String getVbbname3() {
		return vbbname3;
	}

	public void setVbbname3(String vbbname3) {
		this.vbbname3 = vbbname3;
	}

	public String getVbbname4() {
		return vbbname4;
	}

	public void setVbbname4(String vbbname4) {
		this.vbbname4 = vbbname4;
	}

	public String getVbbname5() {
		return vbbname5;
	}

	public void setVbbname5(String vbbname5) {
		this.vbbname5 = vbbname5;
	}

	public String getVbbname6() {
		return vbbname6;
	}

	public void setVbbname6(String vbbname6) {
		this.vbbname6 = vbbname6;
	}

	public String getVbbname7() {
		return vbbname7;
	}

	public void setVbbname7(String vbbname7) {
		this.vbbname7 = vbbname7;
	}

	public String getPk_bb1() {
		return pk_bb1;
	}

	public void setPk_bb1(String pk_bb1) {
		this.pk_bb1 = pk_bb1;
	}

	public String getPk_bb2() {
		return pk_bb2;
	}

	public void setPk_bb2(String pk_bb2) {
		this.pk_bb2 = pk_bb2;
	}

	public String getPk_bb3() {
		return pk_bb3;
	}

	public void setPk_bb3(String pk_bb3) {
		this.pk_bb3 = pk_bb3;
	}

	public String getPk_bb4() {
		return pk_bb4;
	}

	public void setPk_bb4(String pk_bb4) {
		this.pk_bb4 = pk_bb4;
	}

	public String getPk_bb5() {
		return pk_bb5;
	}

	public void setPk_bb5(String pk_bb5) {
		this.pk_bb5 = pk_bb5;
	}

	public String getPk_bb6() {
		return pk_bb6;
	}

	public void setPk_bb6(String pk_bb6) {
		this.pk_bb6 = pk_bb6;
	}

	public String getPk_bb7() {
		return pk_bb7;
	}

	public void setPk_bb7(String pk_bb7) {
		this.pk_bb7 = pk_bb7;
	}

	private String pk_psndoc;
	private String pk_dept;
	private String vbbname1;
	private String vbbname2;
	private String vbbname3;
	private String vbbname4;
	private String vbbname5;
	private String vbbname6;
	private String vbbname7;
	private String vbbname8;
	private String vbbname9;
	private String vbbname10;
	private String vbbname11;
	private String vbbname12;
	private String vbbname13;
	private String vbbname14;
	private String vbbname15;
	private String vbbname16;
	private String vbbname17;
	private String vbbname18;
	private String vbbname19;
	private String vbbname20;
	private String vbbname21;
	private String vbbname22;
	private String vbbname23;
	private String vbbname24;
	private String vbbname25;
	private String vbbname26;
	private String vbbname27;
	private String vbbname28;
	private String vbbname29;
	private String vbbname30;
	private String vbbname31;
	private String pk_bb1;
	private String pk_bb2;
	private String pk_bb3;
	private String pk_bb4;
	private String pk_bb5;
	private String pk_bb6;
	private String pk_bb7;
	private String pk_bb8;
	private String pk_bb9;
	private String pk_bb10;
	private String pk_bb11;
	private String pk_bb12;
	private String pk_bb13;
	private String pk_bb14;
	private String pk_bb15;
	private String pk_bb16;
	private String pk_bb17;
	private String pk_bb18;
	private String pk_bb19;
	private String pk_bb20;
	private String pk_bb21;
	private String pk_bb22;
	private String pk_bb23;
	private String pk_bb24;
	private String pk_bb25;
	private String pk_bb26;
	private String pk_bb27;
	private String pk_bb28;
	private String pk_bb29;
	private String pk_bb30;
	private String pk_bb31;
	
	private String memo;
	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public String getPk_bb10() {
		return pk_bb10;
	}

	public void setPk_bb10(String pk_bb10) {
		this.pk_bb10 = pk_bb10;
	}

	public String getPk_bb11() {
		return pk_bb11;
	}

	public void setPk_bb11(String pk_bb11) {
		this.pk_bb11 = pk_bb11;
	}

	public String getPk_bb12() {
		return pk_bb12;
	}

	public void setPk_bb12(String pk_bb12) {
		this.pk_bb12 = pk_bb12;
	}

	public String getPk_bb13() {
		return pk_bb13;
	}

	public void setPk_bb13(String pk_bb13) {
		this.pk_bb13 = pk_bb13;
	}

	public String getPk_bb14() {
		return pk_bb14;
	}

	public void setPk_bb14(String pk_bb14) {
		this.pk_bb14 = pk_bb14;
	}

	public String getPk_bb15() {
		return pk_bb15;
	}

	public void setPk_bb15(String pk_bb15) {
		this.pk_bb15 = pk_bb15;
	}

	public String getPk_bb16() {
		return pk_bb16;
	}

	public void setPk_bb16(String pk_bb16) {
		this.pk_bb16 = pk_bb16;
	}

	public String getPk_bb17() {
		return pk_bb17;
	}

	public void setPk_bb17(String pk_bb17) {
		this.pk_bb17 = pk_bb17;
	}

	public String getPk_bb18() {
		return pk_bb18;
	}

	public void setPk_bb18(String pk_bb18) {
		this.pk_bb18 = pk_bb18;
	}

	public String getPk_bb19() {
		return pk_bb19;
	}

	public void setPk_bb19(String pk_bb19) {
		this.pk_bb19 = pk_bb19;
	}

	public String getPk_bb20() {
		return pk_bb20;
	}

	public void setPk_bb20(String pk_bb20) {
		this.pk_bb20 = pk_bb20;
	}

	public String getPk_bb21() {
		return pk_bb21;
	}

	public void setPk_bb21(String pk_bb21) {
		this.pk_bb21 = pk_bb21;
	}

	public String getPk_bb22() {
		return pk_bb22;
	}

	public void setPk_bb22(String pk_bb22) {
		this.pk_bb22 = pk_bb22;
	}

	public String getPk_bb23() {
		return pk_bb23;
	}

	public void setPk_bb23(String pk_bb23) {
		this.pk_bb23 = pk_bb23;
	}

	public String getPk_bb24() {
		return pk_bb24;
	}

	public void setPk_bb24(String pk_bb24) {
		this.pk_bb24 = pk_bb24;
	}

	public String getPk_bb25() {
		return pk_bb25;
	}

	public void setPk_bb25(String pk_bb25) {
		this.pk_bb25 = pk_bb25;
	}

	public String getPk_bb26() {
		return pk_bb26;
	}

	public void setPk_bb26(String pk_bb26) {
		this.pk_bb26 = pk_bb26;
	}

	public String getPk_bb27() {
		return pk_bb27;
	}

	public void setPk_bb27(String pk_bb27) {
		this.pk_bb27 = pk_bb27;
	}

	public String getPk_bb28() {
		return pk_bb28;
	}

	public void setPk_bb28(String pk_bb28) {
		this.pk_bb28 = pk_bb28;
	}

	public String getPk_bb29() {
		return pk_bb29;
	}

	public void setPk_bb29(String pk_bb29) {
		this.pk_bb29 = pk_bb29;
	}

	public String getPk_bb30() {
		return pk_bb30;
	}

	public void setPk_bb30(String pk_bb30) {
		this.pk_bb30 = pk_bb30;
	}

	public String getPk_bb31() {
		return pk_bb31;
	}

	public void setPk_bb31(String pk_bb31) {
		this.pk_bb31 = pk_bb31;
	}

	public String getPk_bb8() {
		return pk_bb8;
	}

	public void setPk_bb8(String pk_bb8) {
		this.pk_bb8 = pk_bb8;
	}

	public String getPk_bb9() {
		return pk_bb9;
	}

	public void setPk_bb9(String pk_bb9) {
		this.pk_bb9 = pk_bb9;
	}

	public String getVbbname10() {
		return vbbname10;
	}

	public void setVbbname10(String vbbname10) {
		this.vbbname10 = vbbname10;
	}

	public String getVbbname11() {
		return vbbname11;
	}

	public void setVbbname11(String vbbname11) {
		this.vbbname11 = vbbname11;
	}

	public String getVbbname12() {
		return vbbname12;
	}

	public void setVbbname12(String vbbname12) {
		this.vbbname12 = vbbname12;
	}

	public String getVbbname13() {
		return vbbname13;
	}

	public void setVbbname13(String vbbname13) {
		this.vbbname13 = vbbname13;
	}

	public String getVbbname14() {
		return vbbname14;
	}

	public void setVbbname14(String vbbname14) {
		this.vbbname14 = vbbname14;
	}

	public String getVbbname15() {
		return vbbname15;
	}

	public void setVbbname15(String vbbname15) {
		this.vbbname15 = vbbname15;
	}

	public String getVbbname16() {
		return vbbname16;
	}

	public void setVbbname16(String vbbname16) {
		this.vbbname16 = vbbname16;
	}

	public String getVbbname17() {
		return vbbname17;
	}

	public void setVbbname17(String vbbname17) {
		this.vbbname17 = vbbname17;
	}

	public String getVbbname18() {
		return vbbname18;
	}

	public void setVbbname18(String vbbname18) {
		this.vbbname18 = vbbname18;
	}

	public String getVbbname19() {
		return vbbname19;
	}

	public void setVbbname19(String vbbname19) {
		this.vbbname19 = vbbname19;
	}

	public String getVbbname20() {
		return vbbname20;
	}

	public void setVbbname20(String vbbname20) {
		this.vbbname20 = vbbname20;
	}

	public String getVbbname21() {
		return vbbname21;
	}

	public void setVbbname21(String vbbname21) {
		this.vbbname21 = vbbname21;
	}

	public String getVbbname22() {
		return vbbname22;
	}

	public void setVbbname22(String vbbname22) {
		this.vbbname22 = vbbname22;
	}

	public String getVbbname23() {
		return vbbname23;
	}

	public void setVbbname23(String vbbname23) {
		this.vbbname23 = vbbname23;
	}

	public String getVbbname24() {
		return vbbname24;
	}

	public void setVbbname24(String vbbname24) {
		this.vbbname24 = vbbname24;
	}

	public String getVbbname25() {
		return vbbname25;
	}

	public void setVbbname25(String vbbname25) {
		this.vbbname25 = vbbname25;
	}

	public String getVbbname26() {
		return vbbname26;
	}

	public void setVbbname26(String vbbname26) {
		this.vbbname26 = vbbname26;
	}

	public String getVbbname27() {
		return vbbname27;
	}

	public void setVbbname27(String vbbname27) {
		this.vbbname27 = vbbname27;
	}

	public String getVbbname28() {
		return vbbname28;
	}

	public void setVbbname28(String vbbname28) {
		this.vbbname28 = vbbname28;
	}

	public String getVbbname29() {
		return vbbname29;
	}

	public void setVbbname29(String vbbname29) {
		this.vbbname29 = vbbname29;
	}

	public String getVbbname30() {
		return vbbname30;
	}

	public void setVbbname30(String vbbname30) {
		this.vbbname30 = vbbname30;
	}

	public String getVbbname31() {
		return vbbname31;
	}

	public void setVbbname31(String vbbname31) {
		this.vbbname31 = vbbname31;
	}

	public String getVbbname8() {
		return vbbname8;
	}

	public void setVbbname8(String vbbname8) {
		this.vbbname8 = vbbname8;
	}

	public String getVbbname9() {
		return vbbname9;
	}

	public void setVbbname9(String vbbname9) {
		this.vbbname9 = vbbname9;
	}

	/**
	 * 
	 */
	 public PaibanWeekVO() {
		 // TODO Auto-generated constructor stub
	 }

	 /* (non-Javadoc)
	  * @see nc.vo.pub.SuperVO#getPKFieldName()
	  */
	 @Override
	 public String getPKFieldName() {
		 // TODO Auto-generated method stub
		 return "pk_paiban";
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
		 return "trtam_paiban";
	 }

}
