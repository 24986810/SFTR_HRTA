/**
 * 
 */
package nc.vo.hrwa.troo;

import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFDouble;

/**
 * @author 28729
 *
 */
public class DeptBonusSummariesVO extends SuperVO {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String year;
	private String month;
	private String deptcode;
	private String deptname;
	private String type;
	private UFDouble amount;
	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	public String getDeptcode() {
		return deptcode;
	}

	public void setDeptcode(String deptcode) {
		this.deptcode = deptcode;
	}

	public String getDeptname() {
		return deptname;
	}

	public void setDeptname(String deptname) {
		this.deptname = deptname;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public UFDouble getAmount() {
		return amount;
	}

	public void setAmount(UFDouble amount) {
		this.amount = amount;
	}

	/**
	 * 
	 */
	public DeptBonusSummariesVO() {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see nc.vo.pub.SuperVO#getPKFieldName()
	 */
	@Override
	public String getPKFieldName() {
		// TODO Auto-generated method stub
		return null;
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
		return "V_YongYouDeptBonusSummaries";
	}

}
