package com.example.demo.util;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.propagation.TextMapGetter;
import io.opentelemetry.context.propagation.TextMapSetter;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.util.List;
import static io.opentelemetry.api.common.AttributeKey.stringKey;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.metrics.LongCounter;
import io.opentelemetry.api.metrics.Meter;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;
import java.io.File;
import javax.swing.filechooser.FileSystemView;

public class MatricsUtil {
    private static final Tracer tracer =
            GlobalOpenTelemetry.getTracer("io.opentelemetry.example.metrics", "0.13.1");

    private static final Meter sampleMeter =
            GlobalOpenTelemetry.getMeter("io.opentelemetry.example.metrics");
    private static final LongCounter directoryCounter =
            sampleMeter
                    .counterBuilder("number_of_request_count")
                    .setDescription("Counts number of Requests.")
                    .setUnit("req")
                    .build();

    private static final AttributeKey<String> CONTROLLER_KEY = stringKey("API-Service");
    // statically allocate the attributes, since they are known at init time.
    private static final Attributes CONTROLLER_MAPPING_ATTRIBUTES =
            Attributes.of(CONTROLLER_KEY, "main");
}
