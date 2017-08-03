package cn.woody.test;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import cn.woody.test.server.IPrint;

public class TestPrint {
	@Test
	public void test() throws InterruptedException {
		 ApplicationContext ctx = new ClassPathXmlApplicationContext(new String[]{ "applicationContext-test-rpc.xml" });
		 
		 Thread.sleep(2000);
		 System.out.println("------------------");
		 System.out.println("开始调用");
		 IPrint print = (IPrint) ctx.getBean("print");
		 
		 System.out.println(print.sayHi("我们的歌"));
		 System.out.println(print.sayHi("订阅"));
		 
		 try {
			 Thread.currentThread().join();
		 } catch (Exception e) {
			 e.printStackTrace();
		 }
	}
}