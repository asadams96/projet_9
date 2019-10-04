package com.dummy.myerp.business.impl.manager;

import java.math.BigDecimal;
import java.util.*;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import com.dummy.myerp.business.impl.AbstractBusinessManager;
import com.dummy.myerp.model.bean.comptabilite.*;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.transaction.TransactionStatus;
import com.dummy.myerp.business.contrat.manager.ComptabiliteManager;
import com.dummy.myerp.technical.exception.FunctionalException;
import com.dummy.myerp.technical.exception.NotFoundException;


/**
 * Comptabilite manager implementation.
 */
public class ComptabiliteManagerImpl extends AbstractBusinessManager implements ComptabiliteManager {



    // ==================== Attributs ====================
    private final String formatsReferenceTab[] = {"C-AAAA/SSSSS", "CC-AAAA/SSSSS", "CCC-AAAA/SSSSS", "CCCC-AAAA/SSSSS", "CCCCC-AAAA/SSSSS"};



    // ==================== Constructeurs ====================
    /**
     * Instantiates a new Comptabilite manager.
     */
    public ComptabiliteManagerImpl() {

    }



    // ==================== Getters/Setters ====================
    @Override
    public List<CompteComptable> getListCompteComptable() {
        return getDaoProxy().getComptabiliteDao().getListCompteComptable();
    }


