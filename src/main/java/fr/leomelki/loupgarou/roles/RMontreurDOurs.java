package fr.leomelki.loupgarou.roles;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import fr.leomelki.loupgarou.classes.LGGame;
import fr.leomelki.loupgarou.classes.LGPlayer;
import fr.leomelki.loupgarou.events.LGDayStartEvent;

public class RMontreurDOurs extends Role {
	private int lastNight = -1;

	public RMontreurDOurs(LGGame game) {
		super(game);
	}

	@Override
	public RoleType getType() {
		return RoleType.VILLAGER;
	}

	@Override
	public RoleWinType getWinType() {
		return RoleWinType.VILLAGE;
	}

	@Override
	public String getName(int amount) {
		final String baseline = this.getName();

		return (amount > 1) ? baseline.replace("ontreur", "ontreurs") : baseline;
	}

	@Override
	public String getName() {
		return "§a§lMontreur d'Ours";
	}

	@Override
	public String getFriendlyName() {
		return "du " + getName();
	}

	@Override
	public String getShortDescription() {
		return "Tu gagnes avec le §a§lVillage";
	}

	@Override
	public String getDescription() {
		return "Tu gagnes avec le §a§lVillage§f. Chaque matin, ton Ours va renifler tes voisins et grognera si l'un d'eux est hostile aux Villageois.";
	}

	@Override
	public String getTask() {
		return "";
	}

	@Override
	public String getBroadcastedTask() {
		return "";
	}

	@Override
	public int getTimeout() {
		return -1;
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onDay(LGDayStartEvent e) {
		final int currentNight = this.game.getNight();
		final boolean shouldProceed = (e.getGame() == this.game && lastNight != currentNight && !this.players.isEmpty());

		if (!shouldProceed) {
			return;
		}

		lastNight = currentNight;

		for (LGPlayer bears : this.players) {
			for (LGPlayer neighbor : bears.getAdjacentPlayers()) {
				final RoleWinType neighborWinType = neighbor.getRoleWinType();

				if (neighborWinType != RoleWinType.VILLAGE && neighborWinType != RoleWinType.NONE) {
					getGame().broadcastMessage("§6La bête du " + getName() + "§6 grogne...");
				}
			}
		}
	}
}
