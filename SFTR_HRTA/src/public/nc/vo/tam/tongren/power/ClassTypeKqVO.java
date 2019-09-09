package nc.vo.tam.tongren.power;

import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDateTime;

/**
 * @author 28729
 *
 */
public class ClassTypeKqVO extends SuperVO {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String vcode;
	private String memo;
	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	private String vname;
	private String pk_hrp_classtype;
	private String vdef1;
	private String vdef2;
	private String vdef3;
	private String vdef4;
	private String vdef5;
	private String pk_corp;
	private UFDateTime ts;
	private Integer dr;

	

	public String getPk_hrp_classtype() {
		return pk_hrp_classtype;
	}

	public void setPk_hrp_classtype(String pk_hrp_classtype) {
		this.pk_hrp_classtype = pk_hrp_classtype;
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

	/**
	 * 
	 */
	 public ClassTypeKqVO() {
		 // TODO Auto-generated constructor stub
	 }

	 /* (non-Javadoc)
	  * @see nc.vo.pub.SuperVO#getPKFieldName()
	  */
	 @Override
	 public String getPKFieldName() {
		 // TODO Auto-generated method stub
		 return "pk_hrp_classtype";
	 }

	 /* (non-Javadoc)
	  * @see nc.vo.pub.SuperVO#getParentPKFieldName()
	  */
	 @Override
	 public String getParentPKFieldName() {
		 // TODO Auto-generated method stub
		 return "";
	 }

	 /* (non-Javadoc)
	  * @see nc.vo.pub.SuperVO#getTableName()
	  */
	 @Override
	 public String getTableName() {
		 // TODO Auto-generated method stub
		 return "trtam_classtype_kq";
	 }

	 public String getVcode() {
		 return vcode;
	 }

	 public void setVcode(String vcode) {
		 this.vcode = vcode;
	 }

	 public String getVname() {
		 return vname;
	 }

	 public void setVname(String vname) {
		 this.vname = vname;
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

}
