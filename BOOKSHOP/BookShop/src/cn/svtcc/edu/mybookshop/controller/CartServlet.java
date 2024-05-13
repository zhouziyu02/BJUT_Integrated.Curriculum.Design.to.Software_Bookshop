package cn.svtcc.edu.mybookshop.controller;

import java.io.IOException;
import java.util.Collection;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.svtcc.edu.mybookshop.dao.Impl.BookDaoImpl;
import cn.svtcc.edu.mybookshop.entity.Books;
import cn.svtcc.edu.mybookshop.entity.Users;
import cn.svtcc.edu.mybookshop.shoppingcart.ShoppingCart;
import cn.svtcc.edu.mybookshop.shoppingcart.ShoppingItem;
import cn.svtcc.edu.mybookshop.utils.StringUtil;
/**
 * ShopingCart操作实体类
 *
 */
@WebServlet("/CartServlet")
public class CartServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public CartServlet() {
        super();
    }
	//重写doGet方法，将请求转发给doPost方法
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String opt = request.getParameter("opt");//获取请求参数opt的值
		//判断操作
		if(opt.equals("buy")){//如果opt为buy，调用buyCart方法，将商品添加到购物车
			buyCart(request, response);
		}else if(opt.equals("del")){//如果opt为del，调用delCart方法，将商品从购物车中删除
			delCart(request, response);
		}else if(opt.equals("clear")){//如果opt为clear，调用clearCart方法，清空购物车
			clearCart(request, response);
		}else if(opt.equals("show")){//如果opt为show，调用showCart方法，显示购物车中的商品
			showCart(request, response);
		}else if(opt.equals("inAmount")){//如果opt为inAmount，调用inAmount方法，增加商品的数量
			inAmount(request, response);
		}else if(opt.equals("deAmount")){//如果opt为deAmount，调用deAmount方法，减少商品的数量
			deAmount(request, response);
		}else{
			response.sendRedirect("index.jsp");//如果opt为其他值，重定向到index.jsp页面
		}
		
	}

	/**
	 * 添加商品到购物车
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	protected void buyCart(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ShoppingCart cart = (ShoppingCart) request.getSession().getAttribute("cart");//获取购物车对象
		//如果购物车对象为空，创建一个新的购物车对象
		if(cart==null){
			cart = new ShoppingCart();
		}
		//获取购买数量
		String strNum = request.getParameter("num");
		if(!StringUtil.isNumber(strNum))
			strNum="1";
		
		int num = Integer.parseInt(strNum);
		//获取图书信息
		String isbn = request.getParameter("isbn");
		BookDaoImpl dao = new BookDaoImpl();//创建一个图书数据访问对象
		Books book = dao.getBooksByIsbn(isbn);//根据isbn查询图书信息
		//把商品添加到购物车
		cart.add(book, num);
		//将购物车对象存储到会话对象中
		request.getSession().setAttribute("cart",cart);
		//跳转页面
		response.sendRedirect("addcartok.jsp");
	}
	protected void clearCart(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ShoppingCart cart = (ShoppingCart) request.getSession().getAttribute("cart");
		if(cart!=null){
			//如果购物车对象不为空，调用其clear方法，清空购物车中的商品
			cart.clear();
			request.getSession().setAttribute("cart", cart);//将清空后的购物车对象存储到会话对象中
		}
		//页面跳转
		showCart(request, response);
	}
	
	/**
	 * 删除购物车中的商品
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	protected void delCart(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//从session中获取ShoppingCart
		ShoppingCart cart = (ShoppingCart) request.getSession().getAttribute("cart");
		//判空
		if(cart!=null){
			//获取要删除图书的isbn
			String isbn = request.getParameter("isbn");
			//删除这个图书
			if(isbn!=null && !isbn.equals("")){
				cart.remove(isbn);
				//商品种类减1
				cart.setItemAmount();
				
				//计算商品总价
				cart.getTotalReal();
				request.getSession().setAttribute("cart", cart);

			}
		}
		//调用showCart方法，显示购物车中的商品
		showCart(request, response);
	}
	
	/**
	 * 显示购物车
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	protected void showCart(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ShoppingCart cart = (ShoppingCart) request.getSession().getAttribute("cart");
		if(cart!=null){
			//获取购物车中的商品集合
			Collection<ShoppingItem> silist = cart.getItems();
			//将商品集合存储到会话对象中
			request.getSession().setAttribute("silist", silist);
			//计算商品总价
			cart.getTotalReal();
		}else{
			//如果购物车对象为空，创建一个新的购物车对象
			cart = new ShoppingCart();
		}
		//将购物车对象存储到会话对象中
		request.getSession().setAttribute("cart", cart);
		
		Users user = (Users) request.getSession().getAttribute("user");
		if(user!=null){
			//页面跳转,如果用户已登录，重定向到user/usercart.jsp页面，显示用户的购物车信息
			response.sendRedirect("user/usercart.jsp");
		}else{
			//如果用户未登录，重定向到showcart.jsp页面，显示游客的购物车信息
			response.sendRedirect("showcart.jsp");
		}
	}
	

	/**
	 * 增加购物车商品数量
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	protected void inAmount(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//从session中获取ShoppingCart
		ShoppingCart cart = (ShoppingCart) request.getSession().getAttribute("cart");
		if(cart!=null){
			String isbn =  request.getParameter("isbn");
			//信息完整性校验
			if(isbn!=null && !isbn.equals("")){
				//获取ShoppingItem
				ShoppingItem si =  cart.getItem(isbn);
				//判断商品对象是否为空
				if(si!=null){
					//调用商品对象的inAmount方法，增加商品数量
					si.inAmount();
				}
				//计算商品总价
				cart.getTotalReal();
				//将更新后的购物车对象存储到会话对象中
				request.getSession().setAttribute("cart", cart);
			}
		}
		
		//页面跳转
		showCart(request, response);
	}
	
	/**
	 * 减少购物车商品数量
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	protected void deAmount(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//从session中获取ShoppingCart
		ShoppingCart cart = (ShoppingCart) request.getSession().getAttribute("cart");
		if(cart!=null){
			String isbn = request.getParameter("isbn");
			//信息完整性校验
			if(isbn!=null && !isbn.equals("")){
				//获取ShoppingItem
				ShoppingItem si =  cart.getItem(isbn);
				if(si!=null && si.getAmount()>1)
					si.deAmount();
				//计算商品总价
				cart.getTotalReal();
				request.getSession().setAttribute("cart", cart);
			}
		}
		showCart(request, response);
	}
}
