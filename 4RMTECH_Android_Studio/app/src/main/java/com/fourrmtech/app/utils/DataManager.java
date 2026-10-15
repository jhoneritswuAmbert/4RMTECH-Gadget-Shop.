package com.fourrmtech.app.utils;

import android.content.Context;
import com.fourrmtech.app.api.AuthStore;
import android.content.SharedPreferences;

import com.fourrmtech.app.models.Appointment;
import com.fourrmtech.app.models.Category;
import com.fourrmtech.app.models.Customer;
import com.fourrmtech.app.models.Product;
import com.fourrmtech.app.R;

import java.util.ArrayList;
import java.util.List;

public class DataManager {

    private static DataManager instance;

    private List<Product> products;
    private List<Category> categories;
    private List<Appointment> appointments;
    private List<Customer> customers;

    // Currently logged-in customer
    private Customer currentUser;

    // SharedPreferences
    private SharedPreferences preferences;

    private static final String PREF_NAME = "GadgetShopPrefs";
    private static final String KEY_CUSTOMER_ID = "customer_id";

    private DataManager() {
        initData();
    }

    public static DataManager getInstance() {

        if (instance == null) {
            instance = new DataManager();
        }

        return instance;
    }

    // Initialize SharedPreferences
    public void initialize(Context context) {
        AuthStore.initialize(context);

        preferences = context.getSharedPreferences(
                PREF_NAME,
                Context.MODE_PRIVATE
        );

        loadSavedUser();
    }

    private void initData() {

        // Products
        products = new ArrayList<>();

        products.add(new Product(
                1,
                "iPhone 15 Pro",
                "Apple",
                "Smartphones",
                "6.1\" Super Retina XDR display with ProMotion, A17 Pro chip, titanium design with Action button.",
                52999,
                8,
                "https://images.unsplash.com/photo-1560617544-b4f287789e24?w=400&h=400&fit=crop"
        ));

        products.add(new Product(
                2,
                "Samsung Galaxy S24",
                "Samsung",
                "Smartphones",
                "6.2\" Dynamic AMOLED 2X, Snapdragon 8 Gen 3, 50MP triple camera system.",
                44999,
                12,
                "https://images.unsplash.com/photo-1592899677977-9c10ca588bbd?w=400&h=400&fit=crop"
        ));

        products.add(new Product(
                3,
                "MacBook Air M3",
                "Apple",
                "Laptops",
                "13.6\" Liquid Retina display, Apple M3 chip, 18-hour battery life, 8GB unified memory.",
                71999,
                5,
                "https://images.unsplash.com/photo-1525547719571-a2d4ac8945e2?w=400&h=400&fit=crop"
        ));

        products.add(new Product(
                4,
                "ASUS ROG Zephyrus G14",
                "ASUS",
                "Laptops",
                "14\" QHD 165Hz display, AMD Ryzen 9, NVIDIA RTX 4060, gaming and professional grade.",
                68999,
                3,
                "https://images.unsplash.com/photo-1541807084-5c52b6b3adef?w=400&h=400&fit=crop"
        ));

        products.add(new Product(
                5,
                "Sony WH-1000XM5",
                "Sony",
                "Accessories",
                "Industry-leading noise canceling headphones, 30-hour battery, multipoint connection.",
                18999,
                15,
                "https://images.unsplash.com/photo-1515940175183-6798529cb860?w=400&h=400&fit=crop"
        ));

        products.add(new Product(
                6,
                "AMD Ryzen 7 7700X",
                "AMD",
                "Computer Parts",
                "8-core 16-thread, 5.4GHz max boost, AM5 socket, ideal for gaming and content creation.",
                16999,
                7,
                "https://images.unsplash.com/photo-1591799264318-7e6ef8ddb7ea?w=400&h=400&fit=crop"
        ));

        products.add(new Product(
                7,
                "Logitech MX Master 3S",
                "Logitech",
                "Accessories",
                "Advanced wireless mouse, MagSpeed electromagnetic scrolling, 8K DPI sensor.",
                5499,
                20,
                "https://images.unsplash.com/photo-1527864550417-7fd91fc51a46?w=400&h=400&fit=crop"
        ));

        products.add(new Product(
                8,
                "iPad Air M2",
                "Apple",
                "Smartphones",
                "11\" Liquid Retina display, Apple M2 chip, supports Apple Pencil Pro and Magic Keyboard.",
                39999,
                6,
                "https://images.unsplash.com/photo-1544244015-0df4b3ffc6b0?w=400&h=400&fit=crop"
        ));

        // Categories
        categories = new ArrayList<>();

        categories.add(new Category(
                1,
                "Smartphones",
                "Latest smartphones and tablets from top brands",
                3,
                R.drawable.category_smartphones
        ));

        categories.add(new Category(
                2,
                "Laptops",
                "Performance laptops for work and gaming",
                2,
                R.drawable.category_laptops
        ));

        categories.add(new Category(
                3,
                "Accessories",
                "Headphones, mice, keyboards, and more",
                2,
                R.drawable.category_accessories
        ));

        categories.add(new Category(
                6,
                "Computer Parts",
                "CPUs, GPUs, RAM, and storage components",
                0,
                R.drawable.category_computer_parts
        ));
        // Appointments
        appointments = new ArrayList<>();

        appointments.add(new Appointment(
                1,
                "Maria Santos",
                "09171234567",
                "Smartphone",
                "Samsung Galaxy A54",
                "Cracked screen and broken charging port",
                "2026-09-15",
                "10:00 AM",
                Appointment.Status.APPROVED
        ));

        appointments.add(new Appointment(
                2,
                "Juan Dela Cruz",
                "09281234567",
                "Laptop",
                "HP Pavilion 15",
                "Battery not charging, keyboard keys not responding",
                "2026-09-16",
                "2:00 PM",
                Appointment.Status.PENDING
        ));

        appointments.add(new Appointment(
                3,
                "Ana Reyes",
                "09091234567",
                "Desktop/PC",
                "Custom Build",
                "No display output, possible GPU failure",
                "2026-09-10",
                "11:00 AM",
                Appointment.Status.COMPLETED
        ));

        appointments.add(new Appointment(
                4,
                "Carlo Mendoza",
                "09331234567",
                "Tablet",
                "iPad Air 4th Gen",
                "Water damage, touch screen unresponsive",
                "2026-09-12",
                "3:00 PM",
                Appointment.Status.CANCELLED
        ));

        appointments.add(new Appointment(
                5,
                "Lisa Garcia",
                "09451234567",
                "Smartphone",
                "iPhone 13",
                "Battery drains fast, overheating during use",
                "2026-09-18",
                "9:00 AM",
                Appointment.Status.PENDING
        ));

        // Customers
        customers = new ArrayList<>();

        customers.add(new Customer(
                1,
                "Maria",
                "Santos",
                "maria.santos@email.com",
                "09171234567",
                "123 Rizal Street, Makati City",
                "2026-01-15"
        ));

        customers.add(new Customer(
                2,
                "Juan",
                "Dela Cruz",
                "juan.delacruz@email.com",
                "09281234567",
                "456 Bonifacio Ave, Quezon City",
                "2026-02-20"
        ));

        customers.add(new Customer(
                3,
                "Ana",
                "Reyes",
                "ana.reyes@email.com",
                "09091234567",
                "789 Aguinaldo Blvd, Pasig City",
                "2026-03-05"
        ));

        customers.add(new Customer(
                4,
                "Carlo",
                "Mendoza",
                "carlo.mendoza@email.com",
                "09331234567",
                "09331234567",
                "2026-04-10"
        ));

        customers.add(new Customer(
                5,
                "Lisa",
                "Garcia",
                "lisa.garcia@email.com",
                "09451234567",
                "654 Luna Street, Caloocan City",
                "2026-05-22"
        ));

        // Do NOT set a default mock customer here.
        // The logged-in customer will be set by LoginActivity.
    }

