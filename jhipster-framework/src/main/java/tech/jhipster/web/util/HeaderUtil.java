package tech.jhipster.web.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;

/**
 * Utility class for HTTP headers creation.
 */
public final class HeaderUtil {

    private static final Logger log = LoggerFactory.getLogger(HeaderUtil.class);

    private HeaderUtil() {}

    /**
     * <p>createAlert.</p>
     *
     * @param applicationName a {@link java.lang.String} object.
     * @param alertMessage  a {@link java.lang.String} object.
     * @param alertParam   a {@link java.lang.String} object.
     * @return a {@link org.springframework.http.HttpHeaders} object.
     */
    public static HttpHeaders createAlertHeader(String applicationName, String alertMessage, String alertParam) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-" + applicationName + "-alert", alertMessage);
        try {
            headers.add("X-" + applicationName + "-params", URLEncoder.encode(alertParam, StandardCharsets.UTF_8.toString()));
        } catch (UnsupportedEncodingException e) {
            // StandardCharsets are supported by every Java implementation so this exception will never happen
        }
        return headers;
    }

    /**
     * <p>createEntityCreationAlert.</p>
     *
     * @param applicationName a {@link java.lang.String} object.
     * @param enableTranslation a boolean.
     * @param entityName a {@link java.lang.String} object.
     * @param entityIdentifier  a {@link java.lang.String} object.
     * @return a {@link org.springframework.http.HttpHeaders} object.
     */
    public static HttpHeaders createEntityCreationAlert(
        String applicationName,
        boolean enableTranslation,
        String entityName,
        String entityIdentifier
    ) {
        String alertMessage = enableTranslation
            ? applicationName + "." + entityName + ".created"
            : "A new " + entityName + " is created with identifier " + entityIdentifier;
        return createAlertHeader(applicationName, alertMessage, entityIdentifier);
    }

    /**
     * <p>createEntityUpdateAlert.</p>
     *
     * @param applicationName a {@link java.lang.String} object.
     * @param enableTranslation a boolean.
     * @param entityName a {@link java.lang.String} object.
     * @param entityIdentifier  a {@link java.lang.String} object.
     * @return a {@link org.springframework.http.HttpHeaders} object.
     */
    public static HttpHeaders createEntityUpdateAlert(
        String applicationName,
        boolean enableTranslation,
        String entityName,
        String entityIdentifier
    ) {
        String alertMessage = enableTranslation
            ? applicationName + "." + entityName + ".updated"
            : "A " + entityName + " is updated with identifier " + entityIdentifier;
        return createAlertHeader(applicationName, alertMessage, entityIdentifier);
    }

    /**
     * <p>createEntityDeletionAlert.</p>
     *
     * @param applicationName a {@link java.lang.String} object.
     * @param enableTranslation a boolean.
     * @param entityName a {@link java.lang.String} object.
     * @param entityIdentifier  a {@link java.lang.String} object.
     * @return a {@link org.springframework.http.HttpHeaders} object.
     */
    public static HttpHeaders createEntityDeletionAlert(
        String applicationName,
        boolean enableTranslation,
        String entityName,
        String entityIdentifier
    ) {
        String alertMessage = enableTranslation
            ? applicationName + "." + entityName + ".deleted"
            : "A " + entityName + " is deleted with identifier " + entityIdentifier;
        return createAlertHeader(applicationName, alertMessage, entityIdentifier);
    }

    /**
     * <p>createFailureAlert.</p>
     *
     * @param applicationName a {@link java.lang.String} object.
     * @param enableTranslation a boolean.
     * @param entityName a {@link java.lang.String} object.
     * @param errorKey a {@link java.lang.String} object.
     * @param defaultMessage a {@link java.lang.String} object.
     * @return a {@link org.springframework.http.HttpHeaders} object.
     */
    public static HttpHeaders createFailureAlert(
        String applicationName,
        boolean enableTranslation,
        String entityName,
        String errorKey,
        String defaultMessage
    ) {
        log.error("Entity processing failed, {}", defaultMessage);

        String alertMessage = enableTranslation ? "error." + errorKey : defaultMessage;

        HttpHeaders headers = new HttpHeaders();
        headers.add("X-" + applicationName + "-error", alertMessage);
        headers.add("X-" + applicationName + "-params", entityName);
        return headers;
    }
}
