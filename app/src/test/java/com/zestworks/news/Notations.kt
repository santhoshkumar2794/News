package com.zestworks.news


infix fun <T> T.shouldBe(any: Any?) {
    assert(any == this)
}