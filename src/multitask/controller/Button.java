package multitask.controller;

import java.awt.Rectangle;
import java.util.function.Consumer;

public record Button(Rectangle rect, Consumer<Void> fonction) {
	
}
