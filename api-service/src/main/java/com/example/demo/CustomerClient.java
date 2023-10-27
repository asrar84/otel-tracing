package com.example.demo;

import com.example.demo.util.TracingUtil;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.metrics.LongCounter;
import io.opentelemetry.api.metrics.Meter;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import io.opentelemetry.semconv.trace.attributes.SemanticAttributes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.client.RestTemplate;

import static io.opentelemetry.api.common.AttributeKey.stringKey;

@Component
public class CustomerClient {

	@Autowired
	TracingUtil tracingUtil;

	//=================================================================================================

//	private static final Tracer tracer =
//			GlobalOpenTelemetry.getTracer("io.opentelemetry.example.metrics", "0.13.1");
//
//	private static final Meter sampleMeter =
//			GlobalOpenTelemetry.getMeter("io.opentelemetry.example.metrics");
//	private static final LongCounter reqCounter =
//			sampleMeter
//					.counterBuilder("number_of_request_count")
//					.setDescription("Counts number of Requests.")
//					.setUnit("req")
//					.build();

	private static final AttributeKey<String> REMOTE_REQ_KEY = stringKey("CustomerClient");
	// statically allocate the attributes, since they are known at init time.
	private static final Attributes CONTROLLER_MAPPING_ATTRIBUTES =
			Attributes.of(REMOTE_REQ_KEY, "getCustomerRemote");
	//=================================================================================================


	private static final Logger logger = LoggerFactory.getLogger(CustomerClient.class);
	private RestTemplate restTemplate;
	private String baseUrl;

	public CustomerClient(
			RestTemplate restTemplate,
			@Value("${customerClient.baseUrl}") String baseUrl) {
		this.restTemplate = restTemplate;
		this.baseUrl = baseUrl;
	}

//	@WithSpan(value = "getCustomerRemote",kind = SpanKind.CLIENT)
//	Customer getCustomer(@PathVariable("id") long id){
//		String url = String.format("%s/customers/%d", baseUrl, id);
//		return restTemplate.getForObject(url, Customer.class);
//	}


	@WithSpan(value = "getCustomerRemote",kind = SpanKind.CLIENT)
	Customer getCustomer(@PathVariable("id") long id){

		logger.info("Request Received");

//		reqCounter.add(1, CONTROLLER_MAPPING_ATTRIBUTES);

		String urlStr = String.format("%s/customers/%d", baseUrl, id);

		ResponseEntity<Customer> customer = null;

		Span cakeSpan = Span.current();

		cakeSpan.setAttribute(SemanticAttributes.HTTP_METHOD, "GET");
		cakeSpan.setAttribute(SemanticAttributes.HTTP_FLAVOR, SemanticAttributes.HttpFlavorValues.HTTP_1_1);
		cakeSpan.setAttribute(SemanticAttributes.HTTP_URL, urlStr);
		cakeSpan.setAttribute(SemanticAttributes.NET_PEER_IP, "127.0.0.1");

		cakeSpan.makeCurrent();
		HttpHeaders headers = tracingUtil.injectHeaders(new HttpHeaders());

		customer = restTemplate.exchange(urlStr, HttpMethod.GET,
				new HttpEntity("parameters", headers), Customer.class);

		return customer.getBody();
	}

}
