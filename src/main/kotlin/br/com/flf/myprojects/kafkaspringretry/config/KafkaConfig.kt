package br.com.flf.myprojects.kafkaspringretry.config

import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.common.serialization.StringDeserializer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.core.KafkaOperations
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer
import org.springframework.kafka.listener.SeekToCurrentErrorHandler
import org.springframework.kafka.listener.adapter.RetryingMessageListenerAdapter
import org.springframework.retry.backoff.FixedBackOffPolicy
import org.springframework.retry.policy.SimpleRetryPolicy
import org.springframework.retry.support.RetryTemplate
import org.springframework.util.backoff.FixedBackOff
import java.util.Optional

@EnableKafka
@Configuration
open class KafkaConfig {

    @Autowired
    lateinit var backOffConfig: BackOffConfig

    @Autowired
    lateinit var springKafkaConfig: SpringKafkaConfig

    @Bean
    open fun errorHandler(template: KafkaOperations<String, Any>): SeekToCurrentErrorHandler? {
//        val backOff = ExponentialBackOff(1000L, 2.0)
//        backOff.maxInterval = 30000
//        return SeekToCurrentErrorHandler(
//                DeadLetterPublishingRecoverer(template), backOff)
        return SeekToCurrentErrorHandler(
                DeadLetterPublishingRecoverer(template), FixedBackOff(backOffConfig.interval, backOffConfig.maxAttempts))
    }

    @Bean
    open fun consumerFactory(): ConsumerFactory<String, Any> {
        val props = mapOf(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to springKafkaConfig.bootstrapServers,
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java,
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java
        )
        return DefaultKafkaConsumerFactory(props)
    }

    @Bean
    open fun kafkaRetryFactory(kafkaTemplate: KafkaOperations<String, Any>):
            ConcurrentKafkaListenerContainerFactory<String, Any> {
        val factory = ConcurrentKafkaListenerContainerFactory<String, Any>()
        factory.consumerFactory = consumerFactory()
//        factory.setErrorHandler(errorHandler(template = kafkaTemplate))
        factory.setRetryTemplate(retryTemplate())
        factory.setRecoveryCallback { context ->
            val record = context.getAttribute(RetryingMessageListenerAdapter.CONTEXT_RECORD) as ConsumerRecord<*, *>
            val errorTopic: String = record.topic().replace("retry", "error")
            println("Sending ${record.value()} to $errorTopic topic")
            kafkaTemplate.send(errorTopic, record.value())
            Optional.empty<String>()
        }
        return factory
    }

    @Bean
    open fun retryTemplate(): RetryTemplate {
        val retryTemplate = RetryTemplate()
        val fixedBackoffPolicy = FixedBackOffPolicy()
        fixedBackoffPolicy.backOffPeriod = backOffConfig.interval
        retryTemplate.setBackOffPolicy(fixedBackoffPolicy)

        val retryPolicy = SimpleRetryPolicy()
        retryPolicy.maxAttempts = backOffConfig.maxAttempts.toInt()
        retryTemplate.setRetryPolicy(retryPolicy)

        return retryTemplate
    }
}