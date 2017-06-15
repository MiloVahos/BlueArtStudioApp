/**
 * @Developer: Juan Camilo Peña Vahos
 * @Description: Clase modelo de las noticias
 * @Date: 17/05/2017
 * TODO:
 */

package com.evilgeniuses.raykgeneer.blueartstudioapp;

public class NoticiaModel {

    private String titulo;
    private String imageURL;
    private String descripcion;
    private String fecha;
    private int valor; //Este parámetro sirve para determinar el orden en que van a aparecer las novedades

    public NoticiaModel() {} //Constructor

    public NoticiaModel(String titulo, String descripcion, String imageURL, String fecha, int valor) {
        this.titulo = titulo;
        this.imageURL = imageURL;
        this.descripcion = descripcion;
        this.fecha = fecha;
        this.valor = valor;
    }

    //GETTERS
    public String getTitulo() {
        return titulo;
    }
    public String getImageURL() {
        return imageURL;
    }
    public String getDescripcion() {
        return descripcion;
    }
    public String getFecha() { return fecha; }
    public int getValor() { return  valor; }

}
