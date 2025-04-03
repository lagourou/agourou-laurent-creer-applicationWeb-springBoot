package com.safetynet.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/**
 * Gère globalement les exceptions dans l'application.
 */
@ControllerAdvice
public class GlobalExceptions {

    /**
     * Gère les arguments invalides et renvoie une réponse BAD_REQUEST.
     *
     * @param ex l'exception levée
     * @return réponse avec un message d'erreur
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Erreur : " + ex.getMessage());
    }

    /**
     * Gère type de données incorrect et renvoie une réponse BAD_REQUEST.
     *
     * @param ex l'exception levée
     * @return réponse avec un message d'erreur
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<String> handleTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Type invalide : " + ex.getMessage());
    }

    /**
     * Gère les erreurs internes et renvoie une réponse INTERNAL_SERVER_ERROR.
     *
     * @param ex l'exception levée
     * @return réponse avec un message d'erreur
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Une erreur interne est survenue : " + ex.getMessage());
    }
}
