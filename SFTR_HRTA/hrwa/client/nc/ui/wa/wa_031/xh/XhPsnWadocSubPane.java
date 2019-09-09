package nc.ui.wa.wa_031.xh;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.swing.event.ListSelectionEvent;

import nc.bs.framework.common.NCLocator;
import nc.hr.utils.ResHelper;
import nc.itf.hr.wa.IPrmlv;
import nc.itf.hrp.pub.HRPPubTool;
import nc.itf.uap.bd.def.IDefdoc;
import nc.itf.wa.hrp.pub.IHRPWABtn;
import nc.ui.hi.pub.PsnInfFldRefPane;
import nc.ui.hrp.pub.tool.LongTimeTask_scm;
import nc.ui.hrwa.hrp.xh.tool.AfterEditExecutor;
import nc.ui.pub.ClientEnvironment;
import nc.ui.pub.ToftPanel;
import nc.ui.pub.beans.UICheckBox;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.bill.BillEditEvent;
import nc.ui.pub.bill.BillEditListener2;
import nc.ui.pub.bill.BillItem;
import nc.ui.pub.bill.BillModel;
import nc.ui.pub.bill.IBillModelDecimalListener2;
import nc.ui.pub.bill.itemconverters.UFDoubleConverter;
import nc.ui.trade.business.HYPubBO_Client;
import nc.ui.wa.pub.WADelegator;
import nc.ui.wa.wa_031.CellDecimalListener;
import nc.ui.wa.wa_031.PsnWadocSubPane;
import nc.ui.wa.wa_031.PsnWadocUI;
import nc.uif.pub.exception.UifException;
import nc.vo.bd.b06.PsndocVO;
import nc.vo.bd.def.DefdocVO;
import nc.vo.hi.wadoc.PsndocWadocVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDouble;
import nc.vo.scm.pu.PuPubVO;
import nc.vo.wa.wa_026.GradeVO;
import nc.vo.wa.wa_026.PrmlvVO;

/**
 * �˴��������������� �������ڣ�(2004-6-4 11:35:45)
 * 
 * @author��Administrator
 */
public class XhPsnWadocSubPane extends PsnWadocSubPane implements BillEditListener2{

	private ToftPanel tp = null;

	/**
	 * PsnWadocMainPanel ������ע�⡣
	 */
	public XhPsnWadocSubPane() {
		super();
		init();
//		initialize();
	}

	/**
	 * PsnWadocMainPanel ������ע�⡣
	 * 
	 * @param p0
	 *            java.awt.LayoutManager
	 */
	public XhPsnWadocSubPane(PsnWadocUI fatherUI) {
		super(fatherUI);
		this.tp = fatherUI;
		init();
//		initialize();
	}

	/**
	 * PsnWadocMainPanel ������ע�⡣
	 * 
	 * @param p0
	 *            java.awt.LayoutManager
	 * @param p1
	 *            boolean
	 */
	public XhPsnWadocSubPane(java.awt.LayoutManager p0, boolean p1) {
		super(p0, p1);
		init();
	}

	/**
	 * PsnWadocMainPanel ������ע�⡣
	 * 
	 * @param p0
	 *            boolean
	 */
	public XhPsnWadocSubPane(boolean p0) {
		super(p0);
		init();
	}
	private void init(){
		getBillScrollPane().addEditListener2(this);
	}

	public nc.ui.pub.bill.BillScrollPane getBillScrollPane2() {
		return getBillScrollPane();
	}

