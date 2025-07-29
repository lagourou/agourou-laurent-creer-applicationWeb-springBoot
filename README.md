# SafetyNet

## Description

Ce projet est une application Spring Boot qui fournit des services liés à la sécurité civile : alertes enfants, numéros à contacter en cas d’urgence, informations médicales, etc.

Elle expose des points d'accès (API REST) pour interagir avec des données sur les personnes, les casernes et leurs dossiers médicaux.

---

onctionnalités principales

| Contrôleur                     | Fonction                                                     |
| ------------------------------ | ------------------------------------------------------------ |
| `FirestationController`        | Associer une adresse à une caserne de pompiers               |
| `PersonController`             | Ajouter, modifier ou supprimer une personne                  |
| `MedicalRecordController`      | Gérer les dossiers médicaux                                  |
| `ChildAlertController`         | Lister les enfants à une adresse donnée                      |
| `PhoneAlertController`         | Récupérer les numéros de téléphone d’une caserne             |
| `FloodStationController`       | Lister les foyers par numéro de caserne                      |
| `CommunityEmailController`     | Obtenir les emails d’un quartier                             |
| `FireAddressController`        | Obtenir les personnes et leurs infos médicales à une adresse |
| `PersonInfolastNameController` | Infos d'une personne par nom/prénom                          |

---

## Structure du projet

- `controller` : les endpoints REST
- `dto` : objets de transfert de données (entre service et contrôleur)
- `model` : entités (Person, Firestation, MedicalRecord)
- `service` : logique métier
- `repository` : accès aux données (souvent en mémoire dans ce projet)
- `utils` : classes utilitaires si besoin
- `SafetynetApplication.java` : point d’entrée principal

---

## Prérequis

- Java
- Maven
- Spring boot
- Postman
- Données : Fichier Json

---

## Lancer l’application

1. Cloner le projet ou importer les sources :

```bash
git clone https://github.com/lagourou/agourou-laurent-creer-applicationWeb-springBoot.git
```

2. Compiler le projet avec Maven :

```bash
mvn clean install
mvn spring-boot:run
```

---

## Tests

#### Tests unitiares

Ils vérifient que les méthodes d’un service ou d’un contrôleur fonctionnent de manière isolée.

- Outils utilisés : JUnit 5, Mockito
- Localisation : **src/test/java/com/safetynet**

#### Tests d'intégration

Ils vérifient que plusieurs composants fonctionnent bien ensemble (ex. : un appel REST + traitement + réponse).

- Outils utilisés : SpringBootTest, MockMvc
- Exécution possible via des classes comme **PersonControllerIT.java**

#### Lance tous les tests

```bash
mvn verify
```

Cette commande peut aussi génerer les Rapports de tests Jacoco et Surefire mais aussi la Javadoc.

## Documentation

- [**JavaDoc**](https://lagourou.github.io/agourou-laurent-creer-applicationWeb-springBoot/target/site/apidocs/index.html) — Documentation du code Java

- [**Rapport JaCoCo**](https://lagourou.github.io/agourou-laurent-creer-applicationWeb-springBoot/target/site/jacoco/index.html) — Couverture du code

- [**Rapport Surefire**](https://lagourou.github.io/agourou-laurent-creer-applicationWeb-springBoot/target/site/surefire-report.html) — Résultats des tests
