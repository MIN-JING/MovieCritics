package com.jim.moviecritics.data

sealed class LookItem {

    abstract val id: Int

    data class LookMovie(val look: Look) : LookItem() {
        override val id: Int
            get() = look.id
    }

    data class LookTelevision(val look: Look) : LookItem() {
        override val id: Int
            get() = look.id
    }

    data class LookPerson(val look: Look) : LookItem() {
        override val id: Int
            get() = look.id
    }
}