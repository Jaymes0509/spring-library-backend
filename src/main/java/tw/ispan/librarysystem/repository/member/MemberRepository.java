package tw.ispan.librarysystem.repository.member;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tw.ispan.librarysystem.entity.member.Member;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    boolean existsByEmail(String email);

    boolean existsByIdNumber(String idNumber); // 用來檢查重複身分證

    // 根據 email 查詢會員
    Optional<Member> findByEmail(String email);

    // 檢查 email 和密碼是否匹配（用於登入驗證）
    @Query("SELECT m FROM Member m WHERE m.email = :email AND m.password = :password")
    Optional<Member> findByEmailAndPassword(@Param("email") String email, @Param("password") String password);
}
