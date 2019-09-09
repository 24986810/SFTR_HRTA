package nc.vo.hi.wadoc;

import java.util.Hashtable;

/**
 * 此处插入类型描述。 创建日期：(2004-6-5 13:28:00)
 * 
 * @author：Administrator
 */
public class PsndocWadocMainVO extends nc.vo.pub.CircularlyAccessibleValueObject {

    private static final long serialVersionUID = -8569452394178203946L;
    private java.lang.String deptCode = null;
    private java.lang.String deptName = null;
    private java.lang.String psnCode = null;
    private java.lang.String psnName = null;
    private String psnclassname = null;
    private String jobname = null;
    private String dutyname = null;
    private String remark = null;// zhanghua

    private Hashtable<String, PsndocWadocVO> values = new Hashtable<String, PsndocWadocVO>();
    private nc.vo.hi.wadoc.PsndocWadocVO[] subVOs = null;
    private java.lang.String pk_psndoc = null;

    /**
     * PsndocWadocMainVO 构造子注解。
     */
    public PsndocWadocMainVO() {
	super();
    }

    /**
     * 此处插入方法说明。 创建日期：(01-3-20 17:26:03)
     * 
     * @return java.lang.String[]
     */
    @Override
    public java.lang.String[] getAttributeNames() {
	return new String[] { "pk_psndoc", "deptcode", "deptname", "psncode", "psnname", "psnclassname", "jobname","dutyname","remark" };
    }

    /**
     * 此处插入方法说明。 创建日期：(01-3-20 17:24:29)
     * 
     * @param key
     *                java.lang.String
     */
    @Override
    public Object getAttributeValue(String attributeName) {
	int index = attributeName.indexOf(".");
	if (index > 0) {
	    String pkItem = attributeName.substring(0, index);
	    String itemname = attributeName.substring(index + 1);
	    PsndocWadocVO value = values.get(pkItem);
	    if (value == null) {
		return null;
	    }
	    if (itemname.equalsIgnoreCase("waprmlevel")) {
		return value.getWa_prmlv_levelname();
	    } else if (itemname.equalsIgnoreCase("waseclevel")) {
		return value.getWa_seclv_levelname();
	    } else if (itemname.equalsIgnoreCase("nmoney")) {
		return value.getNmoney();
	    } else if (itemname.equalsIgnoreCase("criterionvalue")) {
		return value.getCriterionvalue();
	    }
	    return null;
	} else {
	    if (attributeName.equals("psncode")) {
		return psnCode;
	    } else if (attributeName.equals("psnname")) {
		return psnName;
	    } else if (attributeName.equals("deptcode")) {
		return deptCode;
	    } else if (attributeName.equals("deptname")) {
		return deptName;
	    } else if (attributeName.equals("pk_psndoc")) {
		return pk_psndoc;
	    } else if (attributeName.equals("psnclassname")) {
		return psnclassname;
	    } else if (attributeName.equals("jobname")) {
		return jobname;
	    } else if(attributeName.equals("dutyname")){
		return dutyname;
	    }else if(attributeName.equals("remark")){
		return remark;
	    }
	}
	return null;
    }

    /**
     * 此处插入方法描述。 创建日期：(2004-6-5 13:37:48)
     * 
     * @return java.lang.String
     */
    public java.lang.String getDeptCode() {
	return deptCode;
    }

    /**
     * 此处插入方法描述。 创建日期：(2004-6-5 13:38:07)
     * 
     * @return java.lang.String
     */
    public java.lang.String getDeptName() {
	return deptName;
    }

    /**
     * 返回数值对象的显示名称。
     * 
     * 创建日期：(2001-2-15 14:18:08)
     * 
     * @return java.lang.String 返回数值对象的显示名称。
     */
    @Override
    public String getEntityName() {
	return null;
    }

    /**
     * 此处插入方法描述。 创建日期：(2004-6-9 20:06:05)
     * 
     * @return java.lang.String
     */
    public java.lang.String getPk_psndoc() {
	return pk_psndoc;
    }

    /**
     * 此处插入方法描述。 创建日期：(2004-6-5 13:38:45)
     * 
     * @return java.lang.String
     */
    public java.lang.String getPsnCode() {
	return psnCode;
    }

    /**
     * 此处插入方法描述。 创建日期：(2004-6-5 13:40:05)
     * 
     * @return java.lang.String
     */
    public java.lang.String getPsnName() {
	return psnName;
    }

