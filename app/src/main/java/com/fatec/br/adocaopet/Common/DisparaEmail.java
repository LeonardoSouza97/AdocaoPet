package com.fatec.br.adocaopet.Common;

import android.os.AsyncTask;

import java.util.Properties;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class DisparaEmail extends AsyncTask<String, Void, Boolean> {

    @Override
    protected Boolean doInBackground(String... params) {

        String assunto = params[0];
        String mensagem = params[1];
        String destinatario = params[2];

        Session session;

        {
            Properties props = new Properties();
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.socketFactory.port", "465");
            props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.port", "465");

            session = Session.getDefaultInstance(props,
                    new javax.mail.Authenticator() {
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication("adota.email.petbox@gmail.com", "petbox123");
                        }
                    });

            session.setDebug(false);

        }

        try {

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("adota.email.petbox@gmail.com"));

            Address[] emailDestinatario = InternetAddress.parse(destinatario);

            message.setRecipients(Message.RecipientType.TO, emailDestinatario);
            message.setSubject(assunto);
            message.setText(mensagem);
            Transport.send(message);

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }

        return true;
    }


    public void enviar(String assunto, String mensagem, String destinatario) {
            new DisparaEmail().execute(assunto, mensagem, destinatario);
    }
}