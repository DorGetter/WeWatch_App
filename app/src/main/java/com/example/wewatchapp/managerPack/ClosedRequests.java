package com.example.wewatchapp.managerPack;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wewatchapp.R;
import com.example.wewatchapp.userPack.Request;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ClosedRequests extends AppCompatActivity implements View.OnClickListener{

    /* buttons */
    private TextView back;
    private TextView open, open2, open3, open4, open5, open6;
    private TextView remove;

    /* firebase object */
    FirebaseDatabase database;
    /* firebase reference to the root */
    DatabaseReference rootRef;

    /* use to store requests from firebase */
    final Request[] requests = new Request[6];


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_closed_requests);

        back = findViewById(R.id.back);
        back.setOnClickListener(this);

        remove = findViewById(R.id.remove);
        remove.setOnClickListener(this);

        open = findViewById(R.id.open);
        open.setOnClickListener(this);

        open2 = findViewById(R.id.open2);
        open2.setOnClickListener(this);

        open3 = findViewById(R.id.open3);
        open3.setOnClickListener(this);

        open4 = findViewById(R.id.open4);
        open4.setOnClickListener(this);

        open5 = findViewById(R.id.open5);
        open5.setOnClickListener(this);

        open6 = findViewById(R.id.open6);
        open6.setOnClickListener(this);

        database = FirebaseDatabase.getInstance();
        rootRef = database.getReference();

        /* shows new requests from the database */
        final TextView requestTextView = (TextView) findViewById(R.id.textNewRequest);
        final TextView requestTextView2 = (TextView) findViewById(R.id.textNewRequest2);
        final TextView requestTextView3 = (TextView) findViewById(R.id.textNewRequest3);
        final TextView requestTextView4 = (TextView) findViewById(R.id.textNewRequest4);
        final TextView requestTextView5 = (TextView) findViewById(R.id.textNewRequest5);
        final TextView requestTextView6 = (TextView) findViewById(R.id.textNewRequest6);


        /* database requests listener */
        rootRef.child("Requests").addListenerForSingleValueEvent(new ValueEventListener() {

            public void onDataChange(@NonNull DataSnapshot snapshot) {

                /* use for debug */
                String requestList = "";

                /* to show each request by name in different text view */
                String requestLine = "closed request from: ";

                /* use for switch case to insert each request to correct number of text view */
                int text_view_num = 1;

                /* use for increment the 'text view num' only if the request is 'CLOSED' */
                boolean toINC = false;

                for (DataSnapshot child : snapshot.getChildren()) {

                    toINC = false;

                    /* use for debug */
                    System.out.println(child.toString());

                    /* store request data */
                    String requestKey = child.getKey();
                    Request request = child.getValue(Request.class);
                    String status = request.getStatus();
                    String name = request.getUserName();

                    /* use for debug */
                    requestList = requestList + "\n" + requestLine + name;



                    /* show only 'CLOSED' requests */
                    if(status.compareTo("CLOSED") == 0) {
                        requests[text_view_num - 1] = child.getValue(Request.class);
                        toINC = true;
                    }


                    switch(text_view_num){
                        case 1:
                            if(status.compareTo("OPEN") != 0)
                                requestTextView.setText(requestLine + "   " + name);
                            break;
                        case 2:
                            if(status.compareTo("OPEN") != 0)
                                requestTextView2.setText(requestLine + "   " + name);
                            break;
                        case 3:
                            if(status.compareTo("OPEN") != 0)
                                requestTextView3.setText(requestLine + "   " + name);
                            break;
                        case 4:
                            if(status.compareTo("OPEN") != 0)
                                requestTextView4.setText(requestLine + "   " + name);
                            break;
                        case 5:
                            if(status.compareTo("OPEN") != 0)
                                requestTextView5.setText(requestLine + "   " + name);
                            break;
                        case 6:
                            if(status.compareTo("OPEN") != 0)
                                requestTextView6.setText(requestLine + "   " + name);
                            break;
                    }

                    if(toINC)
                        text_view_num ++;


                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ClosedRequests.this,"Error!",Toast.LENGTH_LONG);
            }

        });

    }

    /* use to store the request that the manger want to work on */
    Request requestInWork;

    @Override
    public void onClick(View view) {

        switch(view.getId()) {

            case R.id.back:
                startActivity(new Intent(this, ProfileManager.class));
                break;

            case R.id.open:
                final TextView showTheRequest = (TextView) findViewById(R.id.showTheRequest);
                if(requests[0] != null) {
                    showTheRequest.setText("Request from:  " + requests[0].getUserName() + "\n"
                            + "Movie:  " + requests[0].getMovieName() + "\n"
                            + "This Request Is Closed");
                    requestInWork = requests[0];
                }
                break;

            case R.id.open2:
                final TextView showTheRequest2 = (TextView) findViewById(R.id.showTheRequest);
                if(requests[1] != null) {
                    showTheRequest2.setText("Request from:  " + requests[1].getUserName() + "\n"
                            + "Movie:  " + requests[1].getMovieName() + "\n"
                            + "This Request Is Closed");
                    requestInWork = requests[1];
                }
                break;

            case R.id.open3:
                final TextView showTheRequest3 = (TextView) findViewById(R.id.showTheRequest);
                if(requests[2] != null) {
                    showTheRequest3.setText("Request from:  " + requests[2].getUserName() + "\n"
                            + "Movie:  " + requests[2].getMovieName() + "\n"
                            + "This Request Is Closed");
                    requestInWork = requests[2];
                }
                break;

            case R.id.open4:
                final TextView showTheRequest4 = (TextView) findViewById(R.id.showTheRequest);
                if(requests[3] != null) {
                    showTheRequest4.setText("Request from:  " + requests[3].getUserName() + "\n"
                            + "Movie:  " + requests[3].getMovieName() + "\n"
                            + "This Request Is Closed");
                    requestInWork = requests[3];
                }
                break;

            case R.id.open5:
                final TextView showTheRequest5 = (TextView) findViewById(R.id.showTheRequest);
                if(requests[4] != null) {
                    showTheRequest5.setText("Request from:  " + requests[4].getUserName() + "\n"
                            + "Movie:  " + requests[4].getMovieName() + "\n"
                            + "This Request Is Closed");
                    requestInWork = requests[4];
                }
                break;

            case R.id.open6:
                final TextView showTheRequest6 = (TextView) findViewById(R.id.showTheRequest);
                if(requests[5] != null) {
                    showTheRequest6.setText("Request from:  " + requests[5].getUserName() + "\n"
                            + "Movie:  " + requests[5].getMovieName() + "\n"
                            + "This Request Is Closed");
                    requestInWork = requests[5];
                }
                break;

            case R.id.remove:
                final TextView showTheRequestClosed = (TextView) findViewById(R.id.showTheRequest);
                showTheRequestClosed.setText("Request from:  " + requestInWork.getUserName() + "\n"
                        + "For The Movie:  " + requestInWork.getMovieName() + "\n  Is Now Closed");
                String closedRequestID = requestInWork.getRequestID();
                rootRef.child("Requests").child(closedRequestID).removeValue();
                startActivity(new Intent(this, ClosedRequests.class));

                break;
        }
    }
}