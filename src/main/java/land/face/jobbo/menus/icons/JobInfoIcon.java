/**
 * The MIT License Copyright (c) 2015 Teal Cube Games
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package land.face.jobbo.menus.icons;

import com.tealcube.minecraft.bukkit.facecore.utilities.PaletteUtil;
import io.pixeloutlaw.minecraft.spigot.hilt.ItemStackExtensionsKt;
import java.util.ArrayList;
import java.util.List;
import land.face.jobbo.JobboPlugin;
import land.face.jobbo.data.Job;
import land.face.jobbo.data.JobTemplate;
import land.face.strife.data.champion.LifeSkillType;
import ninja.amp.ampmenus.events.ItemClickEvent;
import ninja.amp.ampmenus.items.MenuItem;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class JobInfoIcon extends MenuItem {

  public JobInfoIcon() {
    super("", new ItemStack(Material.AIR));
  }

  @Override
  public ItemStack getFinalIcon(Player player) {
    ItemStack stack = new ItemStack(Material.PAPER);
    Job job = JobboPlugin.getApi().getJobManager().getJob(player);
    List<String> lore = buildCoreLore(stack, job);
    lore.add(PaletteUtil.color("|cyan||Progress: |white|[ " + job.getProgress() + "/" + job.getProgressCap() + " ]"));
    stack.setLore(lore);
    return stack;
  }

  @Override
  public void onItemClick(ItemClickEvent event) {
    super.onItemClick(event);
    event.setWillUpdate(false);
    event.setWillClose(false);
  }

  public static List<String> buildCoreLore(ItemStack stack, Job job) {
    JobTemplate jobTemplate = job.getTemplate();
    ItemStackExtensionsKt.setDisplayName(stack, PaletteUtil.color("|purple||b|" + ChatColor.stripColor(jobTemplate.getJobName())));
    List<String> lore = new ArrayList<>();
    lore.add("");
    lore.addAll(jobTemplate.getDescription().stream().map(line ->
            line.replaceAll("%num%", String.valueOf(Integer.valueOf(job.getProgressCap()))))
        .toList());
    lore.add("");
    lore.add(PaletteUtil.color("|lgreen||b|Rewards:"));
    for (ItemStack rewardItem : job.getItemRewards()) {
      String amount = "|cyan|" + rewardItem.getAmount() + "x ";
      String name = ItemStackExtensionsKt.getDisplayName(rewardItem);
      lore.add(PaletteUtil.color(amount + name));
    }
    if (JobboPlugin.isStrifeEnabled() && !jobTemplate.getSkillXpReward().isEmpty()) {
      for (LifeSkillType lifeSkillType : jobTemplate.getSkillXpReward().keySet()) {
        lore.add(lifeSkillType.getColor() + " " + jobTemplate.getSkillXpReward()
            .get(lifeSkillType) + " " + lifeSkillType.getPrettyName() + " XP");
      }
    }
    if (job.getXp() > 0) {
      lore.add(PaletteUtil.color("|lgreen| " + job.getXp() + " Combat XP"));
    }
    if (job.getMoney() > 0) {
      String moneyz = PaletteUtil.color("|yellow| " + job.getMoney() + "◎");
      if (job.isMoneyBonus()) {
        moneyz += PaletteUtil.color("  |orange_shake||b|In Demand Bonus!");
      }
      lore.add(moneyz);
    }
    // TODO: gems
    if (true == false) {
      lore.add(PaletteUtil.color("|purple| " + job.getXp() + "|white|▼"));
    }
    lore.add("");
    return lore;
  }
}
