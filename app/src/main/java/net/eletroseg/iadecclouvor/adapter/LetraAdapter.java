package net.eletroseg.iadecclouvor.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import net.eletroseg.iadecclouvor.R;
import net.eletroseg.iadecclouvor.modelo.Letra;
import java.util.ArrayList;

/**
 * Created by maycon on 28/02/2018.
 */

public class LetraAdapter extends BaseAdapter {

    private static ArrayList<Letra> ArrayList;

    private LayoutInflater mInflater;
    Context context;
    private ViewHolder viewHolder;

    public LetraAdapter(Context context, ArrayList<Letra> dicaModelos) {
        this.context = context;
        ArrayList = dicaModelos;

        mInflater = LayoutInflater.from(context);
    }

    public int getCount() {
        return ArrayList.size();
    }

    public Object getItem(int position) {
        return ArrayList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_lista_letra, null);

            holder = new ViewHolder();
            holder.letra      = (TextView) convertView.findViewById(R.id.text_item_lista_letra_nome);
            holder.cantor          = (TextView) convertView.findViewById(R.id.text_item_lista_letra_cantor);
            holder.data             = (TextView) convertView.findViewById(R.id.text_item_lista_letra_data);
            holder.avaliacao    = (TextView) convertView.findViewById(R.id.text_item_lista_letra_avaliacao);



            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.letra.setText(ArrayList.get(position).nome);
        holder.cantor.setText(ArrayList.get(position).cantor);
        holder.data.setText(ArrayList.get(position).data);
        holder.avaliacao.setText(ArrayList.get(position).avaliacao);

        return convertView;
    }

    static class ViewHolder {
        TextView letra, cantor, avaliacao, data;



    }
}



