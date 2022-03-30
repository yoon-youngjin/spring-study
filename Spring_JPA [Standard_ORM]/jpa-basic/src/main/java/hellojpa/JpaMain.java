package hellojpa;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.time.LocalDateTime;
import java.util.List;

public class JpaMain {
    public static void main(String[] args) {
        // emf는 웹서버가 올라올 때 db당 하나만 생성
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        // em은 고객의 요청이 올 때마다 생성
        // em는 절대 thread간에 공유해서는 안된다.
        // JPA의 모든 데이터 변경은 트랜잭션 안에서 실행
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        // 모든 데이터 변경하는 작업은 transaction안에서 작업해야함
        tx.begin();

        try {

            Address address = new Address("city", "street", "zipcode");

            Member member = new Member();
            member.setHomeAddress(address);
            member.setName("member1");
            member.getAddressHistory().add(new AddressEntity(new Address("city", "street", "zipcode")));
            member.getAddressHistory().add(new AddressEntity(new Address("city2", "street2", "zipcode2")));
            em.persist(member);

            em.flush();
            em.clear();

            System.out.println("========== START ==========");
            Member findMember = em.find(Member.class, member.getId());

//            // homeCity -> newCity
//            findMember.setHomeAddress(new Address("newCity", findMember.getHomeAddress().getStreet(), findMember.getHomeAddress().getZipcode()));
//
//            // 김치찌개 -> 된장찌개
//            findMember.getFavoriteFoods().remove("김치찌개");
//            findMember.getFavoriteFoods().add("된장찌개");


            // 주소 변경: city -> newCity


            // 해당 아래 두줄이 없는 경우
            // find하면 현재 영속 컨텍스트에 member와 team이 남아있으므로 DB에 접근하지 않고 가져옴
            // 아래 두줄을 통해 영속 컨텍스트로 강제 DB로 보내면 영속 컨텍스트가 비어지므로
            // find시 DB에 쿼리를 날려 가져옴
//            em.flush();
//            em.clear();

//            Member member1 = em.find(Member.class, member.getId());
//            Team findTeam = member1.getTeam();
//            System.out.println(findTeam.getName());
//
//            List<Member> members = findTeam.getMembers();
//            for (Member member2 : members) {
//                System.out.println("m= "+member2.getName());
//            }

            // 등록
//            Member member = new Member();
//            member.setName("kim");
//            em.persist(member);


            // 수정
            // Java객체에서 값만 바꿔도 jpa를 통해 객체를 가져오면(=em.find)
            // JPA가 관리하게 된다. => JPA가 값이 변경되었는지 트랜잭션을 커밋하는 시점에 검사함
            // 변경된 값이 있는 경우 update query를 날림
//            Member f_member = em.find(Member.class, 1L);
//            f_member.setName("update yoon");

            // 삭제
//            Member f_member = em.find(Member.class, 1L);
//            em.remove(f_member);

            // JPQL
//            List<Member> members = em.createQuery("select m from Member m where m.name=:name", Member.class)
//                    .setParameter("name", "yoon")
//                    .getResultList();
//
//            Iterator it = members.listIterator();
//            while (it.hasNext()) {
//                System.out.println(it.next());
//            }

            // 플러시
//            Member member = new Member(200L, "MEMBER");
//            em.persist(member);
//            // 아직 db에 반영 x
//
//            em.flush();
            // db에 강제 반영, flush를 호출하지 않았다면 tx.commit시점에
            // flush가 호출되어 db에 반영
            // flush가 호출된다고 1차캐시가 전부 지워지는게 아닌 쓰기 지연 SQL저장소에
            // 쌓여있던 쿼리문이 날라감
            System.out.println("===================");


            // 준영속
            // 영속 상태
//            Member member = em.find(Member.class, 200L);
//            member.setName("ZZZZZ");

            // 준영속 상태 - JPA에서 관리 X
            // => COMMIT을 해도 변경 X, UPDATE 쿼리X
//            em.detach(member);
            // entitymanager내의 영속 컨텍스트를 전부 지움
//            em.clear();
            tx.commit();
        } catch (Exception e) {
            tx.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }

        emf.close();


    }
}
