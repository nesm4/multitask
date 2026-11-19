package multitask.view.games;

import java.awt.BasicStroke;
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

  /** État de jeu lu pour produire le dessin. */
  private final Balance balance;

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
    var centerY = area.y + area.height / 2 + 5;
    
    var plankWidth = (int) (area.width * 0.55);
    var plankHeight = 14;
    var ballRadius = 16;
    
    var oldTransform = graphics.getTransform();

    var tiltRad = Math.toRadians(balance.tilt());
    graphics.rotate(tiltRad, centerX, centerY);
    
    graphics.setColor(new Color(229, 158, 172));
    graphics.fillRect(centerX - plankWidth / 2, centerY - plankHeight, plankWidth, plankHeight);
    
    var halfTrack = Balance.TRACK_HALF_LENGTH;
    var normPos = balance.position() / halfTrack; 
    var ballOffset = (int) (normPos * (plankWidth / 2.0 - ballRadius / 2.0));

    var ballX = centerX + ballOffset - ballRadius / 2;
    var ballY = centerY - plankHeight / 2 - ballRadius;

    graphics.setColor(Color.WHITE);
    graphics.fillOval(ballX - 3, ballY - 15, 18, 18);
    
    graphics.setTransform(oldTransform);
  }
}