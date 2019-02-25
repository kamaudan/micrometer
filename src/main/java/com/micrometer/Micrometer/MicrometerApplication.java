package com.micrometer.Micrometer;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.binder.MeterBinder;
import io.micrometer.core.instrument.config.MeterFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
public class MicrometerApplication {

	public static void main(String[] args) {
		SpringApplication.run(MicrometerApplication.class, args);

	}

	private final ScheduledExecutorService executorService =
			Executors.newScheduledThreadPool(1);


	@Bean
    MeterBinder meterBinder(){
	    return new MeterBinder() {
            @Override
            public void bindTo(MeterRegistry meterRegistry) {

            }
        };
    }

	@Bean
    MeterRegistryCustomizer<MeterRegistry> registryCustomizer(@Value("${REGION:eu-east}") String region){
	    return  registry -> registry.config().commonTags("region", region);


    }

    @Bean
    MeterFilter meterFilter() {
	    return MeterFilter.denyNameStartsWith("jvm");
    }


	@Bean
	ApplicationRunner runner(MeterRegistry registry){
		return  args -> {
			this.executorService.scheduleWithFixedDelay(
			        () ->
                            Timer
                                    .builder("transforming-photo")
                                    .sla(Duration.ofMillis(1), Duration.ofSeconds(10))
                                    .publishPercentileHistogram()
                                    .tag("format", Math.random() > .5 ? "png" : "jpg")
                                    .register(registry)
                                    .record(Duration.ofMillis( (long)(Math.random() * 1000)))
					,500, 500, TimeUnit.MILLISECONDS);
		};



	}
}
