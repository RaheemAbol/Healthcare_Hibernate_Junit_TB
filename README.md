# **Healthcare Hibernate JUnit Ticket Breakdown**  

---

## **Introduction**  
This breakdown provides **step-by-step** instructions to implement unit tests for the `OfficeServiceTest` and `OfficeServiceTestAll` classes using **JUnit 5 and Hibernate**.  

These test classes ensure that the `OfficeService` class correctly performs CRUD operations (**Create, Read, Update, Delete**) on **Office** entities while integrating with the `Doctor` entity.

- **OfficeServiceTest** → Uses `@BeforeEach` and `@AfterEach` to isolate each test.  
- **OfficeServiceTestAll** → Uses `@BeforeAll` and `@AfterAll` to make tests **dependent on one another**.  

---

# **Ticket 1: `OfficeServiceTest` (Isolated Tests Using `@BeforeEach`)**  

## **1. Set Up the Testing Class and Basic Structure**  
**Task:**  
- Create a new test class named `OfficeServiceTest` inside the `test` package.  

### **Steps to Implement:**  
1. **Annotate the class with `@TestInstance(TestInstance.Lifecycle.PER_CLASS)`**  
   - This will allow `@BeforeEach` and `@AfterEach` to manage Hibernate session instances.  
   
2. **Initialize Hibernate in `@BeforeEach`**  
   - Configure `SessionFactory` using `hibernate-test.cfg.xml`.  
   - Open a Hibernate `Session`.  
   - Begin a `Transaction`.  
   - Create instances of `OfficeRepositoryImpl` and `DoctorRepositoryImpl`.  
   - Inject repositories into `OfficeService` and `DoctorService`.  

3. **Clean up resources in `@AfterEach`**  
   - Roll back the transaction to **ensure database consistency**.  
   - Close the session.  
   - Close the session factory.  

**Goal:**  
- Ensure each test **operates independently**, resetting data before each execution.

---

## **2. Implement the `testCreateOffice` Method**  
**Task:**  
- Verify that an **Office** entity can be successfully created.  

### **Steps to Implement:**  
1. **Create a `Doctor` entity** and persist it using `doctorService.createDoctor(doctor)`.  
2. **Create an `Office` entity**, assign a **location** and **phone number**, and associate it with the saved doctor.  
3. **Call `officeService.createOffice(office)`** to persist the office in the database.  
4. **Retrieve the office** using `officeService.getOfficeById(office.getOfficeId())`.  
5. **Assertions:**
   - `assertNotNull(fetchedOffice, "Office should exist in the database.")`  
   - `assertEquals("123 Main St", fetchedOffice.getLocation(), "Location should match the one assigned.")`  
   - `assertEquals("123-456-7890", fetchedOffice.getPhone(), "Phone number should match the one assigned.")`  

**Goal:**  
- Ensure that `createOffice()` successfully **persists a new Office entity** in the database.

---

## **3. Implement the `testGetOfficeById` Method**  
**Task:**  
- Retrieve an `Office` record by ID and ensure its **correctness**.  

### **Steps to Implement:**  
1. **Create and persist a `Doctor` entity**.  
2. **Create and persist an `Office` entity**.  
3. **Retrieve the office using `officeService.getOfficeById(office.getOfficeId())`**.  
4. **Assertions:**
   - `assertNotNull(fetchedOffice, "Office should be retrievable.")`  
   - `assertEquals(expectedLocation, fetchedOffice.getLocation(), "Location should be correct.")`  
   - `assertEquals(expectedPhone, fetchedOffice.getPhone(), "Phone number should match.")`  

**Goal:**  
- Verify that `getOfficeById()` **correctly fetches stored office details**.

---

## **4. Implement the `testUpdateOffice` Method**  
**Task:**  
- Modify an `Office` record and verify the changes are persisted.  

### **Steps to Implement:**  
1. **Create and persist an `Office` entity**.  
2. **Modify the `location` and `phone number`** of the office.  
3. **Call `officeService.updateOffice(office)`**.  
4. **Retrieve the updated office** and verify that the changes were successfully applied.  
5. **Assertions:**
   - `assertEquals("456 Maple St", updatedOffice.getLocation(), "Location should be updated.")`  
   - `assertEquals("555-123-4567", updatedOffice.getPhone(), "Phone number should be updated.")`  

**Goal:**  
- Ensure that `updateOffice()` **properly modifies an Office record**.

---

