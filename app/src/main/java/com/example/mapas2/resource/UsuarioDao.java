package com.example.mapas2.resource;

import java.sql.SQLException;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;
import com.example.mapas2.model.Usuario;

public class UsuarioDao extends BaseDaoImpl<Usuario, Long> {

    public UsuarioDao(ConnectionSource connectionSource, Class<Usuario> dataClass) throws SQLException {
        super(connectionSource, dataClass);
    }
}

