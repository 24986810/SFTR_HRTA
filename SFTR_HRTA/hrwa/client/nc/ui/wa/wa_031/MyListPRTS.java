package nc.ui.wa.wa_031;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nc.bs.framework.common.NCLocator;
import nc.itf.uap.IUAPQueryBS;
import nc.jdbc.framework.SQLParameter;
import nc.jdbc.framework.processor.BeanListProcessor;
import nc.jdbc.framework.processor.MapListProcessor;
import nc.ui.pub.ClientEnvironment;
import nc.vo.hi.wadoc.PsndocWadocMainVO;
import nc.vo.hi.wadoc.PsndocWadocVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDouble;
/**
 * �˴���������˵����
 * �������ڣ�(2018-12-20 11:28:26)
 */
public class MyListPRTS implements nc.ui.pub.print.IDataSource {
	PsndocWadocMainVO[] wadocVOs;
	HashMap<String, String> paramMap;
	HashMap<String, HashMap<String,String>> psnMap;
	HashMap<String, String[]> itemValueCache;
	int type;
	SimpleDateFormat sdf = new SimpleDateFormat();
	String pattern1 = "yyyy-MM-dd";
	String pattern2 = "yyyy-MM";
	String pattern3 = "yyyy��MM��dd��";
	
	String fw1 = "���ݻ���[2007]6���ģ�";
	String fw2 = "������[2015]29���ĵĹ涨��";
	String zxrq = null;
	
