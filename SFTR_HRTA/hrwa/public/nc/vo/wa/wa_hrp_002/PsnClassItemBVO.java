/**
 * 
 */
package nc.vo.wa.wa_hrp_002;

import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFDouble;

/**
 * @author 宋旨昊
 * 2011-3-21上午10:28:14
 * 说明：
 */
public class PsnClassItemBVO extends SuperVO {
    private String pk_psn_item_b;
    private String pk_psn_item_h;
    private String pk_dept;
    
    private String csourceid;
    private String stafftype;//员工类型 编内编外 
    
    private UFDouble nsmny;//税金
    private UFDouble nsmny_nz;//税金
    private UFDouble naftersmny;//税后金
    private UFDouble nbcnsmny;//本次纳税基数
    
    private String ctaxtableid;//税率表主键
    
    private UFDouble noldsmny;//已纳税
    private UFDouble noldnsmny;//已纳税基数\
    
    private UFDouble noldsmny_nz;//已纳税
    private UFDouble noldnsmny_nz;//已纳税基数\
    
    private UFDouble ndkmny;//本次减免税额
    private UFDouble nsqqkmny;
    private UFDouble nbqqkmny;
    private UFDouble nsfmny;
    
    private UFBoolean biscount;
    private String vcountpsnid;
    private UFDate dcountdate;
    /*科室*/
    private String pk_corp;
	private String pk_psndoc;
	private UFBoolean biscjr;//残疾人标志
	private String pk_classitem;
	private String vmemo;
	private UFDouble nmny;
	private UFDouble nmny1;
	private UFDouble nmny2;
	private UFDouble nmny3;
	private UFDouble nmny4;
	private UFDouble nmny5;
	private UFDouble nmny6;
	private UFDouble nmny7;
	private UFDouble nmny8;
	private UFDouble nmny9;
	private UFDouble nmny10;
	private UFDouble nmny11;
	private UFDouble nmny12;
	private UFDouble nmny13;
	private UFDouble nmny14;
	private UFDouble nmny15;
	private UFDouble nmny16;
	private UFDouble nmny17;
	private UFDouble nmny18;
	private UFDouble nmny19;
	private UFDouble nmny20;
	private UFDouble nmny21;
	private UFDouble nmny22;
	private UFDouble nmny23;
	private UFDouble nmny24;
	private UFDouble nmny25;
	private UFDouble nmny26;
	private UFDouble nmny27;
	private UFDouble nmny28;
	private UFDouble nmny29;
	private UFDouble nmny30;
	private UFDouble nmny31;
	private UFDouble nmny32;
	private UFDouble nmny33;
	private UFDouble nmny34;
	private UFDouble nmny35;
	private UFDouble nmny36;
	private UFDouble nmny37;
	private UFDouble nmny38;
	private UFDouble nmny39;
	private UFDouble nmny40;
	private UFDouble nmny41;
	private UFDouble nmny42;
	private UFDouble nmny43;
	private UFDouble nmny44;
	private UFDouble nmny45;
	private UFDouble nmny46;
	private UFDouble nmny47;
	private UFDouble nmny48;
	private UFDouble nmny49;
	private UFDouble nmny50;
	private UFDouble nmny51;
	private UFDouble nmny52;
	private UFDouble nmny53;
	private UFDouble nmny54;
	private UFDouble nmny55;
	private UFDouble nmny56;
	private UFDouble nmny57;
	private UFDouble nmny58;
	private UFDouble nmny59;
	private UFDouble nmny60;
	private UFDouble nmny61;
	private UFDouble nmny62;
	private UFDouble nmny63;
	private UFDouble nmny64;
	private UFDouble nmny65;
	private UFDouble nmny66;
	private UFDouble nmny67;
	private UFDouble nmny68;
	private UFDouble nmny69;
	private UFDouble nmny70;
	private UFDouble nmny71;
	private UFDouble nmny72;
	private UFDouble nmny73;
	private UFDouble nmny74;
	private UFDouble nmny75;
	private UFDouble nmny76;
	private UFDouble nmny77;
	private UFDouble nmny78;
	private UFDouble nmny79;
	private UFDouble nmny80;
	private UFDouble nmny81;
	private UFDouble nmny82;
	private UFDouble nmny83;
	private UFDouble nmny84;
	private UFDouble nmny85;
	private UFDouble nmny86;
	private UFDouble nmny87;
	private UFDouble nmny88;
	private UFDouble nmny89;
	private UFDouble nmny90;
	private UFDouble nmny91;
	private UFDouble nmny92;
	private UFDouble nmny93;
	private UFDouble nmny94;
	private UFDouble nmny95;
	private UFDouble nmny96;
	private UFDouble nmny97;
	private UFDouble nmny98;
	private UFDouble nmny99;
	private UFDouble nmny100;
	private UFDouble nmny101;
	private UFDouble nmny102;
	private UFDouble nmny103;
	private UFDouble nmny104;
	private UFDouble nmny105;
	private UFDouble nmny106;
	private UFDouble nmny107;
	private UFDouble nmny108;
	private UFDouble nmny109;
	private UFDouble nmny110;
	private UFDouble nmny111;
	private UFDouble nmny112;
	private UFDouble nmny113;
	private UFDouble nmny114;
	private UFDouble nmny115;
	private UFDouble nmny116;
	private UFDouble nmny117;
	private UFDouble nmny118;
	private UFDouble nmny119;
	private UFDouble nmny120;
	private UFDouble nmny121;
	private UFDouble nmny122;
	private UFDouble nmny123;
	private UFDouble nmny124;
	private UFDouble nmny125;
	private UFDouble nmny126;
	private UFDouble nmny127;
	private UFDouble nmny128;
	private UFDouble nmny129;
	private UFDouble nmny130;
	private UFDouble nmny131;
	private UFDouble nmny132;
	private UFDouble nmny133;
	private UFDouble nmny134;
	private UFDouble nmny135;
	private UFDouble nmny136;
	private UFDouble nmny137;
	private UFDouble nmny138;
	private UFDouble nmny139;
	private UFDouble nmny140;
	private UFDouble nmny141;
	private UFDouble nmny142;
	private UFDouble nmny143;
	private UFDouble nmny144;
	private UFDouble nmny145;
	private UFDouble nmny146;
	private UFDouble nmny147;
	private UFDouble nmny148;
	private UFDouble nmny149;
	private UFDouble nmny150;

	
    private UFDateTime ts;
    private Integer dr;
    private String vdef1;
    private String vdef2;
    private String vdef3;
    private String vdef4;
    private String vdef5;
    private String vdef6;
    private String vdef7;
    private String vdef8;
    private UFDate vdef9;
    private UFBoolean vdef10;
    private UFDouble vdef11;
    private UFDouble vdef12;
    private UFDouble vdef13;
    private UFDouble vdef14;
    private UFDouble vdef15;
    private String vdef16;
    private String vdef17;
    private String vdef18;
    private UFDate vdef19;
    private UFDateTime vdef20;
    
    
    
    
    private String pk_wa_dept; //支付部门    qiutian 2012-5-22  G_HR_XZ1205003
    
    
    

