package OperProgram;

import org.apache.log4j.Logger;

import DB.DataBaseSource;
import DB.OADataSource;
import DB.XGDataSource;
import observer.ThreadObserver;
import thread.oaToxgThread;
import thread.xgTOoaThread;

public class TestMain {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		Logger logger=Logger.getLogger(TestMain.class.getName());
		DataBaseSource oa=new OADataSource();
		DataBaseSource xg=new XGDataSource();
		logger.info("***********平台启动***********");
		//oa同步xg
		new ThreadObserver(new xgTOoaThread(oa, xg, 4000L)).start();
		//xg同步oa
		new ThreadObserver(new oaToxgThread(oa, xg, 4000L)).start();
	}

}
