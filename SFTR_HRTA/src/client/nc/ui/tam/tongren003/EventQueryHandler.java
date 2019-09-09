/**
 * 
 */
package nc.ui.tam.tongren003;


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
import nc.vo.tbm.tbm_029.BclbHeaderVO;

/**
 * @author 28729
 *
 */
public class EventQueryHandler extends EventHandler {
	private QryDlg2 m_qryDlg = null;
	private UFDate begindate = null;
	private UFDate enddate = new UFDate("2099-12-31");
	private ArrayList<String> list_dept = new ArrayList<String>();

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
			begindate = new UFDate("2017-01-01");
			enddate = new UFDate("2099-12-31");
			QueryTree aa =(QueryTree) getQryDlg2().getQryCondEditor().getCurrentCriteriaEditor().getCriteria().getCriteriaObject();
			TreeNode copyroot = (TreeNode)aa.getRoot();
			if(copyroot.getChildCount()>0){
				TreeNode realRoot = copyroot.getChildAt(0);
				int count = ((QueryTreeNode) realRoot).getChildCount();
				for(int i=0;i<count;i++){
					FilterNode node = 	(FilterNode)((QueryTreeNode) realRoot).getChildAt(i);
					if(node!=null){
						DefaultFilter aaa = (DefaultFilter)((FilterNode)((QueryTreeNode) realRoot).getChildAt(i)).getUserObject();
						if(aaa.getFilterMeta().getFieldCode().equals("ddate")){
							if(aaa.getFieldValue()!=null){
								List<IFieldValueElement> list = aaa.getFieldValue().getFieldValues();
								if(list!=null&&list.size()>0){
									begindate = list.get(0)!=null?((UFDate)list.get(0).getValueObject()):new UFDate("2017-01-01");
									enddate = list.get(1)!=null? ((UFDate)list.get(1).getValueObject()):new UFDate("2099-12-31");
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
						}
					}
				}
			}
			int dayss = begindate.getDaysBetween(begindate, enddate);
			if(dayss>31){
				MessageDialog.showHintDlg(this.getBillUI(), "提示","查询期间请勿超过一个月");
				return;
			}
			String wheredept = 	" and pk_dept in (select pk_dept from trtam_deptdoc_kq where isnull(dr,0)=0 and  pk_corp='" + _getCorp().getPrimaryKey()+ "' and isnull(bisseal,'N')='N' and pk_dept in (select pk_docid from bd_tr_userpower where isnull(dr,0)=0 and pk_user='"+_getOperator()+"' and powertype=0 and  pk_corp='"+_getCorp().getPrimaryKey()+"')) ";

			String wheresql = " isnull(dr,0)=0 "+wheredept+" and ((dstartdate<'"+begindate+"' and denddate>='"+enddate+"') or (dstartdate>='"+begindate+"' and dstartdate<='"+enddate+"' ))  ";
			if(list_dept!=null&&list_dept.size()>0){
				wheresql += ""+HRPPubTool.formInSQL("pk_dept", list_dept)+" ";
			}

			HashMap<String,PaiPanReportVO> map = new HashMap<String, PaiPanReportVO>();
			if(getQryDlg2().getWhereSql()==null||getQryDlg2().getWhereSql().indexOf("pk_psndoc")<0){
				DeptKqBVO[] kqbvos = (DeptKqBVO[])HYPubBO_Client.queryByCondition(DeptKqBVO.class, wheresql);
				if(kqbvos!=null&&kqbvos.length>0){
					for(DeptKqBVO kqbvo:kqbvos){
						PaiPanReportVO vo = new PaiPanReportVO();
						vo.setBispaiban(new UFBoolean(false));
						vo.setPk_dept(kqbvo.getPk_dept());
						vo.setPk_psndoc(kqbvo.getPk_psndoc());
						map.put(kqbvo.getPk_dept()+kqbvo.getPk_psndoc(), vo);
					}
				}
			}

			IBclbDefining defin = NCLocator.getInstance().lookup(IBclbDefining.class);
			BclbHeaderVO[] bclbvos =  defin.queryBclb029AllBclbHeader(_getCorp().getPrimaryKey(), null);
			ArrayList<String> list_kqbb = new ArrayList<String>();
			HashMap<String,BclbHeaderVO> map_bclb = new HashMap<String, BclbHeaderVO>();
			for(BclbHeaderVO bclbvo:bclbvos){
				if(bclbvo.getLbbm().startsWith("99")){
					list_kqbb.add(bclbvo.getPrimaryKey());
				}
				map_bclb.put(bclbvo.getPrimaryKey(), bclbvo);
			}
			wheresql = getQryDlg2().getWhereSql();
			getBillCardPanelWrapper().getBillCardPanel().getBillModel().clearBodyData();

			String sql = " select pk_psndoc,pk_bb,pk_dept,ddate,pk_bbz,pk_dd,biszb,b.memo from trtam_paiban_b b inner join  ";
			sql+=" tbm_bclb tb on b.pk_bb=tb.pk_bclbid where b.pk_corp='"+_getCorp().getPrimaryKey()+"' and isnull(b.dr,0)=0 ";

			sql+=" and "+wheresql+" "+wheredept+" order by ddate";
			IUAPQueryBS bs= NCLocator.getInstance().lookup(IUAPQueryBS.class);
			String telesql = "select psn.pk_psndoc,psnbas.officephone telephone from bd_psndoc psn inner join bd_psnbasdoc  psnbas on psn.pk_psnbasdoc=psnbas.pk_psnbasdoc ";
			telesql += " where isnull(psnbas.dr,0)=0 and isnull(psn.dr,0)=0 and psn.pk_corp='"+_getCorp().getPrimaryKey()+"' and psnbas.mobile is not null ";
			ArrayList<PaiPanReportVO> listtele = (ArrayList<PaiPanReportVO>)bs.executeQuery(telesql, new BeanListProcessor(PaiPanReportVO.class));
			HashMap<String,String> map_tele = new HashMap<String, String>();
			if(listtele!=null&&listtele.size()>0){
				for(int i=0;i<listtele.size();i++){
					map_tele.put(listtele.get(i).getPk_psndoc(), listtele.get(i).getTelephone());
				}
			}
			ArrayList<PaiPanReportVO> list = (ArrayList<PaiPanReportVO>)bs.executeQuery(sql, new BeanListProcessor(PaiPanReportVO.class));
			if(list!=null&&list.size()>0){
				UFDate begin = list.get(0).getDdate();
				UFDate end = list.get(list.size()-1).getDdate();
				getBillCardPanelWrapper().getBillCardPanel().setHeadItem("ddate", begin+"至"+end);

				ArrayList<String> list_zb = new ArrayList<String>();
				ArrayList<String> list_kq = new ArrayList<String>();
				for(int i=0;i<list.size();i++){
					String key = list.get(i).getPk_dept()+list.get(i).getPk_psndoc();
					PaiPanReportVO vo = map.get(key)!=null?map.get(key):new PaiPanReportVO();
					vo.setPk_dept(list.get(i).getPk_dept());
					vo.setPk_psndoc(list.get(i).getPk_psndoc());
					vo.setBispaiban(new UFBoolean(true));
					if(list.get(i).getMemo()!=null&&list.get(i).getMemo().trim().length()>0){
						if(vo.getMemo()==null||vo.getMemo().trim().length()<=0){
							vo.setMemo(list.get(i).getMemo().trim());
						}else{
							String[] memos = vo.getMemo().split(";&");
							ArrayList<String> list_memo = new ArrayList<String>();
							list_memo.addAll(Arrays.asList(memos));
							if(!list_memo.contains(list.get(i).getMemo().trim())){
								vo.setMemo(vo.getMemo()+";&"+list.get(i).getMemo().trim());
							}

						}
					}

					String pk_bb = list.get(i).getPk_bb();
					if(pk_bb.equals("10028L10000000001O74")){
						vo.setBisbeiban(new UFBoolean(true));
						vo.setTelephone(map_tele.get(vo.getPk_psndoc()));
					}
					UFDate ddate = list.get(i).getDdate();
					int index = new UFDate().getDaysBetween(begin, ddate)+1;
					Object value = vo.getAttributeValue("pk_bb"+index+"");
					if(value==null||value.toString().trim().length()<=0){
						vo.setAttributeValue("pk_bb"+index+"", pk_bb);
					}else{
						vo.setAttributeValue("pk_bb"+index+"", value.toString().trim()+","+pk_bb);
					}
					Object value1 = vo.getAttributeValue("vbbnames"+index+"");
					if(value1==null||value1.toString().trim().length()<=0){
						vo.setAttributeValue("vbbnames"+index+"", map_bclb.get(pk_bb).getLbmc().trim());
					}else{
						vo.setAttributeValue("vbbnames"+index+"", value1.toString().trim()+"/"+ map_bclb.get(pk_bb).getLbmc().trim());
					}


					if(list.get(i).getBiszb()!=null&&list.get(i).getBiszb().booleanValue()){
						list_zb.add(key+list.get(i).getDdate());
					}
					if(list_kqbb.contains(list.get(i).getPk_bb())){
						list_kq.add(key+list.get(i).getDdate());
					}
					map.put(key, vo);
				}


				BillItem[] items = getBillCardPanelWrapper().getBillCardPanel().getBodyItems();
				ArrayList<BillItem> list_item = new ArrayList<BillItem>();
				for(BillItem item :items){
					if(!item.getKey().startsWith("vbbname")&&(!item.getKey().startsWith("pk_bb"))){
						list_item.add(item);
						item.setForeground(3);
					}
				}
				int days = new UFDate().getDaysBetween(begin, end);
				for(int i=1;i<=days+1;i++){
					BillItem item = new BillItem();

					item.setKey("vbbnames"+i+"");
					item.setLength(200);
					item.setWidth(120);
					int week = begin.getDateAfter(i-1).getWeek();
					String name = "";
					if(week==0){
						name = "(星期日)";
					}else if(week==1){
						name = "(星期一)";
					}else if(week==2){
						name = "(星期二)";
					}else if(week==3){
						name = "(星期三)";
					}else if(week==4){
						name = "(星期四)";
					}else if(week==5){
						name = "(星期五)";
					}else if(week==6){
						name = "(星期六)";
					}
					item.setName(begin.getDateAfter(i-1).toString()+name);
					item.setDataType(BillItem.STRING);
					item.setNull(false);
					if(week==0||week==6){
						//						item.setForeground(nc.ui.bill.tools.ColorConstants.COLOR_PINK);
					}
					list_item.add(item);

					BillItem item1 = new BillItem();
					item1.setKey("pk_bb"+i+"");
					item1.setLength(200);
					item1.setWidth(120);
					item1.setShow(false);
					item1.setName(begin.getDateAfter(i-1).toString());
					item1.setDataType(BillItem.STRING);
					list_item.add(item1);
				}
				String[] keys = map.keySet().toArray(new String[0]);
				Arrays.sort(keys);
				ArrayList<PaiPanReportVO> list_data = new ArrayList<PaiPanReportVO>();
				for(String key:keys){
					list_data.add(map.get(key));
				}
				getBillCardPanelWrapper().getBillCardPanel().getBillModel().setBodyItems(list_item.toArray(new BillItem[0]));
				getBillCardPanelWrapper().getBillCardPanel().setBillData(getBillCardPanelWrapper().getBillCardPanel().getBillData());
				getBillCardPanelWrapper().getBillCardPanel().getBillTable().setHeaderHeight(30);
				getBillCardPanelWrapper().getBillCardPanel().getBillModel().setBodyDataVO(list_data.toArray(new PaiPanReportVO[0]));
				getBillCardPanelWrapper().getBillCardPanel().getBillModel().execLoadFormula();
				int rowcount = getBillCardPanelWrapper().getBillCardPanel().getBillModel().getRowCount();
				for(int i=0;i<rowcount;i++){
					String pk_psndoc = getBillCardPanelWrapper().getBillCardPanel().getBillModel().getValueAt(i, "pk_psndoc").toString();
					String pk_dept = getBillCardPanelWrapper().getBillCardPanel().getBillModel().getValueAt(i, "pk_dept").toString();
					UFBoolean bispaipan = getBillCardPanelWrapper().getBillCardPanel().getBillModel().getValueAt(i, "bispaiban")!=null
					?new UFBoolean(getBillCardPanelWrapper().getBillCardPanel().getBillModel().getValueAt(i, "bispaiban").toString()):new UFBoolean(false);
					if(bispaipan==null||!bispaipan.booleanValue()){
						int psncodeindex = getBillCardPanelWrapper().getBillCardPanel().getBillModel().getBodyColByKey("psncode");
						int psnnameindex = getBillCardPanelWrapper().getBillCardPanel().getBillModel().getBodyColByKey("psnname");
						getBillCardPanelWrapper().getBillCardPanel().getBillModel().setBackground(Color.RED, i, psncodeindex);
						getBillCardPanelWrapper().getBillCardPanel().getBillModel().setBackground(Color.RED, i, psnnameindex);
					}
					for(int j=1;j<=days+1;j++){
						int index = getBillCardPanelWrapper().getBillCardPanel().getBillModel().getBodyColByKey("vbbnames"+(j)+"");
						UFDate showdate = begin.getDateAfter(j-1);

						if(list_kq.contains(pk_dept+pk_psndoc+showdate)){
							getBillCardPanelWrapper().getBillCardPanel().getBillModel().setBackground(Color.yellow, i, index);
						}
						if(list_zb.contains(pk_dept+pk_psndoc+showdate)){
							getBillCardPanelWrapper().getBillCardPanel().getBillModel().setBackground(Color.pink, i, index);
						}
					}
				}
				((ClientQueryUI)getBillUI()).onshow();
			}
		}
	}
	@Override
	protected void onBoRefresh() throws Exception {
		// TODO Auto-generated method stub
		super.onBoRefresh();
	}
}
