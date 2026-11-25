package multitask.view;

import com.github.forax.zen.ApplicationContext;

import multitask.model.Model;
import multitask.model.Slot;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.Objects;

/**
 * Vue générale de la partie : dessine l'arrière-plan, le temps écoulé et une
 * grille contenant les vues de chaque mini-jeu.
 */
public final class Fin implements View {

  /** Couleur de fond de la fenêtre. */
  public static final Color BACKGROUND = new Color(24, 26, 32);

  /**
   * Contexte graphique qui fournit la taille d'écran et réalise le rendu des
   * images.
   */
  private final ApplicationContext context;
  /** Largeur de l'écran relevée à la construction, en pixels. */
  private final int width;
  /** Hauteur de l'écran relevée à la construction, en pixels. */
  private final int height;

  /**
   * Crée la vue et mémorise les dimensions de l'écran courant.
   *
   * @param context contexte graphique utilisé pour le rendu
   * @throws NullPointerException si {@code context} ou {@code viewers} est
   *                              {@code null}
   */
  public Fin(ApplicationContext context) {
    this.context = Objects.requireNonNull(context);
    var screen = context.getScreenInfo();
    width = screen.width();
    height = screen.height();
    super();
  }

  /**
   * Demande au contexte graphique de dessiner une image représentant l'état
   * actuel du modèle.
   *
   * @param model modèle contenant le temps de partie et les mini-jeux à afficher
   * @throws NullPointerException si {@code model} est {@code null}
   */
  public void render(Model model) {
    Objects.requireNonNull(model);
    context.renderFrame(graphics -> draw(graphics, model));
  }

  /**
   * Dessine le fond, l'affichage du temps puis la grille des mini-jeux. Les
   * cellules sont réparties sur un nombre de colonnes et de lignes visant une
   * grille approximativement carrée.
   *
   * @param graphics surface graphique de l'image en cours
   * @param model    modèle contenant l'état à dessiner
   */
  private void draw(Graphics2D graphics, Model model) {
    graphics.setColor(BACKGROUND);
    graphics.fillRect(0, 0, width, height);
    
    graphics.setColor(Color.RED);
    centerText(graphics, "Votre temps à été de :" + model.time() + " s", new Rectangle(width/2, height/2 - 20, 0, 0));
    centerText(graphics, "Presser M pour rejouer.", new Rectangle(width/2, height/2, 0, 0));
  }

  /**
   * Dessine un texte horizontalement centré dans un rectangle, à mi-hauteur de
   * celui-ci.
   *
   * @param graphics surface graphique utilisée pour le dessin
   * @param text     texte à afficher
   * @param bounds   rectangle dans lequel centrer le texte
   */
  private static void centerText(Graphics2D graphics, String text, Rectangle bounds) {
    var fm = graphics.getFontMetrics();
    var x = bounds.x + (bounds.width - fm.stringWidth(text)) / 2;
    var y = bounds.y + bounds.height / 2;
    graphics.drawString(text, x, y);
  }
}