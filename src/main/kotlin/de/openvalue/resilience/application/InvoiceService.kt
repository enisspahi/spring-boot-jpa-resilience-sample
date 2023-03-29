package de.openvalue.resilience.application

import de.openvalue.resilience.domain.OrderReceived
import io.github.resilience4j.retry.annotation.Retry
import org.slf4j.LoggerFactory
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import org.springframework.transaction.event.TransactionalEventListener

@Service
class InvoiceService(val mailSender: JavaMailSender) {

    private val logger = LoggerFactory.getLogger(InvoiceService::class.java)
    @Async
    @TransactionalEventListener
    @Retry(name = "InvoiceService")
    fun onEvent(event: OrderReceived) {
        with(event.orderEntity) {
            logger.info("Sending email for received order $this")
            val message = SimpleMailMessage()
            message.setTo(email)
            message.subject = "Invoice of your order $id"
            message.text =  "The invoice for your order $items"
            runCatching { mailSender.send(message) }
                    .onFailure { logger.error("Failed to send email", it) }
                    .getOrThrow()

        }
    }

}