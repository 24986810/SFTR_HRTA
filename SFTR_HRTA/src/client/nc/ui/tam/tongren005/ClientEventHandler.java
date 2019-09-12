/**
 * 
 */
package nc.ui.tam.tongren005;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.table.TableColumnModel;
import javax.swing.tree.TreeNode;

import com.sun.xml.bind.v2.model.core.MaybeElement;

import nc.bs.framework.common.NCLocator;
import nc.itf.hr.ta.IBclbDefining;
import nc.itf.hrp.pub.IHRPBtn;
import nc.itf.uap.bd.def.IDefdoc;
import nc.ui.pub.ButtonObject;
import nc.ui.pub.ClientEnvironment;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UIDialog;
import nc.ui.pub.beans.UITable;
import nc.ui.pub.beans.constenum.DefaultConstEnum;
import nc.ui.pub.beans.table.ColumnGroup;
import nc.ui.pub.beans.table.GroupableTableHeader;
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
import nc.ui.trade.card.CardEventHandler;
import nc.vo.bd.def.DefdocVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDouble;
import nc.vo.querytemplate.TemplateInfo;
import nc.vo.tam.plugin.MidNightFeeBVO;
import nc.vo.tam.tongren001.DeptKqVO;
import nc.vo.tam.tongren003.PanbanWeekBVO;
import nc.vo.tam.tongren004.ZybBzVO;
import nc.vo.tam.tongren005.ZybMnyVO;
import nc.vo.tbm.tbm_029.BclbHeaderVO;

/**
 * @author 28729
 *
 */
