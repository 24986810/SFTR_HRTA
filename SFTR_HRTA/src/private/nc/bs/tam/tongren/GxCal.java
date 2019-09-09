package nc.bs.tam.tongren;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import org.apache.commons.lang.StringUtils;

import nc.bs.dao.BaseDAO;
import nc.bs.pub.taskcenter.BgWorkingContext;
import nc.bs.pub.taskcenter.IBackgroundWorkPlugin;
import nc.bs.trade.comsave.BillSave;
import nc.jdbc.framework.processor.BeanListProcessor;
import nc.jdbc.framework.processor.MapListProcessor;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDouble;
import nc.vo.tam.tongren010.GxBVO;
import nc.vo.tam.tongren010.GxHVO;
import nc.vo.tam.tongren017.GxjcszVO;
import nc.vo.tam.tongren018.GxsxszVO;
import nc.vo.trade.pub.HYBillVO;

public class GxCal implements IBackgroundWorkPlugin {

	BaseDAO dao;
	
	private BaseDAO getDao() {
		if (null == dao) {
			dao = new BaseDAO();
		}
		return dao;
	}

	@SuppressWarnings("unchecked")
	public String executeTask(BgWorkingContext arg0) throws BusinessException {
		// ÿ��һ��һ��ִ��
		int year = Calendar.getInstance().get(Calendar.YEAR);
		int month = Calendar.getInstance().get(Calendar.MONTH)+1;
		int day = Calendar.getInstance().get(Calendar.DATE);
		if (month != 1) {
			return "����1�£���ִ�С�";
		}
		
		// ��ȡ���ݻ�������
		String sql = "select * from tam_gxjcsz where nvl(dr,0)=0 order by iyearstart desc";
		ArrayList<GxjcszVO> jcszVOs = (ArrayList<GxjcszVO>) getDao().executeQuery(sql,
				new BeanListProcessor(GxjcszVO.class));
		// ��ȡ����ʧЧ����
		sql = "select * from tam_gxsxsz where nvl(dr,0)=0 order by vattendtype,iworkyearstart desc";
		ArrayList<GxsxszVO> sxszVOs = (ArrayList<GxsxszVO>) getDao().executeQuery(sql,
				new BeanListProcessor(GxsxszVO.class));
		
		// ɾ���Ѵ��ڵı��깫��ά����Ϣ
//		sql = "delete from tam_gx_h where vyear='"+year+"'";
		sql="update tam_gx_h set dr=1 where nvl(dr,0)=0 and vyear='"+year+"'";
		getDao().executeUpdate(sql);
//		sql = "delete from tam_gx_b where vyear='"+year+"'";
		sql="update tam_gx_b set dr=1 where nvl(dr,0)=0 and vyear='"+year+"'";
		getDao().executeUpdate(sql);
		
		// ��ȡ��Ա��Ϣ��������ȿ�����Ϣ
		StringBuilder sbh = new StringBuilder();
		sbh.append("select bd_psnbasdoc.psnname,bd_psndoc.pk_psndoc,bd_corp.pk_corp,nvl(bd_psndoc.groupdef15, 'N') fsj,nvl(bd_psnbasdoc.basgroupdef3, 'N') gxzm");
		/****************************** update xieye 2018-12-28  start ******************************/
//		sbh.append(",substr(bd_psnbasdoc.joinsysdate,1,4) joinsysdate,substr(bd_psnbasdoc.joinworkdate,1,4) joinworkdate,bd_psnbasdoc.basgroupdef7 glxz");
		sbh.append(",bd_psnbasdoc.joinsysdate,bd_psnbasdoc.joinworkdate,bd_psnbasdoc.basgroupdef7 glxz");
		//sbh.append(",bd_psnbasdoc.joinsysdate joinsysdate,bd_psnbasdoc.joinworkdate joinworkdate,bd_psnbasdoc.basgroupdef7 glxz");
		/****************************** update xieye 2018-12-28  end ******************************/
		StringBuilder sbb = new StringBuilder();
		sbb.append(" from hi_psndoc_deptchg");
		sbb.append(" inner join bd_psndoc");
		sbb.append(" on hi_psndoc_deptchg.pk_psndoc = bd_psndoc.pk_psndoc");
		sbb.append(" inner join bd_psnbasdoc");
		sbb.append(" on hi_psndoc_deptchg.pk_psnbasdoc = bd_psnbasdoc.pk_psnbasdoc");
		sbb.append(" inner join bd_corp");
		sbb.append(" on hi_psndoc_deptchg.pk_corp = bd_corp.pk_corp");
		sbb.append(" inner join bd_psncl");
		sbb.append(" on hi_psndoc_deptchg.pk_psncl = bd_psncl.pk_psncl");

		String ij = null;
		for (int i = 0; i < sxszVOs.size(); i++) {
			GxsxszVO gxsxszVO = sxszVOs.get(i);
			String[] attendtypes = gxsxszVO.getVattendtype().split(",");
			for (int j = 0; j < attendtypes.length; j++) {
				ij = new String(i+""+j);
				sbh.append(",t" + ij + ".lbmc lbmc" + ij + ",t" + ij + ".sum sum" + ij);
				sbb.append(" left join (select pk_psndoc, tb.lbmc, count(1) sum");
				sbb.append(" from trtam_paiban_b b");
				sbb.append(" inner join tbm_bclb tb");
				sbb.append(" on b.pk_bb = tb.pk_bclbid");
				sbb.append(" where nvl(b.dr, 0) = 0");
				sbb.append(" and b.ddate like '" + (year-1) + "%'");
				sbb.append(" and tb.lbmc = '" + attendtypes[j] + "'");
				sbb.append(" group by pk_psndoc, tb.lbmc) t" + ij);
				sbb.append(" on t" + ij + ".pk_psndoc = bd_psndoc.pk_psndoc");
			}
		}

		sbh.append(sbb).append(" where hi_psndoc_deptchg.lastflag = 'Y'");
		sbh.append(" and hi_psndoc_deptchg.bendflag = 'N'");
		sbh.append(" and bd_psndoc.sealdate is null");
		sbh.append(" and hi_psndoc_deptchg.jobtype = 0");
		sbh.append(" and bd_psndoc.indocflag = 'Y'");
		sbh.append(" and (bd_psndoc.psnclscope = 0)");
		sbh.append(" and bd_psncl.psnclscope = 0");
		sbh.append(" and (bd_corp.ishasaccount = 'Y')");
		sbh.append(" and (bd_corp.isseal is null or bd_corp.isseal <> 'Y')");
		ArrayList<HashMap<String, Object>> psninfos = (ArrayList<HashMap<String, Object>>) getDao().executeQuery(sbh.toString(), new MapListProcessor());

		HYBillVO billvo = new HYBillVO();
		
		GxHVO gxhvo = new GxHVO();
		gxhvo.setPk_corp(psninfos.get(0).get("pk_corp").toString());
		gxhvo.setVyear(String.valueOf(year));
		billvo.setParentVO(gxhvo);
		
		ArrayList<GxBVO> gxbvos = new ArrayList<GxBVO>();
		
		// ���㹫������
		for (HashMap<String, Object> psninfo : psninfos) {
			// ��������ά���ӱ����
			GxBVO gxbvo = new GxBVO();
			gxbvos.add(gxbvo);
			gxbvo.setPk_corp(psninfo.get("pk_corp").toString());
			gxbvo.setPk_psndoc(psninfo.get("pk_psndoc").toString());
			gxbvo.setVyear(String.valueOf(year));
			// ������Ա����
			String joinworkdateStr = (String)psninfo.get("joinworkdate");
			
			UFDouble workyear = new UFDouble("0.00");
			if (StringUtils.isNotEmpty(joinworkdateStr)) {
				int joinworkdate = Integer.parseInt(joinworkdateStr.substring(0, 4));
				String curdate = year+"-"+month+"-"+day;
				UFDate ufcurdate = new UFDate(curdate);
				UFDate ufjoinworkdate = new UFDate(joinworkdateStr);
				workyear = new UFDouble(yearCompare(ufjoinworkdate.toDate(),ufcurdate.toDate()));
				//workyear = year-joinworkdate;
				//gxbvo.setNgl(new UFDouble(workyear));
			} else {
				gxbvo.setVmemo("δά���μӹ�������");
				continue;
			}
			String joinsysdateStr = (String)psninfo.get("joinsysdate");
			UFDouble sysworkyear = new UFDouble("0.00");
			if (StringUtils.isNotEmpty(joinsysdateStr)) {
				int joinsysdate = Integer.parseInt(joinsysdateStr.substring(0,4));
				String curdate = year+"-"+month+"-"+day;
				UFDate ufcurdate = new UFDate(curdate);
				UFDate ufjoinsysdate = new UFDate(joinsysdateStr);
				sysworkyear = new UFDouble(yearCompare(ufjoinsysdate.toDate(),ufcurdate.toDate()));
				//sysworkyear = year-joinsysdate;
			} else {
				gxbvo.setVmemo("δά�����뱾��λ����");
				continue;
			}
			
			/****************************** add xieye 2018-12-28 15:44:00 start ******************************/
			String glxzStr=(String)psninfo.get("glxz");//��������
			UFDouble glxz=new UFDouble();
			if(null!=glxzStr&&!"".equals(glxzStr)){
				glxz=new UFDouble(glxzStr);
			}
			UFDouble sjgl=new UFDouble(workyear).add(glxz);//ʵ�ʹ���(ʵ�ʹ���=����+��������)
			gxbvo.setNgl(sjgl);
			/****************************** add xieye 2018-12-28 15:44:00 end ******************************/

			// Ӧ����ǰ�����ֹ�ά������Ӧ�����й���֤���ĵ�һ���ֹ�ά�����޹���֤��ͷ�����޹���
			DecimalFormat df = new DecimalFormat("######0.00");
			String sysworkmonthStr = "";
			String joinsysmonthStr = "";
			if (StringUtils.isNotEmpty(joinworkdateStr)) {
				sysworkmonthStr = ((String)psninfo.get("joinworkdate")).substring(0, 7);//
			}
			if (StringUtils.isNotEmpty(joinsysdateStr)) {
				joinsysmonthStr = ((String)psninfo.get("joinsysdate")).substring(0, 7);
			}
			
			
			if (joinsysmonthStr.equals(sysworkmonthStr) && sysworkyear.compareTo(new UFDouble(1)) <= 0) {
				// Ӧ����ǰ����,�ֶ�ά��
				gxbvo.setVmemo("Ӧ���������뱾��λ��"+(df.format(sysworkyear.add(1).toDouble()))+"��");
				continue;
			} else if (sysworkyear.compareTo(workyear) == -1) {
				// ��Ӧ����
				if ( "N".equals(psninfo.get("gxzm").toString()) ) {
					// �����޹���֤��
					if (sysworkyear.compareTo(new UFDouble(0)) == 0) {
						// ���뱾��λ1�꣬�ֶ�ά��
						gxbvo.setVmemo("�й���֤����Ӧ���������뱾��λ��"+df.format((sysworkyear.add(1)).toDouble())+"��");
						continue;
					}
				} else if (sysworkyear.compareTo(new UFDouble(1)) <= 0) {
					// �޹���֤����ǰ���꣬�޹���
					gxbvo.setNyfgx(new UFDouble(0));
					gxbvo.setNkcgx(new UFDouble(0));
					gxbvo.setNsfgx(new UFDouble(0));
					gxbvo.setVmemo("�޹���֤����Ӧ���������뱾��λ��"+(df.format(sysworkyear.add(1).toDouble()))+"��");
					continue;
				}
			}
			
			// ���ݹ��ݻ���������㹫������
			Integer gxdays = 0;
			for (GxjcszVO gxjcsz : jcszVOs) {
				//if (workyear >= gxjcsz.getIyearstart() && workyear <= gxjcsz.getIyearend()) {
				if(gxbvo.getNgl().compareTo(new UFDouble(gxjcsz.getIyearstart()))>=0 &&
						gxbvo.getNgl().compareTo(new UFDouble(gxjcsz.getIyearend()))<=0){
					gxdays = gxjcsz.getVdays();
					break;
				}
			}
			if ("Y".equals(psninfo.get("fsj").toString())) {
				// �����11��
				gxdays += 11;
			}
			gxbvo.setNyfgx(new UFDouble(gxdays));
			gxbvo.setNsfgx(new UFDouble(gxdays));
	
			// �ж��Ƿ�ʧЧ
			for (int i = 0; i < sxszVOs.size(); i++) {
				GxsxszVO gxsxszVO = sxszVOs.get(i);
				
				//if (workyear >= gxsxszVO.getIworkyearstart() && workyear <= gxsxszVO.getIworkyearend()) {
				if(gxbvo.getNgl().compareTo(new UFDouble(gxsxszVO.getIworkyearstart()))>=0 &&
						gxbvo.getNgl().compareTo(new UFDouble(gxsxszVO.getIworkyearend()))<=0){	
					String[] attendtypes = gxsxszVO.getVattendtype().split(",");
					double sumi = 0;
					for (int j = 0; j < attendtypes.length; j++) {
						ij = new String(i+""+j);
						Integer isumij = (Integer)psninfo.get("sum"+ij);
						double sumij = 0;
						if (isumij != null) {
							sumij = isumij;
							if (attendtypes[j].contains("����")) {
								sumij /= 2;
							}
						}
						sumi += sumij;
					}
					Integer ilosedays = sxszVOs.get(i).getIlosedays();
					if (sumi>ilosedays) {
						// ʧЧ����ӿ۹��ݣ�Ӧ�����ݣ�ʧЧԭ��
						gxbvo.setNkcgx(new UFDouble(gxdays));
						gxbvo.setNsfgx(new UFDouble(0));
						gxbvo.setVreson(gxsxszVO.getVattendtype() + "("+ sumi +")");
						break;
					}
				}
			}
		}
		billvo.setChildrenVO(gxbvos.toArray(new GxBVO[0]));
		BillSave billSave = new BillSave();
		billSave.saveBill(billvo);

		return "���ݼ���ɹ�";
	}

