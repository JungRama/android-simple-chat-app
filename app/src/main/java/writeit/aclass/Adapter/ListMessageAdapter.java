package writeit.aclass.Adapter;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import me.himanshusoni.chatmessageview.ChatMessageView;
import writeit.aclass.Chat;
import writeit.aclass.R;

/**
 * Created by Gung Rama on 12/10/2017.
 */

public class ListMessageAdapter extends RecyclerView.Adapter<ListMessageAdapter.MessageViewHolder> {

    private List<DataMessagesAdapter> MessageList;

    FirebaseUser user;


    public ListMessageAdapter(List<DataMessagesAdapter> MessageList) {
        this.MessageList = MessageList;

    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message_list, parent, false);

        return new MessageViewHolder(view);
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {
        public TextView tvMessages;
        public CircleImageView imgProfil;
        public ChatMessageView chatBG;

        public MessageViewHolder(View view) {
            super(view);

            tvMessages = (TextView) view.findViewById(R.id.tvMessages);
            imgProfil = (CircleImageView) view.findViewById(R.id.imgProfil);
            chatBG = (ChatMessageView) view.findViewById(R.id.chatBG);
        }
    }

    @Override
    public void onBindViewHolder(MessageViewHolder holder, int position) {

        DataMessagesAdapter data = MessageList.get(position);
        holder.tvMessages.setText(data.getMessage());

        user = FirebaseAuth.getInstance().getCurrentUser();
        String Uid = user.getUid();

        String from_user = data.getFrom();

        if (from_user.equals(Uid)) {
            RelativeLayout.LayoutParams params = new RelativeLayout.
                    LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

            holder.chatBG.setLayoutParams(params);

            holder.imgProfil.setVisibility(View.GONE);
            holder.chatBG.setArrowPosition(ChatMessageView.ArrowPosition.RIGHT);

        } else {

            RelativeLayout.LayoutParams paramsL = new RelativeLayout.
                    LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            paramsL.addRule(RelativeLayout.ALIGN_PARENT_LEFT);

            holder.chatBG.setLayoutParams(paramsL);
            holder.imgProfil.setVisibility(View.INVISIBLE);
            holder.chatBG.setArrowPosition(ChatMessageView.ArrowPosition.LEFT);

        }


    }

    @Override
    public int getItemCount() {
        return MessageList.size();
    }


}
