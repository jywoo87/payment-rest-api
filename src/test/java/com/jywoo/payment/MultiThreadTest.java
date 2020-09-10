package com.jywoo.payment;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jywoo.payment.api.exception.dto.ErrorDto;
import com.jywoo.payment.api.model.CancelResponseViewModel;
import com.jywoo.payment.api.model.PaymentResponseViewModel;
import com.jywoo.payment.usecase.CancelUseCase;
import com.jywoo.payment.usecase.PaymentUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class MultiThreadTest {
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    @Test
    @DisplayName("같은 카드로 동시 결제 테스트")
    void testPayment() {
        final PaymentUseCase.PaymentRequest paymentRequest = new PaymentUseCase.PaymentRequest("1234123412341234", "0623", "123", 0, 11000L, 1000L);

        final List<Object> objects = new ArrayList<>();
        Runnable worker = () -> {
            try {
                Object paymentResponse = request("/v1/payment", PaymentResponseViewModel.class, paymentRequest);
                objects.add(paymentResponse);
            } catch (Exception e) {
            }
        };

        CompletableFuture
                .allOf(CompletableFuture.runAsync(worker),
                        CompletableFuture.runAsync(worker),
                        CompletableFuture.runAsync(worker),
                        CompletableFuture.runAsync(worker))
                .join();

        Assertions.assertEquals(1, objects.stream().filter(t -> t instanceof PaymentResponseViewModel).count());
        Assertions.assertEquals(3, objects.stream().filter(t -> t instanceof ErrorDto).count());
    }

    @Test
    @DisplayName("같은 관리번호 동시 취소 테스트")
    void testCancel() throws Throwable {
        // 생성
        final PaymentUseCase.PaymentRequest paymentRequest = new PaymentUseCase.PaymentRequest("1234123412341234", "0623", "123", 0, 11000L, 1000L);
        PaymentResponseViewModel paymentResponse = (PaymentResponseViewModel) request("/v1/payment", PaymentResponseViewModel.class, paymentRequest);


        // 취소
        final CancelUseCase.CancelRequest cancelRequest = new CancelUseCase.CancelRequest(paymentResponse.getTransactionId(), 1100L, 100L);
        final List<Object> objects = new ArrayList<>();

        Runnable worker = () -> {
            try {
                Object cancelResponse = request("/v1/cancel", CancelResponseViewModel.class, cancelRequest);
                objects.add(cancelResponse);
            } catch (Exception e) {
            }
        };

        CompletableFuture
                .allOf(CompletableFuture.runAsync(worker),
                        CompletableFuture.runAsync(worker),
                        CompletableFuture.runAsync(worker),
                        CompletableFuture.runAsync(worker))
                .join();

        Assertions.assertEquals(1, objects.stream().filter(t -> t instanceof CancelResponseViewModel).count());
        Assertions.assertEquals(3, objects.stream().filter(t -> t instanceof ErrorDto).count());
    }

    public <T extends Object, R extends Object> Object request(String requestPath, Class<R> cls, T request) throws Exception {
        MvcResult result = mockMvc.perform(
                                    post(requestPath)
                                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                                            .content(objectMapper.writeValueAsString(request)))
                                    .andReturn();

        final HttpStatus status = HttpStatus.valueOf(result.getResponse().getStatus());
        if(status == HttpStatus.OK) {
            return objectMapper.readValue(result.getResponse().getContentAsString(), cls);
        } else {
            return objectMapper.readValue(result.getResponse().getContentAsString(), ErrorDto.class);
        }
    }
}
