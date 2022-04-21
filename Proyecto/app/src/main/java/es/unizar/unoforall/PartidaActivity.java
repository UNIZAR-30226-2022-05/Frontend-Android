package es.unizar.unoforall;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import java.util.ArrayList;
import java.util.Random;

import es.unizar.unoforall.api.BackendAPI;
import es.unizar.unoforall.model.UsuarioVO;
import es.unizar.unoforall.model.partidas.Carta;
import es.unizar.unoforall.model.partidas.Jugador;
import es.unizar.unoforall.model.partidas.Partida;
import es.unizar.unoforall.model.salas.Sala;
import es.unizar.unoforall.utils.ActivityType;
import es.unizar.unoforall.utils.CustomActivity;
import es.unizar.unoforall.utils.ImageManager;
import es.unizar.unoforall.utils.SalaReceiver;
import es.unizar.unoforall.utils.dialogs.ReglasViewDialogBuilder;
import es.unizar.unoforall.utils.tasks.CancellableRunnable;
import es.unizar.unoforall.utils.tasks.Task;

public class PartidaActivity extends CustomActivity implements SalaReceiver {

    private static final int PAUSAR_ID = 0;
    private static final int ABANDONAR_ID = 1;
    private static final int VER_REGLAS_ID = 2;

    private static final int JUGADOR_ABAJO = 0;
    private static final int JUGADOR_IZQUIERDA = 1;
    private static final int JUGADOR_ARRIBA = 2;
    private static final int JUGADOR_DERECHA = 3;

    private static final int TURNO_ACTIVO_COLOR = Color.GREEN;
    private static final int TURNO_INACTIVO_COLOR = Color.WHITE;

    private static final int MAX_LONG_NOMBRE = 16;

    private LinearLayout[] layoutBarajasJugadores;
    private ImageView sentido;
    private ImageView[] imagenesJugadores;
    private TextView[] nombresJugadores;
    private TextView[] contadoresCartasJugadores;
    private ImageView cartaDelMedio;
    private ImageView mazoRobar;

    private int jugadorActualID = -1;

    @Override
    public ActivityType getType() {
        return ActivityType.PARTIDA;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_partida);

