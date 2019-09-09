package nc.ui.wa.wa_031;

import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFDouble;

public class WuXianYiJinVO extends SuperVO{

	private UFDouble GJJ;
	
	private UFDouble SYJ;
	
	private UFDouble YBJ;
	
	private UFDouble YLJ;
	
	private UFDouble ZYNJ;

	public UFDouble getGJJ() {
		return GJJ;
	}

	public void setGJJ(nc.vo.pub.lang.UFDouble double1) {
		GJJ = double1;
	}

	public UFDouble getSYJ() {
		return SYJ;
	}

	public void setSYJ(UFDouble syj) {
		SYJ = syj;
	}

	public UFDouble getYBJ() {
		return YBJ;
	}

	public void setYBJ(UFDouble ybj) {
		YBJ = ybj;
	}

	public UFDouble getYLJ() {
		return YLJ;
	}

	public void setYLJ(UFDouble ylj) {
		YLJ = ylj;
	}

	public UFDouble getZYNJ() {
		return ZYNJ;
	}

	public void setZYNJ(UFDouble zynj) {
		ZYNJ = zynj;
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
