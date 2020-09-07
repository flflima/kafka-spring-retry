package br.com.flf.myprojects.kafkaspringretry.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration
import kotlin.properties.Delegates

@Configuration
@ConfigurationProperties(prefix="kafka.backoff")
open class BackOffConfig {
    var maxAttempts by Delegates.notNull<Long>()
    var interval by Delegates.notNull<Long>()
}