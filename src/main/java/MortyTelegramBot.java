import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.send.SendPhoto;
import org.telegram.telegrambots.api.objects.PhotoSize;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MortyTelegramBot extends TelegramLongPollingBot {

    boolean addMode = false;

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()){
            String message = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            int messageId = update.getMessage().getMessageId();
            if (update.getMessage().hasPhoto()){
                List<PhotoSize> photos = update.getMessage().getPhoto();
                sendPhoto(chatId, photos.get(0).getFileId());
            } else if (addMode) {
                getBitCoin(message, chatId);
                addMode = false;
            }else{
                switch (message) {
                    case "/hideKeyboard":
                        hideKeyboard("Клавиатура скрыта", chatId, messageId);
                        break;
                    case "/showKeyboard":
                        showKeyboard("Клавиатура активированна", chatId, messageId);
                        break;
                    case "/getValuta":
                        addMode = true;
                            sendMessage("Введите какую валюту перевести." +
                                    "В данном формате:  ...-...   " + "Вместо точек подставить одно из этих наименований: usd(доллары), btc(биткоин), rub(рубли), eur(евро), gbp(фунты стерлингов) и т.д. " , chatId);
                            break;
                    default:
                        sendMessage(message, chatId, messageId);
                }
            }


        }

    }


    //======================================================================================================
    @Override
    public String getBotUsername() {
        return "AmazingMortyBot";
    }

    @Override
    public String getBotToken() {
        return "471190406:AAEHMS2ZvpINsqa80BsIQCdx52w0CwDm-ko";
    }

    private void sendMessage(String text, long chatId) {
        SendMessage sendMessage = new SendMessage()
                .setText(text)
                .setChatId(chatId);

        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void sendMessage(String text, long chatId, int messageId){
        SendMessage sendMessage = new SendMessage()
                .setText(text)
                .setChatId(chatId)
                .setReplyToMessageId(messageId);

        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
    private void hideKeyboard (String text, long chatId,int messageId){
        ReplyKeyboardRemove rkm = new ReplyKeyboardRemove();


        SendMessage sendMessage = new SendMessage()
                .setText(text)
                .setChatId(chatId)
                .setReplyToMessageId(messageId)
                .setReplyMarkup(rkm);

        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
    private void showKeyboard (String text, long chatId, int messageId){
        ReplyKeyboardMarkup rkm = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();
        row.add("/showKeyboard");
        row.add("/hideKeyboard");
        row.add("/getValuta");
        keyboard.add(row);
        rkm.setKeyboard(keyboard);

        SendMessage sendMessage = new SendMessage()
                .setText(text)
                .setChatId(chatId)
                .setReplyToMessageId(messageId)
                .setReplyMarkup(rkm);

        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
    private void sendPhoto(long chatId, String photo){
        SendPhoto request = new SendPhoto();
        request.setChatId(chatId);
        request.setPhoto(photo);
        try {
            sendPhoto(request);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

    }
    //===================================================================================







    private void getBitCoin(String name, long chatId){

        Document doc = null;
        try {
            doc = Jsoup.connect("https://ru.investing.com/currencies/" + name).get();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Elements importantLinks = doc.getElementsByAttributeValue("id", "last_last");

        for (Element link : importantLinks) {
            String linkText = link.html();
            String bitok = linkText;
            System.out.println(bitok);
            sendMessage("Курс " + bitok, chatId);

        }




    }


}



