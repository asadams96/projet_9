package com.dummy.myerp.model.bean.comptabilite;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class JournalComptableTest {



    @Test
    public void getByCode() {

        List<JournalComptable> journalComptableList = new ArrayList<>();
        String libelle = "Une instance de journalComptable avec le code testé qui est présent dans la liste";
        String code = "Code-de-test_1";

        JournalComptable journalComptable = new JournalComptable(code, libelle);
        journalComptableList.add(new JournalComptable("", ""));
        journalComptableList.add(new JournalComptable(null, null));
        journalComptableList.add(new JournalComptable(null, ""));
        journalComptableList.add(new JournalComptable("", null));
        journalComptableList.add(new JournalComptable("Code-incorrect_1", code));
        journalComptableList.add(journalComptable);

        Assert.assertEquals(
                            journalComptable.toString(),
                            JournalComptable.getByCode(journalComptableList, code),
                            journalComptable
                            );



        libelle = "Aucune instance de journalComptable avec le code testé de présent dans la liste";
        journalComptableList.remove(journalComptable);

        Assert.assertNotEquals(
                               journalComptable.toString(),
                               JournalComptable.getByCode(journalComptableList, code),
                               journalComptable
                               );
    }
}