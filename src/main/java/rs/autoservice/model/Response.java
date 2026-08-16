package rs.autoservice.model;

import java.io.Serializable;

/**
 * Generička klasa koja predstavlja uniformni odgovor servera prema klijentu.
 * Demonstrira upotrebu generičkih tipova (Generics) u programskom jeziku Java.
 * Tipski parametar {@code <T>} omogućava prenos bilo kog tipa podataka (npr. Customer, List, Integer itd.).
 *
 * @param <T> Tip podataka koji se prenosi u telu odgovora
 * @author Vukasin Miljkovic
 */
public class Response<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    /** Oznaka da li je serverska operacija uspešno izvršena */
    private boolean success;

    /** Tekstualna poruka o ishodu operacije ili opis greške */
    private String message;

    /** Generički podatak vraćen od strane servera */
    private T data;

    /**
     * Prazan podrazumevani konstruktor.
     */
    public Response() {
    }

    /**
     * Konstruktor sa svim poljima.
     *
     * @param success Da li je operacija uspešna
     * @param message Poruka sa servera
     * @param data    Generički podaci tipa T
     */
    public Response(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    /**
     * Pomoćna statička generička metoda za kreiranje uspešnog odgovora sa podacima.
     *
     * @param <E>  Generički tip podatka
     * @param data Podatak koji se vraća
     * @return Response objekat sa success = true
     */
    public static <E> Response<E> ok(E data) {
        return new Response<>(true, "Operacija je uspešno izvršena.", data);
    }

    /**
     * Pomoćna statička generička metoda za kreiranje uspešnog odgovora sa porukom i podacima.
     *
     * @param <E>     Generički tip podatka
     * @param message Korisnička poruka
     * @param data    Podatak koji se vraća
     * @return Response objekat sa success = true
     */
    public static <E> Response<E> ok(String message, E data) {
        return new Response<>(true, message, data);
    }

    /**
     * Pomoćna statička generička metoda za kreiranje neuspešnog odgovora (greške).
     *
     * @param <E>          Generički tip podatka
     * @param errorMessage Poruka o grešci
     * @return Response objekat sa success = false
     */
    public static <E> Response<E> error(String errorMessage) {
        return new Response<>(false, errorMessage, null);
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Response{success=" + success + ", message='" + message + "', data=" + data + "}";
    }
}
