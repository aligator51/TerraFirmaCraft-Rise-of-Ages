# TerraFirmaCraft: Rise of Ages

<p>
  <img src="docs/images/banner.png" alt="Rise of Ages Banner" width="900"/>
</p>


![Java](https://img.shields.io/badge/Java-21-orange)
![Minecraft](https://img.shields.io/badge/Minecraft-1.20.x-green)
![Forge](https://img.shields.io/badge/Forge-Modding-red)
![Gradle](https://img.shields.io/badge/Build-Gradle-blue)
![License](https://img.shields.io/badge/License-MIT-yellow)

A modular progression framework for TerraFirmaCraft-focused gameplay.

---

## 🚀 Overview

**Rise of Ages** is a progression system built as a modular architecture.

The goal is to provide:
- subject-based progression (player, group, settlement)
- institution development (smithing, cooking, etc.)
- era-based advancement
- extensibility for other mods

---

## 🧱 Architecture

```text
Service Layer      → ProgressService, SubjectService
Repository Layer   → ProgressRepository
Data Layer         → CoreSavedData
Domain Layer       → Subject, Institution, Era, Progress
```

---

## 📦 Modules

### core
Main progression engine.

Contains:
- subject system (`SubjectRef`)
- institution system (`InstitutionState`)
- era system (`EraState`)
- progression model (`ProgressEvent`, `SubjectProgressData`)
- persistence (`CoreSavedData`)
- services (`ProgressService`, `SubjectService`)
- repository layer

---

### institutions *(planned)*
- smithing
- cooking
- carpentry
- agriculture

---

### structures *(planned)*
- smithy validation
- kitchen zones
- workshops

---

## 📁 Project Structure

```text
root
├── src/
├── docs/
└── README.md
```

---

## ⚙️ Running the Project

```bash
  gradlew.bat runClient
```

---

## 🔁 Progression Flow

```java
SubjectRef subjectRef = subjectService.resolve(player);

ProgressEvent event = ProgressEvent.now(
        player.getUUID(),
        subjectRef,
        InstitutionKey.of("smithing"),
        ActivityType.SMITHING,
        5.0D,
        "tfc_anvil"
);

progressService.record(level, event);
```

---

## 🧠 Core Concepts

### Subject
Entity that receives progression:
- player
- group (future)
- settlement (future)

### Institution
Represents a progression branch:
- smithing
- cooking

### Era
Represents global development stage.

### ProgressEvent
Atomic unit of progression.

---

## 📚 Documentation

See:

docs/development/project-setup.md

---

## 🧩 Design Principles

- separation of concerns
- modular architecture
- extensibility
- no hard dependency on TFC in core

---

## 📌 Development Status

Current:
- core progression system implemented
- repository + service layer ready

Planned:
- era registry
- balancing
- UI layer
- networking sync

---

## 📄 License

This project is licensed under the MIT License.
