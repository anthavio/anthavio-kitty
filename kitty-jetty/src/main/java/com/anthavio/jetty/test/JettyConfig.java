/**
 * 
 */
package com.anthavio.jetty.test;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.anthavio.jetty.test.Jetty6Manager.Cache;

/**
 * @author vanek
 *
 */
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface JettyConfig {

	String home() default ".";

	String[] configs() default { "etc/jetty.xml" };

	//cache jetty instance between tests
	Cache cache() default Cache.FOREVER;

	//zero means dynamic, negative means to just let jetty configuration itself to choose port 
	int port() default -1;

}
