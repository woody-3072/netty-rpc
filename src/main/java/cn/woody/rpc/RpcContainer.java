package cn.woody.rpc;

import cn.woody.core.model.Callback;
import cn.woody.core.model.RpcMessage;
import cn.woody.rpc.Reference.Reference;
import cn.woody.rpc.registry.Registry;
import cn.woody.rpc.registry.local.LocalRegistry;
import cn.woody.rpc.registry.local.RMessage.ReferenceConnectionRMessage;
import cn.woody.rpc.service.Service;
import cn.woody.rpc.service.ServiceInvokeTask;
import cn.woody.utils.EnumBeanType;
import io.netty.channel.ChannelHandlerContext;

/**
 * 全局初始化统一入口
 * 存储中心
 * 
 * Title: ApplicationInit<br> 
 * Description: ApplicationInit<br> 
 * CreateDate:2017年7月28日 上午9:27:23 
 * @author woody
 */
public class RpcContainer {
	private static RpcContainer init = new RpcContainer();		// 饱汉模式
	// 本应用所有对外连接的线程池
	private static Registry registry;
	private Service service = new Service();
	private Reference reference = new Reference();
	
	public static RpcContainer instance() { return init; }			// 返回单例实例
	
	static {
		Runtime.getRuntime().addShutdownHook(new Thread(){
			public void run() {
				// 添加关闭钩子
				init.destroy();
			}
		});
	}
	
	/**
	 * 初始化应用rpc信息
	 * 
	 * Title: initService<br>
	 * Description: initService<br>
	 * CreateDate: 2017年8月3日 上午10:35:10<br>
	 * @category initService 
	 * @author woody
	 */
	boolean notInit = true;
	public void init() {
		if (notInit) {
			notInit = false;
			
			System.out.println("******************************************************");
			System.out.println("                    start init");
			System.out.println("******************************************************");
			System.out.println("Start Registry Service!");
			// 连接注册中心 
			registry.connect();
			
			System.out.println("Start service!");
			// 启动服务
			service.start(12345);
			System.out.println("push service to registry!");
			// 发布服务到注册中心
			registry.service(service.getService(), 12345);
			
			// 注册消费者到注册中心  异步接收消息  实例化client到service的连接
			registry.reference(reference.getReference());
		}
	}
	
	/**
	 * 销毁
	 * 
	 * Title: destroy<br>
	 * Description: destroy<br>
	 * CreateDate: 2017年8月3日 上午11:47:04<br>
	 * @category destroy 
	 * @author woody
	 */
	public void destroy() {
		System.out.println("rpcContainer start to destroy!");
		reference.destroy();
		service.destroy();
		registry.destroy();
	}
	
	/**
	 * 对应接口存在服务发布者   添加连接
	 * 
	 * Title: createConnection<br>
	 * Description: createConnection<br>
	 * CreateDate: 2017年8月1日 上午10:19:26<br>
	 * @category createConnection 
	 * @author woody
	 * @param msg
	 */
	public void createConnection(ReferenceConnectionRMessage msg) {
		reference.connect(msg);
	}
	
	/**
	 * 服务提供者执行服务调用
	 * 
	 * Title: executeinvoke<br>
	 * Description: executeinvoke<br>
	 * CreateDate: 2017年8月3日 上午10:34:26<br>
	 * @category executeinvoke 
	 * @author woody
	 * @param ctx
	 * @param task
	 */
	public void executeinvoke(ChannelHandlerContext ctx, ServiceInvokeTask task) {
		service.execute(ctx, task);
	}
	
	/**
	 * 服务消费者发送请求
	 * 
	 * Title: sendRequest<br>
	 * Description: sendRequest<br>
	 * CreateDate: 2017年8月1日 下午3:03:51<br>
	 * @category sendRequest 
	 * @author woody
	 * @param msg
	 * @return
	 */
	public Callback sendRequest(RpcMessage msg) {
		return reference.sendRequest(msg);
	}
	
	/**
	 * 初始化数据
	 * 
	 * Title: initData<br>
	 * Description: initData<br>
	 * CreateDate: 2017年8月3日 下午12:33:09<br>
	 * @category initData 
	 * @author woody
	 * @param interfaceName
	 * @param ref
	 */
	public void initData(EnumBeanType type, Object ... obj) {
		if (type == EnumBeanType.REGISTRY) {
			registry = new LocalRegistry(obj[0].toString());
		} else if(type == EnumBeanType.SERVICE) {
			service.putService(obj[0].toString(), obj[1]);
		} else if(type == EnumBeanType.REFERENCE) {
			reference.putReference(obj[0].toString());
		}
	}
}