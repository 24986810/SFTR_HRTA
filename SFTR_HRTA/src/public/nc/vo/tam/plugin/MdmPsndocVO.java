package nc.vo.tam.plugin;

import nc.vo.pub.SuperVO;

public class MdmPsndocVO extends SuperVO {
	
	private String UserCode;
	private String OfficeMobile;
	
	

	public String getUserCode() {
		return UserCode;
	}

	public void setUserCode(String userCode) {
		UserCode = userCode;
	}

	



	public String getOfficeMobile() {
		return OfficeMobile;
	}

	public void setOfficeMobile(String officeMobile) {
		OfficeMobile = officeMobile;
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
		return "V_API_UserOfficePhone";
	}

}
