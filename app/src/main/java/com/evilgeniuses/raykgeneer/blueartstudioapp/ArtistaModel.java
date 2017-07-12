/**
 * @Developer: Juan Camilo Peña Vahos
 * @Description: Clase modelo de los artistas
 * @Date: 17/05/2017
 * TODO:
 * */

package com.evilgeniuses.raykgeneer.blueartstudioapp;

public class ArtistaModel {

    private String nombre;
    private String apodo;
    private String estilos;
    private String descripcion;
    private String imageURL;
    private String linkInsta;
    private String linkFace;
    private String faceCode;

    public ArtistaModel(){}

    public ArtistaModel(String nombre, String apodo, String estilos, String descripcion, String imageURL,
                        String linkInsta, String linkFace, String faceCode){
        this.nombre = nombre;
        this.apodo = apodo;
        this.estilos = estilos;
        this.descripcion = descripcion;
        this.imageURL = imageURL;
        this.linkInsta =  linkInsta;
        this.linkFace = linkFace;
        this.faceCode = faceCode;
    }

    public String getNombre() {return nombre;}
    public String getApodo() {return apodo;}
    public String getEstilos() {return estilos;}
    public String getDescripcion() {return descripcion;}
    public String getImageURL() {return imageURL;}
    public String getLinkInsta() {return linkInsta;}
    public String getLinkFace() {return linkFace;}
    public String getFaceCode() {return faceCode;}
}
