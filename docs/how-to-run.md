# How to Run YRRAK

## Option 1: Run the Console Game

From the project root:

```bash
./scripts/run.sh
```

This starts the playable console version.

## Option 2: Run the Prepared Demo

```bash
./scripts/demo.sh
```

This automatically plays a prepared winning route using `docs/demo-input.txt`.

## Option 3: Run the GUI Prototype

```bash
./scripts/gui.sh
```

This opens the Java Swing GUI prototype.

## Option 4: Run in IntelliJ IDEA

Console version:

1. Open `src/Main.java`.
2. Click the green run button next to `main`.

GUI prototype:

1. Open `src/gui/GuiMain.java`.
2. Click the green run button next to `main`.

## Manual Compile Command

```bash
javac -encoding UTF-8 -d out/production/YRRAK $(find src -name "*.java")
java -cp out/production/YRRAK Main
```

For the GUI:

```bash
javac -encoding UTF-8 -d out/production/YRRAK $(find src -name "*.java")
java -cp out/production/YRRAK gui.GuiMain
```
