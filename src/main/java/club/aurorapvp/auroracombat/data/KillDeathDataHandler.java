package club.aurorapvp.auroracombat.data;

import club.aurorapvp.auroracombat.AuroraCombat;
import club.aurorapvp.auroracombat.modules.KillDeathTracker;
import club.aurorapvp.auroracombat.modules.Rating;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import org.bson.Document;
import org.bson.conversions.Bson;

public class KillDeathDataHandler {

  private final MongoCollection<Document> collection;
  private final KillDeathTracker tracker;
  private final String playerId;
  private final String rating;
  private final Bson filter;

  public KillDeathDataHandler(KillDeathTracker tracker, Rating rating) {
    this.tracker = tracker;
    this.collection = AuroraCombat.getInstance().getDatabase().getCollection("kill_and_death_data");

    this.playerId = tracker.getPlayer().getUniqueId().toString();
    this.rating = rating.getName();

    Bson pidFilter = Filters.eq("playerId", playerId);
    if (this.rating != null) {
      this.filter = Filters.and(pidFilter, Filters.eq("rating", this.rating));
    } else {
      this.filter = pidFilter;
    }
  }

  public KillDeathDataHandler(KillDeathTracker tracker) {
    this.tracker = tracker;
    this.collection = AuroraCombat.getInstance().getDatabase().getCollection("kill_and_death_data");

    this.playerId = tracker.getPlayer().getUniqueId().toString();
    this.rating = null;

    this.filter = Filters.eq("playerId", playerId);
  }

  public int getKills() {
    Document doc = collection.find(filter).first();
    return doc != null ? doc.getInteger("kills", -1) : -1;
  }

  public int getHighestKillstreak() {
    Document doc = collection.find(filter).first();
    return doc != null ? doc.getInteger("highestKillstreak", -1) : -1;
  }

  public int getKillstreak() {
    Document doc = collection.find(filter).first();
    return doc != null ? doc.getInteger("killstreak", -1) : -1;
  }

  public int getDeaths() {
    Document doc = collection.find(filter).first();
    return doc != null ? doc.getInteger("deaths", -1) : -1;
  }

  public boolean exists() {
    return collection.find(filter).first() != null;
  }

  public void save() {
    Document data =
        new Document("playerId", playerId)
            .append("kills", tracker.getKills())
            .append("deaths", tracker.getDeaths())
            .append("killstreak", tracker.getKillStreak());

    if (rating != null) {
      data.append("rating", rating);
    }

    collection.updateOne(filter, new Document("$set", data), new UpdateOptions().upsert(true));
  }
}
