package me.derflash.plugins.villagerspecialperms;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.util.Map;

public class VillagerSpecialPerms extends JavaPlugin implements Listener {
	
    private Plugin shopkeepersPlugin;
    private Map<?, ?> activeShopkeepers;

    public void onDisable() {
    }

    public void onEnable() {
    	connectWithShopkeepers();
    	
        getServer().getPluginManager().registerEvents(this, this);
    }
    
    @EventHandler
    public void onPlayerVillagerBuy(PlayerInteractEntityEvent event)
    {
    	if (!(event.getPlayer() instanceof Player)) {
    		return;
    	}
    	
        Player player = event.getPlayer();
        Entity entity = event.getRightClicked();
    	
        if(entity.getType() != EntityType.VILLAGER) {
        	return;
        }
        
    	if (isShopKeeper(entity)) {
    		if (!player.hasPermission("vsperms.buy.shopkeeper")) {
                event.setCancelled(true);
        	}
    		
    	} else {
        	if(!player.hasPermission("vsperms.buy.default")) {
                event.setCancelled(true);
            }
    	}

    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event)
    {
    	if (!(event.getDamager() instanceof Player)) {
    		return;
    	}
    	
        Player player = (Player) event.getDamager();
        Entity entity = event.getEntity();

        if(entity.getType() != EntityType.VILLAGER) {
        	return;
        }
        
    	if (isShopKeeper(entity)) {
    		if (!player.hasPermission("vsperms.hurt.shopkeeper")) {
                event.setCancelled(true);
        	}
    		
    	} else {
        	if(!player.hasPermission("vsperms.hurt.default")) {
                event.setCancelled(true);
            }
    	}
    	
    }
    
    
    private void connectWithShopkeepers()
    {
        shopkeepersPlugin = getServer().getPluginManager().getPlugin("Shopkeepers");
        if(shopkeepersPlugin != null)
        {
            getLogger().info("Shopkeepers has been detected.");
            try
            {
                Field activeShopkeepersField = shopkeepersPlugin.getClass().getDeclaredField("activeShopkeepers");
                activeShopkeepersField.setAccessible(true);
                activeShopkeepers = (Map<?, ?>)activeShopkeepersField.get(shopkeepersPlugin);
                getLogger().info("Successfully connected to Shopkeepers.");
            }
            catch(Exception e)
            {
                getLogger().info("Could not properly connect with Shopkeepers - Incorrect version?");
            }
        }
    }
    
    private boolean isShopKeeper(Entity entity) {
        return (activeShopkeepers != null && activeShopkeepers.containsKey(Integer.valueOf(entity.getEntityId())));
    }
    
}

