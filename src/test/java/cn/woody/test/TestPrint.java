package cn.woody.test;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import cn.woody.test.server.IPrint;

public class TestPrint {
	@Test
	public void test() throws InterruptedException {
		 @SuppressWarnings("resource")
		ApplicationContext ctx = new ClassPathXmlApplicationContext(new String[]{ "applicationContext-test-rpc.xml" });
		 
		 Thread.sleep(2000);
		 System.out.println("------------------");
		 System.out.println("开始调用");
		 IPrint print = (IPrint) ctx.getBean("print");
		 
		 System.out.println(print.sayHi("我们的歌"));
		 System.out.println(print.sayHi("订阅"));
		 System.out.println(print.sayHi("在 XML 文档中引用 Schema"));
		 System.out.println(print.sayHi("代码解释"));
		 System.out.println(print.sayHi("下面的片断"));
		 System.out.println(print.sayHi("您就可以使用 schemaLocation 属性了。此属性有两个值。第一个值是需要使用的命名空间。第二个值是供命名空间使用的 XML schema 的位置"));
	}
}