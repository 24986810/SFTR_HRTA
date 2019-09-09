package nc.ui.pub.bill;

import nc.bs.logging.Logger;
import nc.ui.pub.ClientEnvironment;
import nc.ui.pub.para.SysInitBO_Client;
import nc.vo.pub.BusinessException;

public class BillItemNumberFormat {
	
	private int decimalDigits = IBillItem.DEFAULT_DECIMAL_DIGITS;
	private Double minValue = null;
	private Double maxValue = null;
	
	private String digitsPara = null;
	
	private boolean negativeSign = true;		//负数是否显示符号
	private boolean showRed = false;			//负数是否显示红字
	private boolean showThMark = false;			//是否显示千分位
	private boolean showZeroLikeNull = false;	//是否将零显示为空串

	
	public BillItemNumberFormat() {
		super();
	}
	
	public int getDecimalDigits() {
		if(getDigitsPara() != null)
			decimalDigits = getSysParaSet(getDigitsPara());
		
		return decimalDigits;
	}
	public void setDecimalDigits(int decimalDigits) {
		this.decimalDigits = decimalDigits;
	}
	public Double getMaxValue() {
		return maxValue;
	}
	public void setMaxValue(Double maxValue) {
		this.maxValue = maxValue;
	}
	public Double getMinValue() {
		return minValue;
	}
	public void setMinValue(Double minValue) {
		this.minValue = minValue;
	}
	public boolean isNegativeSign() {
		return negativeSign;
	}
	public void setNegativeSign(boolean negativeSign) {
		this.negativeSign = negativeSign;
	}
	public boolean isShowRed() {
		return showRed;
	}
	public void setShowRed(boolean showRed) {
		this.showRed = showRed;
	}
	public boolean isShowThMark() {
		return showThMark;
	}
	public void setShowThMark(boolean showThMark) {
		this.showThMark = showThMark;
	}
	public boolean isShowZeroLikeNull() {
		return showZeroLikeNull;
	}
	public void setShowZeroLikeNull(boolean showZeroLikeNull) {
		this.showZeroLikeNull = showZeroLikeNull;
	}

	public String getDigitsPara() {
		return digitsPara;
	}

	public void setDigitsPara(String digitsPara) {
		this.digitsPara = digitsPara;
	}
	
	protected int getSysParaSet(String paracode) {
		
		int decimalDigits = IBillItem.DEFAULT_DECIMAL_DIGITS;
		if(paracode != null){
			try {
				Integer para = SysInitBO_Client.getParaInt(ClientEnvironment.getInstance().getCorporation().getPk_corp(), paracode);
				decimalDigits = para.intValue();
			} catch (BusinessException e) {
				Logger.warn("参数："+ paracode + "获取失败！");
			}
		}		
		return decimalDigits;
	}

	@Override
	public String toString() {
		
		StringBuffer s = new StringBuffer();
		
		s.append(decimalDigits);
		s.append(",");
		s.append(maxValue);
		s.append(",");
		s.append(minValue);
		
		
		return s.toString();
	}

}
