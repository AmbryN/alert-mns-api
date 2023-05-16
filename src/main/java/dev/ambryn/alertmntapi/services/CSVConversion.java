package dev.ambryn.alertmntapi.services;

import dev.ambryn.alertmntapi.beans.Channel;
import dev.ambryn.alertmntapi.beans.Message;
import dev.ambryn.alertmntapi.beans.User;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

public class CSVConversion implements ConversionStrategy {
    public byte[] convert(Channel channel) {
        String[] csvLines = channel.getMessages()
                                   .stream()
                                   .map(this::convertToCSVLine)
                                   .toArray(String[]::new);
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            Arrays.stream(csvLines)
                  .map(String::getBytes)
                  .forEach(bos::writeBytes);
            return bos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Could not open OutputStream");
        }
    }

    private String convertToCSVLine(Message message) {
        User sender = message.getSender();
        String joinedString = String.join(",",
                                          new String[]{sender.getFirstname(), sender.getLastname(),
                                                  message.getSentAt().toString(), message.getContent()});
        return joinedString + "\n";
    }
}
