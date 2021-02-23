package net.eletroseg.iadecclouvor.adapter;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import net.eletroseg.iadecclouvor.R;
import net.eletroseg.iadecclouvor.modelo.Avisos;
import net.eletroseg.iadecclouvor.modelo.EscalaMes;
import net.eletroseg.iadecclouvor.modelo.Usuario;
import net.eletroseg.iadecclouvor.util.Timestamp;
import net.eletroseg.iadecclouvor.util.Util;

import java.util.ArrayList;
import java.util.List;

public class AdapterEscalaMes extends RecyclerView.Adapter<AdapterEscalaMes.ViewHolder> {

    private Context ctx;
    String pesquisa = "";
    //Itens de exibição / filtrados
    private List<EscalaMes> itens_exibicao;
    //Essa lista contem todos os itens.
    private List<EscalaMes> itens;
    private OnClickListener onClickListener = null;
    private AdapterEscalados mAdapter;
    ArrayList<Usuario> arrayListHino = new ArrayList<>();
    private SparseBooleanArray selected_items;
    private int current_selected_idx = -1;

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView dia, ministrante, data;
        public RecyclerView recyclerInstrumento;


        public ViewHolder(View view) {
            super(view);
            dia = (TextView) view.findViewById(R.id.text_dia);
            ministrante = (TextView) view.findViewById(R.id.text_ministrante);
            recyclerInstrumento = (RecyclerView) view.findViewById(R.id.recycler_escala_mes_instrumentista);


        }
    }

    public AdapterEscalaMes(Context mContext, List<EscalaMes> items) {
        this.ctx = mContext;
        this.itens = items;
        this.itens_exibicao = items;
        selected_items = new SparseBooleanArray();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_escala_mes, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final EscalaMes avisos = itens_exibicao.get(position);

        // displaying text view data

        holder.dia.setText(avisos.diaDoCulto);
        holder.ministrante.setText(avisos.ministrante);


        holder.recyclerInstrumento.setLayoutManager(new GridLayoutManager(ctx,5));
        holder.recyclerInstrumento.setHasFixedSize(true);

        //set data and list adapter
        mAdapter = new AdapterEscalados(ctx, avisos.instrumental);
        holder.recyclerInstrumento.setAdapter(mAdapter);

    }

    public EscalaMes getItem(int position) {
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

    public void hinoBaixado(String hino) {


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
                        EscalaMes avisos = itens.get(i);


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
                itens_exibicao = (List<EscalaMes>) results.values; // Valores filtrados.
                notifyDataSetChanged();  // Notifica a lista de alteração
            }

        };
        return filter;
    }


}