package dev.ambryn.alertmntapi.responses;

import dev.ambryn.alertmntapi.errors.ApplicationError;

public record ErrorResponse(ApplicationError error) {}
