package de.lingo93.updateinfo;

import org.bukkit.entity.Player;
import org.bukkit.event.player.*;

/**
 * UpdateInfo for Bukkit
 * @author Lingo93
 */
public class UpdateInfoPlayerListener extends PlayerListener {

	private final UpdateInfo plugin;
	
	public UpdateInfoPlayerListener(UpdateInfo instance) {
		plugin = instance;
	}
	
	public void onPlayerJoin(PlayerJoinEvent event) {
		if(UpdateInfo.perm((Player)event.getPlayer(), "updateinfo.admin"))
		plugin.checkOverview(event.getPlayer());
	}
}
