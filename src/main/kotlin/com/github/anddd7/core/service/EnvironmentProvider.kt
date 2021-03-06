package com.github.anddd7.core.service

import org.springframework.core.env.Environment
import org.springframework.stereotype.Component

@Component
class EnvironmentProvider(val environment: Environment) {
  fun notProduction() = !environment.activeProfiles.contains("prod")
  fun notOnline() = environment.activeProfiles.contains("local")
}
