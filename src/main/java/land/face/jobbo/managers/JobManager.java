package land.face.jobbo.managers;

import com.tealcube.minecraft.bukkit.facecore.utilities.AdvancedActionBarUtil;
import com.tealcube.minecraft.bukkit.facecore.utilities.MessageUtils;
import com.tealcube.minecraft.bukkit.shade.google.gson.JsonArray;
import com.tealcube.minecraft.bukkit.shade.google.gson.JsonElement;
import io.pixeloutlaw.minecraft.spigot.config.VersionedSmartYamlConfiguration;
import io.pixeloutlaw.minecraft.spigot.garbage.ListExtensionsKt;
import io.pixeloutlaw.minecraft.spigot.garbage.StringExtensionsKt;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import land.face.jobbo.JobboPlugin;
import land.face.jobbo.data.Job;
import land.face.jobbo.data.JobBoard;
import land.face.jobbo.data.JobTemplate;
import land.face.jobbo.data.PostedJob;
import land.face.jobbo.util.GsonFactory;
import land.face.strife.StrifePlugin;
import land.face.strife.data.champion.LifeSkillType;
import land.face.waypointer.WaypointerPlugin;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

public class JobManager {

  private final JobboPlugin plugin;

  private final Map<String, JobTemplate> loadedTemplates = new HashMap<>();
  private final Set<JobBoard> jobBoards = new HashSet<>();
  private final Map<UUID, Job> acceptedJobs = new HashMap<>();

  private final Random random = new Random();

  private final List<TextColor> difficultyColor = Arrays.asList(
      TextColor.color(196, 255, 77),
      TextColor.color(255, 255, 77),
      TextColor.color(255, 195, 77),
      TextColor.color(255, 136, 77),
      TextColor.color(255, 77, 77)
  );

  public JobManager(JobboPlugin plugin) {
    this.plugin = plugin;
  }

  public boolean createBoard(String id) {
    for (JobBoard board : jobBoards) {
      if (board.getId().equals(id)) {
        return false;
      }
    }
    JobBoard board = new JobBoard();
    board.setId(id);
    jobBoards.add(board);
    return true;
  }

  public boolean removeBoard(String id) {
    JobBoard selectedBoard = null;
    for (JobBoard board : jobBoards) {
      if (board.getId().equals(id)) {
        selectedBoard = board;
      }
    }
    if (selectedBoard != null) {
      jobBoards.remove(selectedBoard);
      return true;
    }
    return false;
  }

  public JobBoard getBoard(String id) {
    for (JobBoard board : jobBoards) {
      if (board.getId().equals(id)) {
        return board;
      }
    }
    return null;
  }

  public JobTemplate getJobTemplate(String id) {
    return loadedTemplates.get(id);
  }

  public void clearAllBoardJobs() {
    for (JobBoard board : jobBoards) {
      for (PostedJob postedJob : board.getJobListings()) {
        postedJob.setJob(null);
        postedJob.setSeconds(1);
      }
    }
  }

  public boolean hasJob(Player player) {
    return acceptedJobs.containsKey(player.getUniqueId());
  }

  public void abandonJob(Player player) {
    acceptedJobs.remove(player.getUniqueId());
  }


  public Job getJob(Player player) {
    return acceptedJobs.get(player.getUniqueId());
  }

  public void incrementJobProgress(Player player) {
    Job job = acceptedJobs.get(player.getUniqueId());
    if (job != null) {
      boolean complete = job.addOne();
      if (!complete) {
        AdvancedActionBarUtil.addOverrideMessage(player, "JOB-UPDATE",
            "&aJOB [" + job.getProgress() + "/" + job.getProgressCap() + "]", 30);
      } else {
        AdvancedActionBarUtil.addOverrideMessage(player, "JOB-UPDATE", "&aJOB &fCOMPLETE!", 30);
        doCompletion(player, job);
      }
    }
  }

  private void doCompletion(Player player, Job job) {
    if (job.getTemplate().getCompletionNpc() != -1) {
      NPC npc = CitizensAPI.getNPCRegistry().getById(job.getTemplate().getCompletionNpc());
      if (npc == null) {
        Bukkit.getLogger().warning("[Jobbo] NPC for completion of job " +
            job.getTemplate().getId() + " was missing, granted awards directly! Fix it tho!");
        awardPlayer(player, job);
        return;
      }
      if (plugin.isWaypointerEnabled()) {
        Location wpLoc = npc.getStoredLocation().clone().add(0, 3, 0);
        WaypointerPlugin.getInstance().getWaypointManager()
            .setWaypoint(player, "Job Turn-in", wpLoc);
      }
      player.playSound(player.getLocation(), Sound.UI_TOAST_IN, 5, 1);
      MessageUtils.sendMessage(player, "&2Job Task Finished! &aInform the person who issued" +
          " the job that you've finished it for your reward!");
    } else {
      awardPlayer(player, job);
    }
  }

