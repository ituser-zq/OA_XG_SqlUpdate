package thread;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import DB.DataBaseSource;
import util.ListComparatorUtil;

public class oaToxgThread extends AbstractThread {

	public oaToxgThread(DataBaseSource oaDataSource, DataBaseSource xgDataSource, Long sleep) {
		super(oaDataSource, xgDataSource, sleep);
		// TODO Auto-generated constructor stub
	}

	//��ʼ��������
	public boolean initialContext(){
		try {
			logger.info("**********"+oaToxgThread.class.getName()+"************");
			return true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			notifyListener();
			logger.error(e.getMessage());
			e.printStackTrace();
			logger.info("�߳�������ʼ��ʧ��");
			return false;
		}
	}
	public void notifyListener(){
		super.setChanged();//change�޸�Ϊtrue
		//֪ͨ�۲��߸���===change=true
		notifyObservers();
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		if(initialContext()){
			try {
				//����ҵ��
				doBusiness();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				notifyListener();
				logger.error(e.getMessage());
			}
			
		}
	}
	//ͬ��ѧ����Ϣ��ѧ���ɼ�������������������ɱ�
	public void doBusiness() throws InterruptedException{
		boolean isStart=true;
		while(isStart){
			//ͬ��������
			syncTableData("data_xsxx", null);
			syncTableData("data_xscj", null);
			syncTableData("data_pjsq", null);
			syncTableData("data_pjgs", null);
			syncTableData("data_pjzy", null);
			Thread.sleep(sleep);
		}
	}
	
	public void syncTableData(String tableName,Map<String,Object>filter){
		StringBuffer query_sql=new StringBuffer("select * from ")
				.append(tableName).append(" where 1=1 ");
		if(filter != null && !filter.isEmpty()){
			Set<String> filter_keySet = filter.keySet();
			for(Object key:filter_keySet){
				query_sql.append(" and ").append(key).append("=:").append(key);
			}
		}
		List<Map>oaList=oaDataSource.queryForList(query_sql.toString(), null);
		List<Map>xgList=xgDataSource.queryForList(query_sql.toString(), null);
		
		//xgͬ��oa������,ʹ��oa���ݿ�Ա�xg���ݿ�
		List[] compare=ListComparatorUtil.compare(oaList, xgList);
		
		List<Map>insert=compare[0];//���ݿ�Ĳ������
		//insert into xxx(,�ֶ�1,�ֶ�2) values(:�ֶ�ֵ1,:�ֶ�ֵ2)
		if(insert!=null&&!insert.isEmpty()){
			//����List
			for(int i=0;i<insert.size();i++){
				//ȡ��List�е�Map
				Map map=insert.get(i);
				Set keyset=map.keySet();//�ֶ����ļ���
				Map<String,Object>params=new HashMap<>();
				StringBuffer sql=new StringBuffer();//�������
				StringBuffer fields=new StringBuffer();//�����ֶ���
				StringBuffer values=new StringBuffer();//�����ֶ�ֵ
				for(Object key:keyset){
					fields.append(",").append(key);
					values.append(",:").append(key);
					params.put(key.toString(),map.get(key));
				}
				//ƴ�Ӳ���sql���
				sql.append("insert into ")
				.append(tableName).append("(")
				.append(fields.deleteCharAt(0))
				.append(")")
				.append(" VALUES(").append(values.deleteCharAt(0))
				.append(")");
				xgDataSource.executeUpdate(sql.toString(), params);
			}
		}
		//ִ�и��²��� update table set �ֶ�1=:�ֶ�
		List<Map> update=compare[1];
		if(update!=null&&!update.isEmpty()){
			//����List
			for(int i=0;i<update.size();i++){
				//ȡ��List�е�Map
				Map map=update.get(i);
				Set keyset=map.keySet();//�ֶ����ļ���
				Map<String,Object>params=new HashMap<>();
				StringBuffer sql=new StringBuffer();//�������
				StringBuffer fields=new StringBuffer();//�����ֶ���
				StringBuffer values=new StringBuffer();//�����ֶ�ֵ
				for(Object key:keyset){
					fields.append(",").append(key).append("=:").append(key);
					params.put(key.toString(), map.get(key));
				}
				//ƴ�Ӹ���sql���
				sql.append("UPDATE ").append(tableName)
				.append(" set ").append(fields.deleteCharAt(0))
				.append(" WHERE id=:id");
				xgDataSource.executeUpdate(sql.toString(), params);
			}
		}
	}
}
