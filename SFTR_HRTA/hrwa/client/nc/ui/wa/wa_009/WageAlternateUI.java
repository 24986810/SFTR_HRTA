package nc.ui.wa.wa_009;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.bs.uap.lock.PKLock;
import nc.bs.uap.sf.facility.SFServiceFacility;
import nc.hr.utils.ResHelper;
import nc.itf.uap.IUAPQueryBS;
import nc.itf.uap.busibean.IRefForTempTable;
import nc.itf.wa.hrp.pub.IHRPWABtn;
import nc.itf.wa.wa_hrppub.IDeptPsnItem;
import nc.jdbc.framework.processor.BeanListProcessor;
import nc.jdbc.framework.processor.ColumnListProcessor;
import nc.ui.bd.ref.IRefConst;
import nc.ui.decimal.operator.ThMarker;
import nc.ui.hr.comp.combinesort.Attribute;
import nc.ui.hr.comp.combinesort.SortConfigDialog;
import nc.ui.hr.comp.quicksearch.IQuickSearch;
import nc.ui.hr.comp.quicksearch.QsbUtil;
import nc.ui.hr.comp.quicksearch.SearchType;
import nc.ui.hr.comp.sort.Pair;
import nc.ui.hr.frame.HrQueryDialog;
import nc.ui.hr.frame.IQueryFieldValueEditor;
import nc.ui.hr.global.Global;
import nc.ui.hr.global.HiInfoForQuery;
import nc.ui.ml.NCLangRes;
import nc.ui.pub.ButtonObject;
import nc.ui.pub.ClientEnvironment;
import nc.ui.pub.ToftPanel;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UICheckBox;
import nc.ui.pub.beans.UIComboBox;
import nc.ui.pub.beans.UIDialog;
import nc.ui.pub.beans.UILabel;
import nc.ui.pub.beans.UIPanel;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.beans.UIScrollPane;
import nc.ui.pub.beans.UITextArea;
import nc.ui.pub.bill.BillCardPanel;
import nc.ui.pub.bill.BillEditEvent;
import nc.ui.pub.bill.BillEditListener;
import nc.ui.pub.bill.BillItem;
import nc.ui.pub.bill.BillModel;
import nc.ui.pub.bill.BillScrollPane;
import nc.ui.pub.msg.SwingWorker;
import nc.ui.pub.print.PrintDirectEntry;
import nc.ui.pub.print.datastruct.Constants;
import nc.ui.pub.tools.BannerDialog;
import nc.ui.querytemplate.meta.FilterMeta;
import nc.ui.sm.login.ClientAssistant;
import nc.ui.sm.login.LoginPanelRes;
import nc.ui.trade.business.HYPubBO_Client;
import nc.ui.wa.alert.HRAlertUserData;
import nc.ui.wa.pub.ItemSetDlg;
import nc.ui.wa.pub.PrintManagerForWA;
import nc.ui.wa.pub.WADelegator;
import nc.ui.wa.wa_050.WaContrastDlg;
import nc.ui.wa.wa_053.AlarmAuditInfomationDlg;
import nc.ui.wabm.category.WaClassRefUIModel;
import nc.ui.wabm.global.ItemFilterEditorFactory;
import nc.ui.wabm.global.QueryConditionDlgBuilder;
import nc.ui.wabm.global.RefNomalPartPanel;
import nc.ui.wabm.global.WAGlobalData;
import nc.ui.wabm.global.WaGlobal;
import nc.ui.wabm.print.PrintOnDept;
import nc.ui.wabm.print.TableColResize;
import nc.vo.bd.refdatatemp.RefdatatempVO;
import nc.vo.comw.validator.Validator;
import nc.vo.hr.tools.pub.GeneralVO;
import nc.vo.hr.tools.pub.StringUtils;
import nc.vo.hr.wa_bm.BmcheckVO;
import nc.vo.jcom.util.Convertor;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.util.StringOperator;
import nc.vo.wa.func.WherePartUtil;
import nc.vo.wa.pub.WaClassStateHelper;
import nc.vo.wa.pub.WaClassStateHelper.WaStates;
import nc.vo.wa.wa_002.ClassitemVO;
import nc.vo.wa.wa_008.WaclassVO;
import nc.vo.wa.wa_009.CaculateInfoVO;
import nc.vo.wa.wa_009.DataVO;
import nc.vo.wa.wa_009.PsnConfirmVO;
import nc.vo.wa.wa_009.ReCacuVO;
import nc.vo.wa.wa_019.PeriodVO;
import nc.vo.wa.wa_hrp_002.DeptSumVO;

import org.apache.commons.lang.ArrayUtils;

/**
 * 薪资发放主界面
 *
 * 创建日期：(2001-5-18 9:02:19) fun_code 60131004
 *
 * Modify History: v57 多部门发薪 2010-09-09 xuhw<BR>
 * Modify History: v57 显示设置按用户保存 2010-09-09 xuhw<BR>
 *
 * @author：张森
 */
public class WageAlternateUI extends ToftPanel implements ListSelectionListener, BillEditListener, nc.ui.pub.print.IDataSource, IQueryFieldValueEditor, IQuickSearch {

    private static final long serialVersionUID = 6327667054495955057L;

    private final ButtonObject m_boSearch = new ButtonObject(NCLangRes.getInstance().getStrByID("common", "UC001-0000006"), NCLangRes.getInstance().getStrByID("common", "UC001-0000006"), 0, "查询");

    private final ButtonObject m_boPosition = new ButtonObject(NCLangRes.getInstance().getStrByID("60131004", "UPT10080604-000004"), NCLangRes.getInstance().getStrByID("60131004",
    "UPT10080604-000004"), 0, "定位"); /* -=notranslate=- */

    private final ButtonObject m_boUpdate = new ButtonObject(nc.ui.ml.NCLangRes.getInstance().getStrByID("common", "UC001-0000045"), nc.ui.ml.NCLangRes.getInstance().getStrByID("common",
        "UC001-0000045"), 0, "修改"); /* -=notranslate=- */

    private final ButtonObject m_boRefresh = new ButtonObject(NCLangRes.getInstance().getStrByID("common", "UC001-0000009"), NCLangRes.getInstance().getStrByID("common", "UC001-0000009"), 0, "刷新"); /*
                                                                                                     * -=notranslate = -
                                                                                                     */

    private final ButtonObject m_boSave = new ButtonObject(NCLangRes.getInstance().getStrByID("common", "UC001-0000001"), NCLangRes.getInstance().getStrByID("common", "UC001-0000001"), 0, "保存");

    private final ButtonObject m_boCancel = new ButtonObject(NCLangRes.getInstance().getStrByID("common", "UC001-0000008"), NCLangRes.getInstance().getStrByID("common", "UC001-0000008"), 0, "取消");

    private final ButtonObject m_boReplace = new ButtonObject(NCLangRes.getInstance().getStrByID("60131004", "UPT60131007-000007"), NCLangRes.getInstance()
        .getStrByID("60131004", "UPT60131007-000007"), 0, "替换");

    private final ButtonObject m_boReCal = new ButtonObject(NCLangRes.getInstance().getStrByID("60131004", "UPT60131007-000008"), NCLangRes.getInstance().getStrByID("60131004", "UPT60131007-000008"),
        0, "计算");

    // add for V57 start
    private final ButtonObject m_boReTotal4MulDeptItem = new ButtonObject(ResHelper.getString("6013v57","UPP6013v57-000014")//@res "多部门数据汇总"
, ResHelper.getString("6013v57","UPP6013v57-000014")//@res "多部门数据汇总"
, 0,"多部门数据汇总");	/*-=notranslate=-*/
    private final ButtonObject m_boWatch4MulDeptItem = new ButtonObject(ResHelper.getString("6013v57","UPP6013v57-000015")//@res "多部门数据查看"
, ResHelper.getString("6013v57","UPP6013v57-000015")//@res "多部门数据查看"
, 0, "多部门数据查看");	/*-=notranslate=-*/
    // add for V57 end
    private final ButtonObject m_boReTotal = new ButtonObject(NCLangRes.getInstance().getStrByID("60131004", "UPT60131004-000007"), NCLangRes.getInstance()
        .getStrByID("60131004", "UPT60131004-000007"), 0, "汇总");

    private final ButtonObject m_boCheck = new ButtonObject(NCLangRes.getInstance().getStrByID("60131004", "UPT60330407-000002"), NCLangRes.getInstance().getStrByID("60131004", "UPT60330407-000002"),
        0, "审核");

    private final ButtonObject m_boCancelCheck = new ButtonObject(NCLangRes.getInstance().getStrByID("60131004", "UPT60131004-000337"), NCLangRes.getInstance().getStrByID("60131004",
        "UPT60131004-000337"), 0, "取消审核");

    private final ButtonObject m_boReCheck = new ButtonObject(NCLangRes.getInstance().getStrByID("60131004", "UPT60131019-000002"), NCLangRes.getInstance()
        .getStrByID("60131004", "UPT60131019-000002"), 0, "复审");

    private final ButtonObject m_boCancelReCheck = new ButtonObject(NCLangRes.getInstance().getStrByID("60131004", "UPT60131019-000003"), NCLangRes.getInstance().getStrByID("60131004",
        "UPT60131019-000003"), 0, "取消复审");

    private final ButtonObject m_boPayOff = new ButtonObject(NCLangRes.getInstance().getStrByID("60131004", "UPT60131004-000008"),
        NCLangRes.getInstance().getStrByID("60131004", "UPT60131004-000008"), 0, "发放");
    
    //update by dychf
    //private final ButtonObject m_boTxtOut = new ButtonObject("输出","输出", 0, "输出");

    private final ButtonObject m_bopsnCompute = new ButtonObject(NCLangRes.getInstance().getStrByID("60131004", "UPT60131004-000010"), NCLangRes.getInstance().getStrByID("60131004",
        "UPT60131004-000011"), 0, "时点薪资");

    private final ButtonObject m_boCancelPayOff = new ButtonObject(NCLangRes.getInstance().getStrByID("60131004", "UPT60131004-000009"), NCLangRes.getInstance().getStrByID("60131004",
        "UPT60131004-000009"), 0, "取消发放");

    private final ButtonObject m_boDispChg = new ButtonObject(NCLangRes.getInstance().getStrByID("60131004", "UPP60131004-000231"), NCLangRes.getInstance()
        .getStrByID("60131004", "UPP60131004-000231"), 0, "显示设置");

    private final ButtonObject m_boSort = new ButtonObject(nc.ui.ml.NCLangRes.getInstance().getStrByID("60131001", "UPP60131001-000130"), nc.ui.ml.NCLangRes.getInstance().getStrByID("60131001",
        "UPP60131001-000130"), 0, "排序"); /* -=notranslate=- */

    private final ButtonObject m_boPrintGroup = new ButtonObject(nc.ui.ml.NCLangRes.getInstance().getStrByID("common", "UC001-0000007"), nc.ui.ml.NCLangRes.getInstance().getStrByID("common",
        "UC001-0000007"), 0, "打印");

    private final ButtonObject m_boPrint = new ButtonObject(nc.ui.ml.NCLangRes.getInstance().getStrByID("60131001", "UPP60131001-000132"), nc.ui.ml.NCLangRes.getInstance().getStrByID("60131001",
        "UPP60131001-000132"), 0, "模板打印");

    private final ButtonObject m_boDirectPrint = new ButtonObject(nc.ui.ml.NCLangRes.getInstance().getStrByID("60131001", "UPP60131001-000134"), nc.ui.ml.NCLangRes.getInstance().getStrByID(
        "60131001", "UPP60131001-000134"), 0, "直接打印");

    private final ButtonObject m_boApproveOperation = new ButtonObject(nc.ui.ml.NCLangRes.getInstance().getStrByID("60131004", "UPP60131004-000328"), nc.ui.ml.NCLangRes.getInstance().getStrByID(
        "60131004", "UPP60131004-000328"), 0, "审核操作");

    private final ButtonObject m_btnConstrast = new ButtonObject(NCLangRes.getInstance().getStrByID("60131600", "UPT60131600-000048"), NCLangRes.getInstance().getStrByID("60131600",
        "UPT60131600-000048"), 0, "明细对比");
    
//	szh 确定
	private final ButtonObject m_btnConfirm =  new ButtonObject("确定","确定", 0, "确定");

    private ButtonObject[] m_MainButtonGroup = { m_bopsnCompute, m_boUpdate, m_boReplace, m_boReCal, m_boReTotal, m_boReTotal4MulDeptItem, m_boWatch4MulDeptItem, m_boApproveOperation, m_boPayOff,
        m_boCancelPayOff, m_boSave, m_boCancel, m_btnConstrast, m_boDispChg, m_boSort, m_boSearch, m_boRefresh,m_btnConfirm, m_boPrintGroup };

    private final ButtonObject m_boFirst = new ButtonObject(NCLangRes.getInstance().getStrByID("60131004", "UPT60131007-000016"), NCLangRes.getInstance().getStrByID("60131004", "UPT60131007-000016"),
        0, "首条");

    private final ButtonObject m_boPre = new ButtonObject(NCLangRes.getInstance().getStrByID("60131004", "UPT60131007-000014"), NCLangRes.getInstance().getStrByID("60131004", "UPT60131007-000014"),
        0, "上一条");

    private final ButtonObject m_boNext = new ButtonObject(NCLangRes.getInstance().getStrByID("60131004", "UPT60131007-000013"), NCLangRes.getInstance().getStrByID("60131004", "UPT60131007-000013"),
        0, "下一条");

    private final ButtonObject m_boLast = new ButtonObject(NCLangRes.getInstance().getStrByID("60131004", "UPT60131007-000017"), NCLangRes.getInstance().getStrByID("60131004", "UPT60131007-000017"),
        0, "末条");

    private final ButtonObject m_boUpdateOk = new ButtonObject(NCLangRes.getInstance().getStrByID("common", "UC001-0000044"), NCLangRes.getInstance().getStrByID("common", "UC001-0000044"), 0, "确定"); /* -=notranslate=- */

    private final ButtonObject m_boReturn = new ButtonObject(NCLangRes.getInstance().getStrByID("common", "UC001-0000038"), NCLangRes.getInstance().getStrByID("common", "UC001-0000038"), 0, "返回"); /* -=notranslate=- */

    private final ButtonObject[] m_ButtonGroup2 = { m_boFirst, m_boPre, m_boNext, m_boLast, m_boUpdateOk, m_boReturn };

    private BillScrollPane ivjBspWaData = null;

    private boolean loading = true;

    private WageAlternateIBO ibo = null;

    private DataVO[] waData = null;

    private HrQueryDialog selDlg;

    private String theLatestCondition = null;

    private boolean isHuiZong = false;

    private boolean needReCheck = false;

    private int selRow = -1;

    // private int selCol=-1;
    private DataVO selDataVO = null;

    private Vector<String> vecEdit = new Vector<String>();

    private String[] editableName = null;

    private String[] editableItem = null;

    private String[] editableType = null;

    private String[] itemNames = null;

    private String[] itemCodes = null;

    private String[] itemTypes = null;

    private ReplaceDlg replaceDlg = null;

    // private String whereSql = null;

    private UIPanel ivjpnlList = null;

    private String[] saBodyColName = null;

    private String[] saBodyColKeyName = null;

    private String[] saBodyColType = null;

    private int editaleSqeu[] = null;// 可编辑项目的序号

    private String userId = null;

    private BillCardPanel updateBillCardPanel = null;

    private Vector<String> vecBmLock = null;

    private String pkCorp = null;

    private boolean refreshing = false;

    private RecacuDlg recacuDlg = null;

    private boolean haveException = false;

    private int precisionForUFDouble = 2;

    private java.lang.String[] stDigitItemRefWithoutCurr = null;

    public int[] waItemWidth = null;

    public int[] waItemDecimal = null;

    public java.lang.Integer[] saBodyColDecimal = null;

    public java.lang.Integer[] saBodyColWidth = null;

    private boolean haveInitDataPower = false;
    private String deptpower = "";// 薪资数据表
    private String deptpower1 = "";// 部门表
    private String psnclpower = ""; // 人员类别权限SQL条件

    private java.lang.String orderStr = " order by bd_deptdoc.deptcode,bd_psndoc.psncode";

    private UILabel ivjUILblPayDay = null;

    private UIPanel ivjUIPnlComment = null;

    private UIRefPane ivjUIRefPayDay = null;

    private UIScrollPane ivjUISpComment = null;

    private UITextArea ivjUITaComment = null;

    private UILabel ivjUILblComment = null;

    private SharePeriodSetDlg sharePeriodSetDlg = null;

    private boolean displayNest = false;

    // 下边是支持分批审核，2003年9月29日lhp添加private StateControl bcontrol = new
    // StateControl();

    private CheckDlg checkdlg = null;// 审核

    private CheckDlg cancelcheckdlg = null;// 弃审

    boolean bok = false;

    private boolean finishinit = false;// 没有初始化
    private int updateinit = 0;// 0:没有进入修改页面,1:进入修改页面，但是没有修改2:修改
    private ItemSortFac itemsortfac = null;
    private ItemSetDlg itemSetDlg = null;
    private BillModel billmodel = null;

    // 是否打印0值参数设置的 相关标示
    private boolean printZero = true;
    private boolean getFlag = false;
    /**
     * Added by Young 2006-03-20 Start 审核查询
     */
    private UICheckBox cbxChgSearch = null;
    /**
     * Added by Young 2006-03-20 End 审核查询
     */

    IvjEventHandler ivjEventHandler = new IvjEventHandler();

    private UIPanel ivjUITopPanel = null;

    private final WaGlobal waGlobal = new WaGlobal();

    private UILabel ivjUIWaClassLabel = null;

    private nc.ui.pub.beans.UIRefPane ivjUIRefWaClass = null;

    private SortConfigDialog sortDialog = null;

    private WaContrastDlg contrastDlg = null;

    private MulDataDeptWatchDlg mulDataDeptWatchDlg = null;

    private String oldClassPk = null;

    private final int lockcol = 2;

    private UFBoolean installHRSS = null; //是否安装了自助模块

    /**
     * 记录按钮状态
     */
    public HashMap<ButtonObject, Boolean> buttonState = null;
    // add by sunxj 2010-02-23 快速查询插件 start
    protected boolean isQuickSearch = false;// 标记是否快速查询过

    // add by sunxj 2010-02-23 快速查询插件 end

    class IvjEventHandler implements nc.ui.pub.beans.ValueChangedListener {
    public void valueChanged(nc.ui.pub.beans.ValueChangedEvent event) {
        if (event.getSource() == WageAlternateUI.this.getUIRefWaClass()) {
        connEtoC1(event);
        }
    };
    }

    /**
     * WageAlternateUI 构造子注解。
     */
    public WageAlternateUI() {
    super();
    initialize();
    }

    /**
     * 初始化类。
     */
    /* 警告：此方法将重新生成。 */
    private void addListener() {
    getBspWaData().getTable().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

    getBspWaData().getFixColTable().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

    getBspWaData().setRowNOShow(true);// 序号列
    getBspWaData().setTotalRowShow(true);// 合计行
    getBspWaData().addEditListener(this); // ////////////

    ((javax.swing.DefaultListSelectionModel) getBspWaData().getTable().getSelectionModel()).removeListSelectionListener(this);
    ((javax.swing.DefaultListSelectionModel) getBspWaData().getTable().getSelectionModel()).addListSelectionListener(this);

    ((javax.swing.DefaultListSelectionModel) getBspWaData().getFixColTable().getSelectionModel()).removeListSelectionListener(this);
    ((javax.swing.DefaultListSelectionModel) getBspWaData().getFixColTable().getSelectionModel()).addListSelectionListener(this);

    ((javax.swing.DefaultListSelectionModel) getBspWaData().getRowNOTable().getSelectionModel()).removeListSelectionListener(this);
    ((javax.swing.DefaultListSelectionModel) getBspWaData().getRowNOTable().getSelectionModel()).addListSelectionListener(this);

    getBspWaData().lockTableCol(lockcol);
    // haveAddListener=true;
    }

    /**
     * 此处插入方法说明。 创建日期：(2001-6-22 9:02:27)
     *
     * @param e
     *                nc.ui.pub.bill.BillEditEvent
     */
    public void afterEdit(BillEditEvent e) {
    String value = e.getValue().toString();
    String key = e.getKey();
    int row = e.getRow();

    Object obj = waData[row].getAttributeValue(key);
    String oldValue = "";
    if (obj != null) {
        oldValue = obj.toString();
    }
    if (!value.equals(oldValue)) {
        setSaveAndCancel(true);

        if (!vecEdit.contains(String.valueOf(row))) {
        vecEdit.addElement(String.valueOf(row));
        }
    }
    }

//    /**
//     * 初始化类。
//     */
//    private void beforeInitData() {
//
//  // setButtons(m_MainButtonGroup);
//  if (!haveInitDataPower) {
//      haveInitDataPower = true;
//      // userId = ibo.getUserId();
//      // pkCorp = ibo.getDwbm();
//      try {
//      String[] powers = WaGlobal.getDataPower(false);
//
//      String powerSql = powers[0];
//
//      if (powerSql != null && powerSql.length() > 0) {
//          deptpower = " and wa_data.deptid in (" + powerSql + ")";
//          deptpower1 = " and bd_deptdoc.pk_deptdoc in (" + powerSql + ")";
//      }
//
//      // 人员类别权限
//      powerSql = powers[1];
//      if (powerSql != null && powerSql.length() > 0) {
//          psnclpower = " and wa_data.psnclid in (" + powerSql + ")";
//      }
//
//      } catch (Exception e) {
//      e.printStackTrace();
//      Logger.error(e.getMessage(), e);
//      }
//  }
//
//    }

     public  String getTempDataSql(String inputSql) throws BusinessException
        {
            String pk_ts;
            String refName;
            pk_ts = null;
            refName = "DeptPower";
            List list = (List)WADelegator.getRecaData().executeQuery(inputSql, new ColumnListProcessor());

     if(list == null || list.size() == 0)
                return null;
            try
            {
                RefdatatempVO vos[] = new RefdatatempVO[list.size()];
                for(int i = 0; i < list.size(); i++)
                {
                    vos[i] = new RefdatatempVO();
                    vos[i].setPk_selecteddata((String)list.get(i));
                    vos[i].setPk_corp(Global.getCorpPK());
                    vos[i].setCuserid(Global.getUserID());
                    vos[i].setRefnodename(refName);
                    vos[i].setPk_ts("temp");
                }

                IRefForTempTable iRefForTempTable = NCLocator.getInstance().lookup(IRefForTempTable.class);
                pk_ts = iRefForTempTable.saveRefDataTempVOs(vos);
            }
            catch(Exception e)
            {
                Logger.error(e.getMessage(), e);
            }
            return (new StringBuilder(" select pk_selecteddata from bd_refdatatemp where pk_corp='")).append(Global.getCorpPK()).append("' and cuserid = '").append(Global.getUserID()).append("' ").append(" and refnodename='").append(refName).append("' and pk_ts = '").append(pk_ts).append("'").toString();
        }


   /**
   * 初始化类。
   */
  private void beforeInitData() {

    // setButtons(m_MainButtonGroup);
    if (!haveInitDataPower) {
        haveInitDataPower = true;
        // userId = ibo.getUserId();
        // pkCorp = ibo.getDwbm();
        try {
        String[] powers = WaGlobal.getDataPower(false);

        String powerSql = powers[0];

        if (powerSql != null && powerSql.length() > 0) {
            deptpower = " and wa_data.deptid in (" + powerSql + ")";
            deptpower1 = " and bd_deptdoc.pk_deptdoc in (" + powerSql + ")";
        }

        // 人员类别权限
        powerSql = powers[1];
        if (powerSql != null && powerSql.length() > 0) {
            psnclpower = " and wa_data.psnclid in (" + powerSql + ")";
        }

        } catch (Exception e) {
        e.printStackTrace();
        Logger.error(e.getMessage(), e);
        }
    }

  }

     /**
     *说明：如果人员比较少，使用临时表反而会降低效率。
     *原因：估计是临时表没有索引
     * 初始化类。
     */
//  private void beforeInitData() {
//
////        setButtons(m_MainButtonGroup);
//      if(!haveInitDataPower){
//          haveInitDataPower = true;
//          try {
//              String[] powers = WaGlobal.getDataPower(false);
//              String deptpowerSql = powers[0];
//              if (deptpowerSql != null && deptpowerSql.length() > 0) {
//                  deptpowerSql = getTempDataSql(deptpowerSql);
//                  deptpower = " and wa_data.deptid in (" + deptpowerSql + ")";
//                  deptpower1 = " and bd_deptdoc.pk_deptdoc in (" + deptpowerSql + ")";
//              }
//
//              // 人员类别权限
//              String psnclpowerSql = powers[1];
//              if (psnclpowerSql != null && psnclpowerSql.length() > 0) {
//                  psnclpowerSql = getTempDataSql(psnclpowerSql);
//                  psnclpower = " and wa_data.psnclid in (" + psnclpowerSql + ")";
//              }
//          } catch (Exception e) {
//              e.printStackTrace();
//              Logger.error(e.getMessage(), e);
//          }
//      }
//  }

