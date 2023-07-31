package ru.practicum.shareit.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ExceptionTest {

    @InjectMocks
    private ErrorHandler errorHandler;

    @Mock
    private NotFoundException notFoundException;

    @Mock
    private IncorrectParameterException incorrectParameterException;

    @Mock
    private AlreadyExistException alreadyExistException;

    @Mock
    private ValidationException validationException;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void handleNotFoundException() {
        String errorMessage = String.valueOf(HttpStatus.NOT_FOUND);
        when(notFoundException.getMessage()).thenReturn(errorMessage);
        ErrorResponse response = errorHandler.handleNotFoundException(notFoundException);
        assertEquals(String.valueOf(HttpStatus.NOT_FOUND), response.getError());
        assertEquals(errorMessage, response.getError());
    }

    @Test
    public void handleIncorrectParameterException() {
        String errorMessage = String.valueOf(HttpStatus.BAD_REQUEST);
        when(incorrectParameterException.getMessage()).thenReturn(errorMessage);
        ErrorResponse response = errorHandler.handleIncorrectParameterException(incorrectParameterException);
        assertEquals(String.valueOf(HttpStatus.BAD_REQUEST), response.getError());
        assertEquals(errorMessage, response.getError());
    }

    @Test
    public void handleAlreadyExistException() {
        String errorMessage = String.valueOf(HttpStatus.BAD_REQUEST);
        when(alreadyExistException.getMessage()).thenReturn(errorMessage);
        ErrorResponse response = errorHandler.handleAlreadyExistException(alreadyExistException);
        assertEquals(String.valueOf(HttpStatus.BAD_REQUEST), response.getError());
        assertEquals(errorMessage, response.getError());
    }

    @Test
    public void handleThrowable() {
        ErrorResponse response = errorHandler.handleThrowable(new Throwable());
        assertEquals(String.valueOf("Произошла непредвиденная ошибка."), response.getError());
        assertEquals("Произошла непредвиденная ошибка.", response.getError());
    }

    @Test
    public void errorResponseDefaultConstructor() {
        ErrorResponse response = new ErrorResponse("Error message");
        assertEquals("Error message", response.getError());
    }

    @Test
    public void constructorTest() {
        String message = "This item already exists.";
        AlreadyExistException exception = new AlreadyExistException(message);
        assertEquals(message, exception.getMessage());
    }

}