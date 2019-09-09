package nc.ui.hi.hi_301;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;
import javax.swing.JComponent;
import javax.swing.JPanel;
import nc.itf.hi.HIDelegator;
import nc.itf.hi.IPsnInf;
import nc.itf.hr.bd.ISetdict;
import nc.itf.hr.pub.PubDelegator;
import nc.ui.bd.ref.AbstractRefModel;
import nc.ui.hi.hi_301.trigger.TriggerTable;
import nc.ui.hi.hi_307.KeyPersonUI;
import nc.ui.hi.ref.SpaDeptdocRefGridTreeModel;
import nc.ui.hi.ref.SpaDeptdocRefTreeModel;
import nc.ui.hr.frame.util.BillPanelUtils;
import nc.ui.hr.frame.util.table.TableMultiSelHelper;
import nc.ui.hr.global.Global;
import nc.ui.ml.NCLangRes;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UIButton;
import nc.ui.pub.beans.UIComboBox;
import nc.ui.pub.beans.UIDialog;
import nc.ui.pub.beans.UIPanel;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.beans.ValueChangedEvent;
import nc.ui.pub.beans.ValueChangedListener;
import nc.ui.pub.bill.BillCardPanel;
import nc.ui.pub.bill.BillData;
import nc.ui.pub.bill.BillEditEvent;
import nc.ui.pub.bill.BillEditListener;
import nc.ui.pub.bill.BillItem;
import nc.ui.pub.bill.BillModel;
import nc.ui.pub.bill.BillScrollPane;
import nc.vo.hi.hi_301.GeneralVO;
import nc.vo.hr.bd.setdict.FlddictVO;
import nc.vo.ml.AbstractNCLangRes;
import nc.vo.ml.NCLangRes4VoTransl;
import nc.vo.pub.BusinessException;
import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;

