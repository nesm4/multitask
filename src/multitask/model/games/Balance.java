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
 * atténue sa vitesse. Une fois la bille sortie de la planche, elle chute
 * verticalement et la partie est perdue lorsque la chute est suffisante.
 * </p>
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
  /** Inclinaison de la planche au début de la partie, en degrés. */
  private static final double INITIAL_TILT_DEGREES = 4.0;
  /** Hauteur de chute, en unités du modèle, à partir de laquelle la partie est perdue. */
  private static final double MAX_DROP = 30;

  /** Position longitudinale de la bille sur la piste, centrée en zéro. */
  private double position;
  /** Vitesse longitudinale de la bille. */
  private double speed;
  /** Inclinaison actuelle de la planche, en degrés. */
  private double tilt;
  /** Hauteur dont la bille est tombée depuis qu'elle a quitté la planche. */
  private double drop;
  /** Vitesse verticale de chute de la bille. */
  private double fallSpeed;

  /**
   * Crée un jeu avec la bille au centre, immobile, et une planche légèrement
   * inclinée.
   */
  public Balance() {
    position = 0;
    speed = 0;
    tilt = INITIAL_TILT_DEGREES;
    drop = 0;
    fallSpeed = 0;
    super();
  }

  /**
   * Met à jour les commandes d'inclinaison et la dynamique de la bille, puis
   * vérifie si elle est tombée assez bas pour que la partie soit perdue.
   *
   * @param dt         durée du pas de simulation, en secondes
   * @param input      état des entrées clavier
   * @param difficulty niveau de difficulté global
   * @return {@code true} tant que la bille n'est pas tombée trop bas ;
   *         {@code false} sinon
   * @throws IllegalArgumentException si {@code dt} ou {@code difficulty} est
   *                                  négatif
   * @throws NullPointerException     si {@code input} est {@code null}
   */
  @Override
  public boolean update(double dt, Input input, double difficulty) {
    Game.checkInput(dt, input, difficulty);

    readInput(dt, input, difficulty);
    if (isOffTrack()) {
      fall(dt);
    } else {
      roll(dt, difficulty);
    }
    position += speed * dt;
    return drop < MAX_DROP;
  }

  /**
   * Modifie l'inclinaison selon les touches gauche et droite maintenues, sans
   * dépasser les angles autorisés. En l'absence de ces touches, l'inclinaison
   * reste inchangée.
   *
   * @param dt         durée du pas de simulation, en secondes
   * @param input      état des entrées clavier
   * @param difficulty niveau de difficulté global
   */
  private void readInput(double dt, Input input, double difficulty) {
    var dir = (input.isKeyDown(Key.LEFT) ? -1 : 0) + (input.isKeyDown(Key.RIGHT) ? 1 : 0);
    if (dir != 0) {
      var currentTiltSpeed = TILT_SPEED_DEG_PER_SEC * (1 + difficulty / 5.0);
      tilt = Game.dontExceed(tilt + dir * currentTiltSpeed * dt, MAX_TILT_DEGREES);
    }
  }

  /**
   * Indique si la bille a dépassé une extrémité de la planche.
   *
   * @return {@code true} si la bille n'est plus soutenue par la planche
   */
  private boolean isOffTrack() {
    return Math.abs(position) > TRACK_HALF_LENGTH;
  }

  /**
   * Met à jour la vitesse de la bille lorsqu'elle roule sur la planche :
   * l'accélération dépend de l'inclinaison et la difficulté l'amplifie.
   *
   * @param dt         durée du pas de simulation, en secondes
   * @param difficulty niveau de difficulté global
   */
  private void roll(double dt, double difficulty) {
    var acceleration = GRAVITY * Math.sin(Math.toRadians(tilt)) * (1 + difficulty / 3.0);
    speed += acceleration * dt;
    speed *= FRICTION;
  }

  /**
   * Fait tomber la bille : elle n'est plus soutenue, donc sa vitesse verticale
   * augmente sous l'effet de la gravité, tandis que sa vitesse longitudinale
   * reste inchangée.
   *
   * @param dt durée du pas de simulation, en secondes
   */
  private void fall(double dt) {
    fallSpeed += GRAVITY * dt;
    drop += fallSpeed * dt;
  }

  /**
   * Retourne la position longitudinale courante de la bille.
   *
   * @return position de la bille ; sa valeur absolue dépasse la demi-longueur de
   *         la piste lorsque la bille est tombée
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

  /**
   * Retourne la hauteur dont la bille est tombée depuis la planche.
   *
   * @return hauteur de chute en unités du modèle, nulle tant que la bille est
   *         sur la planche
   */
  public double drop() {
    return drop;
  }
}