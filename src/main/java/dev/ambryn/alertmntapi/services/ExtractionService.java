package dev.ambryn.alertmntapi.services;

import dev.ambryn.alertmntapi.beans.Channel;
import org.springframework.stereotype.Service;

@Service
public class ExtractionService {

    ConversionStrategy strategy;

    public ExtractionService() {
        this.strategy = new CSVConversion();
    }

    public ExtractionService(ConversionStrategy strategy) {
        this.strategy = strategy;
    }

    public byte[] convert(Channel channel) {
        return strategy.convert(channel);
    }
}
