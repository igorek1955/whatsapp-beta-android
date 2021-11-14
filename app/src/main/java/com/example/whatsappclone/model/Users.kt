package com.example.whatsappclone.model

class Users {
    // Getters and Setters
    var id: String? = null
    var username: String? = null
    var imageUrl: String? = "default"
    var status: String? = "offline"

    // Constructors;
    constructor() {}
    constructor(id: String?, username: String?, imageUrl: String?, status: String?) {
        this.id = id
        this.username = username
        this.imageUrl = imageUrl
        this.status = status
    }
}