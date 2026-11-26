package multitask.model.games;

import multitask.controller.Input;
import multitask.model.Game;

import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import com.github.forax.zen.KeyboardEvent.Key;

/**
 * Mini-jeu d'esquive : le joueur change de rangée pour éviter des missiles qui
 * traversent horizontalement la zone de jeu de gauche à droite.
 *
 * <p>
 * La cadence d'apparition et la vitesse des missiles augmentent avec la
 * difficulté. Une collision avec un missile arrivé au bord droit sur la rangée
 * du joueur met fin à la partie.
 * </p>
 */
public final class Touch implements Game {

  /** Abscisse du bord droit de l'aire de jeu dans les coordonnées du modèle. */
  /** Nombre de rangées disponibles pour le joueur et les missiles. */
  /** Intervalle initial entre deux apparitions de missiles, en secondes. */
  private static final double BASE_SPAWN_INTERVAL = 3.0;
  
  private static final double TEMPS_ZONE = 10.0;
  
  private static final int WIDTH = 200;
  private static final int HEIGHT = 100;
  private static final double SPEEDX = WIDTH/100.0;
  private static final double SPEEDY = HEIGHT/100.0;
  private static final int RAYON_ZONE = 5;
  private static final int RAYON_PLAYER = 3;
  
  
  public record Cercle(int x, int y, double rayon) {
  	
  	private boolean isInside(int x, int y) {
  		return (Math.sqrt((this.x - x)*(this.x - x) + (this.y - y)*(this.y - y)) <= rayon);
  	}
  	
		private boolean Intersects(Cercle c2) {
			int milieuX = (c2.x + x) / 2;
			int milieuY = (c2.y + y) / 2;
			return isInside(milieuX, milieuY);
		}
  }
  
  /**
   * Missile se déplaçant horizontalement sur une rangée fixe.
   *
   * @param x   position horizontale du missile dans les coordonnées du modèle
   * @param row indice de la rangée occupée par le missile
   */
  public record Zone(Cercle cercle, double time) {

    /**
     * Crée un missile déplacé horizontalement, en conservant sa rangée.
     *
     * @param dx déplacement horizontal à ajouter à la position courante
     * @return nouvelle valeur immuable représentant le missile déplacé
     */
    private Zone update(double dt) {
      return new Zone(cercle, time - dt);
    }
  }

  /** Rangée actuellement occupée par le joueur. */
  private double x;
  private double y;
  /** Missiles présents dans l'aire de jeu. */
  private final List<Zone> zones;
  /** Temps restant avant la prochaine apparition d'un missile, en secondes. */
  private double spawnTimer;
  /** Générateur utilisé pour choisir aléatoirement la rangée des missiles. */
  private final Random random;

  /**
   * Crée le jeu avec le joueur sur la rangée centrale et initialise le compte à
   * rebours.
   */
  public Touch() {
    x = WIDTH/2;
    y = HEIGHT/2;
    zones = new ArrayList<>();
    spawnTimer = 1;
    random = new Random();
    super();
  }

  /**
   * Traite les changements de rangée, fait apparaître et avance les missiles,
   * puis vérifie si l'un d'eux a atteint le joueur.
   *
   * @param dt         durée du pas de simulation, en secondes
   * @param input      état des entrées clavier
   * @param difficulty niveau de difficulté global
   * @return {@code true} si aucun missile n'a atteint le bord droit sur la rangée
   *         du joueur, {@code false} en cas de collision
   * @throws IllegalArgumentException si {@code dt} ou {@code difficulty} est
   *                                  négatif
   * @throws NullPointerException     si {@code input} est {@code null}
   */
  @Override
  public boolean update(double dt, Input input, double difficulty) {
    Game.checkInput(dt, input, difficulty);
    
    readInput(input);
    spawnZones(dt, difficulty);
    updateZones(dt, difficulty);
    return checkBorder();
  }

