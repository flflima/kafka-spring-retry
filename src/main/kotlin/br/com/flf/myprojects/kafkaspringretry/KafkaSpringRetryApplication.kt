package br.com.flf.myprojects.kafkaspringretry

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
open class KafkaSpringRetryApplication

fun main(args: Array<String>) {
	runApplication<KafkaSpringRetryApplication>(*args)
}
