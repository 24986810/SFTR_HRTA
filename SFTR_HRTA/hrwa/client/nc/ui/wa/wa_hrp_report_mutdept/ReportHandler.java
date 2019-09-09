package nc.ui.wa.wa_hrp_report_mutdept;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.tree.TreeNode;

import nc.bs.framework.common.NCLocator;
import nc.itf.hr.wa.IClassitem;
import nc.itf.hr.wa.IItem;
import nc.itf.hrp.pub.IHRPBtn;
import nc.itf.uap.IUAPQueryBS;
import nc.itf.wa.hrp.pub.IHRPWABtn;
import nc.itf.wa.wa_hrppub.WaHrpBillStatus;
import nc.jdbc.framework.processor.BeanListProcessor;
import nc.ui.pub.ClientEnvironment;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UIDialog;
import nc.ui.pub.beans.constenum.DefaultConstEnum;
import nc.ui.pub.bill.BillItem;
import nc.ui.querytemplate.filter.DefaultFilter;
import nc.ui.querytemplate.querytree.QueryTree;
import nc.ui.querytemplate.querytree.QueryTree.FilterNode;
import nc.ui.querytemplate.querytree.QueryTree.QueryTreeNode;
import nc.ui.querytemplate.value.IFieldValueElement;
import nc.ui.querytemplate.value.RefValueObject;
import nc.ui.trade.bill.ICardController;
import nc.ui.trade.card.BillCardUI;
import nc.ui.trade.card.CardEventHandler;
import nc.ui.wa.wa_hrp_pub.QryDlg2;
import nc.ui.wa.wa_hrpreport_016.ExcelOut;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.querytemplate.TemplateInfo;
import nc.vo.wa.wa_002.ClassitemVO;
import nc.vo.wa.wa_024.ItemVO;
import nc.vo.wa.wa_hrp_report_mutdept.ReportBodyVO;
import nc.vo.wa.wa_reporthrp_008.DeptMnySumAndMxVO;
import nc.vo.wa.wa_reporthrp_008.ReportVO;

/**
 * @author szh
 *
 */
public class ReportHandler extends CardEventHandler {
	private String wheresql;
	private String type = "";
	private String pk_dept = "";
	private String beginperiod;
	/**
	 * @param billUI
	 * @param control
	 */
	public ReportHandler(BillCardUI billUI, ICardController control) {
		super(billUI, control);
		// TODO Auto-generated constructor stub
	}
	private QryDlg2 m_qryDlg = null;

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

	public QryDlg2 getQryDlg2() {
		if (m_qryDlg == null) {
			m_qryDlg = createQryDLG();
		}
		return m_qryDlg;
	}

