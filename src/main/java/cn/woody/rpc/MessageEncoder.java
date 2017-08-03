package cn.woody.rpc;

import com.alibaba.fastjson.JSONObject;

import cn.woody.core.model.RpcMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * 消息编码
 * 
 * Title: MessageEncoder<br> 
 * Description: MessageEncoder<br> 
 * CreateDate:2017年8月1日 下午5:06:24 
 * @author woody
 */
public class MessageEncoder extends MessageToByteEncoder<RpcMessage> {

	// 编码
	protected void encode(ChannelHandlerContext ctx, RpcMessage msg, ByteBuf out) throws Exception {
		if (msg == null || msg.getBody() == null) {
			System.out.println("this message is null");
		} else {
			System.out.println("发送RPC消息：" + msg + " ctx :" + ctx.channel());
			out.writeByte(msg.getType());
			out.writeBytes(msg.getMessageId().getBytes());
			String json = JSONObject.toJSONString(msg.getBody());
			byte[] body = json.getBytes();
			out.writeInt(body.length);
			out.writeBytes(body);
		}
	}
}