package de.buddelbubi.utils;

import cn.nukkit.Server;
import cn.nukkit.level.Level;
import cn.nukkit.utils.Config;
import de.buddelbubi.WorldManager;
import de.buddelbubi.api.WorldManagerOption;

import java.io.File;
import java.io.IOException;

public class LoadWorlds {

	public static boolean loaded = false;
	public static void loadWorlds() {
		
		File folder = new File(Server.getInstance().getDataPath() + "worlds/");
		File[] folders = folder.listFiles();
		for(File f : folders) {
			if(!f.isDirectory()) continue;
			
			if(!new File(Server.getInstance().getDataPath() + "worlds/" + f.getName(), "level.dat").exists()) {
				WorldManager.get().getLogger().alert("Unknown folder: " + f.getName() + ", Missing level.dat.");
				continue;
			}
			
			File configfile = new File(Server.getInstance().getDataPath() + "worlds/" + f.getName(), "config.yml");
			
			if(!configfile.exists()) {
				try {
					configfile.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			Config c = new Config(configfile);
			
			String worldname = f.getName().split("/")[f.getName().split("/").length-1];
			
			
			if(!c.exists("version")) 	c.set("version", 0);
			if(c.exists("UseOwnGamemode")) if(!c.getBoolean("UseOwnGamemode")) c.set("Gamemode", 4);
			if(!c.exists("Gamemode")) 	c.set("Gamemode", 4);
			if(!c.exists("fly")) 	c.set("fly", false);
			if(!c.exists("respawnworld")) 	c.set("respawnworld", worldname);
			if(!c.exists("protected"))		c.set("protected", false);
			if(!c.exists("note")) 	c.set("note", "");
			for(WorldManagerOption option : WorldManagerOption.getCustomOptions()) {
				if(!c.exists(option.getKey())) {
					c.set(option.getKey(), option.getValue());
				}
			}
			
			c.save();
			try {
				Cache.initWorld(worldname);
			} catch (Exception e) {
				WorldManager.get().getLogger().error("Could not initialize " + worldname + ". Please message the developer.");
			}

		}
		loaded = true;
	}
	
}
