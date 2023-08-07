import java.sql.*;
import java.util.Scanner;

public class StudentManagement {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/coursedb";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "19301247";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean exit = false;

        System.out.println("Welcome to Student Management System");
        System.out.println("1. Login as Student");
        System.out.println("2. Register as Student");
        System.out.println("3. Login as Teacher");
        System.out.println("4. Exit");
        int option = scanner.nextInt();

        switch (option) {
            case 1:
                studentLogin(scanner);
                break;
            case 2:
                registerStudent(scanner);
                break;
            case 3:
                teacherLogin(scanner);
                break;
            case 4:
                exit = true;
                break;
            default:
                System.out.println("Invalid option");
                break;
        }
    }

    private static void studentLogin(Scanner scanner) {
        System.out.println("Enter your email: ");
        String email = scanner.next();
        System.out.println("Enter your password: ");
        String password = scanner.next();

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {

            int studentId = authenticateStudent(connection, email, password);
            if (studentId != -1) {
                System.out.println("Student login successful!");

                displaySections(connection);

                System.out.println("Choose a section: ");
                int section = scanner.nextInt();

                if (registerInSection(connection, studentId, section)) {
                    System.out.println("Registration successful!");
                } else {
                    System.out.println("No seats available in the selected section. Please choose another section.");
                }
            } else {
                System.out.println("Invalid email or password. Please try again.");
            }
        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private static int authenticateStudent(Connection connection, String email, String password) throws SQLException {
        String query = "SELECT sid FROM student WHERE email = ? AND password = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, email);
            preparedStatement.setString(2, password);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("sid");
                }
            }
        }
        return -1;
    }

    private static void registerStudent(Scanner scanner) {
        System.out.println("Enter your name: ");
        String name = scanner.next();
        System.out.println("Enter your SID: ");
        String sid = scanner.next();
        System.out.println("Enter your email: ");
        String email = scanner.next();
        System.out.println("Enter your password: ");
        String password = scanner.next();

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO student (name, sid, email, password) VALUES (?, ?, ?, ?)")) {

            preparedStatement.setString(1, name);
            preparedStatement.setString(2, sid);
            preparedStatement.setString(3, email);
            preparedStatement.setString(4, password);
            preparedStatement.executeUpdate();

            System.out.println("Student registration successful!");
        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private static void teacherLogin(Scanner scanner) {
        System.out.println("Enter your email: ");
        String email = scanner.next();
        System.out.println("Enter your password: ");
        String password = scanner.next();

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {

            if (authenticateTeacher(connection, email, password)) {
                System.out.println("Teacher login successful!");

                System.out.println("Enter a section ID: ");
                int section = scanner.nextInt();

                printStudentList(connection, section);
            } else {
                System.out.println("Invalid email or password. Please try again.");
            }
        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private static boolean authenticateTeacher(Connection connection, String email, String password) throws SQLException {
        String query = "SELECT * FROM teacher WHERE email = ? AND password = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, email);
            preparedStatement.setString(2, password);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    private static void displaySections(Connection connection) throws SQLException {
        String query = "SELECT section_id, seats_remaining FROM sections";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            System.out.println("Available sections: ");
            while (resultSet.next()) {
                int sectionId = resultSet.getInt("section_id");
                int seatsRemaining = resultSet.getInt("seats_remaining");

                if (sectionId == 1) {
                    System.out.println(sectionId + " - Sunday: 12:30 pm 1:00 pm " + seatsRemaining + " Seats Remaining");
                } else if (sectionId == 2) {
                    System.out.println(sectionId + " - Sunday: 2:30 pm 4:00 pm " + seatsRemaining + " Seats Remaining");
                } else {
                    System.out.println("Invalid section ID");
                }
            }
        }
    }


    private static boolean registerInSection(Connection connection, int sid, int section) throws SQLException {
        String query = "SELECT seats_remaining FROM sections WHERE section_id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, section);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    int seatsRemaining = resultSet.getInt("seats_remaining");
                    if (seatsRemaining > 0) {
                        query = "UPDATE sections SET seats_remaining = ? WHERE section_id = ?";
                        try (PreparedStatement updateStatement = connection.prepareStatement(query)) {
                            updateStatement.setInt(1, seatsRemaining - 1);
                            updateStatement.setInt(2, section);
                            updateStatement.executeUpdate();
                        }
                        query = "INSERT INTO student_section (sid, section_id) VALUES (?, ?)";
                        try (PreparedStatement insertStatement = connection.prepareStatement(query)) {
                            insertStatement.setInt(1, sid);
                            insertStatement.setInt(2, section);
                            insertStatement.executeUpdate();
                        }
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private static void printStudentList(Connection connection, int section) throws SQLException {
        String query = "SELECT s.name, s.sid FROM student s JOIN student_section ss ON s.sid = ss.sid WHERE ss.section_id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, section);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                System.out.println("Student list in Section " + section + ":");
                while (resultSet.next()) {
                    String name = resultSet.getString("name");
                    int sid = resultSet.getInt("sid");
                    System.out.println("Name: " + name + ", SID: " + sid);
                }
            }
        }
    }
}