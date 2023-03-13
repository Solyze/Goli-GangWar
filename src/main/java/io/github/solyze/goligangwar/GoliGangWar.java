package io.github.solyze.goligangwar;

import io.github.solyze.goligangwar.command.GangCommand;
import io.github.solyze.goligangwar.command.GangWarCommand;
import io.github.solyze.goligangwar.command.PredictionCommand;
import io.github.solyze.goligangwar.listener.*;
import io.github.solyze.goligangwar.manager.*;
import io.github.solyze.goligangwar.utility.FileManager;
import io.github.solyze.goligangwar.utility.PAPIExpansion;
import lombok.Getter;
import net.luckperms.api.LuckPerms;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public final class GoliGangWar extends JavaPlugin {

    /*
     * TODO:
     *  - Clear potion effects on match end etc.
     */

    @Getter
    private static GoliGangWar instance;
    @Getter
    private static String prefix = "&8▎ &6&lGang&e&lWar &8▏ &r";
    private FileManager configFM;
    private FileManager gangsFM;
    private FileManager kitFM;
    private FileManager spawnsFM;
    private FileManager supporterBlocksFM;
    private FileManager barriersFM;
    private FileManager predictionsFM;
    private GangManager gangManager;
    private KitManager kitManager;
    private SpawnManager spawnManager;
    private MatchManager matchManager;
    private LuckPerms luckPerms;
    private BarrierManager barrierManager;
    private PredictionManager predictionManager;

    @Override
    public void onEnable() {
        long start = System.currentTimeMillis();
        instance = this;
        findDependencies(start);
        loadConfigurations(start);
        initializeManagers(start);
        registerListeners(start);
        loadCommands(start);
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) new PAPIExpansion().register();
        getLogger().info(String.format("Successfully enabled. (Took %sms)", System.currentTimeMillis() - start));
    }

    private void findDependencies(long start) {
        getLogger().info("Finding dependencies...");
        RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
        if (provider != null) {
            luckPerms = provider.getProvider();
        } else {
            getLogger().info("LuckPerms was not found (v5.4)!");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        getLogger().info(String.format("Found dependencies. (Took %sms)", System.currentTimeMillis() - start));
    }

    private void loadCommands(long start) {
        getLogger().info("Loading commands...");
        new GangCommand();
        new GangWarCommand();
        new PredictionCommand();
        getLogger().info(String.format("Loaded commands. (Took %sms)", System.currentTimeMillis() - start));
    }

    private void initializeManagers(long start) {
        getLogger().info("Initializing managers...");
        gangManager = new GangManager();
        kitManager = new KitManager();
        spawnManager = new SpawnManager();
        barrierManager = new BarrierManager();
        matchManager = new MatchManager();
        predictionManager = new PredictionManager();
        getLogger().info(String.format("Initialized managers. (Took %sms)", System.currentTimeMillis() - start));
    }

    private void registerListeners(long start) {
        getLogger().info("Registering listeners...");
        new JoinListener();
        new ServerListListener();
        new SpawnListener();
        new AnnoyingListener();
        new SupporterListener();
        new DeathQuitListener();
        new DamageListener();
        new RoundListener();
        new MenuListener();
        getLogger().info(String.format("Registered listeners. (Took %sms)", System.currentTimeMillis() - start));
    }

    private void loadConfigurations(long start) {
        getLogger().info("Loading configurations...");
        if (configFM == null) {
            configFM = new FileManager(this, "config", getDataFolder().getAbsolutePath());
            prefix = configFM.getConfiguration().getString("prefix");
        } else configFM.reload();
        if (gangsFM == null) {
            gangsFM = new FileManager(this, "gangs", getDataFolder().getAbsolutePath());
        } else gangsFM.reload();
        if (kitFM == null) {
            kitFM = new FileManager(this, "kit", getDataFolder().getAbsolutePath());
        } else kitFM.reload();
        if (spawnsFM == null) {
            spawnsFM = new FileManager(this, "spawns", getDataFolder().getAbsolutePath());
        } else spawnsFM.reload();
        if (supporterBlocksFM == null) {
            supporterBlocksFM = new FileManager(this, "supporter_blocks", getDataFolder().getAbsolutePath());
        } else supporterBlocksFM.reload();
        if (barriersFM == null) {
            barriersFM = new FileManager(this, "barriers", getDataFolder().getAbsolutePath());
        } else barriersFM.reload();
        if (predictionsFM == null) {
            predictionsFM = new FileManager(this, "predictions", getDataFolder().getAbsolutePath());
        } else predictionsFM.reload();
        getLogger().info(String.format("Configurations loaded. (Took %sms)", System.currentTimeMillis() - start));
    }
}
