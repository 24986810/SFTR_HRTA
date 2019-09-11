package nc.ui.tam.tongren.basepower005;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

import nc.bs.framework.common.NCLocator;
import nc.itf.hr.ta.IBclbDefining;
import nc.itf.uap.IUAPQueryBS;
import nc.itf.uap.IVOPersistence;
import nc.jdbc.framework.processor.ArrayListProcessor;
import nc.jdbc.framework.processor.BeanListProcessor;
import nc.jdbc.framework.processor.BeanProcessor;
import nc.ui.pub.ClientEnvironment;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UIButton;
import nc.ui.pub.beans.UIDialog;
import nc.ui.pub.beans.UIDialogEvent;
import nc.ui.pub.beans.UIPanel;
import nc.ui.pub.bill.BillCardPanel;
import nc.ui.pub.bill.BillData;
import nc.ui.pub.bill.BillListPanel;
import nc.ui.trade.business.HYPubBO_Client;
import nc.vo.hi.wadoc.PsndocWadocb2VO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;
import nc.vo.tam.tongren.power.UserClassTypeBVO;
import nc.vo.tam.tongren003.PanbanWeekBVO;
import nc.vo.tbm.tbm_029.BclbHeaderVO;


/**
 * 审核班别权限明细
 * @author zhanghua
 * 
 */
public class BasePowerDLG extends UIDialog implements ActionListener{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private BillListPanel billList;

    private UIButton btnPrint;

    private UIButton btnClose;

    private SuperVO[] m_infovos = null;
    
    private BillCardPanel m_billCardPanel = null;

	private String m_billType = null;
	
	private String m_nodeKey = null;
	
	private String m_pk_user = null;
	
	private String m_pk_hrp_classtype = null;
    /**
     *  
     */
    public BasePowerDLG(Container cont,String pk_user,String pk_hrp_classtype) {
		super(cont);
		m_pk_user = pk_user;
		m_pk_hrp_classtype = pk_hrp_classtype;
		init();
	}
    
    public BasePowerDLG(Container cont,SuperVO[] infovos) {
		super(cont);
		m_infovos = infovos;
		init();
	}
    
    /**
     * 方法名:getBillListPanel 作用:列表表体加载数据
     */
    protected BillListPanel getBillListPanel() {
        if (billList == null) {
            billList = new BillListPanel();
        }
        return billList;
    }

    protected UIButton getBtnPrint() {
        if (btnPrint == null){
        	btnPrint = new UIButton("保存");
        	btnPrint.setName("BtnPrint");
        	btnPrint.setText("保存");
        }
        return btnPrint;
    }

    protected UIButton getBtnClose() {
        if (btnClose == null) {
        	btnClose = new UIButton("关闭");
        	btnClose.setName("BtnClose");
        	btnClose.setText("关闭");
        }
        return btnClose;
    }

    /**
     *  
     */
    private void init() {
        this.setTitle("班别信息");
        this.setSize(553, 375);
        //	    this.setSize(((Container) this.getParent()).getWidth() - 80,
        //				((Container) this.getParent()).getHeight() - 80);
        this.initPanel();
        //===================BillListPanel加载数据=======================
        try {
            this.billListLoadData(m_pk_user,m_pk_hrp_classtype);
        } catch (Exception e) {
            nc.ui.pub.beans.MessageDialog.showErrorDlg(this, "错误", e
                    .getMessage());
            return;
        }
        //===================各控件添加本身需要的监听器=======================
    	getBtnPrint().addActionListener(this);
    	getBtnClose().addActionListener(this);
    }

    /**
     *  
     */
    private void initPanel() {
        getContentPane().setLayout(new BorderLayout());
        UIPanel panel = new UIPanel();
//        panel.setPreferredSize(new Dimension(50, 30));
        panel.setLayout(new BorderLayout());
//        try {
//			panel.add(this.getBillCardPanel());
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
        this.billListLoadTemplet(); //BillListPanel加载模板
        getContentPane().add(billList, BorderLayout.CENTER);
        
        UIPanel btnPanel = new UIPanel();
        btnPanel.setLayout(new FlowLayout());
       
        btnPanel.add(this.getBtnPrint());
        btnPanel.add(this.getBtnClose());
       
        getContentPane().add(btnPanel, BorderLayout.SOUTH);
    }

    
    private BillCardPanel getBillCardPanel() throws Exception {
		if (m_billCardPanel == null) {
			m_billCardPanel = new BillCardPanel();
			m_billCardPanel.setName("BillCardPanel");
			m_billCardPanel.loadTemplet("0001AA1000000010MOYC");
		}
		return m_billCardPanel;
	}
    
