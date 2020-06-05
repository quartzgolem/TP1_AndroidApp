package com.example.final_ig2i;

public class Message {
    private String contenu;
    private String auteur;
    private String couleur;

    public Message() {

    }

    public String getContenu() {
        return contenu;
    }

    public String getAuteur() {
        return auteur;
    }

    public String getCouleur() {
        return couleur;
    }

    @Override
    public String toString() {
        return "[" + auteur + "] " + contenu;
    }
}
