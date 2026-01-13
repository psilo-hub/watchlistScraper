package free.svoss.tools.watchlistScraper.controller;

import free.svoss.tools.watchlistScraper.dto.ApiResponse;
import free.svoss.tools.watchlistScraper.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import java.time.LocalDateTime;

@RestControllerAdvice
@Slf4j
public class BaseController {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex, WebRequest request, HttpServletRequest httpRequest) {
        log.error("Error processing request: {}", httpRequest.getRequestURI(), ex);

        ErrorResponse errorResponse = new ErrorResponse(
                "Internal Server Error",
                ex.getMessage(),
                httpRequest.getRequestURI(),
                System.currentTimeMillis()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    protected <T> ResponseEntity<ApiResponse<T>> createSuccessResponse(T data) {
        ApiResponse<T> response = new ApiResponse<>(true, "Success", data);
        return ResponseEntity.ok(response);
    }

    protected <T> ResponseEntity<ApiResponse<T>> createErrorResponse(String message, HttpStatus status) {
        ApiResponse<T> response = new ApiResponse<>(false, message, null);
        return new ResponseEntity<>(response, status);
    }
}