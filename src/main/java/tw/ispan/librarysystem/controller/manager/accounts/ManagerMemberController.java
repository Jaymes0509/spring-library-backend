//package tw.ispan.librarysystem.controller.manager.accounts;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.web.bind.annotation.*;
//import tw.ispan.librarysystem.dto.manager.accounts.ManagerMemberDTO;
//import tw.ispan.librarysystem.dto.manager.accounts.UpdateMemberDto;
//import tw.ispan.librarysystem.service.manager.accounts.ManagerMemberService;
//import tw.ispan.librarysystem.entity.member.Member;
//import tw.ispan.librarysystem.repository.member.MemberRepository;
//import tw.ispan.librarysystem.dto.PageResponseDTO;
//
//@RestController
//@RequestMapping("/api/manager/accounts")
//
//@CrossOrigin(origins = "http://localhost:3000", allowedHeaders = "*", methods = { RequestMethod.GET, RequestMethod.POST,
//        RequestMethod.PUT, RequestMethod.PATCH, RequestMethod.DELETE, RequestMethod.OPTIONS })
//public class ManagerMemberController {
//
//    @Autowired
//    private ManagerMemberService managerMemberService;
//
//    @Autowired
//    private MemberRepository memberRepository;
//
//    @Autowired
//    private PasswordEncoder passwordEncoder;
//
//    @GetMapping("/all")
//    public PageResponseDTO<ManagerMemberDTO> getMembersPage(
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "20") int size) {
//        return managerMemberService.getMembersPage(PageRequest.of(page, size));
//    }
//
//    @GetMapping("/{id}")
//    public ManagerMemberDTO getMemberById(@PathVariable Long id) {
//        return managerMemberService.getMemberDTOById(id);
//    }
//
//    @PatchMapping("/{id}")
//    public ManagerMemberDTO updateMember(
//            @PathVariable Long id,
//            @RequestBody UpdateMemberDto dto) {
//        Member entity = memberRepository.findById(Math.toIntExact(id)).orElseThrow();
//        if (dto.getEmail() != null)
//            entity.setEmail(dto.getEmail());
//        if (dto.getPhone() != null)
//            entity.setPhone(dto.getPhone());
//        if (dto.getAddressCounty() != null)
//            entity.setAddressCounty(dto.getAddressCounty());
//        if (dto.getAddressTown() != null)
//            entity.setAddressTown(dto.getAddressTown());
//        if (dto.getAddressZip() != null)
//            entity.setAddressZip(dto.getAddressZip());
//        if (dto.getAddressDetail() != null)
//            entity.setAddressDetail(dto.getAddressDetail());
//        if (dto.getBirthDate() != null)
//            entity.setBirthDate(dto.getBirthDate());
//        if (dto.getEducation() != null)
//            entity.setEducation(dto.getEducation());
//        if (dto.getOccupation() != null)
//            entity.setOccupation(dto.getOccupation());
//        if (dto.getNationality() != null)
//            entity.setNationality(dto.getNationality());
//        if (dto.getGender() != null)
//            entity.setGender(dto.getGender());
//        if (dto.getIdNumber() != null)
//            entity.setIdNumber(dto.getIdNumber());
//        if (dto.getName() != null)
//            entity.setName(dto.getName());
//        if (dto.getPassword() != null)
//            entity.setPassword(passwordEncoder.encode(dto.getPassword()));
//        memberRepository.save(entity);
//        System.out.println("updateMember: " + entity);
//        return managerMemberService.getMemberDTOById(id);
//    }
//
//    @DeleteMapping("/{id}")
//    public void deleteMember(@PathVariable Long id) {
//        managerMemberService.deleteMember(id);
//    }
//}