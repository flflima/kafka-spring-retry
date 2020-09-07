package br.com.flf.myprojects.kafkaspringretry.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "kafka.topic.retry.blocking")
open class TopicRetryBlockingConfig {
    lateinit var name: String
}