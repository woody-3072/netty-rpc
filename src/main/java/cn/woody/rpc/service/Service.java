package cn.woody.rpc.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

import cn.woody.core.model.RpcMessage;
import cn.woody.rpc.MessageDecoder;
import cn.woody.rpc.MessageEncoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.AdaptiveRecvByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * 服务提供中心
 * 
 * Title: Service<br> 
 * Description: Service<br> 
 * CreateDate:2017年8月1日 下午6:04:20 
 * @author woody
 */
public class Service {
	// 接受请求线程池
	EventLoopGroup boss = new NioEventLoopGroup();
	// io处理线程池
	EventLoopGroup work = new NioEventLoopGroup();
	private final Map<String, Object> service = new HashMap<>();	// key interfaceName value ref
	// 执行任务的线程
	private static volatile ListeningExecutorService threadPoolExecutor = MoreExecutors.listeningDecorator(Executors.newCachedThreadPool());

	public void start(int port) {
		if (service.size() > 0) {
			ServerBootstrap server = new ServerBootstrap();
			server.group(boss, work)
			.channel(NioServerSocketChannel.class)
			.option(ChannelOption.SO_BACKLOG, 128)				// 连接缓冲区大小
			.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000)		// 连接超时
			.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)						// 默认使用堆外内存
			.option(ChannelOption.RCVBUF_ALLOCATOR, AdaptiveRecvByteBufAllocator.DEFAULT)			// 动态分配bytebuf大小  第一次分1k  连续两次大于 或者小于  则修改默认分配的大小
			.childHandler(new ChannelInitializer<SocketChannel>() {
				protected void initChannel(SocketChannel ch) throws Exception {
					ch.pipeline()
					// 编解码
					.addLast(new MessageEncoder())
					.addLast(new MessageDecoder(1024 * 1024, 36, 4))		// 起始第一位是类型  偏移量是36  用于放messageId 偏移位置4  存length
					.addLast(new MessageResvHandler(service));
				}
			})
			.childOption(ChannelOption.SO_KEEPALIVE, true);
			
			try {
				ChannelFuture f = server.bind(port).sync();
				System.out.println("Service started and Listener on port : " + port);
				
				// 同步连接  异步监听
				f.channel().closeFuture().addListener(new ChannelFutureListener() {
					public void operationComplete(ChannelFuture future) throws Exception {
						System.out.println("server is closed complete!");
					}
				});
			} catch (InterruptedException e) {
				e.printStackTrace();
				System.out.println("服务发布者启动失败！");
				System.exit(1);
			}
		} else {
			System.out.println("此项目没有需要发布的服务");
		}
	}
	
	/**
	 * service 执行invoke
	 * 
	 * Title: execute<br>
	 * Description: execute<br>
	 * CreateDate: 2017年8月2日 下午2:49:34<br>
	 * @category execute 
	 * @author woody
	 * @param ctx
	 * @param task
	 */
	public void execute(ChannelHandlerContext ctx, ServiceInvokeTask task) {
		Futures.addCallback(threadPoolExecutor.submit(task), new FutureCallback<RpcMessage>() {
			public void onSuccess(RpcMessage result) {
				ctx.writeAndFlush(result).addListener(new ChannelFutureListener() {
					public void operationComplete(ChannelFuture future) throws Exception {
						System.out.println("成功处理request请求 : " + task + " 并返回完成。" + result);
					}
				});
			}

			public void onFailure(Throwable t) {
				System.out.println("已经吃了业务异常的情况下请求失败！" + t.getMessage());
				t.printStackTrace();
			}
		}, threadPoolExecutor);
	}
	
	/**
	 * 服务关闭
	 * 
	 * Title: destroy<br>
	 * Description: destroy<br>
	 * CreateDate: 2017年7月28日 下午5:46:56<br>
	 * @category destroy 
	 * @author woody
	 */
	public void destroy() {
		work.shutdownGracefully();
        boss.shutdownGracefully();
	}
	
	public void putService(String interfaceName, Object ref) {
		service.put(interfaceName, ref);
	}
	public Set<String> getService() {
		return service.keySet();
	}
}