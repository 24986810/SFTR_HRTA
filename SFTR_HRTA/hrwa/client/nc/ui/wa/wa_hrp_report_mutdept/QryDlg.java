package nc.ui.wa.wa_hrp_report_mutdept;

import java.awt.Container;

import nc.ui.pub.beans.UIRefPane;
import nc.ui.querytemplate.CriteriaChangedEvent;
import nc.ui.querytemplate.normalpanel.INormalQueryPanel;
import nc.ui.querytemplate.value.RefValueObject;
import nc.ui.wa.wa_hrp_pub.QryDlg2;
import nc.vo.querytemplate.TemplateInfo;
/**
 * 
 * @author szh
 */
public class QryDlg extends QryDlg2{

	private RefValueObject beginPeriod = null;
	private RefValueObject dept = null;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public QryDlg(Container parent, INormalQueryPanel normalPnl, TemplateInfo ti) {
		super(parent, normalPnl, ti);

	}
	protected void initRef(CriteriaChangedEvent event){
		if("period".equals(event.getFieldCode())){
			if(beginPeriod==null){
				UIRefPane refPane = getRefPaneCurrEvent(event);
				refPane.setMultiSelectedEnabled(false);
				beginPeriod = new RefValueObject();
				beginPeriod.setName(getCE().getAccountPeriodVO().getPeriodyear()+"-"+getCE().getMonthVO().getMonth());
				beginPeriod.setPk(getCE().getMonthVO().getPrimaryKey());
				setRefValue(event,beginPeriod);
			}
		}else  if("pk_dept".equals(event.getFieldCode())){
				UIRefPane refPane = getRefPaneCurrEvent(event);
				refPane.getRefModel().addWherePart(" AND isnull( vdef5,'N'£©='Y' ");
//				refPane.setMultiSelectedEnabled(false);
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
