package com.iancaffey.http;

import com.iancaffey.http.util.ResponseCode;

import java.io.*;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;

/**
 * Response
 * <p>
 * A compiled form of an HTTP response providing structure to the message, clearly indicating the response code, headers, and body.
 * <p>
 * Responses are mutable and reusable. When written out as a HTTP response, the data is read from the class and written to
 * the response {@code OutputStream}.
 * <p>
 * Static responses can be handled by both {@code Controller} and {@code Router}. However, parameterized responses can only
 * be generated by a {@code Controller}.
 *
 * @author Ian Caffey
 * @since 1.0
 */
public class Response extends Message {
    private final ResponseCode code;

    /**
     * Constructs a new {@code Response} with a specified response code, headers, and no body.
     *
     * @param version the HTTP version
     * @param code    the response code
     * @param headers the response headers
     */
    public Response(String version, ResponseCode code, Map<String, String> headers) {
        this(version, code, headers, null, 0);
    }

    /**
     * Constructs a new {@code Response} with a specified response code, headers, and body.
     * <p>
     * Unknown body length is denoted by a length of -1.
     *
     * @param version the HTTP version
     * @param code    the response code
     * @param headers the response headers
     * @param body    the response body
     * @param length  the response body length
     */
    public Response(String version, ResponseCode code, Map<String, String> headers, ReadableByteChannel body, long length) {
        super(version, headers, body, length);
        this.code = code;
    }

    /**
     * Constructs a new {@code Response} with a {@code ResponseCode.OK} response code and empty headers.
     *
     * @return a new {@code Response}
     */
    public static Response ok() {
        return Response.of(ResponseCode.OK);
    }

    /**
     * Constructs a new {@code Response} with a {@code ResponseCode.OK} response code and a body represented by a {@code byte[]}.
     *
     * @param content the response body
     * @return a new {@code Response}
     */
    public static Response ok(byte[] content) {
        return Response.of(ResponseCode.OK, content);
    }

    /**
     * Constructs a new {@code Response} with a {@code ResponseCode.OK} response code and a body represented by a {@code String}.
     *
     * @param content the response body
     * @return a new {@code Response}
     */
    public static Response ok(String content) {
        return Response.of(ResponseCode.OK, content);
    }

    /**
     * Constructs a new {@code Response} with a {@code ResponseCode.OK} response code and a body represented by a {@code String} with a given charset.
     *
     * @param content the response body
     * @return a new {@code Response}
     */
    public static Response ok(String content, String charset) throws UnsupportedEncodingException {
        return Response.of(ResponseCode.OK, content, charset);
    }

    /**
     * Constructs a new {@code Response} with a {@code ResponseCode.OK} response code and a body represented by a {@code String} with a given charset.
     *
     * @param content the response body
     * @return a new {@code Response}
     */
    public static Response ok(String content, Charset charset) {
        return Response.of(ResponseCode.OK, content, charset);
    }

    /**
     * Constructs a new {@code Response} with a {@code ResponseCode.OK} response code and a body represented by a {@code File}.
     *
     * @param file the response body
     * @return a new {@code Response}
     */
    public static Response ok(File file) throws IOException {
        return Response.of(ResponseCode.OK, file);
    }

    /**
     * Constructs a new {@code Response} with a {@code ResponseCode.OK} response code and a body represented by an {@code InputStream}.
     *
     * @param content the response body
     * @param length  the response body length
     * @return a new {@code Response}
     */
    public static Response ok(InputStream content, long length) {
        return Response.of(ResponseCode.OK, content, length);
    }

    /**
     * Constructs a new {@code Response} with a specified response code and empty headers.
     *
     * @param code the response code
     * @return a new {@code Response}
     */
    public static Response of(ResponseCode code) {
        return new Response(Message.HTTP_VERSION, code, new HashMap<>());
    }

    /**
     * Constructs a new {@code Response} with a specified response code and a body represented by a {@code byte[]}.
     *
     * @param code    the response code
     * @param content the response body
     * @return a new {@code Response}
     */
    public static Response of(ResponseCode code, byte[] content) {
        return content == null ? Response.of(code) :
                new Response(Message.HTTP_VERSION, code, new HashMap<>(),
                        Channels.newChannel(new ByteArrayInputStream(content)), content.length);
    }

    /**
     * Constructs a new {@code Response} with a specified response code and a body represented by an {@code String}.
     *
     * @param code    the response code
     * @param content the response body
     * @return a new {@code Response}
     */
    public static Response of(ResponseCode code, String content) {
        return content == null ? Response.of(code) : Response.of(code, content.getBytes());
    }

    /**
     * Constructs a new {@code Response} with a specified response code and a body represented by a {@code String} with a given charset.
     *
     * @param code    the response code
     * @param content the response body
     * @return a new {@code Response}
     */
    public static Response of(ResponseCode code, String content, String charset) throws UnsupportedEncodingException {
        return content == null ? Response.of(code) : Response.of(code, content.getBytes(charset));
    }

    /**
     * Constructs a new {@code Response} with a specified response code and a body represented by a {@code String} with a given charset.
     *
     * @param code    the response code
     * @param content the response body
     * @return a new {@code Response}
     */
    public static Response of(ResponseCode code, String content, Charset charset) {
        return content == null ? Response.of(code) : Response.of(code, content.getBytes(charset));
    }

    /**
     * Constructs a new {@code Response} with a specified response code, headers,
     * and a body represented by a {@code File}.
     *
     * @param code the response code
     * @param file the response body
     * @return a new {@code Response}
     */
    public static Response of(ResponseCode code, File file) throws IOException {
        return file == null ? Response.of(code) :
                Response.of(code, Files.newByteChannel(file.toPath(), StandardOpenOption.READ), file.length());
    }

    /**
     * Constructs a new {@code Response} with a specified response code and a body represented by an {@code InputStream}.
     *
     * @param code    the response code
     * @param content the response body
     * @param length  the response body length
     * @return a new {@code Response}
     */
    public static Response of(ResponseCode code, InputStream content, long length) {
        return Response.of(code, Channels.newChannel(content), length);
    }

    /**
     * Constructs a new {@code Response} with a specified response code and a body represented by an {@code ReadableByteChannel}.
     *
     * @param code   the response code
     * @param body   the response body
     * @param length the response body length
     * @return a new {@code Response}
     */
    public static Response of(ResponseCode code, ReadableByteChannel body, long length) {
        return new Response(Message.HTTP_VERSION, code, new HashMap<>(), body, length);
    }

    /**
     * Returns the response code.
     *
     * @return the response code
     */
    public ResponseCode code() {
        return code;
    }
}
