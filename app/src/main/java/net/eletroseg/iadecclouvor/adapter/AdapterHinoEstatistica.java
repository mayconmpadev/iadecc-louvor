package net.eletroseg.iadecclouvor.adapter;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import net.eletroseg.iadecclouvor.R;
import net.eletroseg.iadecclouvor.modelo.FiltroCron;
import net.eletroseg.iadecclouvor.modelo.Hino;
import net.eletroseg.iadecclouvor.util.Constantes;
import net.eletroseg.iadecclouvor.util.InstanciaFirebase;
import net.eletroseg.iadecclouvor.util.Util;

import java.util.ArrayList;
import java.util.List;

public class AdapterHinoEstatistica extends RecyclerView.Adapter<AdapterHinoEstatistica.ViewHolder> {


    private Context ctx;
    String pesquisa = "";
    //Itens de exibição / filtrados
    ArrayList<String> arrayList = new ArrayList<>();
    //Essa lista contem todos os itens.
    private List<FiltroCron> itens;
    private OnClickListener onClickListener = null;

    private SparseBooleanArray selected_items;
    private int current_selected_idx = -1;

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imagem;
        public TextView nome, cantor;
        public ProgressBar progressBar;
        public View lyt_parent;

        public ViewHolder(View view) {
            super(view);
            imagem = (ImageView) view.findViewById(R.id.image_ministrante_estatistica);
            nome = (TextView) view.findViewById(R.id.text_nome);
            cantor = (TextView) view.findViewById(R.id.text_cantor);
            lyt_parent = (View) view.findViewById(R.id.lyt_parent_hino);
        }
    }

    public AdapterHinoEstatistica(Context mContext, List<FiltroCron> items, OnClickListener onClickListener) {
        this.ctx = mContext;
        this.itens = items;
        this.onClickListener = onClickListener;

        selected_items = new SparseBooleanArray();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_lista_ministrante, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final FiltroCron filtroCron = itens.get(position);
        FirebaseDatabase database = InstanciaFirebase.getDatabase();
        DatabaseReference reference = database.getReference().child(Constantes.HINO).child(filtroCron.id);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    Hino hino = snapshot.getValue(Hino.class);
                    holder.nome.setText(hino.nome.substring(0, 1).toUpperCase() + hino.nome.substring(1));
                    Util.textoNegrito(filtroCron.numero, holder.cantor, null);
                  //  holder.cantor.setText(filtroCron.numero);
                    //Glide.with(ctx).load(usuario.foto).into(holder.imagem);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




        holder.lyt_parent.setActivated(selected_items.get(position, false));
        // Glide.with(ctx).load(hino.foto).into(holder.imageView);
        //  Toast.makeText(ctx, "onBindViewHolder", Toast.LENGTH_SHORT).show();


        holder.lyt_parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onClickListener == null) return;
                // Toast.makeText(ctx, "onClickListener", Toast.LENGTH_SHORT).show();
                onClickListener.onItemClick(v, filtroCron, position);

            }
        });

        holder.lyt_parent.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (onClickListener == null) return false;
                onClickListener.onItemLongClick(v, filtroCron, position);
                return true;
            }
        });

    }

    public FiltroCron getItem(int position) {
        return itens.get(position);
    }


    @Override
    public int getItemCount() {
        return itens.size();
    }

    public void toggleSelection(int pos) {
        current_selected_idx = pos;
        if (selected_items.get(pos, false)) {
            selected_items.delete(pos);
        } else {
            selected_items.put(pos, true);
        }
        notifyItemChanged(pos);
    }

    public int getSelectedItemCount() {
        return selected_items.size();
    }

    public List<Integer> getSelectedItems() {
        List<Integer> items = new ArrayList<>(selected_items.size());
        for (int i = 0; i < selected_items.size(); i++) {
            items.add(selected_items.keyAt(i));
        }
        return items;
    }

    public void removeData(int position) {
        itens.remove(position);
        resetCurrentIndex();
    }

    private void resetCurrentIndex() {
        current_selected_idx = -1;
    }

    public interface OnClickListener {
        void onItemClick(View view, FiltroCron obj, int pos);

        void onItemLongClick(View view, FiltroCron obj, int pos);
    }

}