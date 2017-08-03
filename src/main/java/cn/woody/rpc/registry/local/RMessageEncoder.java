package cn.woody.rpc.registry.local;

import com.alibaba.fastjson.JSONObject;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * 消息编码
 * 
 * Title: RMessageEncoder<br> 
 * Description: RMessageEncoder<br> 
 * CreateDate:2017年7月31日 下午6:27:21 
 * @author woody
 */
public class RMessageEncoder extends MessageToByteEncoder<RMessage> {

	protected void encode(ChannelHandlerContext ctx, RMessage msg, ByteBuf out) throws Exception {
		if (msg == null || msg.getBody() == null) {
			System.out.println("消息发送失败，消息格式错误");
		} else {
			System.out.println("连接信息 : " + ctx.channel());
			System.out.println("发送Registry消息：" + msg);
			out.writeByte(msg.getType());
			String json = JSONObject.toJSONString(msg.getBody());
			byte[] body = json.getBytes();
			out.writeMedium(body.length);
			out.writeBytes(body);
		}
	}
}