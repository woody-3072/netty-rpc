package cn.woody.spring.xml;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.w3c.dom.Element;

public class ServiceBeanDefinitionParser extends AbstractSingleBeanDefinitionParser {

	protected Class<?> getBeanClass(Element element) {
		return ServiceBean.class;
	}
	
	protected void doParse(Element element, BeanDefinitionBuilder builder) {
		// 读取xml属性
		String ref = element.getAttribute("ref");
		String interfaceName = element.getAttribute("interface");

        // 一个bean的定义
		builder.setLazyInit(false);
        // 设置bean的属性
		builder.addPropertyValue("interfaceName", interfaceName);
		builder.addPropertyValue("ref", ref);
	}
}