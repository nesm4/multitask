package multitask.view.games;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.Objects;

import multitask.model.games.Balance;

public final class BalanceView implements GameView {

  private final Balance balance;

  public BalanceView(Balance balance) {
    Objects.requireNonNull(balance);
    this.balance = balance;
    super();
  }

  @Override
  public void render(Graphics2D graphics, Rectangle area) {
    GameView.checkInput(graphics, balance, area);

    var centerX = area.x + area.width / 2;
    var centerY = area.y + area.height / 2 + 30;

    var plankWidth = (int) (area.width * 0.55);
    var plankHeight = 14;
    var ballDiameter = 24;

    var oldTransform = graphics.getTransform();

    var tiltRad = Math.toRadians(balance.tilt());
    graphics.rotate(tiltRad, centerX, centerY);

    // 1. Dessin de la planche noire/rose
    graphics.setColor(new Color(229, 158, 172));
    graphics.fillRect(centerX - plankWidth / 2, centerY - plankHeight / 2, plankWidth, plankHeight);

    // 2. Calcul du décalage horizontal (X)
    var halfTrack = Balance.TRACK_HALF_LENGTH;
    var currentPos = balance.position();
    var normPos = currentPos / halfTrack;
    var ballOffset = (int) (normPos * (plankWidth / 2.0 - ballDiameter / 2.0));

    var ballX = centerX + ballOffset - ballDiameter / 2;
    
    // 3. Calcul de la position verticale (Y) avec effet de gravité
    var baseBallY = centerY - (plankHeight / 2) - ballDiameter;
    var fallOffsetY = 0;

    // Si la bille a dépassé le bord (à droite ou à gauche)
    if (Math.abs(currentPos) > halfTrack) {
      double overflow = Math.abs(currentPos) - halfTrack;
      // Chute quadratique (accélération gravitationnelle vers le bas)
      fallOffsetY = (int) (0.8 * overflow * overflow); 
    }

    var ballY = baseBallY + fallOffsetY;

    // 4. Dessin de la bille
    graphics.setColor(Color.WHITE);
    graphics.fillOval(ballX, ballY, ballDiameter, ballDiameter);

    graphics.setTransform(oldTransform);
  }
}