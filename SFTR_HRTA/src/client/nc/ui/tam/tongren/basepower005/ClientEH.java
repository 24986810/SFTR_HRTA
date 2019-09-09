package nc.ui.tam.tongren.basepower005;

import java.util.ArrayList;

import javax.swing.tree.TreePath;

import nc.bs.framework.common.NCLocator;
import nc.itf.hr.ta.IBclbDefining;
import nc.itf.hrp.pub.IConstant;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.trade.base.IBillOperate;
import nc.ui.trade.bill.ICardController;
import nc.ui.trade.pub.TableTreeNode;
import nc.ui.trade.pub.VOTreeNode;
import nc.ui.trade.treecard.BillTreeCardUI;
import nc.ui.trade.treecard.TreeCardEventHandler;
import nc.vo.tam.tongren.power.UserClassDeptVO;
import nc.vo.tam.tongren.power.UserClassTypeVO;
import nc.vo.tam.tongren.power.UserDeptVO;
/**
 * @author admin
 * 
 */
public class ClientEH extends TreeCardEventHandler {

	String billtype = "";

	

	@Override
	protected void onBoCancel() throws Exception {
		// TODO Auto-generated method stub
		updateButton(true);
		super.onBoCancel();
		((ClientUI)this.getBillTreeCardUI()).onTreeSelectSetButtonState(this.getBillTreeCardUI().getBillTreeSelectNode());
		
	}

	@Override
	public void onTreeSelected(VOTreeNode selectnode) {
	}

	@Override
	protected void onBoEdit() throws Exception {
		updateButton(false);
		if(((ClientUI)this.getBillTreeCardUI()).getBillTreeSelectNode()==null){
			MessageDialog.showHintDlg(this.getBillTreeCardUI(),"提示","请选中左侧树");
			return;
		}
		this.getBillUI().setBillOperate(nc.ui.trade.base.IBillOperate.OP_EDIT);
		
	}

	public void updateButton(boolean bool){
		((ClientUI)getBillUI()).getRefStrategy().setEnabled(bool);
	}
	public ClientEH(BillTreeCardUI billUI, ICardController control) {
		super(billUI, control);
	}
	
	@Override
	protected void onBoSave() throws Exception {
		updateButton(true);
		
		TreePath[] tpths = ((ClientUI)getBillUI()).getTpths();
		String corp = ((ClientUI)getBillUI()).getRefStrategy().getRefPK();
		String pk_module = ((ClientUI)getBillUI()).getRefModule().getRefPK();
		StringBuffer whesql = new StringBuffer();
		
		TableTreeNode node = (TableTreeNode)tpths[0].getLastPathComponent();
		String parentid   = node.getParentnodeID().toString();
		int vflag  = 0 ;
		if(parentid.equals(IConstant.RoleFlag)){
			vflag = 0 ;
		}
		else if(parentid.equals(IConstant.UserFlag)){
			vflag = 1 ; 
		}
		String tempid = node.getNodeID().toString();

		int cnt = getBillCardPanelWrapper().getBillCardPanel().getRowCount();
		ArrayList<UserClassTypeVO> list = new ArrayList<UserClassTypeVO>();
		
		for(int i = 0 ; i < cnt ; i++){
			Object temp = getBillCardPanelWrapper().getBillCardPanel().getBodyValueAt(i, "flag"); 
			if(temp != null && temp.toString().equals("true")){
				String btt = getBillCardPanelWrapper().getBillCardPanel().getBodyValueAt(i, "pk_hrp_classtype").toString();
				UserClassTypeVO ucavo = new UserClassTypeVO();
				ucavo.setPk_docid(btt);
				//ucavo.setPk_corp(_getCorp().getPrimaryKey());
				ucavo.setPk_corp(corp);
				ucavo.setPk_module(pk_module);
				ucavo.setPowertype(0);
				if(vflag == 0 ){
					ucavo.setPk_role(tempid);
				}
				else if(vflag == 1 ){
					ucavo.setPk_user(tempid);
				}
				list.add(ucavo);
			}
		}
	
		if(list.size() > 1){
			MessageDialog.showHintDlg(this.getBillTreeCardUI(),"提示","只能分配一个权限！");
			return;
		}
		//回写数据库
		IBclbDefining irewrite = (IBclbDefining)NCLocator.getInstance().lookup(IBclbDefining.class);
		irewrite.setClassRoleOrUserAtdClassTypeAuth(tempid,list,vflag,pk_module,_getCorp().getPrimaryKey(),0);
		getBillUI().setBillOperate(IBillOperate.OP_NOTEDIT);
		
	}
}
