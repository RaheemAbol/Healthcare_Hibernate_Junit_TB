package org.rma.service;


import org.rma.model.Office;
import org.rma.repository.OfficeRepositoryImpl;

import java.util.List;

public class OfficeService {

    private final OfficeRepositoryImpl officeRepository;

    public OfficeService(OfficeRepositoryImpl officeRepository) {
        this.officeRepository = officeRepository;
    }

    public void createOffice(Office office) {
        officeRepository.create(office);
    }

    public Office getOfficeById(int id) {
        return officeRepository.findById(id);
    }

    public List<Office> getAllOffices() {
        return officeRepository.findAll();
    }

    public void updateOffice(Office office) {
        officeRepository.updateOffice(office);
    }

    public void deleteOffice(int id) {
        officeRepository.delete(id);
    }
}