    /**
     * 根据类别的状态设置界面的按钮
     *
     * @author zhangg on 2009-8-14
     * @param waclassVO
     * @throws BusinessException
     */
    public void resetButtonSate(WaclassVO waclassVO) throws BusinessException {
    ibo.setWaClassVO(waclassVO);
    WaStates states = waclassVO.getCurrentstate();

    Logger.debug("**************类别现在状态*******************");
    Logger.debug(states + WaClassStateHelper.translateState(states));
    Logger.debug("*********************************************");

    // 是否是汇总类别 需要单独处理汇总薪资类别的状态
    isHuiZong = (waclassVO.getIcollectflag().intValue() == 1);

    // 类别是否需要复审
    needReCheck = (waclassVO.getIrecheckflag().intValue() == 1);

    buttonState = new HashMap<ButtonObject, Boolean>();// 记录所有可以使用的按钮
    if ((states == WaStates.NO_PERIOD_DATA_FOUND || states == WaStates.NO_WA_DATA_FOUND)) {
        setButtonExceptionState();
        throw new BusinessException(WaClassStateHelper.translateState(states));
    }

    Boolean enable = new Boolean(true);
   // buttonState.put(m_boTxtOut, enable);
    buttonState.put(m_boSearch, enable);// 查询
    buttonState.put(m_boRefresh, enable);
    buttonState.put(m_boPosition, enable);// 定位
    buttonState.put(m_boDispChg, enable);// 显示设置
    buttonState.put(m_boSort, enable);// 排序
    buttonState.put(m_boPrintGroup, enable);// 打印
    buttonState.put(m_boPrint, enable);// 模板打印
    buttonState.put(m_boDirectPrint, enable);// 直接打印
    buttonState.put(m_btnConstrast, enable);// 明细对比
    
    boolean flag = false;
    
    String userid = ClientEnvironment.getInstance().getUser().getPrimaryKey();
    
    PsnConfirmVO[] vos = (PsnConfirmVO[])HYPubBO_Client.queryByCondition(PsnConfirmVO.class,
    		"  pk_waclass='"+waclassVO.getPrimaryKey()+"' and isnull(dr,0)=0 and pk_corp='"+waclassVO.getPk_corp()+"' and vconfirmpsn='"+userid+"' and vyear='"+waclassVO.getCyear()+"' and vperiod='"+waclassVO.getCperiod()+"' ");
    
    if(vos!=null&&vos.length>0){
    	flag = true;
    }
    if (!isInstallHRSS()) {
        buttonState.put(m_boWatch4MulDeptItem, false); // 查看多部门薪资项目
    } else {
        buttonState.put(m_boWatch4MulDeptItem, true); // 查看多部门薪资项目
    }
    if (waclassVO.getPayoffflag().intValue() != 0) {
        buttonState.put(m_bopsnCompute, enable);// 时点薪资
    }
    if(waclassVO.getPrimaryKey().equals(IHRPWABtn.PK_JIANG)||waclassVO.getPrimaryKey().equals(IHRPWABtn.PK_JINTIE)
    		||waclassVO.getPrimaryKey().equals(IHRPWABtn.PK_LAOWU)||flag){
    	
    }else if (states == WaStates.CLASS_WITHOUT_RECACULATED || states == WaStates.CLASS_RECACULATED_WITHOUT_CHECK || states == WaStates.SELECTED_WA_DATA_NOT_ALL_RECACULATED_NONE_CHECKED
        || states == WaStates.SELECTED_WA_DATA_ALL_RECACULATED_NONE_CHECKED || states == WaStates.SELECTED_WA_DATA_NOT_ALL_RECACULATED_EXIST_CHECKED) {
        buttonState.put(m_boReplace, enable);// 替换
        buttonState.put(m_boReCal, enable);// 计算
        buttonState.put(m_boReTotal4MulDeptItem, enable);// 汇总多部门薪资项目

        buttonState.put(m_btnConfirm, enable);// 计算
        
        if (isHuiZong) {
        buttonState.put(m_boReTotal, enable);// 汇总
        }

        if (states == WaStates.CLASS_RECACULATED_WITHOUT_CHECK || states == WaStates.SELECTED_WA_DATA_ALL_RECACULATED_NONE_CHECKED
            || states == WaStates.SELECTED_WA_DATA_NOT_ALL_RECACULATED_NONE_CHECKED) {
        buttonState.put(m_boApproveOperation, enable);
        buttonState.put(m_boCheck, enable);
        }
        if (states == WaStates.SELECTED_WA_DATA_NOT_ALL_RECACULATED_EXIST_CHECKED) {
        buttonState.put(m_boApproveOperation, enable);
        buttonState.put(m_boCancelCheck, enable);
        }
    } else if (states == WaStates.CLASS_CHECKED_WITHOUT_PAY || states == WaStates.SELECTED_WA_DATA_ALL_RECACULATED_EXIST_CHECKED || states == WaStates.CLASS_CHECKED_WITHOUT_APPROVE
        // liangxr 部分复审三个状态
        || states == WaStates.SELECTED_WA_DATA_ALL_RECHECKED_CLASS_NONE_RECHECKED || states == WaStates.SELECTED_WA_DATA_ALL_CHECKED_EXIST_RECHECKED
        || states == WaStates.SELECTED_WA_DATA_ALL_CHECKED_NONE_RECHECKED) {
        buttonState.put(m_boApproveOperation, enable);

        if (states == WaStates.CLASS_CHECKED_WITHOUT_APPROVE) {
        if (needReCheck) {
            // 需要复审的
            buttonState.put(m_boCancelReCheck, enable);
        } else {
            // 不需要复审的
            buttonState.put(m_boCancelCheck, enable);
        }

        } else if (states == WaStates.SELECTED_WA_DATA_ALL_RECHECKED_CLASS_NONE_RECHECKED) {
        buttonState.put(m_boCancelReCheck, enable);
        } else if (states == WaStates.SELECTED_WA_DATA_ALL_CHECKED_EXIST_RECHECKED) {
        buttonState.put(m_boCancelReCheck, enable);
        buttonState.put(m_boReCheck, enable);
        } else if (states == WaStates.SELECTED_WA_DATA_ALL_CHECKED_NONE_RECHECKED) {
        buttonState.put(m_boReCheck, enable);
        buttonState.put(m_boCancelCheck, enable);
        } else {
        // CLASS_CHECKED_WITHOUT_PAY 与
        // SELECTED_WA_DATA_ALL_RECACULATED_EXIST_CHECKED
        buttonState.put(m_boCancelCheck, enable);
        if (states == WaStates.CLASS_CHECKED_WITHOUT_PAY) {
            buttonState.put(m_boPayOff, enable);
        } else if (states == WaStates.SELECTED_WA_DATA_ALL_RECACULATED_EXIST_CHECKED) {
            // SELECTED_WA_DATA_ALL_RECACULATED_EXIST_CHECKED
            buttonState.put(m_boCheck, enable);
        }
        }
    }

    else if (states == WaStates.CLASS_RECHECKED_WITHOUT_PAY) {
        buttonState.put(m_boApproveOperation, enable);
        buttonState.put(m_boCancelReCheck, enable);
        buttonState.put(m_boPayOff, enable);
    } else if (states == WaStates.CLASS_CHECKED_WITHOUT_RECHECK) {
        buttonState.put(m_boApproveOperation, enable);
        buttonState.put(m_boReCheck, enable);
        buttonState.put(m_boCancelCheck, enable);
    } else if (states == WaStates.CLASS_IS_APPROVED) {
        buttonState.put(m_boPayOff, enable);
    } else if (states == WaStates.CLASS_ALL_PAY) {
        buttonState.put(m_boCancelPayOff, enable);
    } else if (states == WaStates.CLASS_MONTH_END || states == WaStates.CLASS_IN_APPROVEING || states == WaStates.NO_SELECTED_WA_DATA_FOUND) {
        //
    } else if (states == WaStates.SELECTED_WA_DATA_ALL_CHECKED_CLASS_NONE_CHECKED) {
        // 此处不需要判断是否需要复审。因为是针对wa_data 的数据，所以肯定是第一次审核的数据
        buttonState.put(m_boApproveOperation, enable);
        buttonState.put(m_boCancelCheck, enable);

    }

    // //单独处理汇总薪资类别
    // if(isHuiZong){
    // //
    // if(states == WaStates.NO_WA_DATA_FOUND){
    // //是最新期间以后的期间
    // buttonState.put(m_boReplace, enable);// 替换
    // buttonState.put(m_boSearch, enable);// 查询
    // buttonState.put(m_boReCal, enable);// 计算
    // buttonState.put(m_boReTotal, enable);// 汇总
    //
    // buttonState.put(m_boRefresh, enable);// 计算
    // buttonState.put(m_boSort, enable);// 汇总
    // }
    // else{
    // //是最新期间以前的期间
    //
    //
    // }
    //
    // }
    // 如果一个 按钮下的子按钮都是不可以用， 则其按钮也不可以用。
    // 如果一个按钮下的一个子按钮可用， 则该按钮可以用
    Iterator<ButtonObject> iterator = buttonState.keySet().iterator();
    while (iterator.hasNext()) {
        ButtonObject buttonObject = iterator.next();
        Boolean child_enable = false;
        if (buttonObject.getChildCount() > 0) {
        ButtonObject[] childButtonObjects = buttonObject.getChildButtonGroup();
        for (ButtonObject child_bt : childButtonObjects) {
            if (buttonState.get(child_bt) != null) {
            if (buttonState.get(child_bt)) {
                child_enable = true;
            }
            }
        }
        buttonState.put(buttonObject, child_enable);
        }
    }
    updateButtonStates();
    }

    /**
     * 保存前对表的处理
     *
     * @version (00-6-6 13:33:25)
     *
     * @return java.lang.String
     */
    public void beforeSave() {
    javax.swing.table.TableCellEditor editor = getBspWaData().getTable().getCellEditor();
    if (editor != null) {
        Object value = editor.getCellEditorValue();
        int selCol = getBspWaData().getTable().getSelectedColumn() + 3;// 加2列固定列，后来变成3列

        getBspWaData().getTableModel().setValueAt(value, selRow, saBodyColKeyName[selCol]);

        editor.stopCellEditing();
        // getBspWaData().getTable().removeEditor();

        getBspWaData().getTable().repaint();

        if (!vecEdit.contains(String.valueOf(selRow))) {
        vecEdit.addElement(String.valueOf(selRow));
        setSaveAndCancel(true);
        }
    }

    }

    /**
     * 此处插入方法说明。 创建日期：(2001-6-22 9:02:52)
     *
     * @param e
     *                nc.ui.pub.bill.BillEditEvent
     */
    public void bodyRowChange(BillEditEvent e) {
    if (loading) {
        return;
    }

    selRow = e.getRow();
    selDataVO = waData[selRow];
    setButtonQueryState();

    }

    /**
     * 此处插入方法说明。 创建日期：(2001-8-28 20:55:01)
     *
     * @return boolean
     * @param state
     *                int
     * @throws BusinessException
     */
    public boolean chechState(int state) throws BusinessException {
    boolean ok = true;

    String reSt = null;

    if (state == -5) {
        reSt = nc.ui.ml.NCLangRes.getInstance().getStrByID("60131004", "UPP60131004-000138")/*
                                                 * @res
                                                 * "在判别该薪资类别时出现异常，修改失败，请刷新后再试！"
                                                 */;
        ok = false;
    } else if (state == -2) {
        reSt = nc.ui.ml.NCLangRes.getInstance().getStrByID("60131004", "UPP60131004-000139")/*
                                                 * @res
                                                 * "该薪资类别的薪资期间已发生改变，修改失败，请刷新后再试！"
                                                 */;
        ok = false;
    } else if (state > 0 && state != 5 && state != 6) {
        reSt = nc.ui.ml.NCLangRes.getInstance().getStrByID("60131004", "UPP60131004-000140")/*
                                                 * @res
                                                 * "该薪资类别在本薪资期间的数据已审核，不能修改，请刷新后再试！"
                                                 */;
        ok = false;
    }
    if (!ok) {
        throw new BusinessException(reSt);
    }
    return ok;
    }

    /**
     * 检验关联福利。 创建日期：(2001-6-18 16:38:37)
     */
    public boolean checkBm() {
    boolean yesAll = false;
    boolean allCheck = true;
    vecBmLock = new Vector<String>();

    try {
        if (ibo.isWa_Bm_Connecting()) {
        BmcheckVO[] bmCheckVOs = ibo.findBmByWAClass();

        if (bmCheckVOs != null && bmCheckVOs.length > 0) {
            for (BmcheckVO bmCheckVO : bmCheckVOs) {
            String pk_bm = bmCheckVO.getPk_bm_class();

            if (ibo.checkBmState(pk_bm) > 0 || yesAll) {// 已经审核
                if (nc.bs.uap.lock.PKLock.getInstance().acquireLock(pk_bm, userId, null)) {
                vecBmLock.addElement(pk_bm);
                }
            } else {// 尚未审核
                nc.vo.bm.bm_002.BmclassVO bmClassVO = WageAlternateIBO.findBmByPK(pk_bm);

                String hint = bmClassVO.getVbmclassname() + nc.ui.ml.NCLangRes.getInstance().getStrByID("60131004", "UPP60131004-000141")/*
                                                                             * @res
                                                                             * "尚未审核，是否继续？"
                                                                             */;

                int re = MessageDialog.showYesToAllNoCancelDlg(this, nc.ui.ml.NCLangRes.getInstance().getStrByID("60131004", "UPP60131004-000142")/*
                                                                                 * @res
                                                                                 * "确认"
                                                                                 */, hint);

                if (re == MessageDialog.ID_YES || re == MessageDialog.ID_YESTOALL) {

                if (nc.bs.uap.lock.PKLock.getInstance().acquireLock(pk_bm, userId, null)) {
                    vecBmLock.addElement(pk_bm);
                }

                if (re == MessageDialog.ID_YESTOALL) {
                    yesAll = true;
                }
                } else if (re == MessageDialog.ID_NO || re == MessageDialog.ID_CANCEL) {
                allCheck = false;
                break;
                }

            }

            }
        }

        }

        return allCheck;
    } catch (Exception ex) {
        // showErrorMessage("计算失败，请刷新后重试！");
        showErrorMessage(ex.getMessage());
        allCheck = false;
        return allCheck;
    }
    /*
     * finally { if(!allCheck) { freeBm(); } }
     */
    }

    /**
     * 在取消发放后把本薪资项目的wa_dataz扣税项目清空。 创建日期：(2004-7-15 13:11:32)
     *
     * @return boolean
     * @param vo
     *                nc.vo.wa.wa_009.ReCacuVO
     */
    private void deDuctThisTax(ReCacuVO vo) throws Exception {
    try {
        WADelegator.getRecaData().deDuctThisTax(vo);
    } catch (Exception e) {
        reportException(e);
        throw e;
    }
    }

    /**
     * 删掉editor
     *
     * @version (00-6-6 13:33:25)
     *
     * @return java.lang.String
     */
    public void delEditor() {
    javax.swing.table.TableCellEditor editor = getBspWaData().getTable().getCellEditor();
    if (editor != null) {
        editor.stopCellEditing();
        // getBspWaData().getTable().removeEditor();
    }

    }

    /**
     * 检验关联福利。 创建日期：(2001-6-18 16:38:37)
     */
    public void freeBm() {
    if (vecBmLock != null && vecBmLock.size() > 0) {
        for (int i = 0; i < vecBmLock.size(); i++) {
        try {
            nc.bs.uap.lock.PKLock.getInstance().releaseLock(vecBmLock.elementAt(i).toString(), userId, null);
        } catch (Exception ex) {
        }
        }
        vecBmLock = null;
    }
    }

    public void freeWaclass() throws Exception {
    try {
        Logger.error("freeWaClass:    " + "waClassPk=" + waGlobal.getWaClassPK() + "    userId=" + userId + "     table:   wa_waclass ");
        PKLock.getInstance().releaseLock(waGlobal.getWaClassPK(), userId, null);

    } catch (Exception e) {
        reportException(e);
        throw e;
    }
    }

    /**
     *
     * 得到所有的数据项表达式数组 也就是返回所有定义的数据项的表达式
     *
     */
    public java.lang.String[] getAllDataItemExpress() {
    String itemExpress[] = getDataIteMap().keySet().toArray(new String[0]);

    return itemExpress;
    }

    public HashMap<String, String> getDataIteMap() {
    HashMap<String, String> dataItemMap = new LinkedHashMap<String, String>();

    String[] itemNames = new String[145];
    itemNames[0] = nc.ui.ml.NCLangRes.getInstance().getStrByID("common", "UC000-0000147")/*
                                                 * @res
                                                 * "人员编码"
                                                 */;
    itemNames[1] = nc.ui.ml.NCLangRes.getInstance().getStrByID("common", "UC000-0001403")/*
                                                 * @res
                                                 * "姓名"
                                                 */;
    itemNames[2] = nc.ui.ml.NCLangRes.getInstance().getStrByID("common", "UC000-0004064")/*
                                                 * @res
                                                 * "部门"
                                                 */;
    itemNames[3] = nc.ui.ml.NCLangRes.getInstance().getStrByID("common", "UC000-0000140")/*
                                                 * @res
                                                 * "人员类别"
                                                 */;
    //
    itemNames[4] = nc.ui.ml.NCLangRes.getInstance().getStrByID("common", "UC000-0001857")/*
                                                 * @res
                                                 * "应发合计"
                                                 */;
    itemNames[5] = nc.ui.ml.NCLangRes.getInstance().getStrByID("common", "UC000-0001997")/*
                                                 * @res
                                                 * "扣款合计"
                                                 */;
    itemNames[6] = nc.ui.ml.NCLangRes.getInstance().getStrByID("common", "UC000-0001526")/*
                                                 * @res
                                                 * "实发合计"
                                                 */;
    itemNames[7] = nc.ui.ml.NCLangRes.getInstance().getStrByID("common", "UC000-0002708")/*
                                                 * @res
                                                 * "本次扣税"
                                                 */;
    itemNames[8] = nc.ui.ml.NCLangRes.getInstance().getStrByID("common", "UC000-0002709")/*
                                                 * @res
                                                 * "本次扣税基数"
                                                 */;
    itemNames[9] = nc.ui.ml.NCLangRes.getInstance().getStrByID("common", "UC000-0002633")/*
                                                 * @res
                                                 * "本月扣零"
                                                 */;
    itemNames[10] = nc.ui.ml.NCLangRes.getInstance().getStrByID("common", "UC000-0000023")/*
                                                 * @res
                                                 * "上月扣零"
                                                 */;
    itemNames[11] = nc.ui.ml.NCLangRes.getInstance().getStrByID("common", "UC000-0001726")/*
                                                 * @res
                                                 * "已扣税"
                                                 */;
    itemNames[12] = nc.ui.ml.NCLangRes.getInstance().getStrByID("common", "UC000-0001727")/*
                                                 * @res
                                                 * "已扣税基数"
                                                 */;
    itemNames[13] = nc.ui.ml.NCLangRes.getInstance().getStrByID("common", "UC000-0003394")/*
                                                 * @res
                                                 * "补发扣税"
                                                 */;
    itemNames[14] = nc.ui.ml.NCLangRes.getInstance().getStrByID("common", "UC000-0003395")/*
                                                 * @res
                                                 * "补发金额"
                                                 */;
    // 自定义相
    for (int i = 15; i < 134; i++) {
        itemNames[i] = nc.ui.ml.NCLangRes.getInstance().getStrByID("60390704", "UPP60390704-000143")/*
                                                     * @res
                                                     * "自定义薪资项目"
                                                     */
            + (i - 3);
    }
    itemNames[134] = nc.ui.ml.NCLangRes.getInstance().getStrByID("60390704", "UPT60391601-000001")/*
                                                     * @res
                                                     * "签名"
                                                     */;
    itemNames[135] = nc.ui.ml.NCLangRes.getInstance().getStrByID("common", "UC000-0004073")/*
                                                 * @res
                                                 * "部门编码"
                                                 */;
    itemNames[136] = nc.ui.ml.NCLangRes.getInstance().getStrByID("common", "UC000-0000151")/*
                                                 * @res
                                                 * "人数"
                                                 */;
    itemNames[137] = nc.ui.ml.NCLangRes.getInstance().getStrByID("60390704", "UPT60391601-000002")/*
                                                     * @res
                                                     * "变动类别"
                                                     */;
    itemNames[138] = nc.ui.ml.NCLangRes.getInstance().getStrByID("common", "UC000-0003914")/*
                                                 * @res
                                                 * "身份证号"
                                                 */;
    itemNames[139] = nc.ui.ml.NCLangRes.getInstance().getStrByID("common", "UC000-0003009")/*
                                                 * @res
                                                 * "社会保障号"
                                                 */;
    itemNames[140] = nc.ui.ml.NCLangRes.getInstance().getStrByID("common", "UC000-0003285")/*
                                                 * @res
                                                 * "考勤卡号"
                                                 */;

    itemNames[141] = nc.ui.ml.NCLangRes.getInstance().getStrByID("60390704", "UPT60390704-000001")/*
                                                     * @res
                                                     * "所在岗位"
                                                     */;

    itemNames[142] = nc.ui.ml.NCLangRes.getInstance().getStrByID("common", "UC000-0001912")/*
                                                 * @res
                                                 * "归属部门"
                                                 */;
    itemNames[143] = nc.ui.ml.NCLangRes.getInstance().getStrByID("common", "UC000-0001908")/*
                                                 * @res
                                                 * "归属人员类别"
                                                 */;
    itemNames[144] = nc.ui.ml.NCLangRes.getInstance().getStrByID("common", "UC000-0001909")/*
                                                 * @res
                                                 * "归属岗位"
                                                 */;

    String itemExpress[] = new String[145];

    itemExpress[0] = "psncode";
    itemExpress[1] = "psnname";
    itemExpress[2] = "deptname";
    itemExpress[3] = "psnclasscode";
    //
    itemExpress[4] = "f_1";
    itemExpress[5] = "f_2";
    itemExpress[6] = "f_3";
    itemExpress[7] = "f_4";
    itemExpress[8] = "f_5";
    itemExpress[9] = "f_6";
    itemExpress[10] = "f_7";
    itemExpress[11] = "f_8";
    itemExpress[12] = "f_9";
    itemExpress[13] = "f_10";
    itemExpress[14] = "f_11";
    // 自定义相
    for (int i = 15; i < 134; i++) {
        itemExpress[i] = "f_" + (i - 3);
    }
    itemExpress[134] = "sign";
    itemExpress[135] = "deptcode";
    itemExpress[136] = "personcount";
    itemExpress[137] = "changeclass";
    itemExpress[138] = "id";
    itemExpress[139] = "ssnum";
    itemExpress[140] = "timecardid";

    itemExpress[141] = "jobname";

    itemExpress[142] = "nestdeptname";
    itemExpress[143] = "nestpsnclasscode";
    itemExpress[144] = "nestjobname";

    for (int i = 0; i < itemExpress.length; i++) {
        dataItemMap.put(itemExpress[i], itemNames[i]);
    }

    if (getItemCodes() != null && getItemCodes().length > 0) {
        for (int i = 0; i < getItemCodes().length; i++) {
        dataItemMap.put(getItemCodes()[i], getItemNames()[i]);
        }
    }

    return dataItemMap;
    }

    public java.lang.String[] getAllDataItemNames() {
    String[] itemNames = getDataIteMap().values().toArray(new String[0]);
    return itemNames;
    }

    /**
     * 返回 BspWaData 特性值。
     *
     * @return nc.ui.pub.bill.BillScrollPane
     */
    /* 警告：此方法将重新生成。 */
    private BillScrollPane getBspWaData() {
    if (ivjBspWaData == null) {
        try {
        ivjBspWaData = new WaBillScrollPane();
        ivjBspWaData.setName("BspWaData");
        // user code begin {1}
        // user code end
        } catch (java.lang.Throwable ivjExc) {
        // user code begin {2}
        // user code end
        handleException(ivjExc);
        }
    }
    return ivjBspWaData;
    }

    /**
     * 此处插入方法说明。 创建日期：(2001-10-15 14:12:53)
     *
     * @return nc.ui.wa.wa_009.RecacuDlg
     */
    public CheckDlg getCancelCheckDlg() {
    if (cancelcheckdlg == null) {
        cancelcheckdlg = new CheckDlg(this, nc.ui.ml.NCLangRes.getInstance().getStrByID("60131004", "UPP60131004-000144")/*
                                                                 * @res
                                                                 * "弃审范围设置"
                                                                 */, 1);
    }
    return cancelcheckdlg;
    }

    /**
     * 此处插入方法说明。 创建日期：(2001-10-15 14:12:53)
     *
     * @return nc.ui.wa.wa_009.RecacuDlg
     */
    public CheckDlg getCheckDlg() {
    if (checkdlg == null) {
        checkdlg = new CheckDlg(this, nc.ui.ml.NCLangRes.getInstance().getStrByID("60131004", "UPP60131004-000055")/*
                                                             * @res
                                                             * "审核范围设置"
                                                             */, 0);
    }
    return checkdlg;
    }

    /**
     *
     * 返回依赖项的名称数组，该数据项长度只能为 1 或者 2 返回 null : 没有依赖 长度 1 : 单项依赖 长度 2 : 双向依赖
     *
     */
    public java.lang.String[] getDependentItemExpressByExpress(java.lang.String itemName) {

    return null;
    }

    /**
     * 此处插入方法说明。 创建日期：(2001-6-23 19:54:03)
     *
     * @return java.lang.String[]
     */
    public java.lang.String[] getEditableItem() {
    return editableItem;
    }

    /**
     * 此处插入方法说明。 创建日期：(2001-6-23 19:50:06)
     *
     * @return java.lang.String[]
     */
    public java.lang.String[] getEditableName() {
    return editableName;
    }

    /**
     * 此处插入方法说明。 创建日期：(2001-6-23 19:50:06)
     *
     * @return java.lang.String[]
     */
    public java.lang.String[] getEditableType() {
    return editableType;
    }

    /**
     * 此处插入方法说明。 创建日期：(2004-7-2 14:40:32)
     */
    private boolean getIsCompute() throws Exception {
    boolean bool = !WADelegator.getWaPsndocWa().isExistUnCaculatePsn(ibo.getWaClassVO(), deptpower, psnclpower);

    return bool;
    }

    /**
     * 此处插入方法说明。 创建日期：(2001-6-23 20:04:07)
     *
     * @return java.lang.String[]
     */
    public java.lang.String[] getItemCodes() {
    return itemCodes;
    }

    /**
     * 此处插入方法说明。 创建日期：(2001-6-23 20:02:51)
     *
     * @return java.lang.String[]
     */
    public java.lang.String[] getItemNames() {
    return itemNames;
    }

    /**
     * 此处插入方法说明。 创建日期：(2001-6-23 20:05:15)
     *
     * @return java.lang.String[]
     */
    public java.lang.String[] getItemTypes() {
    return itemTypes;
    }

