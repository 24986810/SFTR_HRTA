package nc.ui.wa.wa_031;

import java.util.LinkedList;
import java.util.List;

import javax.swing.table.TableColumnModel;

import nc.ui.ml.NCLangRes;
import nc.ui.pub.beans.table.ColumnGroup;
import nc.ui.pub.beans.table.GroupableTableHeader;
import nc.ui.pub.bill.BillItem;
import nc.ui.pub.bill.BillModel;
import nc.ui.wabm.print.TableColResize;
import nc.vo.hi.hi_rpt.WaItemVO;
import nc.vo.hi.wadoc.PsndocWadocMainVO;

/**
 * �˴��������������� �������ڣ�(2004-6-4 11:35:45)
 * 
 * @author��Administrator
 */
public class PsnWadocMainPane extends nc.ui.pub.beans.UIPanel {
    /**
     * 
     */
    private static final long serialVersionUID = -8711804531661400794L;
    private nc.ui.pub.bill.BillScrollPane ivjBillPane = null;

    /**
     * PsnWadocMainPanel ������ע�⡣
     */
    public PsnWadocMainPane() {
        super();
        initialize();
    }

    /**
     * PsnWadocMainPanel ������ע�⡣
     * 
     * @param p0
     *            java.awt.LayoutManager
     */
    public PsnWadocMainPane(java.awt.LayoutManager p0) {
        super(p0);
    }

    /**
     * PsnWadocMainPanel ������ע�⡣
     * 
     * @param p0
     *            java.awt.LayoutManager
     * @param p1
     *            boolean
     */
    public PsnWadocMainPane(java.awt.LayoutManager p0, boolean p1) {
        super(p0, p1);
    }

    /**
     * PsnWadocMainPanel ������ע�⡣
     * 
     * @param p0
     *            boolean
     */
    public PsnWadocMainPane(boolean p0) {
        super(p0);
    }

