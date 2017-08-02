package cn.woody.core.model;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 接受回调  等待超时
 * 
 * Title: Callback<br> 
 * Description: Callback<br> 
 * CreateDate:2017年8月1日 下午2:39:26 
 * @author woody
 */
public class Callback {
	private Lock lock = new ReentrantLock();
	private Condition responseOK = lock.newCondition();
	
	private Response response;		// 响应
	private RpcMessage request;
	
	public Callback(RpcMessage request) {
		this.request = request;
	}

	/**
	 * 启动callback监听
	 * 阻塞调用  等待返回
	 * 
	 * Title: start<br>
	 * Description: start<br>
	 * CreateDate: 2017年8月1日 下午2:03:26<br>
	 * @category start 
	 * @author woody
	 * @return 
	 */
	public Object start(int timeout) {
		try {
			lock.lock();
			if (response == null) {
				responseOK.await(timeout, TimeUnit.SECONDS);		// 阻塞响应  等待成功通知 或者 阻塞超时
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			lock.unlock();
		}
		
		return response;
	}
	
	/**
	 * 从通道中成功读取到response
	 * 
	 * Title: setResponse<br>
	 * Description: setResponse<br>
	 * CreateDate: 2017年8月1日 下午3:32:36<br>
	 * @category setResponse 
	 * @author woody
	 * @param response
	 */
	public void setResponse(Response response) {
		try {
			lock.lock();
			this.response = response;
			responseOK.signal();		// 唤醒锁定的线程
		} finally {
			lock.unlock();
		}
	}

	public RpcMessage getRequest() {
		return request;
	}
}