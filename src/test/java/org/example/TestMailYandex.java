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
        //��������� ��������� �������:
        Properties prop = new Properties();
        prop.put("mail.store.protocol","imaps");//SSL
        //������� ��������� ���������:
        Store store = Session.getInstance(prop).getStore();
        store.connect(host,user, password);// ����������� ���������
        //�������� ��������� ����� � ��������� ���������
        Folder inbox = store.getFolder("INBOX");// �������� ����� ��������
        inbox.open(Folder.READ_ONLY);

        System.out.println("���������� �����: " + inbox.getMessageCount());

        //����� ���������� ���������
        Message message = inbox.getMessage(inbox.getMessageCount());
        Multipart multipart = (Multipart) message.getContent(); // ���������� ������ � ����������������� ���
        System.out.println(multipart.getContentType());
        BodyPart body = multipart.getBodyPart(0);
        System.out.println(body.getContent());

        //����� ������������� ���������:
        Message[] messages = inbox.getMessages();
        String subjectToFind = "������ ���� �������!";//���� ���������
        Message foundMessage = null;
        for(Message message1: messages) {
            if(message1.getSubject().equals(subjectToFind)){
                foundMessage = message1;
                break;
            }
        }

        //��������, ������� �� ���������
        if(foundMessage!=null){
            System.out.println("��������� �������");
        } else {
            System.out.println("��������� �� �������");
        }

        inbox.close(false);
        store.close();


    }
}
