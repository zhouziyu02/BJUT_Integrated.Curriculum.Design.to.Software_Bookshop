package cn.svtcc.edu.mybookshop.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.svtcc.edu.mybookshop.dao.Impl.BookDaoImpl;
import cn.svtcc.edu.mybookshop.entity.Books;
import cn.svtcc.edu.mybookshop.entity.PageBean;
import cn.svtcc.edu.mybookshop.entity.Users;
import cn.svtcc.edu.mybookshop.utils.StringUtil;
/**
 * Book操作servlet类
 *
 */
@WebServlet("/BookServlet")
public class BookServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public BookServlet() {
        super();
    }

	//doGet方法有两个参数，HttpServletRequest和HttpServletResponse，分别代表请求和响应对象。
	//当客户端通过浏览器地址栏，超链接，或者表单的method属性为get时，向服务器发送请求，服务器会调用doGet方法来处理这个请求。
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}


	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String opt = request.getParameter("opt");
		String role = request.getParameter("role");
		//判断是否为管理员操作
		if(role!=null && role.equals("3")){
			optByAdmin(request, response);
			return;
		}
		//操作判断
		if(opt.equals("byTitle")){
			queryByTitle(request, response);
		}else if(opt.equals("byIsbn")){
			queryByIsbn(request, response);
		}else if(opt.equals("byCategory")){
			queryByCategory(request, response);
		}else if(opt.equals("buyBook")){
			buyBook(request, response);
		}else if(opt.equals("search")){
			search(request, response);
		}else if(opt.equals("byPublish")){
			queryByPublish(request, response);
		}else if(opt.equals("byCategoryName")){
			queryByCategoryName(request, response);
		}
		
		else{
			response.sendRedirect("index.jsp");
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
		String opt = request.getParameter("opt");
		//判断是否登录
		Users user = (Users) request.getSession().getAttribute("user");
		if(user==null || user.getUserRoleId()!=3){
			response.sendRedirect("admin/login.jsp");
			return;
		}
		if(opt.equals("booklist")){
			bookListByAdmin(request, response);
		}
		else if(opt.equals("del")){
			delBookByAdmin(request, response);
		}
		else if(opt.equals("show")){
			showBookByAdmin(request, response);
		}
		else if(opt.equals("edit")){
			editBookByAdmin(request, response);
		}
		else if(opt.equals("add")){
			addBookByAdmin(request, response);
		}

	}
	/**
	 * 根据书名获取图书,并且跳转到图书显示页面
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	protected void queryByTitle(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String bname = request.getParameter("value");////获取请求参数value的值，即要查询的书籍标题
		BookDaoImpl dao = new BookDaoImpl();//创建一个BookDaoImpl对象，用于访问数据库
		List<Books> searchBList = new ArrayList<Books>();////创建一个List对象，用于存储查询结果
		searchBList = dao.getBooksByTitle(bname);////调用dao对象的getBooksByTitle方法，传入bname参数，返回一个包含匹配书籍的列表，并赋值给searchBList
		//封装成分页对象
		PageBean<Books> Pager = new PageBean<Books>(1,5,searchBList);//创建一个PageBean对象，用于封装分页信息，传入当前页码为1，每页显示数量为5，和searchBList列表
		request.getSession().setAttribute("searchBooks", Pager);////将Pager对象存储在请求会话中，属性名为searchBooks
		response.sendRedirect("search.jsp");////重定向到search.jsp页面，显示查询结果和分页导航
	}
	/**
	 * 根据出版社名称获取图书,并且跳转到图书显示页面
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	protected void queryByPublish(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String bname = request.getParameter("value");
		BookDaoImpl dao = new BookDaoImpl();
		List<Books> searchBList = new ArrayList<Books>();
		searchBList = dao.getBooksByPublishName(bname);
		//封装成分页对象
		PageBean<Books> Pager = new PageBean<Books>(1,5,searchBList);
		request.getSession().setAttribute("searchBooks", Pager);
		response.sendRedirect("search.jsp");
	}
	/**
	 * 根据分类名称获取图书,并且跳转到图书显示页面
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	protected void queryByCategoryName(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String bname = request.getParameter("value");
		BookDaoImpl dao = new BookDaoImpl();
		List<Books> searchBList = new ArrayList<Books>();
		searchBList = dao.getBooksByCategoryName(bname);
		//封装成分页对象
		PageBean<Books> Pager = new PageBean<Books>(1,5,searchBList);
		request.getSession().setAttribute("searchBooks", Pager);
		response.sendRedirect("search.jsp");
	}
	/**
	 * 根据图书Isbn获取图书,并且跳转到图书显示页面
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	protected void queryByIsbn(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String bname = request.getParameter("value");
		BookDaoImpl dao = new BookDaoImpl();
		List<Books> searchBList = new ArrayList<>();
		Books book = dao.getBooksByIsbn(bname);
		searchBList.add(book);
		//封装成分页对象
		PageBean<Books> Pager = new PageBean<Books>(1,5,searchBList);
		request.getSession().setAttribute("searchBooks", Pager);
		response.sendRedirect("search.jsp");
	}
	
	/**
	 * 根据图书Id获取图书,并且跳转到购买页面
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	protected void buyBook(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		///获取图书Id
		int id =Integer.parseInt(request.getParameter("bid"));//获取请求参数bid的值，即要购买的图书Id，并转换为整数类型
		BookDaoImpl dao = new BookDaoImpl();////创建一个BookDaoImpl对象，用于访问数据库
		Books book = dao.getBooksById(id);//调用dao对象的getBooksById方法，传入id参数，返回一个对应的Books对象，并赋值给book
		
		request.getSession().setAttribute("buyBook", book);//将book对象存储在请求会话中，属性名为buyBook
		response.sendRedirect("buy.jsp");////重定向到buy.jsp页面，显示购买信息和支付方式
	}

	/**
	 * 根据分类Id获取图书
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	protected void queryByCategory(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		///获取分类Id
		int cId =Integer.parseInt(request.getParameter("cid"));
		BookDaoImpl dao = new BookDaoImpl();
		List<Books> searchBList = dao.getBooksByCategoryId(cId);//调用dao对象的getBooksByCategoryId方法，传入cId参数，返回一个包含匹配图书的列表，并赋值给searchBList
		Integer page=1;//定义一个变量page，用于存储当前页码，默认为1
		//获取分页
		String strPage = request.getParameter("page");
		if(strPage==null || "".equals(strPage)){
		}else{
			page =Integer.parseInt(strPage);
		}
		//封装成分页对象
		PageBean<Books> Pager = new PageBean<Books>(page,5,searchBList);
		
		request.getSession().setAttribute("searchBooks", Pager);
		response.sendRedirect("search.jsp");
	}
	
	/**
	 * 查询
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	protected void search(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//
		@SuppressWarnings("unchecked")
		//从请求会话中获取searchBooks属性的值，即一个PageBean对象，并赋值给pageBean
		PageBean<Books> pageBean = (PageBean<Books>) request.getSession().getAttribute("searchBooks");
		if(pageBean!=null){
			List<Books> searchBList = pageBean.getSourceList();
			if(searchBList!=null){
				//获取分页
				//从pageBean对象中获取源列表，即查询结果列表，并赋值给searchBList
				Integer page =Integer.parseInt(request.getParameter("page"));
				if(page==null || page<1) page=1;
				//封装成分页对象
				PageBean<Books> Pager = new PageBean<Books>(page,5,searchBList);
				
				request.getSession().setAttribute("searchBooks", Pager);
				response.sendRedirect("search.jsp");
				return;
			}
		}
		response.sendRedirect("index.jsp");
	}
	
	/**
	 * 获取图书列表
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	private void bookListByAdmin(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		BookDaoImpl dao = new BookDaoImpl();
		List<Books> blist = dao.getBookList();
		//获取分页
		Integer page=1;
		String strPage = request.getParameter("page");
		if(StringUtil.isNumber(strPage)==false){
		}else{
			page =Integer.parseInt(strPage);
		}
		//封装成分页对象
		PageBean<Books> Pager = new PageBean<Books>(page,20,blist);
		request.getSession().setAttribute("blist", Pager);
		response.sendRedirect("admin/booklist.jsp");
	}
	/**
	 * 删除图书
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	private void delBookByAdmin(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//获取请求参数bid的值，即要删除的图书Id
		//判断bid参数是否是数字，如果不是，则重定向到admin/booklist.jsp页面，并返回
		if(StringUtil.isNumber(request.getParameter("bid"))==false){
			response.sendRedirect("admin/booklist.jsp");
			return;
		}
		//将bid参数转换为整数类型，并赋值给bookId
		int bookId = Integer.parseInt(request.getParameter("bid"));
		//创建一个BookDaoImpl对象，用于访问数据库
		BookDaoImpl dao = new BookDaoImpl();
		//调用dao对象的delBookBookId方法，传入bookId参数，返回一个布尔值，表示删除操作是否成功，并赋值给b
		boolean b = dao.delBookBookId(bookId);
		//存入操作信息
		if(b==true){
			request.getSession().setAttribute("ADMIN_MSG_BOOK","删除成功");
		}else{
			request.getSession().setAttribute("ADMIN_MSG_BOOK","删除失败");
		}
		bookListByAdmin(request, response);
	}
	/**
	 * 进入编辑图书页面
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	private void showBookByAdmin(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if(StringUtil.isNumber(request.getParameter("bid"))==false){
			response.sendRedirect("admin/booklist.jsp");
			return;
		}
		int bookId = Integer.parseInt(request.getParameter("bid"));
		BookDaoImpl dao = new BookDaoImpl();
		Books b = dao.getBooksById(bookId);
		if(b!=null){
			//存入需要编辑用户
			request.getSession().setAttribute("editBook",b);
			response.sendRedirect("admin/bookedit.jsp");
		}else{
			response.sendRedirect("admin/booklist.jsp");
		}
		
	}
	/**
	 * 编辑图书
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	private void editBookByAdmin(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if(StringUtil.isNumber(request.getParameter("bid"))==false){
			response.sendRedirect("admin/booklist.jsp");
			return;
		}
		int bookId = Integer.parseInt(request.getParameter("bid"));
		//提取参数
		String title = request.getParameter("title");
		Double unitPrice =  Double.parseDouble(request.getParameter("unitPrice"));
		String author =  request.getParameter("author");
		int categoryId =  Integer.parseInt(request.getParameter("categoryId"));
		int publisherId =  Integer.parseInt(request.getParameter("publisherId"));
		String publishDate = request.getParameter("publishDate");
		String isbn =  request.getParameter("ISBN");
		int wordsCount =Integer.parseInt( request.getParameter("wordsCount"));
		String contentDescription =  request.getParameter("contentDescription");
		String authorDescription =  request.getParameter("authorDescription");
		//封装对象
		Books book = new Books();
		book.setId(bookId);
		book.setTitle(title);
		book.setUnitPrice(unitPrice);
		book.setAuthor(author);
		book.setCategoryId(categoryId);
		book.setPublisherId(publisherId);
		book.setPublishDate(publishDate);
		book.setIsbn(isbn);
		book.setWordsCount(wordsCount);
		book.setContentDescription(contentDescription);
		book.setAuthorDescription(authorDescription);
		//调用dao层方法
		BookDaoImpl dao = new BookDaoImpl();
		Boolean b = dao.upBook(book);
		//设置修改信息结果字符串
		if(b)
			request.getSession().setAttribute("ADMIN_MSG_BOOK", "信息编辑成功,BID:"+bookId);
		else
			request.getSession().setAttribute("ADMIN_MSG_BOOK", "信息编辑失败,BID:"+bookId);
			
		showBookByAdmin(request, response);
	}
	/**
	 * 添加图书
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	protected void addBookByAdmin(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//获取提交的参数
		String title = request.getParameter("title");
		Double unitPrice =  Double.parseDouble(request.getParameter("unitPrice"));
		String author =  request.getParameter("author");
		int categoryId =  Integer.parseInt(request.getParameter("categoryId"));
		int publisherId =  Integer.parseInt(request.getParameter("publisherId"));
		String publishDate = request.getParameter("publishDate");
		String isbn =  request.getParameter("ISBN");
		int wordsCount =Integer.parseInt( request.getParameter("wordsCount"));
		String contentDescription =  request.getParameter("contentDescription");
		String authorDescription =  request.getParameter("authorDescription");
		
		//信息完整性校验
		if(title==null 
				|| title.equals("") 
				|| unitPrice==null 
				|| unitPrice<0){
			request.getSession().setAttribute("ADMIN_MSG_BOOK", "添加失败,请检查图书信息是否正确");
			response.sendRedirect("admin/useradd.jsp");
			return;
		}
		//封装对象
		Books book = new Books();
		book.setTitle(title);
		book.setUnitPrice(unitPrice);
		book.setAuthor(author);
		book.setCategoryId(categoryId);
		book.setPublisherId(publisherId);
		book.setPublishDate(publishDate);
		book.setIsbn(isbn);
		book.setWordsCount(wordsCount);
		book.setContentDescription(contentDescription);
		book.setAuthorDescription(authorDescription);
		//调用dao层方法
		BookDaoImpl dao = new BookDaoImpl();
		//页面跳转
		if(dao.addBook(book)){
			request.getSession().setAttribute("ADMIN_MSG_BOOK", "添加成功,图书:"+title);
			bookListByAdmin(request, response);
		}else{
			request.getSession().setAttribute("ADMIN_MSG_BOOK", "添加失败！");
			response.sendRedirect("admin/bookadd.jsp");
		}
		
	}
}
