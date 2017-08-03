package cn.woody.test;

import java.util.concurrent.TimeUnit;

import com.alibaba.fastjson.JSONObject;

import cn.woody.core.model.Callback;
import cn.woody.core.model.Request;
import cn.woody.core.model.Response;
import cn.woody.rpc.MessageDecoder;
import cn.woody.rpc.MessageEncoder;
import cn.woody.rpc.Reference.MessageSendHandler;
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
public class TestReference implements Runnable {
	private static EventLoopGroup client = new NioEventLoopGroup();

	private String address;				// 服务端发布接口网络地址
	private String serverIP; private int serverPort;		// ip 端口
	private MessageSendHandler handler;
	
	public TestReference(String address) {
		this.address = address;
		this.serverIP = address.split(":")[0];
		this.serverPort = Integer.parseInt(address.split(":")[1]);
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
							.addLast(new MessageDecoder(1024 * 1024, 36, 4))		// 起始第一位是类型  偏移量是36  用于放messageId 偏移位置4  存length
							.addLast(new MessageEncoder())
							.addLast(new MessageSendHandler());
						}
					});
			ChannelFuture channelFuture = c.connect(serverIP, serverPort).sync().addListener(new ChannelFutureListener() {
				public void operationComplete(ChannelFuture future) throws Exception {
					if (future.isSuccess()) {
						handler = future.channel().pipeline().get(MessageSendHandler.class);
					}
				}
			});		// 同步等待
			
			System.out.println("服务提供者  " + address + "  连接成功!");
			channelFuture.channel().closeFuture().addListener(new ChannelFutureListener() {
				public void operationComplete(ChannelFuture future) throws Exception {
					System.out.println("rpc server is closed! -- " + address);
					try {
						TimeUnit.SECONDS.sleep(5);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
					run();
				}
			});							// 同步监听通道关闭
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("rpc server connect fail, start to reconnecting to: " + address +  " on 5 seconds!");
			try {
				TimeUnit.SECONDS.sleep(5);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			run();
		}
	}
	
	public static void main(String[] args) {
		Request req = Request.build("aaa", "bbb", null);
		System.out.println(JSONObject.toJSONString(req));
		Response resp = Response.buildSuccess("OK");
		System.out.println(JSONObject.toJSONString(resp));
		
//		new TestReference("127.0.0.1:12345").run();
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