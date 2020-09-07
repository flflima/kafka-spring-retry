package br.com.flf.myprojects.kafkaspringretry.service

import br.com.flf.myprojects.kafkaspringretry.config.TopicRetryBlockingConfig
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.messaging.handler.annotation.Header
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Service

@Service
class KafkaConsumerRetryBlockingService {

    @Autowired
    lateinit var kafkaTemplate: KafkaTemplate<String, String>

    @Autowired
    lateinit var topicRetryBlockingConfig: TopicRetryBlockingConfig

    fun receiveBlockingRetry(message: String) {
        this.kafkaTemplate.send(topicRetryBlockingConfig.name, message)
    }

    // Segunda alternativa: configurar duas filas - a principal e a de retry. Com a possibilidade de uma terceira
    // fila como um erro
    @KafkaListener(topics = ["\${kafka.topic.retry.blocking.name}"])
    fun consumerNonBlocking(@Payload valor: String, @Header(KafkaHeaders.RECEIVED_TOPIC) topic: String) {
        try {
            if (valor.contains("fail")) {
                throw RuntimeException("Error! => $valor")
            }
            println("Consumer - [$topic]: $valor")
        } catch (e: Exception) {
            println("Error! => Sending $valor to ${topic}_retry")
            this.kafkaTemplate.send("${topic}_retry", valor)
        }
    }

    @KafkaListener(topics = ["\${kafka.topic.retry.blocking.name}_retry"], containerFactory = "kafkaRetryFactory",
            groupId = "\${spring.kafka.consumer.group-id}")
    fun retryBlocking(@Payload valor: String, @Header(KafkaHeaders.RECEIVED_TOPIC) topic: String) {
        if (valor.contains("fail")) {
            println("Retry Error! => Message $valor still has errors!")
            throw RuntimeException("Error! => $valor")
        }
        println("Retry - [$topic]: $valor")
    }
}
