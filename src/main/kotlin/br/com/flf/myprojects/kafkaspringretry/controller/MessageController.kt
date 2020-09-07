package br.com.flf.myprojects.kafkaspringretry.controller

import br.com.flf.myprojects.kafkaspringretry.service.KafkaConsumerBlockingService
import br.com.flf.myprojects.kafkaspringretry.service.KafkaConsumerRetryBlockingService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class MessageController {

    @Autowired
    lateinit var kafkaConsumerBlockingService: KafkaConsumerBlockingService

    @Autowired
    lateinit var kafkaConsumerRetryBlockingService: KafkaConsumerRetryBlockingService

    @GetMapping("block/consumer/message/{message}")
    fun receiveBlockingConsumer(@PathVariable message: String) {
        this.kafkaConsumerBlockingService.receiveBlockingConsumer(message)
    }

    @GetMapping("block/retry/message/{message}")
    fun receiveBlockingRetry(@PathVariable message: String) {
        this.kafkaConsumerRetryBlockingService.receiveBlockingRetry(message)
    }
}