package cn.woody.rpc.registry.local;

import cn.woody.rpc.RpcContainer;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * 读取消息
 * 
 * Title: RegistryMessageReadHandler<br> 
 * Description: RegistryMessageReadHandler<br> 
 * CreateDate:2017年7月31日 下午6:16:02 
 * @author woody
 */
public class RMessageReadHandler extends SimpleChannelInboundHandler<RMessage> {
    private volatile Channel channel;

    public Channel getChannel() {
        return channel;
    }
    // 通道注册
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
        this.channel = ctx.channel();
    }

    // 读取消息
	protected void channelRead0(ChannelHandlerContext ctx, RMessage msg) throws Exception {
		if (msg == null) {
			System.out.println("收到消息，消息格式内容错误！"); 
		} else {
			RpcContainer.instance().createConnection(msg.getReferenceConnectionRMessage());
		}
	}
	
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    public void close() {
        channel.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
    }
    
    /**
     *  写消息到channel中
     * 
     * Title: writeMessage<br>
     * Description: writeMessage<br>
     * CreateDate: 2017年7月31日 下午6:19:15<br>
     * @category writeMessage 
     * @author woody
     * @param interfaces
     */
	public void writeMessage(RMessage msg) {
		channel.writeAndFlush(msg);
	}
}