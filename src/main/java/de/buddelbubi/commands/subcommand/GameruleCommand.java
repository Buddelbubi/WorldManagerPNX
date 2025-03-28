package de.buddelbubi.commands.subcommand;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.form.element.custom.ElementInput;
import cn.nukkit.form.element.custom.ElementToggle;
import cn.nukkit.form.window.CustomForm;
import cn.nukkit.level.GameRule;
import cn.nukkit.level.Level;
import de.buddelbubi.WorldManager;

import java.util.LinkedList;

public class GameruleCommand extends SubCommand{

	public GameruleCommand() {
		super("gamerule");
		this.setAliases(new String[] {
				"gamerule",
				"gamerules"
		});
	}

	@Override
	public CommandParameter[] getParameters() {
		
		LinkedList<CommandParameter> parameters = new LinkedList<>();
		parameters.add(CommandParameter.newEnum(this.getName(), this.getAliases()));
		parameters.add(CommandParameter.newType("world", true, CommandParamType.STRING));
		return parameters.toArray(new CommandParameter[parameters.size()]);
		
	}
	
	@Override
	public boolean execute(CommandSender sender, String arg1, String[] args) {
		
		  if (!(sender instanceof Player)) {
			  sender.sendMessage(WorldManager.prefix + "§cThis command can only be used ingame.");
			 return false;
		  }

		  if (!sender.hasPermission("worldmanager.admin") && !sender.hasPermission("worldmanager.gamerule") && !sender.hasPermission("worldmanager.gamerule." + args[1])) {
			  sender.sendMessage(WorldManager.prefix + "§cYou are lacking the permission §e'worldmanager." + args[0] + (args.length == 2 ? args[1] : "") + "'");
			 return false;
		  }

		  Level level;
		  if (args.length == 1) {
			 level = ((Player) sender).getLevel();
		  } else
		  if (args.length == 2) {
			 if (Server.getInstance().getLevelByName(args[1]) != null) {
				level = Server.getInstance().getLevelByName(args[1]);
			 } else {
				 sender.sendMessage(WorldManager.prefix + "§cThis world does not exist.");
				return false;
			 }
		  } else {
			  sender.sendMessage(WorldManager.prefix + "§cDo /worldmanager gamerule [World]*.");
			 return false;
		  }

		CustomForm c = new CustomForm("§3WorldGamerules - " + level.getName());
		  for (GameRule r : GameRule.values()) {
			 switch (level.getGameRules().getGameRuleType(r)) {

				case BOOLEAN:
				    c.addElement(new ElementToggle(r.getName(), level.getGameRules().getBoolean(r)));
				    break;
				case INTEGER:
				    c.addElement(new ElementInput(r.getName(), r.getName(), String.valueOf(level.getGameRules().getInteger(r))));
				    break;
				default:
				    break;
			 }
		  }
		  ((Player) sender).sendForm(c);
		return false;
	}

}
