package dev.ambryn.alertmntapi.enums;

import dev.ambryn.alertmntapi.errors.BadRequestException;

import java.util.Optional;

public enum FileFormat {

    CSV("text/csv", ".csv"), JSON("application/json", ".json"), XML("text/xml", ".xml");

    private String header;
    private String extension;

    FileFormat(String header, String extension) {
        this.header = header;
        this.extension = extension;
    }

    public String getHeader() {
        return header;
    }

    public String getExtension() {
        return extension;
    }
}
