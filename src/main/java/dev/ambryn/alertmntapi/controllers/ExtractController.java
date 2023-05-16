package dev.ambryn.alertmntapi.controllers;

import dev.ambryn.alertmntapi.beans.*;
import dev.ambryn.alertmntapi.dto.mappers.dto.UserMapper;
import dev.ambryn.alertmntapi.enums.FileFormat;
import dev.ambryn.alertmntapi.errors.BadRequestException;
import dev.ambryn.alertmntapi.errors.InternalServerException;
import dev.ambryn.alertmntapi.errors.NotFoundException;
import dev.ambryn.alertmntapi.repositories.ChannelRepository;
import dev.ambryn.alertmntapi.responses.Ok;
import dev.ambryn.alertmntapi.services.ExtractionService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.valueextraction.ExtractedValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/extract")
public class ExtractController {

    Logger logger = LoggerFactory.getLogger("UserController");

    @Autowired
    ChannelRepository channelRepository;

    @Autowired
    ExtractionService extractionService;

    @GetMapping(value = "/{id}")
    public byte[] extractChannel(HttpServletResponse res,
                                 @PathVariable("id") Long id,
                                 @Param("format") Optional<String> format) {
        logger.debug("Extracting Channel with id={}", id);

        FileFormat fileFormat = format.map(String::toUpperCase)
                                      .map(uppercaseFormat -> {
                                          try {
                                              return FileFormat.valueOf(uppercaseFormat);
                                          } catch (IllegalArgumentException e) {
                                              throw new BadRequestException("Format must be CSV, JSON or XML");
                                          }
                                      })
                                      .orElse(FileFormat.CSV);

        String now = LocalDateTime.now()
                                  .format(DateTimeFormatter.ofPattern("YMMd-H_m"));
        String filename = now + "_channel_" + id + fileFormat.getExtension();
        res.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"");

        Channel channel = channelRepository.findById(id)
                                           .orElseThrow(() -> new NotFoundException("Could not find channel with id=" + id));

        try {
            res.setHeader("Content-Type", fileFormat.getHeader());
            return extractionService.convert(channel, fileFormat);
        } catch (RuntimeException e) {
            throw new InternalServerException("Could not extract data from channel with id=" + channel.getId());
        }
    }
}
