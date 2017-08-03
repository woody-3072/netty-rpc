package cn.woody.spring.xml;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

import com.google.common.reflect.Reflection;

import cn.woody.rpc.RpcContainer;
import cn.woody.rpc.Reference.ReferenceProxyInvocationHandler;
import cn.woody.utils.EnumBeanType;

/**
 * 消费者bean工厂
 * 
 * Title: ReferenceBean<br> 
 * Description: ReferenceBean<br> 
 * CreateDate:2017年8月1日 下午3:10:22 
 * @author woody
 */
public class ReferenceBean implements InitializingBean, FactoryBean<Object> {
	private String interfaceName;			// 接口名称
	private int timeout;					// 回调超时时间
	
	public String getInterfaceName() {
		return interfaceName;
	}
	public void setInterfaceName(String interfaceName) {
		this.interfaceName = interfaceName;
	}
	public int getTimeout() {
		return timeout;
	}
	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

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

	// 初始化数据
	public void afterPropertiesSet() throws Exception {
		RpcContainer.instance().initData(EnumBeanType.REFERENCE, interfaceName);
	}
}