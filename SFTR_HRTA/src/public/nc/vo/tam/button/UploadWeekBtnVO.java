/**
 * 
 */
package nc.vo.tam.button;

import java.awt.Event;

import nc.ui.trade.button.IBillButtonVO;
import nc.vo.trade.button.ButtonVO;

/**
 * @author 28729
 *
 */
public class UploadWeekBtnVO implements IBillButtonVO {

	/**
	 * 
	 */
	public UploadWeekBtnVO() {
		// TODO Auto-generated constructor stub
	}

	public ButtonVO getButtonVO() {
		// TODO Auto-generated method stub
		ButtonVO btnVo = new ButtonVO();
		btnVo.setBtnNo(810);
		btnVo.setBtnChinaName("�ϴ��Ű��¼");
		btnVo.setBtnCode("uploadweek");
		btnVo.setBtnName("�ϴ��Ű��¼");
		btnVo.setHintStr("�ϴ��Ű��¼");
		btnVo.setHotKey("L");
		btnVo.setModifiers(Event.CTRL_MASK+Event.ALT_MASK);
		btnVo.setDisplayHotKey("(CTRL+ALT+L)");

		return btnVo;
	}

}
