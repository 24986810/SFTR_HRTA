package nc.ui.wa.wa_hrp_002;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;

import nc.bs.framework.common.NCLocator;
import nc.itf.uap.IUAPQueryBS;
import nc.itf.uap.IVOPersistence;
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
import nc.uif.pub.exception.UifException;
import nc.vo.hi.wadoc.PsndocWadocb2VO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFDate;
import nc.vo.wa.wa_hrp_002.PsnOutStaffVO;
import nc.vo.wa.wa_hrp_004.ItemSetBVO;


/**
 * ������Ա
 * @author zhanghua
 * 
 */
public class OutStaffDLG extends UIDialog implements ActionListener{
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
	
	private String m_pk_dept = null;
	
	private String m_psnname = null;
	
	private PsnOutStaffVO psnoutstaffvo = null;
    /**
     *  
     */
    public OutStaffDLG(Container cont,String pk_dept,String psnname) {
		super(cont);
		m_pk_dept = pk_dept;
		m_psnname = psnname;
		init();
	}
    
    public OutStaffDLG(Container cont,SuperVO[] infovos) {
		super(cont);
		m_infovos = infovos;
		init();
	}
    
    /**
     * ������:getBillListPanel ����:�б�����������
     */
    protected BillListPanel getBillListPanel() {
        if (billList == null) {
            billList = new BillListPanel();
        }
        return billList;
    }

    protected UIButton getBtnPrint() {
        if (btnPrint == null){
        	btnPrint = new UIButton("����");
        	btnPrint.setName("BtnPrint");
        	btnPrint.setText("����");
        }
        return btnPrint;
    }

    protected UIButton getBtnClose() {
        if (btnClose == null) {
        	btnClose = new UIButton("�ر�");
        	btnClose.setName("BtnClose");
        	btnClose.setText("�ر�");
        }
        return btnClose;
    }

    /**
     *  
     */
    private void init() {
        this.setTitle("������Ա");
        this.setSize(553, 375);
        //	    this.setSize(((Container) this.getParent()).getWidth() - 80,
        //				((Container) this.getParent()).getHeight() - 80);
        this.initPanel();
        //===================BillListPanel��������=======================
        try {
            this.billCardLoadData(m_pk_dept,m_psnname);
        } catch (Exception e) {
            nc.ui.pub.beans.MessageDialog.showErrorDlg(this, "����", e
                    .getMessage());
            return;
        }
        //===================���ؼ���ӱ�����Ҫ�ļ�����=======================
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
        try {
			panel.add(this.getBillCardPanel());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
       // this.billListLoadTemplet(); //BillListPanel����ģ��
        getContentPane().add(panel, BorderLayout.CENTER);
        
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
			m_billCardPanel.loadTemplet("0001AA1000000010RKW0");
		}
		return m_billCardPanel;
	}
    
    /**
     * //����ģ��
     */
    private void billListLoadTemplet() {
        try {
            getBillListPanel().loadTemplet("0001ZZ1000000007NK0Y");
        } catch (java.lang.Exception e) {
        	e.printStackTrace(System.out);
            MessageDialog.showErrorDlg(this, "��ʾ", "��ǰ������û�п��õ�ģ��");
            return;
        }
    }
    
    public void billListLoadData( SuperVO[] infovos ) throws Exception {
    	if(infovos != null){
    		 getBillListPanel().setBodyValueVO(infovos);
    		 getBillListPanel().getBodyBillModel().execLoadFormula();
    	}else
    		getBillListPanel().getBodyBillModel().clearBodyData();
    }
    
    public void billCardLoadData(String pk_dept,String psnname) throws Exception {
    	
    	UFDate currdate = ClientEnvironment.getInstance().getDate();
		getBillCardPanel().getHeadItem("dmakedate").setValue(currdate);
		getBillCardPanel().getHeadItem("psncode").setValue(getVbillNo());
		getBillCardPanel().getHeadItem("pk_dept").setValue(pk_dept);
		getBillCardPanel().getHeadItem("psnname").setValue(psnname);
		
//    	if(pk_psndoc != null){
//    		PsndocWadocb2VO wadocb2VO = getPsndocWadocb2VO(pk_psndoc);
//    		getBillCardPanel().getBillData().setHeaderValueVO(wadocb2VO);
//    	}else{
//    		UFDate currdate = ClientEnvironment.getInstance().getDate();
//    		getBillCardPanel().getHeadItem("dmakedate").setValue(currdate);
//    	}
    }
    
    public PsnOutStaffVO getPsnOutStaffVO(String psnname) throws BusinessException{
    	IUAPQueryBS service = NCLocator.getInstance().lookup(IUAPQueryBS.class);
		String sql="select * from hi_psndoc_wadoc_b2 where isnull(dr,0)=0 and pk_psndoc='"+psnname+"'";
		PsnOutStaffVO psnVO = (PsnOutStaffVO) service.executeQuery(sql, new BeanProcessor(PsnOutStaffVO.class)); 
    	return psnVO;
    }
    
    
    public String getVbillNo() throws UifException{
    	String sql = " dr=0 ";
    	String billno = "";
    	PsnOutStaffVO[] staffvos = (PsnOutStaffVO[])HYPubBO_Client.queryByCondition(PsnOutStaffVO.class,sql);
    	if(staffvos != null){
    		billno = String.format("%04d", staffvos.length+1);
    	}
    	return "XW"+billno;
    }
    
    /*
     * ȷ����ȡ�� ��ť�¼�����
     */
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == btnPrint)
            onBtnPrint();
        else if (ae.getSource() == btnClose)
        	onBtnClose();
    }

    private void onBtnPrint() {

    	try {
    		// ����У��
    		PsnOutStaffVO headvo = (PsnOutStaffVO) getBillCardPanel().getBillData().getHeaderValueVO("nc.vo.wa.wa_hrp_002.PsnOutStaffVO");
    		if(headvo.getPsnname() == null){
    			MessageDialog.showErrorDlg(this, "��ʾ", "��������Ϊ��!");
                return;
    		}
            
    		IVOPersistence service = (IVOPersistence)NCLocator.getInstance().lookup(IVOPersistence.class.getName());
    		headvo.setDr(0);
    		String pk = service.insertVO(headvo);
    		headvo.setPk_outstaff(pk);
    		setPsnoutstaffvo(headvo);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
        this.closeOK();
//        this.dispose();
    }

    /**
     * ������:onBtnCancel ����:"ȡ��"��ť����ʱ����Ӧ����
     */
    public void onBtnClose() {
        setResult(ID_CANCEL);
        close();
        fireUIDialogClosed(new UIDialogEvent(this, UIDialogEvent.WINDOW_CANCEL));
        this.dispose();
    }

  

   

	public PsnOutStaffVO getPsnoutstaffvo() {
		return psnoutstaffvo;
	}

	public void setPsnoutstaffvo(PsnOutStaffVO psnoutstaffvo) {
		this.psnoutstaffvo = psnoutstaffvo;
	}

	@Override
	public void closeOK() {
		// TODO Auto-generated method stub
		super.closeOK();
	}
    
    
}