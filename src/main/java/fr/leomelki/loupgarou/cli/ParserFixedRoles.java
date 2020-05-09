package fr.leomelki.loupgarou.cli;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

import fr.leomelki.loupgarou.MainLg;

class ParserFixedRoles extends ParserAbstract {
  final String roleDistributionWarning;
  final boolean isRoleDistributionFixed;

  protected ParserFixedRoles(CommandInterpreter other) {
    super(other);

    final FileConfiguration config = this.instanceMainLg.getConfig();
    final String roleDistribution = config.getString("roleDistribution");

    this.roleDistributionWarning = "§b'roleDistribution: fixed'§5 dans config.yml.\n/!\\ Actuellement le mode de distribution est: §b'roleDistribution: "
        + roleDistribution + "'§5\n";
    this.isRoleDistributionFixed = roleDistribution.equals("fixed");
  }

  protected void processRoles(CommandSender sender, String[] args) {
    final boolean isAuthorized = this.isAuthorized(sender);

    if (args.length == 1) {
      this.displayAvailableRoles(sender);
      return;
    }

    if (args[1].equalsIgnoreCase("list")) {
      this.displayAllRoles(sender);
      return;
    }

    if (args[1].equalsIgnoreCase("set")) {
      if (!isAuthorized) {
        this.denyCommand(sender);
        return;
      }

      this.setRoleAvailability(sender, args);
      return;
    }

    sender.sendMessage("\n§4Erreur: §cCommande incorrecte.");
    sender
        .sendMessage("§4Essayez §c/lg roles§4 ou §c/lg roles list§4 ou §c/lg roles set <role_id/role_name> <nombre>§4");
  }

  /* ========================================================================== */
  /*                           DISPLAY AVAILABLE ROLES                          */
  /* ========================================================================== */

  private void displayAvailableRoles(CommandSender sender) {
    if (!this.isRoleDistributionFixed) {
      sender.sendMessage(
          "\n§l§5/!\\ Les valeurs qui suivent ne sont applicables qu'avec " + this.roleDistributionWarning);
    }

    sender.sendMessage("\n§7Voici la liste des rôles:");

    for (String role : this.instanceMainLg.getRolesBuilder().keySet()) {
      final int openedSlots = this.getOpenedSlots(role);

      if (openedSlots > 0) {
        sender.sendMessage("  §e- §6" + role + " §e: " + openedSlots);
      }
    }
  }

  /* ========================================================================== */
  /*                              DISPLAY ALL ROLES                             */
  /* ========================================================================== */

  private void displayAllRoles(CommandSender sender) {
    if (!this.isRoleDistributionFixed) {
      sender.sendMessage(
          "\n§l§5/!\\ Les valeurs qui suivent ne sont applicables qu'avec " + this.roleDistributionWarning);
    }

    int index = 0;
    sender.sendMessage("\n§7roVoici la liste complète des rôles:");

    for (String role : this.instanceMainLg.getRolesBuilder().keySet()) {
      final int openedSlots = this.getOpenedSlots(role);

      sender.sendMessage("  §e- " + (index++) + " - §6" + role + " §e> " + openedSlots);
    }

    sender.sendMessage(
        "\n§7Écrivez §8§o/lg roles set <role_id/role_name> <nombre>§7 pour définir le nombre de joueurs qui devrons avoir ce rôle.");
  }

  /* ========================================================================== */
  /*                             SET ROLE AVAILABILITY                          */
  /* ========================================================================== */

  private void setRoleAvailability(CommandSender sender, String[] args) {
    if (args.length != 4) {
      sender.sendMessage("\n§4Erreur: §cCommande incorrecte.");
      sender.sendMessage("§4Essayez §c/lg roles set <role_id/role_name> <nombre>§4");
      return;
    }

    if (!this.isRoleDistributionFixed) {
      sender.sendMessage("\n§l§5/!\\ Ces valeurs vont être sauvegardées mais ne seront utilisées qu'avec "
          + this.roleDistributionWarning);
    }

    final String roleName = this.getRoleName(args[2]);

    if (roleName == null) {
      sender.sendMessage("\n§4Erreur: Le rôle §c'" + args[2] + "'§4 n'existe pas");
    }

    final Integer amount = this.getRoleAmount(args[3]);

    if (amount == null) {
      sender.sendMessage("\n§4Erreur: La valeur §c'" + args[3] + "'§4 n'est pas une quantité valide de joueurs");
    }

    this.setOpenedSlots(roleName, amount);

    sender.sendMessage("\n§6Il y aura §e " + amount + " §6" + roleName);
    this.instanceMainLg.saveConfig();
    this.instanceMainLg.loadConfig();
    sender.sendMessage("§7§oSi vous avez fini de changer les rôles, utilisez §8§o/lg joinall§7§o");
  }

  /* ========================================================================== */
  /*                                UTILITY METHODS                            */
  /* ========================================================================== */

  private int getOpenedSlots(final String role) {
    final String roleKey = MainLg.DISTRIBUTION_FIXED_KEY + role;

    return this.instanceMainLg.getConfig().getInt(roleKey);
  }

  private void setOpenedSlots(final String role, final int amount) {
    final String roleKey = MainLg.DISTRIBUTION_FIXED_KEY + role;

    this.instanceMainLg.getConfig().set(roleKey, amount);
  }

  private String parseRoleKey(String raw) {
    try {
      final int roleID = Integer.parseInt(raw);
      final Object[] array = this.instanceMainLg.getRolesBuilder().keySet().toArray();

      return (array.length > roleID) ? (String) array[roleID] : null;
    } catch (NumberFormatException e) {
      return raw;
    }
  }

  private String getRoleName(String raw) {
    final String rawRoleName = this.parseRoleKey(raw);

    return (rawRoleName != null)
        ? this.instanceMainLg.getRolesBuilder().keySet().stream().filter(e -> e.equalsIgnoreCase(rawRoleName)).findAny()
            .orElse(null)
        : null;
  }

  private Integer getRoleAmount(String raw) {
    try {
      final Integer parsedValue = Integer.parseInt(raw);

      return (parsedValue >= 0) ? parsedValue : null;
    } catch (NumberFormatException e) {
      return null;
    }
  }
}