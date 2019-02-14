package net.eletroseg.iadecclouvor.modelo;


import com.google.firebase.database.DatabaseReference;

import net.eletroseg.iadecclouvor.util.ConfiguracaoFiribase;


/**
 * Created by dell on 20/04/2017.
 */

public class Usuario {

    private String id;
    private String email;
    private String senha;
    private String nome;
    private String telefone;
    private String moderador;
    private String status;


    public Usuario(){

    }

    public void salvar(){
        DatabaseReference referenciaFirebase = ConfiguracaoFiribase.getFirebase();
        referenciaFirebase.child("usuarios").child(getId()).setValue(this);

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getModerador() {
        return moderador;
    }

    public void setModerador(String moderador) {
        this.moderador = moderador;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
