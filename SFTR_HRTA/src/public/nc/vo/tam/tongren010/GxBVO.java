/**
 * 
 */
package nc.vo.tam.tongren010;

import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFDouble;

/**
 * @author 28729
 *
 */
public class GxBVO extends SuperVO {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String pk_corp;
	private String vyear;
	private String pk_gx_h;
	private UFDateTime ts;
	private Integer dr;
	private UFDouble nxiuzheng;
	private UFDouble ngl;
	private String vreson;
	private UFDouble nyfgx;
	private UFDouble nkcgx;
	private UFDouble nsfgx;
	private UFDouble nyygx;
	private UFDouble nsxgx;
	private UFDouble nlastsygx;// 上一期公休剩余
	private UFDouble nlastyygx;
	private String 	vmemo;	
	private String pk_psndoc;
	private String 	pk_gx_b;
	private String vdef1;
	private String vdef2;
	private String vdef3;
	private String vdef4;
	private String vdef5;
	/**
	 * 
	 */
	public GxBVO() {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see nc.vo.pub.SuperVO#getPKFieldName()
	 */
	@Override
	public String getPKFieldName() {
		// TODO Auto-generated method stub
		return "pk_gx_b";
	}

	/* (non-Javadoc)
	 * @see nc.vo.pub.SuperVO#getParentPKFieldName()
	 */
	@Override
	public String getParentPKFieldName() {
		// TODO Auto-generated method stub
		return "pk_gx_h";
	}

	/* (non-Javadoc)
	 * @see nc.vo.pub.SuperVO#getTableName()
	 */
	@Override
	public String getTableName() {
		// TODO Auto-generated method stub
		return "tam_gx_b";
	}
	
	

	public UFDouble getNlastyygx() {
		return nlastyygx;
	}

	public void setNlastyygx(UFDouble nlastyygx) {
		this.nlastyygx = nlastyygx;
	}

	public UFDouble getNlastsygx() {
		return nlastsygx;
	}

	public void setNlastsygx(UFDouble nlastsygx) {
		this.nlastsygx = nlastsygx;
	}

	public String getPk_corp() {
		return pk_corp;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}

	public String getVyear() {
		return vyear;
	}

	public void setVyear(String vyear) {
		this.vyear = vyear;
	}

	public String getPk_gx_h() {
		return pk_gx_h;
	}

	public void setPk_gx_h(String pk_gx_h) {
		this.pk_gx_h = pk_gx_h;
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

	public UFDouble getNxiuzheng() {
		return nxiuzheng;
	}

	public void setNxiuzheng(UFDouble nxiuzheng) {
		this.nxiuzheng = nxiuzheng;
	}

	public UFDouble getNgl() {
		return ngl;
	}

	public void setNgl(UFDouble ngl) {
		this.ngl = ngl;
	}

	public String getVreson() {
		return vreson;
	}

	public void setVreson(String vreson) {
		this.vreson = vreson;
	}

	public UFDouble getNyfgx() {
		return nyfgx;
	}

	public void setNyfgx(UFDouble nyfgx) {
		this.nyfgx = nyfgx;
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

	public UFDouble getNyygx() {
		return nyygx;
	}

	public void setNyygx(UFDouble nyygx) {
		this.nyygx = nyygx;
	}

	public UFDouble getNsxgx() {
		return nsxgx;
	}

	public void setNsxgx(UFDouble nsxgx) {
		this.nsxgx = nsxgx;
	}

	public String getVmemo() {
		return vmemo;
	}

	public void setVmemo(String vmemo) {
		this.vmemo = vmemo;
	}

	public String getPk_psndoc() {
		return pk_psndoc;
	}

	public void setPk_psndoc(String pk_psndoc) {
		this.pk_psndoc = pk_psndoc;
	}

	public String getPk_gx_b() {
		return pk_gx_b;
	}

	public void setPk_gx_b(String pk_gx_b) {
		this.pk_gx_b = pk_gx_b;
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
