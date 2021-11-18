package land.face.jobbo;

import com.tealcube.minecraft.bukkit.shade.acf.PaperCommandManager;
import io.pixeloutlaw.minecraft.spigot.config.MasterConfiguration;
import io.pixeloutlaw.minecraft.spigot.config.VersionedConfiguration;
import io.pixeloutlaw.minecraft.spigot.config.VersionedSmartYamlConfiguration;
import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import land.face.jobbo.commands.JobCommand;
import land.face.jobbo.listeners.BuiltInTaskListener;
import land.face.jobbo.listeners.NpcClickListener;
import land.face.jobbo.listeners.SignClickListener;
import land.face.jobbo.managers.JobManager;
import land.face.jobbo.menus.AcceptJobMenu;
import land.face.jobbo.menus.JobsMenu;
import land.face.jobbo.tasks.JobBoardTicker;
import land.face.jobbo.util.JobUtil;
import lombok.Getter;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class JobboPlugin extends JavaPlugin {

  @Getter
  private static JobboApi api;
  @Getter
  public static boolean strifeEnabled, citizensEnabled, waypointerEnabled;

  public static final DecimalFormat INT_FORMAT = new DecimalFormat("#,###,###,###,###");
  public static final DecimalFormat ONE_DECIMAL = new DecimalFormat("#,###,###,###,###.#");

  @Getter
  public Economy economy;

  private JobManager jobManager;

  private MasterConfiguration settings;

  private JobsMenu statusMenu;

  public void onEnable() {
    List<VersionedSmartYamlConfiguration> configurations = new ArrayList<>();
    VersionedSmartYamlConfiguration configYAML;
    configurations.add(configYAML = defaultSettingsLoad("config.yml"));
    VersionedSmartYamlConfiguration templatesYAML = defaultSettingsLoad("templates.yml");

    for (VersionedSmartYamlConfiguration config : configurations) {
      if (config.update()) {
        getLogger().info("Updating " + config.getFileName());
      }
    }

    strifeEnabled = Bukkit.getPluginManager().getPlugin("Strife") != null;
    waypointerEnabled = Bukkit.getPluginManager().getPlugin("Waypointer") != null;
    citizensEnabled = Bukkit.getPluginManager().getPlugin("Citizens") != null;

    settings = MasterConfiguration.loadFromFiles(configYAML);

    jobManager = new JobManager(this);
    jobManager.loadBoards();
    jobManager.loadTemplates(templatesYAML);

    Bukkit.getPluginManager().registerEvents(new SignClickListener(), this);
    Bukkit.getPluginManager().registerEvents(new BuiltInTaskListener(), this);
    if (citizensEnabled) {
      Bukkit.getPluginManager().registerEvents(new NpcClickListener(), this);
    }

    PaperCommandManager commandManager = new PaperCommandManager(this);
    commandManager.registerCommand(new JobCommand(this));

    commandManager.getCommandCompletions()
        .registerCompletion("boards", c -> jobManager.getBoards());
    commandManager.getCommandCompletions()
        .registerCompletion("templates", c -> jobManager.getTemplates());

    setupEconomy();
    if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
      new JobboPlaceholders().register();
    }

    JobBoardTicker boardTicker = new JobBoardTicker();
    boardTicker.runTaskTimer(this, 200L, 20L);

    AcceptJobMenu acceptMenu = new AcceptJobMenu(this);
    AcceptJobMenu.setInstance(acceptMenu);

    statusMenu = new JobsMenu(this);
    JobsMenu.setInstance(statusMenu);

    JobUtil.refreshInstance();

    api = new JobboApi(jobManager);
    Bukkit.getServer().getLogger().info("Jobbo Enabled!");
  }

  public void onDisable() {
    jobManager.saveBoards();
    HandlerList.unregisterAll(this);
    Bukkit.getServer().getScheduler().cancelTasks(this);
    Bukkit.getServer().getLogger().info("Jobbo Disabled!");
  }

  public MasterConfiguration getSettings() {
    return settings;
  }

  private void setupEconomy() {
    if (this.getServer().getPluginManager().getPlugin("Vault") == null) {
      return;
    }
    final RegisteredServiceProvider<Economy> rsp = (RegisteredServiceProvider<Economy>) getServer()
        .getServicesManager().getRegistration((Class) Economy.class);
    if (rsp == null) {
      return;
    }
    economy = rsp.getProvider();
  }

  private VersionedSmartYamlConfiguration defaultSettingsLoad(String name) {
    return new VersionedSmartYamlConfiguration(new File(getDataFolder(), name),
        getResource(name), VersionedConfiguration.VersionUpdateType.BACKUP_AND_UPDATE);
  }

  public JobsMenu getStatusMenu() {
    return statusMenu;
  }

  public JobManager getJobManager() {
    return jobManager;
  }

}