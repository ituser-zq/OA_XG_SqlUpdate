package thread;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import DB.DataBaseSource;
import util.ListComparatorUtil;

public class xgTOoaThread extends AbstractThread {

	public xgTOoaThread(DataBaseSource oaDataSource, DataBaseSource xgDataSource, Long sleep) {
		super(oaDataSource, xgDataSource, sleep);
		// TODO Auto-generated constructor stub
	}

	//初始化上下文
	public boolean initialContext(){
		try {
			logger.info("**********"+xgTOoaThread.class.getName()+"************");
			return true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			notifyListener();
			logger.error(e.getMessage());
			e.printStackTrace();
			logger.info("线程启动初始化失败");
			return false;
		}
	}
	public void notifyListener(){
		super.setChanged();//change修改为true
		//通知观察者更新===change=true
		notifyObservers();
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		if(initialContext()){
			try {
				//处理业务
				doBusiness();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				notifyListener();
				logger.error(e.getMessage());
			}
			
		}
	}
	//同步学生信息表，学生成绩表，评奖申请表，评奖质疑表
	public void doBusiness() throws InterruptedException{
		boolean isStart=true;
		while(isStart){
			//同步各个表
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
		
		//oa同步xg的数据,使用oa数据库对比xg数据库
		List[] compare=ListComparatorUtil.compare(xgList, oaList);
		
		List<Map>insert=compare[0];//数据库的插入操作
		//insert into xxx(,字段1,字段2) values(:字段值1,:字段值2)
		if(insert!=null&&!insert.isEmpty()){
			//遍历List
			for(int i=0;i<insert.size();i++){
				//取出List中的Map
				Map map=insert.get(i);
				Set keyset=map.keySet();//字段名的集合
				Map<String,Object>params=new HashMap<>();
				StringBuffer sql=new StringBuffer();//保存语句
				StringBuffer fields=new StringBuffer();//保存字段名
				StringBuffer values=new StringBuffer();//保存字段值
				for(Object key:keyset){
					fields.append(",").append(key);
					values.append(",:").append(key);
					params.put(key.toString(),map.get(key));
				}
				//拼接插入sql语句
				sql.append("insert into ")
				.append(tableName).append("(")
				.append(fields.deleteCharAt(0))
				.append(")")
				.append(" VALUES(").append(values.deleteCharAt(0))
				.append(")");
				oaDataSource.executeUpdate(sql.toString(), params);
			}
		}
		//执行更新操作 update table set 字段1=:字段
		List<Map> update=compare[1];
		if(update!=null&&!update.isEmpty()){
			//遍历List
			for(int i=0;i<update.size();i++){
				//取出List中的Map
				Map map=update.get(i);
				Set keyset=map.keySet();//字段名的集合
				Map<String,Object>params=new HashMap<>();
				StringBuffer sql=new StringBuffer();//保存语句
				StringBuffer fields=new StringBuffer();//保存字段名
				StringBuffer values=new StringBuffer();//保存字段值
				for(Object key:keyset){
					fields.append(",").append(key).append("=:").append(key);
					params.put(key.toString(), map.get(key));
				}
				//拼接更新sql语句
				sql.append("UPDATE ").append(tableName)
				.append(" set ").append(fields.deleteCharAt(0))
				.append(" WHERE id=:id");
				oaDataSource.executeUpdate(sql.toString(), params);
			}
		}
	}
}
