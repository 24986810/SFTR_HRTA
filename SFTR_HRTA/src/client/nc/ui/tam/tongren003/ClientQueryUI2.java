/**
 * 
 */
package nc.ui.tam.tongren003;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import com.ibm.db2.jcc.am.af;

import nc.bs.framework.common.NCLocator;
import nc.itf.hrp.pub.HRPPubTool;
import nc.itf.uap.IUAPQueryBS;
import nc.jdbc.framework.processor.BeanListProcessor;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.bill.BillEditEvent;
import nc.ui.trade.bill.ICardController;
import nc.ui.trade.button.IBillButton;
import nc.ui.trade.card.BillCardUI;
import nc.ui.trade.card.CardEventHandler;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDouble;
import nc.vo.tam.tongren.power.BclbVO;
import nc.vo.tam.tongren003.PaiPanReportVO;


/**
 * @author 28729
 *
 */
public class ClientQueryUI2 extends BillCardUI {

	private HashMap<String,String> map11 = new HashMap<String,String>();
	PaiPanReportVO[] paipanvos11 = null;
	ArrayList<PaiPanReportVO> list =null;
	/**
	 * 
	 */
	public ClientQueryUI2() {
		// TODO Auto-generated constructor stub
		
		getBillCardPanel().getHeadItem("ddate").setEnabled(true);
		getBillCardPanel().getHeadItem("denddate").setEnabled(true);
		getBillCardPanel().getHeadItem("pk_dept").setEnabled(true);
		getBillCardPanel().getHeadItem("pk_bb").setEnabled(true);
		getBillCardPanel().getHeadItem("pk_psndoc").setEnabled(true);
		getBillCardPanel().getHeadItem("bishowperiod").setEnabled(true);
		getBillCardPanel().getHeadItem("days").setEnabled(true);
		getBillCardPanel().getHeadItem("bishowperiod").setValue(new UFBoolean(true));
		((UIRefPane)getBillCardPanel().getHeadItem("pk_dept").getComponent()).setMultiSelectedEnabled(true);
//		((UIRefPane)getBillCardPanel().getHeadItem("pk_dept").getComponent()).setAutoCheck(false);
		((UIRefPane)getBillCardPanel().getHeadItem("pk_dept").getComponent()).setReturnCode(false);
		String wheredept = 	" pk_corp='" + getCorpPrimaryKey()+ "' and isnull(bisseal,'N')='N' and pk_dept in (select pk_docid from bd_tr_userpower where isnull(dr,0)=0 and pk_user='"+_getOperator()+"' and powertype=0 and  pk_corp='"+_getCorp().getPrimaryKey()+"') ";
		((UIRefPane)getBillCardPanel().getHeadItem("pk_dept").getComponent()).setWhereString(wheredept);

		
		((UIRefPane)getBillCardPanel().getHeadItem("pk_psndoc").getComponent()).setMultiSelectedEnabled(true);
		((UIRefPane)getBillCardPanel().getHeadItem("pk_psndoc").getComponent()).setTreeGridNodeMultiSelected(true);
//		((UIRefPane)getBillCardPanel().getHeadItem("pk_psndoc").getComponent()).setAutoCheck(false);
		((UIRefPane)getBillCardPanel().getHeadItem("pk_psndoc").getComponent()).setReturnCode(false);
		((UIRefPane)getBillCardPanel().getHeadItem("pk_bb").getComponent()).setMultiSelectedEnabled(true);
		((UIRefPane)getBillCardPanel().getHeadItem("pk_bb").getComponent()).setTreeGridNodeMultiSelected(true);
//		((UIRefPane)getBillCardPanel().getHeadItem("pk_bb").getComponent()).setAutoCheck(false);
		((UIRefPane)getBillCardPanel().getHeadItem("pk_bb").getComponent()).setReturnCode(false);
		getButtonManager().getButton(IBillButton.Print).setName("导出");
		getButtonManager().getButton(IBillButton.Print).setEnabled(true);
		getBillCardPanel().getBillTable().setSortEnabled(false);
		updateButtons();
	}

