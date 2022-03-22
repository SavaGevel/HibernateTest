import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import javax.persistence.Parameter;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Root;
import java.sql.ResultSet;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        StandardServiceRegistry registry = new StandardServiceRegistryBuilder().configure("hibernate.cfg.xml").build();
        Metadata metadata = new MetadataSources(registry).getMetadataBuilder().build();

        try(SessionFactory sessionFactory = metadata.getSessionFactoryBuilder().build();
                Session session = sessionFactory.openSession()) {

            Transaction transaction = session.beginTransaction();

            List<PurchaseList> purchases = session.createQuery("from PurchaseList").getResultList();

            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();

            CriteriaQuery<Student> studentIdQuery = criteriaBuilder.createQuery(Student.class);
            Root<Student> rootStudent = studentIdQuery.from(Student.class);

            CriteriaQuery<Course> courseIdQuery = criteriaBuilder.createQuery(Course.class);
            Root<Course> rootCourse = courseIdQuery.from(Course.class);

            for(PurchaseList purchase : purchases) {

                studentIdQuery.select(rootStudent).where(criteriaBuilder.equal(rootStudent.get("name"), purchase.getStudentName()));
                courseIdQuery.select(rootCourse).where(criteriaBuilder.equal(rootCourse.get("name"), purchase.getCourseName()));

                LinkedPurchaseList linkedPurchase = new LinkedPurchaseList();
                LinkedPurchaseListKey linkedPurchaseKey = new LinkedPurchaseListKey();

                linkedPurchase.setStudentId(session.createQuery(studentIdQuery).getSingleResult().getId());
                linkedPurchase.setCourseId(session.createQuery(courseIdQuery).getSingleResult().getId());

                linkedPurchaseKey.setStudentId(linkedPurchase.getStudentId());
                linkedPurchaseKey.setCourseId(linkedPurchase.getCourseId());

                linkedPurchase.setId(linkedPurchaseKey);

                session.save(linkedPurchase);

            }

            transaction.commit();

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

}
