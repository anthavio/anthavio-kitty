/**
 * 
 */
package com.anthavio.tap.test;

import javax.inject.Inject;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import com.anthavio.spring.test.ContextRefLoader;
import com.anthavio.tap.svc.TapService;

/**
 * @author vanek
 *
 */
@ContextConfiguration(locations = "tap-services", loader = ContextRefLoader.class)
public class TapServiceTest extends AbstractTestNGSpringContextTests {

	@Inject
	private TapService exampleService;

	@Test
	public void test() {
		String configValue = exampleService.getConfigValue();
		System.out.println(configValue);
	}
}
