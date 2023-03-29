package de.openvalue.resilience.adapter.repository

import jakarta.persistence.Entity
import jakarta.persistence.Id
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface StreamOffsetRepository : JpaRepository<StreamOffset, String>

@Entity
data class StreamOffset(@Id val name: String,
                        val value: LocalDateTime)
