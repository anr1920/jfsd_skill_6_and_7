package skill6;

import java.util.List;
import java.util.Scanner;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

public class Skill6 {
	public static Session getSession() throws HibernateException {
		String cfg = "hibernate.cfg.xml";
		SessionFactory sessionFactory = new Configuration().configure(cfg).buildSessionFactory();
		return sessionFactory.openSession();
	}

	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);

		boolean exit = false;

		while (!exit) {
			System.out.println("1. Get employee details");
			System.out.println("2. Update employee salary");
			System.out.println("3. Delete employee with least salary");
			System.out.println("4. Show all employees details");
			System.out.println("5. Show employees greater than this age");
			System.out.println("6. Delete a employee");
			System.out.println("7. update employee name");
			System.out.println("8. Exit the program");

			int choice = sc.nextInt();

			switch (choice) {
				case 1:
					System.out.println("Please enter employee id");
					int employeeId = sc.nextInt();
					get_employee_details(employeeId);
					break;

				case 2:
					System.out.println("Please enter employee id");
					int employId = sc.nextInt();
					update_employee_details(employId);
					break;

				case 3:
					delete_minimum_salary_employees();
					break;

				case 4:
					get_all_employees_details();
					break;

				case 5:
					System.out.println("Please enter minimum age");
					int minage = sc.nextInt();
					get_employees_above_age(minage);
					break;

				case 6:
					System.out.println("Enter the id of employee to be deleted");
					int empid = sc.nextInt();
					delete_a_employee(empid);
					break;

				case 7:
					System.out.println("Enter the id of employee to be renamed");
					int emp_id = sc.nextInt();
					sc.nextLine();
					System.out.println("Enter new name");
					String new_name = sc.nextLine();
					update_employee_name(emp_id, new_name);
					break;

				case 8:
					exit = true;
					break;

				default:
					System.out.println("Please Try Again");
					break;
			}
		}
		sc.close();
	}

	public static void get_employee_details(int Empid) {
		try (Session session = getSession()) {

			CriteriaBuilder builder = session.getCriteriaBuilder();

			CriteriaQuery<Employee> criteria = builder.createQuery(Employee.class);
			Root<Employee> employeeRoot = criteria.from(Employee.class);
			criteria.select(employeeRoot);

			criteria.where(builder.equal(employeeRoot.get("Empid"), Empid));
			List<Employee> employeeList = session.createQuery(criteria).getResultList();
			if (employeeList.isEmpty()) {
				System.out.println("No employee found");
			} else {
				for (Employee temp : employeeList) {
					System.out.println("Details of employee with empid " + Empid + " are");
					System.out.println("Employee name " + temp.getEname());
					System.out.println("Employee Department " + temp.getDepartment());
					System.out.println("Employee age " + temp.getAge());
					System.out.println("Employee Salary " + temp.getSalary());
				}
			}
		} catch (Exception e) {
			System.out.println("Something went wrong");
		}
	}

	public static void update_employee_details(int Empid) {
		try (Session session = getSession()) {
			@SuppressWarnings("unchecked")
			List<Double> average = session.createQuery("select avg(salary) from Employee").getResultList();
			Transaction tx = session.beginTransaction();
			Integer avg = average.get(0).intValue();
			int updatedEntities = session.createQuery("update Employee e set e.salary=:n where e.Empid=:i")
					.setParameter("n", avg).setParameter("i", Empid).executeUpdate();

			System.out.println(updatedEntities + " Entities updated succesfully");
			tx.commit();
		} catch (Exception e) {
			System.out.println(e);
			System.out.println("Something went wrong");
		}
	}

	public static void delete_minimum_salary_employees() {
		try (Session session = getSession()) {
			@SuppressWarnings("unchecked")
			List<Integer> minimum = session.createQuery("select min(salary) from Employee").getResultList();
			Transaction tx = session.beginTransaction();
			Integer min = minimum.get(0);
			int updatedEntities = session.createQuery("delete from Employee e where e.salary=:i").setParameter("i", min)
					.executeUpdate();

			System.out.println(updatedEntities + " Entities deleted succesfully");
			tx.commit();
		} catch (Exception e) {
			System.out.println(e);
			System.out.println("Something went wrong");
		}
	}

	public static void get_all_employees_details() {
		try (Session session = getSession()) {

			CriteriaBuilder builder = session.getCriteriaBuilder();

			CriteriaQuery<Employee> criteria = builder.createQuery(Employee.class);
			Root<Employee> employeeRoot = criteria.from(Employee.class);
			criteria.select(employeeRoot);

			List<Employee> employeeList = session.createQuery(criteria).getResultList();
			if (employeeList.isEmpty()) {
				System.out.println("No employee found");
			} else {
				System.out.format("%10s%14s%14s%10s%10s\n", "Empid", "Emp Name", "Department", "Age", "Salary");
				for (Employee temp : employeeList) {
					System.out.format("%10d%14s%14s%10d%10d\n", temp.getEmpid(), temp.getEname(), temp.getDepartment(),
							temp.getAge(), temp.getSalary());
				}
			}
		} catch (Exception e) {
			System.out.println("Something went wrong");
		}
	}

	public static void get_employees_above_age(int empage) {
		try (Session session = getSession()) {

			CriteriaBuilder builder = session.getCriteriaBuilder();

			CriteriaQuery<Employee> criteria = builder.createQuery(Employee.class);
			Root<Employee> employeeRoot = criteria.from(Employee.class);
			criteria.select(employeeRoot);

			criteria.where(builder.greaterThan(employeeRoot.get("age"), empage));
			List<Employee> employeeList = session.createQuery(criteria).getResultList();
			if (employeeList.isEmpty()) {
				System.out.println("No employee found");
			} else {
				System.out.println("Details of employees with age greater than " + empage + " are");
				System.out.format("%10s%14s%14s%10s%10s\n", "Empid", "Emp Name", "Department", "Age", "Salary");
				for (Employee temp : employeeList) {
					System.out.format("%10d%14s%14s%10d%10d\n", temp.getEmpid(), temp.getEname(), temp.getDepartment(),
							temp.getAge(), temp.getSalary());
				}
			}
		} catch (Exception e) {
			System.out.println("Something went wrong");
		}
	}

	public static void delete_a_employee(int empid) {
		try (Session session = getSession()) {
			Transaction tx = session.beginTransaction();
			int updatedEntities = session.createQuery("delete from Employee e where e.Empid=:i")
					.setParameter("i", empid).executeUpdate();

			System.out.println(updatedEntities + " Entities deleted succesfully");
			tx.commit();
		} catch (Exception e) {
			System.out.println(e);
			System.out.println("Something went wrong");
		}
	}

	public static void update_employee_name(int emp_id, String new_name) {
		try (Session session = getSession()) {
			Transaction tx = session.beginTransaction();
			int updatedEntities = session.createQuery("update Employee e set e.Ename=:n where e.Empid=:i")
					.setParameter("n", new_name).setParameter("i", emp_id).executeUpdate();

			System.out.println(updatedEntities + " Employee details updated");
			tx.commit();
		} catch (Exception e) {
			System.out.println(e);
			System.out.println("Something went wrong");
		}
	}
}