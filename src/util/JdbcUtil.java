package util;
import java.sql.Connection;//��һ�������������ݿ�Ľӿڡ����ض����ݿ�����ӣ��Ự������������������ִ�� SQL ��䲢���ؽ���� 
import java.sql.PreparedStatement;//һ����ʾԤ����� SQL ���Ķ���Ľӿڣ������ݿ��Ӧ���е�sql����������������
import java.sql.ResultSet;//һ����ʾ���ݿ����������ݱ�ͨ��ͨ��ִ�в�ѯ���ݿ��������ɵĽӿڡ�
import java.sql.ResultSetMetaData;//һ�������ڻ�ȡ���� ResultSet �������е����ͺ�������Ϣ�Ķ���Ľӿڡ�
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;//����� collection��Ҳ��Ϊ���У����˽ӿڵ��û����Զ��б���ÿ��Ԫ�صĲ���λ�ý��о�ȷ�ؿ��ơ��û����Ը���Ԫ�ص��������������б��е�λ�ã�����Ԫ�أ��������б��е�Ԫ�ء�
import java.util.Map;//����ӳ�䵽ֵ�Ķ���һ��ӳ�䲻�ܰ����ظ��ļ���ÿ�������ֻ��ӳ�䵽һ��ֵ�Ľӿڡ� 
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSourceFactory;
import org.apache.log4j.Logger;


public class JdbcUtil {
	static Logger logger1=Logger.getLogger(JdbcUtil.class.getName());

	static DataSource ds=null;
	/**
	 * ��̬�����
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
		//��������
		conn=DriverManager.getConnection("jdbc:mysql://localhost:3306/xg?useSSL=false",
				"root","922922");
		return conn;*/
		return ds.getConnection();
	}
	
	public static List<Map<String,Object>> queryAll(Connection conn,String sql){
		List<Map<String,Object>>list=new ArrayList<>();//<>:����
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
			ResultSetMetaData metaData=rs.getMetaData();//getMetaData():��ȡ�� ResultSet ������еı�š����ͺ����ԡ�
			//��ȡ�е���Ŀ
			int columnCount=metaData.getColumnCount();//getCoumnCount():���ش� ResultSet �����е�������
			//���������==rs.next()�Ƿ�����һ�����ݣ�����������һ�����ݸ÷����ͷ��ص�false
			while(rs.next()){
				Map<String,Object>map=new LinkedHashMap<>();
				//ʹ��forѭ�����������ֶ�
				for(int i=0;i<columnCount;i++){
					//��ȡ������Ӧ���ֶ���
					String columnName=metaData.getColumnName(i+1);// getColumnName():��ȡָ���е�����
					//map:keyΪ�ֶ�����value:�ֶζ�Ӧ��ֵ
					map.put(columnName,rs.getObject(i+1));
					//put(k,v)��ָ����ֵ���ӳ���е�ָ������������ѡ��������
					//getObject(): �� Java ��������� Object ����ʽ��ȡ�� ResultSet ����ĵ�ǰ����ָ���е�ֵ��
				}
				//��ȡ���˱��е������ֶ�
				list.add(map);// ���б��β�����ָ����Ԫ�أ���ѡ��������
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			if(rs!=null){
				try {
					rs.close();
					logger1.info("youngtse:������ر�");
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
					logger1.info("youngtse:Ԥ����ر�");
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
					logger1.info("youngtse:���ӹر�");
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
	 * @param params:�ɱ������Ӧ���ڵ�����������ȷ���ĳ���
	 * �ɱ�����ĸ�ʽ   ��������...������
	 * insert into xxx(�ֶ�1���ֶ�2���ֶ�3) values(����������)
	 * @return 
	 */
	//ʵ�ָ���-�������� �����޸�����
	public static int modifyDB(Connection conn,String sql,Object...params){
		int result=0;
		try {
			PreparedStatement ps=conn.prepareStatement(sql);
			//ps.setObject(parameterIndex, x);
			for(int i=0;i<params.length;i++){
				ps.setObject(i+1, params[i]);
			}
			result=ps.executeUpdate();
			logger1.info("youngtse:��ʼִ�и��²���");
			//System.out.println("ִ���˸��²���");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
	
}
