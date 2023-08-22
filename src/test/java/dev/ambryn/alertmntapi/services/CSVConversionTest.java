package dev.ambryn.alertmntapi.services;

import dev.ambryn.alertmntapi.beans.Channel;
import dev.ambryn.alertmntapi.beans.Message;
import dev.ambryn.alertmntapi.beans.User;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.HexFormat;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

class CSVConversionTest {

    @Test
    void convert_shouldReturnCorrectByteArray() {
        // GIVEN
        CSVConversion csv = new CSVConversion();

        Channel channel = new Channel();
        User user1 = new User("test@test.com", "test", "Test", "Testeur");
        User user2 = new User("test2@test.com", "test", "Test2", "Testeur2");
        Message message1 = new Message(channel, user1, "Bonjour");
        message1.setSentAt(LocalDateTime.of(2023, 5, 26, 10, 0));
        Message message2 = new Message(channel, user2, "Coucou");
        message2.setSentAt(LocalDateTime.of(2023, 5, 26, 10, 0));
        channel.addMessage(message1);
        channel.addMessage(message2);

        byte[] result = HexFormat.ofDelimiter(" ")
                                 .parseHex(
                                         "54 65 73 74 65 75 72 2c 54 45 53 54 2c 32 30 32 33 2d 30 35 2d 32 36 54 31 " +
                                                 "30 3a 30 30 2c 42 6f 6e 6a 6f 75 72 0a 54 65 73 74 65 75 72 32 2c " +
                                                 "54 45 53 54 32 2c 32 30 32 33 2d 30 35 2d 32 36 54 31 30 3a 30 30 " +
                                                 "2c 43 6f 75 63 6f 75 0a");

        // THEN
        assertArrayEquals(result, csv.convert(channel));
    }

    @Test
    void convert_shouldReturnEmptyByteArrayIfNoMessages() {
        // GIVEN
        CSVConversion csv = new CSVConversion();
        Channel channel = new Channel();

        byte[] result = new byte[]{};

        // THEN
        assertArrayEquals(result, csv.convert(channel));
    }
}