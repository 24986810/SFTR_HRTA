/**
 * 
 */
package nc.vo.tam.tongrenoa;

import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFDate;

/**
 * @author 28729
 *
 */
public class OaHoildayVO extends SuperVO {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String id;
	private String empnumber;
	private String xm;
	private UFDate ksrq;
	private String kssxw;
	private String jssxw;
	private UFDate jsrq;
	private String lx;
	private String bjsj;

	public String getBjsj() {
		return bjsj;
	}

	public void setBjsj(String bjsj) {
		this.bjsj = bjsj;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getEmpnumber() {
		return empnumber;
	}

	public void setEmpnumber(String empnumber) {
		this.empnumber = empnumber;
	}

	public String getXm() {
		return xm;
	}

	public void setXm(String xm) {
		this.xm = xm;
	}

	public UFDate getKsrq() {
		return ksrq;
	}

	public void setKsrq(UFDate ksrq) {
		this.ksrq = ksrq;
	}

	public String getKssxw() {
		return kssxw;
	}

	public void setKssxw(String kssxw) {
		this.kssxw = kssxw;
	}

	public String getJssxw() {
		return jssxw;
	}

	public void setJssxw(String jsxsw) {
		this.jssxw = jsxsw;
	}

	public UFDate getJsrq() {
		return jsrq;
	}

	public void setJsrq(UFDate jsrq) {
		this.jsrq = jsrq;
	}

	public String getLx() {
		return lx;
	}

	public void setLx(String lx) {
		this.lx = lx;
	}

	/**
	 * 
	 */
	public OaHoildayVO() {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see nc.vo.pub.SuperVO#getPKFieldName()
	 */
	@Override
	public String getPKFieldName() {
		// TODO Auto-generated method stub
		return "id";
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
		return "oa.ezoffice.hr";
	}

}
