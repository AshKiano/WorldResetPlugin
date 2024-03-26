package com.ashkiano.worldresetplugin;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class WorldResetPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        this.getLogger().info("Thank you for using the WorldResetPlugin plugin! If you enjoy using this plugin, please consider making a donation to support the development. You can donate at: https://donate.ashkiano.com");
        Metrics metrics = new Metrics(this, 20956);
    }

    @Override
    public void onDisable() {
        getLogger().info("WorldResetPlugin disabled.");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("resetworld") && args.length > 0) {
            String worldName = args[0];

            if (sender instanceof Player && !sender.hasPermission("worldreset.use")) {
                sender.sendMessage("You do not have permission to use this command.");
                return true;
            }

            World world = Bukkit.getWorld(worldName);
            if (world == null) {
                sender.sendMessage("World " + worldName + " does not exist.");
                return true;
            }

            if (deleteWorld(world)) {
                sender.sendMessage("World " + worldName + " has been removed.");

                Bukkit.broadcastMessage("Server is restarting...");
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "restart");

            } else {
                sender.sendMessage("Failed to remove world " + worldName + ".");
            }

            return true;
        }

        return false;
    }

    private boolean deleteWorld(World world) {
        if (world == null) return false;

        world.getPlayers().forEach(player -> player.teleport(Bukkit.getWorlds().get(0).getSpawnLocation()));

        Bukkit.unloadWorld(world, false);

        return deleteDirectory(world.getWorldFolder());
    }

    private boolean deleteDirectory(java.io.File path) {
        if (path.exists()) {
            java.io.File[] files = path.listFiles();
            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory()) {
                    deleteDirectory(files[i]);
                } else {
                    files[i].delete();
                }
            }
        }
        return (path.delete());
    }
}
