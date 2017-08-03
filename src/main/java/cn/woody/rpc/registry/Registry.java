package cn.woody.rpc.registry;

import java.util.Set;

/**
 * 注册中心
 * 
 * Title: Registry<br> 
 * Description: Registry<br> 
 * CreateDate:2017年7月28日 上午10:27:19 
 * @author woody
 */
public interface Registry {

	/**
	 * 连接注册中心
	 * 
	 * Title: connect<br>
	 * Description: connect<br>
	 * CreateDate: 2017年7月28日 上午10:30:12<br>
	 * @category connect 
	 * @author woody
	 */
	void connect();
	
	/**
	 * 服务提供者发布
	 * 往通道中写入服务发布者信息
	 * 
	 * Title: service<br>
	 * Description: service<br>
	 * CreateDate: 2017年7月28日 上午11:42:36<br>
	 * @category service 
	 * @author woody 
	 * @param interfaces 待发布的服务
	 * @param port 服务端口
	 */
	void service(Set<String> interfaces, int port);

	/**
	 * 服务消费者发布
	 * 
	 * Title: reference<br>
	 * Description: reference<br>
	 * CreateDate: 2017年7月28日 上午11:42:24<br>
	 * @category reference 
	 * @author woody
	 * @param interfaces
	 */
	void reference(Set<String> interfaces);
	
	/**
	 * 销毁连接
	 * 
	 * Title: destroy<br>
	 * Description: destroy<br>
	 * CreateDate: 2017年7月31日 下午5:11:05<br>
	 * @category destroy 
	 * @author woody
	 */
	void destroy();
	
	/**
	 * 填充注册中心
	 * 
	 * Title: setInfo<br>
	 * Description: setInfo<br>
	 * CreateDate: 2017年8月3日 下午1:31:32<br>
	 * @category setInfo 
	 * @author woody
	 * @param address
	 */
	void setInfo(String address);
}