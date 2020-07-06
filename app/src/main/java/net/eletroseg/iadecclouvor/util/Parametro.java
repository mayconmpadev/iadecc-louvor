package net.eletroseg.iadecclouvor.util;

import net.eletroseg.iadecclouvor.modelo.Hino;
import net.eletroseg.iadecclouvor.modelo.Usuario;

import java.util.ArrayList;

/**
 * Created by maycon on 29/05/2018.
 */

public  class Parametro {
    public static String nome = "";

    public static String flag = "normal";
    public static String letra = "";
    public static String cifra = "";
    public static boolean tipo = false;
    public static ArrayList<Usuario> staticArrayVocal = new ArrayList<>();
    public static ArrayList<Usuario> staticArrayInstrumental = new ArrayList<>();
    public static ArrayList<Usuario> staticArrayMinistrante = new ArrayList<>();
    public static ArrayList<Hino> staticArrayMusica = new ArrayList<>();
    public static ArrayList<Integer> posicaoVocal = new ArrayList<>();
    public static ArrayList<Integer> posicaoInstrumental = new ArrayList<>();
    public static ArrayList<Integer> posicaoMusica = new ArrayList<>();


    public static boolean download = false;
    public static boolean primeiroAcesso = false;
    public static boolean cep = false;



}
