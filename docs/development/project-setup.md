# TerraFirmaCraft: Rise of Ages

A modular progression framework for TerraFirmaCraft-focused gameplay.

---

## Overview

Rise of Ages is designed as a multi-module project built around a shared core progression system.

The project is intended to support:
- subject-based progression (player, group, settlement)
- institution development
- era progression
- optional integrations with external mods

---

## Modules

### core
The main module that contains:
- subject model
- institution model
- era model
- progression storage
- progression services

### institutions
Planned module for concrete institution definitions and progression rules.

### structures
Planned module for structural gameplay systems such as kitchens, smithies, and workshops.

---

## Project Structure

```text
root
├── build.gradle
├── settings.gradle
├── gradle.properties
└── src/
```

---

## Development Status

### Current focus

- building the core progression foundation
- defining subject, institution, and era state
- implementing persistence and progression services

---

## Running the project

Run the core module client:

```bash
gradlew.bat :core:runClient
```

---

## Notes

At the current stage, `core` is the main working module.  
Other modules are placeholders for future expansion.

---

# Project Setup

## Purpose

This document describes how the Rise of Ages project is structured and how new development should be organized.

---

## Project Type

The project uses a multi-module Gradle structure.

The goal is to keep the core progression system isolated from optional gameplay modules.

---

## Root Files

### settings.gradle

Defines included modules.

```gradle
rootProject.name = 'TerraFirmaCraft-Rise-of-Ages'

include 'core'

```

---

### build.gradle

Contains shared Gradle configuration for all modules.

Typical responsibilities:
- common group/version
- repositories
- Java toolchain
- shared compile settings

---

### gradle.properties

Stores shared project properties such as:
- Minecraft version
- Forge version
- mod version
- common metadata

---

## Module Responsibilities

### core

Contains the shared progression foundation:
- SubjectRef
- InstitutionState
- EraState
- SubjectProgressData
- CoreSavedData
- ProgressService
- SubjectService
- repository layer

---

### institutions

Will contain concrete institution definitions such as:
- smithing
- cooking
- carpentry
- agriculture

---

### structures

Will contain structure-related systems such as:
- smithy validation
- kitchen areas
- workshop registration

---

## Core Package Layout

```text
core/src/main/java/com/rapitor3/riseofages/
├── subject/
├── institution/
├── era/
├── progress/
├── data/
├── repository/
└── service/
```

---

## How to Add a New Core Class

1. Identify the domain area:
   - subject
   - institution
   - era
   - progress
   - data
   - service

2. Place the class into the correct package.

3. Add JavaDoc for:
   - class purpose
   - important fields
   - method responsibilities

4. Keep responsibilities narrow.

Example:
- state classes store state
- services apply logic
- repositories access storage

---

## Persistence Model

Core progression is stored in `CoreSavedData`.

This includes:
- subject progression data
- player-to-subject bindings

The storage is world-level and accessed through the server.

---

## Service Flow

Typical progression flow:

1. Resolve subject for a player
2. Create `ProgressEvent`
3. Pass the event into `ProgressService`
4. Update `SubjectProgressData`
5. Save changes through repository

---

## Example Flow

```java
SubjectRef subjectRef = subjectService.resolve(player);

ProgressEvent event = ProgressEvent.now(
        player.getUUID(),
        subjectRef,
        InstitutionKey.of("smithing"),
        ActivityType.SMITHING,
        5.0D,
        "debug_test"
);

progressService.record(level, event);
```

---

## Current Development Rules

At this stage:
- keep `core` independent from TFC-specific logic
- keep `core` independent from UI-specific logic
- keep external integrations outside core

---

## Next Planned Steps

- add era definitions and registry
- add contribution tracking
- add client sync DTOs
- add UI module support
