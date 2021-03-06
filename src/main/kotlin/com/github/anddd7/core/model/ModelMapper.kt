package com.github.anddd7.core.model

interface ModelMapper<T, M, A> {

  fun toDTO(model: M): T = TODO("not implemented")

  fun toDAO(model: M): A = TODO("not implemented")

  fun fromDTO(dto: T): M = TODO("not implemented")

  fun fromDAO(dao: A): M = TODO("not implemented")

  fun toDTO(models: Collection<M>) = models.map { toDTO(it) }

  fun toDAO(models: Collection<M>) = models.map { toDAO(it) }

  fun fromDTO(dtos: Collection<T>) = dtos.map { fromDTO(it) }

  fun fromDAO(daos: Collection<A>) = daos.map { fromDAO(it) }
}
