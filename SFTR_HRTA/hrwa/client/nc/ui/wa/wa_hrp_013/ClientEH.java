package nc.ui.wa.wa_hrp_013;

import java.util.ArrayList;
import java.util.List;

import javax.swing.tree.TreePath;

import nc.bs.framework.common.NCLocator;
import nc.itf.hrp.pub.IConstant;
import nc.itf.hrp.pub.pub03.IRoleOrUserAtdTypeAuth;
import nc.itf.hrwa.IHRwaPub;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.trade.base.IBillOperate;
import nc.ui.trade.bill.ICardController;
import nc.ui.trade.pub.TableTreeNode;
import nc.ui.trade.pub.VOTreeNode;
import nc.ui.trade.treecard.BillTreeCardUI;
import nc.ui.trade.treecard.TreeCardEventHandler;
import nc.vo.wa.wa_hrp_009.UserDeptVO;
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
		List list = new ArrayList();
		
		for(int i = 0 ; i < cnt ; i++){
			Object temp = getBillCardPanelWrapper().getBillCardPanel().getBodyValueAt(i, "flag"); 
			if(temp != null && temp.toString().equals("true")){
				String btt = getBillCardPanelWrapper().getBillCardPanel().getBodyValueAt(i, "pk_perioddept").toString();
				UserDeptVO ucavo = new UserDeptVO();
				ucavo.setPk_deptdoc(btt);
				//ucavo.setPk_corp(_getCorp().getPrimaryKey());
				ucavo.setPk_corp(corp);
				ucavo.setPk_module(pk_module);
				if(vflag == 0 ){
					ucavo.setPk_role(tempid);
				}
				else if(vflag == 1 ){
					ucavo.setPk_user(tempid);
				}
				list.add(ucavo);
			}
		}
	
		//回写数据库
		IHRwaPub irewrite = (IHRwaPub)NCLocator.getInstance().lookup(IHRwaPub.class);
		irewrite.setRoleOrUserAtdTypeAuth(tempid,list,vflag,pk_module,_getCorp().getPrimaryKey());
		getBillUI().setBillOperate(IBillOperate.OP_NOTEDIT);
		
	}
}
