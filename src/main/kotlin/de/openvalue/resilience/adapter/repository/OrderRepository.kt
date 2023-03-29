package de.openvalue.resilience.adapter.repository

import de.openvalue.resilience.domain.OrderEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface OrderRepository: JpaRepository<OrderEntity, String>
