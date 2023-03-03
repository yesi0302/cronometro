package com.prueba.cronometro;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class UsuarioActivity extends Activity {
    private ListView listaOpciones;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_usuarios);
        listaOpciones=(ListView)findViewById(R.id.listaUsuarios);
        String[] opciones=new String[]{"Subir Versi\u00f3n","Bajar Versi\u00f3n"};
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1);
        listaOpciones.setAdapter(adapter);
    }

    //Maneja los eventos de toque sobre los items de la lista
    private AdapterView.OnItemClickListener listener =new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int posicion, long l) {

        }
    };
}
