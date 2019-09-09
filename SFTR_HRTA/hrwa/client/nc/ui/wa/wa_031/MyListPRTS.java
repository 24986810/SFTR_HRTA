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
 * 此处插入类型说明。
 * 创建日期：(2018-12-20 11:28:26)
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
	String pattern3 = "yyyy年MM月dd日";
	
	String fw1 = "根据沪人[2007]6号文，";
	String fw2 = "沪府办[2015]29号文的规定，";
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
 * 获取单位部门意见
 * @param hasXgwjt 是否有现岗位津贴
 * @param xgwmc 现岗位名称
 * @return
 */
private String getDwbmyj(boolean hasXgwjt,String xgwmc) {
	String dwbmyj = "";
	if (zxrq == null) {
		zxrq = formatDate(paramMap.get("zxrq"));
		zxrq = "从" + zxrq + "起执行";
	}
	
	String fw;
	if (hasXgwjt) {
		// 有现岗位津贴，有26号文。
		fw = fw1 + paramMap.get("dwbmyj") + " " + fw2;
	} else {
		fw = fw1 + paramMap.get("dwbmyj") +" ";
	}
	
	switch(type) {
	case 1: // 正常晋升
		dwbmyj = paramMap.get("zdyt")+fw+zxrq;
		break;
	case 2: // 岗位变动
		dwbmyj = paramMap.get("zdyt")+"从"+formatDate(paramMap.get("qpjbrq"))+"起聘为"+xgwmc+","+fw+zxrq;
		break;
	case 3: // 新录用
		dwbmyj = paramMap.get("zdyt")+formatDate(paramMap.get("qpjbrq"))+"进编，聘"+xgwmc+","+fw+zxrq;
		break;
	default: // 其他
		dwbmyj = paramMap.get("zdyt");
	}
	return dwbmyj;
}

private String getType() {
	switch(type) {
	case 1: // 正常晋升
		return "■正常晋升  □岗位变动  □新录用  □其他";
	case 2: // 岗位变动
		return "□正常晋升  ■岗位变动  □新录用  □其他";
	case 3: // 新录用
		return "□正常晋升  □岗位变动  ■新录用  □其他";
	default: // 其他
		return "□正常晋升  □岗位变动  □新录用  ■其他";
	}
}


