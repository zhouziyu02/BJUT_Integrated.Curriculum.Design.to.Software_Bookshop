package cn.svtcc.edu.mybookshop.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.svtcc.edu.mybookshop.dao.Impl.BookDaoImpl;
import cn.svtcc.edu.mybookshop.dao.Impl.CategoriesDaoImpl;
import cn.svtcc.edu.mybookshop.entity.Books;
import cn.svtcc.edu.mybookshop.entity.Categories;
import cn.svtcc.edu.mybookshop.entity.Users;
import cn.svtcc.edu.mybookshop.utils.StringUtil;

/**
 * Category操作servlet类
 *
 */
@WebServlet("/CategoryServlet")
public class CategoryServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public CategoryServlet() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {	
		//String opt = request.getParameter("opt");
		String role = request.getParameter("role");
		//判断是否为管理员操作
		if(role!=null && role.equals("3")){
			optByAdmin(request, response);
			return;
		}
		

	}
	/**
	 * 管理员操作
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	protected void optByAdmin(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String opt = request.getParameter("opt");//获取请求参数opt的值
		//判断是否登录
		Users user = (Users) request.getSession().getAttribute("user");//如果用户对象为空或者用户角色不是3（管理员）
		if(user==null || user.getUserRoleId()!=3){
			response.sendRedirect("admin/login.jsp");//重定向到管理员登录页面
			return;
		}
		//添加分类
		if(opt.equals("add")){
			addCategoryByAdmin(request, response);
		}
		//编辑分类
		if(opt.equals("edit")){
			editCategoryByAdmin(request, response);
		}
	}
	
	/**
	 * 添加分类
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	protected void addCategoryByAdmin(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//获取提交的参数
		String name = request.getParameter("name");
		//信息完整性校验
		if(name==null || name.equals("")){//如果name的值为空或者空字符串
			request.getSession().setAttribute("ADMIN_MSG_CATEGORY", "添加失败,请检查分类是否合法输入");
			response.sendRedirect("admin/categorylist.jsp"); //重定向到分类列表页面
			return;
		}
		//封装对象
		Categories cate = new Categories();//创建一个Categories对象
		cate.setName(name);
		//调用dao层方法
		CategoriesDaoImpl dao = new CategoriesDaoImpl();//创建一个CategoriesDaoImpl对象，用于操作数据库
		if(dao.addCategory(cate)){
			request.getSession().setAttribute("ADMIN_MSG_CATEGORY", "添加成功");
			//初始化分类集合
			initCategory(request, response);//调用initCategory方法，用于更新分类集合
		}else{
			request.getSession().setAttribute("ADMIN_MSG_CATEGORY", "添加失败");
		}
		//页面跳转
		response.sendRedirect("admin/categorylist.jsp");
	}
	/**
	 * 编辑分类
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	private void editCategoryByAdmin(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//如果请求参数cid的值不是一个数字
		if(StringUtil.isNumber(request.getParameter("cid"))==false){
			response.sendRedirect("admin/categorylist.jsp");
			return;
		}
		//提取参数
		int cId = Integer.parseInt(request.getParameter("cid"));
		String name = request.getParameter("name");//获取请求参数name的值
		if(name==null || name.equals("")){//如果name的值为空或者空字符串
			response.sendRedirect("admin/categorylist.jsp");
			return;
		}
		
		//封装对象
		Categories c = new Categories();
		c.setId(cId);//设置对象的id属性为请求参数cid的值
		c.setName(name); //设置对象的name属性为请求参数name的值
		//调用dao层方法
		CategoriesDaoImpl dao = new CategoriesDaoImpl();
		Boolean b = dao.updateCategory(c);//调用dao对象的updateCategory方法，用于更新数据库中的分类信息
		if(b){//如果更新成功
			initCategory(request, response);//调用initCategory方法，用于更新分类集合
		}
		response.sendRedirect("admin/categorylist.jsp");
	}
	/**
	 * 初始化分类集合
	 * @param request
	 * @param response
	 */
	private void initCategory(HttpServletRequest request, HttpServletResponse response){
		//获取图书分类
		CategoriesDaoImpl dao = new CategoriesDaoImpl();//创建一个CategoriesDaoImpl对象，用于操作数据库
		List<Categories> clist = dao.getCategories();//调用dao对象的getCategories方法，用于获取所有的分类信息
		request.getServletContext().setAttribute("clist", clist);//在应用上下文中设置一个属性，表示分类信息的集合
	}
}
