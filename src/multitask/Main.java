package multitask;

import java.util.function.Function;

import com.github.forax.zen.Application;
import com.github.forax.zen.ApplicationContext;
import multitask.controller.Controller;
import multitask.controller.TitleScreen;
import multitask.model.Game;
import multitask.model.Model;
import multitask.model.games.Balance;
import multitask.model.games.Avoid;
import multitask.view.GeneralView;
import multitask.view.Viewers;
import multitask.view.games.AvoidView;
import multitask.view.games.BalanceView;
import multitask.view.games.GameView;

/**
 * Point d'entrée de l'application de mini-jeux multitâches.
 *
 * <p>
 * Cette classe crée l'interface graphique, le modèle partagé par les mini-jeux
 * et le contrôleur qui orchestre les mises à jour et le rendu. Elle enregistre
 * les mini-jeux avec leur vue respective.
 * </p>
 */
public final class Main {

  /**
   * Constructeur privé afin d'empêcher l'instanciation de cette classe
   * utilitaire.
   */
  private Main() {
    super();
  }

  /**
   * Lance la boucle de l'application graphique.
   *
   * @param args arguments de la ligne de commande ; ils ne sont pas utilisés
   *             actuellement
   */
  public static void main(String[] args) {
    Application.run(GeneralView.BACKGROUND, Main::game);
  }

  /**
   * Construit les composants de la partie et exécute le contrôleur jusqu'à la fin
   * du jeu. <br>
   * Le contexte graphique est libéré même si l'exécution du contrôleur se termine
   * à la suite d'une exception.
   *
   * @param context contexte graphique fourni par la bibliothèque Zen
   */
	private static void game(ApplicationContext context) {
		TitleScreen title = new TitleScreen(context);
		try {
			title.run();
		} finally {
			context.dispose();
		}
		
	}

}
