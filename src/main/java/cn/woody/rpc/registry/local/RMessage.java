package cn.woody.rpc.registry.local;

import java.util.List;
import java.util.Set;

/**
 * 注册中心通信消息
 * 
 * Title: RMessage<br> 
 * Description: RMessage<br> 
 * CreateDate:2017年7月31日 下午3:49:17 
 * @author woody
 */
public class RMessage {
	private byte type;				// 消息类型
	/*
	 * 注册服务发布服务       list<string> interface   adress ip:port
	 * 注册消费者消息           address ip:port   string interface
	 * 订阅消费消息               string interface  list address ip:port
	 */
	private RMessage body;			// 消息内容  
	
	public static RMessage build(byte type, RMessage body) {
		return new RMessage(type, body);
	}
	private RMessage(byte type, RMessage body2) {
		this.body = body2;
		this.type = type;
	}
	protected RMessage(){}
	
	public ReferenceConnectionRMessage getReferenceConnectionRMessage() {
		return EnumRMsgType.REFERENCECONN.getType() == type ? (ReferenceConnectionRMessage) body : null;
	}
	
	public byte getType() {
		return type;
	}
	public void setType(byte type) {
		this.type = type;
	}
	public RMessage getBody() {
		return body;
	}
	public void setBody(RMessage body) {
		this.body = body;
	}
	
	/**
	 * 	 * 注册服务发布服务       list<string> interface   adress ip:port
	 * Title: ServiceRMessage<br> 
	 * Description: ServiceRMessage<br> 
	 * CreateDate:2017年8月1日 下午6:35:29 
	 * @author woody
	 */
	public static class ServiceRMessage extends RMessage {
		private Set<String> interfaceNames;
		private String address;
		
		public static ServiceRMessage build(Set<String> interfaceNames, String address) {
			return new ServiceRMessage(interfaceNames, address);
		}
		private ServiceRMessage(Set<String> interfaceNames, String address) {
			this.interfaceNames = interfaceNames;
			this.address = address;
		}
		
		public Set<String> getInterfaceNames() {
			return interfaceNames;
		}
		public void setInterfaceNames(Set<String> interfaceNames) {
			this.interfaceNames = interfaceNames;
		}
		public String getAddress() {
			return address;
		}
		public void setAddress(String address) {
			this.address = address;
		}
	}
	
	/**
	 * 	 * 注册消费者消息           address ip:port   string interface
	 * Title: ReferenceRMessage<br> 
	 * Description: ReferenceRMessage<br> 
	 * CreateDate:2017年8月1日 下午6:35:40 
	 * @author woody
	 */
	public static class ReferenceRMessage extends RMessage {
		private Set<String> interfaceNames;
		
		public static ReferenceRMessage build(Set<String> interfaceNames) {
			return new ReferenceRMessage(interfaceNames);
		}
		private ReferenceRMessage(Set<String> interfaceNames) {
			this.interfaceNames = interfaceNames;
		}
		public Set<String> getInterfaceNames() {
			return interfaceNames;
		}
		public void setInterfaceNames(Set<String> interfaceNames) {
			this.interfaceNames = interfaceNames;
		}
	}
	
	/**
	 * 	 * 订阅消费消息               string interface  list address ip:port
	 * Title: ReferenceConnectionRMessage<br> 
	 * Description: ReferenceConnectionRMessage<br> 
	 * CreateDate:2017年8月1日 下午6:35:49 
	 * @author woody
	 */
	public static class ReferenceConnectionRMessage extends RMessage {
		private String interfaceName;
		private List<String> addresses;
		
		public static ReferenceConnectionRMessage build(String interfaceName, List<String> addresses) {
			return new ReferenceConnectionRMessage(interfaceName, addresses);
		}
		private ReferenceConnectionRMessage(String interfaceName, List<String> addresses) {
			this.interfaceName = interfaceName;
			this.addresses = addresses;
		}
		
		public String getInterfaceName() {
			return interfaceName;
		}
		public void setInterfaceName(String interfaceName) {
			this.interfaceName = interfaceName;
		}
		public List<String> getAddresses() {
			return addresses;
		}
		public void setAddresses(List<String> addresses) {
			this.addresses = addresses;
		}
	}
	
	public static enum EnumRMsgType {
		SERVICE((byte)1, "服务注册消息"),
		REFERENCE((byte)2, "订阅注册消息"),
		REFERENCECONN((byte)3, "订阅获取连接消息");
		
		private byte type;
		private String desc;
		
		public byte getType() {
			return type;
		}
		public String getDesc() {
			return desc;
		}
		EnumRMsgType (byte type, String desc) {
			this.type = type;
			this.desc = desc;
		}
	}
}