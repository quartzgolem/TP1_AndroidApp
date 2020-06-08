package com.example.final_ig2i;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ChoixConvActivity extends RestActivity {

    private ListeConversations listeConvs;
    private ListView lv;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choix_conversation);

        Bundle bdl = getIntent().getExtras();
        username = bdl.getString("username");

        // Au démarrage de l'activité, réaliser une requete
        // Pour récupérer les conversations
        String qs = "action=getConversations";

        // On se sert des services offerts par RestActivity,
        // qui propose des méthodes d'envoi de requetes asynchrones
        envoiRequete(qs, "recupConversations");

        listeConvs = new ListeConversations();

        lv = findViewById(R.id.choixConversation_choixConv);
    }


    @Override
    public void traiteReponse(JSONObject o, String action) {
        if (action.contentEquals("recupConversations")) {
            gs.alerter(o.toString());

            // On transforme notre objet JSON en une liste de "Conversations"
            // On pourrait utiliser la librairie GSON pour automatiser ce processus d'interprétation
            // des objets JSON reçus
            // Cf. poly de centrale "programmation mobile et réalité augmentée"

            // Ici, on se contente de créer une classe "Conversation" et une classe "ListeConversations"
            // On parcourt l’objet JSON pour instancier des Conversations, que l’on insère dans la liste

            /*
             * {"connecte":true,
             * "action":"getConversations",
             * "feedback":"entrez action: logout, setPasse(passe),setPseudo(pseudo), setCouleur(couleur),getConversations, getMessages(idConv,[idLastMessage]), setMessage(idConv,contenu), ...",
             * "conversations":[ {"id":"12","active":"1","theme":"Les cours en IAM"},
             *                   {"id":"2","active":"1","theme":"Ballon d'Or"}]}
             * */

            int i;
            JSONArray convs;
            try {
                convs = o.getJSONArray("conversations");
                for(i=0;i<convs.length();i++) {
                    JSONObject nextConv = (JSONObject) convs.get(i);

                    int id =Integer.parseInt(nextConv.getString("id"));
                    String theme = nextConv.getString("theme");
                    Boolean active = nextConv.getString("active").contentEquals("1");

                    gs.alerter("Conv " + id  + " theme = " + theme + " active ?" + active);
                    Conversation c = new Conversation(id,theme,active);

                    listeConvs.addConversation(c);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            gs.alerter(listeConvs.toString());

            // On peut maintenant appuyer sur le bouton
            remplirSpinner();
        }
    }

    private void remplirSpinner() {
        // V2 : Utilisation d'un adapteur customisé qui permet de définir nous-même
        // la forme des éléments à afficher

        lv.setAdapter(new MyCustomAdapter(
            this,
            R.layout.spinner_item,
            listeConvs.getList()
        ));
    }



    public class MyCustomAdapter extends ArrayAdapter<Conversation> {

        private int layoutId;
        private ArrayList<Conversation> dataConvs;
        private ChoixConvActivity activity;

        MyCustomAdapter(ChoixConvActivity context, int itemLayoutId, ArrayList<Conversation> objects) {
            super(context, itemLayoutId, objects);
            activity = context;
            layoutId = itemLayoutId;
            dataConvs = objects;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();
            View item = inflater.inflate(layoutId, parent, false);
            final Conversation nextC = dataConvs.get(position);

            item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // lors du clic sur le bouton OK,
                    // récupérer l'id de la conversation sélectionnée
                    // démarrer l'activité d'affichage des messages

                    // NB : il faudrait être sur qu'on ne clique pas sur le bouton
                    // tant qu'on a pas fini de charger la liste des conversations
                    // On indique que le bouton est désactivé au départ.

                    gs.alerter("Conv sélectionnée : " + nextC.getTheme());

                    // On crée un Intent pour changer d'activité
                    Intent toShowConv = new Intent(activity,ShowConvActivity.class);
                    Bundle bdl = new Bundle();
                    bdl.putInt("idConversation",nextC.getId());
                    bdl.putString("themeConversation",nextC.getTheme());
                    bdl.putString("username",username);
                    toShowConv.putExtras(bdl);
                    startActivity(toShowConv);
                }
            });

            TextView label = item.findViewById(R.id.spinner_theme);
            label.setText(nextC.getTheme());

            ImageView icon = item.findViewById(R.id.spinner_icon);
            if (nextC.getActive()) {
                icon.setImageResource(R.drawable.icon);
            } else {
                icon.setImageResource(R.drawable.icongray);
            }

            return item;
        }
    }

}
