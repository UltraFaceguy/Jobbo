package land.face.jobbo.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import land.face.strife.data.champion.LifeSkillType;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;

public class JobTemplate {

  private static final Random random = new Random();

  @Getter
  private final String id, townId, jobType;

  @Getter
  private final Map<LifeSkillType, Integer> skillXpReward = new HashMap<>();
  @Getter
  @Setter
  private String dataStringOne, dataStringTwo;
  @Getter
  @Setter
  private int difficulty, moneyReward, bonusMoney, xpReward, bonusXp, taskCap, bonusCap;
  @Setter
  private double x, y, z;
  @Setter
  private String worldName;
  @Getter
  @Setter
  private int completionNpc;
  @Getter
  @Setter
  private String completionMessage;
  @Getter
  @Setter
  private String jobName;
  @Getter
  @Setter
  private double rerollChance;
  @Getter
  private final List<String> description = new ArrayList<>();

  @Getter
  private Location location;

  public JobTemplate(String id, String townId, String jobType, int difficulty) {
    this.id = id;
    this.townId = townId;
    this.jobType = jobType;
    this.difficulty = Math.min(5, difficulty);
  }

  public void buildLocation() {
    if (worldName != null) {
      location = new Location(Bukkit.getWorld(worldName), x, y, z);
    }
  }

  public Job generateJobInstance(JobBoard board) {
    int totalCap = taskCap + (bonusCap > 0 ? random.nextInt(bonusCap + 1) : 0);
    Job job = new Job(this, board, jobType, dataStringOne, dataStringTwo, totalCap);

    double moneyTotal = moneyReward + (bonusMoney > 0 ? random.nextInt(bonusMoney + 1) : 0);
    double xpTotal = xpReward + (bonusXp > 0 ? random.nextInt(bonusXp + 1) : 0);

    if (Math.random() < 0.02) {
      moneyTotal *= 2 + Math.random() * 4;
      job.setMoneyBonus(true);
    }

    job.setMoney((int) moneyTotal);
    job.setXp((int) xpTotal);
    return job;
  }
}
