package DB;
/**
 * �̳и�DataBaseSource��ȥʵ��oa���ݿ����
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
