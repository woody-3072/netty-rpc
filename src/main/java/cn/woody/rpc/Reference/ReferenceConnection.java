package cn.woody.rpc.Reference;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import cn.woody.core.model.Callback;
import cn.woody.rpc.MessageDecoder;
import cn.woody.rpc.MessageEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * 创建netty  到服务端的连接通道  维持线程
 * 
 * Title: ReferenceConnectTask<br> 
 * Description: ReferenceConnectTask<br> 
 * CreateDate:2017年8月1日 上午10:15:48 
 * @author woody
 */
public class ReferenceConnection implements Runnable {
	private EventLoopGroup client = new NioEventLoopGroup();
	private final MessageSendHandler handler = new MessageSendHandler();

	private String address;				// 服务端发布接口网络地址
	private String serverIP; private int serverPort;		// ip 端口
	private ExecutorService executors;
	
	public ReferenceConnection(String address, ExecutorService executors) {
		this.address = address;
		this.serverIP = address.split(":")[0];
		this.serverPort = Integer.parseInt(address.split(":")[1]);
		this.executors = executors;
	}
	
	// 连接
	public void run() {
		try {
			Bootstrap c = new Bootstrap();
	        c.group(client)
	                .channel(NioSocketChannel.class)
	                .option(ChannelOption.TCP_NODELAY, true)
	                .option(ChannelOption.SO_KEEPALIVE, true)
	                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)			// 使用直接内存内存池分配bytebuf
	                .handler(new ChannelInitializer<SocketChannel>() {
						public void initChannel(SocketChannel ch) throws Exception {
							ch.pipeline()
							// 编解码
							.addLast(new MessageDecoder(1024 * 1024, 37, 4))		// 起始第一位是类型  偏移量是36  用于放messageId 偏移位置4  存length
							.addLast(new MessageEncoder())
							.addLast(handler);
						}
					});
			ChannelFuture channelFuture = c.connect(serverIP, serverPort).sync();		// 同步等待
			
			channelFuture.channel().closeFuture().sync().addListener(new ChannelFutureListener() {
				public void operationComplete(ChannelFuture future) throws Exception {
					System.out.println("rpc server is closed! -- " + address);
				}
			});							// 同步监听通道关闭
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			// 连接失败  或者断开  释放资源 重新连接
			executors.execute(new Runnable() {
				public void run() {
                    System.out.println("rpc server is down, start to reconnecting to: " + address +  " on 5 seconds!");
					try {
						TimeUnit.SECONDS.sleep(5);
						run();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			});
		}
	}

	/**
	 * 关闭通道 资源
	 * 
	 * Title: close<br>
	 * Description: close<br>
	 * CreateDate: 2017年8月1日 上午11:50:51<br>
	 * @category close 
	 * @author woody
	 */
	public void close() {
		handler.close();  	// 关闭通道
		client.shutdownGracefully();	// 关闭连接
	}
	
	/**
	 * 发送请求
	 * 
	 * Title: sendRequest<br>
	 * Description: sendRequest<br>
	 * CreateDate: 2017年8月1日 下午3:00:17<br>
	 * @category sendRequest 
	 * @author woody
	 * @param callback
	 */
	public void sendRequest(Callback callback) {
		handler.sendRequest(callback);
	}
	
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
}