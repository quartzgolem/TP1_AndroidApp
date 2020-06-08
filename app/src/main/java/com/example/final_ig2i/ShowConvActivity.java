package com.example.final_ig2i;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ShowConvActivity extends RestActivity implements View.OnClickListener {

    private int idConv;
    private int idLastMessage = 0;

    private ScrollView msgScroll;
    private LinearLayout msgLayout;
    private EditText edtMsg;
    private String username;
    private String prevAuteur = null;

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

                if (idLastMessage != 0)
                    msgLayout.removeViewAt(msgLayout.getChildCount()-1);

                // parcours des messages
                JSONArray messages = o.getJSONArray("messages");
                int i;
                int last = idLastMessage;
                for(i=0;i<messages.length();i++) {
                    JSONObject msg = (JSONObject) messages.get(i);
                    String contenu =  msg.getString("contenu");
                    String auteur =  msg.getString("auteur");
                    String couleur =  msg.getString("couleur");
                    String nextAuteur = i == messages.length() - 1 ?
                            null : ((JSONObject) messages.get(i+1)).getString("auteur") ;

                    LayoutInflater inflater = getLayoutInflater();
                    View item = inflater.inflate(R.layout.message_item, null, false);

                    TextView content = item.findViewById(R.id.message_content);
                    content.setText(contenu);

                    GradientDrawable back = new GradientDrawable();
                    back.setStroke(6,Color.parseColor(couleur));

                    if(auteur.equals(prevAuteur) && auteur.equals(nextAuteur)) {
                        item.setPadding(0, 0, 0, 5);
                       back.setCornerRadii(new float[]{200,200,40,40,40,40,200,200});
                    } else if (auteur.equals(nextAuteur)) {
                        item.setPadding(0, 0, 0, 5);
                       back.setCornerRadii(new float[]{200,200,200,200,40,40,200,200});
                    } else if (auteur.equals(prevAuteur))
                       back.setCornerRadii(new float[]{200,200,40,40,200,200,200,200});
                    else back.setCornerRadius(200);


                    TextView author = item.findViewById(R.id.message_author);
                    View pad;
                    RelativeLayout layout = item.findViewById(R.id.message_layout);

                    if (auteur.equals(username)) {
                        ((ViewGroup) author.getParent()).removeView(author);

                        back.setColor(Color.parseColor(couleur));
                        content.setTextColor(getResources().getColor(R.color.white));
                        layout.setGravity(Gravity.END);

                        pad = item.findViewById(R.id.message_rPadding);
                    } else {
                        author.setText(auteur);
                        author.setTextColor(Color.parseColor(couleur));
                        pad = item.findViewById(R.id.message_lPadding);
                    }

                    ((ViewGroup) pad.getParent()).removeView(pad);
                    content.setBackground(back);

                    msgLayout.addView(item);

                    if( i != messages.length() - 1 )
                        prevAuteur = auteur;
                    if( i == messages.length() - 2 )
                        last = msg.getInt("id");
                }

                if (i > 1)
                    msgScroll.post(new Runnable() {
                        @Override
                        public void run() {
                            msgScroll.fullScroll(View.FOCUS_DOWN);
                        }
                    });

                // mise à jour du numéro du dernier message
                idLastMessage = last;
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
        username = bdl.getString("username");
        String theme = bdl.getString("themeConversation");

        gs.alerter(Integer.toString(idConv));

        // On récupère la liste des messages périodiquement
        // action=getMessages&idConv=<ID>

        // NB : l'API fournit une route
        // qui permet d'indiquer le dernier message dont on dispose
        // action=getMessages&idConv=<ID>&idLastMessage=<NUMERO>

        gs.alerter("qsgsfg");
        requetePeriodique(10, "chargement_messages");
        msgLayout = findViewById(R.id.conversation_LayoutMessages);
        msgScroll = findViewById(R.id.conversation_svMessages);

        findViewById(R.id.conversation_btnOK).setOnClickListener(this);

        TextView titre = findViewById(R.id.conversation_titre);
        titre.setText(theme);

        edtMsg = findViewById(R.id.conversation_edtMessage);

        edtMsg.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    sendMessage();
                    handled = true;
                }
                return handled;
            }
        });
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
        sendMessage();
    }

    private void sendMessage() {
        // Clic sur OK : on récupère le message
        // conversation_edtMessage

        String msg = edtMsg.getText().toString();
        String qs = "action=setMessage&idConv=" + idConv + "&contenu=" + msg;
        envoiRequete(qs,"posterMessage");

        qs = "action=getMessages&idConv=" + idConv + "&idLastMessage=" + idLastMessage;
        envoiRequete(qs,"chargement_messages");

        edtMsg.setText("");
    }
}