	public String yearCompare(Date fromDate,Date toDate){
	    DayCompare result = dayComparePrecise(fromDate, toDate);
	    double month = result.getMonth();
	    double year = result.getYear();
	    //����2λС����������������
	    DecimalFormat df = new DecimalFormat("######0.0");
	    return df.format(year + month / 12);
	}
	
	/**
	 * ����2������֮������  ���ꡢ�¡���Ϊ��λ�����Լ������Ƕ���
	 * ���磺2011-02-02 ��  2017-03-02
	 *                                ����Ϊ��λ���Ϊ��6��
	 *                                ����Ϊ��λ���Ϊ��73����
	 *                                ����Ϊ��λ���Ϊ��2220��
	 * @param fromDate
	 * @param toDate
	 * @return
	 */
	public DayCompare dayCompare(Date fromDate,Date toDate){
	    Calendar  from  =  Calendar.getInstance();
	    from.setTime(fromDate);
	    Calendar  to  =  Calendar.getInstance();
	    to.setTime(toDate);
	    //ֻҪ����
	    int fromYear = from.get(Calendar.YEAR);
	    int fromMonth = from.get(Calendar.MONTH);

	    int toYear = to.get(Calendar.YEAR);
	    int toMonth = to.get(Calendar.MONTH);

	    int year = toYear  -  fromYear;
	    int month = toYear *  12  + toMonth  -  (fromYear  *  12  +  fromMonth);
	    int day = (int) ((to.getTimeInMillis()  -  from.getTimeInMillis())  /  (24  *  3600  *  1000));
	    
	    DayCompare daycompare = new DayCompare();
	    daycompare.setYear(year);
	    daycompare.setMonth(month);
	    daycompare.setDay(day);
	    
	    return daycompare;
	}
	
	/**
	 * ����2������֮������  ������������
	 * ���磺2011-02-02 ��  2017-03-02 ��� 6�꣬1���£�0��
	 * @param fromDate
	 * @param toDate
	 * @return
	 */
	public DayCompare dayComparePrecise(Date fromDate,Date toDate){
	    Calendar  from  =  Calendar.getInstance();
	    from.setTime(fromDate);
	    Calendar  to  =  Calendar.getInstance();
	    to.setTime(toDate);

	    int fromYear = from.get(Calendar.YEAR);
	    int fromMonth = from.get(Calendar.MONTH);
	    int fromDay = from.get(Calendar.DAY_OF_MONTH);

	    int toYear = to.get(Calendar.YEAR);
	    int toMonth = to.get(Calendar.MONTH);
	    int toDay = to.get(Calendar.DAY_OF_MONTH);
	    int year = toYear  -  fromYear;
	    int month = toMonth  - fromMonth;
	    int day = toDay  - fromDay;
	    
	    DayCompare daycompare = new DayCompare();
	    daycompare.setYear(year);
	    daycompare.setMonth(month);
	    daycompare.setDay(day);
	    
	    return daycompare;
	}
}
