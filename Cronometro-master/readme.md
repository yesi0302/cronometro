# Bases de datos en Android.
Android Provee soporte para bases de datos **SQLite**. La manera recomendada para crear y acceder a las bases de datos es através de una subclase de **SQLiteOpenHelper**.

***

>>
**Nota:** Las bases de datos son privadas para la aplicación y solo son accedidas por todas las clases dentro de la aplicación en la que es creada.<sup>1</sup>

***

La clase **SQLiteOpenHelper** provee dos métodos abstractos que deben ser sobreescritos:

1. **onCreate:** En este método deben incluirse las operaciones de creación de tablas y carga de datos iniciales en la base de datos.
2. **onUpgrade:** En este método se incluyen las instrucciones de creación de tablas, modificación de tablas ó eliminación de tablas en una base de datos existente.

Los pasos necesarios para crear, acceder y actualizar una base de datos **SQLite** en Android se resumen a continuación:

1. Crear una subclase de **SQLiteOpenHelper**.
2. Implementar los métodos **onCreate** y **onUpgrade**.
3. Implementar un constructor que haga referencia al constructor de la clase madre (super).
4. Crear atributos String públicos estáticos con el nombre de la base de datos y las tablas.
5. Crear atributos String con instrucciones **SQL** para crear base de datos y tablas.

## Ejemplo

En este ejemplo se utiliza el elemento **Chronometer** definido en Android, los registros del cronómetro serán almacenados en una base de datos **SQLite**.

### <a name="funcionamiento"/> Funcionamiento Requerido:
1. Debe tener un cronómetro que presente en pantalla el tiempo transcurrido.
2. Debe tener una lista que presente en pantalla los registros anteriores del cronómetro.
3. Debe incluir un botón inicie el cronómetro y lo detenga, este botón debe presentar el texto **Iniciar** cuando el cronómetro está detenido y **Detener** cuando el cronómetro ha sido iniciado.
4. Cada vez que el cronómetro sea detenido, el registro de tiempo debe ser almacenado en una base de datos **SQLite**.
5. Cuando se inicie la aplicación se debe buscar los registros en la base de datos, si existiera alguno, deben ser mostrados el la lista de pantalla.

En **Android Studio** ó  **Eclipse** cree un nuevo proyecto llamado **Cronometro** (se ha omitido el acento intencionalmente) con la siguiente configuración:
 * **Package name:** com.prueba.cronometro
 * **Minimun SDK:** API 10: Android 2.3.3
 * En el diálogo **Ad an Activity to mobile** seleccionar **Blank Activity**
 * **Activity Name:** CronometroActivity.
 * **Layout Name:** layout_cronometro.
 
### 1. Creación del layout de la aplicación
Cambiar la raíz del layout de **RelativeLayout** a **LinearLayout** con orientación vertical como se observa en el siguiente segmento código.
```xml
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools" android:layout_width="match_parent"
    android:orientation="vertical"
    android:layout_height="match_parent" android:paddingLeft="@dimen/activity_horizontal_margin"
    android:paddingRight="@dimen/activity_horizontal_margin"
    android:paddingTop="@dimen/activity_vertical_margin"
    android:paddingBottom="@dimen/activity_vertical_margin" tools:context=".CronometroActivity">

    <TextView android:text="@string/hello_world" android:layout_width="wrap_content"
        android:layout_height="wrap_content" />

</LinearLayout>
``` 
3. Cambiar el valor del String **hello_world** a **Cronómetro**.
4. Cambiar los siguientes atributos del TextView:
 * **android:layout_width**="match_parent".
 * **android:layout_height**="wrap_content".
 * **android:textSize**="20sp"
 * **android:gravity**="center".
5. Añadir al layout un elemento del tipo **Chronometer** con los siguientes atributos:
 * **android:layout_width**="match_parent"
 * **android:layout_height**="0dp"
 * **android:textSize**="40sp"
 * **android:gravity**="center"
 * **android:layout_weight**="2"
6. Añadir al layout un elemento del tipo **ListView** con los siguientes atributos:
 * **android:id**="@+id/listaRegistros"
 * **android:layout_width**="match_parent"
 * **android:layout_height**="0dp"
 * **android:background**="#FFFFFF"
 * **android:layout_weight**="4" 
