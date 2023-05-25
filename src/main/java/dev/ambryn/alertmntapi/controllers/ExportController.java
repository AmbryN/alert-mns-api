package dev.ambryn.alertmntapi.controllers;

import dev.ambryn.alertmntapi.beans.Channel;
import dev.ambryn.alertmntapi.beans.User;
import dev.ambryn.alertmntapi.enums.FileFormat;
import dev.ambryn.alertmntapi.errors.BadRequestException;
import dev.ambryn.alertmntapi.errors.ForbiddenException;
import dev.ambryn.alertmntapi.errors.InternalServerException;
import dev.ambryn.alertmntapi.errors.NotFoundException;
import dev.ambryn.alertmntapi.repositories.ChannelRepository;
import dev.ambryn.alertmntapi.security.MyUserDetails;
import dev.ambryn.alertmntapi.services.AuthorizationService;
import dev.ambryn.alertmntapi.services.ExtractionService;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@RestController
@RequestMapping("/export")
public class ExportController {

    Logger logger = LoggerFactory.getLogger("ExportController");

    @Autowired
    ChannelRepository channelRepository;
    @Autowired
    AuthorizationService authorizationService;

    @Autowired
    ExtractionService extractionService;

    @GetMapping(value = "/{id}")
    public byte[] exportChannel(HttpServletResponse res,
                                @PathVariable("id") Long id,
                                @Param("format") Optional<String> format) {
        logger.debug("Exporting Channel with id={}", id);

        Channel channel = channelRepository.findById(id)
                                           .orElseThrow(() -> new NotFoundException("Could not find channel with id=" + id));
        User user = ((MyUserDetails) SecurityContextHolder.getContext()
                                                          .getAuthentication()
                                                          .getPrincipal()).getUser();

        boolean isAllowedToExport = authorizationService.isMemberOrAdmin(user, channel);
        if (!isAllowedToExport) throw new ForbiddenException("You do not have permissions to do this action");

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

        try {
            res.setHeader("Content-Type", fileFormat.getHeader());
            return extractionService.convert(channel, fileFormat);
        } catch (RuntimeException e) {
            throw new InternalServerException("Could not extract data from channel with id=" + channel.getId());
        }
    }
}
