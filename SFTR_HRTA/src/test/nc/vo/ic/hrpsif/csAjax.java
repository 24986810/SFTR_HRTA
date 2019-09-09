package nc.vo.ic.hrpsif;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.jdbc.framework.processor.BeanListProcessor;
import nc.jdbc.framework.processor.MapListProcessor;
import nc.vo.bd.invdoc.HisitemContrastVO;
import nc.vo.ps.settle.FeeDetailVO;
import nc.vo.pub.lang.UFDouble;


/**
 *   扫描条码后，查询信息所用的class文件
 * @author hel
 * 2016年03月22日
 */
public class csAjax {
	
	private BaseDAO dao =null;
	protected BaseDAO getDao(String datasource){
		if(dao==null){
			dao = new BaseDAO(datasource);
		}
			return dao;
		}
	@SuppressWarnings("restriction")
	public void init(HttpServletRequest request, HttpServletResponse response) throws IOException {
		BaseDAO getdao = new BaseDAO();

		response.setCharacterEncoding("UTF-8");
		response.setContentType("application/json; charset=utf-8");
		PrintWriter out = null;
		try {
			out = response.getWriter();
			out.append("AJAX测试!");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (out != null) {
				out.close();
			}
		}
	}
	@SuppressWarnings("restriction")
	public void mx(HttpServletRequest request, HttpServletResponse response) throws IOException {
		System.out.println("通过");
	}
	
}