## **5. Implement the `testDeleteOffice` Method**  
**Task:**  
- Validate that an `Office` entity can be **removed** from the database.  

### **Steps to Implement:**  
1. **Create and persist an `Office` entity**.  
2. **Call `officeService.deleteOffice(id)`** to remove it.  
3. **Attempt to retrieve the deleted office** using `getOfficeById()`.  
4. **Assertions:**
   - `assertNull(deletedOffice, "Office should be deleted and no longer retrievable.")`  

**Goal:**  
- Confirm that `deleteOffice()` **removes an Office record** from the database.

---

## **6. Implement a Parameterized Test for Office Creation**  
**Task:**  
- Use `@ParameterizedTest` to validate `Office` creation with multiple locations.  

### **Steps to Implement:**  
1. **Use `@ValueSource(strings = { "New York", "Los Angeles", "Chicago", "San Francisco" })`**  
2. **Loop through each location and create an `Office`**.  
3. **Call `officeService.createOffice(office)`**.  
4. **Retrieve and validate the office** using assertions.  

**Example Implementation:**  
```java
@ParameterizedTest
@ValueSource(strings = {"New York", "Los Angeles", "Chicago", "San Francisco"})
public void testCreateOfficeWithDifferentLocations(String location) {
    Doctor doctor = createTestDoctor("Alex", "Karev", "Surgery", "alex.karev@example.com");

    Office office = new Office();
    office.setLocation(location);
    office.setPhone("123-456-7890");
    office.setDoctor(doctor);

    officeService.createOffice(office);

    assertNotNull(office.getOfficeId(), "Office ID should be generated.");
    assertEquals(location, office.getLocation(), "Location should match the assigned value.");
}
```

**Goal:**  
- Ensure that `createOffice()` can **handle multiple valid inputs**.

---


## **Ticket 2: `OfficeServiceTestAll` (Tests That Depend on Each Other Using `@BeforeAll` and `@AfterAll`)**  

### **1. Set Up the Testing Class and Basic Structure**  
**Task:**  
- Create a new test class named `OfficeServiceTestAll` inside the `test` package.  
- Ensure that **each test builds on the previous one** rather than being independent.  

### **Steps to Implement:**  

1. **Use `@TestInstance(TestInstance.Lifecycle.PER_CLASS)`**  
   - This allows `@BeforeAll` and `@AfterAll` to use non-static methods, making it easier to share objects between tests.  

2. **Use `@TestMethodOrder(MethodOrderer.OrderAnnotation.class)`**  
   - This ensures that tests are **executed in a specific order**, so that later tests depend on earlier ones.  
   - The order is set using `@Order(n)`, where `n` is a number indicating execution sequence.  

3. **Initialize Hibernate in `@BeforeAll`**  
   - Configure `SessionFactory` using `hibernate-test.cfg.xml`.  
   - Open a Hibernate `Session`.  
   - Begin a `Transaction`.  
   - Create instances of `OfficeRepositoryImpl` and `DoctorRepositoryImpl`.  
   - Inject repositories into `OfficeService` and `DoctorService`.  

4. **Clean up resources in `@AfterAll`**  
   - Roll back the transaction to **ensure database consistency**.  
   - Close the session.  
   - Close the session factory.  

### **Goal:**  
- Set up **one shared test environment** that runs in order and reuses test data.  
- Reduce redundant data creation and cleanup across tests.  

---

### **2. Implement the `testCreateOffice` Method**  
**Task:**  
- Create a **shared `Office` instance** that will be used in later tests.  

### **Steps to Implement:**  

1. **Create and persist a `Doctor` entity.**  
   - This doctor will be assigned to the office.  

2. **Create and persist an `Office` entity.**  
   - Assign a `location` and `phone number`.  
   - Associate the office with the newly created doctor.  
   - Call `officeService.createOffice(office)`.  

3. **Store the `officeId` in a shared variable.**  
   - This allows future tests to retrieve and modify the same office.  

4. **Assertions:**  
   - `assertNotNull(office.getOfficeId(), "Office should be created with a valid ID.")`  
     - Ensures that Hibernate assigns an ID upon persistence.  
   - `assertTrue(office.getOfficeId() > 0, "Office ID should be a positive number.")`  
     - Confirms that an office entry exists in the database.  

### **Goal:**  
- Ensure `createOffice()` **successfully persists an Office record** that other tests will use.  
- Store the office ID for future retrieval and modification.  

---

### **3. Implement the `testGetOfficeById` Method**  
**Task:**  
- Retrieve and verify details of the **previously created office**.  

