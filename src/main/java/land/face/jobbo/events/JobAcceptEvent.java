package land.face.jobbo.events;

import land.face.jobbo.data.Job;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class JobAcceptEvent extends Event implements Cancellable {

  private static final HandlerList HANDLER_LIST = new HandlerList();

  public static HandlerList getHandlerList() {
    return HANDLER_LIST;
  }

  @Getter
  @Setter
  private Player player;
  @Getter
  @Setter
  private Job jobInstance;
  private boolean cancel;

  public JobAcceptEvent(Player player, Job jobInstance) {
    this.player = player;
    this.jobInstance = jobInstance;
  }

  @Override
  public HandlerList getHandlers() {
    return HANDLER_LIST;
  }

  public void setCancelled(boolean cancel) {
    this.cancel = cancel;
  }

  public boolean isCancelled() {
    return this.cancel;
  }

}
