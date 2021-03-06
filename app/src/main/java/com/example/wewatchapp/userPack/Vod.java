package com.example.wewatchapp.userPack;

import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Pair;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.example.wewatchapp.Adapter.MoviesShowAdapter;
import com.example.wewatchapp.Adapter.SliderPagerAdapterNew;
import com.example.wewatchapp.Model.GetVideoDetails;
import com.example.wewatchapp.Model.MovieItemClickListenerNew;
import com.example.wewatchapp.Model.SliderSide;
import com.example.wewatchapp.R;
import com.example.wewatchapp.utilitiesPack.MovieCounterView;
import com.example.wewatchapp.utilitiesPack.MovieDetailNewActivity;
import com.example.wewatchapp.utilitiesPack.Views;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class Vod extends AppCompatActivity implements MovieItemClickListenerNew, View.OnClickListener {

    MoviesShowAdapter moviesShowAdapter;
    DatabaseReference mDatabaserefence ;
    private List<GetVideoDetails> uploads, uploadsListlatest,uploadsListpopular;
    private List<GetVideoDetails> actionmovies, mymovies,comedymovies,romanticmovies,advanturemovies;

    private ViewPager sliderpager;
    private List<SliderSide> uploadsslider ;

    private TabLayout indicator,tabActionMovies;
    private RecyclerView MoviesRV ,moviesRvWeek ,tab;

    private TextView SearchBar;
    private EditText TitleEditText;
    private String MovieTitle;
    private ImageView MovieCoverImg;
    private FirebaseUser user;
    private DatabaseReference reference
            = FirebaseDatabase.getInstance().getReference("Users");;
    DatabaseReference counterViewMoviesRef;
    DatabaseReference ViewsTableRef;

    ProgressDialog progressDialog;

    String userName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vod);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar);


        TitleEditText  = (EditText) findViewById(R.id.search);
        MovieTitle = TitleEditText    .getText().toString().trim();
        SearchBar = (TextView) findViewById(R.id.search);
        SearchBar.setOnClickListener(this);
        user = FirebaseAuth.getInstance().getCurrentUser();


        counterViewMoviesRef = FirebaseDatabase.getInstance().getReference().child("movie_counter");
        ViewsTableRef = FirebaseDatabase.getInstance().getReference().child("Views");




        ///////////////////////////////////////////

        reference.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userProfile    = snapshot.getValue(User.class);
                if(  userProfile   != null){
                    userName = userProfile.getFullName(); } }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });


        /////////////////////////////////////////////


        progressDialog = new ProgressDialog(this);
        iniViews();
        addAllMovies();
