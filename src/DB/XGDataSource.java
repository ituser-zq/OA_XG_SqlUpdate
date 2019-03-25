package DB;
/**
 * 继承该DataBaseSource类去实现xg数据库操作
 * @author ZQ
 *
 */
public class XGDataSource extends DataBaseSource {

	public XGDataSource(){
		super("xg_dbcp.properties");
	}
	private XGDataSource(String properties){
		super(properties);
	}
}