  /**
   * Déplace le joueur d'au plus une rangée en réponse à un nouvel appui sur la
   * flèche haut ou bas. Le déplacement est limité à l'intervalle des rangées
   * disponibles.
   *
   * @param input état des entrées clavier
   */
  private void readInput(Input input) {
    var dirX = (input.isKeyDown(Key.Q) ? -SPEEDX : 0) + (input.isKeyDown(Key.D) ? SPEEDX : 0);
    var dirY = (input.isKeyDown(Key.Z) ? -SPEEDY : 0) + (input.isKeyDown(Key.S) ? SPEEDY : 0);
    if(dirX != 0) {
    	if(x + dirX > WIDTH - RAYON_PLAYER/2) {
    		x = WIDTH - RAYON_PLAYER/2;
    	}
    	if(x + dirX < RAYON_PLAYER/2) {
    		x = RAYON_PLAYER/2;
    	}
    	x += dirX;
    }
    if(dirY != 0) {
    	if(y + dirY > HEIGHT) {
    		y = HEIGHT;
    	}
    	if(y + dirY < 0) {
    		y = 0;
    	}
    	y += dirY;
    }
  }
  
  public double PlayerX() {
  	return x;
  }
  
  public double PlayerY() {
  	return y;
  }
  
  public int PlayerRayon() {
  	return RAYON_PLAYER;
  }
  
  public int ZoneRayon() {
  	return RAYON_ZONE;
	}

	public int width() {
		return WIDTH;
	}

	public int height() {
		return HEIGHT;
	}
  
  /**
   * Diminue le compte à rebours et fait apparaître un missile sur une rangée
   * aléatoire lorsque celui-ci devient strictement négatif. L'intervalle diminue
   * avec la difficulté, sans passer sous 0,45 seconde.
   *
   * @param dt         durée du pas de simulation, en secondes
   * @param difficulty niveau de difficulté global
   */
  private void spawnZones(double dt, double difficulty) {
    var spawnInterval = Math.max(1.5, BASE_SPAWN_INTERVAL - difficulty * 0.15);
    spawnTimer -= dt;
    Random random = new Random();
    if (spawnTimer < 0) {
      zones.add(new Zone(
      		new Cercle(random.nextInt(WIDTH), random.nextInt(HEIGHT), RAYON_ZONE), 
      		TEMPS_ZONE));
      spawnTimer = spawnInterval;
    }
  }

  /**
   * Déplace chaque missile vers la droite à la vitesse déterminée par la
   * difficulté.
   *
   * @param dt         durée du pas de simulation, en secondes
   * @param difficulty niveau de difficulté global
   */
  private void updateZones(double dt, double difficulty) {
    zones.replaceAll(m -> m.update(dt));
  }

  /**
   * Détecte si un missile a atteint le bord droit sur la rangée du joueur, puis
   * retire les missiles sortis de l'aire de jeu lorsque cela ne met pas fin à la
   * partie.
   *
   * @return {@code false} si un missile a atteint le joueur ; {@code true} sinon
   */
  private boolean checkBorder() {
    if (zones.stream().anyMatch(m -> m.time <= 0)) {
      return false;
    }
    Cercle CerclePlayer = new Cercle((int) x, (int) y, RAYON_PLAYER);
    zones.removeIf(z -> z.cercle().Intersects(CerclePlayer));
    return true;
  }
  
  /**
   * Retourne la rangée actuellement occupée par le joueur.
   *
   * @return indice de rangée, compris entre {@code 0} et {@code ROWS - 1}
   */

  /**
   * Retourne les missiles actuellement présents.
   *
   * <p>
   * La liste ne peut pas être modifiée par l'appelant, mais elle reflète les
   * modifications effectuées lors des mises à jour suivantes du jeu.
   * </p>
   *
   * @return vue non modifiable de la liste des missiles
   */
  public List<Zone> zones() {
    return Collections.unmodifiableList(zones);
  }
}