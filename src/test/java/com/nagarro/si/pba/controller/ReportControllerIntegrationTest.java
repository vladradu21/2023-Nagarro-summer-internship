package com.nagarro.si.pba.controller;

import com.nagarro.si.pba.dto.ReportRequestDTO;
import com.nagarro.si.pba.model.User;
import com.nagarro.si.pba.repository.AbstractMySQLContainer;
import com.nagarro.si.pba.repository.UserRepository;
import com.nagarro.si.pba.security.JWTGenerator;
import com.nagarro.si.pba.utils.TestData;
import com.nagarro.si.pba.utils.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@ActiveProfiles("inttest")
@AutoConfigureMockMvc(addFilters = false)
@Sql(scripts = "classpath:/script/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class ReportControllerIntegrationTest extends AbstractMySQLContainer {

    private final User user = TestData.returnUserForDonJoe();
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JWTGenerator jwtGenerator;

    @BeforeEach
    void setup() {
        userRepository.save(user);
    }

    @Test
    void testGenerateTransactionReport() throws Exception {
        ReportRequestDTO reportRequestDTO = TestData.returnIncomesReportRequestDTO(); // Complete with your test data
        String jwt = jwtGenerator.generateJwtForLogin(user.getId(), user.getUsername());


        MvcResult result = mockMvc.perform(post("/reports/transactions")
                        .header("Authorization", "Bearer " + jwt)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtils.OBJECT_MAPPER.writeValueAsString(reportRequestDTO))
                        .requestAttr("jwtToken", jwt))
                .andExpect(status().isOk())
                .andExpect(header().exists(HttpHeaders.CONTENT_DISPOSITION))
                .andReturn();

        MockHttpServletResponse response = result.getResponse();
        String header = response.getHeader(HttpHeaders.CONTENT_DISPOSITION);
        assertTrue(header.startsWith("attachment; filename="));
        assertTrue(response.getContentLength() > 0);
        assertEquals(MediaType.APPLICATION_OCTET_STREAM_VALUE, response.getContentType());
    }
}
