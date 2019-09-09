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
	
	private boolean negativeSign = true;		//�����Ƿ���ʾ����
	private boolean showRed = false;			//�����Ƿ���ʾ����
	private boolean showThMark = false;			//�Ƿ���ʾǧ��λ
	private boolean showZeroLikeNull = false;	//�Ƿ�����ʾΪ�մ�

	
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
				Logger.warn("������"+ paracode + "��ȡʧ�ܣ�");
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