### **Steps to Implement:**  

1. **Use the `officeId` from `testCreateOffice()`**.  
   - This ensures the test retrieves the exact office created earlier.  

2. **Call `officeService.getOfficeById(officeId)` to fetch the office.**  

3. **Assertions:**  
   - `assertNotNull(retrievedOffice, "Office should exist in the database.")`  
     - Ensures the office entry can be fetched.  
   - `assertEquals("New York", retrievedOffice.getLocation(), "Location should match what was assigned.")`  
     - Confirms that location data is correctly stored.  
   - `assertEquals("123-456-7890", retrievedOffice.getPhone(), "Phone number should be correctly stored.")`  
     - Verifies that phone data is accurate.  

### **Goal:**  
- Ensure `getOfficeById()` retrieves the **correct office instance**.  

---

### **4. Implement the `testUpdateOffice` Method**  
**Task:**  
- Modify and update the `Office` details, then verify the changes are applied.  

### **Steps to Implement:**  

1. **Fetch the existing office using `getOfficeById(officeId)`.**  
   - Ensure that the office was created successfully.  

2. **Modify the office details.**  
   - Change the `location` and `phone number`.  

3. **Call `officeService.updateOffice(office)`.**  
   - This will persist the changes in the database.  

4. **Retrieve the updated office and verify that changes were applied.**  

5. **Assertions:**  
   - `assertEquals("San Francisco", updatedOffice.getLocation(), "Office location should be updated.")`  
   - `assertEquals("555-789-1234", updatedOffice.getPhone(), "Phone number should be updated.")`  

### **Goal:**  
- Ensure `updateOffice()` **correctly modifies an Office record** in the database.  

---

### **5. Implement the `testDeleteOffice` Method**  
**Task:**  
- Remove the previously created office and verify that it no longer exists.  

### **Steps to Implement:**  

1. **Call `officeService.deleteOffice(officeId)`.**  
   - This removes the office record from the database.  

2. **Attempt to retrieve the deleted office using `getOfficeById(officeId)`.**  

3. **Assertions:**  
   - `assertNull(deletedOffice, "Office should be deleted and no longer retrievable.")`  
     - Ensures that no entry exists after deletion.  

### **Goal:**  
- Confirm that `deleteOffice()` successfully **removes an Office record** from the database.  

---

### **6. Implement the `testGetAllOffices` Method**  
**Task:**  
- Validate that retrieving **all offices** works correctly.  

### **Steps to Implement:**  

1. **Create and persist at least two office records.**  
2. **Call `officeService.getAllOffices()` to retrieve all offices.**  
3. **Ensure the list contains at least two entries.**  

4. **Assertions:**  
   - `assertNotNull(offices, "Office list should not be null.")`  
   - `assertFalse(offices.isEmpty(), "Office list should not be empty.")`  
   - `assertTrue(offices.size() >= 2, "At least two offices should exist.")`  

### **Goal:**  
- Ensure `getAllOffices()` successfully **fetches multiple office records**.  

---

### **7. Implement a Parameterized Test for Office Creation**  
**Task:**  
- Use `@ParameterizedTest` to validate `Office` creation with multiple locations.  

### **Steps to Implement:**  

1. **Use `@ValueSource(strings = { "Houston", "Boston", "Seattle", "San Diego" })`.**  
2. **Loop through each location and create an `Office`.**  
3. **Call `officeService.createOffice(office)`.**  
4. **Retrieve and validate the office using assertions.**  

---

### **Example Implementation:**  

```java
@ParameterizedTest
@ValueSource(strings = {"Houston", "Boston", "Seattle", "San Diego"})
@Order(6)
public void testCreateOfficeWithDifferentLocations(String location) {
    Doctor doctor = new Doctor();
    doctor.setFirstName("Greg");
    doctor.setLastName("House");
    doctor.setSpecialty("Diagnostics");
    doctor.setEmail("greg.house@example.com");
    doctorService.createDoctor(doctor);

    Office office = new Office();
    office.setLocation(location);
    office.setPhone("999-999-9999");
    office.setDoctor(doctor);

    officeService.createOffice(office);
    Office retrievedOffice = officeService.getOfficeById(office.getOfficeId());

    assertNotNull(retrievedOffice, "Office should be created.");
    assertEquals(location, retrievedOffice.getLocation(), "Office location should match.");
}
```

### **Goal:**  
- Ensure that `createOffice()` can **handle multiple valid inputs** efficiently.  

---
