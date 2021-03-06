package com.github.anddd7.security.model

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class AuthPermissionTest {

  @Test
  fun isBelongTo() {
    assertThat(AuthPermission("DASHBOARD").belong(PermissionCode.DASHBOARD)).isTrue()
    assertThat(AuthPermission("ORDER").belong(PermissionCode.ORDER)).isTrue()

    assertThat(AuthPermission("Others").belong(PermissionCode.DASHBOARD)).isFalse()
  }
}