        // Ocultar action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        layoutBarajasJugadores = new LinearLayout[] {
                findViewById(R.id.barajaJugadorAbajo),
                findViewById(R.id.barajaJugadorIzquierda),
                findViewById(R.id.barajaJugadorArriba),
                findViewById(R.id.barajaJugadorDerecha)};

        sentido = findViewById(R.id.sentido);

        ImageView botonMenu = findViewById(R.id.botonMenu);
        registerForContextMenu(botonMenu);
        botonMenu.setOnClickListener(view -> view.showContextMenu(view.getX(), view.getY()));

        ImageView botonUNO = findViewById(R.id.botonUNO);
        botonUNO.setOnClickListener(view -> {
            mostrarMensaje("Has pulsado el botón UNO");
        });

        imagenesJugadores = new ImageView[] {
                findViewById(R.id.imagenJugadorAbajo),
                findViewById(R.id.imagenJugadorIzquierda),
                findViewById(R.id.imagenJugadorArriba),
                findViewById(R.id.imagenJugadorDerecha)
        };

        nombresJugadores = new TextView[] {
                findViewById(R.id.nombreJugadorAbajo),
                findViewById(R.id.nombreJugadorIzquierda),
                findViewById(R.id.nombreJugadorArriba),
                findViewById(R.id.nombreJugadorDerecha)
        };

        contadoresCartasJugadores = new TextView[] {
                findViewById(R.id.contadorCartasJugadorAbajo),
                findViewById(R.id.contadorCartasJugadorIzquierda),
                findViewById(R.id.contadorCartasJugadorArriba),
                findViewById(R.id.contadorCartasJugadorDerecha)
        };

        cartaDelMedio = findViewById(R.id.cartaDelMedio);
        mazoRobar = findViewById(R.id.mazoRobar);

        mazoRobar.setOnClickListener(view -> {
            mostrarMensaje("Has robado una carta");
        });

        // Borrar las cartas que están por defecto
        resetCartas();
        manageSala(BackendAPI.getSalaActual());
    }

    @Override
    public void manageSala(Sala sala){
        actualizarPantallaPartida(sala);
    }

    private void actualizarPantallaPartida(Sala sala){
        Partida partida = sala.getPartida();
        jugadorActualID = partida.getIndiceJugador(BackendAPI.getUsuarioID());

        // Falta posicionar los jugadores de forma adecuada en el caso de sólo 2 jugadores
        //  uno en frente del otro
        int numJugadores = partida.getJugadores().size();
        for(int i=0, j=0; j<numJugadores; i = (i + 1) % numJugadores, j++){
            Jugador jugador = partida.getJugadores().get(i);
            int turnoActual = partida.getTurno();
            if(jugador.isEsIA()){
                setImagenJugador(i, ImageManager.IA_IMAGE_ID);
                setNombreJugador(i, "IA_" + i, turnoActual == i);
            }else{
                UsuarioVO usuarioVO = sala.getParticipante(jugador.getJugadorID());
                setImagenJugador(i, usuarioVO.getAvatar());
                setNombreJugador(i, usuarioVO.getNombre(), turnoActual == i);
            }
            setNumCartas(i, jugador.getMano().size());
            jugador.getMano().sort(Carta::compareTo);
            for(Carta carta : jugador.getMano()){
                addCarta(i, carta);
            }
        }
        setSentido(partida.isSentidoHorario());
        setCartaDelMedio(partida.getUltimaCartaJugada());
    }

    private void setSentido(boolean sentidoHorario){
        if(sentidoHorario){
            sentido.setImageResource(R.drawable.ic_sentido_horario);
        }else{
            sentido.setImageResource(R.drawable.ic_sentido_antihorario);
        }
    }

    private void setImagenJugador(int jugadorID, int imageID){
        ImageManager.setImagePerfil(imagenesJugadores[jugadorID], imageID);
    }

    private void setNombreJugador(int jugadorID, String nombre, boolean turnoActivo){
        if(nombre.length() > MAX_LONG_NOMBRE){
            nombre = nombre.substring(0, MAX_LONG_NOMBRE-3) + "...";
        }

        nombresJugadores[jugadorID].setText(nombre);
        if(turnoActivo){
            nombresJugadores[jugadorID].setTextColor(TURNO_ACTIVO_COLOR);
        }else{
            nombresJugadores[jugadorID].setTextColor(TURNO_INACTIVO_COLOR);
        }
    }

    private void setNumCartas(int jugadorID, int numCartas){
        contadoresCartasJugadores[jugadorID].setText(numCartas + "");
    }

    private void setCartaDelMedio(Carta carta){
        ImageManager.setImagenCarta(cartaDelMedio, carta, true, false, true);
    }

    private void setMazoRobar(){
        ImageManager.setImagenMazoCartas(mazoRobar, true);
    }

    private void addCarta(int jugadorID, Carta carta){
        boolean defaultMode = true;
        boolean isDisabled = false;
        boolean isVisible = carta.isVisiblePor(jugadorActualID) || jugadorID == jugadorActualID;

        ImageView imageView = new ImageView(this);
        ImageManager.setImagenCarta(imageView, carta, defaultMode, isDisabled, isVisible);
        if(jugadorID != JUGADOR_ABAJO){
            imageView.setLayoutParams(new LinearLayout.LayoutParams(150, -2));
        }

        if(jugadorID == jugadorActualID){
            imageView.setOnClickListener(view -> mostrarMensaje("Has pulsado en la carta " + carta));
        }

        layoutBarajasJugadores[jugadorID].addView(imageView);
    }

    private void resetCartas(int jugadorID){
        layoutBarajasJugadores[jugadorID].removeAllViews();
    }

    private void resetCartas() {
        resetCartas(JUGADOR_ABAJO);
        resetCartas(JUGADOR_IZQUIERDA);
        resetCartas(JUGADOR_ARRIBA);
        resetCartas(JUGADOR_DERECHA);
    }

    private void abandonarPartida(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Abandonar partida");
        builder.setMessage("¿Quieres abandonar la partida?");
        builder.setPositiveButton("Sí", (dialog, which) -> {
            new BackendAPI(this).salirSala(BackendAPI.getSalaActualID());
            Intent intent = new Intent(this, PrincipalActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivityForResult(intent, 0);
        });
        builder.setNegativeButton("No", (dialog, which) -> {
            dialog.dismiss();
        });
        builder.create().show();
    }

    @Override
    public void onBackPressed(){
        abandonarPartida();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(Menu.NONE, PAUSAR_ID, Menu.NONE, "Pausar partida");
        menu.add(Menu.NONE, ABANDONAR_ID, Menu.NONE, "Abandonar partida");
        menu.add(Menu.NONE, VER_REGLAS_ID, Menu.NONE, "Ver reglas de la sala");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case PAUSAR_ID:
                mostrarMensaje("Pausar partida");
                return true;
            case ABANDONAR_ID:
                abandonarPartida();
                return true;
            case VER_REGLAS_ID:
                if(BackendAPI.getSalaActual() == null){
                    mostrarMensaje("La sala no puede ser null");
                    return false;
                }else{
                    new ReglasViewDialogBuilder(this, BackendAPI.getSalaActual().getConfiguracion()).show();
                    return true;
                }
        }
        return super.onContextItemSelected(item);
    }

    private void test(){
        // Para inicializar el HashMap es necesario usar al menos una carta
        jugadorActualID = JUGADOR_ABAJO;
        ImageManager.setImagenCarta(new ImageView(this), new Carta(Carta.Tipo.n0, Carta.Color.verde), true, false, true);
        Task.runDelayedTask(new CancellableRunnable() {
            private final ArrayList<Carta> defaultCards = new ArrayList<>(ImageManager.getDefaultCardsMap().keySet());
            @Override
            public void run() {
                runOnUiThread(() -> {
                    resetCartas();
                    for(int i=0;i<5;i++){
                        Carta cartaOriginal = defaultCards.get(new Random().nextInt(defaultCards.size()));
                        for(int j=0;j<4;j++){
                            Carta carta = cartaOriginal.clone();
                            carta.marcarVisible(j);
                            addCarta(j, carta);
                        }
                    }
                });
            }
        }, 0);
    }
}