package cn.woody.rpc.registry.local.test;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import cn.woody.rpc.registry.local.RMessage;
import cn.woody.rpc.registry.local.RMessage.EnumRMsgType;
import cn.woody.rpc.registry.local.RMessage.ReferenceConnectionRMessage;
import cn.woody.rpc.registry.local.RMessage.ReferenceRMessage;
import cn.woody.rpc.registry.local.RMessage.ServiceRMessage;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

public class RMessageResvHandler extends SimpleChannelInboundHandler<RMessage> {

	final Map<String, Set<String>> service;		// key interface  value addresses   存储数据
	final Map<String, ChannelGroup> reference;	// key interface  value channel

	public RMessageResvHandler(Map<String, Set<String>> service, Map<String, ChannelGroup> reference) {
		this.service = service;
		this.reference = reference;
	}
	
    public void channelRead0(ChannelHandlerContext ctx, RMessage msg) throws Exception {
    	if (msg == null || msg.getBody() == null) {
    		System.out.println("注册中心收到错误消息null!");
		} else {
			if (msg.getType() == EnumRMsgType.SERVICE.getType()) 		// 是服务发布者注册消息
					serviceMsg(msg.getServiceRMessage());
			else if (msg.getType() == EnumRMsgType.REFERENCE.getType())	// 是服务订阅者注册消息
					referenceMsg(msg.getReferenceRMessage(), ctx.channel());
			else
					System.out.println("注册中心收到错误消息 未知类型!");
		}
    }

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        
        ctx.close();
    }
    
    private void serviceMsg(ServiceRMessage msg) {
    	System.out.println("收到服务注册消息，发布订阅");
    	msg.getInterfaceNames().forEach(interfaceName -> {
    		if (service.containsKey(interfaceName)) {						// 如果已经有了服务提供者
    			service.get(interfaceName).add(msg.getAddress());			// 添加到接口的服务器列表
			} else {														// 如果没有  添加一个
				Set<String> addresses = new HashSet<>();
				addresses.add(msg.getAddress());
				service.put(interfaceName, addresses);		
			}    			
			if(reference.containsKey(interfaceName)) {		// 当前接口存在订阅
				System.out.println("服务提供者，推送给订阅者消息");
				reference.get(interfaceName)
						.forEach(channel -> channel.writeAndFlush(
						RMessage.build(
								EnumRMsgType.REFERENCECONN.getType(), 
								ReferenceConnectionRMessage.build(interfaceName, service.get(interfaceName))
								)
							)
						);
			}
			
    	});
    }
    
    private void referenceMsg(ReferenceRMessage msg, Channel channel) {
    	System.out.println("收到消费注册消息，订阅消费");
    	msg.getInterfaceNames().forEach(interfaceName -> {
    		if (reference.containsKey(interfaceName)) {
				reference.get(interfaceName).add(channel);			// reference 添加通道
			} else {
				final ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
				channels.add(channel);
				reference.put(interfaceName, channels);
			}
    		if (service.containsKey(interfaceName)) {
				channel.writeAndFlush(RMessage.build(
								EnumRMsgType.REFERENCECONN.getType(), 
								ReferenceConnectionRMessage.build(interfaceName, service.get(interfaceName))
								)
							);
			}
    	});
    }
}