	public String getPk_wa_dept() {
		return pk_wa_dept;
	}

	public void setPk_wa_dept(String pk_wa_dept) {
		this.pk_wa_dept = pk_wa_dept;
	}

	public UFDouble getNmny51() {
		return nmny51;
	}

	public void setNmny51(UFDouble nmny51) {
		this.nmny51 = nmny51;
	}

	public UFDouble getNmny52() {
		return nmny52;
	}

	public void setNmny52(UFDouble nmny52) {
		this.nmny52 = nmny52;
	}

	public UFDouble getNmny53() {
		return nmny53;
	}

	public void setNmny53(UFDouble nmny53) {
		this.nmny53 = nmny53;
	}

	public UFDouble getNmny54() {
		return nmny54;
	}

	public void setNmny54(UFDouble nmny54) {
		this.nmny54 = nmny54;
	}

	public UFDouble getNmny55() {
		return nmny55;
	}

	public void setNmny55(UFDouble nmny55) {
		this.nmny55 = nmny55;
	}

	public UFDouble getNmny56() {
		return nmny56;
	}

	public void setNmny56(UFDouble nmny56) {
		this.nmny56 = nmny56;
	}

	public UFDouble getNmny57() {
		return nmny57;
	}

	public void setNmny57(UFDouble nmny57) {
		this.nmny57 = nmny57;
	}

	public UFDouble getNmny58() {
		return nmny58;
	}

	public void setNmny58(UFDouble nmny58) {
		this.nmny58 = nmny58;
	}

	public UFDouble getNmny59() {
		return nmny59;
	}

	public void setNmny59(UFDouble nmny59) {
		this.nmny59 = nmny59;
	}

	public UFDouble getNmny60() {
		return nmny60;
	}

	public void setNmny60(UFDouble nmny60) {
		this.nmny60 = nmny60;
	}

	public UFDouble getNmny61() {
		return nmny61;
	}

	public void setNmny61(UFDouble nmny61) {
		this.nmny61 = nmny61;
	}

	public UFDouble getNmny62() {
		return nmny62;
	}

	public void setNmny62(UFDouble nmny62) {
		this.nmny62 = nmny62;
	}

	public UFDouble getNmny63() {
		return nmny63;
	}

	public void setNmny63(UFDouble nmny63) {
		this.nmny63 = nmny63;
	}

	public UFDouble getNmny64() {
		return nmny64;
	}

	public void setNmny64(UFDouble nmny64) {
		this.nmny64 = nmny64;
	}

	public UFDouble getNmny65() {
		return nmny65;
	}

	public void setNmny65(UFDouble nmny65) {
		this.nmny65 = nmny65;
	}

	public UFDouble getNmny66() {
		return nmny66;
	}

	public void setNmny66(UFDouble nmny66) {
		this.nmny66 = nmny66;
	}

	public UFDouble getNmny67() {
		return nmny67;
	}

	public void setNmny67(UFDouble nmny67) {
		this.nmny67 = nmny67;
	}

	public UFDouble getNmny68() {
		return nmny68;
	}

