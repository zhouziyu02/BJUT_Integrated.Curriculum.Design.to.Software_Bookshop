package cn.svtcc.edu.mybookshop.dao.Impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import cn.svtcc.edu.mybookshop.dao.IUserDao;
import cn.svtcc.edu.mybookshop.db.DB;
import cn.svtcc.edu.mybookshop.entity.Users;

public class UserDaoImpl implements IUserDao {
	DB db;
	public UserDaoImpl() {
		// TODO Auto-generated constructor stub
		db = new DB();
	}

	//登录验证
	@Override
	public Users doLogin(String name, String pwd) {
		Connection con = db.getCon();
		String sql = "select * from users "
				+ "where LoginId=? and LoginPwd=?";
		PreparedStatement stmt = null;
		try {
			stmt = con.prepareStatement(sql);
			stmt.setString(1, name);//设置第一个参数为用户登录名
			stmt.setString(2, pwd);//设置第二个参数为用户密码
			ResultSet rs = stmt.executeQuery();
			if(rs.next()){
				Users user = getUserByResultSet(rs);
				return user;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 注册用户
	 */
	@Override
	public boolean addUser(Users u) {
		if(this.isLoginId(u.getLoginId())){
			return false;
		}
		Connection con = db.getCon();
		String sql;
		if(u.getAddress()!=null){
			sql = "insert into users(loginId,loginPwd,name,address,phone,mail,UserRoleId,UserStateId) value(?,?,?,?,?,?,1,1)";
		}else{
			sql = "insert into users(loginId,loginPwd,name,address,phone,mail,UserRoleId,UserStateId) value(?,?,?,?,?,?,?,?)";
		}
		PreparedStatement stmt = null;
		try {
			stmt = con.prepareStatement(sql);
			stmt.setString(1, u.getLoginId());//设置第一个参数为用户登录名
			stmt.setString(2, u.getLoginPwd());//设置第二个参数为用户密码
			stmt.setString(3, u.getName());//设置第三个参数为用户姓名
			stmt.setString(5, u.getPhone());//设置第五个参数为用户电话
			stmt.setString(6, u.getMail());//设置第六个参数为用户邮箱
			if(u.getAddress()!=null){//判断Users对象中的地址属性是否不为空
				stmt.setString(4, u.getAddress());//如果不为空，设置第四个参数为用户地址
			}else{
				stmt.setString(4, "空");//如果为空，设置第四个参数为"空"
				stmt.setInt(7, u.getUserRoleId());//设置第七个参数为用户角色编号
				stmt.setInt(8, u.getUserStateId());//设置第八个参数为用户状态编号
			}
			int rs = stmt.executeUpdate();
			if(rs>0){
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public boolean isLoginId(String loginId) {//用于判断用户登录名是否存在
		Connection con = db.getCon();
		String sql = "select * from users "
				+ "where LoginId=?";
		PreparedStatement stmt = null;
		try {
			stmt = con.prepareStatement(sql);
			stmt.setString(1, loginId);//设置第一个参数为用户登录名
			ResultSet rs = stmt.executeQuery();
			if(rs.next()){//如果结果集不为空，表示查询到用户
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 修改用户信息
	 */
	@Override
	public boolean updateUser(Users u) {
		Connection con = db.getCon();
		String sql;
		if(u.getLoginId()==null){
			sql = "UPDATE users SET `Name`=?,Mail=?,Phone=?,Address=? WHERE Id=?";
		}else{
			sql = "UPDATE users SET `Name`=?,Mail=?,Phone=?,Address=?,UserRoleId=?,UserStateId=?,LoginId=? WHERE Id=?";
		}
		PreparedStatement stmt = null;
		try {
			stmt = con.prepareStatement(sql);
			//设置参数值
			stmt.setString(1,u.getName());//设置第一个参数为用户姓名
			stmt.setString(2,u.getMail());//设置第二个参数为用户邮箱
			stmt.setString(3,u.getPhone());//设置第三个参数为用户电话
			stmt.setString(4,u.getAddress());//设置第四个参数为用户地址
			if(u.getLoginId()==null){//判断Users对象中的登录名属性是否为空
				stmt.setInt(5,u.getId());//如果为空，设置第五个参数为用户编号
			}else{
				stmt.setInt(5,u.getUserRoleId());//如果不为空设置第五个参数为用户角色编号
				stmt.setInt(6,u.getUserStateId());//设置第六个参数为用户状态编号
				stmt.setString(7,u.getLoginId());//设置第七个参数为用户登录名
				stmt.setInt(8,u.getId());//设置第八个参数为用户编号
			}

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
	 * 根据用户Id获取用户信息
	 */
	@Override
	public Users getUserById(int id) {
		Connection con = db.getCon();
		String sql = "select * from users "
				+ "where Id=?";
		PreparedStatement stmt = null;
		try {
			stmt = con.prepareStatement(sql);
			stmt.setInt(1, id);//设置第一个参数为用户编号
			ResultSet rs = stmt.executeQuery();
			if(rs.next()){
				Users user = getUserByResultSet(rs);
				return user;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 后台登录
	 */
	@Override
	public Users doLoginByAdmin(String name, String pwd) {
		Connection con = db.getCon();
		String sql = "select * from users "
				+ "where LoginId=? and LoginPwd=? and UserRoleId=3";
		PreparedStatement stmt = null;
		try {
			stmt = con.prepareStatement(sql);
			stmt.setString(1, name);//设置第一个参数为用户登录名
			stmt.setString(2, pwd);//设置第二个参数为用户密码
			ResultSet rs = stmt.executeQuery();
			if(rs.next()){
				Users user = getUserByResultSet(rs);
				return user;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * 封装结果集
	 * @param rs
	 * @return
	 * @throws SQLException
	 */
	private Users getUserByResultSet(ResultSet rs) throws SQLException{
		Users user = new Users();
		user.setId(rs.getInt(1));//传入列位置1，获取用户编号
		user.setLoginId(rs.getString(2));//传入列位置2，获取用户登录名
		user.setLoginPwd(rs.getString(3));//传入列位置3，获取用户密码
		user.setName(rs.getString(4));//传入列位置4，获取用户姓名
		user.setAddress(rs.getString(5));//传入列位置5，获取用户地址
		user.setPhone(rs.getString(6));//传入列位置6，获取用户电话
		user.setMail(rs.getString(7));//传入列位置7，获取用户邮箱
		user.setUserRoleId(rs.getInt(8));//传入列位置8,获取用户角色编号
		user.setUserStateId(rs.getInt(9));//传入列位置9,获取用户状态编号
		return user;
	}

	/**
	 * 获取用户列表
	 */
	@Override
	public List<Users> getUserList() {
		List<Users> ulist=new ArrayList<Users>();
		//得到连接
		Connection con=db.getCon();
		String sql="select * from users where UserRoleId!=3  order by Id desc";
		try {
			//创建sql语句
			PreparedStatement stmt=con.prepareStatement(sql);
			//ִ执行sql语句
			ResultSet rs=stmt.executeQuery();
			while (rs.next()) { //循环遍历结果集中的每一条记录
				Users u = getUserByResultSet(rs);//调用getUserByResultSet方法，传入结果集对象，返回一个Users对象，并赋值给u变量
				ulist.add(u);//将Users对象添加到ulist列表中
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return ulist;
	}

	/**
	 * 根据用户Id删除用户
	 */
	@Override
	public boolean delUserByUserId(int userId) {
		Connection con=db.getCon();
		String sql = "DELETE FROM users WHERE Id="+userId;
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
	 * 更新用户状态
	 */
	@Override
	public boolean upUserSate(int userId,int state) {
		Connection con = db.getCon();
		String sql = "UPDATE users SET UserStateId=? WHERE Id=?";
		PreparedStatement stmt = null;
		try {
			stmt = con.prepareStatement(sql);
			//设置参数值
			stmt.setInt(1,state);//设置第一个参数为用户状态编号
			stmt.setInt(2,userId);//设置第二个参数为用户编号
			int rs = stmt.executeUpdate();
			if(rs>0){
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
}
