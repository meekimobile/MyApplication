package com.example.meek.myapplication

class Employee(var department: String?, name: String?) : Person(name) {

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val e1 = Employee("RND","Steward")
            val e2 = Employee("RND","Martha")
            println(e1.name)
            println(e2.name)
        }
    }
}

