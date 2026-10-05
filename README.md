# Ghardari (گهرڌاري / گھرداری) 📱🏠

**Ghardari** is a 100% offline-first native Android application crafted specifically for households and homemakers. It provides effortless tracking of daily cash flow, informal lending/borrowing (*Khata* with tailors, shopkeepers, friends, and family), monthly bills (school fees, rickshaw, milkman, electricity), and a dedicated monthly grocery and ration shopping list.

---

## 🌟 Key Features

### 1. 🔤 Trilingual with Sindhi-First Native RTL (سنڌي، اردو، English)
* **Sindhi (سنڌي)** as the default primary language with full native RTL (Right-to-Left) typography.
* Instant 1-tap language switching between **سنڌي**, **اردو**, and **English**.
* Carefully localized terminology (*کاتو ۽ اڌارو*, *مهيني جا خرچ*, *حساب صاف*, *راشن لسٽ*).

### 2. ⚡ 1-Click Fast Entry (Designed for Speed)
* No tedious multi-page forms.
* Giant numeric keypad + 1-tap quick preset chips (`[ ٻارن جي فيس ]`, `[ راشن ]`, `[ کير وارو ]`, `[ بجلي بل ]`, `[ بي سي ]`).
* Record in under 3 seconds and edit details anytime later.

### 3. 🤝 اڌارو ۽ کاتو / Khata (Lending, Borrowing & Settlements)
* Dedicated party ledgers for individuals and vendors (e.g. *Miss XYZ tailor*, *Amaar*, *Milkman*).
* Color-coded running balances:
  * 🔴 **ڏيڻا آهن / We Owe** (Payables / Tailors / Vendors)
  * 🟢 **وٺڻا آهن / They Owe** (Receivables / Loans Given)
  * ⚪ **حساب صاف / Cleared** (1-tap full settlement)
* **1-Tap WhatsApp Receipt**: Generates a neat Sindhi/Urdu summary statement to share via WhatsApp with one click.

### 4. 🛒 ماهوار راشن ۽ سودا سلف (Monthly Grocery & Ration List)
* Tailored for household needs:
  * Items with exact quantities and units (e.g. *کنڊ 3 kg*, *اٽو 10 kg*, *شيمپو 500 ml*, *تيل/گيهه 5 kg*).
  * Checkbox to mark as purchased (`ورتو` ✅) and record actual cost.
  * **"خرچ ۾ شامل ڪريو" (Add to Monthly Expenses)**: 1-click rolls up the total grocery expense directly into this month's budget without re-typing.

### 5. 🏷️ User-Defined Custom Categories (ڪسٽم ڪيٽيگريون)
* Create unlimited custom categories with personalized colors and icons.

### 6. 🔒 100% Offline-First & Data Backup
* Powered by local **SQLite** (`SQLiteOpenHelper`) — zero internet required, zero cloud latency.
* 1-Click **JSON Backup Export** to share via WhatsApp or Google Drive so family data is never lost.

---

## 📱 Screenshots / Navigation
| Tab | Feature | Description |
| :--- | :--- | :--- |
| **مهيني جا خرچ** | Monthly Expenses & Budget | Inflows, Outflows, Remaining Balance, Category filters |
| **اڌارو ۽ کاتو** | Khata & Lending Ledger | Party cards, Partial payments, Account clearing |
| **راشن لسٽ** | Grocery & Ration Checklist | Shopping checklist with kg/liters/units and cost roll-up |
| **رپورٽ ۽ بيڪ اپ** | Reports & Settings | Language switcher (Sindhi/Urdu/English), JSON backup |

---

## 🛠️ Architecture & Tech Stack

* **Platform**: Native Android (Min SDK 24 / Target SDK 35 / Compile SDK 36)
* **Language**: Kotlin 2.4 (JVM 17)
* **UI**: Jetpack Compose + Material 3 (Dynamic RTL)
* **Architecture**: MVVM (Model-View-ViewModel) + Repository Pattern + StateFlow
* **Local Database**: SQLite (`SQLiteOpenHelper`)
* **Serialization & Backup**: Gson
* **Build System**: Gradle 8.13 + AGP 8.13

---

## 🚀 Building & Running

### Prerequisites
* JDK 17
* Android SDK (API 35+)

### Build APK
```bash
# Clone the repository
git clone https://github.com/sajjad22/ghardari.git
cd ghardari

# Build debug APK
./gradlew assembleDebug

# Output APK path:
# app/build/outputs/apk/debug/app-debug.apk
```

---

## 📄 License
This project is open-source and available under the [MIT License](LICENSE).
