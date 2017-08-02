package cn.woody.rpc.Reference;

import java.util.concurrent.ConcurrentHashMap;

import cn.woody.core.model.Callback;
import cn.woody.core.model.Response;
import cn.woody.core.model.RpcMessage;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * 消息发送handler
 * 
 * Title: MessageSendHandler<br> 
 * Description: MessageSendHandler<br> 
 * CreateDate:2017年8月1日 上午11:20:08 
 * @author woody
 */
public class MessageSendHandler extends SimpleChannelInboundHandler<RpcMessage> {
	// 保存请求messageID 和回调的映射关系   请求是创建好回调
    private ConcurrentHashMap<String, Callback> mapCallback = new ConcurrentHashMap<>();
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
    protected void channelRead0(ChannelHandlerContext ctx, RpcMessage msg) throws Exception {
        String messageId = msg.getMessageId();
        // 通过messageId 拿到回调
        Callback callback = mapCallback.get(messageId);
        if (callback != null) {
            mapCallback.remove(messageId);
            callback.setResponse((Response) msg.getBody());
        }
    }

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    /**
     * 通道关闭
     * 
     * Title: close<br>
     * Description: close<br>
     * CreateDate: 2017年8月1日 上午11:24:51<br>
     * @category close 
     * @author woody
     */
    public void close() {
        channel.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
    }

    /**
     * 代理发送rpc请求
     * 
     * Title: sendRequest<br>
     * Description: sendRequest<br>
     * CreateDate: 2017年8月1日 上午11:24:59<br>
     * @category sendRequest 
     * @author woody
     * @param callback
     */
    public void sendRequest(Callback callback) {
        mapCallback.put(callback.getRequest().getMessageId(), callback);
        channel.writeAndFlush(callback.getRequest());
    }
}