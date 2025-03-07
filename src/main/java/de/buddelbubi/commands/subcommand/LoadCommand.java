package de.buddelbubi.commands.subcommand;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.registry.Registries;
import de.buddelbubi.WorldManager;
import de.buddelbubi.utils.LevelNBT;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class LoadCommand extends SubCommand {

    public LoadCommand() {
        super("load");
        this.setAliases(new String[]{
                "load",
                "import",
                "enable"
        });
    }

    @Override
    public CommandParameter[] getParameters() {

        LinkedList<CommandParameter> parameters = new LinkedList<>();
        parameters.add(CommandParameter.newEnum(this.getName(), this.getAliases()));
        return parameters.toArray(new CommandParameter[parameters.size()]);

    }

    @Override
    public boolean execute(CommandSender sender, String arg1, String[] args) {

        if (!sender.hasPermission("worldmanager.admin") && !sender.hasPermission("worldmanager.load")) {
            sender.sendMessage(WorldManager.prefix + "§cYou are lacking the permission §e'worldmanager.load'.");
            return false;
        } else {
            if (args.length >= 2) {
                String levelname = args[1];
                if (Server.getInstance().getLevelByName(levelname) == null) {
                    File levelDat = new File(Server.getInstance().getDataPath() + "worlds/" + args[1] + "/level.dat");
                    if (levelDat.exists()) {
                        if (args.length >= 3) {
                            for (int i = 0; i < args.length; i++) {
                                if (args[i].equals("-g")) {
                                    sender.sendMessage(WorldManager.prefix + "§cLoading the world with a specific generator is not working on PowerNukkitX. You have to change the generator in the world's confog.json file.");
                                }
                            }
                        }

                        boolean loaded = Server.getInstance().loadLevel(levelname);
                        if (loaded) {
                            sender.sendMessage(WorldManager.prefix + "§7The world §8" + levelname + "§7 loaded successfully.");

                            //Checking for the teleport attribute
                            for (String arg : args) {
                                if (arg.equalsIgnoreCase("-t")) {
                                    if (sender instanceof Player player) {
                                        player.teleport(Server.getInstance().getLevelByName(levelname).getSafeSpawn());
                                    } else {
                                        sender.sendMessage(WorldManager.prefix + "§cThis parameter is for ingame use only!");
                                    }
                                    break;
                                }
                            }
                        } else
                            sender.sendMessage(WorldManager.prefix + "§cThe world §8" + levelname + "§4failed §cload.");
                    } else sender.sendMessage(WorldManager.prefix + "§cThis world does not exist.");
                } else sender.sendMessage(WorldManager.prefix + "§cThis world is already loaded.");
            } else sender.sendMessage(WorldManager.prefix + "§cDo /worldmanager load [Name] (args).");
        }
        return false;
    }

}