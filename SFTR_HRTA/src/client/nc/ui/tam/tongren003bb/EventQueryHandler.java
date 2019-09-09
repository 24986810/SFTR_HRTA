/**
 * 
 */
package nc.ui.tam.tongren003bb;


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
import nc.ui.pub.tools.BannerDialog;
import nc.ui.querytemplate.filter.DefaultFilter;
import nc.ui.querytemplate.querytree.QueryTree;
import nc.ui.querytemplate.querytree.QueryTree.FilterNode;
import nc.ui.querytemplate.querytree.QueryTree.QueryTreeNode;
import nc.ui.querytemplate.value.DefaultFieldValueElement;
import nc.ui.querytemplate.value.IFieldValueElement;
import nc.ui.querytemplate.value.RefValueObject;
import nc.ui.tam.tongren003.EventHandler;
import nc.ui.tam.tongren003.ExcelOut;
import nc.ui.tam.tongren003.QryDlg;
import nc.ui.tam.tongren003.QryDlg2;
import nc.ui.tam.tongren003b.QryClassTypeDlg;
import nc.ui.trade.bill.ICardController;
import nc.ui.trade.business.HYPubBO_Client;
import nc.ui.trade.button.IBillButton;
import nc.ui.trade.card.BillCardUI;
import nc.uif.pub.exception.UifException;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.querytemplate.TemplateInfo;
import nc.vo.tam.tongren.power.UserClassTypeVO;
import nc.vo.tam.tongren.power.UserDeptVO;
import nc.vo.tam.tongren001.DeptKqBVO;
import nc.vo.tam.tongren001.DeptKqVO;
import nc.vo.tam.tongren003.PaiBanAuditMsg;
import nc.vo.tam.tongren003.PaiBanAuditMsg2;
import nc.vo.tam.tongren003.PaiPanReportVO;
import nc.vo.tbm.tbm_029.BclbHeaderVO;

/**
 * @author 28729
 *
 */
public class EventQueryHandler extends EventHandler {
	private QryDlg2 m_qryDlg = null;
	private String begindate = null;
	private String enddate = "2099-12-31";
	private ClientQueryUI ui = null;
	private HashMap<String,DeptKqVO>  map_dept = null;

