package nc.ui.tam.tongren.basepower005;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;

import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import nc.bs.framework.common.NCLocator;
import nc.itf.hrp.pub.IConstant;
import nc.itf.uap.IUAPQueryBS;
import nc.jdbc.framework.processor.BeanListProcessor;
import nc.ui.hrp.pub.ref.CorpTreeRef;
import nc.ui.hrp.pub.ref.NcModuleRef;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.RefEditEvent;
import nc.ui.pub.beans.RefEditListener;
import nc.ui.pub.beans.UILabel;
import nc.ui.pub.beans.UIPanel;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.beans.ValueChangedEvent;
import nc.ui.pub.beans.ValueChangedListener;
import nc.ui.pub.linkoperate.ILinkQuery;
import nc.ui.pub.linkoperate.ILinkQueryData;
import nc.ui.trade.base.IBillOperate;
import nc.ui.trade.bill.ICardController;
import nc.ui.trade.bsdelegate.BusinessDelegator;
import nc.ui.trade.business.HYPubBO_Client;
import nc.ui.trade.button.IBillButton;
import nc.ui.trade.card.CardEventHandler;
import nc.ui.trade.pub.IVOTreeData;
import nc.ui.trade.pub.TableTreeNode;
import nc.ui.trade.treecard.BillTreeCardUI;
import nc.uif.pub.exception.UifException;
import nc.vo.pub.SuperVO;
import nc.vo.tam.tongren.power.ClassTypeKqVO;
import nc.vo.tam.tongren.power.UserClassDeptVO;
import nc.vo.tam.tongren.power.UserClassTypeVO;
import nc.vo.tam.tongren.power.UserDeptVO;
import nc.vo.tam.tongren001.DeptKqVO;
import nc.vo.trade.pub.HYBillVO;
import nc.vo.uap.rbac.RoleVO;

/**
 * @author admin
 * 
 */
public class ClientUI extends BillTreeCardUI implements ILinkQuery,ItemListener,RefEditListener,ValueChangedListener {

	public boolean beforeEdit(RefEditEvent event) {
		return true;
	}

	public void itemStateChanged(ItemEvent arg0) {
	}

	private UIPanel showSealDataPanel = null;
	private UIRefPane RefStrategy = null;// ����ս��
	private UIRefPane RefModule = null;// ģ�����
	
	@Override
	protected IVOTreeData createTableTreeData() {
		return null;
	}

	@Override
	public String getRefBillType() {
		return null;
	}

	public ClientUI() {
		super();
		getButtonManager().getButton(IBillButton.Copy).setName("���Ȩ�޷���");
		
		initlize();//��ӹ�˾ѡ���
		this.getBillTree().getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
		initBufferData(getBodysVO());
	}
	