public class ClientEventHandler extends CardEventHandler {
	private String wheresql;
	/**
	 * @param billUI
	 * @param control
	 */
	public ClientEventHandler(BillCardUI billUI, ICardController control) {
		super(billUI, control);
		// TODO Auto-generated constructor stub
	}
	@Override
	protected void onBoQuery() throws Exception {
		// TODO Auto-generated method stub
		super.onBoQuery();
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
			getBillCardPanelWrapper().getBillCardPanel().getBillModel().clearBodyData();
			wheresql = getQryDlg2().getWhereSql();
			if(wheresql==null||wheresql.trim().length()<=0){
				wheresql = " 1=1 ";
			}
			wheresql+=" and pk_dept in (select pk_docid from bd_tr_userpower where isnull(dr,0)=0 and pk_user='"+ClientEnvironment.getInstance().getUser().getPrimaryKey()+"' and powertype=0 and  pk_corp='"+ClientEnvironment.getInstance().getCorporation().getPrimaryKey()+"') ";
			DeptKqVO[] deptvos = (DeptKqVO[])HYPubBO_Client.queryByCondition(DeptKqVO.class, " pk_corp='"+_getCorp().getPrimaryKey()+"' ");
			HashMap<String,String> dept_scale = new HashMap<String, String>();
			for(DeptKqVO deptvo:deptvos){
				dept_scale.put(deptvo.getPrimaryKey(),deptvo.getVdeptscale());
			}
			IDefdoc doc = NCLocator.getInstance().lookup(IDefdoc.class);
			DefdocVO[] docvos = doc.queryDocs(_getCorp().getPrimaryKey(), "000154100000001119NR");
			ZybBzVO[] bzvos = (ZybBzVO[]) HYPubBO_Client.queryByCondition(ZybBzVO.class, " isnull(dr,0)=0 and pk_corp='"+_getCorp().getPrimaryKey()+"' "); 
			if(bzvos==null||bzvos.length<=0) return;
			HashMap<String,ZybBzVO> bz_map = new HashMap<String, ZybBzVO>();
			ArrayList<String> list_pk_bb = new ArrayList<String>();
			for(ZybBzVO bzvo:bzvos){
				bz_map.put(bzvo.getPk_bb()+"@@"+bzvo.getVdeptscale(),bzvo);
				if(!list_pk_bb.contains(bzvo.getPk_bb())){
					list_pk_bb.add(bzvo.getPk_bb());
				}
			}
			HashMap<String,Integer> map_index = new HashMap<String, Integer>();
			Integer index = 1;
			ArrayList<BillItem> list = new ArrayList<BillItem>();
			BillItem psncode = getBillCardPanelWrapper().getBillCardPanel().getBillModel().getItemByKey("psncode");
			BillItem psnname = getBillCardPanelWrapper().getBillCardPanel().getBillModel().getItemByKey("psnname");
			BillItem deptcode = getBillCardPanelWrapper().getBillCardPanel().getBillModel().getItemByKey("deptcode");
			BillItem deptname = getBillCardPanelWrapper().getBillCardPanel().getBillModel().getItemByKey("deptname");
			BillItem deptscale = getBillCardPanelWrapper().getBillCardPanel().getBillModel().getItemByKey("deptscale");
			BillItem nsummny = getBillCardPanelWrapper().getBillCardPanel().getBillModel().getItemByKey("nsummny");
			BillItem nsubmny = getBillCardPanelWrapper().getBillCardPanel().getBillModel().getItemByKey("nsubmny");
			BillItem nsfmny = getBillCardPanelWrapper().getBillCardPanel().getBillModel().getItemByKey("nsfmny");
			list.add(psncode);
			list.add(psnname);
			list.add(deptcode);
			list.add(deptname);
			list.add(deptscale);
			list.add(nsummny);
			list.add(nsubmny);
			list.add(nsfmny);
			ArrayList<String> listname = new ArrayList<String>();
			HashMap<Integer,String> map_name = new HashMap<Integer, String>();
			for(DefdocVO docvo:docvos){
				if(list_pk_bb.contains(docvo.getPrimaryKey())){
					int xx = index.intValue();
					BillItem nnum = new BillItem();
					nnum.setKey("nnum"+xx+"");
					nnum.setName("班数");
					nnum.setDataType(BillItem.DECIMAL);
					nnum.setShow(true);
					nnum.setWidth(100);
					nnum.setTatol(true);
					list.add(nnum);

					BillItem nbz = new BillItem();
					nbz.setKey("nbz"+xx+"");
					nbz.setName("标准");
					nbz.setDataType(BillItem.DECIMAL);
					nbz.setShow(true);
					nbz.setWidth(100);
					nbz.setTatol(false);
					list.add(nbz);

					BillItem nmny = new BillItem();
					nmny.setKey("nmny"+xx+"");
					nmny.setName("金额");
					nmny.setDataType(BillItem.DECIMAL);
					nmny.setShow(true);
					nmny.setWidth(100);
					nmny.setTatol(true);
					list.add(nmny);
					listname.add(docvo.getDocname());
					map_name.put(xx, docvo.getDocname());
					map_index.put(docvo.getPrimaryKey(), xx);
					index++;
				}
			}
			
			BillItem pk_psndoc = getBillCardPanelWrapper().getBillCardPanel().getBillModel().getItemByKey("pk_psndoc");
			BillItem pk_dept = getBillCardPanelWrapper().getBillCardPanel().getBillModel().getItemByKey("pk_dept");
			
			list.add(pk_psndoc);
			list.add(pk_dept);
			getBillCardPanelWrapper().getBillCardPanel().getBillModel().setBodyItems(list.toArray(new BillItem[0]));
			getBillCardPanelWrapper().getBillCardPanel().setBillData(getBillCardPanelWrapper().getBillCardPanel().getBillData());
			UITable cardTable=getBillCardPanelWrapper().getBillCardPanel().getBillTable();
			GroupableTableHeader cardHeader=(GroupableTableHeader)cardTable.getTableHeader();
			TableColumnModel cardTcm=cardTable.getColumnModel();

			int start = 7;
			for(int i=0;i<listname.size();i++){
				ColumnGroup group_qc = new ColumnGroup(listname.get(i));
				group_qc.add(cardTcm.getColumn(start+i*3+1));
				group_qc.add(cardTcm.getColumn(start+i*3+2));
				group_qc.add(cardTcm.getColumn(start+i*3+3));
				cardHeader.addColumnGroup(group_qc);
			}
			getBillCardPanelWrapper().getBillCardPanel().setShowThMark(true);



			IBclbDefining defin = NCLocator.getInstance().lookup(IBclbDefining.class);
			BclbHeaderVO[] bclbvos = defin.queryBclb029AllBclbHeader(_getCorp().getPrimaryKey(), null);
			HashMap<String,String> map_bclb = new HashMap<String, String>();
			for(BclbHeaderVO bclbvo:bclbvos){
				map_bclb.put(bclbvo.getPrimaryKey(), bclbvo.getPk_bbz());
			}
			HashMap<String,UFDouble> map_daymny = new HashMap<String, UFDouble>();
			HashMap<String,UFDouble> map_daymny_sub = new HashMap<String, UFDouble>();
			
			PanbanWeekBVO[] bvos = (PanbanWeekBVO[])HYPubBO_Client.queryByCondition(PanbanWeekBVO.class, wheresql+" and isnull(dr,0)=0 ");
			
			if(bvos==null||bvos.length<=0) return;
			HashMap<String,ZybMnyVO> mny_map = new HashMap<String, ZybMnyVO>();
			for(PanbanWeekBVO bvo:bvos){
				String pk_bbz = map_bclb.get(bvo.getPk_bb());
				if(pk_bbz==null||pk_bbz.trim().length()<=0||!list_pk_bb.contains(pk_bbz)
						||bz_map.get(pk_bbz+"@@"+dept_scale.get(bvo.getPk_dept()))==null){
					continue;
				}
				String key = bvo.getPk_dept()+bvo.getPk_psndoc();
				ZybMnyVO mnyvo = mny_map.get(key)!=null?mny_map.get(key):new ZybMnyVO();
				mnyvo.setPk_dept(bvo.getPk_dept());
				mnyvo.setPk_psndoc(bvo.getPk_psndoc());
				mnyvo.setDeptscale(dept_scale.get(bvo.getPk_dept()));
				int pk_index = map_index.get(pk_bbz);
				UFDouble nbz = bz_map.get(pk_bbz+"@@"+dept_scale.get(bvo.getPk_dept())).getNmny();
				String key2 = bvo.getPk_dept()+bvo.getPk_psndoc()+bvo.getDdate();
				if(map_daymny.containsKey(key2)){
					UFDouble oldnbz = map_daymny.get(key2);
					UFDouble oldsub = map_daymny_sub.get(key2)!=null?map_daymny_sub.get(key2):new UFDouble(0);
					if(nbz.doubleValue()>oldnbz.doubleValue()){
						oldsub = oldnbz.add(oldsub);
						map_daymny_sub.put(key2, oldsub);
						map_daymny.put(key2, nbz);
					}
//					else{zhanghua
//						oldsub = oldsub.add(nbz);
//						map_daymny_sub.put(key2, oldsub);	
//					}
				}else{
					map_daymny.put(key2, nbz);
				}
				mnyvo.setAttributeValue("nbz"+pk_index+"", nbz);
				UFDouble nnum = mnyvo.getAttributeValue("nnum"+pk_index+"")!=null?(new UFDouble(mnyvo.getAttributeValue("nnum"+pk_index+"").toString()).add(1)):new UFDouble(1);
				mnyvo.setAttributeValue("nnum"+pk_index+"",nnum);
				mnyvo.setAttributeValue("nmny"+pk_index+"", nbz.multiply(nnum));
				mny_map.put(key, mnyvo);
			}
			HashMap<String,UFDouble> map_daymny_sub_sum = new HashMap<String, UFDouble>();
			if(map_daymny_sub!=null&&map_daymny_sub.size()>0){
				String[] keys = map_daymny_sub.keySet().toArray(new String[0]);
				for(String key:keys){
					UFDouble mny = map_daymny_sub.get(key);
					UFDouble oldmny = map_daymny_sub_sum.get(key.substring(0,40))!=null? map_daymny_sub_sum.get(key.substring(0,40)):new UFDouble(0);
					map_daymny_sub_sum.put(key.substring(0,40), oldmny.add(mny));
				}
			}
			if(mny_map!=null&&mny_map.size()>0){
				ZybMnyVO[] datas = mny_map.values().toArray(new ZybMnyVO[0]);
				for(ZybMnyVO data:datas){
					UFDouble summny = new UFDouble(0);
					for(int i=0;i<listname.size();i++){
						UFDouble mny = data.getAttributeValue("nmny"+(i+1)+"")!=null?new UFDouble( data.getAttributeValue("nmny"+(i+1)+"").toString()):new UFDouble(0);
						summny = summny.add(mny);
					}
					data.setNsubmny(map_daymny_sub_sum.get(data.getPk_dept()+data.getPk_psndoc()));
					data.setNsummny(summny);
					data.setNsfmny(summny.sub(data.getNsubmny()!=null?data.getNsubmny():new UFDouble(0)));
				}
				getBillCardPanelWrapper().getBillCardPanel().getBillModel().setBodyDataVO(datas);
				getBillCardPanelWrapper().getBillCardPanel().getBillModel().execLoadFormula();
			}

		}
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
		ExcelOut excel = new ExcelOut(this.getBillUI(),_getDate().toString().substring(0,4),_getDate().toString().substring(5,7));
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
	
