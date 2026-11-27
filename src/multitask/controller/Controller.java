package multitask.controller;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import com.github.forax.zen.ApplicationContext;
import com.github.forax.zen.KeyboardEvent;
import com.github.forax.zen.KeyboardEvent.Action;
import com.github.forax.zen.KeyboardEvent.Key;
import com.github.forax.zen.PointerEvent;

import multitask.model.Model;
import multitask.view.View;
import multitask.view.ViewTitle;
import multitask.view.Fin;

/**
 * Contrôleur principal : collecte les événements clavier, met à jour le modèle
 * et demande le rendu de la vue à chaque itération de la boucle de jeu.
 */
public final class Controller {

  /** Durée cible d'une image, en secondes (environ 60 images par seconde). */
  private static final double TARGET_DT = 1.0 / 60;
  /**
   * Durée maximale d'un pas de simulation, en secondes, pour limiter les grands
   * sauts temporels.
   */
  private static final double MAX_DT = 0.05;

  /** Modèle contenant l'état des mini-jeux. */
  private final Model model;
  /** Vue chargée d'afficher l'état courant du modèle. */
  private final View view;
  private final View EndView;
  /** État des touches maintenues et nouvellement pressées. */
  private final Input input;
  /** Contexte graphique utilisé pour récupérer les événements clavier. */
  private final ApplicationContext context;
  /** Indique si la boucle principale doit continuer. */
  private boolean running;
  private boolean quit;
  private boolean pause;
  
  private List<Button> Buttons;
  private int width;
	private int height;
	
  /**
   * Crée un contrôleur associé au modèle, à la vue et au contexte graphique
   * fournis.
   *
   * @param model   modèle à faire évoluer
   * @param view    vue à redessiner à chaque image
   * @param context contexte dans lequel les événements clavier sont récupérés
   */
  public Controller(Model model, View view, ApplicationContext context) {
    this.model = model;
    this.view = view;
    this.context = context;
    this.EndView = new Fin(context);
    this.Buttons = new ArrayList<>();
    input = new Input();
    running = false;
    quit = false;
    pause = false;
    var screen = context.getScreenInfo();
    width = screen.width();
    height = screen.height();
    
    super();
  }

  /**
   * Exécute la boucle principale jusqu'à ce que le modèle signale la fin de la
   * partie ou que l'attente entre deux images soit interrompue.
   *
   * <p>
   * À chaque itération, cette méthode calcule le temps écoulé depuis l'image
   * précédente, plafonne ce pas à {@link #MAX_DT}, traite les événements, met à
   * jour le modèle, avance l'état temporel de l'entrée, dessine l'image puis
   * attend si nécessaire pour approcher la fréquence cible définie par
   * {@link #TARGET_DT}.
   * </p>
   */
	public boolean run() {
		running = true;
		Buttons.add(new Button("Reprendre", Color.GREEN, Outils.CenterRect(width/2, height/2 - 200, 400, 300), _ -> pause = false));
		Buttons.add(new Button("Quitter", Color.RED, Outils.CenterRect(width/2, height/2 + 200, 400, 300), _ -> quit = true));
		var PauseView = new ViewTitle(context, Buttons);
		
		var lastTime = System.nanoTime();
		while (true) {
			var now = System.nanoTime();
			var dt = Math.min((now - lastTime) / 1_000_000_000.0, MAX_DT);
			lastTime = now;

			pollInput();
			if (input.isKeyJustPressed(Key.ESCAPE)) {
				pause = !pause;
			}
			if(quit || (input.isKeyJustPressed(Key.ESCAPE) && running == false)) {
				return false;
			}
			if (running && !pause) {
				running = model.update(dt, input);
				input.nextFrame();
				view.render(model);
			} 
			else if(pause) {
				checkButtonPressed();
				input.nextFrame();
				PauseView.render(null);
			}
			else {
				if (input.isKeyJustPressed(Key.R)) {
					return true;
				}
				input.nextFrame();
				EndView.render(model);
			}

			sleepUntilNextFrame(now);
		}
	}

  /**
   * Vide la file des événements disponibles et transmet les événements clavier à
   * leur traitement. Les événements d'autres types sont consommés sans action
   * particulière.
   */
	private void pollInput() {
    for (var b = true; b;) {
      b = switch (context.pollEvent()) {
      case null -> false;
      case KeyboardEvent ke -> {
        handleKey(ke);
        yield true;
      }
      case PointerEvent p -> {
        handlePointer(p);
        yield true;
      }
      default -> true;
      };
    }
  }
	
	private void checkButtonPressed() {
		if (input.isPointerJustPressed()) {
			var location = input.PointerLocation();
			int x = location.x();
			int y = location.y();
			for (var button : Buttons) {
				if (button.rect().contains(x, y)) {
					button.fonction().accept(null);
				}
			}
		}
	}
	
  /**
   * Convertit un événement clavier en mise à jour de l'état des touches.
   *
   * @param event événement clavier à interpréter
   */
	private void handleKey(KeyboardEvent event) {
    var gameKey = event.key();
    switch (event.action()) {
    case Action.KEY_PRESSED -> input.keyDown(gameKey);
    case Action.KEY_RELEASED -> input.keyUp(gameKey);
    }
  }
  
  private void handlePointer(PointerEvent event) {
    switch (event.action()) {
    case POINTER_DOWN-> {
    	input.pointerDown();
    	input.UpdateLocation(event.location());
    }
    case POINTER_UP -> {
    	input.pointerUp();
    	input.UpdateLocation(event.location());
    }
		case POINTER_MOVE -> input.UpdateLocation(event.location());
    }
    
  }
  
  /**
   * Attend la fin de l'intervalle cible d'une image si le traitement courant
   * s'est terminé assez tôt.
   *
   * @param lastTime instant de référence de l'image courante, mesuré par
   *                 {@link System#nanoTime()}
   */
  private void sleepUntilNextFrame(long lastTime) {
    var elapsedTime = (System.nanoTime() - lastTime) / 1_000_000;
    var sleepTime = (long) (TARGET_DT * 1000) - elapsedTime;
    if (sleepTime > 0) {
      try {
        Thread.sleep(sleepTime);
      } catch (InterruptedException e) {
        running = false;
      }
    }
  }
}
