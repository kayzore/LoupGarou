package fr.leomelki.loupgarou.roles;

import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import fr.leomelki.loupgarou.classes.LGGame;
import fr.leomelki.loupgarou.classes.LGPlayer;
import fr.leomelki.loupgarou.events.LGPlayerKilledEvent;
import fr.leomelki.loupgarou.events.LGPlayerKilledEvent.Reason;

public class RFaucheur extends Role {
	private static Random random = new Random();
	protected static final String EXTERMINATED_HIS_NEIGHBORS = "reaper_exterminated_his_neighbors";

	public RFaucheur(LGGame game) {
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

		return (amount > 1) ? baseline + "s" : baseline;
	}

	@Override
	public String getName() {
		return "§a§lFaucheur";
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
		return "Tu gagnes avec le §a§lVillage§f. Si les §c§lLoups-Garous§f te tuent pendant la nuit, tu emporteras l’un d’entre eux dans ta mort, mais si tu meurs lors du vote du §a§lvillage§f, ce sont tes deux voisins qui en paieront le prix.";
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
	public void onKill(LGPlayerKilledEvent e) {
		if (e.getKilled().getRole() == this && e.getKilled().isRoleActive()) {
			final LGPlayer killed = e.getKilled();
			final Reason reasonOfDeath = e.getReason();

			// A déjà fait son coup de faucheur !
			if (killed.getCache().getBoolean(RFaucheur.EXTERMINATED_HIS_NEIGHBORS)) {
				return;
			}

			killed.getCache().set(RFaucheur.EXTERMINATED_HIS_NEIGHBORS, true);

			if (reasonOfDeath == Reason.VOTE) {
				for (LGPlayer neighbor : killed.getAdjacentPlayers()) {
					final LGPlayerKilledEvent killEvent = new LGPlayerKilledEvent(getGame(), e.getKilled(), e.getReason());
					
				for (LGPlayer neighbor : killed.getAdjacentPlayers()) {
					e.setKilled(neighbor);
					e.setReason(Reason.FAUCHEUR);
					Bukkit.getPluginManager().callEvent(killEvent);
					
					if (!killEvent.isCancelled()) {
						getGame().kill(killEvent.getKilled(), killEvent.getReason(), false);
					}
				}

				return;
			}

			// Mort par les LG > Tue un lg au hasard
			if (reasonOfDeath == Reason.LOUP_GAROU || reasonOfDeath == Reason.GM_LOUP_GAROU) {
				final LGPlayerKilledEvent killEvent = new LGPlayerKilledEvent(getGame(), e.getKilled(), e.getReason());
				final Role matchingRole = getGame().getRoles().stream().filter(RLoupGarou.class::isInstance).findFirst().orElse(null);

				Bukkit.getPluginManager().callEvent(killEvent);

				if (matchingRole != null) {
					final List<LGPlayer> remainingWerewolves = matchingRole.getPlayers();
					final LGPlayer selected = remainingWerewolves.get(random.nextInt(remainingWerewolves.size()));
					
					e.setKilled(selected);
					e.setReason(Reason.FAUCHEUR);

					if (killEvent.isCancelled()) {
						return;
					}
					
					getGame().kill(killEvent.getKilled(), killEvent.getReason(), false);
				}
			}
		}
	}
}
