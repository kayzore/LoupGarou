package fr.leomelki.loupgarou.cli;

import org.bukkit.command.CommandSender;

import fr.leomelki.loupgarou.MainLg;

public class CommandInterpreter extends ParserAbstract {
  public CommandInterpreter(MainLg instance) {
    super(instance);
  }

  public boolean dispatch(CommandSender sender, String label, String[] args) {
    if (!label.equalsIgnoreCase("lg")) {
      return false;
    }

    this.process(sender, args);

    return true;
  }

  private void process(CommandSender sender, String[] args) {
    if (args[0].equalsIgnoreCase("roles")) {
      (new ParserFixedRoles(this)).processRoles(sender, args);
      return;
    }

    // The rest of the commands require admin permissions
    if (!isAuthorized(sender)) {
      denyCommand(sender);
      return;
    }

    if (args[0].equalsIgnoreCase("joinAll")) {
      (new ParserGame(this)).processJoinAll(sender);
      return;
    }

    if (args[0].equalsIgnoreCase("start")) {
      (new ParserGame(this)).processStartGame(sender, args);
      return;
    }

    if (args[0].equalsIgnoreCase("nextDay")) {
      (new ParserRounds(this)).processNextDay(sender);
      return;
    }

    if (args[0].equalsIgnoreCase("nextNight")) {
      (new ParserRounds(this)).processNextNight(sender);
      return;
    }

    if (args[0].equalsIgnoreCase("end")) {
      (new ParserGame(this)).processEndGame(sender, args);
      return;
    }

    if (args[0].equalsIgnoreCase("addSpawn")) {
      (new ParserSpawnpoints(this)).processSpawn(sender);
      return;
    }

    if (args[0].equalsIgnoreCase("reloadConfig")) {
      (new ParserConfig(this)).processReloadConfig(sender);
      return;
    }

    if (args[0].equalsIgnoreCase("reloadPacks")) {
      (new ParserConfig(this)).processReloadPacks(sender);
      return;
    }

    if (args[0].equalsIgnoreCase("nick")) {
      (new ParserNicknames(this)).processNick(sender, args);
      return;
    }

    if (args[0].equalsIgnoreCase("unnick")) {
      (new ParserNicknames(this)).processUnnick(sender, args);
      return;
    }

    sender.sendMessage("§4Erreur: §cCommande incorrecte.");
    sender.sendMessage(
        "§4Essayez /lg §caddSpawn/end/start/nextNight/nextDay/reloadConfig/roles/reloadPacks/joinAll/nick/unnick");
  }
}
