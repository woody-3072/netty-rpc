package cn.woody.rpc.service;

import java.util.Map;
import java.util.concurrent.Callable;

import org.springframework.util.MethodInvoker;

import cn.woody.core.model.Request;
import cn.woody.core.model.Response;
import cn.woody.core.model.RpcMessage;
import cn.woody.utils.EnumMsgType;

/**
 * 服务提供者  invoke任务线程
 * 
 * Title: ServiceInvokeTask<br> 
 * Description: ServiceInvokeTask<br> 
 * CreateDate:2017年8月2日 下午2:01:10 
 * @author woody
 */
public class ServiceInvokeTask implements Callable<RpcMessage> {
	private RpcMessage msg;			// Rpc消息
	private Map<String, Object> service;  // key interface  value ref
	
	public ServiceInvokeTask(Map<String, Object> service, RpcMessage msg) {
		this.msg = msg;
		this.service = service;
	}
	
	// 调用方法  返回结果
	public RpcMessage call() throws Exception {
		if (msg == null || msg.request() == null) {
			return RpcMessage.build(EnumMsgType.RESPONSE.getType(), msg.getMessageId(), Response.buildError("错误的请求！"));
		} else {
			try {
				Request req = msg.request();
				
				MethodInvoker invoke = new MethodInvoker();
				invoke.setTargetObject(service.get(req.getInterfaceName()));
				invoke.setTargetMethod(req.getMethodName());
				invoke.setArguments(req.getParametersVal());
				invoke.prepare();
				return RpcMessage.build(EnumMsgType.RESPONSE.getType(), msg.getMessageId(), Response.buildSuccess(invoke.invoke()));
			} catch (Exception e) {
				//throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException
				return RpcMessage.build(EnumMsgType.RESPONSE.getType(), msg.getMessageId(), Response.buildError(e.getMessage()));
			}
		}
	}

	public String toString() {
		return "ServiceInvokeTask [msg=" + msg + "]";
	}
}