    /*
     * 返回所有的数据项对应的内容 参数： 数据项的名字 返回： 数据项对应的内容，只能为 String[]； 如果 itemName 拥有依赖项，则：
     * 1 个依赖项：打印系统将根据依赖项的内容的顺序来判断 String[] 中的存放的数据 2 个依赖项：打印系统将根据两个依赖项的索引来决定数据
     *
     * 模板 2 的情况: [科目] ==> [100 200 300 --> 400 500 600]
     *
     * 模板 3 的情况: 如果 getDependItemNamesByName("金额") ==
     *
     * [科目 日期] ==> [100 200 300 400 500 600] 先列后行 [日期 科目] ==> [100 400 200 500
     * 300 600] 先行后列
     */
    public java.lang.String[] getItemValuesByExpress(java.lang.String itemExpress) {
    String[] values = null;
    values = printTable(values, itemExpress);

    return values;
    }

    /**
     * 返回节点号。
     *
     * @version (00-6-6 13:33:25)
     *
     * @return java.lang.String
     */
    @Override
    public String getModuleCode() {
    String code = null;// super.getModuleCode();
    if (code == null || code.trim().length() < 1) {
        code = "60131004";// 默认
    }
    return code;
    }

    /*
     * 返回该数据源对应的节点编码
     */
    public java.lang.String getModuleName() {
    return null;
    }

    /**
     * 获得排序Dialog
     *
     * @return SortCnfigDialog 可以保存排序条件的Dialog
     * @since NCHRV5.5
     */
    public SortConfigDialog getSortDialog() {
    if (sortDialog == null) {
        sortDialog = new SortConfigDialog(getTopFrame(this), true);
    }
    return sortDialog;
    }

    public void setSortItems(SortConfigDialog sortDialog) {
    String newClassPk = getUIRefWaClass().getRefPK();
    if (newClassPk.equalsIgnoreCase(oldClassPk)) {
        return;
    }

    Pair[] listSortItems = null;

    Vector<Pair> vecPair = new Vector<Pair>();

    Pair condVo = new Pair("bd_deptdoc.deptcode", NCLangRes.getInstance().getStrByID("common", "UC000-0004073")/*
                                                             * @res
                                                             * "部门编码"
                                                             */);
    vecPair.addElement(condVo);
    condVo = new Pair("bd_psndoc.psncode", NCLangRes.getInstance().getStrByID("common", "UC000-0000147")/*
                                                         * @res
                                                         * "人员编码"
                                                         */);
    vecPair.addElement(condVo);
    condVo = new Pair("bd_deptdoc.deptname", NCLangRes.getInstance().getStrByID("common", "UC000-0004069")/*
                                                         * @res
                                                         * "部门名称"
                                                         */);
    vecPair.addElement(condVo);
    condVo = new Pair("bd_psndoc.psnname", NCLangRes.getInstance().getStrByID("common", "UC000-0000135")/*
                                                         * @res
                                                         * "人员姓名"
                                                         */);
    vecPair.addElement(condVo);
    condVo = new Pair("bd_psncl.psnclasscode", NCLangRes.getInstance().getStrByID("common", "UC000-0000145")/*
                                                         * @res
                                                         * "人员类别编码"
                                                         */);
    vecPair.addElement(condVo);
    condVo = new Pair("bd_psncl.psnclassname", NCLangRes.getInstance().getStrByID("common", "UC000-0000142")/*
                                                         * @res
                                                         * "人员类别名称"
                                                         */);
    vecPair.addElement(condVo);
    condVo = new Pair("bd_psndoc.clerkcode", NCLangRes.getInstance().getStrByID("common", "UC000-0003308")/*
                                                         * @res
                                                         * "职员编码"
                                                         */);
    vecPair.addElement(condVo);
    condVo = new Pair("bd_psndoc.showorder", NCLangRes.getInstance().getStrByID("common", "UC000-0000129")/*
                                                         * @res
                                                         * "人员"
                                                         */+ NCLangRes.getInstance().getStrByID("common", "UC000-0001821")/*
                                         * @res
                                         * "序号"
                                         */);
    vecPair.addElement(condVo);
    condVo = new Pair("om_job.jobcode", NCLangRes.getInstance().getStrByID("common", "UC000-0001669")/*
                                                         * @res
                                                         * "岗位编码"
                                                         */);
    vecPair.addElement(condVo);
    condVo = new Pair("om_job.jobname", NCLangRes.getInstance().getStrByID("common", "UC000-0001657")/*
                                                         * @res
                                                         * "岗位名称"
                                                         */);
    vecPair.addElement(condVo);
    condVo = new Pair("bd_psnbasdoc.id", NCLangRes.getInstance().getStrByID("common", "UC000-0003914")/*
                                                         * @res
                                                         * "身份证号"
                                                         */);
    vecPair.addElement(condVo);
    condVo = new Pair("bd_psnbasdoc.ssnum", NCLangRes.getInstance().getStrByID("common", "UC000-0003009")/*
                                                         * @res
                                                         * "社会保障号"
                                                         */);
    vecPair.addElement(condVo);
    condVo = new Pair("bd_psnbasdoc.sex", NCLangRes.getInstance().getStrByID("common", "UC000-0001940")/*
                                                         * @res
                                                         * "性别"
                                                         */);
    vecPair.addElement(condVo);
    condVo = new Pair("bd_psnbasdoc.birthdate", NCLangRes.getInstance().getStrByID("common", "UC000-0000523")/*
                                                             * @res
                                                             * "出生日期"
                                                             */);
    vecPair.addElement(condVo);
    condVo = new Pair("bd_psnbasdoc.nationality", NCLangRes.getInstance().getStrByID("common", "UC000-0002827")/*
                                                             * @res
                                                             * "民族"
                                                             */);
    vecPair.addElement(condVo);
    condVo = new Pair("bd_psnbasdoc.marital", NCLangRes.getInstance().getStrByID("common", "UC000-0001411")/*
                                                         * @res
                                                         * "婚姻状况"
                                                         */);
    vecPair.addElement(condVo);
    condVo = new Pair("bd_psnbasdoc.health", NCLangRes.getInstance().getStrByID("common", "UC000-0000362")/*
                                                         * @res
                                                         * "健康状况"
                                                         */);
    vecPair.addElement(condVo);
    condVo = new Pair("bd_psndoc.indutydate", NCLangRes.getInstance().getStrByID("common", "UC000-0000632")/*
                                                         * @res
                                                         * "到职日期"
                                                         */);
    vecPair.addElement(condVo);
    condVo = new Pair("bd_psnbasdoc.joinworkdate", NCLangRes.getInstance().getStrByID("common", "UC000-0000956")/*
                                                             * @res
                                                             * "参加工作时间"
                                                             */);
    vecPair.addElement(condVo);
    condVo = new Pair("bd_psndoc.outdutydate", NCLangRes.getInstance().getStrByID("common", "UC000-0003061")/*
                                                         * @res
                                                         * "离职日期"
                                                         */);
    vecPair.addElement(condVo);

    if (getItemCodes() != null && getItemCodes().length > 0) {
        for (int i = 0; i < getItemCodes().length; i++) {
        condVo = new Pair("wa_data." + getItemCodes()[i], getItemNames()[i]);
        vecPair.addElement(condVo);
        }
    }

    listSortItems = new Pair[vecPair.size()];
    vecPair.copyInto(listSortItems);

    sortDialog.setFields(listSortItems);
    sortDialog.setTableCode(newClassPk);
    sortDialog.setModuleCode(getModuleCode());
    sortDialog.setLocationRelativeTo(this);
    // sortDialog.setStrClassType(newClassPk);

    // 切换类别，且类别发生变化才清掉排序条件列表，否则保留上次排序条件，如果切换类别后则不保存
    if (!newClassPk.equalsIgnoreCase(oldClassPk)) {
        sortDialog.setSortingFields(new Vector());// 先清空默认条件
    }
    sortDialog.btnLoad_ActionPerformed(null);

    // 设置默认排序条件
    setDefaultOrderFields(sortDialog, listSortItems);

    oldClassPk = newClassPk;
    }

    /**
     * 设置默认排序条件
     *
     * @param configDialog
     *                SortConfigDialog 排序Dialog
     * @param listSortItems
     *                Vector 可供选择的排序字段
     * @since NCHRV5.5
     */
    private void setDefaultOrderFields(SortConfigDialog configDialog, Pair[] listSortItems) {
    Vector<Attribute> sortingFields = configDialog.getSortingFields();
    // 第一次使用没有默认排序字段，则设置默认为"部门编码" asc 、"人员编码" asc
    if (sortingFields == null || sortingFields.isEmpty()) {
        Vector<Attribute> defaultSortFields = new Vector<Attribute>();
        Attribute deptcode = new Attribute(new Pair("bd_deptdoc.deptcode", NCLangRes.getInstance().getStrByID("common", "UC000-0004073")/*
                                                                         * @res
                                                                         * "部门编码"
                                                                         */), true);
        Attribute psncode = new Attribute(new Pair("bd_psndoc.psncode", NCLangRes.getInstance().getStrByID("common", "UC000-0000147")/*
                                                                         * @res
                                                                         * "人员编码"
                                                                         */), true);
        defaultSortFields.addElement(deptcode);
        defaultSortFields.addElement(psncode);
        configDialog.setSortingFields(defaultSortFields);
    }
    // 如果已经有默认排序字段，检查排序字段是否在可供选择的排序字段类别中，如果不再则需要删除
    else {
        for (int i = 0; i < sortingFields.size(); i++) {
        boolean isExistInList = false;
        Pair pair = (sortingFields.elementAt(i)).getAttribute();
        for (Pair listSortItem : listSortItems) {
            if (listSortItem.getCode().equals(pair.getCode())) {
            isExistInList = true;
            break;
            }
        }
        if (!isExistInList) {
            sortingFields.removeElementAt(i);
        }
        }
    }
    }

    public static Frame getTopFrame(Component component) {

    // 逐级获取组件的父组件，直到父窗口
    Container parent = component.getParent();
    while (!(parent == null || parent instanceof Frame)) {
        parent = parent.getParent();
    }

    return (Frame) parent;
    }

    /**
     * 返回排序字段
     *
     * @return java.lang.String
     */
    public java.lang.String getOrderStr() {

    return orderStr;
    }

    /**
     * 返回 pnlList 特性值。
     *
     * @return nc.ui.pub.beans.UIPanel
     */
    /* 警告：此方法将重新生成。 */
    private nc.ui.pub.beans.UIPanel getpnlList() {
    if (ivjpnlList == null) {
        try {
        ivjpnlList = new nc.ui.pub.beans.UIPanel();
        ivjpnlList.setName("pnlList");
        ivjpnlList.setLayout(new java.awt.BorderLayout());
        getpnlList().add(getBspWaData(), "Center");
        getpnlList().add(getUIPnlComment(), "South");
        // user code begin {1}
        // user code end
        } catch (java.lang.Throwable ivjExc) {
        // user code begin {2}
        // user code end
        handleException(ivjExc);
        }
    }
    return ivjpnlList;
    }

    /**
     * 此处插入方法说明。 创建日期：(2001-11-1 18:58:06)
     *
     * @return int
     */
    public int getPrecisionForUFDouble() {
    return precisionForUFDouble;
    }

    /**
     * 此处插入方法说明。 创建日期：(2001-10-15 14:12:53)
     *
     * @return nc.ui.wa.wa_009.RecacuDlg
     */
    public RecacuDlg getRecacuDlg() {
    if (recacuDlg == null) {
        recacuDlg = new RecacuDlg(this, nc.ui.ml.NCLangRes.getInstance().getStrByID("60131004", "UPP60131004-000146")/*
                                                                 * @res
                                                                 * "计算条件设置"
                                                                 */);
    }
    return recacuDlg;
    }

    /**
     * 此处插入方法说明。 创建日期：(2001-6-23 21:23:07)
     *
     * @return nc.ui.wa.wa_009.ReplaceDlg
     * @throws Exception
     */
    public ReplaceDlg getReplaceDlg() throws Exception {
    if (replaceDlg == null) {
        replaceDlg = new ReplaceDlg(this);
        replaceDlg.setTitle(nc.ui.ml.NCLangRes.getInstance().getStrByID("60131004", "UPP60131004-000147")/*
                                                         * @res
                                                         * "替换薪资项目"
                                                         */);
            replaceDlg.initData(ibo.getWaClassItemMap(),getEditableItem(), getEditableName(), getEditableType(), getItemCodes(), getItemNames(), getItemTypes());
    }

    return replaceDlg;
    }

    /**
     * 此处插入方法说明。 创建日期：(2002-6-20 9:25:48)
     *
     * @return java.lang.Integer[]
     */
    public java.lang.Integer[] getSaBodyColDecimal() {
    return saBodyColDecimal;
    }

    /**
     * 此处插入方法说明。 创建日期：(2002-6-20 9:26:19)
     *
     * @return java.lang.Integer
     */
    public java.lang.Integer[] getSaBodyColWidth() {
    return saBodyColWidth;
    }

    /**
     * 此处插入方法说明。 创建日期：(2001-6-18 16:41:42)
     *
     * @return nc.ui.pub.query.QueryConditionClient
     */
    public HrQueryDialog getSelDlg() {
    if (selDlg == null) {
        selDlg = new QueryConditionDlgBuilder().createQueryConditionDLG(this, getNormalWhere(), nc.ui.ml.NCLangRes.getInstance().getStrByID("60131001", "UPP60131001-000119")/* "人员选择" */);

        selDlg.registerFilterEditorFactory(new ItemFilterEditorFactory(selDlg.getQueryContext()));
        selDlg.setFieldValueEditor(this);
    }

    getCbxChgSearch().setEnabled(true);

    return selDlg;
    }

    private RefNomalPartPanel normalWhere = null;

    // ///////
    public RefNomalPartPanel getNormalWhere() {
    //class切换时发放部门内容不更新。所以每次都刷新
    String newClassPk = getUIRefWaClass().getRefPK();
    if (normalWhere == null || !newClassPk.equalsIgnoreCase(oldClassPk)) {
        normalWhere = new RefNomalPartPanel(2, newClassPk);
        normalWhere.addUserComponent(getCbxChgSearch());

    }
    return normalWhere;
    }
    
   
   
    /**
     * 此处插入方法描述。 创建日期：(2003-9-16 20:35:54)
     *
     * @return nc.ui.wa.wa_009.SharePeriodSetDlg
     */
    public SharePeriodSetDlg getSharePeriodSetDlg() {
    if (sharePeriodSetDlg == null) {
        sharePeriodSetDlg = new SharePeriodSetDlg(this);
    }
    return sharePeriodSetDlg;
    }

    /**
     * 此处插入方法说明。 创建日期：(2002-9-18 12:56:52) 返回显示在状态条上的提示信息，
     *
     * @return java.lang.String
     */
    @Override
    public String getStatusHintStr() {
    if (waGlobal.getWaClassPK() != null) {
        return nc.ui.ml.NCLangRes.getInstance().getStrByID("60131004", "UPP60131004-000150")/*
                                                 * @res
                                                 * "当前"
                                                 */
            + nc.ui.ml.NCLangRes.getInstance().getStrByID("common", "UC000-0000404")/*
                                                 * @res
                                                 * "公司"
                                                 */
            + "：" + nc.ui.hr.global.Global.getWaCorpName() + "   " + nc.ui.ml.NCLangRes.getInstance().getStrByID("60131004", "UPP60131004-000151")/*
                                                                                 * @res "
                                                                                 * 薪资类别："
                                                                                 */
            + "：" + waGlobal.getWaClassName();
    } else {
        return nc.ui.ml.NCLangRes.getInstance().getStrByID("60131004", "UPP60131004-000150")/*
                                                 * @res
                                                 * "当前"
                                                 */
            + nc.ui.ml.NCLangRes.getInstance().getStrByID("common", "UC000-0000404")/*
                                                 * @res
                                                 * "公司"
                                                 */
            + "：" + nc.ui.hr.global.Global.getWaCorpName();
    }
    }

    /**
     * 此处插入方法说明。 创建日期：(2001-11-27 13:01:55)
     *
     * @return java.lang.String[]
     */
    public java.lang.String[] getStDigitItemRefWithoutCurr() {
    return stDigitItemRefWithoutCurr;
    }

    /**
     * 子类实现该方法，返回业务界面的标题。
     *
     * @version (00-6-6 13:33:25)
     *
     * @return java.lang.String
     */
    @Override
    public String getTitle() {
    return NCLangRes.getInstance().getStrByID("60131004", "UPP60131004-000152")/*
                                             * @res
                                             * "薪资发放表"
                                             */;
    }

    /**
     * 返回 UILblComment 特性值。
     *
     * @return nc.ui.pub.beans.UILabel
     */
    /* 警告：此方法将重新生成。 */
    private nc.ui.pub.beans.UILabel getUILblComment() {
    if (ivjUILblComment == null) {
        try {
        ivjUILblComment = new nc.ui.pub.beans.UILabel();
        ivjUILblComment.setName("UILblComment");
        ivjUILblComment.setText(nc.ui.ml.NCLangRes.getInstance().getStrByID("60131004", "UPT60131004-000005")/*
                                                             * @res
                                                             * "发放说明："
                                                             */);
        ivjUILblComment.setBounds(211, 14, 67, 22);
        // user code begin {1}
        // user code end
        } catch (java.lang.Throwable ivjExc) {
        // user code begin {2}
        // user code end
        handleException(ivjExc);
        }
    }
    return ivjUILblComment;
    }

    /**
     * 返回 UILblPayDay 特性值。
     *
     * @return nc.ui.pub.beans.UILabel
     */
    /* 警告：此方法将重新生成。 */
    private nc.ui.pub.beans.UILabel getUILblPayDay() {
    if (ivjUILblPayDay == null) {
        try {
        ivjUILblPayDay = new nc.ui.pub.beans.UILabel();
        ivjUILblPayDay.setName("UILblPayDay");
        ivjUILblPayDay.setText(nc.ui.ml.NCLangRes.getInstance().getStrByID("60131004", "UPT60131004-000004")/*
                                                             * @res
                                                             * "发放日期："
                                                             */);
        ivjUILblPayDay.setBounds(7, 14, 63, 22);
        // user code begin {1}
        // user code end
        } catch (java.lang.Throwable ivjExc) {
        // user code begin {2}
        // user code end
        handleException(ivjExc);
        }
    }
    return ivjUILblPayDay;
    }

    /**
     * 返回 UIPnlComment 特性值。
     *
     * @return nc.ui.pub.beans.UIPanel
     */
    /* 警告：此方法将重新生成。 */
    private nc.ui.pub.beans.UIPanel getUIPnlComment() {
    if (ivjUIPnlComment == null) {
        try {
        ivjUIPnlComment = new nc.ui.pub.beans.UIPanel();
        ivjUIPnlComment.setName("UIPnlComment");
        ivjUIPnlComment.setPreferredSize(new java.awt.Dimension(10, 50));
        ivjUIPnlComment.setLayout(null);
        getUIPnlComment().add(getUIRefPayDay(), getUIRefPayDay().getName());
        getUIPnlComment().add(getUILblPayDay(), getUILblPayDay().getName());
        getUIPnlComment().add(getUISpComment(), getUISpComment().getName());
        getUIPnlComment().add(getUILblComment(), getUILblComment().getName());
        // user code begin {1}
        // user code end
        } catch (java.lang.Throwable ivjExc) {
        // user code begin {2}
        // user code end
        handleException(ivjExc);
        }
    }
    return ivjUIPnlComment;
    }

    /**
     * 返回 UIRefPayDay 特性值。
     *
     * @return nc.ui.pub.beans.UIRefPane
     */
    /* 警告：此方法将重新生成。 */
    private nc.ui.pub.beans.UIRefPane getUIRefPayDay() {
    if (ivjUIRefPayDay == null) {
        try {
        ivjUIRefPayDay = new nc.ui.pub.beans.UIRefPane();
        ivjUIRefPayDay.setName("UIRefPayDay");
        ivjUIRefPayDay.setBounds(70, 14, 100, 22);
        ivjUIRefPayDay.setRefNodeName("日历");
        // user code begin {1}
        // user code end
        } catch (java.lang.Throwable ivjExc) {
        // user code begin {2}
        // user code end
        handleException(ivjExc);
        }
    }
    return ivjUIRefPayDay;
    }

    /**
     * 返回 UIScrollPane1 特性值。
     *
     * @return nc.ui.pub.beans.UIScrollPane
     */
    /* 警告：此方法将重新生成。 */
    private nc.ui.pub.beans.UIScrollPane getUISpComment() {
    if (ivjUISpComment == null) {
        try {
        ivjUISpComment = new nc.ui.pub.beans.UIScrollPane();
        ivjUISpComment.setName("UISpComment");
        ivjUISpComment.setBounds(279, 5, 492, 40);
        getUISpComment().setViewportView(getUITaComment());
        // user code begin {1}
        // getUISpComment().setBorder(getUItbComment());
        // user code end
        } catch (java.lang.Throwable ivjExc) {
        // user code begin {2}
        // user code end
        handleException(ivjExc);
        }
    }
    return ivjUISpComment;
    }

    /**
     * 返回 UITextArea1 特性值。
     *
     * @return nc.ui.pub.beans.UITextArea
     */
    /* 警告：此方法将重新生成。 */
    private nc.ui.pub.beans.UITextArea getUITaComment() {
    if (ivjUITaComment == null) {
        try {
        ivjUITaComment = new nc.ui.pub.beans.UITextArea();
        ivjUITaComment.setName("UITaComment");
        ivjUITaComment.setLineWrap(true);
        ivjUITaComment.setWrapStyleWord(true);
        ivjUITaComment.setBounds(0, 0, 160, 120);
        ivjUITaComment.setMaxLength(2000);
        // user code begin {1}
        // user code end
        } catch (java.lang.Throwable ivjExc) {
        // user code begin {2}
        // user code end
        handleException(ivjExc);
        }
    }
    return ivjUITaComment;
    }

    /**
     * 此处插入方法说明。 创建日期：(2002-6-20 8:43:27)
     *
     * @return int[]
     */
    public int[] getWaItemDecimal() {
    return waItemDecimal;
    }

    /**
     * 此处插入方法说明。 创建日期：(2002-6-20 8:42:57)
     *
     * @return int[]
     */
    public int[] getWaItemWidth() {
    return waItemWidth;
    }

    /**
     * 此处插入方法说明。 创建日期：(2001-6-27 15:12:17)
     *
     * @return java.lang.String
     * @throws BusinessException
     */
    public String getWhereSqlFromDlg() throws BusinessException {
    String sqlWhere = getSelDlg().getWhereSQL();

    if (sqlWhere == null || sqlWhere.length() <= 0) {
        sqlWhere = "1=1";
    } else {
        sqlWhere = sqlWhere.trim();

    }
    if (sqlWhere != null && sqlWhere.trim().length() > 0) {
        StringOperator str = new StringOperator(sqlWhere);
        str.replaceAllString("'" + NCLangRes.getInstance().getStrByID("60131004", "UPP60131004-000148")/* "未审核" */+ "'", "0");
        str.replaceAllString("'" + NCLangRes.getInstance().getStrByID("60131004", "UPP60131004-000149")/* "已审核" */+ "'", "1");
        sqlWhere = str.toString();

        if (sqlWhere.indexOf("wa_data.f_d") >= 0) {
        throw new BusinessException(NCLangRes.getInstance().getStrByID("60131004", "UPP60131004-000417")/* "请指定具体的数值型薪资项目!" */);
        } else if (sqlWhere.indexOf("wa_data.f_c") >= 0) {
        throw new BusinessException(NCLangRes.getInstance().getStrByID("60131004", "UPP60131004-000418")/* "请指定具体的字符型薪资项目!" */);

        }
    }
    String normalSQl = getNormalWhere().getNomalSql();
    return "(" + sqlWhere + WherePartUtil.formatAddtionalWhere(normalSQl) + ")";
    }

    private String getConditonWithDataPower() throws BusinessException {
    // update by sunxj 2010-03-30 快速查询插件 start
    // String whereSql = getWhereSqlFromDlg() ;
    String whereSql = null;
    if (isQuickSearch) {
        whereSql = getTheLatestCondition();
        return whereSql;
    }
    whereSql = getWhereSqlFromDlg();
    // update by sunxj 2010-03-30 快速查询插件 end
    if (whereSql != null) {
        whereSql += (deptpower + psnclpower);
    } else {
        whereSql = deptpower + psnclpower;
    }
    return whereSql;
    }

    /**
     * 每当部件抛出异常时被调用
     *
     * @param exception
     *                java.lang.Throwable
     */
    private void handleException(java.lang.Throwable exception) {

    /* 除去下列各行的注释，以将未捕捉到的异常打印至 stdout。 */
    exception.printStackTrace(System.out);
    }

    /**
     * 此处插入方法说明。 创建日期：(2001-6-21 15:37:12)
     *
     * @return boolean
     */
    public boolean haveMadeBill() throws Exception {
    return WADelegator.getWaDatapool().isHaveDatapoolBill(waGlobal.getWaClassPK(), ibo.getWaYear(), ibo.getWaPeriod(), "WA");

    }

    /**
     * 此处插入方法说明。 创建日期：(2002-6-20 9:23:34)
     */
    public void initColDecimal() throws Exception {
    // 小数位数
    Vector<Integer> vecColDecimal = new Vector<Integer>();
    vecColDecimal.addElement(new Integer(0));
    vecColDecimal.addElement(new Integer(0));
    vecColDecimal.addElement(new Integer(0));
    vecColDecimal.addElement(new Integer(0));
    vecColDecimal.addElement(new Integer(0));

    if (isDisplayNest()) {
        vecColDecimal.addElement(new Integer(0));
        vecColDecimal.addElement(new Integer(0));
        vecColDecimal.addElement(new Integer(0));
    }

    setWaItemDecimal(ibo.getWaItemDecimal_user());

    if (getWaItemDecimal() != null && getWaItemDecimal().length > 0) {
        for (int i = 0; i < getWaItemDecimal().length; i++) {
        vecColDecimal.addElement(new Integer(getWaItemDecimal()[i]));
        }
    }
    vecColDecimal.addElement(new Integer(0));
    vecColDecimal.addElement(new Integer(0));
    vecColDecimal.addElement(new Integer(0));
    //add by suhf1 xhhrp
    vecColDecimal.addElement(new Integer(0));
    vecColDecimal.addElement(new Integer(0));
    saBodyColDecimal = (Integer[]) Convertor.convertVectorToArray(vecColDecimal);
    }

