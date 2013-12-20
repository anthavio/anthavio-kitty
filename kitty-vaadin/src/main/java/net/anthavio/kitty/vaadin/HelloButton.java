package net.anthavio.kitty.vaadin;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.ui.Button;

public class HelloButton extends Button {

	@Autowired
	private HelloService helloService;

	public HelloButton() {
		super("Click me!");

		//SpringInjector.inject(this);

		addListener(new ClickListener() {
			public void buttonClick(ClickEvent event) {
				String helloText = helloService.getHelloText();
				getApplication().getMainWindow().showNotification(helloText);
			}
		});
	}
}
