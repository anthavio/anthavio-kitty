package com.anthavio.kitty.vaadin;

import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.Application;
import com.vaadin.ui.Window;

@Configurable
public class HelloApplication extends Application {

	private Window window;

	@Override
	public void init() {
		window = new Window("Window Title");
		setMainWindow(window);
		//window.addComponent(new HelloButton());
		window.addComponent(new WindowOpener("Window Opener", window));
	}
}
