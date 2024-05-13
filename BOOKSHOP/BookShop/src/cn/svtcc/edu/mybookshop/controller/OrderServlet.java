package cn.svtcc.edu.mybookshop.controller;//声明包名，存放控制器类

import java.io.IOException;//导入异常类，用于处理输入输出异常
import java.util.Collection;//导入集合类
import java.util.List;//导入集合类，用于存储和操作一组对象

import javax.servlet.ServletException;//导入异常类，用于处理servlet异常
import javax.servlet.annotation.WebServlet;//导入注解类，用于标识一个servlet类
import javax.servlet.http.HttpServlet;//导入HttpServlet类，用于继承和实现一个servlet类
import javax.servlet.http.HttpServletRequest;//导入请求类，用于封装和获取客户端请求信息
import javax.servlet.http.HttpServletResponse;//导入响应类，用于封装和发送服务器响应信息

import cn.svtcc.edu.mybookshop.dao.Impl.OrderDaoImpl;//导入OrderDaoImpl类，用于访问数据库操作订单数据
import cn.svtcc.edu.mybookshop.dao.Impl.UserDaoImpl;//导入UserDaoImpl类，用于访问数据库操作用户数据
import cn.svtcc.edu.mybookshop.entity.OrderBook;//导入OrderBook类，用于封装订单图书信息
import cn.svtcc.edu.mybookshop.entity.OrderBookCustom;//导入OrderBookCustom类，用于封装订单图书的自定义信息，如价格，数量等
import cn.svtcc.edu.mybookshop.entity.Orders;//导入Orders类，用于封装订单信息
import cn.svtcc.edu.mybookshop.entity.PageBean;//导入PageBean类，用于封装分页信息
import cn.svtcc.edu.mybookshop.entity.Users;//导入Users类，用于封装用户信息
import cn.svtcc.edu.mybookshop.shoppingcart.ShoppingCart;//导入ShoppingCart类，用于实现购物车的功能
import cn.svtcc.edu.mybookshop.shoppingcart.ShoppingItem;//导入ShoppingItem类，用于实现购物项的功能
import cn.svtcc.edu.mybookshop.utils.StringUtil;//导入字符串工具类，用于提供字符串相关的工具方法

