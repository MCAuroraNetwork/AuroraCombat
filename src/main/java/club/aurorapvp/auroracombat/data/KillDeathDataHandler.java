package club.aurorapvp.auroracombat.data;

import club.aurorapvp.auroracombat.AuroraCombat;
import club.aurorapvp.auroracombat.modules.KillDeathTracker;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.Document;

public class KillDeathDataHandler {

  private final MongoCollection<Document> collection;
  private final KillDeathTracker tracker;
  private final String playerId;

  public KillDeathDataHandler(KillDeathTracker tracker) {
    this.tracker = tracker;
    this.collection = AuroraCombat.getInstance().getDatabase().getCollection("kill_and_death_data");
    this.playerId = tracker.getPlayer().getUniqueId().toString();
  }

  public int getKills() {
    Document playerData = collection.find(Filters.eq("_id", playerId)).first();
    return playerData != null ? playerData.getInteger("kills", -1) : -1;
  }

  public int getKillstreak() {
    Document playerData = collection.find(Filters.eq("_id", playerId)).first();
    return playerData != null ? playerData.getInteger("killstreak", -1) : -1;
  }

  public int getDeaths() {
    Document playerData = collection.find(Filters.eq("_id", playerId)).first();
    return playerData != null ? playerData.getInteger("deaths", -1) : -1;
  }

  public void save() {
    Document playerData =
        new Document()
            .append("_id", playerId)
            .append("kills", tracker.getKills())
            .append("deaths", tracker.getDeaths())
            .append("killstreak", tracker.getKillStreak());

    collection.updateOne(
        Filters.eq("_id", playerId),
        new Document("$set", playerData),
        new com.mongodb.client.model.UpdateOptions().upsert(true));
  }

  public boolean exists() {
    return collection.find(Filters.eq("_id", playerId)).first() != null;
  }
}