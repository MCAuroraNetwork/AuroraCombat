package club.aurorapvp.util;

import org.bukkit.Material;

public class MaterialUtil {
  public static String getFriendlyName(Material material) {
    // Get the name of the material as a string
    String name = material.toString();
    // Split the name by underscores
    String[] words = name.split("_");
    // Create a string builder to store the result
    StringBuilder result = new StringBuilder();
    // Loop through each word
    for (String word : words) {
      // Capitalize the first letter of the word
      word = word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase();
      // Append the word and a space to the result
      result.append(word).append(" ");
    }
    // Remove the trailing space
    result.setLength(result.length() - 1);
    // Return the result as a string
    return result.toString();
  }
}