	@Override
	public void onBoAdd(ButtonObject bo) throws Exception {

		// TODO Auto-generated method stub
		if (getQryDlg2().showModal() == UIDialog.ID_OK) {
			getBillCardPanelWrapper().getBillCardPanel().getBillModel().clearBodyData();
			wheresql = getQryDlg2().getWhereSql();
			if(wheresql==null||wheresql.trim().length()<=0){
				wheresql = " 1=1 ";
			}
			wheresql+=" and pk_dept in (select pk_docid from bd_tr_userpower where isnull(dr,0)=0 and pk_user='"+ClientEnvironment.getInstance().getUser().getPrimaryKey()+"' and powertype=0 and  pk_corp='"+ClientEnvironment.getInstance().getCorporation().getPrimaryKey()+"') ";
			DeptKqVO[] deptvos = (DeptKqVO[])HYPubBO_Client.queryByCondition(DeptKqVO.class, " pk_corp='"+_getCorp().getPrimaryKey()+"' ");
			HashMap<String,String> dept_scale = new HashMap<String, String>();
			for(DeptKqVO deptvo:deptvos){
				dept_scale.put(deptvo.getPrimaryKey(),deptvo.getVdeptscale());
			}
			IDefdoc doc = NCLocator.getInstance().lookup(IDefdoc.class);
			DefdocVO[] docvos = doc.queryDocs(_getCorp().getPrimaryKey(), "000154100000001119NR");
			ZybBzVO[] bzvos = (ZybBzVO[]) HYPubBO_Client.queryByCondition(ZybBzVO.class, " isnull(dr,0)=0 and pk_corp='"+_getCorp().getPrimaryKey()+"' "); 
			if(bzvos==null||bzvos.length<=0) return;
			HashMap<String,ZybBzVO> bz_map = new HashMap<String, ZybBzVO>();
			ArrayList<String> list_pk_bb = new ArrayList<String>();
			for(ZybBzVO bzvo:bzvos){
				bz_map.put(bzvo.getPk_bb()+"@@"+bzvo.getVdeptscale(),bzvo);
				if(!list_pk_bb.contains(bzvo.getPk_bb())){
					list_pk_bb.add(bzvo.getPk_bb());
				}
			}
			HashMap<String,Integer> map_index = new HashMap<String, Integer>();
			Integer index = 1;
			ArrayList<BillItem> list = new ArrayList<BillItem>();
			BillItem psncode = getBillCardPanelWrapper().getBillCardPanel().getBillModel().getItemByKey("psncode");
			BillItem psnname = getBillCardPanelWrapper().getBillCardPanel().getBillModel().getItemByKey("psnname");
			BillItem deptcode = getBillCardPanelWrapper().getBillCardPanel().getBillModel().getItemByKey("deptcode");
			BillItem deptname = getBillCardPanelWrapper().getBillCardPanel().getBillModel().getItemByKey("deptname");
			BillItem deptscale = getBillCardPanelWrapper().getBillCardPanel().getBillModel().getItemByKey("deptscale");
			BillItem nsummny = getBillCardPanelWrapper().getBillCardPanel().getBillModel().getItemByKey("nsummny");
			BillItem nsubmny = getBillCardPanelWrapper().getBillCardPanel().getBillModel().getItemByKey("nsubmny");
			BillItem nsfmny = getBillCardPanelWrapper().getBillCardPanel().getBillModel().getItemByKey("nsfmny");
			list.add(psncode);
			list.add(psnname);
			list.add(deptcode);
			list.add(deptname);
			list.add(deptscale);
			list.add(nsummny);
			list.add(nsubmny);
			list.add(nsfmny);
			ArrayList<String> listname = new ArrayList<String>();
			HashMap<Integer,String> map_name = new HashMap<Integer, String>();
			for(DefdocVO docvo:docvos){
				if(list_pk_bb.contains(docvo.getPrimaryKey())){
					int xx = index.intValue();
					BillItem nnum = new BillItem();
					nnum.setKey("nnum"+xx+"");
					nnum.setName("班数");
					nnum.setDataType(BillItem.DECIMAL);
					nnum.setShow(true);
					nnum.setWidth(100);
					nnum.setTatol(true);
					list.add(nnum);

					BillItem nbz = new BillItem();
					nbz.setKey("nbz"+xx+"");
					nbz.setName("标准");
					nbz.setDataType(BillItem.DECIMAL);
					nbz.setShow(true);
					nbz.setWidth(100);
					nbz.setTatol(false);
					list.add(nbz);

					BillItem nmny = new BillItem();
					nmny.setKey("nmny"+xx+"");
					nmny.setName("金额");
					nmny.setDataType(BillItem.DECIMAL);
					nmny.setShow(true);
					nmny.setWidth(100);
					nmny.setTatol(true);
					list.add(nmny);
					listname.add(docvo.getDocname());
					map_name.put(xx, docvo.getDocname());
					map_index.put(docvo.getPrimaryKey(), xx);
					index++;
				}
			}
			
			BillItem pk_psndoc = getBillCardPanelWrapper().getBillCardPanel().getBillModel().getItemByKey("pk_psndoc");
			BillItem pk_dept = getBillCardPanelWrapper().getBillCardPanel().getBillModel().getItemByKey("pk_dept");
			
			list.add(pk_psndoc);
			list.add(pk_dept);
			getBillCardPanelWrapper().getBillCardPanel().getBillModel().setBodyItems(list.toArray(new BillItem[0]));
			getBillCardPanelWrapper().getBillCardPanel().setBillData(getBillCardPanelWrapper().getBillCardPanel().getBillData());
			UITable cardTable=getBillCardPanelWrapper().getBillCardPanel().getBillTable();
			GroupableTableHeader cardHeader=(GroupableTableHeader)cardTable.getTableHeader();
			TableColumnModel cardTcm=cardTable.getColumnModel();

			int start = 7;
			for(int i=0;i<listname.size();i++){
				ColumnGroup group_qc = new ColumnGroup(listname.get(i));
				group_qc.add(cardTcm.getColumn(start+i*3+1));
				group_qc.add(cardTcm.getColumn(start+i*3+2));
				group_qc.add(cardTcm.getColumn(start+i*3+3));
				cardHeader.addColumnGroup(group_qc);
			}
			getBillCardPanelWrapper().getBillCardPanel().setShowThMark(true);

			IBclbDefining defin = NCLocator.getInstance().lookup(IBclbDefining.class);
			BclbHeaderVO[] bclbvos = defin.queryBclb029AllBclbHeader(_getCorp().getPrimaryKey(), null);
			HashMap<String,String> map_bclb = new HashMap<String, String>();
			for(BclbHeaderVO bclbvo:bclbvos){
				map_bclb.put(bclbvo.getPrimaryKey(), bclbvo.getPk_bbz());
			}
			HashMap<String,UFDouble> map_daymny = new HashMap<String, UFDouble>();
			HashMap<String,UFDouble> map_daymny_sub = new HashMap<String, UFDouble>();
			
			//PanbanWeekBVO[] bvos = (PanbanWeekBVO[])HYPubBO_Client.queryByCondition(PanbanWeekBVO.class, wheresql+" and isnull(dr,0)=0 ");
			//之前是用的排班子表查询 现在用中夜班费备份表 lizhuchao
			MidNightFeeBVO[] bvos = (MidNightFeeBVO[])HYPubBO_Client.queryByCondition(MidNightFeeBVO.class, wheresql+" and isnull(dr,0)=0 ");
			
			if(bvos==null||bvos.length<=0) return;
			HashMap<String,ZybMnyVO> mny_map = new HashMap<String, ZybMnyVO>();
			for(MidNightFeeBVO bvo:bvos){
				String pk_bbz = map_bclb.get(bvo.getPk_bb());
				if(pk_bbz==null||pk_bbz.trim().length()<=0||!list_pk_bb.contains(pk_bbz)
						||bz_map.get(pk_bbz+"@@"+dept_scale.get(bvo.getPk_dept()))==null){
					continue;
				}
				String key = bvo.getPk_dept()+bvo.getPk_psndoc();
				ZybMnyVO mnyvo = mny_map.get(key)!=null?mny_map.get(key):new ZybMnyVO();
				mnyvo.setPk_dept(bvo.getPk_dept());
				mnyvo.setPk_psndoc(bvo.getPk_psndoc());
				mnyvo.setDeptscale(dept_scale.get(bvo.getPk_dept()));
				int pk_index = map_index.get(pk_bbz);
				UFDouble nbz = bz_map.get(pk_bbz+"@@"+dept_scale.get(bvo.getPk_dept())).getNmny();
				String key2 = bvo.getPk_dept()+bvo.getPk_psndoc()+bvo.getDdate();
				if(map_daymny.containsKey(key2)){
					UFDouble oldnbz = map_daymny.get(key2);
					UFDouble oldsub = map_daymny_sub.get(key2)!=null?map_daymny_sub.get(key2):new UFDouble(0);
					if(nbz.doubleValue()>oldnbz.doubleValue()){
						oldsub = oldnbz.add(oldsub);
						map_daymny_sub.put(key2, oldsub);
						map_daymny.put(key2, nbz);
					}
//					else{zhanghua
//						oldsub = oldsub.add(nbz);
//						map_daymny_sub.put(key2, oldsub);	
//					}
				}else{
					map_daymny.put(key2, nbz);
				}
				mnyvo.setAttributeValue("nbz"+pk_index+"", nbz);
				UFDouble nnum = mnyvo.getAttributeValue("nnum"+pk_index+"")!=null?(new UFDouble(mnyvo.getAttributeValue("nnum"+pk_index+"").toString()).add(1)):new UFDouble(1);
				mnyvo.setAttributeValue("nnum"+pk_index+"",nnum);
				mnyvo.setAttributeValue("nmny"+pk_index+"", nbz.multiply(nnum));
				mny_map.put(key, mnyvo);
			}
			HashMap<String,UFDouble> map_daymny_sub_sum = new HashMap<String, UFDouble>();
			if(map_daymny_sub!=null&&map_daymny_sub.size()>0){
				String[] keys = map_daymny_sub.keySet().toArray(new String[0]);
				for(String key:keys){
					UFDouble mny = map_daymny_sub.get(key);
					UFDouble oldmny = map_daymny_sub_sum.get(key.substring(0,40))!=null? map_daymny_sub_sum.get(key.substring(0,40)):new UFDouble(0);
					map_daymny_sub_sum.put(key.substring(0,40), oldmny.add(mny));
				}
			}
			if(mny_map!=null&&mny_map.size()>0){
				ZybMnyVO[] datas = mny_map.values().toArray(new ZybMnyVO[0]);
				for(ZybMnyVO data:datas){
					UFDouble summny = new UFDouble(0);
					for(int i=0;i<listname.size();i++){
						UFDouble mny = data.getAttributeValue("nmny"+(i+1)+"")!=null?new UFDouble( data.getAttributeValue("nmny"+(i+1)+"").toString()):new UFDouble(0);
						summny = summny.add(mny);
					}
					data.setNsubmny(map_daymny_sub_sum.get(data.getPk_dept()+data.getPk_psndoc()));
					data.setNsummny(summny);
					data.setNsfmny(summny.sub(data.getNsubmny()!=null?data.getNsubmny():new UFDouble(0)));
				}
				getBillCardPanelWrapper().getBillCardPanel().getBillModel().setBodyDataVO(datas);
				getBillCardPanelWrapper().getBillCardPanel().getBillModel().execLoadFormula();
			}

		}
	
	}
}