	public void setNmny68(UFDouble nmny68) {
		this.nmny68 = nmny68;
	}

	public UFDouble getNmny69() {
		return nmny69;
	}

	public void setNmny69(UFDouble nmny69) {
		this.nmny69 = nmny69;
	}

	public UFDouble getNmny70() {
		return nmny70;
	}

	public void setNmny70(UFDouble nmny70) {
		this.nmny70 = nmny70;
	}

	public UFDouble getNmny71() {
		return nmny71;
	}

	public void setNmny71(UFDouble nmny71) {
		this.nmny71 = nmny71;
	}

	public UFDouble getNmny72() {
		return nmny72;
	}

	public void setNmny72(UFDouble nmny72) {
		this.nmny72 = nmny72;
	}

	public UFDouble getNmny73() {
		return nmny73;
	}

	public void setNmny73(UFDouble nmny73) {
		this.nmny73 = nmny73;
	}

	public UFDouble getNmny74() {
		return nmny74;
	}

	public void setNmny74(UFDouble nmny74) {
		this.nmny74 = nmny74;
	}

	public UFDouble getNmny75() {
		return nmny75;
	}

	public void setNmny75(UFDouble nmny75) {
		this.nmny75 = nmny75;
	}

	public UFDouble getNmny76() {
		return nmny76;
	}

	public void setNmny76(UFDouble nmny76) {
		this.nmny76 = nmny76;
	}

	public UFDouble getNmny77() {
		return nmny77;
	}

	public void setNmny77(UFDouble nmny77) {
		this.nmny77 = nmny77;
	}

	public UFDouble getNmny78() {
		return nmny78;
	}

	public void setNmny78(UFDouble nmny78) {
		this.nmny78 = nmny78;
	}

	public UFDouble getNmny79() {
		return nmny79;
	}

	public void setNmny79(UFDouble nmny79) {
		this.nmny79 = nmny79;
	}

	public UFDouble getNmny80() {
		return nmny80;
	}

	public void setNmny80(UFDouble nmny80) {
		this.nmny80 = nmny80;
	}

	public UFDouble getNmny81() {
		return nmny81;
	}

	public void setNmny81(UFDouble nmny81) {
		this.nmny81 = nmny81;
	}

	public UFDouble getNmny82() {
		return nmny82;
	}

	public void setNmny82(UFDouble nmny82) {
		this.nmny82 = nmny82;
	}

	public UFDouble getNmny83() {
		return nmny83;
	}

	public void setNmny83(UFDouble nmny83) {
		this.nmny83 = nmny83;
	}

	public UFDouble getNmny84() {
		return nmny84;
	}

	public void setNmny84(UFDouble nmny84) {
		this.nmny84 = nmny84;
	}

	public UFDouble getNmny85() {
		return nmny85;
	}

	public void setNmny85(UFDouble nmny85) {
		this.nmny85 = nmny85;
	}

	public UFDouble getNmny86() {
		return nmny86;
	}

	public void setNmny86(UFDouble nmny86) {
		this.nmny86 = nmny86;
	}

	public UFDouble getNmny87() {
		return nmny87;
	}

	public void setNmny87(UFDouble nmny87) {
		this.nmny87 = nmny87;
	}

	public UFDouble getNmny88() {
		return nmny88;
	}

	public void setNmny88(UFDouble nmny88) {
		this.nmny88 = nmny88;
	}

	public UFDouble getNmny89() {
		return nmny89;
	}

	public void setNmny89(UFDouble nmny89) {
		this.nmny89 = nmny89;
	}

	public UFDouble getNmny90() {
		return nmny90;
	}

	public void setNmny90(UFDouble nmny90) {
		this.nmny90 = nmny90;
	}

	public UFDouble getNmny91() {
		return nmny91;
	}

	public void setNmny91(UFDouble nmny91) {
		this.nmny91 = nmny91;
	}

	public UFDouble getNmny92() {
		return nmny92;
	}

	public void setNmny92(UFDouble nmny92) {
		this.nmny92 = nmny92;
	}

	public UFDouble getNmny93() {
		return nmny93;
	}

	public void setNmny93(UFDouble nmny93) {
		this.nmny93 = nmny93;
	}

	public UFDouble getNmny94() {
		return nmny94;
	}

	public void setNmny94(UFDouble nmny94) {
		this.nmny94 = nmny94;
	}

	public UFDouble getNmny95() {
		return nmny95;
	}

	public void setNmny95(UFDouble nmny95) {
		this.nmny95 = nmny95;
	}

	public UFDouble getNmny96() {
		return nmny96;
	}

	public void setNmny96(UFDouble nmny96) {
		this.nmny96 = nmny96;
	}

	public UFDouble getNmny97() {
		return nmny97;
	}

	public void setNmny97(UFDouble nmny97) {
		this.nmny97 = nmny97;
	}

	public UFDouble getNmny98() {
		return nmny98;
	}

	public void setNmny98(UFDouble nmny98) {
		this.nmny98 = nmny98;
	}

	public UFDouble getNmny99() {
		return nmny99;
	}

