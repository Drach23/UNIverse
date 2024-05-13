package com.unitech.universe.post_feed

import com.google.firebase.firestore.Exclude

data  class Publicacion(
    var usuarioId: String? = null,
    var titulo: String? = null,
    var costo: Float? = null,
    var categoria: String? = null,
    var descripcion: String? = null,
    var ubicacion: String? = null,
    var stock: Int? = null,
    var imagenUrl: String? = null,
    var likes: List<String>? = listOf(),
    var dislikes: List<String>? = listOf(),
    var fechaPublicacion: String? = null

){
    @Exclude
    @get:Exclude
    @set:Exclude
    var uid: String? = null

    constructor() : this(
        usuarioId = null,
        titulo = null,
        costo = null,
        categoria = null,
        descripcion = null,
        ubicacion = null,
        stock = null,
        imagenUrl = null,
        likes = listOf(),
        dislikes = listOf(),
        fechaPublicacion = null
    )
}
