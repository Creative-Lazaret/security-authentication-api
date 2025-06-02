# Guide de Démarrage - MADAEF Backend Register
## Déploiement sur Windows Server avec SQL Server

---

## 📋 Prérequis

### Logiciels requis sur Windows Server
1. **Java JDK 17** ou supérieur (JRE suffit pour l'exécution)
2. **SQL Server** (Express ou version complète)
3. **SQL Server Management Studio (SSMS)** (optionnel mais recommandé)

### Fichiers à recevoir
1. **madaef-backend-0.0.3.jar** - Le fichier JAR de l'application compilé
2. **GUIDE_DEMARRAGE_WINDOWS_SERVER.md** - Cette documentation

---

## 🚀 Étapes de Démarrage

### 1. Préparation de l'environnement Java et Maven

#### Installation Java JDK 17
```powershell
# Télécharger depuis Oracle ou utiliser OpenJDK
# Vérifier l'installation
java -version
javac -version
```

#### Vérification de Java
```powershell
# Vérifier que Java est correctement installé et accessible
java -version

# La commande doit afficher Java 17 ou supérieur
# Exemple de sortie attendue :
# java version "17.0.x" 2023-xx-xx LTS
```

### 2. Configuration de la Base de Données SQL Server

#### Création de la base de données
```sql
-- Se connecter à SQL Server Management Studio ou via sqlcmd
-- Créer la base de données
CREATE DATABASE authDb;
GO

-- Utiliser la base de données
USE authDb;
GO
```

#### Création d'un utilisateur dédié (optionnel mais recommandé)
```sql
-- Créer un login
CREATE LOGIN madaef_user WITH PASSWORD = 'VotreMotDePasseComplexe123!';
GO

-- Créer un utilisateur dans la base authDb
USE authDb;
CREATE USER madaef_user FOR LOGIN madaef_user;
GO

-- Donner les permissions nécessaires
ALTER ROLE db_owner ADD MEMBER madaef_user;
GO
```

### 3. Configuration des Variables d'Environnement

#### Création du fichier de variables d'environnement
Créer un fichier `setenv.bat` dans le répertoire du projet :

```batch
@echo off
REM Configuration pour MADAEF Backend
SET DB_URL=jdbc:sqlserver://localhost:1433;databaseName=authDb;encrypt=false;trustServerCertificate=true
SET DB_USER=madaef_user
SET DB_PASSWORD=VotreMotDePasseComplexe123!
SET JWT_SECRET=7F8djE2p5Bq9cRmN3vZ6xW4yK1aL0sT8fGhUiJwQzXlAoVbYrDtCnMe2SgP7kH5j

echo Variables d'environnement configurées pour MADAEF Backend
```

#### Alternative : Variables d'environnement système Windows
```powershell
# Via PowerShell (en tant qu'administrateur)
# Configuration Base de données
[Environment]::SetEnvironmentVariable("DB_URL", "jdbc:sqlserver://localhost:1433;databaseName=authDb;encrypt=false;trustServerCertificate=true", "Machine")
[Environment]::SetEnvironmentVariable("DB_USER", "madaef_user", "Machine")
[Environment]::SetEnvironmentVariable("DB_PASSWORD", "VotreMotDePasseComplexe123!", "Machine")

# Configuration JWT
[Environment]::SetEnvironmentVariable("JWT_SECRET", "7F8djE2p5Bq9cRmN3vZ6xW4yK1aL0sT8fGhUiJwQzXlAoVbYrDtCnMe2SgP7kH5j", "Machine")

# Configuration Mail (exemple pour Office 365)
[Environment]::SetEnvironmentVariable("MAIL_HOST", "smtp.office365.com", "Machine")
[Environment]::SetEnvironmentVariable("MAIL_PORT", "587", "Machine")
[Environment]::SetEnvironmentVariable("MAIL_USERNAME", "votre-email@votre-domaine.com", "Machine")
[Environment]::SetEnvironmentVariable("MAIL_PASSWORD", "VotreMotDePasseOffice365!", "Machine")
[Environment]::SetEnvironmentVariable("MAIL_PROTOCOL", "smtp", "Machine")
[Environment]::SetEnvironmentVariable("MAIL_SMTP_AUTH", "true", "Machine")
[Environment]::SetEnvironmentVariable("MAIL_SMTP_STARTTLS", "true", "Machine")
[Environment]::SetEnvironmentVariable("MAIL_SMTP_SSL", "false", "Machine")
[Environment]::SetEnvironmentVariable("MAIL_SMTP_SSL_TRUST", "smtp.office365.com", "Machine")
[Environment]::SetEnvironmentVariable("MAIL_FROM", "votre-email@votre-domaine.com", "Machine")
```

### 4. Déploiement de l'Application

#### Préparation du répertoire de déploiement
```powershell
# Créer le répertoire de l'application
mkdir C:\madaef-backend
cd C:\madaef-backend

# Créer les sous-répertoires
mkdir logs
mkdir config
```

#### Copie du fichier JAR
```powershell
# Copier le fichier JAR reçu dans le répertoire de déploiement
# Le fichier JAR sera nommé : madaef-backend-0.0.3.jar
copy [CHEMIN_SOURCE]\madaef-backend-0.0.3.jar C:\madaef-backend\

# Vérifier que le fichier est présent
dir C:\madaef-backend\*.jar
```

#### Création du script de configuration
Créer le fichier `setenv.bat` dans `C:\madaef-backend` :

**Pour un environnement de développement/test (avec serveur mail local) :**
```batch
@echo off
REM Configuration pour MADAEF Backend

REM Configuration Base de Données
SET DB_URL=jdbc:sqlserver://localhost:1433;databaseName=authDb;encrypt=false;trustServerCertificate=true
SET DB_USER=madaef_user
SET DB_PASSWORD=VotreMotDePasseComplexe123!

REM Configuration JWT
SET JWT_SECRET=7F8djE2p5Bq9cRmN3vZ6xW4yK1aL0sT8fGhUiJwQzXlAoVbYrDtCnMe2SgP7kH5j

REM Configuration Mail (Mode développement - MailHog/MailCatcher)
SET MAIL_HOST=localhost
SET MAIL_PORT=1025
SET MAIL_USERNAME=
SET MAIL_PASSWORD=
SET MAIL_PROTOCOL=smtp
SET MAIL_SMTP_AUTH=false
SET MAIL_SMTP_STARTTLS=false
SET MAIL_SMTP_SSL=false
SET MAIL_SMTP_SSL_TRUST=
SET MAIL_FROM=noreply@madaef.com

echo Variables d'environnement configurées pour MADAEF Backend (Mode développement)
```

**Pour un environnement de production (avec serveur mail d'entreprise) :**
```batch
@echo off
REM Configuration pour MADAEF Backend - PRODUCTION

REM Configuration Base de Données
SET DB_URL=jdbc:sqlserver://localhost:1433;databaseName=authDb;encrypt=false;trustServerCertificate=true
SET DB_USER=madaef_user
SET DB_PASSWORD=VotreMotDePasseComplexe123!

REM Configuration JWT
SET JWT_SECRET=7F8djE2p5Bq9cRmN3vZ6xW4yK1aL0sT8fGhUiJwQzXlAoVbYrDtCnMe2SgP7kH5j

REM Configuration Mail (Serveur SMTP d'entreprise)
SET MAIL_HOST=smtp.votre-entreprise.com
SET MAIL_PORT=587
SET MAIL_USERNAME=madaef@votre-entreprise.com
SET MAIL_PASSWORD=VotreMotDePasseMail123!
SET MAIL_PROTOCOL=smtp
SET MAIL_SMTP_AUTH=true
SET MAIL_SMTP_STARTTLS=true
SET MAIL_SMTP_SSL=false
SET MAIL_SMTP_SSL_TRUST=smtp.votre-entreprise.com
SET MAIL_FROM=madaef@votre-entreprise.com

echo Variables d'environnement configurées pour MADAEF Backend (Mode production)
```

**Pour Office 365/Outlook.com :**
```batch
@echo off
REM Configuration pour MADAEF Backend - Office 365

REM Configuration Base de Données
SET DB_URL=jdbc:sqlserver://localhost:1433;databaseName=authDb;encrypt=false;trustServerCertificate=true
SET DB_USER=madaef_user
SET DB_PASSWORD=VotreMotDePasseComplexe123!

REM Configuration JWT
SET JWT_SECRET=7F8djE2p5Bq9cRmN3vZ6xW4yK1aL0sT8fGhUiJwQzXlAoVbYrDtCnMe2SgP7kH5j

REM Configuration Mail (Office 365)
SET MAIL_HOST=smtp.office365.com
SET MAIL_PORT=587
SET MAIL_USERNAME=votre-email@votre-domaine.com
SET MAIL_PASSWORD=VotreMotDePasseOffice365!
SET MAIL_PROTOCOL=smtp
SET MAIL_SMTP_AUTH=true
SET MAIL_SMTP_STARTTLS=true
SET MAIL_SMTP_SSL=false
SET MAIL_SMTP_SSL_TRUST=smtp.office365.com
SET MAIL_FROM=votre-email@votre-domaine.com

echo Variables d'environnement configurées pour MADAEF Backend (Office 365)
```

#### Démarrage de l'application
```powershell
# Se positionner dans le répertoire de déploiement
cd C:\madaef-backend

# Exécuter les variables d'environnement
.\setenv.bat

# Méthode 1 : Démarrage simple
java -jar madaef-backend-0.0.3.jar

# Méthode 2 : Avec redirection des logs
java -jar madaef-backend-0.0.3.jar > logs\madaef.log 2>&1

# Méthode 3 : En arrière-plan (avec start)
start "MADAEF Backend" java -jar madaef-backend-0.0.3.jar

# Méthode 4 : Avec variables d'environnement explicites (si setenv.bat ne fonctionne pas)
java -DDB_URL="jdbc:sqlserver://localhost:1433;databaseName=authDb;encrypt=false;trustServerCertificate=true" -DDB_USER="madaef_user" -DDB_PASSWORD="VotreMotDePasseComplexe123!" -DJWT_SECRET="7F8djE2p5Bq9cRmN3vZ6xW4yK1aL0sT8fGhUiJwQzXlAoVbYrDtCnMe2SgP7kH5j" -jar madaef-backend-0.0.3.jar
```

### 5. Initialisation des Données de Paramétrage

#### Insertion des rôles obligatoires
```sql
USE authDb;
GO

-- Insertion des rôles
INSERT INTO roles(name) VALUES('ROLE_USER');
INSERT INTO roles(name) VALUES('ROLE_MODERATOR');
INSERT INTO roles(name) VALUES('ROLE_ADMIN');
GO
```

#### Insertion des filiales
```sql
-- Insertion des filiales
INSERT INTO filiales(name) VALUES('SDS');
INSERT INTO filiales(name) VALUES('SAPST');
INSERT INTO filiales(name) VALUES('MSE');
INSERT INTO filiales(name) VALUES('MADAEF');
GO
```

#### Insertion des données métier et domaines (RECOMMANDÉ)
**Il est fortement recommandé d'ajouter les données de référence métier pour le bon fonctionnement de l'application :**

```sql
-- Insertion des domaines d'activité
INSERT INTO domaine(libelle) VALUES('Développement Logiciel');
INSERT INTO domaine(libelle) VALUES('Infrastructure IT');
INSERT INTO domaine(libelle) VALUES('Sécurité Informatique');
INSERT INTO domaine(libelle) VALUES('Data & Analytics');
INSERT INTO domaine(libelle) VALUES('DevOps & Cloud');
INSERT INTO domaine(libelle) VALUES('Architecture Système');
INSERT INTO domaine(libelle) VALUES('Gestion de Projet');
INSERT INTO domaine(libelle) VALUES('Support Technique');
INSERT INTO domaine(libelle) VALUES('Formation & Conseil');
INSERT INTO domaine(libelle) VALUES('Qualité & Tests');
GO

-- Insertion des métiers
INSERT INTO metier(code, libelle) VALUES('DEV-JAVA', 'Développeur Java');
INSERT INTO metier(code, libelle) VALUES('DEV-NET', 'Développeur .NET');
INSERT INTO metier(code, libelle) VALUES('DEV-WEB', 'Développeur Web');
INSERT INTO metier(code, libelle) VALUES('DEV-MOBILE', 'Développeur Mobile');
INSERT INTO metier(code, libelle) VALUES('ADMIN-SYS', 'Administrateur Système');
INSERT INTO metier(code, libelle) VALUES('ADMIN-DB', 'Administrateur Base de Données');
INSERT INTO metier(code, libelle) VALUES('ARCH-SOL', 'Architecte Solution');
INSERT INTO metier(code, libelle) VALUES('ARCH-SYS', 'Architecte Système');
INSERT INTO metier(code, libelle) VALUES('CHEF-PROJ', 'Chef de Projet');
INSERT INTO metier(code, libelle) VALUES('SCRUM-MASTER', 'Scrum Master');
INSERT INTO metier(code, libelle) VALUES('PRODUCT-OWNER', 'Product Owner');
INSERT INTO metier(code, libelle) VALUES('ANALYSTE', 'Analyste Fonctionnel');
INSERT INTO metier(code, libelle) VALUES('TESTEUR', 'Testeur/QA');
INSERT INTO metier(code, libelle) VALUES('DEVOPS', 'Ingénieur DevOps');
INSERT INTO metier(code, libelle) VALUES('SEC-IT', 'Expert Sécurité IT');
INSERT INTO metier(code, libelle) VALUES('DATA-ENG', 'Ingénieur Data');
INSERT INTO metier(code, libelle) VALUES('DATA-SCI', 'Data Scientist');
INSERT INTO metier(code, libelle) VALUES('SUPPORT', 'Support Technique');
INSERT INTO metier(code, libelle) VALUES('CONSULTANT', 'Consultant IT');
INSERT INTO metier(code, libelle) VALUES('FORMATEUR', 'Formateur IT');
GO
```

#### Script de données de test complémentaires (OPTIONNEL)
```sql
-- Données supplémentaires pour les tests
-- Ajoutez d'autres domaines selon vos besoins spécifiques
INSERT INTO domaine(libelle) VALUES('Intelligence Artificielle');
INSERT INTO domaine(libelle) VALUES('Blockchain');
INSERT INTO domaine(libelle) VALUES('IoT & Systèmes Embarqués');
INSERT INTO domaine(libelle) VALUES('Cybersécurité');
INSERT INTO domaine(libelle) VALUES('Business Intelligence');
GO

-- Métiers spécialisés supplémentaires
INSERT INTO metier(code, libelle) VALUES('AI-ENG', 'Ingénieur IA');
INSERT INTO metier(code, libelle) VALUES('BLOCKCHAIN-DEV', 'Développeur Blockchain');
INSERT INTO metier(code, libelle) VALUES('IOT-ENG', 'Ingénieur IoT');
INSERT INTO metier(code, libelle) VALUES('CYBER-SEC', 'Expert Cybersécurité');
INSERT INTO metier(code, libelle) VALUES('BI-ANALYST', 'Analyste BI');
GO
```

#### Vérification des données
```sql
-- Vérifier les rôles
SELECT * FROM roles;

-- Vérifier les filiales
SELECT * FROM filiales;

-- Vérifier les domaines
SELECT * FROM domaine ORDER BY libelle;

-- Vérifier les métiers
SELECT * FROM metier ORDER BY code;

-- Compter les enregistrements
SELECT 
    (SELECT COUNT(*) FROM roles) as nb_roles,
    (SELECT COUNT(*) FROM filiales) as nb_filiales,
    (SELECT COUNT(*) FROM domaine) as nb_domaines,
    (SELECT COUNT(*) FROM metier) as nb_metiers;

-- Vérifier la structure des tables créées automatiquement
SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_TYPE = 'BASE TABLE' ORDER BY TABLE_NAME;
```

### 6. Création du Premier Utilisateur Administrateur

⚠️ **IMPORTANT** : Il est crucial d'assigner à l'utilisateur administrateur les rôles `admin` ET `user` car :
- Le rôle `admin` permet l'administration générale
- Le rôle `user` est **requis** pour accéder aux endpoints métier (missions, collaborateurs, paramétrage)
- Les contrôleurs `MadaefController`, `MissionController` et `ParametrageController` utilisent `@PreAuthorize("hasRole('USER')")` 

#### Via l'endpoint `/api/auth/signup`
Utiliser un client HTTP (Postman, curl, ou navigateur) :

**URL :** `POST http://localhost:8080/api/auth/signup`

**Headers :**
```
Content-Type: application/json
```

**Body JSON :**
```json
{
    "username": "admin",
    "nom": "Administrateur",
    "prenom": "Système",
    "email": "admin@madaef.com",
    "password": "AdminPass123!",
    "role": ["admin", "user"],
    "filiales": ["MADAEF"]
}
```

#### Via curl (si disponible sur Windows Server)
```bash
curl -X POST http://localhost:8080/api/auth/signup \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "nom": "Administrateur",
    "prenom": "Système",
    "email": "admin@madaef.com",
    "password": "AdminPass123!",
    "role": ["admin", "user"],
    "filiales": ["MADAEF"]
  }'
```

#### Via PowerShell
```powershell
$body = @{
    username = "admin"
    nom = "Administrateur"
    prenom = "Système"
    email = "admin@madaef.com"
    password = "AdminPass123!"
    role = @("admin", "user")
    filiales = @("MADAEF")
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://localhost:8080/api/auth/signup" -Method Post -Body $body -ContentType "application/json"
```

#### Création d'utilisateurs métier supplémentaires (RECOMMANDÉ)
Pour tester les fonctionnalités métier, créer des utilisateurs avec différents profils :

**Utilisateur développeur :**
```json
{
    "username": "dev001",
    "nom": "Martin",
    "prenom": "Jean",
    "email": "jean.martin@madaef.com",
    "password": "DevPass123!",
    "role": ["user"],
    "filiales": ["SDS"]
}
```

**Utilisateur chef de projet :**
```json
{
    "username": "chef001",
    "nom": "Dupont",
    "prenom": "Marie",
    "email": "marie.dupont@madaef.com",
    "password": "ChefPass123!",
    "role": ["user", "mod"],
    "filiales": ["MADAEF", "SAPST"]
}
```

### 7. Test de Connexion

#### Connexion avec l'utilisateur créé
**URL :** `POST http://localhost:8080/api/auth/signin`

**Body JSON :**
```json
{
    "username": "admin",
    "password": "AdminPass123!"
}
```

#### Test via PowerShell avec gestion des cookies
```powershell
# Étape 1 : Connexion et récupération du cookie
$loginBody = @{
    username = "admin"
    password = "AdminPass123!"
} | ConvertTo-Json

# Créer une session web pour conserver les cookies
$session = New-Object Microsoft.PowerShell.Commands.WebRequestSession

# Effectuer la connexion et capturer la réponse avec les cookies
$loginResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/signin" -Method Post -Body $loginBody -ContentType "application/json" -WebSession $session

Write-Host "Connexion réussie pour l'utilisateur :" $loginResponse.username
Write-Host "Rôles :" ($loginResponse.roles -join ", ")
Write-Host "Filiales :" ($loginResponse.filiales -join ", ")

# Étape 2 : Test de l'endpoint /me avec le cookie accessToken
$meResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/me" -Method Get -WebSession $session

Write-Host "Informations utilisateur connecté :"
Write-Host "ID :" $meResponse.id
Write-Host "Username :" $meResponse.username
Write-Host "Email :" $meResponse.email
Write-Host "Nom complet :" $meResponse.prenom $meResponse.nom
Write-Host "Filiales :" ($meResponse.filiales | ForEach-Object { $_.name }) -join ", "
```

#### Test via curl avec gestion des cookies
```bash
# Étape 1 : Connexion et sauvegarde des cookies
curl -X POST http://localhost:8080/api/auth/signin \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "AdminPass123!"
  }' \
  -c cookies.txt

# Étape 2 : Test de l'endpoint /me avec les cookies
curl -X GET http://localhost:8080/api/auth/me \
  -b cookies.txt
```

#### Test avec Postman
1. **Connexion :**
   - URL : `POST http://localhost:8080/api/auth/signin`
   - Body : JSON avec username/password
   - Vérifier que le cookie `accessToken` est automatiquement sauvegardé

2. **Test /me :**
   - URL : `GET http://localhost:8080/api/auth/me`
   - Le cookie `accessToken` doit être automatiquement envoyé
   - Pas besoin d'headers Authorization supplémentaires

#### Vérification manuelle des cookies
```powershell
# Vérifier les cookies dans la session PowerShell
$session.Cookies.GetCookies("http://localhost:8080") | ForEach-Object {
    Write-Host "Cookie:" $_.Name "=" $_.Value
    Write-Host "Domaine:" $_.Domain
    Write-Host "Chemin:" $_.Path
    Write-Host "HttpOnly:" $_.HttpOnly
    Write-Host "Secure:" $_.Secure
    Write-Host "---"
}
```

---

## 📧 Configuration du Serveur de Messagerie

### Options de Configuration Mail

L'application MADAEF Backend utilise Spring Mail pour l'envoi de notifications par email. Plusieurs options de configuration sont disponibles :

#### 1. Mode Développement/Test (MailHog/MailCatcher)
**Avantages :** Aucun serveur mail réel requis, emails capturés localement
```
MAIL_HOST=localhost
MAIL_PORT=1025
```

**Installation de MailHog (optionnel) :**
```powershell
# Télécharger MailHog depuis https://github.com/mailhog/MailHog/releases
# Exécuter MailHog.exe
# Interface web accessible sur http://localhost:8025
```

#### 2. Serveur SMTP d'Entreprise
**Configuration type pour un serveur Exchange/SMTP interne :**
```
MAIL_HOST=smtp.votre-entreprise.com
MAIL_PORT=587
MAIL_USERNAME=madaef@votre-entreprise.com
MAIL_PASSWORD=VotreMotDePasseMail123!
MAIL_SMTP_AUTH=true
MAIL_SMTP_STARTTLS=true
```

#### 3. Office 365 / Outlook.com
**Configuration pour Office 365 :**
```
MAIL_HOST=smtp.office365.com
MAIL_PORT=587
MAIL_USERNAME=votre-email@votre-domaine.com
MAIL_PASSWORD=VotreMotDePasseOffice365!
MAIL_SMTP_AUTH=true
MAIL_SMTP_STARTTLS=true
MAIL_SMTP_SSL_TRUST=smtp.office365.com
```

#### 4. Gmail (pour les tests)
**Configuration Gmail (nécessite un mot de passe d'application) :**
```
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=votre-email@gmail.com
MAIL_PASSWORD=VotreMotDePasseApplication
MAIL_SMTP_AUTH=true
MAIL_SMTP_STARTTLS=true
MAIL_SMTP_SSL_TRUST=smtp.gmail.com
```

### Variables d'Environnement Mail Disponibles

| Variable | Description | Valeur par défaut | Exemple |
|----------|-------------|-------------------|----------|
| `MAIL_HOST` | Serveur SMTP | localhost | smtp.office365.com |
| `MAIL_PORT` | Port SMTP | 1025 | 587 |
| `MAIL_USERNAME` | Nom d'utilisateur SMTP |  | user@domain.com |
| `MAIL_PASSWORD` | Mot de passe SMTP |  | MotDePasse123! |
| `MAIL_PROTOCOL` | Protocole | smtp | smtp |
| `MAIL_SMTP_AUTH` | Authentification SMTP | false | true |
| `MAIL_SMTP_STARTTLS` | STARTTLS | false | true |
| `MAIL_SMTP_SSL` | SSL | false | false |
| `MAIL_SMTP_SSL_TRUST` | Serveurs SSL de confiance |  | smtp.office365.com |
| `MAIL_FROM` | Adresse expéditeur | noreply@madaef.com | madaef@domain.com |

### Test de la Configuration Mail

Pour vérifier que la configuration mail fonctionne :

```powershell
# Vérifier les logs de l'application au démarrage
# Rechercher les messages liés à la configuration mail
Get-Content C:\madaef-backend\logs\madaef.log | Select-String -Pattern "mail"

# Les logs doivent indiquer une connexion réussie au serveur SMTP
```

### Dépannage Configuration Mail

#### Problèmes Courants

**1. Erreur de connexion SMTP**
```
Could not connect to SMTP host
```
- Vérifier `MAIL_HOST` et `MAIL_PORT`
- Vérifier le firewall Windows
- Tester la connectivité : `Test-NetConnection smtp.office365.com -Port 587`

**2. Erreur d'authentification**
```
Authentication failed
```
- Vérifier `MAIL_USERNAME` et `MAIL_PASSWORD`
- Pour Office 365 : activer l'authentification moderne
- Pour Gmail : utiliser un mot de passe d'application

**3. Erreur SSL/TLS**
```
SSL handshake failed
```
- Vérifier `MAIL_SMTP_STARTTLS` et `MAIL_SMTP_SSL`
- Ajouter le serveur dans `MAIL_SMTP_SSL_TRUST`

### Sécurité Configuration Mail

⚠️ **Bonnes pratiques de sécurité :**

1. **Comptes dédiés** : Utiliser un compte email dédié pour l'application
2. **Mots de passe d'application** : Utiliser des mots de passe d'application plutôt que les mots de passe principaux
3. **Chiffrement** : Toujours activer STARTTLS ou SSL
4. **Variables d'environnement** : Ne jamais stocker les mots de passe en dur dans les fichiers

---

## ℹ️ Informations Importantes sur l'Application

### Endpoints Métier et Sécurité
L'application dispose de plusieurs contrôleurs avec des restrictions d'accès :

#### Endpoints Publics
- `/api/auth/signin` - Connexion
- `/api/auth/signup` - Inscription
- `/api/auth/me` - Informations utilisateur connecté

#### Endpoints Réservés aux Utilisateurs avec Rôle USER
- `/api/parametrage/*` - Paramétrage (domaines, métiers, filiales, types disponibilité)
- `/api/madaef/*` - Gestion des collaborateurs
- `/api/missions/*` - Gestion des missions

### Données de Paramétrage Obligatoires
Pour le bon fonctionnement de l'application, il est **essentiel** d'insérer les données de référence :

1. **Rôles** - Obligatoire pour l'authentification
2. **Filiales** - Obligatoire pour l'assignation des utilisateurs
3. **Domaines** - Recommandé pour la classification des compétences
4. **Métiers** - Recommandé pour la gestion des profils

Sans ces données, l'application peut démarrer mais les fonctionnalités métier seront limitées.

### 🔍 Options de Test de l'Application

#### Méthode 1 : Tests via API directement (Détaillé ci-dessous)
Utiliser PowerShell, curl ou Postman pour tester les endpoints directement.

#### Méthode 2 : Tests via Interface Web (Frontend React) 🌐
**RECOMMANDÉ pour une validation complète**

Si vous avez également reçu le frontend MADAEF :
1. Déployer le frontend avec Nginx (voir `GUIDE_DEPLOIEMENT_FRONTEND_WINDOWS.md`)
2. Accéder à l'interface sur `http://localhost`
3. Tester toutes les fonctionnalités via l'IHM :
   - 🔑 **Connexion** : Page de login avec username/password
   - 🏠 **Tableau de bord** : Vue d'ensemble (/home)
   - 💼 **Missions** : Création et gestion des missions (/missions)
   - 🔍 **Recherche** : Recherche de collaborateurs (/chercherProfil)
   - 👥 **Équipe** : Gestion de mes collaborateurs (/mesCollaborateurs)

**Avantages du test via IHM :**
- ✅ Test complet de l'expérience utilisateur
- ✅ Validation de l'authentification par cookies
- ✅ Test de toutes les fonctionnalités métier
- ✅ Vérification de l'intégration frontend/backend
- ✅ Test des filtres et recherches
- ✅ Validation de la gestion des erreurs

**Credentials de test par défaut :**
- Username: `admin`
- Password: `AdminPass123!`

### Test des Endpoints
Une fois l'application démarrée et les données insérées, vous pouvez tester les endpoints métier :

#### Méthode 1 : Avec session PowerShell (Recommandée)
```powershell
# 1. Se connecter et créer une session avec cookies
$loginBody = @{
    username = "admin"
    password = "AdminPass123!"
} | ConvertTo-Json

$session = New-Object Microsoft.PowerShell.Commands.WebRequestSession
$loginResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/signin" -Method Post -Body $loginBody -ContentType "application/json" -WebSession $session

# 2. Tester les endpoints de paramétrage
$domaines = Invoke-RestMethod -Uri "http://localhost:8080/api/parametrage/domaines" -WebSession $session
$metiers = Invoke-RestMethod -Uri "http://localhost:8080/api/parametrage/metiers" -WebSession $session
$filiales = Invoke-RestMethod -Uri "http://localhost:8080/api/parametrage/filiales" -WebSession $session

Write-Host "Domaines disponibles :" ($domaines -join ", ")
Write-Host "Métiers disponibles :" ($metiers -join ", ")
Write-Host "Filiales disponibles :" ($filiales -join ", ")

# 3. Tester les endpoints de recherche de collaborateurs
$collaborateurs = Invoke-RestMethod -Uri "http://localhost:8080/api/madaef/myCollaborateurs" -WebSession $session
Write-Host "Nombre de collaborateurs trouvés :" $collaborateurs.totalElements

# 4. Tester les endpoints de missions
$missions = Invoke-RestMethod -Uri "http://localhost:8080/api/missions" -WebSession $session
Write-Host "Nombre de missions trouvées :" $missions.totalElements
```

#### Méthode 2 : Avec curl et fichier de cookies
```bash
# 1. Se connecter et sauvegarder les cookies
curl -X POST http://localhost:8080/api/auth/signin \
  -H "Content-Type: application/json" \
  -d '{"username": "admin", "password": "AdminPass123!"}' \
  -c cookies.txt

# 2. Tester les endpoints avec les cookies
curl -X GET http://localhost:8080/api/parametrage/domaines -b cookies.txt
curl -X GET http://localhost:8080/api/parametrage/metiers -b cookies.txt
curl -X GET http://localhost:8080/api/parametrage/filiales -b cookies.txt
curl -X GET http://localhost:8080/api/madaef/myCollaborateurs -b cookies.txt
curl -X GET http://localhost:8080/api/missions -b cookies.txt
```

#### Méthode 3 : Script de test complet
Créer un fichier `test-endpoints.ps1` :
```powershell
param(
    [string]$BaseUrl = "http://localhost:8080",
    [string]$Username = "admin",
    [string]$Password = "AdminPass123!"
)

Write-Host "=== Test MADAEF Backend Endpoints ===" -ForegroundColor Green

try {
    # Connexion
    Write-Host "1. Connexion..." -ForegroundColor Yellow
    $loginBody = @{ username = $Username; password = $Password } | ConvertTo-Json
    $session = New-Object Microsoft.PowerShell.Commands.WebRequestSession
    $loginResponse = Invoke-RestMethod -Uri "$BaseUrl/api/auth/signin" -Method Post -Body $loginBody -ContentType "application/json" -WebSession $session
    Write-Host "   ✓ Connexion réussie pour : $($loginResponse.username)" -ForegroundColor Green
    
    # Test /me
    Write-Host "2. Test endpoint /me..." -ForegroundColor Yellow
    $meResponse = Invoke-RestMethod -Uri "$BaseUrl/api/auth/me" -WebSession $session
    Write-Host "   ✓ Utilisateur connecté : $($meResponse.username)" -ForegroundColor Green
    
    # Test paramétrages
    Write-Host "3. Test paramétrages..." -ForegroundColor Yellow
    $domaines = Invoke-RestMethod -Uri "$BaseUrl/api/parametrage/domaines" -WebSession $session
    $metiers = Invoke-RestMethod -Uri "$BaseUrl/api/parametrage/metiers" -WebSession $session
    $filiales = Invoke-RestMethod -Uri "$BaseUrl/api/parametrage/filiales" -WebSession $session
    Write-Host "   ✓ $($domaines.Count) domaines, $($metiers.Count) métiers, $($filiales.Count) filiales" -ForegroundColor Green
    
    # Test collaborateurs
    Write-Host "4. Test collaborateurs..." -ForegroundColor Yellow
    $collaborateurs = Invoke-RestMethod -Uri "$BaseUrl/api/madaef/myCollaborateurs" -WebSession $session
    Write-Host "   ✓ $($collaborateurs.totalElements) collaborateurs trouvés" -ForegroundColor Green
    
    # Test missions
    Write-Host "5. Test missions..." -ForegroundColor Yellow
    $missions = Invoke-RestMethod -Uri "$BaseUrl/api/missions" -WebSession $session
    Write-Host "   ✓ $($missions.totalElements) missions trouvées" -ForegroundColor Green
    
    Write-Host "\n=== Tous les tests ont réussi ! ===" -ForegroundColor Green
    
} catch {
    Write-Host "❌ Erreur : $($_.Exception.Message)" -ForegroundColor Red
    Write-Host "Détails : $($_.Exception.Response.StatusCode)" -ForegroundColor Red
}
```

**Utilisation du script :**
```powershell
# Depuis le répertoire C:\madaef-backend
.\test-endpoints.ps1

# Ou avec des paramètres personnalisés
.\test-endpoints.ps1 -Username "dev001" -Password "DevPass123!"
```

---

## 🔧 Configuration Avancée

#### Création d'un script de démarrage
Créer `start-madaef.bat` dans `C:\madaef-backend` :
```batch
@echo off
cd /d "C:\madaef-backend"
call setenv.bat
java -jar madaef-backend-0.0.3.jar
```

#### Installation comme service Windows (avec NSSM)
```powershell
# Télécharger NSSM depuis https://nssm.cc/
# Installer le service
nssm install "MADAEF-Backend" "C:\madaef-backend\start-madaef.bat"

# Configurer le répertoire de travail
nssm set "MADAEF-Backend" AppDirectory "C:\madaef-backend"

# Configurer la redirection des logs
nssm set "MADAEF-Backend" AppStdout "C:\madaef-backend\logs\stdout.log"
nssm set "MADAEF-Backend" AppStderr "C:\madaef-backend\logs\stderr.log"

# Démarrer le service
nssm start "MADAEF-Backend"

# Vérifier le statut
nssm status "MADAEF-Backend"
```

### Configuration du Firewall
```powershell
# Ouvrir le port 8080
New-NetFirewallRule -DisplayName "MADAEF Backend" -Direction Inbound -Port 8080 -Protocol TCP -Action Allow
```

---

## 📊 Vérifications et Monitoring

### Vérification de l'état de l'application
```powershell
# Test de santé de l'application
Invoke-RestMethod -Uri "http://localhost:8080/api/auth/me" -Method Get
```

### Logs de l'application
Les logs sont affichés dans la console. Pour les rediriger vers un fichier :
```powershell
# Depuis le répertoire C:\madaef-backend
java -jar madaef-backend-0.0.3.jar > logs\madaef.log 2>&1

# Ou pour voir les logs en temps réel
Get-Content logs\madaef.log -Wait
```

### Surveillance des processus
```powershell
# Vérifier que Java s'exécute
Get-Process java

# Vérifier l'utilisation du port 8080
netstat -an | findstr :8080
```

---

## 🚨 Dépannage

### Problèmes courants

#### 1. Erreur de connexion à la base de données
- Vérifier que SQL Server est démarré
- Vérifier les variables d'environnement
- Tester la connectivité : `telnet localhost 1433`

#### 2. Port 8080 déjà utilisé
```powershell
# Trouver le processus utilisant le port
netstat -ano | findstr :8080

# Tuer le processus (remplacer PID par l'ID du processus)
taskkill /PID [PID] /F
```

#### 3. Erreur "Role not found" lors de la création d'utilisateur
Vérifier que les rôles ont été insérés dans la base de données.

#### 4. Variables d'environnement non reconnues
Redémarrer la session PowerShell ou l'invite de commandes après la configuration des variables.

#### 5. Problèmes d'authentification avec les cookies
**Erreur 401 Unauthorized sur /api/auth/me**
- Vérifier que le cookie `accessToken` est présent :
```powershell
# Vérifier les cookies dans la session
$session.Cookies.GetCookies("http://localhost:8080")
```
- Le cookie doit avoir les propriétés :
  - `HttpOnly = True`
  - `Path = /`
  - `Domain = localhost` (ou votre domaine)

**Le cookie n'est pas envoyé automatiquement**
- Utiliser `-WebSession $session` dans PowerShell
- Utiliser `-b cookies.txt` avec curl
- Dans Postman, vérifier que les cookies sont activés

**Expiration du cookie**
```powershell
# Vérifier l'expiration du cookie
$session.Cookies.GetCookies("http://localhost:8080") | ForEach-Object {
    Write-Host "Cookie : $($_.Name)"
    Write-Host "Expire : $($_.Expired)"
    Write-Host "Date expiration : $($_.Expires)"
}
```
- Si expiré, se reconnecter avec `/api/auth/signin`

---

## 📋 Résumé des Commandes de Démarrage Rapide

```powershell
# 1. Créer le répertoire et copier le JAR
mkdir C:\madaef-backend
copy [SOURCE]\madaef-backend-0.0.3.jar C:\madaef-backend\
cd C:\madaef-backend

# 2. Créer et configurer les variables d'environnement
# Créer le fichier setenv.bat avec les paramètres de base de données

# 3. Configurer la base de données SQL Server
# Exécuter les scripts SQL pour créer la base, les rôles et filiales

# 4. Démarrer l'application
.\setenv.bat
java -jar madaef-backend-0.0.3.jar

# 5. Créer le premier utilisateur admin
# Utiliser l'endpoint POST /api/auth/signup

# 6. Tester la connexion
# Utiliser l'endpoint POST /api/auth/signin
```

---

## 📞 Contact et Support

En cas de problème, vérifier :
1. Les logs de l'application
2. Les logs de SQL Server
3. La connectivité réseau
4. Les variables d'environnement
5. Les permissions de la base de données

L'application devrait être accessible sur `http://localhost:8080` une fois démarrée avec succès.
