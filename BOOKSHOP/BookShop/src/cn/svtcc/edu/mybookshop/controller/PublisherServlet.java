package cn.svtcc.edu.mybookshop.controller;//声明包名，存放控制器类

import java.io.IOException;//导入异常类，用于处理输入输出异常
import java.util.List;//导入集合类，用于存储和操作一组对象

import javax.servlet.ServletException;//导入异常类，用于处理servlet异常
import javax.servlet.annotation.WebServlet;//导入注解类，用于标识一个servlet类
import javax.servlet.http.HttpServlet;//导入HttpServlet类，用于继承和实现一个servlet类
import javax.servlet.http.HttpServletRequest;//导入请求类，用于封装和获取客户端请求信息
import javax.servlet.http.HttpServletResponse;//导入响应类，用于封装和发送服务器响应信息

import cn.svtcc.edu.mybookshop.dao.Impl.PublisherDaoImpl;//导入PublisherDaoImpl类，用于访问数据库操作分类数据
import cn.svtcc.edu.mybookshop.entity.Publishers;//导入Publishers类，用于封装分类信息
import cn.svtcc.edu.mybookshop.entity.Users;//导入Users类，用于封装用户信息
import cn.svtcc.edu.mybookshop.utils.StringUtil;//导入字符串工具类，用于提供字符串相关的工具方法


@WebServlet("/PublisherServlet")
public class PublisherServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public PublisherServlet() {
		super();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);// 调用doPost方法，将GET请求转为POST请求
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//String opt = request.getParameter("opt");// 获取客户端传递的opt参数，用于判断用户操作类型
		String role = request.getParameter("role");// 获取客户端传递的role参数，用于判断用户角色类型
		//判断是否为管理员操作
		if(role!=null && role.equals("3")){
			// 调用optByAdmin方法，处理管理员操作
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
		String opt = request.getParameter("opt");// 获取客户端传递的opt参数，用于判断用户操作类型
		//判断是否登录
		Users user = (Users) request.getSession().getAttribute("user");//从会话属性中获取用户对象
		//如果用户对象为空，或者用户角色编号不等于3，表示不是管理员
		if(user==null || user.getUserRoleId()!=3){
			response.sendRedirect("admin/login.jsp");//重定向到管理员登录页面
			return;
		}
		//添加出版社
		if(opt.equals("add")){//如果操作类型是添加
			//调用addPublisherByAdmin方法，实现添加出版社的功能
			addPublisherByAdmin(request, response);
		}
		//编辑出版社
		if(opt.equals("edit")){//如果操作类型是编辑
			//调用editPublisherByAdmin方法，实现编辑出版社的功能
			editPublisherByAdmin(request, response);
		}
	}

	/**
	 * 添加出版社
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	protected void addPublisherByAdmin(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//获取提交的参数
		String name = request.getParameter("name");//获取请求参数中的名称
		//信息完整性校验
		if(name==null || name.equals("")){
			//设置会话属性为添加失败，并提示错误信息
			request.getSession().setAttribute("ADMIN_MSG_PUBLISHER", "添加失败,请检查分类是否合法输入");
			//重定向到出版社列表页面
			response.sendRedirect("admin/publisherlist.jsp");
			return;
		}
		//封装对象
		//创建一个Publishers对象，用于封装出版社信息
		Publishers p = new Publishers();
		//设置出版社名称
		p.setName(name);
		//创建一个PublisherDaoImpl对象，用于访问数据库操作出版社数据
		PublisherDaoImpl dao = new PublisherDaoImpl();
		if(dao.addPublisher(p)){//调用addPublisher方法，将出版社信息插入到数据库中
			//设置会话属性为添加成功，并提示信息
			request.getSession().setAttribute("ADMIN_MSG_PUBLISHER", "添加成功");
			//初始化出版社集合
			initPublisher(request, response);
		}else{
			//设置会话属性为添加失败，并提示错误信息
			request.getSession().setAttribute("ADMIN_MSG_PUBLISHER", "添加失败");
		}
		//页面跳转
		//重定向到出版社列表页面
		response.sendRedirect("admin/publisherlist.jsp");
	}
	/**
	 * 编辑出版社
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	private void editPublisherByAdmin(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//如果请求参数中的编号不是数字
		if(StringUtil.isNumber(request.getParameter("pid"))==false){
			//重定向到分类列表页面
			response.sendRedirect("admin/publisherlist.jsp");
			return;
		}
		//提取参数
		//如果请求参数中的编号是数字，转换为整数类型，并赋值给cId变量
		int cId = Integer.parseInt(request.getParameter("pid"));
		String name = request.getParameter("name");//获取请求参数中的名称
		if(name==null || name.equals("")){//如果名称为空
			//重定向到出版社列表页面
			response.sendRedirect("admin/publisherlist.jsp");
			return;
		}
		//封装对象
		//创建一个Publishers对象，用于封装分类信息
		Publishers p = new Publishers();
		p.setId(cId);
		p.setName(name);
		//创建一个PublisherDaoImpl对象，用于访问数据库操作分类数据
		PublisherDaoImpl dao = new PublisherDaoImpl();
		//调用upPublisher方法，根据Publishers对象更新出版社信息，返回一个布尔值，表示操作是否成功
		Boolean b = dao.upPublisher(p);
		if(b){
			//调用initPublisher方法，重新查询列表并存入应用属性中
			initPublisher(request, response);
		}
		//重定向到出版社列表页面
		response.sendRedirect("admin/publisherlist.jsp");
	}
	/**
	 * 初始化出版社集合
	 * @param request
	 * @param response
	 */
	private void initPublisher(HttpServletRequest request, HttpServletResponse response){
		//创建一个PublisherDaoImpl对象，用于访问数据库操作出版社数据
		PublisherDaoImpl dao = new PublisherDaoImpl();
		//调用getPublisher方法，查询所有分类信息，返回一个Publishers对象的列表
		List<Publishers> plist = dao.getPublisher();
		//将Publishers对象的列表设置到应用属性中，方便其他页面获取出版社信息
		request.getServletContext().setAttribute("plist", plist);
	}
}
