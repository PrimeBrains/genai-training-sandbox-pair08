package com.example.training;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ExpenseController.class)
@Import(ExpenseService.class)
class ExpenseControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    @DisplayName("POST /api/reimburse: 交通費の支給額を返す")
    void reimburseTransport() throws Exception {
        mockMvc.perform(post("/api/reimburse")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"category": "TRANSPORT", "amount": 5000}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reimbursed").value(3000));
    }

    @Test
    @DisplayName("POST /api/total: 複数明細の合計を返す")
    void total() throws Exception {
        mockMvc.perform(post("/api/total")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                [
                                  {"category": "TRANSPORT", "amount": 5000},
                                  {"category": "MEAL", "amount": 1000},
                                  {"category": "OTHER", "amount": 2000}
                                ]
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(5500));
    }

    @Test
    @DisplayName("POST /api/reimburse: 負の金額は400を返す")
    void reimburseNegativeAmount() throws Exception {
        mockMvc.perform(post("/api/reimburse")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"category": "OTHER", "amount": -1}
                                """))
                .andExpect(status().isBadRequest());
    }
}
