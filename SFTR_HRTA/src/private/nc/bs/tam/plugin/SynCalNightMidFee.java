package nc.bs.tam.plugin;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.bs.pub.taskcenter.BgWorkingContext;
import nc.bs.pub.taskcenter.IBackgroundWorkPlugin;
import nc.jdbc.framework.processor.BeanListProcessor;
import nc.uif.pub.exception.UifException;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDate;
import nc.vo.tam.plugin.MidNightFeeBVO;
import nc.vo.tam.plugin.MidNightFeeVO;
import nc.vo.tam.tongren003.PaibanWeekVO;
import nc.vo.tam.tongren003.PanbanWeekBVO;

/**
 * 中夜班费计算备份排班AB表
 * @author zhuchaoli
 *
 */
public class SynCalNightMidFee implements IBackgroundWorkPlugin {

	public String executeTask(BgWorkingContext arg0) throws BusinessException {
		// TODO Auto-generated method stub
		try {
			exportConfirm(arg0);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}

	/**
	 * 备份排班表A
	 * @return
	 * @throws DAOException
	 * @throws ParseException 
	 * @throws UifException 
	 */
	public void backupsPaibanWeek(UFDate curdate) throws DAOException, ParseException{
		BaseDAO baseDAO = new BaseDAO();
		
		int month = curdate.getMonth()-1; //上月
		int year = curdate.getYear(); //年
		if(month==0){
			month = 12;
			year =year -1;
		}
		Integer integer = new Integer(month);
		String monthStr = null;
		if (integer<10){
			monthStr = "0" + month ;
		}else{
			monthStr =  month +"" ;
		}
		String beginVdate = year+"-"+monthStr+"-"+"01";
		//+"至"+year+"-"+monthStr
		int thisMonthDay = calculateThisMonthDay(beginVdate);
		String endVdate = year+"-"+monthStr+"-"+thisMonthDay;
		String vdate = beginVdate + "至" +endVdate;
		
		//查询月排班
		String sql = " select * from trtam_paiban where nvl(dr,0)=0 and vdate='"+vdate+"'";
		ArrayList<PaibanWeekVO> weekvoslist = (ArrayList<PaibanWeekVO>) baseDAO.executeQuery(sql, new BeanListProcessor(PaibanWeekVO.class));
		PaibanWeekVO[] weekvos = weekvoslist.toArray( new PaibanWeekVO[weekvoslist.size()]);  //new PanbanWeekBVO[list_b.size()]
			
		
		ArrayList<MidNightFeeVO> list = new ArrayList<MidNightFeeVO>();
		for(PaibanWeekVO vo: weekvos){
			MidNightFeeVO bak = copyToBak(vo);
			list.add(bak);
		}
		
		BaseDAO dao = new BaseDAO();
		
		//----------------------------------周排班
		//UFDate date = new UFDate(endVdate); //y月结束日期
		int index = getDay(endVdate)-1; //周几
		if(index==0){
			index=7;
		}
		UFDate date = new UFDate(endVdate); //y月结束日期
		UFDate dateAfter = date.getDateAfter(7-index); //月结束日期那周的周日
		UFDate dateBefore = date.getDateBefore(index-1);//月结束日期那周的周一
		String weekVdate = dateBefore.toString()+"至"+dateAfter.toString();
		
		ArrayList<String> weekVdateStr = new ArrayList<String>(); //装周排班日期
		weekVdateStr.add(weekVdate);
		
		while(dateBefore .compareTo(new UFDate(beginVdate))>0 ){ //周排班 周一 
			UFDate weekBegin = dateBefore.getDateBefore(7);
			UFDate weekAfter = dateAfter.getDateBefore(7);
			weekVdate = weekBegin.toString()+"至"+weekAfter.toString();
			weekVdateStr.add(weekVdate);
			dateBefore = weekBegin;
			dateAfter = weekAfter;
		}
		
		//再加上最前一周
		//UFDate weekBegin2 = dateBefore.getDateBefore(7);
		//UFDate weekAfter2 = dateBefore.getDateBefore(1);
		//weekVdate = weekBegin2.toString()+"至"+weekAfter2.toString();
		//weekVdateStr.add(weekVdate);
		
		
		for(String vdateStr:weekVdateStr){
			//查询周排班
				
			String sql1=" select * from trtam_paiban where nvl(dr,0)=0 and vdate = '"+vdateStr+"'";
			ArrayList<PaibanWeekVO> weekvosslist = (ArrayList<PaibanWeekVO>) baseDAO.executeQuery(sql1, new BeanListProcessor(PaibanWeekVO.class));
			PaibanWeekVO[] weekvoss = weekvosslist.toArray( new PaibanWeekVO[weekvosslist.size()]);  //new PanbanWeekBVO[list_b.size()]
		
			if (weekvoss != null || weekvoss.length >0){
				for(PaibanWeekVO vo: weekvoss){
					MidNightFeeVO bak = copyToBak(vo);
					list.add(bak);
				}
			}
		}
		
		//插入到 排班备份表
		baseDAO.insertVOArray( list.toArray(new MidNightFeeVO[list.size()]));
		
		System.out.println("执行完了。");
	}
	
	public int getDay(String str) throws ParseException{
		SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd");
		Date date =sdf.parse(str);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		// 第几周
		int week = calendar.get(Calendar.WEEK_OF_MONTH);
		// 第几天，从周日开始
		int day = calendar.get(Calendar.DAY_OF_WEEK);
		return day;
	}
	
	public int calculateThisMonthDay(String vdate){
		int month = new UFDate(vdate).getMonth();
		
		if(month ==1 || month ==3 || month==5 || month==7 || month==8 || month==10 || month==12){
			return 31;
		}else if(month ==4 || month ==6 || month==9 || month==11 ){
			return 30;
		}else {
			int year = new UFDate(vdate).getYear();
			if((year % 4 ==0 && year %100 !=0) ||(year % 400 ==0)){
				return 29;
			}else{
				return 28;
			}
		}
	}
	
	
	public MidNightFeeVO copyToBak(PaibanWeekVO vo){
		MidNightFeeVO bak = new MidNightFeeVO();
		bak.setPk_paiban(vo.getPk_paiban());
		bak.setVdef1(vo.getVdef1());
		bak.setVdef2(vo.getVdef2());
		bak.setVdef3(vo.getVdef3());
		bak.setVdef4(vo.getVdef4());
		bak.setVdef5(vo.getVdef5());
		bak.setVdate(vo.getVdate());
		bak.setPptype(vo.getPptype());
		bak.setNnowdays(vo.getNnowdays());
		bak.setNgxs(vo.getNgxs());
		bak.setNbcgxs(vo.getNbcgxs());
		bak.setNsygxs(vo.getNsygxs());
		bak.setVoperatorid(vo.getVoperatorid());
		bak.setVbillstatus(vo.getVbillstatus());
		bak.setVbillstatus1(vo.getVbillstatus1());
		bak.setVbillstatus2(vo.getVbillstatus2());
		bak.setVbillstatus3(vo.getVbillstatus3());
		//bak.setPk_deptzb(vo.getPk_deptzb());
		//bak.setPk_temp(vo.getPk_temp());
		bak.setUploadnum(vo.getUploadnum());
		bak.setClasstype(vo.getClasstype());
		bak.setPk_psndoc(vo.getPk_psndoc());
		bak.setPk_dept(vo.getPk_dept());
		bak.setPk_corp(vo.getPk_corp());
		
		for(int i =1 ; i<32 ;i++){
			bak.setAttributeValue("vbbname"+i, vo.getAttributeValue("vbbname"+i));
			bak.setAttributeValue("pk_bb"+i, vo.getAttributeValue("pk_bb"+i));
		}
		
		bak.setMemo(vo.getMemo());
		
		return bak;
	}
	
	public MidNightFeeBVO copyToBakPaiBanWeekBvo(PanbanWeekBVO bvo){
		MidNightFeeBVO bak = new MidNightFeeBVO();
		bak.setAttributeValue("ts", bvo.getTs());
		bak.setAttributeValue("dr", bvo.getDr());
		bak.setAttributeValue("pk_paiban", bvo.getPk_paiban());
		bak.setAttributeValue("pk_paiban_b", bvo.getPk_paiban_b());
		bak.setAttributeValue("ddate", bvo.getDdate());
		bak.setAttributeValue("pk_bb", bvo.getPk_bb());
		bak.setAttributeValue("pk_corp", bvo.getPk_corp());
		bak.setAttributeValue("pk_deptzb", bvo.getPk_deptzb());
		bak.setAttributeValue("pptype", bvo.getPptype());
		bak.setAttributeValue("pk_temp", bvo.getPk_temp());
		bak.setAttributeValue("uploadnum", bvo.getUploadnum());
		bak.setAttributeValue("dclassperiod", bvo.getDclassperiod());
		bak.setAttributeValue("vbillstatus1", bvo.getVbillstatus1());
		bak.setAttributeValue("vbillstatus2", bvo.getVbillstatus2());
		bak.setAttributeValue("vbillstatus3", bvo.getVbillstatus3());
		bak.setAttributeValue("deptzbname", bvo.getDeptzbname());
		bak.setAttributeValue("memo", bvo.getMemo());
		bak.setAttributeValue("biszb", bvo.getBiszb());
		bak.setAttributeValue("pk_psndoc", bvo.getPk_psndoc());
		bak.setAttributeValue("pk_dept", bvo.getPk_dept());
		return bak;
	}
	
	/**\
	 * 备份排班表B
	 * @param curdate
	 * @throws DAOException
	 * @throws ParseException
	 */
	public void backupsPaibanWeekB(UFDate curdate) throws DAOException, ParseException{
		int month = curdate.getMonth()-1; //获取上月份
		int year = curdate.getYear(); //年
		if(month==0){
			month = 12;
			year =year -1;
		}
		Integer integer = new Integer(month);
		String monthStr = null;
		if (integer <10){
			monthStr = "0" + month ;
		}else{
			monthStr =  month +"" ;
		}
		//上月份的开始时间
		String ddateBegin= year +"-"+monthStr+"-"+"01";
		int thisMonthDay = calculateThisMonthDay(ddateBegin);
		String ddateEnd = year+"-"+monthStr+"-"+thisMonthDay;
		
		BaseDAO dao = new BaseDAO();
		
		 String sql = " select * from trtam_paiban_b where nvl(dr,0)=0 and (ddate >= '"+ddateBegin+"' and ddate <= '"+ddateEnd+"' )";
		 ArrayList<PanbanWeekBVO> bvoslist = (ArrayList<PanbanWeekBVO>) dao.executeQuery(sql, new BeanListProcessor(PanbanWeekBVO.class));
		 PanbanWeekBVO[] bvo = bvoslist.toArray( new PanbanWeekBVO[bvoslist.size()]);  //new PanbanWeekBVO[list_b.size()]
		
		ArrayList<MidNightFeeBVO> bvolist = new ArrayList<MidNightFeeBVO>();
		for(PanbanWeekBVO paibanweekbvo :bvo){
			MidNightFeeBVO fee = copyToBakPaiBanWeekBvo(paibanweekbvo);
			bvolist.add(fee);
		}
		
		//插入到排班备份表
		dao.insertVOList(bvolist);
		
		System.out.println("B执行好了");
	}
	
	
	
	
	/**
	 * 主方法
	 * @param arg0
	 * @throws DAOException
	 * @throws ParseException 
	 * @throws UifException 
	 */
	public void exportConfirm(BgWorkingContext arg0) throws DAOException, ParseException, UifException {
		//UFDate curdate = nc.ui.hr.global.Global.getServerTime().getDate(); //服务器时间
		UFDate curdate = new UFDate();
		
		//备份排班表A
		backupsPaibanWeek(curdate);
		
		//备份排班表B
		backupsPaibanWeekB(curdate);
		
	}
	
}
