package niix.dan.commanditem;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

public class EventManager implements Listener {
    private final NamespacedKey key;

    @EventHandler(priority = EventPriority.HIGHEST)
    public void OnClick(PlayerInteractEvent e) {
        CommandItem pl = CommandItem.getPlugin(CommandItem.class);

        Player p = e.getPlayer();
        Action a = e.getAction();

        if ((a == Action.PHYSICAL) || (e.getItem() == null) || !e.getItem().hasItemMeta() || e.getItem().getType() == Material.AIR) return;

        ConfigurationSection sec = pl.getConfig();
        for (String item: sec.getKeys(false)) {
            String req_action = pl.getConfig().getString(item + ".Action");
            String itemPermission = pl.getConfig().getString(item + ".Permission", "none");

            if (e.getItem().getItemMeta().getPersistentDataContainer().has(key, PersistentDataType.STRING) &&
                    e.getItem().getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.STRING).equals(pl.getConfig().getString(item + ".ItemIdentifier", "cmdi_default")) &&
                    (itemPermission.equalsIgnoreCase("none") || p.hasPermission(itemPermission))) {

                if (req_action.equalsIgnoreCase("BOTH") ||
                        (req_action.equalsIgnoreCase("R_CLICK") && a == Action.RIGHT_CLICK_AIR) ||
                        (req_action.equalsIgnoreCase("L_CLICK") && a == Action.LEFT_CLICK_AIR)) {


                    ExecuteCustomItem(pl, p, item);
                }
            }
        }
    }

    public void ExecuteCustomItem(CommandItem pl, Player p, String item) {
        if (pl.getConfig().getBoolean(item + ".Consumable")) p.getItemInHand().setAmount(p.getItemInHand().getAmount() - 1);

        (new BukkitRunnable() {
            public void run() {
                for (String cmd: pl.getConfig().getStringList(item + ".Commands.Player")) {
                    Bukkit.getServer().dispatchCommand((CommandSender) p, cmd.replace("%player%", p.getName()));
                }

                for (String cmd: pl.getConfig().getStringList(item + ".Commands.Server")) {
                    Bukkit.getServer().dispatchCommand((CommandSender) Bukkit.getConsoleSender(), cmd.replace("%player%", p.getName()));
                }

                if (pl.getConfig().getBoolean(item + ".Extras.CustomTitle.Enabled")) {
                    p.sendTitle(
                            pl.getConfig().getString(item + ".Extras.CustomTitle.Title").replace("&", "§"),
                            pl.getConfig().getString(item + ".Extras.CustomTitle.Subtitle").replace("&", "§")
                    );
                }

                if (pl.getConfig().getBoolean(item + ".Extras.Sounds.Enabled")) {
                    for (String som: pl.getConfig().getStringList(item + ".Extras.Sounds.List")) {
                        String[] sm = som.split(":");

                        (new BukkitRunnable() {
                            public void run() {
                                p.playSound(p.getLocation(), sm[0].toLowerCase(), 1.0f, Float.parseFloat(sm[1]));
                            }
                        }).runTaskLater(pl, Long.parseLong(sm[2]));
                    }
                }

                if (pl.getConfig().getBoolean(item + ".Extras.Chat.Enabled")) {
                    for (String msg: pl.getConfig().getStringList(item + ".Extras.Chat.Messages")) {
                        msg = msg.replace("&", "§");
                        msg = msg.replace("%player%", p.getName());

                        p.sendMessage(msg);
                    }
                }

                if (pl.getConfig().getBoolean(item + ".Extras.ActionBar.Enabled")) {
                    p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(pl.getConfig().getString(item + ".Extras.ActionBar.Message").replace("&", "§")));
                }
            }
        }).runTask(pl);
    }


    public EventManager(NamespacedKey key) {
        this.key = key;
    }
}