    /**
     * ���� BillPane ����ֵ��
     * 
     * @return nc.ui.pub.bill.BillScrollPane
     */
    /* ���棺�˷������������ɡ� */
    private nc.ui.pub.bill.BillScrollPane getBillPane() {
        if (ivjBillPane == null) {
            try {
                ivjBillPane = new nc.ui.pub.bill.BillScrollPane();
                ivjBillPane.setName("BillPane");
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjBillPane;
    }

    /**
     * �˴����뷽�������� �������ڣ�(2004-6-9 14:49:05)
     */
    public nc.ui.pub.bill.BillScrollPane getBillScrollPane() {
        return getBillPane();
    }

    /**
     * ÿ�������׳��쳣ʱ������
     * 
     * @param exception
     *            java.lang.Throwable
     */
    private void handleException(java.lang.Throwable exception) {

        /* ��ȥ���и��е�ע�ͣ��Խ�δ��׽�����쳣��ӡ�� stdout�� */
        exception.printStackTrace(System.out);
    }

    /**
     * ��ʼ���ࡣ
     */
    /* ���棺�˷������������ɡ� */
    private void initialize() {
        try {
            // user code begin {1}
            // user code end
            setName("PsnWadocMainPane");
            setLayout(new java.awt.BorderLayout());
            setSize(424, 202);
            add(getBillPane(), "Center");
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
        // user code begin {2}
        // user code end
    }

    /**
     * ��ʼ������ �������ڣ�(2004-6-4 19:37:09)
     * 
     * @param item
     *            java.lang.String[][]
     */
    public void initTable(WaItemVO[] item) {

        List<BillItem> listBillItems = new LinkedList<BillItem>();
        BillItem billItem = null;

        billItem = new BillItem();
        billItem.setName(nc.ui.ml.NCLangRes.getInstance().getStrByID("601307", "upt601307-000014")/* "��������" */);
        billItem.setKey("deptname");
        billItem.setDataType(BillItem.STRING);
        billItem.setEdit(false);
        billItem.setWidth(70);
        billItem.setShow(true);
        listBillItems.add(billItem);

        billItem = new BillItem();
        billItem.setName(nc.ui.ml.NCLangRes.getInstance().getStrByID("common", "UC000-0000147") /* "��Ա����" */);
        billItem.setKey("psncode");
        billItem.setDataType(BillItem.STRING);
        billItem.setEdit(false);
        billItem.setWidth(70);
        billItem.setShow(true);
        listBillItems.add(billItem);

        billItem = new BillItem();
        billItem.setName(nc.ui.ml.NCLangRes.getInstance().getStrByID("601307", "UPP601307-000000")/* "��Ա����" */);
        billItem.setKey("psnname");
        billItem.setDataType(BillItem.STRING);
        billItem.setEdit(false);
        billItem.setWidth(70);
        billItem.setShow(true);
        listBillItems.add(billItem);
        
        billItem = new BillItem();
        billItem.setName(NCLangRes.getInstance().getStrByID("common", "UC000-0000140")/*
                 * 
                 * "��Ա���"
                 */);
        billItem.setKey("psnclassname");
        billItem.setDataType(BillItem.STRING);
        billItem.setEdit(false);
        billItem.setWidth(70);
        billItem.setShow(true);
        listBillItems.add(billItem);
        
        billItem = new BillItem();
        billItem.setName(nc.ui.ml.NCLangRes.getInstance().getStrByID("60131004","UPT60131004-000001")/*@res "���ڸ�λ"*/);
        billItem.setKey("jobname");
        billItem.setDataType(BillItem.STRING);
        billItem.setEdit(false);
        billItem.setWidth(70);
        billItem.setShow(true);
        listBillItems.add(billItem);
        
        billItem = new BillItem();
        billItem.setName(nc.ui.ml.NCLangRes.getInstance().getStrByID("common","UC000-0003300")/*@res "ְ��"*/);
        billItem.setKey("dutyname");
        billItem.setDataType(BillItem.STRING);
        billItem.setEdit(false);
        billItem.setWidth(70);
        billItem.setShow(true);
        listBillItems.add(billItem);
        
        // zhanghua
        billItem = new BillItem();
        billItem.setName("��ע");
        billItem.setKey("remark");
        billItem.setDataType(BillItem.STRING);
        billItem.setEdit(false);
        billItem.setWidth(70);
        billItem.setShow(true);
        listBillItems.add(billItem);

        if (item != null) {
            for (WaItemVO element : item) {

                String proKey = element.getPk_wa_item();

                billItem = new BillItem();
                billItem.setName(nc.ui.ml.NCLangRes.getInstance().getStrByID("601307", "upt601307-000004")/* "н�ʼ���" */);
                billItem.setKey(proKey + ".waprmlevel");
                billItem.setDataType(BillItem.STRING);
                billItem.setEdit(false);
                billItem.setWidth(70);
                billItem.setShow(true);
                listBillItems.add(billItem);

                billItem = new BillItem();
                billItem.setName(nc.ui.ml.NCLangRes.getInstance().getStrByID("601307", "upt601307-000005")/* "н�ʵ���" */);
                billItem.setKey(proKey + ".waseclevel");
                billItem.setDataType(BillItem.STRING);
                billItem.setEdit(false);
                billItem.setWidth(70);
                billItem.setShow(true);
                listBillItems.add(billItem);

                billItem = new BillItem();
                billItem.setName(nc.ui.ml.NCLangRes.getInstance().getStrByID("60130704", "UPP60130704-000039") /* "������" */);
                billItem.setKey(proKey + ".criterionvalue");
                billItem.setDataType(BillItem.DECIMAL);
                billItem.setEdit(false);
                billItem.setWidth(70);
                billItem.setDecimalDigits(element.getIflddecimal());
                billItem.setShow(true);
                listBillItems.add(billItem);

                billItem = new BillItem();
                billItem.setName(nc.ui.ml.NCLangRes.getInstance().getStrByID("601307", "upt601307-000006") /* "���" */);
                billItem.setKey(proKey + ".nmoney");
                billItem.setDataType(BillItem.DECIMAL);
                billItem.setEdit(false);
                billItem.setWidth(70);
                billItem.setDecimalDigits(element.getIflddecimal());
                billItem.setShow(true);
                listBillItems.add(billItem);
            }
        }

        billItem = new BillItem();
        billItem.setName("pk_psndoc");
        billItem.setKey("pk_psndoc");
        billItem.setDataType(BillItem.STRING);
        billItem.setEdit(false);
        billItem.setWidth(40);
        billItem.setShow(false);
        listBillItems.add(billItem);

        billItem = new BillItem();
        billItem.setName("deptcode");
        billItem.setKey("deptcode");
        billItem.setDataType(BillItem.STRING);
        billItem.setEdit(false);
        billItem.setWidth(40);
        billItem.setShow(false);
        listBillItems.add(billItem);

        BillModel billModel = new BillModel();
        billModel.setBodyItems(listBillItems.toArray(new BillItem[listBillItems.size()]));

        getBillPane().setTableModel(billModel);
        getBillPane().getTable().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        getBillPane().getTable().setColumnSelectionAllowed(false);

        getBillPane().setRowNOShow(true);

        // �趨���ͷ
        GroupableTableHeader header = (GroupableTableHeader) getBillPane().getTable().getTableHeader();

        if (item != null) {
            for (WaItemVO element : item) {
                header.addColumnGroup(getColumnGroup(element.getPk_wa_item(), element.getVname()));
            }
        }

    }

    /**
     * 
     * @author zhangg on 2009-8-21
     * @param proKey
     * @param headerName
     * @return
     */
    private ColumnGroup getColumnGroup(String proKey, String headerName) {
        ColumnGroup columnGroup = new ColumnGroup(headerName);
        TableColumnModel cm = getBillPane().getTable().getColumnModel();
        for (int i = 0; i < cm.getColumnCount(); i++) {
            if (getBillPane().getBodyKeyByCol(i).startsWith(proKey)) {
                columnGroup.add(cm.getColumn(i));
            }
        }
        return columnGroup;
    }

    public void setWadocData(nc.vo.hi.wadoc.PsndocWadocMainVO[] mainVOs)
            throws java.lang.Exception {
        getBillPane().getTableModel().setBodyDataVO(mainVOs);
       
        TableColResize.reSizeTable(getBillPane());

    }

    /**
     * 
     * @author zhangg on 2009-8-19
     * @return
     */
    public PsndocWadocMainVO getBodySelectedVO(int row) {

        return (PsndocWadocMainVO) getBillPane().getTableModel().getBodyValueRowVO(row, PsndocWadocMainVO.class.getName());

    }
}
