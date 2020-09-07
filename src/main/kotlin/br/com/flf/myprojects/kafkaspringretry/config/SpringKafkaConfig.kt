package br.com.flf.myprojects.kafkaspringretry.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "spring.kafka")
open class SpringKafkaConfig {
    lateinit var bootstrapServers: String
}

