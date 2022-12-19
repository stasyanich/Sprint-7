package com.example.demo.persistance

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import kotlin.test.assertEquals

@DataJpaTest
internal class EntityRepositoryTest {
    @Autowired
    private lateinit var repository: EntityRepository

    @Test
    fun getById(){
        val expected = repository.save(Entity(name = "Uasya"))
        val actual = repository.findById(expected.id!!)
        assertEquals(expected, actual.get())
    }
}