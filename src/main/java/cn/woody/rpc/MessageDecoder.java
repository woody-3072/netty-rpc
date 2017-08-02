package cn.woody.rpc;

import com.alibaba.fastjson.JSONObject;

import cn.woody.core.model.Request;
import cn.woody.core.model.Response;
import cn.woody.core.model.RpcMessage;
import cn.woody.utils.EnumMsgType;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 * 消息解码
 * 服务端 客户端 通用
 * 
 * Title: MessageDecoder<br> 
 * Description: MessageDecoder<br> 
 * CreateDate:2017年8月1日 下午5:05:22 
 * @author woody
 */
public class MessageDecoder extends LengthFieldBasedFrameDecoder {

	private int lengthFieldOffset;		// length起始偏移量
	
	public MessageDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength) {
		super(maxFrameLength, lengthFieldOffset, lengthFieldLength);		// 帧最大长度    length属性起始偏移量   length属性长度
		this.lengthFieldOffset = lengthFieldOffset;
	}

	// 解码
	protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
		Object obj = super.decode(ctx, in);
		if (obj == null) {			// 解析到半包直接返回
			return null;
		} else {
			ByteBuf frame = (ByteBuf) obj;
			
			// type
			byte type = frame.readByte();
			// MessageId
			ByteBuf buf = frame.readBytes(lengthFieldOffset);
			byte[] messageId = new byte[lengthFieldOffset];
			buf.readBytes(messageId);	
			// length
			int length = frame.readInt();
			// body
			byte[] body = new byte[frame.readableBytes()];		// 通过剩余的可读字节数 设置为字节数组容器的容量
			ByteBuf bodyBuf = frame.readBytes(length);
			bodyBuf.readBytes(body);
			
			return RpcMessage.build(
					type, 
					new String(messageId), 
					JSONObject.parseObject(new String(body), type == EnumMsgType.REQUEST.getType() ? Request.class : Response.class) 
				);
		}
	}
}