    /**
     * //加载模板
     */
    private void billListLoadTemplet() {
        try {
            getBillListPanel().loadTemplet("0001AA100000001101NZ");
        } catch (java.lang.Exception e) {
        	e.printStackTrace(System.out);
            MessageDialog.showErrorDlg(this, "提示", "当前操作人没有可用的模板");
            return;
        }
    }
    
    
    
    public void billListLoadData(String pk_user,String pk_hrp_classtype) throws Exception {
    	if(pk_user != null){
    		//PsndocWadocb2VO wadocb2VO = getPsndocWadocb2VO(pk_psndoc);
    		
    		IBclbDefining defin = NCLocator.getInstance().lookup(IBclbDefining.class);
    		BclbHeaderVO[] bclbvos = defin.queryBclb029AllBclbHeader("1002", null);
    		
    		getBillListPanel().getBodyItem("flag").setEnabled(true);
    		getBillListPanel().setBodyValueVO(bclbvos);
    		getBillListPanel().getBodyBillModel().execLoadFormula();
    		
    		ArrayList<UserClassTypeBVO> list = getClassDetail(pk_user,pk_hrp_classtype);
    		int rowcount = this.getBillListPanel().getBodyBillModel().getRowCount();
    		for(int i=0;i<rowcount;i++){
    			String pk_bclbid = (String) getBillListPanel().getBodyBillModel().getValueAt(i, "pk_bclbid");
    			for(UserClassTypeBVO bvo:list){
    				
    				if(bvo.getPk_bclbid().equals(pk_bclbid)){
        				getBillListPanel().getBodyBillModel().setValueAt("Y", i, "flag");
        			}
    			}
    			
    		}
    		
    	}else{
    		getBillCardPanel().getBillData().setHeaderValueVO(null);
    	}
    }
    
    public ArrayList<UserClassTypeBVO> getClassDetail(String pk_user,String pk_hrp_classtype) throws BusinessException{
    	IUAPQueryBS service = NCLocator.getInstance().lookup(IUAPQueryBS.class);
    	String sql = "select * from trtam_classtype_kq_b where pk_user='"+pk_user+"' and pk_hrp_classtype='"+pk_hrp_classtype+"' and isnull(dr,0)=0";
    	ArrayList<UserClassTypeBVO> list = (ArrayList<UserClassTypeBVO>) service.executeQuery(sql, new BeanListProcessor(UserClassTypeBVO.class));
    	
    	return list;
    }
    
  
    /*
     * 确定、取消 按钮事件处理
     */
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == btnPrint)
            onBtnPrint();
        else if (ae.getSource() == btnClose)
        	onBtnClose();
    }

    private void onBtnPrint() {

    	try {
    		BclbHeaderVO[] bclbvos = (BclbHeaderVO[]) getBillListPanel().getBodyBillModel().getBodyValueVOs("nc.vo.tbm.tbm_029.BclbHeaderVO");
    		
    		ArrayList<BclbHeaderVO> list = new ArrayList<BclbHeaderVO>();
    		ArrayList<UserClassTypeBVO> listb = new ArrayList<UserClassTypeBVO>();
    		
    		for(int i=0;i<bclbvos.length;i++){
    			Object temp = getBillListPanel().getBodyBillModel().getValueAt(i, "flag"); 
    			if(temp != null && temp.toString().equals("true")){
    				BclbHeaderVO bclbheadvo = (BclbHeaderVO) getBillListPanel().getBodyBillModel().getBodyValueRowVO(i, "nc.vo.tbm.tbm_029.BclbHeaderVO");
    				list.add(bclbheadvo);
    			}
    		}
    		
    		HYPubBO_Client.deleteByWhereClause(UserClassTypeBVO.class, " pk_user ='"+m_pk_user+"' and pk_hrp_classtype='"+m_pk_hrp_classtype+"'");
    		
    		for(int i=0;i<list.size();i++){
    			UserClassTypeBVO bvo = new UserClassTypeBVO();
    			bvo.setPk_user(m_pk_user);
    			bvo.setPk_hrp_classtype(m_pk_hrp_classtype);
    			bvo.setPk_bclbid(list.get(i).getPk_bclbid());
    			bvo.setDr(0);
    			
    			listb.add(bvo);
    			
    		}
    		    		
    		HYPubBO_Client.insertAry(listb.toArray(new UserClassTypeBVO[0]));
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
        this.closeOK();
//        this.dispose();
    }

    /**
     * 方法名:onBtnCancel 作用:"取消"按钮单击时间相应方法
     */
    public void onBtnClose() {
        setResult(ID_CANCEL);
        close();
        fireUIDialogClosed(new UIDialogEvent(this, UIDialogEvent.WINDOW_CANCEL));
        this.dispose();
    }

  

   

	@Override
	public void closeOK() {
		// TODO Auto-generated method stub
		super.closeOK();
	}
    
    
}