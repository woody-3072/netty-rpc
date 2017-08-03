package cn.woody.rpc.registry.local.test;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import cn.woody.rpc.registry.local.RMessageDecoder;
import cn.woody.rpc.registry.local.RMessageEncoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.AdaptiveRecvByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * 注册中心 服务端
 * 
 * Title: RegistryServer<br> 
 * Description: RegistryServer<br> 
 * CreateDate:2017年8月2日 下午3:10:10 
 * @author woody
 */
public class RegistryServer {
	int port = 23456;		// 来自配置文件
	
	// 接受请求线程池
	EventLoopGroup boss = new NioEventLoopGroup();
	// io处理线程池
	EventLoopGroup work = new NioEventLoopGroup();
	
	static final Map<String, Set<String>> service = new HashMap<>();		// key interface  value addresses   存储数据
	static final Map<String, ChannelGroup> reference = new HashMap<>();	// key interface  value channel
	
	public void start() {
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
				.addLast(new RMessageDecoder(1024 * 1024, 1, 3))		// 起始第一位是类型  偏移量是36  用于放messageId 偏移位置4  存length
				.addLast(new RMessageEncoder())
				.addLast(new RMessageResvHandler(service, reference));
			}
		})
		.childOption(ChannelOption.SO_KEEPALIVE, true);
		
		try {
			ChannelFuture f = server.bind(port).sync();
			System.out.println("Registry started on port : " + port);
			f.channel().closeFuture().sync().addListener(new ChannelFutureListener() {
				public void operationComplete(ChannelFuture future) throws Exception {
					System.out.println("server is closed complete!");
				}
			});
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			
		}
	}
	
	public static void main(String[] args) {
		new RegistryServer().start();
	}
}