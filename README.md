# FolioDesk POS — Restaurant Suite (Java Swing)

A professional Restaurant Point-of-Sale system built with pure Java Swing.
No external UI frameworks. No database required to start. Runs on any machine with Java 17+.

---

## Features

- **Visual Floor Plan** — 12 tables across 3 sections (Indoor / Outdoor / Private)
  with live color-coded status (Vacant / Occupied / Bill Requested)
- **Active Folio System** — Append items to a running table session without closing it
- **KOT Engine** — Tracks new vs already-printed items; fires Kitchen Order Tickets
  with a thermal-style popup preview
- **Kitchen Display** — Dedicated KDS view showing all KOTs in reverse-chronological order
- **GST Invoicing** — GSTIN-compliant bill dialog with item-level tax breakdown
- **End-of-Day Reports** — Revenue, top dishes, waiter performance leaderboard, full bill log
- **Dark Banking Theme** — Navy/Charcoal with Electric Teal accents; easy on the eyes
  during long shifts

---

## Project Structure

```
restaurant-pos/
├── src/main/java/com/restaurantpos/
│   ├── Main.java                  ← Entry point
│   ├── model/
│   │   ├── MenuItem.java
│   │   ├── OrderItem.java
│   │   ├── Table.java
│   │   ├── TableSession.java
│   │   ├── KOT.java
│   │   └── Bill.java
│   ├── data/
│   │   ├── MenuData.java          ← All menu items + tables
│   │   └── POSStore.java          ← Central state manager (singleton)
│   ├── ui/
│   │   ├── MainFrame.java         ← Top-level window + navigation bar
│   │   ├── FloorPanel.java        ← Table grid + order view container
│   │   ├── OrderPanel.java        ← Menu panel + bill panel + actions
│   │   ├── KOTDialog.java         ← KOT ticket popup
│   │   ├── BillDialog.java        ← GST invoice popup
│   │   ├── KitchenPanel.java      ← Kitchen Display System
│   │   └── ReportsPanel.java      ← EOD analytics
│   └── util/
│       ├── Theme.java             ← All colors and fonts
│       └── UIHelper.java          ← Reusable UI component factory
├── .vscode/
│   ├── launch.json                ← Run with F5
│   └── settings.json
├── pom.xml                        ← Maven build file
├── run.bat                        ← Windows one-click launcher
├── run.sh                         ← Linux/Mac launcher
└── README.md
```

---

## Quick Start

### Prerequisites

1. **Java JDK 17 or higher**
   - Download: https://adoptium.net (Temurin is recommended)
   - Verify: `java -version` and `javac -version` both show 17+

2. **Maven 3.8+**
   - Download: https://maven.apache.org/download.cgi
   - Verify: `mvn -version`

3. **VS Code** with the **Extension Pack for Java** installed
   - Open Extensions (`Ctrl+Shift+X`) → search "Extension Pack for Java" → Install

---

### Option A — Run from VS Code (Recommended)

1. Open VS Code → `File → Open Folder` → select the `restaurant-pos` folder
2. Wait for Java Language Server to initialize (bottom status bar)
3. Press **F5** — the POS window launches immediately

---

### Option B — Run from Terminal

```bash
# Build the JAR
mvn package

# Run it
java -jar target/FolioDesk-POS.jar
```

Or use the convenience scripts:

**Windows:**
```
run.bat
```

**Linux / Mac:**
```bash
chmod +x run.sh
./run.sh
```

---

## How to Use

### Floor Plan
- Green = Vacant, Blue = Occupied, Yellow = Bill Requested
- Click any table to open its order folio

### Taking an Order
1. Click a table → Order screen opens
2. Select a waiter from the dropdown
3. Browse categories or use the search box to find items
4. Click **+** or click the item row to add it to the order
5. Adjust quantities with − / + buttons (only unprinted items can be edited)

### Firing a KOT
- Click **KOT** button — a Kitchen Order Ticket popup appears
- Only *new* (unprinted) items are sent — no duplicate cooking
- Switch to **Kitchen** tab to see all KOTs on the KDS board

### Settling a Bill
- Click **Bill Req.** to flag the table (turns Yellow)
- Click **Settle Bill** → choose payment mode (Cash / UPI / Card / Complimentary)
- GST invoice popup appears; table is cleared automatically

### Reports
- Switch to **Reports** tab for End-of-Day stats
- Click any row in the Bill Log to re-open the invoice

---

## Customizing the Menu

Edit `src/main/java/com/restaurantpos/data/MenuData.java`:

```java
menu.put("Starters", Arrays.asList(
    new MenuItem("s1", "Veg Spring Rolls", 120, 5, "Starters"),
    //           id    name               price tax  category
    new MenuItem("s6", "Prawn Cocktail",  280,  5,  "Starters")  // add new item
));
```

---

## Adding More Tables

Edit the `getTables()` method in `MenuData.java`:

```java
new Table(13, "T-13", 4, "Rooftop"),   // new section
new Table(14, "T-14", 2, "Rooftop"),
```

---

## Connecting a Database (SQLite — no server needed)

Add to `pom.xml` dependencies:
```xml
<dependency>
    <groupId>org.xerial</groupId>
    <artifactId>sqlite-jdbc</artifactId>
    <version>3.45.1.0</version>
</dependency>
```

Then replace `POSStore`'s in-memory `List<Bill>` and `List<KOT>` with JDBC calls
to a local `pos.db` file — all bills and KOTs will persist across restarts.

---

## Connecting a Thermal Printer

Use **ESC/POS** commands via `javax.print` or the `escpos-coffee` library:

```xml
<dependency>
    <groupId>com.github.anastaciocintra</groupId>
    <artifactId>escpos-coffee</artifactId>
    <version>4.1.0</version>
</dependency>
```

In `KOTDialog`, replace the popup with a direct print call to your network/USB printer.

---

## Build an Installer (jpackage — JDK 14+)

```bash
# Build the fat JAR first
mvn package

# Create a native installer (.exe on Windows, .dmg on Mac, .deb on Linux)
jpackage \
  --input target \
  --name "FolioDesk POS" \
  --main-jar FolioDesk-POS.jar \
  --main-class com.restaurantpos.Main \
  --type exe \
  --app-version 1.0 \
  --vendor "Your Hotel Name"
```

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 17 |
| UI | Java Swing (javax.swing) |
| State | Singleton POSStore (in-memory) |
| Build | Apache Maven 3.8+ |
| Packaging | jpackage / maven-shade-plugin |
| IDE | VS Code + Extension Pack for Java |