	protected QryDlg2 createQryDLG() {
		TemplateInfo tempinfo = getTempInfo();
		QryDlg2 dlg = new QryClassTypeDlg(this.getBillUI(),null,tempinfo);

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
		ui = (ClientQueryUI) billUI;
		try {
			map_dept = getDeptKqVO();
		} catch (UifException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Override
	protected void onBoQuery() throws Exception {
		// TODO Auto-generated method stub
		if (getQryDlg2().showModal() == UIDialog.ID_OK) {
			ArrayList<String> list_dept = new ArrayList<String>();
			String pk_period = "";
			String deptname = "";
		    UFBoolean bisaudit = new UFBoolean(false);
			QueryTree aa =(QueryTree) getQryDlg2().getQryCondEditor().getCurrentCriteriaEditor().getCriteria().getCriteriaObject();
			TreeNode copyroot = (TreeNode)aa.getRoot();
			if(copyroot.getChildCount()>0){
				TreeNode realRoot = copyroot.getChildAt(0);
				int count = ((QueryTreeNode) realRoot).getChildCount();
				if(count<=0){
					DefaultFilter aaa = 	(DefaultFilter)((FilterNode)((QueryTreeNode) realRoot)).getUserObject();
					if(aaa.getFilterMeta().getFieldCode().equals("pk_dept")){
						if(aaa.getFieldValue()!=null){
							List<IFieldValueElement> list = aaa.getFieldValue().getFieldValues();
							if(list!=null&&list.size()>0){
								for(int j=0;j<list.size();j++){
									list_dept.add(((RefValueObject)list.get(j).getValueObject()).getPk());
									deptname +=(((RefValueObject)list.get(j).getValueObject()).getName()+",");
								}
							}
						}
					}
				}else{
					for(int i=0;i<count;i++){
						FilterNode node = 	(FilterNode)((QueryTreeNode) realRoot).getChildAt(i);
						if(node!=null){
							DefaultFilter aaa = (DefaultFilter)((FilterNode)((QueryTreeNode) realRoot).getChildAt(i)).getUserObject();
							if(aaa.getFilterMeta().getFieldCode().equals("pk_dept")){
								if(aaa.getFieldValue()!=null){
									List<IFieldValueElement> list = aaa.getFieldValue().getFieldValues();
									if(list!=null&&list.size()>0){
										for(int j=0;j<list.size();j++){
											list_dept.add(((RefValueObject)list.get(j).getValueObject()).getPk());
											deptname +=(((RefValueObject)list.get(j).getValueObject()).getName()+",");
										}
									}
								}
							}else if(aaa.getFilterMeta().getFieldCode().equals("pk_period")){
								if(aaa.getFieldValue()!=null){
									List<IFieldValueElement> list = aaa.getFieldValue().getFieldValues();
									if(list!=null&&list.size()>0){
										pk_period = ((RefValueObject)list.get(0).getValueObject()).getName();
									}
								}
							}else if(aaa.getFilterMeta().getFieldCode().equals("isaudit")){
								if(aaa.getFieldValue()!=null){
									List<IFieldValueElement> list = aaa.getFieldValue().getFieldValues();
									if(list!=null&&list.size()>0){
										bisaudit = new UFBoolean(((DefaultFieldValueElement)list.get(0)).getSqlString());
									}
								}
							}
						}
					}
				}
			}
			((ClientQueryUI)getBillUI()).setBisaudit(bisaudit);
			((ClientQueryUI)getBillUI()).setVperiod(pk_period);
			((ClientQueryUI)getBillUI()).setList_dept(list_dept);
			((ClientQueryUI)getBillUI()).initUnAuditData();
		}
	}
	@Override
	protected void onBoRefresh() throws Exception {
		// TODO Auto-generated method stub
		((ClientQueryUI)getBillUI()).initUnAuditData();
	}
	@Override
	public void onBoAudit() throws Exception {
		// TODO Auto-generated method stub
		int x = MessageDialog.showOkCancelDlg(this.getBillUI(), "提示", "确认审核当前排班数据?");
		if(x!=UIDialog.ID_OK) return;
		
		 //线程
        Runnable checkRun = new Runnable(){
            public void run()
            {
             //线程对话框：系统运行提示框
                BannerDialog dialog = new BannerDialog(ui);
                dialog.start();
                try{
                
                	onAudit();
                	
                } catch(Exception e) {
                    e.printStackTrace();
                    ui.showErrorMessage(e.getMessage());
                } finally {
                 //销毁系统运行提示框
                    dialog.end();
                }
            }
        };
        //启用线程
        new Thread(checkRun).start();
	}

	public void onAudit() throws Exception{
		int rowcount = getBillCardPanelWrapper().getBillCardPanel().getRowCount();
		String vdate = getBillCardPanelWrapper().getBillCardPanel().getHeadItem("ddate").getValueObject().toString();
		UFDate begin = new UFDate(vdate+"-01");
		UFDate end = new UFDate(vdate+"-"+begin.getDaysMonth());
	
		String enddate1 = getEndDay(begin.toString());
		int numweek = getWeek(enddate1);
		int numday = getDay(enddate1);// 最后一天是周天，取当月所有天数
		int oneday = getDay(begin.toString());
		
		int numdays = 0;
		if(numday == 0){
			numdays = new UFDate(begin.toString()).getDaysMonth();
		}else if(numday != 0){
			if(oneday !=1){
				numweek = numweek-1;
			}
			numdays = numweek * 7;
		}
		
		int days = numdays;
		
		UserClassTypeVO[] kqclasstypevos = (UserClassTypeVO[])HYPubBO_Client.queryByCondition(UserClassTypeVO.class, " isnull(dr,0)=0 and pk_user='"+_getOperator()+"' and powertype=0 and  pk_corp='"+_getCorp().getPrimaryKey()+"'  ");
		
		String strmb = "";
		for(UserClassTypeVO classtypevo:kqclasstypevos){
			// 医务
			if(classtypevo.getPk_docid().equals("2")){//门办
				strmb = "MB";
			}
			
		}
		
		
		
		ArrayList<PaiBanAuditMsg2> list_b = new ArrayList<PaiBanAuditMsg2>();
		for(int i=0;i<rowcount;i++){
			String pk_deptdoc = (String) getBillCardPanelWrapper().getBillCardPanel().getBillModel().getValueAt(i,"pk_dept");
			String pk_psndoc = (String) getBillCardPanelWrapper().getBillCardPanel().getBillModel().getValueAt(i,"pk_psndoc");
			Object temp = getBillCardPanelWrapper().getBillCardPanel().getBodyValueAt(i, "flag"); 
			
			
			if(temp != null && temp.toString().equals("true")){

				for(int j=1;j<=days+1;j++){
					String pk_bb = getBillCardPanelWrapper().getBillCardPanel().getBillModel().getValueAt(i, "pk_bb"+j+"")!=null?
							getBillCardPanelWrapper().getBillCardPanel().getBillModel().getValueAt(i, "pk_bb"+j+"").toString().trim():null;
					
					BillItem items = getBillCardPanelWrapper().getBillCardPanel().getBillModel().getItemByKey("vbbnames"+j);
					String vddate = items.getName().substring(0, 10);	
					
					
					if(pk_bb!=null&&pk_bb.trim().length()>0){
						String[] values = pk_bb.split(",");
										
						for(int k=0;k<values.length;k++){
							// 医务
							if(strmb.equals("")){
								if(!getMzByName(values[k]).booleanValue() || !getMzByAddress(values[k]).booleanValue()){
									
//								// 一审是否全部审核,有未审核,则不能提交OA
									PaiBanAuditMsg[] msg1vos = (PaiBanAuditMsg[])HYPubBO_Client.queryByCondition(PaiBanAuditMsg.class,
											" vbillstatus =1  and vperiod='"+vdate+"' and isnull(dr,0)=0 and pk_corp='"+_getCorp().getPrimaryKey()+"' and pk_dept='"+pk_deptdoc+"' and pk_psndoc='"+pk_psndoc+"' and pk_bb='"+values[k]+"' and ddate='"+vddate+"'");

									if(msg1vos.length == 0){
										MessageDialog.showHintDlg(this.getBillUI(), "提示", "有未审核完成班别不能上传!");
										return;
									}
									PaiBanAuditMsg2[] oldvos = (PaiBanAuditMsg2[])HYPubBO_Client.queryByCondition(PaiBanAuditMsg2.class,
											" vbillstatus =1  and vperiod='"+vdate+"' and isnull(dr,0)=0 and pk_corp='"+_getCorp().getPrimaryKey()+"' and pk_dept='"+pk_deptdoc+"' and pk_psndoc='"+pk_psndoc+"' and pk_bb='"+values[k]+"' and ddate='"+vddate+"'");
							
									for(PaiBanAuditMsg2 msgvo:oldvos){
										msgvo.setAuditpsn2(ClientEnvironment.getInstance().getUser().getUserName());
										msgvo.setDr(0);
										msgvo.setAudittime2(ClientEnvironment.getServerTime());
										msgvo.setAudittype(0);
										msgvo.setPk_corp(_getCorp().getPrimaryKey());
										msgvo.setVbillstatus(2);
										msgvo.setVperiod(vdate);
									}
									HYPubBO_Client.updateAry(oldvos);
									
								}
							}else{
								PaiBanAuditMsg[] msg1vos = (PaiBanAuditMsg[])HYPubBO_Client.queryByCondition(PaiBanAuditMsg.class,
										" vbillstatus =1  and vperiod='"+vdate+"' and isnull(dr,0)=0 and pk_corp='"+_getCorp().getPrimaryKey()+"' and pk_dept='"+pk_deptdoc+"' and pk_psndoc='"+pk_psndoc+"' and pk_bb='"+values[k]+"' and ddate='"+vddate+"'");

								if(msg1vos.length == 0){
									MessageDialog.showHintDlg(this.getBillUI(), "提示", "有未审核完成班别不能上传!");
									return;
								}
								// 判断一审是否通过
								PaiBanAuditMsg2[] oldvos = (PaiBanAuditMsg2[])HYPubBO_Client.queryByCondition(PaiBanAuditMsg2.class,
										" vbillstatus =1   and vperiod='"+vdate+"' and isnull(dr,0)=0 and pk_corp='"+_getCorp().getPrimaryKey()+"' and pk_dept='"+pk_deptdoc+"' and pk_psndoc='"+pk_psndoc+"' and pk_bb='"+values[k]+"' and ddate='"+vddate+"'");
						
								for(PaiBanAuditMsg2 msgvo:oldvos){
									msgvo.setAuditpsn2(ClientEnvironment.getInstance().getUser().getUserName());
									msgvo.setDr(0);
									msgvo.setAudittime2(ClientEnvironment.getServerTime());
									msgvo.setAudittype(0);
									msgvo.setPk_corp(_getCorp().getPrimaryKey());
									msgvo.setVbillstatus(2);
									msgvo.setVperiod(vdate);
								}
								HYPubBO_Client.updateAry(oldvos);
							}
						}
					}
				}
				
			}
		}
		
		onBoRefresh();
		MessageDialog.showHintDlg(this.getBillUI(), "提示", "审核完成");
		getBillUI().showHintMessage("审核完成!");
	}
	
	
	@Override
	protected void onBoCancelAudit() throws Exception {
		// TODO Auto-generated method stub
		int rowcount = getBillCardPanelWrapper().getBillCardPanel().getRowCount();
		String vdate = getBillCardPanelWrapper().getBillCardPanel().getHeadItem("ddate").getValueObject().toString();
		UFDate begin = new UFDate(vdate+"-01");
		UFDate end = new UFDate(vdate+"-"+begin.getDaysMonth());
		
		String enddate1 = getEndDay(begin.toString());
		int numweek = getWeek(enddate1);
		int numday = getDay(enddate1);// 最后一天是周天，取当月所有天数
		int oneday = getDay(begin.toString());
		
		int numdays = 0;
		if(numday == 0){
			numdays = new UFDate(begin.toString()).getDaysMonth();
		}else if(numday != 0){
			if(oneday !=1){
				numweek = numweek-1;
			}
			numdays = numweek * 7;
		}
		
		int days = numdays;
		
		UserClassTypeVO[] kqclasstypevos = (UserClassTypeVO[])HYPubBO_Client.queryByCondition(UserClassTypeVO.class, " isnull(dr,0)=0 and pk_user='"+_getOperator()+"' and powertype=0 and  pk_corp='"+_getCorp().getPrimaryKey()+"'  ");
		
		String strmb = "";
		for(UserClassTypeVO classtypevo:kqclasstypevos){
			// 医务
			if(classtypevo.getPk_docid().equals("2")){//门办
				strmb = "MB";
			}
			
		}
		
		// 计算部门排班天数
//		HashMap<String,Integer> map_days = new HashMap<String,Integer>();
//		for(int i=0;i<rowcount;i++){
//			String pk_deptdoc = (String) getBillCardPanelWrapper().getBillCardPanel().getBillModel().getValueAt(i,"pk_dept");
//			int ipbday = 0;
//			Object temp = getBillCardPanelWrapper().getBillCardPanel().getBodyValueAt(i, "flag"); 
//			
//			if(temp != null && temp.toString().equals("true")){
//				for(int j=1;j<=days+1;j++){
//					// 判断部门是否为护理，为护理两周可审，其他一个月可审
//					String pk_bb = getBillCardPanelWrapper().getBillCardPanel().getBillModel().getValueAt(i, "pk_bb"+j+"")!=null?
//								getBillCardPanelWrapper().getBillCardPanel().getBillModel().getValueAt(i, "pk_bb"+j+"").toString().trim():null;
//								
//					if(pk_bb!=null&&pk_bb.trim().length()>0){
//						ipbday = ipbday + 1;
//					}
//					
//				}
//				map_days.put(pk_deptdoc, ipbday);
//				
//			}
//			
//		}
		
		
		for(int i=0;i<rowcount;i++){
			String pk_deptdoc = (String) getBillCardPanelWrapper().getBillCardPanel().getBillModel().getValueAt(i,"pk_dept");
			String pk_psndoc = (String) getBillCardPanelWrapper().getBillCardPanel().getBillModel().getValueAt(i,"pk_psndoc");
			Object temp = getBillCardPanelWrapper().getBillCardPanel().getBodyValueAt(i, "flag"); 
			
			if(temp != null && temp.toString().equals("true")){
				
				
				for(int j=1;j<=days+1;j++){
					String pk_bb = getBillCardPanelWrapper().getBillCardPanel().getBillModel().getValueAt(i, "pk_bb"+j+"")!=null?
							getBillCardPanelWrapper().getBillCardPanel().getBillModel().getValueAt(i, "pk_bb"+j+"").toString().trim():null;
					
					BillItem items = getBillCardPanelWrapper().getBillCardPanel().getBillModel().getItemByKey("vbbnames"+j);
					String vddate = items.getName().substring(0, 10);		
					if(pk_bb!=null&&pk_bb.trim().length()>0){
						String[] values = pk_bb.split(",");
										
						for(int k=0;k<values.length;k++){
							if(strmb.equals("")){
								if(!getMzByName(values[k]).booleanValue() || !getMzByAddress(values[k]).booleanValue()){
									PaiBanAuditMsg2[] oldvos = (PaiBanAuditMsg2[])HYPubBO_Client.queryByCondition(PaiBanAuditMsg2.class,
											" vbillstatus=2  and vperiod='"+vdate+"' and isnull(dr,0)=0 and pk_corp='"+_getCorp().getPrimaryKey()+"' and pk_dept='"+pk_deptdoc+"' and pk_psndoc='"+pk_psndoc+"' and pk_bb='"+values[k]+"' and ddate='"+vddate+"'");
							
									for(PaiBanAuditMsg2 msgvo:oldvos){
										msgvo.setAuditpsn2(ClientEnvironment.getInstance().getUser().getUserName());
										msgvo.setDr(0);
										msgvo.setAudittime2(ClientEnvironment.getServerTime());
										msgvo.setAudittype(0);
										msgvo.setPk_corp(_getCorp().getPrimaryKey());
										msgvo.setVbillstatus(1);
										msgvo.setVperiod(vdate);
										msgvo.setAuditpsn2("");
										msgvo.setAudittime2(null);
									}
									HYPubBO_Client.updateAry(oldvos);
									
								}
							}else{
								PaiBanAuditMsg2[] oldvos = (PaiBanAuditMsg2[])HYPubBO_Client.queryByCondition(PaiBanAuditMsg2.class,
										" vbillstatus=2  and vperiod='"+vdate+"' and isnull(dr,0)=0 and pk_corp='"+_getCorp().getPrimaryKey()+"' and pk_dept='"+pk_deptdoc+"' and pk_psndoc='"+pk_psndoc+"' and pk_bb='"+values[k]+"' and ddate='"+vddate+"'");
						
								for(PaiBanAuditMsg2 msgvo:oldvos){
									msgvo.setAuditpsn2(ClientEnvironment.getInstance().getUser().getUserName());
									msgvo.setDr(0);
									msgvo.setAudittime2(ClientEnvironment.getServerTime());
									msgvo.setAudittype(0);
									msgvo.setPk_corp(_getCorp().getPrimaryKey());
									msgvo.setVbillstatus(1);
									msgvo.setVperiod(vdate);
									msgvo.setAuditpsn2("");
									msgvo.setAudittime2(null);
								}
								HYPubBO_Client.updateAry(oldvos);
							}
						}
					}
				}
				
			}
		}
		onBoRefresh();
		MessageDialog.showHintDlg(this.getBillUI(), "提示", "取消审核完成");
		getBillUI().showHintMessage("取消审核完成!");
		
	
	}
	
	public UFBoolean getMzByName(String pk_bb) throws BusinessException{
		UFBoolean isexist= new UFBoolean("N");
		IBclbDefining defin = NCLocator.getInstance().lookup(IBclbDefining.class);
		BclbHeaderVO[] bclbvos =  defin.queryBclb029AllBclbHeader(_getCorp().getPrimaryKey(), null," and pk_bclbid='"+pk_bb+"' and lbmc like '%门诊%'");
		if(bclbvos != null){
			if(bclbvos.length>0){
				isexist = new UFBoolean("Y");
			}
		}
		return isexist;
	
	}
	
	public UFBoolean getMzByAddress(String pk_bb) throws BusinessException{
		UFBoolean isexist= new UFBoolean("N");
		IBclbDefining defin = NCLocator.getInstance().lookup(IBclbDefining.class);
		BclbHeaderVO[] bclbvos =  defin.queryBclb029AllBclbHeader(_getCorp().getPrimaryKey(), null," and pk_bclbid='"+pk_bb+"' and pk_dd in (select pk_defdoc from bd_defdoc where (bd_defdoc.pk_defdoclist = '000154100000001119NG' and pk_corp in ('1002', '0001')) and (sealflag is null or sealflag <> 'Y') and docname like '%门诊%')");
		if(bclbvos != null){
			if(bclbvos.length>0){
				isexist = new UFBoolean("Y");
			}
		}
		return isexist;
	
	}

	@Override
	protected void onBoSelAll() throws Exception {
		// TODO Auto-generated method stub
//		super.onBoSelAll();
		int rowcount = ((ClientQueryUI)getBillUI()).getBillCardPanel().getRowCount();
		for(int i=0;i<rowcount;i++){
			((ClientQueryUI)getBillUI()).getBillCardPanel().getBillModel().setValueAt(true, i, "flag");
		}
	}

	@Override
	protected void onBoSelNone() throws Exception {
		// TODO Auto-generated method stub
//		super.onBoSelNone();
		int rowcount = ((ClientQueryUI)getBillUI()).getBillCardPanel().getRowCount();
		for(int i=0;i<rowcount;i++){
			((ClientQueryUI)getBillUI()).getBillCardPanel().getBillModel().setValueAt(false, i, "flag");
		}
	}
	
	public HashMap<String,DeptKqVO>  getDeptKqVO() throws UifException{
		String sqlwhere =	" pk_corp='" + _getCorp().getPrimaryKey()+ "' and isnull(bisseal,'N')='N' and pk_dept in (select pk_docid from bd_tr_userpower where isnull(dr,0)=0 and pk_user='"+_getOperator()+"' and powertype=0 and  pk_corp='"+_getCorp().getPrimaryKey()+"') ";
		DeptKqVO[] deptkqvos = (DeptKqVO[])HYPubBO_Client.queryByCondition(DeptKqVO.class, sqlwhere);
		HashMap<String,DeptKqVO> map_dept1 = new HashMap<String, DeptKqVO>();
		
		for(DeptKqVO deptvo:deptkqvos){
			map_dept1.put(deptvo.getPk_dept(), deptvo);
		}
		return map_dept1;
	}
	
	public UFBoolean getHlDeptByPK(String pk_deptdoc) throws BusinessException{
		UFBoolean isboolean = new UFBoolean("N");
		DeptKqVO deptvo = map_dept.get(pk_deptdoc);
		if(deptvo.getVcode().length() >=4){
			String prefixcode = deptvo.getVcode().substring(0,4);
			if(prefixcode.equals("1900")){
				isboolean = new UFBoolean("Y");
			}
		}
		
		return isboolean;
	}
	
	public void aditMsg2(String vdate,String pk_deptdoc,String pk_psndoc,String pk_bb,String vddate) throws UifException{
		// 一审是否全部审核,有未审核,则不能提交OA
		PaiBanAuditMsg[] msg1vos = (PaiBanAuditMsg[])HYPubBO_Client.queryByCondition(PaiBanAuditMsg.class,
				" vbillstatus =1  and vperiod='"+vdate+"' and isnull(dr,0)=0 and pk_corp='"+_getCorp().getPrimaryKey()+"' and pk_dept='"+pk_deptdoc+"' and pk_psndoc='"+pk_psndoc+"' and pk_bb='"+pk_bb+"' and ddate='"+vddate+"'");

		if(msg1vos.length == 0){											
			MessageDialog.showHintDlg(this.getBillUI(), "提示", "有未审核完成班别不能上传!");
			return;
			

		}
		PaiBanAuditMsg2[] oldvos = (PaiBanAuditMsg2[])HYPubBO_Client.queryByCondition(PaiBanAuditMsg2.class,
				" (vbillstatus =1)  and vperiod='"+vdate+"' and isnull(dr,0)=0 and pk_corp='"+_getCorp().getPrimaryKey()+"' and pk_dept='"+pk_deptdoc+"' and pk_psndoc='"+pk_psndoc+"' and pk_bb='"+pk_bb+"' and ddate='"+vddate+"'");

		for(PaiBanAuditMsg2 msgvo:oldvos){
			msgvo.setAuditpsn2(ClientEnvironment.getInstance().getUser().getUserName());
			msgvo.setDr(0);
			msgvo.setAudittime2(ClientEnvironment.getServerTime());
			msgvo.setAudittype(0);
			msgvo.setPk_corp(_getCorp().getPrimaryKey());
			msgvo.setVbillstatus(2);
			msgvo.setVperiod(vdate);
		}
		HYPubBO_Client.updateAry(oldvos);
	}
	
	public void cancelAuditMsg2(String vdate,String pk_deptdoc,String pk_psndoc,String pk_bb,String vddate) throws UifException{
		PaiBanAuditMsg2[] oldvos = (PaiBanAuditMsg2[])HYPubBO_Client.queryByCondition(PaiBanAuditMsg2.class,
				" vbillstatus=2  and vperiod='"+vdate+"' and isnull(dr,0)=0 and pk_corp='"+_getCorp().getPrimaryKey()+"' and pk_dept='"+pk_deptdoc+"' and pk_psndoc='"+pk_psndoc+"' and pk_bb='"+pk_bb+"' and ddate='"+vddate+"'");

		for(PaiBanAuditMsg2 msgvo:oldvos){
			msgvo.setAuditpsn2(ClientEnvironment.getInstance().getUser().getUserName());
			msgvo.setDr(0);
			msgvo.setAudittime2(ClientEnvironment.getServerTime());
			msgvo.setAudittype(0);
			msgvo.setPk_corp(_getCorp().getPrimaryKey());
			msgvo.setVbillstatus(1);
			msgvo.setVperiod(vdate);
			msgvo.setAuditpsn2("");
			msgvo.setAudittime2(null);
		}
		HYPubBO_Client.updateAry(oldvos);
	}
}
