package com.example.demo.util;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.propagation.TextMapGetter;
import io.opentelemetry.context.propagation.TextMapSetter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.net.HttpURLConnection;
import java.net.URLConnection;
import java.util.List;

@Component
public class TracingUtil {

//    @Autowired
//    OpenTelemetry openTelemetry;

    TextMapGetter<HttpHeaders> getter =
            new TextMapGetter<HttpHeaders>() {
                @Override
                public Iterable<String> keys(HttpHeaders carrier) {
                    return carrier.keySet();
                }

                @Nullable
                @Override
                public String get(HttpHeaders carrier, String key) {
                    List<String> values = carrier != null ? carrier.get(key) : null;
                    if (values == null || values.isEmpty()) {
                        return null;
                    }
                    return values.get(0);
                }
            };

//    TextMapSetter<HttpURLConnection> httpURLConnectionSetter = URLConnection::setRequestProperty;
    TextMapSetter<HttpHeaders> headersSetter = HttpHeaders::set;

    public Context extractToContext(HttpHeaders headers) {
        return GlobalOpenTelemetry.getPropagators().getTextMapPropagator().extract(Context.current(), headers, getter);
    }

    public void extract(HttpHeaders headers) {
        Context context = extractToContext(headers);
        context.makeCurrent();
    }

//    public HttpURLConnection injectHttpURLConnection(Context context, HttpURLConnection httpURLConnection) {
//        if (context == null) {
//            return httpURLConnection;
//        }
//        openTelemetry.getPropagators().getTextMapPropagator().inject(context, httpURLConnection, httpURLConnectionSetter);
//        return httpURLConnection;
//    }
//
//    public HttpURLConnection injectHttpURLConnection(HttpURLConnection httpURLConnection) {
//        Context context = Context.current();
//        return injectHttpURLConnection(context, httpURLConnection);
//    }

    public HttpHeaders injectHeaders(Context context, HttpHeaders headers) {
        if (context == null) {
            return headers;
        }
        GlobalOpenTelemetry.getPropagators().getTextMapPropagator().inject(context, headers, headersSetter);
        return headers;
    }

    public HttpHeaders injectHeaders(HttpHeaders headers) {
        Context context = Context.current();
        return injectHeaders(context, headers);
    }
}
