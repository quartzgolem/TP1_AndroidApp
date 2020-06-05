package com.example.final_ig2i;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class ShowConvActivity extends RestActivity implements View.OnClickListener {

    private int idConv;
    private int idLastMessage = 0;

    private LinearLayout msgLayout;
    private Button btnOK;
    private EditText edtMsg;

    @Override
    public void traiteReponse(JSONObject o, String action) {
        if (action.contentEquals("posterMessage")) {
            gs.alerter("retour de la requete posterMessage");
        }
        if (action.contentEquals("chargement_messages")) {
            // On a reçu des messages
            // gs.alerter(o.toString());
           /* {"connecte":true,
            "action":"getMessages",
            "feedback":"entrez action: logout, setPasse(passe),setPseudo(pseudo),
            setCouleur(couleur),getConversations,
            getMessages(idConv,[idLastMessage]),
            setMessage(idConv,contenu), ...",
            "messages":[{"id":"35",
                            "contenu":"Que pensez-vous des cours en IAM ?",
                            "auteur":"Tom",
                            "couleur":"#ff0000"}]
            ,"idLastMessage":"35"}
            */

            try {

                // parcours des messages
                String messagesStr = o.getJSONArray("messages").toString();
                Gson gson = new Gson();
                Type messageCollectionType = new TypeToken<Collection<Message>>(){}.getType();
                ArrayList<Message> messageList = gson.fromJson(messagesStr, messageCollectionType);

                for(Message m : messageList) {
                    TextView tv = new TextView(this);
                    String str = m.toString();
                    tv.setText(str);
                    tv.setTextColor(Color.parseColor(m.getCouleur()));
                    msgLayout.addView(tv);
                }

                // mise à jour du numéro du dernier message
                idLastMessage = Integer.parseInt(o.getString("idLastMessage"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_conversation);

        Bundle bdl = getIntent().getExtras();
        idConv = bdl.getInt("idConversation");

        gs.alerter(Integer.toString(idConv));

        // On récupère la liste des messages périodiquement
        // action=getMessages&idConv=<ID>

        // NB : l'API fournit une route
        // qui permet d'indiquer le dernier message dont on dispose
        // action=getMessages&idConv=<ID>&idLastMessage=<NUMERO>

        requetePeriodique(10, "chargement_messages");
        msgLayout = findViewById(R.id.conversation_svLayoutMessages);

        btnOK = findViewById(R.id.conversation_btnOK);
        btnOK.setOnClickListener(this);

        edtMsg = findViewById(R.id.conversation_edtMessage);
    }

    public String urlPeriodique(String action) {
        String qs = "";
        if (action.equals("chargement_messages")) {
            qs = "action=getMessages&idConv=" + idConv;
            qs += "&idLastMessage=" + idLastMessage;
        }

        return qs;
    }

    @Override
    public void onClick(View v) {
        // Clic sur OK : on récupère le message
        // conversation_edtMessage

        String msg = edtMsg.getText().toString();
        String qs="action=setMessage&idConv=" + idConv +"&contenu=" + msg;

        envoiRequete(qs,"posterMessage");

        edtMsg.setText("");
    }
}
