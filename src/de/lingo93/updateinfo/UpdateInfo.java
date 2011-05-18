package de.lingo93.updateinfo;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.nijikokun.bukkit.Permissions.Permissions;
import com.nijiko.permissions.PermissionHandler;

/**
 * UpdateInfo for Bukkit
 * @author Lingo93
 */
public class UpdateInfo extends JavaPlugin {

	private final UpdateInfoPlayerListener playerListener = new UpdateInfoPlayerListener(this);	
	public static Logger log = Logger.getLogger("Minecraft");
	public String rsf;
	public static PermissionHandler permissionHandler;
	public static boolean permFound;
	
	/**
     * disable the Plugin
     */
	@Override
	public void onDisable() {
		log.info("[UpdateInfo] version " + this.getDescription().getVersion() + " disabled.");		
	}

	/**
     * start Plugin
     */
	@Override
	public void onEnable() {
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvent(Event.Type.PLAYER_JOIN, playerListener, Priority.Normal, this);
		setupPermissions();
		log.info("[UpdateInfo] version " + this.getDescription().getVersion() + " enabled.");
	}
	
	/**
     * handles Commands
     */
	@Override
	public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args){
		String commandName = command.getName().toLowerCase();
		Player player = (Player) sender;
		
		if (commandName.equalsIgnoreCase("updateinfo")){
			player.sendMessage(ChatColor.GOLD+"UPDATE INFO - Updates:");
			String[] spli = rsf.split(";");
			for(int i = 0; spli.length > i; i++){
				player.sendMessage(ChatColor.GREEN+spli[i]);
			}
			return true;
		}
		return false;
	}
	
	/**
     * checks for new versions
     */
	public void checkOverview(Player player){
		boolean upd = false;
		String allVersions = "";
		String received = "";
		String buffer;
		Plugin[] plugins = this.getServer().getPluginManager().getPlugins();
		boolean first = true;
		for(int i = 0; i < plugins.length; i++){
			if(first){
				allVersions = plugins[i].getDescription().getName()+":"+plugins[i].getDescription().getVersion();
				first = false;
			}else allVersions += ";"+plugins[i].getDescription().getName()+":"+plugins[i].getDescription().getVersion();
			
			if(allVersions.length() > 400){
				buffer = sendData(allVersions);
				if(!buffer.equals("false")) {
					received += buffer;
					upd = true;
				}
				first = true;
			}
		}
		buffer = sendData(allVersions);
		if(!buffer.equals("false")) {
			received += buffer;
			upd = true;
		}
		if(upd) player.sendMessage(ChatColor.RED+"New Updates available! /updateinfo for details.");
		rsf = received;
	}
	
	/**
     * sends data to database-server
     */
	public String sendData(String send){
		String received = null;
		try {
			//http://plugins.tulano.net/lingo93/updateinfo/get_versions_java.php returns the newest available version
			URL adress = new URL( "http://plugins.tulano.net/lingo93/updateinfo/get_versions_java.php?s="+send );
			InputStream in = adress.openStream();
			received = new Scanner( in ).useDelimiter( "\\Z" ).next();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return received;
	}
	
	/**
	 * load Permissions plugin
	 */
    private void setupPermissions() {
        Plugin permissionsPlugin = this.getServer().getPluginManager().getPlugin("Permissions");        

        if (permissionHandler == null) {
            if (permissionsPlugin != null) {
                permissionHandler = ((Permissions)permissionsPlugin).getHandler();
                log.info("[UpdateInfo] Permission enabled");
                permFound = true;
            } else {
                log.info("[UpdateInfo] Permission system not detected, defaulting to OP");
                permFound = false;
            }
        }
    }
    
    /**
	 * checks Permissions
	 */
    public static boolean perm(Player player, String perm){
    	if (permFound) {
    		return permissionHandler.has((Player)player, perm);
        } else {
            return player.isOp();
        }
    }

}