    /**
     * 此处插入方法说明。 创建日期：(2002-6-20 9:18:52)
     */
    public void initColKeyName() throws Exception {
    // 关键字
    Vector<String> vecKeyName = new Vector<String>();
    vecKeyName.addElement("m_psnCode");
    vecKeyName.addElement("m_psnName");
    vecKeyName.addElement("m_deptName");
    vecKeyName.addElement("m_psnclIdName");
    vecKeyName.addElement("m_omJobName");

    if (isDisplayNest()) {
        vecKeyName.addElement("m_nestDeptName");
        vecKeyName.addElement("m_nestPsnclIdName");
        vecKeyName.addElement("m_nestOmjobName");
    }

    setItemCodes(ibo.getWaItemCodes_user());

    if (getItemCodes() != null && getItemCodes().length > 0) {
        for (int i = 0; i < getItemCodes().length; i++) {
        vecKeyName.addElement(getItemCodes()[i]);
        }
    }
    vecKeyName.addElement("irecaculateflag");
    vecKeyName.addElement("icheckflag");
    vecKeyName.addElement("irecheckflag");
    // add by suhf1 xhhrp
    vecKeyName.addElement("m_rewardDept");
    vecKeyName.addElement("m_tempRewardGroup");
    saBodyColKeyName = (String[]) Convertor.convertVectorToArray(vecKeyName);
    }

    /**
     * 此处插入方法说明。 创建日期：(2002-6-20 9:16:16)
     */
    public void initColName() throws Exception {

    // 显示名称
    Vector<String> vecColName = new Vector<String>();
    vecColName.addElement(nc.ui.ml.NCLangRes.getInstance().getStrByID("common", "UC000-0000147")/*
                                                     * @res
                                                     * "人员编码"
                                                     */);
    vecColName.addElement(nc.ui.ml.NCLangRes.getInstance().getStrByID("common", "UC000-0000135")/*
                                                     * @res
                                                     * "人员姓名"
                                                     */);
    vecColName.addElement(nc.ui.ml.NCLangRes.getInstance().getStrByID("common", "UC000-0001975")/*
                                                     * @res
                                                     * "所在部门"
                                                     */);
    vecColName.addElement(nc.ui.ml.NCLangRes.getInstance().getStrByID("common", "UC000-0000140")/*
                                                     * @res
                                                     * "人员类别"
                                                     */);
    //update by dychf
    /*vecColName.addElement(nc.ui.ml.NCLangRes.getInstance().getStrByID("60131004", "UPT60131004-000001")
                                                         * @res
                                                         * "所在岗位"
                                                         );*/

    if (isDisplayNest()) {
        vecColName.addElement(nc.ui.ml.NCLangRes.getInstance().getStrByID("common", "UC000-0001912")/*
                                                     * @res
                                                     * "归属部门"
                                                     */);
        vecColName.addElement(nc.ui.ml.NCLangRes.getInstance().getStrByID("common", "UC000-0001908")/*
                                                     * @res
                                                     * "归属人员类别"
                                                     */);
        vecColName.addElement(nc.ui.ml.NCLangRes.getInstance().getStrByID("common", "UC000-0001909")/*
                                                     * @res
                                                     * "归属岗位"
                                                     */);
    }

    setItemNames(ibo.getWaItemNames_user());

    if (getItemNames() != null && getItemNames().length > 0) {
        for (int i = 0; i < getItemNames().length; i++) {
        vecColName.addElement(getItemNames()[i]);
        }
    }
    vecColName.addElement(nc.ui.ml.NCLangRes.getInstance().getStrByID("60131004", "UPP60131004-000068")/*
                                                         * @res
                                                         * "计算标志"
                                                         */);
    vecColName.addElement(nc.ui.ml.NCLangRes.getInstance().getStrByID("60131004", "UPP60131004-000305")/*
                                                         * @res
                                                         * "审核标志"
                                                         */);
    vecColName.addElement(ResHelper.getString("6013v57","UPP6013v57-000016")//@res "复审标志"
    		);
    //add by suhf1 xhhrp 
    //update by dychf
    //vecColName.addElement("奖金科室");
    //vecColName.addElement("临时奖金组");
    saBodyColName = (String[]) Convertor.convertVectorToArray(vecColName);
    }

    private void setHintMsg(String psncode, String psnname, String deptname) {
    String hintMsg = "";
    String space = "    ";
    String comm = ": ";
    hintMsg = nc.ui.ml.NCLangRes.getInstance().getStrByID("common", "UC000-0000147")/* "人员编码" */+ comm + psncode + space;
    hintMsg = hintMsg + nc.ui.ml.NCLangRes.getInstance().getStrByID("common", "UC000-0000135")/* "人员姓名" */+ comm + psnname + space;
    hintMsg = hintMsg + nc.ui.ml.NCLangRes.getInstance().getStrByID("common", "UC000-0001975")/* "所在部门" */+ comm + deptname;

    showHintMessage(hintMsg);
    }

    /**
     * 此处插入方法说明。 创建日期：(2002-6-20 9:20:12)
     */
    public void initColType() throws Exception {
    // 类型
    Vector<String> vecColType = new Vector<String>();
    vecColType.addElement("0");
    vecColType.addElement("0");
    vecColType.addElement("0");
    vecColType.addElement("0");
    vecColType.addElement("0");

    if (isDisplayNest()) {
        vecColType.addElement("0");
        vecColType.addElement("0");
        vecColType.addElement("0");
    }

    setItemTypes(ibo.getWaItemTypes_user());

    if (getItemTypes() != null && getItemTypes().length > 0) {
        for (int i = 0; i < getItemTypes().length; i++) {
        vecColType.addElement(getItemTypes()[i]);
        }
    }
    vecColType.addElement("8");// 临时写一个boolean型
    vecColType.addElement("8");// 临时写一个boolean型
    vecColType.addElement("8");// 临时写一个boolean型
    //add by suhf1 xhhrp
    vecColType.add("0");
    vecColType.add("0");
    saBodyColType = (String[]) Convertor.convertVectorToArray(vecColType);
    }

    /**
     * 此处插入方法说明。 创建日期：(2002-6-20 9:21:54)
     */
    public void initColWidth() throws Exception {
    // 长度
    Vector<Integer> vecColWidth = new Vector<Integer>();
    vecColWidth.addElement(new Integer(1000));
    vecColWidth.addElement(new Integer(1000));
    vecColWidth.addElement(new Integer(2000));
    vecColWidth.addElement(new Integer(1000));
    vecColWidth.addElement(new Integer(1000));

    if (isDisplayNest()) {
        vecColWidth.addElement(new Integer(1000));
        vecColWidth.addElement(new Integer(1000));
        vecColWidth.addElement(new Integer(1000));
    }
    setWaItemWidth(ibo.getWaItemWidth_user());

    if (getWaItemWidth() != null && getWaItemWidth().length > 0) {
        for (int i = 0; i < getWaItemWidth().length; i++) {
        int nwidth = getWaItemWidth()[i];
        if ("1".equals(saBodyColType[i])) {
            if (nwidth > 9) {
            nwidth -= 3;
            }
        }

        vecColWidth.addElement(new Integer(getWaItemWidth()[i]));
        }
    }
    vecColWidth.addElement(new Integer(100));
    vecColWidth.addElement(new Integer(100));
    vecColWidth.addElement(new Integer(100));
    //add by suhf1 xhhrp
    vecColWidth.addElement(new Integer(1000));
    vecColWidth.addElement(new Integer(1000));
    saBodyColWidth = (Integer[]) Convertor.convertVectorToArray(vecColWidth);
    }

    /**
     * 初始化类。
     */
    /* 警告：此方法将重新生成。 */
    private void initData() {
    try {
        waData = null;
        if (ibo == null || ibo.getWaClassVO() == null || ibo.getWaClassVO().getPrimaryKey() == null || ibo.getWaClassVO().getPrimaryKey().trim().length() <= 0) {
        setButtonExceptionState();
        return;
        }
        /** 员工薪资变动取值发放方式: 0、自起薪日所在期间开始按照最新薪资发放 */
        if (ibo.getWaClassVO().getPayoffflag().intValue() == 0) {
        m_MainButtonGroup = new ButtonObject[] { m_boUpdate, m_boReplace, m_boSave, m_boCancel, m_boReCal, m_boReTotal, m_boReTotal4MulDeptItem, m_boWatch4MulDeptItem, m_boApproveOperation,
            m_boPayOff, m_boCancelPayOff, m_btnConstrast, m_boDispChg, m_boSort, m_boSearch, m_boRefresh, m_boPrintGroup ,m_btnConfirm};
        if (ibo.getWaClassVO().getCheckflag().intValue() == 1) {
            m_MainButtonGroup = new ButtonObject[] { m_boUpdate, m_boReplace, m_boSave, m_boCancel, m_boReCal, m_boReTotal, m_boReTotal4MulDeptItem, m_boWatch4MulDeptItem, m_boPayOff,
                m_boCancelPayOff, m_btnConstrast, m_boDispChg, m_boSort, m_boSearch, m_boRefresh, m_boPrintGroup,m_btnConfirm };
        }
        }
        /** 员工薪资变动取值发放方式: 1、按时点分别取薪资变动的前后金额 */
        else {
        boolean bool = WAGlobalData.isEnableHI();
        if (bool == true) {
            m_MainButtonGroup = new ButtonObject[] { m_bopsnCompute, m_boUpdate, m_boReplace, m_boSave, m_boCancel, m_boReCal, m_boReTotal4MulDeptItem, m_boWatch4MulDeptItem, m_boReTotal,
                m_boApproveOperation, m_boPayOff, m_boCancelPayOff,m_btnConstrast, m_boDispChg, m_boSort, m_boSearch, m_boRefresh, m_boPrintGroup,m_btnConfirm };
        } else {
            m_MainButtonGroup = new ButtonObject[] { m_boUpdate, m_boReplace, m_boSave, m_boCancel, m_boReCal, m_boReTotal, m_boReTotal4MulDeptItem, m_boWatch4MulDeptItem,
                m_boApproveOperation, m_boPayOff, m_boCancelPayOff, m_btnConstrast, m_boDispChg, m_boSort, m_boSearch, m_boRefresh, m_boPrintGroup,m_btnConfirm };
        }

        /** 薪资审核方式：0、薪资发放节点控制；1，薪资审核节点控制 */
        if (ibo.getWaClassVO().getCheckflag().intValue() == 1) {
            if (bool == true) {
            m_MainButtonGroup = new ButtonObject[] { m_bopsnCompute, m_boUpdate, m_boReplace, m_boSave, m_boCancel, m_boReCal, m_boReTotal, m_boReTotal4MulDeptItem, m_boWatch4MulDeptItem,
                m_boPayOff, m_boCancelPayOff, m_btnConstrast, m_boDispChg, m_boSort, m_boSearch, m_boRefresh, m_boPrintGroup,m_btnConfirm };
            } else {
            m_MainButtonGroup = new ButtonObject[] { m_boUpdate, m_boReplace, m_boSave, m_boCancel, m_boReCal, m_boReTotal, m_boReTotal4MulDeptItem, m_boWatch4MulDeptItem, m_boPayOff,
                m_boCancelPayOff, m_btnConstrast, m_boDispChg, m_boSort, m_boSearch, m_boRefresh, m_boPrintGroup ,m_btnConfirm};
            }
        }
        }
        this.setButtons(m_MainButtonGroup);

        setEditableItem(ibo.getStEditableItem());
        setEditableName(ibo.getStEditableName());
        setEditableType(ibo.getStEditableType());
        setStDigitItemRefWithoutCurr(ibo.getStDigitItemRefWithoutCurr());

        setPrecisionForUFDouble(ibo.getPrecisionForDatabase());

        displayNest = ibo.getWaClassVO().isDisplayNest();

        initTable();

        setSaveAndCancel(false);

        finishinit = true;

        loading = false;

    } catch (Exception ivjExc) {
        String error = ivjExc.getMessage();
        if (error == null || error.trim().length() < 1) {
        error = nc.ui.ml.NCLangRes.getInstance().getStrByID("60131004", "UPP60131004-000159");// "程序初始化时出现错误，请刷新后再试。"
        }
        showException(error);
        setButtonExceptionState();
    }

    }

    /**
     * 初始化类。
     */
    private void initialize() {
    try {
        beforeInitData();
        // haveException = false;
        if (this.waGlobal.getWaClassPK() != null) {
        if (!this.waGlobal.getWaClassPK().equals(getUIRefWaClass().getRefPK())) {
            getUIRefWaClass().setPK(this.waGlobal.getWaClassPK());
        }
        this.waGlobal.setWaYear((String) getUIRefWaClass().getRefValue("a.cyear"));
        this.waGlobal.setWaPeriod((String) getUIRefWaClass().getRefValue("a.cperiod"));

        // String condition = getConditonWithDataPower();
        String condition = deptpower + psnclpower;

        ibo = new WageAlternateIBO(WAGlobalData.getCorpPK(), waGlobal.getWaClassPK(), waGlobal.getWaYear(), waGlobal.getWaPeriod(), condition);

        } else {
        ibo = new WageAlternateIBO(null, deptpower + psnclpower);
        }

        setName("WageAlternateUI");
        setLayout(new java.awt.BorderLayout());
        setSize(774, 419);

        add(getUITopPanel1(), BorderLayout.NORTH);
        add(getpnlList(), "Center");
        getUIRefWaClass().removeValueChangedListener(ivjEventHandler);

        getUIRefWaClass().addValueChangedListener(ivjEventHandler);

        // add for v57 多部门发薪---start----
        // 是否安装了自助模块//TODO （4） 如果没有启用（安装）自助模块，则“多部门数据汇总”、“多部门数据查看”按钮不可见 还没处理
//        installHRSS = SFServiceFacility.getCreateCorpQueryService().isEnabled(Global.getCorpPK(), "HRSS");
        if (!isInstallHRSS()) {
        m_boReTotal4MulDeptItem.setVisible(false);
        m_boWatch4MulDeptItem.setVisible(false);
        } else {
        m_boReTotal4MulDeptItem.setVisible(true);
        m_boWatch4MulDeptItem.setVisible(true);
        }
        // add for v57 多部门发薪---end----
        m_boPrintGroup.removeAllChildren();
        m_boPrintGroup.addChildButton(m_boPrint);
        m_boPrintGroup.addChildButton(m_boDirectPrint);

        m_boApproveOperation.removeAllChildren();
        m_boApproveOperation.addChildButton(m_boCheck);
        m_boApproveOperation.addChildButton(m_boCancelCheck);
        m_boApproveOperation.addChildButton(m_boReCheck);
        m_boApproveOperation.addChildButton(m_boCancelReCheck);

        setButtons(m_MainButtonGroup);
        getButtons();
        userId = ibo.getUserId();
        pkCorp = ibo.getDwbm();
        // beforeInitData();
        initData();

        if (this.waGlobal.getWaClassPK() != null) {
        resetButtonSate(ibo.getWaClassVO());
        } else {
        buttonState = new HashMap<ButtonObject, Boolean>();// 记录所有可以使用的按钮
        setButtonExceptionState();
        }

    } catch (Exception e) {
        Logger.error(e.getMessage(), e);
        showException(e.getMessage());
    }
    }
    
    private boolean isInstallHRSS() throws BusinessException{
    	if(installHRSS==null){
    		installHRSS = new UFBoolean(SFServiceFacility.getCreateCorpQueryService().isEnabled(Global.getCorpPK(), "HRSS"));
    	}    	
    	return installHRSS.booleanValue();
    }

    private void updateButtonStates() {
    ButtonObject[] buttonObjects = getButtons();
    if (buttonObjects != null && buttonObjects.length > 0) {
        for (ButtonObject buttonObject : buttonObjects) {
        Boolean enable = buttonState.get(buttonObject);
        buttonObject.setEnabled(enable == null ? false : enable);
        if (buttonObject.isEnabled()) {// 子按钮
            if (buttonObject.getChildCount() > 0) {
            ButtonObject[] childButtonObjects = buttonObject.getChildButtonGroup();
            for (ButtonObject child_bt : childButtonObjects) {
                enable = buttonState.get(child_bt);
                child_bt.setEnabled(enable == null ? false : enable);
            }
            }
        }
        }
    }
    updateButtons();
    }

    /**
     * 此处插入方法说明。 创建日期：(2001-6-27 16:16:15)
     */
    public void initTable() throws Exception {
    try {

        // 显示名称
        initColName();

        // 关键字
        initColKeyName();

        // 类型
        initColType();

        // 长度
        initColWidth();
        
        // 小数位数
        initColDecimal();

        if (getEditableItem() != null && getEditableItem().length > 0) {
        editaleSqeu = new int[getEditableItem().length];
        }

        BillItem[] biaBody = new BillItem[saBodyColName.length];

        int index = 0;
        for (int i = 0; i < saBodyColName.length; i++) {
        biaBody[i] = new BillItem();
        biaBody[i].setName(saBodyColName[i]);
        biaBody[i].setKey(saBodyColKeyName[i]);
        biaBody[i].setWidth(100);
        biaBody[i].setEnabled(true);

        if (!isEditableItem(saBodyColKeyName[i])) {
            biaBody[i].setEdit(false);
        } else {

            // add for V57 start
            // 手工输入并且不是多部门发放的项目才可以编辑
            ClassitemVO classItemVO = ibo.getWaClassItemMap().get(saBodyColKeyName[i]);

            if (classItemVO.getIsmut_deptitem() != null && classItemVO.getIsmut_deptitem().booleanValue()) {
            biaBody[i].setEdit(false);
            } else {
            biaBody[i].setEdit(true);
            }
            // add for V57 end
            if (editaleSqeu != null && index < editaleSqeu.length) {
            editaleSqeu[index] = i;
            index++;
            }
        }

        if (saBodyColType[i].equals("0")) // 字符型
        {
            biaBody[i].setDataType(BillItem.STRING);
            biaBody[i].setTatol(false);
            biaBody[i].setLength(saBodyColWidth[i].intValue());
            if ("m_deptName".equals(saBodyColKeyName[i])) {
            biaBody[i].setLength(200);
            biaBody[i].setWidth(140);
            }
        } else if (saBodyColType[i].equals("1")) // 数字型
        {
            biaBody[i].setDataType(BillItem.DECIMAL);
            biaBody[i].setTatol(true);
            biaBody[i].setDecimalDigits(-1 * (saBodyColDecimal[i].intValue()));
            double maxValue = Math.pow(10, saBodyColWidth[i].intValue());
            double minDecimal = Math.pow(10, -1 * saBodyColDecimal[i].intValue());
            ((UIRefPane) biaBody[i].getComponent()).setMaxValue(maxValue - minDecimal);
            ((UIRefPane) biaBody[i].getComponent()).setMinValue(-1 * (maxValue - minDecimal));
            biaBody[i].setLength(saBodyColWidth[i].intValue() + saBodyColDecimal[i].intValue());

            /*
             * if(isItemRefWithoutCurr(saBodyColKeyName[i])) {
             * biaBody[i].setDecimalDigits(4); } else {
             * biaBody[i].setDecimalDigits(getPrecisionForUFDouble()); }
             */
        } else if (saBodyColType[i].equals("3"))// 日期型
        {
            biaBody[i].setDataType(BillItem.DATE);
        } else if (saBodyColType[i].equals("8"))// boolean型
        {
            biaBody[i].setDataType(BillItem.BOOLEAN);
        }
        biaBody[i].setNull(false);
        }
        billmodel = new BillModel();
        billmodel.setBodyItems(biaBody);

        getBspWaData().setTableModel(getItemSortFac().transBillModel(billmodel, ibo.getWaItems_user()));
        getBspWaData().getTable().setSortEnabled(false);
        addListener();
    } catch (Exception ex) {
        Logger.error("initTable:" + ex.getMessage(), ex);
        throw ex;
    }
    }

    /**
     * V57 liangxr 不可编辑列背景色设为兰色
     */
    private void setColor() {
    BillItem[] biaBody = getBspWaData().getTableModel().getBodyItems();
    for (int i = getBspWaData().getLockCol() + 1; i < biaBody.length; i++) {
        if (!biaBody[i].isEdit()) {
        for (int j = 0; j < getBspWaData().getTableModel().getRowCount(); j++) {
            getBspWaData().setCellBackGround(j, biaBody[i].getKey(), new Color(220, 238, 255));
        }
        }
    }
    }

    /**
     * 此处插入方法描述。 创建日期：(2003-9-28 22:00:41)
     *
     * @return boolean
     */
    public boolean isDisplayNest() {
    return displayNest;
    }

    /**
     * 此处插入方法说明。 创建日期：(2001-6-22 14:40:29)
     *
     * @return boolean
     * @param item
     *                java.lang.String
     */
    public boolean isEditableItem(String item) {
    boolean editable = false;

    if (getEditableItem() != null && getEditableItem().length > 0) {
        for (int i = 0; i < getEditableItem().length; i++) {
        if (item.equals(getEditableItem()[i])) {
            editable = true;
            break;
        }
        }
    }
    return editable;
    }

    /**
     * 此处插入方法说明。 创建日期：(2001-11-27 13:03:26)
     *
     * @return boolean
     * @param itemId
     *                java.lang.String
     */
    public boolean isItemRefWithoutCurr(String itemId) {
    boolean isWithout = false;

    if (getStDigitItemRefWithoutCurr() != null) {
        for (int i = 0; i < getStDigitItemRefWithoutCurr().length; i++) {
        if (getStDigitItemRefWithoutCurr()[i].equalsIgnoreCase(itemId)) {
            isWithout = true;
            break;
        }
        }
    }
    return isWithout;
    }

    /*
     * 返回该数据项是否为数字项 数字项可参与运算；非数字项只作为字符串常量 如“数量”为数字项、“存货编码”为非数字项
     */
    public boolean isNumber(java.lang.String itemExpress) {
    return false;
    }

    /**
     * 此处插入方法说明。 创建日期：(2001-9-27 13:15:11)
     *
     * @return boolean
     */
    public boolean isRefreshing() {
    return refreshing;
    }

    /**
     * 此处插入方法说明。 创建日期：(2001-8-31 14:45:14)
     *
     * @return boolean
     */
    public boolean lockWaclassSuceed() throws Exception {
    try {
        boolean b = true;

        PKLock.getInstance().acquireLock(waGlobal.getWaClassPK(), userId, null);
        if (!b) {
        showErrorMessage(NCLangRes.getInstance().getStrByID("60131004", "UPP60131004-000160"));// "正在有人处理该薪资类别的数据，请刷新后再试！"
        }
        return b;
    } catch (Exception e) {
        reportException(e);
        throw e;
    }
    }

    /**
     * 此处插入方法说明。 创建日期：(2001-6-18 16:38:37)
     */
    public void onBoCancel() {
    setData();
    setSaveAndCancel(false);
    }

    /**
     * 此处插入方法说明。 创建日期：(2001-6-18 16:38:37)
     */
    public void onBoCancelCheck() {
    try {
        getCancelCheckDlg().showModal();
        if (!getCancelCheckDlg().isCloseOk()) {
        // 取消
        return;
        } else {
        if (!getCancelCheckDlg().isRangeAll()) {
            if (waData == null || waData.length < 1) {
            showErrorMessage(nc.ui.ml.NCLangRes.getInstance().getStrByID("60131004", "UPP60131004-000177")/*
                                                             * @res
                                                             * "您没有选择人员！"
                                                             */);
            return;
            }
        }
        }
        String chenckwhere = " ";
        String condition = getConditonWithDataPower();
        if (!getCancelCheckDlg().isRangeAll()) {
        chenckwhere += condition;
        } else {
        chenckwhere += deptpower + psnclpower; // yxl update
        }
        // added for V57 by liangxr 部分复审状态中，不允许取消审核
        ibo.canBeCancelCheck();

        WaclassVO waclassVO = ibo.onCancelCheck(chenckwhere, needReCheck, condition);

        ibo.getARecaVO().getWaPeriodvo().setIcheckflag(new Integer(0));
        // 刷新数据显示
        if (!StringUtils.isEmpty(condition)) {
        waData = ibo.getWaData(condition, getOrderStr());

        }

        setData();
        resetButtonSate(waclassVO);
        showHintMessage(nc.ui.ml.NCLangRes.getInstance().getStrByID("60131004", "UPP60131004-000164"));// "取消审核完成!"

    } catch (Exception ex) {
        if (ex instanceof BusinessException) {
        showWarningMessage(ex.getMessage());
        } else {
        showWarningMessage(nc.ui.ml.NCLangRes.getInstance().getStrByID("60131004", "UPP60131004-000167")/* "取消审核失败，请刷新后重试！" */);

        }
    }
    }

    /**
     * 取消发放。 创建日期：(2003-9-12 20:40:14)
     */
    public void onBoCancelPayOff() {
    try {

        // 审批流审批的类别
        PeriodVO period = getCurrClassStateVO();
        if (period != null && (period.getApprovetype().intValue() == 1)) {
        if (showYesNoMessage(nc.ui.ml.NCLangRes.getInstance().getStrByID("60131004", "UPP60131004-000309")/* "审批流审批的薪资类别取消发放后需要重新提交审批，是否继续？" */) != MessageDialog.ID_YES) {
            return;
        }
        }

        WaclassVO waclassVO = ibo.onCancelPayOff();
        resetButtonSate(waclassVO);
        ibo.getARecaVO().getWaPeriodvo().setIpayoffflag(0);
        ibo.getARecaVO().getWaPeriodvo().setCpaydate("");
        ibo.getARecaVO().getWaPeriodvo().setVpaycomment("");

        if (ibo.getWaClassVO().isInTaxGroup()) {
        // 在取消发放后把本薪资项目的wa_dataz扣税项目清空。
        // bsj add 2004-10-09
        deDuctThisTax(ibo.getARecaVO());
        String condition = getConditonWithDataPower();
        if (!StringUtils.isEmpty(condition)) {
            waData = ibo.getWaData(condition, getOrderStr(), getCbxChgSearch().isSelected());
        }

        ibo.getARecaVO().getWaPeriodvo().setIpayoffflag(0);
        ibo.getARecaVO().getWaPeriodvo().setCpaydate("");
        ibo.getARecaVO().getWaPeriodvo().setVpaycomment("");
        setData();
        }
        //取消发放后回写奖金二次分配  医疗产品 帅映杰 begin
        if(waData != null && waData.length > 0){
        	for(int i = 0; i < waData.length; i++){
        		DataVO vo = waData[i];
        		String psnid = vo.getPsnid();
        		String year = ibo.getARecaVO().getAccountYear();
        		String periods = ibo.getARecaVO().getAccountPeriod();
        		HashMap<String, String> Map = getPeriodMonth();
        		
        		IDeptPsnItem impls = (IDeptPsnItem)NCLocator.getInstance().lookup(IDeptPsnItem.class.getName());
        		//impls.ReWriteBonusQap(year, Map.get(periods), psnid);
        	} 	
        }
        //end
        
        showHintMessage(nc.ui.ml.NCLangRes.getInstance().getStrByID("60131004", "UPP60131004-000170"));// "取消发放成功！"

    } catch (nc.vo.pub.BusinessException be) {
        Logger.error(be.getMessage(), be);
        showErrorMessage(be.getMessage());
    } catch (Exception e) {
        Logger.error(e.getMessage(), e);
        showErrorMessage(nc.ui.ml.NCLangRes.getInstance().getStrByID("60131004", "UPP60131004-000171")/*
                                                         * "取消发放失败，请稍候再试！"
                                                         */);
    }
    }

