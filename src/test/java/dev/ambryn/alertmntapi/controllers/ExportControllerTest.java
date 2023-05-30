package dev.ambryn.alertmntapi.controllers;

import dev.ambryn.alertmntapi.AlertMnsApiApplication;
import dev.ambryn.alertmntapi.beans.Channel;
import dev.ambryn.alertmntapi.beans.Role;
import dev.ambryn.alertmntapi.beans.User;
import dev.ambryn.alertmntapi.enums.ERole;
import dev.ambryn.alertmntapi.enums.EVisibility;
import dev.ambryn.alertmntapi.repositories.ChannelRepository;
import dev.ambryn.alertmntapi.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = {AlertMnsApiApplication.class})
@Transactional
class ExportControllerTest {
    @Autowired
    WebApplicationContext context;

    MockMvc mvc;

    @MockBean
    ChannelRepository channelRepository;
    @MockBean
    UserRepository userRepository;

    @BeforeEach
    void setup() {
        mvc = MockMvcBuilders.webAppContextSetup(context)
                             .apply(springSecurity())
                             .build();
    }

    @Test
    void unauthenticatedUser_AccessExportChannel_403() throws Exception {
        // WHEN
        when(channelRepository.findById(1L)).thenReturn(Optional.of(new Channel("Test", EVisibility.PUBLIC)));

        // THEN
        mvc.perform(get("/export/1"))
           .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser
    void authenticatedUserButNotMember_AccessExportChannel_403() throws Exception {
        // GIVEN
        when(channelRepository.findById(1L)).thenReturn(Optional.of(new Channel("Test", EVisibility.PUBLIC)));
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(new User()));

        mvc.perform(get("/export/{id}", 1))
           .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser
    void authenticatedUserMember_AccessExportChannel_200() throws Exception {
        // GIVEN
        Channel channel = new Channel("Test", EVisibility.PUBLIC);
        User user = new User("test@test.com", "test", "Test", "Test");
        user.addRole(new Role(ERole.ROLE_USER));
        channel.addMember(user);

        // WHEN
        when(channelRepository.findById(1L)).thenReturn(Optional.of(channel));
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

        // THEN
        mvc.perform(get("/export/{id}", 1))
           .andExpect(status().isOk())
           .andExpect(content().contentType("text/csv"));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void admin_AccessExportChannel_200() throws Exception {
        // GIVEN
        Channel channel = new Channel("Test", EVisibility.PUBLIC);
        User user = new User("test@test.com", "test", "Test", "Test");
        user.addRole(new Role(ERole.ROLE_ADMIN));

        // WHEN
        when(channelRepository.findById(1L)).thenReturn(Optional.of(channel));
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

        // THEN
        mvc.perform(get("/export/{id}", 1))
           .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void admin_AsksForCSV_200_csv() throws Exception {
        // GIVEN
        Channel channel = new Channel("Test", EVisibility.PUBLIC);
        User user = new User("test@test.com", "test", "Test", "Test");
        user.addRole(new Role(ERole.ROLE_ADMIN));

        // WHEN
        when(channelRepository.findById(1L)).thenReturn(Optional.of(channel));
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

        // THEN
        mvc.perform(get("/export/{id}", 1).param("format", "csv"))
           .andExpect(status().isOk())
           .andExpect(content().contentType("text/csv"));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void admin_AsksForJSON_200_json() throws Exception {
        // GIVEN
        Channel channel = new Channel("Test", EVisibility.PUBLIC);
        User user = new User("test@test.com", "test", "Test", "Test");
        user.addRole(new Role(ERole.ROLE_ADMIN));

        // WHEN
        when(channelRepository.findById(1L)).thenReturn(Optional.of(channel));
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

        // THEN
        mvc.perform(get("/export/{id}", 1).param("format", "json"))
           .andExpect(status().isOk())
           .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void admin_AsksForXML_200_xml() throws Exception {
        // GIVEN
        Channel channel = new Channel("Test", EVisibility.PUBLIC);
        User user = new User("test@test.com", "test", "Test", "Test");
        user.addRole(new Role(ERole.ROLE_ADMIN));

        // WHEN
        when(channelRepository.findById(1L)).thenReturn(Optional.of(channel));
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

        // THEN
        mvc.perform(get("/export/{id}", 1).param("format", "xml"))
           .andExpect(status().isOk())
           .andExpect(content().contentType(MediaType.TEXT_XML));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void admin_AsksForInvalidFormat_400() throws Exception {
        // GIVEN
        Channel channel = new Channel("Test", EVisibility.PUBLIC);
        User user = new User("test@test.com", "test", "Test", "Test");
        user.addRole(new Role(ERole.ROLE_ADMIN));

        // WHEN
        when(channelRepository.findById(1L)).thenReturn(Optional.of(channel));
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

        // THEN
        mvc.perform(get("/export/{id}", 1).param("format", "test"))
           .andExpect(status().isBadRequest());
    }
}