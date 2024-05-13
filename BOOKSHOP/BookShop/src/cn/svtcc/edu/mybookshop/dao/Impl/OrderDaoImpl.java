package cn.svtcc.edu.mybookshop.dao.Impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import cn.svtcc.edu.mybookshop.dao.IOrderDao;
import cn.svtcc.edu.mybookshop.db.DB;
import cn.svtcc.edu.mybookshop.entity.OrderBook;
import cn.svtcc.edu.mybookshop.entity.OrderBookCustom;
import cn.svtcc.edu.mybookshop.entity.Orders;

//实现订单数据访问接口
public class OrderDaoImpl implements IOrderDao {
	DB db;//声明一个DB对象，用于获取数据库连接
	public OrderDaoImpl() {
		db = new DB();
	}

	/**
	 * 添加订单
	 * @param order
	 * @return 订单Id
	 */
	@Override
	public int addOrder(Orders orders) {
		//调用DB对象的getCon方法，获取数据库连接对象，并赋值给con变量
		Connection con = db.getCon();
		//创建sql语句(CURRENT_TIMESTAMP()为获取当前时间)
		String sql = "insert into orders(orderDate,userId,totalPrice)"
				+ " value(CURRENT_TIMESTAMP(),?,?)";
		//预编译语句对象，用于执行sql语句，赋值为null
		PreparedStatement stmt = null;
		try {
			//封装sql语句
			stmt = con.prepareStatement(sql);//调用连接对象的prepareStatement方法，传入sql语句，返回一个预编译语句对象，并赋值给stmt变量
			//设置参数
			stmt.setInt(1, orders.getUserId());//调用预编译语句对象的setInt方法，传入参数位置和值，设置第一个参数为用户编号，从Orders对象中获取
			stmt.setDouble(2, orders.getTotalPrice());//调用预编译语句对象的setDouble方法，传入参数位置和值，设置第二个参数为订单总价，从Orders对象中获取
			//执行sql语句(返回影响行数)
			int row = stmt.executeUpdate();//调用预编译语句对象的executeUpdate方法，执行插入操作，返回一个整数值，表示影响的行数，并赋值给row变量
			if(row>0){
				//如果影响行数大于0,说明插入成功,执行下面的sql语句,获取插入的自增长的Id

				//调用预编译语句对象的executeQuery方法，传入查询自增长编号的sql语句，返回一个结果集对象，并赋值给rs变量
				ResultSet rs = stmt.executeQuery("select LAST_INSERT_ID()");
				if (rs.next())
					//返回自增长的Id
					return rs.getInt(1);//调用结果集对象的getInt方法，传入列位置1，获取自增长编号
			}
		} catch (SQLException e) {//捕获可能发生的SQLException异常
			e.printStackTrace();//打印异常堆栈信息
		}
		//返回-1表示插入失败
		return -1;
	}
	/**
	 * 根据用户Id删除订单
	 */
	@Override
	public boolean delOrderByUserId(int userId) {
		Connection con=db.getCon();//调用DB对象的getCon方法，获取数据库连接对象，并赋值给con变量
		String sql = "DELETE FROM orders WHERE Id="+userId;
		PreparedStatement stmt = null;//预编译语句对象，用于执行sql语句，赋值为null
		try {
			stmt = con.prepareStatement(sql);//调用连接对象的prepareStatement方法，传入sql语句，返回一个预编译语句对象，并赋值给stmt变量
			int rs = stmt.executeUpdate();//调用预编译语句对象的executeUpdate方法，执行删除操作，返回一个整数值，表示影响的行数，并赋值给rs变量
			if(rs>0){
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false; //如果没有成功删除或发生异常，则返回false作为返回值，表示删除失败
	}

	/**
	 * 发货
	 */
	@Override
	public boolean comeOrderByUserId(int userId) {
		Connection con=db.getCon();
		String sql = "update orders set state ='1' WHERE Id="+userId;
		PreparedStatement stmt = null;
		try {
			stmt = con.prepareStatement(sql);
			int rs = stmt.executeUpdate();
			if(rs>0){
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	/**
	 * 添加详细订单
	 * @param orderBook
	 * @return
	 */
	@Override
	public boolean addOrderBook(OrderBook orderBook) {
		//得到连接
		Connection con = db.getCon();
		//创建sql语句
		String sql = "insert into orderBook(orderId,bookId,quantity,unitPrice)"
				+ " value(?,?,?,?)";
		PreparedStatement stmt = null;
		try {
			//封装sql语句
			stmt = con.prepareStatement(sql);
			//设置参数
			stmt.setInt(1, orderBook.getOrderId());//设置第一个参数为订单编号，从OrderBook对象中获取
			stmt.setInt(2, orderBook.getBookId());//设置第二个参数为图书编号，从OrderBook对象中获取
			stmt.setInt(3, orderBook.getQuantity());//设置第三个参数为图书数量，从OrderBook对象中获取
			stmt.setDouble(4, orderBook.getUnitPrice());//设置第四个参数为图书价格，从OrderBook对象中获取
			//执行sql语句
			int rs = stmt.executeUpdate();
			if(rs>0){
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 根据当前登录用户Id获取对应的订单
	 * @param userId
	 * @return
	 */
	@Override
	public List<OrderBookCustom> getOrder(int userId) {
		//创建一个OrderBookCustom对象的列表，并赋值给olist变量，用于存储查询结果
		List<OrderBookCustom> olist=new ArrayList<OrderBookCustom>();
		//得到连接
		Connection con=db.getCon();
		//sql语句
		String sql="SELECT orderbook.*,orders.OrderDate,books.Title,orders.comment,orders.state FROM orders,orderbook,books WHERE orderbook.OrderID=orders.Id AND orderbook.BookID=books.Id AND orders.UserId="+userId +" ORDER BY Id DESC";
		try {
			//封装sql语句
			PreparedStatement stmt=con.prepareStatement(sql);
			//ִ执行sql
			ResultSet rs=stmt.executeQuery();
			while (rs.next()) {
				//提取查询的记录进行封装,并添加到集合中
				OrderBookCustom obc = new OrderBookCustom();
				obc.setOrderId(rs.getInt(1));//传入列位置1，获取订单编号，并设置到OrderBookCustom对象中
				obc.setId(rs.getInt(2));//传入列位置2，获取订单图书编号
				obc.setBookId(rs.getInt(3));//传入列位置3，获取图书编号
				obc.setQuantity(rs.getInt(4));//传入列位置4，获取图书数量
				obc.setUnitPrice(rs.getDouble(5));//传入列位置5，获取图书单价
				obc.setOrderDate(rs.getString(6));//传入列位置6，获取订单日期
				obc.setTitle(rs.getString(7));//传入列位置7，获取图书标题
				obc.setComment(rs.getString(8));//传入列位置8，获取订单评论
				obc.setState(rs.getInt(9));//传入列位置9，获取订单状态
				olist.add(obc);//将OrderBookCustom对象添加到olist列表中
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return olist;
	}


	/**
	 * 根据当前登录用户Id获取对应的订单
	 * @param userId
	 * @return
	 */
	@Override
	public List<OrderBookCustom> getOrder() {
		List<OrderBookCustom> olist=new ArrayList<OrderBookCustom>();
		//得到连接
		Connection con=db.getCon();
		//sql语句
		String sql="SELECT orderbook.*,orders.OrderDate,books.Title ,orders.state FROM orders,orderbook,books WHERE orderbook.OrderID=orders.Id AND orderbook.BookID=books.Id  ORDER BY Id DESC";
		try {
			//封装sql语句
			PreparedStatement stmt=con.prepareStatement(sql);
			//ִ执行sql
			ResultSet rs=stmt.executeQuery();
			while (rs.next()) {
				//提取查询的记录进行封装,并添加到集合中
				OrderBookCustom obc = new OrderBookCustom();
				obc.setOrderId(rs.getInt(1));//传入列位置1，获取订单编号
				obc.setId(rs.getInt(2));//传入列位置2，获取订单图书编号
				obc.setBookId(rs.getInt(3));//传入列位置3，获取图书编号
				obc.setQuantity(rs.getInt(4));//传入列位置4，获取图书数量
				obc.setUnitPrice(rs.getDouble(5));//传入列位置5，获取图书价格
				obc.setOrderDate(rs.getString(6));//传入列位置6，获取订单日期
				obc.setTitle(rs.getString(7));//传入列位置7，获取图书标题
				obc.setState(rs.getInt(8));//传入列位置8，获取订单状态

				olist.add(obc);//将OrderBookCustom对象添加到olist列表中
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return olist;
	}

}
