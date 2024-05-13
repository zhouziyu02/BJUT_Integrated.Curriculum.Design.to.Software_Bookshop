package cn.svtcc.edu.mybookshop.controller;//声明包名，存放控制器类

import java.io.IOException;//导入异常类，用于处理输入输出异常
import java.util.List;//导入集合类，用于存储和操作一组对象

import javax.servlet.ServletException;//导入异常类，用于处理servlet异常
import javax.servlet.annotation.WebServlet;//导入注解类，用于标识一个servlet类
import javax.servlet.http.HttpServlet;//导入HttpServlet类，用于继承和实现一个servlet类
import javax.servlet.http.HttpServletRequest;//导入请求类，用于封装和获取客户端请求信息
import javax.servlet.http.HttpServletResponse;//导入响应类，用于封装和发送服务器响应信息

import cn.svtcc.edu.mybookshop.dao.Impl.UserDaoImpl;//导入用户数据访问对象实现类，用于实现用户数据访问对象接口
import cn.svtcc.edu.mybookshop.entity.PageBean;//导入分页实体类，用于封装分页相关信息
import cn.svtcc.edu.mybookshop.entity.Users;//导入用户实体类，用于封装用户实体信息
import cn.svtcc.edu.mybookshop.shoppingcart.ShoppingCart;//导入购物车类，用于封装购物车相关信息
import cn.svtcc.edu.mybookshop.utils.StringUtil;//导入字符串工具类，用于提供字符串相关的工具方法

