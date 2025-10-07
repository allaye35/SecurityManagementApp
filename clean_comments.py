import os
import re
from pathlib import Path

def remove_java_comments(content):
    """Supprime tous les commentaires Java (// et /* */)"""
    # Supprimer les commentaires multilignes /* ... */
    content = re.sub(r'/\*[\s\S]*?\*/', '', content)
    
    # Supprimer les commentaires JavaDoc /** ... */
    content = re.sub(r'/\*\*[\s\S]*?\*/', '', content)
    
    # Supprimer les commentaires de fin de ligne //
    lines = content.split('\n')
    cleaned_lines = []
    for line in lines:
        # Ne pas toucher aux lignes qui sont complètement commentées comme package/import
        if line.strip().startswith('//') and not any(keyword in line for keyword in ['package', 'import', 'src/']):
            continue
        # Supprimer les commentaires en fin de ligne mais garder le code
        if '//' in line and not line.strip().startswith('//'):
            # Trouver le // et couper là
            idx = line.find('//')
            # Vérifier que ce n'est pas dans une string
            before = line[:idx]
            if before.count('"') % 2 == 0 and before.count("'") % 2 == 0:
                line = line[:idx].rstrip()
        cleaned_lines.append(line)
    
    content = '\n'.join(cleaned_lines)
    
    # Supprimer les lignes vides multiples
    content = re.sub(r'\n\s*\n\s*\n+', '\n\n', content)
    
    return content

def remove_js_comments(content):
    """Supprime tous les commentaires JavaScript/JSX (// et /* */)"""
    # Supprimer les commentaires multilignes /* ... */
    content = re.sub(r'/\*[\s\S]*?\*/', '', content)
    
    # Supprimer les commentaires de fin de ligne //
    lines = content.split('\n')
    cleaned_lines = []
    for line in lines:
        # Ne pas toucher aux lignes avec src/ ou import
        if line.strip().startswith('//') and not any(keyword in line for keyword in ['src/', 'import', 'http']):
            continue
        # Supprimer les commentaires en fin de ligne mais garder le code
        if '//' in line and not line.strip().startswith('//'):
            idx = line.find('//')
            before = line[:idx]
            # Compter les guillemets et backticks
            if (before.count('"') % 2 == 0 and before.count("'") % 2 == 0 and 
                before.count('`') % 2 == 0):
                line = line[:idx].rstrip()
        cleaned_lines.append(line)
    
    content = '\n'.join(cleaned_lines)
    
    # Supprimer les lignes vides multiples
    content = re.sub(r'\n\s*\n\s*\n+', '\n\n', content)
    
    return content

def add_minimal_comments_java(content, filename):
    """Ajoute quelques commentaires minimalistes et naturels pour Java"""
    lines = content.split('\n')
    result = []
    
    for i, line in enumerate(lines):
        # Ajouter un commentaire avant les classes principales
        if i > 0 and '@Service' in lines[i-1] and 'public class' in line:
            indent = len(line) - len(line.lstrip())
            result.append(' ' * indent + '// Service métier')
        elif i > 0 and '@RestController' in lines[i-1] and 'public class' in line:
            indent = len(line) - len(line.lstrip())
            result.append(' ' * indent + '// Contrôleur REST')
        elif i > 0 and '@Repository' in lines[i-1] and 'public interface' in line:
            indent = len(line) - len(line.lstrip())
            result.append(' ' * indent + '// Repository JPA')
        
        result.append(line)
    
    return '\n'.join(result)

def add_minimal_comments_js(content, filename):
    """Ajoute quelques commentaires minimalistes et naturels pour JS/JSX"""
    lines = content.split('\n')
    result = []
    
    for i, line in enumerate(lines):
        # Ajouter un commentaire avant les fonctions de service importantes
        if 'export const' in line and 'Service' in filename:
            if 'create' in line.lower() or 'update' in line.lower() or 'delete' in line.lower():
                indent = len(line) - len(line.lstrip())
                # Pas de commentaire, juste garder propre
        
        result.append(line)
    
    return '\n'.join(result)

def process_file(filepath):
    """Traite un fichier pour supprimer les commentaires"""
    try:
        filepath_str = str(filepath)
        with open(filepath_str, 'r', encoding='utf-8') as f:
            content = f.read()
        
        original_content = content
        filename = os.path.basename(filepath_str)
        
        # Traiter selon l'extension
        if filepath_str.endswith('.java'):
            content = remove_java_comments(content)
            content = add_minimal_comments_java(content, filename)
        elif filepath_str.endswith(('.js', '.jsx')):
            content = remove_js_comments(content)
            content = add_minimal_comments_js(content, filename)
        else:
            return False
        
        # Ne sauvegarder que si le contenu a changé
        if content != original_content:
            with open(filepath_str, 'w', encoding='utf-8') as f:
                f.write(content)
            print(f"✓ Nettoyé: {filepath_str}")
            return True
        return False
    except Exception as e:
        print(f"✗ Erreur avec {str(filepath)}: {e}")
        return False

def main():
    base_path = Path(__file__).parent
    
    # Traiter le backend Java
    backend_path = base_path / 'SecurityManagementApp' / 'src' / 'main' / 'java'
    java_files = list(backend_path.rglob('*.java'))
    
    # Traiter le frontend JS/JSX
    frontend_path = base_path / 'security-management-frontend' / 'src'
    js_files = list(frontend_path.rglob('*.js')) + list(frontend_path.rglob('*.jsx'))
    
    all_files = java_files + js_files
    
    print(f"🔍 {len(java_files)} fichiers Java trouvés")
    print(f"🔍 {len(js_files)} fichiers JS/JSX trouvés")
    print(f"📝 Total: {len(all_files)} fichiers à traiter\n")
    
    processed = 0
    for filepath in all_files:
        if process_file(filepath):
            processed += 1
    
    print(f"\n✅ Terminé! {processed}/{len(all_files)} fichiers modifiés")

if __name__ == '__main__':
    main()
