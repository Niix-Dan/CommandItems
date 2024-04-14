package niix.dan.commanditem;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public final class CommandItem extends JavaPlugin {
    public static CommandItem plugin;

    @Override
    public void onEnable() {
        plugin = this;

        getConfig().options().copyDefaults(true);
        saveDefaultConfig();

        Bukkit.getPluginManager().registerEvents(new EventManager(), this);

        getCommand("commanditem").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(!sender.hasPermission("CommandItem.cmi")) return true;
        if(args.length >= 1 && args[0].equalsIgnoreCase("reload")) {
            reloadConfig();
            sender.sendMessage(ChatColor.GRAY+"[CommandItem] Config reloaded!");
            return true;
        }
        sender.sendMessage(cmd.getUsage());

        return false;
    }
}
