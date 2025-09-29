package net.study.springboot.controller;

import com.fasterxml.jackson.databind.ObjectMapper;

import net.study.springboot.controller.EmployeeController;
import net.study.springboot.domain.Employee;
import net.study.springboot.domain.Hobby;
import net.study.springboot.helper.EmployeeModelConverter;
import net.study.springboot.model.EmployeeModel;
import net.study.springboot.model.HobbyModel;
import net.study.springboot.service.EmployeeService;
import net.study.springboot.service.HobbyService;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import java.util.zip.DataFormatException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(HobbyController.class)
@ExtendWith(MockitoExtension.class)
class HobbyControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private HobbyService hobbyService;
    @Autowired
    private ObjectMapper objectMapper;
    private HobbyModel hobbyModel;

    @BeforeEach
    public void init() {
        hobbyModel = HobbyModel.builder()
                .Id(1)
                .name("Reading")
                .description("Enjoying books and literature")
                .build();
    }

    @Test
    public void createHobbyTest() throws Exception {
        given(hobbyService.addHobbyService(ArgumentMatchers.any())).willAnswer(invocation -> invocation.getArgument(0));

        ResultActions response = mockMvc.perform(post("/api/v1/hobbies")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(hobbyModel)));

        response.andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", CoreMatchers.is(hobbyModel.getName())))
                .andExpect(jsonPath("$.description", CoreMatchers.is(hobbyModel.getDescription())));
    }

    @Test
    public void updateHobbyTest() throws Exception {
        int hobbyId = 1;
        when(hobbyService.hobbyUpdateService(hobbyModel, hobbyId)).thenReturn(hobbyModel);

        ResultActions response = mockMvc.perform(put("/api/v1/hobbies/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(hobbyModel)));

        response.andExpect(status().isAccepted())
                .andExpect(jsonPath("$.name", CoreMatchers.is(hobbyModel.getName())))
                .andExpect(jsonPath("$.description", CoreMatchers.is(hobbyModel.getDescription())));
    }

    @Test
    public void getAllHobbiesTest() throws Exception {
        when(hobbyService.getAllHobbyService()).thenReturn(List.of(hobbyModel));

        mockMvc.perform(get("/api/v1/hobbies/allHobbies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.hasSize(1)))
                .andExpect(jsonPath("$[0].name", Matchers.is("Reading")));
    }

    @Test
    public void deleteHobbyTest() throws Exception {
        int hobbyId = 1;
        doNothing().when(hobbyService).deleteHobbyService(hobbyId);

        ResultActions response = mockMvc.perform(delete("/api/v1/hobbies/1")
                .contentType(MediaType.APPLICATION_JSON));

        response.andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    public void createHobbyWithInvalidDataTest() throws Exception {
        HobbyModel invalidHobby = HobbyModel.builder()
                .name("")
                .description("")
                .build();

        ResultActions response = mockMvc.perform(post("/api/v1/hobbies")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidHobby)));

        response.andExpect(status().isBadRequest());
    }

    @Test
    public void updateHobbyNotFoundTest() throws Exception {
        int hobbyId = 999;
        when(hobbyService.hobbyUpdateService(hobbyModel, hobbyId))
                .thenThrow(new RuntimeException("Hobby not found"));

        ResultActions response = mockMvc.perform(put("/api/v1/hobbies/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(hobbyModel)));

        response.andExpect(status().isInternalServerError());
    }

    @Test
    public void deleteHobbyNotFoundTest() throws Exception {
        int hobbyId = 999;
        doThrow(new RuntimeException("Hobby not found"))
                .when(hobbyService).deleteHobbyService(hobbyId);

        ResultActions response = mockMvc.perform(delete("/api/v1/hobbies/999")
                .contentType(MediaType.APPLICATION_JSON));

        response.andExpect(status().isInternalServerError());
    }

    @Test
    public void getAllHobbiesWithEmptyListTest() throws Exception {
        List<HobbyModel> emptyList = List.of();
        when(hobbyService.getAllHobbyService()).thenReturn(emptyList);

        mockMvc.perform(get("/api/v1/hobbies/allHobbies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.hasSize(0)));
    }

    @Test
    public void getAllHobbiesWithMultipleHobbiesTest() throws Exception {
        HobbyModel hobby2 = HobbyModel.builder()
                .Id(2)
                .name("Swimming")
                .description("Water sports activity")
                .build();

        when(hobbyService.getAllHobbyService()).thenReturn(List.of(hobbyModel, hobby2));

        mockMvc.perform(get("/api/v1/hobbies/allHobbies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.hasSize(2)))
                .andExpect(jsonPath("$[0].name", Matchers.is("Reading")))
                .andExpect(jsonPath("$[1].name", Matchers.is("Swimming")));
    }

    @Test
    public void createHobbyWithLongDescriptionTest() throws Exception {
        HobbyModel longDescriptionHobby = HobbyModel.builder()
                .Id(3)
                .name("Photography")
                .description("Taking beautiful pictures of nature, people, and landscapes with various camera equipment and techniques")
                .build();

        given(hobbyService.addHobbyService(ArgumentMatchers.any())).willAnswer(invocation -> invocation.getArgument(0));

        ResultActions response = mockMvc.perform(post("/api/v1/hobbies")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(longDescriptionHobby)));

        response.andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", CoreMatchers.is("Photography")));
    }

    @Test
    public void updateHobbyWithDifferentDataTest() throws Exception {
        HobbyModel updatedHobby = HobbyModel.builder()
                .Id(1)
                .name("Advanced Reading")
                .description("Reading complex literature and academic texts")
                .build();

        when(hobbyService.hobbyUpdateService(updatedHobby, 1)).thenReturn(updatedHobby);

        ResultActions response = mockMvc.perform(put("/api/v1/hobbies/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedHobby)));

        response.andExpect(status().isAccepted())
                .andExpect(jsonPath("$.name", CoreMatchers.is("Advanced Reading")))
                .andExpect(jsonPath("$.description", CoreMatchers.is("Reading complex literature and academic texts")));
    }

    @Test
    public void createHobbyWithSpecialCharactersTest() throws Exception {
        HobbyModel specialCharHobby = HobbyModel.builder()
                .Id(4)
                .name("Rock & Roll")
                .description("Playing rock music with electric guitars & drums!")
                .build();

        given(hobbyService.addHobbyService(ArgumentMatchers.any())).willAnswer(invocation -> invocation.getArgument(0));

        ResultActions response = mockMvc.perform(post("/api/v1/hobbies")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(specialCharHobby)));

        response.andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", CoreMatchers.is("Rock & Roll")));
    }

    @Test
    public void createHobbyDataFormatExceptionTest() throws Exception {
        given(hobbyService.addHobbyService(ArgumentMatchers.any()))
                .willThrow(new DataFormatException("Invalid hobby data format"));

        ResultActions response = mockMvc.perform(post("/api/v1/hobbies")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(hobbyModel)));

        response.andExpect(status().isInternalServerError());
    }

    @Test
    public void updateHobbyDataFormatExceptionTest() throws Exception {
        int hobbyId = 1;
        when(hobbyService.hobbyUpdateService(hobbyModel, hobbyId))
                .willThrow(new DataFormatException("Invalid update data"));

        ResultActions response = mockMvc.perform(put("/api/v1/hobbies/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(hobbyModel)));

        response.andExpect(status().isInternalServerError());
    }

    @Test
    public void createHobbyWithNullValuesTest() throws Exception {
        HobbyModel nullHobby = HobbyModel.builder()
                .name(null)
                .description(null)
                .build();

        given(hobbyService.addHobbyService(ArgumentMatchers.any())).willAnswer(invocation -> invocation.getArgument(0));

        ResultActions response = mockMvc.perform(post("/api/v1/hobbies")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nullHobby)));

        response.andExpect(status().isCreated());
    }

    @Test
    public void updateHobbyWithZeroIdTest() throws Exception {
        int hobbyId = 0;
        when(hobbyService.hobbyUpdateService(hobbyModel, hobbyId)).thenReturn(hobbyModel);

        ResultActions response = mockMvc.perform(put("/api/v1/hobbies/0")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(hobbyModel)));

        response.andExpect(status().isAccepted());
    }

    @Test
    public void updateHobbyWithNegativeIdTest() throws Exception {
        int hobbyId = -1;
        when(hobbyService.hobbyUpdateService(hobbyModel, hobbyId)).thenReturn(hobbyModel);

        ResultActions response = mockMvc.perform(put("/api/v1/hobbies/-1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(hobbyModel)));

        response.andExpect(status().isAccepted());
    }

    @Test
    public void deleteHobbyWithZeroIdTest() throws Exception {
        int hobbyId = 0;
        doNothing().when(hobbyService).deleteHobbyService(hobbyId);

        ResultActions response = mockMvc.perform(delete("/api/v1/hobbies/0")
                .contentType(MediaType.APPLICATION_JSON));

        response.andExpect(status().isNoContent());
    }

    @Test
    public void deleteHobbyWithNegativeIdTest() throws Exception {
        int hobbyId = -1;
        doNothing().when(hobbyService).deleteHobbyService(hobbyId);

        ResultActions response = mockMvc.perform(delete("/api/v1/hobbies/-1")
                .contentType(MediaType.APPLICATION_JSON));

        response.andExpect(status().isNoContent());
    }

    @Test
    public void createHobbyWithVeryLongNameTest() throws Exception {
        String longName = "A".repeat(500);
        HobbyModel longNameHobby = HobbyModel.builder()
                .Id(5)
                .name(longName)
                .description("Short description")
                .build();

        given(hobbyService.addHobbyService(ArgumentMatchers.any())).willAnswer(invocation -> invocation.getArgument(0));

        ResultActions response = mockMvc.perform(post("/api/v1/hobbies")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(longNameHobby)));

        response.andExpect(status().isCreated());
    }

    @Test
    public void createHobbyWithMinimalDataTest() throws Exception {
        HobbyModel minimalHobby = HobbyModel.builder()
                .Id(6)
                .name("A")
                .description("B")
                .build();

        given(hobbyService.addHobbyService(ArgumentMatchers.any())).willAnswer(invocation -> invocation.getArgument(0));

        ResultActions response = mockMvc.perform(post("/api/v1/hobbies")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(minimalHobby)));

        response.andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", CoreMatchers.is("A")))
                .andExpect(jsonPath("$.description", CoreMatchers.is("B")));
    }

    @Test
    public void updateHobbyWithCompletelyDifferentDataTest() throws Exception {
        HobbyModel originalHobby = HobbyModel.builder()
                .Id(1)
                .name("Reading")
                .description("Enjoying books")
                .build();

        HobbyModel updatedHobby = HobbyModel.builder()
                .Id(1)
                .name("Gaming")
                .description("Playing video games")
                .build();

        when(hobbyService.hobbyUpdateService(updatedHobby, 1)).thenReturn(updatedHobby);

        ResultActions response = mockMvc.perform(put("/api/v1/hobbies/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedHobby)));

        response.andExpect(status().isAccepted())
                .andExpect(jsonPath("$.name", CoreMatchers.is("Gaming")))
                .andExpect(jsonPath("$.description", CoreMatchers.is("Playing video games")));
    }

    @Test
    public void createHobbyWithNumericCharactersTest() throws Exception {
        HobbyModel numericHobby = HobbyModel.builder()
                .Id(7)
                .name("Formula 1 Racing")
                .description("Following Formula 1 races and statistics from 1950 to present")
                .build();

        given(hobbyService.addHobbyService(ArgumentMatchers.any())).willAnswer(invocation -> invocation.getArgument(0));

        ResultActions response = mockMvc.perform(post("/api/v1/hobbies")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(numericHobby)));

        response.andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", CoreMatchers.is("Formula 1 Racing")));
    }

    @Test
    public void getAllHobbiesServiceExceptionTest() throws Exception {
        when(hobbyService.getAllHobbyService()).thenThrow(new RuntimeException("Database connection failed"));

        mockMvc.perform(get("/api/v1/hobbies/allHobbies"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void createHobbyWithExcessivelyLongDescriptionTest() throws Exception {
        String excessivelyLongDescription = "A".repeat(10000);
        HobbyModel longDescHobby = HobbyModel.builder()
                .name("Reading")
                .description(excessivelyLongDescription)
                .build();

        ResultActions response = mockMvc.perform(post("/api/v1/hobbies")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(longDescHobby)));

        response.andExpect(status().isBadRequest());
    }

    @Test
    public void createHobbyWithSqlInjectionAttemptTest() throws Exception {
        HobbyModel maliciousHobby = HobbyModel.builder()
                .name("'; DROP TABLE hobbies; --")
                .description("Malicious description")
                .build();

        ResultActions response = mockMvc.perform(post("/api/v1/hobbies")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(maliciousHobby)));

        response.andExpect(status().isBadRequest());
    }

    @Test
    public void updateHobbyWithInvalidIdBoundaryTest() throws Exception {
        int hobbyId = Integer.MAX_VALUE;
        when(hobbyService.hobbyUpdateService(hobbyModel, hobbyId))
                .thenThrow(new RuntimeException("Invalid ID range"));

        ResultActions response = mockMvc.perform(put("/api/v1/hobbies/" + hobbyId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(hobbyModel)));

        response.andExpect(status().isInternalServerError());
    }
}
