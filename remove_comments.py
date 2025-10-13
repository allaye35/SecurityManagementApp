import os
import re

project_path = r"c:\Users\babar\Documents\Projet_non_alternant_2025_MBDS_Final - Copy (4)"

def remove_comments_from_file(file_path):
    """Supprime tous les commentaires d'un fichier"""
    try:
        with open(file_path, 'r', encoding='utf-8') as f:
            content = f.read()
        
        original_content = content
        
        content = re.sub(r'/\*\*[\s\S]*?\*/', '', content)
        content = re.sub(r'/\*[\s\S]*?\*/', '', content)
        content = re.sub(r'//.*?$', '', content, flags=re.MULTILINE)
        content = re.sub(r'\{\s*/\*[\s\S]*?\*/\s*\}', '', content)
        content = re.sub(r'(\r?\n\s*){3,}', '\n\n', content)
        
        content = content.strip()
        
        if content != original_content:
            with open(file_path, 'w', encoding='utf-8') as f:
                f.write(content)
            print(f"✓ {file_path}")
            return True
        return False
    except Exception as e:
        print(f"✗ Erreur {file_path}: {e}")
        return False

def process_directory(directory, extensions):
    """Traite tous les fichiers avec les extensions specifiees"""
    count = 0
    for root, dirs, files in os.walk(directory):
        for file in files:
            if any(file.endswith(ext) for ext in extensions):
                file_path = os.path.join(root, file)
                if remove_comments_from_file(file_path):
                    count += 1
    return count

print("=== Traitement des fichiers Java ===")
java_count = process_directory(
    os.path.join(project_path, "SecurityManagementApp", "src"),
    ['.java']
)

print("\n=== Traitement des fichiers JavaScript/JSX ===")
js_count = process_directory(
    os.path.join(project_path, "security-management-frontend", "src"),
    ['.js', '.jsx']
)

print("\n=== Traitement des fichiers CSS ===")
css_count = process_directory(
    os.path.join(project_path, "security-management-frontend", "src"),
    ['.css']
)

print(f"\n=== TERMINE ===")
print(f"Fichiers modifies: {java_count + js_count + css_count}")
