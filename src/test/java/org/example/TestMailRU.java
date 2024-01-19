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
    @DisplayName("������ ��������� ���������")
    public void testLastMail() throws IOException {
        FileInputStream fileInputStream = new FileInputStream("config.properties");
        Properties properties = new Properties();
        properties.load(fileInputStream);
        String  user = properties.getProperty("mail.user");
        String password = properties.getProperty("mail.password");
        String host = properties.getProperty("mail.host");

        // ��������� �������
        Properties props = new Properties();
        props.put("mail.store.protocol", "imaps");
        props.put("mail.imaps.host", host);
        props.put("mail.imaps.port", "993");
        props.put("mail.imaps.ssl.enable", "true");

        try{
            // ��������� ������
            Session session = Session.getDefaultInstance(props);

            // ����������� � ��������� �������
            Store store = session.getStore("imaps");
            store.connect(host, user, password);

            // �������� ����� "INBOX"
            Folder inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_ONLY);

            /// ��������� ���������� ������
            Message[] messages = inbox.getMessages();
            Message lastMessage = messages[messages.length - 1];

            //���� ���������� ���������:
            System.out.println("Subject:\n"+ lastMessage.getSubject());

            //�������������� ���������� ��������� � String c ������� ������ getTextFromMimeMultipart
            String content = getTextFromMimeMultipart((Multipart) lastMessage.getContent());
            System.out.println("Content:\n" + content);
            // �������� ����������
            inbox.close(false);
            store.close();
        }catch (Exception e){e.printStackTrace();}
    }

    //�������������� ���������� ��������� � String:
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
    @DisplayName("�������� ���� ���������")
    public void testAllDeleteInboxMail() throws IOException {
        FileInputStream fileInputStream = new FileInputStream("config.properties");
        Properties properties = new Properties();
        properties.load(fileInputStream);
        String  user = properties.getProperty("mail.user");
        String password = properties.getProperty("mail.password");
        String host = properties.getProperty("mail.host");

        // ��������� �������
        Properties props = new Properties();
        props.put("mail.store.protocol", "imaps");
        props.put("mail.imaps.host", host);
        props.put("mail.imaps.port", "993");
        props.put("mail.imaps.ssl.enable", "true");
        try{
            // ��������� ������
            Session session = Session.getDefaultInstance(props);

            // �������� ������� Store � ����������� � ��������� �������
            Store store = session.getStore("imaps");
            store.connect(host, user, password);

            // �������� ����� "INBOX"
            Folder inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_WRITE);

            // ��������� ���� ��������� � �����
            Message[]messages = inbox.search( new FlagTerm(new Flags(Flags.Flag.SEEN), false));

            // �������� ������� ���������
            for (Message message : messages) {
                message.setFlag(Flags.Flag.DELETED, true);
            }

            // �������� ����� � ���������� ���������
            inbox.close(true);
            // �������� ���������� � �������� ��������
            store.close();

            System.out.println("��� ��������� �� �������� �������.");

        }catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    @Test
    @DisplayName("�������� ��������� � ������������ �����")
    public void testDeleteInboxMail() throws IOException {
        String text = "����� ��������� ���������������";
        FileInputStream fileInputStream = new FileInputStream("config.properties");
        Properties properties = new Properties();
        properties.load(fileInputStream);
        String  user = properties.getProperty("mail.user");
        String password = properties.getProperty("mail.password");
        String host = properties.getProperty("mail.host");

        // ��������� �������
        Properties props = new Properties();
        props.put("mail.store.protocol", "imaps");
        props.put("mail.pop3.host", host);
        props.put("mail.pop3.port", "995");
        props.put("mail.pop3.starttls.enable", "true");

        try{
            // ��������� ������
            Session session = Session.getDefaultInstance(props);
session.setDebug(true); // ����������� �����
            // ����������� � ��������� �������
            Store store = session.getStore("pop3s");
            store.connect(host, user, password);

            // �������� ����� "INBOX"
            Folder inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_WRITE);

            // ��������� ���� ��������� � �����
            Message[]messages = inbox.search( new FlagTerm(new Flags(Flags.Flag.SEEN), false));

            // �������� ������� ���������
            for (Message message : messages) {
                if(message.getSubject().contains(text)) {
                    message.setFlag(Flags.Flag.DELETED, true);
                    inbox.expunge();
                }
                //inbox.expunge(); //���� ����� ��������� ������� ���������, ���������� ��� ��������
            }


            // �������� ����� � ���������� ���������
            inbox.close(true);

            // �������� ���������� � �������� ��������
            store.close();

            System.out.println("��� ��������� �� �������� ������� c �����: " + text);

        }catch (Exception e){e.printStackTrace();}
    }

    @Test
    @DisplayName("������ ��� �������� � ������")
    public void getTypeContent() throws IOException, MessagingException {
        FileInputStream fileInputStream = new FileInputStream("config.properties");
        Properties properties = new Properties();
        properties.load(fileInputStream);
        String  user = properties.getProperty("mail.user");
        String password = properties.getProperty("mail.password");
        String host = properties.getProperty("mail.host");

        // ��������� �������
        Properties props = new Properties();
        props.put("mail.store.protocol", "imaps");
        props.put("mail.imaps.host", host);
        props.put("mail.imaps.port", "993");
        props.put("mail.imaps.ssl.enable", "true");

        // ��������� ������
        Session session = Session.getDefaultInstance(props);

        // ����������� � ��������� �������
        Store store = session.getStore("imaps");
        store.connect(host, user, password);

        // �������� ����� "INBOX"
        Folder inbox = store.getFolder("INBOX");
        inbox.open(Folder.READ_WRITE);

        System.out.println("���������� �����: " + inbox.getMessageCount());

        Message message = inbox.getMessage(inbox.getMessageCount());
        Multipart multipart = (Multipart) message.getContent();
        System.out.println(message.getSubject().toString());
        System.out.println("��� ��������: "+ multipart.getContentType());

    }

}


