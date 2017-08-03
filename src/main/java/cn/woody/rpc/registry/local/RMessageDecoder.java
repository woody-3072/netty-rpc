package cn.woody.rpc.registry.local;

import com.alibaba.fastjson.JSONObject;

import cn.woody.rpc.registry.local.RMessage.EnumRMsgType;
import cn.woody.rpc.registry.local.RMessage.ReferenceConnectionRMessage;
import cn.woody.rpc.registry.local.RMessage.ReferenceRMessage;
import cn.woody.rpc.registry.local.RMessage.ServiceRMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 * 注册中心 消息解码
 * 
 * Title: RMessageDecoder<br> 
 * Description: RMessageDecoder<br> 
 * CreateDate:2017年8月2日 下午1:10:33 
 * @author woody
 */
public class RMessageDecoder extends LengthFieldBasedFrameDecoder {
	public RMessageDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength) {
		super(maxFrameLength, lengthFieldOffset, lengthFieldLength);		// 帧最大长度    length属性起始偏移量   length属性长度
	}

	protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
		Object obj = super.decode(ctx, in);
		if (obj == null) {
			return null;
		} else {
			ByteBuf frame = (ByteBuf) obj;
			
			// 消息类型
			byte type = frame.readByte();
			
			// 消息长度 三个字节
			int length = frame.readUnsignedMedium();

			// 消息内容
			byte[] body = new byte[frame.readableBytes()];		// 通过剩余的可读字节数 设置为字节数组容器的容量
			ByteBuf bodyBuf = frame.readBytes(length);  
			bodyBuf.readBytes(body);		// 将缓冲区的数据读取到byte数组中
			
			System.out.println("收到Registry消息：" + new String(body));
			
			if (type == EnumRMsgType.SERVICE.getType()) {
				return RMessage.build(type, JSONObject.parseObject(new String(body), ServiceRMessage.class));
			} else if (type == EnumRMsgType.REFERENCE.getType()) {
				return RMessage.build(type, JSONObject.parseObject(new String(body), ReferenceRMessage.class));
			} else if (type == EnumRMsgType.REFERENCECONN.getType()) {
				return RMessage.build(type, JSONObject.parseObject(new String(body), ReferenceConnectionRMessage.class));
			}

			return null;
		}
	}
}
