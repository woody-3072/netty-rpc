package cn.woody.rpc.registry.local;

import java.util.Set;
import java.util.concurrent.TimeUnit;

import cn.woody.rpc.registry.Registry;
import cn.woody.rpc.registry.local.RMessage.EnumRMsgType;
import cn.woody.rpc.registry.local.RMessage.ReferenceRMessage;
import cn.woody.rpc.registry.local.RMessage.ServiceRMessage;
import cn.woody.utils.CommonUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * 网络注册中心
 * 
 * Title: LocalRegistry<br> 
 * Description: LocalRegistry<br> 
 * CreateDate:2017年8月3日 上午10:19:45 
 * @author woody
 */
public class LocalRegistry implements Registry {
	private String address;			// registry 服务注册中心地址
	
	private static EventLoopGroup registryGroup = new NioEventLoopGroup();		// 不要用工作线程作为重试线程，因为是工作线程  会轮训上面绑定的key  有特殊处理
	private static RMessageSendHandler handler;
	
	public LocalRegistry(String address) {
		this.address = address;
	}

	// 连接到注册中心
	public void connect() {
		if (address.split(":").length == 2) {
			String ip = address.split(":")[0];
			int port = Integer.parseInt(address.split(":")[1]);

			try {
				Bootstrap registry = new Bootstrap();
				registry.group(registryGroup)
						.channel(NioSocketChannel.class)
						.option(ChannelOption.TCP_NODELAY, true)
						.option(ChannelOption.SO_KEEPALIVE, true)
						.handler(new ChannelInitializer<SocketChannel>() {
							public void initChannel(SocketChannel ch) throws Exception {
								ch.pipeline()
								.addLast(new RMessageEncoder())
								.addLast(new RMessageDecoder(1024 * 1024, 1, 3))
								.addLast(new RMessageSendHandler());
							}
						});

				// 同步连接  失败时基于当前线程继续重连
				ChannelFuture channelFuture = registry.connect(ip, port).sync().addListener(new ChannelFutureListener() {
					public void operationComplete(ChannelFuture future) throws Exception {
						if (future.isSuccess()) {			// 如果连接成功  则赋值handler
							handler = future.channel().pipeline().get(RMessageSendHandler.class);
						}
					}
				});		// 同步等待

				System.out.println("Registry connected!");
				// 连接成功后释放线程 异步监听断开连接的消息  方便重连
				channelFuture.channel().closeFuture().addListener(new ChannelFutureListener() {							
					 public void operationComplete(ChannelFuture future) throws Exception {
							if (future.isSuccess()) {
								reconnect();		// 如果异步监听到了断开了连接  马上重连
							}
					 }
				});
			} catch (Exception e) {
				// 连接线程  可能是main  或者是 eventloopgroup线程    连接失败时重连的
				e.printStackTrace();
				reconnect();
			}
		} else {
			System.out.println("注册中心初始化参数错误！" + address);
			System.exit(1);
		}
	}
		
	private void reconnect() {
		System.out.println("连接注册中心失败，5秒后重连！" + address);
		try {
			TimeUnit.SECONDS.sleep(5);
			connect();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
	}

	// 通道中写入服务提供者信息
	public void service(Set<String> interfaces, int port) {
		if (interfaces == null || interfaces.size() == 0) {
			return;
		}
		handler.writeMessage(RMessage.build(EnumRMsgType.SERVICE.getType(), ServiceRMessage.build(interfaces, CommonUtils.getAddress(port))));
	}

	// 通道中写入服务消费者的信息
	public void reference(Set<String> interfaces) {
		if (interfaces == null || interfaces.size() == 0) {
			return;
		}
		handler.writeMessage(RMessage.build(EnumRMsgType.REFERENCE.getType(), ReferenceRMessage.build(interfaces)));
	}

	// 销毁registry 客户端连接
	public void destroy() {
		handler.close();
		registryGroup.shutdownGracefully();			
	}
	
	//  设置必须的信息
	public void setInfo(String address) {
		this.address = address;
	}
}