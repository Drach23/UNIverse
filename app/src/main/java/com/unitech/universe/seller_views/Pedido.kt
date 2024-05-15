package com.unitech.universe.seller_views
import com.google.firebase.firestore.Exclude
data class Pedido(
    var cantidad: Int? = null,
    var comprador: String? = null,
    var costo: Double? = null,
    var telComprador: String? = null,
    var titulo: String? = null,
    var compradorId: String? = null,
    var vendedorId: String? = null,
    var state: String? = null
){
    @Exclude
    @get:Exclude
    @set:Exclude
    var uid: String? = null

    constructor() : this(
        cantidad = null,
        comprador = null,
        costo = null,
        telComprador = null,
        titulo = null,
        compradorId  = null,
        vendedorId = null,
        state = null
    )
}