	public void setNmny99(UFDouble nmny99) {
		this.nmny99 = nmny99;
	}

	public UFDouble getNmny100() {
		return nmny100;
	}

	public void setNmny100(UFDouble nmny100) {
		this.nmny100 = nmny100;
	}

	public UFDouble getNmny101() {
		return nmny101;
	}

	public void setNmny101(UFDouble nmny101) {
		this.nmny101 = nmny101;
	}

	public UFDouble getNmny102() {
		return nmny102;
	}

	public void setNmny102(UFDouble nmny102) {
		this.nmny102 = nmny102;
	}

	public UFDouble getNmny103() {
		return nmny103;
	}

	public void setNmny103(UFDouble nmny103) {
		this.nmny103 = nmny103;
	}

	public UFDouble getNmny104() {
		return nmny104;
	}

	public void setNmny104(UFDouble nmny104) {
		this.nmny104 = nmny104;
	}

	public UFDouble getNmny105() {
		return nmny105;
	}

	public void setNmny105(UFDouble nmny105) {
		this.nmny105 = nmny105;
	}

	public UFDouble getNmny106() {
		return nmny106;
	}

	public void setNmny106(UFDouble nmny106) {
		this.nmny106 = nmny106;
	}

	public UFDouble getNmny107() {
		return nmny107;
	}

	public void setNmny107(UFDouble nmny107) {
		this.nmny107 = nmny107;
	}

	public UFDouble getNmny108() {
		return nmny108;
	}

	public void setNmny108(UFDouble nmny108) {
		this.nmny108 = nmny108;
	}

	public UFDouble getNmny109() {
		return nmny109;
	}

	public void setNmny109(UFDouble nmny109) {
		this.nmny109 = nmny109;
	}

	public UFDouble getNmny110() {
		return nmny110;
	}

	public void setNmny110(UFDouble nmny110) {
		this.nmny110 = nmny110;
	}

	public UFDouble getNmny111() {
		return nmny111;
	}

	public void setNmny111(UFDouble nmny111) {
		this.nmny111 = nmny111;
	}

	public UFDouble getNmny112() {
		return nmny112;
	}

	public void setNmny112(UFDouble nmny112) {
		this.nmny112 = nmny112;
	}

	public UFDouble getNmny113() {
		return nmny113;
	}

	public void setNmny113(UFDouble nmny113) {
		this.nmny113 = nmny113;
	}

	public UFDouble getNmny114() {
		return nmny114;
	}

	public void setNmny114(UFDouble nmny114) {
		this.nmny114 = nmny114;
	}

	public UFDouble getNmny115() {
		return nmny115;
	}

	public void setNmny115(UFDouble nmny115) {
		this.nmny115 = nmny115;
	}

	public UFDouble getNmny116() {
		return nmny116;
	}

	public void setNmny116(UFDouble nmny116) {
		this.nmny116 = nmny116;
	}

	public UFDouble getNmny117() {
		return nmny117;
	}

	public void setNmny117(UFDouble nmny117) {
		this.nmny117 = nmny117;
	}

	public UFDouble getNmny118() {
		return nmny118;
	}

	public void setNmny118(UFDouble nmny118) {
		this.nmny118 = nmny118;
	}

	public UFDouble getNmny119() {
		return nmny119;
	}

	public void setNmny119(UFDouble nmny119) {
		this.nmny119 = nmny119;
	}

	public UFDouble getNmny120() {
		return nmny120;
	}

	public void setNmny120(UFDouble nmny120) {
		this.nmny120 = nmny120;
	}

	public UFDouble getNmny121() {
		return nmny121;
	}

	public void setNmny121(UFDouble nmny121) {
		this.nmny121 = nmny121;
	}

	public UFDouble getNmny122() {
		return nmny122;
	}

	public void setNmny122(UFDouble nmny122) {
		this.nmny122 = nmny122;
	}

	public UFDouble getNmny123() {
		return nmny123;
	}

	public void setNmny123(UFDouble nmny123) {
		this.nmny123 = nmny123;
	}

	public UFDouble getNmny124() {
		return nmny124;
	}

	public void setNmny124(UFDouble nmny124) {
		this.nmny124 = nmny124;
	}

	public UFDouble getNmny125() {
		return nmny125;
	}

	public void setNmny125(UFDouble nmny125) {
		this.nmny125 = nmny125;
	}

	public UFDouble getNmny126() {
		return nmny126;
	}

	public void setNmny126(UFDouble nmny126) {
		this.nmny126 = nmny126;
	}

	public UFDouble getNmny127() {
		return nmny127;
	}

	public void setNmny127(UFDouble nmny127) {
		this.nmny127 = nmny127;
	}

	public UFDouble getNmny128() {
		return nmny128;
	}

	public void setNmny128(UFDouble nmny128) {
		this.nmny128 = nmny128;
	}

	public UFDouble getNmny129() {
		return nmny129;
	}

	public void setNmny129(UFDouble nmny129) {
		this.nmny129 = nmny129;
	}

	public UFDouble getNmny130() {
		return nmny130;
	}

	public void setNmny130(UFDouble nmny130) {
		this.nmny130 = nmny130;
	}

