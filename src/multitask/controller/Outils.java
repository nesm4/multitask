package multitask.controller;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;

public class Outils {
	
	public static Rectangle CenterRect(int x, int y, int width, int height) {
		return new Rectangle(x - width/2, y - height/2, width, height);
	}

	public static void centerText(Graphics2D graphics, String text, int x, int y, int size) {
		graphics.setFont(new Font("Dialog", Font.BOLD, size));
		var fm = graphics.getFontMetrics();
		graphics.drawString(text, x - fm.stringWidth(text) / 2, y);
	}

}
