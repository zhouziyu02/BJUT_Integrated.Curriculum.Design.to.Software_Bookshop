package cn.svtcc.edu.mybookshop.entity;

import java.io.Serializable;
/**
 * 订单明细表扩展类
 * @author 猿一只
 *
 */
public class OrderBookCustom extends OrderBook implements Serializable{
	/*扩展字段*/
	//下单时间
	private String orderDate;
	//书名
	private String title;
	
	//评论
	private String comment;
	
	
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}
	//状态
	private int state;
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public String getOrderDate() {
		return orderDate;
	}
	public void setOrderDate(String orderDate) {
		this.orderDate = orderDate;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
}
