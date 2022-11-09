package com.uet.fwork.chat;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.uet.fwork.R;
import com.uet.fwork.database.model.UserModel;
import com.uet.fwork.database.model.chat.ChanelModel;
import com.uet.fwork.database.repository.ChatRepository;
import com.uet.fwork.database.repository.Repository;
import com.uet.fwork.database.repository.UserRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatListRecyclerViewAdapter extends RecyclerView.Adapter<ChatListRecyclerViewAdapter.ViewHolder> {

    private List<String> chanelIdList;
    private Map<String, UserModel> userMap = new HashMap<>();
    private Map<String, Bitmap> userAvatarMap = new HashMap<>();
    private Context context;

    private String currentUserId;
    private UserRepository userRepository;
    private ChatRepository chatRepository;

    private OnItemClickListener listener = null;

    public ChatListRecyclerViewAdapter(Context context, List<String> chanelIdList,
                                       String currentUserId, FirebaseDatabase firebaseDatabase,
                                       @Nullable OnItemClickListener listener) {
        this.context = context;
        this.chanelIdList = chanelIdList;
        this.currentUserId = currentUserId;
        this.userRepository = new UserRepository(firebaseDatabase);
        this.chatRepository = new ChatRepository(firebaseDatabase);
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.item_recyclerview_chat_list, parent, false);
        return new ViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Target target;
        chatRepository.getChatChanelById(chanelIdList.get(position), new Repository.OnQuerySuccessListener<ChanelModel>() {
            @Override
            public void onSuccess(ChanelModel chanel) {
                chanel.getMembers().forEach((key, value) -> {
                    if (!key.equals(currentUserId)) {
                        if (!userMap.containsKey(key)) {
                            System.out.println("RECREATE");
                            userRepository.getUserByUID(key, user -> {
                                userMap.put(key, user);
                                if (user.getAvatar() != null && !user.getAvatar().equals("")) {
                                    Picasso.get().load(user.getAvatar())
                                            .resize(100, 100)
                                            .into(new Target() {
                                                @Override
                                                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                                    holder.imgAvatar.setImageBitmap(bitmap);
                                                    userAvatarMap.put(key, bitmap);
                                                }

                                                @Override
                                                public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                                                }

                                                @Override
                                                public void onPrepareLoad(Drawable placeHolderDrawable) {

                                                }
                                            });
                                }
                                holder.userName.setText(user.getFullName());
                            });
                        } else {
                            holder.userName.setText(userMap.get(key).getFullName());
                            if (userAvatarMap.containsKey(key)) {
                                holder.imgAvatar.setImageBitmap(userAvatarMap.get(key));
                            }
                        }
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return chanelIdList.size();
    }

    public void updateChanelList(List<String> chanelIdList) {
        this.chanelIdList.clear();
        this.chanelIdList.addAll(chanelIdList);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private CircleImageView imgAvatar;
        private TextView userName;

        public ViewHolder (@NonNull View view, @Nullable OnItemClickListener listener) {
            super(view);
            imgAvatar = view.findViewById(R.id.imgAvatar);
            userName = view.findViewById(R.id.txtFullName);
            if (listener != null) {
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        chatRepository.getChatChanelById(chanelIdList.get(getAdapterPosition()),
                                new Repository.OnQuerySuccessListener<ChanelModel>() {
                            @Override
                            public void onSuccess(ChanelModel result) {
                                listener.onClick(result);
                            }
                        });

                    }
                });
            }
        }
    }

    public interface OnItemClickListener {
        void onClick(ChanelModel chanel);
    }
}