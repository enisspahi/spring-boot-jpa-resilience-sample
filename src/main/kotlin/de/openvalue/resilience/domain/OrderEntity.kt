package de.openvalue.resilience.domain

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@Entity
@EntityListeners(AuditingEntityListener::class)
data class OrderEntity(val email: String,
                       @OneToMany(fetch = FetchType.EAGER, cascade = [CascadeType.ALL], mappedBy = "") val items: List<OrderItem>,
                       @Id @GeneratedValue val id: Long ?= null,
                       @CreatedDate var createdDate: LocalDateTime?= null)

@Entity
data class OrderItem(val name: String,
                     val count: Int,
                     @Id @GeneratedValue val id: Long? = null)


