package nc.vo.wa.wa_hrpreport_tr_001;

import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFDouble;

public class ReportBodyVO extends SuperVO {

	private String pk_report_b;
	private String pk_deptdoc;
	private UFDouble nmny1;
	private UFDouble nmny2;
	private String vapprovenote;
	private String remark;
	
	
	
	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public UFDouble getNmny1() {
		return nmny1;
	}

	public void setNmny1(UFDouble nmny1) {
		this.nmny1 = nmny1;
	}

	public UFDouble getNmny2() {
		return nmny2;
	}

	public void setNmny2(UFDouble nmny2) {
		this.nmny2 = nmny2;
	}

	public String getPk_deptdoc() {
		return pk_deptdoc;
	}

	public void setPk_deptdoc(String pk_deptdoc) {
		this.pk_deptdoc = pk_deptdoc;
	}

	public String getPk_report_b() {
		return pk_report_b;
	}

	public void setPk_report_b(String pk_report_b) {
		this.pk_report_b = pk_report_b;
	}

	public String getVapprovenote() {
		return vapprovenote;
	}

	public void setVapprovenote(String vapprovenote) {
		this.vapprovenote = vapprovenote;
	}

	@Override
	public String getPKFieldName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getParentPKFieldName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getTableName() {
		// TODO Auto-generated method stub
		return null;
	}

}
