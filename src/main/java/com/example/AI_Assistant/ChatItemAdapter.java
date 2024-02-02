package com.example.AI_Assistant;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ChatItemAdapter extends RecyclerView.Adapter<ChatItemAdapter.ChatItemViewHolder> {
    private List<ChatMessage> data;

    public ChatItemAdapter(List<ChatMessage> data){
        this.data=data;
    }

    @NonNull
    @Override
    public ChatItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

        View chatView = layoutInflater.inflate(R.layout.rv_item,null);

        ChatItemViewHolder chatItemViewHolder = new ChatItemViewHolder(chatView);
        
        chatItemViewHolder.setIsRecyclable(false);

        return chatItemViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ChatItemViewHolder holder, int position) {
        ChatMessage chatMessage=data.get(position);
        if (chatMessage.getSendType()==chatMessage.CHATGPT_SEND){
            holder.tv_item_chatgpt.setText(chatMessage.getContent());
            holder.tv_item_me.setVisibility(View.GONE);
            holder.tv_item_me_border.setVisibility(View.GONE);

            holder.tv_item_me_header.setVisibility(View.GONE);
            holder.tv_item_me_outer_border.setVisibility(View.GONE);
        } else{
            holder.tv_item_me.setText(chatMessage.getContent());
            holder.tv_item_chatgpt.setVisibility(View.GONE);
            holder.tv_item_chatgpt_border.setVisibility(View.GONE);

            holder.tv_item_chatgpt_header.setVisibility(View.GONE);
            holder.tv_item_chatgpt_outer_border.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ChatItemViewHolder extends RecyclerView.ViewHolder{

        private TextView tv_item_chatgpt;
        private TextView tv_item_me;

        private RelativeLayout tv_item_chatgpt_border;

        private RelativeLayout tv_item_me_border;

        private RelativeLayout tv_item_chatgpt_outer_border;

        private RelativeLayout tv_item_me_outer_border;

        private TextView tv_item_chatgpt_header;
        private TextView tv_item_me_header;
        public ChatItemViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_item_chatgpt = itemView.findViewById(R.id.tv_item_chatgpt);
            tv_item_me = itemView.findViewById(R.id.tv_item_me);

            tv_item_chatgpt_header = itemView.findViewById(R.id.tv_item_chatgpt_header);
            tv_item_me_header = itemView.findViewById(R.id.tv_item_me_header);

            tv_item_chatgpt_outer_border = itemView.findViewById(R.id.tv_item_chatgpt_outer_border);

            tv_item_me_outer_border = itemView.findViewById(R.id.tv_item_me_outer_border);

            tv_item_chatgpt_border = itemView.findViewById(R.id.tv_item_chatgpt_border);

            tv_item_me_border = itemView.findViewById(R.id.tv_item_me_border);
        }
    }
}
