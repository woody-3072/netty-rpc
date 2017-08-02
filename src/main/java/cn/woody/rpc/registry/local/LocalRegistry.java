package cn.woody.rpc.registry.local;

import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import cn.woody.rpc.registry.Registry;
import cn.woody.rpc.registry.local.RMessage.EnumRMsgType;
import cn.woody.rpc.registry.local.RMessage.ReferenceRMessage;
import cn.woody.rpc.registry.local.RMessage.ServiceRMessage;
import cn.woody.utils.CommonUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class LocalRegistry implements Registry {
	private String address;			// registry 服务注册中心地址
	private int serverPort;			// 发布服务端口
	private ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
	private static EventLoopGroup registryGroup = new NioEventLoopGroup();		// 不要用工作线程作为重试线程，因为是工作线程  会轮训上面绑定的key  有特殊处理

	private final RMessageReadHandler handler = new RMessageReadHandler();
	
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
								.addLast(handler);
							}
						});
				
				ChannelFuture channelFuture = registry.connect(ip, port).sync();		// 同步等待
				
				channelFuture.channel().closeFuture().sync();							// 同步监听通道关闭
				/*.addListener(new ChannelFutureListener() {							// 无法做到断开重连
					 public void operationComplete(final ChannelFuture channelFuture) throws Exception {
		                if (!channelFuture.isSuccess()) {
			                   registryGroup.schedule(new Runnable() {
			                        public void run() {
			                        	System.out.println("连接注册中心失败，5秒后重连！" + address);
			            				try {
			            					TimeUnit.SECONDS.sleep(5);
			            				} catch (InterruptedException e1) {
			            					e1.printStackTrace();
			            				}
			                            connect();
			                        }
			                    }, 5, TimeUnit.SECONDS);
			                }
			            }
				});*/
			} catch (InterruptedException e) {
				System.out.println("连接注册中心失败，5秒后重连！" + address);
			} finally {
				executor.execute(new Runnable() {
					public void run() {
						try {
							TimeUnit.SECONDS.sleep(5);
							connect();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				});
			}
		} else {
			System.out.println("注册中心初始化参数错误！" + address);
			System.exit(1);
		}
	}

	// 通道中写入服务提供者信息
	public void service(Set<String> interfaces) {
		handler.writeMessage(RMessage.build(EnumRMsgType.SERVICE.getType(), ServiceRMessage.build(interfaces, CommonUtils.getAddress(serverPort))));
	}

	// 通道中写入服务消费者的信息
	public void reference(Set<String> interfaces) {
		handler.writeMessage(RMessage.build(EnumRMsgType.REFERENCE.getType(), ReferenceRMessage.build(interfaces)));
	}

	// 销毁registry 客户端连接
	public void destroy() {
		executor.shutdown();
		handler.close();
		registryGroup.shutdownGracefully();			
	}
	
	//  设置必须的信息
	public void setInfo(String address, int serverPort) {
		this.address = address;
		this.serverPort = serverPort;
	}
}