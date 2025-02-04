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

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class) // Ensures tests run in order
public class PatientServiceTestAll {

    private PatientService patientService;
    private SessionFactory sessionFactory;
    private Session session;
    private Transaction transaction;

    private static Patient patient; // Shared patient instance
    private static int patientId; // Shared patient ID

    @BeforeAll
    public void setUp() {
        sessionFactory = new Configuration().configure("hibernate-test.cfg.xml").buildSessionFactory();
        session = sessionFactory.openSession();
        transaction = session.beginTransaction();

        PatientRepositoryImpl patientRepository = new PatientRepositoryImpl(sessionFactory);
        patientService = new PatientService(patientRepository);
    }

    @AfterAll
    public void tearDown() {
        if (transaction != null) {
            transaction.rollback(); // Ensures test data isn't persisted
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
    public void testCreatePatient() {
        // Arrange
        patient = new Patient();
        patient.setFirstName("Alice");
        patient.setLastName("Johnson");
        patient.setDateOfBirth("1990-05-10");
        patient.setEmail("alice.johnson@example.com");
        patient.setPhoneNumber("1234567890");

        // Act
        patientService.createPatient(patient);
        patientId = patient.getPatientId(); // Store for future tests

        // Assert
        assertTrue(patientId > 0, "Patient should be created with a valid ID.");
    }

    @Test
    @Order(2)
    public void testGetPatientById() {
        // Act
        Patient retrievedPatient = patientService.getPatientById(patientId);

        // Assert
        assertNotNull(retrievedPatient, "Patient should exist in the database.");
        assertEquals("Alice", retrievedPatient.getFirstName());
    }

    @Test
    @Order(3)
    public void testUpdatePatient() {
        // Arrange
        Patient existingPatient = patientService.getPatientById(patientId);
        assertNotNull(existingPatient, "Patient should exist before updating.");

        existingPatient.setPhoneNumber("777-777-7777");

        // Act
        patientService.updatePatient(existingPatient);
        Patient updatedPatient = patientService.getPatientById(patientId);

        // Assert
        assertNotNull(updatedPatient, "Updated patient should exist.");
        assertEquals("777-777-7777", updatedPatient.getPhoneNumber());
    }

    @Test
    @Order(4)
    public void testDeletePatient() {
        // Act
        patientService.deletePatient(patientId);
        Patient deletedPatient = patientService.getPatientById(patientId);

        // Assert
        assertNull(deletedPatient, "Patient should be deleted.");
    }

    @Test
    @Order(5)
    public void testGetAllPatients() {
        // Arrange
        Patient patient1 = new Patient();
        patient1.setFirstName("Liam");
        patient1.setLastName("Brown");
        patient1.setDateOfBirth("1985-07-20");
        patient1.setEmail("liam.brown@example.com");
        patient1.setPhoneNumber("999-999-9999");
        patientService.createPatient(patient1);

        Patient patient2 = new Patient();
        patient2.setFirstName("Sophia");
        patient2.setLastName("Miller");
        patient2.setDateOfBirth("1995-03-15");
        patient2.setEmail("sophia.miller@example.com");
        patient2.setPhoneNumber("888-888-8888");
        patientService.createPatient(patient2);

        // Act
        List<Patient> patients = patientService.getAllPatients();

        // Assert
        assertNotNull(patients, "Patients list should not be null.");
        assertFalse(patients.isEmpty(), "Patients list should not be empty.");
        assertTrue(patients.size() >= 2, "At least two patients should exist.");
    }

    @ParameterizedTest
    @ValueSource(strings = {"Michael", "Emma", "James", "Olivia"})
    @Order(6)
    public void testCreatePatientWithDifferentNames(String firstName) {
        // Arrange
        Patient patient = new Patient();
        patient.setFirstName(firstName);
        patient.setLastName("Doe");
        patient.setDateOfBirth("1995-08-25");
        patient.setEmail(firstName.toLowerCase() + ".doe@example.com");
        patient.setPhoneNumber("888-888-8888");

        // Act
        patientService.createPatient(patient);
        Patient retrievedPatient = patientService.getPatientById(patient.getPatientId());

        // Assert
        assertNotNull(retrievedPatient, "Patient should be created.");
        assertEquals(firstName, retrievedPatient.getFirstName(), "Patient name should match.");
    }
}
