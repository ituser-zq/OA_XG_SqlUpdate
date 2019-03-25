package thread;

import java.util.Observable;

import org.apache.log4j.Logger;

import DB.DataBaseSource;
/**
 * ±ªπ€≤Ï’ﬂ
 * @author ZQ
 *
 */
public abstract class AbstractThread extends Observable implements Runnable{

	static Logger logger=Logger.getLogger(AbstractThread.class.getName());
	protected DataBaseSource oaDataSource;
	protected DataBaseSource xgDataSource;
	protected Long sleep;
	
	public AbstractThread(DataBaseSource oaDataSource, DataBaseSource xgDataSource, Long sleep) {
		super();
		this.oaDataSource = oaDataSource;
		this.xgDataSource = xgDataSource;
		this.sleep = sleep;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}

}
