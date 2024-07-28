package HospitalManagementSystem;
import java.sql.*;
import java.util.Scanner;

public class HospitalManagementSystem {
    private static final String url = "jdbc:mysql://localhost:3306/hospital";
    private  static  final String username = "root";
    private static final String password = "Admin@123";

    public static void main(String[] args) {
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch(ClassNotFoundException e){
            e.printStackTrace();
        }
        Scanner scanner = new Scanner(System.in);
        try{
            Connection connection = DriverManager.getConnection(url,username,password);
            Patient patient = new Patient(connection,scanner);
            Doctor doctor = new Doctor(connection);
            while (true){
                System.out.println("HOSPITAL MANAGEMENT SYSTEM");
                System.out.println("1 Add patient");
                System.out.println("2 View Patient");
                System.out.println("3 View Doctors");
                System.out.println("4 Book Appointment");
                System.out.println("5 Exit");
                System.out.println("Enter your choice: ");
                int choice = scanner.nextInt();
                switch (choice){
                    case 1:
                        //add patient
                        patient.addPatient();
                        break;
                    case 2:
                        //View patient
                        patient.viewPatients();
                        break;
                    case 3:
                        //view doc
                        doctor.viewDoctors();
                        break;

                    case 4:
                        bookAppointment(connection,scanner,patient,doctor);
                        break;

                    case 5:
                        System.out.print("Thankyou!");
                        return;
                    default:
                        System.out.println("enter valid choice!!");
                        break;
                }



            }
        }catch (SQLException e){
            e.printStackTrace();
        }
    }
    public static void bookAppointment(Connection connection,Scanner scanner, Patient patient , Doctor doctor){
        System.out.println("Enter patient Id: ");
        int patientId = scanner.nextInt();
        System.out.println("Enter doctor Id: ");
        int doctorId = scanner.nextInt();
        System.out.println("Enter appointment Date (YYYY-MM-DD):");
        String appointmentDate = scanner.next();
        if(patient.getPatientById(patientId) && doctor.getDoctorById(doctorId)){
            if(checkDoctorAvailability(doctorId,appointmentDate,connection)){
                String appointmentQuery = "Insert into appointments(patient_id, doctor_id, appointment_date) VALUES(?,?,?)";
               try {
                   PreparedStatement preparedStatement = connection.prepareStatement(appointmentQuery);
                   preparedStatement.setInt(1, patientId);
                   preparedStatement.setInt(2, doctorId);
                   preparedStatement.setString(3,appointmentDate);
                   int rowsAffected = preparedStatement.executeUpdate();
                   if (rowsAffected>0){
                       System.out.println("Appointment Booked");
                   }else{
                       System.out.println("Failed to book appointment");
                   }
               }catch (SQLException e){
                   e.printStackTrace();
               }
            }else {
                System.out.println("Doctor not available on this date!");
            }
        }else {
            System.out.println("EITHER PATIENT OR THE DOCTOR DOESN'T EXIST!!!");

        }
    }
    public static boolean checkDoctorAvailability(int doctorId, String appointments, Connection connection){
        String query = "SELECT COUNT(*) FROM appointments WHERE doctor_id =? AND  appointment_date =?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1,doctorId);
            preparedStatement.setString(2,appointments);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()){
                int count = resultSet.getInt(1);
                return count == 0;
            }else {
                return false;
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }
}
