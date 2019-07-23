package net.eletroseg.iadecclouvor.activity;

import android.app.Dialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import net.eletroseg.iadecclouvor.R;

import net.eletroseg.iadecclouvor.util.Base64Custom;
import net.eletroseg.iadecclouvor.util.ConfiguracaoFiribase;
import net.eletroseg.iadecclouvor.util.InstanciaFirebase;
import net.eletroseg.iadecclouvor.util.Parametro;
import net.eletroseg.iadecclouvor.util.SPM;

public class LoginActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;

    private EditText email, senha;
    private Button entrar;
    private TextInputLayout inputLayoutEmail, inputLayoutSenha;
    private TextView cadastrar, recuperarSenha;
    private LinearLayout layout;
    private Dialog dialog;
    SPM spm = new SPM(LoginActivity.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        FirebaseApp.initializeApp(this);
        getSupportActionBar().hide();

        email = findViewById(R.id.edit_login_email);
        senha = findViewById(R.id.edit_login_senha);
        entrar = findViewById(R.id.btn_login_entrar);
        senha = findViewById(R.id.edit_login_senha);
        inputLayoutEmail = findViewById(R.id.input_layout_login_email);
        inputLayoutSenha = findViewById(R.id.input_layout_login_senha);
        cadastrar = findViewById(R.id.text_cadastrar);
        recuperarSenha = findViewById(R.id.text_recuperar_senha);
        layout = findViewById(R.id.layout_login);
        layout.setVisibility(View.INVISIBLE);
        usuarioLogado();
        entrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exibeProgresso();
                verificar();
            }
        });

        cadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, CadastroLoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        recuperarSenha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputLayoutEmail.setErrorEnabled(false);
                if (!email.getText().toString().equals("")) {
                    FirebaseAuth.getInstance().sendPasswordResetEmail(email.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(LoginActivity.this, "Um email de redefinição " +
                                                "de senha foi enviado ao email " + email.getText().toString(), Toast.LENGTH_LONG).show();
                                        inputLayoutEmail.setErrorEnabled(false);
                                    } else {
                                        inputLayoutEmail.setErrorEnabled(true);
                                        inputLayoutEmail.setError("Esse email nao esta cadastrado");
                                    }
                                }
                            });
                } else {
                    inputLayoutEmail.setErrorEnabled(true);
                    inputLayoutEmail.setError("nao pode ser vazio");
                }


            }
        });
    }


    private void verificar() {
        if (email.getText().toString().isEmpty()) {
            inputLayoutEmail.setErrorEnabled(true);
            inputLayoutEmail.setError("nao pode ser vazio");
            dialog.dismiss();
        } else if (!email.getText().toString().contains("@")) {
            inputLayoutEmail.setErrorEnabled(true);
            inputLayoutEmail.setError("email incorreto");
            dialog.dismiss();
        } else if (senha.getText().toString().isEmpty()) {
            inputLayoutSenha.setErrorEnabled(true);
            inputLayoutEmail.setErrorEnabled(false);
            inputLayoutSenha.setError("nao pode ser vazio");
            dialog.dismiss();
        } else {
            inputLayoutSenha.setErrorEnabled(false);
            inputLayoutEmail.setErrorEnabled(false);
            validarLogin();
        }
    }

    private void validarLogin() {
        firebaseAuth = ConfiguracaoFiribase.getFirebaseAutenticacao();
        firebaseAuth.signInWithEmailAndPassword(
                email.getText().toString(),
                senha.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    dialog.dismiss();
                    if (firebaseAuth.getCurrentUser().isEmailVerified()) {

                        SPM spm = new SPM(LoginActivity.this);
                        spm.setPreferencia("USUARIO_LOGADO", "USUARIO", Base64Custom.codificarBase64(firebaseAuth.getCurrentUser().getEmail()));
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();

                    } else {
                        firebaseAuth.signOut();
                        exibeDialogEmail();
                    }


                } else {


                    String erro;
                    try {
                        throw task.getException();


                    } catch (FirebaseAuthInvalidUserException e) {
                        dialog.dismiss();
                        erro = "Esse e-mail nao existe";
                        inputLayoutEmail.setError(erro);
                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        dialog.dismiss();
                        erro = "Senha incorreta";
                        inputLayoutSenha.setError(erro);

                    } catch (Exception e) {
                        dialog.dismiss();
                        erro = e.getMessage().toString();
                        e.printStackTrace();

                    }

                    Snackbar snackbar = Snackbar.make(layout, erro, Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            }
        });

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

    private void exibeDialogEmail() {
        final Dialog dialog = new Dialog(this);

        dialog.setContentView(R.layout.dialogo_sair);

        //define o título do Dialog
        dialog.setTitle("Email nao confirmado");

        //instancia os objetos que estão no layout customdialog.xml
        final Button sair = (Button) dialog.findViewById(R.id.btn_dialogo_sair_sim);
        final Button continuar = (Button) dialog.findViewById(R.id.btn_dialogo_sair_nao);
        final TextView msg = (TextView) dialog.findViewById(R.id.text_dialogo_sair);
        continuar.setText("Reenviar");

        msg.setText("Um email de confirmacao foi enviado para " + email.getText().toString().toUpperCase() + " clique no link" +
                "de confirmacao do email para validar seu cadastro. Caso nao tenha recebido clique em reenviar");


        sair.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                verificarEmail();
                //finaliza o dialog
                dialog.dismiss();
            }
        });

        continuar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //finaliza o dialog
                dialog.dismiss();
            }
        });


        //exibe na tela o dialog
        dialog.show();

    }

    private void verificarEmail() {
        try {

            final FirebaseUser user = firebaseAuth.getCurrentUser();
            user.updateEmail(email.getText().toString());

            if (user != null) {
                user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "Um email de verificação foi eniado " +
                                    "para " + user.getEmail(), Toast.LENGTH_LONG).show();


                        } else {
                            Toast.makeText(LoginActivity.this, "Verifique se o email cadastrado esta correto!", Toast.LENGTH_LONG).show();
                        }

                    }
                });
            }

        } catch (Exception e) {
            Toast.makeText(this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
        }

    }

    private void usuarioLogado() {
        firebaseAuth = ConfiguracaoFiribase.getFirebaseAutenticacao();
        if (firebaseAuth.getCurrentUser() == null || firebaseAuth.getCurrentUser().toString().equals("")) {
            layout.setVisibility(View.VISIBLE);

        } else {
            final DatabaseReference reference = InstanciaFirebase.getDatabase().getReference("usuarios").child(spm.getPreferencia("USUARIO_LOGADO", "USUARIO", "erro"));
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, "logoff", Toast.LENGTH_SHORT).show();
                        spm.setPreferencia("VENDEDOR_LOGADO", "VENDEDOR", "");
                        firebaseAuth.signOut();
                        layout.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }
    }

    //metodo responsavel por criar o usuario do sistema padrão.
    private void salvarAdmin() {

     //   final Vendedor vendedor = new Vendedor();
      //  vendedor.nome = "admin";
      //  vendedor.senha = "123456";
      //  vendedor.nivel = "03";

      //  vendedor.id = "usuario_admin";

        firebaseAuth = ConfiguracaoFiribase.getFirebaseAutenticacao();

        final DatabaseReference reference = InstanciaFirebase.getDatabase().getReference(spm.getPreferencia("USUARIO_LOGADO", "USUARIO", "erro")).child("usuario");
      //  final DatabaseReference reference1 = InstanciaFirebase.getDatabase().getReference(spm.getPreferencia("USUARIO_LOGADO", "USUARIO", "erro")).child("vendedor").child(vendedor.id);
        reference.keepSynced(true);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                   // reference1.setValue(vendedor);
                    Parametro.primeiroAcesso = true;
                    Toast.makeText(LoginActivity.this, "Salvo com sucesso", Toast.LENGTH_SHORT).show();


                } else {
                    Parametro.primeiroAcesso = false;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }


}
