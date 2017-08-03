package cn.woody.test.server;

public class Print implements IPrint {

	public String sayHi(String str) {
		return "收到消息了，你是说 [" + str + "] 吗？" + System.currentTimeMillis();
	}
}