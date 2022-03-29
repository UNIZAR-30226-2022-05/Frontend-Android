package es.unizar.unoforall;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import es.unizar.unoforall.api.RestAPI;
import es.unizar.unoforall.database.UsuarioDbAdapter;
import es.unizar.unoforall.model.RespuestaLogin;

public class ConfirmEmailActivity extends AppCompatActivity {

    private EditText codigoEditText;
    private UsuarioDbAdapter mDbHelper;
    private Integer codigo;
    private Long mRowId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_email);
        setTitle(R.string.confirmarEmail);

        mDbHelper = new UsuarioDbAdapter(this);
        mDbHelper.open();

        codigoEditText = (EditText) findViewById(R.id.codigoConfirmacion);

        Button confirmCodigo = (Button) findViewById(R.id.confirmCode);

        confirmCodigo.setOnClickListener(view -> {
            //recoleccion de datos
            String codigoString = codigoEditText.getText().toString();
            if(codigoString.length() == 6){
                codigo = Integer.parseInt(codigoString);
            } else {
                Toast.makeText(this, "Codigo incorrecto" , Toast.LENGTH_SHORT).show();
                return;
            }
            String email = this.getIntent().getStringExtra("correo");
            String contrasennaHash = this.getIntent().getStringExtra("contrasenna");

            RestAPI api = new RestAPI(this, "/api/registerStepTwo");
            api.addParameter("correo", email);
            api.addParameter("codigo", codigo);
            api.openConnection();

            //recepcion de los datos y actuar en consecuencia
            api.setOnObjectReceived(String.class, resp -> {
                if(resp == null){
                    mRowId = mDbHelper.createUsuario(email, contrasennaHash);

                    RestAPI api2 = new RestAPI(this, "/api/login");
                    api2.addParameter("correo", email);
                    api2.addParameter("contrasenna", contrasennaHash);
                    api2.openConnection();

                    api2.setOnObjectReceived(RespuestaLogin.class, resp2 -> {
                        if(resp2.isExito()){
                            Intent i = new Intent(this, PantallaPrincipalActivity.class);
                            i.putExtra(PantallaPrincipalActivity.KEY_CLAVE_INICIO, resp2.getClaveInicio());
                            startActivity(i);
                        }else{
                            Toast.makeText(this, resp2.getErrorInfo(), Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });
                } else {
                    Toast.makeText(this, resp, Toast.LENGTH_SHORT).show();
                }
            });
        });

    }
}