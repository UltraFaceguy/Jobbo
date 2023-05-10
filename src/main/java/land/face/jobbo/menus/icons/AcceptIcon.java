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

import com.tealcube.minecraft.bukkit.facecore.utilities.MessageUtils;
import com.tealcube.minecraft.bukkit.facecore.utilities.PaletteUtil;
import io.pixeloutlaw.minecraft.spigot.hilt.ItemStackExtensionsKt;
import java.util.List;
import land.face.jobbo.JobboPlugin;
import land.face.jobbo.data.Job;
import land.face.jobbo.data.JobBoard;
import land.face.jobbo.data.PostedJob;
import land.face.jobbo.menus.AcceptJobMenu;
import ninja.amp.ampmenus.events.ItemClickEvent;
import ninja.amp.ampmenus.items.MenuItem;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class AcceptIcon extends MenuItem {

  public AcceptIcon() {
    super("", new ItemStack(Material.AIR));
  }
  private static final String acceptText = PaletteUtil.color("|cyan||b|Click To Accept!");

  @Override
  public ItemStack getFinalIcon(Player player) {
    ItemStack stack = new ItemStack(Material.BARRIER);
    ItemStackExtensionsKt.setCustomModelData(stack, 50);
    Job job = AcceptJobMenu.getInstance().getSelectedJob().get(player);
    List<String> lore = JobInfoIcon.buildCoreLore(stack, job);
    lore.add(acceptText);
    stack.setLore(lore);
    return stack;
  }

  @Override
  public void onItemClick(ItemClickEvent event) {
    super.onItemClick(event);
    event.setWillUpdate(false);
    event.setWillClose(true);

    if (JobboPlugin.getApi().getJobManager().hasJob(event.getPlayer())) {
      MessageUtils.sendMessage(event.getPlayer(),
          ChatColor.RED + "Failed to accept! You already have a job!");
    }

    Job job = AcceptJobMenu.getInstance().getSelectedJob().get(event.getPlayer());
    if (job == null) {
      MessageUtils.sendMessage(event.getPlayer(), "DEY TUK OUR JERBS");
      return;
    }
    JobBoard board = AcceptJobMenu.getInstance().getSelectedBoard().get(event.getPlayer());
    if (board == null) {
      MessageUtils.sendMessage(event.getPlayer(), "DEY TUK OUR JERBS");
      return;
    }
    for (PostedJob postedJob : board.getJobListings()) {
      if (postedJob.getJob() == job) {
        JobboPlugin.getApi().getJobManager().acceptListing(event.getPlayer(), job);
        return;
      }
    }
    MessageUtils.sendMessage(event.getPlayer(), "DEY TUK OUR JERBS");
  }
}
