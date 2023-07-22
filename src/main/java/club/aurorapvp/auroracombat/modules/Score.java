package club.aurorapvp.auroracombat.modules;

import club.aurorapvp.auroracombat.config.Config;
import club.aurorapvp.auroracombat.config.Lang;
import club.aurorapvp.auroracombat.data.ScoreDataHandler;
import org.bukkit.entity.Player;

public class Score {
  private final Player p;
  private final Rating rating;
  private final ScoreDataHandler data;
  private int points;

  public Score(Player p, Rating rating) {
    this.p = p;
    this.rating = rating;
    this.data = new ScoreDataHandler(this);

    if (this.exists()) {
      this.reload();
    } else {
      points = Config.get().getInt("elo.default-points");
      this.save();
    }
  }

  public Player getPlayer() {
    return p;
  }

  public Rating getRating() {
    return rating;
  }

  public int getPoints() {
    return points;
  }

  public void changePoints(int amount) {
    points = points + amount;

    String name = this.getRating().getName();

    String[] words = name.split("_");
    for (int i = 0; i < words.length; i++) {
      words[i] = words[i].substring(0, 1).toUpperCase() + words[i].substring(1);
    }

    name = String.join(" ", words);

    if (points < 0) {
      p.sendMessage(Lang.formatComponent("points-decreased", name, amount));
    } else {
      p.sendMessage(Lang.formatComponent("points-increased", name, amount));
    }
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
