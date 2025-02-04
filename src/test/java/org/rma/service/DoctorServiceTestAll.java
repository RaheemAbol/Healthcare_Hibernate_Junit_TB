package org.rma.service;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.rma.model.Doctor;
import org.rma.repository.DoctorRepositoryImpl;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DoctorServiceTestAll {

    private DoctorService doctorService;
    private SessionFactory sessionFactory;
    private Session session;
    private Transaction transaction;

    private static Doctor doctor;
    private static int doctorId;

    @BeforeAll
    public void setUp() {
        sessionFactory = new Configuration().configure("hibernate-test.cfg.xml").buildSessionFactory();
        session = sessionFactory.openSession();
        transaction = session.beginTransaction();

        DoctorRepositoryImpl doctorRepository = new DoctorRepositoryImpl(sessionFactory);
        doctorService = new DoctorService(doctorRepository);
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
    public void testCreateDoctor() {
        doctor = new Doctor();
        doctor.setFirstName("Jane");
        doctor.setLastName("Smith");
        doctor.setSpecialty("Cardiology");
        doctor.setEmail("jane.smith@example.com");

        doctorService.createDoctor(doctor);
        doctorId = doctor.getDoctorId(); // Store ID for next tests

        assertTrue(doctorId > 0, "Doctor should be created with a valid ID.");
    }

    @Test
    @Order(2)
    public void testGetDoctorById() {
        Doctor retrievedDoctor = doctorService.getDoctorById(doctorId);

        assertNotNull(retrievedDoctor, "Doctor should exist in the database.");
        assertEquals("Cardiology", retrievedDoctor.getSpecialty());
    }

    @Test
    @Order(3)
    public void testUpdateDoctor() {
        Doctor existingDoctor = doctorService.getDoctorById(doctorId);
        assertNotNull(existingDoctor, "Doctor should exist before updating.");

        existingDoctor.setSpecialty("Neurology");

        doctorService.updateDoctor(existingDoctor);
        Doctor updatedDoctor = doctorService.getDoctorById(doctorId);

        assertNotNull(updatedDoctor, "Updated doctor should exist.");
        assertEquals("Neurology", updatedDoctor.getSpecialty());
    }

    @Test
    @Order(4)
    public void testDeleteDoctor() {
        doctorService.deleteDoctor(doctorId);
        Doctor deletedDoctor = doctorService.getDoctorById(doctorId);

        assertNull(deletedDoctor, "Doctor should be deleted.");
    }

    @Test
    @Order(5)
    public void testGetAllDoctors() {
        Doctor doctor1 = new Doctor();
        doctor1.setFirstName("Emily");
        doctor1.setLastName("Jones");
        doctor1.setSpecialty("Orthopedics");
        doctor1.setEmail("emily.jones@example.com");
        doctorService.createDoctor(doctor1);

        Doctor doctor2 = new Doctor();
        doctor2.setFirstName("Liam");
        doctor2.setLastName("Brown");
        doctor2.setSpecialty("Dermatology");
        doctor2.setEmail("liam.brown@example.com");
        doctorService.createDoctor(doctor2);

        List<Doctor> doctors = doctorService.getAllDoctors();

        assertNotNull(doctors, "Doctors list should not be null.");
        assertFalse(doctors.isEmpty(), "Doctors list should not be empty.");
        assertTrue(doctors.size() >= 2, "At least two doctors should exist.");
    }

    @ParameterizedTest
    @ValueSource(strings = {"Pediatrics", "Neurology", "Dermatology"})
    @Order(6)
    public void testCreateDoctorWithDifferentSpecialties(String specialty) {
        Doctor doctor = new Doctor();
        doctor.setFirstName("Alex");
        doctor.setLastName("Karev");
        doctor.setSpecialty(specialty);
        doctor.setEmail("alex.karev@example.com");

        doctorService.createDoctor(doctor);
        Doctor retrievedDoctor = doctorService.getDoctorById(doctor.getDoctorId());

        assertNotNull(retrievedDoctor, "Doctor should be created.");
        assertEquals(specialty, retrievedDoctor.getSpecialty(), "Doctor specialty should match.");
    }
}
