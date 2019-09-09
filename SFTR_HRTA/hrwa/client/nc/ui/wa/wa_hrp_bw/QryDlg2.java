package nc.ui.wa.wa_hrp_bw;

import java.awt.Container;
import java.util.List;


import nc.ui.pub.ClientEnvironment;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.querytemplate.CriteriaChangedEvent;
import nc.ui.querytemplate.ICriteriaChangedListener;
import nc.ui.querytemplate.filter.IFilter;
import nc.ui.querytemplate.filtereditor.DefaultFilterEditor;
import nc.ui.querytemplate.normalpanel.INormalQueryPanel;
import nc.ui.querytemplate.value.DefaultFieldValue;
import nc.ui.querytemplate.value.DefaultFieldValueElement;
import nc.ui.querytemplate.value.IFieldValueElement;
import nc.ui.querytemplate.value.RefValueObject;
import nc.ui.querytemplate.valueeditor.DefaultFieldValueEditor;
import nc.ui.trade.query.HYQueryConditionDLG;
import nc.vo.querytemplate.TemplateInfo;
/**
 * 
 * @author liqiange
 *
 */
public class QryDlg2 extends HYQueryConditionDLG{

	private static final long serialVersionUID = 1L;

	public QryDlg2(Container parent, INormalQueryPanel normalPnl, TemplateInfo ti) {
		super(parent, normalPnl, ti);
		
		adapterInit();
		
	}
	
	protected void adapterInit(){
		
		registerCriteriaEditorListener(new ICriteriaChangedListener() {

			public void criteriaChanged(CriteriaChangedEvent event) {
				if (event.getEventtype() == CriteriaChangedEvent.FILTER_CHANGED) {
					afterEdit(event, CriteriaChangedEvent.FILTER_CHANGED);
				}
				if (event.getEventtype() == CriteriaChangedEvent.FILTEREDITOR_INITIALIZED) {
					initRef(event);
				}
				if (event.getEventtype() == CriteriaChangedEvent.FILTER_REMOVED) {
					
					afterEdit(event, CriteriaChangedEvent.FILTER_REMOVED);
				}
			}
		});
	}
	/**
	 * 
	 * @param event
	 * @return
	 */
	public UIRefPane getRefPaneCurrEvent(CriteriaChangedEvent event) {
		DefaultFilterEditor dfe = (DefaultFilterEditor) event.getFiltereditor();
		DefaultFieldValueEditor dfve = (DefaultFieldValueEditor) dfe
				.getFieldValueEditor();
		UIRefPane lur = (UIRefPane) dfve.getFieldValueElemEditor()
				.getFieldValueElemEditorComponent();
		return lur;
	}

	protected void setRefValue(CriteriaChangedEvent event,RefValueObject rvo){
	
		getRefPaneCurrEvent(event).setPK(rvo.getPk());
		
		List<IFilter> filter = event.getCriteriaEditor().getFiltersByFieldCode(event.getFieldCode());
		
		DefaultFieldValueElement df = new DefaultFieldValueElement(null,null,rvo);
		DefaultFieldValue dfv = new DefaultFieldValue();
		dfv.add(df);
		filter.get(0).setFieldValue(dfv);
	}
	protected RefValueObject getRefValue(CriteriaChangedEvent event){
        List<IFilter> filter = event.getCriteriaEditor().getFiltersByFieldCode(event.getFieldCode());
        DefaultFieldValue dfv = (DefaultFieldValue)filter.get(0).getFieldValue();
        List<IFieldValueElement> dfve = dfv.getFieldValues();
        return (RefValueObject)((DefaultFieldValueElement)dfve.get(0)).getValueObject();
	}
	protected void afterEdit(CriteriaChangedEvent event,Integer filter){
		
	}
	protected ClientEnvironment getCE(){
		ClientEnvironment ce = ClientEnvironment.getInstance();
		return ce;
	}
	protected void initRef(CriteriaChangedEvent event){
		
		
	}

	

}
