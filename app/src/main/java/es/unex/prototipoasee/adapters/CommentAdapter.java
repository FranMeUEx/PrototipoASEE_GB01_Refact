package es.unex.prototipoasee.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import es.unex.prototipoasee.R;
import es.unex.prototipoasee.model.Comments;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.commentAdapterViewHolder> {

    private List<Comments> commentList;
    private final DeleteCommentInterface deleteCommentInterface;
    String userLogged;

    public CommentAdapter(List<Comments> list, String userLogged, Context context) {
        this.commentList = list;
        this.userLogged = userLogged;
        deleteCommentInterface = (DeleteCommentInterface) context;
    }

    @NonNull
    @Override
    public commentAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.social_item_list_content, parent,false);
        return new commentAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull commentAdapterViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.user.setText(commentList.get(position).getUsername());
        holder.text.setText(commentList.get(position).getText());
        if (commentList.get(position).getUsername().equals(userLogged)){
            holder.deleteCommentButton.setOnClickListener(view -> deleteCommentInterface.deleteComment(commentList.get(position)));
        } else {
            holder.deleteCommentButton.setEnabled(false);
            holder.deleteCommentButton.setVisibility(View.INVISIBLE);
        }
        //para cada comentario cuyo usuario corresponda con el que ha iniciado sesión, se debe habilitar el botón deleteCommentButton
        //en caso contrario se deshabilita
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    public static class commentAdapterViewHolder extends RecyclerView.ViewHolder {
        final TextView user;
        final TextView text;
        final ImageButton deleteCommentButton;

        commentAdapterViewHolder(View view) {
            super(view);
            user = view.findViewById(R.id.tvUsernameComment);
            text = view.findViewById(R.id.tvCommentText);
            deleteCommentButton = view.findViewById(R.id.ibCommentDelete);
        }
    }

    public void swap(List<Comments> commentList){
        this.commentList = commentList;
        notifyDataSetChanged();
    }

    public interface DeleteCommentInterface {
        void deleteComment(Comments comment);
    }
}