	@Override
	protected void onBoBodyQuery() throws Exception {
		// TODO Auto-generated method stub
		if (getQryDlg2().showModal() == UIDialog.ID_OK) {
			wheresql = "";
			QueryTree aa =(QueryTree) getQryDlg2().getQryCondEditor().getCurrentCriteriaEditor().getCriteria().getCriteriaObject();
			TreeNode copyroot = (TreeNode)aa.getRoot();
			if(copyroot.getChildCount()>0){
				TreeNode realRoot = copyroot.getChildAt(0);
				int count = ((QueryTreeNode) realRoot).getChildCount();
				if(count<=0){
					DefaultFilter aaa = 	(DefaultFilter)((FilterNode)((QueryTreeNode) realRoot)).getUserObject();
					if(aaa.getFilterMeta().getFieldCode().equals("pk_wa_period")){
						if(aaa.getFieldValue()!=null){
							List<IFieldValueElement> list = aaa.getFieldValue().getFieldValues();
							if(list!=null&&list.size()>0){
								beginperiod = ((RefValueObject)list.get(0).getValueObject()).getName();
							}
						}
					}
				}
				for(int i=0;i<count;i++){
					FilterNode node = 	(FilterNode)((QueryTreeNode) realRoot).getChildAt(i);
					if(node!=null){
						DefaultFilter aaa = (DefaultFilter)((FilterNode)((QueryTreeNode) realRoot).getChildAt(i)).getUserObject();
						if(aaa.getFilterMeta().getFieldCode().equals("pk_wa_period")){
							if(aaa.getFieldValue()!=null){
								List<IFieldValueElement> list = aaa.getFieldValue().getFieldValues();
								if(list!=null&&list.size()>0){
									beginperiod = ((RefValueObject)list.get(0).getValueObject()).getName();
								}
							}
						}else if(aaa.getFilterMeta().getFieldCode().equals("type")){
							if(aaa.getFieldValue()!=null){
								List<IFieldValueElement> list = aaa.getFieldValue().getFieldValues();
								if(list!=null&&list.size()>0){
									type = ((DefaultConstEnum)list.get(0).getValueObject()).getValue().toString();
								}
							}
						}
//						else if(aaa.getFilterMeta().getFieldCode().equals("pk_dept")){
//							if(aaa.getFieldValue()!=null){
//								List<IFieldValueElement> list = aaa.getFieldValue().getFieldValues();
//								if(list!=null&&list.size()>0){
//									pk_dept = ((RefValueObject)list.get(0).getValueObject()).getPk();
//								}
//							}
//						}
						else if(aaa.getFieldValue()!=null&&aaa.getFieldValue().getFieldValues()!=null
								&&aaa.getFieldValue().getFieldValues().size()>0
								&&aaa.getBasicSql()!=null&&aaa.getBasicSql().trim().length()>0){
							wheresql+=(" and "+ aaa.getBasicSql());
						}
					}
				}
			}
			
			onQueryData();
		}
	}
	private void onQueryData() throws Exception{
		getBillCardPanelWrapper().getBillCardPanel().getBillModel().clearBodyData();
		String vyear = beginperiod.substring(0, 4);
		String vperiod = beginperiod.substring(5,7);
		
		IUAPQueryBS bs = NCLocator.getInstance().lookup(IUAPQueryBS.class);
		String sql="select fh.pk_dept pk_deptdoc, fh.pk_wa_period, fb.pk_psndoc,fb.nmny1,fb.nmny2,vyear||vperiod vperiod"
			+"  from wa_psn_item_h fh"
			+"  left join wa_psn_item_b fb"
			+"    on fh.pk_psn_item_h = fb.pk_psn_item_h"
			+" where fh.dr = 0"
			+"   and fb.dr = 0"
			+"   and vyear = '"+vyear+"'"
			+"   and vperiod = '"+vperiod+"'"
			+"   and fb.pk_psndoc in (select pk_psndoc"
			+"  from (select count(fb.pk_psndoc) count, fb.pk_psndoc"
			+"          from wa_psn_item_h fh"
			+"          left join wa_psn_item_b fb"
			+"            on fh.pk_psn_item_h = fb.pk_psn_item_h"
			+"         where fh.dr = 0"
			+"           and fb.dr = 0"
			+"           and vyear = '"+vyear+"'"
			+"           and vperiod = '"+vperiod+"'"
			+"         group by fb.pk_psndoc)"
			+" where count >= 2)";
		ArrayList<ReportBodyVO> list = (ArrayList<ReportBodyVO>) bs.executeQuery(sql, new BeanListProcessor(ReportBodyVO.class));
		
		getBillCardPanelWrapper().getBillCardPanel().getBillModel().setBodyDataVO(list.toArray(new ReportBodyVO[0]));
		getBillCardPanelWrapper().getBillCardPanel().getBillModel().execLoadFormula();
		getBillCardPanelWrapper().getBillCardPanel().getBillTable().setSortEnabled(true);
		getBillCardPanelWrapper().getBillCardPanel().setShowThMark(true);
		getBillCardPanelWrapper().getBillCardPanel().getBillModel().sortByColumn(1, false);
		getBillCardPanelWrapper().getBillCardPanel().updateUI();
	}