//        iniPopularMovies();
        iniWeekMovies();
        movieviewtab();
        // getActionMovies();
        askPermission();
        //searchMovie();

    }


    private void addAllMovies(){
        uploads = new ArrayList<>();
        uploadsListlatest = new ArrayList<>();
        uploadsListpopular = new ArrayList<>();
        actionmovies = new ArrayList<>();
        mymovies = new ArrayList<>();
        uploadsslider = new ArrayList<>();
        advanturemovies = new ArrayList<>();
        comedymovies = new ArrayList<>();
        romanticmovies = new ArrayList<>();


        mDatabaserefence = FirebaseDatabase.getInstance().getReference("videos");
        progressDialog.setMessage("loading....");
        progressDialog.show();

        mDatabaserefence.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int i = 0;
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    GetVideoDetails upload = postSnapshot.getValue(GetVideoDetails.class);
                    SliderSide slide = postSnapshot.getValue(SliderSide.class);
                    if(upload.getVideo_type().equals("latest movies")){
                        uploadsListlatest.add(upload);

                    }else if(upload.getVideo_type().equals("Best popular movies"))
                    {
                        uploadsListpopular.add(upload);
                    }
                    if(upload.getVideo_category().equals("Action")){
                        actionmovies.add(upload);
                    }else if(upload.getVideo_category().equals("Sports")){
                        mymovies.add(upload);
                    }if(upload.getVideo_category().equals("Adventure")){
                        advanturemovies.add(upload);
                    } else if(upload.getVideo_category().equals("Comedy")){
                        comedymovies.add(upload);
                    }
                    if(upload.getVideo_category().equals("Romantic")){
                        romanticmovies.add(upload);
                    }


                    if(upload.getVideo_slide().equals("Slide movies")){
                        uploadsslider.add(slide);
                    }
                    if(i++ <4 ) {
                        uploadsListlatest.add(upload);
                    }
                    uploads.add(upload);

                }
                System.out.println("upload size in func"+ uploads.size());
                iniSlider();
                getMostPopular();
                progressDialog.dismiss();

            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                progressDialog.dismiss();
            }
        });

    }

    private void iniWeekMovies() {

        moviesShowAdapter = new MoviesShowAdapter(this, uploadsListlatest,this);
        moviesRvWeek.setAdapter(moviesShowAdapter);
        moviesRvWeek.setLayoutManager(new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.HORIZONTAL,false));
        moviesShowAdapter.notifyDataSetChanged();

    }
    private void iniPopularMovies() {

        System.out.println("recieving... afterProccessing uploadListPopular list : \n");

        for (GetVideoDetails VD : uploadsListpopular) {
            System.out.println(VD.video_name);
        }
        moviesShowAdapter = new MoviesShowAdapter(this, uploadsListpopular,this);
        //adding adapter to recyclerview
        MoviesRV.setAdapter(moviesShowAdapter);
        MoviesRV.setLayoutManager(new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.HORIZONTAL,false));
        moviesShowAdapter.notifyDataSetChanged();
    }
    int max;
    private void getMostPopular() {
        ArrayList<Pair<String , Integer>> movieNames = new ArrayList<>();
        int totalMoviesSize = uploads.size();
        System.out.println("number of uploads size : "+totalMoviesSize);
        max = Math.min(4, totalMoviesSize);
        System.out.println("number of max : "+totalMoviesSize);

        counterViewMoviesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot child : snapshot.getChildren()){
                    MovieCounterView MCV = child.getValue(MovieCounterView.class);
                    Pair<String, Integer> pair = Pair.create(MCV.getMovie_name(), MCV.getCounter());
                    movieNames.add(pair);
                }
                sendmovieNames(movieNames);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void sendmovieNames(ArrayList<Pair<String, Integer>> movieNames) {

        System.out.println("getting childrens from MoviecounterView \n");
        for (Pair p : movieNames){
            System.out.println("tuple: "+ p.first+", "+p.second );
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            System.out.println("sorting ... ");
            movieNames.sort((o1, o2) -> o2.second - o1.second);
            System.out.println("after sorting! ...  \n");
            for (Pair p : movieNames){
                System.out.println("tuple: "+ p.first+", "+p.second );
            }
        }

        System.out.println("Movie name size : " + movieNames.size() + "max : " + max);

        movieNames.subList(0, max);

        for (int i=0; i < movieNames.size() ; i++){
            for (int j =0; j < uploads.size() ; j ++) {
                System.out.println("uploads in :"+j+uploads.get(j).getVideo_name()+" movieNames: "+ i + movieNames.get(i).first);
                if (uploads.get(j).getVideo_name().equals(movieNames.get(i).first)){
                    System.out.println("match found");
                    uploadsListpopular.add(uploads.get(j));
                    j = uploads.size();
                }
            }
        }
        System.out.println("Sending... afterProccessing uploadListPopular list : \n");

        for (GetVideoDetails VD : uploadsListpopular) {
            System.out.println(VD.video_name);
        }
        iniPopularMovies();

    }

    private void movieviewtab() {
        getActionMovies();
        tabActionMovies.addTab(tabActionMovies.newTab().setText("Action"));
        tabActionMovies.addTab(tabActionMovies.newTab().setText("Advanture"));
        tabActionMovies.addTab(tabActionMovies.newTab().setText("Comedy"));
        tabActionMovies.addTab(tabActionMovies.newTab().setText("Romantic"));
        tabActionMovies.addTab(tabActionMovies.newTab().setText("My Movies"));                              /// change to my movies from sport
        tabActionMovies.setTabGravity(TabLayout.GRAVITY_FILL);
        tabActionMovies.setTabTextColors(ColorStateList.valueOf(Color.WHITE));

        tabActionMovies.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                switch (tab.getPosition()) {
                    case 0:
                        getActionMovies();
                        break;
                    case 1:
                        getAdvantureMovies();
                        break;
                    case 2:
                        getComedyMovies();
                        break;
                    case 3:
                        getRomanticMovies();
                        break;
                    case 4:
                        getMyMovies();
                        break;
                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });




    }

    private void iniSlider() {
        //uploadsslider = new ArrayList<>();
        SliderPagerAdapterNew adapterslider = new SliderPagerAdapterNew(this,uploadsslider);
        adapterslider.setUserID(userName);





        sliderpager.setAdapter(adapterslider);
        adapterslider.notifyDataSetChanged();
        // setup timer
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new SliderTimer(),4000,6000);
        indicator.setupWithViewPager(sliderpager,true);

    }



    private void iniViews() {
        tabActionMovies = findViewById(R.id.tabActionMovies);
        sliderpager = findViewById(R.id.slider_pager) ;
        indicator = findViewById(R.id.indicator);
        MoviesRV = findViewById(R.id.Rv_movies);
        moviesRvWeek = findViewById(R.id.rv_movies_week);
        tab = findViewById(R.id.tabrecyler);
    }


    @Override
    public void onMovieClick(GetVideoDetails movie, ImageView movieImageView) {

        Intent intent = new Intent(this, MovieDetailNewActivity.class);
        // send movie information to deatilActivity
        intent.putExtra("title",movie.getVideo_name());
        intent.putExtra("imgURL",movie.getVideo_thumb());
        intent.putExtra("imgCover",movie.getVideo_thumb());
        intent.putExtra("movieDetails",movie.getVideo_description());
        intent.putExtra("movieUrl",movie.getVideo_url());
        intent.putExtra("movieCategory",movie.getVideo_category());

        ActivityOptions options = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            options = ActivityOptions.makeSceneTransitionAnimation(Vod.this,
                    movieImageView,"sharedName");
        }

        startActivity(intent,options.toBundle());
    }

    @Override
    public void onClick(View view) {
        searchMovie();
        reference.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userProfile    = snapshot.getValue(User.class);
                if(  userProfile   != null){
                    userProfile.Logit("Searched for "+ MovieTitle);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public class SliderTimer extends TimerTask {


        @Override
        public void run() {

            Vod.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (sliderpager.getCurrentItem()<uploadsslider.size()-1) {
                        sliderpager.setCurrentItem(sliderpager.getCurrentItem()+1);
                    }
                    else
                        sliderpager.setCurrentItem(0);
                }
            });


        }
    }



    private void getActionMovies(){
        moviesShowAdapter = new MoviesShowAdapter(this, actionmovies,this);
        //adding adapter to recyclerview
        tab.setAdapter(moviesShowAdapter);
        tab.setLayoutManager(new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.HORIZONTAL,false));
        moviesShowAdapter.notifyDataSetChanged();
        Toast.makeText(Vod.this, "Tab Action Seleted:", Toast.LENGTH_SHORT).show();
    }

    private void getMyMovies(){
        mymovies = new ArrayList<>();
        Toast.makeText(Vod.this, "user name : "+ userName, Toast.LENGTH_LONG).show();
        // get user
        // Views userName , movie
        ArrayList<String> al = new ArrayList<String>();
        ViewsTableRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot child : snapshot.getChildren()){
                    Views views = child.getValue(Views.class);

                    /* check if username or the view (user name) are null */
                    if(userName != null && views.getUserName() != null && views.getUserName().equals(userName)){
                        if(!al.contains(views.getMovieName())){
                            al.add(views.getMovieName());
                        }
                    }
                }
                System.out.println("user : "+ userName + "watched :");
                for ( String s : al) {
                    //System.out.println("name of movie: " + s.toString());
                }
                // we have all the names of movies the user has been watched..
                // now go to another func and add to myMovies the movies from updates.
                addToMyMovies(al);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });


    }

    private void addToMyMovies(ArrayList<String> al) {
        for (int i=0; i < al.size() ; i++){
            for (int j =0; j < uploads.size() ; j ++) {
                System.out.println("uploads in :"+j+uploads.get(j).getVideo_name()+" movieNames: "+ i + al.get(i));
                if (uploads.get(j).getVideo_name().equals(al.get(i))){
                    System.out.println("match found");
                    mymovies.add(uploads.get(j));
                    j = uploads.size();
                }
            }
        }


        System.out.println("before display ---------------------------------------------------------\n" );
        int i = 0 ;
        for ( GetVideoDetails GVD : mymovies )
            System.out.println((i++) + GVD.video_name);



        moviesShowAdapter = new MoviesShowAdapter(this, mymovies,this);
        //adding adapter to recyclerview
        tab.setAdapter(moviesShowAdapter);
        tab.setLayoutManager(new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.HORIZONTAL,false));
        moviesShowAdapter.notifyDataSetChanged();
    }

    private void getAdvantureMovies(){
        moviesShowAdapter = new MoviesShowAdapter(this, advanturemovies,this);
        //adding adapter to recyclerview
        tab.setAdapter(moviesShowAdapter);
        tab.setLayoutManager(new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.HORIZONTAL,false));
        moviesShowAdapter.notifyDataSetChanged();

    }

    private void getRomanticMovies(){
        moviesShowAdapter = new MoviesShowAdapter(this, romanticmovies,this);
        //adding adapter to recyclerview
        tab.setAdapter(moviesShowAdapter);
        tab.setLayoutManager(new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.HORIZONTAL,false));
        moviesShowAdapter.notifyDataSetChanged();

    }

    private void getComedyMovies(){
        moviesShowAdapter = new MoviesShowAdapter(this, comedymovies,this);
        //adding adapter to recyclerview
        tab.setAdapter(moviesShowAdapter);
        tab.setLayoutManager(new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.HORIZONTAL,false));
        moviesShowAdapter.notifyDataSetChanged();

    }


    public void askPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, 2004);

        }
    }

    private void searchMovie(){
        MovieTitle = TitleEditText    .getText().toString().trim();
        if(MovieTitle.isEmpty()){
            TitleEditText.setError("Please provide a title");
            TitleEditText.requestFocus();
            return;
        }
        else {

            mDatabaserefence = FirebaseDatabase.getInstance().getReference("videos");
            mDatabaserefence.orderByChild("video_name").equalTo(MovieTitle).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChildren()) {
                        progressDialog.setMessage("Searching for "+ MovieTitle);
                        progressDialog.show();
                        for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                            String category = userSnapshot.child("video_category").getValue().toString().trim();
                            String description = userSnapshot.child("video_description").getValue().toString().trim();
                            String slide = userSnapshot.child(("video_slide")).getValue().toString().trim();
                            String thumb = userSnapshot.child(("video_thumb")).getValue().toString().trim();
                            String type = userSnapshot.child(("video_type")).getValue().toString().trim();
                            String URL = userSnapshot.child("video_url").getValue().toString().trim();

                            GetVideoDetails search_result = new GetVideoDetails(slide, type, thumb, URL, MovieTitle, description, category);
                            setContentView(R.layout.activity_movie_detail_new);
                            MovieCoverImg = (ImageView) findViewById(R.id.imageView);
                            onMovieClick(search_result, MovieCoverImg);
                            setContentView(R.layout.activity_vod);
                            progressDialog.dismiss();
                        }

                    }
                    else {
                        TitleEditText.setError("Movie not found, you will be able to request it soon");
                        //TODO: add here ability to request that movie
                        TitleEditText.requestFocus();
                        return;
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            //startActivity(new Intent(Vod.this, MovieDetailNewActivity.class))

        }


    }
}