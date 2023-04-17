package club.aurorapvp.util;

import org.bukkit.Material;

public class MaterialUtil {
  public static String getFriendlyName(Material material) {
    String[] words = material.toString().split("_");
    StringBuilder result = new StringBuilder();

    for (String word : words) {
      word = word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase();
      result.append(word).append(" ");
    }

    result.setLength(result.length() - 1);

    return result.toString();
  }
}
