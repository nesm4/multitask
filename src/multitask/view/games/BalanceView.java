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
    graphics.setColor(new Color(229, 158, 172));
    graphics.fillRect(centerX - plankWidth / 2, centerY - plankHeight / 2, plankWidth, plankHeight);

    var halfTrack = Balance.TRACK_HALF_LENGTH;
    var currentPos = balance.position();
    var normPos = currentPos / halfTrack;
    var ballOffset = (int) (normPos * (plankWidth / 2.0 - ballDiameter / 2.0));

    double localX = centerX + ballOffset - ballDiameter / 2.0;
    double localY = centerY - (plankHeight / 2.0) - ballDiameter;

    if (Math.abs(currentPos) > halfTrack) {
      graphics.setTransform(oldTransform);

      double relX = localX + ballDiameter / 2.0 - centerX;
      double relY = localY + ballDiameter / 2.0 - centerY;

      double worldX = centerX + relX * Math.cos(tiltRad) - relY * Math.sin(tiltRad);
      double worldY = centerY + relX * Math.sin(tiltRad) + relY * Math.cos(tiltRad);

      double overflow = Math.abs(currentPos) - halfTrack;
      // Réduction de l'accélération de chute (0.3 au lieu de 1.2 ou 1.5)
      double fallGravity = 0.3 * overflow * overflow;

      var finalBallX = (int) (worldX - ballDiameter / 2.0);
      var finalBallY = (int) (worldY - ballDiameter / 2.0 + fallGravity);

      graphics.setColor(Color.WHITE);
      graphics.fillOval(finalBallX, finalBallY, ballDiameter, ballDiameter);
    } else {
      graphics.setColor(Color.WHITE);
      graphics.fillOval((int) localX, (int) localY, ballDiameter, ballDiameter);
      graphics.setTransform(oldTransform);
    }
  }
}