    /**
     * 此处插入方法说明。 创建日期：(2001-6-18 16:38:37)
     */
    public void onBoCancelReCheck() {
    try {

        getCancelCheckDlg().showModal();
        if (!getCancelCheckDlg().isCloseOk()) {
        // 取消
        return;
        } else {
        if (!getCancelCheckDlg().isRangeAll()) {
            if (waData == null || waData.length < 1) {
            showErrorMessage(nc.ui.ml.NCLangRes.getInstance().getStrByID("60131004", "UPP60131004-000177")/*
                                                             * @res
                                                             * "您没有选择人员！"
                                                             */);
            return;
            }
        }
        }
        String chenckwhere = " ";
        String selectWhere = getConditonWithDataPower();
        if (!getCancelCheckDlg().isRangeAll()) {
        chenckwhere += " and " + selectWhere;
        } else {
        chenckwhere += deptpower + psnclpower; // yxl update
        }

        WaclassVO waclassVO = ibo.onCancelReCheck(chenckwhere);
        // 刷新数据显示
        if (selectWhere != null && selectWhere.trim().length() > 0) {
        waData = ibo.getWaData(selectWhere, getOrderStr(), getCbxChgSearch().isSelected());
        setData();
        }
        resetButtonSate(waclassVO);
        showHintMessage(nc.ui.ml.NCLangRes.getInstance().getStrByID("60131004", "UPP60131004-000175"));// "取消复审完成!"
    } catch (Exception ex) {
        showErrorMessage(nc.ui.ml.NCLangRes.getInstance().getStrByID("60131004", "UPP60131004-000176"));// "取消重新审核失败，请刷新后重试！"
    }
    }

    /**
     * 预警
     *
     * @author zhangg on 2009-8-13
     * @throws Exception
     */
    private boolean alter(String keyName) throws Exception {
    // 先查询是否有配置预警,如果有预警,则调预警的代码.
    HRAlertUserData alertUserData = new HRAlertUserData(ibo.getWaYear(), ibo.getWaPeriod());

    String[] files = alertUserData.showMessageAlertFileNameByButton(nc.ui.wa.alert.HRAlertEnter.getBtnKey("60131004", keyName));
    showAlertInfo(files);
    // 工资总额控制
    // start----必须进行工资总额控制，如果没有指定指标计划，会自动通过070530
    String tmAlert = getAuditCreondition(ibo.getWaClassVO());

    if (tmAlert != null && tmAlert.length() > 0 && !tmAlert.substring(tmAlert.length() - 4).equals("@@@@")) {
        AlarmAuditInfomationDlg aid = new AlarmAuditInfomationDlg(this, tmAlert);
        if (aid.showModal() != 1) {
        // throw new BusinessException("取消了该操作！");
        return false;
        }
    }
    return true;
    }

    /**
     * 此处插入方法说明。 创建日期：(2001-6-18 16:38:37)
     */
    public void onBoCheck() {
    try {

        getCheckDlg().showModal();
        if (!getCheckDlg().isCloseOk()) {// 取消
        return;
        } else {
        if (!getCheckDlg().isRangeAll()) {
            if (waData == null || waData.length < 1) {
            showErrorMessage(nc.ui.ml.NCLangRes.getInstance().getStrByID("60131004", "UPP60131004-000177"));// "您没有选择人员！"
            return;
            }
        }
        }
        // 继续进行审核
        String chenckwhere = "";
        String selectWhere = getConditonWithDataPower();
        if (!getCheckDlg().isRangeAll()) {
        chenckwhere += " and " + selectWhere;
        } else {
        chenckwhere += deptpower + psnclpower; // yxl update
        // 审核全体人员，就是审核全体已经计算过的人员,加上此条件。防止审核没有计算过的人员，也防止审核时状态判断出现错误
        //加此条件后可能造成状态判断不正确，改在后台加
    //    chenckwhere += " and  wa_data.irecaculateflag >=1 ";
        }

        String keyName = nc.ui.ml.NCLangRes.getInstance().getStrByID("60131004", "UPT60330407-000002")/* "审核" */;
        boolean flag = alter(keyName);
        if (!flag) {
        return;
        }

        WaclassVO waclassVO = null;
        if (!getCheckDlg().isRangeAll() && getCbxChgSearch().isSelected()) {
        waclassVO = ibo.onCheckChg(chenckwhere, needReCheck, selectWhere);
        } else {
        waclassVO = ibo.onCheck(chenckwhere, needReCheck, selectWhere);
        }

        // 刷新数据显示
        if (selectWhere != null && selectWhere.trim().length() > 0) {
        waData = ibo.getWaData(selectWhere, getOrderStr(), getCbxChgSearch().isSelected());
        setData();
        }
        resetButtonSate(waclassVO);

    } catch (Exception ex) {
        reportException(ex);
        showErrorMessage(ex.getMessage());// "审核失败，请刷新后重试！");
    }
    }

    /**
     * 此处插入方法说明。 创建日期：(2001-6-18 16:38:37)
     */
    public void onBoFirst() {
    try {
        if (!onChangedRow()) {
        return;
        }
        selRow = 0;
        selDataVO = (DataVO) waData[0].clone();
        setButtonUpdateState();
        setCardData();

        updateinit = 1;
    } catch (Exception ex) {

    }
    }

    private DataVO[] queryWaDataByConditon() throws Exception {
    String conditon = getConditonWithDataPower();
    waData = ibo.getWaData(conditon, getOrderStr(), getCbxChgSearch().isSelected());
    return waData;

    }

    /**
     * 此处插入方法说明。 创建日期：(2001-6-18 16:38:37)
     */
    public void onBoLast() {
    try {
        if (!onChangedRow()) {
        return;
        }
        selRow = waData.length - 1;
        selDataVO = (DataVO) waData[selRow].clone();
        setButtonUpdateState();
        setCardData();

        updateinit = 1;
    } catch (Exception ex) {

    }
    }

    /**
     * 此处插入方法说明。 创建日期：(2001-6-18 16:38:37)
     */
    public void onBoNext() {
    try {
        if (!onChangedRow()) {
        return;
        }
        selRow++;
        selDataVO = (DataVO) waData[selRow].clone();
        setButtonUpdateState();
        setCardData();
        updateinit = 1;
    } catch (Exception ex) {
        reportException(ex);
        Logger.error(ex.getMessage(), ex);
    }
    }

    /**
     * 此处插入方法描述。 创建日期：(2003-9-12 20:40:00)
     */
    public void onBoPayOff() {
    try {

        String payDate = getUIRefPayDay().getRefName();
        String payComment = getUITaComment().getText();

        if ((payDate == null || payDate.trim().length() < 1) && (payComment == null || payComment.trim().length() < 1)) {
        if (showYesNoMessage(nc.ui.ml.NCLangRes.getInstance().getStrByID("60131004", "UPP60131004-000306")) != UIDialog.ID_YES) {// 没有输入发放日期和发放说明，如果此次不输入，以后将不可修改，要继续吗？
            return;
        }
        } else if (payDate == null || payDate.trim().length() < 1) {
        if (showYesNoMessage(nc.ui.ml.NCLangRes.getInstance().getStrByID("60131004", "UPP60131004-000182")) != UIDialog.ID_YES) {// "没有输入发放日期，如果此次不输入，以后将不可修改，要继续吗？"
            return;
        }
        }

        else if (payComment == null || payComment.trim().length() < 1) {
        if (showYesNoMessage(nc.ui.ml.NCLangRes.getInstance().getStrByID("60131004", "UPP60131004-000183")) != UIDialog.ID_YES) {// "没有输入发放说明，如果此次不输入，以后将不可修改，要继续吗？"
            return;
        }
        }

        ibo.getARecaVO().getWaPeriodvo().setCpaydate(payDate);
        ibo.getARecaVO().getWaPeriodvo().setVpaycomment(payComment);

        ibo.getARecaVO().setRangeAll(true);
        ibo.getARecaVO().setModeAll(true);

        ibo.getARecaVO().setSelCondition(" wa_data.istopflag=0 ");

        String reCacuCondition = deptpower + psnclpower;

        ibo.getARecaVO().setReCacuCondition(reCacuCondition);

        WaclassVO waclassVO = ibo.onPayOff();
        resetButtonSate(waclassVO);

        ibo.getARecaVO().getWaPeriodvo().setIpayoffflag(1);
        String whereSql = getConditonWithDataPower();
        if (ibo.getWaClassVO().isInTaxGroup()) {
        if (whereSql != null && whereSql.trim().length() > 0) {
            /**
             * Modified by Young 2006-03-20 Start 审核查询
             */
            waData = ibo.getWaData(whereSql, getOrderStr(), getCbxChgSearch().isSelected());
            /**
             * Modified by Young 2006-03-20 End 审核查询
             */

        }

        setData();
        } else {
        try {
            getUIRefPayDay().setPK(ibo.getARecaVO().getWaPeriodvo().getCpaydate());
            getUIRefPayDay().setValue(ibo.getARecaVO().getWaPeriodvo().getCpaydate());
            getUITaComment().setText(ibo.getARecaVO().getWaPeriodvo().getVpaycomment());
        } catch (Exception e) {
            reportException(e);
            showErrorMessage(e.getMessage());
        }
        }

        try {
        getUIRefPayDay().setPK(ibo.getARecaVO().getWaPeriodvo().getCpaydate());
        getUIRefPayDay().setValue(ibo.getARecaVO().getWaPeriodvo().getCpaydate());
        getUITaComment().setText(ibo.getARecaVO().getWaPeriodvo().getVpaycomment());
        } catch (Exception e) {
        reportException(e);
        showErrorMessage(e.getMessage());
        }
        showHintMessage(nc.ui.ml.NCLangRes.getInstance().getStrByID("60131004", "UPP60131004-000184"));// "发放成功！"
        // 先查询是否有配置预警,如果有预警,则调预警的代码.
        // 先查询是否有配置预警,如果有预警,则调预警的代码.
        HRAlertUserData alertUserData = new HRAlertUserData(ibo.getWaYear(), ibo.getWaPeriod());
        String[] files = alertUserData.showMessageAlertFileNameByButton(nc.ui.wa.alert.HRAlertEnter.getBtnKey("60131004", nc.ui.ml.NCLangRes.getInstance().getStrByID("60131004",
            "UPT60131004-000008")/* "发放" */));
        showAlertInfo(files);
        
        //发放后回写奖金二次分配  医疗产品 帅映杰 begin
        if(waData != null && waData.length > 0){
        	for(int i = 0; i < waData.length; i++){
        		DataVO vo = waData[i];
        		String psnid = vo.getPsnid();
        		String year = ibo.getARecaVO().getAccountYear();
        		String period = ibo.getARecaVO().getAccountPeriod();
        		HashMap<String, String> Map = getPeriodMonth();
        		
        		IDeptPsnItem impls = (IDeptPsnItem)NCLocator.getInstance().lookup(IDeptPsnItem.class.getName());
        		//impls.ReWriteBonusQap(year, Map.get(period), psnid);
        	} 	
        }
        //end

    } catch (nc.vo.pub.BusinessException be) {
        showErrorMessage(be.getMessage());
    } catch (Exception e) {
        showErrorMessage(nc.ui.ml.NCLangRes.getInstance().getStrByID("60131004", "UPP60131004-000185")/* "发放失败，请稍候再试！" */);
    } finally {
        freeBm();
    }
    }
    
	private HashMap<String, String> getPeriodMonth(){
		HashMap<String, String> PeriodMpnthMap = new HashMap<String, String>();
		String[] monthA = new String[]{"01","02","03","04","05","06","07","08","09","10","11","12","一季度","二季度","三季度","四季度","上半年","下半年","全年","虚拟"};
		String[] monthB = new String[]{"一月","二月","三月","四月","五月","六月","七月","八月","九月","十月","十一月","十二月","第一季度","第二季度","第三季度","第四季度","上半年","下半年","本年",""};
		for(int i = 0; i < monthA.length; i++){
			PeriodMpnthMap.put(monthA[i], monthB[i]);
		}
		return PeriodMpnthMap;
	}

    /**
     * 此处插入方法描述。 创建日期：(2003-9-16 21:05:06)
     */
    public void onBoPeriodSet() {
    getSharePeriodSetDlg().initData();
    getSharePeriodSetDlg().showModal();
    if (getSharePeriodSetDlg().getResult() == UIDialog.ID_OK) {

    }
    }

    /**
     * 此处插入方法说明。 创建日期：(2001-6-18 16:38:37)
     */
    public void onBoPre() {
    try {
        if (!onChangedRow()) {
        return;
        }
        selRow--;
        selDataVO = (DataVO) waData[selRow].clone();
        setButtonUpdateState();
        setCardData();
        updateinit = 1;
    } catch (Exception ex) {

    }
    }

    /**
     * 打印。 创建日期：(2000-8-15 17:04:30)
     *
     * @throws BusinessException
     *
     * @author：张 森
     */
    public void onBoPrint() throws BusinessException {
    // 设置 标示,从而重新得到 是否打印0值 的参数值
    setGetFlag(false);
    if (waData == null || waData.length < 1) {
        showErrorMessage(nc.ui.ml.NCLangRes.getInstance().getStrByID("60131001", "UPP60131001-000058")/*
                                                         * @res
                                                         * "没有可打印的数据！"
                                                         */);
        return;
    }

    String[] deptpks = new String[waData.length];
    for (int i = 0; i < waData.length; i++) {
        deptpks[i] = waData[i].getDeptid();
    }
    PrintOnDept printByDept = new PrintOnDept(getTitle(), deptpks);
    if (printByDept.isPrintAll()) {

        nc.ui.pub.print.PrintEntry print = new nc.ui.pub.print.PrintEntry(this);

        print.setTemplateID(pkCorp, getModuleCode(), userId, null);
        print.selectTemplate();

        print.beginBatchPrint();  //2012.10.17 根据湘雅2 提的要求，批量打印时要区分 部门不能合并在一起  故本行代码注释掉

        int type = print.getBatchExportType();
        Vector<Vector> v = printByDept.onDept(waData);
        if (v != null) {
        	int  no  = 0;
        for (Vector vs : v) {
            DataVO[] datas = new DataVO[vs.size()];
            vs.copyInto(datas);
            WageAlternateUI ds = new WageAlternateUI();
            ds.getUIRefPayDay().setText(this.getUIRefPayDay().getText());
            ds.getUITaComment().setText(this.getUITaComment().getText());
            ds.waData = datas;
            print.setDataSource(ds);
           
            if(type ==  Constants.EXPORT_TYPE_PRINT){//选中直接打印
            	 if(no == 0 ){
                 	if(print.print(true,true)==-1){
                 		return;
                 	}
                 }else{
                 	if(print.print(true,false)==-1){
                 		return;
                 	}
                 }
            	 no++;
            }
            
        }
        }
        if(type !=  Constants.EXPORT_TYPE_PRINT){
        	print.endBatchPrint(); //2012.10.17 根据湘雅2 提的要求，批量打印时要区分 部门不能合并在一起  故本行代码注释掉
        }
        

    } else {
        if (printByDept.getIDRst() == UIDialog.ID_CANCEL) {
        return;
        }
        nc.ui.pub.print.PrintEntry print = new nc.ui.pub.print.PrintEntry(this, this);

        print.setTemplateID(pkCorp, getModuleCode(), userId, null);
        // print.editTemplate();//进入模板编辑
        if (print.selectTemplate() >= 0) {
        print.preview();
        }

    }
    }

    /**
     * 此处插入方法说明。 创建日期：(2004-6-16 11:23:41)
     */
    public void onBoPsnComute() {
    try {
        DlgShowResult dlg = new DlgShowResult(this, ibo.getWaClassVO(), deptpower, psnclpower);
        dlg.showModal();
    } catch (Exception e) {
        reportException(e);
        showErrorMessage(e.getMessage());
    }
    }

    /**
     *
     * 创建日期：(2001-6-18 16:38:37)
     */
    public void onBoReCal() {

    try {
        // 如果有数据变动则保存
        if (m_boSave.isEnabled()) {
        if (onSave()) {
            setSaveAndCancel(false);
        }
        }

        // 判断员工薪资变动取值发放方式
        if (ibo.getWaClassVO().getPayoffflag().intValue() == 1) {
        if (getIsCompute() == false) {
            if (showYesNoMessage(nc.ui.ml.NCLangRes.getInstance().getStrByID("60131004", "UPP60131004-000186")) != MessageDialog.ID_YES) {// "有员工调薪，应先进行计算,是否继续?"
            return;
            }
        }
        }
        // 判别福利
        if (!checkBm()) {
        return;
        }

        String whereSql = getConditonWithDataPower();
        // 计算选择
        getRecacuDlg().showModal();
        boolean flag = getRecacuDlg().isCloseOk();
        if (!flag) {// 取消
        return;
        } else {
        if (!getRecacuDlg().isRangeAll()) {
            if (waData == null || waData.length < 1) {
            showErrorMessage(nc.ui.ml.NCLangRes.getInstance().getStrByID("60131004", "UPP60131004-000177"));// 您没有选择人员！"
            return;
            }

            if (whereSql != null && whereSql.toLowerCase().indexOf(".f_") >= 0) {
            if (showYesNoMessage(nc.ui.ml.NCLangRes.getInstance().getStrByID("60131004", "UPP60131004-000187")) == UIDialog.ID_NO) {// "当前的选择条件中含有薪资项目，这可能会导致某些人员的数据计算错误，确实要计算吗？\n建议您重新设置选择条件。"
                return;
            }
            }
        }
        }

        ibo.getARecaVO().setRangeAll(getRecacuDlg().isRangeAll());
        ibo.getARecaVO().setModeAll(getRecacuDlg().isModeAll());

        if (getRecacuDlg().isRangeAll() && (whereSql == null || whereSql.trim().length() < 1)) {
        ibo.getARecaVO().setSelCondition(" wa_data.istopflag=0 ");
        } else {
        ibo.getARecaVO().setSelCondition(whereSql);
        }
        // yxl update
        String reCacuCondition = " wa_data.icheckflag=0 ";

        if (!getRecacuDlg().isRangeAll()) {

        reCacuCondition += " and " + whereSql;
        } else {
        reCacuCondition += deptpower + psnclpower;// yxl update
        }

        if (!getRecacuDlg().isModeAll()) {
        // 只计算没有计算的人员
        reCacuCondition += " and wa_data.irecaculateflag <> 1 ";
        }

        ibo.getARecaVO().setReCacuCondition(reCacuCondition);

        final BannerDialog dialog = new BannerDialog(getFrame());
        dialog.start();

        SwingWorker worker = new SwingWorker() {

        @Override
        public Object construct() {
            try {
            WaclassVO waclassVO = ibo.onRecaculate(getCaculateInfoVO());

            resetButtonSate(waclassVO);
            String whereSql = getConditonWithDataPower();
            if (whereSql != null && whereSql.trim().length() > 0) {

                waData = ibo.getWaData(whereSql, getOrderStr(), getCbxChgSearch().isSelected());

            }
            } catch (Exception ex) {
            Logger.error(ex.getMessage(), ex);
            if (ex.getMessage() != null && ex.getMessage().trim().length() > 0) {
                showErrorMessage(ex.getMessage());
            } else {
                showErrorMessage(nc.ui.ml.NCLangRes.getInstance().getStrByID("60131004", "UPP60131004-000192")/* "计算失败，请刷新后重试！" */);
            }
            }
            return null;
        }

        @Override
        public void finished() {
            try {
            setData();

            showHintMessage(nc.ui.ml.NCLangRes.getInstance().getStrByID("60131004", "UPP60131004-000191")/* "计算完成！" */);

            // 先查询是否有配置预警,如果有预警,则调预警的代码.
            HRAlertUserData alertUserData = new HRAlertUserData(ibo.getWaYear(), ibo.getWaPeriod());

            String[] files = alertUserData.showMessageAlertFileNameByButton(nc.ui.wa.alert.HRAlertEnter.getBtnKey("60131004", nc.ui.ml.NCLangRes.getInstance().getStrByID("60131004",
                "UPT60131007-000008")/* "计算" */));
            showAlertInfo(files);

            } catch (Exception e) {
            e.printStackTrace();
            } finally {
            dialog.end();
            }
        }
        };
        worker.start();

    } catch (Exception ex) {
        Logger.error(ex.getMessage(), ex);

        if (ex.getMessage() != null && ex.getMessage().trim().length() > 0) {
        showErrorMessage(ex.getMessage());
        } else {
        showErrorMessage(nc.ui.ml.NCLangRes.getInstance().getStrByID("60131004", "UPP60131004-000192"));// "计算失败，请刷新后重试！
        }
    } finally {
        freeBm();
    }
    }

    /**
     * @author zhangg Created on 2007-7-10
     * @return
     */
    private CaculateInfoVO getCaculateInfoVO() {
    CaculateInfoVO caculateInfoVO = new CaculateInfoVO();// 下列信息可能在选择不同的类别的时候有所变化
    caculateInfoVO.setCorppk(Global.getCorpPK());
    caculateInfoVO.setCorpname(Global.getCorpname());
    caculateInfoVO.setWa_classname(Global.getWaclassname());
    caculateInfoVO.setPk_waclass(Global.getWaClass());
    caculateInfoVO.setUsername(Global.getUserName());
    caculateInfoVO.setUserid(Global.getUserID());
    return caculateInfoVO;
    }

    /**
     * 此处插入方法说明。 创建日期：(2001-6-18 16:38:37)
     */
    public void onBoReCheck() {
    try {
        getCheckDlg().showModal();
        if (!getCheckDlg().isCloseOk()) {// 取消
        return;
        } else {
        if (!getCheckDlg().isRangeAll()) {
            if (waData == null || waData.length < 1) {
            showErrorMessage(nc.ui.ml.NCLangRes.getInstance().getStrByID("60131004", "UPP60131004-000177"));// "您没有选择人员！"
            return;
            }
        }
        }

        String tmAlert = getAuditCreondition(ibo.getWaClassVO());

        if (tmAlert != null && tmAlert.length() > 0 && !tmAlert.substring(tmAlert.length() - 4).equals("@@@@")) {
        AlarmAuditInfomationDlg aid = new AlarmAuditInfomationDlg(this, tmAlert);
        if (aid.showModal() != 1) {
            return;
        }
        }

        String checkWhere = "";
        String selectWhere = getConditonWithDataPower();
        if (!getCheckDlg().isRangeAll()) {
        checkWhere += " and " + selectWhere;
        } else {
        checkWhere += deptpower + psnclpower; // yxl update
        }

        WaclassVO waclassVO = ibo.onReCheck(checkWhere, selectWhere);

        // 刷新数据显示
        if (selectWhere != null && selectWhere.trim().length() > 0) {
        waData = ibo.getWaData(selectWhere, getOrderStr(), getCbxChgSearch().isSelected());
        setData();
        }
        showHintMessage(nc.ui.ml.NCLangRes.getInstance().getStrByID("60131004", "UPP60131004-000194"));// "复审完成！"

        resetButtonSate(waclassVO);

        // 审批流审批的类别，还未审批通过的时候，发放按钮不能使用
        PeriodVO period = waclassVO.getCurrentPeriodVO();
        if (period != null && (period.getApprovetype().intValue() == 1) && period.getIsapproved().intValue() != 1) {
        this.m_boPayOff.setEnabled(false);
        updateButtons();
        }

    } catch (Exception ex) {
        showErrorMessage(ex.getMessage());// "复审失败，请刷新后重试！"
    }
    }

    /**
     * 说明，该方法不要再其他节点调用。因为该方法消耗链接数
     * 请调用refreshDataAndState()
     * 此处插入方法说明。 创建日期：(2001-6-18 16:38:37)
     */
    public void onBoRefresh() {
    	if(m_boSave.isEnabled()){
    		int x = showOkCancelMessage("有未保存数据，是否需要保存已修改的数据");
    		if(x==UIDialog.ID_OK){
    			return;
    		}
    	}
    setRefreshing(true);
    // yxl add
    loading = true;

    selDlg = null;

    // 保证用在刷新时，能够取到最新的薪资发放项目。
    initialize();
    if (!haveException) {
        onBoSearch();
    }
    // yxl add
    loading = false;
    setRefreshing(false);    
    }