	/**
	 * @param pk_corp
	 * @param pk_billType
	 * @param pk_busitype
	 * @param operater
	 * @param billId
	 */
	public ClientQueryUI2(String pk_corp, String pk_billType,
			String pk_busitype, String operater, String billId) {
		super(pk_corp, pk_billType, pk_busitype, operater, billId);
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see nc.ui.trade.card.BillCardUI#createController()
	 */
	@Override
	protected ICardController createController() {
		// TODO Auto-generated method stub
		return new ClientQueryCtrl2();
	}
    @Override
    protected CardEventHandler createEventHandler() {
    	// TODO Auto-generated method stub
    	return new EventQueryHandler2(this,this.createController());
    }
	/* (non-Javadoc)
	 * @see nc.ui.trade.base.AbstractBillUI#getRefBillType()
	 */
	@Override
	public String getRefBillType() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see nc.ui.trade.base.AbstractBillUI#initSelfData()
	 */
	@Override
	protected void initSelfData() {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see nc.ui.trade.base.AbstractBillUI#setDefaultData()
	 */
	@Override
	public void setDefaultData() throws Exception {
		// TODO Auto-generated method stub

	}
	public void onshowData() throws BusinessException{
		Object value = getBillCardPanel().getHeadItem("bishowperiod").getValueObject();
			
		if(value!=null&&new UFBoolean(value.toString()).booleanValue()){
			
			PaiPanReportVO[] paipanvos12 =  getPaiPanReportVO();
			
			getBillCardPanel().getBillModel().setBodyDataVO(paipanvos12);
			getBillCardPanel().getBillModel().execLoadFormula();
			getBillCardPanel().getBillTable().setUI(new MergeTableUI(0,3,true,3));
			getBillCardPanel().getBillTable().setEnabled(false);
			getBillCardPanel().getBillTable().setSortEnabled(false);
		}else{

			PaiPanReportVO[] paipanvos13 = getPaiPanReportVO2();
				
			getBillCardPanel().getBillModel().setBodyDataVO(paipanvos13);
			getBillCardPanel().getBillModel().execLoadFormula();
			getBillCardPanel().getBillTable().setUI(new MergeTableUI(0,5,true,3,5,3));
			getBillCardPanel().getBillTable().setEnabled(false);
			getBillCardPanel().getBillTable().setSortEnabled(false);
			
		}
		
	}
	@Override
	public void afterEdit(BillEditEvent e) {
		// TODO Auto-generated method stub
		if(e.getPos()==HEAD&&e.getKey().equals("bishowperiod")){
			if(e.getValue()!=null&&new UFBoolean(e.getValue().toString()).booleanValue()){
				getBillCardPanel().getBillModel().getItemByKey("vperiod").setShow(true);
				getBillCardPanel().getBillModel().getItemByKey("nperiodnum").setShow(true);
//				getBillCardPanel().getBillModel().getItemByKey("deptnamekq").setShow(true);
			}else{
				getBillCardPanel().getBillModel().getItemByKey("vperiod").setShow(false);
				getBillCardPanel().getBillModel().getItemByKey("nperiodnum").setShow(true);
//				getBillCardPanel().getBillModel().getItemByKey("deptnamekq").setShow(false);
			}
			getBillCardPanel().setBillData(getBillCardPanel().getBillData());
			try {
				onshowData();
			} catch (BusinessException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}
	
	public String getBclbVO(String pk_bclb) throws BusinessException{
		String sql = "select * from tbm_bclb where pk_bclbid='"+pk_bclb+"' and dr=0";
		String lbmc = null;
		IUAPQueryBS bs= NCLocator.getInstance().lookup(IUAPQueryBS.class);
		ArrayList<BclbVO> list = (ArrayList<BclbVO>)bs.executeQuery(sql, new BeanListProcessor(BclbVO.class));
		if(list != null){
			if(list.size()>0){
				lbmc = list.get(0).getLbmc();
			}
			
		}
		return lbmc;

	}
	
	
	public PaiPanReportVO[] getPaiPanReportVO() throws BusinessException{
		getBillCardPanel().getBillModel().clearBodyData();
		Object days = getBillCardPanel().getHeadItem("days").getValueObject();
		Object ddate = getBillCardPanel().getHeadItem("ddate").getValueObject();
		Object denddate = getBillCardPanel().getHeadItem("denddate").getValueObject();
		String[] pks_bb = ((UIRefPane)getBillCardPanel().getHeadItem("pk_bb").getComponent()).getRefPKs();
		if(pks_bb==null||pks_bb.length<=0){
			//			MessageDialog.showHintDlg(this.getBillUI(), "提示", "请选择考勤类别");
			//			return;
		}
		String[] pks_psndoc = ((UIRefPane)getBillCardPanel().getHeadItem("pk_psndoc").getComponent()).getRefPKs();
		String[] pks_dept = ((UIRefPane)getBillCardPanel().getHeadItem("pk_dept").getComponent()).getRefPKs();
//		String wheresql = " tb.lbbm like '99%' ";去掉编码限制
		String wheresql = " 1=1 ";
		
		if(ddate!=null&&ddate.toString().trim().length()>0){
			wheresql+=" and b.ddate>='"+ddate+"' ";
		}
		if(denddate!=null&&denddate.toString().trim().length()>0){
			wheresql+=" and b.ddate<='"+denddate+"' ";
		}
		if(pks_bb!=null&&pks_bb.length>0){
			ArrayList<String> list = new ArrayList<String>();
			list.addAll(Arrays.asList(pks_bb));
			wheresql += " "+HRPPubTool.formInSQL("b.pk_bb", list)+" ";
		}
		if(pks_psndoc!=null&&pks_psndoc.length>0){
			ArrayList<String> list = new ArrayList<String>();
			list.addAll(Arrays.asList(pks_psndoc));
			wheresql += " "+HRPPubTool.formInSQL("b.pk_psndoc", list)+" ";
		}
		if(pks_dept!=null&&pks_dept.length>0){
			ArrayList<String> list = new ArrayList<String>();
			list.addAll(Arrays.asList(pks_dept));
			wheresql += " "+HRPPubTool.formInSQL("b.pk_dept", list)+" ";
		}
		String whereday = "";
		if(days!=null&&days.toString().trim().length()>0){
			whereday = " where  nnum>='"+days.toString()+"'";
		}
		String wheredept = 	" and b.pk_dept in (select pk_dept from trtam_deptdoc_kq where isnull(dr,0)=0 and  pk_corp='" + _getCorp().getPrimaryKey()+ "' and isnull(bisseal,'N')='N' and pk_dept in (select pk_docid from bd_tr_userpower where isnull(dr,0)=0 and pk_user='"+_getOperator()+"' and powertype=0 and  pk_corp='"+_getCorp().getPrimaryKey()+"')) ";
		
//		String sql = "select pk_dept,"
//			+"       pk_psndoc,"
//			+"       psnname,"
//			+"       pk_bb,"
//			+"       days nnum,"
//			+"       lbmc,"
//			+"       pk_bbz,"
//			+"       pk_dd,"
//			+"       biszb,"
//			+"       ddate vperiod,"
//			+"      (months_between(to_date(maxdate,'yyyy-mm'),to_date(mindate,'yyyy-mm')) + 1) nperiodnum"
//			+"  from (select b.pk_dept,"
//			+"               kqks.vname,"
//			+"               b.pk_psndoc,"
//			+"               kqry.psnname,"
//			+"               pk_bb,"
//			+"               tb.lbmc,"
//			+"               substr(ddate, 0, 7) ddate,"
//			+"               pk_bbz,"
//			+"               pk_dd,"
//			+"               biszb,"
//			+"               sum(decode(substr(tb.lbmc, 0, 2), '半天', 0.5, 1)) days,"
//			+"               sum(count(b.dr)) over(partition by b.pk_psndoc order by b.pk_psndoc),"
//			+"               max(substr(ddate, 0, 7)) keep(dense_rank FIRST order by substr(ddate, 0, 7),b.pk_psndoc) over (partition by b.pk_psndoc) as mindate,"
//			+"				 max(substr(ddate, 0, 7)) keep(dense_rank LAST order by substr(ddate, 0, 7),b.pk_psndoc) over (partition by b.pk_psndoc) as maxdate"
//			+"          from trtam_paiban_b b"
//			+"         inner join tbm_bclb tb"
//			+"            on b.pk_bb = tb.pk_bclbid"
//			+"          left join trtam_deptdoc_kq kqks"
//			+"            on b.pk_dept = kqks.pk_dept"
//			+"          left join bd_psndoc kqry"
//			+"            on b.pk_psndoc = kqry.pk_psndoc "
//			+"          where b.pk_corp='"+_getCorp().getPrimaryKey()+"' and isnull(b.dr,0)=0 "
//			+" and "+wheresql+" "+wheredept+" "
//			+"         group by b.pk_dept,"
//			+"                  kqks.vname,"
//			+"                  b.pk_psndoc,"
//			+"                  kqry.psnname,"
//			+"                  pk_bb,"
//			+"                  tb.lbmc,"
//			+"                  substr(ddate, 0, 7),"
//			+"                  pk_bbz,"
//			+"                  pk_dd,"
//			+"                  biszb"        
//			+"         order by b.pk_psndoc, substr(ddate, 0, 7) asc) "+whereday
//			+" group by pk_dept,"
//			+"          pk_psndoc,"
//			+"          psnname,"
//			+"          pk_bb,"
//			+"          days,"
//			+"          lbmc,"
//			+"          pk_bbz,"
//			+"          pk_dd,"
//			+"          biszb,"
//			+"			mindate,maxdate,"
//			+"          ddate order by pk_psndoc, ddate asc";
		
		String sql = "select pk_dept,"
			+"       a.pk_psndoc,"
			+"       psnname,"
			+"       pk_bb,"
			+"       nnum,"
			+"       lbmc,"
			+"       pk_bbz,"
			+"       pk_dd,"
			+"       biszb,"
			+"       vperiod,"
			+"       b.nperiodnum"
			+"  from (select pk_dept,"
			+"               pk_psndoc,"
			+"               psnname,"
			+"               pk_bb,"
			+"               days      nnum,"
			+"               lbmc,"
			+"               pk_bbz,"
			+"               pk_dd,"
			+"               biszb,"
			+"               ddate     vperiod"
			+"          from (select b.pk_dept,"
			+"                       kqks.vname,"
			+"                       b.pk_psndoc,"
			+"                       kqry.psnname,"
			+"                       pk_bb,"
			+"                       tb.lbmc,"
			+"                       substr(ddate, 0, 7) ddate,"
			+"                       pk_bbz,"
			+"                       pk_dd,"
			+"                       biszb,"
			+"                       sum(decode(substr(tb.lbmc, 0, 2), '半天', 0.5, 1)) days      "          
			+"                  from trtam_paiban_b b"
			+"                 inner join tbm_bclb tb"
			+"                    on b.pk_bb = tb.pk_bclbid"
			+"                  left join trtam_deptdoc_kq kqks"
			+"                    on b.pk_dept = kqks.pk_dept"
			+"                  left join bd_psndoc kqry"
			+"                    on b.pk_psndoc = kqry.pk_psndoc"
			+"                 where b.pk_corp = '"+_getCorp().getPrimaryKey()+"'  and isnull(b.dr,0)=0"
			+"                   and "+wheresql+" "+wheredept+""
			+"                 group by b.pk_dept,"
			+"                          kqks.vname,"
			+"                          b.pk_psndoc,"
			+"                          kqry.psnname,"
			+"                          pk_bb,"
			+"                          tb.lbmc,"
			+"                          substr(ddate, 0, 7),"
			+"                          pk_bbz,"
			+"                          pk_dd,"
			+"                          biszb"
			+"                 order by substr(ddate, 0, 7) asc)"
			+"         group by pk_dept,"
			+"                  pk_psndoc,"
			+"                  psnname,"
			+"                  pk_bb,"
			+"                  days,"
			+"                  lbmc,"
			+"                  pk_bbz,"
			+"                  pk_dd,"
			+"                  biszb,"
			+"                  ddate   "     
			+"         order by pk_psndoc, ddate asc) a"
	
			+"  left join (select count(ddate) nperiodnum, pk_psndoc"
			+"               from (select distinct b.pk_psndoc,"
			+"                                     kqry.psnname,"
//			+"                                     pk_bb,"
//			+"                                     tb.lbmc,"
			+"                                     substr(ddate, 0, 7) ddate"   
			+"                       from trtam_paiban_b b"
			+"                      inner join tbm_bclb tb"
			+"                        on b.pk_bb = tb.pk_bclbid"
			+"                       left join trtam_deptdoc_kq kqks"
			+"                         on b.pk_dept = kqks.pk_dept"
			+"                       left join bd_psndoc kqry"
			+"                         on b.pk_psndoc = kqry.pk_psndoc"
			+"                 where b.pk_corp = '"+_getCorp().getPrimaryKey()+"'  and isnull(b.dr,0)=0"
			+"                   and "+wheresql+" "+wheredept+""
			+"                      group by b.pk_dept,"
			+"                               kqks.vname,"
			+"                               b.pk_psndoc,"
			+"                               kqry.psnname,"
//			+"                               pk_bb,"
//			+"                               tb.lbmc,"
			+"                               substr(ddate, 0, 7)"
//			+"                               pk_bbz,"
//			+"                               pk_dd,"
//			+"                               biszb"
			+"                      order by b.pk_psndoc, substr(ddate, 0, 7) asc)"
			+"              group by pk_psndoc"
			+"             "
			+"             ) b"
			+"    on a.pk_psndoc = b.pk_psndoc" +whereday +" order by a.pk_psndoc ,a.vperiod";
		
		IUAPQueryBS bs= NCLocator.getInstance().lookup(IUAPQueryBS.class);
		ArrayList<PaiPanReportVO> list1 = (ArrayList<PaiPanReportVO>)bs.executeQuery(sql, new BeanListProcessor(PaiPanReportVO.class));
		PaiPanReportVO[] paipanvos = null;
		if(list1!=null&&list1.size()>0){
			paipanvos = list1.toArray(new PaiPanReportVO[0]);
		}
		
//		int isamerow = 1;
//		Integer beginindex =0;
//		String lastmonth = "",lastpsn="";
//		for(int i=0;i<paipanvos.length;i++){
//			PaiPanReportVO paipanvo = paipanvos[i];
//			
//			if(lastmonth.equals(paipanvo.getVperiod()) && lastpsn.equals(paipanvo.getPk_psndoc())){
//				isamerow =1;
//				
//				
//			}else{
//				Integer nperiodnum = isamerow;
//				for(int j = beginindex; j<=i; j++ ){
//					paipanvos[j].setNperiodnum(nperiodnum);
//				}
//				isamerow=0;
//				beginindex = i;
//			}
//			lastmonth = paipanvo.getVperiod();
//			lastpsn=paipanvo.getPk_psndoc();
//		}
		return paipanvos;
	}
	
	
	public PaiPanReportVO[] getPaiPanReportVO2() throws BusinessException{

		getBillCardPanel().getBillModel().clearBodyData();
		Object days = getBillCardPanel().getHeadItem("days").getValueObject();
		Object ddate = getBillCardPanel().getHeadItem("ddate").getValueObject();
		Object denddate = getBillCardPanel().getHeadItem("denddate").getValueObject();
		String[] pks_bb = ((UIRefPane)getBillCardPanel().getHeadItem("pk_bb").getComponent()).getRefPKs();
		if(pks_bb==null||pks_bb.length<=0){
			//			MessageDialog.showHintDlg(this.getBillUI(), "提示", "请选择考勤类别");
			//			return;
		}
		String[] pks_psndoc = ((UIRefPane)getBillCardPanel().getHeadItem("pk_psndoc").getComponent()).getRefPKs();
		String[] pks_dept = ((UIRefPane)getBillCardPanel().getHeadItem("pk_dept").getComponent()).getRefPKs();
//		String wheresql = " tb.lbbm like '99%' ";去掉编码限制
		String wheresql = " 1=1 ";
		if(ddate!=null&&ddate.toString().trim().length()>0){
			wheresql+=" and b.ddate>='"+ddate+"' ";
		}
		if(denddate!=null&&denddate.toString().trim().length()>0){
			wheresql+=" and b.ddate<='"+denddate+"' ";
		}
		if(pks_bb!=null&&pks_bb.length>0){
			ArrayList<String> list = new ArrayList<String>();
			list.addAll(Arrays.asList(pks_bb));
			wheresql += " "+HRPPubTool.formInSQL("b.pk_bb", list)+" ";
		}
		if(pks_psndoc!=null&&pks_psndoc.length>0){
			ArrayList<String> list = new ArrayList<String>();
			list.addAll(Arrays.asList(pks_psndoc));
			wheresql += " "+HRPPubTool.formInSQL("b.pk_psndoc", list)+" ";
		}
		if(pks_dept!=null&&pks_dept.length>0){
			ArrayList<String> list = new ArrayList<String>();
			list.addAll(Arrays.asList(pks_dept));
			wheresql += " "+HRPPubTool.formInSQL("b.pk_dept", list)+" ";
		}
		String whereday = "";
		if(days!=null&&days.toString().trim().length()>0){
			whereday = " where  nnum>='"+days.toString()+"'";
		}
		
		String wheredept = 	" and b.pk_dept in (select pk_dept from trtam_deptdoc_kq where isnull(dr,0)=0 and  pk_corp='" + _getCorp().getPrimaryKey()+ "' and isnull(bisseal,'N')='N' and pk_dept in (select pk_docid from bd_tr_userpower where isnull(dr,0)=0 and pk_user='"+_getOperator()+"' and powertype=0 and  pk_corp='"+_getCorp().getPrimaryKey()+"')) ";
		
//		String sql = " select distinct  pk_dept, pk_psndoc, psnname, pk_bb, nnum,lbmc,pk_bbz, pk_dd, biszb, nperiodnum from ( select pk_dept,"
//			+"       pk_psndoc,"
//			+"       psnname,"
//			+"       pk_bb,"
//			+"       sum(days) over(partition by pk_psndoc order by  pk_psndoc) nnum,"
//			+"       lbmc,"
//			+"       pk_bbz,"
//			+"       pk_dd,"
//			+"       biszb,"
//			//+"       ddate vperiod,"
//			+"       (months_between(to_date(maxdate,'yyyy-mm'),to_date(mindate,'yyyy-mm')) + 1) nperiodnum"
//			+"  from (select b.pk_dept,"
//			+"               kqks.vname,"
//			+"               b.pk_psndoc,"
//			+"               kqry.psnname,"
//			+"               pk_bb,"
//			+"               tb.lbmc,"
//			+"               substr(ddate, 0, 7) ddate,"
//			+"               pk_bbz,"
//			+"               pk_dd,"
//			+"               biszb,"
//			+"               sum(decode(substr(tb.lbmc, 0, 2), '半天', 0.5, 1)) days,"
//			+"               sum(count(b.dr)) over(partition by b.pk_psndoc order by b.pk_psndoc),"
//			+"				 max(substr(ddate, 0, 7)) keep(dense_rank FIRST order by substr(ddate, 0, 7),b.pk_psndoc) over (partition by b.pk_psndoc) as mindate,"
//			+"				 max(substr(ddate, 0, 7)) keep(dense_rank LAST order by substr(ddate, 0, 7),b.pk_psndoc) over (partition by b.pk_psndoc) as maxdate"
//			+"          from trtam_paiban_b b"
//			+"         inner join tbm_bclb tb"
//			+"            on b.pk_bb = tb.pk_bclbid"
//			+"          left join trtam_deptdoc_kq kqks"
//			+"            on b.pk_dept = kqks.pk_dept"
//			+"          left join bd_psndoc kqry"
//			+"            on b.pk_psndoc = kqry.pk_psndoc "
//			+"          where b.pk_corp='"+_getCorp().getPrimaryKey()+"' and isnull(b.dr,0)=0 "
//			+" and "+wheresql+" "+wheredept+" "
//			+"         group by b.pk_dept,"
//			+"                  kqks.vname,"
//			+"                  b.pk_psndoc,"
//			+"                  kqry.psnname,"
//			+"                  pk_bb,"
//			+"                  tb.lbmc,"
//			+"                  substr(ddate, 0, 7),"
//			+"                  pk_bbz,"
//			+"                  pk_dd,"
//			+"                  biszb"        
//			+"         order by b.pk_psndoc, substr(ddate, 0, 7) asc) "
//			+" group by pk_dept,"
//			+"          pk_psndoc,"
//			+"          psnname,"
//			+"          pk_bb,"
//			+"          days,"
//			+"          lbmc,"
//			+"          pk_bbz,"
//			+"          pk_dd,"
//			+"          biszb,"
//			+"          ddate, mindate, maxdate order by pk_dept,pk_psndoc,lbmc, ddate asc ) "+whereday +"order by pk_psndoc";
		
		String sql = "select pk_dept,"
			+"       a.pk_psndoc,"
			+"       psnname,"
			+"       pk_bb,"
			+"       nnum,"
			+"       lbmc,"
			+"       pk_bbz,"
			+"       pk_dd,"
			+"       biszb,"
			+"       b.nperiodnum"
			+"  from (select distinct pk_dept,"
			+"                        pk_psndoc,"
			+"                        psnname,"
			+"                        pk_bb,"
			+"                        nnum,"
			+"                        lbmc,"
			+"                        pk_bbz,"
			+"                        pk_dd,"
			+"                        biszb"
			+"          from (select pk_dept,"
			+"                       pk_psndoc,"
			+"                       psnname,"
			+"                      pk_bb,"
			+"                       sum(days) over(partition by pk_psndoc,pk_bb order by pk_psndoc) nnum,"
			+"                       lbmc,"
			+"                       pk_bbz,"
			+"                       pk_dd,"
			+"                       biszb"
			+"                  from (select b.pk_dept,"
			+"                               kqks.vname,"
			+"                               b.pk_psndoc,"
			+"                               kqry.psnname,"
			+"                               pk_bb,"
			+"                               tb.lbmc,"
			+"                               substr(ddate, 0, 7) ddate,"
			+"                               pk_bbz,"
			+"                               pk_dd,"
			+"                               biszb,"
			+"                               sum(decode(substr(tb.lbmc, 0, 2),"
			+"                                          '半天',"
			+"                                          0.5,"
			+"                                          1)) days"
			+"                          from trtam_paiban_b b"
			+"                         inner join tbm_bclb tb"
			+"                            on b.pk_bb = tb.pk_bclbid"
			+"                          left join trtam_deptdoc_kq kqks"
			+"                            on b.pk_dept = kqks.pk_dept"
			+"                          left join bd_psndoc kqry"
			+"                            on b.pk_psndoc = kqry.pk_psndoc"
			+"          where b.pk_corp='"+_getCorp().getPrimaryKey()+"' and isnull(b.dr,0)=0 "
			+" and "+wheresql+" "+wheredept+" "
			+"                         group by b.pk_dept,"
			+"                                  kqks.vname,"
			+"                                  b.pk_psndoc,"
			+"                                  kqry.psnname,"
			+"                                  pk_bb,"
			+"                                  tb.lbmc,"
			+"                                  substr(ddate, 0, 7),"
			+"                                  pk_bbz,"
			+"                                  pk_dd,"
			+"                                  biszb"
			+"                         order by b.pk_psndoc, substr(ddate, 0, 7) asc)"
			+"                 group by pk_dept,"
			+"                          pk_psndoc,"
			+"                          psnname,"
			+"                          pk_bb,"
			+"                          days,"
			+"                          lbmc,"
			+"                          pk_bbz,"
			+"                          pk_dd,"
			+"                          biszb,"
			+"                          ddate"
			
			+"                 order by pk_dept, pk_psndoc, lbmc, ddate asc)"
			+"         order by pk_psndoc) a"
			+"  left join (select count(ddate) nperiodnum, pk_psndoc"
			+"               from (select distinct b.pk_psndoc,"
			+"                                     kqry.psnname,"
//			+"                                     pk_bb,"
//			+"                                     tb.lbmc,"
			+"                                     substr(ddate, 0, 7) ddate"
			+"                       from trtam_paiban_b b"
			+"                      inner join tbm_bclb tb"
			+"                         on b.pk_bb = tb.pk_bclbid"
			+"                       left join trtam_deptdoc_kq kqks"
			+"                         on b.pk_dept = kqks.pk_dept"
			+"                       left join bd_psndoc kqry"
			+"                         on b.pk_psndoc = kqry.pk_psndoc"
			+"          where b.pk_corp='"+_getCorp().getPrimaryKey()+"' and isnull(b.dr,0)=0 "
			+" and "+wheresql+" "+wheredept+" "
			+"                      group by b.pk_dept,"
			+"                               kqks.vname,"
			+"                               b.pk_psndoc,"
			+"                               kqry.psnname,"
//			+"                               pk_bb,"
//			+"                               tb.lbmc,"
			+"                               substr(ddate, 0, 7)"
//			+"                               pk_bbz,"
//			+"                               pk_dd,"
//			+"                               biszb"
			+"                      order by b.pk_psndoc, substr(ddate, 0, 7) asc)"
			+"              group by pk_psndoc"
			+"             "
			+"             ) b"
			+"    on a.pk_psndoc = b.pk_psndoc "+whereday +" order by a.pk_psndoc"
;


		IUAPQueryBS bs= NCLocator.getInstance().lookup(IUAPQueryBS.class);
		ArrayList<PaiPanReportVO> list1 = (ArrayList<PaiPanReportVO>)bs.executeQuery(sql, new BeanListProcessor(PaiPanReportVO.class));
		PaiPanReportVO[] paipanvos = null;
		if(list1!=null&&list1.size()>0){
			paipanvos = list1.toArray(new PaiPanReportVO[0]);
		}
		
		return paipanvos;
	
	}
	public void setPaiPanReportVO() throws BusinessException{
		getBillCardPanel().getBillModel().clearBodyData();
		Object days = getBillCardPanel().getHeadItem("days").getValueObject();
		Object ddate = getBillCardPanel().getHeadItem("ddate").getValueObject();
		Object denddate = getBillCardPanel().getHeadItem("denddate").getValueObject();
		String[] pks_bb = ((UIRefPane)getBillCardPanel().getHeadItem("pk_bb").getComponent()).getRefPKs();
		if(pks_bb==null||pks_bb.length<=0){
			//			MessageDialog.showHintDlg(this.getBillUI(), "提示", "请选择考勤类别");
			//			return;
		}
		String[] pks_psndoc = ((UIRefPane)getBillCardPanel().getHeadItem("pk_psndoc").getComponent()).getRefPKs();
		String[] pks_dept = ((UIRefPane)getBillCardPanel().getHeadItem("pk_dept").getComponent()).getRefPKs();
//		String wheresql = " tb.lbbm like '99%' ";去掉编码限制
		String wheresql = " 1=1 ";
		if(ddate!=null&&ddate.toString().trim().length()>0){
			wheresql+=" and b.ddate>='"+ddate+"' ";
		}
		if(denddate!=null&&denddate.toString().trim().length()>0){
			wheresql+=" and b.ddate<='"+denddate+"' ";
		}
		if(pks_bb!=null&&pks_bb.length>0){
			ArrayList<String> list = new ArrayList<String>();
			list.addAll(Arrays.asList(pks_bb));
			wheresql += " "+HRPPubTool.formInSQL("b.pk_bb", list)+" ";
		}
		if(pks_psndoc!=null&&pks_psndoc.length>0){
			ArrayList<String> list = new ArrayList<String>();
			list.addAll(Arrays.asList(pks_psndoc));
			wheresql += " "+HRPPubTool.formInSQL("b.pk_psndoc", list)+" ";
		}
		if(pks_dept!=null&&pks_dept.length>0){
			ArrayList<String> list = new ArrayList<String>();
			list.addAll(Arrays.asList(pks_dept));
			wheresql += " "+HRPPubTool.formInSQL("b.pk_dept", list)+" ";
		}
		String wheredept = 	" and pk_dept in (select pk_dept from trtam_deptdoc_kq where isnull(dr,0)=0 and  pk_corp='" + _getCorp().getPrimaryKey()+ "' and isnull(bisseal,'N')='N' and pk_dept in (select pk_docid from bd_tr_userpower where isnull(dr,0)=0 and pk_user='"+_getOperator()+"' and powertype=0 and  pk_corp='"+_getCorp().getPrimaryKey()+"')) ";
		
		String sql = " select pk_psndoc,pk_bb,pk_dept,ddate,pk_bbz,pk_dd,biszb from trtam_paiban_b b inner join  ";
		sql+=" tbm_bclb tb on b.pk_bb=tb.pk_bclbid where b.pk_corp='"+_getCorp().getPrimaryKey()+"' and isnull(b.dr,0)=0 ";

		sql+=" and "+wheresql+" "+wheredept+" order by ddate";
		IUAPQueryBS bs= NCLocator.getInstance().lookup(IUAPQueryBS.class);
		list = (ArrayList<PaiPanReportVO>)bs.executeQuery(sql, new BeanListProcessor(PaiPanReportVO.class));
		if(list!=null&&list.size()>0){
			Object value = getBillCardPanel().getHeadItem("bishowperiod").getValueObject();
			if(value!=null&&new UFBoolean(value.toString()).booleanValue()){
				HashMap<String,HashMap<String,HashMap<String, PaiPanReportVO>>> map = new HashMap<String, HashMap<String,HashMap<String, PaiPanReportVO>>>();
				HashMap<String,HashMap<String,HashMap<String, PaiPanReportVO>>> map1 = new HashMap<String, HashMap<String,HashMap<String, PaiPanReportVO>>>();
				for(int i=0;i<list.size();i++){
					PaiPanReportVO vo = list.get(i);
					vo.setVperiod(vo.getDdate().toString().substring(0,7));// 期间
					String key = vo.getPk_psndoc()+vo.getPk_dept();// 人员+部门
					HashMap<String,HashMap<String, PaiPanReportVO>> map_t = map.get(key)!=null?map.get(key):new HashMap<String, HashMap<String, PaiPanReportVO>>();
					String key2 = vo.getVperiod();
					HashMap<String, PaiPanReportVO> map_tt = map_t.get(key2)!=null?map_t.get(key2):new HashMap<String, PaiPanReportVO>();
					PaiPanReportVO oldvo = map_tt.get(vo.getPk_bb())!= null?map_tt.get(vo.getPk_bb()):vo;
					// 取
					String lbmc = map11.get(vo.getPk_bb());
					UFDouble icount = new UFDouble(1);
					if(lbmc != null){
						if(lbmc.indexOf("半天") != -1){
							icount = icount.div(2);
						}
					}
					// zhanghua
					oldvo.setNnum(oldvo.getNnum()!=null?oldvo.getNnum().add(icount):new UFDouble(icount));
					map_tt.put(vo.getPk_bb(), oldvo);
					map_t.put(vo.getVperiod(), map_tt);
					map.put(key, map_t);
				}
				
				
				
				
				ArrayList<PaiPanReportVO> listvo = new ArrayList<PaiPanReportVO>();
				String[] keys = map.keySet().toArray(new String[0]);
				for(String key:keys){
					HashMap<String,HashMap<String, PaiPanReportVO>> map_t = map.get(key);
					String[] keys_t = map_t.keySet().toArray(new String[0]);
					Arrays.sort(keys_t);
					for(String key_t:keys_t){
						HashMap<String, PaiPanReportVO> map_tt = map_t.get(key_t);
						String[] keys_tt = map_tt.keySet().toArray(new String[0]);
						for(String key_tt:keys_tt){
							PaiPanReportVO vo = map_tt.get(key_tt);
							vo.setNperiodnum(keys_t.length);
							listvo.add(vo);
						}
					}
				}
				//
				
				ArrayList<PaiPanReportVO> paipanlist = new ArrayList<PaiPanReportVO>();
				PaiPanReportVO[] paipanvos = listvo.toArray(new PaiPanReportVO[0]);
				int monthmrgbegin = 0;
				Integer lastmonthstr = 0;
				String lastpsn = "",lastdept="",lastbclb="";
				
				for(int i=0;i<paipanvos.length;i++){
					PaiPanReportVO paipanvo = paipanvos[i];
					// 部门不相同，人员相同月数进行合并求和
					if(!lastdept.equals(paipanvo.getPk_dept())){
						if(lastpsn.equals(paipanvo.getPk_psndoc()) && lastbclb.equals(paipanvo.getPk_bb())){
							Integer currmonth = lastmonthstr + paipanvo.getNperiodnum();
							
							for(int j=monthmrgbegin;j<=i;j++){
								paipanvos[j].setNperiodnum(currmonth);
							}
							
						}else{
							monthmrgbegin = i;
							lastmonthstr = 0;
						}
					}
					
					
					lastmonthstr = paipanvo.getNperiodnum();
					lastpsn = paipanvo.getPk_psndoc();
					lastdept = paipanvo.getPk_dept();
					lastbclb = paipanvo.getPk_bb();
				}
				
				
				
				PaiPanReportVO[] paipanvos1 = null;
				if(days != null && !"".equals(days.toString())){
//					ArrayList<PaiPanReportVO> paipanlist = new ArrayList<PaiPanReportVO>();
//					PaiPanReportVO[] paipanvos = listvo.toArray(new PaiPanReportVO[0]);
					for(int i=0;i<paipanvos.length;i++){
						PaiPanReportVO paipanvo = paipanvos[i];
						UFDouble curday = new UFDouble(days.toString());
						if(paipanvo.getNnum().compareTo(curday) ==1){
							paipanlist.add(paipanvo);
						}
					}
					paipanvos1 = paipanlist.toArray(new PaiPanReportVO[0]);
				}else{
					paipanvos1 = paipanvos;
				}
				paipanvos11 = paipanvos1;
			}
		}
	}
	
	
	
	public void initBclbVO() throws BusinessException{
		String sql = "select * from tbm_bclb where dr=0";
		
		IUAPQueryBS bs= NCLocator.getInstance().lookup(IUAPQueryBS.class);
		ArrayList<BclbVO> list = (ArrayList<BclbVO>)bs.executeQuery(sql, new BeanListProcessor(BclbVO.class));
		if(list != null){
			for(int i=0;i<list.size();i++){
				String lbmc = list.get(i).getLbmc();
				String pk_bclbid = list.get(i).getPk_bclbid();
				map11.put(pk_bclbid, lbmc);
			}
			
			
		}
		

	}
	
}
