package multitask.controller;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import com.github.forax.zen.ApplicationContext;
import com.github.forax.zen.KeyboardEvent;
import com.github.forax.zen.KeyboardEvent.Action;
import com.github.forax.zen.KeyboardEvent.Key;
import com.github.forax.zen.PointerEvent;

import multitask.model.Game;
import multitask.model.Model;
import multitask.model.games.Avoid;
import multitask.model.games.Balance;
import multitask.view.Fin;
import multitask.view.GeneralView;
import multitask.view.View;
import multitask.view.ViewTitle;
import multitask.view.Viewers;
import multitask.view.games.AvoidView;
import multitask.view.games.BalanceView;
import multitask.view.games.GameView;

public class TitleScreen {

	/** Durée cible d'une image, en secondes (environ 60 images par seconde). */
	private static final double TARGET_DT = 1.0 / 60;
	/**
	 * Durée maximale d'un pas de simulation, en secondes, pour limiter les grands
	 * sauts temporels.
	 */
	private static final double MAX_DT = 0.05;
	
	/** État des touches maintenues et nouvellement pressées. */
	private final Input input;
	/** Contexte graphique utilisé pour récupérer les événements clavier. */
	private final ApplicationContext context;
	/** Indique si la boucle principale doit continuer. */
	private boolean running;
	
	private List<Button> Buttons;
	
	private boolean Play;
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
  public TitleScreen(ApplicationContext context) {
    this.context = context;
    this.Buttons = new ArrayList<>();
    Play = true;
    input = new Input();
    running = false;
    var screen = context.getScreenInfo();
    width = screen.width();
    height = screen.height();
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
	public void run() {
		
		Buttons.add(new Button("Jouer", Color.GREEN, Outils.CenterRect(width/2, height/2 - 200, 400, 300), _ -> RunGame()));
		Buttons.add(new Button("Quitter", Color.RED, Outils.CenterRect(width/2, height/2 + 200, 400, 300), _ -> Play = false));
		var view = new ViewTitle(context, Buttons);
		var lastTime = System.nanoTime();

		while (true) {
			var now = System.nanoTime();
			var dt = Math.min((now - lastTime) / 1_000_000_000.0, MAX_DT);
			lastTime = now;

			pollInput();
			if (input.isKeyJustPressed(Key.ESCAPE) || !Play) {
				break;
			}
			checkButtonPressed(); 
			
			view.render(null);
			input.nextFrame();
			sleepUntilNextFrame(now);
		}
	}
	
	private void checkButtonPressed() {
		if(input.isPointerJustPressed()) {
			var location = input.PointerLocation();
			int x = location.x();
			int y = location.y();
			for(var button : Buttons) {
				if(button.rect().contains(x, y)) {
					button.fonction().accept(null);
				}
			}
		}
		
	}
	/**
   * Enregistre un mini-jeu dans le modèle et associe à cette instance sa vue
   * graphique.
   *
   * @param <G>     type concret du mini-jeu
   * @param model   modèle auquel ajouter le mini-jeu
   * @param viewers registre dans lequel enregistrer sa vue
   * @param game    instance du mini-jeu à enregistrer
   * @param time    temps, en secondes depuis le début de la partie, auquel
   *                activer le mini-jeu
   * @param factory fonction construisant une vue à partir de l'instance du
   *                mini-jeu
   */
	
	private void RunGame() {
		try {
			while (Play) {
				var controller = CreateController();
				Play = controller.run();
			}
		} finally {
		}
	}
	private Controller CreateController() {
		var model = new Model();
		var viewers = new Viewers();
		addGame(model, viewers, new Balance(), 0, BalanceView::new);
		addGame(model, viewers, new Avoid(), 5, AvoidView::new);

		var view = new GeneralView(context, viewers);
		return new Controller(model, view, context);
	}
	
  private static <G extends Game> void addGame(Model model, Viewers viewers, G game, int time,
      Function<G, GameView> factory) {
    model.addGame(game, time);
    viewers.addGame(game, factory.apply(game));
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
