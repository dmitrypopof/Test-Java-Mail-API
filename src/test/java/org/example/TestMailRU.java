package org.example;

import org.junit.jupiter.api.Test;

import javax.mail.*;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import static org.example.ReadEmail.getTextFromMimeMultipart;

public class TestMailRU extends TestBase{
    @Test
    public void checkWriteInbox() throws IOException, MessagingException {
        FileInputStream fileInputStream = new FileInputStream("config.properties");
        Properties properties = new Properties();
        properties.load(fileInputStream);
        String  user = properties.getProperty("mail.user");
        String password = properties.getProperty("mail.password");
        String host = properties.getProperty("mail.host");
        //Настройка почтового сервера:
        Properties prop = new Properties();
        prop.put("mail.store.protocol","imaps");//SSL
        //создать хранилище сообщений:
        Store store = Session.getInstance(prop).getStore();
        store.connect(host,user, password);// подключение хранилища
        //открытие почтового ящика и получение сообщений
        Folder inbox = store.getFolder("INBOX");// создание папки ВХОДЯЩИЕ
        inbox.open(Folder.READ_ONLY);

        System.out.println("Count message: " + inbox.getMessageCount());

        //Поиск последнего сообщения
        Message message = inbox.getMessage(inbox.getMessageCount());
        Multipart multipart = (Multipart) message.getContent(); // сохранение письма в структурированный вид
        BodyPart bodyPart = multipart.getBodyPart(0);
        System.out.println(bodyPart.getContent().toString());
        String content = bodyPart.getContent().toString();
        System.out.println(content);
//        System.out.println(multipart.getContentType());
//        BodyPart body = multipart.getBodyPart(1);
//        System.out.println(body.getContent());

        //поиск определенного сообщения:
        Message[] messages = inbox.getMessages();
//        String subjectToFind = "Пароль ГГИС изменен!";//тема сообщения
//        Message foundMessage = null;
//        for(Message message1: messages) {
//            if(message1.getSubject().equals(subjectToFind)){
//                foundMessage = message1;
//                break;
//            }
//        }

        //Проверка, найдено ли сообщение
//        if(foundMessage!=null){
//            System.out.println("Message found! ");
//        } else {
//            System.out.println("Message not found! ");
//        }





        if(messages.length > 0){
            Message lastMessage = messages[messages.length-1];
            String textTheme = lastMessage.getSubject().toString(); // тема письма
            System.out.println("Theme: "+textTheme);

            String textContentType = lastMessage.getContentType().toString();
            System.out.println(textContentType);

            System.out.println(lastMessage.getSentDate().toString()); // дата получения

            System.out.println(lastMessage.getFrom()[0].toString()); // отправитель

           // System.out.println((MimeMessage) lastMessage).getContent(); // отправитель

        }



        inbox.close(false);
        store.close();


    }

    @Test
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

            // Получение последнего письма
            Message[] messages = inbox.getMessages();
            Message lastMessage = messages[messages.length - 1];

            System.out.println("Theme: "+ lastMessage.getSubject());

            MimeMultipart multipart = (MimeMultipart) lastMessage.getContent();
            int count = multipart.getCount();// узнать количество частей контента
            System.out.println(count);
            BodyPart bodyPart = multipart.getBodyPart(0); // узнать количество частей контента




            System.out.println("Theme: " + lastMessage.getSubject());
            String content = getTextFromMimeMultipart((Multipart) lastMessage.getContent());
            System.out.println("Content: " + content);
            // Закрытие соединения
            inbox.close(false);
            store.close();
        }catch (Exception e){e.printStackTrace();}
    }
}


