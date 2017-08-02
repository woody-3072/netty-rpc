package cn.woody.core.model;

import cn.woody.utils.EnumMsgType;

/**
 * rpc 消息
 * 
 * Title: RpcMessage<br> 
 * Description: RpcMessage<br> 
 * CreateDate:2017年8月1日 下午1:57:35 
 * @author woody
 */
public class RpcMessage {
	private byte type;						// 消息类型
	private String messageId;				// 消息ID
	private RpcMessage body;				// 消息体
	
	public static RpcMessage build(byte type, String messageId, RpcMessage body) {
		return new RpcMessage(type, messageId, body);
	}
	
	protected RpcMessage(){}
	private RpcMessage(byte type, String messageId, RpcMessage body) {
		this.type = type;
		this.messageId = messageId;
		this.body = body;
	}

	/**
	 * 获取body中的属性
	 * 
	 * Title: getInterfaceName<br>
	 * Description: getInterfaceName<br>
	 * CreateDate: 2017年8月1日 下午2:36:15<br>
	 * @category getInterfaceName 
	 * @author woody
	 * @return
	 */
	public String getInterfaceName() {
		return ((Request)body).getInterfaceName();
	}
	
	public Request getRequest() {
		return type == EnumMsgType.REQUEST.getType() ? (Request) body : null;
	}
	
	public RpcMessage getBody() {
		return body;
	}
	public String getMessageId() {
		return messageId;
	}
	public byte getType() {
		return type;
	}

	public String toString() {
		return "RpcMessage [type=" + type + ", messageId=" + messageId + ", body=" + body + "]";
	}
}