package oop.practice;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class Main {
  public static void main(String[] args) throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    File inputFile = new File("src/main/resources/input.json");
    JsonNode data = mapper.readTree(inputFile).get("data");
    ArrayNode arrayNode = (ArrayNode) data;

    for (int i = 0; i < arrayNode.size()-1; i++) {
      JsonNode obj = arrayNode.get(i);
      for (int j = i + 1; j < arrayNode.size(); j++) {
        JsonNode obj2 = arrayNode.get(j);
        if (RepRemover.areEqual(obj, obj2)) {
          arrayNode.remove(j);
          j--;
        }
        }
    }

    Universe starWars = new Universe("starwars", new ArrayList<>());
    Universe hitchhikers = new Universe("hitchhikers", new ArrayList<>());
    Universe marvel = new Universe("marvel", new ArrayList<>());
    Universe rings = new Universe("rings", new ArrayList<>());

    for (JsonNode entry : arrayNode) {
      String entryAsString = entry.toString();
      System.out.print(entryAsString);
      String userInput = Classificator.classify(entry);

      switch (userInput) {
        case "starwars":
          starWars.individuals().add(entryAsString);
          System.out.println(": StarWars unit");
          break;
        case "hitchhikers":
          hitchhikers.individuals().add(entryAsString);
          System.out.println(": Hitchhikers unit");
          break;
        case "marvel":
          marvel.individuals().add(entryAsString);
          System.out.println(": Marvel unit");
          break;
        case "rings":
          rings.individuals().add(entryAsString);
          System.out.println(": Rings unit");
          break;
        default:
          System.out.println("Invalid input");
      }
    }

    mapper.writeValue(new File("src/main/resources/output/starwars.json"), starWars);
    mapper.writeValue(new File("src/main/resources/output/hitchhikers.json"), hitchhikers);
    mapper.writeValue(new File("src/main/resources/output/marvel.json"), marvel);
    mapper.writeValue(new File("src/main/resources/output/rings.json"), rings);
  }
}

record Universe(
    String name,
    List<String> individuals
) { }

class Classificator {

  public static String classify(JsonNode unit) {
    ArrayList<String> universes = new ArrayList<>(Arrays.asList("starwars", "hitchhikers", "marvel", "rings"));
    String isHumanoid = unit.has("isHumanoid") ? unit.get("isHumanoid").toString() : "null";
    String planet = unit.has("planet") ? unit.get("planet").toString() : "null";
    String age = unit.has("age") ? unit.get("age").toString() : "null";
    String traits = unit.has("traits") ? unit.get("traits").toString() : "null";

    if (!((isHumanoid.equals("null") || isHumanoid.equals("false")) && (planet.equals("null") || planet.contains("Kashyyyk") || planet.contains("Endor")) && (age.equals("null") || Integer.parseInt(age) <= 400) && (traits.equals("null") || traits.contains("HAIRY") || traits.contains("TALL") || traits.contains("SHORT")))) {
      universes.remove("starwars");
    }
    if (!((isHumanoid.equals("null") || isHumanoid.equals("true")) && (planet.equals("null") || planet.contains("Asgard")) && (age.equals("null") || Integer.parseInt(age) <= 5000) && (traits.equals("null") || traits.contains("BLONDE") || traits.contains("TALL")))) {
      universes.remove("marvel");
    }

    if (!((planet.equals("null") || planet.contains("Betelgeuse") || planet.contains("Vogsphere")) && (age.equals("null") || Integer.parseInt(age) <= 200) && (traits.equals("null") || traits.contains("EXTRA_ARMS") || traits.contains("EXTRA_HEAD") || traits.contains("GREEN") || traits.contains("BULKY")))) {
      universes.remove("hitchhikers");
    }

    if (!((isHumanoid.equals("null") || isHumanoid.equals("true")) && (planet.equals("null") || planet.contains("Earth")) && (traits.equals("null") || traits.contains("BLONDE") || traits.contains("POINTY_EARS") || traits.contains("SHORT") || traits.contains("BULKY")))) {
      universes.remove("rings");
    }

    return (universes.isEmpty() | (universes.size() != 1)) ? "null" : universes.get(0);
  }
}

class RepRemover {
  private static ArrayList<String> fields = new ArrayList<>(Arrays.asList("isHumanoid", "planet", "age", "traits"));

  public static boolean areEqual(JsonNode unit1, JsonNode unit2) {
    for (String field : fields) {
      if (unit1.has(field) && unit2.has(field)) {
        if (!unit1.get(field).equals(unit2.get(field))) {
          return false;
        }
      }
    }
   return true;
  }
}