package nc.ui.tam.tongren019;


import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.swing.text.StyleConstants.ColorConstants;
import javax.swing.tree.TreeNode;

import nc.bs.framework.common.NCLocator;
import nc.itf.hr.ta.IBclbDefining;
import nc.itf.hrp.pub.HRPPubTool;
import nc.itf.uap.IUAPQueryBS;
import nc.jdbc.framework.processor.BeanListProcessor;
import nc.ui.pub.ClientEnvironment;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UIDialog;
import nc.ui.pub.bill.BillItem;
import nc.ui.querytemplate.filter.DefaultFilter;
import nc.ui.querytemplate.querytree.QueryTree;
import nc.ui.querytemplate.querytree.QueryTree.FilterNode;
import nc.ui.querytemplate.querytree.QueryTree.QueryTreeNode;
import nc.ui.querytemplate.value.IFieldValueElement;
import nc.ui.querytemplate.value.RefValueObject;
import nc.ui.trade.bill.ICardController;
import nc.ui.trade.business.HYPubBO_Client;
import nc.ui.trade.card.BillCardUI;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.querytemplate.TemplateInfo;
import nc.vo.tam.tongren001.DeptKqBVO;
import nc.vo.tam.tongren003.PaiPanReportVO;
import nc.vo.tam.tongren019.GxReportVO;
import nc.vo.tbm.tbm_029.BclbHeaderVO;

/**
 * @author 28729
 *
 */
public class EventQueryHandler extends EventHandler {
	private QryDlg2 m_qryDlg = null;
	private UFDate begindate = null;
	private UFDate enddate = null;
	
	private Integer beginngl = null;
	private Integer endngl = null;
	private String vyear = null;
	
	private ArrayList<String> list_dept = new ArrayList<String>();
	private ArrayList<String> list_psndoc = new ArrayList<String>();

	protected QryDlg2 createQryDLG() {
		TemplateInfo tempinfo = getTempInfo();
		QryDlg2 dlg = new QryDlg(this.getBillUI(),null,tempinfo);

		return dlg;
	}

