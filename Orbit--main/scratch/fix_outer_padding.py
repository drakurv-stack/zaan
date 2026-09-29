import re

files = [
    "app/src/main/java/com/orbit/recovery/ui/screens/WelcomeScreen.kt",
    "app/src/main/java/com/orbit/recovery/ui/screens/PersonalizationScreen.kt",
    "app/src/main/java/com/orbit/recovery/ui/screens/PanicScreen.kt"
]

for filename in files:
    with open(filename, "r", encoding="utf-8") as f:
        content = f.read()

    # The outer Column padding is usually `.padding(20.dp)` in these files right after `modifier = Modifier.fillMaxSize()`
    # Let's replace `.padding(20.dp)` with `.padding(horizontal = 20.dp)` ONLY for the outer column or just replace it since it's the main container padding.
    
    # In WelcomeScreen
    if "WelcomeScreen.kt" in filename:
        old_col = """        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {"""
        new_col = """        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {"""
        content = content.replace(old_col, new_col)
    
    # In PersonalizationScreen
    elif "PersonalizationScreen.kt" in filename:
        old_col = """        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {"""
        new_col = """        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {"""
        content = content.replace(old_col, new_col)

    # In PanicScreen
    elif "PanicScreen.kt" in filename:
        old_col = """        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),"""
        new_col = """        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),"""
        content = content.replace(old_col, new_col)

    with open(filename, "w", encoding="utf-8") as f:
        f.write(content)
    print(f"Updated {filename}")