	public void initTable() {
		String[] itemname = {
				nc.ui.ml.NCLangRes.getInstance().getStrByID("60130715",
				"UPPT60130715-000003"),// н����Ŀ
				nc.ui.ml.NCLangRes.getInstance().getStrByID("60130715",
				"UPPT60130715-000007"),// ���ʵȼ����
				nc.ui.ml.NCLangRes.getInstance().getStrByID("60130715",
				"UPPT60130715-000001"),// н����ʼ����
				nc.ui.ml.NCLangRes.getInstance().getStrByID("60130715",
				"UPPT60130715-000002"),// н�ʽ�ֹ����
				nc.ui.ml.NCLangRes.getInstance().getStrByID("60130715",
				"UPPT60130715-000009"),// н�ʵ�������
				nc.ui.ml.NCLangRes.getInstance().getStrByID("60130704",
				"UPP60130704-000320"),// "̸�й���",
				// //
				nc.ui.ml.NCLangRes.getInstance().getStrByID("60130715",
				"UPPT60130715-000004"),// н�ʼ���
				nc.ui.ml.NCLangRes.getInstance().getStrByID("60130715",
				"UPPT60130715-000005"),// н�ʵ���
				nc.ui.ml.NCLangRes.getInstance().getStrByID("60130704",
				"UPP60130704-000039"),// "������",
				// //
				nc.ui.ml.NCLangRes.getInstance().getStrByID("60130715",
				"UPPT60130715-000006"),// ���
				nc.ui.ml.NCLangRes.getInstance().getStrByID("60130715",
				"UPPT60130715-000008"),// ���ű�־
				nc.ui.ml.NCLangRes.getInstance().getStrByID("60130715",
				"UPPT60130715-000012"),// ���±�־
				nc.ui.ml.NCLangRes.getInstance().getStrByID("60130715",
				"UPPT60130715-000010"),// �䶯ԭ��
				nc.ui.ml.NCLangRes.getInstance().getStrByID("60130715",
				"UPPT60130715-000011"),// �����ļ�
				"pk_wa_item",//
				"iflddecimal",
				"recordnum",//
				"pk_psndoc_sub",//
				"workflowflag",//
				"pk_wa_pralv",//
				"pk_wa_seclv",//
				"pk_wa_grd",
				"pk_changecause",
				nc.ui.ml.NCLangRes.getInstance().getStrByID("60130715",
				"UPPT60130715-000200"),// "��������",
				nc.ui.ml.NCLangRes.getInstance().getStrByID("60130715",
				"UPPT60130715-000201"),// "��Ա����",
				nc.ui.ml.NCLangRes.getInstance().getStrByID("60130715",
				"UPPT60130715-000202") // "��Ա����"
		};
		String[] itemcode = { "vname", // 1
				"wagradename", // 2
				"begindate", // 3
				"enddate", // 4
				"changedate", // 5
				"negotiation_wage", // 6
				"wa_prmlv_levelname", // 7
				"wa_seclv_levelname", // 8
				"criterionvalue", // 9
				"nmoney", // 10
				"waflag", // 11
				"lastflag", // 12
				"docname", // 13
				"vbasefile", // 14
				"pk_wa_item", // 15
				"iflddecimal", "recordnum", // 16
				"pk_psndoc_sub", // 17
				"workflowflag", // 18
				"pk_wa_pralv", // 19
				"pk_wa_seclv", // 20
				"pk_wa_grd",// 21
				"pk_changecause",// 22
				"deptName",// ��������23
				"psnCode",// ��Ա����24
				"psnName"// ��Ա����25
		};

		int[] itemtype = { BillItem.UFREF, // 1
				BillItem.UFREF, // 2
				BillItem.DATE, // 3
				BillItem.DATE, // 4
				BillItem.DATE, // 5
				BillItem.BOOLEAN, // 6
				BillItem.UFREF, // 7
				BillItem.UFREF, // 8
				BillItem.DECIMAL, // 9
				BillItem.DECIMAL, // 0
				BillItem.BOOLEAN, // 1
				BillItem.BOOLEAN, // 2
				BillItem.UFREF, // 3
				BillItem.STRING, // 4
				BillItem.STRING, // 5
				BillItem.INTEGER, // 6
				BillItem.INTEGER, BillItem.STRING, //
				BillItem.BOOLEAN, //
				BillItem.STRING, //
				BillItem.STRING, //
				BillItem.STRING, BillItem.STRING, BillItem.STRING, //
				BillItem.STRING, BillItem.STRING };

		BillItem[] billItems = new BillItem[itemname.length];
		for (int i = 0; i < itemname.length; i++) {
			billItems[i] = new BillItem();
			billItems[i].setName(itemname[i]);
			billItems[i].setKey(itemcode[i]);
			billItems[i].setDataType(itemtype[i]);
			billItems[i].setWidth(70);
			if (i == 0) {
				billItems[0].setRefType("<nc.ui.wa.wa_031.WaItemRefModel>");
				billItems[0].setIDColName("pk_wa_item");
			} else if (i == 12) {
				PsnInfFldRefPane psnRef = new PsnInfFldRefPane(
				"0001AA10000000003Z5S");
				billItems[12].setComponent(psnRef);
			}

			if(i == 1){
				billItems[1].setRefType("<nc.ui.wa.wa_031.xh.XhGradeRefModel>");
				billItems[1].setIDColName("pk_wa_grd");
				((UIRefPane)billItems[1].getComponent()).setReturnCode(true);
				((UIRefPane)billItems[1].getComponent()).setRefInputType(0);
			}

			if (i > 13) {
				billItems[i].setShow(false);
			} else {
				billItems[i].setShow(true);
			}

			billItems[i].setEdit(true);
			billItems[i].setEnabled(true);

			if (itemcode[i].equals("vname")
					|| itemcode[i].equals("wagradename")
					|| itemcode[i].equals("begindate")
					|| itemcode[i].equals("nmoney")) {

				billItems[i].setNull(true);

			} else {
				billItems[i].setNull(false);

			}
			if (itemtype[i] == BillItem.BOOLEAN) {// ���ű�־�޸�ʱ�����������������޸�
				((UICheckBox) billItems[i].getComponent())
				.setHorizontalAlignment(UICheckBox.CENTER);
			}

			if (itemcode[i].equals("criterionvalue")
					|| itemcode[i].equals("nmoney")) {
				IBillModelDecimalListener2 bmd = new CellDecimalListener(
				"iflddecimal");
				billItems[i].addDecimalListener(bmd);
			}
			if (itemcode[i].equals("nmoney")) {
				billItems[i].setLength(12);
			}

			if (itemcode[i].equals("vname")
					|| itemcode[i].equals("wagradename")
					|| itemcode[i].equals("wa_prmlv_levelname")
					|| itemcode[i].equals("wa_seclv_levelname")) {
				billItems[i].setLength(512);
			}
			if (itemcode[i].equals("vbasefile")) {
				billItems[i].setLength(100);
			}
			if (itemcode[i].equals("waflag")) {
//				billItems[i].setEdit(false);
//				billItems[i].setEnabled(false);
			}
		}
		((UIRefPane) billItems[0].getComponent()).addValueChangedListener(this);
		((UIRefPane) billItems[2].getComponent()).addValueChangedListener(this);

		BillModel billModel = new BillModel() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int row, int col) {
				if (getBillScrollPane2().isLockCol()) {
					col = col - getBillScrollPane2().getLockCol();
				}
				return isEditable(row, col);
			}

		};

		//
		billItems[22].setShow(false);
		billItems[23].setShow(false);
		billItems[24].setShow(false);
		//

		billModel.setBodyItems(billItems);

		getBillScrollPane().setTableModel(billModel);
		getBillScrollPane().getTable().setSelectionMode(
				javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		getBillScrollPane().getTable().getSelectionModel().addListSelectionListener(
				this);
		getBillScrollPane().getTable().setSortEnabled(false);

		getBillScrollPane().getTable().setColumnSelectionAllowed(false);
		// getBillPane().getTable().setCellSelectionEnabled(false);
		getBillScrollPane().getTable().setRowSelectionAllowed(true);
		getBillScrollPane().setRowNOShow(true);
	}

	private void setBodyItemValue(int row,String fieldname,Object oValue){
		getBillScrollPane().getTableModel().setValueAt(oValue,
				row, fieldname);
	}

	private AfterEditExecutor edittool = null;
	private AfterEditExecutor getEditTool(){
		if(edittool == null){
			edittool = new AfterEditExecutor();
			edittool.setAfterEditListener(this);
		}
		return edittool;
	}

	public void onTempAdd(){
		int icount = getBillScrollPane().getTableModel().getRowCount();
//		if(icount > 0){
//			super.onAdd();
//			return;
//		}

//		��Ա��������ʱ����н�ʱ�׼����������
		PsndocWadocVO[] vos = null;
		try {
			vos = queryWadocVosOnAdd();
		} catch (Exception e) {
			// TODO �Զ����� catch ��
			e.printStackTrace();
			super.onAdd();
			return;
		}

		if(vos == null || vos.length == 0){
			super.onAdd();
			return;
		}
		UFDate date = ClientEnvironment.getInstance().getDate();
		try {
			String month = WADelegator.getWaPeriod().getYWPeriod(IHRPWABtn.PK_GONG, false);
			date = month!=null?new UFDate(month.substring(0,4)+"-"+month.substring(4,6)+"-01"):date;
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		PsndocWadocVO selectVO = getSelectVO();
		boolean flag = false;
		try {
			PsndocVO pvo = (PsndocVO)HYPubBO_Client.queryByPrimaryKey(PsndocVO.class, selectVO.getPk_psndoc());
			flag = pvo.getGroupdef26()!=null&&pvo.getGroupdef26().equals("000169100000000rOQQW");//��ʿ�󲻴�
		} catch (UifException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// �����Ѿ������н����Ŀzhanghua
		ArrayList<PsndocWadocVO> wa_item_list = new ArrayList<PsndocWadocVO>();
		Collections.addAll(wa_item_list, vos);// ����ת��Ϊlist
		
		if(icount > 0){
			//super.onAdd();
			//return;
			for(int i=0;i<icount;i++){
				String pk_wa_item = (String) getBillScrollPane().getTableModel().getValueAt(i, "pk_wa_item");
				if(wa_item_list != null || wa_item_list.size() != 0){
					for(int j=0;j<wa_item_list.size();j++){
						if(pk_wa_item.equals(wa_item_list.get(j).getPk_wa_item())){
							wa_item_list.remove(j);
							
						}
					}
				}
			}
			
		}
		
		PsndocWadocVO[] psndocwadocvos =  wa_item_list.toArray(new PsndocWadocVO[0]);
//		���������õ�����
		for(PsndocWadocVO vo:psndocwadocvos){
			super.onAdd();
			int row = getBillScrollPane().getTableModel().getRowCount() - 1;
			setBodyItemValue(row, "pk_wa_item", vo.getPk_wa_item());//н����Ŀ
			//0001691000000000BYXA ��ʳ����107  0001691000000000BYXW��ͨ����440��0001691000000000BYXZ��������1   60��0001691000000000BYYH���ս�֧��45��0001691000000000BYYN������30
			if(!flag){
				if(vo.getPk_wa_item().equals("0001691000000000BYXA")){
					setBodyItemValue(row, "nmoney", new UFDouble(107.00));//н����Ŀ  
				}else if(vo.getPk_wa_item().equals("0001691000000000BYXW")){
					setBodyItemValue(row, "nmoney", new UFDouble(440.00));//н����Ŀ  
				}else if(vo.getPk_wa_item().equals("0001691000000000BYXZ")){
					setBodyItemValue(row, "nmoney", new UFDouble(60.00));//н����Ŀ  
				}else if(vo.getPk_wa_item().equals("0001691000000000BYYH")){
					setBodyItemValue(row, "nmoney", new UFDouble(45.00));//н����Ŀ  
				}else if(vo.getPk_wa_item().equals("0001691000000000BYYN")){
					setBodyItemValue(row, "nmoney", new UFDouble(30.00));//н����Ŀ  
				}
			}
			setBodyItemValue(row, "vname", vo.getVname());//н����Ŀ  
			setBodyItemValue(row, "changedate", date);//н����Ŀ  
			setBodyItemValue(row, "negotiation_wage", new UFBoolean(true));//н����Ŀ 
			setBodyItemValue(row, "iflddecimal", vo.getIflddecimal());
//			setBodyItemValue(row, fieldname, oValue);

			UIRefPane ref = (UIRefPane)getBillScrollPane().getTableModel().getItemByKey("vname").getComponent();
			ref.setPK(vo.getPk_wa_item());
			ref.setValue(vo.getVname());

			getEditTool().exe(row, getBillScrollPane().getTableModel().getItemByKey("vname"), vo.getVname());

			setBodyItemValue(row, "pk_wa_grd", vo.getPk_wa_grd());//н�ʱ�׼
			setBodyItemValue(row, "negotiation_wage", vo.getPk_wa_pralv()!=null&&vo.getPk_wa_pralv().trim().length()>0?new UFBoolean(false):new UFBoolean(true));//н�ʱ�׼
			setBodyItemValue(row, "wagradename", vo.getWagradename());
		}
		isbatchadd = true;
		
	
	}
	
	public void onAdd() {
		
		int icount = getBillScrollPane().getTableModel().getRowCount();
		if(icount > 0){
			super.onAdd();
			return;
		}

//		��Ա��������ʱ����н�ʱ�׼����������
		PsndocWadocVO[] vos = null;
		try {
			vos = queryWadocVosOnAdd();
		} catch (Exception e) {
			// TODO �Զ����� catch ��
			e.printStackTrace();
			super.onAdd();
			return;
		}

		if(vos == null || vos.length == 0){
			super.onAdd();
			return;
		}
		UFDate date = ClientEnvironment.getInstance().getDate();
		try {
			String month = WADelegator.getWaPeriod().getYWPeriod(IHRPWABtn.PK_GONG, false);
			date = month!=null?new UFDate(month.substring(0,4)+"-"+month.substring(4,6)+"-01"):date;
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		PsndocWadocVO selectVO = getSelectVO();
		boolean flag = false;
		try {
			PsndocVO pvo = (PsndocVO)HYPubBO_Client.queryByPrimaryKey(PsndocVO.class, selectVO.getPk_psndoc());
			flag = pvo.getGroupdef26()!=null&&pvo.getGroupdef26().equals("000169100000000rOQQW");//��ʿ�󲻴�
		} catch (UifException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		���������õ�����
		for(PsndocWadocVO vo:vos){
			super.onAdd();
			int row = getBillScrollPane().getTableModel().getRowCount() - 1;
			setBodyItemValue(row, "pk_wa_item", vo.getPk_wa_item());//н����Ŀ
			//0001691000000000BYXA ��ʳ����107  0001691000000000BYXW��ͨ����440��0001691000000000BYXZ��������1   60��0001691000000000BYYH���ս�֧��45��0001691000000000BYYN������30
			if(!flag){
				if(vo.getPk_wa_item().equals("0001691000000000BYXA")){
					setBodyItemValue(row, "nmoney", new UFDouble(107.00));//н����Ŀ  
				}else if(vo.getPk_wa_item().equals("0001691000000000BYXW")){
					setBodyItemValue(row, "nmoney", new UFDouble(440.00));//н����Ŀ  
				}else if(vo.getPk_wa_item().equals("0001691000000000BYXZ")){
					setBodyItemValue(row, "nmoney", new UFDouble(60.00));//н����Ŀ  
				}else if(vo.getPk_wa_item().equals("0001691000000000BYYH")){
					setBodyItemValue(row, "nmoney", new UFDouble(45.00));//н����Ŀ  
				}else if(vo.getPk_wa_item().equals("0001691000000000BYYN")){
					setBodyItemValue(row, "nmoney", new UFDouble(30.00));//н����Ŀ  
				}
			}
			setBodyItemValue(row, "vname", vo.getVname());//н����Ŀ  
			setBodyItemValue(row, "changedate", date);//н����Ŀ  
			setBodyItemValue(row, "negotiation_wage", new UFBoolean(true));//н����Ŀ 
			setBodyItemValue(row, "iflddecimal", vo.getIflddecimal());
//			setBodyItemValue(row, fieldname, oValue);

			UIRefPane ref = (UIRefPane)getBillScrollPane().getTableModel().getItemByKey("vname").getComponent();
			ref.setPK(vo.getPk_wa_item());
			ref.setValue(vo.getVname());

			getEditTool().exe(row, getBillScrollPane().getTableModel().getItemByKey("vname"), vo.getVname());

			setBodyItemValue(row, "pk_wa_grd", vo.getPk_wa_grd());//н�ʱ�׼
			setBodyItemValue(row, "negotiation_wage", vo.getPk_wa_pralv()!=null&&vo.getPk_wa_pralv().trim().length()>0?new UFBoolean(false):new UFBoolean(true));//н�ʱ�׼
			setBodyItemValue(row, "wagradename", vo.getWagradename());
		}
		isbatchadd = true;
		
	}

	public void valueChanged(ListSelectionEvent e) {
		if(isbatchadd)
			return;
		super.valueChanged(e);
	}

	public void setState(String state) {
		super.setState(state);
		isbatchadd = false;
	}

	private PsndocWadocVO[] getSubVOsForBatchAdd() throws BusinessException{
		int rowcount = getBillScrollPane().getTable().getRowCount();
		if(rowcount <= 0)
			return null;
		List<PsndocWadocVO> ldata = new ArrayList<PsndocWadocVO>();
		for(int row = 0;row<rowcount;row++){
			PsndocWadocVO selectVO = getSelectVO2(row);
			if(PuPubVO.getUFDouble_NullAsZero(selectVO.getNmoney()).equals(UFDouble.ZERO_DBL))//���Ϊ0�Ĺ��˵�
				continue;
			validate2(selectVO);
			selectVO.setIadjustmatter(1);
			
			//zhanghua �жϵ�ǰ���Ƿ��Ѿ������
			String sqlwhere = " pk_psndoc='"+selectVO.getPk_psndoc()+"' and pk_wa_item='"+selectVO.getPk_wa_item()+"'";
			PsndocWadocVO[] psnwadocvo = (PsndocWadocVO[])HYPubBO_Client.queryByCondition(PsndocWadocVO.class, sqlwhere);
			
			if(psnwadocvo.length == 0){
				ldata.add(selectVO);
			}
			
			
		}

		if(ldata == null || ldata.size() == 0)
			return null;
		return ldata.toArray(new PsndocWadocVO[0]);
	}

	public void validate2(PsndocWadocVO psVo) throws BusinessException {
		String error = null;
		if (psVo.getPk_wa_item() == null || psVo.getPk_wa_item().length() <= 0) {
			error = ResHelper.getString("60130715", "UPPT60130715-000153"); // "н����Ŀ����Ϊ��.";
			throw new BusinessException(error);
		}
		if (psVo.getPk_wa_grd() == null
				|| psVo.getPk_wa_grd().toString().length() <= 0) {
			error = ResHelper.getString("60130704", "UPP60130704-000322");// "н�ʵȼ������Ϊ�ա�";
			throw new BusinessException(error);
		}

		if (psVo.getBegindate() == null
				|| psVo.getBegindate().toString().length() <= 0) {
			error = ResHelper.getString("60130715", "UPPT60130715-000155");// "������н�ʵ���ʼʱ��.";
			throw new BusinessException(error);
		}
		if (psVo.getEnddate() != null
				&& psVo.getEnddate().toString().length() > 0
				&& psVo.getEnddate().before(psVo.getBegindate())) {
			error = ResHelper.getString("60130715", "UPPT60130715-000156"); // "����ʱ�䲻Ӧ������ʼʱ��.";
			throw new BusinessException(error);

		}
		if (!PuPubVO.getUFBoolean_NullAs(psVo.getNegotiation_wage(),UFBoolean.FALSE).booleanValue()) {
			if (psVo.getPk_wa_grd() == null || psVo.getPk_wa_pralv() == null) {
				error = ResHelper.getString("60130704", "UPP60130704-000321");// "��̸�й���,
				// �������뼶��.";
				throw new BusinessException(error);
			}

			GradeVO grdvo = null;
			grdvo = WADelegator.getWaGrade().findGradeByPk(psVo.getPk_wa_grd());

			if (grdvo != null && grdvo.getIsmultsec().booleanValue()) {
				if (psVo.getPk_wa_seclv() == null
						|| psVo.getPk_wa_seclv().length() <= 0) {
					error = ResHelper.getString("60130704",
					"UPP60130704-000321");// "��̸�й���,
					// //
					// �������뼶��.";
					throw new BusinessException(error);
				}
			}
		}
		if (psVo.getNmoney() == null) {
			error = ResHelper.getString("60130715", "UPPT60130715-000154");// "����Ϊ��.";
			throw new BusinessException(error);
		}
	}

	public PsndocWadocVO getSelectVO2(int row) {
		// getBillPane().getTable().getCellEditor().stopCellEditing();
		PsndocWadocVO selectVO = new PsndocWadocVO();
		int selectRow = row;

		BillItem[] billItems = getBillScrollPane().getTableModel().getBodyItems();
		for (BillItem item : billItems) {
			Object value = getBillScrollPane().getTableModel().getValueAt(selectRow,
					item.getKey());
			if (value != null) {
				if (value instanceof Boolean) {
					value = new UFBoolean((Boolean) value);
				}
			}
			if (value != null) {
				selectVO.setAttributeValue(item.getKey(), value);
			}
		}
		// selectVO = (PsndocWadocVO)
		// getBillPane().getTableModel().getBodyValueRowVO(selectRow,
		// PsndocWadocVO.class.getName());
		selectVO.setPk_psndoc(getMainVO().getPk_psndoc());
		return selectVO;
	}

	public boolean onSave() throws Exception {
		boolean retb = false;
		if (!isbatchadd) {
			retb = super.onSave();
		} else {
			PsndocWadocVO[] vos = getSubVOsForBatchAdd();
			if (vos == null || vos.length == 0) {
				tp.showWarningMessage("������");
				return false;
			}

			Class[] ParameterTypes = new Class[] { PsndocWadocVO[].class };
			Object[] ParameterValues = new Object[] { vos };
			vos = (PsndocWadocVO[]) LongTimeTask_scm.calllongTimeService(
					"hrwa", tp, "���ڱ���...", 2, "nc.bs.wa.wa_031.xh.XhPanWaBO",
					null, "batchSave", ParameterTypes, ParameterValues);
			setWadocData(vos);

			for(PsndocWadocVO selectVO:vos){
				String psnid = selectVO.getPk_psndoc();
				String iitemid = selectVO.getPk_wa_item();
				String wa_prmlv_levelname = selectVO.getPk_wa_pralv();
				UFBoolean  lastflag = selectVO.getLastflag();
				UFBoolean  waflag = selectVO.getWaflag();
				if(iitemid.equals("0001691000000000BYWP")&&lastflag!=null&&lastflag.booleanValue()){
					IPrmlv prmlv = NCLocator.getInstance().lookup(IPrmlv.class);
					PrmlvVO pvo = prmlv.findPrmlvByPk(wa_prmlv_levelname);
					String docname = pvo.getLevelname();
					if(pvo.getLevelname().startsWith("ר��δƸ")||pvo.getLevelname().startsWith("����δƸ")){
						docname = "ר��-��ϰ��";
					}else if(pvo.getLevelname().startsWith("����δƸ")){
						docname = "����-��ϰ��";
					}else if(pvo.getLevelname().startsWith("����δƸ")){
						docname = "����-��ϰ��";
					}else if(pvo.getLevelname().startsWith("�̻�")){
						docname = docname.replace("�̻�", "ר��");
					}else if(pvo.getLevelname().startsWith("������")){
						docname = docname.replace("������", "����");
					}else if(pvo.getLevelname().startsWith("��ͨ��")){
						docname = "����-��ͨ��";
					}
					IDefdoc doc = NCLocator.getInstance().lookup(IDefdoc.class);
					DefdocVO[] docvos = (DefdocVO[])doc.queryByWhere(" docname='"+docname+"' and pk_defdoclist='00016910000000009GR4' ");
					if(docvos!=null&&docvos.length>0){
						PsndocVO psnvo = (PsndocVO)HYPubBO_Client.queryByPrimaryKey(PsndocVO.class, psnid);
						if(waflag!=null&&waflag.booleanValue()){
							psnvo.setGroupdef33(docvos[0].getPrimaryKey());
						}else{
							psnvo.setGroupdef33(null);
						}
						HYPubBO_Client.update(psnvo);
					}
				}
			}

			return true;
		}

		return retb;
	}

	private boolean isbatchadd = false;
	public boolean isEditable(int row, int col) {
		if(!isbatchadd)
			return super.isEditable(row, col);
		return true;
	}


	private PsndocWadocVO[] queryWadocVosOnAdd() throws Exception{
		PsndocWadocVO[] vos = null;
		Class[] ParameterTypes = new Class[]{String.class};
		Object[] ParameterValues = new Object[]{ClientEnvironment.getInstance().getCorporation().getPrimaryKey()};
		vos = (PsndocWadocVO[])LongTimeTask_scm.calllongTimeService("hrwa", tp , "��������...", 2, "nc.bs.wa.wa_031.xh.XhPanWaBO", null, "queryPandocWadocVOOnAdd", ParameterTypes, ParameterValues);
		return vos;
	}

	public void afterEdit(BillEditEvent e) {

		int nRow = getBillScrollPane().getTable().getSelectedRow();
		if (e.getKey() == "vname") {
			UIRefPane itemPane = ((UIRefPane) getBillScrollPane().getTableModel()
					.getBodyItems()[0].getComponent());
			String pk_wa_item = itemPane.getRefPK();
			// liangxr
			int iflddecimal = (Integer) itemPane.getRefValue("iflddecimal");
			getBillScrollPane().getTableModel().setValueAt(iflddecimal, nRow,
			"iflddecimal");

			// ����С��λ����һ��ʱ������С��λ����������converter��
			int col = getBillScrollPane().getTableModel().getBodyColByKey(
			"criterionvalue");
			getBillScrollPane().getTableModel().getBodyItems()[col]
			                                                   .setConverter(new UFDoubleConverter(iflddecimal));
			col = getBillScrollPane().getTableModel().getBodyColByKey("nmoney");
			getBillScrollPane().getTableModel().getBodyItems()[col]
			                                                   .setConverter(new UFDoubleConverter(iflddecimal));

//			((UIRefPane) getBillScrollPane().getTableModel().getBodyItems()[1]
//			.getComponent()).setRefModel(new GradeRefModel(pk_wa_item));
			((UIRefPane) getBillScrollPane().getTableModel().getBodyItems()[1]
			                                                                .getComponent()).setText(null);
//			((UIRefPane) getBillScrollPane().getTableModel().getBodyItems()[1]
//			.getComponent()).setReturnCode(true);
//			((UIRefPane) getBillScrollPane().getTableModel().getBodyItems()[1]
//			.getComponent()).setRefInputType(0);
			getBillScrollPane().getTableModel().setValueAt(null, nRow, "wagradename");
			getBillScrollPane().getTableModel().setValueAt(null, nRow, "pk_wa_grd");
			getBillScrollPane().getTableModel().setValueAt(pk_wa_item, nRow,"pk_wa_item");

			if(pk_wa_item.equals("0001691000000000BYY5")){//��λ���������籣��������
				UFDouble nmny = new UFDouble(0.00);
				int rowcount = getBillScrollPane().getTableModel().getRowCount();
				for(int i=0;i<rowcount;i++){
					String iitemid = (String)getBillScrollPane().getTableModel().getValueAt(i, "pk_wa_item");
					UFBoolean last = getBillScrollPane().getTableModel().getValueAt(i, "lastflag")!=null?new UFBoolean(getBillScrollPane().getTableModel().getValueAt(i, "lastflag").toString()):new UFBoolean(false);
					if(last!=null&&last.booleanValue()&&iitemid!=null&&iitemid.equals("0001691000000000T5E2")){
						nmny = getBillScrollPane().getTableModel().getValueAt(i, "nmoney")!=null?new UFDouble(getBillScrollPane().getTableModel().getValueAt(i, "nmoney").toString()):new UFDouble(0.00);
						break;
					}
				}
				getBillScrollPane().getTableModel().setValueAt(nmny.multiply(0.05).setScale(0, 2), nRow,"nmoney");
			}

//			�������� �»� ����   
			if(!isbatchadd && getState().equals(ADD_STATE) && pk_wa_item!=null){
//				���������ͬн����Ŀ ����  н�ʱ�׼
				int rowc = getBillScrollPane().getTableModel().getRowCount();
				rowc = rowc-1;//ȥ����ǰ��������
				for(int row = 0;row < rowc;row++){
					String tmpitemid = HRPPubTool.getString_NullAsTrimZeroLen(getBillScrollPane().getTableModel().getValueAt(row, "pk_wa_item"));
					if(tmpitemid.equalsIgnoreCase(pk_wa_item)){
						String grdid = HRPPubTool.getString_NullAsTrimZeroLen(getBillScrollPane().getTableModel().getValueAt(row, "pk_wa_grd"));
						String grdname = HRPPubTool.getString_NullAsTrimZeroLen(getBillScrollPane().getTableModel().getValueAt(row, "wagradename"));

						getBillScrollPane().getTableModel().setValueAt(grdname, nRow, "wagradename");
						getBillScrollPane().getTableModel().setValueAt(grdid, nRow, "pk_wa_grd");

						break;
					}
				}
			}


			return;
		} 

		if (e.getKey() == "wagradename") {
			UIRefPane gradePane = ((UIRefPane) getBillScrollPane().getTableModel()
					.getBodyItems()[1].getComponent());
			String pk_grd = gradePane.getRefPK();
//			refreshCriterion(pk_grd);

			setDefaultData(pk_grd);

		}

		super.afterEdit(e);
		if(e.getKey().equalsIgnoreCase("begindate")){
			if(!isbatchadd)
				return;
			int rowc = getBillScrollPane().getTable().getRowCount();
			if(rowc<=0)
				return;
			UFDate date = PuPubVO.getUFDate(e.getValue());
			if(date == null)
				return;
			for(int row = 0;row < rowc;row ++){
				UFDate date2 = (UFDate)getBillScrollPane().getTableModel().getValueAt(row, "begindate");
				if(date2 == null){
					setBodyItemValue(row, "begindate", date);
				}
			}
		}
	}

	public void onModify() throws Exception {
		int selectRow = getBillScrollPane().getTable().getSelectedRow();
		last_select_row_number = selectRow;

//		String pk_wa_item = getBillPane().getTableModel().getValueAt(selectRow,
//		"pk_wa_item").toString();

//		((UIRefPane) getBillPane().getTableModel().getBodyItems()[1]
//		.getComponent()).setRefModel(new GradeRefModel(pk_wa_item));

		setState(MODIFY_STATE);

		getBillScrollPane().getTable().setRowSelectionInterval(
				last_select_row_number, last_select_row_number);
	}

	public boolean beforeEdit(BillEditEvent e) {
		// TODO �Զ����ɷ������
		if(e.getKey().equalsIgnoreCase("wagradename")){
			UIRefPane refpane = (UIRefPane)getBillScrollPane().getTableModel().getItemByKey("wagradename").getComponent();
			String pk_wa_item = PuPubVO.getString_TrimZeroLenAsNull(getBillScrollPane().getTableModel().getValueAt(e.getRow(), "pk_wa_item"));
			((XhGradeRefModel)refpane.getRefModel()).setWaItem(pk_wa_item);
		}else if(e.getKey().equalsIgnoreCase("criterionvalue")){//������
			String pk_wa_grd = PuPubVO.getString_TrimZeroLenAsNull(getBillScrollPane().getTableModel().getValueAt(e.getRow(), "pk_wa_grd"));
			if(pk_wa_grd == null)
				return true;
			refreshCriterion(pk_wa_grd);
		}
		return true;
	}
}
