package niix.dan.commanditem;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.stream.Collectors;

public final class CommandItem extends JavaPlugin {
    public static NamespacedKey key;
    public static CommandItem plugin;

    @Override
    public void onEnable() {
        plugin = this;
        key = new NamespacedKey(this, "cmdi_executor");

        getConfig().options().copyDefaults(true);
        saveDefaultConfig();

        Bukkit.getPluginManager().registerEvents(new EventManager(key), this);

        getCommand("commanditem").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(!sender.hasPermission("CommandItem.cmdi")) return false;

        if(args.length >= 1 && args[0].equalsIgnoreCase("reload")) {
            reloadConfig();
            sender.sendMessage(ChatColor.GRAY+"[CommandItem] Config reloaded!");
            return true;
        }

        if(!(sender instanceof Player)) { return false; }

        Player player = (Player) sender;
        ItemStack item = player.getInventory().getItemInMainHand();

        if(args.length >= 2 && args[0].equalsIgnoreCase("set")) {
            if (item != null && item.getType() != Material.AIR) {
                String idenf = Arrays.stream(args).skip(1).collect(Collectors.joining("_"));
                ItemMeta meta = item.getItemMeta();
                meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, idenf);
                item.setItemMeta(meta);

                sender.sendMessage(ChatColor.GRAY + "[CommandItem] §aSuccess!");
                return true;
            }
            return false;
        }/*
        else

        if(args.length >= 2 && args[0].equalsIgnoreCase("rename") && (sender instanceof Player)) {
            Player player = (Player) sender;
            ItemStack item = player.getInventory().getItemInMainHand();

            if (item != null && item.getType() != Material.AIR) {
                String name = Arrays.stream(args).skip(1).collect(Collectors.joining(" "));
                ItemMeta meta = item.getItemMeta();
                meta.setDisplayName(name.replace("&", "§"));
                item.setItemMeta(meta);

                sender.sendMessage(ChatColor.GRAY + "[CommandItem] §aSuccess!");
            }
        }
        */
        //sender.sendMessage(cmd.getUsage());

        return false;
    }
}