	private void initlize() {
		try {
			
			//�����Ƿ���ʾ�������Panel
			addSealdataPanel();
			initLangData();
			setBillOperate(nc.ui.trade.base.IBillOperate.OP_INIT);
			getButtonManager().getButton(IBillButton.Copy).setEnabled(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	//�ڵ��ݱ�ͷ�ϼ���һ�� panel  �Լ����Ŷ���
	public void addSealdataPanel() {
		add(getSealdataPanel(),BorderLayout.NORTH);
	}
	
	private UIPanel getSealdataPanel() {
    	if(showSealDataPanel==null) {
    		showSealDataPanel=new UIPanel();
    		showSealDataPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
    		showSealDataPanel.add(new UILabel("��˾"));
			showSealDataPanel.add(getRefStrategy());//��˾����
			
			showSealDataPanel.add(new UILabel("��Ӧģ��"));
			showSealDataPanel.add(getRefModule());
    	}
    	return showSealDataPanel;
	 }
	
	private void initLangData(){
		
		//����ս�Բ��ն���Ĭ��ֵ
		//RefStrategy.setRefModel(new nc.ui.bd.ref.busi.CorpDefaultRefModel("��˾����"));
		RefStrategy.setRefModel(new CorpTreeRef());
		
		RefStrategy.addRefEditListener(this);
		RefStrategy.setSelectedData(this._getCorp().getPrimaryKey(),this._getCorp().getUnitcode(),this._getCorp().getUnitname());
		RefStrategy.setEnabled(false);
		
		RefModule.setPK("kq");
		RefModule.addRefEditListener(this);
		RefModule.setEnabled(false);
		
		
	}
	
	
	 /**��˾����*/
	 public UIRefPane getRefStrategy() {
		 if(RefStrategy==null) {
			 RefStrategy=new UIRefPane();
			 //RefStrategy.setRefModel(new nc.ui.bd.ref.busi.CorpDefaultRefModel("��˾����"));
			 RefStrategy.setRefModel(new CorpTreeRef());
//			 RefStrategy.setPreferredSize(new Dimension(100,20));
			 RefStrategy.addValueChangedListener(this);
			 RefStrategy.setButtonFireEvent(true);
			 RefStrategy.setEditable(false);
			 RefStrategy.setEnabled(false);
			 //RefStrategy.add
		 }
		 return RefStrategy;
	 }
	 /**ģ�����*/
	 public UIRefPane getRefModule() {
		 if(RefModule==null) {
			 RefModule=new UIRefPane();
			 RefModule.setRefModel(new NcModuleRef());
//			 RefModule.setPreferredSize(new Dimension(100,20));
			 RefModule.addValueChangedListener(this);
			 RefModule.setButtonFireEvent(true);
			 
			 //RefStrategy.add
		 }
		 return RefModule;
	 }
	
	public SuperVO[] getBodysVO() {
		// TODO �Զ����ɷ������
		try {
			return HYPubBO_Client.queryByCondition(ClassTypeKqVO.class, " isnull(dr,0)=0 and pk_corp='"+_getCorp().getPrimaryKey()+"' order by vcode ");
		} catch (UifException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	private void initBufferData(SuperVO[] queryVos) {

		try {
			// ��ջ�������
			getBufferData().clear();
			if (queryVos != null && queryVos.length != 0) {
				HYBillVO billvo = new HYBillVO();
				billvo.setParentVO(null);
				billvo.setChildrenVO(queryVos);
				getBufferData().addVOToBuffer(billvo);
				getBufferData().setCurrentRow(0);
				getBufferData().setCurrentVO(billvo);
			} else {
				setListHeadData(null);
				getBufferData().setCurrentRow(-1);
				setBillOperate(IBillOperate.OP_INIT);
			}
		} catch (java.lang.Exception e) {
			System.out.println("���û�������ʧ�ܣ�");
			e.printStackTrace();
		}
	}

	/**
	 * @param pk_corp
	 * @param pk_billType
	 * @param pk_busitype
	 * @param operater
	 * @param billId
	 */
	public ClientUI(String pk_corp, String pk_billType, String pk_busitype,
			String operater, String billId) {
		super(pk_corp, pk_billType, pk_busitype, operater, billId);
	}

	public void afterInit() throws Exception {
		modifyRootNodeShowName("�û��б�");
		this.getBillTree().setRootVisible(false);
	}

	@Override
	protected ICardController createController() {
		return new Ctrl();
	}

	@Override
	protected CardEventHandler createEventHandler() {
		return new ClientEH(this, createController());
	}

	@Override
	protected IVOTreeData createTreeData() {
		return new TreeData();
	}
	
	private TreePath[] tpths = null;

	@Override
	protected void onTreeSelectSetButtonState(TableTreeNode arg0) {
		if(arg0.getParent() == null){
			return;
		}
		
		//��ʼ���������
		int tempflag = this.getBillCardPanel().getBillTable().getRowCount();
		if(tempflag == 0 ){
			initBufferData(getBodysVO());
			if(this.getBillCardPanel().getRowCount() == 0 ) return ;
		}
		
		if(arg0.getNodeID().toString().equals(IConstant.RoleFlag) || arg0.getNodeID().toString().equals(IConstant.UserFlag) ){
			int nflag = this.getBillCardPanel().getRowCount();
			for (int i = 0; i < nflag; i++) {
				this.getBillCardPanel().setBodyValueAt(false, i, "flag");
			}
			getButtonManager().getButton(IBillButton.Edit).setEnabled(false);
			this.updateButtons();
			return;
		}
		
		//���ɫѡ�У�������
		tpths = this.getBillTree().getSelectionPaths();
		if(tpths == null) return;
		int cnt = tpths.length ;
		if(cnt > 1 ){  return;}//����ѡ��
		
		//����ѡ��
		StringBuffer whesql = new StringBuffer();
		
		try{
			String vflag = arg0.getParentnodeID().toString();
			String sql   = "";
			if(vflag.equals(IConstant.RoleFlag)){//��ɫ
				sql = " pk_role = '"+arg0.getNodeID().toString()+"' and pk_corp = '"+RefStrategy.getRefPK()+"' and  powertype =0 ";
			}
			if(vflag.equals(IConstant.UserFlag)){//�û�
				sql = " pk_user = '"+arg0.getNodeID().toString()+"' and pk_corp = '"+RefStrategy.getRefPK()+"' and  powertype =0 ";
			}
			//�ж� ��ɫ ���û�
			BusinessDelegator business = new BusinessDelegator();
			UserClassTypeVO[] authVOs = (UserClassTypeVO[])business.queryByCondition(UserClassTypeVO.class,sql);
			
			int nflag = this.getBillCardPanel().getRowCount();
			for (int i = 0; i < nflag; i++) {
				this.getBillCardPanel().setBodyValueAt(false, i, "flag");
				String code = this.getBillCardPanel().getBodyValueAt(i,"pk_hrp_classtype").toString();
				for(int j = 0 ; j < authVOs.length;j++){
					if(code.equals(authVOs[j].getPk_docid())&&getRefModule().getRefPK().equals(authVOs[j].getPk_module())){
						this.getBillCardPanel().setBodyValueAt(true, i, "flag");
					}
				}
			}
		} catch (Exception e) {e.printStackTrace();	}
		
		getButtonManager().getButton(IBillButton.Edit).setEnabled(true);
		this.updateButtons();
	}

	@Override
	public void setDefaultData() throws Exception {
		super.setDefaultData();
	}

	@Override
	protected void initSelfData() {
	}

	public TreePath[] getTpths() {
		return tpths;
	}

	public void setTpths(TreePath[] tpths) {
		this.tpths = tpths;
	}
	
	@Override
	protected void initPrivateButton() {
		super.initPrivateButton();
	}
	
	public void doQueryAction(ILinkQueryData querydata) {
	}

	public void valueChanged(ValueChangedEvent e) {
		
		Object sour = e.getSource();
		String mypk_corp ="";
//		if (sour.equals(RefStrategy)) {
			mypk_corp = RefStrategy.getRefPK();//�õ����յ�����  --����
			String sql = "select pk_role,role_code,role_name,'"+IConstant.RoleFlag+"' role_memo from sm_role where  " + " PK_CORP = '"+mypk_corp+"' " +
			" OR exists (select 1 from SM_ROLE_CORP_ALLOC b where b.pk_corp ='"+mypk_corp+"' and sm_role.pk_role = b.pk_role ) " +
			" union all " +
			" select cuserid,user_code,user_name,'"+IConstant.UserFlag+"' from sm_user where pk_corp = '"+mypk_corp+"'" +
			" union all select '"+IConstant.RoleFlag+"' pk_role,' ' role_code,'��ɫ�б�' role_name,'' role_memo from sm_role where rownum < 2 " +
			" union all  select '"+IConstant.UserFlag+"',' ','�û��б�','' from dual  ";
			
			IUAPQueryBS service = (IUAPQueryBS)NCLocator.getInstance().lookup(IUAPQueryBS.class.getName());
			try{
				ArrayList<RoleVO> list =(ArrayList<RoleVO>) service.executeQuery( sql,new BeanListProcessor(RoleVO.class));
				SuperVO[] queryVos = (RoleVO[])list.toArray(new RoleVO[0]);
				constructTree(queryVos);
				initBufferData(getBodysVO());
			}	
			catch(Exception e1){
			}
//		}
		int nflag = this.getBillCardPanel().getRowCount();
		for (int i = 0; i < nflag; i++) {
			this.getBillCardPanel().setBodyValueAt(false, i, "flag");
		}
	}

	public void constructTree(SuperVO[] treevos) throws Exception {
		
		((TreeData)getCreateTreeData()).setTreeVO(treevos);
		//��ջ�������
		//clearTreeSelect();
		//getBillTree().clearSelection();
		getTreeToBuffer().clear();
		createBillTree(getCreateTreeData());
		afterInit();
		this.getBillTree().setRootVisible(false);
	}

	@Override
	public void createBillTree(IVOTreeData treeData) {
		super.createBillTree(treeData);
	}

}
