package org.example;

import org.junit.jupiter.api.Test;

import javax.mail.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class TestMailYandex extends TestBase{
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

        System.out.println("Количество писем: " + inbox.getMessageCount());

        //Поиск последнего сообщения
        Message message = inbox.getMessage(inbox.getMessageCount());
        Multipart multipart = (Multipart) message.getContent(); // сохранение письма в структурированный вид
        System.out.println(multipart.getContentType());
        BodyPart body = multipart.getBodyPart(0);
        System.out.println(body.getContent());

        //поиск определенного сообщения:
        Message[] messages = inbox.getMessages();
        String subjectToFind = "Пароль ГГИС изменен!";//тема сообщения
        Message foundMessage = null;
        for(Message message1: messages) {
            if(message1.getSubject().equals(subjectToFind)){
                foundMessage = message1;
                break;
            }
        }

        //Проверка, найдено ли сообщение
        if(foundMessage!=null){
            System.out.println("Сообщение найдено");
        } else {
            System.out.println("Сообщение не найдено");
        }

        inbox.close(false);
        store.close();


    }
}
