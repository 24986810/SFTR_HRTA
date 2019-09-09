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
		btnVo.setBtnChinaName("上传排班记录");
		btnVo.setBtnCode("uploadweek");
		btnVo.setBtnName("上传排班记录");
		btnVo.setHintStr("上传排班记录");
		btnVo.setHotKey("L");
		btnVo.setModifiers(Event.CTRL_MASK+Event.ALT_MASK);
		btnVo.setDisplayHotKey("(CTRL+ALT+L)");

		return btnVo;
	}

}