7. Añadir al layotu un botón con los siguientes atributos:
 * **android:id**="@+id/btn_cronometro"
 * **android:layout_width**="match_parent"
 * **android:layout_height**="0dp"
 * **android:layout_weight**="1"
 * **android:onClick**="controlCronometro"
 * **android:text**="Iniciar"

 El atributo **oClick** del botón hace referencia al método que controla el funcionamiento del cronómetro en la clase **CronometroActivity**.
 
 El código completo del layout es el siguiente:
 ```xml
 <LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools" android:layout_width="match_parent"
    android:orientation="vertical"
    android:layout_height="match_parent" android:paddingLeft="@dimen/activity_horizontal_margin"
    android:paddingRight="@dimen/activity_horizontal_margin"
    android:paddingTop="@dimen/activity_vertical_margin"
    android:paddingBottom="@dimen/activity_vertical_margin" tools:context=".CronometroActivity">

    <TextView
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:textSize="20sp"
        android:text="@string/hello_world"
        android:gravity="center" />

    <Chronometer
        android:id="@+id/chronometer1"
        android:layout_width="match_parent"
        android:layout_height="0dp"
        android:textSize="40sp"
        android:gravity="center"
        android:layout_weight="2"
        android:text="Chronometer" />
    <ListView
        android:id="@+id/listaRegistros"
        android:layout_width="match_parent"
        android:layout_height="0dp"
        android:background="#FFFFFF"
        android:layout_weight="4" >
    </ListView>

    <Button
        android:id="@+id/btn_cronometro"
        android:layout_width="match_parent"
        android:layout_height="0dp"
        android:layout_weight="1"
        android:onClick="controlCronometro"
        android:text="Iniciar" />

</LinearLayout>
 ```
 
###2. Modificación del Activity.

La plantilla **Blank Activity**, crea una subclase de **ActionBarActivity**, esta clase ha sido declarada como obsoleta (deprecated) y se recomienda utilizar en su lugar **AppComptActivity**, la primera modificación es esa, el código en este punto debe ser el siguiente:

```java
package com.prueba.cronometro;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;


public class CronometroActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_cronometro);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
```

