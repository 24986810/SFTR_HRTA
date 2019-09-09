/**
 * 
 */
package nc.ui.tam.tongren003b;


import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import javax.swing.text.StyleConstants.ColorConstants;
import javax.swing.tree.TreeNode;

import nc.bs.framework.common.NCLocator;
import nc.itf.hr.ta.IBclbDefining;
import nc.itf.hrp.pub.HRPPubTool;
import nc.itf.uap.IUAPQueryBS;
import nc.itf.uap.pf.IPFMessage;
import nc.jdbc.framework.processor.BeanListProcessor;
import nc.jdbc.framework.processor.VectorProcessor;
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
import nc.ui.trade.bill.ICardController;
import nc.ui.trade.business.HYPubBO_Client;
import nc.ui.trade.button.IBillButton;
import nc.ui.trade.card.BillCardUI;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.msg.CommonMessageVO;
import nc.vo.pub.msg.UserNameObject;
import nc.vo.querytemplate.TemplateInfo;
import nc.vo.sm.UserVO;
import nc.vo.tam.tongren.power.UserClassTypeVO;
import nc.vo.tam.tongren.power.UserDeptVO;
import nc.vo.tam.tongren001.DeptKqBVO;
import nc.vo.tam.tongren003.PaiBanAuditMsg;
import nc.vo.tam.tongren003.PaiBanAuditMsg2;
import nc.vo.tam.tongren003.PaiBanAuditReason;
import nc.vo.tam.tongren003.PaiPanReportVO;
import nc.vo.tam.tongren003.PanbanWeekBVO;
import nc.vo.tam.tongren010.GxBVO;
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
	}
	@Override
	protected void onBoQuery() throws Exception {
		// TODO Auto-generated method stub
		if (getQryDlg2().showModal() == UIDialog.ID_OK) {
			ArrayList<String> list_dept = new ArrayList<String>();
			String pk_period = "";
			String deptname = "";
		    UFBoolean bisaudit = new UFBoolean(false);
		    String allselect= "";
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
									}else{
										allselect="ALL";
									}
								}
							}
						}
					}
				}
			}
			((ClientQueryUI)getBillUI()).setBisaudit(bisaudit);
			((ClientQueryUI)getBillUI()).setAllselect(allselect);
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
        
      //  onBoRefresh();
