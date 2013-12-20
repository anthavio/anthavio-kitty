package net.anthavio.web;

import javax.inject.Inject;

import net.anthavio.aspect.Logged;
import net.anthavio.tap.svc.TapService;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;


/**
 * @author vanek
 *
 */
@Controller
@Logged
public class ExampleController {

	@Inject
	private TapService service;

	//@RequestMapping("/denied")
	//public void denied(HttpServletRequest request) {

	//}

	@RequestMapping("/welcome")
	public void welcome() {

	}

	@RequestMapping("/login")
	public void login() {

	}

	@RequestMapping("/logout")
	public void logout() {

	}

	@RequestMapping("/user")
	public void user() {

	}

	@RequestMapping("/home")
	public void home() {

	}

}