    private void loadSavedUser() {

        if (preferences == null) {
            return;
        }

        int savedCustomerId =
                preferences.getInt(KEY_CUSTOMER_ID, -1);

        if (savedCustomerId == -1) {
            currentUser = null;
            return;
        }

        // Customer will be loaded from PostgreSQL
        currentUser = null;
    }

    public List<Product> getProducts() {
        return products;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public List<Appointment> getAppointments() {
        return appointments;
    }

    public List<Customer> getCustomers() {
        return customers;
    }

    public List<Product> getProductsByCategory(String category) {

        if (category.equals("All")) {
            return products;
        }

        List<Product> filtered = new ArrayList<>();

        for (Product p : products) {

            if (p.getCategory().equals(category)) {
                filtered.add(p);
            }
        }

        return filtered;
    }

    public List<Appointment> getAppointmentsByStatus(String status) {

        if (status.equals("All")) {
            return appointments;
        }

        List<Appointment> filtered = new ArrayList<>();

        for (Appointment a : appointments) {

            if (a.getStatusLabel().equals(status)) {
                filtered.add(a);
            }
        }

        return filtered;
    }

    public Product getProductById(int id) {

        for (Product p : products) {

            if (p.getId() == id) {
                return p;
            }
        }

        return null;
    }

    public void addProduct(Product product) {
        products.add(product);
    }

    public void addCategory(Category category) {
        categories.add(category);
    }

    public boolean updateCategory(
            int categoryId,
            String newName,
            String newDescription) {

        for (Category category : categories) {

            if (category.getId() == categoryId) {

                category.setName(newName);
                category.setDescription(newDescription);

                return true;
            }
        }

        return false;
    }

    public boolean deleteCategory(int categoryId) {

        for (int i = 0; i < categories.size(); i++) {

            if (categories.get(i).getId() == categoryId) {

                categories.remove(i);

                return true;
            }
        }

        return false;
    }

    public void addAppointment(Appointment appointment) {
        appointments.add(appointment);
    }

    public int getPendingAppointmentCount() {

        int count = 0;

        for (Appointment a : appointments) {

            if (a.getStatus() == Appointment.Status.PENDING) {
                count++;
            }
        }

        return count;
    }

    // Get currently logged-in customer
    public Customer getCurrentUser() {
        return currentUser;
    }

    // Set currently logged-in customer
    public void setCurrentUser(Customer customer) {

        currentUser = customer;

        if (preferences != null && customer != null) {

            preferences.edit()
                    .putInt(
                            KEY_CUSTOMER_ID,
                            customer.getId()
                    )
                    .apply();
        }
    }

    // Set customer loaded from PostgreSQL
    public void setCurrentUserFromApi(Customer customer) {

        currentUser = customer;

        if (preferences != null && customer != null) {

            preferences.edit()
                    .putInt(
                            KEY_CUSTOMER_ID,
                            customer.getId()
                    )
                    .apply();
        }
    }

    public int getSavedCustomerId() {

        if (preferences == null) {
            return -1;
        }

        return preferences.getInt(
                KEY_CUSTOMER_ID,
                -1
        );
    }

    // Clear currently logged-in customer
    public void logout() {
        AuthStore.logout();

        currentUser = null;

        if (preferences != null) {

            preferences.edit()
                    .remove(KEY_CUSTOMER_ID)
                    .apply();
        }
    }
}