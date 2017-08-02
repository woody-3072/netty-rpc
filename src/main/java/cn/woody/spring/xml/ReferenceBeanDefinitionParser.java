package cn.woody.spring.xml;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;


/**
 * 读取到xml中的一条reference记录
 * 
 * Title: ReferenceBeanDefinitionParser<br> 
 * Description: ReferenceBeanDefinitionParser<br> 
 * CreateDate:2017年7月27日 下午5:02:33 
 * @author woody
 */
public class ReferenceBeanDefinitionParser implements BeanDefinitionParser {

	public BeanDefinition parse(Element element, ParserContext parserContext) {
		// 读取xml属性
		String id = element.getAttribute("id");
		String interfaceName = element.getAttribute("interface");
        int timeout = Integer.parseInt(element.getAttribute("timeout"));

        // 一个bean的定义
        RootBeanDefinition beanDefinition = new RootBeanDefinition();
        beanDefinition.setBeanClass(ReferenceBean.class);
        beanDefinition.setLazyInit(false);

        // 设置bean的属性
        beanDefinition.getPropertyValues().addPropertyValue("interfaceName", interfaceName);
        beanDefinition.getPropertyValues().addPropertyValue("timeout", timeout);

        // 注册bean到上下文
        parserContext.getRegistry().registerBeanDefinition(id, beanDefinition);
        return beanDefinition;
	}
}