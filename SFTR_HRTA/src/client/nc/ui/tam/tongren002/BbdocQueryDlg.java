/**
 * 
 */
package nc.ui.tam.tongren002;

import java.awt.Container;

import nc.ui.bd.ref.IRefQueryDlg;
import nc.ui.pub.ClientEnvironment;
import nc.ui.querytemplate.QueryConditionDLG;
import nc.ui.querytemplate.normalpanel.INormalQueryPanel;
import nc.vo.querytemplate.TemplateInfo;

/**
 * @author 28729
 *
 */
public class BbdocQueryDlg extends QueryConditionDLG implements IRefQueryDlg  {
	private static final long serialVersionUID = 1L;
	private String pk_corp = null;
	/**
	 * @param parent
	 */
	public BbdocQueryDlg(Container parent) {
		super(parent,getTemplateInfo());
		// TODO Auto-generated constructor stub
	}

	public String getConditionSql() {
		// TODO Auto-generated method stub
		String where = getWhereSQL();
		where = where == null || where.length() == 0 ? " 1 = 1 " : where;
		String condition = "(select pk_bclbid from tbm_bclb where " +where+")";
		return condition;
		
	}

	public void setPk_corp(String pk_corp) {
		// TODO Auto-generated method stub
		this.pk_corp = pk_corp;
		getTempInfo().setPk_Org(pk_corp);
		getTempInfo().setCurrentCorpPk(pk_corp);
	}
	
	protected static TemplateInfo getTemplateInfo() {
		TemplateInfo tempinfo = new TemplateInfo();
		tempinfo.setPk_Org(ClientEnvironment.getInstance().getCorporation().getPk_corp());
		tempinfo.setCurrentCorpPk(ClientEnvironment.getInstance().getCorporation().getPk_corp());
		tempinfo.setFunNode("6017010113");
		tempinfo.setUserid(ClientEnvironment.getInstance().getUser().getPrimaryKey());
		tempinfo.setBusiType(null);
		tempinfo.setNodekey(null);

		return tempinfo;
	}

}
