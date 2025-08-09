package main.tgbot;

import main.cities.CitiesGame;
import main.config.AppProps;
import main.entity.Player;
import main.service.PlayerService;
import org.springframework.data.domain.Limit;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.BotSession;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.AfterBotRegistration;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class TgBot implements SpringLongPollingBot, LongPollingSingleThreadUpdateConsumer {

    private final TelegramClient telegramClient;
    private final PlayerService playerService;
    private final CitiesGame citiesGame;
    private final AppProps appProps;

    public TgBot(PlayerService playerService, CitiesGame citiesGame, AppProps appProps) {
        this.playerService = playerService;
        this.citiesGame = citiesGame;
        this.appProps = appProps;
        telegramClient = new OkHttpTelegramClient(getBotToken());
    }

    @Override
    public String getBotToken() {
        return appProps.getToken();
    }

    @Override
    public LongPollingUpdateConsumer getUpdatesConsumer() {
        return this;
    }

    @Override
    public void consume(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            handleMessage(update);
        }
    }

    public void handleMessage(Update update) {
        String messageText = update.getMessage().getText();
        String chatId = update.getMessage().getChatId().toString();
        Player player = new Player();
        if (!playerService.isPlayerExist(chatId)) {
            player.setId(chatId);
            player.setName(update.getMessage().getFrom().getFirstName() + " " + update.getMessage().getFrom().getLastName());
            player.setScore(0);
            player.setHighScore(0);
            player.setBusyCities(new HashSet<String>());
            playerService.savePlayer(player);
        } else {
            player = playerService.getPlayer(chatId);
            if (update.getMessage().getFrom().getLastName() != null) {
                player.setName(update.getMessage().getFrom().getFirstName() + " " + update.getMessage().getFrom().getLastName());
            } else {
                player.setName(update.getMessage().getFrom().getFirstName());

            }

        }
        switch (messageText) {
            case "/start" -> sendMessage(update, "Привет!\n" +
                    "Я бот для игры в города! Я знаю все города в мире!\n" +
                    "Твой текущий рекорд: " + player.getHighScore().toString() + " очков!\n" +
                    "Узнать текущий счет и рекорд:\n/score\n" +
                    "Посмотреть таблицу рекордов:\n" +
                    "/leaderboard\n" +
                    "Начать игру заново:\n /reset\n" +
                    "Напиши город и мы начнем игру!");
            case "/reset" -> {
                sendMessage(update, "Отлично! Ты дошел до " + player.getScore().toString() + " очков!\n" +
                        "Твой рекорд: " + player.getHighScore().toString() + " очков!\n" +
                        "\nА теперь начинаем сначала! Пиши город!");
                player.setScore(0);
                player.setStartLetter(null);
                player.setBusyCities(new HashSet<String>());
                playerService.savePlayer(player);
            }
            case "/score" -> sendMessage(update, "Твой текущий счет: " + player.getScore().toString() + " очков\n" +
                    "Твой рекорд: " + player.getHighScore().toString() + " очков");
            case "/leaderboard" -> sendMessage(update, topPlayersToString(playerService.getTopPlayers(Limit.of(10))));
            default -> {
                if (!citiesGame.isCityExist(messageText)) {
                    sendMessage(update, "Такого города не существует!\nПопробуй другой!");
                } else if (!citiesGame.isCityFree(player, messageText)) {
                    sendMessage(update, "Этот город уже был!\nПопробуй другой!\nЕсли хочешь начать сначала, напиши:\n/reset\nУзнать свой счёт:\n/score");
                } else if (!citiesGame.isStartLetterRight(player, messageText)) {
                    sendMessage(update, "Твой город не подходит!" + "\nОн должен начинаться на '" + player.getStartLetter() + "'");
                } else {
                    String aiCity = citiesGame.getAiCity(player, messageText);
                    sendMessage(update, "Супер! Теперь мой ход:\n" + aiCity + "\nТебе на '" + citiesGame.getLastLetter(aiCity) + "'");
                }
            }
        }
    }

    public String topPlayersToString(List<Player> players) {
        StringBuilder sb = new StringBuilder();
        sb.append("Топовые игроки:\n");
        int i = 1;
        for (Player player : players) {
            sb.append(i).append(") ").append(player.getName()).append(": ").append(player.getHighScore()).append("\n");
            i++;
        }
        return sb.toString();
    }

    public void sendMessage(Update update, String answer) {
        SendMessage message = SendMessage
                .builder()
                .chatId(update.getMessage().getFrom().getId())
                .text(answer)
                .build();
        try {
            telegramClient.execute(message); // Sending our message object to user
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @AfterBotRegistration
    public void afterRegistration(BotSession botSession) {
        System.out.println("Registered bot running state is: " + botSession.isRunning());
    }

}