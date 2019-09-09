package nc.ui.tam.tongren020;

import java.awt.Container;

import nc.ui.pub.ClientEnvironment;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.querytemplate.CriteriaChangedEvent;
import nc.ui.querytemplate.normalpanel.INormalQueryPanel;
import nc.ui.querytemplate.value.RefValueObject;
import nc.vo.querytemplate.TemplateInfo;
/**
 * 
 * @author szh
 */
public class QryDlg extends QryDlg2{

	private RefValueObject beginPeriod = null;
	private RefValueObject endPeriod = null;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public QryDlg(Container parent, INormalQueryPanel normalPnl, TemplateInfo ti) {
		super(parent, normalPnl, ti);

	}
	protected void initRef(CriteriaChangedEvent event){
		if("pk_bb".equals(event.getFieldCode())){
			UIRefPane refPane = getRefPaneCurrEvent(event);
			refPane.setMultiSelectedEnabled(true);
			refPane.setTreeGridNodeMultiSelected(true);
		}else  if("pk_dept".equals(event.getFieldCode())){
			UIRefPane refPane = getRefPaneCurrEvent(event);
			String wheredept = 	" pk_corp='" + ClientEnvironment.getInstance().getCorporation().getPrimaryKey()+ "' and isnull(bisseal,'N')='N' and pk_dept in (select pk_docid from bd_tr_userpower where isnull(dr,0)=0 and pk_user='"+ClientEnvironment.getInstance().getUser().getPrimaryKey()+"' and powertype=0 and  pk_corp='"+ClientEnvironment.getInstance().getCorporation().getPrimaryKey()+"') ";
			refPane.setWhereString(wheredept);
		}else  if("pk_dd".equals(event.getFieldCode())){
		}else  if("pk_psndoc".equals(event.getFieldCode())){
			UIRefPane refPane = getRefPaneCurrEvent(event);
			refPane.setMultiSelectedEnabled(true);
			refPane.setTreeGridNodeMultiSelected(true);
		}
	}
	@Override
	protected void onBtnOK() {
		// TODO Auto-generated method stub
		super.onBtnOK();
	}
	@Override
	public String checkCondition() {
		// TODO Auto-generated method stub
		return super.checkCondition();
	}
	@Override
	protected void afterEdit(CriteriaChangedEvent event, Integer filter) {

		super.afterEdit(event, filter);
	}



}
