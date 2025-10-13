const fs = require('fs');
const path = require('path');

const projectPath = 'c:\\Users\\babar\\Documents\\Projet_non_alternant_2025_MBDS_Final - Copy (4)';

function removeComments(content) {
    content = content.replace(/\/\*\*[\s\S]*?\*\//g, '');
    content = content.replace(/\/\*[\s\S]*?\*\//g, '');
    content = content.replace(/\/\/.*$/gm, '');
    content = content.replace(/\{\s*\/\*[\s\S]*?\*\/\s*\}/g, '');
    content = content.replace(/(\r?\n\s*){3,}/g, '\n\n');
    
    return content.trim();
}

function processFile(filePath) {
    try {
        const content = fs.readFileSync(filePath, 'utf8');
        const newContent = removeComments(content);
        
        if (content !== newContent) {
            fs.writeFileSync(filePath, newContent, 'utf8');
            console.log(`✓ ${filePath}`);
            return true;
        }
        return false;
    } catch (error) {
        console.error(`✗ ${filePath}: ${error.message}`);
        return false;
    }
}

function processDirectory(dir, extensions) {
    let count = 0;
    
    function walk(directory) {
        const files = fs.readdirSync(directory);
        
        for (const file of files) {
            const filePath = path.join(directory, file);
            const stat = fs.statSync(filePath);
            
            if (stat.isDirectory()) {
                walk(filePath);
            } else if (extensions.some(ext => file.endsWith(ext))) {
                if (processFile(filePath)) {
                    count++;
                }
            }
        }
    }
    
    walk(dir);
    return count;
}

console.log('=== Traitement des fichiers Java ===');
const javaCount = processDirectory(
    path.join(projectPath, 'SecurityManagementApp', 'src'),
    ['.java']
);

console.log('\n=== Traitement des fichiers JavaScript/JSX ===');
const jsCount = processDirectory(
    path.join(projectPath, 'security-management-frontend', 'src'),
    ['.js', '.jsx']
);

console.log('\n=== Traitement des fichiers CSS ===');
const cssCount = processDirectory(
    path.join(projectPath, 'security-management-frontend', 'src'),
    ['.css']
);

console.log(`\n=== TERMINE ===`);
console.log(`Fichiers modifies: ${javaCount + jsCount + cssCount}`);
