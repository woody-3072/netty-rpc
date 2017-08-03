package cn.woody.spring.xml;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.w3c.dom.Element;


/**
 * 读取到xml中的一条reference记录
 * 
 * Title: ReferenceBeanDefinitionParser<br> 
 * Description: ReferenceBeanDefinitionParser<br> 
 * CreateDate:2017年7月27日 下午5:02:33 
 * @author woody
 */
public class PubConfigBeanDefinitionParser extends AbstractSingleBeanDefinitionParser  {

	protected Class<?> getBeanClass(Element element) {
		return ReferenceBean.class;
	}

	protected void doParse(Element element, BeanDefinitionBuilder builder) {
		// 读取xml属性
		String interfaceName = element.getAttribute("interface");
        int timeout = Integer.parseInt(element.getAttribute("timeout"));

        // 设置bean的属性
        builder.setLazyInit(false);
        builder.addPropertyValue("interfaceName", interfaceName);
        builder.addPropertyValue("timeout", timeout);
	}
}