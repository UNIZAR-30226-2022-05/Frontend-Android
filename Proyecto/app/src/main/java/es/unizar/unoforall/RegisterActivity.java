package es.unizar.unoforall;

import androidx.appcompat.app.AppCompatActivity;

import static es.unizar.unoforall.utils.HashUtils.cifrarContrasenna;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import es.unizar.unoforall.api.RestAPI;


public class RegisterActivity extends AppCompatActivity{

    private EditText userNameText;
    private EditText mailText;
    private EditText passwordText;
    private EditText passBisText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        setTitle(R.string.register);

        userNameText = (EditText) findViewById(R.id.nombre);
        mailText = (EditText) findViewById(R.id.correo);
        passwordText = (EditText) findViewById(R.id.contrasenna);
        passBisText = (EditText) findViewById(R.id.contrasennabis);

        String userName = userNameText.getText().toString();
        String mail = mailText.getText().toString();

        String contrasenna = passwordText.getText().toString();
        String contrasennaHash = cifrarContrasenna(contrasenna);

        Button confirmRegister = (Button) findViewById(R.id.register);

        confirmRegister.setOnClickListener(view -> {
            setResult(RESULT_OK);
            String password = passwordText.getText().toString();
            String passBis = passBisText.getText().toString();
            if(!password.equals(passBis)){
                Toast.makeText(RegisterActivity.this, getString(R.string.ErrorContrasegnas), Toast.LENGTH_SHORT).show();
                return;
            }
            //envio de los datos al servidor

            RestAPI api = new RestAPI("/api/registerStepOne");
            api.addParameter("Valor1", mail);
            api.addParameter("Valor2", contrasennaHash);
            api.addParameter("Valor3", userName);
            api.openConnection();
            //recepcion de los datos y actuar en consecuencia

            String resp = api.receiveObject(String.class);
            if (resp.equals(null)){
                //Usuario registrado y cambiamos a la pantalla de inicio
            } else {
                Toast.makeText(RegisterActivity.this, resp, Toast.LENGTH_SHORT).show();
                return;
            }
            //finish();
        });
    }

}
