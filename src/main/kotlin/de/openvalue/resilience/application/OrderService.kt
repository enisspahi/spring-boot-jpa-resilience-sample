package de.openvalue.resilience.application

import de.openvalue.resilience.adapter.repository.OrderRepository
import de.openvalue.resilience.domain.OrderEntity
import de.openvalue.resilience.domain.OrderReceived
import jakarta.transaction.Transactional
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service

@Service
class OrderService(val orderRepository: OrderRepository,
                   val publisher: ApplicationEventPublisher) {

    @Transactional
    fun create(order: OrderEntity): OrderEntity {
        return orderRepository.save(order)
                .also { publisher.publishEvent(OrderReceived(it)) }
    }

}