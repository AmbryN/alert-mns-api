package dev.ambryn.alertmntapi.controllers;

import dev.ambryn.alertmntapi.beans.Channel;
import dev.ambryn.alertmntapi.beans.User;
import dev.ambryn.alertmntapi.enums.FileFormat;
import dev.ambryn.alertmntapi.errors.BadRequestException;
import dev.ambryn.alertmntapi.errors.ForbiddenException;
import dev.ambryn.alertmntapi.errors.NotFoundException;
import dev.ambryn.alertmntapi.repositories.ChannelRepository;
import dev.ambryn.alertmntapi.repositories.UserRepository;
import dev.ambryn.alertmntapi.services.AuthorizationUtils;
import dev.ambryn.alertmntapi.services.FileService;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/export")
public class ExportController {

    Logger logger = LoggerFactory.getLogger("ExportController");

    @Autowired
    ChannelRepository channelRepository;
    @Autowired
    UserRepository userRepository;

    @Autowired
    FileService fileService;

    @GetMapping(value = "/{id}")
    public ResponseEntity<byte[]> exportChannel(HttpServletResponse res,
                                                @PathVariable("id") Long id,
                                                @Param("format") Optional<String> format) {
        logger.debug("Exporting Channel with id={}", id);

        Channel channel = channelRepository.findById(id)
                                           .orElseThrow(() -> new NotFoundException("Could not find channel with id=" + id));
        UserDetails userdetails = ((UserDetails) SecurityContextHolder.getContext()
                                                                      .getAuthentication()
                                                                      .getPrincipal());
        User user = userRepository.findByEmail(userdetails.getUsername())
                                  .orElseThrow(() -> new NotFoundException("Could not find user with email=" + userdetails.getUsername()));

        boolean isAllowedToExport = AuthorizationUtils.isMemberOrAdmin(user, channel);
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

        return fileService.generateFrom(channel, fileFormat);
    }
}
