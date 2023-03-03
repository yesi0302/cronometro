package com.prueba.cronometro;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import java.util.Vector;


public class CronometroActivity extends AppCompatActivity {

    //Tag para el Log
    private final static String TAG="CronometroActivity";
    private Chronometer cronometro;
    private Button btn_iniciar;
    private ListView listaRegistros;
    private ArrayAdapter<String> adapter;
    //Diálogo de progreso que se presenta durante las búsquedas en la base de datos
    private ProgressDialog progreso;
    //Indica si el cronómetro ha sido iniciado
    private boolean iniciado=false;
    //Array utilizado para guardar temporalmente la lista de IDs de usuario leídas desde la base de datos
    private int[] idUsuarios=null;
    //Variable que indica el ID del usuario actual
    private int idUsuarioActual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_cronometro);
        cronometro=(Chronometer)findViewById(R.id.chronometer1);
        btn_iniciar=(Button)findViewById(R.id.btn_cronometro);
        adapter=new ArrayAdapter<String>(this, R.layout.layout_item_lista,new Vector<String>());
        listaRegistros=(ListView) findViewById(R.id.listaRegistros);
        listaRegistros.setAdapter(adapter);
        progreso=new ProgressDialog(this);
        progreso.setMessage("Realizando b\u00fasqueda");
        progreso.setTitle("PROGRESO");
        //Inicia la búsqueda de registros guardados en la base de datos en un AsyncTask
        TareaQuery tarea=new TareaQuery();
        tarea.execute("INTERVALO");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_cronometro, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            registrarUsuario();
            return true;
        }
        if (id == R.id.action_seleccionar) {
            TareaQuery tarea=new TareaQuery();
            tarea.execute("USUARIO");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void controlCronometro(View v){
        if(!iniciado){
            //reinicia la base del cronómetro para ce inicie de cero
            cronometro.setBase(SystemClock.elapsedRealtime());
            cronometro.start();
            iniciado=true;
            btn_iniciar.setText("Detener");
        }else{
            cronometro.stop();
            iniciado=false;
            btn_iniciar.setText("Iniciar");
            String registroActual=cronometro.getText().toString();
            adapter.add(registroActual);
            //Iniica el almacenamiento en la base de datos.
            BDHelper helper=new BDHelper(this, BDHelper.NOMBRE_BD, null, BDHelper.VERSION_BD);
            SQLiteDatabase bd=helper.getWritableDatabase();
            ContentValues values=new ContentValues();
            values.put("INTERVALO", registroActual);
            //Si la versión de la base de datos es mayor a 1 entoncas se ha incluido la columna
            //ID_USUARIO a la tabla de registros
            if(BDHelper.VERSION_BD > 1)
                values.put("ID_USUARIO", idUsuarioActual);
            if(bd.insert(BDHelper.NOMBRE_TABLA_REG, null, values)==-1){
                Toast.makeText(getApplicationContext(), "Error al insertar datos", Toast.LENGTH_LONG).show();
            }
            bd.close();
        }
    }

    public String[] buscarRegistros(){
        String[] registrosEncontrados=null;
        BDHelper helper=new BDHelper(getApplicationContext(), BDHelper.NOMBRE_BD, null, BDHelper.VERSION_BD);
        SQLiteDatabase bd=helper.getReadableDatabase();
        //Array con el nombre de las columnas a ser retornadas
        String columnas[]=new String[]{"ID","INTERVALO"};
        //Sentencia SQL WHERE sin el ´WHERE´
        String where="ID < ? AND ID > ?";
        //Array con los valores que sustituyen a los signos de interrogación en el String anterior
        String selectionArgs[]=new String[]{"10","4"};
        //Instrucción que realiza la búsqueda en la base de datos
        Cursor cursor=bd.query(BDHelper.NOMBRE_TABLA_REG, columnas, where,selectionArgs , null, null, null);
        //Retorna true si hay algún dato en el cursor
        if(cursor.moveToFirst()){
            registrosEncontrados=new String[cursor.getCount()];
            do{
                registrosEncontrados[cursor.getPosition()]=cursor.getString(cursor.getColumnIndex("ID"));
                registrosEncontrados[cursor.getPosition()]=cursor.getString(cursor.getColumnIndex("INTERVALO"));
               //adapter.add(cursor.getString(cursor.getColumnIndex("INTERVALO")));
            }while(cursor.moveToNext());
        }
        cursor.close();
        bd.close();
        return registrosEncontrados;
    }

    public String[] buscarUsuarios(){
        String[] usuariosEncontrados=null;
        BDHelper helper=new BDHelper(getApplicationContext(), BDHelper.NOMBRE_BD, null, BDHelper.VERSION_BD);
        SQLiteDatabase bd=helper.getReadableDatabase();
        //Array con el nombre de las columnas a ser retornadas
        String columnas[]=new String[]{"ID","NOMBRE"};
        try{
            //Instrucción que realiza la búsqueda en la base de datos
            Cursor cursor=bd.query("USUARIOS", columnas, null,null, null, null, null);
            //Retorna true si hay algún dato en el cursor
            if(cursor.moveToFirst()){
                usuariosEncontrados=new String[cursor.getCount()];
                idUsuarios=new int[cursor.getCount()];
                do{
                    //guarda los IDs de Usuario en un array temporal para ser accedido Cuando se seleccione
                    //el usuario de la lista.
                    idUsuarios[cursor.getPosition()] = cursor.getInt(cursor.getColumnIndex("ID"));
                    usuariosEncontrados[cursor.getPosition()]=cursor.getString(cursor.getColumnIndex("NOMBRE"));
                }while(cursor.moveToNext());
            }
            cursor.close();
            bd.close();
        }catch (SQLiteException e){
            Log.e(TAG, e.getMessage());
        }
        return usuariosEncontrados;

    }

    //AsyncTask en el que se realiza la búsqueda de registros
    public class TareaQuery extends AsyncTask<String, Integer, String[]> {

        //variable que se utiliza para identificar la búsqueda que se realiza en el AsyncTask
        //Usuarios o Intervalos
        private String tipoBusqueda;
        @Override
        protected String[] doInBackground(String... params) {
            tipoBusqueda=params[0];
            String leidos[]=null;
            if(tipoBusqueda.equals("INTERVALO"))
                leidos = buscarRegistros();
            if(tipoBusqueda.equals("USUARIO"))
                leidos = buscarUsuarios();
            return leidos;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progreso.show();
        }

        @Override
        protected void onPostExecute(String[] result) {
            if (result != null) {
                super.onPostExecute(result);
                if (tipoBusqueda.equals("INTERVALO")) {
                    //adapter.addAll(result); //aplica a versiones API>10
                    for (String resultado : result)
                        adapter.add(resultado);
                }
                if (tipoBusqueda.equals("USUARIO"))
                    seleccionarUsuario(result);
            }
            progreso.dismiss();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }
    }

    public void registrarUsuario(){
        final Context contexto=this;
        View formulario = View.inflate(this, R.layout.layout_registro_usuario, null);
        AlertDialog.Builder alerta = new AlertDialog.Builder(this);
        alerta.setTitle("Registrar Usuario");
        alerta.setView(formulario);
        final EditText edit_nombre = (EditText) formulario
                .findViewById(R.id.edit_nombre);
        alerta.setPositiveButton("Aceptar",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        BDHelper helper=new BDHelper(contexto, BDHelper.NOMBRE_BD, null, BDHelper.VERSION_BD);
                        SQLiteDatabase bd=helper.getWritableDatabase();
                        ContentValues values=new ContentValues();
                        values.put("NOMBRE", edit_nombre.getText().toString());
                        if(bd.insert("USUARIOS", null, values)==-1){
                            Toast.makeText(getApplicationContext(), "Error al insertar datos", Toast.LENGTH_LONG).show();
                        }
                        bd.close();
                    }
                });
        alerta.setNegativeButton("Cancelar",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        AlertDialog dialogo = alerta.create();
        dialogo.show();
    }

    public void seleccionarUsuario(String[] lista){
        AlertDialog.Builder alerta = new AlertDialog.Builder(this);
        alerta.setTitle("Seleccionar Usuario");
        alerta.setItems(lista, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //guarda el usuario actual para el cual se guardan los registros en la base de datos
                idUsuarioActual=idUsuarios[i];
            }
        });
        alerta.setNegativeButton("Cancelar",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        AlertDialog dialogo = alerta.create();
        dialogo.show();
    }
}
