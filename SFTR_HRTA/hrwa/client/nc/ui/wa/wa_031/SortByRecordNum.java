package nc.ui.wa.wa_031;

import java.util.Comparator;

import nc.vo.hi.wadoc.PsndocWadocVO;

public class SortByRecordNum implements Comparator {
	
	public int compare(Object o1, Object o2) {
		// TODO Auto-generated method stub
		PsndocWadocVO s1 = (PsndocWadocVO) o1;
		PsndocWadocVO s2 = (PsndocWadocVO) o2;
        if (s1.getRecordnum() > s2.getRecordnum())
        	return 1;
        return -1;
        
	}

}
