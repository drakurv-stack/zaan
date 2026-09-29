import os, re

screens_dir = "app/src/main/java/com/orbit/recovery/ui/screens"
imports_to_add = [
    "import androidx.compose.foundation.Image",
    "import androidx.compose.ui.layout.ContentScale",
    "import androidx.compose.ui.res.painterResource",
    "import com.orbit.recovery.R"
]

def add_imports(content):
    # Check if we need to add imports
    if "import androidx.compose.foundation.Image" in content:
        return content
    
    # Find the last import
    lines = content.split('\n')
    last_import_idx = -1
    for i, line in enumerate(lines):
        if line.startswith("import "):
            last_import_idx = i
            
    if last_import_idx != -1:
        for imp in reversed(imports_to_add):
            if imp not in content:
                lines.insert(last_import_idx + 1, imp)
        return '\n'.join(lines)
    return content

for filename in os.listdir(screens_dir):
    if not filename.endswith(".kt"): continue
    
    filepath = os.path.join(screens_dir, filename)
    with open(filepath, "r", encoding="utf-8") as f:
        content = f.read()
        
    original_content = content
    
    # If the file is HomeScreen.kt
    if filename == "HomeScreen.kt":
        # Replace the existing Image if it exists, or the texts
        # Actually the user said: "Or if it looks cleaner, replace the entire left Column's label text with the logo image at height(32.dp)."
        # Let's replace the whole Column { ... } with just the Image, wait, the Column has 'Hi $userName'.
        # We must KEEP the 'Hi $userName'.
        # So we want:
        # Image(...) height 32.dp
        # Text(text = "Hi $userName"...)
        # We can regex replace the Column contents.
        pattern = r'Image\(\s*painter\s*=\s*painterResource\(id\s*=\s*R\.drawable\.orbit_logo\),\s*contentDescription\s*=\s*"Orbit",\s*modifier\s*=\s*Modifier\.height\(24\.dp\)\.wrapContentWidth\(\),\s*contentScale\s*=\s*ContentScale\.Fit\s*\)\s*Text\(\s*text\s*=\s*"Your recovery dashboard"[^)]+\)'
        replacement = """Image(
                            painter = painterResource(id = R.drawable.orbit_logo),
                            contentDescription = "Orbit",
                            modifier = Modifier.height(32.dp).wrapContentWidth(),
                            contentScale = ContentScale.Fit
                        )"""
        content = re.sub(pattern, replacement, content)
        
        # If it hasn't been changed yet (e.g. text instead of Image)
        pattern2 = r'Text\(\s*text\s*=\s*"ORBIT"[^)]+\)\s*Text\(\s*text\s*=\s*"Your recovery dashboard"[^)]+\)'
        content = re.sub(pattern2, replacement, content)
        
    elif filename == "WelcomeScreen.kt":
        # height 28.dp for WelcomeScreen
        pattern = r'Text\(\s*text\s*=\s*"ORBIT \/ RECOVERY COMPANION PROTOTYPE"[^)]+\)'
        replacement = """Image(
                    painter = painterResource(id = R.drawable.orbit_logo),
                    contentDescription = "Orbit Logo",
                    modifier = Modifier.height(28.dp).wrapContentWidth(),
                    contentScale = ContentScale.Fit
                )"""
        content = re.sub(pattern, replacement, content)
        
    elif filename in ["PersonalizationScreen.kt", "PanicScreen.kt"]:
        # The prompt says: "These screens have a top bar Row. Add the logo image on the left side at height(24.dp) alongside or replacing any plain "ORBIT" or "ORBIT / RECOVERY COMPANION" text label."
        # PersonalizationScreen has: 
        # Actually, PersonalizationScreen top bar is just a Back button and a Skip button. No ORBIT text.
        # PanicScreen top bar is just a Back button.
        # Wait, if they don't have the text, I should just ADD the logo alongside it.
        # PanicScreen has:
        # Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
        #     Text(text = "← Back"...)
        # }
        # PersonalizationScreen has:
        # Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        #     Surface(...) { Back }
        #     Text("Skip")
        # }
        
        # We can add it in the center or on the left.
        pass # I will handle these manually or with regex below

    # General replacement for other screens
    # Any Text(text = "ORBIT") or Text(text = "ORBIT / RECOVERY COMPANION PROTOTYPE")
    general_pattern1 = r'Text\(\s*text\s*=\s*"ORBIT / RECOVERY COMPANION( PROTOTYPE)?"[^)]+\)'
    general_replacement = """Image(
                    painter = painterResource(id = R.drawable.orbit_logo),
                    contentDescription = "Orbit",
                    modifier = Modifier.height(26.dp).wrapContentWidth(),
                    contentScale = ContentScale.Fit
                )"""
    content = re.sub(general_pattern1, general_replacement, content)
    
    general_pattern2 = r'Text\(\s*text\s*=\s*"ORBIT"[^)]+\)'
    content = re.sub(general_pattern2, general_replacement, content)
    
    # If content changed, add imports
    if content != original_content or "Image(" in content:
        content = add_imports(content)
        with open(filepath, "w", encoding="utf-8") as f:
            f.write(content)
        print(f"Updated {filename}")
