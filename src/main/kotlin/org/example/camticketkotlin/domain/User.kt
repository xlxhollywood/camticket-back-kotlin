package org.example.camticketkotlin.domain

import jakarta.persistence.*
import org.example.camticketkotlin.domain.enums.Role
import org.example.camticketkotlin.dto.UserDto

@Entity
@Table(name = "`user`")
class User private constructor(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long? = null,

        @Column(nullable = false)
        val kakaoId: Long? = null,

        @Column(nullable = false, length = 200)
        var name: String? = null,

        @Column(nullable = true, length = 30)
        var nickName: String? = null,

        @Column(nullable = false, length = 30)
        var email: String? = null,

        @Column(nullable = false, columnDefinition = "TEXT")
        var profileImageUrl: String? = null,

        @Column(nullable = true, length = 500)
        var introduction: String? = null,

        @Column(nullable = true, length = 100)
        var bankAccount: String? = null,

        @Enumerated(EnumType.STRING)
        @Column(nullable = false)
        val role: Role


) : BaseEntity() {

    companion object {
        fun from(dto: UserDto): User {
            return User(
                kakaoId = requireNotNull(dto.kakaoId),
                name = requireNotNull(dto.name),
                nickName = dto.nickName ?: "",
                email = requireNotNull(dto.email),
                profileImageUrl = requireNotNull(dto.profileImageUrl),
                introduction = dto.introduction ?: "",
                bankAccount = dto.bankAccount ?: "",
                role = Role.ROLE_USER
            )
        }
    }
}
