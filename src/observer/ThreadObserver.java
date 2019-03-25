package observer;

import java.util.Observable;
import java.util.Observer;

import org.apache.log4j.Logger;

import thread.AbstractThread;
/**
 * ���������
 * �߳�ʧ��֮����������
 * �̣߳����۲���
 * @author ZQ
 *
 */
public class ThreadObserver implements Observer {
	Logger logger=Logger.getLogger(ThreadObserver.class.getName());

	private AbstractThread thread;
	//ͨ�����췽�����߳�ʵ����
	public ThreadObserver(AbstractThread thread) {
		
		this.thread=thread;
		//��Ҫ��Ӧ�ĵ�ַ�������ӹ۲��ߵķ���
		this.thread.addObserver(this);//this����ǰ������ǹ۲���
	}
	@Override
	public void update(Observable o, Object arg) {
		// TODO Auto-generated method stub
		logger.info("*************************");
		logger.info("�߳�"+thread.getClass().getName()+"��������");
		//�߳�����ʧ�ܺ����������̣߳�����һ���۲���
		thread.addObserver(this);
		new Thread(thread).start();//���������߳�
	}
	public void start(){
		logger.info("*************************");
		logger.info("�߳�"+thread.getClass().getName()+"����");
		new Thread(thread).start();//��һ�������߳�	
		}

}
