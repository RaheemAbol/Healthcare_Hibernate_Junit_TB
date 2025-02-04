package org.rma.service;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.rma.model.Appointment;
import org.rma.model.Doctor;
import org.rma.model.Patient;
import org.rma.repository.AppointmentRepositoryImpl;
import org.rma.repository.DoctorRepositoryImpl;
import org.rma.repository.PatientRepositoryImpl;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AppointmentServiceTestAll {

    private AppointmentService appointmentService;
    private DoctorService doctorService;
    private PatientService patientService;
    private SessionFactory sessionFactory;
    private Session session;
    private Transaction transaction;

    private static int appointmentId;
    private static Patient patient;
    private static Doctor doctor;

    @BeforeAll
    public void setUp() {
        sessionFactory = new Configuration().configure("hibernate-test.cfg.xml").buildSessionFactory();
        session = sessionFactory.openSession();
        transaction = session.beginTransaction();

        AppointmentRepositoryImpl appointmentRepository = new AppointmentRepositoryImpl(sessionFactory);
        DoctorRepositoryImpl doctorRepository = new DoctorRepositoryImpl(sessionFactory);
        PatientRepositoryImpl patientRepository = new PatientRepositoryImpl(sessionFactory);

        doctorService = new DoctorService(doctorRepository);
        patientService = new PatientService(patientRepository);
        appointmentService = new AppointmentService(appointmentRepository, doctorRepository, patientRepository);

        // Creating shared patient & doctor before tests
        patient = new Patient();
        patient.setFirstName("Test");
        patient.setLastName("User");
        patientService.createPatient(patient);

        doctor = new Doctor();
        doctor.setFirstName("Sample");
        doctor.setLastName("Doctor");
        doctorService.createDoctor(doctor);
    }

    @AfterAll
    public void tearDown() {
        if (transaction != null) {
            transaction.rollback();
        }
        if (session != null) {
            session.close();
        }
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }

    @Test
    @Order(1)
    public void testCreateAppointment() {
        // Arrange
        Appointment appointment = new Appointment();
        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointment.setAppointmentDate("2024-09-01");
        appointment.setNotes("Annual checkup");

        // Act
        appointmentService.createAppointment(appointment);
        appointmentId = appointment.getAppointmentId(); // Store ID for next tests

        // Assert
        assertTrue(appointmentId > 0, "Appointment should be created with valid ID.");
    }

    @Test
    @Order(2)
    public void testGetAppointmentById() {
        Appointment retrievedAppointment = appointmentService.getAppointmentById(appointmentId);

        assertNotNull(retrievedAppointment, "Appointment should exist in the database.");
        assertEquals("Annual checkup", retrievedAppointment.getNotes());
    }

    @Test
    @Order(3)
    public void testUpdateAppointment() {
        Appointment appointment = appointmentService.getAppointmentById(appointmentId);
        assertNotNull(appointment, "Appointment should exist before updating.");

        appointment.setNotes("Updated checkup notes");

        appointmentService.updateAppointment(appointment);
        Appointment updatedAppointment = appointmentService.getAppointmentById(appointmentId);

        assertNotNull(updatedAppointment, "Updated appointment should exist.");
        assertEquals("Updated checkup notes", updatedAppointment.getNotes());
    }

    @Test
    @Order(4)
    public void testDeleteAppointment() {
        appointmentService.deleteAppointment(appointmentId);
        Appointment deletedAppointment = appointmentService.getAppointmentById(appointmentId);

        assertNull(deletedAppointment, "Appointment should be deleted.");
    }

    @Test
    @Order(5)
    public void testGetAllAppointments() {
        Appointment appointment1 = new Appointment();
        appointment1.setPatient(patient);
        appointment1.setDoctor(doctor);
        appointment1.setAppointmentDate("2024-09-06");
        appointment1.setNotes("Follow-up visit");
        appointmentService.createAppointment(appointment1);

        Appointment appointment2 = new Appointment();
        appointment2.setPatient(patient);
        appointment2.setDoctor(doctor);
        appointment2.setAppointmentDate("2024-09-07");
        appointment2.setNotes("Final consultation");
        appointmentService.createAppointment(appointment2);

        List<Appointment> appointments = appointmentService.getAllAppointments();

        assertNotNull(appointments, "Appointments list should not be null.");
        assertFalse(appointments.isEmpty(), "Appointments list should not be empty.");
        assertTrue(appointments.size() >= 2, "At least two appointments should exist.");
    }

    @ParameterizedTest
    @ValueSource(strings = {"2024-09-08", "2024-09-09", "2024-09-10", "2024-09-11"})
    @Order(6)
    public void testCreateAppointmentWithDifferentDates(String date) {
        Appointment appointment = new Appointment();
        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointment.setAppointmentDate(date);
        appointment.setNotes("Scheduled appointment");

        appointmentService.createAppointment(appointment);
        Appointment retrievedAppointment = appointmentService.getAppointmentById(appointment.getAppointmentId());

        assertNotNull(retrievedAppointment, "Appointment should be created.");
        assertEquals(date, retrievedAppointment.getAppointmentDate(), "Appointment date should match.");
    }
}
