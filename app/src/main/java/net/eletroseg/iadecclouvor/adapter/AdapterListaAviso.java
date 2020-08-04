package net.eletroseg.iadecclouvor.adapter;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import net.eletroseg.iadecclouvor.R;
import net.eletroseg.iadecclouvor.activity.SelecaoUsuarioActivity;
import net.eletroseg.iadecclouvor.modelo.Avisos;
import net.eletroseg.iadecclouvor.util.Timestamp;
import net.eletroseg.iadecclouvor.util.Util;


import java.util.ArrayList;
import java.util.List;

public class AdapterListaAviso extends RecyclerView.Adapter<AdapterListaAviso.ViewHolder> {

    private Context ctx;
    String pesquisa = "";
    //Itens de exibição / filtrados
    private List<Avisos> itens_exibicao;
    //Essa lista contem todos os itens.
    private List<Avisos> itens;
    private OnClickListener onClickListener = null;

    private SparseBooleanArray selected_items;
    private int current_selected_idx = -1;

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView titulo, corpo, data;
        public LinearLayout lyt_parent;



        public ViewHolder(View view) {
            super(view);
            titulo = (TextView) view.findViewById(R.id.text_titulo_item_lista_aviso);
            corpo = (TextView) view.findViewById(R.id.text_corpo_item_lista_aviso);
            data = (TextView) view.findViewById(R.id.text_data_item_lista_aviso);
            lyt_parent = (LinearLayout) view.findViewById(R.id.linear_root_item_lista_aviso);

        }
    }

    public AdapterListaAviso(Context mContext, List<Avisos> items) {
        this.ctx = mContext;
        this.itens = items;
        this.itens_exibicao = items;
        selected_items = new SparseBooleanArray();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card_lista_aviso, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final Avisos avisos = itens_exibicao.get(position);

        // displaying text view data

        holder.titulo.setText(avisos.titulo);
       Util.textoNegrito(avisos.corpo,holder.corpo, null);
        holder.data.setText(Timestamp.getFormatedDateTime(Long.parseLong(avisos.data), "dd/MM/yyyy"));






        holder.lyt_parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onClickListener == null) return ;
               // Toast.makeText(ctx, "onClickListener", Toast.LENGTH_SHORT).show();
                onClickListener.onItemClick(v, avisos, position);

            }
        });

        holder.lyt_parent.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (onClickListener == null) return false;
                onClickListener.onItemLongClick(v, avisos, position);
                return true;
            }
        });

    }

    public Avisos getItem(int position) {
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
        void onItemClick(View view, Avisos obj, int pos);

        void onItemLongClick(View view, Avisos obj, int pos);
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
                    List<Avisos> itens_filtrados = new ArrayList<Avisos>();

                    //percorre toda lista verificando se contem a palavra do filtro na descricao do objeto.
                    for (int i = 0; i < itens.size(); i++) {
                        Avisos avisos = itens.get(i);

                        filtro = filtro.toString().toLowerCase();
                        String condicao = avisos.titulo.toLowerCase();


                        if (condicao.contains(filtro)) {
                            //se conter adiciona na lista de itens filtrados.
                            itens_filtrados.add(avisos);
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
                itens_exibicao = (List<Avisos>) results.values; // Valores filtrados.
                notifyDataSetChanged();  // Notifica a lista de alteração
            }

        };
        return filter;
    }


}