	public UFDouble getNmny131() {
		return nmny131;
	}

	public void setNmny131(UFDouble nmny131) {
		this.nmny131 = nmny131;
	}

	public UFDouble getNmny132() {
		return nmny132;
	}

	public void setNmny132(UFDouble nmny132) {
		this.nmny132 = nmny132;
	}

	public UFDouble getNmny133() {
		return nmny133;
	}

	public void setNmny133(UFDouble nmny133) {
		this.nmny133 = nmny133;
	}

	public UFDouble getNmny134() {
		return nmny134;
	}

	public void setNmny134(UFDouble nmny134) {
		this.nmny134 = nmny134;
	}

	public UFDouble getNmny135() {
		return nmny135;
	}

	public void setNmny135(UFDouble nmny135) {
		this.nmny135 = nmny135;
	}

	public UFDouble getNmny136() {
		return nmny136;
	}

	public void setNmny136(UFDouble nmny136) {
		this.nmny136 = nmny136;
	}

	public UFDouble getNmny137() {
		return nmny137;
	}

	public void setNmny137(UFDouble nmny137) {
		this.nmny137 = nmny137;
	}

	public UFDouble getNmny138() {
		return nmny138;
	}

	public void setNmny138(UFDouble nmny138) {
		this.nmny138 = nmny138;
	}

	public UFDouble getNmny139() {
		return nmny139;
	}

	public void setNmny139(UFDouble nmny139) {
		this.nmny139 = nmny139;
	}

	public UFDouble getNmny140() {
		return nmny140;
	}

	public void setNmny140(UFDouble nmny140) {
		this.nmny140 = nmny140;
	}

	public UFDouble getNmny141() {
		return nmny141;
	}

	public void setNmny141(UFDouble nmny141) {
		this.nmny141 = nmny141;
	}

	public UFDouble getNmny142() {
		return nmny142;
	}

	public void setNmny142(UFDouble nmny142) {
		this.nmny142 = nmny142;
	}

	public UFDouble getNmny143() {
		return nmny143;
	}

	public void setNmny143(UFDouble nmny143) {
		this.nmny143 = nmny143;
	}

	public UFDouble getNmny144() {
		return nmny144;
	}

	public void setNmny144(UFDouble nmny144) {
		this.nmny144 = nmny144;
	}

	public UFDouble getNmny145() {
		return nmny145;
	}

	public void setNmny145(UFDouble nmny145) {
		this.nmny145 = nmny145;
	}

	public UFDouble getNmny146() {
		return nmny146;
	}

	public void setNmny146(UFDouble nmny146) {
		this.nmny146 = nmny146;
	}

	public UFDouble getNmny147() {
		return nmny147;
	}

	public void setNmny147(UFDouble nmny147) {
		this.nmny147 = nmny147;
	}

	public UFDouble getNmny148() {
		return nmny148;
	}

	public void setNmny148(UFDouble nmny148) {
		this.nmny148 = nmny148;
	}

	public UFDouble getNmny149() {
		return nmny149;
	}

	public void setNmny149(UFDouble nmny149) {
		this.nmny149 = nmny149;
	}

	public UFDouble getNmny150() {
		return nmny150;
	}

	public void setNmny150(UFDouble nmny150) {
		this.nmny150 = nmny150;
	}

	/**
	 * 
	 */
	public PsnClassItemBVO() {
		// TODO 自动生成构造函数存根
	}

	/* （非 Javadoc）
	 * @see nc.vo.pub.SuperVO#getPKFieldName()
	 */
	@Override
	public String getPKFieldName() {
		// TODO 自动生成方法存根
		return "pk_psn_item_b";
	}

	/* （非 Javadoc）
	 * @see nc.vo.pub.SuperVO#getParentPKFieldName()
	 */
	@Override
	public String getParentPKFieldName() {
		// TODO 自动生成方法存根
		return "pk_psn_item_h";
	}

	/* （非 Javadoc）
	 * @see nc.vo.pub.SuperVO#getTableName()
	 */
	@Override
	public String getTableName() {
		// TODO 自动生成方法存根
		return "wa_psn_item_b";
	}

	public Integer getDr() {
		return dr;
	}

	public void setDr(Integer dr) {
		this.dr = dr;
	}

	public UFDouble getNmny() {
		return nmny;
	}

	public void setNmny(UFDouble nmny) {
		this.nmny = nmny;
	}

	public String getPk_classitem() {
		return pk_classitem;
	}

	public void setPk_classitem(String pk_classitem) {
		this.pk_classitem = pk_classitem;
	}

	public String getPk_corp() {
		return pk_corp;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}

	public String getPk_dept() {
		return pk_dept;
	}

	public void setPk_dept(String pk_dept) {
		this.pk_dept = pk_dept;
	}

	public String getPk_psn_item_b() {
		return pk_psn_item_b;
	}

	public void setPk_psn_item_b(String pk_psn_item_b) {
		this.pk_psn_item_b = pk_psn_item_b;
	}

	public String getPk_psn_item_h() {
		return pk_psn_item_h;
	}

