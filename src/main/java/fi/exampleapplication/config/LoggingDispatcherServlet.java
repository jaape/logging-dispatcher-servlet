package fi.exampleapplication.config;

import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Objects;

/**
 * Wraps HttpServletRequest and HttpServletResponse with ContentCachingWrapper. Wrapping enables the content body to read more than once.
 */
public class LoggingDispatcherServlet extends DispatcherServlet {

    private final List<String> routesToLog;


    /**
     * @param routesToLog List of routes to log. Example route is '/api/'. If the list is empty, nothing is logged
     */
    LoggingDispatcherServlet(@NotNull List<String> routesToLog) {
        this.routesToLog = Objects.requireNonNull(routesToLog);
    }

    @Override
    protected void doDispatch(HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (routesToLog.stream().anyMatch(request.getRequestURI()::contains)) {
            doDispatchWithLogging(request, response);
        } else {
            super.doDispatch(request, response);
        }
    }

    /**
     * Logs request and response.
     * Wraps HttpServletRequest and HttpServletResponse with ContentCachingWrapper. Wrapping enables the content body to read more than once.
     */
    private void doDispatchWithLogging(HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (!(request instanceof ContentCachingRequestWrapper)) {
            request = new ContentCachingRequestWrapper(request);
        }
        if (!(response instanceof ContentCachingResponseWrapper)) {
            response = new ContentCachingResponseWrapper(response);
        }
        HandlerExecutionChain handler = getHandler(request);

        try {
            super.doDispatch(request, response);
        } finally {
            log(request, response, handler);
            updateResponse(response);
        }
    }

    /**
     * log here
     **/
    private void log(HttpServletRequest request, HttpServletResponse response, HandlerExecutionChain handler) {

        getRequestBody(request);
        getResponseContent(response);

        response.getStatus();
        request.getMethod();
        request.getRequestURI();
        request.getParameterMap();
        request.getRemoteAddr();
        handler.toString();
    }

    private String getResponseContent(HttpServletResponse response) {
        ContentCachingResponseWrapper wrapper = WebUtils.getNativeResponse(response, ContentCachingResponseWrapper.class);
        return getByteAsString(wrapper.getContentAsByteArray(), wrapper.getCharacterEncoding());
    }

    private String getRequestBody(HttpServletRequest request) {
        ContentCachingRequestWrapper wrapper = WebUtils.getNativeRequest(request, ContentCachingRequestWrapper.class);
        return getByteAsString(wrapper.getContentAsByteArray(), wrapper.getCharacterEncoding());
    }

    private String getByteAsString(byte[] buf, String characterEncoding) {
        if (buf.length > 0) {
            int length = Math.min(buf.length, 5120);
            try {
                return new String(buf, 0, length, characterEncoding);
            } catch (UnsupportedEncodingException ex) {
                // NOOP
            }
        }
        return null;
    }

    private void updateResponse(HttpServletResponse response) throws IOException {
        ContentCachingResponseWrapper responseWrapper =
                WebUtils.getNativeResponse(response, ContentCachingResponseWrapper.class);
        responseWrapper.copyBodyToResponse();
    }

}