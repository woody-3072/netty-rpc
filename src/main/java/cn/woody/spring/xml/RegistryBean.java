package cn.woody.spring.xml;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

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
public class RegistryBean implements InitializingBean, ApplicationListener<ContextRefreshedEvent> {
	private String address;
	private int serverPort;
	
	public void setAddress(String address) {
		this.address = address;
	}
	public void setServerPort(int serverPort) {
		this.serverPort = serverPort;
	}

	// 初始化registry 并连接
	public void afterPropertiesSet() throws Exception {
		RpcContainer.instance().initData(EnumBeanType.REGISTRY, address, serverPort);
	}

	/**
	 * 监听spring容器上下文初始换完成 执行此监听的方法
	 */
	public void onApplicationEvent(ContextRefreshedEvent event) {
		if(event.getApplicationContext().getParent() == null){		//root application context 没有parent，他就是老大.  
	        //需要执行的逻辑代码，当spring容器初始化完成后就会执行该方法。  
			RpcContainer.instance().init();
	    } 
	}
}