Para cumplir con el [funcionamiento requerido](#funcionamiento), se crean instancias de los elementos **Button**, **ListView** y **Chronometer** del layout declarados en el archivo XML utilizando el método del **Activity** `findViewById(R.layout.layout_cronometro)`, además debe hacerse un cast para convertir al tipo específico de elemento.

Los datos en la lista de pantalla se cargan a través de un **ArrayAdapter**, el ArrayAdapter mapea los elementos de cualquier implementación de `java.util.List` en los elementos individuales de la lista que se presentarán en pantalla con los atributos descritos en el layout **layout_item_lista**.

Se implementa el método **controlCronometro** pasando como parámetro un elemento **View**, con este parámetro se le dice al sistema que el método está asociado a una vista. En el método **controlCronometro** se inicia y se detiene el cronómetro, se cambia el texto al botón y se guardan los registros en la base de datos.

```java
    public void controlCronometro(View v){
        if(!iniciado){
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
            BDHelper helper=new BDHelper(this, BDHelper.NOMBRE_BD, null, 1);
            SQLiteDatabase bd=helper.getWritableDatabase();
            ContentValues values=new ContentValues();
            values.put("INTERVALO", registroActual);
            if(bd.insert(BDHelper.NOMBRE_TABLA_REG, null, values)==-1){
                Toast.makeText(getApplicationContext(), "Error al insertar datos", Toast.LENGTH_LONG).show();
            }
            bd.close();
        }
    }
``` 

### <a name="escrituraEnBD"> Escritura en la base de datos
Los pasos para escribir en la base de datos se describen a continuación:

1. Obtener una instancia de una subclase de **SQLiteOpenHelper**, en este caso **BDHelper** `BDHelper helper=new BDHelper(this, BDHelper.NOMBRE_BD, null, 1);`los parámetros son el **context**, el nombre de la base de datos y la versión.
2. Obtener una instancia de una base de datos **SQLite** `SQLiteDatabase bd=helper.getWritableDatabase();`
3. Obtener una instancia de la clase **ContentValues**, `ContentValues values=new ContentValues();` esta representa los datos que serán guardados en la base de datos.  
4. Cargar los datos en la instancia de **ContentValues** creada en el paso anterior `values.put("INTERVALO", registroActual);`, los datos se cargan en la forma **clave-valor** donde la clave representa el nombre de la columna en la base de datos.
5. Insertar los datos en la tabla haciendo uso de la instrucción **insert** `bd.insert(BDHelper.NOMBRE_TABLA_REG, null, values)`, esta instrucción retorna -1 si ocurre un error en la inserción de datos.
6. Cerrar la base de datos `bd.close();`.

### <a name="lecturaEnBD"> Lectura en la base de datos
Los pasos para leer los registros de la base de datos se describen a continuación:

1. Obtener una instancia de una subclase de **SQLiteOpenHelper**, en este caso **BDHelper** `BDHelper helper=new BDHelper(this, BDHelper.NOMBRE_BD, null, 1);`los parámetros son el **context**, el nombre de la base de datos y la versión.
2. Obtener una instancia de una base de datos **SQLite** `SQLiteDatabase bd=helper.getReadbleDatabase();`
3. Realizar una búsqueda (query) en la base de datos, en este punto hay dos formas de hacerlo:
 1. Utilizando el método **query**
    * Crear un array del tipo String con el nombre de las columnas a ser retornadas en la búsqueda: `String columnas[]=new String[]{"ID","INTERVALO"};`
    * En el caso de una búsqueda filtrada, donde sea necesario utilizar una clausula  WHERE, por ejemplo si en la base de datos del cronómetro se quiere leer los registros cuyo campo **ID** es menor a 10 y mayor a 4, la sentencia **SQL** sería `SELECT ID, INTERVALO FROM REGISTROS WHERE ID < 10 AND ID >4`, adaptando esta sentencia a **Android**, se debe crear un String en el que se omita el propio WHERE,  quedaría `String where="ID < ? AND ID > ?"`, el signo de interrogación deja un espacio para los parámetros que deben ser definidos en otro array según el orden en el que aparecen `String[] selectionArgs=new String[]{"10","4"};`
    * Obtener un elemento de la clase **Cursor** a través de la sentencia **query** con los parámetros definido anteriormente: `Cursor cursor=bd.query(BDHelper.NOMBRE_TABLA_REG, columnas, where,selectionArgs , null, null, null);`
    * Leer los datos del cursor utilizando los métodos `cursor.moveToFirst()` para verificar que existe algún registro según el query solicitado y `cursor.moveToNext();` para iterar sobre cada uno de los registros en el cursor. 
    * Cerrar el cursor `cursor.close();`
 2. Utilizando el método **rawQuery**
    * Crear un String con una sentencia **SQL**, por ejemplo `String buqueda="SELECT ID, INTERVALO FROM REGISTROS WHERE ID < ? AND ID > ?"`, al igual que en la instrucción **query** definida en la parte anterior, los signos de interrogación dehan espacio para los parámetros definidos en el array de tipo String con los valores específicos. 
6. Cerrar la base de datos `bd.close();`.

***

>**Nota:** Existen otros métodos **query** y **rawQuery** con parámetros distintos a los aquí mencionados, para mayor información refiérase a la documentación.
 
***

Siguiendo los pasos descritos anteriormente se implementa el método **buscarRegistros**
```java
	public void buscarRegistros(){
		BDHelper helper=new BDHelper(getApplicationContext(), BDHelper.NOMBRE_BD, null, BDHelper.VERSION);
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
			do{
				registros.add(cursor.getString(cursor.getColumnIndex("INTERVALO")));
			}while(cursor.moveToNext());
		}
		cursor.close();
		bd.close();		
	}
```

Para cumplir con el punto [5 de los requerimientos de funcionamiento](#funcionamiento) debe ejecutarse el método **buscarRegistros** aquí definido en el método **onCreate** del Activity.

***

>**Nota:** Una buena práctica es ejecutar la búsquedas en bases de datos dentro de un **AsyncTask**

***

El código completo de **CronometroActivity** se presenta a continuación:

```java
package com.prueba.cronometro;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ListView;
import android.widget.Toast;

import java.util.Vector;


public class CronometroActivity extends AppCompatActivity {

    private Chronometer cronometro;
    private Button btn_iniciar;
    private ListView listaRegistros;
    private ArrayAdapter<String> adapter;
    //Diálogo de progreso que se presenta durante las búsquedas en la base de datos
    private ProgressDialog progreso;
    //Indica si el cronómetro ha sido iniciado
    private boolean iniciado=false;

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
        tarea.execute("");
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
            BDHelper helper=new BDHelper(this, BDHelper.NOMBRE_BD, null, 1);
            SQLiteDatabase bd=helper.getWritableDatabase();
            ContentValues values=new ContentValues();
            values.put("INTERVALO", registroActual);
            if(bd.insert(BDHelper.NOMBRE_TABLA_REG, null, values)==-1){
                Toast.makeText(getApplicationContext(), "Error al insertar datos", Toast.LENGTH_LONG).show();
            }
            bd.close();
        }
    }

    public String[] buscarRegistros(){
        String[] registrosEncontrados=null;
        BDHelper helper=new BDHelper(getApplicationContext(), BDHelper.NOMBRE_BD, null, 1);
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
                registrosEncontrados[cursor.getPosition()]=cursor.getString(cursor.getColumnIndex("INTERVALO"));
               //adapter.add(cursor.getString(cursor.getColumnIndex("INTERVALO")));
            }while(cursor.moveToNext());
        }
        cursor.close();
        bd.close();
        return registrosEncontrados;
    }

    //AsyncTask en el que se realiza la búsqueda de registros
    public class TareaQuery extends AsyncTask<String, Integer, String[]> {

        @Override
        protected String[] doInBackground(String... params) {
            return buscarRegistros();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progreso.show();
        }

        @Override
        protected void onPostExecute(String[] result) {
            super.onPostExecute(result);
            //adapter.addAll(result); //aplica a versiones API>10
            for(String resultado:result)
                adapter.add(resultado);
            progreso.dismiss();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }
    }


}

```

### <a name="actualizacion"> Actualización de la base de datos

En esta sección se entenderá por actualización de la base de datos, todos los cambios en la estructura de las tablas y de la base de datos propiamente. Cuando se presenta un cambio en el parámetro **version** que se pasa al constructor de la subclase de **SQLiteOpenHelper**, en el caso particular de este ejemplo **BDHelper**, se ejecuta el método **onUpgrade** cuando la versión es superior a la anterior y **onDownGrade** cuando la versión es inferior a la anterior, en este método deben ejecutarse las sentencias de creación, alteración y eliminación de tablas de la base de datos.

***

>**Nota 1:** Las operaciones ejecutadas dentro del método **onUpgrade** deben realizarse dentro de una transacción
>**Nota 2:** El método **onDownGrade** se ejecuta solo cuando la aplicación es instalada en versiones del API superiores o iguales a la 11 (3.0.X Honeycomb), en versiones inferiores cualquier cambio de versión debe ser manejado a través del método **onUpgrade**.

***

Modificar el método **onUpgrade** de **BDHelper** de la siguiente manera

```java
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        Log.i(TAG,"Ejecutado onUpGrade "+" "+i+" "+i1);
        sqLiteDatabase.beginTransaction();
        try{
            sqLiteDatabase.execSQL(CREAR_TABLA_USUARIOS);
            sqLiteDatabase.execSQL(ALTER_TABLA_REGISTROS);
            sqLiteDatabase.setTransactionSuccessful();
        }catch (SQLiteException e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        }finally {
            sqLiteDatabase.endTransaction();
        }
    }
``` 

En las instrucciones anteriores se espera que ante un aumento de la versión de la base de datos se cree una nueva tabla con el nombre de **USUARIOS** y se altere la table **REGISTROS** para incluir una columna de nombre **ID_USUARIO** y tipo entero. El método se ejecutará automáticamente cuando se cree una instancia de BDHelper con una versión superior a la actual de la base de datos.

Para cargar los datos en la tabla **USUARIOS** se procederá de la siguiente manera:
* Crear un **layout** con el nombre **layout_registro**, del tipo **LinearLayout** con orientación vertical, un **TextView** que contenga el texto **"Nombre de Usuario"** y un **EditText**.
```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical" >

    <TextView
        android:id="@+id/text_nombre"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Nombre de Usuario" />

    <EditText
        android:id="@+id/edit_nombre"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:background="#FFFFFF"
        android:ems="10"
        android:inputType="text" >

        <requestFocus />
    </EditText>

</LinearLayout>
```
* Crear un método **registrarUsuario** dentro de **CronometroActivity** que despliegue un diálogo de registro de usuario con el **layout** definido en el paso anterior.
```java
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
```
Al hacer click sobre el botón **"Aceptar"** se guarda el contenido del **EditText** en la tabla **USUARIOS**.
* Crear un método **buscarUsuarios** que busque en la tabla **USUARIOS** y retorne los registros encontrados.
```java
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
```
* Crear un método **seleccionarUsuario** en **CronometroActivity** que presente en pantalla un diálogo con una lista de usuarios registrados en la base de datos.
```java
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
```
* Modificar la clase TareaQuery para que discrimine las solicitudes de búsqueda entre **USUARIOS** y **REGISTROS**.
```java
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
```
* Modificar el recurso **res>values>strings.xml** para incluir nuevos Strings
```xml
<resources>
    <string name="app_name">Cronometro</string>

    <string name="hello_world">Cronómetro</string>
    <string name="action_settings">Registrar Usuario</string>
    <string name="action_seleccionar">Seleccionar Usuario</string>

</resources>
```
* Modificar el recurso **res>menu>menu_cronometro.xml** para que tenga dos acciones: **Registrar Usuario** y **Seleccionar Usuario**
```xml
<menu xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools" tools:context=".CronometroActivity">
    <item android:id="@+id/action_settings" android:title="@string/action_settings"
        android:orderInCategory="100" app:showAsAction="never" />
    <item android:id="@+id/action_seleccionar" android:title="@string/action_seleccionar"
        android:orderInCategory="100" app:showAsAction="never" />
</menu>
```
* Modificar el método **onOptionsItemSelected** (eventos en el menú de opciones) para que maneje las opciones de registro y selección de usuarios.
```java
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
```

###3. Configuración de la base de datos SQLite.

Crear una subclase de **SQLiteOpenHelper** con el nombre **BDHelper** en el paquete **com.prueba.cronometro**, implementar los métodos **onCreate** y **onUpgrade** e implementar un constructor que haga referencia al constructor de la clase madre (super). En este punto el código es el siguiente:

```java
package com.prueba.cronometro;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BDHelper extends SQLiteOpenHelper{

    public BDHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
```
El siguiente paso es crear atributos String que servirán de instrucciones para crear las tablas y acceder a ellas.
```java
	public static final String NOMBRE_BD="registros";
	private final String CREAR_TABLA_REGISTROS="CREATE TABLE REGISTROS(ID INTEGER PRIMARY KEY AUTOINCREMENT, INTERVALO TEXT)";
	private final String CREAR_TABLA_USUARIOS="CREATE TABLE USUARIOS(ID INTEGER PRIMARY KEY AUTOINCREMENT, NOMBRE TEXT)";
	public static final String NOMBRE_TABLA_REG="REGISTROS";
```

***

>>**Nota:** Las variables CREAR_TABLA_XX guardan como valores sentencias **SQL** para la creación de tablas con la siguiente sintaxis: 
```sql
CREATE TABLE NOMBRE_TABLA(NOMBRE_COLUMNA1 TIPO_DE_DATO1, NOMBRE_COLUMNA1 TIPO_DE_DATO1...)
```

En el método **onCreate** añadir la siguiente instrucción: `sqLiteDatabase.execSQL(CREAR_TABLA_REGISTROS);`, **onCreate** pasa como parámetro una instancia de **SQLiteDatabase** que representa la base de datos **SQLite** que ha sido creada, con esta instrucción se crea la tabla de registros en la base de datos. El método **onCreate** se ejecuta solo cuando se crea la base de datos, esto ocurre con la invocación del método `getWritableDatabase` ó `getReadableDatabase`, desde una instancia de **BDHelper** esto se explicará en lineas siguientes.
```java
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREAR_TABLA_REGISTROS);
    }
```

***

***

##Referencias
1. [Opciones de Almacenamiento (Bases de Datos)](http://developer.android.com/guide/topics/data/data-storage.html#db)

***
