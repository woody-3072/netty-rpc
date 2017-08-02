package cn.woody.spring.xml;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

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
public class ServiceBean implements ApplicationContextAware, ApplicationListener<ContextRefreshedEvent> {
	private String interfaceName;						// 接口名称
	private String ref;									// 接口实现ID
	private ApplicationContext applicationContext;		// spring上下文  用于获取ref
	
	public String getInterfaceName() {
		return interfaceName;
	}
	public void setInterfaceName(String interfaceName) {
		this.interfaceName = interfaceName;
	}
	public String getRef() {
		return ref;
	}
	public void setRef(String ref) {
		this.ref = ref;
	}

	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	/**
	 * 监听spring容器上下文初始换完成 执行此监听的方法
	 */
	public void onApplicationEvent(ContextRefreshedEvent event) {
		if(event.getApplicationContext().getParent() == null){		//root application context 没有parent，他就是老大.  
	        //需要执行的逻辑代码，当spring容器初始化完成后就会执行该方法。  
			RpcContainer.instance().initData(EnumBeanType.SERVICE, interfaceName, applicationContext.getBean(ref));
	    } 
	}
}