public class BatchAddSetDialog
  extends UIDialog
  implements ActionListener, BillEditListener
{
  private SortableBillScrollPane ivjSortableBillScrollTable = null;
  private UIButton ivjUIButtonCancel = null;
  private UIButton ivjUIButtonNext = null;
  private UIButton ivjUIButtonOK = null;
  private UIButton ivjUIButtonPervious = null;
  private JPanel ivjUIDialogContentPane = null;
  private UIPanel ivjUIPanelCenter = null;
  private BillCardPanel ivjUIPanelFirst = null;
  private UIPanel ivjUIPanelSecond = null;
  private UIPanel ivjUIPanelSouth = null;
  private PsnInfCollectUI parent;
  private BillItem[] headItems;
  private BillItem[] tableItems;
  private String currentSetCode;
  public static final String PK_PREFIX = "$";
  private boolean includeother = false;
  private String modulename = "";
  private boolean caneditkeypsn = false;
  private BillCardPanel ivjUISecondBillCard;
  
  public BatchAddSetDialog()
  {
    initialize();
  }
  
  public BatchAddSetDialog(Container parent)
  {
    super(parent);
  }
  
  public BatchAddSetDialog(PsnInfCollectUI parent, boolean includeOtherCorpperson, String modulname, boolean caneditkeypsn)
  {
    super(parent);
    this.parent = parent;
    this.includeother = includeOtherCorpperson;
    this.modulename = modulname;
    this.caneditkeypsn = caneditkeypsn;
    if ((parent instanceof KeyPersonUI)) {
      setPsnLists(((KeyPersonUI)parent).getCanEditSelectedPsnListData());
    } else {
      setPsnLists(parent.getSelectPsnListData());
    }
    initialize();
  }
  
  public BatchAddSetDialog(Container parent, String title)
  {
    super(parent, title);
  }
  
  public BatchAddSetDialog(Frame owner)
  {
    super(owner);
  }
  
  public BatchAddSetDialog(Frame owner, String title)
  {
    super(owner, title);
  }
  
  private SortableBillScrollPane getSortableBillScrollTable()
  {
    if (this.ivjSortableBillScrollTable == null) {
      try
      {
        this.ivjSortableBillScrollTable = new SortableBillScrollPane();
        this.ivjSortableBillScrollTable.setName("SortableBillScrollTable");
      }
      catch (Throwable ivjExc)
      {
        handleException(ivjExc);
      }
    }
    return this.ivjSortableBillScrollTable;
  }
  
  private UIButton getUIButtonCancel()
  {
    if (this.ivjUIButtonCancel == null) {
      try
      {
        this.ivjUIButtonCancel = new UIButton();
        this.ivjUIButtonCancel.setName("UIButtonCancel");
        this.ivjUIButtonCancel
          .setText(NCLangRes.getInstance().getStrByID(
          "600704", "upt600704-000155"));
      }
      catch (Throwable ivjExc)
      {
        handleException(ivjExc);
      }
    }
    return this.ivjUIButtonCancel;
  }
  
  private UIButton getUIButtonNext()
  {
    if (this.ivjUIButtonNext == null) {
      try
      {
        this.ivjUIButtonNext = new UIButton();
        this.ivjUIButtonNext.setName("UIButtonNext");
        this.ivjUIButtonNext
          .setText(NCLangRes.getInstance().getStrByID(
          "600704", "UPP600704-000207"));
      }
      catch (Throwable ivjExc)
      {
        handleException(ivjExc);
      }
    }
    return this.ivjUIButtonNext;
  }
  
  private UIButton getUIButtonOK()
  {
    if (this.ivjUIButtonOK == null) {
      try
      {
        this.ivjUIButtonOK = new UIButton();
        this.ivjUIButtonOK.setName("UIButtonOK");
        this.ivjUIButtonOK
          .setText(NCLangRes.getInstance().getStrByID(
          "600704", "UPP600704-000010"));
      }
      catch (Throwable ivjExc)
      {
        handleException(ivjExc);
      }
    }
    return this.ivjUIButtonOK;
  }
  
  private UIButton getUIButtonPervious()
  {
    if (this.ivjUIButtonPervious == null) {
      try
      {
        this.ivjUIButtonPervious = new UIButton();
        this.ivjUIButtonPervious.setName("UIButtonPervious");
        this.ivjUIButtonPervious
          .setAlignmentX(1.0F);
        this.ivjUIButtonPervious
          .setText(NCLangRes.getInstance().getStrByID(
          "600704", "UPP600704-000206"));
        this.ivjUIButtonPervious
          .setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
      }
      catch (Throwable ivjExc)
      {
        handleException(ivjExc);
      }
    }
    return this.ivjUIButtonPervious;
  }
  
  private JPanel getUIDialogContentPane()
  {
    if (this.ivjUIDialogContentPane == null) {
      try
      {
        this.ivjUIDialogContentPane = new JPanel();
        this.ivjUIDialogContentPane.setName("UIDialogContentPane");
        this.ivjUIDialogContentPane.setLayout(new BorderLayout());
        getUIDialogContentPane().add(getUIPanelCenter(), "Center");
        getUIDialogContentPane().add(getUIPanelSouth(), "South");
      }
      catch (Throwable ivjExc)
      {
        handleException(ivjExc);
      }
    }
    return this.ivjUIDialogContentPane;
  }
  
  private UIPanel getUIPanelCenter()
  {
    if (this.ivjUIPanelCenter == null) {
      try
      {
        this.ivjUIPanelCenter = new UIPanel();
        this.ivjUIPanelCenter.setName("UIPanelCenter");
        this.ivjUIPanelCenter.setLayout(new CardLayout());
        getUIPanelCenter().add(getUIPanelFirst(), getUIPanelFirst().getName());
        getUIPanelCenter().add(getUIPanelSecond(), getUIPanelSecond().getName());
      }
      catch (Throwable ivjExc)
      {
        handleException(ivjExc);
      }
    }
    return this.ivjUIPanelCenter;
  }
  
  private BillCardPanel getUIPanelFirst()
  {
    if (this.ivjUIPanelFirst == null) {
      try
      {
        this.ivjUIPanelFirst = new BillCardPanel();
        this.ivjUIPanelFirst.setName("UIPanelFirst");
      }
      catch (Throwable ivjExc)
      {
        handleException(ivjExc);
      }
    }
    return this.ivjUIPanelFirst;
  }
  
  private BillCardPanel getUIPanelSecondBillCard()
  {
    if (this.ivjUISecondBillCard == null) {
      try
      {
        this.ivjUISecondBillCard = new BillCardPanel();
        this.ivjUISecondBillCard.setName("SecondBillCard");
      }
      catch (Throwable ivjExc)
      {
        handleException(ivjExc);
      }
    }
    return this.ivjUISecondBillCard;
  }
  
  private UIPanel getUIPanelSecond()
  {
    if (this.ivjUIPanelSecond == null) {
      try
      {
        this.ivjUIPanelSecond = new UIPanel();
        this.ivjUIPanelSecond.setName("UIPanelSecond");
        this.ivjUIPanelSecond.setLayout(new BorderLayout());
        getUIPanelSecond().add(getSortableBillScrollTable(), "Center");
      }
      catch (Throwable ivjExc)
      {
        handleException(ivjExc);
      }
    }
    return this.ivjUIPanelSecond;
  }
  
  private UIPanel getUIPanelSouth()
  {
    if (this.ivjUIPanelSouth == null) {
      try
      {
        this.ivjUIPanelSouth = new UIPanel();
        this.ivjUIPanelSouth.setName("UIPanelSouth");
        this.ivjUIPanelSouth
          .setAlignmentX(1.0F);
        this.ivjUIPanelSouth.setLayout(new FlowLayout(2));
        getUIPanelSouth().add(getUIButtonPervious(), getUIButtonPervious().getName());
        getUIPanelSouth().add(getUIButtonNext(), getUIButtonNext().getName());
        getUIPanelSouth().add(getUIButtonOK(), getUIButtonOK().getName());
        getUIPanelSouth().add(getUIButtonCancel(), getUIButtonCancel().getName());
      }
      catch (Throwable ivjExc)
      {
        handleException(ivjExc);
      }
    }
    return this.ivjUIPanelSouth;
  }
  
  private void handleException(Throwable exception) {}
  
  private void initialize()
  {
    try
    {
      setName("BatchAddSetDialog");
      setDefaultCloseOperation(2);
      setSize(706, 460);
      setTitle(NCLangRes.getInstance()
        .getStrByID("600704", "UPP600704-000185") + "—" + this.parent.getCard().getBodyPanel().getTableName());
      setContentPane(getUIDialogContentPane());
    }
    catch (Throwable ivjExc)
    {
      handleException(ivjExc);
    }
    this.currentSetCode = this.parent.getCurrentSetCode();
    
    BillItem[] srcItems = this.parent.getCurrentSetItems();
    this.headItems = getCopyBillItems(srcItems);
    this.tableItems = getCopyBillItems(srcItems);
    
    initBillItems(this.headItems);
    initBillItems(this.tableItems);
    
    initFirstCardData(this.headItems);
    initSencondPanelTable(this.tableItems);
    
    getUIButtonPervious().addActionListener(this);
    getUIButtonOK().addActionListener(this);
    getUIButtonNext().addActionListener(this);
    getUIButtonCancel().addActionListener(this);
    


    getUIPanelFirst().show();
    getUIPanelSecond().hide();
    getUIButtonPervious().setVisible(false);
    getUIButtonOK().setVisible(false);
    getUIButtonNext().setVisible(true);
    getUIButtonCancel().setVisible(true);
    
    this.parent.setBatchAddSet(true);
  }
  
  public static void main(String[] args)
  {
    try
    {
      BatchAddSetDialog aBatchAddSetDialog = new BatchAddSetDialog();
      aBatchAddSetDialog.setModal(true);
      aBatchAddSetDialog
        .addWindowListener(new WindowAdapter()
        {
          public void windowClosing(WindowEvent e)
          {
            System.exit(0);
          }
        });
      aBatchAddSetDialog.show();
      Insets insets = aBatchAddSetDialog.getInsets();
      aBatchAddSetDialog.setSize(aBatchAddSetDialog.getWidth() + 
        insets.left + insets.right, 
        aBatchAddSetDialog.getHeight() + 
        insets.top + insets.bottom);
      aBatchAddSetDialog.setVisible(true);
    }
    catch (Throwable exception)
    {
      exception.printStackTrace(System.out);
    }
  }
  
  private void initFirstCardData(BillItem[] billItems)
  {
    BillData billData = new BillData();
    billData.setHeadItems(billItems);
    getUIPanelFirst().setBillData(billData);
  }
  
  TriggerTable triggerTable = new TriggerTable();
  
  private void initSencondPanelTable(BillItem[] tableItemsTT)
  {
    ArrayList<BillItem> arrListBillItem = new ArrayList();
    for (BillItem billItemRef : tableItemsTT) {
      if ((billItemRef.isShow()) && (5 == billItemRef.getDataType()))
      {
        dealWithRefField(billItemRef, false, 1, arrListBillItem);
      }
      else
      {
        billItemRef.setShowOrder(arrListBillItem.size() + 1);
        
        arrListBillItem.add(billItemRef);
      }
    }
    tableItemsTT = (BillItem[])arrListBillItem.toArray(new BillItem[0]);
    
    BillPanelUtils.initDefRef(tableItemsTT);
    


    getSortableBillScrollTable().setTableModel(getTableModel(tableItemsTT));
    getSortableBillScrollTable().setHeadMultiSelected(true);
  }
  
  private void dealWithRefField(BillItem billItemRef, boolean blIsCard, int iPosition, ArrayList<BillItem> arrListBillItem)
  {
    BillItem billItemPk = new BillItem();
    billItemPk.setWidth(1);
    billItemPk.setShow(false);
    billItemPk.setList(true);
    billItemPk.setNull(false);
    billItemPk.setKey(billItemRef.getKey());
    billItemPk.setPos(billItemRef.getPos());
    billItemPk.setDataType(0);
    billItemPk.setName(billItemRef.getName() + " pk");
    billItemPk.setTableCode(billItemRef.getTableCode());
    billItemPk.setTableName(billItemRef.getTableName());
    billItemPk.setShowOrder(arrListBillItem.size() + 1);
    

    billItemRef.setIDColName(billItemPk.getKey());
    billItemRef.setName(billItemRef.getName());
    billItemRef.setShowOrder(arrListBillItem.size() + 2);
    billItemRef.setKey(billItemRef.getKey() + BillPanelUtils.REF_SHOW_NAME);
    
    arrListBillItem.add(billItemPk);
    arrListBillItem.add(billItemRef);
  }
  
  private GeneralVO getFirstPanelData()
  {
    BillItem[] items = getUIPanelFirst().getHeadItems();
    GeneralVO headvo = new GeneralVO();
    for (int i = 0; i < items.length; i++) {
      if (items[i].isShow()) {
        if (items[i].getDataType() == 5)
        {
          UIRefPane ref = (UIRefPane)items[i].getComponent();
          if (ref != null)
          {
            headvo.setAttributeValue(items[i].getKey(), items[i].getValue());
            headvo.setAttributeValue(items[i].getKey() + "_showname", 
              ref.getText());
          }
          else
          {
            headvo.setAttributeValue(items[i].getKey(), null);
            headvo.setAttributeValue(items[i].getKey() + "_showname", null);
          }
        }
        else
        {
          headvo.setAttributeValue(items[i].getKey(), 
            items[i].getValue());
        }
      }
    }
    return headvo;
  }
  
  private GeneralVO[] getSecondPanelData()
  {
    GeneralVO[] data = (GeneralVO[])getSortableBillScrollTable().getTableModel().getBodyValueVOs("nc.vo.hi.hi_301.GeneralVO");
    for (int i = 0; i < this.tableItems.length; i++) {
      if ((this.tableItems[i].getDataType() == 5) || 
        (this.tableItems[i].getDataType() == 7))
      {
        for (int j = 0; j < data.length; j++)
        {
          data[j].setAttributeValue("$" + this.tableItems[i].getKey(), this.tableItems[i].getValue());
          if (this.tableItems[i].getKey().endsWith("_showname")) {
            data[j].removeAttributeName(this.tableItems[i].getKey());
          }
        }
      }
      else if (this.tableItems[i].getDataType() == 6)
      {
        UIComboBox comb = (UIComboBox)this.tableItems[i].getComponent();
        ComboBillItem.ComboItem comboItem = (ComboBillItem.ComboItem)comb
          .getSelectedItem();
        if (comboItem == null)
        {
          comb.setSelectedIndex(0);
          comboItem = (ComboBillItem.ComboItem)comb
            .getSelectedItem();
        }
        for (int j = 0; j < data.length; j++)
        {
          Object value = comboItem.getValue();
          data[j].setAttributeValue("$" + 
            this.tableItems[i].getKey(), value);
          data[j].setAttributeValue(this.tableItems[i].getKey(), 
            comboItem.getName());
        }
      }
    }
    GeneralVO[] temp = (GeneralVO[])null;
    int[] rows = getSortableBillScrollTable().getHeadSelectedRows();
    if (rows.length > 0)
    {
      temp = new GeneralVO[rows.length];
      for (int i = 0; i < rows.length; i++)
      {
        temp[i] = data[rows[i]];
        if (temp[i].getAttributeValue("#SEL_COL#") != null) {
          temp[i].removeAttributeName("#SEL_COL#");
        }
      }
    }
    return temp;
  }
  
  private BillItem[] getTableItems(BillItem[] tableItemsTT)
  {
    if ((tableItemsTT == null) || (tableItemsTT.length == 0)) {
      return null;
    }
    String[] columnName = {
      "人员主键", 
      "人员个人主键", 
      NCLangRes.getInstance().getStrByID("600704", 
      "upt600704-000025"), 
      NCLangRes.getInstance().getStrByID("600704", 
      "UPP600704-000064"), 
      NCLangRes.getInstance().getStrByID("600704", 
      "upt600704-000001") };
    String[] columnCode = { "pk_psndoc", "pk_psnbasdoc", "psncode", "psnname", "deptname" };
    int[] dataType = new int[5];
    BillItem[] biaBody = new BillItem[columnCode.length];
    for (int i = 0; i < columnCode.length; i++)
    {
      biaBody[i] = new BillItem();
      biaBody[i].setName(columnName[i]);
      biaBody[i].setKey(columnCode[i]);
      biaBody[i].setWidth(150);
      biaBody[i].setEnabled(true);
      biaBody[i].setEdit(false);
      biaBody[i].setShow(true);
      biaBody[i].setNull(false);
      biaBody[i].setPos(1);
      biaBody[i].setDataType(dataType[i]);
      biaBody[i].setTableCode(this.currentSetCode);
      if ((i == 0) || (i == 1)) {
        biaBody[i].setShow(false);
      }
    }
    BillItem[] biaBodyNew = new BillItem[biaBody.length + tableItemsTT.length];
    for (int i = 0; i < biaBodyNew.length; i++) {
      if (i < 5) {
        biaBodyNew[i] = biaBody[i];
      } else {
        biaBodyNew[i] = tableItemsTT[(i - biaBody.length)];
      }
    }
    return biaBodyNew;
  }
  
  public BillModel getTableModel(BillItem[] tableItemsTT)
  {
    BillModel billModel = new BillModel();
    billModel.setBodyItems(getTableItems(tableItemsTT));
    
    return billModel;
  }
  
  public BillItem[] getCopyBillItems(BillItem[] billItems)
  {
    if ((billItems == null) || (billItems.length == 0)) {
      return null;
    }
    BillItem[] returnBillItems = new BillItem[billItems.length];
    for (int i = 0; i < billItems.length; i++)
    {
      returnBillItems[i] = new BillItem();
      returnBillItems[i].setName(billItems[i].getName());
      returnBillItems[i].setKey(billItems[i].getKey());
      returnBillItems[i].setPos(0);
      returnBillItems[i].setDataType(billItems[i].getDataType());
      returnBillItems[i].setEdit(billItems[i].isEdit());
      returnBillItems[i].setEnabled(billItems[i].isEnabled());
      returnBillItems[i].setShow(billItems[i].isShow());
      returnBillItems[i].setLength(billItems[i].getLength());
      returnBillItems[i].setNull(billItems[i].isNull());
      returnBillItems[i]
        .setDecimalDigits(billItems[i].getDecimalDigits());
      
      returnBillItems[i].setWidth(billItems[i].getWidth());
      returnBillItems[i].setRefType(billItems[i].getRefType());
      returnBillItems[i].setReadOrder(billItems[i].getReadOrder());
      returnBillItems[i].setEditFormula(billItems[i].getEditFormulas());
      returnBillItems[i].setValidateFormulas(
        billItems[i].getValidateFormulas());
      
      returnBillItems[i].setLoadFormula(billItems[i].getLoadFormula());
      
      returnBillItems[i].setForeground(billItems[i].getForeground());
      returnBillItems[i].setIDColName(billItems[i].getIDColName());
      
      returnBillItems[i].setLock(billItems[i].isLock());
      




      returnBillItems[i].setM_bNewLineFlag(
        billItems[i].isM_bNewLineFlag());
      returnBillItems[i].setM_bNotLeafSelectedEnabled(
        billItems[i].isM_bNotLeafSelectedEnabled());
      returnBillItems[i].setM_bReviseFlag(billItems[i].isM_bReviseFlag());
      



      returnBillItems[i].setShowOrder(billItems[i].getShowOrder());
      returnBillItems[i].setTableCode(this.currentSetCode);
      
      returnBillItems[i].setTableName(billItems[i].getTableName());
      
      returnBillItems[i].setWithIndex(billItems[i].isWithIndex());
      

      JComponent component = billItems[i].getComponent();
      if (component != null) {
        component.setEnabled(billItems[i].isEdit());
      }
      returnBillItems[i].setComponent(component);
      
      returnBillItems[i].clearViewData();
    }
    return returnBillItems;
  }
  
  private String getModuleName()
  {
    return this.parent.getModuleName();
  }
  
  private GeneralVO[] psnLists = null;
  
  private void setPsnLists(GeneralVO[] psnListsPara)
  {
    if ((psnListsPara == null) || (psnListsPara.length == 0)) {
      return;
    }
    try
    {
      if (this.includeother)
      {
        Vector vv = new Vector();
        HashMap h = new HashMap();
        for (int i = 0; i < psnListsPara.length; i++)
        {
          GeneralVO vo = (GeneralVO)psnListsPara[i].clone();
          String pk_psndoc = (String)vo.getAttributeValue("pk_psndoc");
          Object o = vo.getAttributeValue("enddate");
          if ((h.get(pk_psndoc) == null) && (o == null))
          {
            vv.addElement(vo);
            h.put(pk_psndoc, vo);
          }
        }
        this.psnLists = new GeneralVO[vv.size()];
        vv.copyInto(this.psnLists);
      }
      else if ((this.modulename.equalsIgnoreCase("600707")) && (!this.caneditkeypsn))
      {
        Vector vv = new Vector();
        for (int i = 0; i < psnListsPara.length; i++) {
          if ((psnListsPara[i].getJobTypeFlag().intValue() == 0) && 
            (Global.getCorpPK().equalsIgnoreCase((String)psnListsPara[i].getAttributeValue("belong_pk_corp"))))
          {
            String sql = " select 1 from hi_psndoc_keypsn where pk_psndoc ='" + (String)psnListsPara[i].getAttributeValue("pk_psndoc") + "'and enddate is null";
            boolean iskeypsn = HIDelegator.getPsnInf().isRecordExist(sql);
            if (!iskeypsn)
            {
              GeneralVO vo = (GeneralVO)psnListsPara[i].clone();
              vv.addElement(vo);
            }
          }
        }
        this.psnLists = new GeneralVO[vv.size()];
        vv.copyInto(this.psnLists);
      }
      else
      {
        Vector vv = new Vector();
        for (int i = 0; i < psnListsPara.length; i++) {
          if ((psnListsPara[i].getJobTypeFlag().intValue() == 0) && 
            (Global.getCorpPK().equalsIgnoreCase(
            (String)psnListsPara[i]
            .getAttributeValue("belong_pk_corp"))))
          {
            GeneralVO vo = (GeneralVO)psnListsPara[i].clone();
            vv.addElement(vo);
          }
        }
        this.psnLists = new GeneralVO[vv.size()];
        vv.copyInto(this.psnLists);
      }
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }
  
  public void actionPerformed(ActionEvent e)
  {
    if (e.getSource() == getUIButtonPervious())
    {
      initFirstCardData(this.headItems);
      

      getUIPanelFirst().setVisible(true);
      getUIPanelSecond().setVisible(false);
      getUIButtonPervious().setVisible(false);
      getUIButtonOK().setVisible(false);
      getUIButtonNext().setVisible(true);
      getUIButtonCancel().setVisible(true);
    }
    else if (e.getSource() == getUIButtonNext())
    {
      this.parent.setBatchAddSet(true);
      setSecondTableData(this.psnLists, getFirstPanelData());
      getSortableBillScrollTable().getHeadTableMultiSelector().selectAllRow();
      



      getUIPanelFirst().setVisible(false);
      getUIPanelSecond().setVisible(true);
      getUIButtonPervious().setVisible(true);
      getUIButtonOK().setVisible(true);
      getUIButtonNext().setVisible(false);
      getUIButtonCancel().setVisible(true);
    }
    else if (e.getSource() == getUIButtonOK())
    {
      this.parent.setBatchAddSet(true);
      GeneralVO[] data = getSecondPanelData();
      try
      {
        HIDelegator.getPsnInf().checkIfExistsUnAuditBillofPSN(this.parent.getselectPsnPksExceptRef("bd_psndoc"), this.currentSetCode);
      }
      catch (BusinessException e1)
      {
        MessageDialog.showErrorDlg(this, NCLangRes.getInstance().getStrByID("nc_hr_pub", "UPPnc_hr_pub-000094"), e1.getMessage());
        e1.printStackTrace();
        return;
      }
      if (data == null)
      {
        this.parent.setBatchAddSet(false);
        closeCancel();
      }
      else
      {
        CircularlyAccessibleValueObject[] datadests = new CircularlyAccessibleValueObject[data.length];
        for (int i = 0; i < datadests.length; i++)
        {
          if (data[i] != null)
          {
            String[] fieldnames = data[i].getAttributeNames();
            for (int j = 0; j < fieldnames.length; j++)
            {
              Object obj = data[i].getAttributeValue(fieldnames[j]);
              if (obj != null) {
                if (obj.toString().equalsIgnoreCase("false")) {
                  data[i].setAttributeValue(fieldnames[j], 
                    new UFBoolean("N"));
                } else if (obj.toString().equalsIgnoreCase("true")) {
                  data[i].setAttributeValue(fieldnames[j], 
                    new UFBoolean("Y"));
                }
              }
            }
          }
          datadests[i] = data[i];
          
          GeneralVO data1 = null;
          if (("hi_psndoc_dimission".equalsIgnoreCase(this.currentSetCode)) && (datadests != null))
          {
          for (int j = 0; j < datadests.length; j++) {
            try
            {
            	data1 = HIDelegator.getPsnInf().queryByPsndocPK(data[i].getAttributeValue("pk_psndoc").toString());
              
                datadests[j].setAttributeValue("psnclbefore", data1.getAttributeValue("pk_psncl") != null ? data1.getAttributeValue("pk_psncl").toString() : "");
                datadests[j].setAttributeValue("pkdeptbefore", data1.getAttributeValue("pk_deptdoc") != null ? data1.getAttributeValue("pk_deptdoc").toString() : "");
                datadests[j].setAttributeValue("pkpostbefore", data1.getAttributeValue("pk_om_job") != null ? data1.getAttributeValue("pk_om_job").toString() : "");
                datadests[j].setAttributeValue("pkomdutybefore", data1.getAttributeValue("dutyname") != null ? data1.getAttributeValue("dutyname").toString() : "");
                datadests[j].setAttributeValue("isreturn", data1.getAttributeValue("isreturn") != null ? data1.getAttributeValue("isreturn").toString() : "");
                datadests[j].setAttributeValue("pk_corp", data1.getAttributeValue("pk_corp") != null ? data1.getAttributeValue("pk_corp").toString() : "");
                datadests[j].setAttributeValue("pkdeptafter", data1.getAttributeValue("pk_deptdoc") != null ? data1.getAttributeValue("pk_deptdoc").toString() : "");
                datadests[j].setAttributeValue("pk_corpafter", data1.getAttributeValue("pk_corp") != null ? data1.getAttributeValue("pk_corp").toString() : "");
              
            }
            catch (BusinessException e1)
            {
              e1.printStackTrace();
            }
          }
          }
          if ("hi_psndoc_ctrt".equalsIgnoreCase(this.currentSetCode))
          {
            datadests[i].setAttributeValue("isrefer", 
              new UFBoolean("Y"));
            datadests[i].setAttributeValue("pk_user", Global.getUserID());
            datadests[i].setAttributeValue("operatedate", Global.getLogDate());
          }
          else if ("hi_psndoc_edu".equalsIgnoreCase(this.currentSetCode))
          {
            Object lasteducation = data[i]
              .getAttributeValue("lasteducation");
            if (lasteducation != null) {
              if ("false".equalsIgnoreCase(lasteducation.toString())) {
                datadests[i].setAttributeValue("lasteducation", 
                  new UFBoolean("N"));
              } else if ("true".equalsIgnoreCase(
                lasteducation.toString())) {
                datadests[i].setAttributeValue("lasteducation", 
                  new UFBoolean("Y"));
              }
            }
          }
        }
        Vector v = new Vector();
        for (int i = 0; i < datadests.length; i++)
        {
          String pk_psnbasdoc = (String)datadests[i].getAttributeValue("pk_psnbasdoc");
          v.addElement(pk_psnbasdoc);
        }
        HashMap hmData = new HashMap();
        try
        {
          hmData = HIDelegator.getPsnInf().querySubInfo(v, this.currentSetCode);
          if (datasValidate(hmData, datadests, this.currentSetCode))
          {
            if (("hi_psndoc_dimission".equalsIgnoreCase(this.currentSetCode)) || ("hi_psndoc_deptchg".equalsIgnoreCase(this.currentSetCode))) {
              for (int i = 0; i < datadests.length; i++)
              {
                datadests[i].setAttributeValue("recordnum", data[i].getAttributeValue("recordnum") != null ? data[i].getAttributeValue("recordnum").toString() : Integer.valueOf(0));
                if ("hi_psndoc_deptchg".equalsIgnoreCase(this.currentSetCode))
                {
                  datadests[i].setAttributeValue("lastflag", UFBoolean.TRUE);
                  if (((GeneralVO)datadests[i]).getAttributeValue("begindate") != null) {
                    datadests[i].setAttributeValue("begindate", new UFDate(((GeneralVO)datadests[i]).getAttributeValue("begindate").toString()));
                  }
                }
                HIDelegator.getPsnInf().insertChild(this.currentSetCode, ((GeneralVO)datadests[i]).getAttributeValue("pk_psndoc").toString(), (GeneralVO)datadests[i]);
              }
            } else if (("hi_psndoc_title".equalsIgnoreCase(this.currentSetCode)) || ("hi_psndoc_engage".equalsIgnoreCase(this.currentSetCode))) {
              for (int i = 0; i < datadests.length; i++) {
                HIDelegator.getPsnInf().saveSubSetInfos(this.currentSetCode, ((GeneralVO)datadests[i]).getAttributeValue("pk_psndoc").toString(), new GeneralVO[] { (GeneralVO)datadests[i] }, new String[0]);
              }
            } else {
            	// zhanghua 年度考核 
            	if("hi_psndoc_grpdef15".equalsIgnoreCase(this.currentSetCode)){
            		HIDelegator.getPsnInf().savePsndocGrpDef15(datadests);
            	}else{
            		HIDelegator.getPsnInf().insertTable(datadests, this.currentSetCode);
            	}
            }
            if (datadests.length > 0) {
              for (int i = 0; i < datadests.length; i++)
              {
                boolean updated = this.parent.isUpdateAccRel(datadests[i].getAttributeNames(), this.currentSetCode);
                if (updated)
                {
                  GeneralVO voTemp = new GeneralVO();
                  voTemp.setAttributeValue("pk_psnbasdoc", datadests[i].getAttributeValue("pk_psnbasdoc"));
                  voTemp.setAttributeValue("pk_psndoc", datadests[i].getAttributeValue("pk_psndoc"));
                  
                  GeneralVO relVO = HIDelegator.getPsnInf().updateAccRel(datadests[i].getAttributeNames(), 
                    this.parent.getRelationMap(), 
                    this.currentSetCode, voTemp);
                  
                  HashMap hmRelVO = new HashMap();
                  String[] fields = relVO.getAttributeNames();
                  GeneralVO psnvo = new GeneralVO();
                  GeneralVO baspsnvo = new GeneralVO();
                  psnvo.setAttributeValue("pk_psnbasdoc", datadests[i].getAttributeValue("pk_psnbasdoc"));
                  psnvo.setAttributeValue("pk_psndoc", datadests[i].getAttributeValue("pk_psndoc"));
                  baspsnvo.setAttributeValue("pk_psnbasdoc", datadests[i].getAttributeValue("pk_psnbasdoc"));
                  baspsnvo.setAttributeValue("pk_psndoc", datadests[i].getAttributeValue("pk_psndoc"));
                  boolean isRelPsndoc = false;
                  boolean isRelBasPsndoc = false;
                  for (int j = 0; j < fields.length; j++) {
                    if (fields[j].indexOf(".") >= 0)
                    {
                      String table = fields[j].substring(0, 
                        fields[j].indexOf("."));
                      if ("bd_psndoc".equalsIgnoreCase(table))
                      {
                        String field = fields[j].substring(fields[j].indexOf(".") + 1);
                        psnvo.setAttributeValue(field, relVO.getAttributeValue(fields[j]));
                        

                        isRelPsndoc = true;
                      }
                      else if ("bd_psnbasdoc".equalsIgnoreCase(table))
                      {
                        String field = fields[j].substring(fields[j].indexOf(".") + 1);
                        baspsnvo.setAttributeValue(field, relVO.getAttributeValue(fields[j]));
                        

                        isRelBasPsndoc = true;
                      }
                    }
                  }
                  if (isRelPsndoc) {
                    hmRelVO.put("bd_psndoc", psnvo);
                  }
                  if (isRelBasPsndoc) {
                    hmRelVO.put("bd_psnbasdoc", baspsnvo);
                  }
                  HIDelegator.getPsnInf().updateRelTable(hmRelVO);
                }
              }
            }
            this.parent.setBatchAddSet(false);
            
            closeOK();
          }
          this.parent.deptTreeValueChanged(null, true);
        }
        catch (Exception ec)
        {
          reportException(ec);
          MessageDialog.showErrorDlg(this, 
            NCLangRes.getInstance().getStrByID(
            "nc_hr_pub", "UPPnc_hr_pub-000094"), 
            
            ec.getMessage());
        }
      }
    }
    else if (e.getSource() == getUIButtonCancel())
    {
      this.parent.setBatchAddSet(false);
      closeCancel();
    }
  }
  
  /**
	 * 数据校验： （1）校验唯一组合字段的值是否重复 （2）对 学历子集的最高学历的校验：只能有一个最高学历
	 * 
	 * @param hmData
	 * @param datadests
	 * @param tablecode
	 * @return
	 */
	private boolean datasValidate(java.util.HashMap hmData,
			nc.vo.pub.CircularlyAccessibleValueObject[] datadests,
			String tablecode) {
		if (datadests == null || datadests.length == 0) {
			return false;
		}

		String msg = null;
		//效率优化--连接数 start
//		for (int i = 0; i < datadests.length; i++) {
//		CircularlyAccessibleValueObject vo = datadests[i];
//		try {
//		if (PubDelegator.getISetdict().checkRecordUnique(tablecode, (String) vo
//		.getAttributeValue("pk_psnbasdoc"), parent.getUniqueFld(tablecode), vo, null, "pk_psndoc_sub", 0)) {
////		if (SetdictBO_Client.checkRecordUnique(tablecode, (String) vo
////		.getAttributeValue("pk_psnbasdoc"), parent
////		.getUniqueFld(tablecode), vo, null, "pk_psndoc_sub", 0)) {
//		FlddictVO[] fldvos = parent.getUniqueFld(tablecode);
//		String psnname = (String) getUIPanelSecondBillCard()
//		.getBillModel().getValueAt(i, "psnname");
//		msg = nc.ui.ml.NCLangRes.getInstance().getStrByID("600704",
//		"UPP600704-000091")/* @res "第" */
//		+ (i + 1)
//		+ nc.ui.ml.NCLangRes.getInstance().getStrByID(
//		"600704", "UPP600704-000208")/* @res "行" */
//		+ ","
//		+ psnname
//		+ nc.ui.ml.NCLangRes.getInstance().getStrByID(
//		"600704", "UPP600704-000209")/*
//		* @res
//		* "在当前子集"
//		*/
//		+ nc.ui.ml.NCLangRes.getInstance().getStrByID(
//		"600704", "UPP600704-000197");
//		for (int j = 0; j < fldvos.length; j++) {
//		msg += fldvos[j].getFldname();
//		if (j < fldvos.length - 1) {
//		msg += ",";
//		}
//		}
//		msg += ","
//		+ nc.ui.ml.NCLangRes.getInstance().getStrByID(
//		"600704", "UPP600704-000196");

//		}
//		} catch (Exception eSub) {
//		reportException(eSub);
//		}
//		}
		FlddictVO[] fldvos = parent.getUniqueFld(tablecode);
		boolean[] isuniques = null;
		try {
			isuniques = PubDelegator.getISetdict().checkRecordsUnique(tablecode, fldvos, datadests, null, "pk_psndoc_sub", 0);
		}catch (Exception eSub) {
			reportException(eSub);
		}
		for (int i = 0; i < datadests.length; i++) {
			if (isuniques[i]) {
				String psnname = (String) getUIPanelSecondBillCard()
				.getBillModel().getValueAt(i, "psnname");
				msg = nc.ui.ml.NCLangRes.getInstance().getStrByID("600704",
				"UPP600704-000091")/* @res "第" */
				+ (i + 1)
				+ nc.ui.ml.NCLangRes.getInstance().getStrByID(
						"600704", "UPP600704-000208")/* @res "行" */
						+ ","
						+ psnname
						+ nc.ui.ml.NCLangRes.getInstance().getStrByID(
								"600704", "UPP600704-000209")/*
								 * @res
								 * "在当前子集"
								 */
								+ nc.ui.ml.NCLangRes.getInstance().getStrByID(
										"600704", "UPP600704-000197");
				for (int j = 0; j < fldvos.length; j++) {
					msg += fldvos[j].getFldname();
					if (j < fldvos.length - 1) {
						msg += ",";
					}
				}
				msg += ","
					+ nc.ui.ml.NCLangRes.getInstance().getStrByID(
							"600704", "UPP600704-000196");

			}

		}
		//效率优化--连接数 end
		if ("hi_psndoc_edu".equalsIgnoreCase(tablecode)) {
			for (int i = 0; i < datadests.length; i++) {
				int count = 0;
				String pk_psnbasdoc = (String) datadests[i].getAttributeValue("pk_psnbasdoc");
				nc.vo.pub.CircularlyAccessibleValueObject[] existedData = (nc.vo.pub.CircularlyAccessibleValueObject[]) hmData.get(pk_psnbasdoc);
				count += getCount(datadests[i]);
				for (int j = 0; j < existedData.length; j++) {
					count += getCount(existedData[j]);
				}
				if (count > 1) {
					String psnname = (String) datadests[i].getAttributeValue("psnname");
					msg = 
//						nc.ui.ml.NCLangRes.getInstance().getStrByID("600704",
//						"UPP600704-000091")/* @res "第" */
//						+ (i + 1)
//						+ nc.ui.ml.NCLangRes.getInstance().getStrByID(
//						"600704", "UPP600704-000208")/* @res "行" */
//						+ ","
//						+ 
						psnname
						+ "  "
						+ nc.vo.ml.NCLangRes4VoTransl.getNCLangRes()
						.getStrByID("600704", "UPP600704-100151")/*
						 * @res
						 * "最高学历只能有一个"
						 */;
					break;
				}
			}
		}
		if (msg != null) {
			MessageDialog.showWarningDlg(this, "", msg);
			return false;
		} else {
			return true;
		}

	}
  
  private int getCount(CircularlyAccessibleValueObject data)
  {
    int intcount = 0;
    Object obj = data.getAttributeValue("lasteducation");
    if ((obj != null) && (obj.toString().equalsIgnoreCase("Y"))) {
      intcount++;
    }
    return intcount;
  }
  
  private GeneralVO[] setSecondTableData(GeneralVO[] psnBasics, GeneralVO vo)
  {
    String[] psnFields = { "pk_psnbasdoc", "pk_psndoc", "psncode", 
      "psnname", "deptname" };
    if (psnBasics != null) {
      for (int i = 0; i < psnBasics.length; i++)
      {
        Object[] values = new Object[psnFields.length];
        for (int i1 = 0; i1 < values.length; i1++) {
          values[i1] = psnBasics[i].getAttributeValue(psnFields[i1]);
        }
        psnBasics[i] = new GeneralVO();
        for (int i2 = 0; i2 < psnFields.length; i2++) {
          psnBasics[i].setAttributeValue(psnFields[i2], values[i2]);
        }
        if (vo != null)
        {
          String[] fieldcodes = vo.getAttributeNames();
          if (fieldcodes != null) {
            for (int j = 0; j < fieldcodes.length; j++)
            {
              BillItem srcItem = getUIPanelFirst().getHeadItem(fieldcodes[j]);
              saveTableRowData(i, srcItem, psnBasics);
              
              psnBasics[i].setAttributeValue(fieldcodes[j], vo.getAttributeValue(fieldcodes[j]));
              String pkfield = "$" + fieldcodes[j];
              Object pkvalue = vo.getAttributeValue(pkfield);
              if (pkvalue != null) {
                psnBasics[i].setAttributeValue(pkfield, pkvalue);
              }
            }
          }
        }
      }
    }
    getSortableBillScrollTable().getTableModel().setBodyDataVO(psnBasics);
    getSortableBillScrollTable().getTableModel().execLoadFormula();
    getSortableBillScrollTable().repaint();
    
    return psnBasics;
  }
  
  public void afterEdit(BillEditEvent e)
  {
    String billKey = e.getKey();
    BillItem srcItem = getUIPanelSecondBillCard().getBodyItem(billKey);
    int selectRow = e.getRow();
    saveTableRowData(selectRow, srcItem, this.psnLists);
  }
  
  private void saveTableRowData(int selectRow, BillItem srcItem, GeneralVO[] psnListsTemp)
  {
    if (srcItem != null)
    {
      String billKey = srcItem.getKey();
      if (psnListsTemp != null) {
        if (srcItem.getDataType() == 6)
        {
          UIComboBox box = (UIComboBox)srcItem.getComponent();
          Object obj = box.getSelectedItem();
          if ((obj instanceof ComboBillItem.ComboItem))
          {
            ComboBillItem.ComboItem comboItem = (ComboBillItem.ComboItem)box
              .getSelectedItem();
            Object value = comboItem.getValue();
            psnListsTemp[selectRow].setAttributeValue(
              "$" + billKey, value);
            psnListsTemp[selectRow].setAttributeValue(billKey, 
              comboItem.getName());
          }
        }
        else if (srcItem.getDataType() == 5)
        {
          psnListsTemp[selectRow].setAttributeValue(billKey, 
            ((UIRefPane)srcItem.getComponent()).getRefName());
          psnListsTemp[selectRow].setAttributeValue("$" + 
            billKey, 
            ((UIRefPane)srcItem.getComponent()).getRefPK());
        }
        else
        {
          psnListsTemp[selectRow].setAttributeValue(billKey, 
            srcItem.getValue());
        }
      }
    }
  }
  
  public void bodyRowChange(BillEditEvent e) {}
  
  private void initBillItems(BillItem[] items)
  {
    try
    {
      for (int i = 0; i < items.length; i++)
      {
        final BillItem item = items[i];
        if (item.getDataType() == 5)
        {
          final UIRefPane ref = (UIRefPane)item.getComponent();
          ref.setReturnCode(false);
          ref.setButtonFireEvent(true);
          
          String refNodeName = ref.getRefNodeName();
          if (item.getPos() == 1)
          {
            if (("tra_type".equals(item.getKey())) || 
              ("tra_mode".equals(item.getKey()))) {
              ref.setMultiSelectedEnabled(true);
            }
            ref.addValueChangedListener(new ValueChangedListener()
            {
              public void valueChanged(ValueChangedEvent e)
              {
                String pk = ref.getRefPK();
                String itemKey = item.getKey();
                if (("tra_type".equals(itemKey)) || 
                  ("tra_mode".equals(itemKey)))
                {
                  String[] traTypePks = ref.getRefPKs();
                  if (traTypePks != null) {
                    for (int jj = 0; jj < traTypePks.length; jj++) {
                      if (jj == 0)
                      {
                        pk = traTypePks[jj];
                      }
                      else
                      {
                        pk = pk + ",";
                        pk = pk + traTypePks[jj];
                      }
                    }
                  }
                }
              }
            });
          }
          if (("pk_corp".equals(item.getKey())) || 
            ("pk_corptraining".equals(item.getKey())) || 
            ("pk_majorcorp".equals(item.getKey())) || 
            ("pk_corpperson".equals(item.getKey())))
          {
            ref.setPK(Global.getCorpPK());
            ref.setEditable(false);
            ref.setEnabled(false);
          }
          if (("部门档案".equals(refNodeName)) || 
            ("部门档案HR".equals(refNodeName))) {
            if (ref != null) {
              if (getModuleName().equals("600704"))
              {
                ref.getRefModel().setUseDataPower(false);
                setWhereToModel(
                  ref.getRefModel(), 
                  "((bd_deptdoc.canceled = 'N' and bd_deptdoc.hrcanceled = 'N') or ( bd_deptdoc.canceled is null and bd_deptdoc.hrcanceled is null))");
              }
              else
              {
                setWhereToModel(ref.getRefModel(), 
                  "((bd_deptdoc.canceled = 'N') or ( bd_deptdoc.canceled is null) )");
              }
            }
          }
          if ((("人员档案HR".equals(refNodeName)) || 
            ("人员档案".equals(refNodeName))) && 
            (ref != null))
          {
            ref.getRefModel().setUseDataPower(false);
            if (getModuleName().equals("600704")) {
              setWhereToModel(
                ref.getRefModel(), 
                "((bd_deptdoc.canceled = 'N' and bd_deptdoc.hrcanceled = 'N') or ( bd_deptdoc.canceled is null and bd_deptdoc.hrcanceled is null))");
            } else {
              setWhereToModel(ref.getRefModel(), 
                "((bd_deptdoc.canceled = 'N') or ( bd_deptdoc.canceled is null) )");
            }
          }
          if (Util.isDefdocPk(refNodeName)) {
            Util.setDefdocRefModel(ref, refNodeName, 
              item.getTableCode(), item.getKey());
          }
        }
        else if (item.getDataType() == 6)
        {
          UIComboBox box = (UIComboBox)item.getComponent();
          box.setSelectedIndex(0);
        }
      }
    }
    catch (Exception e)
    {
      e.printStackTrace();
      reportException(e);
    }
  }
  
  private String setWhereToModel(AbstractRefModel refmodel, String whereSql)
  {
    String sql = refmodel.getQuerySql();
    String whereSqlTemp = refmodel.getWherePart();
    if ((whereSqlTemp != null) && (whereSqlTemp.length() > 0) && 
      (whereSql != null) && (whereSql.length() > 0))
    {
      if (whereSqlTemp.indexOf(whereSql) < 0) {
        whereSqlTemp = whereSqlTemp + " and " + whereSql;
      }
    }
    else {
      whereSqlTemp = whereSql;
    }
    refmodel.setWherePart(whereSqlTemp);
    if ((refmodel instanceof SpaDeptdocRefTreeModel))
    {
      SpaDeptdocRefTreeModel refTreemodel = (SpaDeptdocRefTreeModel)refmodel;
      refTreemodel.setClassWherePart(whereSql);
    }
    else if ((refmodel instanceof SpaDeptdocRefGridTreeModel))
    {
      SpaDeptdocRefGridTreeModel refTreemodel = (SpaDeptdocRefGridTreeModel)refmodel;
      refTreemodel.setClassWherePart(whereSql);
    }
    return whereSqlTemp;
  }
}
