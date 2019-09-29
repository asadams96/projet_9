package com.dummy.myerp.consumer.dao.impl.db.rowmapper.comptabilite;

import com.dummy.myerp.model.bean.comptabilite.SequenceEcritureComptable;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SequenceEcritureComptableRM implements RowMapper<SequenceEcritureComptable> {


    @Override
    public SequenceEcritureComptable mapRow(ResultSet rs, int rowNum) throws SQLException {

        SequenceEcritureComptable sequenceEcritureComptable = new SequenceEcritureComptable();
        sequenceEcritureComptable.setAnnee(rs.getInt("annee"));
        sequenceEcritureComptable.setDerniereValeur(rs.getInt("derniere_valeur"));

        return sequenceEcritureComptable;
    }
}
