import os, re
screens_dir = "app/src/main/java/com/orbit/recovery/ui/screens"

for filename in os.listdir(screens_dir):
    if filename.endswith(".kt"):
        filepath = os.path.join(screens_dir, filename)
        with open(filepath, "r", encoding="utf-8") as f:
            content = f.read()
            
        original = content
        
        # GardenScreen etc
        content = re.sub(
            r'\.padding\(paddingValues\)\s*\.padding\(horizontal\s*=\s*20\.dp\)\s*\)\s*\{',
            r'.padding(paddingValues),\n            contentPadding = PaddingValues(horizontal = 20.dp)\n        ) {',
            content
        )
        
        # LessonDetailScreen
        content = re.sub(
            r'\.weight\(1f\)\s*\.fillMaxWidth\(\)\s*\.padding\(horizontal\s*=\s*20\.dp\)\s*\)\s*\{',
            r'.weight(1f)\n                    .fillMaxWidth(),\n                contentPadding = PaddingValues(horizontal = 20.dp)\n            ) {',
            content
        )
        
        # Double padding inner OrbitCard issue
        # The user said: "For all other screens that use a Column with .padding(20.dp), make sure inner OrbitCard composables do NOT add their own horizontal padding on top of that."
        # If any OrbitCard has .padding(horizontal = 20.dp) directly on it, remove it
        content = content.replace("OrbitCard(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp))", "OrbitCard(modifier = Modifier.fillMaxWidth())")
        content = content.replace("OrbitCard(modifier = Modifier.padding(horizontal = 20.dp).fillMaxWidth())", "OrbitCard(modifier = Modifier.fillMaxWidth())")

        if content != original:
            print(f"Updated {filename}")
            with open(filepath, "w", encoding="utf-8") as f:
                f.write(content)