//		}
	}
	
	public void onAudit() throws Exception{
		int rowcount = getBillCardPanelWrapper().getBillCardPanel().getRowCount();
		// 根据考勤科室查到对应的排班人员
		String vdate = getBillCardPanelWrapper().getBillCardPanel().getHeadItem("ddate").getValueObject().toString();
		UFDate begin = new UFDate(vdate+"-01");
		UFDate end = new UFDate(vdate+"-"+begin.getDaysMonth());
		UFDate auditbegin = null,auditend = null;// 审批开始,结束时间
		
		
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
		
		int days = numdays;//new UFDate().getDaysBetween(begin, end);
		
		
		UserClassTypeVO[] kqclasstypevos = (UserClassTypeVO[])HYPubBO_Client.queryByCondition(UserClassTypeVO.class, " isnull(dr,0)=0 and pk_user='"+_getOperator()+"' and powertype=0 and  pk_corp='"+_getCorp().getPrimaryKey()+"'  ");
		
		String strmb = "";
		for(UserClassTypeVO classtypevo:kqclasstypevos){
			// 医务
			if(classtypevo.getPk_docid().equals("2")){//门办
				strmb = "MB";
			}
			
		}
		
		ArrayList<PaiBanAuditMsg> list = new ArrayList<PaiBanAuditMsg>();
		ArrayList<PaiBanAuditMsg2> list2 = new ArrayList<PaiBanAuditMsg2>();
		ArrayList<PaiBanAuditReason> list3 = new ArrayList<PaiBanAuditReason>();
		ArrayList<PaiBanAuditReason> list4 = new ArrayList<PaiBanAuditReason>();
		
		ArrayList<String> deptmsglist = new ArrayList<String>();
		
		for(int i=0;i<rowcount;i++){
			String pk_deptdoc = (String) getBillCardPanelWrapper().getBillCardPanel().getBillModel().getValueAt(i,"pk_dept");
			String pk_psndoc = (String) getBillCardPanelWrapper().getBillCardPanel().getBillModel().getValueAt(i,"pk_psndoc");
			Object temp = getBillCardPanelWrapper().getBillCardPanel().getBodyValueAt(i, "flag"); 
			Integer uploadnum = (Integer) getBillCardPanelWrapper().getBillCardPanel().getBillModel().getValueAt(i,"uploadnum");
			if(uploadnum == null) uploadnum = 1;
			String reason = (String) getBillCardPanelWrapper().getBillCardPanel().getBillModel().getValueAt(i,"reason");
			String psnname = (String) getBillCardPanelWrapper().getBillCardPanel().getBillModel().getValueAt(i,"psnname");
			
			if(temp != null && temp.toString().equals("true")){
				// 审批通过清除审批意见
				if(reason != null){
					PaiBanAuditReason paibanauditreason = new PaiBanAuditReason();
					paibanauditreason.setPk_dept(pk_deptdoc);
					paibanauditreason.setPk_psndoc(pk_psndoc);
					paibanauditreason.setVperiod(vdate);
					paibanauditreason.setReason(reason);
					paibanauditreason.setDr(0);
					
					if(list4.indexOf(pk_deptdoc) == -1){
						list4.add(paibanauditreason);
					}
				}
				
				for(int j=1;j<=days+1;j++){
					String pk_bb = getBillCardPanelWrapper().getBillCardPanel().getBillModel().getValueAt(i, "pk_bb"+j+"")!=null?
					getBillCardPanelWrapper().getBillCardPanel().getBillModel().getValueAt(i, "pk_bb"+j+"").toString().trim():null;
					String vbbnames =  getBillCardPanelWrapper().getBillCardPanel().getBillModel().getValueAt(i, "vbbnames"+j+"")!=null?
							getBillCardPanelWrapper().getBillCardPanel().getBillModel().getValueAt(i, "vbbnames"+j+"").toString().trim():null;
							
					BillItem items = getBillCardPanelWrapper().getBillCardPanel().getBillModel().getItemByKey("vbbnames"+j);
					String vddate = items.getName().substring(0, 10);
					if(pk_bb!=null&&pk_bb.trim().length()>0){
						String[] values = pk_bb.split(",");
						if(auditbegin == null){
							auditbegin = new UFDate(vddate);
						}
						auditend = new UFDate(vddate);
								
						for(int k=0;k<values.length;k++){
							// 一审过不能再次提交
							
							// 判断当前人员是否为二次上传，二次上传则全部通过，不走门办，医务的的判断
							if(uploadnum == 2){
								PaiBanAuditMsg msgvo = new PaiBanAuditMsg();
								msgvo.setAuditpsn(ClientEnvironment.getInstance().getUser().getUserName());
								msgvo.setDr(0);
								msgvo.setAudittime(ClientEnvironment.getServerTime());
								msgvo.setAudittype(0);
								msgvo.setPk_corp(_getCorp().getPrimaryKey());
								msgvo.setPk_dept(pk_deptdoc);
								msgvo.setPk_psndoc(pk_psndoc);
								msgvo.setPk_bb(values[k]);
								msgvo.setVbillstatus(1);
								msgvo.setVperiod(vdate);
								msgvo.setDdate(vddate);
								list.add(msgvo);
								
								PaiBanAuditMsg2 msgvo2 = new PaiBanAuditMsg2();
								msgvo2.setAuditpsn(ClientEnvironment.getInstance().getUser().getUserName());
								msgvo2.setDr(0);
								msgvo2.setAudittime(ClientEnvironment.getServerTime());
								msgvo2.setAudittype(0);
								msgvo2.setPk_corp(_getCorp().getPrimaryKey());
								msgvo2.setPk_dept(pk_deptdoc);
								msgvo2.setPk_psndoc(pk_psndoc);
								msgvo2.setPk_bb(values[k]);
								msgvo2.setVbillstatus(1);
								msgvo2.setVperiod(vdate);
								msgvo2.setDdate(vddate);
								list2.add(msgvo2);
							}else{
								// 判断当前用户 不是门办，要把门办的类别去掉
								if(strmb.equals("")){
									// 1判断名称是否有门诊，２地点是否有门诊
									if(!getMzByName(values[k]).booleanValue() || !getMzByAddress(values[k]).booleanValue()){
										// 判断排班是否已经审核,已经审核，不再审核
										
										PaiBanAuditMsg msgvo = new PaiBanAuditMsg();
										msgvo.setAuditpsn(ClientEnvironment.getInstance().getUser().getUserName());
										msgvo.setDr(0);
										msgvo.setAudittime(ClientEnvironment.getServerTime());
										msgvo.setAudittype(0);
										msgvo.setPk_corp(_getCorp().getPrimaryKey());
										msgvo.setPk_dept(pk_deptdoc);
										msgvo.setPk_psndoc(pk_psndoc);
										msgvo.setPk_bb(values[k]);
										msgvo.setVbillstatus(1);
										msgvo.setVperiod(vdate);
										msgvo.setDdate(vddate);
										list.add(msgvo);
										
										PaiBanAuditMsg2 msgvo2 = new PaiBanAuditMsg2();
										msgvo2.setAuditpsn(ClientEnvironment.getInstance().getUser().getUserName());
										msgvo2.setDr(0);
										msgvo2.setAudittime(ClientEnvironment.getServerTime());
										msgvo2.setAudittype(0);
										msgvo2.setPk_corp(_getCorp().getPrimaryKey());
										msgvo2.setPk_dept(pk_deptdoc);
										msgvo2.setPk_psndoc(pk_psndoc);
										msgvo2.setPk_bb(values[k]);
										msgvo2.setVbillstatus(1);
										msgvo2.setVperiod(vdate);
										msgvo2.setDdate(vddate);
										list2.add(msgvo2);
									}
								}else{
									PaiBanAuditMsg msgvo = new PaiBanAuditMsg();
									msgvo.setAuditpsn(ClientEnvironment.getInstance().getUser().getUserName());
									msgvo.setDr(0);
									msgvo.setAudittime(ClientEnvironment.getServerTime());
									msgvo.setAudittype(0);
									msgvo.setPk_corp(_getCorp().getPrimaryKey());
									msgvo.setPk_dept(pk_deptdoc);
									msgvo.setPk_psndoc(pk_psndoc);
									msgvo.setPk_bb(values[k]);
									msgvo.setVbillstatus(1);
									msgvo.setVperiod(vdate);
									msgvo.setDdate(vddate);
									list.add(msgvo);
									
									PaiBanAuditMsg2 msgvo2 = new PaiBanAuditMsg2();
									msgvo2.setAuditpsn(ClientEnvironment.getInstance().getUser().getUserName());
									msgvo2.setDr(0);
									msgvo2.setAudittime(ClientEnvironment.getServerTime());
									msgvo2.setAudittype(0);
									msgvo2.setPk_corp(_getCorp().getPrimaryKey());
									msgvo2.setPk_dept(pk_deptdoc);
									msgvo2.setPk_psndoc(pk_psndoc);
									msgvo2.setPk_bb(values[k]);
									msgvo2.setVbillstatus(1);
									msgvo2.setVperiod(vdate);
									msgvo2.setDdate(vddate);
									list2.add(msgvo2);
								}
								
							}
						}
					}
				}
			}
		}
		
		// begin 去掉已经审核的排班记录
		ArrayList<PaiBanAuditMsg> list20 = new ArrayList<PaiBanAuditMsg>();
		ArrayList<PaiBanAuditMsg2> list30 = new ArrayList<PaiBanAuditMsg2>();
		for(int i=0;i<list.size();i++){
			
			PaiBanAuditMsg pbamsg = list.get(i);
			String keyall = pbamsg.getPk_dept()+pbamsg.getPk_psndoc()+pbamsg.getPk_bb()+pbamsg.getDdate();	
			
			PaiPanReportVO reptvo = ui.getMapall().get(keyall);
			if(reptvo!= null){
				if(!"1".equals(reptvo.getVbillstatus())){
					list20.add(pbamsg);
				}
			}
//			String sqlwhere = " vperiod='"+vdate+"'and pk_dept='"+pbamsg.getPk_dept()+"' and pk_psndoc='"+pbamsg.getPk_psndoc()+"' and pk_bb='"+pbamsg.getPk_bb()+"' and vbillstatus='1' and ddate='"+pbamsg.getDdate()+"'";
//			PaiBanAuditMsg[] msgvos = (PaiBanAuditMsg[])HYPubBO_Client.queryByCondition(PaiBanAuditMsg.class, sqlwhere);
//			if(msgvos != null){
//				if(msgvos.length == 0){
//					
//					//list.remove(i);
//					list20.add(pbamsg);
//				}
//			}
			// 删除审批未通过
			//HYPubBO_Client.deleteByWhereClause(PaiBanAuditMsg.class,"pk_dept='"+pbamsg.getPk_dept()+"' and vperiod='"+pbamsg.getVperiod()+"' and (vbillstatus='0' or vbillstatus='3')");
		}
		// end
		
		HYPubBO_Client.insertAry(list20.toArray(new PaiBanAuditMsg[0]));
		
		if(list2 != null){
			if(list2.size() >0){
				for(int i=0;i<list2.size();i++){
					
					PaiBanAuditMsg2 pbamsg2 = list2.get(i);
					String keyall = pbamsg2.getPk_dept()+pbamsg2.getPk_psndoc()+pbamsg2.getPk_bb()+pbamsg2.getDdate();	

					PaiPanReportVO reptvo = ui.getMapall().get(keyall);
					if(reptvo!= null){
						if(!"2".equals(reptvo.getVbillstatus())){
							list30.add(pbamsg2);
						}
					}
//					PaiBanAuditMsg2[] msgvos = (PaiBanAuditMsg2[])HYPubBO_Client.queryByCondition(PaiBanAuditMsg2.class,"pk_dept='"+pbamsg2.getPk_dept()+"' and vperiod='"+pbamsg2.getVperiod()+"' and pk_psndoc='"+pbamsg2.getPk_psndoc()+"' and pk_bb='"+pbamsg2.getPk_bb()+"' and (vbillstatus='2' or vbillstatus='1') and ddate='"+pbamsg2.getDdate()+"'");
//					if(msgvos != null){
//						if(msgvos.length == 0){
//							
//							list30.add(pbamsg2);
//						}
//					}
					
					//HYPubBO_Client.deleteByWhereClause(PaiBanAuditMsg2.class,"pk_dept='"+pbamsg2.getPk_dept()+"' and vperiod='"+pbamsg2.getVperiod()+"' and (vbillstatus='0' or vbillstatus='3')");

				}
			}
		}
		
		HYPubBO_Client.insertAry(list30.toArray(new PaiBanAuditMsg2[0]));
		
		if(list4 != null){
			if(list4.size()>0){
				for(int i=0;i<list4.size();i++){
					
					HYPubBO_Client.deleteByWhereClause(PaiBanAuditReason.class,"pk_dept='"+list4.get(i).getPk_dept()+"' and vperiod='"+list4.get(i).getVperiod()+"'");
				}
				
			}
		}
			
		// 发送消息
		//sendMsg(deptmsglist.toArray(new String[0]),list3.toArray(new PaiBanAuditReason[0]));
		onBoRefresh();
		
		getButtonManager().getButton(IBillButton.Audit).setEnabled(false);
		getButtonManager().getButton(IBillButton.CancelAudit).setEnabled(true);
		getButtonManager().getButton(IBillButton.Edit).setEnabled(false);
		getBillUI().updateButtons();
		MessageDialog.showHintDlg(this.getBillUI(), "提示", auditbegin + "至"+ auditend +"审核完成");
		getBillUI().showHintMessage(auditbegin + "至"+ auditend +"审核完成!");
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
	protected void onBoCancelAudit() throws Exception {
		// TODO Auto-generated method stub
		int x = MessageDialog.showOkCancelDlg(this.getBillUI(), "提示", "确认取消审核当前排班数据?");
		if(x!=UIDialog.ID_OK) return;
		
		int rowcount = getBillCardPanelWrapper().getBillCardPanel().getRowCount();
		// 根据考勤科室查到对应的排班人员
		String vdate = getBillCardPanelWrapper().getBillCardPanel().getHeadItem("ddate").getValueObject().toString();
		UFDate begin = new UFDate(vdate+"-01");
		UFDate end = new UFDate(vdate+"-"+begin.getDaysMonth());
		String enddate1 = getEndDay(begin.toString());
		int numweek = getWeek(enddate1);
		int numday = getDay(enddate1);// 最后一天是周天，取当月所有天数
		int oneday = getDay(begin.toString());
		UFDate auditbegin = null,auditend = null;// 审批开始,结束时间
		
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
		
		ArrayList<PaiBanAuditMsg> list = new ArrayList<PaiBanAuditMsg>();
		
		UserClassTypeVO[] kqclasstypevos = (UserClassTypeVO[])HYPubBO_Client.queryByCondition(UserClassTypeVO.class, " isnull(dr,0)=0 and pk_user='"+_getOperator()+"' and powertype=0 and  pk_corp='"+_getCorp().getPrimaryKey()+"'  ");
		
		String strmb = "";
		for(UserClassTypeVO classtypevo:kqclasstypevos){
			// 医务
			if(classtypevo.getPk_docid().equals("2")){//门办
				strmb = "MB";
			}
			
		}
		
		for(int i=0;i<rowcount;i++){
			String pk_deptdoc = (String) getBillCardPanelWrapper().getBillCardPanel().getBillModel().getValueAt(i,"pk_dept");
			String pk_psndoc = (String) getBillCardPanelWrapper().getBillCardPanel().getBillModel().getValueAt(i,"pk_psndoc");
			Object temp = getBillCardPanelWrapper().getBillCardPanel().getBodyValueAt(i, "flag"); 
			
			if(temp != null && temp.toString().equals("true")){
				// 按门诊进行区分
				for(int j=1;j<=days+1;j++){
				String pk_bb = getBillCardPanelWrapper().getBillCardPanel().getBillModel().getValueAt(i, "pk_bb"+j+"")!=null?
						getBillCardPanelWrapper().getBillCardPanel().getBillModel().getValueAt(i, "pk_bb"+j+"").toString().trim():null;
				
				BillItem items = getBillCardPanelWrapper().getBillCardPanel().getBillModel().getItemByKey("vbbnames"+j);
				String vddate = items.getName().substring(0, 10);		
				if(pk_bb!=null&&pk_bb.trim().length()>0){
					String[] values = pk_bb.split(",");
					if(auditbegin == null){
						auditbegin = new UFDate(vddate);
					}
					auditend =  new UFDate(vddate);
									
					for(int k=0;k<values.length;k++){
						// 查询二审有没有审核
						PaiBanAuditMsg2[] paibanmsg2 = (PaiBanAuditMsg2[])HYPubBO_Client.queryByCondition(PaiBanAuditMsg2.class, " vbillstatus='2' and vperiod='"+vdate+"' and isnull(dr,0)=0 and pk_corp='"+_getCorp().getPrimaryKey()+"' and pk_dept='"+pk_deptdoc+"' and pk_psndoc='"+pk_psndoc+"' and ddate='"+vddate+"' and pk_bb='"+values[k]+"'");
						if(paibanmsg2 != null){
							if(paibanmsg2.length >0){
								MessageDialog.showHintDlg(this.getBillUI(), "提示", "该数据已经二审,不能在一审取消!");
								//getBillUI().showHintMessage("该数据已经二审,不能在一审取消!");
								return;
							}
						}
						
						if(strmb.equals("")){
							// 1判断名称是否有门诊，２地点是否有门诊
							if(!getMzByName(values[k]).booleanValue() || !getMzByAddress(values[k]).booleanValue()){
								//HYPubBO_Client.deleteByWhereClause(PaiBanAuditMsg.class,"vperiod='"+vdate+"' and isnull(dr,0)=0 and pk_corp='"+_getCorp().getPrimaryKey()+"' and pk_dept='"+pk_deptdoc+"' and pk_psndoc='"+pk_psndoc+"' and ddate='"+vddate+"' and pk_bb='"+values[k]+"'");
								//HYPubBO_Client.deleteByWhereClause(PaiBanAuditMsg2.class,"vperiod='"+vdate+"' and isnull(dr,0)=0 and pk_corp='"+_getCorp().getPrimaryKey()+"' and pk_dept='"+pk_deptdoc+"' and pk_psndoc='"+pk_psndoc+"' and ddate='"+vddate+"' and pk_bb='"+values[k]+"'");
//								PaiBanAuditMsg pbamsg = list.get(i);
//								String keyall = pk_deptdoc+pk_psndoc+values[k]+vddate;	
								
								
								PaiBanAuditMsg[] paibanmsg = (PaiBanAuditMsg[])HYPubBO_Client.queryByCondition(PaiBanAuditMsg.class, "vperiod='"+vdate+"' and isnull(dr,0)=0 and pk_corp='"+_getCorp().getPrimaryKey()+"' and pk_dept='"+pk_deptdoc+"' and pk_psndoc='"+pk_psndoc+"' and ddate='"+vddate+"' and pk_bb='"+values[k]+"'");
								if(paibanmsg != null){
									if(paibanmsg.length >0){
										for(int m=0;m < paibanmsg.length;m++){
											PaiBanAuditMsg pbmsg = paibanmsg[m];
											pbmsg.setVbillstatus(3);
											HYPubBO_Client.update(pbmsg);
											
										}
										
									}
								}
								
								PaiBanAuditMsg2[] paibanmsg1 = (PaiBanAuditMsg2[])HYPubBO_Client.queryByCondition(PaiBanAuditMsg2.class, "vperiod='"+vdate+"' and isnull(dr,0)=0 and pk_corp='"+_getCorp().getPrimaryKey()+"' and pk_dept='"+pk_deptdoc+"' and pk_psndoc='"+pk_psndoc+"' and ddate='"+vddate+"' and pk_bb='"+values[k]+"'");
								if(paibanmsg1 != null){
									if(paibanmsg1.length >0){
										for(int m=0;m < paibanmsg1.length;m++){
											PaiBanAuditMsg2 pbmsg = paibanmsg1[m];
											pbmsg.setVbillstatus(3);
											HYPubBO_Client.update(pbmsg);
										}
									}
								}
							}
						}else{
							// 门诊
							//HYPubBO_Client.deleteByWhereClause(PaiBanAuditMsg.class,"vperiod='"+vdate+"' and isnull(dr,0)=0 and pk_corp='"+_getCorp().getPrimaryKey()+"' and pk_dept='"+pk_deptdoc+"' and pk_psndoc='"+pk_psndoc+"' and ddate='"+vddate+"' and pk_bb='"+values[k]+"'");
							//HYPubBO_Client.deleteByWhereClause(PaiBanAuditMsg2.class,"vperiod='"+vdate+"' and isnull(dr,0)=0 and pk_corp='"+_getCorp().getPrimaryKey()+"' and pk_dept='"+pk_deptdoc+"' and pk_psndoc='"+pk_psndoc+"' and ddate='"+vddate+"' and pk_bb='"+values[k]+"'");
							PaiBanAuditMsg[] paibanmsg = (PaiBanAuditMsg[])HYPubBO_Client.queryByCondition(PaiBanAuditMsg.class, "vperiod='"+vdate+"' and isnull(dr,0)=0 and pk_corp='"+_getCorp().getPrimaryKey()+"' and pk_dept='"+pk_deptdoc+"' and pk_psndoc='"+pk_psndoc+"' and ddate='"+vddate+"' and pk_bb='"+values[k]+"'");
							if(paibanmsg != null){
								if(paibanmsg.length >0){
									for(int m=0;m < paibanmsg.length;m++){
										PaiBanAuditMsg pbmsg = paibanmsg[m];
										pbmsg.setVbillstatus(3);
										HYPubBO_Client.update(pbmsg);
									}
								}
							}
							
							PaiBanAuditMsg2[] paibanmsg1 = (PaiBanAuditMsg2[])HYPubBO_Client.queryByCondition(PaiBanAuditMsg2.class, "vperiod='"+vdate+"' and isnull(dr,0)=0 and pk_corp='"+_getCorp().getPrimaryKey()+"' and pk_dept='"+pk_deptdoc+"' and pk_psndoc='"+pk_psndoc+"' and ddate='"+vddate+"' and pk_bb='"+values[k]+"'");
							if(paibanmsg1 != null){
								if(paibanmsg1.length >0){
									for(int m=0;m < paibanmsg1.length;m++){
										PaiBanAuditMsg2 pbmsg = paibanmsg1[m];
										pbmsg.setVbillstatus(3);
										HYPubBO_Client.update(pbmsg);
									}
								}
							}
						}
					}
				}
			}
		}
		}
		//MessageDialog.showHintDlg(this.getBillUI(), "提示", "取消审核完成!");
		getBillUI().showHintMessage(auditbegin + "至"+ auditend +"取消审核完成!");
		
		onBoRefresh();
		getButtonManager().getButton(IBillButton.Audit).setEnabled(false);
		getButtonManager().getButton(IBillButton.CancelAudit).setEnabled(false);
		getButtonManager().getButton(IBillButton.Edit).setEnabled(true);
		getBillUI().updateButtons();
		/*
		ArrayList<String> list_dept = ((ClientQueryUI)getBillUI()).getList_dept();
		if(list_dept!=null&&list_dept.size()>0){
			String vdate = getBillCardPanelWrapper().getBillCardPanel().getHeadItem("ddate").getValueObject().toString();
			PaiBanAuditMsg[] oldvos = (PaiBanAuditMsg[])HYPubBO_Client.queryByCondition(PaiBanAuditMsg.class,
					" vperiod='"+vdate+"' and isnull(dr,0)=0 and pk_corp='"+_getCorp().getPrimaryKey()+"' "+HRPPubTool.formInSQL("pk_dept", list_dept)+"");
			
			if(oldvos!=null&&oldvos.length>0){
				HYPubBO_Client.deleteByWhereClause(PaiBanAuditMsg.class, " vperiod='"+vdate+"' and isnull(dr,0)=0 and pk_corp='"+_getCorp().getPrimaryKey()+"' "+HRPPubTool.formInSQL("pk_dept", list_dept)+"");
				MessageDialog.showHintDlg(this.getBillUI(), "提示", "取消审核完成!");
				getBillUI().showHintMessage("取消审核完成!");
				return;
			}
		}
		*/
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
	
	
	
	@Override
	protected void onBoEdit() throws Exception {
		// TODO Auto-generated method stub

		int rowcount = getBillCardPanelWrapper().getBillCardPanel().getRowCount();
		// 根据考勤科室查到对应的排班人员
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
		
		int days = numdays;//new UFDate().getDaysBetween(begin, end);
		
		
		UserClassTypeVO[] kqclasstypevos = (UserClassTypeVO[])HYPubBO_Client.queryByCondition(UserClassTypeVO.class, " isnull(dr,0)=0 and pk_user='"+_getOperator()+"' and powertype=0 and  pk_corp='"+_getCorp().getPrimaryKey()+"'  ");
		
		String strmb = "";
		for(UserClassTypeVO classtypevo:kqclasstypevos){
			// 医务
			if(classtypevo.getPk_docid().equals("2")){//门办
				strmb = "MB";
			}
			
		}
		
		ArrayList<PaiBanAuditMsg> list = new ArrayList<PaiBanAuditMsg>();
		ArrayList<PaiBanAuditMsg2> list2 = new ArrayList<PaiBanAuditMsg2>();
		ArrayList<PaiBanAuditReason> list3 = new ArrayList<PaiBanAuditReason>();
		ArrayList<PaiBanAuditReason> list4 = new ArrayList<PaiBanAuditReason>();
		
		ArrayList<String> deptmsglist = new ArrayList<String>();
		ArrayList<String> list_psn = new ArrayList<String>();
		
		for(int i=0;i<rowcount;i++){
			String pk_deptdoc = (String) getBillCardPanelWrapper().getBillCardPanel().getBillModel().getValueAt(i,"pk_dept");
			String pk_psndoc = (String) getBillCardPanelWrapper().getBillCardPanel().getBillModel().getValueAt(i,"pk_psndoc");
			Object temp = getBillCardPanelWrapper().getBillCardPanel().getBodyValueAt(i, "flag"); 
			Integer uploadnum = (Integer) getBillCardPanelWrapper().getBillCardPanel().getBillModel().getValueAt(i,"uploadnum");
			if(uploadnum == null) uploadnum = 1;
			String reason = (String) getBillCardPanelWrapper().getBillCardPanel().getBillModel().getValueAt(i,"reason");
			String psnname = (String) getBillCardPanelWrapper().getBillCardPanel().getBillModel().getValueAt(i,"psnname");
			
			if(temp != null && temp.toString().equals("true")){
			
						
				list_psn.add(pk_psndoc);
				// 人员审意见
				if(reason != null){
					PaiBanAuditReason paibanauditreason = new PaiBanAuditReason();
					paibanauditreason.setPk_dept(pk_deptdoc);
					paibanauditreason.setPk_psndoc(pk_psndoc);
					paibanauditreason.setVperiod(vdate);
					paibanauditreason.setReason(reason);
					paibanauditreason.setDr(0);
					paibanauditreason.setPsnname(psnname);
						
					list3.add(paibanauditreason);
				}
					
				if(deptmsglist.size() == 0){
					deptmsglist.add(pk_deptdoc);					
				}else{
					if(deptmsglist.indexOf(pk_deptdoc) == -1){
						deptmsglist.add(pk_deptdoc);
					}
				}	
						
				// 审批未通处理
				for(int j=1;j<=days+1;j++){
					String pk_bb = getBillCardPanelWrapper().getBillCardPanel().getBillModel().getValueAt(i, "pk_bb"+j+"")!=null?
					getBillCardPanelWrapper().getBillCardPanel().getBillModel().getValueAt(i, "pk_bb"+j+"").toString().trim():null;
					String vbbnames =  getBillCardPanelWrapper().getBillCardPanel().getBillModel().getValueAt(i, "vbbnames"+j+"")!=null?
									getBillCardPanelWrapper().getBillCardPanel().getBillModel().getValueAt(i, "vbbnames"+j+"").toString().trim():null;
									
					BillItem items = getBillCardPanelWrapper().getBillCardPanel().getBillModel().getItemByKey("vbbnames"+j);
					String vddate = items.getName().substring(0, 10);
					if(pk_bb!=null&&pk_bb.trim().length()>0){
						String[] values = pk_bb.split(",");
										
						for(int k=0;k<values.length;k++){
									

							// 查询二审有没有审核
							PaiBanAuditMsg2[] paibanmsg2 = (PaiBanAuditMsg2[])HYPubBO_Client.queryByCondition(PaiBanAuditMsg2.class, " vbillstatus='2' and vperiod='"+vdate+"' and isnull(dr,0)=0 and pk_corp='"+_getCorp().getPrimaryKey()+"' and pk_dept='"+pk_deptdoc+"' and pk_psndoc='"+pk_psndoc+"' and ddate='"+vddate+"' and pk_bb='"+values[k]+"'");
							if(paibanmsg2 != null){
								if(paibanmsg2.length >0){
									MessageDialog.showHintDlg(this.getBillUI(), "提示", "该数据已经二审,不能在一审退回!");
									//getBillUI().showHintMessage("该数据已经二审,不能在一审取消!");
									return;
								}
							}
							
							if(strmb.equals("")){
								// 1判断名称是否有门诊，２地点是否有门诊
								if(!getMzByName(values[k]).booleanValue() || !getMzByAddress(values[k]).booleanValue()){
									//HYPubBO_Client.deleteByWhereClause(PaiBanAuditMsg.class,"vperiod='"+vdate+"' and isnull(dr,0)=0 and pk_corp='"+_getCorp().getPrimaryKey()+"' and pk_dept='"+pk_deptdoc+"' and pk_psndoc='"+pk_psndoc+"' and ddate='"+vddate+"' and pk_bb='"+values[k]+"'");
									//HYPubBO_Client.deleteByWhereClause(PaiBanAuditMsg2.class,"vperiod='"+vdate+"' and isnull(dr,0)=0 and pk_corp='"+_getCorp().getPrimaryKey()+"' and pk_dept='"+pk_deptdoc+"' and pk_psndoc='"+pk_psndoc+"' and ddate='"+vddate+"' and pk_bb='"+values[k]+"'");
									PaiBanAuditMsg[] paibanmsg = (PaiBanAuditMsg[])HYPubBO_Client.queryByCondition(PaiBanAuditMsg.class, "vperiod='"+vdate+"' and isnull(dr,0)=0 and pk_corp='"+_getCorp().getPrimaryKey()+"' and pk_dept='"+pk_deptdoc+"' and pk_psndoc='"+pk_psndoc+"' and ddate='"+vddate+"' and pk_bb='"+values[k]+"'");
									if(paibanmsg != null){
										if(paibanmsg.length >0){
											for(int m=0;m < paibanmsg.length;m++){
												PaiBanAuditMsg pbmsg = paibanmsg[m];
												pbmsg.setVbillstatus(0);
												HYPubBO_Client.update(pbmsg);
											}
											
										}else{
											PaiBanAuditMsg msgvo = new PaiBanAuditMsg();
											msgvo.setAuditpsn(ClientEnvironment.getInstance().getUser().getUserName());
											msgvo.setDr(0);
											msgvo.setAudittime(ClientEnvironment.getServerTime());
											msgvo.setAudittype(0);
											msgvo.setPk_corp(_getCorp().getPrimaryKey());
											msgvo.setPk_dept(pk_deptdoc);
											msgvo.setPk_psndoc(pk_psndoc);
											msgvo.setPk_bb(values[k]);
											msgvo.setVbillstatus(0);
											msgvo.setVperiod(vdate);
											msgvo.setDdate(vddate);
											HYPubBO_Client.insert(msgvo);			
										}
									}
									
									PaiBanAuditMsg2[] paibanmsg1 = (PaiBanAuditMsg2[])HYPubBO_Client.queryByCondition(PaiBanAuditMsg2.class, "vperiod='"+vdate+"' and isnull(dr,0)=0 and pk_corp='"+_getCorp().getPrimaryKey()+"' and pk_dept='"+pk_deptdoc+"' and pk_psndoc='"+pk_psndoc+"' and ddate='"+vddate+"' and pk_bb='"+values[k]+"'");
									if(paibanmsg1 != null){
										if(paibanmsg1.length >0){
											for(int m=0;m < paibanmsg1.length;m++){
												PaiBanAuditMsg2 pbmsg = paibanmsg1[m];
												pbmsg.setVbillstatus(0);
												HYPubBO_Client.update(pbmsg);
											}
										}else{
											PaiBanAuditMsg2 msgvo2 = new PaiBanAuditMsg2();
											msgvo2.setAuditpsn(ClientEnvironment.getInstance().getUser().getUserName());
											msgvo2.setDr(0);
											msgvo2.setAudittime(ClientEnvironment.getServerTime());
											msgvo2.setAudittype(0);
											msgvo2.setPk_corp(_getCorp().getPrimaryKey());
											msgvo2.setPk_dept(pk_deptdoc);
											msgvo2.setPk_psndoc(pk_psndoc);
											msgvo2.setPk_bb(values[k]);
											msgvo2.setVbillstatus(0);
											msgvo2.setVperiod(vdate);
											msgvo2.setDdate(vddate);
											HYPubBO_Client.insert(msgvo2);
										}
									}
								}
							}else{
								// 门诊
								//HYPubBO_Client.deleteByWhereClause(PaiBanAuditMsg.class,"vperiod='"+vdate+"' and isnull(dr,0)=0 and pk_corp='"+_getCorp().getPrimaryKey()+"' and pk_dept='"+pk_deptdoc+"' and pk_psndoc='"+pk_psndoc+"' and ddate='"+vddate+"' and pk_bb='"+values[k]+"'");
								//HYPubBO_Client.deleteByWhereClause(PaiBanAuditMsg2.class,"vperiod='"+vdate+"' and isnull(dr,0)=0 and pk_corp='"+_getCorp().getPrimaryKey()+"' and pk_dept='"+pk_deptdoc+"' and pk_psndoc='"+pk_psndoc+"' and ddate='"+vddate+"' and pk_bb='"+values[k]+"'");
								PaiBanAuditMsg[] paibanmsg = (PaiBanAuditMsg[])HYPubBO_Client.queryByCondition(PaiBanAuditMsg.class, "vperiod='"+vdate+"' and isnull(dr,0)=0 and pk_corp='"+_getCorp().getPrimaryKey()+"' and pk_dept='"+pk_deptdoc+"' and pk_psndoc='"+pk_psndoc+"' and ddate='"+vddate+"' and pk_bb='"+values[k]+"'");
								if(paibanmsg != null){
									if(paibanmsg.length >0){
										for(int m=0;m < paibanmsg.length;m++){
											PaiBanAuditMsg pbmsg = paibanmsg[m];
											pbmsg.setVbillstatus(0);
											HYPubBO_Client.update(pbmsg);
										}
									}else{
										PaiBanAuditMsg msgvo = new PaiBanAuditMsg();
										msgvo.setAuditpsn(ClientEnvironment.getInstance().getUser().getUserName());
										msgvo.setDr(0);
										msgvo.setAudittime(ClientEnvironment.getServerTime());
										msgvo.setAudittype(0);
										msgvo.setPk_corp(_getCorp().getPrimaryKey());
										msgvo.setPk_dept(pk_deptdoc);
										msgvo.setPk_psndoc(pk_psndoc);
										msgvo.setPk_bb(values[k]);
										msgvo.setVbillstatus(0);
										msgvo.setVperiod(vdate);
										msgvo.setDdate(vddate);
										HYPubBO_Client.insert(msgvo);
									}
								}
								
								PaiBanAuditMsg2[] paibanmsg1 = (PaiBanAuditMsg2[])HYPubBO_Client.queryByCondition(PaiBanAuditMsg2.class, "vperiod='"+vdate+"' and isnull(dr,0)=0 and pk_corp='"+_getCorp().getPrimaryKey()+"' and pk_dept='"+pk_deptdoc+"' and pk_psndoc='"+pk_psndoc+"' and ddate='"+vddate+"' and pk_bb='"+values[k]+"'");
								if(paibanmsg1 != null){
									if(paibanmsg1.length >0){
										for(int m=0;m < paibanmsg1.length;m++){
											PaiBanAuditMsg2 pbmsg = paibanmsg1[m];
											pbmsg.setVbillstatus(0);
											HYPubBO_Client.update(pbmsg);
										}
									}else{
										PaiBanAuditMsg2 msgvo2 = new PaiBanAuditMsg2();
										msgvo2.setAuditpsn(ClientEnvironment.getInstance().getUser().getUserName());
										msgvo2.setDr(0);
										msgvo2.setAudittime(ClientEnvironment.getServerTime());
										msgvo2.setAudittype(0);
										msgvo2.setPk_corp(_getCorp().getPrimaryKey());
										msgvo2.setPk_dept(pk_deptdoc);
										msgvo2.setPk_psndoc(pk_psndoc);
										msgvo2.setPk_bb(values[k]);
										msgvo2.setVbillstatus(0);
										msgvo2.setVperiod(vdate);
										msgvo2.setDdate(vddate);
										HYPubBO_Client.insert(msgvo2);
									}
								}
							}
						
						}
						
					}
//				}
				}
			}
		
		}
		
		HYPubBO_Client.insertAry(list3.toArray(new PaiBanAuditReason[0]));
		
		// begin 公休退回		
		GxBVO[] gxvos = (GxBVO[])HYPubBO_Client.queryByCondition(GxBVO.class,
					" isnull(dr,0)=0 and vyear='"+begin.getYear()+"' and pk_corp='"+_getCorp().getPrimaryKey()+"' "+HRPPubTool.formInSQL("pk_psndoc", list_psn)+" ");
		if(gxvos!=null&&gxvos.length>0){
			for(GxBVO gxvo:gxvos){
				
				UFDouble nyygx = new UFDouble("0.00");
				UFDouble nsxgx = new UFDouble("0.00");
				UFDouble ncuryygx = new UFDouble("0.00");
				
				if(gxvo.getNlastyygx()!= null){
					nyygx =gxvo.getNlastyygx();
				}
				if(gxvo.getNlastsygx()!= null){
					nsxgx =gxvo.getNlastsygx();
				}
				if(gxvo.getNyygx() != null){
					ncuryygx = gxvo.getNyygx();
					
				}
				gxvo.setNyygx(ncuryygx.sub(nyygx));
				gxvo.setNsxgx(nsxgx.add(nyygx));
			}
		}
		HYPubBO_Client.updateAry(gxvos);
		// end
		
		// 发送消息
		sendMsg(deptmsglist.toArray(new String[0]),list3.toArray(new PaiBanAuditReason[0]));
		onBoRefresh();
		
		getButtonManager().getButton(IBillButton.Audit).setEnabled(false);
		getButtonManager().getButton(IBillButton.CancelAudit).setEnabled(false);
		getButtonManager().getButton(IBillButton.Edit).setEnabled(false);
		getBillUI().updateButtons();
		MessageDialog.showHintDlg(this.getBillUI(), "提示", "退回完成");
		getBillUI().showHintMessage("退回完成!");
	
	}

	public void sendMsg(String[] depts,PaiBanAuditReason[] reasons) throws BusinessException{
		 CommonMessageVO vo = new CommonMessageVO();
	     vo.setTitle("排班审核通知");
	   
	     vo.setSender(ClientEnvironment.getInstance().getUser().getPrimaryKey());   
	     
	     if(depts != null){
	    	 for(int i=0;i<depts.length;i++){
	    		 String reason = "";
	    		 for(int j=0;j<reasons.length;j++){
	    			 if(depts[i].equals(reasons[j].getPk_dept())){
	    				 reason = reason + reasons[j].getPsnname()+":" +reasons[j].getReason();
	    			 }
	    		 }
	    		 
		    	 UserNameObject[] user = getUserNameObjs(depts[i]);
		    	 vo.setMessageContent("该科室排班有问题，请尽快排班！"+reason);
		    	 vo.setReceiver(user);
			     IPFMessage pfs = (IPFMessage) NCLocator.getInstance().lookup(IPFMessage.class.getName());  
			     pfs.insertCommonMsg(vo);
		     }
	     }
	     
	}
	
	public UserNameObject[] getUserNameObjs(String pk_dept) throws BusinessException{
		UserVO[] uservos = getUserVOS(pk_dept);
		UserNameObject[] user = new UserNameObject[uservos.length];
		
		if(uservos != null){
			for(int i=0;i<uservos.length;i++){
				user[i] = new UserNameObject(uservos[i].getUserName());
			    user[i].setUserCode(uservos[i].getUserCode());
			    user[i].setUserName(uservos[i].getUserName());
			    user[i].setUserPK(uservos[i].getPrimaryKey());
			}
		}
		
		return user;
	}
	
	public UserVO[] getUserVOS(String pk_dept) throws BusinessException{
		IUAPQueryBS service = (IUAPQueryBS)NCLocator.getInstance().lookup(IUAPQueryBS.class.getName());
		String strSQL = "select sm_user.user_code,sm_user.user_name,sm_user.cuserid"
			+"  from bd_tr_userpower left join sm_user on bd_tr_userpower.pk_user=sm_user.cuserid"
			+" where pk_docid = '"+pk_dept+"'"
			+"   and powertype = '0'"
			+"   and pk_user is not null"
			+"   and sm_user.dr=0";
	
		UserVO[] uservos = null;
		
		Vector o1 = (Vector) service.executeQuery(strSQL,new VectorProcessor());
		if (o1.size() > 0 && o1 != null) {
			uservos = new UserVO[o1.size()];
			for (int i = 0; i < o1.size(); i++) {
				UserVO uservo = new UserVO();
				String user_code = new String(((Vector) o1.elementAt(i)).elementAt(0) != null ? ((Vector) o1.elementAt(i)).elementAt(0).toString() : null);
				String user_name = new String(((Vector) o1.elementAt(i)).elementAt(1) != null ? ((Vector) o1.elementAt(i)).elementAt(1).toString() : null);
				String cuserid = new String(((Vector) o1.elementAt(i)).elementAt(2) != null ? ((Vector) o1.elementAt(i)).elementAt(2).toString() : null);
 
				uservo.setUserCode(user_code);
				uservo.setUserName(user_name);
				uservo.setPrimaryKey(cuserid);
				
				uservos[i] = uservo;
				 
			}
		}
		
		return uservos;
	}
	
}
