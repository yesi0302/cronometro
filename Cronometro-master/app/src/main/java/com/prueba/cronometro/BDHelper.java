package com.prueba.cronometro;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class BDHelper extends SQLiteOpenHelper{

    //Tag para el Log
    public static final String TAG="BDHelper";
    //Nombre de la base de datos
    public static final String NOMBRE_BD="registros";
    //Version de la Base de datos
    public static final int VERSION_BD=1;
    //Nombre de la tablas de registros del cron�metro.
    public static final String NOMBRE_TABLA_REG="REGISTROS";
    //Instrucci�n para la creaci�n de la tabla de registros de tiempo
    private final String CREAR_TABLA_REGISTROS="CREATE TABLE REGISTROS(ID INTEGER PRIMARY KEY AUTOINCREMENT, INTERVALO TEXT)";
    //Instrucci�n para la creaci�n de una tabla de usuarios
    private final String CREAR_TABLA_USUARIOS="CREATE TABLE USUARIOS(ID INTEGER PRIMARY KEY AUTOINCREMENT, NOMBRE TEXT)";
    //Instrucci�n para alterar la tabla de registros
    private final String ALTER_TABLA_REGISTROS="ALTER TABLE REGISTROS ADD ID_USUARIO INTEGER";

    public BDHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREAR_TABLA_REGISTROS);
    }

    @Override
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

    /**
     * Ejecutado Autom�ticamente cuando la nueva version es inferior a la vieja.
     * En versiones del API inferiores a la 11 (3.0.X Honeycomb) cualquier cambio de versi�n es
     * manejado por el m�todo onUpgrade independientemente del aumento o disminuci�n de la versi�n
     */
    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i(TAG,"Ejecutado onDownGrade");
        db.beginTransaction();
        try{
            //El m�todo super lanza SQLiteException
			//super.onDowngrade(db, oldVersion, newVersion);
            //elimina la tabla de usuarios
            db.execSQL("DROP TABLE USUARIOS");
            db.setTransactionSuccessful();
        }catch (SQLiteException e){
            Log.e(TAG,e.getMessage());
            e.printStackTrace();
        }
        finally{
            db.endTransaction();
        }
    }
}
