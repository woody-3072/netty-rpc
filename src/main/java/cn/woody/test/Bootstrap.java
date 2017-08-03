package cn.woody.test;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Bootstrap {
	@SuppressWarnings("resource")
	public static void main(String[] args) {
        new ClassPathXmlApplicationContext(new String[]{ "applicationContext-rpc.xml" });
        try {
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}