/**
 * @Developer: Juan Camilo Peña Vahos
 * @Description: Clase modelo de la tienda
 * @Date: 17/05/2017
 * TODO:
 */

package com.evilgeniuses.raykgeneer.blueartstudioapp;

public class TiendaModel {

    private String producto;
    private String imageURL;
    private String precio;

    public TiendaModel() {} //Constructor

    public TiendaModel(String producto, String precio, String imageURL) {
        this.producto = producto;
        this.imageURL = imageURL;
        this.precio = precio;
    }

    //GETTERS
    public String getProducto() {return producto;}
    public String getImageURL() {return imageURL;}
    public String getPrecio() {return precio;}
}
