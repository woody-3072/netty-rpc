package cn.woody.utils;

/**
 * 消息类型
 * 
 * Title: EnumMsgType<br> 
 * Description: EnumMsgType<br> 
 * CreateDate:2017年8月1日 下午5:02:41 
 * @author woody
 */
public enum EnumMsgType {
	REQUEST((byte)1, "请求"),
	RESPONSE((byte)2, "响应");
	
	private byte type;
	private String desc;
	
	public byte getType() {
		return type;
	}
	public void setType(byte type) {
		this.type = type;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}

	EnumMsgType(byte type, String desc) {
		this.type = type;
		this.desc = desc;
	}
}