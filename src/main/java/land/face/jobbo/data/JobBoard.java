package land.face.jobbo.data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;

public class JobBoard {

  @Getter
  @Setter
  private String id;
  @Getter
  @Setter
  private List<PostedJob> jobListings = new ArrayList<>();
  @Getter
  @Setter
  private List<String> templateIds = new ArrayList<>();

  public void addLocation(Location location) {
    PostedJob postedJob = new PostedJob();
    postedJob.setSeconds(10);
    postedJob.setLocation(location);
    jobListings.add(postedJob);
  }
}