  public void awardPlayer(Player player, Job job) {
    if (StringUtils.isNotBlank(job.getTemplate().getCompletionMessage())) {
      MessageUtils.sendMessage(player, job.getTemplate().getCompletionMessage());
    }
    MessageUtils.sendMessage(player,
        "&2&lJOB COMPLETE! &aWell done! You can now accept a new job at the job board!");
    player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 5, 2);
    acceptedJobs.remove(player.getUniqueId(), job);
    if (plugin.isStrifeEnabled()) {
      StrifePlugin.getInstance().getExperienceManager().addExperience(player, job.getXp(), true);
      for (LifeSkillType skill : job.getTemplate().getSkillXpReward().keySet()) {
        StrifePlugin.getInstance().getSkillExperienceManager()
            .addExperience(player, skill, job.getTemplate().getSkillXpReward().get(skill), true,
                true);
      }
    } else {
      player.giveExp(job.getXp(), false);
    }
    if (plugin.getEconomy() != null) {
      plugin.getEconomy().depositPlayer(player, job.getMoney());
    }
    // give money
    // give gems
    // give guild xp?
  }

  public boolean isJobComplete(Player player) {
    return hasJob(player) && acceptedJobs.get(player.getUniqueId()).isCompleted();
  }

  public PostedJob getJobPosting(Location location) {
    for (JobBoard board : jobBoards) {
      for (PostedJob postedJob : board.getJobListings()) {
        if (location.equals(postedJob.getLocation())) {
          return postedJob;
        }
      }
    }
    return null;
  }

  public boolean acceptListing(Player player, Job job) {
    if (acceptedJobs.containsKey(player.getUniqueId())) {
      MessageUtils.sendMessage(player, ChatColor.RED + "Failed to accept! You already have a job!");
      return false;
    }
    for (PostedJob postedJob : job.getBoard().getJobListings()) {
      if (postedJob.getJob() == job) {
        acceptedJobs.put(player.getUniqueId(), job);
        postedJob.setJob(null);
        postedJob.setSeconds(315);
        updatePostingSign(job.getBoard(), postedJob);
        return true;
      }
    }
    return false;
  }

  public Job postJob(JobBoard board) {
    if (board.getTemplateIds().isEmpty()) {
      Bukkit.getLogger().warning("Board " + board.getId() + " cannot post. No templates!");
      return null;
    }
    String templateId;
    if (board.getTemplateIds().size() == 1) {
      templateId = board.getTemplateIds().get(0);
    } else {
      templateId = board.getTemplateIds().get(random.nextInt(board.getTemplateIds().size()));
    }
    JobTemplate selectedTemplate = loadedTemplates.get(templateId);
    return selectedTemplate.generateJobInstance(board);
  }

  public void tickListings() {
    for (JobBoard board : jobBoards) {
      for (PostedJob postedJob : board.getJobListings()) {
        if (postedJob.getSeconds() == 0) {
          Job job = postJob(board);
          if (job == null) {
            Bukkit.getLogger().warning("Board " + board.getId() + " failed to post a job...");
            continue;
          }
          postedJob.setJob(job);
          postedJob.setSeconds(315);
          updatePostingSign(board, postedJob);
          continue;
        }
        if (postedJob.getSeconds() % 60 == 0) {
          updatePostingSign(board, postedJob);
        }
        postedJob.setSeconds(postedJob.getSeconds() - 1);
      }
    }
  }

  private void updatePostingSign(JobBoard board, PostedJob postedJob) {
    Location location = postedJob.getLocation();
    Sign sign = (Sign) location.getBlock().getState();
    sign.setEditable(true);
    if (postedJob.getJob() == null) {
      sign.line(0, Component.text(""));
      sign.line(1, Component.text("[JOB ACCEPTED]").color(TextColor
          .color(142, 34, 17)).decoration(TextDecoration.BOLD, true));
      int mins = (int) Math.round((double) postedJob.getSeconds() / 60);
      sign.line(2, Component.text("New Job: " + mins + "m")
          .color(TextColor.color(0, 128, 255)));
      sign.line(3, Component.text(""));
    } else {
      JobTemplate template = postedJob.getJob().getTemplate();
      String starz = IntStream.range(0, template.getDifficulty())
          .mapToObj(i -> "✦").collect(Collectors.joining(""));
      sign.line(0, Component.text(postedJob.getJob().getTaskType() + " JOB")
          .color(TextColor.color(0, 204, 0)).decoration(TextDecoration.BOLD, true));
      sign.line(1, Component.text(starz)
          .color(difficultyColor.get(template.getDifficulty() - 1)));
      sign.line(2, Component.text("Time Left: " + (postedJob.getSeconds() / 60) + "m")
          .color(TextColor.color(0, 128, 255)));
      sign.line(3, Component.text("[Click For Info!]")
          .color(TextColor.color(255, 230, 255)));
    }
    sign.setEditable(false);
    sign.update();
  }

  public void saveBoards() {
    try (FileWriter writer = new FileWriter(plugin.getDataFolder() + "/boards.json")) {
      GsonFactory.getCompactGson().toJson(jobBoards, writer);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void loadBoards() {
    jobBoards.clear();
    try (FileReader reader = new FileReader(plugin.getDataFolder() + "/boards.json")) {
      JsonArray array = GsonFactory.getCompactGson().fromJson(reader, JsonArray.class);
      for (JsonElement e : array) {
        JobBoard board = GsonFactory.getCompactGson().fromJson(e, JobBoard.class);
        jobBoards.add(board);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void loadTemplates(VersionedSmartYamlConfiguration file) {
    loadedTemplates.clear();
    for (String key : file.getKeys(false)) {
      String jobType = file.getString(key + ".type", "KILL");
      String townId = file.getString(key + ".town-id", "NONE");
      int difficulty = file.getInt(key + ".difficulty", 1);
      JobTemplate jobTemplate = new JobTemplate(key, townId, jobType, difficulty);

      jobTemplate.setDataStringOne(file.getString(key + ".data-string-one", null));
      jobTemplate.setDataStringTwo(file.getString(key + ".data-string-two", null));

      jobTemplate.setTaskCap(file.getInt(key + ".task-amount", 1));
      jobTemplate.setBonusCap(file.getInt(key + ".task-amount-bonus", 0));
      jobTemplate.setMoneyReward(file.getInt(key + ".money-reward", 0));
      jobTemplate.setBonusMoney(file.getInt(key + ".money-reward-bonus", 0));
      jobTemplate.setXpReward(file.getInt(key + ".xp-reward", 0));
      jobTemplate.setBonusXp(file.getInt(key + ".xp-reward-bonus", 0));
      if (plugin.isStrifeEnabled()) {
        if (file.isConfigurationSection("life-skill-xp")) {
          for (String skill : file.getConfigurationSection("life-skill-xp").getKeys(false)) {
            LifeSkillType lifeSkillType;
            try {
              lifeSkillType = LifeSkillType.valueOf(skill);
              jobTemplate.getSkillXpReward()
                  .put(lifeSkillType, file.getInt("life-skill-type." + skill));
            } catch (Exception ignored) {
            }
          }
        }
      }

      jobTemplate.setWorldName(file.getString(key + ".waypoint.world", null));
      jobTemplate.setX(file.getDouble(key + ".waypoint.x", 0));
      jobTemplate.setY(file.getDouble(key + ".waypoint.y", 0));
      jobTemplate.setZ(file.getDouble(key + ".waypoint.z", 0));

      jobTemplate.setCompletionNpc(file.getInt(key + ".completion-npc", -1));
      jobTemplate.setCompletionMessage(StringExtensionsKt
          .chatColorize(file.getString(key + ".completion-message", "")));

      jobTemplate.setJobName(StringExtensionsKt.chatColorize(
          file.getString(key + ".name", "Generic Job")));
      jobTemplate.getDescription().clear();
      jobTemplate.getDescription().addAll(ListExtensionsKt.chatColorize(
          file.getStringList(key + ".description")));
      jobTemplate.buildLocation();

      loadedTemplates.put(key, jobTemplate);
    }
    Bukkit.getLogger().info("[Jobbo] Loaded " + loadedTemplates.size() + " job templates!");
  }
}