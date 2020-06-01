package com.ahujadevansh.mbuddy

data class User(val uid: String, val username: String, val profileImageUrl: String) {
    constructor() : this("", "", "")
}