package me.flame.notepad;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import net.md_5.bungee.api.ChatColor;

public class Notepad extends JavaPlugin {

	public void onEnable() {
		loadConfig();
	}
	
	public void loadConfig() {
		getConfig().options().copyDefaults(true);
		saveConfig();
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender instanceof Player)) {
			sender.sendMessage("[" + ChatColor.DARK_PURPLE + "Notepad" + ChatColor.WHITE +"] You can't run commands from console");
			return true;
		}

		
		
		Player player = (Player) sender;
		UUID senderuuid = player.getUniqueId();	
		
			if(args.length == 0 || args[0].equalsIgnoreCase("list")) {
				if(!sender.hasPermission("notepad.list") && !sender.hasPermission("notepad.player") && !sender.hasPermission("notepad.*")) {
					sender.sendMessage("[" + ChatColor.DARK_PURPLE + "Notepad" + ChatColor.WHITE +"] You don't have permission to list notes");
					return true;
				}
				
				if(args.length ==2) {
					String shared = args[1];
					Player p = Bukkit.getPlayer(shared);
					UUID pUUID = p.getUniqueId();			
					List<String> senderlist = getConfig().getStringList(pUUID.toString() +".shared");
					for (String sharedplayer : senderlist) {
						if(sharedplayer.equalsIgnoreCase(senderuuid.toString()) || sharedplayer.equalsIgnoreCase("*")) {	
							List<String> list = getConfig().getStringList(pUUID.toString() +".notes");
							sender.sendMessage(ChatColor.BLACK + "" + ChatColor.STRIKETHROUGH
									+ "                                                                                ");
							sender.sendMessage(centerText(p.getName() +"'s Notes"));
							int count = 1;
							for (String note : list) {
								sender.sendMessage("(" + count + ") " + "- " + note);
								count ++;
							}
							sender.sendMessage(ChatColor.BLACK + "" + ChatColor.STRIKETHROUGH
									+ "                                                                                ");
							return true;
						}
					}
					sender.sendMessage("[" + ChatColor.DARK_PURPLE + "Notepad" + ChatColor.WHITE +"] You can't see these notes");
					
					return true;
				}
				
				sender.sendMessage(ChatColor.BLACK + "" + ChatColor.STRIKETHROUGH
						+ "                                                                                ");
				sender.sendMessage(centerText("Notes"));
				List<String> list = getConfig().getStringList(senderuuid.toString() + ".notes");
				int count = 1;
				for (String note : list) {
					sender.sendMessage("(" + count + ") " + "- " + note);
					count ++;
				}
				sender.sendMessage(ChatColor.BLACK + "" + ChatColor.STRIKETHROUGH
						+ "                                                                                ");
				return true;
			}
			
			if(args[0].equalsIgnoreCase("add")) {
				if(!sender.hasPermission("notepad.add") && !sender.hasPermission("notepad.player") && !sender.hasPermission("notepad.*")) {
					sender.sendMessage("[" + ChatColor.DARK_PURPLE + "Notepad" + ChatColor.WHITE +"] You don't have permission to add notes");
					return true;
				}
				
				if(!(args.length > 1)) {
					sender.sendMessage("[" + ChatColor.DARK_PURPLE + "Notepad" + ChatColor.WHITE +"] Incorrect usage /notes add <note>");
					return true;
				}
				List<String> notes = new ArrayList<String>();
				List<String> notesbefore = getConfig().getStringList(senderuuid.toString() + ".notes");
				String pnote = "";
				for(int i = 1; i < args.length; i++) {
					pnote = pnote + args[i] + " ";
				}
				if(getConfig().get(senderuuid.toString()) == null) { 
					notes.add(pnote);
					getConfig().set(senderuuid.toString() + ".notes", notes);
					saveConfig();
					sender.sendMessage("[" + ChatColor.DARK_PURPLE + "Notepad" + ChatColor.WHITE +"] Added " + pnote + "to notes");
					return true;
				}else {
					for (String note : notesbefore) {
						notes.add(note);
					}
					notes.add(pnote);
					getConfig().set(senderuuid.toString() + ".notes", notes);
					saveConfig();
					sender.sendMessage("[" + ChatColor.DARK_PURPLE + "Notepad" + ChatColor.WHITE +"] Added " + pnote + "to notes");
					return true;
				}
			}
			if(args[0].equalsIgnoreCase("remove")) {
				if(!sender.hasPermission("notepad.remove") && !sender.hasPermission("notepad.player") && !sender.hasPermission("notepad.*")) {
					sender.sendMessage("[" + ChatColor.DARK_PURPLE + "Notepad" + ChatColor.WHITE +"] You don't have permission to remove notes");
					return true;
				}
				
				if(!(args.length > 1)) {
					sender.sendMessage("[" + ChatColor.DARK_PURPLE + "Notepad" + ChatColor.WHITE +"] Incorrect usage /notes remove <number of note>");
					return true;
				}
				int index = Integer.valueOf(args[1])-1;
				List<String> notes = getConfig().getStringList(senderuuid.toString() + ".notes");
				if(notes.isEmpty()) {
					sender.sendMessage("[" + ChatColor.DARK_PURPLE + "Notepad" + ChatColor.WHITE +"] You first need to add a note");
					return true;
				}
				if(notes.size() < index+1 || index+1 < 1) {
					sender.sendMessage("[" + ChatColor.DARK_PURPLE + "Notepad" + ChatColor.WHITE +"] That note does not exist use /notes to list notes");
					return true;
				}
				sender.sendMessage("[" + ChatColor.DARK_PURPLE + "Notepad" + ChatColor.WHITE +"] " + notes.get(index)+"has been removed from notes");
				notes.remove(notes.get(index));
				getConfig().set(senderuuid.toString() + ".notes", notes);
				saveConfig();
				return true;
			}
			if(args[0].equalsIgnoreCase("share")) {
				if(!sender.hasPermission("notepad.share") && !sender.hasPermission("notepad.player") && !sender.hasPermission("notepad.*")) {
					sender.sendMessage("[" + ChatColor.DARK_PURPLE + "Notepad" + ChatColor.WHITE +"] You don't have permission to share notes");
					return true;
				}
				if(!(args.length > 1)) {
					sender.sendMessage("[" + ChatColor.DARK_PURPLE + "Notepad" + ChatColor.WHITE +"] Incorrect usage /notes share <player>");
					return true;
				}
				String shared = args[1];
				OfflinePlayer p = Bukkit.getOfflinePlayer(shared);
				List<String> listshared = new ArrayList<String>();
				List<String> sharedbefore = getConfig().getStringList(senderuuid.toString() + ".shared");
				


				
				if(shared.equalsIgnoreCase("*")) {
					if(getConfig().get(senderuuid.toString()) == null) { 
						getConfig().set(senderuuid.toString() + ".shared", "*");
						saveConfig();
						sender.sendMessage("[" + ChatColor.DARK_PURPLE + "Notepad" + ChatColor.WHITE +"] Everyone can now see your notes");
						return true;
						
					}else {
						for (String sharedplayer : sharedbefore) {
							listshared.add(sharedplayer);
						}
						listshared.add("*");
						getConfig().set(senderuuid.toString() + ".shared", listshared);
						saveConfig();
						sender.sendMessage("[" + ChatColor.DARK_PURPLE + "Notepad" + ChatColor.WHITE +"] Everyone can now see your notes");
						return true;
					}
				}

				
				UUID pUUID = p.getUniqueId();
				if(getConfig().get(senderuuid.toString()) == null) { 
					getConfig().set(senderuuid.toString() + ".shared", pUUID.toString());
					saveConfig();
					sender.sendMessage("[" + ChatColor.DARK_PURPLE + "Notepad" + ChatColor.WHITE +"] Added " + p.getName() + " to players who can see your notes.");
					return true;
				}else {
					
					for (String sharedplayer : sharedbefore) {
						listshared.add(sharedplayer);
					}
					listshared.add(pUUID.toString());
					getConfig().set(senderuuid.toString() + ".shared", listshared);
					saveConfig();
					sender.sendMessage("[" + ChatColor.DARK_PURPLE + "Notepad" + ChatColor.WHITE +"] Added " + p.getName() + " to players who can see your notes.");
					return true;
				}
			}

			if(args[0].equalsIgnoreCase("unshare")) {
				if(!sender.hasPermission("notepad.unshare") && !sender.hasPermission("notepad.player") && !sender.hasPermission("notepad.*")) {
						sender.sendMessage("[" + ChatColor.DARK_PURPLE + "Notepad" + ChatColor.WHITE +"] You don't have permission to remove shared players");
						return true;
					}
					
					if(!(args.length > 1)) {
						sender.sendMessage("[" + ChatColor.DARK_PURPLE + "Notepad" + ChatColor.WHITE +"] Incorrect usage /notes unshare <number of shared player>");
						return true;
					}
					int index = Integer.valueOf(args[1])-1;
					List<String> sharedplayers = getConfig().getStringList(senderuuid.toString() + ".shared");
					if(sharedplayers.isEmpty()) {
						sender.sendMessage("[" + ChatColor.DARK_PURPLE + "Notepad" + ChatColor.WHITE +"] You first need to add a shared player");
						return true;
					}
					if(sharedplayers.size() < index+1 || index+1 < 1) {
						sender.sendMessage("[" + ChatColor.DARK_PURPLE + "Notepad" + ChatColor.WHITE +"] That shared player does not exist use /notes shared to list shared players");
						return true;
					}
					if(sharedplayers.get(index).equalsIgnoreCase("*")) {
						sender.sendMessage("[" + ChatColor.DARK_PURPLE + "Notepad" + ChatColor.WHITE +"] Notes can no longer be accessed by everyone" );
						sharedplayers.remove(sharedplayers.get(index));
						getConfig().set(senderuuid.toString() + ".shared", sharedplayers);
						saveConfig();
						return true;
					}
					OfflinePlayer pname = Bukkit.getOfflinePlayer(UUID.fromString(sharedplayers.get(index)));
					sender.sendMessage("[" + ChatColor.DARK_PURPLE + "Notepad" + ChatColor.WHITE +"] " +pname.getName()+" has been removed from shared players");
					sharedplayers.remove(sharedplayers.get(index));
					getConfig().set(senderuuid.toString() + ".shared", sharedplayers);
					saveConfig();
					return true;
			}
	
				if(args[0].equalsIgnoreCase("shared")) {
					
					if(!sender.hasPermission("notepad.shared") && !sender.hasPermission("notepad.player") && !sender.hasPermission("notepad.*")) {
						sender.sendMessage("[" + ChatColor.DARK_PURPLE + "Notepad" + ChatColor.WHITE +"] You don't have permission to list shared players.");
						return true;
					}
					sender.sendMessage(ChatColor.BLACK + "" + ChatColor.STRIKETHROUGH
							+ "                                                                                ");
					sender.sendMessage(centerText("Shared players"));
					List<String> list = getConfig().getStringList(senderuuid.toString() + ".shared");
					int count = 1;
					for (String sharedplayer : list) {
						if(sharedplayer.equalsIgnoreCase("*")) {
							sender.sendMessage("(" + count + ") " + "- * (Everyone)");
							count ++;
						}else {
							OfflinePlayer sharedPlayer = Bukkit.getOfflinePlayer(UUID.fromString(sharedplayer));
							sender.sendMessage("(" + count + ") " + "- " + sharedPlayer.getName());
							count ++;
						}
					}
					sender.sendMessage(ChatColor.BLACK + "" + ChatColor.STRIKETHROUGH
							+ "                                                                                ");
					return true;
				}
				
				if (args[0].equalsIgnoreCase("reload")) {
					if (!sender.hasPermission("notepad.reload") && !sender.hasPermission("notepad.*")) {
						sender.sendMessage("[" + ChatColor.DARK_PURPLE + "Notepad" + ChatColor.WHITE +"] You don't have permission to reload the config.");
						return true;
					}

					sender.sendMessage("[" + ChatColor.DARK_PURPLE + "Notepad" + ChatColor.WHITE +"] Reloading the plugin...");
					reloadConfig();
					sender.sendMessage("[" + ChatColor.DARK_PURPLE + "Notepad" + ChatColor.WHITE +"] Plugin reloaded");
					return true;
				}
				if(args[0].equalsIgnoreCase("help")) {
					if(!sender.hasPermission("notepad.help") && !sender.hasPermission("notepad.player") && !sender.hasPermission("notepad.*")) {
						sender.sendMessage("[" + ChatColor.DARK_PURPLE + "Notepad" + ChatColor.WHITE +"] You don't have permission to read the help page.");
						return true;
					}
					sender.sendMessage(ChatColor.LIGHT_PURPLE + "" + ChatColor.STRIKETHROUGH
							+ "                                                                                ");
					sender.sendMessage(centerText(ChatColor.WHITE + "Notepad"));
					sender.sendMessage(centerText(ChatColor.GRAY + "Version 1.2"));
					sender.sendMessage("");
					sender.sendMessage(centerText(ChatColor.WHITE + "Commands"));
					sender.sendMessage(centerText(ChatColor.LIGHT_PURPLE + "/notes | /notes list"));
					sender.sendMessage(centerText(ChatColor.DARK_PURPLE + "Usage: /notes list <player> to view shared notes"));
					sender.sendMessage(centerText(ChatColor.DARK_PURPLE + "Displays your notes or shared notes"));
					sender.sendMessage("");
					sender.sendMessage(centerText(ChatColor.LIGHT_PURPLE + "/notes add"));
					sender.sendMessage(centerText(ChatColor.DARK_PURPLE + "Usage: /notes add <note>"));
					sender.sendMessage(centerText(ChatColor.DARK_PURPLE + "Add a note to your list"));
					sender.sendMessage("");
					sender.sendMessage(centerText(ChatColor.LIGHT_PURPLE + "/notes remove"));
					sender.sendMessage(centerText(ChatColor.DARK_PURPLE + "Usage: /notes remove <note number>"));
					sender.sendMessage(centerText(ChatColor.DARK_PURPLE + "Remove a note from your notes"));
					sender.sendMessage("");
					sender.sendMessage(centerText(ChatColor.LIGHT_PURPLE + "/notes shared"));
					sender.sendMessage(centerText(ChatColor.DARK_PURPLE + "Displays the players who can see your notes"));
					sender.sendMessage("");
					sender.sendMessage(centerText(ChatColor.LIGHT_PURPLE + "/notes share"));
					sender.sendMessage(centerText(ChatColor.DARK_PURPLE + "Usage: /notes share <player>"));
					sender.sendMessage(centerText(ChatColor.DARK_PURPLE + "Add a player to the list of players who can see your notes"));
					sender.sendMessage("");
					sender.sendMessage(centerText(ChatColor.LIGHT_PURPLE + "/notes unshare"));
					sender.sendMessage(centerText(ChatColor.DARK_PURPLE + "Usage: /notes unshare <player>"));
					sender.sendMessage(centerText(ChatColor.DARK_PURPLE + "Remove a player from the list of players who can see your notes"));
					sender.sendMessage("");
					sender.sendMessage(centerText(ChatColor.LIGHT_PURPLE + "/notes reload"));
					sender.sendMessage(centerText(ChatColor.DARK_PURPLE + "Reloads the configuration file"));
					sender.sendMessage("");
					sender.sendMessage(ChatColor.LIGHT_PURPLE + "" + ChatColor.STRIKETHROUGH
							+ "                                                                                ");
					return true;
				}
		
		sender.sendMessage("[" + ChatColor.DARK_PURPLE + "Notepad" + ChatColor.WHITE +"] Unknown command");
		return false;
		
	}

	private String centerText(String text) {
		int maxWidth = 80, spaces = (int) Math.round((maxWidth - 1.4 * ChatColor.stripColor(text).length()) / 2);
		return StringUtils.repeat(" ", spaces) + text;
	}
}
