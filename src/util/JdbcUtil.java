package util;
import java.sql.Connection;//是一个关于连接数据库的接口。与特定数据库的连接（会话）。在连接上下文中执行 SQL 语句并返回结果。 
import java.sql.PreparedStatement;//一个表示预编译的 SQL 语句的对象的接口，将数据库对应表中的sql语句给“导”出来。
import java.sql.ResultSet;//一个表示数据库结果集的数据表，通常通过执行查询数据库的语句生成的接口。
import java.sql.ResultSetMetaData;//一个可用于获取关于 ResultSet 对象中列的类型和属性信息的对象的接口。
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;//有序的 collection（也称为序列）。此接口的用户可以对列表中每个元素的插入位置进行精确地控制。用户可以根据元素的整数索引（在列表中的位置）访问元素，并搜索列表中的元素。
import java.util.Map;//将键映射到值的对象。一个映射不能包含重复的键；每个键最多只能映射到一个值的接口。 
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSourceFactory;
import org.apache.log4j.Logger;


public class JdbcUtil {
	static Logger logger1=Logger.getLogger(JdbcUtil.class.getName());

	static DataSource ds=null;
	/**
	 * 静态代码块
	 */
	static{
		Properties pro=new Properties();
		try {
			pro.load(JdbcUtil.class.getClassLoader()
					.getResourceAsStream("xg_dbcp.properties"));
			ds=BasicDataSourceFactory.createDataSource(pro);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static Connection getConn() throws Exception {
		/*Connection conn=null;
		Class.forName("com.mysql.jdbc.Driver");
		//建立连接
		conn=DriverManager.getConnection("jdbc:mysql://localhost:3306/xg?useSSL=false",
				"root","922922");
		return conn;*/
		return ds.getConnection();
	}
	
	public static List<Map<String,Object>> queryAll(Connection conn,String sql){
		List<Map<String,Object>>list=new ArrayList<>();//<>:泛型
		PreparedStatement ps=null;
		ResultSet rs=null;
		try {
			//Connection conn=getConn();
			ps=conn.prepareStatement(sql);
			rs=ps.executeQuery();
			/*while(rs.next()){
				Map<String,Object>map=new HashMap<>();
				Object obj1=rs.getObject(1);
				Object obj2=rs.getObject(2);
				Object obj3=rs.getObject(3);
				map.put("id", obj1);
				map.put("xsid", obj2);
				map.put("xk", obj3);
				list.add(map);
				System.out.println(obj1+","+obj2+","+obj3);
			}*/
			//
			ResultSetMetaData metaData=rs.getMetaData();//getMetaData():获取此 ResultSet 对象的列的编号、类型和属性。
			//获取列的数目
			int columnCount=metaData.getColumnCount();//getCoumnCount():返回此 ResultSet 对象中的列数。
			//遍历结果集==rs.next()是否有下一条数据，如果到了最后一条数据该方法就返回到false
			while(rs.next()){
				Map<String,Object>map=new LinkedHashMap<>();
				//使用for循环，来遍历字段
				for(int i=0;i<columnCount;i++){
					//获取索引对应的字段名
					String columnName=metaData.getColumnName(i+1);// getColumnName():获取指定列的名称
					//map:key为字段名，value:字段对应的值
					map.put(columnName,rs.getObject(i+1));
					//put(k,v)将指定的值与此映射中的指定键关联（可选操作）。
					//getObject(): 以 Java 编程语言中 Object 的形式获取此 ResultSet 对象的当前行中指定列的值。
				}
				//获取到了表中的所有字段
				list.add(map);// 向列表的尾部添加指定的元素（可选操作）。
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			if(rs!=null){
				try {
					rs.close();
					logger1.info("youngtse:结果集关闭");
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}finally{
					rs=null;
				}
			}
			if(ps!=null){
				try {
					ps.close();
					logger1.info("youngtse:预编译关闭");
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}finally{
					ps=null;
				}
			}
			if(conn!=null){
				try {
					conn.close();
					logger1.info("youngtse:连接关闭");
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}finally{
					conn=null;
				}
			}
		}
		return list;
	}
	/**
	 * 
	 * @param conn
	 * @param sql
	 * @param params:可变参数，应对于当参数个数不确定的场景
	 * 可变参数的格式   数据类型...参数名
	 * insert into xxx(字段1，字段2，字段3) values(？，？，？)
	 * @return 
	 */
	//实现更新-插入数据 或者修改数据
	public static int modifyDB(Connection conn,String sql,Object...params){
		int result=0;
		try {
			PreparedStatement ps=conn.prepareStatement(sql);
			//ps.setObject(parameterIndex, x);
			for(int i=0;i<params.length;i++){
				ps.setObject(i+1, params[i]);
			}
			result=ps.executeUpdate();
			logger1.info("youngtse:开始执行更新操作");
			//System.out.println("执行了更新操作");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
	
}
