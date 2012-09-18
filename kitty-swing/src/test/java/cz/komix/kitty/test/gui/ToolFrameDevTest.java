package com.anthavio.kitty.test.gui;

import java.io.File;

import javax.swing.JFrame;

import com.anthavio.kitty.Kitty;
import com.anthavio.kitty.swing.ToolFrame;

/**
 * Interni developersky test pro ladeni GUI TestToolu
 * 
 * @author vanek
 */
public class ToolFrameDevTest {

	public static void main(String[] args) {
		Kitty kitty = Kitty.setup(args);
		ToolFrame gui = new ToolFrame();
		gui.init(kitty, new File("src/test/scenario"));
		//gui.pack();
		gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		gui.setVisible(true);

		kitty.startConsole();
	}

}
