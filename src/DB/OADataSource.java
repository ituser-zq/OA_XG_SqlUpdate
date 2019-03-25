package DB;
/**
 * 继承该DataBaseSource类去实现oa数据库操作
 * @author ZQ
 *
 */
public class OADataSource extends DataBaseSource {

	public OADataSource(){
		super("oa_dbcp.properties");
	}
	private OADataSource(String properties){
		super(properties);
	}
}
