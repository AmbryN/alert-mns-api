package dev.ambryn.alertmntapi.services;

import dev.ambryn.alertmntapi.beans.Channel;

public interface ConversionStrategy {
    byte[] convert(Channel channel);
}
