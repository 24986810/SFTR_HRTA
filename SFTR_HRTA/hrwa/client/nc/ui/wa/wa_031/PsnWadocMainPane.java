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
 * 此处插入类型描述。 创建日期：(2004-6-4 11:35:45)
 * 
 * @author：Administrator
 */
public class PsnWadocMainPane extends nc.ui.pub.beans.UIPanel {
    /**
     * 
     */
    private static final long serialVersionUID = -8711804531661400794L;
    private nc.ui.pub.bill.BillScrollPane ivjBillPane = null;

    /**
     * PsnWadocMainPanel 构造子注解。
     */
    public PsnWadocMainPane() {
        super();
        initialize();
    }

    /**
     * PsnWadocMainPanel 构造子注解。
     * 
     * @param p0
     *            java.awt.LayoutManager
     */
    public PsnWadocMainPane(java.awt.LayoutManager p0) {
        super(p0);
    }

    /**
     * PsnWadocMainPanel 构造子注解。
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
     * PsnWadocMainPanel 构造子注解。
     * 
     * @param p0
     *            boolean
     */
    public PsnWadocMainPane(boolean p0) {
        super(p0);
    }

    /**
     * 返回 BillPane 特性值。
     * 
     * @return nc.ui.pub.bill.BillScrollPane
     */
    /* 警告：此方法将重新生成。 */
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
     * 此处插入方法描述。 创建日期：(2004-6-9 14:49:05)
     */
    public nc.ui.pub.bill.BillScrollPane getBillScrollPane() {
        return getBillPane();
    }

    /**
     * 每当部件抛出异常时被调用
     * 
     * @param exception
     *            java.lang.Throwable
     */
    private void handleException(java.lang.Throwable exception) {

        /* 除去下列各行的注释，以将未捕捉到的异常打印至 stdout。 */
        exception.printStackTrace(System.out);
    }

    /**
     * 初始化类。
     */
    /* 警告：此方法将重新生成。 */
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
     * 初始化主表。 创建日期：(2004-6-4 19:37:09)
     * 
     * @param item
     *            java.lang.String[][]
     */
    public void initTable(WaItemVO[] item) {

        List<BillItem> listBillItems = new LinkedList<BillItem>();
        BillItem billItem = null;

        billItem = new BillItem();
        billItem.setName(nc.ui.ml.NCLangRes.getInstance().getStrByID("601307", "upt601307-000014")/* "部门名称" */);
        billItem.setKey("deptname");
        billItem.setDataType(BillItem.STRING);
        billItem.setEdit(false);
        billItem.setWidth(70);
        billItem.setShow(true);
        listBillItems.add(billItem);

        billItem = new BillItem();
        billItem.setName(nc.ui.ml.NCLangRes.getInstance().getStrByID("common", "UC000-0000147") /* "人员编码" */);
        billItem.setKey("psncode");
        billItem.setDataType(BillItem.STRING);
        billItem.setEdit(false);
        billItem.setWidth(70);
        billItem.setShow(true);
        listBillItems.add(billItem);

        billItem = new BillItem();
        billItem.setName(nc.ui.ml.NCLangRes.getInstance().getStrByID("601307", "UPP601307-000000")/* "人员名称" */);
        billItem.setKey("psnname");
        billItem.setDataType(BillItem.STRING);
        billItem.setEdit(false);
        billItem.setWidth(70);
        billItem.setShow(true);
        listBillItems.add(billItem);
        
        billItem = new BillItem();
        billItem.setName(NCLangRes.getInstance().getStrByID("common", "UC000-0000140")/*
                 * 
                 * "人员类别"
                 */);
        billItem.setKey("psnclassname");
        billItem.setDataType(BillItem.STRING);
        billItem.setEdit(false);
        billItem.setWidth(70);
        billItem.setShow(true);
        listBillItems.add(billItem);
        
        billItem = new BillItem();
        billItem.setName(nc.ui.ml.NCLangRes.getInstance().getStrByID("60131004","UPT60131004-000001")/*@res "所在岗位"*/);
        billItem.setKey("jobname");
        billItem.setDataType(BillItem.STRING);
        billItem.setEdit(false);
        billItem.setWidth(70);
        billItem.setShow(true);
        listBillItems.add(billItem);
        
        billItem = new BillItem();
        billItem.setName(nc.ui.ml.NCLangRes.getInstance().getStrByID("common","UC000-0003300")/*@res "职务"*/);
        billItem.setKey("dutyname");
        billItem.setDataType(BillItem.STRING);
        billItem.setEdit(false);
        billItem.setWidth(70);
        billItem.setShow(true);
        listBillItems.add(billItem);
        
        // zhanghua
        billItem = new BillItem();
        billItem.setName("备注");
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
                billItem.setName(nc.ui.ml.NCLangRes.getInstance().getStrByID("601307", "upt601307-000004")/* "薪资级别" */);
                billItem.setKey(proKey + ".waprmlevel");
                billItem.setDataType(BillItem.STRING);
                billItem.setEdit(false);
                billItem.setWidth(70);
                billItem.setShow(true);
                listBillItems.add(billItem);

                billItem = new BillItem();
                billItem.setName(nc.ui.ml.NCLangRes.getInstance().getStrByID("601307", "upt601307-000005")/* "薪资档别" */);
                billItem.setKey(proKey + ".waseclevel");
                billItem.setDataType(BillItem.STRING);
                billItem.setEdit(false);
                billItem.setWidth(70);
                billItem.setShow(true);
                listBillItems.add(billItem);

                billItem = new BillItem();
                billItem.setName(nc.ui.ml.NCLangRes.getInstance().getStrByID("60130704", "UPP60130704-000039") /* "级别金额" */);
                billItem.setKey(proKey + ".criterionvalue");
                billItem.setDataType(BillItem.DECIMAL);
                billItem.setEdit(false);
                billItem.setWidth(70);
                billItem.setDecimalDigits(element.getIflddecimal());
                billItem.setShow(true);
                listBillItems.add(billItem);

                billItem = new BillItem();
                billItem.setName(nc.ui.ml.NCLangRes.getInstance().getStrByID("601307", "upt601307-000006") /* "金额" */);
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

        // 设定多表头
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