/**
 * 将yyyy-MM-dd字符串格式化为yyy年MM月dd日
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
		return "    年  月  日";
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
 * 获取打印需要的人员信息
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
		String[] ghda = new String[wadocVOs.length];	// 工号档案
		itemValueCache.put("ghda", ghda);
		String[] tbrq = new String[wadocVOs.length];	// 填表日期
		itemValueCache.put("tbrq", tbrq);
		String[] xm = new String[wadocVOs.length];		// 姓名
		itemValueCache.put("xm", xm);
		String[] xb = new String[wadocVOs.length];		// 性别
		itemValueCache.put("xb", xb);
		String[] birthdate = new String[wadocVOs.length];// 生日
		itemValueCache.put("birthdate", birthdate);
		String[] cjgzsj = new String[wadocVOs.length];	// 参加工作时间
		itemValueCache.put("cjgzsj", cjgzsj);
		String[] gznx = new String[wadocVOs.length];	// 工作年限
		itemValueCache.put("gznx", gznx);
		String[] type = new String[wadocVOs.length];	// 变动类型
		itemValueCache.put("type", type);
		String[] tzqgwmc = new String[wadocVOs.length];	// 调整前岗位名称
		itemValueCache.put("tzqgwmc", tzqgwmc);
		String[] xgwmc = new String[wadocVOs.length];	// 现岗位名称
		itemValueCache.put("xgwmc", xgwmc);
		String[] tzqgwgz = new String[wadocVOs.length];	// 调整前岗位工资
		itemValueCache.put("tzqgwgz", tzqgwgz);
		String[] xgwgz = new String[wadocVOs.length];	// 现岗位工资
		itemValueCache.put("xgwgz", xgwgz);
		String[] tzqxjgz = new String[wadocVOs.length];	// 调整前薪级工资
		itemValueCache.put("tzqxjgz", tzqxjgz);
		String[] xxjgz = new String[wadocVOs.length];	// 现薪级工资
		itemValueCache.put("xxjgz", xxjgz);
		String[] tzqgwjt = new String[wadocVOs.length];	// 调整前岗位津贴
		itemValueCache.put("tzqgwjt", tzqgwjt);
		String[] xgwjt = new String[wadocVOs.length];	// 现岗位津贴
		itemValueCache.put("xgwjt", xgwjt);
		String[] tzqhj = new String[wadocVOs.length];	// 调整前合计
		itemValueCache.put("tzqhj", tzqhj);
		String[] xhj = new String[wadocVOs.length];		// 现合计
		itemValueCache.put("xhj", xhj);
		String[] zze = new String[wadocVOs.length];		// 增资额
		itemValueCache.put("zze", zze);
		String[] dwbmyj = new String[wadocVOs.length];	// 单位部门意见
		itemValueCache.put("dwbmyj", dwbmyj);
		String[] dwbmgz = new String[wadocVOs.length];	// 单位部门盖章
		itemValueCache.put("dwbmgz", dwbmgz);
		String[] pzdwyj = new String[wadocVOs.length];	// 批准单位意见
		itemValueCache.put("pzdwyj", pzdwyj);
		String[] bz = new String[wadocVOs.length];		// 备注
		itemValueCache.put("bz", bz);
		String[] xpxjmc = new String[wadocVOs.length];		// 现聘薪级
		itemValueCache.put("xpxjmc", xpxjmc);
		String[] ypxjmc = new String[wadocVOs.length];		// 原聘薪级
		itemValueCache.put("ypxjmc", ypxjmc);               
		
		
		String tbrqFormat = formatDate(paramMap.get("tbrq"));
		String pzdwyjFormat = "批准单位盖章：" + formatDate(paramMap.get("pzdwgzrq"));
		String dwbmgzFormat = "单位或部门盖章日期：" + formatDate(paramMap.get("dwbmgzrq"));
		String bzStr = paramMap.get("bz");
		String typeStr = getType();
		/**************************** add xieye 2018-12-27 14:00:00 start ****************************/
		Map<String,String> mapPk_wa_item=queryMapPk_wa_item();
		/**************************** add xieye 2018-12-27 14:00:00 end ****************************/
		for (int i = 0; i < wadocVOs.length; i++) {
			ghda[i] = wadocVOs[i].getPsnCode()+"/"+getPsnInfo(wadocVOs[i].getPk_psndoc()).get("file_code"); // 人员编码+档案号
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
			String name_prefix="";//调整前岗位名称前缀
			String name_postfix="";//调整前岗位名称后缀
			String newName_prefix="";//现岗位名称前缀
			String newName_postfix="";//现岗位名称后缀
			String xjmc_prefix="";//调整前薪级名称前缀
			String xjmc_postfix="";//调整前薪级名称后缀
			String newXjmc_prefix="";//现薪级名称前缀
			String newXjmc_postfix="";//现薪级名称后缀
			/**************************** add xieye 2018-12-26 21:00:00 end ****************************/
			double dtzqhj = 0;
			double dxhj = 0;
			
			ArrayList<PsndocWadocVO> postfix_list = new ArrayList<PsndocWadocVO>();
			ArrayList<PsndocWadocVO> prefix_list = new ArrayList<PsndocWadocVO>();
			ArrayList<PsndocWadocVO> xjmc_postfix_list = new ArrayList<PsndocWadocVO>();
			ArrayList<PsndocWadocVO> gwjt_postfix_list = new ArrayList<PsndocWadocVO>();
			
			for (int j = 0; j < values.length; j++) {
				PsndocWadocVO vo = values[j];
			
				if(mapPk_wa_item.get("工作年限").equals(vo.getPk_wa_item())){
					if (vo.getNmoney() != null) {
						gznx[i] = vo.getNmoney().toDouble().intValue() + "";
					}
				}
				// 取小于等于当前登录日期的数据
				if (mapPk_wa_item.get("岗位级别名称").equals(vo.getPk_wa_item())
						&& work.getBusinessDate().compareTo(vo.getBegindate()) >= 0) {
					postfix_list.add(vo);
				}
				
				if (mapPk_wa_item.get("岗位工资").equals(vo.getPk_wa_item()) ) {
					if(null!=vo.getWa_prmlv_levelname()&&!"".equals(vo.getWa_prmlv_levelname())
							&& work.getBusinessDate().compareTo(vo.getBegindate()) >= 0){
						prefix_list.add(vo);
					}
				}
				
				if (mapPk_wa_item.get("薪级工资").equals(vo.getPk_wa_item())) {
					if(null!=vo.getWa_prmlv_levelname()&&!"".equals(vo.getWa_prmlv_levelname())
							&& work.getBusinessDate().compareTo(vo.getBegindate()) >= 0){
						xjmc_postfix_list.add(vo);
					}
				}
				
				if (mapPk_wa_item.get("岗位津贴").equals(vo.getPk_wa_item())) {
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
				// 岗位级别名称，当前只有一个
				newName_postfix = ""+postfix_list.get(0).getWa_prmlv_levelname()+"";
			}else if(postfix_list.size() >= 2){
//				if(work.getBusinessDate().compareTo(prefix_list.get(0).getBegindate()) == 0){
					name_postfix = ""+postfix_list.get(1).getWa_prmlv_levelname()+"";
//				}
				newName_postfix = ""+postfix_list.get(0).getWa_prmlv_levelname()+"";
				

			}
			
			if(prefix_list.size() == 1){
				// 岗位工资，当前只有一个
				newName_prefix = ""+prefix_list.get(0).getWa_prmlv_levelname()+"";
				if(work.getBusinessDate().compareTo(prefix_list.get(0).getBegindate()) == 0){
					xgwgz[i] = formatDoubleTool(prefix_list.get(0).getNmoney().toDouble().toString());// 现岗位工资
					
					dxhj += prefix_list.get(0).getNmoney().doubleValue(); 
				}
			}else if(prefix_list.size() >= 2){
				newName_prefix = ""+prefix_list.get(0).getWa_prmlv_levelname()+"";
				
				
				// 岗位工资必须与登录时间相等则显示
				if(work.getBusinessDate().compareTo(prefix_list.get(0).getBegindate()) == 0){
					tzqgwgz[i] = formatDoubleTool(prefix_list.get(1).getNmoney().toDouble().toString());// 调整前
					xgwgz[i] = formatDoubleTool(prefix_list.get(0).getNmoney().toDouble().toString());// 现岗位工资
					
					name_prefix = ""+prefix_list.get(1).getWa_prmlv_levelname()+"";
					
					dtzqhj += prefix_list.get(1).getNmoney().doubleValue(); // 调整前合计
					dxhj += prefix_list.get(0).getNmoney().doubleValue(); // 调整后合计
				}
			
			}
			
			if(xjmc_postfix_list.size() == 1){
				// 薪级工资，当前只有一个
				newXjmc_postfix = ""+getNumber(xjmc_postfix_list.get(0).getWa_prmlv_levelname())+"";
				if(work.getBusinessDate().compareTo(xjmc_postfix_list.get(0).getBegindate()) == 0){
					xxjgz[i] = formatDoubleTool(xjmc_postfix_list.get(0).getNmoney().toDouble().toString());
					dxhj += xjmc_postfix_list.get(0).getNmoney().doubleValue(); 
				}
			}else if(xjmc_postfix_list.size() >= 2){
				
				if(work.getBusinessDate().compareTo(xjmc_postfix_list.get(0).getBegindate()) == 0){
					newXjmc_postfix = ""+getNumber(xjmc_postfix_list.get(0).getWa_prmlv_levelname())+"";
					xjmc_postfix = ""+getNumber(xjmc_postfix_list.get(1).getWa_prmlv_levelname())+"";
					
					tzqxjgz[i] = formatDoubleTool(xjmc_postfix_list.get(1).getNmoney().toDouble().toString());// 调整前
					xxjgz[i] = formatDoubleTool(xjmc_postfix_list.get(0).getNmoney().toDouble().toString());
					
					dtzqhj += xjmc_postfix_list.get(1).getNmoney().doubleValue(); 
					dxhj += xjmc_postfix_list.get(0).getNmoney().doubleValue(); 
				}
				
			}
			
			// 岗位津贴
			if(gwjt_postfix_list.size() == 1){
				if(work.getBusinessDate().compareTo(gwjt_postfix_list.get(0).getBegindate()) == 0){
					xgwjt[i] = formatDoubleTool(gwjt_postfix_list.get(0).getNmoney().toDouble().toString());
					dxhj += gwjt_postfix_list.get(0).getNmoney().doubleValue();
				}
				
			}else if(gwjt_postfix_list.size() >= 2){
				if(work.getBusinessDate().compareTo(gwjt_postfix_list.get(0).getBegindate()) == 0){
					tzqgwjt[i] = formatDoubleTool(gwjt_postfix_list.get(1).getNmoney().toDouble().toString());// 调整前
					xgwjt[i] = formatDoubleTool(gwjt_postfix_list.get(0).getNmoney().toDouble().toString());
					
					dxhj += gwjt_postfix_list.get(0).getNmoney().doubleValue();
					dtzqhj += gwjt_postfix_list.get(1).getNmoney().doubleValue(); 
				}
			}
			
			
			
			
			// begin
			String sNewNamepost = "",sNamepost ="";
			if(newName_prefix == null || "".equals(newName_prefix)){// 现任岗位名称前缀
				if(newName_postfix != null || "".equals(newName_postfix)){//后缀
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
			if(name_prefix == null || "".equals(name_prefix)){// 调整前岗位名称
				if(name_postfix != null || "".equals(name_postfix)){//后缀
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
 * 去掉小数点后无用的0
 * @author xieye 2018-12-27 17:11:00
 * @param value
 * @return
 */
private String formatDoubleTool(String value){
	String s=value;
	if(s.indexOf(".") > 0){
		  //正则表达
		  s = s.replaceAll("0+?$", "");//去掉后面无用的零
		  s = s.replaceAll("[.]$", "");//如小数点后面全是零则去掉小数点
	}
	return s;
}

/**
 * 查询薪资项目PK
 * @author xieye 2018-12-27
 * @return
 */
private Map<String,String> queryMapPk_wa_item(){
	Map<String,String> map=new HashMap<String, String>();
	String sql="select vname,pk_wa_item from wa_item where (dr=0 or dr is null) and pk_corp='1002' and vname in ('工作年限','岗位级别名称','岗位工资','薪级工资','岗位津贴') ";
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
 * 查询对应的PsndocWadocVO集合
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
 * 返回所有的数据项对应的内容
 * 参数： 数据项的名字
 * 返回： 数据项对应的内容，只能为 String[]；
 	
 */
public java.lang.String[] getItemValuesByExpress(String itemExpress) {
	ArrayList<String> list = new ArrayList<String>();
	System.out.println(itemExpress);
	try {
		if (itemExpress.equals("defPsncodeDocname")) {
			// 工号/档案号
			return getItemValues("ghda");
		}
		else if (itemExpress.equals("defTbrq")||itemExpress.equals("def_tbrq")) {
			// 填表日期
			return getItemValues("tbrq");
		}
		else if (itemExpress.equals("defName")) {
			// 姓名	
			return getItemValues("xm");
		}
		else if (itemExpress.equals("defSex")) {
			// 性别
			return getItemValues("xb");
		}
		else if (itemExpress.equals("defBirthday")) {
			// 出生年月	
			return getItemValues("birthdate");
		}
		else if (itemExpress.equals("defWorkDate")) {
			// 参加工作年月
			return getItemValues("cjgzsj");
		}
		else if (itemExpress.equals("defWorkYear")) {
			// 工作年限	
			return getItemValues("gznx");
		}
		else if (itemExpress.equals("defType")) {
			// 变动类型
			return getItemValues("type");
		}
		else if (itemExpress.equals("defGwOld")) {
			// 调整前聘任岗位
			return getItemValues("tzqgwmc");
		}
		else if (itemExpress.equals("defGwNew")) {
			// 现聘任岗位	
			return getItemValues("xgwmc");
		}
		else if (itemExpress.equals("defGwgzOld")) {
			// 调整前岗位工资
			return getItemValues("tzqgwgz");
		}
		else if (itemExpress.equals("defGwgzNow")) {
			// 现岗位工资
			return getItemValues("xgwgz");
		}
		else if (itemExpress.equals("defXjgzOld")) {
			// 调整前薪级工资	
			return getItemValues("tzqxjgz");
		}
		else if (itemExpress.equals("defXjgzNow")) {
			// 现薪级工资
			return getItemValues("xxjgz");
		}
		else if (itemExpress.equals("defGwjtOld")) {
			// 调整前岗位津贴
			return getItemValues("tzqgwjt");
		}
		else if (itemExpress.equals("defGwjtNow")) {
			// 现岗位津贴
			return getItemValues("xgwjt");
		}
		else if (itemExpress.equals("defTotalOld")) {
			// 调整前合计
			return getItemValues("tzqhj");
		}
		else if (itemExpress.equals("defTotalNow")) {
			// 现合计
			return getItemValues("xhj");
		}
		else if (itemExpress.equals("defZze")) {
			// 增资额
			return getItemValues("zze");
		}
		else if (itemExpress.equals("defDwbmyj")) {
			// 单位部门意见
			return getItemValues("dwbmyj");
		}
		else if (itemExpress.equals("defDwbmgz")) {
			// 单位部门盖章日期
			return getItemValues("dwbmgz");
		}
		else if (itemExpress.equals("defPzdwyj")) {
			// 批准单位意见
			return getItemValues("pzdwyj");
		}
		else if (itemExpress.equals("defBz")) {
			// 备注
			return getItemValues("bz");
		}else if (itemExpress.equals("defXjNew")) {//zhanghua
			// 现聘薪级	
			return getItemValues("xpxjmc");
		}else if (itemExpress.equals("defXjOld")) {//zhanghua
			// 原聘薪级	
			return getItemValues("ypxjmc");
		}
	} catch (Exception e) {
		
	}
	
	return list.toArray(new String[0]);
}
/*
 *  返回该数据源对应的节点编码			
 */
public String getModuleName() {
	return null;
}
/*
 * 返回该数据项是否为数字项
 * 数字项可参与运算；非数字项只作为字符串常量
 * 如“数量”为数字项、“存货编码”为非数字项	
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