	private boolean initItem() throws Exception{
		String category = "0001691000000000HZAI";//奖金
		String pk_wa_class = IHRPWABtn.PK_JIANG;
		if(type.equals("1")){
			category = "0001231000000000ARFN";//津贴
			pk_wa_class = IHRPWABtn.PK_JINTIE;
		}
		HashMap<String,ClassitemVO> mapclassitem = new HashMap<String, ClassitemVO>();
		HashMap<String,ItemVO> mapitem = new HashMap<String, ItemVO>();
		IItem item = (IItem)NCLocator.getInstance().lookup(IItem.class);
		ItemVO[] itemvos = item.queryAllItem(_getCorp().getPrimaryKey());
		IClassitem classitem = (IClassitem)NCLocator.getInstance().lookup(IClassitem.class);
		String wheresql = " pk_wa_class='"+pk_wa_class+"' and  isnull(dr,0)=0 and cyear='"+beginperiod.substring(0,4)+"' and cperiod='"+beginperiod.substring(5,7)+"' ";
		wheresql += " and ifromflag=20  order by idisplayseq  ";
		ClassitemVO[] vos = classitem.queryAllByWhere(wheresql);
		if(vos==null||vos.length<=0) return false;
		if(vos!=null&&vos.length>0){
			for(ClassitemVO itemvo:vos){
				mapclassitem.put(itemvo.getPk_wa_item(), itemvo);
			}
		}
		if(itemvos!=null&&itemvos.length>0){
			for(ItemVO itemvo:itemvos){
				mapitem.put(itemvo.getPrimaryKey(), itemvo);
			}
		}
		ArrayList<BillItem> list = new ArrayList<BillItem>();
		BillItem item1 = getBillCardPanelWrapper().getBillCardPanel().getBillModel().getItemByKey("vbatchcode_approve");
		BillItem item2 = getBillCardPanelWrapper().getBillCardPanel().getBillModel().getItemByKey("vbatchcode_dept");
		BillItem item4 = getBillCardPanelWrapper().getBillCardPanel().getBillModel().getItemByKey("pk_psndoc");
		BillItem item5 = getBillCardPanelWrapper().getBillCardPanel().getBillModel().getItemByKey("psncode");
		BillItem item6 = getBillCardPanelWrapper().getBillCardPanel().getBillModel().getItemByKey("psnname");
		BillItem item8 = getBillCardPanelWrapper().getBillCardPanel().getBillModel().getItemByKey("nmny");
		BillItem item9 = getBillCardPanelWrapper().getBillCardPanel().getBillModel().getItemByKey("nsmny");
		BillItem item10 = getBillCardPanelWrapper().getBillCardPanel().getBillModel().getItemByKey("naftersmny");
		list.add(item1);
		list.add(item2);
		list.add(item4);
		list.add(item5);
		list.add(item6);
		list.add(item8);
		list.add(item9);
		list.add(item10);
		int showindex = 9;
		if(vos!=null&&vos.length>0){
			for(int i=0;i<vos.length;i++){
				BillItem itema = new BillItem();
				itema.setKey("item_"+vos[i].getPk_wa_item());
				itema.setName(mapitem.get(vos[i].getPk_wa_item()).getVname());
				itema.setShow(true);
				itema.setDataType(BillItem.DECIMAL);
				itema.setWidth(100);
				itema.setDecimalDigits(2);
				itema.setTatol(true);
				itema.setShowOrder(showindex);
				showindex++;
				list.add(itema);
			}
		}
		getBillCardPanelWrapper().getBillCardPanel().getBillModel().setBodyItems(list.toArray(new BillItem[0]));
		getBillCardPanelWrapper().getBillCardPanel().setBillData(getBillCardPanelWrapper().getBillCardPanel().getBillData());
		return true;
	}
	@Override
	protected void onBoRefresh() throws Exception {
		// TODO Auto-generated method stub
		super.onBoRefresh();
	}
	public void onBoExcelOut() {
		// TODO 自动生成方法存根
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
		ExcelOut excel = new ExcelOut(this.getBillUI(),beginperiod.substring(0,4),beginperiod.substring(5,7));
		boolean flag = excel.createExcelFile(value);
		if(flag){
			MessageDialog.showHintDlg(this.getBillUI(), "提示", "导出完成");
		}
	}
	@Override
	protected void onBoElse(int intBtn) throws Exception {
		// TODO Auto-generated method stub
		switch (intBtn) {
		case IHRPBtn.ExcelOut:
			onBoExcelOut();
			break;
		default:
			break;
		}
	}
}