    /**
     * 此处插入方法说明。 创建日期：(2001-6-18 16:38:37)
     */
    public void onBoReplace() {
    if (getEditableItem() == null || getEditableItem().length < 1) {
        showErrorMessage(nc.ui.ml.NCLangRes.getInstance().getStrByID("60131004", "UPP60131004-000196")/*
                                                         * @res
                                                         * "没有可替换的项目！"
                                                         */);
        return;
    }
    try {
        getReplaceDlg().initData(ibo.getWaClassItemMap(),getEditableItem(), getEditableName(), getEditableType(), getItemCodes(), getItemNames(), getItemTypes());
        getReplaceDlg().clear();
        getReplaceDlg().showModal();
        String whereSql = getConditonWithDataPower();
        if (getReplaceDlg().getResult() == UIDialog.ID_OK) {

        boolean ok = false;

        String[][] itemBeReplaced = new String[1][2];
        itemBeReplaced[0] = getReplaceDlg().getItemReplaced();

        if (getReplaceDlg().isAllReplace()) {
            /* modified by lhp 添加替换时的部门权限,只替换未审核的 */
            ok = chechState(ibo.replaceSel(" wa_data.icheckflag=0 " + deptpower + psnclpower, itemBeReplaced, new String[] { getReplaceDlg().getFormula() }));
        } else {
            if (waData == null || waData.length < 1) {
            showErrorMessage(nc.ui.ml.NCLangRes.getInstance().getStrByID("60131004", "UPP60131004-000197")/*
                                                             * @res
                                                             * "没有选中要替换的数据！"
                                                             */);
            return;
            }

            ok = chechState(ibo.replaceSel(whereSql,// waData,
                itemBeReplaced, new String[] { getReplaceDlg().getFormula() }));
        }

        if (ok) {
            if (whereSql != null && whereSql.trim().length() > 0) {
            /**
             * Modified by Young 2006-03-20 Start 审核查询
             */
            waData = ibo.getWaData(whereSql, getOrderStr(), getCbxChgSearch().isSelected());
            /**
             * Modified by Young 2006-03-20 End 审核查询
             */

            }

            setData();
            showHintMessage(nc.ui.ml.NCLangRes.getInstance().getStrByID("60131004", "UPP60131004-000198")/*
                                                             * @res
                                                             * "替换成功!"
                                                             */);
        }
        }
    } catch (Exception ex) {
        if (ex.getMessage() == null) {
        showErrorMessage(nc.ui.ml.NCLangRes.getInstance().getStrByID("60131004", "UPP60131004-000199")/*
                                                         * @res
                                                         * "替换失败，请检查公式或刷新后重试！"
                                                         */);
        } else {
        showErrorMessage(ex.getMessage());
        }
    } finally {

        freeBm();
    }
    }

    public void onBoUpdate() {
    boolean lock = false;
    try {

        lock = lockWaclassSuceed();
        if (lock == false) {
        return;
        }
        if (getEditableItem() == null || getEditableItem().length < 1) {
        showErrorMessage(nc.ui.ml.NCLangRes.getInstance().getStrByID("60131004", "UPP60131004-000212")/* "没有可修改的项目！" */);
        freeWaclass();
        return;
        }

        hasSaved = true;

        remove(getUITopPanel1());
        remove(getpnlList());
        add(getUpdateBillCardPanel(), BorderLayout.CENTER);

        setCardData();

        updateUI();

        setButtons(m_ButtonGroup2);

        updateButtons();
        setButtonUpdateState();
        updateinit = 1;
    } catch (Exception ex) {
        reportException(ex);
        showErrorMessage(nc.ui.ml.NCLangRes.getInstance().getStrByID("60131004", "UPP60131004-000208")/**
                                                         *
                                                         * "修改失败，请检查所修改的数据或刷新后重试！"
                                                         */
        );
    } finally {
        if (lock) {
        try {
            freeWaclass();
        } catch (Exception e) {
        }
        }
    }
    }

    /**
     * 此处插入方法说明。 创建日期：(2001-6-18 16:38:37)
     */
    public void onBoReTotal() {

    if (showYesNoMessage(nc.ui.ml.NCLangRes.getInstance().getStrByID("60131004", "UPP60131004-000200")/* "汇总将覆盖当前的数据，确实要汇总吗？" */) != UIDialog.ID_YES) {
        return;
    }

    boolean lock = false;
    try {
        lock = lockWaclassSuceed();
        if (lock) {

        ibo.onReTotal();
        String whereSql = getConditonWithDataPower();
        if (whereSql != null && whereSql.trim().length() > 0) {
            // 汇总后刷新数据
            refreshDataAndState();
//            onBoRefresh();
        }
        showHintMessage(nc.ui.ml.NCLangRes.getInstance().getStrByID("60131004", "UPP60131004-000205")/* "汇总完成！" */);

        }
    } catch (Exception ex) {
    	Logger.error(ex.getMessage(),ex);
        showErrorMessage(ex.getMessage());
    } finally {
        if (lock) {
        try {
            freeWaclass();
        } catch (Exception e) {
        }
        }
    }
    }

    boolean hasSaved = false;

    public boolean onChangedRow() {
    if (!hasSaved) {
        int ret = MessageDialog.showYesNoCancelDlg(this, LoginPanelRes.getUFSoft()/*
                                             * @res
                                             * "用友软件"
                                             */, NCLangRes.getInstance().getStrByID("60130119", "UPP60130119-000153")/*
                                             * @res
                                             * "正在编辑的信息没有保存，是否在退出窗口前保存？"
                                             */);

        if (ret == MessageDialog.ID_YES) {
        if (onBoUpdateOk()) {
            return true;
        } else {
            return false;
        }

        } else if (ret == MessageDialog.ID_NO) {
        return true;

        } else if (ret == MessageDialog.ID_CANCEL) {
        return false;
        }
    }
    return true;
    }

    /**
     * 此处插入方法说明。 创建日期：(2001-6-18 16:38:37)
     */
    public void onBoReturn() {
    try {
        add(getUITopPanel1(), BorderLayout.NORTH);
        add(getpnlList(), BorderLayout.CENTER);
        remove(getUpdateBillCardPanel());

        updateUI();

        getBspWaData().getTable().setRowSelectionInterval(selRow, selRow);

        setButtons(m_MainButtonGroup);

        updateButtons();

        setData();
        updateinit = 0;
    } catch (Exception ex) {
        reportException(ex);
        showErrorMessage(nc.ui.ml.NCLangRes.getInstance().getStrByID("60131004", "UPP60131004-000206")/*
                                                         * @res
                                                         * "返回失败!"
                                                         */);
    }
    }

    /**
     * 此处插入方法说明。 创建日期：(2001-6-18 16:38:37)
     */
    public boolean onBoSave() {
    try {
        if (vecEdit == null || vecEdit.size() < 1) {
        return false;
        }

        beforeSave();

        if (onSave()) {
        setSaveAndCancel(false);
        showHintMessage(nc.ui.ml.NCLangRes.getInstance().getStrByID("60131004", "UPP60131004-000207")/*
                                                         * @res
                                                         * "修改成功！"
                                                         */);
        }
        
        refreshDataAndState();
//        onBoRefresh();
    } catch (Exception ex) {
        if (ex.getMessage() == null) {
        showErrorMessage(nc.ui.ml.NCLangRes.getInstance().getStrByID("60131004", "UPP60131004-000208")/*
                                                         * @res
                                                         * "修改失败，请检查所修改的数据或刷新后重试！"
                                                         */);
        } else {
        showErrorMessage(ex.getMessage());
        }
        return false;
    }
    return true;
    }

    /**
     * 提供给其他节点用的查询ORDER BY 排序条件
     *
     * @author zhangg on 2009-6-18
     * @param pk_waclass
     * @return
     */
    public static String getOrderStrForOrtherFuncNode(String pk_waclass) {
    SortConfigDialog dialog = new SortConfigDialog();
    dialog.setTableCode(pk_waclass);
    dialog.setModuleCode("60131004");
    dialog.btnLoad_ActionPerformed(null);

    // add for v57
    // dialog.setStrClassType(pk_waclass);
    return dialog.getOrderStr();
    }

    /**
     * 此处插入方法说明。 创建日期：(2001-6-18 16:38:37)
     */
    public void onBoSearch() {
    try {
    	if(m_boSave.isEnabled()){
    		int x = showOkCancelMessage("有未保存数据，是否需要保存已修改的数据");
    		if(x==UIDialog.ID_OK){
    			return;
    		}
    	}
        if (getUIRefWaClass().getRefPK() == null) {
        showErrorMessage(nc.ui.ml.NCLangRes.getInstance().getStrByID("60131004", "UPP60131004-000333")/*
                                                         * @res
                                                         * "请选择薪资类别！"
                                                         */);
        return;
        }
        if (!isRefreshing()) {
        // add by sunxj 2010-02-23 快速查询插件 start
        isQuickSearch = false;// 标记是否快速查询过
        // add by sunxj 2010-02-23 快速查询插件 end
        getSelDlg().showModal();
        if (getSelDlg().getResult() == UIDialog.ID_OK) {
            // 5.5采用先设置排序条件，不强制每次弹出来选择设置
            setSortItems(getSortDialog());
            setOrderStr(getSortDialog().getOrderStr());
            ibo.setSelectConditon(getConditonWithDataPower());
            // 保存查询条件，供刷新调用
            setTheLatestCondition(getConditonWithDataPower());
        } else {
            return;
        }
        }
        // 刷新或者 查询点击确认
        String whereSql = "";
        if (isRefreshing()) {
        whereSql = getTheLatestCondition();
        } else {
        whereSql = getConditonWithDataPower();
        }

        if (whereSql != null) {
        // update by sunxj 2010-02-23 快速查询插件 start
        // waData = ibo.getWaData(whereSql, getOrderStr(),
        // getCbxChgSearch().isSelected());
        if (isQuickSearch) {
            waData = ibo.getWaData(whereSql, getOrderStr(), false);
        } else {
            waData = ibo.getWaData(whereSql, getOrderStr(), getCbxChgSearch().isSelected());
        }
        // update by sunxj 2010-02-23 快速查询插件 end
        } else {
        waData = null;
        }
        setData();

        // 设定数据完毕，重新设置按钮状态.因为查询的数据变了

        // resetButtonSate(ibo.getWaClassVO());
        resetButtonSate(ibo.resetWaClassVOWithCondition(whereSql));
        // 设定按钮状态。

        if (!isRefreshing()) {
        showHintMessage(nc.ui.ml.NCLangRes.getInstance().getStrByID("60131004", "UPP60131004-000209")/*
                                                         * @res
                                                         * "共有"
                                                         */
            + waData.length + nc.ui.ml.NCLangRes.getInstance().getStrByID("60131004", "UPP60131004-000210")/*
                                                             * @res
                                                             * "条数据。"
                                                             */);
        } else {
        showHintMessage(nc.ui.ml.NCLangRes.getInstance().getStrByID("60131004", "UPP60131004-000211")/*
                                                         * @res
                                                         * "刷新完毕!"
                                                         */);
        }

    } catch (Exception ex) {
        reportException(ex);
        Logger.error(ex.getMessage(), ex);
        showErrorMessage(ex.getMessage());
    }
    }

    /**
     * 不允许选中列，从而做到高亮显示
     *
     * @param table
     */
    private void highlightDisplay(nc.ui.pub.bill.BillScrollPane.BillTable table) {
    table.setColumnSelectionAllowed(false);
    }

    /**
     * 此处插入方法说明。 创建日期：(2001-6-18 16:38:37)
     */
    public boolean onBoUpdateOk() {
    // onSaveCardEdit();
    boolean onsuccess = onSaveCardEdit();
    if (onsuccess) {
        updateinit = 2;
    }
    return onsuccess;
    }

    /**
     * 子类实现该方法，响应按钮事件。
     *
     * @version (00-6-1 10:32:59)
     *
     * @param bo
     *                ButtonObject
     * @throws BusinessException
     */
    @Override
    public void onButtonClicked(ButtonObject bo) {
    showHintMessage("");

    if (bo == m_boSearch) {
        onBoSearch();
    } else if (bo == m_boReCal) {
        onBoReCal();
    } else if (bo == m_boCheck) {
        onBoCheck();
    } else if (bo == m_boCancelCheck) {
        onBoCancelCheck();
    } else if (bo == m_boReCheck) {
        onBoReCheck();
    } else if (bo == m_boCancelReCheck) {
        onBoCancelReCheck();
    } else if (bo == m_boSave) {
        onBoSave();
    } else if (bo == m_boCancel) {
        onBoCancel();
    } else if (bo == m_boReplace) {
        onBoReplace();
    } else if (bo == m_boUpdate) {
        onBoUpdate();
    } else if (bo == m_boReturn) {
        onBoReturn();
    } else if (bo == m_boUpdateOk) {
        onBoUpdateOk();
    } else if (bo == m_boFirst) {
        onBoFirst();
    } else if (bo == m_boPre) {
        onBoPre();
    } else if (bo == m_boNext) {
        onBoNext();
    } else if (bo == m_boLast) {
        onBoLast();
    } else if (bo == m_boReTotal) {
        onBoReTotal();
    } else if (bo == m_boRefresh) {
        onBoRefresh();
    } else if (bo == m_boPrint) {
        try {
        onBoPrint();
        } catch (BusinessException e) {
        e.printStackTrace();
        }
    } else if (bo == m_boPayOff) {
        onBoPayOff();
    }
    else if (bo == m_boCancelPayOff) {
        onBoCancelPayOff();
    } else if (bo == m_bopsnCompute) {
        onBoPsnComute();
    } else if (bo == m_boDispChg) {
        onDispChg();
    } else if (bo == m_btnConstrast) { // 发放明细对比
        onViewConstrast();
    } else if (bo == m_boSort) {
        onSetOrderStr();
    } else if (bo == m_boDirectPrint) {
        onDirectPrint();
    } else if (bo == m_boReTotal4MulDeptItem) {
    	onBtnTotal4MulDeptItem();


    } else if (bo == m_boWatch4MulDeptItem) {
        watchDataDeptDetail();
    }else if(bo==m_btnConfirm){
    	try{
    	WaclassVO classvo =  ibo.getWaClassVO();
    	if(classvo==null) return;
    	int x = MessageDialog.showOkCancelDlg(this, "提示", "确定后,当前期间数据将不能修改!");
    	if(x==UIDialog.ID_OK){
    		 String userid = ClientEnvironment.getInstance().getUser().getPrimaryKey();
    		 PsnConfirmVO vo = new PsnConfirmVO();
    		 vo.setDr(0);
    		 vo.setDdate(ClientEnvironment.getInstance().getDate());
    		 vo.setPk_corp(classvo.getPk_corp());
    		 vo.setVconfirmpsn(userid);
    		 vo.setVyear(classvo.getCyear());
    		 vo.setVperiod(classvo.getCperiod());
    		 vo.setPk_waclass(classvo.getPrimaryKey());
    		 HYPubBO_Client.insert(vo);
    		 showHintMessage("确定完成");
    		 resetButtonSate(classvo);
    	}
    	}catch(Exception e){
    		e.printStackTrace();
    		showHintMessage("确定错误:"+e.getMessage()+"");
    	}
    }
    }

    public void checkWaClassSealState() throws BusinessException {
    WaGlobal.checkWaClassSealState();
    }
    /**
     * TXT输出
     *
     */
   private void onBoTxtOut() {
	  try {
		  if(ibo==null||ibo.getWaClassVO()==null) return;
		WaclassVO classvo =  ibo.getWaClassVO();
		String year = classvo.getCopyear();
		String month = classvo.getCopperiod();
		String classid = classvo.getPrimaryKey();
		StringBuffer buffer = new StringBuffer();
		buffer.append(" select f_4 nmny,p.vacccode vbatchcode from wa_data d inner join wa_psn p on p.classid=d.classid ");
		buffer.append(" and p.cyear=d.cyear and p.cperiod=d.cperiod and p.psnid=d.psnid ");
		buffer.append(" where d.cyear='"+year+"' and d.cperiod='"+month+"' and d.classid='"+classid+"' and isnull(d.dr,0)=0 and d.istopflag=0 ");
		IUAPQueryBS bs = NCLocator.getInstance().lookup(IUAPQueryBS.class);
		ArrayList<DeptSumVO> list = (ArrayList<DeptSumVO>)bs.executeQuery(buffer.toString(),  new BeanListProcessor(DeptSumVO.class) );
		if(list==null||list.size()<=0){
			MessageDialog.showHintDlg(this, "提示", "没有需要输出的数据");
			return;
		}
		TxtOut out = new TxtOut(this);
		if(out.createTxtFile(list, year, month)){
			MessageDialog.showHintDlg(this, "提示", "输出完成");
		}
	} catch (BusinessException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		MessageDialog.showHintDlg(this, "提示", "输出错误："+e.getMessage()+"");
	}
	  
   }
    /**
     * 用户点击修改，进入修改界面。 该界面下的按钮点击，不需要进行状态核查。 m_boRefresh 也不需要进行状态核查
     *
     * @param bo
     * @return
     */
    public boolean needRefreshState(ButtonObject bo) {
    if (bo == m_boRefresh || bo == m_boNext || bo == m_boPre || bo == m_boFirst || bo == m_boLast || bo == m_boOK || bo == m_boReturn || bo == m_boCancel) {
        return false;
    } else {
        return true;
    }

    }

    /**
     * 直接打印
     *
     * @auther zhoucx
     * @since NCHR V5.5
     */
    private void onDirectPrint() {
    int rowCount = getBspWaData().getTable().getRowCount();
    if (rowCount == 0) {
        showErrorMessage(NCLangRes.getInstance().getStrByID("60131001", "UPP60131001-000058")/*
                                                     * @res
                                                     * "没有可打印的数据！"
                                                     */);
        return;
    }

    boolean isPrintTotalRow = PrintManagerForWA.isPrintTotalRow(this);

    PrintDirectEntry print = PrintManagerForWA.getDirectPrinter(getBspWaData(), getBspWaData().getTableModel().getBodyItems(), isPrintTotalRow);
    java.awt.Font font = new java.awt.Font("dialog", java.awt.Font.BOLD, 30);
    print.setTitle(NCLangRes.getInstance().getStrByID("60131004", "UPP60131004-000152")/*
                                                 * @res
                                                 * "薪资发放表"
                                                 */);
    print.setTitleFont(font);
    print.preview();
    }

    /**
     * 设置排序条件
     *
     * @since NCHRV5.5
     */
    private void onSetOrderStr() {
    if (getUIRefWaClass().getRefPK() == null) {
        showErrorMessage(nc.ui.ml.NCLangRes.getInstance().getStrByID("60131004", "UPP60131004-000333")/*
                                                         * @res
                                                         * "请选择薪资类别！"
                                                         */);
        return;
    }

    this.setSortItems(getSortDialog());
    if (getSortDialog().showModal() == UIDialog.ID_OK) {
        setOrderStr(getSortDialog().getOrderStr());
    }
    }

    /**
     * 获得发放明细对比Dialog
     *
     * @since NCHRV5.5
     */
    private void onViewConstrast() {
    try {
        if (getUIRefWaClass().getRefPK() != null) {
        String curClassid = waGlobal.getWaClassPK();
        String curWaYear = waGlobal.getWaYear();
        String curWaPeriod = waGlobal.getWaPeriod();
        String pk_corp = Global.getCorpPK();

        contrastDlg = null;

        getWaConstrastDlg().setClassid(curClassid);
        getWaConstrastDlg().setYear(curWaYear);
        getWaConstrastDlg().setPeriod(curWaPeriod);
        getWaConstrastDlg().setPkCorp(pk_corp);

        getWaConstrastDlg().initTable();
        // getWaConstrastDlg().setViewData();
        String whereSql = getWhereSqlFromDlg();
        // 取道非具体项时跳出
        if ("noContin".equals(whereSql)) {
            return;
        } else if (whereSql != null && !"noContin".equals(whereSql)) {
            String normalSQl = getNormalWhere().getNomalSql();

            whereSql += deptpower + psnclpower;
            whereSql += normalSQl;
        } else {
            String normalSQl = getNormalWhere().getNomalSql();
            whereSql = deptpower + psnclpower;
            whereSql += normalSQl;
        }
        // 如果存在条件则需要加上
        if (whereSql != null && whereSql.trim().length() > 0) {
            getWaConstrastDlg().setViewData(whereSql);
        } else {
            getWaConstrastDlg().setViewData();
        }
        getWaConstrastDlg().showModal();
        }
    } catch (Exception e) {
        Logger.error(e.getMessage(), e);
    }
    }

    private WaContrastDlg getWaConstrastDlg() {
    if (contrastDlg == null) {
        contrastDlg = new WaContrastDlg(this);
        contrastDlg.setName("contrastDlg");
    }
    return contrastDlg;
    }

    /**
     *
     */
    public void onDispChg() {
    if (m_boSave.isEnabled()) {
        if (showOkCancelMessage(NCLangRes.getInstance().getStrByID("60130119", "UPP60130119-000153")/* "数据已经发生变化，是否保存?" */) == UIDialog.ID_OK) {
        onBoSave();
        }
    }

    try {
        if (itemSetDlg == null) {
        itemSetDlg = new ItemSetDlg(this);
        }

        ClassitemVO[] items = null;
        if (ibo.getWaItems_user() != null) {
        int size = ibo.getWaItems_user().length;
        items = new ClassitemVO[size];

        for (int i = 0; i < size; i++) {
            items[i] = (ClassitemVO) ibo.getWaItems_user()[i].clone();
        }
        }
        itemSetDlg.setValueObject(items);
    } catch (Exception e) {
        handleException(e);
        return;
    }
    if (itemSetDlg.showModal() == UIDialog.ID_OK) {
        ibo.setWaItems_user(itemSetDlg.getSelectedVOs());
        getBspWaData().setTableModel(getItemSortFac().transBillModel(billmodel, itemSetDlg.getSelectedVOs()));
        addListener();
        setData();
    }
    // 不可以排序
    getBspWaData().getTable().setSortEnabled(false);

    }

    /**
     * 此处插入方法说明。 创建日期：(2001-6-18 16:38:37)
     */
    public boolean onSave() throws Exception {
    boolean blLock = false;// 锁定薪资类别
    try {
        if (vecEdit.size() == 0) {
        return true;
        }

        DataVO[] editVO = new DataVO[vecEdit.size()];

        blLock = lockWaclassSuceed();
        if (!blLock) {
        return false;
        }

        /**
         * <p>
         * Modified by: zhangg on 2007-9-7
         * <p>
         * Reason:
         * 两个用户同时编辑同一类别的不同薪资项目，用户甲有A项目的编辑权限和B项目的查看权限，用户乙有B项目的编辑权限和A项目的查看权限
         * ，用户甲编辑后保存时B项目的值也会被改变。 修改 pk--> (ItemName, ItemType, ItemValue)数组
         */

        Map<String, GeneralVO[]> editRecordMap = new HashMap<String, GeneralVO[]>();
        for (int i = 0; i < vecEdit.size(); i++) {
        int row = Integer.parseInt(vecEdit.elementAt(i).toString());

        editVO[i] = (DataVO) waData[row].clone();
        String pk_wa_data = editVO[i].getPk_wa_data();

        editVO[i].setRecaculateFlag(new Integer(0));
        ArrayList<GeneralVO> arrayGeneralVOs = new ArrayList<GeneralVO>();

        for (int j = 0; j < getEditableItem().length; j++) {
            BillItem[] billItems = getBspWaData().getTableModel().getBodyItems();
            for (BillItem item : billItems) {
            if (getEditableItem()[j].equalsIgnoreCase(item.getKey())) {
                Object value = getBspWaData().getTableModel().getValueAt(row, getEditableItem()[j]);
                if (value == null) {
                value = "";
                }
                editVO[i].setAttributeValue(getEditableItem()[j], value);

                GeneralVO temp = new GeneralVO();
                temp.setAttributeValue("ItemName", getEditableItem()[j]);
                temp.setAttributeValue("ItemType", getEditableType()[j]);
                temp.setAttributeValue("ItemValue", value.toString());
                arrayGeneralVOs.add(temp);
                break;
            }
            }
        }

        // 因为用户可以修改的项目(EditableItem)不一定全部存在表体的 billItems
        // 中.结果导致editColumGeneralVOs中出现null. 需要将null过滤调 >>xuanlt
        // 2007-11-07.
        GeneralVO[] editColumGeneralVOs = new GeneralVO[arrayGeneralVOs.size()];
        editRecordMap.put(pk_wa_data, arrayGeneralVOs.toArray(editColumGeneralVOs));
        getBspWaData().getTableModel().setBodyRowVO(editVO[i], row);
        }

        boolean ok = chechState(ibo.updateArray(editVO, editRecordMap));

        if (ok) {
        for (int i = 0; i < vecEdit.size(); i++) {
            int row = Integer.parseInt(vecEdit.elementAt(i).toString());

            waData[row] = editVO[i];

        }
        }
        return ok;

    } catch (Exception ex) {
        ex.printStackTrace();
        throw ex;
    } finally {
        if (blLock) {
        try {
            freeWaclass();
        } catch (Exception e) {
        }
        }
        freeBm();
    }
    }

    /**
     * 此处插入方法说明。 创建日期：(2001-6-18 16:38:37)
     */
    public boolean onSaveCardEdit() {
    boolean blLock = false;// 锁定薪资类别
    boolean onSuccess = false;

    BillItem[] billItems = getUpdateBillCardPanel().getHeadItems();
    for (BillItem billItem : billItems) {

        selDataVO.setAttributeValue(billItem.getKey(), billItem.getValueObject());
    }

    selDataVO.setRecaculateFlag(new Integer(0));

    try {
        blLock = lockWaclassSuceed();
        if (!blLock) {
        return onSuccess;
        }
        // ibo.updateArray(new DataVO[]{selDataVO});
        boolean ok = chechState(ibo.updateArray(new DataVO[] { selDataVO }, null));

        if (ok) {
        hasSaved = true;
        waData[selRow] = selDataVO;
        // setData();
        showHintMessage(nc.ui.ml.NCLangRes.getInstance().getStrByID("60131004", "UPP60131004-000207")/*
                                                         * @res
                                                         * "修改成功!"
                                                         */);
        }
        onSuccess = true;
    } catch (Exception ex) {
        showErrorMessage(nc.ui.ml.NCLangRes.getInstance().getStrByID("60131004", "UPP60131004-000214")/*
                                                         * @res
                                                         * "修改失败，请检查所修改的数据是否正确，或刷新以后再试！"
                                                         */);
        onSuccess = false;
    } finally {
        if (blLock) {
        try {
            freeWaclass();
        } catch (Exception e) {
        }
        }
        freeBm();
    }
    return onSuccess;

    }

