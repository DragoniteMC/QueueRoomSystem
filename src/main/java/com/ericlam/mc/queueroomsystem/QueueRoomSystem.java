package com.ericlam.mc.queueroomsystem;

import com.ericlam.mc.bungee.dnmc.config.YamlManager;
import com.ericlam.mc.bungee.dnmc.main.DragoniteMC;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

public class QueueRoomSystem extends Plugin implements Listener {

    private YamlManager yamlManager;
    private QueueRoomManager queueRoomManager;

    private QueueRoomConfig config;

    @Override
    public void onEnable() {
        super.onEnable();
        yamlManager = DragoniteMC.getAPI().getConfigFactory(this)
                .register(QueueRoomConfig.class)
                .register(QueueRoomMessage.class)
                .dump();
        config = yamlManager.getConfigAs(QueueRoomConfig.class);
        var msg = yamlManager.getConfigAs(QueueRoomMessage.class);
        queueRoomManager = new QueueRoomManager(new QueueRoomEventBus(config), this);
        getProxy().getPluginManager().registerListener(this, this);
        DragoniteMC.getAPI().getCommandRegister().registerCommand(this,
                new QueueRoomCommand(config, msg, queueRoomManager));
    }


    @Override
    public void onDisable() {
        super.onDisable();
    }


    public YamlManager getYamlManager() {
        return yamlManager;
    }

    public QueueRoomManager getQueueRoomManager() {
        return queueRoomManager;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerSwitch(ServerConnectEvent e) {
        Server server = e.getPlayer().getServer();
        if (server == null) return;
        // 在 queueing 的伺服器中
        if (config.servers.containsKey(server.getInfo().getName())) {
            queueRoomManager.removeQueue(e.getPlayer());
        }
    }
}
