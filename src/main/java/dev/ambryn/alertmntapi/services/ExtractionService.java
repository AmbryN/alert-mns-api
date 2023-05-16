package dev.ambryn.alertmntapi.services;

import dev.ambryn.alertmntapi.beans.*;
import dev.ambryn.alertmntapi.enums.FileFormat;
import org.springframework.stereotype.Service;

@Service
public class ExtractionService {

    public byte[] convert(Channel channel, FileFormat format) {
        ConversionStrategy strategy = null;
        switch (format) {
            case CSV -> {
                strategy = new CSVConversion();
            }
            case JSON -> {
                strategy = new JSONConversion();
            }
            case XML -> {
                strategy = new XMLConversion();
            }
        }
        return strategy.convert(channel);
    }
}
