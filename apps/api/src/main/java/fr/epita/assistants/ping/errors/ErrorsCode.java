package fr.epita.assistants.ping.errors;

import fr.epita.assistants.ping.utils.HttpError;
import fr.epita.assistants.ping.utils.IHttpError;
import jakarta.ws.rs.core.Response.Status;
import lombok.Getter;

import static jakarta.ws.rs.core.Response.Status.*;


@Getter
public enum ErrorsCode implements IHttpError {
    EXAMPLE_ERROR(BAD_REQUEST, "Example error: %s"),
    LESSON_NOT_FOUND(NOT_FOUND, "Lesson not found"),
    EXERCISE_NOT_FOUND(NOT_FOUND, "Exercise not found"),
    EXERCISE_LOCKED(FORBIDDEN, "Exercise is locked"),
    EXECUTION_FAILED(INTERNAL_SERVER_ERROR, "Code execution failed"),
    EXECUTION_TIMEOUT(REQUEST_TIMEOUT, "Code execution timed out"),
    INVALID_LANGUAGE(BAD_REQUEST, "Invalid language"),
    QUEST_NOT_FOUND(NOT_FOUND, "Quest not found"),
    ACHIEVEMENT_NOT_FOUND(NOT_FOUND, "Achievement not found"),
    ;

    private final HttpError error;

    ErrorsCode(Status status, String message) {
        error = new HttpError(status, message);
    }

    @Override
    public RuntimeException get(Object... args) {
        return error.get(args);
    }

    @Override
    public void throwException(Object... args) {
        throw error.get(args);
    }
}
