package dev.ambryn.alertmntapi.controllers;

import dev.ambryn.alertmntapi.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
class UserControllerTest {

    @Autowired
    WebApplicationContext context;

    @Autowired
    UserRepository userRepository;

    MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.webAppContextSetup(context)
                             .apply(springSecurity())
                             .build();
    }

    @Test
    void unauthenticatedUserAccessGetUsers_403() throws Exception {
        mvc.perform(get("/users"))
           .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser
    void authenticatedUserAccessGetUsers_200OK() throws Exception {
        mvc.perform(get("/users"))
           .andExpect(status().isOk())
           .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void adminAccessGetUsers_200OK() throws Exception {
        mvc.perform(get("/users"))
           .andExpect(status().isOk())
           .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void unauthenticatedUserAccessGetUser_403() throws Exception {
        mvc.perform(get("/users/1"))
           .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser
    void authenticatedUserAccessGetUser1_403() throws Exception {
        mvc.perform(get("/users/1"))
           .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void adminAccessGetUser1_200OK() throws Exception {
        mvc.perform(get("/users"))
           .andExpect(status().isOk())
           .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void unauthenticatedUserAccessGetProfile_403() throws Exception {
        mvc.perform(get("/profile"))
           .andExpect(status().isForbidden());
    }

    @Test
    void unauthenticatedUserAccessSaveUser_403() throws Exception {
        mvc.perform(post("/users"))
           .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser
    void authenticatedUserAccessSaveUser_403() throws Exception {
        mvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON)
                                  .content("{\"email\": \"test@test.com\",\"password\": \"testtest\",\"firstname\": " + "\"test\",\"lastname\": \"Test\"}"))
           .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void authenticatedUserAccessSaveUser_200() throws Exception {
        mvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON)
                                  .content("{\"email\": \"test@test.com\",\"password\": \"testtest\",\"firstname\": " + "\"test\",\"lastname\": \"Test\"}"))
           .andExpect(status().isCreated())
           .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void unauthenticatedUserAccessAddRole_403() throws Exception {
        mvc.perform(post("/1/roles"))
           .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser
    @Disabled
    void authenticatedUserAccessAddRole_403() throws Exception {
        mvc.perform(post("/1/roles").contentType(MediaType.APPLICATION_JSON)
                                    .content("{\"id\": 1}"))
           .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    @Disabled
    void adminAccessAddRole_200OK() throws Exception {
        mvc.perform(post("/1/roles").contentType(MediaType.APPLICATION_JSON)
                                    .content("{\"id\": 1}"))
           .andExpect(status().isOk());
    }
}