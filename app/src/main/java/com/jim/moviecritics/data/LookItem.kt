package com.jim.moviecritics.data

sealed class LookItem(val order: Int) {

    abstract val id: Int

    data class LookMovie(val look: Look) : LookItem(0) {
        override val id: Int
            get() = look.id
    }

    data class LookTelevision(val look: Look) : LookItem(1) {
        override val id: Int
            get() = look.id
    }

    data class LookPerson(val look: Look) : LookItem(2) {
        override val id: Int
            get() = look.id
    }
}