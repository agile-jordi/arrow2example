package com.agilogy.arrow2.codec

sealed class Location {
    object Root: Location(){
        override fun toString(): String = ""
    }
    data class Element(val arrayPosition: Location, val index: Int): Location(){
        override fun toString(): String = "$arrayPosition[$index]"
    }
    data class Member(val objectPosition: Location, val member: String): Location(){
        override fun toString(): String = objectPosition.toString().ifEmpty { null }?.let{"$objectPosition.$member"} ?: member
    }

    operator fun div(index: Int): Element = Element(this, index)
    operator fun div(member: String): Member = Member(this, member)
}