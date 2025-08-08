package main.cities;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class CitiesReader {
    public static final char[] RUSSIAN_ALPHABET = {
            'А', 'Б', 'В', 'Г', 'Д', 'Е', 'Ё', 'Ж', 'З', 'И', 'Й',
            'К', 'Л', 'М', 'Н', 'О', 'П', 'Р', 'С', 'Т', 'У', 'Ф',
            'Х', 'Ц', 'Ч', 'Ш', 'Щ', 'Ъ', 'Ы', 'Ь', 'Э', 'Ю', 'Я'
    };

    public Map<String, Integer> mapLettersToIndex = new HashMap<>();

    public List<List<String>> cityLists = new ArrayList<>();

    public CitiesReader() {
        readCities();
        fillMap();
    }

    public List<String> getArrayCityByLetter(String letter) {
        return cityLists.get(mapLettersToIndex.get(letter));
    }

    private void fillMap() {
        for (int i = 0; i < RUSSIAN_ALPHABET.length; i++) {
            mapLettersToIndex.put(String.valueOf(RUSSIAN_ALPHABET[i]), i);
        }
    }

    private void readCities() {
        try {
            String content = new String(Files.readAllBytes(Paths.get("allCities.json")));
            JSONObject jsonObject = new JSONObject(content);
            JSONArray citiesArray = jsonObject.getJSONArray("city");
            for (int i = 0; i < RUSSIAN_ALPHABET.length; i++) {
                cityLists.add(new ArrayList<>());
            }
            for (int i = 0; i < citiesArray.length(); i++) {
                JSONObject city = citiesArray.getJSONObject(i);
                String cityName = city.getString("name").trim();

                if (cityName.isEmpty()) {
                    continue; // Skip empty names
                }
                char firstLetter = Character.toUpperCase(cityName.charAt(0));
                int index = -1;
                for (int j = 0; j < RUSSIAN_ALPHABET.length; j++) {
                    if (firstLetter == RUSSIAN_ALPHABET[j]) {
                        index = j;
                        break;
                    }
                }
                if (index != -1) {
                    cityLists.get(index).add(cityName);
                } else {
                    System.out.println("Skipping city with non-Russian first letter: " + cityName);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