	public void setPk_psn_item_h(String pk_psn_item_h) {
		this.pk_psn_item_h = pk_psn_item_h;
	}

	public String getPk_psndoc() {
		return pk_psndoc;
	}

	public void setPk_psndoc(String pk_psndoc) {
		this.pk_psndoc = pk_psndoc;
	}

	public UFDateTime getTs() {
		return ts;
	}

	public void setTs(UFDateTime ts) {
		this.ts = ts;
	}

	public String getVdef1() {
		return vdef1;
	}

	public void setVdef1(String vdef1) {
		this.vdef1 = vdef1;
	}

	public UFBoolean getVdef10() {
		return vdef10;
	}

	public void setVdef10(UFBoolean vdef10) {
		this.vdef10 = vdef10;
	}

	public UFDouble getVdef11() {
		return vdef11;
	}

	public void setVdef11(UFDouble vdef11) {
		this.vdef11 = vdef11;
	}

	public UFDouble getVdef12() {
		return vdef12;
	}

	public void setVdef12(UFDouble vdef12) {
		this.vdef12 = vdef12;
	}

	public UFDouble getVdef13() {
		return vdef13;
	}

	public void setVdef13(UFDouble vdef13) {
		this.vdef13 = vdef13;
	}

	public UFDouble getVdef14() {
		return vdef14;
	}

	public void setVdef14(UFDouble vdef14) {
		this.vdef14 = vdef14;
	}

	public UFDouble getVdef15() {
		return vdef15;
	}

	public void setVdef15(UFDouble vdef15) {
		this.vdef15 = vdef15;
	}

	public String getVdef16() {
		return vdef16;
	}

	public void setVdef16(String vdef16) {
		this.vdef16 = vdef16;
	}

	public String getVdef17() {
		return vdef17;
	}

	public void setVdef17(String vdef17) {
		this.vdef17 = vdef17;
	}

	public String getVdef18() {
		return vdef18;
	}

	public void setVdef18(String vdef18) {
		this.vdef18 = vdef18;
	}

	public UFDate getVdef19() {
		return vdef19;
	}

	public void setVdef19(UFDate vdef19) {
		this.vdef19 = vdef19;
	}

	public String getVdef2() {
		return vdef2;
	}

	public void setVdef2(String vdef2) {
		this.vdef2 = vdef2;
	}

	public UFDateTime getVdef20() {
		return vdef20;
	}

	public void setVdef20(UFDateTime vdef20) {
		this.vdef20 = vdef20;
	}

	public String getVdef3() {
		return vdef3;
	}

	public void setVdef3(String vdef3) {
		this.vdef3 = vdef3;
	}

	public String getVdef4() {
		return vdef4;
	}

	public void setVdef4(String vdef4) {
		this.vdef4 = vdef4;
	}

	public String getVdef5() {
		return vdef5;
	}

	public void setVdef5(String vdef5) {
		this.vdef5 = vdef5;
	}

	public String getVdef6() {
		return vdef6;
	}

	public void setVdef6(String vdef6) {
		this.vdef6 = vdef6;
	}

	public String getVdef7() {
		return vdef7;
	}

	public void setVdef7(String vdef7) {
		this.vdef7 = vdef7;
	}

	public String getVdef8() {
		return vdef8;
	}

	public void setVdef8(String vdef8) {
		this.vdef8 = vdef8;
	}

	public UFDate getVdef9() {
		return vdef9;
	}

	public void setVdef9(UFDate vdef9) {
		this.vdef9 = vdef9;
	}

	public String getVmemo() {
		return vmemo;
	}

	public void setVmemo(String vmemo) {
		this.vmemo = vmemo;
	}

	public UFDouble getNmny1() {
		return nmny1;
	}

	public void setNmny1(UFDouble nmny1) {
		this.nmny1 = nmny1;
	}

	public UFDouble getNmny10() {
		return nmny10;
	}

	public void setNmny10(UFDouble nmny10) {
		this.nmny10 = nmny10;
	}

	public UFDouble getNmny11() {
		return nmny11;
	}

	public void setNmny11(UFDouble nmny11) {
		this.nmny11 = nmny11;
	}

	public UFDouble getNmny12() {
		return nmny12;
	}

	public void setNmny12(UFDouble nmny12) {
		this.nmny12 = nmny12;
	}

	public UFDouble getNmny13() {
		return nmny13;
	}

	public void setNmny13(UFDouble nmny13) {
		this.nmny13 = nmny13;
	}

	public UFDouble getNmny14() {
		return nmny14;
	}

	public void setNmny14(UFDouble nmny14) {
		this.nmny14 = nmny14;
	}

	public UFDouble getNmny15() {
		return nmny15;
	}

	public void setNmny15(UFDouble nmny15) {
		this.nmny15 = nmny15;
	}

	public UFDouble getNmny16() {
		return nmny16;
	}

	public void setNmny16(UFDouble nmny16) {
		this.nmny16 = nmny16;
	}

	public UFDouble getNmny17() {
		return nmny17;
	}

	public void setNmny17(UFDouble nmny17) {
		this.nmny17 = nmny17;
	}

