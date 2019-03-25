package observer;

import java.util.Observable;
import java.util.Observer;

import org.apache.log4j.Logger;

import thread.AbstractThread;
/**
 * 程序监听器
 * 线程失败之后，重新启动
 * 线程：被观察者
 * @author ZQ
 *
 */
public class ThreadObserver implements Observer {
	Logger logger=Logger.getLogger(ThreadObserver.class.getName());

	private AbstractThread thread;
	//通过构造方法给线程实例化
	public ThreadObserver(AbstractThread thread) {
		
		this.thread=thread;
		//需要对应的地址调用增加观察者的方法
		this.thread.addObserver(this);//this：当前对象就是观察者
	}
	@Override
	public void update(Observable o, Object arg) {
		// TODO Auto-generated method stub
		logger.info("*************************");
		logger.info("线程"+thread.getClass().getName()+"重新启动");
		//线程启动失败后重新启动线程，增加一个观察者
		thread.addObserver(this);
		new Thread(thread).start();//重新启动线程
	}
	public void start(){
		logger.info("*************************");
		logger.info("线程"+thread.getClass().getName()+"启动");
		new Thread(thread).start();//第一次启动线程	
		}

}
