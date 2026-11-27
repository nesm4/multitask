package multitask.view.games;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.Objects;

import multitask.model.games.Touch;
import multitask.controller.Outils;

/**
 * Vue graphique du mini-jeu {@link Avoid} : dessine les rangées, le joueur et
 * les missiles.
 */
public final class TouchView implements GameView {

  /** État du mini-jeu d'esquive lu pour produire le dessin. */
  private final Touch toucher;
  
  private final Color BACKGROUND_COLOR = new Color(195, 238, 185);
  private final Color PLAYER_COLOR = new Color(51, 128, 32);
  private final Color ZONES_COLOR = new Color(0, 0, 0);
  private final Color TEXT_COLOR = new Color(154, 188, 146);
  
  
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
  
  public Color BackgroundColor() {
  	return BACKGROUND_COLOR;
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
		var width = Math.min(area.width, area.height);
		var areaX = area.x + (area.width - width)/2;
		var areaY = area.y + (area.height - width)/2;
		var PlayerWidth = width * 2 * toucher.PlayerRayon()/toucher.width();
		var PlayerHeight = width * 2 * toucher.PlayerRayon()/toucher.height();
		var PlayerX = areaX + width * toucher.PlayerX()/toucher.width() - PlayerWidth/2;
		var PlayerY = areaY + width * toucher.PlayerY()/toucher.height() - PlayerHeight/2;
		
		graphics.setColor(PLAYER_COLOR);
		graphics.fillOval((int)PlayerX, (int)PlayerY, PlayerWidth, PlayerHeight);
	
		graphics.setStroke(new BasicStroke(5));
		
		
		for (Touch.Zone m : toucher.zones()) {
			
			var ZoneWidth = width * 2 * (int) m.cercle().rayon()/toucher.width();
			var ZoneHeight = width * 2 * (int) m.cercle().rayon()/toucher.height();
			var ZoneX = areaX + width * (int) m.cercle().x()/toucher.width() - ZoneWidth/2;
			var ZoneY = areaY + width * (int) m.cercle().y()/toucher.height() - ZoneHeight/2;
			
			graphics.setStroke(new BasicStroke(7));
			graphics.setColor(ZONES_COLOR);
			graphics.drawOval(ZoneX, ZoneY, ZoneWidth, ZoneHeight);
			
			graphics.setColor(TEXT_COLOR);
			Outils.centerText(graphics, "" + (int)m.time(), ZoneX + ZoneWidth/2, ZoneY + 17 * ZoneHeight/30, 20);
			
			
		}
	}
}