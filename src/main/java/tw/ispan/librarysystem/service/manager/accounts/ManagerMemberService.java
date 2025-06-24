package tw.ispan.librarysystem.service.manager.accounts;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import tw.ispan.librarysystem.dto.manager.accounts.ManagerMemberDTO;
import tw.ispan.librarysystem.entity.member.Member;
import tw.ispan.librarysystem.repository.member.MemberRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ManagerMemberService {

    @Autowired
    private MemberRepository memberRepository;

    public List<ManagerMemberDTO> getAllMembers() {
        List<Member> members = memberRepository.findAll();
        return members.stream().map(this::toDTO).collect(Collectors.toList());
    }

    public Page<ManagerMemberDTO> getMembersPage(Pageable pageable) {
        Page<Member> page = memberRepository.findAll(pageable);
        return page.map(this::toDTO);
    }

    private ManagerMemberDTO toDTO(Member member) {
        ManagerMemberDTO dto = new ManagerMemberDTO();
        BeanUtils.copyProperties(member, dto);
        return dto;
    }
}