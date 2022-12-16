package com.uet.fwork;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.uet.fwork.database.model.CandidateModel;
import com.uet.fwork.database.model.EmployerModel;
import com.uet.fwork.database.model.UserModel;
import com.uet.fwork.database.model.UserRole;
import com.uet.fwork.database.model.post.PostModel;
import com.uet.fwork.database.repository.Repository;
import com.uet.fwork.database.repository.UserRepository;
import com.uet.fwork.post.PostsAdapter;

import java.util.ArrayList;
import java.util.List;

public class ViewProfileActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    RecyclerView postsRecyclerView;

    private UserRepository userRepository;
    private RelativeLayout relUserProfile;

    List<PostModel> postModelList;
    PostsAdapter postsAdapter;
    String uid ="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_page);

        userRepository = UserRepository.getInstance();

        postsRecyclerView = findViewById(R.id.recyclerviewPosts);
        relUserProfile = (RelativeLayout) findViewById(R.id.relUserProfile);

        firebaseAuth = FirebaseAuth.getInstance();

        //get uid of the clicked user
        Intent intent = getIntent();
        uid = intent.getStringExtra("id");

        postModelList = new ArrayList<>();
        loadUserData();
        loadPosts();
    }

    private void loadUserData() {
        userRepository.getUserByUID(uid, userModel -> {
            if (userModel instanceof CandidateModel) {
                CandidateModel candidate = (CandidateModel) userModel;
                LayoutInflater layoutInflater = LayoutInflater.from(this);
                View view = layoutInflater.inflate(R.layout.fragment_profile_candidate, relUserProfile, true);
                TextView txvName = view.findViewById(R.id.nameTv);
                TextView txvBirth = view.findViewById(R.id.birthTv);
                TextView txvSex = view.findViewById(R.id.sexTv);
                TextView txvMajor = view.findViewById(R.id.jobTv);
                TextView txvYearOfExperience = view.findViewById(R.id.workYearTv);
                TextView txvPhone = view.findViewById(R.id.phoneTv);
                TextView txvEmail = view.findViewById(R.id.emailTv);
                ImageView imgAvatar = view.findViewById(R.id.avatarIv);
                txvName.setText(userModel.getFullName());
                txvEmail.setText(userModel.getContactEmail());
                txvPhone.setText(userModel.getPhoneNumber());
                txvBirth.setText(candidate.getDateOfBirth());
                txvSex.setText(candidate.getSex());
                txvMajor.setText(candidate.getMajor());
                txvYearOfExperience.setText(String.valueOf(candidate.getYearOfExperience()));
                String avatarImagePath = userModel.getAvatar();
                if (!avatarImagePath.isEmpty()) {
                    Picasso.get().load(avatarImagePath)
                            .placeholder(R.drawable.wlop_33se)
                            .into(imgAvatar);
                }
            } else if (userModel instanceof EmployerModel) {
                EmployerModel employer = (EmployerModel) userModel;
                LayoutInflater layoutInflater = LayoutInflater.from(this);
                View view = layoutInflater.inflate(R.layout.fragment_profile_employer, relUserProfile, true);
                TextView txvName = view.findViewById(R.id.nameTv);
                TextView txvPhone = view.findViewById(R.id.phoneTv);
                TextView txvEmail = view.findViewById(R.id.emailTv);
                TextView txvAddress = view.findViewById(R.id.txtAddress);
                TextView txvCompanyDescription = view.findViewById(R.id.txtDescription);
                ImageView imgAvatar = view.findViewById(R.id.avatarIv);

                txvName.setText(employer.getFullName());
                txvEmail.setText(employer.getContactEmail());
                txvPhone.setText(employer.getPhoneNumber());
                txvAddress.setText(employer.getAddress().toString());
                txvCompanyDescription.setText(getString(R.string.company_description, employer.getDescription()));
                String avatarImagePath = userModel.getAvatar();
                if (!avatarImagePath.isEmpty()) {
                    Picasso.get().load(avatarImagePath)
                            .placeholder(R.drawable.wlop_33se)
                            .into(imgAvatar);
                }
            }
        });
    }

    private void loadPosts() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        //show newest post first (load data from last)
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        //set this layout to recyclerview
        postsRecyclerView.setLayoutManager(layoutManager);

        //init post list
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("posts/list");
        Query query = databaseReference.orderByChild("userId").equalTo(uid);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postModelList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                   PostModel myPosts = ds.getValue(PostModel.class);

                   //add to list
                    postModelList.add(myPosts);

                    //adapter
                    postsAdapter = new PostsAdapter(ViewProfileActivity.this, postModelList);
                    postsRecyclerView.setAdapter(postsAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ViewProfileActivity.this, ""+error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }
}