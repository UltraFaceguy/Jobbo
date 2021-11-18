package land.face.jobbo.commands;

import com.tealcube.minecraft.bukkit.facecore.utilities.MessageUtils;
import com.tealcube.minecraft.bukkit.shade.acf.BaseCommand;
import com.tealcube.minecraft.bukkit.shade.acf.annotation.CommandAlias;
import com.tealcube.minecraft.bukkit.shade.acf.annotation.CommandCompletion;
import com.tealcube.minecraft.bukkit.shade.acf.annotation.Default;
import com.tealcube.minecraft.bukkit.shade.acf.annotation.Subcommand;
import land.face.jobbo.JobboPlugin;
import land.face.jobbo.data.JobBoard;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("job|jobs|jobbo")
public class JobCommand extends BaseCommand {

  private final JobboPlugin plugin;

  public JobCommand(JobboPlugin plugin) {
    this.plugin = plugin;
  }

  @Default
  public void baseCommand(Player player) {
    if (JobboPlugin.getApi().getJobManager().hasJob(player)) {
      plugin.getStatusMenu().open(player);
    } else {
      MessageUtils.sendMessage(player, ChatColor.YELLOW
          + "You don't have a job you lazy bum! Visit a job board in a town to accept one!");
    }
  }

  @Subcommand("reload")
  public void reload() {
    plugin.onDisable();
    plugin.onEnable();
  }

  @Subcommand("refresh")
  public void refresh() {
    JobboPlugin.getApi().getJobManager().clearAllBoardJobs();
  }

  @Subcommand("board create")
  public void createBoard(CommandSender sender, String boardId) {
    JobboPlugin.getApi().getJobManager().createBoard(boardId);
  }

  @CommandCompletion("@boards")
  @Subcommand("board addSign")
  public void addLocation(Player sender, String boardId) {
    JobBoard jobBoard = JobboPlugin.getApi().getJobManager().getBoard(boardId);
    if (jobBoard == null) {
      MessageUtils.sendMessage(sender, "this board does not exist");
      return;
    }
    Block block = sender.getTargetBlock(40);
    if (block == null || !block.getType().toString().endsWith("_SIGN")) {
      MessageUtils.sendMessage(sender, "this no sign " + (block != null ? block.toString() : ""));
      return;
    }
    MessageUtils.sendMessage(sender, "dun");
    jobBoard.addLocation(block.getLocation());
  }

  @CommandCompletion("@boards @templates")
  @Subcommand("board addTemplate")
  public void addTemplate(Player sender, String boardId, String templateId) {
    JobBoard jobBoard = JobboPlugin.getApi().getJobManager().getBoard(boardId);
    if (jobBoard == null) {
      MessageUtils.sendMessage(sender, "&eFailed! This board doesn't exist!");
      return;
    }
    if (!plugin.getJobManager().getTemplates().contains(templateId)) {
      MessageUtils.sendMessage(sender, "&eFailed! This template doesn't exist!");
      return;
    }
    if (jobBoard.getTemplateIds().contains(templateId)) {
      MessageUtils.sendMessage(sender, "&eFailed! This board already has that template!");
      return;
    }
    jobBoard.getTemplateIds().add(templateId);
    MessageUtils.sendMessage(sender, "&aSuccess! Added " + templateId + " to " + boardId);
  }
}