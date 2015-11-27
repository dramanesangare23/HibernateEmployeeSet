package pkg;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

public class ManageEmployee {
	
	private static SessionFactory sessionFactory;
	
	public static void main(String[] args) {
		try {
			sessionFactory = new Configuration().configure().buildSessionFactory();
		} catch (Throwable ex) {
			System.err.println("Failed to create SessionFactory object : " + ex);
			throw new ExceptionInInitializerError(ex);
		}
		ManageEmployee ME = new ManageEmployee();
		
		//Let us have a set of certificates for the first employee
		HashSet<Certificate> set1 = new HashSet<>();
		set1.add(new Certificate("MCA"));
		set1.add(new Certificate("MBA"));
		set1.add(new Certificate("PMP"));
		
		/* Add employee records in the database*/
		Integer empID1 = ME.addEmployee("Jonasse", "Juliana", 10000, set1);
		
		//Another set of certificates for the second employee
		HashSet<Certificate> set2 = new HashSet<>();
		set2.add(new Certificate("BCA"));
		set2.add(new Certificate("BA"));
		
		//Add another employee record in the database
		Integer empID2 = ME.addEmployee("Angelina", "Joly", 3000, set2);
		
		// List down all the employees
		ME.listEmployees();
		
		//Update empployee's records
		ME.updateEmployee(empID1, 5000);
		
		//Delete an employee from the database
		ME.deleteEmployee(empID2);
		
		//List down new list of the employees
		ME.listEmployees();
		
		sessionFactory.close();
	}
	
	/**
	 * Method to CREATE an employee in the database
	 */
	public Integer addEmployee(String fname, String lname, int salary, Set<Certificate> cert){
		Session session = sessionFactory.openSession();
		Transaction transaction = null;
		Integer employeeID = null;
		try {
			transaction = session.beginTransaction();
			Employee employee = new Employee(fname, lname, salary);
			employee.setCertificates(cert);
			employeeID = (Integer) session.save(employee);
			transaction.commit();
		} catch (HibernateException he) {
			if(transaction != null)
				transaction.rollback();
			he.printStackTrace();
		} finally{
			session.close();
		}
		return employeeID;
	}
	
	/**
	 * Method to READ all the employees
	 */
	@SuppressWarnings("unchecked")
	public void listEmployees(){
		Session session = sessionFactory.openSession();
		Transaction transaction = null;
		try{
			transaction = session.beginTransaction();
			List<Employee> employees = session.createQuery("FROM Employee").list();
			for(Iterator<Employee> iterator = employees.iterator(); iterator.hasNext();){
				Employee employee = (Employee) iterator.next();
				System.out.println("-----------------------------------------------");
				System.out.print("ID = " + employee.getId());
				System.out.print(", Firstname = " + employee.getFirstName());
				System.out.print(", Lastname = " + employee.getLastName());
				System.out.println(", Salary = " + employee.getSalary());
				Set<Certificate> cert = employee.getCertificates();
				for(Iterator<Certificate> iterator2 = cert.iterator(); iterator2.hasNext();){
					Certificate certName = (Certificate) iterator2.next();
					System.out.println("--- Certificate : " + certName.getName());
				}
			}
			transaction.commit();
		} catch(HibernateException he){
			if(transaction != null)
				transaction.rollback();
			he.printStackTrace();
		} finally{
			session.close();
		}
	}
	
	/**
	 * Method UPDATE salary for an employee
	 */
	private void updateEmployee(Integer empID, int salary) {
		Session session = sessionFactory.openSession();
		Transaction transaction = null;
		try {
			transaction = session.beginTransaction();
			Employee employee = session.get(Employee.class, empID);
			employee.setSalary(salary);
			session.update(employee);
			transaction.commit();
		} catch (HibernateException he) {
			if(transaction != null)
				transaction.rollback();
			he.printStackTrace();
		} finally{
			session.close();
		}
	}
	
	/**
	 * DELETE an employee from the employee
	 */
	public void deleteEmployee(Integer empID){
		Session session = sessionFactory.openSession();
		Transaction transaction = null;
		try{
			transaction = session.beginTransaction();
			Employee employee = session.get(Employee.class, empID);
			session.delete(employee);
			transaction.commit();
		} catch(HibernateException he){
			if(transaction != null)
				transaction.rollback();
			he.printStackTrace();
		} finally{
			session.close();
		}
	}

}
