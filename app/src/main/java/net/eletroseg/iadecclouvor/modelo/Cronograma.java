package net.eletroseg.iadecclouvor.modelo;


import java.io.Serializable;
import java.util.List;

/**
 * Created by dell on 20/04/2017.
 */

public class Cronograma implements Serializable {

    public String id;
    public String diaDoCulto;
    public Usuario ministrante;
    public List<Usuario> vocal;
    public List<Usuario> instrumental;
    public List<Hino> musicas;
    public String observacao;




}
