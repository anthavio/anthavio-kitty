package com.anthavio.web;

import javax.inject.Inject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.anthavio.aspect.Logged;
import com.anthavio.tap.svc.TapService;

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
