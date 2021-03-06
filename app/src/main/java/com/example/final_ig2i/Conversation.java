package com.example.final_ig2i;

public class Conversation {

    /*
    * {"connecte":true,
    * "action":"getConversations",
    * "feedback":"entrez action: logout, setPasse(passe),setPseudo(pseudo), setCouleur(couleur),getConversations, getMessages(idConv,[idLastMessage]), setMessage(idConv,contenu), ...",
    * "conversations":[ {"id":"12","active":"1","theme":"Les cours en IAM"},
    *                   {"id":"2","active":"1","theme":"Ballon d'Or"}]}
    * */

    private int id;
    private String theme;
    private Integer active;

    // Raccourci : Alt+Ins pour générer getters, setters et constructeurs


    public Conversation(int id, String theme, Integer active) {
        this.id = id;
        this.theme = theme;
        this.active = active;
    }

    /*
    @Override
    public String toString() {
        return "Conversation{" +
                "id=" + id +
                ", theme='" + theme + '\'' +
                ", active=" + active +
                '}';
    }*/

    public String toString() {
        return theme;
    }


    public int getId() {
        return id;
    }

    public String getTheme() {
        return theme;
    }

    public Boolean isActive(){ return active != 0; }

    public void setId(int id) {
        this.id = id;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public void setActive(Integer active) {
        this.active = active;
    }
}
