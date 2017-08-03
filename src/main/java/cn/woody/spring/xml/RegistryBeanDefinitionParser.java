package cn.woody.spring.xml;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.w3c.dom.Element;

public class RegistryBeanDefinitionParser extends AbstractSingleBeanDefinitionParser {
	
	protected Class<?> getBeanClass(Element element) {
		return RegistryBean.class;
	}
	
	protected void doParse(Element element, BeanDefinitionBuilder builder) {
		// 读取xml属性
		String address = element.getAttribute("address");
	    int port = Integer.parseInt(element.getAttribute("port"));
	    
	    builder.setLazyInit(false);
	    builder.addPropertyValue("address", address);
	    builder.addPropertyValue("serverPort", port);
	}
}