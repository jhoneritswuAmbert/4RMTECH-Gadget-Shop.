# 4RMTECH Gadget Shop

A website and Android application for product browsing, customer inquiries, and gadget repair appointments.

## Project folders

- 4RMTECH_Android_Studio: Android application.
- GadgetShop_Website_Connected: React and Vite website.
- gadget_shop: Main PHP API used by both applications.
- database/schema.sql: PostgreSQL database schema.
- backend: Additional password reset support files. Use gadget_shop as the main API directory.

## Requirements

- XAMPP with Apache and PHP PDO PostgreSQL support.
- PostgreSQL compatible with the supplied schema export.
- Node.js and npm for the website.
- Android Studio and Android SDK 34 for the Android application.

## Database setup

1. Create a PostgreSQL database named gadget_shop.
2. Open its Query Tool in pgAdmin and run database/schema.sql.
3. If pgAdmin rejects the pg_dump backslash commands, import the file using PostgreSQL psql instead.
4. Configure database credentials in the local API db.php file using db.example.php as a template.
5. Provision an administrator account securely. The current appointment creation endpoint expects employee_id 1.

The schema export contains legacy tables alongside the active application tables. Review dependencies before removing any table.

## PHP API setup

1. Copy the gadget_shop folder into C:\xampp\htdocs.
2. Create and configure the local db.php from db.example.php.
3. Enable PDO PostgreSQL support in PHP.
4. Start Apache and PostgreSQL.
5. Test http://localhost/gadget_shop/products.php in a browser.

## Website setup

Open a terminal inside GadgetShop_Website_Connected and run:

    npm install

Copy .env.example to .env and configure:

    VITE_API_BASE_URL=http://localhost/gadget_shop

Start the website:

    npm run dev -- --port 5173 --strictPort

Open http://localhost:5173.

## Android setup

1. Open 4RMTECH_Android_Studio in Android Studio.
2. Allow Gradle to synchronize and install the required SDK.
3. Check BASE_URL in app/src/main/java/com/fourrmtech/app/api/ApiClient.java.
4. For the standard Android emulator, use http://10.0.2.2/gadget_shop/.
5. For a physical device, use the computer's reachable LAN address and connect both devices to the same network.
6. Build and run the application.

## Password reset email

Configure private SMTP settings outside the public website directory. Never commit the SMTP password.

For the documented Windows installation, run:

    C:\xampp\php\php.exe C:\xampp\htdocs\gadget_shop\password_reset_worker.php --watch

Keep the worker running for queued password reset emails.

## Features

- Product and category browsing.
- Customer registration, login, and profile updates.
- Customer inquiries and an administrator unread indicator.
- Repair appointment requests and pending cancellation.
- Admin approval, Ready to Claim, and completion.
- Repair notes and repair total visible to the customer.
- Email verification codes for password recovery.

## Limitations

- No online ordering or online payment.
- Inquiry replies are handled through external email or phone contact.
- Appointment availability is reviewed manually.
- Public HTTPS deployment is not yet documented.

## Configuration and security

Do not commit real db.php, mail_config.php, .env files, passwords, or signing keys.

Remove or restrict diagnostic and setup scripts before public deployment. Rotate any credentials previously exposed in repository history.

## Contributors

- Jhonbert Erit
- Kenneth Tabotabo

Document each member's actual contributions when required.
