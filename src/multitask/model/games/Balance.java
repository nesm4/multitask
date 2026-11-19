package multitask.model.games;

import com.github.forax.zen.KeyboardEvent.Key;

import multitask.controller.Input;
import multitask.model.Game;

/**
 * Mini-jeu d'équilibre : le joueur incline une planche et doit empêcher une
 * bille de sortir de ses limites.
 */
public final class Balance implements Game {

  /** Demi-longueur de la piste, en unités du modèle. */
  public static final double TRACK_HALF_LENGTH = 30;
  /** Valeur absolue maximale de l'inclinaison de la planche, en degrés. */
  public static final double MAX_TILT_DEGREES = 45;

  /** Vitesse de variation de l'inclinaison, en degrés par seconde. */
  private static final double TILT_SPEED_DEG_PER_SEC = 90;
  /** Accélération gravitationnelle de base utilisée par la simulation. */
  private static final double GRAVITY = 45;
  /** Coefficient de réduction de la vitesse appliqué à chaque mise à jour. */
  private static final double FRICTION = 0.995;

  /** Inclinaison initiale fixe pour forcer le joueur à réagir dès le début. */
  private static final double INITIAL_TILT_DEGREES = 2.0;
  /** Marge autorisée hors de la planche pour voir la bille tomber un peu. */
  private static final double DROP_MARGIN = 18.0;

  /** Position longitudinale de la bille sur la piste, centrée en zéro. */
  private double position;
  /** Vitesse longitudinale de la bille. */
  private double speed;
  /** Inclinaison actuelle de la planche, en degrés. */
  private double tilt;

  /**
   * Crée un jeu avec la bille au centre, immobile, et une planche légèrement inclinée.
   */
  public Balance() {
    position = 0;
    speed = 0;
    tilt = INITIAL_TILT_DEGREES;
    super();
  }

  @Override
  public boolean update(double dt, Input input, double difficulty) {
    Game.checkInput(dt, input, difficulty);

    readInput(dt, input, difficulty);
    positionAndSpeed(dt, difficulty);
    return checkBorder();
  }

  /**
   * Modifie l'inclinaison selon les touches gauche et droite maintenues.
   * La flèche gauche (LEFT) fait pencher à gauche (diminue tilt), 
   * la flèche droite (RIGHT) fait pencher à droite (augmente tilt).
   */
  private void readInput(double dt, Input input, double difficulty) {
    var dir = (input.isKeyDown(Key.LEFT) ? -1 : 0) + (input.isKeyDown(Key.RIGHT) ? 1 : 0);
    if (dir != 0) {
      var currentTiltSpeed = TILT_SPEED_DEG_PER_SEC * (1 + difficulty / 5.0);
      tilt = Game.dontExceed(tilt + dir * currentTiltSpeed * dt, MAX_TILT_DEGREES);
    }
  }

  /**
   * Calcule l'accélération gravitationnelle liée à l'inclinaison et à la difficulté.
   */
  private void positionAndSpeed(double dt, double difficulty) {
    var acceleration = GRAVITY * Math.sin(Math.toRadians(tilt)) * (1 + difficulty / 3.0);
    speed += acceleration * dt;
    speed *= FRICTION;
    position += speed * dt;
  }

  /**
   * Limite la position de la bille en incluant la marge de chute et détermine si elle a
   * dépassé la limite autorisée.
   */
  private boolean checkBorder() {
    var maxAllowed = TRACK_HALF_LENGTH + DROP_MARGIN;
    var old = position;
    
    // On limite la position maximale pour éviter que la bille parte à l'infini
    position = Game.dontExceed(position, maxAllowed);
    
    // On vérifie la valeur absolue : si le déplacement d'origine a dépassé la marge tolérée, on perd !
    return Math.abs(old) <= maxAllowed;
  }

  public double position() {
    return position;
  }

  public double tilt() {
    return tilt;
  }
}