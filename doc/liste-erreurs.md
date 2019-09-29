# Liste des erreurs

### Model
*	src/main/java/com/dummy/myerp/model/bean/comptabilite/EcritureComptable#reference : @Pattern => code dans le regexp erroné.
*	src/main/java/com/dummy/myerp/model/bean/comptabilite/EcritureComptable#getTotalCredit : Utilisation de _getDebit_ dans le calcul du crédit => remplacement par _getCredit_
*	src/main/java/com/dummy/myerp/model/bean/comptabilite/EcritureComptable#isEquilibree : Utilisation de _equals_ innapropriée avec un BigDecimal => remplacement par _compareTo_

### Consumer
*	src/main/resrouces/com/dummy/myerp/consumer/sqlContext.xml : SQLinsertListLigneEcritureComptable => Oubli d'une virgule entre debit et credit

### Business
*	src/main/java/com/dummy/myerp/business/impl/manager/ComptabiliteManagerImpl#updateEcritureComptable : Aucune vérification des règles qui incombe une _Ecriture_ _comptable_ => rajout de _checkEcritureComptable_ en tout début de méthode
