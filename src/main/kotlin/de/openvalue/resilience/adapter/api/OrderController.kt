package de.openvalue.resilience.adapter.api

import de.openvalue.resilience.adapter.api.model.OrderResource
import de.openvalue.resilience.application.OrderService
import de.openvalue.resilience.domain.OrderEntity
import de.openvalue.resilience.domain.OrderItem
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class OrderController(val orderService: OrderService) {

    @PostMapping("/orders")
    fun createOrder(@RequestBody request: OrderResource) = orderService.create(request.toEntity()).toResponse()

    private fun OrderResource.toEntity() = OrderEntity(
            email = email,
            items = items.map { OrderItem(it.name, it.count) }
    )

    private fun OrderEntity.toResponse() = OrderResource(
            email = email,
            items = items.map { de.openvalue.resilience.adapter.api.model.OrderResource.OrderItem(it.name, it.count) },
            id = id)

}