    @Override
    public List<JournalComptable> getListJournalComptable() {
        return getDaoProxy().getComptabiliteDao().getListJournalComptable();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public List<EcritureComptable> getListEcritureComptable() {
        return getDaoProxy().getComptabiliteDao().getListEcritureComptable();
    }





    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void addReference(EcritureComptable pEcritureComptable) throws FunctionalException {
        //  1. Remonter depuis la persitance la dernière valeur de la séquence du journal pour l'année de l'écriture (table sequence_ecriture_comptable)
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(pEcritureComptable.getDate());
        Integer lastSequence;
        try {
            SequenceEcritureComptable sequenceEcritureComptable = getDaoProxy().getComptabiliteDao().getSequenceEcritureComptable(
                                                                            calendar.get(Calendar.YEAR), pEcritureComptable.getJournal().getCode());
            lastSequence = sequenceEcritureComptable.getDerniereValeur();
        } catch (NotFoundException e) {
            lastSequence = null;
        }
        //  2. S'il n'y a aucun enregistrement pour le journal pour l'année concernée : -  Utiliser le numéro 1. | Sinon :  - Utiliser la dernière valeur + 1
        //  3. Mettre à jour la référence de l'écriture avec la référence calculée (RG_Compta_5)
        //  4. Enregistrer (insert/update) la valeur de la séquence en persitance (table sequence_ecriture_comptable)
        String reference;
        if(lastSequence == null)  {
            pEcritureComptable.setReference( pEcritureComptable.getJournal().getCode() + "-" + calendar.get(Calendar.YEAR) + "/00001" );
           this.insertSequenceEcritureComptable(new SequenceEcritureComptable(calendar.get(Calendar.YEAR), 1), pEcritureComptable.getJournal().getCode());
        }
        else {
            int count = 0;
            for( int i = 0; i < formatsReferenceTab[0].length(); i++) {
                if(formatsReferenceTab[0].substring(i, i+1).equals("S"))  count++;
            }
            StringBuilder newSequence = new StringBuilder(String.valueOf(lastSequence + 1));
            if(newSequence.length() > count) throw new FunctionalException("Le nombre de caractères de la nouvelle séquence '"+ newSequence
                                                        +"' est supérieur aux formats de séquence (S) imposé '"+ Arrays.toString(formatsReferenceTab) +"'.");

            for (int i = newSequence.length(); i < count; i++) {
                newSequence.insert(0, "0");
            }
            pEcritureComptable.setReference( pEcritureComptable.getJournal().getCode() + "-" + calendar.get(Calendar.YEAR) + "/" +newSequence );
            this.updateSequenceEcritureComptable(new SequenceEcritureComptable(
                                                calendar.get(Calendar.YEAR), lastSequence+1), pEcritureComptable.getJournal().getCode());
        }

    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void checkEcritureComptable(EcritureComptable pEcritureComptable) throws FunctionalException {
        this.checkEcritureComptableUnit(pEcritureComptable);
        this.checkEcritureComptableContext(pEcritureComptable);
    }


    /**
     * Vérifie que l'Ecriture comptable respecte les règles de gestion unitaires,
     * c'est à dire indépendemment du contexte (unicité de la référence, exercie comptable non cloturé...)
     *
     * @param pEcritureComptable -
     * @throws FunctionalException Si l'Ecriture comptable ne respecte pas les règles de gestion
     */
    protected void checkEcritureComptableUnit(EcritureComptable pEcritureComptable) throws FunctionalException {


        // ===== Vérification des contraintes unitaires sur les attributs de l'écriture
        Set<ConstraintViolation<EcritureComptable>> vViolations = getConstraintValidator().validate(pEcritureComptable);
        if (!vViolations.isEmpty()) {
            throw new FunctionalException("L'écriture comptable ne respecte pas les règles de gestion. + "+Arrays.toString(vViolations.toArray()),
                                          new ConstraintViolationException(
                                              "L'écriture comptable ne respecte pas les contraintes de validation",
                                              vViolations));
        }


        // ===== RG_Compta_2 : Pour qu'une écriture comptable soit valide, elle doit être équilibrée
        if (!pEcritureComptable.isEquilibree()) {
            throw new FunctionalException("L'écriture comptable n'est pas équilibrée.");
        }


        // ===== RG_Compta_3 : une écriture comptable doit avoir au moins 2 lignes d'écriture (1 au débit, 1 au crédit)
        int vNbrCredit = 0;
        int vNbrDebit = 0;
        for (LigneEcritureComptable vLigneEcritureComptable : pEcritureComptable.getListLigneEcriture()) {
            if (BigDecimal.ZERO.compareTo(ObjectUtils.defaultIfNull(vLigneEcritureComptable.getCredit(),
                                                                    BigDecimal.ZERO)) != 0) {
                vNbrCredit++;
            }
            if (BigDecimal.ZERO.compareTo(ObjectUtils.defaultIfNull(vLigneEcritureComptable.getDebit(),
                                                                    BigDecimal.ZERO)) != 0) {
                vNbrDebit++;
            }
        }
        // On test le nombre de lignes car si l'écriture à une seule ligne
        //      avec un montant au débit et un montant au crédit ce n'est pas valable
        if (pEcritureComptable.getListLigneEcriture().size() < 2
            || vNbrCredit < 1
            || vNbrDebit < 1) {
            throw new FunctionalException(
                "L'écriture comptable doit avoir au moins deux lignes : une ligne au débit et une ligne au crédit.");
        }


        // RG_Compta_5 : Vérifier que l'année dans la référence correspond à l'année  de l'écriture, idem pour le code journal.
        if (pEcritureComptable.getReference() != null) {
            // Définitiuon du format de la référence
            String formatReference = null;
            for (String format : formatsReferenceTab) {
                if (format.length() == pEcritureComptable.getReference().length()) formatReference = format;
            }
            if(formatReference == null) throw new FunctionalException("Format de la référence inconnu");
            // Récupération du code et de l'année dans la référence à partir du format
            StringBuilder annee = new StringBuilder();
            StringBuilder code = new StringBuilder();
            for (int i = 0; i < formatReference.length(); i++) {
                if (formatReference.substring(i, i + 1).equals("C"))
                    code.append(pEcritureComptable.getReference().substring(i, i + 1));
                else if (formatReference.substring(i, i + 1).equals("A"))
                    annee.append(pEcritureComptable.getReference().substring(i, i + 1));
            }
            // Vérification des correspondances (année + code)
            Calendar calendar = new GregorianCalendar();
            calendar.setTime(pEcritureComptable.getDate());
            if (calendar.get(Calendar.YEAR) != Integer.parseInt(annee.toString()))
                throw new FunctionalException("L'année de l'écriture '" + calendar.get(Calendar.YEAR)
                                                    + "' ne correspond pas avec l'année dans la réference '" + annee + "'.");
            if (!pEcritureComptable.getJournal().getCode().equals(code.toString()))
                throw new FunctionalException("Le code du journal de l'écriture '" + pEcritureComptable.getJournal().getCode()
                                                    + "' ne correspond pas avec le code dans la réference '" + code + "'.");
        }
    }


    /**
     * Vérifie que l'Ecriture comptable respecte les règles de gestion liées au contexte
     * (unicité de la référence, année comptable non cloturé...)
     *
     * @param pEcritureComptable -
     * @throws FunctionalException Si l'Ecriture comptable ne respecte pas les règles de gestion
     */
    protected void checkEcritureComptableContext(EcritureComptable pEcritureComptable) throws FunctionalException {
        // ===== RG_Compta_6 : La référence d'une écriture comptable doit être unique
        if (StringUtils.isNoneEmpty(pEcritureComptable.getReference())) {
            try {
                // Recherche d'une écriture ayant la même référence
                EcritureComptable vECRef = getDaoProxy().getComptabiliteDao().getEcritureComptableByRef(pEcritureComptable.getReference());

                // Si pas de NotFoundException => Ecriture comptable trouvé donc déja existante
                throw new FunctionalException("Une autre écriture comptable existe déjà avec la même référence => "+vECRef.toString());

            } catch (NotFoundException vEx) {
                // Dans ce cas, c'est bon, ça veut dire qu'on n'a aucune autre écriture avec la même référence.
            }
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void insertEcritureComptable(EcritureComptable pEcritureComptable) throws FunctionalException {

        this.checkEcritureComptable(pEcritureComptable);
        TransactionStatus vTS = getTransactionManager().beginTransactionMyERP();
        try {
            getDaoProxy().getComptabiliteDao().insertEcritureComptable(pEcritureComptable);
            getTransactionManager().commitMyERP(vTS);
            vTS = null;
        } finally {
            getTransactionManager().rollbackMyERP(vTS);
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void updateEcritureComptable(EcritureComptable pEcritureComptable) throws FunctionalException {

        this.checkEcritureComptable(pEcritureComptable);
        TransactionStatus vTS = getTransactionManager().beginTransactionMyERP();
        try {
            getDaoProxy().getComptabiliteDao().updateEcritureComptable(pEcritureComptable);
            getTransactionManager().commitMyERP(vTS);
            vTS = null;
        } finally {
            getTransactionManager().rollbackMyERP(vTS);
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteEcritureComptable(Integer pId) {

        TransactionStatus vTS = getTransactionManager().beginTransactionMyERP();
        try {
            getDaoProxy().getComptabiliteDao().deleteEcritureComptable(pId);
            getTransactionManager().commitMyERP(vTS);
            vTS = null;
        } finally {
            getTransactionManager().rollbackMyERP(vTS);
        }
    }


    private void insertSequenceEcritureComptable(SequenceEcritureComptable sequenceEcritureComptable, String codeJournal) throws FunctionalException {

        TransactionStatus vTS = getTransactionManager().beginTransactionMyERP();
        try {
            getDaoProxy().getComptabiliteDao().insertSequenceEcritureComptable(sequenceEcritureComptable, codeJournal);
            getTransactionManager().commitMyERP(vTS);
            vTS = null;
        } finally {
            getTransactionManager().rollbackMyERP(vTS);
        }
    }


    private void updateSequenceEcritureComptable(SequenceEcritureComptable sequenceEcritureComptable, String codeJournal) throws FunctionalException {

        TransactionStatus vTS = getTransactionManager().beginTransactionMyERP();
        try {
            getDaoProxy().getComptabiliteDao().updateSequenceEcritureComptable(sequenceEcritureComptable, codeJournal);
            getTransactionManager().commitMyERP(vTS);
            vTS = null;
        } finally {
            getTransactionManager().rollbackMyERP(vTS);
        }
    }
}
