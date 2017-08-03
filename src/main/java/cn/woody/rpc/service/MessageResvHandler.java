package cn.woody.rpc.service;

import java.util.Map;

import cn.woody.core.model.RpcMessage;
import cn.woody.rpc.RpcContainer;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * 服务提供者 消息处理
 * 
 * Title: MessageResvHandler<br> 
 * Description: MessageResvHandler<br> 
 * CreateDate:2017年8月2日 下午2:51:41 
 * @author woody
 */
public class MessageResvHandler extends SimpleChannelInboundHandler<RpcMessage> {

	private Map<String, Object> service;  // key interface  value ref
	public MessageResvHandler(Map<String, Object> service) {
		this.service = service;
	}

	// 读取通道消息 提交一个invoke任务
    protected void channelRead0(ChannelHandlerContext ctx, RpcMessage msg) throws Exception {
    	RpcContainer.instance().executeinvoke(ctx, new ServiceInvokeTask(service, msg));
    }
}