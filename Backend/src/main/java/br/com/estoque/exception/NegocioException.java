package br.com.estoque.exception;


public class NegocioException extends RuntimeException {
    public NegocioException(String message) {
        super(message);
    }
}

