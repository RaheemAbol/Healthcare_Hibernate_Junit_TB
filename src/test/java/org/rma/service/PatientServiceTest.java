package org.rma.service;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.rma.model.Patient;
import org.rma.repository.PatientRepositoryImpl;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PatientServiceTest {

    private PatientService patientService;
    private SessionFactory sessionFactory;
    private Session session;
    private Transaction transaction;

    @BeforeEach
    public void setUp() {
        sessionFactory = new Configuration().configure("hibernate-test.cfg.xml").buildSessionFactory();
        session = sessionFactory.openSession();
        transaction = session.beginTransaction();

        PatientRepositoryImpl patientRepository = new PatientRepositoryImpl(sessionFactory);
        patientService = new PatientService(patientRepository);
    }

    @AfterEach
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
    public void testCreatePatient() {
        Patient patient = new Patient();
        patient.setFirstName("John");
        patient.setLastName("Doe");
        patient.setDateOfBirth("1980-01-01");
        patient.setEmail("john.doe@example.com");
        patient.setPhoneNumber("1234567890");

        patientService.createPatient(patient);
        Patient retrievedPatient = patientService.getPatientById(patient.getPatientId());

        assertNotNull(retrievedPatient, "Patient should be created successfully.");
        assertEquals("John", retrievedPatient.getFirstName());
        assertEquals("Doe", retrievedPatient.getLastName());
    }

    @Test
    public void testGetPatientById() {
        Patient patient = new Patient();
        patient.setFirstName("Alice");
        patient.setLastName("Smith");
        patient.setDateOfBirth("1995-06-15");
        patient.setEmail("alice.smith@example.com");
        patient.setPhoneNumber("5551234567");
        patientService.createPatient(patient);

        Patient retrievedPatient = patientService.getPatientById(patient.getPatientId());

        assertNotNull(retrievedPatient, "Patient should exist in the database.");
        assertEquals("Alice", retrievedPatient.getFirstName());
        assertEquals("Smith", retrievedPatient.getLastName());
    }

    @Test
    public void testGetAllPatients() {
        Patient patient1 = new Patient();
        patient1.setFirstName("Michael");
        patient1.setLastName("Brown");
        patient1.setDateOfBirth("1987-03-22");
        patient1.setEmail("michael.brown@example.com");
        patient1.setPhoneNumber("9876543210");
        patientService.createPatient(patient1);

        Patient patient2 = new Patient();
        patient2.setFirstName("Sophia");
        patient2.setLastName("Johnson");
        patient2.setDateOfBirth("1992-08-30");
        patient2.setEmail("sophia.johnson@example.com");
        patient2.setPhoneNumber("8765432109");
        patientService.createPatient(patient2);

        List<Patient> patients = patientService.getAllPatients();

        assertNotNull(patients, "Patient list should not be null.");
        assertFalse(patients.isEmpty(), "Patient list should not be empty.");
        assertTrue(patients.size() >= 2, "At least two patients should be present.");
    }

    @Test
    public void testUpdatePatient() {
        // Arrange
        Patient patient = new Patient();
        patient.setFirstName("Chris");
        patient.setLastName("Evans");
        patient.setDateOfBirth("1990-07-19");
        patient.setEmail("chris.evans@example.com");
        patient.setPhoneNumber("1239876543");
        patientService.createPatient(patient);

        // Act
        patient.setPhoneNumber("1112223333"); // Update phone number
        patientService.updatePatient(patient);
        Patient updatedPatient = patientService.getPatientById(patient.getPatientId());

        // Assert
        assertNotNull(updatedPatient, "Updated patient should exist.");
        assertEquals("1112223333", updatedPatient.getPhoneNumber(), "Phone number should be updated.");
    }

    @Test
    public void testDeletePatient() {
        Patient patient = new Patient();
        patient.setFirstName("Emma");
        patient.setLastName("Watson");
        patient.setDateOfBirth("1989-04-15");
        patient.setEmail("emma.watson@example.com");
        patient.setPhoneNumber("9998887777");
        patientService.createPatient(patient);

        int patientId = patient.getPatientId();

        patientService.deletePatient(patientId);
        Patient deletedPatient = patientService.getPatientById(patientId);

        assertNull(deletedPatient, "Patient should be deleted.");
    }

    @ParameterizedTest
    @ValueSource(strings = {"john.doe@example.com", "jane.doe@example.com", "alice.smith@example.com"})
    public void testCreatePatientWithDifferentEmails(String email) {
        Patient patient = new Patient();
        patient.setFirstName("Test");
        patient.setLastName("User");
        patient.setEmail(email);
        patient.setDateOfBirth("1995-01-01");
        patient.setPhoneNumber("1234567890");

        patientService.createPatient(patient);
        Patient retrievedPatient = patientService.getPatientById(patient.getPatientId());

        assertNotNull(retrievedPatient, "Patient should be created.");
        assertEquals(email, retrievedPatient.getEmail(), "Email should match.");
    }
}
