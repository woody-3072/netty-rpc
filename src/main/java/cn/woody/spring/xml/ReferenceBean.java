package cn.woody.spring.xml;

import org.springframework.beans.factory.FactoryBean;

import com.google.common.reflect.Reflection;

import cn.woody.rpc.Reference.ReferenceProxyInvocationHandler;

/**
 * 消费者bean工厂
 * 
 * Title: ReferenceBean<br> 
 * Description: ReferenceBean<br> 
 * CreateDate:2017年8月1日 下午3:10:22 
 * @author woody
 */
public class ReferenceBean implements FactoryBean<Object> {
	private String interfaceName;			// 接口名称
	private int timeout;					// 回调超时时间
	
	// 生成代理对象 返回的结果 通过callback回调阻塞住
	public Object getObject() throws Exception {
		return Reflection.newProxy(getObjectType(), new ReferenceProxyInvocationHandler(timeout));
	}

	// 加载reference 的 class
	public Class<?> getObjectType() {
		try {
            return this.getClass().getClassLoader().loadClass(interfaceName);
        } catch (ClassNotFoundException e) {
            System.err.println("spring analyze fail!");
        }
        return null;
	}

	public boolean isSingleton() {
		return true;
	}
}