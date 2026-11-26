package multitask.view.games;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.Objects;

import multitask.model.games.Touch;

/**
 * Vue graphique du mini-jeu {@link Avoid} : dessine les rangées, le joueur et
 * les missiles.
 */
public final class TouchView implements GameView {

  /** État du mini-jeu d'esquive lu pour produire le dessin. */
  private final Touch toucher;

  /**
   * Crée une vue associée au mini-jeu fourni.
   *
   * @param avoider mini-jeu dont l'état doit être représenté
   * @throws NullPointerException si {@code avoider} est {@code null}
   */
  public TouchView(Touch toucher) {
    Objects.requireNonNull(toucher);
    this.toucher = toucher;
    super();
  }

  /**
   * Dessine le bord droit de l'aire de jeu, la position du joueur et tous les
   * missiles présents. Les coordonnées du modèle sont mises à l'échelle pour
   * tenir dans le rectangle fourni.
   *
   * @param graphics contexte graphique à utiliser
   * @param area     zone disponible pour le dessin
   * @throws NullPointerException si {@code graphics}, le jeu associé ou
   *                              {@code area} est nul
   */
	@Override
	public void render(Graphics2D graphics, Rectangle area) {
		GameView.checkInput(graphics, toucher, area);
		var PlayerWidth = area.width * 2 * toucher.PlayerRayon()/toucher.width();
		var PlayerHeight = area.height * 2 * toucher.PlayerRayon()/toucher.height();
		var PlayerX = area.x + area.width * toucher.PlayerX()/toucher.width() - PlayerWidth/2;
		var PlayerY = area.y + area.height * toucher.PlayerY()/toucher.height() - PlayerHeight/2;
		
		graphics.setColor(Color.GREEN);
		graphics.fillOval(PlayerX, PlayerY, PlayerWidth, PlayerHeight);
	
		graphics.setStroke(new BasicStroke(3));
		
		graphics.setColor(new Color(230, 90, 90));
		for (Touch.Zone m : toucher.zones()) {
			
			var ZoneWidth = area.width * 2 * (int) m.cercle().rayon()/toucher.width();
			var ZoneHeight = area.height * 2 * (int) m.cercle().rayon()/toucher.height();
			var ZoneX = area.x + area.width * (int) m.cercle().x()/toucher.width() - ZoneWidth/2;
			var ZoneY = area.y + area.height * (int) m.cercle().y()/toucher.height() - ZoneHeight/2;
			
			graphics.setColor(Color.ORANGE);
			graphics.drawString("" + (int)m.time(),ZoneX + ZoneWidth/2, ZoneY + ZoneHeight/2);
			graphics.fillOval(ZoneX, ZoneY, ZoneWidth, ZoneHeight);
			
		}
	}
}