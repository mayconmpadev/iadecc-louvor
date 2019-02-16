package net.eletroseg.iadecclouvor.activity;

import android.app.Dialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.github.rtoshiro.util.format.SimpleMaskFormatter;
import com.github.rtoshiro.util.format.text.MaskTextWatcher;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import net.eletroseg.iadecclouvor.R;

import net.eletroseg.iadecclouvor.modelo.Usuario;
import net.eletroseg.iadecclouvor.util.Base64Custom;
import net.eletroseg.iadecclouvor.util.ConfiguracaoFiribase;
import net.eletroseg.iadecclouvor.util.InstanciaFirebase;

import java.util.ArrayList;

public class CadastroLoginActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    Usuario usuarios;
    Dialog dialog;
    LinearLayout cadastro;
    View view;
    boolean b = true;

    ArrayList<Usuario> arrayListUsuarios = new ArrayList<>();

    TextInputLayout inputLayoutEmail, inputLayoutSenha, inputLayoutConfirmar, inputLayoutNome, inputLayoutTelefone;
    EditText email, senha, confirmar, telefone, nome;
    Button salvar;
    String nomeUsado = "vazio";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_login);

        inputLayoutEmail = findViewById(R.id.input_layout_cadastro_login_email);
        inputLayoutSenha = findViewById(R.id.input_layout_cadastro_login_senha);
        inputLayoutConfirmar = findViewById(R.id.input_layout_cadastro_login_confirmar);
        inputLayoutNome = findViewById(R.id.input_layout_cadastro_login_nome);
        inputLayoutTelefone = findViewById(R.id.input_layout_cadastro_login_telefone);
        email = findViewById(R.id.edit_cadastro_login_email);
        senha = findViewById(R.id.edit_cadastro_login_senha);
        confirmar = findViewById(R.id.edit_cadastro_login_confirmar);
        telefone = findViewById(R.id.edit_cadastro_login_telefone);
        nome = findViewById(R.id.edit_cadastro_login_nome);
        salvar = findViewById(R.id.btn_cadastro_login_salvar);
        cadastro = findViewById(R.id.layout_cadastro);

        view = cadastro;

        salvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nomeUsado = nome.getText().toString();
                exibeProgresso();
                verificarNome();

            }
        });

        nome.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!nomeUsado.equals("vazio")) {
                    if (nomeUsado.equals(nome.getText().toString())) {
                        inputLayoutNome.setErrorEnabled(true);
                        inputLayoutNome.setError("nome ja existe");
                    } else {
                        inputLayoutNome.setErrorEnabled(false);

                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        SimpleMaskFormatter simpleMoeda = new SimpleMaskFormatter("(NN)NNNNN-NNNN");
        MaskTextWatcher maskMoeda = new MaskTextWatcher(telefone, simpleMoeda);


        telefone.addTextChangedListener(maskMoeda);
    }

    private void cadastrarUsuario() {

        firebaseAuth = ConfiguracaoFiribase.getFirebaseAutenticacao();
        firebaseAuth.createUserWithEmailAndPassword(
                usuarios.email,
                usuarios.senha).addOnCompleteListener(CadastroLoginActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {

                    verificarEmail();

                    dialog.dismiss();

                    String identificadorDoUsuario = Base64Custom.codificarBase64(usuarios.email);
                    usuarios.id = identificadorDoUsuario ;




                } else {
                    dialog.dismiss();
                    String erro;
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthWeakPasswordException e) {
                        erro = "A senha usada é muito fraca";
                        dialog.dismiss();
                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        erro = "Esse e-mail nao existe";
                        dialog.dismiss();
                    } catch (FirebaseAuthUserCollisionException e) {
                        erro = "Esse e-mail já esta em uso";
                        dialog.dismiss();

                    } catch (Exception e) {
                        erro = "Ao cadastrar usuario";
                        dialog.dismiss();
                        e.printStackTrace();
                    }
                    Snackbar snackbar = Snackbar.make(view, erro, Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            }
        });

    }

    private void verificarEmail() {
        try {

            final FirebaseUser user = firebaseAuth.getCurrentUser();

            if (user != null) {
                user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {
                            Toast.makeText(CadastroLoginActivity.this, "Um email de verificação foi enviado " +
                                    "para " + user.getEmail(), Toast.LENGTH_LONG).show();
                            if (firebaseAuth.getCurrentUser() != null) {
                               salvar();
                                firebaseAuth.signOut();
                                Intent intent = new Intent(CadastroLoginActivity.this, LoginActivity.class);
                                startActivity(intent);
                                finish();
                            }

                        } else {
                            Toast.makeText(CadastroLoginActivity.this, "Verifique se o email cadastrado esta correto!", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
            }

        }catch (Exception e){
            Toast.makeText(this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
        }

    }

    private boolean validarCampos() {
        boolean a;
        if (email.getText().toString().isEmpty()) {
            a = false;
            inputLayoutEmail.setErrorEnabled(true);
            inputLayoutEmail.setError("nao pode ser vazio");

            return a;

        } else {
            a = true;
            inputLayoutEmail.setErrorEnabled(false);
        }

        if (senha.getText().toString().isEmpty()) {
            inputLayoutSenha.setErrorEnabled(true);
            inputLayoutSenha.setError("nao pode ser vazio");
            a = false;
            return a;
        } else {
            inputLayoutSenha.setErrorEnabled(false);
            a = true;

        }

        if (senha.getText().toString().equals(confirmar.getText().toString())) {
            inputLayoutConfirmar.setErrorEnabled(false);
            a = true;
        } else {
            inputLayoutConfirmar.setErrorEnabled(true);
            inputLayoutConfirmar.setError("senha nao confere");
            a = false;
            return a;

        }

        if (nome.getText().toString().isEmpty()) {

            inputLayoutNome.setErrorEnabled(true);
            inputLayoutNome.setError("nao pode ser vazio");
            a = false;
            return a;
        } else {
            inputLayoutNome.setErrorEnabled(false);
            a = true;
        }

        if (telefone.getText().toString().isEmpty()) {
            inputLayoutTelefone.setErrorEnabled(true);
            inputLayoutTelefone.setError("nao pode ser vazio");
            a = false;
            return a;
        } else {
            inputLayoutTelefone.setErrorEnabled(false);
            a = true;
        }


        return a;


    }

    public void exibeProgresso() {


        dialog = new Dialog(this);


        dialog.setContentView(R.layout.progresso_circular);

        //define o título do Dialog
        dialog.setTitle("Editar:");

        //instancia os objetos que estão no layout customdialog.xml
        final ProgressBar nao = dialog.findViewById(R.id.progresso_circulo);

        dialog.show();

    }

    private void verificarNome() {
        DatabaseReference reference = InstanciaFirebase.getDatabase().getReference("usuarios");
        arrayListUsuarios.clear();
        b = true;
        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                Usuario usuarios = dataSnapshot.getValue(Usuario.class);
                arrayListUsuarios.add(usuarios);


            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        reference.addChildEventListener(childEventListener);

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (int i = 0; i < arrayListUsuarios.size(); i++) {

                    if (arrayListUsuarios.get(i).nome.toLowerCase().equals(nome.getText().toString().toLowerCase())) {
                        b = false;
                        break;
                    }

                }


                if (b) {
                    inputLayoutNome.setErrorEnabled(false);
                    usuarios = new Usuario();
                    usuarios.email = email.getText().toString();
                    usuarios.senha =  senha.getText().toString();
                    usuarios.nome = nome.getText().toString().toLowerCase();
                    usuarios.telefone =  telefone.getText().toString();
                    usuarios.moderador = "nao" ;

                    if (validarCampos()) {
                        cadastrarUsuario();
                    }
                } else {
                    inputLayoutNome.setErrorEnabled(true);
                    inputLayoutNome.setError("Esse nome ja existe");
                }

                dialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
    private void salvar(){

        DatabaseReference reference = InstanciaFirebase.getDatabase().getReference("usuarios").child(usuarios.id);
        reference.setValue(usuarios);
    }


}
