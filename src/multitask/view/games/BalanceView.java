package multitask.view.games;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.Objects;

import multitask.model.games.Balance;

/**
 * Vue graphique du mini-jeu {@link Balance} : dessine la planche inclinée et la
 * bille.
 */
public final class BalanceView implements GameView {

  /** Hauteur de la planche, en pixels. */
  private static final int PLANK_HEIGHT = 14;
  /** Diamètre de la bille, en pixels. */
  private static final int BALL_DIAMETER = 24;

  /** État de jeu lu pour produire le dessin. */
  private final Balance balance;
  
  private final Color BACKGROUND_COLOR = new Color(254, 188, 188);
  private final Color BALL_COLOR = new Color(174, 2, 2);
  private final Color PLANK_COLOR = new Color(0, 0, 0);
  
  /**
   * Crée une vue associée au mini-jeu fourni.
   *
   * @param balance mini-jeu dont l'état doit être représenté
   * @throws NullPointerException si {@code balance} est {@code null}
   */
  public BalanceView(Balance balance) {
    Objects.requireNonNull(balance);
    this.balance = balance;
    super();
  }
  
  public Color BackgroundColor() {
  	return BACKGROUND_COLOR;
  }
  
  /**
   * Dessine la planche à son angle courant et positionne la bille
   * proportionnellement à sa position sur la piste.
   *
   * @param graphics contexte graphique à utiliser
   * @param area     zone disponible pour le dessin
   * @throws NullPointerException si {@code graphics}, le jeu associé ou
   *                              {@code area} est nul
   */
  @Override
  public void render(Graphics2D graphics, Rectangle area) {
    GameView.checkInput(graphics, balance, area);

    var centerX = area.x + area.width / 2;
    var centerY = area.y + area.height / 2 + 30;
    var plankWidth = (int) (area.width * 0.55);

    drawPlank(graphics, centerX, centerY, plankWidth);
    drawBall(graphics, centerX, centerY, plankWidth);
  }

  /**
   * Dessine la planche, tournée autour de son centre selon l'inclinaison. Le
   * repère d'origine est rétabli à la fin, pour que la bille ne soit pas
   * dessinée dans le repère incliné.
   *
   * @param graphics   contexte graphique à utiliser
   * @param centerX    abscisse du centre de la planche
   * @param centerY    ordonnée du centre de la planche
   * @param plankWidth longueur de la planche, en pixels
   */
  private void drawPlank(Graphics2D graphics, int centerX, int centerY, int plankWidth) {
    var oldTransform = graphics.getTransform();
    graphics.rotate(Math.toRadians(balance.tilt()), centerX, centerY);
    graphics.setColor(PLANK_COLOR);
    graphics.fillRect(centerX - plankWidth / 2, centerY - PLANK_HEIGHT / 2, plankWidth, PLANK_HEIGHT);
    graphics.setTransform(oldTransform);
  }

  
  /**
   * Dessine la bille. Sa position le long de la planche est projetée sur
   * l'écran grâce à l'inclinaison, de sorte qu'elle reste posée sur la planche.
   *
   * @param graphics   contexte graphique à utiliser
   * @param centerX    abscisse du centre de la planche
   * @param centerY    ordonnée du centre de la planche
   * @param plankWidth longueur de la planche, en pixels
   */
  private void drawBall(Graphics2D graphics, int centerX, int centerY, int plankWidth) {
    var pixelsPerUnit = (plankWidth / 2.0) / Balance.TRACK_HALF_LENGTH;
    var along = balance.position() * pixelsPerUnit;
    var above = PLANK_HEIGHT / 2.0 + BALL_DIAMETER / 2.0;
    var tiltRad = Math.toRadians(balance.tilt());

    var x = centerX + along * Math.cos(tiltRad) + above * Math.sin(tiltRad);
    var y = centerY + along * Math.sin(tiltRad) - above * Math.cos(tiltRad);

    graphics.setColor(BALL_COLOR);
    graphics.fillOval((int) (x - BALL_DIAMETER / 2.0), (int) (y - BALL_DIAMETER / 2.0), BALL_DIAMETER,
        BALL_DIAMETER);
  }
}