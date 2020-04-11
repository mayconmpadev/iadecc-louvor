package net.eletroseg.iadecclouvor.adapter;


import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Filter;
import net.eletroseg.iadecclouvor.R;
import net.eletroseg.iadecclouvor.modelo.Hino;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by maycon on 28/02/2018.
 */

public class LetraAdapter extends BaseAdapter {

    String pesquisa = "";
    //Itens de exibição / filtrados
    private List<Hino> itens_exibicao;
    //Essa lista contem todos os itens.
    private List<Hino> itens;
    //Utilizado no getView para carregar e construir um item.
    private LayoutInflater layoutInflater;

    public LetraAdapter(Context context, List<Hino> itens) {
        this.itens = itens;
        this.itens_exibicao = itens;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return itens_exibicao.size();
    }

    @Override
    public Object getItem(int arg0) {
        return itens_exibicao.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public View getView(int arg0, View arg1, ViewGroup arg2) {
        ItemHelper itemHelper = new ItemHelper();
        Hino objeto = itens_exibicao.get(arg0);

        if (arg1 == null) {
            arg1 = layoutInflater.inflate(R.layout.item_lista_letra, null);

            itemHelper.nome = (TextView) arg1.findViewById(R.id.text_nome);
            itemHelper.cantor = (TextView) arg1.findViewById(R.id.text_cantor);
            itemHelper.data = (TextView) arg1.findViewById(R.id.text_data);
            arg1.setTag(itemHelper);
        } else {
            itemHelper = (ItemHelper) arg1.getTag();
        }

        // itemHelper.nome.setText(objeto.nome);
        itemHelper.nome.setText(Html.fromHtml(objeto.nome));
        itemHelper.cantor.setText(objeto.cantor);
        itemHelper.data.setText(objeto.data);

        return arg1;
    }

    private class ItemHelper {

        TextView nome, cantor, data;
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
                        Hino data = itens.get(i);

                        filtro = filtro.toString().toLowerCase();
                        String condicao = data.nome.toLowerCase();
                        String condicao2 = data.cantor.toLowerCase();

                        if (condicao.contains(filtro)) {
                            //se conter adiciona na lista de itens filtrados.
                            itens_filtrados.add(data);
                            pesquisa = (String) filtro;
                        }
                        if (condicao2.contains(filtro)) {
                            //se conter adiciona na lista de itens filtrados.
                            itens_filtrados.add(data);
                            // pesquisa = (String) filtro;
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
            protected void publishResults(CharSequence constraint, Filter.FilterResults results) {
                itens_exibicao = (List<Hino>) results.values; // Valores filtrados.
                notifyDataSetChanged();  // Notifica a lista de alteração
            }

        };
        return filter;
    }

}








