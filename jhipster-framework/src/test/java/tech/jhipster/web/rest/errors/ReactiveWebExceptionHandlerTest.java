package tech.jhipster.web.rest.errors;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.net.URI;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.http.server.reactive.MockServerHttpResponse;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebHandler;
import org.springframework.web.server.adapter.WebHttpHandlerBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import reactor.core.publisher.Mono;

public class ReactiveWebExceptionHandlerTest {
    
    // Constants
    private static final MediaType RESPONSE_TYPE = MediaType.APPLICATION_PROBLEM_JSON;
    private static Log logger = LogFactory.getLog(ReactiveWebExceptionHandlerTest.class);
    private static final String TITLE_REASON = "Test Reason";
    private static final HttpStatus HANDLED_RESPONSE_STATUS = HttpStatus.METHOD_NOT_ALLOWED;
    private static final HttpStatus NOT_HANDLED_RESPONSE_STATUS = HttpStatus.INTERNAL_SERVER_ERROR;
    private static final ResponseStatusException EXCEPTION_TO_HANDLE = new ResponseStatusException(HANDLED_RESPONSE_STATUS, TITLE_REASON);
    private static final IllegalStateException EXCEPTION_NOT_TO_HANDLE = new IllegalStateException("Test Exception");
    private static final String PROBLEM_TYPE = "http://test.com/testExceptionTranslation";
    private static final String expectedResponse = "{\"type\":\"http://test.com/testExceptionTranslation\",\"title\":\"Test Reason\",\"status\":405,\"detail\":null,\"instance\":null,\"properties\":null}";

    @Test
    void throwResponseStatusException() throws JsonMappingException, JsonProcessingException {
        MockServerHttpRequest request = MockServerHttpRequest.get("/").build();
		MockServerHttpResponse response = new MockServerHttpResponse();

		ReactiveWebExceptionHandler exceptionHandler = new ReactiveWebExceptionHandler(new TestExceptionTranslation(), new ObjectMapper());

		WebHttpHandlerBuilder.webHandler(new TestWebHandler(EXCEPTION_TO_HANDLE))
				.exceptionHandler(exceptionHandler).build()
				.handle(request, response)
				.block();
		
		assertEquals(HANDLED_RESPONSE_STATUS, response.getStatusCode());
        assertEquals(RESPONSE_TYPE, response.getHeaders().getContentType());
        assertEquals(expectedResponse, response.getBodyAsString().block());
    }
    
    @Test
    void throwOtherException() throws JsonMappingException, JsonProcessingException {
        MockServerHttpRequest request = MockServerHttpRequest.get("/").build();
        MockServerHttpResponse response = new MockServerHttpResponse();

        ReactiveWebExceptionHandler exceptionHandler = new ReactiveWebExceptionHandler(new TestExceptionTranslation(), new ObjectMapper());

        WebHttpHandlerBuilder.webHandler(new TestWebHandler(EXCEPTION_NOT_TO_HANDLE))
                .exceptionHandler(exceptionHandler).build()
                .handle(request, response)
                .block();
        
        assertEquals(NOT_HANDLED_RESPONSE_STATUS, response.getStatusCode());
    }
    
    private static ProblemDetail getProblemDetail() {
        ProblemDetail problem = ProblemDetail.forStatus(EXCEPTION_TO_HANDLE.getStatusCode());
        problem.setType(URI.create(PROBLEM_TYPE));
        problem.setTitle(EXCEPTION_TO_HANDLE.getReason());
        return problem;
    } 

    private static class TestExceptionTranslation implements ExceptionTranslation {

        @Override
        public Mono<ResponseEntity<Object>> handleAnyException(Throwable ex, ServerWebExchange request) {
            logger.trace("Stub ExceptionTranslation handleAnyException invoked");
            ProblemDetail problem = getProblemDetail();
            HttpHeaders header = new HttpHeaders();
            header.setContentType(RESPONSE_TYPE);
            return Mono.just(new ResponseEntity<>(problem, header, ((ResponseStatusException) ex).getStatusCode()));
        }
    }

    private static class TestWebHandler implements WebHandler {  
        private Throwable exception;
        TestWebHandler(Throwable exception) {
            this.exception = exception;
        }

        @Override
        public Mono<Void> handle(org.springframework.web.server.ServerWebExchange exchange) {
            logger.trace("StubHandler invoked.");
			return Mono.error(this.exception);
        }
	}
}
