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
		Object[] objects=list.toArray();//toArray():���ذ��ʵ�˳������б��е�����Ԫ�ص����飨�ӵ�һ��Ԫ�ص����һ��Ԫ�أ���
		for(Object obj:objects){
			System.out.println(obj);
		}
		logger.info("youngtse:��ѯ���ݳɹ�");
		/*for(Map<String,Object>m:list){
			for(String k:m.keySet()){
				System.out.println(k+":"+m.get(k)+" ");
			}
		}*/
		//System.out.print(list.toArray()[0]);
	}
	@Test
	public void testupdate(){
		//�������
		/*String insert_sql="insert into data_xsxx(id,xh,xm) values(?,?,?)";
		Connection conn;
		try {
			conn=JdbcUtil.getConn();
			if(JdbcUtil.modifyDB(conn,insert_sql,"x010","123456","misaki")>0){
				System.out.println("�������ݳɹ�");
			}else{
				System.out.println("��������ʧ��");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		//���²���
		String update_sql="update data_xsxx"
				+ " set mz=?"
				+ " where id=?";
		try {
			Connection conn=JdbcUtil.getConn();
			if(JdbcUtil.modifyDB(conn, update_sql,"����","x010" )>0){
				logger.info("youngtse:�������ݳɹ�");
				//System.out.println("�������ݳɹ�");
			}else{
				logger.info("youngtse:��������ʧ��");
				//System.out.println("��������ʧ��");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		
	}

	//��������
	@Test
	public void test() {
		try {
			//��������
			Class.forName("com.mysql.jdbc.Driver");
			//��������
			Connection conn=DriverManager.getConnection("jdbc:mysql://localhost:3306/xg?useSSL=false","root","922922");
			//����sql����
			String select_sql="select * from data_xsxx";
			PreparedStatement ps=conn.prepareStatement(select_sql);
			//��������
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
