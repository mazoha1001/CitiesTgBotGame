package main.cities;

import lombok.RequiredArgsConstructor;
import main.entity.Player;
import main.service.PlayerService;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.ThreadLocalRandom;

@Component
@RequiredArgsConstructor
public class CitiesGame {
    private final CitiesReader citiesReader;
    private final PlayerService playerService;

    public boolean isCityExist(String city) {
        city = city.substring(0, 1).toUpperCase() + city.substring(1);
        if (citiesReader.mapLettersToIndex.containsKey(getFirstLetter(city))) {
            return citiesReader.getArrayCityByLetter(getFirstLetter(city)).contains(city);
        }
        return false;
    }

    public boolean isCityFree(Player player, String city) {
        city = city.substring(0, 1).toUpperCase() + city.substring(1);
        return !player.getBusyCities().contains(city);
    }

    public boolean isStartLetterRight(Player player, String messageText) {
        return (player.getStartLetter() == null) || (player.getStartLetter().equals(getFirstLetter(messageText)));
    }


    public String getAiCity(Player player, String city) {
        ArrayList<String> cities = (ArrayList<String>) citiesReader.getArrayCityByLetter(getLastLetter(city));
        int i = 1000;
        while (i > 0) {
            int a = ThreadLocalRandom.current().nextInt(cities.size());
            if (!player.getBusyCities().contains(cities.get(a))) {
                player.setScore(player.getScore() + 1);
                if (player.getScore() > player.getHighScore()) {
                    player.setHighScore(player.getScore());
                }
                city = city.substring(0, 1).toUpperCase() + city.substring(1);
                player.getBusyCities().add(city);
                player.getBusyCities().add(cities.get(a));
                player.setStartLetter(getLastLetter(cities.get(a)));
                playerService.savePlayer(player);
                return cities.get(a);
            }
            i--;
        }
        return null;
    }

    public String getFirstLetter(String city) {
        return city.toUpperCase().substring(0, 1);
    }

    public String getLastLetter(String city) {
        String lastLetter = String.valueOf(city.charAt(city.length() - 1));
        int i = 1;
        while (lastLetter.equals("ь") || lastLetter.equals("й") || lastLetter.equals("ы")) {
            lastLetter = String.valueOf(city.charAt(city.length() - i));
            i++;
        }
        return lastLetter.toUpperCase();
    }


}
