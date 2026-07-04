package com.amouri_dev.stockflow.common;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Enables Spring's scheduled task support (e.g. the refresh-token cleanup job).
 */
@Configuration
@EnableScheduling
public class SchedulingConfig {
}