	protected TemplateInfo getTempInfo() {
		ClientEnvironment ce = ClientEnvironment.getInstance();
		TemplateInfo tempinfo = new TemplateInfo();
		tempinfo.setPk_Org(ce.getCorporation().getPrimaryKey());
		tempinfo.setCurrentCorpPk(ce.getCorporation().getPrimaryKey());
		tempinfo.setFunNode(getUIController().getBillType());
		tempinfo.setUserid(ce.getUser().getPrimaryKey());
		tempinfo.setNodekey(getUIController().getBillType());
		return tempinfo;
	}
	@Override
	protected void onBoPrint() throws Exception {
		// TODO Auto-generated method stub
		BillItem[] items = getBillCardPanelWrapper().getBillCardPanel().getBodyShowItems();
		int rowcount = getBillCardPanelWrapper().getBillCardPanel().getBillTable().getRowCount();
		Object value[][] = new Object[rowcount+1][items.length]; 
		for(int i=0;i<items.length;i++){
			value[0][i] = items[i].getName();
			for(int j=0;j<rowcount;j++){
				if(items[i].getDataType()==4){
					Object tmpvalue = getBillCardPanelWrapper().getBillCardPanel().getBodyValueAt(j,items[i].getKey());
					value[j+1][i] = tmpvalue!=null&&new UFBoolean(tmpvalue.toString())!=null&&new UFBoolean(tmpvalue.toString()).booleanValue()?"是":"否";
				}else{
					value[j+1][i] = getBillCardPanelWrapper().getBillCardPanel().getBodyValueAt(j,items[i].getKey());
				}
			}
		}
		ExcelOut excel = new ExcelOut(this.getBillUI(),_getDate().toString().substring(0,4),_getDate().toString().substring(5,7));
		boolean flag = excel.createExcelFile(value);
		if(flag){
			MessageDialog.showHintDlg(this.getBillUI(), "提示", "导出完成");
		}
	}
	public QryDlg2 getQryDlg2() {
		if (m_qryDlg == null) {
			m_qryDlg = createQryDLG();
		}
		return m_qryDlg;
	}
	/**
	 * @param billUI
	 * @param control
	 */
	public EventQueryHandler(BillCardUI billUI, ICardController control) {
		super(billUI, control);
		// TODO Auto-generated constructor stub
	}
	@Override
	protected void onBoQuery() throws Exception {
		// TODO Auto-generated method stub
		if (getQryDlg2().showModal() == UIDialog.ID_OK) {
			list_dept = new ArrayList<String>();
			list_psndoc = new ArrayList<String>();
			vyear = null;
			beginngl =null;
			endngl=null;
			begindate=null;
			enddate=null;
			
			QueryTree aa =(QueryTree) getQryDlg2().getQryCondEditor().getCurrentCriteriaEditor().getCriteria().getCriteriaObject();
			TreeNode copyroot = (TreeNode)aa.getRoot();
			if(copyroot.getChildCount()>0){
				TreeNode realRoot = copyroot.getChildAt(0);
				int count = ((QueryTreeNode) realRoot).getChildCount();
				for(int i=0;i<count;i++){
					FilterNode node = 	(FilterNode)((QueryTreeNode) realRoot).getChildAt(i);
					if(node!=null){
						DefaultFilter aaa = (DefaultFilter)((FilterNode)((QueryTreeNode) realRoot).getChildAt(i)).getUserObject();
						if(aaa.getFilterMeta().getFieldCode().equals("joinworkdate")){
							if(aaa.getFieldValue()!=null){
								List<IFieldValueElement> list = aaa.getFieldValue().getFieldValues();
								if(list!=null&&list.size()>0){
									begindate = list.get(0)!=null?((UFDate)list.get(0).getValueObject()):null;
									enddate = list.get(1)!=null? ((UFDate)list.get(1).getValueObject()):null;
								}
							}
						}else if(aaa.getFilterMeta().getFieldCode().equals("pk_dept")){
							if(aaa.getFieldValue()!=null){
								List<IFieldValueElement> list = aaa.getFieldValue().getFieldValues();
								if(list!=null&&list.size()>0){
									for(int j=0;j<list.size();j++){
										list_dept.add(((RefValueObject)list.get(j).getValueObject()).getPk());
									}
								}
							}
						}else if(aaa.getFilterMeta().getFieldCode().equals("pk_psndoc")){
							if(aaa.getFieldValue()!=null){
								List<IFieldValueElement> list = aaa.getFieldValue().getFieldValues();
								if(list!=null&&list.size()>0){
									for(int j=0;j<list.size();j++){
										list_psndoc.add(((RefValueObject)list.get(j).getValueObject()).getPk());
									}
								}
							}
						}else if(aaa.getFilterMeta().getFieldCode().equals("ngl")){
							if(aaa.getFieldValue()!=null){
								List<IFieldValueElement> list = aaa.getFieldValue().getFieldValues();
								if(list!=null&&list.size()>0){
									 beginngl = list.get(0)!=null?((Integer)list.get(0).getValueObject()):null;
									 endngl = list.get(1)!=null?((Integer)list.get(1).getValueObject()):null;
								}
							}
						}else if(aaa.getFilterMeta().getFieldCode().equals("vyear")){
							if(aaa.getFieldValue()!=null){
								List<IFieldValueElement> list = aaa.getFieldValue().getFieldValues();
								if(list!=null&&list.size()>0){
									vyear = list.get(0)!=null?((String)list.get(0).getValueObject()):new String("");
								}
							}
						}
					}
				}
			}
			
			String deptsql = "";
			String wheresql = "";
			if(list_dept!=null&&list_dept.size()>0){
				String deptwhere = ""+HRPPubTool.formInSQL("pk_dept", list_dept)+" ";
				deptsql = "and snap.pk_psndoc in (select gx.pk_psndoc"
				+"  from tam_gx_snap gx"
				+"  left join v_trtam_paiban_deptdoc pb"
				+"    on gx.pk_psndoc = pb.pk_psndoc"
				+" where 1 = 1"+deptwhere
				+"  )";

			}
			if(list_psndoc != null && list_psndoc.size() > 0 ){
				wheresql += " "+HRPPubTool.formInSQL("snap.pk_psndoc", list_psndoc)+" ";
			}
			
			if(vyear != null){
				wheresql += " and snap.vyear = '"+vyear+"'";
			}
			if(begindate != null ){
				wheresql += " and "+" snap.joinworkdate >='"+begindate+"'";
			}
			
			if(enddate != null){
				wheresql += " and snap.joinworkdate <='"+enddate+"'";
			}
			
			if(beginngl  != null){
				wheresql += " and "+" snap.ngl >='"+beginngl+"'";
			}
			
			if(endngl  != null){
				wheresql += " and "+" snap.ngl <='"+endngl+"'";
			}
			
			String wheresql11 = getQryDlg2().getWhereSql();
			String sql ="select snap.*, dept.vname trtam_deptdoc_kp"
				+"  from tam_gx_snap snap"
				+"  left join"

				+"（select vyear, pk_psndoc, vname"
				+"  from (select gx.vyear, gx.pk_psndoc, wm_concat(kqks.vname) vname"
				+"          from tam_gx_snap gx"
				+"          left join v_trtam_paiban_deptdoc pb"
				+"            on gx.pk_psndoc = pb.pk_psndoc"
				+"          left join trtam_deptdoc_kq kqks"
				+"            on pb.pk_dept = kqks.pk_dept where pb.year='"+vyear+"'"   
				+"         group by gx.vyear, gx.pk_psndoc)) dept"
				+"    on snap.vyear = dept.vyear"
				+"   and snap.pk_psndoc = dept.pk_psndoc where 1= 1" + wheresql + deptsql;
			
			IUAPQueryBS bs= NCLocator.getInstance().lookup(IUAPQueryBS.class);
			ArrayList<GxReportVO> list = (ArrayList<GxReportVO>)bs.executeQuery(sql, new BeanListProcessor(GxReportVO.class));
			
			
			getBillCardPanelWrapper().getBillCardPanel().getBillModel().setBodyDataVO(list.toArray(new GxReportVO[0]));
			getBillCardPanelWrapper().getBillCardPanel().getBillModel().execLoadFormula();
	
			
		}
	}
	@Override
	protected void onBoRefresh() throws Exception {
		// TODO Auto-generated method stub
		super.onBoRefresh();
	}
}
