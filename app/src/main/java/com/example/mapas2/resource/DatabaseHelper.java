package com.example.mapas2.resource;

import com.example.mapas2.model.Marcacao;
import com.example.mapas2.model.Usuario;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicInteger;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

/**
 * Created by windows on 23/10/2014.
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    private static final String DATABASE_NAME = "BASE_NAME.db";
    private static final int DATABASE_VERSION = 4;
    private static DatabaseHelper databaseHelper = null;
    private static final AtomicInteger usageCounter = new AtomicInteger(0);

    private UsuarioDao usuarioDao = null;
    private MarcacaoDao marcacaoDao= null;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static synchronized DatabaseHelper getHelper(Context context) {
        if (databaseHelper == null) {
            databaseHelper = new DatabaseHelper(context);
        }
        usageCounter.incrementAndGet();
        return databaseHelper;
    }

    @Override
    public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
        try {
            TableUtils.createTableIfNotExists(connectionSource, Usuario.class);
            TableUtils.createTableIfNotExists(connectionSource, Marcacao.class);
        } catch (SQLException e) {
            Log.e("ERRO", "Can't create database", e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            TableUtils.dropTable(connectionSource, Usuario.class, false);
            TableUtils.dropTable(connectionSource, Marcacao.class, false);
            onCreate(db, connectionSource);
        } catch (Exception e) {
            Log.e("ERRO", "Can't update database", e);
        }
    }

    public UsuarioDao getUsuarioDao() {
        try {
            if (usuarioDao == null) {
                usuarioDao = new UsuarioDao(connectionSource, Usuario.class);
            }
        } catch (SQLException ex) {
            Log.w(DatabaseHelper.class.getName(), "Can't create questionDAO", ex);
        }
        return usuarioDao;
    }

    public MarcacaoDao getMarcacaoDao() {
        try {
            if (marcacaoDao == null) {
                marcacaoDao = new MarcacaoDao(connectionSource, Marcacao.class);
            }
        } catch (SQLException ex) {
            Log.w(DatabaseHelper.class.getName(), "Can't create questionDAO", ex);
        }
        return marcacaoDao;
    }
}
