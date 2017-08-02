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
	private Service service = new Service();
	private Registry registry = new LocalRegistry();
	private Reference reference = new Reference();
	
	public static RpcContainer instance() { return init; }			// 返回单例实例
	
	/**
	 * rpc 启动入口
	 * 
	 * Title: go<br>
	 * Description: go<br>
	 * CreateDate: 2017年7月28日 下午6:07:14<br>
	 * @category go 
	 * @author woody
	 */
	public void go() {
		System.out.println("Start Registry Service!");
		// 连接注册中心 
		registry.connect();
		// 启动服务
		service.start();
		// 发布服务到注册中心
		registry.service(service.getService());
		// 注册消费者到注册中心  异步接收消息  实例化client到service的连接
		registry.reference(reference.getReference());
//		// 启动消费者  获取订阅消息  并创建连接
//		registry.reference(interfaces);
	}
	
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
	
	public void executeinvoke(ChannelHandlerContext ctx, ServiceInvokeTask task) {
		service.execute(ctx, task);
	}
	
	/**
	 * 发送请求
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
	 * 初始化xml数据
	 * 
	 * Title: initData<br>
	 * Description: initData<br>
	 * CreateDate: 2017年7月31日 上午11:28:46<br>
	 * @category initData 
	 * @author woody
	 * @param type
	 * @param obj
	 */
	public void initData(EnumBeanType type, Object ... obj) {
		if (type == EnumBeanType.SERVICE) {
			service.putService(obj[0].toString(), obj[1]);
		}  else if (type == EnumBeanType.REGISTRY) {
			registry.setInfo(obj[0].toString(), Integer.parseInt(obj[1].toString()));
		}
	}
}