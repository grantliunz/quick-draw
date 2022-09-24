package nz.ac.auckland.se206.words;

import com.opencsv.CSVReader;
import java.io.File;
import java.io.FileReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CategorySelector {

  private final Map<Difficulty, List<String>> difficulty2categories;

  public CategorySelector() throws Exception {
    difficulty2categories = new HashMap<>();
    for (Difficulty difficulty : Difficulty.values()) {
      difficulty2categories.put(difficulty, new ArrayList<>());
    }

    for (String[] line : getLines()) {
      difficulty2categories.get(Difficulty.valueOf(line[1])).add(line[0]);
    }
  }

  public String getRandomWord(Difficulty difficulty) {
    return difficulty2categories
        .get(difficulty)
        .get((int) (Math.random() * difficulty2categories.get(difficulty).size()));
  }

  private List<String[]> getLines() throws Exception {
    File file = new File(CategorySelector.class.getResource("/category_difficulty.csv").toURI());
    try (FileReader fr = new FileReader(file, StandardCharsets.UTF_8);
        CSVReader reader = new CSVReader(fr)) {
      return reader.readAll();
    }
  }

  public enum Difficulty {
    E,
    M,
    H
  }
}
