package net.anthavio.kitty.vaadin;

import org.springframework.stereotype.Service;

@Service
public class HelloService {

	public String getHelloText() {
		return "Hello!";
	}

}
