#!/usr/bin/env python3
import bcrypt

# Générer un hash BCrypt pour le mot de passe 'admin123'
password = "admin123"
salt = bcrypt.gensalt()
hashed = bcrypt.hashpw(password.encode('utf-8'), salt)

print(f"Mot de passe: {password}")
print(f"Hash BCrypt: {hashed.decode('utf-8')}")

# Vérifier que le hash fonctionne
if bcrypt.checkpw(password.encode('utf-8'), hashed):
    print("✓ Hash vérifié avec succès!")
else:
    print("✗ Erreur de vérification du hash")
