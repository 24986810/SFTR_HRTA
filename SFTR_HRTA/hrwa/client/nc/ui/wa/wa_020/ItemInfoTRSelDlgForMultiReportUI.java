/**
 *
 */
package nc.ui.wa.wa_020;

import java.awt.Container;
import java.util.List;

import nc.ui.hr.global.Global;
import nc.vo.pub.BusinessException;
import nc.vo.wa.wa_001.WaGlobalVO;
import nc.vo.wa.wa_002.ItemserialVO;
import nc.vo.wa.wa_024.ItemVO;

/**
 * @author zhangg
 *
 */
public class ItemInfoTRSelDlgForMultiReportUI extends ItemInfoTRVOSelAbstractDlg {

	private static final long serialVersionUID = 1524430999048063328L;
	private final String pk_corp = Global.getCorpPK();
	private final String userid = Global.getUserID();

	public ItemInfoTRSelDlgForMultiReportUI(Container parent, int type, WaGlobalVO[] list) {
		super(parent, type, list);
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see nc.ui.wa.wa_020.ItemInfoVOSelAbstractDlg#onSave()
	 */
	@Override
	public void onSave() throws BusinessException {
		// TODO Auto-generated method stub
		ItemserialVO[] vos = null;
		if (getSelectedVo() != null && getSelectedVo().length > 0) {
			vos = new ItemserialVO[getSelectedVo().length];
			for (int i = 0; i < getSelectedVo().length; i++) {
				vos[i] = new ItemserialVO();
				vos[i].setPk_corp(pk_corp);
				vos[i].setOperateid(userid);
				vos[i].setWaclassid("UNITE");
				vos[i].setItemid(getSelectedVo()[i].getPrimaryKey());
				vos[i].setVname(getSelectedVo()[i].getVname());
				vos[i].setItemserial(new Integer(i + 1));
			}
			nc.ui.wa.pub.WADelegator.getClassitem().insertVOsOnUniteClass(vos, pk_corp, userid, "UNITE");
		} else {
			nc.ui.wa.pub.WADelegator.getClassitem().deleteitemOnUniteClass(pk_corp, userid, "UNITE");
		}

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see nc.ui.wa.wa_020.ItemInfoVOSelAbstractDlg#refreshData()
	 */
	@Override
	public void refreshData() throws BusinessException {
		// TODO Auto-generated method stub

		if (getType() == 1 || getType() == 2) {// 1：名细表 2：汇总表
			WaGlobalVO[]list = getWaclassVOs();
			//sqt  2013.01.23   修改逻辑， 只塞入到页面最后一组数据，，现在统一出数据，统一塞入到页面   去掉外部for循环
//			for(WaGlobalVO arrVOs : list){
				ItemVO[] itemNOSelectVos = nc.ui.wa.pub.WADelegator.getClassitem().queryItemInfoWithPowerForMutiClasses(list/*new WaGlobalVO[]{arrVOs}*/, false);
				ItemVO[] itemSelectedVOs = nc.ui.wa.pub.WADelegator.getClassitem().queryItemInfoWithPowerForMutiClasses(list/*new WaGlobalVO[]{arrVOs}*/, true);
				if (itemNOSelectVos != null && itemNOSelectVos.length > 0) {
					setLeftData(itemNOSelectVos);
				}

				if (itemSelectedVOs != null && itemSelectedVOs.length > 0) {
					setRigtData(itemSelectedVOs);
				}
//			}
			
		}
	}

}
