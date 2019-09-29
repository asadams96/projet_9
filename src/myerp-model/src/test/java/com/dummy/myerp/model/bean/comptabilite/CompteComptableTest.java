package com.dummy.myerp.model.bean.comptabilite;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class CompteComptableTest {


    
    @Test
    public void getByNumero() {

        List<CompteComptable> compteComptableList = new ArrayList<>();
        String libelle = "Une instance de compteComptable avec le numero testé qui est présent dans la liste";
        Integer numero = 158;

        CompteComptable compteComptable = new CompteComptable(numero, libelle);
        compteComptableList.add(new CompteComptable(null, null));
        compteComptableList.add(new CompteComptable(5, numero.toString()));
        compteComptableList.add(compteComptable);

        Assert.assertEquals(
                compteComptable.toString(),
                CompteComptable.getByNumero(compteComptableList, numero),
                compteComptable
        );



        libelle = "Aucune instance de compteComptable avec le numero testé de présent dans la liste";
        compteComptableList.remove(compteComptable);

        Assert.assertNotEquals(
                compteComptable.toString(),
                CompteComptable.getByNumero(compteComptableList, numero),
                compteComptable
        );
    }
}