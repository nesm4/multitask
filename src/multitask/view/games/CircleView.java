package multitask.view.games;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Polygon;
import java.util.Objects;

import multitask.model.games.Circle;

/**
 * Vue graphique du mini-jeu {@link Avoid} : dessine les rangées, le joueur et
 * les missiles.
 */
public final class CircleView implements GameView {

  /** État du mini-jeu d'esquive lu pour produire le dessin. */
  private final Circle circler;
  
  private final Color BACKGROUND_COLOR = new Color(250, 241, 184);
  private final Color CIRCLE_COLOR = new Color(131, 120, 1);
  private final Color MISSILES_COLOR = new Color(0, 0, 0);
  
  /**
   * Crée une vue associée au mini-jeu fourni.
   *
   * @param avoider mini-jeu dont l'état doit être représenté
   * @throws NullPointerException si {@code avoider} est {@code null}
   */
  public CircleView(Circle circler) {
    Objects.requireNonNull(circler);
    this.circler = circler;
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
		GameView.checkInput(graphics, circler, area);
		var TravelDistance = Math.min(area.width, area.height) / 2;
		var centerX = area.x + area.width / 2;
		var centerY = area.y + area.height / 2;
		var width = 2 * TravelDistance / 5;
		
		graphics.setStroke(new BasicStroke(10));
		
		graphics.setColor(CIRCLE_COLOR);
		graphics.drawOval(centerX - width / 2, centerY - width / 2, width, width);
		
		var angleOuverture = circler.angleOuverture();
		var debutAngle = (int) Math.round(circler.angle() - angleOuverture / 2.0);
		
		graphics.setStroke(new BasicStroke(15));
		
		graphics.setColor(BACKGROUND_COLOR);
		graphics.drawArc(centerX - width / 2, centerY - width / 2, width - 2, width - 2, debutAngle, angleOuverture);
		graphics.drawArc(centerX - width / 2, centerY - width / 2, width + 2, width + 2, debutAngle, angleOuverture);
		
		graphics.setStroke(new BasicStroke(3));
		
		graphics.setColor(MISSILES_COLOR);
		for (Circle.Missile m : circler.missiles()) {
			var angle = Math.toRadians(- m.angle());
			var avancement = (1 - m.x() / 100);
			var coteTriangle = 10.0;
			var baseHalf = coteTriangle/2;
			var hauteur = Math.sqrt(coteTriangle * coteTriangle - baseHalf * baseHalf);
			
			int mx1 = (int) (centerX + avancement * TravelDistance * Math.cos(angle));
			int my1 = (int) (centerY + avancement * TravelDistance * Math.sin(angle));
				
			double baseCenterX = mx1 + hauteur * Math.cos(angle);
			double baseCenterY = my1 + hauteur * Math.sin(angle);
			
			double perpAngle = angle + Math.PI / 2.0;

			int mx2 = (int) Math.round(baseCenterX + baseHalf * Math.cos(perpAngle));
			int my2 = (int) Math.round(baseCenterY + baseHalf * Math.sin(perpAngle));

			int mx3 = (int) Math.round(baseCenterX - baseHalf * Math.cos(perpAngle));
			int my3 = (int) Math.round(baseCenterY - baseHalf * Math.sin(perpAngle));

			int[] xPoints = { mx1, mx2, mx3 };

			int[] yPoints = { my1, my2, my3 };

			Polygon triangle = new Polygon(xPoints, yPoints, 3);
			graphics.fill(triangle);
		}
	}
}