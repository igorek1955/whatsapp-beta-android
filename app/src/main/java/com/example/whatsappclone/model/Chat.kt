package com.example.whatsappclone.model

class Chat(val sender: String, val receiver: String, val message: String, val isseen: Boolean) {
    constructor() : this("", "", "", false) {}
}