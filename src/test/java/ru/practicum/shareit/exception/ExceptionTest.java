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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExceptionTest {

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
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testHandleNotFoundException() {
        String errorMessage = String.valueOf(HttpStatus.NOT_FOUND);
        when(notFoundException.getMessage()).thenReturn(errorMessage);
        ErrorResponse response = errorHandler.handleNotFoundException(notFoundException);
        assertEquals(String.valueOf(HttpStatus.NOT_FOUND), response.getError(), "Ошибка при обработке исключения NotFound");
        assertEquals(errorMessage, response.getError(), "Ошибка при сравнении сообщения ошибки NotFound");
    }

    @Test
    void testHandleIncorrectParameterException() {
        String errorMessage = String.valueOf(HttpStatus.BAD_REQUEST);
        when(incorrectParameterException.getMessage()).thenReturn(errorMessage);
        ErrorResponse response = errorHandler.handleIncorrectParameterException(incorrectParameterException);
        assertEquals(String.valueOf(HttpStatus.BAD_REQUEST), response.getError(), "Ошибка при обработке исключения IncorrectParameter");
        assertEquals(errorMessage, response.getError(), "Ошибка при сравнении сообщения ошибки IncorrectParameter");
    }

    @Test
    void testHandleAlreadyExistException() {
        String errorMessage = String.valueOf(HttpStatus.BAD_REQUEST);
        when(alreadyExistException.getMessage()).thenReturn(errorMessage);
        ErrorResponse response = errorHandler.handleAlreadyExistException(alreadyExistException);
        assertEquals(String.valueOf(HttpStatus.BAD_REQUEST), response.getError(), "Ошибка при обработке исключения AlreadyExist");
        assertEquals(errorMessage, response.getError(), "Ошибка при сравнении сообщения ошибки AlreadyExist");
    }

    @Test
    void testHandleThrowable() {
        ErrorResponse response = errorHandler.handleThrowable(new Throwable());
        assertEquals("Произошла непредвиденная ошибка.", response.getError(), "Ошибка при обработке исключения Throwable");
        assertEquals("Произошла непредвиденная ошибка.", response.getError(), "Ошибка при сравнении сообщения ошибки Throwable");
    }

    @Test
    void testErrorResponseDefaultConstructor() {
        ErrorResponse response = new ErrorResponse("Error message");
        assertEquals("Error message", response.getError(), "Ошибка при создании ErrorResponse с помощью конструктора по умолчанию");
    }

    @Test
    void testConstructor() {
        String message = "This item already exists.";
        AlreadyExistException exception = new AlreadyExistException(message);
        assertEquals(message, exception.getMessage(), "Ошибка при создании AlreadyExistException через конструктор");
    }

    @Test
    void testConstructorWithMessage() {
        String errorMessage = "Invalid parameter";
        IncorrectParameterException exception = assertThrows(IncorrectParameterException.class, () -> {
            throw new IncorrectParameterException(errorMessage);
        });
        assertEquals(errorMessage, exception.getMessage(), "Ошибка при создании IncorrectParameterException через конструктор с сообщением");
    }

    @Test
    void testConstructorWithMessageTest() {
        String message = "This is a validation error.";
        ValidationException exception = new ValidationException(message);
        assertEquals(message, exception.getMessage(), "Ошибка при создании ValidationException через конструктор с сообщением");
    }

}