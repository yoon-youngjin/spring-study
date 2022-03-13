package dev.yoon.sss.Account.controller;

import dev.yoon.sss.Account.domain.Account;
import dev.yoon.sss.Account.dto.AccountDto;
import dev.yoon.sss.Account.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("account/api")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    public AccountDto.Res signUp(@RequestBody @Valid final AccountDto.SignUpReq dto) {

        System.out.println(dto);
        return new AccountDto.Res(accountService.createAccount(dto));
    }

    @GetMapping(value = "/{id}")
    @ResponseStatus(value = HttpStatus.OK)
    public ResponseEntity<AccountDto.Res> getUser(
            @PathVariable final Long id
    ){
        Account account = accountService.findById(id);
        return ResponseEntity.ok(new AccountDto.Res(account));
    }

    @PutMapping(value = "/{id}")
    @ResponseStatus(value = HttpStatus.OK)
    public AccountDto.Res updateMyAccount(@PathVariable final long id, @RequestBody final AccountDto.MyAccountReq dto) {
        return new AccountDto.Res(accountService.updateMyAccount(id, dto));
    }
}
