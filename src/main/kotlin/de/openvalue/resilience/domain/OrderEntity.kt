package de.openvalue.resilience.domain

import jakarta.persistence.*

@Entity
data class OrderEntity(val email: String,
                       @OneToMany(fetch = FetchType.EAGER, cascade = [CascadeType.ALL], mappedBy = "") val items: List<OrderItem>,
                       @Id @GeneratedValue val id: Long ?= null)

@Entity
data class OrderItem(val name: String,
                     val count: Int,
                     @Id @GeneratedValue val id: Long? = null)

