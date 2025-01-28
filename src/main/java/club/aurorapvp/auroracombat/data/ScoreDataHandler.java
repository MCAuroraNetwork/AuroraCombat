package club.aurorapvp.auroracombat.data;

import club.aurorapvp.auroracombat.AuroraCombat;
import club.aurorapvp.auroracombat.modules.Score;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import org.bson.Document;

public class ScoreDataHandler {

  private final MongoCollection<Document> collection;
  private final Score score;

  public ScoreDataHandler(Score score) {
    this.score = score;
    this.collection = AuroraCombat.getInstance().getDatabase().getCollection("scores");
  }

  public int getPoints() {
    Document scoreDoc =
        collection
            .find(Filters.eq("playerId", score.getPlayer().getUniqueId().toString()))
            .filter(Filters.eq("rating", score.getRating().getName()))
            .first();
    return scoreDoc != null ? scoreDoc.getInteger("points", -1) : -1;
  }

  public void save() {
    Document scoreDoc =
        new Document("playerId", score.getPlayer().getUniqueId().toString())
            .append("rating", score.getRating().getName())
            .append("points", score.getPoints());

    collection.updateOne(
        Filters.and(
            Filters.eq("playerId", score.getPlayer().getUniqueId().toString()),
            Filters.eq("rating", score.getRating().getName())),
        new Document("$set", scoreDoc),
        new UpdateOptions().upsert(true));
  }

  public boolean exists() {
    return collection
            .find(
                Filters.and(
                    Filters.eq("playerId", score.getPlayer().getUniqueId().toString()),
                    Filters.eq("rating", score.getRating().getName())))
            .first()
        != null;
  }
}