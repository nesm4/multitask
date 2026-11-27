package multitask.model.games;

import com.github.forax.zen.KeyboardEvent.Key;

import multitask.controller.Input;
import multitask.model.Game;

/**
 * Mini-jeu d'équilibre : le joueur incline une planche et doit empêcher une
 * bille de tomber de ses extrémités.
 *
 * <p>
 * Les flèches gauche et droite règlent l'inclinaison. La gravité fait accélérer
 * la bille dans le sens de l'inclinaison, tandis qu'un facteur de friction
 * atténue sa vitesse. La bille a un poids : plus elle s'éloigne du centre, plus
 * elle fait pencher la planche de son côté. La partie est perdue dès que la
 * bille quitte la planche.
 * </p>
 */
public final class Balance implements Game {

  /** Demi-longueur de la piste, en unités du modèle. */
  public static final double TRACK_HALF_LENGTH = 30;
  /** Valeur absolue maximale de l'inclinaison de la planche, en degrés. */
  public static final double MAX_TILT_DEGREES = 45;
  /** Valeur absolue maximale de l'accélération de la bille, en unités par seconde au carré. */
  private static final double MAX_ACCELERATION = 15;
  /** Vitesse de variation de l'inclinaison commandée par le joueur, en degrés par seconde. */
  private static final double TILT_SPEED_DEG_PER_SEC = 75;
  /**
   * Vitesse de variation de l'inclinaison due au poids de la bille lorsqu'elle
   * est à une extrémité de la planche, en degrés par seconde.
   */
  private static final double BALL_TORQUE_DEG_PER_SEC = 5;
  /** Accélération gravitationnelle de base utilisée par la simulation. */
  private static final double GRAVITY = 45;
  /** Coefficient de réduction de la vitesse appliqué à chaque mise à jour. */
  private static final double FRICTION = 0.98;
  /** Inclinaison de la planche au début de la partie, en degrés. */
  private static final double INITIAL_TILT_DEGREES = 2.0;

  /** Position longitudinale de la bille sur la piste, centrée en zéro. */
  private double position;
  /** Vitesse longitudinale de la bille. */
  private double speed;
  /** Inclinaison actuelle de la planche, en degrés. */
  private double tilt;

  /**
   * Crée un jeu avec la bille au centre, immobile, et une planche légèrement
   * inclinée.
   */
  public Balance() {
    position = 0;
    speed = 0;
    tilt = INITIAL_TILT_DEGREES;
    super();
  }

  /**
   * Met à jour l'inclinaison de la planche (commandes du joueur et poids de la
   * bille), la dynamique de la bille, puis vérifie qu'elle est toujours sur la
   * planche.
   *
   * @param dt         durée du pas de simulation, en secondes
   * @param input      état des entrées clavier
   * @param difficulty niveau de difficulté global
   * @return {@code true} tant que la bille est sur la planche ; {@code false}
   *         dès qu'elle la quitte
   * @throws IllegalArgumentException si {@code dt} ou {@code difficulty} est
   *                                  négatif
   * @throws NullPointerException     si {@code input} est {@code null}
   */
  @Override
  public boolean update(double dt, Input input, double difficulty) {
    Game.checkInput(dt, input, difficulty);

    readInput(dt, input, difficulty);
    applyBallWeight(dt, difficulty);
    roll(dt, difficulty);
    return !isOffTrack();
  }

  /**
   * Modifie l'inclinaison selon les touches gauche et droite maintenues. La
   * vitesse de rotation commandée augmente avec la difficulté. En l'absence de
   * ces touches, le joueur n'agit pas sur l'inclinaison.
   *
   * @param dt         durée du pas de simulation, en secondes
   * @param input      état des entrées clavier
   * @param difficulty niveau de difficulté global
   */
  private void readInput(double dt, Input input, double difficulty) {
    var dir = (input.isKeyDown(Key.LEFT) ? -1 : 0) + (input.isKeyDown(Key.RIGHT) ? 1 : 0);
    var currentTiltSpeed = TILT_SPEED_DEG_PER_SEC * (1 + difficulty / 10.0);
    rotate(dir * currentTiltSpeed * dt);
  }

  /**
   * Fait pencher la planche du côté de la bille : l'effet est proportionnel à
   * l'éloignement de la bille par rapport au centre, et la difficulté
   * l'amplifie.
   *
   * @param dt         durée du pas de simulation, en secondes
   * @param difficulty niveau de difficulté global
   */
  private void applyBallWeight(double dt, double difficulty) {
    var lever = position / TRACK_HALF_LENGTH;
    rotate(BALL_TORQUE_DEG_PER_SEC * lever * (1 + difficulty / 10.0) * dt);
  }

  /**
   * Fait tourner la planche d'un angle donné, sans dépasser l'inclinaison
   * maximale autorisée.
   *
   * @param degrees variation d'inclinaison, en degrés (positive vers la droite)
   */
  private void rotate(double degrees) {
    tilt = Game.dontExceed(tilt + degrees, MAX_TILT_DEGREES);
  }

  /**
   * Fait rouler la bille sur la planche : l'accélération dépend de
   * l'inclinaison et la difficulté l'amplifie, sans dépasser l'accélération
   * maximale autorisée, puis la vitesse et la position sont mises à jour.
   *
   * @param dt         durée du pas de simulation, en secondes
   * @param difficulty niveau de difficulté global
   */
  private void roll(double dt, double difficulty) {
    var acceleration = GRAVITY * Math.sin(Math.toRadians(tilt)) * (1 + difficulty / 10.0);
    speed += Game.dontExceed(acceleration, MAX_ACCELERATION) * dt;
    speed *= FRICTION;
    position += speed * dt;
  }

  /**
   * Indique si la bille a dépassé une extrémité de la planche.
   *
   * @return {@code true} si la bille n'est plus sur la planche
   */
  private boolean isOffTrack() {
    return Math.abs(position) > TRACK_HALF_LENGTH;
  }

  /**
   * Retourne la position longitudinale courante de la bille.
   *
   * @return position de la bille ; sa valeur absolue dépasse la demi-longueur de
   *         la piste lorsque la partie est perdue
   */
  public double position() {
    return position;
  }

  /**
   * Retourne l'inclinaison courante de la planche.
   *
   * @return inclinaison en degrés
   */
  public double tilt() {
    return tilt;
  }
}