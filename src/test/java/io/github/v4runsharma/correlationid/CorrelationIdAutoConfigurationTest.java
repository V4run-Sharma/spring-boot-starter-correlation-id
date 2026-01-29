package io.github.v4runsharma.correlationid;

import io.github.v4runsharma.correlationid.testapp.TestApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = TestApplication.class)
@AutoConfigureMockMvc
class CorrelationIdAutoConfigurationTest {

  @Autowired
  private MockMvc mockMvc;

  @Test
  void shouldGenerateCorrelationIdHeaderByDefault() throws Exception {
    var result = mockMvc.perform(get("/test"))
        .andExpect(status().isOk())
        .andReturn();

    String header = result.getResponse().getHeader("X-Correlation-Id");

    assertThat(header)
        .as("Correlation ID header should be present")
        .isNotNull()
        .isNotBlank();
  }
}
