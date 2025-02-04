package org.rma.service;

import org.rma.model.Doctor;
import org.rma.model.Patient;
import org.rma.repository.PatientRepositoryImpl;

import java.util.List;

public class PatientService {


    private final PatientRepositoryImpl patientRepository;

    public PatientService(PatientRepositoryImpl patientRepository) {
        this.patientRepository = patientRepository;
    }

    public void createPatient(Patient patient) {
        patientRepository.create(patient);
    }

    public Patient getPatientById(int id) {
        return patientRepository.findById(id);
    }

    public List<Patient> getAllPatients() {
        return patientRepository.findAll();
    }

    public void updatePatient(Patient patient) {
        patientRepository.update(patient);
    }

    public void deletePatient(int id) {
        patientRepository.delete(id);
    }

    public void addDoctorToPatient(int patientId, Doctor doctor) {
        patientRepository.addDoctorToPatient(patientId, doctor);
    }

    public void removeDoctorFromPatient(int patientId, Doctor doctor) {
        patientRepository.removeDoctorFromPatient(patientId, doctor);
    }
}