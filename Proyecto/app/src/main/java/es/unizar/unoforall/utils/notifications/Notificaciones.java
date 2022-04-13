package es.unizar.unoforall.utils.notifications;

import es.unizar.unoforall.api.BackendAPI;
import es.unizar.unoforall.model.UsuarioVO;
import es.unizar.unoforall.model.salas.NotificacionSala;
import es.unizar.unoforall.utils.CustomActivity;

public class Notificaciones {
    public static void mostrarNotificacionAmigo(CustomActivity activity, UsuarioVO usuarioVO){
        NotificationManager.Builder builder = new NotificationManager.Builder(activity);
        builder
                .withTitle("Nueva solicitud de amistad")
                .withMessage("El usuario " + usuarioVO.getNombre() + " quiere ser tu amigo")
                .withAction1("Cancelar", customActivity -> new BackendAPI(activity).rechazarPeticion(usuarioVO))
                .withAction2("Aceptar", customActivity -> new BackendAPI(activity).aceptarPeticion(usuarioVO))
                .build();
    }

    public static void mostrarNotificacionSala(CustomActivity activity, NotificacionSala notificacionSala){
        NotificationManager.Builder builder = new NotificationManager.Builder(activity);
        builder
                .withTitle("Nueva solicitud para unirse a una sala")
                .withMessage("El usuario " + notificacionSala.getRemitente().getNombre() +
                        " te ha propuesto unirse a la sala " + notificacionSala.getSalaID())
                .withAction1("Cancelar", customActivity -> {})
                .withAction2("Aceptar", customActivity -> {
                    // Comprobar en que actividad se encuentra el usuario
                    //   y actuar en consecuencia
                    switch(activity.getType()){
                        case INICIO:
                        case REGISTER:
                        case RESTABLECER_CONTRASENNA:
                            break;
                        case PRINCIPAL:

                    }
                }).build();
    }
}
