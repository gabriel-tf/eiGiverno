package com.example.mapas2.resource;

import com.example.mapas2.model.Marcacao;
import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;

public class MarcacaoDao extends BaseDaoImpl<Marcacao, Long> {

    public MarcacaoDao(ConnectionSource connectionSource, Class<Marcacao> dataClass) throws SQLException {
        super(connectionSource, dataClass);
    }
}
