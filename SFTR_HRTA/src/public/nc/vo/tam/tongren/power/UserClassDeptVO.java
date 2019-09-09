package nc.vo.tam.tongren.power;
	
import nc.vo.pub.*;
import nc.vo.pub.lang.*;
	
/**
 * <b> 在此处简要描述此类的功能 </b>
 * <p>
 *     在此处添加此类的描述信息
 * </p>
 * 创建日期:2012-02-21 13:14:23
 * @author sqt
 * @version NCPrj 1.0
 */
@SuppressWarnings("serial")
public class UserClassDeptVO extends SuperVO {
	private String vdef3;
	private UFDate vdef4;
	private UFBoolean vdef5;
	private UFDouble vdef6;
	private String vdef1;
	private UFBoolean vdef10;
	private String vdef2;
	private UFDateTime ts;
	private UFDouble vdef7;
	private String pk_role;
	private String vdef8;
	private String pk_userclasspower;
	private String pk_docid;
	private UFDate vdef9;
	private String pk_corp;
	private String pk_user;
	private UFDouble dr;
	private String pk_module;
	private Integer powertype;

	public static final String VDEF3 = "vdef3";
	public static final String VDEF4 = "vdef4";
	public static final String VDEF5 = "vdef5";
	public static final String VDEF6 = "vdef6";
	public static final String VDEF1 = "vdef1";
	public static final String VDEF10 = "vdef10";
	public static final String VDEF2 = "vdef2";
	public static final String VDEF7 = "vdef7";
	public static final String PK_ROLE = "pk_role";
	public static final String VDEF8 = "vdef8";
	public static final String PK_USERCLASSPOWER = "pk_userclasspower";
	public static final String PK_DEPT = "pk_dept";
	public static final String VDEF9 = "vdef9";
	public static final String PK_CORP = "pk_corp";
	public static final String PK_USER = "pk_user";
			
	
	public String getPk_module() {
		return pk_module;
	}
	public void setPk_module(String pk_module) {
		this.pk_module = pk_module;
	}
	/**
	 * 属性vdef3的Getter方法.
	 * 创建日期:2012-02-21 13:14:23
	 * @return String
	 */
	public String getVdef3 () {
		return vdef3;
	}   
	/**
	 * 属性vdef3的Setter方法.
	 * 创建日期:2012-02-21 13:14:23
	 * @param newVdef3 String
	 */
	public void setVdef3 (String newVdef3 ) {
	 	this.vdef3 = newVdef3;
	} 	  
	/**
	 * 属性vdef4的Getter方法.
	 * 创建日期:2012-02-21 13:14:23
	 * @return UFDate
	 */
	public UFDate getVdef4 () {
		return vdef4;
	}   
	/**
	 * 属性vdef4的Setter方法.
	 * 创建日期:2012-02-21 13:14:23
	 * @param newVdef4 UFDate
	 */
	public void setVdef4 (UFDate newVdef4 ) {
	 	this.vdef4 = newVdef4;
	} 	  
	/**
	 * 属性vdef5的Getter方法.
	 * 创建日期:2012-02-21 13:14:23
	 * @return UFBoolean
	 */
	public UFBoolean getVdef5 () {
		return vdef5;
	}   
	/**
	 * 属性vdef5的Setter方法.
	 * 创建日期:2012-02-21 13:14:23
	 * @param newVdef5 UFBoolean
	 */
	public void setVdef5 (UFBoolean newVdef5 ) {
	 	this.vdef5 = newVdef5;
	} 	  
	/**
	 * 属性vdef6的Getter方法.
	 * 创建日期:2012-02-21 13:14:23
	 * @return UFDouble
	 */
	public UFDouble getVdef6 () {
		return vdef6;
	}   
	/**
	 * 属性vdef6的Setter方法.
	 * 创建日期:2012-02-21 13:14:23
	 * @param newVdef6 UFDouble
	 */
	public void setVdef6 (UFDouble newVdef6 ) {
	 	this.vdef6 = newVdef6;
	} 	  
	/**
	 * 属性vdef1的Getter方法.
	 * 创建日期:2012-02-21 13:14:23
	 * @return String
	 */
	public String getVdef1 () {
		return vdef1;
	}   
	/**
	 * 属性vdef1的Setter方法.
	 * 创建日期:2012-02-21 13:14:23
	 * @param newVdef1 String
	 */
	public void setVdef1 (String newVdef1 ) {
	 	this.vdef1 = newVdef1;
	} 	  
	/**
	 * 属性vdef10的Getter方法.
	 * 创建日期:2012-02-21 13:14:23
	 * @return UFBoolean
	 */
	public UFBoolean getVdef10 () {
		return vdef10;
	}   
	/**
	 * 属性vdef10的Setter方法.
	 * 创建日期:2012-02-21 13:14:23
	 * @param newVdef10 UFBoolean
	 */
	public void setVdef10 (UFBoolean newVdef10 ) {
	 	this.vdef10 = newVdef10;
	} 	  
	/**
	 * 属性vdef2的Getter方法.
	 * 创建日期:2012-02-21 13:14:23
	 * @return String
	 */
	public String getVdef2 () {
		return vdef2;
	}   
	/**
	 * 属性vdef2的Setter方法.
	 * 创建日期:2012-02-21 13:14:23
	 * @param newVdef2 String
	 */
	public void setVdef2 (String newVdef2 ) {
	 	this.vdef2 = newVdef2;
	} 	  
	/**
	 * 属性ts的Getter方法.
	 * 创建日期:2012-02-21 13:14:23
	 * @return UFDateTime
	 */
	public UFDateTime getTs () {
		return ts;
	}   
	/**
	 * 属性ts的Setter方法.
	 * 创建日期:2012-02-21 13:14:23
	 * @param newTs UFDateTime
	 */
	public void setTs (UFDateTime newTs ) {
	 	this.ts = newTs;
	} 	  
	/**
	 * 属性vdef7的Getter方法.
	 * 创建日期:2012-02-21 13:14:23
	 * @return UFDouble
	 */
	public UFDouble getVdef7 () {
		return vdef7;
	}   
	/**
	 * 属性vdef7的Setter方法.
	 * 创建日期:2012-02-21 13:14:23
	 * @param newVdef7 UFDouble
	 */
	public void setVdef7 (UFDouble newVdef7 ) {
	 	this.vdef7 = newVdef7;
	} 	  
	/**
	 * 属性pk_role的Getter方法.
	 * 创建日期:2012-02-21 13:14:23
	 * @return String
	 */
	public String getPk_role () {
		return pk_role;
	}   
	/**
	 * 属性pk_role的Setter方法.
	 * 创建日期:2012-02-21 13:14:23
	 * @param newPk_role String
	 */
	public void setPk_role (String newPk_role ) {
	 	this.pk_role = newPk_role;
	} 	  
	/**
	 * 属性vdef8的Getter方法.
	 * 创建日期:2012-02-21 13:14:23
	 * @return String
	 */
	public String getVdef8 () {
		return vdef8;
	}   
	/**
	 * 属性vdef8的Setter方法.
	 * 创建日期:2012-02-21 13:14:23
	 * @param newVdef8 String
	 */
	public void setVdef8 (String newVdef8 ) {
	 	this.vdef8 = newVdef8;
	} 	  


