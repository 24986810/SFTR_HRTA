package nc.ui.tam.tongren011;

import nc.itf.hrp.pub.IHRPSysParam;
import nc.ui.bd.ref.AbstractRefModel;
import nc.ui.pub.ClientEnvironment;

/**
 * ֵ��ģ�����
 * 
 * @author zhanghua
 * @data 2018-01-23
 */
public class ZbTempRef extends AbstractRefModel {
	public ZbTempRef() {

	}

	@Override
	public String getRefTitle() {
		return "ֵ��ģ��";
	}

	@Override
	public int getDefaultFieldCount() {
		return 2;
	}

	@Override
	public String[] getFieldCode() {

		return new String[] { "classcode", "deptname", "pk_temp",};
	}

	@Override
	public String[] getFieldName() {

		return new String[] { "���", "����","����",};
	}

	@Override
	public String getRefCodeField() {
		return "classcode";
	}

	@Override
	public String getRefNameField() {
		return "deptname";
	}

	@Override
	public String getPkFieldCode() {

		return "pk_temp";
	}

	@Override
	public String[] getHiddenFieldCode() {
		return new String[] { "pk_temp" };
	}

	@Override
	public String getTableName() {

		return " (select a.pk_paiban pk_temp, b.lbmc classcode,a.deptnamekq deptname,a.dr,a.bisstop,b.pk_bclbid"
			+"  from trtam_zhiban_temp a"
			+"  left join tbm_bclb b"
			+"    on a.pk_bb = b.pk_bclbid ) ";
	}

	@Override
	public String getWherePart() {
//		return " isnull(dr,0)=0 ";
		String corp = ClientEnvironment.getInstance().getCorporation()
				.getPrimaryKey();
		String condition = " isnull(dr,0)=0 and isnull(bisstop,'N')='N'";
		if (super.getWherePart() == null) {
			return condition;
		} else {
			return super.getWherePart();
		}
	}

}
