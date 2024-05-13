package cn.svtcc.edu.mybookshop.entity;

import java.io.Serializable;

public class Users implements Serializable {
	private static final long serialVersionUID = -8636725672050581611L;
	private Integer id;//用户id
	private String LoginId;//用户名
	private String LoginPwd;//密码
	private String name;//姓名
	private String address;//地址
	private String phone;//电话
	private String mail;//邮箱
	private Integer userRoleId;//角色编号
	private Integer userStateId;//用户状态

	public Users() {
	}




	public Users(String loginId, String loginPwd, String name, String address, String phone, String mail,
				 Integer userRoleId, Integer userStateId) {
		super();
		LoginId = loginId;
		LoginPwd = loginPwd;
		this.name = name;
		this.address = address;
		this.phone = phone;
		this.mail = mail;
		this.userRoleId = userRoleId;
		this.userStateId = userStateId;
	}




	public String getLoginId() {
		return LoginId;
	}

	public void setLoginId(String loginId) {
		LoginId = loginId;
	}

	public String getLoginPwd() {
		return LoginPwd;
	}

	public void setLoginPwd(String loginPwd) {
		LoginPwd = loginPwd;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getUserRoleId() {
		return userRoleId;
	}

	public void setUserRoleId(Integer userRoleId) {
		this.userRoleId = userRoleId;
	}

	public Integer getUserStateId() {
		return userStateId;
	}

	public void setUserStateId(Integer userStateId) {
		this.userStateId = userStateId;
	}

	@Override
	public String toString() {
		return "Users [id=" + id + ", LoginId=" + LoginId + ", LoginPwd=" + LoginPwd + ", name=" + name + ", address="
				+ address + ", phone=" + phone + ", mail=" + mail + ", userRoleId=" + userRoleId + ", userStateId="
				+ userStateId + "]";
	}

}
