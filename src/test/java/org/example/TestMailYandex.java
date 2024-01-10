package org.example;

import org.junit.jupiter.api.Test;

import javax.mail.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class TestMailYandex extends TestBase{
    @Test
    public void checkWriteInbox() throws IOException, MessagingException {
        FileInputStream fileInputStream = new FileInputStream("config.properties");
        Properties properties = new Properties();
        properties.load(fileInputStream);

        String  user = properties.getProperty("mail.user");
        System.out.println(user);
        String password = properties.getProperty("mail.password");
        System.out.println(password);
        String host = properties.getProperty("mail.host");
        System.out.println(host);

        Properties prop = new Properties();
        prop.put("mail.store.protocol","imaps");//SSL
        //������� ��������� ���������:
        Store store = Session.getInstance(prop).getStore();
        store.connect(host,user, password);// ����������� ���������
        Folder inbox = store.getFolder("INBOX");// ����� ��������
        inbox.open(Folder.READ_WRITE);

        System.out.println("���������� �����: " + inbox.getMessageCount());

        //����� ���������� ���������
        Message message = inbox.getMessage(inbox.getMessageCount());

        Multipart multipart = (Multipart) message.getContent(); // ���������� ������ � ����������������� ���
        System.out.println(multipart.getContentType());

        BodyPart body = multipart.getBodyPart(0);
        System.out.println(body.getContent());

String textActual = body.getContent().toString();
        System.out.println("����� ���������: " + textActual);

    }
}
