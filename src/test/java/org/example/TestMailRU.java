package org.example;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.mail.*;
import javax.mail.search.FlagTerm;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class TestMailRU {
    @Test
    @DisplayName("Чтение входящего сообщения")
    public void testLastMail() throws IOException {
        FileInputStream fileInputStream = new FileInputStream("config.properties");
        Properties properties = new Properties();
        properties.load(fileInputStream);
        String  user = properties.getProperty("mail.user");
        String password = properties.getProperty("mail.password");
        String host = properties.getProperty("mail.host");

        // Настройка свойств
        Properties props = new Properties();
        props.put("mail.store.protocol", "imaps");
        props.put("mail.imaps.host", host);
        props.put("mail.imaps.port", "993");
        props.put("mail.imaps.ssl.enable", "true");

        try{
            // Установка сессии
            Session session = Session.getDefaultInstance(props);

            // Подключение к почтовому серверу
            Store store = session.getStore("imaps");
            store.connect(host, user, password);

            // Открытие папки "INBOX"
            Folder inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_ONLY);

            /// Получение последнего письма
            Message[] messages = inbox.getMessages();
            Message lastMessage = messages[messages.length - 1];

            //Тема последнего сообщения:
            System.out.println("Subject:\n"+ lastMessage.getSubject());

            //Преобразование последнего сообщения в String c помощью метода getTextFromMimeMultipart
            String content = getTextFromMimeMultipart((Multipart) lastMessage.getContent());
            System.out.println("Content:\n" + content);
            // Закрытие соединения
            inbox.close(false);
            store.close();
        }catch (Exception e){e.printStackTrace();}
    }

    //Преобразование последнего сообщения в String:
    public static String getTextFromMimeMultipart(Multipart mimeMultipart) throws MessagingException, IOException {
        int count = mimeMultipart.getCount();
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < count; i++) {
            BodyPart bodyPart = mimeMultipart.getBodyPart(i);
            if (bodyPart.isMimeType("text/plain")) {
                result.append(bodyPart.getContent());
            } else if (bodyPart.isMimeType("multipart/*")) {
                result.append(getTextFromMimeMultipart((Multipart) bodyPart.getContent()));
            }
        }
        return result.toString();
    }

    @Test
    @DisplayName("Удаление всех сообщений")
    public void testAllDeleteInboxMail() throws IOException {
        FileInputStream fileInputStream = new FileInputStream("config.properties");
        Properties properties = new Properties();
        properties.load(fileInputStream);
        String  user = properties.getProperty("mail.user");
        String password = properties.getProperty("mail.password");
        String host = properties.getProperty("mail.host");

        // Настройка свойств
        Properties props = new Properties();
        props.put("mail.store.protocol", "imaps");
        props.put("mail.imaps.host", host);
        props.put("mail.imaps.port", "993");
        props.put("mail.imaps.ssl.enable", "true");
        try{
            // Установка сессии
            Session session = Session.getDefaultInstance(props);

            // Создание объекта Store и подключение к почтовому серверу
            Store store = session.getStore("imaps");
            store.connect(host, user, password);

            // Открытие папки "INBOX"
            Folder inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_WRITE);

            // Получение всех сообщений в папке
            Message[]messages = inbox.search( new FlagTerm(new Flags(Flags.Flag.SEEN), false));

            // Удаление каждого сообщения
            for (Message message : messages) {
                message.setFlag(Flags.Flag.DELETED, true);
            }

            // Закрытие папки и сохранение изменений
            inbox.close(true);
            // Закрытие соединения с почтовым сервером
            store.close();

            System.out.println("Все сообщения во входящих удалены.");

        }catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    @Test
    @DisplayName("Удаление сообщений с определенной темой")
    public void testDeleteInboxMail() throws IOException {
        String text = "Стать системным администратором";
        FileInputStream fileInputStream = new FileInputStream("config.properties");
        Properties properties = new Properties();
        properties.load(fileInputStream);
        String  user = properties.getProperty("mail.user");
        String password = properties.getProperty("mail.password");
        String host = properties.getProperty("mail.host");

        // Настройка свойств
        Properties props = new Properties();
        props.put("mail.store.protocol", "imaps");
        props.put("mail.pop3.host", host);
        props.put("mail.pop3.port", "995");
        props.put("mail.pop3.starttls.enable", "true");

        try{
            // Установка сессии
            Session session = Session.getDefaultInstance(props);
session.setDebug(true); // подключение логов
            // Подключение к почтовому серверу
            Store store = session.getStore("pop3s");
            store.connect(host, user, password);

            // Открытие папки "INBOX"
            Folder inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_WRITE);

            // Получение всех сообщений в папке
            Message[]messages = inbox.search( new FlagTerm(new Flags(Flags.Flag.SEEN), false));

            // Удаление каждого сообщения
            for (Message message : messages) {
                if(message.getSubject().contains(text)) {
                    message.setFlag(Flags.Flag.DELETED, true);
                    inbox.expunge();
                }
                //inbox.expunge(); //Этот метод физически удаляет сообщения, отмеченные для удаления
            }


            // Закрытие папки и сохранение изменений
            inbox.close(true);

            // Закрытие соединения с почтовым сервером
            store.close();

            System.out.println("Все сообщения во входящих удалены c темой: " + text);

        }catch (Exception e){e.printStackTrace();}
    }

    @Test
    @DisplayName("Узнать тип контента в письме")
    public void getTypeContent() throws IOException, MessagingException {
        FileInputStream fileInputStream = new FileInputStream("config.properties");
        Properties properties = new Properties();
        properties.load(fileInputStream);
        String  user = properties.getProperty("mail.user");
        String password = properties.getProperty("mail.password");
        String host = properties.getProperty("mail.host");

        // Настройка свойств
        Properties props = new Properties();
        props.put("mail.store.protocol", "imaps");
        props.put("mail.imaps.host", host);
        props.put("mail.imaps.port", "993");
        props.put("mail.imaps.ssl.enable", "true");

        // Установка сессии
        Session session = Session.getDefaultInstance(props);

        // Подключение к почтовому серверу
        Store store = session.getStore("imaps");
        store.connect(host, user, password);

        // Открытие папки "INBOX"
        Folder inbox = store.getFolder("INBOX");
        inbox.open(Folder.READ_WRITE);

        System.out.println("Количество писем: " + inbox.getMessageCount());

        Message message = inbox.getMessage(inbox.getMessageCount());
        Multipart multipart = (Multipart) message.getContent();
        System.out.println(message.getSubject().toString());
        System.out.println("Тип контента: "+ multipart.getContentType());

    }

}


