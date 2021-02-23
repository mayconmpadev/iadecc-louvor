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
import net.eletroseg.iadecclouvor.modelo.Usuario;

import java.util.ArrayList;
import java.util.List;

public class AdapterEscalados extends RecyclerView.Adapter<AdapterEscalados.ViewHolder> {

    private Context ctx;
    String pesquisa = "";
    //Itens de exibição / filtrados
    private List<Usuario> itens_exibicao;
    //Essa lista contem todos os itens.
    private List<Usuario> itens;
    private OnClickListener onClickListener = null;

    private SparseBooleanArray selected_items;
    private int current_selected_idx = -1;

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView nome, status;
        public ImageView imageView;
        public ProgressBar progressBar;
        public View lyt_parent;

        public ViewHolder(View view) {
            super(view);
            nome = (TextView) view.findViewById(R.id.text_integrante);
          //  status = (TextView) view.findViewById(R.id.status);
           // imageView = (ImageView) view.findViewById(R.id.image);
           // lyt_parent = (View) view.findViewById(R.id.lyt_parent);
        }
    }

    public AdapterEscalados(Context mContext, List<Usuario> items) {
        this.ctx = mContext;
        this.itens = items;
        this.itens_exibicao = items;
        selected_items = new SparseBooleanArray();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_integrantes_mes, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final Usuario usuario = itens_exibicao.get(position);

        // displaying text view data
        holder.nome.setText(usuario.nome.substring(0, 1).toUpperCase() + usuario.nome.substring(1));
      //  holder.status.setText(usuario.status);
      //  holder.lyt_parent.setActivated(selected_items.get(position, false));
       // Glide.with(ctx).load(usuario.foto).into(holder.imageView);
      //  Toast.makeText(ctx, "onBindViewHolder", Toast.LENGTH_SHORT).show();





    }

    public Usuario getItem(int position) {
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
        void onItemClick(View view, Usuario obj, int pos);

        void onItemLongClick(View view, Usuario obj, int pos);
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
                    List<Usuario> itens_filtrados = new ArrayList<Usuario>();

                    //percorre toda lista verificando se contem a palavra do filtro na descricao do objeto.
                    for (int i = 0; i < itens.size(); i++) {
                        Usuario usuario = itens.get(i);

                        filtro = filtro.toString().toLowerCase();
                        String condicao = usuario.nome.toLowerCase();


                        if (condicao.contains(filtro)) {
                            //se conter adiciona na lista de itens filtrados.
                            itens_filtrados.add(usuario);
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
                itens_exibicao = (List<Usuario>) results.values; // Valores filtrados.
                notifyDataSetChanged();  // Notifica a lista de alteração
            }

        };
        return filter;
    }


}