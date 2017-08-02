package cn.woody.rpc.Reference;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import cn.woody.core.model.Callback;
import cn.woody.core.model.Response;
import cn.woody.core.model.RpcMessage;
import cn.woody.rpc.registry.local.RMessage.ReferenceConnectionRMessage;

/**
 * 服务消费者
 * 
 * Title: Reference<br> 
 * Description: Reference<br> 
 * CreateDate:2017年7月31日 下午1:55:39 
 * @author woody
 */
public class Reference {
	// 纯数据
	private final Set<String> references = new HashSet<>();				// 所有消费者接口信息
	private final Map<String, ReferenceConnection> handlers = new HashMap<>();	// 所有消费者和提供者建立的tcp连接通道   key address:ip+port   value channel
	
	// 一对多映射
	private final Map<String, List<String>> mapping = new HashMap<>();		// 多对多映射   key interfaceName   value addresses
	
	ExecutorService executor = Executors.newCachedThreadPool();
	
	Lock lock = new ReentrantLock();
	
	/**
	 * 创建reference的连接
	// 需要处理每一个address 
	// 如果已经创建的连接 但是不存在了要关闭
	// 如果没有的 就创建连接
	// 如果已存在 且最新的也是存在  则跳过
	 * Title: connect<br>
	 * Description: connect<br>
	 * CreateDate: 2017年8月2日 上午9:44:29<br>
	 * @category connect 
	 * @author woody
	 * @param msg  注册中心对当前接口的所有的提供的发布者的信息  所以 直接覆盖即可
	 */
	public void connect(ReferenceConnectionRMessage msg) {
		if (msg == null) {
			System.out.println("消费者创建连接消息，读取到消息为空！");
		} else {
			// 列出新的服务提供者列表   和此前的服务提供者old 
			List<String> oldAddresses = mapping.get(msg.getInterfaceName());
			List<String> newAddresses = msg.getAddresses();
			try {
				lock.lock();
				mapping.put(msg.getInterfaceName(), msg.getAddresses());	// 同步最新的服务提供者信息
				if (oldAddresses == null || oldAddresses.size() == 0) {		// 之前没有提供者
					if (newAddresses == null || newAddresses.size() == 0) {	// 现在也没有
						return;
					} else {				// 现在有了  新增
						newAddresses.forEach(string -> createConnect(string));
					}
				} else {			// 之前有
					if (newAddresses == null || newAddresses.size() == 0) {		// 现在没有了  移除
						oldAddresses.forEach(string -> removeConnect(string));
					} else {		// 对比
						newAddresses.stream()		// 处理新的连接
							.filter(string -> !oldAddresses.contains(string))
							.forEach(string -> createConnect(string));
						oldAddresses.stream()		// 处理老的连接
							.filter(string -> !newAddresses.contains(string))
							.forEach(string -> removeConnect(string));
					}
				}
			} finally {
				lock.unlock(); 			// 即使发生内存溢出  此代码块也能保证被执行  
			}
		}
	}
	private void createConnect(String address) {
		if (handlers.containsKey(address))			// 如果已经包含
			return;
		else {			// 如果没有包含
			ReferenceConnection conn = new ReferenceConnection(address, executor);
			handlers.put(address, conn);
			executor.execute(conn);		// 开始线程 维持连接
		}
	}
	private void removeConnect(String address) {
		if (handlers.containsKey(address)) {			// 如果已经包含
			ReferenceConnection conn = handlers.get(address);
			handlers.remove(address);		// 移除连接
			conn.close();
			conn = null;					// 线程置空 便于回收
		} else
			return;
	}
	
	/**
	 * 发送消息
	 * 通过interface 拿到多个address
	 * 随机选取一个address
	 * 通过address 拿到channel
	 * 然后往channel中发送消息
	 * 
	 * Title: sendRequest<br>
	 * Description: sendRequest<br>
	 * CreateDate: 2017年8月1日 下午2:31:31<br>
	 * @category sendRequest 
	 * @author woody
	 * @param msg
	 * @return
	 */
	public Callback sendRequest(RpcMessage msg) {
		List<String> address = mapping.get(msg.getInterfaceName());			// 获取可用服务器列表
		return send(getConnect(address), msg);
	}
	
	/**
	 * 获取channel连接  可以使轮训算法  可以是权值算法  拿到服务端连接
	 * 需要判断连接是否可用 如果不可用了 需要移除
	 * 
	 * Title: getConnect<br>
	 * Description: getConnect<br>
	 * CreateDate: 2017年8月1日 下午2:45:54<br>
	 * @category getConnect 
	 * @author woody
	 * @return
	 */
	private ReferenceConnection getConnect(List<String> address) {
		return handlers.get(address.get(0));
	}

	/**
	 * 最终的发送操作
	 * 
	 * Title: send<br>
	 * Description: send<br>
	 * CreateDate: 2017年8月1日 下午3:17:45<br>
	 * @category send 
	 * @author woody
	 * @param referenceConnection
	 * @param msg
	 */
	private Callback send(ReferenceConnection referenceConnection, RpcMessage msg) {
		Callback callback = new Callback(msg);
		if (referenceConnection == null) {
			callback.setResponse(Response.buildError("没有可用的服务器列表!"));
		} else {
			referenceConnection.sendRequest(callback);
		}
		
		return callback;
	}
	
	/**
	 * 关闭重试线程池
	 * 调用channel的close
	 * 关闭eventloopgroup
	 * 
	 * Title: destroy<br>
	 * Description: destroy<br>
	 * CreateDate: 2017年8月1日 下午5:40:39<br>
	 * @category destroy 
	 * @author woody
	 */
	public void destroy() {
		executor.shutdown();
		handlers.forEach((address, handler) -> {
			handler.close();
		});
	}
	
	public Set<String> getReference() {
		return references;
	}
}