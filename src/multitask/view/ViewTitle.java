package multitask.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.List;
import java.util.Objects;

import com.github.forax.zen.ApplicationContext;

import multitask.controller.Button;
import multitask.controller.Outils;
import multitask.model.Model;

public class ViewTitle implements View {
	
	/** Couleur de fond de la fenêtre. */
	public static final Color BACKGROUND = new Color(245, 238, 223);
	
	public static final Color TEXT_COLOR = new Color(0, 0, 0);
  
	/**
	 * Contexte graphique qui fournit la taille d'écran et réalise le rendu des
	 * images.
	 */
	private final ApplicationContext context;
	/** Largeur de l'écran relevée à la construction, en pixels. */
	private final int width;
	/** Hauteur de l'écran relevée à la construction, en pixels. */
	private final int height;
	
	private List<Button> buttons;
	/**
	   * Crée la vue et mémorise les dimensions de l'écran courant.
	   *
	   * @param context contexte graphique utilisé pour le rendu
	   * @throws NullPointerException si {@code context} ou {@code viewers} est
	   *                              {@code null}
	   */
	  public ViewTitle(ApplicationContext context, List<Button> buttons) {
	  	this.buttons = buttons;
	    this.context = Objects.requireNonNull(context);
	    var screen = context.getScreenInfo();
	    width = screen.width();
	    height = screen.height();
	  }

	/**
	 * Demande au contexte graphique de dessiner une image représentant l'état
	 * actuel du modèle.
	 *
	 * @param model modèle contenant le temps de partie et les mini-jeux à afficher
	 * @throws NullPointerException si {@code model} est {@code null}
	 */
	public void render(Model model) {
		context.renderFrame(graphics -> draw(graphics));
	}

	/**
	 * Dessine le fond, l'affichage du temps puis la grille des mini-jeux. Les
	 * cellules sont réparties sur un nombre de colonnes et de lignes visant une
	 * grille approximativement carrée.
	 *
	 * @param graphics surface graphique de l'image en cours
	 * @param model    modèle contenant l'état à dessiner
	 */
	private void draw(Graphics2D graphics) {
		graphics.setColor(BACKGROUND);
		graphics.fillRect(0, 0, width, height);
		
		for (var button : buttons) {
			graphics.setColor(button.color());
			graphics.fillRoundRect(button.rect().x, button.rect().y, button.rect().width, button.rect().height, 100, 100);
			
			graphics.setColor(TEXT_COLOR);
			graphics.setStroke(new BasicStroke(3));
			graphics.drawRoundRect(button.rect().x, button.rect().y, button.rect().width, button.rect().height, 100, 100);
			
			var milieuButtonX = button.rect().x + button.rect().width / 2;
			var milieuButtonY = button.rect().y + button.rect().height / 2;
			Outils.centerText(graphics, button.title(), milieuButtonX, milieuButtonY, 20);
		}
	}

	/**
	 * Dessine un texte horizontalement centré dans un rectangle, à mi-hauteur de
	 * celui-ci.
	 *
	 * @param graphics surface graphique utilisée pour le dessin
	 * @param text     texte à afficher
	 * @param bounds   rectangle dans lequel centrer le texte
	 */

}
