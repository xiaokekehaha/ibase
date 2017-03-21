package com.wzk.mvc.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class JDBCUtil {
	private static Logger log = LoggerFactory.getLogger(JDBCUtil.class);
//	private static ResourceBundle rb = ResourceBundle.getBundle("com.wzk.util.db-config");
	private static String url = "jdbc:mysql://localhost:9700/cqssc";
	private static String username = "wzk";
	private static String password = "wzk";
	
	/*private static String url=rb.getString("jdbc.url");
	private static String username=rb.getString("jdbc.username");
	private static String password=rb.getString("jdbc.password");*/
	
	/**
	 * 加载数据库驱动，获取数据库连接
	 * @return
	 */
	public static Connection getConnection(){
		Connection connection =null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			connection = DriverManager.getConnection(url, username, password);
		} catch (ClassNotFoundException e) {
			log.error("",e);
		} catch (SQLException e) {
			log.error("",e);
		}
		return connection;
	}
	
	public static PreparedStatement getPs(Connection connection, String sql) {
		PreparedStatement ps = null;
		try {
			ps = connection.prepareStatement(sql);
		} catch (SQLException e) {
			log.error("",e);
		}
		return ps;
	}
	
	public static ResultSet doQuery(PreparedStatement ps,String sql){
		ResultSet rs = null;
		try {
			rs = ps.executeQuery(sql);
		} catch (SQLException e) {
			log.error("",e);
		}
		return rs;
	}
	public static void closeConnection(Connection connection,Statement statement){
		try {
			if(null != statement){
				statement.close();
			}
			if(null != connection){
				connection.close();
			} 
		}catch (SQLException e) {
			log.error("",e);
		}
	}
	public static void closeConnection(Connection connection,Statement statement,ResultSet resultSet){
		try {
			if(null != resultSet){
				resultSet.close();
			} 
			if(null != statement){
				statement.close();
			}
			if(null != connection){
				connection.close();
			} 
		}catch (SQLException e) {
			log.error("",e);
		}
	}
	public static void main(String[] args) {
		/*String sql = "select * from my_percent where cycels=?";
		Connection connection = JDBCUtil.getConnection();
		try{
			connection.setAutoCommit(false);
			PreparedStatement ps = JDBCUtil.getPs(connection,sql);
			ps.setInt(0, 22);
			ResultSet rs = JDBCUtil.doQuery(ps,sql);
			connection.commit();
			while(rs.next()){
				System.out.println("============="+ rs.getString(1));
				System.out.println("============="+ rs.getString(2));
			}
			JDBCUtil.closeConnection(connection,ps,rs);
		}catch(Exception e){
			log.error("",e);
			try {
				connection.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}*/
		
	}
}
