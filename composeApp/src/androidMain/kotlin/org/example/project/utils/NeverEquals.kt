package org.example.project.utils

import kotlin.random.Random.Default.nextInt

open class NeverEquals {
    override fun hashCode(): Int = nextInt()

    override fun equals(other: Any?): Boolean = false
}