package multitask.controller;

import java.awt.Rectangle;

public class Outils {
	
	public static Rectangle CenterRect(int x, int y, int width, int height) {
		return new Rectangle(x - width/2, y - height/2, width, height);
	}
	
}
