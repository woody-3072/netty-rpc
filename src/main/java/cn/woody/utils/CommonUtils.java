package cn.woody.utils;

/**
 * 通用工具类
 * 
 * Title: CommonUtils<br> 
 * Description: CommonUtils<br> 
 * CreateDate:2017年8月1日 上午11:31:08 
 * @author woody
 */
public class CommonUtils {
	public static boolean checkAddress(String address) {
		boolean flag = false;
		
		if (address.split(":").length == 2) {
			flag = true;
		}
		
		return flag;
	}
	
	/**
	 * 获取本地地址 用于发布服务  的ip地址
	 * 
	 * Title: getAddress<br>
	 * Description: getAddress<br>
	 * CreateDate: 2017年8月2日 下午12:55:18<br>
	 * @category getAddress 
	 * @author woody
	 * @param port
	 * @return
	 */
	public static String getAddress(int port) {
		return "127.0.0.1:" + port;
	}
}