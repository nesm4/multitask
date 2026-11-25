package multitask.controller;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.Objects;
import java.util.function.Consumer;

public record Button(String title, Color color, Rectangle rect, Consumer<Void> fonction) {
	public Button {
		Objects.requireNonNull(rect);
		Objects.requireNonNull(fonction);
	}
}
