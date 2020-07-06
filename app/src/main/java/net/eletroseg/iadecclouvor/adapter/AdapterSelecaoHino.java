package net.eletroseg.iadecclouvor.adapter;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import net.eletroseg.iadecclouvor.R;
import net.eletroseg.iadecclouvor.activity.SelecaoHinoActivity;
import net.eletroseg.iadecclouvor.activity.SelecaoUsuarioActivity;
import net.eletroseg.iadecclouvor.modelo.Hino;


import java.util.ArrayList;
import java.util.List;

public class AdapterSelecaoHino extends RecyclerView.Adapter<AdapterSelecaoHino.ViewHolder> {

    private Context ctx;
    String pesquisa = "";
    //Itens de exibição / filtrados
    private List<Hino> itens_exibicao;
    //Essa lista contem todos os itens.
    private List<Hino> itens;
    private OnClickListener onClickListener = null;

    private SparseBooleanArray selected_items;
    private int current_selected_idx = -1;

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView nome, cantor;
        public ProgressBar progressBar;
        public View lyt_parent;

        public ViewHolder(View view) {
            super(view);
            nome = (TextView) view.findViewById(R.id.text_nome);
            cantor = (TextView) view.findViewById(R.id.text_cantor);
            lyt_parent = (View) view.findViewById(R.id.lyt_parent_hino);
        }
    }

    public AdapterSelecaoHino(Context mContext, List<Hino> items) {
        this.ctx = mContext;
        this.itens = items;
        this.itens_exibicao = items;
        selected_items = new SparseBooleanArray();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_lista_selecao_hino, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final Hino hino = itens_exibicao.get(position);

        // displaying text view data
        if (SelecaoHinoActivity.arrayListIds.contains(hino.id)){
            holder.lyt_parent.setBackgroundResource(R.color.cinza);
        }else {
            holder.lyt_parent.setBackgroundResource(R.color.branco);
        }
        holder.nome.setText(hino.nome.substring(0, 1).toUpperCase() + hino.nome.substring(1));
        holder.cantor.setText(hino.cantor);
        holder.lyt_parent.setActivated(selected_items.get(position, false));
       // Glide.with(ctx).load(hino.foto).into(holder.imageView);
      //  Toast.makeText(ctx, "onBindViewHolder", Toast.LENGTH_SHORT).show();



        holder.lyt_parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onClickListener == null) return ;
               // Toast.makeText(ctx, "onClickListener", Toast.LENGTH_SHORT).show();
                onClickListener.onItemClick(v, hino, position);

            }
        });

        holder.lyt_parent.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (onClickListener == null) return false;
                onClickListener.onItemLongClick(v, hino, position);
                return true;
            }
        });

    }

    public Hino getItem(int position) {
        return itens.get(position);
    }


    @Override
    public int getItemCount() {
        return itens_exibicao.size();
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

    public void clearSelections() {
        selected_items.clear();
        notifyDataSetChanged();
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
        void onItemClick(View view, Hino obj, int pos);

        void onItemLongClick(View view, Hino obj, int pos);
    }

    public void hinoBaixado(String hino){


    }

    /**
     * Método responsável pelo filtro. Utilizaremos em um EditText
     *
     * @return
     */
    public Filter getFilter() {
        Filter filter = new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence filtro) {

                FilterResults results = new FilterResults();
                //se não foi realizado nenhum filtro insere todos os itens.
                if (filtro == null || filtro.length() == 0) {
                    results.count = itens.size();
                    results.values = itens;
                } else {
                    //cria um array para armazenar os objetos filtrados.
                    List<Hino> itens_filtrados = new ArrayList<Hino>();

                    //percorre toda lista verificando se contem a palavra do filtro na descricao do objeto.
                    for (int i = 0; i < itens.size(); i++) {
                        Hino hino = itens.get(i);

                        filtro = filtro.toString().toLowerCase();
                        String condicao = hino.nome.toLowerCase();


                        if (condicao.contains(filtro)) {
                            //se conter adiciona na lista de itens filtrados.
                            itens_filtrados.add(hino);
                            pesquisa = (String) filtro;
                        }

                    }
                    // Define o resultado do filtro na variavel FilterResults
                    results.count = itens_filtrados.size();
                    results.values = itens_filtrados;
                }
                return results;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                itens_exibicao = (List<Hino>) results.values; // Valores filtrados.
                notifyDataSetChanged();  // Notifica a lista de alteração
            }

        };
        return filter;
    }


}