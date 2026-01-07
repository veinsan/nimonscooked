# 🍔 Nimonscooked - Gastropath: Travelers of the Sacred Stack

A cooperative cooking simulation game built with Java and LibGDX, where two chefs work together to prepare and serve burgers to hungry customers under time pressure.

---

## 📋 Table of Contents

- [Overview](#overview)
- [Features](#features)
- [System Requirements](#system-requirements)
- [Installation & Running](#installation--running)
- [Controls](#controls)
- [Gameplay](#gameplay)
- [Technical Implementation](#technical-implementation)
- [Project Structure](#project-structure)
- [Build & Development](#build--development)
- [Credits](#credits)

---

## 🎮 Overview

Nimonscooked is an OOP-based cooking game where players control two chefs simultaneously to complete burger orders. The game emphasizes resource management, multitasking, and efficient workflow coordination between chefs.

**Map Type:** Burger Kitchen (Type C)  
**Duration:** 3 minutes per level  
**Objective:** Score minimum 100 points before time expires

---

## ✨ Features

### Core Gameplay
- **Dual Chef Control** - Switch between two chefs with independent inventories
- **4 Burger Recipes** - Classic, Cheeseburger, BLT, and Deluxe burgers
- **Dynamic Orders** - Random order generation with time limits and score multipliers
- **Processing Stations** - Cutting, cooking, washing, and assembly mechanics
- **Plate Management** - Clean/dirty plate cycle with automatic return system

### Technical Features
- **Concurrency** - Multi-threaded cooking, cutting, and washing with chef busy states
- **State Machine** - Ingredient states (RAW → CHOPPED → COOKED → BURNED)
- **Recipe Matching** - JSON-based recipe system with automatic dish recognition
- **Audio System** - Background music and contextual sound effects
- **Visual Effects** - Smoke, chop shake, wash bubbles, and progress indicators

### Bonus Features
- **Graphical UI** - Full LibGDX-based interface with scene2d
- **Dash System** - 3-tile dash with 2-second cooldown
- **Throw Mechanic** - 4-tile item throw with collision detection
- **Main Menu** - Landing screen, settings, and how-to-play panels
- **Pause Menu** - In-game pause with restart and exit options

---

## 💻 System Requirements

- **OS:** Windows 10/11, Linux, or macOS
- **Java:** JDK 17 or higher
- **RAM:** Minimum 2GB
- **Display:** 1280x720 minimum (1920x1080 recommended)

---

## 🚀 Installation & Running

### Option 1: Standalone Executable (Windows)

```bash
# Extract the release ZIP
unzip Gastropath-Windows.zip

# Run the executable
cd Gastropath-Windows
./Gastropath.exe
```

### Option 2: From Source

```bash
# Clone repository
git clone https://github.com/your-repo/nimonscooked.git
cd nimonscooked

# Run with Gradle
./gradlew lwjgl3:run

# Or build JAR
./gradlew lwjgl3:dist
java -jar lwjgl3/build/libs/nimonscooked-1.0.0.jar
```

---

## 🎮 Controls

### Chef Movement
- **WASD** or **Arrow Keys** - Move chef
- **SHIFT + Direction** - Dash (3 tiles, 2s cooldown)

### Actions
- **E** or **V** - Interact with stations / Pick up items
- **Q** - Drop item on floor or station
- **G** - Pick up item from floor
- **F** or **K** - Throw item (4 tiles)

### Chef Management
- **X** or **TAB** - Switch between chefs

### Game Controls
- **ESC** - Pause menu
- **Enter** - Confirm (menus)

---

## 📖 Gameplay

### Recipes

| Recipe | Ingredients | Preparation |
|--------|-------------|-------------|
| **Classic Burger** | Bun + Meat | Meat: Chop → Cook |
| **Cheeseburger** | Bun + Meat + Cheese | Meat: Chop → Cook, Cheese: Chop |
| **BLT Burger** | Bun + Lettuce + Tomato + Meat | All: Chop (except Bun), Meat: Cook |
| **Deluxe Burger** | Bun + Lettuce + Meat + Cheese | Lettuce & Cheese: Chop, Meat: Chop → Cook |

### Workflow

1. **Gather Ingredients** - Take from ingredient crates (I)
2. **Process Ingredients** - Chop at cutting station (3s), cook on stove (12s)
3. **Assemble Dish** - Combine ingredients on clean plate at assembly station
4. **Serve** - Deliver to serving counter for order validation
5. **Clean Plates** - Wash dirty plates at washing station (3s per plate)

### Scoring

- **Correct Order:** +120 points (base)
- **Speed Bonus:** ×1.5 (fast), ×1.0 (medium), ×0.7 (slow)
- **Wrong Order:** -50 points
- **Expired Order:** -50 points

### Game Over Conditions

- **Time Up:** Level ends, score evaluated (minimum 100 to pass)
- **5 Failed Orders:** Immediate game over

---

## 🏗️ Technical Implementation

### OOP Concepts

- **Inheritance** - `Item` → `Ingredient`, `Dish`, `KitchenUtensil`
- **Abstract Classes** - `Station`, `Item` base classes
- **Interfaces** - `Preparable`, `CookingDevice` for type contracts
- **Polymorphism** - Station `interact()` method overriding
- **Generics** - `List<Recipe>`, `ArrayList<Order>`, `Stack<Plate>`
- **Exceptions** - Try-catch for file I/O, JSON parsing, resource loading
- **Collections** - HashMap (station registry), ArrayList (orders), Stack (plates)
- **Concurrency** - ExecutorService thread pool for cooking/cutting/washing

### Design Patterns

1. **Singleton** - GameManager, MapManager, ResourceManager, AudioManager
2. **Factory** - StationFactory for station creation from map symbols
3. **Observer** - OrderManager notifies HudRenderer on order changes
4. **MVC** - Model (entities), View (renderers), Controller (input handlers)
5. **State** - MenuState enum for menu navigation

### SOLID Principles

1. **Single Responsibility** - Each class has one clear purpose (e.g., CookingStation only handles cooking)
2. **Open/Closed** - Station classes open for extension (new station types) but closed for modification
3. **Liskov Substitution** - All Station subclasses can be used wherever Station is expected
4. **Interface Segregation** - Preparable and CookingDevice interfaces split concerns
5. **Dependency Inversion** - High-level modules depend on abstractions (interfaces), not concrete implementations

### Concurrency Details

**Thread Pool:** `Executors.newFixedThreadPool(4)` in GameManager

**Concurrent Processes:**
- **Cooking** - Auto-cook thread (12s → COOKED, 24s → BURNED), chef not locked
- **Cutting** - Auto-chop thread (3s), chef locked via `setBusy(true)`
- **Washing** - Auto-wash thread (3s per plate), chef locked
- **Plate Return** - Scheduled task (10s delay) after serving

**Thread Safety:**
- All state modifications via `Gdx.app.postRunnable()` to main thread
- No synchronized blocks needed (single-threaded state updates)
- Graceful shutdown with `threadPool.awaitTermination()`

---

## 📂 Project Structure

```
nimonscooked/
├── core/src/main/java/com/nimonscooked/
│   ├── config/                 # Game constants
│   │   └── GameConfig.java
│   ├── controller/             # Input handling
│   │   ├── InputHandler.java
│   │   └── PlayerController.java
│   ├── factory/                # Object creation
│   │   └── StationFactory.java
│   ├── manager/                # Singleton managers
│   │   ├── GameManager.java
│   │   ├── MapManager.java
│   │   ├── OrderManager.java
│   │   ├── ResourceManager.java
│   │   └── AudioManager.java
│   ├── model/                  # Game entities
│   │   ├── entity/
│   │   │   └── Chef.java
│   │   ├── item/
│   │   │   ├── Item.java
│   │   │   ├── Ingredient.java
│   │   │   ├── Dish.java
│   │   │   └── utensil/
│   │   │       ├── Plate.java
│   │   │       └── FryingPan.java
│   │   ├── order/
│   │   │   ├── Order.java
│   │   │   └── Recipe.java
│   │   └── station/
│   │       ├── Station.java
│   │       ├── CuttingStation.java
│   │       ├── CookingStation.java
│   │       ├── AssemblyStation.java
│   │       ├── WashingStation.java
│   │       ├── ServingCounter.java
│   │       ├── PlateStorage.java
│   │       └── TrashStation.java
│   ├── util/                   # Utilities
│   │   └── Position.java
│   ├── view/                   # Rendering
│   │   ├── renderer/
│   │   │   ├── WorldRenderer.java
│   │   │   └── HudRenderer.java
│   │   └── screens/
│   │       ├── MainMenuScreen.java
│   │       └── GameScreen.java
│   └── NimonscookedGame.java  # Main entry point
├── lwjgl3/src/main/java/      # Desktop launcher
├── assets/                     # Game resources
│   ├── data/
│   │   ├── recipes.json
│   │   └── map_c.txt
│   ├── ui/                     # UI textures
│   ├── ingredients/            # Ingredient sprites
│   ├── stations/               # Station sprites
│   ├── chefs/                  # Chef sprites
│   ├── music/                  # Background music
│   └── sfx/                    # Sound effects
└── build.gradle                # Gradle build config
```

---

## 🔨 Build & Development

### Gradle Tasks

```bash
# Run game
./gradlew lwjgl3:run

# Build distributable JAR
./gradlew lwjgl3:dist

# Clean build artifacts
./gradlew clean

# Run tests
./gradlew test
```

### Configuration

Edit `GameConfig.java` to adjust:
- Timer duration, scoring rules
- Processing times (cook, chop, wash)
- Dash/throw distances
- Audio volumes
- File paths

Edit `recipes.json` to modify:
- Recipe names and ingredients
- Required ingredient states
- Dish textures

---

## 👥 Credits

**Development Team:** Kelompok N
**Course:** IF2010 - Object-Oriented Programming  
**Institution:** Institut Teknologi Bandung  
**Academic Year:** 2025/2026

### Technologies Used
- **Java 17** - Core language
- **LibGDX 1.12.1** - Game framework
- **Gson 2.10.1** - JSON parsing
- **Gradle 8.14** - Build automation

### Third-Party Assets
- Fonts: Roboto Mono, Inter (Google Fonts)
- Music & SFX: [Attribution if applicable]
- Sprites: [Attribution if applicable]

---

## 📄 License

This project is developed for educational purposes as part of IF2010 coursework.

---

## 🐛 Known Issues

- Occasional plate visual duplication after rapid serving (cosmetic only)
- Audio may stutter on first play (LibGDX asset loading)

---

## 🔮 Future Improvements

- Additional map types (Sushi, Pasta, Pizza)
- Multiplayer support (2-4 players)
- Level progression system
- Leaderboard integration
- Random map generator

---

**Developed with ☕ and 🍔 by Kelompok N K-01**
