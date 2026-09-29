with open("app/src/main/java/com/orbit/recovery/ui/screens/HomeScreen.kt", "r", encoding="utf-8") as f:
    content = f.read()

# Fix Problem 2: LazyColumn padding
old_lazy = """        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(horizontal = 20.dp)
        ) {"""

new_lazy = """        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(horizontal = 20.dp, bottom = 16.dp)
        ) {"""

content = content.replace(old_lazy, new_lazy)

with open("app/src/main/java/com/orbit/recovery/ui/screens/HomeScreen.kt", "w", encoding="utf-8") as f:
    f.write(content)
print("Updated HomeScreen.kt")
