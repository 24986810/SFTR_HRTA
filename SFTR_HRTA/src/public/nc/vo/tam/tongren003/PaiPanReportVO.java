/**
 * 
 */
package nc.vo.tam.tongren003;

import java.util.HashMap;

import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDouble;

/**
 * @author 28729
 *
 */
public class PaiPanReportVO extends SuperVO {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private UFDate ddate;
	private UFBoolean biszb;
	private String vbillstatus;
	private String vbillstatus2;
	private Integer uploadnum;// 上传次数
	private String reason;
	
	
	
	public String getVbillstatus2() {
		return vbillstatus2;
	}
	public void setVbillstatus2(String vbillstatus2) {
		this.vbillstatus2 = vbillstatus2;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	public Integer getUploadnum() {
		return uploadnum;
	}
	public void setUploadnum(Integer uploadnum) {
		this.uploadnum = uploadnum;
	}
	public String getVbillstatus() {
		return vbillstatus;
	}
	public void setVbillstatus(String vbillstatus) {
		this.vbillstatus = vbillstatus;
	}
	public String getMemo() {
		return memo;
	}
	public void setMemo(String memo) {
		this.memo = memo;
	}
	private String memo;
	public UFBoolean getBiszb() {
		return biszb;
	}
	public void setBiszb(UFBoolean biszb) {
		this.biszb = biszb;
	}
    public String getVperiod() {
		return vperiod;
	}
	public void setVperiod(String vperiod) {
		this.vperiod = vperiod;
	}
	public UFDouble getNnum() {
		return nnum;
	}
	public void setNnum(UFDouble nnum) {
		this.nnum = nnum;
	}
	public Integer getNperiodnum() {
		return nperiodnum;
	}
	public void setNperiodnum(Integer nperiodnum) {
		this.nperiodnum = nperiodnum;
	}
	private String vperiod;
    private UFDouble nnum;
    private Integer nperiodnum;
	private String pk_bb;
	private String pk_bclbid;
	public String getPk_bclbid() {
		return pk_bclbid;
	}
	public void setPk_bclbid(String pk_bclbid) {
		this.pk_bclbid = pk_bclbid;
	}
	private String pk_psndoc;
	private String pk_dept;
	private String pk_dd;
	private String pk_bbz;
	private String telephone;
	private UFBoolean bisbeiban;
	public UFBoolean getBisbeiban() {
		return bisbeiban;
	}
	public void setBisbeiban(UFBoolean bisbeiban) {
		this.bisbeiban = bisbeiban;
	}
	public String getTelephone() {
		return telephone;
	}
	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}
	private UFBoolean bispaiban = new UFBoolean(false);
	public UFBoolean getBispaiban() {
		return bispaiban;
	}
	public void setBispaiban(UFBoolean bispaiban) {
		this.bispaiban = bispaiban;
	}
	private HashMap<String,String> map_names = new HashMap<String, String>();
	private HashMap<String,String> map_pks = new HashMap<String, String>();
	/**
	 * 
	 */
	public PaiPanReportVO() {
		// TODO Auto-generated constructor stub
	}
	@Override
	public void setAttributeValue(String attributeName, Object value) {
		// TODO Auto-generated method stub
		if(attributeName.startsWith("pk_bb")&&!attributeName.equals("pk_bb")&&!attributeName.equals("pk_bbz")){
			map_pks.put(attributeName, value!=null?value.toString():null);
		}else if(attributeName.startsWith("vbbnames")){
			map_names.put(attributeName, value!=null?value.toString():null);
		}
	}
	@Override
	public Object getAttributeValue(String attributeName) {
		// TODO Auto-generated method stub
		if(attributeName.startsWith("pk_bb")&&!attributeName.equals("pk_bb")&&!attributeName.equals("pk_bbz")){
			return map_pks.get(attributeName);
		}else if(attributeName.startsWith("vbbnames")){
			return map_names.get(attributeName);
		}else{
			return super.getAttributeValue(attributeName);
		}
	}
	public UFDate getDdate() {
		return ddate;
	}
	public void setDdate(UFDate ddate) {
		this.ddate = ddate;
	}
	public String getPk_bb() {
		return pk_bb;
	}
	public void setPk_bb(String pk_bb) {
		this.pk_bb = pk_bb;
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
	public String getPk_dd() {
		return pk_dd;
	}
	public void setPk_dd(String pk_dd) {
		this.pk_dd = pk_dd;
	}
	public String getPk_bbz() {
		return pk_bbz;
	}
	public void setPk_bbz(String pk_bbz) {
		this.pk_bbz = pk_bbz;
	}
	public HashMap<String, String> getMap_names() {
		return map_names;
	}
	public void setMap_names(HashMap<String, String> map_names) {
		this.map_names = map_names;
	}
	public HashMap<String, String> getMap_pks() {
		return map_pks;
	}
	public void setMap_pks(HashMap<String, String> map_pks) {
		this.map_pks = map_pks;
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
		return null;
	}

}
