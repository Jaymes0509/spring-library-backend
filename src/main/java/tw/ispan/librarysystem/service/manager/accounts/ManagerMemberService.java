package tw.ispan.librarysystem.service.manager.accounts;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import tw.ispan.librarysystem.dto.manager.accounts.ManagerMemberDTO;
import tw.ispan.librarysystem.dto.manager.accounts.UpdateMemberDto;
import tw.ispan.librarysystem.dto.PageResponseDTO;
import tw.ispan.librarysystem.entity.member.Member;
import tw.ispan.librarysystem.repository.member.MemberRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ManagerMemberService {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<ManagerMemberDTO> getAllMembers() {
        List<Member> members = memberRepository.findAll();
        return members.stream().map(this::toDTO).collect(Collectors.toList());
    }

    public PageResponseDTO<ManagerMemberDTO> getMembersPage(Pageable pageable) {
        Page<Member> page = memberRepository.findAll(pageable);
        List<ManagerMemberDTO> dtoList = page.getContent().stream().map(this::toDTO).collect(Collectors.toList());
        return new PageResponseDTO<>(
            dtoList,
            page.getNumber(),
            page.getSize(),
            page.getTotalElements(),
            page.getTotalPages(),
            page.isLast(),
            page.isFirst()
        );
    }

    public ManagerMemberDTO updateMember(Long id, UpdateMemberDto dto) {
        Member member = memberRepository.findById(Math.toIntExact(id))
                .orElseThrow(() -> new RuntimeException("找不到會員"));
        member.setName(dto.getName());
        member.setGender(dto.getGender());
        member.setIdNumber(dto.getIdNumber());
        member.setBirthDate(dto.getBirthDate());
        member.setNationality(dto.getNationality());
        member.setEducation(dto.getEducation());
        member.setOccupation(dto.getOccupation());
        member.setAddressCounty(dto.getAddressCounty());
        member.setAddressTown(dto.getAddressTown());
        member.setAddressZip(dto.getAddressZip());
        member.setAddressDetail(dto.getAddressDetail());
        member.setEmail(dto.getEmail());
        member.setPhone(dto.getPhone());
        if (dto.getPassword() != null) {
            member.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        memberRepository.save(member);
        return toDTO(member);
    }

    public ManagerMemberDTO getMemberDTOById(Long id) {
        Member member = memberRepository.findById(Math.toIntExact(id))
                .orElseThrow(() -> new RuntimeException("找不到會員"));
        return toDTO(member);
    }

    public void deleteMember(Long id) {
        memberRepository.deleteById(Math.toIntExact(id));
    }

    private ManagerMemberDTO toDTO(Member member) {
        ManagerMemberDTO dto = new ManagerMemberDTO();
        BeanUtils.copyProperties(member, dto);
        return dto;
    }
}