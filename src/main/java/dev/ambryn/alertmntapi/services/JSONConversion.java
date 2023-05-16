package dev.ambryn.alertmntapi.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.ambryn.alertmntapi.beans.Channel;
import dev.ambryn.alertmntapi.beans.ExtractedMessage;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class JSONConversion implements ConversionStrategy {
    public byte[] convert(Channel channel) {
        ExtractedMessage[] messages = channel.getMessages()
                                             .stream()
                                             .map(message -> new ExtractedMessage(message.getSender()
                                                                                         .getFirstname(),
                                                                                  message.getSender()
                                                                                         .getLastname(),
                                                                                  message.getSentAt()
                                                                                         .toString(),
                                                                                  message.getContent()))

                                             .toArray(ExtractedMessage[]::new);

        ObjectMapper om = new ObjectMapper();
        try {
            String stringifiedMessages = om.writeValueAsString(messages);

            try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
                bos.writeBytes(stringifiedMessages.getBytes());
                return bos.toByteArray();
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException("Could not open OutputStream");
        }
    }
}
