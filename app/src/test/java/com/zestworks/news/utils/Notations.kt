package com.zestworks.news.utils


infix fun <T> T.shouldBe(any: Any?) {
    assert(any == this)
}