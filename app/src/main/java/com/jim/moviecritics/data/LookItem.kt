package com.jim.moviecritics.data

sealed class LookItem(val order: Int) {

    abstract val look: Look
    abstract val id: Int
    abstract val mediaType: String

    data class LookMovie(override val look: Look) : LookItem(0) {
        override val id: Int
            get() = look.id
        override val mediaType: String
            get() = look.mediaType
    }

    data class LookTelevision(override val look: Look) : LookItem(1) {
        override val id: Int
            get() = look.id
        override val mediaType: String
            get() = look.mediaType
    }

    data class LookPerson(override val look: Look) : LookItem(2) {
        override val id: Int
            get() = look.id
        override val mediaType: String
            get() = look.mediaType
    }
}
