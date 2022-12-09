package com.uet.fwork.navbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;
import com.uet.fwork.R;
import com.uet.fwork.account.ChangePasswordActivity;
import com.uet.fwork.account.login.LoginActivity;
import com.uet.fwork.account.profile.ProfileFragment;
import com.uet.fwork.database.model.UserModel;
import com.uet.fwork.database.model.UserRole;
import com.uet.fwork.firebasehelper.FirebaseAuthHelper;
import com.uet.fwork.firebasehelper.FirebaseSignInMethod;
import com.uet.fwork.post.CandidateShowPostApplyFragment;
import com.uet.fwork.post.EmployerShowPostApplyFragment;
import com.uet.fwork.post.ShowMyPostFragment;
import com.uet.fwork.post.ShowPostLikeFragment;

import de.hdodenhof.circleimageview.CircleImageView;

public class EmployerDashboardFragment extends Fragment {

    private CircleImageView cirImgAvatar;
    private TextView txvFullName, txvEmail;
    private LinearLayout lltProfile, lltPost, lltInterest;
    private LinearLayout lltPostApply, lltChangePassword, lltSignOut;

    private ProfileFragment profileFragment;
    private ShowMyPostFragment showMyPostFragment;
    private ShowPostLikeFragment showPostLikeFragment;
    private EmployerShowPostApplyFragment showPostApplyFragment;

    private FirebaseAuthHelper firebaseAuthHelper;

    public EmployerDashboardFragment() {
        super(R.layout.fragment_employer_dashboard);
        firebaseAuthHelper = FirebaseAuthHelper.getInstance();

        profileFragment = new ProfileFragment();
        Bundle bundle = new Bundle();
        bundle.putString("USER_ROLE", UserRole.EMPLOYER);
        profileFragment.setArguments(bundle);
        showMyPostFragment = new ShowMyPostFragment();
        showPostLikeFragment = new ShowPostLikeFragment();
        showPostApplyFragment = new EmployerShowPostApplyFragment();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        cirImgAvatar = view.findViewById(R.id.imgAvatar);
        txvFullName = view.findViewById(R.id.txtFullName);
        txvEmail = view.findViewById(R.id.txtEmail);
        lltProfile = view.findViewById(R.id.relProfile);
        lltPost = view.findViewById(R.id.relPost);
        lltInterest = view.findViewById(R.id.relInterestPost);
        lltPostApply = view.findViewById(R.id.relApplyPost);
        lltChangePassword = view.findViewById(R.id.relChangePassword);
        lltSignOut = view.findViewById(R.id.relSignOut);

        String signInMethod = firebaseAuthHelper.getSignInMethod();
        if (!signInMethod.equals(FirebaseSignInMethod.PASSWORD)) {
            lltChangePassword.setVisibility(View.GONE);
        }
        UserModel userModel = firebaseAuthHelper.getUser();
        if (!userModel.getAvatar().equals("")) {
            Picasso.get().load(userModel.getAvatar()).into(cirImgAvatar);
        }
        txvFullName.setText(userModel.getFullName());
        txvEmail.setText(userModel.getEmail());

        lltProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                getActivity().getSupportFragmentManager()
//                        .beginTransaction()
//                        .addToBackStack("ProfileFragment")
//                        .replace(R.id.content, profileFragment)
//                        .commit();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                if (fragmentManager.findFragmentByTag("Profile Fragment") == null) {
                    getActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .add(R.id.content, profileFragment, "Profile Fragment")
                            .commit();
                }
                fragmentManager.beginTransaction()
                        .addToBackStack("Profile Fragment")
                        .show(profileFragment)
                        .commit();
            }
        });

        lltPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                getActivity().getSupportFragmentManager()
//                        .beginTransaction()
//                        .addToBackStack("PostListFragment")
//                        .replace(R.id.content, new ShowMyPostFragment())
//                        .commit();

                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                if (fragmentManager.findFragmentByTag("PostListFragment") == null) {
                    getActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .add(R.id.content, showMyPostFragment, "PostListFragment")
                            .commit();
                }
                fragmentManager.beginTransaction()
                        .addToBackStack("PostListFragment")
                        .show(showMyPostFragment)
                        .commit();
            }
        });

        lltInterest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                getActivity().getSupportFragmentManager()
//                        .beginTransaction()
//                        .addToBackStack("PostLikeFragment")
//                        .replace(R.id.content, new ShowPostLikeFragment())
//                        .commit();

                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                if (fragmentManager.findFragmentByTag("PostLikeFragment") == null) {
                    getActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .add(R.id.content, showPostLikeFragment, "PostLikeFragment")
                            .commit();
                }
                fragmentManager.beginTransaction()
                        .addToBackStack("PostLikeFragment")
                        .show(showPostLikeFragment)
                        .commit();
            }
        });

        lltPostApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                getActivity().getSupportFragmentManager()
//                        .beginTransaction()
//                        .addToBackStack("PostApplyFragment")
//                        .replace(R.id.content, new CandidateShowPostApplyFragment())
//                        .commit();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                if (fragmentManager.findFragmentByTag("PostApplyFragment") == null) {
                    getActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .add(R.id.content, showPostApplyFragment, "PostApplyFragment")
                            .commit();
                }
                fragmentManager.beginTransaction()
                        .addToBackStack("PostApplyFragment")
                        .show(showPostApplyFragment)
                        .commit();
            }
        });

        lltChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ChangePasswordActivity.class);
                startActivity(intent);
            }
        });

        lltSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuthHelper.signOut();
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                getActivity().finish();
            }
        });
    }
}