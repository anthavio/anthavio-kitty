/**
 * 
 */
package com.anthavio.tap.test;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.anthavio.kitty.Kitty;
import com.anthavio.kitty.KittySpringXmlNamespaceHandler;

/**
 * @author vanek
 *
 */
public class KittyXmlTest {

	public static void main(String[] args) {
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("spring/tap-kitty2.xml");
		Kitty kitty = ctx.getBean(Kitty.class);
		System.out.println(kitty);
		Object bean = ctx.getBean(KittySpringXmlNamespaceHandler.JDBC_TEMPLATE);
	}
}
