package club.aurorapvp.auroracombat.modules;

import club.aurorapvp.auroracombat.AuroraCombat;
import club.aurorapvp.auroracombat.config.Config;
import club.aurorapvp.auroracombat.data.ScoreDataHandler;
import org.bukkit.entity.Player;

public class Score {

  private final Player player;
  private final Rating rating;
  private final ScoreDataHandler data;
  private int points;

  public Score(Player player, Rating rating) {
    this.player = player;
    this.rating = rating;
    this.data = new ScoreDataHandler(this);

    if (this.exists()) {
      this.reload();
    } else {
      points = AuroraCombat.getInstance().getConfig().getInt("elo.default-points");
      this.save();
    }
  }

  public Player getPlayer() {
    return player;
  }

  public Rating getRating() {
    return rating;
  }

  public int getPoints() {
    return points;
  }

  public void changePoints(int amount) {
    points = points + amount;
  }

  public void reload() {
    this.points = data.getPoints();
  }

  public void save() {
    this.data.save();
  }

  public boolean exists() {
    return this.data.exists();
  }
}
