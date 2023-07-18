package me.ilsommo.openpit.commands.subcommands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.ilsommo.openpit.ThePit;

public class Gold implements CommandExecutor
{
	private ThePit main;
	
    public Gold(ThePit main)
    {
        this.main = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String CommandLabel, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Not a player!");
            return true;
        }
        Player p = (Player) sender;
        String name = cmd.getName();
        if (name.equalsIgnoreCase("gold")) {
            if (args[0].equalsIgnoreCase("setloc")) {
                List<Location> l = (List<Location>) main.getConfig().getList("Gold-Locations");
                if (l == null) {
                    l = new ArrayList<Location>(); // Inizializza la lista se è null
                }
                l.add(p.getLocation());
                main.getConfig().set("Gold-Locations", l);
                main.saveConfig();
                sender.sendMessage("Gold Spawn Location Set!");
            }
        }
        return false;
    }
}
 
