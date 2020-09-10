package com.jywoo.payment.usecase;

import com.jywoo.payment.domain.Transaction;
import com.jywoo.payment.except.message.ValidationExceptionMessage;
import com.jywoo.payment.service.TransactionService;
import com.jywoo.payment.usecase.intr.Request;
import com.jywoo.payment.usecase.intr.Response;
import com.jywoo.payment.usecase.intr.UseCase;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Service
@RequiredArgsConstructor
public class SearchUseCase implements UseCase<SearchUseCase.SearchRequest, SearchUseCase.SearchResponse> {
    private final TransactionService transactionService;

    @Override
    public SearchResponse apply(SearchRequest request) {
        final Transaction transaction = transactionService.findById(request.getTransactionId());
        return new SearchResponse(request, transaction);
    }

    @Getter
    @AllArgsConstructor
    public static class SearchRequest implements Request {
        @NotNull(message = ValidationExceptionMessage.REQUIRED_TRANSACTION_ID)
        @Size(min = 20, max = 20, message = ValidationExceptionMessage.INVALID_TRANSACTION_ID)
        private String transactionId;
    }

    @Getter
    @AllArgsConstructor
    public static class SearchResponse implements Response {
        private final SearchRequest searchRequest;
        private final Transaction transaction;
    }

}
