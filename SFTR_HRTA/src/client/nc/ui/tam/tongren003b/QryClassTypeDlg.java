package nc.ui.tam.tongren003b;

import java.awt.Container;

import nc.ui.pub.ClientEnvironment;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.querytemplate.CriteriaChangedEvent;
import nc.ui.querytemplate.normalpanel.INormalQueryPanel;
import nc.ui.querytemplate.value.RefValueObject;
import nc.ui.tam.tongren003.QryDlg2;
import nc.vo.querytemplate.TemplateInfo;
/**
 * 
 * @author szh
 */
public class QryClassTypeDlg extends QryDlg2{

	private RefValueObject beginPeriod = null;
	private RefValueObject endPeriod = null;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public QryClassTypeDlg(Container parent, INormalQueryPanel normalPnl, TemplateInfo ti) {
		super(parent, normalPnl, ti);

	}
	protected void initRef(CriteriaChangedEvent event){
		if("pk_bb".equals(event.getFieldCode())){
			UIRefPane refPane = getRefPaneCurrEvent(event);
			refPane.setMultiSelectedEnabled(true);
			refPane.setTreeGridNodeMultiSelected(true);
		}else  if("pk_dept".equals(event.getFieldCode())){
			UIRefPane refPane = getRefPaneCurrEvent(event);
			String pk_corp = ClientEnvironment.getInstance().getCorporation().getPrimaryKey();
			String operatorid = ClientEnvironment.getInstance().getUser().getPrimaryKey();
			String wheredept = 	" pk_corp='" + pk_corp+ "' and isnull(bisseal,'N')='N' and pk_dept in (select pk_docid from bd_tr_userclasspower where isnull(dr,0)=0 and pk_user='"+operatorid+"' and powertype=0 and  pk_corp='"+pk_corp+"') ";
			refPane.setWhereString(wheredept);
		}else  if("pk_dd".equals(event.getFieldCode())){
		}else  if("pk_psndoc".equals(event.getFieldCode())){
			UIRefPane refPane = getRefPaneCurrEvent(event);
			refPane.setMultiSelectedEnabled(true);
			refPane.setTreeGridNodeMultiSelected(true);
		}else if("pk_period".equals(event.getFieldCode())){
			if(beginPeriod==null){
				UIRefPane refPane = getRefPaneCurrEvent(event);
				refPane.setMultiSelectedEnabled(false);
				beginPeriod = new RefValueObject();
				beginPeriod.setName(getCE().getAccountPeriodVO().getPeriodyear()+"-"+getCE().getMonthVO().getMonth());
				beginPeriod.setPk(getCE().getMonthVO().getPrimaryKey());
				setRefValue(event,beginPeriod);
			}
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
