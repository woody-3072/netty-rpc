package cn.woody.spring.xml;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import cn.woody.rpc.RpcContainer;
import cn.woody.utils.EnumBeanType;

/**
 * 服务提供者 bean
 * 需要调用多次  
 * 能否多次监听ContextRefreshedEvent事件
 * 
 * Title: ServiceBean<br> 
 * Description: ServiceBean<br> 
 * CreateDate:2017年8月1日 下午5:45:12 
 * @author woody
 */
public class ServiceBean implements InitializingBean, ApplicationContextAware {
	private String interfaceName;						// 接口名称
	private String ref;									// 接口实现ID
	private ApplicationContext applicationContext;		// spring上下文  用于获取ref
	
	public void setInterfaceName(String interfaceName) {
		this.interfaceName = interfaceName;
	}
	public void setRef(String ref) {
		this.ref = ref;
	}

	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	// 设置属性
	public void afterPropertiesSet() throws Exception {
		RpcContainer.instance().initData(EnumBeanType.SERVICE, interfaceName, applicationContext.getBean(ref));
	}
}