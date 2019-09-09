package nc.ui.wa.wa_hrp_pub;

import java.awt.LayoutManager;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import nc.ui.hr.func.IParaPanel;
import nc.ui.pub.beans.UIPanel;
import nc.vo.hr.func.FunctableItemVO;

/**
 * 
 * @author tianxfc
 *
 */
@SuppressWarnings("restriction")
public class HrpTongrenPaibanParaPanel extends UIPanel implements ItemListener, IParaPanel {

	private static final long serialVersionUID = 1L;
	
	private nc.ui.pub.beans.UIComboBox ivjUICmbTbmItem = null;
	private nc.ui.pub.beans.UILabel ivjlblItem = null;
	private String[] selkeys = new String[]{"ykholiday"};//,"ykdailyday"
	private String[] selnames = new String[]{"�ڼ����Ű�����"};//,"ȱ�ڿ�������"

	public HrpTongrenPaibanParaPanel() {
		super();
		initialize();
	}
	/**
	 * ���� UILabel1 ����ֵ��
	 * @return nc.ui.pub.beans.UILabel
	 */
	/* ���棺�˷������������ɡ� */
	private nc.ui.pub.beans.UILabel getlblItem() {
		if (ivjlblItem == null) {
			try {
				ivjlblItem = new nc.ui.pub.beans.UILabel();
				ivjlblItem.setName("lblItem");
				ivjlblItem.setText("�ۿ���Ŀ");
				ivjlblItem.setBounds(15, 50, 67, 22);
				// user code begin {1}
				ivjlblItem.setVisible(true);
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjlblItem;
	}
	/**
	 * ��ʼ���ࡣ
	 */
	/* ���棺�˷������������ɡ� */
	private void initialize() {
		try {
			// user code begin {1}
			// user code end
			setName("TbmParaPanel");
			setLayout(null);
			setSize(240, 200);
			add(getlblItem(), getlblItem().getName());
			add(getUICmbTbmItem(), getUICmbTbmItem().getName());
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
		// user code begin {2}
		getUICmbTbmItem().addItemListener(this);
		getUICmbTbmItem().setSelectedIndex(0);
//		initData();
		// user code end
	}
	/**
	 * ÿ�������׳��쳣ʱ������
	 * @param exception java.lang.Throwable
	 */
	private void handleException(java.lang.Throwable exception) {

		/* ��ȥ���и��е�ע�ͣ��Խ�δ��׽�����쳣��ӡ�� stdout�� */
		exception.printStackTrace(System.out);
	}
	/**
	 * ���� UICmbTbmItem ����ֵ��
	 * @return nc.ui.pub.beans.UIComboBox
	 */
	/* ���棺�˷������������ɡ� */
	private nc.ui.pub.beans.UIComboBox getUICmbTbmItem() {
		if (ivjUICmbTbmItem == null) {
			try {
				ivjUICmbTbmItem = new nc.ui.pub.beans.UIComboBox();
				ivjUICmbTbmItem.setName("UICmbTbmItem");
				ivjUICmbTbmItem.setBounds(84, 50, 135, 22);
				// user code begin {1}
				ivjUICmbTbmItem.setVisible(true);
				ivjUICmbTbmItem.addItems(selnames);
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjUICmbTbmItem;
	}
	/**
	 * @param p0
	 */
	public HrpTongrenPaibanParaPanel(LayoutManager p0) {
		super(p0);
	}

	/**
	 * @param p0
	 */
	public HrpTongrenPaibanParaPanel(boolean p0) {
		super(p0);
	}

	/**
	 * @param p0
	 * @param p1
	 */
	public HrpTongrenPaibanParaPanel(LayoutManager p0, boolean p1) {
		super(p0, p1);
	}

	public void itemStateChanged(ItemEvent arg0) {
	}

	public void checkPara() throws Exception {
	}

	public void clearDis() {
	}

	public String getPara() throws Exception {
		int x = getUICmbTbmItem().getSelectedIndex();
		return selkeys[x];
	}

	public String getParaStr() {
		int x = getUICmbTbmItem().getSelectedIndex();
		return selnames[x];
	}

	public void setDatatype(int newDatatype) {
	}

	public void updateDis(FunctableItemVO[] paras) {
	}

	public void updateDis(int index) {
	}

	public void updateDis(String funcname) {
	}

}