    /**
     * 此处插入方法描述。 创建日期：(2002-8-13 10:07:43)
     *
     * @return java.lang.String[]
     */
    public String[] printTable(String[] values, String itemExpress) {

    // values = new String[waData.length + 1];
    values = new String[waData.length];
    if (itemExpress.equals("psnname") || itemExpress.equals("m_psnName")) {
        if (waData != null && waData.length > 0) {

        for (int i = 0; i < waData.length; i++) {
            if (waData[i].getPsnName() != null) {
            values[i] = waData[i].getPsnName();
            } else {
            values[i] = "";
            }
        }
        }
        // values[waData.length] = "";
    } else if (itemExpress.equals("payyear")) { // 发放工资年
        String payyear = ibo.getWaYear();
        if (payyear == null) {
        payyear = "";
        }
        for (int i = 0; i < waData.length; i++) {
        values[i] = payyear;
        }
        // values[waData.length] = "";

    } else if (itemExpress.equals("payproid")) { // 发放工资期间
        String payperiod = ibo.getWaPeriod();
        if (payperiod == null) {
        payperiod = "";
        }
        for (int i = 0; i < waData.length; i++) {
        values[i] = payperiod;
        }
        // values[waData.length] = "";
    } else if (itemExpress.equals("paydate")) { // 发放日期
        values = new String[1];
        values[0] = getUIRefPayDay().getText();
    } else if (itemExpress.equals("paymemo")) { // 发放说明
        values = new String[1];
        values[0] = getUITaComment().getText();
    } else if (itemExpress.equals("deptname") || itemExpress.equals("m_deptName")) {
        if (waData != null && waData.length > 0) {

        for (int i = 0; i < waData.length; i++) {
            if (waData[i].getDeptName() != null) {
            values[i] = waData[i].getDeptName();
            } else {
            values[i] = "";
            }
        }
        }
        // values[waData.length] = "";
    } else if (itemExpress.equals("nestdeptname")) {
        if (waData != null && waData.length > 0) {

        for (int i = 0; i < waData.length; i++) {
            if (waData[i].getNestDeptName() != null) {
            values[i] = waData[i].getNestDeptName();
            } else {
            values[i] = "";
            }
        }
        }
        // values[waData.length] = "";
    } else if (itemExpress.equals("psncode") || itemExpress.equals("m_psnCode")) {

        if (waData != null && waData.length > 0) {

        for (int i = 0; i < waData.length; i++) {
            if (waData[i].getPsnCode() != null) {
            values[i] = waData[i].getPsnCode();
            } else {
            values[i] = "";
            }
        }
        }
        // values[waData.length] =
        // nc.ui.ml.NCLangRes.getInstance().getStrByID("common",
        // "UC000-0001146")/* * @res "合计" */;
    } else if (itemExpress.equals("psnclasscode") || itemExpress.equals("m_psnclIdName")) {
        if (waData != null && waData.length > 0) {
        for (int i = 0; i < waData.length; i++) {
            if (waData[i].getPsnclIdName() != null) {
            values[i] = waData[i].getPsnclIdName();
            } else {
            values[i] = "";
            }
        }
        }
        // values[waData.length] = "";

    } else if (itemExpress.equals("acccode")) {
        if (waData != null && waData.length > 0) {
        for (int i = 0; i < waData.length; i++) {
            if (waData[i].getAccCode() != null) {
            values[i] = waData[i].getAccCode();
            } else {
            values[i] = "";
            }
        }
        }
        // values[waData.length] = "";

    } else if (itemExpress.equals("nestpsnclasscode")) {
        if (waData != null && waData.length > 0) {
        for (int i = 0; i < waData.length; i++) {
            if (waData[i].getNestPsnclIdName() != null) {
            values[i] = waData[i].getNestPsnclIdName();
            } else {
            values[i] = "";
            }
        }
        }
        // values[waData.length] = "";

    } else if (itemExpress.equals("jobname") || itemExpress.equals("m_omJobName")) {
        if (waData != null && waData.length > 0) {
        for (int i = 0; i < waData.length; i++) {
            if (waData[i].getOmJobName() != null) {
            values[i] = waData[i].getOmJobName();
            } else {
            values[i] = "";
            }
        }
        }
        // values[waData.length] = "";

    } else if (itemExpress.equals("nestjobname")) {
        if (waData != null && waData.length > 0) {
        for (int i = 0; i < waData.length; i++) {
            if (waData[i].getNestOmJobName() != null) {
            values[i] = waData[i].getNestOmJobName();
            } else {
            values[i] = "";
            }
        }
        }
        // values[waData.length] = "";

    } else if (itemExpress.equals("id")) {
        if (waData != null && waData.length > 0) {

        for (int i = 0; i < waData.length; i++) {
            if (waData[i].getId() != null) {
            values[i] = waData[i].getId();
            } else {
            values[i] = "";
            }
        }
        }
        // values[waData.length] = "";
    } else if (itemExpress.equals("ssnum")) {
        if (waData != null && waData.length > 0) {

        for (int i = 0; i < waData.length; i++) {
            if (waData[i].getSsnum() != null && !waData[i].getSsnum().equals("0.00")) {
            values[i] = waData[i].getSsnum();
            } else if (waData[i].getSsnum() != null && Validator.isZero(new UFDouble(waData[i].getSsnum()))) {
            // 根据"打印零值"的参数设置.注意小数位数
            if (printZero()) {
                values[i] = waData[i].getSsnum();
            } else {
                values[i] = "";
            }

            } else {
            values[i] = "";
            }
        }
        }
        // values[waData.length] = "";
    } else if (itemExpress.equals("irecaculateflag")) {
        if (waData != null && waData.length > 0) {

        for (int i = 0; i < waData.length; i++) {
            if (waData[i].getRecaculateFlag() != null) {
            if (waData[i].getRecaculateFlag().intValue() == 0) {
                values[i] = "N";
            } else {
                values[i] = "Y";
            }
            } else {
            values[i] = "N";
            }
        }
        }
        // values[waData.length] = "";
    } else if (itemExpress.equals("icheckflag")) {
        if (waData != null && waData.length > 0) {

        for (int i = 0; i < waData.length; i++) {
            if (waData[i].getCheckflag() != null) {
            if (waData[i].getCheckflag().booleanValue()) {
                values[i] = "Y";
            } else {
                values[i] = "N";
            }
            } else {
            values[i] = "N";
            }
        }
        }
        // values[waData.length] = "";
    } else if (itemExpress.equals("timecardid")) {
        if (waData != null && waData.length > 0) {

        for (int i = 0; i < waData.length; i++) {
            if (waData[i].getTimecardid() != null) {
            values[i] = waData[i].getTimecardid();
            } else {
            values[i] = "";
            }
        }
        }
        // values[waData.length] = "";
    } else if (itemExpress.equals("bankname")) { // 此处增加银行名称
        if (waData != null && waData.length > 0) {

        for (int i = 0; i < waData.length; i++) {
            if (waData[i].getBankName() != null) {
            values[i] = waData[i].getBankName();
            } else {
            values[i] = "";
            }
        }
        }
        // values[waData.length] = "";
    } else if (itemExpress.equals("waclassname")){
		values = new String[1];
		values[0] = getUIRefWaClass().getRefName();
		
	} else {
        /*
         * FIXBUG：200604251704588801 去掉合计，使用打印模板的自定义变量---本页小计函数_subtotal_(
         * )进行本页小计
         */
        double sum = new nc.vo.pub.lang.UFDouble("0.00").doubleValue();
        // 先不认为是数字
        if (waData != null && waData.length > 0) {
        int a = -1;
        // 先找到是哪个薪资项目
        if (a == -1) {
            for (int j = 0; j < waData[0].getAppendNames().length; j++) {
            if (itemExpress.equals(waData[0].getAppendNames()[j])) {
                a = j;
                break;
            }
            }
        }

        BillItem item = getBspWaData().getTableModel().getItemByKey(itemExpress);

        // 确定位数
        int nDigitNum = 2;
        if (item != null) {
            nDigitNum = item.getDecimalDigits();
            if (nDigitNum < 0) {
            nDigitNum = -nDigitNum;
            }
        } else {
            if (a == -1) {
            return null;
            }
        }

        for (int i = 0; i < waData.length; i++) {
            if (a != -1) {
            if (waData[i].getAppendNames()[a] != null && waData[i].getAppendValues()[a] != null) {
                if (waData[0].getAppendType()[a].equals("1")) {
                // 数字型
                values[i] = (new nc.vo.pub.lang.UFDouble(waData[i].getAppendValues()[a].toString()).setScale(nDigitNum, 4)).toString();

                if (Validator.isZero(new UFDouble(values[i].toString()))) {
                    if (printZero()) {
                    String ufString = values[i].toString().trim();// 找到要格式化的数值
                    values[i] = ThMarker.getInstance().formatValue(ufString, nDigitNum);
                    } else {
                    values[i] = "";
                    }
                    continue;
                }

                if (values[i].trim().length() > 0) {
                    String ufString = values[i].trim();// 找到要格式化的数值
                    values[i] = ThMarker.getInstance().formatValue(ufString, nDigitNum);
                }
                sum += new nc.vo.pub.lang.UFDouble(values[i]).doubleValue();
                } else {
                // 字符型
                values[i] = waData[i].getAppendValues()[a].toString();
                }
            } else {
                values[i] = "";
            }
            } else {
            values[i] = "";
            }
        } // end for
        // if (isNum){/*FIXBUG：200604251704588801
        // 去掉合计，使用打印模板的自定义变量---本页小计函数_subtotal_( )进行本页小计 */
        // values[waData.length] = new
        // nc.vo.pub.lang.UFDouble(sum).setScale(nDigitNum,
        // 4).toString();
        //
        //
        // if(values[waData.length]!=null &&
        // values[waData.length].trim().length() >0){
        // String ufString = values[waData.length].trim();//找到要格式化的数值
        //
        // values[waData.length] =
        // ThMarker.getInstance().formatValue(ufString,nDigitNum);
        // }
        // else
        // values[waData.length] = "";
        // }

        }
    }

    return values;
    }

    /**
     * 参数设置中是否打印零值
     *
     * @return
     * @author xuanlt 2007-08-20
     */
    private boolean printZero() {
    try {
        if (!getFlag) {
        printZero = WADelegator.getWaParValue().getParaBoolean(Global.getCorpPK(), "WA-PRINTZERO").booleanValue();
        getFlag = true;
        }
        return printZero;

    } catch (Exception e) {
        e.printStackTrace();
        return false;
    }
    }

    /**
     * 处理业务状态标志。
     */
    /*
     * private void resetCheckState() throws Exception {
     *
     * ibo.resetCheckState(deptpower + psnclpower);
     * setCheckState(ibo.getCheckState());
     *
     * if (getCheckState() == WaClassStateHelper.NO_PERIOD_DATA_FOUND ||
     * getCheckState() == WaClassStateHelper.NO_WA_DATA_FOUND) { bcontrol = new
     * StateControl();// 按钮全灰 return; } if (getCheckState() ==
     * WaClassStateHelper.NO_WA_DATA_FOUND) {// 该类别下没人添加人员或用户没有查看人员的权限 if
     * (!isHuiZong) { // 非汇总类别 bcontrol = new StateControl();// 按钮全灰
     * getBspWaData().getTableModel().setBodyDataVO(null); throw new
     * BusinessException(nc.ui.ml.NCLangRes.getInstance().getStrByID("60131004",
     * "UPP60131004-000215") 该类别下没有添加人员或用户没有查看人员的权限！ ); } }
     *
     * if (getCheckState() == WaClassStateHelper.CLASS_RECHECKED_WITHOUT_PAY ||
     * getCheckState() == WaClassStateHelper.CLASS_MONTH_END || getCheckState() ==
     * WaClassStateHelper.CLASS_ALL_PAY) { // 已经不能再修改或审核数据
     * bcontrol.setBRecal(false); bcontrol.setBCheck(false); if (getCheckState() ==
     * WaClassStateHelper.CLASS_RECHECKED_WITHOUT_PAY) { if (needReCheck) {
     * bcontrol.setBCancelCheck(false); bcontrol.setBCancelReCheck(true); } else {
     * bcontrol.setBCancelCheck(true); bcontrol.setBCancelReCheck(false); }
     * bcontrol.setBRecheck(false); bcontrol.setBPayoff(true);
     * bcontrol.setBCancelPayoff(false); } else { // 已经发放
     * bcontrol.setBCancelCheck(false); bcontrol.setBCancelReCheck(false);
     * bcontrol.setBRecheck(false); bcontrol.setBPayoff(false); if
     * (getCheckState() == WaClassStateHelper.CLASS_ALL_PAY) {
     * bcontrol.setBCancelPayoff(true); } else { // 已经结账
     * bcontrol.setBCancelPayoff(false); } } return; } // 以下是等于-1、0、5、6的情况 //
     * 先处理部门权限 String deptstr = ""; if ((deptpower != null &&
     * deptpower.trim().length() > 0) || (psnclpower != null &&
     * psnclpower.trim().length() > 0)) { deptstr = deptpower + psnclpower; //
     * 在有部门权限情况下，需要重新找状态
     *
     * if (getCheckState() ==
     * WaClassStateHelper.SELECTED_WA_DATA_ALL_RECACULATED) {//
     * 若范围内计算完，也认为是计算完，状态变为0
     * setCheckState(WaClassStateHelper.CLASS_RECACULATED_WITHOUT_CHECK); }
     *
     * if (getCheckState() == WaClassStateHelper.SELECTED_WA_DATA_ALL_CHECKED) {
     * setCheckState(1); } } if (getCheckState() ==
     * WaClassStateHelper.SELECTED_WA_DATA_ALL_RECACULATED) {
     * bcontrol.setBCheck(true); // 已经有计算完的但还未审核的数据 } else {
     * bcontrol.setBCheck(false); } bcontrol.setBCancelReCheck(false);
     * bcontrol.setBCancelPayoff(false);
     *
     * if (getCheckState() == 1) { bcontrol.setBRecal(false);
     * bcontrol.setBCancelCheck(true); if (needReCheck) { // 需要复审
     * bcontrol.setBRecheck(true); bcontrol.setBPayoff(false); } else { // 不需要复审
     * bcontrol.setBRecheck(false); bcontrol.setBPayoff(true); } } else {
     * bcontrol.setBRecheck(false); bcontrol.setBPayoff(false);
     * bcontrol.setBRecal(true);// 未全审完随时可以计算 if (getCheckState() >= 5) {
     * bcontrol.setBCancelCheck(true);// 已经有部分审核，复审可以亮 } else {
     * bcontrol.setBCancelCheck(false); } } }
     *
     * private int getCheckStateWithPower() throws BusinessException {
     * checkWaClassSealState(); ibo.resetCheckState(deptpower + psnclpower); int
     * tempState = ibo.getCheckState(); // 以下是等于-1、0、5、6的情况 // 先处理部门权限 if
     * ((deptpower != null && deptpower.trim().length() > 0) || (psnclpower !=
     * null && psnclpower.trim().length() > 0)) { // 在有部门权限情况下，需要重新找状态 if
     * (tempState == -1) { // 若范围内计算完，也认为是计算完，状态变为0 if
     * (!WADelegator.getRecaData().havePsnNotRecacu(waGlobal.getWaClassPK(),
     * ibo.getWaYear(), ibo.getWaPeriod(), deptpower + psnclpower)) { tempState =
     * 0; } else { if
     * (WADelegator.getRecaData().havePsnChecked(waGlobal.getWaClassPK(),
     * ibo.getWaYear(), ibo.getWaPeriod(), deptpower + psnclpower)) {
     *
     * tempState = 5;// 未计算完、部分审核过 } } // 范围内已经有计算完的但还未审核的数据 } if (tempState ==
     * 0) { // 若范围内审核完，状态变为1 if
     * (!WADelegator.getRecaData().havePsnNotCheck(waGlobal.getWaClassPK(),
     * ibo.getWaYear(), ibo.getWaPeriod(), deptpower + psnclpower)) { tempState =
     * 1; } else if
     * (WADelegator.getRecaData().havePsnChecked(waGlobal.getWaClassPK(),
     * ibo.getWaYear(), ibo.getWaPeriod(), deptpower + psnclpower)) { tempState =
     * 6;// 计算完且部分审核过 } } }
     *
     * return tempState; }
     */

    /**
     * 按钮业务状态 创建日期：(2001-6-21 13:15:26)
     */
    /*
     * public void setButtonBsState() { getUIRefPayDay().setText("");
     * getUITaComment().setText(""); getUIRefPayDay().setEnabled(true);
     * getUITaComment().setEnabled(true);
     * m_boReCal.setEnabled(bcontrol.isBRecal());
     * m_boReplace.setEnabled(bcontrol.isBRecal()); if (bcontrol.isBRecal()) {
     * m_boReTotal.setEnabled(isHuiZong); m_bopsnCompute.setEnabled(true); }
     * else { m_boReTotal.setEnabled(false); }
     * m_boCheck.setEnabled(bcontrol.isBCheck());
     * m_boCancelCheck.setEnabled(bcontrol.isBCancelCheck());
     * m_boReCheck.setEnabled(bcontrol.isBRecheck());
     * m_boCancelReCheck.setEnabled(bcontrol.isBCancelReCheck()); //
     * 当做V231时把该段代码放开 m_boPayOff.setEnabled(bcontrol.isBPayoff());
     * m_boCancelPayOff.setEnabled(bcontrol.isBCancelPayoff()); updateButtons(); }
     */

    /**
     * 按钮业务状态 创建日期：(2001-6-21 13:15:26)
     */
    public void setButtonExceptionState() {
    ButtonObject[] buttonObjects = getButtons();
    if (buttonObjects != null && buttonObjects.length > 0) {
        for (ButtonObject buttonObject : buttonObjects) {
        buttonObject.setEnabled(false);
        }
    }
    updateButtons();
    }

    public void setButtonQueryState() {
    if (getBspWaData().getTable() == null || getBspWaData().getTable().getRowCount() < 1) {
        m_boUpdate.setEnabled(false);
        // m_boWatch4MulDeptItem.setEnabled(false);
        return;
    }
    // 允许替换就允许修改
    m_boUpdate.setEnabled(m_boReplace.isEnabled());
    if (selRow < 0) {
        m_boUpdate.setEnabled(false);
        // m_boWatch4MulDeptItem.setEnabled(false);
    } else {
        if (m_boUpdate.isEnabled()) {
        if (selDataVO.getCheckflag().booleanValue()) {
            m_boUpdate.setEnabled(false);
        }
        }
        // 该行已经审核
        boolean b = m_boUpdate.isEnabled();
        if (getEditableItem() != null && getEditableItem().length > 0) {
        for (String element : editableItem) {
            getBspWaData().getTableModel().setCellEditable(selRow, element, b);
        }
        }
    }
    updateButtons();
    }

    /**
     * 按钮查询状态 创建日期：(2001-6-21 13:15:26)
     */
    public void setButtonUpdateState() {
    m_boFirst.setEnabled(true);
    m_boPre.setEnabled(true);
    m_boNext.setEnabled(true);
    m_boLast.setEnabled(true);

    if (selRow == 0) {
        m_boFirst.setEnabled(false);
        m_boPre.setEnabled(false);
    }
    if (selRow == waData.length - 1) {
        m_boLast.setEnabled(false);
        m_boNext.setEnabled(false);
    }
    m_boUpdateOk.setEnabled(!selDataVO.getCheckflag().booleanValue());// 审核过的数据不能存
    updateButtons();
    }

    /**
     * 此处插入方法说明。 创建日期：(2001-6-25 15:08:23)
     */
    public void setCardData() {

    hasSaved = false;

    getUpdateBillCardPanel().getBillData().setHeaderValueVO(selDataVO);
    // 中广核二期项目在页面下的状态栏加入员工编号、姓名、部门等内容显示处理；
    setHintMsg(selDataVO.getAttributeValue("m_psnCode").toString(), selDataVO.getAttributeValue("m_psnName").toString(), selDataVO.getAttributeValue("m_deptName").toString());

    }

    /**
     * 此处插入方法说明。 创建日期：(2001-6-22 13:36:04)
     */
    public void setData() {

    delEditor();
    getBspWaData().getTableModel().setBodyDataVO(waData);
    if (waGlobal.getWaClassPK() == null) {
        return;
    }

    try {
        if (ibo.getARecaVO().getWaPeriodvo().getIpayoffflag() == 1) {// 已经发放
        getUIRefPayDay().setPK(ibo.getARecaVO().getWaPeriodvo().getCpaydate());
        getUIRefPayDay().setValue(ibo.getARecaVO().getWaPeriodvo().getCpaydate());
        getUITaComment().setText(ibo.getARecaVO().getWaPeriodvo().getVpaycomment());
        }
    } catch (Exception e) {
        reportException(e);
        showErrorMessage(e.getMessage());
    }
    getBspWaData().getTable().repaint();
    TableColResize.reSizeTable(getBspWaData());
    // 保证设置完成以后高亮显示
    highlightDisplay(getBspWaData().getTable());
    vecEdit = new Vector<String>();
    selRow = -1;
    selDataVO = null;
    setColor();
    }

    /**
     * 此处插入方法描述。 创建日期：(2003-9-28 22:00:41)
     *
     * @param newDisplayNest
     *                boolean
     */
    public void setDisplayNest(boolean newDisplayNest) {
    displayNest = newDisplayNest;
    }

    /**
     * 此处插入方法说明。 创建日期：(2001-6-23 19:54:03)
     *
     * @param newEditableItem
     *                java.lang.String[]
     */
    public void setEditableItem(java.lang.String[] newEditableItem) {
    editableItem = newEditableItem;
    }

    /**
     * 此处插入方法说明。 创建日期：(2001-6-23 19:50:06)
     *
     * @param newEditableName
     *                java.lang.String[]
     */
    public void setEditableName(java.lang.String[] newEditableName) {
    editableName = newEditableName;
    }

    /**
     * 此处插入方法说明。 创建日期：(2001-6-23 19:50:06)
     *
     * @param newEditableName
     *                java.lang.String[]
     */
    public void setEditableType(java.lang.String[] newEditableType) {
    editableType = newEditableType;
    }

    /**
     * 此处插入方法说明。 创建日期：(2001-6-23 20:04:07)
     *
     * @param newItemCodes
     *                java.lang.String[]
     */
    public void setItemCodes(java.lang.String[] newItemCodes) {
    itemCodes = newItemCodes;
    }

    /**
     * 此处插入方法说明。 创建日期：(2001-6-23 20:02:51)
     *
     * @param newItemNames
     *                java.lang.String[]
     */
    public void setItemNames(java.lang.String[] newItemNames) {
    itemNames = newItemNames;
    }

    /**
     * 此处插入方法说明。 创建日期：(2001-6-23 20:05:15)
     *
     * @param newItemTypes
     *                java.lang.String[]
     */
    public void setItemTypes(java.lang.String[] newItemTypes) {
    itemTypes = newItemTypes;
    }

    /**
     * 函数的功能、用途、对属性的更改，以及函数执行前后对象的状态。
     *
     * @param 参数说明
     * @return 返回值
     * @exception 异常描述
     * @see 需要参见的其它内容
     * @since 从类的那一个版本，此方法被添加进来。（可选）
     *
     *
     *
     * @param newOrderDlg
     *                nc.ui.wa.wa_009.ItemOrderDlgUI
     */
    public void setOrderDlg(nc.ui.hr.pub.ItemOrderDlgUI newOrderDlg) {
    }

    /**
     * 函数的功能、用途、对属性的更改，以及函数执行前后对象的状态。
     *
     * @param 参数说明
     * @return 返回值
     * @exception 异常描述
     * @see 需要参见的其它内容
     * @since 从类的那一个版本，此方法被添加进来。（可选）
     *
     *
     *
     * @param newOrderStr
     *                java.lang.String
     */
    public void setOrderStr(java.lang.String newOrderStr) {
    orderStr = newOrderStr;
    }

    /**
     * 此处插入方法说明。 创建日期：(2001-11-1 18:58:06)
     *
     * @param newPrecisionForUFDouble
     *                int
     */
    public void setPrecisionForUFDouble(int newPrecisionForUFDouble) {
    precisionForUFDouble = newPrecisionForUFDouble;
    }

    /**
     * 此处插入方法说明。 创建日期：(2001-10-15 14:12:53)
     *
     * @param newRecacuDlg
     *                nc.ui.wa.wa_009.RecacuDlg
     */
    public void setRecacuDlg(RecacuDlg newRecacuDlg) {
    recacuDlg = newRecacuDlg;
    }

    /**
     * 此处插入方法说明。 创建日期：(2001-9-27 13:15:11)
     *
     * @param newRefreshing
     *                boolean
     */
    public void setRefreshing(boolean newRefreshing) {
    refreshing = newRefreshing;
    }

    /**
     * 此处插入方法说明。 创建日期：(2001-6-23 21:23:07)
     *
     * @param newReplaceDlg
     *                nc.ui.wa.wa_009.ReplaceDlg
     */
    public void setReplaceDlg(ReplaceDlg newReplaceDlg) {
    replaceDlg = newReplaceDlg;
    }

    /**
     * 此处插入方法说明。 创建日期：(2002-6-20 9:25:48)
     *
     * @param newSaBodyColDecimal
     *                java.lang.Integer[]
     */
    public void setSaBodyColDecimal(java.lang.Integer[] newSaBodyColDecimal) {
    saBodyColDecimal = newSaBodyColDecimal;
    }

    /**
     * 此处插入方法说明。 创建日期：(2002-6-20 9:26:19)
     *
     * @param newSaBodyColWidth
     *                java.lang.Integer
     */
    public void setSaBodyColWidth(java.lang.Integer[] newSaBodyColWidth) {
    saBodyColWidth = newSaBodyColWidth;
    }

    /**
     * 此处插入方法说明。 创建日期：(2001-6-22 13:24:48)
     *
     * @param b
     *                boolean
     */
    public void setSaveAndCancel(boolean b) {
    m_boSave.setEnabled(b);
    m_boCancel.setEnabled(b);
    updateButton(m_boSave);
    updateButton(m_boCancel);
    }

    /**
     * 此处插入方法描述。 创建日期：(2003-9-16 20:35:54)
     *
     * @param newSharePeriodSetDlg
     *                nc.ui.wa.wa_009.SharePeriodSetDlg
     */
    public void setSharePeriodSetDlg(SharePeriodSetDlg newSharePeriodSetDlg) {
    sharePeriodSetDlg = newSharePeriodSetDlg;
    }

    /**
     * 此处插入方法说明。 创建日期：(2001-11-27 13:01:55)
     *
     * @param newStDigitItemRefWithoutCurr
     *                java.lang.String[]
     */
    public void setStDigitItemRefWithoutCurr(java.lang.String[] newStDigitItemRefWithoutCurr) {
    stDigitItemRefWithoutCurr = newStDigitItemRefWithoutCurr;
    }

