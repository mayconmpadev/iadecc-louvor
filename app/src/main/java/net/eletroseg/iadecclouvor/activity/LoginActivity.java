package net.eletroseg.iadecclouvor.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
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
import net.eletroseg.iadecclouvor.util.Util;

public class LoginActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;

    private EditText email, senha;
    private Button entrar;
    private TextInputLayout inputLayoutEmail, inputLayoutSenha;
    private TextView recuperarSenha;
    private LinearLayout layout, cadastrar;
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
        cadastrar = findViewById(R.id.linear_cadastrar);
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
        dialogRedefinirSenha("Atenção", "Tem certeza que deseja redefinir sua senha?");


            }
        });
    }

    private void redefinirSenha() {
        inputLayoutEmail.setErrorEnabled(false);
        if (!email.getText().toString().equals("")) {
            FirebaseAuth.getInstance().sendPasswordResetEmail(email.getText().toString())
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                dialog.dismiss();
                                dialogPadrao("Atenção", "Um email de redefinição de senha foi enviado para *" + email.getText().toString() +
                                        " * clique no link para redefinir sua senha");
                                inputLayoutEmail.setErrorEnabled(false);
                            } else {
                                dialog.dismiss();
                                inputLayoutEmail.setErrorEnabled(true);
                                inputLayoutEmail.setError("Esse email nao esta cadastrado");
                            }
                        }
                    });
        } else {
            dialog.dismiss();
            inputLayoutEmail.setErrorEnabled(true);
            inputLayoutEmail.setError("nao pode ser vazio");
        }
    }

    private void dialogRenvioEmailConfirmacao(final String sTitulo, String menssagem) {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_padrao_ok_cancelar);
        //instancia os objetos que estão no layout customdialog.xml
        final TextView titulo = dialog.findViewById(R.id.dialog_padrao_text_titulo);
        final TextView msg = dialog.findViewById(R.id.dialog_padrao_text_msg);
        final Button cancelar = dialog.findViewById(R.id.dialog_padrao_btn_esquerda);
        final Button ok = dialog.findViewById(R.id.dialog_padrao_btn_direita);
        final LinearLayout layout = dialog.findViewById(R.id.root);
        ok.setText("Reenviar");
        cancelar.setText("sair");
        layout.setVisibility(View.VISIBLE);


        Util.textoNegrito(menssagem, msg, null);
        titulo.setText(sTitulo);


        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                dialog.dismiss();
            }
        });

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(LoginActivity.this, "reenviar", Toast.LENGTH_SHORT).show();
                verificarEmail2();
                dialog.dismiss();
            }
        });

        //exibe na tela o dialog
        dialog.show();

    }

    private void dialogRedefinirSenha(final String sTitulo, String menssagem) {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_padrao_ok_cancelar);
        //instancia os objetos que estão no layout customdialog.xml
        final TextView titulo = dialog.findViewById(R.id.dialog_padrao_text_titulo);
        final TextView msg = dialog.findViewById(R.id.dialog_padrao_text_msg);
        final Button cancelar = dialog.findViewById(R.id.dialog_padrao_btn_esquerda);
        final Button ok = dialog.findViewById(R.id.dialog_padrao_btn_direita);
        final LinearLayout layout = dialog.findViewById(R.id.root);
        ok.setText("Redefinir");
        cancelar.setText("sair");
        layout.setVisibility(View.VISIBLE);


        Util.textoNegrito(menssagem, msg, null);
        titulo.setText(sTitulo);


        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                dialog.dismiss();
            }
        });

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exibeProgresso();
                redefinirSenha();
                dialog.dismiss();
            }
        });

        //exibe na tela o dialog
        dialog.show();

    }

    private void dialogPadrao(final String sTitulo, String menssagem) {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_padrao);

        dialog.setCanceledOnTouchOutside(false);
        //instancia os objetos que estão no layout customdialog.xml
        final TextView titulo = dialog.findViewById(R.id.dialog_padrao_text_titulo);
        final TextView msg = dialog.findViewById(R.id.dialog_padrao_text_msg);
        final Button ok = dialog.findViewById(R.id.dialog_padrao_btn_direita);
        ok.setText("OK");
        Util.textoNegrito(menssagem, msg, null);
        titulo.setText(sTitulo);

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        //exibe na tela o dialog
        dialog.show();

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
                       // firebaseAuth.signOut();
                        String msg = "Um email de confirmação foi enviado para *" + email.getText().toString() + "* clique no link " +
                                "de confirmação do email para validar seu cadastro. Caso nao tenha recebido clique em reenviar";
                        dialogRenvioEmailConfirmacao("Reenvio", msg);
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

    private void verificarEmail2() {
        try {
            firebaseAuth = ConfiguracaoFiribase.getFirebaseAutenticacao();
            final FirebaseUser user = firebaseAuth.getCurrentUser();



            if (user != null) {
                user.updateEmail(email.getText().toString());
                user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "Um e-mail de verificação foi eniado " +
                                    "para o endereço " + user.getEmail(), Toast.LENGTH_LONG).show();

                            firebaseAuth.signOut();
                        } else {
                            Toast.makeText(LoginActivity.this, "Verifique se o e-mail cadastrado está correto!", Toast.LENGTH_LONG).show();
                            firebaseAuth.signOut();
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
