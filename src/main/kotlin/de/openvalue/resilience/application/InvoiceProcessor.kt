package de.openvalue.resilience.application

import de.openvalue.resilience.adapter.repository.OrderRepository
import de.openvalue.resilience.adapter.repository.StreamOffset
import de.openvalue.resilience.adapter.repository.StreamOffsetRepository
import de.openvalue.resilience.domain.OrderReceived
import io.github.resilience4j.reactor.retry.RetryOperator
import io.github.resilience4j.retry.Retry
import io.github.resilience4j.retry.RetryConfig
import jakarta.annotation.PostConstruct
import org.slf4j.LoggerFactory
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.Duration
import java.time.LocalDateTime
import java.time.Month

@Component
class InvoiceProcessor(val mailSender: JavaMailSender,
                       val orderRepository: OrderRepository,
                       val streamOffsetRepository: StreamOffsetRepository) {

    private val logger = LoggerFactory.getLogger(InvoiceProcessor::class.java)

    private var retry: Retry = Retry.of("InvoiceService", RETRY_CONFIG)

    private val stream = Mono.just(Unit).repeat().delayElements(STREAM_INTERVAL)
            .flatMap {
                val offset = streamOffsetRepository.findById(OFFSET_NAME).map { it.value }.orElse(NO_OFFSET)
                Flux.fromIterable(orderRepository.findByCreatedDateAfter(offset))
            }
            .map { OrderReceived(it) }
            .map { onEvent(it) }
            .map { streamOffsetRepository.save(StreamOffset(OFFSET_NAME, it.orderEntity.createdDate!!)) }
            .transformDeferred(RetryOperator.of(retry))

    @PostConstruct
    fun initialize() {
        stream.subscribe { logger.info("Consumed $it") }
    }

    fun onEvent(event: OrderReceived): OrderReceived {
        with(event.orderEntity) {
            logger.info("Sending email for received order $this")
            val message = SimpleMailMessage()
            message.setTo(email)
            message.subject = "Invoice of your order $id"
            message.text = "The invoice for your order $items. Ignore this email if you have already received before."
            runCatching { mailSender.send(message) }
                    .onFailure { logger.error("Failed to send email", it) }
                    .getOrThrow()
        }
        return event
    }

    companion object {
        private val NO_OFFSET: LocalDateTime = LocalDateTime.of(2023, Month.MARCH, 1, 0, 0)
        private val STREAM_INTERVAL: Duration = Duration.ofSeconds(1)
        private const val OFFSET_NAME = "InvoiceProcessorOffset"

        private val RETRY_CONFIG: RetryConfig =
                RetryConfig.custom<RetryConfig>()
                        .maxAttempts(10)
                        .waitDuration(Duration.ofSeconds(3))
                        .build()

    }

}