    /**
     * 此处插入方法说明。 创建日期：(2002-6-20 8:43:27)
     *
     * @param newWaItemDecimal
     *                int[]
     */
    public void setWaItemDecimal(int[] newWaItemDecimal) {
    waItemDecimal = newWaItemDecimal;
    }

    /**
     * 此处插入方法说明。 创建日期：(2002-6-20 8:42:57)
     *
     * @param newWaItemWidth
     *                int[]
     */
    public void setWaItemWidth(int[] newWaItemWidth) {
    waItemWidth = newWaItemWidth;
    }

    /**
     * 创建日期：(2003-11-14 11:05:43)
     */
    public void showAlertInfo(String[] files) {
    try {
        // nc.ui.pub.MainFrame.initAlertInfo()弹出.html信息的方法.
        if (files != null) {
        for (String file : files) {

            // 5.0预警显示HTML文件的方法
            String serverURL = ClientAssistant.getServerURL();
            java.net.URL url = new java.net.URL(serverURL + "NCFindWeb?service=prealert&filename="
            // + "PreAlart/Messages/" + files[i]); //这个即为文件的名字(不包含路径)
                + file);
            ClientAssistant.showDocument(url, "_blank");

            // 3.5预警显示HTML文件方法
            // java.net.URL url = new
            // java.net.URL(nc.ui.sm.cmenu.Desktop
            // .getApplet().getServerURL()
            // + files[i]);
            // nc.ui.sm.cmenu.Desktop.getApplet().getAppletContext()
            // .showDocument(url, "_blank");
        }
        }
    } catch (Exception e) {
        reportException(e);
        showErrorMessage(nc.ui.ml.NCLangRes.getInstance().getStrByID("60131004", "UPP60131004-000216")/*
                                                         * @res
                                                         * "弹出预警信息时出错,请刷新后再试!"
                                                         */);
    }
    }

    /**
     * 显示初始化错误 创建日期：(2001-9-4 12:47:35)
     *
     * @param exceptionMsg
     *                java.lang.String
     */
    public void showException(String exceptionMsg) {
    haveException = true;
    nc.ui.pub.beans.MessageDialog.showWarningDlg(getClientEnvironment().getDesktopApplet(), nc.ui.ml.NCLangRes.getInstance().getStrByID("60131004", "UPP60131004-000217")/*
                                                                                         * @res
                                                                                         * "警告"
                                                                                         */, exceptionMsg);
    }

    /**
     * 此处插入方法说明。 创建日期：(2001-6-8 10:12:14)
     *
     * @param ev
     *                javax.swing.event.ListSelectionEvent
     * @exception java.lang.Exception
     *                    异常说明。
     */
    public void valueChanged(ListSelectionEvent ev) {
    if (!ev.getValueIsAdjusting()) {
        if (loading) {
        return;
        }

        selRow = getBspWaData().getTable().getSelectedRow();

        if (selRow < 0) {
        selRow = getBspWaData().getRowNOTable().getSelectedRow();

        if (selRow < 0) {
            selRow = getBspWaData().getFixColTable().getSelectedRow();
        }
        }
        if (selRow >= 0) {
        selDataVO = (DataVO) waData[selRow].clone();
        } else {
        selDataVO = null;
        }

        setButtonQueryState();
    }
    }

    /**
     * 关闭界面。 创建日期：(2005-6-15 19:45:21)
     *
     * @return boolean
     */
    @Override
    public boolean onClosing() {
    int iSave = 0;
    boolean isClosing = true;
    javax.swing.table.TableCellEditor editor = getBspWaData().getTable().getCellEditor();
    if (editor != null) {
        editor.stopCellEditing();
    }

    // if(1!=1) {
    if (updateinit == 1) {
        iSave = showYesNoCancelMessage(NCLangRes.getInstance().getStrByID("60130119", "UPP60130119-000153")/* "数据已经发生变化，是否保存?" */);
        switch (iSave) {
        case UIDialog.ID_YES:
        try {
            if (onBoUpdateOk()) {
            isClosing = true;
            } else {
            isClosing = false;

            }
        } catch (Exception ex) {
            showErrorMessage(ex.getMessage());
            isClosing = false;
            ex.printStackTrace();
        }
        onBoReturn();
        break;
        case UIDialog.ID_NO:
        isClosing = true;
        onBoReturn();
        break;
        case UIDialog.ID_CANCEL:
        isClosing = false;

        break;
        }
    } else if (finishinit && m_boSave.isEnabled()) {

        iSave = showYesNoCancelMessage(nc.ui.ml.NCLangRes.getInstance().getStrByID("60130119", "UPP60130119-000153")/* "数据已经发生变化，是否保存?" */);

        switch (iSave) {
        case UIDialog.ID_YES:
        try {
            if (onBoSave()) {
            isClosing = true;
            } else {
            isClosing = false;
            }
        } catch (Exception ex) {
            showErrorMessage(ex.getMessage());
            isClosing = false;
            ex.printStackTrace();
        }
        break;
        case UIDialog.ID_NO:
        isClosing = true;
        break;
        case UIDialog.ID_CANCEL:
        isClosing = false;
        break;
        }

    }
    return isClosing;
    }

    private ItemSortFac getItemSortFac() {
    if (itemsortfac == null) {
        itemsortfac = new ItemSortFac();
        itemsortfac.initModelMap(getBspWaData().getTableModel(), IDisplayConst.NORMALDISP);
    }

    return itemsortfac;
    }

    /**
     * Added by Young 2005-12-29 Start 薪资类别
     */
    /**
     * 返回 UIPanel1 特性值。
     *
     * @return nc.ui.pub.beans.UIPanel
     */
    /* 警告：此方法将重新生成。 */
    private nc.ui.pub.beans.UIPanel getUITopPanel1() {
    if (ivjUITopPanel == null) {
        try {
        ivjUITopPanel = new nc.ui.pub.beans.UIPanel();
        ivjUITopPanel.setName("UITopPanel1");
        ivjUITopPanel.setLayout(null);
        ivjUITopPanel.setPreferredSize(new Dimension(600, 30));
        getUITopPanel1().add(getUIWaClassLabel(), getUIWaClassLabel().getName());
        getUITopPanel1().add(getUIRefWaClass(), getUIRefWaClass().getName());
        } catch (java.lang.Throwable ivjExc) {
        handleException(ivjExc);
        }
    }
    return ivjUITopPanel;
    }

    /**
     * 返回 UILabel1 特性值。
     *
     * @return nc.ui.pub.beans.UILabel
     */
    /* 警告：此方法将重新生成。 */
    private nc.ui.pub.beans.UILabel getUIWaClassLabel() {
    if (ivjUIWaClassLabel == null) {
        try {
        ivjUIWaClassLabel = new nc.ui.pub.beans.UILabel();
        ivjUIWaClassLabel.setName("UIWaClassLabel");
        ivjUIWaClassLabel.setPreferredSize(new java.awt.Dimension(14, 5));
        ivjUIWaClassLabel.setText(nc.ui.ml.NCLangRes.getInstance().getStrByID("60130416", "UPP60130416-000019")/*
                                                             * @res
                                                             * "薪资类别"
                                                             */);
        ivjUIWaClassLabel.setBounds(10, 7, 80, 22);
        } catch (java.lang.Throwable ivjExc) {
        // user code begin {2}
        // user code end
        handleException(ivjExc);
        }
    }
    return ivjUIWaClassLabel;
    }

    /**
     * 返回 UIRefWaClass 特性值。
     *
     * @return nc.ui.pub.beans.UIRefPane
     */
    /* 警告：此方法将重新生成。 */
    private nc.ui.pub.beans.UIRefPane getUIRefWaClass() {
    if (ivjUIRefWaClass == null) {
        try {
        ivjUIRefWaClass = new nc.ui.pub.beans.UIRefPane();
        ivjUIRefWaClass.setName("UIRefWaClass");
        ivjUIRefWaClass.setBounds(84, 7, 200, 22);
        // user code begin {1}
        ivjUIRefWaClass.setReturnCode(true);
        ivjUIRefWaClass.setRefModel(new WaClassRefUIModel());
        // ivjUIRefWaClass.setMaxLength(100);
        ivjUIRefWaClass.setButtonFireEvent(true);
        ivjUIRefWaClass.getRefModel().setMatchPkWithWherePart(true);
        // user code end
        } catch (java.lang.Throwable ivjExc) {
        // user code begin {2}
        // user code end
        handleException(ivjExc);
        }
    }
    return ivjUIRefWaClass;
    }

    /**
     * connEtoC16: (UIRefWaClass.valueChanged.valueChanged(nc.ui.pub.beans.
     * ValueChangedEvent) -->
     * SetWAClassItemUI.UIRefWaClass_ValueChanged(Lnc.ui.pub
     * .beans.ValueChangedEvent;)V)
     *
     * @param arg1
     *                nc.ui.pub.beans.ValueChangedEvent
     */
    /* 警告：此方法将重新生成。 */
    private void connEtoC1(nc.ui.pub.beans.ValueChangedEvent arg1) {
    try {
        // user code begin {1}
        // user code end
        this.uIrefWaClass_ValueChanged(arg1);
        // user code begin {2}
        // user code end
    } catch (java.lang.Throwable ivjExc) {
        // user code begin {3}
        // user code end
        handleException(ivjExc);
    }
    }

    /**
     * 薪资类别参照的变化事件
     */
    public void uIrefWaClass_ValueChanged(nc.ui.pub.beans.ValueChangedEvent event) {
    try {
        if ((this.getUIRefWaClass().getText() != null) && (this.getUIRefWaClass().getText().length() > 0) && (getUIRefWaClass().getRefPK() != null)) {
        if (!haveException) {
            haveException = true;
        }
        selDlg = null;
        updateBillCardPanel = null;
        waGlobal.setWaClassPK(getUIRefWaClass().getRefPK());
        onBoRefresh();
        haveException = false;
        }
    } catch (Exception e) {
        showErrorMessage(nc.ui.ml.NCLangRes.getInstance().getStrByID("60130407", "UPP60130407-000067")/* "系统出现异常，操作失败！" */);
        reportException(e);
    }
    }

    /**
     * Added by Young 2005-12-29 End 薪资类别
     */

    private PeriodVO getCurrClassStateVO() {
    PeriodVO period = null;
    try {
        // 得到薪资期间

        period = ibo.getWaClassVO().getCurrentPeriodVO();
    } catch (Exception e) {
        Logger.error(e.getMessage(), e);
    }
    return period;
    }

    /**
     * Added by Young 2006-03-20 Start 审核查询
     */
    /**
     * 此处插入方法说明。 创建日期：(2005-11-24 13:42:56)
     *
     * @return nc.ui.pub.beans.UICheckBox
     */
    public UICheckBox getCbxChgSearch() {
    if (cbxChgSearch == null) {
        cbxChgSearch = new UICheckBox();
        cbxChgSearch.setText(nc.ui.ml.NCLangRes.getInstance().getStrByID("60130407", "UPP60130407-000151")/*
                                                         * @res
                                                         * "只查询当期薪资变化人员"
                                                         */);
        cbxChgSearch.setToolTipText(nc.ui.ml.NCLangRes.getInstance().getStrByID("60130407", "UPP60130407-000152")/*
                                                             * @res
                                                             * "只查询当期薪资与上期不相同的人员"
                                                             */);
        cbxChgSearch.setPreferredSize(new java.awt.Dimension(200, 22));
    }
    return cbxChgSearch;
    }

    /**
     * Added by Young 2006-03-20 End 审核查询
     */

    public boolean isGetFlag() {
    return getFlag;
    }

    public void setGetFlag(boolean getFlag) {
    this.getFlag = getFlag;
    }

    /**
     * 得到更新的CardPanel Created on 2008-5-13
     *
     * @author zhangg
     * @return
     */
    public BillCardPanel getUpdateBillCardPanel() {
    if (updateBillCardPanel == null) {
        ItemCardPanelBulider bulider = new ItemCardPanelBulider();
        BillItem[] billItems = getBspWaData().getTableModel().getBodyItems();

        // 重新计算， 审核不显示
        Vector<BillItem> unConvertBillItems = new Vector<BillItem>();
        for (BillItem billItem : billItems) {
        if (billItem.getKey() == "irecaculateflag" || billItem.getKey() == "icheckflag") {
            unConvertBillItems.add(billItem);
        }
        }

        bulider.setUnConvertBillItem(unConvertBillItems.toArray(new BillItem[unConvertBillItems.size()]));
        updateBillCardPanel = bulider.convertToBillCardPanel(getBspWaData());
    }
    return updateBillCardPanel;
    }

    /**
     * 设定单据的状态 ibo.resetCheckstate 没有部门权限的限制，
     * 而单据的状态有部门权限的约束。从而导致ibo中的状态与单据状态不一致。 所以定义这个方法。
     *
     * @param checkState
     */
    /*
     * public void setCheckState(int checkState) { this.checkState = checkState;
     * ibo.setCheckState(checkState); }
     */

    /*
     * public int getCheckState() { return checkState; }
     */

    public JComponent createFieldValueEditor(FilterMeta filterMeta) {
    if (filterMeta.getFieldCode().equalsIgnoreCase("wa_data.psnclid")) {

        UIRefPane rfPsnclForSel = new nc.ui.pub.beans.UIRefPane();
        rfPsnclForSel.setName("rfPsncl");
        rfPsnclForSel.setText("");
        rfPsnclForSel.setBounds(502, 66, 135, 22);
        rfPsnclForSel.setRefNodeName("人员类别");
        rfPsnclForSel.setReturnCode(false);
        rfPsnclForSel.setRefInputType(1/* 名称 */);

        rfPsnclForSel.setRefType(IRefConst.GRID);
        rfPsnclForSel.setIsCustomDefined(true);
        String st = "pk_corp='" + nc.ui.hr.global.Global.getWaCorpPk() + "' or pk_corp= '0001' or pk_corp is null";
        rfPsnclForSel.getRefModel().setWherePart(st);
        rfPsnclForSel.setButtonFireEvent(true);
        rfPsnclForSel.getRefModel().setUseDataPower(false);
        // user code end

        return rfPsnclForSel;
    }
    if (filterMeta.getFieldCode().equalsIgnoreCase("wa_data.icheckflag")) {
        UIComboBox checkstate = new UIComboBox();
        checkstate.addItem(nc.ui.ml.NCLangRes.getInstance().getStrByID("60131004", "UPP60131004-000148")/* "未审核" */);
        checkstate.addItem(nc.ui.ml.NCLangRes.getInstance().getStrByID("60131004", "UPP60131004-000149")/* "已审核" */);
        return checkstate;
    }
    if (filterMeta.getFieldCode().equalsIgnoreCase("wa_data.psnid")) {

        UIRefPane rfPsnPkForSel = new nc.ui.pub.beans.UIRefPane();
        rfPsnPkForSel.setName("refPsnName");
        rfPsnPkForSel.setText("");

        rfPsnPkForSel.setReturnCode(false);
        rfPsnPkForSel.setRefInputType(1/* 名称 */);

        rfPsnPkForSel.setRefType(IRefConst.GRIDTREE);
        rfPsnPkForSel.setIsCustomDefined(true);

        rfPsnPkForSel.setRefModel(new PsnRefModel(NCLangRes.getInstance().getStrByID("common", "UC000-0000129")));
        rfPsnPkForSel.getRefModel().setUseDataPower(false);
        return rfPsnPkForSel;
    }
    if (filterMeta.getFieldCode().equalsIgnoreCase("bd_psndoc.psnname")) {

        UIRefPane rfPsnNameForSel = new nc.ui.pub.beans.UIRefPane();
        rfPsnNameForSel.setName("refPsnName");
        rfPsnNameForSel.setText("");

        rfPsnNameForSel.setReturnCode(false);
        rfPsnNameForSel.setRefInputType(1/* 名称 */);

        rfPsnNameForSel.setRefType(IRefConst.GRIDTREE);

        rfPsnNameForSel.setIsCustomDefined(true);

        rfPsnNameForSel.setButtonFireEvent(true);
        rfPsnNameForSel.setRefModel(new PsnRefModel(filterMeta.getFieldName()));
        rfPsnNameForSel.getRefModel().setUseDataPower(false);
        return rfPsnNameForSel;
    }
    if (filterMeta.getFieldCode().equalsIgnoreCase("bd_psndoc.psncode")) {

        UIRefPane rfPsnCodeForSel = new nc.ui.pub.beans.UIRefPane();
        rfPsnCodeForSel.setName("refPsnName");
        rfPsnCodeForSel.setText("");

        rfPsnCodeForSel.setRefNodeName("===");
        rfPsnCodeForSel.setReturnCode(true);
        rfPsnCodeForSel.setRefInputType(1/* 名称 */
        );
        // user code begin {1}
        rfPsnCodeForSel.setRefType(IRefConst.GRIDTREE);

        rfPsnCodeForSel.setIsCustomDefined(true);

        rfPsnCodeForSel.setButtonFireEvent(true);
        rfPsnCodeForSel.setRefModel(new PsnRefModel(filterMeta.getFieldName()));
        rfPsnCodeForSel.getRefModel().setUseDataPower(false);
        return rfPsnCodeForSel;
        // user code end

    }
    if (filterMeta.getFieldCode().equalsIgnoreCase("wa_data.deptid")) {

        UIRefPane rfDeptName = new nc.ui.pub.beans.UIRefPane();
        rfDeptName.setName("rfDeptName");
        rfDeptName.setText("");
        rfDeptName.setBounds(502, 66, 135, 22);
        rfDeptName.setRefNodeName("部门档案");
        rfDeptName.setReturnCode(false);
        rfDeptName.setRefInputType(1/** 名称 */
        );
        // user code begin {1}
        rfDeptName.getRefModel().setRefNodeName("部门档案");
        rfDeptName.getRefModel().setPk_corp(Global.getWaCorpPk());

        rfDeptName.setIsCustomDefined(true);

        nc.ui.bd.ref.AbstractRefModel refModel = rfDeptName.getRefModel();

        String st = " and bd_deptdoc.pk_deptdoc in (select pk_deptdoc from wa_dept where pk_wa_class='" + ibo.getGzlbId() + "' and isealflag=0 and dr=0 ) " + deptpower1;

        refModel.addWherePart(st);

        rfDeptName.setRefModel(refModel);
        rfDeptName.setIncludeSubShow(true);
        rfDeptName.setButtonFireEvent(true);
        return rfDeptName;
    }
    try {
        return new HiInfoForQuery().getWaBmQueryValueRef(filterMeta.getFieldCode());
    } catch (Exception e) {
        Logger.error(e.getMessage(), e);
        e.printStackTrace();
    }
    return null;
    }

    public String getRefPanelWherePart(FilterMeta filterMeta) {
    return null;
    }

    /**
     * 工资总额的预警设置在初审复审都要进行提示
     *
     * @return
     */
    public String getAuditCreondition(WaclassVO waclassVO) {
    String tmAlert = "";
    try {
        String currentLimit = " wa_data.pk_wa_data = wa_dataz.pk_wa_dataz " + "and wa_data.classid=wa_waclass.pk_wa_class and wa_waclass.ICOLLECTFLAG=0 "
            + "and  wa_data.deptid=bd_deptdoc.pk_deptdoc and bd_deptdoc.pk_corp='" + Global.getCorpPK() + "' " + "and wa_data.icheckflag = 0 and wa_data.IRECACULATEFLAG=1 "
            + "and bd_psndoc.pk_psndoc= wa_data.psnid " + "and wa_data.classid='" + waclassVO.getPrimaryKey() + "' and wa_data.cyear='" + waclassVO.getCurrentPeriodVO().getCyear()
            + "' and wa_data.cperiod='" + waclassVO.getCurrentPeriodVO().getCperiod() + "'";
        String clwhere = "";
        String whereSql = getConditonWithDataPower();
        if (!getCheckDlg().isRangeAll()) {
        clwhere = ((whereSql == null || whereSql.trim().length() == 0) ? " 1=1 " : whereSql);
        } else {
        clwhere = " 1=1 ";
        }
        tmAlert = ibo.tmAlert(clwhere + " and " + currentLimit);
    } catch (Exception e) {
        Logger.error(e.getMessage(), e);
        e.printStackTrace();
    }
    return tmAlert;
    }

    public String getTheLatestCondition() {
    return theLatestCondition;
    }

    public void setTheLatestCondition(String theLatestCondition) {
    this.theLatestCondition = theLatestCondition;
    }

    public boolean isNeedPrompt() throws BusinessException {
    return false;
    }

    public String quickSearch(String psnWherePart, SearchType searchType) throws BusinessException {
    try {
        // 判断是否可以执行查询
        String powerStr = QsbUtil.checkSearchPower(this, m_boSearch, true);
        if (powerStr != null) {
        return powerStr;
        }
        if (getUIRefWaClass().getRefPK() == null) {
        showErrorMessage(nc.ui.ml.NCLangRes.getInstance().getStrByID("60131004", "UPP60131004-000333")/*
                                                         * @res
                                                         * "请选择薪资类别！"
                                                         */);
        return null;
        }
        String whereSql = " bd_psndoc.pk_psndoc in (" + psnWherePart + ")";
        // 添加部门权限和人员类别权限
        whereSql += (deptpower + psnclpower);
        // 保存查询条件，供刷新调用
        setTheLatestCondition(whereSql);
        ArrayList reList = new ArrayList();
        if (whereSql != null) {
        waData = ibo.getWaData(whereSql, "", false);
        waData = QsbUtil.orderbyResult(psnWherePart, DataVO.class, waData, "m_psnid", reList);
        } else {
        waData = null;
        }
        setData();

        // 设定数据完毕，重新设置按钮状态.因为查询的数据变了
        // resetButtonSate(ibo.getWaClassVO());
        resetButtonSate(ibo.resetWaClassVOWithCondition(whereSql));
        // 设定按钮状态。

        showHintMessage(nc.ui.ml.NCLangRes.getInstance().getStrByID("60131004", "UPP60131004-000209")/*
                                                         * @res
                                                         * "共有"
                                                         */
            + waData.length + nc.ui.ml.NCLangRes.getInstance().getStrByID("60131004", "UPP60131004-000210")/*
                                                             * @res
                                                             * "条数据。"
                                                             */);
        isQuickSearch = true;// 标记是否快速查询过
        // 返回未成功加载人数
        if (reList.size() > 0) {
        return (reList.get(0)).toString();
        }
    } catch (Exception ex) {
        reportException(ex);
        Logger.error(ex.getMessage(), ex);
        showErrorMessage(ex.getMessage());
    }
    return null;
    }

    // add for v57 ---------------- start----------------
    /**
     * 获得多部门薪资查看对话框
     */
    private void watchDataDeptDetail() {
    try {
        if (getUIRefWaClass().getRefPK() != null) {

        if (getBspWaData().getTable() == null || getBspWaData().getTable().getRowCount() < 1 || getBspWaData().getTable().getSelectedRow() == -1) {
            MessageDialog.showHintDlg(null, null, ResHelper.getString("6013v57","UPP6013v57-000018")//@res "请选择要查看的记录！"
);
            return;
        }
        int selectRow = getBspWaData().getTable().getSelectedRow();
        DataVO datavo = (DataVO) getBspWaData().getTableModel().getBodyValueRowVO(selectRow, DataVO.class.getName());
        mulDataDeptWatchDlg = new MulDataDeptWatchDlg(this, waGlobal, datavo.getPsnCode());
        mulDataDeptWatchDlg.setViewData();
        mulDataDeptWatchDlg.showModal();
        }
    } catch (Exception e) {
        Logger.error(e.getMessage(), e);
        MessageDialog.showErrorDlg(null, null, e.getMessage());
    }
    }

    /**
     * 多部门数据汇总
     *
     * @throws BusinessException
     */
    private boolean onBtnTotal4MulDeptItem() {
    String curClassid = waGlobal.getWaClassPK();
    String curWaYear = waGlobal.getWaYear();
    String curWaPeriod = waGlobal.getWaPeriod();
    try {

        String chenckwhere = " 1=1 " + deptpower + psnclpower;
        // 刷新数据显示
        if (waData == null) {
        // 刷新数据显示
        waData = ibo.getWaData(chenckwhere, getOrderStr());
        }

        if (ArrayUtils.isEmpty(waData)) {
        throw new BusinessException(ResHelper.getString("6013v57","UPP6013v57-000019")//@res "没有要汇总的数据。"
);
        }

        boolean blnIsHasCaculate = false;
        for (DataVO currentwaData : waData) {
        if (currentwaData.getRecaculateFlag() != null && currentwaData.getRecaculateFlag() == 1) {
            blnIsHasCaculate = true;
            break;
        }
        }

        if (blnIsHasCaculate) {
        int intResult = MessageDialog.showOkCancelDlg(null, null, ResHelper.getString("6013v57","UPP6013v57-000020")//@res "要汇总的记录中存在计算过的人员，重新汇总会覆盖原来记录并且清除计算标志，确认继续吗？"
);
        if (intResult == nc.ui.pub.beans.MessageDialog.ID_CANCEL || intResult == nc.ui.pub.beans.MessageDialog.ID_NO) {
            // 点击取消或叉时
            return false;
        }
        }

        WADelegator.getIDataDeptService().updateDataItemByDataDept(curClassid, curWaYear, curWaPeriod, null, ibo.getWaMulDeptitems(), deptpower + psnclpower);
       
       // 刷新数据
        refreshDataAndState();
        showHintMessage(ResHelper.getString("6013v57","UPP6013v57-000017")//@res "多部门数据汇总完毕！"
        );
        
        return true;
    } catch (Exception e) {
        Logger.error(e.getMessage(), e);
        MessageDialog.showErrorDlg(null, null, e.getMessage());
        return false;
    }
  } // add for v57 ---------------- end----------------
    
    public void refreshDataAndState() throws Exception{
    	String whereSql = "";
        if (isRefreshing()) {
        whereSql = getTheLatestCondition();
        } else {
        whereSql = getConditonWithDataPower();
        }
        resetButtonSate(ibo.resetWaClassVOWithCondition(whereSql));
        
        //重新查询数据
        waData = ibo.getWaData(whereSql, getOrderStr(), getCbxChgSearch().isSelected());
        setData();
    }
}
   