	public UFDouble getNmny18() {
		return nmny18;
	}

	public void setNmny18(UFDouble nmny18) {
		this.nmny18 = nmny18;
	}

	public UFDouble getNmny19() {
		return nmny19;
	}

	public void setNmny19(UFDouble nmny19) {
		this.nmny19 = nmny19;
	}

	public UFDouble getNmny2() {
		return nmny2;
	}

	public void setNmny2(UFDouble nmny2) {
		this.nmny2 = nmny2;
	}

	public UFDouble getNmny20() {
		return nmny20;
	}

	public void setNmny20(UFDouble nmny20) {
		this.nmny20 = nmny20;
	}

	public UFDouble getNmny21() {
		return nmny21;
	}

	public void setNmny21(UFDouble nmny21) {
		this.nmny21 = nmny21;
	}

	public UFDouble getNmny22() {
		return nmny22;
	}

	public void setNmny22(UFDouble nmny22) {
		this.nmny22 = nmny22;
	}

	public UFDouble getNmny23() {
		return nmny23;
	}

	public void setNmny23(UFDouble nmny23) {
		this.nmny23 = nmny23;
	}

	public UFDouble getNmny24() {
		return nmny24;
	}

	public void setNmny24(UFDouble nmny24) {
		this.nmny24 = nmny24;
	}

	public UFDouble getNmny25() {
		return nmny25;
	}

	public void setNmny25(UFDouble nmny25) {
		this.nmny25 = nmny25;
	}

	public UFDouble getNmny26() {
		return nmny26;
	}

	public void setNmny26(UFDouble nmny26) {
		this.nmny26 = nmny26;
	}

	public UFDouble getNmny27() {
		return nmny27;
	}

	public void setNmny27(UFDouble nmny27) {
		this.nmny27 = nmny27;
	}

	public UFDouble getNmny28() {
		return nmny28;
	}

	public void setNmny28(UFDouble nmny28) {
		this.nmny28 = nmny28;
	}

	public UFDouble getNmny29() {
		return nmny29;
	}

	public void setNmny29(UFDouble nmny29) {
		this.nmny29 = nmny29;
	}

	public UFDouble getNmny3() {
		return nmny3;
	}

	public void setNmny3(UFDouble nmny3) {
		this.nmny3 = nmny3;
	}

	public UFDouble getNmny30() {
		return nmny30;
	}

	public void setNmny30(UFDouble nmny30) {
		this.nmny30 = nmny30;
	}

	public UFDouble getNmny31() {
		return nmny31;
	}

	public void setNmny31(UFDouble nmny31) {
		this.nmny31 = nmny31;
	}

	public UFDouble getNmny32() {
		return nmny32;
	}

	public void setNmny32(UFDouble nmny32) {
		this.nmny32 = nmny32;
	}

	public UFDouble getNmny33() {
		return nmny33;
	}

	public void setNmny33(UFDouble nmny33) {
		this.nmny33 = nmny33;
	}

	public UFDouble getNmny34() {
		return nmny34;
	}

	public void setNmny34(UFDouble nmny34) {
		this.nmny34 = nmny34;
	}

	public UFDouble getNmny35() {
		return nmny35;
	}

	public void setNmny35(UFDouble nmny35) {
		this.nmny35 = nmny35;
	}

	public UFDouble getNmny36() {
		return nmny36;
	}

	public void setNmny36(UFDouble nmny36) {
		this.nmny36 = nmny36;
	}

	public UFDouble getNmny37() {
		return nmny37;
	}

	public void setNmny37(UFDouble nmny37) {
		this.nmny37 = nmny37;
	}

	public UFDouble getNmny38() {
		return nmny38;
	}

	public void setNmny38(UFDouble nmny38) {
		this.nmny38 = nmny38;
	}

	public UFDouble getNmny39() {
		return nmny39;
	}

	public void setNmny39(UFDouble nmny39) {
		this.nmny39 = nmny39;
	}

	public UFDouble getNmny4() {
		return nmny4;
	}

	public void setNmny4(UFDouble nmny4) {
		this.nmny4 = nmny4;
	}

	public UFDouble getNmny40() {
		return nmny40;
	}

	public void setNmny40(UFDouble nmny40) {
		this.nmny40 = nmny40;
	}

	public UFDouble getNmny41() {
		return nmny41;
	}

	public void setNmny41(UFDouble nmny41) {
		this.nmny41 = nmny41;
	}

	public UFDouble getNmny42() {
		return nmny42;
	}

	public void setNmny42(UFDouble nmny42) {
		this.nmny42 = nmny42;
	}

	public UFDouble getNmny43() {
		return nmny43;
	}

	public void setNmny43(UFDouble nmny43) {
		this.nmny43 = nmny43;
	}

	public UFDouble getNmny44() {
		return nmny44;
	}

	public void setNmny44(UFDouble nmny44) {
		this.nmny44 = nmny44;
	}

	public UFDouble getNmny45() {
		return nmny45;
	}

	public void setNmny45(UFDouble nmny45) {
		this.nmny45 = nmny45;
	}

	public UFDouble getNmny46() {
		return nmny46;
	}

