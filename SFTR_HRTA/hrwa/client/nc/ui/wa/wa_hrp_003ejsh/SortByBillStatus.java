package nc.ui.wa.wa_hrp_003ejsh;

import java.util.Comparator;

import nc.vo.wa.wa_hrp_002.PsnClassItemHVO;

public class SortByBillStatus implements Comparator {

	public int compare(Object arg0, Object arg1) {
		// TODO Auto-generated method stub
		PsnClassItemHVO p0= (PsnClassItemHVO) arg0;
		PsnClassItemHVO p1= (PsnClassItemHVO) arg1;
		
		if(!p0.getVapprovenote().equals(p1.getVapprovenote())){
			if(p0.getVapprovenote().indexOf("²»Í¨¹ý") != -1){
				return -1;
			}
		}
		
		return 1;
	}

}