    /**
     * 此处插入方法描述。 创建日期：(2004-6-5 14:37:31)
     * 
     * @return nc.vo.hi.wadoc.PsndocWadocVO[]
     */
    public nc.vo.hi.wadoc.PsndocWadocVO[] getSubVOs() {
	return subVOs;
    }

    /**
     * 此处插入方法描述。 创建日期：(2004-6-5 13:42:45)
     * 
     * @return java.util.Hashtable
     */
    public Hashtable<String, PsndocWadocVO> getValues() {
	return values;
    }

    /**
     * 此处插入方法说明。 创建日期：(01-3-20 17:24:29)
     * 
     * @param key
     *                java.lang.String
     */

    /**
     * 此处插入方法描述。 创建日期：(2004-6-5 13:37:48)
     * 
     * @param newDeptcode
     *                java.lang.String
     */
    public void setDeptCode(java.lang.String newDeptcode) {
	deptCode = newDeptcode;
    }

    /**
     * 此处插入方法描述。 创建日期：(2004-6-5 13:38:07)
     * 
     * @param newDeptname
     *                java.lang.String
     */
    public void setDeptName(java.lang.String newDeptname) {
	deptName = newDeptname;
    }

    /**
     * 此处插入方法描述。 创建日期：(2004-6-9 20:06:05)
     * 
     * @param newPk_psndoc
     *                java.lang.String
     */
    public void setPk_psndoc(java.lang.String newPk_psndoc) {
	pk_psndoc = newPk_psndoc;
    }

    /**
     * 此处插入方法描述。 创建日期：(2004-6-5 13:38:45)
     * 
     * @param newPsnCode
     *                java.lang.String
     */
    public void setPsnCode(java.lang.String newPsnCode) {
	psnCode = newPsnCode;
    }

    /**
     * 此处插入方法描述。 创建日期：(2004-6-5 13:40:05)
     * 
     * @param newPsnName
     *                java.lang.String
     */
    public void setPsnName(java.lang.String newPsnName) {
	psnName = newPsnName;
    }

    /**
     * 此处插入方法描述。 创建日期：(2004-6-5 14:37:31)
     * 
     * @param newSubVOs
     *                nc.vo.hi.wadoc.PsndocWadocVO[]
     */
    public void setSubVOs(nc.vo.hi.wadoc.PsndocWadocVO[] newSubVOs) {
	subVOs = newSubVOs;
    }

    /**
     * 此处插入方法描述。 创建日期：(2004-6-5 13:42:45)
     * 
     * @param newValues
     *                java.util.Hashtable
     */
    public void setValues(Hashtable<String, PsndocWadocVO> newValues) {
	values = newValues;
    }

    /**
     * 验证对象各属性之间的数据逻辑正确性。
     * 
     * 创建日期：(2001-2-15 11:47:35)
     * 
     * @exception nc.vo.pub.ValidationException
     *                    如果验证失败，抛出 ValidationException，对错误进行解释。
     */
    @Override
    public void validate() throws nc.vo.pub.ValidationException {
    }

    @Override
    public void setAttributeValue(String attributeName, Object value) {
	if (attributeName.equals("psncode")) {
	    psnCode = value.toString();
	} else if (attributeName.equals("psnname")) {
	    psnName = value.toString();
	} else if (attributeName.equals("deptcode")) {
	    deptCode = value.toString();
	} else if (attributeName.equals("deptname")) {
	    deptName = value.toString();
	} else if (attributeName.equals("pk_psndoc")) {
	    pk_psndoc = value.toString();
	} else if (attributeName.equals("psnclassname")) {
	    psnclassname = value.toString();
	} else if (attributeName.equals("jobname")) {
	    jobname = value == null ? "" : value.toString();
	} else if (attributeName.equals("dutyname")) {
	    dutyname = value == null ? "" : value.toString();
	}else if (attributeName.equals("remark")) {
	    remark = value == null ? "" : value.toString();
	}
    }

    /**
     * @return the psnclassname
     */
    public String getPsnclassname() {
	return psnclassname;
    }

    /**
     * @param psnclassname
     *                the psnclassname to set
     */
    public void setPsnclassname(String psnclassname) {
	this.psnclassname = psnclassname;
    }

    /**
     * @return the jobname
     */
    public String getJobname() {
	return jobname;
    }

    /**
     * @param jobname
     *                the jobname to set
     */
    public void setJobname(String jobname) {
	this.jobname = jobname;
    }

    /**
     * @return the dutyname
     */
    public String getDutyname() {
        return dutyname;
    }

    /**
     * @param dutyname the dutyname to set
     */
    public void setDutyname(String dutyname) {
        this.dutyname = dutyname;
    }

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}
    
    
}
