import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Scanner;

abstract class Waste {
    static final String[] recyclable = {"paper", "cardboard", "glass", "plastic bottles", "aluminum cans",
            "steel cans", "newspaper", "tin foil", "plastic bags", "textiles"};

    static final String[] nonRecyclable = {"plastic bags", "polystyrene", "disposable coffee cups", "broken glass",
            "bubble wraps", "paper towels", "tissues", "cotton balls", "leather products", "fruit peel"};

    static final String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};

    static final int MAX_DAYS = 7;
    static final int MAX_WASTE_TYPES = Math.max(recyclable.length, nonRecyclable.length);

    abstract void insertData(String day, String wasteType);

    abstract void getDayAndWaste();

    abstract void amount();

    abstract void maxDay();

    abstract void showWasteByDay();
}

class WasteDisposed extends Waste {
    private String[][] wasteData = new String[Waste.MAX_DAYS][Waste.MAX_WASTE_TYPES];
    private int[] wasteCount = new int[Waste.MAX_DAYS];
    private int recyclableCount = 0;
    private int nonRecyclableCount = 0;
    private Scanner scanner = new Scanner(System.in);

    private Connection getConnection() {
        try {
            String driver = "org.postgresql.Driver";
            String databaseUrl = "jdbc:postgresql://localhost:5432/oop";
            String userName = "postgres";
            String password = "venasaur123";

            Class.forName(driver);
            Connection conn = DriverManager.getConnection(databaseUrl, userName, password);

            System.out.println("Database connected, now you can do more operations");
            return conn;
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("Some error: " + e);
        }
        return null;
    }

    @Override
    void insertData(String day, String wasteType) {
        try {
            Connection connection = getConnection();
            String query = "INSERT INTO users (day, waste_type) VALUES (?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);

            preparedStatement.setString(1, day);
            preparedStatement.setString(2, wasteType);

            int result = preparedStatement.executeUpdate();
            System.out.println(result + " row(s) affected");
        } catch (SQLException e) {
            System.out.println("Error inserting data: " + e);
        }
    }

    @Override
    void getDayAndWaste() {
        System.out.println("Enter the day (e.g., Monday, Tuesday, etc.):");
        String day = scanner.nextLine().trim();

        if (!Arrays.asList(Waste.days).contains(day)) {
            System.out.println("Invalid day. Please enter a valid day.");
            return;
        }

        System.out.println("Enter the type of waste to dispose (or type 'exit' to stop):");
        System.out.println("Recyclable items: " + Arrays.toString(Waste.recyclable));
        System.out.println("Non-Recyclable items: " + Arrays.toString(Waste.nonRecyclable));

        int dayIndex = Arrays.asList(Waste.days).indexOf(day);

        int wasteIndex = 0;
        while (wasteIndex < Waste.MAX_WASTE_TYPES) {
            String wasteType = scanner.nextLine().trim();
            if (wasteType.equalsIgnoreCase("exit")) {
                break;
            }

            if (Arrays.asList(Waste.recyclable).contains(wasteType)) {
                wasteData[dayIndex][wasteIndex] = wasteType;
                wasteCount[dayIndex]++;
                recyclableCount++;
                System.out.println("Recyclable waste disposed: " + wasteType);
                insertData(day, wasteType); // Insert into database
                wasteIndex++;
            } else if (Arrays.asList(Waste.nonRecyclable).contains(wasteType)) {
                wasteData[dayIndex][wasteIndex] = wasteType;
                wasteCount[dayIndex]++;
                nonRecyclableCount++;
                System.out.println("Non-Recyclable waste disposed: " + wasteType);
                insertData(day, wasteType); // Insert into database
                wasteIndex++;
            } else {
                System.out.println("Invalid waste type. Please enter a valid waste type or type 'exit' to stop.");
            }
        }
    }

    @Override
    void amount() {
        System.out.println("Total number of recyclable items disposed: " + recyclableCount);
        System.out.println("Total number of non-recyclable items disposed: " + nonRecyclableCount);
    }

    @Override
    void maxDay() {
        if (Arrays.stream(wasteCount).sum() == 0) {
            System.out.println("No waste disposed yet.");
            return;
        }

        int maxIndex = 0;
        for (int i = 1; i < Waste.MAX_DAYS; i++) {
            if (wasteCount[i] > wasteCount[maxIndex]) {
                maxIndex = i;
            }
        }

        System.out.println("Maximum waste disposed on " + Waste.days[maxIndex] + " with " + wasteCount[maxIndex] + " items.");
    }

    @Override
    void showWasteByDay() {
        System.out.println("Enter the day to show waste collected (e.g., Monday, Tuesday, etc.):");
        String day = scanner.nextLine().trim();

        if (!Arrays.asList(Waste.days).contains(day)) {
            System.out.println("Invalid day. Please enter a valid day.");
            return;
        }

        int dayIndex = Arrays.asList(Waste.days).indexOf(day);

        if (wasteCount[dayIndex] > 0) {
            System.out.println("Waste collected on " + Waste.days[dayIndex] + ": " + wasteCount[dayIndex] + " items");
        } else {
            System.out.println("No waste collected on " + Waste.days[dayIndex] + " yet.");
        }
    }

    // Getter method for the scanner
    public Scanner getScanner() {
        return scanner;
    }
}

public class Main {
    public static void main(String[] args) {
        WasteDisposed wasteDisposed = new WasteDisposed();

        while (true) {
            System.out.println("\nChoose an option:");
            System.out.println("1. Dispose waste for a day");
            System.out.println("2. Check the total amount of waste disposed");
            System.out.println("3. Check the day with the maximum waste disposed");
            System.out.println("4. Show waste collected by day");
            System.out.println("5. Exit");

            int choice;
            try {
                choice = Integer.parseInt(wasteDisposed.getScanner().nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
                continue;
            }

            switch (choice) {
                case 1:
                    wasteDisposed.getDayAndWaste();
                    break;
                case 2:
                    wasteDisposed.amount();
                    break;
                case 3:
                    wasteDisposed.maxDay();
                    break;
                case 4:
                    wasteDisposed.showWasteByDay();
                    break;
                case 5:
                    System.out.println("Exiting the Waste Management App. Thank you!");
                    System.exit(0);
                default:
                    System.out.println("Invalid choice. Please enter a valid option.");
            }
        }
    }
}
