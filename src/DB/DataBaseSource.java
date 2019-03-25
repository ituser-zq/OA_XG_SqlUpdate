package DB;


import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSourceFactory;
import org.apache.log4j.Logger;
/**
 * 封装数据库操作
 *
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class DataBaseSource {
	protected static Logger logger = Logger.getLogger(DataBaseSource.class.getName());
	private  DataSource ds = null;//数据源
	public DataBaseSource(String properties) {
		try {
			Properties prop = new Properties();
			// 通过类路径来加载属性文件
			prop.load(DataBaseSource.class.getClassLoader().getResourceAsStream(properties));
			// 获取数据源
			ds = BasicDataSourceFactory.createDataSource(prop);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	/**
	 * <p>获取数据连接</p>
	 *
	 * @return 数据库连接对象
	 * @throws SQLException
	 */
	private   Connection getConnection() 
			throws SQLException{
		return ds.getConnection();
	}
	
	
	/**
	 * <p>获取PreparedStatement</p>
	 *
	 * @param con
	 * @param sql
	 * @param params
	 * @return
	 * @throws Exception
	 */
	private  PreparedStatement prepareStatement(Connection con, String sql,
			Map<String, Object> params) throws Exception {
		PreparedStatement pstat = null;
		if(params != null && !params.isEmpty()){
			List paramList = new ArrayList();//参数列表集合
			String para = ":[[A-Za-z_0-9]]+";
			Pattern p = Pattern.compile(para);
			//查找sql语句中是否存在命令参数 比如：elect * from sc where id=:id1;
			Matcher m = p.matcher(sql);
			List<String> keyList = new ArrayList<String>();
			//查找字符串中是否有匹配正则表达式的字符/字符串
			while (m.find()) {
				int index = m.start();
				int end = m.end();
				//找到参数列表，有可能会传递多个参数
				String key = sql.substring(index+1, end);
				//把找到的参数列表到加入到list中
				keyList.add(key);
			} 
			//从sql语句中拆分出各个参数
			String[] split = sql.split(para);
			int length = split.length;
			boolean split_last_part_is_space = "".equals(split[length-1].trim());//sql语句分隔之后的最后部分是否为空白符
			
			int keyListSize = keyList.size();
			StringBuffer sqlbak = new StringBuffer();
			for (int i = 0; i < length; i++) {
				sqlbak.append(split[i]);
				
				if((split_last_part_is_space && i == length-1) || i==keyListSize){//当最后部分为空白符时，最后部分就不存在参数占位符
					continue;
				}
				
				String key = keyList.get(i);
				//如果传递过来的参数中包含正则表达式匹配的参数，把:id参数名替换成,?占位符的方式
				if(params.containsKey(key)){
					Object object = params.get(key);
					if(object instanceof Collection){//集合参数
						Collection c = (Collection)object;
						Iterator iterator = c.iterator();
						StringBuffer part = new StringBuffer();
						if(iterator.hasNext()){
							Object obj = iterator.next();
							paramList.add(obj);
							
							part.append(",?");
						}
						sqlbak.append(part.substring(1));
					}else if(object instanceof Object[]){//数组参数
						Object[] objs = (Object[])object;
						StringBuffer part = new StringBuffer();
						for(Object obj:objs){
							paramList.add(obj);
							
							part.append(",?");
						}
						sqlbak.append(part.substring(1));
					}else{//单个参赛
						paramList.add(object);
						sqlbak.append("?");
					}
				}else{
					logger.error("参数params未找到匹配 :user_id 的值");
				}
			}
			
			logger.info(sqlbak);
			
			if(!paramList.isEmpty()){
				pstat = con.prepareStatement(sqlbak.toString(),ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
				for (int i = 0; i < paramList.size(); i++) {
					Object value = paramList.get(i);
					if(value instanceof java.util.Date){//日期格式转换
						java.util.Date dateValue = (java.util.Date)value;
						java.sql.Timestamp timestamp = new Timestamp(dateValue.getTime());
						pstat.setObject(i + 1, timestamp);
					}else{
						pstat.setObject(i + 1, value);
					}
				}
			}
		}else{
			pstat = con.prepareStatement(sql);
			logger.info(sql);
		}
		
		
		
		return pstat;
	}
	
	
	
	
	/**
	 * <p>LIST查询</p>
	 *
	 * @param sql	SQL
	 * @param params 参数
	 * @return
	 */
	public  List<Map> queryForList(String sql,Map<String, Object> params)  {
		List<Map> result = new ArrayList<Map>();
		Connection con = null;
		PreparedStatement pstat = null;
		ResultSet rs = null;

		try {
			 con = getConnection();
			pstat = prepareStatement(con, sql, params);
			rs = pstat.executeQuery();

			if (rs != null) {
				ResultSetMetaData metaData = rs.getMetaData();
				int columnCount = metaData.getColumnCount();
				while (rs.next()) {
					Map map = new LinkedHashMap();
					for (int i = 0; i < columnCount; i++) {
						String columnName = metaData.getColumnName(i+1);
						map.put(columnName, rs.getObject(i + 1));
					}
					result.add(map);
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					logger.error(e.getMessage(), e);
				}finally{
					rs = null;
				}
			}
			if (pstat != null) {
				try {
					pstat.close();
				} catch (SQLException e) {
					logger.error(e.getMessage(), e);
				}finally{
					pstat = null;
				}
			}
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					logger.error(e.getMessage(), e);
				} finally {
					con = null;
				}
			}
		}
		return result;
	}
	
	
	/**
	 * <p>数据更新</p>
	 *
	 * @param sql
	 * @param params
	 * @return		SQL执行影响的数据量
	 */
	public  int executeUpdate(String sql,Map<String, Object> params)  {
		int result = 0;
		Connection con = null;
		PreparedStatement pstat = null;

		try {
			 con = getConnection();
			pstat = prepareStatement(con, sql, params);
			result = pstat.executeUpdate();//真正执行SQL语句的处理代码

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			if (pstat != null) {
				try {
					pstat.close();
				} catch (SQLException e) {
					logger.error(e.getMessage(), e);
				}finally{
					pstat = null;
				}
			}
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					logger.error(e.getMessage(), e);
				} finally {
					con = null;
				}
			}
		}
		return result;
	}
	
	/**
	 * <p>分页查询</p>
	 * <p>类似mysql的limit语法： select * from table limit m,n</p>
	 *
	 * @param sql sql语句
	 * @param params 参数集合
	 * @param m	从第m条数据开始	
	 * @param n 最多显示m条数据
	 * @return
	 */
	public  List<Map> queryForList(String sql,Map<String, Object> params,int m,int n)  {
		/**
		 * limit是mysql的语法 select * from table limit m,n
		 */
		List<Map> result = new ArrayList<Map>();
		Connection con = null;
		PreparedStatement pstat = null;
		ResultSet rs = null;
		
		try {
			con = getConnection();
			pstat = prepareStatement(con, sql, params);
			
			pstat.setMaxRows(m+n-1);
			rs = pstat.executeQuery();
			
			if (rs != null) {
				ResultSetMetaData metaData = rs.getMetaData();
				int columnCount = metaData.getColumnCount();
				
				
				if (m-1 > 0) {
					rs.absolute(m-1);
				}
				
				while (rs.next()) {
					Map map = new LinkedHashMap();
					for (int i = 0; i < columnCount; i++) {
						String columnName = metaData.getColumnName(i+1);
						map.put(columnName, rs.getObject(i + 1));
					}
					result.add(map);
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					logger.error(e.getMessage(), e);
				}finally{
					rs = null;
				}
			}
			if (pstat != null) {
				try {
					pstat.close();
				} catch (SQLException e) {
					logger.error(e.getMessage(), e);
				}finally{
					pstat = null;
				}
			}
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					logger.error(e.getMessage(), e);
				} finally {
					con = null;
				}
			}
		}
		return result;
	}
	
	
	
}
