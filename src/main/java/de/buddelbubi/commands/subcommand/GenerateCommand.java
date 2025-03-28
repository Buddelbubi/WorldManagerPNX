package de.buddelbubi.commands.subcommand;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.ConsoleCommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.level.DimensionEnum;
import cn.nukkit.level.format.LevelConfig;
import cn.nukkit.level.generator.terra.PNXPlatform;
import cn.nukkit.registry.Registries;
import de.buddelbubi.WorldManager;
import de.buddelbubi.listener.WorldManagerUI;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class GenerateCommand extends SubCommand {

    public GenerateCommand() {
        super("generate");
        this.setAliases(new String[]{
                "generate",
                "gen",
                "create"
        });
    }

    @Override
    public CommandParameter[] getParameters() {

        LinkedList<CommandParameter> parameters = new LinkedList<>();
        parameters.add(CommandParameter.newEnum(this.getName(), this.getAliases()));
        parameters.add(CommandParameter.newType("name", true, CommandParamType.STRING));
        parameters.add(CommandParameter.newEnum("generator", Registries.GENERATOR.getGeneratorList().toArray(String[]::new)));
        parameters.add(CommandParameter.newType("seed", true, CommandParamType.STRING));
        return parameters.toArray(new CommandParameter[parameters.size()]);
    }

    @Override
    public boolean execute(CommandSender sender, String arg1, String[] args) {

        if (!sender.hasPermission("worldmanager.admin") && !sender.hasPermission("worldmanager.generate")) {

            sender.sendMessage(WorldManager.prefix + "§cYou are lacking the permission §e'worldmanager.generate'.");
            return false;

        } else if (args.length >= 2 && args.length <= 4) {

            if (Server.getInstance().getLevelByName(args[1]) == null) {

                String name = args[1];
                String generator = "flat";
                if (args.length >= 3) {
                    List<String> generators = new ArrayList<>(Registries.GENERATOR.getGeneratorList());
                    if (generators.contains(args[2])) {
                        generator = args[2];
                    } else {
                        sender.sendMessage(WorldManager.prefix + "§cThis generator does not exist.");
                        return false;
                    }
                }
                try {
                    Long seed;
                    if(args.length == 4) {
                        String stringSeed = args[3];
                        try {
                            seed = Long.parseLong(stringSeed);
                        } catch (Exception ex) {
                            seed = (long) stringSeed.hashCode();
                        }
                    } else seed = ThreadLocalRandom.current().nextLong();
                    generateNewWorld(name, seed, generator);
                    sender.sendMessage(WorldManager.prefix + "§7The world §8" + name + "§7 got generated.");
                } catch (Exception e) {
                    sender.sendMessage(WorldManager.prefix + "§cSomething went wrong during the world generation.");
                }

            } else sender.sendMessage(WorldManager.prefix + "§cThis world already exist..");

        } else {
            if ((sender.hasPermission("worldmanager.generateui") || sender.hasPermission("worldmanager.admin")) && args.length == 1 && !(sender instanceof ConsoleCommandSender)) {
                WorldManagerUI.openWorldGenUI((Player) sender);
                return false;
            }
            sender.sendMessage(WorldManager.prefix + "§cDo /worldmanager generate [Name] (Generator) {Seed}.");
        }
        return false;
    }

    public static void generateNewWorld(String name, Long seed, String generator) {
        HashMap<Integer, LevelConfig.GeneratorConfig> generatorConfig = new HashMap<>();
        generatorConfig.put(0, new LevelConfig.GeneratorConfig(generator, seed, false, LevelConfig.AntiXrayMode.LOW, true, DimensionEnum.OVERWORLD.getDimensionData(), generator.equals("terra") ? Map.of("pack", "overworld") : Collections.emptyMap()));
        LevelConfig levelConfig = new LevelConfig("leveldb", true, generatorConfig);
        Server.getInstance().generateLevel(name, levelConfig);
    }

}