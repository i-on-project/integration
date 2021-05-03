package org.ionproject.integration

import org.ionproject.integration.filerepository.serializer.ISerializer
import org.ionproject.integration.filerepository.serializer.SerializerImpl
import org.ionproject.integration.model.OutputFormat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class SerializerTests {
    private val serializer: ISerializer = SerializerImpl()

    @Test
    fun `when serializing a Vehicle to JSON then success`() {
        val car = Vehicle("Starlet", 1996, listOf(Person("Cris", 31), Person("Xana", 29)))
        val actual = serializer.serialize(car, OutputFormat.JSON)

        val expected =
            """{"name":"Starlet","year":1996,"owners":[{"name":"Cris","age":31},{"name":"Xana","age":29}]}"""
        assertEquals(expected, actual)
    }

    @Test
    fun `when serializing a Vehicle to YAML then success`() {
        val car = Vehicle("Starlet", 1996, listOf(Person("Cris", 31), Person("Xana", 29)))
        val actual = serializer.serialize(car, OutputFormat.YAML)

        val expected =
            "---\nname: \"Starlet\"\nyear: 1996\nowners:\n- name: \"Cris\"\n  age: 31\n- name: \"Xana\"\n  age: 29\n"
        assertEquals(expected, actual)
    }

    @Test
    fun `when serializing a list to JSON then success`() {
        val car1 = Vehicle("Dragula", 1715, listOf(Person("Rob Zombie", 54), Person("Count Vlad", 666)))
        val car2 = Vehicle("Kit", 1980, listOf(Person("Michael", 33)))
        val cars = listOf(car1, car2)

        val actual = serializer.serialize(cars, OutputFormat.JSON)
        val expected =
            """[{"name":"Dragula","year":1715,"owners":[{"name":"Rob Zombie","age":54},{"name":"Count Vlad","age":666}]},{"name":"Kit","year":1980,"owners":[{"name":"Michael","age":33}]}]"""

        assertEquals(expected, actual)
    }

    @Test
    fun `when serializing a list to YAML then success`() {
        val car1 = Vehicle("Dragula", 1715, listOf(Person("Rob Zombie", 54), Person("Count Vlad", 666)))
        val car2 = Vehicle("Kit", 1980, listOf(Person("Michael", 33)))
        val cars = listOf(car1, car2)

        val actual = serializer.serialize(cars, OutputFormat.YAML)
        val expected =
            "---\n- name: \"Dragula\"\n  year: 1715\n  owners:\n  - name: \"Rob Zombie\"\n    age: 54\n  - name: \"Count Vlad\"\n    age: 666\n- name: \"Kit\"\n  year: 1980\n  owners:\n  - name: \"Michael\"\n    age: 33\n"

        assertEquals(expected, actual)
    }

    data class Vehicle(val name: String, val year: Int, val owners: List<Person>)
    data class Person(val name: String, val age: Int)
}
