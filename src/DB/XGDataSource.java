package DB;
/**
 * �̳и�DataBaseSource��ȥʵ��xg���ݿ����
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
