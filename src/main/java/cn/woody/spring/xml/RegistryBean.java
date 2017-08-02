package cn.woody.spring.xml;

import org.springframework.beans.factory.InitializingBean;

import cn.woody.rpc.RpcContainer;
import cn.woody.utils.EnumBeanType;

/**
 * 一个服务至少有一个Registry bean
 * 一个bean 代表一个连接  往注册中心
 * 
 * Title: RegistryBean<br> 
 * Description: RegistryBean<br> 
 * CreateDate:2017年7月27日 下午5:04:37 
 * @author woody
 */
public class RegistryBean implements InitializingBean {
	private String address;
	private int serverPort;
	
	public void setIp(String address) {
		this.address = address;
	}
	public void setServerPort(int serverPort) {
		this.serverPort = serverPort;
	}

	// 填充实例
	public void afterPropertiesSet() throws Exception {
		RpcContainer.instance().initData(EnumBeanType.REGISTRY, address, serverPort);
	}
}