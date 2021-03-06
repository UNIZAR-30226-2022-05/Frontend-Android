package es.unizar.unoforall;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import es.unizar.unoforall.api.BackendAPI;
import es.unizar.unoforall.model.salas.ConfigSala;
import es.unizar.unoforall.model.salas.ReglasEspeciales;
import es.unizar.unoforall.utils.ActivityType;
import es.unizar.unoforall.utils.CustomActivity;

public class CrearSalaActivity extends CustomActivity {

    private ConfigSala configSala;
    private ReglasEspeciales reglasEspeciales;

    @Override
    public ActivityType getType(){
        return ActivityType.CREAR_SALA;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_sala);
        setTitle(R.string.crearSala);

        configSala = new ConfigSala();
        configSala.setModoJuego(ConfigSala.ModoJuego.Original);
        reglasEspeciales = configSala.getReglas();

        RadioGroup participantesRadioGroup = findViewById(R.id.participantesRadioGroup);

        RadioButton radio2 = findViewById(R.id.radio_dos);
        RadioButton radio3 = findViewById(R.id.radio_tres);
        RadioButton radio4 = findViewById(R.id.radio_cuatro);

        switch(configSala.getMaxParticipantes()){
            case 2: radio2.setChecked(true); break;
            case 3: radio3.setChecked(true); break;
            case 4: radio4.setChecked(true); break;
        }

        Spinner spinner = findViewById(R.id.modo_juego_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.modo_juego_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setSelection(0);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                configSala.setModoJuego(ConfigSala.ModoJuego.values()[i]);
                if(configSala.getModoJuego() == ConfigSala.ModoJuego.Parejas){
                    radio2.setEnabled(false);
                    radio3.setEnabled(false);
                    radio4.setEnabled(false);
                    configSala.setMaxParticipantes(4);
                    participantesRadioGroup.check(R.id.radio_cuatro);
                }else{
                    radio2.setEnabled(true);
                    radio3.setEnabled(true);
                    radio4.setEnabled(true);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        RadioButton buttonEsPublica;
        if(configSala.isEsPublica()){
            buttonEsPublica = findViewById(R.id.radio_publica);
        }else{
            buttonEsPublica = findViewById(R.id.radio_privada);
        }
        buttonEsPublica.setChecked(true);

        CheckBox checkBox = findViewById(R.id.checkbox_rayosX);
        checkBox.setChecked(reglasEspeciales.isCartaRayosX());
        checkBox.setOnCheckedChangeListener((buttonView, isChecked) ->
                reglasEspeciales.setCartaRayosX(isChecked));

        checkBox = findViewById(R.id.checkbox_intercambio);
        checkBox.setChecked(reglasEspeciales.isCartaIntercambio());
        checkBox.setOnCheckedChangeListener((buttonView, isChecked) ->
                reglasEspeciales.setCartaIntercambio(isChecked));

        checkBox = findViewById(R.id.checkbox_x2);
        checkBox.setChecked(reglasEspeciales.isCartaX2());
        checkBox.setOnCheckedChangeListener((buttonView, isChecked) ->
                reglasEspeciales.setCartaX2(isChecked));


        checkBox = findViewById(R.id.checkbox_encadenar_2_4);
        checkBox.setChecked(reglasEspeciales.isEncadenarRoboCartas());
        checkBox.setOnCheckedChangeListener((buttonView, isChecked) ->
                reglasEspeciales.setEncadenarRoboCartas(isChecked));

        checkBox = findViewById(R.id.checkbox_redirigir_2_4);
        checkBox.setChecked(reglasEspeciales.isRedirigirRoboCartas());
        checkBox.setOnCheckedChangeListener((buttonView, isChecked) ->
                reglasEspeciales.setRedirigirRoboCartas(isChecked));

        checkBox = findViewById(R.id.checkbox_jugar_varias_cartas);
        checkBox.setChecked(reglasEspeciales.isJugarVariasCartas());
        checkBox.setOnCheckedChangeListener((buttonView, isChecked) ->
                reglasEspeciales.setJugarVariasCartas(isChecked));

        checkBox = findViewById(R.id.checkbox_penalizacion_4_color);
        checkBox.setChecked(reglasEspeciales.isEvitarEspecialFinal());
        checkBox.setOnCheckedChangeListener((buttonView, isChecked) ->
                reglasEspeciales.setEvitarEspecialFinal(isChecked));

        Button confirmarSalaButton = findViewById(R.id.confirmarSalaButton);
        confirmarSalaButton.setOnClickListener(view -> {
            if(configSala.getModoJuego() == ConfigSala.ModoJuego.Parejas){
                configSala.setMaxParticipantes(4);
            }
            new BackendAPI(this).crearSala(configSala);
        });

    }

    @SuppressLint("NonConstantResourceId")
    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();

        switch(view.getId()) {
            case R.id.radio_publica:
                if (checked)
                    configSala.setEsPublica(true);
                    break;
            case R.id.radio_privada:
                if (checked)
                    configSala.setEsPublica(false);
                    break;
            case R.id.radio_dos:
                if (checked)
                    configSala.setMaxParticipantes(2);
                    break;
            case R.id.radio_tres:
                if (checked)
                    configSala.setMaxParticipantes(3);
                    break;
            case R.id.radio_cuatro:
                if (checked)
                    configSala.setMaxParticipantes(4);
                    break;
        }
    }
}