@WebServlet("/OrderServlet")
public class OrderServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	public OrderServlet() {
		super();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);// 调用doPost方法，将GET请求转为POST请求
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//判断操作
		//获取请求参数中的opt参数，用于判断用户操作类型，可能是订单列表，删除订单，发货订单，添加订单或查看订单
		String opt = request.getParameter("opt");
		if(opt.equals("orderlist")){//如果操作类型是订单列表
			//判断是否登录
			//从会话属性中获取用户对象
			Users user = (Users) request.getSession().getAttribute("user");
			if(user==null || user.getUserRoleId()!=3){//如果用户对象为空，或者用户角色编号不等于3，表示不是管理员
				//重定向到管理员登录页面
				response.sendRedirect("admin/login.jsp");
				return;
			}
			//调用orderListByAdmin方法，实现查询订单列表的功能
			orderListByAdmin(request, response);
		}

		//删除订单
		if(opt.equals("del")){
			delOrderByAdmin(request, response);
		}
		//发货
		if(opt.equals("fahuo")){
			comeOrderByUserId(request, response);
		}
		//判断是否登录
		if(request.getSession().getAttribute("user")==null){
			//跳转页面
			response.sendRedirect("login.jsp");
			return;
		}
		if(opt.equals("add")){
			addOrder(request, response);
		}

		if(opt.equals("show")){
			showOrder(request, response);
		}

	}

	/**
	 * 删除订单（管理员）
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	private void delOrderByAdmin(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//如果请求参数中的用户编号不是数字
		if(StringUtil.isNumber(request.getParameter("uid"))==false){
			//重定向到用户列表页面
			response.sendRedirect("admin/userlist.jsp");
			return;
		}
		//创建一个OrderDaoImpl对象，用于访问数据库操作订单数据
		OrderDaoImpl dao = new OrderDaoImpl();
		//如果请求参数中的用户编号是数字，转换为整数类型，并赋值给userId变量
		int userId = Integer.parseInt(request.getParameter("uid"));
		//调用delOrderByUserId方法，根据用户编号删除订单信息，返回一个布尔值，表示操作是否成功
		boolean b = dao.delOrderByUserId(userId);
		//存入操作信息
		if(b==true){
			//设置会话属性为删除成功，并提示信息
			request.getSession().setAttribute("ADMIN_MSG_USER","删除成功");
		}else{
			//设置会话属性为删除失败，并提示错误信息
			request.getSession().setAttribute("ADMIN_MSG_USER","删除失败");
		}
		//调用orderListByAdmin方法，重新查询订单列表并显示
		orderListByAdmin(request, response);
	}
	/**
	 * 发货
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	private void comeOrderByUserId(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//如果请求参数中的用户编号不是数字
		if(StringUtil.isNumber(request.getParameter("uid"))==false){
			//重定向到用户列表页面
			response.sendRedirect("admin/userlist.jsp");
			return;
		}
		//创建一个OrderDaoImpl对象，用于访问数据库操作订单数据
		OrderDaoImpl dao = new OrderDaoImpl();
		//如果请求参数中的用户编号是数字，转换为整数类型，并赋值给userId变量
		int userId = Integer.parseInt(request.getParameter("uid"));
		//调用comeOrderByUserId方法，根据用户编号更新订单状态为已发货，返回一个布尔值，表示操作是否成功
		boolean b = dao.comeOrderByUserId(userId);
		//存入操作信息
		if(b==true){
			//设置会话属性为发货成功，并提示信息
			request.getSession().setAttribute("ADMIN_MSG_USER","发货成功");
		}else{
			//设置会话属性为发货失败，并提示错误信息
			request.getSession().setAttribute("ADMIN_MSG_USER","发货失败");
		}
		//调用orderListByAdmin方法，重新查询订单列表并显示
		orderListByAdmin(request, response);
	}
	/**
	 * 添加订单
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	protected void addOrder(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//从session中的cart(购物车)对象
		ShoppingCart cart = (ShoppingCart) request.getSession().getAttribute("cart");
		//获取从jsp页面传过来的userId
		Users user = (Users) request.getSession().getAttribute("user");
		//将获取的数据封装成Orders对象
		Orders order = new Orders();
		order.setUserId(user.getId());
		order.setTotalPrice(cart.getTotal());
		//调用dao的添加订单方法
		OrderDaoImpl dao = new OrderDaoImpl();
		int orderId = dao.addOrder(order);
		//如果返回的订单Id不为-1,则表示添加订单成功
		if(orderId!=-1){
			//获取购物车中的所有图书种类
			Collection<ShoppingItem> silist = cart.getItems();
			//遍历每一项图书
			for(ShoppingItem item:silist){
				//封装成OrderBook对象
				OrderBook ob = new OrderBook();
				ob.setOrderId(orderId);
				ob.setBookId(item.getItem().getId());
				ob.setQuantity(item.getAmount());
				ob.setUnitPrice(item.getItem().getUnitPrice());
				//添加到明细表
				dao.addOrderBook(ob);
			}
			//清空购物车
			request.getSession().removeAttribute("cart");

			//页面跳转
			response.sendRedirect("payok.jsp");
		}else{
			//页面跳转
			response.sendRedirect("user/usercart.jsp");
		}

	}

	/**
	 * 查看订单
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	protected void showOrder(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//获取session中的user对象
		Users user = (Users) request.getSession().getAttribute("user");
		//调用dao层的获取订单方法
		OrderDaoImpl dao = new OrderDaoImpl();
		List<OrderBookCustom> olist = dao.getOrder(user.getId());
		//添加到session中
		request.getSession().setAttribute("olist",olist);
		//页面跳转
		response.sendRedirect("user/userorder.jsp");
	}
	/**
	 * 获取订单列表
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	private void orderListByAdmin(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//调用dao层的获取订单方法
		//创建一个OrderDaoImpl对象，用于访问数据库操作订单数据
		OrderDaoImpl dao = new OrderDaoImpl();
		//调用getOrder方法，查询所有订单信息，返回一个OrderBookCustom对象的列表，用于封装订单图书的自定义信息
		List<OrderBookCustom> olist = dao.getOrder();		//获取分页
		Integer page=1;
		//获取请求参数中的页数
		String strPage = request.getParameter("page");
		if(StringUtil.isNumber(strPage)==false){
		}else{
			page =Integer.parseInt(strPage);
		}
		//封装成分页对象
		//创建一个PageBean对象，用于封装分页信息，传入当前页数，每页显示的数量和订单列表
		PageBean<OrderBookCustom> Pager = new PageBean<OrderBookCustom>(page,10,olist);
		//将PageBean对象设置到会话属性中，方便其他页面获取分页数据
		request.getSession().setAttribute("ulist", Pager);
		//重定向到订单列表页面
		response.sendRedirect("admin/orderlist.jsp");
	}

}