	public void setNmny46(UFDouble nmny46) {
		this.nmny46 = nmny46;
	}

	public UFDouble getNmny47() {
		return nmny47;
	}

	public void setNmny47(UFDouble nmny47) {
		this.nmny47 = nmny47;
	}

	public UFDouble getNmny48() {
		return nmny48;
	}

	public void setNmny48(UFDouble nmny48) {
		this.nmny48 = nmny48;
	}

	public UFDouble getNmny49() {
		return nmny49;
	}

	public void setNmny49(UFDouble nmny49) {
		this.nmny49 = nmny49;
	}

	public UFDouble getNmny5() {
		return nmny5;
	}

	public void setNmny5(UFDouble nmny5) {
		this.nmny5 = nmny5;
	}

	public UFDouble getNmny50() {
		return nmny50;
	}

	public void setNmny50(UFDouble nmny50) {
		this.nmny50 = nmny50;
	}

	public UFDouble getNmny6() {
		return nmny6;
	}

	public void setNmny6(UFDouble nmny6) {
		this.nmny6 = nmny6;
	}

	public UFDouble getNmny7() {
		return nmny7;
	}

	public void setNmny7(UFDouble nmny7) {
		this.nmny7 = nmny7;
	}

	public UFDouble getNmny8() {
		return nmny8;
	}

	public void setNmny8(UFDouble nmny8) {
		this.nmny8 = nmny8;
	}

	public UFDouble getNmny9() {
		return nmny9;
	}

	public void setNmny9(UFDouble nmny9) {
		this.nmny9 = nmny9;
	}

	public UFDouble getNsmny() {
		return nsmny;
	}

	public void setNsmny(UFDouble nsmny) {
		this.nsmny = nsmny;
	}

	public UFDouble getNaftersmny() {
		return naftersmny;
	}

	public void setNaftersmny(UFDouble naftersmny) {
		this.naftersmny = naftersmny;
	}

	public UFDouble getNbcnsmny() {
		return nbcnsmny;
	}

	public void setNbcnsmny(UFDouble nbcnsmny) {
		this.nbcnsmny = nbcnsmny;
	}

	public UFDouble getNoldnsmny() {
		return noldnsmny;
	}

	public void setNoldnsmny(UFDouble noldnsmny) {
		this.noldnsmny = noldnsmny;
	}

	public UFDouble getNoldsmny() {
		return noldsmny;
	}

	public void setNoldsmny(UFDouble noldsmny) {
		this.noldsmny = noldsmny;
	}

	public String getCtaxtableid() {
		return ctaxtableid;
	}

	public void setCtaxtableid(String ctaxtableid) {
		this.ctaxtableid = ctaxtableid;
	}

	public UFBoolean getBiscount() {
		return biscount;
	}

	public void setBiscount(UFBoolean biscount) {
		this.biscount = biscount;
	}

	public UFDate getDcountdate() {
		return dcountdate;
	}

	public void setDcountdate(UFDate dcountdate) {
		this.dcountdate = dcountdate;
	}

	public String getVcountpsnid() {
		return vcountpsnid;
	}

	public void setVcountpsnid(String vcountpsnid) {
		this.vcountpsnid = vcountpsnid;
	}

	public UFDouble getNbqqkmny() {
		return nbqqkmny;
	}

	public void setNbqqkmny(UFDouble nbqqkmny) {
		this.nbqqkmny = nbqqkmny;
	}

	public UFDouble getNdkmny() {
		return ndkmny;
	}

	public void setNdkmny(UFDouble ndkmny) {
		this.ndkmny = ndkmny;
	}

	public UFDouble getNsfmny() {
		return nsfmny;
	}

	public void setNsfmny(UFDouble nsfmny) {
		this.nsfmny = nsfmny;
	}

	public UFDouble getNsqqkmny() {
		return nsqqkmny;
	}

	public void setNsqqkmny(UFDouble nsqqkmny) {
		this.nsqqkmny = nsqqkmny;
	}

	public UFBoolean getBiscjr() {
		return biscjr;
	}

	public void setBiscjr(UFBoolean biscjr) {
		this.biscjr = biscjr;
	}

	public String getCsourceid() {
		return csourceid;
	}

	public void setCsourceid(String csourceid) {
		this.csourceid = csourceid;
	}

	public UFDouble getNoldnsmny_nz() {
		return noldnsmny_nz;
	}

	public void setNoldnsmny_nz(UFDouble noldnsmny_nz) {
		this.noldnsmny_nz = noldnsmny_nz;
	}

	public UFDouble getNoldsmny_nz() {
		return noldsmny_nz;
	}

	public void setNoldsmny_nz(UFDouble noldsmny_nz) {
		this.noldsmny_nz = noldsmny_nz;
	}

	public UFDouble getNsmny_nz() {
		return nsmny_nz;
	}

	public void setNsmny_nz(UFDouble nsmny_nz) {
		this.nsmny_nz = nsmny_nz;
	}

	public String getStafftype() {
		return stafftype;
	}

	public void setStafftype(String stafftype) {
		this.stafftype = stafftype;
	}
	
	
}
