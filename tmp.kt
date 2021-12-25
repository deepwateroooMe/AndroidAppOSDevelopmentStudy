package com.me.ktl

class Person constructor(firstName: String) {
    init {
        print("Person initialized with value ${firstName}")
    }
}

/*
 上面的代码可以简写为
 class Person(firstName: String) {
     init {
     print("Person initialized with value ${firstName}")
 }
 }
 */
