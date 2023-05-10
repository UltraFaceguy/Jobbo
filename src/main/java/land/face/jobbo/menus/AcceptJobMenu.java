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
package land.face.jobbo.menus;

import com.tealcube.minecraft.bukkit.facecore.utilities.PaletteUtil;
import io.pixeloutlaw.minecraft.spigot.garbage.StringExtensionsKt;
import java.util.Map;
import java.util.WeakHashMap;
import land.face.jobbo.JobboPlugin;
import land.face.jobbo.data.Job;
import land.face.jobbo.data.JobBoard;
import land.face.jobbo.menus.icons.AcceptIcon;
import land.face.jobbo.menus.icons.DeclineIcon;
import lombok.Getter;
import lombok.Setter;
import ninja.amp.ampmenus.menus.ItemMenu;
import org.bukkit.entity.Player;

public class AcceptJobMenu extends ItemMenu {

  @Getter
  @Setter
  private static AcceptJobMenu instance;

  private final JobboPlugin plugin;

  @Getter
  private final Map<Player, JobBoard> selectedBoard = new WeakHashMap<>();
  @Getter
  private final Map<Player, Job> selectedJob = new WeakHashMap<>();

  public AcceptJobMenu(JobboPlugin plugin, String name) {
    super(StringExtensionsKt.chatColorize(name), Size.fit(27), plugin);
    this.plugin = plugin;
    setItem(11, new AcceptIcon());
    setItem(12, new AcceptIcon());
    setItem(15, new DeclineIcon());
  }

  public void openForPlayer(Player player, Job job) {
    if (job == null) {
      return;
    }
    selectedBoard.put(player, job.getBoard());
    selectedJob.put(player, job);
    open(player);
  }
}

/*
00 01 02 03 04 05 06 07 08
09 10 11 12 13 14 15 16 17
18 19 20 21 22 23 24 25 26
27 28 29 30 31 32 33 34 35
36 37 38 39 40 41 42 43 44
45 46 47 48 49 50 51 52 53
*/
