/**
 * 
 */
package net.anthavio.tap.test;

import net.anthavio.kitty.Kitty;
import net.anthavio.kitty.KittySpringXmlNamespaceHandler;

import org.springframework.context.support.ClassPathXmlApplicationContext;


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
