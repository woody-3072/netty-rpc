package cn.woody.test;

import java.util.Random;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import cn.woody.test.server.IAdd;

public class TestAdd {
	@Test
	public void test() throws InterruptedException {
		 @SuppressWarnings("resource")
		ApplicationContext ctx = new ClassPathXmlApplicationContext(new String[]{ "applicationContext-test-rpc.xml" });
		 
		 Thread.sleep(2000);
		 System.out.println("------------------");
		 System.out.println("开始调用");
		 IAdd add = (IAdd) ctx.getBean("add");
		 System.out.println(add.add(1, 2));
		 System.out.println(add.add(3, 4));
		 System.out.println(add.add(21, 6));
		 System.out.println(add.add(31, 8));
		 System.out.println(add.add(41, 10));
		 System.out.println(add.add(51, 12));
		 System.out.println(add.add(61, 21));
		 System.out.println(add.add(71, 22));
		 System.out.println(add.add(81, 23));
		 
		 
		 Random random = new Random();
		 
		 long start = System.currentTimeMillis();
		 for (int i = 0; i < 100000; i++) {				//100000 50868  50308
			 System.out.println(add.add(random.nextInt(100), random.nextInt(1000)));
		 }
		 long end = System.currentTimeMillis();
		 
		 System.out.println("用时 ： " + (end - start));
		 
		 try {
			 Thread.currentThread().join();
		 } catch (Exception e) {
			 e.printStackTrace();
		 }
	}
}