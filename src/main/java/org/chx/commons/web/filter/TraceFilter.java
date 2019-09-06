package org.chx.commons.web.filter;

import org.apache.commons.io.IOUtils;
import org.chx.commons.serialize.JsonHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpMethod;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * trace filter
 *
 * @author chenxi
 * @date 2019-08-29
 */
@WebFilter(urlPatterns = "/*", filterName = "traceFilter")
public class TraceFilter extends OncePerRequestFilter {

    private static Logger log = LoggerFactory.getLogger(TraceFilter.class);

    private final static String TRACE_KEY = "trace-id";
    private final static String SPAN_KEY = "span-id";
    private final static String TRACE_HEADER = "TraceId";
    private final static String SPAN_HEADER = "SpanId";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);

        MDC.put(TRACE_KEY, requestWrapper.getHeader(TRACE_HEADER));
        MDC.put(SPAN_KEY, requestWrapper.getHeader(SPAN_HEADER));

        long s = System.currentTimeMillis();
        try {
            filterChain.doFilter(requestWrapper, responseWrapper);
        } finally {
            String requestContent;
            if (HttpMethod.POST.matches(requestWrapper.getMethod())) {

                requestContent = IOUtils.toString(requestWrapper.getContentAsByteArray(), "utf-8");
            } else {
                requestContent = JsonHelper.toJson(requestWrapper.getParameterMap());
            }
            String responseContent = IOUtils.toString(responseWrapper.getContentAsByteArray(), "utf-8");
            log.warn("uri={}||request={}||response={}||proc_time={}", requestWrapper.getRequestURI(), requestContent, responseContent, System.currentTimeMillis() - s);

            MDC.clear();
        }

        responseWrapper.copyBodyToResponse();
    }

}
