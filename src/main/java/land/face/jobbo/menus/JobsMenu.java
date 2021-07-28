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

import land.face.jobbo.JobboPlugin;
import land.face.jobbo.menus.icons.AbandonJobIcon;
import land.face.jobbo.menus.icons.JobInfoIcon;
import land.face.jobbo.menus.icons.JobWaypointButton;
import ninja.amp.ampmenus.menus.ItemMenu;

public class JobsMenu extends ItemMenu {

  private static JobsMenu instance;

  public JobsMenu(JobboPlugin plugin) {
    super("Job", Size.fit(27), plugin);
    setItem(10, new JobInfoIcon(plugin));
    setItem(13, new JobWaypointButton(plugin));
    setItem(16, new AbandonJobIcon(plugin));
    fillEmptySlots(new BlankIcon());
  }

  public static JobsMenu getInstance() {
    return instance;
  }

  public static void setInstance(JobsMenu menu) {
    instance = menu;
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
