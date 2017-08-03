package cn.woody.rpc.registry.local;

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
		return new RMessage().setType(type).setBody(body);
	}
	
	public ServiceRMessage getServiceRMessage() {
		return EnumRMsgType.SERVICE.getType() == type ? (ServiceRMessage) body : null;
	}
	public ReferenceRMessage getReferenceRMessage() {
		return EnumRMsgType.REFERENCE.getType() == type ? (ReferenceRMessage) body : null;
	}
	public ReferenceConnectionRMessage getReferenceConnectionRMessage() {
		return EnumRMsgType.REFERENCECONN.getType() == type ? (ReferenceConnectionRMessage) body : null;
	}
	
	public byte getType() {
		return type;
	}
	public RMessage setType(byte type) {
		this.type = type;
		return this;
	}
	public RMessage setBody(RMessage body) {
		this.body = body;
		return this;
	}
	public RMessage getBody() {
		return body;
	}
	
	public String toString() {
		return "RMessage [type=" + type + ", body=" + body + "]";
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
			return new ServiceRMessage().setAddress(address).setInterfaceNames(interfaceNames);
		}
		public Set<String> getInterfaceNames() {
			return interfaceNames;
		}
		public ServiceRMessage setInterfaceNames(Set<String> interfaceNames) {
			this.interfaceNames = interfaceNames;
			return this;
		}
		public String getAddress() {
			return address;
		}
		public ServiceRMessage setAddress(String address) {
			this.address = address;
			return this;
		}
		public String toString() {
			return "ServiceRMessage [interfaceNames=" + interfaceNames + ", address=" + address + "]";
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
			return new ReferenceRMessage().setInterfaceNames(interfaceNames);
		}
		public ReferenceRMessage setInterfaceNames(Set<String> interfaceNames) {
			this.interfaceNames = interfaceNames;
			return this;
		}
		public Set<String> getInterfaceNames() {
			return interfaceNames;
		}
		public String toString() {
			return "ReferenceRMessage [interfaceNames=" + interfaceNames + "]";
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
		private Set<String> addresses;
		
		public static ReferenceConnectionRMessage build(String interfaceName, Set<String> addresses) {
			return new ReferenceConnectionRMessage().setInterfaceName(interfaceName).setAddresses(addresses);
		}
		
		public ReferenceConnectionRMessage setInterfaceName(String interfaceName) {
			this.interfaceName = interfaceName;
			return this;
		}
		public ReferenceConnectionRMessage setAddresses(Set<String> addresses) {
			this.addresses = addresses;
			return this;
		}
		public String getInterfaceName() {
			return interfaceName;
		}
		public Set<String> getAddresses() {
			return addresses;
		}
		public String toString() {
			return "ReferenceConnectionRMessage [interfaceName=" + interfaceName + ", addresses=" + addresses + "]";
		}
	}
	
	public static enum EnumRMsgType {
		SERVICE((byte)1, "服务注册消息"),
		REFERENCE((byte)2, "订阅注册消息"),
		REFERENCECONN((byte)3, "订阅获取连接消息");
		
		private final byte type;
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