@WebServlet("/UserServlet")
public class UserServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public UserServlet() {
		super();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);// 调用doPost方法，将GET请求转为POST请求
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String opt = request.getParameter("opt");// 获取客户端传递的opt参数，用于判断用户操作类型
		String role = request.getParameter("role");// 获取客户端传递的role参数，用于判断用户角色类型
		//判断是否为管理员操作
		if(role!=null && role.equals("3")){
			// 调用optByAdmin方法，处理管理员操作
			optByAdmin(request, response);
			return;
		}
		// 判断用户操作类型
		//登录
		if(opt.equals("login")){
			// 调用login方法，处理登录操作
			login(request, response);
			return;
		}
		//注册
		else if(opt.equals("register")){
			// 调用register方法，处理注册操作
			register(request, response);
			return;
		}
		// 如果是其他操作，先判断是否已经登录
		if(request.getSession().getAttribute("user")==null){
			// 如果没有登录，重定向到登录页面
			response.sendRedirect("login.jsp");
			return;
		}
		// 如果已经登录，判断用户操作类型
		//注销
		if(opt.equals("logout")){
			// 调用logout方法，处理注销操作
			logout(request, response);
		}
		//修改信息
		else if(opt.equals("updateUser")){
			// 调用updateUser方法，处理修改信息操作
			updateUser(request, response);
		}
		//进入个人中心
		else if(opt.equals("show")){
			// 调用show方法，处理进入个人中心操作
			show(request, response);
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
		//登录
		if(opt.equals("login")){
			// 调用loginByAdmin方法，处理管理员登录操作
			loginByAdmin(request, response);
			return;
		}
		// 如果是其他操作，先判断是否已经登录并且是管理员角色
		Users user = (Users) request.getSession().getAttribute("user");
		if(user==null || user.getUserRoleId()!=3){
			// 如果没有登录或者不是管理员角色，重定向到管理员登录页面
			response.sendRedirect("admin/login.jsp");
			return;
		}
		// 如果已经登录并且是管理员角色，判断用户操作类型
		//获取用户列表
		if(opt.equals("userlist")){
			// 调用userListByAdmin方法，处理获取用户列表操作
			userListByAdmin(request, response);
		}
		//退出
		else if(opt.equals("logout")){
			// 调用logoutByAdmin方法，处理退出操作
			logoutByAdmin(request, response);
		}
		//删除用户
		else if(opt.equals("del")){
			// 调用delUserByAdmin方法，处理删除用户操作
			delUserByAdmin(request, response);
		}
		//锁定用户
		else if(opt.equals("lock")){
			// 调用lockUserByAdmin方法，处理锁定用户操作
			lockUserByAdmin(request, response);
		}
		//解锁用户
		else if(opt.equals("unlock")){
			// 调用unLockUserByAdmin方法，处理解锁用户操作
			unLockUserByAdmin(request, response);
		}
		//获取用户信息
		else if(opt.equals("show")){
			// 调用showUserByAdmin方法，处理获取用户信息操作
			showUserByAdmin(request, response);
		}
		//编辑用户
		else if(opt.equals("edit")){
			// 调用showUserByAdmin方法，处理编辑用户操作
			editUserByAdmin(request, response);
		}
		//注册
		else if(opt.equals("add")){
			// 调用showUserByAdmin方法，处理注册操作
			addUserByAdmin(request, response);
			return;
		}
	}
	/**
	 * 用户登录
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	protected void login(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// 从session中获取购物车对象cart，如果为空，就创建一个新的购物车对象
		ShoppingCart cart = (ShoppingCart) request.getSession().getAttribute("cart");
		if(cart==null){
			cart = new ShoppingCart();
		}
		// 获取客户端传递的loginId和loginPwd参数，用于登录验证
		String name = request.getParameter("loginId");
		String pwd = request.getParameter("loginPwd");
		// 调用登录验证
		// 创建一个用户数据访问对象dao，调用其doLogin方法，传入name和pwd参数，返回一个用户对象user
		UserDaoImpl dao = new UserDaoImpl();
		Users user = dao.doLogin(name, pwd);
		if(user!=null){// 判断user对象是否为空
			// 如果不为空，说明登录成功，将user对象存入session中，便于前台使用
			request.getSession().setAttribute("user", user);
			// 重定向到首页
			response.sendRedirect("index.jsp");
			return;
		}else{
			// 如果为空，说明登录失败，重定向到登录页面
			response.sendRedirect("login.jsp");
			return;
		}

	}
	/**
	 * 管理员登录
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	protected void loginByAdmin(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// 从session中获取购物车对象cart，如果为空，就创建一个新的购物车对象
		ShoppingCart cart = (ShoppingCart) request.getSession().getAttribute("cart");
		if(cart==null){
			cart = new ShoppingCart();
		}
		// 获取客户端传递的loginId和loginPwd参数，用于登录验证
		String name = request.getParameter("loginId");
		String pwd = request.getParameter("loginPwd");
		// 创建一个用户数据访问对象dao，调用其doLoginByAdmin方法，传入name和pwd参数，返回一个用户对象user
		UserDaoImpl dao = new UserDaoImpl();
		Users user = dao.doLoginByAdmin(name, pwd);
		if(user!=null){ // 判断user对象是否为空
			// 如果不为空，说明登录成功，将user对象存入session中，便于前台使用
			request.getSession().setAttribute("user", user);
			// 重定向到管理员首页
			response.sendRedirect("admin/index.jsp");
			return;
		}else{
			// 如果为空，说明登录失败，重定向到管理员登录页面
			response.sendRedirect("admin/login.jsp");
			return;
		}

	}
	/**
	 * 用户注销退出
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	protected void logout(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//从session中获取用户信息
		Users user = (Users) request.getSession().getAttribute("user");
		if(user!=null){// 判断用户信息是否为空
			// 如果不为空，说明已经登录，从session中移除用户信息
			request.getSession().removeAttribute("user");
		}
		// 重定向到登录页面
		response.sendRedirect("login.jsp");
	}

	protected void show(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// 重定向到用户页面
		response.sendRedirect("user/user.jsp");
	}

	/**
	 * 管理员注销退出
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	protected void logoutByAdmin(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//从session中获取用户信息
		Users user = (Users) request.getSession().getAttribute("user");
		if(user!=null){// 判断用户信息是否为空
			// 如果不为空，说明已经登录，从session中移除用户信息
			request.getSession().removeAttribute("user");
		}
		// 重定向到管理员登录页面
		response.sendRedirect("admin/login.jsp");
	}
	/**
	 * 修改用户信息
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	protected void updateUser(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//获取提交的参数：id, name, mail, phone, address
		String id = (String) request.getParameter("id");
		// 判断id参数是否为空，如果为空，重定向到登录页面
		if(id==null || "".equals(id)){
			response.sendRedirect("login.jsp");
			return;
		}
		String name = (String) request.getParameter("name");
		String mail = (String) request.getParameter("mail");
		String phone = (String) request.getParameter("phone");
		String address = (String) request.getParameter("address");
		// 将参数封装成一个用户对象user
		Users user = new Users();
		user.setId(Integer.parseInt(id));
		user.setName(name);
		user.setMail(mail);
		user.setPhone(phone);
		user.setAddress(address);
		// 创建一个用户数据访问对象dao，调用其updateUser方法，传入user对象，返回一个布尔值b
		UserDaoImpl dao = new UserDaoImpl();
		Boolean b = dao.updateUser(user);
		// 调用initUser方法，重置session中的user信息
		initUser(request, response, Integer.parseInt(id));

		//设置修改信息结果字符串，存入session中
		if(b)
			request.getSession().setAttribute("MSG_USER_UPDATE_RESULT", "个人资料修改成功");
		else
			request.getSession().setAttribute("MSG_USER_UPDATE_RESULT", "个人资料修改失败");
		// 重定向到用户信息页面
		response.sendRedirect("user/userinfo.jsp");
	}

	/**
	 * 注册用户
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	protected void register(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//获取提交的参数
		String loginId = request.getParameter("loginId");//获取用户输入的用户名
		String loginPwd = request.getParameter("loginPwd");//获取用户输入的密码
		String name = request.getParameter("name");//获取用户输入的姓名
		String mail = request.getParameter("mail");//获取用户输入的邮箱
		String phone = request.getParameter("phone");//获取用户输入的电话
		String address = request.getParameter("address");//获取用户输入的地址
		//信息完整性校验
		if(loginId==null
				|| loginId.equals("")
				|| loginPwd==null
				|| loginPwd.equals("")
				|| mail==null
				|| mail.equals("")
				|| address==null
				|| address.equals("")){
			//如果有任何参数为空，设置会话属性为注册失败，并提示错误信息
			request.getSession().setAttribute("MSG_USER_REGISTER_RESULT", "注册失败:请检查注册信息是否正确");
			//重定向到注册页面
			response.sendRedirect("register.jsp");
			return;
		}

		//封装用户信息成Users对象
		//创建一个Users对象，用于存储用户信息，最后两个参数是用户状态和角色编号，这里默认为1
		Users user = new Users(loginId,loginPwd,name,address,phone,mail,1,1);
		//调用dao层方法
		//创建一个UserDaoImpl对象，用于访问数据库操作用户数据
		UserDaoImpl dao = new UserDaoImpl();
		//页面跳转
		//调用addUser方法，将用户信息插入到数据库中
		if(dao.addUser(user)){
			//重定向到注册成功页面
			response.sendRedirect("registerok.jsp");
		}else{
			//设置会话属性为注册失败，并提示错误信息
			request.getSession().setAttribute("MSG_USER_REGISTER_RESULT", "注册失败！");
			//重定向到注册页面
			response.sendRedirect("register.jsp");
		}

	}

	/**
	 * 初始化用户信息
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	private void initUser(HttpServletRequest request, HttpServletResponse response,int userId) throws ServletException, IOException {
		//创建一个UserDaoImpl对象，用于访问数据库操作用户数据
		UserDaoImpl dao = new UserDaoImpl();
		//调用getUserById方法，根据用户编号查询用户信息，返回一个Users对象
		Users user = dao.getUserById(userId);
		//将Users对象设置到会话属性中，方便其他页面获取用户信息
		request.getSession().setAttribute("user", user);
	}

	/**
	 * 获取用户列表
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	private void userListByAdmin(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//创建一个UserDaoImpl对象，用于访问数据库操作用户数据
		UserDaoImpl dao = new UserDaoImpl();
		//调用getUserList方法，查询所有用户信息，返回一个Users对象的列表
		List<Users> ulist = dao.getUserList();
		//获取分页
		Integer page=1;//设置默认的当前页数为1
		String strPage = request.getParameter("page");//获取请求参数中的页数
		if(StringUtil.isNumber(strPage)==false){//如果参数不是数字，不做处理
		}else{
			page =Integer.parseInt(strPage);//如果参数是数字，转换为整数类型，并赋值给page变量
		}
		//封装成分页对象
		//创建一个PageBean对象，用于封装分页信息，传入当前页数，每页显示的数量和用户列表
		PageBean<Users> Pager = new PageBean<Users>(page,10,ulist);
		//将PageBean对象设置到会话属性中，方便其他页面获取分页数据
		request.getSession().setAttribute("ulist", Pager);
		//重定向到用户列表页面
		response.sendRedirect("admin/userlist.jsp");
	}
	/**
	 * 删除用户
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	private void delUserByAdmin(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if(StringUtil.isNumber(request.getParameter("uid"))==false){//如果请求参数中的用户编号不是数字
			response.sendRedirect("admin/userlist.jsp");//重定向到用户列表页面
			return;
		}
		//如果请求参数中的用户编号是数字，转换为整数类型，并赋值给userId变量
		int userId = Integer.parseInt(request.getParameter("uid"));
		//创建一个UserDaoImpl对象，用于访问数据库操作用户数据
		UserDaoImpl dao = new UserDaoImpl();
		//调用delUserByUserId方法，根据用户编号删除用户信息，返回一个布尔值，表示操作是否成功
		boolean b = dao.delUserByUserId(userId);
		//存入操作信息
		if(b==true){
			//设置会话属性为删除成功，并提示信息
			request.getSession().setAttribute("ADMIN_MSG_USER","删除成功");
		}else{
			//设置会话属性为删除失败，并提示信息
			request.getSession().setAttribute("ADMIN_MSG_USER","删除失败");
		}
		//调用userListByAdmin方法，重新查询用户列表并显示
		userListByAdmin(request, response);
	}
	/**
	 * 锁定用户
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	private void lockUserByAdmin(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//如果请求参数中的用户编号不是数字
		if(StringUtil.isNumber(request.getParameter("uid"))==false){
			response.sendRedirect("admin/userlist.jsp");//重定向到用户列表页面
			return;
		}
		//如果请求参数中的用户编号是数字，转换为整数类型，并赋值给userId变量
		int userId = Integer.parseInt(request.getParameter("uid"));
		//创建一个UserDaoImpl对象，用于访问数据库操作用户数据
		UserDaoImpl dao = new UserDaoImpl();
		//调用upUserSate方法，根据用户编号更新用户状态为2，表示锁定
		dao.upUserSate(userId, 2);
		//调用userListByAdmin方法，重新查询用户列表并显示
		userListByAdmin(request, response);
	}
	/**
	 * 解锁用户
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	private void unLockUserByAdmin(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//如果请求参数中的用户编号不是数字
		if(StringUtil.isNumber(request.getParameter("uid"))==false){
			response.sendRedirect("admin/userlist.jsp");//重定向到用户列表页面
			return;
		}
		//如果请求参数中的用户编号是数字，转换为整数类型，并赋值给userId变量
		int userId = Integer.parseInt(request.getParameter("uid"));
		//创建一个UserDaoImpl对象，用于访问数据库操作用户数据
		UserDaoImpl dao = new UserDaoImpl();
		//调用upUserSate方法，根据用户编号更新用户状态为1，表示正常
		dao.upUserSate(userId, 1);
		//调用userListByAdmin方法，重新查询用户列表并显示
		userListByAdmin(request, response);
	}

	/**
	 * 进入编辑用户页面
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	private void showUserByAdmin(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//如果请求参数中的用户编号不是数字
		if(StringUtil.isNumber(request.getParameter("uid"))==false){
			response.sendRedirect("admin/userlist.jsp");//重定向到用户列表页面
			return;
		}
		//如果请求参数中的用户编号是数字，转换为整数类型，并赋值给userId变量
		int userId = Integer.parseInt(request.getParameter("uid"));
		//创建一个UserDaoImpl对象，用于访问数据库操作用户数据
		UserDaoImpl dao = new UserDaoImpl();
		//调用getUserById方法，根据用户编号查询用户信息，返回一个Users对象
		Users u = dao.getUserById(userId);
		if(u!=null){//如果查询到用户信息，返回非空对象
			//存入需要编辑用户
			//将Users对象设置到会话属性中，方便其他页面获取用户详情
			request.getSession().setAttribute("editUser",u);
			//重定向到用户编辑页面
			response.sendRedirect("admin/useredit.jsp");
		}else{//如果没有查询到用户信息
			//重定向到用户列表页面
			response.sendRedirect("admin/userlist.jsp");
		}

	}
	/**
	 * 编辑用户
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	private void editUserByAdmin(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//如果请求参数中的用户编号不是数字
		if(StringUtil.isNumber(request.getParameter("uid"))==false){
			response.sendRedirect("admin/userlist.jsp"); //重定向到用户列表页面
			return;
		}
		//如果请求参数中的用户编号是数字，转换为整数类型，并赋值给userId变量
		int userId = Integer.parseInt(request.getParameter("uid"));
		//提取参数
		String loginId = (String) request.getParameter("loginId");
		String name = (String) request.getParameter("name");
		String mail = (String) request.getParameter("mail");
		String phone = (String) request.getParameter("phone");
		String address = (String) request.getParameter("address");
		int userRoleId =  Integer.parseInt(request.getParameter("userRoleId"));
		int userStateId =  Integer.parseInt(request.getParameter("userStateId"));
		//封装成Users对象
		Users user = new Users();
		user.setId(userId);
		user.setLoginId(loginId);
		user.setName(name);
		user.setMail(mail);
		user.setPhone(phone);
		user.setAddress(address);
		user.setUserRoleId(userRoleId);
		user.setUserStateId(userStateId);
		//创建一个UserDaoImpl对象，用于访问数据库操作用户数据
		UserDaoImpl dao = new UserDaoImpl();
		//调用updateUser方法，根据Users对象更新用户信息，返回一个布尔值，表示操作是否成功
		Boolean b = dao.updateUser(user);
		//设置修改信息结果字符串
		if(b)
			//设置会话属性为信息编辑成功，并提示用户编号
			request.getSession().setAttribute("ADMIN_MSG_USER", "信息编辑成功,UID:"+userId);
		else
			//设置会话属性为信息编辑失败，并提示用户编号
			request.getSession().setAttribute("ADMIN_MSG_USER", "信息编辑失败,UID:"+userId);
		//调用userListByAdmin方法，重新查询用户列表并显示
		userListByAdmin(request, response);
	}
	/**
	 * 添加用户
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	protected void addUserByAdmin(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//获取提交的参数
		String loginId = request.getParameter("loginId");
		String loginPwd = request.getParameter("loginPwd");
		String name = request.getParameter("name");
		String mail = request.getParameter("mail");
		String phone = request.getParameter("phone");
		int userRoleId = Integer.parseInt(request.getParameter("userRoleId"));
		int userStateId = Integer.parseInt(request.getParameter("userStateId"));

		//信息完整性校验
		if(loginId==null
				|| loginId.equals("")
				|| loginPwd==null
				|| loginPwd.equals("")
				|| mail==null
				|| mail.equals("")){//如果有任何参数为空，不做处理
			//设置会话属性为添加失败，并提示错误信息
			request.getSession().setAttribute("ADMIN_MSG_USER", "添加失败,请检查注册信息是否正确");
			//重定向到用户添加页面
			response.sendRedirect("admin/useradd.jsp");
			return;
		}

		//封装用户信息成Users对象
		//创建一个Users对象，用于存储用户信息，地址参数为空
		Users user = new Users(loginId,loginPwd,name,null,phone,mail,userRoleId,userStateId);
		//创建一个UserDaoImpl对象，用于访问数据库操作用户数据
		UserDaoImpl dao = new UserDaoImpl();
		//页面跳转
		if(dao.addUser(user)){//创建一个UserDaoImpl对象，用于访问数据库操作用户数据
			//设置会话属性为添加成功，并提示登录名
			request.getSession().setAttribute("ADMIN_MSG_USER", "添加成功,用户名:"+loginId);
			//调用userListByAdmin方法，重新查询用户列表并显示
			userListByAdmin(request, response);
		}else{
			//设置会话属性为添加失败，并提示错误信息
			request.getSession().setAttribute("ADMIN_MSG_USER", "添加失败！");
			//重定向到用户添加页面
			response.sendRedirect("admin/useradd.jsp");
		}

	}
}
