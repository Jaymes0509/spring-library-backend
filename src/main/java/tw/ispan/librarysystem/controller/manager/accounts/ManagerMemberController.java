package tw.ispan.librarysystem.controller.manager.accounts;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tw.ispan.librarysystem.dto.manager.accounts.ManagerMemberDTO;
import tw.ispan.librarysystem.service.manager.accounts.ManagerMemberService;

import java.util.List;

@RestController
@RequestMapping("/api/manager/accounts")
public class ManagerMemberController {

    @Autowired
    private ManagerMemberService managerMemberService;

    @GetMapping("/all")
    public List<ManagerMemberDTO> getAllMembers() {
        return managerMemberService.getAllMembers();
    }
}