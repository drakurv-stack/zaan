import os
import re

screens_dir = "app/src/main/java/com/orbit/recovery/ui/screens"

def fix_lazy_column(content):
    # Find all LazyColumn(modifier = ... .padding(horizontal = 20.dp))
    # We want to remove .padding(horizontal = 20.dp) from modifier and add contentPadding = PaddingValues(horizontal = 20.dp)
    
    # Regex to find LazyColumn with .padding(horizontal = 20.dp) before the closing parenthesis
    # This is tricky, but we can do a simple text replace for the specific known cases
    
    # HomeScreen.kt case:
    content = content.replace(
        "modifier = Modifier\n                .fillMaxSize()\n                .padding(paddingValues)\n                .padding(horizontal = 20.dp)\n        ) {",
        "modifier = Modifier\n                .fillMaxSize()\n                .padding(paddingValues),\n            contentPadding = PaddingValues(horizontal = 20.dp)\n        ) {"
    )
    
    # GardenScreen.kt case:
    content = content.replace(
        "modifier = Modifier\n                .fillMaxSize()\n                .padding(paddingValues)\n                .padding(horizontal = 20.dp)\n        ) {",
        "modifier = Modifier\n                .fillMaxSize()\n                .padding(paddingValues),\n            contentPadding = PaddingValues(horizontal = 20.dp)\n        ) {"
    )
    
    # LearnScreen.kt case:
    content = content.replace(
        "modifier = Modifier\n                .fillMaxSize()\n                .padding(paddingValues)\n                .padding(horizontal = 20.dp)\n        ) {",
        "modifier = Modifier\n                .fillMaxSize()\n                .padding(paddingValues),\n            contentPadding = PaddingValues(horizontal = 20.dp)\n        ) {"
    )

    # ScreenTimeScreen.kt case:
    content = content.replace(
        "modifier = Modifier\n                    .fillMaxSize()\n                    .padding(paddingValues)\n                    .padding(horizontal = 20.dp)\n            ) {",
        "modifier = Modifier\n                    .fillMaxSize()\n                    .padding(paddingValues),\n                contentPadding = PaddingValues(horizontal = 20.dp)\n            ) {"
    )
    
    # LessonDetailScreen.kt case (Column? Or LazyColumn? It's a Column probably, wait, grep said LazyColumn for LessonDetailScreen line 90)
    # Let's just do a generic replace if possible.
    return content

def remove_inner_orbit_card_padding(content):
    # Replace Column(modifier = Modifier.padding(16.dp)) inside OrbitCard with Column()
    # It might be 20.dp or 16.dp
    
    # A simple regex to catch Column(modifier = Modifier.padding(16.dp)) when it's the direct child of OrbitCard
    # But since it's hard to parse AST with regex, we can just replace specific known ones or do a general replace:
    
    # Actually, we can just replace `Column(modifier = Modifier.padding(16.dp)) {` with `Column {` 
    # but ONLY if we know it's double padding. Wait, what if it's NOT inside OrbitCard?
    # Let's replace `OrbitCard(...) {\n                Column(modifier = Modifier.padding(16.dp)) {`
    
    pattern1 = r'(OrbitCard\([^)]*\)\s*\{\s*)Column\(\s*modifier\s*=\s*Modifier\.padding\([^)]+\)\s*\)\s*\{'
    content = re.sub(pattern1, r'\1Column {', content)
    
    # Also if OrbitCard has no modifier arguments:
    pattern2 = r'(OrbitCard\s*\{\s*)Column\(\s*modifier\s*=\s*Modifier\.padding\([^)]+\)\s*\)\s*\{'
    content = re.sub(pattern2, r'\1Column {', content)
    
    return content

def remove_header_padding(content):
    # "no extra start/end padding beyond what the parent Column already provides"
    # Text("Skip", ..., modifier = Modifier.clickable { onSkip() }.padding(8.dp))
    # Let's just remove .padding(8.dp) from Skip and Back texts in headers.
    content = content.replace(".clickable { onSkip() }.padding(8.dp)", ".clickable { onSkip() }")
    content = content.replace(".clickable { onNavigateBack() }.padding(8.dp)", ".clickable { onNavigateBack() }")
    return content

for filename in os.listdir(screens_dir):
    if filename.endswith(".kt"):
        filepath = os.path.join(screens_dir, filename)
        with open(filepath, "r", encoding="utf-8") as f:
            content = f.read()
            
        original_content = content
        
        content = fix_lazy_column(content)
        content = remove_inner_orbit_card_padding(content)
        content = remove_header_padding(content)
        
        if content != original_content:
            print(f"Updated {filename}")
            with open(filepath, "w", encoding="utf-8") as f:
                f.write(content)
