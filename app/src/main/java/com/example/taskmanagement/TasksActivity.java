package com.example.taskmanagement;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.LinkedList;

import dao.TacheDao;
import model.Tache;

public class TasksActivity extends AppCompatActivity implements View.OnClickListener {

    FirebaseFirestore db;

    RecyclerView myRecycler;
    private FirebaseAuth mAuth;

    FloatingActionButton add;

    FirebaseUser user;
    LinkedList<Tache> taches;

    ProgressDialog progdiag;
    EditText search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        taches= new LinkedList<Tache>();
        user = mAuth.getCurrentUser();


        myRecycler=findViewById(R.id.recycler_tasks);
        add=findViewById(R.id.fab_add);
        add.setOnClickListener(this);
        progdiag=new ProgressDialog(this);
        search=(EditText) findViewById(R.id.search);

        search.addTextChangedListener(new TextWatcher() {
          @Override
          public void afterTextChanged(Editable s) {

          }
          @Override
          public void beforeTextChanged(CharSequence s, int start, int count, int after){

          }
          @Override
          public void onTextChanged(CharSequence s, int start, int before, int count) {
              LinkedList<Tache> filtres= new LinkedList<Tache> ();
              for (Tache tache:taches){
                    if (tache.getTitle().contains(s) || tache.getDescription().contains(s)){
                        filtres.add(tache);
                    }
              MyAdapter myAdapter = new MyAdapter(filtres,TasksActivity.this);
              myRecycler.setAdapter(myAdapter);
              }
          }
      }
      );



        //edittext beforetextchanged aftertextchanged ontextchanged
        // on exrivant qlq chose sur edittext, on doit mettre a jour le linkedlist et par la suite mettre a jour
        //l'adapter et le recyclerviex'
       // MyAdapter myAdapter = new MyAdapter(taches,TasksActivity.this);
        //myRecycler.setAdapter(myAdapter);
        getTasks();

    }

    protected void  onStart(){
        super.onStart();

    }
    @Override
    protected void onResume() {
        super.onResume();


    }

    void getTasks(){
        new AsyncTask() {

            //exécuter des tâches avant le démarrage du thread
            //on a encore le droit d’accéder au thread principal du Gui
            protected void onPreExecute(){
                showDialog();
            }

            protected Object doInBackground(Object[] objects) {
                TacheDao dao= new TacheDao(user,db);
                taches.addAll(dao.getAllTaches());
                return null;
            }

            //exécuter des tàches pendant la réalisation de la tâche principale du thread
            //on a encore le droit d’accéder au thread principal du Gui
            protected void onProgressUpdate(Integer... progress) {}

            protected void onPostExecute(Object result) {
                myRecycler.setHasFixedSize(true);
                // use a linear layout manager
                LinearLayoutManager layoutManager = new LinearLayoutManager(TasksActivity.this);
                myRecycler.setLayoutManager(layoutManager);
                // specify an adapter (see also next example)
                MyAdapter myAdapter = new MyAdapter(taches,TasksActivity.this);
                myRecycler.setAdapter(myAdapter);
                hideDialog();
            }
        }.execute();

    }

    @Override
    public void onClick(View view) {
        if (view.getId()==R.id.fab_add){
            Intent MyIntent= new Intent(this, AddTaskActivity.class);
            startActivity(MyIntent);
        }
    }

    void showDialog(){
        progdiag= new ProgressDialog(this);
        progdiag.setMessage("Veuillez patienter, les donnees sont en cours de chargement ... ");
        progdiag.setIndeterminate(true);

        progdiag.show();
    }

    void hideDialog(){
        progdiag.dismiss();
    }



}




