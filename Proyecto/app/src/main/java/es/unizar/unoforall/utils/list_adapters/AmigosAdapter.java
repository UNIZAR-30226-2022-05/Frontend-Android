package es.unizar.unoforall.utils.list_adapters;

import android.app.Activity;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import es.unizar.unoforall.R;
import es.unizar.unoforall.model.ListaUsuarios;
import es.unizar.unoforall.model.UsuarioVO;
import es.unizar.unoforall.utils.ImageManager;

public class AmigosAdapter extends ArrayAdapter<UsuarioVO> {

    private final int resourceLayout;
    private final Activity activity;

    public AmigosAdapter(Activity activity, ListaUsuarios listaUsuarios){
        super(activity, R.layout.salas_row, listaUsuarios.getUsuarios());
        this.resourceLayout = R.layout.amigos_row;
        this.activity = activity;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        View view = convertView;

        if(view == null){
            view = LayoutInflater.from(activity).inflate(resourceLayout, null);
        }

        UsuarioVO usuarioVO = getItem(position);

        String nombre = usuarioVO.getNombre();
        String correo = usuarioVO.getCorreo();
        int imageID = usuarioVO.getAvatar();

        TextView nombreTextView = view.findViewById(R.id.nombre);
        TextView correoTextView = view.findViewById(R.id.correo);
        ImageView imageView = view.findViewById(R.id.imagen);

        nombreTextView.setText(nombre);
        correoTextView.setText(correo);
        ImageManager.setImage(imageView, imageID);

        return view;
    }
}