package br.com.flf.myprojects.kafkaspringretry.service

import br.com.flf.myprojects.kafkaspringretry.config.TopicConsumerBlockingConfig
import br.com.flf.myprojects.kafkaspringretry.config.TopicRetryBlockingConfig
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.Acknowledgment
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.messaging.handler.annotation.Header
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Service
import java.lang.RuntimeException

@Service
class KafkaConsumerBlockingService {

    @Autowired
    lateinit var kafkaTemplate: KafkaTemplate<String, String>

    @Autowired
    lateinit var topicConsumerBlockingConfig: TopicConsumerBlockingConfig

    fun receiveBlockingConsumer(message: String) {
        this.kafkaTemplate.send(topicConsumerBlockingConfig.name, message)
    }

    // Primeira alternativa: configurar o retry com SeekToCurrentErrorHandler, mas vai bloquear o consumidor enquanto nao esgotar os retries
    @KafkaListener(topics = ["\${kafka.topic.consumer.blocking.name}"])
    fun consumerBlocking(@Payload valor: String, @Header(KafkaHeaders.RECEIVED_TOPIC) topic: String) {
        if (valor.contains("fail")) {
            println("Error! => Message $valor has errors!")
            throw RuntimeException("Error! => $valor")
        }
        println("First Consumer - [$topic]: $valor")
    }

    @KafkaListener(topics = ["\${kafka.topic.consumer.blocking.name}"], groupId = "\${kafka.topic.another-consumer}")
    fun anotherConsumer(@Payload valor: String, @Header(KafkaHeaders.RECEIVED_TOPIC) topic: String) {
        println("Second Consumer - [$topic]: $valor")
    }

}
