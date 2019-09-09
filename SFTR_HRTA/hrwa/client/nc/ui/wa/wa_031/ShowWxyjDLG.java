package nc.ui.wa.wa_031;

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
import nc.vo.hi.wadoc.PsndocWadocMainVO;
import nc.vo.hi.wadoc.PsndocWadocVO;
import nc.vo.hi.wadoc.PsndocWadocb2VO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFDouble;


/**
 * @author zhanghua
 * 
 */
public class ShowWxyjDLG extends UIDialog implements ActionListener{
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
	
	private String m_pk_psndoc = null;
	
	private String m_psnclassname = null;
    /**
     *  
     */
    public ShowWxyjDLG(Container cont,String pk_psndoc,String psnclassname) {
		super(cont);
		m_pk_psndoc = pk_psndoc;
		m_psnclassname = psnclassname;
		init();
	}
    
   /* public WXYJDLG(Container cont,SuperVO[] infovos) {
		super(cont);
		m_infovos = infovos;
		init();
	}*/
    
    /**
     * ������:getBillListPanel ����:�б�����������
     */
    protected BillListPanel getBillListPanel() {
        if (billList == null) {
            billList = new BillListPanel();
        }
        return billList;
    }


    /**
     *  
     */
    private void init() {
    	
    	 this.setTitle("����һ��");
         this.setSize(300, 175);
         this.initPanel();
        //===================BillListPanel��������=======================
        try {
            this.billCardLoadData(m_pk_psndoc ,m_psnclassname);
        } catch (Exception e) {
            nc.ui.pub.beans.MessageDialog.showErrorDlg(this, "����", e
                    .getMessage());
           // nc.ui.pub.beans.MessageDialog.showErrorDlg(this, "����", "δά���籣������");
            return;
        }
        
       
        //===================���ؼ���ӱ�����Ҫ�ļ�����=======================
    	//getBtnPrint().addActionListener(this);
    	//getBtnClose().addActionListener(this);
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
       
      //  btnPanel.add(this.getBtnPrint());
      //  btnPanel.add(this.getBtnClose());
       
        getContentPane().add(btnPanel, BorderLayout.SOUTH);
    }

    
    private BillCardPanel getBillCardPanel() throws Exception {
		if (m_billCardPanel == null) {
			m_billCardPanel = new BillCardPanel();
			m_billCardPanel.setName("BillCardPanel");
			m_billCardPanel.loadTemplet("0001AA1000000010XMH6");
		}
		return m_billCardPanel;
	}
    
  /*  *//**
     * //����ģ��
     *//*
    private void billListLoadTemplet() {
        try {
            getBillListPanel().loadTemplet("0001ZZ1000000007NK0Y");
        } catch (java.lang.Exception e) {
        	e.printStackTrace(System.out);
            MessageDialog.showErrorDlg(this, "��ʾ", "��ǰ������û�п��õ�ģ��");
            return;
        }
    }*/
    
   /* public void billListLoadData( SuperVO[] infovos ) throws Exception {
    	if(infovos != null){
    		 getBillListPanel().setBodyValueVO(infovos);
    		 getBillListPanel().getBodyBillModel().execLoadFormula();
    	}else
    		getBillListPanel().getBodyBillModel().clearBodyData();
    }*/
    
    public void billCardLoadData(String pk_psndoc,String psnclassname) throws Exception {
    	if(pk_psndoc != null){
    		PsndocWadocVO psndocWadocVo = getPsndocWadocVO(pk_psndoc);
    		UFDouble nmoney = psndocWadocVo.getNmoney();
    		WuXianYiJinVO wxyj = new WuXianYiJinVO();
    		wxyj.setGJJ(new UFDouble((nmoney.getDouble()*0.07)));
    		wxyj.setSYJ(new UFDouble((nmoney.getDouble()*0.005)+0.04));
    		wxyj.setYBJ(new UFDouble((nmoney.getDouble()*0.02)+0.04));
    		wxyj.setYLJ(new UFDouble((nmoney.getDouble()*0.08)+0.04));
    		if(psnclassname.equals("��ǲ") || psnclassname.equals("����") || psnclassname.equals("�����վ")){
    			wxyj.setZYNJ(new UFDouble(0));
    		}else{
    			wxyj.setZYNJ(new UFDouble((nmoney.getDouble()*0.04)+0.04));
    		}
    		
    		getBillCardPanel().getBillData().setHeaderValueVO(wxyj);
    	}else{
    		getBillCardPanel().getBillData().setHeaderValueVO(null);
    	}
    }
    
    public PsndocWadocVO getPsndocWadocVO(String pk_psndoc) throws BusinessException{
    	IUAPQueryBS service = NCLocator.getInstance().lookup(IUAPQueryBS.class);
		String sql="select * from hi_psndoc_wadoc where isnull(dr,0)=0 and pk_wa_item='10028L1000000001VW2H' and lastflag='Y' and pk_psndoc='"+pk_psndoc+"'";
		PsndocWadocVO psndocWadocVo = (PsndocWadocVO) service.executeQuery(sql, new BeanProcessor(PsndocWadocVO.class)); 
    	return psndocWadocVo;
    }
    
     // ȷ����ȡ�� ��ť�¼�����
     
    public void actionPerformed(ActionEvent ae) {
       /* if (ae.getSource() == btnPrint)
            onBtnPrint();
        else if (ae.getSource() == btnClose)
        	onBtnClose();*/
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

  

   

	@Override
	public void closeOK() {
		// TODO Auto-generated method stub
		super.closeOK();
	}
    
    
}