package nc.vo.wa.wa_hrp_report_mutdept;

import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFDouble;

public class ReportBodyVO extends SuperVO {

	private String pk_report_b;
	private String pk_deptdoc;
	private UFDouble nmny1;
	private UFDouble nmny2;
	private String pk_wa_period;
	private String pk_psndoc;
	private String vperiod;
	
	
	

	public String getVperiod() {
		return vperiod;
	}

	public void setVperiod(String vperiod) {
		this.vperiod = vperiod;
	}

	public String getPk_psndoc() {
		return pk_psndoc;
	}

	public void setPk_psndoc(String pk_psndoc) {
		this.pk_psndoc = pk_psndoc;
	}

	public String getPk_wa_period() {
		return pk_wa_period;
	}

	public void setPk_wa_period(String pk_wa_period) {
		this.pk_wa_period = pk_wa_period;
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
