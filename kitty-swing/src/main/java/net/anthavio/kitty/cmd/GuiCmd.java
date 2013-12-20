package net.anthavio.kitty.cmd;

import java.util.Scanner;

import javax.swing.JFrame;

import net.anthavio.kitty.console.CmdInfo;
import net.anthavio.kitty.console.Command;
import net.anthavio.kitty.model.DirectoryItem;
import net.anthavio.kitty.swing.ToolFrame;


public class GuiCmd extends Command {

	@Override
	public void execute(DirectoryItem item, Scanner scanner) throws Exception {
		ToolFrame gui = new ToolFrame();
		gui.init(kitty, item.getFile());
		//gui.pack();
		gui.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		gui.setVisible(true);
		//JFrame do not block current thread
	}

	@Override
	public CmdInfo getInfo() {
		return new CmdInfo("g", "Show GUI");
	}

}
