# 4RM TECH — Android Studio Project

## Setup Instructions

1. Open Android Studio → **File > New > Import Project**
2. Select this `android-source/` folder
3. Wait for Gradle sync to complete
4. Run on an emulator or device (API 24+)

## Project Structure

```
app/src/main/
  java/com/fourrmtech/app/
    activities/   — All screen Activities
    fragments/    — Customer home tab Fragments
    adapters/     — RecyclerView Adapters
    models/       — Data model classes
    utils/        — DataManager (mock data)
  res/
    layout/       — All XML layout files
    values/       — colors, strings, themes
    menu/         — Navigation menus
    drawable/     — Shapes and backgrounds
```

## Dependencies (app/build.gradle)

- `com.google.android.material:material:1.12.0`
- `androidx.constraintlayout:constraintlayout:2.1.4`
- `androidx.recyclerview:recyclerview:1.3.2`
- `com.github.bumptech.glide:glide:4.16.0`

## Navigation Flow

```
SplashActivity
  └→ LoginActivity
       ├→ CustomerHomeActivity (Customer tab)
       │    ├── HomeFragment
       │    ├── ProductsFragment → ProductDetailActivity
       │    ├── BookingsFragment
       │    └── ProfileFragment
       ├→ RegisterActivity → CustomerHomeActivity
       └→ AdminDashboardActivity (Admin tab)
            ├→ AdminProductsActivity
            ├→ AdminCategoriesActivity
            ├→ AdminAppointmentsActivity
            └→ AdminCustomersActivity

RepairServicesActivity → BookAppointmentActivity
```

## Color Scheme

| Token    | Hex       | Usage                      |
|----------|-----------|----------------------------|
| navy     | #06174A   | Primary, headers, buttons  |
| accent   | #2563EB   | Links, active states       |
| surface  | #F5F7FA   | Page backgrounds           |
| white    | #FFFFFF   | Cards, inputs              |
