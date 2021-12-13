package com.github.anddd7.security.model

import com.fasterxml.jackson.annotation.JsonIdentityInfo
import com.fasterxml.jackson.annotation.ObjectIdGenerators
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.MapsId
import javax.persistence.OneToOne
import javax.persistence.PrimaryKeyJoinColumn
import javax.persistence.Table

@Entity
@Table(name = "auth_user")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator::class, property = "id")
class AuthUser(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Long = 0,

    @Column(name = "name")
    val name: String = ""
) {
    /**
     * @JoinColumn indicates that this entity is the owner of the relationship
     * (that is: the corresponding table has a column with a foreign key to the referenced table)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id")
    val role: AuthRole? = null

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "id")
    lateinit var info: UserInfo

    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY)
    lateinit var employee: Employee

    fun getPermissions() = role?.permissions ?: emptyList()
}

@Entity
@Table(name = "user_info")
data class UserInfo(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Long = 0,

    @Column(name = "email")
    val email: String,
) {
    @OneToOne(mappedBy = "info", fetch = FetchType.LAZY)
    @PrimaryKeyJoinColumn
    lateinit var user: AuthUser
}

@Entity
@Table(name = "employee")
data class Employee(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Long = 0,

    @Column(name = "department")
    val department: String,
) {
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    lateinit var user: AuthUser
}