	public static final int NOMAL = 1;
	public static final int GWBG = 2;
	public static final int NEWLY = 3;
	public static final int OTHER = 4;
	

public MyListPRTS(PsndocWadocMainVO[] wadocVOs, HashMap<String, String> paramMap, int type) {
	super();
	this.wadocVOs = wadocVOs;
	this.paramMap = paramMap;
	this.type = type;
}

public java.lang.String[] getAllDataItemExpress() {
	
	return null;
}
public java.lang.String[] getAllDataItemNames() {	
	return null;
}

public java.lang.String[] getDependentItemExpressByExpress(String itemName) {
	return null;
}

/**
 * ��ȡ��λ�������
 * @param hasXgwjt �Ƿ����ָ�λ����
 * @param xgwmc �ָ�λ����
 * @return
 */
private String getDwbmyj(boolean hasXgwjt,String xgwmc) {
	String dwbmyj = "";
	if (zxrq == null) {
		zxrq = formatDate(paramMap.get("zxrq"));
		zxrq = "��" + zxrq + "��ִ��";
	}
	
	String fw;
	if (hasXgwjt) {
		// ���ָ�λ��������26���ġ�
		fw = fw1 + paramMap.get("dwbmyj") + " " + fw2;
	} else {
		fw = fw1 + paramMap.get("dwbmyj") +" ";
	}
	
	switch(type) {
	case 1: // ��������
		dwbmyj = paramMap.get("zdyt")+fw+zxrq;
		break;
	case 2: // ��λ�䶯
		dwbmyj = paramMap.get("zdyt")+"��"+formatDate(paramMap.get("qpjbrq"))+"��ƸΪ"+xgwmc+","+fw+zxrq;
		break;
	case 3: // ��¼��
		dwbmyj = paramMap.get("zdyt")+formatDate(paramMap.get("qpjbrq"))+"���࣬Ƹ"+xgwmc+","+fw+zxrq;
		break;
	default: // ����
		dwbmyj = paramMap.get("zdyt");
	}
	return dwbmyj;
}

private String getType() {
	switch(type) {
	case 1: // ��������
		return "����������  ����λ�䶯  ����¼��  ������";
	case 2: // ��λ�䶯
		return "����������  ����λ�䶯  ����¼��  ������";
	case 3: // ��¼��
		return "����������  ����λ�䶯  ����¼��  ������";
	default: // ����
		return "����������  ����λ�䶯  ����¼��  ������";
	}
}


/**
 * ��yyyy-MM-dd�ַ�����ʽ��Ϊyyy��MM��dd��
 * @param dateStr
 * @return
 */
private String formatDate(String dateStr) {
	try {
		sdf.applyPattern(pattern1);
		Date date = sdf.parse(dateStr);
		sdf.applyPattern(pattern3);
		dateStr = sdf.format(date);
	} catch (Exception e) {
		return "    ��  ��  ��";
	}
	return dateStr;
}

private String formatNY(String dateStr) {
	try {
		sdf.applyPattern(pattern1);
		Date date = sdf.parse(dateStr);
		sdf.applyPattern(pattern2);
		dateStr = sdf.format(date);
	} catch (Exception e) {
		return "";
	}
	return dateStr;
}


/**
 * ��ȡ��ӡ��Ҫ����Ա��Ϣ
 * @param psndoc
 * @return
 */
@SuppressWarnings("unchecked")
private HashMap<String,String> getPsnInfo(String psndoc) {
	if (psnMap == null) {
		psnMap = new HashMap<String, HashMap<String,String>>();
		StringBuilder sb = new StringBuilder("select bd_psndoc.pk_psndoc,bd_psnbasdoc.sex,bd_psnbasdoc.birthdate,bd_psnbasdoc.joinworkdate,bd_psnbasdoc.file_code");
		sb.append(" from bd_psnbasdoc,bd_psndoc where bd_psndoc.pk_psnbasdoc = bd_psnbasdoc.pk_psnbasdoc");
		if (wadocVOs.length < 1000) {
			sb.append(" and bd_psndoc.pk_psndoc in (");
			for (PsndocWadocMainVO wadoc : wadocVOs) {
				sb.append("'"+wadoc.getPk_psndoc()+"',");
			}
			sb.deleteCharAt(sb.length()-1).append(")");
		}
		IUAPQueryBS bs = NCLocator.getInstance().lookup(IUAPQueryBS.class);
		List<HashMap<String,String>> queryMapList;
		try {
			queryMapList = (List<HashMap<String,String>> ) bs.executeQuery(sb.toString(), new MapListProcessor());
			for (HashMap<String, String> queryMap : queryMapList) {
				psnMap.put(queryMap.get("pk_psndoc"), queryMap);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	return psnMap.get(psndoc);
}

private String[] getItemValues(String key) {
	if (itemValueCache == null) {
		itemValueCache = new HashMap<String, String[]>();
		String[] ghda = new String[wadocVOs.length];	// ���ŵ���
		itemValueCache.put("ghda", ghda);
		String[] tbrq = new String[wadocVOs.length];	// �������
		itemValueCache.put("tbrq", tbrq);
		String[] xm = new String[wadocVOs.length];		// ����
		itemValueCache.put("xm", xm);
		String[] xb = new String[wadocVOs.length];		// �Ա�
		itemValueCache.put("xb", xb);
		String[] birthdate = new String[wadocVOs.length];// ����
		itemValueCache.put("birthdate", birthdate);
		String[] cjgzsj = new String[wadocVOs.length];	// �μӹ���ʱ��
		itemValueCache.put("cjgzsj", cjgzsj);
		String[] gznx = new String[wadocVOs.length];	// ��������
		itemValueCache.put("gznx", gznx);
		String[] type = new String[wadocVOs.length];	// �䶯����
		itemValueCache.put("type", type);
		String[] tzqgwmc = new String[wadocVOs.length];	// ����ǰ��λ����
		itemValueCache.put("tzqgwmc", tzqgwmc);
		String[] xgwmc = new String[wadocVOs.length];	// �ָ�λ����
		itemValueCache.put("xgwmc", xgwmc);
		String[] tzqgwgz = new String[wadocVOs.length];	// ����ǰ��λ����
		itemValueCache.put("tzqgwgz", tzqgwgz);
		String[] xgwgz = new String[wadocVOs.length];	// �ָ�λ����
		itemValueCache.put("xgwgz", xgwgz);
		String[] tzqxjgz = new String[wadocVOs.length];	// ����ǰн������
		itemValueCache.put("tzqxjgz", tzqxjgz);
		String[] xxjgz = new String[wadocVOs.length];	// ��н������
		itemValueCache.put("xxjgz", xxjgz);
		String[] tzqgwjt = new String[wadocVOs.length];	// ����ǰ��λ����
		itemValueCache.put("tzqgwjt", tzqgwjt);
		String[] xgwjt = new String[wadocVOs.length];	// �ָ�λ����
		itemValueCache.put("xgwjt", xgwjt);
		String[] tzqhj = new String[wadocVOs.length];	// ����ǰ�ϼ�
		itemValueCache.put("tzqhj", tzqhj);
		String[] xhj = new String[wadocVOs.length];		// �ֺϼ�
		itemValueCache.put("xhj", xhj);
		String[] zze = new String[wadocVOs.length];		// ���ʶ�
		itemValueCache.put("zze", zze);
		String[] dwbmyj = new String[wadocVOs.length];	// ��λ�������
		itemValueCache.put("dwbmyj", dwbmyj);
		String[] dwbmgz = new String[wadocVOs.length];	// ��λ���Ÿ���
		itemValueCache.put("dwbmgz", dwbmgz);
		String[] pzdwyj = new String[wadocVOs.length];	// ��׼��λ���
		itemValueCache.put("pzdwyj", pzdwyj);
		String[] bz = new String[wadocVOs.length];		// ��ע
		itemValueCache.put("bz", bz);
		String[] xpxjmc = new String[wadocVOs.length];		// ��Ƹн��
		itemValueCache.put("xpxjmc", xpxjmc);
		String[] ypxjmc = new String[wadocVOs.length];		// ԭƸн��
		itemValueCache.put("ypxjmc", ypxjmc);               
		
		
		String tbrqFormat = formatDate(paramMap.get("tbrq"));
		String pzdwyjFormat = "��׼��λ���£�" + formatDate(paramMap.get("pzdwgzrq"));
		String dwbmgzFormat = "��λ���Ÿ������ڣ�" + formatDate(paramMap.get("dwbmgzrq"));
		String bzStr = paramMap.get("bz");
		String typeStr = getType();
		/**************************** add xieye 2018-12-27 14:00:00 start ****************************/
		Map<String,String> mapPk_wa_item=queryMapPk_wa_item();
		/**************************** add xieye 2018-12-27 14:00:00 end ****************************/
		for (int i = 0; i < wadocVOs.length; i++) {
			ghda[i] = wadocVOs[i].getPsnCode()+"/"+getPsnInfo(wadocVOs[i].getPk_psndoc()).get("file_code"); // ��Ա����+������
			tbrq[i] = tbrqFormat;
			xm[i] = wadocVOs[i].getPsnName();
			xb[i] = getPsnInfo(wadocVOs[i].getPk_psndoc()).get("sex");
			birthdate[i] = formatNY(getPsnInfo(wadocVOs[i].getPk_psndoc()).get("birthdate"));
			cjgzsj[i] = formatNY(getPsnInfo(wadocVOs[i].getPk_psndoc()).get("joinworkdate"));
			type[i] = typeStr;
			gznx[i] = "";
			tzqgwmc[i] = "";
			xgwmc[i] = "";
			tzqgwgz[i] = "";
			xgwgz[i] = "";
			tzqxjgz[i] = "";
			xxjgz[i] = "";
			tzqgwjt[i] = "";
			xgwjt[i] = "";
			dwbmgz[i] = dwbmgzFormat;
			pzdwyj[i] = pzdwyjFormat;
			bz[i] = bzStr;
			
			
			PsndocWadocVO[] values = wadocVOs[i].getSubVOs();
			/**************************** add xieye 2018-12-26 24:00:00 start ****************************/
			ClientEnvironment work = ClientEnvironment.getInstance();
			String name_prefix="";//����ǰ��λ����ǰ׺
			String name_postfix="";//����ǰ��λ���ƺ�׺
			String newName_prefix="";//�ָ�λ����ǰ׺
			String newName_postfix="";//�ָ�λ���ƺ�׺
			String xjmc_prefix="";//����ǰн������ǰ׺
			String xjmc_postfix="";//����ǰн�����ƺ�׺
			String newXjmc_prefix="";//��н������ǰ׺
			String newXjmc_postfix="";//��н�����ƺ�׺
			/**************************** add xieye 2018-12-26 21:00:00 end ****************************/
			double dtzqhj = 0;
			double dxhj = 0;
			
			ArrayList<PsndocWadocVO> postfix_list = new ArrayList<PsndocWadocVO>();
			ArrayList<PsndocWadocVO> prefix_list = new ArrayList<PsndocWadocVO>();
			ArrayList<PsndocWadocVO> xjmc_postfix_list = new ArrayList<PsndocWadocVO>();
			ArrayList<PsndocWadocVO> gwjt_postfix_list = new ArrayList<PsndocWadocVO>();
			
			for (int j = 0; j < values.length; j++) {
				PsndocWadocVO vo = values[j];
			
				if(mapPk_wa_item.get("��������").equals(vo.getPk_wa_item())){
					if (vo.getNmoney() != null) {
						gznx[i] = vo.getNmoney().toDouble().intValue() + "";
					}
				}
				// ȡС�ڵ��ڵ�ǰ��¼���ڵ�����
				if (mapPk_wa_item.get("��λ��������").equals(vo.getPk_wa_item())
						&& work.getBusinessDate().compareTo(vo.getBegindate()) >= 0) {
					postfix_list.add(vo);
				}
				
				if (mapPk_wa_item.get("��λ����").equals(vo.getPk_wa_item()) ) {
					if(null!=vo.getWa_prmlv_levelname()&&!"".equals(vo.getWa_prmlv_levelname())
							&& work.getBusinessDate().compareTo(vo.getBegindate()) >= 0){
						prefix_list.add(vo);
					}
				}
				
				if (mapPk_wa_item.get("н������").equals(vo.getPk_wa_item())) {
					if(null!=vo.getWa_prmlv_levelname()&&!"".equals(vo.getWa_prmlv_levelname())
							&& work.getBusinessDate().compareTo(vo.getBegindate()) >= 0){
						xjmc_postfix_list.add(vo);
					}
				}
				
				if (mapPk_wa_item.get("��λ����").equals(vo.getPk_wa_item())) {
					if(null!=vo.getWa_prmlv_levelname()&&!"".equals(vo.getWa_prmlv_levelname())
							&& work.getBusinessDate().compareTo(vo.getBegindate()) >= 0){
						gwjt_postfix_list.add(vo);
					}
				}
			}
			
			Collections.sort(postfix_list,new SortByRecordNum());
			Collections.sort(prefix_list,new SortByRecordNum());
			Collections.sort(xjmc_postfix_list,new SortByRecordNum());
			Collections.sort(gwjt_postfix_list,new SortByRecordNum());
			
			if(postfix_list.size() == 1){
				// ��λ�������ƣ���ǰֻ��һ��
				newName_postfix = ""+postfix_list.get(0).getWa_prmlv_levelname()+"";
			}else if(postfix_list.size() >= 2){
//				if(work.getBusinessDate().compareTo(prefix_list.get(0).getBegindate()) == 0){
					name_postfix = ""+postfix_list.get(1).getWa_prmlv_levelname()+"";
//				}
				newName_postfix = ""+postfix_list.get(0).getWa_prmlv_levelname()+"";
				

			}
			
			if(prefix_list.size() == 1){
				// ��λ���ʣ���ǰֻ��һ��
				newName_prefix = ""+prefix_list.get(0).getWa_prmlv_levelname()+"";
				if(work.getBusinessDate().compareTo(prefix_list.get(0).getBegindate()) == 0){
					xgwgz[i] = formatDoubleTool(prefix_list.get(0).getNmoney().toDouble().toString());// �ָ�λ����
					
					dxhj += prefix_list.get(0).getNmoney().doubleValue(); 
				}
			}else if(prefix_list.size() >= 2){
				newName_prefix = ""+prefix_list.get(0).getWa_prmlv_levelname()+"";
				
				
				// ��λ���ʱ������¼ʱ���������ʾ
				if(work.getBusinessDate().compareTo(prefix_list.get(0).getBegindate()) == 0){
					tzqgwgz[i] = formatDoubleTool(prefix_list.get(1).getNmoney().toDouble().toString());// ����ǰ
					xgwgz[i] = formatDoubleTool(prefix_list.get(0).getNmoney().toDouble().toString());// �ָ�λ����
					
					name_prefix = ""+prefix_list.get(1).getWa_prmlv_levelname()+"";
					
					dtzqhj += prefix_list.get(1).getNmoney().doubleValue(); // ����ǰ�ϼ�
					dxhj += prefix_list.get(0).getNmoney().doubleValue(); // ������ϼ�
				}
			
			}
			
			if(xjmc_postfix_list.size() == 1){
				// н�����ʣ���ǰֻ��һ��
				newXjmc_postfix = ""+getNumber(xjmc_postfix_list.get(0).getWa_prmlv_levelname())+"";
				if(work.getBusinessDate().compareTo(xjmc_postfix_list.get(0).getBegindate()) == 0){
					xxjgz[i] = formatDoubleTool(xjmc_postfix_list.get(0).getNmoney().toDouble().toString());
					dxhj += xjmc_postfix_list.get(0).getNmoney().doubleValue(); 
				}
			}else if(xjmc_postfix_list.size() >= 2){
				
				if(work.getBusinessDate().compareTo(xjmc_postfix_list.get(0).getBegindate()) == 0){
					newXjmc_postfix = ""+getNumber(xjmc_postfix_list.get(0).getWa_prmlv_levelname())+"";
					xjmc_postfix = ""+getNumber(xjmc_postfix_list.get(1).getWa_prmlv_levelname())+"";
					
					tzqxjgz[i] = formatDoubleTool(xjmc_postfix_list.get(1).getNmoney().toDouble().toString());// ����ǰ
					xxjgz[i] = formatDoubleTool(xjmc_postfix_list.get(0).getNmoney().toDouble().toString());
					
					dtzqhj += xjmc_postfix_list.get(1).getNmoney().doubleValue(); 
					dxhj += xjmc_postfix_list.get(0).getNmoney().doubleValue(); 
				}
				
			}
			
			// ��λ����
			if(gwjt_postfix_list.size() == 1){
				if(work.getBusinessDate().compareTo(gwjt_postfix_list.get(0).getBegindate()) == 0){
					xgwjt[i] = formatDoubleTool(gwjt_postfix_list.get(0).getNmoney().toDouble().toString());
					dxhj += gwjt_postfix_list.get(0).getNmoney().doubleValue();
				}
				
			}else if(gwjt_postfix_list.size() >= 2){
				if(work.getBusinessDate().compareTo(gwjt_postfix_list.get(0).getBegindate()) == 0){
					tzqgwjt[i] = formatDoubleTool(gwjt_postfix_list.get(1).getNmoney().toDouble().toString());// ����ǰ
					xgwjt[i] = formatDoubleTool(gwjt_postfix_list.get(0).getNmoney().toDouble().toString());
					
					dxhj += gwjt_postfix_list.get(0).getNmoney().doubleValue();
					dtzqhj += gwjt_postfix_list.get(1).getNmoney().doubleValue(); 
				}
			}
			
			
			
			
			// begin
			String sNewNamepost = "",sNamepost ="";
			if(newName_prefix == null || "".equals(newName_prefix)){// ���θ�λ����ǰ׺
				if(newName_postfix != null || "".equals(newName_postfix)){//��׺
					sNewNamepost = newName_postfix;
				}
			}else if(newName_postfix == null || "".equals(newName_postfix)){
				if(newName_prefix != null || "".equals(newName_prefix)){
					sNewNamepost = newName_prefix;
				}
			}else{
				sNewNamepost = newName_postfix + "("+newName_prefix+")";
			}
			/////////////////////////////////////////////////
			if(name_prefix == null || "".equals(name_prefix)){// ����ǰ��λ����
				if(name_postfix != null || "".equals(name_postfix)){//��׺
					sNamepost = name_postfix;
				}
			}else if(name_postfix == null || "".equals(name_postfix)){
				if(name_prefix != null || "".equals(name_prefix)){
					sNamepost = name_prefix;
				}
			}else{
				sNamepost = name_postfix + "("+name_prefix+")";
			}
			// end
			
			tzqgwmc[i] = sNamepost;
			xgwmc[i] = sNewNamepost;
			ypxjmc[i]=xjmc_postfix;
			xpxjmc[i] = newXjmc_postfix;
			tzqhj[i] = dtzqhj>0 ? formatDoubleTool(dtzqhj+"") : "";
			xhj[i] = dxhj>0 ? formatDoubleTool(dxhj+"") : "";
			zze[i] = formatDoubleTool((dxhj-dtzqhj) + "");
			
			boolean isExist = false;
			if(xgwjt[i] != null){
				if(!"".equals(xgwjt[i])){
					isExist=true;//zhanghua
				}
				
			}
			dwbmyj[i] = getDwbmyj(isExist, xgwmc[i]);
		}
	}
	
	return itemValueCache.get(key);
}

/**
 * ȥ��С��������õ�0
 * @author xieye 2018-12-27 17:11:00
 * @param value
 * @return
 */
private String formatDoubleTool(String value){
	String s=value;
	if(s.indexOf(".") > 0){
		  //������
		  s = s.replaceAll("0+?$", "");//ȥ���������õ���
		  s = s.replaceAll("[.]$", "");//��С�������ȫ������ȥ��С����
	}
	return s;
}

/**
 * ��ѯн����ĿPK
 * @author xieye 2018-12-27
 * @return
 */
private Map<String,String> queryMapPk_wa_item(){
	Map<String,String> map=new HashMap<String, String>();
	String sql="select vname,pk_wa_item from wa_item where (dr=0 or dr is null) and pk_corp='1002' and vname in ('��������','��λ��������','��λ����','н������','��λ����') ";
	IUAPQueryBS query = NCLocator.getInstance().lookup(IUAPQueryBS.class);
	List<HashMap<String,String>> queryMapList;
	try {
		queryMapList = (List<HashMap<String,String>> ) query.executeQuery(sql, new MapListProcessor());
		for (HashMap<String, String> queryMap : queryMapList) {
			map.put(queryMap.get("vname"), queryMap.get("pk_wa_item"));
		}
	} catch (BusinessException e) {
		e.printStackTrace();
	}
	return map;
}

/**
 * ��ѯ��Ӧ��PsndocWadocVO����
 * @author xieye 2018-12-27
 * @param pk_psndoc
 */
private List<PsndocWadocVO> queryPsndocWadocVOs(String pk_psndoc) {
	List<PsndocWadocVO> pdocList=null;
	try {
		IUAPQueryBS query = NCLocator.getInstance().lookup(IUAPQueryBS.class);
		String sql="select * from hi_psndoc_wadoc where (dr=0 or dr is null) and lastflag='Y' and pk_psndoc=? ";
		SQLParameter parameter=new SQLParameter();
		parameter.addParam(pk_psndoc);
		pdocList=(List<PsndocWadocVO>)query.executeQuery(sql, parameter, new BeanListProcessor(PsndocWadocVO.class));
	} catch (BusinessException e) {
		e.printStackTrace();
	}
	return pdocList;
}

/*
 * �������е��������Ӧ������
 * ������ �����������
 * ���أ� �������Ӧ�����ݣ�ֻ��Ϊ String[]��
 	
 */
public java.lang.String[] getItemValuesByExpress(String itemExpress) {
	ArrayList<String> list = new ArrayList<String>();
	System.out.println(itemExpress);
	try {
		if (itemExpress.equals("defPsncodeDocname")) {
			// ����/������
			return getItemValues("ghda");
		}
		else if (itemExpress.equals("defTbrq")||itemExpress.equals("def_tbrq")) {
			// �������
			return getItemValues("tbrq");
		}
		else if (itemExpress.equals("defName")) {
			// ����	
			return getItemValues("xm");
		}
		else if (itemExpress.equals("defSex")) {
			// �Ա�
			return getItemValues("xb");
		}
		else if (itemExpress.equals("defBirthday")) {
			// ��������	
			return getItemValues("birthdate");
		}
		else if (itemExpress.equals("defWorkDate")) {
			// �μӹ�������
			return getItemValues("cjgzsj");
		}
		else if (itemExpress.equals("defWorkYear")) {
			// ��������	
			return getItemValues("gznx");
		}
		else if (itemExpress.equals("defType")) {
			// �䶯����
			return getItemValues("type");
		}
		else if (itemExpress.equals("defGwOld")) {
			// ����ǰƸ�θ�λ
			return getItemValues("tzqgwmc");
		}
		else if (itemExpress.equals("defGwNew")) {
			// ��Ƹ�θ�λ	
			return getItemValues("xgwmc");
		}
		else if (itemExpress.equals("defGwgzOld")) {
			// ����ǰ��λ����
			return getItemValues("tzqgwgz");
		}
		else if (itemExpress.equals("defGwgzNow")) {
			// �ָ�λ����
			return getItemValues("xgwgz");
		}
		else if (itemExpress.equals("defXjgzOld")) {
			// ����ǰн������	
			return getItemValues("tzqxjgz");
		}
		else if (itemExpress.equals("defXjgzNow")) {
			// ��н������
			return getItemValues("xxjgz");
		}
		else if (itemExpress.equals("defGwjtOld")) {
			// ����ǰ��λ����
			return getItemValues("tzqgwjt");
		}
		else if (itemExpress.equals("defGwjtNow")) {
			// �ָ�λ����
			return getItemValues("xgwjt");
		}
		else if (itemExpress.equals("defTotalOld")) {
			// ����ǰ�ϼ�
			return getItemValues("tzqhj");
		}
		else if (itemExpress.equals("defTotalNow")) {
			// �ֺϼ�
			return getItemValues("xhj");
		}
		else if (itemExpress.equals("defZze")) {
			// ���ʶ�
			return getItemValues("zze");
		}
		else if (itemExpress.equals("defDwbmyj")) {
			// ��λ�������
			return getItemValues("dwbmyj");
		}
		else if (itemExpress.equals("defDwbmgz")) {
			// ��λ���Ÿ�������
			return getItemValues("dwbmgz");
		}
		else if (itemExpress.equals("defPzdwyj")) {
			// ��׼��λ���
			return getItemValues("pzdwyj");
		}
		else if (itemExpress.equals("defBz")) {
			// ��ע
			return getItemValues("bz");
		}else if (itemExpress.equals("defXjNew")) {//zhanghua
			// ��Ƹн��	
			return getItemValues("xpxjmc");
		}else if (itemExpress.equals("defXjOld")) {//zhanghua
			// ԭƸн��	
			return getItemValues("ypxjmc");
		}
	} catch (Exception e) {
		
	}
	
	return list.toArray(new String[0]);
}
/*
 *  ���ظ�����Դ��Ӧ�Ľڵ����			
 */
public String getModuleName() {
	return null;
}
/*
 * ���ظ��������Ƿ�Ϊ������
 * ������ɲ������㣻��������ֻ��Ϊ�ַ�������
 * �硰������Ϊ�������������롱Ϊ��������	
 */
public boolean isNumber(String itemExpress) {	
	return false;
}

public String getNumber(String xjmc){
	String regEx="[^0-9]";  
	Pattern p = Pattern.compile(regEx);
	Matcher m = p.matcher(xjmc); 
	
	return m.replaceAll("").trim();
}
}
