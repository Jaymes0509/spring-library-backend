package tw.ispan.librarysystem.controller.manager.accounts;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import tw.ispan.librarysystem.dto.manager.accounts.ManagerMemberDTO;
import tw.ispan.librarysystem.service.manager.accounts.ManagerMemberService;

@RestController
@RequestMapping("/api/manager/accounts")
public class ManagerMemberController {

    @Autowired
    private ManagerMemberService managerMemberService;

    @GetMapping("/all")
    public Page<ManagerMemberDTO> getMembersPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return managerMemberService.getMembersPage(PageRequest.of(page, size));
    }
}