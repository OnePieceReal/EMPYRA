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
import org.apache.catalina.connector.Response;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//@WebMvcTest(controllers= EmployeeController.class)
//@AutoConfigureMockMvc(addFilters = false)
@RunWith(SpringRunner.class)
@ContextConfiguration(classes=HobbyController.class)
@WebMvcTest(EmployeeController.class)
@ExtendWith(MockitoExtension.class)

class HobbyControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private HobbyService hobbyService;
    @Autowired
    private ObjectMapper objectMapper;
    private HobbyModel hobbyModel;

//    @BeforeEach
//    public void init(){
//        //Employee x = new Employee(1,"mullooor","idrc","hello@gmail.com","missisippi",1147484364,true,false,null);
//
//        hobbyModel = HobbyModel.builder().name("randomHobby").description("some random thing").employeeModelWithOutList(null)
//                .build();
//
//    }

//    @Test
//    public void getHobbyByIdTest() throws Exception {
//        int hobbyId = 1;
//        when(hobbyService.getEmployeesWithRespectToHobby(hobbyId)).thenReturn(hobbyModel);
//
//        ResultActions response = mockMvc.perform(get("/api/v1/hobbies/1")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(hobbyModel)));
//
//        response.andExpect(MockMvcResultMatchers.status().isOk())
//                .andExpect(jsonPath("$.name", CoreMatchers.is(hobbyModel.getName())))
//                .andExpect(jsonPath("$.description", CoreMatchers.is(hobbyModel.getDescription())));
//
//    }

}