	public String getPk_userclasspower() {
		return pk_userclasspower;
	}
	public void setPk_userclasspower(String pk_userclasspower) {
		this.pk_userclasspower = pk_userclasspower;
	}
	public String getPk_docid() {
		return pk_docid;
	}
	public void setPk_docid(String pk_docid) {
		this.pk_docid = pk_docid;
	}
	public Integer getPowertype() {
		return powertype;
	}
	public void setPowertype(Integer powertype) {
		this.powertype = powertype;
	}
	/**
	 * 属性vdef9的Getter方法.
	 * 创建日期:2012-02-21 13:14:23
	 * @return UFDate
	 */
	public UFDate getVdef9 () {
		return vdef9;
	}   
	/**
	 * 属性vdef9的Setter方法.
	 * 创建日期:2012-02-21 13:14:23
	 * @param newVdef9 UFDate
	 */
	public void setVdef9 (UFDate newVdef9 ) {
	 	this.vdef9 = newVdef9;
	} 	  
	/**
	 * 属性pk_corp的Getter方法.
	 * 创建日期:2012-02-21 13:14:23
	 * @return String
	 */
	public String getPk_corp () {
		return pk_corp;
	}   
	/**
	 * 属性pk_corp的Setter方法.
	 * 创建日期:2012-02-21 13:14:23
	 * @param newPk_corp String
	 */
	public void setPk_corp (String newPk_corp ) {
	 	this.pk_corp = newPk_corp;
	} 	  
	/**
	 * 属性pk_user的Getter方法.
	 * 创建日期:2012-02-21 13:14:23
	 * @return String
	 */
	public String getPk_user () {
		return pk_user;
	}   
	/**
	 * 属性pk_user的Setter方法.
	 * 创建日期:2012-02-21 13:14:23
	 * @param newPk_user String
	 */
	public void setPk_user (String newPk_user ) {
	 	this.pk_user = newPk_user;
	} 	  
	/**
	 * 属性dr的Getter方法.
	 * 创建日期:2012-02-21 13:14:23
	 * @return UFDouble
	 */
	public UFDouble getDr () {
		return dr;
	}   
	/**
	 * 属性dr的Setter方法.
	 * 创建日期:2012-02-21 13:14:23
	 * @param newDr UFDouble
	 */
	public void setDr (UFDouble newDr ) {
	 	this.dr = newDr;
	} 	  
 
	/**
	  * <p>取得父VO主键字段.
	  * <p>
	  * 创建日期:2012-02-21 13:14:23
	  * @return java.lang.String
	  */
	public java.lang.String getParentPKFieldName() {
	    return null;
	}   
    
	/**
	  * <p>取得表主键.
	  * <p>
	  * 创建日期:2012-02-21 13:14:23
	  * @return java.lang.String
	  */
	public java.lang.String getPKFieldName() {
	  return "pk_userclasspower";
	}
    
	/**
	 * <p>返回表名称.
	 * <p>
	 * 创建日期:2012-02-21 13:14:23
	 * @return java.lang.String
	 */
	public java.lang.String getTableName() {
		return "bd_tr_userclasspower";
	}    
    
    /**
	  * 按照默认方式创建构造子.
	  *
	  * 创建日期:2012-02-21 13:14:23
	  */
     public UserClassDeptVO() {
		super();	
	}    
} 
