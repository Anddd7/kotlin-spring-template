package com.github.anddd7.bitwise

import com.fasterxml.jackson.annotation.JsonIdentityInfo
import com.fasterxml.jackson.annotation.ObjectIdGenerators
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table

@RestController
@RequestMapping("/bitwise")
class BitwiseOperatorController(
    private val repository: BitwiseOperatorRepository
) {

    @GetMapping
    fun bitwise(available: Int) = repository.searchByAvailable(available).map(BitwiseOperator::id).joinToString(",")
}

@Repository
interface BitwiseOperatorRepository : JpaRepository<BitwiseOperator, Long> {
    @Query(
        value = "SELECT * FROM bitwise_operator o WHERE o.available & ?1 = ?1",
        nativeQuery = true,
    )
    fun searchByAvailable(available: Int): List<BitwiseOperator>
}

@Entity
@Table(name = "bitwise_operator")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator::class, property = "id")
data class BitwiseOperator(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Long,
    @Column(name = "name")
    val name: String,
    @Column(name = "available")
    val available: Int,
)
