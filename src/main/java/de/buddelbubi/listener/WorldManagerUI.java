package de.buddelbubi.listener;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerFormRespondedEvent;
import cn.nukkit.form.element.*;
import cn.nukkit.form.window.FormWindowCustom;
import cn.nukkit.form.window.FormWindowSimple;
import cn.nukkit.level.DimensionEnum;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.LevelConfig;
import cn.nukkit.registry.Registries;
import de.buddelbubi.WorldManager;
import de.buddelbubi.api.World;
import de.buddelbubi.commands.subcommand.GenerateCommand;
import de.buddelbubi.utils.Cache;

import java.util.Collections;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class WorldManagerUI implements Listener {


    public static void openWorldTeleportUI(Player p, String search) {

        FormWindowSimple fw = new FormWindowSimple("§3WorldManager §8- §cTeleportation UI", "§8Teleport to another level using an UI");
        for (Level l : Server.getInstance().getLevels().values()) {
            if (search == null || (search != null && l.getFolderName().toLowerCase().contains(search.toLowerCase())))
                if (p.hasPermission("worldmanager.teleport") || p.hasPermission("worldmanager.teleport." + l.getFolderName()) || p.hasPermission("worldmanager.admin")) {
                    World w = Cache.getWorld(l);
                    String thumbnail = "path::textures/ui/ErrorGlyph_small_hover.png";
                    if (w.getThumbnail().startsWith("path::") || w.getThumbnail().startsWith("url::"))
                        thumbnail = w.getThumbnail();
                    fw.addButton(new ElementButton(l.getFolderName(), new ElementButtonImageData(thumbnail.split("::")[0], thumbnail.split("::")[1])));
                }
        }
        p.showFormWindow(fw);
    }


    public static void openWorldGenUI(Player p) {
        FormWindowCustom fw = new FormWindowCustom("§3WorldManager §8- §cGeneration UI");
        fw.addElement(new ElementInput("Name", "Type in a the worldname"));
        fw.addElement(new ElementInput("Seed", "Leave empty for random seed"));
        fw.addElement(new ElementDropdown("Generator", Registries.GENERATOR.getGeneratorList().stream().toList()));
        fw.addElement(new ElementToggle("Teleport after generation?", false));

        p.showFormWindow(fw);
    }

    @EventHandler
    public void onFormResponse(PlayerFormRespondedEvent e) {

        if (e.getWindow() instanceof FormWindowSimple && e.getResponse() != null) {

            FormWindowSimple fw = (FormWindowSimple) e.getWindow();
            if (fw.getTitle().equals("§3WorldManager §8- §cTeleportation UI")) {
                Level level = Server.getInstance().getLevelByName(fw.getResponse().getClickedButton().getText());
                e.getPlayer().teleport(level.getSafeSpawn());
                e.getPlayer().sendMessage(WorldManager.prefix + "§7You got teleported to §8" + level.getFolderName());
            }

        } else if (e.getWindow() instanceof FormWindowCustom && e.getResponse() != null) {

            FormWindowCustom fw = (FormWindowCustom) e.getWindow();
            if (fw.getTitle().equals("§3WorldManager §8- §cGeneration UI")) {
                if (fw.getResponse().getInputResponse(0).isEmpty()) {
                    e.getPlayer().sendMessage(WorldManager.prefix + "§cYou can't leave the name blank.");
                    return;
                }
                if (!fw.getResponse().getInputResponse(1).isEmpty()) {
                    try {
                        Long.parseLong(fw.getResponse().getInputResponse(1));
                    } catch (Exception e2) {
                        e.getPlayer().sendMessage(WorldManager.prefix + "§cThe seed was not a number!");
                        return;
                    }

                }
                String name = fw.getResponse().getInputResponse(0);
                String generator = fw.getResponse().getDropdownResponse(2).getElementContent();
                long seed = (fw.getResponse().getInputResponse(1).isEmpty()) ? ThreadLocalRandom.current().nextLong() : Long.parseLong(fw.getResponse().getInputResponse(1));
                if (Server.getInstance().getLevelByName(name) != null) {
                    e.getPlayer().sendMessage(WorldManager.prefix + "§cThis world already exist..");
                    return;
                }

                GenerateCommand.generateNewWorld(name, seed, generator);

                e.getPlayer().sendMessage(WorldManager.prefix + "§7The world §8" + name + "§7 got generated.");
                if (fw.getResponse().getToggleResponse(3))
                    e.getPlayer().teleport(Server.getInstance().getLevelByName(name).getSafeSpawn());
            }
        }
    }

}
