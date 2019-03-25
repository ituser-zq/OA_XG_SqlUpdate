package OperProgram;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.junit.Test;

import util.JdbcUtil;

public class TestJdbc {
	Logger logger=Logger.getLogger(TestJdbc.class.getName());
	@Test
	public void querycj() throws Exception{
		String select_sql="select * from data_xscj";
		Connection conn=JdbcUtil.getConn();
		List<Map<String,Object>>list=JdbcUtil.queryAll(conn,select_sql);
		Object[] objects=list.toArray();//toArray():返回按适当顺序包含列表中的所有元素的数组（从第一个元素到最后一个元素）。
		for(Object obj:objects){
			System.out.println(obj);
		}
		logger.info("youngtse:查询数据成功");
		/*for(Map<String,Object>m:list){
			for(String k:m.keySet()){
				System.out.println(k+":"+m.get(k)+" ");
			}
		}*/
		//System.out.print(list.toArray()[0]);
	}
	@Test
	public void testupdate(){
		//插入操作
		/*String insert_sql="insert into data_xsxx(id,xh,xm) values(?,?,?)";
		Connection conn;
		try {
			conn=JdbcUtil.getConn();
			if(JdbcUtil.modifyDB(conn,insert_sql,"x010","123456","misaki")>0){
				System.out.println("更新数据成功");
			}else{
				System.out.println("更新数据失败");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		//更新操作
		String update_sql="update data_xsxx"
				+ " set mz=?"
				+ " where id=?";
		try {
			Connection conn=JdbcUtil.getConn();
			if(JdbcUtil.modifyDB(conn, update_sql,"白族","x010" )>0){
				logger.info("youngtse:更新数据成功");
				//System.out.println("更新数据成功");
			}else{
				logger.info("youngtse:更新数据失败");
				//System.out.println("更新数据失败");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		
	}

	//建立连接
	@Test
	public void test() {
		try {
			//加载驱动
			Class.forName("com.mysql.jdbc.Driver");
			//建立连接
			Connection conn=DriverManager.getConnection("jdbc:mysql://localhost:3306/xg?useSSL=false","root","922922");
			//发送sql语句块
			String select_sql="select * from data_xsxx";
			PreparedStatement ps=conn.prepareStatement(select_sql);
			//处理结果集
			ResultSet rs=ps.executeQuery();
			while(rs.next()){
				String id=rs.getString("id");
				String xh=rs.getString("xh");
				String xm=rs.getString("xm");
				System.out.println(id+","+xh+","+xm);
			}
		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
