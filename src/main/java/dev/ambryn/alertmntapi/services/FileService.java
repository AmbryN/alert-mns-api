package dev.ambryn.alertmntapi.services;

import dev.ambryn.alertmntapi.beans.Channel;
import dev.ambryn.alertmntapi.enums.FileFormat;
import dev.ambryn.alertmntapi.errors.InternalServerException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class FileService {

    public ResponseEntity<byte[]> generateFrom(Channel channel, FileFormat format) {

        ConversionStrategy strategy = switch (format) {
            case CSV -> {
                yield new CSVConversion();
            }
            case JSON -> {
                yield new JSONConversion();
            }
            case XML -> {
                yield new XMLConversion();
            }
        };
        ExtractionService es = new ExtractionService(strategy);

        String now = LocalDateTime.now()
                                  .format(DateTimeFormatter.ofPattern("YMMd-H_m"));
        String filename = now + "_channel_" + channel.getId() + format.getExtension();

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"");
        headers.set(HttpHeaders.CONTENT_TYPE, format.getHeader());

        try {
            byte[] binary = es.convert(channel);
            return new ResponseEntity<>(binary, headers, HttpStatus.OK);
        } catch (RuntimeException e) {
            throw new InternalServerException("Could not extract data from channel with id=" + channel.getId());
        }
    }
}
