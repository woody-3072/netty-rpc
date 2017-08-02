package cn.woody.rpc.Reference;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.UUID;

import cn.woody.core.model.Request;
import cn.woody.core.model.RpcMessage;
import cn.woody.rpc.RpcContainer;
import cn.woody.utils.EnumMsgType;

/**
 * 服务消费者 代理类
 * 
 * Title: ReferenceProxyInvocationHandler<br> 
 * Description: ReferenceProxyInvocationHandler<br> 
 * CreateDate:2017年8月1日 下午2:24:45 
 * @author woody
 */
public class ReferenceProxyInvocationHandler implements InvocationHandler {
	private int timeout;
	
	public ReferenceProxyInvocationHandler(int timeout) {
		this.timeout = timeout;
	}
	
	// 代理的调用
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		String messageId = UUID.randomUUID().toString();
		// 构建request
		Request req = Request.build(method.getDeclaringClass().getName(), method.getName(), args);
		// 构建完成的channel消息
		RpcMessage msg = RpcMessage.build(EnumMsgType.REQUEST.getType(), messageId, req);

		return RpcContainer.instance().sendRequest(msg).start(timeout);
	}

}
