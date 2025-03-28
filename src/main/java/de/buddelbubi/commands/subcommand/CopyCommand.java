package de.buddelbubi.commands.subcommand;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.level.Level;
import de.buddelbubi.WorldManager;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

public class CopyCommand extends SubCommand {

    public CopyCommand() {
        super("copy");
        this.setAliases(new String[] {
            "copy",
            "duplicate",
            "dupe",
            "backup",
            "replicate"
        });
    }

    @Override
    public CommandParameter[] getParameters() {
        LinkedList<CommandParameter> parameters = new LinkedList < > ();
        parameters.add(CommandParameter.newEnum(this.getName(), this.getAliases()));
        parameters.add(CommandParameter.newType("world", true, CommandParamType.STRING));
        parameters.add(CommandParameter.newType("nameofcopy", true, CommandParamType.STRING));
        return parameters.toArray(new CommandParameter[parameters.size()]);
    }

    @Override
    public boolean execute(CommandSender sender, String arg1, String[] args) {
        if (!sender.hasPermission("worldmanager.admin") && !sender.hasPermission("worldmanager.copy")) {

			sender.sendMessage(WorldManager.prefix + "§cYou are lacking the permission §e'worldmanager.copy'.");
            return false;

        } else {
                Level level = null;
                String name = null;
                if(args.length >= 1) {
                	for(int i = 0; i < args.length; i++) {
                		String arg = args[i];
                		if(arg.equals("-w")) {
            				if(args.length >= i+1) {
								level = Server.getInstance().getLevelByName(args[i+1]);
            				} else {
								sender.sendMessage(WorldManager.prefix + "§cUse /worldmanager copy -w [World] -n [New Name]");
            					return true;
            				}
            			} else if(arg.equals("-n")) {
            				if(args.length >= i+1) {
            					name = args[i+1];
            				} else {
								sender.sendMessage(WorldManager.prefix + "§cUse /worldmanager copy -n [New Name]");
            					return true;
            				}
            			}
                	}

					if(level == null) {
						if(args.length >= 2) {
							String levelNameToCopy = args[1];
							if(levelNameToCopy.equalsIgnoreCase("-c")) {
								level = ((Player) sender).getLevel();
							} else level = Server.getInstance().getLevelByName(levelNameToCopy);
						}
					}

					if(name == null) {
						if(args.length >= 3) {
							name = args[2];
						}
					}

                	if(level == null && sender instanceof Player) level = ((Player) sender).getLevel();
                	if(name == null && level != null) name = "CopyOf" + level.getFolderName();

				} else
					sender.sendMessage(WorldManager.prefix + "§cDo /worldmanager copy (-w [World])* (-n [Name of Copy])* (-t)*.");
                if (level != null) {

                    int i = 1;
                    while (new File(Server.getInstance().getDataPath() + "worlds/" + name + (i == 1 ? "" : ("#" + i))).exists()) {
                        i++;
                    }
                    if (i != 1) name += (i == 1 ? "" : ("#" + i));
                    new File(Server.getInstance().getDataPath() + "worlds/" + name).mkdir();

                    try {
                        FileUtils.copyDirectory(new File(Server.getInstance().getDataPath() + "worlds/" + level.getFolderName()), new File(Server.getInstance().getDataPath() + "worlds/" + name));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    Server.getInstance().loadLevel(name);
                    
                    for(String arg : args) {
                    	if(arg.equalsIgnoreCase("-t")) {
           				 	if(sender instanceof Player) {
               				 	Player player = (Player) sender;
               				 	player.teleport(Server.getInstance().getLevelByName(name).getSafeSpawn());
           				 	} else  {
								sender.sendMessage(WorldManager.prefix + "§cThis parameter is for ingame use only!");
           				 	}
           				 	break;
                    	}
           		 	}

					sender.sendMessage(WorldManager.prefix + "§7Created a copy of §8" + level.getName() + " §7called §8" + name + ".");

				} else sender.sendMessage(WorldManager.prefix + "§cThis world does not exist.");

        }
        return false;
    }

}