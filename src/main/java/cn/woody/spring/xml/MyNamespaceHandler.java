package cn.woody.spring.xml;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

/**
 * 读取spring配置文件
 * 
 * Title: RpcNamespaceHandler<br> 
 * Description: RpcNamespaceHandler<br> 
 * CreateDate:2017年7月27日 下午4:01:29 
 * @author woody
 */
public class MyNamespaceHandler extends NamespaceHandlerSupport {

	static {
		System.out.println("Init Logo!");
	}
	
	public void init() {
		// xml填充 到model 
		registerBeanDefinitionParser("registry", new RegistryBeanDefinitionParser());
		registerBeanDefinitionParser("service", new ServiceBeanDefinitionParser());
		registerBeanDefinitionParser("reference", new ReferenceBeanDefinitionParser());
		
		// 开始启动
//		RpcContainer.instance().go();
	}
	
	
}